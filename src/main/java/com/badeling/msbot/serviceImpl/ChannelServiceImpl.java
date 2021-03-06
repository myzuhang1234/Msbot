package com.badeling.msbot.serviceImpl;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
import com.badeling.msbot.domain.ChannelMsg;
import com.badeling.msbot.domain.ChannelReplyMsg;
import com.badeling.msbot.domain.ReplyMsg;
import com.badeling.msbot.entity.Msg;
import com.badeling.msbot.entity.MsgNoPrefix;
import com.badeling.msbot.entity.RoleDmg;
import com.badeling.msbot.repository.MsgNoPrefixRepository;
import com.badeling.msbot.repository.MsgRepository;
import com.badeling.msbot.repository.RoleDmgRepository;
import com.badeling.msbot.service.ChannelService;
import com.badeling.msbot.service.DrawService;
import com.badeling.msbot.service.GroupMsgService;
import com.badeling.msbot.service.MvpImageService;
import com.badeling.msbot.service.WzXmlService;
import com.badeling.msbot.util.Loadfont2;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ChannelServiceImpl implements ChannelService{
	
	@Autowired
	MsgRepository msgRepository;
	
	@Autowired
	RoleDmgRepository roleDmgRepository;
	
	@Autowired
	GroupMsgService groupMsgService;
	
	@Autowired
	DrawService drawService;
	
	@Autowired
	MvpImageService mvpImageService;
	
	@Autowired
	MsgZbCalculate msgZbCalculate;
	
	@Autowired
	WzXmlService wzXmlService;
	
	@Autowired
	MsgNoPrefixRepository msgNoPrefixRepository;
	
	@Override
	public void receive(String msg) {
		ChannelMsg channelMsg = null;
		try {
			channelMsg = new ObjectMapper().readValue(msg, ChannelMsg.class);
		} catch (JsonProcessingException e) {
			// TODO ??????????????? catch ???
			e.printStackTrace();
		}
		boolean isbotChannel = false;
		
		for(String channelId : MsbotConst.botChannel) {
			if((channelMsg.getChannel_id()+"").equals(channelId)) {
				isbotChannel = true;
				break;
			}
		}
		
		if(isbotChannel) {
			if(channelMsg.getMessage().startsWith(MsbotConst.botName)) {
	        	System.out.println(channelMsg.toString());
	        	ReplyMsg result = handleNameMsg(channelMsg);
	        	if(result!=null) {
	        		ChannelReplyMsg crm = new ChannelReplyMsg();
					crm.setChannel_id(channelMsg.getChannel_id());
					crm.setGuild_id(channelMsg.getGuild_id());
					crm.setMessage(result.getReply());
					groupMsgService.sendChannelMsg(crm);
	        	}
			}else {
				List<MsgNoPrefix> result = msgNoPrefixRepository.findMsgNPList();
				for(MsgNoPrefix m : result) {
					if(m.isExact()&&channelMsg.getMessage().equals(m.getQuestion())) {
						ChannelReplyMsg crm = new ChannelReplyMsg();
						crm.setChannel_id(channelMsg.getChannel_id());
						crm.setGuild_id(channelMsg.getGuild_id());
						crm.setMessage(m.getAnswer());
						groupMsgService.sendChannelMsg(crm);
		    			break;
					}
					if(!m.isExact()&&channelMsg.getMessage().contains(m.getQuestion())) {
						ChannelReplyMsg crm = new ChannelReplyMsg();
						crm.setChannel_id(channelMsg.getChannel_id());
						crm.setGuild_id(channelMsg.getGuild_id());
						crm.setMessage(m.getAnswer());
						groupMsgService.sendChannelMsg(crm);
		    			break;
		    			
					}
				}
			}
		}
		
		
		
		
	}

	private ReplyMsg handleNameMsg(ChannelMsg channelMsg) {
		Msg msg = null;
		ReplyMsg replyMsg = new ReplyMsg();
		String raw_message = channelMsg.getMessage();
		replyMsg.setAt_sender(true);
		replyMsg.setAuto_escape(false);
		//??????
		Set<Msg> set = msgRepository.findAllQuestion();
		Iterator<Msg> it = set.iterator();
		
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
		//??????
		if(raw_message.contains("?????????")) {
				replyMsg.setAt_sender(true);
				replyMsg.setReply("?????????1??????372752762\r\n?????????2??????1107518527");
				return replyMsg;
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
				
				RoleDmg roleDmg = roleDmgRepository.findRoleBynumber(channelMsg.getSender().getUser_id());
				if(roleDmg == null) {
					//???????????????
					roleDmg = new RoleDmg();
					//??????????????? ???????????? ????????????
					if(channelMsg.getSender().getNickname()!=null) {
						roleDmg.setName(channelMsg.getSender().getNickname());
					}else {
						roleDmg.setName(channelMsg.getSender().getCard());
					}
					//??????QQ???
				roleDmg.setUser_id(channelMsg.getSender().getUser_id());
					//????????????
					roleDmg.setGroup_id(channelMsg.getChannel_id()+"");
					roleDmg.setCommonDmg(100);
					roleDmg.setBossDmg(200);
					roleDmgRepository.save(roleDmg);
					
					ChannelReplyMsg crm = new ChannelReplyMsg();
					crm.setChannel_id(channelMsg.getChannel_id());
					crm.setGuild_id(channelMsg.getGuild_id());
					crm.setMessage("???????????????????????????????????????100,boss???200????????????????????????"+MsbotConst.botName+" ??????50 boss300???????????????????????????");
//					crm.setMessage("[CQ:at,qq="+ channelMsg.getSender().getUser_id() +"]"+"???????????????????????????????????????100,boss???200????????????????????????"+CoolQconst.botName+" ??????50 boss300???????????????????????????");
					groupMsgService.sendChannelMsg(crm);

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
				return replyMsg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		//????????????
		if(raw_message.contains("??????")&&(raw_message.contains("boss")||raw_message.contains("BOSS"))) {
			String[] split = raw_message.split(" ");
			RoleDmg roleDmg = roleDmgRepository.findRoleBynumber(channelMsg.getSender().getUser_id());
			if(roleDmg == null) {
				//???????????????
				roleDmg = new RoleDmg();
				//??????????????? ???????????? ????????????
				roleDmg.setName(channelMsg.getSender().getNickname());
				//??????QQ???
				roleDmg.setUser_id(channelMsg.getSender().getUser_id());
				//????????????
				roleDmg.setGroup_id(channelMsg.getChannel_id()+"");
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
		
		try {
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
				
				int[] starForce = MsgServiceImpl.starForceDesc(level,stat-fireStat,att-fireAtt,nowStar);
				int finalStat = starForce[0]+fireStat;
				int finalAtt = starForce[1]+fireAtt;
				String result = "0??????????????????????????????" + finalAtt;
				replyMsg.setReply(result);
				return replyMsg;
			}

			
			//????????????
			if(raw_message.contains("???")&&raw_message.contains("???")) {
				raw_message = raw_message.replaceAll(" ", "").substring(2);
				
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
				int[] starForce = MsgServiceImpl.starForce(level,stat-fireStat,att-fireAtt,nowStar,targetStar,isWeapon);
				int finalStat = starForce[0]+fireStat;
				int finalAtt = starForce[1]+fireAtt;

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
			return replyMsg;
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
		
		//??????
		if(raw_message.startsWith(MsbotConst.botName+"??????")||raw_message.startsWith(MsbotConst.botName+" ??????")) {
			raw_message = raw_message.replaceAll("??????", "");
			raw_message = raw_message.replaceAll(" ", "");
			raw_message = raw_message.replaceAll(MsbotConst.botName, "");
			if(raw_message.isEmpty()) {
				replyMsg.setReply("???????????????+???????????????????????????????????????????????????????????????????????????????????????????????????????????????");
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
			reply = msgZbCalculate.msgDeZb(channelMsg.getUser_id()+"");
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
			replyMsg.setReply("???nm");
			return replyMsg;
		}
		//????????????
		if(raw_message.startsWith(MsbotConst.botName+"??????")||raw_message.startsWith(MsbotConst.botName+" ??????")) {
			if(raw_message.equals(MsbotConst.botName+"??????")||raw_message.equals(MsbotConst.botName+" ??????")) {
				replyMsg.setReply("????????????????????????");
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
							wzXmlService.searchMobForChannel(mob_id,channelMsg.getChannel_id(),channelMsg.getGuild_id());
							return null;
						}
					} catch (Exception e) {
					}
					wzXmlService.searchMobForChannel(raw_message,channelMsg.getChannel_id(),channelMsg.getGuild_id());
					return null;
					
				}else {
					
				}
				
			}
		}
		
		if(raw_message.replaceAll(MsbotConst.botName, "").replaceAll(" ","").replaceAll("???","").equals("")) {
			raw_message = MsbotConst.botName+"??????????????????";
		}
		
		//??????
		if(raw_message.startsWith(MsbotConst.botName+"??????")||raw_message.startsWith(MsbotConst.botName+" ??????")) {
			String reply = new String();
			String name = channelMsg.getSender().getNickname();
			try {
				reply = msgZbCalculate.msgZb(channelMsg.getUser_id()+"");
				reply = name + reply;
				replyMsg.setReply(reply);
				replyMsg.setAt_sender(true);
				return replyMsg;
			} catch (Exception e) {
				// TODO ??????????????? catch ???
				e.printStackTrace();
			}
			
			return null;
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
				replyMsg.setReply(raw_message.substring(channelMsg.getMessage().indexOf(MsbotConst.botName)+2)+"="+result.toString());
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
				ChannelReplyMsg crm = new ChannelReplyMsg();
				crm.setChannel_id(channelMsg.getChannel_id());
				crm.setGuild_id(channelMsg.getGuild_id());
				crm.setMessage(msg2.getAnswer());
				groupMsgService.sendChannelMsg(crm);
				 
				 String[] split = msg2.getLink().split("#abcde#");
				 for(String temp : split) {
					 try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					 ChannelReplyMsg crm2 = new ChannelReplyMsg();
					 	crm2 = new ChannelReplyMsg();
						crm2.setChannel_id(channelMsg.getChannel_id());
						crm2.setGuild_id(channelMsg.getGuild_id());
						crm2.setMessage(temp);
						groupMsgService.sendChannelMsg(crm2);
				 }
				 return null;
			 }

			 return replyMsg;
		 }
		 
		 if(MsbotConst.moliKey2!=null&&MsbotConst.moliSecret2!=null&&!MsbotConst.moliKey2.isEmpty()&&!MsbotConst.moliSecret2.isEmpty()) {
			 if(!raw_message.contains("[CQ")) {
				String tuLingMsg = groupMsgService.MoliMsg2(command, Math.abs(channelMsg.getUser_id().hashCode())+"", channelMsg.getSender().getNickname());
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
