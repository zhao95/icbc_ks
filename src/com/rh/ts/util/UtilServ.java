package com.rh.ts.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;

public class UtilServ extends CommonServ {

    /**
     * 返回菜单列表
     * 
     * @param paramBean
     * @return
     */
    public OutBean getMenu(ParamBean paramBean) {
	OutBean outBean = new OutBean();
	String servId = "TS_MENU";
	paramBean.set(Constant.PARAM_ORDER, "MENU_SORT");
	List<Bean> list = ServDao.finds(servId, paramBean);
	outBean.set("menuList", list);
	return outBean;
    }
    
    public OutBean  checkVerify(ParamBean paramBean) {
	OutBean outBean = new OutBean();
	String vcode = paramBean.getStr("vcode");
	HttpServletRequest request = Context.getRequest();
	HttpSession session = request.getSession();
	String codeValidate = (String) session.getAttribute("codeValidate");
	boolean msg = false;
	if (codeValidate.equals(vcode.toUpperCase())) {
	    msg = true;
	}
	outBean.set("res", msg);
	return outBean;
    }
    
    /**
     * 回收站
     * @param paramBean
     * @return
     */
    public OutBean trash(ParamBean paramBean) {
	OutBean outBean = new OutBean();
	String servId = paramBean.getStr("servId");
	String pkCodes = paramBean.getStr("pkCodes");
	String stateColCode = paramBean.getStr("stateColCode");
	String action = paramBean.getStr("action");
	
	for (String pkCode : pkCodes.split(",")) {
	    Bean bean = ServDao.find(servId, pkCode);
	    int state = bean.getInt(stateColCode);
	    if (action.equals("add")) {
		if(state > 10) continue;
		bean.set(stateColCode, bean.getInt(stateColCode)+10);
	    }else{
		if(state < 10) continue;
		bean.set(stateColCode, bean.getInt(stateColCode)-10);
	    }
	    ServDao.update(servId, bean);
	}
	outBean.setOk();
	return outBean;
    }
    
    public OutBean copy(ParamBean paramBean) {
	OutBean outBean = new OutBean();
	String servId = paramBean.getStr("servId");
	String primaryColCode = paramBean.getStr("primaryColCode");
	String pkCode = paramBean.getStr("pkCode");
	Bean bean = ServDao.find(servId, pkCode);
	bean.remove(primaryColCode);
	bean.setId("");
	bean = delSysCol(bean);
	Bean newBean = ServDao.create(servId, bean);
	if(!newBean.getId().equals("")){
	    copyLinkData(servId,pkCode,newBean.getId());
	    outBean.setOk();
	}
	return outBean;
    }
    /**
     * 删除系统字段
     * @param bean
     * @return
     */
    public Bean delSysCol(Bean bean){
	bean.remove("S_USER");
	bean.remove("S_DEPT");
	bean.remove("S_ODEPT");
	bean.remove("S_TDEPT");
	bean.remove("S_ATIME");
	bean.remove("S_MTIME");
	return bean;
    }
    
    /**
     * 拷贝关联表数据
     * @param servId
     * @param oldDataId 拷贝数据主键
     * @param newDataId 保存数据主键
     */
    public void copyLinkData(String servId,String oldDataId,String newDataId){
	List<Bean> list = linkServ(servId);
	for (int i = 0; i < list.size(); i++) {
	    String linkServ = list.get(i).getStr("LINK_SERV_ID");
	    String primaryCode = primaryCode(linkServ);
	    String LINK_ITEM_CODE = list.get(i).getStr("LINK_ITEM_CODE");
	    List<Bean> linkDatalist = ServDao.finds(linkServ, "and " + LINK_ITEM_CODE +" = '"+oldDataId+"'");
	    for (int j = 0; j < linkDatalist.size(); j++) {
		Bean bean = linkDatalist.get(j);
		//旧数据主键
		String pkCode = bean.getId();
		bean.set(LINK_ITEM_CODE, newDataId);
		bean.setId("");
		bean.remove(primaryCode);
		bean = delSysCol(bean);
		Bean newBean = ServDao.save(linkServ, bean);
		if (newBean.getId() != "") {
		    copyLinkData(linkServ,pkCode,newBean.getId());
		}
	    }
	}
    }
    
