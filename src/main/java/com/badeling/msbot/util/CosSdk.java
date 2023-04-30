package com.badeling.msbot.util;

import java.io.File;
import java.util.List;

import com.badeling.msbot.config.MsbotConst;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.COSObjectSummary;
import com.qcloud.cos.model.ListObjectsRequest;
import com.qcloud.cos.model.ObjectListing;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;

public class CosSdk {
	
	
	
	public static COSClient createCos() {
		// 1 初始化用户身份信息（secretId, secretKey）。
		// SECRETID和SECRETKEY请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
		String secretId = MsbotConst.tencentSecretId;
		String secretKey = MsbotConst.tencentSecretKey;
		COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
		// 2 设置 bucket 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
		// clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
		Region region = new Region(MsbotConst.bucketRegion);
		ClientConfig clientConfig = new ClientConfig(region);
		// 这里建议设置使用 https 协议
		// 从 5.6.54 版本开始，默认使用了 https
		clientConfig.setHttpProtocol(HttpProtocol.https);
		// 3 生成 cos 客户端。
		COSClient cosClient = new COSClient(cred, clientConfig);
		return cosClient;
	}
	
	@SuppressWarnings("unused")
	public static void deleteAllFile() {
		COSClient cosClient = createCos();
		// Bucket的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		// 设置bucket名称
		listObjectsRequest.setBucketName(MsbotConst.bucketName);
		// prefix表示列出的object的key以prefix开始
		listObjectsRequest.setPrefix("");
		// deliter表示分隔符, 设置为/表示列出当前目录下的object, 设置为空表示列出所有的object
		listObjectsRequest.setDelimiter("/");
		// 设置最大遍历出多少个对象, 一次listobject最大支持1000
		listObjectsRequest.setMaxKeys(1000);
		ObjectListing objectListing = null;
		do {
		    try {
		        objectListing = cosClient.listObjects(listObjectsRequest);
		    } catch (CosServiceException e) {
		        e.printStackTrace();
		        return;
		    } catch (CosClientException e) {
		        e.printStackTrace();
		        return;
		    }
		    // common prefix表示表示被delimiter截断的路径, 如delimter设置为/, common prefix则表示所有子目录的路径
		    List<String> commonPrefixs = objectListing.getCommonPrefixes();

		    // object summary表示所有列出的object列表
		    List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
		    for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
		        // 文件的路径key
		        String key = cosObjectSummary.getKey();
		        // 文件的etag
		        String etag = cosObjectSummary.getETag();
		        // 文件的长度
		        long fileSize = cosObjectSummary.getSize();
		        // 文件的存储类型
		        String storageClasses = cosObjectSummary.getStorageClass();
		        if(!key.equals(MsbotConst.channelBotId + ".json")) {
		        	deleteFile(cosClient,key);
		        }
		    }

		    String nextMarker = objectListing.getNextMarker();
		    listObjectsRequest.setMarker(nextMarker);
		} while (objectListing.isTruncated());
		cosClient.shutdown();
	}
	
	@SuppressWarnings("unused")
	public static String uploadFile(String localFilePath) {
		COSClient cosClient = createCos();
		// 指定要上传的文件
		File localFile = new File(localFilePath);
		// 指定文件将要存放的存储桶
		// 指定文件上传到 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
		String key = localFile.getName();
		if(!key.contains(".")) {
			key = key + ".jpg";
		}
		//判断文件是否存在
		try {
		    boolean objectExists = cosClient.doesObjectExist(MsbotConst.bucketName, key);
		    if(!objectExists) {
		    	//文件不存在 上传
		    	PutObjectRequest putObjectRequest = new PutObjectRequest(MsbotConst.bucketName, key, localFile);
				PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
		    }
		} catch (CosServiceException e) {
		    e.printStackTrace();
		} catch (CosClientException e) {
		    e.printStackTrace();
		}
		cosClient.shutdown();
		return key;
	}
		
	public static void deleteFile(COSClient cosClient,String file_url) {
		// 指定被删除的文件在 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示删除位于 folder 路径下的文件 picture.jpg
		cosClient.deleteObject(MsbotConst.bucketName, file_url);
		cosClient.shutdown();
	}
	
	
}
