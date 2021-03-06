package com.badeling.msbot.serviceImpl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.controller.MsgZbCalculate;
import com.badeling.msbot.domain.GlobalVariable;
import com.badeling.msbot.domain.GroupMsg;
import com.badeling.msbot.domain.NoticeMsg;
import com.badeling.msbot.domain.ReRead;
import com.badeling.msbot.domain.ReReadMsg;
import com.badeling.msbot.domain.ReceiveMsg;
import com.badeling.msbot.domain.ReplyMsg;
import com.badeling.msbot.domain.Result;
import com.badeling.msbot.entity.Msg;
import com.badeling.msbot.entity.MsgNoPrefix;
import com.badeling.msbot.entity.QuizOzAnswer;
import com.badeling.msbot.entity.QuizOzQuestion;
import com.badeling.msbot.entity.RankInfo;
import com.badeling.msbot.entity.RereadSentence;
import com.badeling.msbot.entity.RereadTime;
import com.badeling.msbot.entity.RoleDmg;
import com.badeling.msbot.repository.MsgNoPrefixRepository;
import com.badeling.msbot.repository.MsgRepository;
import com.badeling.msbot.repository.QuizOzAnswerRepository;
import com.badeling.msbot.repository.QuizOzQuestionRepository;
import com.badeling.msbot.repository.RankInfoRepository;
import com.badeling.msbot.repository.RereadSentenceRepository;
import com.badeling.msbot.repository.RereadTimeRepository;
import com.badeling.msbot.repository.RoleDmgRepository;
import com.badeling.msbot.service.ChannelService;
import com.badeling.msbot.service.DrawService;
import com.badeling.msbot.service.GroupMsgService;
import com.badeling.msbot.service.MsgService;
import com.badeling.msbot.service.MvpImageService;
import com.badeling.msbot.service.PrivateService;
import com.badeling.msbot.service.RankService;
import com.badeling.msbot.service.WzXmlService;
import com.badeling.msbot.util.Loadfont2;
import com.badeling.msbot.util.TranslateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MsgServiceImpl implements MsgService{
	
	@Autowired
	private MsgRepository msgRepository;
	
	@Autowired
	private MsgZbCalculate msgZbCalculate;
	
	@Autowired
	private GroupMsgService groupMsgService;
	
	@Autowired
	private MvpImageService mvpImageService;
	
	@Autowired
	private DrawService drawService;
	
	@Autowired
	private RankService rankService;
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private RereadSentenceRepository rereadSentenceRepository;
	
	@Autowired
	private RereadTimeRepository rereadTimeRepository;
	
	@Autowired
	private RoleDmgRepository roleDmgRepository;
	
	@Autowired
	private PrivateService privateService;
	
	@Autowired
	private WzXmlService wzXmlService;
	
	@Autowired
	private MsgNoPrefixRepository msgNoPrefixRepository;
	
	@Autowired
	RankInfoRepository rankInfoRepository;
	
	@Autowired
	QuizOzQuestionRepository quizOzQuestionRepository;
	
	@Autowired
	QuizOzAnswerRepository quizOzAnswerRepository;
	
	@Override
	public ReplyMsg receive(String msg) {
		ReceiveMsg receiveMsg = null;
		//???????????????????????????
		TreeSet<String> msgList = GlobalVariable.getMsgList();
		if(msgList.contains(msg)) {
			return null;
		}
		if(msgList.size()>128) {	
			for(int i=0;i<16;i++) {
				Iterator<String> iterator = msgList.iterator();
				msgList.remove(iterator.next());
			}
		}
		msgList.add(msg);
		GlobalVariable.setMsgList(msgList);
		
		
        try {
        	if(msg.contains("message_type")) {
        		if(msg.contains("\"channel_id\":")) {
        			channelService.receive(msg);
        			return null;
        		}
        		receiveMsg = new ObjectMapper().readValue(msg, ReceiveMsg.class);
        	}else if(msg.contains("\"notice_type\":\"group_increase\"")) {
        		NoticeMsg noticeMsg = new ObjectMapper().readValue(msg, NoticeMsg.class);
        		return handWelcome(noticeMsg);
        	}else if(msg.contains("\"notice_type\":\"group_decrease\"")){
        		System.err.println(msg);
        		NoticeMsg noticeMsg = new ObjectMapper().readValue(msg, NoticeMsg.class);
        		return handLeave(noticeMsg);
        	}else {
        		return null;
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
			//??????
//        if(receiveMsg.getMessage_type().equals("private")&&receiveMsg.getUser_id().equals(MsbotConst.masterId)) {
//      	System.err.println(receiveMsg.toString());
//        	return handlePrivateMsg(receiveMsg);
//        }
        
        //???????????????
        for(String temp : MsbotConst.blackList) {
        	if(receiveMsg.getUser_id().equals(temp)) {
        		return null;
        	}
        }
        
        if(receiveMsg.getRaw_message().startsWith(MsbotConst.botName)) {
        	System.out.println(receiveMsg.toString());
        	return handleNameMsg(receiveMsg);
        }else if(receiveMsg.getRaw_message().startsWith("[CQ:at,qq="+MsbotConst.botId+"]")){
           	receiveMsg.setRaw_message(receiveMsg.getRaw_message().replace("[CQ:at,qq="+MsbotConst.botId+"]", MsbotConst.botName));
        	System.out.println(receiveMsg.toString());
        	return handleNameMsg(receiveMsg);
        }else if((receiveMsg.getRaw_message().contains("??????")||receiveMsg.getRaw_message().contains("MVP"))&&receiveMsg.getRaw_message().length()<=40){
        	System.out.println(receiveMsg.toString());
        	return null;
//        	return handleMvpMsg(receiveMsg);
        }else if(receiveMsg.getRaw_message().contains("[CQ:image,file=")){
        	//???????????????
        	System.out.println(receiveMsg.toString());
        	if(receiveMsg.getRaw_message().contains("???????????????")) {
        		return handRecognize2(receiveMsg);
        	}
        	if(receiveMsg.getRaw_message().contains("??????")) {
        		return handRecognize(receiveMsg);
        	}
        	if(receiveMsg.getRaw_message().startsWith("39")) {
        		return handRecognizeOz39(receiveMsg);
        	}
        }else if(receiveMsg.getRaw_message().length()>=2&&receiveMsg.getRaw_message().substring(0,2).contains("??????")){
        	System.out.println(receiveMsg.toString());
        	return handTransMsg(receiveMsg);
        }else if(receiveMsg.getRaw_message().length()>=4&&receiveMsg.getRaw_message().startsWith("????????????")) {
        	return handLegionRank(receiveMsg);
        }else if(receiveMsg.getRaw_message().length()>=4&&receiveMsg.getRaw_message().startsWith("????????????")) {
//        	????????????badeling
        	return handAddRankName(receiveMsg);
        }
        
        //?????? or ??????
//        Pattern r = Pattern.compile(".*\\d+[e|E|\\???].*");
//		Matcher m = r.matcher(receiveMsg.getRaw_message());
//		boolean matches = m.matches();
//    	if(matches) {
//    		handMemberSellAndBuy(receiveMsg);
//    	}
        
    	//re-read
    	handReplyMsg(receiveMsg);
    	receiveMsg.setRaw_message(receiveMsg.getMessage());
    	return handRereadMsg(receiveMsg);
        
	}

	private ReplyMsg handRecognizeOz39(ReceiveMsg receiveMsg) {
		//??????
		String[] result = mvpImageService.handHigherImageMsg(receiveMsg);
		//????????????
		String raw_message = "";
		String reply = "";
		try {
			@SuppressWarnings("unchecked")
			Map<String,Object> backMessage = (Map<String, Object>) JSONObject.parse(result[0]);
			@SuppressWarnings("unchecked")
			List<Map<String,String>> list = (List<Map<String, String>>) backMessage.get("words_result");
			
			int count = list.size();
			
			for(int i=0;i<count-4;i++) {
				raw_message = raw_message + list.get(i).get("words");
			}
			List<QuizOzQuestion> qozList = quizOzQuestionRepository.findAllQoz();
			System.out.println("????????????:"+raw_message);
			reply = raw_message + "\r\n";
			QuizOzQuestion MatchQoz = null;
			int i=1024;
			for(QuizOzQuestion qoz : qozList) {
				int result2 = getResult(qoz.getQuestion(),raw_message);
				if(result2<i) {
					MatchQoz = qoz;
					i = result2;
				}
			}
			reply = reply + "MatchQue : "+MatchQoz.getQuestion()+"\r\n \r\n";
			
			String a = list.get(count-4).get("words").replaceAll("  ", " ").replaceAll("  ", " ");
			String b = list.get(count-3).get("words").replaceAll("  ", " ").replaceAll("  ", " ");
			String c = list.get(count-2).get("words").replaceAll("  ", " ").replaceAll("  ", " ");
			String d = list.get(count-1).get("words").replaceAll("  ", " ").replaceAll("  ", " ");

			Set<QuizOzAnswer> answers = MatchQoz.getAnswers();
			Iterator<QuizOzAnswer> iterator = answers.iterator();
			
			reply = reply + "???????????? : ";
			int k = 16;
			QuizOzAnswer MatchQoa = null;
			while(iterator.hasNext()) {
				QuizOzAnswer next = iterator.next();
				reply = reply + next.getAnswer();
				if(iterator.hasNext()) {
					reply = reply + " | ";
				}else {
					reply = reply + "\r\n \r\n";
				}
				
				if(getResult(next.getAnswer(),a)<=k) {
					k=getResult(next.getAnswer(),a);
					MatchQoa = next;
				}
				if(getResult(next.getAnswer(),b)<=k) {
					k=getResult(next.getAnswer(),b);
					MatchQoa = next;
				}
				if(getResult(next.getAnswer(),c)<=k) {
					k=getResult(next.getAnswer(),c);
					MatchQoa = next;
				}
				if(getResult(next.getAnswer(),d)<=k) {
					k=getResult(next.getAnswer(),d);
					MatchQoa = next;
				}
			}
			
			reply = reply + "???????????? : " + MatchQoa.getAnswer() + "\r\n";
			ReplyMsg replyMsg = new ReplyMsg();
			replyMsg.setReply(reply);
			return replyMsg;
		} catch (Exception e) {
			e.printStackTrace();
			ReplyMsg replyMsg = new ReplyMsg();
			replyMsg.setReply("????????????????????????????????????");
			return replyMsg;
		}
		}
	//	handAddRankName
	private ReplyMsg handAddRankName(ReceiveMsg receiveMsg) {
		ReplyMsg replyMsg = new ReplyMsg();
		replyMsg.setAuto_escape(true);
		String raw_message = receiveMsg.getRaw_message();
		raw_message = raw_message.substring(4);
		raw_message = raw_message.replace(" ", "");
		if(raw_message.equals("")) {
			replyMsg.setReply("?????????????????????????????????id???");
		}else {
			RankInfo rankInfo = rankInfoRepository.getInfoByUserId(receiveMsg.getUser_id());
			if(rankInfo!=null) {
				rankInfoRepository.delete(rankInfo);
			}
			RankInfo ri = new RankInfo();
			ri.setUser_id(receiveMsg.getUser_id());
			ri.setUser_name(raw_message);
			rankInfoRepository.save(ri);
			replyMsg.setReply("????????????");
		}
		return replyMsg;
	}

	private ReplyMsg handLegionRank(ReceiveMsg receiveMsg) {
		ReplyMsg replyMsg = new ReplyMsg();
		String raw_message = receiveMsg.getRaw_message();
		raw_message = raw_message.replace(" ", "");
		raw_message = raw_message.substring(4);
		String name = "";
		if(raw_message.equals("")) {
			RankInfo rankInfo = rankInfoRepository.getInfoByUserId(receiveMsg.getUser_id());
			if(rankInfo==null) {
				replyMsg.setReply("??????????????????\r\n" + 
						"?????????????????????badeling");
				return replyMsg;
			}else {
				name = rankInfo.getUser_name();
			}
		}else if(raw_message.contains("[CQ:at,qq=")) {
			int aIndex = receiveMsg.getRaw_message().indexOf("[CQ:at,qq=")+10;
    		int bIndex = receiveMsg.getRaw_message().indexOf("]");
    		String findNumber = receiveMsg.getRaw_message().substring(aIndex,bIndex);
    		RankInfo rankInfo = rankInfoRepository.getInfoByUserId(findNumber);
    		if(rankInfo==null) {
				replyMsg.setReply("??????????????????\r\n" + 
						"?????????????????????badeling");
				return replyMsg;
			}else {
				name = rankInfo.getUser_name();
			}
		}else {
			name = raw_message;
		}

			String legionForBtOrLara = rankService.getRank(name);
			replyMsg.setReply(legionForBtOrLara);
		
		GroupMsg groupMsg = new GroupMsg();
		groupMsg.setAuto_escape(false);
		groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
		groupMsg.setMessage(replyMsg.getReply());
		groupMsgService.sendGroupMsg(groupMsg);
		
		return null;
	}

	private void handReplyMsg(ReceiveMsg receiveMsg) {
		List<MsgNoPrefix> result = msgNoPrefixRepository.findMsgNPList();
		for(MsgNoPrefix m : result) {
			if(m.isExact()&&receiveMsg.getRaw_message().equals(m.getQuestion())) {
				GroupMsg groupMsg = new GroupMsg();
    			groupMsg.setMessage(m.getAnswer());
    			groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
    			groupMsgService.sendGroupMsg(groupMsg);
    			System.err.println(groupMsg.toString());
    			break;
			}
			if(!m.isExact()&&receiveMsg.getRaw_message().contains(m.getQuestion())) {
				GroupMsg groupMsg = new GroupMsg();
    			groupMsg.setMessage(m.getAnswer());
    			groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
    			groupMsgService.sendGroupMsg(groupMsg);
    			System.err.println(groupMsg.toString());
    			break;
    			
			}
		}
	}

	private ReplyMsg handlePrivateMsg(ReceiveMsg receiveMsg) {
		
		if(receiveMsg.getRaw_message().length()>=2&&receiveMsg.getRaw_message().substring(0,2).contains(MsbotConst.botName)) {
        	return handleNameMsg(receiveMsg);
        }
		ReplyMsg replyMsg = privateService.handlePrivateMsg(receiveMsg);
		return replyMsg;
	}
	
	
	private ReplyMsg handRecognize3(ReceiveMsg receiveMsg) {
		//??????
		String[] result = mvpImageService.handHigherImageMsg(receiveMsg);
		//????????????
		String raw_message = "";
		try {
			@SuppressWarnings("unchecked")
			Map<String,Object> backMessage = (Map<String, Object>) JSONObject.parse(result[0]);
			@SuppressWarnings("unchecked")
			List<Map<String,String>> list = (List<Map<String, String>>) backMessage.get("words_result");
			for(Map<String,String> a : list) {
				raw_message = raw_message + a.get("words");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		ReplyMsg replyMsg = new ReplyMsg();
		replyMsg.setReply(raw_message);
		return replyMsg;
}

	private ReplyMsg handRecognize2(ReceiveMsg receiveMsg) {
				//??????
				String[] result = mvpImageService.handHigherImageMsg(receiveMsg);
				//????????????
				String raw_message = "????????????????????????\r\n";
				try {
					@SuppressWarnings("unchecked")
					Map<String,Object> backMessage = (Map<String, Object>) JSONObject.parse(result[0]);
					@SuppressWarnings("unchecked")
					List<Map<String,String>> list = (List<Map<String, String>>) backMessage.get("words_result");
					for(Map<String,String> a : list) {
						raw_message = raw_message + a.get("words")+"\r\n";
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				ReplyMsg replyMsg = new ReplyMsg();
				replyMsg.setReply(raw_message);
				return replyMsg;
	}


	private ReplyMsg handRereadMsg(ReceiveMsg receiveMsg) {
		// receiveMsg.getRaw_message().contains("[CQ:image,file=")
		HashMap<String, ReReadMsg> map = ReRead.getMap();
		if(map==null) {
			map = new HashMap<String, ReReadMsg>();
		}
		ReReadMsg reReadMsg = map.get(receiveMsg.getGroup_id());
		
		boolean isBreakReread = false;
		//??????????????????????????????
		if(reReadMsg!=null) {
			if(receiveMsg.getRaw_message().contains("[CQ:image,file=")&&reReadMsg.getRaw_message().contains("[CQ:image,file=")) {
				String name1 = reReadMsg.getRaw_message().substring(reReadMsg.getRaw_message().indexOf("file=")+5,reReadMsg.getRaw_message().indexOf(",url="));
				String name2 = receiveMsg.getRaw_message().substring(receiveMsg.getRaw_message().indexOf("file=")+5,receiveMsg.getRaw_message().indexOf(",url="));
				isBreakReread = !name1.equals(name2);
			}else {
				isBreakReread = !reReadMsg.getRaw_message().equals(receiveMsg.getRaw_message());
			}
			
			if(!isBreakReread) {
				if(reReadMsg.getReread_id().equals(receiveMsg.getUser_id())) {
					return null;
				}
			}
		}
		
		if(reReadMsg==null || isBreakReread) {
			//???????????????
			if(reReadMsg==null) {
				reReadMsg = new ReReadMsg();
				reReadMsg.setMes_count(1);
			}
			if(receiveMsg.getRaw_message().contains("&#")) {
				return null;
			}
			//????????????
			if(reReadMsg.getCount()>1) {
				RereadSentence rereadSentence = rereadSentenceRepository.findMaxByGroup(receiveMsg.getGroup_id());
				if(rereadSentence==null) {
					rereadSentence = new RereadSentence();
					rereadSentence.setGroup_id(receiveMsg.getGroup_id());
					rereadSentence.setMessage(reReadMsg.getRaw_message());
					rereadSentence.setReadTime(reReadMsg.getCount());
					rereadSentence.setUser_id(reReadMsg.start_id);
					rereadSentenceRepository.save(rereadSentence);
				}else if(reReadMsg.getCount()>rereadSentence.getReadTime()){
					rereadSentenceRepository.delete(rereadSentence);
					rereadSentence.setId(null);
					rereadSentence.setGroup_id(receiveMsg.getGroup_id());
					rereadSentence.setMessage(reReadMsg.getRaw_message());
					rereadSentence.setReadTime(reReadMsg.getCount());
					rereadSentence.setUser_id(reReadMsg.start_id);
					rereadSentenceRepository.save(rereadSentence);
				}
			}
			reReadMsg.setCount(1);
			reReadMsg.setRaw_message(receiveMsg.getRaw_message());
			reReadMsg.setReread_id(receiveMsg.getUser_id());
			Random r = new Random();
			reReadMsg.setRe_count(2+r.nextInt(2));
			reReadMsg.setStart_id(receiveMsg.getUser_id());
			map.put(receiveMsg.getGroup_id(), reReadMsg);
			ReRead.setMap(map);
		}else{
			reReadMsg.setCount(reReadMsg.getCount()+1);
			reReadMsg.setReread_id(receiveMsg.getUser_id());
			if(reReadMsg.getCount()>reReadMsg.getRe_count()) {
				reReadMsg.setRe_count(100);
				reReadMsg.setCount(reReadMsg.getCount()+1);
				GroupMsg groupMsg = new GroupMsg();
				groupMsg.setAuto_escape(false);
				groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
				if(reReadMsg.getRaw_message().equals("???")) {
					groupMsg.setMessage("[CQ:image,file=save/7AA9BBE83B63CB529F8EC7B64B14116C]");
				}else {
					groupMsg.setMessage(reReadMsg.getRaw_message());
				}
				groupMsgService.sendGroupMsg(groupMsg);
			}
			map.put(receiveMsg.getGroup_id(), reReadMsg);
			//????????????
			RereadTime rereadTime = rereadTimeRepository.findByGroupAndId(receiveMsg.getGroup_id(),receiveMsg.getUser_id());
			if(rereadTime==null) {
				rereadTime = new RereadTime();
				rereadTime.setCount(1);
				rereadTime.setGroup_id(receiveMsg.getGroup_id());
				rereadTime.setUser_id(receiveMsg.getUser_id());
				rereadTimeRepository.save(rereadTime);
			}else {
				rereadTimeRepository.modifyReread(rereadTime.getId(),rereadTime.getCount()+1);
			}
			
		}
		
		if(reReadMsg.getMes_count()>300&&reReadMsg.getCount()==1) {
			reReadMsg.setMes_count(1);
			
			Random r = new Random();
			//????????????auto
			List<Msg> msgList = msgRepository.findMsgByExtQuestion("????????????auto");
			int random = r.nextInt(msgList.size());
			Msg msg = msgList.get(random);
			ReplyMsg rm = new ReplyMsg();
			if(msg.getLink()!=null&&msg.getLink().equals("at")) {
				rm.setAt_sender(true);
			}else if(msg.getLink()!=null&&msg.getLink().equals("random")) {
				GroupMsg groupMsg = new GroupMsg();
				groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
				Result<?> groupMember = groupMsgService.getGroupMember(groupMsg);
				if(groupMember.getStatus().equals("ok")) {
					@SuppressWarnings("unchecked")
					List<Map<String,Object>> data = (List<Map<String, Object>>) groupMember.getData();
					random = r.nextInt(data.size());
					Map<String, Object> map2 = data.get(random);
					groupMsg.setMessage("[CQ:at,qq="+map2.get("user_id")+"]"+msg.getAnswer());
					groupMsgService.sendGroupMsg(groupMsg);
					return null;
				}
			}
			rm.setAuto_escape(false);
			rm.setReply(msg.getAnswer());
			return rm;
				
		}

		reReadMsg.setMes_count(reReadMsg.getMes_count()+1);
		map.put(receiveMsg.getGroup_id(), reReadMsg);
		return null;
	}


	private ReplyMsg handTransMsg(ReceiveMsg receiveMsg) {
		String transResult;
		ReplyMsg replyMsg = new ReplyMsg();
		String raw_message = receiveMsg.getRaw_message().substring(2);
		replyMsg.setAt_sender(true);
		replyMsg.setAuto_escape(false);
		raw_message = raw_message.replaceAll("\r",".");
		raw_message = raw_message.replaceAll("\n",".");
		try {
			transResult = TranslateUtil.getTransResult(raw_message, "auto", "auto");
//			transResult = Sensitive.replaceSensitiveWords(transResult);
	        replyMsg.setReply(transResult);
	        return replyMsg;
	        
		} catch (IOException e) {
		}
		return null;
	}

	
	private ReplyMsg handRecognize(ReceiveMsg receiveMsg) {
		//??????
		String[] result = mvpImageService.handImageMsg(receiveMsg);
		//????????????
		String raw_message = "";
		try {
			@SuppressWarnings("unchecked")
			Map<String,Object> backMessage = (Map<String, Object>) JSONObject.parse(result[0]);
			@SuppressWarnings("unchecked")
			List<Map<String,String>> list = (List<Map<String, String>>) backMessage.get("words_result");
			for(Map<String,String> a : list) {
				raw_message = raw_message + a.get("words")+"\r\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		ReplyMsg replyMsg = new ReplyMsg();
		replyMsg.setReply(raw_message);
		return replyMsg;
	}
	
	private ReplyMsg handleNameMsg(ReceiveMsg receiveMsg) {
		Msg msg = null;
		ReplyMsg replyMsg = new ReplyMsg();
		String raw_message = receiveMsg.getMessage();
		replyMsg.setAt_sender(true);
		replyMsg.setAuto_escape(false);
		
		//????????????
		if(raw_message.contains("??????")&&raw_message.contains("?????????")&&raw_message.contains("???")) {
			if(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)) {
				try {
					int questionIndex = raw_message.indexOf("???");
					int answerIndex = raw_message.indexOf(" ???");
					String theQuestion = raw_message.substring(questionIndex+1, answerIndex);
					
					String answer = raw_message;
					while(answer.contains("[CQ:image")) {
						String imageName = mvpImageService.saveImage(answer);
						imageName = "[CQ:image,file=save/" + imageName + "]";
						String imageCq = answer.substring(answer.indexOf("[CQ:image,file"), answer.indexOf("]")+1);
						raw_message = raw_message.replace(imageCq, imageName);
						answer = answer.replace(imageCq, "");
					}
					String ans = raw_message.substring(answerIndex+2);
					MsgNoPrefix mnpf = new MsgNoPrefix();
					mnpf.setQuestion(theQuestion);
					mnpf.setAnswer(ans);
					msgNoPrefixRepository.save(mnpf);
					replyMsg.setAt_sender(false);
					replyMsg.setReply("[CQ:image,file=img/record.gif]");
						}catch (Exception e) {
							replyMsg.setReply("????????????????????????????????????");
						}
						
					}else {
						replyMsg.setReply("???????????????????????????????????????????????????");
					}

					return replyMsg;
				}
		
		//????????????
		if(raw_message.contains("??????")&&raw_message.contains("?????????")&&raw_message.contains("???")) {
			if(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)||isAdminMsg(receiveMsg.getUser_id())) {
				try {
					int questionIndex = raw_message.indexOf("???");
					int answerIndex = raw_message.indexOf(" ???");
					String theQuestion = raw_message.substring(questionIndex+1, answerIndex);
					if((theQuestion.contains("????????????")||theQuestion.contains("????????????"))&&!receiveMsg.getUser_id().equals(MsbotConst.masterId)) {
						replyMsg.setReply("?????????????????????????????????????????????????????????22??????????????????21");
						return replyMsg;
					}
					
					String[] qList = theQuestion.split("\\|");
					String answer = raw_message;
					while(answer.contains("[CQ:image")) {
						String imageName = mvpImageService.saveImage(answer);
						imageName = "[CQ:image,file=save/" + imageName + "]";
						String imageCq = answer.substring(answer.indexOf("[CQ:image,file"), answer.indexOf("]")+1);
						raw_message = raw_message.replace(imageCq, imageName);
						answer = answer.replace(imageCq, "");
					}
					String ans = raw_message.substring(answerIndex+2);
					
					for(String a : qList) {
						Msg newMsg = new Msg();
						newMsg.setQuestion(a);
						if(ans.contains("#abcde#")) {
							newMsg.setAnswer(ans.substring(0,ans.indexOf("#abcde#")));
							newMsg.setLink(ans.substring(ans.indexOf("#abcde#")+7));
						}else {
							newMsg.setAnswer(ans);
						}
						newMsg.setCreateId(receiveMsg.getUser_id());
						msgRepository.save(newMsg);
					}
					replyMsg.setAt_sender(false);
					replyMsg.setReply("[CQ:image,file=img/record.gif]");
						}catch (Exception e) {
							replyMsg.setReply("????????????????????????????????????");
						}
						
					}else {
						replyMsg.setReply("???????????????????????????????????????????????????");
					}

					return replyMsg;
				}
		
		//??????
		if(raw_message.contains("??????")&&raw_message.contains("???")&&raw_message.contains("???")) {
			if(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)||isAdminMsg(receiveMsg.getUser_id())) {
				try {
					Msg newMsg = new Msg();
					int questionIndex = raw_message.indexOf("???");
					int answerIndex = raw_message.indexOf(" ???");
					String theQuestion = raw_message.substring(questionIndex+1, answerIndex);
					newMsg.setQuestion(theQuestion);
					if((theQuestion.contains("????????????")||theQuestion.contains("????????????"))&&!receiveMsg.getUser_id().equals(MsbotConst.masterId)) {
						replyMsg.setReply("?????????????????????????????????????????????????????????22??????????????????21");
						return replyMsg;
					}
					String answer = raw_message;
					while(answer.contains("[CQ:image")) {
						String imageName = mvpImageService.saveImage(answer);
						imageName = "[CQ:image,file=save/" + imageName + "]";
						String imageCq = answer.substring(answer.indexOf("[CQ:image,file"), answer.indexOf("]")+1);
						raw_message = raw_message.replace(imageCq, imageName);
						answer = answer.replace(imageCq, "");
					}
					String ans = raw_message.substring(answerIndex+2);
					System.err.println(ans);
					if(ans.contains("#abcde#")) {
						System.err.println(ans);
						newMsg.setAnswer(ans.substring(0,ans.indexOf("#abcde#")));
						newMsg.setLink(ans.substring(ans.indexOf("#abcde#")+7));
					}else {
						System.out.println(ans);
						newMsg.setAnswer(ans);
					}
					newMsg.setCreateId(receiveMsg.getUser_id());
					msgRepository.save(newMsg);
					replyMsg.setAt_sender(false);
					replyMsg.setReply("[CQ:image,file=img/record.gif]");
						}catch (Exception e) {
							replyMsg.setReply("????????????????????????????????????");
						}
						
					}else {
						replyMsg.setReply("???????????????????????????????????????????????????");
					}

					return replyMsg;
				}

		//??????
		Set<Msg> set = msgRepository.findAllQuestion();
		Iterator<Msg> it = set.iterator();
		
		if((raw_message.startsWith(MsbotConst.botName+"??????")||raw_message.startsWith(MsbotConst.botName+" ??????"))&&(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)||isAdminMsg(receiveMsg.getUser_id()))) {
			try {
				int index = raw_message.indexOf("??????");
				Set<Msg> oldMsgList = msgRepository.findMsgByQuestion(raw_message.substring(index+2));
				Msg oldMsg;
				String messageReply = "";
				Iterator<Msg> it2 = oldMsgList.iterator();
				if(!it2.hasNext()) {
					replyMsg.setReply("??????????????????");
					return replyMsg;
				}
				
				while(it2.hasNext()) {
					oldMsg = it2.next();
					if(oldMsg.getAnswer().contains("[CQ:record")) {
						messageReply = messageReply + "ID:"+ oldMsg.getId() + " ?????????"+oldMsg.getQuestion()+" ?????????"+oldMsg.getAnswer().replace("[CQ:record,file", "[voice") + oldMsg.getLink() + "\r\n";
					}else {
						messageReply = messageReply + "ID:"+ oldMsg.getId() + " ?????????"+oldMsg.getQuestion()+" ?????????"+oldMsg.getAnswer();
						if(oldMsg.getLink()!=null) {
							messageReply = messageReply + "#abcde#" + oldMsg.getLink();
						}
						messageReply = messageReply + "\r\n";
					}
				}
				messageReply = messageReply.replaceAll("#abcde#", "\\|");
				replyMsg.setReply(messageReply);
			}catch (Exception e) {
				System.out.println(e);
				replyMsg.setReply("????????????");
			}
			return replyMsg;
		}
		
		//??????
		if(raw_message.startsWith(MsbotConst.botName+"????????????")||raw_message.startsWith(MsbotConst.botName+" ????????????")) {
			if(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)||isAdminMsg(receiveMsg.getUser_id())) {
				try {
					int index = raw_message.indexOf("????????????");
					Msg findQuestion = msgRepository.findQuestion(raw_message.substring(index+4));
					if(findQuestion==null) {
						replyMsg.setReply("?????????????????????");
					}else {
						if(isAdminMsg(receiveMsg.getUser_id())) {
							if(!findQuestion.getCreateId().equals(receiveMsg.getUser_id())) {
								replyMsg.setReply("????????????????????????????????????");
								return replyMsg;
							}
						}
						msgRepository.delete(findQuestion);
						replyMsg.setReply("???????????? ??????:"+findQuestion.getQuestion());
					}
				}catch (Exception e) {
					replyMsg.setReply("????????????");
				}
				
			}else {
				replyMsg.setReply("???????????????????????????????????????????????????");
			}
			return replyMsg;
		}
		
		//????????????
		if(raw_message.contains("??????")&&(raw_message.contains("boss")||raw_message.contains("BOSS"))) {
			String[] split = raw_message.split(" ");
			RoleDmg roleDmg = roleDmgRepository.findRoleBynumber(receiveMsg.getSender().getUser_id());
			if(roleDmg == null) {
				//???????????????
				roleDmg = new RoleDmg();
				//??????????????? ???????????? ????????????
				if(receiveMsg.getSender().getCard()==null || receiveMsg.getSender().getCard().equals("")) {
					roleDmg.setName(receiveMsg.getSender().getNickname());
				}else {
					roleDmg.setName(receiveMsg.getSender().getCard());
				}
				//??????QQ???
				roleDmg.setUser_id(receiveMsg.getSender().getUser_id());
				//????????????
				if(receiveMsg.getGroup_id().contains("101577006")) {
					roleDmg.setGroup_id("398359236");
				}else {
					roleDmg.setGroup_id(receiveMsg.getGroup_id());
				}
				roleDmg.setCommonDmg(100);
				roleDmg.setBossDmg(200);
				roleDmg = roleDmgRepository.save(roleDmg);
			}
			try {
				for(String temp : split) {
					temp.replace("%", "");
					if(temp.contains("??????")) {
						roleDmg.setCommonDmg(Integer.parseInt(temp.replace(MsbotConst.botName,"").replace("??????", "")));
					}else if(temp.toLowerCase().contains("boss")) {
						temp = temp.toLowerCase();
						roleDmg.setBossDmg(Integer.parseInt(temp.replace(MsbotConst.botName,"").replace("boss", "")));
					}else {
					}
				}
				roleDmgRepository.modifyDmg(roleDmg.getId(), roleDmg.getCommonDmg(), roleDmg.getBossDmg());
				replyMsg.setReply("????????????");
				return replyMsg;
			} catch (Exception e) {
				replyMsg.setReply("????????????????????????????????????");
				return replyMsg;
			}
			
		}
		
		
		//????????????
		if(raw_message.startsWith(MsbotConst.botName+"??????")||raw_message.startsWith(MsbotConst.botName+" ??????")) {
			if(raw_message.equals(MsbotConst.botName+"??????")||raw_message.equals(MsbotConst.botName+" ??????")) {
				replyMsg.setReply("????????????????????????");
				return replyMsg;
			}else if(raw_message.equals(MsbotConst.botName+"??????????????????")){
				if(receiveMsg.getUser_id().equals(MsbotConst.masterId)) {
					wzXmlService.updateMobInfo();
					replyMsg.setReply("MobInfo????????????");
				}else {
					replyMsg.setReply("???????????????????????????????????????????????????");
				}
				return replyMsg;
			}else {
				if(raw_message.contains(MsbotConst.botName+"??????")||raw_message.contains(MsbotConst.botName+" ??????")) {
					raw_message = raw_message.substring(raw_message.indexOf("??????")+2);
					if(raw_message.indexOf(" ")==0) {
						raw_message = raw_message.substring(1);
					}
					//????????????
					System.out.println(raw_message);
					try {
						if(raw_message.length()==7) {
							Long mob_id = Long.parseLong(raw_message);
							wzXmlService.searchMob(mob_id,receiveMsg.getGroup_id());
							return null;
						}
					} catch (Exception e) {
					}
					wzXmlService.searchMob(raw_message,receiveMsg.getGroup_id(),receiveMsg.getUser_id());
					return null;
					
				}else {
					
				}
				
			}
		}
		//????????????
		if(raw_message.startsWith(MsbotConst.botName+"????????????")) {
			try {
				String testFont = Loadfont2.testFont2(raw_message.substring(6));
				replyMsg.setReply(testFont);
				return replyMsg;
			} catch (Exception e) {
				replyMsg.setReply("????????????????????????????????????");
				e.printStackTrace();
				return replyMsg;
			}
		}
		
	
		//????????????
		if(raw_message.contains("??????")&&(raw_message.contains("+")||raw_message.contains("-"))) {
			try {
				//????????????
				raw_message = raw_message.replace("%", "");
				raw_message = raw_message.replace(MsbotConst.botName, "");
				raw_message = raw_message.replace(" ", "");
				raw_message = raw_message.replace("=", "");
				raw_message = raw_message.replace("??????", "");
				double ign = 0;		
				double ign_before = 0;
				double ign2 = 0;
				double ign_before2 = 0;
				//???????????????
//				String shortMsg = "";
				
				if(raw_message.contains("+")&&!raw_message.contains("-")) {
					String[] getInt = raw_message.split("\\+");
					for(int i=0;i<getInt.length;i++) {
						if(i==0) {
							ign_before = Double.parseDouble(getInt[i]);
							ign_before2 = ign_before + (100-ign_before)*20/100;
//							shortMsg = getInt[i] + "%";
						}else {
//							shortMsg += " + " + getInt[i] + "%";
						}
						if(Double.parseDouble(getInt[i])>100) {
							replyMsg.setReply("????????????");
							return replyMsg;
						}
						ign = ign + (100-ign)*Double.parseDouble(getInt[i])/100;
					}
				}else if(raw_message.contains("-")&&!raw_message.contains("+")){
					String[] getInt = raw_message.split("\\-");
					for(int i=0;i<getInt.length;i++) {
						if(Double.parseDouble(getInt[i])>100) {
							replyMsg.setReply("????????????");
							return replyMsg;
						}
						if(i==0) {
							ign_before = Double.parseDouble(getInt[i]);
							ign_before2 = ign_before + (100-ign_before)*20/100;
							ign = ign_before;
//							shortMsg = getInt[i] + "%";
						}else {
							ign = (ign-Double.parseDouble(getInt[i]))/(100-Double.parseDouble(getInt[i]))*100;
//							shortMsg += " - " + getInt[i] + "%";
						}
					}
				}else {
					replyMsg.setReply("???????????????????????????????????????");
					return replyMsg;
				}
				
				RoleDmg roleDmg = roleDmgRepository.findRoleBynumber(receiveMsg.getSender().getUser_id());
				if(roleDmg == null) {
					//???????????????
					roleDmg = new RoleDmg();
					//??????????????? ???????????? ????????????
					if(receiveMsg.getSender().getCard()==null || receiveMsg.getSender().getCard().equals("")) {
						roleDmg.setName(receiveMsg.getSender().getNickname());
					}else {
						roleDmg.setName(receiveMsg.getSender().getCard());
					}
					//??????QQ???
					roleDmg.setUser_id(receiveMsg.getSender().getUser_id());
					//????????????
					if(receiveMsg.getGroup_id().contains("101577006")) {
						roleDmg.setGroup_id("398359236");
					}else {
						roleDmg.setGroup_id(receiveMsg.getGroup_id());
					}
					roleDmg.setCommonDmg(100);
					roleDmg.setBossDmg(200);
					roleDmgRepository.save(roleDmg);

					GroupMsg groupMsg = new GroupMsg();
					groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
					groupMsg.setMessage("[CQ:at,qq="+ receiveMsg.getSender().getUser_id() +"]"+"???????????????????????????????????????100,boss???200????????????????????????"+MsbotConst.botName+" ??????50 boss300???????????????????????????");
					groupMsgService.sendGroupMsg(groupMsg);
				}
				
//				shortMsg += " = " + String.format("%.2f", ign) + "%";
//				shortMsg += "\r\n???????????????????????????????????????????????????????????????????????????";
				
				ign2 = ign + (100-ign)*20/100;
				String replyM = "?????????????????????" + ign_before + "%(" + ign_before2 + "%)\r\n" + "?????????????????????" + String.format("%.2f", ign) + "%(" + String.format("%.2f", ign2) + "%)\r\n";
				replyM += "???????????? ??????:" + roleDmg.getCommonDmg() + "% boss:" + roleDmg.getBossDmg() + "%\r\n(???????????????20%??????????????????)\r\n";
				
				replyM += "//----???????????????-----//\r\n";
				replyM += "??????????????????380???????????????" + String.format("%.2f", (defAndign(380, ign)/defAndign(380, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(380, ign2)/defAndign(380, ign_before2)-1)*100) + "%)\r\n";
				replyM += "?????????" + defAndign(380, ign_before) + "%(" + defAndign(380, ign_before2) + "%)\r\n";
				replyM += "?????????" + defAndign(380, ign) + "%(" + defAndign(380, ign2) +  "%)\r\n";
				replyM += "??????????????????" + String.format("%.2f",(defAndign(380, ign)/defAndign(380, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%(" + String.format("%.2f", (defAndign(380, ign2)/defAndign(380, ign_before2)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%)???boss??????\r\n";
				
				replyM += "//-----????????????-----//\r\n";
				replyM += "??????????????????300????????????" + String.format("%.2f", (defAndign(300, ign)/defAndign(300, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(300, ign2)/defAndign(300, ign_before2)-1)*100) + "%)\r\n";
				replyM += "?????????" + defAndign(300, ign_before) + "%(" + defAndign(300, ign_before2) + "%)\r\n";
				replyM += "?????????" + defAndign(300, ign) + "%(" + defAndign(300, ign2) +  "%)\r\n";
				replyM += "??????????????????" + String.format("%.2f",(defAndign(300, ign)/defAndign(300, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%(" + String.format("%.2f", (defAndign(300, ign2)/defAndign(300, ign_before2)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%)???boss??????\r\n";
				
				replyM += "//-----????????????-----//\r\n";
				replyM += "????????????????????????200????????????" + String.format("%.2f", (defAndign(200, ign)/defAndign(200, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(200, ign2)/defAndign(200, ign_before2)-1)*100) + "%)\r\n";
				replyM += "?????????" + defAndign(200, ign_before) + "%(" + defAndign(200, ign_before2) + "%)\r\n";
				replyM += "?????????" + defAndign(200, ign) + "%(" + defAndign(200, ign2) +  "%)\r\n";
				replyM += "??????????????????" + String.format("%.2f",(defAndign(200, ign)/defAndign(200, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%(" + String.format("%.2f", (defAndign(200, ign2)/defAndign(200, ign_before2)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%)???boss??????";
				
				replyMsg.setReply(replyM);
				String reply = null;
				try {
					reply = drawService.ignImage(replyM);
					replyMsg.setReply(reply);
					replyMsg.setAt_sender(false);
				}catch (Exception e) {
					replyMsg.setReply("??????????????????");
					replyMsg.setAt_sender(false);
					e.printStackTrace();
				}
//				PrivateMsg privateMsg = new PrivateMsg();
//				privateMsg.setUser_id(Long.parseLong(receiveMsg.getUser_id()));
//				privateMsg.setMessage(replyM);
//				groupMsgService.sendPrivateMsg(privateMsg);
				return replyMsg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		if(raw_message.contains("???????????????")) {
			//?????????????????????
			GroupMsg gp = new GroupMsg();
			gp.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
			Result<?> groupMember = groupMsgService.getGroupMember(gp);
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> data = (List<Map<String, Object>>) groupMember.getData();
			Map<String,String> map = new HashMap<>();
			for(Map<String,Object> temp:data) {
				String a = temp.get("user_id")+"";
				String b = (String) temp.get("nickname");
				String c = (String) temp.get("card");
				if(c.equals("")) {
					//????????????
					map.put(a, b);
				}else {
					//????????????
					map.put(a, c);
				}
			}
			
			String message = "?????????????????????????????????\r\n";
					RereadSentence rereadSentence = rereadSentenceRepository.findMaxByGroup(receiveMsg.getGroup_id());
					if(rereadSentence!=null) {
						message += rereadSentence.getMessage() + "\r\n" + 
						"????????????????????????????????????"+ map.get(rereadSentence.getUser_id()) + "\r\n" + 
						"????????????????????????????????????" + rereadSentence.getReadTime() + "??????\r\n";
						List<RereadTime> list = rereadTimeRepository.find3thByGroup(receiveMsg.getGroup_id());
						if(list!=null) {
							message += "??????????????????????????????????????????????????????\r\n" + 
									"????????????????????????????????????" + map.get(list.get(0).getUser_id()) + "???\r\n" + 
									"????????????????????????????????????" + list.get(0).getCount() + "???????????????????????????\r\n" + 
									"??????????????????????????????????????????????????????\r\n" + 
									"???????????????????????????????????????????????????????????????????????????????????????\r\n";
									if(list.size()>1) {
										message += map.get(list.get(1).getUser_id()) + " ???????????????"+list.get(1).getCount() + "\r\n";
									}else{
										message += "????????????\r\n";
									}
									if(list.size()>2) {
										message += map.get(list.get(2).getUser_id()) + " ???????????????"+list.get(2).getCount() + "\r\n";
									}else {
										message += "????????????\r\n";
									}
									message += "????????????????????????????????????????????????uwu";
						}else {
							message = "owo,????????????????????????";
						}
					}else {
						message = "owo,?????????????????????";
					}
			replyMsg.setReply(message);
			replyMsg.setAt_sender(false);
			return replyMsg;
		}
		
		
		//????????????
		if(raw_message.startsWith(MsbotConst.botName+"?????????")&&receiveMsg.getUser_id().equals(MsbotConst.masterId)) {
			replyMsg.setAt_sender(false);
			replyMsg.setReply(raw_message.substring(5));
			return replyMsg;
		}
		
		//???xxx
		if(raw_message.startsWith(MsbotConst.botName+"???")&&raw_message.contains("[CQ:at")) {
    		try {
    			int aIndex = receiveMsg.getRaw_message().indexOf("[CQ:at,qq=")+10;
        		int bIndex = receiveMsg.getRaw_message().indexOf("]");
        		String findNumber = receiveMsg.getRaw_message().substring(aIndex,bIndex);
        		if(raw_message.contains("???")||raw_message.contains("???")||raw_message.contains("??????")||raw_message.contains("??????")) {
        			replyMsg.setAt_sender(true);
        			replyMsg.setReply("[CQ:image,file=img/buzhidao5.jpg]");
    				return replyMsg;
    			}
        		String throwSomeone = "";
        		if(findNumber.equals(MsbotConst.masterId)||findNumber.equals(MsbotConst.botId)||findNumber.equals("2419570484")) {
        			String saveTempImage = mvpImageService.saveTempImage("http://q1.qlogo.cn/g?b=qq&nk=" + receiveMsg.getUser_id() + "&s=3");
        			throwSomeone = drawService.throwSomeone(saveTempImage);
        			replyMsg.setAt_sender(false);
    				replyMsg.setReply("[CQ:at,qq=" + receiveMsg.getUser_id() + "]" + throwSomeone);
        		}else {
        			String saveTempImage = mvpImageService.saveTempImage("http://q1.qlogo.cn/g?b=qq&nk=" + findNumber + "&s=3");
        			throwSomeone = drawService.throwSomeone(saveTempImage);
        			replyMsg.setAt_sender(false);
    				replyMsg.setReply("[CQ:at,qq=" + findNumber + "]" + throwSomeone);
        		}
				return replyMsg;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			//????????????[??????][??????][????????????][?????????][????????????][????????????]
			/**
			 	????????????
			 	?????????
			 	????????????
			 	???????????????
			 	????????????
			 	???????????????
			 	???????????????
			 	
			 	?????? 150???16???
			 	
			 	?????? 160???13???428???
		}
			 */
			if(raw_message.contains("???")&&raw_message.contains("???")&&raw_message.contains("???")) {
				raw_message = raw_message.replaceAll(" ", "").substring(2);
				int level = Integer.parseInt(raw_message.substring(0,raw_message.indexOf("???")));
//				int stat = Integer.parseInt(split[2].substring(4));
				int stat = 0;
//				int fireStat = Integer.parseInt(split[3].substring(5));
				int fireStat = 0;
				int att = Integer.parseInt(raw_message.substring(raw_message.indexOf("???")+1,raw_message.indexOf("???")));
//				int fireAtt = Integer.parseInt(split[5].substring(5));
				int fireAtt = 0;
				int nowStar = Integer.parseInt(raw_message.substring(raw_message.indexOf("???")+1,raw_message.indexOf("???")));
				
				if(level!=130&&level!=140&&level!=150&&level!=160&&level!=200) {
					replyMsg.setReply("?????????????????????130,140,150,160,200?????????");
					return replyMsg;
				}
				if((level==130&&nowStar>20)||nowStar>25) {
					replyMsg.setReply("???????????????????????????");
					return replyMsg;
				}
				if(stat<0||fireStat<0||att<0||fireAtt<0||nowStar<0) {
					replyMsg.setReply("??????????????????");
					return replyMsg;
				}
				
				int[] starForce = starForceDesc(level,stat-fireStat,att-fireAtt,nowStar);
//				int finalStat = starForce[0]+fireStat;
				int finalAtt = starForce[1]+fireAtt;
//				String result = "???????????????"+level+" ??????" + stat + " ????????????" + fireStat + "\r\n"
//						+ "??????" + att + " ????????????" + fireAtt + " ??????" + nowStar + "\r\n"
//						+ "????????????????????????\r\n"
//						+ "?????????????????????" +  finalStat + " ??????"+ finalAtt + "\r\n"
//						+ "????????????????????????" +  starForce[0] + " ??????"+ starForce[1];
				String result = "0??????????????????????????????" + finalAtt;
				replyMsg.setReply(result);
				return replyMsg;
			}

			
			//????????????
			if(raw_message.contains("???")&&raw_message.contains("???")) {
				raw_message = raw_message.replaceAll(" ", "").substring(2);
				//level 130 140 150 160 200
				//????????????160 
				//????????????[??????][??????][????????????][?????????][????????????][????????????][????????????]
				/**
			 	????????????
			 	?????????
			 	?????????
			 	????????????
			 	???????????????
			 	????????????
			 	???????????????
			 	???????????????
			 	???????????????
			 	
			 	?????? 150???16???
			 */
				boolean isWeapon = false;
				int level = Integer.parseInt(raw_message.substring(0,raw_message.indexOf("???")));
//				int stat = Integer.parseInt(split[3].substring(4));
				int stat = 0;
//				int fireStat = Integer.parseInt(split[4].substring(5));
				int fireStat = 0;
//				int att = Integer.parseInt(split[5].substring(4));
				int att = 0;
//				int fireAtt = Integer.parseInt(split[6].substring(5));
				int fireAtt = 0;
//				int nowStar = Integer.parseInt(split[7].substring(5));
				int nowStar = 0;
				int targetStar = Integer.parseInt(raw_message.substring(raw_message.indexOf("???")+1,raw_message.indexOf("???")));
				if(level!=130&&level!=140&&level!=150&&level!=160&&level!=200) {
					replyMsg.setReply("?????????????????????130,140,150,160,200?????????");
					return replyMsg;
				}
				if((level==130&&targetStar>20)||targetStar>25) {
					replyMsg.setReply("???????????????????????????");
					return replyMsg;
				}
				if(stat<0||fireStat<0||att<0||fireAtt<0||nowStar<0||targetStar<0) {
					replyMsg.setReply("??????????????????");
					return replyMsg;
				}
				if(nowStar>targetStar) {
					replyMsg.setReply("?????????:??????????????????");
					return replyMsg;
				}
				int[] starForce = starForce(level,stat-fireStat,att-fireAtt,nowStar,targetStar,isWeapon);
				int finalStat = starForce[0]+fireStat;
				int finalAtt = starForce[1]+fireAtt;
//				String result = "???????????????"+level+" ??????" + stat + " ????????????" + fireStat + "\r\n"
//						+ "??????" + att + " ????????????" + fireAtt + "\r\n"
//						+ "????????????" + nowStar + " ????????????" + targetStar + "\r\n"
//						+ "????????????????????????\r\n"
//						+ "?????????????????????" +  finalStat + " ??????"+ finalAtt + "\r\n"
//						+ "????????????????????????" +  starForce[0] + " ??????"+ starForce[1];
				String result = level + "?????????" + targetStar + "????????????????????????" + finalStat + " ??????"+ finalAtt;
				replyMsg.setReply(result);
				return replyMsg;

			}
		} catch (Exception e) {
			replyMsg.setReply("??????????????????\r\n?????????????????????[??????][??????]\r\n" + 
					"eg?????????150???17???\r\n" + 
					"?????????????????????[??????][??????][??????]\r\n" + 
					"eg?????????160???13???428???");
			return replyMsg;
		}
				
		//???xxx
		if((raw_message.startsWith(MsbotConst.botName+"???")||raw_message.startsWith(MsbotConst.botName+"???"))&&raw_message.contains("[CQ:at")) {
    		try {
    			int aIndex = receiveMsg.getRaw_message().indexOf("[CQ:at,qq=")+10;
        		int bIndex = receiveMsg.getRaw_message().indexOf("]");
        		String findNumber = receiveMsg.getRaw_message().substring(aIndex,bIndex);
        		if(raw_message.contains("???")||raw_message.contains("???")||raw_message.contains("??????")||raw_message.contains("??????")) {
        			replyMsg.setAt_sender(true);
        			replyMsg.setReply("[CQ:image,file=img/buzhidao5.jpg]");
    				return replyMsg;
    			}
        		if(findNumber.equals(MsbotConst.masterId)||findNumber.equals("2419570484")) {
        			replyMsg.setAt_sender(true);
        			String saveTempImage = mvpImageService.saveTempImage("http://q1.qlogo.cn/g?b=qq&nk=" + receiveMsg.getUser_id() + "&s=3");
    				String throwSomeone = drawService.pouchSomeone(saveTempImage);
        			replyMsg.setReply(throwSomeone);
    				return replyMsg;
        		}
        		if(findNumber.equals(MsbotConst.botId)) {
        			replyMsg.setAt_sender(true);
        			String saveTempImage = mvpImageService.saveTempImage("http://q1.qlogo.cn/g?b=qq&nk=" + receiveMsg.getUser_id() + "&s=3");
    				String throwSomeone = drawService.pouchSomeone(saveTempImage);
    				replyMsg.setReply(throwSomeone);
    				return replyMsg;
        		}
				String saveTempImage = mvpImageService.saveTempImage("http://q1.qlogo.cn/g?b=qq&nk=" + findNumber + "&s=3");
				String throwSomeone = drawService.pouchSomeone(saveTempImage);
				replyMsg.setAt_sender(false);
				replyMsg.setReply("[CQ:at,qq=" + findNumber + "]" + throwSomeone);
				return replyMsg;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(raw_message.contains("??????")) {
			String mes;
			try {
				mes = drawService.kemomimiDraw();
			} catch (Exception e) {
				e.printStackTrace();
				mes = "?????????????????????";
			}
			replyMsg.setReply(mes);
			return replyMsg;
		}
		
		
		if(raw_message.contains("??????")||raw_message.contains("??????")||raw_message.contains("?????????")) {
			String mes;
			try {
				mes = drawService.startDrawMs();
			} catch (Exception e) {
				e.printStackTrace();
				mes = "?????????????????????";
			}
			replyMsg.setAt_sender(true);
			replyMsg.setReply(mes);
//			?????????????????????????????????????????????????????? ????????????????????????????????????????????????
//			return replyMsg;
		}
		
		
		//??????
		if(raw_message.startsWith(MsbotConst.botName+"??????")||raw_message.startsWith(MsbotConst.botName+" ??????")) {
			raw_message = raw_message.replaceAll("??????", "");
			raw_message = raw_message.replaceAll(" ", "");
			raw_message = raw_message.replaceAll(MsbotConst.botName, "");
			if(raw_message.isEmpty()) {
				replyMsg.setReply("???????????????+???????????????????????????????????????????????????????????????????????????????????????????????????????????????");
				return replyMsg;
			}
			
/**			
 * 			??????????????????????????????????????????????????????????????? ???????????????????????????????????? ???????????????
 * 			??????????????????????????????????????????????????????????????? ????????????????????????
 * 			??????????????????
  			 */
			
//			//???????????????
//			try {
//				String url = "http://mxd.web.sdo.com/web6/home/index.asp";
//				Document doc = Jsoup.connect(url).get();
//				Elements eleList = doc.select("div.news-list");
//				
//				for(Element element : eleList) {
//					Elements elementsByTag2 = element.getElementsByTag("li");
//					for(Element tempElement : elementsByTag2) {
//						System.out.println(tempElement.text());
//						if(tempElement.text().contains("??????")) {
//							Element first = tempElement.getElementsByAttribute("href").first();
//							url = first.attr("href").replaceAll("&amp;", "&");
//
//							if(url.startsWith("..")) {
//								url = "http://mxd.sdo.com/web6" + url.substring(2);
//							}
//							url = "http://mxd.sdo.com/web6" + element.getElementsByAttribute("href").first().attr("href").replaceAll("&amp;", "&").substring(2);
//							Document doc2 = Jsoup.connect(url).get();
//							
//							Element ele1 = doc2.getElementsByClass("innerTitle").first();
//							Element ele2 = doc2.getElementsByClass("innerText").first();
//							String message = "";
//							for(Element temp : ele1.children()) {
//								message = message + temp.text() + "\r\n";
//							}
////							message = message + "???????????????" + url + "\r\n";
//							if(ele2.text().length()>100) {
//								message = message + ele2.text().substring(0,100)+"...";
//							}else {
//								message = message + ele2.text();
//							}
//							
//							if(ele2.getElementsByTag("img").toString().length()>0) {
//								Elements elementsByTag = ele2.getElementsByTag("img");
//								for(Element temp : elementsByTag) {
//									String imageUrl = mvpImageService.saveTempImage(temp.attr("src"));
//									message = message + "[CQ:image,file="+imageUrl+"]";
//								}
//								
//							}
//							System.out.println(message);
//							replyMsg.setReply(message);
//							return replyMsg;
//						}
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			//???????????????
			String url = "http://mxd.sdo.com/web6/news/newsList.asp?wd=" + raw_message +"&CategoryID=a";
			
			try {
				Document doc = Jsoup.connect(url).get();
				Element element = doc.select(".newList").first();
				element.getElementsByAttribute("href").first();
				url = "http://mxd.sdo.com/web6" + element.getElementsByAttribute("href").first().attr("href").replaceAll("&amp;", "&").substring(2);
				Document doc2 = Jsoup.connect(url).get();
				
				Element ele1 = doc2.getElementsByClass("innerTitle").first();
				Element ele2 = doc2.getElementsByClass("innerText").first();
				String message = "";
				for(Element temp : ele1.children()) {
					message = message + temp.text() + "\r\n";
				}
				message = message + "???????????????" + url + "\r\n";
				if(ele2.text().length()>100) {
					message = message + ele2.text().substring(0,100)+"...";
				}else {
					message = message + ele2.text();
				}
				if(ele2.getElementsByTag("img").toString().length()>0) {
					Elements elementsByTag = ele2.getElementsByTag("img");
					for(Element temp : elementsByTag) {
						String imageUrl = mvpImageService.saveTempImage(temp.attr("src"));
						message = message + "[CQ:image,file="+imageUrl+"]";
					}
				}
				replyMsg.setReply(message);
				return replyMsg;
			} catch (Exception e) {
				replyMsg.setReply("????????????");
				return replyMsg;
			}
		}
		
		if(raw_message.startsWith(MsbotConst.botName+"??????")||raw_message.startsWith(MsbotConst.botName+"??????")) {
			String reply = new String();
			reply = msgZbCalculate.msgDeZb(receiveMsg.getUser_id());
			replyMsg.setReply(reply);
			return replyMsg;
		}
		
		if(raw_message.contains("????????????")) {
			String[] list = {"??????","??????","????????????","?????????","??????","????????????"};
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// ???????????????????????????????????????????????????
		    Calendar nowTime = Calendar.getInstance();
		    Calendar beginTime = Calendar.getInstance();
		    nowTime.set(Calendar.HOUR_OF_DAY, 0);
		    nowTime.set(Calendar.MINUTE, 0);
		    nowTime.set(Calendar.SECOND, 0);
		    beginTime.set(2021,4,12,0,0);
		    int count = 0;
		    //??????????????? ???????????????
		    while(nowTime.getTimeInMillis()-beginTime.getTimeInMillis()>1000*60*60*24*14) {
		    	beginTime.add(Calendar.DAY_OF_MONTH, 14);
		    	count = (count+1)%6;
		    }
		    //???????????? ?????? ???????????????
		    StringBuffer message = new StringBuffer();
		    message.append("????????????????????????");
		    for(int i=0;i<6;i++) {
		    	message.append("\r\n");
		    	message.append(sdf.format(beginTime.getTimeInMillis())).append(" ");
		    	beginTime.add(Calendar.DAY_OF_MONTH, 13);
		    	message.append(sdf.format(beginTime.getTimeInMillis())).append("\r\n");;
		    	beginTime.add(Calendar.DAY_OF_MONTH, 1);
		    	message.append(list[count]);
		    	count = (count+1)%6;
		    }
		    replyMsg.setReply(message.toString());
		    return replyMsg;
		}
		
		if(raw_message.replace(" ", "").equals(MsbotConst.botName)) {
			replyMsg.setAuto_escape(false);
			replyMsg.setAt_sender(false);
			replyMsg.setReply("(????????????)????????????~");
			return replyMsg;
		}
		
		if(raw_message.replaceAll(MsbotConst.botName, "").replaceAll(" ","").equals("")) {
			raw_message = raw_message.replaceAll(" ","");
			raw_message = raw_message.replaceFirst(MsbotConst.botName, "");
			raw_message = raw_message.replaceFirst(MsbotConst.botName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("????????????");
				return replyMsg;
			}
			raw_message = raw_message.replaceFirst(MsbotConst.botName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("??????????????????");
				return replyMsg;
			}
			raw_message = raw_message.replaceFirst(MsbotConst.botName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("??????????????????");
				return replyMsg;
			}
			raw_message = raw_message.replaceFirst(MsbotConst.botName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("??????????????????");
				return replyMsg;
			}
			raw_message = raw_message.replaceFirst(MsbotConst.botName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("??????????????????");
				return replyMsg;
			}
			replyMsg.setAt_sender(false);
			replyMsg.setReply(MsbotConst.botName.substring(0,1)+"nm");
			return replyMsg;
		}
		
		if(raw_message.replaceAll(MsbotConst.botName, "").replaceAll(" ","").replaceAll("???","").equals("")) {
			raw_message = MsbotConst.botName+"??????????????????";
		}
	
		//??????
		if(raw_message.startsWith(MsbotConst.botName+"??????")||raw_message.startsWith(MsbotConst.botName+" ??????")) {
			GroupMsg groupMsg = new GroupMsg();
			groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
			String reply = new String();
			try {
				reply = msgZbCalculate.msgZb(receiveMsg.getUser_id());
				replyMsg.setReply(reply);
				replyMsg.setAt_sender(true);
				return replyMsg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
			
//			String reply = new String();
//			reply = msgZbCalculate.msgZb(receiveMsg.getUser_id());
//			replyMsg.setReply(reply);
//			return replyMsg;
		}
		
		try {
			if(raw_message.contains("^")) {
				replyMsg.setReply(MsbotConst.botName+"?????????????????????");
				return replyMsg;
			}else if((raw_message.contains("??")||raw_message.contains("??")||raw_message.contains("+")||raw_message.contains("-")||raw_message.contains("*")||raw_message.contains("/"))){
				raw_message = raw_message.replace("??", "*");
				raw_message = raw_message.replace("??", "/");
				ScriptEngineManager manager = new ScriptEngineManager();
				ScriptEngine engine = manager.getEngineByName("js");
				Object result = engine.eval(raw_message.substring(raw_message.indexOf(MsbotConst.botName)+2));
				//??????????????????
				DecimalFormat myformat = new DecimalFormat();
				myformat.applyPattern("##,###.00000");
				Double d = Double.valueOf(result.toString());
				String formatResult = myformat.format(d);
				if(formatResult.startsWith(".")) {
					formatResult = "0"+formatResult;
				}
				while(formatResult.contains(".")&&(formatResult.endsWith("0")||formatResult.endsWith("."))) {
					formatResult = formatResult.substring(0, formatResult.length()-1);
				}
				replyMsg.setReply(raw_message.substring(receiveMsg.getRaw_message().indexOf(MsbotConst.botName)+2)+"="+formatResult);
				return replyMsg;
			}
		}catch (Exception e) {
		
		}
		
		//????????????
		List<Msg> list = new ArrayList<Msg>();
		List<Msg> rep = new ArrayList<Msg>();
		String command = raw_message.substring(2);
		 while (it.hasNext()) {
			 msg = it.next();
			 if (command.toLowerCase().contains(msg.getQuestion().toLowerCase())) {
				 list.add(msg);
			 }
		 }
		 if(list.size()!=0) {
			 //???????????????
			 int similar = 100;
			 //??????list
			 for(Msg tempMsg:list){
				 //???????????????
				 int similarity = getResult(tempMsg.getQuestion(),command);
				 //??????????????? ??????????????? ????????????
				 if(similarity<similar) {
					 similar = similarity;
					 rep.clear();
					 rep.add(tempMsg);
				 }else if(similarity==similar){
					rep.add(tempMsg);
				 }
		        }
			 Random r = new Random();
			 int random = r.nextInt(rep.size());
			 replyMsg.setAt_sender(false);
			 Msg msg2 = rep.get(random);
			 if(msg2.getLink()==null) {
				 replyMsg.setReply(msg2.getAnswer());
			 }else {
				 GroupMsg groupMsg = new GroupMsg();
				 groupMsg.setAuto_escape(false);
				 groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
				 groupMsg.setMessage(msg2.getAnswer());
				 groupMsgService.sendGroupMsg(groupMsg);
				 String[] split = msg2.getLink().split("#abcde#");
				 for(String temp : split) {
					 try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					 GroupMsg groupMsg2 = new GroupMsg();
					 groupMsg2.setAuto_escape(false);
					 groupMsg2.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
					 groupMsg2.setMessage(temp);
					 groupMsgService.sendGroupMsg(groupMsg2);
				 }
				 return null;
			 }

			 return replyMsg;
		 }
		 
		 if(MsbotConst.moliKey2!=null&&MsbotConst.moliSecret2!=null&&!MsbotConst.moliKey2.isEmpty()&&!MsbotConst.moliSecret2.isEmpty()) {
			 if(!raw_message.contains("[CQ")) {
					String tuLingMsg = groupMsgService.MoliMsg2(command, Math.abs(receiveMsg.getUser_id().hashCode())+"", receiveMsg.getSender().getNickname());
					@SuppressWarnings("unchecked")
					Map<String,Object> result = (Map<String, Object>) JSON.parse(tuLingMsg); 
					System.out.println(tuLingMsg);
					if((result.get("message")+"").contains("????????????")) {
						String reply = tuLingMsg.substring(tuLingMsg.indexOf("content")+10,tuLingMsg.indexOf("\",\"typed\":"));
						replyMsg.setReply(reply);
						return replyMsg;
					}
				}
			}
		
		 
		 
//		if(!raw_message.contains("[CQ")) {
//			//???????????????
//			HashMap<String, Object> map = new HashMap<>();
//			//reqType
//			map.put("reqType", 0);
//			//perception ??????
//			HashMap<String, Object> textMap = new HashMap<>();
//			HashMap<String, Object> contentMap = new HashMap<>();
//			contentMap.put("text", raw_message);
//			textMap.put("inputText", contentMap);
//			map.put("perception", textMap);
//			//key
//			HashMap<String, Object> userMap = new HashMap<>();
//			userMap.put("apiKey", );
//			userMap.put("userId",Math.abs(receiveMsg.getSender().getUser_id().hashCode()) +"");
//			map.put("userInfo", userMap);
//			//??????????????????
//			String json = JSONObject.toJSONString(map);
//			System.out.println(json);
//			String tuLingMsg = groupMsgService.tuLingMsg(json);
//			System.out.println(tuLingMsg);
//			//?????????????????? ????????????
//			@SuppressWarnings("unchecked")
//			Map<String,Object> result = (Map<String, Object>) JSON.parse(tuLingMsg); 
//			
//			@SuppressWarnings("unchecked")
//			Map<String,Object> intent = (Map<String, Object>) result.get("intent"); 
//			Integer code = (Integer) intent.get("code"); 
//			if(code>9000||code<3000) {
//				@SuppressWarnings("unchecked")
//				List<Map<String,Object>> results = (List<Map<String,Object>>) result.get("results");
//				String finalResult = "";
//				for(Map<String,Object> temp : results) {
//					@SuppressWarnings("unchecked")
//					Map<String,String> object = (Map<String, String>) temp.get("values");
//					finalResult += object.get("text");
//				}
//				replyMsg.setAt_sender(true);
//				replyMsg.setReply(finalResult);
//				return replyMsg;
//			}
//		}
//			
		
		
		
		Random r = new Random();
		int random = r.nextInt(6) + 1;
		if(random==1) {
			replyMsg.setAt_sender(false);
			replyMsg.setReply("[CQ:image,file=img/buzhidao1.gif]");
		}else if(random==2) {
			replyMsg.setAt_sender(false);
			replyMsg.setReply("[CQ:image,file=img/buzhidao2.gif]");
		}else if(random==3) {
			replyMsg.setAt_sender(false);
			replyMsg.setReply("[CQ:image,file=img/buzhidao3.png]");
		}else if(random==4){
			replyMsg.setAt_sender(false);
			replyMsg.setReply("[CQ:image,file=img/buzhidao4.png]");
		}else if(random==5){
			replyMsg.setAt_sender(false);
			replyMsg.setReply("[CQ:image,file=img/buzhidao5.png]");
		}else{
			replyMsg.setAt_sender(false);
			replyMsg.setReply("[CQ:image,file=img/buzhidao2.gif]");
		}
        return replyMsg;
	}
	
	public static int[] starForceDesc(int level,int stat,int att,int star) {
		Map<Integer, Map<String, int[]>> starForceDataAfter16 = starForceDataAfter16();
		Map<String, int[]> map = starForceDataAfter16.get(level);
		while(star>0) {
			stat = starForceStatDesc(star, stat, map.get("stat"));
			att = starForceAttDesc(star,att,map.get("attWeapon"));
			star--;
		}
		return new int[]{stat,att};
	}
	
	public static int[] starForce(int level,int stat,int att,int nowStar,int targetStar,Boolean isWeapon) {
		Map<Integer, Map<String, int[]>> starForceDataAfter16 = starForceDataAfter16();
		Map<String, int[]> map = starForceDataAfter16.get(level);
		if(isWeapon) {
			while(nowStar<targetStar) {
				stat = starForceStat(nowStar,stat,map.get("stat"));
				att = starForceAtt(nowStar,att,map.get("attWeapon"));
				nowStar++;
			}
		}else {
			while(nowStar<targetStar) {
				stat = starForceStat(nowStar,stat,map.get("stat"));
				att = starForceAttNotWeapon(nowStar,att,map.get("att"));
				nowStar++;
			}
		}
		return new int[]{stat,att};
	}
	
	private static int starForceStat(int star,int totalStat,int[] stat16) {
		if(star<5) {
			totalStat = totalStat + 2;
		}else if(star<15) {
			totalStat = totalStat + 3;
		}else{
			totalStat = totalStat + stat16[star-15];
		}
		return totalStat;
	}
	
	private static int starForceStatDesc(int star,int totalStat,int[] stat16) {
		if(star<=5) {
			totalStat = totalStat - 2;
		}else if(star<=15) {
			totalStat = totalStat - 3;
		}else{
			totalStat = totalStat - stat16[star-16];
		}
		return totalStat;
	}
	
	private static int starForceAttNotWeapon(int star,int totalAtt,int[] att16) {
		if(star>=15) {
			totalAtt = totalAtt + att16[star-15];
		}
		return totalAtt;
	}
	
	private static int starForceAtt(int star,int totalAtt,int[] att16) {
		if(star<15) {
			totalAtt = totalAtt + totalAtt/50 + 1;
		}else {
			totalAtt = totalAtt + att16[star-15];
		}
		return totalAtt;
	}
	
	private static int starForceAttDesc(int star,int totalAtt,int[] att16) {
		int before = totalAtt;
		if(star<=15) {
			if(totalAtt%50<totalAtt/50) {
				totalAtt = totalAtt - totalAtt/50;
			}else {
				totalAtt = totalAtt - totalAtt/50 - 1;
			}
			
		}else {
			totalAtt = totalAtt - att16[star-16];
		}
		System.out.println("star:" + star + " " + totalAtt + " = " + before + " " + (before-totalAtt));
		return totalAtt;
	}
	
	private static Map<Integer, Map<String, int[]>> starForceDataAfter16() {
		Map<Integer,Map<String,int[]>> map = new HashMap<>();
		
		Map<String, int[]> map130 = new HashMap<>();
		map130.put("stat", new int[] {7,7,7,7,7});
 		map130.put("att", new int[] {7,8,9,10,11});
 		map130.put("attWeapon", new int[] {6,7,7,8,9});
 		map.put(130, map130);
 		
 		Map<String,int[]> map140 = new HashMap<>();
		map140.put("stat", new int[] {9,9,9,9,9,9,9,0,0,0});
 		map140.put("att", new int[] {8,9,10,11,12,13,15,17,19,21});
 		map140.put("attWeapon", new int[] {7,8,8,9,10,11,12,30,31,32});
 		map.put(140, map140);
 		
 		Map<String,int[]> map150 = new HashMap<>();
		map150.put("stat", new int[] {11,11,11,11,11,11,11,0,0,0});
 		map150.put("att", new int[] {9,10,11,12,13,14,16,18,20,22});
 		map150.put("attWeapon", new int[] {8,9,9,10,11,12,13,31,32,33});
 		map.put(150, map150);
 		
 		Map<String,int[]> map160 = new HashMap<>();
 		map160.put("stat", new int[] {13,13,13,13,13,13,13,0,0,0});
 		map160.put("att", new int[] {10,11,12,13,14,15,17,19,21,23});
 		map160.put("attWeapon", new int[] {9,9,10,11,12,13,14,32,33,34});
 		map.put(160, map160);
 		
 		Map<String,int[]> map200 = new HashMap<>();
 		map200.put("stat", new int[] {15,15,15,15,15,15,15,0,0,0});
 		map200.put("att", new int[] {12,13,14,15,16,17,19,21,23,25});
 		map200.put("attWeapon", new int[] {13,13,14,14,15,16,17,34,35,36});
 		map.put(200, map200);
		
		return map;
	}
	
	
	
	private boolean isAdminMsg(String user_id) {
		for(String temp : MsbotConst.managerId) {
			if(temp.equals(user_id)) {
				return true;
			}
		}
		return false;
	}
	

	//someone leave
	private ReplyMsg handLeave(NoticeMsg noticeMsg) {
		if(!noticeMsg.getSub_type().equals("leave")) {
			return null;
		}
		GroupMsg groupMsg = new GroupMsg();
		groupMsg.setAuto_escape(false);
		groupMsg.setGroup_id(Long.parseLong(noticeMsg.getGroup_id()));
		groupMsg.setMessage("[CQ:image,file=img/leave.png]");
		System.out.println(noticeMsg.toString());
		groupMsgService.sendGroupMsg(groupMsg);
		return null;
	}
	
	//???????????????
	private ReplyMsg handWelcome(NoticeMsg noticeMsg) {
//		????????????
		if(!noticeMsg.getSub_type().equals("approve")) {
			return null;
		}
		
//		GroupMsg groupMsg = new GroupMsg();
//		String message = "";
		//???????????????
		//????????????welcome
		Random r = new Random();
		List<Msg> msgList = msgRepository.findMsgByExtQuestion("????????????welcome");
		int random = r.nextInt(msgList.size());
		Msg msg = msgList.get(random);
		
		GroupMsg gm = new GroupMsg();
		gm.setMessage(msg.getAnswer());
		gm.setGroup_id(Long.parseLong(noticeMsg.getGroup_id()));
		groupMsgService.sendGroupMsg(gm);
		return null;
	}
	
	private static double defAndign(double def, double ign) {
		double i = Double.parseDouble(String.format("%.2f", 100-(def-ign*def/100)));
		if(i<0) {
			i=0;
		}
		if(i>100) {
			i=100;
		}
		return i;
	}
	 public static int getResult(String A, String B) {
	        if(A.equals(B)) {
	            return 0;
	        }
	        //dp[i][j]????????????A??????i????????????B??????j??????????????????????????????
	        int[][] dp = new int[A.length() + 1][B.length() + 1];
	        for(int i = 1;i <= A.length();i++)
	            dp[i][0] = i;
	        for(int j = 1;j <= B.length();j++)
	            dp[0][j] = j;
	        for(int i = 1;i <= A.length();i++) {
	            for(int j = 1;j <= B.length();j++) {
	                if(A.charAt(i - 1) == B.charAt(j - 1))
	                    dp[i][j] = dp[i - 1][j - 1];
	                else {
	                    dp[i][j] = Math.min(dp[i - 1][j] + 1,
	                            Math.min(dp[i][j - 1] + 1, dp[i - 1][j - 1] + 1));
	                }
	            }
	        }
	        return dp[A.length()][B.length()];
	    }
	
}
