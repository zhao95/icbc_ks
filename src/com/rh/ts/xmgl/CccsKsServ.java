package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.BeanUtils;
import com.rh.core.comm.FileMgr;
import com.rh.core.comm.file.TempFile;
import com.rh.core.comm.file.TempFile.Storage;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.serv.*;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.ExpUtils;
import com.rh.core.util.ImpUtils;
import com.rh.ts.pvlg.PvlgUtils;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;

public class CccsKsServ extends CommonServ {
    // 查询前添加查询条件
    protected void beforeQuery(ParamBean paramBean) {
        ParamBean param = new ParamBean();
        param.set("paramBean", paramBean);
        param.set("serviceName", paramBean.getServId());
        param.set("fieldName", "S_ODEPT");

        PvlgUtils.setOrgPvlgWhere(param);
    }


    /**
     * 导入方法开始的入口
     */
    public OutBean saveFromExcel(ParamBean paramBean) {
        return ServMgr.act("TS_XMGL_KCAP_DFPKS", "saveFromExcel", paramBean);
//        String fileId = paramBean.getStr("FILE_ID");
//        //方法入口
//        paramBean.set("SERVMETHOD", "impDataSave");
//        OutBean out = ImpUtils.getDataFromXls(fileId, paramBean);
//        String failNum = out.getStr("failernum");
//        String successNum = out.getStr("oknum");
//        //返回导入结果
//        return new OutBean().set("FILE_ID", out.getStr("fileid")).setOk("导入成功：" + successNum + "条,导入失败：" + failNum + "条");
    }

    /**
     * 导入保存方法
     *
     * @param paramBean paramBean
     * @return outBean
     */
    public OutBean impDataSave(ParamBean paramBean) {
        return ServMgr.act("TS_XMGL_KCAP_DFPKS", "impDataSave", paramBean);
    }

    /**
     * 导出全部
     *
     * @param paramBean paramBean XM_ID
     * @return outBean
     */
    public OutBean expAll(ParamBean paramBean) {
        String xmId = paramBean.getStr("XM_ID");
        SqlBean sqlBean = new SqlBean();
        sqlBean.and("XM_ID", xmId);
        List<Bean> allList = ServDao.finds("TS_XMGL_CCCS_KSGL", sqlBean);

        for (Bean bean : allList) {
            String userCode = bean.getStr("BM_CODE");
            ParamBean userCodeParamBean = new ParamBean();
            userCodeParamBean.set("userCode", userCode);
            OutBean userOrgBean = ServMgr.act("TS_XMGL_KCAP_DAPCC", "getUserOrg", userCodeParamBean);
            bean.putAll(userOrgBean);

            String odeptCodeV = bean.getStr("S_ODEPT");
            bean.set("ODEPT_V_NAME", OrgMgr.getDept(odeptCodeV).getName());
        }
        /*设置导出展示信息*/
        LinkedHashMap<String, String> colMap = new LinkedHashMap<String, String>();
        colMap.put("BM_NAME", "姓名");
        colMap.put("BM_CODE", "人力资源编码");
        colMap.put("BM_LB", "考试类别");
        colMap.put("BM_XL", "序列");
        colMap.put("BM_MK", "模块");
        colMap.put("BM_TYPE_NAME", "级别");
        colMap.put("BM_KS_TIME", "考试时长");
        colMap.put("org1", "一级机构");
        colMap.put("org2", "二级机构");
        colMap.put("ODEPT_V_NAME", "所属机构");

        return ExpUtils.expUtil(allList, colMap, paramBean);

    }


    public OutBean imp(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        /***********/
        String xmId = paramBean.getStr("XM_ID");
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
                    book = Workbook.getWorkbook(in);
                } catch (Exception e) {
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
                for (int i = beginColNum; i < sheet.getRows(); i++) {
                    Cell[] cell = sheet.getRow(i);
                    Bean data = new Bean();
                    /****************/
                    data.set("XM_ID", xmId);
                    data.set("SH_LEVEL", 1);
                    data.set("BM_STATUS", 0);
                    /******************/
                    for (int j = 0; j < cell.length && j < cols; j++) {
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
                            Bean bmBean = ServDao.find("TS_ORG_USER_ALL", data.getStr("BM_CODE"));
                            data.set("BM_NAME", bmBean.getStr("USER_NAME"));
                            data.set("JK_ODEPT", bmBean.getStr("ODEPT_CODE"));
                            String KSLBK_NAME = data.getStr("BM_LB");
                            String KSLBK_XL = data.getStr("BM_XL");
                            String KSLBK_MK = data.getStr("BM_MK");
                            String KSLBK_TYPE_NAME = data.getStr("BM_TYPE_NAME");
                            List<Bean> typeList = ServDao.finds("TS_XMGL_BM_KSLBK", " and KSLBK_NAME = '" + KSLBK_NAME + "' and KSLBK_XL = '" + KSLBK_XL + "' and KSLBK_MK ='" + KSLBK_MK + "' and KSLBK_TYPE_NAME = '" + KSLBK_TYPE_NAME + "'");
                            if (typeList.size() > 0) {
                                Bean typebean = typeList.get(0);
                                data.set("BM_LB_CODE", typebean.getStr("KSLBK_CODE"));
                                data.set("BM_XL_CODE", typebean.getStr("KSLBK_XL_CODE"));
                                data.set("BM_MK_CODE", typebean.getStr("KSLBK_MKCODE"));
                                data.set("BM_TYPE", typebean.getStr("KSLBK_TYPE"));
                                data.set("BM_KS_TIME", typebean.getStr("KSLBK_TIME"));
                                data.set("KSLBK_ID", typebean.getStr("KSLBK_ID"));
                            }
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

                fileBean.set("FILE_NAME", fileBean.getStr("DIS_NAME") + "-导入结果.xls");
                Bean newFileBean = saveTempFile(fileBean, tempFile);

                if (newFileBean != null) {
                    outBean.set("FILE_ID", newFileBean.getId());
                }

            } catch (Exception e) {
                outBean.setError("导入失败:文件格式错误，" + e.getMessage());
                isOk = false;
                log.error(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(os);
            }

            if (!isOk) {
//	            	outBean.setError("导入失败，请查看文件。");
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
