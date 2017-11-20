package com.rh.ts.rzgj;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by shenh on 2017/9/19.
 */
public class RzgjServ extends CommonServ {

    public OutBean getInfo(ParamBean paramBean) {
        OutBean outBean = new OutBean();

        //用户的USER_CODE
        String USER_CODE = Context.getUserBean().getCode();
        //获证信息
        ParamBean queryParamBean = new ParamBean();
        queryParamBean.set(Constant.PARAM_WHERE, "and STU_PERSON_ID='" + USER_CODE + "'");
        queryParamBean.set(Constant.PARAM_ORDER, "ISSUE_DATE desc");//发证日期 倒序
        List<Bean> dataList = ServDao.finds("TS_ETI_CERT_QUAL_V", queryParamBean);//用户获证信息
        //用户信息查询
        Bean stu = ServDao.find("SY_ORG_USER_INFO_SELF", USER_CODE);

        //入职日期
        String USER_CMPY_DATE = "";
        String userCmpyDateStr = "";
        //职位名称
        //String USER_POST="";
        if (stu != null) {
            USER_CMPY_DATE = stu.getStr("USER_CMPY_DATE");
            if (!USER_CMPY_DATE.equals("")) {
                userCmpyDateStr = USER_CMPY_DATE.substring(0, 4) + "年" + USER_CMPY_DATE.substring(4, 6) + "月";
            }
            //USER_POST=stu.getStr("USER_POST");
        }
        //查找当前用户的序列
        Bean ser = ServDao.find("SY_HRM_ZDSTAFFPOSITION", USER_CODE);
        //查找用户序列名称编码   //DUTY_LV_CODE职务层级代码
        String STATION_NO_CODE = "";//岗位序列代码
        String STATION_NO = "";//岗位序列
        //职务层级编码
        String POSTION_ID = "";
        if (ser != null) {
            STATION_NO_CODE = ser.getStr("STATION_NO_CODE");
            STATION_NO = ser.getStr("STATION_NO");
            POSTION_ID = ser.getStr("DUTY_LV_CODE");//DUTY_LV_CODE 职务层级代码
        }

        for (Bean data : dataList) {
            String ISSUE_DATE = data.getStr("ISSUE_DATE");//发证日期
            String ISSUE_DATE_STR = ISSUE_DATE.substring(0, 4) + "年" + ISSUE_DATE.substring(4, 6) + "月";

            // 获取等级名称
            String CERT_GRADE_CODE = data.getStr("CERT_GRADE_CODE");//等级编码
            String CERT_GRADE = data.getStr("CERT_GRADE");//等级名称
            String typeName = getTypeName(CERT_GRADE_CODE);
            if (StringUtils.isBlank(typeName)) {
                if (StringUtils.isNotBlank(CERT_GRADE)) {
                    typeName = CERT_GRADE;
                }
            }
            data.set("typeName", typeName);

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
//            List<Bean> infos = ServDao.finds("TS_ETI_CERT_INFO", "and CERT_ID='" + CERT_ID + "'");
            String state = "";
            try {
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                dateFormat.parse(BGN_DATE);
                Date beginDate = dateFormat.parse(BGN_DATE);
                Date endDate = dateFormat.parse(END_DATE);
                if (now.after(beginDate) && now.before(endDate)) {
                    state = "有效";
                } else {
                    state = "无效";
                }
            } catch (ParseException e) {
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
            POSTION_QUALIFICATION_STR = getTypeName(POSTION_QUALIFICATION);
        }

        //过滤出用户当前序列的获证信息
        List<Bean> copyDataList = new ArrayList<Bean>(dataList);//用户当前序列的获证信息
        Collections.reverse(copyDataList);
        for (Iterator<Bean> iterator = copyDataList.iterator(); iterator.hasNext(); ) {
            Bean copyData = iterator.next();
            if ("A000000000000000008".equals(copyData.get("STATION_NO")) && "A000000000000000019".equals(STATION_NO_CODE)) {
//              //财会资金（证书序列）和财会（序列） code不同特殊处理
                //财会资金（证书序列）A000000000000000008   财会（序列）A000000000000000019
            } else if (!STATION_NO_CODE.equals(copyData.get("STATION_NO"))) {
                iterator.remove();
            }
        }

        //追赶，同步，落后
        int pre = 0,
                after = 0,
                other = 0,
                num = 0;//当前序列人数
        Bean queryBean = new Bean();
        queryBean.set("STATION_NO_CODE", STATION_NO_CODE);

        if (!STATION_NO_CODE.equals("")) {
            //查找同等序列下的人数
            num = ServDao.count("SY_HRM_ZDSTAFFPOSITION", queryBean);
            queryBean = new Bean();
            queryBean.set(Constant.PARAM_WHERE, "and STATION_NO='" + STATION_NO_CODE + "' and CERT_GRADE_CODE between '1' and '" + POSTION_QUALIFICATION + "'");
            after = ServDao.count("TS_ETI_CERT_QUAL_V", queryBean);

            queryBean.set(Constant.PARAM_WHERE, "and STATION_NO='" + STATION_NO_CODE + "' and CERT_GRADE_CODE between '" + POSTION_QUALIFICATION + "' and '4'");
            pre = ServDao.count("TS_ETI_CERT_QUAL_V", queryBean);

            queryBean.set(Constant.PARAM_WHERE, "and STATION_NO='" + STATION_NO_CODE + "' and CERT_GRADE_CODE = '" + POSTION_QUALIFICATION + "'");
            other = num - pre - after;
        }

        outBean.set("dataList", dataList);
        outBean.set("currentDataList", copyDataList);

        outBean.set("STATION_NO", STATION_NO);//当前序列
        //初级 中级 高级
        outBean.set("POSTION_QUALIFICATION", POSTION_QUALIFICATION);
        outBean.set("POSTION_QUALIFICATION_STR", POSTION_QUALIFICATION_STR);

        outBean.set("USER_CMPY_DATE", userCmpyDateStr); //入公司日期

        outBean.set("num", num);//当前序列有

        outBean.set("pre", pre);//前
        outBean.set("other", other);//并肩
        outBean.set("after", after);//之后

        return outBean;
    }

    /**
     * 获取级别编码对应的名称
     *
     * @return
     */
    private String getTypeName(String type) {
        String[] classs = {"", "初级", "中级", "高级", "专家级"};
        int i = 0;
        try {
            i = Integer.parseInt(type);
        } catch (Exception ignored) {
        }
        if (i >= classs.length || i < 0) {
            i = 0;
        }
        return classs[i];
    }
}
