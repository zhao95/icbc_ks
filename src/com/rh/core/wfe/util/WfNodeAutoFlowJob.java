package com.rh.core.wfe.util;

import java.net.InetAddress;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.comm.ConfMgr;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.flow.FlowMgr;
import com.rh.core.util.DateUtils;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.Lang;
import com.rh.core.util.exception.ExceptionUtil;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;
import com.rh.core.wfe.WfAct;
import com.rh.core.wfe.WfParam;
import com.rh.core.wfe.WfProcess;
import com.rh.core.wfe.db.WfNodeInstDao;
import com.rh.core.wfe.def.WfNodeDef;
import com.rh.core.wfe.engine.NodeAutoFlow;
import com.rh.core.wfe.resource.WfeBinder;

/**
 * 处理自动流转节点实例线程job
 * 
 * @author Tanyh 20160603
 * 
 */
public class WfNodeAutoFlowJob extends RhJob {

	/** 日志对象 **/
	private Log log = LogFactory.getLog(WfNodeAutoFlowJob.class);

	/**
	 * 处理自动流转节点实例
	 */
	private void doAutoFlow() {
		// 每次轮循的最大数量
		final int max_count = Context
				.getSyConf("SY_WFE_AUTOFLOW_NODE_NUM", 100);
		// 允许同一个节点实例被记录的最高次数
		final int log_max_count = Context.getSyConf("SY_WFE_NODE_LOG_COUNT", 10);
		// 获取节点实例列表
		List<Bean> nodeInstList = findNodeBeanList(max_count, log_max_count);

		if (nodeInstList == null || nodeInstList.size() == 0) {
			return;
		}

		// 获取自动流转用户信息
		// @todo 整理sql添加此用户，放入db目录下
		UserBean userBean = UserMgr.getUser("AUTOFLOW");
		// 设置当前用户信息
		Context.setOnlineUser(userBean);
		for (Bean nodeInst : nodeInstList) {
			// 节点对象
			WfAct currAct = new WfAct(nodeInst.getStr("NI_ID"), true);
			// 流程对象
			WfProcess wfProcess = currAct.getProcess();
			// 节点定义对象
			WfNodeDef nodeDef = currAct.getNodeDef();
			// 流程参数对象
			WfParam wfParam = new WfParam();
			// 办理用户信息
			wfParam.setDoneUser(userBean);
			// 办理类型，正常结束
			wfParam.setDoneType(WfeConstant.NODE_DONE_TYPE_END);
			// 办理描述
			wfParam.setDoneDesc(WfeConstant.NODE_DONE_TYPE_END_DESC);

			try {
				if (WfeConstant.NODE_OPT_TYPE_AUTO_END == currAct
						.getNodeInstBean().getInt("OPT_TYPE")) {// 自动办结
					finish(userBean, currAct, wfProcess, nodeDef, wfParam);
				} else {
					// 待合并节点，不能往下流转
					if (currAct.canStopParallel()) {
						// 如果出现未合并的汇合节点，则处理合并操作后，不往下执行
						currAct.convergeNodeWhenException();
						continue;
					}
					// 判断是否合并节点，是，则判断是否已被合并
					if (currAct.isConvergeNode()) {
						Bean nodeInstBean = WfNodeInstDao
								.findNodeInstById(currAct.getId());
						// 已被合并，则不往下执行
						if (nodeInstBean.getInt("NODE_IF_RUNNING") == WfeConstant.NODE_NOT_RUNNING) {
							continue;
						}
					}
					
					// 获取可送交的下一节点
					List<Bean> nextSteps = currAct.getNodeDefBtns(userBean,
							currAct.getCode());
					// 填写意见
					if (nodeDef.getStr("MIND_CODE").length() > 0) {
						fillMindContent(currAct, userBean);
					}
					if (nextSteps.size() > 0) {
						// 送交类型 送给用户
						wfParam.setTypeTo(WfParam.TYPE_TO_USER);
						send2NextSteps(userBean, currAct, wfParam, nextSteps);
					} else {
						// 没有找到下一节点，则默认办结，并记入日志表中
						WfLogHelper.writeLog(currAct, "节点：" + currAct.getCode()
								+ "，未找到待送交节点，自动进行办结处理",
								WfLogHelper.LOG_TYPE_EXCEPTION);						
						finish(userBean, currAct, wfProcess, nodeDef, wfParam);
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				WfLogHelper.writeLog(currAct,
						"流程：" + currAct.getProcess().getCode() + "，节点："
								+ currAct.getCode() + "，送交失败" + e.getMessage(),
						WfLogHelper.LOG_TYPE_ERROR);
			}
		}

		if (nodeInstList.size() == max_count) {// 待处理的数据量大于最大量，则递归处理
			doAutoFlow();
		}
	}
	/**
	 * 发送给下一批节点
	 * @param userBean
	 * @param currAct
	 * @param wfParam
	 * @param nextSteps
	 */
	private void send2NextSteps(UserBean userBean, WfAct currAct, WfParam wfParam, List<Bean> nextSteps) {
		for (Bean nextStep : nextSteps) {
			send2NextNode(userBean, currAct, wfParam, nextStep);
		}
	}
	
	/**
	 * 发送给下一个节点
	 * @param userBean
	 * @param currAct
	 * @param wfParam
	 * @param nextStep
	 */
	private void send2NextNode(UserBean userBean, WfAct currAct, WfParam wfParam, Bean nextStep) {
		ParamBean pb = new ParamBean();
		List<Bean> userList = currAct.getNodeUserBeanList();
		String doUserDept = getDoUserDeptStr(userBean, currAct, userList);
		
		pb.set("DO_USER_DEPT",  doUserDept);
		pb.set("NODE_CODE", nextStep.getStr("NODE_CODE"));
		
		// 获取下一节点的资源信息
		WfeBinder wfBinder = currAct.getWfBinder(pb);
		StringBuffer users = new StringBuffer();
		for (UserBean user : wfBinder.getUserBeanList()) {
			users.append("," + user.getCode() + "^" + user.getDeptCode());
		}
		if (users.length() <= 0) {
			// 没有找到节点资源信息，则写入日志
			WfLogHelper.writeLog(currAct, "待送交节点："
					+ nextStep.getStr("NODE_CODE")
					+ "，未找到资源信息",
					WfLogHelper.LOG_TYPE_EXCEPTION);
		} else {
			wfParam.setToUser(users.substring(1));
			// 增加流经信息
			addFlowRecord(currAct);
			// 送交下一节点
			currAct.toNextAndEndMe(
					nextStep.getStr("NODE_CODE"), wfParam);
			// 处理办理用户信息
			updateToUserID(currAct, userBean);
		}
		// 释放资源
		users.reverse();
	}
	
	/**
	 * 
	 * @param userBean
	 * @param currAct
	 * @return
	 */
	private String getDoUserDeptStr(UserBean userBean, WfAct currAct, List<Bean> userList) {
		Bean nodeUser = userList.get(0);
		StringBuilder doUserDept = new StringBuilder();
		doUserDept.append(nodeUser.getStr("TO_USER_ID")).append("^");
		doUserDept.append(nodeUser.getStr("TO_DEPT_ID"));
		doUserDept.append("@");
		doUserDept.append(userBean.getCode()).append("^").append(userBean.getDeptCode());
		return doUserDept.toString();
	}

	private void finish(UserBean userBean, WfAct currAct, WfProcess wfProcess, WfNodeDef nodeDef, WfParam wfParam) {
		// 增加流经信息
		addFlowRecord(currAct);
		// 填写意见
		if (nodeDef.getStr("MIND_CODE").length() > 0) {
			fillMindContent(currAct, userBean);
		}
		// 结束流程
		wfProcess.finish(wfParam);
		// 办结之后，执行自动流转实现类的afterFinish方法
		NodeAutoFlow flowCls = getFlowCls(currAct);
		log.debug("WfNodeAutoJob finish:" + currAct.getId() + ";" + flowCls);
		if (flowCls != null) {
			flowCls.afterFinish(currAct);
		}
	}

	/**
	 * 处理节点办理用户信息
	 * 
	 * @param wfAct
	 *            节点实例
	 * @param userBean
	 *            办理用户
	 */
	private void updateToUserID(WfAct wfAct, UserBean userBean) {
		// 判断送交的下一节点操作码，是自动流转，则将办理人设置为自动流转用户
		if (WfeConstant.NODE_OPT_TYPE_AUTO_FLOW == wfAct.getNodeInstBean()
				.getInt("OPT_TYPE")
				|| WfeConstant.NODE_OPT_TYPE_AUTO_END == wfAct
						.getNodeInstBean().getInt("OPT_TYPE")) {
			Bean updateBean = new Bean();
			// 自动流转或自动办结，则更新入库
			updateBean.setId(wfAct.getId());
			// 设置更新字段
			updateBean.set("TO_USER_ID", userBean.getCode());
			updateBean.set("TO_USER_NAME", userBean.getName());
			WfNodeInstDao.updateWfNodeInst(updateBean, true);
		}
	}

	/**
	 * 添加流经的记录
	 * 
	 * @param currAct
	 *            当前节点实例
	 */
	private void addFlowRecord(WfAct currAct) {
		List<Bean> nodeUsers = currAct.getNodeUserBeanList();
		if (nodeUsers == null || nodeUsers.size() <= 0) {
			throw new TipException("节点：" + currAct.getCode() + "，ID："
					+ currAct.getId() + "未找到办理用户");
		}
		String docId = currAct.getProcess().getDocId();
		if (currAct.getNodeInstBean().getInt("TO_TYPE") == WfeConstant.NODE_INST_TO_SINGLE_USER) { // 送单个人
			UserBean userBean = UserMgr.getUser((nodeUsers.get(0)).getStr("TO_USER_ID"));
			userBean.set("AUTH_USER", (nodeUsers.get(0)).getStr("AUTH_USER"));
			FlowMgr.addUserFlow(docId,
					UserMgr.getUser((nodeUsers.get(0)).getStr("TO_USER_ID")),
					FlowMgr.FLOW_TYPE_FLOW);
		} else { // 送角色（多个人） 从节点用户中取发送的用户

			for (Bean nodeUser : nodeUsers) {
				UserBean userBean = UserMgr.getUser(nodeUser
						.getStr("TO_USER_ID"));
				userBean.set("AUTH_USER", nodeUser.getStr("AUTH_USER"));
				FlowMgr.addUserFlow(docId, userBean, FlowMgr.FLOW_TYPE_FLOW);
			}
		}
	}

	/**
	 * 轮循节点实例表，获取操作码OPT_TYPE字段为2或3的数据
	 * 
	 * @param maxCount
	 *            一次获取的最大数据量
	 * @param logMaxCount
	 *            同一个节点实例允许被记录的最高次数
	 * @return List<Bean> 结果列表
	 */
	private List<Bean> findNodeBeanList(int maxCount, int logMaxCount) {
		SqlBean sqlBean = new SqlBean();
		// 查询节点实例ID
		sqlBean.selects("NI_ID");
		// 查询节点实例表
		sqlBean.tables(ServMgr.SY_WFE_NODE_INST);
		// 过滤正在运行的节点实例
		sqlBean.and("NODE_IF_RUNNING", WfeConstant.NODE_IS_RUNNING);
		// 过滤操作码为2或3的节点实例
		sqlBean.and("(OPT_TYPE", WfeConstant.NODE_OPT_TYPE_AUTO_FLOW);
		sqlBean.or("OPT_TYPE", WfeConstant.NODE_OPT_TYPE_AUTO_END);
		sqlBean.getWhere().append(") ");
		if (logMaxCount > 0) {
			sqlBean.andSub("NI_ID", "not in", "select NI_ID from "
					+ WfLogHelper.WFE_LOG_SERV + " where LOG_COUNT >= ?",
					logMaxCount);
		}
		// 按开始时间正序排
		sqlBean.asc("NODE_BTIME");
		// 获取指定数量的数据
		sqlBean.limit(maxCount);

		return ServDao.finds(ServMgr.SY_WFE_NODE_INST, sqlBean);
	}

	/**
	 * 自动填写意见
	 * 
	 * @param nodeDef
	 * @param wfProcess
	 * @param currAct
	 * @param userBean
	 */
	private void fillMindContent(WfAct currAct, UserBean userBean) {
		// 节点配置了意见类型，自动填写节点上配置的意见内容
		if (currAct.getNodeDef().isEmpty("MIND_CODE")) {// 没有配置意见类型，则不填写意见
			return;
		}
		// 如果当前节点已填写意见，则跳过
		if (ServDao.count(ServMgr.SY_COMM_MIND,
				(new Bean()).set("WF_NI_ID", currAct.getId())) > 0) {
			return;
		}
		// 意见编码
		String mindCode = currAct.getNodeDef().getStr("MIND_CODE");
		// 意见类型
		Bean mindCodeBean = ServDao.find(ServMgr.SY_COMM_MIND_CODE, mindCode);
		if (mindCodeBean != null && !mindCodeBean.isEmpty()) {
			Bean mindBean = getMindContent(currAct);
			// 获取意见内容为空
			if (mindBean == null || mindBean.isEmpty()) {
				WfLogHelper.writeLog(currAct, "没有配置自动流转实现类或自动流转实现类执行出错",
						WfLogHelper.LOG_TYPE_EXCEPTION);
				return;
			}
			// 指定意见类型
			if (mindBean.isEmpty("MIND_CODE")) {
				// 意见编码
				mindBean.set("MIND_CODE", mindCode);
				mindBean.set("MIND_CODE_NAME", mindCodeBean.getStr("CODE_NAME"));
				// 意见级别
				mindBean.set("MIND_LEVEL", mindCodeBean.getInt("MIND_LEVEL"));
				// 意见显示规则
				mindBean.set("MIND_DIS_RULE",
						mindCodeBean.getInt("MIND_DIS_RULE"));
			}
			// 意见填写时间
			mindBean.set("MIND_TIME", DateUtils.getDatetime());
			// 意见类别 普通意见、手写意见
			mindBean.set("MIND_TYPE", 1);
			// 业务表单主键
			mindBean.set("DATA_ID", currAct.getProcess().getDocId());
			// 业务表单服务ID
			mindBean.set("SERV_ID", currAct.getProcess().getServId());
			// 节点实例
			mindBean.set("WF_NI_ID", currAct.getId());
			// 节点名称
			mindBean.set("WF_NI_NAME",
					currAct.getNodeInstBean().getStr("NODE_NAME"));
			// 环节
			mindBean.set("HUANJIE", currAct.getNodeInstBean().getStr("HJ"));
			mindBean.set("S_USER", userBean.getCode());
			mindBean.set("S_UNAME", userBean.getName());
			mindBean.set("S_DEPT", userBean.getDeptCode());
			mindBean.set("S_DNAME", userBean.getDeptName());
			mindBean.set("S_TDEPT", userBean.getTDeptCode());
			mindBean.set("S_TNAME", userBean.getTDeptName());
			mindBean.set("S_CMPY", userBean.getCmpyCode());
			mindBean.set("S_ODEPT", userBean.getODeptCode());
			ServDao.save(ServMgr.SY_COMM_MIND, mindBean);
		}
	}

	/**
	 * 
	 * @param wfAct
	 *            当前节点实例对象
	 * @return Bean 意见内容Bean
	 */
	private Bean getMindContent(WfAct wfAct) {
		NodeAutoFlow flowCls = getFlowCls(wfAct);
		Bean paramBean = getFlowParamBean(wfAct);
		if (flowCls != null) {
			return flowCls.getDefaultMindBean(wfAct, paramBean);
		} else {
			return new Bean();
		}
	}

	/**
	 * 根据节点配置获取自动流转接口实现类
	 * 
	 * @param wfAct
	 *            当前节点实例对象
	 * @return NodeAutoFlow接口实现类
	 */
	private NodeAutoFlow getFlowCls(WfAct wfAct) {
		String autoFlowConfStr = wfAct.getNodeDef().getAutoFlow();
		if (autoFlowConfStr == null || autoFlowConfStr.length() <= 0) {
			return null;
		}
		String cls = "";
		int pos = autoFlowConfStr.indexOf(",");
		if (pos > 0) {
			cls = autoFlowConfStr.substring(0, pos);
		} else {
			// 没有配置参数
			cls = autoFlowConfStr;
		}
		try {
			NodeAutoFlow flowCls = Lang.createObject(NodeAutoFlow.class, cls);
			return flowCls;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 根据节点配置获取自动流转配置参数
	 * 
	 * @param wfAct
	 *            当前节点实例对象
	 * @return Bean 配置参数对象
	 */
	private Bean getFlowParamBean(WfAct wfAct) {
		String autoFlowConfStr = wfAct.getNodeDef().getAutoFlow();
		String config = "";
		if (autoFlowConfStr == null || autoFlowConfStr.length() <= 0) {
			return new Bean();
		}
		int pos = autoFlowConfStr.indexOf(",");
		if (pos > 0) {
			config = autoFlowConfStr.substring(pos + 1);
		} else {
			// 没有配置参数
			config = "";
		}
		try {
			return JsonUtils.toBean(config);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new Bean();
		}
	}

	@Override
	protected void executeJob(RhJobContext context) {
		try {
			// 获取启动Job的主机IP和端口(用户在集群环境下部署，多台主机独占同步任务)
			if (ConfMgr.getConf("SY_WF_AUTO_HOST", "").length() == 0
					|| getHostName().equals(
							ConfMgr.getConf("SY_WF_AUTO_HOST", ""))) {
				// 开始处理自动流转节点实例
				doAutoFlow();
			}
		} catch (Exception e) {
			log.error("处理自动流转节点出错：" + e.getMessage(), e);
			WfLogHelper.writeLog(ExceptionUtil.toMsgString(e),
					WfLogHelper.LOG_TYPE_ERROR);
		}
	}

	/**
	 * 获取运行环境的主机名
	 * 
	 * @return 主机名
	 */
	private String getHostName() {
		String hostName = "";
		try {
			hostName = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (Exception e) {
			log.error("无法获得主机地址:" + e.getMessage());
			WfLogHelper.writeLog(ExceptionUtil.toMsgString(e),
					WfLogHelper.LOG_TYPE_ERROR);
		}
		return hostName;
	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub

	}

}
