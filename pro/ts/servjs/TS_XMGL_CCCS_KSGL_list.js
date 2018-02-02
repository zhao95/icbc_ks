var _viewer = this;

$(".rhGrid").find("tr").unbind("dblclick");

/*
 * 业务可覆盖此方法，在导航树的点击事件加载前
 */
_viewer.beforeTreeNodeClickLoad = function (item, id, dictId) {
    var params = {};
    var user_pvlg = _viewer._userPvlg[_viewer.servId + "_PVLG"];
    params["USER_PVLG"] = user_pvlg;
    _viewer.whereData["extParams"] = params;
    var flag = getListPvlg(item, user_pvlg, "CODE_PATH");
    _viewer.listClearTipLoad();
    return flag;
};

_viewer.afterTreeNodeClick = function (item, id, dictId) {
    _viewer._transferData["S_ODEPT"] = item.ODEPT_CODE;
    _viewer._transferData["S_ODEPT__NAME"] = item.ODEPT_CODE;
};

/*
 * 删除前方法执行
 */
_viewer.beforeDelete = function (pkArray) {
    showVerify(pkArray, _viewer);
};


var xmId = _viewer.getParHandler().getItem("XM_ID").getValue();
//导入模板下载
var $impFile = _viewer.getBtn("tmplBtn");
var xmBean = FireFly.doAct("TS_XMGL", "byid", {_PK_: _viewer.links.XM_ID});
$impFile.unbind('click').bind('click', function () {
    if (xmBean.XM_ID) {
        if (xmBean.XM_TYPE === '资格类考试') {
            window.open(FireFly.getContextPath() + '/ts/imp_template/项目管理_待安排考生_导入模版.xls');
        } else {
            window.open(FireFly.getContextPath() + '/ts/imp_template/项目管理_待安排考生_非资格考试_导入模版.xls');
        }
    }
});

/**
 * 系统管理员和项目创建人 显示添加和删除按钮
 */
var userCode = System.getVar("@USER_CODE@");
if (userCode !== 'admin') {
    if (_viewer.links.XM_ID !== 'undefined') {
        var data = {};
        data["_WHERE_"] = " and S_USER ='" + userCode + "'  and XM_ID = '" + _viewer.links.XM_ID + "'";

        FireFly.doAct("TS_XMGL", "count", data, false, true, function (result) {

            if (result._MSG_.indexOf("ERROR,") === 0) {
                _viewer.getBtn("add").hide();
                _viewer.getBtn("delete").hide();
            } else {

                if (result["_OKCOUNT_"] <= 0) {
                    _viewer.getBtn("add").hide();
                    _viewer.getBtn("delete").hide();
                }
            }
        });
    }
}

if (xmBean.XM_ID && xmBean.S_USER === System.getUser("USER_CODE")) {
    var i = 0;
    _viewer.getBtn("impUser").unbind('click').bind('click',
        ImpUtils.impFileFunction(_viewer, "saveFromExcel", function () {
            return {XM_ID: _viewer.links.XM_ID};
        })
    );
} else {
    _viewer.getBtn("impUser").remove();
}

_viewer.getBtn("impUserToJk").unbind('click').bind('click',
    ImpUtils.impFileFunction(_viewer, "saveFromExcel", function () {
        return {XM_ID: _viewer.links.XM_ID, OPT_TYPE: 'JK'};
    })
);

//导出
_viewer.getBtn("exp").unbind("click").bind("click", function () {
    var data = {XM_ID: xmId};
    window.open(FireFly.getContextPath() + '/' + _viewer.servId + '.expAll.do?data=' +
        encodeURIComponent(JSON.stringify(data)));
});

var search = $("#TS_XMGL_CCCS_KSGL .rhGrid-btnBar").find("table[class='searchDiv']");
if (search.length == 0) {
    var searchDiv = '<table class="searchDiv" style="float:right;margin-right: 8px;text-align: right;line-height: 26px;"><tbody><tr><td><select class="rhSearch-select" id="mySelect"><option value="BM_NAME">姓名</option><option value="BM_CODE">人力资源编码</option></select></td><td><input type="text" onfocus="this.value=\'\'" value="输入条件" class="rhSearch-input" id="myInput"></td><td text-valign="middle"><div class="rhSearch-button"><div class="rhSearch-inner">查询</div></div></td></tr></tbody></table>';
    $("#TS_XMGL_CCCS_KSGL .rhGrid-btnBar").append(searchDiv);
}

$(".rhSearch-button").bind("click", function () {
    _viewer.listBarTipLoad("结果加载中..");
    setTimeout(function () {
        var searcCode = $("#mySelect").val();
        var seaValue = jQuery.trim($("#myInput").val().replace(/\'/g, ""));
        var where = "";
        if ((seaValue.length > 0) && (seaValue != "输入条件")) {//TODO：完善
            where += " and ";
            if (seaValue.indexOf("\%") >= 0) {
                where += searcCode + " like '" + seaValue + "'";
            } else {
                where += searcCode + " like '%" + seaValue + "%'";
            }
        }
        _viewer.setSearchWhereAndRefresh(where, true);
        return false;
    }, 0);
});

/*******/
var mySearch = $(".content-navTreeCont").find("input[id='myTreeSearch']");
if(mySearch.length == 0){
	var serachDiv = "<input type='text' id='myTreeSearch' class='rhSearch-input' style='width:65%;'><input id='myTreeSearchBtn' type='button' value='查询'>";
	$(".bbit-tree").prepend(serachDiv);
}

$("#myTreeSearchBtn").unbind("click").bind("click",function(){
	var searchTree = _viewer.navTree;
	var searchVal = $("#myTreeSearch").val();
	var selectDiv = $(".bbit-tree-body").find("div[title*='"+searchVal+"']").eq(0);
	if($(".bbit-tree-body").find("div[title*='"+searchVal+"']").length == 0){_viewer.refreshTreeAndGrid(_viewer.opts);}
	selectDiv.expandParentForTpath(searchTree);
});





