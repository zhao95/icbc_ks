-- 修改视图ts_wfs_bmshlc_v
ALTER 
ALGORITHM=UNDEFINED 
DEFINER=`root`@`%` 
SQL SECURITY DEFINER 
VIEW `ts_wfs_bmshlc_v` AS 
SELECT
	`t`.`BMSHLC_ID` AS `BMSHLC_ID`,
	`t`.`NODE_ID` AS `NODE_ID`,
	`t`.`WFS_ID` AS `WFS_ID`,
	`t`.`BMSHLC_SHR` AS `BMSHLC_SHR`,
	`t`.`BMSHLC_DEPT` AS `BMSHLC_DEPT`,
	`t`.`S_USER` AS `S_USER`,
	`t`.`S_TDEPT` AS `S_TDEPT`,
	`t`.`S_ODEPT` AS `S_ODEPT`,
	`t`.`S_MTIME` AS `S_MTIME`,
	`t`.`S_FLAG` AS `S_FLAG`,
	`t`.`S_DEPT` AS `S_DEPT`,
	`t`.`S_CMPY` AS `S_CMPY`,
	`t`.`S_ATIME` AS `S_ATIME`,
	`t`.`SHR_USERCODE` AS `SHR_USERCODE`,
	`t`.`DEPT_CODE` AS `DEPT_CODE`,
	`t`.`BMSHLC_YESNO` AS `BMSHLC_YESNO`,
	(
		SELECT
			`a`.`NODE_NAME`
		FROM
			`ts_wfs_node_apply` `a`
		WHERE
			(
				`a`.`NODE_ID` = `t`.`NODE_ID`
			)
	) AS `node_name`,
	(
		SELECT
			`b`.`NODE_STEPS`
		FROM
			`ts_wfs_node_apply` `b`
		WHERE
			(
				`b`.`NODE_ID` = `t`.`NODE_ID`
			)
	) AS `node_steps`
FROM
	`ts_wfs_bmshlc` `t` ;

	
	
ALTER TABLE `TS_WFS_BMSHLC` ADD COLUMN `BMSHLC_YESNO` decimal(4,0) NULL;

create or replace view ts_kcgl_v as
select `t`.`KC_ID` AS `kc_id`,`t`.`KC_TYPE` AS `KC_TYPE`,`t`.`KC_XM_ID` AS `KC_XM_ID`,
`t`.`KC_XM_NAME` AS `KC_XM_NAME`,`t`.`KC_CODE` AS `kc_code`,`t`.`SERV_ID` AS `serv_id`,
`t`.`GROUP_ID` AS `GROUP_ID`,`t`.`COPY_ID` AS `copy_id`,`t`.`KC_NAME` AS `kc_name`,
`t`.`KC_ADDRESS` AS `kc_address`,`t`.`KC_ODEPTNAME` AS `kc_odeptname`,
`t`.`KC_ODEPTCODE` AS `kc_odeptcode`,`t`.`KC_LEVEL` AS `kc_level`,
`t`.`KC_LXDH` AS `kc_lxdh`,`t`.`KC_CREATOR` AS `kc_creator`,`t`.`KC_MAX` AS `kc_max`,
`t`.`KC_GOOD` AS `kc_good`,`t`.`KC_USE_NUM` AS `kc_use_num`,`t`.`KC_IP1` AS `kc_ip1`,
`t`.`KC_IP2` AS `kc_ip2`,`t`.`KC_SCORE` AS `kc_score`,
`t`.`KC_STATE` AS `kc_state`,
(CASE WHEN t.KC_STATE = 3 THEN 0 
	  when (select count(u.UPDATE_ID) from TS_KCGL_UPDATE u where  u.kc_id=t.KC_ID and u.kc_commit = 1 and u.UPDATE_AGREE = 0) > 0 then 1
            ELSE 2 END  ) as ORDERCOL,
`t`.`CTLG_PCODE` AS `CTLG_PCODE`,`t`.`S_USER` AS `s_user`,`t`.`S_TDEPT` AS `s_tdept`,
`t`.`S_ODEPT` AS `s_odept`,`t`.`S_MTIME` AS `s_mtime`,`t`.`S_FLAG` AS `s_flag`,
`t`.`S_DEPT` AS `s_dept`,`t`.`S_CMPY` AS `s_cmpy`,`t`.`S_ATIME` AS `s_atime`,
`t`.`KC_STATE2` AS `KC_STATE2`,(select `a`.`DEPT_LEVEL` from `sy_org_dept` `a` 
where (`a`.`DEPT_CODE` = `t`.`S_ODEPT`)) AS `odept_level`,(select `a`.`CODE_PATH` 
from `sy_org_dept` `a` where (`a`.`DEPT_CODE` = `t`.`S_ODEPT`)) AS `odept_path`,
group_concat(`g`.`GLY_NAME` separator ',') AS `gly_name` from (`ts_kcgl` `t` left join
 `ts_kcgl_gly` `g` on((`t`.`KC_ID` = `g`.`KC_ID`))) group by `t`.`KC_ID` 
 
 
 
create view TS_XMGL_BM_KSLB_cccs_v as
select a.*,b.KSLBK_PID from TS_XMGL_BM_KSLB a left join TS_XMGL_BM_KSLBK b on a.KSLBK_ID = b.KSLBK_ID
 