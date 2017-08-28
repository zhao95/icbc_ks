package com.rh.core.serv;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;

public class TestFileServ extends TestEnv {

    @Test
	public void testGetPubConf() throws IOException {
		String randomStr = UUID.randomUUID().toString();
//		String data = "testdata";
//		StringBufferInputStream sis = new StringBufferInputStream(data);
		String newId = "JU" +randomStr;
		System.out.println(newId.length());
//		FileServ fs = new FileServ();
		Bean paramBean = new Bean();
		paramBean.set("FILE_ID", newId);
		paramBean.set("FILE_NAME", "JUNIT_TEST_NAME");
		paramBean.set("FILE_TYPE", "app/msword");
		paramBean.set("FILE_MEMO", "测试");
//		fs.upload(paramBean, sis);
		
//		fs.download(newId);
		//TODO download and assert
	}

}
