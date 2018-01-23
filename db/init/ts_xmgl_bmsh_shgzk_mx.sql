/*
Navicat MySQL Data Transfer

Source Server         : icbc_ks_208
Source Server Version : 50717
Source Host           : 172.16.0.208:3306
Source Database       : icbc_ks2

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-01-13 17:07:44
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ts_xmgl_bmsh_shgzk_mx
-- ----------------------------
DROP TABLE IF EXISTS `ts_xmgl_bmsh_shgzk_mx`;
CREATE TABLE `ts_xmgl_bmsh_shgzk_mx` (
  `GZ_NAME` varchar(400) DEFAULT NULL COMMENT '规则类型',
  `MX_ID` varchar(40) NOT NULL COMMENT '主键id',
  `GZ_ID` varchar(40) DEFAULT NULL COMMENT '规则id',
  `MX_NAME` text COMMENT '名称',
  `MX_VALUE1` varchar(40) DEFAULT NULL COMMENT '值1',
  `MX_VALUE2` text COMMENT '值2',
  `S_USER` varchar(40) DEFAULT NULL COMMENT '创建者',
  `S_TDEPT` varchar(40) DEFAULT NULL COMMENT '有效部门',
  `S_ODEPT` varchar(40) DEFAULT NULL COMMENT '有效机构',
  `S_MTIME` varchar(40) DEFAULT NULL COMMENT '修改时间',
  `S_FLAG` decimal(4,0) DEFAULT NULL COMMENT '有效标志',
  `S_DEPT` varchar(40) DEFAULT NULL COMMENT '部门',
  `S_CMPY` varchar(40) DEFAULT NULL COMMENT '公司',
  `S_ATIME` varchar(40) DEFAULT NULL COMMENT '创建时间',
  `MX_IMPL` varchar(100) DEFAULT NULL COMMENT '实现类',
  `MX_SORT` decimal(4,0) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`MX_ID`),
  UNIQUE KEY `PK_TS_XMGL_BMSH_SHGZK_MX` (`MX_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目管理报名审核审核规则库明细';

-- ----------------------------
-- Records of ts_xmgl_bmsh_shgzk_mx
-- ----------------------------
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('证书规则', '2zyg9LcUNb1EEYVN0yhT', 'Y01', '是否曾经获得过当前考试类别证书', '1', '', 'admin', '', '0010100000', '2018-01-11 11:08:47:773', '1', '0010100000', 'icbc', '2018-01-11 11:08:47:774', 'com.rh.ts.xmgl.rule.impl.EverHasCret', '0');
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('准入规则', 'N0101', 'N01', '准入测试 (准入成绩大于等于#grade#分)', '1', '[{vari:\'grade\',val:\'3\',type:\'grade\',code:\'1\'}]', 'admin', '2cdEVD2ndfR8a-TxqqUK-C', 'rh', '2018-01-12 15:00:54:391', '1', '2cdEVD2ndfR8a-TxqqUK-C', 'ruaho', '2017-08-10 16:18:29:920', 'com.rh.ts.xmgl.rule.impl.AdmitTest', '1');
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('未持证人员夸序列报考限制', 'N0201', 'N02', '是否按要求持证', '2', null, '278p246BZ2jNaftzl7kooot', 'icbc0001', 'icbc0001', '2017-08-17 10:40:23:532', '1', 'icbc0001', 'icbc', '2017-07-28 17:41:16:810', 'com.rh.ts.xmgl.rule.impl.ObtainCert', null);
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('准入测试规则', 'N0401', 'N04', '第一期参考已达60分的考试类别', '2', null, '278p246BZ2jNaftzl7kooot', 'icbc0001', 'icbc0001', '2017-08-17 10:40:34:000', '1', 'icbc0001', 'icbc', '2017-07-28 17:49:59:623', 'com.rh.ts.xmgl.rule.impl.FirstExamScorePass', null);
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('证书规则', 'Y0104', 'Y01', '跨序列报考当前考试时是否已经获得#level##level#有效证书', '1', '[{\'vari\':\'level\',\'val\':\'>\',\'type\':\'level\',\'code\':\'1\'},{\'vari\':\'level\',\'val\':\'中级\',\'type\':\'level\',\'code\':\'2\'}]', '278p246BZ2jNaftzl7kooot', 'icbc0001', 'icbc0001', '2018-01-06 14:44:56:059', '1', 'icbc0001', 'icbc', '2017-07-28 17:57:16:680', 'com.rh.ts.xmgl.rule.impl.BaseValidCert2YearBkxl', '2');
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('证书规则', 'Y0107', 'Y01', '报考本序列证书时是否获取本序列#dateTime##dateTime#有效证书', '1', '[{vari:\'dateTime\',val:\'>\',type:\'date\',code:\'1\'},{vari:\'dateTime\',val:\'初级\',type:\'date\',code:\'1\'}]', '278p246BZ2jNaftzl7kooot', 'icbc0001', 'icbc0001', '2017-12-11 20:11:12:764', '1', 'icbc0001', 'icbc', '2017-07-28 17:59:12:630', 'com.rh.ts.xmgl.rule.impl.BeforeHighCertDue', '4');
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('证书规则', 'Y0110', 'Y01', '岗位类持证规则', '2', '[{\'vari\':\'gwgz\',\'val\':\'gwgz\',\'type\':\'gwgz\'}]', 'admin', '2cdEVD2ndfR8a-TxqqUK-C', 'rh', '2017-09-18 13:37:36:083', '1', '2cdEVD2ndfR8a-TxqqUK-C', 'ruaho', '2017-08-21 15:19:17:833', 'com.rh.ts.xmgl.rule.impl.StaffPost', '1');
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('证书规则', 'Y01100', 'Y01', '报考投行初级时是否已获#muty#、#muty##muty##muty#有效证书', '1', '[{vari:\'muty\',val:\'研究分析\',type:\'muty\',code:\'A000000000000000015\'},{vari:\'muty\',val:\'交易\',type:\'muty\',code:\'A000000000000000009\'},{vari:\'muty\',val:\'>=\',type:\'muty\',code:\'3\'},{vari:\'muty\',val:\'初级\',type:\'muty\',code:\'1\'}]', 'admin', null, '0010100000', '2017-12-11 20:10:30:866', '1', '0010100000', 'icbc', '2017-09-18 11:28:04:558', 'com.rh.ts.xmgl.rule.impl.BaseCert2YearDgYxXd', '5');
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('证书规则', 'Y011001', 'Y01', '是否已获取#yearmuty#、#yearmuty##yearmuty##yearmuty#满#yearmuty#年有效证书', '1', '[{vari:\'yearmuty\',val:\'研究分析\',type:\'yearmuty\',code:\'A000000000000000015\'},{vari:\'yearmuty\',val:\'交易\',type:\'yearmuty\',code:\'A000000000000000009\'},{vari:\'yearmuty\',val:\'>=\',type:\'yearmuty\',code:\'3\'},{vari:\'yearmuty\',val:\'初级\',type:\'yearmuty\',code:\'1\'},{vari:\'yearmuty\',val:\'255\',type:\'yearmuty\'}]', 'admin', null, '0010100000', '2018-01-08 19:42:39:152', '1', '0010100000', 'icbc', '2017-09-18 11:28:04:558', 'com.rh.ts.xmgl.rule.impl.BaseCert2Year', '50');
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('证书规则', 'Y0110102', 'Y01', '是否已获取#XinDaimuty#、#XinDaimuty##XinDaimuty##XinDaimuty#有效证书并且信贷从业经历已满#XinDaimuty#年', '1', '[{vari:\'XinDaimuty\',val:\'研究分析\',type:\'XinDaimuty\',code:\'A000000000000000015\'},{vari:\'XinDaimuty\',val:\'交易\',type:\'XinDaimuty\',code:\'A000000000000000009\'},{vari:\'XinDaimuty\',val:\'>=\',type:\'XinDaimuty\',code:\'3\'},{vari:\'XinDaimuty\',val:\'初级\',type:\'XinDaimuty\',code:\'1\'},{vari:\'XinDaimuty\',val:\'20\',type:\'XinDaimuty\'}]', 'admin', null, '0010100000', '2018-01-08 19:42:34:504', '1', '0010100000', 'icbc', '2017-09-18 11:28:04:558', 'com.rh.ts.xmgl.rule.impl.BaseCert2YearXinDai', '60');
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('证书规则', 'Y01102', 'Y01', '是否已获#select##select##select##select#有效证书', '1', '[{vari:\'select\',val:\'销售类\',type:\'select\',code:\'023003\'},{vari:\'select\',val:\'客服类\',type:\'select\',code:\'023005\'},{vari:\'select\',val:\'>\',type:\'select\',code:\'1\'},{vari:\'select\',val:\'中级\',type:\'select\',code:\'2\'}]', 'admin', null, '0010100000', '2018-01-03 15:07:41:477', '1', '0010100000', 'icbc', '2017-09-18 11:29:58:983', 'com.rh.ts.xmgl.rule.impl.HighValidCertYxKf', '7');
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('证书规则', 'Y01108', 'Y01', '管理类#XL#、#XL#、#XL#序列的证书验证规则', '1', '[{vari:\'XL\',val:\'产品\',type:\'XL\',code:\'A000000000000000007\'},{vari:\'XL\',val:\'营销\',type:\'XL\',code:\'A000000000000000006\'},{vari:\'XL\',val:\'研究分析\',type:\'XL\',code:\'A000000000000000015\'}]', '0000000017', '0010100546', '0010100000', '2017-12-12 11:34:39:090', '1', '0010100546', 'icbc', '2017-10-12 17:38:36:720', 'com.rh.ts.xmgl.rule.impl.GlVerifying', '0');
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('证书规则', 'Y01109', 'Y01', '您的岗位要任职满2年才能参加所选考试任职年限后提交进行人工审核', '1', '[{\'vari\':\'rzyear\',\'val\':\'0\',\'type\':\'int\'}]', '0000000017', '0010100546', '0010100000', '2017-12-12 11:34:38:670', '1', '0010100546', 'icbc', '2017-10-26 16:53:51:552', '', '0');
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('证书规则', 'Y01110', 'Y01', '信贷类任职是否满#XinDai#年', '1', '[{\'vari\':\'XinDai\',\'val\':\'2\',\'type\':\'int\'}]', '0000000017', '0010100546', '0010100000', '2017-10-24 10:13:32:481', '1', '0010100546', 'icbc', '2017-10-12 17:38:36:720', 'com.rh.ts.xmgl.rule.impl.XinDaiLimit', '0');
INSERT INTO `ts_xmgl_bmsh_shgzk_mx` VALUES ('入行时间年限规则', 'Y0201', 'Y02', '入行时间要求 #datetime# 年之前', '1', '[{vari:\'datetime\',val:\'20170101\',type:\'dateyear\'}]', '0000000017', '0010100546', '0010100000', '2017-12-11 20:11:02:269', '1', '0010100546', 'icbc', '2017-10-11 13:25:48:888', 'com.rh.ts.xmgl.rule.impl.YearLimit', '0');
