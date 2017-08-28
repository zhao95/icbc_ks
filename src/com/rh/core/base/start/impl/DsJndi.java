package com.rh.core.base.start.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.sql.DataSource;

import com.rh.core.base.Bean;
import com.rh.core.base.BaseContext;
import com.rh.core.base.BaseContext.DB_TYPE;
import com.rh.core.base.BaseContext.DS;



/**
 * 数据源JNDI管理类，处理数据源的初始化和关闭。
 * 
 * @author Jerry Li
 * @version $Id$
 */
public class DsJndi {
	
	/**
	 * 初始化数据库连接池，支持多数据源。
	 */
	public void start() {
		try {
    		//获取数据源，支持多数据源
		    String jndiPrefix = BaseContext.app(BaseContext.SYS_PARAM_JNDI_PREFIX, "");
		    javax.naming.Context env;
		    if (jndiPrefix.length() > 0) {
		        env = (javax.naming.Context) new InitialContext().lookup(jndiPrefix);
		        jndiPrefix += "/";
		    } else {
		        env = (javax.naming.Context) new InitialContext();
		    }
    		String dsPrefix = BaseContext.app(BaseContext.SYS_PARAM_DATASOURCE_PREFIX, "jdbc");
    		NamingEnumeration<NameClassPair> namEnumList = env.list(dsPrefix);
    		String prefix = dsPrefix + "/";
    		int i = 0;
    		while (namEnumList.hasMore()) {
    		    NameClassPair bnd = namEnumList.next();
    		    String name = prefix + bnd.getName();
        		try {
        		    DataSource ds = (DataSource) env.lookup(name);
        		    Connection conn = ds.getConnection();
        		    DatabaseMetaData dbmd = conn.getMetaData();
        		    String url = dbmd.getURL();
        		    String userName = dbmd.getUserName();
        		  //如果设置了缺省数据源，则直接使用，或者如果没有缺省数据源，设置第一个为缺省数据源
        		    boolean isDefaultDs = name.equalsIgnoreCase(prefix + "default") || i == 0;
        		    
        		    String fullName = jndiPrefix + name;
        		    BaseContext.addDataSource(fullName, name, url, userName, isDefaultDs, ds, null, null);
                    dbmd = null;
                    conn.close();
                    i++;
                    System.out.println("dsName(JNDI):" + name + " Url=" + url + "(" + userName + ") is OK!");
    		    } catch (Exception e) {
    		        System.out.println("dsName(JNDI):" + name + " ERROR! " + e.getMessage());
    		        e.printStackTrace(System.out);
    		    }
    		}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	
	/**
	 * 关闭连接池
	 */
	public void stop() {
		
	}
}