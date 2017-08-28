package com.rh.core.icbc.wf;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.wfe.WfParam;
import com.rh.core.wfe.WfProcess;
import com.rh.core.wfe.util.WfeConstant;

/**
 * 
 * @author yangjinyun
 * modify @author yjzhou 
 * time 2016.11.23
 */
public abstract class ApplicationBaseServ extends CommonServ {
	/**
	 * 审批单字段：流水号
	 * 要生成流水号的审批单都应该加上该字段
	 */
	public static final String S_CODE = "S_CODE";
	
	/**服务端标识，通知前端是否为modify的类型*/
	public static final String _ACT_CHANGE_TO_MODIFY = "_ACT_CHANGE_TO_MODIFY";
	
	/**修改记录的主键*/
	public static final String MODIFY_PK = "MODIFY_PK";
	
	/**判断表是否有需要查询条件SERVID*/
	public static final String IS_NEED_SERVID = "IS_NEED_SERVID";
	
	/**判断是否需要读取保存的草稿*/
	public static final String IS_WF_FORM_OPEN_OLD = "IS_WF_FORM_OPEN_OLD";

	/**
	 * 
	 * @return 编码规则
	 */
	public abstract CodeRule getCodeRule();
	
	@Override
	protected void beforeSave(ParamBean paramBean) {
		super.beforeSave(paramBean);
		
		if (paramBean.getAddFlag() && this.getCodeRule() != null) { // 添加模式
			ServDefBean sdb = ServUtils.getServDef(paramBean.getServId());
			if (sdb.containsItem(S_CODE)) {
				paramBean.set(S_CODE, this.getCodeRule().createCode(paramBean));
			}
		}
		
	}
	
    /**
     * 终止审批单之前执行
     * @param paramBean 参数信息
     */
	protected void beforeTerminate(ParamBean paramBean) {

	}
    
    /**
     * 终止审批单之后执行
     * @param paramBean 参数信息
     * @param outBean 输出信息
     */
	protected void afterTerminate(ParamBean paramBean, OutBean outBean) {

	}
	
	/**
	 * 终止流程，删除审批单
	 * @param paramBean
	 * @return
	 */
	public OutBean terminate(ParamBean paramBean) {
		final String dataId = paramBean.getId();
		final String servId = paramBean.getServId();

		if (StringUtils.isBlank(dataId) || StringUtils.isBlank(servId)) {
			return new OutBean().setError("错误的参数：ID和SERV_ID不能为空。");
		}

		try {
			Bean dataBean = ServDao.find(servId, dataId);

			if (dataBean == null) {
				return new OutBean().setError("没有找到审批单。");
			}

			paramBean.set(ParamBean.SAVE_OLD_DATA, dataBean);
			this.beforeTerminate(paramBean);
			
			endWfProcess(paramBean, dataBean.getStr("S_WF_INST"), WfeConstant.NODE_DONE_TYPE_TERMINATE, WfeConstant.NODE_DONE_TYPE_TERMINATE_DESC);

			ParamBean saveBean = new ParamBean();
			saveBean.setId(dataId);
			OutBean outBean = ServMgr.act(servId, ServMgr.ACT_DELETE, paramBean);
			this.afterTerminate(paramBean, outBean);
			return outBean;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new OutBean().setError(e.getMessage());
		}
	}
	
	/*
	 * 终止审批单
	 */
	protected void endWfProcess(ParamBean paramBean, String wfProcInstId) {
		endWfProcess(paramBean, wfProcInstId, WfeConstant.NODE_DONE_TYPE_FINISH, WfeConstant.NODE_DONE_TYPE_FINISH_DESC);
	}
	
