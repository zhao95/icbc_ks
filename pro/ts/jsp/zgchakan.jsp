<!DOCTYPE html>
<%@page import="com.rh.core.serv.ServDao"%>
<%@page import="com.rh.core.org.mgr.UserMgr"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	<style>.a table tr{height:40px;padding-left: 10px;}</style>
	<style>.a table td{padding-left: 20px;}</style>
	<style>.b table tr{height:30px;}</style>
	<style>.zgks table td{padding-left: 5px;}</style>
	<% 
		//获取项目id
		String bmid = request.getParameter("bmid4");
		Bean bmbean = ServDao.find("TS_BMLB_BM",bmid);
		if(bmbean.isEmpty()){
			return;
		}
		String xm_id = bmbean.getStr("XM_ID");
		Bean xmbean=ServDao.find("TS_XMGL", xm_id);
		if(xmbean.isEmpty()){return;}
		String xm_name = xmbean.getStr("XM_NAME");
		//获取项目id
		String where1 = "AND XM_ID="+"'"+xm_id+"'";
		List<Bean> bmglList = ServDao.finds("TS_XMGL_BMGL", where1);
		if(bmglList.size()==0){
			return;
		}
		String bm_id = bmglList.get(0).getStr("BM_ID");
		String bm_ksxz = bmglList.get(0).getStr("BM_KSXZ");
		String bm_start = bmglList.get(0).getStr("BM_START");
		String bm_end = bmglList.get(0).getStr("BM_END");
		String bm_name = bmglList.get(0).getStr("BM_NAME");
		//获取类型列表    
		String where2 = "AND BM_ID="+"'"+bm_id+"'";
		List<Bean> zgList = ServDao.finds("TS_XMGL_BM_KSLB", where2);
		//获取用户编码
		String user_code = userBean.getStr("USER_WORK_NUM");
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
		//职务
		String user_post =userBean.getStr("USER_POST");
		String wheregw = "AND POSTION_NAME="+"'"+user_post+"'";
		List<Bean> gwList = ServDao.finds("TS_ORG_POSTION", wheregw);
		String pt_type="";
		String pt_sequnce="";
		if(gwList.size()!=0){
		 pt_type=gwList.get(0).getStr("POSTION_TYPE");
		 pt_sequnce= gwList.get(0).getStr("POSTION_SEQUENCE");
			
		}
		
		%>
	<div style="padding-left: 90px;width: 90%;text-align: left;">
			<img alt="中国工商银行" src="<%=CONTEXT_PATH%>/qt/img/u3148.png"> <img alt="考试系统"src="<%=CONTEXT_PATH%>/qt/img/u3376.png">
	</div>
	<div style="background: #dfdfdf;padding-top: 10px"align="center">
       	<div id="" style="background: white;width: 90%;">
       		<div style="background: white;width: 90%;text-align: center">
       		<table  style="height: 50px;width: 90%;">
       			<tr>
       				<td><span style="font-size: 25px;color: #00C2C2;"><%=xm_name%></span></td>
       			</tr>
       		</table>
       		</div>
       	<div class="a">
       		<table  align="center" style="width: 90%;">
       			<tr>
       				<td colspan="4" height="120px"><p style="font-size: 15px;color:red;">报考须知，请仔细阅读！</p>
       				<p style="color: red;"> 报名时间：<%=bm_start%>~~<%=bm_end %></p>
       				<p style="color: red;"> <%=bm_ksxz%></p></td>
       			</tr>
       		</table>
       		<table  align="center" style="width: 90%;background-color: #fed1d1;">
       			<tr>
       				<td><span style="color: #ff0000;">！</span> 温馨提示：</td>
       				<td height="60px" align="left">
       				您当前在 <span style="color: #ff0000;"><%=odept_name%></span> ，将视您办公所在地统一安排考场。如果您发现下面的信息不符，<br>请于借考申请开放期间提交借考申请！
       				</td>
       			</tr>
       		</table>
       		<div style="padding-top: 10px;">
       		<table border="1" align="center" style="width: 90%;padding-top: 10px;">
       			<tr style="background-color: #f2f2f2;">
       				<td colspan="6">个人基本信息</td>
       			</tr>
       			<tr>
       				<td width="16.5%">人力资源编码</td>
       				<td width="16.5%" ><%=user_code %></td>
       				<td width="16.5%">姓名</td>
       				<td width="16.5%"><%=user_name%></td>
       				<td width="16.5%">性别</td>
       				<td width="17.5%">
       				<% if (user_sex == "1") { %>女<% } else { %>男<% } %>
       				</td>
       			</tr>
       			<tr style="background-color: #f7fdff;">
       				<td >所属机构</td>
       				<td colspan="5"><%=odept_name %></td>
       			</tr>
       			<tr>
       				<td width="16.5%">岗位类别</td>
       				<td width="16.5%" ><%=pt_type %></td>
       				<td width="16.5%">岗位序列</td>
       				<td width="16.5%"><%=pt_sequnce%></td>
       				<td width="16.5%">职务层级</td>
       				<td width="17.5%"><%=user_post%></td>
       			</tr>
       			<tr style="background-color: #f7fdff;">
       				<td width="16.5%">入行时间</td>
       				<td width="16.5%"><%=user_cmpy_date%></td>
       				<td width="16.5%">办公电话</td>
       				<td width="16.5%"><%=user_office_phone %></td>
       				<td width="16.5%">手机号码(<span style="color:red;">融e联绑定的手机号</span>)</td>
       				<td width="17.5%"><input type="text" id="user_mobile1" value=""></td>
       			</tr>
       		</table>
       		</div>
       		<div style="padding-top: 10px;">
       					<div style="padding-top: 10px;text-align: left;width:90%;"><span style="color: #ff0000">★ 已报名的考试</span>
       					<span style="color: #fdb64f;">(提示：)</span></div>
       					<table border="1" align="center" style="width: 90%;" id="tableid">
       						<tr style="background-color: #ffbdbd;">
	       						<td width="10%">岗位类别</td>
       							<td width="15%">序列</td>
       							<td width="27%">模块</td>
       							<td width="10%">级别</td>
       							<td width="20%">验证</td>
       							<td width="15%">验证结果</td>
       						</tr>
	      						<% 
	      						String lbname ="";
	      						 String xlname ="";
	      						 String mkname = "";
	      						 String jb = "";
	      						 String shstate = "";
	      						 String cxstate = "";
	      						  lbname = bmbean.getStr("BM_LB");
	      						 xlname= bmbean.getStr("BM_XL");
	      						 mkname = bmbean.getStr("BM_MK");
	      						 String jibietype = bmbean.getStr("BM_TYPE");
	      						 if("1".equals(jibietype)){
	      							 jb="初级";
	      						 }else if("2".equals(jibietype)){
	      							 jb="中级";
	      						 }else if("3".equals(jibietype)){
	      							 jb="高级";
	      						 }
	      						 
	      						 int bmshstate = bmbean.getInt("BM_SH_STATE");
	      						 if(bmshstate==1){
	      							 shstate = "恭喜您！审核已通过，请及时请假参加考试";
	      						 }else if(bmshstate==2){
	      							 shstate="不好意思！审核未通过，如有需要请及时上诉";
	      						 }else if(bmshstate==0){
	      							 shstate="已提交上诉申请，请耐心等待......";
	      						 }else if(bmshstate==3){
	      							 shstate="不好意思！审核未通过，您未获得考试资格";
	      						 }
	      						 String chexiao = bmbean.getStr("BM_STATE");
	      						 if("2".equals(chexiao)){
	      							 shstate = "您已取消此次考试，考试不能恢复，请等待下次考试......";
	      						 }
	      						 
	      						%>
       						<tr>
	      						<td width="10%"><%=lbname%></td>
	      						<td width="15%"><%=xlname%></td>
	      						<td width="15%"><%=mkname%></td>
	      						<td width="15%"><%=jb%></td>
	      						<td width="20%">
	      							<div>禁考规则</div>
									<div>准入测试规则</div>
									<div>本序列持证规则</div>
									<div>多次考试规则</div>
									<div>证书规则</div>
									<div>岗位规则</div>
	      						</td>
	      						<td><%=shstate %></td>
	      						
       						</tr>
       						
       					</table>
       					</div>
       					<div style="padding-top:20px;padding-bottom:20px">
       			<button  onclick="goBack()" class="btn btn-success" style="width:100px;background-color: #00c2c2;">返回</button>	
       			</div>
       		</div>
       	</div>
       	</div>
  	
	
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
	<script type="text/javascript">
	function goBack(){
		window.history.go(-1);
	}
	alert("<%=bmid%>");
	$(function(){
		var bm = "<%=lbname%>";
		var ksmc = "<%=bm_ksxz%>";
		if(bm==""){
			//非资格
			$("#tableid").html("");
			$("#tableid").append("<thead><tr><th>考试名称</th><th>考试时间</th><th>审核结果</th></tr></thead>");
			$("#tableid").append('<tbody><tr><td>'+ksmc+'</td><td></td><td>'+shstate+'</td></tr></tbody>')
			
		}
	});
	</script>
	
	
</body>
</html>
