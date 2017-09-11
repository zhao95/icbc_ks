var _viewer = this;
//取消行点击事件
$(".rhGrid").find("tr").unbind("dblclick");
//列表需建一个code为BUTTONS的自定义字段
//每一行添加编辑和删除
$("#TS_KCGL .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		var state = $(item).find("td[icode='KC_STATE']").attr("title");
		
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optLookBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optIPScopeBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">考场IP段设置</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optIPZwhBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">考场IP座位号</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optSeatBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">系统对应座位号</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optJgBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">关联机构</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optCopyBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">复制</span><span class="rh-icon-img btn-copy"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optDeleteBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
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

//绑定的事件     
function bindCard(){
	//当行查看事件
	jQuery("td [operCode='optLookBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
	    _viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",true);
	});
	
	//当行删除事件
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	//复制
	jQuery("td [operCode='optCopyBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_UTIL","copy",{"servId":_viewer.servId,"pkCode":pkCode,"primaryColCode":"KC_ID"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
				window.location.reload();
			}
		});
	});
	
	//当行编辑事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode);
	});
	
	//放入垃圾箱
	jQuery("td [operCode='optTrashBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkCode,"stateColCode":"KC_STATE","action":"add"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
//				_viewer.onRefreshGridAndTree();
				window.location.reload();
			}
		});
	});
	//从垃圾箱收回
	jQuery("td [operCode='optBackTrashBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkCode,"stateColCode":"KC_STATE","action":"del"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
				window.location.reload();
			}
		});
	});
	
	jQuery("td [operCode='optSeatBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_ZWDYB"});
	});
	
	jQuery("td [operCode='optJgBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_GLJG"});
	});
	
	jQuery("td [operCode='optIPScopeBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_IPSCOPE"});
	});
	jQuery("td [operCode='optIPZwhBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_IPZWH"});
	});
	
}

_viewer.getBtn("trash").unbind("click").bind("click", function(event) {
	var pkArray = _viewer.grid.getSelectPKCodes();
	if (pkArray.length == 0) {
		alert("请选择记录");
		return;
	}

	FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkArray.join(),"stateColCode":"KC_STATE","action":"add"},true,false,function(data){
		if(data._MSG_.indexOf("OK")!= -1){
			window.location.reload();
		}
	});
});

/*
* 删除前方法执行
*/
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
	
	module = 'EXAM_ROOM';
	
	var params = {"isHide":"true", "CTLG_MODULE":module};
	
	var options = {"tTitle":"考场目录管理","url":"TS_COMM_CATALOG.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3};
	Tab.open(options);

});