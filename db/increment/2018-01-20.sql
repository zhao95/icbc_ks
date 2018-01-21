UPDATE `ts_bmsh_px` SET `PX_ID`='04GmtgVG2N9EbgxxMhAf', `PX_COLUMN`='YIYI_LIYOU', `PX_NAME`='异议原因', `PX_XUHAO`='100', `USER_CODE`=NULL WHERE (`PX_ID`='04GmtgVG2N9EbgxxMhAf');

alter table ts_bmsh_stay add column YIYI_LIYOU VARCHAR(400) NULL;
alter table ts_bmsh_pass add column YIYI_LIYOU VARCHAR(400) NULL;
alter table ts_bmsh_nopass add column YIYI_LIYOU VARCHAR(400) NULL;
alter table TS_BMSH_RULE add column G_ID VARCHAR(400) NULL;

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


