<%@ include file="../../sy/base/view/inHeader.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%
    final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
    <title>个人认证轨迹</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>

    <%--<%@ include file="../../sy/base/view/inHeader-icbc.jsp"%>--%>

    <!-- Bootstrap 3.3.6 -->
    <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/bootstrap/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/font-awesome-4.7.0/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/ionicons/css/ionicons.min.css">

    <script src="<%=CONTEXT_PATH%>/qt/js/html5shiv.min.js"></script>
    <script src="<%=CONTEXT_PATH%>/qt/js/respond.min.js"></script>
    <script src="<%=CONTEXT_PATH%>/qt/plugins/jQuery/jquery-2.2.3.min.js"></script>
    <!-- Bootstrap 3.3.6 -->
    <script src="<%=CONTEXT_PATH%>/qt/bootstrap/js/bootstrap.min.js"></script>

    <!-- Theme style -->
    <link rel="stylesheet"
          href="<%=CONTEXT_PATH%>/qt/dist/css/AdminLTE.min.css">
    <!-- AdminLTE Skins. Choose a skin from the css/skins
           folder instead of downloading all of them to reduce the load. -->
    <link rel="stylesheet"
          href="<%=CONTEXT_PATH%>/qt/dist/css/skins/_all-skins.min.css">
    <link rel="stylesheet" href="<%=CONTEXT_PATH%>/qt/bootstrap/js/site.css">

    <!-- 表格样式的设置 -->
    <style>

        /**/
        #nav-ul-id li {
            background-color: #fff;
            color: #808288;
        }

        #nav-ul-id li.active {
            background-color: #808080;
            color: #fff;
        }

        /*左三角*/
        .triangle-left {
            width: 0;
            height: 0;
            border-top: 16px solid transparent;
            border-bottom: 16px solid transparent;
            border-left: 32px solid #7CFC00;
        }

        #rz-table > tbody > tr:nth-of-type(even) {
            background-color: #f1faff;
        }

    </style>
</head>

<body class="skin-black sidebar-mini layout-boxed" style="height: auto;">

