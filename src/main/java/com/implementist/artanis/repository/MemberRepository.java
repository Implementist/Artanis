package com.implementist.artanis.repository;

import com.implementist.artanis.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Implementist
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 以EmailAddress为键更新工作日志内容
     *
     * @param content      工作日志内容
     * @param emailAddress 邮箱地址
     * @return 受影响的行数
     */
    @Modifying
    @Query(value = "UPDATE Member m SET m.content=:content WHERE m.emailAddress=:emailAddress")
    int updateContentByEmailAddress(@Param("content") String content, @Param("emailAddress") String emailAddress);

    /**
     * 以小组号为键更新已提交值
     *
     * @param groups    指定的小组列表
     * @param submitted 已提交值
     * @return 受影响的行数
     */
    @Modifying
    @Query(value = "UPDATE Member m SET m.submitted=:submitted WHERE m.groupId IN (:groups)")
    int updateSubmittedByGroups(@Param("groups") List<Integer> groups, @Param("submitted") boolean submitted);

    /**
     * 更新所有成员的日报内容和已提交值
     *
     * @param content   要修改的日报内容
     * @param submitted 已提交值
     * @return 受影响的行数
     */
    @Modifying
    @Query(value = "UPDATE Member m SET m.content=:content, m.submitted=:submitted")
    int updateAllContentAndSubmitted(@Param("content") String content, @Param("submitted") Boolean submitted);

    /**
     * 以姓名为键更新日志内容
     *
     * @param content 要更新的日志内容
     * @param name    姓名
     * @return 受影响的行数
     */
    @Modifying
    @Query(value = "UPDATE Member m SET m.content=:content WHERE m.name=:name")
    int updateContentByName(@Param("content") String content, @Param("name") String name);


    /**
     * 查询指定小组(已提交/未提交)成员的邮箱列表
     *
     * @param groups    指定小组编号列表
     * @param submitted 是否已提交
     * @return 特定小组的成员邮箱列表
     */
    @Query(value = "SELECT m.emailAddress FROM Member m WHERE m.groupId IN (:groups) AND m.submitted=:submitted")
    List<String> queryEmailAddressByGroups(@Param("groups") List<Integer> groups,
                                           @Param("submitted") boolean submitted);

    /**
     * 查询指定小组成员的邮箱列表
     *
     * @param groups 指定小组编号列表
     * @return 特定小组的成员邮箱列表
     */
    @Query(value = "SELECT m.emailAddress FROM Member m WHERE m.groupId IN (:groups)")
    List<String> queryEmailAddressByGroups(@Param("groups") List<Integer> groups);

    /**
     * 以小组号为键查询成员
     *
     * @param groupId 小组号
     * @return 小组成员列表
     */
    List<Member> queryByGroupId(int groupId);
}
