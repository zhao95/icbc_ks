package com.rh.core.org.serv;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.BaseContext.APP;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.org.util.OrgConstant;
import com.rh.core.plug.im.ImMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.UserName2PinyinUtils;
import com.rh.ts.pvlg.PvlgUtils;

/**
 * 部门服务类
 * 
 * @author cuihf
 * 
 */
public class DeptServ extends CommonServ {

    /**
     * 与RTX通用部门和用户
     * @param paramBean 参数信息
     * @return 同步结果
     */
    public OutBean syncIm(ParamBean paramBean) {
        String servId = paramBean.getServId();
        int count = 0;
        if (Context.appBoolean(APP.IM)) { //只有启动了IM才进行同步
            //添加部门信息
            ParamBean queryBean = new ParamBean();
            UserBean userBean = Context.getUserBean();
            queryBean.set("CMPY_CODE", userBean.getCmpyCode());
            queryBean.set(Constant.PARAM_ORDER, "DEPT_LEVEL,DEPT_SORT");
            List<Bean> deptList = ServDao.finds(servId, queryBean);
            for (Bean data : deptList) {
                if (ImMgr.getIm().saveDept(data)) {
                    count++;
                }
            }
            //添加用户信息
            queryBean = new ParamBean(ServMgr.SY_ORG_USER, ServMgr.ACT_FINDS);
            queryBean.set("CMPY_CODE", userBean.getCmpyCode());
            List<Bean> userList = ServMgr.act(queryBean).getDataList();
            for (Bean data : userList) {
                data.set("USER_PASSWORD_REAL", Context.getSyConf("SY_USER_PASSWORD_INIT", "123456"));
                if (ImMgr.getIm().saveUser(data)) {
                    count++;
                }
            }
        }
        OutBean outBean = new OutBean();
        if (count > 0) {
            outBean.setOk(Context.getSyMsg("SY_SYNC_OK", String.valueOf(count)));
        } else {
            outBean.setError(Context.getSyMsg("SY_SYNC_ERROR"));
        }
        return outBean;
    }

    /**
     * 删除之后更新IM处理
     * @param paramBean 参数信息
     * @param outBean 删除结果信息
     */
    protected void afterDelete(ParamBean paramBean, OutBean outBean) {
        if (Context.appBoolean(APP.IM)) { //启动了IM进行同步
            ImMgr.getIm().deleteDept(paramBean.getId());
        }
    }
    
    /**
     * 保存之前处理
     * @param paramBean 参数信息
     */
    protected void beforeSave(ParamBean paramBean) {
        String servId = paramBean.getServId();
        Bean newData = paramBean.getSaveFullData(); //获取全数据
        if (newData.isEmpty("DEPT_FULL_NAME")) { //部门全称不设置缺省等于部门名称
            paramBean.set("DEPT_FULL_NAME", newData.getStr("DEPT_NAME"));
        }
        if (paramBean.contains("DEPT_NAME")) { //计算部门简称
//            paramBean.set("DEPT_SHORT_NAME", UserName2PinyinUtils.toPinyinHead(paramBean.getStr("DEPT_NAME")));
        	// 去掉了将部门名称转成英文的步骤 - by zhangjx
        }
        if (paramBean.contains("DEPT_PCODE") || paramBean.contains("DEPT_TYPE")) { //存在父部门或设置部门类型
            String curCode = newData.getStr("DEPT_CODE");
            if (newData.isEmpty("DEPT_PCODE")) { //没有设置父部门
                paramBean.set("DEPT_TYPE", OrgConstant.DEPT_TYPE_ORG); //没有父部门的缺省为机构
                paramBean.set("TDEPT_CODE", curCode); //有效部门为自己
                paramBean.set("ODEPT_CODE", curCode); //机构为自己
            } else {
                if (newData.getInt("DEPT_TYPE") == OrgConstant.DEPT_TYPE_ORG) { //当前部门为机构
                    paramBean.set("TDEPT_CODE", curCode); //有效部门为自己
                    paramBean.set("ODEPT_CODE", curCode); //机构为自己
                } else { //当前部门不是机构
                    String pId = newData.getStr("DEPT_PCODE");
                    Bean pDept = ServDao.find(servId, pId);
                    if (pDept.getInt("DEPT_TYPE") == OrgConstant.DEPT_TYPE_ORG) { //父部门为机构，则子部门为有效部门
                        paramBean.set("TDEPT_CODE", curCode); //当前有效部门为自己
                        paramBean.set("ODEPT_CODE", pId);
                    } else {
                        paramBean.set("TDEPT_CODE", pDept.getStr("TDEPT_CODE")); //有效部门继承父部门的
                        paramBean.set("ODEPT_CODE", pDept.getStr("ODEPT_CODE")); //机构继承父部门的
                    }
                }
            }
        }
    }
    
