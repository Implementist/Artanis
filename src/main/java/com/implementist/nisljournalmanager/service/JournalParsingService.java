/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import org.springframework.stereotype.Service;

/**
 *
 * @author Implementist
 */
@Service
public class JournalParsingService {

    /**
     * 从数据库中取得HTML形式的日报内容，解析为纯文本
     *
     * @param sourceContent 源内容
     * @return 纯文本内容
     */
    public String parse(String sourceContent) {
        if ("".equals(sourceContent)) {
            return "该同学未按时提交日志！";
        }

        //1. 清洁邮件内容，去掉不需要的部分
        sourceContent = clean(sourceContent);
        //2. 转译部分标签，释放出换行符
        sourceContent = translate(sourceContent);
        //3. 从标签中提取日报内容
        String content = extract(sourceContent);
        //4. 将被浏览器转义过的字符转回来
        content = reverseTranslate(content);
        //5. 修剪，去除多余的空格符、回车符
        content = trim(content);
        return content;
    }

    /**
     * 清洁邮件内容，去掉不需要的部分
     *
     * @param mailContent 邮件内容
     * @return 清洁后的邮件内容
     */
    private String clean(String mailContent) {
        //去掉HTML注释
        mailContent = removeHtmlComments(mailContent);
        //去掉XML标签
        mailContent = removeXmlTags(mailContent);
        //去掉带有a标签链接的广告
        mailContent = removeAds(mailContent);
        //去除Style标签定义的样式信息
        mailContent = removeStyles(mailContent);
        //去除Script标签定义的脚本信息
        mailContent = removeScripts(mailContent);
        return mailContent;
    }

    /**
     * 转译部分标签，释放出换行符
     *
     * @param mailContent 邮件内容
     * @return 转以后的邮件内容
     */
    private String translate(String mailContent) {
        //把每个p标签解析为一个换行符
        mailContent = mailContent.replaceAll("<p ", "\n<");
        //把每个div标签解析为一个换行符
        mailContent = mailContent.replaceAll("<div", "\n<");
        //把每个<br>标签解析为一个换行符
        mailContent = mailContent.replaceAll("<br>", "\n");
        return mailContent;
    }

    /**
     * 提取日报内容
     *
     * @param mailContent 邮件内容
     * @return 日报内容
     */
    private String extract(String mailContent) {
        int pTraverse = 0;
        StringBuilder content = new StringBuilder();
        for (; pTraverse < mailContent.length(); pTraverse++) {
            if (mailContent.charAt(pTraverse) == '<') {
                pTraverse++;
                while (pTraverse < mailContent.length()
                        && mailContent.charAt(pTraverse) != '>') {
                    pTraverse++;
                }
            } else {
                content.append(mailContent.charAt(pTraverse));
            }
        }

        return content.toString();
    }

    /**
     * 将被浏览器转义过的字符转回来
     *
     * @param mailContent 邮件内容
     * @return 逆转译后的邮件内容
     */
    private String reverseTranslate(String mailContent) {
        mailContent = mailContent.replaceAll("&lt;", "<");
        mailContent = mailContent.replaceAll("&gt;", ">");
        mailContent = mailContent.replaceAll("&amp;", "&");
        mailContent = mailContent.replaceAll("&nbsp;", "  ");
        return mailContent;
    }

    /**
     * 修剪，去除多余的空格符、回车符
     *
     * @param mailContent 邮件内容
     * @return 最终的日报内容
     */
    private String trim(String mailContent) {
        //去除前后多余的空格
        mailContent = mailContent.trim();

        //将换行符前的空格递归移到换行符后
        String spaceAndNewLine = " \n";
        while (mailContent.contains(spaceAndNewLine)) {
            mailContent = mailContent.replaceAll(" \n", "\n ");
        }
        
        //递归删除回车符后多余的空格符
        String newLineAndSpace = "\n ";
        while (mailContent.contains(newLineAndSpace)) {
            mailContent = mailContent.replaceAll("\n ", "\n");
        }
        
        //递归删除多余的换行符
        String doubleNewLines = "\n\n";
        while (mailContent.contains(doubleNewLines)) {
            mailContent = mailContent.replaceAll("\n\n", "\n");
        }

        //去除内容前面的换行符
        mailContent = mailContent.trim();
        return mailContent;
    }

    /**
     * 去除邮件内容中的HTML注释
     *
     * @param mailContent 邮件内容
     * @return 去除HTML注释后的邮件内容
     */
    private String removeHtmlComments(String mailContent) {
        return mailContent.replaceAll("<!--[\\s\\S]*?-->", "");
    }

    /**
     * 去除邮件内容中的XML标签
     *
     * @param mailContent 邮件内容
     * @return 去除XML标签后的邮件内容
     */
    private String removeXmlTags(String mailContent) {
        return mailContent.replaceAll("<xml[\\s\\S]*?</xml>", "");
    }

    /**
     * 去除邮件内容中的文字跳转类广告
     *
     * @param mailContent 邮件内容
     * @return 去除广告后的邮件内容
     */
    private String removeAds(String mailContent) {
        return mailContent.replaceAll("<a [\\s\\S]*?</a>", "");
    }

    /**
     * 去除邮件内容中的Style标签
     *
     * @param mailContent 邮件内容
     * @return 去除Style标签之后的邮件内容
     */
    private String removeStyles(String mailContent) {
        return mailContent.replaceAll("<style[\\s\\S]*?</style>", "");
    }

    /**
     * 去除邮件内容中的Script标签
     *
     * @param mailContent 邮件内容
     * @return 去除Script标签之后的邮件内容
     */
    private String removeScripts(String mailContent) {
        return mailContent.replaceAll("<script[\\s\\S]*?</script>", "");
    }
}
