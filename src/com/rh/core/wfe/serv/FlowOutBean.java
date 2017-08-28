package com.rh.core.wfe.serv;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.WfProcess;
import com.rh.core.wfe.util.FileController;
import com.rh.core.wfe.util.WfBtnConstant;

/**
 * 流经用户 能看意见，相关文件，修改痕迹，文稿
 * @author anan
 *
 */
public class FlowOutBean extends WfOut {
    /** 系统配置项：流经是否允许下载文件 **/
    private static final String CONF_CAN_DOWNLOAD = "WF_FLOW_CAN_DOWNLOAD";
    
    /**
     * 审批单查看模式：流经模式
     */
    private static final String MODE_FLOW = "MODE_FLOW";
	/**
	 * 
	 * @param aWfProc
	 *            流程实例
	 * @param aOutBean
	 *            返回前台Bean
	 * @param aParamBean 参数           
	 */
	public FlowOutBean(WfProcess aWfProc, Bean aOutBean, ParamBean aParamBean) {
		super(aWfProc, aOutBean, aParamBean);
	}

	@Override
	public void fillOutBean(WfAct wfAct) {
//		this.addFenFaProcBtn();
	    initFieldCtrl(); // 字段控制参数
	    
		addFlowProcBtn(wfAct); //增加流经相关按钮
		
		addSaveBtn(wfAct);
		
		addFileControl();  //增加文件操作控制
		
		this.getOutBean().set(DISPLAY_MODE, MODE_FLOW);
	}
	
    /**
     * 表单字段控制
     * 
     */
    private void initFieldCtrl() {
        Bean fieldCtrlBean = new Bean();

        fieldCtrlBean.set("FIELD_CONTROL", false);
        fieldCtrlBean.set("FIELD_EXCEPTION", "");
        fieldCtrlBean.set("FIELD_UPDATE", "");
        fieldCtrlBean.set("FIELD_HIDDEN", "");
        fieldCtrlBean.set("FIELD_MUST", "");

        this.getOutBean().set("fieldControlBean", fieldCtrlBean);
    }
    
    /**
     * 处理流经过用户的处理按钮，如：收回、取消办结
     * 
     * @param wfAct 节点实例
     */
    private void addFlowProcBtn(WfAct wfAct) {
        if (!this.getWfProc().isRunning() && this.getWfProc().canCancelFinish()) { // 已经办结的流程
            Bean undo = getProcServDefAct(WfBtnConstant.BUTTON_UNDO_FINISH);
            this.addBtnBean(undo);
            log.debug("添加取消办结按钮");
        }

        // 流程运行状态，节点已经办结状态
        this.addWithdrawBtn(wfAct);
        
    }
    
    /**
     * 
     * @param wfAct
     */
	private void addSaveBtn(WfAct wfAct) {
		if (!wfAct.getProcess().isRunning()) { // 流程已办结
			String editableFields = wfAct.getProcess().getProcDef().getStr("BIND_ENDEDITFIELD");
			if (StringUtils.isNotBlank(editableFields)) { // 办结后可编辑字段不为空。
				Bean saveBtn = super.getServDefAct("save");
				this.addBtnBean(saveBtn);
				this.getAuthBean().set("BIND_ENDEDITFIELD", editableFields);
			}
		}
	}
    
    /**
     * 添加文件控制
     */
    private void addFileControl() {
        
        FileController fileController = createFileController();
        
        String userCode = this.getDoUser().getCode();
        String roleCodeStr = Context.getSyConf("SY_WFE_FILE_DOWNLOAD_ROLE", "");
        
        if (StringUtils.isEmpty(roleCodeStr)) {
            return;
        }
        
        //设置下载权限
        String canDownload = Context.getSyConf(CONF_CAN_DOWNLOAD, "true");
        if (canDownload.equalsIgnoreCase("true")) {
            // 设置为“下载”
            fileController.appendVal(FileController.READ);
            fileController.appendVal(FileController.DOWNLOAD);
        } else if (UserMgr.existInRoles(userCode, roleCodeStr)) {
            // 如果用户有“公文管理”权限则有 “下载”按钮
            fileController.appendVal(FileController.READ);
            fileController.appendVal(FileController.DOWNLOAD);
        }
        
    }    

}
