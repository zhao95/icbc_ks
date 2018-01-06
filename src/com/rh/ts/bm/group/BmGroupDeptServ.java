package com.rh.ts.bm.group;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.ImpUtils;
import com.rh.ts.util.TsConstant;

public class BmGroupDeptServ extends CommonServ {

    private static String CODE_STR = "codeStr";

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
            Bean deptBean = OrgMgr.getDept(colCode);
            if (deptBean == null) {
                rowBean.set(ImpUtils.ERROR_NAME, "找不到部门");
                continue;
            }

            String code = deptBean.getStr("DEPT_CODE"),
                    name = deptBean.getStr("DEPT_NAME");
            if (codeList.contains(code)) {
                //已包含 continue ：避免重复添加数据
                rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
                continue;
            }

            Bean bean = new Bean();
            bean.set("G_ID", gId);
            bean.set("USER_DEPT_CODE", code);
            bean.set("XM_ID", xmId);
            bean.set("G_TYPE", 2);//选取类型 1人员

            if (ServDao.count(TsConstant.SERV_BM_GROUP_USER, bean) <= 0) {
                //先查询避免重复添加
                bean.set("USER_DEPT_NAME", name);
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
