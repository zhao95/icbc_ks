package com.rh.core.wfe.serv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.comm.mind.DisabledMindUpdater;
import com.rh.core.comm.mind.MindServ;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.flow.FlowMgr;
import com.rh.core.util.Constant;
import com.rh.core.util.JavaScriptEngine;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.Lang;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.WfProcess;
import com.rh.core.wfe.condition.MindContextHelper;
import com.rh.core.wfe.def.WfNodeDef;
import com.rh.core.wfe.util.FileController;
import com.rh.core.wfe.util.WfBtnConstant;
import com.rh.core.wfe.util.WfUtils;
import com.rh.core.wfe.util.WfeConstant;

/**
 * 正办理用户返回Bean
 * 
 */
public class DoingOutBean extends WfOut {
    /**
     * 返回前台的模式
     */
    public static final String MODE_DOING = "MODE_DOING";
    
	private WfAct wfAct;
	private DisabledMindUpdater mindUpdater = null;
	private HashMap<String, Bean> mindCodeMap;
	private String authUser = null;

	@Override
	public void fillOutBean(WfAct aWfAct) {
		this.wfAct = aWfAct;
		
		initDoUser();
		
		int lockState = this.getWfProc().getProcInstBean().getInt("INST_LOCK");
        this.getOutBean().set("_INST_LOCK", lockState);		
		
		if (wfAct.canDuzhan()) { //节点运行状态，同时运行节点大于1 , 如果是独占的话，只出独占的按钮
//			wfAct.duzhan(this.getDoUser()); // 独占 ， 开始处理文件
		}
		
        mindUpdater = new DisabledMindUpdater(wfAct, this.getDoUser());
		
		addOpenTime(); //添加打开的时间，如果打开过，则不能收回了
		
        addFormDefBtn(); // 表单定义的按钮  
        addWfDefBtn();   // 节点上定义的公共的按钮
        
        if (wfAct.getProcess().canDelete(getDoUser())) {
            //如果为起草点，且没有送出，则显示删除按钮。
            this.addDeleteBtn();
        }
		
        String mindCode = wfAct.getNodeDef().getStr("MIND_CODE");
        String mindTerminal = wfAct.getNodeDef().getStr("MIND_TERMINAL");
        String mindCodeReguler = wfAct.getNodeDef().getStr("MIND_REGULAR");
        StringBuilder mindCodes = new StringBuilder();
        mindCodes.append(mindCode).append(",").append(mindTerminal).append(",").append(mindCodeReguler);
		List<Bean> mindDefs = wfAct.getNodeDef().getList("MIND_PUTONG_ADD");
		for (Bean mindDef: mindDefs) {
			if (mindDef.isNotEmpty("MIND_CODE")) {
				mindCodes.append(",").append(mindDef.getStr("MIND_CODE"));
			}
		}
        
        mindCodeMap = new MindServ().getMindCodeBeanMap(mindCodes.toString());
		addMindCode(); // 普通意见和最终意见
		//addMindTerminal(); //最终意见的意见编码Bean
		//addMindRegular(); //增加固定意见
		//addHandMindFlag(); // 是否支持手写意见
		//addMindMust(); //是否必填意见
		addUserDoingFlag(); // 当前节点执行状态
		addFieldCtrl(); // 输入项的控制
		addIfDraftNode(); //是否起草点

		addFileController(); // 文件类型权限控制

		addWorkComplete(); //处理完毕

		
        if (!(wfAct.getProcess().getRunningNodeInstCount() > 1 && wfAct.canStopParallel())
                || wfAct.getNodeDef().getInt("NODE_IF_AUTOEND") == Constant.NO_INT) {
            // 1,汇合点，且活动点数量大于1时，不能送下一步，其它情况可以送下一步。2,非自动结束节点可以送下一步
            addNextStep(); // 下一步能走到节点
        }
		
		
		operateLockBtn(); //显示  锁定/解锁 文件
		
		// 节点定义转换成的按钮
		this.addFinishCurrBtn(); // 结束当前工作
		this.addTerminateParall(); // 终止并发
		this.addSaveAndSend(); //完成并发送
		this.addFinishBtn(); // 办结按钮
		
		//如果不是自动结束，则判断是否可以收回
        if (this.wfAct.getNodeDef().getInt("NODE_IF_AUTOEND") == Constant.NO_INT) {
            this.addWithdrawBtn(this.wfAct); // 收回按钮
        }
		
		// 补登意见
		addBuDengMindParam();
		
		//填入流经的记录
		addFlowRecord();
		
        // 增加用户自定义变量
        addCustomVarList();
        
        this.getOutBean().set(DISPLAY_MODE, MODE_DOING);
        this.appendViewItem();
        
        // 增加办理人标志
        appendDoUserDept();
	}
	
