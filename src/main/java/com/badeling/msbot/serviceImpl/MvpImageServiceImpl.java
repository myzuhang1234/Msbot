package com.badeling.msbot.serviceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.domain.ReceiveMsg;
import com.badeling.msbot.service.MvpImageService;
import com.badeling.msbot.util.Base64Util;
import com.badeling.msbot.util.FileUtil;
import com.badeling.msbot.util.HttpUtil;




@Component
public class MvpImageServiceImpl implements MvpImageService{
	
	@Override
	public String[] handImageMsg(ReceiveMsg receiveMsg) {
		
		String raw_message = receiveMsg.getMessage();
		int questionIndex = raw_message.indexOf(",url=")+5;
		int answerIndex = raw_message.indexOf("]");
		String imageName = UUID.randomUUID().toString().replaceAll("-", "")+".jpg";
//		String imageName = raw_message.substring(raw_message.indexOf("file=")+5,raw_message.indexOf(",url=")-6);
		String imageUrl = raw_message.substring(questionIndex, answerIndex);
//		try {
//			String NewfileName = CoolQconst.imageUrl + imageName +".cqimg";
//			FileReader fileReader = new FileReader(NewfileName);
//
//	        BufferedReader bufferedReader = new BufferedReader(fileReader);
//	        String line =bufferedReader.readLine();
//
//	        while (line!=null){
//	            if(line.contains("url=")) {
//	            	imageUrl = line.substring(4);
//	            }
//	            line = bufferedReader.readLine();
//	        }
//
//	        bufferedReader.close();
//	        fileReader.close();
//
//		}catch(Exception e) {
//			return null;
//		}
		try {
			download(imageUrl, imageName,MsbotConst.imageUrl,imageName);
		} catch (Exception e) {
			return null;
		} 
		
		String back = generalBasic(MsbotConst.imageUrl+imageName);
		String[] result = new String [2];
		result[0] = back;
		result[1] = imageName;
		return result;
	}
	
