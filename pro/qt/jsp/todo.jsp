<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%
    final String CONTEXT_PATH = request.getContextPath();
    String batch = request.getParameter("batch");
%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>待办提醒</title>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
          name="viewport">
    <!-- 获取后台数据 -->
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
    <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/bootstrap/js/site.css">
    <!--工具方法-->
    <script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/global.js"></script>
    <!--插件-->
    <script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/jquery.smart-form.js"></script>

    <script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
    <!-- Bootstrap 3.3.6 -->
    <script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>

    <style>
        /*tab标签页样式*/
        #myTabs .nav-name {
            font-size: 25px;
            color: black;
        }

        #myTabs .active .nav-name {
            color: lightseagreen;
        }

        #myTabs .active div {
            border: none;
            border-bottom: 5px solid LightSeaGreen;
        }

        #myTabs .active .todo-nav-tab-item {
            border: none;
            border-bottom: 5px solid LightSeaGreen;
        }

        #myTabs .todo-nav-tab-item {
            width: 200px;
            text-align: center;
            cursor: pointer;
        }

        /*表格样式*/
        #todo-table > tbody > tr:nth-of-type(even), #done-table > tbody > tr:nth-of-type(even) {
            background-color: Azure;
        }
    </style>

</head>

<body class="hold-transition skin-black sidebar-mini">

<%--页首--%>
<div style="position: absolute; left: 15px; z-index: 10000;">
    <img alt="中国工商银行" src="<%=CONTEXT_PATH%>/qt/img/u3148.png">
    <img alt="考试系统" src="<%=CONTEXT_PATH%>/qt/img/u3376.png">
</div>
<header class="main-header" style="background-color: white;">
    <nav class="navbar navbar-static-top">
    </nav>
</header>

<%--导航栏--%>
<div class="" style="padding: 10px">
    <a href="<%=CONTEXT_PATH%>/index_qt.jsp">
        <img style="padding-bottom: 10px ;color: #388CAE;" src="/ts/image/Home_16x16.png" id="shouye">
    </a>
    <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;待办提醒</span>
</div>

<ul id="myTabs" class="nav nav-tabs" role="tablist" style="margin: 0 10px">
    <li role="presentation" class="active" style="width:50%;padding-left: 14%;">
        <div href="#apply" id="home-tab" role="tab" data-toggle="tab"
             aria-controls="home" aria-expanded="true"
             class="todo-nav-tab-item">
            <img style="margin-bottom:10px" src="/ts/image/u975.png" id="keshenimage"/><%--u1131.png u975.png--%>
            <span id="keshen" class="nav-name">未处理待办</span>
        </div>
    </li>
    <li role="presentation" class="" style="width:50%;padding-left: 14%;">
        <div href="#applied" role="tab" id="profile-tab" data-toggle="tab"
             aria-controls="profile" aria-expanded="false"
             class="todo-nav-tab-item">
            <img style="margin-bottom:10px" src="/ts/image/u984.png" id="yishenimage"/><%--u984.png u7733.png--%>
            <span id="yishen" class="nav-name">已处理待办</span>
        </div>
    </li>
</ul>

