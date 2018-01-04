-- 考场变更问题修复
ALTER TABLE `ts_kcgl_update_gljg` ADD COLUMN `ROOT_ID` varchar(40) NULL;
ALTER TABLE `ts_kcgl_update_gly` ADD COLUMN `ROOT_ID` varchar(40) NULL;
ALTER TABLE `ts_kcgl_update_ipscope` ADD COLUMN `ROOT_ID` varchar(40) NULL;
ALTER TABLE `ts_kcgl_update_ipzwh` ADD COLUMN `ROOT_ID` varchar(40) NULL;
ALTER TABLE `ts_kcgl_update_jkip` ADD COLUMN `ROOT_ID` varchar(40) NULL;
ALTER TABLE `ts_kcgl_update_zwdyb` ADD COLUMN `ROOT_ID` varchar(40) NULL;