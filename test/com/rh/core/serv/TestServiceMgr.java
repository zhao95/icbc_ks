/**
 * 
 */
package com.rh.core.serv;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;
import com.rh.core.base.BeanUtils;
import com.rh.core.base.Context;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.JsonUtils;

/**
 * @author Administrator
 *
 */
public class TestServiceMgr extends TestEnv {

	/** log */
	Log log = LogFactory.getLog(TestServiceMgr.class);
	/**
	 * Test method for {@link com.rh.core.serv.ServiceMgr#act(java.lang.String, java.lang.String, com.rh.core.base.Bean, java.lang.Object[])}.
	 */
	@Test
	public void testAct() {
		ParamBean paramBean = new ParamBean();
		paramBean.set("serv", "SY_SERV_ITEM");
		log.debug(JsonUtils.toJson(ServMgr.act(paramBean.getStr("serv"), "serv", paramBean)));
		log.debug(JsonUtils.toJson(ServMgr.act(paramBean.getStr("serv"), "query", paramBean)));
		paramBean.setId("05j1fNKUd52FSBPOTGyjCK");
		log.debug(JsonUtils.toJson(ServMgr.act(paramBean.getStr("serv"), "byid", paramBean)));
	}
	
	@Test
	public void testServ() {
	    ParamBean paramBean = new ParamBean();
		paramBean.set("serv", "SY_SERV");
		log.debug(JsonUtils.toJson(ServMgr.act(paramBean.getStr("serv"), "serv", paramBean)));
	}
	
//	@SuppressWarnings("unchecked")
//    @Test
//	public void testServDef() {
//	    String sql = "select * from SY_SERV";
//	    List<Bean> servList = Context.getExecutor().query(sql);
//	    String psql  = null;
//	    List<Object[]> params = new ArrayList<Object[]>();
//	    for (Bean serv  : servList) {
//	        boolean changed  = false;
//	        if (ServUtils.getItem(serv.getId(), "S_FLAG") != null) {
//	            String where = serv.getStr("SERV_SQL_WHERE");
//	            if (where.indexOf("S_FLAG") < 0) {
//	                serv.set("SERV_SQL_WHERE",  where + " and S_FLAG=1");
//	                changed = true;
//	            }
//	        }
//            if (ServUtils.getItem(serv.getId(), "S_MTIME") != null) {
//                if (serv.isEmpty("SERV_SQL_ORDER")) {
//                    serv.set("SERV_SQL_ORDER",  " S_MTIME desc");
//                    changed = true;
//                }
//                if (serv.isEmpty("SERV_SEARCH_TIME")) {
//                    serv.set("SERV_SEARCH_TIME",  "S_MTIME");
//                    changed = true;
//                }
//            }
//            if (ServUtils.getItem(serv.getId(), "S_USER") != null) {
//                if (serv.isEmpty("SERV_SEARCH_USER")) {
//                    serv.set("SERV_SEARCH_USER",  "S_USER");
//                    changed = true;
//                }
//            }
//            if (ServUtils.getItem(serv.getId(), "S_DEPT") != null) {
//                if (serv.isEmpty("SERV_SEARCH_DEPT")) {
//                    serv.set("SERV_SEARCH_DEPT",  "S_DEPT");
//                    changed = true;
//                }
//            }
//            if (serv.isEmpty("SERV_KEYS")) {
//                Bean servDef = ServUtils.getServDef(serv.getId());
//                Map<String, Bean> items = (Map<String, Bean>) servDef.get(SERV_DEF.$ITEMS);
//                if(!items.isEmpty()) { //获取主键字段
//                    boolean first = true;
//                    for (String key : items.keySet()) {
//                        if (first) {
//                            serv.set("SERV_KEYS", ((Bean) items.get(key)).get("ITEM_CODE"));
//                            changed = true;
//                            break; //跳出循环
//                        }
//                    }
//                }
//            }
//            if (changed) {
//                psql = Context.getBuilder().updateByid("SY_SERV", serv);
//                params.add((Object[]) serv.get(Constant.PARAM_PRE_VALUES));
//            }
//	    }
//	    if (psql != null) {
//	        Context.getExecutor().executeBatch(psql, params); //批量更新列表数据
//	    }
//	}
	
	@Test
	public void testImpServ() {
	    ServUtils.impServDef("SY_TEST2", "SY_TEST2", "SY_TEST2", false);
	}
	
	@Test
	public void testCombine() {
	    Bean test = new Bean();
	    test.set("TEST_NAME", "A001");
	    test.set("TEST_MEMO", "2012");
	    System.out.println(ServUtils.genCombineItem("SY_TEST", "TEST_ID", 
	            "BBC:#TEST_NAME#-#TEST_MEMO##_SN,4#", test));
	}
	
	@Test
    public void testRegex() {
        String abc = "I-LOVE_YOU:0004";
        String pn = "([-_])";
        System.out.println(abc.replaceAll(pn, "\\\\$1"));
        pn = ".{1}-.{4}_.{3}:(\\d{4})";
        Pattern pattern = Pattern.compile(pn);
        Matcher mt = pattern.matcher(abc);
        while (mt.find()) {
            System.out.println(mt.group(1));
        }
        System.out.println(abc.matches(pn));
    }
	
	@Test
    public void testInitServ() {
        String abc = "ddddddd in @@NS_CHNL_VIEW@@";
        String pn = "@@(.+)@@";
        Pattern pattern = Pattern.compile(pn);
        Matcher mt = pattern.matcher(abc);
        while (mt.find()) {
            System.out.println(mt.group(1));
            System.out.println(mt.group(0));
        }
    }
	
