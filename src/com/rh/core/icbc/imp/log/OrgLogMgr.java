package com.rh.core.icbc.imp.log;

import com.rh.core.util.DateUtils;
import com.rh.core.util.msg.CommonMsg;
import com.rh.core.util.msg.MsgCenter;

public class OrgLogMgr {
	private static final String MGRID = "SY_ORG_MIGRATE_LOG";
	private static final String MGRNAME = "save";
	
	/**
	 * 记录组织数据迁移日志
	 * @param dataId：迁移出错的数据ID
	 * @param servId：出错数据所属服务
	 * @param title：错误数据标题
	 * @param err_MSG：详细错误信息
	 */
	public static void orgLogSave(String dataId, String servId,String title,String err_MSG){
			CommonMsg msg = new CommonMsg(MGRID, MGRNAME);
            msg.set("DATA_ID", dataId);
            msg.set("SERV_ID", servId);
            msg.set("TITLE", title);
            msg.set("ERR_MSG",err_MSG);
            msg.set("S_ATIME", DateUtils.getDatetimeTS());//自动生成时间
            
            MsgCenter.getInstance().addMsg(msg);
		
	}
}
