-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ecommerce_demo
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_address`
--

DROP TABLE IF EXISTS `t_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_address` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `consignee` varchar(64) NOT NULL COMMENT '收货人',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `province` varchar(32) NOT NULL COMMENT '省',
  `city` varchar(32) NOT NULL COMMENT '市',
  `district` varchar(32) NOT NULL COMMENT '区/县',
  `detail` varchar(255) NOT NULL COMMENT '详细地址',
  `is_default` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认地址：0否 1是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_address`
--

LOCK TABLES `t_address` WRITE;
/*!40000 ALTER TABLE `t_address` DISABLE KEYS */;
INSERT INTO `t_address` VALUES (2,1,'张三','13800138000','广东省','深圳市','南山区','科技园XX号',1,'2026-07-25 11:37:56','2026-07-25 13:59:25'),(3,1,'李四','13800138000','广东省','惠州市','惠阳区','土湖路',0,'2026-07-25 11:39:20','2026-07-25 13:50:09'),(4,1,'王五','13800138000','广东省','惠州市','惠城区','体育南路',0,'2026-07-25 13:42:17','2026-07-25 13:50:09'),(5,1,'王五','13800138000','广东省','惠州市','惠城区','体育南路',0,'2026-07-25 13:49:08','2026-07-25 13:50:09'),(6,2,'后勤部','11231231232','的','的','的','1的',1,'2026-08-03 14:43:21','2026-08-03 14:43:21');
/*!40000 ALTER TABLE `t_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_admin`
--

DROP TABLE IF EXISTS `t_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_admin` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码（加密存储）',
  `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
  `role` tinyint NOT NULL DEFAULT '2' COMMENT '角色：1超级管理员 2普通管理员',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0禁用 1正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_admin`
--

LOCK TABLES `t_admin` WRITE;
/*!40000 ALTER TABLE `t_admin` DISABLE KEYS */;
INSERT INTO `t_admin` VALUES (1,'admin','$2a$10$kE91fNvk54wFi/sO3sYIFOM3K7wpt48tlJw9HcczvSbEaDSPI4Hn6','超级管理员',1,1,NULL,'2026-07-18 01:58:02','2026-08-01 22:10:34');/*!40000 ALTER TABLE `t_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_cart`
--

DROP TABLE IF EXISTS `t_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_cart` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `product_id` bigint unsigned NOT NULL COMMENT '商品ID',
  `sku_id` bigint unsigned NOT NULL COMMENT 'SKU ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `checked` tinyint NOT NULL DEFAULT '1' COMMENT '是否选中：0否 1是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_product_sku` (`user_id`,`product_id`,`sku_id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_cart`
--

LOCK TABLES `t_cart` WRITE;
/*!40000 ALTER TABLE `t_cart` DISABLE KEYS */;
INSERT INTO `t_cart` VALUES (18,5,1,1,2,1,'2026-08-03 14:38:41','2026-08-03 14:38:41');
/*!40000 ALTER TABLE `t_cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_category`
--

DROP TABLE IF EXISTS `t_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '父分类ID，0为根分类',
  `name` varchar(64) NOT NULL COMMENT '分类名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标URL',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序值，越小越靠前',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0下架 1正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_category`
--

LOCK TABLES `t_category` WRITE;
/*!40000 ALTER TABLE `t_category` DISABLE KEYS */;
INSERT INTO `t_category` VALUES (1,0,'手机数码','',2,1,'2026-07-18 01:58:02','2026-08-09 17:15:24'),(2,0,'家用电器','',1,1,'2026-07-18 01:58:02','2026-08-09 17:15:24'),(3,0,'电脑办公','',3,1,'2026-07-18 01:58:02','2026-08-09 17:15:24'),(4,1,'手机','',2,1,'2026-07-18 01:58:02','2026-08-03 15:41:17'),(5,1,'耳机','',2,1,'2026-07-18 01:58:02','2026-08-03 15:41:25'),(6,2,'电视','',0,1,'2026-07-18 01:58:02','2026-08-03 15:48:41'),(7,2,'空调','',1,1,'2026-07-18 01:58:02','2026-08-03 15:48:41'),(8,3,'笔记本',NULL,1,1,'2026-07-18 01:58:02','2026-07-18 01:58:02'),(9,3,'键鼠',NULL,2,1,'2026-07-18 01:58:02','2026-07-18 01:58:02'),(10,0,'药品','',0,1,'2026-08-03 15:30:50','2026-08-09 17:15:24');
/*!40000 ALTER TABLE `t_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_coupon`
--

DROP TABLE IF EXISTS `t_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_coupon` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `name` varchar(64) NOT NULL COMMENT '券名称',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '类型：1满减 2折扣 3无门槛',
  `discount_value` decimal(10,2) NOT NULL COMMENT '优惠值（满减/无门槛为金额，折扣为折扣率）',
  `min_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '使用门槛金额',
  `total_count` int NOT NULL DEFAULT '0' COMMENT '发放总量',
  `receive_count` int NOT NULL DEFAULT '0' COMMENT '已领取数量',
  `start_time` datetime NOT NULL COMMENT '生效时间',
  `end_time` datetime NOT NULL COMMENT '失效时间',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0禁用 1启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='优惠券表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_coupon`
--

LOCK TABLES `t_coupon` WRITE;
/*!40000 ALTER TABLE `t_coupon` DISABLE KEYS */;
INSERT INTO `t_coupon` VALUES (1,'满10000减500',1,500.00,10000.00,1000,2,'2026-07-01 00:00:00','2026-12-31 00:00:00',1,'2026-08-01 12:18:04','2026-08-03 16:07:20'),(2,'满5000减200',1,200.00,5000.00,500,1,'2026-07-01 00:00:00','2026-12-31 00:00:00',1,'2026-08-01 12:18:04','2026-08-03 14:38:57'),(3,'9折券',2,0.90,0.00,300,1,'2026-07-01 00:00:00','2026-12-31 00:00:00',1,'2026-08-01 12:18:04','2026-08-03 14:38:58'),(4,'无门槛10元',3,10.00,0.00,2000,1,'2026-07-01 00:00:00','2026-12-31 00:00:00',1,'2026-08-01 12:18:04','2026-08-03 14:38:58');
/*!40000 ALTER TABLE `t_coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_order`
--

DROP TABLE IF EXISTS `t_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_order` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号（业务唯一）',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品总金额',
  `freight` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '运费',
  `pay_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实付金额',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态：0待付款 1待发货 2待收货 3已完成 4已取消 5已退款',
  `address_snapshot` text NOT NULL COMMENT '收货地址快照（JSON）',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `close_time` datetime DEFAULT NULL COMMENT '关闭时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '订单备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `request_id` VARCHAR(64) DEFAULT NULL COMMENT '幂等请求ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  UNIQUE KEY uk_request_id (request_id),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_order`
--

LOCK TABLES `t_order` WRITE;
/*!40000 ALTER TABLE `t_order` DISABLE KEYS */;
INSERT INTO `t_order` VALUES (5,'OD2084169503348477952',2,6598.00,0.00,6598.00,3,'{\"id\":6,\"userId\":2,\"consignee\":\"后勤部\",\"phone\":\"11231231232\",\"province\":\"的\",\"city\":\"的\",\"district\":\"的\",\"detail\":\"1的\",\"isDefault\":1,\"createTime\":1785739401000,\"updateTime\":1785739401000}','2026-08-03 14:48:50','2026-08-03 15:22:31','2026-08-03 15:22:52',NULL,'','2026-08-03 14:48:26','2026-08-03 15:22:52'),(6,'OD2084170913540591616',2,13196.00,0.00,13196.00,4,'{\"id\":6,\"userId\":2,\"consignee\":\"后勤部\",\"phone\":\"11231231232\",\"province\":\"的\",\"city\":\"的\",\"district\":\"的\",\"detail\":\"1的\",\"isDefault\":1,\"createTime\":1785739401000,\"updateTime\":1785739401000}',NULL,NULL,NULL,'2026-08-03 14:54:06','','2026-08-03 14:54:02','2026-08-03 14:54:06'),(7,'OD2084170976023138304',2,16495.00,0.00,16495.00,3,'{\"id\":6,\"userId\":2,\"consignee\":\"后勤部\",\"phone\":\"11231231232\",\"province\":\"的\",\"city\":\"的\",\"district\":\"的\",\"detail\":\"1的\",\"isDefault\":1,\"createTime\":1785739401000,\"updateTime\":1785739401000}','2026-08-03 14:54:22','2026-08-03 15:16:44','2026-08-03 15:22:54',NULL,'','2026-08-03 14:54:17','2026-08-03 15:22:54'),(8,'OD2084171619282571264',2,32596.00,0.00,32596.00,4,'{\"id\":6,\"userId\":2,\"consignee\":\"后勤部\",\"phone\":\"11231231232\",\"province\":\"的\",\"city\":\"的\",\"district\":\"的\",\"detail\":\"1的\",\"isDefault\":1,\"createTime\":1785739401000,\"updateTime\":1785739401000}',NULL,NULL,NULL,'2026-08-03 14:57:53','','2026-08-03 14:56:50','2026-08-03 14:57:53'),(9,'OD2084172104563544064',2,6598.00,0.00,6598.00,4,'{\"id\":6,\"userId\":2,\"consignee\":\"后勤部\",\"phone\":\"11231231232\",\"province\":\"的\",\"city\":\"的\",\"district\":\"的\",\"detail\":\"1的\",\"isDefault\":1,\"createTime\":1785739401000,\"updateTime\":1785739401000}',NULL,NULL,NULL,'2026-08-03 15:07:52','','2026-08-03 14:58:46','2026-08-03 15:07:52'),(10,'OD2084189383997009920',1,13498.00,0.00,13498.00,3,'{\"id\":2,\"userId\":1,\"consignee\":\"张三\",\"phone\":\"13800138000\",\"province\":\"广东省\",\"city\":\"深圳市\",\"district\":\"南山区\",\"detail\":\"科技园XX号\",\"isDefault\":1,\"createTime\":1784950676000,\"updateTime\":1784959165000}','2026-08-03 16:07:32','2026-08-03 16:07:46','2026-08-03 16:07:55',NULL,'','2026-08-03 16:07:26','2026-08-03 16:07:55');
/*!40000 ALTER TABLE `t_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_order_item`
--

DROP TABLE IF EXISTS `t_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_order_item` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `product_id` bigint unsigned NOT NULL COMMENT '商品ID',
  `sku_id` bigint unsigned NOT NULL COMMENT 'SKU ID',
  `product_name` varchar(128) NOT NULL COMMENT '商品名称快照',
  `sku_spec` varchar(255) DEFAULT NULL COMMENT '规格快照',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片快照',
  `price` decimal(10,2) NOT NULL COMMENT '购买单价快照',
  `quantity` int NOT NULL COMMENT '购买数量',
  `subtotal` decimal(10,2) NOT NULL COMMENT '小计金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_order_item`
--

LOCK TABLES `t_order_item` WRITE;
/*!40000 ALTER TABLE `t_order_item` DISABLE KEYS */;
INSERT INTO `t_order_item` VALUES (5,5,'OD2084169503348477952',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:48:26'),(6,5,'OD2084169503348477952',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:48:26'),(7,6,'OD2084170913540591616',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:54:02'),(8,6,'OD2084170913540591616',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:54:02'),(9,6,'OD2084170913540591616',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:54:02'),(10,6,'OD2084170913540591616',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:54:02'),(11,7,'OD2084170976023138304',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:54:17'),(12,7,'OD2084170976023138304',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:54:17'),(13,7,'OD2084170976023138304',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:54:17'),(14,7,'OD2084170976023138304',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:54:17'),(15,7,'OD2084170976023138304',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:54:17'),(16,8,'OD2084171619282571264',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:56:50'),(17,8,'OD2084171619282571264',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:56:50'),(18,8,'OD2084171619282571264',26,36,'ThinkPad X1 Carbon Gen13','内存:32+1TB',NULL,12999.00,1,12999.00,'2026-08-03 14:56:50'),(19,8,'OD2084171619282571264',26,36,'ThinkPad X1 Carbon Gen13','内存:32+1TB',NULL,12999.00,1,12999.00,'2026-08-03 14:56:50'),(20,9,'OD2084172104563544064',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:58:46'),(21,9,'OD2084172104563544064',19,28,'TCL空调 小蓝翼 1.5匹','规格:1.5匹',NULL,3299.00,1,3299.00,'2026-08-03 14:58:46'),(22,10,'OD2084189383997009920',34,47,'vivo X200 Ultra','颜色:白;存储:512GB',NULL,6999.00,1,6999.00,'2026-08-03 16:07:26'),(23,10,'OD2084189383997009920',34,46,'vivo X200 Ultra','颜色:黑;存储:256GB',NULL,6499.00,1,6499.00,'2026-08-03 16:07:26');
/*!40000 ALTER TABLE `t_order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_payment`
--

DROP TABLE IF EXISTS `t_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_payment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `pay_no` varchar(64) NOT NULL COMMENT '支付流水号（第三方）',
  `pay_channel` tinyint NOT NULL DEFAULT '1' COMMENT '支付渠道：1支付宝 2微信 3余额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态：0待支付 1成功 2失败 3已退款',
  `pay_time` datetime DEFAULT NULL COMMENT '支付完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_no` (`pay_no`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_payment`
--

LOCK TABLES `t_payment` WRITE;
/*!40000 ALTER TABLE `t_payment` DISABLE KEYS */;
INSERT INTO `t_payment` VALUES (1,'OD2082688450762993664',1,'PAY2084121533395521536',1,21998.00,1,'2026-08-03 11:37:49','2026-08-03 11:37:49','2026-08-03 11:37:49'),(2,'OD2084169503348477952',2,'PAY2084169605400088576',1,6598.00,1,'2026-08-03 14:48:51','2026-08-03 14:48:50','2026-08-03 14:48:50'),(3,'OD2084170976023138304',2,'PAY2084170997741244416',1,16495.00,1,'2026-08-03 14:54:23','2026-08-03 14:54:22','2026-08-03 14:54:22'),(4,'OD2084189383997009920',1,'PAY2084189409016033280',3,13498.00,1,'2026-08-03 16:07:32','2026-08-03 16:07:32','2026-08-03 16:07:32');
/*!40000 ALTER TABLE `t_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_product`
--

DROP TABLE IF EXISTS `t_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_product` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `category_id` bigint unsigned NOT NULL COMMENT '分类ID',
  `name` varchar(128) NOT NULL COMMENT '商品名称',
  `subtitle` varchar(255) DEFAULT NULL COMMENT '副标题',
  `main_image` varchar(255) DEFAULT NULL COMMENT '主图URL',
  `sub_images` text COMMENT '子图URL列表，JSON数组',
  `detail` text COMMENT '商品详情（富文本HTML）',
  `stock` int NOT NULL DEFAULT '0' COMMENT '总库存',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0下架 1在售 2预售',
  `sales` int NOT NULL DEFAULT '0' COMMENT '销量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品SPU表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_product`
--

LOCK TABLES `t_product` WRITE;
/*!40000 ALTER TABLE `t_product` DISABLE KEYS */;
INSERT INTO `t_product` VALUES (1,4,'iPhone 16 Pro Max','A18 Pro芯片，钛金属设计','https://picsum.photos/seed/p1/600/600',NULL,NULL,500,1,3200,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(2,4,'华为Mate 70 Pro','麒麟9100，卫星通信','https://picsum.photos/seed/p2/600/600',NULL,NULL,300,1,4500,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(3,4,'小米15 Ultra','骁龙8Gen4，徕卡光学','https://picsum.photos/seed/p3/600/600',NULL,NULL,400,1,2900,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(4,4,'OPPO Find X8 Pro','天玑9400，哈苏影像','https://picsum.photos/seed/p4/600/600',NULL,NULL,300,1,2100,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(5,4,'三星Galaxy S25 Ultra','钛金属，S Pen','https://picsum.photos/seed/p5/600/600',NULL,NULL,200,1,1600,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(6,5,'AirPods Pro 3','主动降噪，自适应音频','https://picsum.photos/seed/p6/600/600',NULL,NULL,1200,1,8900,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(7,5,'Sony WH-1000XM6','行业标杆降噪','https://picsum.photos/seed/p7/600/600',NULL,NULL,400,1,2100,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(8,5,'华为FreeBuds Pro 4','星闪连接，静谧通话','https://picsum.photos/seed/p8/600/600',NULL,NULL,800,1,5600,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(9,5,'Bose QC Ultra Earbuds','沉浸空间音频','https://picsum.photos/seed/p9/600/600',NULL,NULL,450,1,3800,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(10,5,'漫步者NeoBuds Pro 3','Hi-Res双金标','https://picsum.photos/seed/p10/600/600',NULL,NULL,500,1,4100,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(11,6,'小米电视 S Pro 75','MiniLED，144Hz高刷','https://picsum.photos/seed/p11/600/600',NULL,NULL,200,1,1500,'2026-08-03 14:09:01','2026-08-03 15:29:51'),(12,6,'TCL 85Q10K Pro','MiniLED，量子点','https://picsum.photos/seed/p12/600/600',NULL,NULL,100,1,680,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(13,6,'海信U8N 75英寸','ULED X，信芯AI画质','https://picsum.photos/seed/p13/600/600',NULL,NULL,80,1,520,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(14,6,'创维壁纸电视 75Q53','无缝贴墙，独立主机','https://picsum.photos/seed/p14/600/600',NULL,NULL,60,1,340,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(15,6,'雷鸟鹤7 MAX 85寸','MiniLED全域光晕控制','https://picsum.photos/seed/p15/600/600',NULL,NULL,70,1,280,'2026-08-03 14:09:01','2026-08-03 14:09:01'),(16,7,'格力空调 云佳 1.5匹','新一级能效，变频冷暖','https://picsum.photos/seed/p16/600/600',NULL,NULL,600,1,3700,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(17,7,'美的空调 酷省电 1.5匹','一晚低至1度电','https://picsum.photos/seed/p17/600/600',NULL,NULL,500,1,4300,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(18,7,'海尔空调 静悦 3匹','56℃除菌自清洁','https://picsum.photos/seed/p18/600/600',NULL,NULL,200,1,1800,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(19,7,'TCL空调 小蓝翼 1.5匹','新风空调，增氧不闷','https://picsum.photos/seed/p19/600/600',NULL,NULL,350,1,1500,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(20,8,'MacBook Air 15 M4','M4芯片，Liquid Retina屏','https://picsum.photos/seed/p20/600/600',NULL,NULL,150,1,980,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(21,8,'华为MateBook X Pro 2026','3.1K OLED触控屏','https://picsum.photos/seed/p21/600/600',NULL,NULL,120,1,750,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(22,8,'小米Book Pro 16','3.2K 165Hz大师屏','https://picsum.photos/seed/p22/600/600',NULL,NULL,180,1,1100,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(23,8,'联想YOGA Pro 14s','PureBright 3K屏','https://picsum.photos/seed/p23/600/600',NULL,NULL,100,1,630,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(24,8,'华硕灵耀14 2026','酷睿Ultra9，1.1kg','https://picsum.photos/seed/p24/600/600',NULL,NULL,130,1,560,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(25,8,'戴尔XPS 14','骁龙X Elite，21h续航','https://picsum.photos/seed/p25/600/600',NULL,NULL,80,1,310,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(26,8,'ThinkPad X1 Carbon Gen13','商务旗舰，轻至1.08kg','https://picsum.photos/seed/p26/600/600',NULL,NULL,80,1,620,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(27,8,'ROG 幻16 Air','RTX4070，2.5K OLED','https://picsum.photos/seed/p27/600/600',NULL,NULL,60,1,420,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(28,9,'罗技 MX Master 4','电磁滚轮，跨设备控制','https://picsum.photos/seed/p28/600/600',NULL,NULL,1000,1,4200,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(29,9,'樱桃MX 8.3 无线键盘','CHERRY MX2A轴，铝合金机身','https://picsum.photos/seed/p29/600/600',NULL,NULL,350,1,1100,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(30,9,'雷蛇蝰蛇V4 Pro','3950传感器，8K轮询','https://picsum.photos/seed/p30/600/600',NULL,NULL,350,1,2200,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(31,9,'罗技G Pro X 2键盘','热插拔，Lightspeed无线','https://picsum.photos/seed/p31/600/600',NULL,NULL,200,1,980,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(32,9,'ROG龙鳞ACE MINI','54g超轻，SpeedNova','https://picsum.photos/seed/p32/600/600',NULL,NULL,250,1,870,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(33,9,'Keychron Q3 Max','Gasket结构，铝坨坨','https://picsum.photos/seed/p33/600/600',NULL,NULL,180,1,620,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(34,4,'vivo X200 Ultra','蔡司2亿APO超级长焦','https://picsum.photos/seed/p34/600/600',NULL,NULL,250,1,1800,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(35,4,'荣耀Magic7 RSR','保时捷设计，青海湖电池','https://picsum.photos/seed/p35/600/600',NULL,NULL,150,1,950,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(36,5,'OPPO Enco X3','丹拿联合调音','https://picsum.photos/seed/p36/600/600',NULL,NULL,600,1,3200,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(37,10,'谷维素片','','',NULL,'',20.00,0,1,0,'2026-08-03 16:01:53','2026-08-03 16:01:53');
/*!40000 ALTER TABLE `t_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_product_sku`
--

DROP TABLE IF EXISTS `t_product_sku`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_product_sku` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `product_id` bigint unsigned NOT NULL COMMENT '商品ID',
  `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码',
  `spec` varchar(255) NOT NULL COMMENT '规格描述，如"颜色:红色;尺寸:L"',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT 'SKU价格',
  `stock` int NOT NULL DEFAULT '0' COMMENT 'SKU库存',
  `image` varchar(255) DEFAULT NULL COMMENT 'SKU图片URL',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0禁用 1启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_code` (`sku_code`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品SKU表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_product_sku`
--

LOCK TABLES `t_product_sku` WRITE;
/*!40000 ALTER TABLE `t_product_sku` DISABLE KEYS */;
INSERT INTO `t_product_sku` VALUES (1,1,'IP16PM-256-BLK','颜色:黑色;存储:256GB',9999.00,200,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(2,1,'IP16PM-512-WHT','颜色:白色;存储:512GB',10999.00,150,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(3,1,'IP16PM-1TB-NAT','颜色:原色钛;存储:1TB',12999.00,150,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(4,2,'M70P-256-BLK','颜色:曜金黑;存储:256GB',6999.00,150,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(5,2,'M70P-512-WHT','颜色:羽砂白;存储:512GB',7999.00,150,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(6,3,'MI15U-256-BLK','颜色:黑;存储:256GB',6499.00,200,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(7,3,'MI15U-512-WHT','颜色:白;存储:512GB',6999.00,200,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(8,4,'OPPOX8-256-BLK','颜色:黑;存储:256GB',5999.00,150,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(9,4,'OPPOX8-512-BLU','颜色:蓝;存储:512GB',6499.00,150,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(10,5,'S25U-256-BLK','颜色:黑;存储:256GB',9699.00,100,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(11,5,'S25U-512-GRY','颜色:灰;存储:512GB',10199.00,100,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(12,6,'APP3-WHT','颜色:白色',1899.00,1200,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(13,7,'XM6-BLK','颜色:黑色',2499.00,300,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(14,7,'XM6-SLV','颜色:银色',2499.00,100,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(15,8,'HWB4-WHT','颜色:白色',1399.00,500,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(16,8,'HWB4-BLK','颜色:黑色',1399.00,300,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(17,9,'BOSE-BLK','颜色:黑色',2099.00,250,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(18,9,'BOSE-WHT','颜色:白色',2099.00,200,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(19,10,'EDIFIER3-BLK','颜色:黑色',799.00,500,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(20,11,'MITV75-STD','规格:标准版',5999.00,200,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(21,12,'TCL85-STD','规格:85寸',9999.00,100,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(22,13,'HISENSE75-STD','规格:75寸',8999.00,80,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(23,14,'SKYWORTH-STD','规格:75寸',7999.00,60,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(24,15,'THUNDER-STD','规格:85寸',7499.00,70,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(25,16,'GREE15-STD','规格:1.5匹',2999.00,600,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(26,17,'MIDEA15-STD','规格:1.5匹',2799.00,500,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(27,18,'HAIER3-STD','规格:3匹',5999.00,200,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(28,19,'TCLAC-STD','规格:1.5匹',3299.00,343,NULL,1,'2026-08-03 14:09:02','2026-08-03 15:07:52'),(29,20,'MBA15-16-512','颜色:午夜黑;内存:16+512',10999.00,80,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(30,20,'MBA15-24-1TB','颜色:星光色;内存:24+1TB',13499.00,70,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(31,21,'MATEBOOK-16-512','内存:16+512',11999.00,120,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(32,22,'MIBOOK16-16-1TB','内存:16+1TB',7999.00,180,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(33,23,'YOGA14-16-1TB','内存:16+1TB',8999.00,100,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(34,24,'ASUS14-16-1TB','内存:16+1TB',8499.00,130,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(35,25,'DELLXPS-16-512','内存:16+512',11999.00,80,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(36,26,'X1C13-32-1TB','内存:32+1TB',12999.00,80,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:57:53'),(37,27,'ROG16-32-1TB','内存:32+1TB',14999.00,60,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(38,28,'MXM4-BLK','颜色:石墨黑',799.00,700,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(39,28,'MXM4-WHT','颜色:珍珠白',799.00,300,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(40,29,'CH83-BLK-RED','颜色:黑;轴体:红轴',1499.00,200,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(41,29,'CH83-WHT-BRN','颜色:白;轴体:茶轴',1499.00,150,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(42,30,'RAZER4-BLK','颜色:黑',1099.00,350,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(43,31,'GPROX2-BLK','轴体:红轴',1699.00,200,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(44,32,'ROGACE-BLK','颜色:黑',899.00,250,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(45,33,'KEYCHRON-BLK','轴体:红轴',1299.00,180,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(46,34,'VIVOX200-256-BLK','颜色:黑;存储:256GB',6499.00,119,NULL,1,'2026-08-03 14:09:02','2026-08-03 16:07:26'),(47,34,'VIVOX200-512-WHT','颜色:白;存储:512GB',6999.00,129,NULL,1,'2026-08-03 14:09:02','2026-08-03 16:07:26'),(48,35,'HONOR7-512-BLK','颜色:黑;存储:512GB',7999.00,150,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02'),(49,36,'ENCOX3-BLK','颜色:黑',999.00,600,NULL,1,'2026-08-03 14:09:02','2026-08-03 14:09:02');
/*!40000 ALTER TABLE `t_product_sku` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_review`
--

DROP TABLE IF EXISTS `t_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_review` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `product_id` bigint unsigned NOT NULL COMMENT '商品ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `rating` tinyint NOT NULL DEFAULT '5' COMMENT '评分：1-5星',
  `content` text COMMENT '评价内容',
  `images` text COMMENT '评价图片URL列表，JSON数组',
  `is_anonymous` tinyint NOT NULL DEFAULT '0' COMMENT '是否匿名：0否 1是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_review`
--

LOCK TABLES `t_review` WRITE;
/*!40000 ALTER TABLE `t_review` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码（加密存储）',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint DEFAULT '0' COMMENT '性别：0未知 1男 2女',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0禁用 1正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user`
--

LOCK TABLES `t_user` WRITE;
/*!40000 ALTER TABLE `t_user` DISABLE KEYS */;
INSERT INTO `t_user` VALUES (1,'tom','$2a$10$d37VrvKKR.uteXAjsV8p6ORBvM1AzPNi6XCix5laxm.MZviMDhSMy','13800138000',NULL,NULL,NULL,0,1,'2026-08-03 16:01:19','2026-07-23 21:18:14','2026-08-03 16:01:19'),(2,'123','$2a$10$HEifMfnN883/YqKEFTIbP.W9gpZ4xnIZQ3gvhqhyCG8RbGwAVCe..','12312321232',NULL,NULL,NULL,0,1,'2026-08-05 13:59:17','2026-08-02 14:05:00','2026-08-05 13:59:17'),(3,'testuser','$2a$10$tU8aUOdf4dt9OybJHcn7FuF6KLVYpv.eI8AvAAuQ5sK4TrHe7tNw2','13900139000',NULL,NULL,NULL,0,1,NULL,'2026-08-02 14:12:45','2026-08-03 15:38:04'),(4,'test99','$2a$10$4x.3w2KhEI9Xa0tLy3g9fuYdb.gtyOxj9osKpzjJi1WPnhKb7eCCe','13900001111',NULL,NULL,NULL,0,1,NULL,'2026-08-03 14:23:31','2026-08-03 15:38:04'),(5,'zhangsan','$2a$10$I6uGp.0MlTWWY4jJDQi0D.dgvyMvMDNZD8nif2m6yypdHIjuqIBse','13600001111',NULL,NULL,NULL,0,1,NULL,'2026-08-03 14:33:47','2026-08-03 15:38:04');
/*!40000 ALTER TABLE `t_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_coupon`
--

DROP TABLE IF EXISTS `t_user_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_coupon` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `coupon_id` bigint unsigned NOT NULL COMMENT '优惠券ID',
  `order_no` varchar(32) DEFAULT NULL COMMENT '使用的订单编号',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0未使用 1已使用 2已过期',
  `receive_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `used_time` datetime DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_coupon_id` (`coupon_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户优惠券表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_coupon`
--

LOCK TABLES `t_user_coupon` WRITE;
/*!40000 ALTER TABLE `t_user_coupon` DISABLE KEYS */;
INSERT INTO `t_user_coupon` VALUES (1,2,1,NULL,0,'2026-08-03 14:38:56',NULL),(2,2,2,NULL,0,'2026-08-03 14:38:57',NULL),(3,2,3,NULL,0,'2026-08-03 14:38:58',NULL),(4,2,4,NULL,0,'2026-08-03 14:38:58',NULL),(5,1,1,NULL,0,'2026-08-03 16:07:20',NULL);
/*!40000 ALTER TABLE `t_user_coupon` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-09 18:38:23
