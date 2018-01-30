alter table TS_BMSH_RULE add column rule_cengji varchar(40) not null;
alter table TS_BMSH_RULE add column rule_nowtime varchar(40) not null;
alter table TS_BMSH_RULE add column rule_lastyear varchar(40) not null;

-- 计划添加字段，匹配项目管理的类型
ALTER TABLE `ts_jhgl` ADD COLUMN `JH_TYPE_NAME`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '计划类型名称，中文' AFTER `JH_LEVEL`;
