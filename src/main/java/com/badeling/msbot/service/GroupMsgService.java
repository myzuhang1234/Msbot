package com.badeling.msbot.service;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.badeling.msbot.domain.ChannelReplyMsg;
import com.badeling.msbot.domain.GroupMsg;
import com.badeling.msbot.domain.PrivateMsg;
import com.badeling.msbot.domain.Result;



@Service
public interface GroupMsgService {
	
	Result<?> sendPrivateMsg(PrivateMsg privateMsg);

	Result<?> sendGroupMsg(GroupMsg groupMsg);
	
	Result<?> sendChannelMsg(ChannelReplyMsg channelReplyMsg);
	
	Result<?> getGroupList();
	
	Result<?> getGroupMember(GroupMsg groupMsg);

	Result<?> deleteMsg(HashMap<String, Integer> map);
	
}
