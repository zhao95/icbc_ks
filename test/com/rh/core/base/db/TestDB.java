package com.rh.core.base.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.Lang;

public class TestDB extends TestEnv {

    @Test
    public void testgetTableDDL() {
        String tableCode = "SY_BASE_USER_V";
        TableBean tableBean = Transaction.getExecutor().getDBTable(tableCode);
        System.out.print(Transaction.getBuilder().getDBTableDDL(tableBean));
    }
    
    
    @Test
    public void testInsert() {
        Bean dataBean = new Bean();
        List<List<Object>> params = new ArrayList<List<Object>>(10000);
        String psql = null;
        for(int i = 0; i < 10000; i++) {
            //dataBean.remove(SERV_DEF.$SERV_VALUES);
            dataBean.set("TEST_ID", Lang.getUUID());
            dataBean.set("TEST_NAME", "name" + i);
            dataBean.set("TEST_FLAG", "1");
            dataBean.set("S_MTIME", DateUtils.getDatetimeTS());
            psql = Context.getBuilder().insertByBean("SY_TEST", dataBean);
            params.add(dataBean.getList(Constant.PARAM_PRE_VALUES));
        }
        System.out.println(Context.getExecutor().executeBatch(psql, params)); //批量插入列表数据
        System.out.println(Context.getExecutor().count("select count(TEST_ID) from SY_TEST"));
    }
    
    @Test
    public void testQuery() {
        String sql;
        sql = "select * from SY_TEST where TEST_FLAG=1";
        Context.getExecutor().count(sql);
        sql = "select TEST_ID,TEST_NAME,TEST_MEMO from SY_TEST where TEST_FLAG=1";
        Context.getExecutor().count(sql);
        sql = "select * from SY_TEST where TEST_FLAG=?";
        Context.getExecutor().count(sql, Lang.asList(1));
        sql = "select TEST_ID,TEST_NAME,TEST_MEMO from SY_TEST where TEST_FLAG=?";
        Context.getExecutor().count(sql, Lang.asList(1));
        sql = "select * from SY_TEST where TEST_FLAG=?";
        for (int i = 0; i < 1; i++) {
            Context.getExecutor().queryPage(sql, 100 + i, 200, Lang.asList(1), null);
        }
    }
    
    @Test
    public void testTableSpace() {
        String sql;
        sql = "select * from user_tables where tablespace_name='FIREFLY'";
        List<Bean> dataList;
        dataList = Context.getExecutor().query(sql);
        for (Bean data : dataList) {
            System.out.println("alter table " + data.getStr("TABLE_NAME") + " move tablespace RHOA;");
        }
        sql = "select * from user_indexes where tablespace_name='FIREFLY'";
        dataList = Context.getExecutor().query(sql);
        for (Bean data : dataList) {
            System.out.println("alter index " + data.getStr("INDEX_NAME") + " rebuild tablespace RHOA;");
        }
    }
    
    @Test
    public void testCount() {
        String sql;
        sql = "select  distinct ENTITY_ID,ENTITY_CODE,TITLE,S_EMERGENCY,SERV_ID,DATA_ID,SERV_NAME,SEND_TIME  from SY_COMM_ENTITY_SEND_DTL_V t where 1=1 and SEND_USER = '4028804e1768dadc011768daf40f0037'  order by SEND_TIME desc";
        Context.getExecutor().count(sql);
    }
}
