package com.badeling.msbot.domain;

import java.util.Map;
import java.util.TreeSet;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="service.properties")
public class GlobalVariable {
	
	//消息记录
	public static TreeSet<String> msgList;
	//新成员
	public static Map<String,Long> newFriendsMap;
	//魔女抽奖CD
	public static Map<String,Long> witchForestMap;
	
	public GlobalVariable() {
		
	}
		
	public static TreeSet<String> getMsgList() {
		return msgList;
	}

	public static void setMsgList(TreeSet<String> msgList) {
		GlobalVariable.msgList = msgList;
	}

	public static Map<String, Long> getNewFriendsMap() {
		return newFriendsMap;
	}

	public static void setNewFriendsMap(Map<String, Long> newFriendsMap) {
		GlobalVariable.newFriendsMap = newFriendsMap;
	}

	public static Map<String, Long> getWitchForestMap() {
		return witchForestMap;
	}
	public static void setWitchForestMap(Map<String, Long> witchForestMap) {
		GlobalVariable.witchForestMap = witchForestMap;
	}
	
	

}
