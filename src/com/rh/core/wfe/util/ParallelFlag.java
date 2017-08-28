package com.rh.core.wfe.util;

import org.apache.commons.lang.StringUtils;

/**
 * 并发目标
 * 
 * @author yangjy
 */
public class ParallelFlag {
    
    /**
     * 多个并发节点标记之间的分隔符
     */
    public static final String MULTI_FLAG_SEPERATOR = ",";
    
    /**
     * 1个节点标记中区分节点编号与并发序号之间的分隔符。
     */
    public static final String FLAG_SEPERATOR = "-";
    
    /**
     * 空节点编码，表示不汇合
     */
    public static final String NULL_NODE_CODE = "NULL_NODE";
    
    /**
     * @param preParallelFlag 前一个点的并发标记
     * @param nodeCode 并发目标点节点Code
     * @param indexNum 并发顺序号
     * @return 并发标记字符串
     */
    public static String createParallelFlagString(String preParallelFlag ,
        String nodeCode ,
        int indexNum) {
        String result = "";
        if (StringUtils.isNotEmpty(preParallelFlag)) { // 已经有并发流标记，则
            result = preParallelFlag + nodeCode + FLAG_SEPERATOR + indexNum
                    + MULTI_FLAG_SEPERATOR;
        } else { // 没有并发流标记
            result = nodeCode + FLAG_SEPERATOR + indexNum
                    + MULTI_FLAG_SEPERATOR;
        }
        
        return result;
    }
    
    /**
     * @param oldParallerFlag 并发标记
     * @param convergedNode 汇合节点
     * @return 返回空表示汇合所有点。如果为NULL_NODE,则表示不汇合，否则返回需要汇合的并发流节点前缀。
     */
    public static String mergeParallelFlag(String oldParallerFlag ,
        String convergedNode) {
        // 需要汇合点的点
        if (StringUtils.isEmpty(convergedNode)
                || StringUtils.isEmpty(oldParallerFlag)) {
            return "";
        }
        
        String[] nodes = convergedNode.split(",");
        String result = oldParallerFlag;
        for (String node: nodes) {
            result = mergeSingleParallelFlag(result, node);
        }
        
        return result;
    }
    
    /**
     * 
     * @param oldParallerFlag 并发标记
     * @param convergedNode 单个汇合目标
     * @return 返回空表示汇合所有点。如果为NULL_NODE,则表示不汇合，否则返回需要汇合的并发流节点前缀。
     */
    private static String mergeSingleParallelFlag(String oldParallerFlag ,
        String convergedNode) {
        
        final String targetNode = convergedNode + FLAG_SEPERATOR;
        if (oldParallerFlag.startsWith(targetNode)) {
            return "";
        } else {
            // 查找是否存在并发点的节点Code
            int pos = oldParallerFlag.indexOf(MULTI_FLAG_SEPERATOR + targetNode);
            
            if (pos == -1) { // 不存在则不汇合
                return oldParallerFlag;
            } else if (pos == 0) {
                return "";
            }
            return oldParallerFlag.substring(0, pos);
        }
    }
}