	/**
	 * 增加办理人身份
	 */
	private void appendDoUserDept() {
		StringBuilder doUserDept = new StringBuilder();
		doUserDept.append(this.getDoUser().getCode()).append("^").append(this.getDoUser().getDeptCode());
		
		if(StringUtils.isNotBlank(this.authUser)) {
			doUserDept.append("@").append(this.authUser);
		}
		
		this.getAuthBean().set("DO_USER_DEPT", doUserDept.toString() );
	}

	/**
	 * 添加处理完毕的相关内容
	 */
	private void addWorkComplete() {
		this.getAuthBean().set("showYueZhi", wfAct.getNodeDef().getInt("SHOW_YUE_ZHI"));
		//处理完毕页面服务字段的配置
		if (wfAct.getNodeDef().isNotEmpty("FIELD_COMPLETE")) {
		    try {
		        this.getOutBean().set("FIELD_COMPLETE", 
		                wfAct.getNodeDef().getList("FIELD_COMPLETE"));
		    } catch (Exception e) {
		        
		    }
		}
		
		//送交多个节点的配置
		if (wfAct.getNodeDef().isNotEmpty("DEVIDE_TWO_NODE")) {
			this.getAuthBean().set("DEVIDE_TWO_NODE", wfAct.getNodeDef().getStr("DEVIDE_TWO_NODE"));
		}
		
		//处理选择的配置
		if (wfAct.getNodeDef().isNotEmpty("S_WF_SELECT")) {
		    try {
		        this.getAuthBean().set("S_WF_SELECT", wfAct.getNodeDef().getList("S_WF_SELECT"));
		        
		        this.getAuthBean().set("S_WF_SELECT_BASE", wfAct.getNodeDef().getBean("S_WF_SELECT_BASE"));
		        
		    } catch(Exception e) {
		        
		    }
		}
		
		//ACT-button的定义信息
		if (wfAct.getNodeDef().isNotEmpty("DELIVER_ACT_DEF")) {
			this.getAuthBean().set("DELIVER_ACT_DEF", wfAct.getNodeDef().getList("DELIVER_ACT_DEF"));
		}
		
	}

	/**
	 * 完成并发送
	 */
    private void addSaveAndSend() {
        // if (Context.getSyConf("SY_WF_NEXT_STEP", "").equals("saveAndSend")) {
        //根据工作流按钮渲染模式系统配置状态值决定是否添见完成并发送按钮
        String renderMode = Context.getSyConf("SY_WF_BTN_RENDER", "");
        if (renderMode.equals(Constant.FLAT_GROUP) || renderMode.equals(Constant.FLAT_BAR)
                || renderMode.equals(Constant.GROUP_GROUP) || renderMode.equals(Constant.GROUP_BAR)
                || renderMode.equals(Constant.FLAT_DIALOG) || renderMode.equals(Constant.GROUP_DIALOG)) {
            if (!this.existBtnBean(WfBtnConstant.BUTTON_STOP_PARALLEL)) {
                Bean saveSendBtn = this.getProcServDefAct(WfBtnConstant.BUTTON_SAVE_SEND);
                this.addBtnBean(saveSendBtn);
            }
        }
    }

	/**
	 * 记录办理人第一次打开本节点实例的时间，如果打开过，则不能收回了。
	 */
	private void addOpenTime() {
        if (wfAct.getNodeInstBean().isEmpty("OPEN_TIME")) {
            wfAct.updateInstOpenTime();
		}
	}

