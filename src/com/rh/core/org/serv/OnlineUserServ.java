package com.rh.core.org.serv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.org.UserStateBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.bean.PageBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;

/**
 * 在线用户服务类
 * 
 * @author liyanwei
 * 
 */
public class OnlineUserServ extends CommonServ {

    /**
     * 列出在线用户列表
     * @param paramBean 参数信息
     * @return 输出信息
     */
    public OutBean query(ParamBean paramBean) {
        String servId = paramBean.getServId();
        ServDefBean servDef = ServUtils.getServDef(servId);
        OutBean outBean = new OutBean();
        
        PageBean pageBean = paramBean.getQueryPage();
        
        List<UserStateBean> list = OnlineUserMgr.getOnlineUserList(pageBean.getOffset(),
                pageBean.getShowNum());
        
        int size = OnlineUserMgr.getSize();
        List<Bean> rtnList = new ArrayList<Bean>(size);
        for (UserStateBean userState : list) {
            UserBean userBean = UserMgr.getUser(userState.getStr("USER_CODE"));
            if (userState.isEmpty("USER_LAST_IP")) { //IP为空不在在线用户中
                continue;
            }
            Bean user = new Bean(userState.getId()).set("SID", userState.getId());
            user.set("USER_NAME", userBean.getName()).set("DEPT_NAME", userBean.getDeptName())
                .set("TDEPT_NAME", userBean.getTDeptName()).set("ODEPT_NAME", userBean.getODeptName())
                .set("USER_LOGIN_NAME", userBean.getLoginName()).set("USER_CODE", userBean.getId())
                .set("USER_LAST_LOGIN", userState.getStr("USER_LAST_LOGIN"))
                .set("IP_ADDRESS", userState.getStr("USER_LAST_IP"))
                .set("ACT_CODE", userState.getStr("ACT_CODE")).set("DATA_ID",  userState.getStr("DATA_ID"))
                .set("SERV_ID", DictMgr.getFullName("SY_SERV", userState.getStr("SERV_ID")));
            rtnList.add(user);
        }
        outBean.setData(rtnList).setCols(servDef.getAllItems());
        
        //增加分页对象
        //PageBean pageBean = new PageBean();
        pageBean.setAllNum(size);
        outBean.setPage(pageBean);
        afterQuery(paramBean, outBean);
        return outBean;
    }
    
    @Override
    protected void afterQuery(ParamBean paramBean, OutBean outBean) {
            Map<String, String> map = getParams(paramBean.getStr("_searchWhere"));
            if (map == null || map.size() < 1) {
                return;
            }
            List<Bean> queryList = getQueryList(map, outBean.getDataList());
            outBean.setData(queryList);
            PageBean page = paramBean.getQueryPage();
            page.setAllNum(queryList.size());
            outBean.setPage(page);
    }
    
    private Map<String, String> getParams(String searchStr) {
        String userName = ""; //用户名
        String userLoginName = ""; //登录名
        String[] paramArray = searchStr.split("AND");
        //_searchWhere= and USER_NAME like '%aaa%'
        for (String s : paramArray) {
            if (s.indexOf("USER_NAME") > 0) {
                int index = s.indexOf("%");
                userName = s.substring(index + 1, s.length() - 2);
            }
            if (s.indexOf("USER_LOGIN_NAME") > 0) {
                int index = s.indexOf("%");
                userLoginName = s.substring(index + 1, s.length() - 2);
            }
        }
        Map<String, String> map = new HashMap<String, String>();
        if (!StringUtils.isBlank(userLoginName)) {
            map.put("USER_LOGIN_NAME", userLoginName);
        }
        if (!StringUtils.isBlank(userName)) {
            map.put("USER_NAME", userName);
        }
        return map;
    }
    
    /**
     * 通过过滤条件过滤数据
     * @param param 参数
     * @param list 数据
     * @return 过滤后的数据
     */
    private List<Bean> getQueryList(Map<String, String> param, List<Bean> list) {
        String userName = param.get("USER_NAME");
        String userLoginName = param.get("USER_LOGIN_NAME");
        List<Bean> newList = new ArrayList<Bean>();
        for (Bean b : list) {
            if (userName != null && b.getStr("USER_NAME").indexOf(userName) >= 0) {
                newList.add(b);
                continue;
            }
            if  (userLoginName != null && b.getStr("USER_LOGIN_NAME").indexOf(userLoginName) >= 0) {
                newList.add(b);
                continue;
            }
        }
        return newList;
    }
    
    /**
     * 强制选定的在线用户退出
     * @param paramBean 参数信息
     * @return 输出信息
     */
    public OutBean kickOff(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String[] ids = paramBean.getId().split(Constant.SEPARATOR);
        for (String id : ids) {
            Context.clearOnlineUser(id);  //清除在线用户
        }
        outBean.setOk();
        return outBean;
    }
}
