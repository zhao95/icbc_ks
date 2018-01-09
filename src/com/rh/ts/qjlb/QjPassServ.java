package com.rh.ts.qjlb;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.PageBean;
import com.rh.core.serv.util.ExportExcel;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.ExpUtils;
import com.rh.core.util.ImpUtils;
import com.rh.ts.util.BMUtil;
import com.rh.ts.util.TsConstant;

public class QjPassServ extends CommonServ {
	 /**
     * 提供导出ExcelexpAll
     *
     * @param paramBean 参数信息
     * @return 执行结果
     */
    public OutBean expAll(ParamBean paramBean) {
        /*获取beanList信息*/
        //*设置查询条件
        //paramBean.set("isArrange", "false");//获取所有安排和未安排的考生
        paramBean.set(ParamBean.QUERY_NOPAGE_FLAG, "true");
      
        List<Bean> allList = this.getKsQjContent(paramBean);
       
        /*设置导出展示信息*/
        LinkedHashMap<String, String> colMap = new LinkedHashMap<String, String>();
        colMap.put("USER_CODE", "人力资源编码");
        colMap.put("QJ_NAME", "姓名");
        colMap.put("QJKS_NAME", "考试名称");
        colMap.put("QJ_TITLE", "请假名称");
        colMap.put("QJ_REASON", "请假事由");
        

        return ExpUtils.expUtil(allList, colMap, paramBean);
    }

