var _viewer=this;
//操作一栏
$(".rhGrid").find("tr").each(function(index,item){
	if(index !=0){
		$(item).find("td[icode='CAOZUO']").append(
				 '<a class="rhGrid-td-rowBtnObj rh-icon"  operCode="optSelect"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-view"></span></a>'+
				 '<a class="rhGrid-td-rowBtnObj rh-icon"  operCode="optEdit"><span class="rh-icon-inner">修改</span><span class="rh-icon-img btn-edit"></span></a>'+
				 '<a class="rhGrid-td-rowBtnObj rh-icon"  operCode="optDelete"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
		)
		btnClick();
	}
});
function btnClick(){
	//查看按钮
	jQuery("td [operCode='optSelect']").unbind("click").bind("click",function(){
		var CERT_ID=$(this).parent().parent().attr("id");
		//打开查看页面act：方法（必填），sId：服务（必填），parHandler：当前句柄，widHeiArray:小卡片的宽度高度，xyArray：左上角坐标
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"ETI_CERTQUAL_TS","parHandler":_viewer,"widHeiArray":[900,700],"xyArray":[200,50]};
		temp[UIConst.PK_KEY]=CERT_ID;//修改时，必填
	    temp[UIConst.readOnly]=true;
	    var cardView = new rh.vi.cardView(temp);
	  	cardView.show()
	});
	//修改按钮
	jQuery("td [operCode='optEdit']").unbind("click").bind("click",function(){	
		var CERT_ID=$(this).parent().parent().attr("id");
		//打开查看页面act：方法（必填），sId：服务（必填），parHandler：当前句柄，widHeiArray:小卡片的宽度高度，xyArray：左上角坐标
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"ETI_CERTQUAL_TS","parHandler":_viewer,"widHeiArray":[900,700],"xyArray":[200,50]};
		temp[UIConst.PK_KEY]=CERT_ID;//修改时，必填
	    var cardView = new rh.vi.cardView(temp);
	  	cardView.show()
	});
	//删除按钮
	jQuery("td [operCode='optDelete']").unbind("click").bind("click", function(){
		var CERT_ID=$(this).parent().parent().attr("id");
    	if (CERT_ID==0) {
    		_viewer.listBarTipError(Language.transStatic("rhListView_string8"));
    	} else { 		   	
    		 if (confirm(Language.transStatic("rhListView_string9"))) {
	    		_viewer.listBarTipLoad("提交中...");
    			 _viewer.listBarTipLoad(Language.transStatic("rhListView_string7"));
	    		setTimeout(function() {
	    			if(!_viewer.beforeDelete(CERT_ID)){
	    				return false;
	    			}
		    		var temp = {};
		    		temp[UIConst.PK_KEY]=CERT_ID;
		    		//alert(temp);
		    		FireFly.listDelete(_viewer.opts.sId,temp,_viewer.getNowDom());
		    		_viewer.refreshGrid();
		    		_viewer.afterDelete();
	    		},0);	
    		 } else {
    		 	return false;
    		 }
    	}
});
}









