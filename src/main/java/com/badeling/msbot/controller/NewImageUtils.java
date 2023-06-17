package com.badeling.msbot.controller;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class NewImageUtils{


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
	public static BufferedImage watermark(File file, File waterFile, int x, int y, float alpha) throws IOException {
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

	public static BufferedImage watermark(BufferedImage buffImg, BufferedImage waterImg, int x, int y, float alpha) throws IOException {
		// 创建Graphics2D对象，用在底图对象上绘图
		Graphics2D g2d = buffImg.createGraphics();
		int waterImgWidth = waterImg.getWidth();// 获取层图的宽度

		int proportion = waterImgWidth/200;

		int waterImgHeight = waterImg.getHeight()/proportion;// 获取层图的高度
		// 在图形和图像中实现混合和透明效果
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
		// 绘制
		g2d.drawImage(waterImg, x, y, 200, waterImgHeight, null);
		g2d.dispose();// 释放图形上下文使用的系统资源
		return buffImg;
	}

	public static BufferedImage watermark(BufferedImage buffImg, File waterFile, int x, int y, float alpha) throws IOException {
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
	public void generateWaterFile(BufferedImage buffImg, String savePath) {
		int temp = savePath.lastIndexOf(".") + 1;
		try {
			ImageIO.write(buffImg, savePath.substring(temp), new File(savePath));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


	//原定的星星代码

//			private static BufferedImage makeStar() throws IOException {
//				int width = 27*1;
//				int height = 27;
//				String star = imageUrl + "icon_rankstar.png";
//		        String starb = imageUrl + "icon_rankstar_blank.png";
//		        String saveFilePath = imageUrl + "112star.png";
//		        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//		        BufferedImage starImg = ImageIO.read(new File(star));
//		        BufferedImage starImgb = ImageIO.read(new File(starb));
//		         // 创建Graphics2D对象，用在底图对象上绘图
//		         Graphics2D g2d = buffImg.createGraphics();
//		         int waterImgWidth = starImg.getWidth();// 获取层图的宽度
//		         int waterImgHeight = starImg.getHeight();// 获取层图的高度
//		         //透明
//			     buffImg = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
//			     g2d.dispose();
//			     g2d = buffImg.createGraphics();
//		         // 在图形和图像中实现混合和透明效果
//		         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
//		         // 绘制
//		         g2d.drawImage(starImg, 0, 0, waterImgWidth, waterImgHeight, null);
//		         g2d.dispose();// 释放图形上下文使用的系统资源
//		         return buffImg;
//			}


	/**
	 * 调整bufferedimage大小
	 * @param source BufferedImage 原始image
	 * @param targetW int  目标宽
	 * @param targetH int  目标高
	 * @param flag boolean 是否同比例调整
	 * @return BufferedImage  返回新image
	 */
	public static BufferedImage resizeBufferedImage(BufferedImage source, int targetW, int targetH, boolean flag) {
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
	/**
	 * 传入的图像必须是正方形的 才会 圆形 如果是长方形的比例则会变成椭圆的
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage convertCircular(BufferedImage bi1) throws IOException {
		// 透明底的图片
		BufferedImage bi2 = new BufferedImage(bi1.getWidth(), bi1.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, bi1.getWidth(), bi1.getHeight());
		Graphics2D g2 = bi2.createGraphics();
		g2.setClip(shape);
		// 使用 setRenderingHint 设置抗锯齿
		g2.drawImage(bi1, 0, 0, null);
		// 设置颜色
		g2.setBackground(Color.green);
		g2.dispose();

		return bi2;
	}
	/**
	 * 旋转图片为指定角度
	 *
	 * @param bufferedimage
	 *            目标图像
	 * @param degree
	 *            旋转角度
	 * @return
	 */
	public static BufferedImage rotateImage(final BufferedImage bufferedimage,
											final int degree) {
		int w = bufferedimage.getWidth();
		int h = bufferedimage.getHeight();
		int type = bufferedimage.getColorModel().getTransparency();
		BufferedImage img;
		Graphics2D graphics2d;
		(graphics2d = (img = new BufferedImage(w, h, type))
				.createGraphics()).setRenderingHint(
				RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
		graphics2d.drawImage(bufferedimage, 0, 0, null);
		graphics2d.dispose();
		return img;
	}

	/**
	 * 图片设置圆角
	 * @param srcImage
	 * @param radius
	 * @param border
	 * @param padding
	 * @return
	 * @throws IOException
	 */

	public static BufferedImage setRadius(BufferedImage srcImage,int radius,int border,int padding)throws IOException{

		int width = srcImage.getWidth();

		int height = srcImage.getHeight();

		int canvasWidth = width + padding *2;

		int canvasHeight = height + padding *2;

		BufferedImage image =new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D gs = image.createGraphics();

		gs.setComposite(AlphaComposite.Src);

		gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		gs.setColor(new Color(68,68,68));

		gs.fill(new RoundRectangle2D.Float(0,0, canvasWidth, canvasHeight, radius, radius));

		gs.setComposite(AlphaComposite.SrcAtop);

		gs.drawImage(setClip(srcImage, radius), padding, padding,null);

		if(border !=0){

			gs.setColor(Color.GRAY);

			gs.setStroke(new BasicStroke(border));

			gs.drawRoundRect(padding, padding, canvasWidth -2 * padding, canvasHeight -2 * padding, radius, radius);

		}

		gs.dispose();

		return image;

	}

	/**
	 * 图片切圆角
	 * @param srcImage
	 * @param radius
	 * @return
	 */

	public static BufferedImage setClip(BufferedImage srcImage,int radius){

		int width = srcImage.getWidth();

		int height = srcImage.getHeight();

		BufferedImage image =new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D gs = image.createGraphics();

		gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		gs.setClip(new RoundRectangle2D.Double(0,0, width, height, radius, radius));

		gs.drawImage(srcImage,0,0,null);

		gs.dispose();

		return image;

	}

}