<!DOCTYPE html>
<%@page import="com.rh.core.serv.ServDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>非资格报名列表</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<%@ include file="/qt/jsp/header-logo.jsp"%>
<%@ include file="/sy/base/view/inHeader-icbc.jsp"%>
<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/font-awesome-4.7.0/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/ionicons/css/ionicons.min.css">

<script src="<%=CONTEXT_PATH%>/qt/js/html5shiv.min.js"></script>
<script src="<%=CONTEXT_PATH%>/qt/js/respond.min.js"></script>
<!-- Theme style -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/dist/css/AdminLTE.min.css">
<!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/dist/css/skins/_all-skins.min.css">
<body class="hold-transition skin-black sidebar-mini">
	<style>.a table tr{height:50px;padding-left: 10px;}</style>
	<style>.a table td{padding-left: 20px;}</style>
		<% 
		//获取项目id
		String xm_id = request.getParameter("fzgtz");
		Bean xmbean=ServDao.find("TS_XMGL", xm_id);
		String xm_name = xmbean.getStr("XM_NAME");
		//获取项目设置id
		String where = "AND XM_SZ_NAME='报名' "+"AND XM_ID="+"'"+xm_id+"'";
		List<Bean> ksList = ServDao.finds("TS_XMGL_SZ", where);
		String xm_sz_id="";
		if(ksList!=null && ksList.size()>0){
		 xm_sz_id = ksList.get(0).getStr("XM_SZ_ID");
		}
		//获取报名管理id
		String where1 = "AND XM_SZ_ID="+"'"+xm_sz_id+"'";
		List<Bean> bmglList = ServDao.finds("TS_XMGL_BMGL", where1);
		String bm_id ="";
		String bm_ksxz ="";
		String bm_start ="";
		String bm_end ="";
		if(bmglList!=null && bmglList.size()>0){
		 bm_id = bmglList.get(0).getStr("BM_ID");
		 bm_ksxz = bmglList.get(0).getStr("BM_KSXZ");
		 bm_start = bmglList.get(0).getStr("BM_START");
		 bm_end = bmglList.get(0).getStr("BM_END");
		}
		//获取非资格列表
		String where2 = "AND BM_ID="+"'"+bm_id+"'";
		List<Bean> fzgbean =ServDao.finds("TS_XMGL_BM_FZGKS", where2);
		if(fzgbean!=null && fzgbean.size()>0){
		 fzgbean = ServDao.finds("TS_XMGL_BM_FZGKS", where2);
		}
		//获取用户编码
		String user_code = userBean.getStr("USER_CODE");
		//获取用户名称
		String user_name = userBean.getStr("USER_NAME");
		//获取用户性别
		String user_sex = userBean.getStr("USER_SEX");
		//获取用户部門
		String dept_name = userBean.getStr("DEPT_NAME");
		//获取用户机构
		String odept_name= userBean.getODeptName();
		//获取用户办公电话
		String user_office_phone = userBean.getStr("USER_OFFICE_PHONE");
		//获取用户手机号码
		String user_mobile = userBean.getStr("USER_MOBILE");
		//获取用户入行时间
		String user_cmpy_date =userBean.getStr("USER_CMPY_DATE");
	
		%>
		<div style="padding-left: 90px;width: 90%;text-align: left;">
				<img alt="中国工商银行" src="./qt/img/u3148.png"> <img alt="考试系统"src="./qt/img/u3376.png">
		</div>
	<div style="background: #dfdfdf;padding-top: 10px"align="center">
       	<div id="" style="background: white;width: 90%;">
       		<div style="background: white;width: 90%;text-align: center">
       		<table  style="height: 100px;width: 90%;">
       			<tr>
       				<td><span style="font-size: 25px;color: #00C2C2;"><%=xm_name %></span></td>
       			</tr>
       			<tr>
       				<td><span>报名编号：<%=xm_id%></span></td>
       			</tr>
       		</table>
       		</div>
       	<div class="a">
       		<table border="1" align="center" style="width: 90%;">
       			<tr style="background-color: #dfdfdf;">
       				<td colspan="4">个人基本信息</td>
       			</tr>
       			<tr>
       				<td width="25%">人力资源编码</td>
       				<td width="25%"><%=user_code %></td>
       				<td width="25%">姓名</td>
       				<td width="25%"><%=user_name%></td>
       			</tr>
       			<tr style="background-color: #f7fdff;">
       				<td >所属机构</td>
       				<td colspan="3"><%=odept_name %></td>
       			</tr>
       			<tr>
       				<td>性别</td>
       				<td>
       				<% if (user_sex == "1") { %>女<% } else { %>男<% } %>
       				</td>
       				<td>入行时间</td>
       				<td><%=user_cmpy_date %></td>
       			</tr>
       			<tr style="background-color: #f7fdff;">
       				<td >办公电话</td>
       				<td><%=user_office_phone %></td>
       				<td>手机号码</td>
       				<td><input type="text" value="<%=user_mobile %>" id="u_mobile"></td>
       			</tr>
       		</table>
       		<table border="1" align="center" style="width: 90%;">
       			<tr style="background-color: #dfdfdf;">
       				<td colspan="4">报考须知</td>
       			</tr>
       			<tr>
       				<td colspan="4" height="120px"><p style="font-size: 15px;color:red;">报考须知，请仔细阅读！</p>
       				<p style="color: red;"> 报名时间：<%=bm_start%>~~<%=bm_end %></p>
       				<p style="color: red;"> <%=bm_ksxz %></p></td>
       			</tr>
       		</table>
       		<table border="1" align="center" style="width: 90%;">
       			<tr style="background-color: #dfdfdf;">
       				<td align="left" colspan="3">考试项目</td>
       			</tr>
       			<tr>
       				<td style="text-align: center" width="10%"><input type="checkbox" id="checkall"></td>
       				<td width="55%" align="center">名称</td>
       				<td width="35" align="center" colspan="2">考试时间</td>
       			</tr>
       			<%
					for(int i=0;i<fzgbean.size();i++){
						Bean bean1 = fzgbean.get(i);
						String fzgks_id = bean1.getStr("FZGKS_ID");
						String name = bean1.getStr("FZGKS_NAME");
						String date1 = bean1.getStr("FZGKS_STADATE");
						String date2 = bean1.getStr("FZGKS_ENDDATE");
						String date3=date1+"~"+date2;
				%>
				<tr>
					<td style="text-align: center" width="10%"><input type="checkbox" name="checkname" value="<%=fzgks_id%>"></td>
					<td ><%=name%></td>
					<td style="text-align: center"><%= date3%></td>
				</tr>
				<%
					}
				%>
       		</table>
       		<div style="height: 100px;padding: 20px;">
       			<button onclick="tijiao()" class="btn btn-success" style="width:100px;background-color: #00c2c2;" data-toggle="modal" data-target="#tiJiao">提交</button>
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			<button  onclick="fanhui()" class="btn btn-success" style="width:100px;background-color: #00c2c2;">返回</button>	
       		</div>
       		
       	</div>
       	</div>
    </div>	
  
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE App -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
	<script type="text/javascript">
		function fanhui(){
			window.history.go(-1);
		}
	</script>
	<script>
		//全选，全不选
		$("#checkall").click( 
			  function(){ 
			    if(this.checked){ 
			        $("input[name='checkname']").prop('checked', true)
			    }else{ 
			        $("input[name='checkname']").prop('checked', false)
			    } 
			  } 
		);
		//非资格考试的提交数据
		function tijiao(){
			var bmArray = document.getElementsByName("checkname");
			var bmlb="";
		     	for(var i=0;i<bmArray.length;i++){
		     		if(bmArray[i].checked){
		     			if(i==0){
		     				bmlb = bmArray[i].value;
		     			}else{
		     		 		bmlb +="," + bmArray[i].value;
		     			}
		     		}
		     	}
		    if(bmlb=="" || bmlb==null){
		    	window.history.go(0);
		    	alert("至少选择一项");
		    }else{
		    var user_mobile = document.getElementById("u_mobile").value;
			var param={};
			param["bmCodes"] = bmlb;
			param["USER_CODE"]="<%=user_code%>";
			param["USER_NAME"] = "<%=user_name%>";
			param["USER_SEX"] = "<%=user_sex%>";
			param["ODEPT_NAME"] = "<%=odept_name%>";
			param["USER_OFFICE_PHONE"] = "<%=user_office_phone%>";
			param["USER_MOBILE"] = user_mobile;
			param["USER_CMPY_DATE"] = "<%=user_cmpy_date%>";
			param["XM_ID"] = "<%=xm_id%>";
			FireFly.doAct("TS_BMLB_BM", "addData", param,true,false);
			window.location.href="bm.jsp";
		    }
		}
	</script>
</body>
</html>
