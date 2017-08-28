package com.rh.ts.pvlg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rh.core.base.Bean;

/**
 * 工行考试系统 角色Bean
 * 
 * @author zjl
 *
 */
public class RoleBean extends Bean {

	private static final long serialVersionUID = 5238440127223274800L;

	/**
	 * 私有化构造方法
	 */
	@SuppressWarnings("unused")
	private RoleBean() {
	}

	/**
	 * 对象构造方法
	 * 
	 * @param roleBean
	 *            数据对象
	 */
	public RoleBean(Bean roleBean) {
		super(roleBean);
		this.setId(roleBean.getId());
	}

	/**
	 * 获取角色编码
	 * 
	 * @return 角色编码
	 */
	public String getCode() {
		return this.getStr("ROLE_ID");
	}

	/**
	 * 获取角色名称
	 * 
	 * @return 角色名称
	 */
	public String getName() {
		return this.getStr("ROLE_NAME");
	}

	/**
	 * 角色关联级别 1,2,3,4,5 共五个级别 (ROLE_TYPE值为1情况下有效)
	 * 
	 * @return
	 */
	public int getOrgLv() {
		return this.getInt("ROLE_ORG_LV");
	}

	/**
	 * 获取角色关联机构编码 (ROLE_TYPE值为2情况下有效)
	 * 
	 * @return 关联机构编码
	 */
	public String getDCode() {
		return this.getStr("ROLE_DCODE");
	}

	/**
	 * 获取角色关联机构名称(ROLE_TYPE值为2情况下有效)
	 * 
	 * @return 关联机构名称
	 */
	public String getDName() {
		return this.getStr("ROLE_DNAME");
	}

	/**
	 * 获取角色排序号
	 * 
	 * @return 角色排序号
	 */
	public int getSort() {
		return this.get("ROLE_SORT", 0);
	}

	/**
	 * 获取角色描述
	 * 
	 * @return 角色描述
	 */
	public String getDesc() {
		return this.getStr("ROLE_DESC");
	}

	/**
	 * 获取角色关联关系 1:关联所在部门; 2:自定义关联
	 * 
	 * @return 角色关联关系
	 */
	public int getRoleType() {
		return this.getInt("ROLE_TYPE");
	}

	/**
	 * 获取启用标志
	 * 
	 * @return 启用标志
	 */
	public int getsFlag() {
		return this.get("S_FLAG", 0);
	}

	/**
	 * 获取公司Code
	 * 
	 * @return 公司Code
	 */
	public String getCmpyCode() {
		return this.getStr("CMPY_CODE");
	}

	/**
	 * 获取添加者
	 * 
	 * @return 添加者
	 */
	public String getsUser() {
		return this.getStr("S_USER");
	}

	/**
	 * 获取更新时间
	 * 
	 * @return 更新时间
	 */
	public String getsMtime() {
		return this.getStr("S_MTIME");
	}

	public List<Bean> getOptList() {
		return this.getList("OPT_LIST");
	}

	public Map<String, String> getOptMap() {

		Map<String, String> map = new HashMap<String, String>();

		for (Bean bean : this.getOptList()) {
			map.put(bean.getStr("MD_CODE"), bean.getStr("MD_VAL"));
		}

		return map;
	}

}
