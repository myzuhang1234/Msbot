package com.badeling.msbot.repository;

import com.badeling.msbot.entity.GroupMember;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GroupMemberRepository extends CrudRepository<GroupMember, Long>{
    @Query(value = "select * from group_member where group_id = ?1",nativeQuery=true)
    List<GroupMember> findGroupMemberByGroup(String group_id);

    @Query(value = "select * from group_member where group_id = ?1 and user_id = ?2",nativeQuery=true)
    List<GroupMember> findGroupMemberInfo(String group_id,String user_id);

    @Query(value = "select * from group_member where group_id = ?1 and leave_time >= ?2",nativeQuery=true)
    List<GroupMember> findLeaveMemberInfo(String group_id,Integer leave_time);

    @Modifying
    @Transactional
    @Query(value = "update group_member set nickname = ?2,card = ?3,join_time = ?4 where id = ?1",nativeQuery=true)
    void modifyMemberInfo(Long id,String nickname,String card,String join_time);

    @Modifying
    @Transactional
    @Query(value = "update group_member set leave_time = ?2 where id = ?1",nativeQuery=true)
    void modifyMemberLeaveTime(Long id,String leaveTime);
}