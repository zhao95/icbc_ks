package com.rh.core.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.rh.core.base.BaseContext.APP;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.logs.RHLog;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.JsonUtils;
import com.rh.core.wfe.db.WfProcDefDao;
import com.rh.core.wfe.def.WfProcDefManager;
import com.rh.core.wfe.def.WfServCorrespond;
import com.rh.core.wfe.serv.ProcDefServ;

/**
 * 支持自动化部署时由程序自动导入最新版本的流程包， 流程包自动导入类，约定流程包放在 WEB-INF/doc/SY_WF_DEF_PKG
 * 目录下，不支持递归查找流程包
 * 
 * @author yjzhou modify time 2016.12.15
 */
public class WfZipServ extends CommonServ {

	private static Logger logger = Logger.getLogger(WfZipServ.class);

	// 流程包相对目录
	public static String WF_DEF_PATH = "doc" + File.separatorChar + "SY_WF_DEF_PKG" + File.separatorChar;

	// 备份目录
	public static String WF_DEF_PATH_HIS = "doc" + File.separatorChar + "SY_WF_DEF_PKG_HIS" + File.separatorChar;

	// zip包后缀
	public static String ZIP_SUFFIX = ".zip";

	public static String WEB_INF = "WEB-INF";

	// 流程包存储路径
	public static String WF_DIR_FULL_PATH = null;

	// 流程包备份存储路径
	public static String WF_DIR_HIS_FULL_PATH = null;

	// 流程服务对象
	private static final ProcDefServ procDefServ = new ProcDefServ();

	// 流程导入记录日志的名称前缀
	public static final String LOG_PRE = "IMP_WF_DEF_";

	/**
	 * 获取WEB-INF/doc/SY_WF_DEF_PKG全路径
	 * 
	 * @return
	 */
	private static String getWebInfPath(String relativePath) {
		try {
			String sysPath = Context.appStr(APP.SYSPATH);
			logger.info("[syspath] " + sysPath);
			return sysPath + relativePath;
		} catch (Exception e) {
			logger.error("------------[getWebInfPath failed!]----------");
			logger.error(ExceptionUtils.getFullStackTrace(e));
			return "";
		}

	}
	
	/**
	 * 初始化zip目录
	 * @return
	 */
	public static boolean initWfPath (){
		WF_DIR_FULL_PATH = getWebInfPath(
				File.separatorChar + WEB_INF + File.separatorChar + WF_DEF_PATH + File.separatorChar);
		WF_DIR_HIS_FULL_PATH = getWebInfPath(
				File.separatorChar + WEB_INF + File.separatorChar + WF_DEF_PATH_HIS + File.separatorChar);
		// 若不是正确的流程包路径，则退出
		if (StringUtils.isEmpty(WF_DIR_FULL_PATH)
				|| !WF_DIR_FULL_PATH.contains(WEB_INF + File.separatorChar + WF_DEF_PATH)) {
			logger.info(" init wf path fail ! [work flow pkg root path is null!] or [path is not correct][path :] " + WF_DIR_FULL_PATH);
			return false;
		}
		
		return true;
	}

	/**
	 * 读取流程定义目录下的zip文件
	 * @param isOnlyClearCache 是否仅清除缓存
	 * @throws IOException
	 */
	public void readWfDefDir(boolean isOnlyClearCache) throws IOException {
        if (!initWfPath()) {
			return;
		}
        
		File file = new File(WF_DIR_FULL_PATH);
		if (null != file && file.exists() && file.isDirectory()) {
			// 文件夹存在
			String[] fileList = file.list();

			if (null == fileList || 0 == fileList.length) {
				// 根目录为空
				log.info("[work flow pkg root path is empty dir!][path :]" + WF_DIR_FULL_PATH);
				return;
			}

			List<String> zipNameList = getZipFileList(fileList);

			Map<String, Bean> procVersionMap = new HashMap<String, Bean>();

			// 逐个读取文件
			for (String zipName : zipNameList) {
				readZipFile(zipName, procVersionMap, isOnlyClearCache);
			}

			procVersionMap.clear();

			// 拷贝目录文件备份
			copyFileToStore(WF_DIR_FULL_PATH, WF_DIR_HIS_FULL_PATH);

			// 删除目录
			deleteDir(WF_DIR_FULL_PATH);
		} else if (null != file && !file.exists()) {
			// 创建目录
			file.mkdirs();
		}
	}

