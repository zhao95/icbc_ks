package com.rh.ts.admit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.ImpUtils;

public class AdmitServ extends CommonServ {

	 /**
     * 导入方法开始的入口
     */
    public OutBean saveFromExcel(ParamBean paramBean){
    	String fileId = paramBean.getStr("FILE_ID");
    	 //方法入口
    	paramBean.set("SERVMETHOD", "savedata");
    	OutBean out =ImpUtils.getDataFromXls(fileId,paramBean);
    	String failnum = out.getStr("failernum");
    	String successnum = out.getStr("oknum");
    	//返回导入结果
    	OutBean outBean = new OutBean();
    	outBean.set("FILE_ID", out.getStr("fileid"));
    	outBean.set("mess", "导入成功："+successnum+"条,导入失败："+failnum+"条");
    	outBean.setOk();
    	return outBean;
       //return new OutBean().set("FILE_ID",out.getStr("fileid")).set("_MSG_", "导入成功："+successnum+"条,导入失败："+failnum+"条");
    }
    
    /**
     * @param paramBean paramBean G_ID FILE_ID
     * @return outBean
     */
    public OutBean savedata(ParamBean paramBean){
    	String XM_Id = paramBean.getStr("XM_ID");
        OutBean outBean = new OutBean();
        //获取前端传递参数
        //*获取文件内容
       // Date date = new Date();
       // SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 获取当前时间
        List<Bean> rowBeanList = paramBean.getList("datalist");
        List<String> codeList = new ArrayList<String>();//避免重复添加数据

        List<Bean> beans = new ArrayList<Bean>();
        for (Bean rowBean : rowBeanList) {
            String colCode = rowBean.getStr(ImpUtils.COL_NAME + "1");
           // Bean userBean = ImpUtils.getUserBeanByString(colCode);
            Bean  userBean=UserMgr.getUser(colCode);//获取人员信息
            if (userBean == null) {
                rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
                continue;
            }

            String code = userBean.getStr("USER_CODE"),
                    name = userBean.getStr("USER_NAME");
            Bean bean = new Bean();
            bean.set("USER_CODE", code);
            String AD_LB = rowBean.getStr(ImpUtils.COL_NAME + "2");
            String adlbWhere="AND KSLBK_NAME='"+AD_LB+"'";
           List<Bean> lblist= ServDao.finds("TS_XMGL_BM_KSLBK", adlbWhere);
           if(lblist != null && !lblist.isEmpty()){
        	  String lb=lblist.get(0).getStr("KSLBK_CODE");
            bean.set("AD_LB", lb);
            if("".equals(AD_LB)){
            	rowBean.set(ImpUtils.ERROR_NAME, "岗位类别为空");
            	continue;
            }
           }
            String AD_XL = rowBean.getStr(ImpUtils.COL_NAME + "3");
            String adxlWhere="AND KSLBK_XL='"+AD_XL+"'";
            List<Bean> xllist= ServDao.finds("TS_XMGL_BM_KSLBK", adxlWhere);
            if(xllist !=null && !xllist.isEmpty()){
            	String xl=xllist.get(0).getStr("KSLBK_XL_CODE");
            bean.set("AD_XL", xl);
            if("".equals(AD_XL)){
            	rowBean.set(ImpUtils.ERROR_NAME, "岗位序列为空");
            	continue;
            }
            }
            String AD_MK = rowBean.getStr(ImpUtils.COL_NAME + "4");
            String admlWhere="AND KSLBK_MK='"+AD_MK+"'";
            List<Bean> mklist= ServDao.finds("TS_XMGL_BM_KSLBK", admlWhere);
            if(mklist !=null && !mklist.isEmpty()){
            	String mk=mklist.get(0).getStr("KSLBK_MKCODE");
            bean.set("AD_MK", mk);
         
            }
            String AD_TYPE = rowBean.getStr(ImpUtils.COL_NAME + "5");
            String adtypeWhere="AND KSLBK_TYPE_NAME='"+AD_TYPE+"'";
            List<Bean> adtypelist= ServDao.finds("TS_XMGL_BM_KSLBK", adtypeWhere);
            if(adtypelist !=null && !adtypelist.isEmpty()){
            	String TYPE=mklist.get(0).getStr("KSLBK_TYPE");
            	bean.set("AD_TYPE", TYPE);
            }
            
//            if("".equals(AD_TYPE)){
//            	rowBean.set(ImpUtils.ERROR_NAME, "岗位等级为空");
//            	continue;
//            }
            String AD_GRADE = rowBean.getStr(ImpUtils.COL_NAME + "6");
            bean.set("AD_GRADE", AD_GRADE);
            if("".equals(AD_GRADE)){
            	rowBean.set(ImpUtils.ERROR_NAME, "分数为空");
            	continue;
            }
            bean.set("XM_ID", XM_Id);
            if (ServDao.count("ts_bmsh_admit", bean) <= 0) {
                //先查询避免重复添加
                bean.set("USER_NAME", name);
                //bean.set("AD_TIME", simp.format(date));操作失败
                //bean.set("XM_ID", XM_Id);
                beans.add(bean);
                codeList.add(code);
            } else {
                rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
            }
        }
        ServDao.creates("ts_bmsh_admit", beans);
        
        return outBean.set("alllist", rowBeanList).set("successlist", codeList).setOk();
        //在excel中设置失败信息
    }
}
