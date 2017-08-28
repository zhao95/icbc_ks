package com.rh.core.wfe.util;


/**
 * 工作流中 用到的 常量
 * 
 * @author ananyuan
 * 
 */
public class WfeConstant {



	/**
	 * 工作流函数定义 服务 code
	 */
	public static final String SY_WFE_FUNC_DEF_SERV = "SY_WFE_FUNC_DEF";
	
    /**
     * 工作流定义：自定义变量
     */
    public static final String SY_WFE_CUSTOM_VAR = "SY_WFE_CUSTOM_VAR";

	/**
	 * 流程节点实例运行
	 */
	public static final int WFE_NODE_INST_IS_RUNNING = 1;

	/**
	 * 流程节点实例没有运行
	 */
	public static final int WFE_NODE_INST_NOT_RUNNING = 2;

	/**
	 * 流程未办结
	 */
	public static final int PROC_IS_RUNNING = 1;
	
	/**
	 * 流程已经办结
	 */
	public static final int PROC_NOT_RUNNING = 2;
	
	/**
	 * 
	 */
	public static final String FILTER_SCRIPT = "filterScript";

	/**
	 * 节点绑定 模式 1 指定
	 */
	public static final int NODE_BIND_MODE_ZHIDING = 1;

	/**
	 * 节点绑定 模式 2 全部
	 */
	public static final int NODE_BIND_MODE_ALL = 2;

	/**
	 * 节点绑定 模式 3 预定义
	 */
	public static final int NODE_BIND_MODE_PREDEF = 3;

	/**
	 * 节点绑定 类型 1 部门
	 */
	public static final int NODE_BIND_TYPE_DEPT = 1;

	/**
	 * 节点绑定 类型 2 角色
	 */
	public static final int NODE_BIND_TYPE_ROLE = 2;

	/**
	 * 节点绑定 类型 3 用户
	 */
	public static final int NODE_BIND_TYPE_USER = 3;

	/**
	 * 工作流 起草节点
	 */
	public static final int NODE_TYPE_DRAFT = 1;

	/**
	 * 工作流 活动节点
	 */
	public static final int NODE_TYPE_ACTIVE = 2;
	
	/**
     * 工作流结束点
     */
    public static final int NODE_TYPE_END = 3;
	
	/**
     * 工作流 子流程类型节点
     */
    public static final int NODE_TYPE_SUB_PROCESS = 4;

	/**
	 * 工作流 节点 运行状态
	 */
	public static final int NODE_IS_RUNNING = 1;

	/**
	 * 工作流 节点非 运行 状态
	 */
	public static final int NODE_NOT_RUNNING = 2;

	/**
	 * 工作流 连线 能够回退
	 */
	public static final int LINE_CAN_RETURN = 1;

	/**
	 * 工作流 连线 不能够回退
	 */
	public static final int LINE_CAN_NOT_RETURN = 2;

	/**
	 * 节点办理类型 正常结束
	 */
	public static final int NODE_DONE_TYPE_END = 1;
	
	/**
	 * 节点办理类型 正常结束
	 */
	public static final String NODE_DONE_TYPE_END_DESC = "正常结束";

	/**
	 * 节点办理类型 终止
	 */
	public static final int NODE_DONE_TYPE_STOP = 2;
	
	/**
	 * 节点办理类型 终止
	 */
	public static final String NODE_DONE_TYPE_STOP_DESC = "终止";

	/**
	 * 节点办理类型 收回
	 */
	public static final int NODE_DONE_TYPE_WITHDRAW = 4;
	
	/**
	 * 节点办理类型 收回
	 */
	public static final String NODE_DONE_TYPE_WITHDRAW_DESC = "取回";
	
	/**
	 * 节点办理类型 ：取消办结
	 */
	public static final int NODE_DONE_TYPE_UNDO = 8;
	
	/**
	 * 节点办理类型 ：取消办结
	 */
	public static final String NODE_DONE_TYPE_UNDO_DESC = "取消办结";
	
	
	/**
	 * 节点办理类型 ：办结
	 */
	public static final int NODE_DONE_TYPE_FINISH = 16;
	
	/**
	 * 节点办理类型 ：办结
	 */
	public static final String NODE_DONE_TYPE_FINISH_DESC = "办结";
	
