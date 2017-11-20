package com.rh.core.org.serv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import com.rh.core.base.BaseContext.APP;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.plug.im.ImMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.EncryptUtils;
import com.rh.core.util.UserName2PinyinUtils;

/**
 * 用户服务类
 * 
 * @author liyanwei
 * 
 */
public class UserServ extends CommonServ {

    /** 服务名称：工作兼岗 */
    public static final String JIANGANG_SERV = "SY_ORG_USER_JIANGANG";  
    /** 关联类型：工作兼岗 */
    public static final int RELATE_TYPE_JIANGANG = 1;  
    
    @Override
    protected void afterSave(ParamBean paramBean, OutBean outBean) {
        if (outBean.isOk()) {
            if (!paramBean.getAddFlag()) { //修改模式
                UserBean currUser = Context.getUserBean();
                if (currUser != null && paramBean.getId().equals(currUser.getCode())) { //如果操作者为当前人，则清除当前用户缓存
                    UserMgr.clearSelfUserCache();
                } else { //不是当前用户，清除该用户缓存
                    UserBean userBean = UserMgr.getCacheUser(paramBean.getId());
                    if (userBean != null) {
                        UserMgr.clearSelfUserCache(userBean);
                    }
                }
                    
                if (paramBean.contains("DEPT_CODE")) { //如果修改了部门，则清除用户的菜单缓存
                    UserMgr.clearMenuByUsers(paramBean.getId());
                }
            }
            if (Context.appBoolean(APP.IM)) { //启动了IM进行同步
                if (paramBean.contains("USER_PASSWORD_REAL")) {
                    outBean.set("USER_PASSWORD_REAL", paramBean.getStr("USER_PASSWORD_REAL")); //真实密码给接口用
                }
                ImMgr.getIm().saveUser(outBean);
            }
        }
        
        /** 处理兼岗用户 */
        if (!paramBean.containsKey("_JIANGANG_FLAG_")) {
            treatJiangangUser(paramBean, outBean);
        }
    }
    
    /**
     * 保存之前的操作
     * @param paramBean 传入的参数Bean
     */
    @Override
    protected void beforeSave(ParamBean paramBean) {

        // 去除姓名左右空格
        if (paramBean.isNotEmpty("USER_NAME")) {
            paramBean.set("USER_NAME", paramBean.getStr("USER_NAME").trim());
            //都给转成小写 为前段排序使用（防止输入以纯小写的英文字母开头，导致排序出错）
            paramBean.set("USER_SHORT_NAME", UserName2PinyinUtils.toPinyinHead(paramBean.getStr("USER_NAME")).toLowerCase());
            paramBean.set("USER_EN_NAME", UserName2PinyinUtils.toPinyin(paramBean.getStr("USER_NAME")));
        }
        
        // 去除登录名左右空格
        if (paramBean.isNotEmpty("USER_LOGIN_NAME")) {
//            System.out.println(paramBean.getStr("USER_LOGIN_NAME").trim());
            paramBean.set("USER_LOGIN_NAME", paramBean.getStr("USER_LOGIN_NAME").trim());
        }
        
        // 去除工号左右空格
        if (paramBean.isNotEmpty("USER_WORK_NUM")) {
            paramBean.set("USER_WORK_NUM", paramBean.getStr("USER_WORK_NUM").trim());
        }
        
        // 将邮箱转换成大写放入临时字段中
        if (paramBean.isNotEmpty("USER_EMAIL")) {
        	paramBean.set("USER_EDU_SCHOOL", paramBean.getStr("USER_EMAIL").trim().toUpperCase());
        }
        
        	if(paramBean.isNotEmpty("USER_TEMP_PASSWORD")){
        		//不启用用户临时登录密码
        			 paramBean.set("USER_TEMP_PASSWORD", 
        					 EncryptUtils.encrypt(paramBean.getStr("USER_TEMP_PASSWORD"), 
        							 Context.getSyConf("SY_USER_PASSWORD_ENCRYPT", EncryptUtils.DES)));
        			 
        			 paramBean.set("USER_PASSWORD_REAL", paramBean.getStr("USER_TEMP_PASSWORD")); //记录真实密码给接口用
        			 
                     SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                     Date date= new Date();
                     paramBean.set("USER_TEMP_PASSWORD_MADTIME",sim.format(date));
        	}
        	//if (!paramBean.containsKey("_JIANGANG_FLAG_")) {
        	if (paramBean.getAddFlag() && (paramBean.isEmpty("USER_PASSWORD"))) { //添加模式没有设密码则给缺省密码
                paramBean.set("USER_PASSWORD", Context.getSyConf("SY_USER_PASSWORD_INIT", "123456"));
            }
            if (paramBean.isNotEmpty("USER_PASSWORD")) {
                paramBean.set("USER_PASSWORD_REAL", paramBean.getStr("USER_PASSWORD")); //记录真实密码给接口用
                paramBean.set("USER_PASSWORD", 
                        EncryptUtils.encrypt(paramBean.getStr("USER_PASSWORD"), 
                                Context.getSyConf("SY_USER_PASSWORD_ENCRYPT", EncryptUtils.DES)));
            } else if (paramBean.contains("USER_PASSWORD")) {
                paramBean.remove("USER_PASSWORD");
            }
    }
    
