package com.rh.core.icbc.imp;

import com.rh.core.icbc.imp.origin.ZDPStruInfoImporter;
import com.rh.core.icbc.imp.origin.ZDStaffBInfoImporter;
import com.rh.core.icbc.imp.origin.ZDStaffContactImporter;
import com.rh.core.icbc.imp.origin.ZDStaffStateImporter;
import com.rh.core.icbc.imp.target.ImpDept;
import com.rh.core.icbc.imp.target.ImpUser;
import com.rh.core.icbc.imp.target.ImpUtils;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.DateUtils;
import com.rh.core.util.threadpool.RhThreadTask;

public class ImpDataTask extends RhThreadTask {

	private static final long serialVersionUID = 1L;

	private static final String FULLDATA = "FULLDATA";
	private static final String ERROR = "ERROR";
	private static final String INCREMENT = "INCREMENT";
	private ParamBean param = null;
	
	public ImpDataTask(ParamBean param) {
		this.param = param;
	}
	
	@Override
	public boolean execute() {
		log.error("数据导入开始！");
		//备份SY_BASE_USER_V和SY_ORG_DEPT表
		ImpUtils.bakOrg();
		//逻辑判断进行数据导入
		impDataToDept(param);
		impDataToUser(param);
		log.error("记录更新数据");
		//记录数据更新
		ImpUtils.compareOrgInsertLog();
		log.error("更新SY_ORG_ADDRESS_V表");
		ImpUtils.createTableFromView();
		log.error("导入数据结束！");
		return true;
	}
	
	private void impDataToDept(ParamBean param ){
		log.error("部门数据导入。。。。");
		String struInfoUrl = param.getStr("STRUINFO_URL");
		//导入全量
		String fdResult = new ZDPStruInfoImporter().impFullData(struInfoUrl);
		if(fdResult.equalsIgnoreCase(FULLDATA)){
			try {
				new ImpDept().recuDept();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			log.error("数据未导入部门表");
		}
		
		//导入增量
		//设置时间戳
//		String smtime = DateUtils.getDatetimeTS();
//		String idResult = new ZDPStruInfoImporter().impIncrementalData(struInfoUrl);
//		if(idResult.equalsIgnoreCase(INCREMENT)){
//		new ImpDept().addDeptDatas(smtime);
//		}else{
//			log.error("数据未导入部门表");
//		}
	}
	
	private void impDataToUser(ParamBean param){
		log.error("用户数据导入。。。。。");
		//设置时间戳
		String smtime = DateUtils.getDatetimeTS();
		//导入上游数据到中间表
		String SCResult = new ZDStaffContactImporter().imp(param.getStr("STAFFCONTACT_URL"));
		String SBIResult = new ZDStaffBInfoImporter().imp(param.getStr("STAFFBINFO_URL"));
		String SSResult = new ZDStaffStateImporter().imp(param.getStr("STAFFSTATE_URL"));
		//中间表都进行全量导入，用户表全量导入；中间表无数据导入，不进行用户表导入
		if(SCResult.equals(FULLDATA) && SBIResult.equals(FULLDATA) && SSResult.equals(FULLDATA) ){
			try {
				new ImpUser().recuUser();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(SCResult.equals(ERROR) && SBIResult.equals(ERROR) && SSResult.equals(ERROR)){
			log.warn("未进行数据导入到用户表");
		}else{
			new ImpUser().addUserDatas(smtime);
		}
	}

}
