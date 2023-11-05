package com.badeling.msbot.serviceImpl;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.domain.GlobalVariable;
import com.badeling.msbot.domain.GroupMsg;
import com.badeling.msbot.domain.ReceiveMsg;
import com.badeling.msbot.entity.ImageSecurity;
import com.badeling.msbot.repository.ImageSecurityRepository;
import com.badeling.msbot.service.GroupMsgService;
import com.badeling.msbot.service.ImgModerationService;
import com.badeling.msbot.util.Base64Util;
import com.badeling.msbot.util.FileUtil;
import com.badeling.msbot.util.HttpUtil;
import com.badeling.msbot.util.ImageHash;


@Component
public class ImgModerationServiceImpl implements ImgModerationService{
	
	@Autowired
	ImageSecurityRepository imageSecurityRepository;
	
	@Autowired
	GroupMsgService groupMsgService;
	
	
	@Override
	public void imgModeration(ReceiveMsg receiveMsg) {
		String raw_message = receiveMsg.getRaw_message();
		int count = 0;
		while(raw_message.contains("[CQ:image")&&count<30) {
			String imageName = UUID.randomUUID().toString().replaceAll("-", "")+".jpg";
			//分析图片名
			try {
				int start = raw_message.indexOf("file=")+5;
				int end = raw_message.indexOf(".image");
				if((start+32)==end) {
					imageName = raw_message.substring(start,end)+".jpg";
					System.out.println("图片名完全正确，"+imageName);
					ImageSecurity isr = imageSecurityRepository.findImageByName(imageName);
					if(isr!=null) {
						System.out.println("图片名查询匹配成功");
						if(!isr.getConclusion().equals("合规")) {
							//撤回消息
							if(receiveMsg.getSender().getRole().equals("admin")||receiveMsg.getSender().getRole().equals("owner")) {
								GroupMsg groupMsg = new GroupMsg();
								groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
								groupMsg.setMessage("置信度"+isr.getProbability());
								groupMsgService.sendGroupMsg(groupMsg);
							}else {
								HashMap<String,Integer> map = new HashMap<>();
								map.put("message_id", Integer.parseInt(receiveMsg.getMessage_id()));
								groupMsgService.deleteMsg(map);
							}
							Thread.sleep(3257);
							GroupMsg groupMsg = new GroupMsg();
							groupMsg.setGroup_id(Long.parseLong(MsbotConst.notice_group));
							groupMsg.setMessage("已存在" + isr.getConclusion() + "图片:"+isr.getImageName().substring(0,10) +"; 置信度"+isr.getProbability());
							groupMsgService.sendGroupMsg(groupMsg);
							
				    		break;
						}
						raw_message = raw_message.substring(raw_message.indexOf("]")+1);
						count++;
						continue;
					}
				}else {
					if(end>start) {
						if(end-start<40) {
							imageName = raw_message.substring(start,end)+".jpg";
							System.out.println("图片名正确，"+(end-start));
						}else {
							imageName = raw_message.substring(start,start+32)+".jpg";
							System.out.println("图片名正确2，"+(end-start));
						}
					}else {
						end = raw_message.length();
						if(end-start<40) {
							imageName = raw_message.substring(start,end)+".jpg";
							System.out.println("图片名不太正确，"+(end-start));
						}else {
							imageName = raw_message.substring(start,start+32)+".jpg";
							System.out.println("图片名不太正确2，"+(end-start));
						}
					}
				}
			} catch (Exception e) {
				System.out.println("图片名格式有误");
			}
			
			try{
				//分析图片CQ码的地址
				int start = raw_message.indexOf(",url=")+5;
				int end = raw_message.indexOf("]");
				if(start>end) {
					raw_message = raw_message.substring(end+1);
					continue;
				}				
				String imageUrl = raw_message.substring(start, end);
				//保存图片 计算图片哈希值
//				String imageName = UUID.randomUUID().toString().replaceAll("-", "")+".jpg";
				byte[] sha256 = ImageHash.calculateSHA256(imageUrl,MsbotConst.imageUrl+imageName);
				String sha256Hex = ImageHash.bytesToHex(sha256);
				System.out.println("SHA256: " + sha256Hex);
				
				String imageCq = raw_message.substring(raw_message.indexOf("[CQ:image,file"), raw_message.indexOf("]")+1);
				raw_message = raw_message.replace(imageCq, "");
				
				ImageSecurity isr = imageSecurityRepository.findImageByHash(sha256Hex);
				if(isr!=null) {
					System.out.println("图片哈希值查询匹配成功");
					if(!isr.getConclusion().equals("合规")) {
						//撤回消息
						if(receiveMsg.getSender().getRole().equals("admin")||receiveMsg.getSender().getRole().equals("owner")) {
							GroupMsg groupMsg = new GroupMsg();
							groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
							groupMsg.setMessage("置信度"+isr.getProbability());
							groupMsgService.sendGroupMsg(groupMsg);
						}else {
							HashMap<String,Integer> map = new HashMap<>();
							map.put("message_id", Integer.parseInt(receiveMsg.getMessage_id()));
							groupMsgService.deleteMsg(map);
						}
						
						GroupMsg groupMsg = new GroupMsg();
						groupMsg.setGroup_id(Long.parseLong(MsbotConst.notice_group));
						groupMsg.setMessage("已存在" + isr.getConclusion() + "图片哈希:"+isr.getImageHashCode() +"; 置信度"+isr.getProbability());
						groupMsgService.sendGroupMsg(groupMsg);
												
			    		break;
					}
					raw_message = raw_message.substring(raw_message.indexOf("]")+1);
					continue;
				}
				
				
				String imgCensor = ImgCensor(imageUrl);
				JSONObject obj = new JSONObject(imgCensor);
		    	
		    	if(!imgCensor.contains("conclusion")) {
		    		System.out.println(obj.get("error_msg"));
		    		GroupMsg gm = new GroupMsg();
		    		gm.setGroup_id(Long.parseLong(MsbotConst.notice_group));
		    		gm.setMessage(obj.get("error_msg").toString());
					groupMsgService.sendGroupMsg(gm);
		    		
		    	}
		    	
		    	if(obj.get("conclusion").toString().equals("合规")) {
		    		System.out.println("合规");
		    		ImageSecurity is = new ImageSecurity();
		    		is.setConclusion(obj.get("conclusion").toString());
		    		is.setImageHashCode(sha256Hex);
		    		is.setImageName(imageName);
		    		imageSecurityRepository.save(is);
		    	}else if(obj.get("conclusion").toString().equals("不合规")||obj.get("conclusion").toString().equals("疑似")){
		    		ImageSecurity is = new ImageSecurity();
		    		is.setConclusion(obj.get("conclusion").toString());
		    		is.setImageHashCode(sha256Hex);
		    		is.setImageName(imageName);
		    		
		    		String data = obj.get("data").toString();
		    		JSONObject obj2 = new JSONObject(data.substring(1,data.length()-1));
		    		Float f = Float.parseFloat(obj2.get("probability").toString());
		    		DecimalFormat df = new DecimalFormat("0.00%");
		    		System.out.println(df.format(f));
		    		is.setMsg(obj2.get("msg").toString());
		    		is.setProbability(df.format(f));
		    		imageSecurityRepository.save(is);
		    		
		    		//撤回消息
		    		if(receiveMsg.getSender().getRole().equals("admin")||receiveMsg.getSender().getRole().equals("owner")) {
		    			GroupMsg groupMsg = new GroupMsg();
						groupMsg.setGroup_id(Long.parseLong(receiveMsg.getGroup_id()));
						groupMsg.setMessage("类型"+obj2.get("subType").toString()+";置信度"+df.format(f));
						groupMsgService.sendGroupMsg(groupMsg);
					}else {
						HashMap<String,Integer> map = new HashMap<>();
						map.put("message_id", Integer.parseInt(receiveMsg.getMessage_id()));
						groupMsgService.deleteMsg(map);
					}
		    		GroupMsg groupMsg = new GroupMsg();
					groupMsg.setGroup_id(Long.parseLong(MsbotConst.notice_group));
					groupMsg.setMessage(obj.get("conclusion").toString() + "类型"+obj2.get("subType").toString()+";置信度"+df.format(f)+";"+imageName.substring(0,10));
					groupMsgService.sendGroupMsg(groupMsg);   		
		    		break;
		    	}else if(obj.get("conclusion").toString().equals("审核失败")) {
		    		GroupMsg groupMsg = new GroupMsg();
					groupMsg.setGroup_id(Long.parseLong(MsbotConst.notice_group));
					groupMsg.setMessage("errer:"+imgCensor);
					groupMsgService.sendGroupMsg(groupMsg);
		    	}else{
		    		GroupMsg groupMsg = new GroupMsg();
					groupMsg.setGroup_id(Long.parseLong(MsbotConst.notice_group));
					groupMsg.setMessage("errer2:"+imgCensor);
					groupMsgService.sendGroupMsg(groupMsg);
		    	}
				
				
			}catch (Exception e) {
				e.printStackTrace();
			}		
			
			count++;
		}
	}
	
	
	public static String ImgCensor(String image) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/img_censor/v2/user_defined";
        try {
            // 本地文件路径
            String filePath = image;
            String param = null;
            if(filePath.contains("http")) {
            	param = "imgUrl=" + filePath;
            }else {
            	byte[] imgData = FileUtil.readFileByBytes(filePath);
                String imgStr = Base64Util.encode(imgData);
                String imgParam = URLEncoder.encode(imgStr, "UTF-8");
                param = "image=" + imgParam;
            }
            
            String accessToken = GlobalVariable.getBaiduAuth().get("imageSecurity");
            
            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
		
	@Override
	public String getAuth(String ak, String sk) {
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
	
}
