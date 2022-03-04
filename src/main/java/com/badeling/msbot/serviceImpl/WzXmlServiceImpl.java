package com.badeling.msbot.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.domain.ChannelReplyMsg;
import com.badeling.msbot.domain.GroupMsg;
import com.badeling.msbot.domain.PrivateMsg;
import com.badeling.msbot.entity.MobInfo;
import com.badeling.msbot.entity.MobName;
import com.badeling.msbot.repository.MobInfoRepository;
import com.badeling.msbot.repository.MobNameRepository;
import com.badeling.msbot.service.GroupMsgService;
import com.badeling.msbot.service.WzXmlService;

@Component
public class WzXmlServiceImpl implements WzXmlService{
	
	@Autowired
	private MobNameRepository mobNameRepository;
	
	@Autowired
	private MobInfoRepository mobInfoRepository;
	
	@Autowired
	private GroupMsgService groupMsgService;
	
	
	public static void main(String[] args) throws Exception {
		clearImage();
	}
	
	//导出怪物所有图片文件后 运行 让每只怪物的图片只留下一张
	private static void clearImage() {
		File file1=new File("C:\\Users\\Admin\\Desktop\\result_img");//根据指定的路径创建file对象
		File[] listFiles = file1.listFiles();
		for(File file : listFiles) {
			boolean isStand = false;
			String standName = "";
			boolean isMove = false;
			String moveName = "";
			File[] listFiles2 = file.listFiles();
			File[] listFiles3 = listFiles2[0].listFiles();
			
			if(listFiles3.length>1) {
				for(File temp : listFiles3) {
					if(temp.getName().contains("stand")) {
						standName = temp.getName();
						isStand = true;
						break;
					}
				}
				
				if(isStand) {
					for(File temp : listFiles3) {
						if(!temp.getName().equals(standName)) {
							temp.delete();
						}
					}
				}else {
					for(File temp : listFiles3) {
						if(temp.getName().contains("move")) {
							moveName = temp.getName();
							isMove = true;
						}
					}
					if(isMove) {
						for(File temp : listFiles3) {
							if(!temp.getName().equals(moveName)) {
								temp.delete();
							}
						}
					}else {
						for(int i=0;i<listFiles3.length-2;i++) {
							File file2 = listFiles3[i];
							file2.delete();
						}
					}
				}
			}
		
		}
	}
	
	//读取json文件
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