	/**
	 * 是否是起草点
	 */
	private void addIfDraftNode() {
		boolean isDraftNode = false;
		if (wfAct.getNodeDef().getInt("NODE_TYPE") == WfeConstant.NODE_TYPE_DRAFT) {
			isDraftNode = true;
		}

		this.getAuthBean().set("isDraftNode", isDraftNode);
	}


	/**
	 * 添加流经的记录
	 */
	private void addFlowRecord() {
        if (this.wfAct.getNodeInstBean().getInt("TO_TYPE") == WfeConstant.NODE_INST_TO_SINGLE_USER) { // 送单个人
            FlowMgr.addUserFlow(this.getWfProc().getDocId(), getDoUser(), FlowMgr.FLOW_TYPE_FLOW);
        } else { // 送角色（多个人） 从节点用户中取发送的用户
            List<Bean> nodeUsers = this.wfAct.getNodeUserBeanList();
            for (Bean nodeUser: nodeUsers) {
                UserBean userBean = UserMgr.getUser(nodeUser.getStr("TO_USER_ID"));
                userBean.set("AUTH_USER", nodeUser.getStr("AUTH_USER"));
                FlowMgr.addUserFlow(this.getWfProc().getDocId(), userBean, FlowMgr.FLOW_TYPE_FLOW);
            }
        }
	}

	/**
	 * 增加补登意见的参数
	 */
	private void addBuDengMindParam() {
		//是否有补登意见按钮
		Bean bdMindAct = getBtnBean(WfBtnConstant.BUTTON_BUDENG);
		if (null != bdMindAct) {
			// 补登意见的时候，是需要带上领导的，即给谁补登
			bdMindAct.set("leaders", getLeaders(bdMindAct));
		}
	}
	
	/**
	 * 取到当前人的领导 , 在补登意见的按钮上设置  {'MIND_CODE':'PS-0001','ROLE_CODE':'RBMLD','ACL_LEVEL':'1'}
	 * @param bdMindBtn 补登意见的定义
	 * @return 得到用户的领导 形成 串，用于补登意见  {{'liyanwei','李延伟'},{'cuihf','崔海峰'}}
	 */
    private String getLeaders(Bean bdMindBtn) {
        
        List<Bean> rtnBeanList = new ArrayList<Bean>();
        
		//取到节点上的补登意见的定义
        if (bdMindBtn.isEmpty("WFE_PARAM")) {
            return "";
        }
        
		String bdMindParam = bdMindBtn.getStr("WFE_PARAM");
		
		Bean param = null;
        try {
            param = JsonUtils.toBean(bdMindParam);
        } catch (Exception e) {
            StringBuilder error = new StringBuilder();
            error.append("Workflow define error. Budeng Mind Button Value is '");
            error.append(bdMindParam);
            error.append("', workflowCode:");
            error.append(this.wfAct.getNodeDef().getStr("PROC_CODE"));
            error.append(",NODE_CODE:");
            error.append(this.wfAct.getCode());
            log.error(error.toString());
            return "";
        }
        
	    List<UserBean> users;
	    UserBean user = this.getDoUser();
	    
	    if (param.getInt("ACL_LEVEL") == WfeConstant.BUDENG_SCOPE_DEPT) {  //本部门
	        List<DeptBean> subDepts = OrgMgr.getChildDepts(user.getCmpyCode(), user.getTDeptCode());
	        
	        StringBuilder strdept = new StringBuilder(user.getTDeptCode());
	        for (DeptBean dept: subDepts) {
	            strdept.append(",").append(dept.getCode());
	        }
	        
	        users = UserMgr.getUsersByDept(strdept.toString(), param.getStr("ROLE_CODE"));
	    } else { //本机构
	        users = UserMgr.getUsersInOdept(user.getODeptCode(), param.getStr("ROLE_CODE"));
	    }
	    
	    for (UserBean leader: users) {
	        Bean leaderBean = new Bean();
            leaderBean.set("userCode", leader.getCode());
            leaderBean.set("userName", leader.getName());
            rtnBeanList.add(leaderBean);
	    }
		
		return JsonUtils.toJson(rtnBeanList);
	}