    /**
     * 保存之后处理
     * @param paramBean 参数信息
     * @param outBean  参数信息
     */
    protected void afterSave(ParamBean paramBean, OutBean outBean) {
        String servId = paramBean.getServId();
        if (!paramBean.getAddFlag()) { //修改模式
            Bean oldBean = paramBean.getSaveOldData();
            Bean setBean = new Bean();
            SqlBean whereBean = new SqlBean();
            //根据当前部门类型更新所有类型为部门的子孙部门
            
            if (paramBean.contains("DEPT_TYPE") 
                    && (paramBean.getInt("DEPT_TYPE") != oldBean.getInt("DEPT_TYPE"))) { //变更了部门类型
                if (paramBean.getInt("DEPT_TYPE") == OrgConstant.DEPT_TYPE_ORG) { //部门变机构
                    //找到所有直接子部门（类型为部门）
                    whereBean.and("DEPT_TYPE", OrgConstant.DEPT_TYPE_DEPT).and("DEPT_PCODE", oldBean.getId());
                    List<Bean> subList = ServDao.finds(servId, whereBean);
                    for (Bean sub : subList) {
                        whereBean = new SqlBean().and("DEPT_TYPE", OrgConstant.DEPT_TYPE_DEPT)
                                .and("ODEPT_CODE", sub.getStr("ODEPT_CODE"))
                                .andLikeRT("CODE_PATH", sub.getStr("CODE_PATH")); //向下修改所有子部门及子的子孙部门
                        setBean.set("ODEPT_CODE", outBean.getStr("ODEPT_CODE"))
                            .set("TDEPT_CODE", sub.getId()); //将子部门设为有效部门
                        ServDao.updates(servId, setBean, whereBean);
                    }
                } else { //机构变部门,找到本机构下所有子孙部门（类型为部门）
                    whereBean.and("DEPT_TYPE", OrgConstant.DEPT_TYPE_DEPT)
                        .and("ODEPT_CODE", oldBean.getStr("ODEPT_CODE"))
                        .andLikeRT("CODE_PATH", oldBean.getStr("CODE_PATH")); //向下修改所有子部门及子的子孙部门
                    setBean.set("ODEPT_CODE", outBean.getStr("ODEPT_CODE"))
                        .set("TDEPT_CODE", outBean.getStr("TDEPT_CODE"));
                    ServDao.updates(servId, setBean, whereBean);
                }
            } else if (paramBean.contains("TDEPT_CODE")
                    && !(paramBean.getStr("TDEPT_CODE").equals(oldBean.getStr("TDEPT_CODE")))) { //变更了有效部门
                whereBean.and("DEPT_TYPE", OrgConstant.DEPT_TYPE_DEPT).and("ODEPT_CODE", oldBean.getStr("ODEPT_CODE"))
                    .andLikeRT("CODE_PATH", oldBean.getStr("CODE_PATH"));
                setBean.set("ODEPT_CODE", outBean.getStr("ODEPT_CODE")).set("TDEPT_CODE", outBean.getStr("TDEPT_CODE"));
                ServDao.updates(servId, setBean, whereBean);
            }
        }
        if (Context.appBoolean(APP.IM)) { //启动了IM进行同步
            ImMgr.getIm().saveDept(outBean);
        }
        
        // 添加，修改模式下重新计算sort排序
//        if (!paramBean.isEmpty("DEPT_PCODE")) { // 如果父部门不为空，即非根节点
//        	List<Bean> updatesList = new ArrayList<Bean>(); // 需要修改的deptBean集合
//        	List<String> updateFields = new ArrayList<String>(); // 需要修改的字段
//        	updateFields.add("DEPT_SORT");
//        	
//        	// 查找同层级需要修改的数据
//        	ParamBean whereBean = new ParamBean();
//        	whereBean.setWhere(" AND S_FLAG = '1' AND DEPT_PCODE = '" + paramBean.getStr("DEPT_PCODE") + "'"); // 同层级，有效数据
//        	whereBean.setOrder("DEPT_NAME ASC"); // 按照名字正序排列
//        	List<Bean> siblings = ServDao.finds(ServMgr.SY_ORG_DEPT, whereBean);
//        	int sort = 900; // sort起始值
//        	for (int i = 0; i < siblings.size(); i++) {
//        		Bean deptBean = siblings.get(i);
//        		if (deptBean.getInt("DEPT_SORT") != 999) { // 如果是999，即本部，跳过
//        			deptBean.set("DEPT_SORT", sort - 2 * i); // 逐个递减，步长为2
//        			updatesList.add(deptBean);
//        		}
//        	}
//        	ServDao.updates(ServMgr.SY_ORG_DEPT, updateFields, updatesList); // 批量更新
//        }
    }
    
