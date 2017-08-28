package com.rh.core.icbc.imp.origin;

import java.io.File;

import com.rh.core.serv.base.BaseServ;

/**
 * 数据导入表CC_CT_RIGHTINFO
 * @author caoyiqing
 *
 */
public class RightInfoImporter extends BaseServ {
	
	private static final String SERVID = "CC_CT_RIGHTINFO";
	
	private final String[] items = {"RIGHT_VALUE","RIGHT_NAME"};

	public void impData(String fileUrl){
		if(CommonImporter.isExistFile(fileUrl)){
			File file = new File(fileUrl);
			CommonImporter.addData(SERVID, file, items);
		}
	}
}
