package com.badeling.msbot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.badeling.msbot.entity.MonvTime;
import java.sql.Timestamp;

public interface MonvTimeRepository extends CrudRepository<MonvTime, Long>{
    @Query(value = "select * from monv_time where user_id = ?1",nativeQuery=true)
    MonvTime findRoleBynumber(String user_id);

    @Modifying
    @Transactional
    @Query(value = "update monv_time set updated_at = ?2 where id = ?1",nativeQuery=true)
    void modifyUpdateTime(Long id, Timestamp updated_at);

    @Modifying
    @Transactional
    @Query(value = "update monv_time set prize_1 = ?2, prize_2 = ?3, prize_3 = ?4,prize_4 = ?5, prize_5 = ?6 where id = ?1",nativeQuery=true)
    void modifyUpdatePrize(Long id, Integer prize_1,Integer prize_2,Integer prize_3,Integer prize_4,Integer prize_5);

}
