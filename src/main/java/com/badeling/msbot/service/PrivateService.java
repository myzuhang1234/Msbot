package com.badeling.msbot.service;

import org.springframework.stereotype.Service;

import com.badeling.msbot.domain.ReceiveMsg;
import com.badeling.msbot.domain.ReplyMsg;

@Service
public interface PrivateService {

	ReplyMsg handlePrivateMsg(ReceiveMsg receiveMsg);

}
