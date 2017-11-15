<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    final String CONTEXT_PATH = request.getContextPath();
    String xmId = request.getParameter("xmId") != null ? request.getParameter("xmId") : "";

    //todo  xmId值为空的处理
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>考位安排</title>
    <link rel="stylesheet" href="../js/dist/themes/default/style.min.css"/>
    <%--<script
            src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.1/jquery.min.js"></script>--%>

    <%@ include file="../../sy/base/view/inHeader-icbc.jsp" %>
    <script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
    <!-- AdminLTE Theme style -->
    <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/dist/css/AdminLTE.min.css">
    <!-- Bootstrap 3.3.6 -->
    <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/bootstrap/css/bootstrap.min.css">
    <!-- Bootstrap 3.3.6 -->
    <script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>
    <!-- 外部jquerytree库js脚本 -->
    <link rel="stylesheet" type="text/css" href="<%=CONTEXT_PATH %>/sy/base/frame/coms/tree/style.css"/>
    <script type="text/javascript" src="<%=CONTEXT_PATH %>/sy/base/frame/coms/tree/jquery.tree.js"></script>

    <script type="text/javascript" src="<%=CONTEXT_PATH %>/sy/base/frame/plugs/jquery-ui/rh.jquery-ui.min.js"></script>
    <link rel="stylesheet" type="text/css"
          href="<%=CONTEXT_PATH %>/sy/base/frame/plugs/jquery-ui/rh.jquery-ui.min.css"/>

    <!--jstree-->
    <script src="../js/dist/jstree.min.js"></script>

    <!-- Font Awesome -->
    <link rel="stylesheet"
          href="<%=CONTEXT_PATH%>/qt/font-awesome-4.7.0/css/font-awesome.min.css">

    <%--jquery.ui.droppable.min.js--%>

    <style type="text/css">
        /*自动分配场次 模态框样式*/
        #kwap-body .modal {
            background: transparent;
        }

        #kwap-body .modal-backdrop {
            background: transparent;
        }

        /**/
        #allocation-rule .checkbox {
            margin-top: 0;
        }

        .tip-red {
            color: red;
        }

        /*表格样式*/
        #submissionArrangement-table > tbody > tr:nth-of-type(even) {
            background-color: Azure;
        }

        /*#examref-table > tbody > tr > td, #examref-table > thead > tr > th {
                    padding: 5px;
                }*/
        #submissionArrangement-table > thead > tr > th {
            font-weight: bold
        }

        #selectXL-table > tbody > tr:nth-of-type(even) {
            background-color: Azure;
        }

        /*#examref-table > tbody > tr > td, #examref-table > thead > tr > th {
                    padding: 5px;
                }*/
        #selectXL-table > thead > tr > th {
            font-weight: bold
        }

        /*滚动条样式*/
        ._scrollbar::-webkit-scrollbar {
            -webkit-appearance: none;
        }

        ._scrollbar::-webkit-scrollbar-thumb {
            min-height: 2rem;
            background: #ccc;
            background-clip: padding-box;
            border: 5px solid transparent;
            border-radius: 10px;
        }

        ._scrollbar::-webkit-scrollbar-corner {
            display: none
        }

        #search-input > div {
            padding-left: 5px;
            padding-right: 5px;
        }

        #search-input select {
            border: 1px solid #a7aab0;
            margin-left: 2px;
        }

        #search-input input {
            margin-left: 5px;
        }

        /*考生表 场次表*/
        #ksTable > tbody > tr:nth-of-type(even), #kcInfo > tbody > tr:nth-of-type(even) {
            background-color: #faffff;
        }

        /*droppable*/
        #kcInfo .ui-droppable {
            opacity: 1 !important;
        }

        .droppable-hover {
            background: #c4ffb3 !important;
            /*background-color: #00FF40;*/
        }

        .droppable-active {
            background: #b3ebff;
            /*background-color: #b3ebff;*/
        }

    </style>
</head>

<body id="kwap-body">

