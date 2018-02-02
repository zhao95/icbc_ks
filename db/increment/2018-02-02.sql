create or replace view TS_KCGL_ZWDYB_V as
select t.*,substring_index(t.ZW_ZWH_XT,"-",1) as sort1,substring_index(t.ZW_ZWH_XT,"-",-1) as sort2 from TS_KCGL_ZWDYB t;


create or replace view TS_KCGL_V as
select `t`.`KC_ID` AS `kc_id`,(select group_concat(`k`.`ZW_IP` separator ',') from `ts_kcgl_zwdyb` `k` where (`k`.`KC_ID` = `t`.`KC_ID`)) AS `kc_ips`,`t`.`KC_TYPE` AS `KC_TYPE`,`t`.`KC_CODE` AS `kc_code`,`t`.`SERV_ID` AS `serv_id`,`t`.`GROUP_ID` AS `GROUP_ID`,`t`.`COPY_ID` AS `copy_id`,`t`.`KC_NAME` AS `kc_name`,`t`.`KC_ADDRESS` AS `kc_address`,`t`.`KC_ODEPTNAME` AS `kc_odeptname`,`t`.`KC_ODEPTCODE` AS `kc_odeptcode`,`t`.`KC_LEVEL` AS `kc_level`,`t`.`KC_LXDH` AS `kc_lxdh`,`t`.`KC_CREATOR` AS `kc_creator`,`t`.`KC_MAX` AS `kc_max`,`t`.`KC_GOOD` AS `kc_good`,`t`.`KC_USE_NUM` AS `kc_use_num`,`t`.`KC_IP1` AS `kc_ip1`,`t`.`KC_IP2` AS `kc_ip2`,`t`.`KC_SCORE` AS `kc_score`,`t`.`KC_STATE` AS `kc_state`,(case when (`t`.`KC_STATE` = 3) then 0 when ((select count(`u`.`UPDATE_ID`) from `ts_kcgl_update` `u` where ((`u`.`KC_ID` = `t`.`KC_ID`) and (`u`.`KC_COMMIT` = 1) and (`u`.`UPDATE_AGREE` = 0))) > 0) then 1 else 2 end) AS `ORDERCOL`,`t`.`CTLG_PCODE` AS `CTLG_PCODE`,`t`.`S_USER` AS `s_user`,`t`.`S_TDEPT` AS `s_tdept`,`t`.`S_ODEPT` AS `s_odept`,`t`.`S_MTIME` AS `s_mtime`,`t`.`S_FLAG` AS `s_flag`,`t`.`S_DEPT` AS `s_dept`,`t`.`S_CMPY` AS `s_cmpy`,`t`.`S_ATIME` AS `s_atime`,`t`.`KC_STATE2` AS `KC_STATE2`,(select `a`.`DEPT_LEVEL` from `sy_org_dept` `a` where (`a`.`DEPT_CODE` = `t`.`S_ODEPT`)) AS `odept_level`,(select `a`.`CODE_PATH` from `sy_org_dept` `a` where (`a`.`DEPT_CODE` = `t`.`S_ODEPT`)) AS `odept_path`,group_concat(`g`.`GLY_NAME` separator ',') AS `gly_name` from (`ts_kcgl` `t` left join `ts_kcgl_gly` `g` on((`t`.`KC_ID` = `g`.`KC_ID`))) group by `t`.`KC_ID`;