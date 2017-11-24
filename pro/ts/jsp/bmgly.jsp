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
<title>验证规则</title>
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport">

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
<body class="hold-transition skin-black sidebar-mini" onload="startYanzheng()">
<style>.a table tr{height:40px;padding-left: 10px;}</style>
	
	<% 	
		String  czglb= request.getParameter("zglb");
		String [] czgArray= czglb.split(",");
		String  user_mob= request.getParameter("usermob");
		String  xm_id= request.getParameter("xmid");
		String  xm_name= request.getParameter("xmname");
		String  bm_start= request.getParameter("bmstart");
		String  bm_end= request.getParameter("bmend");
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
	<div style="width: 90%;text-align: center;padding: 20px;">
		<div style="text-align: left;">
			<table  style="width: 90%;">
       			<tr>
       				<td><span>正在报名请稍后。。。</span></td>
       			</tr>
       		</table>
		</div>
		<div class="a">
			<table border="1" style="width: 90%;" id="tableId">
       			<tr style="background-color: #dfdfdf;">
       				<td style="width: 45%;"><span>报名</span></td>
       				<td style="width: 25%;"><span>验证规则</span></td>
       				<td style="width: 30%;"><span>验证结果</span></td>
       			</tr>
       			<% 
							Bean cbean1 = ServDao.find("TS_XMGL_BM_KSLBK", "1HjT0eSXZ5MauSymtKSE");
	       					String ckslbk_id = cbean1.getStr("KSLBK_ID");
		       				String ckslbk_name = cbean1.getStr("KSLBK_NAME");
		       				String ckslbk_xl = cbean1.getStr("KSLBK_XL");
		       				String ckslbk_mk = cbean1.getStr("KSLBK_MK");
		       				String ckslbk_type = cbean1.getStr("KSLBK_TYPE");
		       				String allk = ckslbk_name+"-"+ckslbk_xl+"-"+ckslbk_mk+"-"+ckslbk_type;
				%>
						<tr>
							<td width="15%"><%=allk%></td>
							<td style="padding-left: 30px;width: 15%;text-align: left;">
									<span id="ks01"><img src="./ts/image/u4717.png"></span>禁考规则<br>
									<span id="ks02"><img src="./ts/image/u4717.png"></span>准入测试规则<br>
									<span id="ks03"><img src="./ts/image/u4717.png"></span>本序列持证规则<br>
									<span id="ks04"><img src="./ts/image/u4717.png"></span>多次考试规则<br>
									<span id="ks05"><img src="./ts/image/u4717.png"></span>证书规则<br>
									<span id="ks06"><img src="./ts/image/u4717.png"></span>岗位规则<br>
							</td>
							<td width="15%">2222</td>
						</tr>
					
       				<% 
       				for(int i=0;i<czgArray.length;i++){
						String cwhere = "AND KSLB_ID="+"'"+czgArray[i]+"'";
						List<Bean> cbeanList = ServDao.finds("TS_XMGL_BM_KSLB", cwhere);
						for(int j=0;j<cbeanList.size();j++){
	       					Bean cbean = cbeanList.get(j);
	       					String ckslb_id = cbean.getStr("KSLB_ID");
		       				String ckslb_name = cbean.getStr("KSLB_NAME");
		       				String ckslb_xl = cbean.getStr("KSLB_XL");
		       				String ckslb_mk = cbean.getStr("KSLB_MK");
		       				String ckslb_type = cbean.getStr("KSLB_TYPE");
		       				String all = ckslb_name+"-"+ckslb_xl+"-"+ckslb_mk+"-"+ckslb_type;
					%>
						<tr>
							<td width="15%"><%=all%></td>
							<td style="padding-left: 30px;width: 15%;text-align: left;">
									<span id="ks01<%=i%>"><img src="./ts/image/u4717.png"></span>禁考规则<br>
									<span id="ks02<%=i%>"><img src="./ts/image/u4717.png"></span>准入测试规则<br>
									<span id="ks03<%=i%>"><img src="./ts/image/u4717.png"></span>本序列持证规则<br>
									<span id="ks04<%=i%>"><img src="./ts/image/u4717.png"></span>多次考试规则<br>
									<span id="ks05<%=i%>"><img src="./ts/image/u4717.png"></span>证书规则<br>
									<span id="ks06<%=i%>"><img src="./ts/image/u4717.png"></span>岗位规则<br>
							</td>
							<td width="15%">2222</td>
							<td class="rhGrid-td-hide"><input type="text" name="zgksname" value="<%=ckslb_id%>"></td>
						</tr>
					<%
						}
       				}
					%>
       			
       		</table>
		</div>
		<div style="height: 100px;padding: 20px;">
       			<button class="btn btn-success" style="width:100px;background-color: #00c2c2;" data-toggle="modal"  onclick="zglbTiJiao()">确认</button>
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			<button  onclick="fanhui()" class="btn btn-success" style="width:100px;background-color: #00c2c2;">取消</button>	
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
	<script type="text/javascript">
		//提交通过验证的信息
		function zglbTiJiao(){
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
			
		var param={};
			param["user_code"]="<%=user_code%>";
			param["user_name"] = "<%=user_name%>";
			param["user_sex"] = "<%=user_sex%>";
			param["odept_name"] = "<%=odept_name%>";
			param["user_office_phone"] = "<%=user_office_phone%>";
			param["user_mobile"] = "<%=user_mob%>";
			param["user_cmpy_date"] = "<%=user_cmpy_date%>";
			param["bmCodes"] = zglb;
			param["xm_id"] = "<%=xm_id%>";
			param["bm_start"] = "<%=bm_start%>";
			param["bm_end"] = "<%=bm_end%>";
			param["xm_name"] = "<%=xm_name%>";
			if(zgArray.length >0){
		 		FireFly.doAct("TS_BMLB_BM", "addZgData", param);
		 		FireFly.doAct("TS_BMLB_BM", "addZgDataOne", param);
		 		window.location.href="bm.jsp";
			}else{
		 		FireFly.doAct("TS_BMLB_BM", "addZgDataOne", param);
		 		window.location.href="bm.jsp";
			}
		}
	function startYanzheng(){
		//表格行数 
		var hangShu = document.getElementById("tableId").rows.length;
		var t1=setTimeout("tongGuo1()",1500);
		var t2=setTimeout("tongGuo2()",1800);
		var t3=setTimeout("tongGuo3()",2100);
		var t4=setTimeout("tongGuo4()",2400);
		var t5=setTimeout("tongGuo5()",2700);
		var t6=setTimeout("tongGuo6()",3000);
		if(hangShu>2){
			var t7=setTimeout("startYanzheng1()",3300);
		}else{
			clearTimeout();
		}
	}
	function startYanzheng1(){
		var t1=setTimeout("weiTongGuo1()",0)
		var t2=setTimeout("weiTongGuo2()",1500)
		var t3=setTimeout("weiTongGuo3()",1800)
		var t4=setTimeout("weiTongGuo4()",2100)
		var t5=setTimeout("weiTongGuo5()",2400)
		var t6=setTimeout("weiTongGuo6()",2700);
		
	}
	
	function tongGuo1(){
		document .getElementById ("ks010").innerHTML = "<img src='<%=CONTEXT_PATH%>/ts/image/u4719.png'>";
	}
	function tongGuo2(){
		document .getElementById ("ks020").innerHTML = "<img src='<%=CONTEXT_PATH%>/ts/image/u4719.png'>";
	}
	function tongGuo3(){
		document .getElementById ("ks030").innerHTML = "<img src='<%=CONTEXT_PATH%>/ts/image/u4719.png'>";
	}
	function tongGuo4(){
		document .getElementById ("ks040").innerHTML = "<img src='<%=CONTEXT_PATH%>/ts/image/u4719.png'>";
	}
	function tongGuo5(){
		document .getElementById ("ks050").innerHTML = "<img src='<%=CONTEXT_PATH%>/ts/image/u4719.png'>";
	}
	function tongGuo6(){
		document .getElementById ("ks060").innerHTML = "<img src='<%=CONTEXT_PATH%>/ts/image/u4719.png'>";
	}
	
	
	
	
	function weiTongGuo1(){
		document .getElementById ("ks011").innerHTML = "<img src='<%=CONTEXT_PATH%>/ts/image/u4721.png'>";
	}
	function weiTongGuo2(){
		document .getElementById ("ks021").innerHTML = "<img src='<%=CONTEXT_PATH%>/ts/image/u4721.png'>";
	}
	function weiTongGuo3(){
		document .getElementById ("ks031").innerHTML = "<img src='<%=CONTEXT_PATH%>/ts/image/u4721.png'>";
	}
	function weiTongGuo4(){
		document .getElementById ("ks041").innerHTML = "<img src='<%=CONTEXT_PATH%>/ts/image/u4721.png'>";
	}
	function weiTongGuo5(){
		document .getElementById ("ks051").innerHTML = "<img src='<%=CONTEXT_PATH%>/ts/image/u4721.png'>";
	}
	function weiTongGuo6(){
		document .getElementById ("ks061").innerHTML = "<img src='<%=CONTEXT_PATH%>/ts/image/u4721.png'>";
	}
	</script>
</body>
</html>
