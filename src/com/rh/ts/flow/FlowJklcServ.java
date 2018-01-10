package com.rh.ts.flow;

import com.rh.core.base.Bean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ExportExcel;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.ImpUtils;
import com.rh.ts.util.TsConstant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FlowJklcServ extends CommonServ {
	//导出
	 private static final int ONETIME_EXP_NUM = 20000;
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

	
	
	
	
    /**
     * 导入方法开始的入口
     */
    public OutBean saveFromExcel(ParamBean paramBean) {
        String fileId = paramBean.getStr("FILE_ID");
        //保存方法入口
        paramBean.set(ImpUtils.SERV_METHOD_NAME, "impDataSave");
        OutBean out =ImpUtils.getDataFromXls(fileId,paramBean);
       // paramBean.set("SERVMETHOD", "impDataSave");
       // OutBean out =ImpUtils.getDataFromXls(fileId,paramBean);
        
        String failnum = out.getStr("failernum");
        String successnum = out.getStr("oknum");
        //返回导入结果
        return new OutBean().set("FILE_ID",out.getStr("fileid")).set("_MSG_", "导入成功："+successnum+"条,导入失败："+failnum+"条");
    }

    /**
     * 导入保存方法
     *
     * @param paramBean
     * @return
     */
    public OutBean impDataSave(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        Bean  userBean  =new  Bean();
        // 获取前端传递参数
        String nodeId = (String) paramBean.get("NODE_ID"); // 节点id

        //*获取文件内容
        List<Bean> rowBeanList = paramBean.getList(ImpUtils.DATA_LIST);
        List<String> codeList = new ArrayList<String>();// 避免重复添加数据

        // 获取流程id
        Bean bmGroupBean = ServDao.find(TsConstant.SERV_WFS_NODE_APPLY, nodeId);
        String wfsId = bmGroupBean.getStr("WFS_ID");
        List<Bean> beans = new ArrayList<Bean>();
        for (int index = 0; index < rowBeanList.size(); index++) {
            Bean rowBean = rowBeanList.get(index);
            String colCode = rowBean.getStr(ImpUtils.COL_NAME + "1");//审核人力资源编码
            String shrName = rowBean.getStr(ImpUtils.COL_NAME + "2");//审核人
            String shGroup = rowBean.getStr(ImpUtils.COL_NAME + "3");//审核群组
            String levleDept = rowBean.getStr(ImpUtils.COL_NAME + "4");//所属机构层级
            String predeFineDept = rowBean.getStr(ImpUtils.COL_NAME + "5");//预定义部门
            String customDept = rowBean.getStr(ImpUtils.COL_NAME + "6");//自定义部门
            String shrJob = rowBean.getStr(ImpUtils.COL_NAME + "7");//审核人职位
            //Bean userBean = ImpUtils.getUserBeanByString(colCode);
            
            if(colCode != null && colCode.length() != 0){
              userBean=UserMgr.getUser(colCode);//获取人员信息
//            if (userBean == null) {
//                rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
//                continue;
            }  
            String code="";
            if(userBean !=null){
             code = userBean.getStr("USER_CODE");
             //String name = userBean.getStr("USER_NAME"),
               //     userDeptCode = userBean.getStr("DEPT_CODE");
            if (codeList.contains(code)) {
                // 已包含 continue ：避免重复添加数据
                rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
                continue;
            }
            }
            Bean bean = new Bean();
            bean.set("NODE_ID", nodeId);
            bean.set("WFS_ID", wfsId);
            //人
            bean.set("SHR_USERCODE", colCode);// 审核人资源编码
            bean.set("QJKLC_SHR", shrName);// 审核人
            if(colCode != null && colCode.length() != 0) { 
            	bean.set("QJKLC_SEL", 1);
            }
            
            //部门预定义
            bean.set("QJKLC_YDDEPT", predeFineDept);//预定义部门
            if(predeFineDept != null && predeFineDept.length() != 0) { 
            	bean.set("QJKLC_SEL", 0);
            	bean.set("QJKLC_SEL_DEPT", 0);
            }
            bean.set("QJKLC_SHZW", shrJob);//审核人职位
            if(shrJob != null && shrJob.length() != 0) { 
            String[] shrJobArray = shrJob.split(",");
            String shzwCodes = "";
            for(int i=0;i<shrJobArray.length;i++){
            	 String shrJobName=shrJobArray[i];
            	 String where = "AND DUTY_LV_NAME='" + shrJobName + "'";
                 List<Bean> shrJobBean = ServDao.finds("TS_WFS_LEADER", where); 
                 if (shrJobBean != null && !shrJobBean.isEmpty()) {
                	Bean shrTing= shrJobBean.get(0);
                	shzwCodes += shrTing.getStr("DUTY_LV");
                	shzwCodes += ",";
                 }
            }	
            bean.set("QJKLC_SHZW_CODE", shzwCodes.substring(0, shzwCodes.length() - 1));
            }
           //部门自定义
            bean.set("QJKLC_ZDDEPT", customDept);//自定义部门
            if(customDept != null && customDept.length() != 0) { 
            	bean.set("QJKLC_SEL", 0);
            	bean.set("QJKLC_SEL_DEPT", 1);
           	    String where = "AND DEPT_NAME='" + customDept + "'";
                List<Bean> shrOrgBean= ServDao.finds("SY_ORG_DEPT", where); 
                if (shrOrgBean != null && !shrOrgBean.isEmpty()) {
                	Bean shrOrgTing= shrOrgBean.get(0);
                	String shrOrgCodes= shrOrgTing.getStr("DEPT_CODE");
                	bean.set("DEPT_CODE", shrOrgCodes);
                }
            }
            //群组
            /**bean.set("QJKLC_SHQZ", shGroup);//审核群组
        	bean.set("QJKLC_QZDEPT_CODE", levleDept);//审核所属机构
        	  if(shGroup != null && shGroup.length() != 0) { 
        		  bean.set("QJKLC_SEL", 1);//选择方式
        		  String where = "AND DEPT_NAME='" + customDept + "'";
              List<Bean> shrGroupBean= ServDao.finds("TS_PVLG_GROUP", where);
              if (shrGroupBean != null && !shrGroupBean.isEmpty()) {
              	Bean shrOrgTing= shrGroupBean.get(0);
              	String shrOrgCodes= shrOrgTing.getStr("G_ID");
              	bean.set("QJKLC_SHQZ_CODE", shrOrgCodes);
              }
        	  }
        	  if(levleDept.equals(2)) { 
        		  bean.set("QJKLC_ZDDEPT_COLCODE", "JK_YJFH"); //一级机构
        	  }*/
            // 先查询避免重复添加col3=总行/广东分行营业部,总行/福建分行,
          /** bean.set("BMSHLC_SHR", name);
            String[] colDeptCode = colDeptCodes.split(",");
            String deptcode = "";
            for (int i = 0; i < colDeptCode.length; i++) {
                String getDept = colDeptCode[i];
                String[] colDeptNAME = getDept.split("/");
                String deptName = colDeptNAME[1];// 名称
                String where = "AND DEPT_NAME='" + deptName + "'";
                List<Bean> deptBean = ServDao.finds("TS_ORG_DEPT", where);
                if (deptBean != null && !deptBean.isEmpty()) {
                    Bean deptCodeBean = deptBean.get(0);

                    deptcode += deptCodeBean.getStr("DEPT_CODE");
                    deptcode += ",";
                } else {
                    rowBean.set(ImpUtils.ERROR_NAME, "找不到审核机构名称对应的编码");
                    continue;
                }

            }
            bean.set("DEPT_CODE", deptcode.substring(0, deptcode.length() - 1));*/
            Bean  tempBean=new Bean();
            tempBean.set("SHR_USERCODE", colCode);
            if(colCode != null && colCode.length() != 0) {
            if (ServDao.count(TsConstant.SERV_WFS_QJKLC, tempBean) <= 0) {
            
                beans.add(bean);
                if(userBean !=null){
                	 codeList.add(code);
                }
            } else {
            	 if(userBean !=null){
            		 rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
            	 }
            }
        }
    
    }
        ServDao.creates(TsConstant.SERV_WFS_QJKLC, beans);

        return outBean.set(ImpUtils.ALL_LIST, rowBeanList).set("successlist", codeList);

    }

}
