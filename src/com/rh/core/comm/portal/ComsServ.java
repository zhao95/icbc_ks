package com.rh.core.comm.portal;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
/**
 * 组件服务监听类
 * @author weiju
 */
public class ComsServ extends CommonServ {
    
    /**
     * 提供基于组件的查询服务 
     * @param paramBean   
     * @return 查询结果
     */
    public OutBean queryCms(ParamBean paramBean) {
        String userCode = paramBean.getStr("USER_CODE"); // 用户编码
        String deptCode = paramBean.getStr("DEPT_CODE"); // 部门编码
        String roleCodes = paramBean.getStr("ROLE_CODES"); //角色编码
        String nowPage = paramBean.getStr("NOWPAGE"); // 得到当前页码
        Bean tempBean = new Bean();
        tempBean.set("NOWPAGE", nowPage);
        paramBean.set("_PAGE_", tempBean);
        String sql = " and ACL_OWNER in ('" + userCode + "','" + deptCode + "'," + roleCodes + ")";
        List<Bean> beans = ServDao.finds("SY_SERV_DACL_ITEM", sql); //查询出该权限下的所有组件
        StringBuilder ids = new StringBuilder("");
        if (beans.size() > 0) {
            ids.append(" (");
            for (Bean bean : beans) {
                ids.append("'").append(bean.getStr("DATA_ID")).append("',");
            }
            ids = new StringBuilder(ids.substring(0, ids.toString().length() - 1)).append(") ");
        }
        if (!ids.toString().equals("")) {
            paramBean.put("_extWhere", " and PC_ID in " + ids.toString());
        } else {
            paramBean.put("_extWhere", " and 1=2 ");
        }
        OutBean outBean = new OutBean();
        outBean = super.query(paramBean);
        return outBean;
    }
    
}
