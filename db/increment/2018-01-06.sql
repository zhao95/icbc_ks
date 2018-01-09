-- 考位安排自动分配规则 相同考试前后左右不相邻(R001) 不强制要求勾选
UPDATE `ts_xmgl_kcap_gzk` SET `GZ_TYPE`='2' WHERE (`GZ_CODE`='R001');
UPDATE `ts_xmgl_kcap_gz` SET `GZ_TYPE`='2' WHERE (`GZ_CODE`='R001');
