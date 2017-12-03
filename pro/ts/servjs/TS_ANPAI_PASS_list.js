_viewer = this;
$(".rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		console.log(item);
		var XM_ID = $(item).find("td[icode='XM_ID']").html();
		$(item).find("td[icode='buttons']").append('<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optSetBtn" rowpk="'+XM_ID+'"><span class="rh-icon-inner">设置</span><span class="rh-icon-img btn-option"></span></a>'); 
		bindCard();
	}
});

function bindCard(){
	jQuery("a[operCode='optSetBtn']").unbind("click").bind("click",function() {
		var pkCode = jQuery(this).attr("rowpk");
		var height = jQuery(window).height()-80;
		var width = jQuery(window).width()-200;
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_KCAP","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
		temp[UIConst.PK_KEY]=pkCode;//修改时，必填	    
		var cardView = new rh.vi.cardView(temp);
		cardView.show();
	});		
}