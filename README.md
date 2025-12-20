# 用户管理系统 (User Management System)

基于Spring Boot 3.5.6开发的用户管理系统，采用现代化的技术栈实现用户注册、登录、权限管理等功能。

## 技术栈

- **核心框架**: Spring Boot 3.5.6, Java 17
- **持久层**: MyBatis Plus 3.5.6
- **数据库**: MySQL 8.x
- **数据库迁移**: Flyway
- **缓存/会话**: Redis, Spring Session (基于Redis实现分布式会话)
- **安全框架**: Spring Security 6.x (提供认证和授权功能)
- **密码加密**: BCryptPasswordEncoder (安全的密码哈希算法)
- **参数校验**: Jakarta Validation (JSR-380)
- **JSON序列化**: Jackson (支持LocalDateTime类型)
- **构建工具**: Gradle 8.x
- **热部署**: Spring Boot DevTools
- **容器化**: Docker

## 快速开始

### 环境准备

确保本地安装了以下软件：
- Java 17+
- MySQL 8.x
- Redis
- Docker (可选，用于容器化部署)

### 数据库配置

1. 创建MySQL数据库:
   ```sql
   CREATE DATABASE user CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. 在`application.yml`中配置数据库连接参数

### 启动Redis

使用Docker启动Redis服务:
```shell
docker run -d \
  --name redis-server \
  -p 6379:6379 \
  redis:latest \
  redis-server --requirepass "p@ssw0rd"
```

### 运行应用

```bash
./gradlew bootRun
```

应用将在 `http://localhost:8080/user-center` 启动

### 测试API

1. 注册新用户:
   ```bash
   curl -X POST http://localhost:8080/user-center/api/users/register \
        -H "Content-Type: application/json" \
        -d '{"account":"testuser","password":"Test123!@#","checkPassword":"Test123!@#"}'
   ```

2. 用户登录:
   ```bash
   curl -X POST http://localhost:8080/user-center/api/users/login \
        -H "Content-Type: application/json" \
        -d '{"account":"testuser","password":"Test123!@#"}'\
        --cookie-jar cookies.txt
   ```

## API接口说明

### 用户注册

- 接口：POST /api/users/register
- 入参：UserRegisterRequest对象
  - account: 账户名（6-20位字符）
  - password: 密码（8-30位字符）
  - checkPassword: 确认密码（必须与password一致）
- 返回值：创建好的账号id，如果注册失败，抛出异常
- 逻辑：
  - 入参校验
    - 账户：
      - 长度校验：6-20位字符
      - 业务逻辑校验：账户不能和已有的重复
    - 密码：
      - 长度校验：8-30位字符
      - 两次输入的密码必须一致
      - 密码使用BCrypt加密后存储在数据库

### 用户登录

- 接口：POST /api/users/login
- 入参：UserLoginRequest对象
  - account: 账户名（6-20位字符）
  - password: 密码（8-30位字符）
- 返回值：登录成功返回"Login success"，失败返回"Invalid credentials"
- 逻辑：
  - 参数校验
  - 密码正确性校验（使用BCrypt验证）
  - 记录用户的登录态（session），存到Redis中
  - 返回登录结果

### 获取当前用户

- 接口：GET /api/users/current
- 返回值：当前已登录用户信息或401未授权状态
- 逻辑：
  - 通过Spring Security获取当前认证信息
  - 如果未认证，返回401状态码
  - 如果已认证，返回用户名和权限信息

### 注销/退出登录

- 接口：POST /api/users/logout
- 请求体：无
- 返回值：无
- 逻辑：
  - 从session中获取当前用户凭据，移除凭据

### 管理员查询用户列表

- 接口：GET /api/users
- 请求体：无
- 返回值：BaseResponse包装的用户列表
- 逻辑：
  - 从Session中获取当前用户信息
  - 判断当前用户是否是管理员（role=1），如果不是管理员返回权限错误
  - 如果当前用户是管理员，查询所有用户列表
  - 返回经过脱敏处理的用户列表
- 分页（TODO）

### 管理员删除用户

- 接口：DELETE /api/users/{userId}
- 请求体：无
- 返回值：BaseResponse包装的布尔值，表示是否删除成功
- 逻辑：
  - 从Session中获取当前用户信息
  - 判断当前用户是否是管理员（role=1）
  - 如果是管理员，根据用户ID删除指定用户（逻辑删除）
  - 返回删除结果

## 数据库设计

### 用户表 (user)

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(256) | 用户昵称 |
| account | VARCHAR(256) | 账户名 |
| avatar_url | VARCHAR(256) | 头像地址 |
| gender | TINYINT | 性别 |
| password | VARCHAR(256) | 密码（加密存储） |
| phone | VARCHAR(50) | 手机号 |
| email | VARCHAR(50) | 邮箱 |
| status | INT | 状态，0-正常 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 是否删除，0-未删除，1-已删除 |
| role | INT | 用户角色，0-普通用户，1-管理员 |

## 部署

### 方式一：直接运行

构建并运行应用:
```bash
./gradlew bootRun
```

### 方式二：构建JAR包运行

构建JAR包:
```bash
./gradlew build
```

运行JAR包:
```bash
java -jar build/libs/user-management-system-0.0.1-SNAPSHOT.jar
```

### 方式三：Docker部署

构建Docker镜像:
```bash
docker build -t user-management-system .
```

运行容器:
```bash
docker run -d -p 8080:8080 user-management-system
```

## 核心技术点

### Cookie和Session机制

#### 工作原理
- 客户端在第一次请求服务端之后，服务端生成一个session，在响应体中返回给前端一个设置cookie的命令，并且返回sessionId；
- 前端根据请求的响应体，设置cookie，保存到浏览器中
- 前端再次请求服务端的时候，请求头带上cookie
- 后端根据请求头的cookie，识别到sessionId，根据sessionId获取存储的信息（用户登录状态）

#### 分布式Session
本项目使用Spring Session + Redis实现分布式Session管理，具有以下优势：
- 支持多实例部署，用户会话可在不同服务器间共享
- Session数据持久化存储，重启应用不会丢失用户登录状态
- 支持Session过期管理，默认超时时间为60分钟

### Spring Security集成

项目集成了Spring Security进行身份验证和授权管理，通过Redis存储用户会话信息，实现分布式会话管理。

#### 认证流程
1. 用户通过表单登录提交账户和密码
2. Spring Security调用CustomUserDetailService验证用户凭证
3. 验证成功后生成认证令牌并存储到Redis Session中
4. 返回认证成功的响应给客户端

#### 授权机制
- 普通用户：只能访问注册、登录、获取当前用户等基础接口
- 管理员用户：除了基础接口外，还可以访问用户列表查询、用户删除等管理接口
- 未登录用户：只能访问注册和登录接口

#### 安全特性
- 密码使用BCrypt加密存储，保证安全性
- 基于角色的访问控制（RBAC）
- CSRF保护已禁用（适用于REST API）
- Session管理策略：由Spring Session管理（Redis）