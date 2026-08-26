# JDemo 电商平台

基于 Spring Boot 3 + MyBatis + Redis 的仿京东电商全栈项目（前后端分离），从数据库设计到部署上线全程独立完成。

## 在线体验
- C 端：https://jdemotoc.devbug.icu （演示账号 Tom / 123456）
- B 端：https://jdemotob.devbug.icu （演示账号 admin / admin123）

## 技术栈
Java 17 · Spring Boot 3.3.5 · MyBatis · MySQL · Redis · Spring Security + JJWT · PageHelper · Hutool · Docker · Nginx · HTTPS

## 功能
- C 端：注册登录、商品浏览/搜索/分页、购物车、下单（防超卖）、优惠券、订单管理、评价
- B 端：商品 / 分类 / 订单 / 优惠券 / 用户管理

## 核心亮点
1. **认证鉴权**：对比 Session 共享与手写拦截器，选用 Spring Security + JJWT 无状态认证，统一 JWT 过滤器 + URL 级与方法级双重鉴权，C/B 端隔离
2. **防超卖**：Redis 分布式锁串行化扣库存 + 数据库条件更新（stock ≥ 数量）兜底，双层防护
3. **超时关单**：对比 RabbitMQ 延迟队列与定时任务，权衡部署维护成本后选用 @Scheduled 扫描 + 事务内取消订单并回滚库存
4. **缓存**：空值缓存防穿透、互斥锁防击穿、随机 TTL 防雪崩
5. **工程化**：雪花算法订单号、地址快照、Spring 事件异步解耦、BCrypt 密码加密、手机号脱敏
6. **部署**：Docker Compose + Nginx 反向代理 + HTTPS，阿里云公网上线

## 本地启动
1. 安装 JDK 17、Maven，启动 MySQL（导入 `ecommerce_demo_dump.sql`）与 Redis
2. 通过环境变量配置（不设置则使用 `application.properties` 中的本地默认值）：
   - `MYSQL_USERNAME` / `MYSQL_PASSWORD`
   - `JWT_SECRET`（生产环境必须设置，仓库内不存真实密钥）
3. 启动：`./mvnw spring-boot:run`（Windows 用 `mvnw.cmd spring-boot:run`）

## 部署
`deploy/` 目录包含 Dockerfile、docker-compose.yml、nginx.conf：

```bash
docker compose up -d --build
```

## 项目结构
```
src/main/java/com/example/jingdongdemo
├── controller/   # 接口层（C 端 + B 端）
├── service/      # 业务层（含实现类）
├── mapper/       # MyBatis 数据访问层
├── entity/ dto/ vo/  # 数据模型 / 请求 / 响应
├── config/       # Security / Redis / JWT 配置
├── common/       # 统一响应、全局异常、工具类
├── task/         # 定时任务（超时关单）
└── listener/     # 事件监听（下单异步解耦）
```
