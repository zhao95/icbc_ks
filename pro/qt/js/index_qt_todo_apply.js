/**
 * 首页待办/可申请报名列表初始化
 */

$(function () {
    showTodoContent();
    setApplyContent();
});

/**
 * 待办/提醒页面内容
 */
function showTodoContent() {

    //获取待办/提醒列表数据
    var todoListEl = jQuery('.index-qt-todo-list');
    todoListEl.html('');
    var data = {};
    data = {_NOPAGE_: true};
    var todoListPageList = FireFly.doAct("TS_COMM_TODO", 'query', data, false);
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
        $('#todoListSum').html(todoList.length);//设置待办总数
        var itemContent = jQuery(
            [
                '<div id="' + item.TODO_ID + '" data-id="' + item.DATA_ID + '" style="" class="todo-content">',
                '   <div style="min-height: 17px;">' + item.TITLE + '</div>',
                '   <div style="font-size: 12px;color:#999999;min-height: 17px;">',
                '   ' + item.SEND_DEPT_NAME + ' ' + item.SEND_NAME + ' ' + new Date(item.SEND_TIME).format("yyyy-mm-dd HH:MM"),
                '   </div>',
                '</div>'].join('')
        ).bind('click', function () {//跳转到页面详情（请假/借考/异议）
            var dataId = $(this).attr('data-id');
            var todoId = $(this).attr('id');
            doPost("/ts/jsp/qjlb_qj2.jsp", {todoid: todoId, qjid: dataId});
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
}

/**
 * 可申请报名列表
 */
function setApplyContent() {
    // TS_XMGL;
    var userCode = System.getVar("@USER_CODE@");//当前登录用户code
    var userXmListBean = FireFly.doAct('TS_XMGL', 'getUserXm', {user_code: userCode});
    var userXmList = JSON.parse(userXmListBean.list);

    $('#keshenqingbaomingSum').html(userXmList.length);

    var applyListEl = $('#apply-panel .grid-tbody');
    applyListEl.html('');
    for (var i = 0; i < userXmList.length; i++) {
        if (i === 4) {
            return false;
        }

        var userXm = userXmList[i];
        var xmId = userXm.XM_ID;
        var name = userXm.XM_NAME;
        var deptCode = userXm.XM_FQDW_NAME;
        var kstype = userXm.XM_TYPE;

        var where1 = "AND XM_ID=" + "'" + xmId + "'";
        var data = {
            "_SELECT_": "BM_START,BM_END,XM_ID,XM_SZ_ID ",
            "_extWhere": where1,
            "_NOPAGE_": "true"
        };
        var listBean = FireFly.doAct("TS_XMGL_BMGL", "query", data);
        var bmBean = listBean._DATA_[0];
        var startTime = bmBean.BM_START;
        var endTime = bmBean.BM_END;

        var canApply = false;

        if (startTime !== '' && startTime !== null && startTime !== undefined
            && endTime !== '' && endTime !== null && endTime !== undefined
            && new Date().getTime() >= Date.parse(startTime) && new Date().getTime() < Date.parse(endTime)) {
            //当前可报名
            canApply = true;
        }

        var trEl = jQuery(
            [
                '<tr>',
                '   <td>' + name + '</td>',
                '   <td>' + deptCode + '</td>',
                '   <td>' + startTime + ' － ' + endTime + '</td>',
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
                    // document.getElementById("zgtz").value = id;
                    // document.getElementById("form1").submit();
                };

            } else {
                result = function () {
                    postData = {fzgtz: xmId};
                    doPost('/ts/jsp/bmglf.jsp', postData);
                    // document.getElementById("fzgtz").value = id;
                    // document.getElementById("form2").submit();
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