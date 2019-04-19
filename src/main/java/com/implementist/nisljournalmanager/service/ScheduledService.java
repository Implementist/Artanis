/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import com.implementist.nisljournalmanager.dao.MemberDAO;
import com.implementist.nisljournalmanager.domain.InitializeTask;
import com.implementist.nisljournalmanager.domain.SummaryTask;
import com.implementist.nisljournalmanager.domain.SystemConfig;
import com.implementist.nisljournalmanager.domain.UrgeTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author Implementist
 */
public class ScheduledService extends HttpServlet {

    private final Logger logger = Logger.getLogger(ScheduledService.class);

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private TimeService timeService;

    private static final long MILLIS_OF_ONE_DAY = 24 * 60 * 60 * 1000;

    private List<UrgeTask> urgeTasks;
    private List<SummaryTask> summaryTasks;
    private InitializeTask initializeTask;
    private ScheduledExecutorService periodicExecutor;
    private ScheduledExecutorService oneTimeExecutor;

    /**
     * 从配置文件中加载并启动当天各项任务的任务
     */
    private final Runnable LOAD_TASK = () -> {
        //刷新配置文件
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{
            "file:C:/JournalManagerConfig/journalConfig.xml",
            "file:C:/JournalManagerConfig/systemConfig.xml"
        });

        SystemConfig systemConfig = getSystemConfig(ctx);
        if (systemConfig.isHolidayModeOn() && timeService.isHolidayToday(systemConfig.getFrom(), systemConfig.getTo())) {
            return;
        }

        //获取ServletContext
        ServletContext context = getServletContext();

        //从配置文件中读取各种任务的定义
        summaryTasks = getSummaryTasks(ctx);
        urgeTasks = getUrgeTasks(ctx, summaryTasks);
        initializeTask = getInitializeTask(ctx);

        //为休假中的同学设置日报内容
        setJournalContentForHolidayers(summaryTasks);

        //设置所有督促提交邮件
        if (urgeTasks != null) {
            UrgeTaskFactory urgeTaskFactory = new UrgeTaskFactory(context);
            for (UrgeTask urgeTask : urgeTasks) {
                urgeTaskFactory.build(urgeTask);
                ExecuteOnce(urgeTask.getStartTime(), urgeTaskFactory.getRunnable());
            }
        }

        //设置所有的日报汇总任务
        if (summaryTasks != null) {
            SummaryTaskFactory summaryTaskFactory = new SummaryTaskFactory(context);
            for (SummaryTask summaryTask : summaryTasks) {
                summaryTaskFactory.build(summaryTask);
                ExecuteOnce(summaryTask.getStartTime(), summaryTaskFactory.getRunnable());
            }
        }

        //设置初始化邮箱和数据库任务
        InitializeTaskFactory initializeTaskFactory = new InitializeTaskFactory(context);
        initializeTaskFactory.build(initializeTask);
        ExecuteOnce(initializeTask.getStartTime(), initializeTaskFactory.getRunnable());

        //设置捕获器捕获未处理的异常，输出异常信息
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("Internal Error Occured!", e);
        });
    };

    /**
     * Servlet初始化函数
     */
    @Override
    public void init() {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
        periodicExecutor = Executors.newSingleThreadScheduledExecutor();
        oneTimeExecutor = Executors.newScheduledThreadPool(5);
        ExecuteByCycle("21:45:00", MILLIS_OF_ONE_DAY, LOAD_TASK);
    }

    /**
     * Servlet销毁函数，销毁所有线程池
     */
    @Override
    public void destroy() {
        periodicExecutor.shutdownNow();
        oneTimeExecutor.shutdownNow();
    }

    /**
     * 周期性定时执行
     *
     * @param time 初次执行时间
     * @param cycle 周期
     * @param task 任务
     */
    private void ExecuteByCycle(String time, long cycle, Runnable task) {
        long initDelay = timeService.getTimeMillis(time) - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : cycle + initDelay;

        //执行器开始执行任务
        periodicExecutor.scheduleAtFixedRate(task, initDelay, cycle, TimeUnit.MILLISECONDS);
    }

    /**
     * 一次性定时执行
     *
     * @param time 执行时间
     * @param task 任务
     */
    private void ExecuteOnce(String time, Runnable task) {
        long delay = timeService.getTimeMillis(time) - System.currentTimeMillis();

        //执行器开始执行任务
        oneTimeExecutor.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 读取配置文件，获取系统配置
     *
     * @param ctx 配置信息
     * @return 系统配置
     */
    private SystemConfig getSystemConfig(ApplicationContext ctx) {
        return (SystemConfig) ctx.getBean("systemConfig");
    }

    /**
     * 读取配置文件，获取所有督促任务
     *
     * @param ctx 配置信息
     * @param summaryTasks 日报任务列表
     * @return 督促任务列表
     */
    private List<UrgeTask> getUrgeTasks(ApplicationContext ctx, List<SummaryTask> summaryTasks) {
        if (summaryTasks != null && summaryTasks.size() > 0) {
            List<UrgeTask> tasks = new ArrayList<>();
            String[] taskNames = ctx.getBeanNamesForType(UrgeTask.class);

            //获取日报汇总任务中当天需要发日报的小组号
            List<Integer> groups = new ArrayList<>();
            summaryTasks.forEach((summaryTask) -> {
                for (int group : summaryTask.getGroups()) {
                    groups.add(group);
                }
            });

            for (String taskName : taskNames) {
                UrgeTask urgeTask = (UrgeTask) ctx.getBean(taskName);
                urgeTask.setGroups(groups);
                tasks.add(urgeTask);
            }
            return tasks;
        }
        return null;
    }

    /**
     * 读取配置文件，获取所有小组的日报任务（在休假的小组会被排除）
     *
     * @param ctx 配置信息
     * @return 日报任务列表
     */
    private List<SummaryTask> getSummaryTasks(ApplicationContext ctx) {
        List<SummaryTask> tasks = new ArrayList<>();
        String[] taskNames = ctx.getBeanNamesForType(SummaryTask.class);
        for (String taskName : taskNames) {
            SummaryTask task = (SummaryTask) ctx.getBean(taskName);
            if (!task.isGroupOnHoliday() && !timeService.isRestDayToday(task.getRestDays())) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * 读取配置文件，获取初始化任务
     *
     * @param ctx 配置信息
     * @return 初始化任务
     */
    private InitializeTask getInitializeTask(ApplicationContext ctx) {
        return (InitializeTask) ctx.getBean("initializeTask");
    }

    /**
     * 为正在请假或休假中的同学设置日报内容
     *
     * @param summaryTasks 日报任务列表
     */
    private void setJournalContentForHolidayers(List<SummaryTask> summaryTasks) {
        if (summaryTasks != null) {
            List<String> namesOfHolidayers = new ArrayList<>();
            summaryTasks.forEach((task) -> {
                if (task.getHolidayers() != null) {
                    namesOfHolidayers.addAll(Arrays.asList(task.getHolidayers()));
                }
            });

            String journalContent = "该同学正在请假或休假中。";
            namesOfHolidayers.forEach((holidayer) -> {
                memberDAO.updateContentByName(journalContent, holidayer);
            });
        }
    }
}
