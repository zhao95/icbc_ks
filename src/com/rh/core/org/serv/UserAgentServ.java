package com.rh.core.org.serv;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.Strings;

/**
 * 用户委托办理服务类
 * 
 * @author liyanwei
 * 
 */
public class UserAgentServ extends CommonServ {

    /** 委托状态：1 进行中 */
    public static final int AGT_STATUS_RUNNING = 1;
    /** 委托状态：2 已结束 */
    public static final int AGT_STATUS_DONE = 2;
    
    /** 增减标志：add 增加 */
    private static final String RECURSIVE_TYPE_ADD = "add";
    /** 增减标志：delete 减少 */
    private static final String RECURSIVE_TYPE_DEL = "delete";
    
    /**
     * 删除之前的拦截方法，由子类重载
     * @param paramBean 参数信息
     */
    protected void beforeDelete(ParamBean paramBean) {
        List<Bean> dataList = paramBean.getDeleteDatas();
        for (Bean dataBean : dataList) {
            //清除委托人设定
            if (dataBean.getInt("AGT_STATUS") == AGT_STATUS_RUNNING) {
                clearUserAgentState(dataBean.getStr("USER_CODE"), dataBean.getStr("TO_USER_CODE"));
            }
        }
    }
    
    /**
     * 保存之前的操作
     * @param paramBean 传入的参数Bean
     */
    @Override
    protected void beforeSave(ParamBean paramBean) {
        String servId = paramBean.getServId();
        Bean oldBean = paramBean.getSaveOldData(); //原始数据
        Bean userAgent = paramBean.getSaveFullData(); //更新后全数据
        if (userAgent.getStr("USER_CODE").equals(userAgent.getStr("TO_USER_CODE"))) {
            throw new TipException(Context.getSyMsg("SY_ORG_USER_AGT_SELF_ERROR")); //不能设置自己为受委托人
        }
        if (paramBean.getId().length() == 0) { //新增模式，要判断是否有未结束的工作委托
            SqlBean sql = new SqlBean().and("USER_CODE", paramBean.getStr("USER_CODE"))
                    .and("AGT_STATUS", AGT_STATUS_RUNNING);
            if (ServDao.count(servId, sql) > 0) { //如果当前用户存在未结束的的工作，提示错误
                throw new TipException(Context.getSyMsg("SY_ORG_USER_AGT_EXIST"));
            }
            addUserAgentState(paramBean.getStr("USER_CODE"), paramBean.getStr("TO_USER_CODE"));
        } else if (paramBean.contains("TO_USER_CODE") 
                && (oldBean.getInt("AGT_STATUS") == AGT_STATUS_RUNNING)) { //运行状态下修改了被委托人
            //清除上一个委托人设定
            clearUserAgentState(userAgent.getStr("USER_CODE"), oldBean.getStr("TO_USER_CODE"));
            //添加新的委托人设定
            addUserAgentState(userAgent.getStr("USER_CODE"), paramBean.getStr("TO_USER_CODE"));
        }
    }
    
    /**
     * 启动委托
     * @param paramBean 参数信息，要求有USER_CODE参数
     * @return 结果
     */
    public OutBean startAgent(ParamBean paramBean) {
        String servId = paramBean.getServId();
        OutBean outBean = new OutBean();
        if (paramBean.getId().length() > 0) {
            String userCode = paramBean.getStr("USER_CODE");
            String toUserCode = paramBean.getStr("TO_USER_CODE");
            paramBean.set("AGT_STATUS", AGT_STATUS_RUNNING).set("AGT_BEGIN_DATE", DateUtils.getDatetime())
                .set("AGT_END_DATE", "");
            ServDao.update(servId, paramBean);
            addUserAgentState(userCode, toUserCode);
        }
        outBean.setOk();
        return outBean;
    }
    
