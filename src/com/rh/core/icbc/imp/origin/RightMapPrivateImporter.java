package com.rh.core.icbc.imp.origin;

import java.io.File;

import com.rh.core.serv.base.BaseServ;

/**
 * CC_CT_RIGHTMAPPRIVATE表的数据导入
 * @author caoyiqing
 *
 */
public class RightMapPrivateImporter extends BaseServ {

	private static final String SERVID = "CC_CT_RIGHTMAPPRIVATE";
	private final String[] items = {"MAP_ID","GID_1","GID_2","RIGHT_VALUE","ORG_ID"};
	
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
