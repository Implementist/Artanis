/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import com.implementist.nisljournalmanager.domain.Identity;
import com.sun.mail.util.MailConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import org.apache.log4j.Logger;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Implementist
 */
@Service
@EnableRetry(proxyTargetClass = true)
public class NetEase163Service {

    private final Logger logger = Logger.getLogger(NetEase163Service.class);

    private final HashMap<String, String> ports = new HashMap<String, String>() {
        {
            put("imap", "993");
            put("smtp", "994");
            put("pop3", "995");
        }
    };

    /**
     * 获取网易163邮箱协议属性
     *
     * @param protocol 协议
     * @return 网易163邮箱协议属性
     */
    public Properties getProperties(String protocol) {
        Properties properties = new Properties();
        // 开启debug调试  
        properties.setProperty("mail.debug", "true");
        // 发送服务器需要身份验证  
        properties.setProperty("mail." + protocol + ".auth", "true");
        // 设置邮件服务器主机名
        properties.setProperty("mail." + protocol + ".host", protocol + ".163.com");
        // 设置端口
        properties.setProperty("mail." + protocol + ".port", ports.get(protocol));
        // 使用JSSE的SSL socketfactory 来取代默认的socketfactory
        properties.setProperty("mail." + protocol + ".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // 只处理SSL的连接,对于非SSL的连接不做处理
        properties.setProperty("mail." + protocol + ".socketFactory.fallback", "false");
        // 设置套接字中的端口
        properties.setProperty("mail." + protocol + ".socketFactory.port", ports.get(protocol));

        // 存储协议
        if (!protocol.equals("smtp")) {
            properties.setProperty("mail.store.protocol", protocol);
        }
        return properties;
    }

    /**
     * 获取邮件通信会话
     *
     * @param properties 通信属性
     * @return 邮件通信会话
     */
    @Retryable(value = MailConnectException.class, maxAttempts = 90, backoff = @Backoff)
    public Session getSession(Properties properties) {
        return Session.getInstance(properties);
    }

    @Recover
    public Session recover(MailConnectException mce, Properties properties) {
        logger.error("Mail Connection Still Failing After 90 Times of Attempts.", mce);
        return Session.getInstance(properties);
    }

    /**
     * 获取邮箱存储
     *
     * @param session 邮件通信会话
     * @param identity 邮箱身份
     * @return 邮箱存储
     * @throws MessagingException
     */
    @Retryable(value = {MessagingException.class, NoSuchProviderException.class,
        UnknownHostException.class, MailConnectException.class}, maxAttempts = 90,
            backoff = @Backoff)
    public Store getStore(Session session, Identity identity) throws MessagingException {
        Store store = session.getStore();
        store.connect(identity.getFrom(), identity.getAuthorizationCode());
        return store;
    }

    @Recover
    public Store recover(MessagingException me, Session session, Identity identity) {
        logger.error("Messaging Exception Still Remaining After 90 Times of Attemts.", me);
        return null;
    }

    @Recover
    public Store recover(NoSuchProviderException nspe, Session session, Identity identity) {
        logger.error("No Such Provider Exception Still Remaining After 90 Times of Attemts.", nspe);
        return null;
    }

    @Recover
    public Store recover(UnknownHostException uhe, Session session, Identity identity) {
        logger.error("Unknown Host Exception Still Remaining After 90 Times of Attemts.", uhe);
        return null;
    }

    @Recover
    public Store recover(MailConnectException mce, Session session, Identity identity) {
        logger.error("Mail Connection Still Failing After 90 Times of Attempts.", mce);
        return null;
    }

    /**
     * 获取邮箱传输连接
     *
     * @param transport 邮箱传输
     * @param identity 邮箱身份
     * @throws MessagingException
     */
    @Retryable(value = {MessagingException.class, NoSuchProviderException.class,
        UnknownHostException.class, MailConnectException.class}, maxAttempts = 90,
            backoff = @Backoff)
    public void getTransportConncted(Transport transport, Identity identity) throws MessagingException {
        transport.connect(identity.getFrom(), identity.getAuthorizationCode());
    }

    @Recover
    public void recover(MessagingException me, Transport transport, Identity identity) {
        logger.error("Messaging Exception Still Remaining After 90 Times of Attemts.", me);
    }

    @Recover
    public void recover(NoSuchProviderException nspe, Transport transport, Identity identity) {
        logger.error("No Such Provider Exception Still Remaining After 90 Times of Attemts.", nspe);
    }

    @Recover
    public void recover(UnknownHostException uhe, Transport transport, Identity identity) {
        logger.error("Unknown Host Exception Still Remaining After 90 Times of Attemts.", uhe);
    }

    @Recover
    public void recover(MailConnectException mce, Transport transport, Identity identity) {
        logger.error("Mail Connection Still Failing After 90 Times of Attempts.", mce);
    }
}
