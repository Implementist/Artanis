package com.implementist.artanis.service;

import com.implementist.artanis.entity.Group;
import com.implementist.artanis.entity.Member;
import com.implementist.artanis.repository.GroupRepository;
import com.implementist.artanis.repository.MemberRepository;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Administrator
 */
@Service
@Scope("prototype")
public class SummaryFileService {

    private final Logger logger = LoggerFactory.getLogger(SummaryFileService.class);

    private final GroupRepository groupRepository;

    private final MemberRepository memberRepository;

    private final TimeService timeService;

    private final JournalParsingService journalParsingService;

    @Autowired
    public SummaryFileService(GroupRepository groupRepository, MemberRepository memberRepository,
                              TimeService timeService, JournalParsingService journalParsingService) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.timeService = timeService;
        this.journalParsingService = journalParsingService;
    }

    /**
     * 创建日志汇总PDF文件
     *
     * @param groups             小组列表
     * @param nameStringOfGroups 小组的名字字符串
     */
    public void create(List<Integer> groups, String nameStringOfGroups) {
        // 创建Document对象(页面的大小为A4,左、右、上、下的页边距为10)
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        try {
            String dateString = timeService.getDateString();

            // 建立书写器
            PdfWriter.getInstance(document, new FileOutputStream("/NISLJournal/DailySummary-Group" + nameStringOfGroups + "-" + dateString + ".PDF"));

            // 设置相关的参数
            setParameters(document, "DailySummary" + dateString, dateString + "日报汇总", "NISLJrounalManager", "NISLJrounalManager");

            // 打开文档
            document.open();
            // 添加表头
            document.add(getHeader());
            // 按小组添加日志内容
            addContentsByGroup(document, groups);
        } catch (IOException | DocumentException ex) {
            logger.error("IO Exception! | Document Exception!", ex);
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
     * @param group   组名
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

        for (Member member : members) {
            Cell memberName = fillCellWithContent(member.getName());
            String reformatedContent = journalParsingService.parse(member.getContent());
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
        } catch (IOException | DocumentException ex) {
            logger.error("IO Exception! | Document Exception!", ex);
        }
        return new Font(baseFont, fontSize, Font.NORMAL, Color.BLACK);
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
     * @param title    标题
     * @param subject  主题
     * @param author   作者
     * @param creator  制作人
     */
    private void setParameters(Document document, String title, String subject, String author,
                               String creator) {
        // 设置标题
        document.addTitle(title);
        // 设置主题
        document.addSubject(subject);
        // 设置作者
        document.addAuthor(author);
        // 设置创建者
        document.addCreator(creator);
        // 设置生产者
        document.addProducer();
        // 设置创建日期
        document.addCreationDate();
    }

    /**
     * 按小组添加日志内容
     *
     * @param document 文档对象
     */
    private void addContentsByGroup(Document document, List<Integer> groups) throws DocumentException {
        for (int group : groups) {
            Group groupInfo = groupRepository.queryById(group);
            List<Member> groupMembers = memberRepository.queryByGroupId(group);
            Table groupContent = getGroupContent(groupInfo.getName(), groupMembers);
            document.add(groupContent);
        }
    }
}
