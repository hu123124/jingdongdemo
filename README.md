# JDemo 电商平台

基于 Spring Boot 3 + MyBatis + Redis + Elasticsearch 的仿京东电商全栈项目（前后端分离），从数据库设计到部署上线全程独立完成。

## 在线体验
- C 端：https://jdemotoc.devbug.icu （演示账号 Tom / 123456）
- B 端：https://jdemotob.devbug.icu （演示账号 admin / admin123）

## 技术栈
Java 17 · Spring Boot 3.3.5 · MyBatis · MySQL · Redis · Spring Security + JJWT · PageHelper · Hutool · Docker · Nginx · HTTPS · MinIO · Elasticsearch 8.17 + IK 分词

## 功能
- C 端：注册登录、商品浏览/全文检索(ES+IK+高亮)/分页、购物车、下单（防超卖）、优惠券、订单管理、评价
- B 端：商品 / 分类 / 订单 / 优惠券 / 用户管理

## 核心亮点
1. **认证鉴权**：对比 Session 共享与手写拦截器，选用 Spring Security + JJWT 无状态认证，统一 JWT 过滤器 + URL 级与方法级双重鉴权，C/B 端隔离
2. **防超卖**：Redis 分布式锁串行化扣库存 + 数据库条件更新（stock ≥ 数量）兜底，双层防护
3. **超时关单**：对比 RabbitMQ 延迟队列与定时任务，权衡部署维护成本后选用 @Scheduled 扫描 + 事务内取消订单并回滚库存
4. **缓存**：空值缓存防穿透、互斥锁防击穿、随机 TTL 防雪崩
5. **工程化**：雪花算法订单号、地址快照、Spring 事件异步解耦、BCrypt 密码加密、手机号脱敏
6. **部署**：Docker Compose + Nginx 反向代理 + HTTPS，阿里云公网上线
7. **对象存储**：基于 MinIO 将商品图片独立于应用存储、易于扩展；文件名 UUID 化防重名，类型白名单与 5MB 大小双重校验。应用启动时自动建桶并设置公开读策略，图片可直链访问，配置支持环境变量覆盖、适配多环境部署
8. **ES 全文检索**：基于 Elasticsearch 8 + IK 中文分词重构商品搜索——商品增改/上下架事务提交后经事件异步双写 ES、应用启动全量重建兜底（最终一致）；C 端关键词走 multi_match 加权检索 + 命中词高亮 + 分类/价格 filter + from/size 分页；ES 故障自动降级 + 配置开关一键切回 MySQL LIKE，搜索挂了业务不挂

## 本地启动
1. 安装 JDK 17、Maven，启动 MySQL（导入 `ecommerce_demo_dump.sql`）与 Redis；本地需先启动 MinIO（默认账号 minioadmin / minioadmin），否则应用启动时会连接失败；搜索功能依赖远端 Elasticsearch（默认 http://8.138.45.121:9200，已内置配置）
2. 通过环境变量配置（不设置则使用 `application.properties` 中的本地默认值）：
   - `MYSQL_USERNAME` / `MYSQL_PASSWORD`
   - `JWT_SECRET`（生产环境必须设置，仓库内不存真实密钥）
   - `MINIO_ENDPOINT` / `MINIO_ACCESS_KEY` / `MINIO_SECRET_KEY` / `MINIO_BUCKET`（本地默认 http://localhost:9000 / minioadmin / minioadmin / jd-images）
   - `ES_URIS` / `ES_SEARCH_ENABLED`（ES 地址与搜索开关，本地默认 8.138.45.121:9200 / true）
3. 启动：`./mvnw spring-boot:run`（Windows 用 `mvnw.cmd spring-boot:run`）

## 部署
`deploy/` 目录包含 Dockerfile、docker-compose.yml、nginx.conf：

```bash
docker compose up -d --build
```

### 部署拓扑（两台阿里云服务器）
- **A 台**：Nginx（HTTPS 反代 + 前端静态页）+ Spring Boot + MySQL + Redis（docker compose 部署，域名 devbug.icu / jdemotoc / jdemotob 反代到 C/B 端）
- **B 台**：MinIO（对象存储，域名 img.devbug.icu 反代）+ Elasticsearch 8.17 + IK 分词（9200 端口，与 MinIO 同一 Docker Compose 编排，容器内存受限 1.5G / 堆 512m）

### ES 连接与搜索降级
- 应用通过 `ES_URIS` 环境变量连 B 台 ES（默认 http://8.138.45.121:9200，A→B 公网直连）
- 商品增改/上下架 → 事务提交后事件异步双写 ES；应用启动全量重建索引兜底（MySQL 为唯一事实源）
- `ES_SEARCH_ENABLED=false` 一键关闭 ES 搜索（自动回退 MySQL LIKE）
- ⚠️ TODO（生产安全）：ES 未开启 xpack 账号认证，9200 建议云防火墙限制源 IP；当前数据为公开商品目录风险可控，商业上线前需开启安全认证

## 项目结构
```
src/main/java/com/example/jingdongdemo
├── controller/   # 接口层（C 端 + B 端）
├── service/      # 业务层（含实现类）
├── mapper/       # MyBatis 数据访问层
├── entity/ dto/ vo/  # 数据模型 / 请求 / 响应
├── document/     # ES 文档模型 + 仓储（对应 ES 索引，类比 entity 对应 MySQL 表）
├── config/       # Security / Redis / JWT 配置
├── common/       # 统一响应、全局异常、工具类
├── task/         # 定时任务（超时关单）
└── listener/     # 事件监听（下单异步解耦、商品变更同步 ES）
```
