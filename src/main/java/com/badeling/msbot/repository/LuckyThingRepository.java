package com.badeling.msbot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.LuckyThing;

public interface LuckyThingRepository extends CrudRepository<LuckyThing, Long>{
	
	@Query(value = "select count(*) from lucky_thing",nativeQuery=true)
	int getCount();
	
	@Query(value = "select * from lucky_thing limit ?1,1",nativeQuery=true)
	LuckyThing findByRandom(int l);

}
