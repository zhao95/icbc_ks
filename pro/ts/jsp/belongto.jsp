<!DOCTYPE html>
<%@page import="java.sql.Array"%>
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
<title>报名审核</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<!-- 获取后台数据 -->
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

<%
String username = "";
String loginname = "";
if(userBean != null) {
				 username=userBean.getStr("USER_NAME");
				 loginname=userBean.getStr("USER_LOGIN_NAME");
			 }
			 String xmid = request.getParameter("zgtz");
			 %>
<style>

	tr{cursor: pointer;}
	#yiyi .modal-dialog{
	position: absolute; 
    top: 20%; 
    bottom: 200px; 
    left: 20%; 
    right: 0; 
	}
	#excleupload .modal-dialog{
	position: absolute; 
    top: 20%; 
    bottom: 200px; 
    left: 20%; 
    right: 0; 
	}
	#excleupload .modal-footer{
	position: absolute; 
    top: 78%; 
    bottom: 200px; 
    left: 0; 
    right: 0; 
	}
	
	#paixu .modal-dialog { 
    position: absolute; 
    top: 50px; 
    bottom: 200px; 
    left: 0; 
    right: 0; 
    } 
    #userbminfo .modal-dialog { 
    position: absolute; 
    top: 50px; 
    bottom: 200px; 
    left: 0; 
    right: 0; 
    } 
      #dshtable{
            margin: 100px auto;
            position: relative;
      }
      table{
            border-collapse: collapse;
            border-spacing: 0;
            table-layout: fixed;
      }
   
     #box{position:absolute;display:none;opacity:0.9;background:#fff;text-align: center;top:0;}
    </style>

	<div class="" style="padding: 10px">
		<a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img style="padding-bottom: 10px ;color: #388CAE;" src="<%=CONTEXT_PATH%>/ts/image/Home_16x16.png" id="shouye"></a>
		 <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;辖内报名情况</span>
	</div>
	
	<span style="padding-left:5%;">当前辖内报名总人数：</span><strong id="allnum" style="color:red"></strong>、待审核：<strong id="staynum" style="color:red"></strong>、审核通过：<strong id="passnum" style="color:red"></strong>、审核未通过：<strong id="nopassnum" style="color:red"></strong>
	<table id="myTab" class="nav nav-tabs"
		style="margin-left: 10px; width: 98%; background-color: white">
		<tr style="height: 70px">
			<td id="dshtd"  class="active"
				style="border-bottom:white solid 1px;width: 33.3%; text-align: center; font-size: 25px"><image
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u1677.png" id="dshimage">
				<a id="dsha" onclick="dsha()" href="#home" data-toggle="tab"><span id="dshsp"
					style="color: lightseagreen">待审核人员</span>
				</a>
			</td>
			<td id="shtgtd" class="" style="border-bottom:lightgray solid 1px;width: 33.3%; text-align: center; font-size: 25px"><image
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u1681.png" id="shtgimage">
				<a id="shtga" onclick="shtga()" href="#tab2" data-toggle="tab"><span id="shtgsp"
					style="color: black">审核通过人员</span>
				</a>
			</td>
			<td id="shwtgtd" class="" style="border-bottom:lightgray solid 1px;width: 33.3%; text-align: center; font-size: 25px"><image
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u1695.png" id="shwtgimage">
				<a id="shwtda" onclick="shwtda()" href="#tab3" data-toggle="tab"><span id="shwtgsp"
					style="color: black">审核未通过人员</span>
				</a>
			</td>
		</tr>

	</table>
	<div id="myTabContent" class="tab-content">
		<div class="tab-pane fade in active" style="position:relative;" id="home">
			<div
				style="margin-top: -5px; margin-left: 7%; height: 5px; width: 20%; background-color: LightSeaGreen"></div>
				<div id="cuxian1"
				style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">待审核人员</span>
			</div>
			<table id="cxkuang" style="margin-top:20px">
				<tbody>
					<tr style="height:20px">
						<td style="width:18%;text-align:right">姓名&nbsp;&nbsp;<input style="height:30px;width:50%" id="xm1" type="text"></input></td>
						<td style="width:18%;text-align:right">审核级数&nbsp;&nbsp;<input style="height:30px;width:50%" id="shjs" type="text"></input></td>
						<td style="width:20%;text-align:center">人力资源编码&nbsp;&nbsp;<input  style="height:30px;width:50%" id="rlzybm1" type="text"></input></td>
						<td style="width:15%;text-align:center">部门&nbsp;&nbsp;<input  style="height:30px;width:50%" id="bumende" type="text"></input></td>
				        
				        <td style="width:12%;text-align:left;"><button id="check1" class ="btn" style="color:white;height:30px;width:35%;background:DarkTurquoise" onclick="xzcu(1)"id = "chaxun">查询</button></td>
					</tr>
				</tbody>
			</table>
			<div id="xixian"
				style="margin-left: 10px; margin-top: 10px; background-color: lightgray; height:1px; width: 98%">
			</div>
			<!--按钮  -->
			<table style="margin-top:10px;width:50%">
			<tr style="width:98%">
				<td style="width:20%;text-align:right"><button  class="btn btn-success"  data-toggle="modal" data-target="#paixu" style="border:none;color:white;height:30px;width:80%;background:lightseagreen" onclick="zdyl()" id = "zdyl" >自定义显示列</button></td>
				<td style="width:20%;text-align:right"><button class="btn btn-success" data-toggle="modal" data-target="#excleupload" onclick="importdata(1)"  style="border:none;color:white;height:30px;width:80%;background:lightseagreen"  id = "import">文件批量导入</button></td>
				<td style="width:20%;text-align:right"><button class="btn btn-success" style="border:none;color:white;height:30px;width:80%;background:lightseagreen" onclick="exportdata('TS_BMSH_STAY','checkboxa')" id = "export">文件批量导出</button></td>
				<td style="width:20%;text-align:center"><button class="btn btn-success" style="border:none;color:white;height:30px;width:40%;background:lightseagreen" onclick="fanhui()" id = "fanhui">返回</button></td>
			</tr>
			</table>
			<!--所有数据  -->
			<div id="dshtable" class="" style="margin-top:20px;margin-left: 10px; width: 98%">
					<table class="rhGrid JPadding JColResizer" id="staytable">
					<thead>
							<tr style="backGround-color:WhiteSmoke; height: 30px">
							<th style="width: 2%; text-align: center"><input type="checkbox" name="checkbox1" value="checkboxaa" onchange="change(this)"></th>
								<th style="width: 3%; text-align: center">序号</th>
								<th style="width: 8%; text-align: center">操作</th>
								<!--从 TS_BMSH_PX中查找 当前审核人的 排序字段记录 没有的话 默认为空 循环遍历 list输出th  -->
							</tr>
					</thead>
					<tbody>
					
					</tbody>
					</table>
					<div id="box"> </div>
				</div>
		<div id="fenyediv1" style="position:absolute;right:5%;bottom:-20;">
			<table class="row">
			<tr>
				<td><!-- 分页展示 -->
					<div class="rhGrid-page">
		            <span class="disabled ui-corner-4">上一页</span>
		            <span class="current ui-corner-4">1</span>
		            <span class="disabled ui-corner-4">下一页</span>
		            <span class="allNum">共15条</span>
		            </div>
			    </td>
			    <td style="width:5%"></td>
			    <td><select id = "select1" onchange="fenyeselect(1)">
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
		<div class="tab-pane fade"  id="tab2">
		    <div
				style="margin-top: -5px; margin-left: 40%; height: 5px; width: 20%; background-color: LightSeaGreen"></div>
				<div id="cuxian2"
				style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">审核通过人员</span>
			</div>
			<div id="xixian"
				style="margin-left: 10px; margin-top: 10px; background-color: lightgray; height:1px; width: 98%">
			</div>
			<table style="margin-top:10px">
			<tr>
				<td style="width:10%;text-align:right"><button id="TS_BMSH_PASS" class="btn btn-success" data-toggle="modal" data-target="#excleupload" onclick="importdata(2)"  style="border:none;color:white;height:30px;width:80%;background:lightseagreen"  id = "import2">文件批量导入</button></td>
				<td style="width:12%;text-align:right"><button class="btn btn-success" style="border:none;color:white;height:30px;width:70%;background:lightseagreen" onclick="exportdata('TS_BMSH_PASS','checkboxb')" id = "export2">文件批量导出</button></td>
				<td style="width:10%;text-align:right"><button class="btn btn-success" style="border:none;color:white;height:30px;width:50%;background:lightseagreen" onclick="fanhui()" id = "fanhui">返回</button></td>
				<td style="width:10%;"></td>
				<td style="width:12%;text-align:right">姓名&nbsp;&nbsp;<input style="height:30px;width:70%" id="xm2" type="text"></input></td>
						<td style="width:15;text-align:center">人力资源编码&nbsp;&nbsp;<input style="height:30px;width:40%" id="rlzybm2" type="text"></input></td>
						<td style="width:15%;text-align:center">部门&nbsp;&nbsp;<input  style="height:30px;width:50%" id="bumende" type="text"></input></td>
						<td style="width:1%;text-align:left"><select id = "zhuangtai1">
				            <option selected="selected">全部</option>
				            <option value="进行中">进行中</option>
				            <option value="已结束">已结束</option>
				            </select>
				        </td>
				        <td style="width:8%;text-align:left"><button id="check2" class ="btn" style="border:none;color:white;height:30px;width:60%;background:DarkTurquoise" onclick="xzcu(2)"id = "chaxun">查询</button></td>
			</tr>
			</table>
			<div id="dshtable" class="" style="margin-top:20px;margin-left: 10px; width: 98%">
					<table class="rhGrid JPadding JColResizer" id="passtable">
						<thead id="tem" class="">
							<tr id="firsttr" style="backGround-color:WhiteSmoke; height: 30px">
							<th style="width: 5%; text-align: center"><input type="checkbox" name="checkbox2" value="checkboxbb" onchange="changeb(this)"></th>
								<th style="width: 5%; text-align: center">序号</th>
								<th style="width: 5%; text-align: center">操作</th>
							
							</tr>
						</thead>
						
					<tbody>
					
					<tr></tr>
					</tbody>
					</table>
		
				</div>
		<div id="fenyediv2" style="position:absolute;right:5%;bottom:-20;">
			<table class="row">
			<tr>
				<td><!-- 分页展示 -->
					<div class="rhGrid-page">
		            <span class="disabled ui-corner-4">上一页</span>
		            <span class="current ui-corner-4">1</span>
		            <span class="disabled ui-corner-4">下一页</span>
		            <span class="allNum">共15条</span>
		            </div>
			    </td>
			    <td style="width:5%"></td>
			    <td><select id = "select2" onchange="fenyeselect(2)">
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
		<div class="tab-pane fade"  id="tab3">
		<div
				style="margin-top: -5px; margin-left: 73%; height: 5px; width: 20%; background-color: LightSeaGreen"></div>
				<div id="cuxian3"
				style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">审核不通过人员</span>
			</div>
			<div id="xixian"
				style="margin-left: 10px; margin-top: 10px; background-color: lightgray; height:1px; width: 98%">
			</div>
			<table style="margin-top:10px">
			<tr>
				<td style="width:10%;text-align:right"><button id="TS_BMSH_NOPASS" class="btn btn-success" data-toggle="modal" data-target="#excleupload" onclick="importdata(3)"  style="border:none;color:white;height:30px;width:80%;background:lightseagreen" id = "import3">文件批量导入</button></td>
				<td style="width:12%;text-align:right"><button class="btn btn-success" style="border:none;color:white;height:30px;width:70%;background:lightseagreen" onclick="exportdata('TS_BMSH_NOPASS','checkboxc')" id = "export3">文件批量导出</button></td>
				<td style="width:10%;text-align:right"><button class="btn btn-success" style="border:none;color:white;height:30px;width:50%;background:lightseagreen" onclick="fanhui()" id = "fanhui">返回</button></td>
						<td style="width:10%"></td>
				<td style="width:12%;text-align:right">姓名&nbsp;&nbsp;<input style="height:30px;width:70%" id="xm3" type="text"></input></td>
						<td style="width:15;text-align:center">人力资源编码&nbsp;&nbsp;<input style="height:30px;width:40%" id="rlzybm3" type="text"></input></td>
						<td style="width:15%;text-align:center">部门&nbsp;&nbsp;<input  style="height:30px;width:50%" id="bumende" type="text"></input></td>
						<td style="width:1%;text-align:left"><select id = "zhuangtai2">
				            <option selected="selected">全部</option>
				            <option value="进行中">进行中</option>
				            <option value="已结束">已结束</option>
				            </select>
				        </td>
				        <td style="width:8%;text-align:left"><button id="check3" class="btn" style="border:none;color:white;height:30px;width:60%;background:DarkTurquoise" onclick="xzcu(3)"id = "chaxun">查询</button></td>
			</tr>
			</table>
			<div id="dshtable" class="" style="margin-top:20px;margin-left: 10px; width: 98%">
					<table class="rhGrid JPadding JColResizer" id="nopasstable">
						<thead id="tem" class="">
							<tr style="backGround-color:WhiteSmoke; height: 30px">
							<th style="width: 5%; text-align: center"><input type="checkbox" name="checkbox3" value="checkboxcc" onchange="changec(this)"></th>
								<th 
									style="width: 5%; text-align: center">序号</th>
								<th style="width: 5%; text-align: center">操作</th>
							
							</tr>
						</thead>
						
					<tbody>
					
					</tbody>
					</table>
				</div>
		<div id="fenyediv3" style="position:absolute;right:5%;bottom:-20;">
			<table class="row">
			<tr>
				<td>	<!-- 分页展示 -->
					<div class="rhGrid-page">
		            <span class="disabled ui-corner-4">上一页</span>
		            <span class="current ui-corner-4">1</span>
		            <span class="disabled ui-corner-4">下一页</span>
		            <span class="allNum">共15条</span>
		            </div>
			    </td>
			    <td style="width:5%"></td>
			    <td><select id = "select3" onchange="fenyeselect(3)">
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
			<div class="modal fade" id="tiJiao" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  aria-hidden="true" style="padding-top:5%">
		<div class="modal-dialog" style="width:50%">
			<div class="modal-content">
				<div class="modal-header" style="line-height:20px;font-size:16px;height:50px;background-color: #00c2c2;color: white">
						批量审核
				</div>
				<form id="formmotai" method="post" action="bmshzg.jsp">
				<div>
				<table style="height:125px;font-size:14px;">
				<tr style="height:25%">
				<td style="text-align:right;width:20%">审核人姓名</td><td style="width:5%"></td><td><input style="height:30px" type="text" value="<%=username%>" name="shren"/></td>
				<td style="width:3%"></td>
				<td style="text-align:right">审核人登录名</td><td style="width:5%"></td><td><input style="height:30px" type="text" value="<%=loginname %>" name="shdlming"/></td>
				</tr>
				<tr style="height:25%">
				<td style="text-align:right">审核状态</td><td style="width:5%"></td><td><label><input style="vertical-align:text-bottom; margin-bottom:-3;" name="state" type="radio" value="1" checked>审核通过</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<label><input name="state" style="vertical-align:text-bottom; margin-bottom:-4;" type="radio" value="2">审核不通过</label>
				</td>
				</tr>
				</table>
				<table  style="height:100px;width:700px">
				<tr>
				<td style="text-align:right;width:17.5%;vertical-align:top">审核理由</td><td style="width:4%"></td><td style="width:75%;vertical-align:top"><textarea id="liyou" style="border:solid 1px lightseagreen;height:90%;width:90%" wrap="soft"></textarea></td>
				</tr>
				</table>
				</div>
				<input type="hidden" id="mokuai"></input>
				</form>
				<div class="modal-footer" style="text-align:center;height:60px">
					<button type="button" class="btn btn-primary" style="height:35px;background:lightseagreen;width:80px" onclick="mttijiao()">审核</button>
					<button type="button" class="btn btn-default" style="background:lightseagreen;margin-left:100px;color:white;height:35px;width:80px" data-dismiss="modal">关闭
					</button>
				</div>
			</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
	
	<div class="modal fade" id="paixu" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  aria-hidden="true">
		<div class="modal-dialog" style="width:50%;">
			<div class="modal-content" style="height:650px">
				<div class="modal-header" style="line-height:20px;font-size:16px;height:50px;background-color: #00c2c2;color: white">
						
					自定义显示列
					
				</div>
				<div >
				<table style="width:360px;">
				    <thead style="background-color:lightgray;border-bottom:solid 1px lightgray">
				      <tr style="height:35px;">
				        <th style="padding-left:20px;width:40%;text-align:left"><input type="checkbox" style="font-size:13px;width:16px;height:16px" id="daixuan" onclick="checkall(this)">待选</th>
				      </tr>
				      </thead>
				      </table>
				</div>
				<div id ="pxtablediv" style="height:500px;overflow-y:auto;width:360px">
				<table id="pxtable" style="width:340px;">
				      <tbody>
				      </tbody>
				    </table>
				    </div>
				    <div style="position:relative;left:370px;top:-400px">
				   <a onclick="removeleft()" href="#"><image id="imageleft" src="/ts/image/1124.png"></image></a>
				    </div>
				    <div style="position:relative;left:370px;top:-370px">
				    <a onclick="removeright()" href="#"><image id="imageright" src="/ts/image/1348.png"></image> </a>
				    </div>
				     <div style="position:relative;left:370px;top:-350px">
				   <a id="leftalla" onclick="leftall()" href="#"><image id="leftall" src="/ts/image/1440.png"></image></a>
				    </div>
				     <div style="position:relative;left:370px;top:-330px">
				   <a id="rightalla" onclick="rightall()" href="#"><image id="rightall" src="/ts/image/1552.png"></image></a>
				    </div>
				    <div style="width:330px;position:relative;left:420px;top:-666px">
				     <table style="width:339px;">
				     <thead style="background-color:lightgray;width:200px;border-bottom:solid 1px lightgray">
				      <tr style="height:35px;">
				        <th style="padding-left:20px;text-align:left"><input type="checkbox" style="width:16px;height:16px" id="daixuanrt" onclick="checkallright(this)">已选&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
				      </tr>
				      </thead>
				      </table>
				    </div>
				      <div id="pxtable2div" style="height:500px;overflow-y:auto;width:338px;position:relative;left:420px;top:-664px">
				   <table id="pxtable2" style="width:310px;">
				      <tbody>
				    
				      </tbody>
				    </table>
				    </div>
				<div  class="modal-footer" style="position:relative;top:-660px;text-align:center;height:32px">
					<button type="button" class="btn btn-primary" style="font-size:16px;height:35px;background:lightseagreen;width:80px" onclick="savePX()">保存</button>
					<button type="button" class="btn btn-default" style="color:white;background:lightseagreen;font-size:16px;margin-left:100px;height:35px;width:80px" data-dismiss="modal">关闭
					</button>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="yiyi" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  aria-hidden="true">
		<div class="modal-dialog" style="width:50%">
			<div class="modal-content">
				<div class="modal-header" style="line-height:20px;font-size:16px;height:50px;background-color: #00c2c2;color: white">
						
						异议
				</div>
				<div style="padding-top:20px;width:300px;font-size:20px;text-align:center;color:lightseagreen">
				申诉理由：
				</div>
				<div style="padding-top:20px">
				<table style="width:700px">
				<tr>
				<td style="width:15%"></td><td style="height:150px;vertical-align:top"><textarea id="backliyou" style="border:solid 1px white;height:90%;width:90%" wrap="soft"></textarea></td>
				</tr>
				</table>
				<div style="padding-top:20px;width:300px;font-size:20px;text-align:center;color:lightseagreen">
				申诉详细材料：
				</div>
				<table id="filehistory">
				
				</table>
				
				</div>
				<div class="modal-footer" style="text-align:center;height:100px">
					<button type="button" class="btn btn-default" style="color:white;background:lightseagreen;font-size:16px;height:50px;width:100px" data-dismiss="modal">取消</button>
				</div>
				
			</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
	<div class="modal fade" id="excleupload" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  aria-hidden="true">
		<div class="modal-dialog" style="width:50%">
			<div class="modal-content"  style="width:400px;height:280px">
				<div class="modal-header" style="line-height:20px;font-size:16px;height:50px;background-color: #00c2c2;color: white">
						请选择文件
				</div>
				<div style="position:absolute;padding-left:30px;padding-top:30px;color:gray">请导入要上传的  Excel</div>
				<div id="uploadfile"  style="width:200px;position:relative;top:60px;color:lightseagreen;font-size:20px"><form action="/file" name="formup" id="excleupload11" class="form form-horizontal" style="width:200%"></form></div>
				<div class="modal-footer" style="text-align:center;width:400px;">
					<button id="excelimp" type="button" class="btn btn-primary" style="height:35px;background:lightseagreen;width:80px">导入</button>
					<button type="button" onclick = "closemot()" class="btn btn-default" style="color:white;background:lightseagreen;margin-left:100px;height:35px;width:80px;font-size:16px;" data-dismiss="modal">取消</button>
				</div>
			</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
	<!--报名详细信息  -->
	<div class="modal fade" id="userbminfo" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"  aria-hidden="true">
		<div class="modal-dialog" style="width:50%">
			<div class="modal-content">
				<div class="modal-header" style="line-height:20px;font-size:16px;height:50px;background-color: #00c2c2;color: white">
						
						报名详细信息
				</div>
				<div style="padding-left:30px;padding-top:20px;">
				<table style="width:650px;font-size:15px;color:black">
				<thead>
				<tr>
				<td style="text-align:right;font-size:20px;vertical-align:bottom">考试标题：&nbsp;</td><td style="font-size:18px;color:gray;width:40%" id="ks_title"></td>
				</tr>
				</thead>
				<tr height="40px">
				<td style="color:gray;width:20%;text-align:right;vertical-align:bottom">报名人：&nbsp;</td><td style="vertical-align:bottom;font-size:14px;height:20px;color:gray;width:15%;border-bottom:solid 1px lightgray;" id="bm_name"></td><td style="color:gray;vertical-align:bottom;width:20%;text-align:right;">人力编码：&nbsp;</td><td style="border-bottom:solid 1px lightgray;vertical-align:bottom;font-size:14px;color:gray;text-align:left;width:50%" id="work_num"></td>
				</tr>
				<tr height="50px">
				<td style="color:gray;width:20%;text-align:right;vertical-align:bottom">性别：&nbsp;</td><td style="vertical-align:bottom;font-size:14px;color:gray;width:15%;border-bottom:solid 1px lightgray;" id="gender"></td><td style="color:gray;vertical-align:bottom;width:20%;text-align:right;">电话：&nbsp;</td><td style="border-bottom:solid 1px lightgray;vertical-align:bottom;font-size:14px;color:gray;text-align:left;width:50%" id="phone_num"></td>
				</tr>
				<tr height="50px">
				<td style="color:gray;width:20%;text-align:right;vertical-align:bottom">报名时间：&nbsp;</td><td style="vertical-align:bottom;font-size:14px;color:gray;width:15%;border-bottom:solid 1px lightgray;" id="starttime"></td><td style="color:gray;vertical-align:bottom;width:20%;text-align:right;">所属机构：&nbsp;</td><td style="vertical-align:bottom;font-size:14px;color:gray;width:50%;border-bottom:solid 1px lightgray;" id="belongto"></td>
				</tr>
				</table>
				</div>
				<div class="modal-footer" style="text-align:center;height:70px">
					<button type="button" class="btn btn-default" style="color:white;background:lightseagreen;height:35px;width:80px;font-size:16px;"data-dismiss="modal">关闭</button>
				</div>
				
			</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
			
			<form id="form2" style="display:none" method="post" action="baomingglf.jsp">
				<input id = "fzgtz" name="fzgtz"></input>
			</form>
		
		<input type="hidden" id="xmid" value="<%=xmid %>"/>
		<input type="hidden" id="dijige">
	<script>
	var jq = $.noConflict(true);
	</script>
	 <script src="<%=CONTEXT_PATH%>/ts/js/belongto.js"></script>
	 <script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>	 
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/global.js"></script> 
	<!--插件--> 
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/jquery.smart-form.js"></script> 
	<script src="<%=CONTEXT_PATH%>/ts/js/jquery.iframe-transport.js"></script> 
	<script src="<%=CONTEXT_PATH%>/ts/js/jquery.ui.widget.js"></script> 
	<script src="<%=CONTEXT_PATH%>/ts/js/jquery.fileupload.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script>
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE App -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>

</body>
</html>
