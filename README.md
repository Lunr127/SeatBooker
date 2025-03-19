# 自习座位预约系统

## 项目介绍

自习座位预约系统是一个为高校学生提供自习室座位预约服务的应用，旨在提升各类自习室座位使用率。通过此系统，学生可以便捷地预约自习座位，管理员可以高效地管理自习室资源。

## 技术栈

### 前端
- 微信小程序

### 后端
- Java Spring Boot 2.7.10
- MyBatis Plus 3.5.2
- Spring Security + JWT
- Redis
- WebSocket

### 数据库
- MySQL 8.0

## 系统功能

- 用户注册/登录
- 自习室查询与筛选
- 座位预约
- 二维码签到
- 预约提醒
- 违约记录与处理
- 管理员功能

## 环境配置

### 后端

1. 安装Java 11+
2. 安装MySQL 8.0+
3. 安装Redis
4. 导入数据库初始化脚本：`backend/src/main/resources/db/init.sql`
5. 修改配置文件：`backend/src/main/resources/application.yml`
6. 启动Spring Boot应用

### 前端

1. 安装微信开发者工具
2. 导入frontend目录
3. 配置小程序appid
4. 修改api地址

## 启动项目

### 后端启动
```shell
cd backend
mvn spring-boot:run
```

### 前端调试
使用微信开发者工具打开frontend目录，点击编译即可。

## 项目结构

```
├── backend                        # 后端代码
│   ├── src                        
│   │   ├── main                   
│   │   │   ├── java               # Java代码
│   │   │   │   └── com/example/seatbooker
│   │   │   │       ├── common     # 公共类
│   │   │   │       ├── config     # 配置类
│   │   │   │       ├── controller # 控制器
│   │   │   │       ├── dto        # 数据传输对象
│   │   │   │       ├── entity     # 实体类
│   │   │   │       ├── mapper     # MyBatis映射接口
│   │   │   │       ├── security   # 安全相关
│   │   │   │       ├── service    # 服务接口与实现
│   │   │   │       └── util       # 工具类
│   │   │   └── resources          # 资源文件
│   │   │       ├── db             # 数据库脚本
│   │   │       └── mapper         # MyBatis XML映射文件
│   │   └── test                   # 测试代码
│   └── pom.xml                    # Maven配置
└── frontend                       # 微信小程序前端代码
    ├── pages                      # 小程序页面
    ├── components                 # 组件
    ├── images                     # 图片资源
    ├── utils                      # 工具类
    ├── app.js                     # 小程序入口
    ├── app.json                   # 小程序配置
    └── app.wxss                   # 全局样式
```