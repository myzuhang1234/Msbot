package com.badeling.msbot.controller;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.domain.ReplyMsg;
import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.util.Proxys;

@Component
public class ChatGpt {
	
	public static ReplyMsg getGpt(String raw_message,String prompt){
		
			if(MsbotConst.gpt_apiKey==null||MsbotConst.gpt_apiKey.isEmpty()) {
				return null;
			}		
			
			while(raw_message.contains("[CQ:")) {
				raw_message = raw_message.substring(raw_message.indexOf("[CQ:"),raw_message.indexOf("]"));
			}
			
	        ChatGPT chatGPT = null;
		    
	        if(MsbotConst.proxy_ip!=null&&MsbotConst.proxy_port!=null) {
	        	chatGPT = ChatGPT.builder()
		                .apiKey(MsbotConst.gpt_apiKey)
		                .timeout(900)
		                .proxy(Proxys.http(MsbotConst.proxy_ip, MsbotConst.proxy_port))
		                .apiHost("https://api.openai.com/") //反向代理地址
		                .build()
		                .init();
	        }else {
	        	chatGPT = ChatGPT.builder()
		                .apiKey(MsbotConst.gpt_apiKey)
		                .timeout(900)
		                .apiHost("https://api.openai.com/") //反向代理地址
		                .build()
		                .init();
	        }
	        
	        Message system = Message.ofSystem(prompt);
	        Message message = Message.of(raw_message);
	
	        ChatCompletion chatCompletion = ChatCompletion.builder()
	                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
	                .messages(Arrays.asList(system, message))
	                .maxTokens(3000)
	                .temperature(0.9)
	                .build();
	        
	        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
	        Message res = response.getChoices().get(0).getMessage();
	        System.out.println(res);
	        String reply = res.getContent();
	        
	        if(reply.indexOf(":")>=0&&reply.indexOf(":")<4) {
	        	reply = reply.substring(reply.indexOf(":")+1);
	        }
	        if(reply.indexOf("：")>=0&&reply.indexOf("：")<4) {
	        	reply = reply.substring(reply.indexOf("：")+1);
	        }
	        if(reply.startsWith("\"")) {
	        	reply = reply.substring(1,reply.length());
	        }
	        if(reply.endsWith("\"")) {
	        	reply = reply.substring(0,reply.length()-1);
	        }
	        if(reply.startsWith(" ")) {
	        	reply = reply.substring(1,reply.length());
	        }
	        ReplyMsg replyMsg = new ReplyMsg();
	        replyMsg.setReply(reply);
	        return replyMsg;
	}
}
