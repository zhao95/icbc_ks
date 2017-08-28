package com.rh.core.comm.print;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.FileMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.freemarker.FreeMarkerUtils;

/**
 * 
 * @author zhanghailong
 * 
 */
public class FileDataHelper {
    private List<Bean> fileList = null;

    /**
     * 
     * @param servSrcId 服务ID
     * @param dataId 关联公文的数据ID
     */
    public FileDataHelper(String servSrcId, String dataId) {
        fileList = FileMgr.getFileListBean(servSrcId, dataId);
    }
    
    /**
    * 获取相关文件名，多个文件以逗号分割
    * @param dataid 主键
    * @param srcServId 服务id
    * @return 相关文件名
    */
    public String getRelateFileNames(String dataid, String srcServId) {
        SqlBean sqlBean = new SqlBean();
        sqlBean.and("DATA_ID", dataid).and("SERV_ID", srcServId).and("QUERY_ODEPT", 
                Context.getUserBean().getODeptCode());
        List<Bean> relateList = ServDao.finds("SY_SERV_RELATE", sqlBean);
        if (relateList.size() == 0) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        for (Bean reBean : relateList) {
            str.append(",");
            str.append(reBean.getStr("TITLE"));
        }
        return str.substring(1);
    }

    /**
     * 获取文件名称
     * @param fileCat 文件类型编码
     * @return 多个文件名使用都好分割
     */
    public String getFileNames(String fileCat) {
        List<Bean> list = this.getFileList(fileCat);
        Bean bean = new Bean();
        bean.set("fileList", list);
        //模板文件路径默认为：/sy/comm/print/ftl/printFileList.ftl
        String fileName = Context.appStr(Context.APP.SYSPATH) + File.separator
                + "sy" + File.separator + "comm" + File.separator + "print"
                + File.separator + "ftl" + File.separator + "printFileList.ftl";

        return FreeMarkerUtils.parseText(fileName, bean);
    }

    /**
     * 获取文件列表
     * @param fileCat 文件类型
     * @return 指定类型的文件列表
     */
    public List<Bean> getFileList(String fileCat) {
        List<Bean> result = new ArrayList<Bean>();
        for (Bean bean : fileList) {
            if (bean.getStr("FILE_CAT").equals(fileCat)) {
                result.add(bean);
            }
        }

        return result;
    }
}
