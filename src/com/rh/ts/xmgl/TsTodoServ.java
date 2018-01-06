package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.bean.PageBean;
import com.rh.core.util.Constant;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by shenh on 2017/12/9.
 */
public class TsTodoServ extends CommonServ {

    /**
     * 获取待办列表
     *
     * @param paramBean TYPE
     * @return outBean
     */
    public OutBean getToDoList(ParamBean paramBean) {
        /*分页参数处理*/
        PageBean page = paramBean.getQueryPage();
        int rowCount = paramBean.getShowNum(); //通用分页参数优先级最高，然后是查询的分页参数
        if (rowCount > 0) { //快捷参数指定的分页信息，与finds方法兼容
            page.setShowNum(rowCount); //从参数中获取需要取多少条记录，如果没有则取所有记录
            page.setNowPage(paramBean.getNowPage());  //从参数中获取第几页，缺省为第1页
        } else {
            if (!page.contains(Constant.PAGE_SHOWNUM)) { //初始化每页记录数设定
                if (paramBean.getQueryNoPageFlag()) { //设定了不分页参数
                    page.setShowNum(0);
                } else { //没有设定不分页，取服务设定的每页记录数
                    page.setShowNum(50);
                }
            }
        }

        String type = paramBean.getStr("TYPE");
        String xmId = paramBean.getStr("XM_ID");
        String whereSql = "";

        if (StringUtils.isNotBlank(type)) {
            whereSql += "and a.TYPE = '" + type + "'";
        }
        if (StringUtils.isNotBlank(xmId)) {
            whereSql += " AND (c.XM_ID = '" + xmId + "' or b.XM_ID = '" + xmId + "')";
        }
        String currentUserCode = Context.getUserBean().getCode();

        String sql = "select a.* from ts_comm_todo a " +
                " left join ts_qjlb_qj b on b.QJ_ID=a.DATA_ID " +
                " left join ts_jklb_jk c on c.JK_ID=a.DATA_ID " +
                " where a.OWNER_CODE ='" + currentUserCode + "' " +
                whereSql + " order by a.SEND_TIME desc";

        List<Bean> dataList = Transaction.getExecutor().queryPage(
                sql, page.getNowPage(), page.getShowNum(), null, null);

        String countSql = "select count(*) as count " + sql.substring(sql.indexOf("from ts_comm_todo a"));
        /*设置数据总数*/
        int count = dataList.size();
        int showCount = page.getShowNum();
        boolean bCount; //是否计算分页
        if ((showCount == 0) || paramBean.getQueryNoPageFlag()) {
            bCount = false;
        } else {
            bCount = true;
        }
        OutBean outBean = new OutBean();
        if (bCount) { //进行分页处理
            if (!page.contains(Constant.PAGE_ALLNUM)) { //如果有总记录数就不再计算
                int allNum;
                if ((page.getNowPage() == 1) && (count < showCount)) { //数据量少，无需计算分页
                    allNum = count;
                } else {
                    allNum = Transaction.getExecutor().queryOne(countSql).getInt("COUNT");
                }
                page.setAllNum(allNum);
            }
            outBean.setCount(page.getAllNum()); //设置为总记录数
        } else {
            outBean.setCount(dataList.size());
        }
        outBean.setData(dataList);
        outBean.setPage(page);
        return outBean;
    }

    /**
     * 获取待办对应的项目列表
     *
     * @param paramBean TYPE
     * @return outBean
     */
    public OutBean getTodoXMList(ParamBean paramBean) {
        String type = paramBean.getStr("TYPE");
        String whereSql = " ";
        if (StringUtils.isNotBlank(type)) {
            whereSql += " and a.TYPE = '" + type + "'";
        }

        String sql = "select xm.XM_ID,xm.XM_NAME from ts_xmgl xm where exists(" +
                " select '' from ts_comm_todo a" +
                " left join ts_qjlb_qj b on b.QJ_ID=a.DATA_ID " +
                " left join ts_jklb_jk c on c.JK_ID=a.DATA_ID " +
                " where a.OWNER_CODE='" + Context.getUserBean().getCode() + "' AND (c.XM_ID = xm.XM_ID or b.XM_ID = xm.XM_ID)" + whereSql +
                " )";

        List<Bean> xmList = Transaction.getExecutor().query(sql);
        OutBean outBean = new OutBean();
        outBean.setData(xmList);
        return outBean;
    }

}
