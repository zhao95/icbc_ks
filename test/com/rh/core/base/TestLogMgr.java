/*
 * Copyright (c) 2011 Ruaho All rights reserved.
 */
package com.rh.core.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rh.core.base.BaseContext.APP;
import com.rh.core.base.start.LogMgr;

/**
 * log测试类
 * 
 * @author Jerry Li
 */
public class TestLogMgr {
	
	Log log = LogFactory.getLog(TestLogMgr.class);

	LogMgr logMgr;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Context.setApp(Context.SYS_PARAM_LOG, "log4j.properties");
		Context.setApp(APP.WEBINF, "D:\\DEV\\rh\\trunk\\firefly\\pro\\WEB-INF\\");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		logMgr = new LogMgr();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStart() {
		logMgr.start();
	}

	@Test
	public void testChangeLevel() {
		LogMgr.changeLevel("DEBUG");
		log.error("ERROR is OK");
		log.debug("DEBUG is OK");
		LogMgr.changeLevel("ERROR");
		log.error("ERROR is OK");
		log.debug("DEBUG is OK");
	}

	@Test
	public void testLoadAndWatch() {
		logMgr.loadAndWatch("D:\\DEV\\rh\\trunk\\firefly\\pro\\WEB-INF\\log4j.properties");
	}

	@Test
	public void testStop() {
		logMgr.stop();
	}
}
