var _viewer = this;

//去掉行单击事件
_viewer.grid.unbindTrdblClick();
_viewer._btnBar.hide();
_viewer.grid._page.hide();

//隐藏已经办结的记录对应的radio
jQuery("TBODY > TR").each(
	function(i,item){
		var itemObj = jQuery(item);
		var runningStat = _viewer.grid.getRowItemValue(item.id,"NODE_IF_RUNNING");
		if(runningStat  != "1"){ //节点实例还是活动节点（未结束）
			itemObj.find("input.rowIndex").remove();
		}
	}
);

//如果不是管理员，则去掉管理员才能操作的按钮
if(!_viewer._listData || !_viewer._listData._IS_WF_MANAGER || _viewer._listData._IS_WF_MANAGER != "1"){
	_viewer.getBtn("wfToNext").hide();
	_viewer.getBtn("stopNodeInst").hide();
	_viewer.getBtn("cuiban").hide();
	_viewer.grid.hideCheckBoxColum();
}

//ICBC：隐藏管理员功能
if (window.ICBC) {
	_viewer.getBtn("wfToNext").hide();
	_viewer.getBtn("stopNodeInst").hide();
	_viewer.getBtn("cuiban").hide();
	_viewer.grid.hideCheckBoxColum();
}

//隐藏高级查询
_viewer.hideSearchItem();
if (_viewer.getAdvancedSearchBtn()) {
	_viewer.getAdvancedSearchBtn().hide();
}

/**
 *是否选中radioBox 
 */
function _ifCheckRadio(){
	//判断是否选中radio
	if(jQuery("input.rowIndex:checked").length == 0){
		alert("请选择流程实例。");
		return;
	}
	return true;
}

//送下一个节点按钮相关操作
if (_viewer.getBtn("wfToNext")) {
_viewer.getBtn("wfToNext").unbind("click").bind("click", function(event){

	if(!_ifCheckRadio()){
		return;
	}
	//弹出对话框
	var temp = new rh.ui.popPrompt({
		title:"送交指定办理人",
		tip:"请选择需要送交的办理节点和办理人。",
		okFunc:function() {
			if(jQuery('#wfToNext_NODE_CODE').val().length==0 
				|| jQuery("#wfToNext_DONE_USER_ID").val().length == 0){
				alert("请选择用户或节点。");
				return;
			}
			
			var param = {};
			param["NI_ID"] = jQuery('#wfToNext_NI_ID').val();
			param["NODE_CODE"] = jQuery('#wfToNext_NODE_CODE').val();
			param["DONE_USER_ID"] = jQuery("#wfToNext_DONE_USER_ID").val();
			FireFly.doAct(_viewer.servId, "wfToNext", param, true);
			temp.closePrompt();
			_viewer.refresh();
		}
	});
	
	var niIDs = _viewer.grid.getSelectPKCodes();
	
	
	var param = {};
	param["NI_ID"] = niIDs[0];
	//param["SERV_ID"] = _viewer.getByIdData("SERV_ID");
	var nodeDefList = FireFly.doAct(_viewer.servId, "reteieveNodeDefList", param, false)._DATA_;
	
	temp._layout(event,undefined,[500,300]);
	var tbl = "<table border='0' class='wp100 mt20'>"
			+ "<tr><td class='wp20 h25 tr p5'>节点名称</td>"
			+ "<td><select id='wfToNext_NODE_CODE' name='NODE_CODE'></select></td>"
			+ "</tr><tr>"
			+ "<td class='wp20 h25 tr pr10'>用户</td>"
			+ "<td><input type='hidden'id='wfToNext_DONE_USER_ID' name='DONE_USER_ID'>"
			+ "<input id='wfToNext_DONE_USER_ID__NAME' name='DONE_USER_NAME' class='wp80'>"
			+ "<span style='text-decoration:underline' class='cp' id='wfToNext_USER_SELECT'>选择</span></td>"
			+ "<input type='hidden' id='wfToNext_NI_ID' name='NI_ID' value='" + param["NI_ID"] + "' >"
			+ "</tr>"
			+ "</table>";
	temp.display(tbl);
	var nodeIDSelect = jQuery("#wfToNext_NODE_CODE");
	nodeIDSelect.append("<option value=''></option>");
	for(var i=0;i<nodeDefList.length;i++){
		var nodeDef = nodeDefList[i];
		nodeIDSelect.append("<option value='" + nodeDef.NODE_CODE + "'>" + nodeDef.NODE_NAME + "(" + nodeDef.NODE_CODE + ")</option>");
	}
	
	//显示用户选择对话框
	jQuery('#wfToNext_USER_SELECT').bind("click",function(){
		var options = {
			"itemCode" : "wfToNext_DONE_USER_ID",
			"rebackCodes" : "wfToNext_DONE_USER_ID",
			"config" : "SY_ORG_DEPT_USER",
			"parHandler" : _viewer
		};
		var dictView = new rh.vi.rhDictTreeView(options);
		dictView.show(event);

	});
});
}

