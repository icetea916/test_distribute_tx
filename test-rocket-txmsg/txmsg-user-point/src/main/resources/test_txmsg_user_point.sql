/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50743
 Source Host           : localhost:3306
 Source Schema         : test_txmsg_user_point

 Target Server Type    : MySQL
 Target Server Version : 50743
 File Encoding         : 65001

 Date: 01/06/2025 15:26:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_user_point
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_point`;
CREATE TABLE `tb_user_point`  (
  `user_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `point` bigint(20) NULL DEFAULT NULL COMMENT '积分',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户积分' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user_point
-- ----------------------------
INSERT INTO `tb_user_point` VALUES (1, 0, '2025-06-01 13:15:43', '2025-06-01 15:25:53');

-- ----------------------------
-- Table structure for tb_user_point_stream
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_point_stream`;
CREATE TABLE `tb_user_point_stream`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `identifier` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一标识 幂等专用 order_no',
  `record` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '记录',
  `gmt_create` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_identifier`(`identifier`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户积分流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user_point_stream
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
