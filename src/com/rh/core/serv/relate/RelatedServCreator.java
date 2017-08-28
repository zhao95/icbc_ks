package com.rh.core.serv.relate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.BeanUtils;
import com.rh.core.base.Context;
import com.rh.core.comm.FileMgr;
import com.rh.core.org.UserBean;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.var.VarMgr;

/**
 * 根据A审批单起草另外一种类型的B审批单。
 * @author yangjy
 * 
 */
public class RelatedServCreator {

    /** log */
    // private static final Log log = LogFactory.getLog(RelatedAppServCreator.class);

    /** 转换设置 */
    private RelatedServCreatorSetting setting = null;

    /** 参数Bean **/
    private ParamBean paramBean = null;

    private String newServId = null;
    
    private HashSet<String> fileIdSet = null;

    /**
     * @param paramBean 参数对象
     * @param oldBean 旧审批单
     * @param newServId 新起草审批单服务ID
     * @return 新审批单数据
     */
    public OutBean create(ParamBean paramBean, Bean oldBean, String newServId) {
        this.paramBean = paramBean;
        this.newServId = newServId;

        ParamBean newServBean = cloneDataBean(oldBean, newServId);

        beforeCreate(oldBean, newServBean);

        OutBean outBean = ServMgr.act(newServId, "save", newServBean);
        copyFile(oldBean, outBean);
        addRelation(oldBean, outBean);

        afterCreate(oldBean, outBean);
        return outBean;
    }

    /**
     * 
     * @param oldBean 老审批单
     * @param newServId 新审批单服务ID
     * @return 从老审批单复制数据至新审批单，并返回复制的数据对象。
     */
    private ParamBean cloneDataBean(Bean oldBean, String newServId) {
        ParamBean bean = new ParamBean();

        Bean itemMap = setting.getItemMap();

        @SuppressWarnings("rawtypes")
        Iterator it = itemMap.keySet().iterator();

        while (it.hasNext()) {
            final String key = (String) it.next();
            final String value = itemMap.getStr(key);

            bean.put(key, BeanUtils.replaceValues(VarMgr.replaceSysVar(value), oldBean));
        }

        return bean;
    }

    /**
     * @param fileId 文件ID
     * @return 是否是必须复制的文件
     */
    private boolean requiredFile(String fileId) {
        if (this.paramBean.isNotEmpty("_fileIds")) { // 如果指定只复制哪些文件
            if (this.fileIdSet == null) {
                this.fileIdSet = new HashSet<String>();
                String[] fileIds = paramBean.getStr("_fileIds").split(",");
                for (String temp : fileIds) {
                    fileIdSet.add(temp);
                }
            }

            if (fileIdSet.contains(fileId)) { // 是否包含指定ID
                return true;
            }

            // 不匹配则返回null
            return false;
        }
        return true;
    }

    /**
     * 
     * @param oldData 老审批单
     * @param newData 新审批单服务ID
     */
    private void copyFile(Bean oldData, Bean newData) {
        List<Bean> fileList = FileMgr.getFileListBean("", oldData.getId());

        if (fileList == null) {
            return;
        }

        UserBean user = Context.getUserBean();

        for (Bean fileBean : fileList) {
            // 是否是需要复制的文件类型
            if (!setting.requiredFileType(fileBean.getStr("FILE_CAT"), fileBean.getStr("ITEM_CODE"))) {
                continue;
            }

            if (!requiredFile(fileBean.getId())) {
                continue;
            }

            String[] targetType = setting.getTargetFileType(fileBean.getStr("FILE_CAT")
                    , fileBean.getStr("ITEM_CODE"));

            ServDefBean newServDefBean = ServUtils.getServDef(newServId);

            Bean fileParamBean = new Bean().set("SERV_ID", newServDefBean.getSrcId())
                    .set("DATA_ID", newData.getId()).set("FILE_HIST_COUNT", 0);

            fileParamBean.set("S_USER", user.getCode())
                    .set("S_UNAME", user.getName()).set("S_DEPT", user.getDeptCode())
                    .set("S_DNAME", user.getDeptName()).set("S_CMPY", user.getCmpyCode());

            // 设置目标文件的类型
            fileParamBean.set("FILE_CAT", targetType[0]);
            if (StringUtils.isNotEmpty(targetType[1])) {
                fileParamBean.set("ITEM_CODE", targetType[1]);
            }

            if (setting.isCopyFileLink()) {
                FileMgr.createLinkFile(fileBean, fileParamBean);
            } else {
                FileMgr.copyFile(fileBean, fileParamBean);
            }
        }
    }

    /**
     * 
     * @param oldBean 老审批单
     * @param newBean 新审批单
     */
    private void addRelation(Bean oldBean, Bean newBean) {
        // 互为关联文件
        addRelationRecord(oldBean.getId(), paramBean.getStr("oldServId"), newBean.getId(), this.newServId);
        addRelationRecord(newBean.getId(), this.newServId, oldBean.getId(), paramBean.getStr("oldServId"));
    }

    /**
     * @param oldDataId 老审批单ID
     * @param oldServId 老审批单服务ID
     * @param newDataId 新审批单ID
     * @param newServId 新审批单服务ID
     */
    private void addRelationRecord(String oldDataId, String oldServId, String newDataId, String newServId) {
        ServDefBean serv = ServUtils.getServDef(newServId);
        String srcNewServId = serv.getSrcId();
        Bean param = new Bean().set("SERV_ID", srcNewServId)
                .set("DATA_ID", newDataId)
                .set("RELATE_SERV_ID", oldServId)
                .set("RELATE_DATA_ID", oldDataId);
        ServDao.create(ServMgr.SY_SERV_RELATE, param);
    }

    /**
     * 
     * @return 转换设置类
     */
    public RelatedServCreatorSetting getSetting() {
        return setting;
    }

    /**
     * 
     * @param setting 设置转换设置类
     */
    public void setSetting(RelatedServCreatorSetting setting) {
        this.setting = setting;
    }

    /**
     * 
     * @return 取得参数Bean
     */
    public ParamBean getParamBean() {
        return paramBean;
    }

    /**
     * 
     * @return 取得新服务ID
     */
    public String getNewServId() {
        return newServId;
    }

    /**
     * 空方法，在创建新审批单之前使用，便于子类扩展逻辑处理。
     * @param oldBean 老审批单
     * @param newServBean 新审批单数据，从老审批单复制过来的。
     */
    protected void beforeCreate(Bean oldBean, ParamBean newServBean) {

    }

    /**
     * 空方法。在创建审批单之后使用，便于子类扩展逻辑处理
     * @param oldBean 老审批单
     * @param outBean 新审批单保存之后，输出的数据
     */
    protected void afterCreate(Bean oldBean, OutBean outBean) {

    }

}
