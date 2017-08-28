package com.rh.core.icbc.imp;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.target.ImpUser;

public class TestImpUser extends TestEnv {
	
	@Test
	public void test() {
		ImpUser impUser = new ImpUser();
		
		
		// 清除所有icbc用户数据
//		impUser.cleanUserData();
		
		
		// 全量导入用户数据
//		impUser.recuUser();
		
		
		// 增量
		impUser.addUserDatas("2015-10-21 10:00:00:000");
		
	}
}
