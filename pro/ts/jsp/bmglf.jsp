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
	<style>.a table tr{height:50px;padding-left: 10px;}
	.btn-default{
	color: WHITE;
	border-color:#245580;
	}
	</style>
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
		<div class="" style="padding: 10px">
		<a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img style="padding-bottom: 10px ;color: #388CAE;" src="<%=CONTEXT_PATH%>/ts/image/Home_16x16.png" id="shouye"></a>
		 <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;非资格报名</span>
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
							<td colspan="5"><span id="deptspan"><%=odept_name %></span></td>
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
       			<button onclick="tijiaofzg()" class="btn btn-success" style="width:100px;background-color: #00c2c2;">提交</button>
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			<button  onclick="fanhui()" class="btn btn-success" style="width:100px;background-color: #00c2c2;">返回</button>	
       		</div>
       		
       	</div>
       	</div>
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
		</div>
    </div>	
	<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
	<!-- Bootstrap 3.3.6 -->
	<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript">
		function fanhui(){
			window.history.go(-1);
		}
	</script>
	<script>
	var result =  FireFly.byId("SY_HRM_ZDSTAFFPOSITION", "<%=user_code%>");
	if(result!=null){
		 STATION_TYPE_CODE=result.STATION_TYPE_CODE;
		 STATION_TYPE = result.STATION_TYPE;
		 STATION_NO = result.STATION_NO;
		 STATION_NO_CODE= result.STATION_NO_CODE;
		 ADMIN_DUTY = result.ADMIN_DUTY;
		 DUTY_LEVEL_CODE = result.DUTY_LV_CODE;
		 $("#gwlb").html(STATION_TYPE);
		 $("#gwxl").html(STATION_NO);
		 $("#zwcj").html(ADMIN_DUTY);
		 
	}
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
			var bmlb="";
		function tijiaofzg(){
			var bmArray = document.getElementsByName("checkname");
			
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
		     	 }
		     	var param={};
		     	param["ids"]=bmlb;
		     	var result = FireFly.doAct("TS_BMLB_BM","pdfzg",param);
		     	if(result.flag=="false"){
		     		alert("您已重复报名")
		     		return
		     		
		     	}else{
		     		var arrChk=$("input[name='checkname']:checked"); 
					tbody=document.getElementById("xinxi");
					for(var i=0;i<arrChk.length;i++){
					 //得到tr
					  var tr=arrChk[i].parentNode.parentNode;
				      var tds=tr.getElementsByTagName("td");
				      
				      var ntr = tbody.insertRow();
				      
				      if(i==0){
					      ntr.innerHTML=
					      '<td style="text-align:right;color:lightseagreen">报考名称</td>'+
					      '<td style="text-align:center">'+tds[1].innerHTML+'</td>';
					    
				     	}else{
				    	   ntr.innerHTML=
						       '<td style="text-align:center;color:blue"></td>'+
						       '<td style="text-align:center">'+tds[1].innerHTML+'</td>';
				       }
					}
		     		$("#tiJiao").modal().show;
		     	}
		}
		function quxiao(){
			//获取到table
			var motaitable = document.getElementById("motaitable");
			var rowlength = motaitable.rows.length-1;
			for(var i=rowlength;i>1;i--){
				motaitable.deleteRow(i);
			}
		}
		
		//提交所有数据
		function mttijiao(){
			//获取手机号码
			var ryl_mobile = document.getElementById("user_mobile2").value
			
					if (ryl_mobile == "") {
						alert("手机号码不能为空");
					}
			  var user_mobile = document.getElementById("user_mobile1").value;
				var param={};
				param["bmCodes"] = bmlb;
				param["USER_CODE"]="<%=user_code%>";
				param["USER_NAME"] = "<%=user_name%>";
				param["USER_SEX"] = "<%=user_sex%>";
				param["ODEPT_NAME"] = "<%=odept_name%>";
				param["USER_OFFICE_PHONE"] = "<%=user_office_phone%>";
				param["USER_MOBILE"] = ryl_mobile;
				param["USER_CMPY_DATE"] = "<%=user_cmpy_date%>";
				param["XM_ID"] = "<%=xm_id%>";
				FireFly.doAct("TS_BMLB_BM", "addData", param,true,false);
				window.location.href = "bm.jsp";
			}
		  
	</script>
</body>
</html>
