# 重要信息

随着go-cqhttp落幕，本项目已于2023.11.5停止更新。目前go-cqhttp还能使用，暂时没有转战ntqq的想法。

# Msbot

冒险岛群聊机器人

前台使用go-cqhttp https://github.com/Mrs4s/go-cqhttp

前台上报地址 http://127.0.0.1:8081/msg/receive

前台监听地址 host: 127.0.0.1  port: 5700

# 视频搭建流程：

windows免环境版：https://www.bilibili.com/video/BV1us4y1w7JH

Linux服务器搭建：https://www.bilibili.com/video/BV14R4y1G7sM

windows服务器搭建：https://www.bilibili.com/video/BV1pW4y1r7JS

## 静态资源 

注：如从之前的版本更新到v2.0，需要更新image文件，有几个功能从文字输出变成了图片输出

下载链接：链接：https://pan.baidu.com/s/1DqnaK_MKystywwAsT_nZ7g 
提取码：9xgb

image.zip 图片资源 解压到go-cqhttp/data/image/

msbot_20220306_192337.sql 数据库备份文件

go-cqhttp_linux_amd64.tar 可以在上面go-cqhttp网址下载最新版本

jdk-8u321-linux-x64.tar

gradle-4.10.1-all.zip

## 运行环境 

  gradle 4.10.1

  java 1.8.0
  
## 配置文件

  修改数据库账号 密码
      
    /src/main/resources/application.properties
  
  配置前端目录 机器人QQ 昵称等
   
    /src/main/java/com/badeling/msbot/config/MsbotConst.java

## 运行方法

    gradle build

    gradle bootrun

## 功能列表.



*无视计算

    蠢猫无视92+30

*攻击计算（v2.1新增 数据会应用在无视计算中）

    蠢猫攻击6728 百分比157 面板66030797

*星之力计算

    蠢猫150级22星

    蠢猫160级13星428攻

*官网查询 常见条目：周日、维护、敲敲乐、礼品袋

    蠢猫官网周日

*世界组队时间表

    蠢猫世界组队
    
*收售金币（v2.0新增 无需前缀）

    5.4出200e
    
    收100e

*魔女抽奖（v2.0默认开启，有cd）

    //更换卡池内容修改go-cqhttp\data\images\bfb的图片即可
    蠢猫魔女

*退群查询（v2.0新增）

    蠢猫谁退群了

*迎新（v2.0新增）

    //修改数据库group_info的welcome字段，当有人入群，5分钟没有修改群名片，便会提醒。
    eg:改下群名片，【职业】ID，然后你就是群主了~

*娱乐功能

    蠢猫占卜

    蠢猫抽卡

    复读机周报

*词库 msg数据库

    //添加词库
    蠢猫学习问练级 答不练
    
    //查询词库
    蠢猫查询练级
    
    //删除词库 通过查询获得问题的id
    蠢猫删除问题[id]
    
    //语料库
    蠢猫火花
    蠢猫水路

*词库 msg_no_prefix 数据库 （无需前缀触发的语料）

exact为是否精确匹配 需要在数据库手动修改 

删除也需要在数据库中删除 没有直接的命令
   
*添加词库
   
    蠢猫学习布尔问好耶 答禁止好耶

*角色查询 限GMS

    联盟查询badeling
 
*查成分（v2.0新增）
    //查看群友活跃度 可@指定对象
    查成分
    
*排行榜（v2.0新增）
    //活跃度排行榜
    蠢猫排行榜

*起源39F答题 目前只有英文题库
    
    //v2.0版本无需39 直接发图片即可 但需配置MsbotConst中的起源塔专用群属性 以及百度云识别密钥（高精度识别每天免费500次）
    39[图片]
    
*翻译

    翻译I love you
    
*识别图片文字

    识图[图片]
    高精度识图[图片]

*图片安全（最后一个版本新增）

    自动识别并撤回群内的色图。调用百度云API。图片识别后会将hash和识别内容存入数据库。只对指定群聊生效 MsbotConst.img_security_group。

*早晚安问好（最后一个版本新增）

    早上6.30 晚上23.50会发送早晚安。只对指定群聊生效MsbotConst.group_morning
    时间可以在ScheduleTask中修改。
    文字从数据库中选（让GPT写了500多条）。
    图片从morning和night文件夹中随机选取。
    
*怪物查询（v2.0版本删除该功能，因为懒得更新数据库）

    蠢猫怪物[ID/名称]

*茉莉云删除

    有无敌的GPT了不需要了。
