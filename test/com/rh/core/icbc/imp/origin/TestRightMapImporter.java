package com.rh.core.icbc.imp.origin;


import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.origin.RightMapImporter;

/**
 * 导入数据到表CC_ICBC_RIGHTMAP
 * @author caoyiqing
 *
 */
public class TestRightMapImporter extends TestEnv {
	
	@Test
	public void test(){
		new RightMapImporter().impData("E:/icbc/rightMap_20150910.txt");
	}

}
