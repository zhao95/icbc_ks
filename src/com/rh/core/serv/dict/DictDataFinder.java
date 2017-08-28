package com.rh.core.serv.dict;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.Lang;

/**
 * 
 * 数据字典数据查询器
 * @author yangjy
 *
 */
public class DictDataFinder {
    /** 最大返回条数 **/
    private int maxSize = 10;
    
    private List<Bean> resultList = null;
    
    private Bean dict = null;
    
    private Bean subDict = null;
    
    // 需要查询的字段
    private List<String> findItemCodes = new ArrayList<String>();
    
    /** 只返回叶子节点？ **/
    private boolean onlyLeaf = false;
    
    /**
     * 
     * @param dict 字典定义Bean
     * @param paramBean 参数Bean
     */
    public DictDataFinder(Bean dict , ParamBean paramBean) {
        this.maxSize = paramBean.get("maxSize", 10);
        this.dict = dict;
        this.subDict = (dict.isNotEmpty("DICT_CHILD_ID")) ? DictMgr
                .getDict(dict.getStr("DICT_CHILD_ID")) : null;
        findItemCodes = new ArrayList<String>();
        findItemCodes.add("NAME");
        if (paramBean.isNotEmpty("findItems")) {
            String[] itemCodes = paramBean.getStr("findItems").split(",");
            for (String itemCode : itemCodes) {
                findItemCodes.add(itemCode);
            }
        }
        
        onlyLeaf = paramBean.getStr("findType").equals("LEAF");
    }
    
    /**
     * @param pid 父节点ID
     * @param findKey 查询字段名
     * @return 符合条件的结果
     */
    public List<Bean> findTreeList(String pid , String findKey) {
        resultList = new ArrayList<Bean>();
        if (dict == null) {
            return resultList;
        }
        
        findKey = StringUtils.lowerCase(findKey);
        
        if (StringUtils.isEmpty(pid)) { // 如果没有指定父
            if (dict.isNotEmpty("DICT_ROOT")) { // 且设定了动态根，用动态根
                pid = ServUtils.replaceSysVars(dict.getStr("DICT_ROOT"));
            }
        }
        Bean itemCmpyBean = DictMgr.getItemCmpyBean(dict); // 获取字典数据信息
        LinkedHashMap<String, Bean> nodeMap = itemCmpyBean
                .getLinkedMap(DictMgr.CHILD_NODE_MAP);
        List<Bean> dataList;
        if ((pid.length() == 0) || !nodeMap.containsKey(pid)) {
            dataList = itemCmpyBean.getList(DictMgr.CHILD_NODE);
        } else {
            dataList = nodeMap.get(pid).getList(DictMgr.CHILD_NODE);
        }
        
        // 分级获取数据，包含当前层级
        recurFindSubList(dataList, findKey);
        
        return resultList;
    }
    
    /**
     * 分级获取数据，获取的总级数包含当前层级
     * 
     * @param treeList 当前级别数据
     * @param findKey 查询关键字
     */
    @SuppressWarnings("unchecked")
    private void recurFindSubList(List<Bean> treeList , String findKey) {
        String exp = dict.getStr("DICT_EXPRESSION");
        String subExp = (subDict != null) ? this.subDict
                .getStr("DICT_EXPRESSION") : "";
        boolean isEmpty = StringUtils.isBlank(findKey);
        for (Bean item : treeList) {
            if (resultList.size() >= maxSize) {
                break;
            }
            // 根据数据规则表达式确定是否包含在显示结果中
            // 独立树或者父子树的枝节点判断当前字典的规则表达式
            if ((dict.isEmpty("DICT_CHILD_ID") || (item.getInt("LEAF") != Constant.YES_INT))) {
                if (exp.length() > 0) {
                    String str = ServUtils.replaceSysAndData(exp, item);
                    if (!Lang.isTrueScript(str)) {
                        continue;
                    }
                }
            } else if (subExp.length() > 0) { // 父子树的叶子节点判断子字典的规则表达式
                if (!Lang.isTrueScript(ServUtils
                        .replaceSysAndData(subExp, item))) {
                    continue;
                }
            }
            
            if (item.contains(DictMgr.CHILD_NODE)) {
                recurFindSubList((List<Bean>) item.get(DictMgr.CHILD_NODE),
                        findKey);
            }
            
            // 如果要记录叶子节点，且当前节点不是叶子节点，则
            if (onlyLeaf && !item.getBoolean("LEAF")) {
                continue;
            }
            
            if ((isEmpty || containsKey(item, findKey))) {
                Bean out = (Bean) item.copyOf();
                out.remove(DictMgr.CHILD_NODE);
                resultList.add(out);
            }
        } 
    }
    
    /**
     * 是否包括关键字
     * 
     * @param item 字典数据
     * @param findKey 关键字
     * @return 指定字段包含关键字则返回true，否则false。
     */
    private boolean containsKey(Bean item , String findKey) {
        for (String itemCode : this.findItemCodes) {
            if (item.getStr(itemCode).indexOf(findKey) >= 0) {
                return true;
            }
        }
        
        return false;
    }
}
