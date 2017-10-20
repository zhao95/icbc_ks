<!DOCTYPE html>
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
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<!-- 获取后台数据 -->
<%@ include file="../../qt/jsp/header-logo.jsp"%> 
<%@ include file="/sy/base/view/inHeader-icbc.jsp"%>
<%@ page import="com.rh.core.serv.ServDao"%>
<%@ page import="com.rh.core.serv.ServMgr"%>
<%@ page import="com.rh.ts.pvlg.mgr.GroupMgr"%>
<%@ page import="com.rh.core.base.Bean"%>
<%@ page import="java.text.SimpleDateFormat"%>
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
<style>
	 .trcss{
		width:16%;
		text-align:right;
		vertical-align:bottom;
		font-size:14px;
		color:black;
	}
	.secss{
	font-size:14px;
	color:gray;
	width:34%;
	border-bottom:solid 1px lightgray;
	vertical-align:bottom;
	}
	
	#bminfo .modal-dialog { 
    position: absolute; 
    top: 50px; 
    bottom: 200px; 
    left: 0; 
    right: 0; 
    } 
</style>

	<div class="" style="padding: 10px">
		<a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img style="padding-bottom: 10px ;color: #388CAE;" src="<%=CONTEXT_PATH%>/ts/image/Home_16x16.png" id="shouye"></a>
		 <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;报名审核</span>
	</div>
	<div id="cuxian1"
		style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
		<span style="margin-left: 50px; padding-top: 10px">报名审核</span>
	</div>
	<table id="cxkuang" style="margin-top: 20px">
		<tbody>
			<tr style="height: 20px">
				<td id="mingcheng" style="width: 25%; text-align: right">名称&nbsp;&nbsp;<input
					style="height: 30px; width: 70%" id="mc" type="text"></input></td>
				<td style="width: 5%"></td>
				<td id="zuzhidanwei" style="width: 20%">组织单位&nbsp;&nbsp;<input
					style="height: 30px; width: 70%" id="zzdw" type="text"></input></td>
				<td style="width: 5%"></td>
				<td style="width: 15%"><select id="zhuangtai" onchange="ztcx()">
						<option selected="selected">全部</option>
						<option value="1">进行中</option>
						<option value="2">已结束</option>
				</select></td>
				<td style="width: 5%"></td>
				<td><button id="search"
						style="color: white; height: 30px; width: 25%; background: DarkTurquoise" class="btn"
						onclick="xzcu()">查询</button></td>
			</tr>
		</tbody>
	</table>

	<div
		style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
	<div id="table1" class=""
		style="margin-left: 10px; position: relative; width: 98%">
		<table class="rhGrid JPadding JColResizer" id="table">
			<thead id="tem" class="">
				<tr style="backGround-color: WhiteSmoke; height: 30px">
					<th id="BM_XUHAO" class="" style="width: 6.6%; text-align: center">序号</th>
					<th id="BM_NAME" class="" style="width: 20%; text-align: left">名称</th>
					<th id="BM_ODEPT__NAME" class=""
						style="width: 20%; text-align: left">组织单位</th>
					<th id="S_ATIME" class="" style="width: 20%; text-align: left">创建时间</th>
					<th id="BM_STATE__NAME" class="" style="width: 5%;">状态</th>
					<th id="BM_OPTIONS" class="" style="width: 20%; text-align: center">操作</th>
				</tr>
			</thead>
			<tbody class="">
				
			</tbody>
		</table>
		<div id="fenyediiv"
			style="position: absolute; right: 5%; bottom: -20;">
			<table class="row">
				<tr>
					<td><!-- 分页展示 -->
					<div class="rhGrid-page">
		            <span class="disabled ui-corner-4">上一页</span>
		            <span class="current ui-corner-4">1</span>
		            <span class="disabled ui-corner-4">下一页</span>
		            <span class="allNum">共15条</span>
        </div></td>
					<td style="width: 5%"></td>
					<td><select id="yema" onchange="fenyeselect()">
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
	<div class="modal fade" id="bminfo" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static" aria-hidden="true">
		<div class="modal-dialog" style="width:50%">
			<div class="modal-content">
				<div class="modal-header" style="background-color: #00c2c2;color: white">
						
					<h4 class="modal-title">
						报名详细信息
					</h4>
					
				</div>
				<div style="padding-left:10px;">
				<table id="xminfotable" style="width:95%;font-size:20px;color:lightseagreen">
				<tr height="50px">
				<td class="trcss" >报名编号：&nbsp;</td><td class="secss" id="bmcode"></td><td class="trcss">报名名称：&nbsp;</td><td class="secss" id="bmname" ></td>
				</tr>
				<tr height="50px">
				<td class="trcss">创建人：&nbsp;</td><td  class="secss" id="creator"></td><td class="trcss">组织单位：&nbsp;</td><td class="secss" id="oragnize"></td>
				</tr>
				<tr height="50px">
				<td class="trcss">报名开始时间：&nbsp;</td><td class="secss" id="starttime"></td><td class="trcss">报名结束时间：&nbsp;</td><td class="secss" id="endtime"></td>
				</tr>
				<tr height="50px">
				<td class="trcss">状态：&nbsp;</td><td class="secss" id="status"></td><td class="trcss"></td><td style="font-size:14px;color:red;text-align:left;width:40%" ></td>
				</tr>
				</table>
				</div>
				<div style="padding-top:25px;width:23%;font-size:16px;text-align:center;color:lightseagreen">
				描述：
				</div>
				<table style="width:95%">
				<tr>
				<td style="width:15%"></td><td style="disabled:disabled;height:100px;vertical-align:top"><textarea id="describe" style="border:solid 1px white;height:90%;width:90%" wrap="soft"></textarea></td>
				</tr>
				</table >
				<div style="padding-top:10px;width:23%;font-size:16px;text-align:center;color:lightseagreen">
				考试须知：
				</div>
				<table style="width:95%">
				<tr>
				<td style="width:15%"></td><td style="height:100px;vertical-align:top"><textarea id="mustknow" style="border:solid 1px white;height:90%;width:90%" wrap="soft"></textarea></td>
				</tr>
				</table>
				<div class="modal-footer" style="text-align:center;height:50px">
					<button type="button" class="btn btn-default" style="margin-top:-10px;color:white;background:lightseagreen;height:35px;width:80px" data-dismiss="modal">关闭</button>
				</div>
				
			</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
	<form id="form1" style="display: none" method="post"
		action="bmshzg.jsp">
		<input id="zgtz" name="zgtz"></input>
	</form>
	<form id="form2" style="display: none" method="post"
		action="bmshfzg.jsp">
		<input id="fzgtz" name="fzgtz"></input>
	</form>
	<form id="form3" style="display: none" method="post"
		action="belongto.jsp">
		<input id="zgtzzz" name="zgtz"></input>
		<input id="xianei" name="xianei"></input>
	</form>


	<input type="hidden" id="xmid" />
	<input type="hidden" id="dijige">
	<script type="text/javascript">
	var user_code = System.getVar("@USER_CODE@");
	var jq = $.noConflict(true);
	jq(function (){
		//页面加载完执行
		new listPage().gotoPage(1);
		var table = document.getElementById("table");  
		rowscolor(table);
	});
	function tiaozhuan(i){
		 var hid = "XM_ID"+i;
		 var xmtypeid = "XM_TYPE"+i;
			var id = document.getElementById(hid).innerHTML;
			document.getElementById("zgtz").value=id;
			document.getElementById("form1").submit();
	}
	function chakanbelong(i){
		 var hid = "XM_ID"+i;
		 var xmtypeid = "XM_TYPE"+i;
		 var xianei = "belong";
			var id = document.getElementById(hid).innerHTML;
			document.getElementById("zgtzzz").value=id;
			document.getElementById("xianei").value=xianei;
			document.getElementById("form3").submit();
	}
	function chakan(obj){
		var param={};
		 var hid = "XM_ID"+obj;
		var id = document.getElementById(hid).innerHTML;
		param["xmid"]=id;
		//通过项目查找报名信息
		var result1 = FireFly.doAct("TS_XMGL_BMGL","getBMState",param);
		var data1 = result1.list;
		var pageEntity1 = JSON.parse(data1);
		var state1 = pageEntity1[0].STATE;
		
		var result = FireFly.doAct("TS_XMGL_BMGL","getXmInfo",param);
		var data = result.list;
		var pageEntity = JSON.parse(data);
		$("#bmcode").text(pageEntity[0].BM_ID);
		$("#bmname").text(pageEntity[0].BM_NAME);
		$("#creator").text(pageEntity[0].BM_USER);
		$("#oragnize").text(pageEntity[0].BM_ODEPT);
		$("#starttime").text(pageEntity[0].BM_START);
		$("#endtime").text(pageEntity[0].BM_END);
		$("#mustknow").text(pageEntity[0].BM_KSXZ);
		$("#mustknow").attr("disabled","disabled");
		$("#status").text(state1);
		$("#describe").text(pageEntity[0].BM_DESC);
		$("#describe").attr("disabled","disabled");
	}

	
	function rowscolor(table){
		 var rows = table.getElementsByTagName("tr");  
		    for(i = 1; i < rows.length; i++){  
		        if(i % 2 == 0){  
		            rows[i].style.backgroundColor = "Azure";  
		       }  
		    } 
	}
	
	</script>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script>
	<script src="<%=CONTEXT_PATH%>/ts/js/fenye.js"></script>
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE  -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
</body>
</html>