package com.rh.core.icbc.imp.origin;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.origin.ZDStaffBInfoImporter;


public class TestZDStaffBInfoImporter extends TestEnv {
	
	@Test
	public void test (){
		new ZDStaffBInfoImporter().imp("E:/staffbinfo");
	}

}
