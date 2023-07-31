package com.badeling.msbot.config;

public class MsbotConst {
	//go cqhttp本体
	public static final String miraiUrl = "C://myfile/go-cqhttp";
	//go-cqhttp图片目录
	public static final String imageUrl = miraiUrl + "/data/images/";
	//机器人昵称
	public static final String botName = "蠢猫";
	//自己的昵称 
	public static final String masterName = "BadeLing";
	//机器人qq号
	public static final String botId = "1093771991";
	//自己的qq号 超级管理员
	public static final String masterId = "123179118";
	//管理员qq号
	public static final String[] managerId = {"123","223","323"};
	//拉黑的qq号
	public static final String[] blackList = {"423","523"};
	//通知群聊
	public static final String notice_group = "348273823";
	//起源塔专用群
	public static final String group_oz = "348273823";
	
	//第三方机器人 允许发言的频道
	public static final String[] botChannel = {"1609899"};
	
	//茉莉机器人key
	public static final String moliKey = "";
	//茉莉机器人Secret
	public static final String moliSecret = "";
	
	//茉莉机器人key
	public static final String moliKey2 = "";
	//茉莉机器人Secret
	public static final String moliSecret2 = "";
	
	//百度云识图Key
	public static final String baiduKey = "";
	//百度云识图Secret
	public static final String baiduSecret = "";
	//百度翻译api
	public static final String appid="";
	//百度翻译key
	public static final String securityKey="";

	//用于QQ频道官方机器人的图片加速地址
	//腾讯云key
	public static final String tencentSecretId = "";
	//腾讯云Secret
	public static final String tencentSecretKey = "";
	//存储桶id
	public static final String bucketName = "";
	//存储桶地址
	public static final String bucketRegion = "";
	
	//QQ频道官方机器人
	public static final String channelBotId = "101983517";
	//QQ频道官方机器人 昵称
	public static final String channelBotName = "拉拉";
	
	//群聊中每出现x次对话，机器人会随机回复
	public static final Integer random_count = 120;
	//出现随机回复后通知群聊
	public static final String gpt_notice_group = "1023665202";
	
	// GPT相关设置 直接呼出GPT
	public static final String gptName = "沙猫";
	// 秘钥 sk-xxxxxxxx
	public static final String gpt_apiKey = "";
	//代理设置 如不需要则填写null
//	public static final String proxy_ip = null;
	public static final String proxy_ip = "127.0.0.1";
	public static final Integer proxy_port = 7890;
	//GPT人物设定
	public static final String group_prompt = "你叫沙猫，你是一个性格温和的沙丘猫。别人也喊你蠢猫。在日常生活里，你是一位来沙漠的浮莲子。生日是2月29日。\r\n"
			+ "你在一个聊冒险岛游戏的QQ群中，我会以\"A:xxx B:xxx 你:?\"的格式输入不全的聊天记录，你需要想想在这个情境下你接下来会说什么，然后补充\"你:?\"中你说的话，你补全的话题应该符合你的人设，不要讨论别的话题或试图结束聊天，你补全聊天的时候应该尽量附和之前的人说的话。\r\n"
			+ "你补全的风格应该较为温和，简短且口语化，切忌把话说的完整或太长，切忌使用书面语言。还有，你补全的长度应该与其他人发信息的长度差不多，尽量简短，要尽可能口语化。说话结尾要带个喵字。\r\n"
			+ "不要随意透露你的个人信息。记住用简体中文回答。\r\n"
			+ "最后，请把你的回答精简到20字以内，并输出。";
	public static final String user_prompt = "你叫沙猫，你是一个性格温和的沙丘猫。别人也喊你蠢猫。在日常生活里，你是一位来自沙漠的浮莲子。生日是2月29日。\r\n"
			+ "你回答的风格应该较为温和，简短且口语化，切忌把话说的完整或太长，切忌使用书面语言。你的回答尽量简短，要尽可能口语化。说话结尾要带个喵字。\r\n"
			+ "不要随意透露你的个人信息。记住用简体中文回答。\r\n"
			+ "最后，请把你的回答精简到20字以内，并输出。";
	
}