<div class="wrapper" style="height: auto;">

    <header class="main-header">
        <div class="rhGrid-btnBar" style="background-color: #d9eaf4;">
            <a class="rh-icon rhGrid-btnBar-a" id="zdfpcc" actcode="zdfpcc" title="">
                <span class="rh-icon-inner">自动分配场次</span>
                <span class="rh-icon-img btn-option"></span>
            </a>
            <a class="rh-icon rhGrid-btnBar-a" id="updatecc" actcode="updatecc"
               title="">
                <span class="rh-icon-inner">更改场次</span>
                <span class="rh-icon-img btn-edit"></span>
            </a>
            <a class="rh-icon rhGrid-btnBar-a" id="submissionArrangement" actcode="submissionArrangement"
               title="">
                <span class="rh-icon-inner">查看场次安排情况</span>
                <span class="rh-icon-img btn-search"></span>
            </a>
            <a
                    class="rh-icon rhGrid-btnBar-a" id="lookJk" actcode="lookJk" title="">
            <span
                    class="rh-icon-inner">查看借考人员</span><span class="rh-icon-img btn-user"></span>
            </a>
            <a class="rh-icon rhGrid-btnBar-a" id="xngs" actcode="xngs" title="">
                <span class="rh-icon-inner">辖内公示</span><span class="rh-icon-img btn-report"></span>
            </a>
            <a class="rh-icon rhGrid-btnBar-a" id="tjccap" actcode="tjccap" title="">
                <span class="rh-icon-inner">提交场次安排</span><span class="rh-icon-img btn-ok"></span>
            </a>
            <a style="display: none;" class="rh-icon rhGrid-btnBar-a" id="publish" actcode="publish" title="">
                <span class="rh-icon-inner">发布场次安排</span><span class="rh-icon-img btn-ok"></span>
            </a>
            <a style="display: none;" class="rh-icon rhGrid-btnBar-a" id="unPublish" actcode="unPublish" title="">
                <span class="rh-icon-inner">取消发布场次安排</span><span class="rh-icon-img btn-ok"></span>
            </a>
        </div>
    </header>

    <aside class="main-sidebar"
           style="padding-top:30px; border-right: 1px solid #bfdfee;width: 250px;height:100%;">
        <!-- sidebar: style can be found in sidebar.less -->
        <section id="left-sidebar-content" class="sidebar" style="height:100%;">
            <div class="row">
                <div class="col-sm-12" style="border-top: 1px solid #bfdfee;"></div>
            </div>
            <div class="_scrollbar" style="height:100%;padding-top: 5px;padding-left: 5px;overflow: auto;">
                <button id="toggle-sidebar"
                        style="position: absolute;right: 0;top: 30px;padding:3px">
                    <i class="fa fa-angle-left"></i>
                </button>
                <%--<span onclick="KcObject.setAllKcInfo()" style="cursor:pointer;">考场场次</span>--%>
                <div id="ccJstree">

                </div>
            </div>

        </section>
    </aside>

    <div class="content-wrapper"
         style="background-color:transparent;min-height: 642px;border-top: 1px solid #bfdfee;margin-left:250px;">
        <!-- Content Header (Page header) -->
        <section id="search-input" class="content-header"
                 style="height:33px;padding:5px;background-color:#e5eeef;background: linear-gradient(180deg, rgba(223, 234, 234, 1) 0%, rgba(223, 234, 234, 1) 2%, rgba(255, 255, 255, 1) 100%, rgba(255, 255, 255, 1) 100%);">
            <div class="col-sm-2" style="width: 12%;min-width: 93px;max-width: 100px">
                姓名<input id="search-name" style="width:50px;" type="text" title="">
            </div>
            <div class="col-sm-2" style="width: 14%;min-width: 108px;max-width: 120px">
                登录名<input id="search-login-name" style="width:50px;" type="text" title="">
            </div>
            <div class="col-sm-4" style="width: 54%;min-width: 421px;max-width:430px;">
                报考类型
                <select id="search-bm-xl" style=""></select>
                <select id="search-bm-mk" style="width:176px;" type="text" title=""></select>
                <select id="search-bm-jb" type="text" title=""></select>
            </div>
            <%--<div class="col-sm-2" style="width: 10%;min-width: 82px;max-width:90px;">
                报考数<input id="search-bm-count" type="text" title="" style="width: 24px;">
            </div>--%>
            <div class="col-sm-2" style="width: 7%;float: right">
                <button onclick="KsObject.search()" class="btn btn-default" value="查询"
                        style="background: linear-gradient(180deg, rgb(123, 202, 249) 0%, rgb(136, 218, 236) 2%, rgba(148, 221, 245, 1) 3%, rgb(75, 189, 239) 93%, rgb(91, 200, 218) 96%, rgb(155, 214, 243) 100%);
                        background-color:#5bc8da;color: #fff;padding: 2px 11px;">
                    查询
                </button>
            </div>
        </section>

        <section class="content" style="padding:0;background-color: transparent;">
            <div id="topContent" class="row" style="border-top: 1px solid #bfdfee;margin:0;">

                <div class="col-sm-3"
                     style="padding-left: 0;padding-right: 0;height: 100%;border-right: 1px solid #bfdfee;">
                    <div class="row">
                        <div class="col-sm-12">
                            <div style="padding: 5px;background-color: #e4e8ec;">
                                人员列表 [<span class="tip-red">10</span> (<span class="tip-red">1</span>,1) / 800]
                            </div>
                        </div>
                        <div id="ksOrgTree" class="col-sm-12">
                            <div id="ksOrgTreeContent" class="_scrollbar content-navTree"
                                 style="height: 255px;border: none;margin:0"></div>
                        </div>
                    </div>
                </div>

                <div class="col-sm-9 "
                     style="padding-left:3px;padding-right:0;">
                    <div id="ksContent" class="_scrollbar" style="overflow: auto;height:215px;">
                        <table id="ksTable" class="table table-hover table-bordered"
                               border="1"><%-- dshtablea JPadding--%>
                            <thead id="tem" class="">
                            <tr style="background-color: #e3e6ea">
                                <th><input type="checkbox"/></th>
                                <th>序号</th>
                                <th>一级机构</th>
                                <th>二级机构</th>
                                <th>三级机构</th>
                                <th>四级机构</th>
                                <th>姓名</th>
                                <th>考试名称</th>
                                <th>级别</th>
                                <th>报考数</th>
                                <th>状态</th>
                                <th>人力资源编码</th>
                            </tr>
                            </thead>
                            <tbody id="rhGrid-tbody">
                            </tbody>
                        </table>
                    </div>

                    <div id="ksTablePage" style="display: none;">
                        <%--每页显示条数--%>
                        <select id="showNumSelect"
                                style="padding:3px;height: 29px;border:#8db5d7 1px solid;border-radius: 3px;margin: 20px 10px 20px 0;float: right;"
                                title="">
                            <option value="10">10 条/页</option>
                            <option value="20">20 条/页</option>
                            <option value="30">30 条/页</option>
                            <option value="40">40 条/页</option>
                            <option value="50">50 条/页</option>
                        </select>

                        <%--page--%>
                        <div class="rhGrid-page">
                            <span class="disabled ui-corner-4">上一页</span>
                            <span class="current ui-corner-4">1</span>
                            <span class="disabled ui-corner-4">下一页</span>
                            <span class="allNum">共15条</span>
                        </div>
                    </div>
                </div>
            </div>
            <%--<div class="row" id="dragDiv">
                <div class="col-sm-12" id="rDown"
                     style="height: 2px;background-color: black; cursor: n-resize;z-index: 999;">
                </div>
            </div>--%>
            <div class="row">
                <div id="kcTip" class="col-sm-12"
                     style="background-color: #ecedef; padding: 5px 39px 5px 5px;border-top: 1px solid #bfdfee;border-bottom: 1px solid #bfdfee;margin: 0 15px;">

                </div>
                <div class="col-sm-12">
                    <div style="margin-top: 3px;">
                        <table id="kcInfo" class="table table-hover table-bordered">
                            <thead>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>

