package com.rh.core.comm.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.rh.core.base.Bean;

/**
 * 文件上传监听类
 * @author yangjy
 * 
 */
public interface FileUploadListener {
    /**
     * 文件上传配置的前缀
     */
    String CONF_PREFIX = "FILE_UPLOAD_LISTENER_";
    
    /**
     * 以服务来区分的文件监听类配置前缀
     * 获取配置的方式是CONF_PREFIX_LISTENER + SERV_ID
     */
    String CONF_PREFIX_LISTENER = "SERV_FILE_UPLOAD_LISTENER_";

    /**
     * 上传文件之前调用
     * @param paramBean request中的获取的参数数据
     */
    void beforeAdd(Bean paramBean);
    
    /**
     * 上传文件之后调用
     * @param paramBean request中的获取的参数数据
     * @param dataList 修改后的数据列表对象
     */
    void afterAdd(Bean paramBean, List<Bean> dataList);
    
    /**
     * 更新文件之前调用
     * @param paramBean request中的获取的参数数据
     * @param fileBean 文件Bean的所有数据
     */
    void beforeUpdate(Bean paramBean, Bean fileBean);

    /**
     * 更新文件之后调用
     * @param paramBean request中的获取的参数数据
     * @param fileBean 文件Bean的所有数据
     * @param dataList 修改后的数据列表对象
     */
    void afterUpdate(Bean paramBean, Bean fileBean, List<Bean> dataList);
    
    /**
     * 
     * @return 是否启用download 方法。
     */
    boolean enableDownload();
    
    /**
     * 
     * @param request HttpRequest对象
     * @param fileBean 文件Bean对象
     * @return 文件流
     * @throws IOException 
     */
    InputStream download(HttpServletRequest request, Bean fileBean) throws IOException;
    
    /**
     * 
     * @param fileBean 文件Bean对象
     */
    void afterDownload(Bean fileBean);
}