    /**
     * 根据条件 获取请假信息
     *
     * @param paramBean paramBean searchKcId searchSjId searchDeptCode
     * @return outBean
     */
    public List<Bean> getKsQjContent(ParamBean paramBean) {
//        /*分页参数处理*/
//        PageBean page = paramBean.getQueryPage();
//        int rowCount = paramBean.getShowNum(); //通用分页参数优先级最高，然后是查询的分页参数
//        if (rowCount > 0) { //快捷参数指定的分页信息，与finds方法兼容
//            page.setShowNum(rowCount); //从参数中获取需要取多少条记录，如果没有则取所有记录
//            page.setNowPage(paramBean.getNowPage());  //从参数中获取第几页，缺省为第1页
//        } else {
//            if (!page.contains(Constant.PAGE_SHOWNUM)) { //初始化每页记录数设定
//                if (paramBean.getQueryNoPageFlag()) { //设定了不分页参数
//                    page.setShowNum(0);
//                } else { //没有设定不分页，取服务设定的每页记录数
//                    page.setShowNum(50);
//                }
//            }
//        }
    	
    	List<Bean> allQjList = new ArrayList<Bean>();
        /*拼sql并查询*/
        String xmId=  paramBean.getStr("XM_ID");
        String where="and   XM_ID='"+xmId+"'  and QJ_STATUS="+2;
        //	请假的数据
        List<Bean>  list=ServDao.finds("TS_XMGL_QJPASS", where);
        if(list != null && list.size() != 0){
        	for(Bean bean :list){
        		Bean qjBean=new Bean();
        		String typeName="";
                String tsName="";
        		String userCode=bean.getStr("USER_CODE");
        		String qjName=bean.getStr("QJ_NAME");
        		String qjReason=bean.getStr("QJ_REASON");
        		String qjTitle=bean.getStr("QJ_TITLE");
        		String qjKsName=bean.getStr("QJ_KSNAME");
        		String[] qjKsNameArry=qjKsName.split(",");
        		for(int i=0;i<qjKsNameArry.length;i++){
        			String bmId=qjKsNameArry[i];
        		    String bmidWhere="and   BM_ID='"+bmId+"'";
        		    List<Bean>  bmidBean=ServDao.finds("TS_BMSH_PASS", bmidWhere);
        		    
        		    if(bmidBean !=null   &&  bmidBean.size() !=0){
        		    		tsName=(String) bmidBean.get(0).get("BM_LB")+bmidBean.get(0).get("BM_XL")+bmidBean.get(0).get("BM_MK")+bmidBean.get(0).get("BM_TYPE_NAME")+",";
        		    }
        		     typeName += tsName;
        		}
        		
        		qjBean.set("USER_CODE", userCode);
        		qjBean.set("QJ_NAME", qjName);
        		qjBean.set("QJ_REASON", qjReason);
        		qjBean.set("QJKS_NAME", typeName);
        		qjBean.set("QJ_TITLE", qjTitle);
        		allQjList.add(qjBean);
        	}
        }
        
        return  allQjList;
    }
    

/**
	 * @param paramBean
	 *            paramBean G_ID FILE_ID
	 * @return outBean
	 */
   /**
    * 导入方法开始的入口
    */
   public OutBean saveFromExcel(ParamBean paramBean) {
       String fileId = paramBean.getStr("FILE_ID");
//       //保存方法入口
//       paramBean.set(ImpUtils.SERV_METHOD_NAME, "impDataSave");
//       String finalfileid = ImpUtils.getDataFromXls(fileId, paramBean);
//       return new OutBean().set("FILE_ID", finalfileid);
      // String fileId = paramBean.getStr("FILE_ID");
  	 //方法入口
  	paramBean.set("SERVMETHOD", "savedata");
     OutBean out =ImpUtils.getDataFromXls(fileId,paramBean);
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
   public OutBean savedata(ParamBean paramBean) {
	   Date day=new Date();    
	   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	   String nowTime=df.format(day);
       OutBean outBean = new OutBean();
       //获取项目id
       String xmId=paramBean.getStr("XM_ID");
       Bean xmBean=ServDao.find("TS_XMGL", xmId);
       String xmName=xmBean.getStr("XM_NAME");
       // 获取前端传递参数
       //*获取文件内容
       List<Bean> rowBeanList = paramBean.getList("datalist");
      // List<Bean> rowBeanList = paramBean.getList(ImpUtils.DATA_LIST);
       List<String> codeList = new ArrayList<String>();// 避免重复添加数据
       List<Bean> beans = new ArrayList<Bean>();
       for (int j = 0; j < rowBeanList.size(); j++) {
           Bean rowBean = rowBeanList.get(j);
         
           String colCode = rowBean.getStr(ImpUtils.COL_NAME + "1");//人力资源编码
           //if("".equals(colCode) ){
        	if(colCode != null && colCode.length() != 0) { 
			//String qjTitle = rowBean.getStr(ImpUtils.COL_NAME + "3");//请假主题
			//String qjTime = rowBean.getStr(ImpUtils.COL_NAME + "3");//请假时间
			String qjReason = rowBean.getStr(ImpUtils.COL_NAME + "3");//请假事由
			String qjTsNames = rowBean.getStr(ImpUtils.COL_NAME + "4");//考试请假
			Bean  userBean=UserMgr.getUser(colCode);//获取人员信息
			if(userBean == null){
				rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
              continue;
			}
//           Bean userBean = ImpUtils.getUserBeanByString(colCode);
//           if (userBean == null) {
//               rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
//               continue;
//           }

           String code = userBean.getStr("USER_CODE"), name = userBean.getStr("USER_NAME"),
        		   tdepeCode = userBean.getStr("TDEPT_CODE"),
                   userDeptCode = userBean.getStr("DEPT_NAME");
           if (codeList.contains(code)) {
               // 已包含 continue ：避免重复添加数据
               rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
               continue;
           }

           Bean bean = new Bean();
           bean.set("USER_CODE", colCode);//人力资源编码
           bean.set("QJ_STATUS", 2);// 请假通过的状态
           bean.set("XM_ID", xmId);// XMID
           if (ServDao.count(TsConstant.SERV_XMGL_QJPASS, bean) <= 0) {
            bean.set("QJ_NAME", name);//姓名
   			bean.set("QJ_TITLE", xmName);//主题
   			bean.set("QJ_REASON", qjReason);//请假原因
   			bean.set("QJ_DATE", nowTime);//请假时间
   			bean.set("QJ_DANWEI", userDeptCode);//请假单位
   			bean.set("S_TDEPT", tdepeCode);
   			String ksName="";
   			String lbxlmk="";
   			//查询bmId
   			String  where="AND XM_ID='"+xmId+"' AND BM_CODE=' "+colCode+"' AND BM_TYPE="+2;
   			List<Bean> qjList=ServDao.finds("TS_BMLB_BM", where);
   			if(qjList !=null  && qjList.size() !=0){
   				for(int i=0;i<qjList.size();i++){
   					Bean qjBean=qjList.get(i);
   					lbxlmk=qjBean.getStr("BM_LB")+qjBean.getStr("BM_XL")+qjBean.getStr("BM_MK")+qjBean.getStr("BM_TYPE_NAME");
   					//类别+序列+模块+等级
   					if(qjTsNames.indexOf(lbxlmk)!=-1){
   						ksName=qjBean.getStr("BM_ID");
   	   					ksName+=",";
   					}
   					
   					
   				}
   			}
   			//bean.set("JKGL_START_DATE", jkStartTime);// 禁考开始时间
   			//bean.set("JKGL_END_DATE", jkEndTime);// 禁考结束时间
   			
   		  
              // bean.set("G_TYPE", 1);//选取类型 1人员
               // 先查询避免重复添加col3=总行/广东分行营业部,总行/福建分行,
//               bean.set("BMSHLC_SHR", name);
//               String[] colDeptCode = colDeptCodes.split(",");
//               String deptcode = "";
//               for (int i = 0; i < colDeptCode.length; i++) {
//                   String getDept = colDeptCode[i];
//                   String[] colDeptNAME = getDept.split("/");
//                   String deptName = colDeptNAME[1];// 名称
//                   String where = "AND DEPT_NAME='" + deptName + "'";
//                   List<Bean> deptBean = ServDao.finds("TS_ORG_DEPT", where);
//                   if (deptBean != null && !deptBean.isEmpty()) {
//                       Bean deptCodeBean = deptBean.get(0);
//
//                       deptcode += deptCodeBean.getStr("DEPT_CODE");
//                       deptcode += ",";
//                   } else {
//                       rowBean.set(ImpUtils.ERROR_NAME, "找不到审核机构名称对应的编码");
//                       continue;
//                   }
//
//               }
   			if("".equals(ksName)){
   			 bean.set("QJ_KSNAME", null);
   			}else{
   			 bean.set("QJ_KSNAME", ksName.substring(0, ksName.length() - 1));
   			}
              
               beans.add(bean);
               codeList.add(code);
           } else {
               rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
           }
       }
       }
       ServDao.creates(TsConstant.SERV_XMGL_QJPASS, beans);

     //  return outBean.set(ImpUtils.ALL_LIST, rowBeanList).set("successlist", codeList);
       return outBean.set("alllist", rowBeanList).set("successlist", codeList);
 
   }


}