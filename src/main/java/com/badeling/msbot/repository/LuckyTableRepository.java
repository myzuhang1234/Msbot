package com.badeling.msbot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.LuckyTable;

public interface LuckyTableRepository extends CrudRepository<LuckyTable, Long>{
	
	@Query(value = "select count(*) from lucky_table where lucky_star = ?1",nativeQuery=true)
	int getCountByRank(String i);
	@Query(value = "select * from lucky_table where lucky_star = ?1 limit ?2,1",nativeQuery=true)
	LuckyTable findByRandom(String i, int n);

	
	

}
