package com.rh.core.org.mgr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.logs.RHLog;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Lang;

public class RoleUserRelMgr {
	/** log */
    private static Log log = LogFactory.getLog(RoleUserRelMgr.class);
    
    /** 角色用户表 */
    public static String SY_ORG_ROLE_USER = "SY_ORG_ROLE_USER";
    
    /** 转授权表 */
    public static final String SY_ORG_ROLE_ACCREDIT = "SY_ORG_ROLE_ACCREDIT";
    
    public static final String UPDATE_STATE = "UPDATE_STATE"; //更新状态字段
    public static final int UPDATE_STATE_ADD = 1; //添加
    public static final int UPDATE_STATE_MODIFY = 2; //修改
    public static final int UPDATE_STATE_DELETE = 3; //删除
    
    /**
     * 同步dams_accredit_main表数据
     */
    public static OutBean impAccreditMain(List<Bean> dataList) throws Exception {
    	log.debug("------------------impAccreditMain：开始");
    	if (dataList == null) {
    		return new OutBean();
    	}
    	
    	List<Bean> addList = new ArrayList<Bean>();
		List<String> delList = new ArrayList<String>();
		
		for (Bean data : dataList) {
			String pk = data.getStr("P_KEY");
			if (StringUtils.isEmpty(pk)) {
				log.error("------------------主键为空：" + data);
				continue;
			} else {
				data.setId(pk);
			}
			
			int updateState = data.getInt(UPDATE_STATE);
			if (updateState == UPDATE_STATE_ADD) { //添加
				//添加ADD_FLAG标志，强制添加
				data.set(ParamBean.ADD_FLAG, true);
				addList.add(data);
			} else if (updateState == UPDATE_STATE_MODIFY) { //修改
				addList.add(data);
			} else if (updateState == UPDATE_STATE_DELETE) { //删除，必须有主键
				delList.add(pk);
			} else {
				log.error("------------------无效的更新状态：" + updateState);
				RHLog.error(RoleUserRelMgr.class, "------------------无效的更新状态：" + updateState);
			}
		}
		
		ParamBean paramBean = new ParamBean(SY_ORG_ROLE_ACCREDIT, ServMgr.ACT_BATCHSAVE)
				.setBatchSaveDatas(addList)
				.setBatchSaveDelIds(Lang.arrayJoin(delList.toArray(new String[delList.size()])));
		OutBean out = ServMgr.act(paramBean);
		updateCurrentPersonId();
		return out;
    }
    /**
     * 更新表SY_ORG_ROLE_ACCREDIT 的 CURR_PERSON_ID 字段值。
     */
    private static void updateCurrentPersonId() {
    	StringBuilder sql = new StringBuilder();
    	sql.append("Update SY_ORG_ROLE_ACCREDIT a set CURR_PERSON_ID = (select USER_CODE from SY_ORG_USER"); 
    	sql.append(" where USER_LOGIN_NAME = a.CURRENT_USER_ID) where CURR_PERSON_ID is null");
    	
    	Context.getExecutor().execute(sql.toString());
    }
    
    /**
     * 导入REL数据，金喜的逻辑：
     * （1）角色、统一认证号、部门联合必然唯一，所以数据列表中如果存在，不管是什么操作，先直接删除老数据。
     * （2）如果是新增操作，则添加；如果是修改且不是“取消授权”（相当于删除），添加；其它不予处理。
     * @param dataList
     * @throws Exception
     */
	public static OutBean impRoleUserRel(List<Bean> dataList) throws Exception {
		log.debug("------impRoleUserRel : begin !");
		
		//把同步的角色用户数据存入历史表
		try {
			ParamBean batchSaveBean = new ParamBean("SY_USER_ROLE_REL_HIS", ServMgr.ACT_BATCHSAVE)
					.setBatchSaveDatas(dataList);
			ServMgr.act(batchSaveBean);
		} catch (Exception e) {
			log.error(e);
			RHLog.error(RoleUserRelMgr.class, e);
		}
		
		List<Bean> addList = new ArrayList<Bean>();
		List<Bean> delList = new ArrayList<Bean>();
		boolean isRoleExist = true;
		
		for (Bean data : dataList) {
			Bean roleBean = parseData(data);
			//角色是否存在
			isRoleExist = isRoleExisted(roleBean);
			
			// 查询本次变动的老数据
			Bean oldRelBean = findRelBean(roleBean.getStr("ROLE_CODE"), roleBean.getStr("SSIC_ID"), roleBean.getStr("DEPT_CODE"));
			if (oldRelBean != null) {
				delList.add(oldRelBean);
			}else {
				/* 若角色不存在则需要删除原先存的脏数据  */
				if (!isRoleExist) {
					List<Bean> badDatas = findRoleUserWhenRoleNotExisted(roleBean);
					if (null != badDatas && !badDatas.isEmpty()) {
						delList.addAll(badDatas);
					}
				}
			}
			
			int updateState = roleBean.getInt(UPDATE_STATE);	
			if (updateState == UPDATE_STATE_ADD) { // 新增
				if (isRoleExist) {
					addList.add(roleBean);
				}
			} else if (updateState == UPDATE_STATE_MODIFY) { // 修改
				if (roleBean.getStr("AUTH_STATE").equals("3")) { // 授权字段为3时表示取消转授权，这里删除信息
					// 不做添加操作
				} else {
					if (isRoleExist) {
						addList.add(roleBean);
					}
				}
			} else if (updateState == UPDATE_STATE_DELETE) { // 删除
				// 暂不执行操作
			} else {
				log.error("------impRoleUserRel : action is error! data = " + data + "-------");
				RHLog.error(RoleUserRelMgr.class, "------impRoleUserRel : action is error! data = " + data + "-------");
			}
		}
		log.debug("------impRoleUserRel : delList = " + delList + "!");
		// 删除本次变动的数据
		OutBean delBean = ServMgr.act(SY_ORG_ROLE_USER, ServMgr.ACT_DELETE, new ParamBean().setDeleteDatas(delList));
		log.debug("------impRoleUserRel : addList = " + addList + "!");
		// 添加本次新添加数据
		OutBean saveBean = ServMgr.act(SY_ORG_ROLE_USER, ServMgr.ACT_BATCHSAVE, new ParamBean().setBatchSaveDatas(addList));
		
		return new OutBean().set("_DEL_", delBean).set("_SAVE_", saveBean);
	}
	
