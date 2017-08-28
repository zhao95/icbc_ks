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
<title>报名审核</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">
<!-- 获取后台数据 -->
<%@ include file="/sy/base/view/inHeader.jsp"%>
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

	<div class="" style="padding: 10px">
		<a href="index_qt.jsp"><image style="padding-bottom:10px"
				src="/ts/image/u1155.png" id="shouye"></image></a> <span
			style="color: blue; font-size: 20px">&nbsp;&nbsp;/&nbsp;&nbsp;我的报名</span>
	</div>
	<table id="myTab" class="nav nav-tabs"
		style="margin-left: 10px; width: 98%; background-color: white">
		<tr style="height: 70px">
			<td id="dshtd"  class="active"
				style="border-bottom:white solid 1px;width: 33.3%; text-align: center; font-size: 25px"><image
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u1677.png" id="dshimage">
				<a id="dsha" href="#home" data-toggle="tab"><span id="dshsp"
					style="color: lightseagreen">待审核人员</span>
				</a>
			</td>
			<td id="shtgtd" class="" style="border-bottom:lightgray solid 1px;width: 33.3%; text-align: center; font-size: 25px"><image
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u1681.png" id="shtgimage">
				<a id="shtga" href="#tab2" data-toggle="tab"><span id="shtgsp"
					style="color: black">审核通过人员</span>
				</a>
			</td>
			<td id="shwtgtd" class="" style="border-bottom:lightgray solid 1px;width: 33.3%; text-align: center; font-size: 25px"><image
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u1695.png" id="shwtgimage">
				<a id="shwtda" href="#tab3" data-toggle="tab"><span id="shwtgsp"
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
						<td style="width:15%;text-align:right">姓名&nbsp;&nbsp;<input style="height:30px;width:50%" id="xm" type="text"></input></td>
						<td style="width:15;text-align:center">登录名&nbsp;&nbsp;<input style="height:30px;width:50%" id="dlm" type="text"></input></td>
						<td style="width:15%;text-align:center">审核级数&nbsp;&nbsp;<input style="height:30px;width:50%" id="shjs" type="text"></input></td>
						<td style="width:30%;text-align:right">人力资源编码&nbsp;&nbsp;<input  style="height:30px;width:50%" id="rlzybm" type="text"></input></td>
						<td style="width:13%;text-align:center"><select id = "zhuangtai" onchange="ztcx()">
				            <option selected="selected">全部</option>
				            <option value="进行中">进行中</option>
				            <option value="已结束">已结束</option>
				            </select>
				        </td>
				        <td style="width:12%;"><button style="border:none;color:white;height:30px;width:35%;background:DarkTurquoise" onclick="xzcu()"id = "chaxun">查询</button></td>
					</tr>
				</tbody>
			</table>
			<div id="xixian"
				style="margin-left: 10px; margin-top: 20px; background-color: lightgray; height:1px; width: 98%">
			</div>
			<table style="margin-top:20px">
			<tr>
				<td style="width:15%;text-align:right"><button style="border:none;color:white;height:30px;width:50%;background:lightseagreen" onclick="shenhe()" id = "shenhe">审核</button></td>
				<td style="width:20%;text-align:right"><button style="border:none;color:white;height:30px;width:80%;background:lightseagreen" onclick="zdyl()" id = "zdyl">自定义显示列</button></td>
				<td style="width:20%;text-align:right"><button style="border:none;color:white;height:30px;width:80%;background:lightseagreen" onclick="export()" id = "export">文件批量导出</button></td>
				<td style="width:20%;text-align:right"><button style="border:none;color:white;height:30px;width:40%;background:lightseagreen" onclick="fanhui()" id = "fanhui">返回</button></td>
			</tr>
			</table>
			<div id="dshtable" class="" style="margin-top:20px;margin-left: 10px; width: 98%">
					<table class="rhGrid JPadding JColResizer" id="dshtablea">
						<thead id="tem" class="">
							<tr style="backGround-color:WhiteSmoke; height: 30px">
								<th 
									style="width: 6.6%; text-align: center">序号</th>
								<th style="width: 10%;">姓名</th>
								<th style="width: 10%;">岗位类别</th>
								<th style="width: 10%;">序列</th>
								<th style="width: 10%;">模块</th>
								<th style="width: 10%; text-align: center">报考级别</th>
								<th style="width: 10%; text-align: center">职务层级</th>
								<th style="width: 10%; text-align: center">审核级数</th>
								<th style="width: 10%; text-align: center">状态</th>
								<th style="width: 10%; text-align: center">部门</th>
								<th style="width: 10%; text-align: center">操作</th>
							</tr>
						</thead>
						
					</table>
					<tbody>
					<%
					//项目
					String xmid = request.getParameter("zgtz"); 
					String servId = "TS_BMLB_BM";
					String act = "1";
					Bean param = new Bean();
					param.set("xmid",xmid);
					Bean bean = ServMgr.act(servId, act, param); 
					List<Bean> list = bean.getList("list");
					for(int i=0;i<list.size();i++){
						String name = list.get(i).getStr("BM_NAME");
						String leibie = list.get(i).getStr("BM_LB");
						String xulie = list.get(i).getStr("BM_XL");
						String mokuai = list.get(i).getStr("BM_MK");
						String jibie = list.get(i).getStr("BM_TYPE");
						String zhiwu = list.get(i).getStr("BM_LB");
						String zhuangtai = list.get(i).getStr("BM_LB");
						String bumen = list.get(i).getStr("BM_LB");
						//审核级数
						String dept = list.get(i).getStr("S_DEPT");
						String id = list.get(i).getId();
					%>
					<tr><td><%=i %></td><td><%=name %></td><td><%=leibie %></td><td><%=xulie %></td><td><%=mokuai %></td><td><%=jibie %></td><td><%=zhiwu %></td><td><%=zhuangtai %></td><td><%=bumen %></td><td class="rhGrid-td-hide" id="BM_ID<%=i%>" ><%=id %></td><td style="text-align: center"><a onclick = "formsubmit(<%=i %>)" href="#"><image src="/ts/image/u1747.png"></image></a>&nbsp;&nbsp;<a onclick="form2submit(<%=i %>)" href="#"><image src="/ts/image/u1755.png"></image></a></td></tr>
					<%} %>
					<tr></tr>
					</tbody>
				</div>
		<div id="fenyediiv" style="position:absolute;right:5%;bottom:-20;">
			<table class="row">
			<tr>
				<td><ul id="fenyeul" class="pagination">
			          <li><a href="#">&laquo;</a><li ><a  href="#">&raquo;</a></li>
			        </ul>
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
		<div class="tab-pane fade"  id="tab2">
		    <div
				style="margin-top: -5px; margin-left: 40%; height: 5px; width: 20%; background-color: LightSeaGreen"></div>
				<div id="cuxian2"
				style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">审核通过人员</span>
			</div>
			<table id="cxkuang" style="margin-top:20px">
				<tbody>
					<tr style="height:20px">
						<td style="width:15%;text-align:right">姓名&nbsp;&nbsp;<input style="height:30px;width:50%" id="xm" type="text"></input></td>
						<td style="width:5%"></td>
						<td style="width:15;text-align:left">登录名&nbsp;&nbsp;<input style="height:30px;width:50%" id="dlm" type="text"></input></td>
				        <td style="width:60%;text-align:left"><button style="border:none;color:white;height:30px;width:10%;background:DarkTurquoise" onclick="xzcu()"id = "chaxun">查询</button></td>
					</tr>
				</tbody>
			</table>
			<div id="xixian"
				style="margin-left: 10px; margin-top: 20px; background-color: lightgray; height:1px; width: 98%">
			</div>
			<table style="margin-top:20px">
			<tr>
				<td style="width:15%;text-align:right"><button style="border:none;color:white;height:30px;width:50%;background:lightseagreen" onclick="shenhe()" id = "shenhe">审核</button></td>
				<td style="width:20%;text-align:right"><button style="border:none;color:white;height:30px;width:80%;background:lightseagreen" onclick="export()" id = "export">文件批量导出</button></td>
				<td style="width:20%;text-align:right"><button style="border:none;color:white;height:30px;width:40%;background:lightseagreen" onclick="fanhui()" id = "fanhui">返回</button></td>
			</tr>
			</table>
			<div id="dshtable" class="" style="margin-top:20px;margin-left: 10px; width: 98%">
					<table class="rhGrid JPadding JColResizer" id="dshtablea">
						<thead id="tem" class="">
							<tr style="backGround-color:WhiteSmoke; height: 30px">
								<th 
									style="width: 6.6%; text-align: center">序号</th>
								<th style="width: 10%;">姓名</th>
								<th style="width: 10%;">岗位类别</th>
								<th style="width: 10%;">序列</th>
								<th style="width: 10%;">模块</th>
								<th style="width: 10%; text-align: center">报考级别</th>
								<th style="width: 10%; text-align: center">职务层级</th>
								<th style="width: 10%; text-align: center">审核级数</th>
								<th style="width: 10%; text-align: center">状态</th>
								<th style="width: 10%; text-align: center">部门</th>
								<th style="width: 10%; text-align: center">操作</th>
							</tr>
						</thead>
						
					</table>
					<tbody>
					<%
					//项目
					String xmid1 = request.getParameter("zgtz"); 
					String servId1 = "TS_BMLB_BM";
					String act1 = "1";
					Bean param1 = new Bean();
					param.set("xmid",xmid);
					Bean bean1 = ServMgr.act(servId, act, param); 
					List<Bean> list1 = bean.getList("list");
					for(int i=0;i<list.size();i++){
						String name = list1.get(i).getStr("BM_NAME");
						String leibie = list1.get(i).getStr("BM_LB");
						String xulie = list1.get(i).getStr("BM_XL");
						String mokuai = list1.get(i).getStr("BM_MK");
						String jibie = list1.get(i).getStr("BM_TYPE");
						String zhiwu = list1.get(i).getStr("BM_LB");
						String zhuangtai = list1.get(i).getStr("BM_LB");
						String bumen = list1.get(i).getStr("BM_LB");
						//审核级数
						String dept = list1.get(i).getStr("S_DEPT");
						String id = list1.get(i).getId();
					%>
					<tr><td><%=i %></td><td><%=name %></td><td><%=leibie %></td><td><%=xulie %></td><td><%=mokuai %></td><td><%=jibie %></td><td><%=zhiwu %></td><td><%=zhuangtai %></td><td><%=bumen %></td><td class="rhGrid-td-hide" id="BM_ID<%=i%>" ><%=id %></td><td style="text-align: center"><a onclick = "formsubmit(<%=i %>)" href="#"><image src="/ts/image/u1747.png"></image></a>&nbsp;&nbsp;<a onclick="form2submit(<%=i %>)" href="#"><image src="/ts/image/u1755.png"></image></a></td></tr>
					<%} %>
					<tr></tr>
					</tbody>
				</div>
		<div id="fenyediiv" style="position:absolute;right:5%;bottom:-20;">
			<table class="row">
			<tr>
				<td><ul id="fenyeul" class="pagination">
			          <li><a href="#">&laquo;</a><li ><a  href="#">&raquo;</a></li>
			        </ul>
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
		<div class="tab-pane fade"  id="tab3">
		<div
				style="margin-top: -5px; margin-left: 73%; height: 5px; width: 20%; background-color: LightSeaGreen"></div>
				<div id="cuxian3"
				style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">审核不通过人员</span>
			</div>
			<table id="cxkuang" style="margin-top:20px">
				<tbody>
					<tr style="height:20px">
						<td style="width:15%;text-align:right">姓名&nbsp;&nbsp;<input style="height:30px;width:50%" id="xm" type="text"></input></td>
						<td style="width:5%"></td>
						<td style="width:15;text-align:left">登录名&nbsp;&nbsp;<input style="height:30px;width:50%" id="dlm" type="text"></input></td>
				        <td style="width:60%;text-align:left"><button style="border:none;color:white;height:30px;width:10%;background:DarkTurquoise" onclick="xzcu()"id = "chaxun">查询</button></td>
					</tr>
				</tbody>
			</table>
			<div id="xixian"
				style="margin-left: 10px; margin-top: 20px; background-color: lightgray; height:1px; width: 98%">
			</div>
			<table style="margin-top:20px">
			<tr>
				<td style="width:15%;text-align:right"><button style="border:none;color:white;height:30px;width:50%;background:lightseagreen" onclick="shenhe()" id = "shenhe">审核</button></td>
				<td style="width:20%;text-align:right"><button style="border:none;color:white;height:30px;width:80%;background:lightseagreen" onclick="export()" id = "export">文件批量导出</button></td>
				<td style="width:20%;text-align:right"><button style="border:none;color:white;height:30px;width:40%;background:lightseagreen" onclick="fanhui()" id = "fanhui">返回</button></td>
			</tr>
			</table>
			<div id="dshtable" class="" style="margin-top:20px;margin-left: 10px; width: 98%">
					<table class="rhGrid JPadding JColResizer" id="dshtablea">
						<thead id="tem" class="">
							<tr style="backGround-color:WhiteSmoke; height: 30px">
								<th 
									style="width: 6.6%; text-align: center">序号</th>
								<th style="width: 10%;">姓名</th>
								<th style="width: 10%;">岗位类别</th>
								<th style="width: 10%;">序列</th>
								<th style="width: 10%;">模块</th>
								<th style="width: 10%; text-align: center">报考级别</th>
								<th style="width: 10%; text-align: center">职务层级</th>
								<th style="width: 10%; text-align: center">审核级数</th>
								<th style="width: 10%; text-align: center">状态</th>
								<th style="width: 10%; text-align: center">部门</th>
								<th style="width: 10%; text-align: center">操作</th>
							</tr>
						</thead>
						
					</table>
					<tbody>
					<%
					//项目
					String xmid2 = request.getParameter("zgtz"); 
					String servId2 = "TS_BMLB_BM";
					String act2 = "1";
					Bean param2 = new Bean();
					param.set("xmid",xmid);
					Bean bean2 = ServMgr.act(servId, act, param); 
					List<Bean> list2 = bean.getList("list");
					for(int i=0;i<list.size();i++){
						String name = list2.get(i).getStr("BM_NAME");
						String leibie = list2.get(i).getStr("BM_LB");
						String xulie = list2.get(i).getStr("BM_XL");
						String mokuai = list2.get(i).getStr("BM_MK");
						String jibie = list2.get(i).getStr("BM_TYPE");
						String zhiwu = list2.get(i).getStr("BM_LB");
						String zhuangtai = list2.get(i).getStr("BM_LB");
						String bumen = list2.get(i).getStr("BM_LB");
						//审核级数
						String dept = list2.get(i).getStr("S_DEPT");
						String id = list2.get(i).getId();
					%>
					<tr><td><%=i %></td><td><%=name %></td><td><%=leibie %></td><td><%=xulie %></td><td><%=mokuai %></td><td><%=jibie %></td><td><%=zhiwu %></td><td><%=zhuangtai %></td><td><%=bumen %></td><td class="rhGrid-td-hide" id="BM_ID<%=i%>" ><%=id %></td><td style="text-align: center"><a onclick = "formsubmit(<%=i %>)" href="#"><image src="/ts/image/u1747.png"></image></a>&nbsp;&nbsp;<a onclick="form2submit(<%=i %>)" href="#"><image src="/ts/image/u1755.png"></image></a></td></tr>
					<%} %>
					<tr></tr>
					</tbody>
				</div>
		<div id="fenyediiv" style="position:absolute;right:5%;bottom:-20;">
			<table class="row">
			<tr>
				<td><ul id="fenyeul" class="pagination">
			          <li><a href="#">&laquo;</a><li ><a  href="#">&raquo;</a></li>
			        </ul>
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
			
			<form id="form1" style="display:none" method="post" action="baomingzgks.jsp">
				<input id = "zgtz" name="zgtz"></input>
				<input type="submit" name="Submit" value="提交">
			</form>
			<form id="form2" style="display:none" method="post" action="baomingglf.jsp">
				<input id = "fzgtz" name="fzgtz"></input>
			<input type="submit" name="Submit" value="提交">
			</form>
		
		<input type="hidden" id="xmid"/>
		<input type="hidden" id="dijige">
	
	<script>
	//分页查询
	function fenyeselect(){	
		//跟 级别 按钮 的onchange时间一样都要 筛选所有条件下的数据
		selectdata1("<%=xmid%>");
	}
	//全部条件查询
	function xzcu(){
		alert("a");
		selectdata1("<%=xmid%>");
	}
	function fanhui(){
		alert("a");
		window.history.go(-1);
	}
	
	//上一页 按钮
	function forward(){
		//获取页码 数
		var yema = document.querySelectorAll("li[class='active']")[0].innerText;
		if(yema==1){
			return false;
		}else{
			//要显示的页码
			var yema1 = yema-1;
			var xmid = "<%=xmid%>"
			shenHeFenYe(xmid,yema1);
			//table tr  背景色
			var table = document.getElementById("table");   
			rowscolor(table);
		}
	}
	//下一页按钮
	function backward(last){
		//获取页码 数
		var yema = document.querySelectorAll("li[class='active']")[0].innerText;
		var lastyema = yema-1+2;
		if(lastyema==last){
			return false;
		}else{
			//要显示的页码数
			var xmid = "<%=xmid%>"
			 var yema1 = yema-1+2;
			 shenHeFenYe(xmid,yema1);
			//table tr  背景色
			var table = document.getElementById("table");   
			rowscolor(table);
	     	  
		}
	}
	//点击第几页跳转
	 function chaxun(i){
		var id = "yema"+i;
		//点击第几页
		var ym = document.getElementById(id).innerText;
		//传入 要显示的页码数即可
		var xmid = "<%=xmid%>"
		shenHeFenYe(xmid,ym);
		//table tr  背景色
		var table = document.getElementById("table");   
		rowscolor(table);
	} 
	//可选报名  已选报名字体图片改变
		$('#dsha').click(function(){
			document.getElementById("dshimage").src="/ts/image/u1677.png";
			document.getElementById("dshsp").style.color="LightSeaGreen";
			document.getElementById("shtgsp").style.color="black";
			document.getElementById("shtgimage").src="/ts/image/u1681.png";
			document.getElementById("shwtgsp").style.color="black";
			document.getElementById("shwtgimage").src="/ts/image/u1695.png";
			
		});
		$('#shtga').click(function(){
		   
			document.getElementById("shtgsp").style.color="LightSeaGreen";
			document.getElementById("shtgimage").src="/ts/image/u2216.png";
			document.getElementById("dshimage").src="/ts/image/u2212.png";
			document.getElementById("dshsp").style.color="black";
			document.getElementById("shwtgsp").style.color="black";
			document.getElementById("shwtgimage").src="/ts/image/u1695.png";
			var table = document.getElementById("table3");  
		    rowscolor(table);
		    
		});
		$('#shwtda').click(function(){
			   

			document.getElementById("shwtgsp").style.color="LightSeaGreen";
			document.getElementById("shwtgimage").src="/ts/image/u2813.png";
			document.getElementById("shtgsp").style.color="black";
			document.getElementById("shtgimage").src="/ts/image/u1681.png";
			document.getElementById("dshimage").src="/ts/image/u2212.png";
			document.getElementById("dshsp").style.color="black";
			var table = document.getElementById("table3");  
		    rowscolor(table);
		    
		});
</script>
<script src="<%=CONTEXT_PATH%>/shenhe.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/baoming.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE App -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
</body>
</html>
