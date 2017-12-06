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
    <title>我的请假</title>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
          name="viewport">

    <%@ include file="../../sy/base/view/inHeader-icbc.jsp" %>
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

    <style>
        /*表格行颜色设置*/
        #ybmtable1 > tbody > tr:nth-of-type(odd), #ybmtable2 > tbody > tr:nth-of-type(odd) {
            background-color: Azure;
        }

    </style>
</head>
<body class="hold-transition skin-black sidebar-mini">
<%
    //    String user_code = userBean.getStr("USER_CODE");
//	//获取用户名称
//	String user_name = userBean.getStr("USER_NAME");
//	//获取用户性别
//	String user_sex = userBean.getStr("USER_SEX");
//	//获取用户机构
//	String dept_name = userBean.getStr("DEPT_NAME");
//	//获取用户办公电话
//	String user_office_phone = userBean.getStr("USER_OFFICE_PHONE");
//	//获取用户手机号码
//	String user_mobile = userBean.getStr("USER_MOBILE");
//	//获取用户入行时间
//	String user_cmpy_date =userBean.getStr("USER_CMPY_DATE");

%>
<%@ include file="../../qt/jsp/header-logo.jsp" %>
<div class="" style="padding: 10px">
    <a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img style="padding-bottom: 10px ;color: #388CAE;"
                                                  src="<%=CONTEXT_PATH%>/ts/image/Home_16x16.png" id="shouye"></a>
    <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;我的请假</span>
</div>
<%--tab标签页--%>
<table id="myTab" class="nav nav-tabs"
       style="margin-left: 10px; width: 98%; background-color: white">
    <tr style="height: 70px">
        <td id="keshenqingtd" class="active"
            style="border-bottom:white solid 1px;width: 50%; text-align: center; font-size: 25px">
            <img style="margin-bottom:10px" src="<%=CONTEXT_PATH%>/ts/image/u975.png" id="keshenimage">
            <a id="akeshen" href="#home" data-toggle="tab">
                <span id="keshen" style="color: lightseagreen">可申请的请假</span>
            </a>
        </td>
        <td id="yishenqingtd" class=""
            style="border-bottom:lightgray solid 1px;width: 50%; text-align: center; font-size: 25px">
            <img style="margin-bottom:10px" src="<%=CONTEXT_PATH%>/ts/image/u984.png" id="yishenimage">
            <a id="ayishen" href="#tab2" data-toggle="tab">
                <span id="yishen" style="color: black">已申请的请假</span>
            </a>
        </td>
    </tr>

</table>
<%--tab内容页--%>
<div id="myTabContent" class="tab-content">
    <div class="tab-pane fade in active" id="home">
        <%----%>
        <div style="margin-top: -5px; margin-left: 19%; height: 5px; width: 190px; background-color: LightSeaGreen"></div>
        <div id="cuxian1"
             style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
            <span style="margin-left: 50px; padding-top: 10px">可申请的请假</span>
            <%--<div style="float:right;">
                <a onclick="qingjia()"
                   style="color:white;font-size:20px;background-color:LightSeaGreen;height:45px;width:140px;margin:15px;cursor: pointer;">
                    <i class="fa fa-plus-circle" aria-hidden="true"></i>
                    我要请假
                </a>
            </div>--%>
        </div>
        <div style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
        <div id="table1" class="" style="margin-left: 10px; width: 98%">
            <div class="content-main1">
                <table id="ybmtable1" style="padding: 10px;width:100%;">
                    <thead>
                    <tr style="backGround-color: WhiteSmoke; height: 30px">
                        <%--<th style="width: 10%;text-align: center;" align="center;"><input type="checkbox" id="checkall">
                        </th>--%>
                        <th style="width: 5%;text-align: center">序号</th>
                        <th style="width: 27%;">项目名称</th>
                        <th style="width: 25%">操作</th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
                <%-- <button id="wyqj" class="btn btn-success"
                         style="top: 20px;position: relative;font-size:16px;width: 120px;height:35px;background-color: #00c2c2;left: 43%;"
                         data-toggle="modal" data-target="#wyqj" onclick="qingjia()">我要请假
                 </button>--%>
            </div>
        </div>
    </div>
    <div class="tab-pane fade" id="tab2">
        <div style="margin-top: -6px; margin-left: 68%; height: 5px; width: 190px; background-color: LightSeaGreen"></div>
        <div id="cuxian2"
             style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
            <span style="margin-left: 50px; padding-top: 10px">已申请的请假</span>
        </div>
        <div style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
        <div id="table2" class="" style="margin-left: 10px; width: 98%">
            <div class="content-main1">
                <table id="ybmtable2" style="padding: 10px;width:100%;">
                    <thead>
                    <tr style="backGround-color: WhiteSmoke; height: 30px">
                        <td style="width: 6%;" align="left">序号</td>
                        <td style="width: 25%;">标题</td>
                        <td style="width: 20%;">单位</td>
                        <td style="width: 10%;">请假人</td>
                        <td style="width: 10%">创建时间</td>
                        <td style="width: 9%;text-align: center ">审核状态</td>
                        <td style="width: 20%; text-align: center">操作</td>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>

            </div>
        </div>
    </div>