<%--更改场次 updateccModal--%>
<div class="modal" id="updateccModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h5 class="modal-title">
                    更改场次
                </h5>
            </div>
            <div class="modal-body" style="padding: 24px;">
                <div class="row">
                    <div class="col-sm-6 _scrollbar" style="height: 350px;overflow: auto;">
                        <div id="updateCCTree1"></div>
                    </div>
                    <div class="col-sm-6 _scrollbar" style="height: 350px;overflow: auto;">
                        <div id="updateCCTree2"></div>
                    </div>
                </div>
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" onclick="UpdateCCModal.ensure();"
                        style="width:100px;background-color: #00c2c2;">
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

<%--查看借考人员  lookJkModal--%>
<div class="modal" id="submissionArrangementModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h5 class="modal-title">
                    各单位场次安排情况
                </h5>
            </div>
            <div class="modal-body" style="padding: 24px;">
                <div class="row">
                    <div class="col-sm-12">
                        机构数：<span id="totalCount" style="color: red">23</span>
                        已提交：<span id="hasCount" style="color: red">20</span>
                        未提交：<span id="noCount" style="color: red">3</span>
                    </div>
                    <div class="col-sm-12">
                        <table id="submissionArrangement-table" class="table table-border">
                            <thead>
                            <tr style="backGround-color:WhiteSmoke; height: 30px;font-weight: bold">
                                <th>序号</th>
                                <th>机构</th>
                                <th>状态</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>10</td>
                                <td>六安市分行</td>
                                <td>已提交</td>
                            </tr>
                            <tr>
                                <td>11</td>
                                <td>六安市分行</td>
                                <td>已提交</td>
                            </tr>
                            <tr>
                                <td>12</td>
                                <td>六安市分行</td>
                                <td>已提交</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" onclick=""
                        data-dismiss="modal" style="width:100px;background-color: #00c2c2;">
                    确定
                </button>
                <%--<button type="button" class="btn btn-default" onclick=""
                        data-dismiss="modal" style="width:100px;background-color: #fff;">
                    取消
                </button>--%>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<%--selectXLModal--%>
