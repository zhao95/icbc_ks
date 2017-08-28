package com.rh.core.wfe;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;
import com.rh.core.wfe.def.WFParser;

public class TestWfParseV2 extends TestEnv {
	@Test
	public void testWfParser() {
		String cmpyId = "ruaho";
		String enName = "procCodeV2";
		String procName = "procNameV2";
		int wfType = 1;  // ?
		String wfXmlStr = "";
		String procMemo = "";
		
		File wfXmlFile = new File("D:\\dev\\firefly\\docs\\newWf.xml");
		try {
			wfXmlStr = FileUtils.readFileToString(wfXmlFile,"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Bean procBean = new Bean();
		procBean.set("PROC_CODE", enName + "@" + cmpyId);
		procBean.set("EN_NAME", enName);
		procBean.set("PROC_NAME", procName);
		procBean.set("PROC_TYPE", wfType);
		procBean.set("PROC_MEMO", procMemo);
		procBean.set("BIND_TITLE", procMemo);
		procBean.set("SERV_ID", procMemo);
		procBean.set("S_CMPY", cmpyId);
		procBean.set("S_PUBLIC", procMemo);
		procBean.set("BIND_TITLE", procMemo);
		procBean.set("BIND_BUTTONS", procMemo);
		procBean.set("BIND_BUTTONS_NAME", procMemo);
		procBean.set("BIND_ENDEDITFIELD", procMemo);

		
		WFParser myParser = new WFParser(cmpyId, procBean);

		// 保存定义文件
		myParser.setDefContent(wfXmlStr);
		
		myParser.modify();
	}
	
}
