ALTER TABLE `ts_kcgl_update` ADD COLUMN `SHR_NAME` varchar(40) NULL;
ALTER TABLE `ts_kcgl_update` ADD COLUMN `SHR_ODEPT_NAME` varchar(40) NULL;

-- 项目报名群组人员添加字段
ALTER TABLE `ts_bm_group_user_dept`
ADD COLUMN `XM_ID`  varchar(400) NULL AFTER `G_ID`;

ALTER TABLE `ts_bm_group_user_dept`
ADD COLUMN `ODEPT_CODE`  varchar(400) NULL AFTER `SEN_LEVEL`;