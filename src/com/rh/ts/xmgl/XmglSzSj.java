package com.rh.ts.xmgl;

import com.rh.core.base.Bean;
import com.rh.core.base.TipException;
import com.rh.core.comm.FileMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.dict.DictMgr;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenh on 2017/8/8.
 */
public class XmglSzSj extends CommonServ {
    private final static String SERV_ID = "TS_XMGL_SJ";//自身服务id

    /**
     * 从excel文件中读取试卷信息，并保存
     *
     * @param paramBean paramBean
     * @return outBean
     */
    public OutBean saveFromExcel(ParamBean paramBean) throws IOException, BiffException {
        OutBean outBean = new OutBean();

        String xmSzId = (String) paramBean.get("XM_SZ_ID"),//项目id
                fileId = (String) paramBean.get("FILE_ID");//文件id

        List<Bean> beanList = this.getDataFromXls(fileId);
        int count = 0;
        Bean queryBean = new Bean();
        for (Bean bean : beanList) {
            String code = (String) bean.get("code");
            String name = (String) bean.get("name");
            queryBean.set("SJ_CODE", code);
            queryBean.set("XM_SZ_ID", xmSzId);
            if (ServDao.count(SERV_ID, queryBean) <= 0) {
                //如果试卷未添加到试卷设置中 就添加；否则跳过
                Bean dataBean = new Bean();
                dataBean.set("XM_SZ_ID", xmSzId);
                dataBean.set("SJ_CODE", code);
                dataBean.set("SJ_NAME", name);
                ServDao.create(SERV_ID, dataBean);
                count++;
            }
        }
        return outBean.setCount(count).setMsg("添加试卷成功").setOk();
    }

    /**
     * 通过excl文件获取试卷相关信息
     *
     * @param fileId 文件id
     */
    private List<Bean> getDataFromXls(String fileId) throws IOException, BiffException {
        List<Bean> result = new ArrayList<>();
        List<String> codeList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        Bean fileBean = FileMgr.getFile(fileId);
        InputStream in = FileMgr.download(fileBean);
        Workbook workbook = Workbook.getWorkbook(in);
        try {
            Sheet sheet1 = workbook.getSheet("试卷信息");
            int rows = sheet1.getRows();
            for (int i = 0; i < rows; i++) {
                if (i != 0) {
                    Cell[] cells = sheet1.getRow(i);
                    String contents0 = cells[0].getContents();
                    String contents1 = cells[1].getContents();
                    if (!StringUtils.isEmpty(contents0) && !StringUtils.isEmpty(contents1)) {
                        Bean bean = new Bean();
                        bean.set("code", contents0);
                        bean.set("name", contents1);
                        result.add(bean);
                    }
                }
            }
        } catch (Exception e) {
            throw new TipException("Excel文件解析错误，请校验！");
        } finally {
            workbook.close();
        }
        return result;
    }

    /**
     * 项目试卷设置关联
     *
     * @param paramBean
     * @return
     */
    public OutBean savePaperLink(ParamBean paramBean) {
        OutBean outBean = new OutBean();

        String xmSzId = (String) paramBean.get("XM_SZ_ID");//项目id
        String sjCode = (String) paramBean.get("SJ_CODE");//所选试卷编码
        String[] splitCode = sjCode.split(",");
        String sjName = (String) paramBean.get("SJ_NAME");//所选试卷名称
        String[] splitName = sjName.split(",");

        // TS_XMGL_BM_KSLBK_POSTION_TYPE  TS_XMGL_BM_KSLBK_POSTION_SEQUENCE TS_XMGL_BM_KSLBK_POSTION_MODULE KS_BLACK_CJKSJBFF
        //SY_POSTION_TYPE SY_ORG_POSTION_SEQUENCE_DICT  KS_BM_TYPE2
        //获取字典数据
        List<Bean> syPostionType = DictMgr.getTreeList("TS_XMGL_BM_KSLBK_POSTION_TYPE");
        List<Bean> syOrgPostionTypeClassifyDict = DictMgr.getTreeList("TS_XMGL_BM_KSLBK_POSTION_SEQUENCE");
        List<Bean> ksBmType2 = DictMgr.getTreeList("TS_XMGL_BM_KSLBK_POSTION_MODULE");
        List<Bean> ksBlackCjksjb = DictMgr.getTreeList("KS_BLACK_CJKSJB");/*KS_BLACK_CJKSJB  KS_BLACK_CJKSJBFF*/
        //获取字典中第一个数据的code  SEQUENCE
        String postionType = (String) syPostionType.get(0).get("ITEM_CODE");
        String orgPostionTypeClassify = (String) syOrgPostionTypeClassifyDict.get(0).get("ID");
        String ksBmType2Item = (String) ksBmType2.get(0).get("ITEM_CODE");
        String ksBlackCjksjbItem = (String) ksBlackCjksjb.get(0).get("ITEM_CODE");

        Bean queryBean = new Bean();
        for (int i = 0; i < splitCode.length; i++) {
            String code = splitCode[i];
            queryBean.set("SJ_CODE", code);
            queryBean.set("XM_SZ_ID", xmSzId);
            if (ServDao.count(SERV_ID, queryBean) <= 0) {
                //如果试卷未添加到试卷设置中 就添加；否则跳过
                Bean dataBean = new Bean();
                dataBean.set("XM_SZ_ID", xmSzId);
                dataBean.set("SJ_CODE", code);
                dataBean.set("SJ_NAME", splitName[i]);
                dataBean.set("SJ_DYLB", postionType);
                dataBean.set("SJ_DYXL", orgPostionTypeClassify);
                dataBean.set("SJ_DYMK", ksBmType2Item);
                dataBean.set("SJ_DYJB", ksBlackCjksjbItem);
                ServDao.create(SERV_ID, dataBean);
            }
        }

        return outBean.setMsg("添加试卷成功").setOk();
    }

}
