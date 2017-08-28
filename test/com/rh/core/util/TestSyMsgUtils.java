package com.rh.core.util;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;

public class TestSyMsgUtils extends TestEnv {

	@Test
	public void test() {
		String result = Context.getSyMsg("SY_MISSING_PARAM", "测试", "参数");
		System.out.println(result);

//		String s = "a{b}c{d";
//		int charCount = s.replaceAll("[^{]", "").length();
//		System.out.println(charCount);

	}
	@Test
	public void testReplace() {
	    String src = "dddd#SERV_ID#ddsdfsd#SERV_NAME#";
	    Bean bean = new Bean();
	    bean.set("SERV_ID", "你好");
	    bean.set("SERV_NAME", "欢迎");
	    System.out.println(src.replaceAll("#(\\w+_?\\w)#", "$1"));
	}
	
	   @Test
	    public void testArrayJoin() {
	        String[] src = {"111", "333", "444", "2224567"};
	        System.out.println(Lang.arrayJoin(src, ","));
	    }
}
