package com.rh.core.wfe;



import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;

/**
 * 接收处理完毕页面的请求。
 * 
 * @author yangjy
 */
public class WfIntegrationProcessServ {
    
    /**
     * 接收处理完毕页面请求。保存意见、分发和处理工作流送下一步功能。
     * 
     * @param paramBean 参数
     * @return
     */
    public OutBean process(ParamBean paramBean) {
        WfIntegrationProcessor processor = new WfIntegrationProcessor(paramBean);
        
        // 保存表单数据
        OutBean resultForm = processor.saveFormData();
        processor.checkExecResult(resultForm);
        
        // 保存意见
        OutBean resultMind = processor.saveMind(paramBean);
        processor.checkExecResult(resultMind);
        
        // 送下一步
        OutBean resultNext = processor.toNext(paramBean);
        processor.checkExecResult(resultNext);
        
        // 传阅
        OutBean resultFenfa = processor.saveSendData();
        processor.checkExecResult(resultFenfa);
        
        OutBean out = new OutBean();
        out.setOk();
        out.set("WF_DATA", resultNext);
        
        return out;
    }
}
