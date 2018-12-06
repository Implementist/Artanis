/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import com.implementist.nisljournalmanager.dao.MemberDAO;
import com.implementist.nisljournalmanager.domain.Identity;
import com.implementist.nisljournalmanager.domain.Mail;
import com.implementist.nisljournalmanager.domain.Member;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author Implementist
 */
@Service
@Scope("prototype")
public class MailService {

    private final Logger logger = Logger.getLogger(MailService.class);

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private NetEase163Service netEase163Service;

    private Boolean haveGotHtmlEdition;  //用于设置在一个邮件中是否已读出一份内容文本
    private String currentContent;  //用于接收当前邮件中的内容文本

    /**
     * 发送邮件
     *
     * @param identity 邮件发送者身份
     * @param mail 邮件
     */
    public void send(Identity identity, Mail mail) {
        //设置邮件通信属性
        Properties properties = netEase163Service.getProperties("smtp");
        //获取邮件通信会话
        Session session = netEase163Service.getSession(properties);

        try (Transport transport = session.getTransport()) {
            MimeMessage mimeMessage = buildMessage(session, identity, mail);

            //用邮箱地址和授权码连接邮件服务器
            netEase163Service.getTransportConncted(transport, identity);
            // 发送邮件
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        } catch (MessagingException | UnsupportedEncodingException ex) {
            logger.error("Massaging Exception! | Unsupported Encoding!", ex);
        }
    }

    /**
     * 读取邮件
     *
     * @param identity 邮箱账户身份
     */
    public void read(Identity identity) {
        //设置邮件通信属性
        Properties properties = netEase163Service.getProperties("pop3");
        //获取邮件通信会话
        Session session = netEase163Service.getSession(properties);

        try (Store store = netEase163Service.getStore(session, identity.getFrom(), identity.getAuthorizationCode());
                Folder folder = store.getFolder("INBOX");) {
            //设置邮件只读
            folder.open(Folder.READ_ONLY);
            //将邮件中的日报中写回到数据库中
            saveJournalContents(folder.getMessages());
        } catch (MessagingException | IOException ex) {
            logger.error("Massaging Exception! | IO Exception!", ex);
        }
    }

    /**
     * 移动邮件
     *
     * @param identity 邮箱账户身份
     * @param source 源文件夹
     * @param target 目标文件夹
     */
    public void move(Identity identity, String source, String target) {

        //设置邮件通信属性
        Properties properties = netEase163Service.getProperties("imap");
        //获取邮件通信会话
        Session session = netEase163Service.getSession(properties);

        try (Store store = netEase163Service.getStore(session, identity.getFrom(), identity.getAuthorizationCode());
                Folder sfolder = store.getFolder(source);
                Folder tfolder = store.getFolder(target);) {
            sfolder.open(Folder.READ_WRITE);
            tfolder.open(Folder.READ_WRITE);

            Message[] msgs = sfolder.getMessages();

            if (msgs.length != 0) {
                //将源文件夹下的邮件标记为已读
                sfolder.setFlags(msgs, new Flags(Flags.Flag.SEEN), true);
                //复制到新文件夹 
                sfolder.copyMessages(msgs, tfolder);
                //删除源文件夹下的邮件
                sfolder.setFlags(msgs, new Flags(Flags.Flag.DELETED), true);
            }
        } catch (MessagingException ex) {
            logger.error("Massaging Exception!", ex);
        }
    }

    /**
     * 构建邮件信息
     */
    private MimeMessage buildMessage(Session session, Identity identity, Mail mail)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = new MimeMessage(session);
        MimeMultipart multipart = new MimeMultipart();

        //装配邮件
        setSubject(mimeMessage, mail.getSubject());
        setFrom(mimeMessage, identity);
        setTo(mimeMessage, mail);
        setCC(mimeMessage, mail);
        setContent(multipart, mail.getContent());
        setAttachment(multipart, mail.getFiles());

