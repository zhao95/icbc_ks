package com.rh.core.wfe.ext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Lang;

/**
 * 流程节点服务扩展，暂时支持修改节点名称
 * @author yjzhou
 * modify time 2017.02.21
 */
public class WfeNodeExtendServ extends CommonServ {
	
	private static final Log LOG = LogFactory.getLog(WfeNodeExtendServ.class);
	
	private static final String SY_WFE_TRACK = "SY_WFE_TRACK";
	
	private static final String SY_COMM_TODO = "SY_COMM_TODO";
	
	/**
	 * 查询节点扩展信息
	 * @param paramBean 包含PROC_CODE,NODE_CODES,NODE_CHECK_CLASS（校验的完全类名）
	 * @return
	 */
    @SuppressWarnings("unchecked")
	public static OutBean queryNodeExtInfo (ParamBean paramBean) {
    	OutBean outBean = new OutBean();
		try {
			//如果有自身的校验方法，则调用相应的校验方法
			if (StringUtils.isNotEmpty(paramBean.getStr("NODE_CHECK_CLASS"))) {
				Class<?> clazz = Lang.loadClass(paramBean.getStr("NODE_CHECK_CLASS"));
				Method method = clazz.getMethod("filterNode", ParamBean.class);
				Object object = method.invoke(clazz.newInstance(), paramBean);
				List<Bean> results = (List<Bean>)object;
				removeNotMatchNodes(results);
				outBean.setData(results).setOk();
			}
		} catch (Exception e) {
			LOG.error("invoke filterNode failed");
			LOG.error(e.getMessage(), e);
			outBean.setData(new ArrayList<Bean>()).setOk();
		}
		
    	return outBean;
    }
    
    /**
     * 去除list中不含有NODE_CODE和NODE_NAME的无效数据bean
     * @param list 待整理的list
     */
    private static void removeNotMatchNodes (List<Bean> list){
    	if (null == list){
    		return;
    	}
    	
    	Iterator<Bean> iterator = list.iterator();
    	while (iterator.hasNext()) {
    		Bean tempBean = iterator.next();
    		if (StringUtils.isEmpty(tempBean.getStr("NODE_CODE")) 
    				|| StringUtils.isEmpty(tempBean.getStr("NODE_NAME"))) {
				iterator.remove();
			}
		}
    }
    
	/**
	 * 更新跟踪信息列表的节点名称
	 * @param paramBean
	 * @return
	 */
	public OutBean updateTrackNode(ParamBean paramBean){
		OutBean outBean = new OutBean();
		String procCode = paramBean.getStr("PROC_CODE");
		String nodeCode = paramBean.getStr("NODE_CODE");
		String niId = paramBean.getStr("NI_ID");
		//如果有自身的校验方法，则调用相应的校验方法
		if (StringUtils.isNotEmpty(paramBean.getStr("NODE_CHECK_CLASS"))) {
			try {
				Class<?> clazz = Lang.loadClass(paramBean.getStr("NODE_CHECK_CLASS"));
				Method method = clazz.getMethod("getInfoByProcAndNode",String.class,String.class);
				Object object = method.invoke(clazz.newInstance(), procCode,nodeCode);
				Bean nodeInfo = (Bean)object;
				if (null != nodeInfo && StringUtils.isNotEmpty(nodeInfo.getStr("NODE_NAME"))) {
					SqlBean sqlBean = new SqlBean();
					sqlBean.and("NI_ID", niId);
					sqlBean.set("NODE_NAME", nodeInfo.getStr("NODE_NAME"));
					ServDao.update(SY_WFE_TRACK, sqlBean);
				}
			} catch (Exception e) {
				LOG.error("invoke getInfoByProcAndNode failed");
				LOG.error(e.getMessage(), e);
			} 

		}
		
		return outBean.setOk();
	}
	
	/**
	 * 更新待办的节点名称信息
	 * @param paramBean
	 * @return
	 */
	public static OutBean updateTodoNodeInfo (ParamBean paramBean){
		OutBean outBean = new OutBean();
		try {
			Transaction.begin();
			String niId = paramBean.getStr("NI_ID");
			String nodeName = paramBean.getStr("NODE_NAME");
			String dataId = paramBean.getStr("DATA_ID");
			if (StringUtils.isEmpty(nodeName) || StringUtils.isEmpty(dataId)
					|| StringUtils.isEmpty(niId)) {
				return outBean.setOk();
			}
			String userCode = Context.getUserBean().getCode();
			SqlBean sqlBean = new SqlBean();
			sqlBean.and("TODO_OBJECT_ID1", dataId);
			sqlBean.and("SEND_USER_CODE", userCode);
			sqlBean.set("PRE_OPT_NAME", nodeName);
			ServDao.update(SY_COMM_TODO, sqlBean);
			sqlBean.clear();
			sqlBean.and("NI_ID", niId);
			sqlBean.set("NODE_NAME", nodeName);
			ServDao.update(SY_WFE_TRACK, sqlBean);
			Transaction.commit();
		} catch (Exception e) {
			Transaction.rollback();
			LOG.error(e.getMessage(), e);
		}
		return outBean.setOk();
	}
    
}