	@SuppressWarnings("unchecked")
	@Override
	public void updateMobInfo() {
		int a = 1;
		a++;
		if(a==2) {
			String readJsonFile = readJsonFile("/www/wwwroot/badeling/Mob.img.json");
			LinkedHashMap<String,Object> jobj = JSON.parseObject(readJsonFile,LinkedHashMap.class,Feature.OrderedField);
			for(Map.Entry<String, Object> entry : jobj.entrySet()) {
//	            System.out.println("key:" + entry.getKey() + "   value:" + entry.getValue());
				Map<String,Object> object = (Map<String, Object>) jobj.get(entry.getKey());
				Map<String,Object> object2 = (Map<String, Object>) object.get("name");
				//前者id 后者名字
				MobName mobName = new MobName();
				mobName.setMobId(object.get("_dirName")+"");
				mobName.setName(object2.get("_value")+"");
				mobNameRepository.save(mobName);
				System.out.println(mobName.toString());
	        }
		}
		
		File file1=new File("/www/wwwroot/badeling/result");
		File[] listFiles = file1.listFiles();
		for(File file : listFiles) {
			String readJsonFile = readJsonFile(file.getPath());
			Map<String,Object> jobj = JSON.parseObject(readJsonFile);
			Object object = jobj.get("info");
			if(object!=null) {
				Map<String,Object> map = (Map<String, Object>) jobj.get("info");
				MobInfo mobInfo = new MobInfo();
				mobInfo.setMobId(file.getName().substring(0,file.getName().indexOf(".")));
				if(map.get("level")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("level");
					mobInfo.setLevel(map2.get("_value")+"");
				}
				if(map.get("maxHP")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("maxHP");
					mobInfo.setMaxHp(map2.get("_value")+"");
				}
				if(map.get("maxMP")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("maxMP");
					mobInfo.setMaxMp(map2.get("_value")+"");
				}
				if(map.get("speed")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("speed");
					mobInfo.setSpeed(map2.get("_value")+"");
				}
				if(map.get("PDDamage")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("PDDamage");
					mobInfo.setPaDamage(map2.get("_value")+"");
				}
				if(map.get("MADamage")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("MADamage");
					mobInfo.setMaDamage(map2.get("_value")+"");
				}
				if(map.get("PDRate")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("PDRate");
					mobInfo.setPdRate(map2.get("_value")+"");
				}
				if(map.get("MDRate")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("MDRate");
					mobInfo.setMdRate(map2.get("_value")+"");
				}
				if(map.get("acc")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("acc");
					mobInfo.setAcc(map2.get("_value")+"");
				}
				if(map.get("eva")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("eva");
					mobInfo.setEva(map2.get("_value")+"");
				}
				if(map.get("pushed")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("pushed");
					mobInfo.setPushed(map2.get("_value")+"");
				}
				if(map.get("fs")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("fs");
					mobInfo.setFs(map2.get("_value")+"");
				}
				if(map.get("exp")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("exp");
					mobInfo.setExp(map2.get("_value")+"");
				}
				if(map.get("summonType")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("summonType");
					mobInfo.setSummerType(map2.get("_value")+"");
				}
				if(map.get("category")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("category");
					mobInfo.setCategory(map2.get("_value")+"");
				}
				if(map.get("elemAttr")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("elemAttr");
					mobInfo.setElemAttr(map2.get("_value")+"");
				}
				if(map.get("mobType")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("mobType");
					mobInfo.setMobType(map2.get("_value")+"");
				}
				if(map.get("link")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("link");
					mobInfo.setLink(map2.get("_value")+"");
				}
				if(map.get("boss")!=null) {
					Map<String,Object> map2 = (Map<String, Object>)map.get("boss");
					mobInfo.setBoss(map2.get("_value")+"");
				}
				System.out.println(mobInfo.toString());
				mobInfoRepository.save(mobInfo);
			}
			
		}
	}
	
