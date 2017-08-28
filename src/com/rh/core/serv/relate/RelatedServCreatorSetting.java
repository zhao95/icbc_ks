package com.rh.core.serv.relate;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.util.XmlUtils;

/**
 * 从A审批单起草B审批单功能定义类.定义格式如下：<br>
 *  &lt;EXTEND_CLS&gt;实现类&lt;/EXTEND_CLS&gt;<br>
 *   &lt;ITEM_MAP&gt;<br>
 *    <!--新审批单的字段对应老审批单的字段--><br>
 *    &lt;GW_TITLE&gt;#GW_TITLE#&lt;/GW_TITLE&gt;  <br>
 *   &lt;/ITEM_MAP&gt;<br>
 *   &lt;COPY_FILE_LINK&gt;true&lt;/COPY_FILE_LINK&gt;<br>
 *   &lt;FILE_TYPE_MAP&gt;<br>
 *    &lt;ZHENGWEN.ZHENGWEN&gt;FUJIAN&lt;/ZHENGWEN.ZHENGWEN><br>
 *   &lt;/FILE_TYPE_MAP&gt;<br>
 *   &lt;REQUIRED_FILE_TYPE&gt;<br>
 *    &lt;INCLUDE&gt;ZHENGWEN.ZHENGWEN&lt;/INCLUDE&gt;<br>
 *    &lt;EXCLUDE&gt;ZHENGWEN.REDHEADER&lt;/EXCLUDE&gt;<br>
 *   &lt;/REQUIRED_FILE_TYPE&gt;<br>
 * @author yangjy
 * 
 */
public class RelatedServCreatorSetting extends Bean {
    private static final long serialVersionUID = -7486638148126257006L;
    private static final Log LOG = LogFactory.getLog(RelatedServCreatorSetting.class);

    /** 扩展类，RelatedAppServCreator的子类，代替RelatedAppServCreator处理整个审批单复制过程 **/
    private static final String EXTEND_CLS = "EXTEND_CLS";

    /** 字段映射 **/
    private static final String ITEM_MAP = "ITEM_MAP";

    /** 是否只复制文件链接，不复制实体文件 **/
    private static final String COPY_FILE_LINK = "COPY_FILE_LINK";

    /** 文件类型映射，例如：A审批单的ZHENGWEN转换成B服务的FUJIAN **/
    private static final String FILE_TYPE_MAP = "FILE_TYPE_MAP";

    /** 指定需要复制的文件类型，例如，只把老服务的盖章正文和附件复制过来 **/
    private static final String REQUIRED_FILE_TYPE = "REQUIRED_FILE_TYPE";

    /** 不允许复制的文件类型 **/
    private static final String EXCLUDE = "EXCLUDE";

    /** 允许复制的文件类型 **/
    private static final String INCLUDE = "INCLUDE";

    /**
     * @param bean 参数Bean
     */
    public RelatedServCreatorSetting(Bean bean) {
        if (bean != null) {
            this.copyFrom(bean);
        }
    }

    /**
     * 
     * @param fileCat 文件大类型
     * @param itemCode 文件小类型
     * @return 是否需要复制此类型文件
     */
    public boolean requiredFileType(String fileCat, String itemCode) {
        Bean bean = this.getBean(REQUIRED_FILE_TYPE);

        // 优先判断不允许的数据，ALLOW和DENY不能同时存在
        if (bean.isNotEmpty(EXCLUDE)) {
            boolean isMatch = matchFileType(fileCat, itemCode, bean.getStr(EXCLUDE));
            if (isMatch) { // 除了拒绝的其它都允许
                return false;
            }

            return true;
        } else if (bean.isNotEmpty(INCLUDE)) {
            // 如果设置了允许，则允许之外的都拒绝
            boolean isMatch = matchFileType(fileCat, itemCode, bean.getStr(INCLUDE));
            if (isMatch) { // 除了允许的，其它都不允许
                return true;
            }

            return false;
        } else {
            // 如果没有设置，则全部允许
            return true;
        }
    }

