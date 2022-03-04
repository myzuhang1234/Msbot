package com.badeling.msbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.badeling.msbot.entity.RereadTime;

public interface RereadTimeRepository extends CrudRepository<RereadTime, Long>{
	
	@Query(value = "select * from reread_time where group_id = ?1 and user_id = ?2 limit 0,1",nativeQuery=true)
	RereadTime findByGroupAndId(String group_id, String user_id);
	
	@Modifying
	@Transactional
	@Query(value = "update reread_time set count = ?2 where id = ?1",nativeQuery=true)
	void modifyReread(Long id, int count);
	
	@Query(value = "select * from reread_time where group_id = ?1 order by count desc limit 0,3",nativeQuery=true)
	List<RereadTime> find3thByGroup(String group_id);
}
