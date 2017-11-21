<%@page import="com.rh.core.serv.OutBean"%>
<%@page import="javax.swing.text.StyledEditorKit.ForegroundAction"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>


<meta charset="utf-8">
 <meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>报名管理</title>
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<!-- 获取后台数据 -->
<%@ include file="/qt/jsp/header-logo.jsp"%>
<%@ include file="/sy/base/view/inHeader-icbc.jsp"%>
<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/font-awesome-4.7.0/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/ionicons/css/ionicons.min.css">

<script src="<%=CONTEXT_PATH%>/qt/js/html5shiv.min.js"></script>
<script src="<%=CONTEXT_PATH%>/qt/js/respond.min.js"></script>
<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>

<!-- Theme style -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/dist/css/AdminLTE.min.css">
<!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/dist/css/skins/_all-skins.min.css">
	 <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/bootstrap/js/site.css">
<!--工具方法-->
<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/global.js"></script>
<!--插件-->
<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/jquery.smart-form.js"></script>
<script src="<%=CONTEXT_PATH%>/ts/js/jquery.iframe-transport.js"></script>
<script src="<%=CONTEXT_PATH%>/ts/js/jquery.ui.widget.js"></script>
<script src="<%=CONTEXT_PATH%>/ts/js/jquery.fileupload.js"></script>
<body class="hold-transition skin-black sidebar-mini" id="bmbody">
<style>
#appeal .modal-dialog{
	position: absolute;
    top: 10%;
    bottom: 200px;
    left: 0;
    right: 0;
}

#bmbody .modal-backdrop {
    background: transparent;
}
#showSeatModal th{
    font-weight: bold;
}

#jkinfo .modal-dialog{
	position: absolute;
    top: 10%;
    bottom: 200px;
    left: 0;
    right: 0;
}
 #loadingdialog{
 font-family: Arial;
 border: 2px solid #379082;
 border-radius: 20px;
 padding: 30px 30px;
 width: 330px;
}
#loading .modal-dialog{
position: absolute;
    top: 20%;
    bottom: 200px;
    left: 20%;
    right: 0;
}

