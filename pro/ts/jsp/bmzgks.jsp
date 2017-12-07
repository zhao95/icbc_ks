<!DOCTYPE html>
<%@page import="com.rh.core.serv.ServDao"%>
<%@page import="com.rh.core.org.mgr.UserMgr"%>
<%@ include file="/qt/jsp/header-logo.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>资格报名列表</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
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
<style type="text/css">
#tabletjId{
	border:lightgray;
}
.a table tr {
	height: 40px;
	padding-left: 10px;
}
</style>
<style type="text/css">
.a table td {
	padding-left: 20px;
}
</style>
<style type="text/css">
.b table tr {
	height: 30px;
}
</style>
<style type="text/css">
.zgks table td {
	padding-left: 5px;
}
 #loadingdialog{
 font-family: Arial;
 border: 2px solid black;
 border-radius: 10px;
 padding: 30px 30px;
 width: 330px;
}
</style>
<!-- 遮罩层 -->
<style type="text/css">
.mask {
	position: absolute;
	top: 0px;
	filter: alpha(opacity = 60);
	background-color: #777;
	z-index: 1000;
	left: 0px;
	opacity: 0.5;
	-moz-opacity: 0.5;
}
#loading .modal-dialog{
position: absolute; 
    top: 20%; 
    bottom: 200px; 
    left: 20%; 
    right: 0; 
}
</style>
<body class="hold-transition skin-black sidebar-mini">
	<% 
		//获取项目id
		String xm_id = request.getParameter("zgtz");
		//获取用户编码
		String user_code = userBean.getStr("USER_CODE");
		//获取用户名称
		String user_name = userBean.getStr("USER_NAME");
		//获取用户性别
		String user_sex = userBean.getStr("USER_SEX");
		//获取用户机构
		String odept_name = userBean.getODeptName();
		//获取用户办公电话
		String user_office_phone = userBean.getStr("USER_OFFICE_PHONE");
		//获取用户手机号码
		String user_mobile = userBean.getStr("USER_MOBILE");
		//获取用户入行时间
		String user_cmpy_date =userBean.getStr("USER_CMPY_DATE");
		%>
		<!-- <div class="center-block" style="width: 313px;padding-top:50px;padding-bottom: 50px;">
                    <ul id="nav-ul-id" class="btn-group nav nav-tabs" data-toggle="buttons">
                        <li class="btn btn-default active" style="padding:8px 49px;" href="#view" aria-controls="profile" role="tab" data-toggle="tab">
                            图形显示
                        </li>
                        <li class="btn btn-default" style="padding:8px 49px;" href="#list" aria-controls="profile" role="tab" data-toggle="tab">
                            表格显示
                        </li>
                    </ul>
                </div> -->
	<div style="background: #dfdfdf; padding-top: 10px" align="center">
		<div id="" style="padding-bottom:50px;background: white; width: 90%;">
			<div style="background: white; width: 90%; text-align: center">
				<table style="height: 100px; width: 90%;">
					<tr>
						<td><span id='xmnamecon' style="font-size: 25px; color: #00C2C2;"></span></td>
					</tr>
				</table>
			</div>
			<div class="a">
				<table align="center" style="width: 90%;">
					<tr>
						<td colspan="4" height="120px"><strong
								style="font-size: 15px; color: #ff7721;">报考须知，请仔细阅读！</strong>
							<p id="bmddatecon" style="color: red;">
								</p>
							<p id="ksxzcon" style="color: #ff7721;"></p></td>
					</tr>
				</table>
				<table align="center" style="border-radius:5px;width: 90%; background-color: #fed1d1;">
					<tr>
						<td style="width:14%">温馨提示：</td>
						<td height="50px" align="left">1.您当前在 <span id="odptnspan"
							style="color: #ff0000;"><%=odept_name%></span>
							，将视您办公所在地统一安排考场。如果您发现下面的信息不符，请于借考申请开放期间提交借考申请！
						</td>
					</tr>
					<tr>
						<td></td>
						<td height="50px" align="left">2. 您在本年度跨序列已报名中级考试：<span id='allnum' style="color:red"></span>&nbsp;个（还可报名： <span id='cannum' style="color:red"></span>
							&nbsp;个）、高级：<span id='gaoji' style="color:red"></span>&nbsp;个（还可报名：<span id='canheighnum' style="color:red"></span>）&nbsp;个。如超过报名总数，应先取消已有报名，然后再提交新的报名。</td>
					</tr>
				</table>
				<div style="padding-top: 10px;">
					<table border="1" align="center" id="baseinfo"
						style="width: 90%; padding-top: 10px;">
						<tr style="background-color: #f2f2f2;">
							<td colspan="6" style="color:#666666"><B>个人基本信息</B></td>
						</tr>
						<tr>
							<td width="16.5%" style="color:#00C2C2">人力资源编码</td>
							<td width="16.5%" ><%=user_code %></td>
							<td width="16.5%" style="color:#00C2C2">姓名</td>
							<td width="16.5%" ><%=user_name%></td>
							<td width="16.5%" style="color:#00C2C2">性别</td>
							<td width="17.5%" >
								<% if (user_sex == "1") { %>女<% } else { %>男<% } %>
							</td>
						</tr>
						<tr style="background-color: #f7fdff;">
							<td style="color:#00C2C2">所属机构</td>
							<td colspan="5"><span id="deptspan"><%=odept_name %></span></td>
							<%-- <span id="selectdeptspan" style="display:none"><input id="radio1" style="vertical-align:text-bottom; margin-bottom:-3;" name="state" type="radio" value="1">主机构身份报名(<%=odept_name %>)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<input id="radio2" name="state" style="vertical-align:text-bottom; margin-bottom:-4;" type="radio" value="2">次机构身份报名&nbsp;&nbsp;&nbsp;&nbsp;<select style="margin-left:35%;margin-top:-2.2%;display:none" id="slaveselect"></select><span id="tsspan" style="fontWeight:bold;color:red">请选择您的所属机构</span></span> --%>
						</tr>
						<tr>
							<td width="16.5%" style="color:#00C2C2">岗位类别</td>
							<td width="16.5%" id='gwlb'></td>
							<td width="16.5%" style="color:#00C2C2">岗位序列</td>
							<td width="16.5%" id='gwxl'></td>
							<td width="16.5%" style="color:#00C2C2">职务层级</td>
							<td width="17.5%" id='zwcj'></td>
						</tr>
						<tr style="background-color: #f7fdff;">
							<td width="16.5%" style="color:#00C2C2">入行时间</td>
							<td width="16.5%"><%=user_cmpy_date%></td>
							<td width="16.5%" style="color:#00C2C2">办公电话</td>
							<td width="16.5%"><%=user_office_phone %></td>
							<td width="16.5%" style="color:#00C2C2">手机号码(<span style="color: red;">融e联绑定的手机号</span>)
							</td>
							<td width="17.5%"><input type="text" id="user_mobile1"
								value=""></td>
						</tr>
					</table>
				</div>
				<div style="padding-top: 10px;">
					<div style="padding-top: 10px; text-align: left; width: 90%;">
						<span style="color: #ff0000">★ 应报名的考试</span> <span
							style="color: #fdb64f;">(提示：如果应考序列包含模块，请在下方选择具体的模块)</span>
					</div>
					<table border="1" align="center" style="width: 90%;" id="tableid">
					<thead>
						<tr style="background-color: #ffbdbd;">
							<td width="3%"></td>
							<td width="10%">岗位类别</td>
							<td width="15%">序列</td>
							<td width="27%">模块</td>
							<td width="10%">级别</td>
							<td width="20%">验证</td>
							<td width="15%">验证结果</td>
						</tr>
						</thead>
						<tbody>
						
       						</tbody>
					</table>
				</div>
				<div style="width: 90%; 60 px; padding-top: 20px; text-align: left;">
					<span style="color: #0782cb;">★ 跨序列报名的考试</span> <span
						style="color: #fdb64f;">(提示：只允许选择两个跨序列的考试)</span>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<button class="btn btn-success" id='selectks' 
						style="width: 100px; background-color: #00c2c2;"
						data-toggle="modal" data-target="#myModal">选择考试</button>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<!-- 	       					<button class="btn btn-success" style="width:100px;background-color: #00c2c2;" data-toggle="modal" data-target="#deletec">删除</button> -->
				</div>
				<div style="padding-top: 5px;">
					<table border="1" style="bordercolor:lightgray;width: 90%" id="tablehang">
						<tr style="background-color: #d9eeeb;">
							<td width="3%"><input onclick="change2(this)" style="margin-right:12px" type="checkbox"></td>
							<td width="10%">岗位类别</td>
							<td width="15%">序列</td>
							<td width="27%">模块</td>
							<td width="10%">级别</td>
							<td width="20%">验证</td>
							<td width="15%">验证结果</td>
						</tr>
						<tbody id="goods">

						</tbody>
					</table>
				</div>
				<div style="height: 100px; padding: 20px;padding-top:120px;">
					<button id="zgyzbt"  class="btn btn-success"  onclick ="yanzheng()" data-toggle="modal" data-target=""
						style="font-size:16px;width: 150px;height:50px; background-color: #00c2c2;">1.资格验证</button>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<button id="tjbt" class="btn btn-success"
						style="font-size:16px;width: 150px;height:50px; background-color: #00c2c2;"
						data-toggle="modal" data-target="#tiJiao" onclick="tijiao()">2.提交报名</button>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<button onclick="goBack()" class="btn btn-success"
						style="font-size:16px;width: 150px;height:50px; background-color: #00c2c2;">返回</button>
				</div>
			</div>
		</div>
	</div>
	<!-- 模态选择框跨序列（Modal） -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" data-backdrop="static" aria-hidden="true">
		<div class="modal-dialog" style="width:700px;">
			<div class="modal-content">
				<div class="modal-header"
					style="background-color: #00c2c2; color: white">
					
					<h5 class="modal-title">可选择的考试</h5>
				</div>
				<div class="modal-body zgks">
					<div class="row" style="height: 450px;">
						<div class="col-sm-4">
							<div class="content-navTreeCont">
								<div class="content-navTree" style="height: 450px;"></div>
							</div>

						</div>
						<div class="col-sm-8" style="overflow-y: auto;height: 450px;">
							<table border="1" style="border-color:lightgray;width: 100%;" id="tabletjId">
								<thead>
									<tr style="background-color: #d9eeeb; padding-left: 5px;height:30px;">
										<th width="10%"></th>
										<th width="15%">岗位类别</th>
										<th width="15%">序列</th>
										<th width="45%">模块</th>
										<th width="15%">级别</th>
									</tr>
								</thead>
								<tbody id="ksxxId">
								</tbody>
							</table>
						</div>
					</div>
				</div>
				<div class="modal-footer" style="text-align: center;">
					<button type="button" class="btn btn-success" data-dismiss="modal"
						style="width: 100px; background-color: #00c2c2;" onclick="fuzhi()">确定</button>
					<button type="button" class="btn btn-default" data-dismiss="modal"
						style="color:white;width: 100px;margin-left:100px;background-color: #00c2c2;">返回</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal -->
	</div>
	<!-- 模态提交框（Modal） -->
	<div class="modal fade" id="tiJiao" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" data-backdrop="static" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div id="mask" class="mask"></div>
				<div class="modal-header"
					style="background-color: #00c2c2; color: white">
					
					<h6 class="modal-title">报名信息</h6>
				</div>
				<div class="b">
					<table id="motaitable" style="width: 100%; height: 50%">
						<tbody id="xinxi" style="height: 10px">
							<tr style="font-size: 15px">
								<td style="width: 30%; color: lightseagreen;" align="right">姓名</td>
								<td colspan="4" style="width: 70%; text-align: center"><%=user_name%></td>
							<tr id="tr2" style="height: 10px; font-size: 15px">
								<td style="width: 30%; color: lightseagreen; text-align: right;">人力资源编码</td>
								<td colspan="4" style="width: 70%; text-align: center;"><%=user_code %></td>
							</tr>

						</tbody>
					</table>
					<table>
						<tr style="height: 10px; font-size: 15px">
							<td style="width: 35%; color: red;display:none" align="right">融e联绑定的手机号</td>
							<td style="width: 3%"></td>
							<td style="width: 20%; text-align: center;display:none"><input
								type="text" id="user_mobile2" style="width: 100px; height: 30px"
								id="user_mobile2"></td>
							<td style="width: 3%"></td>
							<td style="color: lightseagreen;;display:none">验证码：<input type="text"
								style="width: 50px; height: 30px"> <input
								style="height: 30px" type="button" value="获取验证码">
							</td>
						</tr>

					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" onclick="mttijiao()">提交</button>
					<button type="button" class="btn btn-default" data-dismiss="modal"
						onclick="quxiao()">取消</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		</div>
		<!-- /.modal -->
		<!-- 模态选择框跨序列（Modal） -->
	<div class="modal fade" id="loading" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" data-backdrop="static" aria-hidden="true">
		<div  class="modal-dialog" style="width:700px;">
			<div id="loadingdialog" class="modal-content" style="position:absoluate;background:white;height:100px;width:50%">
				<div style="color:lightseagreen;padding-left:30%;padding-top:10px;font-size:16px;">&nbsp;&nbsp;<B>正在验证请稍候.....</B></div>
				<div id="loadiv" style="position:relative;top:-80%;left:15%;font-size:16px;"><img src="<%=CONTEXT_PATH%>/ts/image/loading.gif"></img></div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal -->
	</div>
	
		<div class="modal fade" id="yzxx" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" data-backdrop="static" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div id="mask" class="mask"></div>
				<div class="modal-header"
					style="background-color: #00c2c2; color: white">
					
					<h6 class="modal-title">信息验证</h6>
				</div>
					<div style="padding-left:5%;padding-right:5%;font-size:16px;color:red" id="tishiyu"></div>
				<div style="padding-top:2%;padding-bottom:6%;margin-left:27%;font-size:16px;">请输入您已入职满&nbsp;<input style="width:20%" id="yzinput"/>&nbsp;年</div>
				<div class="modal-footer" style="height:10%">
					<button type="button" class="btn btn-primary" onclick="yztj()">提交</button>

					
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		</div>
		<div class="modal fade" id="littleyzxx" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" data-backdrop="static" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div id="mask" class="mask"></div>
				<div class="modal-header"
					style="background-color: #00c2c2; color: white">
					
					<h6 class="modal-title">规则详细验证信息</h6>
				</div>
				<div class="modal-content" style="padding-top:20px;padding-bottom:20px">
					<div id="littlediv"></div>
					</div>
					<div class="modal-footer" style="text-align:center;height:50px">
					<button type="button" class="btn btn-default" style="border-color:#245580;color:white;background:lightseagreen;font-size:13px;height:35px;width:80px;margin-top:-8px" data-dismiss="modal">关闭</button>
				</div>
					
				</div>
			<!-- /.modal-content -->
		</div>
		</div>
	<div id = 'chufa'></div>
	<form id="form1" style="display:none" method="post" action="bm.jsp">
				<input id = "bmbq" name="bmbq"></input><!--报名标签，是否选中第二个标签  -->
				<input type="submit" name="Submit" value="提交">
			</form>
	<input type="hidden" id='xmidval' value="<%=xm_id %>">
	<script>
	
	/* function checkLeave(){
		//判断是否有考试没有提交
		var  leng = $("input[name='checkboxaa']").length;
		if(leng>0){
			var sureconfir = confirm("还有考试没有提交是否要离开页面");
			if(sureconfir){
				//确定关闭页面
			}else{
				 return '您可能有数据没有保存'; 
			}
		}
	} */
	</script>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<script src="<%=CONTEXT_PATH%>/sy/base/frame/coms/tree/jquery.tree.js"></script>
	<script src="<%=CONTEXT_PATH%>/ts/js/bmzgks.js"></script>
</body>
</html>
