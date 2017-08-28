package com.rh.core.icbc.imp.origin;

import java.io.File;

import com.rh.core.serv.base.BaseServ;

/**
 * 导入数据到表CC_CT_RIGHTMAP
 * @author caoyiqing
 *
 */
public class RightMapImporter extends BaseServ {
	
	private static final String SERVID = "CC_CT_RIGHTMAP";
	private final String[] items = {"MAP_ID","GID_1","GID_2","RIGHT_VALUE"};
	
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
