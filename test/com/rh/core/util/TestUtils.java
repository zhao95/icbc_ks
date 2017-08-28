package com.rh.core.util;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Context;

/**
 * @author Jerry Li
 *
 */
public class TestUtils extends TestEnv {

    @Test
    public void testHttpURL() {
        System.out.println(Context.getHttpUrl() + "====");
    }

}
