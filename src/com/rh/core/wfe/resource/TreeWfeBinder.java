package com.rh.core.wfe.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.util.JsonUtils;

/**
 * 存放节点可办理用户的组织关系，及其操作的类
 * 
 * @author yangjy
 *
 */
public class TreeWfeBinder extends AbstractWfeBinder {
    
    /** 返回的过滤之后的树的节点列表 */
    private List<Bean> treeBeanList = new ArrayList<Bean>();
    
    private HashMap<String, Bean> treeNodeMap = new HashMap<String, Bean>();
    
    
	@Override
	public void addTreeBean(Bean binderBean) {
		if (!treeNodeMap.containsKey(binderBean.getStr("ID"))) {
			this.treeBeanList.add(binderBean);
			treeNodeMap.put(binderBean.getStr("ID"), binderBean);
		}
	}
	

    @Override
	public String getBinders() {
    	//将treebeanList 按照层级level进行排序
		ComparatorTreeLevel comparator = new ComparatorTreeLevel();
		Collections.sort(treeBeanList, comparator);
    	
		//将树组织起来，添加子节点
		WfResTreeHelper wfResTree = new WfResTreeHelper();
    	for (Bean treeBean: treeBeanList) {
    		wfResTree.addNode(treeBean);
    	}
    	
    	//获取根节点列表
    	List<WfResTreeNode> treeNodeList = wfResTree.getRootNodeList();
    	
    	//将树上每一级内的兄弟按照sort排序
    	BinderBeanSort.sortTheBindList(treeNodeList);
    	
		return JsonUtils.toJson(treeNodeList);
    }


	@Override
	public String getDisplayType() {
		return "TREE";
	}
}

 
/**
 * 树按层排序
 *
 */
class ComparatorTreeLevel implements Comparator<Bean> {
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
		if (bean1.getInt("LEVEL") > bean2.getInt("LEVEL")) {
			return 1;
		}
		
		if (bean1.getInt("LEVEL") == bean2.getInt("LEVEL")) {
			return 0;
		}
		
		return -1;
	}
}