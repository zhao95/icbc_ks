var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
$("#TS_KCZGL .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;		
		var state = $(item).find("td[icode='KCZ_STATE']").attr("title");
		$(item).find("td[icode='BUTTONS']").append(
			'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optLookBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>'+	
			'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optZBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">组管理</span><span class="rh-icon-img btn-edit"></span></a>'+
			'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
			'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optDeleteBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'+
			'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optCopyBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">复制</span><span class="rh-icon-img btn-copy"></span></a>'
		);	
		
		if(state < 5){
			$(item).find("td[icode='BUTTONS']").append('<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optTrashBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">垃圾箱</span><span class="rh-icon-img btn-garbage"></span></a>');
		}else{
			$(item).find("td[icode='BUTTONS']").append('<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optBackTrashBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">撤销</span><span class="rh-icon-img btn-garbage"></span></a>');
		}	
		// 为每个按钮绑定卡片
		bindCard();
	}
});

function bindCard(){
	//当行查看事件
	jQuery("td [operCode='optLookBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//	    _viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",true);
		openMyCard(pkCode,true);
	});
	//当行编辑事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode);
		openMyCard(pkCode);
	});
	//当行删除事件
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	//复制
	jQuery("td [operCode='optCopyBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_UTIL","copy",{"servId":_viewer.servId,"pkCode":pkCode,"primaryColCode":"KCZ_ID"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
				window.location.reload();
			}
		});
	});
	//放入垃圾箱
	jQuery("td [operCode='optTrashBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkCode,"stateColCode":"KCZ_STATE","action":"add"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
//				_viewer.onRefreshGridAndTree();
				window.location.reload();
			}
		});
	});
	//从垃圾箱收回
	jQuery("td [operCode='optBackTrashBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkCode,"stateColCode":"KCZ_STATE","action":"del"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
				window.location.reload();
			}
		});
	});
	
	jQuery("td [operCode='optZBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCZGL_GROUP"});
	    openMyCard(pkCode,"","TS_KCZGL_GROUP");
	});
	
}

_viewer.getBtn("trash").unbind("click").bind("click", function(event) {
	var pkArray = _viewer.grid.getSelectPKCodes();
	if (pkArray.length == 0) {
		alert("请选择记录");
		return;
	}

	FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkArray.join(),"stateColCode":"KCZ_STATE","action":"add"},true,false,function(data){
		if(data._MSG_.indexOf("OK")!= -1){
			window.location.reload();
		}
	});
});

//_viewer.getBtn("add").unbind("click").bind("click", function(event) {
//	var height = jQuery(window).height()-200;
//	var width = jQuery(window).width()-200;
//
//    var temp = {"act":UIConst.ACT_CARD_ADD,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
//    var cardView = new rh.vi.cardView(temp);
//    cardView.show();
//});
/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

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