	@Test
	public void testTableName() {
	    String sql = "select * from (select TBL_TEST.testid, tbl_test2.testName, c.test_memo from tbl_test,"
	    + "TBL_TEST2, tbl_org_user c where TBL_test.testid = tbl_test2.testid and tbl_test.testid = c.testid)";
	    String pn = "tbl(\\w|_)*";
	    Pattern pattern = Pattern.compile(pn, Pattern.CASE_INSENSITIVE); //不区分大小写
        Matcher mt = pattern.matcher(sql);
        StringBuffer sb=new StringBuffer();
        while (mt.find()) {
            String replacement=mt.group(0).toUpperCase();
            mt.appendReplacement(sb, replacement);
        }
        System.out.println(sb);
        mt.appendTail(sb);
        System.out.println(sb);
	}
	
	@Test
	public void testReplaceValues() {
	    String src = "你好#TEST_ID#，这是一个测试#，不知道准确不#TEST_NAME#？";
	    Bean testBean = new Bean();
	    testBean.set("TEST_ID", "world").set("TEST_NAME", "世界的主宰");
	    System.out.println(BeanUtils.replaceValues(src, testBean));
	}
	
	@Test
	public void testFormTable() {
	    ParamBean param = new ParamBean();
	    param.set("TABLE_VIEW", "jdbc/oa.TBL_MSV_GONGWEN");
	    ServMgr.act("SY_SERV", "fromTable", param);
	}
	
    @Test
	public void testGenServIDStr() {
	    SqlBean param = new SqlBean();
	    param.selects("SERV_ID,SERV_NAME").and("S_FLAG", 1).andIn("SERV_TYPE", 1, 2).orders("SERV_ID");
	    List<Bean> dataList = ServMgr.act("SY_SERV", ServMgr.ACT_FINDS, new ParamBean(param)).getDataList();
	    for (Bean data : dataList) {
	        System.out.println("   /** 服务主键：" + data.getStr("SERV_NAME") + " */");
	        System.out.println("   public static final String " + data.getStr("SERV_ID") + " = \"" 
	                + data.getStr("SERV_ID") + "\";");
	        
	    }
	}
	
	@Test
	public void testGetSearchLink() {
	    ServDefBean servDef = ServUtils.getServDef("SY_ADDRESS_LIST");
	    Bean linkDef = ServUtils.getSearchLinkDef(servDef, "SY_CALENDAR");
	    log.debug(linkDef.getId());
	}
	
	@Test
	public void testTimeStamp() {
	    String sql = "select distinct table_action from sy_serv where serv_id in (select serv_id from sy_serv_item where item_code='S_ATIME')";
	    List<Bean> datas = Context.getExecutor().query(sql);
	    for (Bean data : datas) {
	        String t = data.getStr("TABLE_ACTION");
	        System.out.println("comment on column " + t + ".s_ATIME is '添加时间';");
//	        System.out.println("alter table " + t + " add S_TIME varchar2(23);");
//	        System.out.println("update " + t + " set s_time = to_char(S_ATIME, 'yyyy-mm-dd hh24:mi:ss:ff3');");
//	        System.out.println("alter table " + t + " drop (S_ATIME);");
//	        System.out.println("alter table " + t + " rename column s_time to S_ATIME;");
	    }
	}
	
	@Test
	public void testSyncDb() {
	    ParamBean param = new ParamBean().setId("SY_ORG_USER");
	    ServMgr.act(ServMgr.SY_SERV, "reloadServ", param);
	}
	
	@Test
	public void testRename() {
	    ParamBean param = new ParamBean();
	        param.set(Constant.PARAM_SELECT, "TABLE_NAME")
	            .set(Constant.PARAM_WHERE, "and TABLE_NAME like 'SY%'");
	        param.set(Constant.PARAM_ORDER, "TABLE_NAME desc");
	        List<Bean> dataList = ServMgr.act("SY_SERV_TABLE", ServMgr.ACT_FINDS, param).getDataList();
	        for (Bean data : dataList) {
	            String n = data.getStr("TABLE_NAME");
	            String t = "OA_" + n;
	            System.out.println("rename " + n + " to OA_" + t + ";");
	            System.out.println("update sy_comm_menu set menu_info = replace(menu_info, '" + n + "', '" +
	                    t + "') where menu_info like '" + n + "%';");
	            
	        }
	}
	
	@Test
	public void testQuery() {
        ParamBean param = new ParamBean("SY_ORG_USER", "query");
        param.setWhere("and 2=2 and CMPY_CODE='ruaho'");
        param.setShowNum(10);
        int i = 1;
        while(true) {
            param.setNowPage(i);
            OutBean out = ServMgr.act(param);
            List<Bean> data = out.getDataList();
            System.out.println("===============page" + i + "===============");
            System.out.println(data.size());
            if (data.size() < 10) {
                break;
            }
            i++;
        }
	}
	@Test
    public void testFinds() {
        ParamBean param = new ParamBean("SY_ORG_USER", "finds");
        param.set("CMPY_CODE", "ruaho");
        param.setShowNum(10);
        int i = 1;
        while(true) {
            param.setNowPage(i);
            OutBean out = ServMgr.act(param);
            List<Bean> data = out.getDataList();
            System.out.println("===============page" + i + "===============");
            System.out.println(data.size());
            if (data.size() < 10) {
                break;
            }
            i++;
        }
	}
	
	@Test
	public void testDao() {
	    SqlBean sql = new SqlBean();
	    sql.and("S_WF_INST", "1111");
	    System.out.println(ServDao.delete("SY_WFE_TEST", sql));
	}
}