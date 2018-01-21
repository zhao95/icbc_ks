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


CREATE VIEW `ts_org_user_v_lv` AS (
SELECT
  `u`.`USER_CODE`                  AS `USER_CODE`,
  `u`.`USER_LOGIN_NAME`            AS `USER_LOGIN_NAME`,
  `u`.`USER_NAME`                  AS `USER_NAME`,
  `u`.`DEPT_CODE`                  AS `DEPT_CODE`,
  `u`.`USER_PASSWORD`              AS `USER_PASSWORD`,
  `u`.`USER_SORT`                  AS `USER_SORT`,
  `u`.`USER_HOME_PHONE`            AS `USER_HOME_PHONE`,
  `u`.`USER_MOBILE`                AS `USER_MOBILE`,
  `u`.`USER_QQ`                    AS `USER_QQ`,
  `u`.`USER_EMAIL`                 AS `USER_EMAIL`,
  `u`.`USER_WORK_LOC`              AS `USER_WORK_LOC`,
  `u`.`USER_POST`                  AS `USER_POST`,
  `u`.`USER_POST_LEVEL`            AS `USER_POST_LEVEL`,
  `u`.`USER_ROOM`                  AS `USER_ROOM`,
  `u`.`USER_WORK_NUM`              AS `USER_WORK_NUM`,
  `u`.`USER_IDCARD`                AS `USER_IDCARD`,
  `u`.`USER_BIRTHDAY`              AS `USER_BIRTHDAY`,
  `u`.`USER_OFFICE_PHONE`          AS `USER_OFFICE_PHONE`,
  `u`.`USER_NATION`                AS `USER_NATION`,
  `u`.`USER_HEIGHT`                AS `USER_HEIGHT`,
  `u`.`USER_SEX`                   AS `USER_SEX`,
  `u`.`USER_HOME_LAND`             AS `USER_HOME_LAND`,
  `u`.`USER_POLITICS`              AS `USER_POLITICS`,
  `u`.`USER_MARRIAGE`              AS `USER_MARRIAGE`,
  `u`.`USER_EDU_LEVLE`             AS `USER_EDU_LEVLE`,
  `u`.`USER_EDU_SCHOOL`            AS `USER_EDU_SCHOOL`,
  `u`.`USER_EDU_MAJOR`             AS `USER_EDU_MAJOR`,
  `u`.`USER_TITLE`                 AS `USER_TITLE`,
  `u`.`USER_TITLE_DATE`            AS `USER_TITLE_DATE`,
  `u`.`USER_WORK_DATE`             AS `USER_WORK_DATE`,
  `u`.`USER_CMPY_DATE`             AS `USER_CMPY_DATE`,
  `u`.`USER_STATE`                 AS `USER_STATE`,
  `u`.`CMPY_CODE`                  AS `CMPY_CODE`,
  `u`.`S_FLAG`                     AS `S_FLAG`,
  `u`.`S_USER`                     AS `S_USER`,
  `u`.`USER_LOGIN_TYPE`            AS `USER_LOGIN_TYPE`,
  `u`.`USER_EXPIRE_DATE`           AS `USER_EXPIRE_DATE`,
  `u`.`USER_PASSWORD_DATE`         AS `USER_PASSWORD_DATE`,
  `u`.`S_MTIME`                    AS `S_MTIME`,
  `u`.`USER_IMG_SRC`               AS `USER_IMG_SRC`,
  `u`.`PT_ID`                      AS `PT_ID`,
  `u`.`USER_FROM`                  AS `USER_FROM`,
  `u`.`JIANGANG_FLAG`              AS `JIANGANG_FLAG`,
  `u`.`USER_SHORT_NAME`            AS `USER_SHORT_NAME`,
  `u`.`USER_EN_NAME`               AS `USER_EN_NAME`,
  `u`.`USER_SHORT_ENNAME`          AS `USER_SHORT_ENNAME`,
  `u`.`USER_ENNAME`                AS `USER_ENNAME`,
  `u`.`WHITELIST`                  AS `WHITELIST`,
  `u`.`USER_TEMP_PASSWORD`         AS `USER_TEMP_PASSWORD`,
  `u`.`USER_TEMP_PASSWORD_MADTIME` AS `USER_TEMP_PASSWORD_MADTIME`,
  `u`.`DUTY_LEVEL_CODE`            AS `DUTY_LEVEL_CODE`,
  `u`.`DUTY_LEVEL`                 AS `DUTY_LEVEL`,
  `u`.`DUTY_LV_CODE`               AS `DUTY_LV_CODE`,
  `u`.`DUTY_LV`                    AS `DUTY_LV`,
  `u`.`STATION_NO_CODE`            AS `STATION_NO_CODE`,
  `u`.`STATION_NO`                 AS `STATION_NO`,
  `u`.`STATION_TYPE_CODE`          AS `STATION_TYPE_CODE`,
  `u`.`STATION_TYPE`               AS `STATION_TYPE`,
  `u`.`RESERVE2`                   AS `RESERVE2`,
  `u`.`RESERVE3`                   AS `RESERVE3`,
  `d`.`ODEPT_CODE`                 AS `ODEPT_CODE`,
  `d`.`CODE_PATH`                  AS `CODE_PATH`,
  ''                               AS `ODEPT_CODE_LV1`,
  ''                               AS `ODEPT_NAME_LV1`,
  ''                               AS `ODEPT_CODE_LV2`,
  ''                               AS `ODEPT_NAME_LV2`,
  ''                               AS `ODEPT_CODE_LV3`,
  ''                               AS `ODEPT_NAME_LV3`
FROM (`sy_org_user` `u`
   JOIN `sy_org_dept` `d`)
WHERE (`u`.`DEPT_CODE` = `d`.`DEPT_CODE`));

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