//图形化流程跟踪按钮
var figureBtn = _viewer.getBtn('wfFigure');
figureBtn.bind("click",function() {
	if (!(_viewer.params && _viewer.params.PI_ID)) {
		_viewer.params = _viewer.links;
	}
	var opts = {"tTitle":"流程图","url":"SY_WFE_TRACK_FIGURE.show.do?data=" + encodeURI(JsonToStr(_viewer.params)),"params":_viewer.params,"menuFlag":3};
	Tab.open(opts);
});

if (_viewer.getListData()._EXIST_PROC_DEF_ && _viewer.getListData()._EXIST_PROC_DEF_ == 2) {
	figureBtn.hide();
}

//点击中止按钮
if(_viewer.getBtn('stopNodeInst')){
	_viewer.getBtn('stopNodeInst').unbind("click").bind("click",function() {
		if(!_ifCheckRadio()){
			return;
		}
		var niIDs = _viewer.grid.getSelectPKCodes();
		var param = {};
		param["NI_ID"] = niIDs[0];	
		FireFly.doAct(_viewer.servId, "stopNodeInst", param, true);
		_viewer.refresh();
	});
}

//催办按钮
if (_viewer.getBtn("cuiban")) {
	_viewer.getBtn('cuiban').unbind("click").bind("click",function() {
		if(!_ifCheckRadio()){
			return;
		}
		
		var listData = _viewer.getListData();
		
		//流程跟踪（催办）按钮的其它实现，实现com.rh.core.plug.ICuiBan接口
		var cuibanConfig = System.getVar("@C_SY_WF_TRACK_CUIBAN_CLASS@");
		if (cuibanConfig) { //调用配置类的实现
			FireFly.doAct(_viewer.servId, "cuiban", {
				"CUIBAN_CLASS":cuibanConfig,
				"NI_ID":_viewer.grid.getSelectItemValues("NI_ID").join(","),
				"TO_USER_ID":_viewer.grid.getSelectItemValues("TO_USER_ID").join(","),
				"DONE_USER_ID":_viewer.grid.getSelectItemValues("DONE_USER_ID").join(","),
				"DATA_ID":listData.DOC_ID,
				"SERV_ID":listData.SERV_ID
			}, false, true, function(result) {
				var msg = $("<div>").html(result._MSG_);
				alert(msg.text());
			});
		} else { //起草催办单
			//取得指定列的办理人ID
			var params = {'ACPT_USER':_viewer.grid.getSelectItemVal("TO_USER_ID")};
			var deadLine = _viewer.grid.getSelectItemVal("NODE_LIMIT_TIME");
			if(deadLine){
				params.DEADLINE = deadLine;
			}
			
			
			params.DATA_ID = listData.DOC_ID;
			params.SERV_ID = listData.SERV_ID;
			
			jQuery.extend(params,_viewer.params);
			var url = "SY_WFE_REMIND.card.do";
			var options = {"url":url, "tTitle":"催办", "params":params};

			Tab.open(options);
		}
	});
};


