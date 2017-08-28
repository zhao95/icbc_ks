package com.rh.core.org;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;

public class TestOrg extends TestEnv{

    @Test
    public void testInitOrg() {
        //通过保存根部门处理所有部门的有效部门编码和机构部门编码
        ParamBean param = new ParamBean(ServMgr.SY_ORG_DEPT, ServMgr.ACT_SAVE);
        param.setId("8541");
        param.set("DEPT_TYPE", 1);
        ServMgr.act(param);
        
    }
    
    @Test
    public void testChar() {
        System.out.println("".toCharArray());
    }
}
