package com.badeling.msbot.controller;

import com.alibaba.fastjson.JSON;
import com.badeling.msbot.domain.GroupMsg;
import com.badeling.msbot.domain.ReplyMsg;
import com.badeling.msbot.service.GroupMsgService;
import com.badeling.msbot.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/msg")
public class MsbotController {

	@Autowired
	MsgService msgService;

	@Autowired
	GroupMsgService groupMsgService;

	@RequestMapping("/receive")
	@ResponseBody
	public String msgReceive(HttpServletRequest request) {
		String msg = null;
		try {
			msg = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (msg == null) {
			return null;
		}
		//解析收到的消息
//        ReceiveMsg receiveMsg = null;
		System.out.println(msg);
		ReplyMsg receive = msgService.receive(msg);
		//消息处理结束后
		if(receive==null) {
			return null;
		}

		if(receive!=null&&receive.getReply()!=null) {
			System.out.println(receive.toString());
			receive.setReply(receive.getReply().replaceAll("\\\\", "/"));
		}

		return JSON.toJSONString(receive);
	}

	//对接b站挂小心心的功能 可以忽略
	@RequestMapping("/server_bili")
	@ResponseBody
	public String msgServer(@RequestParam("title")String title,@RequestParam("desp")String desp) {
		GroupMsg groupMsg = new GroupMsg();
		groupMsg.setGroup_id(Long.parseLong("348273823"));
		String[] split = desp.split("用户:");
		for(String temp : split) {
			if(temp.isEmpty()) {
				continue;
			}else {
				while(temp.endsWith("\r")||temp.endsWith("\n")) {
					temp = temp.substring(0,temp.length()-2);
				}
				groupMsg.setMessage(title+"\r\n"+"用户:"+temp);
				groupMsgService.sendGroupMsg(groupMsg);
				try {
					Thread.sleep(1221);
				} catch (Exception e) {

				}
			}
		}
		return null;
	}


}