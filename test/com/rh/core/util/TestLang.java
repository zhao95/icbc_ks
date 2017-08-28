package com.rh.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;
import com.rh.core.base.BeanUtils;
import com.rh.core.util.var.VarMgr;

public class TestLang extends TestEnv {
    /** log */
    private static Log log = LogFactory.getLog(TestLang.class);

    
    @Test
    public void testIsTrueScirpt() {
        String src= "111==111";
        assertTrue(Lang.isTrueScript(src));
        src = "'1111'=='1111'";
        assertTrue(Lang.isTrueScript(src));
        src = "a='1111'; a.length == 4";
        assertTrue(Lang.isTrueScript(src));
        src = "234==1111";
        assertFalse(Lang.isTrueScript(src));
    }
    
    @Test
    public void testBooleanScirptByClass() {
        Bean data = new Bean();
        data.set("USER_ID", "liyanwei");
        String script = "com.rh.core.org.mgr.UserMgr.existInRoles('#USER_ID#','R510')";
        assertTrue(Lang.isTrueScript(BeanUtils.replaceValues(script, data)));
    }
    
    @Test
    public void testBooleanScirpt() {
        String script = "a=false;if (#ATTACH_SPECIAL#==1 && '#ATTACH_FILE_NAME#'.indexOf('.doc') > 0) {"
                + " a = true;"
                + "} else if (#ATTACH_SPECIAL#==6) {"
                + "a = true;"
                + "}";
        Bean data = new Bean();
        //测试普通正文
        data.set("ATTACH_SPECIAL", 1);
        data.set("ATTACH_FILE_NAME", "你好这是一个测试.doc");
        String src = BeanUtils.replaceValues(script, data);
        assertTrue(Lang.isTrueScript(src));
        //测试红头正文
        data.set("ATTACH_SPECIAL", 6);
        data.set("ATTACH_FILE_NAME", "你好这是一个测试.txt");
        src = BeanUtils.replaceValues(script, data);
        assertTrue(Lang.isTrueScript(src));
        //测试书生正文
        data.set("ATTACH_SPECIAL", 1);
        data.set("ATTACH_FILE_NAME", "你好这是一个测试.gd");
        src = BeanUtils.replaceValues(script, data);
        assertFalse(Lang.isTrueScript(src));
        //测试附件
        data.set("ATTACH_SPECIAL", 2);
        data.set("ATTACH_FILE_NAME", "你好这是一个测试.doc");
        src = BeanUtils.replaceValues(script, data);
        assertFalse(Lang.isTrueScript(src));
    }
    
    @Test
    public void testSplit() {
        String test = "你好,这是一个测试 不错 小朋友,haha ";
        String[] tests = test.split("[ |,]");
        for (String t : tests) {
            log.debug(t);
        }
    }
    
    @Test
    public void testEncodeUrl() {
        String test= "/SY_ADDRESS_LIST.xdoc?_xdataurl=";
//        String test2 = "javascript:var opts={'sId':'SY_ADDRESS_LIST','sName':'#USER_NAME#','pkCode':'#USER_CODE#','act':'xdoc'};openNewLinkPage(opts);";
        String testUrl = "json:http://localhost:8080/SY_ADDRESS_LIST.byid.do?_PK_=liyanwei&_LINK_=true&_format=web";
        log.debug(test + RequestUtils.encodeStr(testUrl));
    }
    
    @Test
    public void testDeCodeUrl() {
        String url = "json%3Ahttp%3A%2F%2F172.16.0.154%3A8080%2FTBL_MSV_QIANBAO.byid.do%3F_PK_%3D10035666%26_LINK_%3Dtrue";
        log.debug(RequestUtils.decodeStr(url));
    }
    
    @Test
    public void testReplace() {
        String select1 = "ABC,DEF,ZZZ";
        String select2 = "DEF,ZZZ,  ABC";
        String select3 = "DEF,ABC,ZZZ";
        String select4 = "DEF,abc,ZZZ";
        log.debug(select1.replaceAll("ABC" + ",?", ""));
        log.debug(select2.replaceAll(",\\s*" + "ABC", ""));
        log.debug(select3.replaceAll("ABC" + ",?", ""));
        log.debug(select4.replaceAll("ABC" + ",?", ""));
    }
    
    @Test
    public void testReplace1() {
        String select1 = "select SERV_ID, SERV_NAME,  SERV_TYPE__NAME,SERV_FLAG";
        log.debug(select1.replaceAll(",\\s*\\w*__NAME", ""));
    }
    
