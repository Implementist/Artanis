package com.implementist.artanis;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.implementist.artanis.runnable.MainTask;
import com.implementist.artanis.service.TimeService;
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

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);

        ThreadFactory mainThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("pool-main-thread-%d")
                .build();

        ExecutorService periodicExecutor = new ScheduledThreadPoolExecutor(1,
                mainThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());

        MainTask mainTask = new MainTask(applicationContext);
        executeByCycle(periodicExecutor, "21:45:00", TimeService.MILLIS_OF_ONE_DAY, mainTask);
    }

    /**
     * 周期性定时执行
     *
     * @param time  初次执行时间
     * @param cycle 周期
     * @param task  任务
     */
    private static void executeByCycle(ExecutorService periodicExecutor, String time, long cycle, Runnable task) {
        long initDelay = getTimeMillis(time) - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : cycle + initDelay;

        //执行器开始执行任务
        ((ScheduledThreadPoolExecutor) periodicExecutor).scheduleAtFixedRate(task, initDelay, cycle, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取指定时间对应的毫秒数
     *
     * @param time "HH:mm:ss"格式的指定时间
     * @return 指定时间对应的毫秒数
     */
    private static long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }
}