	@Override
	public String searchMob(String raw_message, String group_id, String user_id) {
		raw_message = raw_message.replaceAll("&#91;", "[").replaceAll("&#93;", "]");
		System.out.println(raw_message);
		List<MobName> mobNameList = mobNameRepository.findByName(raw_message);
//		List<MobName> mobNameExactList = mobNameRepository.findAllMob();
		StringBuffer reply = new StringBuffer();
		System.out.println(mobNameList.size());
		int i=0;
		if(mobNameList.size()!=0) {
			while(i<mobNameList.size()) {
				MobName mobName = mobNameList.get(i);
				MobInfo mobInfo = mobInfoRepository.findMobInfoByMobId(mobNameList.get(i).getMobId());
				try {
					if(mobInfo.getLink()!=null) {
						String url = MsbotConst.imageUrl+"result_img/"+mobInfo.getLink()+".img/"+mobInfo.getLink()+".img";
						File file = new File(url);
						if(file.exists()) {
							File[] listFiles = file.listFiles();
							File file2 = listFiles[0];
							reply.append("[CQ:image,file=result_img/")
							.append(mobInfo.getLink())
							.append(".img/")
							.append(mobInfo.getLink())
							.append(".img/")
							.append(file2.getName())
							.append("]");
							break;
						}
					}
				} catch (Exception e) {
					System.out.println("link 空指针");
				}
				String url = MsbotConst.imageUrl+"result_img/"+mobName.getMobId()+".img/"+mobName.getMobId()+".img";
				System.out.println(url);
				File file = new File(url);
				if(file.exists()) {
					File[] listFiles = file.listFiles();
					File file2 = listFiles[0];
					reply.append("[CQ:image,file=result_img/")
					.append(mobName.getMobId())
					.append(".img/")
					.append(mobName.getMobId())
					.append(".img/")
					.append(file2.getName())
					.append("]");
					break;
				}
				i=i+1;
			}
			if(i==mobNameList.size()) {
				i=0;
			}
			MobInfo mobInfo = mobInfoRepository.findMobInfoByMobId(mobNameList.get(i).getMobId());
			if(mobInfo!=null) {
				reply.append("\r\n").append(mobNameList.get(i).getName()).append("(").append(mobNameList.get(i).getMobId()).append(")");
				if(mobInfo.getBoss()==null) {
					reply.append("\r\n怪物类型:普通怪");
				}else {
					reply.append("\r\n怪物类型:首领怪");
				}
				reply.append("\r\n")
				.append("等级:").append(mobInfo.getLevel()).append("\r\n")
				.append("血量:").append(mobInfo.getMaxHp()).append("\r\n")
//				.append("蓝量:").append(mobInfo.getMaxMp()).append("\r\n")
				.append("防御:").append(mobInfo.getPdRate()).append("%\r\n")
//				.append("魔法防御:").append(mobInfo.getMdRate()).append("%\r\n")
//				.append("移动速度:").append(mobInfo.getSpeed()).append("\r\n")
//				.append("击退伤害:").append(mobInfo.getPushed()).append("\r\n")
				.append("经验:").append(mobInfo.getExp()).append("\r\n")
				.append("冰雷火毒圣暗物").append("\r\n");
				if(mobInfo.getElemAttr()==null) {
					reply.append("○○○○○○○");
				}else {
					String attr = mobInfo.getElemAttr();
					Map<String,String> attrMap = new HashMap<>();
					attrMap.put("I", "○");attrMap.put("L", "○");attrMap.put("F", "○");attrMap.put("S", "○");
					attrMap.put("H", "○");attrMap.put("D", "○");attrMap.put("P", "○");
					int index=0;
					while(index<attr.length()) {
						if(attr.substring(index+1,index+2).equals("1")) {
							attrMap.put(attr.substring(index,index+1), "×");
						}else if(attr.substring(index+1,index+2).equals("2")) {
							attrMap.put(attr.substring(index,index+1), "△");
						}else if(attr.substring(index+1,index+2).equals("3")) {
							attrMap.put(attr.substring(index,index+1), "◎");
						}
						index = index+2;
						System.out.println(index);
					}
					reply.append(attrMap.get("I"))
					.append(attrMap.get("L"))
					.append(attrMap.get("F"))
					.append(attrMap.get("S"))
					.append(attrMap.get("H"))
					.append(attrMap.get("D"))
					.append(attrMap.get("P"));
				}
			}
		}else {
			reply.append("没有查询到怪物信息");
		}
		GroupMsg groupMsg = new GroupMsg();
		groupMsg.setGroup_id(Long.parseLong(group_id));
		groupMsg.setMessage(reply.toString());
		groupMsgService.sendGroupMsg(groupMsg);
		
		if(mobNameList.size()>=2) {
			String reply2 = "查询到同名怪物有：";
			for(MobName mn : mobNameList) {
				reply2 = reply2 + "\r\n" + mn.getName()+"("+mn.getMobId()+")";
			}
			GroupMsg groupMsg2 = new GroupMsg();
			groupMsg2.setGroup_id(Long.parseLong(group_id));
			groupMsg2.setMessage(reply2);
			groupMsgService.sendGroupMsg(groupMsg2);
		}
		
		List<MobName> mobNameLikeList = mobNameRepository.findByNameLike(raw_message);
		mobNameLikeList.removeAll(mobNameList);
		System.out.println("相似的结果量"+mobNameLikeList.size());
		String reply3 = "查询到相似的怪物有：";
		if(mobNameLikeList.size()==0) {
			reply3 = "查询到相似的怪物有：null";
			return null;
		}else if(mobNameLikeList.size()<10) {
			for(MobName mn : mobNameLikeList) {
				reply3 = reply3 + "\r\n" + mn.getName()+"("+mn.getMobId()+")";
			}
		}else{
			reply3 = "查询到相似的怪物有：查询结果超过10条，为防止刷屏，已将结果私聊于您。";
			int k = 0;
			String pReply = "";
			while(mobNameLikeList.size()>0) {
				MobName remove = mobNameLikeList.remove(0);
				pReply = pReply + remove.getName()+"("+remove.getMobId()+")"+"\r\n";
				k++;
				if(k==10) {
					PrivateMsg privateMsg = new PrivateMsg();
					privateMsg.setUser_id(Long.parseLong(user_id));
					privateMsg.setMessage(pReply);
					groupMsgService.sendPrivateMsg(privateMsg);
					pReply = "";
					k=0;
				}
			}
			if(!pReply.equals("")) {
				PrivateMsg privateMsg = new PrivateMsg();
				privateMsg.setUser_id(Long.parseLong(user_id));
				privateMsg.setMessage(pReply);
				groupMsgService.sendPrivateMsg(privateMsg);
			}
		}
		GroupMsg groupMsg3 = new GroupMsg();
		groupMsg3.setGroup_id(Long.parseLong(group_id));
		groupMsg3.setMessage(reply3);
		groupMsgService.sendGroupMsg(groupMsg3);
		return null;
	}

