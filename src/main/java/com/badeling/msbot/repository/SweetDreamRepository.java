package com.badeling.msbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.SweetDream;

public interface SweetDreamRepository extends CrudRepository<SweetDream, Long>{
	
	@Query(value = "select * from sweet_dream where type = ?1",nativeQuery=true)
	List<SweetDream> findByType(String type);
	
	
	
	

}
