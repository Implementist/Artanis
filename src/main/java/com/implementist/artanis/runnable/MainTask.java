package com.implementist.artanis.runnable;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.implementist.artanis.Application;
import com.implementist.artanis.entity.SystemConfig;
import com.implementist.artanis.entity.taskdata.InitializeTaskData;
import com.implementist.artanis.entity.taskdata.SummaryTaskData;
import com.implementist.artanis.entity.taskdata.UrgeTaskData;
import com.implementist.artanis.repository.MemberRepository;
import com.implementist.artanis.service.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import javax.transaction.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Implementist
 */
@Transactional(rollbackOn = {Exception.class})
public class MainTask implements Runnable {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TimeService timeService;

    private ExecutorService oneTimeExecutor;
    private ApplicationContext applicationContext;

    public MainTask(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        factory.autowireBean(this);

        ThreadFactory taskThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("pool-task-thread-%d")
                .build();

        oneTimeExecutor = new ScheduledThreadPoolExecutor(5,
                taskThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    @Override
    public void run() {
        ApplicationHome home = new ApplicationHome(Application.class);
        String rootDirectory = home.getSource().getParentFile().toString();
        String journalConfigFileDirectory = rootDirectory + File.separator + "journalConfig.xml";
        String systemConfigFileDirectory = rootDirectory + File.separator + "systemConfig.xml";

        //刷新配置文件
        ApplicationContext ctx = new FileSystemXmlApplicationContext(
                journalConfigFileDirectory,
                systemConfigFileDirectory
        );

        SystemConfig systemConfig = getSystemConfig(ctx);
        if (systemConfig.isHolidayModeOn() && timeService.isHolidayToday(systemConfig.getHolidayFrom(), systemConfig.getHolidayTo())) {
            return;
        }

        //从配置文件中读取各种任务的定义
        List<SummaryTaskData> summaryTaskDataList = getSummaryTasks(ctx, systemConfig);
        List<UrgeTaskData> urgeTaskDataList = getUrgeTasks(ctx, summaryTaskDataList);
        InitializeTaskData initializeTaskData = getInitializeTask(ctx);

        //为休假中的同学设置日报内容
        setJournalContentForHolidayers(summaryTaskDataList);

        //设置所有督促提交邮件
        if (urgeTaskDataList != null) {
            for (UrgeTaskData taskDataUnit : urgeTaskDataList) {
                BaseTask task = Tasks.newUrgeTask(applicationContext, taskDataUnit);
                executeOnce(taskDataUnit.getStartTime(), task);
            }
        }

        //设置所有的日报汇总任务
        for (SummaryTaskData taskDataUnit : summaryTaskDataList) {
            BaseTask task = Tasks.newSummaryTask(applicationContext, taskDataUnit);
            executeOnce(taskDataUnit.getStartTime(), task);
        }

        if (summaryTaskDataList.size() > 0) {
            //设置初始化邮箱和数据库任务
            BaseTask task = Tasks.newInitializeTask(applicationContext, initializeTaskData);
            executeOnce(initializeTaskData.getStartTime(), task);
        }
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
     * @param ctx              配置信息
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
            namesOfHolidayers.forEach((holidayer) -> memberRepository.updateContentByName(journalContent, holidayer));
        }
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
}