    /**
     * 结束委托
     * @param paramBean 参数信息，要求有USER_CODE参数
     * @return 结果
     */
    public OutBean endAgent(ParamBean paramBean) {
        String servId = paramBean.getServId();
        OutBean outBean = new OutBean();
        String userCode = paramBean.getStr("USER_CODE");
        if (userCode.length() > 0) {
            Bean param = new Bean();
            param.set("USER_CODE", userCode).set("AGT_STATUS", AGT_STATUS_RUNNING);
            Bean userAgent = ServDao.find(servId, param);
            String toUserCode = userAgent.getStr("TO_USER_CODE");
            userAgent.set("AGT_STATUS", AGT_STATUS_DONE).set("AGT_END_DATE", DateUtils.getDatetime());
            ServDao.update(servId, userAgent);
            clearUserAgentState(userCode, toUserCode);
        }
        outBean.setOk();
        return outBean;
    }
    
    /**
     * 添加用户委托信息
     * @param userCode 委托人编码
     * @param toUserCode 被委托人编码
     */
    private void addUserAgentState(String userCode, String toUserCode) {
        //更新委托人状态
        Bean userState = new Bean();
        userState.set("USER_CODE", userCode);
        userState.set("USER_AGT_FLAG", Constant.YES); //设置代理开始
        UserMgr.saveUserState(userState); //更新
        userState = UserMgr.getUserState(userCode); //获取最新的用户状态信息
        //更新被委托人状态
        String subCodes = userState.getStr("SUB_CODES");
        if (Strings.containsValue(subCodes, toUserCode)) { //不允许被委托人包含在已委托列表中
            throw new TipException(Context.getSyMsg("SY_ORG_USER_AGT_SELF_ERROR"));
        }
        //增加被委托人的代理设定的用户编码
        subCodes = Strings.addValue(subCodes, userCode);
        recursiveUserAgent(toUserCode, subCodes, RECURSIVE_TYPE_ADD);
    }
    
    /**
     * 清除用户委托状态
     * @param userCode 委托人编码
     * @param toUserCode 被委托人编码
     */
    private void clearUserAgentState(String userCode, String toUserCode) {
        //更新委托人状态
        Bean userState = new Bean();
        userState.set("USER_CODE", userCode);
        userState.set("USER_AGT_FLAG", Constant.NO); //设置代理结束
        UserMgr.saveUserState(userState); //更新并获取最新的用户状态信息
        userState = UserMgr.getUserState(userCode); //获取最新的用户状态信息
        //更新被委托人状态，去掉委托人的用户编码
        Bean toUserState = UserMgr.getUserState(toUserCode);
        if (toUserState != null) {
            String subCodes = userState.getStr("SUB_CODES");
            subCodes = Strings.addValue(subCodes, userCode);
            recursiveUserAgent(toUserCode, subCodes, RECURSIVE_TYPE_DEL);
        }
    }
    
