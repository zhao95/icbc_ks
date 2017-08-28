package com.rh.core.icbc.imp.origin;


import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.origin.RightMapPrivateImporter;

/**
 * CC_ICBC_RIGHTMAPPRIVATE表的数据导入
 * @author caoyiqing
 *
 */
public class TestRightMapPrivateImporter extends TestEnv {

	@Test
	public void test(){
		new RightMapPrivateImporter().impData("E:/icbc/rightMapPrivate_20150910.txt");
	}
}
