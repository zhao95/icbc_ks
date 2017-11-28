var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
//获得父级菜单的句柄
var  parent= _viewer.getParHandler();
$("#TS_XMGL_BMSH_KSQZ .rhGrid").find("tr").each(function(index,item) {
	if(index!=0 ){
		var val = $("input[name='TS_XMGL_BMSH-SH_ZDSH']:checked").val();
		if(val==2){
			$(item).find("td[icode='BUTTONS']").append(				
					'<a class="rhGrid-td-rowBtnObj rh-icon" style="display:none" operCode="optOption"><span class="rh-icon-inner">设置</span><span class="rh-icon-img btn-option"></span></a>'
			)
		}else{
			$(item).find("td[icode='BUTTONS']").append(				
					'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optOption"><span class="rh-icon-inner">设置</span><span class="rh-icon-img btn-option"></span></a>'
			)	
		}
		// 为每个按钮绑定卡片
		bindCard();
	}
});
function bindCard() {
	var height = jQuery(window).height()-120;
	var width = jQuery(window).width()-200;
	//设置
	jQuery("#TS_XMGL_BMSH_KSQZ td [operCode='optOption']").unbind("click").bind("click", function(){
		var KSQZ_ID=$(this).parent().parent().attr("id");
		//打开查看页面act：方法（必填），sId：服务（必填），parHandler：当前句柄，widHeiArray:小卡片的宽度高度，xyArray：左上角坐标
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_BMSH_KSQZ","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,65]};
		temp[UIConst.PK_KEY]=KSQZ_ID;//修改时，必填	    
		 var cardView = new rh.vi.cardView(temp);
		cardView.show(true);
	});
}
