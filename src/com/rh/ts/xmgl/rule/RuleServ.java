package com.rh.ts.xmgl.rule;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
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

		try {

			return RuleMgr.getInstance(className).validate(param);

		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}

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

			if (gzmxBean.isEmpty() || gzBean.isEmpty()) {

				throw new TipException("未绑定审核规则!");
			}

			JSONArray objList = new JSONArray(liststr);

			for (int i = 0; i < objList.length(); i++) {
				
				List<Bean> passList = new ArrayList<Bean>();// 验证结果

				JSONObject obj = (JSONObject) objList.get(i);
				
				if (obj.length() == 0) {
					continue;
				}

				Bean bmBean = jsonToBean(obj);

				SqlBean sql = new SqlBean();
				if(!bmBean.getStr("BM_MK").equals("")){
				sql.and("KSLB_CODE", bmBean.getStr("BM_LB"));// 类别
				sql.and("KSLB_XL_CODE", bmBean.getStr("BM_XL"));// 序列
				sql.and("KSLB_MK_CODE", bmBean.getStr("BM_MK"));// 模块
				sql.and("KSLB_TYPE", bmBean.getStr("BM_TYPE"));// 级别
				sql.and("XM_ID", xmId); // 项目ID
				}if(bmBean.getStr("BM_MK").equals("")){
					sql.and("KSLB_CODE", bmBean.getStr("BM_LB"));// 类别
					sql.and("KSLB_XL_CODE", bmBean.getStr("BM_XL"));// 序列
					sql.and("KSLB_MK", "-1");// 模块
					sql.and("KSLB_TYPE", bmBean.getStr("BM_TYPE"));// 级别
					sql.and("XM_ID", xmId); // 项目ID
					}
				List<Bean> kslbList = ServDao.finds(TsConstant.SERV_BM_KSLB, sql); // 报名的考试类别

				if (kslbList != null && kslbList.size() != 0) {

					String ksqzId = kslbList.get(0).getStr("KSQZ_ID"); // 考试群组id

					List<Bean> mxList = gzmxBean.getList(ksqzId); // 规则明细list

					Bean gzmxGroup = convertGzmxToGroup(mxList); // 规则明细分组Bean,key:规则组
					
					for (Object gzId : gzmxGroup.keySet()) { // 遍历规则组

						Bean shgz = gzBean.getBean(gzId); // 审核规则bean
						if(shgz.size()==0||"N03".equals(shgz.getStr("GZK_ID"))){
							continue;
						}
						int gzType = shgz.getInt("GZ_TYPE");

						List<Bean> gzmxGroupList = gzmxGroup.getList(gzId); // 审核规则关联的规则明细list
						
						boolean pass = gzType == 1 ? true : false; // 审核不通过规则(与)默认ture，否则默认false

						String msg = ""; // 验证信息

						for (Bean bean : gzmxGroupList) {
							

							bean.putAll(bmBean); // 报名考试信息
							bean.putAll(bmInfo); // 报名人信息
							

							String mxName = getMxName(bean);
							String clazz = bean.getStr("MX_IMPL");

							boolean result = this.vlidateOne(clazz, bean); // 执行验证

							if (gzType == 1) { // 审核不通过规则(与)

								if (!result) {
									pass = false;
									msg += mxName + ";";
								}
							} else if (gzType == 2) { // 审核通过规则(或)

								if (result) {
									pass = true;
								} else {
									msg += mxName + ";";
								}
							} else {
								pass = false;
								msg += mxName + "，审核类型不存在;";
							}

						}

						Bean data = new Bean();

						data.set("NAME", shgz.getStr("GZ_NAME")); // 规则名称

						data.set("VLIDATE", pass);

						data.set("MSG", msg);

						passList.add(data);

					}

				} else {
					Bean data = new Bean();
					
					data.set("NAME", "无审核规则"); // 规则名称

					data.set("VLIDATE", false);

					data.set("MSG", "未找到该考试类别,审核不通过");

					passList.add(data);
				}

				outBean.set(bmBean.getStr("ID"), passList);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			outBean.setError(e.getMessage());
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

				List<Bean> list = gzmxBean.getList(qzId);

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

				List<Bean> list = groupBean.getList(gzId);

				list.add(bean);

				groupBean.set(gzId, list);

			} else {

				List<Bean> list = new ArrayList<Bean>();

				list.add(bean);

				groupBean.set(gzId, list);
			}
		}
		return groupBean;
	}

	private String getMxName(Bean bean) {
		
		String name = bean.getStr("MX_NAME");
		String jsonStr = bean.getStr("MX_VALUE2");
		String type = bean.getStr("MX_VALUE1");

		if (type.equals("1")) {
			try {
				JSONArray jsonarray = new JSONArray(jsonStr); 
				 // 过时方法
				for(int i=0;i<jsonarray.length();i++){
					JSONObject obj = jsonarray.getJSONObject(i);
					String var = obj.getString("vari");
					String val = obj.getString("val");
					String repacle = "#" + var + "#";
					name = name.replace(repacle, val);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return name;
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
