/*
 * Copyright (c) 2011 Ruaho All rights reserved.
 */
package com.rh.core;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.BaseContext.APP;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.comm.ConfMgr;
import com.rh.core.comm.FileMgr;
import com.rh.core.comm.FileStorage;
import com.rh.core.comm.file.FileUploadListener;
import com.rh.core.comm.file.upload.DefaultUploadHandler;
import com.rh.core.comm.file.upload.FileUploadHandler;
import com.rh.core.comm.file.upload.RedheadUploadHandler;
import com.rh.core.comm.file.upload.UEditorUploadHandler;
import com.rh.core.org.UserBean;
import com.rh.core.plug.IBarcode;
import com.rh.core.plug.search.RhFileClient;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.Constant;
import com.rh.core.util.ImageUtils;
import com.rh.core.util.Lang;
import com.rh.core.util.RequestUtils;
import com.rh.core.util.Strings;
import com.rh.core.util.encoder.Base64;
import com.rh.core.util.file.RuahoFileItem;
import com.rh.core.util.http.HttpGetResponse;
import com.rh.core.util.http.HttpUtils;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 文件响应Servlet
 * @author liwei
 */
public class FileServlet extends HttpServlet {

    /** log */
    private static Log log = LogFactory.getLog(FileServlet.class);
    /** UID */
    private static final long serialVersionUID = 1L;
    /** default io buffer size */
    /** default http response client error code */
    private static final int RESPONSE_CLIENT_ERROR_CODE = 400;
    /** default http response server error code */
    private static final int RESPONSE_SERVER_ERROR_CODE = 500;
    /** default file download uri pattern example-http://localhost:8080/file/download/sjdh33242.doc */
    private static final String FILE_DOWNLOAD_URI_PATTERN = "download";
    /** default file preview uri pattern example-http://localhost:8080/file/preview/sjdh33242.doc */
    private static final String FILE_PREVIEW_URI_PATTERN = "preview";
    private static final String FILE_OPEN_URI_PATTERN = "open";

    private static final String FILE_CREATE_URI_PATTERN = "thum";
    private static final String B64_RETURN_TYPE_PATTERN = "base64";
    private static final String THUM_DEAFULT_SIZE = "100";

