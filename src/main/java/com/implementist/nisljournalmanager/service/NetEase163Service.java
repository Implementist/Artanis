/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author Implementist
 */
@Service
@Scope("prototype")
public class NetEase163Service {

    /**
     * 获取网易163邮箱SMTP协议属性
     *
     * @return 网易163邮箱SMTP属性
     */
    public Properties getSMTPProperties() {
        Properties properties = new Properties();
        // 开启debug调试  
        properties.setProperty("mail.debug", "true");
        // 发送服务器需要身份验证  
        properties.setProperty("mail.smtp.auth", "true");
        // 设置邮件服务器主机名  
        properties.setProperty("mail.host", "smtp.163.com");
        // 使用JSSE的SSL socketfactory 来取代默认的socketfactory
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // 只处理SSL的连接,对于非SSL的连接不做处理
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        // 设置SMTP端口
        properties.setProperty("mail.smtp.port", "465");
        // 设置SMTP套接字中的端口
        properties.setProperty("mail.smtp.socketFactory.port", "465");

        return properties;
    }

    /**
     * 获取网易163邮箱POP3协议属性
     *
     * @return 网易163邮箱POP3协议属性
     */
    public Properties getPOP3Properties() {
        Properties properties = new Properties();
        // 发送邮件协议名称
        properties.setProperty("mail.store.protocol", "pop3");
        // 设置邮件服务器主机名 
        properties.setProperty("mail.pop3.host", "pop.163.com");
        // 端口
        properties.setProperty("mail.pop3.port", "110");

        return properties;
    }

    /**
     * 获取网易163邮箱IMAP协议属性
     *
     * @return 网易163邮箱IMAP协议属性
     */
    public Properties getIMAPProperties() {
        Properties properties = new Properties();
        // 开启debug调试  
        properties.setProperty("mail.debug", "true");
        // 发送服务器需要身份验证  
        properties.setProperty("mail.imap.auth", "true");
        // 设置邮件服务器主机名 
        properties.setProperty("mail.imap.host", "imap.163.com");
        // 移动邮件协议名称
        properties.setProperty("mail.store.protocol", "imap");

        return properties;
    }

    /**
     * 获取网易163邮箱POP3协议连接
     *
     * @param session 邮件通信会话
     * @param destination 目标邮箱地址
     * @param authorizationCode 授权码
     * @return 网易163邮箱POP3协议连接
     */
    public Store getPOP3Store(Session session, String destination, String authorizationCode) {
        try {
            Store store = session.getStore("pop3");
            store.connect(destination, authorizationCode);
            return store;
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(NetEase163Service.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (MessagingException ex) {
            Logger.getLogger(NetEase163Service.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * 获取网易163邮箱IMAP协议连接
     *
     * @param session 邮件通信会话
     * @param destination 目标邮箱地址
     * @param authorizationCode 授权码
     * @return 网易163邮箱IMAP协议连接
     */
    public Store getImapStore(Session session, String destination, String authorizationCode) {
        try {
            Store store = session.getStore("imap");
            store.connect("imap.163.com", destination, authorizationCode);
            return store;
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(NetEase163Service.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (MessagingException ex) {
            Logger.getLogger(NetEase163Service.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * 获取邮件通信会话
     *
     * @param properties 通信属性
     * @return 邮件通信会话
     */
    public Session getSession(Properties properties) {
        return Session.getInstance(properties);
    }
}
