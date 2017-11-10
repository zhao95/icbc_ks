package com.rh.core.icbc.basedata;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.OutBean;
/**
 * 提醒信息发送接口
 * @author leader
 *
 */
public class KSSendTipMessageServ {
	/**
	 * 发送提醒消息接口类，对接工行发送提醒的类型（融易联短信，邮件，腾讯通）
	 * @param tipBean 参数为信息数据bean，包含发送消息提醒语tipMsg，人员编码USER_CODE等详情参照该方法内对照字段
	 * @param tipFlag 提醒类型区分（qjStar 请假开始，qjResult 请假结果，jkStar 借考开始，jkResult 借考结果，bmStar 报名开始，bmEnd 报名截止，kczwShow 考场座位公示，zkzStar 准考证开始打印）
	 * @return
	 */
		public OutBean sendTipMessageBeanForICBC(Bean tipBean , String tipFlag){
			//请假开始
			if(tipFlag.equals("qjStar")){
				String USER_CODE = tipBean.getStr("USER_CODE");
				String tipMsg = tipBean.getStr("tipMsg");
				System.out.println("---发送请假开始提醒消息--》"+USER_CODE+tipMsg);
			}
			//请假结果
			if(tipFlag.equals("qjResult")){
				String USER_CODE = tipBean.getStr("USER_CODE");
				String tipMsg = tipBean.getStr("tipMsg");
				System.out.println("---发送请假结果提醒消息--》"+USER_CODE+tipMsg);
			}
			//借考开始
			if(tipFlag.equals("jkStar")){
				String USER_CODE = tipBean.getStr("USER_CODE");
				String tipMsg = tipBean.getStr("tipMsg");
				System.out.println("---发送借考开始提醒消息--》"+USER_CODE+tipMsg);
			}
			//借考结果
			if(tipFlag.equals("jkResult")){
				String USER_CODE = tipBean.getStr("USER_CODE");
				String tipMsg = tipBean.getStr("tipMsg");
				System.out.println("---发送借考结果提醒消息--》"+USER_CODE+tipMsg);
			}
			//报名开始
			if(tipFlag.equals("bmStar")){
				String USER_CODE = tipBean.getStr("USER_CODE");
				String tipMsg = tipBean.getStr("tipMsg");
				System.out.println("---发送报名开始提醒消息--》"+USER_CODE+tipMsg);
			}
			//报名截止
			if(tipFlag.equals("bmEnd")){
				String USER_CODE = tipBean.getStr("USER_CODE");
				String tipMsg = tipBean.getStr("tipMsg");
				System.out.println("---发送报名截止提醒消息--》"+USER_CODE+tipMsg);
			}
			//考场座位公示
			if(tipFlag.equals("kczwShow")){
				String USER_CODE = tipBean.getStr("USER_CODE");
				String tipMsg = tipBean.getStr("tipMsg");
				System.out.println("---发送考场座位公示提醒消息--》"+USER_CODE+tipMsg);
			}
			//准考证开始打印
			if(tipFlag.equals("zkzStar")){
				String USER_CODE = tipBean.getStr("USER_CODE");
				String tipMsg = tipBean.getStr("tipMsg");
				System.out.println("---发送准考证开始打印提醒消息--》"+USER_CODE+tipMsg);
			}
			
			return new OutBean().set("OK","消息提醒发送成功");
			
		}
		/**
		 * 发送提醒消息接口，对接工行发送提醒的接口（融易联短信，邮件，腾讯通）
		 * @param tipList 参数为信息数据集，集合中的bean包含发送消息提醒语tipMsg，人员编码USER_CODE其余字段要在该方法内寻找匹配键值
		 * @param tipFlag 提醒类型区分（qjStar 请假开始，qjResult 请假结果，jkStar 借考开始，jkResult 借考结果，bmStar 报名开始，bmEnd 报名截止，kczwShow 考场座位公示，zkzStar 准考证开始打印）
		 */
		public OutBean sendTipMessageListForICBC(List<Bean> tipList , String tipFlag){
			//请假开始
			if(tipFlag.equals("qjStar")){
				for (Bean aTipList : tipList) {
					String USER_CODE = aTipList.getStr("USER_CODE");
					String tipMsg = aTipList.getStr("tipMsg");
					System.out.println("---发送请假开始提醒消息--》" + USER_CODE + tipMsg);
				}
			}
			//请假结果
			if(tipFlag.equals("qjResult")){
				for (Bean aTipList : tipList) {
					String USER_CODE = aTipList.getStr("USER_CODE");
					String tipMsg = aTipList.getStr("tipMsg");
					System.out.println("---发送请假结果提醒消息--》" + USER_CODE + tipMsg);
				}
			}
			//借考开始
			if(tipFlag.equals("jkStar")){
				for (Bean aTipList : tipList) {
					String USER_CODE = aTipList.getStr("USER_CODE");
					String tipMsg = aTipList.getStr("tipMsg");
					System.out.println("---发送借考开始提醒消息--》" + USER_CODE + tipMsg);
				}
			}
			//借考结果
			if(tipFlag.equals("jkResult")){
				for (Bean aTipList : tipList) {
					String USER_CODE = aTipList.getStr("USER_CODE");
					String tipMsg = aTipList.getStr("tipMsg");
					System.out.println("---发送借考结果提醒消息--》" + USER_CODE + tipMsg);
				}
			}
			//报名开始
			if(tipFlag.equals("bmStar")){
				for (Bean aTipList : tipList) {
					String USER_CODE = aTipList.getStr("USER_CODE");
					String tipMsg = aTipList.getStr("tipMsg");
					System.out.println("---发送报名开始提醒消息--》" + USER_CODE + tipMsg);
				}
			}
			//报名截止
			if(tipFlag.equals("bmEnd")){
				for (Bean aTipList : tipList) {
					String USER_CODE = aTipList.getStr("USER_CODE");
					String tipMsg = aTipList.getStr("tipMsg");
					System.out.println("---发送报名截止提醒消息--》" + USER_CODE + tipMsg);
				}
			}
			//考场座位公示
			if(tipFlag.equals("kczwShow")){
				for (Bean aTipList : tipList) {
					String USER_CODE = aTipList.getStr("USER_CODE");
					String tipMsg = aTipList.getStr("tipMsg");
					System.out.println("---发送考场座位公示提醒消息--》" + USER_CODE + tipMsg);
				}
			}
			//准考证开始打印
			if(tipFlag.equals("zkzStar")){
				for (Bean aTipList : tipList) {
					String USER_CODE = aTipList.getStr("USER_CODE");
					String tipMsg = aTipList.getStr("tipMsg");
					System.out.println("---发送准考证开始打印提醒消息--》" + USER_CODE + tipMsg);
				}
			}
			
			return new OutBean().set("OK","消息提醒发送成功");
		}
		
}
