package com.rh.ts.xmgl.kcap;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;

public class KcapResource {

	private List<Bean> passList = null;

	/**
	 * 考场 key:场次 value:bean( [gljg:list]关联机构 ,[xxxx:xxxx]考场其他信息 )
	 */
	private Bean kcBean = null;

	/**
	 * 考生 key:考生机构 value:list
	 */
	private Bean ksBean = null;

	/**
	 * 机构 key:考生机构，value:下级部门
	 */
	private Bean ksOrg = null;

	public KcapResource(String xmId) {
		//考生信息
		loadKs(xmId);
		//考场信息
		loadKc(xmId);
	}

	private void loadKs(String xmId) {

		ksOrg = new Bean();

		ksBean = new Bean();

		SqlBean sql = new SqlBean();
		sql.and("XM_ID", xmId);
		sql.and("S_FLAG", 1);

		passList = ServDao.finds(TsConstant.SERV_BMSH_PASS, sql);

		for (Bean pass : passList) {

			String oDCode = pass.getStr("S_ODEPT");

			List<Bean> ksList = null;

			if (ksBean.containsKey(oDCode)) {

				ksList = ksBean.getList(ksBean);

				ksList.add(pass);

			} else {

				ksList = new ArrayList<Bean>();

				ksList.add(pass);
			}

			if (ksList != null) {

				ksBean.set(oDCode, ksList);
			}

			// ----------考生机构关系---------------------
			
			String tDCode = pass.getStr("S_TDEPT");

			String dCode = pass.getStr("S_DEPT");
			
			Bean temp = new Bean();

			if (dCode.equals(tDCode)) {

				if (ksOrg.containsKey(oDCode)) {

					temp = ksOrg.getBean(oDCode);
				}

				temp.set(dCode, new Bean());

				ksOrg.set(oDCode, temp);

			} else {

				if (ksOrg.containsKey(oDCode)) {

					temp = ksOrg.getBean(oDCode);
				}

				Bean d = new Bean();

				d.set(dCode, new Bean());

				temp.set(tDCode, d);

				ksOrg.set(oDCode, temp);

			}

		}

	}

	private void loadKc(String xmId) {

		List<Object> values = new ArrayList<>();
		
		values.add(xmId);
		
		String sql = "select k.KC_ID,K.KC_ODEPTCODE,K.KC_ODEPTNAME,k.KC_SCORE,k.KC_STATE,k.KC_MAX,k.KC_GOOD,k.KC_LEVEL,c.CC_ID,c.XM_ID from TS_XMGL_KCAP_DAPCC c LEFT JOIN ts_kcgl k on k.kc_id = c.kc_id where a.XM_ID=? ";
		
		List<Bean> list = Transaction.getExecutor().query(sql, values);

		for (Bean item : list) {
			
			String ccId = item.getId();
			
			List<Bean> list2 = ServDao.finds("TS_XMGL_KCAP_DAPCC_CCSJ", "and CC_ID = '" + ccId + "'");
			
			Bean kcInfo = new Bean();
			
			kcInfo.set("INFO", item);
			kcInfo.set("CC", list2);
			
			kcBean.set(item.getStr("KC_ODEPTCODE"), kcInfo);
		}
	}

	public Bean getKcBean() {
		return kcBean;
	}

	public Bean getKsBean() {
		return ksBean;
	}

	public Bean getKsOrg() {
		return ksOrg;
	}

}
