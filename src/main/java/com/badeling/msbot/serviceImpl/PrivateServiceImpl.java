package com.badeling.msbot.serviceImpl;

import org.springframework.stereotype.Component;

import com.badeling.msbot.domain.ReceiveMsg;
import com.badeling.msbot.domain.ReplyMsg;
import com.badeling.msbot.service.PrivateService;

@Component
public class PrivateServiceImpl implements PrivateService{


	@Override
	public ReplyMsg handlePrivateMsg(ReceiveMsg receiveMsg) {
		//接受私聊消息 但是很久以前就没用了
		return null;
	}
	
}
