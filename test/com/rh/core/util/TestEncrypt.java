/**
 * 
 */
package com.rh.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.util.encoder.Base64;
import com.rh.core.util.encoder.UrlBase64;
import com.rh.core.util.encrypt.DesUtils;
import com.rh.core.util.encrypt.KeyGenerator;

/**
 * @author Administrator
 *
 */
public class TestEncrypt extends TestEnv {
    
    @Test
    public void testMD5() {
        String input = "123456";
        String output = EncryptUtils.encrypt(input, "MD5");
        System.out.println(output);
        assertEquals("e10adc3949ba59abbe56e057f20f883e", output);
    }
    
	@Test
	public void test() {
		String input = "测试一下";
		String output = EncryptUtils.encrypt(input, "DES");
		System.out.println(output);
		output = EncryptUtils.decrypt(output, "DES");
		System.out.println(output);
		assertEquals(input, output);
	}
	
	@Test
	public void testDes() {
	    String input = "测试一下";
	    String output = DesUtils.encrypt(input);
        System.out.println(output);
        output = DesUtils.decrypt(output);
        System.out.println(output);
        assertEquals(input, output);
	}
	
	@Test
	public void testBase64() {
        String input = "测试一下";
        String output = new String(Base64.encode(input.getBytes()));
        System.out.println(output);
        output = new String(Base64.decode(output.getBytes()));
        System.out.println(output);
        assertEquals(input, output);
	}
	@Test
	public void testUrlBase64() {
	    String input = "测试一下";
	    String output = new String(UrlBase64.encode(input.getBytes()));
	    System.out.println(output);
	    output = new String(UrlBase64.decode(output.getBytes()));
	    System.out.println(output);
	    assertEquals(input, output);
	}
	
    @Test
    public void testSunBase64() {
        String input = "测试一下";
        String output = new sun.misc.BASE64Encoder().encode(input.getBytes());
        System.out.println(output);
        try {
            output = new String(new sun.misc.BASE64Decoder().decodeBuffer(output));
            System.out.println(output);
            assertEquals(input, output);
        } catch (Exception e) {
            
        }
    }
    
    @Test
    public void testSign() {
        KeyGenerator key = new KeyGenerator();
        String in = Lang.getMAC();
        byte[] out = KeyGenerator.sign(key.getPriKey(), in);
        System.out.println(out);
        System.out.println(key.getPriKey());
        System.out.println(key.getPubKey());
        assertTrue(KeyGenerator.verify(key.getPubKey(), in, out));
        assertFalse(KeyGenerator.verify(key.getPubKey(), "this is a test ddddd, 你好", out));
    }
    
    @Test
    public void testSystem() {
        System.out.println(Lang.getMAC());
    }
    
    @Test
    public void resetPassword() {
        // 重置用户密码以及加密方式
        // MD5
        // update sy_org_user a set a.USER_PASSWORD='e10adc3949ba59abbe56e057f20f883e';
        // update sy_comm_config b set b.CONF_VALUE='MD5' where b.CONF_KEY='SY_USER_PASSWORD_ENCRYPT';
        
        // DES
        // update sy_org_user a set a.USER_PASSWORD='033a4983bad57092';
        // update sy_comm_config b set b.CONF_VALUE='DES' where b.CONF_KEY='SY_USER_PASSWORD_ENCRYPT';
        
        String userLoginName = "admin";
        UserBean userBean = UserMgr.getUserByLoginName(userLoginName, "zhbx");
        System.out.println(EncryptUtils.decrypt(userBean.getPassword(), "DES"));
    }

}
