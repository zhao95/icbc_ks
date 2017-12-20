package com.rh.ts.xmgl;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import com.icbc.ctp.utility.CollectionUtil;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.FileMgr;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.bean.PageBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.ts.util.BMUtil;
import com.rh.ts.util.POIExcelUtil;
import com.rh.ts.util.TsConstant;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.util.*;

/**
 * Created by shenh on 2017/10/25.
 */
public class XmglAdmissionServ extends CommonServ {

    /**
     * 获取准考证列表
     *
     * @param paramBean 分页信息
     * @return 分页数据
     */
    public OutBean getAdmissionList(ParamBean paramBean) {
        OutBean outBean = new OutBean();

        /*分页参数处理*/
        PageBean page = paramBean.getQueryPage();
        int rowCount = paramBean.getShowNum(); //通用分页参数优先级最高，然后是查询的分页参数
        if (rowCount > 0) { //快捷参数指定的分页信息，与finds方法兼容
            page.setShowNum(rowCount); //从参数中获取需要取多少条记录，如果没有则取所有记录
            page.setNowPage(paramBean.getNowPage());  //从参数中获取第几页，缺省为第1页
        } else {
            if (!page.contains(Constant.PAGE_SHOWNUM)) { //初始化每页记录数设定
                if (paramBean.getQueryNoPageFlag()) { //设定了不分页参数
                    page.setShowNum(0);
                } else { //没有设定不分页，取服务设定的每页记录数
                    page.setShowNum(50);
                }
            }
        }

        String userCode = Context.getUserBean().getCode();
        List<Object> values = new ArrayList<Object>();
        values.add(userCode);

        //BM_STATUS not in('1','3')：项目中考试全部请假不显示
        String sql = "SELECT a.* FROM `ts_xmgl` a " +
                " where exists (" +
                "select 'X' from ts_bmsh_pass b where a.XM_ID =b.XM_ID and b.BM_CODE=? and b.BM_STATUS not in( '1','3')" +
                ")" +
                "order by a.XM_START";

        List<Bean> dataList = Transaction.getExecutor().queryPage(
                sql, page.getNowPage(), page.getShowNum(), new ArrayList<Object>(values), null);

        for (Bean bean : dataList) {
            String state = "";
            String xmEnd = bean.getStr("XM_END");
            try {
                if (StringUtils.isNotBlank(xmEnd) && new Date().getTime() > DateUtils.parseDate(xmEnd).getTime()) {
                    state = "已结束";
                } else if (StringUtils.isBlank(bean.getStr("XM_KCAP_PUBLISH_TIME"))) {
                    state = "未发布";
                } else {
                    state = "可打印";
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            bean.set("state", state);
        }
        /*设置数据总数*/
        int count = dataList.size();
        int showCount = page.getShowNum();
        boolean bCount; //是否计算分页
        if ((showCount == 0) || paramBean.getQueryNoPageFlag()) {
            bCount = false;
        } else {
            bCount = true;
        }
        if (bCount) { //进行分页处理
            if (!page.contains(Constant.PAGE_ALLNUM)) { //如果有总记录数就不再计算
                int allNum;
                if ((page.getNowPage() == 1) && (count < showCount)) { //数据量少，无需计算分页
                    allNum = count;
                } else {
                    allNum = Transaction.getExecutor().count(sql, values);
                }
                page.setAllNum(allNum);
            }
            outBean.setCount(page.getAllNum()); //设置为总记录数
        } else {
            outBean.setCount(dataList.size());
        }
        outBean.setData(dataList);
        outBean.setPage(page);
        return outBean;
    }

    /**
     * 当前用户是否有可打印却未打印的准考证
     *
     * @return outBean {hasPrint}
     */
    public OutBean getHasCanPrintAdmission() {
        OutBean outBean = new OutBean();

        String userCode = Context.getUserBean().getCode();
        List<Object> values = new ArrayList<Object>();
        values.add(userCode);
        values.add(userCode);

        String sql = "SELECT a.XM_ID FROM `ts_xmgl` a " +
                " where exists (" +
                " select 'X' from ts_bmsh_pass b where a.XM_ID =b.XM_ID and b.BM_CODE=? and b.BM_STATUS not in( '1','3')" +
                " ) " +
                //当前时间在项目开始和结束时间之间
                " and now() BETWEEN " +
                " str_to_date(a.XM_START, '%Y-%m-%d %H:%i:%s') " +
                " and str_to_date( a.XM_END, '%Y-%m-%d %H:%i:%s') " +
                //项目场次发布时间不为空（项目场次已发布）
                " and a.XM_KCAP_PUBLISH_TIME is not null and a.XM_KCAP_PUBLISH_TIME !='' " +
                //未打印(不存在 生成准考证时间>项目场次发布时间 当前用户 的准考证)
                " and not exists(select '' from ts_xmgl_admission_file f where f.XM_ID=a.XM_ID and str_to_date(f.S_ATIME, '%Y-%m-%d %H:%i:%s') > str_to_date(a.XM_KCAP_PUBLISH_TIME, '%Y-%m-%d %H:%i:%s') and f.USER_CODE =?)";
        List<Bean> dataList = Transaction.getExecutor().query(sql, values);
        String hasPrint = "false";
        if (dataList != null && dataList.size() > 0) {
            hasPrint = "true";
        }
        outBean.set("hasPrint", hasPrint);
        return outBean;
    }

    /**
     * 接口，获取准考证信息
     *
     * @param paramBean {XM_ID:"XXX",USER_CODE:"XXX"}
     * @return 准考证信息
     */
    public OutBean getAdmissionFile(ParamBean paramBean) {
        String xmId = paramBean.getStr("XM_ID");
        String userCode = paramBean.getStr("USER_CODE");

        Bean pdfFileBean = getPdfFileBean(xmId, userCode);

        OutBean outBean = new OutBean();
        outBean.set("fileId", pdfFileBean.getId());
        outBean.set("fileName", pdfFileBean.getStr("FILE_NAME"));
        return outBean;
    }

    /**
     * 接口，获取准考证stream流
     *
     * @param paramBean {XM_ID:"XXX",USER_CODE:"XXX"}
     * @return 准考证信息
     */
    public OutBean getAdmissionFileStream(ParamBean paramBean) {
        String xmId = paramBean.getStr("XM_ID");
        String userCode = paramBean.getStr("USER_CODE");

        Bean pdfFileBean = getPdfFileBean(xmId, userCode);
        HttpServletResponse response = Context.getResponse();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/pdf;charset=UTF-8");
        try {
            InputStream input = FileMgr.download(pdfFileBean);
//            input = new BufferedInputStream(httpUrl.getInputStream());
            byte buffBytes[] = new byte[1024];
            OutputStream out = response.getOutputStream();
            int read = 0;
            while ((read = input.read(buffBytes)) != -1) {
                out.write(buffBytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new OutBean();
    }

    /**
     * 根据项目id和用户编码获取准考证文件信息
     * 已生成直接获取；没有生成，执行生成代码
     *
     * @param xmId     项目id
     * @param userCode 用户编码
     * @return 准考证信息
     */
    private Bean getPdfFileBean(String xmId, String userCode) {
        String fileId;
        Bean pdfFileBean = null;
        Bean admissionFileBean = new Bean();
        admissionFileBean.set("XM_ID", xmId);
        admissionFileBean.set("USER_CODE", userCode);
        List<Bean> existsBeanList = ServDao.finds(TsConstant.SERV_XMGL_ADMISSION_FILE, admissionFileBean);
        Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
        String xmKcapPublishTime = xmBean.getStr("XM_KCAP_PUBLISH_TIME");

        if (existsBeanList.size() > 0 && StringUtils.isNotEmpty(existsBeanList.get(0).getStr("FILE_ID"))//已生成该项目该考生的准考证
                && existsBeanList.get(0).getStr("S_ATIME").compareTo(xmKcapPublishTime) > 0//生成时间在发布之后
                ) {
            //已生成该项目该考生的准考证并且 生成时间在发布之后，直接返回
            fileId = existsBeanList.get(0).getStr("FILE_ID");
            pdfFileBean = FileMgr.getFile(fileId);
        } else {
            //还未生成，执行生成代码
            UserBean userBean = UserMgr.getUser(userCode);
            String excel_template_id = xmBean.getStr("EXCEL_TEMPLATE_ID");
            String excelTemplateId = "";
            if (StringUtils.isNotEmpty(excel_template_id)) {
                excelTemplateId = excel_template_id.split(",")[0];
            }
            InputStream in;
            if (StringUtils.isBlank(excelTemplateId)) {
                throw new TipException("准考证模板未定义");
            } else if (!(excelTemplateId.contains("xls") || excelTemplateId.contains("xlsx"))) {
                throw new TipException("准考证模板格式错误");
            }

            Bean excelTemplateFileBean = FileMgr.getFile(excelTemplateId);

            File admissionExcelFile = null;
            File admissionPdfFile = null;
            try {
                in = FileMgr.download(excelTemplateFileBean);
                //根据模板生成excel文件
                admissionExcelFile = generateAdmissionExcelFile(xmId, userCode, in);
                //转换成pdf
                admissionPdfFile = File.createTempFile("admissionPdf", ".pdf");
                int pdfFlag = office2PDF(admissionExcelFile.getAbsolutePath(), admissionPdfFile.getAbsolutePath());
                if (pdfFlag == 0) {
                    //pdf转换成功：添加/更新 TS_XMGL_ADMISSION_FILE  上传pdf文件
                    if (existsBeanList.size() > 0) {
                        //存在admissionFileBean数据
                        admissionFileBean = existsBeanList.get(0);
                        String existFileId = admissionFileBean.getStr("FILE_ID");
                        if (StringUtils.isNotBlank(existFileId)) {
                            //fileId 不为空 删除文件
                            FileMgr.deleteFile(existFileId);
                            admissionFileBean.set("FILE_ID", "");
                        }
                    } else {
                        //不存在admissionFileBean，创建
                        admissionFileBean.set("FILE_ID", "");
                        admissionFileBean.set("S_ATIME", DateUtils.getDatetime());
                        admissionFileBean = ServDao.save(TsConstant.SERV_XMGL_ADMISSION_FILE, admissionFileBean);
                    }
                    String dataId = admissionFileBean.getId();
                    FileInputStream fileInputStream = new FileInputStream(admissionPdfFile);
//                    resultInputStream = new FileInputStream(admissionPdfFile);
                    pdfFileBean = FileMgr.upload(TsConstant.SERV_XMGL_ADMISSION_FILE, dataId, "ADMISSION", fileInputStream,
                            xmBean.getStr("XM_NAME") + "-" + userBean.getStr("USER_NAME") + ".pdf");
                    fileId = pdfFileBean.getId();
                    admissionFileBean.set("FILE_ID", fileId);
                    admissionFileBean.set("S_ATIME", DateUtils.getDatetime());
                    ServDao.update(TsConstant.SERV_XMGL_ADMISSION_FILE, admissionFileBean);
                } else {
                    //excel转pdf失败
                    throw new TipException("excel转pdf失败");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (admissionExcelFile != null) {
                    admissionExcelFile.delete();
                }
                if (admissionPdfFile != null) {
                    admissionPdfFile.delete();
                }
            }
        }
        return pdfFileBean;
    }

    /**
     * 根据模板文件生成excel文件
     *
     * @param xmId        项目id
     * @param userCode    用户编码
     * @param inputStream 模板文件inputStream
     * @return admissionExcelFile
     * @throws IOException IOException
     */
    public File generateAdmissionExcelFile(String xmId, String userCode, InputStream inputStream) throws IOException {
        Bean dataBean = new Bean();

        //基本信息 {title} {userCode} {userName} {userSexName} {orgName} {kcName} {kcAdress}
        Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
        UserBean userBean = UserMgr.getUser(userCode);
        dataBean.put("title", xmBean.getStr("XM_NAME"));
        dataBean.put("userCode", userCode);
        dataBean.put("userName", userBean.getStr("USER_NAME"));
        String userSexName = "1".equals(userBean.getStr("USER_SEX")) ? "女" : "男";
        dataBean.put("userSexName", userSexName);
        dataBean.put("orgName", userBean.getDeptName());

        //picture
//        bufferImg = ImageIO.read(new File("C:\\Users\\shenh\\Pictures\\Saved Pictures\\微信截图_20170901180205.png"));
        String userImgSrc = userBean.getStr("USER_IMG_SRC");

        if (StringUtils.isNotBlank(userImgSrc)) {
            String fileId = userImgSrc.split(",")[0];
            dataBean.put("fileId", fileId);
            dataBean.put("pictureFileSuffix", fileId.substring(fileId.lastIndexOf(".") + 1, fileId.length()));
        }

        //ksList
        String sql = "select c.BM_TYPE,c.BM_MK,c.BM_XL,f.BM_TITLE,b.KC_NAME,b.KC_ADDRESS,e.ZW_ZWH_SJ,d.SJ_START,c.BM_KS_TIME  " +
                "from ts_xmgl_kcap_yapzw a " +
                "left join ts_kcgl b on b.KC_ID = a.KC_ID " +
                "left join ts_bmsh_pass c on c.SH_ID = a.SH_ID " +
                "left join ts_xmgl_kcap_dapcc_ccsj d on d.SJ_ID = a.SJ_ID " +
                "left join ts_kcgl_zwdyb e on e.ZW_ID = a.ZW_ID " +
                "left join ts_bmlb_bm f on f.BM_ID = c.BM_ID " +
                "where a.XM_ID =? and c.BM_CODE =? order by d.SJ_START";
        List<Object> values = new ArrayList<Object>();
        values.add(xmId);
        values.add(userCode);

        List<Bean> ksbakList = new ArrayList<Bean>();
        List<Bean> ksDataList = Transaction.getExecutor().query(sql, values);
//        ServDao.finds(TsConstant.SERV_KCAP_YAPZW, " and ");
//        ServDao.finds();
        //SERV_KCAP_YAPZW
        for (Bean ksDataBean : ksDataList) {
            String bmTitle = (String) ksDataBean.get("BM_TITLE");
            String bmXl = (String) ksDataBean.get("BM_XL");
            String bmMk = (String) ksDataBean.get("BM_MK");
            String bmType = (String) ksDataBean.get("BM_TYPE");
            String bm_bt = BMUtil.getExaminationName(bmType, bmXl, bmMk);
            String title;
            if (!"".equals(bmMk)) {
                title = bm_bt;
            } else {
                title = bmTitle;
            }
            Bean kcBean = new Bean();
            kcBean.set("kcName", ksDataBean.getStr("KC_NAME"));//考场名称
            kcBean.set("kcAddress", ksDataBean.getStr("KC_ADDRESS"));//考场地址
            kcBean.set("ksName", title);//考试名称
            kcBean.set("ksXTZW", ksDataBean.getStr("ZW_ZWH_SJ"));//考试座位号
            kcBean.set("ksBeginTime", ksDataBean.getStr("SJ_START"));//考试开始时间
            kcBean.set("ksDuration", ksDataBean.getStr("BM_KS_TIME"));//考试时长
            ksbakList.add(kcBean);
        }
        dataBean.set("ksList", ksbakList);

        //需要替换的考试信息 rowIndex colIndex
        Map<String, Integer> ksIndexMap = new HashMap<String, Integer>();

        List<String> ksInfoFieldNameList = new ArrayList<String>();
        ksInfoFieldNameList.add("{ksName}");
        ksInfoFieldNameList.add("{ksXTZW}");
        ksInfoFieldNameList.add("{ksBeginTime}");
        ksInfoFieldNameList.add("{ksDuration}");
        ksInfoFieldNameList.add("{kcName}");
        ksInfoFieldNameList.add("{kcAddress}");

        List<String> list = new ArrayList<String>();
        for (Object o : dataBean.keySet()) {
            list.add("{" + o + "}");
        }

        /*生成xls文件*/
        File temp = File.createTempFile("admission", ".xls");

        try {
            //poi包下的类读取excel文件
            POIFSFileSystem ts = new POIFSFileSystem(inputStream);
            // 创建一个webbook，对应一个Excel文件
            HSSFWorkbook workbook = new HSSFWorkbook(ts);
            //对应Excel文件中的sheet
            HSSFSheet sheet = workbook.getSheetAt(0);

            /*替换单元格内容*/
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                HSSFRow row = sheet.getRow((short) i);
                if (null != row) {
                    for (int j = 0; j <= row.getLastCellNum(); j++) {
                        HSSFCell cell = row.getCell(j);
                        if (null != cell) {
                            if (org.apache.poi.ss.usermodel.CellType.STRING == cell.getCellTypeEnum()) {
                                if (list.contains(cell.getStringCellValue())) {
                                    /*基本信息*/
                                    String str = dataBean.getStr(cell.getStringCellValue().substring(1, cell.getStringCellValue().length() - 1));
                                    cell.setCellValue(str);
                                } else if ("{picture}".equals(cell.getStringCellValue())) {
                                    /*图片*/
                                    String avatarFileId = dataBean.getStr("fileId");
                                    String pictureFileSuffix = dataBean.getStr("pictureFileSuffix");
                                    int pictureType = 0;
                                    int tempVal = 0;
                                    if (pictureFileSuffix.equals("png")) {
                                        tempVal = 1;
                                    } else if (pictureFileSuffix.equals("emf")) {
                                        tempVal = 2;
                                    } else if (pictureFileSuffix.equals("pict")) {
                                        tempVal = 3;
                                    } else if (pictureFileSuffix.equals("jpg")) {
                                        tempVal = 4;
                                    } else if (pictureFileSuffix.equals("jpeg")) {
                                        tempVal = 5;
                                    } else if (pictureFileSuffix.equals("die")) {
                                        tempVal = 6;
                                    }

                                    switch (tempVal) {
                                        case 1:
                                            pictureType = HSSFWorkbook.PICTURE_TYPE_PNG;
                                            break;
                                        case 2:
                                            pictureType = HSSFWorkbook.PICTURE_TYPE_EMF;
                                            break;
                                        case 3:
                                            pictureType = HSSFWorkbook.PICTURE_TYPE_PICT;
                                            break;
                                        case 4:
                                            pictureType = HSSFWorkbook.PICTURE_TYPE_JPEG;
                                            break;
                                        case 5:
                                            pictureType = HSSFWorkbook.PICTURE_TYPE_JPEG;
                                            break;
                                        case 6:
                                            pictureType = HSSFWorkbook.PICTURE_TYPE_DIB;
                                            break;
                                        default:
                                            break;
                                    }

                                    if (StringUtils.isNotBlank(avatarFileId) && pictureType != 0) {
                                        //插入图片
                                        cell.setCellValue("头像");
                                        Bean mergedRegion = POIExcelUtil.isMergedRegion(sheet, i, j);
                                        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
                                        //anchor主要用于设置图片的属性
                                        boolean merged = mergedRegion.getBoolean("merged");
                                        HSSFClientAnchor anchor;
                                        if (merged) {
                                            anchor = new HSSFClientAnchor(0, 0, 0, 0,
                                                    (short) (mergedRegion.getInt("startCol") - 1), (mergedRegion.getInt("startRow") - 1),
                                                    (short) mergedRegion.getInt("endCol"), mergedRegion.getInt("endRow"));
                                        } else {
                                            anchor = new HSSFClientAnchor(0, 0, 0, 0,
                                                    (short) i, j,
                                                    (short) (i + 1), (j + 1));
                                        }
                                        //anchor.setAnchorType(3);
                                        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                                        patriarch.createPicture(anchor, workbook.addPicture(byteArrayOut.toByteArray(), pictureType));
                                        Bean fileBean = FileMgr.getFile(avatarFileId);
                                        BufferedImage bufferedImage = ImageIO.read(FileMgr.download(fileBean));
                                        ImageIO.write(bufferedImage, pictureFileSuffix, byteArrayOut);
                                    } else {
                                        //图片类型不支持 || 用户无头像文件
                                        cell.setCellValue("请在此处上传电子照片一并打印准考证或打印准考证后粘贴本人近照！");
                                    }

                                } else if (ksInfoFieldNameList.contains(cell.getStringCellValue())) {//"{ksName}".equals(cell.getStringCellValue())) {
                                    /*考试信息index*/
                                    ksIndexMap.put(cell.getStringCellValue().substring(1, cell.getStringCellValue().length() - 1) + "RowIndex", i);
                                    ksIndexMap.put(cell.getStringCellValue().substring(1, cell.getStringCellValue().length() - 1) + "ColIndex", j);
                                }
                            }
                        }
                    }
                }
            }
            /*考试信息*/
            List<Bean> ksList = dataBean.getList("ksList");

            int ksNameRowIndex, ksNameColIndex,
                    ksXTZWRowIndex, ksXTZWColIndex,
                    ksBeginTimeRowIndex, ksBeginTimeColIndex,
                    ksDurationRowIndex, ksDurationColIndex,
                    kcNameRowIndex, kcNameColIndex,
                    kcAddressRowIndex, kcAddressColIndex;
            ksNameRowIndex = ksIndexMap.get("ksNameRowIndex");
            ksNameColIndex = ksIndexMap.get("ksNameColIndex");
            ksXTZWRowIndex = ksIndexMap.get("ksXTZWRowIndex");
            ksXTZWColIndex = ksIndexMap.get("ksXTZWColIndex");
            ksBeginTimeRowIndex = ksIndexMap.get("ksBeginTimeRowIndex");
            ksBeginTimeColIndex = ksIndexMap.get("ksBeginTimeColIndex");
            ksDurationRowIndex = ksIndexMap.get("ksDurationRowIndex");
            ksDurationColIndex = ksIndexMap.get("ksDurationColIndex");
            kcNameRowIndex = ksIndexMap.get("kcNameRowIndex");
            kcNameColIndex = ksIndexMap.get("kcNameColIndex");
            kcAddressRowIndex = ksIndexMap.get("kcAddressRowIndex");
            kcAddressColIndex = ksIndexMap.get("kcAddressColIndex");

            List<Integer> nums = new ArrayList<Integer>();
            nums.add(ksDurationRowIndex);
            nums.add(ksNameRowIndex);
            nums.add(ksXTZWRowIndex);
            nums.add(ksBeginTimeRowIndex);
            nums.add(kcNameRowIndex);
            nums.add(kcAddressRowIndex);

            Integer max = Collections.max(nums);
            Integer min = Collections.min(nums);
            int rowMaxDistance = max - min + 1;

            if (ksList != null && ksList.size() > 1) {
                for (int ksIndex = ksList.size() - 1; ksIndex >= 1; ksIndex--) {
                    Bean ksBean = ksList.get(ksIndex);
                    POIExcelUtil.insertRow(sheet, max, rowMaxDistance);
                    for (int j = 0; j < rowMaxDistance; j++) {
                        POIExcelUtil.copyRows(workbook, 0, 0, min + j, min + j, min + j + rowMaxDistance);
                        HSSFRow row = sheet.getRow(min + j + rowMaxDistance);
                        for (Cell cell : row) {
                            if (cell.getCellTypeEnum().equals(CellType.STRING)//单元格类型为String
                                    && ksInfoFieldNameList.contains(cell.getStringCellValue())//是需要替换的考试信息字段
                                    ) {
                                HSSFCell nameCell = row.getCell(cell.getColumnIndex() - 1);
                                if (nameCell != null && cell.getCellTypeEnum().equals(CellType.STRING)) {
                                    nameCell.setCellValue(nameCell.getStringCellValue().replace("1", String.valueOf(ksIndex + 1)));
                                }
                                cell.setCellValue(
                                        ksBean.getStr(cell.getStringCellValue().substring(1, cell.getStringCellValue().length() - 1))
                                );
                            }
                        }
                    }
                }
            }


            if (!CollectionUtil.isEmpty(ksList)) {
                Bean ksBean = ksList.get(0);
                String ksName = ksBean.getStr("ksName");
                String ksXTZW = ksBean.getStr("ksXTZW");
                String ksBeginTime = ksBean.getStr("ksBeginTime");
                String ksDuration = ksBean.getStr("ksDuration");
                String kcName = ksBean.getStr("kcName");
                String kcAddress = ksBean.getStr("kcAddress");
                //ksName
                HSSFRow row = sheet.getRow(ksNameRowIndex);
                if (row != null) {
                    HSSFCell cell = row.getCell(ksNameColIndex);
                    if (cell != null) {
                        cell.setCellValue(ksName);
                    }
                }
                //ksXTZW
                HSSFRow row2 = sheet.getRow(ksXTZWRowIndex);
                if (row2 != null) {
                    HSSFCell cell = row2.getCell(ksXTZWColIndex);
                    if (cell != null) {
                        cell.setCellValue(ksXTZW);
                    }
                }
                //ksBeginTime
                HSSFRow row3 = sheet.getRow(ksBeginTimeRowIndex);
                if (row3 != null) {
                    HSSFCell cell = row3.getCell(ksBeginTimeColIndex);
                    if (cell != null) {
                        cell.setCellValue(ksBeginTime);
                    }
                }
                //ksDuration
                HSSFRow row4 = sheet.getRow(ksDurationRowIndex);
                if (row4 != null) {
                    HSSFCell cell = row4.getCell(ksDurationColIndex);
                    if (cell != null) {
                        cell.setCellValue(ksDuration);
                    }
                }
                //kcName
                HSSFRow row5 = sheet.getRow(kcNameRowIndex);
                if (row4 != null) {
                    HSSFCell cell = row5.getCell(kcNameColIndex);
                    if (cell != null) {
                        cell.setCellValue(kcName);
                    }
                }
                //kcAddress
                HSSFRow row6 = sheet.getRow(kcAddressRowIndex);
                if (row6 != null) {
                    HSSFCell cell = row6.getCell(kcAddressColIndex);
                    if (cell != null) {
                        cell.setCellValue(kcAddress);
                    }
                }
            }

            FileOutputStream os = new FileOutputStream(temp);
            os.flush();
            //将Excel写出
            workbook.write(os);
            //关闭流
//            fileInput.close();
            os.close();
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
            throw new TipException("准考证生成失败");
        }
    }

    /**
     * 将Office文档转换为PDF. 运行该函数需要用到OpenOffice
     * <p>
     * <pre>
     * 方法示例:
     * String sourcePath = "F:\\office\\source.doc";
     * String destFile = "F:\\pdf\\dest.pdf";
     * Converter.office2PDF(sourcePath, destFile);
     * </pre>
     *
     * @param sourceFile 源文件, 绝对路径. 可以是Office2003-2007全部格式的文档, Office2010的没测试. 包括.doc,
     *                   .docx, .xls, .xlsx, .ppt, .pptx等. 示例: F:\\office\\source.doc
     * @param destFile   目标文件. 绝对路径. 示例: F:\\pdf\\dest.pdf
     * @return 操作成功与否的提示信息. 如果返回 -1, 表示找不到源文件, 或url.properties配置错误; 如果返回 0,
     * 则表示操作成功; 返回1, 则表示转换失败
     */
    public static int office2PDF(String sourceFile, String destFile) throws FileNotFoundException {
        try {
            File inputFile = new File(sourceFile);
            if (!inputFile.exists()) {
                return -1;// 找不到源文件, 则返回-1
            }

            // 如果目标路径不存在, 则新建该路径
            File outputFile = new File(destFile);
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }

            // connect to an OpenOffice.org instance running on port 8100
            OpenOfficeConnection connection = new SocketOpenOfficeConnection(
                    "127.0.0.1", 8100);//192.168.159.130
            connection.connect();

            // convert
            DocumentConverter converter = new StreamOpenOfficeDocumentConverter(
                    connection);
            converter.convert(inputFile, outputFile);

            // close the connection
            connection.disconnect();

            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }

}
