package com.rh.core.org.auth.login;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Constant;
import com.rh.core.util.EncryptUtils;

/**
 * 基于用户名密码的认证模块
 * 
 * @author cuihf
 *
 */
public class PasswordLoginModule extends AbstractLoginModule {
    private static final String PARAM_ID = "id";
    private static final String PARAM_LOGINNAME = "loginName";
    private static final String PARAM_PASSWORD = "password";
    private static final String PARAM_CMPYCODE = "cmpyCode";

    @Override
    public UserBean authenticate(Bean paramBean) {
        UserBean userBean;
        String cmpyCode;
        // 接受的参数：id,password 或者 loginName,password,cmpyCode
        String id = paramBean.getStr(PARAM_ID);
        String password = paramBean.getStr(PARAM_PASSWORD);
        if (id.length() > 0) { //id认证模式
            userBean = UserMgr.getUserByMobileOrMail(id);
            cmpyCode = userBean.getStr("CMPY_CODE");
        } else { //login和cmpy认证模式
            String loginName = paramBean.getStr(PARAM_LOGINNAME);
            cmpyCode = paramBean.getStr(PARAM_CMPYCODE);
            if (loginName.length() <= 0 || password.length() <= 0 || cmpyCode.length() <= 0) {
                throw new RuntimeException("认证参数错误，用户名：" + loginName + ";公司编码：" + cmpyCode 
                        + ";密码长度：" + password.length());
            }

            if (Context.getSyConf("SY_ORG_LOGIN_NAME_LOWERCASE", true)) {
                loginName = loginName.toLowerCase();
            }
            userBean = UserMgr.getUserByLoginName(loginName, cmpyCode);
        }
        //验证公司信息
        Bean cmpy = DictMgr.getItem("SY_ORG_CMPY", cmpyCode);
        if (cmpy == null) {
            throw new TipException(Context.getSyMsg("SY_CMPY_NOT_FOUND", cmpyCode));
        } else if (cmpy.getInt("FLAG") != Constant.YES_INT) { //公司被禁用
            throw new TipException(Context.getSyMsg("SY_CMPY_FORBIDDEN", cmpyCode));
        }
        //加密后进行判断
        password = EncryptUtils.encrypt(password, 
                Context.getSyConf("SY_USER_PASSWORD_ENCRYPT", EncryptUtils.DES));
        //先进行 临时用户密码判断   如果临时用户密码 有效启用临时密码
        String s = userBean.getStr("USER_TEMP_PASSWORD_MADTIME");
        if(!"".equals(s)){
        	    try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long timeInMillis = sdf.parse(s).getTime();
					Date datenow= new Date();
					long between = datenow.getTime()-timeInMillis;
					if(between>=(24* 3600000)){
						//超过了一天 不能使用临时密码
						if (!password.equals(userBean.getPassword())) {
			        		throw new RuntimeException("输入的密码错误。");
			        	}
					}else{
						if(!password.equals(userBean.getTempPassword())){
							throw new RuntimeException("输入的密码错误。");
						}
					}
				} catch (ParseException e) {
					throw new RuntimeException("日期转换错误。");
				}

        }else{
        	if (!password.equals(userBean.getPassword())) {
        		throw new RuntimeException("输入的密码错误。");
        	}
        }
        String user_code = userBean.getStr("USER_CODE");
        String where = "AND PERSON_ID='"+user_code+"' AND STRU_FLAG='1' ORDER BY S_MTIME DESC";
        List<Bean> slavelist  = ServDao.finds("sy_hrm_zdstaffstru",where);
        if(slavelist!=null&&slavelist.size()!=0){
        	for (Bean bean : slavelist) {
        		String dept_code = bean.getStr("STRU_ID");
        		DeptBean dept = OrgMgr.getDept(dept_code);
        		if(dept.getStr("DEPT_CODE").equals(dept.getStr("ODEPT_CODE"))){
        			//机构
        		}else{
        			//部门
        			userBean.set("DEPT_CODE", dept.getStr("DEPT_CODE"));
        			userBean.set("ODEPT_CODE", dept.getStr("ODEPT_CODE"));
        			userBean.set("TDEPT_CODE", dept.getStr("TDEPT_CODE"));
        			userBean.set("DEPT_NAME", dept.getName());
        			userBean.set("ODEPT_NAME", dept.getODeptBean().getName());
        			userBean.set("TDEPT_NAME", dept.getTDeptBean().getName());
        			userBean.set("CODE_PATH", dept.getCodePath());
        			userBean.set("STRU_FLAG", 1);
        			userBean.set("DEPT_LEVEL", dept.getStr("DEPT_LEVEL"));
        			userBean.set("DEPT_SORT", dept.getStr("DEPT_SORT"));
        			userBean.set("DEPT_CODE_M", dept.getStr("DEPT_CODE"));
        			Context.setOnlineUser(userBean);
        		}
			}
        	
        }
        return userBean;
    }

}
