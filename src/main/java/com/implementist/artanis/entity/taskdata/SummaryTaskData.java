package com.implementist.artanis.entity.taskdata;

import com.implementist.artanis.entity.Identity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Implementist
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SummaryTaskData extends BaskTaskData {

    /**
     * 小组是否在休假
     */
    private boolean groupOnHoliday;
    /**
     * 该汇总任务是否只发送给老师
     */
    private boolean forBossesOnly;
    /**
     * 休假或请假中的人（成员名）数组
     */
    private String[] holidayers;
    /**
     * 当前任务包含的小组
     */
    private List<Integer> groups;
    /**
     * 每周的休息日
     */
    private int[] restDays;
    /**
     * 任务起始时间
     */
    private String startTime;
    /**
     * 邮件主题
     */
    private String mailSubject;
    /**
     * 抄送人邮箱数组
     */
    private String[] bossesAddresses;
    /**
     * 邮件内容
     */
    private String mailContent;
    /**
     * 邮件发送者身份
     */
    private Identity mailSenderIdentity;
}