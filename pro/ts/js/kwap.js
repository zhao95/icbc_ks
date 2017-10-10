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
    xmId: '',
    rootData: '',//后端树数据
    rootNodes: '',//处理后的树数据
    kcArr: [],//所有待安排考场
    ccArr: [],//所有待安排场时间
    kctree: {},//场次树jstree对象
    currentCc: '',//当前显示的场次
    currentParentKc: '',//当前显示的考场
    /*初始化界面数据*/
    initData: function (xmId) {
        this.xmId = xmId;
        this.getInitData(function () {
            this.setCCTree();
            this.setAllKcInfo();
        });//setCCTree setAllKcInfo方法在加载完数据后执行

    },
    /*从后端获取初始化数据并处理*/
    getInitData: function (callback) {
        var self = this;//self指向KcObject
        FireFly.doAct("TS_XMGL_KCAP_DAPCC", "getKcAndCc", {
                "xmId": self.xmId
            }, false, false, function (data) {
                //处理数据
                if (data !== "") {
                    self.rootData = data.root;
                    self.getCCTreeNodes();
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
        this.recursiveTreeData(rootData);
        return this.rootNodes;
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
                //显示考场关联机构人员
                KsObject.setKcId(data.node.data.KC_ID);
            } else if (dataType === 'cc') {
                var parentKcNode = $.jstree.reference(jstree).get_node(data.node.parent);
                //选中场次
                self.setCcInfo(data.node.data, parentKcNode.data);
                //显示考场关联机构人员
                KsObject.setKcId(parentKcNode.data.KC_ID);
            } else if (dataType === 'dept') {
                KcObject.setOrgKcInfo(data.node.data.DEPT_CODE);
//                    var parent = data.node.parent;
//                    if (parent !== '#') {
//                        var deptCode = data.node.data.DEPT_CODE;
//                        KsObject.setInitData(deptCode);
//                    } else {
//                        KcObject.setAllKcInfo();
//                    }
            }
        });
    },

    /**
     * 机构下的考场信息
     */
    setOrgKcInfo: function (deptCode) {
        var kcArrFilter = [];
        for (var i = 0; i < this.kcArr.length; i++) {
            var kc = this.kcArr[i];
            if (kc.KC_ODEPTCODE === deptCode) {
                kcArrFilter.push(kc);
            }
        }
        this.setKcArrInfo(kcArrFilter);
    },

    /**
     * 全考场信息
     */
    setAllKcInfo: function () {
        this.setKcArrInfo(this.kcArr);
    },

    /**
     * 根据kcArr渲染考场信息
     */
    setKcArrInfo: function (kcArr) {
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

        var ccTimes = [];//kcArrhz中对应的所有场次时间
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
            var ccId = cc.CC_ID;
            var sjId = cc.SJ_ID;
//                var kcData = FireFly.doAct("TS_XMGL_KCAP_DAPCC", 'byid', {
//                    _PK_: ccId
//                }, false, false);
            var kcId = this.currentParentKc.KC_ID;
            var zwListBean = FireFly.doAct("TS_KCGL_ZWDYB", 'query', {
                _WHERE_: " and KC_ID = '" + kcId + "'",
                _ORDER_: " ZW_ZWH_XT asc"
            }, false, false);
            var zwList = zwListBean._DATA_;
            var tData = [], trData, preLetter = null;
            for (var i = 0; i < zwList.length; i++) {
                var zw = zwList[i];
                if (preLetter !== zw.ZW_ZWH_XT.substring(0, 1)) {
                    if (trData !== undefined) {
                        tData.push(trData);
                    }
                    trData = [];
                    preLetter = zw.ZW_ZWH_XT.substring(0, 1);
                }
                trData.push(zw);
            }
            if (trData !== undefined) {
                tData.push(trData);
            }

            for (var i = 0; i < tData.length; i++) {
                trData = tData[i];
                var $tr = jQuery("<tr></tr>");
                for (var j = 0; j < trData.length; j++) {
                    var tdData = trData[j];
                    var $td = jQuery('<td id="' + tdData.ZW_ID + '" style="width:10%;">' +
                        '   <span>' + tdData.ZW_ZWH_SJ + '</span>' +
                        '   <span class="userName"></span>' +
//                            '   <span class="close">x</span>' +
                        '</td>');
                    $tr.append($td);
                }
                $kcInfoTbody.append($tr);
            }
            /**/
            this.addDroppableEvent($("#kcInfo").find("td"));

            this.setZw(sjId);

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
    },

    setZw: function (sjId) {
        var zwListBean = FireFly.doAct("TS_XMGL_KCAP_YAPZW", "getYapZw", {SJ_ID: sjId});
        var zwList = zwListBean._DATA_;
        for (var i = 0; i < zwList.length; i++) {
            var zw = zwList[i];
            $('#' + zw.ZW_ID).attr('shid', zw.SH_ID);
            $('#' + zw.ZW_ID).find('.userName').html(zw.BM_NAME);
            this.setZwContent(zw.ZW_ID, zw.YAPZW_ID, zw.BM_NAME);
        }
    },

    setZwContent: function (zwId, yapzwId, userName) {
        var self = this;
        var $zw = $('#' + zwId);
        $zw.attr('yapzwId', yapzwId);
        $zw.find('.userName').html(userName);
//            $zw.droppable("destroy");
        $zw.droppable("disable");
        var $span = jQuery('<span class="close">x</span>');
        $span.unbind('click').bind('click', function () {
            FireFly.doAct("TS_XMGL_KCAP_YAPZW", "delete", {_PK_: yapzwId}, false, false, function () {
                var $zw = $('#' + zwId);
                $zw.droppable("enable");
//                    self.addDroppableEvent($zw);
                $zw.find('.userName').html('');
                $zw.find('.close').remove();
                KsObject.search();
            });
        });
        $zw.append($span);
    },

    /**
     * 添加拖拉放置事件
     **/
    addDroppableEvent: function ($element) {
        var self = this;
        $element.droppable({
            activeClass: "droppable-active",//"ui-state-default",  droppable-active
            hoverClass: "droppable-hover",//"ui-state-hover",  droppable-hover
            drop: function (event, ui) {
//                    ccId;
                //YAPZW_ID
//                    CC_ID
//                    ZW_ID
//                    KC_ID
//                    SH_ID
//                    SJ_ID
                var sjid = self.currentCc.SJ_ID;
                var ccid = self.currentCc.CC_ID;
                var kcid = self.currentParentKc.KC_ID;
                var shId = $(ui.draggable[0]).attr('id');
                var zwId = $(this).attr('id');
                var userCode = ui.draggable[0].cells[11].innerText.trim();
                var _this = this;
                FireFly.doAct("TS_XMGL_KCAP_YAPZW", 'save', {
                    ZW_ID: zwId,
                    SJ_ID: sjid,
                    CC_ID: ccid,
                    KC_ID: kcid,
                    SH_ID: shId
                }, false, false, function (data) {
                    self.setZwContent(zwId, data.YAPZW_ID, ui.draggable[0].cells[6].innerText);
//                        $(_this).find(".userName").html(ui.draggable[0].cells[6].innerText);
                });
                KsObject.search();
//                    var inventor = ui.draggable.text();
//                    $(this).find("input").val(inventor);
//                    $(c.tr).remove();
//                    $(c.helper).remove();
            }
        });
    }
};

