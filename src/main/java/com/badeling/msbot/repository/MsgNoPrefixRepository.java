package com.badeling.msbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.MsgNoPrefix;

public interface MsgNoPrefixRepository extends CrudRepository<MsgNoPrefix, Long>{
	@Query(value = "select * from msg_no_prefix order by id desc",nativeQuery=true)
	List<MsgNoPrefix> findMsgNPList();

}
