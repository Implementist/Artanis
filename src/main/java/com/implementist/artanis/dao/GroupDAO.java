package com.implementist.artanis.dao;

import com.implementist.artanis.entity.Group;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 *
 * @author Implementist
 */
@Repository
public class GroupDAO {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * 查询数据库中所有小组
     *
     * @return 小组列表
     */
    public ArrayList<Group> queryAll() {
        Query<Group> query = sessionFactory.getCurrentSession().createQuery("FROM Group g");
        return (ArrayList<Group>) query.getResultList();
    }

    /**
     * 按小组号查询小组
     *
     * @param groupId 小组号
     * @return 小组对象
     */
    public Group queryGroupById(int groupId) {
        Query<Group> query = sessionFactory.getCurrentSession().createQuery("FROM Group g WHERE g.id=:id");
        query.setParameter("id", groupId);
        return query.uniqueResult();
    }
}
