<!DOCTYPE html>
<%@page import="com.rh.core.serv.ServDao" %>
<%@ page import="com.icbc.ctp.utility.StringUtil" %>
<%@ page import="com.rh.core.serv.ParamBean" %>
<%@ page import="com.rh.core.serv.ServMgr" %>
<%@ page import="com.rh.core.serv.OutBean" %>
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
    String jk_id = request.getParameter("jkid");
    String todoId = request.getParameter("todoId") != null ? request.getParameter("todoId") : "";
    String hidden = request.getParameter("hidden") != null ? request.getParameter("hidden") : "";
//    String todo_id = request.getParameter("todoid");
//    String done_id = request.getParameter("doneid");
    //根据请假id获取请假服务，获取请教列表信息
    Bean jkbean = ServDao.find("TS_JKLB_JK", jk_id);
    String jk_title = jkbean.getStr("JK_TITLE");
    String jk_ksname = jkbean.getStr("JK_KSNAME");
//    String[] bmidArray = jk_ksname.split(",");
    String jk_reason = jkbean.getStr("JK_REASON");
    String s_atime = jkbean.getStr("S_ATIME");
//    String jk_status = jkbean.getStr("JK_STATUS");
    String jk_dept = jkbean.getStr("JK_DEPT");
    String jk_name = jkbean.getStr("JK_NAME");
    String jkImg = jkbean.getStr("JK_IMG");
    String jk_jkcity = jkbean.getStr("JK_JKCITY");
    String jk_yjfh = jkbean.getStr("JK_YJFH");
    jk_yjfh = StringUtil.isEmpty(jk_yjfh) ? "" : jk_yjfh;
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
            <div class="col-sm-12 text-center" style="background-color: #ababab;">
                <div id="flowView" style="min-height: 50px;padding:10px;">
                    <div style="display: inline-block">
                                <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial',serif;
                     font-weight: 700;font-style: normal;font-size: 16px;color: #FFFFFF;">1</span>
                        <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5520.png">
                        <span style="position: relative;font-size: 25px;top: 5px;">&nbsp;&nbsp;填写申请单&nbsp;&nbsp;</span>
                    </div>

                    <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">

                    <div style="display: inline-block">
                                <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial',serif;
                     font-weight: 700;font-style: normal;font-size: 16px;">2</span>
                        <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5522.png">
                        <span style="position: relative;font-size: 25px;top: 5px;">&nbsp;&nbsp;部门领导审批&nbsp;&nbsp;</span>
                    </div>

                    <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">

                    <div style="display: inline-block">
                                <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial',serif;
                     font-weight: 700;font-style: normal;font-size: 16px;">3</span>
                        <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5524.png">
                        <span style="position: relative;font-size: 25px;top: 5px;">&nbsp;&nbsp;考务管理人员审批</span>
                    </div>

                </div>
            </div>
        </div>

        <div class="row" style="padding-top:10px;">
            <div class="col-sm-12">
                <span style="font-size: 18px">借考申请</span>
                <hr/>
            </div>
        </div>


        <form class="form-horizontal" style="padding-right: 50px">
            <div class="form-group">
                <label for="jktitle" class="col-sm-2 control-label">
                    借考标题
                    <span style="color: red;font-weight: bold">*</span></label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="jktitle" value="<%=jk_title%>" disabled>
                </div>
            </div>

            <div class="form-group">
                <label for="jkyiji" class="col-sm-2 control-label">
                    借考一级分行
                </label>
                <div class="col-sm-4">
                    <input type="text" class="form-control" id="jkyiji" disabled>
                </div>
                <label for="jkcity" class="col-sm-2 control-label">
                    希望借考的城市
                </label>
                <div class="col-sm-4">
                    <input type="text" class="form-control" id="jkcity" value="<%=jk_jkcity%>" disabled>
                </div>
            </div>

            <div class="form-group">
                <label for="jkdks" class="col-sm-2 control-label">
                    借考的考试
                    <span style="color: red;font-weight: bold">*</span>
                </label>
                <div class="col-sm-10 bkuan">
                    <div onclick="xuanze()" data-toggle="modal" data-target="#myModal" id="jkdks"
                         style="display: inline-block;cursor: pointer;color: #4cd4d4;padding:5px;">
                        <i class="fa fa-search" aria-hidden="true" style="font-size: 24px;color: #91dce4;"></i>
                        <span>选择</span>
                    </div>
                    <div style="min-height: 120px">
                        <table id="jkks-table" border="1" style="width: 100%;border-color: white;">
                            <thead>
                            <tr style="background-color: #f0f0f0;padding-left: 5px;text-align: center">
                                <td width="35%">考试名称</td>
                                <td width="35%">考试开始时间</td>
                                <td width="30%">操作</td>
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
                    借考人
                </label>
                <div class="col-sm-3">
                    <input type="text" class="form-control" value="<%=jk_name%>" disabled>
                </div>
                <label class="col-sm-2 control-label">
                    部门
                </label>
                <div class="col-sm-5">
                    <input type="text" id="bumen" class="form-control" value="<%=jk_dept%>" disabled>
                </div>
            </div>

            <div class="form-group">
                <label for="jkreason" class="col-sm-2 control-label">借考事由</label>
                <div class="col-sm-10">
                    <textarea id="jkreason" class="form-control" rows="3" disabled><%=jk_reason%></textarea>
                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-2 control-label">证明材料</label>
                <div class="col-sm-10">

                    <div class="row">
                        <div class="col-sm-4">
                            <div id="localImag" style="display: block;height: 88px">
                                <% if (jkImg != null && !jkImg.equals("")) {
                                %>
                                <img style="width: 88px;height: 88px;" src="<%=CONTEXT_PATH %>/file/<%=jkImg%>">
                                <%}%>
                            </div>
                        </div>
                        <div class="col-sm-4" style="line-height: 88px;">
                            <% if (jkImg != null && !jkImg.equals("")) {
                            %>
                            <a id="downImag" onclick="downImg('<%=jkImg%>')"
                               style="display: inline;cursor: pointer;">下载</a>
                            <%}%>
                        </div>
                    </div>

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
                                <label class="col-sm-4 control-label">
                                    审核人姓名
                                </label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" id="shname" value="<%=user_name%>">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-4 control-label">
                                    审核状态
                                </label>
                                <div class="col-sm-8">

                                    <div class="radio" style="float: left;margin-right: 30px;">
                                        <label>
                                            <input type="radio" name="sh_status" value="1" checked=""
                                                   onclick="tongyi()">
                                            同意
                                        </label>
                                    </div>
                                    <div class="radio" style="float: left;">
                                        <label>
                                            <input type="radio" name="sh_status" value="2" onclick="butongyi()">
                                            不同意
                                        </label>
                                    </div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-4 control-label">
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
                    <div id="nextStep" class="col-sm-6">
                        <button onclick="bcnext()" class="btn btn-success"
                                style="background-color: #00c2c2;">
                            送下一环节审核
                        </button>
                    </div>
                    <div id="retreat" class="col-sm-6" style="display: none;">
                        <button onclick="tuihui()" class="btn btn-success"
                                style="width:100px;background-color: #00c2c2;">
                            退回
                        </button>
                    </div>
                </div>

            </div>
        </div>

        <%--审批意见--%>
        <div class="row" id="shxxid">
            <div class="col-sm-12">
                <%--时间轴 审批意见--%>
                <div class="times">
                    <ul>
                        <%
                            String cwhere1 = "AND DATA_ID=" + "'" + jk_id + "'";
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
                        %>
                        <li style="">
                            <b></b>
                            <div style="position: relative;margin-left: 27px;top: -5px;">
                                <div class="node-name">
                                    <%=sh_node%>
                                </div>
                                <div style="padding-bottom: 20px;">
                                    <img style="position: absolute;margin-left: 11px;width: 55px;height:55px;"
                                         src="/sy/theme/default/images/common/user1.png">
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

        <div class="row" style="padding-top:20px;">
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
                    下一环节审批人
                </h5>
            </div>
            <div class="modal-body zgks">
                <table style="width: 100%;height: 100px;border: 0;">
                    <tr>
                        <td style="text-align: center" width="10%">已经提交给<span id="shrNames"></span>进行审核</td>
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
    $(function () {
        if (parseInt('<%=shbeanList.size()%>') <= 0) {
            //设置div签隐藏
            //没有审批意见 隐藏审批意见
            document.getElementById("shxxid").style.display = "none";
        }
        if ('<%=hidden%>' === '') {
            //设置div签隐藏
            //查看
            document.getElementById("shenpi").style.display = "none";
        } else {
            //审批
            document.getElementById("shenpi").style.display = "block";
        }

        //借考一级分行
        var jkYjfh = '<%=jk_yjfh%>';
        $('#jkyiji').val(FireFly.getDictNames(FireFly.getDict('TS_JKLB_JKCITY'), jkYjfh));

        //考试信息
        var bmIdStr = '<%=jk_ksname%>';
        var params = {bmids: bmIdStr};
        var bmInfoListBean = FireFly.doAct('TS_JKLB_JK', 'getBmInfoByIds', params);
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

        showFlowView();

    });
