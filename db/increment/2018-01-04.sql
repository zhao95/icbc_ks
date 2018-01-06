-- 考场变更问题修复
ALTER TABLE `ts_kcgl_update_gljg` ADD COLUMN `ROOT_ID` varchar(40) NULL;
ALTER TABLE `ts_kcgl_update_gly` ADD COLUMN `ROOT_ID` varchar(40) NULL;
ALTER TABLE `ts_kcgl_update_ipscope` ADD COLUMN `ROOT_ID` varchar(40) NULL;
ALTER TABLE `ts_kcgl_update_ipzwh` ADD COLUMN `ROOT_ID` varchar(40) NULL;
ALTER TABLE `ts_kcgl_update_jkip` ADD COLUMN `ROOT_ID` varchar(40) NULL;
ALTER TABLE `ts_kcgl_update_zwdyb` ADD COLUMN `ROOT_ID` varchar(40) NULL;

-- 添加表ts_wfs_node_history
DROP TABLE IF EXISTS `ts_wfs_node_history`;
CREATE TABLE `ts_wfs_node_history` (
  `NODE_HISTROY_ID` varchar(40) NOT NULL,
  `NODE_NAME` varchar(100) DEFAULT NULL COMMENT '节点名称',
  `NODE_NUM` varchar(40) DEFAULT NULL COMMENT '节点排序',
  `S_USER` varchar(40) DEFAULT NULL COMMENT '创建者',
  `S_TDEPT` varchar(40) DEFAULT NULL COMMENT '有效部门',
  `S_ODEPT` varchar(40) DEFAULT NULL COMMENT '有效机构',
  `S_MTIME` varchar(40) DEFAULT NULL COMMENT '修改时间',
  `S_FLAG` decimal(4,0) DEFAULT NULL COMMENT '有效标志',
  `S_DEPT` varchar(40) DEFAULT NULL COMMENT '部门',
  `S_CMPY` varchar(40) DEFAULT NULL COMMENT '公司',
  `S_ATIME` varchar(40) DEFAULT NULL COMMENT '创建时间',
  `DATA_ID` varchar(40) DEFAULT NULL COMMENT '流程ID',
  `WFS_ID` varchar(40) DEFAULT NULL COMMENT '数据ID',
  `NODE_STEPS` varchar(40) DEFAULT NULL COMMENT '级数',
  PRIMARY KEY (`NODE_HISTROY_ID`),
  UNIQUE KEY `SYS_C009946` (`NODE_HISTROY_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流程节点历史记录';