    /**
     * 重建当前用户所在公司的组织机构数据
     * @param paramBean 无
     * @return outBean 更新数量
     */
    @SuppressWarnings("deprecation")
	public OutBean rebuild(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        List<Bean> deptList = new ArrayList<Bean>(200000);
        buildSubList(deptList, new Bean());
        int count = deptList.size();
        if (count > 0) {
            count = ServDao.updates(ServMgr.SY_ORG_DEPT, deptList);
        }
        return outBean.setCount(count).setOk();
    }
    
    /**
     * 递归整理部门数据
     * @param list 存放部门数据的列表
     * @param pBean 父部门信息
     */
    private void buildSubList(List<Bean> list, Bean pBean) {
        SqlBean whereBean = new SqlBean();
        whereBean.and("CMPY_CODE", Context.getCmpy());
        if (pBean.getId().length() > 0) { //有父
            whereBean.and("DEPT_PCODE", pBean.getId());
        } else {
            whereBean.andNull("DEPT_PCODE");
        }
        whereBean.selects("DEPT_CODE, DEPT_TYPE");
        List<Bean> subList = ServDao.finds(ServMgr.SY_ORG_DEPT, whereBean);
        for (Bean item : subList) {
            if (pBean.getId().length() == 0) { //根节点
                item.set("DEPT_LEVEL", 1).set("ODEPT_CODE", item.getId()).set("TDEPT_CODE", item.getId())
                    .set("DEPT_TYPE", Constant.DEPT_TYPE_ORG)
                    .set("CODE_PATH", item.getId() + Constant.CODE_PATH_SEPERATOR);
            } else {
                item.set("DEPT_LEVEL", pBean.getInt("DEPT_LEVEL") + 1)
                    .set("CODE_PATH", pBean.getStr("CODE_PATH") + item.getId() + Constant.CODE_PATH_SEPERATOR);
                if (item.getInt("DEPT_TYPE") == Constant.DEPT_TYPE_ORG) { //子是机构
                    item.set("ODEPT_CODE", item.getId()).set("TDEPT_CODE", item.getId());
                } else { //子是部门
                    item.set("ODEPT_CODE", pBean.getStr("ODEPT_CODE"));
                    if (pBean.getInt("DEPT_TYPE") == Constant.DEPT_TYPE_ORG) { //父是机构
                        item.set("TDEPT_CODE", item.getId());
                    } else {
                        item.set("TDEPT_CODE", pBean.getStr("TDEPT_CODE"));
                    }
                }
            }
            buildSubList(list, item);
            list.add(item);
        }
    }
    
    /**
     * 初始化部门简称
     * @param paramBean 参数
     * @return 更新数量
     */
    public OutBean initPinyin(ParamBean paramBean) {
        ServDefBean servDef = ServUtils.getServDef(paramBean.getServId());
        ParamBean param = new ParamBean(ServMgr.SY_ORG_DEPT_ALL, ServMgr.ACT_FINDS);
        param.setWhere(servDef.getServDefWhere());
        List<Bean> list = ServMgr.act(param).getDataList();
        List<Bean> dataList = new ArrayList<Bean>(list.size());
        for (Bean dept : list) {
            Bean data = new Bean();
            data.set("DEPT_SHORT_NAME", UserName2PinyinUtils.toPinyinHead(dept.getStr("DEPT_NAME")))
                .set("DEPT_CODE", dept.getId());
            dataList.add(data);
        }
        String sql = "update SY_ORG_DEPT set DEPT_SHORT_NAME=#DEPT_SHORT_NAME# where DEPT_CODE=#DEPT_CODE#";
        int count = Transaction.getExecutor().executeBatchBean(sql, dataList);
        return new OutBean().setOk(Context.getSyMsg("SY_BATCHSAVE_OK", count));
    }

    /**
     * 查询之前的拦截方法，由子类重载
     *
     * @param paramBean
     *            参数信息
     */
    protected void beforeQuery(ParamBean paramBean) {
        ParamBean param = new ParamBean();
        param.set("paramBean", paramBean);
        param.set("_searhFirstType", false);
//  			param.set("ctlgModuleName", ctlgModuleName);
        param.set("fieldName","DEPT_CODE");
        param.set("serviceName", paramBean.getServId());
        PvlgUtils.setCtlgPvlgWhere(param);
        //PvlgUtils.setOrgPvlgWhereNoSearch(param);
    }
}
