package com.rh.core.comm.entity;

import com.rh.core.base.Bean;
import com.rh.core.comm.todo.TodoUtils;
import com.rh.core.org.DeptBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;

/**
 * 
 * @author yangjy 
 *
 */
public class EntityServ extends CommonServ {

    @Override
    protected void afterSave(ParamBean paramBean, OutBean outBean) {
        super.afterSave(paramBean, outBean);
        
        Bean oldBean = paramBean.getSaveOldData();
        if (oldBean != null) {
            final String oldTitle = oldBean.getStr("TITLE");
            final String newTitle = outBean.getStr("TITLE");

            if (!newTitle.equals(oldTitle)) { //如果标题有变化，则更改待办标题。
                Bean whereBean = new Bean();
                whereBean.set("TODO_OBJECT_ID1", outBean.getStr("DATA_ID"));
                Bean dataBean = new Bean();
                dataBean.set("TODO_TITLE", newTitle);
                TodoUtils.updates(dataBean, whereBean);
            }
        }
        
    }
    /**
     * 根据tdept 的 code 值 获取相应的tdept name值
     * @param paranBean 参数
     * @return 结果集
     */
    public OutBean getTdeptName(ParamBean paranBean) {
    	OutBean outBean = new OutBean();
    	outBean.set("TDEPT_CODE", "");
    	outBean.set("TDEPT_NAME", "");
        Bean entityBean = EntityMgr.getEntity(paranBean.getStr("DATA_ID"));
        if (entityBean == null) {
        	return outBean;
        }
        
        DeptBean deptBean = OrgMgr.getDept(entityBean.getStr("S_TDEPT"));
        if (deptBean != null) {
            outBean.set("TDEPT_CODE", deptBean.getId());
            outBean.set("TDEPT_NAME", deptBean.getName());
        }
        return outBean;
    }
}
