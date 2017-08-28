/**
 * 
 */
package com.rh.core.plug.report;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认报表参数处理器
 * 
 * @author chenchh
 *
 */
public class DefaultParameterHandler implements IReportPatameterHandler {

    /* (non-Javadoc)
     * @see com.rh.core.plug.report.IReportPatameterHandler#handle(com.rh.core.plug.report.ReportConfig)
     */
    @Override
    public Map<String, String> handle(ReportConfig config) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(ReportConfig.REPORT_NAME, config.getReportName());
        
        params.put(ReportConfig.REPORT_FILE_NAME, config.getReportFileName());
        params.put(ReportConfig.REPORT_SQL_FILTER , config.getSqlFilter());
        for (String pair : config.getParams().split("&")) {
            int index = pair.indexOf("=");
            params.put(pair.substring(0, index), pair.substring(index + 1));
        }
        params.put(ReportConfig.EXCEL_ABLE, config.isExcelAble() ? "yes" : "no");
        params.put(ReportConfig.PDF_ABLE, config.isPdfAble() ? "yes" : "no");
        return params;
    }

}
