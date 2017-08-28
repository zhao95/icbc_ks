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
<title>我的请假查看</title>
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
<style>.bkuan table td{height: 50px;}</style>
	<% 
	//获取请假id和一个状态
	String  qj_id = request.getParameter("qjid");
	String  todo_id = request.getParameter("todoid");
	String  done_id = request.getParameter("doneid");
	//根据请假id获取请假服务，获取请教列表信息
	Bean qjbean = ServDao.find("TS_QJLB_QJ", qj_id);
		String qj_title = qjbean.getStr("QJ_TITLE");
		String qj_ksname = qjbean.getStr("QJ_KSNAME");
		String [] bmidArray= qj_ksname.split(",");
		String qj_reason = qjbean.getStr("QJ_REASON");
		String s_atime = qjbean.getStr("S_ATIME");
		String qj_status = qjbean.getStr("QJ_STATUS");
		String qj_danwei = qjbean.getStr("QJ_DANWEI");
		String qj_name = qjbean.getStr("QJ_NAME");
		//获取人力资源编码
		String user_code = userBean.getStr("USER_CODE");
		//获取用户登录名
		String user_login_name = userBean.getStr("USER_LOGIN_NAME");
		//获取用户部门名称
		String dept_name = userBean.getDeptName();
		//获取用户名
		String user_name = userBean.getStr("USER_NAME");
		%>
	<div style="padding-left: 15%;width: 90%;text-align: left;">
			<img alt="中国工商银行" src="<%=CONTEXT_PATH %>/qt/img/u3148.png"> <img alt="考试系统"src="<%=CONTEXT_PATH %>/qt/img/u3376.png">
	</div>
	<div style="background-color: #dfdfdf;padding-top: 10px;padding-left: 10%;padding-right: 10%;padding-bottom: 10px;">
       <div  style="padding-left: 10px;">
       		<table id="ybmtable" style="padding: 10px;width:100%;background-color: #5ab6a6;height: 80px;">
					<tr style="backGround-color: #ababab; height: 30px">
						<td style="text-align: center;">
						<img alt="u5520" src="<%=CONTEXT_PATH %>/ts/image/u5522.png">
						<span style="font-size: 25px;">&nbsp;&nbsp;填写申请单&nbsp;&nbsp;</span>
						<img alt="u5522" src="<%=CONTEXT_PATH %>/ts/image/u5520.png">
						<img alt="u5532" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">
						<span style="font-size: 25px;">&nbsp;&nbsp;部门领导审批&nbsp;&nbsp;</span>
						<img alt="u5524" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">
						<img alt="u5532" src="<%=CONTEXT_PATH %>/ts/image/u5524.png">
						<span style="font-size: 25px;">&nbsp;&nbsp;考务管理人员审批</span>
						</td>
					</tr>
			</table>
		</div>
		<div style="padding-left: 10px;">
			<table id="ybmtable" style="padding: 10px;width:100%;background-color: #ffffff;">
					<tr >
						<td style="text-align: center;padding-top: 5px;"><input type="text" disabled="disabled" style="width:70%;height:40px;background-color: #fed1d1;border-color: red;" value="！ 温馨提示：您今年已请假 2 次，还可请假 1 次。满 3 次后，本年度将不允许再请假。请合理使用请假次数！"/></td>
					</tr>
					<tr>
						<td style="text-align: left;padding-top: 5px;padding-left: 2%;"><span style="font-size: 25px;">请假申请</span><br/>
						<img alt="u5532" data-toggle="modal" data-target="#myModal" src="<%=CONTEXT_PATH %>/ts/image/u5540.png">
						</td>
					</tr>
			</table>
		</div>
		<div class="bkuan" style="padding-left: 10px;">
			<table  style="padding: 10px;width:100%;background-color: #ffffff;">
					<tr>
						<td style="width: 10%;text-align: right;">请假标题&nbsp;&nbsp;</td>
						<td colspan="3"><input style="width: 90%;height: 40px;" id="qjtitle" value="<%=qj_title%>" readonly></td>
					</tr>
					<tr>
						<td style="width: 10%;text-align: right;">请假的考试&nbsp;&nbsp;</td>
						<td colspan="3"><img alt="选择"  src="<%=CONTEXT_PATH %>/ts/image/uwxz.png"></td>
					</tr>
					<tr>
							<td style="width: 10%;"></td>
							<td colspan="3">
								<table   border="1" style="width: 95%;background-color: #f0f0f0;border-color: white;" id="tablelbId">
									<tr style="padding-left: 5px;text-align: center">
										<td width="35%">考试名称</td>
										<td width="35%">考试开始时间</td>
										<td width="30%"></td>
									</tr>
									<% 
									
									for(int i=0;i<bmidArray.length;i++){
										String cwhere = "AND BM_ID="+"'"+bmidArray[i]+"'";
										List<Bean> cbeanList = ServDao.finds("TS_QJLB_BM", cwhere);
										for(int j=0;j<cbeanList.size();j++){
											Bean bmbean = cbeanList.get(j);
											String lb_id = bmbean.getStr("LB_ID");
											String bm_id = bmbean.getStr("BM_ID");
											String lb_title = bmbean.getStr("LB_TITLE");
											String lb_date = bmbean.getStr("LB_DATE");
											
									%>
										<tr style="padding-left: 5px;text-align: center">
											<td class="rhGrid-td-hide"><%=bm_id%></td>
											<td class="rhGrid-td-hide"><input type="text" name="bmids" id="tjid" value="<%=bm_id%>"></td>
											<td width="35%"><%=lb_title%></td>
											<td width="35%"><%=lb_date%></td>
											<td class="rhGrid-td-hide"><input type="text" name="lbids"  value="<%=lb_id%>"></td>
											<td></td>
										</tr>
									<%
										}
									}
									%>
								</table>
							</td>
						</tr>
					<tr>
						<td style="width: 10%;text-align: right;">请假人&nbsp;&nbsp;</td>
						<td style="width: 15%;"><input style="width: 90%;height: 40px;" value="<%=qj_name%>" readonly></td>
						<td style="width: 45%;">部门&nbsp;&nbsp;<input style="width: 90%;height: 40px;" id="bumen" value="<%=qj_danwei %>" readonly></td>
						<td style="width: 30%;">创建时间&nbsp;&nbsp;<input style="height: 40px;" id="satime" value="<%=s_atime %>" readonly></td>
					</tr>
					<tr>
						<td style="width: 10%;text-align: right;">请假事由&nbsp;&nbsp;</td>
						<td colspan="3"><textarea rows="3" cols="130" id="qjreason" readonly><%=qj_reason%></textarea></td>
					</tr>
					<tr>
						<td style="width: 10%;text-align: right;">证明材料&nbsp;&nbsp;</td>
						<td colspan="3"><img alt="上传" onclick="shangchuan()" src="<%=CONTEXT_PATH %>/ts/image/uqjsc.png"></td>
					</tr>
					<tr>
						<td style="width: 10%;"></td>
						<td style="width: 25%;text-align: right;"></td>
						<td colspan="2"><a>下载</a>&nbsp;&nbsp;<a>删除</a></td>
					</tr>
			</table>
		</div>
		<div  class="bkuan" style="padding-left: 10px;">
			<table  style="padding: 10px;width:100%;background-color: #ffffff;text-align: center;">
				<tr>
					<td>
		       			<div  id="shaddid" style="padding-left: 10px;">
						<table  style="width:90%;background-color: #ffffff;text-align: center;">
								<tr>
									<td align="right" style="width: 30%;">审核人姓名:</td>
									<td style="width: 5%;"></td>
			       					<td align="left"><input type="text" id="shname" value="<%=user_name%>"></td>
			       				</tr>
			       				<tr>
									<td align="right" style="width: 30%;">审核状态:</td>
									<td style="width: 5%;"></td>
									<td align="left"><input type="radio" name="sh_status" value="1">同意
									&nbsp;&nbsp;&nbsp;&nbsp;
									<input type="radio" name="sh_status" value="2">不同意</td>
			       				</tr>
			       				<tr>
									<td align="right" style="width: 30%;">审核理由:</td>
									<td style="width: 5%;"></td>
									<td align="left"><textarea rows="3" cols="60" id="shreason" ></textarea></td>
			       				</tr>
			       		</table>
						</div>
	     			</td>
	     		</tr>
	     		<tr>
					<td>
		       			<div id="xyhjid" class="bkuan" style="padding-left: 10px;">
						<table  style="padding: 10px;width:80%;background-color: #ffffff;text-align: center;">
							<tr>
								<td>
					       			<button onclick="bcnext()"  class="btn btn-success" style="width:120px;background-color: #00c2c2;" >送下一环节审核</button>
		       					</td>
		       					<td>
					       			<button  onclick="tuihui()" class="btn btn-success" style="width:100px;background-color: #00c2c2;">退回</button>
		       					</td>
		       				</tr>
			       		</table>
						</div>
	     			</td>
	     		</tr>
	     		<tr>
					<td>
		       			<div  style="padding-left: 10px;" id="shxxid">
						<table  style="width:90%;background-color: #ffffff;text-align: center;">
	       					<tr>
	       					<td>
	       					<% 
								String cwhere1 = "AND DATA_ID="+"'"+qj_id+"'";
								List<Bean> shbeanList = ServDao.finds("TS_COMM_MIND", cwhere1);
								for(int j=0;j<shbeanList.size();j++){
									Bean shbean = shbeanList.get(j);
									String sh_id = shbean.getStr("MIND_ID");
									String sh_status = shbean.getStr("SH_STATUS");
									String sh_reason = shbean.getStr("MIND_CONTENT");
									String shs_atime = shbean.getStr("S_ATIME");
									String shs_dept = shbean.getStr("S_DEPT");
									String sh_node = shbean.getStr("SH_NODE");
							%>
								<table style="width:100%;background-color: #ffffff;">
								<tr>
									<td colspan="4"><%=sh_node%></td>
			       				</tr>
			       				<tr>
									<td rowspan="2"><img width="55px" height="55px"></td>
			       					<td><%=user_name%></td>
			       					<td><%=sh_status%></td>
			       					<td><%=sh_reason%></td>
			       				</tr>
			       				<tr>
			       					<td colspan="2"><%=shs_atime%></td>
			       					<td><%=shs_dept%></td>
			       				</tr>
			       				
			       				</table>
							<%
							}
							%>
							</td>
							</tr>
			       		</table>
						</div>
	     			</td>
	     		</tr>
       		</table>
		</div>
		<div class="bkuan" id="fhid" style="padding-left: 10px;">
			<table  style="padding: 10px;width:100%;background-color: #ffffff;text-align: center;">
					<tr>
						<td>
			       			<button  onclick="fanhui()" class="btn btn-success" style="width:100px;background-color: #00c2c2;">返回</button>
       					</td>
       				</tr>
       		</table>
		</div>
    </div>
   	
  	<script type="text/javascript">
  	$(function () {
  		doubleBgColor(document.getElementById("ybmtable"),"#f0faff","#ffffff")
  		 if (<%=shbeanList%> == null) {
		    	//设置div签隐藏
		    	document.getElementById("shxxid").style.display="none";
		    }else if(<%=todo_id%> == null) {
		    	//设置div签隐藏
		    	document.getElementById("xyhjid").style.display="none";
		    	document.getElementById("shaddid").style.display="none";
		    }
		
 	 }); 
  	//表格颜的设置
  	function doubleBgColor(Table,Bg1,Bg2) {  
  		for (var i=1;i<Table.rows.length;i++) Table.rows[i].bgColor=i%2?Bg2:Bg1;  
  	}  
  	</script>
  	<script type="text/javascript">
  		function fanhui(){
  			window.history.go(-1);
  		}
  		//修改请假
  		function bcnext(){
  			var staArray =document.getElementsByName("sh_status");
  			var shname =document.getElementById("shname").value;
  			var shreason =document.getElementById("shreason").value;
  			var shstatus="";
  				for(i=0;i<staArray.length;i++){
  					if(staArray[i].checked){
  						shstatus=staArray[i].value;
  					}
  				}
  				if(shstatus=="不同意"){
  					
  				var shsta="1";
  				var	qjstatus="2";
	 			var param={};
	 			param["qjid"]="<%=qj_id%>";
	 			param["qjstatus"]=qjstatus;
	 			param["shreason"]=shreason;
	 			param["shstatus"]=shsta;
				param["userloginname"] = "<%=user_login_name%>";
				param["deptname"] = "<%=dept_name%>";
				param["usercode"] = "<%=user_code%>";
				FireFly.doAct("TS_QJLB_QJ", "updateData", param,);
				window.location.href="qjlb.jsp";
  				}
  		}
  		
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
