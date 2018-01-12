-- 日志相关视图更改
-- sy_serv_log_item_v 视图添加sy_org_user的user_name    作为视图的USER_NAME
DROP VIEW IF EXISTS `sy_serv_log_item_v`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `sy_serv_log_item_v` AS 
SELECT
  `a`.`ILOG_ID`         AS `ILOG_ID`,
  `a`.`ITEM_ID`         AS `ITEM_ID`,
  `a`.`ITEM_CODE`       AS `ITEM_CODE`,
  `a`.`DATA_ID`         AS `DATA_ID`,
  `a`.`ILOG_OLD`        AS `ILOG_OLD`,
  `a`.`ILOG_NEW`        AS `ILOG_NEW`,
  `a`.`S_CMPY`          AS `S_CMPY`,
  `a`.`S_USER`          AS `S_USER`,
  `u`.`USER_NAME`       AS `USER_NAME`,
  `a`.`S_MTIME`         AS `S_MTIME`,
  `a`.`ILOG_IP`         AS `ILOG_IP`,
  `a`.`SERV_ID`         AS `serv_id`,
  `b`.`ITEM_NAME`       AS `item_name`,
  `b`.`ITEM_INPUT_TYPE` AS `item_input_type`,
  `b`.`ITEM_INPUT_MODE` AS `item_input_mode`,
  `b`.`DICT_ID`         AS `dict_id`
FROM ((`sy_serv_log_item` `a`
    LEFT JOIN `sy_serv_item` `b`
      ON ((`a`.`ITEM_ID` = `b`.`ITEM_ID`)))
   LEFT JOIN `sy_org_user` `u`
     ON ((`a`.`S_USER` = `u`.`USER_CODE`)));

