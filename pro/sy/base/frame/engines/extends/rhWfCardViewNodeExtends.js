/**
 * 修改节点名称
 * @param {} param
 */
rh.vi.wfCardView.prototype.changeNodeName = function (param){
	var _self = this;
	var nodes = "";
	var SPLIT_POUND = "##";
    var nodeInstBean = _self.getNodeInstBean();
    var nextSteps = _self.getNextStepBean();
    //拼接当前节点
    nodes += (nodeInstBean.NODE_CODE + SPLIT_POUND);
    for (var i=0; i<nextSteps.length; i++){
    	//拼接下一处理节点
    	nodes += (nextSteps[i].NODE_CODE + SPLIT_POUND);
    }
    
    var paramBean = {"PROC_CODE":nodeInstBean.PROC_CODE,"NODE_CODES":nodes};
    jQuery.extend(paramBean,param);
    var result = FireFly.doAct("SY_WFE_NODE_EXTEND","queryNodeExtInfo",paramBean,false,false);
    var nodeInfoArr = result._DATA_;
    var $currentStep = jQuery("#__RhText_currentStep");
    var $nextStepOptions = jQuery("#__RhSelect_nextStepSelect").find("select option");
    for (var j=0; j<nodeInfoArr.length; j++){
    	if (nodeInfoArr[j].NODE_CODE == nodeInstBean.NODE_CODE){
    		$currentStep.find("span[class='rh-text-name']").text(nodeInfoArr[j].NODE_NAME);
    		continue;
    	}
    	
    	$nextStepOptions.each(function () {
    		if(jQuery(this).val() == nodeInfoArr[j].NODE_CODE){
    			jQuery(this).text(nodeInfoArr[j].NODE_NAME);
    		}
    	});
    }
}

/**
 * 更新跟踪信息的节点名称
 * @param {} className 类名称，用于找到指定的节点
 */
rh.vi.wfCardView.prototype.updateTrackNodeName = function (className) {
	if(className == null || "" == className){
		return;
	}
	var _self = this;
	var nodeInstBean = _self.getNodeInstBean();
	if (nodeInstBean){
		var nodeInstId = nodeInstBean.NI_ID;
        var currentNode = nodeInstBean.NODE_CODE;
        var procCode = nodeInstBean.PROC_CODE;
        FireFly.doAct("SY_WFE_NODE_EXTEND","updateTrackNode",{"NI_ID":nodeInstId,"NODE_CODE":currentNode,"PROC_CODE":procCode,"NODE_CHECK_CLASS":className},false,false);
    }
}

/**
 * 更新待办节点名称
 * @param {} dataId 数据主键
 */
rh.vi.wfCardView.prototype.updateTodoNodeInfo = function (dataId) {
	if(null == dataId || "" == dataId){
		return;
	}
	var _self = this;
	var nodeInstBean = _self.getNodeInstBean();
	if(nodeInstBean){
		var nodeName = jQuery("#__RhText_currentStep").find("span[class='rh-text-name']").text();
	    FireFly.doAct("SY_WFE_NODE_EXTEND","updateTodoNodeInfo",{"DATA_ID":dataId,"NODE_NAME":nodeName,"NI_ID":nodeInstBean.NI_ID},false,false);
	}
}

/**
 * 退回流程的上一节点处理人，基于处理完毕对话框改造
 * @param {} event
 * @param {} actItem
 */
rh.vi.wfCardView.prototype.cmBack = function(event, actItem){
	var _self = this;

	// 完成并发送之前所做操作
	if (_self._beforeSaveAndSend() === false) {
		return false;
	}
	
	//保存之前的监听方法beforeSave()
    if (_self._parHandler.beforeSave() == false) {
//    	Tip.showError("校验未通过！");
    	Tip.showError(Language.transStatic('rhWfCardViewNodeExtends_string1'));
    	return false;
    };
	 //保存意见方法
    if (_self._parHandler.saveMind() == false) {
    	return false;
    }
    
    //判断是否需要校验
    var params = {
        "PROC_CODE" : _self.getNodeInstBean().PROC_CODE,
        "NODE_CODE" : _self.getNodeInstBean().NODE_CODE,
        "SERV_ID" : _self._parHandler.servId
    };
    
    var rtnCheck = FireFly.doAct("SY_WFE_PROC","isBackBtnNeedCheck",params,false,false);
    if (rtnCheck.IS_VALIDATE == 1){
    	// 验证通过
		if (!_self._parHandler.form.validate()) {
//			Tip.showError("校验未通过");
			Tip.showError(Language.transStatic('rhWfCardViewNodeExtends_string1'));
			return false;
		}
    }

	// 保存
	var changeData = _self._parHandler.getChangeData();
	if (!jQuery.isEmptyObject(changeData)) {
		_self._parHandler.modifySave(changeData, false, false);
	}

	// 清除功能性按钮
	if(_self.btnRenderMode == 11 ||_self.btnRenderMode == 111){
		//先隐藏所有组按钮
		_self._hideAllBtnGroup();
		
		// 循环功能按钮，将其隐藏
		for ( var wfbtn in _self.wfBtns) {
			_self.wfBtns[wfbtn].layoutObj.hide();
		}
	
		// 显示下一步的按钮
		_self._reloadNextBtn();
	} else if (_self.btnRenderMode == 12 || _self.btnRenderMode == 112) { //处理完毕，弹出新框
		_self.bldBackConfirmWindow(true);
	}
	
	//弹出下一步后，加载ICBC刷新待办的页面
	if (window.ICBC) {
		var OIS_SYSTEM_URL = (System.getVar("@C_OIS_URL@") || "") + "/icbc/ois/jumper.html";
		if (OIS_SYSTEM_URL) {
			$(".ICBC_TODO_REFRESH").remove();
			$("body").append('<div class="ICBC_TODO_REFRESH" style="display:none;"><iframe id="jumper" src=' + OIS_SYSTEM_URL + '></iframe></div>');
		}
	}

	_self.isSendState = true;

	// 完成并发送之后所做操作
	_self._afterSaveAndSend();
	
	return true;    
}


