package com.rh.ts.rzgj;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;

import java.util.HashMap;
import java.util.List;

/**
 * Created by shenh on 2017/9/19.
 */
public class RzgjServ extends CommonServ {

    public OutBean getInfo(ParamBean paramBean) {
        OutBean outBean = new OutBean();

        //用户的USER_CODE
        String USER_CODE = Context.getUserBean().getCode();
        //获证信息
        List<Bean> dataList = ServDao.finds("TS_ETI_CERT_QUAL_V", "and STU_PERSON_ID='" + USER_CODE + "'");
        //用户信息查询
        Bean stu = ServDao.find("SY_ORG_USER_INFO_SELF", USER_CODE);

        //入职日期
        String USER_CMPY_DATE = "";
        String userCmpyDate = "";
        //职位名称
        //String USER_POST="";
        if (stu != null) {
            USER_CMPY_DATE = stu.getStr("USER_CMPY_DATE");
            if (!USER_CMPY_DATE.equals("")) {
                userCmpyDate = USER_CMPY_DATE.substring(0, 4) + "年" + USER_CMPY_DATE.substring(4, 6) + "月";
            }
            //USER_POST=stu.getStr("USER_POST");
        }
        //查找当前用户的序列
        Bean ser = ServDao.find("SY_HRM_ZDSTAFFPOSITION", USER_CODE);
        //查找用户序列名称编码
        String STATION_NO_CODE = "";
        String STATION_NO = "";
        //职务层级编码
        String POSTION_ID = "";
        if (ser != null) {
            STATION_NO_CODE = ser.getStr("STATION_NO_CODE");
            STATION_NO = ser.getStr("STATION_NO");
            POSTION_ID = ser.getStr("DUTY_LV_CODE");
        }

        for (Bean data : dataList) {
            String ISSUE_DATE = data.getStr("ISSUE_DATE");//发证日期
            String ISSUE_DATE_STR = ISSUE_DATE.substring(0, 4) + "年" + ISSUE_DATE.substring(4, 6) + "月";

            String CERT_GRADE_CODE = data.getStr("CERT_GRADE_CODE");//名称
            String CERT_ID = data.getStr("CERT_ID");
            Integer QUALFY_STAT = data.getInt("QUALFY_STAT");//证书状态
            //有效日期
            String BGN_DATE = data.getStr("BGN_DATE");//起始有效日期
            String END_DATE = data.getStr("END_DATE");//结束有效日期
            String date = BGN_DATE.equals("") ? END_DATE : BGN_DATE;
            if (!BGN_DATE.equals("") && !END_DATE.equals("")) {
                date = BGN_DATE + "-" + END_DATE;
            }

            //证书管理
            List<Bean> infos = ServDao.finds("TS_ETI_CERT_INFO", "and CERT_ID='" + CERT_ID + "'");
            String state = "";
            Integer VALID_TERM = infos.get(0).getInt("VALID_TERM");
            if (VALID_TERM == 1) {
                if (QUALFY_STAT == 1) {
                    state = "正常";
                } else if (QUALFY_STAT == 2) {
                    state = "获取中";
                } else {
                    state = "过期";
                }
            } else {
                state = "无效";
            }

            data.set("ISSUE_DATE_STR", ISSUE_DATE_STR);
//            data.set("CERT_GRADE_CODE", CERT_GRADE_CODE);
            data.set("date", date);
            data.set("state", state);
        }

        String POSTION_QUALIFICATION = "0";
        String POSTION_QUALIFICATION_STR = "";
        if (dataList.size() == 0) {
            //根据职位名称查找岗位信息
            //岗位资格
            if (!POSTION_ID.equals("")) {
                Bean bean = ServDao.find("TS_ORG_POSTION", POSTION_ID);
                if (bean != null) {
                    POSTION_QUALIFICATION = bean.getStr("POSTION_QUALIFICATION");
                }
            }
            String[] classs = {" ", "初级", "中级", "高级", "专家级"};
            Integer i = Integer.valueOf(POSTION_QUALIFICATION);
            POSTION_QUALIFICATION_STR = classs[i];
        }


        //追赶，同步，落后
        int pre = 0, after = 0, other = 0, num = 0;
        HashMap<Object, Object> p = new HashMap<>();
        p.put("STATION_NO_CODE", STATION_NO_CODE);
        Bean queryBean = new Bean(p);
        if (!STATION_NO_CODE.equals("")) {
            //查找同等序列下的人数
            HashMap<Object, Object> t = new HashMap<>();
            num = ServDao.count("SY_HRM_ZDSTAFFPOSITION", queryBean);
            List<Bean> infos = ServDao.finds("TS_ETI_CERT_INFO", "and STATION_NO='" + STATION_NO + "'");
            if (infos.size() > 0) {
                if (dataList.size() < infos.size()) {
                    t.put("CERT_ID", infos.get(dataList.size()).getStr("CERT_ID"));
                    Bean param = new Bean(t);
                    pre = ServDao.count("TS_ETI_CERT_QUAL", param);
                    if (dataList.size() > 0) {
                        t.put("CERT_ID", infos.get(dataList.size() - 1).getStr("CERT_ID"));
                        Bean par = new Bean(t);
                        other = ServDao.count("TS_ETI_CERT_QUAL", par) - 1 - pre;
                        after = num - other - pre - 1;
                    }
                    if (dataList.size() == 0) {
                        other = num - pre - 1;
                    }

                }
                if (dataList.size() == infos.size()) {
                    pre = 0;
                    t.put("CERT_ID", infos.get(dataList.size() - 1).getStr("CERT_ID"));
                    Bean param = new Bean(t);
                    other = ServDao.count("TS_ETI_CERT_QUAL", param) - 1 - pre;
                    after = num - other - pre;
                }
            } else {
                other = num - 1;
            }
        }
        if (STATION_NO_CODE.equals("")) {
            other = num;
        }

//        dataList = new ArrayList<>();
        outBean.set("dataList", dataList);

        outBean.set("STATION_NO", STATION_NO);//当前序列
        //初级 中级 高级
        outBean.set("POSTION_QUALIFICATION", POSTION_QUALIFICATION);
        outBean.set("POSTION_QUALIFICATION_STR", POSTION_QUALIFICATION_STR);

        outBean.set("USER_CMPY_DATE", userCmpyDate); //入公司日期

        outBean.set("num", num);//当前序列有

        outBean.set("pre", pre);//前
        outBean.set("other", other);//并肩
        outBean.set("after", after);//之后

        return outBean;
    }
}
