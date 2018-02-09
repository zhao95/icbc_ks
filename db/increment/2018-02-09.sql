-- 增加 ts_jklb_jk 表 JK_DEPT 字段长度
ALTER TABLE `ts_jklb_jk`
MODIFY COLUMN `JK_DEPT`  varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '借考人所在的部门';
