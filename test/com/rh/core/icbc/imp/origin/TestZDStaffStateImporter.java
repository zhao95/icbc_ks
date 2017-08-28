package com.rh.core.icbc.imp.origin;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.origin.ZDStaffStateImporter;

public class TestZDStaffStateImporter extends TestEnv {
	
	@Test
	public void test(){
		new ZDStaffStateImporter().imp("E:/staffstate");
	}

}
