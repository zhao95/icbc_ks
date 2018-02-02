var _viewer = this
var height = jQuery(window).height()-50;
var width = jQuery(window).width()-100;
////传给后台的数据
///*
//* 业务可覆盖此方法，在导航树的点击事件加载前
//*/
rh.vi.listView.prototype.beforeTreeNodeClickLoad = function(item,id,dictId) {
	var params = {};
	var user_pvlg=_viewer._userPvlg[_viewer.servId+"_PVLG"];
	params["USER_PVLG"] = user_pvlg;
	_viewer.whereData["extParams"] = params;
	 var flag = getListPvlg(item,user_pvlg,"CODE_PATH");
	_viewer.listClearTipLoad();
	return flag;
};
//重写add方法
_viewer.getBtn("add").unbind("click").bind("click",function() {
	var pcodeh = _viewer._transferData["CTLG_PCODE"];
		if(pcodeh == "" || typeof(pcodeh) == "undefined") {
		alert("请选择添加目录的层级 !");
		return false;
	}
	var temp = {"act":UIConst.ACT_CARD_ADD,
			"sId":_viewer.servId,
			"params":  {
				//"CTLG_MODULE" : module,
			},
			"transferData": _viewer._transferData,
			"links":_viewer.links,
			"parHandler":_viewer,
			"widHeiArray":[width,height],
			"xyArray":[50,50]
	};
	console.log(temp);
	var cardView = new rh.vi.cardView(temp);
	
	cardView.show();
	
});
/*
* 删除前方法执行
*/
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
}
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
        	 var filesize = fileData[propertyName].FILE_SIZE;
       	  if(filesize>1024*1024*20){
       		  alert("文件超过20M");
       		  file.clear();
       		  return false;
       	  }
            var fileId = fileData[propertyName].FILE_ID;
            if (fileId) {
                var data = {};
                // data.XM_SZ_ID = xmSzId;
                data.FILE_ID = fileId;
                data.XM_ID =  _viewer.getParHandler().getItem("XM_ID").getValue();
                FireFly.doAct(_viewer.servId, "saveFromExcel", data, false, false, function (data) {
                    rh.ui.File.prototype.downloadFile(data.FILE_ID, "test");
                    alert(data.mess);
                    _viewer.refreshGrid();
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
$importFile.attr('title', '导入文件为excel格式文件');


//导入模板下载
var $impFile = jQuery('#' + _viewer.servId + '-impFile');
$impFile.unbind('click').bind('click', function () {
  window.open(FireFly.getContextPath() + '/ts/imp_template/报名准入测试导入模板.xls');
});