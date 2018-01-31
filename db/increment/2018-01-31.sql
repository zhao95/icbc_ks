ALTER TABLE ts_xmgl_kcap_dapcc DROP INDEX IDX_XM_KC_CTLG;
ALTER TABLE TS_BMSH_RULE ADD COLUMN RULE_DESC VARCHAR(40)NOT NULL;
alter table TS_BMSH_RULE_POST add column POST_ZD VARCHAR(40)NOT NULL;
alter table TS_BMSH_RULE_POST add column POST_DESC VARCHAR(40)NOT NULL;
alter table ts_bmsh_rule_kxlgz add column gz_tx_year varchar(40)not null;
