_viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");

$("#TS_BMSH_RULE_GZK .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;		
		$(item).find("td[icode='BUTTONS']").append(
			'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optSetBtn1" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-option"></span></a>'+
			'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optDeleteBtn1" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
		);	
		// 为每个按钮绑定卡片
		bindCard();
	}
});
/*
* 删除前方法执行
*/
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
}
function bindCard(){
	//设置
	jQuery("td [operCode='optSetBtn1']").unbind("click").bind("click", function(){
		var height = jQuery(window).height();
		var width = jQuery(window).width()-150;
		//设置
			var pkCode = jQuery(this).attr("rowpk");
			var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_BMSH_RULE_GZK","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,0]};
			temp[UIConst.PK_KEY]=pkCode;//修改时，必填	    
			 var cardView = new rh.vi.cardView(temp);
			cardView.show(true);		
	});
	
	//删除
	jQuery("td [operCode='optDeleteBtn1']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
}