	@Override
	public String searchMob(Long mob_id,String group_id) {
		MobName mobName = mobNameRepository.findByMobId(mob_id);
		if(mobName==null) {
			return null;
		}
		MobInfo mobInfo = mobInfoRepository.findMobInfoByMobId(mob_id+"");
		StringBuffer reply = new StringBuffer();
		try {
			if(mobInfo.getLink()!=null) {
				String url = MsbotConst.imageUrl+"result_img/"+mobInfo.getLink()+".img/"+mobInfo.getLink()+".img";
				File file = new File(url);
				if(file.exists()) {
					File[] listFiles = file.listFiles();
					File file2 = listFiles[0];
					reply.append("[CQ:image,file=result_img/")
					.append(mobInfo.getLink())
					.append(".img/")
					.append(mobInfo.getLink())
					.append(".img/")
					.append(file2.getName())
					.append("]");
				}
			}
		} catch (Exception e) {
			
		}
		String url = MsbotConst.imageUrl+"result_img/"+mobName.getMobId()+".img/"+mobName.getMobId()+".img";
		System.out.println(url);
		File file = new File(url);
		if(file.exists()) {
			File[] listFiles = file.listFiles();
			File file2 = listFiles[0];
			reply.append("[CQ:image,file=result_img/")
			.append(mobName.getMobId())
			.append(".img/")
			.append(mobName.getMobId())
			.append(".img/")
			.append(file2.getName())
			.append("]");
		}
		
		if(mobInfo!=null) {
			reply.append("\r\n").append(mobName.getName()).append("(").append(mobName.getMobId()).append(")");
			if(mobInfo.getBoss()==null) {
				reply.append("\r\n怪物类型:普通怪");
			}else {
				reply.append("\r\n怪物类型:首领怪");
			}
			reply.append("\r\n")
			.append("等级:").append(mobInfo.getLevel()).append("\r\n")
			.append("血量:").append(mobInfo.getMaxHp()).append("\r\n")
//			.append("蓝量:").append(mobInfo.getMaxMp()).append("\r\n")
			.append("防御:").append(mobInfo.getPdRate()).append("%\r\n")
//			.append("魔法防御:").append(mobInfo.getMdRate()).append("%\r\n")
//			.append("移动速度:").append(mobInfo.getSpeed()).append("\r\n")
//			.append("击退伤害:").append(mobInfo.getPushed()).append("\r\n")
			.append("经验:").append(mobInfo.getExp()).append("\r\n")
			.append("冰雷火毒圣暗物").append("\r\n");
			if(mobInfo.getElemAttr()==null) {
				reply.append("○○○○○○○");
			}else {
				String attr = mobInfo.getElemAttr();
				Map<String,String> attrMap = new HashMap<>();
				attrMap.put("I", "○");attrMap.put("L", "○");attrMap.put("F", "○");attrMap.put("S", "○");
				attrMap.put("H", "○");attrMap.put("D", "○");attrMap.put("P", "○");
				int index=0;
				while(index<attr.length()) {
					if(attr.substring(index+1,index+2).equals("1")) {
						attrMap.put(attr.substring(index,index+1), "×");
					}else if(attr.substring(index+1,index+2).equals("2")) {
						attrMap.put(attr.substring(index,index+1), "△");
					}else if(attr.substring(index+1,index+2).equals("3")) {
						attrMap.put(attr.substring(index,index+1), "◎");
					}
					index = index+2;
					System.out.println(index);
				}
				reply.append(attrMap.get("I"))
				.append(attrMap.get("L"))
				.append(attrMap.get("F"))
				.append(attrMap.get("S"))
				.append(attrMap.get("H"))
				.append(attrMap.get("D"))
				.append(attrMap.get("P"));
			}
		}
		GroupMsg groupMsg = new GroupMsg();
		groupMsg.setGroup_id(Long.parseLong(group_id));
		groupMsg.setMessage(reply.toString());
		groupMsgService.sendGroupMsg(groupMsg);
		return null;
	}

