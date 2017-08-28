package com.rh.core.wfe.out;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.BaseContext.THREAD;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.FileMgr;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.JsonUtils;
import com.rh.core.wfe.util.WfeConstant;

/**
 * SY_WFE_PROCESS_INTERFACE服务的服务扩展类。
 * 工行日常办公系统工作流对外提供接口
 * @author ssw
 * 创建时间：2016-11-03
 */
public class WfeProcessInterfaceServ{
	
	//日志对象
	private static Log log = LogFactory.getLog(WfeProcessInterfaceServ.class);
	
	/**
	 * 审批单对应的服务ID
	 */
	private static final String SERV_ID = "SERV_ID";
	
	/**
	 * 拟稿人统一认证号
	 */
	private static final String SSIC_ID = "SSIC_ID";
	
	/**
	 * 审批单ID
	 */
	private static final String DATA_ID = "DATA_ID";
	
	/**
	 * 部门编码(以支持多部门用户)
	 */
	private static final String DEPT_CODE = "DEPT_CODE";
	
	/**
	 * 请求流水号
	 */
	private static final String REQ_NUM = "REQ_NUM";
	
	/**
	 * 审批单内容字段（json）
	 */
	private static final String DATAS = "DATAS";
	
	/**
	 * 用户Bean
	 */
	private static final String USER_BEAN = "USER_BEAN";
	
	/**
	 * 起草流程（调用本方法时，请确认您的类已经继成了com.rh.core.icbc.wf.ApplicationBaseServ类，并配置了相关服务定义）
	 * @param paramBean
	 * @return
	 */
	public OutBean start(ParamBean paramBean) {
		try{
			this.before(paramBean);
			UserBean user = (UserBean) paramBean.get(USER_BEAN);
			String ssicId = this.getValueByParamBean(paramBean, SSIC_ID);
			String servId = this.getValueByParamBean(paramBean, SERV_ID);
			String reqNum = this.getValueByParamBean(paramBean, REQ_NUM);
			
			//判断是否需要唯一性约束（同一服务下，每人只能存在一个流程）
			if (paramBean.get("SINGLE_FLAG", true)) {
				SqlBean sqlBean = new SqlBean()
					.and("S_USER", user.getCode())
					.andNotNull("S_WF_INST")
					.and("S_WF_STATE", "1");
				Bean bean = ServDao.find(servId, sqlBean);
				if (bean != null) {
					log.debug("存在重复的审批单：ID:" + bean.getId() + ";SERV_ID:" + servId + ";SSIC_ID:" + ssicId);
					return new OutBean().set(REQ_NUM, reqNum).setError("用户已存在审批单，ID:" + bean.getId());
				}
			}

			String strDATAS = paramBean.getStr(DATAS);
			Bean datas = JsonUtils.toBean(strDATAS);
			if (datas == null) {
				return new OutBean().set(REQ_NUM, reqNum).setError("审批单数据（DATAS）不能为空");
			}
			
			log.debug("启动流程:" + servId + ";" + datas.toString());
			ParamBean wfParam = new ParamBean();
			wfParam.copyFrom(datas);
			wfParam.setAddFlag(true);
		    OutBean saveBean = ServMgr.act(servId, ServMgr.ACT_SAVE, wfParam);
		    OutBean outBean = new OutBean();
		    //删除掉多余字段
		    saveBean.remove("_SAVEIDS_");
		    saveBean.remove("_ADD_");
		    saveBean.remove("_TIME_");
		    saveBean.remove("_ROWNUM_");
		    String msg = saveBean.getMsg();
		    log.debug("result:" + msg);
		    if (msg != null && msg.indexOf("流程已经成功启动") != -1) {
		    	log.info("REQ_NUM:" + reqNum + ";流程已经成功启动");
		    	outBean.setMsg(msg);
		    } else {
		    	//流程启动失败时，事务回滚
		    	Transaction.rollback();
		    	log.error("REQ_NUM:" + reqNum + ";流程启动失败，事务回滚");
		    	outBean.setMsg("您的流程未成功启动，请将服务定义中的【流程标志】设置为“自动启动流程”");
		    }
		    saveBean.remove("_MSG_");
		    outBean.set(DATAS, saveBean);
		    outBean.set(REQ_NUM, reqNum);
		    outBean.set(SERV_ID, servId);
		    outBean.set(DATA_ID, saveBean.getId());
		    return outBean;
		} catch (Exception e){
			//有可能错误为 流水号为空产生，所以此处不对流水号做非空判定
			String reqNum = paramBean.getStr(REQ_NUM);
			log.error("REQ_NUM:" + reqNum + ";error:" + e.getMessage() + ";" + paramBean.toString(), e);
			return new OutBean().set(REQ_NUM, reqNum).setError(e.getMessage());
		} finally {
			Context.removeThreadUser();
		}
	}
	
