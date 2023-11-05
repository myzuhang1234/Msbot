package com.badeling.msbot.repository;

import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.MsgDeleted;

public interface MsgDeletedRepository extends CrudRepository<MsgDeleted, Long>{
	
}
