<%@ page import="com.rh.core.serv.ServDao" %>
<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%
    final String CONTEXT_PATH = request.getContextPath();
    String bmIdStr = request.getParameter("bmids") != null ? request.getParameter("bmids") : "";//已选中的报名-传递到该页面的参数
    String xmId = request.getParameter("xmId") != null ? request.getParameter("xmId") : "";//项目id
%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>我的请假</title>
    <meta
            content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
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

    <script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
    <!-- Bootstrap 3.3.6 -->
    <script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
    <!--工具方法-->
    <script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/global.js"></script>
    <!--插件-->
    <script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/jquery.smart-form.js"></script>
    <script src="<%=CONTEXT_PATH%>/ts/js/jquery.iframe-transport.js"></script>
    <script src="<%=CONTEXT_PATH%>/ts/js/jquery.ui.widget.js"></script>
    <script src="<%=CONTEXT_PATH%>/ts/js/jquery.fileupload.js"></script>
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
        #tabletjId {
            border: lightgray;
        }

        #myModal .modal-dialog {
            position: absolute;
            top: 8%;
            left: 30%;
        }

        .bkuan table td {
            height: 50px;
        }

        #qjks-table > tbody > tr:nth-of-type(odd) {
            background-color: Azure;
        }
    </style>
</head>
<body class="hold-transition skin-black sidebar-mini">

<%
    String user_code = userBean.getStr("USER_CODE");
    //获取用户名称
    String user_name = userBean.getStr("USER_NAME");
    //获取用户机构编码
    String dept_code = userBean.getStr("DEPT_CODE");
    //获取用户机构
    String dept_name = userBean.getStr("DEPT_NAME");

    Bean bean = ServDao.find("SY_ORG_DEPT", dept_code);
    String codePath = bean.getStr("CODE_PATH");
    String[] splits = codePath.split("\\^");//0010100000^0020000000^0020000087^
    StringBuilder deptNameStr = new StringBuilder();
    for (int i = 1; i < splits.length; i++) {
        String split = splits[i];
        Bean itemBean = ServDao.find("SY_ORG_DEPT", split);
        if (itemBean == null) {
            continue;
        }
        String itemDeptName = itemBean.getStr("DEPT_NAME");
        deptNameStr.append("/").append(itemDeptName);
    }
    if (deptNameStr.length() > 0) {
        deptNameStr = new StringBuilder(deptNameStr.substring(1));
        dept_name = deptNameStr.toString();
    }

    //    //获取用户性别
    //    String user_sex = userBean.getStr("USER_SEX");
    //    //获取用户办公电话
    //    String user_office_phone = userBean.getStr("USER_OFFICE_PHONE");
    //    //获取用户手机号码
    //    String user_mobile = userBean.getStr("USER_MOBILE");
    //    //获取用户入行时间
    //    String user_cmpy_date = userBean.getStr("USER_CMPY_DATE");
