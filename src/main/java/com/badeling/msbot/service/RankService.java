package com.badeling.msbot.service;

import org.springframework.stereotype.Service;

@Service
public interface RankService {

	String getRank(String raw_message);

}
