package com.rh.core.icbc.wf;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.ParamBean;

/**
 * 
 * @author yangjy
 *
 */
public class ApplicationCodeRule extends CodeRule {

	private int numberLength = 7;
	private String prefix = null;
	private String sequenceName = null;

	/**
	 * 
	 * @param prefix
	 *            前缀
	 * @param sequenceName
	 *            Sequence名称
	 */
	public ApplicationCodeRule(String prefix, String sequenceName) {
		this.prefix = prefix;
		this.sequenceName = sequenceName;
	}

	/**
	 * 
	 * @return
	 */
	public int getNumberLength() {
		return numberLength;
	}

	public void setNumberLength(int numberLength) {
		this.numberLength = numberLength;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	@Override
	public String createCode(ParamBean paramBean) {
		if (StringUtils.isBlank(prefix)) {
			throw new TipException("编号前缀 不能为空");
		}
		StringBuilder result = new StringBuilder();
		result.append(this.prefix.toUpperCase());
		result.append(getDate());
		result.append(getNum());

		if (result.length() > 19) {
			throw new TipException("编号的长度不能超过19位");
		}

		return result.toString();
	}

	/**
	 * 
	 * @return sequence的下一个值
	 */
	private int getSequence() {
		if (StringUtils.isBlank(this.sequenceName)) {
			throw new TipException("sequenceName 不能为空");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select ").append(getSequenceName()).append(".nextVal \"VALUE\" FROM DUAL");
		List<Bean> list = Transaction.getExecutor().query(sql.toString());

		Bean result = list.get(0);

		if (result != null) {
			return result.getInt("VALUE");
		}

		throw new TipException("无效的Sequence。");
	}

	/**
	 * 
	 * @return 取得当天的日期
	 */
	private String getDate() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(calendar.getTime());
	}

	private String getNum() {
		int num = getSequence();
		String strNum = String.valueOf(num);
		int count = numberLength - strNum.length();

		for (int i = 0; i < count; i++) {
			strNum = "0" + strNum;
		}

		return strNum;
	}
}
