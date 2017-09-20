package com.rh.ts.xmgl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.icbc.ctp.utility.StringUtil;
import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.util.Constant;
import com.rh.ts.pvlg.mgr.GroupMgr;

/**
 * 
 * @author
 * @version
 *
 */
public class XmglServ extends CommonServ {
	/** 群组角色服务编码 */
	private static final String TS_XMGL_BMGL = "TS_XMGL_BMGL";

	/**
	 * 项目管理
	 * 
	 * @author LIAN
	 * @param paramBean
	 * 
	 */
	public void copy(Bean paramBean) {
		//OutBean NBean = new OutBean();
		// 获取服务ID
		String servId = paramBean.getStr(Constant.PARAM_SERV_ID);
		// 获取 主键id list
		String dataId = paramBean.getStr("pkCodes");
		// 根据服务id 主键id获取 当前对象
		Bean bean = ServDao.find(servId, dataId);
		Bean NBean = new Bean();
		NBean.set("XM_TITLE", bean.getStr("XM_TITLE"));
		NBean.set("XM_NAME", bean.getStr("XM_NAME")+"_复制");
		NBean.set("XM_FQDW_NAME", bean.getStr("XM_FQDW_NAME"));
		NBean.set("XM_TYPE", bean.getStr("XM_TYPE"));
		NBean.set("XM_START", bean.getStr("XM_START"));
		NBean.set("XM_END", bean.getStr("XM_END"));
		NBean.set("XM_GJ", bean.getStr("XM_GJ"));
		NBean.set("XM_FQDW_CODE", bean.getStr("XM_FQDW_CODE"));
		// 保存到数据库
		Bean res=ServDao.save(servId, NBean);
		// 从数据库得到xm_id和xm_gj；
		//String XMID = res.getStr("XM_ID");
		//NBean.setSaveIds(XMID);
		//afterSaveToSz(NBean);
		//return NBean;
	}

	// 下一步
	public OutBean saveAndToSZ(Bean bean) {
		OutBean result = new OutBean();
		// 获取服务ID
		String servId = bean.getStr(Constant.PARAM_SERV_ID);

		// 保存到数据库
		Bean res = ServDao.save(servId, bean);
		// 从数据库得到xm_id和xm_gj；
		String XMID = res.getStr("XM_ID");
		// String XMGJ=res.getStr("XM_GJ");
		result.setSaveIds(XMID);
		afterSaveToSz(bean);
		return result;
	}

