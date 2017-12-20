package com.rh.core.util;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.comm.FileMgr;
import com.rh.core.comm.file.TempFile;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;
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

    /**
     * 1、fileId
     * 2、读取信息  只读取第一个sheet
     * 3、beanList 错误
     * 4、save
     * 5、下载
     */

    /**
     * 在excel中设置失败信息，返回fileId
     *
     * @param fileId      fileId
     * @param rowBeanList 通过getDataFromXls返回的rowBeanList
     * @return errorFileId
     * @throws WriteException 写入失败
     */
    public static String saveErrorAndReturnErrorFile(String fileId, List<Bean> rowBeanList) throws WriteException {
        OutBean outBean = new OutBean();
        Bean fileBean = FileMgr.getFile(fileId);
        if (fileBean != null/* && fileBean.getStr("FILE_MTYPE").equals("application/vnd.ms-excel")*/) { //只支持excel类型
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

                tempFile = new TempFile(TempFile.Storage.SMART);
                os = tempFile.getOutputStream();

                log.debug("imp---->open file 1");
                WritableWorkbook wbook = Workbook.createWorkbook(os, book);
                WritableSheet wSheet = wbook.getSheet(0);

                //取得第一个sheet
                Sheet sheet = book.getSheet(0);
//                final int titleRowNum = getImpTitleRowNum();
                //取得第一行数据，这里非常重要，只要遇到空的cell，则忽略且不再往后处理
//                int columns = sheet.getColumns();
//                Cell[] titleCell = sheet.getRow(titleRowNum);
                int cols = sheet.getColumns();
                for (int i = 0; i < rowBeanList.size(); i++) {
                    Bean bean = rowBeanList.get(i);
                    WritableFont font = new WritableFont(WritableFont.COURIER);
                    font.setColour(Colour.GREEN);
                    WritableCellFormat format = new WritableCellFormat(font);
                    format.setAlignment(Alignment.CENTRE);
                    if (StringUtils.isEmpty(bean.getStr(ERROR_NAME))) {//没有出现错误信息
//                        Label label = new Label(cols, i, "成功", format);
//                        wSheet.addCell(label);
                    } else {
                        font.setColour(Colour.RED);
                        Label label = new Label(cols, i, "失败", format);
                        wSheet.addCell(label);
                        isOk = false;
                        Label label2 = new Label(cols + 1, i, bean.getStr(ERROR_NAME), format);
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
                FileMgr.deleteFile(fileId);
            }

            if (!isOk) {
//            	outBean.setError("导入失败，请查看文件。");
            } else {
                tempFile.destroy();
            }
        }
        return outBean.getStr("FILE_ID");
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
     * 通过fileId获取第一个sheet的内容
     *
     * @param fileId      文件id
     * @param colCodeList 列对应的属性名
     *                    例：["USER_CODE","USER_NAME"] 第一列的值key为USER_CODE，第二列的值key为USER_NAME
     * @return outBean  rowBeans(List<Bean>)
     * @throws TipException 文件不存在，请重试 / Excel文件解析错误，请校验！
     */
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
    }

    /**
     * 通过fileId获取第一个sheet的内容
     *
     * @param fileId 文件id
     * @return outBean  rowBeans(List<Bean>)
     * @throws TipException 文件不存在，请重试 / Excel文件解析错误，请校验！
     */
    public static List<Bean> getDataFromXls(String fileId) {
        List<Bean> result = new ArrayList<Bean>();
        InputStream in = null;
        try {
            Bean fileBean = FileMgr.getFile(fileId);
            in = FileMgr.download(fileBean);
        } catch (Exception e) {
            throw new TipException("文件不存在，请重试");
        }
        Workbook workbook = null;
        try {
            workbook = Workbook.getWorkbook(in);
            Sheet sheet1 = workbook.getSheet(0);
            int rows = sheet1.getRows();
            for (int i = 0; i < rows; i++) {
                Cell[] cells = sheet1.getRow(i);
                Bean rowBean = new Bean();
                for (int j = 0; j < cells.length; j++) {
                    Cell cell = cells[j];
                    String content = cell.getContents();
                    rowBean.set(COL_NAME + "" + (j + 1), content);
                }
                result.add(rowBean);
            }
        } catch (Exception e) {
            throw new TipException("Excel文件解析错误，请校验！");
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        return result;
    }
}
