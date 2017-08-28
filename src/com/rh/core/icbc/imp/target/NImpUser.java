package com.rh.core.icbc.imp.target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.icbc.imp.log.OrgLogMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;

/**
 * 导入用户
 * @author anan
 *
 */
public class NImpUser {

	private static Log log = LogFactory.getLog(NImpUser.class);
	
	public static final int QUERY_NUM = 5000;
	
	public static final String CMPY_CODE = "icbc";
	
	public static final String S_USER = "SYSTEM_ICBC";
	
    //编码/所在机构编码/所在机构名称/人员状态/机构类型/所属一级机构/所属二级机构/上级分支
    public static final String HRM_ZDSTAFFSTRU = "HRM_ZDSTAFFSTRU";
    
    //编码/姓名/性别/生日/在职状态/参加工作时间/民族/籍贯/人员类别
    public static final String HRM_ZDSTAFFBINFO = "HRM_ZDSTAFFBINFO";
    
    //人员状态/所属机构/所属一级机构/所属二级机构/上级分支/职务层级
    public static final String HRM_ZDSTAFFSTATE = "HRM_ZDSTAFFSTATE";
    
    //日增量员工行政职务信息
    public static final String HRM_ZDSTAFFADMIN = "HRM_ZDSTAFFADMIN";
    
    // 人员信息视图
    public static final String HRM_ZDSTAFF_V = "SY_HRM_ZDSTAFF_V";
    
	/**
	 * 全量导入用户数据
	 */
	public void recuUser() throws Exception {
//		Transaction.begin();
		
		cleanUserData();
		
//		Transaction.commit();
		
		// 已经执行过的数据
		Map<String, Boolean> executedData = new HashMap<String, Boolean>();
		
		Bean queryCount = new Bean();
//		String where = " AND S_FLAG = '1' AND PERSON_TYPE = '在岗员工' AND WORK_STATE = '正常'";
		String where = " AND S_FLAG = '1' AND WORK_STATE != '离职' AND WORK_STATE != '死亡'";
		queryCount.set(Constant.PARAM_WHERE, where);

		int count = ServDao.count(HRM_ZDSTAFF_V, queryCount);
		
//		Transaction.begin();
		
		for (int i = 1; i <= count / QUERY_NUM + 1; i++) {
			log.debug("开始第 " + i + "页");
			ParamBean queryBean = new ParamBean();
			
			queryBean.setNowPage(i); // 当前页数
			queryBean.setShowNum(QUERY_NUM); // 每页条数
			queryBean.setQueryExtWhere(where);
			
			Bean resultBean = ServMgr.act(HRM_ZDSTAFF_V, ServMgr.ACT_QUERY , queryBean);
			List<Bean> resultList = resultBean.getList(Constant.RTN_DATA);
			
			List<Bean> userList = new ArrayList<Bean>(resultList.size());
			for (Bean userBean : resultList) {
				Bean newUser = addUser(userBean, null);

				// 用户统一初始密码,123456
				newUser.set("USER_PASSWORD", "e10adc3949ba59abbe56e057f20f883e");
				
				if (executedData.containsKey(newUser.getStr("USER_CODE"))) {
					// 存在多条重复主键数据，跳过
					log.error("import SY_ORG_USER error ! " + "跳过重复主键数据：" + newUser);
					OrgLogMgr.orgLogSave("", "CC_DATA_IMPORT", "import SY_ORG_USER error ! 跳过重复主键数据：" + newUser,
							"跳过重复主键数据：" + newUser);
				} else {
					executedData.put(newUser.getStr("USER_CODE"), true);
					userList.add(newUser);
				}
			}
			
			ServDao.creates(ServMgr.SY_ORG_USER, userList);
			
//			Transaction.commit();
		}
		
//		Transaction.end();
	}
	
	
	/**
	 * 增量导入用户数据
	 */
	public void addUserDatas(String smtime) throws Exception {
		Bean queryCount = new Bean(); // 统计总数
		String where = " AND 1 = 1 AND S_MTIME = '" + smtime + "'";
		// 统计本次增量的数据量
		queryCount.set(Constant.PARAM_WHERE, where);
		int count = ServDao.count(HRM_ZDSTAFF_V, queryCount);
		
//		Transaction.begin();
		
		for (int i = 1; i <= count / QUERY_NUM + 1; i++) {
			log.debug("开始第 " + i + "页");
			ParamBean queryBean = new ParamBean();
			
			queryBean.setNowPage(i); // 当前页数
			queryBean.setShowNum(QUERY_NUM); // 每页条数
			queryBean.setQueryExtWhere(where);
			
			Bean resultBean = ServMgr.act(HRM_ZDSTAFF_V, ServMgr.ACT_QUERY , queryBean);
			List<Bean> resultList = resultBean.getList(Constant.RTN_DATA);
			
			List<Bean> userList = new ArrayList<Bean>();
			for (Bean userBean : resultList) {
				// 查询本系统中是否有当前用户信息
				Bean oldUser = ServDao.find(ServMgr.SY_ORG_USER, userBean.getStr("PERSON_ID"));
//				Bean oldUser = ServDao.find(ServMgr.SY_ORG_USER, userBean.getStr("SSIC_ID"));
				Bean newUser = null; // 构造的新用户Bean
				
				if (oldUser == null) { // 没有当前用户，添加状态
					newUser = addUser(userBean, null);
				} else { // 有当前用户，修改状态
					newUser = addUser(userBean, oldUser);
				}
				// 添加到更新集合中
				userList.add(newUser);
			}
			
			ParamBean batchSave = new ParamBean();
			batchSave.setBatchSaveDatas(userList);
			ServMgr.act(ServMgr.SY_ORG_USER, ServMgr.ACT_BATCHSAVE, batchSave);
			
//			Transaction.commit();
		}
		
//		Transaction.end();
	}
	
