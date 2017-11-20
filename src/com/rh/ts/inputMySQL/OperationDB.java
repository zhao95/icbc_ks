package com.rh.ts.inputMySQL;

import java.lang.reflect.Field; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.db.Transaction;
/**
 * 操作数据库，插入解析后的数据
 * @author leader
 *
 */
public class OperationDB {
	private Connection con = null;
/**
 * 将传入的bean对象的数据插入到数据库中
 * @param infoBean
 * @throws SQLException
 * @throws Exception
 */
	public int addRcorder(InfoBean infoBean){
		if (con == null) {
			//获取数据库链接
//			con = BeyondbConnection.getConnection();
			//连接池方式
//			con = Transaction.getConn();
//			con = BeyondbConnection.getConnection();
			
			
		}
		PreparedStatement pstmt = null;
		int executeUpdateSum = 0;
		try {
			//bom_cmpstruinfo
			String sql = "insert into BOM_ZDPSTRUINFO (STRU_ID,STRU_FNAME,STRU_SNAME,OLDSYS_STRUID,FLICENCE_ID,STRU_ADDR,ZIPCODE,PHONE,STRU_SIGN,STRU_LV,ADMIN_LV,SUP_STRU,SETUP_TIME,LST_ALT_TYPE,LST_ALT_TIME,STRU_STATE,BLICENCE_ID,REVOKE_TIME,DIST_SIGN,STRU_GRADE,CODECERT_ID,TOWN_FLAG,BUSI_AREA,BUSI_SITE_USE,FEXCHANGE_FLAG,MAN_GRADE,CHARGE_PROP,PROFESSION_LEVEL,NODE_TYPE,ECON_AREA,IS_HUN_CITY,IS_HUN_COUNTY,COUNTRY,VILLAGE,NP_OPER_TYPE,MANAGE_STRU_ID,SPECIALTY_PROP,FINANCE_STRUID,PBANK_STRUID,ADMIN_CODE,ADMIN_VALUE,LST_OPTIMIZE_TYPE,OPTIMIZE_PLAN,BUSI_TYPE,MEMO,IS_NEW_BUSI_NODE,CREATE_DATE,STRU_FOREIGN_FNAME,STRU_FOREIGN_SNAME,STRU_CHN_FNAME,STRU_CHN_SNAME,BACK1,BACK2,BACK3,BACK4,BACK5,BACK6,BACK7,BACK8,BACK9,BACK10)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

//			pstmt = con.prepareStatement(sql);
//			 pstmt = con.prepareStatement(sql);
			List<Object> values =new ArrayList<Object>();
			String[] arr = new String[]{"STRU_ID","STRU_FNAME","STRU_SNAME","OLDSYS_STRUID","FLICENCE_ID","STRU_ADDR","ZIPCODE","PHONE","STRU_SIGN","STRU_LV","ADMIN_LV","SUP_STRU","SETUP_TIME","LST_ALT_TYPE","LST_ALT_TIME","STRU_STATE","BLICENCE_ID","REVOKE_TIME","DIST_SIGN","STRU_GRADE","CODECERT_ID","TOWN_FLAG","BUSI_AREA","BUSI_SITE_USE","FEXCHANGE_FLAG","MAN_GRADE","CHARGE_PROP","PROFESSION_LEVEL","NODE_TYPE","ECON_AREA","IS_HUN_CITY","IS_HUN_COUNTY","COUNTRY","VILLAGE","NP_OPER_TYPE","MANAGE_STRU_ID","SPECIALTY_PROP","FINANCE_STRUID","PBANK_STRUID","ADMIN_CODE","ADMIN_VALUE","LST_OPTIMIZE_TYPE","OPTIMIZE_PLAN","BUSI_TYPE","MEMO","IS_NEW_BUSI_NODE","CREATE_DATE","STRU_FOREIGN_FNAME","STRU_FOREIGN_SNAME","STRU_CHN_FNAME","STRU_CHN_SNAME","BACK1","BACK2","BACK3","BACK4","BACK5","BACK6","BACK7","BACK8","BACK9","BACK10"};
			for (int i = 0; i < arr.length; i++) {
				Field field = infoBean.getClass().getDeclaredField(arr[i]);
				field.setAccessible(true);
				Object object = field.get(infoBean);
				values.add(object);
//				pstmt.setObject(i+1,object);
			}
			//返回插入数据的条数
			int executeUpdate =Transaction.getExecutor().execute(sql, values);
//			int executeUpdate = pstmt.executeUpdate();
//			System.out.println("本次总共插入数据："+executeUpdate+"条。");
			executeUpdateSum+=executeUpdate;
			
			
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			//释放预处理，连接池资源
			try {
				if (pstmt != null) {
	                 pstmt.close();
	             }
				if(con!=null){
					con.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return executeUpdateSum;
	}
}