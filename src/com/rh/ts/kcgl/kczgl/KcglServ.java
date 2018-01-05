package com.rh.ts.kcgl.kczgl;

import java.util.LinkedHashMap;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ExportExcel;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Strings;

public class KcglServ extends CommonServ{
    private static final int ONETIME_EXP_NUM = 20000;
    private static final String servId1 = "TS_KCZGL_KCGL";
    
    /**
     * 考场组添加考场
     * @param paramBean
     * @return
     */
    public  OutBean kczAddKc(ParamBean paramBean) {
	String kcIds = paramBean.getStr("ids");
	String groupId = paramBean.getStr("groupId");
	for (int i = 0; i < kcIds.split(",").length; i++) {
	    String kcId = kcIds.split(",")[i];
	    
	    if (Strings.isBlank(kcId)) {
			continue;
		}
	    Bean kcBean = ServDao.find("TS_KCGL", kcId);
	    kcBean = delSysCol(kcBean);
	    kcBean.set("GROUP_ID", groupId);
	    kcBean.set("SERV_ID", servId1);
	    kcBean.set("COPY_ID", kcBean.getId());
	    kcBean.setId("");
	    kcBean.remove("KC_ID");
	    //主表数据复制
	    Bean resBean = ServDao.save(servId1, kcBean);
	    String resBeanId = resBean.getId();
	    //子表数据复制	 
	    List<Bean> list = ServDao.finds("SY_SERV_LINK", "and SERV_ID = 'TS_KCZGL_KCGL' and S_FLAG = 1");
	    for (int j = 0; j < list.size(); j++) {
		String linkServId = list.get(j).getStr("LINK_SERV_ID");
		List<Bean> linkList = ServDao.finds(linkServId, "and KC_ID = '"+kcId+"'");
		for (int k = 0; k < linkList.size(); k++) {
		    Bean linkBean = linkList.get(k);
		    linkBean = delSysCol(linkBean);
		    linkBean.setId("");
		    linkBean.remove(primaryCode(linkServId));
		    linkBean.set("KC_ID", resBeanId);
		    ServDao.save(linkServId, linkBean);
		 }
	    }
	}
	OutBean outBean = new OutBean();
	outBean.setOk();
	return outBean;
    }
    /**
     * 根据服务ID 取得主键编码
     * @param servId
     * @return
     */
    public String primaryCode(String servId){
	Bean bean = ServDao.find("SY_SERV", servId);
	return bean.getStr("SERV_KEYS");
    }
    /**
     * 更新考场信息
     * @param paramBean
     * @return
     */
    public OutBean updateKcInfo(ParamBean paramBean){
	OutBean outBean = new OutBean();
	String pkCodes = paramBean.getStr("pkCodes");
	String servId = paramBean.getStr("servId");
	for (int i = 0; i < pkCodes.split(",").length; i++) {
	    String dataId = pkCodes.split(",")[i];
	    Bean bean = ServDao.find(servId, dataId);
	    //此条考场属于的组ID	    
	    String GROUP_ID = bean.getStr("GROUP_ID");
	    //此条考场数据是从哪条数据拷贝过来的
	    String COPY_ID = bean.getStr("COPY_ID");
	    delLinkServInfo(servId,dataId);
	    Bean kcBean = ServDao.find("TS_KCGL", COPY_ID);
	    kcBean = delSysCol(kcBean);
	    kcBean.setId(dataId);
	    kcBean.remove("KC_ID");
	    kcBean.set("GROUP_ID", GROUP_ID);
	    kcBean.set("COPY_ID", COPY_ID);
	    kcBean.set("SERV_ID", servId1);
	    //考场组管理 考场表
	    ServDao.save(servId1, kcBean);
	    List<Bean> list = ServDao.finds("SY_SERV_LINK", "and SERV_ID = 'TS_KCZGL_KCGL' and S_FLAG = 1");
	    for (int j = 0; j < list.size(); j++) {
		String linkServId = list.get(j).getStr("LINK_SERV_ID");
		if (linkServId == "TS_XMGL_KCAP_DAPCC") {
		    continue;
		}
		List<Bean> linkList = ServDao.finds(linkServId, "and KC_ID = '"+COPY_ID+"'");
		for (int k = 0; k < linkList.size(); k++) {
		    Bean linkBean = linkList.get(k);
		    linkBean = delSysCol(linkBean);
		    linkBean.setId("");
		    linkBean.remove(primaryCode(linkServId));
		    linkBean.set("KC_ID", dataId);
		    ServDao.save(linkServId, linkBean);
		}
	    }
	}
	outBean.setOk();
	return outBean;
    }
    
