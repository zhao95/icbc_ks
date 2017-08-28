package com.rh.core.comm.todo;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.entity.EntityMgr;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;

public class TodoAuthedServ extends CommonServ {
    /**
     * 查询前添加查询条件
     * 
     * @param paramBean 
     */
    protected void beforeQuery(ParamBean paramBean) { 
        StringBuilder strWhere = new StringBuilder();

        String currUserCode = Context.getUserBean().getCode();
        //指定本人
        strWhere.append(" and AGT_USER_CODE = '" + currUserCode + "'");
        strWhere.append(" and TODO_CATALOG <= 1");
        
        String extWhere = paramBean.getStr("_extWhere");
        if (extWhere.startsWith("{")) {
//            strWhere.append(getTodoCodeWhere(paramBean)); // 指定待办类型
            extWhere = strWhere.toString();
        } else {
            extWhere = extWhere + strWhere.toString();
        }    
        paramBean.set("_extWhere", extWhere);
    }
    
    /**
     * 查询后添加查询条件
     * 
     * @param paramBean 
     * @param outBean 查询结果
     */
    protected void afterQuery(ParamBean paramBean, OutBean outBean) {
        List<Bean> dataList = outBean.getList(Constant.RTN_DATA);
        for (Bean data : dataList) {
        	//原来的处理
            String sendUser = data.getStr("SEND_USER_CODE");
            if(StringUtils.isNotBlank(sendUser)) {
				try {
					UserBean sendUserBean = UserMgr.getUser(sendUser);
					data.set("SEND_USER_NAME", sendUserBean.getName());
				} catch (Exception e) {
					log.error("无效的用户ID：" + sendUser);
				}
            }
			Bean eb = EntityMgr.getEntity(data.getStr("TODO_OBJECT_ID1"));
			if (eb != null) {
				data.set("S_WF_USER_STATE", eb.getStr("S_WF_USER_STATE"));
			}
        }
    }
}
