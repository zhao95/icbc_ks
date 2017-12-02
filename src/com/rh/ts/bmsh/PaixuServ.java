package com.rh.ts.bmsh;

import java.util.List;


import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;

/**
 * 保存当前用户 的 审核排序
 * 
 * @author shiyun
 *
 */
public class PaixuServ extends CommonServ {
	/**
	 * 对th进行 排序保存
	 * 
	 * @param paramBean
	 */
	public Bean paixu(Bean paramBean) {
		String user_code = paramBean.getStr("user_code");
		String xuhao = paramBean.getStr("xuhao");
		String name = paramBean.getStr("name");
		String icode = paramBean.getStr("id");
		// 两个字段可以确认 一条记录 唯一的
		String where = "AND USER_CODE = " + "'" + user_code
				+ "' order by cast(PX_XUHAO as SIGNED)";
		if ("1".equals(xuhao)) {
			// 删除以前的数据重新加载
			List<Bean> list = ServDao.finds("TS_BMSH_PX", where);
			if (list.size() != 0) {
				for (Bean bean : list) {
					ServDao.delete("TS_BMSH_PX", bean);
				}
			}
		}
		Bean dataBean = new Bean();
		dataBean.set("PX_XUHAO", xuhao);
		dataBean.set("PX_COLUMN", icode);
		dataBean.set("PX_NAME", name);
		dataBean.set("USER_CODE", user_code);
		ServDao.save("TS_BMSH_PX", dataBean);
		return new OutBean();
	}

	/**
	 * 所有的排序
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getShenhelist(Bean paramBean) {
		Bean outBean = new Bean();
		String where1 = "AND USER_CODE is null order by cast(PX_XUHAO as SIGNED)";
		// 默认没有保存过 的排序
		List<Bean> listall = ServDao.finds("TS_BMSH_PX", where1);

		outBean.set("list", listall);
		return outBean;
	}

	/**
	 * user_code 下的排序显示 已保存的排序 进行回显
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean getUserList(Bean paramBean) {
		String user_code = paramBean.getStr("user_code");
		String where = "AND USER_CODE=" + "'" + user_code
				+ "' order by cast(PX_XUHAO as SIGNED)";
		List<Bean> list = ServDao.finds("TS_BMSH_PX", where);
		Bean outBean = new Bean();

		outBean.set("list", list);

		return outBean;
	}

	/*
	 * firefly调用 返回json格式
	 */
	public Bean getShenheJson(Bean paramBean) {
		String user_code = paramBean.getStr("user_code");
		String where = "AND USER_CODE=" + "'" + user_code
				+ "' order by cast(PX_XUHAO as SIGNED)";
		List<Bean> list = ServDao.finds("TS_BMSH_PX", where);
		Bean outBean = new Bean();
		if (list.size() == 0) {
			String where1 = "AND USER_CODE is null order by cast(PX_XUHAO as SIGNED) limit 0,5";
			// 默认没有保存过 的排序
			List<Bean> listall = ServDao.finds("TS_BMSH_PX", where1);

			
			outBean.set("list", listall);
		} else {
			
			outBean.set("list", list);
		}
		return outBean;

	}
}
