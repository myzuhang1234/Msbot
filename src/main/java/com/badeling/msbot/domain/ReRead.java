package com.badeling.msbot.domain;

import java.util.HashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "re-read")
public class ReRead {
	
	public static HashMap<String, ReReadMsg> map;

	public ReRead() {
		
	}

	public static HashMap<String, ReReadMsg> getMap() {
		return map;
	}

	public static void setMap(HashMap<String, ReReadMsg> map) {
		ReRead.map = map;
	}
	
	
	
}
