package com.badeling.msbot.service;

import org.springframework.stereotype.Service;

import com.badeling.msbot.domain.ReceiveMsg;


@Service
public interface MvpImageService {

	String[] handImageMsg(ReceiveMsg receiveMsg);

	String saveImage(String raw_message);
	
	String saveTempImage(String raw_message) throws Exception;

	String saveFlagRaceImage(String today, String time);

	String[] handHigherImageMsg(ReceiveMsg receiveMsg);

	String getAuth(String ak, String sk);
}
