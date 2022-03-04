package com.badeling.msbot.service;

import org.springframework.stereotype.Service;

@Service
public interface ChannelService {

	void receive(String msg);

}
