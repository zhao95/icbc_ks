/**
 * 
 */
package com.rh.core.util;

import java.util.List;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.util.var.VarMgr;

/**
 * @author Administrator
 *
 */
public class TestJsonUtils extends TestEnv {

	/**
	 * Test method for {@link com.rh.core.util.JsonUtils#toJson(com.rh.core.base.Bean)}.
	 */
	@Test
	public void testToJsonBean() {
		String sql;
		Bean bean = new Bean();
		bean.set("TEST", "okokok''d\"");
		sql = "select * from SY_SERV";
		bean.set("SY_SERV", Context.getExecutor().query(sql));
		bean.set("SY_SERV2", Context.getExecutor().query(sql));
		bean.set("SY_SERV3", Context.getExecutor().queryOne(sql));
		sql = "select * from SY_SERV_ITEM";
		//bean.set("SY_SERV_ITEM", Context.getExecutor().query(sql));
		System.out.println(JsonUtils.toJson(bean));
	}

	/**
	 * Test method for {@link com.rh.core.util.JsonUtils#toJson(java.util.List)}.
	 */
	@Test
	public void testToJsonListOfQ() {
		String sql;
		sql = "select * from SY_SERV_ITEM";
		System.out.println(JsonUtils.toJson(Context.getExecutor().query(sql)));
	}
	
	/**
	 * Test method for {@link com.rh.core.util.JsonUtils#toBean(java.util.List)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testJsonToBean() {
		String json = "{'name':'袁大头','id':'0100','SUB_USER':[{'user':'刘大头','id':'111','arrays':['1','2']},{'user':'金大头','id':'112'}]}";
		Bean bean = JsonUtils.toBean(json);
		System.out.println(bean.get("name"));
		System.out.println(bean.get("id"));
		System.out.println(bean.get("SUB_USER"));
		List<Bean> userList = (List<Bean>) bean.get("SUB_USER");
		for (Bean user : userList) {
			System.out.println(user.get("id"));
			System.out.println(user.get("user"));
			if (user.contains("arrays")) {
			    for (Object array : user.getList("arrays")) {
			        System.out.println(array);
			    }
			}
		}
	}
	
	@Test
	public void testMethod() {
	    System.out.println(JsonUtils.toJson(VarMgr.getConfMap()));
	    System.out.println(JsonUtils.toJson(VarMgr.getOrgMap()));
	    System.out.println(JsonUtils.toJson(VarMgr.getDateMap()));
	}
	
	@Test
	public void testToBean() {
	    String str = "{'_PAGE_':{'SHOWNUM':8},'_NOPAGE_':'true','_SELECT_':'TODO_ID,OWNER_CODE,TODO_URL,TODO_CODE,TODO_SEND_TIME,TODO_CODE_NAME,TODO_OBJECT_ID1,TODO_TITLE,SEND_USER_CODE,TODO_EMERGENCY,TODO_CONTENT','_searchWhere':' and (TODO_CATALOG =0 or TODO_CATALOG =1) and OWNER_CODE=\\'@USER_CODE@\\''}";
	    Bean testBean = JsonUtils.toBean(str);
	    System.out.println(testBean.toString());
	}

}
