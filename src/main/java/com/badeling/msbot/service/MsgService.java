package com.badeling.msbot.service;


import org.springframework.stereotype.Service;

import com.badeling.msbot.domain.ReplyMsg;

@Service
public interface MsgService {

	ReplyMsg receive(String msg);
	
}
