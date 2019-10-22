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
@Table(name = "`group`")
public class Group implements Serializable {

    /**
     * Id
     */
    @Id
    @Column(name = "id", columnDefinition = "int(1) unsigned DEFAULT '0'")
    private int id;
    /**
     * 名称
     */
    @Column(name = "name", columnDefinition = "varchar(128) NOT NULL DEFAULT ''")
    private String name;
}