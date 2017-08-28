package com.rh.core.icbc.imp.origin;

import java.io.File;

import com.rh.core.serv.base.BaseServ;

public class SupbrExceptionImporter extends BaseServ {
	
	private static final String SERVID = "CC_CT_SUPBREXCEPTION";	
	private final String[] items = {"EID", "ORG_ID_1", "P_ORG_ID"};
	
	/** 
	 * 数据导入
	 * @param fileUrl 数据文件路径
	 */
	public void impData(String fileUrl){
		if(CommonImporter.isExistFile(fileUrl)){
			File file = new File(fileUrl);
			CommonImporter.addData(SERVID, file, items);
		}
	}

}
