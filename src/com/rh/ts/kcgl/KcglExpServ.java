package com.rh.ts.kcgl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.BeanUtils;
import com.rh.core.comm.FileMgr;
import com.rh.core.comm.file.TempFile;
import com.rh.core.comm.file.TempFile.Storage;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.serv.util.ExportExcel;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.var.Var;

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

public class KcglExpServ extends CommonServ{
    private static final int ONETIME_EXP_NUM = 20000;
    public OutBean exp(ParamBean paramBean) {
   	String servId = paramBean.getServId();
   	ServDefBean serv = ServUtils.getServDef(servId);
   	long count = 0;
   	long times = 0;
   	paramBean.setQueryPageShowNum(ONETIME_EXP_NUM); // 设置每页最大导出数据量
   	beforeExp(paramBean); // 执行监听方法
   	if (paramBean.getId().length() > 0) { // 支持指定记录的导出（支持多选）
   	    String searchWhere = " and " + serv.getPKey() + " in ('" + paramBean.getId().replaceAll(",", "','") + "')";
   	    paramBean.setQuerySearchWhere(searchWhere);
   	}
   	ExportExcel expExcel = new ExportExcel(serv);
   	try {
   	    OutBean outBean = queryExp(paramBean);
   	    count = outBean.getCount();
   	    // 导出第一次查询数据
   	    paramBean.setQueryPageNowPage(1); // 导出当前第几页
   	    afterExp(paramBean, outBean); // 执行导出查询后扩展方法
   	    LinkedHashMap<String, Bean> cols = outBean.getCols();
   	    cols.remove("BUTTONS");
   	    expExcel.createHeader(cols);
   	    expExcel.appendData(outBean.getDataList(), paramBean);

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
    
    public OutBean imp(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        /***********/
        String kcId = paramBean.getStr("KC_ID");
        String kczId = paramBean.getStr("KCZ_ID");
        /*************/
        beforeImp(paramBean); //执行监听方法
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
                    Cell [] cell = sheet.getRow(i);
                    Bean data = new Bean();
                    /****************/
                    if(servId.equals("TS_KCZGL_GROUP")){
                	data.set("KCZ_ID", kczId);
                	data.set("SERV_ID", servId);
                    }else{
                	data.set("KC_ID", kcId);
                    }
                    /******************/
                    for(int j = 0; j < cell.length && j < cols; j++) {
                        if (itemMaps[j] != null) {
                            String value = sheet.getCell(j, i).getContents();
                            if (itemMaps[j].isNotEmpty("DICT_ID")) { //字典处理名称和值的转换
                                String dictVal = DictMgr.getItemCodeByName(itemMaps[j].getStr("DICT_ID"), value);
                                if (dictVal != null) {
                                    value = dictVal;
                                }
                            }
                            data.set(itemMaps[j].getStr("ITEM_CODE"), value);
                        }
                    }
                    //校验该行数据为空行，则continue
                    if (isEmptyBeanByItems(cell, itemMaps, cols, data)) {
						continue;
					}
                   
                    //试图保存每一条数据
                    String error = "";
                    try {
                    	error = getExcelRowDataError(data);
                    	
                    	if(servId.equals("TS_KCGL_ZWDYB")){
                    	    Bean kc1 = ServDao.find("TS_KCGL_UTIL", kcId);
                    	    int maxNum = 0;
                    	    if(!kc1.isEmpty()){
                    		maxNum = kc1.getInt("KC_MAX");
                    	    }
                    	    int zwNum = ServDao.count("TS_KCGL_ZWDYB", new ParamBean().setWhere("and kc_id = '"+kcId+"'"));
                    	    if(zwNum >= maxNum){
                    		error = "座位数不能大于考场最大设备数";
                    	    }
                    	}
                    	
                    	//验证座位号IP和系统座位号
                    	
			if (servId.equals("TS_KCGL_ZWDYB") && (error == null || error.isEmpty())) {
			    String zwh = data.getStr("ZW_ZWH_XT");
			    String IPStr = data.getStr("ZW_IP");

			    String pattern_zwh = "^([0-9]{1,3})-([0-9]{1,3})$";
			    String pattern_ip = "^([0-9]{1,3}).([0-9]{1,3}).([0-9]{1,3}).([0-9]{1,3})$";
			    boolean zwhMatch = Pattern.matches(pattern_zwh, zwh);
			    boolean IPMatch = Pattern.matches(pattern_ip, IPStr);
			    if (!zwhMatch) {
				error = "座位号格式不正确";
			    }
			    if (!IPMatch) {
				error = "IP地址格式不正确";
			    }
			    
			    int num_zwh = ServDao.count("TS_KCGL_ZWDYB", new ParamBean().setWhere("and kc_id = '"+kcId+"' and ZW_ZWH_XT ='"+zwh+"'"));
			    int num_ip = ServDao.count("TS_KCGL_ZWDYB", new ParamBean().setWhere("and kc_id = '"+kcId+"' and ZW_IP ='"+IPStr+"'"));
			    if(num_zwh > 0){
				error = "座位号已存在";
			    }
			    if(num_ip > 0){
				error = "IP地址已存在";
			    }
			    List<Bean> scopeList = ServDao.finds("TS_KCGL_IPSCOPE", "and KC_ID = '"+kcId+"'");
			    //是否在考场IP段范围内
			    boolean inFlag = false;
			    int c0 = Integer.parseInt(IPStr.split("\\.")[0]);
			    int c1 = Integer.parseInt(IPStr.split("\\.")[1]);
			    int c2 = Integer.parseInt(IPStr.split("\\.")[2]);
			    int c3 = Integer.parseInt(IPStr.split("\\.")[3]);
			    for (int j = 0; j < scopeList.size(); j++) {
				String tmpScope = scopeList.get(j).getStr("IPS_SCOPE");
				String a1 = tmpScope.split("-")[0];
				String a2 = tmpScope.split("-")[1];
				int b1_0 = Integer.parseInt(a1.split("\\.")[0]);
				int b1_1 = Integer.parseInt(a1.split("\\.")[1]);
				int b1_2 = Integer.parseInt(a1.split("\\.")[2]);
				int b1_3 = Integer.parseInt(a1.split("\\.")[3]);
				int b2_3 = Integer.parseInt(a2.split("\\.")[3]);
				if (b1_0 == c0 && b1_1 == c1 && b1_2 == c2) {
				    if (c3 >= b1_3 && c3 <= b2_3) {
					inFlag = true;
					break;
				    }
				}
			    }
			    
			    if(!inFlag){
				error = "IP地址不在IP段范围内";
			    }
			} else if (servId.equals("TS_KCGL_IPSCOPE") && (error == null || error.isEmpty())) {
			    String scope = data.getStr("IPS_SCOPE");
			    String[] sz = scope.split("-");
			    if (sz.length != 2) {
				error = "操作IP区段格式不正确";
			    } else {
				String a = sz[0];
				String b = sz[1];
				if (a.split("\\.").length != 4 || b.split("\\.").length != 4) {
				    error = "操作IP区段格式不正确";
				} else {
				    String pattern_ip = "^([0-9]{1,3}).([0-9]{1,3}).([0-9]{1,3}).([0-9]{1,3})$";
				    boolean IPMatch_a = Pattern.matches(pattern_ip, a);
				    boolean IPMatch_b = Pattern.matches(pattern_ip, b);

				    if (IPMatch_a && IPMatch_b) {
					boolean r1 = Integer.parseInt(a.split("\\.")[0]) != Integer
						.parseInt(b.split("\\.")[0]);
					boolean r2 = Integer.parseInt(a.split("\\.")[1]) != Integer
						.parseInt(b.split("\\.")[1]);
					boolean r3 = Integer.parseInt(a.split("\\.")[2]) != Integer
						.parseInt(b.split("\\.")[2]);
					if (r1 || r2 || r3) {
					    error = "操作IP区段格式不正确";
					} else {
					    int sa4 = Integer.parseInt(a.split("\\.")[3]);
					    int sb4 = Integer.parseInt(b.split("\\.")[3]);

					    if (sa4 > sb4) {
						error = "操作IP区段格式不正确";
					    }
					}
				    } else {
					error = "操作IP区段格式不正确";
				    }
				}

			    }
			}
                    	
                    	if (StringUtils.isEmpty(error)) { //数据校验通过
                    		ServMgr.act(servId, ServMgr.ACT_SAVE, new ParamBean(data));
                    	}
                    } catch (Exception e) {
                    	error = e.getMessage();
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
				Bean newFileBean = saveTempFile(fileBean, tempFile);
				
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
