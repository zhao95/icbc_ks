var _viewer = this;

$(".rhGrid").find("tr").unbind("dblclick");

$("#TS_XMGL_BM_KSQZ .rhGrid").find("tr").each(function(index,item) {
	if(index!=0 ){
			$(item).find("td[icode='BUTTONS']").append(				
					'<a class="rhGrid-td-rowBtnObj rh-icon"  operCode="optOption"><span class="rh-icon-inner">设置</span><span class="rh-icon-img btn-option"></span></a>'
			)
		// 为每个按钮绑定卡片
		bindCard();
	}
});
function bindCard(){
	var height = jQuery(window).height();
	var width = jQuery(window).width();
	//设置
	jQuery("td [operCode='optOption']").unbind("click").bind("click", function(){
		var KSQZ_ID=$(this).parent().parent().attr("id");
		//打开查看页面act：方法（必填），sId：服务（必填），parHandler：当前句柄，widHeiArray:小卡片的宽度高度，xyArray：左上角坐标
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_BM_KSQZ","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,0]};
		temp[UIConst.PK_KEY]=KSQZ_ID;//修改时，必填	    
		 var cardView = new rh.vi.cardView(temp);
		cardView.show(true);		
	});
}

_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
}