    /**
     * 删除之前处理
     * @param paramBean 参数信息
     */
    protected void beforeDelete(ParamBean paramBean) {
        /** 处理兼岗用户 */
        if (!paramBean.containsKey("_JIANGANG_FLAG_")) {
            String[] users = paramBean.getId().split(",");
            for (String user : users) {
                if (!UserMgr.isMainUser(user)) {
                    throw new TipException("不能删除兼岗用户信息");
                }
            }
        }
    }
    
    /**
     * 删除之后更新IM处理
     * @param paramBean 参数信息
     * @param outBean 删除结果信息
     */
    protected void afterDelete(ParamBean paramBean, OutBean outBean) {
        if (outBean.isOk()) {
            String[] ids = outBean.getDeleteIds().split(Constant.SEPARATOR);
            for (String id : ids) {
                UserMgr.clearUserCache(id);
            }
            if (Context.appBoolean(APP.IM)) { //启动了IM进行同步
                ImMgr.getIm().deleteUser(outBean.getDeleteIds());
            }
            if (!paramBean.getDeleteDropFlag()) { //假删除修改登录名确保不会重复
                ParamBean modifyBean = new ParamBean();
                modifyBean.setServId(ServMgr.SY_ORG_USER_ALL);
                List<Bean> dataList = outBean.getDataList();
                for (Bean userBean : dataList) {
                    modifyBean.setAct(ServMgr.ACT_SAVE);
                    modifyBean.setId(userBean.getId());
                    // 非兼岗用户被删除的时候在登录名后面添加随机后缀
                    if (userBean.getInt("JIANGANG_FLAG") == Constant.NO_INT) {
                        String newLoginName = userBean.getStr("USER_LOGIN_NAME");
                        newLoginName += "@" + userBean.getId();
                            int size = 40;
                            if (newLoginName.length() > size) {
                                newLoginName = newLoginName.substring(0, size);
                            }
                            modifyBean.set("USER_LOGIN_NAME", newLoginName);
                            ServMgr.act(modifyBean);
                    }
                }
            }
        }
    }
    
    /**
     * 为用户创建汉语拼音
     * @param paramBean 参数Bean
     * @return 执行结果
     */
    public OutBean buildPinyin(ParamBean paramBean) {
        StringBuilder response = new StringBuilder();
        int userSuccessful = buildPinyin("SY_ORG_USER", "USER_NAME", 
                "USER_EN_NAME", "USER_SHORT_NAME");
        response.append("SY_ORG_USER ");
        response.append(" update rows:");
        response.append(userSuccessful);
        response.append("\n");
        int deptSuccessful = buildPinyin("SY_ORG_DEPT", "DEPT_NAME"
                , "DEPT_ENNAME", "DEPT_SHORT_ENNAME");
        response.append("SY_ORG_DEPT ");
        response.append(" update rows:");
        response.append(deptSuccessful);
        response.append("\n");
        return new OutBean().setOk(response.toString());
    }
    
