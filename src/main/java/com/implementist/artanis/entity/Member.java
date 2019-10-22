package com.implementist.artanis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Implementist
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member", uniqueConstraints = @UniqueConstraint(name = "uniq_email_address", columnNames = "email_address"),
        indexes = {@Index(name = "idx_name", columnList = "name")})
@DynamicInsert
@DynamicUpdate
public class Member implements Serializable {

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int(2) unsigned NOT NULL AUTO_INCREMENT")
    private int id;
    /**
     * 姓名
     */
    @Column(name = "name", columnDefinition = "varchar(64) DEFAULT ''")
    private String name;
    /**
     * 所属小组Id
     */
    @Column(name = "group_id", columnDefinition = "int(1) unsigned DEFAULT '0'")
    private int groupId;
    /**
     * 是否已提交日报
     */
    @Column(name = "submitted", columnDefinition = "tinyint(1) unsigned DEFAULT '0'")
    private Boolean submitted;
    /**
     * 日报内容
     */
    @Column(name = "journal_content", columnDefinition = "text")
    private String content;
    /**
     * 邮箱地址
     */
    @Column(name = "email_address", columnDefinition = "varchar(128) NOT NULL DEFAULT ''")
    private String emailAddress;
    /**
     * 身份
     */
    @Column(name = "identity", columnDefinition = "varchar(16) NOT NULL DEFAULT ''")
    private String identity;
}