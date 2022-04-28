package com.badeling.msbot.serviceImpl;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.domain.ChannelReplyMsg;
import com.badeling.msbot.domain.GroupMsg;
import com.badeling.msbot.domain.NoticeMsg;
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
	public Result<?> getGroupMember(GroupMsg groupMsg) {
		Result<?> result = restTemplate.postForObject("http://127.0.0.1:5700/get_group_member_list", groupMsg, Result.class);
        return result;
	}

	@Override
	public Result<?> deleteMsg(HashMap<String, Integer> map) {
		Result<?> result = restTemplate.postForObject("http://127.0.0.1:5700/delete_msg", map, Result.class);
        return result;
	}
	
	@Override
	public String tuLingMsg(String json) {
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		String result = restTemplate.postForObject("http://openapi.tuling123.com/openapi/api/v2", json, String.class);
        return result;
	}
	
	@Override
	public String MoliMsg(String content,String user_id,String name) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Api-Key", MsbotConst.moliKey);
		headers.add("Api-Secret", MsbotConst.moliSecret);

		Map<String,Object> body = new HashMap<>();
		// 发送的内容
		body.put("content", content);
		// 消息类型，1：私聊，2：群聊
		body.put("type", 2);
		body.put("from", user_id);
		body.put("fromName", name);
		body.put("to", user_id);
		String json = JSONObject.toJSONString(body);
		HttpEntity<String> formEntity = new HttpEntity<String>(json.toString(), headers);
		JSONObject jsonObject = restTemplate.postForEntity("https://i.mly.app/reply", formEntity, JSONObject.class).getBody();
//		(Map<String, Object>)JSON.parseObject(msg);
		return jsonObject.toString().replaceAll("\\\\n", "\r\n");
	}
	
	@Override
	public String MoliMsg2(String content,String user_id,String name) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Api-Key", MsbotConst.moliKey2);
		headers.add("Api-Secret", MsbotConst.moliSecret2);

		Map<String,Object> body = new HashMap<>();
		// 发送的内容
		body.put("content", content);
		// 消息类型，1：私聊，2：群聊
		body.put("type", 2);
		body.put("from", user_id);
		body.put("fromName", name);
		body.put("to", user_id);
		String json = JSONObject.toJSONString(body);
		HttpEntity<String> formEntity = new HttpEntity<String>(json.toString(), headers);
		JSONObject jsonObject = restTemplate.postForEntity("https://i.mly.app/reply", formEntity, JSONObject.class).getBody();
//		(Map<String, Object>)JSON.parseObject(msg);
		return jsonObject.toString().replaceAll("\\\\n", "\r\n");
	}
}