	@Override
	public String saveImage(String raw_message) {
		System.out.println(raw_message);
		int questionIndex = raw_message.indexOf(",url=")+5;
		int answerIndex = raw_message.indexOf("]");
//		[CQ:image,file=6BBE03D40F0DA08AD91BBF6C627AEEE5,url=http://gchat.qpic.cn/gchatpic_new/123179118/4193168946-3038401046-6BBE03D40F0DA08AD91BBF6C627AEEE5/0?term=2]","raw_message":"[CQ:image,file=6BBE03D40F0DA08AD91BBF6C627AEEE5,url=http://gchat.qpic.cn/gchatpic_new/123179118/4193168946-3038401046-6BBE03D40F0DA08AD91BBF6C627AEEE5/0?term=2]","font":0,"sender":{"user_id":123179118,"nickname":"神话ヤ北真天","card":"神话ヤ北真天","sex":"unknown","age":0,"area":"unknown","level":"unknown","role":"admin","title":"unknown"},"time":1600180430,"post_type":"message","message_type":"group"}
//		String imageName = raw_message.substring(raw_message.indexOf("file=")+5,raw_message.indexOf(",url="));
//		String imageName = raw_message.substring(raw_message.indexOf("file=")+5,raw_message.indexOf(",url=")-6);
		String imageName = UUID.randomUUID().toString().replaceAll("-", "")+".jpg";
		String imageUrl = raw_message.substring(questionIndex, answerIndex);
		try {
			download(imageUrl, imageName,MsbotConst.imageUrl+"save/",imageName);
			return imageName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	//将图片下载到电脑
	private void download(String urlString, String filename,String savePath,String imageName) throws Exception {
		// 构造URL  
        URL url = new URL(urlString);  
        // 打开连接  
        URLConnection con = url.openConnection();  
        //设置请求超时为5s  
        con.setConnectTimeout(5*1000);  
        // 输入流  
        InputStream is = con.getInputStream();  
      
        // 1K的数据缓冲  
        byte[] bs = new byte[1024];  
        // 读取到的数据长度  
        int len;
        // 输出的文件流  
       File sf=new File(savePath);  
       if(!sf.exists()){  
           sf.mkdirs();  
       }  
       // 新的图片文件名 = 编号 +"."图片扩展名
       String newFileName = imageName;
       OutputStream os = new FileOutputStream(sf.getPath()+"/"+newFileName);  
        // 开始读取  
        while ((len = is.read(bs)) != -1) {  
          os.write(bs, 0, len);  
        }  
        // 完毕，关闭所有链接  
        os.close();  
        is.close();  
		
	}

	//上传图片识别
	public static String generalBasic(String imgUrl) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        try {
            // 本地文件路径
            String filePath = imgUrl;
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam + "&detect_language=true";

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = getAuth();

            String result = HttpUtil.post(url, accessToken, param);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	//获取token
	public static String getAuth() {
        // 官网获取的 API Key 更新为你注册的
        String clientId = MsbotConst.baiduKey;
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = MsbotConst.baiduSecret;
        return getAuth(clientId, clientSecret);
    }

    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                @SuppressWarnings("unused")
				String object = key + "--->" + map.get(key);
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            
        }
        return null;
    }


	@Override
	public String saveTempImage(String raw_message) throws Exception {
		String imageName = UUID.randomUUID().toString().replaceAll("-", "")+".jpg";
		download(raw_message, imageName , MsbotConst.imageUrl , imageName);
		return imageName;
	}

	@Override
	public String saveFlagRaceImage(String raw_message,String imageNewName) {
		int questionIndex = raw_message.indexOf(",url=")+5;
		int answerIndex = raw_message.indexOf("]");
		String imageName = UUID.randomUUID().toString().replaceAll("-", "")+".jpg";
//		String imageName = raw_message.substring(raw_message.indexOf("file=")+5,raw_message.indexOf(",url=")-6);
		String imageUrl = raw_message.substring(questionIndex, answerIndex);
//		try {
//			String NewfileName = CoolQconst.imageUrl + imageName + ".cqimg";
//			FileReader fileReader = new FileReader(NewfileName);
//
//	        BufferedReader bufferedReader = new BufferedReader(fileReader);
//	        String line =bufferedReader.readLine();
//
//	        while (line!=null){
//	            if(line.contains("url=")) {
//	            	imageUrl = line.substring(4);
//	            }
//	            line = bufferedReader.readLine();
//	        }
//
//	        bufferedReader.close();
//	        fileReader.close();
//
//		}catch(Exception e) {
//			return null; 
//		}
		
		try {
			download(imageUrl, imageName,MsbotConst.imageUrl+"FR/",imageNewName);
			return imageName;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String[] handHigherImageMsg(ReceiveMsg receiveMsg) {
		String raw_message = receiveMsg.getMessage();
		int questionIndex = raw_message.indexOf(",url=")+5;
		int answerIndex = raw_message.indexOf("]");
		String imageName = UUID.randomUUID().toString().replaceAll("-", "")+".jpg";
//		String imageName = raw_message.substring(raw_message.indexOf("file=")+5,raw_message.indexOf(",url=")-6);
		String imageUrl = raw_message.substring(questionIndex, answerIndex);
//		try {
//			String NewfileName = CoolQconst.imageUrl + imageName +".cqimg";
//			FileReader fileReader = new FileReader(NewfileName);
//
//	        BufferedReader bufferedReader = new BufferedReader(fileReader);
//	        String line =bufferedReader.readLine();
//
//	        while (line!=null){
//	            if(line.contains("url=")) {
//	            	imageUrl = line.substring(4);
//	            }
//	            line = bufferedReader.readLine();
//	        }
//
//	        bufferedReader.close();
//	        fileReader.close();
//
//		}catch(Exception e) {
//			return null;
//		}
		
		try {
			download(imageUrl, imageName,MsbotConst.imageUrl,imageName);
		} catch (Exception e) {
			return null;
		} 
		
		String back = generalBasic2(MsbotConst.imageUrl+imageName);
		String[] result = new String [2];
		result[0] = back;
		result[1] = imageName;
		return result;
	}

	private String generalBasic2(String imgUrl) {
		// 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";
        try {
            // 本地文件路径
            String filePath = imgUrl;
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam + "&language_type=auto_detect";

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = getAuth();

            String result = HttpUtil.post(url, accessToken, param);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	//表格识图
	public static String getOrcTemplateRequest(String imgUrl) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/iocr/recognise";
        try {
            // 本地文件路径
            String filePath = imgUrl;
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            // 请求模板参数
            String recogniseParams = "templateSign=47714237acb4991b0449de6e409dd519&image=" + URLEncoder.encode(imgStr, "UTF-8");
            // 请求分类器参数
//            String classifierParams = "classifierId=your_classfier_id&image=" + URLEncoder.encode(imgStr, "UTF-8");
            
            
            String accessToken = getAuth();
            // 请求模板识别
            String result = HttpUtil.post(url, accessToken, recogniseParams);
            // 请求分类器识别
            // String result = HttpUtil.post(recogniseUrl, accessToken, classifierParams);
            
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	
}
