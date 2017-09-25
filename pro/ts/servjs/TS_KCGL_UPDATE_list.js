var _viewer = this;

var parHandlerServ = _viewer.getParHandler().servId;
var parHandlerPk = _viewer.getParHandler().getPKCode();

if(parHandlerServ == "TS_KCGL"){
	var add = '<a class="rh-icon rhGrid-btnBar-a" id="TS_KCGL_UPDATE-add" actcode="add" title=""><span class="rh-icon-inner"> 添 加 </span><span class="rh-icon-img btn-add"></span></a>';
	var del = '<a class="rh-icon rhGrid-btnBar-a" id="TS_KCGL_UPDATE-delete" actcode="delete" title=""><span class="rh-icon-inner"> 删 除 </span><span class="rh-icon-img btn-delete"></span></a>';
	
	var addObj = $("#TS_KCGL_UPDATE .rhGrid-btnBar").find("a[actcode='add']");
	var delObj = $("#TS_KCGL_UPDATE .rhGrid-btnBar").find("a[actcode='delete']");
	if(addObj.length == 0){
		$("#TS_KCGL_UPDATE .rhGrid-btnBar").append(add);
	}
	if(delObj.length == 0){
		$("#TS_KCGL_UPDATE .rhGrid-btnBar").append(del);
	}
}

var height = jQuery(window).height()-200;
var width = jQuery(window).width()-200;
$("#TS_KCGL_UPDATE-add").unbind("click").bind("click",function(){
	var temp = {
		"act" : UIConst.ACT_CARD_ADD,
		"sId" : "TS_KCGL_UPDATE",
		"parHandler" : _viewer,
		"widHeiArray" : [ width, height ],
		"xyArray" : [ 100, 100 ]
	};
	var cardView = new rh.vi.cardView(temp);
	cardView.show();
});

$("#TS_KCGL_UPDATE-delete").unbind("click").bind("click",function(){
	var pkCodes = _viewer.grid.getSelectPKCodes();//获取主键值
	var strs = pkCodes.join(",");
	var temp = {};
	temp[UIConst.PK_KEY]=strs;
	var resultData = FireFly.listDelete("TS_KCGL_UPDATE",temp,true);
	_viewer.refresh();
});

$("#TS_KCGL_UPDATE .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		var commit = $(item).find("td[icode='KC_COMMIT']").text();
		var agree = $(item).find("td[icode='UPDATE_AGREE']").text();
		if(commit == '1' && agree == '0'){
			$(item).css("color","blue");
		}
		
//		$(item).find("td[icode='view']").append(
//			'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optDeleteBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
//		);
		bindCard();
	}
});
function bindCard(){
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
}
$("#TS_KCGL_UPDATE .rhGrid").find("th[icode='view']").html("操作");

_viewer.grid.getBtn("view").unbind("click").bind("click",function() {
	var pkCode = jQuery(this).attr("rowpk");//获取主键信息
	openMyCard(pkCode);
});

//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var height = jQuery(window).height()-200;
	var width = jQuery(window).width()-200;
	
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
/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};