    /**
     * 
     * @param fileCat 文件大类型
     * @param itemCode 文件小类型
     * @param strFileType 配置类型
     * @return 是否匹配指定文件类型
     */
    private boolean matchFileType(String fileCat, String itemCode, String strFileType) {
        String[] fileTypes = strFileType.split(",");
        for (String fileType : fileTypes) {

            String[] types = fileType.split("\\.");
            if (types.length == 2) {
                if (fileType.equals(fileCat + "." + itemCode)) {
                    return true;
                }
            } else {
                if (fileType.equals(fileCat)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 
     * @param fileCat 文件类型
     * @param itemCode 文件类型：小类
     * @return 目标文件大小类型，数组的第一个字符为大类FILE_CAT，第二个字符串为小类ITEM_CODE
     */
    public String[] getTargetFileType(String fileCat, String itemCode) {
        Bean fileTypeMap = this.getBean(FILE_TYPE_MAP);

        if (fileTypeMap.isNotEmpty(fileCat)) {
            String value = fileTypeMap.getStr(fileCat);
            String[] values = value.split("\\.");
            if (values.length == 2) {
                return values;
            }

            String[] rtn = new String[2];
            rtn[0] = values[0];
            rtn[1] = "";
            return rtn;
        } else if (fileTypeMap.isNotEmpty(fileCat + "." + itemCode)) {
            String value = fileTypeMap.getStr(fileCat + "." + itemCode);

            String[] values = value.split("\\.");
            if (values.length == 2) {
                return values;
            }

            String[] rtn = new String[2];
            rtn[0] = values[0];
            rtn[1] = "";
            return rtn;
        } else {
            String[] rtn = new String[2];
            rtn[0] = fileCat;
            rtn[1] = itemCode;
            return rtn;
        }
    }

    /**
     * 
     * @return 是否复制文件链接
     */
    public boolean isCopyFileLink() {
        return this.getBoolean(COPY_FILE_LINK);
    }

    /**
     * 
     * @return 取得新审批单与老审批单字段映射关系。
     */
    public Bean getItemMap() {
        return this.getBean(ITEM_MAP);
    }

    /**
     * 
     * @return 取得审批单起草类的子类
     */
    public String getExtendCls() {
        return this.getStr(EXTEND_CLS);
    }

    /** 配置文件默认路径 **/
    private static final String DEFAULT_PATH = "conf/relatedServ/";

    /**
     * 
     * @param oldServId 老服务ID
     * @param newServId 新服务ID
     * @return 关联服务创建设置对象
     */
    public static RelatedServCreatorSetting getSetting(String oldServId, String newServId) {
        File settingFile = getSettingFile(oldServId);
        if (settingFile != null) {
            try {
                Bean bean = XmlUtils.toBean(FileUtils.readFileToString(settingFile, "UTF-8"));
                if (bean.isNotEmpty(newServId)) {
                    RelatedServCreatorSetting setting = new RelatedServCreatorSetting(bean.getBean(newServId));
                    return setting;
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }

        }
        return null;
    }

    /**
     * 
     * @param oldServId 老服务ID
     * @return 老服务的关联服务创建配置文档
     */
    private static File getSettingFile(String oldServId) {
        String fileStr = Context.app(Context.APP.WEBINF) + DEFAULT_PATH;
        File file = new File(fileStr);

        Collection<File> list = FileUtils.listFiles(file,
                FileFilterUtils.nameFileFilter(oldServId + ".xml"), TrueFileFilter.INSTANCE);

        if (list != null) {
            @SuppressWarnings("rawtypes")
            Iterator it = list.iterator();

            if (it.hasNext()) {
                File xmlFile = (File) it.next();
                return xmlFile;
            }
        }

        return null;
    }
}
