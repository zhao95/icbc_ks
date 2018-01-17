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

//从excel中导入人员
const IMPORT_FILE_ID = _viewer.servId + "-impUser";
var $importUser = $('#' + IMPORT_FILE_ID);
//避免刷新数据重复添加
var $impFile = jQuery('#' + _viewer.servId + '-impFile');
if ($importUser.length === 0) {
    var config = {
        "SERV_ID": _viewer.servId,
        "TEXT": "导入",
        "FILE_CAT": "",
        "FILENUMBER": 1,
        "BTN_IMAGE": "btn-imp",
        // "VALUE": 15,
        "TYPES": "*.xls;*.xlsx;",
        "DESC": ""
    };
    var file = new rh.ui.File({
        "id": IMPORT_FILE_ID,
        "config": config
    });
    file._obj.insertBefore(_viewer.getBtn("tmplBtn"));
    $("#" + file.time + "-upload span:first").css('padding', '0 7px 2px 20px');
    jQuery('<span class="rh-icon-img btn-imp"></span>').appendTo($("#" + file.time + "-upload"));
    file.initUpload();
    file.afterQueueComplete = function (fileData) {// 这个上传队列完成之后
        console.log("这个上传队列完成之后" + fileData);
        for (var propertyName in fileData) {
            var fileId = fileData[propertyName].FILE_ID;
            debugger;
            if (fileId) {
                var data = {};
                data.FILE_ID = fileId;
                data.XM_ID = xmId;
                FireFly.doAct(_viewer.servId, "saveFromExcel", data, false, false, function (data) {
                    rh.ui.File.prototype.downloadFile(data.FILE_ID, "test");
                    _viewer.refresh();
                    alert(data._MSG_);
                });
            }
        }
        file.clear();
    };
}
$importUser.find('object').css('cursor', 'pointer');
$importUser.find('object').css('z-index', '999999999');
$importUser.find('object').css('width', '100%');

//导入模板下载
_viewer.getBtn("tmplBtn").unbind("click").bind("click", function () {
    window.open(FireFly.getContextPath() + '/ts/imp_template/项目管理_待安排考生_导入模版.xls');
});
_viewer.getBtn("imp").remove();

//导出
_viewer.getBtn("exp").unbind("click").bind("click", function () {
    var data = {XM_ID: xmId};
    window.open(FireFly.getContextPath() + '/' + _viewer.servId + '.expAll.do?data=' +
        encodeURIComponent(JSON.stringify(data)));
});
// _viewer.getBtn("imp").unbind("click").bind("click", function () {
//     var xmId = _viewer.getParHandler().getItem("XM_ID").getValue();
//     var config = {
//         "SERV_ID": _viewer.opts.sId, "FILE_CAT": "EXCEL_UPLOAD", "FILENUMBER": 1,
//         "VALUE": 5, "TYPES": "*.xls;*.xlsx", "DESC": "导入Excel文件"
//     };
//     var file = new rh.ui.File({
//         "config": config, "width": "99%"
//     });
//
//     var importWin = new rh.ui.popPrompt({
//         title: "请选择文件",
//         tip: "请选择要导入的Excel文件：",
//         okFunc: function () {
//             var fileData = file.getFileData();
//             if (jQuery.isEmptyObject(fileData)) {
//                 alert("请选择文件上传");
//                 return;
//             }
//             var fileId = null;
//             for (var key in fileData) {
//                 fileId = key;
//             }
//             if (fileId === null) {
//                 alert("请选择文件上传");
//                 return;
//             }
//
//             var param = {};
//             param["XM_ID"] = xmId;
//
//             _viewer._imp(fileId, param);
//             importWin.closePrompt();
//             // _viewer.refreshGrid();
//             file.destroy();
//         },
//         closeFunc: function () {
//             file.destroy();
//         }
//     });
//
//     var container = _viewer._getImpContainer(event, importWin);
//     container.append(file.obj);
//     file.obj.css({'margin-left': '5px'});
//     file.initUpload();
// });


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






