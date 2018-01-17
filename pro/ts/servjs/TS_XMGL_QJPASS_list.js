var _viewer = this; 
$("#TS_XMGL_QJPASS-expAll").unbind('click').bind('click', function () {
	//获得父级菜单的句柄
	var  parent= _viewer.getParHandler();
	var xmId=parent.opts.XM_ID;
    var data = { XM_ID: xmId};
    window.open(FireFly.getContextPath() + '/TS_XMGL_QJPASS.expAll.do?data=' +
        encodeURIComponent(JSON.stringify(data)));
   // FireFly.doAct("TS_XMGL_QJPASS", "expAll", data);
});


//从excel中导入人员
const IMPORT_FILE_ID = "TS_XMGL_QJPASS-impUserByExcel";
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
              var  parent= _viewer.getParHandler();
               data.XM_ID = parent.opts.XM_ID;
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
$importUser.attr('title', '导入文件为excel格式文件，请不要随意修改下载模板格式');

//导入模板下载
$impFile.unbind('click').bind('click', function () {
  window.open(FireFly.getContextPath() + '/ts/imp_template/项目请假申请导入模板.xls');
});




