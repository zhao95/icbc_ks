UPDATE `ts_xmgl_bm_kslbk` SET `KSLBK_ALIKE_DIFF`=NULL, `KSLBK_TYPE_LEVEL`='2', `KSLBK_ID`='0YuWKLvZJaZrTenZ4N4t', `KSLBK_NAME`='销售类', `KSLBK_XL`='个人客户经理', `KSLBK_MK`='无模块', `KSLBK_TYPE`='1', `S_USER`='278p246BZ2jNaftzl7kooot', `S_TDEPT`='icbc0001', `S_ODEPT`='icbc0001', `S_MTIME`='2017-08-25 18:48:43:152', `S_FLAG`='1', `S_DEPT`='icbc0001', `S_CMPY`='icbc', `S_ATIME`='2017-08-25 18:48:43:152', `KSLBK_PID`='3tySLUbYp98kW5VBJKt3', `KSLBK_LEVEL`='4', `CODE_PATH`='00KpjXBn59JbxOu0Sdzm^3cvxfgcbR9AWG9WH37ij^3tySLUbYp98kW5VBJKt3^0YuWKLvZJaZrTenZ4N4t^', `KSLBK_IDVALUE`='初级', `KSLBK_MKCODE`='-1', `KSLBK_TIME`='60', `KSLBK_CODE`='023003', `KSLBK_XL_CODE`='A000000000000000021', `KSLBK_TYPE_NAME`='初级' WHERE (`KSLBK_ID`='0YuWKLvZJaZrTenZ4N4t');
UPDATE `ts_xmgl_bmsh_shgzk_mx` SET `GZ_NAME`='证书规则', `MX_ID`='Y01100', `GZ_ID`='Y01', `MX_NAME`='报考投行初级时是否已获#muty#、#muty##muty##muty#有效证书', `MX_VALUE1`='1', `MX_VALUE2`='[{vari:\'muty\',val:\'研究分析\',type:\'muty\',code:\'A000000000000000015\'},{vari:\'muty\',val:\'交易\',type:\'muty\',code:\'A000000000000000009\'},{vari:\'muty\',val:\'>=\',type:\'muty\',code:\'3\'},{vari:\'muty\',val:\'初级\',type:\'muty\',code:\'1\'}]', `S_USER`='admin', `S_TDEPT`=NULL, `S_ODEPT`='0010100000', `S_MTIME`='2017-12-11 20:10:30:866', `S_FLAG`='2', `S_DEPT`='0010100000', `S_CMPY`='icbc', `S_ATIME`='2017-09-18 11:28:04:558', `MX_IMPL`='com.rh.ts.xmgl.rule.impl.BaseCert2YearDgYxXd', `MX_SORT`='5' WHERE (`MX_ID`='Y01100');
UPDATE `ts_xmgl_bmsh_shgzk_mx` SET `GZ_NAME`='跨序列规则', `MX_ID`='Y0304', `GZ_ID`='Y03', `MX_NAME`='跨序列报考当前考试时是否已经获得#level##level#有效证书', `MX_VALUE1`='1', `MX_VALUE2`='[{\'vari\':\'level\',\'val\':\'>\',\'type\':\'level\',\'code\':\'1\'},{\'vari\':\'level\',\'val\':\'中级\',\'type\':\'level\',\'code\':\'2\'}]', `S_USER`='278p246BZ2jNaftzl7kooot', `S_TDEPT`='icbc0001', `S_ODEPT`='icbc0001', `S_MTIME`='2018-01-06 14:44:56:059', `S_FLAG`='1', `S_DEPT`='icbc0001', `S_CMPY`='icbc', `S_ATIME`='2017-07-28 17:57:16:680', `MX_IMPL`='com.rh.ts.xmgl.rule.impl.BaseValidCert2YearBkxl', `MX_SORT`='2' WHERE (`MX_ID`='Y0304');
INSERT INTO `ts_xmgl_bmsh_shgzk` (`GZ_ID`, `GZ_TYPE`, `GZ_NAME`, `GZ_INFO`, `GZ_SORT`, `S_USER`, `S_TDEPT`, `S_ODEPT`, `S_MTIME`, `S_FLAG`, `S_DEPT`, `S_CMPY`, `S_ATIME`) VALUES ('Y03', '2', '夸序列规则', NULL, '1', '0000000017', '0010100546', '0010100000', '2017-10-17 13:23:56:132', '1', '0010100546', 'icbc', NULL);
UPDATE TS_BMSH_PX SET PX_COLUMN='RZYEAR' where PX_COLUMN='RZ_YEAR';

