package com.rh.ts.util;

import com.rh.core.serv.dict.DictMgr;

/**
 * 基本工具方法
 * Created by shenh on 2017/11/30.
 */
public class BMUtil {

    /**
     * @param bmType 类别（初中高）
     * @param bmXl   序列
     * @param bmMk   模块
     * @return 考试名称
     */
    public static String getExaminationName(String bmType, String bmXl, String bmMk) {
        String s = DictMgr.getName("TS_XMGL_BM_KSLBK_LV", bmType) + "-" + bmXl;
        if (bmMk == null || !bmMk.contains("无模块")) {
            s += "-" + bmMk;
        }
        return s;
    }
}
