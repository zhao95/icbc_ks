package com.rh.core.icbc.basedata;

import java.util.ArrayList;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.db.Transaction;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.util.threadpool.RhThreadTask;
/**
 * 提醒信息的线程父类，供子类线程任务调用共有方法
 * @author leader
 */
public abstract class AbstractTipTask extends RhThreadTask {

	private static final long serialVersionUID = 1L;
	/**
	 * 空方法，子类需要重写该方法
	 */
	@Override
	public abstract boolean execute();
	
	/**
	 * 获取群组中的所有人员，包含群组中的部门下的人员
	 * @param qzCode 提醒中的可见群组的编码
	 * @return 群组中所有人员的结果集
	 */
	public List<Bean> getAllUserForBMTip(String qzCode){
		String[] qzCodeArr = qzCode.split(",");
		List<Bean> userListByQZId = new ArrayList<Bean>();
		for (String G_ID : qzCodeArr) {
			String sqlForUser = "select GUD_ID, G_ID ,USER_DEPT_CODE AS USER_CODE,USER_DEPT_NAME,G_TYPE FROM TS_BM_GROUP_USER_DEPT WHERE G_TYPE =1 AND G_ID='" + G_ID + "'";
			List<Bean> userListByGid = Transaction.getExecutor().query(sqlForUser);
			//若G_TYPE=1  用户编码即为USER_DEPT_CODE,若G_TYPE=2 USER_DEPT_CODE 即为部门编码
			userListByQZId.addAll(userListByGid);
			String sqlForUserDept = "select GUD_ID, G_ID ,USER_DEPT_CODE,USER_DEPT_NAME,G_TYPE FROM TS_BM_GROUP_USER_DEPT WHERE G_TYPE =2 AND G_ID='" + G_ID + "'";
			List<Bean> deptListByGid = Transaction.getExecutor().query(sqlForUserDept);
			if (deptListByGid.size() > 0) {
				for (Bean aDeptListByGid : deptListByGid) {
					String userDpetCode = aDeptListByGid.getStr("USER_DEPT_CODE");
					if (!userDpetCode.equals("0010100000")) {
						//获取当前机构下所有用户
						List<Bean> userListByOdept = UserMgr.getUserListByOdept(userDpetCode);
						userListByQZId.addAll(userListByOdept);
					} else if (userDpetCode.equals("0010100000")) {
						//总行下，是全部所有人都可见
						List<Bean> userListByOdept = UserMgr.getUserListByOdept(userDpetCode);
						userListByQZId.addAll(userListByOdept);
						System.out.println("总行下所有人都要通知");
					}
				}
			}
		}
		return userListByQZId;
	}
}
