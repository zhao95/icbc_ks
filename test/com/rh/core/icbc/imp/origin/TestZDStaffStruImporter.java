package com.rh.core.icbc.imp.origin;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.origin.ZDStaffStruImporter;

public class TestZDStaffStruImporter extends TestEnv {
	
	@Test
	public void  test(){
		new ZDStaffStruImporter().impData("");
	}
	
}