	/**
	 * 节点办理类型 ：合并
	 */
	public static final int NODE_DONE_TYPE_CONVERGE = 32;
	
	/**
	 * 节点办理类型 ：合并
	 */
	public static final String NODE_DONE_TYPE_CONVERGE_DESC = "合并";
	
	/**
     * 节点办理类型 ：类型转换
     */
    public static final int NODE_DONE_TYPE_CONVERTION = 64;
    
    /**
     * 节点办理类型 ：类型转换
     */
    public static final String NODE_DONE_TYPE_CONVERTION_DESC = "类型转换";
	
	/**
     * 节点办理类型 ：自动退回
     */
    public static final int NODE_DONE_TYPE_AUTORTN = 128;
    
    /**
     * 节点办理类型 ：自动退回
     */
    public static final String NODE_DONE_TYPE_AUTORTN_DESC = "自动退回";
    
    /**
     * 节点办理类型 ：通过
     */
    public static final int NODE_DONE_TYPE_PERMISSION = 256;
    
    public static final String NODE_DONE_TYPE_PERMISSION_DESC = "通过";
    
    /**
     * 废止
     */
    public static final int NODE_DONE_TYPE_TERMINATE = 512;
    
    /**
     * 废止
     */
    public static final String NODE_DONE_TYPE_TERMINATE_DESC = "废止";
    
    
	/**
	 * 节点为并发点
	 */
	public static final int NODE_IS_PARALLEL = 1;
	
	/**
	 * 节点不是并发点
	 */
	public static final int NODE_IS_NOT_PARALLEL = 2;
	
	/**
	 * 节点为合并点
	 */
	public static final int NODE_IS_CONVERGE = 1;
	
	/**
	 * 节点不是合并点
	 */
	public static final int NODE_IS_NOT_CONVERGE = 2;
	
	
	/**
	 * 节点  能 办结 流程
	 */
	public static final int NODE_CAN_END_PROC = 1;
	
	/**
	 * 节点 不能 办结流程
	 */
	public static final int NODE_CAN_NOT_END_PROC = 2;
	
	/**
	 * 任务送交给单个处理人处理  值为3
	 */
    public static final int NODE_INST_TO_SINGLE_USER = 3;
    
    /**
     * 任务送交给多个处理人处理  值为1
     */
    public static final int NODE_INST_TO_MULTI_USER = 1;
    
    /**
     * 节点送交类型：3 送用户
     */
    public static final int NODE_TO_USER = 3;
    
	/**
	 * 条件 角色
	 */
	public static final String WF_LINE_CONS_ROLE = "角色";
	
	/**
	 * 字段控制类型：1 完全控制；
	 */
	public static final int WF_FIELD_CONTROL_ALL = 1;
	
	
	/**
	 * 字段控制类型：2 只读控制
	 */
	public static final int WF_FIELD_CONTROL_READ = 2;
	
	
	/**
	 * 是否启用节点控制 1.启用
	 */
	public static final int PROC_ACT_CONTROL = 1;

	/**
	 * 是否启用节点控制 2.禁用
	 */
	public static final int PROC_ACT_NOT_CONTROL = 2;
	
	
	/**
	 * 节点 是否 自动结束 1,是
	 */
	public static final int NODE_AUTO_END = 1;

	/**
	 * 节点 是否 自动结束 2,否
	 */
	public static final int NODE_AUTO_END_NO = 2;
	
	/**
	 * 用户预定义：拟稿人
	 */
	public static final String USER_YUDING_DRAFT_USER = "draftUser";
	
	/**
	 * 用户预定于：当前用户
	 */
	public static final String USER_YUDING_CURRENT_USER = "currentUser";
	
	/** 用户预定义：目标节点最后一个办理用户**/
	public static final String USER_YUDING_TARGET_NODE_LAST_USER = "targetNodeLastUser";
	
    /**
	 * 自定义变量：用户自定义
	 */
    public static final String CUSTOM_VARS = "WF_CUSTOM_VARS";
    
    
    /** 流程编码中公司的前缀 */
    public static final String PROC_CMPY_PREFIX = "@";

    /** 流程编码中版本的前缀 */
    public static final String PROC_VERSION_PREFIX = "@";

