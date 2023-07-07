package com.badeling.msbot.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.badeling.msbot.domain.GlobalVariable;
import com.badeling.msbot.domain.GroupMsg;
import com.badeling.msbot.domain.Result;
import com.badeling.msbot.entity.GroupInfo;
import com.badeling.msbot.entity.GroupMember;
import com.badeling.msbot.repository.GroupInfoRepository;
import com.badeling.msbot.repository.GroupMemberRepository;
import com.badeling.msbot.service.GroupMsgService;

@Component
public class ApplicationRunnerImpl implements ApplicationRunner{
//public class ApplicationRunnerImpl{

	@Autowired
	GroupMsgService groupMsgService;

	@Autowired
	GroupInfoRepository groupInfoRepository;

	@Autowired
	GroupMemberRepository groupMemberRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		TreeSet<String> msgList = new TreeSet<>();
		GlobalVariable.setMsgList(msgList);
		Map<String,Long> newFriendsList = new HashMap<>();
		GlobalVariable.setNewFriendsMap(newFriendsList);
		Map<String,Long> witchForestMap = new HashMap<>();
		GlobalVariable.setWitchForestMap(witchForestMap);


		System.out.println("初始程序执行完毕");

		List<String> list = new ArrayList<String>();
		//获取群列表
		try {
			Result<?> groupList = groupMsgService.getGroupList();

			if(groupList.getStatus().equals("ok")) {
				@SuppressWarnings("unchecked")
				List<Map<String,Object>> data = (List<Map<String, Object>>) groupList.getData();
				int countGroup = 0;
				int addGroup = 0;
				for(Map<String,Object> mapElement : data) {
					GroupInfo groupInfo = new GroupInfo();
					list.add(""+mapElement.get("group_id"));
					String group_id = ""+mapElement.get("group_id");
					String group_name = ""+mapElement.get("group_name");
					String group_memo = ""+mapElement.get("group_memo");
					GroupInfo findByGroupId = groupInfoRepository.findByGroupId(group_id);
					if(findByGroupId==null) {
						groupInfo.setGroup_id(group_id);
						groupInfo.setGroup_name(group_name);
						groupInfo.setGroup_memo(group_memo);
						groupInfoRepository.save(groupInfo);
						addGroup++;
					}
					countGroup++;
				}
				System.out.println("共获取"+countGroup+"个群，新增群聊"+addGroup+"个");
			}
		} catch (Exception e) {
			// 获取群列表失败
			e.printStackTrace();
			System.out.println("获取群列表失败");
		}


		try {
			//更新群成员信息
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			int countAdd = 0;
			int countModify = 0;
			int countLeave = 0;
			int countSame = 0;
			Long startTime = System.currentTimeMillis();
			List<GroupMember> addList = new ArrayList<GroupMember>();
			for(String group_id : list) {
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
				for(Map<String,Object> map : data) {
					String user_id = String.valueOf(map.get("user_id"));
					if(groupMemberMap.containsKey(user_id)) {
						GroupMember groupMember = groupMemberMap.get(user_id);
						groupMemberMap.remove(user_id);
						String leave_time = groupMember.getLeave_time();
						if(leave_time!=null&&!leave_time.isEmpty()) {
							groupMemberRepository.modifyMemberLeaveTime(groupMember.getId(),"");
						}
						if(groupMember.getCard().equals(String.valueOf(map.get("card")))&&groupMember.getNickname().equals(String.valueOf(map.get("nickname")))) {
							countSame++;
							continue;
						}else {
							countModify++;
							groupMemberRepository.modifyMemberInfo(groupMember.getId(), String.valueOf(map.get("nickname")), String.valueOf(map.get("card")), sdf.format(Long.parseLong(map.get("join_time")+"000")));
						}
					}else {
						String time = sdf.format(Long.parseLong(map.get("join_time")+"000"));
						GroupMember groupMember = new GroupMember();
						groupMember.setCard(String.valueOf(map.get("card")));
						groupMember.setGroup_id(group_id);
						groupMember.setJoin_time(time);
						groupMember.setLeave_time(null);
						groupMember.setNickname(String.valueOf(map.get("nickname")));
						groupMember.setUser_id(user_id);
						addList.add(groupMember);
						countAdd++;
					}
				}

				if(!groupMemberMap.isEmpty()) {
					String time = sdf.format(System.currentTimeMillis());
					for(String temp : groupMemberMap.keySet()) {
						GroupMember groupMember = groupMemberMap.get(temp);
						if(groupMember.getLeave_time()==null||groupMember.getLeave_time().isEmpty()) {
							groupMemberRepository.modifyMemberLeaveTime(groupMember.getId(), time);
							countLeave++;
							continue;
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

				if(addList.size()>0) {
					groupMemberRepository.saveAll(addList);
				}
			}
			System.out.println("更新群成员信息成功:新增"+countAdd+"人，更新"+countModify+"人，离开"+countLeave+"人，保持"+countSame+"人，耗时"+((startTime-System.currentTimeMillis())/1000)+"ms");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("更新群成员信息失败");
		}



	}

}