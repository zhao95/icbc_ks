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

    <%@ include file="../../sy/base/view/inHeader.jsp" %>
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
    <!--jstree-->
    <script src="../js/dist/jstree.min.js"></script>

    <!-- Font Awesome -->
    <link rel="stylesheet"
          href="<%=CONTEXT_PATH%>/qt/font-awesome-4.7.0/css/font-awesome.min.css">

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
                <span
                        class="rh-icon-img btn-edit"></span>
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
                <%--<span onclick="KcObject.setAllKcInfo()" style="cursor:pointer;">考场场次</span>--%>
                <div id="ccJstree">
                    <!-- in this example the tree is populated from inline HTML -->
                    <!-- <ul>
                        <li>Root node 1
                            <ul>
                                <li id="child_node_1">Child node 1</li>
                                <li>Child node 2</li>
                            </ul>
                        </li>
                        <li>Root node 2</li>
                    </ul> -->
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
            <div class="col-sm-2" style="width: 10%;min-width: 82px;max-width:90px;">
                报考数<input id="search-bm-count" type="text" title="" style="width: 24px;">
            </div>
            <div class="col-sm-2" style="width: 7%;float: right">
                <button onclick="KsObject.search()" class="btn btn-default" value="查询"
                        style="background: linear-gradient(180deg, rgb(123, 202, 249) 0%, rgb(136, 218, 236) 2%, rgba(148, 221, 245, 1) 3%, rgb(75, 189, 239) 93%, rgb(91, 200, 218) 96%, rgb(155, 214, 243) 100%);
                        background-color:#5bc8da;color: #fff;padding: 2px 11px;">
                    查询
                </button>
            </div>
        </section>

        <section class="content" style="padding:0;background-color: transparent;">
            <div class="row-fluid1" style="border-top: 1px solid #bfdfee;">

                <div class="col-sm-3"
                     style="padding-left: 0;padding-right: 0;height: 100%;border-right: 1px solid #bfdfee;">
                    <div class="row">
                        <div class="col-sm-12">
                            <div style="padding: 5px;background-color: #e4e8ec;">
                                人员列表 [<span class="tip-red">10</span> (<span class="tip-red">1</span>,1) / 800]
                            </div>
                        </div>
                        <div id="ksOrgTree" class="col-sm-12">
                            <div class="_scrollbar content-navTree" style="height: 240px;border: none;margin:0"></div>
                        </div>
                    </div>
                </div>

                <div class="col-sm-9 _scrollbar"
                     style="padding-left:3px;padding-right:0;height:270px;overflow: auto;">
                    <table id="ksTable" class="table table-hover table-bordered">
                        <thead>
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
                        <tbody>

                        </tbody>
                    </table>
                </div>
            </div>
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
                            <input name="distribution" type="radio" value="indistinct" checked> 模糊分布
                        </label>
                        <label class="radio-inline">
                            <input name="distribution" value="distinct" type="radio"> 精确分布
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
                <button type="button" class="btn btn-success" onclick="console.log(ZdfpccModal.getZdfpccModalValue())"
                        data-dismiss="modal" style="width:100px;background-color: #00c2c2;">
                    确定
                </button>
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
                    辖内公示
                </h5>
            </div>
            <div class="modal-body" style="padding: 24px;">
                辖内公示
            </div>
            <div class="modal-footer" style="text-align: center;">
                <button type="button" class="btn btn-success" onclick=""
                        data-dismiss="modal" style="width:100px;background-color: #00c2c2;">
                    确定
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