    /** 流程定义为最新版 */
    public static final int PROC_IS_LATEST = 1;
    
    /** 流程定义不是最新版 */
    public static final int PROC_IS_NOT_LATEST = 2;
    
    
    /** 补登意见  角色 范围 为 本部门 */
    public static final int BUDENG_SCOPE_DEPT = 1;
    
    /** 补登意见  角色 范围 为 本机构 */
    public static final int BUDENG_SCOPE_ORG = 2;
    
    /** 流程  按钮组  */
    public static final String PROC_BTN_GROUP = "GROUP_CODE";

    /** 流程 按钮组 */
    public static final String PROC_BTN_GROUP_NAME = "GROUP_NAME";
    
    /** 流程  按钮组 其他  */
    public static final String PROC_BTN_GROUP_OTHER = "OTHER_ACT_GROUP";

    /** 流程 按钮组 其他名称 */
    public static final String PROC_BTN_GROUP_OTHER_NAME = "其它操作";
    
    /** ----------------------工作流按钮渲染模式系统配置状态值--------------------------------------- **/
    /** 操作按钮平铺，流程按钮平铺 */
    public static final String FLAT_FLAT = "0";

    /** 操作按钮平铺，流程按钮下拉组 */
    public static final String FLAT_GROUP = "10";

    /** 操作按钮平铺，流程按钮单独按钮条 */
    public static final String FLAT_BAR = "11";

    /** 操作按钮下拉组，流程按钮平铺 */
    public static final String GROUP_FLAT = "100";

    /** 操作按钮下拉组，流程按钮下拉组 */
    public static final String GROUP_GROUP = "110";

    /** 操作按钮下拉组，流程按钮单独按钮条 */
    public static final String GROUP_BAR = "111";

    /** 系统配置：是否只显示汇合后的最后一条待办 **/
    public static final String CONF_WF_CONVERGE_LAST_TODO = "WF_CONVERGE_LAST_TODO"; 
    
    /** 穿透分隔符 **/
    public static final String FREE_SEPARATOR = "_"; 
    
    /** 自由节点ID前缀 **/
    public static final String FREE_NODE_ID_PREFIX = "FREE-";
    
    /**
     * 工作流按钮渲染模式。
     */
    public static final String CONF_WF_BTN_RENDER = "SY_WF_BTN_RENDER";
    
    /** 出现处理完毕对话框  **/
    public static final String CONF_WF_BTN_RENDER_CONFIRM_WINDOW = "12";
    
    /** 一个节点可以送多个节点参数  **/
    public static final String MULT_NODE = "_MULT_NODE";
    
    /** 当前处理节点对象 **/
    public static final String CURR_WF_ACT = "_CURR_WF_ACT";
    
    /** 意见列表在主单是否显示 **/
    public static final String MIND_LIST_NOT_SHOW = "_MIND_LIST_NOT_SHOW";
    
    /** 能删除流程不校验权限  **/
    public static final String DEL_WF_IGNORE_RIGHT = "__DEL_WF_IGNORE_RIGHT";
    
    /** 强制办结流程标志：不判断并发  **/
    public static final String FINISH_WF_FORCE_FLAG = "__FINISH_WF_FORCE_FLAG";
    
    /** 审批单上定义的Act的前缀 **/
    public static final String PREFIX_SERV_ACT = "ACT-";
    
    /** 流程上定义的Act的前缀 **/
    public static final String PREFIX_PROC_ACT = "ACTP-";
    
    /** 操作类型变量名称 **/
    public static final String ACT_TYPE = "_ACT_TYPE";
    
    /** 送交时 返回OutBean中 的下一个节点ID的key **/
    public static final String NEXT_NI_ID = "NEXT_NI_ID";
    
    /** 节点操作码   1人工流转   added by Tanyh 20160604 **/
    public static final int NODE_OPT_TYPE_MANUAL_FLOW = 1;
    
    /** 节点操作码   2自动流转   added by Tanyh 20160604 **/
    public static final int NODE_OPT_TYPE_AUTO_FLOW = 2;
    
    /** 节点操作码  3自动办结 added by Tanyh 20160604 **/
    public static final int NODE_OPT_TYPE_AUTO_END = 3;
    
}
