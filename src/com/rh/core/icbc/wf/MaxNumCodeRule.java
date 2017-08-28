package com.rh.core.icbc.wf;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.DateUtils;

/**
 * 
 * @author yangjy
 *
 */
public class MaxNumCodeRule extends CodeRule {

	/**
	 * 审批单字段：流水号 要生成流水号的审批单都应该加上该字段
	 */
	public static final String S_CODE = "S_CODE";

	private String codePrefix = "";
	private int numLength = 7;

	public MaxNumCodeRule(String prefix, int numLength) {
		this.codePrefix = prefix;
		this.numLength = numLength;
	}

	/**
	 * 生成流水号 生成规则：流程简称＋日期时间＋序列号
	 * 
	 */
	@Override
	public String createCode(ParamBean paramBean) {
		String rtnCode = "";

		// 1.查询该生成规则下最大的流水号
		SqlBean findBean = new SqlBean().selects("max(" + S_CODE + ") " + S_CODE).andLikeRT(S_CODE, codePrefix);
		Bean result = ServDao.find(paramBean.getServId(), findBean);
		String maxCode = result.getStr(S_CODE);

		// 2.第一次生成
		if (StringUtils.isEmpty(maxCode)) {
			rtnCode = codePrefix + DateUtils.getCustomDateTime("yyyyMMdd") + getSpecificCodeByLength(1);
		} else {
			// 3.取得当前流水号
			maxCode = maxCode.substring(codePrefix.length() + "yyyyMMdd".length());
			rtnCode = codePrefix + DateUtils.getCustomDateTime("yyyyMMdd")
					+ getSpecificCodeByLength(Integer.parseInt(maxCode) + 1);
		}

		return rtnCode;
	}

	/**
	 * 根据流水号长度取得真正的流水号
	 */
	private String getSpecificCodeByLength(int code) {
		StringBuilder sb = new StringBuilder();
		int diffLength = numLength - String.valueOf(code).length();
		for (int i = 0; i < diffLength; i++) {
			sb.append(0);
		}
		sb.append(code);

		return sb.toString();
	}

}
