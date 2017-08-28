package com.rh.core.icbc.imp.origin;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.origin.ZDStaffContactImporter;

public class TestZDStaffContactImporter extends TestEnv{
	
	@Test
	public void test() {
//		ParamBean bean = new ParamBean("SY_HRM_ZDSTAFFCONTACT");
//		bean.set("FILEURL", "C:/HRM_ZDSTAFFCONTACT0000000000.BIN");
//		bean.set("FILEURL", "D:/12.bin");
//		new ZDStaffContactServ().impData(bean);
		new ZDStaffContactImporter().imp("E:/staffcontact");
//		System.out.println(String.valueOf('\33'));
	}
}
