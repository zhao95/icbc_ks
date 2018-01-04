UPDATE ts_xmgl_bm_kslbk set kslbk_type_name = '高级',KSLBK_IDVALUE='高级' WHERE kslbk_type_name = '高级-投行顾问（TH）';

DELETE FROM ts_xmgl_bm_kslbk WHERE kslbk_type_name='高级-投行顾问（ZT）';

UPDATE ts_xmgl_bm_kslbk SET kslbk_type = '1',kslbk_type_name = 'c类',KSLBK_IDVALUE='c类' WHERE  kslbk_id =  '155024vTq17xpi3uQBGu';

-- 更改数据库配置项表，以适配邮件提醒信息
UPDATE `sy_comm_config` SET `CONF_NAME`='借考开启提醒语', `CONF_KEY`='TS_JK_START_TIP', `CONF_VALUE`='您#XM_NAME##BM_LB#的考试，可在：#JK_STARTDATE#到#JK_ENDDATE#进行异地借考申请。具体情况详见工商银行考试系统首页，或关注融e联、微信“我爱考拉”公众号获取相关信息。【工商银行】', `CONF_FLAG`='1', `CONF_ORDER`='0', `CONF_MEMO`='请按照以下格式编写提示语，注：#XM_NAME#和#BM_LB#等为系统所用编码名称，请不要缺失或修改。\n格式为：您#XM_NAME##BM_LB#的考试，可在：#JK_STARTDATE#到#JK_ENDDATE#进行异地借考申请。', `S_FLAG`='1', `S_CMPY`='icbc', `S_PUBLIC`='1', `S_MTIME`='2017-11-10 18:34:11:906' WHERE (`CONF_ID`='01J3NJtKhaYbKiOuFBAx');
UPDATE `sy_comm_config` SET `CONF_NAME`='考场公示提示语', `CONF_KEY`='TS_KCGS_START_TIP', `CONF_VALUE`='您报名的#XM_NAME#考试开始公示考场，请登录工商银行考试系统查看详情。具体情况详见工商银行考试系统首页，或关注融e联、微信“我爱考拉”公众号获取相关信息。【工商银行】', `CONF_FLAG`='1', `CONF_ORDER`='0', `CONF_MEMO`='请按照以下格式编写提示语，注：#QJ_TITLE#和#QJ_RESULT#等为系统所用编码名称，请不要缺失或修改。\n格式为：您报名的#XM_NAME#考试开始公示考场，请登录工商银行考试系统查看详情。', `S_FLAG`='1', `S_CMPY`='icbc', `S_PUBLIC`='1', `S_MTIME`='2017-11-10 18:39:38:192' WHERE (`CONF_ID`='0e8yWXDe5fOEuVfxJt8c');
UPDATE `sy_comm_config` SET `CONF_NAME`='请假结果提醒语', `CONF_KEY`='TS_QJ_RESULT_TIP', `CONF_VALUE`='您的#QJ_TITLE#请假申请，有了审批结果，审批结果为：#QJ_RESULT#。具体情况详见工商银行考试系统首页，或关注融e联、微信“我爱考拉”公众号获取相关信息。【工商银行】', `CONF_FLAG`='1', `CONF_ORDER`='0', `CONF_MEMO`='请按照以下格式编写提示语，注：#QJ_TITLE#和#QJ_RESULT#等为系统所用编码名称，请不要缺失或修改。\n格式为：您的#QJ_TITLE#请假申请，有了审批结果，审批结果为：#QJ_RESULT#。', `S_FLAG`='1', `S_CMPY`='icbc', `S_PUBLIC`='1', `S_MTIME`='2017-11-10 18:37:50:961' WHERE (`CONF_ID`='0j7oivXtEJfgHzhTnlPb');
UPDATE `sy_comm_config` SET `CONF_NAME`='报名截止提醒信息', `CONF_KEY`='TS_BM_END_TIP', `CONF_VALUE`='#XM_TITLE#项目的报名截止时间为：#BM_END_DATE#，如未报名，请等待下次考试。具体情况详见工商银行考试系统首页，或关注融e联、微信“我爱考拉”公众号获取相关信息。【工商银行】', `CONF_FLAG`='1', `CONF_ORDER`='0', `CONF_MEMO`='请按照以下格式编写提示语，注：#XM_TITLE#和#BM_END_DATE#等为系统所用编码名称，请不要缺失或修改。\n格式为：#XM_TITLE#项目的报名截止时间为：#BM_END_DATE#，如未报名，请等待下次考试。', `S_FLAG`='1', `S_CMPY`='icbc', `S_PUBLIC`='1', `S_MTIME`='2017-11-10 18:36:22:134' WHERE (`CONF_ID`='0wY1dTvex47FEWEUO5Lq');
UPDATE `sy_comm_config` SET `CONF_NAME`='请假开启提醒语', `CONF_KEY`='TS_QJ_START_TIP', `CONF_VALUE`='您报名的#XM_NAME##BM_LB#考试，可在：#QJ_STARTDATE#至#QJ_ENDDATE#进行请假。具体情况详见工商银行考试系统首页，或关注融e联、微信“我爱考拉”公众号获取相关信息。【工商银行】', `CONF_FLAG`='1', `CONF_ORDER`='0', `CONF_MEMO`='请按照以下格式编写提示语，注：#XM_NAME#和#BM_LB#等为系统所用编码名称，请不要缺失或修改。\n格式为：您报名的#XM_NAME##BM_LB#考试，可在：#QJ_STARTDATE#至#QJ_ENDDATE#进行请假。', `S_FLAG`='1', `S_CMPY`='icbc', `S_PUBLIC`='1', `S_MTIME`='2017-11-10 18:07:27:159' WHERE (`CONF_ID`='2rF1Ji8ihfrV59cU08IY');
UPDATE `sy_comm_config` SET `CONF_NAME`='报名开始提醒语', `CONF_KEY`='TS_BM_START_TIP', `CONF_VALUE`='您能报名的项目：#XM_TITLE#，可以开始报名了，报名时间为：#BM_START_DATE#至#BM_END_DATE#，请在时间段内报名，逾期将无法报名。具体情况详见工商银行考试系统首页，或关注融e联、微信“我爱考拉”公众号获取相关信息。【工商银行】', `CONF_FLAG`='1', `CONF_ORDER`='0', `CONF_MEMO`='请按照以下格式编写提示语，注：#XM_TITLE#和#BM_START_DATE#等为系统所用编码名称，请不要缺失或修改。\n格式为： 您能报名的项目：#XM_TITLE#，可以开始报名了，报名时间为：#BM_START_DATE#至#BM_END_DATE#，请在时间段内报名，逾期将无法报名。', `S_FLAG`='1', `S_CMPY`='icbc', `S_PUBLIC`='1', `S_MTIME`='2017-11-10 18:35:19:095' WHERE (`CONF_ID`='3rVRDCvoxdz8UuxtrOTK');
UPDATE `sy_comm_config` SET `CONF_NAME`='借考结果提醒语', `CONF_KEY`='TS_JK_RESULT_TIP', `CONF_VALUE`='您的#JK_TITLE#借考申请，有了审批结果，审批结果为：#JK_RESULT#。具体情况详见工商银行考试系统首页，或关注融e联、微信“我爱考拉”公众号获取相关信息。【工商银行】', `CONF_FLAG`='1', `CONF_ORDER`='0', `CONF_MEMO`='请按照以下格式编写提示语，注：#JK_TITLE#和#JK_RESULT#等为系统所用编码名称，请不要缺失或修改。\n格式为： 您的#JK_TITLE#借考申请，有了审批结果，审批结果为：#JK_RESULT#。', `S_FLAG`='1', `S_CMPY`='icbc', `S_PUBLIC`='1', `S_MTIME`='2017-11-10 18:41:06:441' WHERE (`CONF_ID`='3vALgAM5vt3TbqNhEyRX');
UPDATE `sy_comm_config` SET `CONF_NAME`='准考证开始打印提示语', `CONF_KEY`='TS_ZKZ_START_TIP', `CONF_VALUE`='您所报名的#XM_NAME#考试，已可以打印准考证，祝考试顺利！具体情况详见工商银行考试系统首页，或关注融e联、微信“我爱考拉”公众号获取相关信息。【工商银行】', `CONF_FLAG`='1', `CONF_ORDER`='0', `CONF_MEMO`='请按照以下格式编写提示语，注：#XM_NAME#为系统所用编码名称，请不要缺失或修改。\n\n格式为：您所报名的#XM_NAME#考试，已可以打印准考证，祝考试顺利！', `S_FLAG`='1', `S_CMPY`='icbc', `S_PUBLIC`='1', `S_MTIME`='2017-11-10 18:36:58:838' WHERE (`CONF_ID`='3wwd1q3Ehc3bqgxiK5zv');
