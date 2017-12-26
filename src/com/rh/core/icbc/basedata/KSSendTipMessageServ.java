package com.rh.core.icbc.basedata;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.msg.DIIOPMailSender;
import com.rh.core.icbc.basedata.serv.KSNImpDataServ;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.OutBean;
import com.rh.core.util.DateUtils;
import com.rh.core.util.freemarker.FreeMarkerUtils;

/**
 * 提醒信息发送接口
 *
 * @author leader
 */
public class KSSendTipMessageServ {
	/** log. */
	private static Log log = LogFactory.getLog(KSNImpDataServ.class);
    /**
     * 发送提醒消息接口类，对接工行发送提醒的类型（本系统只做邮件，别的提醒方式在e办公系统体现）
     *
     * @param tipBean 参数为信息数据bean，包含发送消息提醒语tipMsg，人员编码USER_CODE等详情参照该方法内对照字段
     * @param tipFlag 提醒类型区分（qjStar 请假开始，qjResult 请假结果，jkStar 借考开始，jkResult 借考结果，bmStar 报名开始，bmEnd 报名截止，kczwShow 考场座位公示，zkzStar 准考证开始打印）
     * @return
     */
    public OutBean sendTipMessageBeanForICBC(Bean tipBean, String tipFlag) {
    	log.error("--------------开始执行发送邮件提醒bean类------------");
        final String USER_CODE = tipBean.getStr("USER_CODE");
        final String tipMsg = tipBean.getStr("tipMsg");
        //发送请假开始提醒消息
        if (tipFlag.equals("qjStar")) {
            //创建bean存储网页模板生成所需要的数据。
            Bean pBean = new Bean();
            pBean.set("tipMsg", tipMsg);
            //String confTipType = ConfMgr.getConf("TS_TIP_QJ_START_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
            String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
            if (confTipType.equals("EMAIL")) {
                //发送邮件包含的网页页面
                sendMail(USER_CODE, "请假开始提醒", tipMsg);
            }
            //融E联方式发送
            else if (confTipType.equals("ELINK")) {
                //暂未处理
            }
        }
        //发送请假结果提醒消息
        if (tipFlag.equals("qjResult")) {
            //创建bean存储网页模板生成所需要的数据。
//            Bean pBean = new Bean();
//            pBean.set("tipMsg", tipMsg);
            String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
            if (confTipType.equals("EMAIL")) {
                //发送邮件包含的网页页面
                sendMail(USER_CODE, "请假结果提醒", tipMsg);
            }
        }
        //发送借考开始提醒消息
        if (tipFlag.equals("jkStar")) {
            //创建bean存储网页模板生成所需要的数据。
//            Bean pBean = new Bean();
//            pBean.set("tipMsg", tipMsg);
            String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
            if (confTipType.equals("EMAIL")) {
                //发送邮件包含的网页页面
                sendMail(USER_CODE, "借考开始提醒", tipMsg);
            }
        }
        //发送借考结果提醒消息
        if (tipFlag.equals("jkResult")) {
            //创建bean存储网页模板生成所需要的数据。
//            Bean pBean = new Bean();
//            pBean.set("tipMsg", tipMsg);
            String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
            if (confTipType.equals("EMAIL")) {
                //发送邮件包含的网页页面
                sendMail(USER_CODE, "借考结果提醒", tipMsg);
            }
        }
        //发送报名开始提醒消息
        if (tipFlag.equals("bmStar")) {
            //创建bean存储网页模板生成所需要的数据。
//            Bean pBean = new Bean();
//            pBean.set("tipMsg", tipMsg);
            String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
            if (confTipType.equals("EMAIL")) {
                //发送邮件包含的网页页面
                sendMail(USER_CODE, "报名开始提醒", tipMsg);
            }
        }
        //发送报名截止提醒消息
        if (tipFlag.equals("bmEnd")) {
            //创建bean存储网页模板生成所需要的数据。
//            Bean pBean = new Bean();
//            pBean.set("tipMsg", tipMsg);
            String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
            if (confTipType.equals("EMAIL")) {
                //发送邮件包含的网页页面
                sendMail(USER_CODE, "报名截止提醒", tipMsg);
            }
        }
        //考场座位公示
        if (tipFlag.equals("kczwShow")) {
            //创建bean存储网页模板生成所需要的数据。
//            Bean pBean = new Bean();
//            pBean.set("tipMsg", tipMsg);
            String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
            if (confTipType.equals("EMAIL")) {
                //发送邮件包含的网页页面
                sendMail(USER_CODE, "考场座位公示提醒", tipMsg);
            }
        }
        //发送准考证开始打印提醒消息
        if (tipFlag.equals("zkzStar")) {
            //创建bean存储网页模板生成所需要的数据。
//            Bean pBean = new Bean();
//            pBean.set("tipMsg", tipMsg);
            String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
            if (confTipType.equals("EMAIL")) {
                //发送邮件包含的网页页面
                sendMail(USER_CODE, "准考证开始打印提醒", tipMsg);
            }
        }
        log.error("--------------结束执行发送邮件提醒bean类------------");
        return new OutBean().set("OK", "消息提醒发送成功");
    }

    private void sendMail(String USER_CODE, String title, String tipMsg) {
        //发送邮件包含的网页页面
        Bean pBean = new Bean();
        pBean.set("title", title);
        pBean.set("tipMsg", tipMsg);
        pBean.set("CURRENT_TIME", DateUtils.getDatetime());
        String html = FreeMarkerUtils.parseText(Context.appStr(Context.APP.SYSPATH) + "sy/comm/home/ftl/SY_EMAIL_NEWS.ftl", pBean);
        DIIOPMailSender.sendMail(title, html, UserMgr.getUser(USER_CODE).getStr("USER_EMAIL"));
    }

