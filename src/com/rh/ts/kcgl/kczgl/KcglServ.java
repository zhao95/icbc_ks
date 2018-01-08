package com.rh.ts.kcgl.kczgl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;

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

public class KcglServ extends CommonServ{
    private static final int ONETIME_EXP_NUM = 20000;
    private static final String servId1 = "TS_KCZGL_KCGL";
    
    /**
     * 考场组添加考场
     * @param paramBean
     * @return
     */
    public  OutBean kczAddKc(ParamBean paramBean) {
	String kcIds = paramBean.getStr("ids");
	String groupId = paramBean.getStr("groupId");
	for (int i = 0; i < kcIds.split(",").length; i++) {
	    String kcId = kcIds.split(",")[i];
	    
	    if (Strings.isBlank(kcId)) {
			continue;
		}
	    Bean kcBean = ServDao.find("TS_KCGL", kcId);
	    kcBean = delSysCol(kcBean);
	    kcBean.set("GROUP_ID", groupId);
	    kcBean.set("SERV_ID", servId1);
	    kcBean.set("COPY_ID", kcBean.getId());
	    kcBean.setId("");
	    kcBean.remove("KC_ID");
	    //主表数据复制
	    Bean resBean = ServDao.save(servId1, kcBean);
	    String resBeanId = resBean.getId();
	    //子表数据复制	 
	    List<Bean> list = ServDao.finds("SY_SERV_LINK", "and SERV_ID = 'TS_KCZGL_KCGL' and S_FLAG = 1");
	    for (int j = 0; j < list.size(); j++) {
		String linkServId = list.get(j).getStr("LINK_SERV_ID");
		List<Bean> linkList = ServDao.finds(linkServId, "and KC_ID = '"+kcId+"'");
		for (int k = 0; k < linkList.size(); k++) {
		    Bean linkBean = linkList.get(k);
		    linkBean = delSysCol(linkBean);
		    linkBean.setId("");
		    linkBean.remove(primaryCode(linkServId));
		    linkBean.set("KC_ID", resBeanId);
		    ServDao.save(linkServId, linkBean);
		 }
	    }
	}
	OutBean outBean = new OutBean();
	outBean.setOk();
	return outBean;
    }
    /**
     * 根据服务ID 取得主键编码
     * @param servId
     * @return
     */
    public String primaryCode(String servId){
	Bean bean = ServDao.find("SY_SERV", servId);
	return bean.getStr("SERV_KEYS");
    }
    /**
     * 更新考场信息
     * @param paramBean
     * @return
     */
    public OutBean updateKcInfo(ParamBean paramBean){
	OutBean outBean = new OutBean();
	String pkCodes = paramBean.getStr("pkCodes");
	String servId = paramBean.getStr("servId");
	for (int i = 0; i < pkCodes.split(",").length; i++) {
	    String dataId = pkCodes.split(",")[i];
	    Bean bean = ServDao.find(servId, dataId);
	    //此条考场属于的组ID	    
	    String GROUP_ID = bean.getStr("GROUP_ID");
	    //此条考场数据是从哪条数据拷贝过来的
	    String COPY_ID = bean.getStr("COPY_ID");
	    delLinkServInfo(servId,dataId);
	    Bean kcBean = ServDao.find("TS_KCGL", COPY_ID);
	    kcBean = delSysCol(kcBean);
	    kcBean.setId(dataId);
	    kcBean.remove("KC_ID");
	    kcBean.set("GROUP_ID", GROUP_ID);
	    kcBean.set("COPY_ID", COPY_ID);
	    kcBean.set("SERV_ID", servId1);
	    //考场组管理 考场表
	    ServDao.save(servId1, kcBean);
	    List<Bean> list = ServDao.finds("SY_SERV_LINK", "and SERV_ID = 'TS_KCZGL_KCGL' and S_FLAG = 1");
	    for (int j = 0; j < list.size(); j++) {
		String linkServId = list.get(j).getStr("LINK_SERV_ID");
		if (linkServId == "TS_XMGL_KCAP_DAPCC") {
		    continue;
		}
		List<Bean> linkList = ServDao.finds(linkServId, "and KC_ID = '"+COPY_ID+"'");
		for (int k = 0; k < linkList.size(); k++) {
		    Bean linkBean = linkList.get(k);
		    linkBean = delSysCol(linkBean);
		    linkBean.setId("");
		    linkBean.remove(primaryCode(linkServId));
		    linkBean.set("KC_ID", dataId);
		    ServDao.save(linkServId, linkBean);
		}
	    }
	}
	outBean.setOk();
	return outBean;
    }
    
    /**
     * 删除关联数据
     * @param servId
     * @param dataId 注：kc_id是关联字段
     */
    public void delLinkServInfo(String servId,String dataId){
	 List<Bean> list = ServDao.finds("SY_SERV_LINK", "and SERV_ID = '"+servId+"' and S_FLAG = 1");
	 for (int j = 0; j < list.size(); j++) {
		String linkServId = list.get(j).getStr("LINK_SERV_ID");
		Bean whereBean = new Bean();
		whereBean.set("KC_ID", dataId);
		ServDao.deletes(linkServId, whereBean);
	 }
    }
    
    /**
     * 删除系统字段
     * @param bean
     * @return
     */
    public Bean delSysCol(Bean bean){
	bean.remove("S_USER");
	bean.remove("S_DEPT");
	bean.remove("S_TDEPT");
	bean.remove("S_ODEPT");
	bean.remove("S_ATIME");
	bean.remove("S_MTIME");
	return bean;
    }
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
        String CTLG_PCODE = paramBean.getStr("CTLG_PCODE");
        
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
                    data.set("CTLG_PCODE", CTLG_PCODE);
                    data.set("SERV_ID", servId);
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
