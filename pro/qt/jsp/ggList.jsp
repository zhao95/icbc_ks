<%@page import="com.rh.core.serv.OutBean" %>
<%@page import="javax.swing.text.StyledEditorKit.ForegroundAction" %>
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
    <title>通知公告</title>
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
    <!--（下载文件有用到zotnClientNTKO）-->
    <script type="text/javascript" src="<%=CONTEXT_PATH%>/sy/util/office/zotnClientLib_NTKO.js"></script>
    <script type="text/javascript">
        function post(URL, PARAMS) {
            var temp = document.createElement("form");
            temp.action = URL;
            temp.method = "post";
            temp.style.display = "none";
            temp.target = "_blank";
            for (var x in PARAMS) {
                var opt = document.createElement("textarea");
                opt.name = x;
                opt.value = PARAMS[x];
                // alert(opt.name)        
                temp.appendChild(opt);
            }
            document.body.appendChild(temp);
            temp.submit();
            return temp;
        }
    </script>
    <style>
        #examref-table > tbody > tr:nth-of-type(even) {
            background-color: Azure;
        }

        #examref-table > tbody > tr > td, #examref-table > thead > tr > th {
            padding: 5px;
        }

        #examref-table > thead > tr > th {
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
    <!-- Logo -->
    <!-- <div class="logo">
        <span class="logo-lg"><b>考试系统</b></span>
    </div> -->
    <!-- Header Navbar: style can be found in header.less -->
    <nav class="navbar navbar-static-top">
        <!-- Sidebar toggle button-->
        <!-- <a href="/" class="sidebar-toggle" data-toggle="offcanvas" role="button">
<span class="sr-only">Toggle navigation</span>
</a> -->
    </nav>
</header>

<%--导航栏--%>
<div class="" style="padding: 10px">
    <a href="<%=CONTEXT_PATH%>/index_qt.jsp">
        <img style="padding-bottom: 10px ;color: #388CAE;" src="/ts/image/Home_16x16.png" id="shouye">
    </a>
    <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;通知公告</span>
</div>

<div id="myTabContent" class="tab-content">
    <div class="tab-pane fade in active" style="position:relative;" id="home">
        <div id="cuxian1"
             style="margin-left: 10px;  background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
            <span style="margin-left: 50px; padding-top: 10px">通知公告列表</span>
        </div>
    </div>
    <div style="margin-left: 10px; width: 98%;border-bottom: 1px solid #ccc;"></div>

    <div id="POSTION_TYPE_TAG" style="margin-top: 15px;color: #388CAE;margin-left: 30px;font-size: 18px;">
    </div>

    <div id="dshtable" class="" style="margin-top:20px;margin-left: 10px; width: 98%">
        <table class="rhGrid  JColResizer" id="todo-table"><%--JPadding--%>
            <thead class="">
            <tr style="backGround-color:WhiteSmoke; height: 30px">
                <th class="" style="width: 6%; text-align: center">序号</th>
                <th class="" style="width: 20%;text-align: center">名称</th>
                <th class="" style="width: 10%;text-align: center">创建时间</th>
                <th class="" style="width: 10%;text-align: center">操作</th>
            </tr>
            </thead>
            <tbody class="grid-tbody">

            </tbody>
        </table>

        <%--每页显示条数--%>
        <select id="showNumSelect"
                style="padding:3px;height: 29px;border:#8db5d7 1px solid;border-radius: 3px;margin: 20px 10px 20px 0;float: right;"
                title="">
            <option value="10">10条/页</option>
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
    <script>
        var ListPage = function () {
            // 构建页码所需参数
            this.showPageNum = 5; // 最多显示的页码
            this.startNum = 1; // 中间页码的第一个页码
            this.endNum = this.startNum; // 中间页码的最后一个页码
        };
        /*获取页码的变换*/
        ListPage.prototype.getListData = function (num) {
            var showNum = parseInt(jQuery('#showNumSelect').find("option:selected").val());
            var data = {};
            data["_PAGE_"] = {"NOWPAGE": num, "SHOWNUM": showNum};
            return FireFly.getListData("TS_GG", data, false);
        };
        /*根据listdata构建表格*/
        ListPage.prototype.bldTable = function (dataList) {
            var data = {};
            var tbodyEl = jQuery('#dshtable .rhGrid tbody');
            tbodyEl.html('');
            for (var i = 0; i < dataList._DATA_.length; i++) {
                var gg = dataList._DATA_[i];
                var trEl = jQuery([
                    '<tr id="' + gg.GG_ID + '" style="height: 40px">',
                    '<td class="col-md-4" style="width: 6%; text-align: center">' + (i + 1) + '</td>',//序号
                    '<td class="col-md-4" style="width: 20%;text-align: center">' + gg.GG_TITLE + '</td>',
                    '<td class="col-md-4" style="width: 20%;text-align: center">' + ((gg.S_ATIME&&gg.S_ATIME.length>=10) ? gg.S_ATIME.substring(0,10) : '') + '</td>',//yyyy-mm-dd HH:MM
                    ' <td style="width: 20%;text-align: center">',
                    ' <a href="javascript:post(' + "'" + FireFly.getContextPath() + '/qt/jsp/gg.jsp' + "'" + ',{id:' + "'" + gg.GG_ID + "'" + '});" style="display:inline;" class="gg-title" style="cursor:pointer;">' + '详情' + '</a>',
                    '   </td>',
                    '</tr>'
                ].join(''));
                trEl.appendTo(tbodyEl);
            }
        };

        /*构建主体内容（表格和分页）*/
        ListPage.prototype._bldBody = function (num) {
            var dataList = this.getListData(num);
            this._lPage = dataList._PAGE_;
            this._lData = dataList._DATA_;
            this.bldTable(dataList);
            this.bldPage(/*listData._PAGE_*/);
        };
        /*查询更新*/
        ListPage.prototype.search = function () {
            this.gotoPage(1);
        };
        /*跳转到指定页*/
        ListPage.prototype.gotoPage = function (num) {
            this._bldBody(num);
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
            this._page = jQuery(".rhGrid-page");
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
                jQuery("<span class='allNum'></span>").text("共" + allNum + "条").appendTo(this._page);
                //jQuery("<span class='allNum'></span>").text(Language.transArr("rh_ui_grid_L1", [allNum])).appendTo(this._page);
            }
            return this._page;
        };
        //变更每页显示条数时，重新获取数据
        jQuery('#showNumSelect').on('change', function () {
            listPage.search();
        });
        var listPage = new ListPage();
        listPage.search();
    </script>

</div>

</body>
