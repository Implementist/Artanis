/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import com.sun.mail.util.MailConnectException;
import java.util.HashMap;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
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
     * 获取邮箱存储
     *
     * @param session 邮件通信会话
     * @param destination 目标邮箱地址
     * @param authorizationCode 授权码
     * @return 邮箱存储
     */
    public Store getStore(Session session, String destination, String authorizationCode) {
        try {
            Store store = session.getStore();
            store.connect(destination, authorizationCode);
            return store;
        } catch (NoSuchProviderException ex) {
            logger.error("No Such Provider!", ex);
            return null;
        } catch (MessagingException ex) {
            logger.error("Massaging Exception!", ex);
            return null;
        }
    }

    /**
     * 获取邮件通信会话
     *
     * @param properties 通信属性
     * @return 邮件通信会话
     */
    @Retryable(value = MailConnectException.class, maxAttempts = 60, backoff = @Backoff)
    public Session getSession(Properties properties) {
        return Session.getInstance(properties);
    }

    @Recover
    public void recover(MailConnectException mce) {
        logger.error("Mail Connection Still Failing After 60 Times of Attempts.", mce);
    }
}
