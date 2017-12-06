package com.rh.ts.xmgl;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.ts.util.TsConstant;

public class ArrangeServ extends CommonServ {

	protected void beforeDelete(ParamBean paramBean) {

		List<String> delKcapCcsj = new ArrayList<String>();

		List<Bean> delDatas = paramBean.getDeleteDatas();

		for (Bean bean : delDatas) {

			String arrId = bean.getId();

			String xmId = bean.getStr("XM_ID");

			SqlBean sql = new SqlBean();

			sql.selects("a.ARR_CC AS SJ_CC,b.CC_ID ");

			sql.tables("TS_XMGL_CCCS_ARRANGE a, TS_XMGL_KCAP_DAPCC b");

			String wh = " AND a.XM_ID = b.XM_ID ";

			sql.appendWhere(wh);

			sql.and("a.XM_ID", xmId);
			sql.and("a.ARR_ID", arrId);

			String where = "select " + sql.getStr(Constant.PARAM_SELECT) + " from " + sql.getStr(Constant.PARAM_TABLE);

			where += " where 1=1 " + sql.getWhere();

			List<Bean> cclist = Transaction.getExecutor().query(where, sql.getVars());

			for (Bean cc : cclist) {

				String sjCC = cc.getStr("SJ_CC");

				String ccId = cc.getStr("CC_ID");

				SqlBean sjsql = new SqlBean();

				sjsql.and("SJ_CC", sjCC);
				sjsql.and("CC_ID", ccId);

				List<Bean> sjlist = ServDao.finds(TsConstant.SERV_KCAP_CCSJ, sjsql);

				for (Bean sj : sjlist) {

					delKcapCcsj.add(sj.getId());
				}
			}
		}

		paramBean.set("__DEL_KCAP_CCSJ", delKcapCcsj);
	}

	/**
	 * 删除场次测算大时间段，关联删除考场安排的关联场次
	 */
	protected void afterDelete(ParamBean paramBean, OutBean outBean) {

		List<String> list = paramBean.getList("__DEL_KCAP_CCSJ");

		for (String id : list) {
			
			ParamBean param = new ParamBean();
			param.set("SJ_ID", id);
			param.setId(id);
			@SuppressWarnings("unused")
			OutBean out = ServMgr.act(TsConstant.SERV_KCAP_CCSJ, ServMgr.ACT_DELETE, param);
			
//			log.debug(out.toString());
		}
	}

}
