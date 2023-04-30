package com.badeling.msbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.Score;

public interface ScoreRepository extends CrudRepository<Score, Long>{
	@Query(value = "select * from score where user_id = ?1 and group_id = ?2 and time = ?3",nativeQuery=true)
	List<Score> findScoreById(String user_id,String group_id,int time);
	
	@Query(value = "select * from score where user_id = ?1 and group_id = ?2",nativeQuery=true)
	List<Score> findScoreById(String user_id,String group_id);
	
	@Query(value = "select * from score where user_id = ?1",nativeQuery=true)
	List<Score> findScoreById(String user_id);
	
	@Query(value = "select group_id,sum(score) from score where user_id = ?1 group by group_id",nativeQuery=true)
	List<Object[]> getUserScoreById(String findNumber);

	@Query(value = "select group_id,sum(score) from score group by group_id",nativeQuery=true)
	List<Object[]> getAllGroupScore();
	
	@Query(value = "select group_id,user_id,sum(score) from score where time <= ?1 or time >= ?2 group by group_id,user_id",nativeQuery=true)
	List<Object[]> findLastWeekScore(String time1,String time2);
	
	@Query(value = "select * from score where time <= ?1 or time >= ?2",nativeQuery=true)
	List<Score> findLastWeekScore2(String time1,String time2);
	
	@Query(value = "select user_id,sum(score) from score where group_id = ?1 group by user_id order by sum(score) desc",nativeQuery=true)
	List<Object[]> getRankingList(String group_id);
	
	@Query(value = "select sum(score) from msbot.score where user_id = ?1 and group_id = ?2",nativeQuery=true)
	Integer getUserTotalScore(String user_id,String group_id);
	
}
