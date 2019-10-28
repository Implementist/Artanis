package com.implementist.artanis;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.implementist.artanis.runnable.MainTask;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @author Implementist
 */
@SpringBootApplication
@EnableEncryptableProperties
public class Application extends SpringBootServletInitializer {
    /**
     * 系统设定的配置刷新时间
     */
    private static final String FIXED_TIME = "21:45:00";

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    public static void main(String[] args) {
        //启动工程
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);

        //创建线程工厂，定制线程名称
        ThreadFactory mainThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("pool-main-thread-%d")
                .build();

        //调用全参构造函数创建定时线程池
        ExecutorService periodicExecutor = new ScheduledThreadPoolExecutor(1,
                mainThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());

        //创建并执行主任务
        MainTask mainTask = new MainTask(applicationContext);
        executeByCycle(periodicExecutor, mainTask);
    }

    /**
     * 周期性定时执行
     *
     * @param task 任务
     */
    private static void executeByCycle(ExecutorService periodicExecutor, Runnable task) {
        long millisOfOneDay = 24 * 60 * 60 * 1000;
        long initDelay = getInitDelay();
        initDelay = initDelay > 0 ? initDelay : millisOfOneDay + initDelay;

        //执行器开始执行任务
        ((ScheduledThreadPoolExecutor) periodicExecutor).scheduleAtFixedRate(task, initDelay, millisOfOneDay,
                TimeUnit.MILLISECONDS);
    }

    /**
     * 获取距离指定时间所剩的毫秒数
     *
     * @return 距离指定时间所剩的毫秒数
     */
    private static long getInitDelay() {
        long fixedTimeMillis;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + FIXED_TIME);
            fixedTimeMillis = curDate.getTime();
        } catch (ParseException e) {
            fixedTimeMillis = 0;
        }
        return fixedTimeMillis - System.currentTimeMillis();
    }
}
