/** 服务卡片使用的js方法定义：开始 */
var _viewer = this;

_viewer.backA.hide();

/**
 * 点击保存按钮
 */
_viewer.getBtn('saveWf').bind("click",function() {
	saveProcDef("saveWf");
	_parent.window.scrollTo(0,0); //进入卡片，外层页面滚动到顶部
});


/**
 * 点击“另存为新版本”按钮
 */
_viewer.getBtn('saveWfAsNewVersion').bind("click",function() {
	if(!_viewer.byIdData._PK_){
		saveProcDef("saveWf");
	}else{
		saveProcDef("saveWfAsNewVersion");
	}
	_parent.window.scrollTo(0,0); //进入卡片，外层页面滚动到顶部
});


/**
 * 提交流程
 * @returns
 */
function saveProcDef(act){
	
	if(!_viewer.form.validate()) {
		_viewer.cardBarTipError("校验未通过");
    	return false;
    }
	
	var data = _viewer.form.getItemsValues();
	var wfIframe = frames[_viewer.opts.sId + "-_WF_IFRAME"];
	if(wfIframe != undefined){
		if(wfIframe.contentWindow) {
			wfIframe = wfIframe.contentWindow;
		}
		//取得定义页面上的
		var wfResStr = wfIframe.showWorkflowDefXml();
	    data["xmlStr"] = wfResStr;
	}
	
	data["PROC_TYPE"] = "1";
	
	var result = rh_processData(_viewer.opts.sId + "." + act + ".do",data);
	if (result[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
		Tip.show("保存成功");
		
		_viewer._parentRefreshFlag = true;
		_viewer._pkCode = result["_DATA_"]["PROC_CODE"];
		_viewer._actVar = UIConst.ACT_CARD_MODIFY;
		
		_viewer.getItem("PROC_CODE").setValue(result["_DATA_"]["PROC_CODE"]);
		
		//“另存为新版本”时，刷新页面
		if(result["_DATA_"]["PROC_VERSION"]){
			_viewer.refresh();
		}
	}
	
	return result;
}


var _oldProcDefFrameHeight = 0;

/**
 * 点击放大按钮
 */
_viewer.getBtn("fullScreen").unbind("click").bind("click",function() {
	var iframeElem = jQuery("#SY_WFE_PROC_DEF-_WF_IFRAME");
	if(iframeElem.hasClass("pa")){
		Menu.expandLeftMenu();
		//缩小
		iframeElem.removeClass();
		_viewer.getBtn("fullScreen").find("span.rh-icon-inner").text("放大");
		//iframeElem.resizable();
		iframeElem.height(_oldProcDefFrameHeight);
	}else{
		_oldProcDefFrameHeight = iframeElem.height();
		//放大
		Menu.closeLeftMenu();
		var pageHeight = jQuery(document).height();
		var siblings = iframeElem.siblings();
		var pos = { top: 140, left: 10 };
		if(siblings.length > 0){
			pos = siblings.offset();
		}
		iframeElem.addClass("pa ");
		iframeElem.offset(pos);
		iframeElem.height(pageHeight - 120);
		_viewer.getBtn("fullScreen").find("span.rh-icon-inner").text("缩小");
	}
});
//增加卡片关闭时的扩展销毁
this.extendDestroy =  function() {
	return false;
};

_viewer.displayDetails = function(url, datas, title) {
	var dialogElement = jQuery("#wfe-details-dialog");
	if(dialogElement.length > 0) {
		dialogElement.remove();
	}

	var htmls = new Array();
	htmls.push("<div id='wfe-details-dialog' class='details-dialog ui-widget'>");
	htmls.push("<iframe class='wfe-details-iframe' src=''>");
	htmls.push("</iframe></div>");

	dialogElement = jQuery(htmls.join(""));
	dialogElement.appendTo(jQuery("body"));
	dialogElement.dialog({
		height: 520,
	    width: 1020,
	    modal: true,
	    title: title,
	    position: {my:"top",at:"top",of:"#SY_WFE_PROC_DEF-_WF_IFRAME_div"},
		open: function( event, ui ) {
			dialogElement.prev().css("display", "block");
			var iframeObj = dialogElement.find(".wfe-details-iframe");
			if(iframeObj.length > 0) {
				iframeObj.attr("src", _appendRhusSession(url));
				//parent.$(parent.document).find(".wfe-details-iframe").data()
				iframeObj.data("dataObj", datas);
				iframeObj.data("dialogActs", {
					"saveNode": function(rtnObj) { // 保存节点
						var procDefIframes = jQuery("#SY_WFE_PROC_DEF-_WF_IFRAME");
						if(procDefIframes.length > 0) {
							var iframeEle = procDefIframes[0];
							var iframeWin;
							if(iframeEle.contentWindow) {
								iframeWin = iframeEle.contentWindow;
							} else {
								iframeWin = iframeEle;
							}
							if(typeof(rtnObj) == "object") {
								if(rtnObj.NODE_NAME) {
									iframeWin.saveNodeInfo(rtnObj, rtnObj.NODE_NAME);
								} else {
									iframeWin.saveNodeInfo(rtnObj, rtnObj.LINE_COND_NAME);
								}
							}
						}
						dialogElement.dialog("close");
						dialogElement.remove();
					},
					"close" : function() {  // 关闭
						dialogElement.dialog("close");
						dialogElement.remove();
					}
				});
			}
		}
	});
}