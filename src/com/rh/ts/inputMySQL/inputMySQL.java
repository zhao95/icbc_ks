package com.rh.ts.inputMySQL;

import java.io.File; 
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.db.SqlExecutor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * 工行上游接口数据 BOM类数据导入MySQL数据库的方法
 * @author leader
 * @date 2017/10/11
 */
public class inputMySQL {
	 /** log */
    private static Log log = LogFactory.getLog(SqlExecutor.class);
    /**sql执行时间*/
    long sqlTime = 0;
	/**
	 * 解析BIN数据包中的定长文件，使用连接池插入到DB
	 * @throws IOException
	 */
	public void insertMySQL() throws IOException {
		log.info("-------------- impDatafromTable ---------------");
		InfoBean infoBean = new InfoBean();
		OperationDB operationDB = new OperationDB();
		// 优点：分段读取数据文件，没有最大文件限制，不会造成太大的压力。
//		LineIterator it = FileUtils.lineIterator(new File("D://data1.txt"), "GBK");
		LineIterator it = FileUtils.lineIterator(new File("C://ProgramData/MySQL/MySQL Server 5.7/Uploads/BOM_CMPSTRUINFO00000000001.BIN"), "GBK");
		// LineIterator it = FileUtils.lineIterator(new
		// File("D://BOM_CMPSTRUINFO0000000000.BIN"), "GBK");
		try {
		 long startTime = System.currentTimeMillis(); // 起始时间
		 long endTime = 0;
		 @SuppressWarnings("unused")
		long sqlTime = 0;
			while (it.hasNext()) {
				String lineTxt = it.nextLine();
				lineTxt = new String(lineTxt.getBytes("GBK"), "ISO8859-1");
				// 优势方法，使用反射的方法获取和设置原来字段的值。
				//字段名称，反射时使用该名称获取到infoBean属性
				String[] arr = new String[] { "STRU_ID", "STRU_FNAME", "STRU_SNAME", "OLDSYS_STRUID", "FLICENCE_ID",
						"STRU_ADDR", "ZIPCODE", "PHONE", "STRU_SIGN", "STRU_LV", "ADMIN_LV", "SUP_STRU", "SETUP_TIME",
						"LST_ALT_TYPE", "LST_ALT_TIME", "STRU_STATE", "BLICENCE_ID", "REVOKE_TIME", "DIST_SIGN",
						"STRU_GRADE", "CODECERT_ID", "TOWN_FLAG", "BUSI_AREA", "BUSI_SITE_USE", "FEXCHANGE_FLAG",
						"MAN_GRADE", "CHARGE_PROP", "PROFESSION_LEVEL", "NODE_TYPE", "ECON_AREA", "IS_HUN_CITY",
						"IS_HUN_COUNTY", "COUNTRY", "VILLAGE", "NP_OPER_TYPE", "MANAGE_STRU_ID", "SPECIALTY_PROP",
						"FINANCE_STRUID", "PBANK_STRUID", "ADMIN_CODE", "ADMIN_VALUE", "LST_OPTIMIZE_TYPE",
						"OPTIMIZE_PLAN", "BUSI_TYPE", "MEMO", "IS_NEW_BUSI_NODE", "CREATE_DATE", "STRU_FOREIGN_FNAME",
						"STRU_FOREIGN_SNAME", "STRU_CHN_FNAME", "STRU_CHN_SNAME", "BACK1", "BACK2", "BACK3", "BACK4",
						"BACK5", "BACK6", "BACK7", "BACK8", "BACK9", "BACK10" };
				//各字段的字符串长度，用于截取读取到的数据使用
				int[] nums = new int[] { 10, 80, 80, 15, 40, 120, 20, 40, 3, 3, 3, 10, 6, 3, 6, 3, 36, 6, 3, 3, 36, 3,
						9, 3, 3, 3, 3, 3, 3, 3, 3, 3, 100, 40, 3, 10, 80, 14, 14, 6, 100, 3, 4, 3, 100, 1, 8, 80, 80,
						80, 80, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100 };
				//截取字符串的开始值
				int sumNum = 0;
				for (int i = 0; i < arr.length; i++) {
					// 反射获取成员变量的私有字段
					Field field = infoBean.getClass().getDeclaredField(arr[i]);
					// 解决不能设置私有变量的限制
					field.setAccessible(true);
					if (arr[i] == "BUSI_AREA") {
						field.set(infoBean, Integer.parseInt(lineTxt.substring(sumNum, sumNum + nums[i]).trim()));
						sumNum += nums[i];
					} else {
						field.set(infoBean, translateStr(lineTxt.substring(sumNum, sumNum + nums[i]).trim()));
						sumNum += nums[i];
					}
				}

				System.out.println(infoBean.toString());
				// 将数据bean写入到数据库
				int addRcorderSum = operationDB.addRcorder(infoBean);
				System.out.println("截止本次共更新数据："+addRcorderSum+" 条");
				
			}
			 
			 endTime = System.currentTimeMillis(); // 结束时间
			 sqlTime = endTime - startTime;
			 
		} catch (NoSuchFieldException e) {
			// 文件未找到异常
			e.printStackTrace();
		} catch (SecurityException e) {
			//安全异常
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// 字符串长度异常
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// 非法参数异常
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// 反射时的安全权限异常，反射调用私有变量导致，已处理。
			e.printStackTrace();
		} catch (Exception e) {
			// 其他异常
			e.printStackTrace();
		} finally {
			LineIterator.closeQuietly(it);
			log.info("共耗时："+sqlTime+"秒。");
		}

	}

	// 转换为指定格式的方法
	public static String translateStr(String paramStr) throws Exception {
		String str = new String(paramStr.getBytes("ISO8859-1"), "GBK");
		// String str =new String(paramStr.getBytes("ISO8859-1"),"UTF8");
		return str;
	}
}