<div class="wrapper" style="background-color: transparent;overflow: hidden;height: auto;max-width: 1024px">
    <%--CONTEXT_PATH--%>
    <%@ include file="../../qt/jsp/header-logo.jsp" %>

    <div class="" style="padding: 10px;background-color: #dbdde1;">
        <a href="<%=CONTEXT_PATH%>/index_qt.jsp"><img style="padding-bottom: 10px ;color: #388CAE;"
                                                      src="<%=CONTEXT_PATH%>/ts/image/Home_16x16.png" id="shouye"></a>
        <span style="color: #909090;font-size: 16px;">&nbsp;&nbsp;/&nbsp;&nbsp;我的认证轨迹</span>
    </div>

    <div class="container-fluid">
        <div class="row" style="background-color: #fff">
            <div class="col-sm-12" style="box-shadow: rgb(190, 190, 190) 1px 0px 6px 0px;padding: 50px 30px 37px 50px;">
                <div class="row">
                    <div class="col-sm-4"
                         style="border-right: 4px solid #3a6ab3;min-width: 400px;padding: 0">
                        <div style="font-size: 36px;line-height: 60px; ">
                            当前序列共有 <span id="xlNum" style="color:#1D69AC;font-weight: bold;">0</span> 人
                        </div>
                    </div>
                    <div class="col-sm-4" style="font-size: 16px;max-width: 283px;">
                        <div><span id="before-you" style="color: #BA3830;">0</span> 人在您的前方, 待您追赶...</div>
                        <div><span id="equal-you" style="color: #DB6600;">0</span> 人与您并驾齐驱...</div>
                        <div><span id="after-you" style="color: #289C80;">0</span> 人在您身后紧追不舍...</div>
                    </div>
                    <div class="col-sm-4" style="max-width: 260px;padding: 0">
                        <div style="font-size: 36px;">
                            努力才能进步 !
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row" style="min-height: 650px;background-color: #f6f6f7">

            <div class="col-sm-12">

                <div class="center-block" style="width: 313px;padding-top:50px;padding-bottom: 50px;">
                    <ul id="nav-ul-id" class="btn-group nav nav-tabs" data-toggle="buttons">
                        <li class="btn btn-default active" style="padding:8px 49px;"
                            href="#view" aria-controls="profile" role="tab" data-toggle="tab">
                            图形显示
                        </li>
                        <li class="btn btn-default" style="padding:8px 49px;"
                            href="#list" aria-controls="profile" role="tab" data-toggle="tab">
                            表格显示
                        </li>
                    </ul>
                </div>
                <div class="row">
                    <div class="tab-content">
                        <div role="tabpanel" class="tab-pane active" id="view">
                            <div id="has-cert" style="display: none;">
                                <div class="row">
                                    <div class="col-sm-offset-1 col-sm-11" style="margin-left: 100px;">
                                        <label class="radio-inline">
                                            <input type="radio" name="inlineRadioOptions" id="inlineRadio1"
                                                   value="option1" onclick="RzgjObject.setValidViewData()"
                                                   checked>
                                            有效
                                        </label>
                                        <label class="radio-inline">
                                            <input type="radio" name="inlineRadioOptions" id="inlineRadio2"
                                                   onclick="RzgjObject.setAllViewData()" value="option2">
                                            全部
                                        </label>
                                    </div>
                                </div>

                                <div id="cert-content" style="padding: 200px 0 30px 0;" class="center-block">
                                    <div id="joined-bank" style="position: relative;float: left;width: 66px;">
                                        <div style="width: 66px;height: 66px;border-radius: 50%;background-color: #3a6ab3">
                                            <img class="img " src="<%=CONTEXT_PATH%>/eti/images/icbc32x32.png"
                                                 style="position: relative;top: 16px;left: 16px;"><%--u5074.png--%>
                                            <div id="USER_CMPY_DATE"
                                                 style="position: absolute;top:80px;left:-26px;font-size: 20px;width: 150px;">
                                                日期
                                            </div>
                                            <div style="height: 60px;width: 2px;background-color: #707070;position: absolute;top: -60px;left: 31px;"></div>
                                            <div style="position: absolute;top:-85px;left:0;width: 85px;font-size: 16px">
                                                加入工行
                                            </div>
                                            <div style="background-color: #3a6ab3;width: 25px;height: 22px;position: absolute;top: -60px;left: 34px;"></div>
                                        </div>
                                    </div>

                                    <div id="add-cert-content" style="float: left;"></div>
                                </div>

                            </div>

                            <div id="no-cert" style="display: none"
                                 class="center-block">
                                <div class="col-sm-12">
                                    <div class="row">
                                        <div class="col-sm-12"
                                             style="font-size: 28px;color: #388CAE;margin-left: 120px;padding: 19px;">
                                            <img id="u5189_img" class="img "
                                                 src="<%=CONTEXT_PATH%>/eti/images/u5189.png">
                                            <span style="color: red;position: relative;top: 3px;left: -33px;">!</span>
                                            您还没有获得该序列有效相关资格证书
                                        </div>
                                        <div class="col-sm-12"
                                             style="font-size: 20px;margin-left: 220px;padding: 19px;">
                                              <span style="color: #289C80">
                                                  您的岗位序列：
                                              </span>
                                            <span id="xlName"></span>
                                            序列
                                        </div>
                                        <div class="col-sm-12"
                                             style="font-size: 20px;margin-left: 220px;padding: 19px;">
                                            <span style="color: #289C80">
                                                您要获取的资格：
                                            </span>
                                            <span id="zgName"></span>序列
                                            （
                                            <a onclick="toExamRef()" href="#">相关学习资料下载</a> >工银大学）
                                        </div>
                                    </div>
                                </div>

                            </div>
                        </div>

                        <div role="tabpanel" class="tab-pane" id="list">

                            <table id="rz-table" class="rhGrid JPadding JColResizer"
                                   style="margin-top: 30px;position: absolute; width: 950px; left: 30px; top: 98px; border-collapse: collapse;">
                                <thead>
                                <tr style="backGround-color: #20b2aa; height: 50px">
                                    <th style="width: 4%; text-align: center">序号</th>
                                    <th style="width: 15%; text-align: center">日期/获证日期</th>
                                    <th style="width: 6%; text-align: center">名&nbsp;&nbsp;&nbsp;&nbsp;称</th>
                                    <th style="width: 18%; text-align: center">有效期</th>
                                    <th style="width: 7%; text-align: center">当前状态</th>
                                    <th style="width: 11%; text-align: center">操作</th>
                                </tr>
                                </thead>
                                <tbody class="">
                                </tbody>
                            </table>

                        </div>
                    </div>
                </div>

            </div>

        </div>

    </div>

