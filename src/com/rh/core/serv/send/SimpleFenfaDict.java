package com.rh.core.serv.send;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.dict.DictItems;
import com.rh.core.serv.dict.DictMgr;

/**
 * 
 * @author yangjy
 * 
 */
public class SimpleFenfaDict implements DictItems {
    /**
     * 用户选择字典
     */
    private String userSelectDict = "SY_ORG_DEPT_USER_SUB";

    /**
     * 已分发用户ID集合
     */
    private Set<String> userCodes = null;
    
    /** 是否显示分发方案 **/
    private boolean displaySendSchm = true;
    
    /** 显示范围：本机构odept、本部门tdept **/
    private String displayScope = "";
    
    /** 所有部门信息 */
    private final LinkedHashMap<String, Bean> allDepts = DictMgr.getItemMap(DictMgr.getDict("SY_ORG_DEPT_ALL"));
    
    /**
     * 初始化参数
     * @param paramBean 参数Bean
     */
    private void initParam(ParamBean paramBean) {
        if (paramBean.isNotEmpty("DATA_ID")) {
            userCodes = SendUtils.getReceivedUserSet(paramBean.getStr("DATA_ID"));
        }

        if (paramBean.isNotEmpty("userSelectDict")) {
            this.userSelectDict = paramBean.getStr("userSelectDict");
        }

        if (paramBean.isNotEmpty("displaySendSchm")) {
            displaySendSchm = paramBean.getBoolean("displaySendSchm");
        }
        if (paramBean.isNotEmpty("displayScope")) {
            displayScope = paramBean.getStr("displayScope");
        } else {
            if (paramBean.isNotEmpty("includeSubOdept") && !paramBean.getBoolean("includeSubOdept")) { //兼容老参数
                displayScope = "odept";
            }
        }
    }

    /**
     * @param paramBean 参数Bean
     * @return 反馈给前台的字典数据
     */
    public Bean getItems(ParamBean paramBean) {
        initParam(paramBean);
        
        Bean root = new Bean();
        appendDictInfo(root);

        List<Bean> child = new ArrayList<Bean>();
        root.set("CHILD", child);

        if (paramBean.isEmpty("PID")) {
            if (this.displaySendSchm) {
                appendSendSchema(child);
            }
            appendDeptUser(child);
        } else {
            final String pid = paramBean.getStr("PID");
            int level = paramBean.getInt("LEVEL");
            if (level == 0) {
                level = 1;
            }
            appendDeptUser(child, pid, level);
        }

        return root;
    }

    /**
     * 增加本机构 和部门树
     * @param child 子节点列表
     */
    private void appendDeptUser(List<Bean> child) {
        List<Bean> list = DictMgr.getTreeList(userSelectDict, 2);
        if (list.size() > 0) {
            Bean bean = list.get(0);
            List<Bean> childList = bean.getList("CHILD");
            UserBean userBean = Context.getUserBean();
            List<Bean> mirrorChildList = new ArrayList<Bean>();
            for (Bean childBean : childList) {
                final String id = childBean.getStr("ID");
                /**根据显示策略过滤-开始*/
                if (!displayScope.isEmpty()) {
                    Bean data = allDepts.get(id);
                    if (displayScope.equals("odept")) { //只展示本机构
                        if (data != null && data.getInt("DEPT_TYPE") == 2) { //其他机构
                            continue;
                        }
                    } else if (displayScope.equals("tdept")) { //只展示本部门
                        if (!id.equals(userBean.getTDeptCode())) { //其他部门或人或机构
                            continue;
                        }
                    }
                }
                /**根据显示策略过滤-结束*/
                if (id.equals(userBean.getTDeptCode())) {//默认优先展开本部门树
                    childBean.set("isexpand", "true");
                    List<Bean> deptChildList = new ArrayList<Bean>();
                    childBean.set("CHILD", deptChildList);
                    appendDeptUser(deptChildList, userBean.getTDeptCode(), 1);
                } else if (isReceivedUser(id)) {//人员节点并且已经传阅过
                    childBean.set("NAME", childBean.getStr("NAME") + " (已传)");
                }
                mirrorChildList.add(childBean);
            }
            childList = mirrorChildList;
            bean.set("CHILD", childList);
        }
        child.addAll(list);
    }

    /**
     * 
     * @param child 树子节点列表
     * @param pid 父节点
     * @param level 加载级别
     */
    private void appendDeptUser(List<Bean> child, String pid, int level) {
        List<Bean> list = DictMgr.getTreeList(userSelectDict, pid, level);
        child.addAll(list);
        for (Bean bean : list) {
            final String id = bean.getStr("ID");
            if (isReceivedUser(id)) {
                bean.set("NAME", bean.getStr("NAME") + " (已传)");
            }
        }
    }

    /**
     * 
     * @param child 树子节点列表
     */
    private void appendSendSchema(List<Bean> child) {
        Bean schemaBean = new Bean();
        child.add(schemaBean);
        schemaBean.set("ID", "SendSchema");
        schemaBean.set("NAME", "选择发送机构");
        schemaBean.set("CODE", "SendSchema");
        schemaBean.set("LEAF", "2");
        schemaBean.set("isexpand", "false");

        List<Bean> childList = new ArrayList<Bean>();
        schemaBean.set("CHILD", childList);

        List<Bean> list = SendSchemeMgr.findScheme();
        for (Bean bean : list) {
            Bean treeNode = new Bean();
            final String id = SendConstant.PREFIX_SCHM + bean.getStr("SEND_ID");
            treeNode.set("ID", id);
            treeNode.set("NAME", bean.getStr("SEND_NAME"));
            treeNode.set("CODE", id);
            treeNode.set("LEAF", "1");
            childList.add(treeNode);
        }
    }

    /**
     * 增加字典基本信息
     * @param root 数据Bean
     */
    private void appendDictInfo(Bean root) {
        root.set("DICT_CHILD_ID", "ZhuanfaDict");
        root.set("DICT_DIS_LAYER", "0");
        root.set("DICT_DIS_ID", "ZhuanfaDict");
        root.set("DICT_NAME", "组织机构");
        root.set("DICT_TYPE", "2");
    }

    /**
     * 
     * @param userCode 用户ID
     * @return 是否已经接收了传阅数据。
     */
    private boolean isReceivedUser(String userCode) {
        if (this.userCodes != null && this.userCodes.contains(userCode)) {
            return true;
        }

        return false;
    }

	@Override
	public String getDictId(ParamBean paramBean) {
		return paramBean.getStr("userSelectDict");
	}
}
