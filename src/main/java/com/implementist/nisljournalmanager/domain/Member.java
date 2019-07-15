/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Implementist
 */
@Entity
@Table(name = "member")
public class Member implements Serializable {

    /**
     * Id
     */
    @Id
    @Column(name = "Id")
    private int id;
    /**
     * 姓名
     */
    @Column(name = "Name")
    private String name;
    /**
     * 所属小组Id
     */
    @Column(name = "GroupId")
    private int groupId;
    /**
     * 是否已提交日报
     */
    @Column(name = "Submitted")
    private Boolean submitted;
    /**
     * 日报内容
     */
    @Column(name = "Content")
    private String content;
    /**
     * 邮箱地址
     */
    @Column(name = "EmailAddress")
    private String emailAddress;
    /**
     * 身份
     */
    @Column(name = "Identity")
    private String identity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Boolean getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Boolean submitted) {
        this.submitted = submitted;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

}
