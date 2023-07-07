package com.badeling.msbot.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.controller.MsgZbCalculate;
import com.badeling.msbot.domain.GlobalVariable;
import com.badeling.msbot.domain.ReplyMsg;
import com.badeling.msbot.entity.Msg;
import com.badeling.msbot.entity.RoleDmg;
import com.badeling.msbot.repository.MsgRepository;
import com.badeling.msbot.repository.RoleDmgRepository;
import com.badeling.msbot.service.*;
import com.badeling.msbot.util.CosSdk;
import com.badeling.msbot.util.Loadfont2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ChannelOfficialServiceImpl implements ChannelOfficialService{

	@Autowired
	MsgRepository msgRepository;

	@Autowired
	RoleDmgRepository roleDmgRepository;

	@Autowired
	DrawService drawService;

	@Autowired
	MvpImageService mvpImageService;

	@Autowired
	MsgZbCalculate msgZbCalculate;


	@Autowired
	RankService rankService;

	@Autowired
	GroupMsgService groupMsgService;

	public static void main(String[] args) {

	}

	@SuppressWarnings("unused")
	@Override
	public String receive(String msg) {
		Map<String, Object> map = (Map<String, Object>)JSON.parseObject(msg);

		String message = map.get("message")+"";

		while(!message.isEmpty()&&(message.startsWith(" ")||message.startsWith("/"))) {
			message = message.substring(1);
		}

		while(!message.isEmpty()&&message.endsWith(" ")) {
			message = message.substring(0,message.length()-1);
		}

		String raw_message = MsbotConst.channelBotName + message;
		String channel_id = map.get("channel_id")+"";
		String user_id = map.get("user_id")+"";
		String user_name = map.get("user_name")+"";
		String guild_id = map.get("guild_id")+"";

		if(guild_id.equals("2345324182645790134")&&!channel_id.equals("2105717")) {
			return null;
		}

//        String message_id = map.get("message_id")+"";
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

		ReplyMsg result = handleNameMsg(raw_message,channel_id,user_id,user_name);
		//at信息
		if(result.isAt_sender()) {
			String atMsg = "<@!" + user_id + ">";
			result.setReply(atMsg + " "+result.getReply());
		}

		if(result!=null) {
			Map<String,Object> replyMsg = new HashMap<String,Object>();
			replyMsg.put("channelId", channel_id);

			if(result.getReply().contains(MsbotConst.botName)) {
				result.setReply(result.getReply().replaceAll(MsbotConst.botName, MsbotConst.channelBotName));
			}

			if(!result.getReply().contains("CQ:image,file")) {
				replyMsg.put("content", result.getReply());
				return JSON.toJSONString(replyMsg);
			}

			String[] shortMessage = result.getReply().split("\\[CQ:image,file=");
			String resultMsg = "";
			for(int i=0;i<shortMessage.length;i++) {
				int indexOf = shortMessage[i].indexOf("]");
				if(i>0&&indexOf!=-1) {
					if(i==1) {
						String imageName = shortMessage[i].substring(0,indexOf).replaceAll("\\\\", "/");
						String uploadFile = CosSdk.uploadFile(MsbotConst.imageUrl+imageName);
						String imageUrl = "https://image.badeling.com/"+uploadFile;
						replyMsg.put("image", imageUrl);
					}
//        			resultMsg += "<img src=\"image/";
//        			resultMsg += shortMessage[i].substring(0,indexOf);
//        			resultMsg += "\" alt=\"\" />";
					resultMsg += shortMessage[i].substring(indexOf+1);
				}else {
					resultMsg += shortMessage[i];
				}
			}

			if(!resultMsg.equals("")) {
				replyMsg.put("content", resultMsg);
			}
			return JSON.toJSONString(replyMsg);
		}

		return null;
	}

	private ReplyMsg handleNameMsg(String raw_message,String channel_id,String user_id,String user_name) {
		Msg msg = null;
		ReplyMsg replyMsg = new ReplyMsg();
		replyMsg.setAt_sender(true);
		replyMsg.setAuto_escape(false);
		//查询
		Set<Msg> set = msgRepository.findAllQuestion();
		Iterator<Msg> it = set.iterator();

		//测试字体
		if(raw_message.startsWith(MsbotConst.channelBotName+"测试字体")) {
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

		if(raw_message.startsWith(MsbotConst.channelBotName+"联盟")) {
			if(raw_message.equals(MsbotConst.channelBotName+"联盟")) {
				replyMsg.setReply("联盟后面跟上角色名就可以查询角色数据啦");
			}else {
				String rank = rankService.getRank(raw_message.substring(raw_message.indexOf("联盟")));
				replyMsg.setReply(rank);
			}
			return replyMsg;

		}

		//无视计算
		if(raw_message.contains("无视")&&(raw_message.contains("+")||raw_message.contains("-"))) {
			try {
				//消息过滤
				raw_message = raw_message.replace("%", "");
				raw_message = raw_message.replace(MsbotConst.channelBotName, "");
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

				RoleDmg roleDmg = roleDmgRepository.findRoleBynumber(user_id);
				if(roleDmg == null) {
					//查询无角色
					roleDmg = new RoleDmg();
					//设置群名片 如果没有 设置昵称
					roleDmg.setName(user_name);
					//设置QQ号
					roleDmg.setUser_id(user_id);
					//设置群号
					roleDmg.setGroup_id(channel_id+"");
					roleDmg.setCommonDmg(100);
					roleDmg.setBossDmg(200);
					roleDmgRepository.save(roleDmg);

//					ChannelReplyMsg crm = new ChannelReplyMsg();
//					crm.setChannel_id(channelMsg.getChannel_id());
//					crm.setGuild_id(channelMsg.getGuild_id());
//					crm.setMessage("未查询到角色信息，默认伤害100,boss伤200。你可通过指令【"+MsbotConst.channelBotName+" 伤害50 boss300】命令修改角色信息");
//					crm.setMessage("[CQ:at,qq="+ channelMsg.getSender().getUser_id() +"]"+"未查询到角色信息，默认伤害100,boss伤200。你可通过指令【"+MsbotConst.channelBotName+" 伤害50 boss300】命令修改角色信息");
//					groupMsgService.sendChannelMsg(crm);

				}

//				shortMsg += " = " + String.format("%.2f", ign) + "%";
//				shortMsg += "\r\n为防止刷屏，详细计算的部分将以私聊的形式发送于您。";

				ign2 = ign + (100-ign)*20/100;
				String replyM = "你之前的无视：" + ign_before + "%(" + ign_before2 + "%)\r\n" + "计算后的无视：" + String.format("%.2f", ign) + "%(" + String.format("%.2f", ign2) + "%)\r\n";
				replyM += "角色数据 伤害:" + roleDmg.getCommonDmg() + "% boss:" + roleDmg.getBossDmg() + "%\r\n(括号为核心20%无视加成结果)\r\n";

				replyM += "//----超高防对比-----//\r\n";
				replyM += "卡琳提升率（380超高防）：" + String.format("%.2f", (defAndign(380, ign)/defAndign(380, ign_before)-1)*100) + "%(" + String.format("%.2f", (defAndign(380, ign2)/defAndign(380, ign_before2)-1)*100) + "%)\r\n";
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

				return replyMsg;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		//内鬼
		if(raw_message.contains("职业群")) {
			replyMsg.setAt_sender(true);
			replyMsg.setReply("林之灵1群：372752762\r\n林之灵2群：1107518527");
			return replyMsg;
		}

		if(raw_message.contains("无视")) {
			replyMsg.setReply("无视计算功能包含以下三个方面\r\n" +
					"//无视加算\r\n" +
					"无视 70+50+20\r\n" +
					"//无视逆运算\r\n" +
					"无视 90-50\r\n" +
					"//修改角色属性数据\r\n" +
					"伤害50 boss250");
			return replyMsg;
		}

		//伤害信息
		if(raw_message.contains("伤害")&&(raw_message.contains("boss")||raw_message.contains("BOSS"))) {
			String[] split = raw_message.split(" ");
			RoleDmg roleDmg = roleDmgRepository.findRoleBynumber(user_id);
			if(roleDmg == null) {
				//查询无角色
				roleDmg = new RoleDmg();
				//设置群名片 如果没有 设置昵称
				roleDmg.setName(user_name);
				//设置QQ号
				roleDmg.setUser_id(user_id);
				//设置群号
				roleDmg.setGroup_id(channel_id+"");
				roleDmg.setCommonDmg(100);
				roleDmg.setBossDmg(200);
				roleDmg = roleDmgRepository.save(roleDmg);
			}
			try {
				for(String temp : split) {
					temp.replace("%", "");
					if(temp.contains("伤害")) {
						roleDmg.setCommonDmg(Integer.parseInt(temp.replace(MsbotConst.channelBotName,"").replace("伤害", "")));
					}else if(temp.toLowerCase().contains("boss")) {
						temp = temp.toLowerCase();
						roleDmg.setBossDmg(Integer.parseInt(temp.replace(MsbotConst.channelBotName,"").replace("boss", "")));
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

				int[] starForce = MsgServiceImpl.starForceDesc(level,stat-fireStat,att-fireAtt,nowStar);
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
				int[] starForce = MsgServiceImpl.starForce(level,stat-fireStat,att-fireAtt,nowStar,targetStar,isWeapon);
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
			String mes;
			try {
				mes = drawService.startDrawMs();
			} catch (Exception e) {
				e.printStackTrace();
				mes = "图片文件缺失。";
			}
			replyMsg.setAt_sender(true);
			replyMsg.setReply(mes);
			return replyMsg;
		}

		//官网
		if(raw_message.startsWith(MsbotConst.channelBotName+"官网")||raw_message.startsWith(MsbotConst.channelBotName+" 官网")) {
			raw_message = raw_message.replaceAll("官网", "");
			raw_message = raw_message.replaceAll(" ", "");
			raw_message = raw_message.replaceAll(MsbotConst.channelBotName, "");

			if(raw_message.isEmpty()) {
				replyMsg.setReply("输入【官网+项目】，查询游戏官网最新资讯。常见项目有：周日冒险岛、维护、敲敲乐、礼品袋");
				return replyMsg;
			}

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
				replyMsg.setReply(message);
				return replyMsg;
			} catch (Exception e) {
				replyMsg.setReply("查询失败");
				return replyMsg;
			}
		}

		if(raw_message.startsWith(MsbotConst.channelBotName+"抽签")||raw_message.startsWith(MsbotConst.channelBotName+"运势")) {
			String reply = new String();
			reply = msgZbCalculate.msgDeZb(user_id+"");
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


		if(raw_message.replace(" ", "").equals(MsbotConst.channelBotName)) {
			replyMsg.setAuto_escape(false);
			replyMsg.setReply("(ﾉﾟ▽ﾟ)ﾉ我在哦~");
			return replyMsg;
		}

		if(raw_message.replaceAll(MsbotConst.channelBotName, "").replaceAll(" ","").equals("")) {
			raw_message = raw_message.replaceAll(" ","");
			raw_message = raw_message.replaceFirst(MsbotConst.channelBotName, "");
			raw_message = raw_message.replaceFirst(MsbotConst.channelBotName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("禁止套娃");
				return replyMsg;
			}
			raw_message = raw_message.replaceFirst(MsbotConst.channelBotName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("禁止双重套娃");
				return replyMsg;
			}
			raw_message = raw_message.replaceFirst(MsbotConst.channelBotName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("禁止三重套娃");
				return replyMsg;
			}
			raw_message = raw_message.replaceFirst(MsbotConst.channelBotName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("禁止四重套娃");
				return replyMsg;
			}
			raw_message = raw_message.replaceFirst(MsbotConst.channelBotName, "");
			if(raw_message.equals("")) {
				replyMsg.setReply("禁止五重套娃");
				return replyMsg;
			}
			replyMsg.setAt_sender(false);
			replyMsg.setReply("=.=");
			return replyMsg;
		}

		if(MsbotConst.moliKey!=null&&MsbotConst.moliSecret!=null&&!MsbotConst.moliKey.isEmpty()&&!MsbotConst.moliSecret.isEmpty()) {

		}

		if(raw_message.replaceAll(MsbotConst.channelBotName, "").replaceAll(" ","").replaceAll("？","").equals("")) {
			raw_message = MsbotConst.channelBotName+"固定回复问号";
		}

		//占卜
		if(raw_message.startsWith(MsbotConst.channelBotName+"占卜")||raw_message.startsWith(MsbotConst.channelBotName+" 占卜")) {
			String reply = new String();
			try {
				reply = msgZbCalculate.msgZb(user_id+"");
				replyMsg.setReply(reply);
				replyMsg.setAt_sender(true);
				return replyMsg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		try {
			if(raw_message.contains("^")) {
				replyMsg.setReply(MsbotConst.channelBotName+"不支持平方计算");
				return replyMsg;
			}else if((raw_message.contains("×")||raw_message.contains("÷")||raw_message.contains("+")||raw_message.contains("-")||raw_message.contains("*")||raw_message.contains("/"))){
				raw_message = raw_message.replace("×", "*");
				raw_message = raw_message.replace("÷", "/");
				ScriptEngineManager manager = new ScriptEngineManager();
				ScriptEngine engine = manager.getEngineByName("js");
				Object result = engine.eval(raw_message.substring(raw_message.indexOf(MsbotConst.channelBotName)+2));
				replyMsg.setReply(raw_message.substring(raw_message.indexOf(MsbotConst.channelBotName)+2)+"="+result.toString());
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
			if(msg2.getLink()==null) {
				replyMsg.setReply(msg2.getAnswer());
			}else {
				String msgReply = msg2.getAnswer();

				String[] split = msg2.getLink().split("#abcde#");
				for(String temp : split) {
					msgReply += "\r\n" + temp;
				}
				replyMsg.setReply(msgReply);
			}

			return replyMsg;
		}

		if(MsbotConst.moliKey!=null&&MsbotConst.moliSecret!=null&&!MsbotConst.moliKey.isEmpty()&&!MsbotConst.moliSecret.isEmpty()) {
			//消息传给图灵
			String tuLingMsg = groupMsgService.MoliMsg(command, Math.abs(user_id.hashCode())+"", user_name);
			@SuppressWarnings("unchecked")
			Map<String,Object> result = (Map<String, Object>) JSON.parse(tuLingMsg);
			System.out.println(tuLingMsg);
			if((result.get("message")+"").contains("请求成功")) {
				String reply = tuLingMsg.substring(tuLingMsg.indexOf("content")+10,tuLingMsg.indexOf("\",\"typed\":"));
				replyMsg.setReply(reply);
				return replyMsg;
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

}