/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Implementist
 */
public abstract class TaskFactory {

    protected Runnable runnable;

    /**
     * 任务线程构造函数
     *
     */
    protected abstract void buildTask();

    protected Runnable getRunnable() {
        return runnable;
    }

    /**
     * 获取传入的日期是本周的第几天：因为西方的每周是从周日开始的，因此需要做额外的映射处理
     *
     * @param date 传入的日期
     * @return 传入的日期是本周的第几天
     */
    protected int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        //映射
        if (dayOfWeek != 1) {
            return dayOfWeek - 1;
        } else {
            return 7;
        }
    }

    /**
     * 判断今天是不是休息日
     *
     * @param today 今天在一周中的第几天
     * @param restDays 休息日在一周中的哪几天
     * @return 判断结果
     */
    protected boolean isRestDay(int today, int[] restDays) {
        for (int restDay : restDays) {
            if (today == restDay) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取日期字符串
     *
     * @return 日期字符串
     */
    protected String getDateString() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }

    /**
     * 获取日期时间字符串
     *
     * @return 日期时间字符串
     */
    protected String getDateTimeString() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
