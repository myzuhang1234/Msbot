package com.badeling.msbot.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.badeling.msbot.entity.Msg;


public interface MsgRepository extends CrudRepository<Msg, Long>{
	@Query(value = "select * from msg",nativeQuery=true)
	Set<Msg> findAllQuestion();
	
	@Modifying
	@Transactional
	@Query(value = "delete from msg where id = ?1",nativeQuery=true)
	void deleteQuestion(String id);
	
	@Query(value = "select * from msg where question like %?1%",nativeQuery=true)
	Set<Msg> findMsgByQuestion(String question);
	
	@Query(value = "select * from msg where id = ?1",nativeQuery=true)
	Msg findQuestion(String id);
	
	@Query(value = "select * from msg where question = ?1",nativeQuery=true)
	List<Msg> findMsgByExtQuestion(String question);
	
	@Modifying
	@Transactional
	@Query(value = "update msg set count = ?2 where id = ?1",nativeQuery=true)
	void modifyCount(Long id,Integer count);
}