    /**
     * 递归增减用户代理编码，确保递归调用多级委托
     * @param userCode 当前用户编码
     * @param addCode 需要被增减的用户编码
     * @param type 增减标志，add：增加；delete：减少
     */
    private void recursiveUserAgent(String userCode, String addCode, String type) {
        Bean userState = UserMgr.getUserState(userCode);
        Bean data = new Bean();
        data.set("USER_CODE", userCode);
        String subCodes;
        if (userState != null) {
            subCodes = userState.getStr("SUB_CODES");
        } else {
            subCodes = "";
        }
        if (type.equals(RECURSIVE_TYPE_ADD)) { //添加
            data.set("SUB_CODES",  Strings.addValue(subCodes, addCode));
        } else { //删除
            data.set("SUB_CODES",  Strings.removeValue(subCodes, addCode));
        }
        UserMgr.saveUserState(data); //更新用户状态
        //当前用户也处于委托代理状态，递归处理增减用户编码
        if ((userState != null) && (userState.getInt("USER_AGT_FLAG") == Constant.YES_INT)) {
            Bean param = new Bean();
            param.set("USER_CODE", userCode).set("AGT_STATUS", AGT_STATUS_RUNNING);
            Bean userAgent = ServDao.find(ServMgr.SY_ORG_USER_AGENT, param);
            if (userAgent != null) {
                recursiveUserAgent(userAgent.getStr("TO_USER_CODE"), addCode, type);
            }
        }
    }
    
    
    /**
     * 查看是否存在委托
     * @param paramBean 参数
     * @return 返回结果集
     */
    public OutBean isExiteAnent(ParamBean paramBean) {
        SqlBean sql = new SqlBean();
        String userCodes = paramBean.getStr("USER_CODES"); //被委托人编码
        if (StringUtils.isBlank(userCodes)) {
            return new OutBean().setError("被送交用户编码不存在！");
        }
        sql.andIn("USER_CODE", userCodes).and("AGT_STATUS", Constant.YES).and("S_FLAG", Constant.YES)
           .and("S_CMPY", Context.getCmpy());
        sql.selects("AGT_END_DATE, REAL_END_DATE, USER_CODE");
        //查询出委托信息
        List<Bean> agentList = ServDao.finds(ServMgr.SY_ORG_USER_TYPE_AGENT, sql);
        StringBuffer agentNames = new StringBuffer(); //委托用户姓名
        for (Bean b : agentList) {
            String userName = DictMgr.getFullName(ServMgr.SY_ORG_USER, b.getStr("USER_CODE"));
            String agentName = "";
            //查看实际结束时间
            String realDate = b.getStr("REAL_END_DATE");
            //实际结束时间不存在，则获取结束时间
            if (StringUtils.isBlank(realDate)) {
                realDate = b.getStr("AGT_END_DATE");
            }
            if (realDate.length() <= 10) {
                realDate = realDate + " 23:59:59";
            }
            //获取最终结束时间date对象
            Date realDateObj = DateUtils.getDateFromString(realDate);
            //如果委托任务当前时间结束
            if (realDateObj.getTime() > new Date().getTime()) {
                //查找委托任务
                agentName = getAgentUserName(b.getId(), getAgtType(paramBean.getStr("servId")));
                agentNames.append(",").append(userName + "[委托" + agentName + "处理]");
            } else {
                agentNames.append(",").append(userName);
            }
        }
        String agentNamesStr = agentNames.toString();
        if (agentNamesStr.startsWith(",")) {
            agentNamesStr = agentNamesStr.substring(1, agentNamesStr.length());
        }
        return new OutBean().set("USER_NAMES", agentNamesStr);
    }
    
    /**
     * 查找委托用户名
     * @param agtId 委托类型id
     * @param agtType 业务类型
     * @return 委托用户名
     */
    private String getAgentUserName(String agtId, String agtType) {
        SqlBean sql = new SqlBean();
        sql.and("AGT_ID", agtId).and("S_FLAG", Constant.YES).and("S_CMPY", Context.getCmpy())
           .and("AGT_STATUS", Constant.YES);
        sql.selects("TO_USER_CODE, AGT_TYPE_CODE");
        //委托人姓名
        StringBuffer agentNames = new StringBuffer("(");
        //查询出委托信息
        List<Bean> agentList = ServDao.finds(ServMgr.SY_ORG_USER_TYPE_AGENT_FROM, sql);
        for (Bean b : agentList) {
            //如果存在此业务委托
            if (b.getStr("AGT_TYPE_CODE").equals(agtType) || b.getStr("AGT_TYPE_CODE").equals("_ALL_")) {
                agentNames.append(DictMgr.getFullName(ServMgr.SY_ORG_USER, b.getStr("TO_USER_CODE"))).append(",");
            }
        }
        String agentNamesStr = agentNames.toString();
        if (agentNamesStr.split(Constant.SEPARATOR).length > 1) {
            return agentNamesStr.substring(0, agentNamesStr.length() - 1) + ")";
        } else if (agentNamesStr.equals("(")) {
            return "";
        }
        return agentNamesStr.substring(1, agentNamesStr.length() - 1);
    }
    
    /**
     * 获取AGT_TYPE
     * @param servId 当前审批单ID
     * @return TYPE
     */
    private String getAgtType(String servId) {
        //发文
        SqlBean sql = new SqlBean();
        sql.andLike("AGT_COND", servId).and("S_FLAG", Constant.YES).and("S_CMPY", "zhbx");
        sql.selects("AGT_TYPE_CODE");
        List<Bean> list = ServDao.finds(ServMgr.SY_ORG_USER_AGT_TYPE, sql);
        if (list.size() < 1) {
            return "_OTHER_";
        }
        return list.get(0).getStr("AGT_TYPE_CODE");
    }
}
