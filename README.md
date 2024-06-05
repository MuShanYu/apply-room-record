# 房间预约与流动统计后端

## 项目演示

小程序、后端、管理端演示版开发中，后续会进行更新。

部分界面预览：

![image-20240226142457470](https://mushanyu-note.oss-cn-beijing.aliyuncs.com/mk-image/image-20240226142457470.png)

![image-20240226142949403](https://mushanyu-note.oss-cn-beijing.aliyuncs.com/mk-image/image-20240226142949403.png)

![image-20240524154241867](https://mushanyu-note.oss-cn-beijing.aliyuncs.com/mk-image/image-20240524154241867.png)

![image-20240605171235244](https://mushanyu-note.oss-cn-beijing.aliyuncs.com/mk-image/image-20240605171235244.png)

![image-20240524154342067](https://mushanyu-note.oss-cn-beijing.aliyuncs.com/mk-image/image-20240524154342067.png)

![image-20240524154142556](https://mushanyu-note.oss-cn-beijing.aliyuncs.com/mk-image/image-20240524154142556.png)

## 项目描述

本项目为**房间预约与流动统计**，应用功能不局限于预约与流动统计，主要功能包含消息通知、公告、房间预约、通用申请处理、房间签到签出、人员人数与人次统计、签到时长统计、报表统计、基础功能等。

基础功能主要有：统一异常处理、统一请求结果返回、统一请求异常处理、wx请求工具包基础等。

技术栈：

- 管理端：Vue、element-ui等、基于vue-element-admin框架开发。
- 客户端：基于uni-app开发，组件使用图鸟UI。
- 后端：springBoot、mybatis-dynamic-sql、redis、mysql、sa-token等。

本项目分为：

- 客户端：[apply-room-record-app](https://github.com/MuShanYu/apply-room-record-app) 小程序
- 管理端：[apply-room-record-admin](https://github.com/MuShanYu/apply-room-record-admin) 
- 后端：[apply-room-record](https://github.com/MuShanYu/apply-room-record) 

## 本地运行说明

### 1. 后端

- 首先将代码clone到您的本地，使用集成开发工具打开项目，如idea。
- 配置本地开发环境，要求使用jdk11，数据库使用mysql8，redis。
- 环境配置完成后修改配置文件，包括数据库配置（账号密码）、redis配置、邮箱账号密码配置（如没有请去对应邮箱官网申请）。
- 运行db_create.sql，创建基础数据库。
- 运行项目，查看本地数据库是否成功生成数据表。
- 下载测试数据：[测试数据](https://mushanyu-app-arr.oss-cn-beijing.aliyuncs.com/static/apply_room_record.sql)
- 运行测试数据脚本。（必须，因为包含了配置信息）

### 2. 客户端

可以百度搜搜uniapp开发小程序步骤，以及需要的工具。

- Hbuildx

- 微信开发者工具

### 3. 管理端

- 将代码clone到本地。
- `npm install`

## 部署说明

后端项目提供完整dockerfile构建应用镜像

## 开源协议

项目使用MIT开源协议。

## 联系方式

QQ交流群

![image-20240226155137983](https://mushanyu-note.oss-cn-beijing.aliyuncs.com/mk-image/image-20240226155137983.png)

@MuShanYu：wx：wxid_f5ayc9d2mken22