        mimeMessage.setContent(multipart);
        mimeMessage.saveChanges();
        return mimeMessage;
    }

    /**
     * 设置主题
     *
     * @throws MessagingException 信息异常
     */
    private void setSubject(MimeMessage mimeMessage, String mailSubject) throws MessagingException {
        mimeMessage.setSubject(mailSubject);
    }

    /**
     * 设置发件人
     *
     * @throws UnsupportedEncodingException 不支持的编码异常
     * @throws MessagingException 信息异常
     */
    private void setFrom(MimeMessage mimeMessage, Identity identity) throws UnsupportedEncodingException, MessagingException {
        //自定义发件人昵称
        String nickNameEncoded = MimeUtility.encodeText(identity.getNickName());
        //设置发件人
        mimeMessage.setFrom(new InternetAddress(identity.getFrom(), nickNameEncoded));
    }

    /**
     * 设置收件人
     *
     * @throws MessagingException 信息异常
     */
    private void setTo(MimeMessage mimeMessage, Mail mail) throws MessagingException {
        String[] to = mail.getTo();
        if (null != to && to.length > 0) {
            String toListString = getAddressString(to);
            mimeMessage.addRecipients(Message.RecipientType.TO, toListString);
        }
    }

    /**
     * 设置抄送
     *
     * @throws MessagingException 信息异常
     */
    private void setCC(MimeMessage mimeMessage, Mail mail) throws MessagingException {
        String[] cc = mail.getCc();
        if (null != cc && cc.length > 0) {
            String ccListString = getAddressString(cc);
            mimeMessage.addRecipients(Message.RecipientType.CC, ccListString);
        }
    }

    /**
     * 设置内容
     *
     * @throws MessagingException 信息异常
     */
    private void setContent(MimeMultipart multipart, String content) throws MessagingException {
        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(content, "text/html;charset=utf-8");
        multipart.addBodyPart(bodyPart);
    }

    /**
     * 设置附件
     *
     * @throws MessagingException 信息异常
     * @throws UnsupportedEncodingException 不支持的编码异常
     */
    private void setAttachment(MimeMultipart multipart, String[] files) throws MessagingException, UnsupportedEncodingException {
        if (files != null && files.length > 0) {
            for (String file : files) {
                BodyPart bodyPart = new MimeBodyPart();
                FileDataSource fileDataSource = new FileDataSource(file);
                bodyPart.setDataHandler(new DataHandler(fileDataSource));
                bodyPart.setFileName(MimeUtility.encodeText(fileDataSource.getName(), "UTF-8", "B"));
                multipart.addBodyPart(bodyPart);
            }
        }
    }

    /**
     * 解析邮件内容
     *
     * @param part 内容分段
     * @throws MessagingException 通信异常
     * @throws IOException 输入输出异常
     */
    private void getTextMultipart(Part part) throws MessagingException, IOException {
        if (part.isMimeType("text/plain") && !haveGotHtmlEdition) {
            currentContent = (String) part.getContent();
        } else if (part.isMimeType("text/html")) {
            currentContent = (String) part.getContent();
            haveGotHtmlEdition = true;
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                getTextMultipart(multipart.getBodyPart(i));
            }
        }
    }

    /**
     * 从成员列表中提取出邮箱地址数组
     *
     * @param members 成员列表
     * @return 邮箱地址数组
     */
    public String[] getAddressArray(ArrayList<Member> members) {
        String[] addressArray = new String[members.size()];
        for (int i = 0; i < members.size(); i++) {
            addressArray[i] = members.get(i).getEmailAddress();
        }
        return addressArray;
    }

    /**
     * 将各邮箱地址连接在一个字符串中
     *
     * @param addressArray 地址数组
     * @return 地址字符串
     */
    public String getAddressString(String[] addressArray) {
        StringBuilder toList = new StringBuilder();
        if (null != addressArray && addressArray.length > 0) {
            toList.append(addressArray[0]);
            for (int i = 1; i < addressArray.length; i++) {
                toList.append(",").append(addressArray[i]);
            }
        }
        return toList.toString();
    }

    /**
     * 将日志内容写回数据库
     *
     * @param messages 邮件内容
     */
    private void saveJournalContents(Message[] messages) throws MessagingException, IOException {
        if (messages != null) {
            for (Message message : messages) {
                haveGotHtmlEdition = false;
                currentContent = "";

                //解析发件人地址
                String address = ((InternetAddress[]) message.getFrom())[0].getAddress();
                //解析内容文本的各个部分
                getTextMultipart(message);

                currentContent = new String(currentContent.getBytes("UTF-8"), "UTF-8");
                memberDAO.updateContentByAddress(currentContent, address);
            }
        }
    }
}
