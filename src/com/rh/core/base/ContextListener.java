package com.rh.core.base;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.rh.core.base.BaseContext.APP;
import com.rh.core.util.Constant;

/**
 * 启动listener类，用于系统环境总体初始化
 * @author Jerry Li
 * @version $id$
 */
public class ContextListener implements ServletContextListener {
	
	private static final Logger logger = Logger.getLogger(ContextListener.class);

    /**
     * 初始化系统
     * @param sce 存放于WEB.XML中的配置信息
     */
    @SuppressWarnings("unchecked")
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 加载配置参数
        System.out.println(".........................................................");
        System.out.println("正在启动系统 ... ...");
        ServletContext sc = sce.getServletContext();
        // 获取系统真实路径
        String systemPath = sc.getRealPath("/");
        
        if (!systemPath.endsWith(File.separator)) {
            systemPath += Constant.PATH_SEPARATOR;
        }
        
        String contextPath = sc.getContextPath();
        if (contextPath.equals("/")) {
            contextPath = "";
        } else if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }
        
        System.out.println("系统工作目录: " + systemPath);
        System.out.println("系统服务路径: " + contextPath);
        Bean paramBean = new Bean();
        String name;
        // 获取StartupServlet的配置信息
        Enumeration<String> names = sc.getInitParameterNames();
        while (names.hasMoreElements()) {
            name = names.nextElement();
            paramBean.set(name, sc.getInitParameter(name));
        }
        paramBean.set(APP.SYSPATH, systemPath).set(APP.CONTEXTPATH, contextPath).set(APP.WEBINF, systemPath  + "WEB-INF" 
                + Constant.PATH_SEPARATOR);
        BaseContext.start(paramBean);
        System.out.println("系统初始化完毕，开始接收请求！");
        System.out.println(".........................................................");
        //系统启动完成，自动读取zip
        impWfZip();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        BaseContext.stop();
    }
    
    /**
     * 导入流程包，捕获异常，保证系统可以启动
     */
    private void impWfZip(){				
    	try {
			String isOPenImp = Context.getSyConf("IS_OPEN_IMP", "2");
			if (StringUtils.equals(isOPenImp, Constant.YES)) {
				Connection conn = BaseContext.getConn();
				if (null == conn) {
					return;
				}
				String sql = "select IMP_KEY,IMP_VAL from SY_WFE_IMP_STATE where IMP_KEY = 'IMP_STATE' for update skip locked";
				String sqlUpdate = "update SY_WFE_IMP_STATE set IMP_VAL='2' where IMP_KEY = 'IMP_STATE'";
				PreparedStatement prepstmtQuery = 
						conn.prepareStatement(sql);
				PreparedStatement prepstmtUpdate = 
						conn.prepareStatement(sqlUpdate);
				//查询并锁住行
				prepstmtQuery.executeQuery();
				ResultSet resultSet = prepstmtQuery.getResultSet();
				WfZipServ wfZipServ = new WfZipServ();
				if (resultSet.next() 
						&& StringUtils.equals(resultSet.getString("IMP_KEY"), "IMP_STATE") 
						&& StringUtils.equals(Constant.YES, resultSet.getString("IMP_VAL"))) {
					//更新表数据
					prepstmtUpdate.executeUpdate();
					conn.commit();
					wfZipServ.readWfDefDir(false);
					logger.info("[current host application imp wfe zip]");
				}else {
					//释放锁，并删除zip包
					conn.commit();
					//仅清除缓存，不做导入
					clearWfCache();
					logger.info("[current host application does not imp wfe zip]");
				}
				
				//关闭本次连接
				prepstmtQuery.close();
				prepstmtUpdate.close();
				conn.close();
			}
		} catch (Exception e) {
			logger.error("[imp work flow pkg faild!]");
            logger.error(ExceptionUtils.getFullStackTrace(e));
		} 

    }
    
    /**
     * 若存在流程包，则启动6分钟后清理流程缓存，加载最新的流程
     */
    private static void clearWfCache (){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(6*60*1000);
					WfZipServ wfZipServ = new WfZipServ();
					wfZipServ.readWfDefDir(true);
				} catch (Exception e) {
                    logger.error(e.getMessage(), e);
				} 
				
			}
		}).start();
    }
}