	/**
	 * 废止流程（调用本方法时，请确认您的类已经继成了com.rh.core.icbc.wf.ApplicationBaseServ类，并配置了相关服务定义）
	 * @param paramBean
	 * @return
	 */
	public OutBean terminate(ParamBean paramBean) {
		try {
			this.before(paramBean);
			UserBean user = (UserBean) paramBean.get(USER_BEAN);
			String servId = this.getValueByParamBean(paramBean, SERV_ID);
			String reqNum = this.getValueByParamBean(paramBean, REQ_NUM);
			String dataId = this.getValueByParamBean(paramBean, DATA_ID);
			
			log.debug("废止流程:" + servId + ";审批单ID：" + dataId);
			ParamBean wfParam = new ParamBean();
			
			//强制办结标志
			wfParam.set(WfeConstant.FINISH_WF_FORCE_FLAG, 
					paramBean.get(WfeConstant.FINISH_WF_FORCE_FLAG, true));
			wfParam.set("DO_USER_DEPT", user.getCode() + "^" + user.getDeptCode());
			wfParam.setId(dataId);
			if (wfParam.getBoolean(WfeConstant.FINISH_WF_FORCE_FLAG)) { //删除不判断权限
				wfParam.set(WfeConstant.DEL_WF_IGNORE_RIGHT, true);
			}
			OutBean outBean = ServMgr.act(servId, "terminate", wfParam);
			if (outBean.isOk()) {
				log.info("REQ_NUM:" + reqNum + ";流程废止成功");
				return new OutBean().set(REQ_NUM, reqNum).setOk("流程废止成功。");
			} else {
				return new OutBean().set(REQ_NUM, reqNum).setMsg(outBean.getMsg());
			}
		} catch (Exception e){
			//有可能错误为 流水号为空产生，所以此处不对流水号做非空判定
			String reqNum = paramBean.getStr(REQ_NUM);
			log.error("REQ_NUM:" + reqNum + ";error:" + e.getMessage());
			return new OutBean().set(REQ_NUM, reqNum).setError(e.getMessage());
		} finally {
			Context.removeThreadUser();
		}
	}
	
	/**
	 * 查询流程状态
	 * @param paramBean
	 * @return
	 */
	public OutBean findState(ParamBean paramBean) {
		try{
			String servId = this.getValueByParamBean(paramBean, SERV_ID);
			String reqNum = this.getValueByParamBean(paramBean, REQ_NUM);
			String dataId = this.getValueByParamBean(paramBean, DATA_ID);
			log.debug("开始查询流程状态：REQ_NUM" + reqNum + ";DATA_ID:" + dataId);
			Bean data = ServDao.find(servId, dataId);
			String state = data.getStr("S_WF_STATE");
			String doState = data.getStr("S_WF_USER_STATE");
			OutBean outBean = new OutBean();
			outBean.set(REQ_NUM, reqNum);
			outBean.set(SERV_ID, servId);
			outBean.set(DATA_ID, dataId);
			outBean.set("WF_STATE", state);
			outBean.set("DO_STATE", doState);
			outBean.set(DATAS, data);
			List<Bean> files = FileMgr.getFileListBean(servId, dataId);
			if (!files.isEmpty()) {//去掉文件对象中的多于字段
				for (Bean file : files) {
					file.remove("S_CMPY");
				}
			}
			outBean.set("FILES", files);
			log.info("REQ_NUM:" + reqNum + ";查询流程状态成功");
			return outBean.setOk();
		} catch (Exception e){
			//有可能错误为 流水号为空产生，所以此处不对流水号做非空判定
			String reqNum = paramBean.getStr(REQ_NUM);
			log.error("REQ_NUM:" + reqNum + ";error:" + e.getMessage());
			return new OutBean().set(REQ_NUM, reqNum).setError(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param paramBean
	 * @return
	 */
	private String getValueByParamBean(ParamBean paramBean, String paramName) {
		String value = paramBean.getStr(paramName);
		if (StringUtils.isEmpty(value)) {
			throw new TipException(paramName + " 参数不能为空");
		}
		return value;
	}
	
	/**
	 * 塞入相关上下文
	 * @param paramBean
	 */
	private void before(ParamBean paramBean) {
		final String ssicId = this.getValueByParamBean(paramBean, SSIC_ID);
		UserBean user = UserMgr.getUserByLoginName(ssicId);
		if(paramBean.isNotEmpty(DEPT_CODE)) {
			final String deptId = paramBean.getStr(DEPT_CODE);
			user = UserMgr.getUser(user.getId(), deptId);
		}
		
		paramBean.set(USER_BEAN, user);
		Context.setThread(THREAD.USERBEAN, user);
		Context.setThread(THREAD.CMPYCODE, user.getCmpyCode());
	}
	
	/*public static void main(String[] args) {
		String url = "http://127.0.0.1/icbc/roa/SY_WFE_PROCESS_INTERFACE.terminate.do";
		Map<String, String> postParams = new HashMap<String, String>();
		try {
			postParams.put("DATAS", "{\"LEAVE_ID\":\"asdfasdfasdfasdf\",\"TITLE\":\"12-06-1\"}");
			postParams.put("REQ_NUM", "00");
			postParams.put("SERV_ID", "KQ_WF_TEST");
			postParams.put("SSIC_ID", "000000017");
			postParams.put("DATA_ID", "1vo3ySvPVfbWj8blsFqv");
			postParams.put("DEPT_CODE", "0010100546"); 
			HttpPost httpPost = HttpClientUtils.createHttpPost(url, postParams, "utf-8");
			OutBean outBean = HttpClientUtils.execute(httpPost);
			System.out.println(JsonUtils.toJson(outBean));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	
}