	@Override
	public String searchMobForChannel(Long mob_id, Long channel_id, Long guild_id) {
		MobName mobName = mobNameRepository.findByMobId(mob_id);
		if(mobName==null) {
			return null;
		}
		MobInfo mobInfo = mobInfoRepository.findMobInfoByMobId(mob_id+"");
		StringBuffer reply = new StringBuffer();
		try {
			if(mobInfo.getLink()!=null) {
				String url = MsbotConst.imageUrl+"result_img/"+mobInfo.getLink()+".img/"+mobInfo.getLink()+".img";
				File file = new File(url);
				if(file.exists()) {
					File[] listFiles = file.listFiles();
					File file2 = listFiles[0];
					reply.append("[CQ:image,file=result_img/")
					.append(mobInfo.getLink())
					.append(".img/")
					.append(mobInfo.getLink())
					.append(".img/")
					.append(file2.getName())
					.append("]");
				}
			}
		} catch (Exception e) {
			
		}
		String url = MsbotConst.imageUrl+"result_img/"+mobName.getMobId()+".img/"+mobName.getMobId()+".img";
		System.out.println(url);
		File file = new File(url);
		if(file.exists()) {
			File[] listFiles = file.listFiles();
			File file2 = listFiles[0];
			reply.append("[CQ:image,file=result_img/")
			.append(mobName.getMobId())
			.append(".img/")
			.append(mobName.getMobId())
			.append(".img/")
			.append(file2.getName())
			.append("]");
		}
		
		if(mobInfo!=null) {
			reply.append("\r\n").append(mobName.getName()).append("(").append(mobName.getMobId()).append(")");
			if(mobInfo.getBoss()==null) {
				reply.append("\r\n怪物类型:普通怪");
			}else {
				reply.append("\r\n怪物类型:首领怪");
			}
			reply.append("\r\n")
			.append("等级:").append(mobInfo.getLevel()).append("\r\n")
			.append("血量:").append(mobInfo.getMaxHp()).append("\r\n")
//			.append("蓝量:").append(mobInfo.getMaxMp()).append("\r\n")
			.append("防御:").append(mobInfo.getPdRate()).append("%\r\n")
//			.append("魔法防御:").append(mobInfo.getMdRate()).append("%\r\n")
//			.append("移动速度:").append(mobInfo.getSpeed()).append("\r\n")
//			.append("击退伤害:").append(mobInfo.getPushed()).append("\r\n")
			.append("经验:").append(mobInfo.getExp()).append("\r\n")
			.append("冰雷火毒圣暗物").append("\r\n");
			if(mobInfo.getElemAttr()==null) {
				reply.append("○○○○○○○");
			}else {
				String attr = mobInfo.getElemAttr();
				Map<String,String> attrMap = new HashMap<>();
				attrMap.put("I", "○");attrMap.put("L", "○");attrMap.put("F", "○");attrMap.put("S", "○");
				attrMap.put("H", "○");attrMap.put("D", "○");attrMap.put("P", "○");
				int index=0;
				while(index<attr.length()) {
					if(attr.substring(index+1,index+2).equals("1")) {
						attrMap.put(attr.substring(index,index+1), "×");
					}else if(attr.substring(index+1,index+2).equals("2")) {
						attrMap.put(attr.substring(index,index+1), "△");
					}else if(attr.substring(index+1,index+2).equals("3")) {
						attrMap.put(attr.substring(index,index+1), "◎");
					}
					index = index+2;
					System.out.println(index);
				}
				reply.append(attrMap.get("I"))
				.append(attrMap.get("L"))
				.append(attrMap.get("F"))
				.append(attrMap.get("S"))
				.append(attrMap.get("H"))
				.append(attrMap.get("D"))
				.append(attrMap.get("P"));
			}
		}
		
		ChannelReplyMsg crm = new ChannelReplyMsg();
		crm.setChannel_id(channel_id);
		crm.setGuild_id(guild_id);
		crm.setMessage(reply.toString());
		groupMsgService.sendChannelMsg(crm);
		
		return null;
	}

