package com.rh.ts.xmgl.rule.impl;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import com.rh.ts.xmgl.rule.IRule;

/**
 * 按要求持证
 * 
 * @author zjl
 *
 */
public class ObtainCert implements IRule {
	
	static final String wyCode = "A000000000000000004"; // 文员序列code
	static final String ywclCode = "A000000000000000005"; // 业务处理序列code
	static final String thgwCode = "A000000000000000016"; // 投行顾问序列code
	static final String yxCode = "A000000000000000006"; // 营销序列code
	static final String dgCode = "A000000000000000022"; // 对公客户经理序列code
	static final String xdCode = "A000000000000000013"; // 信贷序列code

	public boolean validate(Bean param) {

		// 报名等级
		String lvCode = param.getStr("BM_TYPE");

		// 报名者人力资源编码
		String user = param.getStr("BM_CODE");

		// 报名序列
		String xl = param.getStr("BM_XL");


		// 报名类别
//		String lb = param.getStr("BM_LB");

		// 岗位sql
		SqlBean postSql = new SqlBean();

		// 获证sql
		SqlBean certSql = new SqlBean();

		// 人力资源编码; 获证状态(1-正常;2-获取中;3-过期)
		certSql.and("STU_PERSON_ID", user).andNot("QUALFY_STAT", 3);

		// 报考初级无持证要求
		if (lvCode.equals("1")) {
			return true;
		}
		
		if (wyCode.equals(xl)) { // 文员序列各层级资格不设置考试，取得业务类其他各序列同等级专业资格视同取得文员序列专业资格
			xl = ywclCode;
		}

		if (lvCode.equals("2")) { // 报考中级，需要初级资质

			// 投行顾问序列初级资格不设置考试，取得营销、对公客户经理序列初级专业资格，信贷A类初级和信贷B类初级专业资质，可视为取得投行顾问序列初级资格
			if (thgwCode.equals(xl)) {

				postSql.and("POSTION_QUALIFICATION", "1").andIn("POSTION_SEQUENCE_ID", yxCode, dgCode, xdCode);

			} else {

				postSql.and("POSTION_QUALIFICATION", "1").and("POSTION_SEQUENCE_ID", xl);
			}

			certSql.and("CERT_GRADE_CODE", "1");

		} else if (lvCode.equals("3")) { // 报考高级，需要中级资质

			postSql.and("POSTION_QUALIFICATION", "2").and("POSTION_SEQUENCE_ID", xl);

			certSql.and("CERT_GRADE_CODE", "2");

		} else { // 报考专家级，需要高级资质

			postSql.and("POSTION_QUALIFICATION", "3").and("POSTION_SEQUENCE_ID", xl);

			certSql.and("CERT_GRADE_CODE", "3");
		}

		// 获证sql 设置证书模块条件
//		if (xlArg != null && xlArg.length > 0) {
//
//			certSql.andIn("CERT_MODULE_CODE", getMkCodes(lvCode, xlArg));
//
//		} else {
//
//			certSql.andIn("CERT_MODULE_CODE", getMkCodes(lvCode, xl));
//		}

		int certCount = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, certSql);

		if (certCount > 0) {
			return true;
		}

		// List<Bean> postList =  ServDao.finds(TsConstant.SERV_ORG_POSTION,postSql);

		return false;
	}

	/**
	 * 通过证书类别和证书序列 获得证书
	 * 
	 * @param lb
	 * @param xl
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<Bean> getCertInfo(String lb, String xl) {

		SqlBean sql = new SqlBean();
		sql.and("STATION_TYPE", lb);
		sql.and("STATION_NO", xl);
		return ServDao.finds(TsConstant.SERV_ETI_CERT_INFO, sql);
	}

	/**
	 * 根据考试序列 考试级别 获取考试模块
	 * @param lv
	 * @param xl
	 * @return
	 */
	@SuppressWarnings("unused")
	private String[] getMkCodes(String lv, String... xl) {
		String[] mkCodes = new String[100];

		SqlBean sql = new SqlBean();

		sql.and("KSLBK_TYPE", lv); // 级别

		sql.andIn("KSLBK_XL", xl);

		// 考试类别库找到编号
		List<Bean> list = ServDao.finds(TsConstant.SERV_BM_KSLBK, sql);

		for (int i = 0; i < list.size(); i++) {

			String mkCode = list.get(i).getStr("KSLBK_MKCODE");

			mkCodes[i] = mkCode;
		}
		return mkCodes;
	}

}