</script>
<script type="text/javascript">

    //加载流程图
    function showFlowView() {
        //        List<Bean> tsWfsNodeApplyList = ServDao.finds("TS_WFS_NODE_APPLY", "and WFS_ID ='" + wfsId + "'");
        var todoId = '<%=todoId%>';
        if (todoId !== '') {
            var data = {_PK_: todoId};
            var sId = 'TS_COMM_TODO';
            var todoBean = FireFly.doAct(sId, 'byid', data);
            var wfsId = todoBean.WFS_ID;
            data = {_ORDER_: 'NODE_STEPS desc', _extWhere: "and WFS_ID ='" + wfsId + "'", _NOPAGE_: true};
            var nodeListBean = FireFly.doAct('TS_WFS_NODE_APPLY', 'query', data);
            var nodeList = nodeListBean._DATA_;
            if (nodeList.length > 0) {
                var flowView = jQuery('#flowView');
                flowView.html('');
                flowView.append(
                    [
                        '<div style="display: inline-block">',
                        '<span style="position: relative;left: 18px;top:2px;',
                        'font-weight: 700;font-style: normal;font-size: 14px;color: #FFFFFF;">1</span>',
                        '<img alt="" style="width:20px;" src="<%=CONTEXT_PATH %>/ts/image/u5522.png">',
                        '<span style="position: relative;font-size: 15px;top: 5px;">&nbsp;&nbsp;填写申请单&nbsp;&nbsp;</span>',
                        '</div>'
                    ].join('')
                );
                for (var i = 0; i < nodeList.length; i++) {
                    var node = nodeList[i];
                    var nodeName = node.NODE_NAME;
                    var nodeSteps = node.NODE_STEPS;
                    flowView.append(
                        [
                            '<img alt=""  style="width:50px;height:5px;" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">',
                            '<div style="display: inline-block">',
                            '   <span style="position: relative;left: 18px;top:2px;',
                            '       font-weight: 700;font-style: normal;font-size: 14px;color: #FFFFFF;">' + (i + 2) + '</span>',
                            '   <img alt="" style="width:20px;" src="<%=CONTEXT_PATH %>/ts/image/' + (nodeSteps === todoBean.NODE_STEPS ? 'u5520.png' : 'u5522.png') + '">',
                            '   <span style="position: relative;font-size: 15px;top: 5px;">&nbsp;&nbsp;' + nodeName + '&nbsp;&nbsp;</span>',
                            '</div>'
                        ].join('')
                    );
                }
            }


        }
        //
    }

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
        param.type = 'tuihui';
        updateData(param);
    }

    //提交审批
    function updateData(param) {
        FireFly.doAct("TS_JKLB_JK", "updateData", param, false, false, function (response) {
            if (response._MSG_.indexOf('ERROR,') >= 0) {
                //发起申请出错
                alert(response._MSG_.substring(response._MSG_.indexOf('ERROR,'), response._MSG_.length));
            } else {
                if (param.type === 'tuihui') {
                    fanhui();
                } else {
                    //模态框
                    var $tiJiao = $('#tiJiao');
                    $('#shrNames').html(response.shrNames);
                    //关闭提示框后返回到请假页面
                    $tiJiao.on('hidden.bs.modal', function (/*e*/) {
                        fanhui();
                    });
                    //显示提示框
                    $tiJiao.modal('show');
                }
            }
        });
    }

    //下载证明材料
    function downImg(fileId) {
        rh.ui.File.prototype.downloadFile(fileId);
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
