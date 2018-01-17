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
import com.rh.core.util.Constant;
import com.rh.core.util.ImpUtils;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.RoleBean;
import com.rh.ts.pvlg.mgr.RoleMgr;
import com.rh.ts.util.RoleUtil;
import com.rh.ts.util.TsConstant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class FlowNodeApplyAdminerServ extends CommonServ {

    /**
     * 导入方法开始的入口
     */
    public OutBean saveFromExcel(ParamBean paramBean) {
        String fileId = paramBean.getStr("FILE_ID");
        //保存方法入口
        paramBean.set(ImpUtils.SERV_METHOD_NAME, "impDataSave");
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
    public OutBean impDataSave(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        // 获取前端传递参数
        String nodeId = (String) paramBean.get("NODE_ID"); // 节点id

        //*获取文件内容
        List<Bean> rowBeanList = paramBean.getList(ImpUtils.DATA_LIST);
        List<String> codeList = new ArrayList<String>();// 避免重复添加数据
        List<Bean> beans = new ArrayList<Bean>();
        for (int index = 0; index < rowBeanList.size(); index++) {
            Bean rowBean = rowBeanList.get(index);
            String colCode = rowBean.getStr(ImpUtils.COL_NAME + "1");
            if(colCode != null && colCode.length() != 0) { 
            Bean  userBean=UserMgr.getUser(colCode);//获取人员信息
           // Bean userBean = ImpUtils.getUserBeanByString(colCode);
            if (userBean == null) {
                rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
                continue;
            }

            String code = userBean.getStr("USER_CODE");
            String name = userBean.getStr("USER_NAME");
            if (codeList.contains(code)) {
                // 已包含 continue ：避免重复添加数据
                rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
                continue;
            }
            Bean bean = new Bean();
            bean.set("NODE_ID", nodeId);
            bean.set("ADMINER_UWERCODE", code);// 审核人资源编码
            if (ServDao.count(TsConstant.SERV_WFS_NODEAPPLY_ADMINER, bean) <= 0) {
            	 bean.set("ADMINER_NAME", name);
                beans.add(bean);
                codeList.add(code);
            } else {
                rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
            }
            }
        }
        ServDao.creates(TsConstant.SERV_WFS_NODEAPPLY_ADMINER, beans);

        return outBean.set(ImpUtils.ALL_LIST, rowBeanList).set("successlist", codeList);

    }
    
    
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
    //
    public Bean findShDate(ParamBean  ParanBean){
    	int  tmp=6;
    	Bean  countDate   =new Bean();
    	String  userCode=ParanBean.getStr("USER_CODE");//登陆人
    	String  wfsId=ParanBean.getStr("WFS_ID");//节点
    	String  where="and  SHR_USERCODE='"+userCode+"' and  WFS_ID='"+wfsId+"' and BMSHLC_YESNO="+1;
    	List<Bean> nodeNumBean=ServDao.finds("TS_WFS_BMSHLC", where);
    	if(nodeNumBean !=null  &&  !nodeNumBean.isEmpty() ){
    		for(int i=0;i<nodeNumBean.size();i++){
    			String nodeId=nodeNumBean.get(i).getStr("NODE_ID");
    			String nodeWhere="AND NODE_ID='"+nodeId+"'";
    			List<Bean> nodeApplyBean=ServDao.finds("TS_WFS_NODE_APPLY", nodeWhere);
    			int nodeSteps=nodeApplyBean.get(0).getInt("NODE_STEPS");//级数
    			if(tmp>nodeSteps){
    				tmp=nodeSteps;
    			}
    		}
    	}
    	
    	countDate.set("TMP", tmp);//返回6表示没有人
    	return countDate;
    }
    
    
    
    //比较登陆人是否在审核管理员中
//    public Bean findAdminerDate(ParamBean  ParanBean){
//    	Bean  countDate   =new Bean();
//    	String  userCode=ParanBean.getStr("USER_CODE");//登陆人
//    	String  nodeId=ParanBean.getStr("NODE_ID");//节点
//    	String  where="and  NODE_ID='"+nodeId+"'";
//    	List<Bean> nodeNumBean=ServDao.finds("TS_WFS_NODE_APPLY", where);
//    	String nodeStept=nodeNumBean.get(0).getStr("NODE_STEPS");//级数
//    	
//    	Bean  bean=new Bean();
//    	bean.set("NODE_ID", nodeId);
//    	bean.set("SHR_USERCODE", userCode);
//    	bean.set("BMSHLC_YESNO", 1);
//    	int adminerCou=ServDao.count("TS_WFS_BMSHLC", bean);
//    	countDate.set("COUNT", adminerCou);
//    	countDate.set("NUM", nodeStept);
//    	return countDate;
//    }
//    
    
//    public  Bean getUser (ParamBean paramBean) throws JSONException {
//    	Bean  beanResult=new Bean();
//    	String  userCode=paramBean.getStr("USER_CODE");//登陆人
//    	//Bean bean=RoleMgr.getRoleOptsByUser(userCode,"TS_WFS_NODEAPPLY_ADMINER");
//    	if(!"admin".equals(userCode)){
//    		Bean bean=RoleUtil.getPvlgRole( userCode, "TS_WFS_NODEAPPLY_ADMINER");
//    		Bean extParams = bean.getBean("TS_WFS_NODEAPPLY_ADMINER_PVLG");
//    		//if (!extParams.isEmpty() && extParams != null) {
//    			JSONObject jsonObject = new JSONObject(extParams);
//    		//}
//    			String result = null;
//				Iterator iterator = jsonObject.keys();
//				String key;
//				while (iterator.hasNext()) {
//					key = (String) iterator.next();
//					try {
//						JSONObject object = (JSONObject) jsonObject.get(key);
//						String object2 = (String) object.get("ROLE_DCODE");
//						//String object2 = (String) object.get("ROLE_DCODE");
//						String[] object3 = object2.split(",");
//						if (result != null) {
//							for (int i = 0; i < object3.length; i++) {
//								if (result.indexOf(object3[i]) < 0) {
//									result += "," + object3[i];
//								}
//							}
//						} else {
//							result = object2;
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				
//				if (!Strings.isBlank(result)) {
//					// result 排序
//					beanResult.set("RESULT", result);
//					
//				}
//    	}
//    	return beanResult;
//    	
//    }
    
//    public void adminerSave(ParamBean  ParanBean){
//    	Bean  bean=new Bean();
//    	String userCode=ParanBean.getStr("ADMINER_UWERCODE");//获取人力资源编码
//    	String nodeId=ParanBean.getStr("NODE_ID");//获取nodeId
//    	String where =" and NODE_ID='"+nodeId+"'";
//    	List<Bean> applyBean=ServDao.finds("TS_WFS_NODE_APPLY", where);
//    	if(applyBean!=null && !applyBean.isEmpty()){
//    		
//    		String wfsId=applyBean.get(0).getStr("WFS_ID");//获取wfsid
//    	}
//    	 Bean  userBean=UserMgr.getUser(userCode);//获取人员信息
//    	 String deptCode = userBean.getStr("DEPT_CODE");//审核人所在机构
//    	 //查询用户所有权限 
//    	 Bean allOpt= RoleUtil.getPvlgRole(userCode);
//    	 
//    
//    	// 用户权限 所有权限的机构编码getroleoptsbyuser
// 		
//
// 		try {
// 			JSONObject jsonObject = new JSONObject(allOpt);
// 	    	
// 	    
// 				
// 				String result = null;
// 				Iterator iterator = jsonObject.keys();
// 				String key;
// 				while (iterator.hasNext()) {
// 					key = (String) iterator.next();
// 					try {
// 						JSONObject object = (JSONObject) jsonObject.get(key);
// 						String object2 = (String) object.get("ROLE_DCODE");
// 						String[] object3 = object2.split(",");
//// 						if (result != null) {
//// 							for (int i = 0; i < object3.length; i++) {
//// 								if (result.indexOf(object3[i]) < 0) {
//// 									result += "," + object3[i];
//// 								}
//// 							}
//// 						} else {
//// 							result = object2;
//// 						}
// 					} catch (JSONException e) {
// 						e.printStackTrace();
// 					}
// 				}
// 				/**if (!Strings.isBlank(result)) {
// 					// result 排序DEPT_PCODE
// 					String[] roles = result.split(",");
//
// 					StringBuilder param_where = new StringBuilder();
// 					param_where.append(" AND  EXISTS ( ");
// 					param_where.append(" SELECT DEPT_CODE FROM SY_ORG_DEPT  A ");
//
// 					param_where.append(" WHERE " + tableView + "."+deptPcode+" = A.DEPT_CODE ");
//
//   					param_where.append(" and INSTR (A.CODE_PATH," + "'" + roles[0] + "') ");
//
// 					param_where.append(") ");
// 					paramBean.set(Constant.PARAM_WHERE, param_where.toString());
// 				} else {
// 					// 无权限
// 					paramBean.set(Constant.PARAM_WHERE, " and 1=2 ");
// 				}*/
// 			
// 		} catch (Exception e) {
// 			// 无权限
// 			//paramBean.set(Constant.PARAM_WHERE, " and 1=2 ");
// 		}
// 	}
    	 

  

}