    /**
     * 发送提醒消息接口，对接工行发送提醒的接口（融E联短信，邮件）
     *
     * @param tipList 参数为信息数据集，集合中的bean包含发送消息提醒语tipMsg，人员编码USER_CODE其余字段要在该方法内寻找匹配键值
     * @param tipFlag 提醒类型区分（qjStar 请假开始，qjResult 请假结果，jkStar 借考开始，jkResult 借考结果，bmStar 报名开始，bmEnd 报名截止，kczwShow 考场座位公示，zkzStar 准考证开始打印）
     */
    public OutBean sendTipMessageListForICBC(List<Bean> tipList, String tipFlag) {
    	log.error("--------------开始执行发送邮件提醒list类------------");
        //请假开始
        if (tipFlag.equals("qjStar")) {
            for (Bean aTipList : tipList) {
                String USER_CODE = aTipList.getStr("USER_CODE");
                String tipMsg = aTipList.getStr("tipMsg");
                String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
                if (confTipType.equals("EMAIL")) {
                    //发送邮件包含的网页页面
                    sendMail(USER_CODE, "请假开始提醒", tipMsg);
                }
                System.out.println("---发送请假开始提醒消息--》" + USER_CODE + tipMsg);
            }
        }
        //请假结果
        if (tipFlag.equals("qjResult")) {
            for (Bean aTipList : tipList) {
                String USER_CODE = aTipList.getStr("USER_CODE");
                String tipMsg = aTipList.getStr("tipMsg");
                String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
                if (confTipType.equals("EMAIL")) {
                    //发送邮件包含的网页页面
                    sendMail(USER_CODE, "请假结果提醒", tipMsg);
                }
                System.out.println("---发送请假结果提醒消息--》" + USER_CODE + tipMsg);
            }
        }
        //借考开始
        if (tipFlag.equals("jkStar")) {
            for (Bean aTipList : tipList) {
                String USER_CODE = aTipList.getStr("USER_CODE");
                String tipMsg = aTipList.getStr("tipMsg");
                String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
                if (confTipType.equals("EMAIL")) {
                    //发送邮件包含的网页页面
                    sendMail(USER_CODE, "借考开始提醒", tipMsg);
                }
                System.out.println("---发送借考开始提醒消息--》" + USER_CODE + tipMsg);
            }
        }
        //借考结果
        if (tipFlag.equals("jkResult")) {
            for (Bean aTipList : tipList) {
                String USER_CODE = aTipList.getStr("USER_CODE");
                String tipMsg = aTipList.getStr("tipMsg");
                String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
                if (confTipType.equals("EMAIL")) {
                    //发送邮件包含的网页页面
                    sendMail(USER_CODE, "借考结果提醒", tipMsg);
                }
                System.out.println("---发送借考结果提醒消息--》" + USER_CODE + tipMsg);
            }
        }
        //报名开始
        if (tipFlag.equals("bmStar")) {
            for (Bean aTipList : tipList) {
                String USER_CODE = aTipList.getStr("USER_CODE");
                String tipMsg = aTipList.getStr("tipMsg");
                String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
                if (confTipType.equals("EMAIL")) {
                    //发送邮件包含的网页页面
                    sendMail(USER_CODE, "报名开始提醒", tipMsg);
                }
                System.out.println("---发送报名开始提醒消息--》" + USER_CODE + tipMsg);
            }
        }
        //报名截止
        if (tipFlag.equals("bmEnd")) {
            for (Bean aTipList : tipList) {
                String USER_CODE = aTipList.getStr("USER_CODE");
                String tipMsg = aTipList.getStr("tipMsg");
                String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
                if (confTipType.equals("EMAIL")) {
                    //发送邮件包含的网页页面
                    sendMail(USER_CODE, "报名截止提醒", tipMsg);
                }
                System.out.println("---发送报名截止提醒消息--》" + USER_CODE + tipMsg);
            }
        }
        //考场座位公示
        if (tipFlag.equals("kczwShow")) {
            for (Bean aTipList : tipList) {
                String USER_CODE = aTipList.getStr("USER_CODE");
                String tipMsg = aTipList.getStr("tipMsg");
                String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
                if (confTipType.equals("EMAIL")) {
                    //发送邮件包含的网页页面
                    sendMail(USER_CODE, "考场座位公示提醒", tipMsg);
                }
                System.out.println("---发送考场座位公示提醒消息--》" + USER_CODE + tipMsg);
            }
        }
        //准考证开始打印
        if (tipFlag.equals("zkzStar")) {
            for (Bean aTipList : tipList) {
                String USER_CODE = aTipList.getStr("USER_CODE");
                String tipMsg = aTipList.getStr("tipMsg");
                String confTipType = Context.getSyConf("TIP_SEND_TYPE", "EMAIL");//获取系统配置的提醒方式，缺省为邮件
                if (confTipType.equals("EMAIL")) {
                    //发送邮件包含的网页页面
                    sendMail(USER_CODE, "准考证开始打印提醒", tipMsg);
                }
                System.out.println("---发送准考证开始打印提醒消息--》" + USER_CODE + tipMsg);
            }
        }
        log.error("--------------结束执行发送邮件提醒list类------------");
        return new OutBean().set("OK", "消息提醒发送成功");
    }

}
