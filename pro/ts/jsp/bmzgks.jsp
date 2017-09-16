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
	 <%@ include file="../../sy/base/view/inHeader.jsp" %>
 <%-- <%@ include file="/sy/base/view/inHeader-icbc.jsp"%>  --%>
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
				<table align="center" style="width: 90%; background-color: #fed1d1;">
					<tr>
						<td><span style="color: #ff0000;">！</span> 温馨提示：</td>
						<td height="50px" align="left">1.您当前在 <span
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
					<table border="1" align="center"
						style="width: 90%; padding-top: 10px;">
						<tr style="background-color: #f2f2f2;">
							<td colspan="6">个人基本信息</td>
						</tr>
						<tr>
							<td width="16.5%">人力资源编码</td>
							<td width="16.5%"><%=user_code %></td>
							<td width="16.5%">姓名</td>
							<td width="16.5%"><%=user_name%></td>
							<td width="16.5%">性别</td>
							<td width="17.5%">
								<% if (user_sex == "1") { %>女<% } else { %>男<% } %>
							</td>
						</tr>
						<tr style="background-color: #f7fdff;">
							<td>所属机构</td>
							<td colspan="5"><%=odept_name %></td>
						</tr>
						<tr>
							<td width="16.5%">岗位类别</td>
							<td width="16.5%" id='gwlb'></td>
							<td width="16.5%">岗位序列</td>
							<td width="16.5%" id='gwxl'></td>
							<td width="16.5%">职务层级</td>
							<td width="17.5%" id='zwcj'></td>
						</tr>
						<tr style="background-color: #f7fdff;">
							<td width="16.5%">入行时间</td>
							<td width="16.5%"><%=user_cmpy_date%></td>
							<td width="16.5%">办公电话</td>
							<td width="16.5%"><%=user_office_phone %></td>
							<td width="16.5%">手机号码(<span style="color: red;">融e联绑定的手机号</span>)
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
					<span style="color: #0782cb;">★ 跨序列应报名的考试</span> <span
						style="color: #fdb64f;">(提示：只允许选择两个跨序列的考试)</span>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<button class="btn btn-success"
						style="width: 100px; background-color: #00c2c2;"
						data-toggle="modal" data-target="#myModal">选择考试</button>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<!-- 	       					<button class="btn btn-success" style="width:100px;background-color: #00c2c2;" data-toggle="modal" data-target="#deletec">删除</button> -->
				</div>
				<div style="padding-top: 5px;">
					<table border="1" style="bordercolor:lightgray;width: 90%" id="tablehang">
						<tr style="background-color: #d9eeeb;">
							<td width="3%"><input type="checkbox"></td>
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
					<button id="zgyzbt" onclick="checky()" class="btn btn-success"
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
						<tr style="height: 100px; font-size: 15px">
							<td style="width: 35%; color: red;" align="right">融e联绑定的手机号</td>
							<td style="width: 3%"></td>
							<td style="width: 20%; text-align: center"><input
								type="text" id="user_mobile2" style="width: 100px; height: 30px"
								id="user_mobile2"></td>
							<td style="width: 3%"></td>
							<td style="color: lightseagreen;">验证码：<input type="text"
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
		<!-- /.modal -->
	</div>
	<input type="hidden" id='xmidval' value="<%=xm_id %>">
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
	<script src="<%=CONTEXT_PATH%>/sy/base/frame/coms/tree/jquery.tree.js"></script>
	

	<%-- //模块改变事件
	function typeId(obj){
		var tab = document.getElementById("tableid");
	    //表格行数
	    var rows = tab.rows.length;
	    if(rows>1){
		var mkvalue= document.getElementById("mkid").value;
		var param = {};
		param["MK"]=mkvalue;
		param["lbname"]="<%=lbname%>";
		param["xlname"]="<%=xlname%>";
		param["xm_id"]="<%=xm_id%>";
		var ww= FireFly.doAct("TS_BMLB_BM", "getMkvalue", param,true,false);
		hh= ww.list;
		var tyArray = hh.split(",");
		var select = document.getElementById("lxid");
		jQuery("#lxid").empty();          //把select对象的所有option清除掉
		for(var i=0;i<tyArray.length;i++){
			select.options[i]=new Option((tyArray[i]=="1")?"初级":(tyArray[i]=="2")?"中级":(tyArray[i]=="3")?"高级":"无",tyArray[i]);
		}
		var tab = document.getElementById("tableid");
	    //表格行数
	    var rows = tab.rows.length;
	    if(rows>1){
			yk["BM_LB"]="<%=lbcode%>";
			yk["BM_XL"]="<%=xlcode%>";
			yk["ID"]=document.getElementById("zglbid").value;
			yk["BM_MK"]=mkvalue;
			var sel = document.getElementById("lxid");
			var selected_val = sel.options[sel.selectedIndex].value;
			yk["BM_TYPE"]=selected_val;
	       }
		}
	}
	 --%>
	
	
	
	
	<%-- //进行资格验证
	function checky(){
		var param = {};
		var bminfo={};
		bminfo['XM_ID'] = '<%=xm_id%>';
		bminfo['BM_CODE'] = '<%=user_code%>';
		bminfo['BM_STARTDATE'] = '<%=bm_start%>';
		bminfo['BM_ENDDATE'] = '<%=bm_end%>';
// 		xkArg.push(yk);
		var neAry=xkArg;
		if(yk.ID){
			neAry=xkArg.concat(yk);	
		}
		//数组去重
		for(var i=0; i < neAry.length; i++) {
		    for(var j=i+1;j< neAry.length; j++) {
		        if(neAry[i].ID == neAry[j].ID) {
		        	neAry.splice(neAry[j],1);
		        }
		    }
		}
		param['BM_INFO'] = JSON.stringify(bminfo);
		param['BM_LIST'] = JSON.stringify(neAry);
		console.log(JSON.stringify(param));
		FireFly.doAct("TS_XMGL_BMSH", "vlidates", param, true,false,function(data){
    		console.log(data);
    		yzgz=data;
    		//获取后台传过来的key
    		var zgArray = document.getElementsByName("zgksname");
         	for(var i=0;i<zgArray.length;i++){
             	//获取验证规则div的id
       			var a=zgArray[i].value;
       			//获取验证结果div的id
             	var yzjg=a+"yzjg";
       			var dataArray =data[a];
            	//获取div对应的数组
       			//append内容之前判断是否有内容
       			var divtext1 = $("#"+a).html();
       			if(divtext1==null||divtext1.length==0){
       				var shArray=true;
       				for(var j=0;j<dataArray.length;j++){
       					
       					if(dataArray[j].VLIDATE=="true"){
	       					$("#"+a).append('<div><img src="<%=CONTEXT_PATH%>/ts/image/u4719.png">'+dataArray[j].NAME+'</div>');
	       					
						}if(dataArray[j].VLIDATE=="false"){
							$("#"+a).append('<div style="color:red;"><img src="<%=CONTEXT_PATH%>/ts/image/u4721.png">'+dataArray[j].NAME+'</div>');
						}
						if(dataArray[j].VLIDATE=="false"){
							shArray=false;
						}
					 }
       				if(shArray==false){
       					$("#"+yzjg).append('审核不通过');
       				
       				}if(shArray==true){
       					$("#"+yzjg).append('审核通过');
       				}
	       		}
       			
       		}
    	});	
	} --%>
	<%-- //提交所有数据
	function mttijiao(){
		checky();
		//获取手机号码
		var ryl_mobile = document.getElementById("user_mobile2").value
		//获取到资格考试类型主键id
		var zgArray = document.getElementsByName("zgksname");
		var zglb="";
     	for(var i=0;i<zgArray.length;i++){
   			if(i==0){
   				zglb = zgArray[i].value;
   			}else{
   				zglb +="," + zgArray[i].value;
   			}
   		}
     	var param = {};
// 		xkArg.push(yk);
		var neAry;
		var neAry=xkArg;
		if(yk.ID){
			neAry=xkArg.concat(yk);	
		}
		
		for(var i=0; i < neAry.length; i++) {
		    for(var j=i+1;j< neAry.length; j++) {
		        if(neAry[i].ID == neAry[j].ID) {
		        	neAry.splice(neAry[j],1);
		        }
		    }
		}
		param["USER_CODE"] = "<%=user_code%>";
		param["USER_NAME"] = "<%=user_name%>";
		param["USER_SEX"] = "<%=user_sex%>";
		param["ODEPT_NAME"] = "<%=odept_name%>";
		param["USER_OFFICE_PHONE"] = "<%=user_office_phone%>";
		param["USER_MOBILE"] = ryl_mobile;
		param["USER_CMPY_DATE"] = "<%=user_cmpy_date%>";
		param["XM_ID"] = "<%=xm_id%>";
		param["BM_START"] = "<%=bm_start%>";
		param["BM_END"] = "<%=bm_end%>";
		param["XM_NAME"] = "<%=xm_name%>";
			param['BM_LIST'] = JSON.stringify(neAry);
			param["YZGZ_LIST"] = JSON.stringify(yzgz);
			//本序列表格行数
			var bxltabObj = document.getElementById("tableid");
			var bxlrows = bxltabObj.rows.length;
			//跨序列表格行数
			var kxlObj = document.getElementById("tablehang");
			var kxlrows = kxlObj.rows.length;
			if (bxlrows < 2 && kxlrows < 2) {
				alert("您没有选择考试")
			}
			if (bxlrows != 1 || kxlrows != 1) {
				if (ryl_mobile == "") {
					alert("手机号码不能为空");
				}
				if (ryl_mobile != "" && ryl_mobile != null) {
					var BM_ID = FireFly.doAct("TS_BMLB_BM", "addZgData", param,
							true, false);
					showMask();
					console.log(JSON.stringify(BM_ID.strresult));
					if (BM_ID.strresult != null || BM_ID.strresult != "") {
						hideMask();
					}
					window.location.href = "bm.jsp";
				}
			}
		} --%>
	<script src="<%=CONTEXT_PATH%>/ts/js/bmzgks.js"></script>
</body>
</html>
