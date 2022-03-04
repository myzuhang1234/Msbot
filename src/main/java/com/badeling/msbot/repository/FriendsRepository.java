package com.badeling.msbot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.Friends;

public interface FriendsRepository extends CrudRepository<Friends, String>{
	
	
	@Query(value = "select * from friends where id = ?1",nativeQuery=true)
	Friends findFriendsById(String id);
	
	@Query(value = "select count(star) FROM coolq.friends where star=?1 and id<2000",nativeQuery=true)
	String getFriendsNum(String string);
	
	@Query(value = "select count(star) FROM coolq.friends where star=?1 and id>=2000",nativeQuery=true)
	String getPhotoNum(String string);
	
	@Query(value = "select * FROM friends where star=?1 limit ?2,1",nativeQuery=true)
	Friends findFriendsByStar(String string,int ram);
	
	@Query(value = "select * FROM friends where star=?1 and id>=2000 limit ?2,1",nativeQuery=true)
	Friends findPhotoByStar(String string,int ram);
	
	
}
