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

<%String user_code=userBean.getStr("user_code"); %>
	<div class="" style="padding: 10px">
		<a href="bmsh.jsp"><image style="padding-bottom:10px"
				src="<%=CONTEXT_PATH%>/ts/image/u1155.png" id="shouye"></image></a> <span
			style="color: blue; font-size: 20px">&nbsp;&nbsp;/&nbsp;&nbsp;报名审核</span>
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
						<option value="进行中">进行中</option>
						<option value="已结束">已结束</option>
				</select></td>
				<td style="width: 5%"></td>
				<td><button
						style="color: white; height: 30px; width: 35%; background: DarkTurquoise"
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
					<th id="BM_OPTIONS" class="" style="width: 30%; text-align: center">操作</th>
				</tr>
			</thead>
			<tbody class="">
				<%
					//查询出所有的项目   对登陆人的节点进行判断包含那几个机构的报名他可以看
								String servId = "TS_XMGL";
								String act = "getDshList";
								Bean param = new Bean();
								param.set("user_code", user_code);
								//返回需要的数据放入list中进行展示
								Bean bean = ServMgr.act(servId, act, param);
								List<Bean> list = bean.getList("list");
								for(int i =0;i<list.size();i++){
									String name = list.get(i).getStr("XM_NAME");
									String zzdw = list.get(i).getStr("XM_FQDW_NAME");
									String xmtype = list.get(i).getStr("XM_TYPE");
									String cjsj = list.get(i).getStr("S_ATIME");
									String startdate = list.get(i).getStr("XM_START");
									String enddate = list.get(i).getStr("XM_END");
									//创建新时间 判断 状态
									SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
									String state = "未开始";
									String display = "none";
									if(startdate!=""&&enddate!=""){
									Date start = sdf.parse(startdate);
									Date end = sdf.parse(enddate);
									Date date = new Date();
									if(date.getTime()>start.getTime()&&date.getTime()<end.getTime()){
										
									 state = "报名审核";
									 display = "block";
									}else if(date.getTime()>end.getTime()){
										state="已结束";
									}
									}
									//添加一行隐藏的项目id
									String id = list.get(i).getStr("XM_ID");
									if(state=="报名审核"){
				%>
				<tr style="height: 50px; width: 100%">
					<td><%=i+1%></td>
					<td style="text-align: left"><%=name%></td>
					<td style="text-align: left"><%=zzdw%></td>
					<td style="text-align: left"><%=cjsj%></td>
					<td style="text-align: left"><%=state%></td>
					<td><input type="button" onclick="tiaozhuan(<%=i%>)"
						style="margin-top: 7px; border: none; color: white; font-size: 15px; background-color: LightSeaGreen; height: 35px; width: 80px"
						value="审核"></input> &nbsp;&nbsp;<input type="button"
						style="border: none; color: white; font-size: 15px; background-color: LightSeaGreen; height: 35px; width: 80px"
						value="配置"></input> &nbsp;&nbsp;<input type="button"
						style="border: none; color: white; font-size: 15px; background-color: LightSeaGreen; height: 35px; width: 80px"
						value="查看"></input></td>
					<td class="rhGrid-td-hide" id="XM_ID<%=i%>"><%=id%></td>
					<td class="rhGrid-td-hide" id="XM_TYPE<%=i%>"><%=xmtype%></td>
				</tr>
				<%
					}else{
				%>
				<tr style="height: 50px; width: 100%">
					<td><%=i+1%></td>
					<td style="text-align: left"><%=name%></td>
					<td style="text-align: left"><%=zzdw%></td>
					<td style="text-align: left"><%=cjsj%></td>
					<td style="text-align: left"><%=state%></td>
					<td></td>
					<td class="rhGrid-td-hide" id="XM_ID<%=i%>"><%=id%></td>
				</tr>
				<%
					}}
				%>
			</tbody>
		</table>
		<div id="fenyediiv"
			style="position: absolute; right: 5%; bottom: -20;">
			<table class="row">
				<tr>
					<td><ul id="fenyeul" class="pagination">
							<li><a href="#">&laquo;</a>
							<li><a href="#">&raquo;</a></li>
						</ul></td>
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
	<form id="form1" style="display: none" method="post"
		action="bmshzg.jsp">
		<input id="zgtz" name="zgtz"></input>
	</form>
	<form id="form2" style="display: none" method="post"
		action="bmshfzg.jsp">
		<input id="fzgtz" name="fzgtz"></input>
	</form>


	<input type="hidden" id="xmid" />
	<input type="hidden" id="dijige">
	<script type="text/javascript">
	$(function (){
		var user_code = "<%=user_code%>";
		selectxmdata(user_code,1);
		 var s ="yema"+1;
		 $("#"+s).addClass("active");	 
		//页面加载完执行
		var table = document.getElementById("table");  
		rowscolor(table);
	});
	function tiaozhuan(i){
		 var hid = "XM_ID"+i;
		 var xmtypeid = "XM_TYPE"+i;
			var id = document.getElementById(hid).innerHTML;
			var xmtype = document.getElementById(xmtypeid).innerHTML;
			if(xmtype=="资格类考试"){
			document.getElementById("zgtz").value=id;
				
			document.getElementById("form1").submit();
			}else{
			document.getElementById("fzgtz").value=id;
			document.getElementById("form2").submit();
			}
	}
	//分页
	function fenyeselect(){
		var user_code = "<%=user_code%>";
		selectxmdata(user_code,1);
	}
	//查询
	function xzcu(){
		var user_code = "<%=user_code%>";
		selectxmdata(user_code,1);
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
			var user_code = "<%=user_code%>";
			selectxmdata(user_code,yema1);
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
			 var yema1 = yema-1+2;
			 var user_code = "<%=user_code%>";
			 selectxmdata(user_code,yema1);
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
		var user_code = "<%=user_code%>";
		selectxmdata(user_code,ym);
		 
		//table tr  背景色
		var table = document.getElementById("table");   
		rowscolor(table);
	} 
	</script>
	<script src="<%=CONTEXT_PATH%>/ts/jsp/shenhe.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE  -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
</body>
</html>