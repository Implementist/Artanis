/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import com.implementist.nisljournalmanager.dao.MemberDAO;
import com.implementist.nisljournalmanager.domain.InitializeTask;
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
public class InitializeTaskFactory extends TaskFactory {

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private MailService mailService;

    private InitializeTask initializeTask;

    @SuppressWarnings("LeakingThisInConstructor")
    public InitializeTaskFactory(ServletContext context) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(context);
        AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
    }

    public void setInitializeTask(InitializeTask initializeTask) {
        this.initializeTask = initializeTask;
    }

    @Override
    protected void buildTask() {
        runnable = () -> {
            //获取当前是周几，为避免变量名冲突，故使用匿名方式
            int dayOfWeek = getDayOfWeek(new Date());

            if (!isRestDay(dayOfWeek, initializeTask.getRestDays())) {
                memberDAO.updateContentOfEveryStudent(initializeTask.getInitialContent());
                mailService.move(initializeTask.getMailSenderIdentity(), initializeTask.getSourceFolder(), initializeTask.getTargetFolder());
            }
        };
    }

}
