package com.rh.core.icbc.imp.exception;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.icbc.imp.target.ImpDept;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;

/**
 * 部门类型例外表记录操作
 * @author huangsanbao--20151022
 */
public class TypeException extends CommonServ {
	
	
	
	@Override
	protected void beforeSave(ParamBean paramBean) {
		Bean bean = paramBean.getSaveFullData();
		String dcode=bean.getStr("DEPT_CODE");
		Bean deptBean = ServDao.find(ServMgr.SY_ORG_DEPT,dcode );
		if (!deptBean.isEmpty()) {
			//判断当前保存的部门类型是否与实际存在的类型一致
			if(paramBean.getInt("DEPT_TYPE")==deptBean.getInt("DEPT_TYPE"))
			{
				throw new TipException("例外类型设置与实际部门类型一致，请确认后再配置!");
			}
			
		}
		else {
			throw new TipException("当前部门并不存在，请确认后再配置!");
		}
		// TODO Auto-generated method stub
//		super.beforeSave(paramBean);
	}

	
	/*@Override
	public OutBean save(ParamBean paramBean) {
		
		Bean bean=paramBean.getSaveFullData();
		String dcode=paramBean.getSaveFullData().getStr("DEPT_CODE");
				//paramBean.getStr("DEPT_CODE");
		*
		*当前判断前端已经控制，无需再行判断
		 * if("".equals(dcode)){
			return new OutBean().setError("部门信息不能为空！");
		}
		*
		Bean deptBean = ServDao.find(ServMgr.SY_ORG_DEPT,dcode );
		if (!deptBean.isEmpty()) {
			//判断当前保存的部门类型是否与实际存在的类型一致
			if(paramBean.getInt("DEPT_TYPE")==deptBean.getInt("DEPT_TYPE"))
			{
				return new OutBean().setError("例外类型设置与实际部门类型一致，请确认后再配置！");
			}
			else {
				return super.save(paramBean);
			}
			
		}
		else {
			return new OutBean().setError("当前部门并不存在，请确认后再配置！");
		}
	}*/
    

	@Override
	protected void afterSave(ParamBean paramBean, OutBean outBean) {
//		paramBean.getStr("EID");
		//同步更新部门表的部门类型
		ParamBean deptBean = new ParamBean();
		Bean bean= paramBean.getSaveFullData();
		deptBean.setId(bean.getStr("DEPT_CODE"));
		deptBean.set("DEPT_TYPE", bean.getStr("DEPT_TYPE"));
		ServMgr.act(ServMgr.SY_ORG_DEPT, ServMgr.ACT_SAVE, deptBean);
		outBean.setOk();
	}

	@Override
	protected void afterDelete(ParamBean paramBean, OutBean outBean) {
		//删除例外部门类型后，取反同步更新部门表的部门类型	
	    /*删除后，不再取反，流程变更20151022
	     * java.util.List<Bean> dbeanList=	paramBean.getDeleteDatas();
	    if(!dbeanList.isEmpty())
	    {
	    	for (Bean bean : dbeanList) {
	    		ParamBean deptBean = new ParamBean();
	    		deptBean.setId(bean.getStr("DEPT_CODE"));
	    		int dt=bean.getInt("DEPT_TYPE");
	    		if(dt==1)dt=2;
	    		else {
	    			dt=1;
	    		}
	    		deptBean.set("DEPT_TYPE",dt);
	    		ServMgr.act(ServMgr.SY_ORG_DEPT, ServMgr.ACT_SAVE, deptBean);
			}
	    }*/
		//同步删除，调用原始表数据进行恢复
		List<Bean> dbeanList=	paramBean.getDeleteDatas();
		if(!dbeanList.isEmpty())
	    {
	    	for (Bean bean : dbeanList) {
	    		ImpDept impDept=new ImpDept();
	    		impDept.resetDeptData(bean.getStr("DEPT_CODE"));
	    	}
	    }
	}

}
