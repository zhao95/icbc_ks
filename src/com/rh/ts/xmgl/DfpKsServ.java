package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.DateUtils;
import com.rh.core.util.ImpUtils;
import com.rh.ts.pvlg.PvlgUtils;
import com.rh.ts.util.TsConstant;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DfpKsServ extends CommonServ {
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
        paramBean.set("SERVMETHOD", "impDataSave");
        OutBean out = ImpUtils.getDataFromXls(fileId, paramBean);
        String failNum = out.getStr("failernum");
        String successNum = out.getStr("oknum");
        //返回导入结果
        return new OutBean().set("FILE_ID", out.getStr("fileid")).setOk("导入成功：" + successNum + "条,导入失败：" + failNum + "条");
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
        String xmId = (String) paramBean.get("XM_ID"); // xmId
//        "SOURCE":"USER_CODE~USER_NAME~ODEPT_CODE",
// "TARGET":"BM_CODE~BM_NAME","TYPE"

//        "SOURCE":"KSLBK_NAME~KSLBK_CODE~KSLBK_XL~KSLBK_XL_CODE~KSLBK_MK~KSLBK_MKCODE~KSLBK_TYPE~KSLBK_TYPE_NAME~KSLBK_TIME~KSLBK_ID"
// ,"TARGET":"BM_LB~BM_LB_CODE~BM_XL~BM_XL_CODE~BM_MK~BM_MK_CODE~BM_TYPE~BM_TYPE_NAME~BM_KS_TIME~KSLBK_ID"


//AND KSLBK_NAME is not null AND  KSLBK_XL is not  null AND KSLBK_MK is not null AND KSLBK_TYPE is not null

        //*获取文件内容
        List<Bean> rowBeanList = paramBean.getList(ImpUtils.DATA_LIST);

        List<Bean> beans = new ArrayList<Bean>();
        for (Bean rowBean : rowBeanList) {
            String col1String = rowBean.getStr(ImpUtils.COL_NAME + "1");
            String lbString = rowBean.getStr(ImpUtils.COL_NAME + "2");
            String xlString = rowBean.getStr(ImpUtils.COL_NAME + "3");
            String mkString = rowBean.getStr(ImpUtils.COL_NAME + "4");
            String typeString = rowBean.getStr(ImpUtils.COL_NAME + "5");
            UserBean userBean = ImpUtils.getUserBeanByString(col1String);
            if (userBean == null) {
                rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
                continue;
            }

            //查询考试类别kslb
            ParamBean queryBean = new ParamBean();
            queryBean.set("LB_NAME", lbString);
            queryBean.set("XL_NAME", xlString);
            queryBean.set("MK_NAME", mkString);
            queryBean.set("TYPE_NAME", typeString);
            OutBean kslbBean = this.getKSLBBySql(queryBean);
            if (kslbBean == null) {
                rowBean.set(ImpUtils.ERROR_NAME, "找不到相关考试类别");
                continue;
            }

            Bean passBean = new Bean();
            passBean.set("XM_ID", xmId);//项目id
            passBean.set("BM_CODE", userBean.getCode());// 人力资源编码
            passBean.set("KSLBK_ID", kslbBean.getStr("KSLBK_ID"));// 考试类别id
            // 先查询避免重复添加
            if (ServDao.count(TsConstant.TS_BMSH_PASS, passBean) <= 0) {
//                passBean.set("BM_ID", );
                passBean.set("BM_NAME", userBean.getName());
                passBean.set("KSLBK_ID", kslbBean.getStr("KSLBK_ID"));
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
//                passBean.set("BM_TITLE", );
//                passBean.set("SH_ID", );//id
                beans.add(passBean);
            } else {
                rowBean.set(ImpUtils.ERROR_NAME, "重复数据:" + userBean.getCode() + "-" + kslbBean.getStr("KSLBK_ID"));
            }
        }
        ServDao.creates(TsConstant.TS_BMSH_PASS, beans);

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
