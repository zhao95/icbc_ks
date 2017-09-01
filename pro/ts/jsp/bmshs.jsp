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
		<a href="/index_qt.jsp"><image style="padding-bottom:10px"
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
					<th id="BM_OPTIONS" class="" style="width: 10%; text-align: center">操作</th>
				</tr>
			</thead>
			<tbody class="">
				
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
	<div class="modal fade" id="bminfo" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog" style="width:50%">
			<div class="modal-content">
				<div class="modal-header" style="background-color: #00c2c2;color: white">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
						&times;
					</button>
					<h4 class="modal-title">
						报名详细信息
					</h4>
					
				</div>
				<div style="padding-left:30px;padding-top:20px;">
				<table style="width:650px;font-size:20px;color:lightseagreen">
				<tr height="50px">
				<td style="width:30%;text-align:right;">报名编号：&nbsp;</td><td style="font-size:14px;color:red;width:20%" id="bmcode"></td><td style="width:30%;text-align:right;">报名名称：&nbsp;</td><td style="font-size:14px;color:red;text-align:left;width:40%" id="bmname" ></td>
				</tr>
				<tr height="50px">
				<td style="width:30%;text-align:right;">创建人：&nbsp;</td><td style="font-size:14px;color:red;width:20%" id="creator"></td><td style="width:30%;text-align:right;">组织单位：&nbsp;</td><td style="font-size:14px;color:red;text-align:left;width:40%" id="oragnize"></td>
				</tr>
				<tr height="50px">
				<td style="width:30%;text-align:right;">报名开始时间：&nbsp;</td><td style="font-size:14px;color:red;width:20%" id="starttime"></td><td style="width:30%;text-align:right;">报名结束时间：&nbsp;</td><td style="font-size:14px;color:red;text-align:left;width:40%" id="endtime"></td>
				</tr>
				<tr height="50px">
				<td style="width:30%;text-align:right;">状态：&nbsp;</td><td style="font-size:14px;color:red;width:20%" id="status"></td><td style="width:30%;text-align:right;"></td><td style="font-size:14px;color:red;text-align:left;width:40%" ></td>
				</tr>
				</table>
				</div>
				<div style="padding-top:10px;width:200px;font-size:20px;text-align:center;color:lightseagreen">
				描述：
				</div>
				<table style="width:700px">
				<tr>
				<td style="width:15%"></td><td style="disabled:disabled;height:100px;vertical-align:top"><textarea id="describe" style="border:solid 1px white;height:90%;width:90%" wrap="soft"></textarea></td>
				</tr>
				</table >
				<div style="padding-top:10px;width:200px;font-size:20px;text-align:center;color:lightseagreen">
				考试须知：
				</div>
				<table style="width:700px">
				<tr>
				<td style="width:15%"></td><td style="height:100px;vertical-align:top"><textarea id="mustknow" style="border:solid 1px white;height:90%;width:90%" wrap="soft"></textarea></td>
				</tr>
				</table>
				<div class="modal-footer" style="text-align:center;height:100px">
					<button type="button" class="btn btn-default" style="height:50px;width:100px" data-dismiss="modal">关闭</button>
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


	<input type="hidden" id="xmid" />
	<input type="hidden" id="dijige">
	<script type="text/javascript">
	var jq = $.noConflict(true);
	jq(function (){
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
	function chakan(obj){
		
		//通过项目查找报名信息
		 var hid = "XM_ID"+obj;
		var id = document.getElementById(hid).innerHTML;
		var param={};
		param["xmid"]=id;
		var result = FireFly.doAct("TS_XMGL_BMGL","getXmInfo",param);
		var data = result.list;
		var pageEntity = JSON.parse(data);
		$("#bmcode").text(pageEntity[0].BM_ID);
		$("#bmname").text(pageEntity[0].BM_NAME);
		$("#creator").text(pageEntity[0].BM_USER);
		$("#oragnize").text(pageEntity[0].ODEPT);
		$("#starttime").text(pageEntity[0].BM_START);
		$("#endtime").text(pageEntity[0].BM_END);
		$("#mustknow").text(pageEntity[0].BM_KSXZ);
		$("#mustknow").attr("disabled","disabled");
		$("#status").text(pageEntity[0].BM_STATE);
		$("#describe").text(pageEntity[0].BM_DESC);
		$("#describe").attr("disabled","disabled");
	}

	//项目筛选  能审核的项目   
	function selectxmdata(user_code,yema){
		var name = document.getElementById("mc").value;
	    var zuzhidanwei =  document.getElementById("zzdw").value;
	    var where1 = "";
	    var where2 = "";
	    if(name!=""){
	    	where1 = "AND XM_NAME like"+"'%"+name+"%'";
	    }
	    if(zuzhidanwei!=""){
	    	where2 = " AND XM_FQDW_NAME like"+"'%"+zuzhidanwei+"%'"
	    }
	     	//下拉框值
	     	var type =  document.getElementById("zhuangtai");
	     	var index = type.selectedIndex;
	     	var  zhuangtai = type.options[index].value;
	     	
	     	
	     	//每页条数
			var select = document.getElementById("yema");
			 var index = document.getElementById("yema").selectedIndex;
			var myts = select.options[index].value;
			//重新计算 页码
			var param={};
			param["bm_zhuantai"]=zhuangtai;
			param["user_code"]=user_code;
			param["nowpage"]=yema;
			param["shownum"]=myts;
	     	param["where"]=where1 + where2;
	     	//在某一结点下 可以查看哪些机构的 审核  过滤出来
	     	var result = FireFly.doAct("TS_XMGL","getUncheckList",param);
	     	
	     	var result2 = FireFly.doAct("TS_XMGL","getShJsonList",param);
			//data为json格式字符串
			var data2 = result2.list;
	     	var data = result.list;
	     	//将json字符串 转换为 json对象
	     	var first = (yema-1)*myts+1;
	     	if(data==null){
	     		$("#table tbody").html("");
	     		$("#fenyeul").html("");
	     		$("#fenyeul").append('<li><a href="#">&laquo;</a></li><li><a  href="#">&raquo;</a></li>');
	     	}else{
	     		
	     		
	     	var pageEntity=JSON.parse(data);
	     	var pageEntity2 = JSON.parse(data2);
	     	 //总条数/每页
			//页数
			 var yeshu = Math.floor(pageEntity2.length/myts);
			 var yushu = pageEntity2.length%myts;
			 $("#fenyeul").html("");
			
				 if(yushu!=0){
					 //余数为0
					 yeshu+=1;
				 }
				  for(var z=0;z<yeshu;z++){
				  var j=z+1;
					 if(z==0){
				$("#fenyeul").append('<li onclick="forward()"><a href="#">&laquo;</a></li><li id="yema1" class="active" onclick="chaxun('+j+')"><a href="#">1</a></li>');
					 }else {
				 $("#fenyeul").append('<li id="yema'+j+'" onclick=chaxun('+j+')><a  href="#">'+j+'</a></li>');
						 
					 }
					 
				 
			 }
	     		  //最后一页
				 var last =yeshu+1;
				 $("#fenyeul").append(' <li id="yema'+last+'" onclick="backward('+last+')"><a  href="#">&raquo;</a></li>');
				 $("li.active").removeClass();
				 var s ="yema"+yema;
				 $("#"+s).addClass("active");
	     	$("#table tbody").html("");
	     	  for(var i=0;i<pageEntity.length;i++){
	     		  
	     		 var name = pageEntity[i].XM_NAME;
					var zzdw = pageEntity[i].XM_FQDW_NAME;
					var startdate = pageEntity[i].XM_START;
					var cjsj = pageEntity[i].S_ATIME;
					var enddate = pageEntity[i].XM_END;
					var xmtype = pageEntity[i].XM_TYPE;
					//创建新时间 判断 状态
					var str1 =startdate;
					var str2 = enddate;
					str1 = str1.replace(/-/g,"/");
					str2 = str2.replace(/-/g,"/");
					var state = "已结束";
					if(startdate!=""&&enddate!=""){
					var end = new Date(str2);
					var start = new Date(str1);
					var date = new Date();
				
					if(date.getTime()>start.getTime()&&date.getTime()<end.getTime()){
						
					 state = "报名审核";
					}else if(date.getTime()<start.getTime()){
						state = "未开始";
					}
					}
					//添加一行隐藏的项目id
					var id = pageEntity[i].XM_ID;
					var xuhao = first+i;
	     		//为table重新appendtr
					if(state=="报名审核"){
	     			$("#table tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+xuhao+'</td><td class="indexTD" style="text-align: left">'+name+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: left">'+zzdw+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: left">'+cjsj+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: left">'+state+'</td><td class="rhGrid-td-hide" id="XM_ID'+i+'" >'+id+'</td><td class="rhGrid-td-hide" id="XM_TYPE'+i+'">'+xmtype+'</td><td><input type="button" onclick="tiaozhuan('+i+')" style="margin-top:7px;border:none;color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:80px" value="审核"></input>&nbsp;&nbsp;<input data-toggle="modal" data-target="#bminfo" onclick="chakan('+i+')" type="button" style="border:none;color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:80px" value="查看"></input></td></tr>');
		     		
					}else{
						$("#table tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+xuhao+'</td><td class="indexTD" style="text-align: left">'+name+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: left">'+zzdw+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: left">'+cjsj+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: left">'+state+'</td><td class="rhGrid-td-hide" id="BM_ID'+i+'" >'+id+'</td><td><input data-toggle="modal" data-target="#bminfo" onclick="chakan('+i+')" type="button" style="border:none;color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:80px" value="查看"></input></td></tr>');	
					}
	     	  }
	     	
	     	}	 
	     	
	   	 
	}
	function rowscolor(table){
		 var rows = table.getElementsByTagName("tr");  
		    for(i = 1; i < rows.length; i++){  
		        if(i % 2 == 0){  
		   
		            rows[i].style.backgroundColor = "Azure";  
		       }  
		    } 
	}
	function ztcx(){
		var user_code = "<%=user_code%>";
		selectxmdata(user_code,1);
	}
	</script>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script>
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE  -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
</body>
</html>