package com.badeling.msbot.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.entity.LuckyMap;
import com.badeling.msbot.entity.LuckyTable;
import com.badeling.msbot.entity.LuckyThing;
import com.badeling.msbot.repository.LuckyMapRepository;
import com.badeling.msbot.repository.LuckyTableRepository;
import com.badeling.msbot.repository.LuckyThingRepository;
import com.badeling.msbot.service.DrawService;
import com.badeling.msbot.util.Loadfont2;

@Component
public class MsgZbCalculate {
	@Autowired
	LuckyMapRepository luckyMapRepository;
	@Autowired
	LuckyTableRepository luckyTableRepository;
	@Autowired
	LuckyThingRepository luckyThingRepository;
	@Autowired
	DrawService drawService;

	public String msgZb(String numb) {
		Date date = new Date(System.currentTimeMillis());
		//几号
		SimpleDateFormat format1 = new SimpleDateFormat("dd");
		//星期几
		SimpleDateFormat format2 = new SimpleDateFormat("u");
		String reply = new String();
		int i = Math.abs((numb+format1.format(date)+format2.format(date)).hashCode()%97);
		int j = Math.abs((numb+format2.format(date)+format1.format(date)).hashCode()%12)+1;
		int k = Math.abs((format1.format(date)+ numb +format2.format(date)).hashCode());
		int l = Math.abs((format2.format(date)+ numb +format1.format(date)).hashCode());
		int m = Math.abs((format1.format(date)+format2.format(date)+numb).hashCode());
		int n = Math.abs((format2.format(date)+format1.format(date)+numb).hashCode());

		/**
		 * i 运势
		 * j 频道
		 * i运势算n标签
		 * k 宜
		 * l 忌
		 * m 地图
		 */
		String lucky = luckyRank2(i);
		//i n
		int count = luckyTableRepository.getCountByRank(lucky);
		n = n % count;
		LuckyTable lt = luckyTableRepository.findByRandom(lucky,n);
		String luckyTable = lt.getLuckyTable();
		//k
		int count2 = luckyThingRepository.getCount();
		k = k % count2;
		l = l % count2;
		while(l==k) {
			l = (l+1) % count2;
		}
		LuckyThing lgt = luckyThingRepository.findByRandom(l);
		LuckyThing lbt = luckyThingRepository.findByRandom(k);
		String luckyThing = "宜：" + lgt.getGood() + "\n" + lgt.getGoodThing();
		//l
		String luckyBdThing = "忌：" + lbt.getBad() + "\n" + lbt.getBadThing();
		//m
		int count3 = luckyMapRepository.getCount();
		m = m % count3;
		LuckyMap lm = luckyMapRepository.findByRandom(m);
		String luckyMap = lm.getMapUrl() + lm.getMap();
		reply = "\n"+ "您今日的运势指数为" +luckyRank(i)+"\n"
				+ "运势最好的频道是"+ j +"频道哦！"+"\n"
				+ luckyTable + "\n"
				+ "//-------------------//"+"\n"
				+ luckyThing+"\n"
				+ "//-------------------//"+"\n"
				+ luckyBdThing +"\n"
				+ "//-------------------//"+"\n"
				+ "今日最佳玄学地图是"+"\n"
				+ luckyMap +"\n"
				+ "//-------------------//";

		System.out.println(reply);

		String a1 = "您今日的运势指数为" +luckyRank(i);
		String a2 = "运势最好的频道是"+ j +"频道哦！";
		String a3 = luckyTable;
		String a4 = "宜：" + lgt.getGood();
		String a5 = lgt.getGoodThing();
		String a6 = "忌：" + lbt.getBad();
		String a7 = lbt.getBadThing();
		String a8 = "今日最佳玄学地图是：";
		String a9 = lm.getMap();


		String mapUrl = MsbotConst.imageUrl + lm.getMapUrl().substring(15,lm.getMapUrl().length()-1).replaceAll("\\\\", "/");


		String roleUrl = MsbotConst.imageUrl + "class/0.png";
		File f = new File(MsbotConst.imageUrl + "class");
		File[] listFiles = f.listFiles();

		int p = Math.abs((format2.format(date)+format1.format(date)+numb).hashCode()%listFiles.length);
		File file = listFiles[p];

		roleUrl = file.getParent() + "/" +file.getName();

		String[] msg = {a1,a2,a3,a4,a5,a6,a7,a8,a9,mapUrl,roleUrl};
		try {
			reply = drawService.zbImage(msg);
		} catch (Exception e) {
			reply = "出现未知的错误";
			e.printStackTrace();
		}
		return reply;
	}
	public String msgZb(String numb,String name) throws Exception {
		Date date = new Date(System.currentTimeMillis());
		//几号
		SimpleDateFormat format1 = new SimpleDateFormat("dd");
		//星期几
		SimpleDateFormat format2 = new SimpleDateFormat("u");
		int i = Math.abs((numb+format1.format(date)+format2.format(date)).hashCode()%97);
		int j = Math.abs((numb+format2.format(date)+format1.format(date)).hashCode()%20)+1;
		int k = Math.abs((format1.format(date)+ numb +format2.format(date)).hashCode());
		int l = Math.abs((format2.format(date)+ numb +format1.format(date)).hashCode());
		int m = Math.abs((format1.format(date)+format2.format(date)+numb).hashCode());
		int n = Math.abs((format2.format(date)+format1.format(date)+numb).hashCode());
		/**
		 * i 运势
		 * j 频道
		 * i运势算n标签
		 * k 宜
		 * l 忌
		 * m 地图
		 */
		String lucky = luckyRank2(i);
		//i n
		int count = luckyTableRepository.getCountByRank(lucky);
		n = n % count;
		LuckyTable lt = luckyTableRepository.findByRandom(lucky,n);
		String luckyTable = lt.getLuckyTable();
		//k
		int count2 = luckyThingRepository.getCount();
		k = k % count2;
		l = l % count2;
		while(l==k) {
			l = (l+1) % count2;
		}
		LuckyThing lgt = luckyThingRepository.findByRandom(l);
		LuckyThing lbt = luckyThingRepository.findByRandom(k);
		String luckyThing = "宜：" + lgt.getGood() + "\n" + lgt.getGoodThing();
		//l
		String luckyBdThing = "忌：" + lbt.getBad() + "\n" + lbt.getBadThing();
		//m
		int count3 = luckyMapRepository.getCount();
		m = m % count3;
		LuckyMap lm = luckyMapRepository.findByRandom(m);

		Map<String,String> map = new HashMap<String, String>();
		map.put("name", name);
		map.put("star", "您今日的运势指数为" +luckyRank(i));
		map.put("ch", "运势最好的频道是"+ j +"频道哦！");
		map.put("table", luckyTable);
		map.put("gThing", luckyThing);
		map.put("bThing", luckyBdThing);
		map.put("map", lm.getMap());
		map.put("mapUrl", lm.getMapUrl());
		return Loadfont2.zbImage(map);
	}
	//指数
	private String luckyRank(int i) {
		if(i<5) {
			return "★☆☆☆☆";
		}else if(i<10){
			return "★★☆☆☆";
		}else if(i<45){
			return "★★★☆☆";
		}else if(i<80){
			return "★★★★☆";
		}else{
			return "★★★★★";
		}
	}
	//指数
	private String luckyRank2(int i) {
		if(i<5) {
			return "1";
		}else if(i<10){
			return "2";
		}else if(i<45){
			return "3";
		}else if(i<80){
			return "4";
		}else{
			return "5";
		}
	}

	public void msgAddMap(String raw_message, String imageCq) {
		LuckyMap luckyMap = new LuckyMap();
		luckyMap.setMap(raw_message);
		luckyMap.setMapUrl(imageCq);
		luckyMapRepository.save(luckyMap);
	}
	public String msgDeZb(String numb) {
		Date date = new Date(System.currentTimeMillis());
		//几号
		SimpleDateFormat format1 = new SimpleDateFormat("dd");
		//星期几
		SimpleDateFormat format2 = new SimpleDateFormat("u");
		String reply = new String();
		int i = Math.abs((numb+format1.format(date)+format2.format(date)).hashCode()%97);
		int n = Math.abs((format2.format(date)+format1.format(date)+numb).hashCode());

		String lucky = luckyRank2(i);
		//i n
		int count = luckyTableRepository.getCountByRank(lucky);
		n = n % count;
		LuckyTable lt = luckyTableRepository.findByRandom(lucky,n);
		reply = "签文："+lt.getLuckyTable()+"\n"
				+ "解签："+lt.getLuckyThing();
		return reply;
	}
}