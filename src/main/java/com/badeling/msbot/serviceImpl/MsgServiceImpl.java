package com.badeling.msbot.serviceImpl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.badeling.msbot.controller.ChatGpt;
import com.badeling.msbot.controller.MsgZbCalculate;
import com.badeling.msbot.domain.GlobalVariable;
import com.badeling.msbot.domain.GroupMsg;
import com.badeling.msbot.domain.NoticeMsg;
import com.badeling.msbot.domain.ReRead;
import com.badeling.msbot.domain.ReReadMsg;
import com.badeling.msbot.domain.ReceiveMsg;
import com.badeling.msbot.domain.ReplyMsg;
import com.badeling.msbot.domain.Result;
import com.badeling.msbot.entity.GroupInfo;
import com.badeling.msbot.entity.GroupMember;
import com.badeling.msbot.entity.Message;
import com.badeling.msbot.entity.Msg;
import com.badeling.msbot.entity.MsgNoPrefix;
import com.badeling.msbot.entity.QuizOzAnswer;
import com.badeling.msbot.entity.QuizOzQuestion;
import com.badeling.msbot.entity.RankInfo;
import com.badeling.msbot.entity.RereadSentence;
import com.badeling.msbot.entity.RereadTime;
import com.badeling.msbot.entity.RoleAtt;
import com.badeling.msbot.entity.RoleDmg;
import com.badeling.msbot.entity.Score;
import com.badeling.msbot.entity.SellAndBuy;
import com.badeling.msbot.repository.GroupInfoRepository;
import com.badeling.msbot.repository.GroupMemberRepository;
import com.badeling.msbot.repository.MessageRepository;
import com.badeling.msbot.repository.MsgNoPrefixRepository;
import com.badeling.msbot.repository.MsgRepository;
import com.badeling.msbot.repository.QuizOzAnswerRepository;
import com.badeling.msbot.repository.QuizOzQuestionRepository;
import com.badeling.msbot.repository.RankInfoRepository;
import com.badeling.msbot.repository.RereadSentenceRepository;
import com.badeling.msbot.repository.RereadTimeRepository;
import com.badeling.msbot.repository.RoleAttRepository;
import com.badeling.msbot.repository.RoleDmgRepository;
import com.badeling.msbot.repository.ScoreRepository;
import com.badeling.msbot.repository.SellAndBuyRepository;
import com.badeling.msbot.service.ChannelService;
import com.badeling.msbot.service.DrawService;
import com.badeling.msbot.service.GroupMsgService;
import com.badeling.msbot.service.MsgService;
import com.badeling.msbot.service.MvpImageService;
import com.badeling.msbot.service.PrivateService;
import com.badeling.msbot.service.RankService;
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
	private RoleAttRepository roleAttRepository;
	
	@Autowired
	private PrivateService privateService;
	
	@Autowired
	private MsgNoPrefixRepository msgNoPrefixRepository;
	
	@Autowired
	RankInfoRepository rankInfoRepository;
	
	@Autowired
	QuizOzQuestionRepository quizOzQuestionRepository;
	
	@Autowired
	QuizOzAnswerRepository quizOzAnswerRepository;
	
	@Autowired
	SellAndBuyRepository sellAndBuyRepository;
	
	@Autowired
	GroupInfoRepository groupInfoRepository;
	
	@Autowired
	MessageRepository messageRepository;
	
	@Autowired
	ScoreRepository scoreRepository;
	
	@Autowired
	GroupMemberRepository groupMemberRepository;
	
	@Override
	public ReplyMsg receive(String msg) {
		ReceiveMsg receiveMsg = null;
		//判定是否有重复请求
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
        	}else if(msg.contains("\"notice_type\":\"group_card\"")) {
        		//修改群名片的事件 但目前使用的版本没有上传该事件 故暂时搁置
//        		NoticeMsg noticeMsg = new ObjectMapper().readValue(msg, NoticeMsg.class);
//        		return handModifyCard(noticeMsg);
        		return null;
        	}else {
        		return null;
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
			//私聊
//        if(receiveMsg.getMessage_type().equals("private")&&receiveMsg.getUser_id().equals(MsbotConst.masterId)) {
//      	System.err.println(receiveMsg.toString());
//        	return handlePrivateMsg(receiveMsg);
//        }
        
        //接收消息统计
        try {
        	int length = receiveMsg.getRaw_message().length();
        	if(length>2000) {
        		length = 2000;
        	}
        	Message ms = new Message();
        	ms.setGroup_id(receiveMsg.getGroup_id());
        	ms.setUser_id(receiveMsg.getUser_id());
        	ms.setRaw_message(receiveMsg.getRaw_message().substring(0,length));
        	ms.setTime(System.currentTimeMillis());
        	messageRepository.save(ms);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    	//过滤自己的消息 、 统计自己的消息需设置report-self-message:true
    	if(receiveMsg.getUser_id()!=null&&receiveMsg.getUser_id().equals(receiveMsg.getSelf_id())) {
    		return null;
    	}
    	
        //黑名单的人
        for(String temp : MsbotConst.blackList) {
        	if(receiveMsg.getUser_id().equals(temp)) {
        		return null;
        	}
        }
        
        if(receiveMsg.getRaw_message().startsWith(MsbotConst.botName)) {
        	System.out.println(receiveMsg.toString());
        	return handleNameMsg(receiveMsg);
        }else if(receiveMsg.getRaw_message().startsWith("[CQ:at,qq="+MsbotConst.botId+"]")){
           	receiveMsg.setRaw_message(receiveMsg.getRaw_message().replace("[CQ:at,qq="+receiveMsg.getSelf_id()+"]", MsbotConst.botName));
        	System.out.println(receiveMsg.toString());
        	return handleNameMsg(receiveMsg);
        }else if(receiveMsg.getRaw_message().startsWith(MsbotConst.gptName)) {
        	String raw_message = receiveMsg.getRaw_message();
			ReplyMsg gptForUser = ChatGpt.getGptForUser(raw_message);
			GroupMsg groupMsg = new GroupMsg();
			groupMsg.setAuto_escape(false);
			groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
			groupMsg.setMessage(gptForUser.getReply());
			groupMsgService.sendGroupMsg(groupMsg);
        	return null;
        }else if((receiveMsg.getRaw_message().contains("气象")||receiveMsg.getRaw_message().contains("MVP"))&&receiveMsg.getRaw_message().length()<=40){
        	System.out.println(receiveMsg.toString());
        	return null;
//        	return handleMvpMsg(receiveMsg);
        }else if(receiveMsg.getRaw_message().contains("[CQ:image,file=")){
        	//识别气象图
        	System.out.println(receiveMsg.toString());
        	if(receiveMsg.getRaw_message().contains("高精度识图")) {
        		return handRecognize2(receiveMsg);
        	}
        	if(receiveMsg.getRaw_message().contains("识图")) {
        		return handRecognize(receiveMsg);
        	}
        	if(receiveMsg.getRaw_message().contains("翻译")) {
        		return handTransMsg(receiveMsg);
        	}
        	if(receiveMsg.getGroup_id().equals(MsbotConst.group_oz)) {
        		return handRecognizeOz39(receiveMsg);
        	}
        }else if(receiveMsg.getRaw_message().length()>=2&&receiveMsg.getRaw_message().substring(0,2).contains("翻译")){
        	System.out.println(receiveMsg.toString());
        	return handTransMsg(receiveMsg);
        }else if(receiveMsg.getRaw_message().length()>=4&&receiveMsg.getRaw_message().startsWith("联盟查询")) {
        	return handLegionRank(receiveMsg);
        }else if(receiveMsg.getRaw_message().length()>=4&&receiveMsg.getRaw_message().startsWith("查询绑定")) {
//        	查询绑定badeling
        	return handAddRankName(receiveMsg);
        }else if(receiveMsg.getRaw_message().length()>=2&&receiveMsg.getRaw_message().contains("查成分")){
        	return handMemberAtt(receiveMsg);
        }
        //收币 or 卖币
        Pattern r = Pattern.compile(".*\\d+[e|E|\\亿].*");
		Matcher m = r.matcher(receiveMsg.getRaw_message());
		boolean matches = m.matches();
    	if(matches) {
    		handMemberSellAndBuy(receiveMsg);
    	}
        
    	//re-read
    	handReplyMsg(receiveMsg);
    	receiveMsg.setRaw_message(receiveMsg.getMessage());
    	return handRereadMsg(receiveMsg);
        
	}
	
	private ReplyMsg handMemberAtt(ReceiveMsg receiveMsg) {
		ReplyMsg replyMsg = new ReplyMsg();
		String findNumber = null;
    	
    	StringBuffer reply = new StringBuffer();
    	if(receiveMsg.getRaw_message().contains("[CQ:at,qq=")) {
    		int aIndex = receiveMsg.getRaw_message().indexOf("[CQ:at,qq=")+10;
    		int bIndex = receiveMsg.getRaw_message().indexOf("]");
    		findNumber = receiveMsg.getRaw_message().substring(aIndex,bIndex);    		
    	}else {
    		findNumber = receiveMsg.getUser_id();
    	}
    	reply.append("[CQ:at,qq=");
		reply.append(findNumber);
		reply.append("]的成分查询结果如下：\r\n");
    	
		reply.append("入群时间：");
		List<GroupMember> findGroupMemberInfo = groupMemberRepository.findGroupMemberInfo(receiveMsg.getGroup_id(), findNumber);
		String join_time = findGroupMemberInfo.get(0).getJoin_time();
		String year = join_time.substring(0,4);
		String month = join_time.substring(4,6);
		String day = join_time.substring(6);
		if(month.startsWith("0")) {
			month = month.substring(1);
		}
		if(day.startsWith("0")) {
			day = day.substring(1);
		}
		reply.append((year+"年"+month+"月"+day+"日"));
		
		List<Score> findScoreById = scoreRepository.findScoreById(findNumber, receiveMsg.getGroup_id());
		int lastweek_score = 0;
		int total_score = 0;
		for(Score s : findScoreById) {
			total_score = total_score + s.getScore();
			if(s.getTime()!=99999999) {
				lastweek_score = lastweek_score + s.getScore();
			}
		}
		reply.append("\r\n近期活跃度：");
		reply.append(lastweek_score);
		reply.append("\r\n累计活跃度：");
		reply.append(total_score);
		if(total_score<0||lastweek_score<0) {
			reply.append("\r\n------------\r\n一眼咩啊，鉴定为内鬼");
		}
    	replyMsg.setReply(reply.toString());
		return replyMsg;
	}


	@SuppressWarnings("unused")
	private ReplyMsg handModifyCard(NoticeMsg noticeMsg) {
		ReplyMsg replyMsg = new ReplyMsg();
		replyMsg.setReply("[sandbox]" + noticeMsg.getCard_old()+" -> "+noticeMsg.getCard_new());
		return replyMsg;
	}

	private void handMemberSellAndBuy(ReceiveMsg receiveMsg) {
		String temp = receiveMsg.getRaw_message();
		Pattern r = Pattern.compile(".*\\d+.*[出].*\\d+[e|E|\\亿].*");
		Matcher m = r.matcher(receiveMsg.getRaw_message());
		boolean matches = m.matches();
		r = Pattern.compile(".*[收].*\\d+[e|E|\\亿].*");
		m = r.matcher(receiveMsg.getRaw_message());
		boolean matches2 = m.matches();
		try {
			if(temp.length()<30&&!temp.contains("突破")&&!temp.contains("上限")) {
				if(matches) {
					handMemberSellAndBuy2(receiveMsg,"sell");
				}else if(matches2) {
					handMemberSellAndBuy2(receiveMsg,"buy");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	private void handMemberSellAndBuy2(ReceiveMsg receiveMsg, String type) throws Exception {
		//存储收售信息
		SellAndBuy sab = new SellAndBuy();
		sab.setGroup_id(receiveMsg.getGroup_id());
		sab.setType(type);
		sab.setUser_id(receiveMsg.getUser_id());
		String name = receiveMsg.getSender().getCard();
		if(name==null||name.isEmpty()) {
			name = receiveMsg.getSender().getNickname();
		}
		sab.setUser_name(name);
		sab.setGoods(receiveMsg.getRaw_message());
		sab.setTime(System.currentTimeMillis()+"");
		SellAndBuy sab2 = sellAndBuyRepository.findUserByIdAndType(sab.getUser_id(), sab.getType());
		if(sab2!=null) {
			sellAndBuyRepository.deleteById(sab2.getId());
		}
		sellAndBuyRepository.save(sab);
		
		String type2 = "";
		if(type.equals("sell")) {
			type2 = "buy";
		}else {
			type2 = "sell";
		}
		//读取已有收售信息
		List<SellAndBuy> sabList = sellAndBuyRepository.findSabByType(type2);
		if(sabList.size()>0) {
			//list排序 按时间排序 家族分类
			List<List<SellAndBuy>> totList = new ArrayList<>();
			Map<String,Integer> map = new HashMap<>();
			//自己家族排最前
			map.put(receiveMsg.getGroup_id(), 0);
			List<SellAndBuy> own = new ArrayList<>();
			totList.add(own);
			int point = 1;
			for(SellAndBuy sabTemp : sabList) {
				Integer integer = map.get(sabTemp.getGroup_id());
				if(integer==null) {
					map.put(sabTemp.getGroup_id(), point);
					integer = point;
					List<SellAndBuy> list = new ArrayList<>();
					totList.add(list);
					point++;
				}
				List<SellAndBuy> list = totList.get(integer);
				list.add(sabTemp);
				totList.set(integer, list);
			}
			
//			读取家族列表
			Iterable<GroupInfo> findAll = groupInfoRepository.findAll();
			Map<String,String> groupList = new HashMap<>();
			for(GroupInfo gi : findAll) {
				if(gi.getGroup_memo()==null||gi.getGroup_id().isEmpty()||gi.getGroup_memo().equals("null")) {
					groupList.put(gi.getGroup_id(), gi.getGroup_name());
				}else {
					groupList.put(gi.getGroup_id(), gi.getGroup_memo());
				}
				
			}
//			家族 角色名 出 xxx 游戏币 xx分钟/小时前
			StringBuffer result = new StringBuffer();
			result.append("[CQ:at,qq=").append(receiveMsg.getUser_id()).append("]");
			long currentTimeMillis = System.currentTimeMillis();
			for(List<SellAndBuy> sabTempList : totList) {
				if(sabTempList.size()>0) {
					result.append("\r\n").append("【").append(groupList.get(sabTempList.get(0).getGroup_id())).append("】");
					for(SellAndBuy sabTemp : sabTempList) {
						long time = (currentTimeMillis - Long.parseLong(sabTemp.getTime()))/1000/60;
						String time2 = "";
						if(time<60) {
							time2 = time + "分钟前";
						}else if(time<1440) {
							time2 = time/60 + "小时前";
						}else {
							time2 = "1天前";
						}
						result.append("\r\n").append(sabTemp.getUser_name()).append(":").append(sabTemp.getGoods())
						.append(" ").append(time2);
					}
				}
			}
			GroupMsg groupMsg = new GroupMsg();
			groupMsg.setAuto_escape(false);
			groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
			groupMsg.setMessage(result.toString());
//			groupMsg.setGroup_id(Long.parseLong(GroupIdConst.GroupTest1));
			groupMsgService.sendGroupMsg(groupMsg);
		}
		
	}

	private ReplyMsg handRecognizeOz39(ReceiveMsg receiveMsg) {
		//识图
		String[] result = mvpImageService.handHigherImageMsg(receiveMsg);
		//接受数据
		String raw_message = "";
		String reply = "";
		try {
			@SuppressWarnings("unchecked")
			Map<String,Object> backMessage = (Map<String, Object>) JSONObject.parse(result[0]);
			@SuppressWarnings("unchecked")
			List<Map<String,String>> list = (List<Map<String, String>>) backMessage.get("words_result");
			
			int count = list.size();
			
			for(int i=0;i<count-4;i++) {
				if(i==count-5) {
					if(list.get(i).get("words").contains("1.")) {
						break;
					}
				}
				raw_message = raw_message + " " + list.get(i).get("words");
			}
			
			List<QuizOzQuestion> qozList = quizOzQuestionRepository.findAllQoz();
			System.out.println("原始信息:"+raw_message);
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
			
			while(!reply.isEmpty()&&reply.startsWith(" ")) {
				reply = reply.substring(1);
			}
			
			reply = reply + "MatchQue : "+MatchQoz.getQuestion()+"\r\n \r\n";
			
//			String a = list.get(count-4).get("words").replaceAll("  ", " ").replaceAll("  ", " ");
//			String b = list.get(count-3).get("words").replaceAll("  ", " ").replaceAll("  ", " ");
//			String c = list.get(count-2).get("words").replaceAll("  ", " ").replaceAll("  ", " ");
//			String d = list.get(count-1).get("words").replaceAll("  ", " ").replaceAll("  ", " ");

			Set<QuizOzAnswer> answers = MatchQoz.getAnswers();			
			reply = reply + "所有答案 : ";
			//排序
			List<String> answer_list = new ArrayList<>();
			for(QuizOzAnswer temp : answers) {
				answer_list.add(temp.getAnswer());
			}
			Collections.sort(answer_list);
			int k = 16;
			String matchQoa = null;
			for(int j=0;j<answer_list.size();j++) {
				String answer = answer_list.get(j);
				reply = reply + answer;
				if(j<answer_list.size()-1) {
					reply = reply + " | ";
				}else {
					reply = reply + "\r\n \r\n";
				}
				
				for(Map<String,String> temp : list) {
					String a = temp.get("words").replaceAll("  ", " ").replaceAll("  ", " ");
					int compare = getResult(answer,a);
					if(compare<=k) {
						k=compare;
						matchQoa = answer;
					}
				}
				
//				if(getResult(next.getAnswer(),a)<=k) {
//					k=getResult(next.getAnswer(),a);
//					MatchQoa = next;
//				}
//				if(getResult(next.getAnswer(),b)<=k) {
//					k=getResult(next.getAnswer(),b);
//					MatchQoa = next;
//				}
//				if(getResult(next.getAnswer(),c)<=k) {
//					k=getResult(next.getAnswer(),c);
//					MatchQoa = next;
//				}
//				if(getResult(next.getAnswer(),d)<=k) {
//					k=getResult(next.getAnswer(),d);
//					MatchQoa = next;
//				}
			}
			
			reply = reply + "匹配答案 : " + matchQoa + "\r\n";
			ReplyMsg replyMsg = new ReplyMsg();
			replyMsg.setReply(reply);
			return replyMsg;
		} catch (Exception e) {
			e.printStackTrace();
			ReplyMsg replyMsg = new ReplyMsg();
			replyMsg.setReply("出现了一个意料之外的问题");
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
			replyMsg.setReply("笨蛋，你得告诉我绑定的id啊");
		}else {
			RankInfo rankInfo = rankInfoRepository.getInfoByUserId(receiveMsg.getUser_id());
			if(rankInfo!=null) {
				rankInfoRepository.delete(rankInfo);
			}
			RankInfo ri = new RankInfo();
			ri.setUser_id(receiveMsg.getUser_id());
			ri.setUser_name(raw_message);
			rankInfoRepository.save(ri);
			replyMsg.setReply("绑定成功");
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
				replyMsg.setReply("请先绑定角色\r\n" + 
						"例如：查询绑定badeling");
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
				replyMsg.setReply("请先绑定角色\r\n" + 
						"例如：查询绑定badeling");
				return replyMsg;
			}else {
				name = rankInfo.getUser_name();
			}
		}else {
			name = raw_message;
		}

			String legion = rankService.getRank(name);
			replyMsg.setReply(legion);
		
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
	
	@SuppressWarnings("unused")
	private ReplyMsg handlePrivateMsg(ReceiveMsg receiveMsg) {
		
		if(receiveMsg.getRaw_message().length()>=2&&receiveMsg.getRaw_message().substring(0,2).contains(MsbotConst.botName)) {
        	return handleNameMsg(receiveMsg);
        }
		ReplyMsg replyMsg = privateService.handlePrivateMsg(receiveMsg);
		return replyMsg;
	}
	
	
	private ReplyMsg handRecognize2(ReceiveMsg receiveMsg) {
				//识图
				String[] result = mvpImageService.handHigherImageMsg(receiveMsg);
				//接受数据
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


	private ReplyMsg handRereadMsg(ReceiveMsg receiveMsg) {
		// receiveMsg.getRaw_message().contains("[CQ:image,file=")
		HashMap<String, ReReadMsg> map = ReRead.getMap();
		if(map==null) {
			map = new HashMap<String, ReReadMsg>();
		}
		ReReadMsg reReadMsg = map.get(receiveMsg.getGroup_id());
		
		boolean isBreakReread = false;
		//图片信息判定是否相同
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
			//第一次打开
			if(reReadMsg==null) {
				reReadMsg = new ReReadMsg();
				reReadMsg.setMes_count(1);
			}
			if(receiveMsg.getRaw_message().contains("&#")) {
				return null;
			}
			//打断复读
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
				if(reReadMsg.getRaw_message().equals("？")) {
					groupMsg.setMessage("[CQ:image,file=save/7AA9BBE83B63CB529F8EC7B64B14116C]");
				}else {
					groupMsg.setMessage(reReadMsg.getRaw_message());
				}
				groupMsgService.sendGroupMsg(groupMsg);
			}
			map.put(receiveMsg.getGroup_id(), reReadMsg);
			//计算复读
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
		
		if(reReadMsg.getMes_count()>MsbotConst.random_count&&reReadMsg.getCount()==1) {
			reReadMsg.setMes_count(1);
			try {
				String gpt_message = "";
				List<Message> list = messageRepository.findForGpt(receiveMsg.getGroup_id());
				
				if(list.isEmpty()) {
					gpt_message = "A:" + receiveMsg.getRaw_message() + "\r\n你:?";
				}else {
					Collections.reverse(list);
					Map<String,String> roleMap = new HashMap<>();
					//user_id替换为ABCD
					for(Message temp : list) {
						if(!roleMap.containsKey(temp.getUser_id())) {
							int i = roleMap.size();
							char s1 = (char)(i+(int)'A');
							roleMap.put(temp.getUser_id(), s1+"");
						}
					}
					roleMap.put(MsbotConst.botId, MsbotConst.gptName);
					for(Message temp : list) {
						gpt_message = gpt_message + roleMap.get(temp.getUser_id())+":"+temp.getRaw_message()+"\r\n";
					}
					gpt_message = gpt_message + "你:?";
				}

				ReplyMsg gpt = ChatGpt.getGptForGroup(gpt_message);
				
				GroupMsg groupMsg = new GroupMsg();
				groupMsg.setAuto_escape(false);
				groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
				groupMsg.setMessage(gpt.getReply());
				groupMsgService.sendGroupMsg(groupMsg);
				
				Thread.sleep(5873);
				
				GroupMsg g = new GroupMsg();
				g.setGroup_id(Long.parseLong(MsbotConst.gpt_notice_group));
				GroupInfo findByGroupId = groupInfoRepository.findByGroupId(receiveMsg.getGroup_id());
				g.setMessage(gpt_message+"\r\n"+gpt.getReply()+" -by "+findByGroupId.getGroup_name());
				groupMsgService.sendGroupMsg(g);
				
				return null;
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			Random r = new Random();
			//随机回复auto
			List<Msg> msgList = msgRepository.findMsgByExtQuestion("随机回复auto");
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

		if(raw_message.contains("[CQ:image")) {
			ReplyMsg h = handRecognize2(receiveMsg);
			System.out.println(h.getReply());
			raw_message = h.getReply().replaceAll("\\r\\n", "");
		}
		
		
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
		//识图
		String[] result = mvpImageService.handImageMsg(receiveMsg);
		//接受数据
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
		
		//布尔学习
		if(raw_message.contains("学习")&&raw_message.contains("布尔问")&&raw_message.contains("答")) {
			if(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)) {
				try {
					int questionIndex = raw_message.indexOf("问");
					int answerIndex = raw_message.indexOf(" 答");
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
							replyMsg.setReply("出现了一个意料之外的错误");
						}
						
					}else {
						replyMsg.setReply("宁是什么东西也配命令老娘？爬爬爬！");
					}

					return replyMsg;
				}
		
		//正则学习
		if(raw_message.contains("学习")&&raw_message.contains("正则问")&&raw_message.contains("答")) {
			if(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)||isAdminMsg(receiveMsg.getUser_id())) {
				try {
					int questionIndex = raw_message.indexOf("问");
					int answerIndex = raw_message.indexOf(" 答");
					String theQuestion = raw_message.substring(questionIndex+1, answerIndex);
					if((theQuestion.contains("固定回复")||theQuestion.contains("随机回复"))&&!receiveMsg.getUser_id().equals(MsbotConst.masterId)) {
						replyMsg.setReply("你的权限不足，添加固定回复词条所需权限22，你当前权限21");
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
							replyMsg.setReply("出现了一个意料之外的错误");
						}
						
					}else {
						replyMsg.setReply("宁是什么东西也配命令老娘？爬爬爬！");
					}

					return replyMsg;
				}
		
		//学习
		if(raw_message.contains("学习")&&raw_message.contains("问")&&raw_message.contains("答")) {
			if(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)||isAdminMsg(receiveMsg.getUser_id())) {
				try {
					Msg newMsg = new Msg();
					int questionIndex = raw_message.indexOf("问");
					int answerIndex = raw_message.indexOf(" 答");
					String theQuestion = raw_message.substring(questionIndex+1, answerIndex);
					newMsg.setQuestion(theQuestion);
					if((theQuestion.contains("固定回复")||theQuestion.contains("随机回复"))&&!receiveMsg.getUser_id().equals(MsbotConst.masterId)) {
						replyMsg.setReply("你的权限不足，添加固定回复词条所需权限22，你当前权限21");
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
							replyMsg.setReply("出现了一个意料之外的错误");
						}
						
					}else {
						replyMsg.setReply("宁是什么东西也配命令老娘？爬爬爬！");
					}

					return replyMsg;
				}

		//查询
		Set<Msg> set = msgRepository.findAllQuestion();
		Iterator<Msg> it = set.iterator();
		
		if((raw_message.startsWith(MsbotConst.botName+"查询")||raw_message.startsWith(MsbotConst.botName+" 查询"))&&(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)||isAdminMsg(receiveMsg.getUser_id()))) {
			try {
				int index = raw_message.indexOf("查询");
				Set<Msg> oldMsgList = msgRepository.findMsgByQuestion(raw_message.substring(index+2));
				Msg oldMsg;
				String messageReply = "";
				Iterator<Msg> it2 = oldMsgList.iterator();
				if(!it2.hasNext()) {
					replyMsg.setReply("查询结果为空");
					return replyMsg;
				}
				
				while(it2.hasNext()) {
					oldMsg = it2.next();
					if(oldMsg.getAnswer().contains("[CQ:record")) {
						messageReply = messageReply + "ID:"+ oldMsg.getId() + " 问题："+oldMsg.getQuestion()+" 回答："+oldMsg.getAnswer().replace("[CQ:record,file", "[voice") + oldMsg.getLink() + "\r\n";
					}else {
						messageReply = messageReply + "ID:"+ oldMsg.getId() + " 问题："+oldMsg.getQuestion()+" 回答："+oldMsg.getAnswer();
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
				replyMsg.setReply("出现异常");
			}
			return replyMsg;
		}
		
		//删除
		if(raw_message.startsWith(MsbotConst.botName+"删除问题")||raw_message.startsWith(MsbotConst.botName+" 删除问题")) {
			if(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)||isAdminMsg(receiveMsg.getUser_id())) {
				try {
					int index = raw_message.indexOf("删除问题");
					Msg findQuestion = msgRepository.findQuestion(raw_message.substring(index+4));
					if(findQuestion==null) {
						replyMsg.setReply("指定问题不存在");
					}else {
						if(isAdminMsg(receiveMsg.getUser_id())) {
							if(!findQuestion.getCreateId().equals(receiveMsg.getUser_id())) {
								replyMsg.setReply("只能删除自己创建的问题喔");
								return replyMsg;
							}
						}
						msgRepository.delete(findQuestion);
						replyMsg.setReply("删除成功 问题:"+findQuestion.getQuestion());
					}
				}catch (Exception e) {
					replyMsg.setReply("出现异常");
				}
				
			}else {
				replyMsg.setReply("宁是什么东西也配命令老娘？爬爬爬！");
			}
			return replyMsg;
		}
		
		//伤害信息
		if(raw_message.contains("伤害")&&(raw_message.contains("boss")||raw_message.contains("BOSS"))) {
			String[] split = raw_message.split(" ");
			RoleDmg roleDmg = roleDmgRepository.findRoleBynumber(receiveMsg.getSender().getUser_id());
			if(roleDmg == null) {
				//查询无角色
				roleDmg = new RoleDmg();
				//设置群名片 如果没有 设置昵称
				if(receiveMsg.getSender().getCard()==null || receiveMsg.getSender().getCard().equals("")) {
					roleDmg.setName(receiveMsg.getSender().getNickname());
				}else {
					roleDmg.setName(receiveMsg.getSender().getCard());
				}
				//设置QQ号
				roleDmg.setUser_id(receiveMsg.getSender().getUser_id());
				//设置群号
				roleDmg.setGroup_id(receiveMsg.getGroup_id());
				roleDmg.setCommonDmg(100);
				roleDmg.setBossDmg(200);
				roleDmg = roleDmgRepository.save(roleDmg);
			}
			try {
				for(String temp : split) {
					temp = temp.replace("%", "").replace(MsbotConst.botName,"");
					if(temp.contains("伤害")) {
						roleDmg.setCommonDmg(Integer.parseInt(temp.replace("伤害", "")));
					}else if(temp.toLowerCase().contains("boss")) {
						temp = temp.toLowerCase();
						roleDmg.setBossDmg(Integer.parseInt(temp.replace("boss", "")));
					}else {
					}
				}
				roleDmgRepository.modifyDmg(roleDmg.getId(), roleDmg.getCommonDmg(), roleDmg.getBossDmg());
				replyMsg.setReply("修改成功");
				return replyMsg;
			} catch (Exception e) {
				replyMsg.setReply("出现了一个意料之外的错误");
				return replyMsg;
			}
			
		}
		
		//攻击信息
		if(raw_message.contains("攻击")&&raw_message.contains("百分比")&&raw_message.contains("面板")) {
			String[] split = raw_message.split(" ");
			RoleAtt roleAtt = roleAttRepository.findRoleBynumber(receiveMsg.getSender().getUser_id());
			if(roleAtt == null) {
				//查询无角色
				roleAtt = new RoleAtt();
				//设置群名片 如果没有 设置昵称
				if(receiveMsg.getSender().getCard()==null || receiveMsg.getSender().getCard().equals("")) {
					roleAtt.setName(receiveMsg.getSender().getNickname());
				}else {
					roleAtt.setName(receiveMsg.getSender().getCard());
				}
				//设置QQ号
				roleAtt.setUser_id(receiveMsg.getSender().getUser_id());
				//设置群号
				roleAtt.setGroup_id(receiveMsg.getGroup_id());
				roleAtt.setAtt(6728);
				roleAtt.setAttPer(157);
				roleAtt.setMaxAtt(Long.parseLong("66030797"));
				roleAtt = roleAttRepository.save(roleAtt);
			}
			try {
				for(String temp : split) {
					temp = temp.replace("%", "").replace(MsbotConst.botName,"");
					if(temp.contains("攻击")) {
						roleAtt.setAtt(Integer.parseInt(temp.replace("攻击", "")));
					}else if(temp.contains("百分比")) {
						roleAtt.setAttPer(Integer.parseInt(temp.replace("百分比", "")));
					}else if(temp.contains("面板")){
						roleAtt.setMaxAtt(Long.parseLong(temp.replace("面板", "")));
					}else {
					}
				}
				roleAttRepository.modifyAtt(roleAtt.getId(), roleAtt.getAtt(), roleAtt.getAttPer(),roleAtt.getMaxAtt());
				
				String reply = "修改成功";
				
				RoleDmg roleDmg = roleDmgRepository.findRoleBynumber(receiveMsg.getSender().getUser_id());
				if(roleDmg==null) {
					reply = reply + "，未查询到伤害、boss数据，无法计算具体收益比。";
					replyMsg.setReply(reply);
					return replyMsg;
				}
				
				//攻击 攻击百分比
				int att_per = roleAtt.getAttPer();
				int att = roleAtt.getAtt();
				//面板
				Long max_att = roleAtt.getMaxAtt();
				//总伤
				int dmg = roleDmg.getCommonDmg();
				int boss = roleDmg.getBossDmg();
				
				//计算如果新增40boss 相当于提升x攻击%
				int add_boss = 40;
				//boss面板
				float real_max_att = max_att/(100+dmg)*(100+dmg+boss);
				//增加40boss后的boss面板
				float real_after_max_att =  max_att/(100+dmg)*(100+dmg+boss+add_boss);
				//增加40boss后的等效总攻击
				float after_att = att*real_after_max_att/real_max_att;
				//等效总攻击换算为百分比
				float eq_att_per = ((float)after_att)/att*(100+att_per)-(100+att_per);
				//等效攻击去除百分比
				float eq_att = (after_att-att)*100/(100+att_per);
				reply = reply + "\r\n当前数据：伤害："+dmg+"%，boss："+boss+"%\r\n"
						+ "攻击："+att+"，攻击百分比："+att_per+"%，\r\n面板："+max_att+"\r\n"
						+ add_boss + "%boss=" + String.format("%.2f",eq_att_per) + "%攻击力=" + String.format("%.2f",eq_att) + "攻击力";
				replyMsg.setReply(reply);
				return replyMsg;
			} catch (Exception e) {
				replyMsg.setReply("出现了一个意料之外的错误");
				e.printStackTrace();
				return replyMsg;
			}
		}
		
		//测试字体
		if(raw_message.startsWith(MsbotConst.botName+"测试字体")) {
			try {
				String testFont = Loadfont2.testFont2(raw_message.substring(6));
				replyMsg.setReply(testFont);
				return replyMsg;
			} catch (Exception e) {
				replyMsg.setReply("出现了一个意料之外的错误");
				e.printStackTrace();
				return replyMsg;
			}
		}
		
		//内鬼
		if(raw_message.contains("职业群")&&(receiveMsg.getGroup_id().equals("372752762")||receiveMsg.getGroup_id().equals("1107518527"))) {
			replyMsg.setAt_sender(true);
			replyMsg.setReply("林之灵1群：372752762\r\n林之灵2群：1107518527");
			return replyMsg;
		}
		
		//无视计算
		if(raw_message.contains("无视")&&(raw_message.contains("+")||raw_message.contains("-"))) {
			try {
				//消息过滤
				raw_message = raw_message.replace("%", "");
				raw_message = raw_message.replace(MsbotConst.botName, "");
				raw_message = raw_message.replace(" ", "");
				raw_message = raw_message.replace("=", "");
				raw_message = raw_message.replace("无视", "");
				double ign = 0;		
				double ign_before = 0;
				double ign2 = 0;
				double ign_before2 = 0;
				//短消息回复
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
							replyMsg.setReply("我想揍你");
							return replyMsg;
						}
						ign = ign + (100-ign)*Double.parseDouble(getInt[i])/100;
					}
				}else if(raw_message.contains("-")&&!raw_message.contains("+")){
					String[] getInt = raw_message.split("\\-");
					for(int i=0;i<getInt.length;i++) {
						if(Double.parseDouble(getInt[i])>100) {
							replyMsg.setReply("我想揍你");
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
					replyMsg.setReply("又是加又是减，你可摇了我吧");
					return replyMsg;
				}
				
				RoleDmg roleDmg = roleDmgRepository.findRoleBynumber(receiveMsg.getSender().getUser_id());
				RoleAtt roleAtt = roleAttRepository.findRoleBynumber(receiveMsg.getSender().getUser_id());			
				
				if(roleDmg==null&&roleAtt==null) {
					GroupMsg groupMsg = new GroupMsg();
					groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
					groupMsg.setMessage("[CQ:at,qq="+ receiveMsg.getSender().getUser_id() +"]"+"未查询到攻击、伤害信息，默认伤害100,boss伤200,攻击6728(157%),面板66030797。你可通过以下指令修改信息\r\n【"+MsbotConst.botName+" 伤害50 boss300】\r\n【"+MsbotConst.botName+" 攻击6728 百分比157 面板66030797】");
					groupMsgService.sendGroupMsg(groupMsg);
				}else if(roleDmg==null&&roleAtt!=null) {
					GroupMsg groupMsg = new GroupMsg();
					groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
					groupMsg.setMessage("[CQ:at,qq="+ receiveMsg.getSender().getUser_id() +"]"+"未查询到角色信息，默认伤害100,boss伤200。你可通过指令【"+MsbotConst.botName+" 伤害50 boss300】修改角色信息");
					groupMsgService.sendGroupMsg(groupMsg);
				}else if(roleDmg!=null&&roleAtt==null) {
					GroupMsg groupMsg = new GroupMsg();
					groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
					groupMsg.setMessage("[CQ:at,qq="+ receiveMsg.getSender().getUser_id() +"]"+"未查询到面板信息，默认攻击6728(157%),面板66030797。你可通过指令【"+MsbotConst.botName+" 攻击6728 百分比157 面板66030797】修改角色信息");
					groupMsgService.sendGroupMsg(groupMsg);
				}else {
					
				}
				
				
				if(roleDmg == null) {
					//查询无角色
					roleDmg = new RoleDmg();
					//设置群名片 如果没有 设置昵称
					if(receiveMsg.getSender().getCard()==null || receiveMsg.getSender().getCard().equals("")) {
						roleDmg.setName(receiveMsg.getSender().getNickname());
					}else {
						roleDmg.setName(receiveMsg.getSender().getCard());
					}
					//设置QQ号
					roleDmg.setUser_id(receiveMsg.getSender().getUser_id());
					//设置群号
					roleDmg.setGroup_id(receiveMsg.getGroup_id());
					roleDmg.setCommonDmg(100);
					roleDmg.setBossDmg(200);
					roleDmgRepository.save(roleDmg);
				}
				if(roleAtt == null) {
					//查询无角色
					roleAtt = new RoleAtt();
					//设置群名片 如果没有 设置昵称
					if(receiveMsg.getSender().getCard()==null || receiveMsg.getSender().getCard().equals("")) {
						roleAtt.setName(receiveMsg.getSender().getNickname());
					}else {
						roleAtt.setName(receiveMsg.getSender().getCard());
					}
					//设置QQ号
					roleAtt.setUser_id(receiveMsg.getSender().getUser_id());
					//设置群号
					roleAtt.setGroup_id(receiveMsg.getGroup_id());
					roleAtt.setAtt(6728);
					roleAtt.setAttPer(157);
					roleAtt.setMaxAtt(Long.parseLong("66030797"));
					roleAttRepository.save(roleAtt);
				}
//				shortMsg += " = " + String.format("%.2f", ign) + "%";
//				shortMsg += "\r\n为防止刷屏，详细计算的部分将以私聊的形式发送于您。";
				ign2 = ign + (100-ign)*20/100;
				
				//攻击 攻击百分比
				int att_per = roleAtt.getAttPer();
				int att = roleAtt.getAtt();
				//面板
				Long max_att = roleAtt.getMaxAtt();
				//总伤
				int dmg = roleDmg.getCommonDmg();
				int boss = roleDmg.getBossDmg();
				
				//计算如果新增40boss 相当于提升x攻击%
				int add_boss = 40;
				//boss面板
				float real_max_att = max_att/(100+dmg)*(100+dmg+boss);
				//增加40boss后的boss面板
				float real_after_max_att =  max_att/(100+dmg)*(100+dmg+boss+add_boss);
				//增加40boss后的等效总攻击
				float after_att = att*real_after_max_att/real_max_att;
				//等效总攻击换算为百分比
				float eq_att_per = ((float)after_att)/att*(100+att_per)-(100+att_per);
				//等效攻击去除百分比
				float eq_att = (after_att-att)*100/(100+att_per);
				
				String replyM = "你之前的无视：" + ign_before + "%(" + ign_before2 + "%)\r\n" + "计算后的无视：" + String.format("%.2f", ign) + "%(" + String.format("%.2f", ign2) + "%)\r\n";
				replyM += "角色数据 伤害:" + roleDmg.getCommonDmg() + "% boss:" + roleDmg.getBossDmg() + "%\r\n"
						+ "攻击:" + att + "(" + att_per + "%)" + " 面板:" + max_att + "\r\n";
				
				replyM += "//----超高防对比-----//\r\n";
				replyM += "卡琳提升率（380超高防）：" + String.format("%.2f", (defAndign(380, ign)/defAndign(380, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(380, ign2)/defAndign(380, ign_before2)-1)*100) + "%)\r\n";
				replyM += "原伤害" + defAndign(380, ign_before) + "%(" + defAndign(380, ign_before2) + "%)\r\n";
				replyM += "现伤害" + defAndign(380, ign) + "%(" + defAndign(380, ign2) +  "%)\r\n";
				replyM += "提升" + String.format("%.2f",(defAndign(380, ign)/defAndign(380, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%boss/" + String.format("%.2f",(defAndign(380, ign)/defAndign(380, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())*eq_att_per/add_boss) + "%攻击/" + String.format("%.2f",(defAndign(380, ign)/defAndign(380, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())*eq_att/add_boss)+ "攻击\r\n";

				replyM += "//-----高防对比-----//\r\n";
				replyM += "斯乌提升率（300高防）：" + String.format("%.2f", (defAndign(300, ign)/defAndign(300, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(300, ign2)/defAndign(300, ign_before2)-1)*100) + "%)\r\n";
				replyM += "原伤害" + defAndign(300, ign_before) + "%(" + defAndign(300, ign_before2) + "%)\r\n";
				replyM += "现伤害" + defAndign(300, ign) + "%(" + defAndign(300, ign2) +  "%)\r\n";
				replyM += "提升" + String.format("%.2f",(defAndign(300, ign)/defAndign(300, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%boss/" + String.format("%.2f",(defAndign(300, ign)/defAndign(300, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())*eq_att_per/add_boss) + "%攻击/" + String.format("%.2f",(defAndign(300, ign)/defAndign(300, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())*eq_att/add_boss)+ "攻击\r\n";
				
				replyM += "//-----中防对比-----//\r\n";
				replyM += "进阶贝伦提升率（200中防）：" + String.format("%.2f", (defAndign(200, ign)/defAndign(200, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(200, ign2)/defAndign(200, ign_before2)-1)*100) + "%)\r\n";
				replyM += "原伤害" + defAndign(200, ign_before) + "%(" + defAndign(200, ign_before2) + "%)\r\n";
				replyM += "现伤害" + defAndign(200, ign) + "%(" + defAndign(200, ign2) +  "%)\r\n";
				replyM += "提升" + String.format("%.2f",(defAndign(200, ign)/defAndign(200, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%boss/" + String.format("%.2f",(defAndign(200, ign)/defAndign(200, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())*eq_att_per/add_boss) + "%攻击/" + String.format("%.2f",(defAndign(200, ign)/defAndign(200, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())*eq_att/add_boss)+ "攻击";
				
				replyMsg.setReply(replyM);
				String reply = null;
				try {
					reply = drawService.ignImage(replyM);
					replyMsg.setReply(reply);
					replyMsg.setAt_sender(false);
				}catch (Exception e) {
					replyMsg.setReply("图片文件缺失");
					replyMsg.setAt_sender(false);
					e.printStackTrace();
				}
//					PrivateMsg privateMsg = new PrivateMsg();
//					privateMsg.setUser_id(Long.parseLong(receiveMsg.getUser_id()));
//					privateMsg.setMessage(replyM);
//					groupMsgService.sendPrivateMsg(privateMsg);
				return replyMsg;
				
				
				//无视 核心20%计算
//				String replyM = "你之前的无视：" + ign_before + "%(" + ign_before2 + "%)\r\n" + "计算后的无视：" + String.format("%.2f", ign) + "%(" + String.format("%.2f", ign2) + "%)\r\n";
//				replyM += "角色数据 伤害:" + roleDmg.getCommonDmg() + "% boss:" + roleDmg.getBossDmg() + "%\r\n(括号为核心20%无视加成结果)\r\n";
//				
//				replyM += "//----超高防对比-----//\r\n";
//				replyM += "卡琳提升率（380超高防）：" + String.format("%.2f", (defAndign(380, ign)/defAndign(380, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(380, ign2)/defAndign(380, ign_before2)-1)*100) + "%)\r\n";
//				replyM += "原伤害" + defAndign(380, ign_before) + "%(" + defAndign(380, ign_before2) + "%)\r\n";
//				replyM += "现伤害" + defAndign(380, ign) + "%(" + defAndign(380, ign2) +  "%)\r\n";
//				replyM += "相当于提升了" + String.format("%.2f",(defAndign(380, ign)/defAndign(380, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%(" + String.format("%.2f", (defAndign(380, ign2)/defAndign(380, ign_before2)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%)点boss伤害\r\n";
//				
//				replyM += "//-----高防对比-----//\r\n";
//				replyM += "斯乌提升率（300高防）：" + String.format("%.2f", (defAndign(300, ign)/defAndign(300, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(300, ign2)/defAndign(300, ign_before2)-1)*100) + "%)\r\n";
//				replyM += "原伤害" + defAndign(300, ign_before) + "%(" + defAndign(300, ign_before2) + "%)\r\n";
//				replyM += "现伤害" + defAndign(300, ign) + "%(" + defAndign(300, ign2) +  "%)\r\n";
//				replyM += "相当于提升了" + String.format("%.2f",(defAndign(300, ign)/defAndign(300, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%(" + String.format("%.2f", (defAndign(300, ign2)/defAndign(300, ign_before2)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%)点boss伤害\r\n";
//				
//				replyM += "//-----中防对比-----//\r\n";
//				replyM += "进阶贝伦提升率（200中防）：" + String.format("%.2f", (defAndign(200, ign)/defAndign(200, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(200, ign2)/defAndign(200, ign_before2)-1)*100) + "%)\r\n";
//				replyM += "原伤害" + defAndign(200, ign_before) + "%(" + defAndign(200, ign_before2) + "%)\r\n";
//				replyM += "现伤害" + defAndign(200, ign) + "%(" + defAndign(200, ign2) +  "%)\r\n";
//				replyM += "相当于提升了" + String.format("%.2f",(defAndign(200, ign)/defAndign(200, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%(" + String.format("%.2f", (defAndign(200, ign2)/defAndign(200, ign_before2)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%)点boss伤害";
//				
//				replyMsg.setReply(replyM);
//				String reply = null;
//				try {
//					reply = drawService.ignImage(replyM);
//					replyMsg.setReply(reply);
//					replyMsg.setAt_sender(false);
//				}catch (Exception e) {
//					replyMsg.setReply("图片文件缺失");
//					replyMsg.setAt_sender(false);
//					e.printStackTrace();
//				}
//				PrivateMsg privateMsg = new PrivateMsg();
//				privateMsg.setUser_id(Long.parseLong(receiveMsg.getUser_id()));
//				privateMsg.setMessage(replyM);
//				groupMsgService.sendPrivateMsg(privateMsg);
//				return replyMsg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		if(raw_message.contains("无视")){
			String reply = "//无视加算\r\n"
					+ MsbotConst.botName + " 无视 70+50+20\r\n"
					+ "//无视逆运算\r\n"
					+ MsbotConst.botName + " 无视 90-50\r\n"
					+ "//修改角色属性数据\r\n"
					+ MsbotConst.botName + " 伤害50 boss250\r\n"
					+ MsbotConst.botName + " 攻击6728 百分比157 面板66030797";
			replyMsg.setReply(reply);
			return replyMsg;
		}
		
		if(raw_message.contains("复读机周报")) {
			//得到群成员信息
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
					//无群名片
					map.put(a, b);
				}else {
					//有群名片
					map.put(a, c);
				}
			}
			
			String message = "本周最长的复读长龙是：\r\n";
					RereadSentence rereadSentence = rereadSentenceRepository.findMaxByGroup(receiveMsg.getGroup_id());
					if(rereadSentence!=null) {
						message += rereadSentence.getMessage() + "\r\n" + 
						"此金句出自———————"+ map.get(rereadSentence.getUser_id()) + "\r\n" + 
						"当时被复读机们连续复读了" + rereadSentence.getReadTime() + "次！\r\n";
						List<RereadTime> list = rereadTimeRepository.find3thByGroup(receiveMsg.getGroup_id());
						if(list!=null) {
							message += "——————————————————\r\n" + 
									"本周最佳复读机的称号授予" + map.get(list.get(0).getUser_id()) + "！\r\n" + 
									"他在过去的一周里疯狂复读" + list.get(0).getCount() + "次！简直太丧病了。\r\n" + 
									"——————————————————\r\n" + 
									"此外，以下两名成员获得了亚军和季军，也是非常优秀的复读机：\r\n";
									if(list.size()>1) {
										message += map.get(list.get(1).getUser_id()) + " 复读次数："+list.get(1).getCount() + "\r\n";
									}else{
										message += "虚位以待\r\n";
									}
									if(list.size()>2) {
										message += map.get(list.get(2).getUser_id()) + " 复读次数："+list.get(2).getCount() + "\r\n";
									}else {
										message += "虚位以待\r\n";
									}
									message += "为了成为最佳复读机，努力复读吧！uwu";
						}else {
							message = "owo,本群没有复读机。";
						}
					}else {
						message = "owo,本群没有复读机";
					}
			replyMsg.setReply(message);
			replyMsg.setAt_sender(false);
			return replyMsg;
		}
		//退群人员查询
		if(raw_message.contains("谁")&&raw_message.contains("退群")) {
			Long time = System.currentTimeMillis()-24*60*60*1000;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			List<GroupMember> findLeaveMemberList = groupMemberRepository.findLeaveMemberInfo(receiveMsg.getGroup_id(), Integer.parseInt(sdf.format(time)));
			if(findLeaveMemberList==null||findLeaveMemberList.isEmpty()) {
				replyMsg.setReply("好像没有人退群呀");
				replyMsg.setAt_sender(false);
				return replyMsg;
			}else {
				String reply = "";
				for(GroupMember temp : findLeaveMemberList) {
					String leave_time = temp.getLeave_time();
					if(!reply.contains(leave_time+":")) {
						if(reply.isEmpty()) {
							reply = reply + leave_time+":";
						}else {
							reply = reply + "\r\n" + leave_time+":";
						}
					}
					
					if(temp.getCard()!=null&&!temp.getCard().isEmpty()) {
						reply = reply + "\r\n" + temp.getCard();
					}else {
						reply = reply + "\r\n" + temp.getNickname();
					}
					List<Score> findScoreById = scoreRepository.findScoreById(temp.getUser_id(), receiveMsg.getGroup_id());
					int score = 0;
					for(Score s : findScoreById) {
						score = score + s.getScore();
					}
					reply = reply + " - " + score;
				}
				replyMsg.setReply(reply);
				replyMsg.setAt_sender(false);
				return replyMsg;
			}
			
		}
		
		if(raw_message.contains("排行榜")) {
			List<Object[]> rankingList = scoreRepository.getRankingList(receiveMsg.getGroup_id());
			List<GroupMember> findGroupMemberByGroup = groupMemberRepository.findGroupMemberByGroup(receiveMsg.getGroup_id());
			List<String> richList = new ArrayList<String>();
			List<String> poorList = new ArrayList<String>();
			Map<String,String> map = new HashMap<String, String>();
			for(GroupMember gm : findGroupMemberByGroup) {
				if(gm.getCard()!=null&&!gm.getCard().isEmpty()) {
					map.put(gm.getUser_id(), gm.getCard());
				}else if(gm.getNickname()!=null){
					map.put(gm.getUser_id(), gm.getNickname());
				}else {
					map.put(gm.getUser_id(), "null");
				}
			}
			
			int count = 0;
			for(Object[] obj : rankingList) {
				String user_id = String.valueOf(obj[0]);//user_id
				String score = String.valueOf(obj[1]);//score
				String name = "unknown";
				if(Integer.parseInt(score)<=0||count>10) {
					break;
				}
				if(map.containsKey(user_id)) {
					name = map.get(user_id);
				}
				richList.add(name+" : "+score);
				count++;
			}
			
			Collections.reverse(rankingList);
			count = 0;
			for(Object[] obj : rankingList) {
				String user_id = String.valueOf(obj[0]);//user_id
				String score = String.valueOf(obj[1]);//score
				String name = "unknown";
				if(Integer.parseInt(score)>=0||count>10) {
					break;
				}
				if(map.containsKey(user_id)) {
					name = map.get(user_id);
				}
				poorList.add(name+" : "+score);
				count++;
			}
			
			try {
				String reply = drawService.getRankList(richList,poorList);
				replyMsg.setReply(reply);
				return replyMsg;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//测试接口
		if(raw_message.startsWith(MsbotConst.botName+"跟我读")&&receiveMsg.getUser_id().equals(MsbotConst.masterId)) {
			replyMsg.setAt_sender(false);
			replyMsg.setReply(raw_message.substring(5));
			return replyMsg;
		}
		
		//扔xxx
		if(raw_message.startsWith(MsbotConst.botName+"扔")&&raw_message.contains("[CQ:at")) {
    		try {
    			int aIndex = receiveMsg.getRaw_message().indexOf("[CQ:at,qq=")+10;
        		int bIndex = receiveMsg.getRaw_message().indexOf("]");
        		String findNumber = receiveMsg.getRaw_message().substring(aIndex,bIndex);
        		if(raw_message.contains("别")||raw_message.contains("不")||raw_message.contains("怎么")||raw_message.contains("禁止")) {
        			replyMsg.setAt_sender(true);
        			replyMsg.setReply("[CQ:image,file=img/buzhidao5.jpg]");
    				return replyMsg;
    			}
        		String throwSomeone = "";
        		if(findNumber.equals(MsbotConst.masterId)||findNumber.equals(MsbotConst.botId)||findNumber.equals(receiveMsg.getSelf_id())) {
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
			if(raw_message.contains("级")&&raw_message.contains("星")&&raw_message.contains("攻")) {
				raw_message = raw_message.replaceAll(" ", "").substring(MsbotConst.botName.length());
				int level = Integer.parseInt(raw_message.substring(0,raw_message.indexOf("级")));
				int stat = 0;
				int fireStat = 0;
				int att = Integer.parseInt(raw_message.substring(raw_message.indexOf("星")+1,raw_message.indexOf("攻")));
				int fireAtt = 0;
				int nowStar = Integer.parseInt(raw_message.substring(raw_message.indexOf("级")+1,raw_message.indexOf("星")));
				
				if(level!=130&&level!=140&&level!=150&&level!=160&&level!=200) {
					replyMsg.setReply("只能计算等级为130,140,150,160,200的装备");
					return replyMsg;
				}
				if((level==130&&nowStar>20)||nowStar>25) {
					replyMsg.setReply("超出星之力范围上限");
					return replyMsg;
				}
				if(stat<0||fireStat<0||att<0||fireAtt<0||nowStar<0) {
					replyMsg.setReply("数据不能为负");
					return replyMsg;
				}
				
				int[] starForce = starForceDesc(level,stat-fireStat,att-fireAtt,nowStar);
				int finalAtt = starForce[1]+fireAtt;
				String result = "0星状态下满卷攻击为：" + finalAtt;
				replyMsg.setReply(result);
				return replyMsg;
			}

			
			//正推星星
			if(raw_message.contains("级")&&raw_message.contains("星")) {
				raw_message = raw_message.replaceAll(" ", "").substring(MsbotConst.botName.length());
				/**
			 	正推星星
			 	蠢猫 150级16星
			 */
				boolean isWeapon = false;
				int level = Integer.parseInt(raw_message.substring(0,raw_message.indexOf("级")));
				int stat = 0;
				int fireStat = 0;
				int att = 0;
				int fireAtt = 0;
				int nowStar = 0;
				int targetStar = Integer.parseInt(raw_message.substring(raw_message.indexOf("级")+1,raw_message.indexOf("星")));
				if(level!=130&&level!=140&&level!=150&&level!=160&&level!=200) {
					replyMsg.setReply("只能计算等级为130,140,150,160,200的装备");
					return replyMsg;
				}
				if((level==130&&targetStar>20)||targetStar>25) {
					replyMsg.setReply("超出星之力范围上限");
					return replyMsg;
				}
				if(stat<0||fireStat<0||att<0||fireAtt<0||nowStar<0||targetStar<0) {
					replyMsg.setReply("数据不能为负");
					return replyMsg;
				}
				if(nowStar>targetStar) {
					replyMsg.setReply("请使用:逆推星星功能");
					return replyMsg;
				}
				int[] starForce = starForce(level,stat-fireStat,att-fireAtt,nowStar,targetStar,isWeapon);
				int finalStat = starForce[0]+fireStat;
				int finalAtt = starForce[1]+fireAtt;
				String result = level + "级装备" + targetStar + "星的加成为：主属" + finalStat + " 攻击"+ finalAtt;
				replyMsg.setReply(result);
				return replyMsg;

			}
		} catch (Exception e) {
			replyMsg.setReply("输入数据异常\r\n防具正推：" + MsbotConst.botName + "[等级][星星]\r\n" + 
					"eg：" + MsbotConst.botName + "150级17星\r\n" + 
					"武器逆推："+ MsbotConst.botName + "[等级][星星][攻击]\r\n" + 
					"eg：" + MsbotConst.botName + "160级13星428攻");
			return replyMsg;
		}
		
		if(receiveMsg.getRaw_message().contains("星之力")||receiveMsg.getRaw_message().contains("星星")) {
			replyMsg.setReply("为方便计算星之力属性，有 防具正推和武器逆推两种功能\r\n防具正推：" + MsbotConst.botName + "[等级][星星]\r\n" + 
					"eg：" + MsbotConst.botName + "150级17星\r\n" + 
					"武器逆推："+ MsbotConst.botName + "[等级][星星][攻击]\r\n" + 
					"eg：" + MsbotConst.botName + "160级13星428攻\r\n"
							+ "注：1、武器攻击为 总攻击 - 火花，结果为满卷0星状态下的属性\r\n"
							+ "2、目前只支持130 140 150 160 200级装备计算。\r\n"
							+ "3、目前支持0-25星计算，不支持蓝星、极真、降星装备计算。");
			return replyMsg;
		}
				
		//打xxx
		if((raw_message.startsWith(MsbotConst.botName+"揍")||raw_message.startsWith(MsbotConst.botName+"打"))&&raw_message.contains("[CQ:at")) {
    		try {
    			int aIndex = receiveMsg.getRaw_message().indexOf("[CQ:at,qq=")+10;
        		int bIndex = receiveMsg.getRaw_message().indexOf("]");
        		String findNumber = receiveMsg.getRaw_message().substring(aIndex,bIndex);
        		if(raw_message.contains("别")||raw_message.contains("不")||raw_message.contains("怎么")||raw_message.contains("禁止")) {
        			replyMsg.setAt_sender(true);
        			replyMsg.setReply("[CQ:image,file=img/buzhidao5.jpg]");
    				return replyMsg;
    			}
        		if(findNumber.equals(MsbotConst.masterId)||findNumber.equals(receiveMsg.getSelf_id())) {
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
		
		if(raw_message.contains("抽卡")) {
			String mes;
			try {
				mes = drawService.kemomimiDraw();
			} catch (Exception e) {
				e.printStackTrace();
				mes = "图片文件缺失。";
			}
			replyMsg.setReply(mes);
			return replyMsg;
		}
		
		if(raw_message.contains("抽奖")||raw_message.contains("魔女")||raw_message.contains("百分百")) {
			Integer score = scoreRepository.getUserTotalScore(receiveMsg.getUser_id(),receiveMsg.getGroup_id());
			if(score!=null&&score<0) {
				replyMsg.setAt_sender(true);
				replyMsg.setReply("java.io.IOException: You don't have enough meso");
				return replyMsg;
			}
			
			Map<String, Long> witchForestMap = GlobalVariable.getWitchForestMap();
			if(witchForestMap.containsKey(receiveMsg.getUser_id())) {
				Long time1 = witchForestMap.get(receiveMsg.getUser_id());
				Long time2 = System.currentTimeMillis();
				//魔女抽奖cd 默认30分钟
				if(time2-time1<1000*60*30) {
					replyMsg.setAt_sender(true);
					replyMsg.setReply("你又在抽獎喔，休息一下吧，去玩會冒冒好不好。");
					return replyMsg;
				}
			}
			String mes;
			try {
				mes = drawService.startDrawMs();
				witchForestMap.put(receiveMsg.getUser_id(), System.currentTimeMillis());
				GlobalVariable.setWitchForestMap(witchForestMap);
			} catch (Exception e) {
				e.printStackTrace();
				mes = "图片文件缺失。";
			}
			replyMsg.setAt_sender(true);
			replyMsg.setReply(mes);
			return replyMsg;
		}
		
		
		//官网
		if(raw_message.startsWith(MsbotConst.botName+"官网")||raw_message.startsWith(MsbotConst.botName+" 官网")) {
			raw_message = raw_message.replaceAll("官网", "");
			raw_message = raw_message.replaceAll(" ", "");
			raw_message = raw_message.replaceAll(MsbotConst.botName, "");
			if(raw_message.isEmpty()) {
				replyMsg.setReply("输入【官网+项目】，查询游戏官网最新资讯。常见项目有：周日冒险岛、维护、敲敲乐、礼品袋");
				return replyMsg;
			}
			
			//搜索页面找
			String url = "http://mxd.sdo.com/web6/news/newsList.asp?wd=" + raw_message +"&CategoryID=a";
			
			try {
				Document doc = Jsoup.connect(url).get();
				Element element = doc.select(".newList").first();
				element.getElementsByAttribute("href").first();
				url = "http://mxd.sdo.com/web6" + element.getElementsByAttribute("href").first().attr("href").replaceAll("&amp;", "&").substring(2);
				Document doc2 = Jsoup.connect(url).get();
				
//				Element ele1 = doc2.getElementsByClass("innerTitle").first();
				Element ele2 = doc2.getElementsByClass("innerText").first();
				String message = "";
//				for(Element temp : ele1.children()) {
//					message = message + temp.text() + "\r\n";
//				}
//				message = message + "官网链接：" + url + "\r\n";
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
				replyMsg.setAt_sender(false);
				replyMsg.setReply(message);
				return replyMsg;
			} catch (Exception e) {
				replyMsg.setReply("查询失败");
				return replyMsg;
			}
		}
		
		if(raw_message.startsWith(MsbotConst.botName+"抽签")||raw_message.startsWith(MsbotConst.botName+"运势")) {
			String reply = new String();
			reply = msgZbCalculate.msgDeZb(receiveMsg.getUser_id());
			replyMsg.setReply(reply);
			return replyMsg;
		}
		
		if(raw_message.contains("世界组队")) {
			String[] list = {"月妙","废都","艾琳森林","女神塔","沙漠","木之公园"};
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// 使用默认时区和语言环境获得一个日历
		    Calendar nowTime = Calendar.getInstance();
		    Calendar beginTime = Calendar.getInstance();
		    nowTime.set(Calendar.HOUR_OF_DAY, 0);
		    nowTime.set(Calendar.MINUTE, 0);
		    nowTime.set(Calendar.SECOND, 0);
		    beginTime.set(2021,4,12,0,0);
		    int count = 0;
		    //两周一循环 循环到本周
		    while(nowTime.getTimeInMillis()-beginTime.getTimeInMillis()>1000*60*60*24*14) {
		    	beginTime.add(Calendar.DAY_OF_MONTH, 14);
		    	count = (count+1)%6;
		    }
		    //输出本周 以及 后五个周期
		    StringBuffer message = new StringBuffer();
		    message.append("世界组队时间表：");
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
			replyMsg.setReply("(ﾉﾟ▽ﾟ)ﾉ我在哦~");
			return replyMsg;
		}
		
		if(raw_message.replaceAll(MsbotConst.botName, "").replaceAll(" ","").equals("")) {
			raw_message = raw_message.replaceAll(" ","");
			raw_message = raw_message.replaceFirst(MsbotConst.botName, "");
			raw_message = raw_message.replaceFirst(MsbotConst.botName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("禁止套娃");
				return replyMsg;
			}
			raw_message = raw_message.replaceFirst(MsbotConst.botName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("禁止双重套娃");
				return replyMsg;
			}
			raw_message = raw_message.replaceFirst(MsbotConst.botName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("禁止三重套娃");
				return replyMsg;
			}
			raw_message = raw_message.replaceFirst(MsbotConst.botName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("禁止四重套娃");
				return replyMsg;
			}
			raw_message = raw_message.replaceFirst(MsbotConst.botName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("禁止五重套娃");
				return replyMsg;
			}
			replyMsg.setAt_sender(false);
			replyMsg.setReply(MsbotConst.botName.substring(0,1)+"nm");
			return replyMsg;
		}
		
		if(raw_message.replaceAll(MsbotConst.botName, "").replaceAll(" ","").replaceAll("？","").equals("")) {
			raw_message = MsbotConst.botName+"固定回复问号";
		}
	
		//占卜
		if(raw_message.startsWith(MsbotConst.botName+"占卜")||raw_message.startsWith(MsbotConst.botName+" 占卜")) {
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
				replyMsg.setReply(MsbotConst.botName+"不支持平方计算");
				return replyMsg;
			}else if((raw_message.contains("×")||raw_message.contains("÷")||raw_message.contains("+")||raw_message.contains("-")||raw_message.contains("*")||raw_message.contains("/"))){
				raw_message = raw_message.replace("×", "*");
				raw_message = raw_message.replace("÷", "/");
				ScriptEngineManager manager = new ScriptEngineManager();
				ScriptEngine engine = manager.getEngineByName("js");
				Object result = engine.eval(raw_message.substring(raw_message.indexOf(MsbotConst.botName)+2));
				//更改结果格式
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
		
		//查找答案
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
			 //默认相似度
			 int similar = 100;
			 //遍历list
			 for(Msg tempMsg:list){
				 //查看相似度
				 int similarity = getResult(tempMsg.getQuestion(),command);
				 //比较相似度 如果更相似 放入变量
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
			 if(msg2.getLink()==null||msg2.getLink().equals("NULL")||msg2.getLink().equals("null")) {
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
		 
		 try {
			 ReplyMsg gptForUser = ChatGpt.getGptForUser(raw_message);
			 GroupMsg groupMsg = new GroupMsg();
			 groupMsg.setAuto_escape(false);
			 groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
			 groupMsg.setMessage(gptForUser.getReply());
			 groupMsgService.sendGroupMsg(groupMsg);
			 return null;
		} catch (Exception e) {
			
		}
		 
		 
		 if(MsbotConst.moliKey2!=null&&MsbotConst.moliSecret2!=null&&!MsbotConst.moliKey2.isEmpty()&&!MsbotConst.moliSecret2.isEmpty()) {
			 if(!raw_message.contains("[CQ")) {
					String tuLingMsg = groupMsgService.MoliMsg2(command, Math.abs(receiveMsg.getUser_id().hashCode())+"", receiveMsg.getSender().getNickname());
					@SuppressWarnings("unchecked")
					Map<String,Object> result = (Map<String, Object>) JSON.parse(tuLingMsg); 
					System.out.println(tuLingMsg);
					if((result.get("message")+"").contains("请求成功")) {
						String reply = tuLingMsg.substring(tuLingMsg.indexOf("content")+10,tuLingMsg.indexOf("\",\"typed\":"));
						replyMsg.setReply(reply);
						
						GroupMsg groupMsg = new GroupMsg();
						groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
						groupMsg.setMessage(reply);
						groupMsgService.sendGroupMsg(groupMsg);
						return null;
					}
				}
			}
		
		 
		
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
		
		String group_id = noticeMsg.getGroup_id();
		String user_id = noticeMsg.getUser_id();
		
		try {
			List<GroupMember> findGroupMemberInfo = groupMemberRepository.findGroupMemberInfo(group_id, user_id);
			if(findGroupMemberInfo!=null&&findGroupMemberInfo.size()>0) {
				boolean isModify = false;
				for(GroupMember temp : findGroupMemberInfo) {
					//判定重复信息 保留第一条 删除其他的
					if(!isModify) {
						Long time = System.currentTimeMillis();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						String leave_time = sdf.format(time);
						groupMemberRepository.modifyMemberLeaveTime(temp.getId(), leave_time);
						isModify = true;
					}else {
						groupMemberRepository.delete(temp);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("退群成员表修改失败");
		}
		
		GroupMsg groupMsg = new GroupMsg();
		groupMsg.setAuto_escape(false);
		groupMsg.setGroup_id(Long.parseLong(noticeMsg.getGroup_id()));
		groupMsg.setMessage("[CQ:image,file=img/leave.png]");
		System.out.println(noticeMsg.toString());
		groupMsgService.sendGroupMsg(groupMsg);
		return null;
	}
	
	//欢迎新成员
	private ReplyMsg handWelcome(NoticeMsg noticeMsg) {
//		群发消息
		if(!noticeMsg.getSub_type().equals("approve")) {
			return null;
		}
		
		String group_id = noticeMsg.getGroup_id();
		String user_id = noticeMsg.getUser_id();
		try {
			List<GroupMember> findGroupMemberInfo = groupMemberRepository.findGroupMemberInfo(group_id, user_id);
			if(findGroupMemberInfo!=null&&findGroupMemberInfo.size()>0) {
				groupMemberRepository.deleteAll(findGroupMemberInfo);
			}
			
			//懒得找单个查询的接口了 先直接拉取群列表的消息 后期如果空闲了再改
			GroupMsg groupMsg = new GroupMsg();
			groupMsg.setGroup_id(Long.parseLong(group_id));
			Result<?> groupMemberList = groupMsgService.getGroupMember(groupMsg);
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> data = (List<Map<String, Object>>) groupMemberList.getData();
			String nickname = "";
			String card = "";
			for(Map<String,Object> map : data) {
				if(String.valueOf(map.get("user_id")).equals(user_id)) {
					nickname = String.valueOf(map.get("nickname"));
					card = String.valueOf(map.get("card"));
					break;
				}
			}
			
			Long time = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String join_time = sdf.format(time);
			GroupMember groupMember = new GroupMember();
			groupMember.setCard(card);
			groupMember.setGroup_id(group_id);
			groupMember.setJoin_time(join_time);
			groupMember.setLeave_time(null);
			groupMember.setNickname(nickname);
			groupMember.setUser_id(user_id);
			groupMemberRepository.save(groupMember);
		} catch (Exception e) {
			System.out.println("添加新成员信息失败");
		}
		
		//添加新成员
		Map<String,Long> newGuyList = GlobalVariable.getNewFriendsMap();
		newGuyList.put(noticeMsg.getUser_id()+"-"+noticeMsg.getGroup_id(),System.currentTimeMillis()+1000*60*5);
		GlobalVariable.setNewFriendsMap(newGuyList);
		//固定回复welcome
		Random r = new Random();
		List<Msg> msgList = msgRepository.findMsgByExtQuestion("固定回复welcome");
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
	        //dp[i][j]表示源串A位置i到目标串B位置j处最低需要操作的次数
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
