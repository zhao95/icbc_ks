package com.rh.core.comm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Bean;

public class TestFileMgr extends TestEnv {
	
	/*
    @Test
    public void testUpload() {
    	InputStream is = null;
		try {
			is = new FileInputStream("/Users/well/Desktop/test.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Bean param = new Bean();
		param.set("FILE_NAME", "upload测试");
    	Bean out = FileMgr.upload(param, is);
    	System.out.println("---debug------filebean:" + out);
    }
    */
    
    @Test
    public void testClean() {
    	FileMgr.cleanFile("CC_COCHAT_FILE", "", 1965);
    }

}
