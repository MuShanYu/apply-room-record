/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80035 (8.0.35)
 Source Host           : localhost:3306
 Source Schema         : apply_room_record

 Target Server Type    : MySQL
 Target Server Version : 80035 (8.0.35)
 File Encoding         : 65001

 Date: 01/07/2024 09:44:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for tbl_access_record
-- ----------------------------
DROP TABLE IF EXISTS `tbl_access_record`;
CREATE TABLE `tbl_access_record`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `entry_time` bigint NULL DEFAULT NULL COMMENT '进入时间',
  `out_time` bigint NULL DEFAULT NULL COMMENT '出去的时间',
  `state` smallint NULL DEFAULT NULL COMMENT '状态：-1：代表数据已经逻辑删除，1：代表数据正常',
  `update_time` bigint NULL DEFAULT NULL,
  `create_time` bigint NULL DEFAULT NULL,
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '被记录用户id',
  `room_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '进出的房间id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_record_fk`(`user_id` ASC) USING BTREE,
  INDEX `room_record_fk`(`room_id` ASC) USING BTREE,
  CONSTRAINT `room_record_fk` FOREIGN KEY (`room_id`) REFERENCES `tbl_room` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_record_fk` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '进出记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_access_record
-- ----------------------------
INSERT INTO `tbl_access_record` VALUES ('1bb47850aacf4803844faac39e9664dd', 1717566694195, 1717567580679, 1, 1717567580679, 1717566694195, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b');
INSERT INTO `tbl_access_record` VALUES ('2e0b5859acaf4804908e4764d1618677', 1709707050191, NULL, 1, 1709707050191, 1709707050191, 'ae391d3580b2433fb8dcae22caae1643', 'f4f7facadaec438788f937189adad17c');
INSERT INTO `tbl_access_record` VALUES ('3675720f1cbe4239b02bd037a1864a98', 1709702356601, NULL, 1, 1709702356601, 1709702356601, 'ae391d3580b2433fb8dcae22caae1643', 'bf3354cf8f14401fa6db095f6697f4e9');
INSERT INTO `tbl_access_record` VALUES ('3db835958b524351860e6325d3356014', 1709703905135, NULL, 1, 1709703905135, 1709703905135, 'ae391d3580b2433fb8dcae22caae1643', 'bf3354cf8f14401fa6db095f6697f4e9');
INSERT INTO `tbl_access_record` VALUES ('60f9e42fa9df48ee991485add098f49d', 1709706279688, NULL, 1, 1709706279688, 1709706279688, 'ae391d3580b2433fb8dcae22caae1643', 'bf3354cf8f14401fa6db095f6697f4e9');
INSERT INTO `tbl_access_record` VALUES ('6d2e6f38664a47ec91835d948fe86669', 1718695509208, NULL, 1, 1718695509208, 1718695509208, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b');
INSERT INTO `tbl_access_record` VALUES ('6dcdf2da7ea847c2879d960cdb943e8a', 1709703585603, NULL, 1, 1709703585603, 1709703585603, 'ae391d3580b2433fb8dcae22caae1643', 'bf3354cf8f14401fa6db095f6697f4e9');
INSERT INTO `tbl_access_record` VALUES ('7de30741960144498bd9175bdf9edb40', 1709703670096, NULL, 1, 1709703670096, 1709703670096, 'ae391d3580b2433fb8dcae22caae1643', '28967db4f02143638044e990eb4b84ef');
INSERT INTO `tbl_access_record` VALUES ('90dd312565ca4e4d8a7885887c4812cb', 1709706610124, NULL, 1, 1709706610124, 1709706610124, 'ae391d3580b2433fb8dcae22caae1643', 'f4f7facadaec438788f937189adad17c');
INSERT INTO `tbl_access_record` VALUES ('98942d6011cc473cb9e51efa7df7f4a3', 1709707139553, NULL, 1, 1709707139553, 1709707139553, 'ae391d3580b2433fb8dcae22caae1643', 'bf3354cf8f14401fa6db095f6697f4e9');
INSERT INTO `tbl_access_record` VALUES ('996633fd5cf84d9abfdbd20fa506145e', 1709707430310, NULL, 1, 1709707430310, 1709707430310, 'ae391d3580b2433fb8dcae22caae1643', 'bf3354cf8f14401fa6db095f6697f4e9');
INSERT INTO `tbl_access_record` VALUES ('a92d429b9fe144328218c2bba8f806cf', 1709706394034, NULL, 1, 1709706394034, 1709706394034, 'ae391d3580b2433fb8dcae22caae1643', 'f4f7facadaec438788f937189adad17c');

-- ----------------------------
-- Table structure for tbl_application
-- ----------------------------
DROP TABLE IF EXISTS `tbl_application`;
CREATE TABLE `tbl_application`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `reason` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '原因',
  `application_type` smallint NOT NULL COMMENT '申请类型：0：代表补卡申请。。',
  `matter_record_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '事项记录id,对什么已操作事项的申请',
  `handle_user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '处理人id',
  `apply_user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请人id',
  `state` smallint NOT NULL DEFAULT 0 COMMENT '状态：0：代表申请中，1：代表申请成功，2：代表申请失败',
  `remarks` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注,审核通过或者是拒绝的理由',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `handle_user_fk`(`handle_user_id` ASC) USING BTREE,
  INDEX `apply_user_fk`(`apply_user_id` ASC) USING BTREE,
  CONSTRAINT `apply_user_fk` FOREIGN KEY (`apply_user_id`) REFERENCES `tbl_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `handle_user_fk` FOREIGN KEY (`handle_user_id`) REFERENCES `tbl_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_application
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_message
-- ----------------------------
DROP TABLE IF EXISTS `tbl_message`;
CREATE TABLE `tbl_message`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `message_type` smallint NOT NULL COMMENT '消息类型:待办通知：0 申请结果通知：1',
  `read_state` smallint NOT NULL DEFAULT 0 COMMENT '阅读状态:0未读，1已读',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
  `message_receiver_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息接收者id',
  `message_sender_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息发送者id',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `message_receiver_fk`(`message_receiver_id` ASC) USING BTREE,
  INDEX `message_sender_fk`(`message_sender_id` ASC) USING BTREE,
  CONSTRAINT `message_receiver_fk` FOREIGN KEY (`message_receiver_id`) REFERENCES `tbl_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `message_sender_fk` FOREIGN KEY (`message_sender_id`) REFERENCES `tbl_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_message
