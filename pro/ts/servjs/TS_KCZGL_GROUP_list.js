var _viewer = this;
//取消行点击事件
$(".rhGrid").find("tr").unbind("dblclick");
//如果父页面是只读的，则隐藏编辑行按钮
if(_viewer.getParHandler().opts.readOnly || _viewer.getParHandler()._readOnly){
	$("#TS_KCZGL_GROUP-tmplBtn").hide();
}
//列表需建一个code为BUTTONS的自定义字段
//每一行添加编辑和删除
$("#TS_KCZGL_GROUP .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").append(
//				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optKcBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">考场管理</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optLookBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
//				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optDeleteBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
				);	
		// 为每个按钮绑定卡片
		bindCard();
	}
});

//绑定的事件     
function bindCard(){
	//当行查看事件
	jQuery("td [operCode='optLookBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//	    _viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",true);
	    openMyCard(pkCode,true);
	});
	
	//当行删除事件
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	//当行编辑事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode);
	    openMyCard(pkCode);
	});
		
	//考场
	jQuery("td [operCode='optKcBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCZGL_KCGL"});
		openMyCard(pkCode,"","TS_KCZGL_KCGL");
	});
}

//_viewer.getBtn("add").unbind("click").bind("click", function(event) {
//	
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

_viewer.getBtn("delete").unbind("click").bind("click",function() {
	
    var pkArray = _viewer.grid.getSelectPKCodes();
	if (jQuery.isArray(pkArray) && pkArray.length == 0) {
//		 _viewer.listBarTipError("请选择要删除的条目");
		_viewer.listBarTipError(Language.transStatic("rhListView_string8"));
	} else {
//		 var res = confirm("您确定要删除该数据么？");Language.transStatic("rhListView_string9")
		 var res = confirm("同时删除考场安排中引用的考场！");
		 if (res == true) {
//    		_viewer.listBarTipLoad("提交中...");
			 _viewer.listBarTipLoad(Language.transStatic("rhListView_string7"));
    		setTimeout(function() {
    			if(!_viewer.beforeDelete(pkArray)){
    				return false;
    			}
	    		var strs = pkArray.join(",");
	    		var temp = {};
	    		temp[UIConst.PK_KEY]=strs;
	    		var resultData = FireFly.listDelete(_viewer.opts.sId,temp,_viewer.getNowDom());
	    		_viewer._deletePageAllNum();
	    		_viewer.refreshGrid();
	    		_viewer.afterDelete();
    		},0);	
		 } else {
		 	return false;
		 }
	}
}); 

_viewer.getBtn("imp").unbind("click").bind("click",function() {
	var kczId = _viewer.getParHandler().getItem("KCZ_ID").getValue();
	
	var config = {"SERV_ID":_viewer.opts.sId, "FILE_CAT":"EXCEL_UPLOAD", "FILENUMBER":1, 
		"VALUE":5, "TYPES":"*.xls;*.xlsx", "DESC":"导入Excel文件"};
	var file = new rh.ui.File({
		"config" : config,"width":"99%"
	});
	
	var importWin = new rh.ui.popPrompt({
		title:"请选择文件",
		tip:"请选择要导入的Excel文件：",
		okFunc:function() {
			var fileData = file.getFileData();
			if (jQuery.isEmptyObject(fileData)) {
				Tip.showError("请选择文件上传", true);
				return;
			}
			var fileId = null;
			for (var key in fileData) {
				fileId = key;
			}
			if (fileId == null){
				Tip.showError("请选择文件上传", true);
				return;
			}
			
			var param = {};
			param["KCZ_ID"] = kczId;
			
			_viewer._imp(fileId,param);
			importWin.closePrompt();
	        // _viewer.refreshGrid();
			file.destroy();
		},
		closeFunc:function() {
			file.destroy();
		}
	});

    var container = _viewer._getImpContainer(event, importWin);
	container.append(file.obj);
	file.obj.css({'margin-left':'5px'});
	file.initUpload();
});

_viewer.getBtn("tmplBtn").unbind("click").bind("click",function(){
	window.open(FireFly.getContextPath() + '/ts/imp_template/考场组管理组导入模版.xls');
});