	public OutBean afterSaveToSz(Bean bean) {
		String XMID = bean.getStr("XM_ID");
		String XMGJ = bean.getStr("XM_GJ");
		// 根据XM_ID查询，从数据库查询
		String where = " and XM_ID='" + XMID + "'";
		List<Bean> szList = ServDao.finds("TS_XMGL_SZ", where);
		if (!StringUtil.isBlank(XMGJ)) {
			String[] gj = XMGJ.split(",");
			// 批量保存项目设置
			List<Bean> beans = new ArrayList<Bean>();
			for (int i = 0; i < gj.length; i++) {
				int j = 0;
				for (; j < szList.size(); j++) {
					if (gj[i].equals(szList.get(j).getStr("XM_SZ_NAME"))) {
						break;
					}
				}
				if (szList.size() == 0 || j == szList.size()) {
					Bean s = new Bean();
					if (gj[i].equals("报名")) {
						s.set("XM_NAME_NUM", 1);
					} else if (gj[i].equals("审核")) {
						s.set("XM_NAME_NUM", 2);
					} else if (gj[i].equals("请假")) {
						s.set("XM_NAME_NUM", 3);
					} else if (gj[i].equals("异地借考")) {
						s.set("XM_NAME_NUM", 4);
					} else if (gj[i].equals("试卷")) {
						s.set("XM_NAME_NUM", 5);
					} else if (gj[i].equals("场次测算")) {
						s.set("XM_NAME_NUM", 6);
					} else if (gj[i].equals("考场安排")) {
						s.set("XM_NAME_NUM", 7);
					}
					s.set("XM_SZ_NAME", gj[i]);
					s.set("XM_ID", XMID);
					beans.add(s);
				}
			}
			if (beans.size() > 0) {
				ServDao.creates("TS_XMGL_SZ", beans);
			}
		}
		// 不存在则删除
		String delIds = "";
		String bmid = "";
		for (Bean s : szList) {
			int index = XMGJ.indexOf(s.getStr("XM_SZ_NAME"));
			if (index == -1) {
				delIds += "," + s.getId();
				String wherebmgl = " and XM_SZ_ID='" + s.getId() + "'";
				List<Bean> BMList = ServDao.finds("TS_XMGL_BMGL", wherebmgl);
				if (BMList.size() > 0) {
					for (Bean BM : BMList) {
						bmid = BM.getId();
					}
				}
			}
		}

		if (!StringUtil.isBlank(delIds)) {
			// 删除项目设置
			String sql = "delete from ts_xmgl_sz where XM_SZ_ID in ('" + delIds.substring(1).replace(",", "','") + "')";
			Transaction.getExecutor().execute(sql);
			// 删除报名
			String bmsql = "delete from ts_xmgl_bmgl where XM_SZ_ID in ('" + delIds.substring(1).replace(",", "','")
					+ "')";
			Transaction.getExecutor().execute(bmsql);
			// 删除人员群组
			String ryqz = "delete from ts_xmgl_bmgl where XM_SZ_ID in ('" + delIds.substring(1).replace(",", "','")
					+ "')";
			Transaction.getExecutor().execute(ryqz);
			 //删除审核
		    String bmsh ="delete from ts_xmgl_bmsh where XM_SZ_ID in ('"+delIds.substring(1).replace(",", "','")+"')";
		    Transaction.getExecutor().execute(bmsh);
		    //删除请假
		    String qj ="delete from ts_xmgl_qjgl where XM_SZ_ID in ('"+delIds.substring(1).replace(",", "','")+"')";
		    Transaction.getExecutor().execute(qj);
		  //删除异地借考
		    String ydjk ="delete from ts_xmgl_ydjk where XM_SZ_ID in ('"+delIds.substring(1).replace(",", "','")+"')";
		    Transaction.getExecutor().execute(ydjk);
		}

		if (!StringUtil.isBlank(bmid)) {
			// 删除考试类别
			String kslb = "delete from  ts_xmgl_bm_kslb  where  BM_ID='" + bmid + "'";
			Transaction.getExecutor().execute(kslb);
			// 删除非资格考试
			String fzgks = "delete from  ts_xmgl_bm_fzgks  where  BM_ID='" + bmid + "'";
			Transaction.getExecutor().execute(fzgks);
		}
		return new OutBean();
	}

	// 根据XM_ID删除项目管理设置数据
	public void delSzByXmid(Bean bean) {
		String xmid = bean.getStr("XM_ID");
		String sql = "delete from ts_xmgl_sz where XM_ID='" + xmid + "'";
		Transaction.getExecutor().execute(sql);
	}

//	@Override
//	protected void afterDelete(ParamBean paramBean, OutBean outBean) {
//		String XM_IDs = outBean.getDeleteIds();
//		if (!StringUtil.isBlank(XM_IDs)) {
//			String sql = "delete from ts_xmgl_sz where XM_ID in ('" + XM_IDs.replace(",", "','") + "')";
//			Transaction.getExecutor().execute(sql);
//		}
//	}

