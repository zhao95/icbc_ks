package com.rh.core.plug.im;
import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;
import com.rh.core.util.EncryptUtils;


public class TestIm extends TestEnv {

    @Test
    public void testSendNotify() {
       /* ImMgr.getIm().sendNotify("zhangjinwei", "这是一个测试，注意查收", 
                "你好，[腾讯|http://www.qq.com]");*/
        ImMgr.getIm().sendNotify("test1", "来自【系统管理员】的消息","[年假申请|http://10.10.11.124:8077/sy/comm/page/page.jsp?openTab=7b2775726c273a2753435f414e4e55414c5f4c454156455f4150504c592e636172642e646f3f706b436f64653d3339727976674e4f52345772694358414d5667386b63272c276d656e75466c6167273a342c277265706c61636555726c273a2753435f414e4e55414c5f4c454156455f4150504c592e627969642e646f3f646174613d7b5f504b5f3a3339727976674e4f52345772694358414d5667386b632c4e495f49443a33415771557a343574314570706135455259726279767d277d]  [SY_WFE_PROC_DEF toNext 10.10.106.128 06Frbcvq5bNWvw7z7aXpcJ]");
    }
    
    @Test
    public void testSendSms() {
        ImMgr.getIm().sendSms("yuananan", "liyanwei", "这是一个测试，注意查收,你好，[腾讯|http://www.qq.com]");
    }
    
    @Test
    public void testAddUser() {
        Bean bean = new Bean();
        bean.set("username", "tom");
        bean.set("nickname", "tom123");
        bean.set("pwd", "123456");
        bean.set("deptpath", "search");
        ImMgr.getIm().saveUser(bean);
    }
    
    @Test
    public void testShortUrl(){
        String url = "http://localhost:8081/sy/comm/page/page.jsp?openTab=7b27745469746c65273a27e5be85e58a9e2d2d2de5b9b4e58187e794b3e8afb72775726c273a2753435f414e4e55414c5f4c454156455f4150504c592e636172642e646f3f706b436f64653d3172687547466665546461767042517870596641704b7279272c276d656e75466c6167273a342c277265706c61636555726c273a2753435f414e4e55414c5f4c454156455f4150504c592e627969642e646f3f646174613d7b5f504b5f3a3172687547466665546461767042517870596641704b72792c47";
        String encodeUrl = EncryptUtils.encrypt(url, "MD5");
        
        System.out.println(encodeUrl);
    }
}
