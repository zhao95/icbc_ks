package com.rh.core.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.util.freemarker.FreeMarkerUtils;

/**
 * @author chensheng
 *
 */
public class TestFreeMarkerUtils extends TestEnv {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testParseString() {
        String ftlContent = "欢迎：${user}！${greet} ${person.addr} <#list person?keys as key>${key}=${person[key]}<br /></#list>";
        Map root = new HashMap();  
        root.put("user", "陈胜");
        root.put("greet", "你好");
        Map<String, String> data = new HashMap<String, String>();
        data.put("name", "cs");
        data.put("addr", "安定门");
        root.put("person", data);
        System.out.println(FreeMarkerUtils.parseString(ftlContent, root));
        
        String ftl = "Hello, ${user2}";
        Map root2 = new HashMap();  
        root2.put("user2", "陈胜");
        System.out.println(FreeMarkerUtils.parseString(ftl, root2));
    }

}
