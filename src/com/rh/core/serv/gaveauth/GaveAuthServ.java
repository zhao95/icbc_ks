package com.rh.core.serv.gaveauth;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.Lang;

public class GaveAuthServ extends CommonServ {
	private static String DEPT_CODE = "";
	private static String SOURCE_USER_ID = "";
	
	/**
	 * 获取授权人信息
	 * @param paramBean
	 * @return
	 */
	public OutBean getSouUserInfo(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		List<Bean> dataList = new ArrayList<Bean>();
		SqlBean queryBean = new SqlBean();
		queryBean.limit(20);
		queryBean.selects("USER_LOGIN_NAME, USER_NAME");
		String keyword = paramBean.getStr("keyword");
		if (!StringUtils.isBlank(keyword)) {
			setUserQueryInfo(queryBean, keyword);
		}
		List<Bean> userBeans = ServDao.finds("SY_ORG_USER", queryBean);
		if (userBeans != null && userBeans.size() > 0){
			dataList = getUserInfoList(userBeans);
		}
		return outBean.setData(dataList);
	}
	
	/**
	 * 获取在combox中展示的机构数据
	 * @param paramBean
	 * @return
	 */
	public OutBean getComDeptInfo(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		List<Bean> dataList = new ArrayList<Bean>();
		ParamBean bean = new ParamBean();
		Bean dataBean = JsonUtils.toBean(paramBean.getStr("SOURCE_USER_NAME"));
		bean.set("SOURCE_USER_ID", dataBean.getStr("value"));
		List<Bean> deptBeans = getDeptInfo(bean).getList("USER_DEPTS");
		if (deptBeans != null && deptBeans.size() > 0) {
			for (Bean deptBean : deptBeans) {
				dataBean = new Bean();
				dataBean.set("name", deptBean.getStr("TDEPT_NAME"));
				dataBean.set("value", deptBean.getStr("TDEPT_CODE"));
				dataList.add(dataBean);
			}
		}
		return outBean.setData(dataList);
	}
	
	/**
	 * 根据SSID获取用户所在机构
	 * @param paramBean
	 * @return
	 */
	public OutBean getDeptInfo(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		String ssId = paramBean.getStr("SOURCE_USER_ID");
		if (StringUtils.isBlank(ssId)) {
			return outBean.setError();
		}
		SqlBean queryBean = new SqlBean();
		queryBean.selects("TDEPT_CODE,TDEPT_NAME");
		queryBean.and("USER_LOGIN_NAME", ssId);
		List<Bean> dataList = ServDao.finds("SY_ORG_USER", queryBean);
		if (dataList == null || dataList.size() == 0){
			return outBean.setError();
		}
		DEPT_CODE = dataList.get(0).getStr("TDEPT_CODE");
		SOURCE_USER_ID = ssId;
		outBean.set("USER_DEPTS", dataList);
		return outBean;
	}
	
	/**
	 * 获取代理授权中受权人信息
	 * @param paramBean
	 * @return
	 */
	public OutBean getCurrUserInfo(ParamBean paramBean) {
		Bean dataBean = JsonUtils.toBean(paramBean.getStr("SOURCE_USER_NAME"));
		paramBean.set("SOURCE_USER_ID", dataBean.getStr("value"));
		dataBean = JsonUtils.toBean(paramBean.getStr("DEPT_NAME"));
		paramBean.set("DEPT_CODE", dataBean.getStr("value"));
		return getDeptUserInfo(paramBean);
	}
	
	/**
	 * 获取指定部门下用户信息
	 * @param paramBean
	 * @return
	 */
	public OutBean getDeptUserInfo(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		List<Bean> dataList = new ArrayList<Bean>();
		String ssId = paramBean.getStr("SOURCE_USER_ID");
		String deptCode = paramBean.getStr("DEPT_CODE");
		if (StringUtils.isBlank(ssId) || StringUtils.isBlank(deptCode)) {
			return outBean.setError();
		}
		SqlBean queryBean = new SqlBean();
		queryBean.selects("USER_LOGIN_NAME,USER_NAME");
		queryBean.and("TDEPT_CODE", deptCode);
		queryBean.andNot("USER_LOGIN_NAME", ssId);
		queryBean.asc("USER_POST_LEVEL");
		String keyword = paramBean.getStr("keyword");
		if (!StringUtils.isBlank(keyword)) {
			setUserQueryInfo(queryBean, keyword);
		}
		List<Bean> userBeans = ServDao.finds("SY_ORG_USER", queryBean);
		if (userBeans != null && userBeans.size() > 0){
			dataList = getUserInfoList(userBeans);
		}
		outBean.setData(dataList);
		return outBean;
	}
	
