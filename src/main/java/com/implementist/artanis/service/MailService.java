package com.implementist.artanis.service;

import com.implementist.artanis.entity.Identity;
import com.implementist.artanis.entity.Mail;
import com.implementist.artanis.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author Implementist
 */
@Slf4j
@Service
@Scope("prototype")
public class MailService {

    private final MemberRepository memberRepository;

    private final NetEase163Service netEase163Service;

    /**
     * 用于设置在一个邮件中是否已读出一份内容文本
     */
    private Boolean haveGotHtmlEdition;
    /**
     * 用于接收当前邮件中的内容文本
     */
    private String currentContent;

    @Autowired
    public MailService(MemberRepository memberRepository, NetEase163Service netEase163Service) {
        this.memberRepository = memberRepository;
        this.netEase163Service = netEase163Service;
    }

    /**
     * 发送邮件
     *
     * @param identity 邮件发送者身份
     * @param mail     邮件
     */
    public void send(Identity identity, Mail mail) {
        //设置邮件通信属性
        Properties properties = netEase163Service.getProperties("smtp");
        //获取邮件通信会话
        Session session = netEase163Service.getSession(properties);

        try {
            MimeMessage mimeMessage = buildMessage(session, identity, mail);
            // 发送邮件
            netEase163Service.sendMessage(session, identity, mimeMessage, mimeMessage.getAllRecipients());
        } catch (MessagingException ex) {
            log.error("Massaging Exception!", ex);
        } catch (UnsupportedEncodingException ex) {
            log.error("Unsupported Encoding Exception!", ex);
        } catch (Exception ex) {
            log.error("Exception!", ex);
        }
    }

    /**
     * 读取邮件
     *
     * @param identity 邮箱账户身份
     */
    public void read(Identity identity) {
        //设置邮件通信属性
        Properties properties = netEase163Service.getProperties("imap");
        //获取邮件通信会话
        Session session = netEase163Service.getSession(properties);

        try (Store store = netEase163Service.getStore(session, identity);
             Folder folder = store.getFolder("INBOX")) {
            //设置邮件只读
            folder.open(Folder.READ_ONLY);
            //将邮件中的日报中写回到数据库中
            saveJournalContents(folder.getMessages());
        } catch (MessagingException | IOException ex) {
            log.error("Massaging Exception! | IO Exception!", ex);
        } catch (Exception ex) {
            log.error("Exception!", ex);
        }
    }

    /**
     * 移动邮件
     *
     * @param identity 邮箱账户身份
     * @param source   源文件夹
     * @param target   目标文件夹
     */
    public void move(Identity identity, String source, String target) {

        //设置邮件通信属性
        Properties properties = netEase163Service.getProperties("imap");
        //获取邮件通信会话
        Session session = netEase163Service.getSession(properties);

        try (Store store = netEase163Service.getStore(session, identity);
             Folder sfolder = store.getFolder(source);
             Folder tfolder = store.getFolder(target)) {
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
            log.error("Massaging Exception!", ex);
        } catch (Exception ex) {
            log.error("Exception!", ex);
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
        setCc(mimeMessage, mail);
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
     * @throws MessagingException           信息异常
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
    private void setCc(MimeMessage mimeMessage, Mail mail) throws MessagingException {
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
     * @throws MessagingException           信息异常
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
     * @throws IOException        输入输出异常
     */
    private void getTextMultipart(Part part) throws MessagingException, IOException {
        String textPlain = "text/plain";
        String textHtml = "text/html";
        String multipartAll = "multipart/*";
        if (part.isMimeType(textPlain) && !haveGotHtmlEdition) {
            currentContent = (String) part.getContent();
        } else if (part.isMimeType(textHtml)) {
            currentContent = (String) part.getContent();
            haveGotHtmlEdition = true;
        } else if (part.isMimeType(multipartAll)) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                getTextMultipart(multipart.getBodyPart(i));
            }
        }
    }

    /**
     * 将各邮箱地址连接在一个字符串中
     *
     * @param addressArray 地址数组
     * @return 地址字符串
     */
    private String getAddressString(String[] addressArray) {
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

                currentContent = new String(currentContent.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                memberRepository.updateContentByEmailAddress(currentContent, address);
            }
        }
    }
}
