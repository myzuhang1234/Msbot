package com.badeling.msbot.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badeling.msbot.entity.MonvTime;
import com.badeling.msbot.repository.MonvTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.domain.GroupMsg;
import com.badeling.msbot.domain.Result;
import com.badeling.msbot.entity.RereadSentence;
import com.badeling.msbot.entity.RereadTime;
import com.badeling.msbot.repository.RereadSentenceRepository;
import com.badeling.msbot.repository.RereadTimeRepository;
import com.badeling.msbot.service.GroupMsgService;
import com.badeling.msbot.util.CosSdk;

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
	private MonvTimeRepository monvTimeRepository;
	
	
		//清除图片缓存
		@Scheduled(cron="0 30 4 ? * MON")
		private void delete() {
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
				if(file2.isFile()&&!file2.getName().contains(MsbotConst.channelBotId)) {
					file2.delete();
				}
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

						String message = "\r\n本日氪佬是：\r\n";
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


}