    /**
     * 上传文件
     * @param request 请求头 如果上传文件同时创建缩略图:thum=true 如果上传文件后返回的文件 bean 需要图片的 base64: return=base64
     * @param response 响应头
     * @throws ServletException ServletException
     * @throws IOException IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        request.setCharacterEncoding(Constant.ENCODING);
        Context.cleanThreadData();
        Context.setRequest(request);
        ParamBean param = new ParamBean(request, false);
        String callbackStr = param.getStr("callback");
        //进行跨域访问处理，如果为CORS的OPTIONS请求，则设置完response后忽略后续执行
        if (RequestUtils.crossDomainRequest(request, response, callbackStr)) {
            return;
        }
        UserBean userBean;
        if (request.getParameter("X-XSRF-TOKEN") != null) {
            userBean = Context.getUserBean(request, request.getParameter("X-XSRF-TOKEN"), 
                    request.getParameter("X-DEVICE-NAME"));
        } else {
            userBean = Context.getUserBean(request);
        }
        
        if (userBean == null) {
            if (request.getHeader("X-DEVICE-NAME") != null 
                    || request.getParameter("X-DEVICE-NAME") != null) { //远程设备方式访问返回401错误码
            	
            	// 如果不为空,返回token错误
                if (request.getParameter("X-XSRF-TOKEN") != null) {
                	response.setHeader(Constant.RTN_MSG, URLEncoder.encode(Context.getSyConf("CC_TOKEN_ERROR_MSG", "用户凭证信息验证失败,请重新登录!"), "utf-8"));
                } else { // 如果为空,返回token为空
                	response.setHeader(Constant.RTN_MSG, URLEncoder.encode(Context.getSyConf("CC_TOKEN_NULL_MSG", "用户凭证信息为空,请重新登录!"), "utf-8"));
                }
            	
            	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            
            } else { //浏览器进行登录跳转
                RequestUtils.sendDisp(request, response, "/sy/comm/login/jumpToIndex.jsp");
                response.flushBuffer();
            }
            return;
        }
        
        FileUploadHandler handler = null;
        String handlerStr = request.getParameter("handler");
        if (handlerStr == null) {
            handler = new DefaultUploadHandler();
        } else if (handlerStr.equals("ueditor")) {
            handler = new UEditorUploadHandler();
        } else if (handlerStr.equals("redhead")) {
            handler = new RedheadUploadHandler();
        } else {
            handler = new DefaultUploadHandler();
        }

        handler.handleRequest(request, param); // 处理参数信息

        // upload...
        String fileId = getFileId(request);
        String model = RequestUtils.getStr(request, "model");

        Bean resultBean = null;
        try {
            if (0 < fileId.length()) {
                Bean fileBean = FileMgr.getFile(fileId);
                if (fileBean != null) { // update file
                    if (model.equals("saveHist")) {
                        resultBean = update(request, param, false, fileBean);
                    } else if (model.equals("cropImg")) {
                        resultBean = cropImg(request, param, fileBean);
                    } else {
                        resultBean = update(request, param, true, fileBean);
                    }
                } else { // create file
                    resultBean = create(request, param);
                }
            } else {
                resultBean = create(request, param);
            }
        } catch (TipException e) { // 不记录Log，直接传递错误信息给界面
            if (log.isDebugEnabled()) { // 调试模式下也输出exception到log中
                log.debug(e.getMessage(), e);
            }
            resultBean = new Bean();
            resultBean.set(Constant.RTN_MSG, Constant.RTN_MSG_ERROR + e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resultBean = new Bean();
            resultBean.set(Constant.RTN_MSG, Constant.RTN_MSG_ERROR + e.getMessage());
        }
        // =================处理返回信息===================
        if (!response.isCommitted()) {
            // after upload handle
            handler.handleResponse(request, response, resultBean);
        }
    }

    /**
     * 下载/预览 文件请求处理
     * @param request 请求头
     * @param response 响应头
     * @throws ServletException ServletException
     * @throws IOException IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // use restful uri
        request.setCharacterEncoding("UTF-8");
        // TODO upload qrcode use post method
        String act = RequestUtils.getStr(request, "act");
        if (act.equals("qrCode")) {
            String value = RequestUtils.getStr(request, "value");
            OutputStream out = response.getOutputStream();
            try {
                int size = RequestUtils.get(request, "size", 200);
                IBarcode barcode = (IBarcode) Lang.createObject(IBarcode.class,
                        Context.getInitConfig("rh.barcode", "com.rh.opt.plug.barcode.BarcodeOutput"));
                barcode.genQRCode(out, value, size, size);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            out.flush();
            response.flushBuffer();
            return;
        }

        if (act.equals(FILE_PREVIEW_URI_PATTERN)) {
            // file preview
            preview(request, response);
        } else {
            // set client cache
            response.setHeader("Server", "rhfile-server");
            response.setHeader("Cache-Control", "max-age=" + (3600 * 2 * 12));
            // file download
            download(request, response);
        }

    }

    /**
     * 文件预览
     * @param request 请求头
     * @param response 响应头
     * @throws ServletException ServletException
     * @throws IOException IOException
     */
    private void preview(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String currentHost = getHost(request.getRequestURL().toString());

        Context.getUserBean(request);
        String fileId = getFileId(request);
        fileId = URLDecoder.decode(fileId, "UTF-8");
        String type = RequestUtils.getStr(request, "type");
        boolean isMobile = false;
        if (type.equals("mobile")) {
            isMobile = true;
        }

        // http file
        if (fileId.startsWith("http://")) {
            // get file from http
            HttpGetResponse getResp = HttpUtils.httpGet(fileId);
            // String fileName = getResp.getDownloadName();
            InputStream is = getResp.getInputStream();
            previewWithInputStream(currentHost, is, "", response, isMobile);
        } else {
            // get file from internal fs
            Bean fileBean = FileMgr.getFile(fileId);
            if (null == fileBean) {
                String errorMsg = Context.getSyMsg("SY_DOWNLOAD_FILE_NOT_FOUND");
                if (null == errorMsg || 0 == errorMsg.length()) {
                    errorMsg = "the file not found";
                }
                throw new TipException(errorMsg);
            }

            // // get file checksum
            // String checksum = fileBean.getStr("FILE_CHECKSUM");
            // // preview with file checksum first
            // int status = 0;
            // if (null == checksum || 0 == checksum.length()) {
            // log.warn("the file's checksum is null, file id:" + fileId);
            // } else {
            // status = RhFileClient.getInstance().getPreviewStatusCode(checksum, isMobile);
            // }
            //
            // if (200 == status) {
            // String url = RhFileClient.getInstance().getServerUri();
            // if (!url.endsWith("/")) {
            // url += "/";
            // }
            // url += "preview/" + checksum + fileId.substring(fileId.lastIndexOf(".")) + ".html";
            // if (isMobile) {
            // url += "?previewType=mobile";
            // }
            // response.sendRedirect(url);
            // } else {
            // String fileName = resultBean.getStr("FILE_NAME");
            InputStream is = FileMgr.download(fileBean);
            previewWithInputStream(currentHost, is, "", response, isMobile);
            // }
        }

    }

