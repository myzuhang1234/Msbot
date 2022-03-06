# Msbot

冒险岛群聊机器人

前台使用go-cqhttp https://github.com/Mrs4s/go-cqhttp

前台上报地址 http://127.0.0.1:8081/msg/receive

前台监听地址 host: 127.0.0.1  port: 5700

视频搭建流程 https://www.bilibili.com/video/BV14R4y1G7sM

## 静态资源 

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

*怪物查询

    蠢猫怪物[ID/名称]

*无视计算

    蠢猫无视92+30

*星之力计算

    蠢猫150级22星

    蠢猫160级13星428攻

*官网查询 常见条目：周日、维护、敲敲乐、礼品袋

    蠢猫官网周日

*世界组队时间表

    蠢猫世界组队

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

*起源39F答题 目前只有英文题库

    39[图片]
    
*翻译

    翻译I love you
    
*识别图片文字

    识图[图片]
    高精度识图[图片]
