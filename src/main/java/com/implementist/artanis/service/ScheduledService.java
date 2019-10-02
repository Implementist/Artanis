package com.implementist.artanis.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.implementist.artanis.dao.MemberDAO;
import com.implementist.artanis.entity.*;
import com.implementist.artanis.entity.task.BaseTask;
import com.implementist.artanis.entity.task.Tasks;
import com.implementist.artanis.entity.taskdata.InitializeTaskData;
import com.implementist.artanis.entity.taskdata.SummaryTaskData;
import com.implementist.artanis.entity.taskdata.UrgeTaskData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Implementist
 */
public class ScheduledService extends HttpServlet {

    private final Logger logger = Logger.getLogger(ScheduledService.class);

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private TimeService timeService;

    private static final long MILLIS_OF_ONE_DAY = 24 * 60 * 60 * 1000;

    private List<UrgeTaskData> urgeTaskDatas;
    private List<SummaryTaskData> summaryTaskDatas;
    private InitializeTaskData initializeTaskData;
    private ExecutorService periodicExecutor;
    private ExecutorService oneTimeExecutor;

    /**
     * 从配置文件中加载并启动当天各项任务的任务
     */
    private final Runnable LOAD_TASK = () -> {
        //刷新配置文件
        ApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:journalConfig.xml",
                "classpath:systemConfig.xml");

        SystemConfig systemConfig = getSystemConfig(ctx);
        if (systemConfig.isHolidayModeOn() && timeService.isHolidayToday(systemConfig.getHolidayFrom(), systemConfig.getHolidayTo())) {
            return;
        }

        //获取ServletContext
        ServletContext context = getServletContext();

        //从配置文件中读取各种任务的定义
        summaryTaskDatas = getSummaryTasks(ctx, systemConfig);
        urgeTaskDatas = getUrgeTasks(ctx, summaryTaskDatas);
        initializeTaskData = getInitializeTask(ctx);

        //为休假中的同学设置日报内容
        setJournalContentForHolidayers(summaryTaskDatas);

        //设置所有督促提交邮件
        if (urgeTaskDatas != null) {
            for (UrgeTaskData taskData : urgeTaskDatas) {
                BaseTask task = Tasks.newUrgeTask(context, taskData);
                executeOnce(taskData.getStartTime(), task);
            }
        }

        //设置所有的日报汇总任务
        if (summaryTaskDatas != null) {
            for (SummaryTaskData taskData : summaryTaskDatas) {
                BaseTask task = Tasks.newSummaryTask(context, taskData);
                executeOnce(taskData.getStartTime(), task);
            }
        }

        if (summaryTaskDatas != null && summaryTaskDatas.size() > 0) {
            //设置初始化邮箱和数据库任务
            BaseTask task = Tasks.newInitializeTask(context, initializeTaskData);
            executeOnce(initializeTaskData.getStartTime(), task);
        }

        //设置捕获器捕获未处理的异常，输出异常信息
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> logger.error("Internal Error Occured!", e));
    };

    /**
     * Servlet初始化函数
     */
    @Override
    public void init() {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
        factory.autowireBean(this);

        ThreadFactory mainThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("pool-main-thread-%d")
                .build();

        ThreadFactory taskThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("pool-task-thread-%d")
                .build();

        periodicExecutor = new ScheduledThreadPoolExecutor(1,
                mainThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
        oneTimeExecutor = new ScheduledThreadPoolExecutor(5,
                taskThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
        executeByCycle("21:45:00", MILLIS_OF_ONE_DAY, LOAD_TASK);
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
     * @param time  初次执行时间
     * @param cycle 周期
     * @param task  任务
     */
    private void executeByCycle(String time, long cycle, Runnable task) {
        long initDelay = timeService.getTimeMillis(time) - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : cycle + initDelay;

        //执行器开始执行任务
        ((ScheduledThreadPoolExecutor) periodicExecutor).scheduleAtFixedRate(task, initDelay, cycle, TimeUnit.MILLISECONDS);
    }

    /**
     * 一次性定时执行
     *
     * @param time 执行时间
     * @param task 任务
     */
    private void executeOnce(String time, Runnable task) {
        long delay = timeService.getTimeMillis(time) - System.currentTimeMillis();

        //执行器开始执行任务
        ((ScheduledThreadPoolExecutor) oneTimeExecutor).schedule(task, delay, TimeUnit.MILLISECONDS);
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
     * @param ctx          配置信息
     * @param summaryTaskDatas 日报任务列表
     * @return 督促任务列表
     */
    private List<UrgeTaskData> getUrgeTasks(ApplicationContext ctx, List<SummaryTaskData> summaryTaskDatas) {
        if (summaryTaskDatas != null && summaryTaskDatas.size() > 0) {
            List<UrgeTaskData> tasks = new ArrayList<>();
            String[] taskNames = ctx.getBeanNamesForType(UrgeTaskData.class);

            //获取日报汇总任务中当天需要发日报的小组号
            List<Integer> groups = new ArrayList<>();
            summaryTaskDatas.forEach((summaryTaskData) -> groups.addAll(summaryTaskData.getGroups()));

            for (String taskName : taskNames) {
                UrgeTaskData urgeTaskData = (UrgeTaskData) ctx.getBean(taskName);
                urgeTaskData.setGroups(groups);
                tasks.add(urgeTaskData);
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
    private List<SummaryTaskData> getSummaryTasks(ApplicationContext ctx, SystemConfig config) {
        List<SummaryTaskData> tasks = new ArrayList<>();
        String[] taskNames = ctx.getBeanNamesForType(SummaryTaskData.class);
        boolean isWorkdayToday = config.isWorkdayModeOn()
                && timeService.isWorkdayToday(config.getWorkdayFrom(), config.getWorkdayTo());
        for (String taskName : taskNames) {
            SummaryTaskData task = (SummaryTaskData) ctx.getBean(taskName);
            boolean isRestDayToday = timeService.isRestDayToday(task.getRestDays());
            if (!task.isGroupOnHoliday() && (!isRestDayToday || isWorkdayToday)) {
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
    private InitializeTaskData getInitializeTask(ApplicationContext ctx) {
        return (InitializeTaskData) ctx.getBean("initializeTask");
    }

    /**
     * 为正在请假或休假中的同学设置日报内容
     *
     * @param summaryTaskDatas 日报任务列表
     */
    private void setJournalContentForHolidayers(List<SummaryTaskData> summaryTaskDatas) {
        if (summaryTaskDatas != null) {
            List<String> namesOfHolidayers = new ArrayList<>();
            summaryTaskDatas.forEach((task) -> {
                if (task.getHolidayers() != null) {
                    namesOfHolidayers.addAll(Arrays.asList(task.getHolidayers()));
                }
            });

            String journalContent = "该同学正在请假或休假中。";
            namesOfHolidayers.forEach((holidayer) -> memberDAO.updateContentByName(journalContent, holidayer));
        }
    }
}