	/**
	 * 节点上定义的公共的按钮 , 如分发 ， 打印，相关文件
	 */
	private void addWfDefBtn() {
		List<Bean> wfCmButton = wfAct.getNodeDef().getWfButton();
		this.addBtnBeanList(wfCmButton);
	}

	/**
	 * 操作 锁定文件的按钮
	 */
	private void operateLockBtn() {
		//从流程中取是否已经lock的状态 ， 如果已经lock了，则移出  锁定按钮
		int lockState = this.getWfProc().getProcInstBean().getInt("INST_LOCK");
		this.getOutBean().set("_INST_LOCK", lockState);
		
		if (WfProcess.PROC_INST_LOCK == lockState) { //已经锁定
			Bean lockBean = this.getProcServDefAct(WfBtnConstant.BUTTON_LOCK);
			
			Bean unLockBean = this.getProcServDefAct(WfBtnConstant.BUTTON_LOCK_UN);
			
			//如果当前节点设置了锁定，而且是锁定状态的
			if (existBtnBean(lockBean) && !existBtnBean(unLockBean)) {
				this.addBtnBean(unLockBean);
			}
			
			removeBtnBean(lockBean);
		} else { //没锁定， 去掉解锁按钮
			Bean unLockBean = this.getProcServDefAct(WfBtnConstant.BUTTON_LOCK_UN);
			
			removeBtnBean(unLockBean);
		}
	}
	
	/**
	 * 表单上定义的按钮
	 */
	private void addFormDefBtn() {
	    // 对于字段完全控制的单据，显示保存按钮，
	    // 或者只读控制但是有可编辑字段，也显示保存按钮
        if (this.wfAct.getNodeDef().isEntirelyControl()
                || (StringUtils.isNotEmpty(this.wfAct.getNodeDef().getFieldException()))) {
            Bean saveAct = this.getServDefAct(ServMgr.ACT_SAVE);
            
            if (null == saveAct) {
            	log.debug("从服务定义中没取到保存按钮，有可能未启用");
            } else {
            	this.addBtnBean(saveAct);	
            }
        } else {
            this.removeBtnBean(ServMgr.ACT_SAVE);
        }
	    
		List<Bean> formButton = wfAct.getNodeDef().getFormButton();

		this.addBtnBeanList(formButton);
	}

	/**
	 * 文件类型权限的控制，从节点定义上取
	 */
	private void addFileController() {
		String fileControl = wfAct.getNodeDef().getFileControl();

		FileController fileController = new FileController(fileControl);
		
		this.getAuthBean().set("nodeFileControl", fileController.getFileControlBeanList());
	}

	/**
	 * 表单字段控制
	 * 
	 */
	private void addFieldCtrl() {
		WfNodeDef nodeDef = wfAct.getNodeDef();

		Bean fieldCtrlBean = new Bean();

		fieldCtrlBean.set("FIELD_CONTROL", nodeDef.isEntirelyControl());
		fieldCtrlBean.set("FIELD_EXCEPTION", nodeDef.getFieldException());
		fieldCtrlBean.set("FIELD_HIDDEN", nodeDef.getFieldHidden());
		fieldCtrlBean.set("FIELD_MUST", nodeDef.getFieldMust());
		fieldCtrlBean.set("FIELD_DISPLAY", nodeDef.getFieldDisplay());
		
        for (int i = 0; i < WfNodeDef.GROUP_FIELDS.length; i++) {
            fieldCtrlBean.set(WfNodeDef.GROUP_FIELDS[i], nodeDef.getStr(WfNodeDef.GROUP_FIELDS[i]));
        }		

		this.getOutBean().set("fieldControlBean", fieldCtrlBean);
	}

	/**
	 * 
	 * @param wfProc
	 *            流程实例
	 * @param outBean
	 *            返回数据
	 * @param paramBean
	 *            保持客户端提交数据的Bean
	 */
	public DoingOutBean(WfProcess wfProc, Bean outBean, ParamBean paramBean) {
		super(wfProc, outBean, paramBean);
	}