<div class="modal" id="selectXLModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true" style="position: relative;left: 100px;">
    <div class="modal-dialog" style="width: 450px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h5 class="modal-title">
                    选择序列
                </h5>
            </div>
            <div class="modal-body" style="padding: 24px;">
                <div class="row">
                    <div class="col-sm-12 _scrollbar" style="height: 300px;overflow-y: scroll;">
                        <table id="selectXL-table" class="table table-border">
                            <thead>
                            <tr style="backGround-color:WhiteSmoke; height: 30px;font-weight: bold">
                                <th></th>
                                <th>序号</th>
                                <th>专业类</th>
                                <th>序列</th>
                            </tr>
                            </thead>
                            <tbody>
                            <%--<tr>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                            </tr>--%>

                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" onclick=""
                        data-dismiss="modal" style="width:100px;background-color: #00c2c2;">
                    确定
                </button>
                <%--<button type="button" class="btn btn-default" onclick=""
                        data-dismiss="modal" style="width:100px;background-color: #fff;">
                    取消
                </button>--%>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<%--selectXLModal--%>
<div class="modal" id="selectOrgModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true" style="position: relative;left: 100px;">
    <div class="modal-dialog" style="width: 450px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h5 class="modal-title">
                    选择机构
                </h5>
            </div>
            <div class="modal-body" style="padding: 24px;">
                <div class="row">
                    <div class="col-sm-6">
                        <input id="select-org-tree-search-name" type="text" title=""
                               class="form-control">
                        <%-- <button id="select-org-tree-search" class="btn btn-default" value="查询"
                                 style="margin:5px;float:left;background:linear-gradient(180deg,rgb(123,202,249)0%,rgb(136,218,236)2%,rgba(148,221,245,1)3%,rgb(75,189,239)93%,rgb(91,200,218)96%,rgb(155,214,243)100%);background-color:#5bc8da;color:#fff;padding:2px 11px;">
                             查询
                         </button>--%>
                        <%-- <div class="rhSearch-button">
                             <div class="rhSearch-inner">查询</div>
                         </div>--%>
                    </div>
                    <div class="col-sm-6">
                        <select id="org-direction" title="" class="form-control" style="float: right;max-width: 82px;">
                            <option value="forward">靠前</option>
                            <option value="back">靠后</option>
                        </select>
                    </div>
                    <div class="col-sm-12 ">
                        <div id="selectOrg-tree" style="height: 300px;overflow-y: scroll;margin-top: 9px;"
                             class="_scrollbar">
                            <div class="content-navTree" style="border: none;"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" onclick=""
                        data-dismiss="modal" style="width:100px;background-color: #00c2c2;">
                    确定
                </button>
                <%--<button type="button" class="btn btn-default" onclick=""
                        data-dismiss="modal" style="width:100px;background-color: #fff;">
                    取消
                </button>--%>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<%--查看借考人员  lookJkModal--%>
