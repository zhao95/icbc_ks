package com.rh.ts.jkgl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;

public class JkglServ extends CommonServ {

    public Bean getListDicCode(Bean paramBean) {
	String dicServId = paramBean.getStr("dicServId");
	String where = paramBean.getStr("where");
	List<Bean> treeList = ServDao.finds(dicServId, where);
	Bean outBean = new Bean();
	String aaa = "";
	for (Bean bean : treeList) {
	    String itemName = bean.getStr("ITEM_NAME");
	    aaa = aaa + itemName + ",";
	}
	String substring = "";
	if (aaa.length() > 0)
	    substring = aaa.substring(0, aaa.length() - 1);
	outBean.set("ITEM_NAME", substring);
	return outBean;
    }
    /**
     * 判断当前报名人是否在禁考名单中
     */
    public OutBean getjkstate(Bean paramBean){
    	OutBean out = new OutBean();
    	//判读审核规则里有没有 禁考规则 有的话  先验证禁考  没有不用验证
    	String xmid = paramBean.getStr("xmid");
    	String where1 = "AND XM_ID='"+xmid+"'";
    	List<Bean> guizelist = ServDao.finds("TS_XMGL_BMSH_SHGZ", where1);
    	boolean flag = false;
    	String gzid = "";
    	for (Bean bean : guizelist) {
			if("N03".equals(bean.getStr("GZK_ID"))){
				//启用禁考规则
				flag = true;
				gzid=bean.getStr("GZ_ID");
			}
		}
    	if(flag){
    	
    	UserBean userBean = Context.getUserBean();
    	String str = userBean.getStr("USER_CODE");
    	Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String format = sdf.format(date);
    	String where ="AND JKGL_USER_CODE='"+str+"' AND '"+format+"' BETWEEN JKGL_START_DATE AND JKGL_END_DATE";
    	List<Bean> finds = ServDao.finds("TS_JKGL",where);
    	if(finds!=null && finds.size()!=0){
    		out.set("num", finds.size());
    		out.set("start", finds.get(0).getStr("JKGL_START_DATE"));
    		out.set("end", finds.get(0).getStr("JKGL_END_DATE"));
    		out.set("reason", finds.get(0).getStr("JKGL_REASON"));
    		//提示语
    		String where3 = "AND GZ_ID='"+gzid+"'";
    		List<Bean> finds2 = ServDao.finds("TS_XMGL_BMSH_SHGZ_MX", where3);
    			for (Bean bean : finds2) {
					String str2 = bean.getStr("MX_VALUE2");
					out.set("tsh", str2);
				}
    	}else if(finds!=null && finds.size()==0){
    		out.set("num", finds.size());
    	}
    	}else{
    		out.set("num",0);
    	}
    	return out;
    }

}
