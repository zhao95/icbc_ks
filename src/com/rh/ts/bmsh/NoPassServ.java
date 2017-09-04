package com.rh.ts.bmsh;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.util.ExportExcel;
import com.rh.core.serv.util.ServUtils;
/**
 * 没有通过的审核
 * @author shiyun
 *
 */
public class NoPassServ extends CommonServ {
	 /**每次获取数据条数*/
    private static final int ONETIME_EXP_NUM = 5000;
    /**excel最大行数*/
    private static final int EXCEL_MAX_NUM = 65536;
	/**
	 * 获取那一页的记录  返回 
	 * @param paramBean
	 * @return
	 */
	public Bean getUncheckList(Bean paramBean){
		Bean outBean = new Bean();
		String servId = "TS_BMSH_NOPASS";
		String NOWPAGE = paramBean.getStr("nowpage");
		String SHOWNUM = paramBean.getStr("shownum");
		String where1 = paramBean.getStr("where");
		List<Bean> list = ServDao.finds(servId, where1);
		int ALLNUM = list.size();
		//计算页数
		int meiye =  Integer.parseInt(SHOWNUM);
		int yeshu = ALLNUM/meiye;
		int yushu = ALLNUM%meiye;
		//获取总页数
		if(yushu==0&&yeshu!=0){
			yeshu+=1;
		}
		
		int nowpage = Integer.parseInt(NOWPAGE);
		int showpage = Integer.parseInt(SHOWNUM);
		//计算第一项 开始
		int chushi = (nowpage-1)*showpage+1;
		//计算结束项
		int jieshu = (nowpage-1)*showpage+showpage;
		//放到Array中
		List<Bean> list2 = new ArrayList<Bean>();
		if(ALLNUM==0){
			//没有数据
		}else{
			
			if(jieshu<=ALLNUM){
				//循环将数据放入list2中返回给前台
				for(int i =chushi;i<=jieshu;i++){
					list2.add(list.get(i-1));
				}
				
			}else{
				for(int j=chushi;j<ALLNUM+1;j++){
					list2.add(list.get(j-1));
				}
			}
		}
		//ObjectMapper和StringWriter都是jackson中的，通过这两个可以实现对list的序列化  
	   ObjectMapper mapper = new ObjectMapper();    
       StringWriter w = new StringWriter();  
       try {
		mapper.writeValue(w, list2);
	} catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	 catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       outBean.set("list",w.toString());
       return outBean;
	}
	
	/**
	 * 获取项目下所有审核不通过的数据
	 * @param paramBean
	 * @return
	 */
	public Bean getAllData(Bean paramBean){
		Bean outBean = new Bean();
		String servId = "TS_BMSH_NOPASS";
		String where1 = paramBean.getStr("where");
		List<Bean> list = ServDao.finds(servId, where1);
		
		//ObjectMapper和StringWriter都是jackson中的，通过这两个可以实现对list的序列化  
	   ObjectMapper mapper = new ObjectMapper();    
	   StringWriter w = new StringWriter();  
	   try {
		mapper.writeValue(w, list);
	} catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	 catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   outBean.set("list",w.toString());
	   return outBean;
	}
	
	
	/**
	 * 修改 审核状态 
	 * @param paramBean
	 */
	public OutBean update(Bean paramBean){
		String s = paramBean.getStr("checkedid");
		String xmid = paramBean.getStr("xmid");
		String shenuser ="";
		UserBean userBean = Context.getUserBean();
		if(userBean.isEmpty()){
			 return new OutBean().setError("ERROR:user_code 为空");
		}else{
			shenuser=userBean.getStr("USER_CODE");
		}
		shenuser="admin";
		//被选中的id
		String[] ss = s.split(",");
		String liyou = paramBean.getStr("liyou");
		//继续进行审核
		
		for (String id : ss) {
			if(!"".equals(id)){
				//获取下级参数
				Bean bean = ServDao.find("TS_BMSH_NOPASS", id);
				String allman ="";
				String nextman = "";
				String levels = bean.getStr("SH_LEVEL");
				int level=Integer.parseInt(levels);
				int nowlevel=0;
				if(level!=1){
					
				ParamBean parambean = new ParamBean();
				parambean.set("examerWorekNum", bean.getStr("BM_CODE"));
				parambean.set("level",level);
				parambean.set("shrWorekNum", shenuser);
				parambean.set("flowName", 1);
				parambean.set("xmId", xmid);
				OutBean outbean = ServMgr.act("TS_WFS_APPLY", "backFlow", parambean);
				List<Bean> list = outbean.getList("result");
				
				for (int l=0;l<list.size();l++) {
					
					if(l==list.size()-1){
						
						allman+= list.get(l).getStr("SHR_WORKNUM");
					}else{
						allman+= list.get(l).getStr("SHR_WORKNUM")+",";
					}
					
				}
				String  nowlevelS = list.get(0).getStr("NODE_STEPS");
				 nowlevel = Integer.parseInt(nowlevelS);
				}
			bean.remove("SH_ID");
			bean.remove("S_CMPY");
			bean.remove("S_ODEPT");
			bean.remove("S_TDEPT");
			bean.remove("S_DEPT");
			bean.remove("S_ATIME");
			bean.remove("S_MTIME");
			bean.remove("S_USER");
			bean.remove("S_FLAG");
			bean.remove("_PK_");
			bean.remove("ROW_NUM_");
			Bean newBean = new Bean();
			newBean.copyFrom(bean);
			ServDao.save("TS_BMSH_PASS", newBean);
			if(level==1){
				//不用再去待审核中直接去  审核通过中 且数据无改动
			}else{
				newBean.set("SH_OTHER", allman+","+shenuser);
				newBean.set("SH_LEVEL", nowlevel);
				ServDao.save("TS_BMSH_STAY", newBean);
			}
			ServDao.delete("TS_BMSH_NOPASS", id);
			//审核明细表中插入此次审核数据
			Bean mindbean = new Bean();
			UserBean userbean = UserMgr.getUserByWorkNum(shenuser);
			mindbean.set("SH_LEVEL",level);
			mindbean.set("SH_MIND", liyou);
			mindbean.set("DATA_ID",bean.getStr("BM_ID"));
			mindbean.set("SH_STATUS", 1);
			mindbean.set("SH_ULOGIN",userbean.getLoginName());
			mindbean.set("SH_UNAME",userbean.getName());
			mindbean.set("SH_UCODE",shenuser);
			mindbean.set("SH_TYPE", 1);
			ServDao.save("TS_COMM_MIND",mindbean);
		}
		}
		return new OutBean().setOk();
	}
	
