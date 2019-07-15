/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.domain;

import java.util.List;

/**
 *
 * @author Implementist
 */
public class SummaryTask {

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

    public boolean isGroupOnHoliday() {
        return groupOnHoliday;
    }

    public void setGroupOnHoliday(boolean groupOnHoliday) {
        this.groupOnHoliday = groupOnHoliday;
    }

    public boolean isForBossesOnly() {
        return forBossesOnly;
    }

    public void setForBossesOnly(boolean forBossesOnly) {
        this.forBossesOnly = forBossesOnly;
    }
    
    public String[] getHolidayers() {
        return holidayers;
    }

    public void setHolidayers(String[] holidayers) {
        this.holidayers = holidayers;
    }

    public List<Integer> getGroups() {
        return groups;
    }

    public void setGroups(List<Integer> groups) {
        this.groups = groups;
    }

    public int[] getRestDays() {
        return restDays;
    }

    public void setRestDays(int[] restDays) {
        this.restDays = restDays;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String[] getBossesAddresses() {
        return bossesAddresses;
    }

    public void setBossesAddresses(String[] bossesAddresses) {
        this.bossesAddresses = bossesAddresses;
    }

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public Identity getMailSenderIdentity() {
        return mailSenderIdentity;
    }

    public void setMailSenderIdentity(Identity mailSenderIdentity) {
        this.mailSenderIdentity = mailSenderIdentity;
    }
}
