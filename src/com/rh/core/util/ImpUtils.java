package com.rh.core.util;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.comm.FileMgr;
import com.rh.core.comm.file.TempFile;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;

import com.rh.core.serv.bean.SqlBean;
import com.rh.ts.util.TsConstant;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入工具类
 * Created by shenh on 2017/12/20.
 */
public class ImpUtils {

    private static Log log = LogFactory.getLog(ImpUtils.class);

    public final static String COL_NAME = "col";

    public final static String ERROR_NAME = "error";

    public final static String DATA_LIST = "datalist";

    public final static String ALL_LIST = "alllist";

    public final static String SUCCESS_LIST = "successlist";
    
    //服务id标识符
    public final static String SERV_ID = "SERV_ID";

    //服务id对应类方法名称
    public final static String SERV_METHOD_NAME = "SERVMETHOD";

    /**
     * 在excel中设置失败信息，返回fileId
     *
     * @param rowBeanList 通过getDataFromXls返回的rowBeanList
     * @return errorFileId
     * @throws WriteException 写入失败
     */
    public static void saveErrorAndReturnErrorFile(int i, WritableSheet wSheet, Sheet sheet, List<Bean> rowBeanList) throws WriteException {
        OutBean outBean = new OutBean();
        //获取下载excel  sheet页
//                final int titleRowNum = getImpTitleRowNum();
        //取得第一行数据，这里非常重要，只要遇到空的cell，则忽略且不再往后处理
//                int columns = sheet.getColumns();
//                Cell[] titleCell = sheet.getRow(titleRowNum);
        int cols = sheet.getColumns();
        for (int j = 0; j < rowBeanList.size(); j++) {
            Bean bean = rowBeanList.get(j);
            WritableFont font = new WritableFont(WritableFont.COURIER);
            font.setColour(Colour.GREEN);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            if (StringUtils.isEmpty(bean.getStr(ERROR_NAME))) {//没有出现错误信息
                Label label = new Label(cols, i + j - rowBeanList.size() + 1, "成功", format);
                wSheet.addCell(label);
            } else {
                font.setColour(Colour.RED);
                Label label = new Label(cols, i + j - rowBeanList.size() + 1, "失败", format);
                wSheet.addCell(label);
                Label label2 = new Label(cols + 1, i + j - rowBeanList.size() + 1, bean.getStr(ERROR_NAME), format);
                wSheet.addCell(label2);
            }
        }
    }

    /**
     * @param oldFileBean
     * @param tempFile
     * @return
     */
    public static Bean saveTempFile(Bean oldFileBean, TempFile tempFile) {
        try {
            Bean param = new Bean();
            param.set("SERV_ID", oldFileBean.getStr("SERV_ID"));
            param.set("FILE_CAT", "TEMP_EXCEL_IMP");
            param.set("DATA_ID", oldFileBean.getStr("DATA_ID"));
            param.set("FILE_NAME", oldFileBean.getStr("FILE_NAME"));
            param.set("FILE_MTYPE", oldFileBean.getStr("FILE_MTYPE"));
            param.set("S_FLAG", 2);
            return FileMgr.upload(param, tempFile.openNewInputStream());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            tempFile.destroy();
        }

        return null;
    }

    /**
     * 通过人力资源编码、登录名、身份证 获取 用户信息（userBean）
     *
     * @param str 资源编码、登录名、身份证
     * @return userBean 用户信息, null 找不到
     */
    public static UserBean getUserBeanByString(String str) {
        UserBean userBean = null;
        int length = str.trim().length();
        try {
            if (length == 9) {
                userBean = UserMgr.getUserByLoginName(str);
            } else if (length == 10) {
                userBean = UserMgr.getUser(str);
            } else if (length > 11) {
                List<Bean> userBeanList = ServDao.finds("SY_ORG_USER_ALL", " and USER_IDCARD ='" + str + "'");
                if (CollectionUtils.isNotEmpty(userBeanList)) {
                    userBean = UserMgr.getUser(userBeanList.get(0).getStr("USER_CODE"));
                }
            }
        } catch (Exception e) {
            userBean = null;
        }
        return userBean;
    }


    /**
     * 获取考试类别库Bean
     *
     * @param lbName   类别名称
     * @param xlName   序列名称
     * @param mkName   模块名称
     * @param typeName 级别名称
     * @return bean
     */
    public static Bean getKsLBKBean(String lbName, String xlName, String mkName, String typeName) {
        //查询考试类别kslb
        SqlBean sqlBean = new SqlBean();
        sqlBean.and("KSLBK_NAME", lbName);
        sqlBean.and("KSLBK_XL", xlName);
        if (StringUtils.isNotBlank(mkName)) {
            sqlBean.and("KSLBK_MK", mkName);
        }
        sqlBean.and("KSLBK_TYPE_NAME", typeName);
        return ServDao.find(TsConstant.SERV_BM_KSLBK, sqlBean);
    }

