package com.implementist.artanis.runnable;

import com.implementist.artanis.entity.Mail;
import com.implementist.artanis.entity.taskdata.UrgeTaskData;
import com.implementist.artanis.repository.MemberRepository;
import com.implementist.artanis.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 *
 * @author Implementist
 */
public class UrgeTask extends BaseTask {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MailService mailService;

    private static ThreadLocal<UrgeTaskData> UrgeTaskDataUnitHolder;

    UrgeTask(ApplicationContext context, UrgeTaskData taskDataUnit) {
        AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
        UrgeTaskDataUnitHolder = new ThreadLocal<>();
        UrgeTaskDataUnitHolder.set(taskDataUnit);
    }

    @Override
    public void run() {
        UrgeTaskData taskData = UrgeTaskDataUnitHolder.get();
        //从邮箱读入日志，提交了日志的同学的Submitted位会被置位1
        mailService.read(taskData.getMailSenderIdentity());

        //获取未提交日志的学生的地址数组
        String[] addresses = getAddressesOfUnsubmited(taskData.getGroups());
        //向每一位Submitted位为0的学生发送督促邮件
        if (addresses.length > 0) {
            Mail mail = new Mail(
                    taskData.getMailSubject(),
                    taskData.getMailContent(),
                    addresses
            );
            mailService.send(taskData.getMailSenderIdentity(), mail);
        }
        UrgeTaskDataUnitHolder.remove();
    }

    /**
     * 获取每一位未提交日志同学的地址
     *
     * @return 未提交日志同学的地址数组
     */
    private String[] getAddressesOfUnsubmited(List<Integer> groups) {
        List<String> addressOfUnsubmited = memberRepository.queryEmailAddressByGroups(groups, false);
        return addressOfUnsubmited.toArray(new String[0]);
    }
}
