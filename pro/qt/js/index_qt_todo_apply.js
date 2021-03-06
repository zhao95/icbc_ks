/**
 * 首页待办/可申请报名列表/通知公告初始化
 */

$(function () {
    showTodoContent();
    setApplyContent();
    setAnnouncementContent();
    /**
     * 跳转到后台的按钮可见判断
     * 首页上标题的信息展示方法
     * 点击跳转到用户个人信息的方法
     * 注销用户的方法
     */
    showTodoTip();
    jQuery("#loginOutBtn").unbind("click").click(clickAndLoginOut);
    jQuery("#TipUserInfo").unbind("click").click(userInfoPage);
    /** 点击左侧认证轨迹图片跳转到认证轨迹*/
//    jQuery("#left-img-renzheng").unbind("click").click(function () {
//        location.href = "/eti/jsp/rzgj.jsp";
//    });
});

/**
 * 待办/提醒页面内容
 */
function showTodoContent() {

    //获取待办/提醒列表数据
    var todoListEl = jQuery('.index-qt-todo-list');
    todoListEl.html('');
    var data = {};
    data = {_extWhere: "and OWNER_CODE='" + System.getUser("USER_CODE") + "'", _NOPAGE_: true};
    /*var todoListPageList =*/
    FireFly.doAct("TS_COMM_TODO", 'getTodoXMListHasType', data, false, true, function (todoListPageList) {
        var todoXMList = todoListPageList._DATA_;

        var typeNameMap = {
            '0': '请假',
            '1': '借考',
            '2': '异议'
        };

        var colorClassNameMap = {
            '0': 'qijia',
            '1': 'jiekao',
            '2': 'yiyi'
        };

        for (var i = 0; i < todoXMList.length; i++) {
            var item = todoXMList[i];
            //最多显示3个待办
            if (i === 3) {
                return false;
            }
            var batchType = '';
            if (item.TYPE === '0') {
                batchType = '1';
            } else if (item.TYPE === '1') {
                batchType = '2';
            }
            var typeName = typeNameMap[item.TYPE];
            typeName = typeName ? typeName : '';
            var colorClassName = colorClassNameMap[item.TYPE];
            colorClassName = colorClassName ? colorClassName : 'yichang';
            $('#todoListSum').html(todoXMList.length);//设置待办总数
            var itemContent = jQuery(
                [
                    '<div id="' + item.XM_ID + '" batch-type="' + batchType + '" style="" class="todo-content">',
                    '   <div style="min-height: 17px;">' + item.XM_NAME + '</div>',
                    // '   <div style="font-size: 12px;color:#999999;min-height: 17px;">',
                    // '   ' + item.SEND_DEPT_NAME + ' ' + item.SEND_NAME + ' ' + ((item.SEND_TIME && item.SEND_TIME.length >= 16) ? item.SEND_TIME.substring(0, 16) : ''),
                    // '   </div>',
                    '</div>'].join('')
            );
            itemContent.bind('click', function () {//跳转到页面详情（请假/借考/异议）
                var batchType = $(this).attr('batch-type');
                var xmId = $(this).attr('id');
                indexUtils.goTodoList(xmId, batchType);
            });

            todoListEl.append(
                jQuery([
                    '<div style="" class="todo-item">',
                    '   <div style="" class="todo-circle ' + colorClassName + '">',
                    '       <div style="padding:10px 4px;color: #fff">' + typeName + '</div>',
                    '   </div>',
                    '</div>'
                ].join('')).append(itemContent)
            );
        }
    });
}

/**
 * 可申请报名列表
 */
