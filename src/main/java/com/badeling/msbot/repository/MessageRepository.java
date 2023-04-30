package com.badeling.msbot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.badeling.msbot.entity.Message;

public interface MessageRepository extends CrudRepository<Message, Long>{
	@Query(value = "select group_id,count(id) from message group by group_id order by count(id) desc",nativeQuery=true)
	List<String[]> groupCount();
	
	@Query(value = "select group_id,count(id) from message where user_id = ?1 group by group_id order by count(id) desc",nativeQuery=true)
	List<String[]> userCount(String user_id);
	
	@Query(value = "select count(*) from message",nativeQuery=true)
	Integer getCount();
	@Query(value = "select * from message limit ?1,1",nativeQuery=true)
	Message getMsgByrank(int rank);
	
	@Modifying
	@Transactional
	@Query(value = "delete from message where id < ?1",nativeQuery=true)
	void deleteByrank(Long id);
	
	@Query(value = "select count(*) from message where user_id = ?1 and group_id = ?2 and raw_message not like '蠢猫%'",nativeQuery=true)
	Integer getCountByGroup(String user_id,String group_id);
	
	@Query(value = "select * from message where time < ?1",nativeQuery=true)
	List<Message> findMsgByTime(long time);
	
	@Query(value = "select * from message",nativeQuery=true)
	List<Message> findAllMsg();
}
