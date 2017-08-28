package com.rh.ts.qjlb;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;

public class QjlbServ extends CommonServ {
	/**
	 * 请假列表的新增以及待办事项的新增
	 * @param paramBean
	 * @return
	 */
	public void addData(Bean paramBean){
		String servId=paramBean.getStr(Constant.PARAM_SERV_ID);
		//获取前台传过来的值
		String user_name = paramBean.getStr("user_name");
		String qj_title = paramBean.getStr("qjtitle");
		String bu_men = paramBean.getStr("bumen");
		String qj_reason = paramBean.getStr("qjreason");
		String user_code = paramBean.getStr("user_code");
		String bmids = paramBean.getStr("bmids");
		String[] bmcodes = bmids.split(",");
		//获取项目id
		String xm_ids=bmcodes[0];
		//请假bean
		Bean qjbean=new Bean();
		qjbean.set("QJ_NAME",user_name);
		qjbean.set("QJ_TITLE",qj_title);
		qjbean.set("QJ_REASON",qj_reason);
		qjbean.set("QJ_DANWEI",bu_men);
		qjbean.set("QJ_KSNAME",bmids);
		qjbean.set("USER_CODE",user_code);
		qjbean.set("XM_ID",xm_ids);
		Bean qjbeans = ServDao.create(servId, qjbean);
		//获取到请假id
		String data_id= qjbeans.getStr("QJ_ID");
		//获取请假表单对象
		Bean qjbd = ServDao.find("TS_QJLB_QJ",data_id);
		String send_name = qjbd.getStr("QJ_NAME");//发送人姓名
		String send_time = qjbd.getStr("S_ATIME");//发送时间
		String send_user = qjbd.getStr("USER_CODE");//发送人编码
		String send_dept = qjbd.getStr("S_DEPT");//发送人部门编码
		String send_dept_name = qjbd.getStr("QJ_DANWEI");//发送人部门名称
		String title = qjbd.getStr("QJ_TITLE");//发送人姓名
		//待办bean
		Bean dbBean =new Bean();
		dbBean.set("SEND_NAME",send_name);
		dbBean.set("SEND_TIME",send_time);
		dbBean.set("SEND_DEPT",send_dept);
		dbBean.set("SEND_USER",send_user);
		dbBean.set("SEND_DEPT_NAME",send_dept_name);
		dbBean.set("DATA_ID",data_id);
		dbBean.set("TITLE",title);
		dbBean.set("TYPE","0");
		ServDao.save("TS_COMM_TODO", dbBean);
		//获取到报名主键集合
		for (String string : bmcodes) {
		//根据服务id 主键id获取 当前资格考试报名的服务
		Bean bmbean = ServDao.find("TS_BMLB_BM", string);
		String bm_type = bmbean.getStr("BM_TYPE");
		String bm_xl = bmbean.getStr("BM_XL");
		String bm_mk = bmbean.getStr("BM_MK");
		String xm_id = bmbean.getStr("XM_ID");
		String lm_title = bm_type+"-"+bm_xl+"-"+bm_mk;
		Bean beans = new Bean();
		beans.set("LB_TITLE",lm_title);
		beans.set("BM_ID",string);
		beans.set("XM_ID",xm_id);
		ServDao.save("TS_QJLB_BM", beans);
		}
	}
	/**
	 * 请假列表的查看
	 * @param paramBean
	 * @return
	 */
	public void updateData(Bean paramBean){
		String servId=paramBean.getStr(Constant.PARAM_SERV_ID);
		//获取前台传过来的值
		String qj_status = paramBean.getStr("qjstatus");
		String qj_id = paramBean.getStr("qjid");
		String sh_status = paramBean.getStr("shstatus");
		String sh_reason = paramBean.getStr("shreason");
		String user_longin = paramBean.getStr("userloginname");
		String s_dname = paramBean.getStr("deptname");
		String s_uname = paramBean.getStr("usercode");
		Bean qjbean=ServDao.find("TS_QJLB_QJ",qj_id);
		//修改数据
		qjbean.set("QJ_STATUS",qj_status);
		ServDao.update(servId, qjbean);
		//添加审核意见信息
	 	Bean shyjBean =new Bean();
	 	shyjBean.set("MIND_CONTENT", sh_reason);
//	 	shyjBean.set("SH_NODE", sh_reason);
//	 	shyjBean.set("SH_LEVEL", sh_reason);
	 	shyjBean.set("SH_STATUS", sh_status);
	 	shyjBean.set("DATA_ID", qj_id);
	 	shyjBean.set("S_DNAME", s_dname);
	 	shyjBean.set("S_UNAME", s_uname);
	 	shyjBean.set("USER_LOGIN", user_longin);
	 	shyjBean.set("MIND_CONTENT", sh_reason);
	 	ServDao.save("TS_COMM_MIND", shyjBean);
	}
}