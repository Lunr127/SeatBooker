CREATE DATABASE IF NOT EXISTS seatbooker DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE seatbooker;

-- 院系表
CREATE TABLE IF NOT EXISTS `department` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '院系名称',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='院系信息表';

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` varchar(20) NOT NULL COMMENT '学号',
  `nickname` varchar(50) NOT NULL COMMENT '昵称',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `open_id` varchar(50) DEFAULT NULL COMMENT '微信openId',
  `department_id` bigint NOT NULL COMMENT '院系ID',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `credit_score` int DEFAULT '100' COMMENT '信用分',
  `violation_count` int DEFAULT '0' COMMENT '违约次数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_student_id` (`student_id`),
  KEY `idx_open_id` (`open_id`),
  KEY `fk_user_department` (`department_id`),
  CONSTRAINT `fk_user_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 自习室表
CREATE TABLE IF NOT EXISTS `study_room` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '自习室名称',
  `location` varchar(100) NOT NULL COMMENT '位置',
  `department_id` bigint DEFAULT NULL COMMENT '所属院系ID，NULL表示公共',
  `seat_count` int NOT NULL COMMENT '座位总数',
  `open_time` time NOT NULL COMMENT '开放时间',
  `close_time` time NOT NULL COMMENT '关闭时间',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-关闭，1-开放',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_room_department` (`department_id`),
  CONSTRAINT `fk_room_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自习室信息表';

-- 座位表
CREATE TABLE IF NOT EXISTS `seat` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL COMMENT '自习室ID',
  `seat_number` varchar(20) NOT NULL COMMENT '座位编号',
  `row_number` int NOT NULL COMMENT '行号',
  `column_number` int NOT NULL COMMENT '列号',
  `has_power` tinyint DEFAULT '0' COMMENT '是否有电源：0-无，1-有',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_room_seat` (`room_id`, `seat_number`),
  CONSTRAINT `fk_seat_room` FOREIGN KEY (`room_id`) REFERENCES `study_room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位信息表';

-- 预约记录表
CREATE TABLE IF NOT EXISTS `reservation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `seat_id` bigint NOT NULL COMMENT '座位ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-未签到，1-已签到，2-已完成，3-已取消，4-违约',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`, `start_time`),
  KEY `idx_seat_time` (`seat_id`, `start_time`, `end_time`),
  CONSTRAINT `fk_reservation_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_reservation_seat` FOREIGN KEY (`seat_id`) REFERENCES `seat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录表';

-- 签到记录表
CREATE TABLE IF NOT EXISTS `check_in` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` bigint NOT NULL COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `check_in_time` datetime NOT NULL COMMENT '签到时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_reservation` (`reservation_id`),
  CONSTRAINT `fk_checkin_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录表';

-- 违约记录表
CREATE TABLE IF NOT EXISTS `violation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `reservation_id` bigint NOT NULL COMMENT '预约ID',
  `violation_time` datetime NOT NULL COMMENT '违约时间',
  `reason` varchar(200) NOT NULL COMMENT '违约原因',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_violation_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_violation_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违约记录表';

-- 系统参数表
CREATE TABLE IF NOT EXISTS `system_param` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `param_key` varchar(50) NOT NULL COMMENT '参数键',
  `param_value` varchar(255) NOT NULL COMMENT '参数值',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_param_key` (`param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数表';

-- 插入默认数据
INSERT INTO `department` (`name`) VALUES ('计算机科学与技术学院');
INSERT INTO `department` (`name`) VALUES ('数学学院');
INSERT INTO `department` (`name`) VALUES ('物理学院');

-- 插入系统参数
INSERT INTO `system_param` (`param_key`, `param_value`, `description`) VALUES ('max_booking_hours', '4', '单次最大预约小时数');
INSERT INTO `system_param` (`param_key`, `param_value`, `description`) VALUES ('max_violation_count', '3', '最大违约次数');
INSERT INTO `system_param` (`param_key`, `param_value`, `description`) VALUES ('check_in_timeout', '15', '签到超时时间(分钟)');
INSERT INTO `system_param` (`param_key`, `param_value`, `description`) VALUES ('advance_reminder', '15', '提前提醒时间(分钟)'); 