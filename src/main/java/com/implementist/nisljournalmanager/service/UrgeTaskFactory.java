/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import com.implementist.nisljournalmanager.dao.MemberDAO;
import com.implementist.nisljournalmanager.domain.Mail;
import com.implementist.nisljournalmanager.domain.Member;
import com.implementist.nisljournalmanager.domain.UrgeTask;
import java.util.ArrayList;
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
public class UrgeTaskFactory extends TaskFactory {

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private TimeService timeService;

    @Autowired
    private MailService mailService;

    private static ThreadLocal<UrgeTask> urgeTaskHolder;

    @SuppressWarnings("LeakingThisInConstructor")
    public UrgeTaskFactory(ServletContext context) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(context);
        AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
        urgeTaskHolder = new ThreadLocal<>();
    }

    @Override
    public void build(Object urgeTask) {
        runnable = () -> {
            urgeTaskHolder.set((UrgeTask) urgeTask);
            mailService.read(urgeTaskHolder.get().getMailSenderIdentity());  //从邮箱读入日志，提交了日志的同学的Submitted位会被置位1

            String[] addresses = getAddressesOfUnsubmited(urgeTaskHolder.get().getGroups());  //获取未提交日志的学生的地址数组
            if (addresses.length > 0) {  //向每一位Submitted位为0的学生发送督促邮件
                Mail mail = new Mail(
                        urgeTaskHolder.get().getMailSubject(),
                        urgeTaskHolder.get().getMailContent() + setTimeAsHtmlStyle(timeService.getDateTimeString()),
                        addresses
                );
                mailService.send(urgeTaskHolder.get().getMailSenderIdentity(), mail);
            }
            urgeTaskHolder.remove();
        };
    }

    /**
     * 获取每一位未提交日志同学的地址
     *
     * @return 未提交日志同学的地址数组
     */
    private String[] getAddressesOfUnsubmited(List<Integer> groups) {
        //从数据库读出学生信息
        ArrayList<Member> students = new ArrayList<>();
        groups.forEach((group) -> {
            students.addAll(memberDAO.queryByGroup(group));
        });

        ArrayList<String> addressOfUnsubmited = new ArrayList<>();

        //获取每一位未提交日志同学的地址
        students.stream().filter((student) -> (!student.getSubmitted())).forEachOrdered((student) -> {
            addressOfUnsubmited.add(student.getEmailAddress());
        });

        return (String[]) addressOfUnsubmited.toArray(new String[addressOfUnsubmited.size()]);
    }

    /**
     * 设置HTML格式的时间戳
     *
     * @param time 时间戳
     * @return HTML格式的时间戳
     */
    private String setTimeAsHtmlStyle(String time) {
        return time + "</div>";
    }
}
