<!DOCTYPE html>
<%@page import="com.rh.core.serv.ServDao" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%
    final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>我的借考查看</title>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
          name="viewport">

    <%@ include file="../../sy/base/view/inHeader.jsp" %>
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
<style>
    .bkuan table td {
        height: 50px;
    }

    #qjks-table > tbody > tr:nth-of-type(odd) {
        background-color: Azure;
    }

    .times {
        display: block;
        margin: 15px 0;
    }

    .times ul {
        margin-left: 164px;
        border-left: 2px solid #ddd;
    }

    .times ul li {
        width: 100%;
        margin-left: -12px;
        line-height: 30px;
        /*font-weight: blod;*/
    }

    .times ul li b {
        width: 16px;
        height: 16px;
        background: #fff;
        border: 2px solid #d0cdc7;
        margin: 3px;
        border-radius: 50%;
        -webkit-border-radius: 50%;
        -moz-border-radius: 6px;
        overflow: hidden;
        display: inline-block;
        float: left;
    }

    .times ul li span {
        padding-left: 7px;
        font-size: 12px;
        line-height: 20px;
        color: #555;
    }

    .times ul li:first-child b {
        border: 2px solid #00aba6;
    }

    .times ul li:first-child span {
        color: #00aba6;
    }

    /*同上*/
    .times ul li p {
        padding-left: 15px;
        font-size: 14px;
        line-height: 25px;
    }

</style>
<%
    //获取请假id和一个状态
    String jk_id = request.getParameter("qjid");
    String todoId = request.getParameter("todoId");

    String hidden = request.getParameter("hidden") != null ? request.getParameter("hidden") : "";
//    String todo_id = request.getParameter("todoid");
//    String done_id = request.getParameter("doneid");
    //根据请假id获取请假服务，获取请教列表信息
    Bean jkbean = ServDao.find("TS_JKLB_JK", jk_id);
    String jk_title = jkbean.getStr("JK_TITLE");
    String jk_ksname = jkbean.getStr("JK_KSNAME");
    String[] bmidArray = jk_ksname.split(",");
    String jk_reason = jkbean.getStr("JK_REASON");
    String s_atime = jkbean.getStr("S_ATIME");
    String jk_status = jkbean.getStr("JK_STATUS");
    String jk_dept = jkbean.getStr("JK_DEPT");
    String jk_name = jkbean.getStr("JK_NAME");
    String jkImg = jkbean.getStr("JK_IMG");
    String jk_jkcity = jkbean.getStr("JK_JKCITY");
	String jk_yjfh=jkbean.getStr("JK_YIFH");
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
    <img alt="中国工商银行" src="<%=CONTEXT_PATH %>/qt/img/u3148.png"> <img alt="考试系统"
                                                                      src="<%=CONTEXT_PATH %>/qt/img/u3376.png">