<div id="myTabContent" class="tab-content">
    <div role="tabpanel" class="tab-pane fade in active" id="apply">
        <%--<div style="margin-top: -5px; margin-left: 19%; height: 5px; width: 190px; background-color: LightSeaGreen"></div>--%>
        <div id="cuxian1"
             style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
            <span style="margin-left: 50px; padding-top: 10px">未处理待办</span>
        </div>
        <div style="display:none;width:85px;;background-color:lightseagreen;margin-left:5%;color:white;margin-top:10px"
             id="batchsh" class="btn">
            批量审核
        </div>
        <div style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
        <div id="table1" class="" style="margin-left: 10px; width: 98%">
            <div class="content-main1">
                <table class="rhGrid  JColResizer" id="todo-table"><%--JPadding--%>
                    <thead class="">
                    <tr style="backGround-color:WhiteSmoke; height: 30px">
                        <th id="todo-th-checkbox" style="display: none;text-align: center;">
                            <input type="checkbox" title=""/>
                        </th>
                        <th class="" style="width: 10%; text-align: center">序号</th>
                        <th class="" style="width: 20%;">名称</th>
                        <th class="" style="width: 10%;">类型</th>
                        <th class="" style="width: 10%;">送交人</th>
                        <th class="" style="width: 20%;">送交人所在单位</th>
                        <th class="" style="width: 20%;">送交时间</th>
                    </tr>
                    </thead>
                    <tbody class="grid-tbody">

                    </tbody>
                </table>

                <%--每页显示条数--%>
                <select class="showNumSelect"
                        style="padding:3px;height: 29px;border:#8db5d7 1px solid;border-radius: 3px;margin: 20px 10px 20px 0;float: right;"
                        title="">
                    <option value="10">10 条/页</option>
                    <option value="20">20 条/页</option>
                    <option value="30">30 条/页</option>
                    <option value="40">40 条/页</option>
                    <option value="50">50 条/页</option>
                </select>

                <%--分页--%>
                <div class='rhGrid-page'></div>
            </div>

        </div>
    </div>
    <div role="tabpanel" class="tab-pane fade " id="applied">
        <%--<div style="margin-top: -6px; margin-left: 68%; height: 5px; width: 190px; background-color: LightSeaGreen"></div>--%>
        <div id="cuxian2"
             style="margin-left: 10px; margin-top: 20px; background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
            <span style="margin-left: 50px; padding-top: 10px">已处理待办</span>
        </div>

        <div style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
        <div id="qihuan" style="">
            <div id="table2" class="" style="margin-left: 10px; width: 98%;">
                <div class="content-main2" style="position:relative;">
                    <table id="done-table" class="rhGrid  JColResizer"><%--JPadding--%>
                        <thead id="tem" class="">
                        <tr style="backGround-color:WhiteSmoke; height: 30px">
                            <th class="" style="width: 10%; text-align: center">序号</th>
                            <th class="" style="width: 20%;">名称</th>
                            <th class="" style="width: 10%;">类型</th>
                            <th class="" style="width: 10%;">送交人</th>
                            <th class="" style="width: 20%;">送交人所在单位</th>
                            <th class="" style="width: 20%;">送交时间</th>
                            <th class="" style="width: 10%;">处理结果</th>
                        </tr>
                        </thead>
                        <tbody class="grid-tbody">
                        </tbody>

                    </table>

                    <%--每页显示条数--%>
                    <select class="showNumSelect"
                            style="padding:3px;height: 29px;border:#8db5d7 1px solid;border-radius: 3px;margin: 20px 10px 20px 0;float: right;"
                            title="">
                        <option value="10">10 条/页</option>
                        <option value="20">20 条/页</option>
                        <option value="30">30 条/页</option>
                        <option value="40">40 条/页</option>
                        <option value="50">50 条/页</option>
                    </select>

                    <%--分页--%>
                    <div class='rhGrid-page'></div>

                </div>
            </div>

        </div>
    </div>
</div>

