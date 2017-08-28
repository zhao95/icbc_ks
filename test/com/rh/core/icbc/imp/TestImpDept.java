package com.rh.core.icbc.imp;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.target.ImpDept;

public class TestImpDept extends TestEnv {

	@Test
    public void test() {
		ImpDept impDept = new ImpDept();
		
		// 同步组织机构总方法
//		impDept.recuDept();
		
		// 增量同步组织机构方法
//		impDept.addDeptDatas("2015-10-22 10:00:00:000");
		
		
    }
}
