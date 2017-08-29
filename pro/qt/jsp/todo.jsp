<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%
    final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>待办提醒</title>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
          name="viewport">
    <!-- 获取后台数据 -->
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
        <image style="padding-bottom:10px;width: 15px"
               src="<%=CONTEXT_PATH%>/ts/image/u1155.png" id="shouye"/>
    </a>
    <span style="color: blue; font-size: 15px">&nbsp;&nbsp;/&nbsp;&nbsp;待办提醒</span>
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
        <div style="margin-left: 10px; background-color: white; height: 20px; width: 98%"></div>
        <div id="table1" class="" style="margin-left: 10px; width: 98%">
            <div class="content-main1">
                <table class="rhGrid  JColResizer" id="todo-table"><%--JPadding--%>
                    <thead class="">
                    <tr style="backGround-color:WhiteSmoke; height: 30px">
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

<script>
    $(function () {

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
            var extWhere = '';
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
                var tr = jQuery('<tr></tr>');

                tr.append('<td style="text-align: center;">' + (i + 1) + '</td>');

                var td = jQuery('<td></td>');
                var a = jQuery('<a id="' + item.TODO_ID + '" data-id="' + item.DATA_ID + '"  style="cursor: pointer">' + item.TITLE + '</a>').unbind('click').bind('click', function () {
                    var todoId = $(this).attr('id');
                    var dataId = $(this).attr('data-id');
                    doPost("/ts/jsp/qjlb_qj2.jsp", {todoid: todoId, qjid: dataId});
                });
                td.append(a);
                tr.append(td);

                tr.append('<td>' + typeNameMap[item.TYPE] + '</td>');
                tr.append('<td>' + item.SEND_NAME + '</td>');
                tr.append('<td>' + item.SEND_DEPT_NAME + '</td>');
                tr.append('<td>' + new Date(item.SEND_TIME).format("yyyy-mm-dd HH:MM") + '</td>');
                rhGridTBody.append(tr);
            }
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
            return FireFly.doAct("TS_COMM_TODO_DONE", 'query', data, false);
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

                tr.append('<td style="text-align: center;">' + (i + 1) + '</td>');

                var td = jQuery('<td></td>');
                var a = jQuery('<a  id="' + item.TODO_ID + '" data-id="' + item.DATA_ID + '"  style="cursor: pointer">' + item.TITLE + '</a>').unbind('click').bind('click', function () {
//                    var todoId = $(this).attr('id');
                    var dataId = $(this).attr('data-id');
                    doPost("/ts/jsp/qjlb_qj2.jsp", {/*todoid: item.TODO_ID,*/ qjid: dataId});
                });
                td.append(a);

                tr.append(td);
                tr.append('<td>' + typeNameMap[item.TYPE] + '</td>');
                tr.append('<td>' + item.SEND_NAME + '</td>');
                tr.append('<td>' + item.SEND_DEPT_NAME + '</td>');
                tr.append('<td>' + new Date(item.SEND_TIME).format("yyyy-mm-dd HH:MM") + '</td>');
                rhGridTBody.append(tr);
            }
        };
        listPage2.bldBody();

    });

</script>


</body>