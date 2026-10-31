-- ============================================================
-- Shede B2C Platform - Schema (MySQL 8.0, utf8mb4)
-- Product -> ProductDetail -> Order -> PaymentRecord -> User
-- NOTE: `order` is a MySQL reserved word, always quote it.
-- Execute this file in your MySQL client.
-- ============================================================

CREATE DATABASE IF NOT EXISTS `shede_db`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `shede_db`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `payment_record`;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS `product_detail`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------
-- 1. user
-- ------------------------------------------------------------
CREATE TABLE `user` (
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `user_id`      VARCHAR(64)     NOT NULL                COMMENT 'business user id (UUID)',
    `username`     VARCHAR(64)     NOT NULL                COMMENT 'login name',
    `password`     VARCHAR(128)    NOT NULL                COMMENT 'password (salted hash)',
    `nickname`     VARCHAR(64)              DEFAULT NULL   COMMENT 'nickname',
    `phone`        VARCHAR(20)              DEFAULT NULL   COMMENT 'mobile',
    `email`        VARCHAR(128)             DEFAULT NULL   COMMENT 'email',
    `avatar`       VARCHAR(512)             DEFAULT NULL   COMMENT 'avatar url',
    `status`       TINYINT         NOT NULL DEFAULT 1      COMMENT '1 normal / 0 disabled',
    `gmt_created`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `gmt_modified` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'modified time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='user';

-- ------------------------------------------------------------
-- 2. product  (SPU)
-- ------------------------------------------------------------
CREATE TABLE `product` (
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `product_id`   VARCHAR(64)     NOT NULL                COMMENT 'business product id (UUID)',
    `name`         VARCHAR(128)    NOT NULL                COMMENT 'product name',
    `subtitle`     VARCHAR(255)             DEFAULT NULL   COMMENT 'selling point',
    `brand`        VARCHAR(64)              DEFAULT NULL   COMMENT 'brand',
    `category`     VARCHAR(64)              DEFAULT NULL   COMMENT 'category',
    `price`        DECIMAL(10, 2)  NOT NULL DEFAULT 0.00   COMMENT 'sale price',
    `stock`        INT             NOT NULL DEFAULT 0      COMMENT 'total stock (sum of SKU)',
    `sales`        INT             NOT NULL DEFAULT 0      COMMENT 'sold count',
    `main_image`   VARCHAR(512)             DEFAULT NULL   COMMENT 'main image url',
    `status`       TINYINT         NOT NULL DEFAULT 1      COMMENT '1 on-shelf / 0 off-shelf',
    `gmt_created`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `gmt_modified` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'modified time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_id` (`product_id`),
    KEY `idx_status` (`status`),
    KEY `idx_name` (`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='product (SPU)';

-- ------------------------------------------------------------
-- 3. product_detail  (SKU: size / color)
-- ------------------------------------------------------------
CREATE TABLE `product_detail` (
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `product_detail_id` VARCHAR(64)     NOT NULL                COMMENT 'business sku id (UUID)',
    `product_id`        VARCHAR(64)     NOT NULL                COMMENT 'fk -> product.product_id',
    `size`              VARCHAR(32)              DEFAULT NULL   COMMENT 'size',
    `color`             VARCHAR(32)              DEFAULT NULL   COMMENT 'color',
    `price`             DECIMAL(10, 2)  NOT NULL DEFAULT 0.00   COMMENT 'sku price',
    `stock`             INT             NOT NULL DEFAULT 0      COMMENT 'sku stock',
    `detail_desc`       VARCHAR(1024)            DEFAULT NULL   COMMENT 'detail description',
    `detail_images`     TEXT                     DEFAULT NULL   COMMENT 'comma separated image urls',
    `status`            TINYINT         NOT NULL DEFAULT 1      COMMENT '1 on-shelf / 0 off-shelf',
    `gmt_created`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `gmt_modified`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'modified time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_detail_id` (`product_detail_id`),
    UNIQUE KEY `uk_product_size_color` (`product_id`, `size`, `color`),
    KEY `idx_product_id` (`product_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='product detail (SKU)';

-- ------------------------------------------------------------
-- 4. order
-- ------------------------------------------------------------
CREATE TABLE `order` (
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `order_id`          VARCHAR(64)     NOT NULL                COMMENT 'business order id (UUID)',
    `user_id`           VARCHAR(64)     NOT NULL                COMMENT 'fk -> user.user_id',
    `product_id`        VARCHAR(64)     NOT NULL                COMMENT 'fk -> product.product_id',
    `product_detail_id` VARCHAR(64)     NOT NULL                COMMENT 'fk -> product_detail.product_detail_id',
    `product_name`      VARCHAR(128)    NOT NULL                COMMENT 'product name snapshot',
    `product_image`     VARCHAR(512)             DEFAULT NULL   COMMENT 'product image snapshot',
    `size`              VARCHAR(32)              DEFAULT NULL   COMMENT 'size snapshot',
    `quantity`          INT             NOT NULL DEFAULT 1      COMMENT 'quantity',
    `unit_price`        DECIMAL(10, 2)  NOT NULL DEFAULT 0.00   COMMENT 'unit price snapshot',
    `total_amount`      DECIMAL(10, 2)  NOT NULL DEFAULT 0.00   COMMENT 'total amount',
    `status`            TINYINT         NOT NULL DEFAULT 0      COMMENT '0 unpaid / 1 paid / 2 cancelled / 3 finished',
    `pay_time`          DATETIME                 DEFAULT NULL   COMMENT 'pay time',
    `cancel_time`       DATETIME                 DEFAULT NULL   COMMENT 'cancel time',
    `gmt_created`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `gmt_modified`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'modified time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_status` (`status`),
    KEY `idx_gmt_created` (`gmt_created`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='order';

-- ------------------------------------------------------------
-- 5. payment_record
-- ------------------------------------------------------------
CREATE TABLE `payment_record` (
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    `payment_id`   VARCHAR(64)     NOT NULL                COMMENT 'business payment id (UUID)',
    `order_id`     VARCHAR(64)     NOT NULL                COMMENT 'fk -> order.order_id',
    `user_id`      VARCHAR(64)     NOT NULL                COMMENT 'fk -> user.user_id',
    `pay_amount`   DECIMAL(10, 2)  NOT NULL DEFAULT 0.00   COMMENT 'pay amount',
    `pay_channel`  VARCHAR(32)     NOT NULL DEFAULT 'ALIPAY' COMMENT 'pay channel',
    `trade_no`     VARCHAR(64)              DEFAULT NULL   COMMENT 'alipay trade no',
    `subject`      VARCHAR(255)             DEFAULT NULL   COMMENT 'pay subject',
    `pay_status`   TINYINT         NOT NULL DEFAULT 0      COMMENT '0 unpaid / 1 success / 2 failed / 3 refunded',
    `pay_time`     DATETIME                 DEFAULT NULL   COMMENT 'pay time',
    `notify_body`  TEXT                     DEFAULT NULL   COMMENT 'alipay async notify body',
    `gmt_created`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `gmt_modified` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'modified time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_id` (`payment_id`),
    UNIQUE KEY `uk_trade_no` (`trade_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_pay_status` (`pay_status`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='payment record';

-- ------------------------------------------------------------
-- Seed data (optional, for stage-2 smoke test)
-- ------------------------------------------------------------
INSERT INTO `product` (`product_id`, `name`, `subtitle`, `brand`, `category`, `price`, `stock`, `sales`, `main_image`, `status`)
VALUES ('P20260001', 'Shede Classic Sneaker', 'Lightweight daily sneaker', 'Shede', 'Sneaker', 399.00, 100, 0, '/img/p20260001.jpg', 1);

INSERT INTO `product_detail` (`product_detail_id`, `product_id`, `size`, `color`, `price`, `stock`, `detail_desc`, `status`)
VALUES ('PD20260001', 'P20260001', '42', 'White', 399.00, 50, 'Breathable mesh upper', 1),
       ('PD20260002', 'P20260001', '43', 'Black', 399.00, 50, 'Breathable mesh upper', 1);
