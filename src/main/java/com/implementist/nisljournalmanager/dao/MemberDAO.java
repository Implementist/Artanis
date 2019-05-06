/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.dao;

import com.implementist.nisljournalmanager.domain.Member;
import java.util.List;
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
        String sqlStatement = "UPDATE Member m SET m.content=:content,"
                + "m.submitted=:submitted WHERE m.emailAddress=:emailAddress";
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
        String sqlStatement = "UPDATE Member m SET m.content=:content,"
                + "m.submitted=:submitted WHERE m.name=:name";
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
        String sqlStatement = "UPDATE Member m SET m.content=:content,"
                + "m.submitted=:submitted WHERE m.identity=:identity";
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
     * @param groupId 小组号
     * @param submitted 是否已提交值
     * @return 操作影响的行数
     */
    public int updateSubmittedByGroup(int groupId, boolean submitted) {
        String sqlStatement = "UPDATE Member m SET m.submitted=:submitted WHERE"
                + " m.groupId=:groupId";
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement);
        query.setParameter("submitted", submitted);
        query.setParameter("groupId", groupId);
        return query.executeUpdate();
    }

    /**
     * 根据小组号列表更新这些小组所有成员的是否已提交值
     *
     * @param groups 小组号列表
     * @param submitted 是否已提交值
     * @return 操作影响的行数
     */
    public int updateSubmittedByGroups(List<Integer> groups, boolean submitted) {
        String sqlStatement = "UPDATE Member m SET m.submitted=:submitted WHERE m.groupId IN (:groups)";
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement);
        query.setParameter("submitted", submitted);
        query.setParameterList("groups", groups);
        return query.executeUpdate();
    }

    /**
     * 查询数据库中除了老师的所有成员
     *
     * @return 查询结果列表
     */
    public List<Member> queryAllStudents() {
        String sqlStatement = "SELECT * FROM Member m WHERE m.identity=:identity";
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement);
        query.addEntity(Member.class);
        query.setParameter("identity", "Student");
        return query.list();
    }

    /**
     * 查找数据库中特定小组的全部成员
     *
     * @param groupId 小组号
     * @return 查询结果列表
     */
    public List<Member> queryByGroup(int groupId) {
        String sqlStatement = "SELECT * FROM Member m WHERE m.groupId=:groupId";
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement);
        query.addEntity(Member.class);
        query.setParameter("groupId", groupId);
        return query.list();
    }

    /**
     * 查找数据库中特定几个小组的全部成员
     *
     * @param groups 小组号列表
     * @return 查询结果列表
     */
    public List<Member> queryByGroups(List<Integer> groups) {
        String sqlStatement = "SELECT * FROM Member m WHERE m.groupId IN (:groups)";
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement);
        query.addEntity(Member.class);
        query.setParameterList("groups", groups);
        return query.list();
    }

    /**
     * 查找几个特定小组全部成员的邮箱地址
     *
     * @param groups 小组号列表
     * @return 查询结果列表
     */
    public List<String> queryEmailAddressByGroups(List<Integer> groups) {
        String sqlStatement = "SELECT m.emailAddress FROM Member m WHERE m.groupId IN (:groups)";
        NativeQuery<String> query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement);
        query.setParameterList("groups", groups);
        return query.list();
    }

    /**
     * 查找几个特定小组全部成员的邮箱地址
     *
     * @param groups 小组号列表
     * @param submitted 是否已提交值
     * @return 查询结果列表
     */
    public List<String> queryEmailAddressByGroups(List<Integer> groups, boolean submitted) {
        String sqlStatement = "SELECT m.emailAddress FROM Member m WHERE m.groupId IN (:groups) AND m.submitted=:submitted";
        NativeQuery<String> query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement);
        query.setParameterList("groups", groups);
        query.setParameter("submitted", submitted);
        return query.list();
    }

    /**
     * 查询数据库中所有成员
     *
     * @return 查询结果列表
     */
    public List<Member> queryAll() {
        NativeQuery<Member> query = sessionFactory.getCurrentSession().createNativeQuery("SELECT * FROM Member");
        query.addEntity(Member.class);
        return query.list();
    }
}