    /**
     * 基于inputstream文件预览
     * @param currentHost - 当前访问服务器host
     * @param is - inputstream
     * @param fileName - file name
     * @param response 响应头
     * @param isMobile - 是否为手机版
     * @throws ServletException ServletException
     * @throws IOException IOException
     */
    private void previewWithInputStream(String currentHost, InputStream is, String fileName,
            HttpServletResponse response, boolean isMobile)
            throws ServletException, IOException {
        String url = "";
        try {
            url = RhFileClient.getInstance().preview(is, fileName, isMobile);
        } catch (IOException e) {
            if (e.getMessage().contains("Do not support suffix")) {
                log.warn("该文件不支持预览!", e);
                response.getOutputStream().write("该文件不支持预览!".getBytes("UTF-8"));
                response.getOutputStream().flush();
                return;
            } else {
                throw e;
            }

        }
        url = RhFileClient.getInstance().getRedirectServerUri() + url;
        url = url.replace("${host}", currentHost);
        log.debug("File Service redirect to " + url);

        IOUtils.closeQuietly(is);
        response.sendRedirect(url);
    }

    /**
     * 下载/使用浏览器打开文件
     * @param request 请求头
     * @param response 响应头
     * @throws ServletException ServletException
     * @throws IOException IOException
     */
    private void download(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String act = RequestUtils.getStr(request, "act");
        InputStream is = null;
        String fileId = null;
        Bean resultBean = null;
        FileUploadListener listener = null ;
        try {
            Context.getUserBean(request);
            fileId = getFileId(request);
            // 获取图片文件
            if (isImage(fileId)) {
                String size = RequestUtils.getStr(request, "size");
                resultBean = FileMgr.getImgFile(fileId, size);
            } else {
                // 获取普通文件
                resultBean = FileMgr.getFile(fileId);
            }
            if (null == resultBean) {
                String errorMsg = Context.getSyMsg("SY_COMM_FILE_NOT_EXIST", fileId);
                if (null == errorMsg || 0 == errorMsg.length()) {
                    errorMsg = "the file not found";
                }
//                throw new TipException(errorMsg);
                response.setStatus(404);
				OutputStream out = response.getOutputStream();
				out.write(errorMsg.getBytes());
				out.flush();
				return;
            }
            
            listener = this.createListener(resultBean.getStr("SERV_ID"));
            
            String relativePath = resultBean.getStr("FILE_PATH");
            if (0 == relativePath.length()) {
                // set waiting for storage default image
                String waiting4Storage = Context.app(APP.SYSPATH) + File.separator
                        + "sy/comm/file/images/waiting_for_storage.png";
                is = new FileInputStream(waiting4Storage);
            } else {
            	if(listener != null && listener.enableDownload()) {
            		is = listener.download(request, resultBean);
            	} else {
            		is = FileMgr.download(resultBean);
            	}
            }

            // response for download request
            // set response file name
            if (resultBean.contains("FILE_NAME") && !FILE_OPEN_URI_PATTERN.equals(act)) {
                String downloadFileName = FileMgr.getFileDisName(resultBean);
                RequestUtils.setDownFileName(request, response, downloadFileName);
                // RequestUtils.setDownFileName(request, response, resultBean.getStr("FILE_NAME"));
            }
            // set response content type
            String downFileType = "attachment;";
            if (0 < resultBean.getStr("FILE_MTYPE").length()) {
                downFileType = resultBean.getStr("FILE_MTYPE");
            }
            if (act.equals(FILE_DOWNLOAD_URI_PATTERN)) {
                downFileType = "attachment;";
            }
            if (!FILE_OPEN_URI_PATTERN.equals(act)) {
                // set response file content
                response.setContentType(downFileType);
            }
            
            //response设置数据大小
            if (0 < is.available()) {
                response.setContentLength(is.available());
                log.info("从流中读取文件大小, 大小为 : " + is.available());
            } else {
            	int totalBytes = resultBean.get("FILE_SIZE", 0);
            	log.info("从文件字段中获取文件大小, 大小为 : " + totalBytes);
            	if (0 < totalBytes) {
            		log.info("从InputStream流中获取不到文件大小,只能从文件表中获取,可能不准!");
            		response.setContentLength(totalBytes);
            	}
            }
      
            OutputStream out = response.getOutputStream();
            IOUtils.copyLarge(is, out);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(out);
            out.flush();
        } catch (TipException e) { // 不记录Log，直接传递错误信息给界面
            if (log.isDebugEnabled()) { // 调试模式下也输出exception到log中
                log.debug(e.getMessage(), e);
            }
            outputError(response, e.getMessage(), RESPONSE_CLIENT_ERROR_CODE);
        } catch (FileNotFoundException fe) {
            log.error(fe.getMessage());
            outputError(response, "file not found in the server.", RESPONSE_SERVER_ERROR_CODE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            outputError(response, e.getMessage(), RESPONSE_SERVER_ERROR_CODE);
        } finally {
            IOUtils.closeQuietly(is);
            if(listener != null) {
            	listener.afterDownload(resultBean);
            }
            response.flushBuffer();
        }
    }
    
    /**
     * 输出错误消息
     * @param response HttpServletResponse
     * @param errMessage 消息内容
     * @param status HTTP请求响应状态码
     * @throws IOException Io错误
     */
    private void outputError(HttpServletResponse response, String errMessage, int status) throws IOException
    {
        response.setStatus(status);
        response.setContentType("text/html;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        out.write(Strings.escapeHtml(errMessage).getBytes("UTF-8"));
        out.flush();
    }

    /**
     * create file
     * @param request httpServletRequest
     * @param paramBean param Bean
     * @return out bean
     * @throws IOException IOException
     */
    private Bean create(HttpServletRequest request, Bean paramBean) throws IOException {
        String fileId = getFileId(request);
        String thum = RequestUtils.getStr(request, FILE_CREATE_URI_PATTERN);
        String retunType = RequestUtils.getStr(request, "return");
        paramBean.set("FILE_ID", fileId);

        // handle file uploads param
        List<FileItem> items = handleFileUpload(request, paramBean);

        FileUploadListener listener = this.createListener(paramBean);

        if (listener != null) {
            listener.beforeAdd(paramBean);
        }

        /**
         * 前缀加服务ID配置的文件监听
         */
        FileUploadListener servListener = this.createListener(paramBean.getStr("SERV_ID"));
        if (servListener != null) {
            servListener.beforeAdd(paramBean);
        }
        
        String fileName = paramBean.getStr("FILE_NAME");
        List<Bean> dataList = new ArrayList<Bean>();
        // upload file from file form
        Iterator<FileItem> iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = iter.next();
            checkSpecialFile(item);
            InputStream is = null;
            try {
                if (null == fileName || 0 == fileName.length()) {
                    fileName = item.getName();
                    paramBean.set("FILE_NAME", fileName);
                }

                String mType = getMimeType(item, request);
                paramBean.set("FILE_MTYPE", mType);

                // 上传文件
                InputStream input = null;
                if (isCuttingImage(paramBean)) {
                    // 剪裁后图片
                    input = cuttingImage(paramBean, item);
                } else {
                    // 原始文件
                    input = item.getInputStream();
                }
                
            
                // 是否翻转图片
                double rotate = paramBean.get("rotate", 0);
                if (rotate > 0 && isImage(fileName)) {
//                   IOUtils.closeQuietly(input);
                    String type = getImageType(item);
                    input = rotateImage(rotate, input, type);
//                    item.delete();
//                    item = new RuahoFileItem(input);
                }
                
                Bean data = FileMgr.upload(paramBean, input);
                IOUtils.closeQuietly(input);

                Bean thumBean = null;
                // 创建缩略图
                if (thum.equals("true") && isImage(fileName)) {
                    thumBean = FileMgr.getThumFile(data, paramBean.get("size", THUM_DEAFULT_SIZE));
                }
                // 返回图片 base64
                if (retunType.equals(B64_RETURN_TYPE_PATTERN)) {
                    // 如果有缩略图使用缩略图
                    if (null == thumBean) {
                        is = FileMgr.download(data);
                    } else {
                        // 如果没有缩略图，使用源图
                        is = FileMgr.download(thumBean);
                    }
                    String base64 = new String(Base64.encode(IOUtils.toByteArray(is)), Constant.ENCODING);
                    data.set(B64_RETURN_TYPE_PATTERN, base64);
                }

                dataList.add(data);
            } catch (IOException ioe) {
                log.error(" file upload error.", ioe);
                throw ioe;
            } finally {
                IOUtils.closeQuietly(is);
                item.delete();
            }

        }
        Bean resultBean = new Bean();
        resultBean.set(Constant.RTN_DATA, dataList);

        if (listener != null) {
            listener.afterAdd(paramBean, dataList);
        }
        
        if (servListener != null) {
            servListener.afterAdd(paramBean, dataList);
        }

        return resultBean;
    }

    /**
     * 截取图片
     * @param paramBean - 参数bean
     * @param item - file item
     * @return - 剪裁后图片inputstream
     * @throws IOException - 剪裁出错
     */
    private InputStream cuttingImage(Bean paramBean, FileItem item) throws IOException {
        log.info("cutting file...");
        int x1 = paramBean.get("x1", -1);
        int y1 = paramBean.get("y1", -1);
        int x2 = paramBean.get("x2", -1);
        int y2 = paramBean.get("y2", -1);

        // 坐标均不为空,将进行截取,并更新
        if (x1 > -1 && y1 > -1 && x2 > -1 && y2 > -1) {
            // 根据坐标计算出宽、高
            int width = x2 - x1;
            int height = y2 - y1;
            InputStream is = item.getInputStream();
            String type = getImageType(item);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageUtils.cutting(is, out, type, x1, y1, width, height);
            IOUtils.closeQuietly(is);
            InputStream result = new ByteArrayInputStream(out.toByteArray());
            IOUtils.closeQuietly(out);
            return result;
        } else {
            return null;
        }

    }

    /**
     * 旋转图片
     * @param rotate - 旋转度
     * @param input - file input
     * @param outType - output image type
     * @return - inputstream
     * @throws IOException 旋转时发生异常
     */
    private InputStream rotateImage(double rotate, InputStream input, String outType) throws IOException {
        log.info("rotate file...rotate:" + rotate);
        if (rotate > 0) {

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            BufferedImage buffimg = ImageIO.read(input);
            Thumbnails.of(buffimg).size(buffimg.getWidth(), buffimg.getHeight()).rotate(rotate).outputFormat(outType)
                    .toOutputStream(out);

            IOUtils.closeQuietly(input);
            InputStream result = new ByteArrayInputStream(out.toByteArray());
            IOUtils.closeQuietly(out);
            return result;
        } else {
            return null;
        }

    }
    
    /**
     * get image file type
     * @param item - file item
     * @return type: jpeg, jpg, gif, png,bmp ...
     */
    private String getImageType(FileItem item) {
        String type = FileMgr.getSuffixByMtype(item.getContentType());
        if (null == type || 0 == type.length()) {
            type = "jpeg";
        }
        if (type.startsWith(".")) {
            type = type.substring(1);
        }
        return type;
    }

    /**
     * 是否剪裁图片
     * @param paramBean - 参数bean
     * @return iscutting?
     */
    private boolean isCuttingImage(Bean paramBean) {
        int x1 = paramBean.get("x1", -1);
        int y1 = paramBean.get("y1", -1);
        int x2 = paramBean.get("x2", -1);
        int y2 = paramBean.get("y2", -1);
        return (x1 > -1 && y1 > -1 && x2 > -1 && y2 > -1);
    }

    /**
     * update file
     * @param request httpServletRequest
     * @param paramBean paramBean
     * @param overWrite is owver write
     * @param fileBean 文件Bean
     * @return out bean
     * @throws IOException IOException
     */
    private Bean update(HttpServletRequest request, Bean paramBean, boolean overWrite
            , Bean fileBean) throws IOException {
        String fileId = getFileId(request);

        paramBean.setId(fileId);
        // handle file uploads param
        List<FileItem> items = handleFileUpload(request, paramBean);
        List<Bean> dataList = new ArrayList<Bean>();
        // upload file from file form
        Iterator<FileItem> iter = items.iterator();

        FileUploadListener listener = this.createListener(paramBean);

        if (listener != null) {
            listener.beforeUpdate(paramBean, fileBean);
        }
        
        /**
         * 前缀加服务ID配置的文件监听
         * 必须通过fileBean才能获取到服务ID，因为paramBean里可能没有SERV_ID
         */
        FileUploadListener servListener = this.createListener(fileBean.getStr("SERV_ID"));
        if (servListener != null) {
            servListener.beforeUpdate(paramBean, fileBean);
        }
        
        Bean newFileBean = null;

        while (iter.hasNext()) {
            FileItem item = iter.next();
            checkSpecialFile(item);
            try {
                InputStream is = item.getInputStream();

                String mType = "";
                if (paramBean.getStr("FILE_MTYPE") != null && paramBean.getStr("FILE_MTYPE").length() > 0) {
                    mType = paramBean.getStr("FILE_MTYPE");
                } else {
                    mType = getMimeType(item, request);
                }

                // Process a file upload
//                Bean data = null;
                if (overWrite) {
                    boolean keepMeta = RequestUtils.get(request, "keepMetaData", false);
                    newFileBean = FileMgr.overWrite(fileId, is, item.getName(), paramBean.getStr("DIS_NAME")
                            , keepMeta, paramBean);
                } else {
                    newFileBean = FileMgr.update(fileId, is, mType);
                }
                IOUtils.closeQuietly(is);
                dataList.add(newFileBean);
            } catch (IOException ioe) {
                log.error(" file upload error.", ioe);
                throw ioe;
            } finally {
                item.delete();
            }
        }
        Bean resultBean = new Bean();
        resultBean.set(Constant.RTN_DATA, dataList);
        if (listener != null) {
            listener.afterUpdate(paramBean, newFileBean, dataList);
        }
        if (servListener != null) {
            servListener.afterUpdate(paramBean, newFileBean, dataList);
        }
        return resultBean;
    }

    /**
     * 图片裁剪
     * @param request - 请求参数,包括原图ID
     * @param paramBean - 裁剪参数,已换算成原图的起点坐标和宽高
     * @param fileBean - 原图Bean
     * @return
     * @throws IOException
     */
    private Bean cropImg(HttpServletRequest request, Bean paramBean, Bean fileBean) throws IOException {
        int x1 = paramBean.getInt("x1");
        int y1 = paramBean.getInt("y1");
        int width = paramBean.getInt("width");
        int height = paramBean.getInt("height");
        String pathExpre = fileBean.getStr("FILE_PATH");
        String absolutePath = FileMgr.getAbsolutePath(pathExpre);
        
        InputStream in = null;
        ByteArrayOutputStream tmp = null; // 中转图片流
        try {
            in = FileStorage.getInputStream(absolutePath);
            tmp = new ByteArrayOutputStream();
            // 裁剪图片到中转图片流中
//            ImageUtils.cutting(in, tmp, FileMgr.getSuffixByMtype(imgType).substring(1), x1, y1, width, height);
            ImageUtils.cutting(in, tmp, fileBean.getId().substring(fileBean.getId().indexOf(".") + 1), x1, y1, width, height);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(tmp);
        }
        
        InputStream result = null;
        try {
            result = new ByteArrayInputStream(tmp.toByteArray());
            // 用中转图片流重写原图
//            fileBean = FileMgr.overWrite(fileBean.getId(), result, fileBean.getStr("FILE_NAME"), false);
            
            // 用中转图片重新生成一条文件的记录，否则由于checkSum的去重，服务器端不会保存新图片
            fileBean.remove("FILE_ID");
            fileBean.remove("FILE_PATH");
            fileBean.remove("FILE_MTYPE");
            fileBean.remove("FILE_CHECKSUM");
            fileBean.remove("FILE_SIZE");
            fileBean = FileMgr.upload(fileBean, result);
            
        } finally {
            IOUtils.closeQuietly(result);
        }
        
        return fileBean;
    }
    
    /**
     * get file id from restful url
     * @param request HttpServletRequest
     * @return file Id
     */
    private String getFileId(HttpServletRequest request) {
        String uri = request.getRequestURI();

        String servlet = request.getServletPath();
        int index = uri.indexOf(servlet);
        String fileId = uri.substring(index + servlet.length());

        if (fileId.startsWith("/")) {
            fileId = fileId.substring(1);
            try {
                fileId = URLDecoder.decode(fileId, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.warn(e);
            }
        } else {
            fileId = "";
        }
        return fileId;
    }

    /**
     * 是否是图片文件?
     * @param fileId - file id
     * @return 该文件是否是图片类型
     */
    private boolean isImage(String fileId) {
        String surfix = "";
        if (0 < fileId.lastIndexOf(".")) {
            surfix = fileId.substring(fileId.lastIndexOf(".") + 1);
        }
        surfix = surfix.toLowerCase();
        boolean result = false;
        if (surfix.equals("jpg")) {
            result = true;
        } else if (surfix.equals("jpeg")) {
            result = true;
        } else if (surfix.equals("jpeg")) {
            result = true;
        } else if (surfix.equals("bmp")) {
            result = true;
        } else if (surfix.equals("png")) {
            result = true;
        } else if (surfix.equals("gif")) {
            result = true;
        }
        return result;
    }

    /**
     * 处理文件上传表单
     * @param request request
     * @param paramBean 参数Bean
     * @return 有效的上传文件的FileItem
     */
    @SuppressWarnings("unchecked")
    private List<FileItem> handleFileUpload(HttpServletRequest request, Bean paramBean) {
        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        List<FileItem> items = new ArrayList<FileItem>();
        if (isMultipart) {
            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            // Parse the request
            try {
                List<FileItem> allItems = upload.parseRequest(request);
                for (FileItem item : allItems) {
                    if (item.isFormField()) {
                        paramBean.set(item.getFieldName(), item.getString(Constant.ENCODING));
                        continue;
                    }

                    items.add(item);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new TipException("file upload param error.");
            }
        } else {
            try {
                items.add(new RuahoFileItem(request.getInputStream()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return items;
    }

    /**
     * get file mime Type
     * @param item <CODE>FileItem</CODE>
     * @param request - http request
     * @return file mtype
     */
    private String getMimeType(FileItem item, HttpServletRequest request) {
        // parse file mime type
        String contentType = item.getContentType();

        if (null == contentType || 0 == contentType.length() || contentType.equals("application/octet-stream")) {
            contentType = FileMgr.getMTypeByName(item.getName());
        }

        if (contentType.equals("application/octet-stream")) {
            contentType = request.getContentType();
            if (-1 < contentType.lastIndexOf(";")) {
                contentType = contentType.substring(0, contentType.lastIndexOf(";"));
            }
        }

        return contentType;
    }

    /**
     * 
     * @param paramBean 参数名称
     * @return 创建监听对象
     */
    private FileUploadListener createListener(Bean paramBean) {
        String listener = paramBean.getStr("listener");
        if (paramBean.isEmpty("listener")) {
            return null;
        }
        return this.doCreateListener(FileUploadListener.CONF_PREFIX + listener);
    }
    
    /**
     * @param servId 参数名称
     * @return 通过SERV_ID获取配置创建监听对象
     */
    private FileUploadListener createListener(String servId) {
        if (Strings.isBlank(servId)) {
            return null;
        }
        
        return this.doCreateListener(FileUploadListener.CONF_PREFIX_LISTENER + servId);
    }
    
    /**
     * 根据配置KEY构造监听类
     * @param confKey 监听配置KEY
     * @return 返回监听对象
     */
    private FileUploadListener doCreateListener(String confKey) {
        String clsName = ConfMgr.getConf(confKey, "");
        if (StringUtils.isEmpty(clsName)) {
            return null;
        }
        
        return (FileUploadListener) Lang.createObject(FileUploadListener.class, clsName);
    }

    /**
     * 获取用户访问的目标地址
     * @param url - url
     * @return host
     */
    private String getHost(String url) {
        url = url.replace("http://", "");
        url = url.replace("https://", "");
        int portFlag = url.indexOf(":");
        if (-1 < portFlag) {
            url = url.substring(0, portFlag);
        }
        int uriFlag = url.indexOf("/");
        if (-1 < uriFlag) {
            url = url.substring(0, uriFlag);
        }
        return url;
    }
    
    /**
     * 检查特殊文件 ， 如果存在于黑名单中，则抛出提示错误信息
     * @param item 文件条目
     * 
     */
    private void checkSpecialFile(FileItem item) {
        String fileName = item.getName();
        if (0 < fileName.lastIndexOf(".")) {
            String suffix = "," + FilenameUtils.getExtension(fileName) + ",";
            
            //不能上传的文件后缀
            String blackFile = "," + Context.getSyConf("SY_FILE_NOT_UPLOAD_SUFFIX", "") + ","; 
            
            if (blackFile.toLowerCase().indexOf(suffix.toLowerCase()) >= 0) { //存在于黑名单中
                throw new TipException("不能上传后缀是 " + suffix + " 的文件");
            }
        }
    }
}
