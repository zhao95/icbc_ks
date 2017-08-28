package com.rh.core.wfe.serv;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;

/**
 * 默认的ITrackQuery实现
 * 配置：SY_TRACK_QUERY_INTERFACE用来覆盖该类
 */
public class DefaultTrackQuery implements ITrackQuery {

	@Override
	public void beforeQuery(ParamBean paramBean) {
		
	}

	@Override
	public void afterQuery(ParamBean paramBean, OutBean dataBean) {
		String pk = paramBean.getId();
		
		List<Bean> list = dataBean.getDataList();
		for (Bean data : list) {
			//添加节点意见信息
			SqlBean findBean = new SqlBean()
					.selects("MIND_CONTENT")
					.and("DATA_ID", pk)
					.and("WF_NI_ID", data.getStr("NI_ID"));
			Bean mindBean = ServDao.find(ServMgr.SY_COMM_MIND, findBean);
			if (mindBean != null) {
				data.set("MIND_CONTENT", mindBean.getStr("MIND_CONTENT"));
			}
		}
	}

}
