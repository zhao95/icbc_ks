package com.rh.ts.xmglsz;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;

/**
 * 项目管理设置
 * 
 * @author
 *
 */
public class XmglszServ extends CommonServ {

	public OutBean findByXmid(Bean paramBean) {
		OutBean result = new OutBean();
		List<Bean> list = ServDao.finds("TS_XMGL_SZ", paramBean);

		result.set("resList", list);
		return result;

	}

	public OutBean findBmId(Bean paramBean) {
		OutBean out = new OutBean();
		String XM_SZ_ID = paramBean.getId();
		String where = " and XM_SZ_ID='" + XM_SZ_ID + "'";
		List<Bean> listBmgl = ServDao.finds("TS_XMGL_BMGL", where);
		if (listBmgl.isEmpty()) {
			// 返回一个可以新建卡片的
		} else {
			for (int i = 0; i < listBmgl.size(); i++) {
				String BMID = listBmgl.get(i).getStr("BM_ID");
			}
		}
		// ServDao.save(servId, dataBean)
		return null;

	}

	public OutBean existSH(Bean paramBean) {

		OutBean out = new OutBean();

		String xmId = paramBean.getStr("XM_ID");

		SqlBean sql = new SqlBean();

		sql.and("XM_ID", xmId);

		sql.and("S_FLAG", 1);

		List<Bean> list = ServDao.finds(TsConstant.SERV_BMSH, sql);

		if (list != null && list.size() > 0) {
			Bean bean = list.get(0);
			int zd = bean.getInt("SH_ZDSH");
			int rg = bean.getInt("SH_RGSH");

			if (zd == 1 && rg == 1) { // 自动和人工
				out.setCount(3);
			} else if (zd == 1) { // 自动审核
				out.setCount(1);
			} else if (rg == 1) { // 人工审核
				out.setCount(2);
			}
		} else {
			out.setCount(0); // 无审核
		}

		return out;
	}

	public OutBean existModule(Bean paramBean) {

		OutBean out = new OutBean();

		String szName = paramBean.getStr("XM_SZ_NAME");

		String xmId = paramBean.getStr("XM_ID");

		SqlBean sql = new SqlBean();

		sql.and("XM_SZ_NAME", szName);

		sql.and("XM_ID", xmId);

		sql.and("S_FLAG", 1);

		int count = ServDao.count(TsConstant.SERV_XMGL_SZ, sql);

		if (count > 0) {
			out.setCount(count);
		}

		return out;
	}

}
