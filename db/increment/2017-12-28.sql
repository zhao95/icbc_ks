-- ts_xmgl_sz 添加字段  XM_SZ_TYPENUM
ALTER TABLE `TS_XMGL_SZ` ADD COLUMN `XM_SZ_TYPENUM` decimal(4,0) NULL;

ALTER TABLE `TS_KCGL_UPDATE_ZWDYB` ADD COLUMN `zw_ip` varchar(20) NULL;
ALTER TABLE `TS_KCGL_UPDATE_ZWDYB` ADD COLUMN `zw_desc` varchar(400) NULL;