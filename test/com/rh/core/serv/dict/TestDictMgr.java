package com.rh.core.serv.dict;

import static org.junit.Assert.fail;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.Lang;

/**
 * 数据字典测试类
 * 
 * @author cuihf
 * 
 */
public class TestDictMgr extends TestEnv {

	/** log */
	private static Log log = LogFactory.getLog(TestDictMgr.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestEnv.start();
		log.debug("Test begin!");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// String sql = "delete from SY_SERV";
		// Context.getSqlExecutor().executeNormal(sql);
		// sql = "delete from SY_SERV_ITEM";
		// Context.getSqlExecutor().executeNormal(sql);
		TestEnv.stop();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testClearCache() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetItemList() {
		System.out.println(JsonUtils.toJson(DictMgr.getItemList("id0")));
	}

	@Test
	public void testGetTreeListStringInt() {
		System.out.println(JsonUtils.toJson(DictMgr.getTreeList("id9", 0)));
	}

	@Test
	public void testGetTreeListStringString() {
		List<Bean> treeList = DictMgr.getTreeList("id9", "itemCode9_1");
		System.out.println(JsonUtils.toJson(treeList));
	}

	@Test
	public void testGetTreeListStringStringInt() {
		fail("Not yet implemented");
		//ArrayList<Bean> treeList = DictUtils.getTreeList("id9", "itemCode9_1", 1);
		//System.out.println(JsonUtils.toJson(treeList));
	}

	@Test
	public void testGetDict() {
		
		fail("Not yet implemented");
	}

	@Test
	public void testGetName() {
		System.out.println(DictMgr.getName("id9", "itemCode9_1"));
		
		//fail("Not yet implemented");
	}

	@Test
	public void testGetFullName() {
		//System.out.println(DictUtils.getFullName("id9", "itemCode9_1"));
		fail("Not yet implemented");
	}

	@Ignore
	public void testInsert() {
		Bean dataBean = new Bean();
		String psql = null;
		for (int i = 0; i < 10; i++) {
			dataBean.remove(Constant.PARAM_PRE_VALUES);
			dataBean.set("DICT_ID", "id" + i);
			dataBean.set("DICT_NAME", "name" + i);
			dataBean.set("DICT_IS_INNER", "1");
			dataBean.set("DICT_TYPE", "2");
			dataBean.set("TABLE_ID", "SY_SERV_DICT_ITEM");
			dataBean.set("TABLE_WHERE", " and dict_id='id" + i + "'");
			dataBean.set("DICT_F_ID", "ITEM_CODE");
			dataBean.set("DICT_F_NAME", "ITEM_NAME");
			dataBean.set("DICT_F_PARENT", "ITEM_PCODE");
			dataBean.set("DICT_LOAD_TYPE", 1);
			dataBean.set("S_FLAG", 1);
			dataBean.set("S_MTIME", DateUtils.getDatetimeTS());

			psql = Context.getBuilder().insertByBean("SY_DICT", dataBean);

			Context.getExecutor().execute(psql, dataBean.getList(Constant.PARAM_PRE_VALUES));

			Bean itemBean = new Bean();
			for (int j = 0; j < 10; j++) {
				itemBean.remove(Constant.PARAM_PRE_VALUES);
				itemBean.set("DICT_ID", "id" + i);
				itemBean.set("ITEM_ID", Lang.getUUID());
				itemBean.set("ITEM_CODE", "itemCode" + i + "_" + j);
				itemBean.set("ITEM_NAME", "itemName" + i + "_" + j);
				itemBean.set("ITEM_PCODE", "");

				psql = Context.getBuilder().insertByBean("SY_SERV_DICT_ITEM",
						itemBean);

				Context.getExecutor().execute(psql, itemBean.getList(Constant.PARAM_PRE_VALUES));

				for (int k = 0; k < 10; k++) {
					itemBean.remove(Constant.PARAM_PRE_VALUES);
					itemBean.set("DICT_ID", "id" + i);
					itemBean.set("ITEM_ID", Lang.getUUID());
					itemBean.set("ITEM_CODE", "itemCode" + i + "_" + j + "_"
							+ k);
					itemBean.set("ITEM_NAME", "itemName" + i + "_" + j + "_"
							+ k);
					itemBean.set("ITEM_PCODE", "itemCode" + i + "_" + j);

					psql = Context.getBuilder().insertByBean(
							"SY_SERV_DICT_ITEM", itemBean);

					Context.getExecutor().execute(psql, itemBean.getList(Constant.PARAM_PRE_VALUES));
				}
			}
		}
		// System.out.println(); // 批量插入列表数据
		System.out.println(Context.getExecutor().count(
				"select count(*) from SY_DICT"));
	}
	
	@Ignore
	public void getDef() {
	
	    ServUtils.getServDef("SY_DICT");
	    ServUtils.getServDef("SY_SERV_DICT_ITEM");
	}
	
	@Test 
	public void testReplace() {
	    String fileIds = "1,333,332324,324234,234232134dsdfs";
	    System.out.println(fileIds.replaceAll(",", "','"));
	}
}
