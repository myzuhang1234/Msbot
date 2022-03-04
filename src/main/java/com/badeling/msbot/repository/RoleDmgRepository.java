package com.badeling.msbot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.badeling.msbot.entity.RoleDmg;

public interface RoleDmgRepository extends CrudRepository<RoleDmg, Long>{
	@Query(value = "select * from role_dmg where user_id = ?1",nativeQuery=true)
	RoleDmg findRoleBynumber(String user_id);
	
	@Modifying
	@Transactional
	@Query(value = "update role_dmg set common_dmg = ?2,boss_dmg = ?3 where id = ?1",nativeQuery=true)
	void modifyDmg(Long id, Integer commonDmg,Integer bossDmg);
	
}