-- ----------------------------
INSERT INTO `tbl_message` VALUES ('049466c4cdbe49598dd7d1269aa8bbcb', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年04月18日 12:44至2024年04月18日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713410781339, 1713410781339);
INSERT INTO `tbl_message` VALUES ('05f0f320ecbf4ef5a28d44575ed019f9', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年04月17日 16:06至2024年04月17日 16:55，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713341107778, 1713341107778);
INSERT INTO `tbl_message` VALUES ('1208a305de9f46cbb1dcb860cc39baf1', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年04月19日 12:44至2024年04月19日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713418264113, 1713418264113);
INSERT INTO `tbl_message` VALUES ('1cb1c96750f04149baf1b2f6a8b7cfe7', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年03月06日 13:48至2024年03月06日 14:48，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1709704090331, 1709704090331);
INSERT INTO `tbl_message` VALUES ('2170ba014e4c43f0bcf012331531da90', 0, 0, '您收到来自User的5505房间预约申请，预约时间2024年04月19日 15:44至2024年04月19日 20:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713419789104, 1713419789104);
INSERT INTO `tbl_message` VALUES ('28823d5836c94256b388d96bf5fbd4bb', 1, 0, 'User取消了房间6605的预约申请。预约时间：2024年03月04日 16:37~2024年03月04日 17:37。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1709541620911, 1709541620911);
INSERT INTO `tbl_message` VALUES ('2dcec5d8f0d64f0eb21d54830a1c46e3', 0, 0, '您收到来自User的5505房间预约申请，预约时间2024年04月18日 11:44至2024年04月18日 13:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713408511872, 1713408511872);
INSERT INTO `tbl_message` VALUES ('36a85864019b4d21ae948042286f753f', 0, 0, '您收到来自User的5504房间预约申请，预约时间2024年03月06日 13:48至2024年03月06日 14:48，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1709704099949, 1709704099949);
INSERT INTO `tbl_message` VALUES ('3a80f562323b45a59db70f0761ddf17d', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年04月17日 15:30至2024年04月17日 16:55，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713338992484, 1713338992484);
INSERT INTO `tbl_message` VALUES ('3d087cd8966745c092365843172206ad', 1, 0, '您2024年06月18日 15:20发起的6604预约申请，预约时间为2024年06月19日 15:18~16:18（周三），审核不通过。原因为：852。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1718696893057, 1718696893057);
INSERT INTO `tbl_message` VALUES ('3e625dab1d3a4ee3a0990b6b04d5be59', 0, 0, '您收到来自User的5505房间预约申请，预约时间2024年04月18日 12:44至2024年04月18日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713411143150, 1713411143150);
INSERT INTO `tbl_message` VALUES ('3ebaaeb26f6144e2a6f8783cb6ec82e3', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年04月17日 15:47至2024年04月17日 16:55，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713339962980, 1713339962980);
INSERT INTO `tbl_message` VALUES ('405e4464c5aa42669467ac15285633ff', 0, 0, '您收到来自User的5505房间预约申请，预约时间2024年03月06日 13:48至2024年03月06日 14:48，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1709704095457, 1709704095457);
INSERT INTO `tbl_message` VALUES ('40f5defa29414ae7b62b1365b10de57a', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年04月17日 15:40至2024年04月17日 16:55，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713339435586, 1713339435586);
INSERT INTO `tbl_message` VALUES ('4620c277cc15484591f211f24860244b', 0, 0, '您收到来自User的6604房间预约申请，预约时间2024年04月19日 12:44至2024年04月19日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713418727558, 1713418727558);
INSERT INTO `tbl_message` VALUES ('4941c752fd2f47e3bfb3eaa8a150ea4e', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年03月04日 16:44至2024年03月04日 17:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1709541889362, 1709541889362);
INSERT INTO `tbl_message` VALUES ('4d29a34a12424771bb5d7689f1448b29', 0, 0, '您收到来自User的5504房间预约申请，预约时间2024年04月18日 12:44至2024年04月18日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713411147099, 1713411147099);
INSERT INTO `tbl_message` VALUES ('51066a8a65dc4feb8409e94fc9d12ab8', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年06月06日 16:08~17:08（周四），请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1717574945230, 1717574945230);
INSERT INTO `tbl_message` VALUES ('533105d97d7d4bc3904945d587b431a8', 0, 1, '您收到来自User的6605房间预约申请，预约时间2024年02月23日 11:43至2024年02月23日 12:43，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1708659823003, 1708659852847);
INSERT INTO `tbl_message` VALUES ('5a7f065a0f0e4d709cfc5ae9f1aeaa3a', 0, 0, '您收到来自User的6603房间预约申请，预约时间2024年04月19日 12:44至2024年04月19日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713419530740, 1713419530740);
INSERT INTO `tbl_message` VALUES ('63168696bb5047f9b01c07a6f29ced6c', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年04月18日 10:44至2024年04月18日 11:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713408291419, 1713408291419);
INSERT INTO `tbl_message` VALUES ('699cd3dc5a284529a400a6998003a1a8', 1, 0, '您2024年03月11日 15:46:14发起的6605预约申请，预约时间为2024年03月11日 15:46至2024年03月11日 16:46，审核不通过。原因为：。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1710143224882, 1710143224882);
INSERT INTO `tbl_message` VALUES ('6a19563f751b413f88b3c1ed10bc4dce', 1, 0, '您2024年04月18日 10:50:37发起的5507预约申请，预约时间为2024年04月18日 11:44至2024年04月18日 13:44，审核不通过。原因为：撒发射点发生。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1713409987736, 1713409987736);
INSERT INTO `tbl_message` VALUES ('6aad0d4d90e34049aa3f7f922a0759ae', 1, 0, '您2024年04月18日 10:47:25发起的6605预约申请，预约时间为2024年04月18日 11:44至2024年04月18日 13:44，审核不通过。原因为：11111。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1713408472289, 1713408472289);
INSERT INTO `tbl_message` VALUES ('6c799b51b81f4caba974c64100561cd9', 0, 0, '您收到来自User的6604房间预约申请，预约时间2024年06月19日 15:18~16:18（周三），请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1718695251257, 1718695251257);
INSERT INTO `tbl_message` VALUES ('6cdb3db16a284e749bb9d2f02235ff8e', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年06月05日 14:02~15:02（周三），请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1717394563657, 1717394563657);
INSERT INTO `tbl_message` VALUES ('6ee553bfd6a34a93926859f4a7cc05a5', 1, 0, 'User取消了房间6605的预约申请。预约时间：2024年03月04日 16:44~2024年03月04日 17:44。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1709541965513, 1709541965513);
INSERT INTO `tbl_message` VALUES ('6f129a53c7c44240a307728da431d4db', 0, 0, '您收到来自User的5504房间预约申请，预约时间2024年04月19日 12:44至2024年04月19日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713418458048, 1713418458048);
INSERT INTO `tbl_message` VALUES ('72aa7a039ba5456db2f535b70e0c3fa4', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年03月11日 15:40至2024年03月11日 16:40，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1710142818650, 1710142818650);
INSERT INTO `tbl_message` VALUES ('752cba68677b421488287b1e81017085', 0, 0, '您收到来自User的5505房间预约申请，预约时间2024年06月06日 16:08~17:08（周四），请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1717574949096, 1717574949096);
INSERT INTO `tbl_message` VALUES ('7958957ad41643979524035f9337b99e', 0, 0, '您收到来自User的5502房间预约申请，预约时间2024年04月19日 12:44至2024年04月19日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713418734405, 1713418734405);
INSERT INTO `tbl_message` VALUES ('7c0530ab9b6a4b03a0892b985069987b', 1, 0, '您2024年06月05日 16:09发起的5505预约申请，预约时间为2024年06月06日 16:08~17:08（周四），已由审核员审核通过。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1717574964557, 1717574964557);
INSERT INTO `tbl_message` VALUES ('7c782b62747f459e9a0930838302e464', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年04月19日 15:44至2024年04月19日 20:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713419786164, 1713419786164);
INSERT INTO `tbl_message` VALUES ('7cff8f20beb94e4cbe5edfbda970dbd1', 0, 0, '您收到来自User的5505房间预约申请，预约时间2024年03月11日 15:46至2024年03月11日 16:46，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1710143268598, 1710143268598);
INSERT INTO `tbl_message` VALUES ('7edef35aa774485f8e9f82f76c9851c3', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年06月04日 13:39~14:39，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1717393450850, 1717393450850);
INSERT INTO `tbl_message` VALUES ('7f115ab6399f4274b23d79ed7579612e', 1, 0, '您2024年04月17日 14:59:12发起的6605预约申请，预约时间为2024年04月17日 15:00至2024年04月17日 15:55，审核不通过。原因为：111111。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1713337408768, 1713337408768);
INSERT INTO `tbl_message` VALUES ('8486cd71b8704531891d20c5ae7e0e3f', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年04月17日 16:03至2024年04月17日 16:55，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713340889158, 1713340889158);
INSERT INTO `tbl_message` VALUES ('871e16910bc347f5bcb81d5c69f03f93', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年04月17日 15:56至2024年04月17日 16:55，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713340473641, 1713340473641);
INSERT INTO `tbl_message` VALUES ('89b766dddd9c4fd986446bd337fe6ee2', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年03月04日 16:37至2024年03月04日 17:37，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1709541476755, 1709541476755);
INSERT INTO `tbl_message` VALUES ('8c1455cf984a45d9b4051728a26b7ec0', 0, 0, '您收到来自User的5503房间预约申请，预约时间2024年04月19日 12:44至2024年04月19日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713419286833, 1713419286833);
INSERT INTO `tbl_message` VALUES ('913fd5b085cc4a3198c7c40892185273', 1, 0, '您2024年06月03日 14:02发起的6605预约申请，预约时间为2024年06月05日 14:02~15:02（周三），已由审核员审核通过。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1717394575341, 1717394575341);
INSERT INTO `tbl_message` VALUES ('986b98f8ba1543cfabf67b69ee746c38', 1, 0, '您2024年06月03日 13:44发起的6605预约申请，预约时间为2024年06月04日 13:39~14:39（周二），审核不通过。原因为：jhgjhjhgvjhgkhj。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1717394604910, 1717394604910);
INSERT INTO `tbl_message` VALUES ('a6d97b4f72b74a9f87b06f4d53a4a7cf', 0, 0, '您收到来自User的5505房间预约申请，预约时间2024年04月19日 12:44至2024年04月19日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713418269164, 1713418269164);
INSERT INTO `tbl_message` VALUES ('a7455b6c7e7c4839a6cc98b0363743cd', 0, 0, '您收到来自User的5504房间预约申请，预约时间2024年04月18日 11:44至2024年04月18日 13:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713408580745, 1713408580745);
INSERT INTO `tbl_message` VALUES ('a89bb7a7a26740cfb06de040676c651f', 0, 0, '您收到来自User的5506房间预约申请，预约时间2024年04月19日 12:44至2024年04月19日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713419526604, 1713419526604);
INSERT INTO `tbl_message` VALUES ('ac4e898aa9014707bc87e1359c66c294', 0, 0, '您收到来自User的5508房间预约申请，预约时间2024年04月19日 12:44至2024年04月19日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713418960486, 1713418960486);
INSERT INTO `tbl_message` VALUES ('b576e6d865d244e68b60f869572cc125', 1, 0, '您2024年03月11日 15:40:18发起的6605预约申请，预约时间为2024年03月11日 15:40至2024年03月11日 16:40，审核不通过。原因为：。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1710142977993, 1710142977993);
INSERT INTO `tbl_message` VALUES ('b8c8e2fc08a849a3ad2c817e66a19935', 1, 0, 'User取消了房间6605的预约申请。预约时间：2024年03月04日 16:47~2024年03月04日 17:47。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1709542077259, 1709542077259);
INSERT INTO `tbl_message` VALUES ('c0f38819a4fa44dab369bd5c66a5669e', 0, 0, '您收到来自User的5501房间预约申请，预约时间2024年04月19日 12:44至2024年04月19日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713418955789, 1713418955789);
INSERT INTO `tbl_message` VALUES ('c127a04419e94e64a4d16763dd755c88', 1, 0, '您2024年04月18日 10:48:31发起的5505预约申请，预约时间为2024年04月18日 11:44至2024年04月18日 13:44，审核不通过。原因为：说法是发送。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1713410001097, 1713410001097);
INSERT INTO `tbl_message` VALUES ('cf7ccb95a7344375b65b3f6db68da30c', 1, 0, '您2024年04月18日 10:49:40发起的5504预约申请，预约时间为2024年04月18日 11:44至2024年04月18日 13:44，审核不通过。原因为：撒飞洒发生。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1713409992419, 1713409992419);
INSERT INTO `tbl_message` VALUES ('dc52cc934ef745a596fee1b4d8fb586f', 1, 0, '您2024年04月18日 11:26:21发起的6605预约申请，预约时间为2024年04月18日 12:44至2024年04月18日 14:44，审核不通过。原因为：发撒发撒法发达到发发。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1713410880491, 1713410880491);
INSERT INTO `tbl_message` VALUES ('dcde09ddd29f4ddfb142c5762b27bbfc', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年03月04日 16:47至2024年03月04日 17:47，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1709542065597, 1709542065597);
INSERT INTO `tbl_message` VALUES ('e2251b7310754201b4cfd339570ee9c6', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年03月11日 15:46至2024年03月11日 16:46，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1710143174283, 1710143174283);
INSERT INTO `tbl_message` VALUES ('ea47884192fd490fa13d4cc65ca0dca4', 0, 0, '您收到来自User的5507房间预约申请，预约时间2024年04月19日 12:44至2024年04月19日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713418461217, 1713418461217);
INSERT INTO `tbl_message` VALUES ('ea9b3dde67f848b098414a6d6e4bfd92', 1, 0, 'User取消了房间5505的预约申请。预约时间：2024年06月04日 13:39~14:39（周二）。取消理由：就卡机两地分居。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1717394382984, 1717394382984);
INSERT INTO `tbl_message` VALUES ('ef19e8710dc24517a92cf8865cc1970b', 0, 0, '您收到来自User的5505房间预约申请，预约时间2024年06月04日 13:39~14:39（周二），请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1717394303293, 1717394303293);
INSERT INTO `tbl_message` VALUES ('efc4241f7a6147f5a35d3524468ddb76', 1, 0, '您2024年03月11日 15:47:48发起的5505预约申请，预约时间为2024年03月11日 15:46至2024年03月11日 16:46，审核不通过。原因为：sadfafasdfsa。', '8e6b34a790c046e5a5b8bbf47ce7c190', '8e6b34a790c046e5a5b8bbf47ce7c190', 1710143382982, 1710143382982);
INSERT INTO `tbl_message` VALUES ('f1da46349361440b8917df8b8bd587ce', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年04月18日 11:44至2024年04月18日 13:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713408445017, 1713408445017);
INSERT INTO `tbl_message` VALUES ('f2360e5a1544439793b0c3043ad871a0', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年06月19日 15:18~16:18（周三），请您及时处理。', '2', 'ae391d3580b2433fb8dcae22caae1643', 1718695148086, 1718695148086);
INSERT INTO `tbl_message` VALUES ('f3b9c6a5a9d245b58cfccf56a4d80d35', 1, 0, 'User取消了房间6605的预约申请。预约时间：2024年02月23日 11:43~2024年02月23日 12:43。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1709541804820, 1709541804820);
INSERT INTO `tbl_message` VALUES ('fb16e08f3723462fae9a5d3a38676b8f', 0, 0, '您收到来自User的6605房间预约申请，预约时间2024年04月17日 17:06至2024年04月17日 18:55，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713341419400, 1713341419400);
INSERT INTO `tbl_message` VALUES ('fb22ff421e79457ab7459f4c89cba9cf', 0, 0, '您收到来自User的6602房间预约申请，预约时间2024年04月19日 12:44至2024年04月19日 14:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713419290443, 1713419290443);
INSERT INTO `tbl_message` VALUES ('ffb4197a1b8d4ec5a49716eb5672adbd', 0, 0, '您收到来自User的5507房间预约申请，预约时间2024年04月18日 11:44至2024年04月18日 13:44，请您及时处理。', '8e6b34a790c046e5a5b8bbf47ce7c190', 'ae391d3580b2433fb8dcae22caae1643', 1713408637174, 1713408637174);

-- ----------------------------
-- Table structure for tbl_notice
-- ----------------------------
DROP TABLE IF EXISTS `tbl_notice`;
CREATE TABLE `tbl_notice`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `publish_user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发布人id',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint NOT NULL COMMENT '修改时间',
  `state` smallint NOT NULL DEFAULT 1 COMMENT '状态：-1：代表数据已经逻辑删除，1：代表数据正常',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `publish_user_fk`(`publish_user_id` ASC) USING BTREE,
  CONSTRAINT `publish_user_fk` FOREIGN KEY (`publish_user_id`) REFERENCES `tbl_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_notice
-- ----------------------------
INSERT INTO `tbl_notice` VALUES ('011ef44660404813b8278ecf529db168', 'test2', '这是Test内容，这是test内容，这是一堆废话，这是一堆废话，这是一堆废话。这是Test内容，这是test内容，这是一堆废话，这是一堆废话，这是一堆废话。这是Test内容，这是test内容，这是一堆废话，这是一堆废话，这是一堆废话。这是Test内容，这是test内容，这是一堆废话，这是一堆废话，这是一堆废话。这是Test内容，这是test内容，这是一堆废话，这是一堆废话，这是一堆废话。这是Test内容，这是test内容，这是一堆废话，这是一堆废话，这是一堆废话。这是Test内容，这是test内容，这是一堆', '8e6b34a790c046e5a5b8bbf47ce7c190', 1716535723268, 1716535723268, 1);
INSERT INTO `tbl_notice` VALUES ('1d962e4f10b54db482490465e0a219da', 'Test', '这是Test内容，这是test内容，这是一堆废话，这是一堆废话，这是一堆废话。', '8e6b34a790c046e5a5b8bbf47ce7c190', 1708655792370, 1708655792370, 1);

-- ----------------------------
-- Table structure for tbl_role
-- ----------------------------
DROP TABLE IF EXISTS `tbl_role`;
CREATE TABLE `tbl_role`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标识',
  `role_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限名称',
  `role_des` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限描述',
  `state` smallint NULL DEFAULT NULL COMMENT '状态',
  `create_time` bigint NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tbl_role
-- ----------------------------
INSERT INTO `tbl_role` VALUES ('0', 'user', '普通用户', 1, 1638605879181, 1638605879181);
INSERT INTO `tbl_role` VALUES ('1', 'admin', '房间负责人', 1, 1638605879166, 1719380119946);
INSERT INTO `tbl_role` VALUES ('2', 'super-admin', '管理员', 1, 1638605879166, 1719384362583);
INSERT INTO `tbl_role` VALUES ('69f811fefd5e4512ae55b3728b1c08f0', 'sys-admin', '系统管理员', 1, 1719379983398, 1719380136406);

-- ----------------------------
-- Table structure for tbl_room
-- ----------------------------
DROP TABLE IF EXISTS `tbl_room`;
CREATE TABLE `tbl_room`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `school` varchar(62) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '校区',
  `teach_building` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '楼栋',
  `category` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类别',
  `room_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '房间名称：房间号+名字',
  `equipment_info` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备信息',
  `capacity` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '容量',
  `state` smallint NULL DEFAULT NULL COMMENT '状态：-1：代表数据已经逻辑删除，1：代表数据正常，5：代表房间不可预约',
  `update_time` bigint NULL DEFAULT NULL,
  `create_time` bigint NULL DEFAULT NULL,
  `charge_person` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '房间负责人姓名',
  `charge_person_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '房间负责人id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '房间' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_room
-- ----------------------------
INSERT INTO `tbl_room` VALUES ('07b4771ab015442eb5e47d5d95639b5b', '花江校区', '6', '会议室', '6605', '无', '100', 1, 1719389797841, 1708655939088, 'admin2', '2');
INSERT INTO `tbl_room` VALUES ('0c3d943fcb7b4817a778917c0321393c', '花江校区', '5', '会议室', '5505', '无', '100', 1, 1718696552796, 1708655854030, 'admin', '8e6b34a790c046e5a5b8bbf47ce7c190');
INSERT INTO `tbl_room` VALUES ('157170e44d824ee593c2c7e67b30e7d2', '花江校区', '5', '会议室', '5504', '无', '100', 1, 1718696552796, 1708655849920, 'admin', '8e6b34a790c046e5a5b8bbf47ce7c190');
INSERT INTO `tbl_room` VALUES ('216b6362a8894fd8b02ab96fd598318e', '花江校区', '5', '会议室', '5507', '无', '100', 1, 1718696552796, 1708655862205, 'admin', '8e6b34a790c046e5a5b8bbf47ce7c190');
INSERT INTO `tbl_room` VALUES ('28967db4f02143638044e990eb4b84ef', '花江校区', '6', '会议室', '6604', '无', '100', 1, 1718696552796, 1708655934721, 'admin', '8e6b34a790c046e5a5b8bbf47ce7c190');
INSERT INTO `tbl_room` VALUES ('8e83ff366b4a4a01b25b213b32651d85', '花江校区', '5', '会议室', '5502', '无', '100', 1, 1718696552796, 1708655840596, 'admin', '8e6b34a790c046e5a5b8bbf47ce7c190');
INSERT INTO `tbl_room` VALUES ('9f4cdde6d56b40408c0ec96636ec46dc', '花江校区', '5', '会议室', '5501', '无', '100', 1, 1718696611153, 1708655828621, 'admin', '8e6b34a790c046e5a5b8bbf47ce7c190');
INSERT INTO `tbl_room` VALUES ('aaafb423b9bf47a88bb1a4ff9958e2f7', '花江校区', '5', '会议室', '5508', '无', '100', 1, 1718696552796, 1708655866329, 'admin', '8e6b34a790c046e5a5b8bbf47ce7c190');
INSERT INTO `tbl_room` VALUES ('b0edd4756dbc47c2b259f66ff8538599', '花江校区', '5', '会议室', '5503', '无', '100', 1, 1719390074439, 1708655845603, 'admin2', '2');
INSERT INTO `tbl_room` VALUES ('b281144f33c54885b032ab54402d7c32', '花江校区', '5', '会议室', '6606', '852', '50', 1, 1719563951420, 1718687976245, 'admin', '8e6b34a790c046e5a5b8bbf47ce7c190');
INSERT INTO `tbl_room` VALUES ('bf3354cf8f14401fa6db095f6697f4e9', '花江校区', '6', '会议室', '6602', '无', '100', 1, 1718696552796, 1708655924532, 'admin', '8e6b34a790c046e5a5b8bbf47ce7c190');
INSERT INTO `tbl_room` VALUES ('f0ba52fff02448eab1488e8406e831a8', '花江校区', '5', '会议室', '5506', '无', '100', 1, 1718696552796, 1708655858390, 'admin', '8e6b34a790c046e5a5b8bbf47ce7c190');
INSERT INTO `tbl_room` VALUES ('f4f7facadaec438788f937189adad17c', '花江校区', '6', '会议室', '6603', '无', '100', 1, 1718696552796, 1708655930151, 'admin', '8e6b34a790c046e5a5b8bbf47ce7c190');

-- ----------------------------
-- Table structure for tbl_room_reservation
-- ----------------------------
DROP TABLE IF EXISTS `tbl_room_reservation`;
CREATE TABLE `tbl_room_reservation`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `room_usage` varchar(521) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '预约用途',
  `reserve_start_time` bigint NULL DEFAULT NULL COMMENT '预约起始时间',
  `reserve_end_time` bigint NULL DEFAULT NULL COMMENT '预约结束时间',
  `verify_user_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核人姓名',
  `state` smallint NULL DEFAULT NULL COMMENT '状态：0：代表会议室为待审核状态，2：代表会议室为预约成功状态，3：代表会议室预约主动取消，4：代表会议室的预约被驳回',
  `update_time` bigint NULL DEFAULT NULL,
  `create_time` bigint NULL DEFAULT NULL,
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '预约用户id',
  `room_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '房间id',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注，用于记录审核拒绝or通过原因',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_room_apply_fk`(`user_id` ASC) USING BTREE,
  INDEX `room_room_apply_fk`(`room_id` ASC) USING BTREE,
  CONSTRAINT `room_room_apply_fk` FOREIGN KEY (`room_id`) REFERENCES `tbl_room` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_room_apply_fk` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '房间预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_room_reservation
-- ----------------------------
INSERT INTO `tbl_room_reservation` VALUES ('047aaeb476c243989f47c072e9700821', '卡号发扩大和对方开讲啦sss号发扩大和对方号发扩大和对方号发扩大和对方号发扩大和对方', 1710143170062, 1710146770062, 'Admin', 3, 1710143224877, 1710143174269, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('08c83454e1d845b895d06ec84099c893', 'sfadasfa', 1713501840000, 1713509040000, NULL, 4, 1716442091292, 1713419286777, 'ae391d3580b2433fb8dcae22caae1643', 'b0edd4756dbc47c2b259f66ff8538599', '');
INSERT INTO `tbl_room_reservation` VALUES ('0eff08b78fd9474191b4ff33cb23d226', 'safdasfasf', 1713501840000, 1713509040000, NULL, 4, 1716442183728, 1713418269156, 'ae391d3580b2433fb8dcae22caae1643', '0c3d943fcb7b4817a778917c0321393c', '');
INSERT INTO `tbl_room_reservation` VALUES ('19c923225cc34dafb7b9ceae3f191173', '看见了SADHKAJ·', 1717567320000, 1717570920000, 'User', 1, 1717394575334, 1717394563651, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '符合要求，给予通过');
INSERT INTO `tbl_room_reservation` VALUES ('1b206a39ea154e3bb20e041583558990', 'SDdDsddsdfsaf', 1713501840000, 1713509040000, NULL, 4, 1716442183740, 1713418264053, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('22b4d03c47cd46f19f4b81ac1aebf166', '564654984', 1717479540000, 1717483140000, 'User', 3, 1717394604900, 1717393450766, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', 'jhgjhjhgvjhgkhj');
INSERT INTO `tbl_room_reservation` VALUES ('2b693b94fdc44ff18f78f561f5ac18d3', '8555', 1713501840000, 1713509040000, NULL, 4, 1716442091308, 1713418734397, 'ae391d3580b2433fb8dcae22caae1643', '8e83ff366b4a4a01b25b213b32651d85', '');
INSERT INTO `tbl_room_reservation` VALUES ('314cda254bd9469687a47f4f36962fb2', 'sdfafafa', 1713512640000, 1713530640000, NULL, 4, 1716442091272, 1713419786114, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('33d8dee018c94d4f90865b14eae1029c', '这是预约理由，这是预约理由', 1709541464107, 1709545064107, NULL, 2, 1709541620852, 1709541476616, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '这是取消原因，这是取消原因');
INSERT INTO `tbl_room_reservation` VALUES ('34bf77203a814328a9e6343c8356d8d7', '撒法的手动阀手动阀十分', 1709704084447, 1709707684447, NULL, 4, 1709782311169, 1709704095446, 'ae391d3580b2433fb8dcae22caae1643', '0c3d943fcb7b4817a778917c0321393c', '');
INSERT INTO `tbl_room_reservation` VALUES ('3917d8a8372e4ae9aa8556e599399c07', 'sfdafasfa', 1713501840000, 1713509040000, NULL, 4, 1716442183728, 1713418457997, 'ae391d3580b2433fb8dcae22caae1643', '157170e44d824ee593c2c7e67b30e7d2', '');
INSERT INTO `tbl_room_reservation` VALUES ('410bff404eda44508164f0bba4507515', 'est in irure minim', 1713339000000, 1713344100000, NULL, 4, 1713339393958, 1713338992389, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('4ebeee0ec0314f028523a4b4eeee31ff', '撒范德萨发生', 1713415440000, 1713422640000, NULL, 4, 1713419439046, 1713411147092, 'ae391d3580b2433fb8dcae22caae1643', '157170e44d824ee593c2c7e67b30e7d2', '');
INSERT INTO `tbl_room_reservation` VALUES ('546b59df585d48829580b801d1e871bf', 'est in irure minim', 1713340020000, 1713344100000, NULL, 4, 1713339962918, 1713339962918, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('5836bd7ef60f4f1ca1c3b0d9689eadce', 'fdafasf', 1717661280000, 1717664880000, NULL, 4, 1717574945191, 1717574945191, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('650e96b7698843c3bb12a38148f0eda6', '还不赶快和顾客', 1717479540000, 1717483140000, NULL, 2, 1717394382976, 1717394303235, 'ae391d3580b2433fb8dcae22caae1643', '0c3d943fcb7b4817a778917c0321393c', '就卡机两地分居');
INSERT INTO `tbl_room_reservation` VALUES ('6a734d5cbe484c73a5582f194cf063ea', 'sdafasfasf', 1713501840000, 1713509040000, NULL, 4, 1716442091287, 1713419290435, 'ae391d3580b2433fb8dcae22caae1643', 'bf3354cf8f14401fa6db095f6697f4e9', '');
INSERT INTO `tbl_room_reservation` VALUES ('6d302a2ee3e94087994ba7c8c0068817', 'sdfsafasfd', 1713501840000, 1713509040000, NULL, 4, 1716442091295, 1713418960480, 'ae391d3580b2433fb8dcae22caae1643', 'aaafb423b9bf47a88bb1a4ff9958e2f7', '');
INSERT INTO `tbl_room_reservation` VALUES ('72daacf9aaac43139a6588887dfb7030', '撒飞洒地方大撒旦飞洒', 1713411840000, 1713419040000, 'Admin', 3, 1713409987688, 1713408637167, 'ae391d3580b2433fb8dcae22caae1643', '216b6362a8894fd8b02ab96fd598318e', '撒发射点发生');
INSERT INTO `tbl_room_reservation` VALUES ('79223068e0ed4ee787c85af16e704d9b', 'est in irure minim', 1713340980000, 1713344100000, NULL, 4, 1713340889152, 1713340889152, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('7fcc2b499e7b4fc286ff1e4bc8403311', '的撒法沙发沙发', 1713408284644, 1713411884644, NULL, 4, 1713408291346, 1713408291346, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('82910af9665c4209a5c3fa19ddde743c', '军区防空拦截大量砍伐建档立卡', 1709541882725, 1709545482725, NULL, 2, 1709541965494, 1709541889293, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '欧锦hihi比哪里可2');
INSERT INTO `tbl_room_reservation` VALUES ('8493bf63719b4eff93d861857f2b7e9a', 'sadfasfa', 1713501840000, 1713509040000, NULL, 4, 1716442183719, 1713418461211, 'ae391d3580b2433fb8dcae22caae1643', '216b6362a8894fd8b02ab96fd598318e', '');
INSERT INTO `tbl_room_reservation` VALUES ('8dafdc77700d455bb34adbf955196489', 'est in irure minim', 1713339600000, 1713344100000, NULL, 4, 1713339958282, 1713339435552, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('93628d25399d497991bda2df20217ab3', '撒发发啊发发', 1713411840000, 1713419040000, 'Admin', 3, 1713409992411, 1713408580688, 'ae391d3580b2433fb8dcae22caae1643', '157170e44d824ee593c2c7e67b30e7d2', '撒飞洒发生');
INSERT INTO `tbl_room_reservation` VALUES ('9ca13323a6e64a65810f0bc25eb700ac', 'afdsafasd', 1717661280000, 1717664880000, 'User', 1, 1717574964547, 1717574949093, 'ae391d3580b2433fb8dcae22caae1643', '0c3d943fcb7b4817a778917c0321393c', '符合要求，给予通过');
INSERT INTO `tbl_room_reservation` VALUES ('9e337588857741b588152bcb56de4a5b', 'est in irure minim', 1713340560000, 1713344100000, NULL, 4, 1713340473631, 1713340473631, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('a33e28d28e9947ecaa6b4c604cc8812a', '撒發射點發順豐', 1718781480000, 1718785080000, 'admin', 3, 1718696893016, 1718695251250, 'ae391d3580b2433fb8dcae22caae1643', '28967db4f02143638044e990eb4b84ef', '852');
INSERT INTO `tbl_room_reservation` VALUES ('a547827949434a5986b545c772f88deb', '是发范德萨发', 1713415440000, 1713422640000, 'Admin', 3, 1713410880439, 1713410781291, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '发撒发撒法发达到发发');
INSERT INTO `tbl_room_reservation` VALUES ('aa3072355bd743308eb411ec7b641b86', '爱上发发打发十分', 1709704084447, 1709707684447, NULL, 4, 1709782311183, 1709704090292, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('b1f2a749a5bf4918bfb1a2e72e6a4ab8', 'est in irure minim', 1713344760000, 1713351300000, NULL, 4, 1713341419394, 1713341419394, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('b7630ef49862490f82bdc3116b7b6d83', 'est in irure minim', 1713337200000, 1713340500000, 'Admin', 3, 1713337408674, 1713337152566, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '111111');
INSERT INTO `tbl_room_reservation` VALUES ('bb53819b4c604144a6d06415c32aad8c', '5655', 1713501840000, 1713509040000, NULL, 4, 1716442091277, 1713419530731, 'ae391d3580b2433fb8dcae22caae1643', 'f4f7facadaec438788f937189adad17c', '');
INSERT INTO `tbl_room_reservation` VALUES ('c59e790117064e4488d325376b65213e', '这是预约理由，这是预约理由，这是预约理由', 1708659799950, 1708663399950, NULL, 2, 1709541804776, 1708659822947, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '取消啦啦啦啦啦啦啦');
INSERT INTO `tbl_room_reservation` VALUES ('d03ee1847ac64ea5819d4973a928b047', '看了很多饭卡上', 1710142813435, 1710146413435, 'Admin', 3, 1710142977984, 1710142818561, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('d145cf56e4a34692979581e4fa2dff2f', 'est in irure minim', 1713341160000, 1713344100000, NULL, 4, 1713341107699, 1713341107699, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('d48bbe3143b149a4ad1a675c74eec405', '撒反对发生', 1713415440000, 1713422640000, NULL, 4, 1713419439078, 1713411143096, 'ae391d3580b2433fb8dcae22caae1643', '0c3d943fcb7b4817a778917c0321393c', '');
INSERT INTO `tbl_room_reservation` VALUES ('dc020eaf987d42db93e902b1fada6245', 'safafasdf', 1713512640000, 1713530640000, NULL, 4, 1716442091245, 1713419789099, 'ae391d3580b2433fb8dcae22caae1643', '0c3d943fcb7b4817a778917c0321393c', '');
INSERT INTO `tbl_room_reservation` VALUES ('dedf650e53c44b9692c020b3b1ee34c8', 'sfsfasf', 1713501840000, 1713509040000, NULL, 4, 1716442091301, 1713418955784, 'ae391d3580b2433fb8dcae22caae1643', '9f4cdde6d56b40408c0ec96636ec46dc', '');
INSERT INTO `tbl_room_reservation` VALUES ('f34d205bad894cd38758229a39196535', '撒旦发射点发大水', 1713411840000, 1713419040000, 'Admin', 3, 1713408472277, 1713408445012, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '11111');
INSERT INTO `tbl_room_reservation` VALUES ('f4e17e3c155b40e28eac3b1703060fbb', '算法发生法发', 1713411840000, 1713419040000, 'Admin', 3, 1713410001091, 1713408511866, 'ae391d3580b2433fb8dcae22caae1643', '0c3d943fcb7b4817a778917c0321393c', '说法是发送');
INSERT INTO `tbl_room_reservation` VALUES ('f62a2a9b1559418b9dcd7d65a5078672', '888888', 1713501840000, 1713509040000, NULL, 4, 1716442091312, 1713418727499, 'ae391d3580b2433fb8dcae22caae1643', '28967db4f02143638044e990eb4b84ef', '');
INSERT INTO `tbl_room_reservation` VALUES ('faeedf4a6df7411681c48d1e30957d5f', '85241', 1718781480000, 1718785080000, NULL, 4, 1719389937557, 1718695148045, 'ae391d3580b2433fb8dcae22caae1643', '07b4771ab015442eb5e47d5d95639b5b', '');
INSERT INTO `tbl_room_reservation` VALUES ('fb14443adb814ad4a2e6511f3faf09c9', '啊发射点发生发', 1709704084447, 1709707684447, NULL, 4, 1709782311059, 1709704099938, 'ae391d3580b2433fb8dcae22caae1643', '157170e44d824ee593c2c7e67b30e7d2', '');
INSERT INTO `tbl_room_reservation` VALUES ('fee93256627f49898de678495eaa5844', '98999', 1713501840000, 1713509040000, NULL, 4, 1716442091283, 1713419526543, 'ae391d3580b2433fb8dcae22caae1643', 'f0ba52fff02448eab1488e8406e831a8', '');

-- ----------------------------
-- Table structure for tbl_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `tbl_sys_config`;
CREATE TABLE `tbl_sys_config`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `config_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置key',
  `config_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '配置value',
  `config_desc` varchar(62) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置描述',
  `state` smallint NULL DEFAULT NULL COMMENT '状态：-1：代表数据已经逻辑删除，1：代表数据正常',
  `create_time` bigint NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tbl_sys_config
-- ----------------------------
INSERT INTO `tbl_sys_config` VALUES ('396c36037f1348a390d6b255db292344', 'schools', '{\"schools\":[\"花江校区\",\"金鸡岭校区\",\"北海校区\"]}', '校区', 1, 1661246797944, 1661248415646);
INSERT INTO `tbl_sys_config` VALUES ('3a66ccd7c4c1451395cbb6cbb2b94fc1', 'categories', '{\"categories\":[\"会议室\",\"研究室\",\"实验室\",\"休息室\",\"活动室\",\"教室\",\"企业校内研发基地\",\"创新基地\",\"创新实验基地\"]}', '房间类别列表', 1, 1661257570342, 1667382572793);
INSERT INTO `tbl_sys_config` VALUES ('4539b071182048dfb9e137de60157ee9', 'versionInfo', '{\"versionCode\":\"1.7.12\"}', '客户端系统版本', 1, 1676975518261, 1705385825349);
INSERT INTO `tbl_sys_config` VALUES ('55c10273a48d416e8a57878c716e71bb', 'userPrivacyPdfUrl', '{\"url\":\"your url\"}', '用户隐私协议pdf地址', 1, 1705909617883, 1705909617883);
INSERT INTO `tbl_sys_config` VALUES ('7e033e06bc5f4d0c9b22853f5fa2fe05', 'helpInfo', '{\"list\":[{\"title\":\"关雎\",\"content\":\"关关雎鸠，在河之洲。窈窕淑女，君子好逑。参差荇菜，左右流之。窈窕淑女，寤寐求之。求之不得，寤寐思服。悠哉悠哉，辗转反侧。参差荇菜，左右采之。窈窕淑女，琴瑟友之。参差荇菜，左右芼之。窈窕淑女，钟鼓乐之。\",\"disabled\":false},{\"title\":\"长歌行\",\"content\":\"青青园中葵，朝露待日晞。阳春布德泽，万物生光辉。常恐秋节至，焜黄华叶衰。百川东到海，何时复西归？少壮不努力，老大徒伤悲！\",\"disabled\":false},{\"title\":\"秋风辞\",\"content\":\"秋风起兮白云飞，草木黄落兮雁南归。兰有秀兮菊有芳，怀佳人兮不能忘。泛楼船兮济汾河，横中流兮扬素波。少壮几时兮奈老何！\",\"disabled\":false}]}', '帮助界面配置信息', 1, 1704961228686, 1704961228686);
INSERT INTO `tbl_sys_config` VALUES ('7efcce86346942ec9734a73041062743', 'commonPhrases', '{\"phrases\":[\"符合要求，给予通过。\",\"不符合要求，驳回。\",\"审批给予通过。\"]}', '审批快捷键入语', 1, 1704251696183, 1704251696183);
INSERT INTO `tbl_sys_config` VALUES ('88df7215b4bd48c3a7cec90f9e1e311f', 'applicationType', '{\"types\":[{\"name\":\"补卡申请\",\"type\":0},{\"name\":\"申请1类\",\"type\":1},{\"name\":\"申请2类\",\"type\":2},{\"name\":\"申请3类\",\"type\":3},{\"name\":\"申请4类\",\"type\":4},{\"name\":\"申请5类\",\"type\":5}]}', '申请类型，用于控制tab', 1, 1704780222048, 1704784673121);
INSERT INTO `tbl_sys_config` VALUES ('94c396213db941e5a4d8981146393cd5', 'webVersion', '{\"currentVersion\":\"1.2.3\"}', '管理端web的版本信息', 1, 1695886076312, 1697008435725);
INSERT INTO `tbl_sys_config` VALUES ('bf50a7ff964f422ca14900a5f6f0be60', 'signInNotice', '{\"content\":\"只允许申请本周非当天的打卡记录，最多只能申请三次，次周重置，被驳回的申请可重新申请，如有遗漏，请及时处理。\"}', '补卡申请界面的notice bar 内容', 1, 1703837737890, 1703837737890);
INSERT INTO `tbl_sys_config` VALUES ('e69c74e8969a461db5d1d940ae79d149', 'modalNotice', '{\"title\":\"这是标题，这是标题\",\"content\":\"这是最新弹窗公告，这是最新弹窗公告。\",\"version\":\"1.2\"}', '客户端弹窗公告', 1, 1705385937152, 1705387310708);
INSERT INTO `tbl_sys_config` VALUES ('ec15bc0c86f847cc9a91a40ffb6e109f', 'institutes', '{\"institutes\":[\"计算机与信息安全学院\",\"机电工程学院\",\"信息与通信学院\",\"艺术与设计学院\",\"商学院\",\"外国语学院\",\"数学与计算机科学学院\",\"电子工程与自动化学院\",\"法学院\",\"材料科学与工程学院\",\"马克思主义学院\",\"生命与环境科学学院\",\"建筑与交通工程学院\",\"国际学院\",\"海洋信息工程学院\"]}', '学院列表', 1, 1661246707014, 1661246707014);

-- ----------------------------
-- Table structure for tbl_sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `tbl_sys_menu`;
CREATE TABLE `tbl_sys_menu`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单ID',
  `parent_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '父菜单ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组件名称',
  `title` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单标题',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query_param` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由参数',
  `is_link` smallint NULL DEFAULT 0 COMMENT '是否为外链（1是 0否）',
  `breadcrumb` smallint NULL DEFAULT 1 COMMENT '如果设置为false，则不会在breadcrumb面包屑中显示(默认 true)',
  `no_cache` smallint NULL DEFAULT 1 COMMENT '是否缓存（1不缓存 0缓存）',
  `menu_type` smallint NULL DEFAULT NULL COMMENT '菜单类型（0目录 1菜单 2按钮）',
  `hide` smallint NULL DEFAULT 1 COMMENT '显示状态（1显示 0隐藏）',
  `state` smallint NULL DEFAULT 1 COMMENT '菜单状态（1正常 0停用 2删除）',
  `perms` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `update_time` bigint NULL DEFAULT 0 COMMENT '修改时间',
  `create_time` bigint NULL DEFAULT 0 COMMENT '创建时间',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_sys_menu
-- ----------------------------
INSERT INTO `tbl_sys_menu` VALUES ('0b1360105a674950ad2f66ba35d4544c', '0b5b49b9611f437797c1df7a3cd57d8f', '', '预约详情', 4, '', '', '', 0, 1, 0, 2, 0, 1, 'system:user:reserveDetail', '#', 1719370505925, 1719370505925, '预约详情');
INSERT INTO `tbl_sys_menu` VALUES ('0b5b49b9611f437797c1df7a3cd57d8f', '222021087fce4739a8e59a2315aca75c', 'UserManager', '用户管理', 3, 'user', 'system/user/index', '', 0, 1, 0, 1, 0, 1, 'system:user', 'user', 1718948880553, 1718947884371, '');
INSERT INTO `tbl_sys_menu` VALUES ('0bc35a6a328041a38958a1e3c9ede82a', '4f91b0cdc12f4bf49449eebbcfcf005a', '', '我管理的', 1, '', '', '', 0, 1, 0, 2, 0, 1, 'work:statistics:self', '#', 1719218859358, 1719218481688, '我管理的按钮权限');
INSERT INTO `tbl_sys_menu` VALUES ('149c98c1f29f422493b11512ea941ca3', '4ad1e78175e94cdda674505806926c7e', '', '禁止预约/解除', 2, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:disableReserveAndRollBack', '#', 1719369696830, 1719369566904, '禁止预约/解除');
INSERT INTO `tbl_sys_menu` VALUES ('1a15d0b3ae074370805f478785319f6b', 'de47bbbeb5134ce581a74c75634b3209', '', '设备列表', 1, '', '', '', 0, 1, 0, 2, 0, 1, 'monitor:onlineUser:equipment', '#', 1719371294783, 1719371294783, '设备列表');
INSERT INTO `tbl_sys_menu` VALUES ('1b4c7b059344433fbdd9a7bc7fc60610', 'fd860db51b4c41409b444f8787adec66', '', '修改', 1, '', '', '', 0, 1, 0, 2, 0, 1, 'authority:role:update', '#', 1719371477703, 1719371477703, '');
INSERT INTO `tbl_sys_menu` VALUES ('1f8f5eeb57ed49658151fdd8746616a5', '4ad1e78175e94cdda674505806926c7e', '', '批量禁用/解除', 3, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:disableAndRollBack', '#', 1719369943713, 1719369943713, '批量禁用/解除');
INSERT INTO `tbl_sys_menu` VALUES ('222021087fce4739a8e59a2315aca75c', '0', 'System', '系统管理', 2, 'system', 'Layout', '', 0, 1, 0, 0, 0, 1, '', 'sys', 1719390574844, 1718947721144, '');
INSERT INTO `tbl_sys_menu` VALUES ('25fcebd07bdd42c080a3acc856c13c35', '48454a3df4844538a3a84c6255d7f0d2', 'Server', '服务监控', 2, 'server', 'monitor/server/index', '', 0, 1, 0, 1, 0, 1, 'monitor:server', 'server', 1719391139056, 1718948102054, '');
INSERT INTO `tbl_sys_menu` VALUES ('26f5e7b1ddc04f6a92ea3e4f3649b5d5', '0b5b49b9611f437797c1df7a3cd57d8f', '', '导入', 1, '', '', '', 0, 1, 0, 2, 0, 1, 'system:user:import', '#', 1719370267196, 1719370267196, '导入');
INSERT INTO `tbl_sys_menu` VALUES ('2741b2f5d80f4efebe56f6cd336edb73', '4ad1e78175e94cdda674505806926c7e', '', '签到详情', 10, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:attendanceDetail', '#', 1719373905161, 1719373905161, '');
INSERT INTO `tbl_sys_menu` VALUES ('2c8c0c5d366040d3a76cfd67f9256d13', '4ad1e78175e94cdda674505806926c7e', '', '查看所有', 13, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:all', '#', 1719379321411, 1719379321411, '查看所有房间');
INSERT INTO `tbl_sys_menu` VALUES ('30148447b6c747c192e570aa60fd9e55', 'cc830367d4874fe9b91a7cf09d2b7d01', '', '更新申请单状态', 4, '', '', '', 0, 1, 0, 2, 0, 1, 'work:approve:application', '#', 1719560441541, 1719560441541, '');
INSERT INTO `tbl_sys_menu` VALUES ('32678d16bdaf418dad4acd33f45db3dc', 'fd860db51b4c41409b444f8787adec66', '', '取消授权', 6, '', '', '', 0, 1, 0, 2, 0, 1, 'authority:role:cancelUserDistribute', '#', 1719562289506, 1719562289506, '');
INSERT INTO `tbl_sys_menu` VALUES ('33e3d1de669344ac9a9adba6df367270', '71eb95391b1c4381abbe2139b1c9c5d6', '', '批量删除', 1, '', '', '', 0, 1, 0, 2, 0, 1, 'monitor:sysOperateLog:del', '#', 1719371096975, 1719371096975, '批量删除');
INSERT INTO `tbl_sys_menu` VALUES ('340ff559c39849ca8e6bdeae0f43a403', 'eaf6bf4ffae24560a680700544a96b6f', '', '添加', 1, '', '', '', 0, 1, 0, 2, 0, 1, 'authority:menu:add', '#', 1719371353208, 1719371353208, '');
INSERT INTO `tbl_sys_menu` VALUES ('3dbc18b14d004d31bd2f753c29657719', '4ad1e78175e94cdda674505806926c7e', '', '导入', 5, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:import', '#', 1719370075635, 1719370064593, '导入');
INSERT INTO `tbl_sys_menu` VALUES ('40cc017031f24b69b78122b268120479', '0', 'Authority', '权限管理', 4, 'authority', 'Layout', '', 0, 1, 0, 0, 0, 1, '', 'auth', 1719391325701, 1718948181885, '');
INSERT INTO `tbl_sys_menu` VALUES ('42b5c2a99ceb439986bc536938286f9f', '0b5b49b9611f437797c1df7a3cd57d8f', '', '重置密码', 8, '', '', '', 0, 1, 0, 2, 0, 1, 'system:user:resetPwd', '#', 1719370608766, 1719370608766, '重置密码');
INSERT INTO `tbl_sys_menu` VALUES ('43ad77ad7ca54339a17b8ad7bdd65968', '8bef4171e7cd46ef9d3d960a7caa80bc', '', '禁用', 1, '', '', '', 0, 1, 0, 2, 0, 1, 'system:config:disable', '#', 1719370770091, 1719370770091, '禁用');
INSERT INTO `tbl_sys_menu` VALUES ('48454a3df4844538a3a84c6255d7f0d2', '0', 'Monitor', '系统监控', 3, 'monitor', 'Layout', '', 0, 1, 0, 0, 0, 1, '', 'monitor', 1719390976306, 1718948005579, '');
INSERT INTO `tbl_sys_menu` VALUES ('4ad1e78175e94cdda674505806926c7e', '222021087fce4739a8e59a2315aca75c', 'RoomList', '房间管理', 2, 'room', 'system/room/index', '', 0, 1, 0, 1, 0, 1, 'system:room', 'room', 1719390775012, 1718947833587, '');
INSERT INTO `tbl_sys_menu` VALUES ('4cb765a62585445ea93c8cf4766b2dda', '0', 'Work', '工作台', 1, 'work', 'Layout', '', 0, 1, 0, 0, 0, 1, '', 'workTable', 1719390220396, 1718947300540, '');
INSERT INTO `tbl_sys_menu` VALUES ('4f91b0cdc12f4bf49449eebbcfcf005a', '4cb765a62585445ea93c8cf4766b2dda', 'DataStatistics', '数据统计', 1, 'data-statistics', 'work/statistics/index', '', 0, 1, 0, 1, 0, 1, 'work:statistics', 'dataStatistic', 1719390339995, 1718947378693, '');
INSERT INTO `tbl_sys_menu` VALUES ('505d7e2f13a548db959df8a71ce6611a', 'cc830367d4874fe9b91a7cf09d2b7d01', '', '拒绝按钮', 3, '', '', '', 0, 1, 0, 2, 0, 1, 'work:roomApprove:reject', '#', 1719220317755, 1719220317755, '房间审批拒绝按钮');
INSERT INTO `tbl_sys_menu` VALUES ('554fb409bf1b4b9a8e8a08049ac8bf38', '0b5b49b9611f437797c1df7a3cd57d8f', '', '修改信息', 7, '', '', '', 0, 1, 0, 2, 0, 1, 'system:user:update', '#', 1719370546171, 1719370546171, '修改信息');
INSERT INTO `tbl_sys_menu` VALUES ('556ac64b205449efa23dc85da21297d0', 'fd860db51b4c41409b444f8787adec66', '', '添加', 2, '', '', '', 0, 1, 0, 2, 0, 1, 'authority:role:add', '#', 1719371620648, 1719371453964, '');
INSERT INTO `tbl_sys_menu` VALUES ('5632924876e6490e9bf53d4b35b78e49', '4ad1e78175e94cdda674505806926c7e', '', '修改房间负责人', 14, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:updateCharger', '#', 1719558763613, 1719558743879, '');
INSERT INTO `tbl_sys_menu` VALUES ('575070817b1548769f720287b2d6be61', '0', 'DataImport', '数据导入', 5, 'data-import', 'Layout', '', 0, 1, 0, 0, 0, 1, '', 'import', 1719391368287, 1718948277674, '');
INSERT INTO `tbl_sys_menu` VALUES ('58d39f011946499eb8345b0e1ac2e48a', '575070817b1548769f720287b2d6be61', 'UserImport', '导入用户数据', 1, 'user', 'import/room/index', '', 0, 1, 0, 1, 0, 1, 'import:user', 'importUser', 1719391411620, 1718948343999, '');
INSERT INTO `tbl_sys_menu` VALUES ('5a00f45da3af4ca19d44887149d85f4b', '4ad1e78175e94cdda674505806926c7e', '', '导入模板下载', 7, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:downloadTemplate', '#', 1719370135979, 1719370135979, '');
INSERT INTO `tbl_sys_menu` VALUES ('64cbf16ac5174c2198e53961044fd0b6', '8bef4171e7cd46ef9d3d960a7caa80bc', '', '修改', 2, '', '', '', 0, 1, 0, 2, 0, 1, 'system:config:update', '#', 1719370904543, 1719370752339, '修改');
INSERT INTO `tbl_sys_menu` VALUES ('6733fb0162104246bee921755fa071d4', '4ad1e78175e94cdda674505806926c7e', '', '生成二维码房间', 4, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:generateQRCode', '#', 1719372961861, 1719370030654, '生成二维码房间');
INSERT INTO `tbl_sys_menu` VALUES ('6b469f9b8bae400e984034925fbdefab', 'eaf6bf4ffae24560a680700544a96b6f', '', '删除', 2, '', '', '', 0, 1, 0, 2, 0, 1, 'authority:menu:del', '#', 1719371424498, 1719371409729, '');
INSERT INTO `tbl_sys_menu` VALUES ('6ea9c08a6b584584a44fadbafece435d', 'cc830367d4874fe9b91a7cf09d2b7d01', '', '通过按钮', 2, '', '', '', 0, 1, 0, 2, 0, 1, 'work:roomApprove:pass', '#', 1719220285683, 1719220285683, '通过房间预约按钮');
INSERT INTO `tbl_sys_menu` VALUES ('6fdb0ced21d940bda1e5f0eebff88e06', '4ad1e78175e94cdda674505806926c7e', '', '删除进出记录', 11, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:delRecord', '#', 1719558313822, 1719558313822, '');
INSERT INTO `tbl_sys_menu` VALUES ('71eb95391b1c4381abbe2139b1c9c5d6', '48454a3df4844538a3a84c6255d7f0d2', 'SysOperateLog', '操作日志', 1, 'sys-operate-log', 'monitor/sysOperateLog/index', '', 0, 1, 0, 1, 0, 1, 'monitor:sysOperateLog', 'log', 1719391023077, 1718948147839, '');
INSERT INTO `tbl_sys_menu` VALUES ('74355f5f5b494cdd91cc218b1176708d', '0b5b49b9611f437797c1df7a3cd57d8f', '', '导入模板下载', 2, '', '', '', 0, 1, 0, 2, 0, 1, 'system:user:downloadTemplate', '#', 1719370395763, 1719370395763, '导入模板下载');
INSERT INTO `tbl_sys_menu` VALUES ('7f303b2ce1794dc9b5322af688a55bf6', '0b5b49b9611f437797c1df7a3cd57d8f', '', '分配角色', 9, '', '', '', 0, 1, 0, 2, 0, 1, 'system:user:distributeRole', '#', 1719370920448, 1719370695196, '分配角色');
INSERT INTO `tbl_sys_menu` VALUES ('7fbde6194fca4640a4d468caa3861d2e', 'fd860db51b4c41409b444f8787adec66', '', '分配用户', 7, '', '', '', 0, 1, 0, 2, 0, 1, 'authority:role:userDistribute', '#', 1719371673193, 1719371673193, '');
INSERT INTO `tbl_sys_menu` VALUES ('81e423f5a31b485b8196e728510a9b1f', '8bef4171e7cd46ef9d3d960a7caa80bc', '', '添加', 3, '', '', '', 0, 1, 0, 2, 0, 1, 'system:config:add', '#', 1719370790298, 1719370731790, '添加');
INSERT INTO `tbl_sys_menu` VALUES ('8269eeb76fcc4fe787abce3bfb419a57', '4ad1e78175e94cdda674505806926c7e', '', '预约详情', 9, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:reserveDetail', '#', 1719373850852, 1719373845295, '');
INSERT INTO `tbl_sys_menu` VALUES ('8bef4171e7cd46ef9d3d960a7caa80bc', '222021087fce4739a8e59a2315aca75c', 'Config', '配置管理', 4, 'config', 'system/config/index', '', 0, 1, 0, 1, 0, 1, 'system:config', 'config', 1718948889818, 1718947957362, '');
INSERT INTO `tbl_sys_menu` VALUES ('8e6c65f153174a58a5613d51c3617818', '4ad1e78175e94cdda674505806926c7e', '', '禁用', 12, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:disable', '#', 1719378703189, 1719378703189, '');
INSERT INTO `tbl_sys_menu` VALUES ('90fdc974051449c086bca2f40e155366', 'cc830367d4874fe9b91a7cf09d2b7d01', '', '刷新', 1, '', '', '', 0, 1, 0, 2, 0, 2, 'work:roomApprove:refresh', '#', 1719220251549, 1719220251549, '刷新按钮');
INSERT INTO `tbl_sys_menu` VALUES ('97df460bdc034be79d313b31b78d39a9', 'de64f0bf2d6e44afba6ee6b2920b8653', 'ProjectInfo', '项目信息', 1, 'project-info', 'about/index', '', 0, 1, 0, 1, 0, 1, 'about:info', 'github', 1719391648698, 1719220564544, '');
INSERT INTO `tbl_sys_menu` VALUES ('9800ef8713a547d4bb7db5693be5ae4b', '222021087fce4739a8e59a2315aca75c', 'NoticeManager', '公告管理', 1, 'notice', 'system/notice/index', '', 0, 1, 0, 1, 0, 1, 'system:notice', 'notice', 1719390661017, 1718947783342, '');
INSERT INTO `tbl_sys_menu` VALUES ('9cc0f5e677834bf9bbb30d1d9b0479dc', '0b5b49b9611f437797c1df7a3cd57d8f', '', '足迹详情', 3, '', '', '', 0, 1, 0, 2, 0, 1, 'system:user:accessRecord', '#', 1719370460189, 1719370460189, '足迹详情');
INSERT INTO `tbl_sys_menu` VALUES ('9fc8a2d9a1464e45b6e66712405b647d', '4ad1e78175e94cdda674505806926c7e', '', '足迹详情', 11, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:accessRecordDetail', '#', 1719378709543, 1719373932303, '');
INSERT INTO `tbl_sys_menu` VALUES ('ad422f40a5b4465f9fed45c1bf68b213', '4ad1e78175e94cdda674505806926c7e', '', '搜索', 8, '', '', '', 0, 1, 0, 2, 0, 2, 'system:room:search', '#', 1719370235702, 1719370235702, '搜索');
INSERT INTO `tbl_sys_menu` VALUES ('b2345457936145c6853a0f4e41feb529', 'eaf6bf4ffae24560a680700544a96b6f', '', '修改', 3, '', '', '', 0, 1, 0, 2, 0, 1, 'authority:menu:update', '#', 1719371426763, 1719371388225, '修改');
INSERT INTO `tbl_sys_menu` VALUES ('b7bad81cf17444988dc8a36310970584', '4ad1e78175e94cdda674505806926c7e', '', '导出用户进出记录列表', 11, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:exportRecord', '#', 1719558194021, 1719558194021, '');
INSERT INTO `tbl_sys_menu` VALUES ('b87648f077e3410aa2abf09366ff134d', 'fd860db51b4c41409b444f8787adec66', '', '菜单分配', 3, '', '', '', 0, 1, 0, 2, 0, 1, 'authority:role:menuDistribute', '#', 1719371626632, 1719371598102, '');
INSERT INTO `tbl_sys_menu` VALUES ('bcf94b339f2d43398036c883d1230015', '4f91b0cdc12f4bf49449eebbcfcf005a', '', '导出报表数据', 2, '', '', '', 0, 1, 0, 2, 0, 1, 'work:statistics:export', '#', 1719218562477, 1719218562477, '导出报表数据');
INSERT INTO `tbl_sys_menu` VALUES ('c4a34226f1d04d5a98cf3f51ba322e47', '9800ef8713a547d4bb7db5693be5ae4b', '', '添加', 1, '', '', '', 0, 1, 0, 2, 0, 1, 'system:notice:add', '#', 1719369303107, 1719369303107, '添加');
INSERT INTO `tbl_sys_menu` VALUES ('cc830367d4874fe9b91a7cf09d2b7d01', '4cb765a62585445ea93c8cf4766b2dda', 'RoomApprove', '预约审批', 2, 'approve', 'work/approve/index', '', 0, 1, 0, 1, 0, 1, 'work:roomApprove', 'approve', 1719390438556, 1718947685871, '');
INSERT INTO `tbl_sys_menu` VALUES ('ce79a6a731fc40dcad0d3afa232d3f42', '575070817b1548769f720287b2d6be61', 'RoomImport', '导入房间数据', 1, 'room', 'import/user/index', '', 0, 1, 0, 1, 0, 1, 'import:room', 'roomImport', 1719391490442, 1718948311410, '');
INSERT INTO `tbl_sys_menu` VALUES ('ce9c612ab5164030b3ee00a858c00d51', 'fd860db51b4c41409b444f8787adec66', '', '删除', 4, '', '', '', 0, 1, 0, 2, 0, 1, 'authority:role:del', '#', 1719371630187, 1719371496664, '');
INSERT INTO `tbl_sys_menu` VALUES ('cf42cdea62ab4554942786e032931983', '9800ef8713a547d4bb7db5693be5ae4b', '', '撤销', 2, '', '', '', 0, 1, 0, 2, 0, 1, 'system:notice:revoke', '#', 1719369462255, 1719369406784, '撤销');
INSERT INTO `tbl_sys_menu` VALUES ('dcd90acfb4d341eb8a680e2e80665ac6', '71eb95391b1c4381abbe2139b1c9c5d6', '', '详情', 2, '', '', '', 0, 1, 0, 2, 0, 1, 'monitor:sysOperateLog:detail', '#', 1719371136965, 1719371136965, '详情');
INSERT INTO `tbl_sys_menu` VALUES ('de47bbbeb5134ce581a74c75634b3209', '48454a3df4844538a3a84c6255d7f0d2', 'OnlineUser', '在线用户', 1, 'online-user', 'monitor/onlineUser/index', '', 0, 1, 0, 1, 0, 1, 'monitor:onlineUser', 'onlineUser', 1719391075952, 1718948053131, '');
INSERT INTO `tbl_sys_menu` VALUES ('de64f0bf2d6e44afba6ee6b2920b8653', '0', 'AboutM', '关于项目', 6, 'about', 'Layout', '', 0, 1, 0, 0, 0, 1, '', 'about', 1719391567596, 1718951465911, '');
INSERT INTO `tbl_sys_menu` VALUES ('e12cec26ebee4f5c9a79b23b8c54e1ff', '4ad1e78175e94cdda674505806926c7e', '', '修改', 14, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:update', '#', 1719379327053, 1719378687947, '');
INSERT INTO `tbl_sys_menu` VALUES ('e868146df6c0407ab97ba38ca4762ad4', '4ad1e78175e94cdda674505806926c7e', '', '补卡审批(wx)', 14, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:wxSignInApprove', '#', 1719385505244, 1719385505244, '');
INSERT INTO `tbl_sys_menu` VALUES ('e8eb90018e4545efadcc248321f0bb2b', 'cc830367d4874fe9b91a7cf09d2b7d01', '', '删除', 4, '', '', '', 0, 1, 0, 2, 0, 1, 'work:roomApprove:del', '#', 1719372569038, 1719372569038, '');
INSERT INTO `tbl_sys_menu` VALUES ('ea8efcbc755641ada6a9672a66be2af8', '4ad1e78175e94cdda674505806926c7e', '', '添加', 1, '', '', '', 0, 1, 0, 2, 0, 1, 'system:room:add', '#', 1719369674412, 1719369486235, '添加');
INSERT INTO `tbl_sys_menu` VALUES ('ead80d743c9c4455ad42dc20806537a2', '9800ef8713a547d4bb7db5693be5ae4b', '', '修改', 3, '', '', '', 0, 1, 0, 2, 0, 1, 'system:notice:update', '#', 1719369465543, 1719369341797, '修改');
INSERT INTO `tbl_sys_menu` VALUES ('eaf6bf4ffae24560a680700544a96b6f', '40cc017031f24b69b78122b268120479', 'SysMenu', '菜单管理', 1, 'menu', 'authority/menu/index', '', 0, 1, 0, 1, 0, 1, 'authority:menu', 'menu', 1719391224410, 1718948211518, '');
INSERT INTO `tbl_sys_menu` VALUES ('fd860db51b4c41409b444f8787adec66', '40cc017031f24b69b78122b268120479', 'SysRole', '角色管理', 2, 'role', 'authority/role/index', '', 0, 1, 0, 1, 0, 1, 'authority:role', 'role', 1719391282329, 1718948244645, '');

-- ----------------------------
-- Table structure for tbl_sys_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `tbl_sys_operate_log`;
CREATE TABLE `tbl_sys_operate_log`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` tinyint NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(216) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求方式',
  `operate_source` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '操作来源（pc, app）',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作人员',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求URL',
  `ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '主机地址',
  `location` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作地点',
  `param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求参数',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '返回结果',
  `state` tinyint NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '错误消息',
  `create_time` bigint NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_operate_log_bt`(`business_type` ASC) USING BTREE,
  INDEX `idx_sys_operate_log_s`(`state` ASC) USING BTREE,
  INDEX `idx_sys_operate_log_ot`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_sys_operate_log
-- ----------------------------
INSERT INTO `tbl_sys_operate_log` VALUES ('019ac7ad76734e398aaebdad865cb427', '禁止预约房间', 2, 'com.guet.ARC.controller.RoomController.disableReserveRoomApi()', 'GET', 'pc', 'admin', '/room/disable/reserve', '0:0:0:0:0:0:0:1', '', '{\"roomId\":\"b281144f33c54885b032ab54402d7c32\"}', NULL, 1, '', 1719305096689, 19);
INSERT INTO `tbl_sys_operate_log` VALUES ('41291c17e44d4eb2a6436fb7231da5f7', '禁止预约房间', 2, 'com.guet.ARC.controller.RoomController.disableReserveRoomApi()', 'GET', 'pc', 'admin3', '/room/disable/reserve', '0:0:0:0:0:0:0:1', '', '{\"roomId\":\"b281144f33c54885b032ab54402d7c32\"}', NULL, 1, '', 1719563950557, 12);
INSERT INTO `tbl_sys_operate_log` VALUES ('8f1406ec52ea4ceb86373b26cc63d264', '禁止预约房间', 2, 'com.guet.ARC.controller.RoomController.disableReserveRoomApi()', 'GET', 'pc', 'admin3', '/room/disable/reserve', '0:0:0:0:0:0:0:1', '', '{\"roomId\":\"b281144f33c54885b032ab54402d7c32\"}', NULL, 1, '', 1719563951429, 14);
INSERT INTO `tbl_sys_operate_log` VALUES ('a2b6f2b11a9240cd8dddc330fffab6b7', '启用/禁用房间', 2, 'com.guet.ARC.controller.RoomController.disableRoom()', 'GET', 'pc', 'admin', '/room/disable', '0:0:0:0:0:0:0:1', '', '{\"id\":\"b281144f33c54885b032ab54402d7c32\"}', NULL, 1, '', 1719305083987, 126);
INSERT INTO `tbl_sys_operate_log` VALUES ('af5fe4aaf91b4318941bf3469465986a', '启用/禁用房间', 2, 'com.guet.ARC.controller.RoomController.disableRoom()', 'GET', 'pc', 'admin3', '/room/disable', '0:0:0:0:0:0:0:1', '', '{\"id\":\"b281144f33c54885b032ab54402d7c32\"}', NULL, 1, '', 1719563947844, 14);
INSERT INTO `tbl_sys_operate_log` VALUES ('c564be6548ff46bb8dc9fdafbba7642c', '更改用户权限', 4, 'com.guet.ARC.controller.UserController.changeRoleApi()', 'POST', 'pc', 'admin', '/admin/update/role', '0:0:0:0:0:0:0:1', '', '{\"userRoleChangeDTO\":\"{\\\"roleIds\\\":[\\\"0\\\"],\\\"userId\\\":\\\"2\\\"}\"}', NULL, 1, '', 1718933034852, 158);
INSERT INTO `tbl_sys_operate_log` VALUES ('c8e10c7c52f7475b84d8df0e00a1aa3d', '启用/禁用房间', 2, 'com.guet.ARC.controller.RoomController.disableRoom()', 'GET', 'pc', 'admin', '/room/disable', '0:0:0:0:0:0:0:1', '', '{\"id\":\"b281144f33c54885b032ab54402d7c32\"}', NULL, 1, '', 1719305086963, 22);
INSERT INTO `tbl_sys_operate_log` VALUES ('d9ca1e58e2b0433fa34dd4e21eea2a09', '禁止预约房间', 2, 'com.guet.ARC.controller.RoomController.disableReserveRoomApi()', 'GET', 'pc', 'admin3', '/room/disable/reserve', '0:0:0:0:0:0:0:1', '', '{\"roomId\":\"b281144f33c54885b032ab54402d7c32\"}', NULL, 1, '', 1719563946162, 120);

-- ----------------------------
-- Table structure for tbl_sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `tbl_sys_role_menu`;
CREATE TABLE `tbl_sys_role_menu`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ID',
  `role_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
  `menu_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单ID'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_sys_role_menu
-- ----------------------------
INSERT INTO `tbl_sys_role_menu` VALUES ('fc1578b3f0b641c0a05cef2461beca21', '69f811fefd5e4512ae55b3728b1c08f0', '4cb765a62585445ea93c8cf4766b2dda');
INSERT INTO `tbl_sys_role_menu` VALUES ('b030758fcdbb441fb2c29188502bab4d', '69f811fefd5e4512ae55b3728b1c08f0', '4f91b0cdc12f4bf49449eebbcfcf005a');
INSERT INTO `tbl_sys_role_menu` VALUES ('ce140d43ac504b32bf77831454d1b50c', '69f811fefd5e4512ae55b3728b1c08f0', '0bc35a6a328041a38958a1e3c9ede82a');
INSERT INTO `tbl_sys_role_menu` VALUES ('19e4cb48d7c44648aa0bcac60caa5bb5', '69f811fefd5e4512ae55b3728b1c08f0', 'bcf94b339f2d43398036c883d1230015');
INSERT INTO `tbl_sys_role_menu` VALUES ('7d7e6ad80118490a868edc1a81666171', '69f811fefd5e4512ae55b3728b1c08f0', 'cc830367d4874fe9b91a7cf09d2b7d01');
INSERT INTO `tbl_sys_role_menu` VALUES ('1b62f3dadade44c18b35fadd7bb5e9c1', '69f811fefd5e4512ae55b3728b1c08f0', '6ea9c08a6b584584a44fadbafece435d');
INSERT INTO `tbl_sys_role_menu` VALUES ('ca568cee8e804cfa8261b647886a04ca', '69f811fefd5e4512ae55b3728b1c08f0', '505d7e2f13a548db959df8a71ce6611a');
INSERT INTO `tbl_sys_role_menu` VALUES ('baae3f551ae94cfa9d723d583e663c18', '69f811fefd5e4512ae55b3728b1c08f0', '30148447b6c747c192e570aa60fd9e55');
INSERT INTO `tbl_sys_role_menu` VALUES ('da3f82a79401450683f3604962dbd573', '69f811fefd5e4512ae55b3728b1c08f0', 'e8eb90018e4545efadcc248321f0bb2b');
INSERT INTO `tbl_sys_role_menu` VALUES ('ec93a668d42241aaa90b1a6b68bb6515', '69f811fefd5e4512ae55b3728b1c08f0', '222021087fce4739a8e59a2315aca75c');
INSERT INTO `tbl_sys_role_menu` VALUES ('87377bc744a14cda80e09506f87c46e6', '69f811fefd5e4512ae55b3728b1c08f0', '9800ef8713a547d4bb7db5693be5ae4b');
INSERT INTO `tbl_sys_role_menu` VALUES ('58f6242e8fb44db280e0b851c8419f0c', '69f811fefd5e4512ae55b3728b1c08f0', 'c4a34226f1d04d5a98cf3f51ba322e47');
INSERT INTO `tbl_sys_role_menu` VALUES ('796f68d6b20d4896931972180a579a7c', '69f811fefd5e4512ae55b3728b1c08f0', 'cf42cdea62ab4554942786e032931983');
INSERT INTO `tbl_sys_role_menu` VALUES ('075d347f666a4e1f877647fc608d05d4', '69f811fefd5e4512ae55b3728b1c08f0', 'ead80d743c9c4455ad42dc20806537a2');
INSERT INTO `tbl_sys_role_menu` VALUES ('3da9007611aa46579a611916b22dce40', '69f811fefd5e4512ae55b3728b1c08f0', '4ad1e78175e94cdda674505806926c7e');
INSERT INTO `tbl_sys_role_menu` VALUES ('2946edc990f949cfa52006755c8a0c70', '69f811fefd5e4512ae55b3728b1c08f0', 'ea8efcbc755641ada6a9672a66be2af8');
INSERT INTO `tbl_sys_role_menu` VALUES ('df2d5c3c68ca466eaea2a01769181576', '69f811fefd5e4512ae55b3728b1c08f0', '149c98c1f29f422493b11512ea941ca3');
INSERT INTO `tbl_sys_role_menu` VALUES ('9d02bb7720ce4af399c23f1472a8c08b', '69f811fefd5e4512ae55b3728b1c08f0', '1f8f5eeb57ed49658151fdd8746616a5');
INSERT INTO `tbl_sys_role_menu` VALUES ('08b4dfefd9054a3e9d2eb53061f7f985', '69f811fefd5e4512ae55b3728b1c08f0', '6733fb0162104246bee921755fa071d4');
INSERT INTO `tbl_sys_role_menu` VALUES ('d267eeab0e234f17adca77fbc69fb5d3', '69f811fefd5e4512ae55b3728b1c08f0', '3dbc18b14d004d31bd2f753c29657719');
INSERT INTO `tbl_sys_role_menu` VALUES ('c64761b3193a4e8bb86ef366e8440289', '69f811fefd5e4512ae55b3728b1c08f0', '5a00f45da3af4ca19d44887149d85f4b');
INSERT INTO `tbl_sys_role_menu` VALUES ('ba62fc87a5c14879ac1c1175d0eba1d5', '69f811fefd5e4512ae55b3728b1c08f0', '8269eeb76fcc4fe787abce3bfb419a57');
INSERT INTO `tbl_sys_role_menu` VALUES ('5f23d6a043bd4be491f9e254a02f9eaf', '69f811fefd5e4512ae55b3728b1c08f0', '2741b2f5d80f4efebe56f6cd336edb73');
INSERT INTO `tbl_sys_role_menu` VALUES ('c84a042705d9493cb3ffe13b2537c23d', '69f811fefd5e4512ae55b3728b1c08f0', '6fdb0ced21d940bda1e5f0eebff88e06');
INSERT INTO `tbl_sys_role_menu` VALUES ('ff03097880aa4d4c9ace9625bb79873b', '69f811fefd5e4512ae55b3728b1c08f0', '9fc8a2d9a1464e45b6e66712405b647d');
INSERT INTO `tbl_sys_role_menu` VALUES ('9e485b61d96740c78f677eb773ed1f65', '69f811fefd5e4512ae55b3728b1c08f0', 'b7bad81cf17444988dc8a36310970584');
INSERT INTO `tbl_sys_role_menu` VALUES ('cd29eaae13c0469895aca995efa7199d', '69f811fefd5e4512ae55b3728b1c08f0', '8e6c65f153174a58a5613d51c3617818');
INSERT INTO `tbl_sys_role_menu` VALUES ('fe831fa86ab74513a9de7e65238afd0e', '69f811fefd5e4512ae55b3728b1c08f0', '2c8c0c5d366040d3a76cfd67f9256d13');
INSERT INTO `tbl_sys_role_menu` VALUES ('fa74200fc4eb4959bec8cbaa297b885a', '69f811fefd5e4512ae55b3728b1c08f0', '5632924876e6490e9bf53d4b35b78e49');
INSERT INTO `tbl_sys_role_menu` VALUES ('bbe71e9c5f20417991842b20cd28ce08', '69f811fefd5e4512ae55b3728b1c08f0', 'e12cec26ebee4f5c9a79b23b8c54e1ff');
INSERT INTO `tbl_sys_role_menu` VALUES ('04df63e3e9684204a073a836b281ae31', '69f811fefd5e4512ae55b3728b1c08f0', 'e868146df6c0407ab97ba38ca4762ad4');
INSERT INTO `tbl_sys_role_menu` VALUES ('bc724ba78d0741b3b2403ea3549b3536', '69f811fefd5e4512ae55b3728b1c08f0', '0b5b49b9611f437797c1df7a3cd57d8f');
INSERT INTO `tbl_sys_role_menu` VALUES ('d49eb8d5beae4119a906862cac2cd7c0', '69f811fefd5e4512ae55b3728b1c08f0', '26f5e7b1ddc04f6a92ea3e4f3649b5d5');
INSERT INTO `tbl_sys_role_menu` VALUES ('3f8dc4d9fedc4583b90ba9f48db722bd', '69f811fefd5e4512ae55b3728b1c08f0', '74355f5f5b494cdd91cc218b1176708d');
INSERT INTO `tbl_sys_role_menu` VALUES ('3b1816e010f64a6ebeb8e498d70f2b2e', '69f811fefd5e4512ae55b3728b1c08f0', '9cc0f5e677834bf9bbb30d1d9b0479dc');
INSERT INTO `tbl_sys_role_menu` VALUES ('8677c61f5e5048758153a26b4b8e9a6e', '69f811fefd5e4512ae55b3728b1c08f0', '0b1360105a674950ad2f66ba35d4544c');
INSERT INTO `tbl_sys_role_menu` VALUES ('b17952132af6470c91a63f2945518bb9', '69f811fefd5e4512ae55b3728b1c08f0', '554fb409bf1b4b9a8e8a08049ac8bf38');
INSERT INTO `tbl_sys_role_menu` VALUES ('c74e74fe1e74487eaa207d652f712e9b', '69f811fefd5e4512ae55b3728b1c08f0', '42b5c2a99ceb439986bc536938286f9f');
INSERT INTO `tbl_sys_role_menu` VALUES ('31ced7644bbe415e81ff383d49be5d0e', '69f811fefd5e4512ae55b3728b1c08f0', '7f303b2ce1794dc9b5322af688a55bf6');
INSERT INTO `tbl_sys_role_menu` VALUES ('421d74aa40344049959f07becd1b5b8a', '69f811fefd5e4512ae55b3728b1c08f0', '8bef4171e7cd46ef9d3d960a7caa80bc');
INSERT INTO `tbl_sys_role_menu` VALUES ('241aefa574674cb09c4558fc0f593896', '69f811fefd5e4512ae55b3728b1c08f0', '43ad77ad7ca54339a17b8ad7bdd65968');
INSERT INTO `tbl_sys_role_menu` VALUES ('41915a307c6c4ec7bd22e4521752ea68', '69f811fefd5e4512ae55b3728b1c08f0', '64cbf16ac5174c2198e53961044fd0b6');
INSERT INTO `tbl_sys_role_menu` VALUES ('5f8f2908731a4104a95d9fc0b5d8680d', '69f811fefd5e4512ae55b3728b1c08f0', '81e423f5a31b485b8196e728510a9b1f');
INSERT INTO `tbl_sys_role_menu` VALUES ('13f10de70c95438198934803539c9d88', '69f811fefd5e4512ae55b3728b1c08f0', '48454a3df4844538a3a84c6255d7f0d2');
INSERT INTO `tbl_sys_role_menu` VALUES ('4818b9a4abbd43c0938268413eca6c9c', '69f811fefd5e4512ae55b3728b1c08f0', '71eb95391b1c4381abbe2139b1c9c5d6');
INSERT INTO `tbl_sys_role_menu` VALUES ('f88ac510ea034bb484d9479454c0bbb7', '69f811fefd5e4512ae55b3728b1c08f0', '33e3d1de669344ac9a9adba6df367270');
INSERT INTO `tbl_sys_role_menu` VALUES ('63b73273d66a4bd1b88a3ba5320dc771', '69f811fefd5e4512ae55b3728b1c08f0', 'dcd90acfb4d341eb8a680e2e80665ac6');
INSERT INTO `tbl_sys_role_menu` VALUES ('12fb78bacc90472483973d94a74ef35e', '69f811fefd5e4512ae55b3728b1c08f0', 'de47bbbeb5134ce581a74c75634b3209');
INSERT INTO `tbl_sys_role_menu` VALUES ('71dab5e0e17d4f51a9e96f9656f3a696', '69f811fefd5e4512ae55b3728b1c08f0', '1a15d0b3ae074370805f478785319f6b');
INSERT INTO `tbl_sys_role_menu` VALUES ('4c1157a08c3c424d8fcb727740552c0f', '69f811fefd5e4512ae55b3728b1c08f0', '25fcebd07bdd42c080a3acc856c13c35');
INSERT INTO `tbl_sys_role_menu` VALUES ('30efd08081eb4f0fa51d467594a1232c', '69f811fefd5e4512ae55b3728b1c08f0', '40cc017031f24b69b78122b268120479');
INSERT INTO `tbl_sys_role_menu` VALUES ('dbe403dc37e54eb88841de4e598a0a03', '69f811fefd5e4512ae55b3728b1c08f0', 'eaf6bf4ffae24560a680700544a96b6f');
INSERT INTO `tbl_sys_role_menu` VALUES ('5267644b28aa4057878a94465bbaa0cc', '69f811fefd5e4512ae55b3728b1c08f0', '340ff559c39849ca8e6bdeae0f43a403');
INSERT INTO `tbl_sys_role_menu` VALUES ('68e6b751d82644bf8b880b1040aaa952', '69f811fefd5e4512ae55b3728b1c08f0', '6b469f9b8bae400e984034925fbdefab');
INSERT INTO `tbl_sys_role_menu` VALUES ('8c42fd7e13394103b78d72ecf90cade2', '69f811fefd5e4512ae55b3728b1c08f0', 'b2345457936145c6853a0f4e41feb529');
INSERT INTO `tbl_sys_role_menu` VALUES ('1e2aa270200c4eabb1ede1c4ca5e4141', '69f811fefd5e4512ae55b3728b1c08f0', 'fd860db51b4c41409b444f8787adec66');
INSERT INTO `tbl_sys_role_menu` VALUES ('ee5a3fb622fa4266b82e01d913e9e77c', '69f811fefd5e4512ae55b3728b1c08f0', '1b4c7b059344433fbdd9a7bc7fc60610');
INSERT INTO `tbl_sys_role_menu` VALUES ('ff5994b7267f4b59bbec10102f696984', '69f811fefd5e4512ae55b3728b1c08f0', '556ac64b205449efa23dc85da21297d0');
INSERT INTO `tbl_sys_role_menu` VALUES ('552896fd99ab4ea79ebc61156ad9e7c6', '69f811fefd5e4512ae55b3728b1c08f0', 'b87648f077e3410aa2abf09366ff134d');
INSERT INTO `tbl_sys_role_menu` VALUES ('88d9a4de29c048ca9b82f65739efd47c', '69f811fefd5e4512ae55b3728b1c08f0', 'ce9c612ab5164030b3ee00a858c00d51');
INSERT INTO `tbl_sys_role_menu` VALUES ('68a6b93dae0c42da8c49ec89c61ab4ca', '69f811fefd5e4512ae55b3728b1c08f0', '32678d16bdaf418dad4acd33f45db3dc');
INSERT INTO `tbl_sys_role_menu` VALUES ('03cf464b4496466c914ebf9e4a6ed348', '69f811fefd5e4512ae55b3728b1c08f0', '7fbde6194fca4640a4d468caa3861d2e');
INSERT INTO `tbl_sys_role_menu` VALUES ('1fc134f831df4f43ab5a5f6007d9218a', '69f811fefd5e4512ae55b3728b1c08f0', '575070817b1548769f720287b2d6be61');
INSERT INTO `tbl_sys_role_menu` VALUES ('7a9d225887014b999534e0772ed57915', '69f811fefd5e4512ae55b3728b1c08f0', '58d39f011946499eb8345b0e1ac2e48a');
INSERT INTO `tbl_sys_role_menu` VALUES ('1cd171bd87ec43a6b1706a117b571b60', '69f811fefd5e4512ae55b3728b1c08f0', 'ce79a6a731fc40dcad0d3afa232d3f42');
INSERT INTO `tbl_sys_role_menu` VALUES ('18951aec80b24f6cbd4096f14a35696b', '69f811fefd5e4512ae55b3728b1c08f0', 'de64f0bf2d6e44afba6ee6b2920b8653');
INSERT INTO `tbl_sys_role_menu` VALUES ('4aeb5b997f764978a19958524d13bcf6', '69f811fefd5e4512ae55b3728b1c08f0', '97df460bdc034be79d313b31b78d39a9');

-- ----------------------------
-- Table structure for tbl_user
-- ----------------------------
DROP TABLE IF EXISTS `tbl_user`;
CREATE TABLE `tbl_user`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `pwd` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `stu_num` varchar(62) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `institute` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `state` smallint NULL DEFAULT NULL COMMENT '状态：-1：代表数据已经逻辑删除，1：代表数据正常',
  `update_time` bigint NULL DEFAULT NULL,
  `create_time` bigint NULL DEFAULT NULL,
  `mail` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮件',
  `open_id` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_user
-- ----------------------------
INSERT INTO `tbl_user` VALUES ('2', 'e10adc3949ba59abbe56e057f20f883e', '202300003', 'admin2', '计算机与信息安全学院', 1, 1719297064999, 1708593015901, '1832648@163.com', NULL);
INSERT INTO `tbl_user` VALUES ('344', 'e10adc3949ba59abbe56e057f20f883e', '202300004', 'admin3', '计算机与信息安全学院', 1, 1719299129451, 1719299129451, '1838548@163.com', NULL);
INSERT INTO `tbl_user` VALUES ('8e6b34a790c046e5a5b8bbf47ce7c190', 'e10adc3949ba59abbe56e057f20f883e', '202300001', 'admin', '计算机与信息安全学院', 1, 1708593290833, 1708593015901, '1843@163.com', NULL);
INSERT INTO `tbl_user` VALUES ('ae391d3580b2433fb8dcae22caae1643', 'e10adc3949ba59abbe56e057f20f883e', '202300002', 'User2', '计算机与信息安全学院', 1, 1719299129451, 1718700711371, '1890543@163.com', NULL);

-- ----------------------------
-- Table structure for tbl_user_role
-- ----------------------------
DROP TABLE IF EXISTS `tbl_user_role`;
CREATE TABLE `tbl_user_role`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标识',
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户外键',
  `role_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色外键',
  `state` smallint NULL DEFAULT NULL COMMENT '状态：-1：代表数据已经逻辑删除，1：代表数据正常',
  `create_time` bigint NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `role_user_fk`(`user_id` ASC) USING BTREE,
  INDEX `role_role_user_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `role_role_user_id` FOREIGN KEY (`role_id`) REFERENCES `tbl_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `role_user_fk` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_user_role
-- ----------------------------
INSERT INTO `tbl_user_role` VALUES ('222', '8e6b34a790c046e5a5b8bbf47ce7c190', '69f811fefd5e4512ae55b3728b1c08f0', 1, 1719303324649, 1719303324649);
INSERT INTO `tbl_user_role` VALUES ('799ca2d8c7e443aabf0c46e8aafc8c48', '344', '2', 1, 1719388993379, 1719388993379);
INSERT INTO `tbl_user_role` VALUES ('fa4ac33a9da94dcaa212c1b4a5ab5b05', '2', '1', 1, 1719388712059, 1719388712059);

SET FOREIGN_KEY_CHECKS = 1;
