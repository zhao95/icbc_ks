package com.rh.ts.qjlb;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenh on 2018/1/15.
 */
public class QjUtils {

    protected static Log log = LogFactory.getLog(QjUtils.class);

    /**
     * 保存流程的审批节点
     *
     * @param dataId
     */
    public static void saveFlowHistoryNode(String dataId) {
//        Bean qjBean = ServDao.find(TsConstant.SERV_QJ, dataId);

        try {
            //记录流程节点信息
            SqlBean sqlBean = new SqlBean();
            sqlBean.and("DATA_ID", dataId);
            List<Bean> tsCommTodoDoneList = ServDao.finds("TS_COMM_TODO_DONE", sqlBean);

            String wfsId = null;
            if (CollectionUtils.isNotEmpty(tsCommTodoDoneList)) {
                wfsId = tsCommTodoDoneList.get(0).getStr("WFS_ID");
            }
            if (StringUtils.isNotBlank(wfsId)) {
                //String getStep = todoBean.getStr("NODE_STEPS");
                List<Bean> nodeApplyList = ServDao.finds("TS_WFS_NODE_APPLY", "AND WFS_ID='" + wfsId + "'");// and NODE_STEPS = " + getStep
                if (CollectionUtils.isNotEmpty(nodeApplyList)) {
                    List<Bean> saveNodeHistoryBeanList = new ArrayList<Bean>();
                    for (Bean nodeApply : nodeApplyList) {
                        Bean nodeHistoryBean = new Bean();
                        nodeHistoryBean.set("DATA_ID", dataId);
                        nodeHistoryBean.set("NODE_NAME", nodeApply.getStr("NODE_NAME"));
                        nodeHistoryBean.set("NODE_NUM", nodeApply.getStr("NODE_NUM"));
                        nodeHistoryBean.set("WFS_ID", nodeApply.getStr("WFS_ID"));
                        nodeHistoryBean.set("NODE_STEPS", nodeApply.getStr("NODE_STEPS"));
                        saveNodeHistoryBeanList.add(nodeHistoryBean);
                    }
                    ServDao.creates(TsConstant.SERV_WFS_NODE_HISTORY, saveNodeHistoryBeanList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("流程节点保存失败，" + "todo DATA_ID:" + dataId /*",USER_CODE:" + qjBean.getStr("USER_CODE")*/);
        }
    }

    public static String getQjStatusName(String qjStatus) {
        String result = "";
        if ("1".equals(qjStatus)) {
            result = "审批中";
        } else if ("2".equals(qjStatus)) {
            result = "已通过";
        } else if ("3".equals(qjStatus)) {
            result = "未通过";
        }
        return result;
    }
}
