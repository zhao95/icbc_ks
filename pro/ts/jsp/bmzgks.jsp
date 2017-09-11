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
<style type="text/css">
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
		Bean xmbean=ServDao.find("TS_XMGL", xm_id);
		String xm_name = xmbean.getStr("XM_NAME");
		//获取报名管理id
		String where1 = "AND XM_ID="+"'"+xm_id+"'";
		List<Bean> bmglList = ServDao.finds("TS_XMGL_BMGL", where1);
		String bm_id = "";
		String bm_ksxz ="";
		String bm_start = "";
		String bm_end = "";
		String bm_name = "";
		if(bmglList!=null && bmglList.size()>0){
		 bm_id = bmglList.get(0).getStr("BM_ID");
		 String bm_ksxzs = bmglList.get(0).getStr("BM_KSXZ");
		 bm_ksxz=bm_ksxzs.replace("\n","<br>");
		 bm_start = bmglList.get(0).getStr("BM_START");
		 bm_end = bmglList.get(0).getStr("BM_END");
		 bm_name = bmglList.get(0).getStr("BM_NAME");
		}
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
		//职务
		//人员信息
		String wheregw = "AND PERSON_ID="+"'"+user_code+"'";
		List<Bean> gwList = ServDao.finds("SY_HRM_ZDSTAFFPOSITION", wheregw);
		//岗位类别名称代码
		String STATION_TYPE_CODE="";
		//岗位类别名称
		String STATION_TYPE="";
		// 序列名称代码
		String STATION_NO_CODE= "";
 		//序列名称
		String STATION_NO= "";
 		//职务层级
 		String DUTY_LEVEL="";
		if(gwList!=null && gwList.size()>0){
		 STATION_TYPE_CODE=gwList.get(0).getStr("STATION_TYPE_TYPE");
		 STATION_TYPE = gwList.get(0).getStr("STATION_TYPE");
		 STATION_NO = gwList.get(0).getStr("STATION_NO");
		 STATION_NO_CODE= gwList.get(0).getStr("STATION_NO_CODE");
		 DUTY_LEVEL = gwList.get(0).getStr("DUTY_LEVEL");
		}
		//获取跨序列的类型列表  
		String where2 ="";
		if(!STATION_TYPE.equals("") || !STATION_NO.equals("")){
			 where2= " AND KSLB_XL<>"+"'"+STATION_NO+"'"+" AND XM_ID="+"'"+xm_id+"'";
		}if(STATION_TYPE.equals("") || STATION_NO.equals("")){
			 where2= " AND XM_ID="+"'"+xm_id+"'";
		}
		List<Bean> zgList= ServDao.finds("TS_XMGL_BM_KSLB", where2);
		if(zgList!=null && zgList.size()>0){
		  zgList = ServDao.finds("TS_XMGL_BM_KSLB", where2);
		}
		%>
	<div style="background: #dfdfdf; padding-top: 10px" align="center">
		<div id="" style="background: white; width: 90%;">
			<div style="background: white; width: 90%; text-align: center">
				<table style="height: 100px; width: 90%;">
					<tr>
						<td><span style="font-size: 25px; color: #00C2C2;"><%=xm_name%></span></td>
					</tr>
				</table>
			</div>
			<div class="a">
				<table align="center" style="width: 90%;">
					<tr>
						<td colspan="4" height="120px"><p
								style="font-size: 15px; color: red;">报考须知，请仔细阅读！</p>
							<p style="color: red;">
								报名时间：<%=bm_start%>~~<%=bm_end %></p>
							<p style="color: red;"><%=bm_ksxz%></p></td>
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
						<td height="50px" align="left">2. 您在本考试周已报名：0 个，还可报名： 3
							个（本序列考试：1，跨序列考试：2）。如需重新报名，应先取消已有报名，然后再提交新的报名。</td>
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
							<td width="16.5%"><%=STATION_TYPE%></td>
							<td width="16.5%">岗位序列</td>
							<td width="16.5%"><%=STATION_NO%></td>
							<td width="16.5%">职务层级</td>
							<td width="17.5%"><%=DUTY_LEVEL%></td>
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
						<tr style="background-color: #ffbdbd;">
							<td width="3%"></td>
							<td width="10%">岗位类别</td>
							<td width="15%">序列</td>
							<td width="27%">模块</td>
							<td width="10%">级别</td>
							<td width="20%">验证</td>
							<td width="15%">验证结果</td>
						</tr>
						<% 
	      						String wherexl = "AND KSLB_NAME="+"'"+STATION_TYPE+"'"+" AND KSLB_XL="+"'"+STATION_NO+"'"+" AND XM_ID="+"'"+xm_id+"'";
	      						List<Bean> xlList = ServDao.finds("TS_XMGL_BM_KSLB", wherexl);
	      						Bean mkBean = new Bean();
	      						Bean mkcodeBean = new Bean();
	      						String lbname ="";
	      						String xlname ="";
	      						String lbcode ="";
	      						String xlcode ="";
	      						if(xlList!=null && xlList.size()!=0){
	      						 String kslb_id = xlList.get(0).getStr("KSLB_ID");
	      						 lbname = xlList.get(0).getStr("KSLB_NAME");
	      						 xlname= xlList.get(0).getStr("KSLB_XL");
	      						 lbcode= xlList.get(0).getStr("KSLB_CODE");
	      						 xlcode= xlList.get(0).getStr("KSLB_XL_CODE");
	      						%>
						<tr>
							<td><input class="rhGrid-td-hide" type="text"
								name="checkboxaa"></td>
							<td width="10%"><%=lbname%></td>
							<td width="15%"><%=xlname%></td>
							<% 
	      						
	      						%>
							<td width="27%"><select id="mkid" onchange="typeId(this)">
									<% 
	      						//根据岗位名称，序列和项目id找到对应的模块
	      						for (Bean bean : xlList) {
      									String type = bean.getStr("KSLB_TYPE");
      									String mk = bean.getStr("KSLB_MK");
      									String mkcode = bean.getStr("KSLB_MK_CODE");
      									if (mkBean.containsKey(mk)) {
      										List list = mkBean.getList(mk);
      										list.add(type);
      										mkBean.set(mk,list);
      										mkcodeBean.set(mk,mkcode);
      									} else {
      										List list = new ArrayList();
      										list.add(type);
      										mkBean.set(mk,list);
      										mkcodeBean.set(mk,mkcode);
      									}
	      						}
	      						for(Object mk: mkcodeBean.keySet()){
	      							String mkcode =mkcodeBean.getStr(mk);
      							%>
									<option value="<%=mkcode%>"><%=mk%></option>
									<%
	      						}
	      						%>
							</select></td>
							<td width="10%"><select id="lxid" onchange="changeyk(this)">
									<option></option>
							</select></td>
							<td class="rhGrid-td-hide"><input type="text" id="zglbid"
								name="zgksname" value="<%=kslb_id%>"></td>
							<td width="20%">
								<div id="<%=kslb_id%>"></div>
							</td>
							<td width="15%"><div id="<%=kslb_id%>yzjg"></div></td>
						</tr>
						<% 
	      					}
       						%>
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
					<table border="1" style="width: 90%" id="tablehang">
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
				<div style="height: 100px; padding: 20px;">

					<button id="zgyzbt" onclick="checky()" class="btn btn-success"
						style="width: 100px; background-color: #00c2c2;">1.资格验证</button>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<button id="tjbt" class="btn btn-success"
						style="width: 100px; background-color: #00c2c2;"
						data-toggle="modal" data-target="#tiJiao" onclick="tijiao()">2.提交报名</button>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<button onclick="goBack()" class="btn btn-success"
						style="width: 100px; background-color: #00c2c2;">返回</button>
				</div>
			</div>
		</div>
	</div>
	<!-- 模态选择框跨序列（Modal） -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog" style="width:700px;">
			<div class="modal-content">
				<div class="modal-header"
					style="background-color: #00c2c2; color: white">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
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
							<table border="1" style="width: 100%;" id="tabletjId">
								<thead>
									<tr style="background-color: #d9eeeb; padding-left: 5px;">
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
						style="width: 100px;margin-left:100px;background-color: #00c2c2;">返回</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal -->
	</div>
	<!-- 模态提交框（Modal） -->
	<div class="modal fade" id="tiJiao" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div id="mask" class="mask"></div>
				<div class="modal-header"
					style="background-color: #00c2c2; color: white">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
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
	<script type="text/javascript">
	function getFzgList(){
		var param = {};
 		param["STATION_TYPE"]="<%=STATION_TYPE%>";
 		param["STATION_NO"]="<%=STATION_NO%>";
 		param["xm_id"]="<%=xm_id%>";
 		var fzgList= FireFly.doAct("TS_BMLB_BM", "getFzgValue", param,true,false);
 		console.log(fzgList);
 		return fzgList['_DATA_'];
	}
	
	function showFzgList(showList){
		jQuery('#ksxxId').html('');
		for(var i=0; i<showList.length;i++){
			var showItem = showList[i];
			var kslb_id = showItem.KSLB_ID;
			var kslb_name = showItem.KSLB_NAME;
			var kslb_xl = showItem.KSLB_XL;
			var kslb_mk = showItem.KSLB_MK;
			var kslb_type_name = showItem.KSLB_TYPE_NAME;
			var kslb_code = showItem.KSLB_CODE;
			var kslb_xl_code = showItem.KSLB_XL_CODE;
			var kslb_mk_code = showItem.KSLB_MK_CODE;
			var kslb_type = showItem.KSLB_TYPE;
			
	jQuery('#ksxxId').append([
		'<tr>',
		'<td style="text-align: center" width="10%"><input type="checkbox" onchange="change(this)" name="checkname1" value="'+kslb_id+'" ></td>',
		'<td width="15%">'+kslb_name+'</td>',
		'<td width="15%">'+kslb_xl+'</td>',
		'<td width="45%">'+kslb_mk+'</td>',
		'<td width="15%">'+kslb_type_name+'</td>',
		'<td class="rhGrid-td-hide" id="HANGHAO'+i+'">'+i +'</td>',
		'<td class="rhGrid-td-hide" >'+kslb_id+'</td>',
		'<td class="rhGrid-td-hide">'+kslb_code+'</td>',
		'<td class="rhGrid-td-hide">'+kslb_xl_code+'</td>',
		'<td class="rhGrid-td-hide">'+kslb_mk_code+'</td>',
		'<td class="rhGrid-td-hide">'+kslb_type+'</td>',
	'</tr>'
	].join('')
	);
		}
	}
	
	var yk={};
	var xkArg=[];//考试结果
	var yzgz;//资格验证后端返回到前端的数据
	 $(function(){ 
		 typeId(obj);
		 var allList=getFzgList();
		 
		 showFzgList(allList);
		 
		 var setting={data
	             :FireFly.getDict('TS_XMGL_BM_KSLBK'),
	         dictId:"TS_XMGL_BM_KSLBK",expandLevel:1,
	         extWhere:"",
	         onnodeclick :function (item) {
				 console.log(item);
	        	var idName=item['NAME'];
	        	 var showList=[];
	        	for(var i=0; i<allList.length;i++){
	        		var showItem = allList[i];
	        		if(idName===showItem.KSLB_NAME || idName===showItem.KSLB_MK || idName===showItem.KSLB_XL){
	        			showList.push(showItem);
		        	}
	        	}
	   		 showFzgList(showList);

	     		
	         },
	         rhItemCode:"KSLBK_PID",
	         rhLeafIcon:"",
	         rhexpand:false,
				showcheck:false,
	         theme: "bbit-tree-no-lines",
	         url  :"SY_COMM_INFO.dict.do"
	        };
	         var tree = new rh.ui.Tree(setting);
	         $('.content-navTree').append(tree.obj);
	 });
	//兼容火狐、IE8   
    //显示遮罩层    
    function showMask(){     
        $("#mask").css("height",$(document).height());     
        $("#mask").css("width",$(document).width());     
        $("#mask").show();     
    }  
    //隐藏遮罩层  
    function hideMask(){     
        $("#mask").hide();     
    }  
	//等级改变事件
	function changeyk(obj){
		var sel = document.getElementById("lxid");
		var selected_val = sel.options[sel.selectedIndex].value;
		yk["BM_TYPE"]=selected_val;
	}
	//模块改变事件
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
	//模态页面 取消按钮 删除之前append的tr
	function quxiao(){
		//获取到table
		var motaitable = document.getElementById("motaitable");
		var rowlength = motaitable.rows.length-1;
		for(var i=rowlength;i>1;i--){
			motaitable.deleteRow(i);
		}
	}
	function goBack(){
		window.history.go(-1);
	}
	function deletec(){
			var checkArray = document.getElementsByName("checkboxaa");
			var kslxArray = document.getElementsByName("checkname1");
			for(var j=0;j<checkArray.length;j++){
				for(var i=0;i<kslxArray.length;i++){
		     		if(kslxArray[i].value==checkArray[j]){
		     			kslxArray[i].disabled=false;
		     		}
				}
			}
		    
		}
	//跨序列资格考试选择数量上限
	var total = 0;
	function change(obj){
		var kslxArray = document.getElementsByName("checkname1");
		if($(obj).prop("checked")){ 
			total+=1;
			}else{
				total-=1;
				if(total==1){
			     	for(var i=0;i<kslxArray.length;i++){
			     		if(kslxArray[i].checked){
			     			
			     		}else{
			     			kslxArray[i].disabled=false;
			     		}
			     	}
				}
			}
		if(total==2){
	     	for(var i=0;i<kslxArray.length;i++){
	     		if(kslxArray[i].checked){
	     			
	     		}else{
	     			kslxArray[i].disabled=true;
	     		}
	     	}
		}
	}
	
	//获取应考试的值
	function tijiao(){
		//获取手机号码
		 	var ryl_mobile = document.getElementById("user_mobile2").value=document.getElementById("user_mobile1").value; 
			//获取 当前页面中checkbox选中的数据
			var arrChk=$("input[name='checkboxaa']"); 
			tbody=document.getElementById("xinxi");
			for(var i=0;i<arrChk.length;i++){
			 //得到tr
			  var tr=arrChk[i].parentNode.parentNode;
		      var tds=tr.getElementsByTagName("td");
		      var ntr = tbody.insertRow();
		      if(i==0){
			      ntr.innerHTML=
			      '<td style="text-align:right;color:lightseagreen">报考类型</td>'+
			      '<td style="text-align:center">'+tds[1].innerHTML+'</td>'+
			      '<td style="text-align:left">'+tds[2].innerHTML+'</td>'+
			      '<td style="text-align:left">'+tds[3].innerHTML+'</td>'+
			      '<td style="text-align:left">'+tds[4].innerHTML+'</td>';
		     	}else{
		    	   ntr.innerHTML=
				       '<td style="text-align:center;color:blue"></td>'+
				       '<td style="text-align:center">'+tds[1].innerHTML+'</td>'+
				       '<td style="text-align:left">'+tds[2].innerHTML+'</td>'+
				       '<td style="text-align:left">'+tds[3].innerHTML+'</td>'+
				       '<td style="text-align:left">'+tds[4].innerHTML+'</td>';
		       }
			}
	}
	 
	//跨序列的考试
	function fuzhi(){
		var tab = document.getElementById("tablehang");
 	    var kslxArray = document.getElementsByName("checkname1");
     	for(var i=0;i<kslxArray.length;i++){
     		if(kslxArray[i].checked && !kslxArray[i].disabled){
		       var tr=kslxArray[i].parentNode.parentNode;
		       var tds=tr.getElementsByTagName("td");
		       var www=tds[0].innerText;
		       var kslb_name=tds[1].innerText;
		       var kslb_xl=tds[2].innerText;
		       var kslb_mk=tds[3].innerText;
		       var kslb_type_name=tds[4].innerText;
		       var hanghao = tds[5].innerText;
			   var kslb_id = tds[6].innerText;
			   var kslb_code=tds[7].innerText;
		       var kslb_xl_code=tds[8].innerText;
		       var kslb_mk_code=tds[9].innerText;
		       var kslb_type = tds[10].innerText;
		       tbody=document.getElementById("goods");
		       var ntr = tbody.insertRow();
		       ntr.innerHTML=
		       '<td ><input checked="checked" type="checkbox" onchange="change2(this)" name="checkboxaa"></td>'+
		       '<td >'+kslb_name+'</td>'+
		       '<td >'+kslb_xl+'</td>'+
		       '<td >'+kslb_mk+'</td>'+
		       '<td >'+kslb_type_name+'</td>'+
		       '<td class="rhGrid-td-hide" >'+hanghao+'</td>'+
		       '<td ><div id="'+kslb_id+'"></div></td>'+
		       '<td ><div id="'+kslb_id+'yzjg"></div></td>'+
			   '<td class="rhGrid-td-hide" ><input type="text" name="zgksname" value="'+kslb_id+'"></td>'+
			   '<td class="rhGrid-td-hide" >'+kslb_id+'</td>'+
			   '<td class="rhGrid-td-hide" >'+kslb_code+'</td>'+
			   '<td class="rhGrid-td-hide" >'+kslb_xl_code+'</td>'+
			   '<td class="rhGrid-td-hide" >'+kslb_mk_code+'</td>'+
			   '<td class="rhGrid-td-hide" >'+kslb_type+'</td>';
		      kslxArray[i].disabled=true;
		       var xk = {};
		       xk['ID'] = kslb_id;
		       xk['BM_LB'] = kslb_code;
		       xk['BM_XL'] = kslb_xl_code;
		       xk['BM_MK'] = kslb_mk_code;
		       xk['BM_TYPE'] =kslb_type;
		       xkArg.push(xk);
			}
     	}
     	
	}
	//删除
	//跨序列复选框变动
	function change2(obj){
		var arrChk=$("input[name='checkboxaa']"); 
		if($(obj).prop("checked")){ 
			}else{
				var tab = document.getElementById("tablehang");
				var hang = tab.rows.length;
		 	    var kslxArray = document.getElementsByName("checkboxaa");
		     	for(var i=0;i<kslxArray.length;i++){
		     		if(kslxArray[i].checked){
				       
					}else{
				var row =obj.parentNode.parentNode;
				var tds=row.getElementsByTagName("td");
				var j=obj.parentNode.parentNode.rowIndex;
				var hanghao = tds[5].innerText;
				//删除时清空数组中的元素
				var ys = tds[9].innerText;
				for(var i=0;i<xkArg.length;i++){
					xkArg[i]
					if(xkArg[i].ID===ys){
						var index = xkArg.indexOf(xkArg[i]);
						if (index > -1) {
							xkArg.splice(index, 1);
						}
					}
				}
				//删除行
				tab.deleteRow(j);
				 var kslxArray2 = document.getElementsByName("checkname1");
				 kslxArray2[hanghao].disabled=false;
				
					}
		     	}
	     		
		}
	}
	//进行资格验证
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
	}
	//提交所有数据
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
		}
	</script>
</body>
</html>
