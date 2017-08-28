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
<title>我的请假</title>
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
	
	String user_code = userBean.getStr("USER_CODE");
	//获取用户名称
	String user_name = userBean.getStr("USER_NAME");
	//获取用户性别
	String user_sex = userBean.getStr("USER_SEX");
	//获取用户机构
	String dept_code = userBean.getStr("DEPT_NAME");
	//获取用户办公电话
	String user_office_phone = userBean.getStr("USER_OFFICE_PHONE");
	//获取用户手机号码
	String user_mobile = userBean.getStr("USER_MOBILE");
	//获取用户入行时间
	String user_cmpy_date =userBean.getStr("USER_CMPY_DATE");
	
	%>
	<div style="padding-left: 15%;width: 90%;text-align: left;">
			<img alt="中国工商银行" src="<%=CONTEXT_PATH %>/qt/img/u3148.png"> <img alt="考试系统"src="<%=CONTEXT_PATH %>/qt/img/u3376.png">
	</div>
	<div style="background-color: #dfdfdf;padding-top: 10px;padding-left: 10%;padding-right: 10%;padding-bottom: 10px;">
       <div  style="padding-left: 10px;">
       		<table id="ybmtable" style="padding: 10px;width:100%;background-color: #5ab6a6;height: 80px;">
					<tr style="backGround-color: #ababab; height: 30px">
						<td style="text-align: center;">
						<img alt="u5520" src="<%=CONTEXT_PATH %>/ts/image/u5520.png">
						<span style="font-size: 25px;">&nbsp;&nbsp;填写申请单&nbsp;&nbsp;</span>
						<img alt="u5522" src="<%=CONTEXT_PATH %>/ts/image/u5522.png">
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
						<td colspan="3"><input style="width: 90%;height: 40px;" id="qjtitle" value=""></td>
					</tr>
					<tr>
						<td style="width: 10%;text-align: right;">请假的考试&nbsp;&nbsp;</td>
						<td colspan="3"><img alt="选择" data-toggle="modal" data-target="#myModal" onclick="xuanze()" src="<%=CONTEXT_PATH %>/ts/image/uwxz.png"></td>
					</tr>
					<tr>
						<td style="width: 10%;"></td>
						<td colspan="3">
							<table   border="1" style="width: 95%;background-color: #f0f0f0;border-color: white;" id="tablelbId">
			       			<tr style="padding-left: 5px;text-align: center">
			   					<td width="35%">考试名称</td>
			   					<td width="35%">考试开始时间</td>
			   					<td width="30%">操作</td>
			      			</tr>
				      			<% 
				      			String  czglb= request.getParameter("bmids");
				      			String [] czgArray= czglb.split(",");
				      			for(int i=0;i<czgArray.length;i++){
									String cwhere = "AND BM_ID="+"'"+czgArray[i]+"'";
									List<Bean> cbeanList = ServDao.finds("TS_BMLB_BM", cwhere);
									for(int j=0;j<cbeanList.size();j++){
										Bean bmbean = cbeanList.get(j);
										String tj_id = bmbean.getStr("BM_ID");
										String tj_xl = bmbean.getStr("BM_XL");
										String tj_mk = bmbean.getStr("BM_MK");
										String tj_type = bmbean.getStr("BM_TYPE");
										String tj_date = bmbean.getStr("BM_ENDDATE");
										String tj_name = tj_type+"-"+tj_xl+"-"+tj_mk;
										
								%>
								<tr style="padding-left: 5px;text-align: center">
									<td class="rhGrid-td-hide"><%=tj_id%></td>
									<td class="rhGrid-td-hide"><input type="text" name="bmids" id="tjid" value="<%=tj_id%>"></td>
									<td width="35%"><%=tj_name%></td>
									<td width="35%"><%=tj_date%></td>
									<td><a href="#" onclick="delOne1(this)">删除</a></td>
								</tr>
								<%
									}
				      			}
								%>
			       			</table>
			       			<table  border="1" style="width: 95%;background-color: #f0f0f0;border-color: white;" id="tablehang">
			      			<tbody id="goods">
			      			
			      			</tbody>
			       			</table>
			       			
						</td>
					</tr>
					<tr>
						<td style="width: 10%;text-align: right;">请假人&nbsp;&nbsp;</td>
						<td style="width: 20%;"><input style="width: 90%;height: 40px;" value="<%=user_name%>" readonly></td>
						<td style="width: 70%;" colspan="3">部门&nbsp;&nbsp;<input style="width: 90%;height: 40px;" id="bumen" value="<%=dept_code %>"></td>
					</tr>
					<tr>
						<td style="width: 10%;text-align: right;">请假事由&nbsp;&nbsp;</td>
						<td colspan="3"><textarea rows="3" cols="130" id="qjreason"></textarea></td>
					</tr>
					<tr>
						<td style="width: 10%;text-align: right;">证明材料&nbsp;&nbsp;</td>
						<td colspan="3" style="padding-top: 15px;">
							<form action="/com/rh/core/FileServlet" method="post" id="imgformid" enctype="multipart/form-data">
							<div class="form-group" id="caseIma">
							<label class="btn btn-primary">选择图片
							<input type="file" style="display: none;" class="form-control" id="caseImage" name="caseImage" onchange="viewImage(this)"/>
							</label>
							</div>
							<input type = "submit" value="传递" style="display: none;"/>
							</form>
						</td>
					</tr>
					<tr>
						<td style="width: 10%;"></td>
						<td style="width: 25%;text-align: right;padding-top: 10px;">
     					<div id="localImag"><img id="preview" width=-1 height=-1 style="diplay:none" />
						</div>
						</td>
						<td colspan="2"><a>下载</a>&nbsp;&nbsp;<a>删除</a></td>
					</tr>
			</table>
		</div>
		<div class="bkuan" style="padding-left: 10px;">
			<table  style="padding: 10px;width:100%;background-color: #ffffff;text-align: center;">
					<tr>
						<td>
			       			<button data-toggle="modal" data-target="#tiJiao" class="btn btn-success" style="width:100px;background-color: #00c2c2;" >提交申请</button>
			       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			       			<button  onclick="fanhui()" class="btn btn-success" style="width:100px;background-color: #00c2c2;">返回</button>
       					</td>
       				</tr>
       		</table>
		</div>
    </div>
    <%--模态窗口查询请假考试--%>
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" style="background-color: #00c2c2;color: white">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
						&times;
					</button>
					<h5 class="modal-title">
						选择需要请假的考试
					</h5>
				</div>
				<div class="modal-body zgks">
					<table border="1" style="width: 100%;" id="tabletjId">
	       				<tr>
		       				<td style="text-align: center" width="10%"></td>
		       				<td width="10%" align="center">序号</td>
		       				<td width="55%" align="center">名称</td>
		       				<td width="35" align="center" colspan="2">考试时间</td>
		       			</tr>
		       			<%	String where1 = " AND BM_CODE='"+user_code+"'";
		       				List<Bean> bmglList = ServDao.finds("TS_BMLB_BM", where1);
							for(int i=0;i<bmglList.size();i++){
								Bean bean1 = bmglList.get(i);
								String bm_id = bean1.getStr("BM_ID");
								String bm_xl = bean1.getStr("BM_XL");
								String bm_mk = bean1.getStr("BM_MK");
								String bm_type = bean1.getStr("BM_TYPE");
								String ks_date = bean1.getStr("BM_ENDDATE");
								String qj_name = bm_type+"-"+bm_xl+"-"+bm_mk;
						%>
						<tr>
							<td style="text-align: center" width="10%"><input type="checkbox"  name="checkname1" value="<%=bm_id%>" ></td>
							<td align="center"><%=i+1%></td>
							<td align="center"><%=qj_name%></td>
							<td style="text-align: center"><%=ks_date%></td>
							<td class="rhGrid-td-hide"><%=bm_id%></td>
						</tr>
						<%
							}
						%>
	       			</table>
				</div>
				<div class="modal-footer" style="text-align: center;">
					<button type="button" class="btn btn-success" data-dismiss="modal" style="width:100px;background-color: #00c2c2;"  onclick="fuzhi()">确定</button>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	      					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<button type="button" class="btn btn-default" data-dismiss="modal" style="width:100px;background-color: #00c2c2;">返回
					</button>
				</div>
			</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>	
	<%--模态窗口回显审核人 --%>
    <div class="modal fade" id="tiJiao" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" style="background-color: #00c2c2;color: white">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
						&times;
					</button>
					<h5 class="modal-title">
						选择需要请假的考试
					</h5>
				</div>
				<div class="modal-body zgks">
					<table  style="width: 100%;height: 100px;border: 0;">
	       				<tr>
		       				<td style="text-align: center" width="10%">已经提交给？？？进行审核</td>
		       			</tr>
	       			</table>
				</div>
				<div class="modal-footer" style="text-align: center;">
					<button type="button" class="btn btn-success" data-dismiss="modal" style="width:100px;background-color: #00c2c2;"  onclick="baocun()">确定</button>
				</div>
			</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>	
  	<script type="text/javascript">
  	$(function () {
  		doubleBgColor(document.getElementById("ybmtable"),"#f0faff","#ffffff")
  		
 	 }); 
  	//表格颜的设置
  	function doubleBgColor(Table,Bg1,Bg2) {  
  		for (var i=1;i<Table.rows.length;i++) Table.rows[i].bgColor=i%2?Bg2:Bg1;  
  	}  
  	</script>
  	<script type="text/javascript">
  		function fanhui(){
  			window.location.href="qjlb.jsp";
  		}
  		//若果考试存在，就不能进行选择
  		function xuanze(){
  			var bmidsArray = document.getElementsByName("bmids");
  			var kslxArray = document.getElementsByName("checkname1");
  			
  			for(var i=0;i<kslxArray.length;i++){
  				for(var j=0;j<bmidsArray.length;j++){
		     		if(bmidsArray[j].value==kslxArray[i].value){
		     			kslxArray[i].disabled=true;
		     		}
  				}
  	     	}
  		}
  		//将选择的请假赋值给table表格
  		function fuzhi(){
  			var tab = document.getElementById("tablehang");
  	 	    var kslxArray = document.getElementsByName("checkname1");
  	     	for(var i=0;i<kslxArray.length;i++){
  	     		if(kslxArray[i].checked && !kslxArray[i].disabled){
  			       var tr=kslxArray[i].parentNode.parentNode;
  			       var tds=tr.getElementsByTagName("td");
  			       var xu_hao = tds[1].innerText;
  			       var qj_name =tds[2].innerText;
  			       var ks_date = tds[3].innerText;
  			       var bm_id = tds[4].innerText;
  			       tbody=document.getElementById("goods");
  			       var ntr = tbody.insertRow();
  			       ntr.innerHTML=
  			       '<td class="rhGrid-td-hide">'+bm_id+'</td>'+
  			       '<td align="center" width="35%">'+qj_name+'</td>'+
  			       '<td align="center" width="35%">'+ks_date+'</td>'+
  			       '<td align="center" width="35%"><a href="#" onclick="delOne(this)">删除</a></td>'+
  			       '<td class="rhGrid-td-hide"><input type="text" name="bmids" value="'+bm_id+'"></td>';
  			      	kslxArray[i].disabled=true;
  				}
  	     	}
  	     	
  		}
  		//删除已选择的请假
  		function delOne(delObj){
  			var tab = document.getElementById("tablehang");
  			var pkCode = delObj.parentNode.parentNode.getElementsByTagName("td")[0].innerHTML;
  			var kslxArray = document.getElementsByName("checkname1");
  			for(var i=0;i<kslxArray.length;i++){
		     		if(kslxArray[i].value==pkCode){
		     			kslxArray[i].disabled=false;
  						var j = delObj.parentNode.parentNode.rowIndex;
  						tab.deleteRow(j);
		     		}
  			}
  		}
  		function delOne1(delObj){
  			var tab = document.getElementById("tablelbId");
  			var pkCode = delObj.parentNode.parentNode.getElementsByTagName("td")[0].innerHTML;
  			var kslxArray = document.getElementsByName("checkname1");
  			for(var i=0;i<kslxArray.length;i++){
		     		if(kslxArray[i].value==pkCode){
		     			kslxArray[i].disabled=false;
  						var j = delObj.parentNode.parentNode.rowIndex;
  						tab.deleteRow(j);
		     		}
  			}
  		}
  		//添加请假
  		function baocun(){
  			var imgformid = document.getElementById("imgformid");
  			var qjtitle = document.getElementById("qjtitle").value;
  			var bumen = document.getElementById("bumen").value;
  			var qjreason = document.getElementById("qjreason").value;
  			var bmidsArray = document.getElementsByName("bmids");
  			var bmids="";
	     	for(var i=0;i<bmidsArray.length;i++){
	     			if(i==0){
	     				bmids = bmidsArray[i].value;
	     			}else{
	     				bmids +="," + bmidsArray[i].value;
	     			}
	     	}
  			var param={};
			param["qjtitle"] = qjtitle;
			param["user_code"] = "<%=user_code%>";
			param["bumen"]=bumen;
			param["qjreason"] = qjreason;
			param["bmids"] = bmids;
			param["user_name"] = "<%=user_name%>";
			if(bmids==""){alert("请选择请假的考试")}else{
			FireFly.doAct("TS_QJLB_QJ", "addData", param);
			window.location.href="qjlb.jsp";
			}
  		}
  	</script>
 <script type="text/javascript">
//上传图片后预览图片
 function viewImage(file){
             var preview = document.getElementById('preview');
             if(file.files && file.files[0]){
                 //火狐下
                 preview.style.display = "block";
                 preview.style.width = "88px";
                 preview.style.height = "88px";
                 preview.src = window.URL.createObjectURL(file.files[0]);
                 alert(src);
             }else{
                 //ie下，使用滤镜
                 file.select();
                 var imgSrc = document.selection.createRange().text;
                 var localImagId = document.getElementById("localImag"); 
                 //必须设置初始大小 
                 localImagId.style.width = "88px"; 
                 localImagId.style.height = "88px"; 
                 try{ 
                 localImagId.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
                 locem("DXImageTransform.Microsoft.AlphaImageLoader").src = imgSrc; 
                 }catch(e){ 
                 alert("您上传的图片格式不正确，请重新选择!"); 
                 return false; 
                 } 
                 preview.style.display = 'none'; 
                 document.selection.empty(); 
                 } 
                 return true; 
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
