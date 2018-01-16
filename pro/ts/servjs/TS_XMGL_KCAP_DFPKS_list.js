var _viewer = this;
var height = jQuery(window).height() - 200;
var width = jQuery(window).width() - 200;

//取消行点击事件
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
}

/*
 * 删除前方法执行
 */
_viewer.beforeDelete = function (pkArray) {
    showVerify(pkArray, _viewer);
};

/**
 * 系统管理员和项目创建人 显示添加和删除按钮
 */
var userCode = System.getVar("@USER_CODE@");
if (userCode != 'admin') {
    if (_viewer.links.XM_ID != 'undefined') {
        var data = {};
        data["_WHERE_"] = " and S_USER ='" + userCode + "'  and XM_ID = '" + _viewer.links.XM_ID + "'";

        FireFly.doAct("TS_XMGL", "count", data, false, true, function (result) {

            if (result._MSG_.indexOf("ERROR,") == 0) {
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
    file._obj.insertBefore($impFile);
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
                // data.XM_SZ_ID = xmSzId;
                // data.G_ID = _viewer.getParHandler()._pkCode;
                data.FILE_ID = fileId;
                data.XM_ID = _viewer.links.XM_ID;
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
$impFile.unbind('click').bind('click', function () {
    window.open(FireFly.getContextPath() + '/ts/imp_template/场次安排导入人员模板.xls');
});
