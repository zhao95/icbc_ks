package com.rh.ts.jkgl;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rh.core.base.Bean;
import com.rh.core.base.BeanUtils;
import com.rh.core.base.Context;
import com.rh.core.comm.FileMgr;
import com.rh.core.comm.file.TempFile;
import com.rh.core.comm.file.TempFile.Storage;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Strings;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

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
    /**
     * 判断当前报名人是否在禁考名单中
     */
    public OutBean getjkstate(Bean paramBean){
    	OutBean out = new OutBean();
    	//判读审核规则里有没有 禁考规则 有的话  先验证禁考  没有不用验证
    	String xmid = paramBean.getStr("xmid");
    	String where1 = "AND XM_ID='"+xmid+"'";
    	List<Bean> guizelist = ServDao.finds("ts_xmgl_bm_jkgz", where1);
    	boolean flag = false;
    	String gzid = "";
    	for (Bean bean : guizelist) {
				//启用禁考规则
				gzid=bean.getStr("GZ_ID");
				//判断此人禁考类型是否和项目禁考类型一致不一致不再往下进行
				 flag = true;
				 
		}
    	if(flag){
    	UserBean userBean = Context.getUserBean();
    	String str = userBean.getStr("USER_CODE");
    	Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String format = sdf.format(date);
    	String where ="AND JKGL_RLZY='"+str+"' AND '"+format+"' BETWEEN JKGL_START_DATE AND JKGL_END_DATE";
    	List<Bean> finds = ServDao.finds("TS_JKGL",where);
    	String str2 = "";
    	if(finds!=null && finds.size()!=0){
    		//被禁考了  判断此人禁考结束时间 是否在配置信息中的时间之前
    		String where3 = "AND GZ_ID='"+gzid+"' order by MX_SORT asc";
    		List<Bean> finds2 = ServDao.finds("ts_xmgl_bm_jkglgz_mx", where3);
    		
    		for (Bean bean2 : finds2) {
    			String lk=bean2.getStr("MX_NAME");
    			String  jsonStr =  bean2.getStr("MX_VALUE2");
    			if(lk.indexOf("jktype")>-1){
    				JSONArray obj;

    				try {
    					obj = new JSONArray(jsonStr);
    					List<String> s = new ArrayList<String>();
    					for(int i=0;i<obj.length();i++){
    						JSONObject jsonObject = obj.getJSONObject(i);
    						s.add((String) jsonObject.get("code"));
    					}
    					for (String string : s) {
    						if(string.equals(finds.get(0).getStr("JKGL_TYPE"))){
    							//包含此人 的禁考类型
    							flag = false;
    						}
    						
    					}
    			}catch (Exception e){
    				
    			}
			}
    		}
    		
    		if(flag){
    			//true 不包含
    			//如果是false 则包含此人的 禁考类型
    			return new OutBean().set("num",0);
    		}
    		
    		
    		boolean flagbm = false;
    		for (Bean bean : finds2) { 
    			String lk=bean.getStr("MX_NAME");
    			if(lk.indexOf("reason")>-1){
    				 str2 = bean.getStr("MX_NAME");

	    			 str2 = str2.replace("#stime#", finds.get(0).getStr("JKGL_START_DATE"));
	    			 str2=str2.replace("#endtime#", finds.get(0).getStr("JKGL_END_DATE"));
	    			 str2=str2.replace("#reason#", finds.get(0).getStr("JKGL_REASON"));
	    			 String start = bean.getStr("JKGL_START_DATE");
	    			String endd =  finds.get(0).getStr("JKGL_END_DATE");
	    			String reason = finds.get(0).getStr("JKGL_REASON");
		    		out.set("start",start);
		    		out.set("end",endd);
		    		out.set("reason",reason);

    			}else if(lk.indexOf("dataTime")>-1){
    				//时间
    				String str3 = bean.getStr("MX_VALUE2");
    				try {
						JSONArray JSON  = new JSONArray(str3);
						JSONObject jsonObject = JSON.getJSONObject(0);
						//时间
						String string = jsonObject.getString("val");
						SimpleDateFormat simp = new SimpleDateFormat("yyyyMMdd");
						SimpleDateFormat simp2 = new SimpleDateFormat("yyyy-MM-dd");
						try {
							Date parse = simp.parse(string);
						String end = finds.get(0).getStr("JKGL_END_DATE");
						Date parse2 = simp2.parse(end);
						
						if(parse2.getTime()>parse.getTime()){
							//超过配置时间 不可报名
							flagbm= true;
						}else{
						}
						
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				
    			}
			}
    		
    		if(flagbm){
    			//不可报名
    			out.set("num", finds.size());
    			out.set("tsh", str2);
    		}else{
    			out.set("num",0);
    		}
    	}else if(finds!=null && finds.size()==0){
    		out.set("num", 0);
    	}
    	}else{
    		out.set("num",0);
    	}
    	return out;
    }
    
    public OutBean imp(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        
        String servId = paramBean.getServId();
        ServDefBean servDef = ServUtils.getServDef(servId);
        LinkedHashMap<String, Bean> titleMap = BeanUtils.toLinkedMap(servDef.getTableItems(), "ITEM_NAME");
        String fileId = paramBean.getStr("fileId");
        Bean fileBean = FileMgr.getFile(fileId);
        if (fileBean != null && fileBean.getStr("FILE_MTYPE").equals("application/vnd.ms-excel")) { //只支持excel类型
            Workbook book = null;
            InputStream in = null;
            OutputStream os = null;
            TempFile tempFile = null;
            boolean isOk = true;
            try {
                in = FileMgr.download(fileBean);
                log.debug("imp---->open file ");
                //打开文件 
                try {
                    book = Workbook.getWorkbook(in) ;
                } catch(Exception e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException("请使用excel 2003导入!");
                }
                
                tempFile = new TempFile(Storage.SMART);
				os = tempFile.getOutputStream();
				
				log.debug("imp---->open file 1");
				WritableWorkbook wbook = Workbook.createWorkbook(os, book);
				WritableSheet wSheet = wbook.getSheet(0);
				
                //取得第一个sheet  
                Sheet sheet = book.getSheet(0);
                final int titleRowNum = getImpTitleRowNum();
                //取得第一行数据，这里非常重要，只要遇到空的cell，则忽略且不再往后处理
                Cell[] titleCell = sheet.getRow(titleRowNum);
                int cols = titleCell.length;
                Bean[] itemMaps = new Bean[cols];
                final int beginColNum = getImpContentBeginRowNum();
                log.debug("imp---->read Header");
                for (int j = 0; j < cols; j++) { //第一行标题列，进行标题与字段的自动匹配，优先匹配中文名称，其次配置编码
                    String title = sheet.getCell(j, titleRowNum).getContents();
                    if (StringUtils.isEmpty(title)) { //标题为空，则忽略该列，且不再往后处理
                    	cols = j;
                    	itemMaps = new Bean[cols];
                    }
                    Bean itemMap = null;
                    if (titleMap.containsKey(title)) {
                        itemMap = titleMap.get(title);
                    } else {
                        itemMap = servDef.getItem(title);
                    }
                    if (itemMap != null) {
                        itemMaps[j] = itemMap;
                    }
                }
                log.debug("imp---->read data");
                //逐行插入
                for(int i = beginColNum; i < sheet.getRows(); i++) {
                	
                	boolean isok = true;
                	
                	String error = "";
                	
                    Cell [] cell = sheet.getRow(i);
                    Bean data = new Bean();
                    
                    String org = "";
                    
                    for(int j = 0; j < cell.length && j < cols; j++) {
                        if (itemMaps[j] != null) {
                            String value = sheet.getCell(j, i).getContents();
                            
                            if (itemMaps[j].isNotEmpty("DICT_ID")) { //字典处理名称和值的转换
                                String dictVal = DictMgr.getItemCodeByName(itemMaps[j].getStr("DICT_ID"), value);
                                if (dictVal != null) {
                                    value = dictVal;
                                }
                            }
                            
							if (j == 1) { // 人员编码

								UserBean user = null;

								try {
									user = UserMgr.getUser(value);
								} catch (Exception e) {
								}

								if (user != null && !user.isEmpty()) {
									org = user.getDeptCode();
								} else {
									error = "无效的人力资源编码";
									isok = false;
									break;
								}
							}
                            
                            if(j==2){ //部门
								value = org;
                            }
                            
                            
                            data.set(itemMaps[j].getStr("ITEM_CODE"), value);
                        }
                    }
                    
                    if(isok) {
	                    //校验该行数据为空行，则continue
	                    if (this.isEmptyBeanByItems(cell, itemMaps, cols, data)) {
							continue;
						}
	                    
	                    //试图保存每一条数据
	                    
	                    try {
	                    	error = getExcelRowDataError(data);
	                    	if (StringUtils.isEmpty(error)) { //数据校验通过
	                    		ServMgr.act(servId, ServMgr.ACT_SAVE, new ParamBean(data));
	                    	}
	                    } catch (Exception e) {
	                    	error = e.getMessage();
	                    }
                    }
                    
                    WritableFont font = new WritableFont(WritableFont.COURIER);
					font.setColour(Colour.GREEN);
					WritableCellFormat format = new WritableCellFormat(font);
					format.setAlignment(Alignment.CENTRE);
                    if (StringUtils.isEmpty(error)) { //没有出现错误信息
                    	Label label = new Label(cols, i, "成功", format);
						wSheet.addCell(label);
                    } else {
                    	font.setColour(Colour.RED);
						Label label = new Label(cols, i, "失败", format);
						wSheet.addCell(label);
						isOk = false;
						Label label2 = new Label(cols + 1, i, error, format);
						wSheet.addCell(label2);
                    }
                }
                
                log.debug("imp---->close File");
                //关闭文件  
                wbook.write();
				wbook.close();
				
				fileBean.set("FILE_NAME", fileBean.getStr("DIS_NAME")+"-导入结果.xls");
				Bean newFileBean = this.saveTempFile(fileBean, tempFile);
				
				if(newFileBean != null) {
					outBean.set("FILE_ID", newFileBean.getId());
				}
				
            } catch (Exception e) {
            	outBean.setError("导入失败:文件格式错误，"+e.getMessage());
            	isOk = false;
                log.error(e.getMessage(), e);  
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(os);
            }
            
            if(!isOk) {
//            	outBean.setError("导入失败，请查看文件。");
            } else {
            	tempFile.destroy();
            }
        } else { //错误的文件内容或格式
            outBean.setError("无效的上传文件。");
        }
        FileMgr.deleteFile(fileBean); //最后删除临时上传的文件
        return outBean;
    }

}
