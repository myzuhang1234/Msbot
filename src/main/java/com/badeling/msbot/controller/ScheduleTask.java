package com.badeling.msbot.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.domain.GlobalVariable;
import com.badeling.msbot.domain.GroupMsg;
import com.badeling.msbot.domain.Result;
import com.badeling.msbot.entity.GroupInfo;
import com.badeling.msbot.entity.GroupMember;
import com.badeling.msbot.entity.Message;
import com.badeling.msbot.entity.RereadSentence;
import com.badeling.msbot.entity.RereadTime;
import com.badeling.msbot.entity.Score;
import com.badeling.msbot.entity.SellAndBuy;
import com.badeling.msbot.entity.BanTime;
import com.badeling.msbot.entity.MonvTime;

import com.badeling.msbot.repository.GroupInfoRepository;
import com.badeling.msbot.repository.GroupMemberRepository;
import com.badeling.msbot.repository.MessageRepository;
import com.badeling.msbot.repository.RereadSentenceRepository;
import com.badeling.msbot.repository.RereadTimeRepository;
import com.badeling.msbot.repository.ScoreRepository;
import com.badeling.msbot.repository.SellAndBuyRepository;
import com.badeling.msbot.service.GroupMsgService;
import com.badeling.msbot.util.CosSdk;
import com.badeling.msbot.repository.BanTimeRepository;
import com.badeling.msbot.repository.MonvTimeRepository;


@EnableScheduling
@Component
public class ScheduleTask {
	
	@Autowired
	GroupMsgService groupMsgService;
	
	@Autowired
	private RereadSentenceRepository rereadSentenceRepository;
	
	@Autowired
	private RereadTimeRepository rereadTimeRepository;

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

	@Autowired
	private BanTimeRepository banTimeRepository;
	@Autowired
	private MonvTimeRepository monvTimeRepository;

