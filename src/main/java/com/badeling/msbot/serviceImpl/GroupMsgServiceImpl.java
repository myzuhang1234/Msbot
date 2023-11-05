package com.badeling.msbot.serviceImpl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.domain.ChannelReplyMsg;
import com.badeling.msbot.domain.GroupMsg;
import com.badeling.msbot.domain.PrivateMsg;
import com.badeling.msbot.domain.Result;
import com.badeling.msbot.service.GroupMsgService;

@Component
public class GroupMsgServiceImpl implements GroupMsgService{

	@Autowired
    RestTemplate restTemplate;
	
//  @LoadBalanced
	@Bean
	@Autowired
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
	
	@Override
	public Result<?> sendPrivateMsg(PrivateMsg privateMsg) {
		if(privateMsg!=null&&privateMsg.getMessage().contains("[CQ:image,file=")&&!privateMsg.getMessage().contains("url=http")&&!privateMsg.getMessage().contains(MsbotConst.imageUrl)) {
			String temp = "W31HAS321BA68C3XR8YS1ZX86T138V503Z2XCVR11F64";
			String reply = privateMsg.getMessage();
			while(reply.contains("[CQ:image,file=")) {
				reply = reply.replace("[CQ:image,file=", temp);
			}
			reply = reply.replaceAll(temp, "[CQ:image,file="+MsbotConst.imageUrl);
			reply = reply.replaceAll("\\\\", "/");
			privateMsg.setMessage(reply);
		}
		System.out.println(privateMsg.toString());
		Result<?> result = restTemplate.postForObject("http://127.0.0.1:5700/send_private_msg", privateMsg, Result.class);
		return result;
	}
	
	@Override
	public Result<?> sendGroupMsg(GroupMsg groupMsg) {
		groupMsg.setMessage(groupMsg.getMessage().replaceAll("\\\\", "/"));
		Result<?> result = restTemplate.postForObject("http://127.0.0.1:5700/send_group_msg", groupMsg, Result.class);
		System.err.println(result.toString());
		return result;
	}
	
	@Override
	public Result<?> sendChannelMsg(ChannelReplyMsg channelReplyMsg) {
		channelReplyMsg.setMessage(channelReplyMsg.getMessage().replaceAll("\\\\", "/"));
		Result<?> result = restTemplate.postForObject("http://127.0.0.1:5700/send_guild_channel_msg", channelReplyMsg, Result.class);
        return result;
	}
	
	@Override
	public Result<?> getGroupList() {
		Result<?> result = restTemplate.postForObject("http://127.0.0.1:5700/get_group_list", null, Result.class);
        return result;
	}
	
	@Override
	public Result<?> getGroupMember(GroupMsg groupMsg) {
		Result<?> result = restTemplate.postForObject("http://127.0.0.1:5700/get_group_member_list", groupMsg, Result.class);
        return result;
	}

	@Override
	public Result<?> deleteMsg(HashMap<String, Integer> map) {
		Result<?> result = restTemplate.postForObject("http://127.0.0.1:5700/delete_msg", map, Result.class);
        return result;
	}

}
