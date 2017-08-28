package com.rh.core.wfe.serv;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.WfProcess;
import com.rh.core.wfe.db.WfNodeInstDao;
import com.rh.core.wfe.db.WfNodeInstHisDao;
import com.rh.core.wfe.db.WfNodeUserDao;
import com.rh.core.wfe.db.WfNodeUserHisDao;
import com.rh.core.wfe.util.WfeConstant;
import com.rh.core.wfe.util.WfeFigure;

/**
 * 图形化流程跟踪
 *
 */
public class FigureServ extends CommonServ {
	
    /**
     * 图形化显示流程跟踪
     * @param paramBean 参数Bean
     * @return 返回前台Bean
     */
    public OutBean show(ParamBean paramBean) {
    	String pid = paramBean.getStr("PI_ID");
        String procRunning = paramBean.getStr("INST_IF_RUNNING");
        
        String servId = WfNodeInstDao.SY_WFE_NODE_INST_SERV;  //流程未办结
        String userServId = WfNodeUserDao.SY_WFE_NODE_USERS;
        
		if (!procInstIsRunning(paramBean)) { //流程已经办结
			servId = WfNodeInstHisDao.SY_WFE_NODE_INST_HIS_SERV; 
			userServId = WfNodeUserHisDao.SY_WFE_NODE_USERS_HIS;
		}
		
		Bean queryBean = new Bean();
		queryBean.set("PI_ID", pid);
		queryBean.set("INST_IF_RUNNING", procRunning);
		queryBean.set(Constant.PARAM_ORDER, "NODE_BTIME ASC");
		
        boolean isProcRunning = procInstIsRunning(paramBean);
		
        WfProcess wfProcess = new WfProcess(pid, isProcRunning);
    	
        queryBean.set("PROC_CODE", wfProcess.getProcDef().getStr("PROC_CODE"));
        
    	//图形化数据 for 测试
        String xmlContent = wfProcess.getProcDef().getStr("PROC_XML");  //通过工作流定义，取到xml文件
        
		List<Bean> wfInstBeanList = (List<Bean>) ServDao.finds(servId, queryBean);
		
		SqlBean sqlUser = new SqlBean();
		sqlUser.and("PI_ID", pid);
		sqlUser.asc("NI_ID");
		List<Bean> wfNodeUsers = (List<Bean>) ServDao.finds(userServId, sqlUser);
        
        List<WfAct> wfInstList = new ArrayList<WfAct>();
        for (Bean wfInstBean: wfInstBeanList) {
        	WfAct wfAct = new WfAct(wfProcess, wfInstBean.getId(), isProcRunning);
        	
        	wfInstList.add(wfAct);
        }
        
        WfeFigure wfeFigure = new WfeFigure(xmlContent, wfInstList, wfNodeUsers);
        
        OutBean outBean = new OutBean();
        outBean.setToDispatcher("/sy/wfe/track.jsp");
        outBean.set("WF_XML", wfeFigure.getXMLContent());
        return outBean;
    }
    
    /**
     * @param paramBean 从页面传来的  INST_IF_RUNNING 字符串类型的 流程是否运行
     * @return 流程是否运行
     */
    private boolean procInstIsRunning(ParamBean paramBean) {

        String procRunning = paramBean.getStr("INST_IF_RUNNING");
        int sFlag = paramBean.getInt("S_FLAG");
        boolean procIsRunning = true;
        if (sFlag > 0 && sFlag == Constant.NO_INT) {
            procIsRunning = false;
        } else if (procRunning.equals(String.valueOf(WfeConstant.PROC_NOT_RUNNING))) {
            procIsRunning = false;
        }

        return procIsRunning;
    }
}