	@Scheduled(cron="23 24 5 * * ?")
	private void deleteExpiredSellAndBuy() {
		//测试程序运行时间
		Long time1,time2;
		//删除过期的收售数据
		time1 = System.currentTimeMillis();
		Long deleteTime = System.currentTimeMillis() - 1000*60*60*48;
		List<SellAndBuy> sabList = sellAndBuyRepository.findSabByTime(deleteTime+"");
		if(sabList.size()>0) {
			for(SellAndBuy sab :sabList) {
				sellAndBuyRepository.delete(sab);
			}
		}
		//统计昨天聊天记录 添加积分
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String time = sdf.format(time1-1000*60*60*24);
		List<Message> list = messageRepository.findAllMsg();
		Map<String,Integer> map = new HashMap<String,Integer>();

		for(Message ms : list) {
			Integer score = null;
			if(ms.getRaw_message().startsWith(MsbotConst.botName)||ms.getRaw_message().startsWith("[CQ:at,qq="+MsbotConst.botId+"]")) {
				score = (int)(Math.log(ms.getRaw_message().length()+1)*75*(-1));
			}else {
				score = (int)(Math.log(ms.getRaw_message().length()+1)*15);
			}

			if(map.containsKey(ms.getUser_id()+"-"+ms.getGroup_id())) {
				score = score + map.get(ms.getUser_id()+"-"+ms.getGroup_id());
			}
			map.put(ms.getUser_id()+"-"+ms.getGroup_id(), score);
		}
		Set<String> keySet = map.keySet();
		List<Score> addList = new ArrayList<Score>();
		for(String temp : keySet) {
			String[] split = temp.split("-");
			Score s = new Score();
			s.setUser_id(split[0]);
			s.setGroup_id(split[1]);
			s.setScore(map.get(temp));
			s.setTime(Integer.parseInt(time));
			addList.add(s);
		}
		scoreRepository.saveAll(addList);
		messageRepository.deleteAll(list);
		//归总前两周的积分
		time = sdf.format(time1-1000*60*60*24*7);
		List<Score> scoreList = scoreRepository.findLastWeekScore2(time,"99999999");
		List<Score> modifyScore = new ArrayList<Score>();
		if(scoreList!=null&&!scoreList.isEmpty()) {
			List<Object[]> findLastWeekScore = scoreRepository.findLastWeekScore(time,"99999999");
			for(Object[] obj : findLastWeekScore) {
				Score s = new Score();
				s.setGroup_id(String.valueOf(obj[0]));
				s.setUser_id(String.valueOf(obj[1]));
				s.setScore(Integer.parseInt(String.valueOf(obj[2])));
				s.setTime(99999999);
				modifyScore.add(s);
			}
			scoreRepository.deleteAll(scoreList);
			scoreRepository.saveAll(modifyScore);
		}


		sdf = new SimpleDateFormat("EEEE");
		//星期一清除图片缓存
		//计数器
		Long size = 0l;
		int count = 0;
		if(sdf.format(time1).equals("星期一")) {
			try {
				//清除云端缓存
				CosSdk.deleteAllFile();
			} catch (Exception e) {

			}
			String url = MsbotConst.imageUrl;
			File file = new File(url);
			String[] fileList = file.list();

			for(String a : fileList) {
				File file2 = new File(url,a);
				if(file2.isFile()&&a.length()==32&&!file2.getName().contains(MsbotConst.channelBotId)) {
					size = size + file2.length();
					file2.delete();
					count++;
				}
			}
		}
		time2=System.currentTimeMillis();
		String reply = "";
		if(count!=0) {
			reply = "程序运行总时间： "+(time2-time1)+"ms";
			reply = reply + "\r\n清理图片" + count + "张，共计" + (size/1024/1024) + "MB";
		}

		try {
			List<String> group_list = new ArrayList<String>();
			Result<?> groupList = groupMsgService.getGroupList();
			if(groupList.getStatus().equals("ok")) {
				@SuppressWarnings("unchecked")
				List<Map<String,Object>> data = (List<Map<String, Object>>) groupList.getData();
				for(Map<String,Object> mapElement : data) {
					GroupInfo groupInfo = new GroupInfo();
					group_list.add(String.valueOf(mapElement.get("group_id")));
					String group_id = ""+mapElement.get("group_id");
					String group_name = ""+mapElement.get("group_name");
					String group_memo = ""+mapElement.get("group_memo");
					GroupInfo findByGroupId = groupInfoRepository.findByGroupId(group_id);
					if(findByGroupId==null) {
						groupInfo.setGroup_id(group_id);
						groupInfo.setGroup_name(group_name);
						groupInfo.setGroup_memo(group_memo);
						groupInfoRepository.save(groupInfo);
					}
				}
			}
			//更新群成员信息
			sdf = new SimpleDateFormat("yyyyMMdd");
			int countAdd = 0;
			int countModify = 0;
			int countLeave = 0;
			int countSame = 0;
			Long startTime = System.currentTimeMillis();
			for(String group_id : group_list) {
				GroupMsg groupMsg = new GroupMsg();
				groupMsg.setGroup_id(Long.parseLong(group_id));
				Result<?> groupMemberList = groupMsgService.getGroupMember(groupMsg);
				List<GroupMember> findGroupMemberList = groupMemberRepository.findGroupMemberByGroup(group_id);
				Map<String,GroupMember> groupMemberMap = new HashMap<String, GroupMember>();
				for(GroupMember groupMember : findGroupMemberList) {
					groupMemberMap.put(groupMember.getUser_id(), groupMember);
				}

				@SuppressWarnings("unchecked")
				List<Map<String,Object>> data = (List<Map<String, Object>>) groupMemberList.getData();
				for(Map<String,Object> map2 : data) {
					String user_id = String.valueOf(map2.get("user_id"));
					if(groupMemberMap.containsKey(user_id)) {
						GroupMember groupMember = groupMemberMap.get(user_id);
						groupMemberMap.remove(user_id);
						String leave_time = groupMember.getLeave_time();
						if(leave_time!=null&&!leave_time.isEmpty()) {
							groupMemberRepository.modifyMemberLeaveTime(groupMember.getId(),"");
						}
						if(groupMember.getCard().equals(String.valueOf(map2.get("card")))&&groupMember.getNickname().equals(String.valueOf(map2.get("nickname")))) {
							countSame++;
							continue;
						}else {
							countModify++;
							groupMemberRepository.modifyMemberInfo(groupMember.getId(), String.valueOf(map2.get("nickname")), String.valueOf(map2.get("card")), sdf.format(Long.parseLong(map2.get("join_time")+"000")));
						}
					}else {
						String join_time = sdf.format(Long.parseLong(map2.get("join_time")+"000"));
						GroupMember groupMember = new GroupMember();
						groupMember.setCard(String.valueOf(map2.get("card")));
						groupMember.setGroup_id(group_id);
						groupMember.setJoin_time(join_time);
						groupMember.setLeave_time(null);
						groupMember.setNickname(String.valueOf(map2.get("nickname")));
						groupMember.setUser_id(user_id);
						groupMemberRepository.save(groupMember);
						countAdd++;
					}
				}

				if(!groupMemberMap.isEmpty()) {
					time = sdf.format(System.currentTimeMillis());
					for(String temp : groupMemberMap.keySet()) {
						GroupMember groupMember = groupMemberMap.get(temp);
						if(groupMember.getLeave_time()==null||groupMember.getLeave_time().isEmpty()) {
							groupMemberRepository.modifyMemberLeaveTime(groupMember.getId(), time);
							countLeave++;
						}
						try {
							if(Integer.parseInt(groupMember.getJoin_time())>Integer.parseInt(groupMember.getLeave_time())) {
								groupMemberRepository.modifyMemberLeaveTime(groupMember.getId(), time);
								countLeave++;
								continue;
							}
						} catch (Exception e) {
							System.out.println("统计减少人数时出错:"+groupMember.toString());
						}
					}
				}


			}
			System.out.println("更新群成员信息成功:新增"+countAdd+"人，更新"+countModify+"人，离开"+countLeave+"人，保持"+countSame+"人，耗时"+((startTime-System.currentTimeMillis())/1000)+"ms");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("更新群成员信息失败");
		}

		if(MsbotConst.notice_group!=null&&!MsbotConst.notice_group.isEmpty()&&!reply.isEmpty()) {
			GroupMsg groupMsg = new GroupMsg();
			groupMsg.setGroup_id(Long.parseLong(MsbotConst.notice_group));
			groupMsg.setMessage(reply);
			groupMsgService.sendGroupMsg(groupMsg);
		}

		System.out.println(reply);

	}

