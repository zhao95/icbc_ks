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
            alert(response._MSG_.substring(response._MSG_.indexOf('ERROR,'), response._MSG_.length));
        } else {
            var $tiJiao = $('#tiJiao');
            if (param.shstatus === '2') {
                //不同意 退回
                $('#tiJiaoTip').html('提示信息');
                $('#tiJiaoContent').html('流程已退回');
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
    if (todoId !== '') {
        var data = {_PK_: todoId};
        var todoBean = FireFly.doAct('TS_COMM_TODO', 'byid', data);
        var wfsId = todoBean.WFS_ID;
        data = {_ORDER_: 'NODE_STEPS desc', _extWhere: "and WFS_ID ='" + wfsId + "'", _NOPAGE_: true};
        var nodeListBean = FireFly.doAct('TS_WFS_NODE_APPLY', 'query', data);
        var nodeList = nodeListBean._DATA_;
        if (nodeList.length > 0) {
            var flowView = jQuery('#flowView');
            flowView.html('');
            flowView.append(
                [
                    '<div style="display: inline-block">',
                    '<span style="position: relative;left: 26px;top:3px;',
                    'font-weight: 700;font-style: normal;font-size: 21px;color: #FFFFFF;">1</span>',
                    '<img alt="" src="<%=CONTEXT_PATH %>/ts/image/u5522.png">',//style="width:20px;"
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
                        '<img alt=""  style="width:50px;height:5px;" src="<%=CONTEXT_PATH %>/ts/image/u5532.png">',
                        '<div style="display: inline-block">',
                        '   <span style="position: relative;left: 28px;top:3px;',
                        '       font-weight: 700;font-style: normal;font-size: 21px;color: #FFFFFF;">' + (i + 2) + '</span>',
                        '   <img alt="" src="<%=CONTEXT_PATH %>/ts/image/' + (nodeSteps === todoBean.NODE_STEPS ? 'u5520.png' : 'u5522.png') + '">',
                        '   <span style="position: relative;font-size: 15px;top: 5px;">&nbsp;&nbsp;' + nodeName + '&nbsp;&nbsp;</span>',
                        '</div>'
                    ].join('')
                );
            }
        }

    }
    //
}

/**
 * 下载证明材料
 */
function downImg(fileId) {
    rh.ui.File.prototype.downloadFile(fileId);
}

function fanhui() {
    window.history.go(-1);
}