%>
<%@ include file="../../qt/jsp/header-logo.jsp" %>
<div style="background-color: #dfdfdf; padding: 0 10%;">
    <div style="background-color: #fdfdfd; padding-bottom: 30px"
         class="container-fluid">

        <div class="row">
            <div class="col-sm-12 text-center"
                 style="background-color: #dfdfdf;">
                <div style="min-height: 50px; padding: 10px;">
                    <div style="display: inline-block">
							<span
                                    style="position: relative; left: 26px; top: 3px; font-family: 'Arial Negreta', 'Arial Normal', 'Arial', serif; font-weight: 700; font-style: normal; font-size: 16px; color: #FFFFFF;">1</span>
                        <img alt="" src="<%=CONTEXT_PATH%>/ts/image/u5520.png"> <span
                            style="position: relative; font-size: 15px; top: 5px;">&nbsp;&nbsp;填写申请单&nbsp;&nbsp;</span>
                    </div>

                    <img alt="" style="width: 50px;height:5px;" src="<%=CONTEXT_PATH%>/ts/image/u5532.png">

                    <div style="display: inline-block">
							<span
                                    style="position: relative; left: 26px; top: 3px; font-family: 'Arial Negreta', 'Arial Normal', 'Arial', serif; font-weight: 700; font-style: normal; font-size: 16px;">2</span>
                        <img alt="" src="<%=CONTEXT_PATH%>/ts/image/u5522.png"> <span
                            style="position: relative; font-size: 15px; top: 5px;">&nbsp;&nbsp;部门领导审批&nbsp;&nbsp;</span>
                    </div>

                    <img alt="" style="width: 50px;height:5px;" src="<%=CONTEXT_PATH%>/ts/image/u5532.png">

                    <div style="display: inline-block">
							<span
                                    style="position: relative; left: 26px; top: 3px; font-family: 'Arial Negreta', 'Arial Normal', 'Arial', serif; font-weight: 700; font-style: normal; font-size: 16px;">3</span>
                        <img alt="" src="<%=CONTEXT_PATH%>/ts/image/u5524.png"> <span
                            style="position: relative; font-size: 15px; top: 5px;">&nbsp;&nbsp;考务管理人员审批</span>
                    </div>

                    <%-- <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">

                 <div style="display: inline-block">
                             <span style="position: relative;left: 26px;top:3px;
                  font-family: 'Arial Negreta', 'Arial Normal', 'Arial';
                  font-weight: 700;font-style: normal;font-size: 16px;">3</span>
                     <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5524.png">
                     <span style="position: relative;font-size: 15px;top: 5px;">&nbsp;&nbsp;考务管理人员审批</span>
                 </div>--%>
                </div>
                <%--<table style="width:100%;height: 80px;">
                <tr style="backGround-color: #ababab; height: 30px">
                    <td style="text-align: center;">



                    </td>
                </tr>
            </table>--%>
            </div>
        </div>

        <div class="row" style="padding-top: 50px;">
            <div class="col-sm-2"></div>
            <div class="col-sm-9" style="padding: 0;">
                <div
                        style="background-color: #fed1d1; border: 1px solid red; border-radius: 5px; padding: 5px; color: #570000; min-width: 821px;">
                    ！ 温馨提示：本年度可请假 <span id="weekes" style="color: red;">  </span> 个考试周 (限<span
                        id="counts" style="color: red">  </span> 个考试), 您已请假 <span id="leaveWeekNum"
                                                                                  style="color: red">  </span> 个
                    考试周 ( <span id="leaveCiShuNum" style="color: red">   </span> 个考试),
                    请合理使用请假次数,超出将不允许请假！
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-sm-12">
                <span style="font-size: 18px; margin-left: 60px">请假申请</span>
                <hr/>
            </div>
        </div>

        <form class="form-horizontal" style="padding-right: 50px">
            <div class="form-group">
                <label for="qjtitle" class="col-sm-2 control-label">请假标题<span
                        style="color: red; font-weight: bold">*</span></label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="qjtitle" readonly>
                </div>
            </div>
            <div class="form-group">
                <label for="qjdks" class="col-sm-2 control-label">
                    请假的考试
                    <span style="color: red; font-weight: bold">*</span>
                </label>
                <div class="col-sm-10 bkuan">
                    <div onclick="xuanze()" data-toggle="modal" data-target="#myModal"
                         id="qjdks"
                         style="display: inline-block; cursor: pointer; color: #4cd4d4; padding: 5px;">
                        <a href="#" style="font-size: 15px; color: #91dce4;"> <img
                                src="<%=CONTEXT_PATH%>/ts/image/0255.png"/> <%-- <i class="fa fa-search" aria-hidden="true" style="font-size: 15px ; color: #91dce4;"></i>--%>请选择
                        </a>
                    </div>
                    <div style="min-height: 120px">
                        <table id="qjks-table" style="width: 100%; border-color: white;">
                            <thead>
                            <tr style="padding-left: 5px; text-align: center">
                                <td width="35%">考试名称</td>
                                <%--<td width="35%">考试开始时间</td>--%>
                                <td width="30%">操作</td>
                            </tr>
                            </thead>
                            <tbody style="background-color: #f0f0f0;" border="1">
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-2 control-label">请假人</label>
                <div class="col-sm-3">
                    <input type="text" class="form-control" value="<%=user_name%>"
                           disabled>
                </div>
                <label class="col-sm-2 control-label"> 部门 </label>
                <div class="col-sm-5">
                    <input type="text" id="bumen" class="form-control"
                           value="<%=dept_name%>" readonly>
                </div>
            </div>

            <div class="form-group">
                <label for="qjreason" class="col-sm-2 control-label">
                    <span style="color: red; font-weight: bold">*</span>
                    请假事由
                </label>
                <div class="col-sm-10">
                    <textarea id="qjreason" class="form-control" rows="3"></textarea>
                </div>
            </div>

            <div class="form-group">
                <label class="col-sm-2 control-label">证明材料</label>
                <div class="col-sm-10">
                    <div class="row">
                        <div class="col-sm-12">

                            <div class="form-group" id="caseIma2">
                                <label class="" style="cursor:pointer;" onclick="uploadFiles()"><%----%>
                                    <div style="float: left;background-image: url(<%=CONTEXT_PATH%>/ts/image/005.png);width: 32px;height: 32px;">
                                        &nbsp;&nbsp;
                                    </div>
                                    <div style="margin-top: 5px;margin-left: 2px;color: #91dce4;float: left;"
                                         href="#">上传
                                    </div>
                                </label>
                            </div>
                            <ul id="files">
                                <%--<li>
                                    <div>
                                        <div>asdfsfd.png</div>
                                        <span>del</span>
                                    </div>
                                </li>--%>
                            </ul>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-sm-4">
                            <div id="localImag" style="display: block; height: 88px">
                                <img id="preview"
                                     style="display: none; width: 88px; height: 88px;"/>
                            </div>
                        </div>
                        <div class="col-sm-4" style="line-height: 88px;">
                            <a id="deleteLocalImag" onclick="deleteImage()"
                               style="display: none; cursor: pointer;">删除</a>
                        </div>
                    </div>

                </div>
                <div class="col-sm-12"> <%--
                    <div id="fileupload" style="color:lightseagreen;font-size:20px">
                        &lt;%&ndash;<form action="/file" name="formup" id="formContainer2" class="form form-horizontal"></form>&ndash;%&gt;
                    </div>
                </div>--%>
                    <%--<div class="col-sm-12">
                        <div id="uploadfile" style="color:lightseagreen;font-size:20px">
                            <form action="/file" name="formup" id="formContainer2" class="form form-horizontal"></form>
                        </div>
                    </div>--%>
                    <input id="fileupload" type="file" name="files" multiple style="display: none">
                    <%--<a data-btn="btn_upload_many" onclick="uploadFiles();" href="javascript:void(0);"
                       class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-search'">批量上传</a>--%>
                </div>
            </div>

        </form>

        <div class="row">
            <div class="col-sm-offset-4 col-sm-3">
                <button onclick="applyForLeave()" class="btn btn-success"
                        style="width: 150px; height: 45px; background-color: #00c2c2;font-size: 16px;">
                    提交申请
                </button>
            </div>
            <div class="col-sm-3">
                <button onclick="back()" class="btn btn-success"
                        style="width: 150px; height: 45px; background-color: #00c2c2;font-size: 16px;">
                    返回
                    <div id="serverResponse"></div>
                </button>
            </div>
        </div>

    </div>

    <!-- <footer class="main-footer" id="main-fotter-div" style="background-color:#333333; margin-left: 0;">
    <div align="center">
        <img id="qt_footer_logo" class="img" src="/qt/img/666.png">
    </div>
    <div>
        <table>
            <tbody>
            <tr id="qt_footer_tr">
                <td width="20%"><span>版权所有：中国工商银行 </span></td>
                <td><span>|</span></td>
                <td width="30%"><span>地址：北京市复兴门内大街55号</span></td>
                <td><span></span></td>
                <td width="16%"><span>邮编：100140 </span></td>
                <td><span>|</span></td>
                <td width="14%" align="right"><span style="font-family:'微软雅黑';font-weight: 400;color: #CCCCCC;">2017 年 6 月 </span>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    </footer> -->

