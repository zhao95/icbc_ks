var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");  
//列表需建一个code为BUTTONS的自定义字段
//每一行添加编辑和删除
$("#TS_KCGL_IPZWH .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon"  id="TS_KCGL_IPZWH_look" operCode="optLookBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_KCGL_IPZWH_edit" operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
//				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optDeleteBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
				);
		// 为每个按钮绑定卡片
		bindCard();
	}
});

//绑定的事件     
function bindCard(){
	//当行删除事件
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
		
	//当行编辑事件
	jQuery("td [id='TS_KCGL_IPZWH_edit']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowEdit(pkCode,_viewer,[1000,500],[200,100]);
	});
	//查看
	jQuery("td [id='TS_KCGL_IPZWH_look']").unbind("click").bind("click", function(){
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
/*
* 删除前方法执行
*/
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

//_viewer.getBtn("add").unbind("click").bind("click", function(event) {
//    var temp = {"act":UIConst.ACT_CARD_ADD,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[1000,600],"xyArray":[100,100]};
//    var cardView = new rh.vi.cardView(temp);
//    cardView.show();
//});
