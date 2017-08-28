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
<title>请假列表</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">

<%@ include file="/sy/base/view/inHeader.jsp"%>
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
	String user_code = userBean.getStr("USER_CODE");
	//获取用户名称
	String user_name = userBean.getStr("USER_NAME");
	//获取用户性别
	String user_sex = userBean.getStr("USER_SEX");
	//获取用户机构
	String dept_name = userBean.getStr("DEPT_NAME");
	//获取用户办公电话
	String user_office_phone = userBean.getStr("USER_OFFICE_PHONE");
	//获取用户手机号码
	String user_mobile = userBean.getStr("USER_MOBILE");
	//获取用户入行时间
	String user_cmpy_date =userBean.getStr("USER_CMPY_DATE");
	
	%>
	<div class="" style="padding: 10px">
		<a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img style="padding-bottom:10px"
				src="<%=CONTEXT_PATH%>/ts/image/u1155.png" id="shouye"></a> <span
			style="color: blue; font-size: 20px">&nbsp;&nbsp;/&nbsp;&nbsp;我的请假</span>
	</div>
	<table id="myTab" class="nav nav-tabs"
		style="margin-left: 10px; width: 98%; background-color: white">
		<tr style="height: 70px">
			<td id="keshenqingtd"  class="active"
				style="border-bottom:white solid 1px;width: 50%; text-align: center; font-size: 25px"><img
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u975.png" id="keshenimage">
				<a id="akeshen" href="#home" data-toggle="tab"><span id="keshen"
					style="color: lightseagreen">可申请的请假</span></a></td>
			<td id="yishenqingtd" class="" style="border-bottom:lightgray solid 1px;width: 50%; text-align: center; font-size: 25px"><img
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u984.png" id="yishenimage">
				<a id="ayishen" href="#tab2" data-toggle="tab"><span id="yishen"
					style="color: black">已申请的请假</span></a></td>
		</tr>

	</table>
