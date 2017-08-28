package com.rh.core.comm;

import org.junit.Test;

import com.rh.core.TestEnv;
import com.rh.core.base.Context;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.JsonUtils;

public class TestToDo extends TestEnv {

    @Test
    public void testGetTodo() {
        System.out.println(JsonUtils.toJson(ServMgr.act(new ParamBean(ServMgr.SY_COMM_TODO, "getTodo"))));
    }
    
    @Test
    public void testGetTodoCount() {
        Context.setThreadUser(UserMgr.getUserState("liyanwei"));
        System.out.println(JsonUtils.toJson(ServMgr.act(new ParamBean(ServMgr.SY_COMM_TODO, "getTodoCount"))));
        Context.setThreadUser(UserMgr.getUserState("admin"));
        System.out.println(JsonUtils.toJson(ServMgr.act(new ParamBean(ServMgr.SY_COMM_TODO, "getTodoCount"))));
        Context.setThreadUser(UserMgr.getUserState("liyanwei"));
        System.out.println(JsonUtils.toJson(ServMgr.act(new ParamBean(ServMgr.SY_COMM_TODO, "getTodoCount"))));
        System.out.println(JsonUtils.toJson(ServMgr.act(new ParamBean(ServMgr.SY_COMM_TODO, "getTodoCount"))));
        Context.setThreadUser(UserMgr.getUserState("admin"));
        System.out.println(JsonUtils.toJson(ServMgr.act(new ParamBean(ServMgr.SY_COMM_TODO, "getTodoCount"))));
    }

}