	/**
	 * 导出事获取 项目下的数据逗号分隔 返回 字符串 导出所有
	 */
	public Bean reSids(Bean paramBean){
		Bean outBean = new Bean();
		String servId = paramBean.getStr("servId");
		String xmid = paramBean.getStr("xmid");
		String where = "AND XM_ID="+"'"+xmid+"'";
		List<Bean> list = ServDao.finds(servId,where );
		String ids = "";
		for(int i=0;i<list.size();i++){
			String id = list.get(i).getId();
			if(i==list.size()-1){
				
				ids+=id;
			}else{
				ids+=id+",";
			}
		}
		   ObjectMapper mapper = new ObjectMapper();    
		   StringWriter w = new StringWriter();  
		   try {
			mapper.writeValue(w, ids);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   outBean.set("string",w.toString());
		   return outBean;
		
		
	}
	/**
	 * 提交异议
	 */
	public Bean yiyi(Bean paramBean){
		String liyou = paramBean.getStr("liyou");
		String bmid = paramBean.getStr("bmid");
		//将异议状态改为1  已提交
		//将提交理由保存到  此表  ts_bmlb_bm
		String where1 =  "AND BM_ID="+"'"+bmid+"'";
		List<Bean> bmlist = ServDao.finds("TS_BMLB_BM", where1);
		Bean bmbean = bmlist.get(0);
		bmbean.set("BM_YIYI_STATE", 1);
		bmbean.set("BM_SS_REASON", liyou);
		bmbean.set("BM_SH_STATE", 0);
		ServDao.save("TS_BMLB_BM",bmbean);
		String shenuser = "";
		UserBean userBean = Context.getUserBean();
		if(userBean.isEmpty()){
			 return new OutBean().setError("ERROR:user_code 为空");
		}else{
			shenuser=userBean.getStr("USER_CODE");
		}
		String where = "AND BM_ID="+"'"+bmid+"'";
		List<Bean> list = ServDao.finds("TS_BMSH_NOPASS", where);
		
		//继续走审核流程
		if(list.size()!=0){
			Bean bean = list.get(0);
			String slevel  = bean.getStr("SH_LEVEL");
			String xmid = bean.getStr("XM_ID");
			String bm_code = bean.getStr("BM_CODE");
			int level = Integer.parseInt(slevel);
			ParamBean parambean = new ParamBean();
			parambean.set("examerWorekNum", bm_code);
			parambean.set("level",level);
			parambean.set("shrWorekNum", shenuser);
			parambean.set("flowName", 1);
			parambean.set("xmId", xmid);
			
			OutBean outbean = ServMgr.act("TS_WFS_APPLY", "backFlow", parambean);
			List<Bean> list1 = outbean.getList("result");
			
			String allman ="";
			String nextman = "";
			int nowlevel=level;
			for (int l=0;l<list1.size();l++) {
				if(l==0){
					nextman = list1.get(l).getStr("BMSHLC_SHR");
				}
				if(l==list1.size()-1){
					
					allman+= list1.get(l).getStr("BMSHLC_SHR");
				}else{
					allman+= list1.get(l).getStr("BMSHLC_SHR")+",";
				}
				
			}
			String nowslevel = list.get(0).getStr("NODE_ID");
			 nowlevel = Integer.parseInt(nowslevel);
			
			bean.remove("SH_ID");
			bean.remove("S_CMPY");
			bean.remove("S_ODEPT");
			bean.remove("S_TDEPT");
			bean.remove("S_DEPT");
			bean.remove("S_ATIME");
			bean.remove("S_MTIME");
			bean.remove("S_USER");
			bean.remove("S_FLAG");
			bean.remove("_PK_");
			bean.remove("ROW_NUM_");
			Bean newBean = new Bean();
			newBean.copyFrom(bean);
			if(level==1){
				//不再stay中加数据
				ServDao.save("TS_BMSH_PASS", newBean);
			}else{
				if(level==0){
					
				}else{
					
					ServDao.save("TS_BMSH_PASS", newBean);
				}
				newBean.set("SH_USER", nextman);
				newBean.set("SH_OTHER", allman);
				newBean.set("SH_LEVEL",nowlevel);
				newBean.set("BM_YIYI", bmid);
				ServDao.save("TS_BMSH_STAY", newBean);
			}
			//添加一个字段用来标识异议   显示图标按钮
		}
		return new OutBean().setOk();
	}
	  /**
     * 提供导出Excel
     * @param paramBean 参数信息
     * @return 执行结果
     */
  	@Override
    public OutBean exp(ParamBean paramBean) {
  		String servid= paramBean.getServId();
  		 ParamBean parr = new ParamBean();
  		UserBean userBean1 = Context.getUserBean();
  				String user_code1 = "";
  				if(userBean1.isEmpty()){
  					 return new OutBean().setError("ERROR:user_code 为空");
  				}else{
  					user_code1=userBean1.getStr("USER_CODE");
  				}
  				user_code1="admin";
  		 parr.copyFrom(paramBean);
  		 parr.setServId("TS_BMSH_PX");
        String servId = paramBean.getServId();
        ServDefBean serv = ServUtils.getServDef(servId);
        long count = 0;
        long times = 0;
        paramBean.setQueryPageShowNum(ONETIME_EXP_NUM); // 设置每页最大导出数据量
        String searchWhere="";
        beforeExp(paramBean); //执行监听方法
        if (paramBean.getId().length() > 0) { //支持指定记录的导出（支持多选）
             searchWhere = " and " + serv.getPKey() + " in ('" 
                    + paramBean.getId().replaceAll(",", "','") + "')";
            paramBean.setQuerySearchWhere(searchWhere);
        }
        //所有
        List<Bean> dataList = ServDao.finds(servid, searchWhere);
        
        	List<Bean> finalList = new ArrayList<Bean>();
        
	      	//判断user_code 是否为空  若为空则 导出所有
        	
       	 	 searchWhere = " AND USER_CODE="+"'"+user_code1+"'";
       	 
	        //排序用的 parr存读取th
	        parr.setQuerySearchWhere(searchWhere);
            LinkedHashMap<String, Bean> cols = new LinkedHashMap<String, Bean>();
            String s = "";
        	List<Bean> pxdatalist1 = ServDao.finds("TS_BMSH_PX", searchWhere);
        	if(pxdatalist1.size()==0){
        		String where1 = "AND USER_CODE is null ";
        		 pxdatalist1 = ServDao.finds("TS_BMSH_PX", where1);
        	}
	        //查询出所有的 待审核记录
	        OutBean outBean = query(paramBean);
	        LinkedHashMap<String, Bean> cols1 = outBean.getCols();
	        for (Bean bean : dataList) {
			String work_num = bean.getStr("BM_CODE");
			Bean userBean =getUserInfo1(work_num);
			Bean newBean = new Bean();
			//for循环排序bean
			for(Bean pxbean : pxdatalist1){
				String aa = pxbean.getStr("PX_NAME");
				String namecol = pxbean.getStr("PX_COLUMN");
				String pxcol = namecol;
				 Bean colBean = new Bean();
				 
				 colBean.set("SAFE_HTML", "");
				 colBean.set("ITEM_LIST_FLAG", "1");
				 colBean.set("ITEM_CODE",namecol);
				 colBean.set("EN_JSON","");
				 colBean.set("ITEM_NAME",aa);
				 cols.put(pxcol, colBean);
				 
				//字段
				//如果 有值 赋值
				 String name = bean.getStr(namecol);
				if(!"".equals(bean.getStr(namecol))){
					newBean.set(namecol, bean.getStr(namecol));
				}
				if(!"".equals(userBean.getStr(namecol))){
					newBean.set(namecol, userBean.getStr(namecol));
					name = userBean.getStr(namecol);
				}
				if("".equals(bean.getStr(namecol))&&"".equals(userBean.getStr(namecol))){
					newBean.set(namecol,"");
				}
				if("".equals(name)){
					
				if("JOB_LB".equals(namecol)){
					name = bean.getStr("BM_LB");
	    			}
	    			if("JOB_XL".equals(namecol)){
	    				name = bean.getStr("BM_XL");
	    			}
	    			if("TONGYI".equals(namecol)){
	    				name=bean.getStr("BM_CODE");
	    			}
	    			String BM_TYPE = "";
	    			if("BM_TYPE".equals(namecol)){
	    				if("1".equals(aa)){
	    					BM_TYPE="初级";
	    				}else if("2".equals(aa)){
	    					BM_TYPE="中级";
	    				}else{
	    					BM_TYPE="高级";
	    				}
	    				name = BM_TYPE;
	    			}
	    			newBean.set(namecol, name);
				}
				newBean.set("_ROWNUM_","");
				newBean.set("ROWNUM_","");
			}
			finalList.add(newBean);
			
		}
        ExportExcel expExcel = new ExportExcel(serv);
        try {
        	 //查询出 要导出的数据
	            count = outBean.getCount();
            //总数大于excel可写最大值
            if (count > EXCEL_MAX_NUM) {
                return new OutBean().setError("导出数据总条数大于Excel最大行数：" + EXCEL_MAX_NUM);
            }
            //导出第一次查询数据
            paramBean.setQueryPageNowPage(1); //导出当前第几页
            afterExp(paramBean, outBean);   //执行导出查询后扩展方法
        //查询出表头  查询出 对应数据    hashmaplist
            expExcel.createHeader(cols);
            expExcel.appendData1(finalList, paramBean);
            // 存在多页数据
            if (ONETIME_EXP_NUM < count) {
                times = count / ONETIME_EXP_NUM;
                // 如果获取的是整页数据
                if (ONETIME_EXP_NUM * times == count && count != 0) {
                    times = times - 1;
                        }
                for (int i = 1; i <= times; i++) {
                    paramBean.setQueryPageNowPage(i + 1); // 导出当前第几页
                    OutBean out = query(paramBean);
                    afterExp(paramBean, out); // 执行导出查询后扩展方法
                    expExcel.appendData(out.getDataList(), paramBean);
                    }
                }
            expExcel.addSumRow();
        } catch (Exception e) {
            log.error("导出Excel文件异常" + e.getMessage(), e);
        } finally {
            expExcel.close();
            }
        return new OutBean().setOk();
        }
  	/**
	 * 获取用户信息
	 */
	public Bean getUserInfo1(String s){
		Bean returnBean = new Bean();
		Bean outBean = new Bean();
		try{
		//根据人力编码获取人力信息
		UserBean userBean = UserMgr.getUser(s);
	    
    	//获取当前机构;
    	DeptBean oneodeptcode1= userBean.getODeptBean();
    	String codes = "";
    	if(oneodeptcode1!=null){
    		//获取所有逗号分隔的字符串
    		codes = getusercodes(oneodeptcode1,s);
    	}
    		String[] codesarr = codes.split(",");
    	
    		int j=6;
    	for(int i=codesarr.length-1;i>=0;i--){
    		//最后一个 deptcodename
    		String evname = codesarr[i];
    		j--;
    		outBean.set("LEVEL"+j,evname);
    	}
    
    	//性别
    	int user_sex = userBean.getSex();
    	if(user_sex==0){
    		outBean.set("USER_SEX","男");
    	}else{
    		outBean.set("USER_SEX","女");
    	}
    	//入行时间
    	String date = userBean.getStr("USER_CMPY_DATE");
    	outBean.set("USER_CMPY_DATE",date);
    	//办公电话
    	String office_phone = userBean.getOfficePhone();
    	outBean.set("USER_OFFICE_PHONE", office_phone);
    	//手机号码
    	String user_phone = userBean.getMobile();
    	outBean.set("USER_MOBILE",user_phone);
    	//职务层级
    	String cengji = userBean.getPost();
    	outBean.set("USER_POST_LEVEL",cengji);
		}catch(Exception exception){
			
		}
		
		return returnBean;
	}
	/**
	 * 获取所有部门信息
	 * @param oneodeptcode1
	 * @return
	 */
	  public String getusercodes(DeptBean oneodeptcode1,String codes){
  		DeptBean newBean= oneodeptcode1.getParentDeptBean();
  		if(newBean!=null){
  			String s = newBean.getName()+",";
  			return  codes +=getusercodes(newBean,s);
  		}else{
  			
  			return "";
  		}
  }
}