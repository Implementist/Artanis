package com.implementist.artanis.service;

import com.implementist.artanis.entity.Identity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Implementist
 */
@Slf4j
@Service
@EnableRetry(proxyTargetClass = true)
public class NetEase163Service {

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
        String smtp = "smtp";
        if (!protocol.equals(smtp)) {
            properties.setProperty("mail.store.protocol", protocol);
        }
        return properties;
    }

    /**
     * 获取邮件通信会话
     *
     * @param properties 通信属性
     * @return 邮件通信会话
     * @throws RuntimeException 任何运行时异常
     */
    @Retryable(value = RuntimeException.class, maxAttempts = 90, backoff = @Backoff)
    public Session getSession(Properties properties) throws RuntimeException {
        return Session.getInstance(properties);
    }

    @Recover
    public Session recover(Exception ex, Properties properties) {
        log.error("Exception Still Remaining After 90 Times of Attemts.", ex);
        return null;
    }

    /**
     * 发送邮件
     *
     * @param session   邮箱通信会话
     * @param identity  邮箱身份
     * @param msg       邮件内容
     * @param addresses 收件人列表
     * @throws NoSuchProviderException 没有这个服务提供商异常
     * @throws MessagingException      信息异常
     */
    @Retryable(value = {NoSuchProviderException.class, MessagingException.class}, maxAttempts = 90, backoff = @Backoff)
    public void sendMessage(Session session, Identity identity, Message msg, Address[] addresses) throws NoSuchProviderException, MessagingException {
        try (Transport transport = session.getTransport()) {
            transport.connect(identity.getFrom(), identity.getAuthCode());
            transport.sendMessage(msg, addresses);
        }
    }

    @Recover
    public void recover(Exception ex, Session session, Identity identity, Message msg, Address[] addresses) {
        log.error("Exception Still Remaining After 90 Times of Attemts.", ex);
    }

    /**
     * 获取邮箱存储
     *
     * @param session  邮件通信会话
     * @param identity 邮箱身份
     * @return 邮箱存储
     * @throws NoSuchProviderException 没有这个服务提供商异常
     * @throws MessagingException      信息异常
     */
    @Retryable(value = {NoSuchProviderException.class, MessagingException.class}, maxAttempts = 90, backoff = @Backoff)
    public Store getStore(Session session, Identity identity) throws NoSuchProviderException, MessagingException {
        Store store = session.getStore();
        store.connect(identity.getFrom(), identity.getAuthCode());
        return store;
    }

    @Recover
    public Store recover(Exception ex, Session session, Identity identity) {
        log.error("Exception Still Remaining After 90 Times of Attemts.", ex);
        return null;
    }
}
