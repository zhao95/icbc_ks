package com.rh.ts.util;

import com.rh.core.base.Bean;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * poi操作excel方法
 */
public class POIExcelUtil {

    private static Logger logger = Logger.getLogger(POIExcelUtil.class);

    private POIExcelUtil() {
    }

    /**
     * 复制工作表
     * 此方法主要用于复制2个不同HSSFWorkbook间的工作表
     */
    public static void copySheet(HSSFWorkbook fromWorkbook, HSSFWorkbook toWorkbook, int fromSheetIndex, int toSheetIndex) {
        toWorkbook.setSheetName(toSheetIndex, fromWorkbook.getSheetName(fromSheetIndex));
        HSSFSheet fromSheet = fromWorkbook.getSheetAt(fromSheetIndex);
        for (int i = fromSheet.getFirstRowNum(); i <= fromSheet.getLastRowNum(); i++) {
            copyRows(fromWorkbook, toWorkbook, fromSheetIndex, toSheetIndex, i, i, i);
        }
    }

    /**
     * 复制行
     * 此方法主要用于复制2个不同HSSFWorkbook间的行
     */
    public static void copyRows(HSSFWorkbook fromWorkbook, HSSFWorkbook toWorkbook, int fromSheetIndex, int toSheetIndex, int startRow, int endRow, int position) {
        HSSFSheet fromSheet = fromWorkbook.getSheetAt(fromSheetIndex);
        HSSFSheet toSheet = toWorkbook.getSheetAt(toSheetIndex);
        int i;
        int j;

        if ((startRow == -1) || (endRow == -1)) {
            return;
        }

        List<CellRangeAddress> oldRanges = new ArrayList<CellRangeAddress>();
        for (i = 0; i < fromSheet.getNumMergedRegions(); i++) {
            oldRanges.add(fromSheet.getMergedRegion(i));
        }

        // 拷贝合并的单元格。原理：复制当前合并单元格后，原位置的格式会移动到新位置，需在原位置生成旧格式
        for (CellRangeAddress oldRange : oldRanges) {
            CellRangeAddress newRange = new CellRangeAddress(oldRange.getFirstRow(), oldRange.getLastRow(),
                    oldRange.getFirstColumn(), oldRange.getLastColumn());

            if (oldRange.getFirstRow() >= startRow && oldRange.getLastRow() <= endRow) {
                int targetRowFrom = oldRange.getFirstRow() - startRow + position;
                int targetRowTo = oldRange.getLastRow() - startRow + position;
                oldRange.setFirstRow(targetRowFrom);
                oldRange.setLastRow(targetRowTo);
                toSheet.addMergedRegion(oldRange);
                fromSheet.addMergedRegion(newRange);
            }
        }

        // 设置列宽
        for (i = startRow; i <= endRow; i++) {
            HSSFRow fromRow = fromSheet.getRow(i);
            if (fromRow != null) {
                for (j = fromRow.getLastCellNum(); j >= fromRow.getFirstCellNum(); j--) {
                    toSheet.setColumnWidth(j, fromSheet.getColumnWidth(j));
                    toSheet.setColumnHidden(j, false);
                }
                break;
            }
        }

        // 拷贝行并填充数据
        for (; i <= endRow; i++) {
            HSSFRow fromRow = fromSheet.getRow(i);
            if (fromRow == null) {
                continue;
            }
            HSSFRow toRow = toSheet.createRow(i - startRow + position);
            toRow.setHeight(fromRow.getHeight());
            for (j = fromRow.getFirstCellNum(); j <= fromRow.getPhysicalNumberOfCells(); j++) {
                HSSFCell fromCell = fromRow.getCell(j);
                if (fromCell == null) {
                    continue;
                }
                HSSFCell toCell = toRow.createCell(j);
                HSSFCellStyle toStyle = toWorkbook.createCellStyle();
                copyCellStyle(fromWorkbook, toWorkbook, fromCell.getCellStyle(), toStyle);
                toCell.setCellStyle(toStyle);
                CellType cellTypeEnum = fromCell.getCellTypeEnum();
                toCell.setCellType(cellTypeEnum);
                switch (cellTypeEnum) {
                    case BOOLEAN://HSSFCell.CELL_TYPE_BOOLEAN
                        toCell.setCellValue(fromCell.getBooleanCellValue());
                        // System.out.println("--------TYPE_BOOLEAN:" +
                        // targetCell.getBooleanCellValue());
                        break;
                    case ERROR://HSSFCell.CELL_TYPE_ERRORF
                        toCell.setCellErrorValue(FormulaError._NO_ERROR);//fromCell.getErrorCellValue()
                        // System.out.println("--------TYPE_ERROR:" +
                        // targetCell.getErrorCellValue());
                        break;
                    case FORMULA://HSSFCell.CELL_TYPE_FORMULA
                        toCell.setCellFormula(parseFormula(fromCell.getCellFormula()));
                        // System.out.println("--------TYPE_FORMULA:" +
                        // targetCell.getCellFormula());
                        break;
                    case NUMERIC://HSSFCell.CELL_TYPE_NUMERIC
                        toCell.setCellValue(fromCell.getNumericCellValue());
                        // System.out.println("--------TYPE_NUMERIC:" +
                        // targetCell.getNumericCellValue());
                        break;
                    case STRING://HSSFCell.CELL_TYPE_STRING
                        toCell.setCellValue(fromCell.getRichStringCellValue());
                        // System.out.println("--------TYPE_STRING:" + i +
                        // targetCell.getRichStringCellValue());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 复制行
     * 如果是同一个HSSFWorkbook中的行请用此方法
     */
    public static void copyRows(HSSFWorkbook workbook, int fromSheetIndex, int toSheetIndex, int startRow, int endRow, int position) {
        HSSFSheet fromSheet = workbook.getSheetAt(fromSheetIndex);
        HSSFSheet toSheet = workbook.getSheetAt(toSheetIndex);
        int i;
        int j;

        if ((startRow == -1) || (endRow == -1)) {
            return;
        }

        List<CellRangeAddress> oldRanges = new ArrayList<CellRangeAddress>();
        for (i = 0; i < fromSheet.getNumMergedRegions(); i++) {
            oldRanges.add(fromSheet.getMergedRegion(i));
        }

        // 拷贝合并的单元格。原理：复制当前合并单元格后，原位置的格式会移动到新位置，需在原位置生成旧格式
        for (CellRangeAddress oldRange : oldRanges) {
            CellRangeAddress newRange = new CellRangeAddress(oldRange.getFirstRow(), oldRange.getLastRow(),
                    oldRange.getFirstColumn(), oldRange.getLastColumn());

            if (oldRange.getFirstRow() >= startRow && oldRange.getLastRow() <= endRow) {
                int targetRowFrom = oldRange.getFirstRow() - startRow + position;
                int targetRowTo = oldRange.getLastRow() - startRow + position;
                oldRange.setFirstRow(targetRowFrom);
                oldRange.setLastRow(targetRowTo);
                toSheet.addMergedRegion(oldRange);
                fromSheet.addMergedRegion(newRange);
            }
        }

        // 设置列宽
        for (i = startRow; i <= endRow; i++) {
            HSSFRow fromRow = fromSheet.getRow(i);
            if (fromRow != null) {
                for (j = fromRow.getLastCellNum(); j >= fromRow.getFirstCellNum(); j--) {
                    toSheet.setColumnWidth(j, fromSheet.getColumnWidth(j));
                    toSheet.setColumnHidden(j, false);
                }
                break;
            }
        }

        // 拷贝行并填充数据
        for (; i <= endRow; i++) {
            HSSFRow fromRow = fromSheet.getRow(i);
            if (fromRow == null) {
                continue;
            }
            HSSFRow toRow = toSheet.createRow(i - startRow + position);
            toRow.setHeight(fromRow.getHeight());
            for (j = fromRow.getFirstCellNum(); j <= fromRow.getPhysicalNumberOfCells(); j++) {
                HSSFCell fromCell = fromRow.getCell(j);
                if (fromCell == null) {
                    continue;
                }
                HSSFCell toCell = toRow.createCell(j);
                toCell.setCellStyle(fromCell.getCellStyle());
                CellType cellTypeEnum = fromCell.getCellTypeEnum();
                toCell.setCellType(cellTypeEnum);
                switch (cellTypeEnum) {
                    case BOOLEAN://HSSFCell.CELL_TYPE_BOOLEAN
                        toCell.setCellValue(fromCell.getBooleanCellValue());
                        // System.out.println("--------TYPE_BOOLEAN:" +
                        // targetCell.getBooleanCellValue());
                        break;
                    case ERROR://HSSFCell.CELL_TYPE_ERRORF
                        toCell.setCellErrorValue(FormulaError._NO_ERROR);//fromCell.getErrorCellValue()
                        // System.out.println("--------TYPE_ERROR:" +
                        // targetCell.getErrorCellValue());
                        break;
                    case FORMULA://HSSFCell.CELL_TYPE_FORMULA
                        toCell.setCellFormula(parseFormula(fromCell.getCellFormula()));
                        // System.out.println("--------TYPE_FORMULA:" +
                        // targetCell.getCellFormula());
                        break;
                    case NUMERIC://HSSFCell.CELL_TYPE_NUMERIC
                        toCell.setCellValue(fromCell.getNumericCellValue());
                        // System.out.println("--------TYPE_NUMERIC:" +
                        // targetCell.getNumericCellValue());
                        break;
                    case STRING://HSSFCell.CELL_TYPE_STRING
                        toCell.setCellValue(fromCell.getRichStringCellValue());
                        // System.out.println("--------TYPE_STRING:" + i +
                        // targetCell.getRichStringCellValue());
                        break;
                    default:
                        break;
                }
            }
        }
    }


    /**
     * 复制行
     * 如果是同一个HSSFWorkbook中的行请用此方法
     */
    public static void copyRows(HSSFWorkbook workbook, int sheetIndex, int startRow, int endRow, int position) {
        HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
//        HSSFSheet toSheet = workbook.getSheetAt(toSheetIndex);
        int i;
        int j;

        if ((startRow == -1) || (endRow == -1)) {
            return;
        }

        List<CellRangeAddress> oldRanges = new ArrayList<>();
        for (i = 0; i < sheet.getNumMergedRegions(); i++) {
            oldRanges.add(sheet.getMergedRegion(i));
        }

        // 拷贝合并的单元格。原理：复制当前合并单元格后，原位置的格式会移动到新位置，需在原位置生成旧格式
        for (CellRangeAddress oldRange : oldRanges) {
            CellRangeAddress newRange = new CellRangeAddress(oldRange.getFirstRow(), oldRange.getLastRow(),
                    oldRange.getFirstColumn(), oldRange.getLastColumn());

            if (oldRange.getFirstRow() >= startRow && oldRange.getLastRow() <= endRow) {
                int targetRowFrom = oldRange.getFirstRow() - startRow + position;
                int targetRowTo = oldRange.getLastRow() - startRow + position;
                oldRange.setFirstRow(targetRowFrom);
                oldRange.setLastRow(targetRowTo);
                sheet.addMergedRegion(oldRange);
                sheet.addMergedRegion(newRange);
            }
        }

        // 设置列宽
        for (i = startRow; i <= endRow; i++) {
            HSSFRow fromRow = sheet.getRow(i);
            if (fromRow != null) {
                for (j = fromRow.getLastCellNum(); j >= fromRow.getFirstCellNum(); j--) {
                    sheet.setColumnWidth(j, sheet.getColumnWidth(j));
                    sheet.setColumnHidden(j, false);
                }
                break;
            }
        }

        // 拷贝行并填充数据
        for (i = startRow; i <= endRow; i++) {
            HSSFRow fromRow = sheet.getRow(i);
            if (fromRow == null) {
                continue;
            }
            HSSFRow toRow = sheet.createRow(i - startRow + position);
            toRow.setHeight(fromRow.getHeight());
            for (j = fromRow.getFirstCellNum(); j <= fromRow.getPhysicalNumberOfCells(); j++) {
                HSSFCell fromCell = fromRow.getCell(j);
                if (fromCell == null) {
                    continue;
                }
                HSSFCell toCell = toRow.createCell(j);
                toCell.setCellStyle(fromCell.getCellStyle());
                CellType cellTypeEnum = fromCell.getCellTypeEnum();
                toCell.setCellType(cellTypeEnum);
                switch (cellTypeEnum) {
                    case BOOLEAN://HSSFCell.CELL_TYPE_BOOLEAN
                        toCell.setCellValue(fromCell.getBooleanCellValue());
                        // System.out.println("--------TYPE_BOOLEAN:" +
                        // targetCell.getBooleanCellValue());
                        break;
                    case ERROR://HSSFCell.CELL_TYPE_ERRORF
                        toCell.setCellErrorValue(FormulaError._NO_ERROR);//fromCell.getErrorCellValue()
                        // System.out.println("--------TYPE_ERROR:" +
                        // targetCell.getErrorCellValue());
                        break;
                    case FORMULA://HSSFCell.CELL_TYPE_FORMULA
                        toCell.setCellFormula(parseFormula(fromCell.getCellFormula()));
                        // System.out.println("--------TYPE_FORMULA:" +
                        // targetCell.getCellFormula());
                        break;
                    case NUMERIC://HSSFCell.CELL_TYPE_NUMERIC
                        toCell.setCellValue(fromCell.getNumericCellValue());
                        // System.out.println("--------TYPE_NUMERIC:" +
                        // targetCell.getNumericCellValue());
                        break;
                    case STRING://HSSFCell.CELL_TYPE_STRING
                        toCell.setCellValue(fromCell.getRichStringCellValue());
                        // System.out.println("--------TYPE_STRING:" + i +
                        // targetCell.getRichStringCellValue());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 复制单元格样式
     * 此方法主要用于复制2个不同HSSFWorkbook间的单元格样式
     */
    public static void copyCellStyle(HSSFWorkbook fromWorkbook, HSSFWorkbook toWorkbook, HSSFCellStyle fromStyle, HSSFCellStyle toStyle) {
        toStyle.setAlignment(fromStyle.getAlignmentEnum());

        // 边框和边框颜色
        toStyle.setBorderBottom(fromStyle.getBorderBottomEnum());
        toStyle.setBorderLeft(fromStyle.getBorderLeftEnum());
        toStyle.setBorderRight(fromStyle.getBorderRightEnum());
        toStyle.setBorderTop(fromStyle.getBorderTopEnum());
        toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
        toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
        toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
        toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());

        // 字体
        HSSFFont tofont = toWorkbook.createFont();
        copyFont(fromStyle.getFont(fromWorkbook), tofont);
        toStyle.setFont(tofont);

        // 背景和前景
        toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
        toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());

        toStyle.setDataFormat(fromStyle.getDataFormat());
        toStyle.setFillPattern(fromStyle.getFillPatternEnum());
        toStyle.setHidden(fromStyle.getHidden());
        toStyle.setIndention(fromStyle.getIndention());
        toStyle.setLocked(fromStyle.getLocked());
        toStyle.setRotation(fromStyle.getRotation());
        toStyle.setVerticalAlignment(fromStyle.getVerticalAlignmentEnum());
        toStyle.setWrapText(fromStyle.getWrapText());
    }

    /**
     * 复制字体
     * 此方法主要用于复制2个不同HSSFWorkbook间的字体
     */
    public static void copyFont(HSSFFont fromFont, HSSFFont toFont) {
        toFont.setBold(fromFont.getBold());
        toFont.setCharSet(fromFont.getCharSet());
        toFont.setColor(fromFont.getColor());
        toFont.setFontHeight(fromFont.getFontHeight());
        toFont.setFontHeightInPoints(fromFont.getFontHeightInPoints());
        toFont.setFontName(fromFont.getFontName());
        toFont.setItalic(fromFont.getItalic());
        toFont.setStrikeout(fromFont.getStrikeout());
        toFont.setTypeOffset(fromFont.getTypeOffset());
        toFont.setUnderline(fromFont.getUnderline());
    }

    private static String parseFormula(String pPOIFormula) {
        final String cstReplaceString = "ATTR(semiVolatile)"; //$NON-NLS-1$
        StringBuffer result;
        int index;

        result = new StringBuffer();
        index = pPOIFormula.indexOf(cstReplaceString);
        if (index >= 0) {
            result.append(pPOIFormula.substring(0, index));
            result.append(pPOIFormula.substring(index + cstReplaceString.length()));
        } else {
            result.append(pPOIFormula);
        }

        return result.toString();
    }

    /**
     * 根据单元格信息动态插入图片，如果单元格有文字，图片的位置会在文字之后，如果同样的位置已有图片则会往下插入
     *
     * @param workbook    Excel
     * @param cell        单元格信息
     * @param inputStream 图片输入流
     * @param scale       图片缩放，传入null表示原始尺寸，其余表示图片高于行高的比（例如传入1.5，表示该图片占1.5个行高）
     */
    public static void createPicture(HSSFWorkbook workbook, HSSFCell cell, InputStream inputStream, Double scale) {
        ByteArrayOutputStream byteArrayOut = null;
        try {
            byteArrayOut = new ByteArrayOutputStream();
            BufferedImage bufferImg = ImageIO.read(inputStream);
            ImageIO.write(bufferImg, "png", byteArrayOut);

            if (cell != null && (cell.getCellTypeEnum() == CellType.STRING || cell.getCellTypeEnum() == CellType.BLANK)) {
                HSSFSheet sheet = cell.getSheet();
                HSSFRow row = cell.getRow();
                HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
                String cellValue = cell.getStringCellValue().contains("#{") ? cell.getStringCellValue().split("#\\{")[0] : cell.getStringCellValue();

                int i = row.getRowNum();
                short j = (short) cell.getColumnIndex();

                int colWidth = sheet.getColumnWidth(cell.getColumnIndex()) / 32; // 单元格像素宽度
                int wordWidth = cellValue.getBytes("GBK").length == 0 ? 0 : ((cellValue.getBytes("GBK").length + 2) * 8); // 单元格文本大致像素宽度
                double pert = new BigDecimal(wordWidth).divide(new BigDecimal(colWidth), 10, BigDecimal.ROUND_HALF_UP).doubleValue();

                int dx1 = new BigDecimal(pert * 1023).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                int dy1 = 0;

                List<HSSFShape> shapes = sheet.getDrawingPatriarch().getChildren();
                for (HSSFShape shape : shapes) {
                    HSSFClientAnchor anchor = (HSSFClientAnchor) shape.getAnchor();
                    if (anchor.getRow1() == i && anchor.getCol1() == j && anchor.getDx1() == dx1 && anchor.getDy1() == dy1) {
                        if (anchor.getDy2() >= 255) {
                            i = anchor.getRow2() + 1;
                            dy1 = 0;
                        } else {
                            i = anchor.getRow2();
                            dy1 = anchor.getDy2() + 1;
                        }
                    }
                }

                HSSFClientAnchor anchor = new HSSFClientAnchor(dx1, dy1, 0, 0, j, i, j, i + 1); // 由于用了getPreferredSize所以dx2,dy2无效
                anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
                if (scale == null) {
                    patriarch.createPicture(anchor, workbook.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).getPreferredSize(1.0);
                } else {
                    double zoom = new BigDecimal(row.getHeight() / 15).divide(new BigDecimal(bufferImg.getHeight()), 10, BigDecimal.ROUND_HALF_UP).doubleValue(); // 行高像素与图片高度像素比例
                    patriarch.createPicture(anchor, workbook.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG)).getPreferredSize(zoom * scale);
                }
            }
        } catch (IOException ioe) {
            logger.error("插入图片失败", ioe);
        } finally {
            if (byteArrayOut != null) {
                try {
                    byteArrayOut.close();
                } catch (IOException e) {
                    logger.error("关闭ByteArrayOutputStream失败", e);
                }
            }
        }
    }


    /**
     * 验证某个单元格是否是合并单元格，以及该单元格所属的合并单元格的开始行、结束行、起始列以及结束列
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public static Bean isMergedRegion(Sheet sheet, int row, int column) {
        Bean bean = new Bean();
        boolean merged = false;
        int startRow = row;
        int endRow = row;
        int startCol = column;
        int endCol = column;

        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    merged = true;
                    startRow = firstRow + 1;
                    endRow = lastRow + 1;
                    startCol = firstColumn + 1;
                    endCol = lastColumn + 1;
                }
            }
        }
        bean.put("merged", merged);
        bean.put("startRow", startRow);
        bean.put("endRow", endRow);
        bean.put("startCol", startCol);
        bean.put("endCol", endCol);
        return bean;
    }

    /**
     * insert row into the target sheet, the style of cell is the same as startRow
     */
    public static void insertRow(HSSFSheet sheet, int startRow, int rows) {
        sheet.shiftRows(startRow + 1, sheet.getLastRowNum(), rows, true, false);
    }
}