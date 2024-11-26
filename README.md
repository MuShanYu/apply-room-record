# 房间预约与流动统计后端

## 前言

本项目为单体项目，集成常用配置，包括统一请求结果返回、统一请求异常处理、项目统一配置、通用权限（按钮级别）等等，鉴权使用Sa-Token。您可以将其作为一个单体项目开发框架，可以大大缩减您的开发时间，或者您也可以抽取其中的部分功能进行学习或者二次开发。如果本项目对您有作用，您可以点一个star，感谢！

## 项目功能

- 在线实时消息通知与聊天
- 公告
- 房间预约
- 通用事项申请与处理
- 房间签到
- 统计签到时长与人数、人次
- 后端页面报表
- 通用权限解决方案（按钮级别）
- 用户在线监控
- 系统监控
- 等等.......

服务端包括常用的统一请求结果返回、统一请求异常处理、wx请求工具包基础、项目统一配置、Redis工具等等。

本项目提供完整小程序客户端，后台管理客户端，**页面精美**。

## 项目技术点

### 3. 服务端

- JDK17
- Redis
- MyBatis-Dynamic-SQL
- SpringBoot
- SpringBoot-JPA
- Netty
- Sa-Token
- Hu-Tool
- EasyExcel、FastJson
- 等等......

本项目分为：

- 后端：[apply-room-record](https://github.com/MuShanYu/apply-room-record) 

## 服务端本地运行说明

1. 首先将代码clone到您的本地，使用集成开发工具打开项目，如idea。
2. 配置本地开发环境，要求使用jdk17，数据库使用mysql8，redis。
3. 环境配置完成后修改配置文件，包括数据库配置（账号密码）、redis配置、邮箱账号密码配置（如没有请去对应邮箱官网申请）。
4. 运行resource/db/db_create.sql，创建基础数据库。
5. 运行项目，查看本地数据库是否成功生成数据表。
6. 运行项目下的init_data.sql脚本插入初始化数据。

## 项目演示

后台管理客户端：https://www.mushanyu.xyz

登录账号：202300001，密码：123456

小程序客户端界面预览：

![image-20241122142648511](https://mushanyu-note.oss-cn-beijing.aliyuncs.com/mk-image/image-20241122142648511.png)

![image-20241122142726997](https://mushanyu-note.oss-cn-beijing.aliyuncs.com/mk-image/image-20241122142726997.png)

![image-20241122142758062](https://mushanyu-note.oss-cn-beijing.aliyuncs.com/mk-image/image-20241122142758062.png)

## 部署说明

前后端项目提供完整dockerfile构建应用镜像，您也可以参考提供的Jenkfile构建自己的流水线。

## 开源协议

项目使用MIT开源协议。

## 联系方式

QQ交流群：811043258

作者微信：@MuShanYu：wx：wxid_f5ayc9d2mken22
