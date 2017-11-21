package com.rh.core.icbc.monitor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import com.rh.core.base.BaseContext;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.BaseContext.DS;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.FileMgr;
import com.rh.core.comm.FileStorage;
import com.rh.core.util.scheduler.RhJobContext;
import com.rh.core.util.scheduler.RhLocalJob;

public class SystemMonitor extends RhLocalJob {
	private static Logger log = Logger.getLogger(SystemMonitor.class);
	private List<Bean> lbeans = new ArrayList<Bean>();
	/**
	 * Quartz requires a public empty constructor so that the scheduler can
	 * instantiate the class whenever it needs.
	 */
	public SystemMonitor() {
	}

	// /*
	@Override
	public void executeJob(RhJobContext context) {

		int flag = Context.getSyConf("CC_MONITOR_FLAG", 2);//1:监控上送 2：监控不上送
		String SAPPSNAME=Context.getSyConf("CC_MONITOR_APPNAME", "SYSTEMPANTMONITOR");//应用名称
		String fromapp=Context.getSyConf("CC_MONITOR_FROMAPP", "F-MCCP");
		try {
			//正则过滤ip地址:数据库服务器
			//获取不到Ip地址的用当前应用服务器ip代替:磁盘读写地址
			StringBuilder strinfo = new StringBuilder();
			String info = "";
			String servip = InetAddress.getLocalHost().getHostAddress();
			strinfo.append("<?xml version=\"1.0\" encoding=\"GBK\" ?>");
			strinfo.append("<APPROOT type=\"BAMC_USABILITY_REPORT\" ver=\"2.0\" from =\""+fromapp+"\" to=\"F-AMC\" mode=\"asy\">");
			strinfo.append("<PUBLIC>");
			strinfo.append("<APPSNAME>"+fromapp+"</APPSNAME>");
			strinfo.append("<TYPE>02</TYPE>");
			strinfo.append("<IP>" + servip + "</IP>");
			strinfo.append("</PUBLIC>");
			strinfo.append("<PRIVATE><ROWSET>");
			
			strinfo.append(getDataBase(SAPPSNAME));
			log.info("--可用性上报_数据库连接状态获取完毕");

			strinfo.append(getDiskRW(servip,SAPPSNAME));			
			log.info("--可用性上报_磁盘读写状态获取完毕");
			
			strinfo.append(" </ROWSET></PRIVATE></APPROOT>");
			info = strinfo.toString();
			String serverHost = Context.getSyConf("CC_MONITOR_SENDIP",
					"122.19.157.167");// "122.19.157.166";CC_Monitor_SendIp
			int serverPort = Context.getSyConf("CC_MONITOR_SENDPORT", 3001);// 3001;
			log.info("应用监控信息："+info);
			if (flag == 1) {
				sendUDP(info,serverHost,serverPort);// 提交监控				
			}
			
		} catch (Exception e) {
			log.error("心跳上报失败：" + e.getMessage(),e);
		} finally {
//			ServDao.creates(serv_Id, lbeans);//上报信息保存数据库，根据需要启用
		}

	}

	@Override
	public void interrupt() {
		// TODO Auto-generated method stub
	}

	
	/**
	 * 
	 * @ParamBean paramBean 数据库信息
	 */
	private String getDataBase(String sappname) {
		String ret="";
		String status="";
		String strurl ="";
		int istatus=0;
		try {
			
			Bean sbean = BaseContext.getDSBean();
			strurl = sbean.getStr(DS.URL);
			strurl=filterip(strurl);
			
			int ct = Transaction.getExecutor().count("select * from dual");
			if (ct > 0) {
				istatus=0;
				status="数据库连接状态正常";
			} else {
				istatus=1;
				status="数据库连接状态异常";
			}
			ret=xmlMonitor("DataBase", strurl, sappname,
					"DataBase", istatus, status);
			lbeans.add(saveMonitor("DataBase", strurl, sappname,
				"DataBase", istatus, status));
			return ret;
		} catch (Exception e) {
			// e.printStackTrace();
			log.error("数据库访问异常信息：" + e.getMessage(), e);
			istatus=1;
			status="数据库连接状态异常";
			ret=xmlMonitor("DataBase", strurl, sappname,
					"DataBase", istatus, status);
			lbeans.add(saveMonitor("DataBase", strurl, sappname,
				"DataBase", istatus, status));
			return ret;
		}
	}

	
		
