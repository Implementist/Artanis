package com.implementist.artanis.repository;

import com.implementist.artanis.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Implementist
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    /**
     * 以id为键查询小组
     *
     * @param id 小组id
     * @return 小组
     */
    Group queryById(int id);
}
