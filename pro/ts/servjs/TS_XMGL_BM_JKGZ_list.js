var _viewer = this;

var xmId = null;

if(_viewer.getParHandler()) {
	xmId = _viewer.getParHandler().getItem("XM_ID").getValue();
}

if(!xmId){
	alert("项目ID为空，操作无效!");
}

$(".rhGrid").find("tr").unbind("dblclick");
$("#TS_XMGL_BM_JKGZ .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;		
		$(item).find("td[icode='BUTTONS']").append(
			'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optSetBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">设置</span><span class="rh-icon-img btn-option"></span></a>'+
			'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optDeleteBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
		);	
		// 为每个按钮绑定卡片
		bindCard();
	}
});

function bindCard(){
	var height = jQuery(window).height()-115;
	var width = jQuery(window).width()-200;
	//设置
	jQuery("td [operCode='optSetBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		/*_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"ts_xmgl_bm_jkgz",false);*/
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"ts_xmgl_bm_jkgz","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,60]};
		temp[UIConst.PK_KEY]=pkCode;//修改时，必填	    
		 var cardView = new rh.vi.cardView(temp);
		cardView.show(true);	
	});
	
	//删除
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
}
_viewer.getBtn("add").unbind("click").bind("click", function(event) {
	//1.构造查询选择参数，其中参数【HTMLITEM】非必填，用以标识返回字段的值为html标签类的
	var configStr = "ts_xmgl_bm_jkglgzk,{'TARGET':'','SOURCE':'GZ_ID~GZ_TYPE~GZ_NAME~GZ_INFO'," +
			"'HIDE':'','TYPE':'multi','HTMLITEM':''}";
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
	    	var ids = idArray.GZ_ID;
	    	FireFly.doAct(_viewer.servId, "impShgz", {"ids":ids,"xmId":xmId}, true,false,function(data){
	    		_viewer.refresh();
	    	});	
		}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event,[],[0,495]);
});