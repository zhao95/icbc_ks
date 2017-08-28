package com.rh.ts.jkgl;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.ServDao;

public class JkglServ extends CommonServ {

    public Bean getListDicCode(Bean paramBean) {
	String dicServId = paramBean.getStr("dicServId");
	String where = paramBean.getStr("where");
	List<Bean> treeList = ServDao.finds(dicServId, where);
	Bean outBean = new Bean();
	String aaa = "";
	for (Bean bean : treeList) {
	    String itemName = bean.getStr("ITEM_NAME");
	    aaa = aaa + itemName + ",";
	}
	String substring = "";
	if (aaa.length() > 0)
	    substring = aaa.substring(0, aaa.length() - 1);
	outBean.set("ITEM_NAME", substring);
	return outBean;
    }

}
