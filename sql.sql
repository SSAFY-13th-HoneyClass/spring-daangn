# CREATE DATABASE daangn;
use daangn;

DROP TABLE IF EXISTS `message`;

DROP TABLE IF EXISTS `chat_room`;

DROP TABLE IF EXISTS `favorite`;

DROP TABLE IF EXISTS `comment`;

DROP TABLE IF EXISTS `image`;

DROP TABLE IF EXISTS `manner_rating`;

DROP TABLE IF EXISTS `manner_detail`;

DROP TABLE IF EXISTS `product`;

DROP TABLE IF EXISTS `user`;

DROP TABLE IF EXISTS `region`;

DROP TABLE IF EXISTS `category`;

CREATE TABLE `region` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `name` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `category` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `type` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `region_id` INT NOT NULL COMMENT '지역명 FK',
    `email` VARCHAR(100),
    `password` VARCHAR(255),
    `nickname` VARCHAR(50),
    `phone` VARCHAR(20),
    `temperature` DECIMAL(4, 2) DEFAULT 36.5,
    `create_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `profile_url` VARCHAR(255),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`region_id`) REFERENCES `region` (`id`)
);

CREATE TABLE `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `seller_id` BIGINT NOT NULL COMMENT '사용자 FK',
    `category_id` INT NOT NULL COMMENT '카테고리 FK',
    `region_id` INT NOT NULL COMMENT '지역명 FK',
    `title` VARCHAR(100) NOT NULL,
    `thumbnail` VARCHAR(255) NOT NULL
    `description` TEXT,
    `price` INT,
    `is_negotiable` TINYINT(1) DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `dump_time` INT,
    `is_reserved` TINYINT(1) NOT NULL DEFAULT 0,
    `is_completed` TINYINT(1) NOT NULL DEFAULT 0,
    `chat_count` BIGINT NOT NULL DEFAULT 0,
    `view_count` BIGINT NOT NULL DEFAULT 0,
    `favorite_count` BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`),
    FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
    FOREIGN KEY (`region_id`) REFERENCES `region` (`id`)
);

CREATE TABLE `favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
);

CREATE TABLE `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `create_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `parent_comment_id` BIGINT,
    `child_count` INT DEFAULT 0,
    `level` INT DEFAULT 0,
    `hierarchy_path` VARCHAR(255),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `manner_detail` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `content` VARCHAR(100),
    PRIMARY KEY (`id`)
);

CREATE TABLE `manner_rating` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `rated_user_id` BIGINT NOT NULL,
    `detail_id` INT NOT NULL,
    `rater_user_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`rated_user_id`) REFERENCES `user` (`id`),
    FOREIGN KEY (`detail_id`) REFERENCES `manner_detail` (`id`)
);

CREATE TABLE `image` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL,
    `image_url` VARCHAR(255) NOT NULL,
    `order` INT NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
);

CREATE TABLE `chat_room` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `buyer_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `seller_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`),
    FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
    FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `chat_room_id` BIGINT NOT NULL,
    `message` TEXT NOT NULL,
    `send_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_read` TINYINT(1) NOT NULL DEFAULT 0,
    `sender_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`chat_room_id`) REFERENCES `chat_room` (`id`)
);