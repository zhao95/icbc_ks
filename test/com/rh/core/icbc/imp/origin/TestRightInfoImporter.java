package com.rh.core.icbc.imp.origin;


import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.origin.RightInfoImporter;

/**
 * 数据导入表CC_ICBC_RIGHTINFO
 * @author caoyiqing
 *
 */
public class TestRightInfoImporter extends TestEnv {
	
	@Test
	public void test(){
//		new RightInfoImporter().impData("E:/icbc/rightInfo_20150910.txt");
		
		new RightInfoImporter().impData("E:/icbc/rightInfo_20150910.txt");
	}
}
