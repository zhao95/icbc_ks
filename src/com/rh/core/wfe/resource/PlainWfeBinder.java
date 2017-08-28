package com.rh.core.wfe.resource;

import java.util.Collections;
import java.util.HashMap;

import com.rh.core.base.Bean;
import com.rh.core.util.JsonUtils;

/**
 * 
 * @author yangjinyun
 *
 */
public class PlainWfeBinder extends AbstractWfeBinder {
	
	private HashMap<String, Boolean> users = new HashMap<String, Boolean>();
	
	
	/**
	 * binderBean 中需要使用的数据：NAME、ID、SORT、LEVEL、NODETYPE。
	 */
	@Override
	public void addTreeBean(Bean binderBean) {
		final String id = binderBean.getStr("ID");
		// 不插入重复数据
		if (users.containsKey(id)) {
			return;
		}

		final String nodeType = binderBean.getStr("NODETYPE");
		// 如果是送用户，则不加入部门节点，只要用户节点
		if (WfeBinder.NODE_BIND_USER.equals(this.binderType) && nodeType.equals(AbstractWfeBinder.DEPT_NODE_PREFIX)) {
			return;
		}
		
		if (this.isIgnoreCurrentUser()) { // 忽略当前用户
			if (this.doUserBean != null && id.equals(WfeBinder.USER_NODE_PREFIX + ":" + this.doUserBean.getId())) {
				return;
			}
		}

		users.put(id, true);
		this.treeBeanList.add(binderBean);
	}
	
    @Override
	public void setRootBean(Bean root) {
        rootBean = root;
    }
	

	@Override
	public String getBinders() {
		// 将treebeanList 按照层级level进行排序
		ComparatorTreeLevel comparator = new ComparatorTreeLevel();
		Collections.sort(treeBeanList, comparator);

		// 组织数据
//		List<Bean> userList = new ArrayList<Bean>();

		// 获取根节点列表
		return JsonUtils.toJson(treeBeanList);
	}
	
	@Override
	public String getDisplayType() {
		return "COMBOBOX";
	}

}
