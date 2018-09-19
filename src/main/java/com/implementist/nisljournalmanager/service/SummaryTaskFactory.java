/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import com.implementist.nisljournalmanager.dao.MemberDAO;
import com.implementist.nisljournalmanager.domain.Mail;
import com.implementist.nisljournalmanager.domain.Member;
import com.implementist.nisljournalmanager.domain.SummaryTask;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
    private MailService mailService;

    @Autowired
    private SummarizeFileService summarizeFileService;

    @Autowired
    private MemberDAO memberDAO;

    private SummaryTask summaryTask;

    public SummaryTaskFactory(ServletContext context) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(context);
        AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
    }

    public void setSummaryTask(SummaryTask summaryTask) {
        this.summaryTask = summaryTask;
    }

    @Override
    protected void buildTask() {
        runnable = () -> {
            String nameStringOfGroups = getNameStringOfGroups(summaryTask.getGroups());
            int dayOfWeek = getDayOfWeek(new Date());  //获取当前是周几，为避免变量名冲突，故使用匿名方式
            if (!isRestDay(dayOfWeek, summaryTask.getRestDays())) {
                String dateString = getDateString();  //获取当前日期时间字符串
                mailService.read(summaryTask.getMailSenderIdentity());  //从邮箱读入日志，提交了日志的同学的Submitted位会被置位1
                summarizeFileService.create(summaryTask.getGroups(), nameStringOfGroups);  //创建日报汇总PDF文件
                String[] toList = getToList(summaryTask.getGroups());  //获取to的地址数组
                String[] ccList = summaryTask.getCcAddresses();  //获取cc的地址数组

                Mail mail = new Mail(
                        summaryTask.getMailSubject() + dateString,
                        summaryTask.getMailContent() + setTimeToHtmlStyle(dateString),
                        toList,
                        ccList,
                        new String[]{System.getProperty("user.dir").split("\\\\")[0] + File.separator + "NISLJournal" + File.separator + "DailySummary-Group" + nameStringOfGroups + "-" + dateString + ".PDF"}
                );
                mailService.send(summaryTask.getMailSenderIdentity(), mail);
            }
        };
    }

    /**
     * 获取目标列表
     *
     * @param groups
     * @return 目标列表
     */
    public String[] getToList(int[] groups) {
        //获取to的地址数组
        ArrayList<Member> students = new ArrayList<>();
        for (int i = 0; i < groups.length; i++) {
            ArrayList<Member> groupMembers = memberDAO.queryByGroup(groups[i]);
            students.addAll(groupMembers);
        }
        return mailService.getAddressArray(students);
    }

    /**
     * 获取组名
     *
     * @param groupIds 组号数组
     * @return 组名
     */
    private String getNameStringOfGroups(int[] groupIds) {
        StringBuilder nameStringOfGroups = new StringBuilder();
        if (groupIds.length == 1) {
            nameStringOfGroups.append(groupIds[0]);
        } else {
            nameStringOfGroups.append(groupIds[0]);
            for (int i = 1; i < groupIds.length; i++) {
                nameStringOfGroups.append("&").append(groupIds[i]);
            }
        }
        return nameStringOfGroups.toString();
    }

    /**
     * 设置HTML格式的时间戳
     *
     * @param time 时间戳
     * @return HTML格式的时间戳
     */
    private String setTimeToHtmlStyle(String time) {
        return time + "</div></div>";
    }
}