<div class="modal fade" id="tiJiao" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static"
     aria-hidden="true" style="padding-top:5%">
    <div class="modal-dialog" style="width:50%">
        <div class="modal-content">
            <div class="modal-header"
                 style="line-height:20px;font-size:16px;height:50px;background-color: #00c2c2;color: white">
                批量审核
            </div>
            <form id="formmotai" method="post" action="bmshzg.jsp">
                <div>
                    <table style="height:125px;font-size:14px;">
                        <tr style="height:25%">
                            <td style="text-align:right;width:20%">审核人姓名</td>
                            <td style="width:5%"></td>
                            <td>
                                <input id="userName" style="height:30px" type="text" name="shren" readonly/>
                            </td>
                            <td style="width:3%"></td>
                            <td style="text-align:right">审核人登录名</td>
                            <td style="width:5%"></td>
                            <td>
                                <input id="loginName" style="height:30px" type="text" name="shdlming" readonly/>
                            </td>
                        </tr>
                        <tr style="height:25%">
                            <td style="text-align:right">审核状态</td>
                            <td style="width:5%"></td>
                            <td><span id="radiospan1"><input style="vertical-align:text-bottom; margin-bottom:-3px;"
                                                             name="state" type="radio" value="1" checked>审核通过&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
                                <span id="radiospan2">
                                    <input name="state" style="vertical-align:text-bottom; margin-bottom:-4px;"
                                           type="radio" value="2">
                                    审核不通过
                                </span>
                            </td>
                        </tr>
                    </table>
                    <table style="height:100px;width:700px">
                        <tr>
                            <td style="text-align:right;width:17.5%;vertical-align:top">审核理由</td>
                            <td style="width:4%"></td>
                            <td style="width:75%;vertical-align:top"><textarea id="liyou"
                                                                               style="border:solid 1px lightseagreen;height:90%;width:88%"
                                                                               wrap="soft"></textarea></td>
                        </tr>
                    </table>
                </div>
                <input type="hidden" id="mokuai"/>
            </form>
            <div class="modal-footer" style="text-align:center;height:60px">
                <button type="button" class="btn btn-primary" style="height:35px;background:lightseagreen;width:80px"
                        onclick="mttijiao()">审核
                </button>
                <button type="button" class="btn btn-default"
                        style="background:lightseagreen;margin-left:100px;color:white;height:35px;width:80px"
                        data-dismiss="modal">关闭
                </button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>