</div>

<%--确认撤回--%>
<div class="modal" style="z-index: 999999999" id="retractModal" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h5 class="modal-title">
                    撤回确定
                </h5>
            </div>
            <div class="modal-body" style="padding: 24px;">
                是否撤回该请假？
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button id="" type="button" class="btn btn-success" onclick=""
                        data-dismiss="modal" style="width:100px;background-color: #00c2c2;">
                    确定
                </button>
                <button type="button" class="btn btn-default" onclick=""
                        data-dismiss="modal" style="width:100px;background-color: #fff;">
                    取消
                </button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<script>
    //全选，全不选
    $("#checkall").click(
        function () {
            $("input[name='bm_id']").prop('checked', !!this.checked);
        }
    );
    //可选报名  已选报名字体图片改变
    $('#akeshen').click(function () {
        document.getElementById("keshen").style.color = "LightSeaGreen";
        document.getElementById("keshenimage").src = "/ts/image/u975.png";
        document.getElementById("yishenimage").src = "/ts/image/u984.png";
        document.getElementById("yishen").style.color = "black";
    });
    $('#ayishen').click(function () {
        document.getElementById("keshen").style.color = "black";
        document.getElementById("yishenimage").src = "/ts/image/u7733.png";
        document.getElementById("keshenimage").src = "/ts/image/u1131.png";
        document.getElementById("yishen").style.color = "LightSeaGreen";
    });
