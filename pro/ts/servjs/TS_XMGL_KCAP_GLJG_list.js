var _viewer = this;
$("#TS_XMGL_KCAP_GLJG .rhGrid").find("tr").unbind("dblclick"); 
$("#TS_XMGL_KCAP_GLJG .rhGrid").find("th[icode='del']").html("操作");

/*
* 删除前方法执行
*/
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

$("#TS_XMGL_KCAP_GLJG .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").prepend(
				'<a class="rhGrid-td-rowBtnObj rh-icon"  id="TS_XMGL_KCAP_GLJG_look" operCode="optLookBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_KCAP_GLJG_edit"  operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
		);
		bindCard(dataId);
	}
});	
function bindCard(dataId){
	jQuery("td [id='TS_XMGL_KCAP_GLJG_edit']").unbind("click").bind("click", function(){
		var pkCode = $(this).attr("rowpk");
		openMyCard(pkCode);
	});
	
	 //查看
	jQuery("td [id='TS_XMGL_KCAP_GLJG_look']").unbind("click").bind("click", function(){
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
