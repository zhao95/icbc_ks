package com.rh.ts.xmgl.kcap;

public enum KcapRuleEnum {

	/**
	 * 相同考试前后左右不相邻
	 */
	R001("R001", "1"),

	/**
	 * 同一考生同一场场次连排
	 */
	R002("R002", "2"),

	/**
	 * 距离远近规则
	 */
	R003("R003", "3"),

	/**
	 * 同一网点级机构考生均分安排
	 */
	R004("R004", "4"),

	/**
	 * 来自同一机构考生不连排
	 */
	R005("R005", "5"),

	/**
	 * 考生人数少于机器数一半时，考生左右间隔不低于1个座位，前后不低于1个
	 */
	R006("R006", "6"),

	/**
	 * 领导职务考生座位靠前安排
	 */
	R007("R007", "7"),

	/**
	 * 特定机构考生场次先后安排
	 */
	R008("R008", "8"),

	/**
	 * 特定考试仅限于省分行安排
	 */
	R009("R009", "9"),

	/**
	 * 最少考场，最少场次
	 */
	S001("S001", "100"),

	/**
	 * 无符合规则考生 是否强制安排
	 */
	S002("S002", "110");

	private String code;
	private String name;

	private KcapRuleEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}
}
