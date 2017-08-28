package com.rh.core.wfe.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.util.JsonUtils;

/**
 * 文件类型权限公用类
 * 
 */
public class FileController {
    /** 文档权限：下载文件 **/
    public static final int DOWNLOAD = 32;
    /** 文档权限：修改文件信息，如排序、文件名 **/
    public static final int MODIFY = 16;
    /** 文档权限：删除文件 **/
    public static final int DELETE = 8;
    /** 文档权限：上传新文件 **/
    public static final int UPLOAD = 4;
    /** 文档权限：编辑 **/
    public static final int WRITE = 2;
    /** 文档权限：只读 **/
    public static final int READ = 1;
    

    private List<Bean> filePrivBeanList;

    /**
     * {'VALUE':'3','TYPES':'*.jpg;*.jpeg;*.png;*.gif;*.doc;*.docx;*.wps;*.xls;*.xlsx;*.ppt;*.pptx;*.txt;'
     * ,'UPLOAD_BTN_NAME':'上传附件','FILENUMBER':'13'}
     * 
     * [{'ID':'GESHIHETONG','NAME':'合同列表','VALUE':'15','DESC':'格式合同文本'},
     * {'ID':'FUJIANLIEBIAO','NAME':'附件列表','VALUE':'15','DESC':'格式合同附件'}]
     * 
     * @param fileAuthStr 文件授权值
     */
    @SuppressWarnings("unchecked")
    public FileController(String fileAuthStr) {
        if (fileAuthStr.startsWith("{")) {
            Bean fileDefBean = JsonUtils.toBean(fileAuthStr);

            this.filePrivBeanList = (List<Bean>) fileDefBean.get("CAT");

        } else {
            this.filePrivBeanList = JsonUtils.toBeanList(fileAuthStr);
        }
    }

    /**
     * 
     * @param list 文件权限列表
     */
    public FileController(List<Bean> list) {
        this.filePrivBeanList = list;
    }

    /**
     * 
     * @return 文件类型转成的权限列表
     */
    public List<Bean> getFileControlBeanList() {
        return filePrivBeanList;
    }
    
    /**
     * 追加权限
     * @param val 权限值，只能为本类中指定的常量值
     */
    public void appendVal(int val) {
        for (Bean fileAuthBean : filePrivBeanList) {
            int oldVal = fileAuthBean.getInt("VALUE");
            if ((val & oldVal) == 0) {
                fileAuthBean.set("VALUE", oldVal + val);
            }
        }
    }

    /**
     * 保留服务上定义的最低权限：读和写
     */
    public void reserveMinPermission() {
        for (Bean fileAuthBean : filePrivBeanList) {
            String value = fileAuthBean.get("VALUE", "1");
            if (StringUtils.isNumeric(value)) {
                final int val = Integer.parseInt(value);
                int result = 1; // 默认读权限
                if ((val & 32) > 0) { // 如果有下载则增加下载权限
                    result += 32;
                }

                fileAuthBean.set("VALUE", result);
            } else {
                value = value.toLowerCase();
                String result = "r";
                if (value.contains("d")) {
                    result += "d";
                }

                fileAuthBean.set("VALUE", result);
            }
        }
    }
}
