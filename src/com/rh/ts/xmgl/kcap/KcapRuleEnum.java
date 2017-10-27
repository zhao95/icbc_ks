package com.rh.ts.xmgl.kcap;

public enum KcapRuleEnum {

	/**
	 * 相同考试前后左右不相邻
	 */
	R001(1, "R001"),

	/**
	 * 同一考生同一场场次连排
	 */
	R002(2, "R002"),

	/**
	 * 距离远近规则
	 */
	R003(3, "R003"),

	/**
	 * 同一网点级机构考生均分安排
	 */
	R004(4, "R004"),

	/**
	 * 来自同一机构考生不连排
	 */
	R005(5, "R005"),

	/**
	 * 考生人数少于机器数一半时，考生左右间隔不低于1个座位，前后不低于1个
	 */
	R006(6, "R006"),

	/**
	 * 领导职务考生座位靠前安排
	 */
	R007(7, "R007"),

	/**
	 * 特定机构考生场次先后安排
	 */
	R008(8, "R008"),

	/**
	 * 特定考试仅限于省分行安排
	 */
	R009(9, "R009"),
	
	/**
	 * 最少考场，最少场次
	 */
	S001(100,"S001"),
	
	/**
	 * 无符合规则考生 是否强制安排
	 */
	S002(110,"S002");

	private int code;
	private String name;

	private KcapRuleEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getCode() {
		return code;
	}
}
