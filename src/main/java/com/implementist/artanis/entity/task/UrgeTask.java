package com.implementist.artanis.entity.task;

import com.implementist.artanis.dao.MemberDAO;
import com.implementist.artanis.entity.Mail;
import com.implementist.artanis.entity.taskdata.UrgeTaskData;
import com.implementist.artanis.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.List;

/**
 *
 * @author Implementist
 */
public class UrgeTask extends BaseTask {

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private MailService mailService;

    private static ThreadLocal<UrgeTaskData> urgeTaskDataHolder;

    @SuppressWarnings("LeakingThisInConstructor")
    public UrgeTask(ServletContext context, UrgeTaskData taskData) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(context);
        AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
        urgeTaskDataHolder = new ThreadLocal<>();
        urgeTaskDataHolder.set(taskData);
    }

    @Override
    public void run() {
        UrgeTaskData taskData = urgeTaskDataHolder.get();
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
        urgeTaskDataHolder.remove();
    }

    /**
     * 获取每一位未提交日志同学的地址
     *
     * @return 未提交日志同学的地址数组
     */
    private String[] getAddressesOfUnsubmited(List<Integer> groups) {
        List<String> addressOfUnsubmited = memberDAO.queryEmailAddressByGroups(groups, false);
        return addressOfUnsubmited.toArray(new String[addressOfUnsubmited.size()]);
    }
}
