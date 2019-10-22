package com.implementist.artanis.entity.taskdata;

import com.implementist.artanis.entity.Identity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Implementist
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InitializeTaskData extends BaskTaskData {

    /**
     * 任务起始时间
     */
    private String startTime;
    /**
     * 初始化后的日报内容
     */
    private String initialContent;
    /**
     * 需要被移动的邮件的源文件夹
     */
    private String sourceFolder;
    /**
     * 目标文件夹
     */
    private String targetFolder;
    /**
     * 邮箱账户身份信息
     */
    private Identity mailSenderIdentity;
}