	/**
	 * 能走的节点
	 */
	private void addNextStep() {
		List<Bean> nextSteps = wfAct.getNextAvailableSteps(this.getDoUser());
		
		this.getOutBean().set("nextSteps", nextSteps);
	}

	/**
	 * 添加意见编码Bean
	 */
	private void addMindCode() {
		List<Bean> ptMinds = new ArrayList<Bean>();
		String mindCode = wfAct.getNodeDef().getStr("MIND_CODE");
		
		//执行过滤脚本
//		String script = wfAct.getNodeDef().getStr("MIND_SCRIPT");
//		boolean canWrite = this.execScript(script);
		boolean canWrite = true;

		boolean wfSelectMode = false; //处理完毕模式，在前台
		if (wfAct.getNodeDef().isNotEmpty("S_WF_SELECT")) {
			if (wfAct.getNodeDef().getList("S_WF_SELECT").size() > 0) {
				wfSelectMode = true;
			}
		}
		
		Bean mindCodeBean = null;
        if ((canWrite || wfSelectMode) && StringUtils.isNotEmpty(mindCode)) {
            // 根据mindCode得到意见类型信息
            mindCodeBean = mindCodeMap.get(mindCode);
        }
        
        if (null == mindCodeBean) {
            mindCodeBean = new Bean();
        } else {
            final String pk = Lang.getUUID();
//            mindCodeBean.setId(pk);
            mindCodeBean.set("MIND_ID", pk);
        }
		
		mindCodeBean.set("MIND_MUST", wfAct.getNodeDef().getStr("MIND_NEED_FLAG"));
		mindCodeBean.set("MIND_SCRIPT", wfAct.getNodeDef().getStr("MIND_SCRIPT"));
		mindCodeBean.set("DEFAULT_CONTENT", wfAct.getNodeDef().getStr("MIND_CONTENT"));
		
		ptMinds.add(mindCodeBean);  //默认以前的
		
		//取到动态添加上的意见类型
		List<Bean> mindDefs = wfAct.getNodeDef().getList("MIND_PUTONG_ADD");
		
		for (Bean mindDef: mindDefs) {
			if (mindDef.isNotEmpty("MIND_CODE")) {
				String mindScript = mindDef.getStr("MIND_SCRIPT");
				Bean mindDefAdd = new Bean();
				mindDefAdd = mindCodeMap.get(mindDef.getStr("MIND_CODE"));
				
				mindDefAdd.set("MIND_ID", mindCodeBean.getStr("MIND_ID"));
				mindDefAdd.set("MIND_MUST", mindDef.getStr("MIND_NEED_FLAG"));
				mindDefAdd.set("MIND_SCRIPT", mindScript);
				mindDefAdd.set("DEFAULT_CONTENT", mindDef.getStr("MIND_CONTENT"));
				
				ptMinds.add(mindDefAdd); 
			}
		}
		
		this.getOutBean().set("mindCodeBean", ptMinds);
	}
	
	/**
	 * 增加固定意见定义
	 */
    @SuppressWarnings("unused")
	private void addMindRegular() {
        Bean regularMind = null;
        
        // 执行意见类型过滤脚本
        String script = wfAct.getNodeDef().getStr("MIND_REGULAR_SCRIPT");
        boolean canWrite = this.execScript(script);
        
        final String mindCode = wfAct.getNodeDef().getStr("MIND_REGULAR");
        if (canWrite && StringUtils.isNotEmpty(mindCode)) {
            //取得指定意见Bean
            regularMind = mindCodeMap.get(mindCode);
            
            //设置默认值
            if (wfAct.getNodeDef().isNotEmpty("MIND_REGULAR_CONTENT")) {
                String defaultValue = wfAct.getNodeDef().getStr("MIND_REGULAR_CONTENT");
                
                //TODO 这个usualID将从节点定义中去取，现在暂时直接查询一下
                String value = defaultValue.substring(defaultValue.indexOf("(") + 1, defaultValue.indexOf(")"));
                SqlBean sql = new SqlBean();
                sql.and("REGULAR_TYPE", mindCode);
                sql.and("MIND_VALUE", value);
                sql.selects("MIND_ID");
                
                Bean usualBean = ServDao.find(ServMgr.SY_COMM_MIND_USUAL, sql);
                
                regularMind.set("DEFAULT_USUAL_ID", usualBean.getStr("MIND_ID"));
                regularMind.set("DEFAULT_VALUE", value);
                regularMind.set("DEFAULT_CONTENT", defaultValue.substring(0, defaultValue.indexOf("(")));
            }
        }
        
        if (regularMind == null) {
            List<Bean> list = mindUpdater.getRegularMindList();
            
            if (list.size() > 0) {
                regularMind = new MindServ().getMindCode(list.get(0).getStr("MIND_CODE"));
                regularMind.set("READ_ONLY", "true");
            } else {
                regularMind = new Bean();
            }
        } else {
            final String pk = Lang.getUUID();
//            regularMind.setId(pk);
            regularMind.set("MIND_ID", pk);
        }
        
        // 固定意见是否必填
        regularMind.set("MIND_MUST", wfAct.getNodeDef().getStr("MIND_REGULAR_MUST"));

        this.getOutBean().set("regularMind", regularMind);
    }
    
