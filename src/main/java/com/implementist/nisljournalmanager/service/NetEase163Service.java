/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import com.implementist.nisljournalmanager.domain.Identity;
import java.util.HashMap;
import java.util.Properties;
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
        // 开启SSL连接方式
        properties.setProperty("mail." + protocol + ".ssl.enable", "true");

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
    @Retryable(value = Exception.class, maxAttempts = 60, backoff = @Backoff)
    public Session getSession(Properties properties) {
        return Session.getInstance(properties);
    }

    @Recover
    public Session recover(Exception ex, Properties properties) {
        logger.error("Exception Still Remaining After 60 Times of Attemts.", ex);
        return Session.getInstance(properties);
    }

    /**
     * 获取邮箱存储
     *
     * @param session 邮件通信会话
     * @param identity 邮箱身份
     * @return 邮箱存储
     * @throws Exception
     */
    @Retryable(value = {Exception.class}, maxAttempts = 60, backoff = @Backoff)
    public Store getStore(Session session, Identity identity) throws Exception {
        Store store = session.getStore();
        store.connect(identity.getFrom(), identity.getAuthorizationCode());
        return store;
    }

    @Recover
    public Store recover(Exception ex, Session session, Identity identity) {
        logger.error("Exception Still Remaining After 60 Times of Attemts.", ex);
        return null;
    }

    /**
     * 获取邮箱传输连接
     *
     * @param transport 邮箱传输
     * @param identity 邮箱身份
     * @throws Exception
     */
    @Retryable(value = {Exception.class}, maxAttempts = 60, backoff = @Backoff)
    public void getTransportConncted(Transport transport, Identity identity) throws Exception {
        transport.connect(identity.getFrom(), identity.getAuthorizationCode());
    }

    @Recover
    public void recover(Exception ex, Transport transport, Identity identity) {
        logger.error("Exception Still Remaining After 60 Times of Attemts.", ex);
    }
}
