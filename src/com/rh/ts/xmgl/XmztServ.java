package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.ts.util.TsConstant;

/**
 * 项目状态进度-扩展类
 * @author wanglida
 *
 */
public class XmztServ extends CommonServ{
	/**
	 * 项目状态专用。
	 * 前端传入的数据，判断是新增还是修改，之后存入公用的辅助表TS_OBJECT中
	 * 
	 * @param paramBean
	 * @return
	 */
	public Bean  saveAndAddDataToObject_Table(ParamBean paramBean){
		//判断传入的数据中是否有ID，有则修改，无则新增
		
		//获取前台传回的数据，并将数据分类取出，存入到指定的bean里面，用于保存或者添加数据
		//获取ID，有则修改，为空则新增
		String id = paramBean.getId();
		//获取服务id
		String servId = paramBean.getServId();
		//获取登录用户的人力资源编号
		String user_work_num = paramBean.getStr("USER_WORK_NUM");
		//获取项目ID
		String xm_id = paramBean.getStr("XM_ID");
		//获取是否展示在首页的标志数据
		int show_type = Integer.parseInt(paramBean.getStr("SHOW_TYPPE"));
		//新建用来存储传递参数的bean
		OutBean param = new OutBean();
		param.set("ID", id);
		param.set("STR1", user_work_num);
		param.set("DATA_ID", xm_id);
		param.set("INIT1", show_type);
		//调用数据库交互的方法，将数据存入数据库, 使用已经配置好的类里面的服务id，获取到TS_OBJECT服务  
		Bean outBean = ServDao.save(TsConstant.SERV_OBJECT, param);
		
		return outBean;
	}
	
}
