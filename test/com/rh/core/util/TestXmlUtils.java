/**
 * 
 */
package com.rh.core.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;

/**
 * @author Jerry Li
 *
 */
public class TestXmlUtils extends TestEnv {

    /** log */
    private static Log log = LogFactory.getLog(TestXmlUtils.class);
    @Test
    public void testToBean() {
        ParamBean paramBean = new ParamBean();
        paramBean.setId("SY_SERV").setLinkFlag(true);
        OutBean bean = ServMgr.act("SY_SERV", "byid", paramBean);
        String xml = XmlUtils.toFullXml(bean);
        try {
            File xmlFile = new File("d:/test.xml");
            FileWriter fw = new FileWriter(xmlFile);
            fw.write(xml);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug(xml);
        Bean xmlBean = XmlUtils.toBean(xml);
        List<Bean> itemList = xmlBean.getList("SY_SERV_ITEM");
        for (Bean item : itemList) {
            log.debug(item.getStr("ITEM_CODE"));
        }
//        try {
//            File xmlFile = new File("d:/test.json");
//            FileWriter fw = new FileWriter(xmlFile);
//            fw.write(JsonUtils.toJson(bean));
//            fw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
