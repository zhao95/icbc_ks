package com.rh.ts.xmgl.kcap;

public enum KcapRuleEnum {
	/**
	 * 相同考试前后左右不相邻
	 */
	R001,
	
	/**
	 * 同一考生同一场场次连排
	 */
	R002,
	
	/**
	 * 距离远近规则
	 */
	R003,
	
	/**
	 * 同一网点级机构考生均分安排
	 */
	R004,
	
	/**
	 * 来自同一机构考生不连排
	 */
	R005,
	
	/**
	 * 考生人数少于机器数一半时，考生左右间隔不低于1个座位，前后不低于1个
	 */
	R006,
	
	/**
	 * 领导职务考生座位靠前安排
	 */
	R007,
	
	/**
	 * 特定机构考生场次靠后安排
	 */
	R008,
	
	/**
	 * 特定考试仅限于省分行安排
	 */
	R009;
	
	public static void main(String[] a){
		System.out.println(KcapRuleEnum.R001);
		System.out.println(KcapRuleEnum.R001.name());
		System.out.println(KcapRuleEnum.R001.ordinal());
	}
}
