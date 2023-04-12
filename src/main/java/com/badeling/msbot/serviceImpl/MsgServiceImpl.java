package com.badeling.msbot.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.controller.MsgZbCalculate;
import com.badeling.msbot.domain.*;
import com.badeling.msbot.entity.*;
import com.badeling.msbot.repository.*;
import com.badeling.msbot.service.*;
import com.badeling.msbot.util.Loadfont2;
import com.badeling.msbot.util.TranslateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MsgServiceImpl implements MsgService{
	
	@Autowired
	RankInfoRepository rankInfoRepository;
	@Autowired
	QuizOzQuestionRepository quizOzQuestionRepository;
	@Autowired
	QuizOzAnswerRepository quizOzAnswerRepository;
	@Autowired
	private MsgRepository msgRepository;
	@Autowired
	private MsgZbCalculate msgZbCalculate;
	@Autowired
	private GroupMsgService groupMsgService;
	@Autowired
	private MvpImageService mvpImageService;

	@Autowired
	private BanService banService;

	@Autowired
	private RecordService recordService;
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
	private MonvTimeRepository monvTimeRepository;
	@Autowired
	private BanTimeRepository banTimeRepository;

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
        	}else {
        		return null;
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
		//私聊
		//if(receiveMsg.getMessage_type().equals("private")&&receiveMsg.getUser_id().equals(MsbotConst.masterId)) {
        //      	System.err.println(receiveMsg.toString());
        //        	return handlePrivateMsg(receiveMsg);
        // }

        //黑名单的人
        for(String temp : MsbotConst.blackList) {
        	if(receiveMsg.getUser_id().equals(temp)) {
        		return null;
        	}
        }
		//处理禁言延时
		Timestamp time_now = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date_now = df.format(time_now);
		BanTime banTime = banTimeRepository.findRoleBynumber(receiveMsg.getSender().getUser_id(),date_now,receiveMsg.getGroup_id());

		if(banTime != null) {
			long delay_time = (time_now.getTime()-banTime.getUpdateTime().getTime())/ 1000;
			if (delay_time<=5 && banTime.getBan_times() != 1 ){
				String url = "http://127.0.0.1:5700/delete_msg";
				JSONObject postData = new JSONObject();
				postData.put("message_id",receiveMsg.getMessage_id());
				RestTemplate client = new RestTemplate();
				JSONObject json = client.postForEntity(url, postData, JSONObject.class).getBody();
				System.out.println(json);
				return null;
			}
		}

		//禁言信息
		String checkResultImage = banService.getCheckResultImage(receiveMsg.getRaw_message());

		if (checkResultImage.equals("禁言")){
			time_now = new Timestamp(System.currentTimeMillis());
			df = new SimpleDateFormat("yyyy-MM-dd");
			date_now = df.format(time_now);
			banTime = banTimeRepository.findRoleBynumber(receiveMsg.getSender().getUser_id(),date_now,receiveMsg.getGroup_id());
			if(banTime == null) {
				//查询无角色
				banTime = new BanTime();
				//设置群名片 如果没有 设置昵称
				if(receiveMsg.getSender().getCard()==null || receiveMsg.getSender().getCard().equals("")) {
					banTime.setName(receiveMsg.getSender().getNickname());
				}else {
					banTime.setName(receiveMsg.getSender().getCard());
				}
				//设置QQ号
				banTime.setUser_id(receiveMsg.getSender().getUser_id());
				//设置群号
				if(receiveMsg.getGroup_id().contains("101577006")) {
					banTime.setGroup_id("398359236");
				}else {
					banTime.setGroup_id(receiveMsg.getGroup_id());
				}
				Timestamp timestamp = new Timestamp(0);
				banTime.setUpdateTime(timestamp);
				banTime.setDate(date_now);
				banTime.setBan_times(0);
				banTime = banTimeRepository.save(banTime);
			}
			long delay_time = (time_now.getTime()-banTime.getUpdateTime().getTime())/ 1000;
			if (delay_time<=5){
				return null;
			}
			else {
				banTimeRepository.modifyUpdateBanTimes(
						banTime.getId(),
						banTime.getBan_times()+1,
						time_now
				);

				BanTime banTime1 = banTimeRepository.findBanTimesTodayByGroup(receiveMsg.getSender().getUser_id(),receiveMsg.getGroup_id());
				ReplyMsg replyMsg = new ReplyMsg();
				if (banTime1.getBan_times()==1){
					replyMsg.setReply("每日第一次禁言会被赦免,要乖噢～");
				}
				else {
					List<BanTime> list = banTimeRepository.findBanTimesByGroup(receiveMsg.getSender().getUser_id(),receiveMsg.getGroup_id());
					int ban_times=0;
					for (int i =0; i < list.size(); i++) {
						ban_times += list.get(i).getBan_times();
					}
					replyMsg.setBan_duration((ban_times/5+1)*30*60);
					replyMsg.setBan(true);
					replyMsg.setReply("[CQ:image,file=save/AB59F6053D317B67646AA3B363B87415]");
				}
				replyMsg.setAt_sender(true);
				replyMsg.setAuto_escape(false);
				System.out.println(replyMsg);
				return replyMsg;
			}
		}

		List<MsgNoPrefix> result = msgNoPrefixRepository.findMsgNPList();
		for(MsgNoPrefix m : result) {
			if(m.isExact()&&receiveMsg.getRaw_message().contains(m.getQuestion())) {
				System.out.println(m.getAnswer());
				if (m.getQuestion().contains("md") && receiveMsg.getRaw_message().contains("md5")){
					break;
				}
				else if (m.getAnswer().equals("禁言")){

					time_now = new Timestamp(System.currentTimeMillis());
					df = new SimpleDateFormat("yyyy-MM-dd");
					date_now = df.format(time_now);

					banTime = banTimeRepository.findRoleBynumber(receiveMsg.getSender().getUser_id(),date_now,receiveMsg.getGroup_id());
					if(banTime == null) {
						//查询无角色
						banTime = new BanTime();
						//设置群名片 如果没有 设置昵称
						if(receiveMsg.getSender().getCard()==null || receiveMsg.getSender().getCard().equals("")) {
							banTime.setName(receiveMsg.getSender().getNickname());
						}else {
							banTime.setName(receiveMsg.getSender().getCard());
						}
						//设置QQ号
						banTime.setUser_id(receiveMsg.getSender().getUser_id());
						//设置群号
						if(receiveMsg.getGroup_id().contains("101577006")) {
							banTime.setGroup_id("398359236");
						}else {
							banTime.setGroup_id(receiveMsg.getGroup_id());
						}
						Timestamp timestamp = new Timestamp(0);
						banTime.setUpdateTime(timestamp);
						banTime.setDate(date_now);
						banTime.setBan_times(0);
						banTime = banTimeRepository.save(banTime);
					}

					banTimeRepository.modifyUpdateBanTimes(
							banTime.getId(),
							banTime.getBan_times()+1,
							time_now
					);

					List<BanTime> list = banTimeRepository.findBanTimesByGroup(receiveMsg.getSender().getUser_id(),receiveMsg.getGroup_id());
					int ban_times=0;
					for (int i = 0; i < list.size(); i++) {
						ban_times += list.get(i).getBan_times();
					}
					ReplyMsg replyMsg = new ReplyMsg();
					replyMsg.setBan_duration((ban_times/5+1)*30*60);
					replyMsg.setAt_sender(true);
					replyMsg.setAuto_escape(false);
					replyMsg.setBan(true);
					replyMsg.setReply("[CQ:image,file=save/AB59F6053D317B67646AA3B363B87415]");
					System.out.println(replyMsg);
					return replyMsg;
				}
			}
		}

		if (receiveMsg.getRaw_message().startsWith(MsbotConst.botName)) {
			System.out.println(receiveMsg.toString());
			return handleNameMsg(receiveMsg);
		}
		else if (receiveMsg.getRaw_message().startsWith("[CQ:at,qq=" + MsbotConst.botId + "]")) {
			receiveMsg.setRaw_message(receiveMsg.getRaw_message().replace("[CQ:at,qq=" + MsbotConst.botId + "]", MsbotConst.botName));
			System.out.println(receiveMsg.toString());
			return handleNameMsg(receiveMsg);
		}
		else if ((receiveMsg.getRaw_message().contains("气象") || receiveMsg.getRaw_message().contains("MVP")) && receiveMsg.getRaw_message().length() <= 40) {
			System.out.println(receiveMsg.toString());
			return null;
//        	return handleMvpMsg(receiveMsg);
		} else if (receiveMsg.getRaw_message().contains("[CQ:image,file=")) {
			//识别气象图
			System.out.println(receiveMsg.toString());
			if (receiveMsg.getRaw_message().contains("高精度识图")) {
				return handRecognize2(receiveMsg);
			}
			if (receiveMsg.getRaw_message().contains("识图")) {
				return handRecognize(receiveMsg);
			}
			if (receiveMsg.getRaw_message().startsWith("39")) {
				return handRecognizeOz39(receiveMsg);
			}
		}
		else if (receiveMsg.getRaw_message().length() >= 2 && receiveMsg.getRaw_message().substring(0, 2).contains("翻译")) {
			System.out.println(receiveMsg.toString());
			return handTransMsg(receiveMsg);
		} else if (receiveMsg.getRaw_message().length() >= 4 && receiveMsg.getRaw_message().startsWith("联盟查询")) {
			return handLegionRank(receiveMsg);
		}
		else if (receiveMsg.getRaw_message().length() >= 4 && receiveMsg.getRaw_message().startsWith("查询绑定")) {
//        	查询绑定badeling
			return handAddRankName(receiveMsg);
		}

        //收币 or 卖币
//        Pattern r = Pattern.compile(".*\\d+[e|E|\\亿].*");
//		Matcher m = r.matcher(receiveMsg.getRaw_message());
//		boolean matches = m.matches();
//    	if(matches) {
//    		handMemberSellAndBuy(receiveMsg);
//    	}

		handReplyMsg(receiveMsg);

		//re-read
    	receiveMsg.setRaw_message(receiveMsg.getMessage());
    	return handRereadMsg(receiveMsg);

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
				raw_message = raw_message + list.get(i).get("words");
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
			reply = reply + "MatchQue : "+MatchQoz.getQuestion()+"\r\n \r\n";

			String a = list.get(count-4).get("words").replaceAll("  ", " ").replaceAll("  ", " ");
			String b = list.get(count-3).get("words").replaceAll("  ", " ").replaceAll("  ", " ");
			String c = list.get(count-2).get("words").replaceAll("  ", " ").replaceAll("  ", " ");
			String d = list.get(count-1).get("words").replaceAll("  ", " ").replaceAll("  ", " ");

			Set<QuizOzAnswer> answers = MatchQoz.getAnswers();
			Iterator<QuizOzAnswer> iterator = answers.iterator();

			reply = reply + "所有答案 : ";
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

			reply = reply + "匹配答案 : " + MatchQoa.getAnswer() + "\r\n";
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
				//识图
				String[] result = mvpImageService.handHigherImageMsg(receiveMsg);
				//接受数据
				String raw_message = "高精度识图结果：\r\n";
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

		if(reReadMsg.getMes_count()>300&&reReadMsg.getCount()==1) {
			reReadMsg.setMes_count(1);

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

		//翻译
		if(raw_message.contains(MsbotConst.botName+"翻译")) {
			raw_message = raw_message.substring(raw_message.indexOf("翻译")+2);
			raw_message = raw_message.replaceAll("\r",".");
			raw_message = raw_message.replaceAll("\n",".");
			System.out.println(raw_message);
			String transResult;
			try {
				transResult = TranslateUtil.getTransResult(raw_message, "auto", "auto");
				replyMsg.setReply(transResult);
				return replyMsg;

			} catch (IOException e) {
			}
			return null;
		}

		//说
		if(raw_message.contains(MsbotConst.botName+"说")){
			raw_message = raw_message.substring(raw_message.indexOf("说")+1);
			raw_message = raw_message.replaceAll("\r",".");
			raw_message = raw_message.replaceAll("\n",".");
			System.out.println(raw_message);

			try {
				String result = recordService.sendRecordMsg(raw_message);
				replyMsg.setReply(result);


			} catch (Exception e) {
				e.printStackTrace();
				replyMsg.setReply("失败");
			}
			replyMsg.setAt_sender(false);
			return replyMsg;
		}

		//上色
		if(raw_message.contains(MsbotConst.botName+"上色")) {
			try {
				raw_message = "上色结果：\r\n";
				String result = mvpImageService.handColorImageMsg(receiveMsg);
				replyMsg.setReply(raw_message+result);

			} catch (Exception e) {
				e.printStackTrace();
				replyMsg.setReply("上色失败");
			}
			replyMsg.setAt_sender(true);
			return replyMsg;
		}

		//画头像
		if(raw_message.contains(MsbotConst.botName+"画头像")) {
			try {
				raw_message = "绘画结果：\r\n";
				String result = mvpImageService.handAnimeImageMsg(receiveMsg);
				replyMsg.setReply(raw_message+result);

			} catch (Exception e) {
				e.printStackTrace();
				replyMsg.setReply("绘画失败");
			}
			replyMsg.setAt_sender(true);
			return replyMsg;
		}

		//识图
		if(raw_message.contains(MsbotConst.botName+"识图")) {
			//识图
			String[] result = mvpImageService.handImageMsg(receiveMsg);
			raw_message = "";

			//接受数据
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
			replyMsg.setReply(raw_message);
			return replyMsg;
		}

		//高精度识图
		if(raw_message.contains(MsbotConst.botName+"高精度识图")){
			//识图
			String[] result = mvpImageService.handHigherImageMsg(receiveMsg);
			//接受数据
			raw_message = "高精度识图结果：\r\n";
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
			replyMsg.setReply(raw_message);
			return replyMsg;
		}

		//roll点
		if(raw_message.contains(MsbotConst.botName+"roll")){
			try {
				Random r = new Random();
				int roll = r.nextInt(100)+1;
				replyMsg.setReply("点数为:"+roll);
				return replyMsg;
			} catch (Exception e) {
				replyMsg.setReply("出现了一个意料之外的错误");
				e.printStackTrace();
				return replyMsg;
			}
		}

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
				if(receiveMsg.getGroup_id().contains("101577006")) {
					roleDmg.setGroup_id("398359236");
				}else {
					roleDmg.setGroup_id(receiveMsg.getGroup_id());
				}
				roleDmg.setCommonDmg(100);
				roleDmg.setBossDmg(200);
				System.out.println("roleDmg:");
				System.out.println(roleDmg);
				roleDmg = roleDmgRepository.save(roleDmg);
			}
			try {
				for(String temp : split) {
					temp.replace("%", "");
					if(temp.contains("伤害")) {
						roleDmg.setCommonDmg(Integer.parseInt(temp.replace(MsbotConst.botName,"").replace("伤害", "")));
					}else if(temp.toLowerCase().contains("boss")) {
						temp = temp.toLowerCase();
						roleDmg.setBossDmg(Integer.parseInt(temp.replace(MsbotConst.botName,"").replace("boss", "")));
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


		//怪物查询
		if(raw_message.startsWith(MsbotConst.botName+"怪物")||raw_message.startsWith(MsbotConst.botName+" 怪物")) {
			if(raw_message.equals(MsbotConst.botName+"怪物")||raw_message.equals(MsbotConst.botName+" 怪物")) {
				replyMsg.setReply("爬，你才是怪物。");
				return replyMsg;
			}else if(raw_message.equals(MsbotConst.botName+"怪物更新信息")){
				if(receiveMsg.getUser_id().equals(MsbotConst.masterId)) {
					wzXmlService.updateMobInfo();
					replyMsg.setReply("MobInfo更新完成");
				}else {
					replyMsg.setReply("宁是什么东西也配命令老娘？爬爬爬！");
				}
				return replyMsg;
			}else {
				if(raw_message.contains(MsbotConst.botName+"怪物")||raw_message.contains(MsbotConst.botName+" 怪物")) {
					raw_message = raw_message.substring(raw_message.indexOf("怪物")+2);
					if(raw_message.indexOf(" ")==0) {
						raw_message = raw_message.substring(1);
					}
					//查询怪物
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
					groupMsg.setMessage("[CQ:at,qq="+ receiveMsg.getSender().getUser_id() +"]"+"未查询到角色信息，默认伤害100,boss伤200。你可通过指令【"+MsbotConst.botName+" 伤害50 boss300】命令修改角色信息");
					groupMsgService.sendGroupMsg(groupMsg);
				}

//				shortMsg += " = " + String.format("%.2f", ign) + "%";
//				shortMsg += "\r\n为防止刷屏，详细计算的部分将以私聊的形式发送于您。";

				ign2 = ign + (100-ign)*20/100;
				String replyM = "你之前的无视：" + ign_before + "%(" + ign_before2 + "%)\r\n" + "计算后的无视：" + String.format("%.2f", ign) + "%(" + String.format("%.2f", ign2) + "%)\r\n";
				replyM += "角色数据 伤害:" + roleDmg.getCommonDmg() + "% boss:" + roleDmg.getBossDmg() + "%\r\n(括号为核心20%无视加成结果)\r\n";

				replyM += "//----超高防对比-----//\r\n";
				replyM += "塞伦提升率（380超高防）：" + String.format("%.2f", (defAndign(380, ign)/defAndign(380, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(380, ign2)/defAndign(380, ign_before2)-1)*100) + "%)\r\n";
				replyM += "原伤害" + defAndign(380, ign_before) + "%(" + defAndign(380, ign_before2) + "%)\r\n";
				replyM += "现伤害" + defAndign(380, ign) + "%(" + defAndign(380, ign2) +  "%)\r\n";
				replyM += "相当于提升了" + String.format("%.2f",(defAndign(380, ign)/defAndign(380, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%(" + String.format("%.2f", (defAndign(380, ign2)/defAndign(380, ign_before2)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%)点boss伤害\r\n";

				replyM += "//-----高防对比-----//\r\n";
				replyM += "斯乌提升率（300高防）：" + String.format("%.2f", (defAndign(300, ign)/defAndign(300, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(300, ign2)/defAndign(300, ign_before2)-1)*100) + "%)\r\n";
				replyM += "原伤害" + defAndign(300, ign_before) + "%(" + defAndign(300, ign_before2) + "%)\r\n";
				replyM += "现伤害" + defAndign(300, ign) + "%(" + defAndign(300, ign2) +  "%)\r\n";
				replyM += "相当于提升了" + String.format("%.2f",(defAndign(300, ign)/defAndign(300, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%(" + String.format("%.2f", (defAndign(300, ign2)/defAndign(300, ign_before2)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%)点boss伤害\r\n";

				replyM += "//-----中防对比-----//\r\n";
				replyM += "进阶贝伦提升率（200中防）：" + String.format("%.2f", (defAndign(200, ign)/defAndign(200, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(200, ign2)/defAndign(200, ign_before2)-1)*100) + "%)\r\n";
				replyM += "原伤害" + defAndign(200, ign_before) + "%(" + defAndign(200, ign_before2) + "%)\r\n";
				replyM += "现伤害" + defAndign(200, ign) + "%(" + defAndign(200, ign2) +  "%)\r\n";
				replyM += "相当于提升了" + String.format("%.2f",(defAndign(200, ign)/defAndign(200, ign_before)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%(" + String.format("%.2f", (defAndign(200, ign2)/defAndign(200, ign_before2)-1)*(100+roleDmg.getCommonDmg()+roleDmg.getBossDmg())) + "%)点boss伤害";

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
//				PrivateMsg privateMsg = new PrivateMsg();
//				privateMsg.setUser_id(Long.parseLong(receiveMsg.getUser_id()));
//				privateMsg.setMessage(replyM);
//				groupMsgService.sendPrivateMsg(privateMsg);
				return replyMsg;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		//复读机周报
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
			//逆推星星[等级][主属][火花主属][总攻击][火花攻击][当前星星]
			/**
			 	逆推星星
			 	等级：
			 	总主属：
			 	火花主属：
			 	总攻击：
			 	火花攻击：
			 	当前星星：

			 	蠢猫 150级16星

			 	蠢猫 160级13星428攻
		}
			 */
			if(raw_message.contains("级")&&raw_message.contains("星")&&raw_message.contains("攻")) {
				raw_message = raw_message.replaceAll(" ", "").substring(2);
				int level = Integer.parseInt(raw_message.substring(0,raw_message.indexOf("级")));
//				int stat = Integer.parseInt(split[2].substring(4));
				int stat = 0;
//				int fireStat = Integer.parseInt(split[3].substring(5));
				int fireStat = 0;
				int att = Integer.parseInt(raw_message.substring(raw_message.indexOf("星")+1,raw_message.indexOf("攻")));
//				int fireAtt = Integer.parseInt(split[5].substring(5));
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
//				int finalStat = starForce[0]+fireStat;
				int finalAtt = starForce[1]+fireAtt;
//				String result = "数据：等级"+level+" 主属" + stat + " 火花主属" + fireStat + "\r\n"
//						+ "攻击" + att + " 火花攻击" + fireAtt + " 星星" + nowStar + "\r\n"
//						+ "逆推星星结果为：\r\n"
//						+ "计算火花：主属" +  finalStat + " 攻击"+ finalAtt + "\r\n"
//						+ "不计算火花：主属" +  starForce[0] + " 攻击"+ starForce[1];
				String result = "0星状态下满卷攻击为：" + finalAtt;
				replyMsg.setReply(result);
				return replyMsg;
			}


			//正推星星
			if(raw_message.contains("级")&&raw_message.contains("星")) {
				raw_message = raw_message.replaceAll(" ", "").substring(2);
				//level 130 140 150 160 200
				//蠢猫武器160
				//星星武器[等级][主属][火花主属][总攻击][火花攻击][当前星星][目标星星]
				/**
			 	正推星星
			 	等级：
			 	类型：
			 	总主属：
			 	火花主属：
			 	总攻击：
			 	火花攻击：
			 	当前星星：
			 	目标星星：

			 	蠢猫 150级16星
			 */
				boolean isWeapon = false;
				int level = Integer.parseInt(raw_message.substring(0,raw_message.indexOf("级")));
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
//				String result = "数据：等级"+level+" 主属" + stat + " 火花主属" + fireStat + "\r\n"
//						+ "攻击" + att + " 火花攻击" + fireAtt + "\r\n"
//						+ "当前星星" + nowStar + " 目标星星" + targetStar + "\r\n"
//						+ "计算星星结果为：\r\n"
//						+ "计算火花：主属" +  finalStat + " 攻击"+ finalAtt + "\r\n"
//						+ "不计算火花：主属" +  starForce[0] + " 攻击"+ starForce[1];
				String result = level + "级装备" + targetStar + "星的加成为：主属" + finalStat + " 攻击"+ finalAtt;
				replyMsg.setReply(result);
				return replyMsg;

			}
		} catch (Exception e) {
			replyMsg.setReply("输入数据异常\r\n防具正推：蠢猫[等级][星星]\r\n" +
					"eg：蠢猫150级17星\r\n" +
					"武器逆推：蠢猫[等级][星星][攻击]\r\n" +
					"eg：蠢猫160级13星428攻");
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

		if(raw_message.contains("抽蠢猫")) {
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

		if(raw_message.startsWith(MsbotConst.botName+"结账")){
			if (raw_message.contains("[CQ:at")){
				try {
					int aIndex = receiveMsg.getRaw_message().indexOf("[CQ:at,qq=")+10;
					int bIndex = receiveMsg.getRaw_message().indexOf("]");
					String findNumber = receiveMsg.getRaw_message().substring(aIndex,bIndex);


					if(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)||isAdminMsg(receiveMsg.getUser_id())) {
						List<MonvTime> list = monvTimeRepository.findCostByGroup(findNumber,receiveMsg.getGroup_id());

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

						String mes = "\r\n";
						if(list.size() == 0) {
							mes += "未查到: "+ map.get(findNumber) +" 的抽奖记录";
							replyMsg.setAt_sender(true);
							replyMsg.setReply(mes);
						}
						else {
							mes += "氪佬: "+map.get(list.get(0).getUser_id()) +"\r\n";
							int prize_1 =0,prize_2 =0,prize_3 =0,prize_4 =0,prize_5 =0;

							for (int i = 0; i < list.size(); i++) {
								prize_1 += list.get(i).getPrize_1();
								prize_2 += list.get(i).getPrize_2();
								prize_3 += list.get(i).getPrize_3();
								prize_4 += list.get(i).getPrize_4();
								prize_5 += list.get(i).getPrize_5();
							}

							mes += "一爆 "+prize_1+"\r\n";
							mes += "二爆 "+prize_2+"\r\n";
							mes += "三爆 "+prize_3+"\r\n";
							mes += "四爆 "+prize_4+"\r\n";
							mes += "五爆 "+prize_5+"\r\n";
							mes += "氪金总额: "+(prize_1+prize_2+prize_3+prize_4+prize_5)*100+" 悲伤币";

							replyMsg.setAt_sender(true);
							replyMsg.setReply(mes);
						}
					}
					else {
						replyMsg.setAt_sender(false);
						replyMsg.setReply("[CQ:at,qq=" + receiveMsg.getUser_id()  + "]" + "宁是什么东西也配命令老娘？爬爬爬！");
					}
					return replyMsg;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					String findNumber = receiveMsg.getUser_id();
					List<MonvTime> list = monvTimeRepository.findCostByGroup(findNumber,receiveMsg.getGroup_id());

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
						}
						else {
							//有群名片
							map.put(a, c);
						}
					}

					String mes = "\r\n";

					if(list.size() == 0) {
							mes += "未查到: "+ map.get(findNumber) +" 的抽奖记录";
							replyMsg.setAt_sender(true);
							replyMsg.setReply(mes);
						}
					else {
							mes += "氪佬: "+map.get(list.get(0).getUser_id()) +"\r\n";
							int prize_1 =0,prize_2 =0,prize_3 =0,prize_4 =0,prize_5 =0;

							for (int i = 0; i < list.size(); i++) {
								prize_1 += list.get(i).getPrize_1();
								prize_2 += list.get(i).getPrize_2();
								prize_3 += list.get(i).getPrize_3();
								prize_4 += list.get(i).getPrize_4();
								prize_5 += list.get(i).getPrize_5();
							}

							mes += "一爆 "+prize_1+"\r\n";
							mes += "二爆 "+prize_2+"\r\n";
							mes += "三爆 "+prize_3+"\r\n";
							mes += "四爆 "+prize_4+"\r\n";
							mes += "五爆 "+prize_5+"\r\n";
							mes += "氪金总额: "+(prize_1+prize_2+prize_3+prize_4+prize_5)*100+" 悲伤币";

							replyMsg.setAt_sender(true);
							replyMsg.setReply(mes);
						}

					return replyMsg;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (raw_message.contains("禁言周报")) {
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

			List<BanTime> list = banTimeRepository.findBanTimesWeeklyByGroup(receiveMsg.getGroup_id());
			String message="本周禁言榜榜首是：\r\n";
			if(list.size()>0) {
				for (int i = 0; i < list.size(); i++) {
					if (i==1){
						message += "此外，以下成员也榜上有名：\r\n";
					}
					message += map.get(list.get(i).getUser_id()) + "  被禁言次数: "+ list.get(i).getBan_times() +"\r\n";
				}
			}
			else {
				message += "虚位以待\r\n";
			}

			message += "——————————————————\r\n以上群友新的一周要乖噢～！uwu";
			replyMsg.setReply(message);
			return replyMsg;
		}

		if (raw_message.contains("抽奖日报")||raw_message.contains("魔女日报")||raw_message.contains("百分百日报")) {
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

			String message = "\r\n本日氪佬是：\r\n";
			List<MonvTime> list = monvTimeRepository.find3thCostByGroup(receiveMsg.getGroup_id());
			if(list!=null) {
				message += map.get(list.get(0).getUser_id()) + "  氪金总额: "+ list.get(0).getPrize()*100 +" 悲伤币\r\n";
				message += "此外，以下两名成员获得了亚军和季军，也是非常优秀的氪佬：\r\n";
				if(list.size()>1) {
					message += map.get(list.get(1).getUser_id()) +  "  氪金总额: "+ list.get(1).getPrize()*100 +" 悲伤币\r\n";
				}else{
					message += "虚位以待\r\n";
				}
				if(list.size()>2) {
					message += map.get(list.get(2).getUser_id()) +  "  氪金总额: "+ list.get(2).getPrize()*100 +" 悲伤币\r\n";
				}else{
					message += "虚位以待\r\n";
				}
			}
			else {
				message += "虚位以待\r\n";
			}
			message += "——————————————————\r\n 本日欧皇是：\r\n";
			List<MonvTime> list2 = monvTimeRepository.find3thLuckByGroup(receiveMsg.getGroup_id());
			if(list!=null) {
				message += map.get(list2.get(0).getUser_id()) +
						"\r\n五爆: "+ list2.get(0).getPrize_5() +
						" , 四爆: "+ list2.get(0).getPrize_4() +
						" , 三爆: "+ list2.get(0).getPrize_3() +
						" , 二爆: "+ list2.get(0).getPrize_2() +
						" , 一爆: "+ list2.get(0).getPrize_1()+"\r\n";
				message += "此外，以下两名成员获得了亚军和季军，也是非常优秀的欧皇：\r\n";
				if(list2.size()>1) {
					message += map.get(list2.get(1).getUser_id()) +
							"  五爆: "+ list2.get(1).getPrize_5() +
							" , 四爆: "+ list2.get(1).getPrize_4() +
							" , 三爆: "+ list2.get(1).getPrize_3() +
							" , 二爆: "+ list2.get(1).getPrize_2() +
							" , 一爆: "+ list2.get(1).getPrize_1()+"\r\n";
				}
				else{
					message += "虚位以待\r\n";
				}
				if(list2.size()>2) {
					message += map.get(list2.get(2).getUser_id()) +
							"  五爆: "+ list2.get(2).getPrize_5() +
							" , 四爆: "+ list2.get(2).getPrize_4() +
							" , 三爆: "+ list2.get(2).getPrize_3() +
							" , 二爆: "+ list2.get(2).getPrize_2() +
							" , 一爆: "+ list2.get(2).getPrize_1()+"\r\n";
				}
				else{
					message += "虚位以待\r\n";
				}
			}
			else {
				message += "虚位以待\r\n";
			}
			message += "——————————————————\r\n为了成为欧洲人，努力氪金吧！uwu";
			replyMsg.setReply(message);
			return replyMsg;
		}

		if(raw_message.contains("抽奖统计")||raw_message.contains("魔女统计")||raw_message.contains("百分百统计")) {
			Timestamp time_now = new Timestamp(System.currentTimeMillis());
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String date_now = df.format(time_now);

			MonvTime monvTime = monvTimeRepository.findRoleBynumber(receiveMsg.getSender().getUser_id(),date_now,receiveMsg.getGroup_id());
			if(monvTime == null) {
				//查询无角色
				monvTime = new MonvTime();
				//设置群名片 如果没有 设置昵称
				if(receiveMsg.getSender().getCard()==null || receiveMsg.getSender().getCard().equals("")) {
					monvTime.setName(receiveMsg.getSender().getNickname());
				}else {
					monvTime.setName(receiveMsg.getSender().getCard());
				}
				//设置QQ号
				monvTime.setUser_id(receiveMsg.getSender().getUser_id());
				//设置群号
				if(receiveMsg.getGroup_id().contains("101577006")) {
					monvTime.setGroup_id("398359236");
				}else {
					monvTime.setGroup_id(receiveMsg.getGroup_id());
				}
				Timestamp timestamp = new Timestamp(0);
				monvTime.setUpdateTime(timestamp);
				monvTime.setDate(date_now);
				monvTime.setPrize(0,0,0,0,0);
				monvTime = monvTimeRepository.save(monvTime);
			}

			String mes = "\r\n";
			mes += "一爆 "+(monvTime.getPrize_1())+"\r\n";
			mes += "二爆 "+(monvTime.getPrize_2())+"\r\n";
			mes += "三爆 "+(monvTime.getPrize_3())+"\r\n";
			mes += "四爆 "+(monvTime.getPrize_4())+"\r\n";
			mes += "五爆 "+(monvTime.getPrize_5())+"\r\n";
			mes += "氪金总额: "+(monvTime.getPrize_1()+monvTime.getPrize_2()+monvTime.getPrize_3()+monvTime.getPrize_4()+monvTime.getPrize_5())*100+" 悲伤币";

			replyMsg.setAt_sender(true);
			replyMsg.setReply(mes);
			return replyMsg;

		}

		if(raw_message.startsWith(MsbotConst.botName+"禁言统计")){
			if (raw_message.contains("[CQ:at")){
				try {
					int aIndex = receiveMsg.getRaw_message().indexOf("[CQ:at,qq=")+10;
					int bIndex = receiveMsg.getRaw_message().indexOf("]");
					String findNumber = receiveMsg.getRaw_message().substring(aIndex,bIndex);

					if(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)||isAdminMsg(receiveMsg.getUser_id())) {
						List<BanTime>list = banTimeRepository.findBanTimesByGroup(findNumber,receiveMsg.getGroup_id());

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

						String mes = "\r\n";
						if(list.size() == 0) {
							mes += "未查到: "+ map.get(findNumber) +" 的禁言记录";
							replyMsg.setAt_sender(true);
							replyMsg.setReply(mes);
						}
						else {
							mes += "成员: "+map.get(list.get(0).getUser_id()) +"\r\n";
							int ban_times=0;
							for (int i = 0; i < list.size(); i++) {
								ban_times += list.get(i).getBan_times();
							}
							mes += "禁言次数："+ban_times+"\r\n";
							mes += "处罚时间："+(ban_times/5+1)*30+" 分钟\r\n";

							replyMsg.setAt_sender(true);
							replyMsg.setReply(mes);
						}
					}
					else {
						replyMsg.setAt_sender(false);
						replyMsg.setReply("[CQ:at,qq=" + receiveMsg.getUser_id()  + "]" + "宁是什么东西也配命令老娘？爬爬爬！");
					}
					return replyMsg;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					String findNumber = receiveMsg.getUser_id();
					List<BanTime> list = banTimeRepository.findBanTimesByGroup(findNumber,receiveMsg.getGroup_id());

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
						}
						else {
							//有群名片
							map.put(a, c);
						}
					}

					String mes = "\r\n";

					if(list.size() == 0) {
						mes += "未查到: "+ map.get(findNumber) +" 的禁言记录";
						replyMsg.setAt_sender(true);
						replyMsg.setReply(mes);
					}
					else {
						mes += "成员: "+map.get(list.get(0).getUser_id()) +"\r\n";
						int ban_times=0;
						for (int i = 0; i < list.size(); i++) {
							ban_times += list.get(i).getBan_times();
						}
						mes += "禁言次数："+ban_times+"\r\n";
						mes += "处罚时间："+(ban_times/5+1)*30+" 分钟\r\n";

						replyMsg.setAt_sender(true);
						replyMsg.setReply(mes);
					}

					return replyMsg;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
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

/**
 * 			这一段代码本来是因为最近周日冒险岛没加索引 所以添加一个新从首页查询 再搜索查询
 * 			但是我写完之后发现周日冒险岛从首页挤出去了 现在首页也找不到
 * 			所以先放在这
  			 */

//			//官网首页找
//			try {
//				String url = "http://mxd.web.sdo.com/web6/home/index.asp";
//				Document doc = Jsoup.connect(url).get();
//				Elements eleList = doc.select("div.news-list");
//
//				for(Element element : eleList) {
//					Elements elementsByTag2 = element.getElementsByTag("li");
//					for(Element tempElement : elementsByTag2) {
//						System.out.println(tempElement.text());
//						if(tempElement.text().contains("转蛋")) {
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
////							message = message + "官网链接：" + url + "\r\n";
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
			//搜索页面找
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
				message = message + "官网链接：" + url + "\r\n";
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
				replyMsg.setReply("查询失败");
				return replyMsg;
			}
		}

		//魔女
		if(raw_message.contains("抽奖")||raw_message.contains("魔女")||raw_message.contains("百分百")) {
			String mes;
			Timestamp time_now = new Timestamp(System.currentTimeMillis());

			SimpleDateFormat formatHours = new SimpleDateFormat("HH");
			int hours = Integer.parseInt(formatHours.format(time_now));

			if(hours>=0 && hours<=11){
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String date_now = df.format(time_now);
				MonvTime monvTime = monvTimeRepository.findRoleBynumber(receiveMsg.getSender().getUser_id(),date_now,receiveMsg.getGroup_id());

				if(monvTime == null) {
					//查询无角色
					monvTime = new MonvTime();
					//设置群名片 如果没有 设置昵称
					if(receiveMsg.getSender().getCard()==null || receiveMsg.getSender().getCard().equals("")) {
						monvTime.setName(receiveMsg.getSender().getNickname());
					}else {
						monvTime.setName(receiveMsg.getSender().getCard());
					}
					//设置QQ号
					monvTime.setUser_id(receiveMsg.getSender().getUser_id());
					//设置群号
					if(receiveMsg.getGroup_id().contains("101577006")) {
						monvTime.setGroup_id("398359236");
					}else {
						monvTime.setGroup_id(receiveMsg.getGroup_id());
					}
					Timestamp timestamp = new Timestamp(0);
					monvTime.setUpdateTime(timestamp);
					monvTime.setDate(date_now);
					monvTime.setPrize(0,0,0,0,0);
					monvTime = monvTimeRepository.save(monvTime);
				}

				long cd_time = (time_now.getTime()-monvTime.getUpdateTime().getTime())/ 1000;
				if (cd_time >= MsbotConst.monv_cd){
					monvTime.setUpdateTime(time_now);
					monvTimeRepository.modifyUpdateTime(monvTime.getId(), monvTime.getUpdateTime());

					try {
						mes = drawService.startDrawMs(monvTime);
					} catch (Exception e) {
						e.printStackTrace();
						mes = "图片文件缺失。";
					}
					replyMsg.setAt_sender(true);
					replyMsg.setReply(mes);
					return replyMsg;
				}
				else {
					mes = "抽奖冷却中,剩余"+(MsbotConst.monv_cd-cd_time)+"秒";
					replyMsg.setAt_sender(true);
					replyMsg.setReply(mes);
					return replyMsg;
				}
			}
			else {
				mes = "抽奖时间: 每日0-12点,请稍后...";
				replyMsg.setAt_sender(true);
				replyMsg.setReply(mes);
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

		//禁言
		if(raw_message.startsWith(MsbotConst.botName+"禁言")&&raw_message.contains("[CQ:at")) {
			if(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)||isAdminMsg(receiveMsg.getUser_id())) {
				try {
					int aIndex = receiveMsg.getRaw_message().indexOf("[CQ:at,qq=")+10;
					int bIndex = receiveMsg.getRaw_message().indexOf("]");
					String findNumber = receiveMsg.getRaw_message().substring(aIndex,bIndex);
					if(isAdminMsg(findNumber)|| findNumber.equals(MsbotConst.masterId)||findNumber.equals(MsbotConst.botId)||findNumber.equals("2419570484")) {
						replyMsg.setReply("禁言防御");
					}
					else {
						String imageName = "[CQ:image,file=save/AB59F6053D317B67646AA3B363B87415]";
						replyMsg.setAt_sender(false);
						String url = "http://127.0.0.1:5700/set_group_ban";
						JSONObject postData = new JSONObject();
						postData.put("group_id",receiveMsg.getGroup_id());
						postData.put("user_id",findNumber);
						postData.put("duration",30*60);
						RestTemplate client = new RestTemplate();
						JSONObject json = client.postForEntity(url, postData, JSONObject.class).getBody();
						System.out.println(json);
						//replyMsg.setBan(true);
						replyMsg.setReply("[CQ:at,qq=" + findNumber + "]"+imageName);
					}
				}
				catch (Exception e) {
					replyMsg.setReply("出现异常");
				}
			}
			else {
				replyMsg.setReply("宁是什么东西也配命令老娘？爬爬爬！");
			}
			return replyMsg;
		}
		//解除禁言
		if(raw_message.startsWith(MsbotConst.botName+"解除禁言")&&raw_message.contains("[CQ:at")) {
			if(receiveMsg.getUser_id().equalsIgnoreCase(MsbotConst.masterId)||isAdminMsg(receiveMsg.getUser_id())) {
				try {
					int aIndex = receiveMsg.getRaw_message().indexOf("[CQ:at,qq=")+10;
					int bIndex = receiveMsg.getRaw_message().indexOf("]");
					String findNumber = receiveMsg.getRaw_message().substring(aIndex,bIndex);
					if(isAdminMsg(findNumber)|| findNumber.equals(MsbotConst.masterId)||findNumber.equals(MsbotConst.botId)||findNumber.equals("2419570484")) {
						replyMsg.setReply("防御");
					}
					else {
						String imageName = "[CQ:image,file=save/AB59F6053D317B67646AA3B363B87415]";
						replyMsg.setAt_sender(false);
						String url = "http://127.0.0.1:5700/set_group_ban";
						JSONObject postData = new JSONObject();
						postData.put("group_id",receiveMsg.getGroup_id());
						postData.put("user_id",findNumber);
						postData.put("duration",0);
						RestTemplate client = new RestTemplate();
						JSONObject json = client.postForEntity(url, postData, JSONObject.class).getBody();
						System.out.println(json);
						//replyMsg.setBan(true);
						replyMsg.setReply("[CQ:at,qq=" + findNumber + "]"+"要乖噢～");
					}
				}
				catch (Exception e) {
					replyMsg.setReply("出现异常");
				}
			}
			else {
				replyMsg.setReply("宁是什么东西也配命令老娘？爬爬爬！");
			}

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
			 replyMsg.setAt_sender(true);
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
					if((result.get("message")+"").contains("请求成功")) {
						String reply = tuLingMsg.substring(tuLingMsg.indexOf("content")+10,tuLingMsg.indexOf("\",\"typed\":"));
						replyMsg.setReply(reply);
						return replyMsg;
					}
				}
			}



//		if(!raw_message.contains("[CQ")) {
//			//图灵机器人
//			HashMap<String, Object> map = new HashMap<>();
//			//reqType
//			map.put("reqType", 0);
//			//perception 内容
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
//			//消息传给图灵
//			String json = JSONObject.toJSONString(map);
//			System.out.println(json);
//			String tuLingMsg = groupMsgService.tuLingMsg(json);
//			System.out.println(tuLingMsg);
//			//图灵消息返回 读取消息
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

	//欢迎新成员
	private ReplyMsg handWelcome(NoticeMsg noticeMsg) {
//		群发消息
		if(!noticeMsg.getSub_type().equals("approve")) {
			return null;
		}

//		GroupMsg groupMsg = new GroupMsg();
//		String message = "";
		//添加新成员
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
	
}
