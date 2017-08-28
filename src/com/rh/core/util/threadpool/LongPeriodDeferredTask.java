package com.rh.core.util.threadpool;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.comm.CacheMgr;
import com.rh.core.comm.FileMgr;
import com.rh.core.comm.FileStorage;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.DateUtils;

/**
 * 长期延时任务重新进入任务池的调度任务
 * 
 * @author yangjy
 */
public class LongPeriodDeferredTask extends TimerTask {
    
    private static final String CACHE_TYPE = "LongPeriodDeferredCache";
    
    private static Log log = LogFactory.getLog(LongPeriodDeferredTask.class);
    
    @Override
    public void run() {
        String taskId = null;
        try {
            List<Bean> list = findTaskList();
            for (Bean bean : list) {
                taskId = bean.getId();
                if (!isRunning(bean.getId())) {
                    executeSingle(bean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            removeRunningFlag(taskId);
        }
    }
    
    /**
     * 执行单个任务
     * 
     * @param bean 任务Bean
     */
    private void executeSingle(Bean bean) {
        String filePath = bean.getStr("FT_FILE");
        RhThreadTask task = loadObject(filePath);
        task.setExecCount(bean.getInt("FT_COUNT"));
        RhThreadPool.getDefaultPool().execute(task);
    }
    
    /**
     * 装载任务
     * @param filePath 文件路径
     * @return 从文件中装载的RhThreadTask对象
     */
    protected RhThreadTask loadObject(String filePath) {
        RhThreadTask task = null;
        InputStream is = null;
        ObjectInputStream ois = null;
        try {
            is = FileStorage.getInputStream(FileMgr.getAbsolutePath(filePath));
            ois = new ObjectInputStream(is);
            task = (RhThreadTask) ois.readObject();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(is);
        }
        return task;
    }
    
    /**
     * @return 需要执行的任务列表
     */
    private List<Bean> findTaskList() {
        Date now = Calendar.getInstance().getTime();
        String nowTime = DateUtils.formatDatetime(now);
        // 一天之前还为完成的任务不处理了
        Date begin = DateUtils.addDays(now, -1);
        String beginTime = DateUtils.formatDatetime(begin);
        
        SqlBean sql = new SqlBean();
        sql.andGT("NEXT_TIME", beginTime);
        sql.andLTE("NEXT_TIME", nowTime);
        
        return ServDao.finds(LongPeriodDeferredQueue.CC_OPEN_APP_FAILED_TASK,
                sql);
    }
    
    /**
     * @param taskId 任务ID
     * @return 是否指定任务正在执行
     */
    private boolean isRunning(String taskId) {
        Object obj = CacheMgr.getInstance().get(taskId, CACHE_TYPE);
        if (obj != null) {
            return true;
        }
        
        CacheMgr.getInstance().set(taskId, 1, CACHE_TYPE);
        
        return false;
    }
    
    /**
     * @param taskId 任务ID
     */
    public static void removeRunningFlag(String taskId) {
        if (taskId != null) {
            CacheMgr.getInstance().remove(taskId, CACHE_TYPE);
        }
    }
}
