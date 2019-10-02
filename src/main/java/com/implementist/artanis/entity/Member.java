package com.implementist.artanis.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Implementist
 */
@Data
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
}