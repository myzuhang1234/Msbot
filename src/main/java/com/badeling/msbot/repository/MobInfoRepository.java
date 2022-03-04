package com.badeling.msbot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.badeling.msbot.entity.MobInfo;

public interface MobInfoRepository extends CrudRepository<MobInfo, Long>{
	@Query(value = "select * from mob_info where mob_id = ?1",nativeQuery=true)
	MobInfo findMobInfoByMobId(String mobId);
	
	@Modifying
	@Transactional
	@Query(value = "update mob_info set boss = ?2 WHERE (mob_id = ?1);",nativeQuery=true)
	void modifyBoss(String mob_id,String boss);
	
	@Modifying
	@Transactional
	@Query(value = "update mob_info set link = ?2 WHERE (mob_id = ?1);",nativeQuery=true)
	void modifyLink(String mob_id,String link);
}
