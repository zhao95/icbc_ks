package com.rh.core.wfe;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.rh.core.TestEnv;
//import com.rh.core.wfe.def.v1.WFParser;

public class TestWfParser {
	/** log */
	private static Log log = LogFactory.getLog(TestWfParser.class);
	
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
		TestEnv.stop();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testWfParser() {
		/*String cmpyId = "ruaho";
		String procCode = "procCode";
		String procName = "procName";
		int wfType = 1;  // ?
		String procMemo = "";
		
		String xmlFilePath = "D:\\dev\\firefly\\docs\\办公室发文.xml";
		
		WFParser myParser = new WFParser(xmlFilePath, cmpyId, procCode);

		myParser.setDescription(procMemo);
		myParser.setName(procName);

		// 加入分类
		myParser.setType(wfType);
        
		myParser.save();*/
	}
}
