package com.rh.ts.jkgl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    	List<Bean> guizelist = ServDao.finds("ts_xmgl_bm_jkgz", where1);
    	boolean flag = false;
    	String gzid = "";
    	for (Bean bean : guizelist) {
				//启用禁考规则
				gzid=bean.getStr("GZ_ID");
				 flag = true;
		}
    	if(flag){
    	UserBean userBean = Context.getUserBean();
    	String str = userBean.getStr("USER_CODE");
    	Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String format = sdf.format(date);
    	String where ="AND JKGL_RLZY='"+str+"' AND '"+format+"' BETWEEN JKGL_START_DATE AND JKGL_END_DATE";
    	List<Bean> finds = ServDao.finds("TS_JKGL",where);
    	String str2 = "";
    	if(finds!=null && finds.size()!=0){
    		//被禁考了  判断此人禁考结束时间 是否在配置信息中的时间之前
    		String where3 = "AND GZ_ID='"+gzid+"' order by MX_SORT asc";
    		List<Bean> finds2 = ServDao.finds("ts_xmgl_bm_jkglgz_mx", where3);
    		boolean flagbm = false;
    		for (Bean bean : finds2) {
    			int count=0;
    			String lk=bean.getStr("MX_NAME");
    			for(int i=0;i<lk.length();i++){
    			if(lk.charAt(i)=='#'){
    			count++;
    			}
    			}
    			if(count>2){
    				 str2 = bean.getStr("MX_NAME");

	    			 str2 = str2.replace("#stime#", finds.get(0).getStr("JKGL_START_DATE"));
	    			 str2=str2.replace("#endtime#", finds.get(0).getStr("JKGL_END_DATE"));
	    			 str2=str2.replace("#reason#", finds.get(0).getStr("JKGL_REASON"));
	    			 String start = bean.getStr("JKGL_START_DATE");
	    			String endd =  finds.get(0).getStr("JKGL_END_DATE");
	    			String reason = finds.get(0).getStr("JKGL_REASON");
		    		out.set("start",start);
		    		out.set("end",endd);
		    		out.set("reason",reason);

    			}else{
    				//时间
    				String str3 = bean.getStr("MX_VALUE2");
    				try {
						JSONArray JSON  = new JSONArray(str3);
						JSONObject jsonObject = JSON.getJSONObject(0);
						//时间
						String string = jsonObject.getString("val");
						SimpleDateFormat simp = new SimpleDateFormat("yyyyMMdd");
						SimpleDateFormat simp2 = new SimpleDateFormat("yyyy-MM-dd");
						try {
							Date parse = simp.parse(string);
						String end = finds.get(0).getStr("JKGL_END_DATE");
						Date parse2 = simp2.parse(end);
						
						if(parse2.getTime()>parse.getTime()){
							//超过配置时间 不可报名
							flagbm= true;
						}else{
						}
						
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				
    			}
			}
    		
    		if(flagbm){
    			//不可报名
    			out.set("num", finds.size());
    			out.set("tsh", str2);
    		}else{
    			out.set("num",0);
    		}
    	}else if(finds!=null && finds.size()==0){
    		out.set("num", 0);
    	}
    	}else{
    		out.set("num",0);
    	}
    	return out;
    }

}
