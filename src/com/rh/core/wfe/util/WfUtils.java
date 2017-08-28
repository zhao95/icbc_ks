package com.rh.core.wfe.util;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.base.BaseContext.APP;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.Lang;
import com.rh.core.wfe.WfAct;

/**
 * 工作流常用类
 *
 */
public class WfUtils {
	/**
	 * 得到工作流定义的保存路径 
	 * '/home/zotn-oa/webapps/product/public-html/WEB-INF/doc/company/compyID/workflow/defxml/'
	 * 
	 * @param cmpyID
	 *            公司ID
	 * 
	 * @return the path of gongwen for http useage
	 */
	public static String getCmpyWfDefXMLPath(String cmpyID) {
		StringBuffer path = new StringBuffer(Context.appStr(APP.WEBINF_DOC_CMPY));
		path.append(cmpyID);
		path.append(Constant.PATH_SEPARATOR);
		path.append("workflow");
		path.append(Constant.PATH_SEPARATOR);
		path.append("defxml");

		return path.toString();
	}

	/**
	 * 得到工作流 定义的 文件  完整路径
	 * @param cmpyId 公司ID
	 * @param enName 英文名称
	 * @return 工作流文件的保存路径
	 */
	public static String getSavedDefFileName(String cmpyId , String enName) {
		return getCmpyWfDefXMLPath(cmpyId)
				+ Constant.PATH_SEPARATOR
				+ enName + ".xml";
	}
	
    /**
     * 执行返回结果为bool值的js脚本
     * @param script 脚本
     * @param bean 包含脚本中需使用变量数据的bean
     * @return 执行结果
     */
    public static boolean execCondScript(String script , Bean bean) {
        if (StringUtils.isEmpty(script)) {
            return true;
        }
        script = ServUtils.replaceSysAndData(script, bean);
        
        return Lang.isTrueScript(script);
    }
    
    /**
     * @param procRunning 从页面传来的 WF_INST_ID 字符串类型的 流程是否运行
     * @return 流程数据是否放在运行表中。
     */
    public static boolean procIsRunning(String procRunning) {
        boolean procIsRunning = true;

        if (procRunning.equals(String.valueOf(WfeConstant.PROC_NOT_RUNNING))) {
            procIsRunning = false;
        }

        return procIsRunning;
    }
    
    /**
     * 从ParamBean中获取参数，返回流程数据是否放在运行表中。
     * @param paramBean 参数Bean
     * @return 流程数据是否放在运行表中，是返回true，否则返回false。
     */
    public static boolean procIsRunning(ParamBean paramBean) {
        return procIsRunning(paramBean.getStr("INST_IF_RUNNING"));
    }
    
    /**
     * 从ParamBean中获取参数，创建WfAct对象。
     * @param paramBean 参数Bean。
     * @return WfAct对象
     */
    public static WfAct createWfAct(ParamBean paramBean) {
        if (paramBean.isEmpty("INST_IF_RUNNING")) {
            return null;
        }
        
        if (paramBean.isEmpty("NI_ID")) {
            return null;
        }
        boolean procInstIsRunning = procIsRunning(paramBean);
        WfAct currWfAct = new WfAct(paramBean.getStr("NI_ID"),
                procInstIsRunning);
        
        return currWfAct;
    }
    
    
    /***
     * 
     * @param niId
     * @param realUser
     * @param procIsRunning
     */
	public static void duzhan(String niId, UserBean realUser, boolean procIsRunning) {
		WfAct wfAct = null;
		  if (StringUtils.isNotBlank(niId)) {
		      wfAct = new WfAct(niId, procIsRunning);
		  } else {
		      throw new TipException("无效参数NI_ID");
		  }

		  if (wfAct.canDuzhan()) { //点了独占之后，添加TO_USER_ID 和 TO_USER_NAME
		      wfAct.duzhan(realUser);
		  }
	}
	
   /**
    * 
    * @return 取得流程的办理用户 ， 如果参数中包含DO_USER_DEPT， 则返回这个用户的UserBean，否则抛错误。
    */
	public static UserBean getDoUserBean(ParamBean paramBean) {
		// 获取 委托人 的 UserBean
		if(paramBean.isEmpty("DO_USER_DEPT")) {
        	throw new TipException("参数DO_USER_DEPT不能为空。");
        }
		// 处理转授权办理用户
		String doUserDept = paramBean.getStr("DO_USER_DEPT");
		final int pos = doUserDept.indexOf("@");
		if(pos > 0) {
			doUserDept = doUserDept.substring(0, pos);
		}
		UserBean doUser = UserMgr.getUserByUserDept(doUserDept);
        return doUser;
	}
}