    /**
     * 
     * @param servId
     * @param nameField
     * @param pinyinField
     * @param shortPinyinField
     * @return
     */
    private int buildPinyin(String servId,String nameField, String pinyinField, String shortPinyinField){
        System.out.println(" start build pinyin for service:" + servId);
        int successful = 0;
        ParamBean query = new ParamBean();
        int page = 1;
        query.set(Constant.PAGE_SHOWNUM, 5000);
        List<Bean> queryResult = null;
        do {
            query.set(Constant.PAGE_NOWPAGE, page++);
            queryResult = ServDao.finds(servId, query);
            if (null == queryResult) {
                queryResult = new ArrayList<Bean>();
            }
            List<Bean> batch = new ArrayList<Bean>();
            for (Bean user : queryResult) {
                if (user.getStr(pinyinField).length() > 0
                        && user.getStr(shortPinyinField).length() > 0) {
                    continue;
                }
                // update
                ParamBean updateQuery = new ParamBean();
                updateQuery.setId(user.getId());
                String username = user.getStr(nameField);
                String fullPinyin = UserName2PinyinUtils.toPinyin(username);
                String shortPinyin = UserName2PinyinUtils.toPinyinHead(username);
                updateQuery.set(pinyinField, fullPinyin);
                updateQuery.set(shortPinyinField, shortPinyin);
                batch.add(updateQuery);
                //ServDao.update("SY_ORG_USER", updateQuery);
                successful++;
            }
            ServDao.updates(servId, batch);
            batch.clear();
            System.out.println("update page:" + page);
        } while (queryResult.size() > 0);
        return successful;
    }

    
    /**
     * 兼岗：处理兼岗用户
     * @param paramBean 请求bean
     * @param outBean 结果bean
     */
    private void treatJiangangUser(ParamBean paramBean, OutBean outBean) {
        String mainUserCode = paramBean.getId();
        String compy_code = paramBean.getStr("CMPY_CODE");
        if (!UserMgr.isMainUser(mainUserCode)) {
            throw new TipException("不能修改兼岗用户信息");
        }

        ParamBean auxBean = copyUserInfo(paramBean);
        auxBean.set("_JIANGANG_FLAG_", true);
        String mainDeptCode = "";
        String mainDeptName = "";
        if (!paramBean.getAddFlag() && paramBean.containsKey("DEPT_CODE")) {
            mainDeptCode = paramBean.getStr("DEPT_CODE");
            mainDeptName = DictMgr.getFullNames("SY_ORG_DEPT_ALL", mainDeptCode);
        }
        List<Bean> auxUserList = UserMgr.getAuxiliaryUserBeansByMainUser(mainUserCode,compy_code);
        for (Bean user : auxUserList) {
            String auxDeptCode = user.getStr("DEPT_CODE");
            if (mainDeptCode.equals(auxDeptCode)) {
                throw new TipException("当前用户所在部门和其兼岗部门相同，请先删除" + mainDeptName + "部门下的兼岗设定");
            }
            auxBean.setId(user.getId());
            auxBean.set("USER_CODE", user.getId());
            auxBean.set("JIANGANG_FLAG", Constant.YES_INT);
            //设置了唯一分组变更时需要单独处理的字段
            if (paramBean.contains("USER_WORK_NUM")) {
                Bean updateWnBean = new Bean();
                updateWnBean.setId(user.getId());
                updateWnBean.set("USER_WORK_NUM", paramBean
                        .getStr("USER_WORK_NUM"));
                ServDao.update(paramBean.getServId(), updateWnBean);
                auxBean.remove("USER_WORK_NUM");
            }
            if (paramBean.contains("USER_LOGIN_NAME")) {
                String auxUserLoginName = paramBean.getStr("USER_LOGIN_NAME");
                if (auxUserLoginName.isEmpty()) {
                    auxUserLoginName = outBean.getStr("USER_CODE");
                }
                // 如果本身超过33位则截取33位
                int size = 33;
                if (auxUserLoginName.length() > size) {
                    auxUserLoginName = auxUserLoginName.substring(0, size);
                } 
                auxUserLoginName += "@" + RandomStringUtils.randomAlphanumeric(6);
//                int size = 40;
//                if (auxUserLoginName.length() > size) {
//                    auxUserLoginName = auxUserLoginName.substring(0, size);
//                }
                Bean updateUlnBean = new Bean();
                updateUlnBean.setId(user.getId());
                updateUlnBean.set("USER_LOGIN_NAME", auxUserLoginName);
                ServDao.update(paramBean.getServId(), updateUlnBean);
                auxBean.remove("USER_LOGIN_NAME");
            }
            ServMgr.act(auxBean);
        }
    }

