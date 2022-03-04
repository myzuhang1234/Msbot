package com.badeling.msbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.LuckyMap;

public interface LuckyMapRepository extends CrudRepository<LuckyMap, Long>{
	@Query(value = "select count(*) from lucky_map",nativeQuery=true)
	int getCount();
	@Query(value = "select * from lucky_map limit ?1,1",nativeQuery=true)
	LuckyMap findByRandom(int m);
	
	@Query(value = "select * from lucky_map",nativeQuery=true)
	List<LuckyMap> findAllMap();
	
}