</style>
<!--判断用户是否登录  -->
<%
	String user_code = userBean.getStr("USER_CODE");

			//获取所有项目ID

							%>
	<div class="" style="padding: 10px">
		<a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img style="padding-bottom: 10px ;color: #388CAE;" src="<%=CONTEXT_PATH%>/ts/image/Home_16x16.png" id="shouye"></a>
		 <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;我的报名</span>
	</div>
	<table id="myTab" class="nav nav-tabs"
		style="margin-left: 10px; width: 98%; background-color: white">
		<tr style="height: 70px">
			<td id="keshenqingtd"  class="active"
				style="border-bottom:white solid 1px;width: 50%; text-align: center; font-size: 25px"><image
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u975.png" id="keshenimage">
				<a id="akeshen" href="#home" data-toggle="tab"><span id="keshen"
					style="color: lightseagreen">可申请的报名</span></a></td>
			<td id="yishenqingtd" class="" style="border-bottom:lightgray solid 1px;width: 50%; text-align: center; font-size: 25px"><image
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u984.png" id="yishenimage">
				<a id="ayishen" href="#tab2" data-toggle="tab"><span id="yishen"
					style="color: black">已申请的报名</span></a></td>
		</tr>


	</table>
	<div id="myTabContent" class="tab-content">
		<div class="tab-pane fade in active" id="home">
			<div
				style="margin-top: -5px; margin-left: 19%; height: 5px; width: 190px; background-color: LightSeaGreen"></div>
			<div id="cuxian1"
				style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">可申请的报名</span>
			</div>
			<div
				style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
			<div id="table1" class="" style="margin-left: 10px; width: 98%">
				<div class="content-main1">
					<table class="rhGrid JPadding JColResizer" id="table">
						<thead id="tem" class="">
							<tr style="backGround-color:WhiteSmoke; height: 30px">
								<th id="BM_XUHAO" class=""
									style="width: 6.6%; text-align: center">序号</th>
								<th id="BM_NAME" class="" style="width: 29.1%;">名称</th>
								<th   style="text-align: center;width: 15%;">组织单位</th>
								<th   style="text-align: center;width: 35%;">报名时间</th>
								 <th id="BM_STATE__NAME" class="" style="width: 5%;">状态</th>
								<th id="BM_OPTIONS" class=""
									style="width: 10%; text-align: center">操作</th>
							</tr>
						</thead>
						<tbody class="">

						</tbody>
					</table>

				</div>

		</div>
		</div>
		<div class="tab-pane fade"  id="tab2">
			<div
				style="margin-top: -6px; margin-left: 68%; height: 5px; width: 190px; background-color: LightSeaGreen"></div>
			<div id="cuxian2"
				style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">已申请的报名</span>
			</div>

                 <div  id="jibie" style="padding-top:20px;padding-left:80%">
                 <select id = "jb" onchange="jibieonchange()">
                 <option selected="selected">全部</option>
                 <option value="1">初级</option>
                 <option value="2">中级</option>
                 <option value="3">高级</option>
                 </select>
                 </div>

			<div id ="qihuan" style="margin-top:-40px">

              <div id="sanjiliandong" class="panel-body" style=":15px;width:800px;margin-left:200px">
                 <form action="#" id="formContainer" class="form form-horizontal"></form>

             </div>
			<div id="table2" class="" style="margin-top:-15px;margin-left: 10px; width: 98%;">
				<div class="content-main2" style="position:relative;">
					<table id="ybmtable" class="rhGrid JPadding JColResizer">
						<thead id="tem" class="">
							<tr style="backGround-color: WhiteSmoke; height: 30px">
							<th id="BM_XUHAO" class=""
									style="width: 6.6%; text-align: center">序号</th>
								<th style="width: 25%; text-align: center">名称</th>
								<th style="width: 5%; text-align: center">类型</th>
								<th style="text-align: center; width: 14%;">审核状态</th>
							 	<th style="width: 10%; text-align: center">状态</th>
								<th id="BM_OPTIONS" class=""
									style="width: 12%; text-align: center">操作</th>
							</tr>
						</thead>
						<tbody>

						</tbody>

					</table>

			<div id="fenyediiv" style="position:absolute;right:5%;padding-bottom:-20px;">
			<table class="row">
			<tr>
			<td><div class="rhGrid-page">
		            <span class="disabled ui-corner-4">上一页</span>
		            <span class="current ui-corner-4">1</span>
		            <span class="disabled ui-corner-4">下一页</span>
		            <span class="allNum">共15条</span>
		            </div>
		    </td>
		    <td style="width:5%"></td>
		    <td><select id = "yema" onchange="fenyeselect()">
                 <option value="10" selected="selected">10条/页</option>
                 <option value="1">1条/页</option>
                 <option value="20">20条/页</option>
                 <option value="50">50条/页</option>
                 <option value="100">100条/页</option>
                 </select></td>
			</tr>
			</table>
			</div>
				</div>
			</div>
			</div>
			<form id="form1" style="display:none" method="post" action="bmzgks.jsp">
				<input id = "zgtz" name="zgtz"></input>
				<input type="submit" name="Submit" value="提交">
			</form>
			<form id="form2" style="display:none" method="post" action="bmglf.jsp">
				<input id = "fzgtz" name="fzgtz"></input>
			<input type="submit" name="Submit" value="提交">
			</form>
		</div>
		<!--异议模态窗口  -->
	<div class="modal fade" id="appeal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static" aria-hidden="true">
		<div class="modal-dialog" style="width:40%">
			<div class="modal-content">
				<div class="modal-header" style="background-color: #00c2c2;color: white">

					<h4 class="modal-title">
						异议
					</h4>

				</div>
				<div style="padding-top:20px;width:90%;font-size:16px;text-align:left;color:lightseagreen">
				<span style="padding-left:10%">如确需报考，请说明并提交相关证明材料</span>
				</div>
				<div style="padding-top:20px">
				<table style="width:100%">
				<tr>
				<td style="width:10%"></td><td style="color:lightseagreen;font-size:13px;height:150px;vertical-align:top"><textarea id="liyou11" style="background-color:white;border:solid 1px lightseagreen;height:90%;width:90%" wrap="soft"></textarea></td>
				</tr>
				</table>
				<table id="filehis">

				</table>
				<div id="uploadfile" style="color:lightseagreen;font-size:20px"><form action="/file" name="formup" id="formContainer2" class="form form-horizontal"></form></div>
				</div>
				<div class="modal-footer" style="text-align:center;height:60px">
					<button id="tjbutt" type="button" onclick="tijiaoyiyi()" class="btn btn-primary" style="margin-top:-5px;height:30px;background:lightseagreen;width:80px">提交异议</button>
					<button type="button" onclick = "closemotai()" class="btn btn-primary" style="margin-top:-5px;background:lightseagreen;color:white;height:30px;width:80px;margin-left:20%;" data-dismiss="modal">取消</button>
				</div>

			</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
	<div class="modal fade" id="jkinfo" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static" aria-hidden="true">
		<div class="modal-dialog" style="width:30%">
			<div class="modal-content">
				<div class="modal-header" style="background-color: #00c2c2;color: white">
					<h4 class="modal-title">
					禁考提示
					</h4>
				</div>
				<div align="center">
				<textarea style="background-color:white;display:table-cell;vertical-align:middle;width:400px;height:80px" id = 'jkxxinfo'></textarea>
				</div>
				<div class="modal-footer" style="text-align:center;height:50px">
					<button type="button" onclick = "closemotai()" class="btn btn-default" style="margin-top:-6px;background:lightseagreen;color:white;height:30px;width:75px;" data-dismiss="modal">确定</button>
				</div>

			</div><!-- /.modal-content -->
		</div><!-- /.modal -->
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
				<div style="padding-top:4%;padding-bottom:6%;margin-left:27%;font-size:16px;">请输入您已入职满&nbsp;<input style="width:20%" id="yzinput"/>&nbsp;年</div>
				<div class="modal-footer" style="height:10%">
					<button type="button" class="btn btn-primary" onclick="yztj()">提交</button>

				</div>
			</div>
			<!-- /.modal-content -->
		</div>
	</div>
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


		<div class="modal" style="z-index: 999999;background: transparent;" id="showSeatModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
							&times;
						</button>
						<h5 class="modal-title">
							预安排座位
						</h5>
					</div>
					<div class="modal-body" style="padding: 24px;">
                        <div class="row">
                            <table class="table">
                                <tbody>
                                <tr class="active">
                                    <th scope="row">1</th>
                                    <td>Column content</td>
                                    <td>Column content</td>
                                    <td>Column content</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
					</div>
					<div class="modal-footer" style="text-align: center;">
						<button type="button" class="btn btn-success" onclick="" data-dismiss="modal"
                                style="width:100px;background-color: #00c2c2;">
							确定
						</button>
					</div>
				</div><!-- /.modal-content -->
			</div><!-- /.modal -->
		</div>

		<input type="hidden" id="xmid"/>
		<input type="hidden" id="dijige">
		<input type="hidden" id="user_code" value="<%=user_code %>">
	</div>

	<script src="<%=CONTEXT_PATH%>/ts/js/baoming.js"></script>
	
</body>
</html>