//跟踪信息：底部信息
if ($(".BOTTOM_INFO").length == 0) {
	//因私出国（境）不予显示
	if (_viewer.params && _viewer.params.SERV_ID && StringUtils.startWith(_viewer.params.SERV_ID, "PE_")) {
		return false;
	}
	var bottomInfo = $("<div class='BOTTOM_INFO'>").css({
		"position":"absolute",
		"padding":"4px 0px",
		"background":"#F2F2F2",
		"border-top":"1px #DFDFDF solid",
		"border-bottom":"1px #DFDFDF solid",
		"line-height":"24px",
		"height":"24px",
		"clear":"both",
		"font-size":"12px",
		"width":"100%",
		"bottom":"0"
	});
	$("#SY_WFE_TRACK-select-container").after(bottomInfo);
	//bottomInfo.append("当前环节：");
	var NODE_NAME = "";
	if (_viewer.params.INST_IF_RUNNING == "1") { //运行中
		var list = _viewer._listData._DATA_ || [];
		var node = list[list.length-1];
		NODE_NAME = node.NODE_NAME;
	//	bottomInfo.append(node.NODE_NAME);
	} else { //办结
		NODE_NAME = "无";
	//	bottomInfo.append("无");
	}
	bottomInfo.append('<div style ="float:left; position:relative;width: 25%;">&nbsp;&nbsp;&nbsp;&nbsp;当前环节：'+ NODE_NAME+'</div>');
	//bottomInfo.append("&nbsp;&nbsp;&nbsp;&nbsp;当前处理人：");
	if (_viewer.params.INST_IF_RUNNING == "1") { //运行中
		var user = [];
		var code = [];
		var data = FireFly.doAct("SY_WFE_NODE_USERS", "finds", {
			"NI_ID":node.NI_ID,
			"PI_ID":_viewer.params.PI_ID
		}, false);
		$.each(data._DATA_, function(index, item){
			user.push(item.TO_USER_NAME + "[" + item.TO_DEPT_NAME + "]");
			code.push(item.TO_USER_NAME + "[" + item.TO_USER_ID + "]");
		});
		//bottomInfo.append($('<div style="float:left; position:relative; width: 400px; line-height:25px; height:25px; overflow-y:auto;">').text(user.join("，")));
		bottomInfo.append('<span><div style="float:left; position:relative;width:10%; height:25px;text-align:right;">&nbsp;&nbsp;当前处理人：</div><div style="float:left; position:relative; width: 64%; z-index : 1;line-height:25px; height:25px; overflow-y:auto;">'+user.join("，")+'</div></span>');
		
		//bottomInfo.append($("<span>").text(user.join("，")));
		bottomInfo.append($("<span style='display:none;'>").text(code.join("，")));
	} else { //办结
		bottomInfo.append("&nbsp;&nbsp;当前处理人：无");
	}
}

//合并“处理者”和“处理部门”
_viewer.grid.getBodyTr().each(function(){
    var dept = jQuery(this).find("td[icode='DONE_DEPT_NAMES']").text();
    var username = jQuery(this).find("td[icode='DONE_USER_NAME']").text();
    jQuery(this).find("td[icode='DONE_USER_NAME']").text(username+"["+dept+"]");
});

jQuery("th[icode='NI_ID']").prev().prev().text("序号");
jQuery("th[icode='DONE_DEPT_NAMES']").hide();
jQuery("td[icode='DONE_DEPT_NAMES']").hide();
jQuery("td[icode='DONE_USER_NAME']").css("text-align","left");
jQuery("td[icode='NODE_NAME']").css("text-align","left");
jQuery("td[icode='MIND_CONTENT']").css("text-align","left");
jQuery("th[icode='DONE_USER_NAME']").css("width","160px");
jQuery("th[icode='NODE_NAME']").css("width","200px");
jQuery("th[icode='MIND_CONTENT']").css("width","300px");
jQuery("th[icode='OPEN_TIME']").css("width","170px");
jQuery("th[icode='NODE_ETIME']").css("width","170px");