	public Bean getXmList(Bean paramBean) {
		List<Bean> list = ServDao.finds("TS_XMGL", "");
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			if (list.size() == 0) {
				s += list.get(i).getId();
			} else if (i == (list.size() - 1)) {
				s += list.get(i).getId();
			} else {
				s += list.get(i).getId() + ",";
			}
		}
		Bean out = new Bean();
		out.set("xid", s);
		return out;
	}

	public Bean getUserXm(Bean paramBean) {
		Bean outBean = new Bean();
		String user_code = paramBean.getStr("user_code");
		// 本人所在的群组编码
		String qz = GroupMgr.getGroupCodes(user_code);
		List<Bean> list = ServDao.finds("TS_XMGL", "");
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			if (i == (list.size() - 1)) {
				s += list.get(i).getId();
			} else {
				s += list.get(i).getId() + ",";
			}
		}
		String[] xmarray = s.split(",");
		// 将可见的 项目 ID 放到新的数组中
		List<String> kjxm = new ArrayList<String>();
		// 遍历项目ID 匹配项目和本人的 群组权限
		for (int a = 0; a < xmarray.length; a++) {
			ParamBean param = new ParamBean();
			param.set("xmid", xmarray[a]);
			Bean outBeanCode = ServMgr.act("TS_XMGL_RYGL_V", "getCodes", param);
			String codes = outBeanCode.getStr("rycodes");
			Boolean boo = false;
			if ("".equals(codes)) {
			} else {
				// 本人所在的群组编码
				String[] codeArray = codes.split(",");
				String[] qzArray = qz.split(",");
				for (int b = 0; b < qzArray.length; b++) {
					if (Arrays.asList(codeArray).contains(qzArray[b])) {
						boo = true;
					}
				}
			}
			// 可见的项目id
			if (boo == true) {
				kjxm.add(xmarray[a]);
			}
		}
		// kjxm为可见项目idlist stringlist 为已报名的项目idlist
		List<Bean> lastlist = new ArrayList<Bean>();
		for (int i = 0; i < list.size(); i++) {
			Bean bean = list.get(i);
			// 项目中已存在array的 title 数据 将展示在 已报名信息中
			String id = bean.getStr("XM_ID");
			if (!kjxm.contains(id)) {
				// 已报名这个考试之后 或者他不能报名这个考试 中断循环 继续开始
				continue;
			} 
			if("1".equals(bean.getStr("XM_STATE"))){
				lastlist.add(bean);
			}
		}
		
		// 将lastlist转换为 json字符串传给前台
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, lastlist);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outBean.set("list", w.toString());
		return outBean;
	}


	/**
	 * 获取此人所在节点下 可审核 的 机构 根据机构 筛选可审核的项目
	 */
	public Bean getDshList(Bean paramBean) {
		String user_code = paramBean.getStr("user_code");
		/* ServDao.finds("TS_XMGL", where); */
		List<Bean> list = ServDao.finds("TS_XMGL", "");
		// 可审核的项目list
		List<Bean> SHlist = new ArrayList<Bean>();
		Boolean show = false;
		for (Bean bean : list) {
			String id = bean.getId();
			// 查询待审核 表 里的other字段判断 是否包含user_code
			String where = "AND XM_ID=" + "'" + id + "'";
			List<Bean> staylist = ServDao.finds("TS_BMSH_STAY", where);

			for (Bean bean2 : staylist) {

				String other = bean2.getStr("SH_OTHER");
				if (other.contains(user_code)) {
					show = true;
				}
			}
			if (show) {
				SHlist.add(bean);
			}
		}

		Bean out = new Bean();
		out.set("list", SHlist);
		return out;
	}

	/**
	 * 获取此人所在节点下 可审核 的 机构 根据机构 筛选可审核的项目
	 */
	public Bean getShJsonList(Bean paramBean) {
		String where1 = paramBean.getStr("where");
		String user_code = paramBean.getStr("user_code");
		List<Bean> list = ServDao.finds("TS_XMGL", where1);
		// 可审核的项目list
		List<Bean> SHlist = new ArrayList<Bean>();
		for (Bean bean : list) {
			String id = bean.getId();
			// 查询待审核 表 里的other字段判断 是否包含user_code
			String where = "AND XM_ID="+"'"+id+"'"+" AND SH_OTHER like"+"'%"+user_code+"%'";
			List<Bean> staylist = ServDao.finds("TS_BMSH_STAY", where);
			List<Bean> NOPASSlist = ServDao.finds("TS_BMSH_NOPASS", where);
			List<Bean> PASSlist = ServDao.finds("TS_BMSH_PASS", where);
			if(staylist.size()!=0||NOPASSlist.size()!=0||PASSlist.size()!=0){
				SHlist.add(bean);
			}
		}
		Bean outBean = new Bean();
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, SHlist);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outBean.set("list", w.toString());
		return outBean;
	}


	/**
	 * 获取项目下所有 未审核的 报名 (某一页 每页多少条)
	 *
	 */
	public Bean getUncheckList(Bean paramBean) {
		Bean outBean = new Bean();
		Bean _PAGE_ = new Bean();
		String servId = "TS_XMGL";
		String zhuangtai = paramBean.getStr("zhuangtai");
		String user_code = paramBean.getStr("user_code");
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		String where1 = paramBean.getStr("where");
		List<Bean> list = ServDao.finds(servId, where1);
		List<Bean> SHlist = new ArrayList<Bean>();
		
		for (Bean bean : list) {
			//根据报名id找到审核数据的状态
			String id = bean.getId();
			ParamBean paramb = new ParamBean();
			paramb.set("xmid", id);
			OutBean out = ServMgr.act("TS_XMGL_BMGL", "getBMState", paramb);
			String state = "";
			 List<Bean> list2 = out.getList("nojson");
			 if(list2.size()!=0){
				  state = list2.get(0).getStr("STATE");
			 }
			//根据项目id找到流程下的所有节点
				String belongwhere = "AND XM_ID='"+id+"'";
				List<Bean> finds = ServDao.finds("TS_XMGL_BMSH", belongwhere);
				if(finds.size()!=0){
					String wfsid = finds.get(0).getStr("WFS_ID");
					//根据流程id查找所有审核节点
					String wfswhere = "AND WFS_ID='"+wfsid+"'";
					List<Bean> finds2 = ServDao.finds("TS_WFS_NODE_APPLY", wfswhere);
					//遍历审核节点  获取 当前人的审核机构
					for (Bean nodebean : finds2) {
						//根据流程id获取 流程绑定的人和审核机构
						String nodeid = nodebean.getStr("NODE_ID");
						String nodewhere = "AND NODE_ID='"+nodeid+"'";
						List<Bean> finds3 = ServDao.finds("TS_WFS_BMSHLC", nodewhere);
						for (Bean codebean : finds3) {
							if(user_code.equals(codebean.getStr("SHR_USERCODE"))){
								//此流程内包含此审核人
								if("1".equals(zhuangtai)&&"待报名".equals(state)){
									
									SHlist.add(bean);
								}else if("2".equals(zhuangtai)&&"已结束".equals(state)){
									SHlist.add(bean);
								}else if("全部".equals(zhuangtai)){
									SHlist.add(bean);
								}
								
							}
						}
					}
				}
			 
		
			
		}
		
		int ALLNUM = SHlist.size();
		// 计算页数
		int meiye = Integer.parseInt(SHOWNUM);
		int yeshu = ALLNUM / meiye;
		int yushu = ALLNUM % meiye;
		// 获取总页数
		if (yeshu == 0 && yushu != 0) {
			yeshu += 1;
		}

		int nowpage = Integer.parseInt(NOWPAGE);
		int showpage = Integer.parseInt(SHOWNUM);
		// 计算第一项 开始
		int chushi = (nowpage - 1) * showpage + 1;
		// 计算结束项
		int jieshu = (nowpage - 1) * showpage + showpage;
		// 放到Array中
		List<Bean> list2 = new ArrayList<Bean>();
		if (ALLNUM == 0) {
			// 没有数据
		} else {

			if (jieshu <= ALLNUM) {
				// 循环将数据放入list2中返回给前台
				for (int i = chushi; i <= jieshu; i++) {
					list2.add(SHlist.get(i - 1));
				}

			} else {
				for (int j = chushi; j < ALLNUM + 1; j++) {
					list2.add(SHlist.get(j - 1));
				}
			}
		}
		// ObjectMapper和StringWriter都是jackson中的，通过这两个可以实现对list的序列化
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, list2);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		_PAGE_.set("ALLNUM", SHlist.size());
		_PAGE_.set("NOWPAGE", NOWPAGE);
		_PAGE_.set("PAGES", yeshu);
		_PAGE_.set("SHOWNUM", SHOWNUM);
		outBean.set("list", w.toString());
		outBean.set("_PAGE_", _PAGE_);
		outBean.set("first", chushi);
		return outBean;
	}




//按钮发布的操作  传过来id

public void UpdateStatusStart(ParamBean paramBean){
		try {
		//获取服务ID
		String servId=paramBean.getStr(Constant.PARAM_SERV_ID);
		//获取 主键id  list
		String dataId = paramBean.getStr("pkCodes");
		
		String[] dataIds = dataId.split(",");
		//循环遍历 dataIds,
		for(int  i=0;i<dataIds.length;i++){
			String  where="and  XM_ID ='"+dataIds[i]+"'";
			Bean xmBean =  ServDao.find("TS_XMGL", where);
			ServDao.save("TS_XMGL",xmBean.set("XM_STATE", 1) );
		}
	} catch (Exception e) {
			throw new TipException("服务器异常，发布失败！");
	}
	}

}