	/*
	 * 终止审批单
	 */
	protected void endWfProcess(ParamBean paramBean,String wfProcInstId, int doneType, String doneDesc) {
		WfParam wfParamBean = null;
		if (paramBean.isEmpty("DO_USER_DEPT")) {
			wfParamBean = new WfParam();
			wfParamBean.setDoneUser(Context.getUserBean());
		} else {
			wfParamBean = WfParam.createWfParamAndFillDoUser(paramBean);
		}
		wfParamBean.set("PI_ID", wfProcInstId);
		wfParamBean.set(WfeConstant.FINISH_WF_FORCE_FLAG, 
				paramBean.getBoolean(WfeConstant.FINISH_WF_FORCE_FLAG));
		wfParamBean.setDoneType(doneType);
		wfParamBean.setDoneDesc(doneDesc);

		WfProcess wp = new WfProcess(wfProcInstId, true);
		wp.finish(wfParamBean);
	}
	
	/**
	 * 查询打开当前用户  曾经保存过但未发起流程的审批单
	 */
	@Override
	public OutBean byid(ParamBean paramBean) {
		try {
			//默认启用查找原保存草稿
			int isOPen = Context.getSyConf(IS_WF_FORM_OPEN_OLD, 1);
			if (isOPen == Constant.YES_INT) {
				//设置ID查询
				handleFlowTableParam(paramBean);
			}
		} catch (Exception e) {
			//可能出现抽象类无法实例化，暂不处理
		}
		return super.byid(paramBean);
	}
	
	/**
	 * 通知前端fillData时，actVar修改为modify的类型
	 */
	@Override
	protected void afterByid(ParamBean paramBean, OutBean outBean) {
		if (StringUtils.isNotEmpty(outBean.getId())) {
			outBean.set(_ACT_CHANGE_TO_MODIFY, 1);
		}else {
			outBean.set(_ACT_CHANGE_TO_MODIFY, 2);
		}
		super.afterByid(paramBean, outBean);
	}
	
	/**
	 * 启动流程
	 * @param paramBean
	 * @return
	 */
	public OutBean startWf (ParamBean paramBean){
		OutBean outBean = new OutBean();
		if (startWf(paramBean.getServId(), new Bean(paramBean.getStr("pkCode")).set("S_WF_INST", ""))) {
			outBean.setOk(Context.getSyMsg("SY_WF_START_OK"));
		} else {
			outBean.setError(Context.getSyMsg("SY_ADD_ERROR"));
		}
		return outBean;
	}
	
	/**
	 * 流程表单新建时，检查有没有未处理的草稿
	 * @param paramBean
	 */
	private void handleFlowTableParam (ParamBean paramBean){
		if (StringUtils.isNotEmpty(paramBean.getId())) {
			//若当前已经存在主键的数据，即打开某一条记录，本方法不作处理
			return;
		}
		
		String servId = paramBean.getStr("sId");
		if (StringUtils.isEmpty(servId)) {
			servId = paramBean.getStr("serv");
		}
		if (StringUtils.isEmpty(servId)) {
			return;
		}

		ServDefBean servDefBean = ServUtils.getServDef(servId);
		if (null != servDefBean && 2 == servDefBean.getInt("SERV_WF_FLAG")) {
			Bean wfInstItem = servDefBean.getItem("S_WF_INST");
			Bean wfStateItem = servDefBean.getItem("S_WF_STATE");
			Bean servIdItem = servDefBean.getItem("SERV_ID");
			if (null != wfInstItem && null!=wfStateItem) {
				//判断为流程的表单服务，获取保存的表单打开（未启动流程：即S_WF_INST为空，S_WF_STATE为0）
				SqlBean sqlBean = new SqlBean();
				sqlBean.andNull("S_WF_INST");
				sqlBean.and("S_WF_STATE", 0);
				//增加筛选条件为当前用户
				sqlBean.and("S_USER", Context.getUserBean().getCode());
				//若是多个审批单服务用同一个表，则默认审批单会存在SERV_ID字段，区分不同的服务
				if(null != servIdItem && 1==servIdItem.getInt("ITEM_TYPE")){
					//SERV_ID必须是表字段
					sqlBean.and("SERV_ID", servId);
				}
				List<Bean> list = ServDao.finds(servId, sqlBean);
				if (null!=list && !list.isEmpty()) {
					paramBean.setId(list.get(0).getId());				
				}
			}
		}
	}
}