<div id="myTabContent" class="tab-content">
	<div class="tab-pane fade in active" id="home">
		<div
			style="margin-top: -5px; margin-left: 19%; height: 5px; width: 190px; background-color: LightSeaGreen"></div>
		<div id="cuxian1"
			style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
			<span style="margin-left: 50px; padding-top: 10px">可申请的请假</span>
			<div style="float:right;">
				<input type="button" onclick="qingjia()"style="color:white;font-size:20px;background-color:LightSeaGreen;height:45px;width:140px"value="我要请假" />
			</div>
		</div>
		<div
			style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
		<div id="table1" class="" style="margin-left: 10px; width: 98%">
			<div class="content-main1">
	      		<table id="ybmtable1" style="padding: 10px;width:100%;background-color: #5ab6a6;">
					<tr style="backGround-color: WhiteSmoke; height: 30px">
						<td style="width: 10%;" align="center"><input type="checkbox" id="checkall"></td>
						<td style="width: 5%;">序号</td>
						<td style="width: 45%;">标题</td>
						<td style="width: 15%;">报名人</td>
						<td style="width: 25%">创建时间</td>
					</tr>
					<%
					List<String> stringlist = new ArrayList<String>();
					List<Bean> bmksList = ServDao.finds("TS_QJLB_BM", "");
					for(int j=0;j<bmksList.size();j++){
						if(bmksList.size()!=0){
							for(int a=0;a<bmksList.size();a++){
								//获取请假报名的 报名id  将报名id放到array中
								String qjbm_id = bmksList.get(a).getStr("BM_ID");
								if(qjbm_id!=""){
								stringlist.add(qjbm_id);
								}
							}
						}
					}
					String where1 = "AND BM_CODE="+"'"+user_code+"'";
       				List<Bean> bmglList = ServDao.finds("TS_BMLB_BM", where1);
					int j = 0;
       				for(int i=0;i<bmglList.size();i++){
						Bean bean1 = bmglList.get(i);
						String bm_id = bean1.getStr("BM_ID");
						String bm_name = bean1.getStr("BM_NAME");
						String bm_type = bean1.getStr("BM_TYPE");
						String bm_mk = bean1.getStr("BM_MK");
						String bm_xl = bean1.getStr("BM_XL");
						String bm_atime = bean1.getStr("S_ATIME");
						String bm_bt = bm_type+"-"+bm_xl+"-"+bm_mk;
						String bm_title = bean1.getStr("BM_TITLE");
						String tj_wang = "";
						if(!"".equals(bm_mk)){
							tj_wang=bm_bt;
						}else{
							tj_wang=bm_title;
						}
						if(!stringlist.contains(bm_id)){
							j++;
					%>
					<tr style="height: 40px">
						<td class="rhGrid-td-hide"><%=bm_id%></td>
						<td align="center"><input type="checkbox" name="bm_id" value="<%=bm_id%>"></td>
						<td style="padding-left: 10px;text-align: left;"><%=j%></td>
						<td ><%=tj_wang%></td>
						<td ><%=bm_name%></td>
						<td ><%=bm_atime%></td>
					</tr>
					<%
						}
					}
					%>
				</table>
			</div>
		</div>
	</div>
	<div class="tab-pane fade"  id="tab2">
		<div
			style="margin-top: -6px; margin-left: 68%; height: 5px; width: 190px; background-color: LightSeaGreen"></div>
		<div id="cuxian2"
			style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
			<span style="margin-left: 50px; padding-top: 10px">已申请的请假</span>
		</div>
		<div style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
		<div id="table1" class="" style="margin-left: 10px; width: 98%">
			<div class="content-main1">
	      		<table id="ybmtable" style="padding: 10px;width:100%;background-color: #5ab6a6;">
					<tr style="backGround-color: WhiteSmoke; height: 30px">
						<td style="width: 6%;">序号</td>
						<td style="width: 25%;">标题</td>
						<td style="width: 20%;">单位</td>
						<td style="width: 10%;">请假人</td>
						<td style="width: 10%">创建时间</td>
						<td style="width: 9%; ">审核状态</td>
						<td style="width: 20%; text-align: center">操作</td>
					</tr>
					<%
					List<Bean> qjList = ServDao.finds("TS_QJLB_QJ","");
					for(int i=0;i<qjList.size();i++){
						Bean bean1 = qjList.get(i);
						String qj_id = bean1.getStr("QJ_ID");
						String qj_name = bean1.getStr("QJ_NAME");
						String qj_title = bean1.getStr("QJ_TITLE");
						String qj_danwei = bean1.getStr("QJ_DANWEI");
						String qj_date = bean1.getStr("QJ_DATE");
						String qj_status = bean1.getStr("QJ_STATUS");
						if(qj_status.equals("1")){
							qj_status="审核中";
						}else if(qj_status.equals("2")){
							qj_status="已通过";
						}else if(qj_status.equals("3")){
							qj_status="未通过";
						}
					%>
					<tr style="height: 40px">
						<td class="rhGrid-td-hide"><%=qj_id%></td>
						<td style="padding-left: 10px;text-align: left;"><%=i+1%></td>
						<td ><%=qj_title%></td>
						<td ><%=qj_danwei%></td>
						<td ><%=qj_name%></td>
						<td ><%=qj_date%></td>
						<td ><%=qj_status%></td>
						<td><input type="button" onclick="chakan(this)"style="color:white;font-size:15px;background-color:LightSeaGreen;height:30px;width:70px"value="查看" />
<%-- 							<input type="button" onclick="bianji(this)"style="color:white;font-size:15px;background-color:LightSeaGreen;height:30px;width:70px"value="编辑" />
							<input type="button" onclick="shanchu(this)"style="color:white;font-size:15px;background-color:LightSeaGreen;height:30px;width:70px"value="删除" />
						</td>
--%>
					</tr>
					<%
						}
					%>
				</table>
	      		
			</div>
		</div>
	</div>
</div>
<form action="qjlb_qj2.jsp" method="post" id="formchakan" style="display: none;">
		<input type = "text" id="qjid" name="qjid" value=""/>
		<input type = "text" id="hidden" name="hidden" value=""/>
		<input type = "submit" value="传递" />
