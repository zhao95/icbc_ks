<%@page import="java.sql.Array"%>
<%@page import="com.rh.core.serv.OutBean"%>
<%@page import="javax.swing.text.StyledEditorKit.ForegroundAction"%>
<%@ page import="com.rh.core.serv.ServDao"%>
<%@ page import="com.rh.core.base.Bean"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.rh.core.base.db.Transaction" %>
<%@ page import="com.rh.core.org.UserBean" %>
<%@ page import="com.rh.core.serv.bean.SqlBean" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.rh.core.org.mgr.UserMgr" %>
<%@ page import="com.rh.core.base.Context" %>
<%-- <%@ page import="java.util.HashMap" %> --%>
<%@ include file="../../sy/base/view/inHeader.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
<title>个人认证轨迹</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<link href="../css/jquery-ui-themes.css" type="text/css"
	rel="stylesheet" />
<link href="../css/axure_rp_page.css" type="text/css" rel="stylesheet" />
<link href="../css/styles1.css" type="text/css" rel="stylesheet" />
<link href="../css/styles.css" type="text/css" rel="stylesheet" />
<script src="../js/scripts/jquery-1.7.1.min.js"></script>
<script src="../js/scripts/jquery-ui-1.8.10.custom.min.js"></script>
<script src="../js/scripts/prototypePre.js"></script>
<script src="../js/document.js"></script>
<script src="../js/scripts/prototypePost.js"></script>
<script src="../js/data.js"></script>
<script type="text/javascript">
function post(URL, PARAMS) {        
	    var temp = document.createElement("form");        
	    temp.action = URL;        
	    temp.method = "post";        
	    temp.style.display = "none"; 
	    temp.target="_blank";       
	    for (var x in PARAMS) {        
	        var opt = document.createElement("textarea");        
	        opt.name = x;        
	        opt.value = PARAMS[x];        
	        // alert(opt.name)        
	        temp.appendChild(opt);        
	    }        
	    document.body.appendChild(temp);        
	    temp.submit();        
	    return temp;        
	}               
</script>
<!-- 表格样式的设置 -->
<style>
.even {
	background: #eefaff;
}

.odd {
	background: #ffffff;
}

.selected {
	background: #FF9900;
}
</style>
<script type="text/javascript">
	$(function() {
		$("tr:odd").addClass("odd");
		$("tr:even").addClass("even");
	});
