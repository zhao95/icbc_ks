UPDATE `icbc_ks2`.`ts_xmgl_bmsh_shgzk_mx` SET mx_value2 = "[{vari:'grade',val:'3',type:'grade',code:'1'}]" WHERE mx_id = 'N0101';
INSERT INTO `icbc_ks2`.`ts_xmgl_bmsh_shgzk_mx` (`GZ_NAME`, `MX_ID`, `GZ_ID`, `MX_NAME`, `MX_VALUE1`, `MX_VALUE2`, `S_USER`, `S_TDEPT`, `S_ODEPT`, `S_MTIME`, `S_FLAG`, `S_DEPT`, `S_CMPY`, `S_ATIME`, `MX_IMPL`, `MX_SORT`) VALUES ('证书规则', 'Y01110', 'Y01', '信贷类任职满#XinDai#年可报名', '1', '[{\'vari\':\'XinDai\',\'val\':\'2\',\'type\':\'int\'}]', '0000000017', '0010100546', '0010100000', '2017-10-24 10:13:32:481', '1', '0010100546', 'icbc', '2017-10-12 17:38:36:720', 'com.rh.ts.xmgl.rule.impl.XinDaiLimit', '0');

-- 更新公告数据
update ts_gg set CTLG_PCODE='0010100000' where CTLG_PCODE is null or CTLG_PCODE='';