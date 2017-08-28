package com.rh.core.icbc.imp.origin;


import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.origin.SpecialGroupUserImporter;


public class TestSpecialGroupUserImporter extends TestEnv{
	
	@Test
	public void test(){
		new SpecialGroupUserImporter().impData("E:/icbc/specialGroupUser_20150910.txt");
	}
	

}
