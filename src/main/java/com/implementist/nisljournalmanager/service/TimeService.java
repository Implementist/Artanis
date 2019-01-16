/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author Implementist
 */
@Service
public class TimeService {

    private final Logger logger = Logger.getLogger(TimeService.class);

    /**
     * 获取指定时间对应的毫秒数
     *
     * @param time "HH:mm:ss"格式的指定时间
     * @return 指定时间对应的毫秒数
     */
    public long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            logger.error("Parse Exception!", e);
            return 0;
        }
    }

    /**
     * 获取传入的日期是本周的第几天：因为西方的每周是从周日开始的，因此需要做额外的映射处理
     *
     * @param date 传入的日期
     * @return 传入的日期是本周的第几天
     */
    private int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        //映射表，将每周的第一天转换为周一
        int[] map = {7, 1, 2, 3, 4, 5, 6};
        return map[dayOfWeek - 1];
    }

    /**
     * 判断今天是不是休息日
     *
     * @param restDays 休息日在一周中的哪几天
     * @return 判断结果
     */
    public boolean isRestDayToday(int[] restDays) {
        int today = getDayOfWeek(new Date());
        for (int restDay : restDays) {
            if (today == restDay) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断今天是不是节假日
     *
     * @param from 节假日开始日期的字符串
     * @param to 节假日截止日期的字符串
     * @return 判断结果
     */
    public boolean isHolidayToday(String from, String to) {
        LocalDate fromLocalDate = LocalDate.parse(from);
        LocalDate toLocalDate = LocalDate.parse(to);
        LocalDate now = LocalDate.now();
        return now.isAfter(fromLocalDate.minusDays(1)) && now.isBefore(toLocalDate.plusDays(1));
    }

    /**
     * 获取当前的日期字符串
     *
     * @return 当前的日期字符串
     */
    public String getDateString() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }

    /**
     * 获取当前的日期时间字符串
     *
     * @return 当前的日期时间字符串
     */
    public String getDateTimeString() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
