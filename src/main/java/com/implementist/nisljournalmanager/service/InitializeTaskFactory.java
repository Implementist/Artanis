/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import com.implementist.nisljournalmanager.dao.MemberDAO;
import com.implementist.nisljournalmanager.domain.InitializeTask;
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
    private TimeService timeService;

    @Autowired
    private MailService mailService;

    private static ThreadLocal<InitializeTask> initializeTaskHolder;

    @SuppressWarnings("LeakingThisInConstructor")
    public InitializeTaskFactory(ServletContext context) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(context);
        AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
        initializeTaskHolder = new ThreadLocal<>();
    }

    @Override
    protected void build(Object initializeTask) {
        runnable = () -> {
            initializeTaskHolder.set((InitializeTask) initializeTask);
            if (!timeService.isRestDayToday(initializeTaskHolder.get().getRestDays())) {
                memberDAO.updateContentOfEveryStudent(initializeTaskHolder.get().getInitialContent());
                mailService.move(initializeTaskHolder.get().getMailSenderIdentity(), initializeTaskHolder.get().getSourceFolder(), initializeTaskHolder.get().getTargetFolder());
            }
            initializeTaskHolder.remove();
        };
    }

}
