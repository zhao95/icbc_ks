/*
 * Copyright (c) 2011 Ruaho All rights reserved.
 */
package com.rh.core;

import com.rh.core.util.RequestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 通用响应Servlet
 *
 * @version $Id$
 */
public class TemporaryLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
     * 请求处理，要求url格式为：http://....:80/t(临时登录入口)
     *
     * @param request  请求头
     * @param response 响应头
     * @throws ServletException ServletException
     * @throws IOException      IOException
     */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        RequestUtils.sendDisp(request, response, "/ts/ksgogogo.jsp");
    }
}
