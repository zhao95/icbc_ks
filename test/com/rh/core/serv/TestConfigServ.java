package com.rh.core.serv;

import java.util.UUID;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.comm.ConfServ;

public class TestConfigServ extends TestEnv {

	@Test
	public void testGetPubConf() {
		String randomStr = UUID.randomUUID().toString();
		String newId = "JU" +randomStr;
		System.out.println(newId.length());
		ConfServ cs = new ConfServ();
		ParamBean paramBean = new ParamBean();
		paramBean.set("serv", "SY_CONFIG");
		paramBean.set("CONF_ID", newId);
		paramBean.set("CONF_NAME", "JUNIT_TEST_NAME");
		paramBean.set("CONF_VALUE", "JUNIT_TEST_VALUE");
		paramBean.set("CONF_KEY", newId);
		paramBean.set("CONF_ORDER", "0");
		paramBean.set("CONF_FLAG", "0");
		paramBean.set("CONF_MEMO", "测试");
//		paramBean.set("S_PUBLIC", "0");
		cs.save(paramBean);
		
//		Bean result = cs.getConf(newId);
//		Assert.assertEquals(paramBean.get("CONF_ID"), result.get("CONF_ID"));
	}

}