<script>

    $(function () {
        var batch = '<%=batch%>';
        if (batch === '1' || batch === '2') {
            $('#todo-th-checkbox').css('display', 'block');
            $('#batchsh').css('display', 'block');
        }

        $('#batchsh').unbind('click').bind('click', function () {
            $('#tiJiao').modal({backdrop: false, show: true});
        });

        var userName = System.getUser('USER_NAME');
        var loginName = System.getUser('LOGIN_NAME');
        $('#userName').val(userName);
        $('#loginName').val(loginName);


        //tab标签页切换改变图标
        function changeImg($img) {
            var str = "/ts/image/";

            function changeImgAttr(imgName) {
                $img.attr('src', str + imgName);
            }

            var src = $img.attr('src');
            if (src.indexOf("u1131.png") >= 0) {
                changeImgAttr("u975.png");
            } else if (src.indexOf("u975.png") >= 0) {
                changeImgAttr("u1131.png");
            } else if (src.indexOf("u984.png") >= 0) {
                changeImgAttr("u7733.png");
            } else if (src.indexOf("u7733.png") >= 0) {
                changeImgAttr("u984.png");
            }
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

        //变更标签页时，更改图标
        $('div[data-toggle="tab"]').on('shown.bs.tab', function (e) {
            var img1 = jQuery(e.target).find('img');
            changeImg(img1);
            if (jQuery(e.target).attr('href').indexOf("applied") > 0) {
                changeImg(jQuery('div[href="#apply"]').find('img'));
            } else {
                changeImg(jQuery('div[href="#applied"]').find('img'));
            }
//            var img2 = jQuery(e.relatedTarget).find('img');
            <%--u1131.png u975.png--%>
            <%--u984.png u7733.png--%>
            // newly activated tab
            // previous active tab
        });


        var typeNameMap = {
            '0': '请假',
            '1': '借考',
            '2': '异议'
        };
        //
        var ListPage = function (preHandle) {
            this.preHandle = preHandle;
            if (this.preHandle) {
                this.showNumSelect = this.preHandle.find('.showNumSelect');
            } else {
                this.showNumSelect = jQuery('.showNumSelect');
            }

            // 构建页码所需参数
            this.showPageNum = 5; // 最多显示的页码
            this.startNum = 1; // 中间页码的第一个页码
            this.endNum = this.startNum; // 中间页码的最后一个页码
        };
        /*获取搜索条件 where语句*/
        ListPage.prototype.getExtWhere = function () {
            var extWhere = "and OWNER_CODE='" + System.getUser("USER_CODE") + "'";
            if (batch === '1') {
                extWhere += " and TYPE ='0'";
            } else if (batch === '2') {
                extWhere += " and TYPE ='1'";
            }
//            for (var i = 0; i < icodes.length; i++) {
//                var icode = icodes[i];
//                var select = jQuery('select[icode="' + icode + '"]');
//                var option = select.find("option:selected").val();
//                if (option) {
//                    extWhere += " and " + icode + "= '" + option + "'";
//                }
//            }
            return extWhere;
        };
        /*根据条件获取数据*/
        ListPage.prototype.getListData = function (num) {
            var showNum = parseInt(this.showNumSelect.find("option:selected").val());
            var data = {};
            data["_PAGE_"] = {"NOWPAGE": num, "SHOWNUM": showNum};
            data["_extWhere"] = this.getExtWhere();
            return FireFly.doAct("TS_COMM_TODO", 'query', data, false);
//            return FireFly.getListData("TS_COMM_TODO", data, false);
        };
        /*根据listdata构建表格*/
        ListPage.prototype.bldTable = function (listData) {
            var rhGridTBody;
            if (this.preHandle) {
                rhGridTBody = this.preHandle.find(".grid-tbody");
            } else {
                rhGridTBody = jQuery(".grid-tbody");
            }

            rhGridTBody.html('');
            for (var i = 0; i < listData._DATA_.length; i++) {
                var item = listData._DATA_[i];
                var tr = jQuery('<tr style="height:50px;"></tr>');
                if (batch === '1' || batch === '2') {
                    tr.append('<td style="text-align: center;"><input id="' + item.TODO_ID + '"  type="checkbox"></td>');
                }
                tr.append('<td style="text-align: center;">' + (i + 1) + '</td>');

                var td = jQuery('<td></td>');
                var a = jQuery('<a id="' + item.TODO_ID + '" data-id="' + item.DATA_ID + '"  style="cursor: pointer">' + item.TITLE + '</a>');

                if (item.TYPE === '0') {
                    a.unbind('click').bind('click', function () {
                        var todoId = $(this).attr('id');
                        var dataId = $(this).attr('data-id');
                        doPost("/ts/jsp/qjlb_qj2.jsp", {todoId: todoId, qjid: dataId, hidden: '2'});
                    });
                } else if (item.TYPE === '2') {
                    a.unbind('click').bind('click', function () {
                        var todoId = $(this).attr('id');
                        var dataId = $(this).attr('data-id');
                        doPost("/ts/jsp/jklb_jk2.jsp", {todoId: todoId, jkid: dataId, hidden: '2'});
                    });
                }
                td.append(a);
                tr.append(td);

                tr.append('<td>' + typeNameMap[item.TYPE] + '</td>');
                tr.append('<td>' + item.SEND_NAME + '</td>');
                tr.append('<td>' + item.SEND_DEPT_NAME + '</td>');
                tr.append('<td>' + ((item.SEND_TIME && item.SEND_TIME.length >= 16) ? item.SEND_TIME.substring(0, 16) : '') + '</td>');
                rhGridTBody.append(tr);
            }
            Utils.addTableCheckboxChangeEvent("todo-table");
        };
        /*构建主体内容（表格和分页）*/
        ListPage.prototype.bldBody = function (num) {
            if (!num) {
                num = 0;
            }
            var listData = this.getListData(num);
            this._lPage = listData._PAGE_;
            this._lData = listData._DATA_;
            this.bldTable(listData);
            this.bldPage(/*listData._PAGE_*/);
        };
        /*查询更新*/
        ListPage.prototype.search = function () {
            this.bldBody();
        };
        /*跳转到指定页*/
        ListPage.prototype.gotoPage = function (num) {
            this.bldBody(num);
        };
        /*上一页*/
        ListPage.prototype.prePage = function () {
            var prePage = parseInt(this._lPage.NOWPAGE) - 1;
            var nowPage = "" + ((prePage > 0) ? prePage : 1);
            this.gotoPage(nowPage);
        };
        /*下一页*/
        ListPage.prototype.nextPage = function () {
            var nextPage = parseInt(this._lPage.NOWPAGE) + 1;
            var pages = parseInt(this._lPage.PAGES);
            var nowPage = "" + ((nextPage > pages) ? pages : nextPage);
            this.gotoPage(nowPage);
        };
        /*首页*/
        ListPage.prototype.firstPage = function () {
            this.gotoPage(1);
        };
        /*末页*/
        ListPage.prototype.lastPage = function () {
            this.gotoPage(this._lPage.PAGES);
        };
        /*构建分页*/
        ListPage.prototype.bldPage = function () {
            this._buildPageFlag = true;
            var _self = this;
//            this._page = jQuery("<div class='rhGrid-page'></div>");
            if (this.preHandle) {
                this._page = this.preHandle.find(".rhGrid-page");
            } else {
                this._page = jQuery(".rhGrid-page");
            }
            this._page.html('');
            //判断是否构建分页
            if (this._buildPageFlag === "false" || this._buildPageFlag === false) {
                this._page.addClass("rhGrid-page-none");
            } else if (this._lPage.PAGES === null) {//没有总条数的情况
                if (this._lPage.NOWPAGE > 1) {//上一页 {"ALLNUM":"1","SHOWNUM":"1000","NOWPAGE":"1","PAGES":"1"}
//			this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function(){
                    this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'><</a>").click(function () {
                        _self.prePage();
                    }));
                } else {
//			this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
                    this._page.append("<span class='disabled ui-corner-4'><</span>");
                }
                this._page.append("<span class='current ui-corner-4'>" + this._lPage.NOWPAGE + "</span>");	//当前页
                if (this._lData.length === this._lPage.SHOWNUM) {//下一页
//			this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
                    this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>></a>").click(function () {
                        _self.nextPage();
                    }));
                } else {
//			this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
                    this._page.append("<span class='disabled ui-corner-4'>></span>");
                }
            } else if (!jQuery.isEmptyObject(this._lPage)) {
                // 当前页码
                var currentPageNum = parseInt(this._lPage.NOWPAGE);
                // 总页数
                var sumPage = parseInt(this._lPage.PAGES);

                if (this.startNum + this.showPageNum < sumPage) {
                    this.endNum = this.startNum + this.showPageNum
                } else {
                    this.endNum = sumPage;
                }

                // 总条数
                var allNum = parseInt(this._lPage.ALLNUM);
                // 显示上一页
                if (currentPageNum !== 1) {
//			this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function(){
                    this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'><</a>").click(function () {
                        _self.prePage();
                    }));
                } else {
//			this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
                    this._page.append("<span class='disabled ui-corner-4'><</span>");
                }
                // 移动页码
                if (currentPageNum > this.startNum + Math.floor((this.endNum - this.startNum) / 2)) {// 如果点击了后面的页码，则后移
                    if (currentPageNum === sumPage) {// 点击了最后一页
                        this.endNum = sumPage;

                        if (this.endNum - this.showPageNum > 0) {
                            this.startNum = this.endNum - this.showPageNum;
                        } else {
                            this.startNum = 1;
                        }
                    } else {
                        if (currentPageNum > this.showPageNum) {
                            this.endNum = currentPageNum + 1;
                            this.startNum = currentPageNum - this.showPageNum + 1;
                        }
                    }
                } else {// 否则前移
                    if (currentPageNum === 1) {// 点击了第一页
                        this.startNum = 1;
                    } else {
                        this.startNum = currentPageNum - 1;
                    }
                    if (this.startNum + this.showPageNum < sumPage) {
                        this.endNum = this.startNum + this.showPageNum;
                    } else {
                        this.endNum = sumPage;
                    }
                }
                // 显示首页
                if (this.startNum !== 1) {
                    this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>1</a>").click(function () {
                        _self.gotoPage(parseInt(jQuery(this).html()));
                    })).append("...");
                }
                // 如果总页数小于本页显示的最大页码
                if (sumPage < this.endNum) {
                    this.endNum = sumPage;
                }
                // 显示中间页码
                for (var i = this.startNum; i <= this.endNum; i++) {
                    if (i === currentPageNum) {// 构建当前页
                        this._page.append("<span class='current ui-corner-4'>" + i + "</span>");
                    } else {
                        this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>" + i + "</a>").click(function () {
                            _self.gotoPage(parseInt(jQuery(this).html()));
                        }));
                    }
                }
                // 显示尾页
                if (sumPage > this.endNum) {
                    this._page.append("...").append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>" + sumPage + "</a>").click(function () {
                        _self.lastPage();
                    }));
                }
                // 显示下一页
                if (currentPageNum !== sumPage) {
//			this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
                    this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>></a>").click(function () {
                        _self.nextPage();
                    }));
                } else {
//			this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
                    this._page.append("<span class='disabled ui-corner-4'>></span>");
                }
                // 显示跳转到指定页码
                if (sumPage > 6) {
                    this._page.append("<input class='toPageNum ui-corner-4' type='text' value=''/>").append(jQuery("<input class='toPageBtn' type='button' value='GO' />").click(function () {
                        try {
                            var val = parseInt(jQuery(this).prev().val());
                            if (val >= 1 && val <= sumPage) {
                                _self.gotoPage(val);
                            }
                        } catch (e) {
                            // 页码转换异常，忽略
                        }
                    }));
                }
                //总条数显示
