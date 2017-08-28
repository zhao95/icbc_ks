package com.rh.core.org.auth.login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.rh.core.base.Context;
import com.rh.core.serv.OutBean;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.device.Device;
import com.rh.core.util.device.DeviceUtils;

/**
 * 
 * @author yangjy
 * 
 */
public class LoginHelper {
    private static final Logger LOG = Logger.getLogger(LoginHelper.class);

    /**
     * @param request 请求对象
     * @param removeParamKey 响应对象
     * @return 当前Get请求的 URL
     */
    public static String getCurrentUrl(HttpServletRequest request, String... removeParamKey) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(request.getScheme());
        buffer.append("://");
        buffer.append(request.getServerName());
        int port = request.getServerPort();
        if (port != 80) {
            buffer.append(":").append(port);
        }

        buffer.append(request.getRequestURI());
        String queryString = request.getQueryString();
        if (StringUtils.isNotEmpty(queryString)) {

            queryString = removeUrlParameter(queryString,
                    "code");
            if (removeParamKey != null) {
                for (String paramKey : removeParamKey) {
                    queryString = removeUrlParameter(queryString, paramKey);
                }
            }

            if (StringUtils.isNotEmpty(queryString)) {
                buffer.append("?").append(queryString);
            }
        }

        return buffer.toString();
    }

    /**
     * 
     * @param request HttpServletRequest 对象
     * @param removeParam 需要被移除的参数
     * @return 进入登录界面的URL
     */
    public static String getLoginUrl(HttpServletRequest request, String... removeParam) {
        Device device = DeviceUtils.getCurrentDevice(request);
        final String contextPath = request.getContextPath();
        String loginUrl = null;
        if (contextPath.endsWith("/")) {
            loginUrl = contextPath;
        } else {
            loginUrl = contextPath + "/";
        }
        if (device != null && device.isMobile()) {
            loginUrl = Context.getSyConf("LOGIN_URL_MOBILE", loginUrl);
            loginUrl = StringUtils.remove(loginUrl, "\n");
            loginUrl = StringUtils.remove(loginUrl, "\r");
        }

        StringBuilder redUrl = new StringBuilder(loginUrl);
        if (redUrl.indexOf("?") >= 0) {
            redUrl.append("&");
        } else {
            redUrl.append("?");
        }
        String oldUrl = getCurrentUrl(request, removeParam);
        try {
            redUrl.append("redirect_url=").append(URLEncoder.encode(oldUrl, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
        }

        return redUrl.toString();
    }

    /**
     * 增加参数
     * @param url 老的URL
     * @param key 参数名
     * @param value 参数值
     * @return 增加参数之后的URL
     */
    public static String addParameter(String url, String key, String value) {
        StringBuilder result = new StringBuilder();
        result.append(url);
        if (url.indexOf("?") > 0) {
            result.append("&");
        } else {
            result.append("?");
        }

        return result.toString();
    }

    /**
     * 向客户端输出错误信息
     * @param response HttpServletResponse对象
     * @param status 状态值
     * @param errMsg 错误消息
     */
    public static void responseError(HttpServletResponse response, int status, String errMsg) {
        try {
            OutBean bean = new OutBean();
            bean.setMsg(errMsg);
            response.setStatus(status);
            response.getWriter().write(JsonUtils.toJson(bean));
            response.flushBuffer();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * 移除参数中的ticket参数，避免出现问题。
     * 
     * @param queryString 查询字符串。
     * @param paramKey 需要移除的参数名
     * @return 移除参数之后的URL参数
     */
    private static String removeUrlParameter(String queryString, String paramKey) {
        int pos = queryString.indexOf(paramKey + "=");

        if (pos == 0) { // 第一个参数
            int end = queryString.indexOf("&");
            if (end > 0) {
                return queryString.substring(end + 1);
            } else { // 只有一个参数
                return "";
            }
        } else if (pos > 0) { // 找到了，不是第一个参数
            pos = queryString.indexOf("&" + paramKey + "=");
            if (pos >= 0) { // 找到了
                int end = queryString.indexOf("&", pos + 1);
                if (end > 0) { // 是中间的参数
                    queryString = queryString.substring(0, pos)
                            + queryString.substring(end);
                    return queryString;
                } else { // 是最后一个参数
                    return queryString.substring(0, pos);
                }
            }
        }

        return queryString;
    }
}
