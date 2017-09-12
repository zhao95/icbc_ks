var _viewer = this;
//$(".rhGrid").find("tr").unbind("dblclick");
//每一行添加编辑和删除
$("#TS_KCGL_SH .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		var state = $(item).find("td[icode='KC_STATE']").attr("title");
		
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optLookBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">审核</span><span class="rh-icon-img btn-edit"></span></a>'
				);
		// 为每个按钮绑定卡片
		bindCard();
	}
});

function bindCard(){
	//当行查看事件
	jQuery("td [operCode='optLookBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode);
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[1000,600],"xyArray":[100,50]};
	    temp[UIConst.PK_KEY] = pkCode;
	    var cardView = new rh.vi.cardView(temp);
	    cardView.show();
	});
}

_viewer.getBtn("add").unbind("click").bind("click", function(event) {
    var temp = {"act":UIConst.ACT_CARD_ADD,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[1000,600],"xyArray":[100,100]};
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
});
/*
* 删除前方法执行
*/
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};