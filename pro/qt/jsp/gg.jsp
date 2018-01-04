<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
 -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.rh.core.serv.ServDao" %>
<%@ page import="com.rh.core.base.Bean" %>
<%@ page import="com.rh.core.org.DeptBean" %>
<%@ page import="com.rh.core.serv.dict.DictMgr" %>
<%@ include file= "../../sy/base/view/inHeader-icbc.jsp" %>
<%
	String id = request.getParameter("id");
	Bean ks = ServDao.find("TS_GG",id);
	String title = ks.getStr("GG_TITLE");
	String content = ks.getStr("GG_CONTENTS");
// 	String dept = DictMgr.getName("SY_ORG_DEPT",ks.getStr("S_DEPT"));
	DeptBean deptBean = OrgMgr.getDept(ks.getStr("S_DEPT"));
	String dept="";
	if(deptBean!=null){
		dept=deptBean.getName();
	}
    String sAtime = ks.getStr("S_ATIME").substring(0,19);
%>

<html>
  <head>
    <title>公告详情</title>
  	<link href="<%=urlPath %>/qt/css/jquery-ui-themes.css" rel="stylesheet" type="text/css"/>
   	<link href="<%=urlPath %>/qt/css/axure_rp_page.css" rel="stylesheet" type="text/css"/>
   	<link href="<%=urlPath %>/qt/css/styles.css" rel="stylesheet" type="text/css"/>
    <link href="<%=urlPath %>/qt/css/styles1.css" rel="stylesheet" type="text/css"/>
  </head>
    <body>
      <div class="" id="base">
      <!-- Unnamed (矩形) -->
      <div class="ax_default box_1" id="u3547">
        <div id="u3547_div"></div>
        <!-- Unnamed () -->
        <div class="text" id="u3548" style="display: none; visibility: hidden;">
          <p><span></span></p>
        </div>
      </div>

      <!-- Unnamed (图像) -->
      <div class="ax_default image" id="u3549">
         <img class="img " id="u3549_img" src="<%=urlPath %>/qt/img/u3148.png"> 
         <div class="text" id="u3550" style="display: none; visibility: hidden;">
          <p><span></span></p>
        </div>
      </div>

      

      <!-- Unnamed (图像) -->
      <div class="ax_default image" id="u3553">
        <img class="img " id="u3553_img" src="<%=urlPath %>/qt/img/u3376.png">
        <div class="text" id="u3554" style="display: none; visibility: hidden;">
          <p><span></span></p>
        </div>
      </div>

      <!-- foot_text (矩形) -->
 <!--       <div class="ax_default box_3" id="u3555" data-label="foot_text"> 
        <div id="u3555_div"></div>
        Unnamed ()
        <div class="text" id="u3556" style="display: none; visibility: hidden;">
          <p><span></span></p>
        </div>
      </div> -->

      <!-- Unnamed (矩形) -->
      <div class="ax_default box_1" id="u3557">
        <div id="u3557_div"></div>
        <!-- Unnamed () -->
        <div class="text" id="u3558" style="display: none; visibility: hidden;">
          <p><span></span></p>
        </div>
      </div>

      <!-- Unnamed (矩形) -->
      <div class="ax_default label" id="u3559">
        <div id="u3559_div"></div>
       <div class="text" id="GG_TITLE" style="top: -6px;position: relative;">
          <p><span><%=title%></span></p>
        </div>
        <div class="" id="" style="color: #999999;line-height: 19px;font-size: 14px;">
          <p><span><%=sAtime%> <%=dept%></span></p>
        </div>
      </div>
       <!-- Unnamed (图像) -->
      <div class="ax_default image" id="u3561">
	       	 <img class="img " id="u3561_img" src="<%=urlPath %>/qt/img/u1182.png">
	        <div class="text" id="u3562" style="display: none; visibility: hidden;">
	          <p><span></span></p>
	        </div>
      </div>

      <!-- Unnamed (矩形) -->
      <div class="ax_default label" id="u3563">
        <div id="u3563_div"></div>
        <!-- Unnamed () -->
        <div class="text" id="u3564">
          <p><span>/</span></p>
        </div>
      </div>

      <!-- Unnamed (矩形) -->
      <div class="ax_default label" id="u3565">
        <div id="u3565_div"></div>
        <!-- Unnamed () -->
        <div class="text" id="u3566">
          <p><span>通知公告</span></p>
        </div>
      </div>

      <!-- Unnamed (横线) -->
      <div class="ax_default line" id="u3567">
        <img class="img " id="u3567_img" src="<%=urlPath %>/qt/img/u3567.png">
        <div class="text" id="u3568" style="display: none; visibility: hidden;">
          <p><span></span></p>
        </div>
      </div>

      <!-- Unnamed (矩形) -->
  <!--     <div class="ax_default label" id="u3569">
        <div id="u3569_div"></div>
        Unnamed ()
        <div class="text" id="u3570">
          <p><span>建议使用 1024 x 768 及以上分辨率浏览</span></p>
        </div>
      </div> -->

      <!-- Unnamed (矩形) u3572-->
      <div class="ax_default _文本" id="u3571">
        <div id="u3571_div"></div>
        <!-- Unnamed () -->
        <div class="text" id="GG_CONTENTS">
          <p><span><%=content%></span></p>
        </div>
      </div>
    </div>
    </body>
   </html> 