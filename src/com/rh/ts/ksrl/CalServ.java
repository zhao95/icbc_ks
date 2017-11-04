package com.rh.ts.ksrl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.FileMgr;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;

/**
 * 对应服务:TS_KS_CAL | 考试日历
 *
 */
public class CalServ extends CommonServ {
	/**
	 * 批量导入考试日程信息
	 * @param paramBean 参数Bean
	 * @return 结果
	 */
	public OutBean excelImp(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		
		String fileId = paramBean.getStr("fileId");
		
		if (!fileId.isEmpty()) {
			Bean fileBean = FileMgr.getFile(fileId);
			InputStream in = null;
			try {
				in = FileMgr.download(fileBean);
				Workbook book = Workbook.getWorkbook(in);
				Sheet sheet = book.getSheet(0);

				for (int i = 1; i < sheet.getRows(); i++) {
					Cell[] cells = sheet.getRow(i);
					Bean bean = cellsToBean(cells); // 取得行信息的Bean
					
					//保存信息
					try {
						if (bean.isNotEmpty("CAL_NAME")) {
							ServDao.save(paramBean.getServId(), bean);
						}
					} catch (Exception e2) {
						System.out.println(e2.getMessage());
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
		
		return outBean;
	}

	/**
	 * 行数据-->Bean对象
	 * @param cells 表格数组
	 * @return Bean
	 */
	private Bean cellsToBean(Cell[] cells) {
		String codes = "CAL_NAME,START_DATE,END_DATE,CAL_TYPE,CAL_MONTH,CAL_COMMENT"; //顺序:考试名称,开始时间,结束时间,类型,月份,备注
		String[] codeArr = codes.split(",");
		Bean bean = new Bean();
		
		for (int i = 0; i < cells.length; i++) {
			if (CellType.DATE.equals(cells[i].getType())) {
				DateCell cell = (DateCell) cells[i];
				Date date = cell.getDate();
				bean.set(codeArr[i], DateUtils.getStringFromDate(date, "yyyy-MM-dd"));
			} else {
				String content = cells[i].getContents();
				bean.set(codeArr[i], content.trim());
			}
			
		}
		
		return bean;
	}
	
	/**
	 * 分页查询返回结果
	 * @param paramBean
	 * @return outBean 
	 */
	public OutBean queryByPage(ParamBean paramBean){
		OutBean outBean = new OutBean();
		
		return outBean;
		
	}
	
	
	// 查询前添加查询条件
	protected void beforeFinds(ParamBean paramBean) {
		ParamBean roleParam = new ParamBean();
		UserBean userBean = Context.getUserBean();
		String userCode = userBean.getStr("USER_CODE");
		String userOdeptCode = userBean.getStr("ODEPT_CODE");
		//用户编码设置进roleParam里面，使其查询处用户所有的权限
		roleParam.set("USER_CODE", userCode);
		
		StringBuilder param_where = new StringBuilder();
		param_where.append(" AND  (SELECT d.code_path from sy_org_dept d   ");
		param_where.append(" WHERE d.dept_code ='"+userOdeptCode+"') like CONCAT('%',S_ODEPT,'^%') ");
		paramBean.set(Constant.PARAM_WHERE, param_where.toString());
	}
	
	
}
