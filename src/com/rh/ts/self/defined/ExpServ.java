package com.rh.ts.self.defined;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;

public class ExpServ extends CommonServ {

	/**
	 * 根据servId获取  serv字段
	 */
	public OutBean getServColumn(Bean paramBean){
		//所有表字段
		String servId = paramBean.getStr("servid");
		String where = "AND SERV_ID=" + "'" + servId + "' AND ITEM_TYPE <> 3 AND S_FLAG=1";
		List<Bean> listcolumn = ServDao.finds("SY_SERV_ITEM", where);
		//已保存的自定义字段
		String user_code  =  Context.getUserBean().getStr("USER_CODE");
		String where2="AND USER_CODE='"+user_code+"' AND SERV_NAME='"+servId+"' ORDER BY COLUMN_XUHAO";
		List<Bean> selflist = ServDao.finds("TS_SELF_DEFINED_EXP", where2);
		List<String> strlist = new ArrayList<String>();
		for (Bean bean : selflist) {
			strlist.add(bean.getStr("COLUMN_CODE"));
		}
		if(listcolumn!=null&&listcolumn.size()!=0){
			for (int j=0;j<listcolumn.size();j++) {
				String str = listcolumn.get(j).getStr("ITEM_CODE");
				if(strlist.contains(str)){
					listcolumn.remove(j);
					j=0;
				}
			}
			
			//返回column
			OutBean out = new OutBean();
			
			out.set("list", listcolumn);
			out.set("list1", selflist);
			return out;
		}
		return new OutBean();
	}
	/**
	 * 保存排序
	 */
	public void saveself(Bean paramBean){
		String servId = paramBean.getStr("ServID");
		String user_code = paramBean.getStr("user_code");
		String codes = paramBean.getStr("codes");
		String names = paramBean.getStr("names");
		String where = "AND USER_CODE = '"+user_code+"' AND SERV_NAME='"+servId+"'";
		 List<Bean> existlist = ServDao.finds("TS_SELF_DEFINED_EXP", where);
		if(existlist!=null&&existlist.size()!=0){
			//删除已存在的数据
			for(int i=0;i<existlist.size();i++){
				
				ServDao.delete("TS_SELF_DEFINED_EXP", existlist.get(i).getId());
			}
		}
		String[] split = codes.split(",");
		String[] split2 = names.split(",");
		if(split!=null&&split.length!=0){
			for(int j=0;j<split.length;j++){
				if(!"".equals(split[j])){
					Bean paranbean = new Bean();
					paranbean.set("USER_CODE", user_code);
					paranbean.set("COLUMN_CODE",split[j]);
					paranbean.set("SERV_NAME", servId);
					paranbean.set("COLUMN_XUHAO", j);
					paranbean.set("COLUMN_NAME", split2[j]);
					ServDao.save("TS_SELF_DEFINED_EXP", paranbean);
				}
			}
		}
		
	}
}
