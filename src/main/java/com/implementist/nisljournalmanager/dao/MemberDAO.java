/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.dao;

import com.implementist.nisljournalmanager.domain.Member;
import java.util.ArrayList;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Implementist
 */
@Repository
public class MemberDAO {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * 根据邮箱地址更改日志内容
     *
     * @param content 日报内容
     * @param address 邮箱地址
     * @return 操作影响的行数
     */
    public int updateContentByAddress(String content, String address) {
        String sqlStatement = "UPDATE Member member SET member.content=:content,"
                + "member.submitted=:submitted WHERE member.emailAddress=:emailAddress";
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement);
        query.addEntity(Member.class);
        query.setParameter("content", content);
        query.setParameter("submitted", true);
        query.setParameter("emailAddress", address);
        return query.executeUpdate();
    }

    /**
     * 根据姓名更新该成员的日报内容
     *
     * @param content 日报内容
     * @param name 成员名
     * @return 操作影响的行数
     */
    public int updateContentByName(String content, String name) {
        String sqlStatement = "UPDATE Member member SET member.content=:content,"
                + "member.submitted=:submitted WHERE member.name=:name";
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement);
        query.addEntity(Member.class);
        query.setParameter("content", content);
        query.setParameter("submitted", true);
        query.setParameter("name", name);
        return query.executeUpdate();
    }

    /**
     * 更改所有学生的日报内容（一般用于清零）
     *
     * @param content 日报内容
     * @return 操作影响的行数
     */
    public int updateContentOfEveryStudent(String content) {
        String sqlStatement = "UPDATE Member member SET member.content=:content,"
                + "member.submitted=:submitted WHERE member.identity=:identity";
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement);
        query.addEntity(Member.class);
        query.setParameter("content", content);
        query.setParameter("submitted", false);
        query.setParameter("identity", "Student");
        return query.executeUpdate();
    }

    /**
     * 根据小组号更新该小组所有成员的是否已提交值
     *
     * @param group 小组号
     * @param submitted 是否已提交值
     * @return 操作影响的行数
     */
    public int updateSubmittedByGroup(int group, boolean submitted) {
        String sqlStatement = "UPDATE Member member SET member.submitted=:submitted"
                + " WHERE member.group=:group";
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement);
        query.addEntity(Member.class);
        query.setParameter("submitted", submitted);
        query.setParameter("group", group);
        return query.executeUpdate();
    }

    /**
     * 查询数据库中除了老师的所有成员
     *
     * @return 查询结果列表
     */
    public ArrayList<Member> queryAllStudents() {
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery("SELECT * FROM Member member WHERE member.identity=:identity");
        query.addEntity(Member.class);
        query.setParameter("identity", "Student");
        return (ArrayList<Member>) query.list();
    }

    /**
     * 查找数据库中特定小组的全部成员
     *
     * @param groupId 小组号
     * @return 查询结果列表
     */
    public ArrayList<Member> queryByGroup(int groupId) {
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery("SELECT * FROM Member member WHERE member.groupId=:groupId");
        query.addEntity(Member.class);
        query.setParameter("groupId", groupId);
        return (ArrayList<Member>) query.list();
    }

    /**
     * 查询数据库中所有成员
     *
     * @return 查询结果列表
     */
    public ArrayList<Member> queryAll() {
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery("SELECT * FROM Member");
        query.addEntity(Member.class);
        return (ArrayList<Member>) query.list();
    }
}