    /**
     * 执行返回结果为bool值的js脚本
     * @param script 脚本
     * @return 执行结果
     */
    private boolean execScript(String script) {
        return WfUtils.execCondScript(script, getOutBean());
    }
	
	/**
	 * 添加最终意见编码Bean
	 */
	@SuppressWarnings("unused")
	private void addMindTerminal() {
	    final String mindCode = wfAct.getNodeDef().getStr("MIND_TERMINAL");
	    // 意见类型过滤脚本
	    String script = wfAct.getNodeDef().getStr("MIND_TERMINAL_SCRIPT");
	    boolean canWrite = this.execScript(script);
	    
		// 根据mindCode得到意见类型信息
        Bean mindTerminal = null;
        if (canWrite && StringUtils.isNotEmpty(mindCode)) {
            mindTerminal = mindCodeMap.get(mindCode);
            
            mindTerminal.set("DEFAULT_CONTENT", wfAct.getNodeDef().getStr("MIND_TERMINAL_CONTENT"));
        }
        
		if (null == mindTerminal) {
//            List<Bean> list = mindUpdater.getTerminalMindList();
//            
//            if (list.size() > 0) {
//                mindTerminal = new MindServ().getMindCode(list.get(0).getStr("MIND_CODE"));
//                mindTerminal.set("READ_ONLY", "true");
//            } else {
                mindTerminal = new Bean();
//            }
		} else {
            final String pk = Lang.getUUID();
//            mindTerminal.setId(pk);
            mindTerminal.set("MIND_ID", pk);
		}
		
		mindTerminal.set("MIND_MUST", wfAct.getNodeDef().getStr("MIND_TERMINAL_MUST"));
		
		this.getOutBean().set("mindTerminal", mindTerminal);
	}
	

	/**
	 * 是否支持手写意见
	 */
//	private void addHandMindFlag() {
//		boolean canHandMind = false;
//		if (wfAct.getNodeDef().getInt("MIND_IF_HAND") == 1) {
//			canHandMind = true;
//		}
//
//		this.getAuthBean().set("canHandWrite", canHandMind);
//	}

	/**
	 * 当前用户 执行状态
	 */
	private void addUserDoingFlag() {
		this.getAuthBean().set("userDoInWf", true);
	}

	/**
	 * 添加办结按钮
	 */
	private void addFinishBtn() {
		if (wfAct.canEndTheProcess() && !this.existBtnBean(WfBtnConstant.BUTTON_SAVE_SEND)) {
			Bean finishBtn = this.getProcServDefAct(WfBtnConstant.BUTTON_FINISH);
			finishBtn.set("ACT_NAME", this.wfAct.getNodeDef().getStr("PROC_END_NAME"));
			this.addBtnBean(finishBtn);
		}
	}

	/**
	 * 添加开始处理文件 按钮
	 */
	@SuppressWarnings("unused")
	private void addDuZhanBtn() {
		Bean duzhanBtn = this.getProcServDefAct(WfBtnConstant.BUTTON_DUZHAN);
		this.addBtnBean(duzhanBtn);
	}

