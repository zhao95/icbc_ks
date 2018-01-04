package com.rh.ts.bm.group;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.ImpUtils;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 报名群组
 *
 * @author root
 */
public class BmGroupServ extends CommonServ {


    private static String CODE_STR = "codeStr";


    /**
     * 获取用户所有报名群组编码 (逗号相隔)
     *
     * @param paramBean
     * @return
     */
    public OutBean getBmGroupCodes(Bean paramBean) {
        UserBean userBean = Context.getUserBean();
        String userCode = userBean.getStr("USER_CODE");
        String groupCodes = "";
        Bean queryUser = new Bean().set("USER_DEPT_CODE", userCode).set("S_FLAG", 1);
        // 查询用户所有群组
        List<Bean> userList = ServDao.finds("TS_BM_GROUP_USER", queryUser);

        for (Bean user : userList) {
            // 用户群组id
            String groupCode = user.getStr("G_ID");

            if (Strings.isBlank(groupCodes)) {

                groupCodes = groupCode;
            } else {
                groupCodes += "," + groupCode;
            }
        }

        if (!Strings.isBlank(groupCodes)) {
            // 去掉重复群组
            groupCodes = Strings.removeSame(groupCodes);
        }

        return new OutBean().set("qzcodes", groupCodes);
    }

    /**
     * @param paramBean paramBean G_ID FILE_ID
     * @return outBean
     */
    public OutBean savedata(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        //获取前端传递参数
        String gId = (String) paramBean.get("G_ID");//报名群组id

        //*获取文件内容
        List<Bean> rowBeanList = paramBean.getList("datalist");
        List<String> codeList = new ArrayList<String>();//避免重复添加数据

        //获取项目id（xmId）
        Bean bmGroupBean = ServDao.find(TsConstant.SERV_BM_GROUP, gId);
        String xmId = bmGroupBean.getStr("XM_ID");

        List<Bean> beans = new ArrayList<Bean>();
        for (Bean rowBean : rowBeanList) {
            String colCode = rowBean.getStr(ImpUtils.COL_NAME + "1");
            Bean userBean = ImpUtils.getUserBeanByString(colCode);
            if (userBean == null) {
                rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
                continue;
            }

            String code = userBean.getStr("USER_CODE"),
                    name = userBean.getStr("USER_NAME");
            if (codeList.contains(code)) {
                //已包含 continue ：避免重复添加数据
                rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
                continue;
            }

            Bean bean = new Bean();
            bean.set("G_ID", gId);
            bean.set("USER_DEPT_CODE", code);
            bean.set("XM_ID", xmId);
            bean.set("G_TYPE", 1);//选取类型 1人员

            if (ServDao.count(TsConstant.SERV_BM_GROUP_USER, bean) <= 0) {
                //先查询避免重复添加
                bean.set("USER_DEPT_NAME", name);
                Bean syHrmZdstaffposition = ServDao.find("SY_HRM_ZDSTAFFPOSITION", code);
                if (syHrmZdstaffposition != null) {
                    bean.set("GW_LB", syHrmZdstaffposition.getStr("STATION_TYPE"));
                    bean.set("GW_XL", syHrmZdstaffposition.getStr("STATION_NO"));
                }
                bean.set("USER_DEPT_NAME", name);
                //保存一二级机构
                String deptCode = UserMgr.getUser(userBean.getStr("USER_CODE")).getDeptBean().getCode();//部门编码
                String[] deptArr = OrgMgr.getDept(deptCode).getCodePath().split("\\^");//五级机构
                for (int i = 0; i < deptArr.length; i++) {
                    // 最后一个 deptcodename
                    if (i == 0) {
                        String evname = OrgMgr.getDept(deptArr[i]).getName();
                        bean.set("FIR_LEVEL", evname);
                    } else if (i == 1) {
                        String evname = OrgMgr.getDept(deptArr[i]).getName();
                        bean.set("SEN_LEVEL", evname);
                    }
                }

                beans.add(bean);
                codeList.add(code);
            } else {
                rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
            }
        }
        ServDao.creates(TsConstant.SERV_BM_GROUP_USER, beans);

        return outBean.set("alllist", rowBeanList).set("successlist", codeList);
        //在excel中设置失败信息
    }

    /**
     * 导入方法开始的入口
     */
    public OutBean saveFromExcel(ParamBean paramBean) {
        String fileId = paramBean.getStr("FILE_ID");
        //方法入口
        paramBean.set("SERVMETHOD", "savedata");
        OutBean out = ImpUtils.getDataFromXls(fileId, paramBean);
        String failnum = out.getStr("failernum");
        String successnum = out.getStr("oknum");
        //返回导入结果
        return new OutBean().set("FILE_ID", out.getStr("fileid")).setOk("导入成功：" + successnum + "条,导入失败：" + failnum + "条");
    }
}
