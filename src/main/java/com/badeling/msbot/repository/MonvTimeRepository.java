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

}
