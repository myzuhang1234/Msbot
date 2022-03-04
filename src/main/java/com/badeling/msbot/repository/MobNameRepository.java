package com.badeling.msbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.badeling.msbot.entity.MobName;

public interface MobNameRepository extends CrudRepository<MobName, Long>{
	@Query(value = "select * from mob_name where name = ?1 order by mob_id",nativeQuery=true)
	List<MobName> findByName(String mobName);
	@Query(value = "select * from mob_name where name like %?1% order by mob_id",nativeQuery=true)
	List<MobName> findByNameLike(String mobNames);
	
	@Query(value = "select * from mob_name",nativeQuery=true)
	List<MobName> findAllmobName();
	
	@Modifying
	@Transactional
	@Query(value = "update mob_name set mob_id = ?2, name = ?3 WHERE (id = ?1);",nativeQuery=true)
	void modifyName(Long id,String mob_id,String name);
	
	@Query(value = "select * from mob_name where mob_id = ?1",nativeQuery=true)
	MobName findByMobId(Long mob_id);
	
	
	
}
