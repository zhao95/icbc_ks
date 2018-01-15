DROP TABLE IF EXISTS `ts_bmgz_admit_grade`;
CREATE TABLE `ts_bmgz_admit_grade` (
  `KSQZ_ID` varchar(400) NOT NULL COMMENT '群组ID',
  `ADMIT_ID` varchar(400) NOT NULL COMMENT '主键ID',
  `KSLBK_ID` varchar(400) NOT NULL COMMENT '项目考试',
  `kslbk_admit_id` varchar(400) NOT NULL COMMENT '准入测试ID',
  `KSLB_XL` varchar(400) DEFAULT NULL COMMENT '准入序列',
  `KSLB_MK` varchar(400) DEFAULT NULL COMMENT '准入模块',
  `KSLB_TYPE` varchar(400) DEFAULT NULL COMMENT '准入等级',
  `KSLB_LB` varchar(400) DEFAULT NULL COMMENT '准入类别',
  PRIMARY KEY (`ADMIT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;