	/**
	 * 获取指定部门下用户信息
	 * @param paramBean
	 * @return
	 */
	public OutBean getUserInfo(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		List<Bean> dataList = new ArrayList<Bean>();
		if (StringUtils.isBlank(DEPT_CODE) || StringUtils.isBlank(SOURCE_USER_ID)) {
			return outBean.setError();
		}
		SqlBean queryBean = new SqlBean();
		queryBean.selects("USER_LOGIN_NAME,USER_NAME");
		queryBean.and("TDEPT_CODE", DEPT_CODE);
		queryBean.andNot("USER_LOGIN_NAME", SOURCE_USER_ID);
		queryBean.asc("USER_POST_LEVEL");
		String keyword = paramBean.getStr("keyword");
		if (!StringUtils.isBlank(keyword)) {
			setUserQueryInfo(queryBean, keyword);
		}
		List<Bean> userBeans = ServDao.finds("SY_ORG_USER", queryBean);
		if (userBeans != null && userBeans.size() > 0){
			dataList = getUserInfoList(userBeans);
		}
		outBean.setData(dataList);
		return outBean;
	}
	
	/**
	 * 转交单个部门下权限给受权人
	 * @param paramBean
	 * @return
	 */
	public OutBean gaveDeptAuth(ParamBean paramBean) {
		String sysCode = paramBean.getStr("SYS_CODE");
		if (!StringUtils.isBlank(sysCode)) {
			paramBean.set("SYS_CODE", "");
		}
		return saveAuthInfo(paramBean);
	}
	
	/**
	 * 转交某个子系统下权限给受权人
	 * @param paramBean
	 * @return
	 */
	public OutBean gaveSysAuth(ParamBean paramBean) {
		
		String sysCode = paramBean.getStr("SYS_CODE");
		if (StringUtils.isBlank(sysCode)) {
			return new OutBean().setError();
		}
		return saveAuthInfo(paramBean);
	}
	
