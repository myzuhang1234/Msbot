package com.badeling.msbot.serviceImpl;


import com.badeling.msbot.service.BaikeService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class BaikeServiceImpl implements BaikeService{
	
	
	public String getBaike(String name) {
		String url = "https://baike.baidu.com/search/word?word=" + name;
		
		try {
			Document doc = Jsoup.connect(url).get();
			String title = doc.title();
			String text = "";
			
			if(doc.select("div.lemmaWgt-subLemmaListTitle").text().length()>0) {
				Element ele = doc.select("div.para").first();
				Element ele2 = ele.getElementsByAttribute("href").first();
				url = "https://baike.baidu.com"+ele2.attr("href");
				doc = Jsoup.connect(url).get();
				title = doc.title();
			}
			
			if(title.contains("百度百科_全球最大中文百科全书")) {
				//搜索矫正
				Element para2 = doc.select("div.spell-correct").first();
				url = "https://baike.baidu.com/search/word?word=" + para2.select("a").text();
				doc = Jsoup.connect(url).get();
				Elements para = doc.select("div.para");
				for(Element temp : para) {
					text = text + temp.text();
				}
			}else {
				Elements para = doc.select("div.para");
				
				for(Element temp : para) {
					text = text + temp.text();
				}
			}
			String newText = "";
			if(text.length()>100) {
				String[] sentence = text.split("。");
				for(String temp : sentence) {
					if(newText.length()<100) {
						newText = newText+temp+"。";
					}
				}
			}else {
				newText = text;
			}
			if(newText.length()>170) {
				newText = newText.substring(0,170)+"...";
			}
			
			return newText;
		} catch (Exception e) {
			return null;
		}	
		
		
	}
	
	
	
	
}
