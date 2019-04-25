/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import com.implementist.nisljournalmanager.dao.MemberDAO;
import com.implementist.nisljournalmanager.domain.Mail;
import com.implementist.nisljournalmanager.domain.SummaryTask;
import java.io.File;
import java.util.List;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author Implementist
 */
public class SummaryTaskFactory extends TaskFactory {

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private SummaryFileService summaryFileService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private MailService mailService;

    private static ThreadLocal<SummaryTask> summaryTaskHolder;

    @SuppressWarnings("LeakingThisInConstructor")
    public SummaryTaskFactory(ServletContext context) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(context);
        AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
        summaryTaskHolder = new ThreadLocal<>();
    }

    @Override
    protected void build(Object summaryTask) {
        runnable = () -> {
            summaryTaskHolder.set((SummaryTask) summaryTask);
            String nameStringOfGroups = getNameStringOfGroups(summaryTaskHolder.get().getGroups());
            String dateString = timeService.getDateString();  //获取当前日期时间字符串
            mailService.read(summaryTaskHolder.get().getMailSenderIdentity());  //从邮箱读入日志，提交了日志的同学的Submitted位会被置位1
            summaryFileService.create(summaryTaskHolder.get().getGroups(), nameStringOfGroups);  //创建日报汇总PDF文件

            String[] toList, ccList;
            if (summaryTaskHolder.get().isOnlyForTeachers()) {
                toList = summaryTaskHolder.get().getTeachersAddresses();
                ccList = null;
            } else {
                toList = getToList(summaryTaskHolder.get().getGroups());
                ccList = summaryTaskHolder.get().getTeachersAddresses();
            }

            Mail mail = new Mail(
                    summaryTaskHolder.get().getMailSubject() + dateString,
                    summaryTaskHolder.get().getMailContent() + setTimeAsHtmlStyle(dateString),
                    toList,
                    ccList,
                    new String[]{System.getProperty("user.dir").split("\\\\")[0] + File.separator + "NISLJournal" + File.separator + "DailySummary-Group" + nameStringOfGroups + "-" + dateString + ".PDF"}
            );
            mailService.send(summaryTaskHolder.get().getMailSenderIdentity(), mail);
            memberDAO.updateSubmittedByGroups(summaryTaskHolder.get().getGroups(), true);
            summaryTaskHolder.remove();
        };
    }

    /**
     * 获取目标列表
     *
     * @param groups
     * @return 目标列表
     */
    public String[] getToList(List<Integer> groups) {
        List<String> toList = memberDAO.queryEmailAddressByGroups(groups);
        return toList.toArray(new String[toList.size()]);
    }

    /**
     * 获取组名
     *
     * @param groups 组号数组
     * @return 组名
     */
    private String getNameStringOfGroups(List<Integer> groups) {
        StringBuilder nameStringOfGroups = new StringBuilder();
        nameStringOfGroups.append(groups.get(0));
        for (int i = 1; i < groups.size(); i++) {
            nameStringOfGroups.append("&").append(groups.get(i));
        }
        return nameStringOfGroups.toString();
    }

    /**
     * 设置HTML格式的时间戳
     *
     * @param time 时间戳
     * @return HTML格式的时间戳
     */
    private String setTimeAsHtmlStyle(String time) {
        return time + "</div></div>";
    }
}