	/**
	 * 授权人将权限转交给受权人
	 * @param paramBean
	 * @return
	 */
	public OutBean saveAuthInfo(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		String deptCode = paramBean.getStr("DEPT_CODE");
		String suId = paramBean.getStr("SOURCE_USER_ID");
		String userCode = UserMgr.getUserByLoginName(suId).getCode();
		if (StringUtils.isBlank(deptCode) || StringUtils.isBlank(userCode)) {
			return outBean.setError();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct t.AUTH_STATE from SY_ORG_ROLE_USER_V t where USER_CODE='");
		sb.append(userCode);
		sb.append("' and TDEPT_CODE ='");
		sb.append(deptCode);
		sb.append("' and AUTH_STATE in (0,1)");
		String sysCode = paramBean.getStr("SYS_CODE");
		if (!StringUtils.isBlank(sysCode)) {
			sb.append(" and SYS_CODE ='" + sysCode + "'");
		}
		String querySql = sb.toString();
		List<Bean> dataList = Transaction.getExecutor().query(querySql);
		if (dataList == null || dataList.size() == 0){
			return outBean.setError("没有可以授予的权限");
		} 
		
		if (dataList.get(0).getInt("AUTH_STATE") == 1 || 
				(dataList.size() > 1 && dataList.get(1).getInt("AUTH_STATE") == 1)) {
			// 权限已授权，先收回权限
			outBean = recoverAuth(paramBean);
			if (outBean.getStr(Constant.RTN_MSG).startsWith(Constant.RTN_MSG_ERROR)) {
				return outBean.setError();
			}
		} else if (dataList.get(0).getInt("AUTH_STATE") != 0 || 
				(dataList.size() > 1 && dataList.get(1).getInt("AUTH_STATE") != 0)) {
			return outBean.setError();
		}
		// 转交权限给受权人
		outBean = gaveAuth(paramBean);
		if (outBean.getStr(Constant.RTN_MSG).startsWith(Constant.RTN_MSG_ERROR)) {
			return outBean.setError();
		}
		 
		return outBean;
	}
	
	/**
	 * 转交权限给受权人
	 * @param paramBean
	 * @return
	 */
	public OutBean gaveAuth(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		// 获取指定机构下授权人权限信息
		List<Bean> roleBeans = getUserRoles(paramBean);
		if (roleBeans == null || roleBeans.size() == 0) {
			return outBean.setError("没有可以授予的权限");
		}
		// 将授权信息存储到SY_ORG_ROLE_ACCREDIT表中
		if (!addAccrItems(paramBean, roleBeans)) {
			return outBean.setError("添加数据到授权关系信息表失败");
		}
		// 将受权人权限信息插入到SY_ORG_ROLE_USER表中
		if (!addRoleItems(paramBean, roleBeans)) {
			return outBean.setError("添加受权人权限信息失败");
		}
		// 设置SY_ORG_ROLE_USER表中授权人AUTH_STATE状态为1
		if (!setAuthState1(paramBean)) {
			return outBean.setError("更新授权人权限状态失败");
		}
		return outBean.setOk();
	}
	
	/**
	 * 获取授权人角色权限
	 * @param paramBean
	 * @return
	 */
	private List<Bean> getUserRoles(ParamBean paramBean) {
		String suId = paramBean.getStr("SOURCE_USER_ID");
		String suserCode = UserMgr.getUserByLoginName(suId).getCode();
		if (StringUtils.isBlank(suserCode)) {
			return null;
		}
		
		// 查询授权人的权限
		String[] nums = {"0" ,"1"};
		SqlBean queryBean = new SqlBean();
		queryBean.and("USER_CODE", suserCode);
		setQueryTItem(paramBean, queryBean);
		queryBean.andIn("AUTH_STATE", nums);
		List<Bean> roleBeans = ServDao.finds("SY_ORG_ROLE_USER", queryBean);
		return roleBeans;
	}
	
	/**
	 * 将授权信息存储到SY_ORG_ROLE_ACCREDIT表中
	 * @param paramBean
	 * @param roleBeans
	 * @return
	 */
	private boolean addAccrItems(ParamBean paramBean, List<Bean> roleBeans) {
		List<Bean> accrBeans = new ArrayList<Bean>();
		String cuId = paramBean.getStr("CURRENT_USER_ID");
		String cuserCode = UserMgr.getUserByLoginName(cuId).getCode();
		String suId = paramBean.getStr("SOURCE_USER_ID");
		String begDate = paramBean.getStr("BEG_DATE");
		String endDate = paramBean.getStr("END_DATE");
		if (StringUtils.isBlank(cuserCode) || StringUtils.isBlank(suId) || 
			StringUtils.isBlank(begDate) || StringUtils.isBlank(endDate)) {
			return false;
		}
		Bean accrBean = null;
		for (Bean roleBean : roleBeans) {
			accrBean = new Bean();
			accrBean.set("P_KEY", Lang.getUUID());
			accrBean.set("SYS_CODE", roleBean.getStr("SYS_CODE"));
			accrBean.set("SOURCE_USER_ID", suId);
			accrBean.set("CURRENT_USER_ID", cuId);
			accrBean.set("ROLE_ID", roleBean.getStr("ROLE_CODE"));
			accrBean.set("BNCH_ID", roleBean.getStr("DEPT_CODE"));
			accrBean.set("BEG_DATE", begDate);
			accrBean.set("END_DATE", endDate);
			accrBean.set("STATE", 1);
			accrBean.set("CURR_PERSON_ID", cuserCode);
			accrBeans.add(accrBean);
		}
		if (accrBeans.size() == 0) {
			return false;
		}
		int num = ServDao.creates("SY_ORG_ROLE_ACCREDIT", accrBeans);
		if (num != accrBeans.size()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 将受权人权限信息插入到SY_ORG_ROLE_USER表中
	 * @param paramBean
	 * @param roleBeans
	 * @return
	 */
	private boolean addRoleItems(ParamBean paramBean, List<Bean> roleBeans) {
		List<Bean> croleBeans = new ArrayList<Bean>();
		String cuId = paramBean.getStr("CURRENT_USER_ID");
		String cuserCode = UserMgr.getUserByLoginName(cuId).getCode();
		String suId = paramBean.getStr("SOURCE_USER_ID");
		String suserCode = UserMgr.getUserByLoginName(suId).getCode();
		String tDeptCode = paramBean.getStr("DEPT_CODE");
		if (StringUtils.isBlank(cuserCode) || StringUtils.isBlank(suserCode) 
				|| StringUtils.isBlank(tDeptCode)) {
			return false;
		}
		String deptCode = getDeptCode(cuId, tDeptCode);
		for (Bean roleBean : roleBeans) {
			roleBean.set("RU_ID", Lang.getUUID());
			roleBean.set("USER_CODE", cuserCode);
			roleBean.set("S_MTIME", DateUtils.getDatetimeTS());
			roleBean.set("DEPT_CODE", deptCode);
			roleBean.set("S_USER", suserCode);
			roleBean.set("SSIC_ID", cuId);
			roleBean.set("AUTH_STATE", 2);
			croleBeans.add(roleBean);
		}
		if (croleBeans.size() == 0) {
			return false;
		}
		int num = ServDao.creates("SY_ORG_ROLE_USER", croleBeans);
		if (num != croleBeans.size()) {
			return false;
		}
		this.resetUserMenu(cuserCode);
		
		return true;
	}
	
	/**
	 * 设置SY_ORG_ROLE_USER表中授权人AUTH_STATE状态为1
	 * @param paramBean
	 * @return
	 */
	private boolean setAuthState1(ParamBean paramBean) {
		String suId = paramBean.getStr("SOURCE_USER_ID");
		String suserCode = UserMgr.getUserByLoginName(suId).getCode();
		if (StringUtils.isBlank(suserCode)) {
			return false;
		}
		SqlBean sqlBean = new SqlBean();
		sqlBean.and("USER_CODE", suserCode);
		setQueryItem(paramBean, sqlBean, suId);
		sqlBean.and("AUTH_STATE", 0);
		sqlBean.set("AUTH_STATE", 1);
		Bean result = ServDao.update("SY_ORG_ROLE_USER", sqlBean);
		if (result == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * 回收所有部门受权人权限
	 * @param paramBean
	 * @return
	 */
	public OutBean recoverDeptsAuth(ParamBean paramBean) {
		String deptCode = paramBean.getStr("DEPT_CODE");
		if (!StringUtils.isBlank(deptCode)) {
			paramBean.set("DEPT_CODE", "");
		}
		return recoverAuth(paramBean);
	}
	
	/**
	 * 回收部门下受权人权限
	 * @param paramBean
	 * @return
	 */
	public OutBean recoverDeptAuth(ParamBean paramBean) {
		String sysCode = paramBean.getStr("SYS_CODE");
		if (!StringUtils.isBlank(sysCode)) {
			paramBean.set("SYS_CODE", "");
		}
		return recoverAuth(paramBean);
	}
	
	/**
	 * 回收某个子系统下受权人权限
	 * @param paramBean
	 * @return
	 */
	public OutBean recoverSysAuth(ParamBean paramBean) {
		String sysCode = paramBean.getStr("ROLE_CODE");
		if (StringUtils.isBlank(sysCode)) {
			return new OutBean().setError();
		}
		return recoverAuth(paramBean);
	}
	
	/**
	 * 回收某个受权人权限
	 * @param paramBean
	 * @return
	 */
	public OutBean recoverRoleAuth(ParamBean paramBean) {
		return recoverAuth(paramBean);
	}
	
	/**
	 * 回收受权人权限
	 * @param paramBean
	 * @return
	 */
	public OutBean recoverAuth(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		String suId = paramBean.getStr("SOURCE_USER_ID");
		if (StringUtils.isBlank(suId)) {
			return outBean.setError("授权人信息有误");
		}
		// 判断是否有可回收权限
		if (getCanRecoverRoleNum(paramBean) == 0) {
			return outBean.setError("无可回收权限");
		}
		// 设置SY_ORG_ROLE_USER表中受权人AUTH_STATE状态为3
		if (!setAuthState3(paramBean)) {
			return outBean.setError("删除受权人权限失败");
		}
		// 设置SY_ORG_ROLE_USER表中授权人AUTH_STATE状态为0
		if (!setAuthState0(paramBean)) {
			return outBean.setError("更新授权人权限状态失败");
		}
		// 设置SY_ORG_ROLE_ACCREDIT表中指定记录STATE状态为0
		if (!setAccrState0(paramBean)) {
			return outBean.setError("更新授权关系信息状态失败");
		}
		return outBean.setOk();
	}
	
	/**
	 * 设置SY_ORG_ROLE_USER表中受权人AUTH_STATE状态为3
	 * @param paramBean
	 * @return
	 */
	private boolean setAuthState3(ParamBean paramBean) {
		String suId = paramBean.getStr("SOURCE_USER_ID");
		String suserCode = UserMgr.getUserByLoginName(suId).getCode();
		if (StringUtils.isBlank(suserCode)) {
			return false;
		}
		// 获取需要回收的受权人编码
		List<String> curUserIds = getCurUserIds(paramBean);
		if (curUserIds == null || curUserIds.size() == 0) {
			return false;
		}
		SqlBean sqlBean = null;
		String cuserCode = null;
		for (String curUserId : curUserIds) {
			sqlBean = new SqlBean();
			cuserCode = UserMgr.getUserByLoginName(curUserId)
								.getStr("USER_CODE");
			sqlBean.and("USER_CODE", cuserCode);
			setQueryItem(paramBean, sqlBean, curUserId);
			sqlBean.and("S_USER", suserCode);
			sqlBean.and("AUTH_STATE", 2);
//			sqlBean.set("AUTH_STATE", 3);
//			Bean result = ServDao.update("SY_ORG_ROLE_USER", sqlBean);
			ServDao.delete("SY_ORG_ROLE_USER", sqlBean);
			this.resetUserMenu(cuserCode);
		}
		return true;
	}
	
	/**
	 * 设置SY_ORG_ROLE_USER表中授权人AUTH_STATE状态为0
	 * @param paramBean
	 * @return
	 */
	private boolean setAuthState0(ParamBean paramBean) {
		String suId = paramBean.getStr("SOURCE_USER_ID");
		String suserCode = UserMgr.getUserByLoginName(suId).getCode();
		if (StringUtils.isBlank(suserCode)) {
			return false;
		}
		
		SqlBean sqlBean = new SqlBean();
		sqlBean.and("USER_CODE", suserCode);
		setQueryItem(paramBean, sqlBean, suId);
		sqlBean.and("AUTH_STATE", 1);
		sqlBean.set("AUTH_STATE", 0);
		ServDao.update("SY_ORG_ROLE_USER", sqlBean);
		return true;
	}
	
	/**
	 * 设置SY_ORG_ROLE_ACCREDIT表中指定记录STATE状态为0
	 * @param paramBean
	 * @return
	 */
	private boolean setAccrState0(ParamBean paramBean) {
		String suId = paramBean.getStr("SOURCE_USER_ID");
		if (StringUtils.isBlank(suId)) {
			return false;
		}
		
		SqlBean sqlBean = new SqlBean();
		sqlBean.and("SOURCE_USER_ID", suId);
		setQueryAccItem(paramBean, sqlBean, suId);
		sqlBean.and("STATE", 1);
		sqlBean.set("STATE", 0);
		// 设置实际收回权限时间
		sqlBean.set("END_DATE", DateUtils.getDatetime("yyyy-MM-dd HH"));
		ServDao.update("SY_ORG_ROLE_ACCREDIT", sqlBean);
		return true;
	}
	
	/**
	 * 从SY_ORG_ROLE_ACCREDIT表中获取受权人usercode
	 * @param paramBean
	 * @return
	 */
	private List<String> getCurUserIds(ParamBean paramBean) {
		List<Bean> accBeans = new ArrayList<Bean>();
		String suId = paramBean.getStr("SOURCE_USER_ID");
		if (StringUtils.isBlank(suId)) {
			return null;
		}
		
		SqlBean sqlBean = new SqlBean();
		sqlBean.and("SOURCE_USER_ID", suId);
		sqlBean.and("STATE", 1);
		setQueryAccItem(paramBean, sqlBean, suId);
		accBeans = ServDao.finds("SY_ORG_ROLE_ACCREDIT", sqlBean);
		if (accBeans == null || accBeans.size() == 0) {
			return null;
		}
		List<String> curUserIds = new ArrayList<String>();
		String curUserId = null;
		for (Bean bean : accBeans) {
			curUserId = bean.getStr("CURRENT_USER_ID");
			if (!curUserIds.contains(curUserId)) {
				curUserIds.add(curUserId);
			}
		}
		return curUserIds;
	}
	
	/**
	 * 设置查询条件(DEPT_CODE)
	 * @param paramBean
	 * @param sqlBean
	 */
	private void setQueryItem(ParamBean paramBean, SqlBean sqlBean, String ssid) {
		String tDeptCode = paramBean.getStr("DEPT_CODE");
		String sysCode = paramBean.getStr("SYS_CODE");
		String roleCode = paramBean.getStr("ROLE_CODE");
		
		if (!StringUtils.isBlank(tDeptCode)) {
			List<String> deptCodes = getDeptCodeList(ssid, tDeptCode);
			if (deptCodes == null || deptCodes.size() == 0) {
				return;
			}
			sqlBean.andIn("DEPT_CODE", deptCodes.toArray());
			if (!StringUtils.isBlank(sysCode)) {
				sqlBean.and("SYS_CODE", sysCode);
			}
			if (!StringUtils.isBlank(roleCode)) {
				sqlBean.and("ROLE_CODE", roleCode);
			}
		}
	}
	
	/**
	 * 设置查询条件(TDEPT_CODE)
	 * @param paramBean
	 * @param sqlBean
	 */
	private void setQueryTItem(ParamBean paramBean, SqlBean sqlBean) {
		String deptCode = paramBean.getStr("DEPT_CODE");
		String sysCode = paramBean.getStr("SYS_CODE");
		String roleCode = paramBean.getStr("ROLE_CODE");
		
		if (!StringUtils.isBlank(deptCode)) {
			sqlBean.and("TDEPT_CODE", deptCode);
			if (!StringUtils.isBlank(sysCode)) {
				sqlBean.and("SYS_CODE", sysCode);
			}
			if (!StringUtils.isBlank(roleCode)) {
				sqlBean.and("ROLE_CODE", roleCode);
			}
		}
	}
	
	/**
	 * 设置查询SY_ORG_ROLE_ACCREDIT表的条件
	 * @param paramBean
	 * @param sqlBean
	 */
	private void setQueryAccItem(ParamBean paramBean, SqlBean sqlBean, String ssid) {
		String tDeptCode = paramBean.getStr("DEPT_CODE");
		String sysCode = paramBean.getStr("SYS_CODE");
		String roleCode = paramBean.getStr("ROLE_CODE");
		
		if (!StringUtils.isBlank(tDeptCode)) {
			List<String> deptCodes = getDeptCodeList(ssid, tDeptCode);
			if (deptCodes == null || deptCodes.size() == 0) {
				return;
			}
			sqlBean.andIn("BNCH_ID", deptCodes.toArray());
			if (!StringUtils.isBlank(sysCode)) {
				sqlBean.and("SYS_CODE", sysCode);
			}
			if (!StringUtils.isBlank(roleCode)) {
				sqlBean.and("ROLE_ID", roleCode);
			}
		}
	}
	
	/**
	 * 设置查询用户的条件
	 * @param queryBean
	 * @param keyword
	 */
	private void setUserQueryInfo(SqlBean queryBean, String keyword) {
		String[] keywords = keyword.split("\\|");
		
		if (keywords.length == 1) {
			if (!StringUtils.isBlank(keywords[0])) {
				int i = 0;
				for (; i < keywords[0].length(); i++) {
					char keyChar = keywords[0].charAt(i);
					if (keyChar < '0' || keyChar > '9') {
						queryBean.andLike("USER_NAME", keywords[0].trim());
						break;
					}
				}
				if (i >= keywords[0].length()) {
					queryBean.andLike("USER_LOGIN_NAME", keywords[0].trim());
				}
			}
		}
		if (keywords.length > 1) {
			if (!StringUtils.isBlank(keywords[0])) {
				queryBean.andLike("USER_NAME", keywords[0].trim());
			}
			if (!StringUtils.isBlank(keywords[1])) {
				queryBean.andLike("USER_LOGIN_NAME", keywords[1].trim());
			}
		} 
	}
	
	/**
	 * 获取combobox中的数据集合
	 * @param userBeans
	 * @return
	 */
	private List<Bean> getUserInfoList(List<Bean> userBeans) {
		List<Bean> dataList = new ArrayList<Bean>();
		Bean dataBean = null;
		StringBuilder sb = null;
		for (Bean userBean : userBeans) {
			dataBean = new Bean();
			sb = new StringBuilder();
			sb.append(userBean.getStr("USER_NAME"));
			sb.append("|");
			sb.append(userBean.getStr("USER_LOGIN_NAME"));
			dataBean.set("name", sb.toString());
			dataBean.set("value", userBean.getStr("USER_LOGIN_NAME"));
			dataList.add(dataBean);
		}
		return dataList;
	}
	
	/**
	 * 根据SSID和TDEPT_CODE获取DEPT_CODE
	 * @param ssid
	 * @param tDeptCode
	 * @return
	 */
	private List<String> getDeptCodeList(String ssid, String tDeptCode) {
		List<String> deptCodeList = new ArrayList<String>();
		SqlBean queryBean = new SqlBean();
		queryBean.selects("DEPT_CODE");
		queryBean.and("USER_LOGIN_NAME", ssid);
		queryBean.and("TDEPT_CODE", tDeptCode);
		List<Bean> userBeans = ServDao.finds("SY_ORG_USER", queryBean);
		if (userBeans != null && userBeans.size() > 0) {
			for (Bean userBean : userBeans) {
				deptCodeList.add(userBean.getStr("DEPT_CODE"));
			}
		}
		return deptCodeList;
	}
	
	/**
	 * 根据SSID和ROLE_CODE获取DEPT_CODE
	 * @param ssid
	 * @param tDeptCode
	 * @return
	 */
	private String getDeptCode(String ssid, String deptCode) {
		SqlBean queryBean = new SqlBean();
		queryBean.selects("DEPT_CODE");
		queryBean.and("USER_LOGIN_NAME", ssid);
		queryBean.and("TDEPT_CODE", deptCode);
		Bean userBean = ServDao.find("SY_ORG_USER", queryBean);
		return userBean.getStr("DEPT_CODE");
	}
	
	/**
	 * 获取指定日期所在星期所有授权信息
	 * @param paramBean
	 * @return
	 */
	public OutBean getSpeDateAccBeans(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		String suUid = paramBean.getStr("SOURCE_USER_ID");
		String dateStr = paramBean.getStr("SPEC_DATE");
		if (StringUtils.isBlank(suUid) || StringUtils.isBlank(dateStr)) {
			return outBean.setError();
		}
		
		StringBuilder sb = null;
		String monDate = DateUtils.getFirstDateOfWeek(dateStr);
		for (int i = 0; i < 7; i++) {
			String minDayTime = DateUtils.getDateAdded(i, monDate);
			String maxDayTime = DateUtils.getDateAdded(i+1, monDate);
			sb = new StringBuilder();
			sb.append("select distinct SOURCE_USER_NAME,DEPT_NAME,CURRENT_USER_NAME,SYS_NAME");
			sb.append(" from SY_ORG_ROLE_ACCREDIT_ALL_V ");
			sb.append("Where SOURCE_USER_ID ='");
			sb.append(suUid);
			sb.append("' and BEG_DATE <'");
			sb.append(maxDayTime);
			sb.append("' and END_DATE >'");
			sb.append(minDayTime);
			sb.append("'");
			List<Bean> beanList = Transaction.getExecutor().query(sb.toString());
			outBean.set("ACC_BEANS" + i, beanList);
		}
		return outBean;
	}
	
	/**
	 * 查询授权人已授权的权限数目
	 * @param paramBean
	 * @return
	 */
	private int getCanRecoverRoleNum(ParamBean paramBean) {
		String suId = paramBean.getStr("SOURCE_USER_ID");
		String suserCode = UserMgr.getUserByLoginName(suId).getCode();
		if (StringUtils.isBlank(suserCode)) {
			return 0;
		}
		
		// 查询授权人的权限
		String[] nums = {"1"};
		SqlBean queryBean = new SqlBean();
		queryBean.and("USER_CODE", suserCode);
		setQueryTItem(paramBean, queryBean);
		queryBean.andIn("AUTH_STATE", nums);
		int num = ServDao.count("SY_ORG_ROLE_USER", queryBean);
		return num;
	}
	
	/**
	 * 更新静态变量部门号
	 * @param paramBean
	 * @return
	 */
	public OutBean updateDeptCode(ParamBean paramBean) {
		OutBean outBean = new OutBean();
		DEPT_CODE = paramBean.getStr("DEPT_CODE");
		return outBean;
	}
	
	/**
	 * 清除用户菜单信息
	 * @param userCode
	 */
	private void resetUserMenu(String userCode) {
	    //清除菜单信息
	    UserMgr.clearMenuByUsers(userCode);
	    //清除缓存中的用户扩展信息
	    UserBean userBean = UserMgr.getCacheUser(userCode);
	    if (userBean != null) {
	        userBean.clearUserExt();
	    }
	}
	
}
