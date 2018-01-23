var _viewer = this;
var  height=jQuery(window).height()-200;
var  width=jQuery(window).width()-200;
var userCode = System.getVar("@USER_CODE@");//当前登录用户code
//var  params={};
//params["USER_CODE"]=userCode;debugger;
//FireFly.doAct("TS_WFS_BMSHLC","getUsers",params,true,false,function(data){});debugger;
//列表需要建一个code为buttons的自定义字段。
$("#TS_WFS_BMSHLC .rhGrid").find("tr").each(function(index,item){
	if(index !=0){
		var  dataId=item.id;
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_WFS_BMSHLC_edit" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_WFS_BMSHLC_delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
				)
				//为每个按钮绑定卡片
				bindCard();
	}
});
/*
* 删除前方法执行
*/
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
}
//绑定的事件     
function bindCard(){
	//编辑
	jQuery("td [id='TS_WFS_BMSHLC_edit']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		openMyCard(pkCode);
	});
	//当行删除事件
	jQuery("td [id='TS_WFS_BMSHLC_delete']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
}


//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
    temp[UIConst.PK_KEY] = dataId;
    if(readOnly != ""){
    	temp["readOnly"] = readOnly;
    }
    if(showTab != ""){
    	temp["showTab"] = showTab;
    }
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
}

var height = jQuery(window).height()-100;
var width = jQuery(window).width()-100;
_viewer.getBtn("add").unbind("click").bind("click",function(){
	var wfsId = _viewer.getParHandler().getItem("WFS_ID").getValue();
	var nodeId = _viewer.getParHandler().getItem("NODE_ID").getValue();
	//打开添加页面act：方法（必填），sId：服务（必填），parHandler：当前句柄，widHeiArray:小卡片的宽度高度，xyArray：左上角坐标
    var temp = {"act":UIConst.ACT_CARD_ADD,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[50,50]};
    temp["WFS_ID"] = wfsId;
    temp["NODE_ID"] = nodeId;
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
});
//从excel中导入人员
const IMPORT_FILE_ID = "TS_WFS_BMSHLC-impUserByExcel";
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
          
             data.NODE_ID = _viewer.getParHandler()._pkCode;
            data.code = userCode;
            data.FILE_ID = fileId;debugger;
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
window.open(FireFly.getContextPath() + '/ts/imp_template/报名或请假流程各节点审核人导入模板 .xls');
});