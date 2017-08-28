package com.rh.core.wfe;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;
import com.rh.core.serv.ServDao;
import com.rh.core.wfe.def.MigrateProcData;

public class TestWfProcDef extends TestEnv {
	/** log */
	private static Log log = LogFactory.getLog(TestWfParser.class);

	@Test
	public void testMigrateWfe() {
		List<Bean> wfs = ServDao.finds("SY_WFE_PROC_DEF", new Bean());
        MigrateProcData proc = new MigrateProcData();
		for (Bean wfBean : wfs) {
		    try {
		        proc.migrate(wfBean.getStr("S_CMPY"), wfBean.getStr("PROC_CODE"));
		    } catch (Exception e) {
		        log.error(e.getMessage() + " " + wfBean.getStr("S_CMPY") + " " + wfBean.getStr("PROC_CODE"), e);
		    }
		}
	}
}
