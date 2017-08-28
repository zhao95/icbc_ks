package com.rh.core.icbc.imp;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.icbc.imp.target.ImpUtils;

public class TestImpUtils extends TestEnv {

	@Test
	public void test() {
//		ImpUtils.bakOrg();
//		ImpUtils.compareOrgInsertLog();
//		ImpUtils.cleanOrgBak();
		System.out.println(ImpUtils.getBakTableSuffix());
	}
}
