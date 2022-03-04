package com.badeling.msbot.util;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.controller.NewImageUtils;

public class Loadfont2{
	public static String testFont(String message) throws Exception {
		BufferedImage buffImg = new BufferedImage(480, (message.length()/24+1)*34, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0,0,6000,6000);//填充整个屏幕
        Font font = Font();
        g.setBackground(Color.WHITE);
        g.drawImage(buffImg, 32, 32, buffImg.getWidth(), buffImg.getHeight(), null);
        g.setFont(font);
        font = Loadfont.Font();
        g.setColor(new Color(0,0,0));
        for(int i=0;i<message.length();i=i+24){
        	if(i+24>message.length()) {
        		g.drawString(message.substring(i),32,32*((i+24)/24)-6);
        	}else {
        		g.drawString(message.substring(i, i+24),32,32*((i+24)/24)-6);
        	}
        }
        g.dispose();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String saveFilePath = MsbotConst.imageUrl + uuid +".png";
        ImageIO.write(buffImg, "jpg", new File(MsbotConst.imageUrl + uuid +".jpg"));
        generateWaterFile(buffImg, saveFilePath);
        return "[CQ:image,file=" + uuid +".jpg]";
	}
	
	public static String testFont2(String msg) throws Exception {
		String[] messageList = msg.split("\r");
		int count = 0;
		for(String temp : messageList) {
			count = count + temp.length()/24+1;
		}
		
		BufferedImage buffImg = new BufferedImage(480, count*32, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0,0,6000,6000);//填充整个屏幕
        Font font = Font();
        g.setBackground(Color.WHITE);
        g.drawImage(buffImg, 32, 32, buffImg.getWidth(), buffImg.getHeight(), null);
        g.setFont(font);
        font = Loadfont.Font();
        g.setColor(new Color(0,0,0));
        
        int wCount = 0;
        for(String message : messageList) {
        	if(message.equals("")) {
        		wCount++;
        		continue;
        	}
			for(int i=0;i<message.length();i=i+24){
	        	if(i+24>message.length()) {
	        		g.drawString(message.substring(i),32,wCount*32-6);
	        		wCount++;
	        	}else {
	        		g.drawString(message.substring(i, i+24),32,wCount*32-6);
	        		wCount++;
	        	}
	        }
		}
        
        
        g.dispose();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String saveFilePath = MsbotConst.imageUrl + uuid +".png";
        ImageIO.write(buffImg, "jpg", new File(MsbotConst.imageUrl + uuid +".jpg"));
        generateWaterFile(buffImg, saveFilePath);
        return "[CQ:image,file=" + uuid +".jpg]";
	}
	
	
	//第一个参数是外部字体名，第二个是字体大小
	public static Font loadFont(String fontFileName, float fontSize) {
		try {
			File file = new File(fontFileName);
			
			FileInputStream aixing = new FileInputStream(file);
			
			Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT, aixing);
			
			Font dynamicFontPt = dynamicFont.deriveFont(fontSize);
			
			aixing.close();
		
			return dynamicFontPt;
		}
	
		catch(Exception e){
		//异常处理
		e.printStackTrace();
	
		return new java.awt.Font("宋体", Font.PLAIN, 14);
		}
	}

	public static java.awt.Font Font(){
		String root= MsbotConst.imageUrl + "qd";//项目根目录路径
//		String root = "D:\\go-cqhttp\\data\\images\\qd";
		Font font = Loadfont2.loadFont(root+"/原版宋体.ttf", 18f);//调用
	
		return font;//返回字体
	}
	
	public static java.awt.Font Font(float i) {
		String root= MsbotConst.imageUrl + "qd";//项目根目录路径
//		String root = "D:\\go-cqhttp\\data\\images\\qd";
		Font font = Loadfont2.loadFont(root+"/原版宋体.ttf", i);//调用
		return font;//返回字体
	}

	public static java.awt.Font Font2(){
		String root= MsbotConst.imageUrl + "qd";
//		String root = "D:\\go-cqhttp\\data\\images\\qd";
		Font font = Loadfont2.loadFont(root+"/原版宋体.ttf", 14f);

		return font;//返回字体

	}	
	
	private static void generateWaterFile(BufferedImage buffImg, String savePath) {
        int temp = savePath.lastIndexOf(".") + 1;
        try {
            ImageIO.write(buffImg, savePath.substring(temp), new File(savePath));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

	public static String zbImage(Map<String, String> map) throws Exception{

		BufferedImage buffImg = new BufferedImage(500 , 500, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0,0,6000,6000);//填充整个屏幕
        Font font = Font();
        g.setBackground(Color.WHITE);
        g.drawImage(buffImg, 32, 32, buffImg.getWidth(), buffImg.getHeight(), null);
        g.setFont(font);
        font = Loadfont.Font();
        g.setColor(new Color(0,0,0));
        
        g.drawString(map.get("name"),32,24);
        g.drawString(map.get("star"),32,24*2);
        g.drawString(map.get("ch"),32,24*3);
        g.drawString(map.get("table"),32,24*4);
        g.drawString("//-------------------//",32,24*5);
        String[] split = map.get("gThing").split("\\n");
        
        g.drawString(split[0],32,24*6);
        g.drawString(split[1],32,24*7);
        g.drawString("//-------------------//",32,24*8);
        split = map.get("bThing").split("\\n");
        g.drawString(split[0],32,24*9);
        g.drawString(split[1],32,24*10);
        g.drawString("//-------------------//",32,24*11);
        g.drawString("今日最佳玄学地图是：",32,24*12);
        g.drawString(map.get("map"),32,24*13);
        String mapUrl = map.get("mapUrl");
        
        mapUrl = mapUrl.substring(mapUrl.indexOf("save"),mapUrl.length()-1);
        String mapFilePath = MsbotConst.imageUrl + mapUrl;
        mapFilePath = mapFilePath.replaceAll("\\\\", "/");
        System.out.println(mapFilePath);
//        500,341
        BufferedImage mapFile = ImageIO.read(new File(mapFilePath));
        mapFile = NewImageUtils.resizeBufferedImage(mapFile, 250, 170 , true);
        buffImg = NewImageUtils.watermark(buffImg, mapFile, 32 , 24*13+10, 1.0f);
        
        g.dispose();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String saveFilePath = MsbotConst.imageUrl + uuid +".png";
        ImageIO.write(buffImg, "jpg", new File(MsbotConst.imageUrl + uuid +".jpg"));
        generateWaterFile(buffImg, saveFilePath);
        return "[CQ:image,file=" + uuid +".jpg]";
	}

}