package com.rh.core.util.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 压缩服务器端反馈给客户端数据的过滤器。常用于压缩js、css等
 * @author yangjy
 * 
 */
public class GZipServletFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (acceptsGZipEncoding(httpRequest)) {
            httpResponse.addHeader("Content-Encoding", "gzip");
            GZipServletResponseWrapper gzipResponse = null;
            try {
                gzipResponse =
                        new GZipServletResponseWrapper(httpResponse);
                chain.doFilter(request, gzipResponse);
            } finally {
                if (gzipResponse != null) {
                    gzipResponse.close();
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * 判断客户端浏览器是否支持Gzip压缩
     * @param httpRequest 客户端请求
     * @return 是否支持Gzip压缩
     */
    private boolean acceptsGZipEncoding(HttpServletRequest httpRequest) {
        String acceptEncoding = httpRequest.getHeader("Accept-Encoding");
        return acceptEncoding != null && acceptEncoding.indexOf("gzip") != -1;
    }

}
