package com.rh.core.icbc.pushwxtodo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.icbc.sso.CryptString;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.DateUtils;
import com.rh.core.util.scheduler.RhJob;
import com.rh.core.util.scheduler.RhJobContext;

/**
 * @author scf
 */
public class SynWXTodoListJob extends RhJob {
	private Logger log = Logger.getLogger(getClass());
	public static final String SYNC_FLAG = "SYNC_FLAG";
	private static final int WX_ADD_ACTION = 0;
	private static final int WX_DEL_ACTION = 2;
	private static final String PE_TODO_ACCOUNT = "PE_TODO_ACCOUNT";
	private static final String SY_COMM_TODO = "SY_COMM_TODO";
	private static final String SY_COMM_TODO_HIS = "SY_COMM_TODO_HIS";
	private static final int TODO_DEL = 1;		// 需要从网讯删除的数据
	private static final int TODO_ADD = 2;		// 需要添加到网讯的数据
	private static final int TODO_FINISH = 3;   // 已完成添加或者删除的数据
	private static int PE_WX_PUSH_NUM = 5000;
	private int PE_WX_APP_ID = 72;
	
	@Override
	protected void executeJob(RhJobContext context) {
		log.info("----start synchronize job----");

		PE_WX_PUSH_NUM = Context.getSyConf("PE_WX_PUSH_NUM", 5000);
		PE_WX_APP_ID = Context.getSyConf("PE_WX_APP_ID", 72);
		// 设置查询条件
		String speDate = getSpeDate(context);
		try {
			// 处理指定天数内未添加到网讯集中待办平台的待办
			doAddTodo2WXList(speDate);
			// 处理指定天数内未从网讯集中待办平台删除的已完成的待办
			doDelTodo2WXList(speDate);
			// 将SY_COMM_TODO_HIS表中SYNC_FLAG为2的置为3
			updateSyncFlag();
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
		log.info("----end synchronize job----");
	}
	
	/**
	 * 获取指定日期字符串
	 * @param context
	 * @return
	 */
	private String getSpeDate(RhJobContext context) {
		// 获取配置的天数
		Bean jobData = context.getRhJobDetail().getJobData();
		int preserveDays = 1;
		if(jobData != null){
			preserveDays = jobData.getInt("DAYS");
			if (0 == preserveDays) {
				preserveDays = 1;
			}
		}
		return DateUtils.getCertainDate(0 - preserveDays);
	}
	
	/**
	 * 添加数据到网讯,添加数据到本地表并更新本地数据库表字段
	 * @param sqlBean
	 * @throws Exception
	 */
	private void doAddTodo2WXList(String speDate) throws Exception {
		List<String> sucaddList = null;
		SqlBean sqlQuery = new SqlBean();
		sqlQuery.limit(PE_WX_PUSH_NUM);
		sqlQuery.andGTE("S_MTIME", speDate);
		sqlQuery.and(SYNC_FLAG, TODO_ADD);
		List<Bean> findsBeanList = ServDao.finds(SY_COMM_TODO, sqlQuery);
		if (findsBeanList != null && findsBeanList.size() > 0) {
			// 每次最多向网讯上送100条数据
			int resultSize = findsBeanList.size();
			int count = (resultSize - 1)/100;
			List<Bean> partBeanList = null;
			for (int i = 0; i <= count; i++) {
				if (i == count) {
					partBeanList = findsBeanList.subList(count * 100, resultSize);
				} else {
					partBeanList = findsBeanList.subList(i * 100, (i + 1) * 100);
				}
				if (partBeanList == null) {
					return;
				}
				sucaddList = addTodo2WXList(partBeanList);
				if (sucaddList != null && sucaddList.size() > 0) {
					addPushData(sucaddList, partBeanList);
				}
			}
		}
	}
	
	/**
	 * 从网讯删除数据，将数据从本地表中删除并更新本地数据库表字段
	 * @param sqlBean
	 * @throws Exception
	 */
	private void doDelTodo2WXList(String speDate) throws Exception {
		List<String> sucdelList = null;
		SqlBean sqlQuery = new SqlBean();
		sqlQuery.limit(PE_WX_PUSH_NUM);
		sqlQuery.andGTE("S_MTIME", speDate);
		sqlQuery.and(SYNC_FLAG, TODO_DEL);
		List<Bean> findsBeanList = ServDao.finds(SY_COMM_TODO_HIS, sqlQuery);
		if (findsBeanList != null && findsBeanList.size() > 0) {
			// 每次最多向网讯上送100条数据
			int resultSize = findsBeanList.size();
			int count = (resultSize - 1)/100;
			List<Bean> partBeanList = null;
			for (int i = 0; i <= count; i++) {
				if (i == count) {
					partBeanList = findsBeanList.subList(count * 100, resultSize);
				} else {
					partBeanList = findsBeanList.subList(i * 100, (i + 1) * 100);
				}
				if (partBeanList == null) {
					return;
				}
				sucdelList = delTodo2WXList(partBeanList);
				if (sucdelList != null && sucdelList.size() > 0) {
					delPushData(sucdelList, partBeanList);
				}
			}
		}
	}
	
	/**
	 * 将SY_COMM_TODO_HIS表中SYNC_FLAG为2的置为3
	 * @param sqlQuery
	 */
	private void updateSyncFlag() {
		SqlBean sqlBean = new SqlBean();
		sqlBean.and(SYNC_FLAG, TODO_ADD);
		sqlBean.set(SYNC_FLAG, TODO_FINISH);
		ServDao.update(SY_COMM_TODO_HIS, sqlBean);
	}

	/**
	 * 添加数据到网讯
	 * @param findsBeanList
	 * @return 添加成功的数据id
	 * @throws Exception
	 */
	private List<String> addTodo2WXList(List<Bean> findsBeanList) throws Exception {
		return syncWXTodo(findsBeanList, WX_ADD_ACTION);
	}
	
	/**
	 * 将数据从网讯中删除
	 * @param findsBeanList
	 * @return 删除成功的数据id
	 * @throws Exception
	 */
	private List<String> delTodo2WXList(List<Bean> findsBeanList) throws Exception {
		return syncWXTodo(findsBeanList, WX_DEL_ACTION);
	}
	
	/**
	 * 添加数据到本地表，更新本地数据库表字段SYNC_FLAG为1
	 * @param sucaddList
	 * @param findsBeanList
	 */
	private void addPushData(List<String> sucaddList, List<Bean> findsBeanList) {
		List<Bean> dataBeanList = new ArrayList<Bean>();
		
		for (Bean bean : findsBeanList) {
			if(!sucaddList.contains(bean.getStr("TODO_ID"))){
				 continue;
			 }
			
			Bean saveBean = new Bean();
			saveBean.set("FILTER_ITEM", bean.getStr("TODO_CODE_NAME"));
			saveBean.set("OWNER_ID", getSSICID(bean.getStr("OWNER_CODE")));
			saveBean.set("TODO_ID", bean.getStr("TODO_ID"));
			saveBean.set("APP_ID", PE_WX_APP_ID);
			saveBean.set("TODO_TITLE", bean.getStr("TODO_TITLE"));
			saveBean.set("TODO_URL", getUrl(bean));
			String createTime = bean.getStr("TODO_SEND_TIME");
			if (createTime.length() > 19) {
				createTime = createTime.substring(0, 19);
			}
			saveBean.set("TODO_SEND_TIME", createTime);
			String expireDate = bean.getStr("TODO_FINISH_TIME");
			if (expireDate.length() > 19) {
				expireDate = expireDate.substring(0, 19);
			}
			saveBean.set("TODO_FINISH_TIME", expireDate);
			String updateTime = bean.getStr("S_MTIME");
			if (updateTime.length() > 19) {
				updateTime = updateTime.substring(0, 19);
			}
			saveBean.set("S_MTIME", updateTime);
			saveBean.set("REMARK1", "");
			saveBean.set("REMARK2", "");
			saveBean.set("REMARK3", "");
			
			dataBeanList.add(saveBean);
			// 更新SY_COMM_TODO表中字段SYNC_FLAG的值为1
			SqlBean sqlBean = new SqlBean();
			sqlBean.set("SYNC_FLAG", TODO_DEL);
			sqlBean.andIn("TODO_ID", bean.getStr("TODO_ID"));
			ServDao.update(SY_COMM_TODO, sqlBean);
		}
		// 将数据添加到PE_TODO_ACCOUNT表中
		if(dataBeanList.size() > 0){
			ServDao.creates(PE_TODO_ACCOUNT, dataBeanList);
		}
	}
	
	/**
	 * 将数据从本地表中删除，更新本地数据库表字段SYNC_FLAG为3
	 * @param sucdelList
	 * @param findsBeanList
	 */
	private void delPushData(List<String> sucdelList, List<Bean> findsBeanList) {
		// 将数据从PE_TODO_ACCOUNT表中删除
		SqlBean delSql = new SqlBean();
		delSql.andIn("TODO_ID", sucdelList.toArray());
		ServDao.delete(PE_TODO_ACCOUNT, delSql);
		// 更新SY_COMM_TODO_HIS表中字段SYNC_FLAG为3
		SqlBean setBean = new SqlBean();
		setBean.set("SYNC_FLAG", TODO_FINISH);
		ServDao.updates(SY_COMM_TODO_HIS, setBean, delSql);
	}

	/**
	 * 与网讯进行待办同步
	 * @param beanList
	 * @param act    0:增加,1:更新,2:删除
	 * @throws Exception
	 */
	private List<String> syncWXTodo(List<Bean> beanList, int act) throws Exception {
		int num = beanList.size();
		ArrayOfString filterItemList = new ArrayOfString();
		ArrayOfString ownerSSICIDList = new ArrayOfString();
		ArrayOfInt appIDList = new ArrayOfInt();
		ArrayOfString toDoIDList = new ArrayOfString();
		ArrayOfString titleList = new ArrayOfString();
		ArrayOfString urlList = new ArrayOfString();
		ArrayOfString expireDateList = new ArrayOfString();
		ArrayOfString createTimeList = new ArrayOfString();
		ArrayOfInt actionList = new ArrayOfInt();
		ArrayOfString updateTimeList = new ArrayOfString();
		ArrayOfString remark1List = new ArrayOfString();
		ArrayOfString remark2List = new ArrayOfString();
		ArrayOfString remark3List = new ArrayOfString();
		
		String filterItem = null;
		String ownerSSICID = null;
		int appID = -1;
		String toDoID = null;
		String title = null;
		String url = null;
		String expireDate = null;
		String createTime = null;
		int action = -1;
		String updateTime = null;
		String remark1 = null;
		String remark2 = null;
		String remark3 = null;
		// 添加需要更新的数据
		for (Bean bean : beanList) {
			url = getUrl(bean);
			if (StringUtils.isBlank(url)) {
				num--;
				continue;
			}
			filterItem = bean.getStr("TODO_CODE_NAME");
			filterItemList.getString().add(filterItem);
			ownerSSICID = getSSICID(bean.getStr("OWNER_CODE"));
			if(ownerSSICID == null){
				continue;
			}
			ownerSSICIDList.getString().add(ownerSSICID);
			appID = PE_WX_APP_ID;
			appIDList.getInt().add(appID);
			toDoID = bean.getStr("TODO_ID");
			toDoIDList.getString().add(toDoID);
			title = bean.getStr("TODO_TITLE");
			titleList.getString().add(title);
			urlList.getString().add(url);
			expireDate = bean.getStr("TODO_FINISH_TIME");
			if (expireDate.length() > 19) {
				expireDate = expireDate.substring(0, 19);
			}
			expireDateList.getString().add(expireDate);
			createTime = bean.getStr("TODO_SEND_TIME");
			if (createTime.length() > 19) {
				createTime = createTime.substring(0, 19);
			}
			createTimeList.getString().add(createTime);
			action = act;
			actionList.getInt().add(action);
			updateTime = bean.getStr("S_MTIME");
			if (updateTime.length() > 19) {
				updateTime = updateTime.substring(0, 19);
			}
			updateTimeList.getString().add(updateTime);
			remark1 = "";
			remark1List.getString().add(remark1);
			remark2 = "";
			remark2List.getString().add(remark2);
			remark3 = "";
			remark3List.getString().add(remark3);
		}
		log.info("---"+act+"---推送数据："+num);
		if (num <= 0) {
			return new ArrayList<String>();
		}
		// 与网讯进行同步
		ToDoService toDoService = new ToDoService();
		String wxurl = ToDoService.TODOSERVICE_WSDL_LOCATION.toString();
		log.info("推送到网讯URL:" + wxurl);
		List<String> result = toDoService.getToDoServiceSoap().saveToDo(num, filterItemList, 
				ownerSSICIDList, appIDList, toDoIDList, titleList, urlList, 
				expireDateList, createTimeList, actionList, updateTimeList, 
				remark1List, remark2List, remark3List).getString();
		
		if(result != null && result.size() > 0){
			log.info("---"+act+"---返回数据："+result.size());
			log.info("-----推送成功-----");
		}else{
			log.info("--------推送失败--------");
		}
		
		return result;
	}
	
	/**
	 * 获取用户ID
	 * @param ownerCode
	 * @return
	 */
	private String getSSICID(String ownerCode) {
		try{
			UserBean user = UserMgr.getUser(ownerCode);
			return user.getLoginName();
		}catch(Exception e){
			log.error(e.getMessage(), e);
			return null;
		}
		
	}
	
	/**
	 * 获取URL
	 * @param todoBean
	 * @return
	 */
	private String getUrl(Bean todoBean) {
		// 获取需要在客户端展示因私出境的部门编号
		String deptCodes = Context.getSyConf("PE_WX_DEPT_CODE", "*");
		
		UserBean userBean = UserMgr.getUser(todoBean.getStr("OWNER_CODE"));
		StringBuilder remoteUrl = new StringBuilder(Context.getHttpUrl())
    			.append("/sy/base/view/stdCardView.jsp?sId=")
    			.append(todoBean.getStr("SERV_ID"))
    			.append("&pkCode=" )
    			.append(todoBean.getStr("TODO_OBJECT_ID1"))
    			.append("&replaceUrl=")
    			.append(todoBean.getStr("SERV_ID"))
    			.append(".byid.do?data={_PK_:")
    			.append(todoBean.getStr("TODO_OBJECT_ID1"))
    			.append(",NI_ID:")
    			.append(todoBean.getStr("TODO_OBJECT_ID2"))
    			.append("}&encUserInfo=")
    			.append(CryptString.encryptUserInfo(userBean.getLoginName()));
		
		if(isDoWithMobile(todoBean)){
			remoteUrl.append("&_SUPMOBILE_=1");
		}
		
		//因私出入境的代办推送
		if(todoBean.getStr("SERV_ID").trim().equals("PE_APPLICATION_INFO")){
			String deptCodePath = userBean.getCodePath();
			if (deptCodes.equals("*")) {
				return remoteUrl.toString();
			} else {
				String[] deptCodeArr = deptCodes.split(",");
				for(String code : deptCodeArr){
					if(StringUtils.isNotEmpty(code) && deptCodePath.indexOf(code) >=0){
						return remoteUrl.toString();
					}
				}
				remoteUrl = new StringBuilder();
			}
		}		
		return remoteUrl.toString();		
	}
	
	/**
	 * 判断是否在手机端处理
	 * @param todoBean
	 * @return
	 */
	private boolean isDoWithMobile(Bean todoBean) {
		String servIds = Context.getSyConf("OIS_DO_MOBILE_SERV", ",PE_APPLICATION_INFO,");
		String servId = todoBean.getStr("SERV_ID");
		if(servIds.indexOf(","+servId+",") >= 0){
			if(todoBean.getStr("OWNER_CODE").equals(todoBean.getStr("S_USER"))){
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void interrupt() {
		
	}
}
