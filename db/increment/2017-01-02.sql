UPDATE ts_xmgl_bm_kslbk set kslbk_type_name = '高级',KSLBK_IDVALUE='高级' WHERE kslbk_type_name = '高级-投行顾问（TH）';

DELETE FROM ts_xmgl_bm_kslbk WHERE kslbk_type_name='高级-投行顾问（ZT）';

UPDATE ts_xmgl_bm_kslbk SET kslbk_type = '1',kslbk_type_name = 'c类',KSLBK_IDVALUE='c类' WHERE  kslbk_id =  '155024vTq17xpi3uQBGu'