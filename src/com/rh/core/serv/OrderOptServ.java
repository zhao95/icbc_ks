package com.rh.core.serv;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.var.VarMgr;

/**
 * 页面排序操作
 * @author yjzhou
 * modify time 2017.02.14
 *
 */
public class OrderOptServ extends CommonServ{
	
	private static final Logger LOGGER = Logger.getLogger(OrderOptServ.class);
	
	private static final String SY_SERV_ORDER = "SY_SERV_ORDER";
	
    //序号向前
	private static final String OPT_UP = "OPT_UP";
	
	//序号向后
	private static final String OPT_DOWN = "OPT_DOWN";
	
	/**
	 * 获取服务的排序信息
	 * @param paramBean
	 * @return
	 */
	public OutBean getOrderInfo (ParamBean paramBean){
		OutBean outBean = new OutBean();
		String servId = paramBean.getStr("DATA_SERV_ID").trim();
		Bean bean = ServDao.find(SY_SERV_ORDER, new SqlBean().and("SERV_ID",servId).and("S_FLAG", 1));
		outBean.setData(bean);
		return outBean;
	}

	/**
	 * 更新排序数据
	 * @param paramBean
	 * @return
	 */
	public OutBean updateOrderNum (ParamBean paramBean){
		OutBean outBean = new OutBean();
		String servId = paramBean.getStr("DATA_SERV_ID").trim();
		String pkCode = paramBean.getStr("DATA_ID");
        String optType = paramBean.getStr("OPT_TYPE");
		Bean bean = ServDao.find(SY_SERV_ORDER, new SqlBean().and("SERV_ID",servId).and("S_FLAG", 1));
        String orderItem = bean.getStr("ORDER_ITEM").trim();
        String extWhere = bean.getStr("EXT_WHERE");
        String extWhereValues = bean.getStr("EXT_WHERE_VALUE");
        
		Bean dataBean = ServDao.find(servId, new SqlBean().setId(pkCode));
	    String orderNumStr = dataBean.getStr(orderItem).trim();
		
	    SqlBean sqlBean = new SqlBean();
	    
        try {
        	Transaction.begin();
        	
			if (OPT_UP.equals(optType)) {
		    	//查询比自己小的第一条记录，交换排序字段的值
		    	sqlBean.andLT(orderItem, orderNumStr);
		    	sqlBean.desc(orderItem);

			}else if (OPT_DOWN.equals(optType)) {
		    	//查询比自己 的第一条记录，交换排序字段的值
		    	sqlBean.andGT(orderItem, orderNumStr);
		    	sqlBean.orders(orderItem);

			}else {
				
			}
			
	    	parseExtWhere(extWhere, extWhereValues, sqlBean);
	    	List<Bean> list = ServDao.finds(servId, sqlBean);
	    	if (list == null || list.isEmpty()) {
				log.debug("not found any data");
			}else {
				Bean data = list.get(0);
				ServDao.update(servId, new SqlBean().setId(data.getId()).set(orderItem, orderNumStr));
				ServDao.update(servId, new SqlBean().setId(pkCode).set(orderItem, data.get(orderItem)));
			}
	    	
			Transaction.commit();
		} catch (Exception e) {
			LOGGER.info("update  order num error");
            LOGGER.error(e.getMessage(), e);
            Transaction.rollback();
		}finally {
			Transaction.end();
		}
        
		return outBean.setOk();
	}
	
	/**
	 * 处理数值的格式
	 * @param num 数值
	 * @param standLen 标准长度
	 * @return
	 */
	@SuppressWarnings("unused")
	private String handleNumLen(int num, int standLen){
		StringBuffer buffer = new StringBuffer("");
		String numStr = String.valueOf(num);
		if (numStr.length()<standLen) {
			for (int i = 0; i < standLen - numStr.length(); i++) {
				buffer.append("0");
			}
			buffer.append(numStr);
			return buffer.toString();
		}else{
			return numStr;
		}
	}
	
	/**
	 * 解析扩展的where条件
	 * @param extWhere
	 * @param extValues
	 * @param sqlBean
	 */
	private void parseExtWhere (String extWhere,String extValues,SqlBean sqlBean){
		if (StringUtils.isEmpty(extWhere) || StringUtils.isEmpty(extValues)) {
			LOGGER.info("-----extwhere or extvalues  is empty------");
			return;
		}
		//替换系统变量
		String newExtWhere = VarMgr.replaceSysVar(extWhere);
		int paramNeedCount = getSubStrCount(newExtWhere, "\\?");
		LOGGER.info("this search need params number is :"+paramNeedCount);
		Object[] arr = extValues.split("##");
	    sqlBean.appendWhere(newExtWhere, arr);
	}
	
	/**
	 * 检测字符串模式匹配
	 * @param srcStr
	 * @param patternStr
	 * @return
	 */
	public static int getSubStrCount (String srcStr, String patternStr){
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(srcStr);
		int icount = 0;
        while (matcher.find()) {
        	icount++;
		}
        return icount;
	}
	
}
