package com.rh.core.icbc.basedata.serv;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.icbc.basedata.serv.KSOuttimeApproveServ;
import com.rh.core.serv.ServDao;
import com.rh.core.util.DateUtils;

public class KSUpdateXmjdJobServ {
	private static Log log = LogFactory.getLog(KSOuttimeApproveServ.class);
	public static void startJob() {
		countProcess();
	}
	/**
	 * 计算项目进度,更新项目进度
	 * @param xmId
	 * @param xmSzId
	 */
	public static void countProcess() {
		Bean xmIdBean = new Bean();
//		xmIdBean.set("_WHERE_", "AND XM_ID='"+xmId+"'");
		List<Bean> processXMBeanList = ServDao.finds("TS_XMGL", xmIdBean);
		List<Bean> updateProcessXMBeanList = new ArrayList<Bean>();
		for (int i = 0; i < processXMBeanList.size(); i++) {
			String xmId = processXMBeanList.get(i).getStr("XM_ID");
//			String xmJd = processXMBeanList.get(i).getStr("XM_JD");
			String xmStart = processXMBeanList.get(i).getStr("XM_START");
			String xmeEnd = processXMBeanList.get(i).getStr("XM_END");
			Date currentDate = new Date();
			Date xmStartDate=null;
			Date xmEndDate=null;
			try {
				xmStartDate = DateUtils.parseDate(xmStart);
			} catch (ParseException e) {
				log.error("项目开始时间转换异常，时间为:"+xmStart);
			}
			try {
				xmEndDate = DateUtils.parseDate(xmeEnd);
			} catch (ParseException e) {
				log.error("项目结束时间转换异常，时间为:"+xmeEnd);
			}
			if(currentDate.before(xmStartDate)){
				//当前时间在项目开始时间之前
				//设置项目百分比为0%
				Bean bean0 = new Bean();
				bean0.set("XM_ID",xmId);
				bean0.set("XM_JD","0%");
				updateProcessXMBeanList.add(bean0);
				
			}
			if(xmStartDate.before(currentDate) && currentDate.before(xmEndDate)){
				//当前时间在项目开始和项目结束之间
				//计算百分比
				Bean beanX = new Bean();
				beanX.set("XM_ID",xmId);
				//调用百分比转换器方法
				String formatProcess = dateFormatProcess(xmStartDate,xmEndDate,currentDate);
				beanX.set("XM_JD",formatProcess);
				updateProcessXMBeanList.add(beanX);
			}
			if(xmEndDate.before(currentDate)){
				//当前时间在项目结束时间之后
				//设置项目百分比为100%
				Bean bean100 = new Bean();
				bean100.set("XM_ID",xmId);
				bean100.set("XM_JD","100%");
				updateProcessXMBeanList.add(bean100);
			}
		}
		//统一改变项目进度百分比
		if(updateProcessXMBeanList.size()>0){
			//批量更新状态
			//将要更新的字段放入到集合中
			List<String> updateFields = new ArrayList<String>();
			updateFields.add("XM_JD");
			//使用批量更新方法更新指定字段
			ServDao.updates("TS_XMGL", updateFields, updateProcessXMBeanList);
		}
		
		//改变项目设置状态（暂废止）
//		int allSZNum = ServDao.count("TS_XM_SZ", xmIdBean);
//		Bean xmIdFinishBean = new Bean();
//		xmIdFinishBean.set("_WHERE_", "AND XM_SZ_TYPE like '%已结束' and XM_ID='"+xmId+"'");
//		int endSZNum = ServDao.count("TS_XMGL_SZ", xmIdFinishBean);
//		int processNum = endSZNum%allSZNum;
//		Bean newNumBean = new Bean();
//		newNumBean.set("XM_JD",processNum);
//		ServDao.updates("TS_XMGL_SZ", newNumBean, xmIdBean);
	}
	/**
	 * 日期计算百分比
	 * @param 项目开始时间
	 * @param 项目结束时间
	 * @param 当前时间
	 * @return 20% 类的字符串格式
	 */
	public static String dateFormatProcess(Date startDate, Date endDate, Date currentDate) {
		//使用大数据格式处理日期格式
		BigDecimal b1 = new BigDecimal(startDate.getTime());
		BigDecimal b2 = new BigDecimal(endDate.getTime());
		BigDecimal bc = new BigDecimal(currentDate.getTime());
		//实例化数值转换器
		DecimalFormat df = new DecimalFormat("0%");
		//设置最大小数位
		//df.setMaximumFractionDigits(1);
		//设置为四舍五入方式
		//df.setRoundingMode(RoundingMode.HALF_UP);
		//减法
		BigDecimal dateMiuns = bc.subtract(b1);
		BigDecimal dateMiunsAll = b2.subtract(b1);
		//除法
		BigDecimal bOk = dateMiuns.divide(dateMiunsAll, 2, RoundingMode.HALF_UP);
		//使用转换器转换为百分比格式
		String bOk1 =df.format(bOk); 
		return bOk1;
	}
	
}
