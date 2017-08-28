package com.rh.core.wfe.serv;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.print.PrintDataHelper;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.Lang;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.WfFilter;
import com.rh.core.wfe.WfProcess;
import com.rh.core.wfe.util.FileController;
import com.rh.core.wfe.util.WfBtnConstant;


/**
 * 返回前台数据
 * 
 */
public abstract class WfOut extends WfBaseOut {

    private static final String AUTH_BEAN = "authBean";

    /**
     * 审批单查看模式
     */
    public static final String DISPLAY_MODE = "WF_DISPLAY_MODE";
    
    /** log对象 **/
    protected Log log = LogFactory.getLog(this.getClass());

    /** 权限Bean */
    private Bean authBean = null;

    /**
     * 
     * @param aWfProc 流程实例
     * @param aOutBean 返回前台Bean
     * @param aParamBean 保存客户端提交参数的Bean
     */
    public WfOut(WfProcess aWfProc, Bean aOutBean, ParamBean aParamBean) {
        super(aWfProc, aOutBean, aParamBean);
        initAuthBean();
        initFieldCtrl();
        initMindCode();
        this.addPrintAuditBtn();
    }

    /**
     * 初始化权限Bean
     */
    private void initAuthBean() {
        // 如果不为空，则取已有
        if (this.getOutBean().isNotEmpty(AUTH_BEAN)) {
            authBean = this.getOutBean().getBean(AUTH_BEAN);
        } else {
            authBean = new Bean();
            this.getOutBean().set(AUTH_BEAN, authBean);
        }
        
        if (authBean.isEmpty("userDoInWf")) {
            authBean.set("userDoInWf", false);
        }
        
        if (authBean.isEmpty("isDraftNode")) {
            authBean.set("isDraftNode", false);
        }
        
        authBean.set("lockState", this.getWfProc().getProcInstBean().getInt("INST_LOCK")); // 是否锁定了\
        authBean.set("INST_LOCK_USER", this.getWfProc().getProcInstBean().getStr("INST_LOCK_USER")); // 锁定人
        authBean.set("INST_LOCK_TIME", this.getWfProc().getProcInstBean().getStr("INST_LOCK_TIME")); // 锁定时间
    }
    
    /**
     * 
     * @return 取得文件权限控制对象
     */
    protected FileController createFileController() {
        FileController fileController = null;
        if (this.getAuthBean().isNotEmpty("nodeFileControl")) {
            List<Bean> list = this.getAuthBean().getList("nodeFileControl");
            fileController = new FileController(list);
        } else {
            String servId = super.getWfProc().getServId();
            ServDefBean servDefBean = ServUtils.getServDef(servId);
            fileController = getServDefFileCtrl(servDefBean);
        }
        
        authBean.set("nodeFileControl", fileController.getFileControlBeanList());
        
        return fileController;
    }       
    
    /**
     * 
     * @param servBean 服务定义Bean
     * @return 文件权限定义列表
     */
    private FileController getServDefFileCtrl(ServDefBean servBean) {
        List<Bean> fileItemList = servBean.getFileSelfItems();

        List<Bean> fileDef = new ArrayList<Bean>();

        for (Bean bean : fileItemList) {
            String config = bean.getStr("ITEM_INPUT_CONFIG");
            Bean configBean = JsonUtils.toBean(config);
            configBean.set("ID", bean.getStr("ITEM_CODE"));
            configBean.set("NAME", bean.getStr("ITEM_NAME"));
            fileDef.add(configBean);
        }
        
        FileController fileCtrl = new FileController(fileDef);
        
        fileCtrl.reserveMinPermission(); 

        return fileCtrl;
    }

