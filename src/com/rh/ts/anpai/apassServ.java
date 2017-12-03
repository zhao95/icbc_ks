package com.rh.ts.anpai;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;
import com.rh.ts.util.RoleUtil;
/**
 * 
 * @author shiyun
 *
 */
public class apassServ extends CommonServ {

	protected void beforeQuery(ParamBean paramBean){
		UserBean userBean = Context.getUserBean();
		String user_code = userBean.getCode();
		String odeptcode ="";
		Bean userPvlgToHT = RoleUtil.getPvlgRole(user_code);
		Bean userPvlgToHTBean = (Bean) userPvlgToHT.get("TS_XMGL_KCAP_YAPZW_PVLG");
		if (userPvlgToHTBean == null) {
			return ;
		}
		if("0".equals(userPvlgToHTBean.getStr("publish"))&&"0".equals(userPvlgToHTBean.getStr("auto"))){
			return ;
		}
		int  a=0;
		if ("0".equals(userPvlgToHTBean.getStr("publish"))) {
			Bean str = (Bean) userPvlgToHTBean.get("auto");
			if (str == null) {
				return;
			}
			odeptcode = str.getStr("ROLE_DCODE");
			if ("".equals(odeptcode)) {
				 odeptcode = userBean.getODeptCode();
			}
			//提交人
			a=1;
		} else {
			Bean str = (Bean) userPvlgToHTBean.get("publish");
			if (str == null) {
				return ;
			}
			odeptcode = str.getStr("ROLE_DCODE");
			if ("".equals(odeptcode)) {
				 odeptcode = userBean.getODeptCode();
			}
			//发布人  
			a=2;
		}
		odeptcode=odeptcode.substring(0,10);
		if(a==1){
			//查询提交人
			paramBean.set(Constant.PARAM_WHERE, "and TJ_DEPT_CODE='"+odeptcode+"'");
		}else if(a==2){
			//发布人
			paramBean.set(Constant.PARAM_WHERE, "and LENGTH(TJ_DEPT_CODE)>10");

		}

	}
}
