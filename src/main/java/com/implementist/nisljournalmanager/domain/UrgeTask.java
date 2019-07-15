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
public class UrgeTask {

    /**
     * 当前任务包含的小组
     */
    private List<Integer> groups;
    /**
     * 任务起始时间
     */
    private String startTime;
    /**
     * 邮件主题
     */
    private String mailSubject;
    /**
     * 邮件内容
     */
    private String mailContent;
    /**
     * 邮件发送者身份
     */
    private Identity mailSenderIdentity;

    public List<Integer> getGroups() {
        return groups;
    }

    public void setGroups(List<Integer> groups) {
        this.groups = groups;
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
