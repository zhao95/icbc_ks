package com.rh.core.wfe.serv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.wfe.util.WfBtnConstant;

/**
 * 
 * @author yangjy
 * 
 */
public class ProcDefActServ extends CommonServ {
    private static HashSet<String> specialBtn = new HashSet<String>();
    
    static {
        specialBtn.add(WfBtnConstant.BUTTON_FINISH);
    }

    @Override
    public OutBean query(ParamBean paramBean) {
        String servId = paramBean.getServId();
        ServDefBean servDef = ServUtils.getServDef(servId);
        LinkedHashMap<String, Bean> list = servDef.getAllActs();

        List<Bean> rtnList = new ArrayList<Bean>();

        for (String key : list.keySet()) {
            Bean actBean = list.get(key);
            String actCode = actBean.getStr("ACT_CODE"); 
            if ((specialBtn.contains(actCode) || actCode.startsWith("cm")) 
                    && actBean.getInt("S_FLAG") == Constant.YES_INT) {
            	actBean.set("PROC_CODE", actBean.getStr("ACT_ID"));
                rtnList.add(actBean);
            }
        }

        OutBean outBean = new OutBean();
        outBean.setPage(rtnList.size());
        outBean.setData(rtnList);
        outBean.setCols(servDef.getAllItems());
        return outBean;
    }

}
