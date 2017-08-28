package com.rh.core.wfe.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;

/**
 * 绑定树排序
 * 
 */
public class BinderBeanSort {
	private static Log log = LogFactory.getLog(BinderBeanSort.class);

	/**
	 * 对同一level 的Bean排序
	 * 
	 * @param treeBeanList
	 *            树的节点 列表
	 */
	@SuppressWarnings("unchecked")
	public static void sortTheBindList(List<WfResTreeNode> treeBeanList) {
		ComparatorBean comparator = new ComparatorBean();

		List<WfResTreeNode> userList = new ArrayList<WfResTreeNode>();
		List<WfResTreeNode> deptList = new ArrayList<WfResTreeNode>();
		for (WfResTreeNode treeBean: treeBeanList) {
			if (treeBean.getStr("NODETYPE").equals(WfeBinder.USER_NODE_PREFIX)) {
				userList.add(treeBean);
			} else {
				deptList.add(treeBean);
			}
			
			if (null != treeBean.get("CHILD")) {
				sortTheBindList((List<WfResTreeNode>) treeBean.get("CHILD"));
			}
		}
		//如果部门下既有处室，又有用户， 并且 用户的排序号大于 处室的排序号，则会出现，部门下的用户排到处室后面的情况
		Collections.sort(userList, comparator);
		Collections.sort(deptList, comparator);
		
		treeBeanList.clear();
		treeBeanList.addAll(userList); //先加用户
		treeBeanList.addAll(deptList); //再加部门
	}

}

/**
 * 
 *
 */
class ComparatorBean implements Comparator<Bean> {
	/**
	 * 实现接口的方法
	 * 
	 * @param arg0  比较的对象
	 * @param arg1 比较的对象
	 * @return 比较结果
	 */
	public int compare(Bean arg0, Bean arg1) {
		Bean bean1 = (Bean) arg0;
		Bean bean2 = (Bean) arg1;

		// 比较排序
		if (bean1.getInt("SORT") > bean2.getInt("SORT")) {
			return 1;
		}
		
		if (bean1.getInt("SORT") == bean2.getInt("SORT")) {
			return 0;
		}
		
		return -1;
	}
}