/**
 * 构造完退回对话框（渲染状态为12或112时），基于处理完毕对话框改造
 * @param bool 是否重置默认值
 */
rh.vi.wfCardView.prototype.bldBackConfirmWindow = function(bool){
	var _self = this;
	
	//重新构建下拉列表选择的环节
    _self._clearNextSteps();
	_self.addNextStepBean(_self.getPreStepDoUser());
	
	var extOpts = _self.beforeBldBackConfirmWindow();
	
	var fieldCompleteOpts = _self.bldFieldComplete();
	
	var diaFuns = {
		"save": function(){},
		"confirm": function(){},
		"cancel": function(){}
	};
	
	// 获取dialog的位置
	var wid = 750;
	var hei = 450;
    var posArray = Tools.getDialogPosition(wid, hei);
    //卡片页面强制dialog位置top为100
    posArray[1] = 100;
	
	//1构造窗口
    var nextDialog;
    var isSave = false; // 记录是否暂存过，用于提醒用户暂存
	var winOptions = {
		autoOpen: false,
		height: 450,
		width: 750,
		modal: true,
		resizable:false,
		position: posArray,
		buttons: {
			"确定": function(){
//			Language.transStatic("rh_ui_gridCard_string17"): function(){	
				(diaFuns.confirm)();
			},
			"取消": function(){
//			Language.transStatic("rh_ui_card_string18"): function(){	
				(diaFuns.cancel)(nextDialog);
				try {
					top.jQuery("body").css("overflow-y","auto");
				} catch(e) {
					jQuery("body").css("overflow-y","auto");
				}
                _self._clearBackAndResetWfData();
		        	
			}
		},
		beforeClose: function(){
			
			var mindData = _self.mind.getValue();
			var hasContent = false;
			if ("REGULAR" in mindData 
					&& "MIND_CONTENT" in mindData["REGULAR"]
					&& mindData["REGULAR"]["MIND_CONTENT"].length > 0) { // 固定意见
				
				hasContent = true;
			}
			
			if ("TERMINAL" in mindData
					&& "MIND_CONTENT" in mindData["TERMINAL"]
					&& mindData["TERMINAL"]["MIND_CONTENT"].length > 0) { // 最终意见
				
				hasContent = true;
			}
			
			if ("GENERAL" in mindData
					&& "MIND_CONTENT" in mindData["GENERAL"]
					&& mindData["GENERAL"]["MIND_CONTENT"].length > 0) { // 普通意见
				
				hasContent = true;
			}
			
			return true;
		},
		close: function() {
			try {
				top.jQuery("body").css("overflow-y","auto");
			} catch (e) {
				jQuery("body").css("overflow-y","auto");
			}
			nextDialog.remove();
            _self._clearBackAndResetWfData();
		}
	};
//	nextDialog = _self.getDialog("cmBack_dialog", "退回处理", winOptions);
	nextDialog = _self.getDialog("cmBack_dialog", Language.transStatic("rhWfCardViewNodeExtends_string2"), winOptions);
	nextDialog.css({"background-color":"white"}); // 背景颜色修改改成白色突出内容
	nextDialog.parent().addClass('rh-next-dialog');
	
	//2构造下一环节设置区域
	var rhNextContainer = jQuery("<div id='rh-next' class='rh-next'></div>");
	
	//当前环节
//	var opts = [{'type':'text', 'id':'currentStep', 'label':'当前环节', 'name':_self.getNodeInstBean().NODE_NAME, 'value':_self.getNodeInstBean().NI_ID, 'sort':100}];
	var opts = [{'type':'text', 'id':'currentStep', 'label':Language.transStatic("rhWfCardViewNodeExtends_string3"), 'name':_self.getNodeInstBean().NODE_NAME, 'value':_self.getNodeInstBean().NI_ID, 'sort':100}];
	//构造 处理选择的部分
	_self.bldWfSelect(opts);
	
	//构造送交下一步的，节点及人员
	_self.bldNextStep(opts);
	
	//构造分发(阅知)区域，先判断是否定义了
	if (_self.getAuthBean().showYueZhi == 1) {
		var configStr = "@com.rh.core.serv.send.SimpleFenfaDict,{'TYPE':'multi','CHECKBOX':true,'extendDicSetting':{'rhexpand':false,'expandLevel':1,'cascadecheck':true,'checkParent':false,'childOnly':true}}";
//		var yuezhi = {'type':'tree', 'id':'yuezhiSelect', 'label':'阅知', 'value':'', 'dictConfig':configStr ,'sort':400, "params": {"displaySendSchm": false, "displayScope": "tdept"}};
		var yuezhi = {'type':'tree', 'id':'yuezhiSelect', 'label':Language.transStatic("rhWfCardViewNodeExtends_string4"), 'value':'', 'dictConfig':configStr ,'sort':400, "params": {"displaySendSchm": false, "displayScope": "tdept"}};
		
		opts.push(yuezhi);
	}
	
	opts = jQuery.merge(opts, fieldCompleteOpts);
	
	opts = jQuery.merge(opts, extOpts);
	
	opts.sort(function(a, b){
        return a.sort - b.sort;
    });
	opts.diaFuns = diaFuns;
	opts.dialog = nextDialog;
	
	this.rhNext = new RhNext(opts, null, {'nodeId':_self.getNodeInstBean().NI_ID, 'dataId':''});
	this.rhNext.appendTo(rhNextContainer);
	nextDialog.append(rhNextContainer);
	
	//重载 RhNext 确定时候的 事件，构造传入后台的数据
	this.rhNext.save = function() {
		var data = this.getStoreValue();
		data["MIND_DATA"] = _self.mind.getValue();
		store.set(this.storeKey, data);
		try {
//			top.Tip.show("草稿成功保存");
			top.Tip.show(Language.transStatic("rhWfCardViewNodeExtends_string5"));
		} catch (e) {
//			Tip.show("草稿成功保存");
			Tip.show(Language.transStatic("rhWfCardViewNodeExtends_string5"));
		}
	}
	this.rhNext.confirm = function() {
		return _self.confirmToNext();
	}

	//4构造意见区域
	var param = {"viewer":_self._parHandler,"id":"rh.cm.mind","servId":_self._parHandler.getServSrcId()
			,"dataId":_self._parHandler._pkCode,"wfCard":_self,"pCon":this.rhNext.getMindCon(),"_isDialog":true,"_asyncList":true,"_rhNext":this.rhNext};
	_self.mind = new rh.cm.mind(param);
	_self.mind.render();
	
	//注册下一步按钮变化的事件
	_self.regStepChg();
	
	//注册点击获取用户的时候的事件
	_self.userSelectClick();
	
	//初始化确定
	_self.setDialogConfirmName();
	
	jQuery("#__RhSelect_nextStepSelect").find("label").text("上一环节");
	
	//在构造处理完毕完成之后要做的事情
	_self.afterBldBackConfirmWindow();
};