    /**
     * 删除关联数据
     * @param servId
     * @param dataId 注：kc_id是关联字段
     */
    public void delLinkServInfo(String servId,String dataId){
	 List<Bean> list = ServDao.finds("SY_SERV_LINK", "and SERV_ID = '"+servId+"' and S_FLAG = 1");
	 for (int j = 0; j < list.size(); j++) {
		String linkServId = list.get(j).getStr("LINK_SERV_ID");
		Bean whereBean = new Bean();
		whereBean.set("KC_ID", dataId);
		ServDao.deletes(linkServId, whereBean);
	 }
    }
    
    /**
     * 删除系统字段
     * @param bean
     * @return
     */
    public Bean delSysCol(Bean bean){
	bean.remove("S_USER");
	bean.remove("S_DEPT");
	bean.remove("S_TDEPT");
	bean.remove("S_ODEPT");
	bean.remove("S_ATIME");
	bean.remove("S_MTIME");
	return bean;
    }
    public OutBean exp(ParamBean paramBean) {
   	String servId = paramBean.getServId();
   	ServDefBean serv = ServUtils.getServDef(servId);
   	long count = 0;
   	long times = 0;
   	paramBean.setQueryPageShowNum(ONETIME_EXP_NUM); // 设置每页最大导出数据量
   	beforeExp(paramBean); // 执行监听方法
   	if (paramBean.getId().length() > 0) { // 支持指定记录的导出（支持多选）
   	    String searchWhere = " and " + serv.getPKey() + " in ('" + paramBean.getId().replaceAll(",", "','") + "')";
   	    paramBean.setQuerySearchWhere(searchWhere);
   	}
   	ExportExcel expExcel = new ExportExcel(serv);
   	try {
   	    OutBean outBean = queryExp(paramBean);
   	    count = outBean.getCount();
   	    // 导出第一次查询数据
   	    paramBean.setQueryPageNowPage(1); // 导出当前第几页
   	    afterExp(paramBean, outBean); // 执行导出查询后扩展方法
   	    LinkedHashMap<String, Bean> cols = outBean.getCols();
   	    cols.remove("BUTTONS");
   	    expExcel.createHeader(cols);
   	    expExcel.appendData(outBean.getDataList(), paramBean);

   	    // 存在多页数据
   	    if (ONETIME_EXP_NUM < count) {
   		times = count / ONETIME_EXP_NUM;
   		// 如果获取的是整页数据
   		if (ONETIME_EXP_NUM * times == count && count != 0) {
   		    times = times - 1;
   		}
   		for (int i = 1; i <= times; i++) {
   		    paramBean.setQueryPageNowPage(i + 1); // 导出当前第几页
   		    OutBean out = query(paramBean);
   		    afterExp(paramBean, out); // 执行导出查询后扩展方法
   		    expExcel.appendData(out.getDataList(), paramBean);
   		}
   	    }
   	    expExcel.addSumRow();
   	} catch (Exception e) {
   	    log.error("导出Excel文件异常" + e.getMessage(), e);
   	} finally {
   	    expExcel.close();
   	}
   	return new OutBean().setOk();
       }
}
