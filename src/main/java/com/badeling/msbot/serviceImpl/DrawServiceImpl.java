package com.badeling.msbot.serviceImpl;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.controller.NewImageUtils;
import com.badeling.msbot.domain.Photo;
import com.badeling.msbot.entity.Friends;
import com.badeling.msbot.repository.FriendsRepository;
import com.badeling.msbot.service.DrawService;
import com.badeling.msbot.util.Loadfont;
import com.badeling.msbot.util.Loadfont2;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;



@Component
public class DrawServiceImpl implements DrawService{
	
	@Autowired
	FriendsRepository friendsRepository;
	
	
	
	@Override
	public String startDraw() throws Exception {
		Random r = new Random();
        HashSet<Integer> rr = new HashSet<>();
		boolean has4Frends =false;
        //系统生成随机数
        while (rr.size() < 10) {
        	int i = r.nextInt(10000)+1;
        	if(i<200) {
        		has4Frends = true;
        	}
            rr.add(i);
        }
		//创建浮莲子
		String sourceFilePath;
		if(has4Frends) {
			sourceFilePath = MsbotConst.imageUrl + "kf3/" + "2.png";
		}else {
			sourceFilePath = MsbotConst.imageUrl + "kf3/" + "1.png";
		}
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String saveFilePath = MsbotConst.imageUrl + uuid +".png";
        BufferedImage sourceFile = ImageIO.read(new File(sourceFilePath));
        BufferedImage buffImg = null;
        //获取各个等级浮莲子的数量
        int f2 = Integer.parseInt(friendsRepository.getFriendsNum("2"));
        int f3 = Integer.parseInt(friendsRepository.getFriendsNum("3"));
        int f4 = Integer.parseInt(friendsRepository.getFriendsNum("4"));
        int p2 = Integer.parseInt(friendsRepository.getPhotoNum("2"));
        int p3 = Integer.parseInt(friendsRepository.getPhotoNum("3"));
        int p4 = Integer.parseInt(friendsRepository.getPhotoNum("4"));
		 // 构建叠加层
        int count =1;
        for(int random:rr){
        	
			Friends f;
			if(count<9) {
				if(random<200) {
					int random2 = r.nextInt(f4);
					f = friendsRepository.findFriendsByStar("4",random2);
				}else if(random<200+400) {
					int random2 = r.nextInt(f3);
					f = friendsRepository.findFriendsByStar("3",random2);
				}else if(random<200+400+2500) {
					int random2 = r.nextInt(f2);
					f = friendsRepository.findFriendsByStar("2",random2);
				}else if(random<200+400+2500+700) {
					int random2 = r.nextInt(p4);
					f = friendsRepository.findPhotoByStar("4",random2);
				}else if(random<200+400+2500+700+2000) {
					int random2 = r.nextInt(p3);
					f = friendsRepository.findPhotoByStar("3",random2);
				}else {
					int random2 = r.nextInt(p2);
					f = friendsRepository.findPhotoByStar("2",random2);
				}
			}else if(count==9){
				if(random<200) {
					int random2 = r.nextInt(f4);
					f = friendsRepository.findFriendsByStar("4",random2);
				}else if(random<200+400) {
					int random2 = r.nextInt(f3);
					f = friendsRepository.findFriendsByStar("3",random2);
				}else{
					int random2 = r.nextInt(f2);
					f = friendsRepository.findFriendsByStar("2",random2);
				}
			}else {
				if(random<200) {
					int random2 = r.nextInt(f4);
					f = friendsRepository.findFriendsByStar("4",random2);
				}else if(random<200+2900) {
					int random2 = r.nextInt(f3);
					f = friendsRepository.findFriendsByStar("3",random2);
				}else if(random<200+2900+700) {
					int random2 = r.nextInt(p4);
					f = friendsRepository.findPhotoByStar("4",random2);
				}else{
					int random2 = r.nextInt(p3);
					f = friendsRepository.findPhotoByStar("3",random2);
				}
			}
        	BufferedImage friends = createFriends(f.getId(),f.getAtr(),f.getStar(),f.getStarBlank());
       	 if(count<=5) {
       		 buffImg = NewImageUtils.watermark(sourceFile, friends, 141+210*(count-1) , 179, 1.0f);
       	 }else {
       		 buffImg = NewImageUtils.watermark(sourceFile, friends, 141+210*(count-6) , 369, 1.0f);
       	 }
   		 count = count + 1;
        }
//        buffImg = resizeBufferedImage(buffImg,buffImg.getWidth()/2,buffImg.getHeight()/2,true);
     // TYPE_INT_RGB:创建一个RBG图像，24位深度，成功将32位图转化成24位
        BufferedImage newBufferedImage = new BufferedImage(
        		buffImg.getWidth(), buffImg.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(buffImg, 0, 0,
            Color.WHITE, null);
        // write to jpeg file
        ImageIO.write(newBufferedImage, "jpg", new File(MsbotConst.imageUrl + uuid +".jpg"));
        generateWaterFile(newBufferedImage, saveFilePath);
		return "[CQ:image,file=" + uuid +".jpg]";
	}
	
	//生成十连图片
	private BufferedImage createFriends(String id,String atr,String Star,String Star_blank) throws Exception {
		String sourceFilePath = MsbotConst.imageUrl + "kf3/" + "charaicon_base.png";
		String waterFilePath;
		if(Star_blank.equals("0")) {
			waterFilePath = MsbotConst.imageUrl + "kf3/" + "icon_photo_" + id + ".png";
		}else {
			waterFilePath = MsbotConst.imageUrl + "kf3/" + "icon_chara_" + id + ".png";
		}
        // 构建叠加层
        BufferedImage buffImg = NewImageUtils.watermark(new File(sourceFilePath), new File(waterFilePath), 0, 0, 1.0f);
        //加属性
        waterFilePath = MsbotConst.imageUrl + "kf3/" + "icon_atr_"+ atr +".png";
        buffImg = NewImageUtils.watermark(buffImg, new File(waterFilePath), 5, 5, 1.0f);
        //加边框
        if(Star_blank.equals("0")) {
        	waterFilePath = MsbotConst.imageUrl + "kf3/" + "charaicon_frame.png";
        }else {
        	waterFilePath = MsbotConst.imageUrl + "kf3/" + "charaicon_frame_kiseki.png";
        }
        buffImg = NewImageUtils.watermark(buffImg, new File(waterFilePath), 0, 0, 1.0f);
        //加星星
	     BufferedImage starImg = makeStar(Integer.parseInt(Star),Integer.parseInt(Star_blank));
        buffImg = NewImageUtils.watermark(buffImg, starImg , 0, 135, 1.0f);
        // 输出水印图片
//        generateWaterFile(buffImg, saveFilePath);
		return buffImg;
	}


	//抓取浮莲子的数据
	@Override
	public String updateFriends() {
		String url = "https://sandstar.site/kf3_db/friends/";
		String returnMsg = "增加数据";
		String addMsg = "";
		int countFriends = 0;
		try {
			Document doc = Jsoup.connect(url).get();
			Elements elements = doc.select("div[class~=^friends-grid]");

			for(Element ele : elements) {
				String content = ele.toString();
				Friends friends = new Friends();
				int index1 = content.indexOf("alt");
				int count = 0;
				int starNum = 0;
				int starBlankNum = 0;
				while(true) {
					index1 = content.indexOf("alt",index1);
					if(index1 == -1) {
						break;
					}
					int index2 = content.indexOf("\"",index1+1);
					int index3 = content.indexOf("\"",index2+1);
					if(count==1) {
						friends.setId(content.substring(index2+1, index3));
					}else if(count==3) {
						friends.setAtr(content.substring(index2+1, index3));
					}else if(count>3) {
						String a = content.substring(index2+1, index3);
						if(a.contains("★")) {
							starNum = starNum + 1;
						}else if(a.contains("☆")) {
							starBlankNum = starBlankNum + 1;
						}else {
							
						}
						
					}
					count = count + 1;
					index1 = index3;
				}
				
				friends.setStar(starNum+"");
				friends.setStarBlank(starBlankNum+"");
				friends.setName(content.substring(content.indexOf("<b>")+3,content.indexOf("</b>")));
				if(!friends.getId().contains("★")&&!friends.getAtr().contains("★")) {
					Friends findById = friendsRepository.findFriendsById(friends.getId());
					if(findById==null) {
						friendsRepository.save(friends);
						countFriends = countFriends + 1;
						addMsg = addMsg + "\r\nID:" +  friends.getId()+ " rare:" + friends.getStar() + " name:" + friends.getName();
					}
				}
				
			}
		} catch (Exception e) {
			System.out.println("err");
		}
		returnMsg = returnMsg + countFriends + "条" + addMsg;
		return returnMsg;
	}
	
	//抓取照片的数据
	@Override
	public String updatePhoto() {
		StringBuilder json = new StringBuilder();
		String returnMsg = "增加数据";
		String addMsg = "";
		int count = 0;
        try {  
            URL urlObject = new URL("https://sandstar.site/static/kf3_db/photo/PHOTO_DATA_cn.json");  
            URLConnection uc = urlObject.openConnection();  
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(),"UTF-8"));  
            String inputLine = null;  
            while ( (inputLine = in.readLine()) != null) {  
                json.append(inputLine);  
            }  
            in.close();  
        } catch (Exception e) {  
            System.out.println(e);
        }
        String jsonData = json.toString().replace(" ", "");
        List<Photo> list = JSON.parseArray(jsonData, Photo.class);
        for(Photo a:list) {
        	Friends friends = new Friends();
        	if(a.getHead().equals("0")||a.getLevel().equals("0")) {
        		continue;
        	}
        	friends.setId(a.getId());
        	if(a.getType().equals("1")) {
        		friends.setAtr("parameter");
        	}else if(a.getType().equals("2")){
        		friends.setAtr("ability");
        	}else {
        		continue;
        	}
        	friends.setStar(a.getRarity());
        	friends.setStarBlank("0");
        	friends.setName(a.getName());
        	
			Friends findById = friendsRepository.findFriendsById(friends.getId());
			if(findById==null) {
				friendsRepository.save(friends);
				addMsg = addMsg + "\r\nID:" +  friends.getId()+ " rare:" + friends.getStar() + " name:" + friends.getName();
				count = count + 1;
			}
			
        }
        returnMsg = returnMsg + count + "条" + addMsg;
		return returnMsg;
		
	}
	//创建星星
	private static BufferedImage makeStar(int i, int j) throws Exception {
		String sourceFilePath = MsbotConst.imageUrl + "kf3/" + "star" + (i+j) +".png";
        String waterFilePath = MsbotConst.imageUrl + "kf3/" + "icon_rankstar.png";
        String waterFilePath_b = MsbotConst.imageUrl + "kf3/" + "icon_rankstar_blank.png";
        BufferedImage background = ImageIO.read(new File(sourceFilePath));
        BufferedImage star = ImageIO.read(new File(waterFilePath));
        BufferedImage star_b = ImageIO.read(new File(waterFilePath_b));
		//画的总星
		int count = 1;
		//画的实星
		int count_true = 1;
        // 构建叠加层
        BufferedImage buffImg = NewImageUtils.watermark(background, star , (162-27*(i+j))/2 + 27*count, 0, 1.0f);
//		BufferedImage buffImg = NewImageUtils.watermark(new File(sourceFilePath), new File(waterFilePath) , 0, 0, 1.0f);
        while(count<i+j) {
        	if(count_true<i) {
        		buffImg = NewImageUtils.watermark(buffImg, star, (162-27*(i+j))/2 + 27*count , 0, 1.0f);
        		count = count + 1;
        		count_true = count_true + 1;
        	}else {
        		buffImg = NewImageUtils.watermark(buffImg, star_b, (162-27*(i+j))/2 + 27*count , 0, 1.0f);
        		count = count + 1;
        	}
        }
	return buffImg;
}







	/**
	      * 
	      * @Title: 构造图片
	      * @Description: 生成水印并返回java.awt.image.BufferedImage
	      * @param file
	      *            源文件(图片)
	      * @param waterFile
	      *            水印文件(图片)
	      * @param x
	      *            距离右下角的X偏移量
	      * @param y
	      *            距离右下角的Y偏移量
	      * @param alpha
	      *            透明度, 选择值从0.0~1.0: 完全透明~完全不透明
	      * @return BufferedImage
	      * @throws IOException
	      */
	     public static BufferedImage watermark(File file, File waterFile, int x, int y, float alpha) throws Exception {
	         // 获取底图
	         BufferedImage buffImg = ImageIO.read(file);
	         // 获取层图
	         BufferedImage waterImg = ImageIO.read(waterFile);
	         // 创建Graphics2D对象，用在底图对象上绘图
	         Graphics2D g2d = buffImg.createGraphics();
	         int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
	         int waterImgHeight = waterImg.getHeight();// 获取层图的高度
	         // 在图形和图像中实现混合和透明效果
	         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
	         // 绘制
	         g2d.drawImage(waterImg, x, y, waterImgWidth, waterImgHeight, null);
	         g2d.dispose();// 释放图形上下文使用的系统资源
	         return buffImg;
	     }
	     
	     public static BufferedImage watermark(BufferedImage buffImg, BufferedImage waterImg, int x, int y, float alpha) throws Exception {
	         // 创建Graphics2D对象，用在底图对象上绘图
	         Graphics2D g2d = buffImg.createGraphics();
	         int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
	         int waterImgHeight = waterImg.getHeight();// 获取层图的高度
	         // 在图形和图像中实现混合和透明效果
	         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
	         // 绘制
	         g2d.drawImage(waterImg, x, y, waterImgWidth, waterImgHeight, null);
	         g2d.dispose();// 释放图形上下文使用的系统资源
	         return buffImg;
	     }
	     
	     public static BufferedImage watermark(BufferedImage buffImg, File waterFile, int x, int y, float alpha) throws Exception {
	         // 底图直接传入
	         // 获取层图
	         BufferedImage waterImg = ImageIO.read(waterFile);
	         // 创建Graphics2D对象，用在底图对象上绘图
	         Graphics2D g2d = buffImg.createGraphics();
	         int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
	         int waterImgHeight = waterImg.getHeight();// 获取层图的高度
	         // 在图形和图像中实现混合和透明效果
	         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
	         // 绘制
	         g2d.drawImage(waterImg, x, y, waterImgWidth, waterImgHeight, null);
	         g2d.dispose();// 释放图形上下文使用的系统资源
	         return buffImg;
	     }
	     /**
	      * 输出水印图片
	      * 
	      * @param buffImg
	      *            图像加水印之后的BufferedImage对象
	      * @param savePath
	      *            图像加水印之后的保存路径
	      */
	     private static void generateWaterFile(BufferedImage buffImg, String savePath) {
	         int temp = savePath.lastIndexOf(".") + 1;
	         try {
	             ImageIO.write(buffImg, savePath.substring(temp), new File(savePath));
	         } catch (Exception e1) {
	             e1.printStackTrace();
	         }
	     }
 
 
 //原定的星星代码

