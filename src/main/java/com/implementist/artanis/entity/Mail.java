package com.implementist.artanis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Implementist
 */
@Data
@AllArgsConstructor
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
     * @param to      收件人
     */
    public Mail(String subject, String content, String[] to) {
        this.subject = subject;
        this.content = content;
        this.to = to;
    }
}