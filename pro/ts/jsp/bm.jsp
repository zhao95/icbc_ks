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
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<!-- 获取后台数据 -->
<%@ include file="/sy/base/view/inHeader.jsp"%>
<%@ page import="com.rh.core.serv.ServDao"%>
<%@ page import="com.rh.core.serv.ServMgr"%>
<%@ page import="com.rh.ts.pvlg.mgr.GroupMgr"%>
<%@ page import="com.rh.core.base.Bean"%>
<%@ page import="java.text.SimpleDateFormat"%>
<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/bootstrap/css/bootstrap.min.css">
<!-- Font Awesome -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/font-awesome-4.7.0/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/ionicons/css/ionicons.min.css">

<script src="<%=CONTEXT_PATH%>/qt/js/html5shiv.min.js"></script>
<script src="<%=CONTEXT_PATH%>/qt/js/respond.min.js"></script>
<script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>

<!-- Theme style -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/dist/css/AdminLTE.min.css">
<!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. -->
<link rel="stylesheet"
	href="<%=CONTEXT_PATH%>/qt/dist/css/skins/_all-skins.min.css">
	 <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/bootstrap/js/site.css">
<!--工具方法--> 
<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/global.js"></script> 
<!--插件--> 
<script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/jquery.smart-form.js"></script> 
<script src="<%=CONTEXT_PATH%>/ts/jsp/jquery.iframe-transport.js"></script> 
<script src="<%=CONTEXT_PATH%>/ts/jsp/jquery.ui.widget.js"></script> 
<script src="<%=CONTEXT_PATH%>/ts/jsp/jquery.fileupload.js"></script> 

