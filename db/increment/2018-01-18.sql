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