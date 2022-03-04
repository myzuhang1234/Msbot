package com.badeling.msbot.controller;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;
import com.badeling.msbot.domain.ReplyMsg;
import com.badeling.msbot.service.GroupMsgService;
import com.badeling.msbot.service.MsgService;

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
	
	
	
	
}
