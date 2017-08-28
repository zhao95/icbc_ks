package com.rh.core.icbc.imp.origin;



import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.origin.SupbrExceptionImporter;


public class TestSupbrExceptionImporter extends TestEnv {
	
	@Test
	public void test(){
		new SupbrExceptionImporter().impData("E:/icbc/SupbrException_20150910.txt");
	}

}
