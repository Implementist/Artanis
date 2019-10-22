package com.implementist.artanis.repository;

import com.implementist.artanis.entity.Member;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberRepositoryTests {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Rollback
    public void testUpdateContentByEmailAddress(){
        String content = "Hahaha";
        String emailAddress = "541263884@qq.com";
        int returnCode = memberRepository.updateContentByEmailAddress(content, emailAddress);
        Assert.assertEquals(1, returnCode);
    }

    @Test
    @Rollback
    public void testUpdateSubmittedByGroups(){
        List<Integer> groups = new ArrayList<>();
        groups.add(0);
        int returnCode = memberRepository.updateSubmittedByGroups(groups, false);
        Assert.assertEquals(2, returnCode);

        groups.clear();
        groups.add(1);
        returnCode = memberRepository.updateSubmittedByGroups(groups, false);
        Assert.assertEquals(0, returnCode);
    }

    @Test
    @Rollback
    public void testUpdateAllContentAndSubmitted(){
        int returnCode = memberRepository.updateAllContentAndSubmitted("", false);
        Assert.assertEquals(2, returnCode);
    }

    @Test
    @Rollback
    public void testUpdateContentByName(){
        int returnCode = memberRepository.updateContentByName("休假中", "曹帅");
        Assert.assertEquals(1, returnCode);
        returnCode = memberRepository.updateContentByName("休假中", "老李");
        Assert.assertEquals(0, returnCode);
    }

    @Test
    public void testQueryEmailAddressByGroups(){
        List<Integer> groups = new ArrayList<>();
        groups.add(-1);
        List<String> results = memberRepository.queryEmailAddressByGroups(groups);
        Assert.assertEquals(0, results.size());

        groups.add(0);
        results = memberRepository.queryEmailAddressByGroups(groups);
        Assert.assertEquals(2, results.size());

        results = memberRepository.queryEmailAddressByGroups(groups, true);
        Assert.assertEquals(1, results.size());

        results = memberRepository.queryEmailAddressByGroups(groups, false);
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void testQueryByGroupId(){
        List<Member> members = memberRepository.queryByGroupId(-1);
        Assert.assertEquals(0, members.size());

        members = memberRepository.queryByGroupId(0);
        Assert.assertEquals(2, members.size());
    }
}