/**
 * 构建退回弹出框之前的处理
 * @return {}
 */
rh.vi.wfCardView.prototype.beforeBldBackConfirmWindow = function (){
	return [];
}

/**
 * 构建退回弹出框之后的处理
 */
rh.vi.wfCardView.prototype.afterBldBackConfirmWindow = function (){

}

/**
 * 获取上一环节的处理人
 * @return {}
 */
rh.vi.wfCardView.prototype._getPreStepDoUserNoModify = function (){
	var _self = this;
	var nodeBean = _self.getNodeInstBean();
	var paramBean = {
		"PI_ID":nodeBean.PI_ID,
		"NODE_CODE":nodeBean.NODE_CODE
	};
	var result = FireFly.doAct("SY_WFE_PROC","getPreStepNodeDoUser",paramBean,false,false);
	var list = result._DATA_;
	for (var i=0; i<list.length; i++){
//		list[i].NODE_NAME = "退回"+list[i].NODE_NAME;
		list[i].NODE_NAME = Language.transStatic("rhWfCardViewNodeExtends_string6")+list[i].NODE_NAME;
	}
	return result._DATA_;
}

/**
 * 清除并复位“处理完毕”流程流转数据
 */
rh.vi.wfCardView.prototype._clearBackAndResetWfData = function (){
	var _self = this;
    _self._clearNextSteps();
    var params = {
    	"S_WF_STATE":_self._parHandler.byIdData.S_WF_STATE,
        "NI_ID":_self.getNodeInstBean().NI_ID,
        "DO_USER_DEPT":_self.getAuthBean().DO_USER_DEPT
    };
    var rtnData = FireFly.doAct("SY_WFE_PROC","getNextSteps",params,false,false);
    for (var i=0; i<rtnData.nextSteps.length; i++){
    	(_self._parHandler.byIdData.nextSteps).push(rtnData.nextSteps[i]);
    }
}

/**
 * 清除下一步中的流转数据
 */
rh.vi.wfCardView.prototype._clearNextSteps = function (){
	var _self = this;
	var wfSendBtns = _self._parHandler.byIdData.nextSteps;
    while (wfSendBtns.length > 0){
    	wfSendBtns.pop();
    }
}