    /**
     * 取得服务的主键编码
     * @param servId
     * @return
     */
    public String primaryCode(String servId){
	Bean bean = ServDao.find("SY_SERV", servId);
	return bean.getStr("SERV_KEYS");
    }
    
    /**
     * 注意：程序只考虑了最普通的主服务和自服务有一个字段有关系的情况，如果使用中情况不只一条绑定主键外键这种情况，需要自己另作处理
     * 取得关联服务
     * @param servId
     */
    public List<Bean> linkServ(String servId){
	ArrayList<Bean> list = new ArrayList<Bean>();
	List<Bean> linkList = ServDao.finds("SY_SERV_LINK", "and SERV_ID = '"+servId+"' and S_FLAG = 1");
	for (int i = 0; i < linkList.size(); i++) {
	    Bean bean = new Bean();
	    String linkId = linkList.get(i).getId();
	    String LINK_SERV_ID = linkList.get(i).getStr("LINK_SERV_ID");
	    bean.set("LINK_SERV_ID", LINK_SERV_ID);
	    List<Bean> itemList = ServDao.finds("SY_SERV_LINK_ITEM", "and LINK_ID = '"+linkId+"' and LINK_VALUE_FLAG = 1");
	    if(itemList.size() > 0){
		String ITEM_CODE = itemList.get(0).getStr("ITEM_CODE");
		String LINK_ITEM_CODE = itemList.get(0).getStr("LINK_ITEM_CODE");
		bean.set("ITEM_CODE", ITEM_CODE);
		bean.set("LINK_ITEM_CODE", LINK_ITEM_CODE);
	    }
	    list.add(bean);
	}
	return list;
    }
    /**
     * 
     * 登陆人对应列表数据的权限
     * @param servId
     */  
    public  void userPvlg(ParamBean param){
    	ParamBean paramBean = (ParamBean) param.getBean("paramBean");
    	String ctlgModuleName = param.getStr("ctlgModuleName");
    	int treeWhereSize = paramBean.getList("_treeWhere").size();
		if(treeWhereSize == 0){
		 // tree的编码 机构编码
		Bean extParams = paramBean.getBean("extParams");
		// String dcode = extParams.getStr("PVLG_FIELD");
		// 用户权限 所有权限的机构编码
		Bean userPvlg = extParams.getBean("USER_PVLG");
		JSONObject jsonObject = new JSONObject(userPvlg);
		String result=null;
		Iterator iterator = jsonObject.keys();
	  	String key;
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			try {
				JSONObject object = (JSONObject) jsonObject.get(key);
				String object2 = (String)object.get("ROLE_DCODE");
				String[] object3=object2.split(",");
				if(result!=null){
					for(int  i=0;i<object3.length;i++){
						if(result.indexOf(object3[i])<0){
							result+=","+object3[i];
						}
					}
				}else{
					result =object2;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		 if(result !=null){			
			 //result 排序
			 String[] roles = result.split(",");
		/*	 Arrays.sort(roles);
			 String lastResult="";
			 for(int i=0;i<roles.length;i++){
				 lastResult+="^PROJECT-"+roles[i];
			 }
			 lastResult = lastResult.substring(1)+"^";
			 System.out.println("lastResult:"+lastResult);*/
			 StringBuilder param_where=new StringBuilder();
			 param_where.append("AND  EXISTS ( ");
			 param_where.append("SELECT CTLG_CODE FROM TS_COMM_CATALOG  ");
			 param_where.append("WHERE  CTLG_MODULE='"+ctlgModuleName+"' AND TS_XMGL.CTLG_PCODE = CTLG_CODE_H ");
			 param_where.append("and INSTR (CTLG_PATH,"+"'"+roles[0]+"') ");
			 //StringBuilder subSQL= new StringBuilder();
			 param_where.append(") ");
			 paramBean.set(Constant.PARAM_WHERE, param_where.toString());
			 System.out.println("param_where:"+param_where.toString());
		 }else{
		 //无权限
		 paramBean.set(Constant.PARAM_WHERE, " and 1=2");
		 }
		} 
    	
    }
}