	/**
	 * 拷贝文件备份
	 * 
	 * @param srcDir
	 * @param destDir
	 */
	private void copyFileToStore(String srcDir, String destDir) {
		try {
			//按时间建立目录
			File destFile = new File(destDir + File.separatorChar + DateUtils.getDatetime("yyyyMMddHHmmss"));
			if (destFile == null || !destFile.exists()) {
				destFile.mkdirs();
			}
			FileUtils.copyDirectory(new File(srcDir), destFile);
		} catch (Exception e) {
			log.error("copy file to store failed !", e);
		}
	}

	/**
	 * 获取zip文件列表
	 * 
	 * @param sourceNames
	 * @return
	 */
	private List<String> getZipFileList(String[] sourceNames) {
		List<String> zipNameList = new ArrayList<String>();
		if (null == sourceNames || 0 == sourceNames.length) {
			return zipNameList;
		}

		for (String name : sourceNames) {
			if (name.endsWith(ZIP_SUFFIX)) {
				zipNameList.add(WF_DIR_FULL_PATH + name);
			}
		}

		return zipNameList;
	}

	/**
	 * 读取zip文件并导入
	 * 
	 * @param relativeFilePath
	 * @throws IOException
	 */
	public void readZipFile(String filePath, Map<String, Bean> procVersionMap,boolean isOnlyClearChache) throws IOException {
		// 设置线程用户
		String onlineUserCode = Context.getSyConf("CC_IMPDATE_ONLINE_USER", "0000803837");
		try {
			Context.setThreadUser(UserMgr.getUserState(onlineUserCode));
		} catch (Exception e1) {
			log.error("------------------ imp wf zip error! " + e1.getMessage());
			RHLog.error(WfZipServ.class, e1);
		}
		InputStream inputStream = new FileInputStream(new File(filePath));
		ZipInputStream zipIn = new ZipInputStream(inputStream);
		String procCode = null;
		while (zipIn.getNextEntry() != null) {
			try {
				Transaction.begin();

				BufferedInputStream in = new BufferedInputStream(zipIn);
				Bean dataBean = JsonUtils.toBean(IOUtils.toString(in, Constant.ENCODING));
				// 导入之前先删除流程历史数据
				procCode = dataBean.getStr("PROC_CODE");

				// 多个zip包中若有相同的流程编码，则判断是否最新的，是则执行导入
				if (isHigherVersion(dataBean, procVersionMap)) {
					if (isOnlyClearChache) {
						//清除缓存
						clearProcCache(dataBean.getStr("SERV_ID"), procCode);
					} else {
						// 执行导入
						impJsonDef(dataBean);
					}
				}

				zipIn.closeEntry();

				Transaction.commit();
			} catch (Exception e) {
				// 回滚导入失败的流程
				Transaction.rollback();
				log.error(ExceptionUtils.getFullStackTrace(e));
				// 记录到数据库
				RHLog.error(LOG_PRE + procCode, e);
			} finally {
				Transaction.end();
			}
		}
		// 关闭文件流，否则无法清除文件
		zipIn.close();
		inputStream.close();
	}

	/**
	 * 比较多个zip导入时，当前流程是否为最新的版本
	 * 
	 * @param dataBean
	 * @return
	 */
	private boolean isHigherVersion(Bean dataBean, Map<String, Bean> procVersionMap) {
		try {
			String procCode = dataBean.getStr("PROC_CODE");
			String sMtime = dataBean.getStr("S_MTIME");
			if (StringUtils.isEmpty(procCode)) {
				return false;
			}

			String[] arr = procCode.split("@");
			Date date = DateUtils.getDateFromString(sMtime, DateUtils.FORMAT_TIMESTAMP);

			// 若缓存不包含此流程编码，则直接返回true
			if (!procVersionMap.containsKey(arr[0])) {
				Bean bean = new Bean().set("VERSION", arr[arr.length - 1]);
				if (date == null) {
					bean.set("S_MTIME", "");
				} else {
					bean.set("S_MTIME", date.getTime());
				}
				procVersionMap.put(arr[0], bean);
				return true;
			}

			if (date != null && StringUtils.isNotEmpty(procVersionMap.get(arr[0]).getStr("S_MTIME"))) {
				// 优先使用S_MTIME作版本比较
				if (date.getTime() <= procVersionMap.get(arr[0]).getLong("S_MTIME")) {
					// map中缓存的版本更高
					return false;
				} else {
					// 若是缓存未包含流程编码，则加入缓存
					procVersionMap.put(arr[0],
							new Bean().set("S_MTIME", date.getTime()).set("VERSION", arr[arr.length - 1]));
					return true;
				}
			} else {
				// 用版本号比较
				if (procVersionMap.containsKey(arr[0])
						&& procVersionMap.get(arr[0]).getInt("VERSION") >= Integer.parseInt(arr[arr.length - 1])) {
					// map中缓存的版本更高
					return false;
				} else {
					// 若是缓存未包含流程编码，或者版本号更高，则加入缓存
					procVersionMap.put(arr[0], new Bean().set("VERSION", arr[arr.length - 1]).set("S_MTIME", ""));
					return true;
				}
			}

		} catch (Exception e) {
			log.error("procCode is not format :" + dataBean.getStr("PROC_CODE"));
			return false;
		}
	}

