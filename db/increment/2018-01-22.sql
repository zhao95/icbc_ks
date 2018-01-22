alter table ts_bmsh_rule add COLUMN CONDITION_ONE VARCHAR(400) NOT NULL;
alter table ts_bmsh_rule add COLUMN CONDITION_TWO VARCHAR(400) NOT NULL;
alter table ts_bmsh_rule add COLUMN VALID_START VARCHAR(400) NOT NULL;
alter table ts_bmsh_rule add COLUMN VALID_END VARCHAR(400) NOT NULL;

DROP TABLE IF EXISTS `TS_BMSH_RULE_POST`;
CREATE TABLE `TS_BMSH_RULE_POST` (
  ROLE_DEPT_ID VARCHAR(400) NOT NULL COMMENT '主键',
  DEPT_CODE VARCHAR(400) DEFAULT NULL COMMENT '部门编码',
  POST_CODE VARCHAR(400) DEFAULT NULL COMMENT '职务层级',
  GOTO_STAY VARCHAR(400) DEFAULT NULL COMMENT '待审核标志',
  R_ID VARCHAR(400) DEFAULT NULL COMMENT '关联建',
 `S_USER` varchar(40) DEFAULT NULL COMMENT '创建者',
  `S_TDEPT` varchar(40) DEFAULT NULL COMMENT '有效部门',
  `S_MTIME` varchar(40) DEFAULT NULL COMMENT '修改时间',
  `S_FLAG` decimal(4,0) DEFAULT NULL COMMENT '有效标志',
  `S_DEPT` varchar(40) DEFAULT NULL COMMENT '部门',
  `S_CMPY` varchar(40) DEFAULT NULL COMMENT '公司',
  `S_ATIME` varchar(40) DEFAULT NULL COMMENT '创建时间',
  `S_ODEPT` varchar(40) DEFAULT NULL COMMENT '有效机构',
  PRIMARY KEY (`ROLE_DEPT_ID`),
  UNIQUE KEY `ROLE_DEPT_ID` (`ROLE_DEPT_ID`) USING BTREE,
   KEY `R_ID` (`R_ID`),
  KEY `DEPT_CODE` (`DEPT_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
