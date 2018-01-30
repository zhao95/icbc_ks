alter table TS_BMSH_RULE add column rule_cengji varchar(40) not null;
alter table TS_BMSH_RULE add column rule_nowtime varchar(40) not null;
alter table TS_BMSH_RULE add column rule_lastyear varchar(40) not null;

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

