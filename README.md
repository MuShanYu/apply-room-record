# 房间预约与流动统计后端

## 项目演示

后台管理客户端：https://www.mushanyu.xyz

小程序客户端界面预览：

![image-20240226142457470](https://mushanyu-note.oss-cn-beijing.aliyuncs.com/mk-image/image-20240226142457470.png)

![image-20240226142949403](https://mushanyu-note.oss-cn-beijing.aliyuncs.com/mk-image/image-20240226142949403.png)

## 项目描述

本项目为**房间预约与流动统计**，应用功能不局限于预约与流动统计，主要功能包含消息通知、公告、房间预约、通用申请处理、房间签到签出、人员人数与人次统计、签到时长统计、报表统计、基础功能、通用权限解决方案、在线用户监控（基于netty）等。

基础功能主要有：统一异常处理、统一请求结果返回、统一请求异常处理、wx请求工具包基础，项目统一配置等。

技术栈：

- 管理端：Vue、element-ui等、基于vue-element-admin框架开发。
- 客户端：基于uni-app开发，组件使用图鸟UI。
- 后端：springBoot、mybatis-dynamic-sql、redis、mysql、sa-token、springboot-jpa、netty等。

本项目分为：

- 客户端：[apply-room-record-app](https://github.com/MuShanYu/apply-room-record-app) 小程序
- 管理端：[apply-room-record-admin](https://github.com/MuShanYu/apply-room-record-admin) 
- 后端：[apply-room-record](https://github.com/MuShanYu/apply-room-record) 

## 本地运行说明

### 1. 后端

- 首先将代码clone到您的本地，使用集成开发工具打开项目，如idea。
- 配置本地开发环境，要求使用jdk17，数据库使用mysql8，redis。
- 环境配置完成后修改配置文件，包括数据库配置（账号密码）、redis配置、邮箱账号密码配置（如没有请去对应邮箱官网申请）。
- 运行resource/db/db_create.sql，创建基础数据库。
- 运行项目，查看本地数据库是否成功生成数据表。
- 运行项目下的init_data.sql脚本插入初始化数据。

### 2. 客户端

可以百度搜搜uniapp开发小程序步骤，以及需要的工具。

- vscode（vscode开发微信小程序，可以参考这篇博客https://juejin.cn/post/7090532271257714695）

- 微信开发者工具

- npm install

### 3. 管理端

- 将代码clone到本地。
- `npm install`

## 部署说明

前后端项目提供完整dockerfile构建应用镜像

## 开源协议

项目使用MIT开源协议。

## 联系方式

QQ交流群：811043258

作者微信：@MuShanYu：wx：wxid_f5ayc9d2mken22
