ALTER TABLE `ts_pvlg_group_user` ADD COLUMN DUTY_LV_CODE VARCHAR(40) COMMENT '职位层级编码';

DROP VIEW IF EXISTS `ts_pvlg_group_user_v`;

CREATE  VIEW `ts_pvlg_group_user_v` AS 
SELECT
  `a`.`GU_ID`        AS `GU_ID`,
  `a`.`G_ID`         AS `G_ID`,
  (SELECT
     `t`.`G_NAME`
   FROM `ts_pvlg_group` `t`
   WHERE (`t`.`G_ID` = `a`.`G_ID`)) AS `G_NAME`,
  `a`.`USER_CODE`    AS `USER_CODE`,
  `a`.`USER_NAME`    AS `USER_NAME`,
  `a`.`GU_TYPE`      AS `GU_TYPE`,
  `a`.`S_CMPY`       AS `S_CMPY`,
  `a`.`S_ODEPT`      AS `S_ODEPT`,
  `a`.`S_TDEPT`      AS `S_TDEPT`,
  `a`.`S_DEPT`       AS `S_DEPT`,
  `a`.`S_ATIME`      AS `S_ATIME`,
  `a`.`S_MTIME`      AS `S_MTIME`,
  `a`.`S_USER`       AS `S_USER`,
  `a`.`S_FLAG`       AS `S_FLAG`,
  `a`.`DUTY_LV_CODE` AS `DUTY_LV_CODE`,
  (SELECT
     `c`.`ODEPT_CODE`
   FROM `sy_org_dept` `c`
   WHERE (`c`.`DEPT_CODE` = (SELECT
                               `b`.`DEPT_CODE`
                             FROM `sy_org_user` `b`
                             WHERE (`b`.`USER_CODE` = `a`.`USER_CODE`)))) AS `ODEPT_CODE`
FROM `ts_pvlg_group_user` `a`;

ALTER TABLE TS_PVLG_GROUP_ROLE ADD COLUMN ROLE_TYPE  DECIMAL(1,0) COMMENT '关联类型 1本机构 2自定义';
ALTER TABLE TS_PVLG_GROUP_ROLE ADD COLUMN ROLE_DCODE VARCHAR(40) COMMENT '关联部门编码';
ALTER TABLE TS_PVLG_GROUP_ROLE ADD COLUMN ROLE_DNAME VARCHAR(100) COMMENT '关联部门名称' ;
ALTER TABLE TS_PVLG_GROUP_ROLE ADD COLUMN ROLE_ORG_LV  DECIMAL(1,0) COMMENT '关联本机构 级别 一二三四五级';

UPDATE ts_pvlg_group_role a,ts_pvlg_role b SET a.role_type = b.role_type,a.role_dcode= b.role_dcode,a.role_dname = b.role_dname, a.role_org_lv = b.role_org_lv WHERE b.role_id = a.role_code;

UPDATE 
  sy_org_user u,
  hrm_zdstaffposition p 
SET
  u.duty_level_code = p.duty_level_code,
  u.duty_level = p.duty_level,
  u.duty_lv_code = p.duty_lv_code,
  u.duty_lv = p.duty_lv,
  u.station_no_code = p.station_no_code,
  u.station_no = p.station_no,
  u.station_type_code = p.station_type_code,
  u.station_type = p.station_type 
WHERE u.`USER_CODE` = p.`PERSON_ID`;