	/**
	 * 导入zip包
	 * 
	 * @param procDefBean
	 */
	public void impJsonDef(Bean procDefBean) {
		try {
			ParamBean param = new ParamBean(procDefBean);
			param.setServId(ServMgr.SY_WFE_PROC_DEF).set("xmlStr", procDefBean.getStr("PROC_XML"));

			// 修改服务ID为流程服务ID，否则无法导入
			Object oldServId = Context.getThread(Context.THREAD.SERVID);
			Context.setThread(Context.THREAD.SERVID, WfProcDefDao.SY_WFE_PROC_DEF_SERV);

			procDefServ.saveWfAsNewVersion(param);

			// 保存之后，尝试查询一次，防止保存有错误
			ParamBean searchBean = new ParamBean(param).setServId(WfProcDefDao.SY_WFE_PROC_DEF_SERV)
					.setId(param.getStr("PROC_CODE"));
			procDefServ.byid(searchBean);

			// 重置线程服务ID为原服务ID
			Context.setThread(Context.THREAD.SERVID, oldServId);

			log.info("[auto import work flow zip success, proc_code :] " + param.getStr("PROC_CODE"));
			RHLog.info(LOG_PRE + param.getStr("PROC_CODE"), "[auto import work flow zip success]");
		} catch (Exception e) {
			log.error("[auto import work flow zip failed, proc_code : ]" + procDefBean.getStr("PROC_CODE"));
			throw new RuntimeException(e);
		}
	}

	/**
	 * 清除文件目录，若文件目录本身为空，则删除目录
	 */
	public void deleteDir(String rootPath) {
		File file = new File(rootPath);
		if (null != file && file.exists() && file.isDirectory()) {
			String[] fileList = file.list();
			if (null != fileList) {
				if (0 == fileList.length && !StringUtils.equals(rootPath, WF_DIR_FULL_PATH)) {
					file.delete();
				} else {
					for (String fileName : fileList) {
						String fullPath = rootPath + fileName;
						File tempFile = new File(fullPath);
						if (null != tempFile && file.exists()) {
							if (tempFile.isFile()) {
								tempFile.delete();
							} else if (tempFile.isDirectory()) {
								// 递归删除
								deleteDir(tempFile.getAbsolutePath() + File.separatorChar);
								tempFile.delete();
							} else {

							}
						}
					}
				}
			}
		}
		log.info("[clear work flow pkg root dir!][path :]" + WF_DIR_FULL_PATH);
	}

	// 删除单个的流程
	public void delWfDefByProcCode(String procCode) {
		try {
			if (StringUtils.isEmpty(procCode)) {
				log.info("[delete old work flow complete, proc_code is null]");
				return;
			}
			ProcDefServ procDefServ = new ProcDefServ();
			procDefServ.deleteProcDef(new ParamBean().set("procIds", procCode));
			log.info("[delete old work flow success, proc_code :] " + procCode);
			RHLog.info(LOG_PRE + procCode, "[delete old work flow success] ");
		} catch (Exception e) {
			log.error("[delete old work flow failed when auto importing work flow zip!]");
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 清除服务对应的流程缓存
	 * @param servId
	 * @param procCode
	 */
	public static void clearProcCache (String servId, String procCode){
	        String procKey = "_WF_MAP";
	        ServDefBean servDef = ServUtils.getServDef(servId);
	        logger.info(servDef.getStr(procKey));
	        servDef.remove(procKey);
	        
	        // 清除流程的业务服务的缓存
	        ParamBean param = new ParamBean(ServMgr.SY_SERV, "clearCache");
	        param.setId(servId);
	        ServMgr.act(param);
	        
	        Bean procBean = ServDao.find(ServMgr.SY_WFE_PROC_DEF, new SqlBean().and("SERV_ID", servId).and("PROC_IS_LATEST", 1));
	        if (null != procBean) {
				//将之前缓存中的流程进行删除
				WfProcDefManager.removeFromCache(procBean.getStr("PROC_CODE"));
				logger.info("[clear cache servId = "+servId+", proc_code = "+procBean.getStr("PROC_CODE")+"]");
			}
	        
			WfServCorrespond.removeFromCache(servId);
	}
}
