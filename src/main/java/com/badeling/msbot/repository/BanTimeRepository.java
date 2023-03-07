package com.badeling.msbot.repository;

import com.badeling.msbot.entity.BanTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

public interface BanTimeRepository extends CrudRepository<BanTime, Long> {
    @Query(value = "select * from ban_time where user_id = ?1 and date = ?2 and group_id=?3",nativeQuery=true)
    BanTime findRoleBynumber(String user_id, String date, String group_id );

    @Query(value = "SELECT * FROM ban_time WHERE user_id  =?1 AND group_id = ?2 ",nativeQuery=true)
    List<BanTime> findBanTimesByGroup(String user_id, String group_id);

    @Modifying
    @Transactional
    @Query(value = "update ban_time set updated_at = ?2 where id = ?1",nativeQuery=true)
    void modifyUpdateTime(Long id, Timestamp updated_at);

    @Modifying
    @Transactional
    @Query(value = "update ban_time set ban_times = ?2,updated_at = ?3 where id = ?1",nativeQuery=true)
    void modifyUpdateBanTimes(Long id, Integer ban_times,Timestamp updated_at);

    @Query(value = "select distinct group_id FROM ban_times WHERE (TO_DAYS(NOW()) - TO_DAYS(`date`) <=0)",nativeQuery=true)
    List <String> findEveryGroup();
}
