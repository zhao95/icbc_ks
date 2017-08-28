package com.rh.core.icbc.imp.origin;


import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.origin.RightGroupImporter;

/**
 * 导入数据到表CC_ICBC_RIGHTMAP
 * @author caoyiqing
 *
 */
public class TestRightGroupImporter extends TestEnv {
	
	@Test
	public void test(){
		new RightGroupImporter().impData("E:/icbc/rightGroup_20150910.txt");

	}

}