    /**
     * 通过fileId获取第一个sheet的内容
     *
     * @param fileId      文件id
     * @param colCodeList 列对应的属性名
     *                    例：["USER_CODE","USER_NAME"] 第一列的值key为USER_CODE，第二列的值key为USER_NAME
     * @return outBean  rowBeans(List<Bean>)
     * @throws TipException 文件不存在，请重试 / Excel文件解析错误，请校验！
     */
    /*
    public static List<Bean> getDataFromXls(String fileId, List<String> colCodeList) {
        List<Bean> rowBeans = getDataFromXls(fileId);
        for (Bean rowBean : rowBeans) {
            for (int i = 0; i < colCodeList.size(); i++) {
                String colCode = colCodeList.get(i);
                String key = COL_NAME + (i + 1);
                rowBean.set(colCode, rowBean.getStr(key));
                rowBean.remove(key);
            }
        }
        return rowBeans;
    }*/

    /**
     * 通过fileId获取第一个sheet的内容
     *
     * @param fileId 文件id
     * @return outBean  rowBeans(List<Bean>)
     * @throws TipException 文件不存在，请重试 / Excel文件解析错误，请校验！
     */
    public static OutBean getDataFromXls(String fileId, ParamBean paramBean) {
        List<Bean> result = new ArrayList<Bean>();
        int successnum = 0;
        int failernum = 0;
        String servId = paramBean.getStr(SERV_ID);
        if (StringUtils.isBlank(servId)) {
            servId = paramBean.getServId();//服务名
        }

        String method = paramBean.getStr(SERV_METHOD_NAME);//方法名
        InputStream in = null;
        OutputStream os = null;
        TempFile tempFile = null;
        Bean fileBean = null;
        Bean newFileBean = null;
        try {
            fileBean = FileMgr.getFile(fileId);
            in = FileMgr.download(fileBean);
        } catch (Exception e) {
            throw new TipException("文件不存在，请重试");
        }
        Workbook workbook = null;
        try {
            Sheet sheet1;
            WritableSheet wSheet;
            WritableWorkbook wbook;
            try {
                workbook = Workbook.getWorkbook(in);
                sheet1 = workbook.getSheet(0);

                tempFile = new TempFile(TempFile.Storage.SMART);
                os = tempFile.getOutputStream();

                log.debug("imp---->open file 1");
                wbook = Workbook.createWorkbook(os, workbook);
                wSheet = wbook.getSheet(0);

            } catch (Exception e) {
                throw new TipException("Excel文件解析错误，请校验！");
            }

            int rows = sheet1.getRows();
            //rows大于500时 分批次  
            //rows小于500一次导出
            for (int i = 1; i < rows; i++) {
                Cell[] cells = sheet1.getRow(i);
                Bean rowBean = new Bean();
                for (int j = 0; j < cells.length; j++) {
                    Cell cell = cells[j];
                    String content = cell.getContents();
                    rowBean.set(COL_NAME + "" + (j + 1), content);
                }
                result.add(rowBean);

                if (rows < 500) {
                    if (i == rows - 1) {
                        //每五百条进行  一次轮询   对数据进行处理
                        paramBean.set(DATA_LIST, result);
                        Bean resultres = ServMgr.act(servId, method, paramBean);
                        List<Bean> rowBeanList = resultres.getList(ALL_LIST);
                        List<Object> successlist = resultres.getList(SUCCESS_LIST);
                        successnum += successlist.size();
                        failernum += rowBeanList.size() - successlist.size();
                        saveErrorAndReturnErrorFile(i, wSheet, sheet1, rowBeanList);
                    }
                } else if (rows >= 500) {
                    if (result.size() == 500) {
                        //每五百条进行  一次轮询   对数据进行处理
                        paramBean.set(DATA_LIST, result);
                        Bean resultres = ServMgr.act(servId, method, paramBean);
                        List<Bean> rowBeanList = resultres.getList(ALL_LIST);
                        List<Object> successlist = resultres.getList(SUCCESS_LIST);
                        successnum += successlist.size();
                        failernum += rowBeanList.size() - successlist.size();
                        saveErrorAndReturnErrorFile(i, wSheet, sheet1, rowBeanList);
                        result = null;
                        result = new ArrayList<Bean>();
                    } else if (i == rows - 1) {
                        //每五百条进行  一次轮询   对数据进行处理
                        paramBean.set(DATA_LIST, result);
                        Bean resultres = ServMgr.act(servId, method, paramBean);
                        List<Bean> rowBeanList = resultres.getList(ALL_LIST);
                        List<Object> successlist = resultres.getList(SUCCESS_LIST);
                        successnum += successlist.size();
                        failernum += rowBeanList.size() - successlist.size();
                        saveErrorAndReturnErrorFile(i, wSheet, sheet1, rowBeanList);
                        result = null;
                        result = new ArrayList<Bean>();
                    }
                }
            }
            log.debug("imp---->close File");
            //关闭文件
            wbook.write();
            wbook.close();

            fileBean.set("FILE_NAME", fileBean.getStr("DIS_NAME") + "-导入结果.xls");
            newFileBean = saveTempFile(fileBean, tempFile);

        } catch (WriteException e) {
            throw new RuntimeException("导入结果填写错误");
        } catch (IOException e) {
            throw new RuntimeException("导入结果填写错误");
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(os);
            FileMgr.deleteFile(fileId);
            tempFile.destroy();
        }
        return new OutBean().set("oknum", successnum).set("failernum", failernum).set("fileid", newFileBean.getId());
    }
}