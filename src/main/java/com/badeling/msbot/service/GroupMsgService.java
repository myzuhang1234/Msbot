package com.badeling.msbot.service;

import com.badeling.msbot.domain.ChannelReplyMsg;
import com.badeling.msbot.domain.GroupMsg;
import com.badeling.msbot.domain.PrivateMsg;
import com.badeling.msbot.domain.Result;
import org.springframework.stereotype.Service;

import java.util.HashMap;



@Service
public interface GroupMsgService {

	Result<?> sendPrivateMsg(PrivateMsg privateMsg);

	Result<?> sendGroupMsg(GroupMsg groupMsg);

	Result<?> sendChannelMsg(ChannelReplyMsg channelReplyMsg);

	Result<?> getGroupList();

	Result<?> getGroupMember(GroupMsg groupMsg);

	Result<?> deleteMsg(HashMap<String, Integer> map);

	String tuLingMsg(String str);

	String MoliMsg(String content, String user_id, String name);

	String MoliMsg2(String content, String user_id, String name);

}