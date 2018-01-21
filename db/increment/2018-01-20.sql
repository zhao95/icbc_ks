UPDATE `ts_bmsh_px` SET `PX_ID`='04GmtgVG2N9EbgxxMhAf', `PX_COLUMN`='YIYI_LIYOU', `PX_NAME`='异议原因', `PX_XUHAO`='100', `USER_CODE`=NULL WHERE (`PX_ID`='04GmtgVG2N9EbgxxMhAf');

alter table ts_bmsh_stay add column YIYI_LIYOU VARCHAR(400) NULL;
alter table ts_bmsh_pass add column YIYI_LIYOU VARCHAR(400) NULL;
alter table ts_bmsh_nopass add column YIYI_LIYOU VARCHAR(400) NULL;
alter table TS_BMSH_RULE add column G_ID VARCHAR(400) NULL;

