ALTER TABLE ts_xmgl_kcap_dapcc DROP INDEX IDX_XM_KC_CTLG;
ALTER TABLE TS_BMSH_RULE ADD COLUMN RULE_DESC VARCHAR(40)NOT NULL;
alter table TS_BMSH_RULE_POST add column POST_ZD VARCHAR(40)NOT NULL;
alter table TS_BMSH_RULE_POST add column POST_DESC VARCHAR(40)NOT NULL;
alter table ts_bmsh_rule_kxlgz add column gz_tx_year varchar(40)not null;
INSERT INTO `sy_comm_config` (`CONF_ID`, `CONF_NAME`, `CONF_KEY`, `CONF_VALUE`, `CONF_FLAG`, `CONF_ORDER`, `CONF_MEMO`, `S_FLAG`, `S_CMPY`, `S_PUBLIC`, `S_MTIME`) VALUES ('23NQBEDhl1E8tfeEWUZ5', '女性退休年纪', 'TS_WOMAN_TUIXIU_AGE', '60', '1', '0', '', '1', 'icbc', '1', '2018-01-31 16:14:26:566');
INSERT INTO `sy_comm_config` (`CONF_ID`, `CONF_NAME`, `CONF_KEY`, `CONF_VALUE`, `CONF_FLAG`, `CONF_ORDER`, `CONF_MEMO`, `S_FLAG`, `S_CMPY`, `S_PUBLIC`, `S_MTIME`) VALUES ('2ArWp7ABR5dLb9GsFkEZ', '男性退休年纪', 'TS_MAN_TUIXIU_AGE', '60', '1', '0', '', '1', 'icbc', '1', '2018-01-31 16:13:39:732');


