var _viewer = this;
//取消行点击事件
$(".rhGrid").find("tr").unbind("dblclick");
$("#TS_KCGL_UPDATE_MX .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var colValue = $(item).find("td[icode='MX_COL']").text();
		var data = $(item).find("td[icode='MX_DATA']").text();
		var data3 = $(item).find("td[icode='MX_DATA3']").text();
		var data4 = $(item).find("td[icode='MX_DATA4']").text();
		if(colValue == "KC_ODEPTCODE"){
			$(item).find("td[icode='ZDY']").text(data3);
		}else if(colValue == "KC_LEVEL"){
			$(item).find("td[icode='ZDY']").text(data4);
		}else{
			$(item).find("td[icode='ZDY']").text(data);
		}
	}
});

$("#TS_KCGL_UPDATE_MX .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
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
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
	    openMyCard(pkCode);
	});
};

rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

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









