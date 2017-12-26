var _viewer = this
//从excel中导入人员
const IMPORT_FILE_ID = "TS_BMSH_ADMIT-impUserByExcel";
//避免刷新数据重复添加
if (jQuery('#' + IMPORT_FILE_ID).length === 0) {
    var config = {
        "SERV_ID": _viewer.servId,
        "TEXT": "导入用户",
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
    file._obj.insertBefore(jQuery('#' + _viewer.servId + '-delete'));
    $("#" + file.time + "-upload span:first").css('padding', '0 7px 2px 20px');
    jQuery('<span class="rh-icon-img btn-imp"></span>').appendTo($("#" + file.time + "-upload"));
    file.initUpload();
    file.afterQueueComplete = function (fileData) {// 这个上传队列完成之后
        console.log("这个上传队列完成之后" + fileData);
        for (var propertyName in fileData) {
            var fileId = fileData[propertyName].FILE_ID;
            if (fileId) {
                var data = {};
                // data.XM_SZ_ID = xmSzId;
                data.FILE_ID = fileId;
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
var $importFile = $('#' + IMPORT_FILE_ID);
$importFile.find('object').css('cursor', 'pointer');
$importFile.find('object').css('z-index', '999999999');
$importFile.find('object').css('width', '100%');
$importFile.attr('title', '导入文件为excel格式文件，内容为无标题的单列数据，一行数据为一个用户，数据为人力资源编码、统一认证号、身份证号');