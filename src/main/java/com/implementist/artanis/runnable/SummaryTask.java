package com.implementist.artanis.runnable;

import com.implementist.artanis.entity.Mail;
import com.implementist.artanis.entity.taskdata.SummaryTaskData;
import com.implementist.artanis.repository.MemberRepository;
import com.implementist.artanis.service.MailService;
import com.implementist.artanis.service.SummaryFileService;
import com.implementist.artanis.service.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

/**
 *
 * @author Implementist
 */
public class SummaryTask extends BaseTask {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SummaryFileService summaryFileService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private MailService mailService;

    private static ThreadLocal<SummaryTaskData> summaryTaskDataUnitHolder;

    SummaryTask(ApplicationContext context, SummaryTaskData taskDataUnit) {
        AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
        summaryTaskDataUnitHolder = new ThreadLocal<>();
        summaryTaskDataUnitHolder.set(taskDataUnit);
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void run() {
        SummaryTaskData taskData = summaryTaskDataUnitHolder.get();
        String nameStringOfGroups = getNameStringOfGroups(taskData.getGroups());
        //获取当前日期时间字符串
        String dateString = timeService.getDateString();
        //从邮箱读入日志，提交了日志的同学的Submitted位会被置位1
        mailService.read(taskData.getMailSenderIdentity());
        //创建日报汇总PDF文件
        summaryFileService.create(taskData.getGroups(), nameStringOfGroups);

        String[] toList, ccList;
        if (taskData.isForBossesOnly()) {
            toList = taskData.getBossesAddresses();
            ccList = null;
        } else {
            toList = getToList(taskData.getGroups());
            ccList = taskData.getBossesAddresses();
        }

        Mail mail = new Mail(
                taskData.getMailSubject() + dateString,
                taskData.getMailContent(),
                toList,
                ccList,
                new String[]{System.getProperty("user.dir").split("\\\\")[0] + File.separator + "NISLJournal" + File.separator + "DailySummary-Group" + nameStringOfGroups + "-" + dateString + ".PDF"}
        );
        mailService.send(taskData.getMailSenderIdentity(), mail);
        memberRepository.updateSubmittedByGroups(taskData.getGroups(), true);
        summaryTaskDataUnitHolder.remove();
    }

    /**
     * 获取目标列表
     *
     * @param groups 小组号列表
     * @return 目标列表
     */
    private String[] getToList(List<Integer> groups) {
        List<String> toList = memberRepository.queryEmailAddressByGroups(groups);
        return toList.toArray(new String[0]);
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
}