	/**
	 * 查询rel bean
	 * @param roleCode
	 * @param ssicId
	 * @param deptCode
	 * @return
	 */
	private static Bean findRelBean(String roleCode, String ssicId, String deptCode) {
		SqlBean sql = new SqlBean();
		sql.and("ROLE_CODE", roleCode);
		sql.and("SSIC_ID", ssicId);
		sql.and("DEPT_CODE", deptCode);
		return ServDao.find(SY_ORG_ROLE_USER, sql);
	}
	
	/**
	 * 将接口传入的数据格式化成本系统的roleBean
	 * @param data
	 * @return
	 */
	private static Bean parseData(Bean data) {
		String userCode = "NOT FOUND USER";
		String ssicId = data.getStr("SSIC_ID");
		
		// 必须获取UserCode，因为服务扩展类中会用到
		Bean bean = UserMgr.findUserByLoginName(ssicId);
		if (bean != null) {
			userCode = bean.getStr("USER_CODE");
		}
		
		Bean parseBean = new Bean();
		parseBean.set("ROLE_CODE", data.getStr("ROLE_ID"));
		parseBean.set("DEPT_CODE", data.getStr("STRU_ID"));
		parseBean.set("USER_CODE", userCode);
		parseBean.set("SSIC_ID", ssicId);
//		parseBean.set("ACTION", data.getStr("action"));
		parseBean.set("UPDATE_STATE", data.getStr("UPDATE_STATE")); 	// 1表示新增，2表示修改，3表示删除 
		parseBean.set("AUTH_STATE", data.getStr("AUTH_STATE"));		// 0正常 1转授权 2被转授权 3取消转授权
		parseBean.set("SYS_CODE", data.getStr("SYS_CODE"));
		// TODO authState转授权功能没有实现
		return parseBean;
	}
	
	/**
	 * 当角色不存在时，查找当前角色用户的脏数据
	 * @param roleBean
	 * @return
	 */
	private static List<Bean> findRoleUserWhenRoleNotExisted (Bean roleBean){
		if (roleBean == null) {
			return new ArrayList<Bean>();
		}
		
		StringBuffer sqlBuffer = new StringBuffer(" select * from SY_ORG_ROLE_USER where 1=1 ");
		String[] keys = {"ROLE_CODE","SSIC_ID","DEPT_CODE"};
		for (String key : keys) {
			sqlBuffer.append(" and "+key+" = '"+roleBean.getStr(key)+"'");
		}
		log.info("------impRoleUserRel : ROLE_CODE is not existed，system will delete invalid role_user, " + sqlBuffer.toString());
		return Transaction.getExecutor().query(sqlBuffer.toString());
	}
	
	/**
	 * 判断角色是否存在
	 * @param roleBean
	 * @return
	 */
	private static boolean isRoleExisted (Bean roleBean){
		if (null == roleBean) {
			return false;
		}
		
		int count = ServDao.count("SY_ORG_ROLE_ALL", new SqlBean().and("ROLE_CODE", roleBean.getStr("ROLE_CODE")));
		if (count == 0) {
			return false;
		}else {
			return true;
		}
	}
}