<div class="modal" id="lookJkModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h5 class="modal-title">
                    查看借考人员
                </h5>
            </div>
            <div class="modal-body" style="padding: 24px;">
                <div class="row">
                    <div class="col-sm-12">

                    </div>
                </div>
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" onclick=""
                        data-dismiss="modal" style="width:100px;background-color: #00c2c2;">
                    确定
                </button>
                <%--<button type="button" class="btn btn-default" onclick=""
                        data-dismiss="modal" style="width:100px;background-color: #fff;">
                    取消
                </button>--%>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<%--自动分配场次 模态框--%>
<div class="modal" id="zdfpccModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h5 class="modal-title">
                    自动分配场次
                </h5>
            </div>
            <div class="modal-body" style="padding: 24px;">
                <div class="row text-center">
                    <div class="col-sm-12" style="font-size: 25px;color:red;margin-bottom: 12px;">
                        考生场次自动分配
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-3">
                        考生分布方式：
                    </div>
                    <div class="col-sm-8">
                        <label class="radio-inline">
                            <input name="least" type="radio" value="leastKc" checked>最少考场
                        </label>
                        <label class="radio-inline">
                            <input name="least" value="leastCc" type="radio">最少场次
                        </label>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-12">
                        <hr style="color:#c9cbd0;"/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-3">
                        分配规则：
                    </div>
                    <div class="col-sm-8" id="allocation-rule">
                    </div>
                </div>
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" onclick="console.log(ZdfpccModal.doArrangeSeat())"
                        data-dismiss="modal" style="width:100px;background-color: #00c2c2;">
                    确定
                </button>
                <%--<button type="button" class="btn btn-default" onclick="ZdfpccModal.doArrangeSeat()"
                        data-dismiss="modal" style="width:100px;background-color: #fff;">
                    取消
                </button>--%>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<%--辖内公示--%>
<div class="modal" id="xngsModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h5 class="modal-title">
                    辖内公示确定
                </h5>
            </div>
            <div class="modal-body" style="padding: 24px;">
                是否辖内公示座位信息？
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" onclick=""
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

<%--提交场次安排--%>
<div class="modal" id="tjccapModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h5 class="modal-title">
                    提交场次安排确认
                </h5>
            </div>
            <div class="modal-body" style="padding: 24px;">
                是否提交场次安排？
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" onclick=""
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

<%--发布场次安排--%>
<div class="modal" id="publishModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h5 class="modal-title">
                    发布场次安排确认
                </h5>
            </div>
            <div class="modal-body" style="padding: 24px;">
                是否发布场次安排？
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" onclick=""
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

<%--发布场次安排--%>
<div class="modal" id="unPublishModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h5 class="modal-title">
                    取消发布场次安排确认
                </h5>
            </div>
            <div class="modal-body" style="padding: 24px;">
                是否取消发布场次安排？
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" onclick=""
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


<script src="<%=CONTEXT_PATH%>/ts/js/kwap.js"></script>

<script type="text/javascript">
    var xmId = '<%=xmId%>';
    $(function () {
        initData(xmId);
    });
</script>

</body>
</html>
