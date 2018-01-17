package com.rh.ts.pvlg;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.util.ImpUtils;
import com.rh.core.util.Strings;
import com.rh.ts.pvlg.mgr.GroupMgr;
import com.rh.ts.util.TsConstant;
import jxl.read.biff.BiffException;

import java.io.IOException;
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
        String fileId = paramBean.getStr("FILE_ID");
        //方法入口
        paramBean.set("SERVMETHOD", "savedata");
        OutBean out = ImpUtils.getDataFromXls(fileId, paramBean);
        String failnum = out.getStr("failernum");
        String successnum = out.getStr("oknum");
        //返回导入结果
        return new OutBean().set("FILE_ID", out.getStr("fileid")).setOk("导入成功：" + successnum + "条,导入失败：" + failnum + "条");
    }

    /**
     * @param paramBean paramBean G_ID FILE_ID
     * @return outBean
     */
    public OutBean savedata(ParamBean paramBean) {
        OutBean outBean = new OutBean();

        String gId = (String) paramBean.get("G_ID"),//报名群组id
                fileId = (String) paramBean.get("FILE_ID");//文件id

        List<Bean> beanList = paramBean.getList("datalist");
        List<String> codeList = new ArrayList<String>();//避免重复添加数据

        List<Bean> beans = new ArrayList<Bean>();
        for (Bean rowBean : beanList) {
            String codeStr = rowBean.getStr(ImpUtils.COL_NAME + "1");

            Bean userBean = ImpUtils.getUserBeanByString(codeStr);
            if (userBean == null) {
                rowBean.set(ImpUtils.ERROR_NAME, "找不到用户");
                continue;
            }

            String code = userBean.getStr("USER_CODE"),
                    name = userBean.getStr("USER_NAME");

            if (codeList.contains(code)) {
                //已包含 continue ：避免重复添加数据
                rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
                continue;
            }

            rowBean.clear();
            rowBean.set("G_ID", gId);
            rowBean.set("USER_CODE", code);
            rowBean.set("GU_TYPE", 1);//选取类型 1人员

            if (ServDao.count(TsConstant.SERV_GROUP_USER, rowBean) <= 0) {
                //先查询避免重复添加
                rowBean.set("USER_NAME", name);
                beans.add(rowBean);
                codeList.add(code);
            }else{
                rowBean.set(ImpUtils.ERROR_NAME, "重复数据：" + code);
                continue;
            }
        }
        ServDao.creates(TsConstant.SERV_GROUP_USER, beans);

        return outBean.set("alllist", beanList).set("successlist", codeList);

    }

}
