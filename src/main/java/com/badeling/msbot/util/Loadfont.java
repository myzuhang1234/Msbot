package com.badeling.msbot.util;
import java.awt.Font;

import java.io.File;

import java.io.FileInputStream;

import com.badeling.msbot.config.MsbotConst;

public class Loadfont{
	public static void main(String[] args) {
		String msg = "abdbdbbddd";
		String[] split = msg.split("b");
		for(String temp : split) {
			System.out.println(temp+temp.equals(""));
		}
		
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
	
		Font font = Loadfont.loadFont(root+"/japarifont.ttf", 18f);//调用
	
		return font;//返回字体
	
	}

	public static java.awt.Font Font2(){
		String root= MsbotConst.imageUrl + "qd";

		Font font = Loadfont.loadFont(root+"/japarifont.ttf", 14f);

		return font;//返回字体

	}	

}