function setApplyContent() {
    var userCode = System.getVar("@USER_CODE@");//当前登录用户code
    /*var userXmListBean = */
    FireFly.doAct('TS_XMGL', 'getUserXm', {user_code: userCode}, false, true, function (userXmListBean) {
        var userXmList = userXmListBean.list;

        $('#keshenqingbaomingSum').html(userXmList.length);//可申请报名数目

        var applyListEl = $('#apply-panel').find('.grid-tbody');
        applyListEl.html('');
        for (var i = 0; i < userXmList.length; i++) {
            if (i === 3) {
                //展示3条
                return false;
            }

            var userXm = userXmList[i];
            var xmId = userXm.XM_ID;
            var name = userXm.XM_NAME;
            var deptCode = userXm.XM_FQDW_NAME;
            var kstype = userXm.XM_TYPE;

            //获取报名时间判断  报名状态
            var param1 = {};
            param1["xmid"] = xmId;
            var resultBean = FireFly.doAct("TS_XMGL_BMGL", "getBMState", param1);
            var list = resultBean.list;
            var startTime = list[0].START_TIME;
            var endTime = list[0].END_TIME;
            var state = list[0].STATE;

            var canApply = false;
            if (state === "待报名") {
                //当前可报名
                canApply = true;
            }
            var startTimeStr = '';
            var endTimeStr = '';
            if (startTime && startTime.length >= 10) {
                startTimeStr = startTime.substring(0, 10);
            }
            if (endTime && endTime.length >= 10) {
                endTimeStr = endTime.substring(0, 10);
            }

            var trEl = jQuery(
                [
                    '<tr>',
                    '   <td>' + name + '</td>',
                    '   <td>' + deptCode + '</td>',
                    '   <td>' + startTimeStr + ' － ' + endTimeStr + '</td>',
                    '</tr>'
                ].join('')
            );

            /*点击行跳转到报名列表*/
            function trClickFunction(xmId, kstype) {
                var result;
                if (kstype === "资格类考试") {
                    result = function () {
                        var postData = {zgtz: xmId};
                        doPost('/ts/jsp/bmzgks.jsp', postData);
                    };

                } else {
                    result = function () {
                        postData = {fzgtz: xmId};
                        doPost('/ts/jsp/bmglf.jsp', postData);
                    };
                }
                return result;
            }

            //可报名添加点击事件
            if (canApply) {
                trEl.css('cursor', 'pointer');
                trEl.bind('click', trClickFunction(xmId, kstype));
            }
            trEl.appendTo(applyListEl);
        }
    });
}

/**
 * 通知公告
 */
function setAnnouncementContent() {
    var data = {};
    var params = {};
    var currentUserPvlg = FireFly.getCache(System.getUserBean().USER_CODE, FireFly.userPvlg);
    params["USER_PVLG"] = currentUserPvlg["TS_GG_PVLG"];
    data["extParams"] = params;
    data["_ORDER_"] = "GG_SORT,S_ATIME";
    FireFly.doAct("TS_GG", 'finds', {data: JSON.stringify(data)}, false, true, function (ggList) {
        var tbodyEl = jQuery('#announcement-box').find('.table tbody');
        tbodyEl.html('');

        var circleColors = ['#398daf', '#b4dbc0', '#ff0000', '#62a9d8', '#9521b8'];

        for (var i = 0; i < ggList._DATA_.length; i++) {
            if (i === 5) {
                return false;
            }
            var gg = ggList._DATA_[i];
            var trEl = jQuery([
                '<tr id="' + gg.GG_ID + '">',
                '   <td class="col-md-8">',
                '       <span style="color: ' + circleColors[i] + ';width: 16px;height: 16px;display: inline-block;font-size: 13px;text-align: center;">●</span>',
                '       <a href="' + FireFly.getContextPath() + '/qt/jsp/gg.jsp?id=' + gg.GG_ID + '"target="_blank" style="display:inline;cursor:pointer;" class="gg-title">' + gg.GG_TITLE + '</a>',
                '   </td>',
                '<td class="col-md-4">' + ((gg.S_ATIME && gg.S_ATIME.length >= 10) ? gg.S_ATIME.substring(0, 10) : '') + '</td>',//yyyy-mm-dd HH:MM
                '</tr>'
            ].join(''));
            trEl.appendTo(tbodyEl);
        }
    });


}

/**
 * form表单post请求
 * @param to 例：/ts/jsp/qjlb_qj2.jsp
 * @param data {property1:value1,property2:value2}
 */
