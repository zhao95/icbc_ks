package com.rh.ts.bm.group;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.comm.FileMgr;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
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
     * @param userCode
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
    public OutBean saveFromExcel(ParamBean paramBean) throws IOException, BiffException {
        OutBean outBean = new OutBean();

        String gId = (String) paramBean.get("G_ID"),//报名群组id
                fileId = (String) paramBean.get("FILE_ID");//文件id
        //获取项目id（xmId）
        Bean bmGroupBean = ServDao.find(TsConstant.SERV_BM_GROUP, gId);
        String xmId = bmGroupBean.getStr("XM_ID");

        List<Bean> beanList = this.getDataFromXls(fileId);
        List<String> codeList = new ArrayList<String>();//避免重复添加数据

        List<Bean> beans = new ArrayList<Bean>();
        for (Bean bean : beanList) {
            String codeStr = bean.getStr(CODE_STR);

            int length = codeStr.trim().length();
            Bean userBean = null;
            try {
                if (length == 9) {
                    userBean = UserMgr.getUserByLoginName(codeStr);
                } else if (length == 10) {
                    userBean = UserMgr.getUser(codeStr);
                } else if (length > 11) {
                    List<Bean> userBeanList = ServDao.finds("SY_ORG_USER_ALL", " and USER_IDCARD ='" + codeStr + "'");
                    if (CollectionUtils.isNotEmpty(userBeanList)) {
                        userBean = userBeanList.get(0);
                    }
                }
            } catch (Exception e) {
                userBean = null;
            }
            if (userBean == null) {
                continue;
            }

            String code = userBean.getStr("USER_CODE"),
                    name = userBean.getStr("USER_NAME");

            if (codeList.contains(code)) {
                //已包含 continue ：避免重复添加数据
                continue;
            }

            bean.clear();
            bean.set("G_ID", gId);
            bean.set("USER_DEPT_CODE", code);
            bean.set("XM_ID", xmId);
            bean.set("G_TYPE", 1);//选取类型 1人员

            if (ServDao.count(TsConstant.SERV_BM_GROUP_USER, bean) <= 0) {
                //先查询避免重复添加
                bean.set("USER_DEPT_NAME", name);
                beans.add(bean);
                codeList.add(code);
            }
        }
        ServDao.creates(TsConstant.SERV_BM_GROUP_USER, beans);
//        int total = beanList.size();
        FileMgr.deleteFile(fileId);
        return outBean.setCount(codeList.size()).setOk("成功导入" + codeList.size() + "条");
    }

    /**
     *
     *
     * @param fileId 文件id
     */
    private List<Bean> getDataFromXls(String fileId) throws IOException, BiffException {
        List<Bean> result = new ArrayList<Bean>();
        Bean fileBean = FileMgr.getFile(fileId);
        InputStream in = FileMgr.download(fileBean);
        Workbook workbook = Workbook.getWorkbook(in);
        try {
            Sheet sheet1 = workbook.getSheet(0);
            int rows = sheet1.getRows();
            for (int i = 0; i < rows; i++) {
//                if (i != 0) {
                Cell[] cells = sheet1.getRow(i);
                String contents0 = cells[0].getContents();
                if (!StringUtils.isEmpty(contents0)) {
                    Bean bean = new Bean();
                    bean.set(CODE_STR, contents0);
//                        bean.set("name", contents1);
                    result.add(bean);
                }
//                }
            }
        } catch (Exception e) {
            throw new TipException("Excel文件解析错误，请校验！");
        } finally {
            workbook.close();
        }
        return result;
    }


}
