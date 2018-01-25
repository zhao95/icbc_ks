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
    <title>我的准考证</title>
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
    <!--（下载文件有用到zotnClientNTKO）-->
    <script type="text/javascript" src="<%=CONTEXT_PATH%>/sy/util/office/zotnClientLib_NTKO.js"></script>

    <style>
        #admission-table > tbody > tr:nth-of-type(even) {
            background-color: Azure;
        }

        #admission-table > tbody > tr > td, #examref-table > thead > tr > th {
            padding: 5px;
        }

        #admission-table > thead > tr > th {
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

    </style>

</head>

<body class="hold-transition skin-black sidebar-mini">

<%@ include file="../../qt/jsp/header-logo.jsp" %>
<div class="" style="padding: 10px">
    <a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img  title="返回" style="padding-bottom: 10px ;color: #388CAE;"
                                                  src="<%=CONTEXT_PATH%>/ts/image/Home_16x16.png" id="shouye"></a>
    <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;我的准考证</span>
</div>

<div id="myTabContent" class="tab-content">
    <div class="tab-pane fade in active" style="position:relative;" id="home">
        <div id="cuxian1"
             style="margin-left: 10px;  background-color: LightSeaGreen; height: 45px; font-size: 20px; line-height: 45px; color: white; width: 98%">
            <span style="margin-left: 50px; padding-top: 10px">我的准考证</span>
        </div>
    </div>

    <div id="dshtable" class="" style="margin-top:20px;margin-left: 10px; width: 98%">
        <table class="rhGrid JColResizer" id="admission-table"><%-- dshtablea JPadding--%>
            <thead id="tem" class="">
            <tr style="backGround-color:WhiteSmoke; height: 30px;font-weight: bold">
                <th style="width: 6.6%; text-align: center">序号</th>
                <th style="width: 20%;">考试名称</th>
                <th style="width: 20%;">组织单位</th>
                <th style="width: 10%;">考试时间</th>
                <th style="width: 10%;">状态</th>
                <th style="width: 150px">操作</th>
            </tr>
            </thead>

            <tbody id="rhGrid-tbody">
            </tbody>
        </table>

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
    <script>

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

        var ListPage = function () {
            // 构建页码所需参数
            this.showPageNum = 5; // 最多显示的页码
            this.startNum = 1; // 中间页码的第一个页码
            this.endNum = this.startNum; // 中间页码的最后一个页码
        };
        /*获取搜索条件 where语句*/
        ListPage.prototype.getExtWhere = function () {
            var extWhere = '';
            for (var i = 0; i < icodes.length; i++) {
                var icode = icodes[i];
                var select = jQuery('select[icode="' + icode + '"]');
                var option = select.find("option:selected").val();
                if (option) {
                    extWhere += " and " + icode + "= '" + option + "'";
                }
            }
            return extWhere;
        };
        /*根据条件获取数据*/
        ListPage.prototype.getListData = function (num) {
            var showNum = parseInt(jQuery('#showNumSelect').find("option:selected").val());
            var data = {};
            data["_PAGE_"] = {"NOWPAGE": num, "SHOWNUM": showNum};
//            data["_extWhere"] = this.getExtWhere();
            return FireFly.doAct("TS_XMGL_ADMISSION_FILE", 'getAdmissionList', {data: JSON.stringify(data)});
        };
        /*根据listdata构建表格*/
        ListPage.prototype.bldTable = function (listData) {
            var rhGridTBody = jQuery("#rhGrid-tbody");
            rhGridTBody.html('');

            var userCode = System.getUser("USER_CODE");
            for (var i = 0; i < listData._DATA_.length; i++) {
                var item = listData._DATA_[i];
                var state = item.state;
                var operDiv = jQuery('<div></div>');
                if (state === '可打印') {
//                    var lookBtn = jQuery(
//                        '<input xm_id="' + item.XM_ID + '" type="button" class="btn" style="border:none;color:white;font-size:13px;background-color:LightSeaGreen;height:30px;width:70px;" value="查看"/>'
//                    );
                    var downBtn = jQuery(
                        '<input xm_id="' + item.XM_ID + '" type="button" class="btn" style="border:none;color:white;font-size:13px;background-color:LightSeaGreen;height:30px;width:70px;margin-left:10px;" value="下载"/>'
                    );
//                    lookBtn.bind('click', function () {
////                    alert('查看');
//                        var xmId = $(this).attr("xm_id");
//                        var userCode = System.getUser("USER_CODE");
//
//                        FireFly.doAct('TS_XMGL_ADMISSION_FILE',
//                            'getAdmissionFile',
//                            {XM_ID: xmId, USER_CODE: userCode},
//                            true, false, function (response) {
////                            debugger;
//                                if (response._MSG_.indexOf('ERROR,') >= 0) {
//                                    //生成pdf出错
////                                alert(response._MSG_.substring(response._MSG_.indexOf('ERROR,'), response._MSG_.length));
//                                } else {
//                                    doPost("admission-view.jsp", {XM_ID: xmId, USER_CODE: userCode});
//                                }
//                            });
//                    });
                    downBtn.bind('click', function () {
//                    alert('下载');
                        var xmId = $(this).attr("xm_id");
                        FireFly.doAct('TS_XMGL_ADMISSION_FILE',
                            'getAdmissionFile',
                            {XM_ID: xmId, USER_CODE: userCode},
                            true, false, function (response) {
                                if (response._MSG_.indexOf('ERROR,') >= 0) {
                                    //生成pdf出错
//                                alert(response._MSG_.substring(response._MSG_.indexOf('ERROR,')+6, response._MSG_.length));
                                } else {
                                    var fileId = response.fileId;
                                    var fileName = response.fileName;
                                    rh.ui.File.prototype.downloadFile(fileId, fileName);
                                }
                            });
                    });
//                    operDiv.append(lookBtn);
                    operDiv.append(downBtn);
                }

                var tr = jQuery('<tr></tr>');
                /* tr.append(['<td style="text-align: center;">',
                 '  <input type="checkbox" name="checkbox1" value="checkboxaa" onchange="change(this)" title="">',
                 ' </td>'].join('')); */
                tr.append('<td style="text-align: center;">' + (i + 1) + '</td>');
                tr.append('<td>' + item.XM_NAME + '</td>');
                tr.append('<td>' + item.XM_FQDW_NAME + '</td>');
                tr.append('<td>' + item.XM_KSSTARTDATA + '</td>');//{考试时间}
                tr.append('<td>' + state + '</td>');
                tr.append(jQuery('<td></td>').append(operDiv));
                rhGridTBody.append(tr);
            }
        };
        /*构建主体内容（表格和分页）*/
        ListPage.prototype._bldBody = function (num) {
            var listData = this.getListData(num);
            this._lPage = listData._PAGE_;
            this._lData = listData._DATA_;
            this.bldTable(listData);
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
//		jQuery("<span class='allNum'></span>").text("共" + allNum + "条").appendTo(this._page);
//                jQuery("<span class='allNum'></span>").text(Language.transArr("rh_ui_grid_L1", [allNum])).appendTo(this._page);
            }
            // _PAGE_ :{ALLNUM: "2", NOWPAGE: "1", PAGES: "1", SHOWNUM: "50"}
            //上一页
//            if () {
//            }
//            pageBean.NOWPAGE;
//            pageBean.PAGES;
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