//		jQuery("<span class='allNum'></span>").text("共" + allNum + "条").appendTo(this._page);
//                jQuery("<span class='allNum'></span>").text(Language.transArr("rh_ui_grid_L1", [allNum])).appendTo(this._page);
            }
            // _PAGE_ :{ALLNUM: "2", NOWPAGE: "1", PAGES: "1", SHOWNUM: "50"}
            //上一页
//            if () {
//            }
//            pageBean.NOWPAGE;
//            pageBean.PAGES;

            //变更每页显示条数时，重新获取数据
            var showNumSelect;
            if (this.preHandle) {
                showNumSelect = this.preHandle.find('.showNumSelect');
            } else {
                showNumSelect = jQuery('.showNumSelect');
            }
            showNumSelect.on('change', function () {
                _self.search();
            });

            return this._page;
        };

        //未处理待办列表
        var listPage = new ListPage(jQuery('.content-main1'));
        listPage.bldBody();

        function mttijiao() {
            var param = {};
            var radiovalue = $('#tiJiao').find('input:radio:checked').val();
            var liyou = document.getElementById("liyou").value;
            param["shstatus"] = radiovalue;
            param["shreason"] = liyou;
            if (radiovalue == 1) {
                param["isRetreat"] = "false";
            } else {
                param["isRetreat"] = "true";
            }
            var ids = "";
            var $checkedItems = $('#todo-table tbody').find('input[type="checkbox"]:checked');
            if ($checkedItems.length <= 0) {
                alert('请选择要审批的待办！');
            } else {
                $checkedItems.each(function () {
                    ids = ids + $(this).attr("id") + ',';
                });
                ids = ids.substring(0, ids.length - 1);
                param["todoId"] = ids;
                debugger;
            }
            listPage.search();
            if (batch === '1') {
                FireFly.doAct("TS_QJLB_QJ", "updateData", param);
            } else if (batch === '2') {
                FireFly.doAct("TS_JKLB_JK", "updateData", param);
            }
            $("#tiJiao").modal("hide");
        }

        //已处理待办列表
        var listPage2 = new ListPage(jQuery('.content-main2'));
        listPage2.getListData = function (num) {
            var showNumSelect;
            if (this.preHandle) {
                showNumSelect = this.preHandle.find('.showNumSelect');
            } else {
                showNumSelect = jQuery('.showNumSelect');
            }
            var showNum = parseInt(showNumSelect.find("option:selected").val());
            var data = {};
            data["_PAGE_"] = {"NOWPAGE": num, "SHOWNUM": showNum};
            data["_extWhere"] = this.getExtWhere();
            return FireFly.doAct("TS_COMM_TODO_DONE", 'getDoneList', data, false);
        };
        listPage2.bldTable = function (listData) {
            var rhGridTBody;
            if (this.preHandle) {
                rhGridTBody = this.preHandle.find(".grid-tbody");
            } else {
                rhGridTBody = jQuery(".grid-tbody");
            }

            rhGridTBody.html('');
            for (var i = 0; i < listData._DATA_.length; i++) {
                var item = listData._DATA_[i];
                var tr = jQuery('<tr></tr>');

                tr.append('<td style="text-align: center;height:50px;">' + (i + 1) + '</td>');

                var td = jQuery('<td></td>');
                var a = jQuery('<a  id="' + item.TODO_ID + '" data-id="' + item.DATA_ID + '"  style="cursor: pointer">' + item.TITLE + '</a>')
                if (item.TYPE === '0') {
                    a.unbind('click').bind('click', function () {
                        var todoId = $(this).attr('id');
                        var dataId = $(this).attr('data-id');
                        doPost("/ts/jsp/qjlb_qj2.jsp", {/*todoid: item.TODO_ID,*/ qjid: dataId});
                    });
                } else if (item.TYPE === '2') {
                    a.unbind('click').bind('click', function () {
                        var todoId = $(this).attr('id');
                        var dataId = $(this).attr('data-id');
                        doPost("/ts/jsp/jklb_jk2.jsp", {/*todoid: item.TODO_ID,*/ jkid: dataId});
                    });
                }


                td.append(a);

                tr.append(td);
                tr.append('<td>' + typeNameMap[item.TYPE] + '</td>');
                tr.append('<td>' + item.SEND_NAME + '</td>');
                tr.append('<td>' + item.SEND_DEPT_NAME + '</td>');
                tr.append('<td>' + ((item.SEND_TIME && item.SEND_TIME.length >= 16) ? item.SEND_TIME.substring(0, 16) : '') + '</td>');
                tr.append('<td>' + ( item.SH_STATUS === '2' ? '不同意' : '同意' ) + '</td>');
                rhGridTBody.append(tr);
            }
        };
        listPage2.bldBody();

    });

    var Utils = {
        /**
         * 表格添加全选/全不选功能（复选框）
         * @param tableId table id
         */
        addTableCheckboxChangeEvent: function (tableId) {
            var $table = $('#' + tableId);
            var $thCheckbox = $table.find('th input[type="checkbox"]');
            if ($thCheckbox.length >= 0) {
                //th checkbox 全选/全不选 事件
                $($thCheckbox[0]).unbind('change').bind('change', function () {
                    var $tdCheckboxs = $table.find('td input[type="checkbox"]');
                    for (var i = 0; i < $tdCheckboxs.length; i++) {
                        var tdCheckbox = $tdCheckboxs[i];
                        tdCheckbox.checked = this.checked;
                    }
                });
                //td checkbox td中checkbox变更，改变th checkbox
                var tdCheckboxs = $table.find('td input[type="checkbox"]');
                tdCheckboxs.unbind('change').bind('change', function () {
                    if ($thCheckbox[0].checked && !this.checked) {
                        $thCheckbox[0].checked = false;
                    } else {
                        var allChecked = true;
                        for (var i = 0; i < tdCheckboxs.length; i++) {
                            var tdCheckbox = tdCheckboxs[i];
                            if (!tdCheckbox.checked) {
                                allChecked = false;
                            }
                        }
                        $thCheckbox[0].checked = allChecked;
                    }
                });
            }
        },

        getTableTbodyCheckedTrs: function (tableId) {
            var result = [];
            var $table = $('#' + tableId);
            var $trs = $table.find('tbody tr');

            for (var i = 0; i < $trs.length; i++) {
                var $tr = jQuery($trs[i]);
                var $checkBox = $tr.find('td input[type="checkbox"]');
                if ($checkBox[0].checked) {
                    result.push($tr);
                }
            }
            return result;
        }
    };

</script>


</body>