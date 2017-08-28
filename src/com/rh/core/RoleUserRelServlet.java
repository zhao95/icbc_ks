package com.rh.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.logs.RHLog;
import com.rh.core.org.mgr.RoleUserRelMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.RequestUtils;

/**
 * 提供给OIS系统同步角色接口
 * @author yangjy
 *
 */
public class RoleUserRelServlet extends HttpServlet {

	/** sid */
	private static final long serialVersionUID = 8552320749993501826L;
	/** log */
    private static Log log = LogFactory.getLog(RoleUserRelServlet.class);


	@Override
	/**
	 * 角色和用户关系接口请求处理，要求url格式为：http://....:80/roleuserrel?.....
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		log.debug("------------------ RoleUserRelServlet begin !");
		// 设置上下文信息
		String onlineUserCode = Context.getSyConf("CC_IMPDATE_ONLINE_USER", "0000803837");
		try {
			Context.setThreadUser(UserMgr.getUserState(onlineUserCode));
		} catch (Exception e) {
			log.error("------------------ RoleUserRelServlet error! " + e.getMessage());
			RHLog.error(RoleUserRelServlet.class, e);
		}
		
		ParamBean paramBean = null; // 参数信息
		OutBean resultBean = new OutBean(); // 结果信息
		
		paramBean = new ParamBean(request);
		String datasStr = paramBean.getStr("DATAS");
		String act = paramBean.getStr("act");
		log.debug("------------------ RoleUserRelServlet : datasStr = " + datasStr + "!");
		List<Bean> dataList = JsonUtils.toBeanList(datasStr);
		log.debug("------------------ RoleUserRelServlet : dataList = " + dataList + "!");
		try {
			resultBean = doAct(act, dataList);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			RHLog.error(RoleUserRelServlet.class, e);
            resultBean.setError(RequestUtils.escapeHTML(e.getMessage()));
		}
		
		// =================处理返回信息===================
        if (!response.isCommitted()) {
            // 返回信息
            String header;
            String content;
            header = "text/html; charset=utf-8";
            content = JsonUtils.toJson(resultBean, false, paramBean.getEmptyFlag()); //支持压缩空值输出
            response.setContentType(header);
            PrintWriter out = response.getWriter();
            out.write(content);
            out.flush();
            out.close();
        }
        
        Context.cleanThreadData();
	}
	
	/**
	 * 根据参数act，来处理数据dataList
	 */
	private OutBean doAct(String act, List<Bean> dataList) throws Exception {
		if (StringUtils.isEmpty(act)) { //act为空，同步dams_user_role_rel表数据
			return RoleUserRelMgr.impRoleUserRel(dataList);
		} else if (act.equals("accredit_main")) { //同步dams_accredit_main表数据
			return RoleUserRelMgr.impAccreditMain(dataList);
		} else {
			return new OutBean().setError("无效的act类型：" + act);
		}
	}
}
