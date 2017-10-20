package com.rh.core.serv.util;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDefBean;
import com.rh.core.util.Constant;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.RequestUtils;

/**
 * 将列表页面的数据导出到Excel文件中，并将文件流返回客户端
 * @author yangjy
 * 
 */
public class ExportExcel {
    /** 已办结状态 **/
    private static final String S_WF_STATE_YB = "2";

    /**
     * 记录日志信息的函数
     */
    protected Log log = LogFactory.getLog(this.getClass());
    private HttpServletResponse response = null;
    private WritableSheet sheet = null;
    private WritableWorkbook wwInst = null;
    private WritableCellFormat strFormat = null;
    private HashMap<Integer, Integer> columnWidthMap = new HashMap<Integer, Integer>();
    private LinkedHashMap<String, Bean> colList = null;
    // private String servId = null;
    private ServDefBean servDefBean = null;

    /** 是佛已经创建Excel表头 **/
    private boolean headerCreated = false;

    private int rowNum = 0;

    /**
     * 
     * @param serv 服务定义
     */
    public ExportExcel(ServDefBean serv) {
        this.servDefBean = serv;
        HttpServletResponse res = Context.getResponse();
        HttpServletRequest req = Context.getRequest();

        if (res != null) {
            res.resetBuffer();
            res.setContentType("application/x-msdownload");
            RequestUtils.setDownFileName(req, res, serv.getName() + ".xls");
            try {
                OutputStream out = res.getOutputStream();
                this.wwInst = Workbook.createWorkbook(out);
                this.sheet = wwInst.createSheet(serv.getName(), 0);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    /**
     * 关闭Excel文件流，以及HttpResponse数据流。
     */
    public void close() {
        writeExcel();
        if (this.wwInst != null) {
            try {
                this.wwInst.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        if (response != null && !response.isCommitted()) {
            try {
                this.response.flushBuffer();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 写Excel文件
     */
    private void writeExcel() {
        if (this.wwInst != null) {
            try {
                this.wwInst.write();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 创建Excel表头
     * @param colList 列定义
     */
    public void createHeader(LinkedHashMap<String, Bean> colList) {
        if (headerCreated) {
            return;
        }

        this.colList = colList;

        try {
            WritableFont titleWf = new WritableFont(WritableFont.createFont("微软简仿宋"), 12,
                    WritableFont.BOLD);
            WritableCellFormat formatTitle = new WritableCellFormat(titleWf);
            formatTitle.setWrap(true);
            formatTitle.setAlignment(jxl.format.Alignment.CENTRE);
            formatTitle.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            formatTitle.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            formatTitle.setBackground(Colour.GRAY_25);
            int x = 0;
            int y = 0;
            for (String key : colList.keySet()) {
                Bean col = colList.get(key);
                if (col.getInt("ITEM_LIST_FLAG") == Constant.YES_INT) { // 进行展示
                    Label tmpLabel = new Label(y++, x, col.getStr("ITEM_NAME"), formatTitle);
                    sheet.addCell(tmpLabel);
                }
            }
            sheet.setRowView(0, 500);
        } catch (WriteException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        headerCreated = true;
    }

    /**
     * 
     * @param dataList 数据
     * @param colList 列表显示定义
     */
    private void initSpecItem(List<Bean> dataList, LinkedHashMap<String, Bean> colList) {
        boolean hasWfState = false;
        boolean hasEmergency = false;
        if (colList.containsKey("S_WF_USER_STATE")) {
            Bean col = colList.get("S_WF_USER_STATE");
            if (col.getInt("ITEM_LIST_FLAG") == Constant.YES_INT) {
                hasWfState = true;
            }
        }

        if (colList.containsKey("S_EMERGENCY__NAME")) {
            Bean col = colList.get("S_EMERGENCY__NAME");
            if (col.getInt("ITEM_LIST_FLAG") == Constant.YES_INT) {
                hasEmergency = true;
            }
        }

        // 没有办理环节和缓急字段则不处理特殊字段数据
        if (!hasWfState && !hasEmergency) {
            return;
        }

        for (Bean bean : dataList) {
            if (hasWfState) {
                if (bean.isNotEmpty("S_WF_STATE") && bean.getStr("S_WF_STATE").equals(S_WF_STATE_YB)) {
                    bean.set("S_WF_USER_STATE", "已办结");
                } else {
                    bean.set("S_WF_USER_STATE", modifyWfState(bean.getStr("S_WF_USER_STATE")));
                }
            }

            if (hasEmergency) {
                String val = bean.getStr("S_EMERGENCY__NAME");
                bean.set("S_EMERGENCY__NAME", getEmergencyName(val));
            }
        }
    }

    /**
     * 
     * @param val 紧急程度的字典值
     * @return 去掉不在字典中的数据
     */
    private String getEmergencyName(String val) {
        if (NumberUtils.isNumber(val)) {
            return "";
        }

        return val;
    }

    /**
     * 修改[办理环节]excel导出数据
     * @param oldStr 原数据
     * @return newStr 修改后的数据
     */
    private String modifyWfState(String oldStr) {
        StringBuffer newStr = new StringBuffer();
        List<Bean> wfStateBeanList = JsonUtils.toBeanList(oldStr);
        // 单个用户
        if (wfStateBeanList.size() == 1) {
            Bean wfStrtrBean = wfStateBeanList.get(0);
            newStr.append(wfStrtrBean.getStr("D")).append("(").append(wfStrtrBean.getStr("N")).append(")");
        } else {
            // 多个并发用户
            newStr.append("并发：");
            for (int i = 0; i < wfStateBeanList.size(); i++) {
                StringBuffer thisNodeStr = new StringBuffer();
                // 如果不为最后一个，则追加逗号
                thisNodeStr.append(wfStateBeanList.get(i).getStr("D")).append("(");
                thisNodeStr.append(wfStateBeanList.get(i).getStr("N")).append(")");
                if ((i + 1) < wfStateBeanList.size()) {
                    newStr.append(thisNodeStr.toString()).append(",");
                } else {
                    // 如果是最后一个，则不追加逗号
                    newStr.append(thisNodeStr);
                }
            }
        }
        return newStr.toString();
    }

    /**
     * 填充列表数据
     * @param paramBean 参数Bean
     * @param dataList 数据列表
     */
    public void appendData(List<Bean> dataList, ParamBean paramBean) {
        initSpecItem(dataList, colList);

        try {
            strFormat = new WritableCellFormat(
                    new WritableFont(WritableFont.createFont("微软简仿宋"), 12)); // 设置字体
            strFormat.setWrap(true); // 自动换行
            strFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

            // 添加数据体
            for (Bean data : dataList) {
                rowNum++;
                addRow(rowNum, data);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }
    /**
     * 填充列表数据   排序专用  。。。。。。
     * @param paramBean 参数Bean
     * @param dataList 数据列表
     */
    public void appendData1(List<Bean> dataList, ParamBean paramBean) {
        initSpecItem(dataList, colList);

        try {
            strFormat = new WritableCellFormat(
                    new WritableFont(WritableFont.createFont("微软简仿宋"), 12)); // 设置字体
            strFormat.setWrap(true); // 自动换行
            strFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

            // 添加数据体
            for (Bean data : dataList) {
                rowNum++;
                addRow1(rowNum, data);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }
    /**
     * 
     * @param rowNum 行号
     * @param rowData 行数据
     * @throws WriteException Excel操作错误
     */
    private void addRow1(int rowNum,
            Bean rowData) throws WriteException {
        int y = 0;
        for (String key : colList.keySet()) {
        	
            Bean col = colList.get(key);
            if (col.getInt("ITEM_LIST_FLAG") != Constant.YES_INT) {
                continue;
            }

            if (key.endsWith("__NAME")) { // 默认为字符串
                String cellData = rowData.getStr(key);
                addCell(rowNum, y, cellData);
                y++;
                continue;
            }

           /* Bean itemBean = this.servDefBean.getItem(key);
            if (itemBean.getStr("ITEM_FIELD_TYPE").equals(Constant.ITEM_FIELD_TYPE_NUM)) {

                Double cellData = rowData.getDouble(col.getStr("ITEM_CODE"));
                addCell(rowNum, y, cellData, itemBean.getStr("ITEM_FIELD_LENGTH"));
            } else {
            }*/
            String cellData = rowData.getStr(col.getStr("ITEM_CODE"));
            addCell(rowNum, y, cellData);
            y++;
        }
    }
    

    /**
     * 
     * @param rowNum 行号
     * @param rowData 行数据
     * @throws WriteException Excel操作错误
     */
    private void addRow(int rowNum,
            Bean rowData) throws WriteException {
        int y = 0;
        for (String key : colList.keySet()) {
        	
            Bean col = colList.get(key);
            if (col.getInt("ITEM_LIST_FLAG") != Constant.YES_INT) {
                continue;
            }

            if (key.endsWith("__NAME")) { // 默认为字符串
                String cellData = rowData.getStr(key);
                addCell(rowNum, y, cellData);
                y++;
                continue;
            }

            Bean itemBean = this.servDefBean.getItem(key);
            if (itemBean.getStr("ITEM_FIELD_TYPE").equals(Constant.ITEM_FIELD_TYPE_NUM)) {

                Double cellData = rowData.getDouble(col.getStr("ITEM_CODE"));
                addCell(rowNum, y, cellData, itemBean.getStr("ITEM_FIELD_LENGTH"));
            } else {
            	String cellData = rowData.getStr(col.getStr("ITEM_CODE"));
            	addCell(rowNum, y, cellData);
            }
            y++;
        }
    }
    
    /**
     * 
     * @param rowNum 行号
     * @param colNum 列号
     * @param cellData 单元格数据
     * @param fieldLength 数据格式
     * @throws WriteException excel操作异常
     */
    private void addCell(int rowNum,
            int colNum, double cellData, String fieldLength) throws WriteException {
        WritableCellFormat format = formatNumber(fieldLength);
        jxl.write.Number number = new jxl.write.Number(colNum, rowNum, cellData, format);
        sheet.setColumnView(colNum, 25);
        sheet.addCell(number);
    }

    /**
     * 增加单元格数据
     * @param rowNum 行号
     * @param colNum 列好
     * @param cellData 单元格数据
     * @throws WriteException Excel 操作 Exception
     */
    private void addCell(int rowNum,
            int colNum, String cellData) throws WriteException {

        Label tmpLabel = new Label(colNum, rowNum, cellData, strFormat);
        sheet.addCell(tmpLabel);
        if (columnWidthMap.containsKey(colNum)) {
            if (columnWidthMap.get(colNum) < cellData.length()) {
                columnWidthMap.put(colNum, cellData.length());
            }
        } else {
            columnWidthMap.put(colNum, cellData.length());
        }

        int colWidth = columnWidthMap.get(colNum);

        if (colWidth >= 40) {
            sheet.setColumnView(colNum, 80);
        } else if (colWidth >= 30) {
            sheet.setColumnView(colNum, 60);
        } else if (colWidth >= 20) {
            sheet.setColumnView(colNum, 45);
        } else {
            sheet.setColumnView(colNum, 25);
        }
    }

    /**
     * 根据服务定义为字段启用[合计]追加合计行
     * @throws WriteException excel操作异常
     */
    public void addSumRow() throws WriteException {
        int y = -1;
        char asciiOfA = 'A';
        int firstCell = asciiOfA;
        boolean hasSumItem = false;
        List<WritableCell> sumRow = new ArrayList<WritableCell>();
        rowNum += 1;
        for (String key : colList.keySet()) {
            Bean col = colList.get(key);
            if (col.getInt("ITEM_LIST_FLAG") == Constant.YES_INT) {
                if (key.endsWith("__NAME")) {
                    key = key.replace("__NAME", "");
                }
                Bean item = servDefBean.getItem(key);
                WritableCell tmpLabel = null;
                y += 1;
                WritableFont titleWf = new WritableFont(WritableFont.createFont("微软简仿宋"), 12,
                        WritableFont.BOLD);
                WritableCellFormat formatTitle = new WritableCellFormat(titleWf);
                formatTitle.setWrap(true);
                formatTitle.setAlignment(jxl.format.Alignment.CENTRE);
                formatTitle.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
                formatTitle.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
                formatTitle.setBackground(Colour.GREY_25_PERCENT);
                if(item==null){
                	continue;
                }
                WritableCellFormat sumNumFormat = formatNumber(item.getStr("ITEM_FIELD_LENGTH"));
                sumNumFormat.setBackground(Colour.GREY_25_PERCENT);
                // 如果启用了合计标志
                if (item.getInt("ITEM_SUM_FLAG") == Constant.YES_INT) {
                    hasSumItem = true;
                    String sumStart = (char) (firstCell + y) + "1";
                    String sumEnd = ((char) (firstCell + y)) + "" + rowNum;
                    tmpLabel = new Formula(y, rowNum, "SUM(" + sumStart + ":" + sumEnd + ")", sumNumFormat);
                } else {
                    if (y == 0) {
                        tmpLabel = new Label(y, rowNum, "合计", formatTitle);
                    } else {
                        tmpLabel = new Label(y, rowNum, "--", formatTitle);
                    }
                }
                sumRow.add(tmpLabel);
            }
        }
        if (hasSumItem) {
            for (WritableCell cell : sumRow) {
                sheet.addCell(cell);
            }
        } else {
            sumRow = null;
        }
    }

    /**
     * 设置数字格式
     * @param formatStr 定义字段格式
     * @return 包含数据，和数据格式化的cell
     * @throws WriteException excel操作异常
     */
    private WritableCellFormat formatNumber(String formatStr) throws WriteException {
        String[] formatDef = formatStr.split(",");
        int decimal = 0;
        if (formatDef.length > 1) {
            decimal = NumberUtils.createInteger(formatDef[1]);
        }
        String decimalFormat = "";
        if (decimal > 0) {
            decimalFormat = ".";
            for (int i = 0; i < decimal; i++) {
                decimalFormat += "0";
            }
        }
        decimalFormat = "#,##0" + decimalFormat;
        WritableCellFormat format = new WritableCellFormat(new NumberFormat(decimalFormat));
        format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        format.setAlignment(jxl.format.Alignment.CENTRE);
        format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
        return format;
    }
}
