/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

import com.implementist.nisljournalmanager.dao.GroupDAO;
import com.implementist.nisljournalmanager.dao.MemberDAO;
import com.implementist.nisljournalmanager.domain.Group;
import com.implementist.nisljournalmanager.domain.Member;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author Administrator
 */
@Service
@Scope("prototype")
public class SummarizeFileService {

    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private TimeService timeService;

    /**
     * 创建日志汇总PDF文件
     *
     * @param groups 小组列表
     * @param nameStringOfGroups 小组的名字字符串
     */
    public void create(int[] groups, String nameStringOfGroups) {
        // 创建Document对象(页面的大小为A4,左、右、上、下的页边距为10)
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        try {
            String dateString = timeService.getDateString();

            // 建立书写器
            PdfWriter.getInstance(document, new FileOutputStream("/NISLJournal/DailySummary-Group" + nameStringOfGroups + "-" + dateString + ".PDF"));

            // 设置相关的参数
            setParameters(document, "DailySummary" + dateString, dateString + "日报汇总", "NISLJrounalManager", "NISLJrounalManager");

            document.open();  // 打开文档
            document.add(getHeader());  // 添加表头
            addContentsByGroup(document, groups);  // 按小组添加日志内容
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(SummarizeFileService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            document.close();  // 关闭文档
        }
    }

    /**
     * 添加表头
     *
     * @return 只包含表头的表
     * @throws BadElementException 坏成员异常
     */
    private Table getHeader() throws BadElementException {
        //创建一个有5列的表格
        Table table = new Table(5);
        table.setBorderWidth(1);
        table.setBorderColor(Color.BLACK);
        table.setPadding(5);

        // 创建表头
        Cell name = getHeaderCell("姓名");
        Cell content = getHeaderCell("内容");
        content.setColspan(4);

        table.addCell(name);
        table.addCell(content);

        return table;
    }

    /**
     * 添加内容
     *
     * @param group 组名
     * @param members 该组成员列表
     * @return 只包含该组成员日报内容的表
     * @throws BadElementException 坏成员异常
     */
    private Table getGroupContent(String group, List<Member> members) throws BadElementException {
        //创建一个有5列的表格
        Table table = new Table(5);
        table.setBorderWidth(1);
        table.setBorderColor(Color.BLACK);
        table.setPadding(5);

        Cell groupHeader = getGroupHeaderCell(group);
        table.addCell(groupHeader);

        for (int i = 0; i < members.size(); i++) {
            Cell memberName = fillCellWithContent(members.get(i).getName());
            String reformatedContent = reformatContent(members.get(i).getContent());
            Cell journalContent = fillCellWithContent(reformatedContent);
            journalContent.setColspan(4);
            table.addCell(memberName);
            table.addCell(journalContent);
        }
        return table;
    }

    /**
     * 设置字体编码格式
     *
     * @param fontSize 字体大小
     * @return 字体
     */
    private Font setFont(int fontSize) {
        BaseFont baseFont = null;
        try {
            baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        } catch (DocumentException | IOException e) {
            java.util.logging.Logger.getLogger(SummarizeFileService.class.getName()).log(Level.SEVERE, null, e);
        }
        Font font = new Font(baseFont, fontSize, Font.NORMAL, Color.BLACK);
        return font;
    }

    /**
     * 设置表头单元
     *
     * @param name 列名
     * @return 包含列名的单元格
     * @throws BadElementException 坏成员异常
     */
    private Cell getHeaderCell(String name) throws BadElementException {

        Cell cell = new Cell(new Phrase(name, setFont(12)));
        //单元格水平对齐方式
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        //单元格垂直对齐方式
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.WHITE);
        return cell;
    }