</div>

<%--模态窗口查询请假考试--%>
<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header"
                 style="background-color: #00c2c2; color: white">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;
                </button>
                <h5 class="modal-title">选择需要请假的考试</h5>
            </div>
            <div class="modal-body zgks">
                <table border="1" style="width: 100%;" id="tabletjId">
                    <thead>
                    <tr style="height: 30px;">
                        <td style="text-align: center" width="10%"></td>
                        <td width="10%" align="center">序号</td>
                        <td width="55%" align="center">名称</td>
                        <%--<td width="35" align="center" colspan="2">考试时间</td>--%>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" data-dismiss="modal"
                        style="width: 100px; background-color: #00c2c2;" onclick="fuzhi()">确定
                </button>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <button type="button" class="btn btn-default" data-dismiss="modal"
                        style="color: white; width: 100px; background-color: #00c2c2;">返回
                </button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal -->
</div>
<%--模态窗口回显审核人 --%>
<div class="modal fade" id="tiJiao" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header"
                 style="background-color: #00c2c2; color: white">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;
                </button>
                <h5 class="modal-title">下一环节审批人</h5>
            </div>
            <div class="modal-body zgks">
                <table style="width: 100%; height: 100px; border: 0;">
                    <tr>
                        <td style="text-align: center" width="10%">已经提交给<span
                                id="shrNames"></span>进行审核
                        </td>
                    </tr>
                </table>
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" data-dismiss="modal"
                        style="width: 100px; background-color: #00c2c2;">确定
                </button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal -->
