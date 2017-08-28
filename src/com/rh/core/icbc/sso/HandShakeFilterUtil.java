package com.rh.core.icbc.sso;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;

/**
 * HandShakeFilterOis的工具辅助类
 * @author yjzhou
 * 2016.08.08
 */
public class HandShakeFilterUtil {
	
    //服务ID，用户存储用户从OIS登录因私出境系统的URL
    private static final String PE_LOGIN_LINK_PARAM = "PE_LOGIN_LINK_PARAM";
	
	//构造函数私有
    private HandShakeFilterUtil(){
    	
    }
    
	/**
	 * 保存用户登录因私出境系统的URL的参数
	 * @param paramBean
	 * @param ssicId
	 */
	public static void saveLoginParam(ParamBean paramBean,String ssicId){
		final String ssiAuth = paramBean.getStr("ssiAuth");
		final String ssiSign = paramBean.getStr("ssiSign");
		final String dseSessionId = paramBean.getStr("dse_sessionId");
		final String language = paramBean.getStr("language");
		if (StringUtils.isNotEmpty(ssicId)) {
			Bean oldData = ServDao.find(PE_LOGIN_LINK_PARAM, ssicId);
		    Bean newData = new Bean();
		    newData.set("SSI_AUTH", ssiAuth);
		    newData.set("SSI_SIGN", ssiSign);
		    newData.set("DSE_SESSIONID", dseSessionId);
		    newData.set("LANGUAGE_TYPE", language);
		    newData.set("SSIC_ID", ssicId);
			if (null == oldData) {
                //若原本不存在
				ServDao.save(PE_LOGIN_LINK_PARAM, newData);
			}else {
				//若原本存在
				newData.setId(oldData.getId());
				newData.set("SSIC_ID", oldData.getId());
				ServDao.update(PE_LOGIN_LINK_PARAM, newData);
			}
		}
	}
}
