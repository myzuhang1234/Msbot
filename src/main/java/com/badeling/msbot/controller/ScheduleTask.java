package com.badeling.msbot.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
						Thread.sleep(3542);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				
			}
			rereadSentenceRepository.deleteAll();
			rereadTimeRepository.deleteAll();
		}
			
		
		
}

