
    
    
ALTER TABLE `TS_BMSH_ADMIT` ADD COLUMN `XM_ID` varchar(40) NULL;
alter table ts_bmsh_rule add column KSQZ_ID varchar(400);
ALTER table ts_bmsh_rule add R_ZD VARCHAR(400);

ALTER table ts_bmsh_rule add R_RZYEAR VARCHAR(400);
UPDATE `ts_xmgl_bmsh_shgzk` SET `GZ_ID`='N02', `GZ_TYPE`='1', `GZ_NAME`='跨序列报考限制', `GZ_INFO`=NULL, `GZ_SORT`='50', `S_USER`='278p246BZ2jNaftzl7kooot', `S_TDEPT`='icbc0001', `S_ODEPT`='icbc0001', `S_MTIME`='2017-09-19 19:47:47:458', `S_FLAG`='1', `S_DEPT`='icbc0001', `S_CMPY`='icbc', `S_ATIME`='2017-07-28 16:30:40:330' WHERE (`GZ_ID`='N02');
UPDATE `ts_xmgl_bmsh_shgzk` SET `GZ_ID`='Y05', `GZ_TYPE`='2', `GZ_NAME`='证书规则', `GZ_INFO`=NULL, `GZ_SORT`='60', `S_USER`='278p246BZ2jNaftzl7kooot', `S_TDEPT`='icbc0001', `S_ODEPT`='icbc0001', `S_MTIME`='2017-09-19 19:45:58:595', `S_FLAG`='1', `S_DEPT`='icbc0001', `S_CMPY`='icbc', `S_ATIME`='2017-07-28 16:35:48:378' WHERE (`GZ_ID`='Y01');
DELETE FROM ts_xmgl_bmsh_shgzk WHERE GZ_ID = 'Y03';