	//新成员提醒修改群名片
	@Scheduled(cron="24 * * * * ?")
	private void remindModifyName() {
		Map<String, Long> newFriendsMap = GlobalVariable.getNewFriendsMap();
		if(newFriendsMap.size()!=0) {
			for(String key : newFriendsMap.keySet()) {
				if(System.currentTimeMillis()>newFriendsMap.get(key)) {
					try {
						String[] split = key.split("-");
						String user_id = split[0];
						String group_id = split[1];
						GroupInfo findByGroupId = groupInfoRepository.findByGroupId(group_id);
						String welcome = findByGroupId.getWelcome();

						//懒得找单个查询的接口了 先直接拉取群列表的消息 后期如果空闲了再改
						GroupMsg groupMsg = new GroupMsg();
						groupMsg.setGroup_id(Long.parseLong(group_id));
						Result<?> groupMember = groupMsgService.getGroupMember(groupMsg);
						@SuppressWarnings("unchecked")
						List<Map<String,Object>> data = (List<Map<String, Object>>) groupMember.getData();
						boolean alreadyModify = false;
						for(Map<String,Object> map : data) {
							if(String.valueOf(map.get("user_id")).equals(user_id)) {
								if(!String.valueOf(map.get("nickname")).isEmpty()&&!String.valueOf(map.get("nickname")).equals(String.valueOf(map.get("card")))) {
									alreadyModify = true;
								}
								break;
							}
						}

						if(welcome!=null&&!welcome.isEmpty()&&!alreadyModify) {
							groupMsg.setMessage("[CQ:at,qq="+user_id+"]"+welcome);
							groupMsgService.sendGroupMsg(groupMsg);
						}
						Thread.sleep(654);
					} catch (Exception e) {
						e.printStackTrace();
					}
					newFriendsMap.remove(key);
				}
			}
			GlobalVariable.setNewFriendsMap(newFriendsMap);
		}
	}

		
		//复读机周报
		@Scheduled(cron="0 0 0 ? * MON")
		private void rereadReport() {
			List<String> groupList = rereadSentenceRepository.findMaxEveryGroup();
			if(groupList!=null) {
				try {
					for(String group_id:groupList) {
						RereadSentence rereadSentence = rereadSentenceRepository.findMaxByGroup(group_id);

						//得到群成员信息
						GroupMsg gp = new GroupMsg();
						gp.setGroup_id(Long.parseLong(group_id));
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
						if(rereadSentence!=null) {
							message += rereadSentence.getMessage() + "\r\n" + 
							"此金句出自———————" + map.get(rereadSentence.getUser_id()) + "\r\n" + 
							"当时被复读机们连续复读了" + rereadSentence.getReadTime() + "次！\r\n";
							List<RereadTime> list = rereadTimeRepository.find3thByGroup(group_id);
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
						GroupMsg groupMsg = new GroupMsg();
						groupMsg.setAuto_escape(false);
						groupMsg.setMessage(message);
						groupMsg.setGroup_id(Long.parseLong((group_id)));
						groupMsgService.sendGroupMsg(groupMsg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {

			}

			rereadSentenceRepository.deleteAll();
			rereadTimeRepository.deleteAll();
		}

		@Scheduled(cron="1 0 0 ? * MON")
		private void banReport(){
			List<String> groupList = banTimeRepository.findEveryGroup();
			if(groupList!=null) {
				try {
					for(String group_id:groupList) {
						//得到群成员信息
						GroupMsg gp = new GroupMsg();
						gp.setGroup_id(Long.parseLong(group_id));
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

						List<BanTime> list = banTimeRepository.findBanTimesWeeklyByGroup(group_id);
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
						GroupMsg groupMsg = new GroupMsg();
						groupMsg.setAuto_escape(false);
						groupMsg.setMessage(message);
						groupMsg.setGroup_id(Long.parseLong((group_id)));
						groupMsgService.sendGroupMsg(groupMsg);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}else {}

			banTimeRepository.deleteAll();
		}
		@Scheduled(cron="0 0 20,21,22 ? * SUN")
		private void paoqiReport(){
			List<String> groupList = monvTimeRepository.findEveryGroup();
			if(groupList!=null) {
				try {
					for(String group_id:groupList) {
						String message = "[CQ:at,qq=all] 周日啦，跑旗水路抓紧啦";
						GroupMsg groupMsg = new GroupMsg();
						groupMsg.setAuto_escape(false);
						groupMsg.setMessage(message);
						groupMsg.setGroup_id(Long.parseLong((group_id)));
						groupMsgService.sendGroupMsg(groupMsg);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}else {}
		}

		@Scheduled(cron="0 0 12 * * ?")
		private void monvReport(){
			List<String> groupList = monvTimeRepository.findEveryGroup();
			if(groupList!=null) {
				try {
					for(String group_id:groupList) {
						//得到群成员信息
						GroupMsg gp = new GroupMsg();
						gp.setGroup_id(Long.parseLong(group_id));
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

						String message = "本日氪佬是：\r\n";
						List<MonvTime> list = monvTimeRepository.find3thCostByGroup(group_id);
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
						List<MonvTime> list2 = monvTimeRepository.find3thLuckByGroup(group_id);
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
						GroupMsg groupMsg = new GroupMsg();
						groupMsg.setAuto_escape(false);
						groupMsg.setMessage(message);
						groupMsg.setGroup_id(Long.parseLong((group_id)));
						groupMsgService.sendGroupMsg(groupMsg);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}else {}
		}

	    @Scheduled(cron="0 0 0 * * ?")
		private void deleteRecord(){
			String url = MsbotConst.voiceUrl;
			File file = new File(url);
			String[] fileList = file.list();

			for(String a : fileList) {
				File file2 = new File(url,a);
				if(file2.isFile()&&!file2.getName().contains(MsbotConst.channelBotId)) {
					file2.delete();
				}
			}
		}


}

