package com.badeling.msbot.serviceImpl;

import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.controller.NewImageUtils;
import com.badeling.msbot.entity.MonvTime;
import com.badeling.msbot.entity.Msg;
import com.badeling.msbot.repository.MonvTimeRepository;
import com.badeling.msbot.service.DrawService;
import com.badeling.msbot.service.MvpImageService;
import com.badeling.msbot.util.Loadfont;
import com.badeling.msbot.util.Loadfont2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.codec.digest.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;


@Component
public class DrawServiceImpl implements DrawService{

	@Autowired
	MvpImageService mvpImageService;

	@Autowired
	private MonvTimeRepository monvTimeRepository;

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

	@SuppressWarnings("unused")
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

	@Override
	public String kemomimiDraw2(List <Msg> pick) throws Exception {
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

		// 构建叠加层
		int count =1;
		for(int random:rr){

			BufferedImage friends = createKomomimi2(count,pick);

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

	private BufferedImage createKomomimi2(int count,List<Msg> pick) throws Exception {
		String sourceFilePath = MsbotConst.imageUrl + "kf3/" + "charaicon_base.png";
		String waterFilePath;

		String pic = pick.get(count).getAnswer();
		String path = pic.substring(pic.indexOf("file=")+5,pic.indexOf("]"));

		waterFilePath = MsbotConst.imageUrl + path;
		String md5 = DigestUtils.md5Hex(new FileInputStream(waterFilePath));

		// 构建叠加层
		BufferedImage buffImg = NewImageUtils.watermark(new File(sourceFilePath), new File(waterFilePath), 0, 0, 1.0f);

		int num = Integer.parseInt(md5.substring(md5.length()-4),16);
		int star =  num % 4 + 2;
		//System.out.println("star:"+star);

		//加边框
		if(star==5){
			waterFilePath = MsbotConst.imageUrl + "kf3/" + "charaicon_frame_kiseki.png";
		}
		else{
			waterFilePath = MsbotConst.imageUrl + "kf3/" + "charaicon_frame.png";
		}
		buffImg = NewImageUtils.watermark(buffImg, new File(waterFilePath), 0, 0, 1.0f);

		String saveFilePath = MsbotConst.imageUrl + "save/20230620_"+count+".png";
		generateWaterFile(buffImg, saveFilePath);


		//加星星
		waterFilePath = MsbotConst.imageUrl + "kf3/" + "star" + star + ".png";

		BufferedImage starImg = ImageIO.read(new File(waterFilePath));
		buffImg = NewImageUtils.watermark(buffImg, starImg , 0, 135, 1.0f);
		// 输出水印图片
		//generateWaterFile(buffImg, saveFilePath);

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
		g.dispose();
		return "[CQ:image,file=" + uuid +".jpg]";
	}

	@Override
	public String startDrawMs() throws Exception {
		Random r = new Random();
		HashSet<Integer> randomList = new HashSet<>();
		while (randomList.size() < 10) {
			int i = r.nextInt(10000)+1;
			randomList.add(i);
		}

		Calendar cal = Calendar.getInstance();
		int w=cal.get(Calendar.DAY_OF_WEEK)-1;

		String sourceFilePath = MsbotConst.imageUrl + "bfb/" + "mn.jpg";

		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		String saveFilePath = MsbotConst.imageUrl + uuid +".png";
		BufferedImage sourceFile = ImageIO.read(new File(sourceFilePath));

		BufferedImage buffImg1 = ImageIO.read(new File(MsbotConst.imageUrl + "bfb/a" + w + "1.png"));
		BufferedImage buffImg2 = ImageIO.read(new File(MsbotConst.imageUrl + "bfb/a" + w + "2.png"));
		BufferedImage buffImg3 = ImageIO.read(new File(MsbotConst.imageUrl + "bfb/a" + w + "3.png"));
		BufferedImage buffImg4 = ImageIO.read(new File(MsbotConst.imageUrl + "bfb/a" + w + "4.png"));
		BufferedImage buffImg5 = ImageIO.read(new File(MsbotConst.imageUrl + "bfb/a" + w + "5.png"));
		List<BufferedImage> list = new ArrayList<>();
		for(Integer random : randomList) {
			if(random<7550) {
				list.add(NewImageUtils.resizeBufferedImage(buffImg1,90,90,false));
			}else if(random<7550+2124){
				list.add(NewImageUtils.resizeBufferedImage(buffImg2,90,90,false));
			}else if(random<7550+2124+300) {
				list.add(NewImageUtils.resizeBufferedImage(buffImg3,90,90,false));
			}else if(random<7550+2124+300+20) {
				list.add(NewImageUtils.resizeBufferedImage(buffImg4,90,90,false));
			}else {
				list.add(NewImageUtils.resizeBufferedImage(buffImg5,90,90,false));
			}
		}

		sourceFile = watermark(sourceFile, list.get(0) , 375, 410, 1.0f);
		sourceFile = watermark(sourceFile, list.get(1) , 620, 410, 1.0f);
		sourceFile = watermark(sourceFile, list.get(2) , 870, 410, 1.0f);
		sourceFile = watermark(sourceFile, list.get(3) , 250, 610, 1.0f);
		sourceFile = watermark(sourceFile, list.get(4) , 500, 610, 1.0f);
		sourceFile = watermark(sourceFile, list.get(5) , 745, 610, 1.0f);
		sourceFile = watermark(sourceFile, list.get(6) , 990, 610, 1.0f);
		sourceFile = watermark(sourceFile, list.get(7) , 375, 805, 1.0f);
		sourceFile = watermark(sourceFile, list.get(8) , 620, 805, 1.0f);
		sourceFile = watermark(sourceFile, list.get(9) , 870, 805, 1.0f);

//	        buffImg = resizeBufferedImage(buffImg,buffImg.getWidth()/2,buffImg.getHeight()/2,true);
		// TYPE_INT_RGB:创建一个RBG图像，24位深度，成功将32位图转化成24位
		BufferedImage newBufferedImage = new BufferedImage(
				sourceFile.getWidth(), sourceFile.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		newBufferedImage.createGraphics().drawImage(sourceFile, 0, 0,
				Color.WHITE, null);
		// write to jpeg file
//	        ImageIO.write(newBufferedImage, "jpg", new File(MsbotConst.imageUrl + "bfb/mn1.jpg"));
		ImageIO.write(newBufferedImage, "jpg", new File(MsbotConst.imageUrl +uuid +".jpg"));
		generateWaterFile(newBufferedImage, saveFilePath);
		return "[CQ:image,file=" + uuid +".jpg]";
	}

	@Override
	public String startDrawMs(MonvTime monvTime) throws Exception {
		int prize_1=0;
		int prize_2=0;
		int prize_3=0;
		int prize_4=0;
		int prize_5=0;
		Random r = new Random();
		HashSet<Integer> randomList = new HashSet<>();
		while (randomList.size() < 10) {
			int i = r.nextInt(10000)+1;
			randomList.add(i);
		}

		Calendar cal = Calendar.getInstance();
		int w=cal.get(Calendar.DAY_OF_WEEK)-1;

		String sourceFilePath = MsbotConst.imageUrl + "bfb/" + "mn.jpg";

		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		String saveFilePath = MsbotConst.imageUrl + uuid +".png";
		BufferedImage sourceFile = ImageIO.read(new File(sourceFilePath));

		BufferedImage buffImg1 = ImageIO.read(new File(MsbotConst.imageUrl + "bfb/a" + w + "1.png"));
		BufferedImage buffImg2 = ImageIO.read(new File(MsbotConst.imageUrl + "bfb/a" + w + "2.png"));
		BufferedImage buffImg3 = ImageIO.read(new File(MsbotConst.imageUrl + "bfb/a" + w + "3.png"));
		BufferedImage buffImg4 = ImageIO.read(new File(MsbotConst.imageUrl + "bfb/a" + w + "4.png"));
		BufferedImage buffImg5 = ImageIO.read(new File(MsbotConst.imageUrl + "bfb/a" + w + "5.png"));
		List<BufferedImage> list = new ArrayList<>();
		for(Integer random : randomList) {
			if(random<7550) {
				list.add(NewImageUtils.resizeBufferedImage(buffImg1,90,90,false));
				prize_1=prize_1+1;
			}else if(random<7550+2124){
				list.add(NewImageUtils.resizeBufferedImage(buffImg2,90,90,false));
				prize_2=prize_2+1;
			}else if(random<7550+2124+300) {
				list.add(NewImageUtils.resizeBufferedImage(buffImg3,90,90,false));
				prize_3=prize_3+1;
			}else if(random<7550+2124+300+20) {
				list.add(NewImageUtils.resizeBufferedImage(buffImg4,90,90,false));
				prize_4=prize_4+1;
			}else {
				list.add(NewImageUtils.resizeBufferedImage(buffImg5,90,90,false));
				prize_5=prize_5+1;
			}
		}
		System.out.println(monvTime.getId());
		System.out.println(monvTime.getPrize_1());
		System.out.println(monvTime.getPrize_2());
		System.out.println(monvTime.getPrize_3());
		System.out.println(monvTime.getPrize_4());
		System.out.println(monvTime.getPrize_5());

		monvTimeRepository.modifyUpdatePrize(
				monvTime.getId(),
				monvTime.getPrize_1()+prize_1,
				monvTime.getPrize_2()+prize_2,
				monvTime.getPrize_3()+prize_3,
				monvTime.getPrize_4()+prize_4,
				monvTime.getPrize_5()+prize_5
		);

		sourceFile = watermark(sourceFile, list.get(0) , 375, 410, 1.0f);
		sourceFile = watermark(sourceFile, list.get(1) , 620, 410, 1.0f);
		sourceFile = watermark(sourceFile, list.get(2) , 870, 410, 1.0f);
		sourceFile = watermark(sourceFile, list.get(3) , 250, 610, 1.0f);
		sourceFile = watermark(sourceFile, list.get(4) , 500, 610, 1.0f);
		sourceFile = watermark(sourceFile, list.get(5) , 745, 610, 1.0f);
		sourceFile = watermark(sourceFile, list.get(6) , 990, 610, 1.0f);
		sourceFile = watermark(sourceFile, list.get(7) , 375, 805, 1.0f);
		sourceFile = watermark(sourceFile, list.get(8) , 620, 805, 1.0f);
		sourceFile = watermark(sourceFile, list.get(9) , 870, 805, 1.0f);

//	        buffImg = resizeBufferedImage(buffImg,buffImg.getWidth()/2,buffImg.getHeight()/2,true);
		// TYPE_INT_RGB:创建一个RBG图像，24位深度，成功将32位图转化成24位
		BufferedImage newBufferedImage = new BufferedImage(
				sourceFile.getWidth(), sourceFile.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		newBufferedImage.createGraphics().drawImage(sourceFile, 0, 0,
				Color.WHITE, null);
		// write to jpeg file
//	        ImageIO.write(newBufferedImage, "jpg", new File(MsbotConst.imageUrl + "bfb/mn1.jpg"));
		ImageIO.write(newBufferedImage, "jpg", new File(MsbotConst.imageUrl +uuid +".jpg"));
		generateWaterFile(newBufferedImage, saveFilePath);
		return "[CQ:image,file=" + uuid +".jpg]";
	}

	@Override
	public String zbImage(String[] msg) throws Exception {
		String a1 = msg[0];
		String a2 = msg[1];
		String a3 = msg[2];
		String a4 = msg[3];
		String a5 = msg[4];
		String a6 = msg[5];
		String a7 = msg[6];
		String a8 = msg[7];
		String a9 = msg[8];
		String imageUrl = msg[9];
		String roleUrl = msg[10];

		String sourceFilePath = MsbotConst.imageUrl + "zb/background.png";

		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		String saveFilePath = MsbotConst.imageUrl + uuid +".png";
		BufferedImage sourceFile = ImageIO.read(new File(sourceFilePath));

		//紫色条形框
		BufferedImage buffImg1 = ImageIO.read(new File(MsbotConst.imageUrl + "zb/a1.png"));

		//人物
		BufferedImage roleImg = ImageIO.read(new File(roleUrl));
		roleImg = NewImageUtils.resizeBufferedImage(roleImg,188,210,false);
		sourceFile = watermark(sourceFile, roleImg , 550, 30, 1.0f);

		//地图
		BufferedImage mapImg = ImageIO.read(new File(imageUrl));
		mapImg = NewImageUtils.resizeBufferedImage(mapImg,370,180,false);
		mapImg = NewImageUtils.setRadius(mapImg,30,0,0);
		sourceFile = watermark(sourceFile, buffImg1 , 450, 230, 1.0f);
		sourceFile = watermark(sourceFile, mapImg , 460, 350, 1.0f);


		Graphics2D g = sourceFile.createGraphics();
		Font font = Loadfont2.Font(22);
		g.setFont(font);
		g.setColor(new Color(230,230,230));
		//指数
		g.drawString(a1,65,65);
		//频道
		g.drawString(a2,65,65+65);
		//事件

		if(a3.length()>16) {
			g.drawString(a3.substring(0,16),65,65+65+35);
			g.drawString(a3.substring(16),65,65+65+35+35);
		}else {
			g.drawString(a3,65,65+65+35);
		}
		//宜
		g.drawString(a4,65,65+65+30+110);
		//宜事件
		if(a5.length()>16) {
			g.drawString(a5.substring(0,16),65,65+65+30+110+35);
			g.drawString(a5.substring(16),65,65+65+30+110+35+35);
		}else {
			g.drawString(a5,65,65+65+30+110+35);
		}


		//忌
		g.drawString(a6,65,65+65+30+110+30+130);
		//忌事件
		if(a7.length()>16) {
			g.drawString(a7.substring(0,16),65,65+65+30+110+30+130+35);
			g.drawString(a7.substring(16),65,65+65+30+110+30+130+35+35);
		}else {
			g.drawString(a7,65,65+65+30+110+30+130+35);
		}


		//地图
		g.drawString(a8,480,65+65+30+110);
		//地图名
		g.drawString(a9,480,65+65+30+110+35);

		g.dispose();

//	        buffImg = resizeBufferedImage(buffImg,buffImg.getWidth()/2,buffImg.getHeight()/2,true);
		// TYPE_INT_RGB:创建一个RBG图像，24位深度，成功将32位图转化成24位
		BufferedImage newBufferedImage = new BufferedImage(
				sourceFile.getWidth(), sourceFile.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		newBufferedImage.createGraphics().drawImage(sourceFile, 0, 0,
				Color.WHITE, null);
		// write to jpeg file
//	        ImageIO.write(newBufferedImage, "jpg", new File(MsbotConst.imageUrl + "bfb/mn1.jpg"));
		ImageIO.write(newBufferedImage, "jpg", new File(MsbotConst.imageUrl +uuid +".jpg"));
		generateWaterFile(newBufferedImage, saveFilePath);
		return "[CQ:image,file=" + uuid +".jpg]";
	}

	@Override
	public String drawRankImage(Map<String, Object> mapler) throws Exception {
		//判断是否有经验数据
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> gd = (List<Map<String, Object>>) mapler.get("GraphData");
		String sourceFilePath = MsbotConst.imageUrl + "lm/background2.jpg";
		if(gd!=null&&gd.size()>1) {
			sourceFilePath = MsbotConst.imageUrl + "lm/background1.jpg";
		}
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		String saveFilePath = MsbotConst.imageUrl + uuid +".png";
		BufferedImage sourceFile = ImageIO.read(new File(sourceFilePath));

		String imageUrl = (String) mapler.get("CharacterImageURL");
		List<String> images = new ArrayList<>();
		images.add(imageUrl);
		BufferedImage imageImg = null;
		if(imageUrl!=null) {
			try {
				String imageName = mvpImageService.saveTempImage(imageUrl);
				imageImg = ImageIO.read(new File(MsbotConst.imageUrl + imageName));
//	        		imageImg = ImageIO.read(new File("D:\\go-cqhttp\\data\\images\\lm\\1.png"));
				imageImg = NewImageUtils.mirrorImage(imageImg);
				imageImg = NewImageUtils.resizeBufferedImage(imageImg, 200, 200, true);
				sourceFile = watermark(sourceFile, imageImg , 75, 25, 1.0f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Graphics2D g = sourceFile.createGraphics();
		Font font = Loadfont2.Font(22);
		g.setFont(font);
		g.setColor(new Color(0,0,0));

		int startX = 65;
		int startY = 265;
//	        int intervalX =
		int interval = 30;

		g.drawString("角色：" + mapler.get("Name"),startX,startY);
		g.drawString("服务器：" + mapler.get("Server"),startX,startY+interval*2);
		g.drawString("等级：" + mapler.get("Level") + " - " + mapler.get("EXPPercent") + "%",startX,startY+interval*4);
		g.drawString("(排名" + mapler.get("ServerRank") + ")",startX,startY+interval*5);
		g.drawString("职业：" + mapler.get("Class"),startX,startY+interval*6);
		g.drawString("(排名" + mapler.get("ServerClassRanking") +")",startX,startY+interval*7);


		if(mapler.get("LegionCoinsPerDay")==null) {
			g.drawString("非联盟最高角色，无法查",startX,startY+interval*9);
			g.drawString("询联盟信息",startX,startY+interval*10);
		}else {
			g.drawString("联盟等级：" + mapler.get("LegionLevel"),startX,startY+interval*8);
			g.drawString("(排名" + mapler.get("LegionRank") +")",startX,startY+interval*9);
			g.drawString("联盟战斗力：" + mapler.get("LegionPower"),startX,startY+interval*10);
			g.drawString("(每天" + mapler.get("LegionCoinsPerDay") +"币)",startX,startY+interval*11);
		}


		startX = 425;
		startY = 265;

		if(gd!=null) {
			Long expYesterday = Long.parseLong("0");
			Long expLastWeek = Long.parseLong("0");
			Long totalOverallExp = Long.parseLong("0");
			Long maxExp = Long.parseLong("0");
			int count = 0;
			Collections.reverse(gd);
			//角色图
			try {
				for(Map<String,Object> temp : gd) {
					String avatar = temp.get("AvatarURL")+"";
					if(!images.contains(avatar)) {
						images.add(avatar);
					}
				}
				if(images.size()==1) {
					imageImg = NewImageUtils.mirrorImage(imageImg);
					sourceFile = watermark(sourceFile, imageImg , 425, 25, 1.0f);
				}else {
					Random r = new Random();
					int a = r.nextInt(images.size()-1)+1;
					imageUrl = images.get(a);
					try {
						String imageName = mvpImageService.saveTempImage(imageUrl);
						imageImg = ImageIO.read(new File(MsbotConst.imageUrl + imageName));
					} catch (Exception e) {
						imageImg = NewImageUtils.mirrorImage(imageImg);
					}
//						imageImg = ImageIO.read(new File("D:\\go-cqhttp\\data\\images\\lm\\1.png"));
					imageImg = NewImageUtils.resizeBufferedImage(imageImg, 200, 200, true);
					sourceFile = watermark(sourceFile, imageImg , 425, 25, 1.0f);
				}
			} catch (Exception e) {
				System.out.println("角色2图加载失败");
			}

			//经验
			for(int i=0;i<gd.size()-1;i++) {
				if(count<7) {
					Long exp = Long.parseLong(gd.get(i+1).get("EXPDifference")+"");
					Long exp2 = Long.parseLong("0");

					if(i==0) {
						expYesterday = exp;
						totalOverallExp = Long.parseLong(gd.get(0).get("TotalOverallEXP")+"");
						System.out.println("当前经验"+totalOverallExp);
					}
					if(maxExp<exp) {
						maxExp = exp;
					}
					expLastWeek = expLastWeek + exp;

					String expString = exp+"";
					if(exp>10000) {
						exp2 = exp%10000/100;
						exp = exp/10000;
						expString = exp + "."+exp2+"w";
						if(exp>10000) {
							exp2 = exp%10000/100;
							exp = exp/10000;
							expString = exp + "."+exp2+"e";
							if(exp>10000) {
								exp2 = exp%10000/100;
								exp = exp/10000;
								expString = exp + "."+exp2+"t";
							}
						}
					}
					g.drawString(gd.get(i).get("DateLabel") + ":" + expString,startX,startY);
					startY = startY + 30;
					count++;
				}else {
					break;
				}
			}
			if(gd.size()>1) {
				int nextLevel = nextLevel(Integer.parseInt(mapler.get("Level")+""));
				Long nextExp = nextLevelExp(nextLevel);
				String day1 = null;
				if(expYesterday==0l) {
					day1 = "∞";
				}else {
					day1 = (nextExp - totalOverallExp)/expYesterday + "";
				}
				g.drawString("按照最近1天的进度，还需要"+day1+"天到达"+nextLevel+"级",startX,startY);
				startY = startY + 30;
				if(count>1) {
					String day2 = null;
					if(expLastWeek/count==0l) {
						day2 = "∞";
					}else {
						day2 = (nextExp - totalOverallExp)/(expLastWeek/count)+"";
					}
					g.drawString("按照最近"+count+"天的进度，还需要"+day2+"天到达"+nextLevel+"级",startX,startY);
					startY = startY + 30;
				}
				if(nextLevel<300) {
					nextExp = nextLevelExp(300);
					String day3 = null;
					if(maxExp==0l){
						day3 = "∞";
					}else {
						day3 = (nextExp - totalOverallExp)/maxExp + "";
					}

					startY = startY + 30;
					g.drawString("按照目前的进度，还需要"+day3+"天到达"+"300级",startX,startY);

				}
			}
		}



		//事件
		g.dispose();

//	        buffImg = resizeBufferedImage(buffImg,buffImg.getWidth()/2,buffImg.getHeight()/2,true);
		// TYPE_INT_RGB:创建一个RBG图像，24位深度，成功将32位图转化成24位
		BufferedImage newBufferedImage = new BufferedImage(
				sourceFile.getWidth(), sourceFile.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		newBufferedImage.createGraphics().drawImage(sourceFile, 0, 0,
				Color.WHITE, null);
		// write to jpeg file
//	        ImageIO.write(newBufferedImage, "jpg", new File(MsbotConst.imageUrl + "bfb/mn1.jpg"));
		ImageIO.write(newBufferedImage, "jpg", new File(MsbotConst.imageUrl +uuid +".jpg"));
		generateWaterFile(newBufferedImage, saveFilePath);
		return "[CQ:image,file=" + uuid +".jpg]";

	}

	@Override
	public String getRankList(List<String> richList, List<String> poorList) throws Exception {
		//画一个圆角矩形
//	      g.drawRoundRect(10,10,150,70,40,25);
		//涂一个圆角矩形块
//	      g.fillRoundRect(80,100,150,70,60,40);

		//创建图片对象
		BufferedImage image = new BufferedImage(900, 400, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font font = Loadfont2.Font(22);
		Font font2 = Loadfont2.Font(14);
		g.setBackground(new Color(247,250,231));
		g.clearRect(0,0,image.getWidth(),image.getHeight());

		g.setColor(new Color(230,245,252));
		g.fillRoundRect(33,20,400,360,40,25);
		g.setColor(new Color(0,0,0));
		g.setFont(font);
		g.drawString("排行榜", 180,50);
		g.setFont(font2);
		for(int i=0;i<richList.size();i++) {
			g.drawString(richList.get(i), 60, 50+28*(i+1));
		}

		g.setColor(new Color(230,245,252));
		g.fillRoundRect(466,20,400,360,40,25);
		g.setColor(new Color(0,0,0));
		g.setFont(font);
		g.drawString("负债榜", 180+433,50);
		g.setFont(font2);
		for(int i=0;i<poorList.size();i++) {
			g.drawString(poorList.get(i), 60+433, 50+28*(i+1));
		}
		g.dispose();

		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		ImageIO.write(image, "jpg", new File(MsbotConst.imageUrl +uuid +".jpg"));
//	        generateWaterFile(image, saveFilePath);
		return "[CQ:image,file=" + uuid +".jpg]";
	}

	private static int nextLevel(int level) {
		if(level<220) {
			return 220;
		}
		if(level<230) {
			return 230;
		}
		if(level<240) {
			return 240;
		}
		if(level<250) {
			return 250;
		}
		if(level<260) {
			return 260;
		}
		if(level<270) {
			return 270;
		}
		if(level<275) {
			return 275;
		}
		if(level<280) {
			return 280;
		}
		if(level<285) {
			return 285;
		}
		if(level<290) {
			return 290;
		}
		if(level<295) {
			return 295;
		}
		return 300;
	}
	private static Long nextLevelExp(int level) {
		switch (level) {
			case 220:
				return 226834057694l;
			case 230:
				return 888805728115l;
			case 240:
				return 2780379685705l;
			case 250:
				return 7764453421743l;
			case 260:
				return 19276710581130l;
			case 270:
				return 49642521336419l;
			case 275:
				return 82351036260243l;
			case 280:
				return 164638698169785l;
			case 285:
				return 408002977089330l;
			case 290:
				return 1127748451436850l;
			case 295:
				return 3256382736401070l;
			case 300:
				return 10100775367634700l;
			default:
				return 0l;
		}
	}
}
