/*
 * Copyright (c) 2011 Ruaho All rights reserved.
 */
package com.rh.core.util.lang;



/**
 * 列表处理器
 * @param <T> 对象类型
 * @author liyanwei
 */
public interface ListHandler<T> {
    /**
     * 处理行数据
     * @param data 对象
     */
    void handle(T data);
}
