package com.rh.ts.xmgl.rule;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Strings;
import com.rh.ts.util.TsConstant;

public class RuleServ extends CommonServ {

	/**
	 * 验证单条规则
	 * 
	 * @param className
	 * @param paramBean
	 * @return
	 */
	private boolean vlidateOne(String className, Bean param) {

		return RuleMgr.getInstance(className).validate(param);
	}

	/**
	 * 验证考试规则
	 * 
	 * @param paramBean
	 * @return
	 * @throws JSONException
	 */

	public OutBean vlidates(ParamBean paramBean) {

		OutBean outBean = new OutBean();

		try {

			List<Bean> passList = new ArrayList<Bean>();// 验证结果

			String infostr = paramBean.getStr("BM_INFO");

			String liststr = paramBean.getStr("BM_LIST");

			JSONObject bmObj = new JSONObject(infostr);

			Bean bmInfo = jsonToBean(bmObj);

			String xmId = bmInfo.getStr("XM_ID");

			if (Strings.isBlank(xmId)) {
				throw new TipException("XM_ID为空，不能验证报名规则!");
			}

			Bean gzmxBean = getGzMx(xmId); // 规则明细bean key:考试群组

			Bean gzBean = getGz(xmId); // 规则bean key:规则id

			JSONArray objList = new JSONArray(liststr);

			for (int i = 0; i < objList.length(); i++) {

				JSONObject obj = (JSONObject) objList.get(i);

				Bean bmBean = jsonToBean(obj);

				String msg = ""; // 验证信息

				boolean pass = false;

				SqlBean sql = new SqlBean();
				sql.and("KSLB_NAME", bmBean.getStr("BM_LB"));// 类别
				sql.and("KSLB_XL", bmBean.getStr("BM_XL"));// 序列
				sql.and("KSLB_MK", bmBean.getStr("BM_MK"));// 模块
				sql.and("KSLB_TYPE", bmBean.getStr("BM_TYPE"));// 级别
				sql.and("XM_ID", xmId); // 项目ID

				List<Bean> kslbList = ServDao.finds(TsConstant.SERV_BM_KSLB, sql); // 报名的考试类别

				if (kslbList == null || kslbList.size() == 0) {
					return outBean.setError("没有找到该考试类别");
				}

				Bean kslb = kslbList.get(0);

				String ksqzId = kslb.getStr("KSQZ_ID"); // 考试群组id

				List<Bean> mxList = gzmxBean.getList(ksqzId); // 规则明细list

				Bean gzmxGroup = convertGzmxToGroup(mxList); // 规则明细分组Bean
																// key:规则组

				for (Object gzId : gzmxGroup.keySet()) {

					Bean shgz = gzBean.getBean(gzId); // 审核规则bean

					List<Bean> gzmxGroupList = gzmxGroup.getList(gzId); // 审核规则关联的规则明细list

					for (Bean bean : gzmxGroupList) {

						bean.putAll(bmBean); // 报名考试信息
						bean.putAll(bmInfo); // 报名人信息

						boolean result = this.vlidateOne(bean.getStr("XM_IMPL"), bean); // 执行验证

						if (shgz.getInt("GZ_TYPE") == 1) { // 审核不通过规则(与)

							if (!result) {
								pass = false;
								msg += bean.getStr("MX_NAME") + ";";
							}
						} else if (shgz.getInt("GZ_TYPE") == 2) { // 审核通过规则(或)

							if (result) {
								pass = true;
							} else {
								msg += bean.getStr("MX_NAME") + ";";
							}
						} else {
							pass = false;
							msg += bean.getStr("MX_NAME") + "，审核类型不存在;";
						}

					}

					Bean data = new Bean();

					data.set("NAME", shgz.getStr("GZ_NAME")); // 规则名称

					data.set("VLIDATE", pass);

					data.set("MSG", msg);

					passList.add(data);

				}

				outBean.set(bmBean.getStr("ID"), passList);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return outBean;
	}

	/**
	 * 获取项目规则明细
	 * 
	 * @param xmId
	 * @return Bean key:考试群组
	 */
	private Bean getGzMx(String xmId) {

		Bean gzmxBean = new Bean(); // 规则明细bean key:考试群组

		SqlBean sql = new SqlBean().and("XM_ID", xmId).asc("KSQZ_ID");

		List<Bean> gzmxList = ServDao.finds(TsConstant.SERV_BMSH_SHGZ_MX, sql);// 审核规则明细list

		for (Bean bean : gzmxList) {

			String qzId = bean.getStr("KSQZ_ID");// 考试群组id

			if (gzmxBean.containsKey(qzId)) {
				
				List<Bean> list =  gzmxBean.getList(qzId);
				
				list.add(bean);

				gzmxBean.set(qzId, list);

			} else {
				
				List<Bean> list = new ArrayList<Bean>();
				
				list.add(bean);

				gzmxBean.set(qzId, list);
			}
		}

		return gzmxBean;
	}

	/**
	 * 获取项目规则
	 * 
	 * @param xmId
	 * @return Bean key:规则ID
	 */
	private Bean getGz(String xmId) {

		Bean gzBean = new Bean();

		List<Bean> gzList = getGzList(xmId);

		for (Bean bean : gzList) {

			String gzId = bean.getStr("GZ_ID");

			gzBean.set(gzId, bean);
		}

		return gzBean;
	}

	/**
	 * 获取项目规则
	 * 
	 * @param xmId
	 * @return Bean key:规则ID
	 */
	private List<Bean> getGzList(String xmId) {

		SqlBean sql = new SqlBean().and("XM_ID", xmId);

		List<Bean> gzList = ServDao.finds(TsConstant.SERV_BMSH_SHGZ, sql);// 审核规则list

		return gzList;
	}

	/**
	 * 规则明细 根据规则组 分组
	 * 
	 * @param mxList
	 * @return
	 */
	private Bean convertGzmxToGroup(List<Bean> mxList) {

		Bean groupBean = new Bean();

		for (Bean bean : mxList) {

			String gzId = bean.getStr("GZ_ID");

			if (groupBean.containsKey(gzId)) {

				groupBean.set(gzId, groupBean.getList(gzId).add(bean));

			} else {

				groupBean.set(gzId, new ArrayList<Bean>().add(bean));
			}
		}
		return groupBean;
	}

	private Bean jsonToBean(JSONObject obj) {

		Bean bean = new Bean();

		for (Object key : obj.keySet()) {
			try {
				bean.set(key, obj.getString(String.valueOf(key)));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return bean;
	}

}