	/**
	 * 
	 * @ParamBean 磁盘读写情况
	 */
	private String getDiskRW(String servip,String sappname) {
		ByteArrayInputStream stream = null;
		String ret="";
		String status="";
		int istatus=0;
		try {
			String pathString = FileMgr.getRootPath();// 取文件路径
			String path = pathString + "test_disk_wr.txt";

			// 判断文件是否存在
			if (!FileStorage.exists(path)) {
				FileStorage.createFile(path);
			}
			String strtest = "hello " + new Date();
			// 写入文件
			stream = new ByteArrayInputStream(strtest.getBytes());
			FileStorage.saveFile(stream, path);

			// 读取文件
			StringBuffer buffer = new StringBuffer();
			InputStream inputStream = FileStorage.getInputStream(path);
			List<String> list = IOUtils.readLines(inputStream);
			for (String str : list) {
				buffer.append(str);
			}
			String strequ = buffer.toString();

			// 判断读取和写入是否相等，若相等，表明读写正常
			if (strtest.equals(strequ)) {
				istatus=0;
				status="磁盘读写状态正常";
			} else {
				istatus=0;
				status="磁盘读写状态异常";
			}
			ret=xmlMonitor("DiskRWStatus",servip,
					sappname, "DiskRW", istatus, status);
			lbeans.add(saveMonitor("DiskRWStatus",servip,
					sappname, "DiskRW", istatus, status));
			return ret;
		} catch (Exception e) {
			log.error("文件可读写异常信息：" + e.getMessage(), e);
			istatus=1;
			status="磁盘读写状态异常";
			ret=xmlMonitor("DiskRWStatus",servip,
					sappname, "DiskRW", istatus, status);
			lbeans.add(saveMonitor("DiskRWStatus",servip,
					sappname, "DiskRW", istatus, status));
			return ret;
		} finally {
			if (stream != null) {
				IOUtils.closeQuietly(stream);
			}
		}
	}

	

	public static void sendUDP(String sendstr,String serverHost,int serverPort) {
		// /*发送xml心跳
		UDPSocket client = null;
		try {
			log.info("提交应用监控信息开始"+serverHost+"——"+serverPort);
			client = new UDPSocket();
			client.setSoTimeout(1000);
			client.send(serverHost, serverPort, sendstr.getBytes("GBK"));
			log.info("提交应用监控信息完成");
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally{
			if(client != null) {
				client.close();
			}
		}

	}

	public static Bean saveMonitor(String occname, String occip, String mcode,
			String smcode, int istatus, String status) {
		Bean bean = new Bean();
		status=status.replace("/", "").replace("\\", "");//应用上报对于部分特殊字符需要处理
		bean.set("OCCUREDAPPSNAME", occname);
		bean.set("OCCUREDIP", occip);
		bean.set("MODULECODE", mcode);
		bean.set("SUBMODULECODE", smcode);
		bean.set("STATUS", istatus);
		bean.set("MSG", status);
		return bean;
	}

	public static String xmlMonitor(String occname, String occip, String mcode,
			String smcode, int istatus, String status) {
		StringBuilder strinfo = new StringBuilder();
		String fromapp=Context.getSyConf("CC_MONITOR_FROMAPP", "F-MCCP");
		status=status.replace("/", "").replace("\\", "");//应用上报对于部分特殊字符需要处理
		strinfo.append("<ROW>");
		//2016.3.30孙振兴邮件确认，该项不能随意命名，需要留空或填写F-MCCP
		//strinfo.append("<OCCUREDAPPSNAME>" + occname + "</OCCUREDAPPSNAME>");
		strinfo.append("<OCCUREDAPPSNAME>"+fromapp+"</OCCUREDAPPSNAME>");
		strinfo.append("<OCCUREDIP>" + occip + "</OCCUREDIP>");
		strinfo.append("<MODULECODE>" + mcode + "</MODULECODE>");
		strinfo.append("<SUBMODULECODE>" + smcode + "</SUBMODULECODE>");
		strinfo.append("<STATUS>" + istatus + "</STATUS>");
		strinfo.append("<MSG>" + status + "</MSG>");
		strinfo.append("</ROW>");
		return strinfo.toString();
	}
	/**  
     * 过滤ip地址.  
     * @param strip 待过滤的信息
     * @return 返回ip地址，多地址以","分隔
     */
	private static String filterip(String strip) {
		StringBuilder strb=new StringBuilder();
		//String str = "http://192.168.1.1/,http://192.168.1.1/";
		try{
		    String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";  
		    Pattern p = Pattern.compile(regex);  
		    Matcher m = p.matcher(strip);  
		    int n=0;
		    while (m.find()) {  
		        //System.out.println(m.group()); 
		    	if(n>=1){
		    		strb.append(",");
		    	}
		        strb.append(m.group());
		        n++;
		    }   
		    return strb.toString();
		}catch(Exception ex){
			log.error("可用性上报_过滤ip地址异常："+strip,ex);
			return strb.toString();
		}		
	}
}
