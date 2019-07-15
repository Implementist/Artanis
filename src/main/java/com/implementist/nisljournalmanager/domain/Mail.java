/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.domain;

/**
 *
 * @author Implementist
 */
public class Mail {

    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 收件人数组
     */
    private String[] to;
    /**
     * 抄送人数组
     */
    private String[] cc;
    /**
     * 附件文件数组
     */
    private String[] files;

    /**
     * 三参构造函数
     *
     * @param subject 注意
     * @param content 内容
     * @param to 收件人
     */
    public Mail(String subject, String content, String[] to) {
        this.subject = subject;
        this.content = content;
        this.to = to;
    }

    /**
     * 全参构造函数
     *
     * @param subject 主题
     * @param content 内容
     * @param to 收件人
     * @param cc 抄送
     * @param files 附件
     */
    public Mail(String subject, String content, String[] to, String[] cc, String[] files) {
        this.subject = subject;
        this.content = content;
        this.to = to;
        this.cc = cc;
        this.files = files;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String[] getCc() {
        return cc;
    }

    public void setCc(String[] cc) {
        this.cc = cc;
    }

    public String[] getFiles() {
        return files;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }
}