</form>
<form action="qjlb_qj.jsp" method="post" id="formid" style="display: none;">
		<input type = "text" id="bmids" name="bmids" value=""/>
		<input type = "submit" value="传递" />
</form>
<script>
	//全选，全不选
	$("#checkall").click( 
			  function(){ 
			    if(this.checked){ 
			        $("input[name='bm_id']").prop('checked', true)
			    }else{ 
			        $("input[name='bm_id']").prop('checked', false)
			    } 
			  } 
		);
	//可选报名  已选报名字体图片改变
	$('#akeshen').click(function(){
		document.getElementById("keshen").style.color="LightSeaGreen";
		document.getElementById("keshenimage").src="/ts/image/u975.png";
		document.getElementById("yishenimage").src="/ts/image/u1131.png";
		document.getElementById("yishen").style.color="black";
		
		
	});
	$('#ayishen').click(function(){
	   
		document.getElementById("keshen").style.color="black";
		document.getElementById("yishenimage").src="/ts/image/u975.png";
		document.getElementById("keshenimage").src="/ts/image/u1131.png";
		document.getElementById("yishen").style.color="LightSeaGreen";
	    
	});
	//加载完毕  显示第一个 tab active  显示隐藏
	 $(function () {
		$('#myTab li:eq(1) a').tab('show');
      }); 
</script>
<script type="text/javascript">
	$(function () {
		doubleBgColor1(document.getElementById("ybmtable1"),"#f0faff","#ffffff");
		doubleBgColor(document.getElementById("ybmtable"),"#f0faff","#ffffff")
	}); 
	//表格颜的设置
	function doubleBgColor1(Table,Bg1,Bg2) {  
		for (var i=1;i<Table.rows.length;i++) Table.rows[i].bgColor=i%2?Bg2:Bg1;  
	} 
	function doubleBgColor(Table,Bg1,Bg2) {  
		for (var i=1;i<Table.rows.length;i++) Table.rows[i].bgColor=i%2?Bg2:Bg1;  
	} 
</script>
<script type="text/javascript">
	//请假跳转到请假页面
	function qingjia(){
		var pkCode=document.getElementsByName("bm_id");
		var bmids="";
		for(var i=0;i<pkCode.length;i++){
     		if(pkCode[i].checked){
     			if(i==0){
     				bmids = pkCode[i].value;
     			}else{
     				bmids +="," + pkCode[i].value;
     			}
     		}
     	}
		document.getElementById("bmids").value = bmids;
    	document.getElementById("formid").submit();
	}
	//点击进行编辑
	function bianji(obj){
		var pkCode=obj.parentNode.parentNode.getElementsByTagName("td")[0].innerHTML;
		document.getElementById("qjid").value = pkCode;
		document.getElementById("hidden").value = 1;
    	document.getElementById("formchakan").submit();
	}
	//点击进行查看
	function chakan(obj){
		var pkCode=obj.parentNode.parentNode.getElementsByTagName("td")[0].innerHTML;
		document.getElementById("qjid").value = pkCode;
		document.getElementById("hidden").value = 2;
    	document.getElementById("formchakan").submit();
	}
	//删除当前条请假数据
	function shanchu(obj){
		var pkCode=obj.parentNode.parentNode.getElementsByTagName("td")[0].innerHTML;
		var param={};
		param["pkCode"]=pkCode;
		FireFly.doAct("TS_QJLB_QJ", "deleteData", param);
		window.history.go(0);
	}
	$('#ayishen').click(function(){
		document.getElementById("keshen").style.color="black";
		document.getElementById("yishenimage").src="/ts/image/u975.png";
		document.getElementById("keshenimage").src="/ts/image/u1131.png";
		document.getElementById("yishen").style.color="LightSeaGreen";
	    
	});
	//加载完毕  显示第一个 tab active  显示隐藏
	 $(function () {
		
			$('#myTab li:eq(1) a').tab('show');
			var table = document.getElementById("ybmtable");  
	    
      });
</script>
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
</body>
</html>
