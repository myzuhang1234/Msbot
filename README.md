# Msbot

冒险岛群聊机器人 蠢猫（无视计算 星之力计算 世界组队表 占卜 怪物查询）

前台使用go-cqhttp

前台上报地址 http://127.0.0.1:8081/msg/receive

前台监听地址 host: 127.0.0.1  port: 5700

搭建流程的视频正在录制中 

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