	/**
	 * 添加终止并发
	 */
	private void addTerminateParall() {
		if (wfAct.canStopParallel()) {
			Bean stopParallBtn = this
					.getProcServDefAct(WfBtnConstant.BUTTON_STOP_PARALLEL);
			this.addBtnBean(stopParallBtn);
			this.removeBtnBean(WfBtnConstant.BUTTON_SAVE_SEND);
		}
	}

	/**
	 * 结束当前工作
	 */
	private void addFinishCurrBtn() {
		if (wfAct.canHandStopNode()) { // 节点运行状态，同时运行节点大于1
			Bean handFinishButton = this.getProcServDefAct(WfBtnConstant.BUTTON_STOP_NODE);
			handFinishButton.set("NI_ID", wfAct.getId());

			this.addBtnBean(handFinishButton);
		}
	}
	
    /**
     * 增加自定义变量
     */
    private void addCustomVarList() {
        Object obj = this.wfAct.getNodeDef().get(WfeConstant.CUSTOM_VARS);
        if (obj != null) {
            this.getOutBean().set(WfeConstant.CUSTOM_VARS, obj);
        }
    }
    
    /**
     * 从工作流定义中取得“查看审批单”更新字段表达式，并把字段及其执行结果放到OutBean中，不更新数据库中的数据。
     */
    private void appendViewItem() {
        List<Bean> list = this.wfAct.getNodeDef().getUpdateExpressWhenView();
        execUpdateExpress(list);
    }
    
    /**
     * @param exprList 表达式Bean列表。表达式Bean有3个属性：UPDATE_CONDS（更新条件），UPDATE_FIELD（更新字段名）， UPDATE_VALUE（更新表达式）。
     */
    private void execUpdateExpress(List<Bean> exprList) {
        if (exprList == null || exprList.size() == 0) {
            return;
        }
        
//        Bean dataBean = this.wfAct.getProcess().getServInstBean();
//        boolean needUpdate = false;
        JavaScriptEngine jsEng = new JavaScriptEngine(this.getOutBean());
//        jsEng.addVar("param", paramBean);
        
        MindContextHelper mindContext = new MindContextHelper(this.wfAct, this.getDoUser());
        jsEng.addVar("mindContext", mindContext);
        
        for (int i = 0; i < exprList.size(); i++) {
            Bean expBean = exprList.get(i);
            String conds = expBean.getStr("UPDATE_CONDS");

            if (jsEng.isTrueScript(conds)) {
                String field = expBean.getStr("UPDATE_FIELD");
                String valueExp = expBean.getStr("UPDATE_VALUE");
                String value = jsEng.runScript(valueExp);

                this.getOutBean().set(field, value);
//                needUpdate = true;
            }
        }

//        if (needUpdate) {
//            updateServInst(dataBean);
//        }
    }
    
    private UserBean tempUserBean = null;
    
    @Override
    public void setDoUser(UserBean aDoUser) {
    	this.tempUserBean = aDoUser;
    }
    
    /**
     * 初始化办理用户，并把这个值传递给客户端。
     */
    private void initDoUser() {
    	// 从流程中取得办理用户
    	Bean userBean = this.wfAct.getAvailableDoUser(tempUserBean.getCode());
    	if(userBean == null) {
    		throw new TipException("没有办理权限。USER:" + tempUserBean.getCode()  + ", NI_ID:" + this.wfAct.getId());
    	}
    	
		final String toDeptId = userBean.getStr("TO_DEPT_ID");
		final String toUserId = userBean.getStr("TO_USER_ID");
		UserBean doUserBean = UserMgr.getUser(toUserId, toDeptId);
		
		if(userBean.isNotEmpty("AUTH_USER")) { // 被授权人
			this.authUser = userBean.getStr("AUTH_USER");
			doUserBean.set("AUTH_USER", userBean.getStr("AUTH_USER"));
		}
		
		// 如果办理人就是当前用户，则不设置被授权人。
		if(tempUserBean.getCode().equals(toUserId)) {
			this.authUser = null;
		}
		
    	super.setDoUser(doUserBean);
    }
}
