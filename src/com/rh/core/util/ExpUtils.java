package com.rh.core.util;

import com.rh.core.base.Bean;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ExportExcel;
import com.rh.core.serv.util.ServUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by shenh on 2018/1/3.
 */
public class ExpUtils {

    /**
     * 记录日志信息的函数
     */
    protected static Log log = LogFactory.getLog(ExpUtils.class);

    /**
     * 每次获取数据条数
     */
    private static final int ONETIME_EXP_NUM = 5000;
    /**
     * excel最大行数
     */
    private static final int EXCEL_MAX_NUM = 65536;

    public static OutBean expUtil(List<Bean> beanList, LinkedHashMap<String, String> colMap, ParamBean paramBean) {
        OutBean outBean = new OutBean();

        LinkedHashMap<String, Bean> cols = new LinkedHashMap<String, Bean>();
        for (String itemCode : colMap.keySet()) {
            Bean colBean = new Bean();
            colBean.set("SAFE_HTML", "");
            colBean.set("ITEM_LIST_FLAG", "1");
            colBean.set("ITEM_CODE", itemCode);
            colBean.set("EN_JSON", "");
            colBean.set("ITEM_NAME", colMap.get(itemCode));
            cols.put(itemCode, colBean);
        }
        String servId = paramBean.getServId();
        ServDefBean serv = ServUtils.getServDef(servId);
        ExportExcel expExcel = new ExportExcel(serv);
        try {
            // 查询出 要导出的数据
            long count = beanList.size();
            // 总数大于excel可写最大值
            if (count > EXCEL_MAX_NUM) {
                return outBean.setError("导出数据总条数大于Excel最大行数："
                        + EXCEL_MAX_NUM);
            }
            // 导出第一次查询数据
            paramBean.setQueryPageNowPage(1); // 导出当前第几页
//            afterExp(paramBean, outBean); // 执行导出查询后扩展方法
            // 查询出表头 查询出 对应数据 hashmaplist

            expExcel.createHeader(cols);
            expExcel.appendData1(beanList, paramBean);
//             //存在多页数据
//            if (ONETIME_EXP_NUM < count) {
//                times = count / ONETIME_EXP_NUM;
//                // 如果获取的是整页数据
//                if (ONETIME_EXP_NUM * times == count && count != 0) {
//                    times = times - 1;
//                }
//                for (int i = 1; i <= times; i++) {
//                    paramBean.setQueryPageNowPage(i + 1); // 导出当前第几页
//                    OutBean out = query(paramBean);
//                    afterExp(paramBean, out); // 执行导出查询后扩展方法
//                    expExcel.appendData(out.getDataList(), paramBean);
//                }
//            }
            expExcel.addSumRow();
        } catch (Exception e) {
            log.error("导出Excel文件异常" + e.getMessage(), e);
        } finally {
            expExcel.close();
        }
        return outBean.setOk();
    }


}