</div>
<div style="background-color: #dfdfdf;padding-top: 10px;padding-left: 10%;padding-right: 10%;padding-bottom: 10px;">
    <div style="padding-left: 10px;">
        <table style="padding: 10px;width:100%;background-color: #5ab6a6;height: 80px;">
            <tr style="backGround-color: #ababab; height: 30px">
                <td style="text-align: center;">
                    <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial';
                     font-weight: 700;font-style: normal;font-size: 16px;color: #FFFFFF;">1</span>
                    <img alt="u5520" src="<%=CONTEXT_PATH %>/ts/image/u5520.png">
                    <span style="font-size: 15px;">&nbsp;&nbsp;填写申请单&nbsp;&nbsp;</span>
                    <img alt="u5532" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">
                    <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial';
                     font-weight: 700;font-style: normal;font-size: 16px;">2</span>
                    <img alt="u5522" src="<%=CONTEXT_PATH %>/ts/image/u5522.png">
                    <span style="font-size: 15px;">&nbsp;&nbsp;部门领导审批&nbsp;&nbsp;</span>
                    <img alt="u5524" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">
                    <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial';
                     font-weight: 700;font-style: normal;font-size: 16px;">3</span>
                    <img alt="u5532" src="<%=CONTEXT_PATH %>/ts/image/u5524.png">
                    <span style="font-size: 15px;">&nbsp;&nbsp;一级分行考管理员审批</span>
                    <img alt="u5524" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">
                    <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial';
                     font-weight: 700;font-style: normal;font-size: 16px;">4</span>
                    <img alt="u5532" src="<%=CONTEXT_PATH %>/ts/image/u5524.png">
                    <span style="font-size: 15px;">&nbsp;&nbsp;对方一级分行考管理员审批</span>
                    </td>
            </tr>
        </table>

          <div style="padding-left: 10px;">
        <table id="" style="padding: 10px;width:100%;">
           <%--  <tr>
                <td style="/*text-align: center;*/padding-top: 15px;">
                    <div style="background-color: #fed1d1;border:1px solid red;border-radius: 5px;
                             margin: 0 123px;padding: 5px;min-width: 830px;max-width: 685px;">
                        ！ 温馨提示：您今年已请假 <span style="color: red">2</span> 次，还可请假 <span style="color: red">1</span> 次。
                        满 <span style="color: red">3</span> 次后，本年度将不允许再请假。请合理使用请假次数！
                    </div>
                </td>
            </tr>--%>
            <tr>
                <td style="text-align: left;padding-top: 5px;padding-left: 2%;">
                    <span style="font-size: 18px;">借考申请</span><br/>
                    <img alt="u5532" data-toggle="modal" data-target="#myModal"
                         src="<%=CONTEXT_PATH %>/ts/image/u5540.png">
                </td>
            </tr>
        </table>
       </div>

    <div class="bkuan" style="padding-left: 10px;">
        <table style="padding: 10px;width:100%;background-color: #ffffff;">
            <tr>
                <td style="width: 10%;text-align: right;">借考标题&nbsp;&nbsp;</td>
                <td colspan="3"><input style="width: 90%;height: 25px;" id="qjtitle" value="<%=jk_title%>" disabled>
                </td>
            </tr>
            <tr>
					<td style="width: 10%;text-align: right;">借考一级分行</td>
					<td><select id="jkyiji"   style="min-width: 208px;height: 25px; border: 1px solid rgb(0, 0, 0); " disabled></select></td>
					
					<!--  <td ><input style="width:95%;height: 25px;" id="jktitle" value=""></td>-->
					<td style="width: 10%;text-align:left ;" >希望借考的城市&nbsp;&nbsp;
					<input style="width: 60%;height: 25px;" id="jkcity" value=""  disabled></td>
				</tr>
				<tr>
					<td style="width: 10%;text-align: right;">借考的考试&nbsp;&nbsp;
					</td>
					<td colspan="3">
                    <div  data-toggle="modal" data-target="#myModal" style="display: inline-block;cursor: pointer;color: #4cd4d4;">
                         
                        <i class="fa fa-search" aria-hidden="true" style="font-size: 24px;color: #91dce4;"></i>
                        <span>选择</span>
                    </div>
					</td>
				</tr>
            <tr>
                <td style="width: 10%;"></td>
                <td colspan="3">
                    <table border="1" style="width: 95%;border-color: white;" id="jkks-table">
                        <thead style="background-color: #f0f0f0;">
                        <tr style="padding-left: 5px;text-align: center">
                            <td width="35%">考试名称</td>
                            <td width="35%">考试开始时间</td>
                            <td width="30%"></td>
                        </tr>
                        </thead>
                        <tbody>
                        <%--<%

                            for (int i = 0; i < bmidArray.length; i++) {
                                String cwhere = "AND BM_ID=" + "'" + bmidArray[i] + "'";
                                List<Bean> cbeanList = ServDao.finds("TS_QJLB_BM", cwhere);
                                for (int j = 0; j < cbeanList.size(); j++) {
                                    Bean bmbean = cbeanList.get(j);
                                    String lb_id = bmbean.getStr("LB_ID");
                                    String bm_id = bmbean.getStr("BM_ID");
                                    String lb_title = bmbean.getStr("LB_TITLE");
                                    String lb_date = bmbean.getStr("LB_DATE");

                        %>
                        <tr style="padding-left: 5px;text-align: center">
                            <td class="rhGrid-td-hide"><%=bm_id%>
                            </td>
                            <td class="rhGrid-td-hide"><input type="text" name="bmids" id="tjid" value="<%=bm_id%>">
                            </td>
                            <td width="35%"><%=lb_title%>
                            </td>
                            <td width="35%"><%=lb_date%>
                            </td>
                            <td class="rhGrid-td-hide"><input type="text" name="lbids" value="<%=lb_id%>"></td>
                            <td></td>
                        </tr>
                        <%
                                }
                            }
                        %>--%>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td style="width: 10%;text-align: right;">借考人&nbsp;&nbsp;</td>
                <td style="width: 15%;"><input style="width: 90%;height: 25px;" value="<%=jk_name%>" disabled></td>
                <td style="width: 30%;">部门&nbsp;&nbsp;<input style="width: 60%;height: 25px;" id="bumen"
                                                             value="<%=jk_dept %>" disabled></td>
                <td style="width: 45%;">创建时间&nbsp;&nbsp;<input style="height: 25px;" id="satime" value="<%=s_atime %>"
                                                               disabled></td>
            </tr>
            <tr>
                <td style="width: 10%;text-align: right;">借考事由&nbsp;&nbsp;</td>
                <td colspan="3"><textarea rows="3" cols="130" id="jkreason" disabled><%=jk_reason%></textarea></td>
            </tr>
            <tr>
                <td style="width: 10%;text-align: right;">证明材料&nbsp;&nbsp;</td>
                <td colspan="3">
                    <%--<img alt="上传" onclick="shangchuan()" src="<%=CONTEXT_PATH %>/ts/image/uqjsc.png">--%>
                </td>
            </tr>
            <tr>
                <td style="width: 10%;"></td>
                <%--<td style="width: 25%;text-align: right;">--%>
                <%----%>
                <%--</td>--%>
                <td colspan="3">
                    <% if (jkImg != null && !jkImg.equals("")) {
                    %>
                    <img style="width: 88px;height: 88px;" src="<%=CONTEXT_PATH %>/file/<%=jkImg%>">
                    <%}%>
                </td>
                <%--<td style="width: 10%;"></td>
                <td style="width: 25%;text-align: right;"></td>
                <td colspan="2"><a>下载</a>&nbsp;&nbsp;<a>删除</a></td>--%>
            </tr>
        </table>
    </div>
    <div class="bkuan" style="padding-left: 10px;">
        <table style="padding: 10px;width:100%;background-color: #ffffff;text-align: center;">
            <tr>
                <td>
                    <div id="shaddid" style="padding-left: 10px;">
                        <table style="width:90%;background-color: #ffffff;text-align: center;">
                            <tr>
                                <td align="right" style="width: 30%;">审核人姓名:</td>
                                <td style="width: 5%;"></td>
                                <td align="left"><input type="text" id="shname" value="<%=user_name%>"></td>
                            </tr>
                            <tr>
                                <td align="right" style="width: 30%;">审核状态:</td>
                                <td style="width: 5%;"></td>
                                <td align="left">
                                    <input type="radio" name="sh_status" value="1" onclick="tongyi()">同意
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                    <input type="radio" name="sh_status" value="2" onclick="butongyi()">不同意
                                </td>
                            </tr>
                            <tr>
                                <td align="right" style="width: 30%;">审核理由:</td>
                                <td style="width: 5%;"></td>
                                <td align="left"><textarea rows="3" cols="60" id="shreason"></textarea></td>
                            </tr>
                        </table>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <div id="xyhjid" class="bkuan" style="padding-left: 10px;">
                        <table style="padding: 10px;width:80%;background-color: #ffffff;text-align: center;">
                            <tr>
                                <td>
                                    <button id="nextStep" onclick="bcnext()" class="btn btn-success"
                                            style="width:120px;background-color: #00c2c2;">送下一环节审核
                                    </button>
                                </td>
                                <td>
                                    <button id="retreat" onclick="tuihui()" class="btn btn-success"
                                            style="width:100px;background-color: #00c2c2;">退回
                                    </button>
                                </td>
                            </tr>
                        </table>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <hr style="width: 95%;"/>
                    <div style="padding-left: 10px;" id="shxxid">
                        <div style="text-align: left;font-size: 20px;color: #ff6600;margin-left: 20px;">
                            审批信息
                        </div>
                        <div class="times">
                            <ul>
                                <%
                                    String cwhere1 = "AND DATA_ID=" + "'" + jk_id + "'";
                                    List<Bean> shbeanList = ServDao.finds("TS_COMM_MIND", cwhere1);
                                    for (int j = 0; j < shbeanList.size(); j++) {
                                        Bean shbean = shbeanList.get(j);
                                        String sh_id = shbean.getStr("MIND_ID");
                                        String sh_status = shbean.getStr("SH_STATUS");
                                        String sh_mind = shbean.getStr("SH_MIND");
                                        String sh_uname = shbean.getStr("SH_UNAME");
                                        String shs_atime = shbean.getStr("S_ATIME");
                                        String shs_dept = shbean.getStr("S_DEPT");
                                        String sh_node = shbean.getStr("SH_NODE");
                                %>
                                <li style="">
                                    <b></b>
                                    <div style="position: relative;text-align: left;left: 3px;top:-3px;">
                                        <%=sh_node%>
                                    </div>
                                    <div style="position: relative;text-align: left;left: 136px;top: 25px;">
                                        <%=sh_uname%>
                                    </div>
                                    <div style="position: relative;text-align: left;left: 255px;top: -4px;">
                                        <%=sh_status.equals("1") ? "审批通过" : "审批不通过"%>
                                    </div>
                                    <div style="position: relative;text-align: left;left: 355px;top: -33px;">
                                        <%=sh_mind%>
                                    </div>
                                    <div style="position: relative;text-align: left;left: 136px;top: -33px;">
                                        <span style="color:#999999"><%=shs_atime%>&nbsp;&nbsp;&nbsp;<%=shs_dept%></span>
                                    </div>
                                    <img style="left: -446px;position: relative;width: 55px;height:55px;text-align: left;top: -90px;"
                                         src="/sy/theme/default/images/common/user1.png">
                                </li>
                                <% } %>
                            </ul>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
    <div class="bkuan" id="fhid" style="padding-left: 10px;">
        <table style="padding: 10px;width:100%;background-color: #ffffff;text-align: center;">
            <tr>
                <td>
                    <button onclick="fanhui()" class="btn btn-success" style="width:100px;background-color: #00c2c2;">
                        返回
                    </button>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    $(function () {
        if (parseInt('<%=shbeanList.size()%>') <= 0) {
            //设置div签隐藏
            //没有审批意见 隐藏审批意见
            document.getElementById("shxxid").style.display = "none";
        }
        if ('<%=hidden%>' === '') {
            //设置div签隐藏
            //查看
            document.getElementById("xyhjid").style.display = "none";
            document.getElementById("shaddid").style.display = "none";
        } else {
            //审批
            document.getElementById("xyhjid").style.display = "block";
            document.getElementById("shaddid").style.display = "block";
        }

        //考试信息
        var bmIdStr = '<%=jk_ksname%>';
        var params = {bmids: bmIdStr};
        var bmInfoListBean = FireFly.doAct('TS_JKLB_JK', 'toApplyExam', params);
        var bmInfoList = bmInfoListBean._DATA_;
        var $jkksTable = $('#jkks-table tbody');
        for (var i = 0; i < bmInfoList.length; i++) {
            var bmInfo = bmInfoList[i];
            $jkksTable.append([
                '<tr style="padding-left: 5px;text-align: center">',
                '   <td class="rhGrid-td-hide">' + bmInfo.BM_ID,
                '   </td>',
                '   <td class="rhGrid-td-hide"><input type="text" name="bmids" id="tjid" value="' + bmInfo.BM_ID + '">',
                '   </td>',
                '   <td width="35%">' + bmInfo.title,
                '   </td>',
                '   <td width="35%">',//+ new Date(bmInfo.lbDate?bmInfo.lbDate:'').format("yyyy-mm-dd"),
                '   </td>',
                '   <td class="rhGrid-td-hide"><input type="text" name="lbids" value=""></td>',//'+bmInfo.lbDate+'
                '   <td></td>',
                '</tr>'
            ].join(''));
        }

    });
