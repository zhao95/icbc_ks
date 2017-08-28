package com.rh.core.comm;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.serv.util.ServUtils;

public class TestMenuMgr extends TestEnv {

    @Test
    public void test() {
        ServUtils.getServDef("SY_MENU");
    }

    @Test
    public void testMenuPath() {
        String userCode = "sdfsd3423423";
        System.out.println(MenuServ.getMenuFullPath(userCode));
        userCode = "0dfsd3423423";
        System.out.println(MenuServ.getMenuFullPath(userCode));
        userCode = "23fsd3423423";
        System.out.println(MenuServ.getMenuFullPath(userCode));
    }
}
