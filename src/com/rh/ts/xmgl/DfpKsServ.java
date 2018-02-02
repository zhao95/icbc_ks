package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.DateUtils;
import com.rh.core.util.ImpUtils;
import com.rh.ts.pvlg.PvlgUtils;
import com.rh.ts.util.KcUtils;
import com.rh.ts.util.TsConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DfpKsServ extends CommonServ {

    @Override
    protected void beforeSave(ParamBean paramBean) {
        String userCode = paramBean.getStr("BM_CODE");
        UserBean userBean = UserMgr.getUser(userCode);
        paramBean.set("S_DEPT", userBean.getDeptCode());
        paramBean.set("S_TDEPT", userBean.getTDeptCode());
        paramBean.set("S_ODEPT", userBean.getODeptCode());//机构编码 导入后所属机构
        paramBean.set("ODEPT_CODE", userBean.getODeptCode());
        super.beforeSave(paramBean);
    }

    // 查询前添加查询条件
    protected void beforeQuery(ParamBean paramBean) {
        ParamBean param = new ParamBean();
        param.set("paramBean", paramBean);
        param.set("serviceName", paramBean.getServId());
        param.set("fieldName", "S_ODEPT");

        paramBean.setWhere("and BM_STATUS not in('1','3')");

        PvlgUtils.setOrgPvlgWhere(param);
    }

    /**
     * 导入方法开始的入口
     */
    public OutBean saveFromExcel(ParamBean paramBean) {

        String fileId = paramBean.getStr("FILE_ID");
        //方法入口
        if (paramBean.getStr("OPT_TYPE").equals("JK")) {
            paramBean.set("SERVMETHOD", "impDataSaveToJk");
        } else {
            paramBean.set("SERVMETHOD", "impDataSave");
        }
        OutBean out = ImpUtils.getDataFromXls(fileId, paramBean);
        String failNum = out.getStr("failernum");
        String successNum = out.getStr("oknum");
        //返回导入结果
        return new OutBean().set("FILE_ID", out.getStr("fileid")).setOk("导入成功：" + successNum + "条,导入失败：" + failNum + "条");
    }


    public OutBean impDataSaveToJk(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        // 获取前端传递参数
        String xmId = paramBean.getStr("XM_ID"); // xmId

        //*获取文件内容
        List<Bean> rowBeanList = paramBean.getList(ImpUtils.DATA_LIST);
        List<Bean> beans = new ArrayList<Bean>();

        Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
        if (xmBean == null) {
            for (Bean rowBean : rowBeanList) {
                rowBean.set(ImpUtils.ERROR_NAME, "程序错误，找不到项目");
            }

        } else {
            String xmType = xmBean.getStr("XM_TYPE");

            //获取当前用户 安排权限
            String roleDcode = KcUtils.getAutoPvlgCode();
            String[] deptCodes = roleDcode.split(",");

//            //当前登录人是否是项目创建人
//            boolean isXmCreator = false;
//            if (Context.getUserBean().getCode().equals(xmBean.getStr("S_USER"))) {
//                isXmCreator = true;
//            }

            for (Bean rowBean : rowBeanList) {

                String col1String = rowBean.getStr(ImpUtils.COL_NAME + "1");
                UserBean userBean = ImpUtils.getUserBeanByString(col1String);
                if (userBean == null) {
                    rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
                    continue;
                }

                String kslbkId = "";
                Bean kslbBean = new Bean();
                Bean xmglBmFzgksBean = new Bean();
                if (xmType.equals("资格类考试")) {
                    String lbString = rowBean.getStr(ImpUtils.COL_NAME + "2");
                    String xlString = rowBean.getStr(ImpUtils.COL_NAME + "3");
                    String mkString = rowBean.getStr(ImpUtils.COL_NAME + "4");
                    String typeString = rowBean.getStr(ImpUtils.COL_NAME + "5");


                    //查询考试类别kslb
                    kslbBean = ImpUtils.getKsLBKBean(lbString, xlString, mkString, typeString);
                    if (kslbBean == null) {
                        rowBean.set(ImpUtils.ERROR_NAME, "找不到相关考试类别");
                        continue;
                    } else {
                        kslbkId = kslbBean.getStr("KSLBK_ID");
                    }

                    //不在项目引入的考试类别
                    SqlBean sqlBean = new SqlBean();
                    sqlBean.and("XM_ID", xmId);
                    sqlBean.and("KSLBK_ID", kslbkId);
                    if (ServDao.count(TsConstant.SERV_BM_KSLB, sqlBean) <= 0) {
                        rowBean.set(ImpUtils.ERROR_NAME, "该考试类别不在项目引入的考试类别中");
                        continue;
                    }

                } else {
                    String fzgksName = rowBean.getStr(ImpUtils.COL_NAME + "2");
                    SqlBean sqlBean = new SqlBean();
                    sqlBean.and("XM_ID", xmId);
                    sqlBean.and("FZGKS_NAME", fzgksName);
                    //TS_XMGL_BM_FZGKS FZGKS_ID 非资格考试 TS_BMSH_PASS KSLBK_ID
                    xmglBmFzgksBean = ServDao.find("TS_XMGL_BM_FZGKS", sqlBean);
                    if (xmglBmFzgksBean == null) {
                        rowBean.set(ImpUtils.ERROR_NAME, "找不到相关考试类别");
                        continue;
                    } else {
                        kslbkId = xmglBmFzgksBean.getStr("FZGKS_ID");
                    }

                }

                Bean passBean = new Bean();
                passBean.set("XM_ID", xmId);//项目id
                passBean.set("BM_CODE", userBean.getCode());// 人力资源编码
                passBean.set("KSLBK_ID", kslbkId);// 考试类别id
                // 先查询避免重复添加
                if (ServDao.count(TsConstant.TS_BMSH_PASS, passBean) > 0) {
                    //TS_BMSH_PASS 有数据
                    List<Bean> passBeanList = ServDao.finds(TsConstant.TS_BMSH_PASS, passBean);
                    if (CollectionUtils.isNotEmpty(passBeanList)) {
                        Bean existsPassBean = passBeanList.get(0);
//                        ParamBean deptContainParamBean = new ParamBean();
//                        deptContainParamBean.set("DEPT_CODE", userBean.getStr("DEPT_CODE"));
//                        OutBean deptContainFlagOutBean = ServMgr.act(TsConstant.SERV_KCAP_DAPCC, "deptContainFlag", deptContainParamBean);
//                        if ("true".equals(deptContainFlagOutBean.getStr("flag"))) {
//                            //有权限(TS_BMSH_PASS有数据 + 有权限 -> 重复数据)
//                            rowBean.set(ImpUtils.ERROR_NAME, "重复数据");
//                            continue;
//                        } else {
//                        }

                        //(TS_BMSH_PASS有数据 -> 借考到本地)
                        String bmStatus = existsPassBean.getStr("BM_STATUS");
                        if ("1".equals(bmStatus) && "3".equals(bmStatus)) {
                            //已请假
                            rowBean.set(ImpUtils.ERROR_NAME, "已请假");
                            continue;
                        } else {
                            //未请假
                            if (deptCodes.length > 0) {
                                //借考地为
                                existsPassBean.set("BM_STATUS", "2");
                                existsPassBean.set("JK_ODEPT", deptCodes[0]);
                                ServDao.save(TsConstant.TS_BMSH_PASS, existsPassBean);
                            }
                        }
                    }
                } else {
//                    if (paramBean.getStr("OPT_TYPE").equals("JK")) {
//                    }
                    //请先将该人员添加到待安排考生中
                    rowBean.set(ImpUtils.ERROR_NAME, "请先将该人员添加到待安排考生中");
                    continue;
                }
            }
            ServDao.creates(TsConstant.TS_BMSH_PASS, beans);

        }

        return outBean.set(ImpUtils.ALL_LIST, rowBeanList).set("successlist", beans);
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
        String xmId = paramBean.getStr("XM_ID"); // xmId

        //*获取文件内容
        List<Bean> rowBeanList = paramBean.getList(ImpUtils.DATA_LIST);
        List<Bean> beans = new ArrayList<Bean>();

        Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
        if (xmBean == null) {
            for (Bean rowBean : rowBeanList) {
                rowBean.set(ImpUtils.ERROR_NAME, "程序错误，找不到项目");
            }

        } else {
            String xmType = xmBean.getStr("XM_TYPE");

            //获取当前用户 安排权限
//            String roleDcode = KcUtils.getAutoPvlgCode();
//            String[] deptCodes = roleDcode.split(",");

            //当前登录人是否是项目创建人
            boolean isXmCreator = false;
            if (Context.getUserBean().getCode().equals(xmBean.getStr("S_USER"))) {
                isXmCreator = true;
            }

            for (Bean rowBean : rowBeanList) {
                String col1String = rowBean.getStr(ImpUtils.COL_NAME + "1");
                UserBean userBean = ImpUtils.getUserBeanByString(col1String);
                if (userBean == null) {
                    rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
                    continue;
                }

                String kslbkId = "";
                Bean kslbBean = new Bean();
                Bean xmglBmFzgksBean = new Bean();
                if (xmType.equals("资格类考试")) {
                    String lbString = rowBean.getStr(ImpUtils.COL_NAME + "2");
                    String xlString = rowBean.getStr(ImpUtils.COL_NAME + "3");
                    String mkString = rowBean.getStr(ImpUtils.COL_NAME + "4");
                    String typeString = rowBean.getStr(ImpUtils.COL_NAME + "5");


                    //查询考试类别kslb
                    kslbBean = ImpUtils.getKsLBKBean(lbString, xlString, mkString, typeString);
                    if (kslbBean == null) {
                        rowBean.set(ImpUtils.ERROR_NAME, "找不到相关考试类别");
                        continue;
                    } else {
                        kslbkId = kslbBean.getStr("KSLBK_ID");
                    }

                    //不在项目引入的考试类别
                    SqlBean sqlBean = new SqlBean();
                    sqlBean.and("XM_ID", xmId);
                    sqlBean.and("KSLBK_ID", kslbkId);
                    if (ServDao.count(TsConstant.SERV_BM_KSLB, sqlBean) <= 0) {
                        rowBean.set(ImpUtils.ERROR_NAME, "该考试类别不在项目引入的考试类别中");
                        continue;
                    }

                } else {
                    String fzgksName = rowBean.getStr(ImpUtils.COL_NAME + "2");
                    SqlBean sqlBean = new SqlBean();
                    sqlBean.and("XM_ID", xmId);
                    sqlBean.and("FZGKS_NAME", fzgksName);
                    //TS_XMGL_BM_FZGKS FZGKS_ID 非资格考试 TS_BMSH_PASS KSLBK_ID
                    xmglBmFzgksBean = ServDao.find("TS_XMGL_BM_FZGKS", sqlBean);
                    if (xmglBmFzgksBean == null) {
                        rowBean.set(ImpUtils.ERROR_NAME, "找不到相关考试类别");
                        continue;
                    } else {
                        kslbkId = xmglBmFzgksBean.getStr("FZGKS_ID");
                    }

                }

                Bean passBean = new Bean();
                passBean.set("XM_ID", xmId);//项目id
                passBean.set("BM_CODE", userBean.getCode());// 人力资源编码
                passBean.set("KSLBK_ID", kslbkId);// 考试类别id
                // 先查询避免重复添加
                if (ServDao.count(TsConstant.TS_BMSH_PASS, passBean) > 0) {
                    rowBean.set(ImpUtils.ERROR_NAME, "重复数据");
                    continue;

//                    //TS_BMSH_PASS 有数据
//                    List<Bean> passBeanList = ServDao.finds(TsConstant.TS_BMSH_PASS, passBean);
//                    if (CollectionUtils.isNotEmpty(passBeanList)) {
//                        Bean existsPassBean = passBeanList.get(0);
//                        ParamBean deptContainParamBean = new ParamBean();
//                        deptContainParamBean.set("DEPT_CODE", userBean.getStr("DEPT_CODE"));
//                        OutBean deptContainFlagOutBean = ServMgr.act(TsConstant.SERV_KCAP_DAPCC, "deptContainFlag", deptContainParamBean);
//                        if ("true".equals(deptContainFlagOutBean.getStr("flag"))) {
//                            //有权限(TS_BMSH_PASS有数据 + 有权限 -> 重复数据)
//                            rowBean.set(ImpUtils.ERROR_NAME, "重复数据");
//                            continue;
//                        } else {
//                            //无权限(TS_BMSH_PASS有数据 + 无权限 -> 借考到本地)
//                            String bmStatus = existsPassBean.getStr("BM_STATUS");
//                            if ("1".equals(bmStatus) && "3".equals(bmStatus)) {
//                                //已请假
//                                rowBean.set(ImpUtils.ERROR_NAME, "已请假");
//                                continue;
//                            } else {
//                                //未请假
//                                if (deptCodes.length > 0) {
//                                    //借考地为
//                                    existsPassBean.set("BM_STATUS", "2");
//                                    existsPassBean.set("JK_ODEPT", deptCodes[0]);
//                                    ServDao.save(TsConstant.TS_BMSH_PASS, existsPassBean);
//                                }
//                            }
//                        }
//                    }
                } else {
                    //                        ParamBean deptContainParamBean = new ParamBean();
//                        deptContainParamBean.set("DEPT_CODE", userBean.getStr("DEPT_CODE"));
//                        OutBean deptContainFlagOutBean = ServMgr.act(TsConstant.SERV_KCAP_DAPCC, "deptContainFlag", deptContainParamBean);
//                        if ("true".equals(deptContainFlagOutBean.getStr("flag"))) {
//                            //有权限(TS_BMSH_PASS有数据 + 有权限 -> 重复数据)
//                            rowBean.set(ImpUtils.ERROR_NAME, "重复数据");
//                            continue;
//                        } else {
//                        }
                    if (isXmCreator) {
                        //没有记录 ->添加 额外的人
                        if (xmType.equals("资格类考试")) {
                            //资格考试
                            //passBean.set("BM_ID", );
                            passBean.set("BM_NAME", userBean.getName());
                            passBean.set("KSLBK_ID", kslbBean.getStr("KSLBK_ID"));
                            //passBean.set("BM_TITLE", );
                            passBean.set("BM_LB", kslbBean.getStr("KSLBK_NAME"));
                            passBean.set("BM_XL", kslbBean.getStr("KSLBK_XL"));
                            passBean.set("BM_MK", kslbBean.getStr("KSLBK_MK"));
                            passBean.set("BM_TYPE_NAME", kslbBean.getStr("KSLBK_TYPE_NAME"));
                            passBean.set("BM_LB_CODE", kslbBean.getStr("KSLBK_CODE"));
                            passBean.set("BM_XL_CODE", kslbBean.getStr("KSLBK_XL_CODE"));
                            passBean.set("BM_MK_CODE", kslbBean.getStr("KSLBK_MKCODE"));
                            passBean.set("BM_TYPE", kslbBean.getStr("KSLBK_TYPE"));
                            passBean.set("BM_KS_TIME", kslbBean.getStr("KSLBK_TIME"));
                            passBean.set("S_FLAG", "1");
                            passBean.set("S_DEPT", userBean.getDeptCode());
                            passBean.set("S_TDEPT", userBean.getTDeptCode());
                            passBean.set("S_ODEPT", userBean.getODeptCode());//机构编码 导入后所属机构
                            passBean.set("ODEPT_CODE", userBean.getODeptCode());
                            passBean.set("BM_STATUS", "0");
//                passBean.set("BM_YIYI",);
//                passBean.set("S_CMPY", );
                            passBean.set("S_USER", Context.getUserBean().getCode());
                            passBean.set("S_MTIME", DateUtils.getDatetime());
                            passBean.set("S_ATIME", DateUtils.getDatetime());
//                passBean.set("SH_NODE", );
                            passBean.set("SH_STATUS", "0");
                            passBean.set("SH_LEVEL", "1");
//                passBean.set("SH_USER", );
//                passBean.set("SH_OTHER", );
//                passBean.set("SH_DESC", );
//                passBean.set("JK_ODEPT", );
//                passBean.set("RZYEAR", );//任职时间
//                passBean.set("SH_ID", );//id
                            beans.add(passBean);
                        } else {
                            //非资格考试
                            //passBean.set("BM_ID", );
                            passBean.set("BM_NAME", userBean.getName());
                            passBean.set("KSLBK_ID", kslbkId);
                            passBean.set("BM_TITLE", xmglBmFzgksBean.getStr("FZGKS_NAME"));
//                                passBean.set("BM_LB",);
//                                passBean.set("BM_XL", );
//                                passBean.set("BM_MK",);
//                                passBean.set("BM_TYPE_NAME",);
//                                passBean.set("BM_LB_CODE", );
//                                passBean.set("BM_XL_CODE",);
//                                passBean.set("BM_MK_CODE", );
//                                passBean.set("BM_TYPE", );
//                                passBean.set("BM_KS_TIME", );
                            passBean.set("S_FLAG", "1");
                            passBean.set("S_DEPT", userBean.getDeptCode());
                            passBean.set("S_TDEPT", userBean.getTDeptCode());
                            passBean.set("S_ODEPT", userBean.getODeptCode());//机构编码 导入后所属机构
                            passBean.set("ODEPT_CODE", userBean.getODeptCode());
                            passBean.set("BM_STATUS", "0");
//                passBean.set("BM_YIYI",);
//                passBean.set("S_CMPY", );
                            passBean.set("S_USER", Context.getUserBean().getCode());
                            passBean.set("S_MTIME", DateUtils.getDatetime());
                            passBean.set("S_ATIME", DateUtils.getDatetime());
//                passBean.set("SH_NODE", );
                            passBean.set("SH_STATUS", "0");
                            passBean.set("SH_LEVEL", "1");
//                passBean.set("SH_USER", );
//                passBean.set("SH_OTHER", );
//                passBean.set("SH_DESC", );
//                passBean.set("JK_ODEPT", );
//                passBean.set("RZYEAR", );//任职时间
//                passBean.set("SH_ID", );//id
                            beans.add(passBean);

                        }

                    } else {
                        rowBean.set(ImpUtils.ERROR_NAME, "只有项目创建人可以添加额外的人员");
                        continue;
                    }
                }

//            else{
//                rowBean.set(ImpUtils.ERROR_NAME, "重复数据:" + userBean.getCode() + "-" + kslbBean.getStr("KSLBK_ID"));
//            }
            }
            ServDao.creates(TsConstant.TS_BMSH_PASS, beans);


        }


        return outBean.set(ImpUtils.ALL_LIST, rowBeanList).set("successlist", beans);
    }

    public OutBean getKSLBBySql(ParamBean paramBean) {
        SqlBean sqlBean = new SqlBean();
        String lbName = paramBean.getStr("LB_NAME");
        String xlName = paramBean.getStr("XL_NAME");
        String mkName = paramBean.getStr("MK_NAME");
        String typeName = paramBean.getStr("TYPE_NAME");

        sqlBean.and("KSLBK_NAME", lbName);
        sqlBean.and("KSLBK_XL", xlName);
        if (StringUtils.isNotBlank(mkName)) {
            sqlBean.and("KSLBK_MK", mkName);
        }
        sqlBean.and("KSLBK_TYPE_NAME", typeName);
        Bean find = ServDao.find("TS_XMGL_BM_KSLBK", sqlBean);
        if (find != null) {
            OutBean outBean = new OutBean();
            outBean.putAll(find);
            return outBean;
        }
        return null;
    }
}
