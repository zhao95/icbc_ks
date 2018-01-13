<!DOCTYPE html>
<%@page import="com.rh.core.serv.ServDao" %>
<%@ page import="com.rh.core.serv.ParamBean" %>
<%@ page import="com.rh.core.serv.ServMgr" %>
<%@ page import="com.rh.core.serv.OutBean" %>
<%@ page import="com.rh.core.org.mgr.UserMgr" %>
<%@ page import="com.rh.core.comm.FileMgr" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%
    final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>我的请假查看</title>
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
    <!--（下载文件有用到zotnClientNTKO）-->
    <script type="text/javascript" src="<%=CONTEXT_PATH%>/sy/util/office/zotnClientLib_NTKO.js"></script>
</head>

<body class="hold-transition skin-black sidebar-mini">
<style>
    .bkuan table td {
        height: 50px;
    }

    #qjks-table > tbody > tr:nth-of-type(odd) {
        background-color: Azure;
    }

    /*时间轴样式*/
    .times {
        display: block;
        margin: 15px 0;
    }

    .times ul {
        /*margin-left: 164px;*/
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
        /*padding-left: 7px;*/
        font-size: 12px;
        line-height: 20px;
        color: #555;
    }

    .times ul li:first-child b {
        border: 2px solid #00aba6;
    }

    .times ul li:first-child .node-name {
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
    String qj_id = request.getParameter("qjid");
    String todoId = request.getParameter("todoId") != null ? request.getParameter("todoId") : "";
    String hidden = request.getParameter("hidden") != null ? request.getParameter("hidden") : "";
//    String todo_id = request.getParameter("todoid");
//    String done_id = request.getParameter("doneid");
    //根据请假id获取请假服务，获取请教列表信息
    Bean qjbean = ServDao.find("TS_QJLB_QJ", qj_id);
    String qj_title = qjbean.getStr("QJ_TITLE");
    String qj_ksname = qjbean.getStr("QJ_KSNAME");
//    String[] shIdArray = qj_ksname.split(",");
    String qj_reason = qjbean.getStr("QJ_REASON");
    String s_atime = qjbean.getStr("S_ATIME");
//    String qj_status = qjbean.getStr("QJ_STATUS");
    String qj_danwei = qjbean.getStr("QJ_DANWEI");
    String qj_name = qjbean.getStr("QJ_NAME");
    String qjImg = qjbean.getStr("QJ_IMG");

    List<Bean> files = FileMgr.getFileListBean("TS_QJLB_QJ", qjImg);

//    //获取人力资源编码
//    String user_code = userBean.getStr("USER_CODE");
//    //获取用户登录名
//    String user_login_name = userBean.getStr("USER_LOGIN_NAME");
//    //获取用户部门名称
//    String dept_name = userBean.getDeptName();
    //获取用户名
    String user_name = userBean.getStr("USER_NAME");
%>
<%@ include file="../../qt/jsp/header-logo.jsp" %>
<div style="background-color: #dfdfdf;padding: 0 10%;">
    <div style="background-color: #fdfdfd;padding-bottom: 30px" class="container-fluid">

        <%--流程流转过程图--%>
        <div class="row">
            <div class="col-sm-12 text-center" style="background-color: #dfdfdf;">
                <div id="flowView" style="min-height: 50px;padding:10px;">
                    <div style="display: inline-block">
                                <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial',serif;
                     font-weight: 700;font-style: normal;font-size: 16px;color: #FFFFFF;">1</span>
                        <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5520.png">
                        <span style="position: relative;font-size: 21px;top: 5px;">&nbsp;&nbsp;填写申请单&nbsp;&nbsp;</span>
                    </div>

                    <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">

                    <div style="display: inline-block">
                                <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial',serif;
                     font-weight: 700;font-style: normal;font-size: 16px;">2</span>
                        <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5522.png">
                        <span style="position: relative;font-size: 21px;top: 5px;">&nbsp;&nbsp;部门领导审批&nbsp;&nbsp;</span>
                    </div>

                    <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">

                    <div style="display: inline-block">
                                <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial',serif;
                     font-weight: 700;font-style: normal;font-size: 16px;">3</span>
                        <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5524.png">
                        <span style="position: relative;font-size: 21px;top: 5px;">&nbsp;&nbsp;考务管理人员审批</span>
                    </div>

                </div>
            </div>
        </div>

        <div class="row" style="padding-top:10px;">
            <div class="col-sm-12">
                <span style="font-size: 18px">请假申请</span>
                <hr/>
            </div>
        </div>

        <%--表单--%>
        <form class="form-horizontal" style="padding-right: 50px">
            <div class="form-group">
                <label for="qjtitle" class="col-sm-2 control-label">
                    项目名称
                    <span style="color: red;font-weight: bold">*</span>
                </label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="qjtitle" value="<%=qj_title%>" disabled>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">
                    请假的考试
                    <span style="color: red;font-weight: bold">*</span></label>
                </label>
                <div class="col-sm-10 bkuan">
                    <div style="min-height: 120px">
                        <table id="qjks-table" border="1" style="width: 100%;border-color: white;">
                            <thead>
                            <tr style="background-color: #f0f0f0;padding-left: 5px;text-align: center">
                                <td width="35%">考试名称</td>
                                <%--<td width="35%">考试开始时间</td>--%>
                                <%--<td width="30%">操作</td>--%>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-2 control-label">
                    请假人
                </label>
                <div class="col-sm-2">
                    <input type="text" class="form-control" value="<%=qj_name%>" disabled>
                </div>
                <label class="col-sm-1 control-label">
                    部门
                </label>
                <div class="col-sm-2">
                    <input type="text" id="bumen" class="form-control" value="<%=qj_danwei %>" disabled>
                </div>
                <label class="col-sm-2 control-label">
                    创建时间
                </label>
                <div class="col-sm-3">
                    <input type="text" id="satime" class="form-control" value="<%=s_atime %>" disabled>
                </div>
            </div>

            <div class="form-group">
                <label for="qjreason" class="col-sm-2 control-label">请假事由</label>
                <div class="col-sm-10">
                    <textarea id="qjreason" class="form-control" rows="3" disabled><%=qj_reason%></textarea>
                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-2 control-label">证明材料</label>
                <div class="col-sm-10">

                    <div class="row">
                        <div class="col-sm-12">
                            <ul>
                                <%
                                    for (Bean file : files) {
                                %>
                                <li style="clear:both;margin-top:7px;">

                                    <div style="float: left;overflow:hidden;text-overflow:ellipsis; width: 200px"
                                         title="<%=file.getStr("FILE_NAME")%>">
                                        <%=file.getStr("FILE_NAME")%>
                                    </div>
                                    <span id="<%=file.getId()%>" file-name="<%=file.getStr("FILE_NAME")%>"
                                          class="file" style="margin-left: 5px;cursor: pointer;color: blue;">
                                        下载
                                    </span>
                                </li>
                                <%
                                    }
                                %>
                            </ul>
                        </div>
                    </div>

                    <%--<div class="row">
                        <div class="col-sm-4">
                            <div id="localImag" style="display: block;height: 88px">
                                <% if (qjImg != null && !qjImg.equals("")) {
                                %>
                                <img style="width: 88px;height: 88px;" src="<%=CONTEXT_PATH %>/file/<%=qjImg%>">
                                <%}%>
                            </div>
                        </div>
                        <div class="col-sm-4" style="line-height: 88px;">
                            <% if (qjImg != null && !qjImg.equals("")) {
                            %>
                            <a id="downImag" onclick="downImg('<%=qjImg%>')"
                               style="display: inline;cursor: pointer;">下载</a>
                            <%}%>
                        </div>
                    </div>--%>

                </div>
            </div>

        </form>

        <%--审批信息头--%>
        <div class="row">
            <hr style="width: 95%;"/>
            <div style="padding-left: 10px;">
                <div style="text-align: left;font-size: 20px;color: #ff6600;margin-left: 20px;">
                    审批信息
                </div>
            </div>
        </div>

        <%--审批填写--%>
        <div class="row" id="shenpi">
            <div class="col-sm-12">
                <div class="row">
                    <div class="col-sm-offset-3 col-sm-6">
                        <form class="form-horizontal" style="padding-right: 50px">
                            <div class="form-group">
                                <label for="qjtitle" class="col-sm-4 control-label">
                                    审核人姓名
                                </label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" id="shname" value="<%=user_name%>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="qjtitle" class="col-sm-4 control-label">
                                    审核状态
                                </label>
                                <div class="col-sm-8">

                                    <div class="radio" style="float: left;margin-right: 30px;">
                                        <label>
                                            <input type="radio" name="sh_status" value="1" checked="">
                                            <%--onclick="tongyi()"--%>
                                            同意
                                        </label>
                                    </div>
                                    <div class="radio" style="float: left;">
                                        <label>
                                            <input type="radio" name="sh_status" value="2">
                                            <%--onclick="butongyi()"--%>
                                            不同意
                                        </label>
                                    </div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="qjtitle" class="col-sm-4 control-label">
                                    审核理由
                                </label>
                                <div class="col-sm-8">
                                    <textarea id="shreason" class="form-control" rows="3"></textarea>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="row" id="xyhjid1">
                    <div class="col-sm-4"></div>
                    <div id="nextStep" class="col-sm-3">
                        <button onclick="bcnext()" class="btn btn-success"
                                style="width: 150px;height:45px;background-color: #00c2c2;">
                            提交审批结果
                        </button>
                    </div>
                    <div class="col-sm-4">
                        <button onclick="fanhui()" class="btn btn-success"
                                style="width:100px;height:45px;background-color: #00c2c2;">
                            返回
                        </button>
                    </div>
                    <%--<div id="retreat" class="col-sm-6" style="display: none;">
                        <button onclick="tuihui()" class="btn btn-success"
                                style="width:100px;background-color: #00c2c2;">
                            退回
                        </button>
                    </div>--%>
                </div>

            </div>
        </div>


        <%--审批意见--%>
        <div class="row" id="shxxid" style="margin-top:60px">
            <div class="col-sm-12">
                <%--时间轴 审批意见--%>
                <div class="times">
                    <ul class="col-sm-offset-2">
                        <%
                            String cwhere1 = "AND DATA_ID=" + "'" + qj_id + "'";
                            ParamBean paramBean = new ParamBean();
                            paramBean.set("_SELECT_", "*");
                            paramBean.set("_extWhere", cwhere1);
                            paramBean.set("_ORDER_", "S_ATIME DESC");
                            paramBean.set("_NOPAGE_", true);
                            OutBean queryBean = ServMgr.act("TS_COMM_MIND", "query", paramBean);
                            List<Bean> shbeanList = (List<Bean>) queryBean.getData();
                            for (int j = 0; j < shbeanList.size(); j++) {
                                Bean shbean = shbeanList.get(j);
//                                String sh_id = shbean.getStr("MIND_ID");
                                String sh_status = shbean.getStr("SH_STATUS");
                                String sh_mind = shbean.getStr("SH_MIND");
                                String sh_uname = shbean.getStr("SH_UNAME");
                                String shs_atime = shbean.getStr("S_ATIME");
                                String shsDeptName = shbean.getStr("S_DNAME");
                                String sh_node = shbean.getStr("SH_NODE");
                                String shUcode = shbean.getStr("SH_UCODE");
                                UserBean user;
                                try {
                                    user = UserMgr.getUser(shUcode);
                                } catch (Exception e) {
                                    user = new UserBean(new Bean());
                                }
                        %>
                        <li style="">
                            <b></b>
                            <div style="position: relative;margin-left: 27px;top: -5px;">
                                <div class="node-name">
                                    <%=sh_node%>
                                </div>
                                <div style="padding-bottom: 20px;">
                                    <img style="position: absolute;margin-left: 11px;width: 55px;height:55px;"
                                         src="<%=CONTEXT_PATH%><%=user.getStr("USER_IMG")%>"
                                         onerror="this.src='<%=CONTEXT_PATH%>/sy/theme/default/images/common/user1.png'"><%----%>

                                    <div style="margin-left:78px;">
                                        <div style="font-size: 14px;">
                                            <span style="color:#666666;"><%=sh_uname%>：&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; </span>
                                            <span style="color:#000000;"><%=sh_status.equals("1") ? "同意" : "不同意"%>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; </span>
                                            <span style="color:#666666;"><%=sh_mind%></span>
                                        </div>
                                        <div style="font-size: 12px;">
                                            <span style="color:#999999;font-weight:400;"><%=shs_atime%>&nbsp; &nbsp; &nbsp; </span>
                                            <span style="color:#999999;font-weight:400;"><%=shsDeptName%></span>
                                        </div>
                                    </div>
                                </div>

                            </div>
                        </li>
                        <% } %>
                    </ul>
                </div>

            </div>
        </div>
        <div class="row" style="padding-top:20px;display: none" id="fanhuiId">
            <div class="col-sm-12 text-center">
                <button onclick="fanhui()" class="btn btn-success" style="width:100px;background-color: #00c2c2;">
                    返回
                </button>
            </div>
        </div>


    </div>
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
                    <span id="tiJiaoTip">下一环节审批人</span>
                </h5>
            </div>
            <div class="modal-body zgks">
                <table style="width: 100%;height: 100px;border: 0;">
                    <tr>
                        <td id="tiJiaoContent" style="text-align: center" width="10%">已经提交给<span id="shrNames"></span>进行审核
                        </td>
                    </tr>
                </table>
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" data-dismiss="modal"
                        style="width:100px;background-color: #00c2c2;">确定
                </button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<script type="text/javascript">
    var contextPath = '<%=CONTEXT_PATH %>';
    $(function () {
        var todoId = '<%=todoId%>';
        var qjId = '<%=qj_id%>';
        var shIdStr = '<%=qj_ksname%>';//报名id
        initData();
        setKsInfo(shIdStr);

        //展示流程图
        if (todoId) {
            showFlowView(todoId);
        } else {
            showFlowViewByDataId(qjId);
        }

        $('.file').unbind('click').bind('click', function () {
            var $this = jQuery(this);
            rh.ui.File.prototype.downloadFile($this.attr("id"), $this.attr("file-name"));
        });
    });

    function initData() {
        if (parseInt('<%=shbeanList.size()%>') <= 0) {
            //设置div签隐藏
            //没有审批意见 隐藏审批意见
            document.getElementById("shxxid").style.display = "none";
        }
        if ('<%=hidden%>' === '') {
            //设置div签隐藏
            //查看
            document.getElementById("shenpi").style.display = "none";
            document.getElementById("fanhuiId").style.display = "block";
        } else {
            //审批
            document.getElementById("shenpi").style.display = "block";
            document.getElementById("fanhuiId").style.display = "none";
        }
    }

    function setKsInfo(shIdStr) {
        //考试信息
        var params = {shids: shIdStr};
        var bmInfoListBean = FireFly.doAct('TS_QJLB_QJ', 'getBmInfoByIds', params);
        var bmInfoList = bmInfoListBean._DATA_;
        var $qjksTable = $('#qjks-table').find('tbody');
        for (var i = 0; i < bmInfoList.length; i++) {
            var bmInfo = bmInfoList[i];
            $qjksTable.append([
                '<tr style="padding-left: 5px;text-align: center">',
                '   <td class="rhGrid-td-hide">' + bmInfo.SH_ID,
                '   </td>',
                '   <td class="rhGrid-td-hide"><input type="text" name="shids" id="tjid" value="' + bmInfo.SH_ID + '">',
                '   </td>',
                '   <td width="35%">' + bmInfo.title,
                '   </td>',

                '</tr>'
            ].join(''));
//                '   <td width="35%">',//+ new Date(bmInfo.lbDate?bmInfo.lbDate:'').format("yyyy-mm-dd"),
//                '   </td>',
//            '   <td class="rhGrid-td-hide"><input type="text" name="lbids" value=""></td>',//'+bmInfo.lbDate+'
//                '   <td></td>',
        }
    }

    //修改请假(审批)
    function bcnext() {
        var staArray = document.getElementsByName("sh_status");
        var shname = document.getElementById("shname").value;
        var shreason = document.getElementById("shreason").value;
        var shstatus = "";
        for (i = 0; i < staArray.length; i++) {
            if (staArray[i].checked) {
                shstatus = staArray[i].value.trim();
            }
        }
        var param = {};
        param.todoId = '<%=todoId%>';
        param.shstatus = shstatus;
        param.shreason = shreason;
        if (shstatus === '1') {
            param.isRetreat = "false";
        } else {
            param.isRetreat = "true";
        }
        updateData("TS_QJLB_QJ", param);
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
        param.type = 'tuihui';
        updateData("TS_QJLB_QJ", param);
    }

    /* function fanhui() {
     if ('<!%=hidden%>' === '') {
     doPost('qjlb.jsp', {});
     }else{
     doPost('/<!%=CONTEXT_PATH%>/qt/jsp/todo.jsp', {});
     }
     }

     function doPost(to, data) {  // to:提交动作（action）,data:参数
     var myForm = document.createElement("form");
     myForm.method = "post";
     myForm.action = to;
     for (var i in data) {
     var myInput = document.createElement("input");
     myInput.setAttribute("name", i);  // 为input对象设置name
     myInput.setAttribute("value", data[i]);  // 为input对象设置value
     myForm.appendChild(myInput);
     }
     document.body.appendChild(myForm);
     myForm.submit();
     document.body.removeChild(myForm);  // 提交后移除创建的form
     }*/

</script>
<script src="<%=CONTEXT_PATH%>/ts/js/qj_jk_apply.js"></script>
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