    @Test
    public void testMatch() {
        String select1 = "<ABC,DEF,ZZZ>";
        String select2 = "DEF,ZZZ,  ABC\"";
        String select3 = "DEF,ABC,ZZZ'";
        String pn = ".*[<>&'\"]+.*";
        assertTrue(select1.matches(pn));
        assertTrue(select2.matches(pn));
        assertFalse(select3.matches(pn));
    }
    
    @Test
    public void testBeanMath() {
        String src = "dsdfsdf#你好#-#TEST#-#TEST_ID#sdfsd";
        Bean data = new Bean();
        data.set("你好", "张三丰").set("TEST_ID", "李四光").set("TEST", "xxxx");
        log.debug(BeanUtils.replaceValues(src, data));
    }
    
    @Test
    public void testSubString() {
        String script = "'111'=111';'222'='333'";
        int pos = script.indexOf(";"); //如果设定了显示表达式，忽略掉显示表达式
        if (pos > 0) {
            script = script.substring(pos + 1);
        }
        System.out.println(script);
    }
    @Test
    public void testSysVas() {
        System.out.println("and userCode='@ABC_'".matches(".*@(\\w)+@.*"));
        System.out.println("and userCode='@ABC_@'".matches(".*@(\\w)+@.*"));
        System.out.println("and userCode='@@ and def=@@'".matches(".*@(\\w)+@.*"));
        System.out.println("@dddd_ddd@'".matches(".*@(\\w)+@.*"));
    }
    
    @Test
    public void testReplaceSysVar() {
        System.out.println(VarMgr.replaceSysVar("@DEPT_NAME@ and ergoli@126.com userCode='@USER_NAME@'"));
        System.out.println(VarMgr.replaceSysVar("and userCode='@DATE@'"));
        System.out.println(VarMgr.replaceSysVar("and userCode='@C_SY_HUB_SEARCH@'"));
    }
    
    @Test
    public void testArrayList() {
        List<Bean> list = new ArrayList<Bean>(100);
        for (int i = 0; i < 9; i++) {
            list.add(new Bean(String.valueOf(i)));
        }
        System.out.println(list.size());
        for (Bean data : list) {
            System.out.println(data);
        }
    }
    
    @Test
    public void testStrMatch() {
        String keyField = "SERV_ID";
        String pn = "\\s*((?i)" + keyField + "|distinct).*";
        System.out.println("distinct ABC_CODE, dddd".matches(pn));
        System.out.println("DISTINCT ABC_CODE, dddd".matches(pn));
        System.out.println("DISTI, SERV_ID, SERV_NAME".matches(pn));
        System.out.println("serv_id, SERV_NAME".matches(pn));
    }
    
    @Test
    public void testNumber() {
        Bean bean = new Bean();
        bean.set("ABC", 1).set("DEF", "");
        System.out.println(bean.get("ABC") instanceof Number);
        System.out.println(bean.get("DEF") instanceof Number);
    }
    
    @Test
    public void testCalendar() {
        Calendar calendar = DateUtils.getCalendar("2012-12-12");
        System.out.println(calendar.get(Calendar.DATE));
        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
        System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
    }
    
    @Test
    public void testMysqlURL() {
        String url = "jdbc:mysql://localhost:3306/rhcore";
        Pattern pattern = Pattern.compile(".*/(\\w+)\\??.*"); //不区分大小写
        Matcher mt = pattern.matcher(url);
        if (mt.find()) {
            System.out.println(mt.group(1));
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testNull() {
        Bean data = new Bean();
        System.out.println((List<Object>) data.get("TEST"));
    }
    
    @Test
    public void testReplaceDot() {
        System.out.println("SY_TEST.byid.do?data={.byid.do..}".replaceAll("\\.byid\\.do", "\\.card\\.do"));
    }
    
    @Test
    public void testMatch1() {
        String pn = "\\s*(--|commit;).*";
        System.out.println("--test".matches(pn));
        System.out.println("  --test".matches(pn));
        System.out.println("commit;".matches(pn));
        System.out.println("  select a,b,c from d--;".matches(pn));
    }
    
    @Test
    public void testPinyin() {
        System.out.println(Lang.arrayJoin(PinyinHelper.toHanyuPinyinStringArray("单让".toCharArray()[0])));
    }
    
    @Test
    public void testReplace2() {
        String value = "dd>d<dd<script>ddddsss";
        System.out.println(value.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
    }
 }
