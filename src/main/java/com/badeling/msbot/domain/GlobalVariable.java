package com.badeling.msbot.domain;

import java.util.TreeSet;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="service.properties")
public class GlobalVariable {
	
	//消息记录
	public static TreeSet<String> msgList;
	
	public GlobalVariable() {
		
	}
		
	public static TreeSet<String> getMsgList() {
		return msgList;
	}

	public static void setMsgList(TreeSet<String> msgList) {
		GlobalVariable.msgList = msgList;
	}
}
