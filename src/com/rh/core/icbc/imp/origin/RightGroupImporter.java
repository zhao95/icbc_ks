package com.rh.core.icbc.imp.origin;

import java.io.File;


import com.rh.core.serv.base.BaseServ;

/**
 * 用户权限分组表数据导入
 * @author cayiqing
 *
 */
public class RightGroupImporter extends BaseServ {
	
	private static final String SERVID = "CC_CT_RIGHTGROUP";
	private final String[] items = {"GID","GCODE","GNAME"};
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
