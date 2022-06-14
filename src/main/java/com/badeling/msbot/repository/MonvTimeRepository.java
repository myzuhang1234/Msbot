package com.badeling.msbot.repository;

import com.badeling.msbot.entity.RereadSentence;
import com.badeling.msbot.entity.RereadTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.badeling.msbot.entity.MonvTime;
import java.sql.Timestamp;
import java.util.List;

public interface MonvTimeRepository extends CrudRepository<MonvTime, Long>{

    @Query(value = "select * from monv_time where user_id = ?1 and date = ?2 and group_id=?3",nativeQuery=true)
    MonvTime findRoleBynumber(String user_id,String date,String group_id );

    @Query(value = "SELECT *,(SELECT SUM(prize_1+prize_2+prize_3+prize_4+prize_5) FROM monv_time mt2 WHERE mt1.id = mt2.id ) as gold" +
            " FROM monv_time mt1 WHERE (group_id  = ?1 AND TO_DAYS(NOW()) - TO_DAYS(mt1.`date`) <=0) ORDER BY gold DESC LIMIT 0,3 ",nativeQuery=true)
    List <MonvTime> find3thCostByGroup(String group_id);

    @Query(value = "SELECT * FROM monv_time mt WHERE user_id  =?1 AND group_id = ?2 ",nativeQuery=true)
    List <MonvTime> findCostByGroup(String user_id,String group_id);

    @Query(value = "SELECT * FROM monv_time WHERE (group_id =?1 AND TO_DAYS(NOW()) - TO_DAYS(`date`) <=0) " +
            "ORDER BY prize_5 DESC,prize_4 DESC,prize_3 DESC,prize_2 DESC,prize_1 DESC LIMIT 0,3",nativeQuery=true)
    List <MonvTime> find3thLuckByGroup(String group_id);


    @Modifying
    @Transactional
    @Query(value = "update monv_time set updated_at = ?2 where id = ?1",nativeQuery=true)
    void modifyUpdateTime(Long id, Timestamp updated_at);

    @Modifying
    @Transactional
    @Query(value = "update monv_time set prize_1 = ?2, prize_2 = ?3, prize_3 = ?4,prize_4 = ?5, prize_5 = ?6 where id = ?1",nativeQuery=true)
    void modifyUpdatePrize(Long id, Integer prize_1,Integer prize_2,Integer prize_3,Integer prize_4,Integer prize_5);

    @Query(value = "select distinct group_id FROM monv_time WHERE (TO_DAYS(NOW()) - TO_DAYS(`date`) <=0)",nativeQuery=true)
    List <String> findEveryGroup();
}
