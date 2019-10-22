package com.implementist.artanis.runnable;

import com.implementist.artanis.entity.taskdata.InitializeTaskData;
import com.implementist.artanis.repository.MemberRepository;
import com.implementist.artanis.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author Implementist
 */
public class InitializeTask extends BaseTask {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MailService mailService;

    private static ThreadLocal<InitializeTaskData> initializeTaskDataHolder;

    public InitializeTask(ApplicationContext context, InitializeTaskData taskDataUnit) {
        AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
        initializeTaskDataHolder = new ThreadLocal<>();
        initializeTaskDataHolder.set(taskDataUnit);
    }

    @Override
    public void run() {
        InitializeTaskData taskData = initializeTaskDataHolder.get();
        memberRepository.updateAllContentAndSubmitted(taskData.getInitialContent(), false);
        mailService.move(taskData.getMailSenderIdentity(), taskData.getSourceFolder(), taskData.getTargetFolder());
        initializeTaskDataHolder.remove();
    }
}
