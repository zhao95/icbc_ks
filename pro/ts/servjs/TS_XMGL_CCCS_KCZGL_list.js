var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
_viewer.getBtn("impKczgl").unbind("click").bind("click",function(event){
	//1.构造查询选择参数，其中参数【HTMLITEM】非必填，用以标识返回字段的值为html标签类的
	var configStr = "TS_KCZGL,{'TARGET':'','SOURCE':'KCZ_ID~KCZ_NAME~KCZ_CREATOR~KCZ_DATE'," +
			"'HIDE':'','TYPE':'multi','HTMLITEM':''}";
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
	    	var xmId = _viewer.getParHandler().getPKCode();
	    	var ids = idArray.KCZ_ID;
	    	if(xmId != "" && ids != ""){
	    		FireFly.doAct(_viewer.servId, "xmAddKcz", {"ids":ids,"xmId":xmId}, true,false,function(data){
		    		_viewer.refresh();
		    	});	
	    	}
		}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});

$("#TS_XMGL_CCCS_KCZGL .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;		
		$(item).find("td[icode='BUTTONS']").append(
			'<a class="rhGrid-td-rowBtnObj rh-icon" actcode="read" operCode="optLookBtn" rowpk="'+dataId+'" title="查看"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>'+	
			'<a class="rhGrid-td-rowBtnObj rh-icon" actcode="groupmgr" operCode="optZBtn" rowpk="'+dataId+'" title="组管理"><span class="rh-icon-inner">组管理</span><span class="rh-icon-img btn-edit"></span></a>'+
			'<a class="rhGrid-td-rowBtnObj rh-icon" actcode="upd" operCode="optEditBtn" rowpk="'+dataId+'" title="编辑"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
			'<a class="rhGrid-td-rowBtnObj rh-icon" actcode="delete" operCode="optDeleteBtn" rowpk="'+dataId+'" title="删除"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
		);	
		// 为每个按钮绑定卡片
		bindCard();
	}
});

function bindCard(){
	//当行查看事件
	jQuery("td [operCode='optLookBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
	    _viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",true);
	});
	//当行编辑事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode);
	});
	//当行删除事件
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	jQuery("td [operCode='optZBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCZGL_GROUP"});
	});
	
}

/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};