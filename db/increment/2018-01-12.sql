UPDATE `ts_xmgl_bmsh_shgzk_mx` SET `GZ_NAME`='证书规则', `MX_ID`='2zyg9LcUNb1EEYVN0yhT', `GZ_ID`='Y01', `MX_NAME`='曾获考试类别证书已过期则有效', `MX_VALUE1`='1', `MX_VALUE2`='', `S_USER`='admin', `S_TDEPT`='', `S_ODEPT`='0010100000', `S_MTIME`='2018-01-11 11:08:47:773', `S_FLAG`='1', `S_DEPT`='0010100000', `S_CMPY`='icbc', `S_ATIME`='2018-01-11 11:08:47:774', `MX_IMPL`='com.rh.ts.xmgl.rule.impl.EverHasCret', `MX_SORT`='0' WHERE (`MX_ID`='2zyg9LcUNb1EEYVN0yhT');

ALTER TABLE TS_PVLG_GROUP ADD COLUMN G_DEL  decimal(1,0) default 0;
ALTER TABLE TS_PVLG_ROLE ADD COLUMN ROLE_DEL  decimal(1,0) default 0;


ALTER TABLE `ts_kcgl_update` ADD COLUMN `SHR_NAME` varchar(40) NULL;
ALTER TABLE `ts_kcgl_update` ADD COLUMN `SHR_ODEPT_NAME` varchar(40) NULL;

-- 项目报名群组人员添加字段
ALTER TABLE `ts_bm_group_user_dept` ADD COLUMN `XM_ID`  varchar(400) NULL;

-- 更改考场安排规则提示语
UPDATE `ts_xmgl_kcap_gzk` SET `GZ_NAME`='只安排距离近的机构' WHERE (`GZ_CODE`='R003');
UPDATE `ts_xmgl_kcap_gzk` SET `GZ_NAME`='特定机构考生场次靠前靠后安排' WHERE (`GZ_CODE`='R008');
UPDATE `ts_xmgl_kcap_gz` SET `GZ_NAME`='只安排距离近的机构' WHERE (`GZ_CODE`='R003');
UPDATE `ts_xmgl_kcap_gz` SET `GZ_NAME`='特定机构考生场次靠前靠后安排' WHERE (`GZ_CODE`='R008');
