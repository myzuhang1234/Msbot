package com.badeling.msbot.serviceImpl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.service.DrawService;
import com.badeling.msbot.service.MvpImageService;
import com.badeling.msbot.service.RankService;

@Component
public class RankServiceImpl implements RankService{
	@Autowired
	MvpImageService mvpImageService;
	
	@Autowired
	DrawService drawService;
	
	@Override
	public String getRank(String raw_message) {
		raw_message = raw_message.replace(MsbotConst.botName, "");
		raw_message = raw_message.replace("联盟", "");
		raw_message = raw_message.replace(" ", "");
		String url = "https://api.maplestory.gg/v1/public/character/gms/" + raw_message;
		
		try {
			StringBuilder json = new StringBuilder();
            URL urlObject = new URL(url);  
            URLConnection uc = urlObject.openConnection();
            uc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(),"UTF-8"));  
            String inputLine = null;  
            while ( (inputLine = in.readLine()) != null) {  
                json.append(inputLine);  
            }  
            in.close();
	        @SuppressWarnings("unchecked")
			Map<String,Object> mapler = (Map<String, Object>) JSONObject.parse(json.toString());
	        String drawRankImage = drawService.drawRankImage(mapler);
			return drawRankImage;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "查询角色不存在";
		} catch (Exception e) {
			e.printStackTrace();
			return "连接超时";
		}
	}
	
}