</script>
<script type="text/javascript">
    $(function () {

        $("#retractModal").find('button[class="btn btn-success"]').bind('click', function () {
            var qjId = $(this).attr('id');
            if (qjId !== '') {
                FireFly.doAct('TS_QJLB_QJ', 'retract', {QJ_ID: qjId}, false, false, function (response) {
                    if (response._MSG_.indexOf('ERROR') >= 0) {
                        //撤回出错
                        alert(response._MSG_.substring(response._MSG_.indexOf('ERROR,') + 6, response._MSG_.length));
                    } else {
                        setAppliedLeaveList();
                        alert('撤回成功');
                    }
                    $("#retractModal").find('button[class="btn btn-success"]').attr('id', '');
                });
            }
        });

        var currentUserCode = System.getUser("USER_CODE");

        /*可申请的请假列表*/
        var table1Tbody = jQuery('#ybmtable1 tbody');
        table1Tbody.html('');
        //获取可申请的请假数据
        var data = {USER_CODE: currentUserCode};
        var userCanLeaveList = FireFly.doAct('TS_QJLB_QJ', 'getUserCanLeaveXmList', data);
        //没有可请假的考试，我要请假 置灰
        if (userCanLeaveList._DATA_.length <= 0) {
            $('#wyqj').attr('disabled', 'disabled');
        }
        for (var i = 0; i < userCanLeaveList._DATA_.length; i++) {
            var userCanLeave = userCanLeaveList._DATA_[i];

            /*'   <td class="rhGrid-td-hide">',
             '       ' + userCanLeave.BM_ID + '',
             '   </td>',
             '   <td align="center">',
             '       <input type="checkbox" name="bm_id" value="' + userCanLeave.BM_ID + '"/>',
             '   </td>',*/
            table1Tbody.append([
                '<tr style="height: 50px">',
                '   <td style="padding-left: 10px;text-align: center"">',
                '       ' + (i + 1),
                '   </td>',
                '   <td>',
                '       ' + userCanLeave.XM_NAME,//userCanLeave.title
                '   </td>',
                '   <td>',
                '       <input type="button" id="' + userCanLeave.XM_ID + '" onclick="qingjia2(this)" value="请假" style="color:white;font-size:15px;background-color:LightSeaGreen;height:30px;width:70px">',
                '   </td>',
                '</tr>'
            ].join(''));
        }
        // <a id="' + userCanLeave.XM_ID + '" onclick="qingjia2(this)">请假</a>',//id 为项目id

        setAppliedLeaveList();
    });

    /**
     * 已申请的请假
     **/
    function setAppliedLeaveList() {
        var currentUserCode = System.getUser("USER_CODE");

        /*已申请的请假*/
        var table1Tbody2 = jQuery('#ybmtable2 tbody');
        table1Tbody2.html('');
        //获取已申请的请假数据
        var data = {_SELECT_: '*', _extWhere: "and USER_CODE='" + currentUserCode + "'", _NOPAGE_: true};
        var qjListBean = FireFly.doAct('TS_QJLB_QJ', 'getAppliedLeaveList', data);
        var qjList = qjListBean._DATA_;
        for (var i = 0; i < qjList.length; i++) {
            var qj = qjList[i];
            var qjId = qj.QJ_ID;
            var qjName = qj.QJ_NAME;
            var qjTitle = qj.QJ_TITLE;
            var qjDanwei = qj.QJ_DANWEI;
            var qjDate = qj.QJ_DATE;
            var qjStatus = qj.QJ_STATUS;
            if (qjStatus === "1") {
                qjStatus = "审核中";
            } else if (qjStatus === "2") {
                qjStatus = "已通过";
            } else if (qjStatus === "3") {
                qjStatus = "未通过";
            } else if (qjStatus === "4") {
                qjStatus = "已撤回";
            }

            var $tr = jQuery([
                '<tr style="height: 50px">',
                '	<td class="rhGrid-td-hide">',
                '	    ' + qjId,
                '	</td>',
                '	<td style="padding-left: 10px;text-align: left;">',
                '	    ' + (i + 1),
                '	</td>',
                '	<td>',
                '	    ' + qjTitle,
                '	</td>',
                '	<td>',
                '	    ' + qjDanwei,
                '	</td>',
                '	<td>',
                '	    ' + qjName,
                '	</td>',
                '	<td>',
                '	    ' + qjDate,
                '	</td>',
                '	<td  style="text-align: center">',
                '	    ' + qjStatus,
                '	</td>',
                '</tr>'
            ].join(''));

            var $retract = jQuery(['<input type="button" id="' + qjId + '" value="撤回"',
                ' style="color:white;font-size:15px;background-color:LightSeaGreen;height:30px;width:70px"/>'
            ].join(''));
            $retract.unbind('click').bind('click', function () {
                var qjId = $(this).attr('id');
                var $retractModal = $('#retractModal');
                $retractModal.find('button[class="btn btn-success"]').attr('id', qjId);
                $retractModal.modal({backdrop: false, show: true});
            });

            var $td = jQuery(['	<td style="text-align: center">',
                '	    <input type="button" onclick="chakan(this)" value="查看"',
                '	        style="color:white;font-size:15px;background-color:LightSeaGreen;height:30px;width:70px"/>',
                '	</td>'].join(''));
            if (qj.CANRETRACT === 'true' && qj.QJ_STATUS !== "4") {
                $td.append($retract);
            }

            $tr.append($td);
            table1Tbody2.append($tr);
        }
    }

    //请假跳转到请假页面
    function qingjia() {
        var pkCode = document.getElementsByName("bm_id");
        var bmids = "";
        for (var i = 0; i < pkCode.length; i++) {
            if (pkCode[i].checked) {
                if (i === 0) {
                    bmids = pkCode[i].value;
                } else {
                    bmids += "," + pkCode[i].value;
                }
            }
        }
        doPost('qjlb_qj.jsp', {bmids: bmids});
    }

    //请假跳转到请假页面
    function qingjia2(e) {
        var xmId = $(e).attr('id');
        var xmSzBean = FireFly.doAct('ts_xmgl_sz', 'query', {_extWhere: " and XM_ID = '" + xmId + "' and XM_SZ_NAME ='考场安排'"});
        if (xmSzBean._DATA_.length > 0 && (xmSzBean._DATA_[0].XM_SZ_TYPE === '未开启' || xmSzBean._DATA_[0].XM_SZ_TYPE === '')) {
            alert('考场安排未开始，您可在我的报名已申请报名页面撤销报名');
        } else {
            doPost('qjlb_qj.jsp', {xmId: xmId});
        }
    }

    //已申请的请假列表 点击进行查看
    function chakan(obj) {
        var pkCode = obj.parentNode.parentNode.getElementsByTagName("td")[0].innerHTML.trim();
        doPost('qjlb_qj2.jsp', {qjid: pkCode, hidden: ""});//hidden为空 查看
    }

    /**
     * 实现post请求
     * @param to 例：/ts/jsp/qjlb_qj2.jsp
     * @param data {property1:value1,property2:value2}
     */
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
