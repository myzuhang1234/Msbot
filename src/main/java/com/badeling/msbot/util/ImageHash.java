package com.badeling.msbot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.hutool.core.io.FileUtil;

public class ImageHash {
	/**
	 * 将字节数组转换为String类型哈希值
	 * @param bytes 字节数组
	 * @return 哈希值
	 */
	public static String bytesToHex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte b : bytes) {
			result.append(String.format("%02x", b));
		}
		return result.toString();
	}

	/**
	 * 计算SHA256哈希值
	 * @param filePath 文件路径
	 * @return 字节数组
	 * @throws IOException IO异常
	 * @throws NoSuchAlgorithmException NoSearch算法异常
	 */
	public static byte[] calculateSHA256(String filePath) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		try (
				FileInputStream fis = new FileInputStream(filePath);
				FileChannel channel = fis.getChannel();
				DigestInputStream dis = new DigestInputStream(fis, digest)) {
				ByteBuffer buffer = ByteBuffer.allocate(8192); // 8 KB buffer
			while (channel.read(buffer) != -1) {
				buffer.flip();
				digest.update(buffer);
				buffer.clear();
			}
			return digest.digest();
		}
	}
	
	//网络图片
	public static byte[] calculateSHA256(String filePath,String imageSavePath) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
 
		//获取网络URL文件
		InputStream fis2 = new URL(filePath).openStream();
		//图片存储位置
		File file = new File(imageSavePath);
		FileUtil.writeFromStream(fis2,file);
 
		try (
				FileInputStream fis = new FileInputStream(file);
				FileChannel channel = fis.getChannel();
				DigestInputStream dis = new DigestInputStream(fis, digest)) {
				ByteBuffer buffer = ByteBuffer.allocate(8192); // 8 KB buffer
			while (channel.read(buffer) != -1) {
				buffer.flip();
				digest.update(buffer);
				buffer.clear();
			}
			System.out.println(file.getPath());
			file.delete();
			return digest.digest();
		}
	}

}