    /**
     * 设置小组头单元
     *
     * @param name 组名
     * @return 包含组名的单元格
     * @throws BadElementException 坏成员异常
     */
    private Cell getGroupHeaderCell(String name) throws BadElementException {
        Font font = setFont(16);
        font.setStyle(Font.BOLD);
        Cell cell = new Cell(new Phrase(name, font));
        //单元格水平对齐方式
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        //单元格垂直对齐方式
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.WHITE);
        cell.setColspan(5);
        return cell;
    }

    /**
     * 设置内容单元
     *
     * @param content 内容
     * @return 包含内容的单元格
     * @throws BadElementException 坏成员异常
     */
    private Cell fillCellWithContent(String content) throws BadElementException {
        Cell cell = new Cell(new Phrase(content, setFont(12)));
        //单元格水平对齐方式
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        //单元格垂直对齐方式
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.WHITE);
        return cell;
    }

    /**
     * 设置相关参数
     *
     * @param document 文档
     * @param title 标题
     * @param subject 主题
     * @param author 作者
     * @param creator 制作人
     * @return 设置过相关参数的文档
     */
    private Document setParameters(Document document, String title, String subject, String author,
            String creator) {
        document.addTitle(title);  // 设置标题
        document.addSubject(subject);  // 设置主题
        document.addAuthor(author);  // 设置作者
        document.addCreator(creator);  // 设置创建者
        document.addProducer();  // 设置生产者
        document.addCreationDate();  // 设置创建日期
        return document;
    }

    /**
     * 从数据库中取得HTML形式的日报内容，解析为纯文本
     *
     * @param sourceContent 源内容
     * @return 纯文本内容
     */
    private String reformatContent(String sourceContent) {
        if (sourceContent.equals("")) {
            return "该同学未按时提交日志！";
        }

        int pTraverse = 0;
        StringBuilder reformatedContent = new StringBuilder();

        //去掉带有a标签链接的广告
        sourceContent = removeAds(sourceContent);

        //把每个p标签解析为一个换行符
        sourceContent = sourceContent.replaceAll("<p", "\n<");

        //把每个div标签解析为一个换行符
        sourceContent = sourceContent.replaceAll("<div", "\n<");

        //把每个<br>标签解析为一个换行符
        sourceContent = sourceContent.replaceAll("<br>", "\n");

        //对html格式的内容进行去标签
        for (; pTraverse < sourceContent.length(); pTraverse++) {
            if (sourceContent.charAt(pTraverse) == '<') {
                pTraverse++;
                while (pTraverse < sourceContent.length()
                        && sourceContent.charAt(pTraverse) != '>') {
                    pTraverse++;
                }
            } else {
                reformatedContent.append(sourceContent.charAt(pTraverse));
            }
        }

        String content = reformatedContent.toString();

        //将被浏览器转义过的字符转回来
        content = content.replaceAll("&lt;", "<");
        content = content.replaceAll("&gt;", ">");
        content = content.replaceAll("&amp;", "&");
        content = content.replaceAll("&nbsp;", "  ");

        //去除前后多余的空格
        content = content.trim();

        //将换行符前的空格递归移到换行符后
        while (content.contains(" \n")) {
            content = content.replaceAll(" \n", "\n ");
        }

        //递归删除多余的换行符
        while (content.contains("\n\n")) {
            content = content.replaceAll("\n\n", "\n");
        }

        //去除内容前面的换行符
        while (content.length() > 1 && content.charAt(0) == '\n') {
            content = content.substring(1);
        }

        return content;
    }

    /**
     * 去除邮件内容中的文字跳转类广告
     *
     * @param mailContent 邮件内容
     * @return 去除广告后的邮件内容
     */
    private String removeAds(String mailContent) {
        while (mailContent.contains("</a>")) {
            int startIndex = mailContent.indexOf("<a"),
                    endIndex = mailContent.indexOf("</a") + 4;
            String wholeATag = mailContent.substring(startIndex, endIndex);
            mailContent = mailContent.replace(wholeATag, "");
        }
        return mailContent;
    }

    /**
     * 按小组添加日志内容
     *
     * @param document 文档对象
     */
    private void addContentsByGroup(Document document, int[] groupIds) throws BadElementException, DocumentException {
        for (int groupId : groupIds) {
            Group groupInfo = groupDAO.queryGroupById(groupId);
            ArrayList<Member> groupMembers = memberDAO.queryByGroup(groupId);
            Table groupContent = getGroupContent(groupInfo.getName(), groupMembers);
            document.add(groupContent);
        }
    }
}