</div>
<script type="text/javascript">
    var fileData = undefined;

    var indexOfLastModified = function (arr, lastModified) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i].lastModified === lastModified) return i;
        }
        return -1;
    };

    var fileRemove = function (arr, val) {
        var index = indexOfLastModified(arr, val);
        if (index > -1) {
            arr.splice(index, 1);
        }
    };

    function getUuid() {
        var len = 32;//32长度
        var radix = 16;//16进制
        var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
        var uuid = [], i;
        radix = radix || chars.length;
        if (len) {
            for (i = 0; i < len; i++)uuid[i] = chars[0 | Math.random() * radix];
        } else {
            var r;
            uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
            uuid[14] = '4';
            for (i = 0; i < 36; i++) {
                if (!uuid[i]) {
                    r = 0 | Math.random() * 16;
                    uuid[i] = chars[(i === 19) ? (r & 0x3) | 0x8 : r];
                }
            }
        }
        return uuid.join('');
    }

    var uuid = getUuid().substring(0, 32);

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

    function initData(xmId) {
        var xmBean = FireFly.doAct("TS_XMGL", 'byid', {_PK_: xmId});
        var xmName = xmBean.XM_NAME;
        $('#qjtitle').val(xmName);//标题

        //获取用户usercode
        var data = {USER_CODE: System.getUser("USER_CODE"), XM_ID: xmId};
        //获取最大请假的次数显示
        var countCon = System.getVar("@C_TS_KSQJ_SETCONUTS@");
        $("#counts").html(countCon);
        //获取最大的周次数
        var weekes = System.getVar("@C_TS_KSQJ_WEEK_MAXNUM@");
        $("#weekes").html(weekes);
        //考生已经借考过多少次
        //1.一个周内不能请假6次。2.不能超过2个考试周。3.考试的前后加减一天
        var getLeaveCount = FireFly.doAct('TS_QJLB_QJ', 'getLeaveCount', data);
        var leaveWeekNum = getLeaveCount.weekNum;
        var leaveCiShuNum = getLeaveCount.ciShuNum;
        $("#leaveWeekNum").html(leaveWeekNum);
        $("#leaveCiShuNum").html(leaveCiShuNum);

        /*可申请的请假列表*/
        var table1Tbody = $('#tabletjId ').find('tbody');
        table1Tbody.html('');
        //获取可申请的请假数据
        var userCanLeaveList = FireFly.doAct('TS_QJLB_QJ', 'getUserCanLeaveList', data);
        for (var i = 0; i < userCanLeaveList._DATA_.length; i++) {
            var userCanLeave = userCanLeaveList._DATA_[i];
            table1Tbody.append([
                '<tr>',
                '   <td style="text-align: center" width="10%">',
                '   <input type="checkbox" name="checkname1" value="' + userCanLeave.BM_ID + '">',
                '   </td>',
                '   <td align="center">' + (i + 1),
                '   </td>',
                '   <td align="center">' + userCanLeave.title,
                '   </td>',
                '   <td class="rhGrid-td-hide">' + userCanLeave.BM_ID,
                '   </td>',
                '</tr>'
            ].join(''));
        }
//        '   <td style="text-align: center">' + userCanLeave.lbDate,
//            '   </td>',


        /*将参数bmId对应的，报名添加到请假的考试列表中*/
        var bmIdStr = '<%=bmIdStr%>';
        var bmIds = bmIdStr.split(',');
        var kslxArray = document.getElementsByName("checkname1");

        for (var i = 0; i < bmIds.length; i++) {
            var bmId = bmIds[i];
            for (var j = 0; j < kslxArray.length; j++) {
                var kslx = kslxArray[j];
                if (bmId === kslx.value) {
                    addToSelectedKSTable(kslx)
                }
            }
        }

//        global.Fn.InitPlugin('file', 'formContainer2', 'sfdf');

        //fileupload
        var $fileUpload = $('#fileupload');
        // data赋值
        $fileUpload.bind('fileuploadsubmit', function (e, data) {
            // console.log(data);
            var jsonData = {};
            jsonData["DATA_ID"] = uuid;
            jsonData["SERV_ID"] = "TS_QJLB_QJ";
            data.formData = jsonData;
        });
        $fileUpload.fileupload({
            autoUpload: false,//是否自动上传
            acceptFileTypes: /(.|\/)(jpe?g|png)$/i,//文件格式限制
            maxNumberOfFiles: 1,//最大上传文件数目
            maxFileSize: 5000000,//文件不超过5M
            sequentialUploads: true,//是否队列上传
            url: '/file',
            dataType: 'json',
            add: function (e, data) {

                var uploadErrors = [];

                var acceptFileTypes = /\/(pdf|xml)$/i;
                if(data.originalFiles[0]['type'].length && !acceptFileTypes.test(data.originalFiles[0]['type'])) {
                    uploadErrors.push('Tipo de Archivo no Aceptado');
                }

                for (var i = 0; i < data.files.length; i++) {
                    var obj = data.files[i];
                    var $li = $([
                        '<li style="clear:both;margin-top:7px;">',
                        '     <div style="float: left;overflow:hidden;text-overflow:ellipsis; width: 200px" title="' + obj.name + '">' + obj.name + '</div>',
                        '     ',
                        '</li>'
                    ].join(''));
                    var $a1 = $('<span id="' + obj.lastModified + '" style="margin-left: 5px;cursor: pointer;color: blue;">删除</span>');
                    $a1.unbind('click').bind('click', function () {
                        var id = $(this).attr('id');
                        fileRemove(fileData.files, id);
                        $a1.parent().remove();
                    });
                    $li.append($a1);
                    $li.appendTo($('#files'));
                }
                if (fileData === undefined) {
                    fileData = data;
                } else {
                    fileData.files = fileData.files.concat(data.files);
                }
            },
            done: function (/*e, data*/) {
                saveData(uuid);
//                data.context.text('Upload finished.');
            },
            fail: function () {
                alert("上传附件失败");
            }
        });

    }

    var xmId = '<%=xmId%>';
    //xmId值为空返回到列表页面
    if (!xmId) {
        doPost('qjlb.jsp', {});
    }

    var uploadFiles = function () {

    };
    $(function () {
        initData(xmId);
        uploadFiles = function () {
            $('#fileupload').click();
        };

    });

    //添加到已选择请假列表中
    function addToSelectedKSTable(kslx) {
        var tr = kslx.parentNode.parentNode;
        var tds = tr.getElementsByTagName("td");
//                var xu_hao = tds[1].innerText.trim();
        var qj_name = tds[2].innerText.trim();
//        var ks_date = tds[3].innerText.trim();
        var bm_id = tds[3].innerText.trim();
        var qjksTbody = jQuery('#qjks-table tbody');
        qjksTbody.append(
            '<tr data-id="' + bm_id + '">' +
            '<td class="rhGrid-td-hide">' + bm_id + '</td>' +
            '<td align="center" width="35%">' + qj_name + '</td>' +
//            '<td align="center" width="35%">' + ks_date + '</td>' +
            '<td align="center" width="35%" >' +
            '   <a style="cursor:pointer;" data-id="' + bm_id + '" onclick="delOne(this)">删除</a>' +
            '</td>' +
            '<td class="rhGrid-td-hide"><input type="text" name="bmids" value="' + bm_id + '"></td>' +
            '</tr>'
        );
        kslx.disabled = true;
    }

    //返回
    function back() {
        window.location.href = "qjlb.jsp";
    }
    //根据选择的考试设置disabled和checked -- 若果考试存在，就不能进行选择   选择按钮点击事件
    function xuanze() {
        var bmidsArray = document.getElementsByName("bmids");
        var kslxArray = document.getElementsByName("checkname1");

        for (var i = 0; i < kslxArray.length; i++) {
            kslxArray[i].disabled = false;
            kslxArray[i].checked = false;
            for (var j = 0; j < bmidsArray.length; j++) {
                if (bmidsArray[j].value === kslxArray[i].value) {
                    kslxArray[i].disabled = true;
                    kslxArray[i].checked = true;
                }
            }
        }
    }
    //将选择的请假赋值给已选考试列表（table表格）
    function fuzhi() {
//        var tab = document.getElementById("tablehang");
        var kslxArray = document.getElementsByName("checkname1");
        for (var i = 0; i < kslxArray.length; i++) {
            var kslx = kslxArray[i];
            if (kslx.checked && !kslx.disabled) {
                addToSelectedKSTable(kslx);
            }
        }
    }
    //删除已选择的请假
    function delOne(delObj) {
        var bmId = $(delObj).attr("data-id");
        $('#qjks-table').find('tr[data-id="' + bmId + '"]').remove();
    }
    //提交申请
    function applyForLeave() {

//        var currentUserWorkNum = System.getUser("USER_WORK_NUM");
        var qjtitle = document.getElementById("qjtitle").value;
        var bumen = document.getElementById("bumen").value;
        var qjreason = document.getElementById("qjreason").value;
        var bmidsArray = document.getElementsByName("bmids");
        var bmids = "";
        for (var i = 0; i < bmidsArray.length; i++) {
            if (i === 0) {
                bmids = bmidsArray[i].value;
            } else {
                bmids += "," + bmidsArray[i].value;
            }
        }

        var cishu = FireFly.getConfig("TS_KSQJ_SETCONUTS").CONF_VALUE;
        var zhoushu = FireFly.getConfig("TS_KSQJ_WEEK_MAXNUM").CONF_VALUE;
        if (bmids === "") {
            alert("请选择请假的考试");
            return false;
        } else {
            var bmidarr = bmids.split(",");
            if (bmidarr.length > cishu) {
                alert("选择考试超过上限");
                return false;
            }
        }

        if (qjtitle === "") {
            alert("标题不能为空");
        } else if (qjreason === '' || qjreason === undefined) {
            alert('请假事由不能为空');
            return false;
        } else {
            var paramstr = {};
            paramstr["xm_id"] = xmId;
            paramstr["cishu"] = cishu;
            paramstr["bmids"] = bmids;
            paramstr["zhoushu"] = zhoushu;
            var result = FireFly.doAct("TS_BM_QJ_NUM", "getFlag", paramstr);

            if (result.yes === "true") {
            } else {
                alert(result._MSG_);
                return false;
            }

            //var currentUserWorkNum = System.getUser("USER_WORK_NUM");
//        var data = {USER_WORK_NUM: currentUserWorkNum};
            if (/*$('#preview').css('display') === 'none' ||*/ fileData === undefined || fileData.files.length <= 0) {
                //没有证明材料不用上传
                saveData('');
            } else {
                fileData.submit();
            }

        }
    }

    function saveData(fileId) {
        var currentUserWorkNum = System.getUser("USER_WORK_NUM");
        var qjtitle = document.getElementById("qjtitle").value;
        var bumen = document.getElementById("bumen").value;
        var qjreason = document.getElementById("qjreason").value;
        var bmidsArray = document.getElementsByName("bmids");
        var bmids = "";
        for (var i = 0; i < bmidsArray.length; i++) {
            if (i === 0) {
                bmids = bmidsArray[i].value;
            } else {
                bmids += "," + bmidsArray[i].value;
            }
        }

        var param = {};
        param["qjimg"] = fileId;
        param["qjtitle"] = qjtitle;
        param["user_work_num"] = currentUserWorkNum;
        param["user_code"] = System.getUser("USER_CODE");
        param["bumen"] = bumen;
        param["qjreason"] = qjreason;
        param["bmids"] = bmids;
        param["user_name"] = System.getUser("USER_NAME");
        FireFly.doAct("TS_QJLB_QJ", "addData", param, false, false, function (response) {

            if (response._MSG_.indexOf('ERROR') >= 0) {
                //发起申请出错
                alert(response._MSG_.substring(response._MSG_.indexOf('ERROR,') + 6, response._MSG_.length));
            } else {
                //模态框
                var $tiJiao = $('#tiJiao');
                $('#shrNames').html(response.shrNames);
                //关闭提示框后返回到请假页面
                $tiJiao.on('hidden.bs.modal', function (/*e*/) {
                    back();
                });
                //显示提示框
                $tiJiao.modal('show');
            }

        });
    }

</script>


<!-- FastClick -->
<script src="<%=CONTEXT_PATH%>/qt/plugins/fastclick/fastclick.js"></script>
<!-- AdminLTE App -->
<script src="<%=CONTEXT_PATH%>/qt/dist/js/app.min.js"></script>
<!-- AdminLTE for demo purposes -->
<script src="<%=CONTEXT_PATH%>/qt/dist/js/demo.js"></script>
</body>
</html>