	/**
	 * 将中间表中的人员数据转换成本系统人员数据Bean
	 * @param userBean - 中间表人员数据
	 * @return - 本系统人员数据Bean
	 */
	private Bean addUser(Bean userBean, Bean oldUserBean) throws Exception {
		// 用户数据Bean
		ParamBean user = new ParamBean();
		
		// 构造数据Bean
		user
			.set("USER_CODE", userBean.getStr("PERSON_ID"))
//			.set("USER_CODE", userBean.getStr("SSIC_ID"))
			.set("USER_NAME", userBean.getStr("NAME"))
			.set("USER_LOGIN_NAME", userBean.getStr("SSIC_ID"))
			.set("DEPT_CODE", userBean.getStr("STRU_ID"))
			.set("USER_SORT", userBean.getStr("SORT"))
			.set("USER_HOME_PHONE", userBean.getStr("HOME_PHONE1"))
			.set("USER_MOBILE", userBean.getStr("MOBILE_PHONE1"))
			.set("USER_POST", userBean.getStr("DUTY_LV"))
			.set("USER_ROOM", userBean.getStr("OFFICE_ROOMNO"))
			.set("USER_WORK_NUM", userBean.getStr("SSIC_ID"))
			.set("USER_BIRTHDAY", userBean.getStr("BIRTHDAY"))
			.set("USER_OFFICE_PHONE", userBean.getStr("OFFICE_PHONE1"))
			.set("USER_NATION", userBean.getStr("FOLK"))
			.set("USER_HOME_LAND", userBean.getStr("NATIVE"))
			.set("USER_POLITICS", userBean.getStr("POLITY"))
			.set("USER_MARRIAGE", userBean.getStr("MARRIAGE"))
			.set("USER_WORK_DATE", userBean.getStr("WORKING_DATE"))
			.set("USER_CMPY_DATE", userBean.getStr("ICBC_DATE"))
			.set("USER_EXPIRE_DATE", 9999)
			.set("USER_PASSWORD_DATE", 9999)
			.set("USER_EN_NAME", userBean.getStr("ENAME"))
			.set("USER_SHORT_NAME", userBean.getStr("SNAME"))
//			.set("USER_FROM", "ICBC-" + userBean.getStr("PERSON_ID"));
			.set("USER_FROM", "ICBC")
			.set("CMPY_CODE", NImpUser.CMPY_CODE);
		
		// 邮箱去掉最后的逗号
		String userEmail = userBean.getStr("EMAIL").trim();
		if (userEmail.length() > 1 && userEmail.endsWith(",")) { // 以逗号结尾
			userEmail = userEmail.substring(0, userEmail.length() - 1);
		}
		// 不为空时更新邮箱，为空时保留老邮箱
		if (StringUtils.isNotEmpty(userEmail)) {
			user.set("USER_EMAIL", userEmail);
			user.set("USER_EDU_SCHOOL", userEmail.toUpperCase()); // 先将邮箱大写信息放到这个字段中，这个字段没有用
		}
		
		// 性别字段
		if (userBean.getStr("SEX").equals("女")) {
			user.set("USER_SEX", 2);
		} else {
			user.set("USER_SEX", 1);
		}
		
		// 处理S_FLAG标识
		if (userBean.getStr("PERSON_TYPE").equals("在岗员工") || userBean.getStr("WORK_STATE").equals("正常")) {
			user.set("S_FLAG", Constant.YES_INT);
		} else {
			user.set("S_FLAG", Constant.NO_INT);
		}
		// 如果源数据已经设置为删除，就覆盖S_FLAG值为删除状态
		if (userBean.getInt("S_FLAG") == 2) {
			user.set("S_FLAG", 2);
		}
		
		// 如果本系统没有用户信息，说明是添加模式
		if (oldUserBean == null) {
			user.setAddFlag(true);
		} else { // 修改模式
			user.setId(userBean.getStr("PERSON_ID"));
//			user.setId(userBean.getStr("SSIC_ID"));
		}
		
		return user;
	}
	
