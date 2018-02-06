package com.rh.ts.xmgl.rule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.comm.ConfMgr;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.ImpUtils;
import com.rh.core.util.Strings;
import com.rh.core.util.var.OrgVar;
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
				return new OutBean().set("none", "true");				
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
						String str = "";
						Bean shgz = gzBean.getBean(gzId); // 审核规则bean
						if(shgz.size()==0||"Y09".equals(shgz.getStr("GZK_ID"))||"N03".equals(shgz.getStr("GZK_ID"))){
							continue;
						}
						int gzType = shgz.getInt("GZ_TYPE");

						List<Bean> gzmxGroupList = gzmxGroup.getList(gzId); // 审核规则关联的规则明细list
						
						boolean pass = gzType == 1 ? true : false; // 审核不通过规则(与)默认ture，否则默认false

						String msg = ""; // 验证信息

						boolean flag = false;
						Bean data = new Bean();
						List<Bean> littlegz = new ArrayList<Bean>();
						for (Bean bean : gzmxGroupList) {
							Bean littelGzBean = new Bean();
							bean.putAll(bmBean); // 报名考试信息
							bean.putAll(bmInfo); // 报名人信息
							String mx_name = bean.getStr("MX_NAME");
							String mx_value2 = bean.getStr("MX_VALUE2");
							String littlemxname = mx_name;
							JSONArray mxvaluearr;
							
							String mxName = getMxName(bean);
							String clazz = bean.getStr("MX_IMPL");
							boolean result=false;
							if(mx_name.indexOf("XL")!=-1){
								//启用管理类规则
								result = this.vlidateOne(clazz, bean);
								if(result==true){
									flag=true;
									result=false;
								}
								/*littelGzBean.set("name", obj);*/
							}else if(bean.getStr("MX_VALUE2").indexOf("rzyear")!=-1){
								//管理员任职年限提示
								str =mx_name;
								result=false;
							}else{
								result = this.vlidateOne(clazz, bean); // 执行验证
								try {
									if("".equals(mx_value2)){
										littelGzBean.set("validate", result);
										littelGzBean.set("name",littlemxname);
									}else{
										mxvaluearr = new JSONArray(mx_value2);
										for(int a=0;a<mxvaluearr.length();a++){
											String vari = mxvaluearr.getJSONObject(a).getString("vari");
											String val = mxvaluearr.getJSONObject(a).getString("val");
											if(">".equals(val)){
												val= mxvaluearr.getJSONObject(a+1).getString("val")+"以上";
												littlemxname=littlemxname.replaceFirst("#"+vari+"#", val);
												littlemxname=littlemxname.replaceFirst("#"+vari+"#", "");
												a++;
											}else if("<".equals(val)){
												val= mxvaluearr.getJSONObject(a+1).getString("val")+"以下";
												littlemxname=littlemxname.replaceFirst("#"+vari+"#", val);
												littlemxname=littlemxname.replaceFirst("#"+vari+"#", "");
												a++;
											}else if("<=".equals(val)){
												val= mxvaluearr.getJSONObject(a+1).getString("val")+"及以下";
												littlemxname=littlemxname.replaceFirst("#"+vari+"#", val);
												littlemxname=littlemxname.replaceFirst("#"+vari+"#", "");
												a++;
											}else if(">=".equals(val)){
												val= mxvaluearr.getJSONObject(a+1).getString("val")+"及以上";
												littlemxname=littlemxname.replaceFirst("#"+vari+"#", val);
												littlemxname=littlemxname.replaceFirst("#"+vari+"#", "");
												a++;
											}else if("=".equals(val)){
												val= mxvaluearr.getJSONObject(a+1).getString("val");
												littlemxname=littlemxname.replaceFirst("#"+vari+"#", val);
												littlemxname=littlemxname.replaceFirst("#"+vari+"#", "");
												a++;
											}else if("gwgz".equals(vari)){
												List<Bean> gwgzlist = ServDao.finds("TS_BMSH_RULE"," AND R_XL='"+bmBean.getStr("BM_XL")+"' AND R_LV ="+bmBean.getStr("BM_TYPE") );
												if(gwgzlist!=null&&gwgzlist.size()!=0){
													String POSTCODE = gwgzlist.get(0).getStr("R_POST_CODE");
													if("".equals(POSTCODE)){
														littlemxname="";
														break;
													}else{
														String[] split = POSTCODE.split(",");
														for (String string : split) {
															Bean find = ServDao.find("ts_org_postion", string);
															if(find!=null){
																littlemxname+=" "+find.getStr("POSTION_NAME");
															}
														}
														littlemxname+=" 可报名此考试";
													}
												}else{
													littlemxname="未设置此考试岗位规则";
													break;
												}
											}else{
												littlemxname=littlemxname.replaceFirst("#"+vari+"#", val);
											}
										}
										
										littelGzBean.set("validate", result);
										littelGzBean.set("name",littlemxname);
									}
								} catch (JSONException e) {

									e.printStackTrace();
								}
							}
							
							littlegz.add(littelGzBean);
							
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
						
						data.set("littlega", littlegz);
						data.set("NAME", shgz.getStr("GZ_NAME")); // 规则名称
						data.set("VLIDATE", pass);

 						data.set("MSG", msg);
						if(flag==true&&pass==false){
							//显示提示语
							data.set("TISHI","TRUE");
							data.set("tishiyu", str);
						}else{
							data.set("TISHI","");
							data.set("tishiyu", str);
						}
						
						if("Y01".equals(shgz.getStr("GZK_ID"))){
							//启用了证书规则
							data.set("zsgz", pass);
						}else{
							//其它证书 有false 则 一定不通过 
							if(!pass){
								data.set("othergz", pass);
							}else{
								data.set("othergz", "");
							}
						}
						passList.add(data);

					}

				//证书条件没有小规则  直接验证
				Bean data = new Bean();
				ParamBean parambean = new ParamBean();
				parambean.set("BM_XL", bmBean.getStr("BM_XL"));
				parambean.set("BM_TYPE", bmBean.getStr("BM_TYPE"));
				parambean.set("BM_CODE", bmInfo.getStr("BM_CODE"));
				parambean.set("QZ_ID", ksqzId);
				Bean resultBean = validbmopt(parambean);
				if(resultBean.getBoolean("gotostay")==true){
					data.set("TISHI","TRUE");
				}
				data.set("littlega", resultBean.getList("littlega"));
				data.set("NAME", resultBean.getStr("GZ_NAME")); // 规则名称
				data.set("VLIDATE", resultBean.getBoolean("VLIDATE"));
				data.set("GZ_ID", resultBean.getStr("GZ_ID"));
				if("".equals(resultBean.getStr("GZ_NAME"))){
				}else{
					passList.add(data);
				}
				Bean data1 = new Bean();
				Bean resBean = validkxl(parambean);
				data1.set("littlega", resBean.getList("littlega"));
				data1.set("NAME", resBean.getStr("GZ_NAME")); // 规则名称
				data1.set("VLIDATE", resBean.getBoolean("VLIDATE"));
				data1.set("GZ_ID", resBean.getStr("GZ_ID"));
				if("".equals(resBean.getStr("GZ_NAME"))){
				}else{
					passList.add(data1);
				}
				} else {
					Bean data = new Bean();
					
					data.set("NAME", "无审核规则"); // 规则名称

					data.set("VLIDATE", true);

					data.set("MSG", "未找到该考试类别,审核不通过");

					passList.add(data);
				}

				outBean.set(bmBean.getStr("ID"), passList);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			outBean.setError(e.getMessage());
		}
		outBean.set("none", "none");
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

		if (type.equals("1")&&!"".equals(jsonStr)) {
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
	/**
	 * 验证报名条件
	 * @param paramBean
	 * @return
	 */
	public OutBean validbmopt (Bean paramBean){
		OutBean outBean = new OutBean();
		Boolean staytogo = false;
		String BM_MK = paramBean.getStr("BM_MK");
		String BM_CODE = paramBean.getStr("BM_CODE");//考生编码
		String BM_XL = paramBean.getStr("BM_XL");//报考序列
		String BM_TYPE = paramBean.getStr("BM_TYPE");//报考类别
		String str = paramBean.getStr("QZ_ID");//群组id
		List<Bean> gzlist = ServDao.finds("TS_XMGL_BMSH_SHGZ", " AND KSQZ_ID = '"+str+"' AND GZK_ID='Y05'");
		List<Bean> finds = new ArrayList<Bean>();//证书规则
		List<Bean> post_list = new ArrayList<Bean>();//职务规则
		if(gzlist!=null&&gzlist.size()!=0){
			post_list = ServDao.finds("TS_BMSH_RULE_POST", "AND GZ_ID ='"+gzlist.get(0).getId()+"'");
			 finds = ServDao.finds("TS_BMSH_RULE", "AND G_ID ='"+gzlist.get(0).getId()+"'");
		}
		if(finds.size()==0&&post_list.size()==0){
			return new OutBean();
		}
		boolean flag = false;
		List<Bean> listbean = new ArrayList<Bean>();
		for (Bean bean : post_list) {
			int post_defined = bean.getInt("POST_DEFINED");//是否指定职务层级
			String success_info = bean.getStr("SUCCESS_INFO");//成功提示信息
			String failer_info = bean.getStr("FAIL_INFO");//成功提示信息
			Bean littelGzBean = new Bean();
			String dept_codes = bean.getStr("DEPT_CODE");//机构
			String zspost = bean.getStr("POST_CODE");//职务
			int gotostay = bean.getInt("GOTO_STAY");//验证失败1去待审核  2.审核不通过
			String post_type = bean.getStr("POST_TYPE"); //类别
			String post_xl = bean.getStr("POST_XL");//序列
			String post_zw = bean.getStr("POST_DUTIES");//没有指定的职务
			int post_fh = bean.getInt("POST_FUHAO");//符号
			int POST_ZD = bean.getInt("POST_ZD");//是否自动验证报考序列
			String post_desc = bean.getStr("POST_DESC");
			//判断 此人是否在此机构内
			String[] codearr = dept_codes.split(",");
			Boolean codeflag = false;
			for (String string : codearr) {
				if(string.equals("0010100000")){
					codeflag = true;
					break;
				}else{
					DeptBean dept = OrgMgr.getDept(string);
					UserBean user = UserMgr.getUser(BM_CODE);
					DeptBean userdept = OrgMgr.getDept(user.getDeptCode());
					if(dept!=null&&userdept!=null){
						if(userdept.getCodePath().indexOf(dept.getCodePath())!=-1){
							//包含此部门
							codeflag = true;
							break;
						}
					}
				}
			}
			if(codeflag){
				
			}else{
				//不包含在此部门
				littelGzBean.set("validate", false);
				littelGzBean.set("name",post_desc+failer_info);
				listbean.add(littelGzBean);
				if(gotostay==1){
					staytogo=true;
				}
				continue;
			}
			

			if(post_defined==1){
			//指定职务层级
				String[] postarr = zspost.split(",");
				Bean find = ServDao.find("sy_hrm_zdstaffposition", BM_CODE);
				if(find!=null&&find.size()!=0){
					String possql="";
					 possql = " AND POSTION_NAME_CODE = '"+find.getStr("DUTY_LV_CODE")+"' AND POSTION_SEQUENCE_ID ='"+find.getStr("STATION_NO_CODE")+"' AND POSTION_TYPE='"+find.getStr("STATION_TYPE_CODE")+"'";
					List<Bean> finds2 = ServDao.finds("ts_org_postion", possql);
					Boolean flagpost = false;
					for (Bean bean2 : finds2) {
						for (String string : postarr) {
							if(bean2.getStr("POSTION_ID").equals(string)){
								flagpost=true;
							}
						}
					}
					if(flagpost){
						//在此层级内
						flag = true;
						littelGzBean.set("validate", true);
					littelGzBean.set("name",post_desc+success_info);
					listbean.add(littelGzBean);
					continue;
					}else{
						if(gotostay==1){
							staytogo=true;
						}
						littelGzBean.set("validate", false);
						littelGzBean.set("name",post_desc+failer_info);
						listbean.add(littelGzBean);
						continue;
					}
				}else{
					if(gotostay==1){
						staytogo=true;
					}
					littelGzBean.set("validate", false);
					littelGzBean.set("name",post_desc+failer_info);
					listbean.add(littelGzBean);
					continue;
				}
			}else{
				
				Bean find = ServDao.find("sy_hrm_zdstaffposition", BM_CODE);
				if(find!=null&&find.size()!=0){
					String possql="";
					 possql = " AND POSTION_NAME_CODE = '"+find.getStr("DUTY_LV_CODE")+"' AND POSTION_SEQUENCE_ID ='"+find.getStr("STATION_NO_CODE")+"' AND POSTION_TYPE='"+find.getStr("STATION_TYPE_CODE")+"'";
					List<Bean> finds2 = ServDao.finds("ts_org_postion", possql);
					Boolean flagpost = false;
				List<Bean> postlist = ServDao.finds("TS_ORG_POSTION","AND POSTION_NAME = '"+post_zw+"' AND POSTION_TYPE='"+post_type+"'");
				Bean findbean = null;
				if(postlist.size()!=0){
					 findbean = postlist.get(0);
				}
				SqlBean sql = new SqlBean();
				//不指定
				if(findbean!=null){
				if(post_fh==1){
					sql.andGT("POSTION_QUALIFICATION", findbean.getInt("POSTION_QUALIFICATION"));
				}else if(post_fh==2){
					sql.andLT("POSTION_QUALIFICATION",  findbean.getInt("POSTION_QUALIFICATION"));
				}else if(post_fh==3){
					sql.and("POSTION_QUALIFICATION",  findbean.getInt("POSTION_QUALIFICATION"));
				}else if(post_fh==4){
					sql.andGTE("POSTION_QUALIFICATION",  findbean.getInt("POSTION_QUALIFICATION"));
				}else if(post_fh==5){
					sql.andLTE("POSTION_QUALIFICATION",  findbean.getInt("POSTION_QUALIFICATION"));
				}
			/*	if(POST_ZD==1){
					//自动验证报考序列
					sql.and("POSTION_SEQUENCE_ID", find.getStr("STATION_NO_CODE"));
					sql.and("POSTION_TYPE", find.getStr("STATION_TYPE_CODE"));
				}else{*/
					//指定序列验证
					if(!"".equals(post_xl)){
						sql.and("POSTION_SEQUENCE_ID", post_xl);
					}
					sql.and("POSTION_TYPE", post_type);
					
				}else{
					if(gotostay==1){
						staytogo=true;
					}
					littelGzBean.set("validate", false);
					littelGzBean.set("name",post_desc+failer_info);
					listbean.add(littelGzBean);
					continue;
				}
				List<Bean> findlist = ServDao.finds("TS_ORG_POSTION", sql);
				if(findlist!=null&&findlist.size()!=0){
					for (Bean bean2 : finds2) {
						for (Bean posbean : findlist) {
							if(bean2.getStr("POSTION_ID").equals(posbean.getStr("POSTION_ID"))){
								flagpost=true;
							}
						}
					}
					if(flagpost){
						//在此层级内
						flag = true;
						littelGzBean.set("validate", true);
					littelGzBean.set("name",post_desc+success_info);
					listbean.add(littelGzBean);
					continue;
					}
		}
				}
				if(gotostay==1){
					staytogo=true;
				}
				littelGzBean.set("validate", false);
				littelGzBean.set("name",post_desc+failer_info);
				listbean.add(littelGzBean);
				continue;
			}
			}
		for (Bean bean : finds) {
			Date newdate  = new Date();//当前时间
			String tishixin = "";
			Bean littelGzBean= new Bean();
			//查找当前人的职务层级
			//验证最小模块
			String zslb = bean.getStr("R_LB");//证书类别
			String zsxl = bean.getStr("R_XL");//证书序列
			String zsmk = bean.getStr("R_MK");//证书模块
			String zsdj = bean.getStr("R_LV");//证书等级   //为空时不进行证书的校验
			String zslv = bean.getStr("R_TYPE");//证书获证类型  1 与  2 或
			int zsvalid = bean.getInt("R_YEAR");//证书有效期
			String lxtype = bean.getStr("R_CERT_STAT");//证书类型  有效获取中  无效  
			int zd = bean.getInt("R_ZD");//自动验证  报考序列证书是否 通过
			int ruzhi = bean.getInt("R_POST_CODE");//入职年限
			String success_info = bean.getStr("SUCCESS_INFO");//成功提示信息
			String failer_info = bean.getStr("FAIL_INFO");//成功提示信息
			int R_ZD_MK = bean.getInt("R_ZD_MK");//是否自动验证模块
			int R_ZD_TYPE = bean.getInt("R_ZD_TYPE");//自动验证报考等级
			String rule_nowtime = bean.getStr("RULE_NOWTIME");//精准获证时间1
			int rule_lastyear = bean.getInt("RULE_LASTYEAR");//获证年前1
			String start_date = bean.getStr("VALID_START");//时间1
			int rule_cengji	= bean.getInt("RULE_CENGJI");//时间类型1
			
			String condition_one_time = bean.getStr("CONDITION_ONE_TIME");//精准时间2
			String condition_time = bean.getStr("CONDITION_TIME");//时间2
			int condition_year = bean.getInt("CONDITION_YEAR");//获证年前2
			int condition_one_type	= bean.getInt("CONDITION_ONE_TYPE");//时间类型2
			
			String end_date = bean.getStr("VALID_END");//精准时间3
			String condition_two_time = bean.getStr("CONDITION_TWO_TIME");//时间3
			int condition_two_type	= bean.getInt("CONDITION_TWO_TYPE");//时间类型3
			int condition_two_year = bean.getInt("CONDITION_TWO_YEAR");//获证年前3
			
			int condition_two_end_type	= bean.getInt("CONDITION_TWO_END_TYPE");//时间类型4
			String condition_two_timetw = bean.getStr("CONDITION_TWO_TIMETW");//精准时间4
			int  condition_two_end_year = bean.getInt("CONDITION_TWO_END_YEAR");//获证年前4
			String CONDITION_TWO_END_TIME = bean.getStr("CONDITION_TWO_END_TIME");//获证时间4
			
			int gotostay = bean.getInt("GOTO_STAY");//是否去待审核
			int cy = bean.getInt("R_RZYEAR");//从业年限
			String RULE_CY = bean.getStr("RULE_CY");//验证从业序列
			int RULE_MAST = bean.getInt("RULE_MAST");//是否担任管理类职务
			String RULE_DESC = bean.getStr("RULE_DESC");
			if(RULE_MAST==1){//担任管理类职务
				SqlBean adminbean = new SqlBean();
				adminbean.set("STATION_TYPE_CODE", "023001");
				adminbean.set("STATION_TYPE_CODE", "023001");
				adminbean.and("PERSON_ID", BM_CODE);
				int adcount = ServDao.count("sy_hrm_zdstaffposition", adminbean);
				if(adcount<=0){//不是管理类职务
					if(gotostay==1){
						staytogo=true;
					}
					littelGzBean.set("validate", false);
					littelGzBean.set("name",RULE_DESC+failer_info);
					listbean.add(littelGzBean);
					continue;
				}
			}
			if(ruzhi==0){
			}else{
				SqlBean ruzhibean = new SqlBean();
				Calendar c = Calendar.getInstance();

				int valfu = -ruzhi;
				
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				c.setTime(newdate);

				c.add(Calendar.YEAR, valfu);

				Date y = c.getTime();

				String yearago = format.format(y);
				ruzhibean.andLTE("USER_CMPY_DATE", yearago);
				ruzhibean.and("USER_CODE", BM_CODE);
				int count = ServDao.count("SY_ORG_USER",ruzhibean);
				if(count<=0){
					if(gotostay==1){
						staytogo=true;
					}
					littelGzBean.set("validate", false);
					littelGzBean.set("name",RULE_DESC+failer_info);
					listbean.add(littelGzBean);
					continue;
				}
			}
			SqlBean sqlbean = new SqlBean();
			/*ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, new ParamBean().setWhere(""));*/
			SqlBean cybean = new SqlBean();
			if(1==zd){ //自动验证报考考试
				zsxl=BM_XL;
				zsdj=BM_TYPE;
				if(R_ZD_MK==1){
					zsmk = BM_MK;
				}
			}else{
				if(!"".equals(zsxl)){
					SqlBean xlsql = new SqlBean();
					xlsql.andIn("KSLBK_ID", zsxl.split(","));
					List<Bean> finds2 = ServDao.finds("TS_XMGL_BM_KSLBK", xlsql);
					zsxl="";
					for(int i=0;i<finds2.size();i++){
						if(i==0){
							zsxl+=finds2.get(i).getStr("KSLBK_XL_CODE");
						}else{
							zsxl+=","+finds2.get(i).getStr("KSLBK_XL_CODE");
						}
					}
				}
			}
			/*String zspost = bean.getStr("R_POST_NAME");//职务层级
*/			//先判断从业年限
		
			if(cy==0){
				
			}else{
				if(!"".equals(RULE_CY)){
					SqlBean xlsql = new SqlBean();
					xlsql.andIn("KSLBK_ID", zsxl.split(","));
					List<Bean> finds2 = ServDao.finds("TS_XMGL_BM_KSLBK", xlsql);
					String cyxl="";
					String cylb="";
					for(int i=0;i<finds2.size();i++){
						if(i==0){
							if("".equals(finds2.get(i).getStr("KSLBK_XL_CODE"))){
								cylb+=finds2.get(i).getStr("KSLBK_CODE");
							}else{
								cyxl+=finds2.get(i).getStr("KSLBK_XL_CODE");
							}
						}else{
							if("".equals(finds2.get(i).getStr("KSLBK_XL_CODE"))){
								cylb+=","+finds2.get(i).getStr("KSLBK_CODE");
							}else{
								cyxl+=","+finds2.get(i).getStr("KSLBK_XL_CODE");
							}
						}
					}
					if(!"".equals(cylb)){
						cybean.andIn("STATION_TYPE_CODE", cylb.split(","));
					}
					if(!"".equals(cyxl)){
						cybean.andIn("STATION_NO_CODE", cyxl.split(","));
					}
					cybean.and("PERSON_ID", BM_CODE);
					//从业当前职位满多少年
					//当前时间减去年数
					Calendar c = Calendar.getInstance();
					
					int valfu = -cy;
					
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
					c.setTime(newdate);
					
					c.add(Calendar.YEAR, valfu);
					
					Date y = c.getTime();
					
					String yearago = format.format(y);
					
					cybean.andLTE("HOLD_TIME", yearago);// 开始日期小于等于
					int cycount = ServDao.count("sy_hrm_zdstaffposition", cybean);
					if(cycount<=0){//从业年限不符合条件
						if(gotostay==1){
							staytogo=true;
						}
						littelGzBean.set("validate", false);
						littelGzBean.set("name",RULE_DESC+failer_info);
						listbean.add(littelGzBean);
					}
					
				}
			}
			if(!"".equals(zsmk)){
				sqlbean.and("STU_PERSON_ID", BM_CODE);
				if(!"".equals(lxtype)){
					sqlbean.andIn("QUALFY_STAT", lxtype.split(","));
				}
				if(!"".equals(zsdj)){
					if(R_ZD_TYPE==1){
						sqlbean.and("CERT_GRADE_CODE", zsdj);
					}else if(R_ZD_TYPE==2){
						sqlbean.andLT("CERT_GRADE_CODE", zsdj);
					}else if(R_ZD_TYPE==3){
						sqlbean.andLTE("CERT_GRADE_CODE", zsdj);
					}else if(R_ZD_TYPE==4){
						sqlbean.andGT("CERT_GRADE_CODE", zsdj);
					}else if(R_ZD_TYPE==5){
						sqlbean.andGTE("CERT_GRADE_CODE", zsdj);
					}
				}
				//判断证书有效期
				if(0==zsvalid){
				}else{
					sqlbean.andGTE("YEAR(END_DATE)-YEAR(BGN_DATE)", zsvalid);
				}
				sqlbean.andIn("CERT_MODULE", zsmk.split(","));
				
			}else{
			if(!"".equals(zsxl)){
				sqlbean.and("STU_PERSON_ID", BM_CODE);
				//是否是 自动获取 报名的考试
					if(!"".equals(lxtype)){
						sqlbean.andIn("QUALFY_STAT", lxtype.split(","));
					}
					if(!"".equals(zsdj)){
						sqlbean.andIn("CERT_GRADE_CODE", zsdj.split(","));
					}
					//判断证书有效期
					if(0==zsvalid){
					}else{
						sqlbean.andGTE("YEAR(END_DATE)-YEAR(BGN_DATE)", zsvalid);
					}
			}else{
				if(!"".equals(zslb)){
					sqlbean.and("STU_PERSON_ID",BM_CODE);
					sqlbean.andIn("STATION_TYPE", zslb.split(","));
					if(!"".equals(lxtype)){
						sqlbean.andIn("QUALFY_STAT", lxtype.split(","));
					}
					if(!"".equals(zsdj)){
						sqlbean.andIn("CERT_GRADE_CODE", zsdj.split(","));
					}
					//判断证书有效期
					if(0==zsvalid){
					}else{
						sqlbean.andGTE("YEAR(END_DATE)-YEAR(BGN_DATE)", zsvalid);
					}
					
				}else{
				}
			}
			}
			SimpleDateFormat simp = new SimpleDateFormat("yyyy");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			int year = Integer.parseInt(simp.format(date));//当前年限 yyyy
			String datestr = sdf.format(date);//当前时间yyyy-mm-dd
			
			if(rule_cengji==1){//获证时间类型
				if(!"".equals(rule_nowtime)){//证书时间判断  
					int CON_ONE = bean.getInt("CONDITION_ONE");
					if(CON_ONE==1){
						sqlbean.andGT("BGN_DATE", rule_nowtime);
					}else if(CON_ONE==2){
						sqlbean.andGTE("BGN_DATE", rule_nowtime);
					}
				}
			}else if(rule_cengji==2){
				if(!"".equals(start_date)){//证书时间判断  
					int year1 = year-rule_lastyear;
					int CON_ONE = bean.getInt("CONDITION_ONE");
					if(CON_ONE==1){
						sqlbean.andGT("BGN_DATE", year1+"-"+start_date);
					}else if(CON_ONE==2){
						sqlbean.andGTE("BGN_DATE", year1+"-"+start_date);
					}
				}
				
			}else if(rule_cengji==3){
				int CON_ONE = bean.getInt("CONDITION_ONE");
				if(CON_ONE==1){
					sqlbean.andGT("BGN_DATE", datestr);
				}else if(CON_ONE==2){
					sqlbean.andGTE("BGN_DATE", datestr);
				}
			}
			

			if(condition_one_type==1){//获证时间类型
				if(!"".equals(condition_one_time)){//证书时间判断  
					int CON_ONE = bean.getInt("CONDITION_ONE_END");
					if(CON_ONE==1){
						sqlbean.andLT("BGN_DATE", condition_one_time);
					}else if(CON_ONE==2){
						sqlbean.andLTE("BGN_DATE", condition_one_time);
					}
				}
			}else if(condition_one_type==2){
				if(!"".equals(condition_time)){//证书时间判断  
					int year2 = year-condition_year;
					int CON_ONE = bean.getInt("CONDITION_ONE_END");
					if(CON_ONE==1){
						sqlbean.andLT("BGN_DATE", year2+"-"+condition_time);
					}else if(CON_ONE==2){
						sqlbean.andLTE("BGN_DATE", year2+"-"+condition_time);
					}
				}
				
			}else if(condition_one_type==3){
				int CON_ONE = bean.getInt("CONDITION_ONE_END");
				if(CON_ONE==1){
					sqlbean.andLT("BGN_DATE", datestr);
				}else if(CON_ONE==2){
					sqlbean.andLTE("BGN_DATE", datestr);
				}
			}
			
			
			if(condition_two_type==1){//获证时间类型
				if(!"".equals(end_date)){//证书时间判断  
					int CON_ONE = bean.getInt("CONDITION_TWO");
					if(CON_ONE==1){
						sqlbean.andGT("END_DATE", end_date);
					}else if(CON_ONE==2){
						sqlbean.andGTE("END_DATE", end_date);
					}
				}
			}else if(condition_two_type==2){
				if(!"".equals(condition_two_time)){//证书时间判断  
					int year2 = year-condition_two_year;
					int CON_ONE = bean.getInt("CONDITION_TWO");
					if(CON_ONE==1){
						sqlbean.andGT("END_DATE", year2+"-"+condition_two_time);
					}else if(CON_ONE==2){
						sqlbean.andGTE("END_DATE", year2+"-"+condition_two_time);
					}
				}
				
			}else if(condition_two_type==3){
				int CON_ONE = bean.getInt("CONDITION_TWO");
				if(CON_ONE==1){
					sqlbean.andGT("END_DATE", datestr);
				}else if(CON_ONE==2){
					sqlbean.andGTE("END_DATE", datestr);
				}
			}
			
			if(condition_two_end_type==1){//获证时间类型
				if(!"".equals(condition_two_timetw)){//证书时间判断  
					int CON_ONE = bean.getInt("CONDITION_TWO_END");
					if(CON_ONE==1){
						sqlbean.andLT("END_DATE", condition_two_timetw);
					}else if(CON_ONE==2){
						sqlbean.andLTE("END_DATE", condition_two_timetw);
					}
				}
			}else if(condition_two_end_type==2){
				if(!"".equals(CONDITION_TWO_END_TIME)){//证书时间判断  
					int year2 = year-condition_two_end_year;
					int CON_ONE = bean.getInt("CONDITION_TWO_END");
					if(CON_ONE==1){
						sqlbean.andLT("END_DATE", year2+"-"+CONDITION_TWO_END_TIME);
					}else if(CON_ONE==2){
						sqlbean.andLTE("END_DATE", year2+"-"+CONDITION_TWO_END_TIME);
					}
				}
				
			}else if(condition_two_end_type==3){
				int CON_ONE = bean.getInt("CONDITION_TWO_END");
				if(CON_ONE==1){
					sqlbean.andLT("END_DATE", datestr);
				}else if(CON_ONE==2){
					sqlbean.andLTE("END_DATE", datestr);
				}
			}
			
			int count = 0;
			if(!"".equals(zsmk)){
					count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, sqlbean);
			}else{
				if(!"".equals(zsxl)){
					//是否是 自动获取 报名的考试
					//证书类型 是 与还是或
					if("1".equals(zslv)){
						String[] xlarr = zsxl.split(",");
						for (String string : xlarr) {
							sqlbean.and("STATION_NO",string);
							if(!"".equals(zsdj)){
								count += ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, sqlbean);
							}
						}
					}else{
						sqlbean.andIn("STATION_NO", zsxl.split(","));
						if(!"".equals(zsdj)){
							count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, sqlbean);
						}
					}
				}else{
					if(!"".equals(zslb)){//是否验证证书
						count = ServDao.count(TsConstant.SERV_ETI_CERT_QUAL_V, sqlbean);
					}else{
						//不用验证证书
						count=0;
					}
				}
			}
			if(count>0){
				flag = true;
				littelGzBean.set("validate", true);
				littelGzBean.set("name",RULE_DESC+success_info);
				listbean.add(littelGzBean);
			}else{
				if(gotostay==1){
					staytogo=true;
				}
				littelGzBean.set("validate", false);
				littelGzBean.set("name",RULE_DESC+failer_info);
				listbean.add(littelGzBean);
			}
		}
		if(finds.size()!=0||post_list.size()!=0){
			outBean.set("gotostay", staytogo);
			outBean.set("littlega", listbean); 
			outBean.set("VLIDATE", flag);
			outBean.set("GZ_NAME", gzlist.get(0).getStr("GZ_NAME"));
			outBean.set("GZ_ID", "Y05");
		}
		return outBean;
	}
	
	public OutBean validkxl (Bean paramBean){
		OutBean outbean = new OutBean();
		List<Bean> listbean = new ArrayList<Bean>();
		String xl = paramBean.getStr("BM_XL");
		String BM_CODE = paramBean.getStr("BM_CODE");//考生编码
		//判断报名序列 和本人序列是否相同
		String str = paramBean.getStr("QZ_ID");//群组id
		List<Bean> gzlist = ServDao.finds("TS_XMGL_BMSH_SHGZ", " AND KSQZ_ID = '"+str+"' AND GZK_ID='Y09'");
		List<Bean> finds = new ArrayList<Bean>();//跨序列规则
		String gz_name = "";
		if(gzlist!=null&&gzlist.size()!=0){
			gz_name = gzlist.get(0).getStr("GZ_NAME");
			 finds = ServDao.finds("ts_bmsh_rule_kxlgz", "AND GZ_ID ='"+gzlist.get(0).getId()+"'");
		}
		if(finds.size()==0){
			return new OutBean();
		}
		Bean zwbean = ServDao.find("SY_HRM_ZDSTAFFPOSITION", BM_CODE);  //本序列
		
		String user_xl = zwbean.getStr("STATION_NO_CODE");
		if("A000000000000000020".equals(user_xl)){//才会资金序列 报名  考试无  资金  默认 匹配 财会
			user_xl="A000000000000000019";
		}
		Boolean flag = false;
		for (Bean bean : finds) {
			Bean littelGzBean = new Bean();
			String POSTION_TYPE = bean.getStr("POSTION_TYPE");//类别
			String POSTION_SEQUENCE = bean.getStr("POSTION_SEQUENCE");//序列
			String POSTION_NAME = bean.getStr("POSTION_NAME");//职务
			String POSTION_SUCCESS = bean.getStr("POSTION_SUCCESS");//成功提示信息
			String POSTION_FAIL = bean.getStr("POSTION_FAIL");//失败提示信息
			String POSTION_DESC = bean.getStr("POSTION_DESC");//规则描述
			int POSTION_QUALIFICATION = bean.getInt("POSTION_QUALIFICATION");//条件
			int gz_tx_year = bean.getInt("GZ_TX_YEAR");//离退休不满多少年
			
			SqlBean sqlbean = new SqlBean();
			if(!"".equals(POSTION_TYPE)){
				//本人是否在此职务类别中 
				sqlbean.and("PERSON_ID", BM_CODE);
				sqlbean.andIn("STATION_TYPE_CODE", POSTION_TYPE.split(","));
			}
			if(!"".equals(POSTION_SEQUENCE)){
				//职务序列
				sqlbean.andIn("STATION_NO_CODE", POSTION_SEQUENCE.split(","));
			}
			if(!"".equals(POSTION_NAME)){
				List<Bean> find = ServDao.finds("TS_ORG_POSTION", " AND POSTION_NAME='"+POSTION_NAME+"'");
				if(find!=null&&find.size()!=0){
					SqlBean sql = new SqlBean();
					sql.and("POSTION_TYPE", find.get(0).getStr("POSTION_TYPE"));
					sql.and("POSTION_SEQUENCE_ID", find.get(0).getStr("POSTION_SEQUENCE_ID"));
					int type = find.get(0).getInt("POSTION_QUALIFICATION");
					if(POSTION_QUALIFICATION==1){
						sql.andGT("POSTION_QUALIFICATION", type);
					}else if(POSTION_QUALIFICATION==2){
						sql.andLT("POSTION_QUALIFICATION", type);
					}else if(POSTION_QUALIFICATION==3){
						sql.and("POSTION_QUALIFICATION", type);
					}else if(POSTION_QUALIFICATION==4){
						sql.andGTE("POSTION_QUALIFICATION", type);
					}else if(POSTION_QUALIFICATION==5){
						sql.andLTE("POSTION_QUALIFICATION", type);
					}
					List<Bean> finds2 = ServDao.finds("TS_ORG_POSTION", sql);
					if(finds2!=null&&finds2.size()!=0){
						//查找条件数据
						String ids = "";
						for (Bean bean2 : finds2) {
							if("".equals(ids)){
								ids+=bean2.getStr("POSTION_NAME_CODE");
							}else{
								ids+=","+bean2.getStr("POSTION_NAME_CODE");
							}
						}
						if(!"".equals(ids)){
							sqlbean.andIn("DUTY_LV_CODE",ids.split(","));
						/*int count = ServDao.count("sy_hrm_zdstaffposition", sqlbean);
						if(count<=0){
							//不在职务层级内
						}else{
							//在此类职务内
							flag=true;
							littelGzBean.set("validate", true);
							listbean.add(littelGzBean);
							continue;
						}*/
						}
					}
				}
			}
			int count = ServDao.count("sy_hrm_zdstaffposition", sqlbean);
			if(count<=0){
				//不在职务层级内
				littelGzBean.set("validate", false);
				littelGzBean.set("name",POSTION_DESC+POSTION_FAIL);
				listbean.add(littelGzBean);
			}else{
				if(gz_tx_year==0){
					//在此类职务内
					flag=true;
					littelGzBean.set("validate", true);
					littelGzBean.set("name",POSTION_DESC+POSTION_SUCCESS);
					listbean.add(littelGzBean);
				}else{
					UserBean user = UserMgr.getUser(BM_CODE);
					int sex = user.getSex();
					String age = "";
					if(sex==1){
						age = ConfMgr.getConf("TS_MAN_TUIXIU_AGE", "0");
					}else{
						age = ConfMgr.getConf("TS_WOMAN_TUIXIU_AGE", "0");
					}
						//退休几年进行验证
						int txage = Integer.parseInt(age);
						String birthday = user.getBirthday();
							Calendar calendar=Calendar.getInstance();
							int year=calendar.get(Calendar.YEAR);//当前年份
							if(!"".equals(birthday)){//出生日期为空
								birthday = birthday.substring(0,4);
								int nowage = Integer.parseInt(birthday);
								if(txage-(year-nowage)<gz_tx_year){
									flag=true;//离退休时间小于配置年限
									littelGzBean.set("validate", true);
									littelGzBean.set("name",POSTION_DESC+POSTION_SUCCESS);
									listbean.add(littelGzBean);
								}else{
									littelGzBean.set("validate", false);//离退休时间大于配置年限
									littelGzBean.set("name",POSTION_DESC+POSTION_FAIL);
									listbean.add(littelGzBean);
								}
							}else{
								littelGzBean.set("validate", false);
								littelGzBean.set("name",POSTION_DESC+POSTION_FAIL);
								listbean.add(littelGzBean);
							}
				}
			
			}
			
		}

		if(user_xl.equals(xl)){
			Bean littelGzBean = new Bean();
			flag=true;
			littelGzBean.set("validate", true);
			littelGzBean.set("name","报考本序列考试");
			listbean.add(littelGzBean);
		}
		if(flag){
			outbean.set("gotostay", false);
			outbean.set("littlega", listbean); 
			outbean.set("VLIDATE", flag);
			outbean.set("GZ_NAME", gz_name);
			outbean.set("GZ_ID", "Y09");
		}else{
			outbean.set("gotostay", false);
			outbean.set("littlega", listbean); 
			outbean.set("VLIDATE", flag);
			outbean.set("GZ_NAME", gz_name);
			outbean.set("GZ_ID", "Y09");
		}
		
		return outbean;//同序列自动验证通过
	}
}
