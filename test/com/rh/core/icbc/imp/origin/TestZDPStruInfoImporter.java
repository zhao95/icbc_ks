package com.rh.core.icbc.imp.origin;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.origin.ZDPStruInfoImporter;

import org.junit.Test;


public class TestZDPStruInfoImporter extends TestEnv {
	
	@Test
	 public void Test(){
		
		new ZDPStruInfoImporter().impIncrementalData("E:/BOM");
//		new ZDPStruInfoImporter().impFullData("E:/BOM_CMPSTRUINFO0000000000.BIN");
		 
	 }
	
}
