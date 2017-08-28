package com.rh.core.util.threadpool;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.comm.FileMgr;
import com.rh.core.comm.FileStorage;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.Lang;

/**
 * 将指定任务加入到长期延迟任务队列
 * 
 * @author yangjy
 */
public class LongPeriodDeferredQueue {
    /** SERV_ID **/
    public static final String CC_OPEN_APP_FAILED_TASK = "CC_OPEN_APP_FAILED_TASK";
    private static Log log = LogFactory.getLog(LongPeriodDeferredQueue.class);

    
    /**
     * 将指定任务加入到长期延迟任务队列
     * 
     * @param task 指定任务
     * @param nextTime 下次执行时间
     */
    public void add(RhThreadTask task , int nextTime) {
        final Bean data = saveData(task, nextTime);
        if (data != null) { // 新增数据需要保持任务
            saveObject(task, data.getStr("FT_FILE"));
        }
    }
    
    /**
     * 
     * @param task 任务
     * @param filepath 文件存储路径
     */
    protected void saveObject(RhThreadTask task , String filepath) {
        OutputStream os = null;
        ObjectOutputStream oos = null;
        try {
            filepath = FileMgr.getAbsolutePath(filepath);
            os = FileStorage.getOutputStream(filepath);
            oos = new ObjectOutputStream(os);
            oos.writeObject(task);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(os);
        }
    }
    
    /**
     * 保存任务数据
     * 
     * @param task 任务
     * @param nextTime 下次执行时间
     * @return 保存后的数据Bean。如果是增加则返回数据Bean，如果是修改则返回null。
     */
    private Bean saveData(RhThreadTask task , int nextTime) {
        String strNextTime;
        Date date = Calendar.getInstance().getTime();
        Date nextDate = null;
        if (nextTime > 0) {
            nextDate = DateUtils.addSeconds(date, nextTime);
        } else {
            nextDate = DateUtils.addSeconds(date, LongValidity.TS);
        }
        strNextTime = com.rh.core.util.DateUtils.formatDatetime(nextDate);
        Bean bean = null;
        final String serialid = task.getSerialId();
        if (serialid != null && serialid.length() > 0) {
            bean = updateData(task, strNextTime);
            return null;
        }
        
        bean = addData(task, strNextTime);
        return bean;
    }
    
    /**
     * 
     * @param task 任务
     * @param strNextTime 下次执行时间
     * @return 存储结果
     */
    private Bean addData(RhThreadTask task , String strNextTime) {
        final String uuid = Lang.getUUID();
        final String file = getFilePath(uuid);
        
        task.setSerialId(uuid);
        
        ParamBean saveBean = new ParamBean();
        saveBean.setId(uuid);
        saveBean.set("FT_ID", uuid);
        saveBean.set("FT_FILE", file);
        saveBean.set("FT_COUNT", task.getExecCount());
        saveBean.set("FT_STRATEGY", task.getStrategy());
        saveBean.set("NEXT_TIME", strNextTime);
        
        saveBean.setAddFlag(true);
        saveBean.setServId(CC_OPEN_APP_FAILED_TASK);
        saveBean.setAct(ServMgr.ACT_SAVE);
        return ServMgr.act(saveBean);
    }
    
    /**
     * 更新数据库数据。
     * 
     * @param task 任务
     * @param strNextTime 下次执行时间
     * @return 修改之后的结果
     */
    private Bean updateData(RhThreadTask task , String strNextTime) {
        final String serialid = task.getSerialId();
        ParamBean modifyBean = new ParamBean();
        modifyBean.setId(serialid);
        modifyBean.set("NEXT_TIME", strNextTime);
        modifyBean.set("FT_COUNT", task.getExecCount());
        modifyBean.setServId(CC_OPEN_APP_FAILED_TASK);
        modifyBean.setAct(ServMgr.ACT_SAVE);
        return ServMgr.act(modifyBean);
    }
    
    /**
     * @param uuid 唯一编码
     * @return 指定任务的存储路径
     */
    private static String getFilePath(String uuid) {
        Date now = Calendar.getInstance().getTime();
        
        StringBuilder path = new StringBuilder();
        path.append("@SYS_FILE_PATH@");
        path.append("/").append("RhThreadTask");
        path.append("/").append(DateFormatUtils.format(now, "yyyy"));
        path.append("/").append(DateFormatUtils.format(now, "MMdd"));
        path.append("/").append(uuid);
        
        return path.toString();
    }
}