    /**
     * 复制用户信息
     * @param srcBean 原始bean
     * @return ParamBean 结果
     */
    public ParamBean copyUserInfo(Bean srcBean) {
        ParamBean toBean = new ParamBean();
        toBean.copyFrom(srcBean);
        removeUnuseKeys(toBean);
        return toBean;
    }

    
    /**
     * 初始化用户名拼音和检查
     * @param paramBean 参数
     * @return 更新数量
     */
    public OutBean initPinyin(ParamBean paramBean) {
        ServDefBean servDef = ServUtils.getServDef(paramBean.getServId());
        ParamBean param = new ParamBean(ServMgr.SY_ORG_USER_ALL, ServMgr.ACT_FINDS);
        param.setWhere(servDef.getServDefWhere());
        List<Bean> list = ServMgr.act(param).getDataList();
        List<Bean> dataList = new ArrayList<Bean>(list.size());
        for (Bean user : list) {
            Bean data = new Bean();
            data.set("USER_SHORT_NAME", UserName2PinyinUtils.toPinyinHead(user.getStr("USER_NAME")))
                .set("USER_EN_NAME", UserName2PinyinUtils.toPinyin(user.getStr("USER_NAME")))
                .set("USER_CODE", user.getId());
            dataList.add(data);
        }
        String sql = "update SY_ORG_USER set USER_SHORT_NAME=#USER_SHORT_NAME#,USER_EN_NAME=#USER_EN_NAME#"
                + " where USER_CODE=#USER_CODE#";
        int count = Transaction.getExecutor().executeBatchBean(sql, dataList);
        return new OutBean().setOk(Context.getSyMsg("SY_BATCHSAVE_OK", count));
    }
    
    /**
     * 设置白名单用户
     * @param paramBean
     * @return 更新数量
     */
    public OutBean setWhiteList(ParamBean paramBean) {
    	String code = paramBean.getStr("CODE");
    	String type = paramBean.getStr("TYPE");
    	int count = 0;
    	if ("D".equals(type)) { // 选定的是部门
    		Bean deptBean = ServDao.find(ServMgr.SY_ORG_DEPT_ALL, code);
    		String codePath = deptBean.getStr("CODE_PATH");
    		
    		String sql = "update SY_ORG_USER set WHITELIST = 1 where S_FLAG = 1 and "
    				+ "DEPT_CODE in (select DEPT_CODE from SY_ORG_DEPT where CODE_PATH like '" + codePath + "%')";
    		count = Transaction.getExecutor().execute(sql);
    	}
    	return new OutBean().setOk(Context.getSyMsg("SY_BATCHSAVE_OK", count));
    }
    
    /**
     * 去除bean中的无用key
     * @param b 待处理bean
     */
    private void removeUnuseKeys(Bean b) {
        b.remove(Constant.KEY_ID);
        b.remove("USER_CODE");
        b.remove("S_MTIME");
        b.remove("PT_DEPT_CODE");
        b.remove("DEPT_SORT");
        b.remove("DEPT_FLAG");
        b.remove("DEPT_NAME");
        b.remove("_MSG_");
        b.remove("_SERV_ID");
        if (b.getInt("S_FLAG") != Constant.NO_INT) {
            b.remove("S_FLAG");
        }
        b.remove("CODE_PATH");
        b.remove("DEPT_CODE");
        b.remove("DEPT_PT_ID");
        b.remove("DEPT_LEVEL");
        b.remove("TDEPT_CODE");
        b.remove("ODEPT_CODE");
        b.remove("_OLDBEAN_");
    }
}