	@Override
	public String searchMobForChannel(String raw_message, Long channel_id, Long guild_id) {
		raw_message = raw_message.replaceAll("&#91;", "[").replaceAll("&#93;", "]");
		System.out.println(raw_message);
		List<MobName> mobNameList = mobNameRepository.findByName(raw_message);
//		List<MobName> mobNameExactList = mobNameRepository.findAllMob();
		StringBuffer reply = new StringBuffer();
		System.out.println(mobNameList.size());
		int i=0;
		if(mobNameList.size()!=0) {
			while(i<mobNameList.size()) {
				MobName mobName = mobNameList.get(i);
				MobInfo mobInfo = mobInfoRepository.findMobInfoByMobId(mobNameList.get(i).getMobId());
				try {
					if(mobInfo.getLink()!=null) {
						String url = MsbotConst.imageUrl+"result_img/"+mobInfo.getLink()+".img/"+mobInfo.getLink()+".img";
						File file = new File(url);
						if(file.exists()) {
							File[] listFiles = file.listFiles();
							File file2 = listFiles[0];
							reply.append("[CQ:image,file=result_img/")
							.append(mobInfo.getLink())
							.append(".img/")
							.append(mobInfo.getLink())
							.append(".img/")
							.append(file2.getName())
							.append("]");
							break;
						}
					}
				} catch (Exception e) {
					System.out.println("link 空指针");
				}
				String url = MsbotConst.imageUrl+"result_img/"+mobName.getMobId()+".img/"+mobName.getMobId()+".img";
				System.out.println(url);
				File file = new File(url);
				if(file.exists()) {
					File[] listFiles = file.listFiles();
					File file2 = listFiles[0];
					reply.append("[CQ:image,file=result_img/")
					.append(mobName.getMobId())
					.append(".img/")
					.append(mobName.getMobId())
					.append(".img/")
					.append(file2.getName())
					.append("]");
					break;
				}
				i=i+1;
			}
			if(i==mobNameList.size()) {
				i=0;
			}
			MobInfo mobInfo = mobInfoRepository.findMobInfoByMobId(mobNameList.get(i).getMobId());
			if(mobInfo!=null) {
				reply.append("\r\n").append(mobNameList.get(i).getName()).append("(").append(mobNameList.get(i).getMobId()).append(")");
				if(mobInfo.getBoss()==null) {
					reply.append("\r\n怪物类型:普通怪");
				}else {
					reply.append("\r\n怪物类型:首领怪");
				}
				reply.append("\r\n")
				.append("等级:").append(mobInfo.getLevel()).append("\r\n")
				.append("血量:").append(mobInfo.getMaxHp()).append("\r\n")
//				.append("蓝量:").append(mobInfo.getMaxMp()).append("\r\n")
				.append("防御:").append(mobInfo.getPdRate()).append("%\r\n")
//				.append("魔法防御:").append(mobInfo.getMdRate()).append("%\r\n")
//				.append("移动速度:").append(mobInfo.getSpeed()).append("\r\n")
//				.append("击退伤害:").append(mobInfo.getPushed()).append("\r\n")
				.append("经验:").append(mobInfo.getExp()).append("\r\n")
				.append("冰雷火毒圣暗物").append("\r\n");
				if(mobInfo.getElemAttr()==null) {
					reply.append("○○○○○○○");
				}else {
					String attr = mobInfo.getElemAttr();
					Map<String,String> attrMap = new HashMap<>();
					attrMap.put("I", "○");attrMap.put("L", "○");attrMap.put("F", "○");attrMap.put("S", "○");
					attrMap.put("H", "○");attrMap.put("D", "○");attrMap.put("P", "○");
					int index=0;
					while(index<attr.length()) {
						if(attr.substring(index+1,index+2).equals("1")) {
							attrMap.put(attr.substring(index,index+1), "×");
						}else if(attr.substring(index+1,index+2).equals("2")) {
							attrMap.put(attr.substring(index,index+1), "△");
						}else if(attr.substring(index+1,index+2).equals("3")) {
							attrMap.put(attr.substring(index,index+1), "◎");
						}
						index = index+2;
						System.out.println(index);
					}
					reply.append(attrMap.get("I"))
					.append(attrMap.get("L"))
					.append(attrMap.get("F"))
					.append(attrMap.get("S"))
					.append(attrMap.get("H"))
					.append(attrMap.get("D"))
					.append(attrMap.get("P"));
				}
			}
		}else {
			reply.append("没有查询到怪物信息");
		}
		ChannelReplyMsg crm = new ChannelReplyMsg();
		crm.setChannel_id(channel_id);
		crm.setGuild_id(guild_id);
		crm.setMessage(reply.toString());
		groupMsgService.sendChannelMsg(crm);
		
		
		if(mobNameList.size()>=2) {
			String reply2 = "查询到同名怪物有：";
			for(MobName mn : mobNameList) {
				reply2 = reply2 + "\r\n" + mn.getName()+"("+mn.getMobId()+")";
			}			
			ChannelReplyMsg crm2 = new ChannelReplyMsg();
			crm2.setChannel_id(channel_id);
			crm2.setGuild_id(guild_id);
			crm2.setMessage(reply2);
			groupMsgService.sendChannelMsg(crm2);
		}
		
		List<MobName> mobNameLikeList = mobNameRepository.findByNameLike(raw_message);
		mobNameLikeList.removeAll(mobNameList);
		System.out.println("相似的结果量"+mobNameLikeList.size());
		String reply3 = "查询到相似的怪物有：";
		if(mobNameLikeList.size()==0) {
			reply3 = "查询到相似的怪物有：null";
			return null;
		}else if(mobNameLikeList.size()<30) {
			for(MobName mn : mobNameLikeList) {
				reply3 = reply3 + "\r\n" + mn.getName()+"("+mn.getMobId()+")";
			}
		}else{
			reply3 = "查询到相似的怪物有：查询结果超过30条，为防止刷屏，已将结果私聊于您。";
		}
		ChannelReplyMsg crm3 = new ChannelReplyMsg();
		crm3.setChannel_id(channel_id);
		crm3.setGuild_id(guild_id);
		crm3.setMessage(reply3);
		groupMsgService.sendChannelMsg(crm3);		
		return null;
		
	}

}
