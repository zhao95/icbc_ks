package com.rh.ts.xmgl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
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
			list.get(i).set("OBJ_INT1", 0);
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
		resultBean.set("OBJ_INT1", 1);
		//更改对应用户的项目ID即可
		resultBean.set("DATA_ID", xm_id);
		Bean bean = ServDao.save(TsConstant.SERV_OBJECT, resultBean);
		return bean;
	}
	
	
	/**
	 * 项目状态使用到的扩展类
	 * 获取到项目管理中考场安排中的考试时间，将对应状态给前端进度条使用
	 *	@param xm_id
	 *	@return 字符串类型的考试时间对应的状态
	 */
	public Bean getXMExamTime(ParamBean paramBean){
		String xm_id =paramBean.getStr("XM_ID");
		Bean findXMBean = ServDao.find("TS_XMGL", xm_id);
		if(findXMBean==null){
			return null;
		}
		//创建查询语句，左连接查询考试时间，并将查询到的考试结束的时间从大到小排序。
		String sql = "SELECT b.SJ_START,b.SJ_END "
				+ "FROM TS_XMGL_KCAP_DAPCC a LEFT JOIN TS_XMGL_KCAP_DAPCC_CCSJ b on a.CC_ID=b.CC_ID "
				+ "where XM_ID= ? "
				+ "and b.SJ_END is not null "
				+ "ORDER BY b.SJ_END desc";
		List<Object> queryParams=new ArrayList<Object>();
		queryParams.add(xm_id);
		List<Bean> beanList = Transaction.getExecutor().query(sql,queryParams);
		
		//创建查询语句，左连接查询考试时间，并将查询到的考试考试的时间从小到大排序。
		String sql1 = "SELECT b.SJ_START,b.SJ_END "
				+ "FROM TS_XMGL_KCAP_DAPCC a LEFT JOIN TS_XMGL_KCAP_DAPCC_CCSJ b on a.CC_ID=b.CC_ID "
				+ "where XM_ID= ? "
				+ "and b.SJ_END is not null "
				+ "ORDER BY b.SJ_START";
		List<Object> queryParams1=new ArrayList<Object>();
		queryParams1.add(xm_id);
		List<Bean> beanList1 = Transaction.getExecutor().query(sql1,queryParams1);
		Bean returnBean = new Bean();

		if(beanList.size() > 0 && beanList1.size() > 0){
			//获取开始时间和结束时间，并将其格式化后与当前时间比较，返回状态值
			String SJ_START_TIME = beanList1.get(0).getStr("SJ_START");
			String SJ_END_TIME = beanList.get(0).getStr("SJ_END");
			Date currentTime = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date sj_start_Date = null;
			Date sj_end_Date = null;
			Boolean start_flag=true;
			Boolean end_flag=true;
			String divState="";
			try {
				sj_start_Date = sdf.parse(SJ_START_TIME);
				sj_end_Date = sdf.parse(SJ_END_TIME);
				start_flag = currentTime.before(sj_start_Date);
				end_flag = currentTime.before(sj_end_Date);
				if(start_flag){
					divState="未开始";
					returnBean.set("divState", divState);
				}else{
					if(end_flag){
						divState="考试中";
						returnBean.set("divState", divState);
					}else{
						divState="考后请假";
						returnBean.set("divState", divState);
					}
				}
			} catch (ParseException e) {
				throw new TipException("服务器异常，获取动态失败！");
			}
		}else{
			returnBean.set("divState", "未启用");
			return returnBean;
		}
		return returnBean;
		
	}
	
}