// function setCCContent1() {
//     var $leftSidebarContent = $('#left-sidebar-content');
// //        $leftSidebarContent.html('');
// //        var param = {
// //            "_linkWhere": " and XM_ID='" + xmId + "' ",
// //            "_linkServQuery": "2",
// //            "XM_ID": xmId
// //        };
// //        var data = FireFly.doAct('TS_XMGL_KCAP_DAPCC', 'query', param);
//
//     var setting = {
//         showcheck: true,
//         rhexpand: false,
//         expandLevel: 1,
//         url: "SY_COMM_INFO.dict.do",
//         theme: "bbit-tree-no-lines",
//         rhItemCode: "CTLG_PCODE",
//         rhLeafIcon: "",
//         data: []
//     };
//     var tree = new rh.ui.Tree(setting);
//     tree.obj.appendTo($leftSidebarContent);
// }

var KsObject = {
    xmId: '',
//        deptCode: '',//那个部门下的考生
    kcId: '',//考场id
    ksArr: [],//考生信息
    ksOrgTree: '',
    initData: function (xmId) {
        this.xmId = xmId;
//            this.getKsArr(null, function () {
//                this.setDfpKsContent();
////                this.setKsOrgContent();
//            });
        this._initSearchValue();
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

    setKcId: function (kcId) {
        if (this.kcId !== kcId) {
            this.kcId = kcId;
            this.setKsOrgContent(kcId);
        }
    },

    setInitData: function (deptCode) {
        this.setKsOrgContent(deptCode);
        this.getKsArr(null, function () {
            this.setDfpKsContent();
//                this.setKsOrgContent();
        });
    },

    getKsArr: function (params, callback) {
        var self = this;
        var param = {
            _linkWhere: " and XM_ID='" + self.xmId + "' ",
            _linkServQuery: "2",
            XM_ID: self.xmId
        };
        jQuery.extend(param, params);
        FireFly.doAct("TS_XMGL_KCAP_DAPCC", 'getKsContent', param, false, false, function (data) {
            self.ksArr = data._DATA_;
            if (callback) {
                callback.apply(self);
            }
        });
    },


    /**
     * 考生机构
     */
    setKsOrgContent: function (kcId) {
        var self = this;
        kcId = kcId ? kcId : '';
        var param = {kcId: kcId};
        FireFly.doAct("TS_XMGL_KCAP_DAPCC", 'getKsOrgTree', param, false, false, function (data) {
            console.log(data);
            var root = {
                id: data.DEPT_CODE,
                text: data.DEPT_NAME,
                data: {id: data.DEPT_CODE, text: data.DEPT_NAME},
                children: []
            };
            var $ksOrgTreeContent = $('#ksOrgTree').find('.content-navTree');
//                $ksOrgTreeContent.html('');
//            var data = FireFly.getDict('SY_ORG_ODEPT_ALL', pdeptCode);
//            var deptName = FireFly.getDictNames(FireFly.getDict('SY_ORG_ODEPT_ALL'), pdeptCode);
//            var root = {id: pdeptCode, text: deptName, data: {id: pdeptCode, text: deptName}, children: []};

            var putChildren = function (parent, childs) {
                childs = childs ? childs : [];
                for (var i = 0; i < childs.length; i++) {
                    var child = childs[i];
                    var id = child.DEPT_CODE ? child.DEPT_CODE : child.id;
                    var text = child.DEPT_NAME ? child.DEPT_NAME : child.NAME;
                    var item = {
                        id: id,
                        text: text,
                        data: child,
                        children: []
                    };
                    parent.children.push(item);
                    putChildren(item, child.CHILD);
                }
            };

            var childs = data.CHILD;
            putChildren(root, childs);

            try {
                self.ksOrgTree.jstree('destroy');//已经初始化tree，先destroy
            } catch (e) {
            }
            self.ksOrgTree = $ksOrgTreeContent.jstree({
                'core': {
                    "multiple": false,
                    'data': [root]
                }
            });

            $ksOrgTreeContent.on("changed.jstree", function (e, data) {
                var id = data.node.data.id ? data.node.data.id : data.node.data.ID;
                id = id ? id : data.node.data.DEPT_CODE;
                self.searchDeptCode = id;
                self.search();

//                if (data.node.data.KC_ID) {
//                    //选中考场
//                    self.setKcInfo(data.node.data);
//                } else {
//                    var parentKcNode = $.jstree.reference(jstree).get_node(data.node.parent);
//                    //选中场次
//                    self.setCcInfo(data.node.data, parentKcNode.data);
//                }
            });

        });

    },

    /**
     * 考生机构
     */
    setKsOrgContent2: function (pdeptCode) {
        pdeptCode = pdeptCode ? pdeptCode : '';
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
                '<tr scope="row" id="' + ks.SH_ID + '">',
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
        }

        Utils.addTableCheckboxChangeEvent('ksTable');


        $("#ksTable tbody tr").draggable({
            // cursor: 'move',
            cursorAt: {left: 33, top: 55},
            containment: 'body',
            appendTo: 'body',
            helper: function (event) {
                var cells = event.currentTarget.cells;
                // debugger;
                return [
                    '<div style="/*width:30px;height: 30px;*/border:1px solid #999999;background-color: #91BDEA;">',//
                    '   <div>' + cells[6].innerText + '</div>',
                    '   <div>' + cells[7].innerText + '</div>',
                    '   <div>' + cells[11].innerText + '</div>',
                    '</div>'
                ].join('');
            },
        });

//            $ksTableTbody.find('tr').draggable({revert: true});
    },

    search: function () {
        var self = this;
        //条件  请求  渲染
        var params = this._getExtWhere();
        this.getKsArr(params, function () {
            self.setDfpKsContent();
        });
    },

    /*获取搜索条件*/
    _getExtWhere: function () {
        var searchName = $('#search-name').val();
        var searchLoginName = $('#search-login-name').val();
        var searchBmXl = $('#search-bm-xl').val();
        var searchBmMk = $('#search-bm-mk').val();
        var searchBmJb = $('#search-bm-jb').val();
        var searchBmCount = $('#search-bm-count').val();
        return {
            searchDeptCode: this.searchDeptCode,
            searchKcId: this.kcId,
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

    _initSearchValue: function () {
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
            var tdCheckboxs = $table.find('td input[type="checkbox"]');
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