<script type="text/javascript">

    var xmId = '<%=xmId%>';
    $(function () {
        bindHeaderAction();
        ZdfpccModal.setZdfpccModalContent();
        KcObject.initData();
        KsObject.initData();
    });

    /**
     * 绑定页首按钮事件
     */
    function bindHeaderAction() {
        $("#zdfpcc").click(function () {
            $('#zdfpccModal').modal('show');
        });
        $("#updatecc").click(function () {
            UpdateCCModal.show();
        });
        $("#lookJk").click(function () {
            LookJkModal.show();
        });
        $("#xngs").click(function () {
            $('#xngsModal').modal('show');
        });
        $("#tjccap").click(function () {
            $('#tjccapModal').modal('show');
        });
    }

    /**
     * 自动分配模态框相关方法
     */
    var ZdfpccModal = {

        settingArray: [
            {id: 'setting1', name: '相同考试前后左右不相邻', checked: true, disabled: true},
            {id: 'setting2', name: '同一考生同一考场场次连排'},
            {id: 'setting3', name: '距离远近规则（里面配置，场次可以后可选择）'},
            {id: 'setting4', name: '同一网点级机构考生均分安排'},
            {id: 'setting5', name: '来自同一机构考生不连排'},
            {id: 'setting6', name: '考生人数少于机器数一半时，考生左右间隔不低于2个座位，前后不低于1个'},
            {id: 'setting7', name: '特定机构考生场次靠后安排'},
            {id: 'setting8', name: '领导职务考生座位靠前安排'},
            {id: 'setting9', name: '特定考试仅限于省分行安排（考试－考生，领导不能在当地考试，省分行）'}
        ],

        /**
         * 设置考生场次自动分配内容
         */
        setZdfpccModalContent: function () {
            var $allocationRule = $('#allocation-rule');
            $allocationRule.html('');
            for (var i = 0; i < this.settingArray.length; i++) {
                var setting = this.settingArray[i];
                $allocationRule.append(
                    [
                        '<div class="checkbox">',
                        '   <label>',
                        '     <input id="' + setting.id + '" type="checkbox" '
                        + (setting.checked ? 'checked ' : ' ') + (setting.disabled ? 'disabled ' : ' ') + '>' + setting.name,
                        '   </label>',
                        '</div>'
                    ].join('')
                );
            }
        },

        getZdfpccModalValue: function () {
            var result = {
                distribution: 'indistinct'
            };
            result.distribution = $('input[name="distribution"]').filter(':checked').val();

            for (var i = 0; i < this.settingArray.length; i++) {
                var setting = this.settingArray[i];
                result[setting.id] = $('#' + setting.id)[0].checked;
            }
            return result;
        }
    };

    var LookJkModal = {
        lookJkModal: '',
        initData: function () {
            this.lookJkModal = $('#lookJkModal');

        },
        show: function () {
            if (this.lookJkModal === '') {
                this.initData();
            }
            this.lookJkModal.modal('show');
        }
    };

    var UpdateCCModal = {
        updateccModal: '',//modal
        updateCCTree1: '',//tree1
        updateCCTree2: '',//tree2

        initData: function () {
            this.updateccModal = $('#updateccModal');
            this.setUpdateCCTree1();
            this.setUpdateCCTree2();
        },

        /*确定按钮事件*/
        ensure: function () {
            var tree1Selected = UpdateCCModal.updateCCTree1.jstree().get_selected();
            var tree2Selected = UpdateCCModal.updateCCTree2.jstree().get_selected();

            if (tree1Selected.length <= 0 || tree2Selected.length <= 0) {
                alert('请选择场次');
            } else if (KcObject.getCcById(tree1Selected[0]) === null || KcObject.getCcById(tree2Selected[0]) === null) {
                alert('选中非场次，请重新选择');
            } else if (tree1Selected[0] === tree2Selected[0]) {
                alert('两边选择的为同一场次，请重新选择');
            } else {
                alert(tree1Selected[0] + tree2Selected[0]);
                this.updateccModal.modal('hide');
            }
        },

        /*显示modal*/
        show: function () {
            if (this.updateccModal === '') {
                this.initData();
            }
            this.updateccModal.modal('show');
        },

        getTreeData: function () {
            var ccTreeNodes = KcObject.getCCTreeNodes();
            if (ccTreeNodes === null || ccTreeNodes.length === 0) {
                ccTreeNodes = [];
            }
            return ccTreeNodes;
        },

        setUpdateCCTree1: function () {
            var self = this;

            var $ccJstree = $('#updateCCTree1');
            $ccJstree.html('');
            var kcTreeNodes = this.getTreeData();//jsTree构造jstree数据结构
            self.updateCCTree1 = $ccJstree.jstree({
                'core': {
                    "multiple": false,
                    'data': kcTreeNodes
                }
            });

            $ccJstree.on("changed.jstree", function (e, data) {
//                if (data.node.data.KC_ID) {
//                    //选中考场
//                    self.setKcInfo(data.node.data);
//                } else {
//                    var parentKcNode = $.jstree.reference(jstree).get_node(data.node.parent);
//                    //选中场次
//                    self.setCcInfo(data.node.data, parentKcNode.data);
//                }
            });

        },

        setUpdateCCTree2: function () {
            var self = this;

            var $ccJstree = $('#updateCCTree2');
            $ccJstree.html('');

            var kcTreeNodes = this.getTreeData();//jsTree构造jstree数据结构
            self.updateCCTree2 = $ccJstree.jstree({
                'core': {
                    "multiple": false,
                    'data': kcTreeNodes
                }
            });

            $ccJstree.on("changed.jstree", function (e, data) {
//                if (data.node.data.KC_ID) {
//                    //选中考场
//                    self.setKcInfo(data.node.data);
//                } else {
//                    var parentKcNode = $.jstree.reference(jstree).get_node(data.node.parent);
//                    //选中场次
//                    self.setCcInfo(data.node.data, parentKcNode.data);
//                }
            });
        }
    };

    /**
     * 考场场次操作集合
     */
    var KcObject = {
        rootData: '',
        rootNodes: '',
        kcTreeNodes: '',
        kcArr: [],//待安排场次
        ccArr: [],
        kctree: {},
        /*初始化界面数据*/
        initData: function () {
            this.getInitData(function () {
                this.setCCTree();
                this.setAllKcInfo();
            });//setCCTree setAllKcInfo方法在加载完数据后执行

        },
        /*从后端获取初始化数据并处理*/
        getInitData: function (callback) {
            var self = this;//self指向KcObject
            FireFly.doAct("TS_XMGL_KCAP_DAPCC", "getKcAndCc", {
                    "xmId": xmId
                }, false, false, function (data) {
                    //处理数据
                    if (data !== "") {
//                        var kcArr = [];
                        self.rootData = data.root;
//                        debugger;
                        self.getCCTreeNodes();
//                        while (rootData.CHILD) {
//
//                        }
//
//                        var kcArr = self.kcArr = data.list;
//                        var ccArr = [];
//                        for (var i = 0; i < kcArr.length; i++) {
//                            //考场
//                            var kc = kcArr[i];
//                            for (var j = 0; j < kc.ccList.length; j++) {
//                                //场次
//                                var cc = kc.ccList[j];
//                                var date = cc.SJ_START.substring(0, 10);
//                                var start = cc.SJ_START.substring(11);
//                                var end = cc.SJ_END.substring(11);
//                                var dateStr = date + "(" + start + "-" + end + ")";
//                                cc.ccTime = dateStr;
//                            }
//                        }
//                        ccArr.push(cc);
//                        self.ccArr = ccArr;
                    }
                    if (callback) {
                        callback.apply(self);
                    }
                }
            );
        },

        /**
         * 数据类型:dept kc cc
         * 获取数据对应的类型
         **/
        getDataType: function (data) {
            if (data.DEPT_ENNAME && data.DEPT_CODE) {
                //有DEPT_ENNAME、DEPT_CODE属性为dept
                return "dept";
            } else if (data.KC_NAME && data.KC_ID) {
                //有KC_NAME、KC_ID属性为kc
                return "kc";
            } else if (data.SJ_CC && data.SJ_ID) {
                return "cc";
            } else {
                return '';
            }
        },

        getCcById: function (id) {
            var result = null;
            for (var i = 0; i < this.ccArr.length; i++) {
                var cc = this.ccArr[i];
                if (cc.CC_ID === id) {
                    result = cc;
                }
            }
            return result;
        },


        getCCTreeNodes: function () {
            if (this.rootNodes) {
                return this.rootNodes;
            }
            var rootData = this.rootData;
//            debugger;
            this.recursiveTreeData(rootData);
            return this.rootNodes;
//            var child = rootData.CHILD;
//            if (child) {
//                for (var i = 0; i < child.length; i++) {
//                    var item = child[i];
//
//                }
//            }
//
//            var self = this;//self指向KcObject
//            var kcArr = self.kcArr;
//            var kcTreeNodes = [];//jsTree构造jstree数据结构
//            for (var i = 0; i < kcArr.length; i++) {
//                //考场
//                var kc = kcArr[i];
//                var children = [];
//                for (var j = 0; j < kc.ccList.length; j++) {
//                    //场次
//                    var cc = kc.ccList[j];
//                    var childNode = {id: cc.CC_ID, text: cc.ccTime, data: cc, children: []};
//                    children.push(childNode);
//                }
//                var node = {id: kc.KC_ID, text: kc.KC_NAME, data: kc, children: children};
//                kcTreeNodes.push(node);
//            }
//
//            this.kcTreeNodes = kcTreeNodes;
//            return kcTreeNodes;
        },

        recursiveTreeData: function (data, parentNode) {
            var node;
            var dataType = this.getDataType(data);
            switch (dataType) {
                case 'dept':
                    node = {id: data.DEPT_CODE, text: data.DEPT_NAME, data: data, children: []};
                    break;
                case 'kc':
                    this.kcArr.push(data);
                    node = {id: data.KC_ID, text: data.KC_NAME, data: data, children: []};
                    break;
                case 'cc':
                    var cc = data;
                    var date = cc.SJ_START.substring(0, 10);
                    var start = cc.SJ_START.substring(11);
                    var end = cc.SJ_END.substring(11);
                    var dateStr = date + "(" + start + "-" + end + ")";
                    cc.ccTime = dateStr;
                    this.ccArr.push(data);
                    node = {id: data.SJ_ID, text: data.ccTime, data: data, children: []};
                    break;
            }
            if (parentNode) {
                parentNode.children.push(node);
            } else {
                this.rootNodes = [node];
            }
            if (data.CHILD) {
                for (var i = 0; i < data.CHILD.length; i++) {
                    var item = data.CHILD[i];
                    this.recursiveTreeData(item, node);
                }
            }
        },

        /**
         * 加载考场场次数据
         **/
        setCCTree: function () {
            var self = this;

            var $ccJstree = $('#ccJstree');
            var kcTreeNodes = this.getCCTreeNodes();//jsTree构造jstree数据结构
            var jstree = $ccJstree.jstree({
                'core': {
                    'data': kcTreeNodes
                }
            });
            self.kctree = jstree;

            $ccJstree.on("changed.jstree", function (e, data) {
                var dataType = self.getDataType(data.node.data);
                if (dataType === 'kc') {
                    //选中考场
                    self.setKcInfo(data.node.data);
                } else if (dataType === 'cc') {
                    var parentKcNode = $.jstree.reference(jstree).get_node(data.node.parent);
                    //选中场次
                    self.setCcInfo(data.node.data, parentKcNode.data);
                } else if (dataType === 'dept') {

                    var parent = data.node.parent;
                    if (parent !== '#') {
                        var deptCode = data.node.data.DEPT_CODE;
                        KsObject.setInitData(deptCode);
                    } else {
                        KcObject.setAllKcInfo();
                    }
                }
            });
        },

        /**
         * 全考场信息
         */
        setAllKcInfo: function () {
            var $kcTip = $('#kcTip');
            $kcTip.html('');
            var $kcInfoThead = $('#kcInfo').find('thead');
            $kcInfoThead.html('');
            var $kcInfoTbody = $('#kcInfo').find('tbody');
            $kcInfoTbody.html('');

            //kcTip
            $kcTip.append([
                '<span style="color:#12769C;">当前考场及场次：</span>',
                '考场数:<span id="kcCount" class="tip-red">3</span>',
                '场次数数：<span id="ccCount" class="tip-red">11</span>',
                '考生数：<span id="ksCount" class="tip-red">800</span>',
                '（借入：<span id="jieruCount" class="tip-red">3</span>，',
                '借出：<span id="jiechuCount" class="tip-red">2</span>，',
                '请假：<span id="qjCount" class="tip-red">1</span>）',
                '已安排：<span id="yapCount" class="tip-red">790</span>'
            ].join(''));

            var ccTimes = [];//所有的场次时间
            var kcArr = this.kcArr;
            //获取所有的场次时间
            for (var i = 0; i < kcArr.length; i++) {
                //考场
                var kc = kcArr[i];
                var ccList = kc.CHILD;

                for (var j = 0; j < ccList.length; j++) {
                    //场次
                    var cc = ccList[j];
                    if (ccTimes.indexOf(cc.ccTime) < 0) {
                        ccTimes.push(cc.ccTime);
                    }
                }
            }

            //thead(表头)
            var $headTr = jQuery('<tr style="background-color: #e3e6ea"></tr>');
            $headTr.append('<th>考场</th>');
            for (var i = 0; i < ccTimes.length; i++) {
                var ccTime = ccTimes[i];
                $headTr.append('<th>' + ccTime + '</th>');
            }
            $kcInfoThead.append($headTr);

            //tbody
            for (var i = 0; i < kcArr.length; i++) {
                var kc = kcArr[i];
                var $bodyTr = jQuery('<tr style=""></tr>');
                $bodyTr.append('<td>' + kc.KC_NAME + '</td>');
                for (var j = 0; j < ccTimes.length; j++) {
                    var ccTime = ccTimes[j];

                    function hasCcTime(ccList, ccTime) {
                        var result = false;
                        for (var i = 0; i < ccList.length; i++) {
                            var cc = ccList[i];
                            if (cc.ccTime === ccTime) {
                                result = true;
                            }
                        }
                        return result;
                    }

                    if (hasCcTime(kc.CHILD, ccTime)) {
                        $bodyTr.append('<td>' + '0 / 0' + '</td>');
                    } else {
                        $bodyTr.append('<td></td>');
                    }
                }
                $kcInfoTbody.append($bodyTr);
            }
        },

        /**
         * 单个考场的信息
         */
        setKcInfo: function (kc) {
            var $kcTip = $('#kcTip');
            $kcTip.html('');
            var $kcInfoThead = $('#kcInfo').find('thead');
            $kcInfoThead.html('');
            var $kcInfoTbody = $('#kcInfo').find('tbody');
            $kcInfoTbody.html('');

            //kcTip
            $kcTip.append([
                '<span style="color:#12769C;">当前考场及场次：</span>' + kc.KC_NAME,
            ].join(''));

            //thead
            var $headTr = jQuery('<tr style="background-color: #e3e6ea"></tr>');
            $headTr.append('<th>考场</th>');
            for (var i = 0; i < kc.CHILD.length; i++) {
                var cc = kc.CHILD[i];
                $headTr.append('<th>' + cc.ccTime + '</th>');
            }
            $kcInfoThead.append($headTr);

            //tbody
            var $bodyTr = jQuery('<tr style=""></tr>');
            $bodyTr.append('<td>' + kc.KC_NAME + '</td>');
            for (var i = 0; i < kc.CHILD.length; i++) {
                var cc = kc.CHILD[i];
                $bodyTr.append('<td>' + '0 / 0' + '</td>');
            }
            $kcInfoTbody.append($bodyTr);
        },


        _setCcInfoType: function (type) {
            this.setCcInfo(this.currentCc, this.currentParentKc, type);
        },
        /**
         * 具体场次信息
         */
        setCcInfo: function (cc, parentKc, type) {
            this.currentCc = cc;
            this.currentParentKc = parentKc;
            if (type !== 'view' && type !== 'list') {
                type = 'view';
            }
            var $kcTip = $('#kcTip');
            $kcTip.html('');
            var $kcInfoThead = $('#kcInfo').find('thead');
            $kcInfoThead.html('');
            var $kcInfoTbody = $('#kcInfo').find('tbody');
            $kcInfoTbody.html('');

            //kcTip
            $kcTip.append([
                '<span style="color:#12769C;">当前考场及场次：</span>',
                '' + parentKc.KC_NAME,
                '&nbsp;&nbsp;' + cc.ccTime,
                '最优数：<span class="tip-red">48</span>',
                '已安排：<span style="color:green">48</span>',
                '借考：<span style="color:#00a7d0">1</span>，',
                '请假：<span class="tip-red">1</span>',
                '<div onclick="KcObject._setCcInfoType(\'list\')" style="cursor:pointer;padding: 3px;float: right"><i class="fa fa fa-list-ul"></i></div>',
                '<div onclick="KcObject._setCcInfoType(\'view\')" style="cursor:pointer;padding: 3px;float: right"><i class="fa fa-dashboard" aria-hidden="true"></i></div>',
            ].join(''));

            if (type === 'view') {
                //tbody
                var arr = [
                    '黄虹',
                    '潘美云',
                    '李江',
                    '周晓红',
                    '赵杰',
                    '丁立春',
                    '单晓梅',
                    '刘伟',
                    '王武',
                ];
                for (var i = 0; i < 41; i++) {
                    arr.push('XXX');
                }
                var tr = "<tr>";
                for (var i = 0; i < arr.length; i++) {
                    var item = arr[i];
                    tr += '<td>' + (i + 1) + item + '</td>';
                    if ((i + 1) % 9 === 0) {
                        tr += '</tr>';
                        $kcInfoTbody.append(tr);
                        tr = '<tr>';
                    }
                }
                if (tr !== '<tr>') {
                    $kcInfoTbody.append(tr);
                }

            } else if (type === 'list') {
                $kcTip.append([
                    '<div style="margin:0 10px;float: right;height: 20px;width: 1px;background-color: #fff;border-left: 1px solid #7a7c81;"></div>',
                    '<div style="cursor:pointer;padding: 3px 10px;float: right;"><i class="fa fa-arrow-up" style="color:green;"></i><span>移出</span></div>',
                    '<div style="cursor:pointer;padding: 3px 10px;float: right;"><i class="fa fa-arrow-down" style="color:green;" aria-hidden="true"></i><span>添加</span></div>',
                    '<div style="margin-right:10px;float: right;height: 20px;width: 1px;background-color: #fff;border-left: 1px solid #7a7c81;"></div>',
                ].join(''));

                $kcInfoThead.append([
                    '<tr style="background-color: #e3e6ea">',
                    '   <th><input type="checkbox"></th>',
                    '   <th>座号</th>',
                    '   <th>一级机构</th>',
                    '   <th>二级机构</th>',
                    '   <th>三级机构</th>',
                    '   <th>四级机构</th>',
                    '   <th>姓名</th>',
                    '   <th>考试名称</th>',
                    '   <th>考试级别</th>',
                    '   <th>报考数</th>',
                    '   <th>IP地址</th>',
                    '   <th>备注</th>',
                    '</tr>'
                ].join(''));

                $kcInfoThead.append([
                    '<tr>',
                    '   <td><input type="checkbox"></td>',
                    '   <td>1</td>',
                    '   <td>安徽省分行</td>',
                    '   <td>合肥市分行</td>',
                    '   <td>包河区支行</td>',
                    '   <td>宁国路网点</td>',
                    '   <td>黄虹</td>',
                    '   <td>初级营销 (公司业务)</td>',
                    '   <td>初级</td>',
                    '   <td>2</td>',
                    '   <td>10.108.11.1</td>',
                    '   <td>备注</td>',
                    '</tr>'
                ].join(''));
                //添加表头复选框变更事件
                Utils.addTableCheckboxChangeEvent('kcInfo');
            }
        }
    };

    function setCCContent1() {
        var $leftSidebarContent = $('#left-sidebar-content');
//        $leftSidebarContent.html('');
//        var param = {
//            "_linkWhere": " and XM_ID='" + xmId + "' ",
//            "_linkServQuery": "2",
//            "XM_ID": xmId
//        };
//        var data = FireFly.doAct('TS_XMGL_KCAP_DAPCC', 'query', param);

        var setting = {
            showcheck: true,
            rhexpand: false,
            expandLevel: 1,
            url: "SY_COMM_INFO.dict.do",
            theme: "bbit-tree-no-lines",
            rhItemCode: "CTLG_PCODE",
            rhLeafIcon: "",
            data: []
        };
        var tree = new rh.ui.Tree(setting);
        tree.obj.appendTo($leftSidebarContent);
    }

    var KsObject = {
        deptCode: '',//那个部门下的考生
        ksArr: [],//考生信息
        ksOrgTree: '',
        initData: function () {
//            this.getKsArr(null, function () {
//                this.setDfpKsContent();
////                this.setKsOrgContent();
//            });
            this.initSearchValue();
        },

        getKsArrByIds: function (ids) {
            //ids this.ksArr
            var result = [];
            for (var i = 0; i < this.ksArr.length; i++) {
                var ks = this.ksArr[i];
                for (var j = 0; j < ids.length; j++) {
                    var id = ids[j];
                    if (ks.KS_ID === id) {
                        result.push(ks);
                    }
                }
            }
            return result;
        },

        setInitData: function () {

        },

        getKsArr: function (params, callback) {
            var self = this;
            var param = {
                _linkWhere: " and XM_ID='" + xmId + "' ",
                _linkServQuery: "2",
                XM_ID: xmId
            };
            jQuery.extend(param, params);
            FireFly.doAct("TS_XMGL_KCAP_DAPCC", 'getKsContent', param, false, false, function (data) {
                self.ksArr = data._DATA_;
//                debugger;
                if (callback) {
                    callback.apply(self);
                }
            });
        },

        /**
         * 考生机构
         */
        setKsOrgContent: function (pdeptCode) {
            pdeptCode = pdeptCode ? pdeptCode : '';
            this.deptCode = pdeptCode;
            var $ksOrgTreeContent = $('#ksOrgTree').find('.content-navTree');
            $ksOrgTreeContent.html('');
            var data = FireFly.getDict('SY_ORG_ODEPT_ALL', pdeptCode);
            var deptName = FireFly.getDictNames(FireFly.getDict('SY_ORG_ODEPT_ALL'), pdeptCode);
            var root = {id: pdeptCode, text: deptName, data: {id: pdeptCode, text: deptName}, children: []};

            var putChildren = function (parent, childs) {
                childs = childs ? childs : [];
                for (var i = 0; i < childs.length; i++) {
                    var child = childs[i];
                    var item = {
                        id: child.ID,
                        text: child.NAME,
                        data: child,
                        children: []
                    };
                    parent.children.push(item);
                    putChildren(item, child.CHILD);
                }
            };

            var childs = data[0].CHILD;
            putChildren(root, childs);

            try {
                this.ksOrgTree.jstree('destroy');//已经初始化tree，先destroy
            } catch (e) {
            }
            this.ksOrgTree = $ksOrgTreeContent.jstree({
                'core': {
                    "multiple": false,
                    'data': [root]
                }
            });

            $ksOrgTreeContent.on("changed.jstree", function (e, data) {
                debugger;
                var id = data.node.data.id;

//                if (data.node.data.KC_ID) {
//                    //选中考场
//                    self.setKcInfo(data.node.data);
//                } else {
//                    var parentKcNode = $.jstree.reference(jstree).get_node(data.node.parent);
//                    //选中场次
//                    self.setCcInfo(data.node.data, parentKcNode.data);
//                }
            });
        },

        /**
         * 考生信息
         **/
        setDfpKsContent: function () {
            var ksArr = this.ksArr;
            var $ksTableTbody = $('#ksTable').find('tbody');
            $ksTableTbody.html('');
            for (var i = 0; i < ksArr.length; i++) {
                var ks = ksArr[i];
//                var bmCode = ks.BM_CODE;
//                var bmMk = ks.BM_MK;
//                var bmMkName = ks.BM_MK__NAME;
//                var bmName = ks.BM_NAME;
//                var bmType = ks.BM_TYPE;
//                var bmTypeName = ks.BM_TYPE__NAME;
//                var bmXl = ks.BM_XL;
//                var shId = ks.SH_ID;
//                var odeptName = ks.S_ODEPT__NAME;
//                var bmxlName = ks.BM_XL__NAME;
                $ksTableTbody.append([
                    '<tr scope="row">',
                    '   <td><input type="checkbox"></td>',
                    '   <td>' + (i + 1) + '</td>',//序号
                    '   <td>' + ks.org1 + '</td>',//一级机构
                    '   <td>' + ks.org2 + '</td>',//二级机构
                    '   <td>' + ks.org3 + '</td>',//三级机构
                    '   <td>' + ks.org4 + '</td>',//四级机构
                    '   <td>' + ks.BM_NAME + '</td>',//姓名
                    '   <td>' + ks.BM_XL + '-' + ks.BM_MK + '</td>',//考试名称
                    '   <td>' + FireFly.getDictNames(FireFly.getDict('TS_XMGL_BM_KSLBK_LV'), ks.BM_TYPE) + '</td>',//级别
                    '   <td>Mark</td>',//报考数
                    '   <td>Mark</td>',//状态
                    '   <td>' + ks.BM_CODE + '</td>',//人力资源编码
                    '</tr>'
                ].join(''));
                //添加表头复选框变更事件
                Utils.addTableCheckboxChangeEvent('ksTable');
            }
        },

        search: function () {
            var self = this;
            //条件  请求  渲染
            var params = this.getExtWhere();
            this.getKsArr(params, function () {
                self.setDfpKsContent();
            });
        },

        /*获取搜索条件*/
        getExtWhere: function () {
            var searchName = $('#search-name').val();
            var searchLoginName = $('#search-login-name').val();
            var searchBmXl = $('#search-bm-xl').val();
            var searchBmMk = $('#search-bm-mk').val();
            var searchBmJb = $('#search-bm-jb').val();
            var searchBmCount = $('#search-bm-count').val();
            return {
                searchDeptCode: this.deptCode,
                searchName: searchName,
                searchLoginName: searchLoginName,
                searchBmXl: searchBmXl,
                searchBmMk: searchBmMk,
                searchBmJb: searchBmJb,
                searchBmCount: searchBmCount
            };
//            return [];
//            alert('searchName:' + searchName + '\n'
//                + 'searchLoginName:' + searchLoginName + '\n'
//                + 'searchBmXl:' + searchBmXl + '\n'
//                + 'searchBmMk:' + searchBmMk + '\n'
//                + 'searchBmJb:' + searchBmJb,//FireFly.getDictNames(FireFly.getDict('TS_XMGL_BM_KSLBK_LV'), searchBmJb) + '\n'
//                +'searchBmCount:' + searchBmCount + '\n');
        },

        initSearchValue: function () {
            var self = this;
            //KSLBK_NAMEDF
//            KSLBK_XL
//            KSLBK_MK
//            TS_XMGL_BM_KSLBK_LV
            //search-bm-xl search-bm-mk search-bm-jb
            var xlDictData = Utils.getDictData("", 'KSLBK_XL');
            var jbDictData = FireFly.getDict('TS_XMGL_BM_KSLBK_LV')[0].CHILD;
            Utils.setOptionData('search-bm-xl', xlDictData);
            var xlSelect = $('#search-bm-xl');
            xlSelect.on('change', function () {
                var option = xlSelect.val();
                var mkDictData = Utils.getDictData(" and KSLBK_XL='" + option + "'", 'KSLBK_MK');
                Utils.setOptionData('search-bm-mk', mkDictData);
            });
            Utils.setOptionData('search-bm-jb', jbDictData);
        },

    };


    var Utils = {
        /**
         * 表格添加全选/全不选功能（复选框）
         * @param tableId table id
         */
        addTableCheckboxChangeEvent: function (tableId) {
            var $table = $('#' + tableId);
            var thCheckbox = $table.find('th input[type="checkbox"]');
            if (thCheckbox.length >= 0) {
                //th checkbox 全选/全不选 事件
                $(thCheckbox[0]).unbind('change').bind('change', function () {
                    var tdCheckboxs = $table.find('td input[type="checkbox"]');
                    for (var i = 0; i < tdCheckboxs.length; i++) {
                        var tdCheckbox = tdCheckboxs[i];
                        tdCheckbox.checked = this.checked;
                    }
                });
                //td checkbox td中checkbox变更，改变th checkbox
                var tdCheckboxs = $('#' + tableId).find('td input[type="checkbox"]');
                tdCheckboxs.unbind('change').bind('change', function () {
                    if (thCheckbox[0].checked && !this.checked) {
                        thCheckbox[0].checked = false;
                    } else {
                        var allChecked = true;
                        for (var i = 0; i < tdCheckboxs.length; i++) {
                            var tdCheckbox = tdCheckboxs[i];
                            if (!tdCheckbox.checked) {
                                allChecked = false;
                            }
                        }
                        thCheckbox[0].checked = allChecked;
                    }
                });
            }
        },

        setOptionData: function (selectId, dictData, selectValue) {
            selectValue = selectValue ? selectValue : '';
            var select = $('#' + selectId);
            //移除原有的选项
            select.find('option').remove();
            var opt = [];
            opt.push("<option value=''></option>");
            jQuery.each(dictData, function (i, dictItem) {
                opt.push("<option value='");
                opt.push(dictItem.ID);
                opt.push("'");
                if (dictItem.ID === selectValue) {
                    opt.push(" selected ");
                }
                opt.push(">");
                opt.push(dictItem.NAME);
                opt.push("</option>");
            });
            jQuery(opt.join("")).appendTo(select);
        },

        //获取级联下拉框数据 (考试类别库 TS_XMGL_BM_KSLBK)
        getDictData: function (linkWhere, codeName) {
            var data = {
                "_SELECT_": codeName,
                "_ORDER_": codeName,
                "_AFTER_SELECT_KEYWORDS": "distinct",
                "_linkWhere": linkWhere + " and " + codeName + " is not null ",
                "_NOPAGE_": "true"
            };
//            var result = FireFly.getListData("TS_XMGL_BM_KSLBK", data, false);
            var result = FireFly.doAct("TS_XMGL_BM_KSLBK", 'query', data);
            var dictData = [];
            for (var i = 0; i < result._DATA_.length; i++) {
                var dict = result._DATA_[i];
                dictData.push({ID: dict[codeName], NAME: dict[codeName]});
            }
            return dictData;
        }

    };
</script>

</body>
</html>
