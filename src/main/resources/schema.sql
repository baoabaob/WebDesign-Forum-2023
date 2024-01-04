CREATE DATABASE IF NOT EXISTS forumApp;
USE forumApp;
CREATE TABLE IF NOT EXISTS `user`
(
    `id`       int(11)     NOT NULL,
    `username` varchar(50) NOT NULL,
    `password` varchar(60) NOT NULL,
    `messages` TEXT, -- 存储JSON格式的字符串
    `posts`    TEXT, -- 存储JSON格式的字符串
    `replies`  TEXT, -- 存储JSON格式的字符串
    PRIMARY KEY (`id`)
);
CREATE TABLE IF NOT EXISTS `post`
(
    `id`           INT         NOT NULL,
    `title`        VARCHAR(50) NOT NULL,
    `created_date` DATETIME    NOT NULL,
    `username`     VARCHAR(50) not null,
    `content`      TEXT        NOT NULL,
    `replies`      TEXT, -- 存储JSON格式的字符串
    PRIMARY KEY (`id`)
);