<body class="hold-transition skin-black sidebar-mini">
<%
			//获取所有项目ID
			String user_code = userBean.getStr("USER_CODE");
			Bean paramBean = new Bean();
			String qz = GroupMgr.getGroupCodes(user_code);
			Bean outBean = ServMgr.act("TS_XMGL","getXmList",paramBean );
			String xmlist = outBean.getStr("xid");
			String[] xmarray = xmlist.split(",");
			//将可见的 项目 ID 放到新的数组中
			List<String>  kjxm = new ArrayList<String>();
			//遍历项目ID  匹配项目和本人的 群组权限
			for(int a=0;a<xmarray.length;a++){
				paramBean.set("xmid", xmarray[a]);
				Bean outBeanCode = ServMgr.act("TS_XMGL_RYGL_V","getCodes",paramBean);
				String codes = outBeanCode.getStr("rycodes");
			Boolean boo = false;
			if(codes==""){
			}else{
			//本人所在的群组编码
			String[] codeArray = codes.split(",");
			String[] qzArray = qz.split(",");
			for(int b=0;b<qzArray.length;b++){
				if(Arrays.asList(codeArray).contains(qzArray[b])){
					boo=true;
				}
			}
			}
			//可见的项目id
			if(boo==true){
				kjxm.add(xmarray[a]);
			}
			}
			//从已报名的考试中找到已报名的考试信息   判断是否报名了  报的是什么
							String where = "AND BM_CODE="+"'"+user_code+"'";
							List<Bean> baominglist = ServDao.finds("TS_BMLB_BM",where);
							List<String> stringlist = new ArrayList<String>();
							if(baominglist.size()!=0){
								
								for(int a=0;a<baominglist.size();a++){
									//获取报名的 项目信息  的name  将报名项目名称放到array中
							String XM_ID = baominglist.get(a).getStr("XM_ID");
									if(XM_ID!=""){
										
								stringlist.add(XM_ID);
									}
								}
							}
							%> 
	<div class="" style="padding: 10px">
		<a href="index_qt.jsp"><image style="padding-bottom:10px"
				src="<%=CONTEXT_PATH%>/ts/image/u1155.png" id="shouye"></image></a> <span
			style="color: blue; font-size: 20px">&nbsp;&nbsp;/&nbsp;&nbsp;我的报名</span>
	</div>
	<table id="myTab" class="nav nav-tabs"
		style="margin-left: 10px; width: 98%; background-color: white">
		<tr style="height: 70px">
			<td id="keshenqingtd"  class="active"
				style="border-bottom:white solid 1px;width: 50%; text-align: center; font-size: 25px"><image
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u975.png" id="keshenimage">
				<a id="akeshen" href="#home" data-toggle="tab"><span id="keshen"
					style="color: lightseagreen">可申请的报名</span></a></td>
			<td id="yishenqingtd" class="" style="border-bottom:lightgray solid 1px;width: 50%; text-align: center; font-size: 25px"><image
					style="margin-bottom:10px"
					src="<%=CONTEXT_PATH%>/ts/image/u984.png" id="yishenimage">
				<a id="ayishen" href="#tab2" data-toggle="tab"><span id="yishen"
					style="color: black">已申请的报名</span></a></td>
		</tr>

	</table>
	<div id="myTabContent" class="tab-content">
		<div class="tab-pane fade in active" id="home">
			<div
				style="margin-top: -5px; margin-left: 19%; height: 5px; width: 190px; background-color: LightSeaGreen"></div>
			<div id="cuxian1"
				style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">可申请的报名</span>
			</div>
			<div
				style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
			<div id="table1" class="" style="margin-left: 10px; width: 98%">
				<div class="content-main1">
					<table class="rhGrid JPadding JColResizer" id="table">
						<thead id="tem" class="">
							<tr style="backGround-color:WhiteSmoke; height: 30px">
								<th id="BM_XUHAO" class=""
									style="width: 6.6%; text-align: center">序号</th>
								<th id="BM_NAME" class="" style="width: 29.1%;">名称</th>
								<th id="BM_ODEPT__NAME" class="" style="width: 15%;">组织单位</th>
								<th id="S_ATIME" class="" style="width: 29.3%;">报名时间</th>
								<th id="BM_STATE__NAME" class="" style="width: 10%;">状态</th>
								<th id="BM_OPTIONS" class=""
									style="width: 10%; text-align: center">操作</th>
							</tr>
						</thead>
						<tbody class="">
							<%
								String servId = "TS_XMGL";
										List<Bean> list = ServDao.finds(servId,"");
										
										int j=0;
									for(int i=0;i<list.size();i++){
										j++;
										Bean bean = list.get(i);
										String name = bean.getStr("XM_NAME");
							 //项目中已存在array的  title  数据  将展示在  已报名信息中
										String id = bean.getStr("XM_ID");
										if(stringlist.contains(id)|| !kjxm.contains(id)){
											//已报名这个考试之后  或者他不能报名这个考试 中断循环 继续开始
											j--;
											continue;
										} 
										
										String dept = bean.getStr("XM_FQDW_NAME");
										String type = bean.getStr("XM_TYPE");
										String where1 = "AND XM_ID="+"'"+id+"'";
										List<Bean> listbean = ServDao.finds("TS_XMGL_BMGL",where1);
										Bean bmbean = listbean.get(0);
										SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
										String startTime = bmbean.getStr("BM_START");
										String endTime = bmbean.getStr("BM_END");
										String state = "未开始";
										String display = "none";
										if(startTime!=""&&endTime!=""){
										Date date1 = sdf.parse(startTime);
										Date date2 = sdf.parse(endTime);
										Date date = new Date();
										if(date.getTime()<date2.getTime()&&date.getTime()>date1.getTime()){
										 state = "待报名";
										 display = "block";
										}else if(date.getTime()>date2.getTime()){
											state="已结束";
										}
										}
							%>

							<tr class="rhGrid-td-left" 
								style="height: 50px">
								<td class="indexTD" style="text-align: center"><%=j%></td>
								<td class="rhGrid-td-hide" id="BM_TYPE<%=i%>" ><%=type %></td>
								<td class="rhGrid-td-hide" id="BM_ID<%=i%>" ><%=id %></td>
								<td class="rhGrid-td-left " id="BM_NAME<%=i%>"
									style="text-align: left"><%=name%></td>
								<td class="rhGrid-td-left " id="BM_ODEPT__NAME"
									style="text-align: left"><%=dept%></td>
								<td class="rhGrid-td-left " id="S_ATIME"
									style="text-align: left" ><%=startTime%></td>
								<td class="rhGrid-td-left " id="BM_STATE__NAME"
									style="text-align: left"><%=state%></td>
								<td id="BM_OPTIONS" style="text-align: center"><input
									type="button" onclick="tiaozhuan(<%=i%>)"
									style="display:<%=display%>;color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:80px"
									value="报名"></input></td>
							</tr>


							<%
							
										}
							%>
						</tbody>
					</table>

				</div>

		</div>
		</div>
		<div class="tab-pane fade"  id="tab2">
			<div
				style="margin-top: -6px; margin-left: 68%; height: 5px; width: 190px; background-color: LightSeaGreen"></div>
			<div id="cuxian2"
				style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
				<span style="margin-left: 50px; padding-top: 10px">已申请的报名</span>
			</div>
                 
                 <div  id="jibie" style="padding-top:20px;padding-left:75%">
                 <select id = "jb" onchange="jibieonchange()">
                 <option selected="selected">全部</option>
                 <option value="初级">初级</option>
                 <option value="中级">中级</option>
                 <option value="高级">高级</option>
                 </select>
                 </div>
                 
			<div id ="qihuan" style="margin-top:-40px">
		
              <div id="sanjiliandong" class="panel-body" style=":15px;width:800px;margin-left:200px"> 
                 <form action="#" id="formContainer" class="form form-horizontal"></form>
                 
             </div> 
			<div id="table2" class="" style="margin-top:-15px;margin-left: 10px; width: 98%;">
				<div class="content-main2" style="position:relative;">
					<table id="ybmtable" class="rhGrid JPadding JColResizer">
						<thead id="tem" class="">
							<tr style="backGround-color: WhiteSmoke; height: 30px">
							<th id="BM_XUHAO" class=""
									style="width: 6.6%; text-align: center">序号</th>
								<th style="width: 20%; text-align: center">名称</th>
								<th style="width: 5%; text-align: center">类型</th>
								<th style="width: 20%; text-align: center">考试时间</th>
								<th style="text-align: center; width: 14%;">审核状态</th>
								<th style="width: 14%; text-align: center">状态</th>
								<th id="BM_OPTIONS" class=""
									style="width: 14%; text-align: center">操作</th>
							</tr>
						</thead>
						<tbody>
							
						</tbody>
						
					</table>
			
			<div id="fenyediiv" style="position:absolute;right:5%;padding-bottom:-20px;">
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
			</div>
			<form id="form1" style="display:none" method="post" action="bmzgks.jsp">
				<input id = "zgtz" name="zgtz"></input>
				<input type="submit" name="Submit" value="提交">
			</form>
			<form id="form2" style="display:none" method="post" action="bmglf.jsp">
				<input id = "fzgtz" name="fzgtz"></input>
			<input type="submit" name="Submit" value="提交">
			</form>
		</div>
		<!--异议模态窗口  -->
		<div class="modal fade" id="appeal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog" style="width:50%">
			<div class="modal-content">
				<div class="modal-header" style="background-color: #00c2c2;color: white">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
						&times;
					</button>
					<h5 class="modal-title">
						异议
					</h5>
					
				</div>
				<div style="padding-top:20px;width:700px;font-size:20px;text-align:center;color:lightseagreen">
				如确需报考，请说明并提交相关证明材料
				</div>
				<div style="padding-top:20px">
				<table style="width:700px">
				<tr>
				<td style="width:15%"></td><td style="height:150px;width:%;vertical-align:top"><textarea id="liyou" style="border:solid 1px lightseagreen;height:90%;width:90%" wrap="soft"></textarea></td>
				</tr>
				</table>
				<div id="uploadfile" style="padding-left:50px;color:lightseagreen;font-size:20px"><form action="/file" name="formup" id="formContainer2" class="form form-horizontal"></form></div>
				</div>
				<div class="modal-footer" style="text-align:center;height:100px">
					<button type="button" class="btn btn-primary" style="height:50px;background:lightseagreen;width:100px" onclick="">提交异议</button>
					<button type="button" onclick = "closemotai()" class="btn btn-default" style="height:50px;width:100px" data-dismiss="modal">取消</button>
				</div>
				
			</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
		<input type="hidden" id="xmid"/>
		<input type="hidden" id="dijige">
		<input type="hidden" id="user_code" value="<%=user_code %>">
	</div>

	<script src="<%=CONTEXT_PATH%>/qt/js/index_qt.js"></script>
	
	<!-- FastClick -->
	<script src="<%=CONTEXT_PATH%>/baoming.js"></script>
	<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
	<!-- AdminLTE App -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
	<!-- AdminLTE for demo purposes -->
	<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
</body>
</html>