function doPost(to, data) {  //to:提交动作（action）,data:参数
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
/**
 * 首页上标题栏 提醒信息按钮的下拉框内容
 */
function showTodoTip() {
    //跳转后台按钮的可见判断
    var btnParam = {};
    /*var btnRoleResult =*/
    FireFly.doAct("TS_GG", "btnRoleFun", btnParam, false, true, function (btnRoleResult) {
        var hasRoleFlag = btnRoleResult.hasRole;
        if (hasRoleFlag !== undefined) {
            if (hasRoleFlag === "1") {
                jQuery("#btnToHT").css("display", "block");
            } else if (hasRoleFlag === "2") {
                jQuery("#btnToHT").css("display", "none");
            }
        }
    });

    //获取待办/提醒列表数据
    var tipListEl = jQuery('.index-qt-tip-list');
    tipListEl.html('');
    var data = {};
    data = {_NOPAGE_: true};
    data = {_extWhere: "and OWNER_CODE='" + System.getUser("USER_CODE") + "'", _NOPAGE_: true};
    /*  var todoListPageList = */
    FireFly.doAct("TS_COMM_TODO", 'query', data, false, true, function (todoListPageList) {
        var todoList = todoListPageList._DATA_;

        var typeNameMap = {
            '0': '请假',
            '1': '借考',
            '2': '异议'
        };

        var colorClassNameMap = {
            '0': 'qijia',
            '1': 'jiekao',
            '2': 'yiyi'
        };

        tipListEl.append(
            '<li class="header"><a href="#">待办 / 消息</a></li>'
        );

        tipListEl.append('<li class="body">');

        for (var i = 0; i < todoList.length; i++) {
            var item = todoList[i];
            //最多显示3个待办
            if (i === 3) {
                return false;
            }
            var typeName = typeNameMap[item.TYPE];
            typeName = typeName ? typeName : '';
            var colorClassName = colorClassNameMap[item.TYPE];
            colorClassName = colorClassName ? colorClassName : 'yichang';
            $('#tipSum').html(todoList.length);//设置待办总数
            var itemContent = jQuery(
                [
                    '<div id="' + item.TODO_ID + '" data-id="' + item.DATA_ID + '" style="text-lign:center;margin-left: 10px;" class="todo-content">',
                    '   <div style="min-height: 17px;">' + item.TITLE + '</div>',
                    '   <div style="font-size: 12px;color:#999999;min-height: 15px; cursor: pointer;">', '<i class="fa fa-envelope-o fa-fw"></i>',
                    '   ' + item.SEND_DEPT_NAME + ' ' + item.SEND_NAME + ' ' + ((item.SEND_TIME && item.SEND_TIME.length >= 16) ? item.SEND_TIME.substring(0, 16) : ''),
                    '   </div>',
                    '</div>'].join('')
            );
            itemContent.css('cursor', 'pointer');
            if (item.TYPE === '0') {
                itemContent.bind('click', function () {//跳转到页面详情（请假/借考/异议）
                    var dataId = $(this).attr('data-id');
                    var todoId = $(this).attr('id');
                    doPost("/ts/jsp/qjlb_qj2.jsp", {todoId: todoId, qjid: dataId, hidden: '2'});
                });
            } else if (item.TYPE === '1') {
                itemContent.bind('click', function () {//跳转到页面详情（请假/借考/异议）
                    var dataId = $(this).attr('data-id');
                    var todoId = $(this).attr('id');
                    doPost("/ts/jsp/jklb_jk2.jsp", {todoId: todoId, jkid: dataId, hidden: '2'});
                });
            }

            tipListEl.append(itemContent);
        }
        //如无数据，则提示
        if (todoList.length === 0) {
            tipListEl.append(
                [
                    '<div style="text-align:center; min-height: 100px;font-size: 14px;color:#999999;padding-top:10px;">',
                    '暂无消息/待办</div>'].join('')
            );
        }
        tipListEl.append('</li>');

        var tipViewAll = tipListEl.append(
            '<li class="footer" style="padding-top:10px;border-top:1px solid #f4f4f4; "><a href="#">查看所有待办 / 消息</a></li>'
        ).bind('click', function () {//跳转到页面详情（请假/借考/异议）
            doPost("/qt/jsp/todo.jsp");
        });
    });

}
/**
 * 点击注销用户按钮触发注销用户的操作
 */
function clickAndLoginOut() {
    var param = {};
    FireFly.doAct("SY_ORG_LOGIN", "logout", param);
    window.location.href = "/logout.jsp";
}
/**
 * 点击个人信息跳转到用户个人信息界面
 */
function userInfoPage() {
    $.ajax('/qt/jsp/user_info.jsp').then(function (response) {
        $('section[class="content"]').html(response);
    })
//    window.location.href = "/qt/jsp/user_info.jsp";

}


var indexUtils = {

    /**
     * 跳转到待办页面
     * @param xmId
     * @param batchType 1 请假 2 借考
     */
    goTodoList: function (xmId, batchType) {
        doPost("/qt/jsp/todo.jsp", {batch: batchType, XM_ID: xmId});
    }
};