</div>

<script type="text/javascript">

    $(function () {
        RzgjObject.initData();
    });

    var RzgjObject = {
        info: '',
        initData: function () {
            this.info = this.getInitData();
            this.setValues();
            this.initTable();
            this.initView();
        },
        getInitData: function () {
            return FireFly.doAct("TS_ETI_CERT_QUAL", "getInfo");
        },
        setValues: function () {
            $('#xlNum').html(this.info.num);
            $('#before-you').html(this.info.pre);
            $('#equal-you').html(this.info.other);
            $('#after-you').html(this.info.after);
            $('#xlName').html(this.info.STATION_NO);
            $('#zgName').html(this.info.POSTION_QUALIFICATION_STR);
            $('#USER_CMPY_DATE').html(this.info.USER_CMPY_DATE);
        },
        initTable: function () {
            var $rzTable = $('#rz-table');
            var $rzTableTbody = $rzTable.find('tbody');
            var dataList = this.info.dataList;
            for (var i = 0; i < dataList.length; i++) {
                var data = dataList[i];
                $rzTableTbody.append([
                    '<tr class="" style="height: 50px">',
                    '   <td style="text-align: center">' + (i + 1),
                    '   </td>',
                    '   <td style="text-align: center">',
                    '   ' + data.ISSUE_DATE_STR,
                    '   </td>',
                    '   <td style="text-align: center">' + data.FNAME_CHN + '</td>',//CERT_GRADE_CODE
                    '   <td style="text-align: center">' + data.date + '</td>',
                    '   <td style="text-align: center">' + data.state + '</td>',
                    '   <td style="text-align: center">' +
                    '       <a href="<%=CONTEXT_PATH%>/eti/jsp/zhengshu.jsp">',
                    '            <img src="<%=CONTEXT_PATH%>/eti/images/chankan.png">',
                    '       </a>',
                    '    </td>',
                    '</tr>'
                ].join(''));

            }

            $rzTableTbody.append([
                '<tr class="" style="height: 50px">',
                '   <td style="text-align: center">' + (i + 1),
                '   </td>',
                '   <td style="text-align: center">',
                '   ' + this.info.USER_CMPY_DATE,
                '   </td>',
                '   <td style="text-align: center">加入工行</td>',
                '   <td style="text-align: center"></td>',
                '   <td style="text-align: center"></td>',
                '   <td style="text-align: center"></td>',
                '</tr>'
            ].join(''));
        },
        initView: function () {
            this.setValidViewData();
        },
        setValidViewData: function () {
            var result = [];
            var dataList = this.info.currentDataList;//this.info.dataList;//this.info.currentDataList;
            for (var i = 0; i < dataList.length; i++) {
                var data = dataList[i];
                if (data.state !== '无效') {
                    result.add(data);
                }
            }
            this._setViewContent(result, this.info.currentDataList.length > 0);
        },
        setAllViewData: function () {
            var dataList = this.info.currentDataList;
            this._setViewContent(dataList, this.info.currentDataList.length > 0);
        },
        _setViewContent: function (dataList, hasCert) {
            var $noCert = $('#no-cert');
            var $hasCert = $('#has-cert');
            var $certContent = $('#cert-content');
            var $addCertContent = $('#add-cert-content');
            $addCertContent.html('');
            if (hasCert) {
                $noCert.css('display', 'none');
                $hasCert.css('display', 'block');
            } else {
                $noCert.css('display', 'block');
                $hasCert.css('display', 'none');
            }

            //u5076.png u5080.png u5084.png u5090.png
            var settings = [
                {name: '初级', imgUrl: '<%=CONTEXT_PATH%>/eti/images/primary32x32.png', color: '#529c85', height: 80},
                {
                    name: '中级',
                    imgUrl: '<%=CONTEXT_PATH%>/eti/images/intermediate32x32.png',
                    color: '#e19b25',
                    height: 110
                },
                {name: '高级', imgUrl: '<%=CONTEXT_PATH%>/eti/images/high32x32.png', color: '#a53f38', height: 150},
                {name: '专家级', imgUrl: '<%=CONTEXT_PATH%>/eti/images/expert32x32.png', color: '#67686e', height: 200},
            ];

            //1加入工行
            //2获证信息
            var count = 0;//获证数
            count = dataList.length > 4 ? 4 : dataList.length;//最多展示4个
            var lineWidths = [200, 120, 100, 100];
            var index = count - 1;
            if (index < 0) {
                index = 0;
            }
            var lineWidth = lineWidths[index];
            $certContent.css('width', (66 + count * (lineWidth + 66)) + 'px');
            for (var i = 0; i < dataList.length; i++) {
                var data = dataList[i];
                var setting = settings[i];
                if (i === count) {
                    //最多展示4个
                    return;
                }
                $addCertContent.append([
                    '<div style="position: relative;float: left;margin-left: ' + lineWidth + 'px;width: 66px;">',
                    '   <div style="background-color: #c9cbd0;width: ' + lineWidth + 'px;height:2px;position: absolute;top:33px;left: -' + lineWidth + 'px;"></div>',
                    '       <div style="width: 66px;height: 66px;border-radius: 50%;background-color: ' + setting.color + '">',
                    '       <img class="img " src="' + setting.imgUrl + '"',
                    '       style="position: relative;top: 17px;left: 17px;">',
                    '       <div style="position: absolute;top:80px;left:-10px;font-size: 20px;width: 150px;">' + data.ISSUE_DATE_STR + '</div>',
                    '       <div style="height: ' + setting.height + 'px;width: 2px;background-color: #707070;position: absolute;top: -' + setting.height + 'px;left: 31px;"></div>',
                    '       <div style="text-align:center;position: absolute;top:' + (-setting.height - 43) + 'px;left:-58px;font-size: 16px;width: 185px;">',
                    '           <div style="text-overflow: ellipsis;overflow: hidden;white-space:nowrap;">' + data.FNAME_CHN + '</div>',
                    '           <div>(' + data.state + ')</div>',
                    '       </div>',
                    '       <div class="triangle-left" style="border-left: 33px solid ' + setting.color + ';position: absolute;top: -' + setting.height + 'px;left: 34px;"></div>',
                    '   </div>',
                    '</div>'

                ].join(''));
            }

            var jbArr = ['初级', '中级', '高级', '资深专家'];
            var jb = jbArr[count];

            //下一级 next
            setting = settings[i];
            if (setting !== null) {
                $certContent.css('width', $certContent.width() + (lineWidth + 66));
                $addCertContent.append([
                    '<div style="position: relative;float: left;margin-left:  ' + lineWidth + 'px;width: 66px;">',
                    '   <div style="border-bottom: 2px dashed #999ba0;width:  ' + lineWidth + 'px;height:0;position: absolute;top:33px;left: -' + lineWidth + 'px;"></div>',
                    '       <img class="img " src="<%=CONTEXT_PATH%>/eti/images/next32x32.png"',
                    '       style="position: relative;top: 16px;left: 15px;height: 33px;">',
                    '       <img src="<%=CONTEXT_PATH%>/eti/images/u5088.png" style="position: absolute;left:0;">',
                    '       <div style="position: absolute;top:80px;left:8px;font-size: 20px;width: 150px;">NEXT</div>',
                    '       <div style="height: ' + setting.height + 'px;width: 0;border-left: 2px dashed #999ba0;position: absolute;top: -' + setting.height + 'px;left: 31px;"></div>',
                    '       <div style="position: absolute;top:' + (-setting.height) + 'px;left:38px;width:200px;">',
                    '           <div style="width: 25px;height: 22px;background-color: #94b94e;float: left"></div>',
                    '           <div style="float: left;margin-left: 2px;">' + this.info.STATION_NO + ' | ',
                    '               <a onclick="toExamRef()" href="#">了解详情 ></a>' +
                    '           </div>',
                    '       </div>',
                    '   </div>',
                    '</div>'

                ].join(''));
            }
        }
    };

    /**
     * 跳转到参考资料
     **/
    function toExamRef() {
        doPost('<%=CONTEXT_PATH%>/qt/jsp/examref.jsp', {REF_DYXL: RzgjObject.info.STATION_NO});
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
</body>
</html>
