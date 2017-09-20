var _viewer = this;

$("#TS_KCGL_IPSCOPE  .rhGrid").find("th[icode='del']").html("操作");
$("#TS_KCGL_IPSCOPE  .rhGrid").find("tr").unbind("dblclick");
//删除单行数据
_viewer.grid.getBtn("del").unbind("click").bind("click",function() {
	var pk = jQuery(this).attr("rowpk");//获取主键信息
	rowDelete(pk,_viewer);
});

/*
* 删除前方法执行
*/
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

$("#TS_KCGL_IPSCOPE .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='del']").prepend(
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
		);
		bindCard();
	}
});	

function bindCard(){
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		openMyCard(pkCode);
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