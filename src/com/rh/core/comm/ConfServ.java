/*
 * Copyright (c) 2011 Ruaho All rights reserved.
 */
package com.rh.core.comm;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.EncryptUtils;

/**
 * 系统配置扩展类。用于保存完成后及时提交事务，确保字典能够取到最新的数据。
 * 
 * @author jerry li
 */
public class ConfServ extends CommonServ {
    
    @Override
	protected void beforeSave(ParamBean paramBean) {
    	// 如果为添加模式，或者CONF_VALUE字段发生改变
    	if (paramBean.getAddFlag() || !paramBean.getSaveFullData().getStr("CONF_VALUE").equals(paramBean.getSaveOldData().getStr("CONF_VALUE"))) {
    		// 根据是否加密标识，处理CONF_VALUE字段
    		if (paramBean.getSaveFullData().getInt("CONF_ENCRYPT") == 1) { // 加密CONF_VALUE字段
    			// 加密后的value
    			String encValue = EncryptUtils.desEncrypt(paramBean.getStr("CONF_VALUE"));
    			// 重置value值
    			paramBean.set("CONF_VALUE", encValue);
    		}
    	}
	}
	@Override
    protected void afterSave(ParamBean paramBean, OutBean outBean) {
        if (outBean.isOkOrWarn()) {
            Transaction.commit();
            ServDefBean servDef = ServUtils.getServDef(paramBean.getServId());
            servDef.clearDictCache(outBean.getStr("S_CMPY")); 
        }
    }
    @Override
    protected void afterDelete(ParamBean paramBean, OutBean outBean) {
        if (outBean.getCount() > 0) { //删除成功的数量大于0
            Transaction.commit();
        }
    }
    
    /**
     * 显示系统级别配置信息
     * @param paramBean 参数
     * @return 结果信息，disp到JSP页面
     */
    public OutBean showAPPConf(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        outBean.setData(Context.getAppBean());
        outBean.setToDispatcher("/sy/comm/conf/showAPPConf.jsp");
        return outBean;
    }
    
    /**
     * 在内存中修改系统级别配置信息
     * @param paramBean 参数
     * @return 结果信息
     */
    public OutBean changeAPPConf(ParamBean paramBean) {
        Bean appBean = paramBean.getBean("APP_BEAN");
        if (appBean.size() > 0) {
            Context.getAppBean().putAll(appBean);
        }
        return new OutBean().setOk();
    }
}
