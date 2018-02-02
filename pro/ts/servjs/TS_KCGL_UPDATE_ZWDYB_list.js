var _viewer = this;
var updateId = _viewer.getParHandler().getPKCode();
var kcId = _viewer.getParHandler().getItem("KC_ID").getValue();
_viewer.getBtn("impData").unbind("click").bind("click", function(event) {
	var param = {};
	param["SOURCE"] = "ZW_ZWH_XT~ZW_ZWH_SJ~ZW_IP~ZW_ID";
	param["TYPE"] = "multi";
	param["HIDE"] = "ZW_ID";
	param["EXTWHERE"] = "and kc_id = '"+kcId+"'";
	var configStr = "TS_KCGL_ZWDYB,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var zwIds = idArray.ZW_ID;
	    	var zwNums = zwIds.split(",").length;
	    	for(var i = 0;i < zwNums;i++){
	    		var zwId = zwIds.split(",")[i];
	    		FireFly.doAct("TS_KCGL_ZWDYB","byid",{"_PK_":zwId},true,false,function(data){
	    			var bean = {};
	    			bean["UPDATE_ID"] = updateId;
	    			bean["ZW_ACTION"] = "delete";//引入数据默认为删除
	    			bean["ZW_ZWH_XT"] = data.ZW_ZWH_XT;
	    			bean["ZW_ZWH_SJ"] = data.ZW_ZWH_SJ;
	    			bean["ZW_IP"] = data.ZW_IP;
	    			bean["ZW_DESC"] = data.ZW_DESC;
	    			bean["ROOT_ID"] = data.ZW_ID;
	    			FireFly.doAct("TS_KCGL_UPDATE_ZWDYB","save",bean);
	    		});
	    	}
	    	 setTimeout(function(){
	    		 _viewer.refreshGrid();
	         },100);
		}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);	 
});

$("#TS_KCGL_UPDATE_ZWDYB .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		
		var rootId = $(item).find("td[icode='ROOT_ID']").html();
		if(rootId == ""){
			$(item).find("select[icode='ZW_ACTION']").attr("disabled",true);
		}else{
			$(item).find("select[icode='ZW_ACTION']").find("option[value='add']").attr("disabled", "disabled"); 
		}
		
		$(item).find("td[icode='BUTTONS']").append(
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
	//当行删除事件
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	//当行编辑事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
	    openMyCard(pkCode);
	});
	
	//当行查看事件
	jQuery("td [operCode='optLookBtn']").unbind("click").bind("click", function(){;
		var pkCode = jQuery(this).attr("rowpk");
		openMyCard(pkCode,true);
	});
};

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

if (_viewer.getParHandler().getParHandler().getParHandler() != undefined
		&& _viewer.getParHandler().getParHandler().getParHandler() != null) {
	var parparServId = _viewer.getParHandler().getParHandler().getParHandler().servId;
	if (parparServId == "TS_KCGL_SH") {
		$("#TS_KCGL_UPDATE_ZWDYB-add").hide();
		$("#TS_KCGL_UPDATE_ZWDYB-impData").hide();
		$("#TS_KCGL_UPDATE_ZWDYB-delete").hide();
		$("#TS_KCGL_UPDATE_ZWDYB").find("a[opercode='optEditBtn']").hide();
		$("#TS_KCGL_UPDATE_ZWDYB-tmplBtn").hide();
		$("#TS_KCGL_UPDATE_ZWDYB-imp").hide();
	}
}


_viewer.getBtn("tmplBtn").unbind("click").bind("click",function(){
	window.open(FireFly.getContextPath() + '/ts/imp_template/考场管理-变更审核-座位对应表导入模版.xls');
});

_viewer.getBtn("imp").unbind("click").bind("click",function() {
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
			param["updateId"] = updateId;
			
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



