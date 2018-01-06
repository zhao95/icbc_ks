<%@ page import="com.rh.core.serv.ServMgr" %>
<%@ page import="com.rh.ts.util.TsConstant" %>
<%@ page import="com.rh.core.serv.ParamBean" %>
<%@ page import="com.rh.core.serv.OutBean" %>
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
    <title>我的借考</title>
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

        #jkks-table > tbody > tr:nth-of-type(odd) {
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
<div style="background-color: #dfdfdf;padding: 0 10%;">
    <div style="background-color: #fdfdfd;padding-bottom: 30px" class="container-fluid">

        <div class="row">
            <div class="col-sm-12 text-center" style="background-color:#dfdfdf;">
                <div id="flowView" style="min-height: 50px;padding:10px;">
                    <div style="display: inline-block">
                                <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial',serif;
                     font-weight: 700;font-style: normal;font-size: 16px;color: #FFFFFF;">1</span>
                        <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5520.png">
                        <span style="position: relative;font-size: 15px;top: 5px;">&nbsp;&nbsp;填写申请单&nbsp;&nbsp;</span>
                    </div>

                    <img alt="" style="width: 50px;height:5px;" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">

                    <div style="display: inline-block">
                                <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial',serif;
                     font-weight: 700;font-style: normal;font-size: 16px;">2</span>
                        <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5522.png">
                        <span style="position: relative;font-size: 15px;top: 5px;">&nbsp;&nbsp;部门领导审批&nbsp;&nbsp;</span>
                    </div>

                    <img alt="" style="width: 50px;height:5px;" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">

                    <div style="display: inline-block">
                                <span style="position: relative;left: 26px;top:3px;
                     font-family: 'Arial Negreta', 'Arial Normal', 'Arial',serif;
                     font-weight: 700;font-style: normal;font-size: 16px;">3</span>
                        <img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5524.png">
                        <span style="position: relative;font-size: 15px;top: 5px;">&nbsp;&nbsp;考务管理人员审批</span>
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

        <div class="row" style="padding-top:50px">
            <div class="col-sm-2">
            </div>
            <%-- <div  class="col-sm-9">
                <div style="background-color: #fed1d1;border:1px solid red;border-radius: 5px;padding: 5px;color: #570000;">
                    ！ 温馨提示：您今年已请假 <span style="color: red">2</span> 次，还可请假 <span style="color: red">1</span> 次。
                    满 <span style="color: red">3</span> 次后，本年度将不允许再请假。请合理使用请假次数！
                </div>
            </div>--%>
        </div>

        <div class="row">
            <div class="col-sm-12">
                <span style="font-size: 18px;margin-left:60px;">借考申请</span>
                <hr/>
            </div>
        </div>

        <form class="form-horizontal" style="padding-right: 50px">
            <div class="form-group">
                <label for="jktitle" class="col-sm-2 control-label">
                    项目名称
                    <span style="color: red;font-weight: bold">*</span></label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="jktitle" readonly>
                </div>
            </div>

            <div class="form-group">
                <label for="jkyiji" class="col-sm-2 control-label">
                    借考一级分行
                    <span style="color: red;font-weight: bold">*</span>
                </label>
                <div class="col-sm-4">
                    <select id="jkyiji" class="form-control"></select>
                </div>
                <label for="jkcity" class="col-sm-2 control-label">
                    希望借考的城市
                    <span style="color: red;font-weight: bold">*</span>
                </label>
                <div class="col-sm-4">
                    <input type="text" class="form-control" id="jkcity">
                </div>
            </div>

            <div class="form-group">
                <label for="jkdks" class="col-sm-2 control-label">
                    借考的考试
                    <span style="color: red;font-weight: bold">*</span>
                </label>
                <div class="col-sm-10 bkuan">
                    <div onclick="xuanze()" data-toggle="modal" data-target="#myModal" id="jkdks"
                         style="display: inline-block;cursor: pointer;color: #91dce4;padding:5px;">
                        <a href="#" style="font-size: 15px ; color: #91dce4;" title="请选择">
                            <img src="<%=CONTEXT_PATH %>/ts/image/0255.png"/>
                            <%--<i class="fa fa-search" aria-hidden="true" style="font-size: 15px;"></i>--%>请选择</a>
                    </div>
                    <%--#4cd4d4 --%>
                    <div style="min-height: 120px">
                        <table id="jkks-table" style="width: 100%;border-color: white;">
                            <thead>
                            <tr style="padding-left: 5px;text-align: center">
                                <td width="35%">考试名称</td>
                                <%--<td width="35%">考试开始时间</td>--%>
                                <td width="30%">操作</td>
                            </tr>
                            </thead>
                            <tbody border="1" style="background-color: #f0f0f0;width: 100%;border-color: lightgray;">
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
                    <input type="text" class="form-control" value="<%=user_name%>" disabled>
                </div>
                <label class="col-sm-2 control-label">
                    部门
                </label>
                <div class="col-sm-5">
                    <input type="text" id="bumen" class="form-control" value="<%=dept_name %>" readonly>
                </div>
            </div>

            <div class="form-group">
                <label for="jkreason" class="col-sm-2 control-label">
                    借考事由
                    <span style="color: red;font-weight: bold">*</span>
                </label>
                <div class="col-sm-10">
                    <textarea id="jkreason" class="form-control" rows="3"></textarea>
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
                            <div id="localImag" style="display: block;height: 88px">
                                <img id="preview"
                                     style="display: none; width: 88px; height: 88px;"/>
                            </div>
                        </div>
                        <div class="col-sm-4" style="line-height: 88px;">
                            <a id="deleteLocalImag" onclick="deleteImage()"
                               style="display: none;cursor: pointer;">删除</a>
                        </div>
                    </div>

                </div>
            </div>
            <div class="col-sm-12">
                <input id="fileupload" type="file" name="files" multiple style="display: none">
            </div>

        </form>

        <div class="row">
            <div class="col-sm-offset-4 col-sm-3">
                <button onclick="applyForLeave()" class="btn btn-success"
                        style="width:150px;height:45px;background-color: #00c2c2;font-size: 16px;">
                    提交申请
                </button>
            </div>
            <div class="col-sm-3">
                <button onclick="back()" class="btn btn-success"
                        style="width:150px;height:45px;background-color: #00c2c2;font-size: 16px;">
                    返回
                    <div id="serverResponse"></div>
                </button>
            </div>
        </div>

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
                    选择需要借考的考试
                </h5>
            </div>
            <div class="modal-body zgks">
                <table border="1" style="width: 100%;" id="tabletjId">
                    <thead>
                    <tr style="height:30px">
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
                        style="width:100px;background-color: #00c2c2;" onclick="fuzhi()">确定
                </button>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <button type="button" class="btn btn-default" data-dismiss="modal"
                        style="color:white;width:100px;background-color: #00c2c2;">返回
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
        $('#jktitle').val(xmName);//标题


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
            url: '/file',
            dataType: 'json',
            add: function (e, data) {
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
        doPost('jklb.jsp', {});
    }

    var uploadFiles = function () {

    };

    $(function () {
        initData(xmId);

        uploadFiles = function () {
            $('#fileupload').click();
        };
        /*可申请的请假列表*/
        var table1Tbody = jQuery('#tabletjId tbody');
        table1Tbody.html('');
        //获取可申请的请假数据
        var data = {USER_CODE: System.getUser("USER_CODE"), XM_ID: xmId};
        var userCanLeaveList = FireFly.doAct('TS_JKLB_JK', 'getUserCanLeaveList', data);
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
//            '   <td style="text-align: center">' + userCanLeave.lbDate,
//                '   </td>',
        }


        /*将参数bmId对应的，报名添加到请假的考试列表中*/
        var bmIdStr = '<%=bmIdStr%>';
        var bmIds = bmIdStr.split(',');
        var kslxArray = document.getElementsByName("checkname1");

        for (var i = 0; i < bmIds.length; i++) {
            var bmId = bmIds[i];
            for (var j = 0; j < kslxArray.length; j++) {
                var kslx = kslxArray[j];
                if (bmId === kslx.value) {
                    addToSelectedKSTable(kslx);
                }
            }
        }

        //下拉选的初始化
        var dictCode = "TS_JKLB_JKCITY";
        var temp = FireFly.getDict(dictCode);
        var arr = temp[0].CHILD;


        var $jkyiji = $('#jkyiji');
        $jkyiji.append('<option value=""></option>');

        for (var i = 0; i < arr.length; i++) {
            var item = arr[i];
            var itemId = item.ID, itemName = item.NAME;
            $jkyiji.append('<option value="' + itemId + '">' + itemName + '</option>');
        }
        //绑定change事件
        //var d= document.getElementById("jkyiji");
        //d.addEventListener("change",citySelect(d),false);
    });


    //添加到已选择请假列表中
    function addToSelectedKSTable(kslx) {
        var tr = kslx.parentNode.parentNode;
        var tds = tr.getElementsByTagName("td");
//                var xu_hao = tds[1].innerText.trim();
        var jk_name = tds[2].innerText.trim();
//        var ks_date = tds[3].innerText.trim();
        var bm_id = tds[3].innerText.trim();
        var jkksTbody = jQuery('#jkks-table tbody');
        jkksTbody.append(
            '<tr data-id="' + bm_id + '">' +
            '<td class="rhGrid-td-hide">' + bm_id + '</td>' +
            '<td align="center" width="35%">' + jk_name + '</td>' +
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
        window.location.href = "jklb.jsp";
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
        $('#jkks-table').find('tr[data-id="' + bmId + '"]').remove();
    }

    //提交申请
    function applyForLeave() {
        //var currentUserWorkNum = System.getUser("USER_WORK_NUM");
//        var data = {USER_WORK_NUM: currentUserWorkNum};

//        var imgformid = document.getElementById("imgformid");
        var jktitle = document.getElementById("jktitle").value;
        var jkyiji = document.getElementById("jkyiji").value;
        var jkcity = document.getElementById("jkcity").value;
        var bumen = document.getElementById("bumen").value;
        var jkreason = document.getElementById("jkreason").value;
        var bmidsArray = document.getElementsByName("bmids");
        var bmids = "";
        for (var i = 0; i < bmidsArray.length; i++) {
            if (i === 0) {
                bmids = bmidsArray[i].value;
            } else {
                bmids += "," + bmidsArray[i].value;
            }
        }

        if (jktitle === "") {
            alert("标题不能为空");
        } else if (bmids === "") {
            alert("请选择借考的考试");
        } else if (jkyiji === "") {
            alert("请选择借考一级分行");
        } else if (jkcity === "") {
            alert("借考城市不能为空");
        } else if (jkreason === "") {
            alert("借考事由不能为空");
        } else {
            if (/*$('#preview').css('display') === 'none' ||*/ fileData === undefined || fileData.files.length <= 0) {
                //没有证明材料不用上传
                saveData('');
            } else {
                fileData.submit();
            }
        }
    }

    function saveData(fileId) {
        var jktitle = document.getElementById("jktitle").value;
        var jkyiji = document.getElementById("jkyiji").value;
        var jkcity = document.getElementById("jkcity").value;
        var bumen = document.getElementById("bumen").value;
        var jkreason = document.getElementById("jkreason").value;
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
        param["jkimg"] = fileId;
        param["jktitle"] = jktitle;
        param["jkyiji"] = jkyiji;
        param["jkcity"] = jkcity;
        // param["user_work_num"] = currentUserWorkNum;
        param["user_code"] = System.getUser("USER_CODE");
        param["bumen"] = bumen;
        param["jkreason"] = jkreason;
        param["bmids"] = bmids;
        param["user_name"] = System.getUser("USER_NAME");

        FireFly.doAct("TS_JKLB_JK", "addData", param, false, false, function (response) {

            if (response._MSG_.indexOf('ERROR') >= 0) {
                //发起申请出错
                alert(
                    response._MSG_.substring(response._MSG_.indexOf('ERROR,') + 6, response._MSG_.length)
                );
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
<script type="text/javascript">
    //上传资料
    function upImg(callback) {
        var formData = new FormData($("#imgformid")[0]);
        return $.ajax({
            url: "<%=CONTEXT_PATH%>/sy/base/frame/coms/ueditor/jsp/imageUp.jsp",
            type: 'POST',
            data: formData,
            async: false,
            cache: false,
            contentType: false,
            processData: false,
            success: function (returndata) {
                returndata = JSON.parse(returndata.trim().replace(/\'/g, '\"'));
                if (returndata.url) {
                    if (callback) {
                        callback(returndata.url);
                    }
                } else {
                    alert('图片上传失败请重试！');
                }
            },
            error: function (returndata) {
            }
        });

//        var eles = [
//            [
//                {
//                    ele: {
//                        type: 'img',
//                        id: 'img1',
//                        name: 'files',
//                        title: '',
//                        extendAttr: {filed: 'deatil_img', handle: 'single', url: ''}
//                    }
//                }
//            ]
//        ];
//        var bsForm = new BSForm({eles: eles, autoLayout: true}).Render('formContainer2', function (bf) {
//
//            global.Fn.InitPlugin('img', 'formContainer2');
//
//        });

    }
    function deleteImage() {
        $('#preview').css('display', 'none');
        $('#deleteLocalImag').css('display', 'none');

        var caseImage = document.getElementById('caseImage');
        caseImage.value = null;
    }

    function viewImage(file) {
        $('#preview').css('display', 'block');
        $('#deleteLocalImag').css('display', 'inline-block');

        var preview = document.getElementById('preview');
        if (file.files && file.files[0]) {
            //火狐下
            preview.style.display = "block";
            preview.style.width = "88px";
            preview.style.height = "88px";
            preview.src = window.URL.createObjectURL(file.files[0]);
//            alert(preview.src);
        } else {
            //ie下，使用滤镜
            file.select();
            var imgSrc = document.selection.createRange().text;
            var localImagEl = document.getElementById("localImag");
            //必须设置初始大小
            localImagEl.style.width = "88px";
            localImagEl.style.height = "88px";
            try {
                localImagEl.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
                locem("DXImageTransform.Microsoft.AlphaImageLoader").src = imgSrc;
            } catch (e) {
                alert("您上传的图片格式不正确，请重新选择!");
                return false;
            }
            preview.style.display = 'none';
            document.selection.empty();
        }
        return true;
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
