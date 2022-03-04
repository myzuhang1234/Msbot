package com.badeling.msbot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.RankInfo;

public interface RankInfoRepository extends CrudRepository<RankInfo, Long>{
	@Query(value = "select * from rank_info where user_id = ?1",nativeQuery=true)
	RankInfo getInfoByUserId(String user_id);
}
