package com.implementist.artanis.entity.task;

import com.implementist.artanis.dao.MemberDAO;
import com.implementist.artanis.entity.taskdata.InitializeTaskData;
import com.implementist.artanis.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 *
 * @author Implementist
 */
public class InitializeTask extends BaseTask {

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private MailService mailService;

    private static ThreadLocal<InitializeTaskData> initializeTaskDataHolder;

    @SuppressWarnings("LeakingThisInConstructor")
    public InitializeTask(ServletContext context, InitializeTaskData taskData) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(context);
        AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
        initializeTaskDataHolder = new ThreadLocal<>();
        initializeTaskDataHolder.set(taskData);
    }

    @Override
    public void run() {
        InitializeTaskData taskData = initializeTaskDataHolder.get();
        memberDAO.updateContentOfEveryStudent(taskData.getInitialContent());
        mailService.move(taskData.getMailSenderIdentity(), taskData.getSourceFolder(), taskData.getTargetFolder());
        initializeTaskDataHolder.remove();
    }
}
