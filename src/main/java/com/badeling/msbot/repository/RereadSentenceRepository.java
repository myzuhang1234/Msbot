package com.badeling.msbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.RereadSentence;

public interface RereadSentenceRepository extends CrudRepository<RereadSentence, Long>{
	@Query(value = "select * from reread_sentence where group_id = ?1 limit 0,1",nativeQuery=true)
	RereadSentence findMaxByGroup(String group_id);
	
	@Query(value = "select distinct group_id from reread_sentence",nativeQuery=true)
	List<String> findMaxEveryGroup();

}