</script>
</head>
<body>
	<div id="base" class="">

		<!-- Unnamed (矩形) -->
		<div id="u4991" class="ax_default box_1">
			<div id="u4991_div" class=""></div>
			<!-- Unnamed () -->
			<div id="u4992" class="text"
				style="display: none; visibility: hidden">
				<p>
					<span></span>
				</p>
			</div>
		</div>

		<!-- Unnamed (矩形) -->
		<div id="u4993" class="ax_default box_1">
			<div id="u4993_div" class=""></div>
			<!-- Unnamed () -->
			<div id="u4994" class="text"
				style="display: none; visibility: hidden">
				<p>
					<span></span>
				</p>
			</div>
		</div>

		<!-- Unnamed (图像) -->
		<div id="u4995" class="ax_default image">
			<img id="u4995_img" class="img " src="../images/u3271.png" />
			<!-- Unnamed () -->
			<div id="u4996" class="text"
				style="display: none; visibility: hidden">
				<p>
					<span></span>
				</p>
			</div>
		</div>

		<!-- Unnamed (矩形) -->
		<div id="u4997" class="ax_default box_1">
			<!-- Unnamed () -->
			<div id="u4998" class="text"
				style="display: none; visibility: hidden">
				<p>
					<span></span>
				</p>
			</div>
		</div>

		<!-- Unnamed (图像) -->
		<div id="u4999" class="ax_default image">
			<img id="u4999_img" class="img " src="../images/u3493.png" />
			<!-- Unnamed () -->
			<div id="u5000" class="text"
				style="display: none; visibility: hidden">
				<p>
					<span></span>
				</p>
			</div>
		</div>

		<!-- foot_text (矩形) -->
		<div id="u5001" class="ax_default box_3" data-label="foot_text">
			<div id="u5001_div" class=""></div>
			<!-- Unnamed () -->
			<div id="u5002" class="text"
				style="display: none; visibility: hidden">
				<p>
					<span></span>
				</p>
			</div>
		</div>

		<!-- Unnamed (矩形) -->
		<div id="u5003" class="ax_default box_1">
			<div id="u5003_div" class=""></div>
			<!-- Unnamed () -->
			<div id="u5004" class="text"
				style="display: none; visibility: hidden">
				<p>
					<span></span>
				</p>
			</div>
		</div>

		<!-- Unnamed (图像) -->
		<div id="u5005" class="ax_default image">
			<a href="javascript:history.go(-1);"><img id="u5005_img" class="img " src="../images/u1155.png" /></a>
			<!-- Unnamed () -->
			<div id="u5006" class="text"
				style="display: none; visibility: hidden">
				<p>
					<span></span>
				</p>
			</div>
		</div>

		<!-- Unnamed (矩形) -->
		<div id="u5007" class="ax_default label">
			<div id="u5007_div" class=""></div>
			<!-- Unnamed () -->
			<div id="u5008" class="text">
				<p>
					<span>/</span>
				</p>
			</div>
		</div>

		<!-- Unnamed (矩形) -->
		<div id="u5009" class="ax_default label">
			<div id="u5009_div" class=""></div>
			<!-- Unnamed () -->
			<div id="u5010" class="text">
				<p>
					<span>我的认证轨迹</span>
				</p>
			</div>
		</div>

		<!-- Unnamed (矩形) -->
		<div id="u5011" class="ax_default label">
			<div id="u5011_div" class=""></div>
			<!-- Unnamed () -->
			<div id="u5012" class="text">
				<p>
					<span>建议使用 1024 x 768 及以上分辨率浏览</span>
				</p>
			</div>
		</div>

		<!-- 图表动态面板 (动态面板) -->
		<div id="u5013" class="ax_default" data-label="图表动态面板">
			<%
				//用户的USER_CODE				
				String USER_CODE=userBean.getCode();
				//获证信息
				List<Bean> dataList = ServDao.finds("TS_ETI_CERT_QUAL","and STU_PERSON_ID='" +USER_CODE+"'");				
				//用户信息查询
				Bean stu=ServDao.find("SY_ORG_USER_INFO_SELF",USER_CODE);
				//入职日期
				String USER_CMPY_DATE="";
				//职位名称
				String USER_POST="";
				if(stu!=null){				
					USER_CMPY_DATE=stu.getStr("USER_CMPY_DATE");
					if(!USER_CMPY_DATE.equals("")){
						USER_CMPY_DATE=USER_CMPY_DATE.substring(0,4)+"年"+USER_CMPY_DATE.substring(4,6)+"月";
					}				
				    USER_POST=stu.getStr("USER_POST");
				}
				
				if (dataList.size()> 0) {
			%>
			<div id="u5013_state0" class="panel_state" data-label="图" style="">
				<div id="u5013_state0_content" class="panel_state_content">
					<%
						for (int i = 0; i < dataList.size(); i++) {
								String ISSUE_DATE = dataList.get(i).getStr("ISSUE_DATE");//发证日期
								ISSUE_DATE=ISSUE_DATE.substring(0,4)+"年"+ISSUE_DATE.substring(5,7)+"月";
								String CERT_GRADE_CODE = dataList.get(i).getStr("CERT_GRADE_CODE");//证书名称
								String CERT_ID = dataList.get(i).getStr("CERT_ID");//证书内码
								Integer QUALFY_STAT=dataList.get(i).getInt("QUALFY_STAT");//证书状态
								 //证书管理
								String servID = "TS_ETI_CERT_INFO";
								String wher = "and CERT_ID='" + CERT_ID + "'";
								List<Bean> bean = ServDao.finds(servID,wher); 
								Integer VALID_TERM=bean.get(0).getInt("VALID_TERM");
								String state = "";
								if(VALID_TERM==1){										
									if(QUALFY_STAT==1){
										state = "正常";
									}
									else if(QUALFY_STAT==2){
										state = "获取中";
									}
									else{
										state = "过期";
									}
								}else{
									state = "无效";
								}
					%>

					<!-- Unnamed (垂直线)-->
					<div id="u5014" class="ax_default line">
						<img id="u5014_img" class="img " src="../images/u5014.png" />
						<!--  Unnamed () -->
						<div id="u5015" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!--  Unnamed (矩形) -->
					<div id="u5016" class="ax_default label">
						<div id="u5016_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5017" class="text">
							<p>
								<span><%=USER_CMPY_DATE%></span>
							</p>
						</div>

					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5018" class="ax_default label">
						<div id="u5018_div" class=""></div>
						<!--   Unnamed () -->
						<%
							if (i == 0) {
						%>
						<div id="u5019" class="text">
							<p>
								<span><%=ISSUE_DATE%></span>
							</p>
						</div>
						<%
							}
						%>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5020" class="ax_default label">
						<div id="u5020_div" class=""></div>
						<!--   Unnamed () -->

						<div id="u5021" class="text">
							<p>
								<span>加入工行<%=CONTEXT_PATH %></span>
							</p>
						</div>

					</div>
					<!--  Unnamed (垂直线) -->
					<div id="u5022" class="ax_default line">
						<img id="u5022_img" class="img " src="../images/u5022.png" />
						<!-- Unnamed () -->
						<div id="u5023" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (形状) -->
					<div id="u5024" class="ax_default box_1">
						<img id="u5024_img" class="img " src="../images/u5024.png" />
						<!--  Unnamed () -->
						<div id="u5025" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!--  Unnamed (矩形) -->
					<div id="u5026" class="ax_default label">
						<div id="u5026_div" class=""></div>
						<!--  Unnamed () -->
						<%
							if (i == 0) {
						%>
						<div id="u5027" class="text">
							<p>
								<span><%=CERT_GRADE_CODE%></span>
							</p>
							<p>
								<span style="color: #990000;"><%=state%></span>
							</p>
						</div>
						<%
							}
						%>
					</div>

					<!--  Unnamed (矩形) -->
					<div id="u5028" class="ax_default label">
						<div id="u5028_div" class=""></div>
						<!-- Unnamed () -->
						<%
							if (i == 1) {
						%>
						<div id="u5029" class="text">
							<p>
								<span><%=ISSUE_DATE%></span>
							</p>
						</div>
						<%
							}
						%>
					</div>

					<!-- Unnamed (垂直线) -->
					<div id="u5030" class="ax_default line">
						<img id="u5030_img" class="img " src="../images/u5030.png" />
						<!-- Unnamed () -->
						<div id="u5031" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (形状) -->
					<div id="u5032" class="ax_default box_1">
						<img id="u5032_img" class="img " src="../images/u5032.png" />
						<!-- Unnamed () -->
						<div id="u5033" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!--  Unnamed (矩形) -->
					<div id="u5034" class="ax_default label">
						<div id="u5034_div" class=""></div>
						<!-- Unnamed () -->
						<%
							if (i == 1) {
						%>
						<div id="u5035" class="text">
							<p>
								<span><%=CERT_GRADE_CODE%></span>
							</p>
							<p>
								<span style="color: #990000;"><%=state%></span>
							</p>
						</div>
						<%
							}
						%>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5036" class="ax_default label">
						<div id="u5036_div" class=""></div>
						<!--  Unnamed () -->
						<%
							if (i == 2) {
						%>
						<div id="u5037" class="text">
							<p>
								<span><%=ISSUE_DATE%></span>
							</p>
						</div>
						<%
							}
						%>
					</div>

					<!-- Unnamed (垂直线) -->
					<div id="u5038" class="ax_default line">
						<img id="u5038_img" class="img " src="../images/u5038.png" />
						<!--  Unnamed () -->
						<div id="u5039" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!--  Unnamed (形状) -->
					<div id="u5040" class="ax_default box_1">
						<img id="u5040_img" class="img " src="../images/u5040.png" />
						<!-- Unnamed () -->
						<div id="u5041" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5042" class="ax_default label">
						<div id="u5042_div" class=""></div>
						<!--  Unnamed ()-->
						<%
							if (i == 2) {
						%>
						<div id="u5043" class="text">
							<p>
								<span><%=CERT_GRADE_CODE%></span>
							</p>
							<p>
								<span style="color: #990000;"><%=state%></span>
							</p>
						</div>
						<%
							}
						%>
					</div>

					<!--  Unnamed (矩形) -->
					<div id="u5044" class="ax_default label">
						<div id="u5044_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5045" class="text">
							<p>
								<span>NEXT</span>
							</p>
						</div>
					</div>
					<%
						}
					%>
					<!-- Unnamed (垂直线) -->
					<div id="u5046" class="ax_default line">
						<img id="u5046_img" class="img " src="../images/u5046.png" />
						<!--  Unnamed () -->
						<div id="u5047" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!--  Unnamed (矩形) -->
					<div id="u5048" class="ax_default label">
						<div id="u5048_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5049" class="text">
							<p>
								<span>资深专家1</span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5050" class="ax_default label">
						<div id="u5050_div" class=""></div>
						<!--  Unnamed () -->
						<div id="u5051" class="text">
							<p>
								<span>资深专家2</span>
							</p>
						</div>
					</div>

					<!--  Unnamed (矩形) -->
					<div id="u5052" class="ax_default label">
						<div id="u5052_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5053" class="text">
							<p>
								<span>资深专家3</span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5054" class="ax_default box_1">
						<div id="u5054_div" class=""></div>
						<!--  Unnamed () -->
						<div id="u5055" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5056" class="ax_default box_1">
						<div id="u5056_div" class=""></div>
						<!--  Unnamed () -->
						<div id="u5057" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5058" class="ax_default box_1">
						<div id="u5058_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5059" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5060" class="ax_default label">
						<div id="u5060_div" class=""></div>
						<!--  Unnamed () -->
						<div id="u5061" class="text">
							<p>
								<span style="color: #666666;">| </span><span
									style="color: #006600;"> 了解详情</span><span
									style="color: #666666;"> </span><span style="color: #FF0000;">&gt;</span>
							</p>
						</div>
					</div>

					<!--  Unnamed (矩形) -->
					<div id="u5062" class="ax_default label">
						<div id="u5062_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5063" class="text">
							<p>
								<span style="color: #666666;">| </span><span
									style="color: #006600;"> 了解详情</span><span
									style="color: #666666;"> </span><span style="color: #FF0000;">&gt;</span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5064" class="ax_default label">
						<div id="u5064_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5065" class="text">
							<p>
								<span style="color: #44546A;">| </span><span
									style="color: #006600;"> 了解详情</span><span
									style="color: #44546A;"> </span><span style="color: #FF0000;">&gt;</span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5066" class="ax_default box_1">
						<div id="u5066_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5067" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>
					<% 
						int num=103;
					    for(int i=0;i<dataList.size();i++){
					    	num=i*160;
					 %>
					 
					<!--  Unnamed (横线) -->
					<div id="u5068" class="ax_default line">
						<img id="u5068_img" class="img " src="../images/u5068.png" style="left:<%=num%>px;"/>
						<!-- Unnamed () -->
						<div id="u5087" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>
					<% 
					    }
					  %>
					<% 
						int line=-182;
						for(int i=0;i<3-dataList.size();i++){
							line=(i+1)*line;
					%>
					<!--  Unnamed (横线) -->
					<div id="u5086" class="ax_default line">
						<img id="u5086_img" class="img " src="../images/u5086.png" style="width:120px;left:<%=line%>px;"/>
						<!--  Unnamed () -->
						<div id="u5087" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>
					<%		
						}
					%>
					<!--  Unnamed (椭圆) -->
					<div id="u5070" class="ax_default ellipse">
						<img id="u5070_img" class="img " src="../images/u5070.png" />
						<!-- Unnamed () -->
						<div id="u5071" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (椭圆) -->
					<div id="u5072" class="ax_default ellipse">
						<img id="u5072_img" class="img " src="../images/u5072.png" />
						<!-- Unnamed () -->
						<div id="u5073" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (图像) -->
					<div id="u5074" class="ax_default image">
						<img id="u5074_img" class="img " src="../images/u5074.png" />
						<!-- Unnamed () -->
						<div id="u5075" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (图像) -->
					<div id="u5076" class="ax_default image">
						<img id="u5076_img" class="img " src="../images/u5076.png" />
						<!-- Unnamed () -->
						<div id="u5077" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!--  Unnamed (椭圆) -->
					<div id="u5078" class="ax_default ellipse">
						<img id="u5078_img" class="img " src="../images/u5078.png" />
						<!-- Unnamed () -->
						<div id="u5079" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (图像) -->
					<div id="u5080" class="ax_default image">
						<img id="u5080_img" class="img " src="../images/u5080.png" />
						<!-- Unnamed () -->
						<div id="u5081" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!--  Unnamed (椭圆) -->
					<div id="u5082" class="ax_default ellipse">
						<img id="u5082_img" class="img " src="../images/u5082.png" />
						<!-- Unnamed () -->
						<div id="u5083" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (图像) -->
					<div id="u5084" class="ax_default image">
						<img id="u5084_img" class="img " src="../images/u5084.png" />
						<!-- Unnamed () -->
						<div id="u5085" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!--  Unnamed (横线) -->
					 <div id="u5086" class="ax_default line">
						<img id="u5086_img" class="img " src="../images/u5086.png" style="width:;left:;"/>
						 <!-- Unnamed () -->
						<div id="u5087" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (椭圆) -->
					<div id="u5088" class="ax_default ellipse">
						<img id="u5088_img" class="img " src="../images/u5088.png" />
						<!--  Unnamed () -->
						<div id="u5089" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>

					<!-- Unnamed (图像) -->
					<div id="u5090" class="ax_default image">
						<img id="u5090_img" class="img " src="../images/u5090.png" />
						<!--  Unnamed () -->
						<div id="u5091" class="text"
							style="display: none; visibility: hidden">
							<p>
								<span></span>
							</p>
						</div>
					</div>
					<!-- Unnamed (矩形) -->
					<div id="u5092" class="ax_default box_1">
						<div id="u5092_div" class=""></div>
						<!--  Unnamed () -->
						<div id="u5093" class="text">
							<p>
								<span>图形显示</span>
							</p>
						</div>
					</div>

					<!--  Unnamed (形状) -->
					<div id="u5094" class="ax_default box_1">
						<img id="u5094_img" class="img " src="../images/u5094.png" />
						<!--  Unnamed () -->
						<div id="u5095" class="text">
							<p>
								<span>表格显示</span>
							</p>
						</div>
					</div>

				</div>
			</div>
			<div id="u5013_state1" class="panel_state" data-label="表"
				style="visibility: hidden;">

				<div id="u5013_state1_content" class="panel_state_content">
					<table class="rhGrid JPadding JColResizer"
						style="position: absolute; width: 950px; left: 30px; top: 98px; border-collapse: collapse;">
						<thead>
							<tr style="backGround-color: #20b2aa; height: 50px">
								<th style="width: 4%; text-align: center">序号</th>
								<th style="width: 15%; text-align: center">日期/获证日期</th>
								<th style="width: 6%; text-align: center">名&nbsp;&nbsp;&nbsp;&nbsp;称</th>
								<th style="width: 18%; text-align: center">有效期</th>
								<th style="width: 7%; text-align: center">当前状态</th>
								<th style="width: 11%; text-align: center">操作</th>
							</tr>
						</thead>
						<tbody class="">
							<%
								int j = 0;
									for (int i = dataList.size()-1; i >=0;i--) {
										j++;//序号
										String ISSUE_DATE = dataList.get(i).getStr("ISSUE_DATE");//发证日期
										String CERT_GRADE_CODE = dataList.get(i).getStr("CERT_GRADE_CODE");//名称
										//有效日期
										String BGN_DATE = dataList.get(i).getStr("BGN_DATE");//起始有效日期
										String END_DATE = dataList.get(i).getStr("END_DATE");//结束有效日期
										String date = BGN_DATE.equals("") ? END_DATE : BGN_DATE;
										if (!BGN_DATE.equals("") && !END_DATE.equals("")) {
											date = BGN_DATE + "-" + END_DATE;
										}
										//获证状态
										String CERT_ID = dataList.get(i).getStr("CERT_ID");//证书内码
										Integer QUALFY_STAT=dataList.get(i).getInt("QUALFY_STAT");//证书状态
										String servID = "ETI_CERTINFO_TS";
										String wher = "and CERT_ID='" + CERT_ID + "'";
										List<Bean> bean = ServDao.finds(servID, wher); 
										Integer VALID_TERM=bean.get(0).getInt("VALID_TERM");
										String state = "";
										if(VALID_TERM==1){										
											if(QUALFY_STAT==1){
												state = "正常";
											}
											else if(QUALFY_STAT==2){
												state = "获取中";
											}
											else{
												state = "过期";
											}
										}else{
											state = "无效";
										}
							%>
							<tr class="" style="height: 50px">
								<td style="text-align: center"><%=j%></td>
								<td style="text-align: center" id="ISSUE_DATE<%=j%>"><%=ISSUE_DATE%></td>
								<td style="text-align: center" id="CERT_GRADE_CODE<%=j%>"><%=CERT_GRADE_CODE%></td>
								<td style="text-align: center" id="date<%=j%>"><%=date%></td>
								<td style="text-align: center" id="state<%=j%>"><%=state%></td>
								<td style="text-align: center" id="caozuo<%=j%>">
									<a href="../jsp/zhengshu.jsp">
										<img src="../images/chankan.png">
									</a>
								</td>
							</tr>						

							<%
								}
							%>
							<tr class="" style="height: 50px">
								<td style="text-align: center"><%=j+1%></td>
								<td style="text-align: center" id=""><%=USER_CMPY_DATE.equals("")?"":USER_CMPY_DATE.substring(0,4)+"."+USER_CMPY_DATE.substring(4,6)+"."+USER_CMPY_DATE.substring(6)%></td>
								<td style="text-align: center" id="">加入工行</td>
								<td style="text-align: center" id=""></td>
								<td style="text-align: center" id=""></td>
								<td style="text-align: center" id=""></td>
							</tr>
						</tbody>
					</table>
				</div>
				<!-- Unnamed (形状) -->
				<div id="u5169" class="ax_default box_1">
					<img id="u5169_img" class="img " src="../images/u5169.png" />
					<!-- Unnamed () -->
					<div id="u5170" class="text">
						<p>
							<span>表格显示</span>
						</p>
					</div>
				</div>

				<!--  Unnamed (形状) -->
				<div id="u5171" class="ax_default box_1">
					<img id="u5171_img" class="img " src="../images/u5171.png" />
					<!-- Unnamed () -->
					<div id="u5172" class="text">
						<p>
							<span>图形显示</span>
						</p>
					</div>
				</div>
			</div>
			<%
				}
			%>
			<%
			Bean ser=ServDao.find("SY_HRM_ZDSTAFFPOSITION",USER_CODE);
			//查找用户序列名称
			String STATION_NO="";
			//职务层级编码
			String DUTY_LV_CODE="";
			if(ser!=null){
				STATION_NO=ser.getStr("STATION_NO");
				DUTY_LV_CODE=ser.getStr("DUTY_LV_CODE");	
			}	
				if (dataList.size()==0) {				
					//根据职位名称查找岗位信息
					Bean bean=ServDao.find("TS_ORG_POSTION","and POSTION_ID='"+DUTY_LV_CODE+"'");
					//岗位资格
					String POSTION_QUALIFICATION="0";
					if(bean!=null){															 				
					 POSTION_QUALIFICATION=bean.getStr("POSTION_QUALIFICATION");
					}
					String[] classs={" ","初级","中级","高级","专家级"};
					Integer i=Integer.valueOf(POSTION_QUALIFICATION);
					POSTION_QUALIFICATION=classs[i];
			%>
			<div id="u5013_state0" class="panel_state" data-label="空白" style="">
				
				<div id="u5013_state0_content" class="panel_state_content">
					<!-- Unnamed (矩形) -->
					<div id="u5181" class="ax_default label">
						<div id="u5181_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5182" class="text">
							<p>
								<span>您的岗位序列：</span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5183" class="ax_default box_1">
						<div id="u5183_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5184" class="text">
							<p>
								<span>图形显示</span>
							</p>
						</div>
					</div>

					<!-- Unnamed (形状) -->
					<div id="u5185" class="ax_default box_1">
						<img id="u5185_img" class="img " src="../images/u5094.png" />
						<!-- Unnamed () -->
						<div id="u5186" class="text">
							<p>
								<span>表格显示</span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5187" class="ax_default label">
						<div id="u5187_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5188" class="text">
							<p>
								<span>您还没有获得过相关资格证书</span>
							</p>
						</div>
					</div>

					<!-- Unnamed (椭圆) -->
					<div id="u5189" class="ax_default ellipse">
						<img id="u5189_img" class="img " src="../images/u5189.png" />
						<!-- Unnamed () -->
						<div id="u5190" class="text">
							<p>
								<span>!</span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5191" class="ax_default label">
						<div id="u5191_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5192" class="text">
							<p>
								<span><%=STATION_NO.equals("")?"":STATION_NO%>序列</span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5193" class="ax_default label">
						<div id="u5193_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5194" class="text">
							<p>
								<span>您要获取的资格：</span>
							</p>
						</div>
					</div>

					<!-- Unnamed (矩形) -->
					<div id="u5195" class="ax_default label">
						<div id="u5195_div" class=""></div>
						<!-- Unnamed () -->
						<div id="u5196" class="text">
							<p>							 
								<span id="tijiao" style="color: #666666;"><%=STATION_NO.equals("")?"":STATION_NO%>序列 <%=POSTION_QUALIFICATION.equals("")?"":POSTION_QUALIFICATION%>（</span><span
									style="text-decoration: underline; color: #388CAE;"><a href="javascript:post('../../qt/jsp/examref.jsp',{REF_DYXL:'<%=STATION_NO%>'})">相关学习资料下载</a></span><span
									style="color: #388CAE;">&nbsp;&nbsp; </span><span
									style="text-decoration: underline; color: #388CAE;">工银大学</span><span>）</span>							
							</p>
						</div>
					</div>
				</div>			
			</div>
			
			<div id="u5013_state1" class="panel_state" data-label="表" style="visibility: hidden;">
				<div id="u5013_state1_content" class="panel_state_content">
					<table class="rhGrid JPadding JColResizer"
						style="position: absolute; width: 950px; left: 30px; top: 98px; border-collapse: collapse;">
						<thead>
							<tr style="backGround-color: #20b2aa; height: 50px">
								<th style="width: 4%; text-align: center">序号</th>
								<th style="width: 15%; text-align: center">日期/获证日期</th>
								<th style="width: 6%; text-align: center">名&nbsp;&nbsp;&nbsp;&nbsp;称</th>
								<th style="width: 18%; text-align: center">有效期</th>
								<th style="width: 7%; text-align: center">当前状态</th>
								<th style="width: 11%; text-align: center">操作</th>
							</tr>
						</thead>
						<tbody class="">
							<tr class="" style="height: 50px">
								<td style="text-align: center"><%=1%></td>
								<td style="text-align: center" id=""><%=USER_CMPY_DATE.equals("")?"":USER_CMPY_DATE.substring(0,4)+"."+USER_CMPY_DATE.substring(4,6)+"."+USER_CMPY_DATE.substring(6)%></td>
								<td style="text-align: center" id="">加入工行</td>
								<td style="text-align: center" id=""></td>
								<td style="text-align: center" id=""></td>
								<td style="text-align: center" id=""></td>
							</tr>
						</tbody>
					</table>
				</div>
				<!-- Unnamed (形状) -->
				<div id="u5169" class="ax_default box_1">
					<img id="u5169_img" class="img " src="../images/u5169.png" />
					<!-- Unnamed () -->
					<div id="u5170" class="text">
						<p>
							<span>表格显示</span>
						</p>
					</div>
				</div>

				<!--  Unnamed (形状) -->
				<div id="u5171" class="ax_default box_1">
					<img id="u5171_img" class="img " src="../images/u5171.png" />
					<!-- Unnamed () -->
					<div id="u5172" class="text">
						<p>
							<span>图形显示</span>
						</p>
					</div>
				</div>
			</div>
			<%
				}
			%>
		</div>
	<%
			//追赶，同步，落后
			int pre=0, after =0, other =0,nums=0,num=0;
				HashMap<Object, Object> p=new HashMap<Object, Object>();
				p.put("USER_POST",USER_POST);
				Bean paramBean=new Bean(p);
			if(!STATION_NO.equals("")){
				//职务层级编码
				//String DUTY_LV_CODE="";
				//查找同等序列下的人数
				//paramBean.set(USER_POST," count(*) COUNT_ where USER_POST='"+USER_POST+"'");
				//List<Bean> lists=ServDao.finds("SY_ORG_USER_INFO_SELF","and USER_POST='"+USER_POST+"'");
				//num=lists.size();
				//num=ServDao.count("SY_ORG_USER_INFO_SELF",paramBean);
				num=ServDao.count("SY_ORG_USER_INFO_SELF",paramBean);
				//获取证书内码
				List<Bean> infos=ServDao.finds("TS_ETI_CERT_INFO","and STATION_NO='"+USER_POST+"'");
				 HashMap<Object, Object> t=new HashMap<Object, Object>();
				 t.put("CERT_ID",infos.get(dataList.size()).getStr("CERT_ID"));
				Bean param=new Bean(t); 
				if(infos.size()>0){
				 pre=ServDao.count("TS_ETI_CERT_QUAL",param); 
				 if(dataList.size()>0){
					 	t.put("CERT_ID",infos.get(dataList.size()-1).getStr("CERT_ID"));
						 param=new Bean(t); 
				 		other=ServDao.count("TS_ETI_CERT_QUAL",param)-1-pre;
				}
				 after=num-other-pre-1;
				}else{
					other=num-1;
				}
			}
			if(STATION_NO.equals("")){				
				//num=ServDao.count("SY_HRM_ZDSTAFFPOSITION",paramBean);
				other=num;
			}	 
		%>
		<!-- Unnamed (矩形) -->
		<div id="u5197" class="ax_default label">
			<div id="u5197_div" class=""></div>
			<!-- Unnamed () -->
			<div id="u5198" class="text">
				<p>
					<span
						style="font-family: '微软雅黑'; font-weight: 410; color: #666666;">当前序列共有</span><span
						style="font-family: '微软雅黑'; font-weight: 410; color: #CC6600;">
					</span><span
						style="font-family: '微软雅黑 Bold', '微软雅黑 Regular', '微软雅黑'; font-weight: 700; color: #1D69AC;"><%=num%></span><span
						style="font-family: '微软雅黑'; font-weight: 410; color: #666666;">
						人</span>
				</p>
			</div>
		</div>

		<!-- Unnamed (矩形) -->
		<div id="u5199" class="ax_default _文本">
			<div id="u5199_div" class=""></div>
			<!-- Unnamed () -->
			<div id="u5200" class="text">
				<p>
					<span style="color: #BA3830;"><%=pre%></span><span
						style="color: #666666;"> 人在您的前方, 待您追赶...</span>
				</p>
				<p>
					<span style="color: #DB6600;"><%=other%></span><span
						style="color: #666666;"> 人与您并驾齐驱...</span>
				</p>
				<p>
					<span style="color: #289C80;"><%=after%></span><span
						style="color: #666666;"> 人在您身后紧追不舍...</span>
				</p>
				<p>
					<span style="color: #666666;"><br></span>
				</p>
			</div>
		</div>
		
		<!-- Unnamed (垂直线) -->
		<div id="u5201" class="ax_default line">
			<img id="u5201_img" class="img " src="../images/u5201.png" />
			<!-- Unnamed () -->
			<div id="u5202" class="text"
				style="display: none; visibility: hidden">
				<p>
					<span></span>
				</p>
			</div>
		</div>

		<!-- Unnamed (矩形) -->
		<div id="u5203" class="ax_default label">
			<div id="u5203_div" class=""></div>
			<!-- Unnamed () -->
			<div id="u5204" class="text">
				<p>
					<span>努力才能进步 !</span>
				</p>
			</div>
		</div>

		<!-- Unnamed (横线) -->
		<div id="u5205" class="ax_default line">
			<img id="u5205_img" class="img " src="../images/u5205.png" />
			<!-- Unnamed () -->
			<div id="u5206" class="text"
				style="display: none; visibility: hidden">
				<p>
					<span></span>
				</p>
			</div>
		</div>
	</div>
</body>
</html>
