var _viewer = this;
$("#TS_KCGL_GLJG .rhGrid").find("tr").unbind("dblclick"); 
$("#TS_KCGL_GLJG .rhGrid").find("th[icode='del']").html("操作");

//删除单行数据
//_viewer.grid.getBtn("del").unbind("click").bind("click",function() {
//	var pk = jQuery(this).attr("rowpk");//获取主键信息
//	rowDelete(pk,_viewer);
//});

/*
* 删除前方法执行
*/
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

$("#TS_KCGL_GLJG .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").prepend(
				'<a class="rhGrid-td-rowBtnObj rh-icon"  id="TS_KCGL_GLJG_look" operCode="optLookBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_KCGL_GLJG_edit"  operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
		);
		bindCard(dataId);
	}
});	
function bindCard(dataId){
	jQuery("td [id='TS_KCGL_GLJG_edit']").unbind("click").bind("click", function(){
		var pkCode = $(this).attr("rowpk");
		openMyCard(pkCode);
	});
	
	 //查看
	jQuery("td [id='TS_KCGL_GLJG_look']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		//$(".hoverDiv").css('display','none');
		openMyCard(pkCode,true);
	 });
}

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

//如果父页面是只读的，则隐藏编辑行按钮
if(_viewer.getParHandler().opts.readOnly || _viewer.getParHandler()._readOnly){
	$("a#TS_KCGL_GLJG_edit").hide();
	_viewer.getBtn("far").hide();
	_viewer.getBtn("near").hide();
}

//1远2近
_viewer.getBtn("far").unbind("click").bind("click",function() {
	var pkCodes = _viewer.grid.getSelectPKCodes();//获取主键值
	for(var i=0;i<pkCodes.length;i++){
		var dataId = pkCodes[i];
		var param = {};
		param["JG_FAR"] = 1;
		param["_PK_"] = dataId;
		FireFly.doAct("TS_KCGL_GLJG","save",param);
	}
	if(pkCodes.length > 0){
		_viewer.refreshGrid();
	}
});

_viewer.getBtn("near").unbind("click").bind("click",function() {
	var pkCodes = _viewer.grid.getSelectPKCodes();//获取主键值
	for(var i=0;i<pkCodes.length;i++){
		var dataId = pkCodes[i];
		var param = {};
		param["JG_FAR"] = 2;
		param["_PK_"] = dataId;
		FireFly.doAct("TS_KCGL_GLJG","save",param);
	}
	if(pkCodes.length > 0){
		_viewer.refreshGrid();
	}
});

