package com.rh.ts.xmgl;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.ts.util.TsConstant;

/**
 * 项目状态进度-扩展类
 * @author wanglida
 */
public class XmztServ extends CommonServ {

	/**
	 * 项目状态的设置，传入parameBan 将对应的所有的数据更改结果。
	 * @param 前端传入bean
	 * @return 保存或更改首页显示状态后返回的bean
	 */
	public Bean modifyShowType(ParamBean paramBean) {
		//获取当前登录用户的人力资源编码
		String user_work_num = paramBean.getStr("USER_WORK_NUM");
		String xm_id = paramBean.getStr("XM_ID");
		//查询到当前用户所有的进行中的和进行过的项目，将状态设置为首页不显示
		ParamBean param = new ParamBean();
		param.set("STR1", user_work_num);
		List<Bean> list = ServDao.finds("TS_XMZT", param);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).set("INT1", 0);
			ServDao.save("TS_XMZT", list.get(i));
		}
		//通过编码查询到TS_OBJECT表中的记录，获取到用户对应表的ID
		ParamBean param1 = new ParamBean();
		param1.set("STR1", user_work_num);
		Bean result1 = ServDao.find("TS_XMZT", param1);
		Object TR_ID = result1.get("ID");
		
		//通过id查询到记录，然后修改其中首页显示的状态INT1字段。
		ParamBean param2 = new ParamBean();
		param2.set("ID", TR_ID);
		Bean resultBean = ServDao.find("TS_XMZT", param2);
		resultBean.set("INT1", 1);
		//更改对应用户的项目ID即可
		resultBean.set("DATA_ID", xm_id);
		Bean bean = ServDao.save(TsConstant.SERV_OBJECT, resultBean);
		return bean;
	}
}