</script>
<script type="text/javascript">
    function fanhui() {
        window.history.go(-1);
    }

    function tongyi() {
        $('#nextStep').css('display', 'block');
        $('#retreat').css('display', 'none');
    }

    function butongyi() {
        $('#nextStep').css('display', 'none');
        $('#retreat').css('display', 'block');
    }
    //修改请假(审批)
    function bcnext() {
        var staArray = document.getElementsByName("sh_status");
        var shname = document.getElementById("shname").value;
        var shreason = document.getElementById("shreason").value;
        var shstatus = "";
        for (i = 0; i < staArray.length; i++) {
            if (staArray[i].checked) {
                shstatus = staArray[i].value;
            }
        }
        var param = {};
        param.todoId = '<%=todoId%>';
        param.shstatus = shstatus;
        param.shreason = shreason;
        param.isRetreat = "false";
        updateData(param);

        <%--FireFly.doAct("TS_QJLB_QJ", "updateData", param,);--%>

        /*if (shstatus === "不同意") {
         //            var shsta = "1";
         //            var qjstatus = "2";
         var param = {};
         param["qjid"] = "<!%=qj_id%>";
         param["qjstatus"] = qjstatus;
         param["shreason"] = shreason;
         param["shstatus"] = shsta;
         param["userloginname"] = "<！%=user_login_name%>";
         param["deptname"] = "<!%=dept_name%>";
         param["usercode"] = "<!%=user_code%>";
         FireFly.doAct("TS_QJLB_QJ", "updateData", param,);
         window.location.href = "qjlb.jsp";
         }*/
    }

    //退回
    function tuihui() {
        var staArray = document.getElementsByName("sh_status");
        var shname = document.getElementById("shname").value;
        var shreason = document.getElementById("shreason").value;
        var shstatus = "";
        for (i = 0; i < staArray.length; i++) {
            if (staArray[i].checked) {
                shstatus = staArray[i].value;
            }
        }
        var param = {};
        param.todoId = '<%=todoId%>';
        param.shstatus = shstatus;
        param.shreason = shreason;
        param.isRetreat = "true";
        updateData(param);
    }

    //提交审批
    function updateData(param) {
        FireFly.doAct("TS_JKLB_QJ", "updateData", param, false, false, function (response) {
            if (response._MSG_.indexOf('ERROR,') >= 0) {
                //发起申请出错
                alert(response._MSG_.substring(response._MSG_.indexOf('ERROR,'), response._MSG_.length));
            } else {
                fanhui();
            }
        });
    }

</script>
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
