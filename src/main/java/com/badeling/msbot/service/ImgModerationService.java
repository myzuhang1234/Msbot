package com.badeling.msbot.service;

import org.springframework.stereotype.Service;

import com.badeling.msbot.domain.ReceiveMsg;

@Service
public interface ImgModerationService {
	void imgModeration(ReceiveMsg receiveMsg);

	String getAuth(String imgApikey, String imgSecretkey);
}