	/**
	 * 清除SY_ORG_USER表中ICBC的数据
	 */
	public void cleanUserData() throws Exception {
		ParamBean whereBean = new ParamBean();
		 whereBean.setWhere(" AND CMPY_CODE = '" + NImpUser.CMPY_CODE + "' AND USER_LOGIN_NAME != 'admin'");
		 ServDao.destroys(ServMgr.SY_ORG_USER, whereBean);
		 log.info("table SY_ORG_USER is clean !");
	}
	
	/**
	 * 初始化管理员角色中人员
	 */
	public void initAdminRole(ParamBean paramBean) throws Exception {
		// 需要添加的统一认证号
//		String userSSICIDs = Context.getSyConf("CC_INIT_ADMIN_ROLE_SSICIDS", "");
		String userSSICIDs = paramBean.getStr("userSSICIDs");
		
		if (userSSICIDs.length() > 0) {
			// 查询指定统一认证号的人员ID
			SqlBean queryUser = new SqlBean();
			queryUser.selects("USER_CODE");
			queryUser.andIn("USER_LOGIN_NAME", userSSICIDs.split(","));
			queryUser.and("S_FLAG", 1);
			queryUser.and("CMPY_CODE", NImpUser.CMPY_CODE);
			List<Bean> userList = ServDao.finds(ServMgr.SY_ORG_USER, queryUser);
			List<String> delUserCodeList = new ArrayList<String>();
			for (Bean userBean : userList) {
				delUserCodeList.add(userBean.getStr("USER_CODE"));
			}
			
			// 删除已有的关系数据
			SqlBean delParam = new SqlBean();
			delParam.and("CMPY_CODE", NImpUser.CMPY_CODE);
			delParam.and("ROLE_CODE", "RADMIN");
			delParam.andIn("USER_CODE", delUserCodeList.toArray());
			ServDao.destroys(ServMgr.SY_ORG_ROLE_USER, delParam);
			
//			Transaction.commit();
			
			// 添加数据集合
			List<Bean> addBeanList = new ArrayList<Bean>();
			
			// 构造保存数据Bean
			for (Bean bean : userList) {
				Bean addBean = new Bean();
				addBean.set("USER_CODE", bean.getStr("USER_CODE"));
				addBean.set("ROLE_CODE", "RADMIN");
				addBean.set("CMPY_CODE", NImpUser.CMPY_CODE);
				addBean.set("S_USER", NImpUser.S_USER);
				addBean.set("S_FLAG", 1);
				addBeanList.add(addBean);
			}
			
			// 批量保存
			ServDao.creates(ServMgr.SY_ORG_ROLE_USER, addBeanList);
			
//			Transaction.commit();
		}
	}
}