//private static BufferedImage makeStar() throws IOException {
//	int width = 27*1;
//	int height = 27;
//	String star = CoolQconst.imageUrl + "kf3/" + "icon_rankstar.png";
//    String starb = CoolQconst.imageUrl + "kf3/" + "icon_rankstar_blank.png";
//    String saveFilePath = CoolQconst.imageUrl + "kf3/" + "112star.png";
//    BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//    BufferedImage starImg = ImageIO.read(new File(star));
//    BufferedImage starImgb = ImageIO.read(new File(starb));
//     // 创建Graphics2D对象，用在底图对象上绘图
//     Graphics2D g2d = buffImg.createGraphics();
//     int waterImgWidth = starImg.getWidth();// 获取层图的宽度
//     int waterImgHeight = starImg.getHeight();// 获取层图的高度
//     //透明
//     buffImg = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
//     g2d.dispose();
//     g2d = buffImg.createGraphics();  
//     // 在图形和图像中实现混合和透明效果
//     g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
//     // 绘制
//     g2d.drawImage(starImg, 0, 0, waterImgWidth, waterImgHeight, null);
//     g2d.dispose();// 释放图形上下文使用的系统资源
//     return buffImg;
//}

	 	private static BufferedImage resizeBufferedImage(BufferedImage source, int targetW, int targetH, boolean flag) {
	 		int type = source.getType();
	 		BufferedImage target = null;
	 		double sx = (double) targetW / source.getWidth();
	 		double sy = (double) targetH / source.getHeight();
	 		if (flag && sx > sy) {
	 			sx = sy;
	 			targetW = (int) (sx * source.getWidth());
	 		} else if(flag && sx <= sy){
	 			sy = sx;
	 			targetH = (int) (sy * source.getHeight());
	 		}
	 		if (type == BufferedImage.TYPE_CUSTOM) { // handmade
	 			ColorModel cm = source.getColorModel();
	 			WritableRaster raster = cm.createCompatibleWritableRaster(targetW, targetH);
	 			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
	 			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
	 		} else {
	 			target = new BufferedImage(targetW, targetH, type);
	 		}
	 		Graphics2D g = target.createGraphics();
	 		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	 		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
	 		g.dispose();
	 		return target;
	 	}
	 	//小狐狸生成
		@Override
		public String kemomimiDraw() throws Exception {
			Random r = new Random();
	        HashSet<Integer> rr = new HashSet<>();
			boolean has5Frends =false;
	        //系统生成随机数
	        while (rr.size() < 10) {
	        	int i = r.nextInt(10000)+1;
	        	if(i<300) {
	        		has5Frends = true;
	        	}
	            rr.add(i);
	        }
	        /**
	         * i为编号
			star 5 <300
			star 4 <2200
			star 3 <4800
			star 2 <10000
			*/
	        if(!has5Frends) {
	        	int i = r.nextInt(10000)+1;
	        	if(i<500) {
	        		rr.clear();
	        		while (rr.size() < 10) {
	    	        	int j = r.nextInt(5000)+4801;
	    	            rr.add(j);
	    	        }
	        	}else if(i<800) {
	        		rr.clear();
	        		while (rr.size() < 10) {
	    	        	int j = r.nextInt(2500)+2201;
	    	            rr.add(j);
	    	        }
	        	}else if(i<1000) {
	        		rr.clear();
	        		while (rr.size() < 10) {
	    	        	int j = r.nextInt(1800)+301;
	    	            rr.add(j);
	    	        }
	        	}
	        }
	        
			//创建浮莲子
			String sourceFilePath;
			if(has5Frends) {
				sourceFilePath = MsbotConst.imageUrl + "kf3/" + "2.png";
			}else {
				sourceFilePath = MsbotConst.imageUrl + "kf3/" + "1.png";
			}
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
	        String saveFilePath = MsbotConst.imageUrl + uuid +".png";
	        BufferedImage sourceFile = ImageIO.read(new File(sourceFilePath));
	        BufferedImage buffImg = null;
	        //获取各个等级浮莲子的数量
//	        int f2 = 10;
//	        int f3 = 9;
//	        int f4 = 9;
//	        int f5 = 1;
	        int f2 = 60;
	        int f3 = 26;
	        int f4 = 19;
	        int f5 = 3;
	        
			 // 构建叠加层
	        int count =1;
	        for(int random:rr){
				String f;
				int star;
				
				if(random<300) {
					int random2 = r.nextInt(f5)+1;
					star = 5;
					f = "5-"+random2;
				}else if(random<300+1900) {
					int random2 = r.nextInt(f4)+1;
					star = 4;
					f = "4-"+random2;
				}else if(random<200+1900+2700) {
					int random2 = r.nextInt(f3)+1;
					star = 3;
					f = "3-"+random2;
				}else{
					int random2 = r.nextInt(f2)+1;
					star = 2;
					f = "2-"+random2;
					}
				BufferedImage friends = createKomomimi(f,star);
	       	 if(count<=5) {
	       		 buffImg = NewImageUtils.watermark(sourceFile, friends, 141+210*(count-1) , 179, 1.0f);
	       	 }else {
	       		 buffImg = NewImageUtils.watermark(sourceFile, friends, 141+210*(count-6) , 369, 1.0f);
	       	 }
	   		 count = count + 1;
	        }
//	        buffImg = resizeBufferedImage(buffImg,buffImg.getWidth()/2,buffImg.getHeight()/2,true);
	     // TYPE_INT_RGB:创建一个RBG图像，24位深度，成功将32位图转化成24位
	        BufferedImage newBufferedImage = new BufferedImage(
	        		buffImg.getWidth(), buffImg.getHeight(),
	                BufferedImage.TYPE_INT_RGB);
	        newBufferedImage.createGraphics().drawImage(buffImg, 0, 0,
	            Color.WHITE, null);
	        // write to jpeg file
	        ImageIO.write(newBufferedImage, "jpg", new File(MsbotConst.imageUrl + uuid +".jpg"));
	        generateWaterFile(newBufferedImage, saveFilePath);
			return "[CQ:image,file=" + uuid +".jpg]";
		}
		//生成十连图片
		private BufferedImage createKomomimi(String id,int star) throws Exception {
			String sourceFilePath = MsbotConst.imageUrl + "kf3/" + "charaicon_base.png";
			String waterFilePath;
			waterFilePath = MsbotConst.imageUrl + "kf3/" + id + ".jpg";
	        // 构建叠加层
	        BufferedImage buffImg = NewImageUtils.watermark(new File(sourceFilePath), new File(waterFilePath), 0, 0, 1.0f);
	        //加边框
	        waterFilePath = MsbotConst.imageUrl + "kf3/" + "charaicon_frame_kiseki.png";
	        buffImg = NewImageUtils.watermark(buffImg, new File(waterFilePath), 0, 0, 1.0f);
	        //加星星
	        waterFilePath = MsbotConst.imageUrl + "kf3/" + "star" + star + ".png";
		    BufferedImage starImg = ImageIO.read(new File(waterFilePath));
	        buffImg = NewImageUtils.watermark(buffImg, starImg , 0, 135, 1.0f);
	        // 输出水印图片
//	        generateWaterFile(buffImg, saveFilePath);
			return buffImg;
		}

		@Override
		public String throwSomeone(String headImg) throws Exception {
			String sourceFilePath = MsbotConst.imageUrl + "img/throw.jpg";
			String waterFilePath = MsbotConst.imageUrl + headImg;
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
	        String saveFilePath = MsbotConst.imageUrl + uuid +".png";
	        BufferedImage sourceFile = ImageIO.read(new File(sourceFilePath));
	        BufferedImage buffImg = ImageIO.read(new File(waterFilePath));
	        buffImg = NewImageUtils.resizeBufferedImage(buffImg,148,148,true);
	        buffImg = NewImageUtils.convertCircular(buffImg);
	        buffImg = NewImageUtils.rotateImage(buffImg,300);
			buffImg = NewImageUtils.watermark(sourceFile, buffImg, 13, 175, 1.0f);
			BufferedImage newBufferedImage = new BufferedImage(
	        		buffImg.getWidth(), buffImg.getHeight(),
	                BufferedImage.TYPE_INT_RGB);
	        newBufferedImage.createGraphics().drawImage(buffImg, 0, 0,
	            Color.WHITE, null);
	        // write to jpeg file
	        ImageIO.write(newBufferedImage, "jpg", new File(MsbotConst.imageUrl + uuid +".jpg"));
	        generateWaterFile(newBufferedImage, saveFilePath);
			return "[CQ:image,file=" + uuid +".jpg]";
		}

		@Override
		public String pouchSomeone(String headImg) throws Exception {
			String sourceFilePath = MsbotConst.imageUrl + "img/punch.jpg";
			String waterFilePath = MsbotConst.imageUrl + headImg;
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
	        String saveFilePath = MsbotConst.imageUrl + uuid +".png";
	        BufferedImage sourceFile = ImageIO.read(new File(sourceFilePath));
	        BufferedImage buffImg = ImageIO.read(new File(waterFilePath));
	        buffImg = NewImageUtils.resizeBufferedImage(buffImg,148,148,true);
	        buffImg = NewImageUtils.convertCircular(buffImg);
	        buffImg = NewImageUtils.rotateImage(buffImg,320);
	        sourceFile = NewImageUtils.watermark(sourceFile, buffImg, 50,300, 1.0f);
			//第二张
	        buffImg = NewImageUtils.resizeBufferedImage(buffImg,148,148,true);
	        buffImg = NewImageUtils.convertCircular(buffImg);
	        buffImg = NewImageUtils.rotateImage(buffImg,60);
			buffImg = NewImageUtils.watermark(sourceFile, buffImg, 280, 270, 1.0f);
			BufferedImage newBufferedImage = new BufferedImage(
	        		buffImg.getWidth(), buffImg.getHeight(),
	                BufferedImage.TYPE_INT_RGB);
	        newBufferedImage.createGraphics().drawImage(buffImg, 0, 0,
	            Color.WHITE, null);
	        // write to jpeg file
	        ImageIO.write(newBufferedImage, "jpg", new File(MsbotConst.imageUrl + uuid +".jpg"));
	        generateWaterFile(newBufferedImage, saveFilePath);
			return "[CQ:image,file=" + uuid +".jpg]";
		}

		@Override
		public String testFont(String message) throws Exception {
			BufferedImage buffImg = new BufferedImage(480, (message.length()/24+1)*34, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = buffImg.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0,0,6000,6000);//填充整个屏幕
	        Font font = Loadfont.Font();
	        g.setBackground(Color.WHITE);
	        g.drawImage(buffImg, 32, 32, buffImg.getWidth(), buffImg.getHeight(), null);
	        g.setFont(font);
	        font = Loadfont.Font();
	        g.setColor(new Color(237,120,1));
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
		
		@Override
		public String ignImage(String ignDate) throws Exception {
			ignDate = ignDate.replaceAll("（", "(").replaceAll("）", ")")
					.replaceAll("Infinity", "∞").replaceAll("NaN", "0");
			String[] split = ignDate.split("\\r\\n");
			
			
			String sourceFilePath = MsbotConst.imageUrl + "img/ign_background.jpg";
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			BufferedImage sourceFile = ImageIO.read(new File(sourceFilePath));
			Graphics2D g = sourceFile.createGraphics();
	        Font font = Loadfont2.Font(16);
	        g.setFont(font);
	        g.setColor(new Color(230,230,230));
	        
	        int x1 = 65;
	        int x2 = 382;
	        int x3 = 687;
	        int y1 = 70;
	        int y2 = 288;
	        
	        g.drawString("基本数据",x1,y1);
	        g.drawString("超高防对比", x1, y2);
	        g.drawString("高防对比",x2,y2);
	        g.drawString("中防对比", x3, y2);
	        
	        font = Loadfont2.Font(14);
	        g.setFont(font);
	        
			g.drawString(split[0], x1, y1+31);
			g.drawString(split[1], x1, y1+31*2);
			g.drawString(split[2], x1, y1+31*3);
			g.drawString(split[3], x1, y1+31*4);
			
			g.drawString(split[5], x1, y2+31);
			g.drawString(split[6], x1, y2+31*2);
			g.drawString(split[7], x1, y2+31*3);
			g.drawString(split[8], x1, y2+31*4);
			
			g.drawString(split[10], x2, y2+31);
			g.drawString(split[11], x2, y2+31*2);
			g.drawString(split[12], x2, y2+31*3);
			g.drawString(split[13], x2, y2+31*4);
			
			g.drawString(split[15], x3, y2+31);
			g.drawString(split[16], x3, y2+31*2);
			g.drawString(split[17], x3, y2+31*3);
			g.drawString(split[18], x3, y2+31*4);
			
	        String saveFilePath = MsbotConst.imageUrl + uuid +".jpg";
//			String saveFilePath = "C:\\Users\\Admin\\Desktop\\result.jpg";
	        
	        // write to jpeg file
//	        ImageIO.write(newBufferedImage, "jpg", new File(CoolQconst.imageUrl + uuid +".jpg"));
	        ImageIO.write(sourceFile, "jpg", new File(saveFilePath));
	        generateWaterFile(sourceFile, saveFilePath);
			return "[CQ:image,file=" + uuid +".jpg]";
		}


}
