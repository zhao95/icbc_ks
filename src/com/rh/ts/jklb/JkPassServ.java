package com.rh.ts.jklb;

import com.rh.core.base.Bean;
import com.rh.core.org.DeptBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.ExpUtils;
import com.rh.core.util.ImpUtils;
import com.rh.ts.qjlb.QjUtils;
import com.rh.ts.util.BMUtil;
import com.rh.ts.util.TsConstant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class JkPassServ extends CommonServ {
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

        List<Bean> allList = this.getKsJkContent(paramBean);
       
        /*设置导出展示信息*/
        LinkedHashMap<String, String> colMap = new LinkedHashMap<String, String>();

        colMap.put("JK_ID", "借考申请单编码");
        colMap.put("JK_TITLE", "项目名称");
        colMap.put("USER_CODE", "人力资源编码");
        colMap.put("JK_NAME", "姓名");
        colMap.put("KS_NAME", "考试名称");
        colMap.put("JK_YJFH_NAME", "借考一级分行");
        colMap.put("JK_JKCITY", "借考的城市");
        colMap.put("JK_REASON", "借考事由");
        colMap.put("JK_STATUS_NAME", "借考状态");

        return ExpUtils.expUtil(allList, colMap, paramBean);
    }

    /**
     * 根据条件 获取请假信息
     *
     * @param paramBean paramBean {XM_ID}
     * @return outBean
     */
    public List<Bean> getKsJkContent(ParamBean paramBean) {
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

        /*拼sql并查询*/
        String xmId = paramBean.getStr("XM_ID");
        SqlBean sqlBean = new SqlBean();
        sqlBean.and("XM_ID", xmId);
        //	请假的数据
        List<Bean> list = ServDao.finds(TsConstant.SERV_XMGL_JKPASS, sqlBean);
        if (list != null && list.size() != 0) {
            for (Bean bean : list) {
                String jkKsName = bean.getStr("JK_KSNAME");
                String[] shIdArray = jkKsName.split(",");
                StringBuilder ksName = new StringBuilder();
                for (String shId : shIdArray) {
                    Bean bmPassBean = ServDao.find(TsConstant.SERV_BM, shId);
                    if (bmPassBean == null) {
                        bmPassBean = ServDao.find(TsConstant.SERV_BMSH_PASS, shId);
                    }
                    String bmTitle = bmPassBean.getStr("BM_TITLE");
//                    String bmLb = bmPassBean.getStr("BM_LB");
                    String bmXl = bmPassBean.getStr("BM_XL");
                    String bmMk = bmPassBean.getStr("BM_MK");
                    String bmType = bmPassBean.getStr("BM_TYPE");
                    String examinationName = BMUtil.getExaminationName(bmType, bmXl, bmMk, bmTitle);
                    ksName.append(",").append(examinationName);
                }
                if (ksName.length() > 0) {
                    ksName = new StringBuilder(ksName.substring(1));
                }
                bean.set("KS_NAME", ksName.toString());
                bean.set("JK_STATUS_NAME", QjUtils.getQjStatusName(bean.getStr("JK_STATUS")));
                DeptBean jkYjfh = OrgMgr.getDept(bean.getStr("JK_YJFH"));
                String jkYjfhName = "";
                if (jkYjfh != null) {
                    jkYjfhName = jkYjfh.getName();
                }
                bean.set("JK_YJFH_NAME", jkYjfhName);

            }
        }

        return list;
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
        OutBean out = ImpUtils.getDataFromXls(fileId, paramBean);
        String failnum = out.getStr("failernum");
        String successnum = out.getStr("oknum");
        //返回导入结果
        return new OutBean().set("FILE_ID", out.getStr("fileid")).set("_MSG_", "导入成功：" + successnum + "条,导入失败：" + failnum + "条");
    }


    /**
     * 导入保存方法
     *
     * @param paramBean
     * @return
     */
    public OutBean savedata(ParamBean paramBean) {
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(day);
        OutBean outBean = new OutBean();
        //获取项目id
        String xmId = paramBean.getStr("XM_ID");
        //String  xmName="XM_ID='"+xmId+"'";
        Bean xmBean = ServDao.find("TS_XMGL", xmId);
        String xmName = xmBean.getStr("XM_NAME");
        // 获取前端传递参数
        //*获取文件内容
        List<Bean> rowBeanList = paramBean.getList("datalist");
        // List<Bean> rowBeanList = paramBean.getList(ImpUtils.DATA_LIST);
        List<String> codeList = new ArrayList<String>();// 避免重复添加数据
        List<Bean> beans = new ArrayList<Bean>();
        for (int j = 0; j < rowBeanList.size(); j++) {
            Bean rowBean = rowBeanList.get(j);
            String colCode = rowBean.getStr(ImpUtils.COL_NAME + "1");//人力资源编码
            if (colCode != null && colCode.length() != 0) {
                String jkYifh = rowBean.getStr(ImpUtils.COL_NAME + "3");//借考一级分行
                String jkCity = rowBean.getStr(ImpUtils.COL_NAME + "4");//希望借考的城市
                String jkReason = rowBean.getStr(ImpUtils.COL_NAME + "5");//借考事由
                //String jkTime = rowBean.getStr(ImpUtils.COL_NAME + "6");//借考时间
                String jkTsNames = rowBean.getStr(ImpUtils.COL_NAME + "6");//考试借考
                Bean userBean = UserMgr.getUser(colCode);//获取人员信息
                if (userBean == null) {
                    rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
                    continue;
                }

                String code = userBean.getStr("USER_CODE"), name = userBean.getStr("USER_NAME"),
                        userDeptCode = userBean.getStr("DEPT_NAME");
                userDeptCode = userBean.getStr("DEPT_CODE");
                if (codeList.contains(code)) {
                    // 已包含 continue ：避免重复添加数据
                    rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
                    continue;
                }

                Bean bean = new Bean();

                bean.set("USER_CODE", code);// 人资源编码
                bean.set("JK_STATUS", 2);// 状态通过
                bean.set("XM_ID", xmId);// xmid

                // bean.set("G_TYPE", 1);//选取类型 1人员
                if (ServDao.count(TsConstant.SERV_XMGL_JKPASS, bean) <= 0) {
                    bean.set("JK_NAME", name);
                    bean.set("JK_TITLE", xmName);//借考标题
                    bean.set("JK_YJFH", jkYifh);// 借考一级分行
                    bean.set("JK_DEPT", userDeptCode);// 借考人所在的部门
                    bean.set("JK_JKCITY", jkCity);//希望借考的城市
                    bean.set("JK_REASON", jkReason);//借考原因
                    bean.set("JK_DATE", nowTime);//借考时间
                    String ksName = "";
                    String lbxlmk = "";
                    //查询bmId
                    String where = "AND XM_ID='" + xmId + "' AND BM_CODE=' " + colCode + "' AND BM_TYPE=" + 2;
                    List<Bean> qjList = ServDao.finds("TS_BMLB_BM", where);
                    if (qjList != null && qjList.size() != 0) {
                        for (int i = 0; i < qjList.size(); i++) {
                            Bean qjBean = qjList.get(i);
                            lbxlmk = qjBean.getStr("BM_LB") + qjBean.getStr("BM_XL") + qjBean.getStr("BM_MK") + qjBean.getStr("BM_TYPE_NAME");
                            //类别+序列+模块+等级
                            if (jkTsNames.indexOf(lbxlmk) != -1) {
                                ksName = qjBean.getStr("BM_ID");
                                ksName += ",";
                            }
                        }
                    }
                    if ("".equals(ksName)) {
                        bean.set("JK_KSNAME", null);
                    } else {
                        bean.set("JK_KSNAME", ksName.substring(0, ksName.length() - 1));
                    }

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
                    //bean.set("DEPT_CODE", deptcode.substring(0, deptcode.length() - 1));
                    beans.add(bean);
                    codeList.add(code);
                } else {
                    rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
                }
            }
        }
        ServDao.creates(TsConstant.SERV_XMGL_JKPASS, beans);

        //  return outBean.set(ImpUtils.ALL_LIST, rowBeanList).set("successlist", codeList);
        return outBean.set("alllist", rowBeanList).set("successlist", codeList);

    }


}