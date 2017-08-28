package com.rh.core.comm.print;

import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.mind.UserMind;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Constant;
/**
 * 
 * @author zhanghailong
 * 
 */
public class PrintDataHelper {
    /** 服务主键：打印模板 */
    public static final String SY_COMM_PRINT_TMPL = "SY_COMM_PRINT_TMPL";
        
    @SuppressWarnings("unused")
    private String ftlName = "printMind.ftl";

    /**
     * 设置信息模版
     * @param ftl 模版
     */
    public void setMindPrintFtl(String ftl) {
        this.ftlName = ftl;
    }
   /**
    * 获取公共数据
    * @param paramBean 入参
    * @return 需要打印的数据
    */
    public OutBean getCommData(Bean paramBean) {
        /**
         * 这四个参数必须确保都传递了，代码不做检查，默认认为都传了
         */
        String servId = paramBean.getStr("servId"); // 服务ID
        String servSrcId = paramBean.getStr("servSrcId");
        String dataId = paramBean.getStr("dataId"); // 数据ID

        // 审签单基础数据数据
        ParamBean queryBean = new ParamBean().set(Constant.PARAM_SERV_ID,
                servId).set(Constant.KEY_ID, dataId);
        OutBean outBean = ServMgr.act(servId, "getPrintData", queryBean);

        FileDataHelper fdh = new FileDataHelper(servSrcId, dataId);
        outBean.put("_ZHENGWEN_", fdh.getFileNames("ZHENGWEN"));
        outBean.put("_FUJIAN_", fdh.getFileNames("FUJIAN"));
        outBean.put("_ZHUANFA_", fdh.getFileNames("ZHUANFA"));
        outBean.put("_fdh_", fdh);
        //获取相关文件名
        outBean.put("_RELATEFILE_", fdh.getRelateFileNames(dataId, servSrcId));
        /*
         * 取得意见列表中的数据 按意见编码分类，并拼接显示样式
         */
        UserMind userMind = UserMind.create(new ParamBean(paramBean), Context.getUserBean());
        userMind.query(servId, dataId);

        outBean.put("_userMind_", userMind);

        return outBean;
    }
    
    
    /**
     * 后台替换特殊字符 替换包含Html中<> <——&lt;    >——&gt;   "——&quot; 逐个替换
     * @param ftlcontent 模板内容
     * @return 替换后的模板内容
     */
   public String replaceRegex(String ftlcontent) {
        ftlcontent = ftlcontent.replaceAll("&quot;", "\"");
        ftlcontent = ftlcontent.replaceAll("&lt;", "<");
        ftlcontent = ftlcontent.replaceAll("&gt;", ">");
        return ftlcontent;
   }
    
    /**
     * 
     * @param servId 服务ID
     * @return 该服务是否存在打印模板
     */
    public static boolean existPrintTmpl(String servId) {
        List<Bean> dictItems = DictMgr.getItemList(SY_COMM_PRINT_TMPL);
        for (Bean item: dictItems) {
            if (item.getStr("ID").equals(servId)) { //配置的字典上有这个服务ID
                return true;
            }
        }
        
        return false;
    }
}
