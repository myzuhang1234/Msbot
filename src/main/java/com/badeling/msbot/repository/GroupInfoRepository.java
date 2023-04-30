package com.badeling.msbot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.GroupInfo;

public interface GroupInfoRepository extends CrudRepository<GroupInfo, Long>{
	@Query(value = "select * from group_info where group_id = ?1",nativeQuery=true)
	GroupInfo findByGroupId(String group_id);
	
	
}
