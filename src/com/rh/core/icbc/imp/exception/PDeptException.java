package com.rh.core.icbc.imp.exception;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.icbc.imp.target.ImpDept;

/**
 * 父部门例外表操作
 * @author huangsanbao--20151022
 */
public class PDeptException extends CommonServ {

	@Override
	protected void beforeSave(ParamBean paramBean) {
		ParamBean pBean=new ParamBean();
		Bean bean1 = paramBean.getSaveFullData();
		String dcode=bean1.getStr("DEPT_CODE");
		String dpcode=bean1.getStr("PDEPT_CODE");
		if(dpcode.equals(dcode)){
			throw new TipException("本部门不能配置成自己的上级部门！");
		}
		ParamBean paramBean2=new ParamBean();
		paramBean2.setWhere("and DEPT_CODE='"+dcode+"'");
		Bean bean3=ServDao.find(ServMgr.SY_ORG_DEPT, paramBean2);
		if(!bean3.isEmpty())
		{
			String codep=bean3.getStr("CODE_PATH");
			pBean.setWhere(" and CODE_PATH like '"+codep+"%^'");
			//pBean.setWhere(" and DEPT_CODE!='"+dcode+"' and CODE_PATH like '%"+dcode+"%'");
			List<Bean> bean= ServDao.finds(ServMgr.SY_ORG_DEPT,pBean);
			int ifd=0;
			if(!bean.isEmpty())
			{
				for (Bean bean2 : bean) {
					String deodetemp=bean2.getStr("DEPT_CODE");
					//判断父部门是否是否存在于其子部门中
					if(deodetemp.equals(dpcode)){
						ifd++;
					}
				}
				if(ifd>0)
				{
					throw new TipException("部门层级关系配置有误，请确认后再保存！");
				}
			}
			else {
				//叶子节点数据
				//return super.save(paramBean);
			}
		}
		/*Bean deptBean = ServDao.find(ServMgr.SY_ORG_DEPT,dcode );
		if (!deptBean.isEmpty()) {
			//判断当前保存的部门类型是否与实际存在的类型一致
			if(paramBean.getInt("DEPT_TYPE")==deptBean.getInt("DEPT_TYPE"))
			{
				throw new TipException("例外类型设置与实际部门类型一致，请确认后再配置!");
			}
			
		}
		else {
			throw new TipException("当前部门并不存在，请确认后再配置!");
		}*/
		// TODO Auto-generated method stub
//		super.beforeSave(paramBean);
	}
	
	/*@Override
	public OutBean save(ParamBean paramBean) {
		// TODO Auto-generated method stub
//		return super.save(paramBean);
		//判断更改的父部门是否为该部门之前的子部门，如果是，则禁止保存
		ParamBean pBean=new ParamBean();
//		pBean=paramBean;
		String dcode=paramBean.getStr("DEPT_CODE");
		String dpcode=paramBean.getStr("PDEPT_CODE");
		
		if(dpcode.equals(dcode)){
			return new OutBean().setError("本部门不能配置成自己的上级部门！");
		}
		pBean.setWhere(" and DEPT_CODE!='"+dcode+"' and CODE_PATH like '%"+dcode+"%'");
		List<Bean> bean= ServDao.finds(ServMgr.SY_ORG_DEPT,pBean);
		int ifd=0;
		if(!bean.isEmpty())
		{
			//保存 
			for (Bean bean2 : bean) {
				String deodetemp=bean2.getStr("DEPT_CODE");
				//判断父部门是否是否存在于其子部门中
				if(deodetemp.equals(dpcode)){
					ifd++;
				}
			}
			if(ifd==0)
			{
				return super.save(paramBean);				
			}
			else {
				return new OutBean().setError("部门层级关系配置有误，请确认后再保存！");
			}
		}
		else {
			//叶子节点数据
			return super.save(paramBean);
		}
		
	}*/

	@Override
	protected void afterSave(ParamBean paramBean, OutBean outBean) {
		// TODO Auto-generated method stub
		//同步更新部门表的父子关系
		ParamBean deptBean = new ParamBean();
		Bean bean= paramBean.getSaveFullData();
		deptBean.setId(bean.getStr("DEPT_CODE"));
		deptBean.set("DEPT_PCODE", bean.getStr("PDEPT_CODE"));
		ServMgr.act(ServMgr.SY_ORG_DEPT, ServMgr.ACT_SAVE, deptBean);
		outBean.setOk();
	}
	
	@Override
	protected void afterDelete(ParamBean paramBean, OutBean outBean) {
		// TODO Auto-generated method stub
		//同步删除，调用原始表数据进行恢复
		List<Bean> dbeanList=	paramBean.getDeleteDatas();
		if(!dbeanList.isEmpty())
	    {
	    	for (Bean bean : dbeanList) {
	    		ImpDept impDept=new ImpDept();
	    		impDept.resetDeptData(bean.getStr("DEPT_CODE"));
	    	}
	    }
		
		super.afterDelete(paramBean, outBean);
	}

}
