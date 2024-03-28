-- 创建库
create database if not exists bi_db;

-- 切换库
use bi_db;

-- 用户表
create table if not exists `user`
(
    `id`           bigint(20)                              NOT NULL AUTO_INCREMENT COMMENT 'id',
    `userAccount`  varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
    `userPassword` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
    `userName`     varchar(256) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '用户昵称',
    `userAvatar`   varchar(1024) COLLATE utf8mb4_unicode_ci         DEFAULT NULL COMMENT '用户头像',
    `userRole`     varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin',
    `createTime`   datetime                                NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `isDelete`     tinyint(4)                              NOT NULL DEFAULT 0 COMMENT '是否删除',
    `singInDate`   datetime                                NOT NULL DEFAULT current_timestamp() COMMENT '签到时间',
    `score`        tinyint(4)                                       DEFAULT 5 COMMENT '积分',
    PRIMARY KEY (`id`),
    KEY `idx_userAccount` (`userAccount`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1763233553784532998
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户';

-- 图表表
create table if not exists chart
(
    `id`          bigint(20)                              NOT NULL AUTO_INCREMENT COMMENT 'id',
    `userId`      bigint(20)                                       DEFAULT NULL COMMENT '用户ID',
    `goal`        text COLLATE utf8mb4_unicode_ci                  DEFAULT NULL COMMENT '分析目标',
    `chartData`   text COLLATE utf8mb4_unicode_ci                  DEFAULT NULL COMMENT '图表数据',
    `chartType`   varchar(128) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '图表类型',
    `genChart`    text COLLATE utf8mb4_unicode_ci                  DEFAULT NULL COMMENT '生成的图表数据',
    `genResult`   text COLLATE utf8mb4_unicode_ci                  DEFAULT NULL COMMENT '生成的分析结论',
    `createTime`  datetime                                NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `updateTime`  datetime                                NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
    `isDelete`    tinyint(4)                              NOT NULL DEFAULT 0 COMMENT '是否删除',
    `name`        varchar(128) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '图表名称',
    `status`      varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'wait' COMMENT 'wait,running,success,failed',
    `execMessage` text COLLATE utf8mb4_unicode_ci                  DEFAULT NULL COMMENT '执行信息',
    `number`      tinyint(4)                              NOT NULL DEFAULT 0 COMMENT '生成echarts错误,重试次数',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1765039094425051139
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='图表信息表';


CREATE TABLE `userinfo`
(
    `ip`        varchar(255) DEFAULT NULL,
    `loginTime` datetime     DEFAULT current_timestamp()
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;