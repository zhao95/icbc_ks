var _viewer = this;
var height = jQuery(window).height()-200;
var width = jQuery(window).width()-200;

$(".rhGrid").find("tr").unbind("dblclick"); 
$(".rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").append(
				 '<a class="rh-icon rhGrid-btnBar-a" operCode="optEditBtn"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				 '<a class="rh-icon rhGrid-btnBar-a" operCode="optDeleteBtn"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
				 );
		// 为每个按钮绑定卡片
		bindCard();
	}
});

/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

//绑定的事件     
function bindCard(){
	//当行删除事件
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = $(this).parent().parent().attr("id");
		rowDelete(pkCode,_viewer);
	});
	
	//当行编辑事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = $(this).parent().parent().attr("id");
		rowEdit(pkCode,_viewer,[width,height],[100,100]);
	});
}

//从excel中导入人员
//const IMPORT_FILE_ID = "TS_JKGL-impUserByExcel";
////避免刷新数据重复添加
//if (jQuery('#' + IMPORT_FILE_ID).length === 0) {
//    var config = {
//        "SERV_ID": _viewer.servId,
//        "TEXT": "导入",
//        "FILE_CAT": "",
//        "FILENUMBER": 1,
//        "BTN_IMAGE": "btn-imp",
//        // "VALUE": 15,
//        "TYPES": "*.xls;*.xlsx;",
//        "DESC": ""
//    };
//    var file = new rh.ui.File({
//        "id": IMPORT_FILE_ID,
//        "config": config
//    });
//    file._obj.insertBefore(jQuery('#' + _viewer.servId + '-delete'));
//    $("#" + file.time + "-upload span:first").css('padding', '0 7px 2px 20px');
//    jQuery('<span class="rh-icon-img btn-imp"></span>').appendTo($("#" + file.time + "-upload"));
//    file.initUpload();
//    file.afterQueueComplete = function (fileData) {// 这个上传队列完成之后
//        console.log("这个上传队列完成之后" + fileData);
//        for (var propertyName in fileData) {
//            var fileId = fileData[propertyName].FILE_ID;
//            if (fileId) {
//                var data = {};
//                // data.XM_SZ_ID = xmSzId;_listData._DATA_[0]
//             // data.NODE_ID = _viewer.getParHandler()._pkCode;
//              data.FILE_ID = fileId;
//                FireFly.doAct(_viewer.servId, "saveFromExcel", data, false, false, function (data) {
//                    rh.ui.File.prototype.downloadFile(data.FILE_ID, "test");
//                    _viewer.refresh();
//                    alert(data._MSG_);
//                });
//            }
//        }
//        file.clear();
//    };
//}
//var $importFile = $('#' + IMPORT_FILE_ID);
//$importFile.find('object').css('cursor', 'pointer');
//$importFile.find('object').css('z-index', '999999999');
//$importFile.find('object').css('width', '100%');
//$importFile.attr('title', '导入文件为excel格式文件，内容为单列数据，数据为人力资源编码、审核人、审核机构等');



//从excel中导入人员
const IMPORT_FILE_ID = "TS_JKGL-impUserByExcel";
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
            if (fileId) {
                var data = {};
                // data.XM_SZ_ID = xmSzId;
               // data.G_ID = _viewer.getParHandler()._pkCode;
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
$importUser.find('object').css('cursor', 'pointer');
$importUser.find('object').css('z-index', '999999999');
$importUser.find('object').css('width', '100%');
$importUser.attr('title', '导入文件为excel格式文件，内容为无标题的单列数据，一行数据为一个用户，数据为人力资源编码、统一认证号、身份证号');

//导入模板下载
$impFile.unbind('click').bind('click', function () {
    window.open(FireFly.getContextPath() + '/ts/imp_template/禁考管理导入模板.xls');
});