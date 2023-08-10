package com.badeling.msbot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.badeling.msbot.entity.RoleAtt;

public interface RoleAttRepository extends CrudRepository<RoleAtt, Long>{
    @Query(value = "select * from role_att where user_id = ?1",nativeQuery=true)
    RoleAtt findRoleBynumber(String user_id);

    @Modifying
    @Transactional
    @Query(value = "update role_att set att = ?2,att_per = ?3,max_att = ?4 where id = ?1",nativeQuery=true)
    void modifyAtt(Long id, Integer att,Integer attPer,Long maxAtt);

}