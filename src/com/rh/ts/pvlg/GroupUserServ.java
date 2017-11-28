package com.rh.ts.pvlg;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.comm.FileMgr;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.mgr.GroupMgr;
import com.rh.ts.util.TsConstant;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GroupUserServ extends CommonServ {

	private static String CODE_STR = "codeStr";

	/**
	 * 删除之后执行
	 */
	protected void afterDelete(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {

			List<Bean> list = paramBean.getDeleteDatas();

			if (list != null && list.size() > 0) {

				Bean user = list.get(0);

				String groupId = user.getStr("G_ID");

				if (!Strings.isBlank(groupId)) {

					// 更新群组对应所有用户缓存
					setGroupToUserCache(list, false);
					// 更新用户对应所有群组缓存
					setUserToGroupsCache(list, false);
				}
			}
		}
	}

	/**
	 * 批量保存后执行
	 */
	protected void afterBatchSave(ParamBean paramBean, OutBean outBean) {

		if (outBean.getStr("_MSG_").startsWith("OK")) {

			List<Bean> list = paramBean.getBatchSaveDatas();

			if (list != null && list.size() > 0) {

				Bean bean = list.get(0);

				String groupId = bean.getStr("G_ID");

				if (!Strings.isBlank(groupId)) {

					// 更新群组对应所有用户缓存
					setGroupToUserCache(list, true);
					// 更新用户对应所有群组缓存
					setUserToGroupsCache(list, true);
				}
			}
		}
	}

	/**
	 * 重置用户的所有群组缓存, key：用户 val：群组
	 * 
	 * @param userCode
	 * @param groupId
	 * @param list
	 * @param addORdel
	 *            true 添加群组/false 删除群组
	 */
	private void setUserToGroupsCache(List<Bean> list, boolean addORdel) {

		for (Bean user : list) {

			String groupId = user.getStr("G_ID");

			String userCode = user.getStr("USER_CODE");

			String groupCodes = GroupMgr.getGroupCodes(userCode);

			if (addORdel) {
				groupCodes = Strings.mergeStr(groupCodes, groupId);
			} else {
				groupCodes = Strings.removeValue(groupCodes, groupId);
			}

			if (Strings.isBlank(groupCodes)) {
				GroupMgr.removeGroupCache(userCode, TsConstant.SERV_GROUP);
			} else {
				GroupMgr.updateGroupCache(userCode, TsConstant.SERV_GROUP, groupCodes);
			}

		}
	}

	/**
	 * 重置群组的所有用户缓存, key：群组 val：用户
	 * 
	 * @param userCode
	 * @param groupId
	 * @param list
	 * @param addORdel
	 *            true 添加群组/false 删除群组
	 */
	private void setGroupToUserCache(List<Bean> list, boolean addORdel) {

		for (Bean user : list) {

			String groupId = user.getStr("G_ID");

			String userCode = user.getStr("USER_CODE");

			String userCodes = GroupMgr.getGroupUserCodes(groupId);

			if (addORdel) {
				userCodes = Strings.mergeStr(userCodes, userCode);
			} else {
				userCodes = Strings.removeValue(userCodes, userCode);
			}

			if (Strings.isBlank(userCodes)) {
				GroupMgr.removeGroupCache(userCode, TsConstant.SERV_GROUP_USER);
			} else {
				GroupMgr.updateGroupCache(groupId, TsConstant.SERV_GROUP_USER, userCodes);
			}

		}
	}


	/**
	 * @param paramBean paramBean G_ID FILE_ID
	 * @return outBean
	 */
	public OutBean saveFromExcel(ParamBean paramBean) throws IOException, BiffException {
		OutBean outBean = new OutBean();

		String gId = (String) paramBean.get("G_ID"),//报名群组id
				fileId = (String) paramBean.get("FILE_ID");//文件id

		List<Bean> beanList = this.getDataFromXls(fileId);
		List<String> codeList = new ArrayList<String>();//避免重复添加数据

		List<Bean> beans = new ArrayList<Bean>();
		for (Bean bean : beanList) {
			String codeStr = bean.getStr(CODE_STR);

			int length = codeStr.trim().length();
			Bean userBean = null;
			try {
				if (length == 9) {
					userBean = UserMgr.getUserByLoginName(codeStr);
				} else if (length == 10) {
					userBean = UserMgr.getUser(codeStr);
				} else if (length > 11) {
					List<Bean> userBeanList = ServDao.finds("SY_ORG_USER_ALL", " and USER_IDCARD ='" + codeStr + "'");
					if (CollectionUtils.isNotEmpty(userBeanList)) {
						userBean = userBeanList.get(0);
					}
				}
			} catch (Exception e) {
				userBean = null;
			}
			if (userBean == null) {
				continue;
			}

			String code = userBean.getStr("USER_CODE"),
					name = userBean.getStr("USER_NAME");

			if (codeList.contains(code)) {
				//已包含 continue ：避免重复添加数据
				continue;
			}

			bean.clear();
			bean.set("G_ID", gId);
			bean.set("USER_CODE", code);
			bean.set("GU_TYPE", 1);//选取类型 1人员

			if (ServDao.count(TsConstant.SERV_GROUP_USER, bean) <= 0) {
				//先查询避免重复添加
				bean.set("USER_NAME", name);
				beans.add(bean);
				codeList.add(code);
			}
		}
		ServDao.creates(TsConstant.SERV_GROUP_USER, beans);
//        int total = beanList.size();
		FileMgr.deleteFile(fileId);
		return outBean.setCount(codeList.size()).setOk("成功导入" + codeList.size() + "条");
	}

	/**
	 *
	 *
	 * @param fileId 文件id
	 */
	private List<Bean> getDataFromXls(String fileId) throws IOException, BiffException {
		List<Bean> result = new ArrayList<Bean>();
		Bean fileBean = FileMgr.getFile(fileId);
		InputStream in = FileMgr.download(fileBean);
		Workbook workbook = Workbook.getWorkbook(in);
		try {
			Sheet sheet1 = workbook.getSheet(0);
			int rows = sheet1.getRows();
			for (int i = 0; i < rows; i++) {
//                if (i != 0) {
				Cell[] cells = sheet1.getRow(i);
				String contents0 = cells[0].getContents();
				if (!StringUtils.isEmpty(contents0)) {
					Bean bean = new Bean();
					bean.set(CODE_STR, contents0);
//                        bean.set("name", contents1);
					result.add(bean);
				}
//                }
			}
		} catch (Exception e) {
			throw new TipException("Excel文件解析错误，请校验！");
		} finally {
			workbook.close();
		}
		return result;
	}

}
