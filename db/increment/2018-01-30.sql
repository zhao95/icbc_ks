alter table TS_BMSH_RULE add column rule_cengji varchar(40) not null;
alter table TS_BMSH_RULE add column rule_nowtime varchar(40) not null;
alter table TS_BMSH_RULE add column rule_lastyear varchar(40) not null;
ALTER TABLE TS_BMLB_BM ADD COLUMN BM_IDCARD VARCHAR(400)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_ONE_END VARCHAR(40)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_TWO_END VARCHAR(40)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_TIME VARCHAR(40)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_YEAR VARCHAR(40)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_ONE_TIME VARCHAR(40)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_ONE_TYPE VARCHAR(40)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_TWO_TIME VARCHAR(40)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_TWO_YEAR VARCHAR(40)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_TWO_TYPE VARCHAR(40)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_TWO_END_YEAR VARCHAR(40)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_TWO_END_TIME VARCHAR(40)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_TWO_END_TYPE VARCHAR(40)NOT NULL;
ALTER TABLE TS_BMSH_RULE ADD COLUMN CONDITION_TWO_TIMETW VARCHAR(40)NOT NULL;

-- 计划添加字段，匹配项目管理的类型
ALTER TABLE `ts_jhgl` ADD COLUMN `JH_TYPE_NAME`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '计划类型名称，中文' AFTER `JH_LEVEL`;

-- 项目管理添加字段  关联计划ID
ALTER TABLE `ts_xmgl`
ADD COLUMN `JH_ID`  varchar(200) CHARACTER SET UTF8 COLLATE utf8_general_ci NULL COMMENT '关联计划ID' AFTER `XM_LBNUM`;

-- 更新计划新增字段默认值
UPDATE ts_jhgl SET jh_type_name ='其他类考试' WHERE JH_FLAG=2 AND JH_TYPE3 =2;
UPDATE ts_jhgl SET jh_type_name ='资格类考试' WHERE JH_FLAG=2 AND JH_TYPE3 =1;

-- 更新计划包新增字段默认值
UPDATE ts_jhgl SET jh_type_name ='资格类考试' WHERE JH_FLAG=1 AND JH_TYPE =1;
UPDATE ts_jhgl SET jh_type_name ='其他类考试' WHERE JH_FLAG=1 AND JH_TYPE =2;

-- 准入测试、禁考管理添加权限控制
alter table  TS_BMSH_ADMIT add column CTLG_PCODE varchar(40) not null;
alter table TS_JKGL add column CTLG_PCODE varchar(40) not null;