    /**
     * 添加文件控制
     */
    private void addDefaultFileControl() {
        FileController fileController = createFileController();
        
        String userCode = getDoUser().getCode();
        String roleCodeStr = Context.getSyConf("SY_WFE_FILE_DOWNLOAD_ROLE", "");
        
        if (StringUtils.isEmpty(roleCodeStr)) {
            return;
        }
        
        //如果用户有“公文管理”权限则有 “下载”按钮
        if (UserMgr.existInRoles(userCode, roleCodeStr)) {
            //设置为“下载”
            fileController.appendVal(FileController.READ);
            fileController.appendVal(FileController.DOWNLOAD);
        }
        
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
     * 添加意见编码Bean
     */
    private void initMindCode() {
        this.getOutBean().set("mindCodeBean", "");
    }

    /**
     * 向OutBean中添加符合格式的数据
     * 
     * @param wfAct 节点实例
     */
    public abstract void fillOutBean(WfAct wfAct);

    /**
     * 使用扩展类来过滤输出到客户端的数据
     * 
     * @param wfAct 节点实例对象
     */
    public void filter(WfAct wfAct) {
        addProcGlobalAct(); // 加入流程公共按钮
        
        //如果没有文件控制参数，则增加默认的文件控制参数。
        if (authBean.isEmpty("nodeFileControl")) {
            this.addDefaultFileControl();
        }
        
        String expandClass = getWfProc().getProcDef().getStr("EXPAND_CLASS");

        if (!StringUtils.isEmpty(expandClass)) {
            WfFilter wfBtnFilter = null;
            try {
                wfBtnFilter = (WfFilter) Lang.loadClass(expandClass)
                        .newInstance();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            wfBtnFilter.doButtonFilter(wfAct, this);
        }
    }

    /**
     * 流程上定义的全局按钮：在任意节点都能显示，如流程跟踪，收藏夹
     */
    private void addProcGlobalAct() {
        log.debug("流程上定义的按钮，在任意节点都能显示的");
        List<Bean> procButton = this.getWfProc().getProcDef().getProActs();
        this.addBtnBeanList(procButton);
    }

    /**
     * 取得流程提供的功能按钮
     * @param actCode act编码
     * @return ACT 功能按钮定义Bean
     */
    public Bean getProcServDefAct(String actCode) {
        ServDefBean servDef = ServUtils.getServDef(ServMgr.SY_WFE_PROC_DEF_ACT);
        
        Bean actBean = servDef.getAct(actCode);
        
        if (null == actBean) {
        	return null;
        } 
        
        return actBean.copyOf();
    }
    
    /**
     * 
     * @param actCode 按钮编码
     * @return 按钮定义
     */
    public Bean getServDefAct(String actCode) {
    	ServDefBean servDef = ServUtils.getServDef(this.getWfProc().getServId());
    	
        Bean actBean = servDef.getAct(actCode);
        
        if (null == actBean) {
        	return null;
        }
        
        return actBean.copyOf();
    }
    
    /**
     * 
     * @return 权限Bean
     */
    public Bean getAuthBean() {
        return authBean;
    }
    
    /**
     * 增加删除按钮
     */
    public void addDeleteBtn() {
    	//工行项目，暂时屏蔽 @yangjinyun
//        if (isGteSUserODeptLevel()) {
//            Bean deleteBean = getServDefAct(WfBtnConstant.BUTTON_DELETE); 
//
//            if (null == deleteBean) {
//            	log.debug("从服务定义中没取到删除按钮，有可能未启用");
//            } else {
//            	this.addBtnBean(deleteBean);	
//            }
//        }
    }
    
    /**
     * 增加“打印审批单”按钮
     */
    private void addPrintAuditBtn() {
    	
    	//如果没有流程，不添加打印审批单按钮。直接跳出，不添加按钮。解决老数据中没有流程定义，却显示“打印审批单”按钮的bug
		if (null == this.getWfProc().getProcDef()
				|| this.getWfProc().getProcDef().isEmpty()) {
			return;
		}
		
		if (!PrintDataHelper.existPrintTmpl(this.getWfProc().getServId())) { //如果不存在打印模板的话，也不显示打印的按钮
			return;
		}
		
        String roleCodeStr = Context.getSyConf("SY_WFE_PRINT_AUDIT_ROLE", "");
        if (StringUtils.isEmpty(roleCodeStr)) {
            return;
        }
        String userCode = getDoUser().getCode();
        if (UserMgr.existInRoles(userCode, roleCodeStr)) {
            // “打印审批单”按钮
            Bean printAudit = this.getProcServDefAct(WfBtnConstant.BUTTON_PRINTAUDIT);
            this.addBtnBean(printAudit);
        }
    }
    /**
     * 
     * @return 当前用户的机构级别是否大于等于拟稿用户的机构级别，即
     */
    public boolean isGteSUserODeptLevel() {
        try {
            UserBean sUserBean = UserMgr.getUser(this.getWfProc().getSUserId()); // 起草
            if (this.getDoUser().getODeptLevel() <= sUserBean.getODeptLevel()) {
                return true;
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return false;
    }
    
    /**
     * 增加收回按钮
     * @param wfAct 节点实例
     */
    protected void addWithdrawBtn(WfAct wfAct) {
        // 流程运行状态，节点已经办结状态，且
        if (this.getWfProc().isRunning() || wfAct.getNodeDef().isFreeNode()) {
            List<Bean> list = wfAct.getCanWithdrawList();
            if (list.size() > 0) {
                Bean withdrawButton = getProcServDefAct(WfBtnConstant.BUTTON_WITHDRAW);
                withdrawButton.set("NI_ID", wfAct.getId());
                
                List<Bean> withdrawList = new ArrayList<Bean>();
                for (Bean bean : list) {
                    Bean wBean = new Bean();
                    wBean.set("NI_ID", bean.getId());
                    wBean.set("NODE_NAME", bean.getStr("NODE_NAME"));
                    wBean.set("TO_USER_NAME", bean.getStr("TO_USER_NAME"));
                    withdrawList.add(wBean);
                }
                withdrawButton.set("wdlist", withdrawList);
                
                this.addBtnBean(withdrawButton);
            }
        }
    }    
}
