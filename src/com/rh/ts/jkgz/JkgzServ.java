package com.rh.ts.jkgz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;

public class JkgzServ extends CommonServ {
	/**
     * 从规则库中倒入规则  注：TS_XMGL_BMSH_SHGZK中的主键GZ_ID
     * 保存到TS_XMGL_BMSH_SHGZ的字段GZK_ID非GZ_ID
     * @param paramBean
     * @return
     */
    public OutBean impShgz(ParamBean paramBean) {
	OutBean outBean =new OutBean();
	String xmId = paramBean.getStr("xmId");
	String ids = paramBean.getStr("ids");
	for (int i = 0; i < ids.split(",").length; i++) {
	    String dataId = ids.split(",")[i];
	    Bean bean = ServDao.find("TS_XMGL_BM_JKGLGZK", dataId);
	    Bean dataBean = new Bean();
	    dataBean.set("XM_ID", xmId);
	    dataBean.set("GZK_ID", bean.getStr("GZ_ID"));
	    dataBean.set("GZ_TYPE", bean.getStr("GZ_TYPE"));
	    dataBean.set("GZ_NAME", bean.getStr("GZ_NAME"));
	    dataBean.set("GZ_INFO", bean.getStr("GZ_INFO"));
	    dataBean.set("GZ_SORT", bean.getStr("GZ_SORT"));
	    Bean gzBean = ServDao.save("TS_XMGL_BM_JKGZ", dataBean);
	    String gzId = gzBean.getId();
	    //TS_XMGL_BMSH_SHGZK_MX TS_XMGL_BMSH_SHGZK_MX
	    List<Bean> gzkMxList = ServDao.finds("TS_XMGL_BM_JKGLGZK_MX", "and GZ_ID = '"+dataId+"'");
	    for (int j = 0; j < gzkMxList.size(); j++) {
		Bean gzkMxBean = gzkMxList.get(j);
		gzkMxBean.setId("");
		gzkMxBean.remove("MX_ID");
		gzkMxBean.set("GZ_ID", gzId);
		gzkMxBean.set("XM_ID", xmId);
		gzkMxBean.remove("S_CMPY");
		gzkMxBean.remove("S_USER");
		gzkMxBean.remove("S_DEPT");
		gzkMxBean.remove("S_TDEPT");
		gzkMxBean.remove("S_ODEPT");
		gzkMxBean.remove("S_MTIME");
		gzkMxBean.remove("S_ATIME");
		String MXVALUE2 = gzkMxBean.getStr("MX_VALUE2");
		if(!"".equals(MXVALUE2)){
			try {
				JSONArray json = new JSONArray(MXVALUE2);
				if("date".equals(json.getJSONObject(0).getString("type"))){
					String fuhao2 = json.getJSONObject(0).getString("val");
					String jsons = json.toString();
					String[] split = jsons.split(fuhao2);
					SimpleDateFormat simp = new SimpleDateFormat("yyyyMMdd");
					Date date = new Date();
					String newdate = simp.format(date);
					String jsonstr = split[0]+ newdate+split[1];
					jsonstr = jsonstr.replace("\"", "\'");
					gzkMxBean.set("MX_VALUE2",jsonstr);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ServDao.save("ts_xmgl_bm_jkglgz_mx", gzkMxBean);
	    }
	}
	outBean.setOk();
	return outBean;
    }
}
