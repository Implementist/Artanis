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
public class UrgeTaskData extends BaseTaskData {

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
}