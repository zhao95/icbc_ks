package com.rh.ts.kcgl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
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
import com.rh.core.serv.ServDefBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.serv.util.ServUtils;

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

public class KcglUpdateZwServ extends CommonServ{
    public OutBean imp(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        /***********/
        String updateId = paramBean.getStr("updateId");
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
                	data.set("UPDATE_ID", updateId);
                	data.set("ZW_ACTION","add");
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
                    	
                    	//验证座位号IP和系统座位号
			if (servId.equals("TS_KCGL_UPDATE_ZWDYB")) {
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
