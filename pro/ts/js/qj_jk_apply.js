/*!
 * 请假/借考审批js公共代码提取
 !*/

/**
 *
 * @param servId
 * @param param {shname:'',todoId:'',shstatus:'',shreason:'',isRetreat:''}
 */
function updateData(servId, param) {
    FireFly.doAct(servId, "updateData", param, false, false, function (response) {
        if (response._MSG_.indexOf('ERROR,') >= 0) {
            //发起申请出错
            alert(response._MSG_.substring(response._MSG_.indexOf('ERROR,') + 6, response._MSG_.length));
        } else {
            var $tiJiao = $('#tiJiao');
            if (param.shstatus === '2') {
                //不同意 退回
                $('#tiJiaoTip').html('提示信息');
                $('#tiJiaoContent').html('该申请不通过');
            } else {
                //模态框
                if (response.shrNames) {
                    $('#shrNames').html(response.shrNames);
                    $('#tiJiaoTip').html('下一环节审批人');
                    $('#tiJiaoContent').html('已经提交给<span id="shrNames">' + response.shrNames + '</span>进行审核');
                } else {
                    $('#tiJiaoTip').html('提示信息');
                    $('#tiJiaoContent').html('审批已处理');
                }
            }
            //关闭提示框后返回到请假页面
            $tiJiao.on('hidden.bs.modal', function (/*e*/) {
                fanhui();
            });
            //显示提示框
            $tiJiao.modal('show');
        }
    });
}

/**
 * todoId
 * 加载流程图
 */
function showFlowView(todoId) {
    if (todoId) {
        var data = {_PK_: todoId};
        var todoBean = FireFly.doAct('TS_COMM_TODO', 'byid', data);
        var wfsId = todoBean.WFS_ID;
        showFlowViewByWfsId(wfsId, todoBean.NODE_STEPS);
    }
}

/**
 * 加载流程图
 * @param wfsId 流程id
 * @param todoNodeSteps 流程流转的节点（1，2，3）
 */
function showFlowViewByWfsId(wfsId, todoNodeSteps) {
    if (wfsId) {
        var data = {_ORDER_: 'NODE_STEPS desc', _extWhere: "and WFS_ID ='" + wfsId + "'", _NOPAGE_: true};
        var nodeListBean = FireFly.doAct('TS_WFS_NODE_APPLY', 'query', data);
        var nodeList = nodeListBean._DATA_;
        showFlowViewByNodeList(nodeList, todoNodeSteps);
    }
}


/**
 * 加载流程图
 * @param dataId 例请假id、借考id
 * @param todoNodeSteps 流程流转的节点（1,2,3）
 */
function showFlowViewByDataId(dataId, todoNodeSteps) {
    if (dataId) {
        var data = {DATA_ID: dataId, _extWhere: "and DATA_ID ='" + dataId + "'", _NOPAGE_: true};
        var todoList = FireFly.doAct('TS_COMM_TODO', 'query', data);
        if (todoList._DATA_ && todoList._DATA_.length > 0) {
            //流程未结束
            var todo = todoList._DATA_[0];
            showFlowView(todo.TODO_ID);
        } else {
            //流程已结束，没有待办信息
            data = {_ORDER_: 'NODE_STEPS desc', _extWhere: "and DATA_ID ='" + dataId + "'", _NOPAGE_: true};
            var nodeListBean = FireFly.doAct('TS_WFS_NODE_HISTORY', 'query', data);
            var nodeList = nodeListBean._DATA_;
            if (nodeList && nodeList.length > 0) {
                showFlowViewByNodeList(nodeList, todoNodeSteps);
            }
        }
    }
}

/**
 * 加载流程图
 * @param nodeList 流程节点list
 * @param todoNodeSteps 流程流转的节点（1，2，3）
 */
function showFlowViewByNodeList(nodeList, todoNodeSteps) {
    if (nodeList.length > 0) {
        var flowView = jQuery('#flowView');
        flowView.html('');
        flowView.append(
            [
                '<div style="display: inline-block">',
                '<span style="position: relative;left: 26px;top:3px;',
                'font-weight: 700;font-style: normal;font-size: 21px;color: #FFFFFF;">1</span>',
                '<img alt="" src="' + contextPath + '/ts/image/u5522.png">',//style="width:20px;"
                '<span style="position: relative;font-size: 15px;top: 5px;">&nbsp;&nbsp;填写申请单&nbsp;&nbsp;</span>',
                '</div>'
            ].join('')
        );
        for (var i = 0; i < nodeList.length; i++) {
            var node = nodeList[i];
            var nodeName = node.NODE_NAME;
            var nodeSteps = node.NODE_STEPS;
            flowView.append(
                [
                    '<img alt=""  style="width:50px;height:5px;" src="' + contextPath + '/ts/image/u5532.png">',
                    '<div style="display: inline-block">',
                    '   <span style="position: relative;left: 28px;top:3px;',
                    '       font-weight: 700;font-style: normal;font-size: 21px;color: #FFFFFF;">' + (i + 2) + '</span>',
                    '   <img alt="" src="' + contextPath + '/ts/image/' + (nodeSteps === todoNodeSteps ? 'u5520.png' : 'u5522.png') + '">',
                    '   <span style="position: relative;font-size: 15px;top: 5px;">&nbsp;&nbsp;' + nodeName + '&nbsp;&nbsp;</span>',
                    '</div>'
                ].join('')
            );
        }
    }
}

/**
 * 下载证明材料
 */
function downImg(fileId) {
    rh.ui.File.prototype.downloadFile(fileId);
}

function fanhui() {
    window.history.go(-1);
    //window.location.href = "/qt/jsp/t odo.jsp";
}
