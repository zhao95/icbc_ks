
GLOBAL.namespace("mb.vi");

mb.vi.wfCardView = function(options) {
    var defaults = {
		"id":options.sId + "-viWfCardView",
		"sId":"",//服务ID
		"pId":options.sId,
		"parHandler":null,//主卡片的句柄
		"pkCode":""
	};
	var _self = this;
	this.opts = jQuery.extend(defaults,options);
	this._parHandler = this.opts.parHandler;
	this._nextStepNodeCode = "";
	
	//从页面取流程实例的信息，因手机版的未绑定流程信息故，注释掉该代码
	this.procInstId = this._parHandler.itemValue("S_WF_INST");
	this.wfState = this._parHandler.itemValue("S_WF_STATE");	
	
    this.reqdata = {}
	this.reqdata["PI_ID"] = this.procInstId;	//流程实例ID
	this.reqdata["INST_IF_RUNNING"] = this.wfState;	   //流程是否在运行
	this.reqdata["DATA_ID"] = this._parHandler._pkCode; 
	if (!(this.getNodeInstBean() == undefined)) {
		this.reqdata["NI_ID"] = this.getNodeInstBean().NI_ID;
	} 
	this.wfBtns = {};
	this.wfNextBtns = {};
	this.wfNextBtnNames = {};
	
    return this;	
};

mb.vi.wfCardView.prototype.render = function() {
	var _self = this;
	//先注释掉获取表单按钮的代码
	_self.btnRenderMode = 0;
	/*获取按钮条渲染方式的系统配置(共八种状态)
	 * 0:操作按钮平铺，流程按钮平铺
	 * 10：操作按钮平铺，流程按钮下拉组
	 * 11：操作按钮平铺，流程按钮单独按钮条
	 * 12: 操作按钮平铺，流程按钮在弹出框中去处理
	 * 100：操作按钮下拉组，流程按钮平铺
	 * 110：操作按钮下拉组，流程按钮下拉组
	 * 111：操作按钮下拉组，流程按钮单独按钮条
	 * 112:操作按钮下拉组，流程按钮在弹出框中去处理
	 **/
	var renderMode = System.getVar("@C_SY_WF_BTN_RENDER@");
	if(renderMode){
		if((renderMode == 0 ||
			renderMode == 10 || 
			renderMode == 11 ||  
			renderMode == 12 || 
			renderMode == 100 || 
			renderMode == 110 || 
			renderMode == 111 ||
			renderMode == 112)){
				_self.btnRenderMode = renderMode;
		}else{
			alert("系统配置：按钮条渲染方式SY_WF_BTN_RENDER配置值有误，请检查！");
		}
	}
	//临时改法
	var btnBarHtmlObj = this._parHandler.btnBar[0];
	btnBarHtmlObj.addEventListener("click", function(event){
		if(_focus_){
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			return false;
		}
	}, true);
	
	this._bldBtnBar();
	//获取工作的按钮
	this._bldNextBtnBar();
	this._doFiledControl();
	//显示相关文件
	/*if (this.getAuthBean().isShowRelate == "true") { 
	    this._bldRelateList();
	}
	Load.scriptJS("/sy/base/frame/engines/rhRealteFiles.js");
	var opts = {};
	opts["wfHandler"] = this;
	this.wfRelate = new mb.vi.relateFiles(opts);	
	*/
	//显示分发列表
	/*if (this.getAuthBean().isShowFenfaList == "true") {
		this._bldSendList();
	}*/
	
	//判断是否锁定 1,锁定，2正常 ， 锁定的文件，全部只读
	if (this.getAuthBean().lockState == "1") {
		this._parHandler.form.disabledAll();
	}
	//初始化意见
	this.initMind();
};

/*
 * 节点实例bean
 */
mb.vi.wfCardView.prototype.getNodeInstBean = function() {
    var _self = this;
	
	return _self._parHandler.byIdData.nodeInstBean;
};

/*
 * 相关权限 比如能否手写意见， 当前人是否正在处理当前的流程
 */
mb.vi.wfCardView.prototype.getAuthBean = function() {
    var _self = this;
	return _self._parHandler.byIdData.authBean;
};

/*
 * 获取按钮
 */
mb.vi.wfCardView.prototype.getButtonBean = function() {
    var _self = this;
	return _self._parHandler.byIdData.buttonBean;
};


/*
 * 获取页面field控制的数据
 */
mb.vi.wfCardView.prototype.getFieldControlBean = function() {
    var _self = this;
	return _self._parHandler.byIdData.fieldControlBean;
};

/*
 * 获取页面field控制的数据
 */
mb.vi.wfCardView.prototype.getBindTitle = function() {
    var _self = this;
	return _self._parHandler.byIdData.bindTitle;
};
mb.vi.wfCardView.prototype.getNextStepBean = function() {
    var _self = this;
	if (_self._parHandler.byIdData.nextSteps == undefined || _self._parHandler.byIdData.nextSteps == "undefined") {
	    return "";
	}
	return _self._parHandler.byIdData.nextSteps;
};
/*
 * 获取下一步的节点列表数据
 */
mb.vi.wfCardView.prototype.getNextStepBean_sc = function() {
    var _self = this;
    var userCode =  _viewer.itemValue("USER_CODE");
    var actID =  _viewer.itemValue("WF_ACT_ID");
	
    var actParam= {'WF_ACT_ID':actID};
    var result = FireFly.doAct("TBL_WFE_ACTIVITY_INST","find",actParam,false);
    var wfActID = result._DATA_;
    
    var wfParam= {'userID':userCode,'wfActID':'151054'};
    result = FireFly.doAct("TBL_WFE_BUTTONS","find",wfParam,false);
    var wfButtons = result._DATA_;
    return buttons;
};



/*
 * 对页面字段进行控制 , 如隐藏， 必填等控制
 */
mb.vi.wfCardView.prototype._doFiledControl = function() {
    var _self = this;
    var _fileControlData = _self.getFieldControlBean();
	
	var entirelyControl = _fileControlData.FIELD_CONTROL;
	var exceptionFiledStr = _fileControlData.FIELD_EXCEPTION;
	var updateFiledStr = _fileControlData.FIELD_UPDATE;
	var hiddenFiledStr = _fileControlData.FIELD_HIDDEN;	
	var mustFiledStr = _fileControlData.FIELD_MUST;		
 
	var parServID = _self.opts.pId;
	//可编辑字段
	if(entirelyControl == "false") {
		_self._parHandler.form.disabledAll();
		if (exceptionFiledStr.length > 0) {
			jQuery.each(exceptionFiledStr.split(','),function(i,item) {
				if(_self._parHandler.getItem(item)) {
					_self._parHandler.getItem(item).enabled();
				}
			});	
		}
	}

	//隐藏字段
	if (hiddenFiledStr.length > 0) {
		jQuery.each(hiddenFiledStr.split(','),function(i,item) {
			_self._parHandler.getItem(item).obj.hide();
		});
	}
	
	//必填字段
	if (mustFiledStr.length > 0) {
		jQuery.each(mustFiledStr.split(','),function(i,item) {
			_self._parHandler.form.setNotNull(item,true);
		});	
	}
};

/*
 * 构建卡片按钮条, 表单按钮  节点定义转换之后的按钮
 */
mb.vi.wfCardView.prototype._bldBtnBar = function() {
    var _self = this;
    this._btnBar = this._parHandler.btnBar;
	
	_self._buttonData = _self.getButtonBean();
	
	var formSaveButtonFlag = false;
	jQuery.each(this._buttonData,function(i,actItem) {
		if (actItem.ACT_CODE == "cmWfDataMgr" || actItem.ACT_CODE == "cmSetAttention" || actItem.ACT_CODE == "conversion") { //手机版暂时未实现的按钮功能
			return;
		}
		if (actItem.ACT_MOBILE_FLAG == "2") {
			return;
		}
		if (actItem.ACT_CODE == "save") { // 如果循环到了save
			formSaveButtonFlag = true;
			return;
		}
		if (actItem.ACT_CODE == "cmWfTrackFigure") { //不渲染流程跟踪按钮
			return;
		}
		if (actItem.ACT_CODE == "delete") {
		    actItem.ACT_CODE = "deleteDoc";
		}
		//构造提交任务按钮
		if (actItem.ACT_CODE == "cmSaveAndSend") {
			var btn = _self._parHandler.bldGroupBtn(actItem,_self[actItem.ACT_CODE],_self);
			_self._parHandler.addBtn(actItem.ACT_CODE, btn, true);
			_self._parHandler.bldBtnGroup(actItem.ACT_CODE);
			return;
		}
		//构造普通流程按钮
		var btn = _self._parHandler.bldBtn(actItem,_self[actItem.ACT_CODE],_self);
		_self._parHandler.addBtn(actItem.ACT_CODE, btn);
		var wfBtnObj = {};
		wfBtnObj["layoutObj"] = btn;
		wfBtnObj["dataObj"] = actItem;
		_self.wfBtns[actItem.ACT_CODE] = wfBtnObj;
	});
	if(!formSaveButtonFlag){ //没有save,隐藏表单的
	    //jQuery("#" + GLOBAL.getUnId("save",this.opts.pId)).parent().hide();
		if (_self._parHandler.temlBtnStore.save) {
			delete _self._parHandler.temlBtnStore.save;
			_self._parHandler.temlBtnStore.COUNT = _self._parHandler.temlBtnStore.COUNT - 1;
		}
	}
};

/*
 * 构建下一步卡片按钮条
 */
mb.vi.wfCardView.prototype._bldNextBtnBar = function() {
    var _self = this;
    this._btnBar = this._parHandler.btnBar;
	var _nextData = _self.getNextStepBean();	
	jQuery.each(_nextData,function(i,actItem) {
    	//工作流按钮名称上输入有空格,则取空格后部分
		var nodeNameSplitArray = actItem.NODE_NAME.split(" ");
		if (nodeNameSplitArray.length > 1) {
			actItem.NODE_NAME = nodeNameSplitArray[1];
		}
//    	if (name.length > 4) {
//    		name = name.substring(0,4) + "..";
//    	}
		actItem.ACT_CODE = actItem.NODE_CODE;
		actItem.ACT_NAME = actItem.NODE_NAME;
		var btn = _self._parHandler.bldBtn(actItem);
		_self._parHandler.addBtn("_cmSaveAndSend%" + actItem.ACT_CODE, btn, true);
		if(typeof(actItem.NODE_USER) != "undefined"){ // 如果是返回XX的按钮，取得返回人的CODE
			btn.bind("click",function() {
				
				if (_self._mindMust() == false) { // 先执行送交前的代码，比如必填意见
//					_self._parHandler.shieldHide(); 
					return false;
				}
				
				_self.reqdata["NODE_CODE"] = actItem.NODE_CODE;
				_self.reqdata["TO_USERS"] = actItem.NODE_USER;		
				_self.reqdata["TO_TYPE"] = "3";		
				//把流程信息送交给oa
				var newActInst = rh_processData("SY_WFE_PROC.toNext.do",_self.reqdata);
				_self._parHandler.showTip("已经成功 " + actItem.NODE_NAME);
				_self._parHandler.refresh();
		    });			
		} else {
			btn.bind("click",function(event) {
				_self._openSelectOrg(actItem.NODE_CODE,btn,event);
		    });		
			var nextBtnObj = {};
			nextBtnObj.layoutObj = btn;
			nextBtnObj.dataObj = actItem;
		    _self.wfNextBtns[actItem.NODE_CODE] = nextBtnObj;			
			_self.wfNextBtnNames[actItem.NODE_NAME] = nextBtnObj;
		}
	});
};

/**
 * 保存并发送
 */
mb.vi.wfCardView.prototype.cmSaveAndSend = function() {
	var _self = this;
	// 完成并发送之前所做操作
	if (_self._beforeSaveAndSend() === false) {
		return false;
	}
	// 验证通过
	if (!_self._parHandler.form.validate()) {
		_self._parHandler.showTipWarn("校验未通过");
		return false;
	}
	// 保存
	var changeData = _self._parHandler.getChangeData();
	if (!jQuery.isEmptyObject(changeData)) {
		_self._parHandler.modifySave(changeData, false);
	}
	if (_self.btnRenderMode == 12 || _self.btnRenderMode == 112) { //处理完毕，弹出新框
		_self.bldNextConfirmWindow();
		return false;
	}
	//隐藏组
	// 完成并发送之后所做操作
	_self._afterSaveAndSend();	
	return true;
}

/**
 * 点击完成并发送之前所做的逻辑操作
 */
mb.vi.wfCardView.prototype._beforeSaveAndSend = function() {
	return true;
};

/**
 * 点击完成并发送之后所做的逻辑操作
 */
mb.vi.wfCardView.prototype._afterSaveAndSend = function() {
};

/**
 * 模拟点击返回的按钮
 */ 
mb.vi.wfCardView.prototype._pretendClickBack = function() {
    var _self = this;    
    _self._refreshMainPage();
	
    var aBackElement = jQuery("#rhCard-back");
	if (aBackElement) {
	   jQuery("#rhCard-back").click();
	}
}

/**
 * 刷新首页待办
 */
mb.vi.wfCardView.prototype._refreshMainPage = function() {
	//刷新首页的待办，主办等区块
	if (_parent.Portal.getBlock("SY_COMM_TODO")) {
		_parent.Portal.getBlock("SY_COMM_TODO").refresh();	//刷新待办	
	}
	if (_parent.Portal.getBlock("SY_COMM_ENTITY")) {
		_parent.Portal.getBlock("SY_COMM_ENTITY").refresh();	//刷新主办	
	}
}

/**
 * 判断返回的组织资源树，是否只有一个人  //"NAME":.*?"usr".*?usr:.*?}    new RegExp("\"usr:.*?\"", "g");
 */
mb.vi.wfCardView.prototype.treeDataHaseOneMan = function (treeData) {
	if (treeData == "[]") {
	    return "multi";
	}

	if (treeData.split("usr:").length == "2") {//
		// 取到这个人，然后直接送这个人，
		var end = treeData.indexOf("}");
		var start = treeData.lastIndexOf("{");
		var userCode = treeData.substring(start + 1, end)

		return userCode;
	}
	
	return "multi";
};


 /*
 * 根据 节点信息， 打开人员选择窗口 ，也就是送交人
 * @param aId 动作ID
 */
mb.vi.wfCardView.prototype._openSelectOrg = function(aId,aObj,event, selectedUserIds, selectedNames) {
	var _self = this;
	
	if ((_self.btnRenderMode != 12 && _self.btnRenderMode != 112) && _self._mindMust() == false) {
//		_self._parHandler.shieldHide();
		return false;
	}
	
	var taObj = aObj;
	var nodeCode = aId;
	_self._nextStepNodeCode = nodeCode;	
	var inputName = GLOBAL.getUnId("wfToUser",_self.opts.sId);
	_self.reqdata["NI_ID"] = _self.getNodeInstBean().NI_ID;
	_self.reqdata["NODE_CODE"] = nodeCode;
	_self.reqdata["SERV_ID"] = _self._parHandler.servId;	
	
	var rtnTreeData = rh_processData("SY_WFE_PROC.getNextStepUsersForSelect.do",_self.reqdata);
	var rtnMsg = rtnTreeData._MSG_;
	if (rtnMsg && (StringUtils.startWith(rtnMsg, "ERROR,") || 
			StringUtils.startWith(rtnMsg, "OK,ERROR,getNextStepUsersForAct:"))) { 
		if (StringUtils.startWith(rtnMsg, "OK,ERROR,getNextStepUsersForAct:")) {
			//判断返回的错误信息是不是ACT抛出的, 没有人选择出来， 将人员的框进行隐藏
			_self.hideUserSelect();
		} else {
			alert(rtnTreeData._MSG_);
		}
		
		return;
	}
	
	var treeData = rtnTreeData.treeData;
	if (treeData == "[]") {
		_self._parHandler.showTipError("当前节点没有可供选择的人员，请检查工作流配置！");
		return false;
	}
	var sendDirectFlag = true;
	var oneUserCodes = "";
	var bindTreeTitle = "人员选择";
	this.binderType = rtnTreeData.binderType;  //送交类型  
	if (_self.binderType == "ROLE") {
	    //如果是角色，取到角色的CODE
		this._binderRoleCode = rtnTreeData.roleCode;
		bindTreeTitle = "部门选择";
		sendDirectFlag = false;
	} else { //送人
		var rtnOneValue = _self.treeDataHaseOneMan(treeData);
		if (rtnOneValue == "multi") {
		    sendDirectFlag = false;
		} else {
		    oneUserCodes = rtnOneValue;
		}
	}
	if (sendDirectFlag) { // 直接送人
	    var oneUserObj = StrToJson("{" + oneUserCodes + "}");
	
	    var userCodeArray = new Array();
		userCodeArray.push(oneUserObj.ID);
	    var userNameArray = new Array();
		userNameArray.push(oneUserObj.NAME);		
	    _self._confirmSend(userCodeArray, userNameArray);
	} else { //显示组织机构树
		treeData = eval("(" + treeData + ")");
		var multiSelect = rtnTreeData.multiSelect;  //是否能多选
		var extendTreeSetting = "{'rhexpand':false,'expandLevel':0,";
		var selectType = "multi";
		if (multiSelect == "false") {
			selectType = "single";
			//extendTreeSetting += "'cascadecheck':true,'checkParent':false,'showcheck':true";
		} else {
			//extendTreeSetting += "'cascadecheck':false,'checkParent':false,'showcheck':false";
		}
		//extendTreeSetting += "}";
		//extendTreeSetting = StrToJson(extendTreeSetting);	
		var configStr = "SY_ORG_USER" + ",{'TYPE':'"+selectType+"'}";  //multi , single
//		var options = {"itemCode":inputName,"config" : configStr,"hide":"explode","show":"blind","rebackCodes":inputName,"replaceData":treeData,
//		"replaceCallBack":_self._confirmSend,
//		"extendDicSetting":extendTreeSetting,
//		"dialogName":bindTreeTitle,
//		"parHandler":_self};
//		var dictView = new mb.vi.rhDictTreeView(options);
//		dictView.show(event);	
	    var temp = {"config": configStr,"replaceData":treeData,"replaceCallBack":_self._confirmSend,"parHandler":_self};
	    this.selectView = new mb.vi.selectList(temp);
	    this.selectView._bldWin(event, true);
	    this.selectView.show();
	}
};

/**
 * 必填意见的判断，不通过mind去判断了 , 写到这里，直接查数据库
 */
mb.vi.wfCardView.prototype._mindMust = function() {
	var _self = this;
	
	var mindMustReq = _self.getNodeMindObj();
	
	mindMustReq.NI_ID = _self.getNodeInstBean().NI_ID;
	mindMustReq.DATA_ID = _self._parHandler._pkCode;
	
	var rtnData = FireFly.doAct("SY_COMM_MIND", "checkFillMind", mindMustReq, false);	
	
	if (rtnData.pass == "NO") { //没通过检查
		
		alert(rtnData.reason);
		
		return false;
	} else {
		return true;
	}
};

/**
 * 将流程送下个节点的人
 */
mb.vi.wfCardView.prototype._confirmSend = function(idArray,nameArray) {
	var _self = this;
	if (_self.btnRenderMode == 12 || _self.btnRenderMode == 112 || _self._parHandler.form.validate()) { // 校验成功
		//送交的类型  1 送部门+角色 ， 3 送用户
		var toType = "3";
		if(_self.binderType == "ROLE"){
		    toType = "1";
			_self.reqdata["TO_DEPT"] = idArray[0].replace("dept:",""); //送交部门
			_self.reqdata["TO_ROLE"] = _self._binderRoleCode; //送交角色		
		} else {
			var userArray = new Array();
			jQuery(idArray).each(function(indextrty,intrty){
			   if (intrty.indexOf("usr:") ==0) {
				   userArray.push(intrty);
			   }
			});
			var userNameStr = userArray.toString().replace(new RegExp("usr:","gm"),"");
			if (userNameStr.length<=0) {
				_self._parHandler.showTipWarn("没有选中人员，请重新选择送交人员");
				return false;
			} 
		    _self.reqdata["TO_USERS"] = userNameStr; //送交人 替换掉所有的usr:
		}
		_self.reqdata["TO_TYPE"] = toType;  //类别
		_self.reqdata["NI_ID"] = _self.getNodeInstBean().NI_ID;  //当前节点实例ID
		_self.reqdata["NODE_CODE"] = _self._nextStepNodeCode;  //下个节点CODE
		
		if (_self.btnRenderMode == 12 || _self.btnRenderMode == 112) { //如果是弹出框的模式
			_self.confirmUserSelectSend(_self.reqdata, nameArray);
			
			return;
		}

		if(confirm("确认送交?")){
			if (this.selectView) {
				this.selectView._close();
			}
		} else {
			return false;
		}		
		
		var result = rh_processData("SY_WFE_PROC.toNext.do",_self.reqdata);
		//alert(idArray.join(",") + nameArray.join(","));
		//刷新页面
		_self._parHandler.showTip("已经成功送交给" + nameArray.join(","));
		//_self._parHandler.refresh();
		_self._parHandler.backClick(true);
	}
};

 /*
 * 根据动作绑定相应的方法
 * @param actId 动作ID
 * 
 */
mb.vi.wfCardView.prototype._act = function(actId,servId,aObj,actItem) {
	var _self = this;
	var taObj = aObj;
	if (actItem.ACT_CODE == "delete") {
	    actItem.ACT_CODE = "deleteDoc";
	}
	taObj.bind("click",function(event){
		if(actItem.ACT_MEMO.length > 1 && actItem.ACT_MEMO != "null") {
		    eval(actItem.ACT_MEMO);
		} else {
			eval("_self." + actItem.ACT_CODE + "(event,actItem)");
		}
	});
	var wfBtnObj = {};
	wfBtnObj["layoutObj"] = taObj;
	wfBtnObj["dataObj"] = actItem;
	_self.wfBtns[actItem.ACT_CODE] = wfBtnObj;
};


/**
 * 获取按钮 
 */
mb.vi.wfCardView.prototype._getBtn = function(actCode) {
	return this.wfBtns[actCode];
};

/**
 * 获取按钮 根据CODE
 */
mb.vi.wfCardView.prototype._getWfNextBtn = function(actCode) {
	return this.wfNextBtns[actCode];
};


/**
 * 获取按钮 根据名称
 */
mb.vi.wfCardView.prototype._getNextBtnByName = function(actName) {
	return this.wfNextBtnNames[actName];
};


/**
 * 点击流程按钮  前执行 funName 函数
 */
mb.vi.wfCardView.prototype._doWfBtnBeforeClick = function(funName,btnObj) {
    var _self = this;
			
	var layoutObj = btnObj.layoutObj;
    var actItem = 	btnObj.dataObj;
	
	layoutObj.unbind("click").bind("click",function(event) {
	    var rtnValue = funName.call();

	    if (rtnValue) {
			if(actItem.ACT_MEMO.length > 1 && actItem.ACT_MEMO != "null") {
				eval(actItem.ACT_MEMO);
			}else {
				eval("_self." + actItem.ACT_CODE + "(event,actItem)");
			}
		}
	});	
};

/**
 * 点击流程按钮  后执行 funName 函数
 */
mb.vi.wfCardView.prototype._doWfBtnAfterClick = function(funName,btnObj) {
    var _self = this;
			
	var layoutObj = btnObj.layoutObj;
    var actItem = 	btnObj.dataObj;
	
	layoutObj.unbind("click").bind("click",function(event) {
		if(actItem.ACT_MEMO.length > 1 && actItem.ACT_MEMO != "null") {
			eval(actItem.ACT_MEMO);
		}else {
			eval("_self." + actItem.ACT_CODE + "(event,actItem)");
		}
		//之后执行
	    funName.call();		
	});	
};

/**
 * 点击下一步节点前执行 funName 函数
 */
mb.vi.wfCardView.prototype._doNextStepBeforeClick = function(funName,btnObj) {
    var _self = this;
			
	var layoutObj = btnObj.layoutObj;
    var actItem = 	btnObj.dataObj;
	
	layoutObj.unbind("click").bind("click",function(event) {
	    var rtnValue = funName.call();

	    if (rtnValue) {
		    _self._openSelectOrg(actItem.NODE_CODE,layoutObj,event);
		}
	});	
};

/**
 * 打开组织结构选择
 */
mb.vi.wfCardView.prototype._doOpenOrgSelect = function(event,btnObj) {
    var _self = this;
			
	var layoutObj = btnObj.layoutObj;
    var actItem = 	btnObj.dataObj;    

    _self._openSelectOrg(actItem.NODE_CODE,layoutObj,event);
};

/** 
 * 相关文件 , 出查询选择，选择关联的文件
 * TODO 这里的查询条件，需要是当前人能查到的，
 */
mb.vi.wfCardView.prototype.cmRelate = function(event) { 
	var _self = this;
	
	this.wfRelate.chooseRelateWay(event);
};

/** 
 * 套红头  ， 套红头之前，需要先选择成文模板 ， 点按钮出一个dialog ，可以选择成文模板，确定的时候，再提交
 */
mb.vi.wfCardView.prototype.redHead = function(event) { 
	var _self = this;
	var _self = this;
    
	//查询成文模板的列表
	var reqCwData = {};
	_self._parHandler.itemValue("GW_YEAR_CODE")
	
	var gwYearCodeItemValue = _self._parHandler.itemValue("GW_YEAR_CODE");
	if (gwYearCodeItemValue == undefined || gwYearCodeItemValue == "" ) {
	    _self._parHandler.showTipWarn("没有生成机关代字，不能套红头");
		return false;
	}
	
	reqCwData["GW_YEAR_CODE"] = _self._parHandler.itemValue("GW_YEAR_CODE");
	
	var cwTmplRtnList = rh_processData("OA_GW_CODE_CW_TMPL.getCwTmplListByCode.do", reqCwData);
	
	var cwListObj = cwTmplRtnList.cwList;
	
	if (cwListObj.length == 1) {
		var orgFileId = cwListObj[0].FILE_ID;
		var realFileId = orgFileId.split(",")[0];
	    _self._doRedHeadConfirm(realFileId);
	} else {
		var winDialog = jQuery("<div style='margin:10px 0px 0px 10px'></div>").addClass("selectDialog").attr("id","chengWenTmplListDiv").attr("title","成文模板");    
		
		var chengContent = jQuery("<div></div>").appendTo(winDialog);
		jQuery("<li style='width: 100%'></li>").append("请选择一个成文模板:").appendTo(chengContent);
		var chengWenTmplList = jQuery("<li class='right'></li>");

		var cwTmplList = jQuery("<select id='templateSelectName' class='ui-select-default2' name='templateSelectName' style='width:80%;margin-right:20%' />").appendTo(chengWenTmplList).appendTo(chengContent);
		
		jQuery.each(cwTmplRtnList.cwList,function(i, cwTmp) {
			//将FILE_ID 进行截串
			var orgFileId = cwTmp.FILE_ID;
			var realFileId = orgFileId.split(",")[0];
			
			jQuery("<option value='" + realFileId + "'>").append(cwTmp.CW_NAME).appendTo(cwTmplList);
		});
		
		//按钮-确定
		var confirmCwTmplUl = jQuery("<div></div>").appendTo(winDialog);
		var confirmCwTmplLi = jQuery("<li style='width: 100%;'></li>").appendTo(confirmCwTmplUl);
		var confirmRedHead = jQuery("<a href='javascript:void(0);'>确定</a>").appendTo(confirmCwTmplLi).appendTo(confirmCwTmplUl);
		confirmRedHead.bind("click",function() {
		    //取到选中的成文模板
		    _self._doRedHeadConfirm(jQuery("#templateSelectName").val());
		});

		//按钮-取消
		jQuery("<span>&nbsp;&nbsp;&nbsp;</span>").appendTo(confirmRedHead);
		var redHeadCancelBtn = jQuery("<a href='javascript:void(0);'>取消</a>").appendTo(confirmRedHead);
		redHeadCancelBtn.bind("click",function() {
			winDialog.remove();
		});
		
		var posArray = [];
		if (event) {
			var cy = event.clientY;
			posArray[0] = "";
			posArray[1] = cy-120;
		}
		winDialog.appendTo(jQuery("body"));
		jQuery("#chengWenTmplListDiv").dialog({
			autoOpen: false,
			width: 400,
			height: 260,
			modal: true,
			resizable:false,
			position:posArray,
			open: function() { 

			},
			close: function() {
				winDialog.remove();
			}
		});	
		jQuery("#chengWenTmplListDiv").dialog("open");
		jQuery(".ui-dialog-titlebar").last().css("display","block");			
	}
};

/**
 * 套红头
 */
mb.vi.wfCardView.prototype._doRedHeadConfirm = function(cwTmplFileId) { 
	var _self = this;
	
	var servDataId = _self._parHandler._pkCode;
	
	var servId = _self._parHandler.servId; //_self._parHandler.itemValue("TMPL_CODE");
	
	var target = _self._getZhengWenId(); //正文文件ID
	
	var source = _self._getWenGaoId(); //文稿的文件ID
	
	var redhatUrl = encodeURI("/sy/util/office/doRedHead.jsp?gwId=" + servDataId + "&dataId="  + servDataId + "&servId=" + servId + "&source=" + source + "&target=" + target + "&cwTmplFileId=" + cwTmplFileId);
	
	window.open(redhatUrl);    
};

/**
 * 取到公文的正文ID
 */
mb.vi.wfCardView.prototype._getZhengWenId = function() { 
	var _self = this;
	_self.reqdata.PK_CODE = _self._parHandler._pkCode;
	
	var formServId = _self._parHandler.servId;
	_self.reqdata.TMP_CODE = formServId;

	var result = rh_processData(formServId+".getGwZhengWenId.do",_self.reqdata);
	
	return result.zhengWenId;
};

/**
 * 取到公文的文稿ID
 */
mb.vi.wfCardView.prototype._getWenGaoId = function() { 
	var _self = this;
	_self.reqdata.PK_CODE = _self._parHandler._pkCode;
	
	var formServId = _self._parHandler.servId;
	_self.reqdata.TMP_CODE = formServId;

	var result = rh_processData(formServId+".getGwWenGaoId.do",_self.reqdata);
	
	return result.wenGaoId;
};


/** 
 * 退回 ， 填写退回说明 -> 添加退回的待办 -> 完成分发（设置返回的字段值） -> 待办变成已办
 * 
 */
mb.vi.wfCardView.prototype.cmTuiHui = function(event,actItem) { 
	var _self = this;
    var sendId = actItem.SEND_ID;
	//退回回执
	var winDialog = jQuery("<div style='margin:10px 0px 0px 10px'></div>").addClass("selectDialog").attr("id","tuiHuiDiv").attr("title","退回回执");    
	

	var huiZhiContent = jQuery("<ul></ul>").appendTo(winDialog);
	jQuery("<li  class='left' style='width: 30%'></li>").append("请填写退回原因:").appendTo(huiZhiContent);
	var huiZhiLi = jQuery("<li class='right'></li>");
	jQuery("<textarea class='ui-textarea-default' type='textarea' cols='50' rows='5' id ='huiZhiContent' name ='huiZhiContent'/>").appendTo(huiZhiLi);
	huiZhiLi.appendTo(huiZhiContent);	

	//按钮-确定
	var  tuiHuiBtn = jQuery("<a href='javascript:void(0);'>确定</a>").appendTo(winDialog);
	tuiHuiBtn.bind("click",function() {
		_self.reqdata["TODO_CONTENT"] = jQuery("#huiZhiContent").val();
		_self.reqdata["SEND_ID"] = sendId;
		var result = rh_processData(WfActConst.SERV_PROC + ".cmTuiHui.do",_self.reqdata);
		
		if (result.rtnstr == "success") {
			_self._parHandler.showTip("退回成功",true);
			_parent.Portal.getBlock("SY_COMM_TODO").refresh();
			_self._parHandler.backClick(true); 
		} else {
			_self._parHandler.showTipError("退回失败",true);
		}
	});

	//按钮-取消
	jQuery("<span>&nbsp;&nbsp;&nbsp;</span>").appendTo(winDialog);
	var tuiHuiCancelBtn = jQuery("<a href='javascript:void(0);'>取消</a>").appendTo(winDialog);
	tuiHuiCancelBtn.bind("click",function() {
	    winDialog.remove();
	});
	
	var posArray = [];
	if (event) {
		var cy = event.clientY;
		posArray[0] = "";
		posArray[1] = cy-120;
	}
	winDialog.appendTo(jQuery("body"));
	jQuery("#tuiHuiDiv").dialog({
		autoOpen: false,
		width: 400,
		height: 260,
		modal: true,
		resizable:false,
		position:posArray,
		open: function() { 

		},
		close: function() {
			winDialog.remove();
		}
	});	
	jQuery("#tuiHuiDiv").dialog("open");
	jQuery(".ui-dialog-titlebar").last().css("display","block");	
};

/** 
 * 转发   弹出选人的页面，确定即分发出去
 */
mb.vi.wfCardView.prototype.cmZhuanFa = function(event,actItem) { 
	var _self = this;
	//分发ID
	var sendId = actItem.SEND_ID;
	_self.reqdata.SEND_ID = sendId;  
	//var inputName = "zhuanfaUsers";	
	var configStr = "SY_ORG_DEPT_USER,{'TYPE':'multi'}";
	//var extendTreeSetting = {'cascadecheck':false,'checkParent':false};
	var options = {
		//"itemCode":inputName,		
		//"hide":"explode",
		//"show":"blind",
		//"extendDicSetting":extendTreeSetting,
		"dictId": "SY_ORG_DEPT_USER",
		"pCon":null,
		"config": configStr,
		"linkWhere": "",
		"extWhere": "",
		"showSearchFlag":"true",
		"replaceCallBack":_self._zhuanFa,
		"parHandler":_self
	};
	var dictView = new mb.vi.selectList(options);
	dictView._bldWin(event);
	dictView.show(event);
};

/**
 * 转发  
 */
mb.vi.wfCardView.prototype._zhuanFa = function(idArray,nameArray) {
	var _self = this;
	_self.reqdata["TARGET_USERS"] = idArray.join(",");
	_self.reqdata["SERV_ID"] = _self._parHandler.servId;
	_self.reqdata["DATA_ID"] = _self._parHandler._pkCode;
	_self.reqdata["DATA_TITLE"] = _self.getBindTitle(); // _self._parHandler.itemValue("GW_TITLE");
	// 是否转发
	_self.reqdata["isZF"] = "yes"; 
	var result = FireFly.doAct("SY_COMM_SEND_SHOW_CARD","cmZhuanFa",
			_self.reqdata);
	if (result[UIConst.RTN_MSG] && result[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
		_self._parHandler.showTip("转发成功");
    } else {
    	_self._parHandler.showTipError("转发失败");
    }
};

/** 
 * 签收
 */
mb.vi.wfCardView.prototype.cmQianShou = function(event, actItem) { 
	var _self = this;
	//分发ID
	var sendId = actItem.SEND_ID;
	_self.reqdata.SEND_ID = sendId;
	var result = rh_processData(WfActConst.SERV_PROC+".cmQianShou.do",_self.reqdata);
	if (result.rtnstr == "success") {
		_self._parHandler.showTip("操作成功",true);
	} else {
		_self._parHandler.showTipError("操作失败",true);
	}
	_self._parHandler.refresh();    
};

/** 
 * 锁定文件
 */
mb.vi.wfCardView.prototype.cmLockFile = function() { 
	var _self = this;
    
	var result = rh_processData(WfActConst.SERV_PROC+".cmLockFile.do",_self.reqdata);
	
	if (result.rtnstr == "success") {
	    _self._parHandler.refresh();
	    _self._parHandler.showTip("锁定成功");		
	} else {
		_self._parHandler.showTipError("操作失败",true);
	}
};

/** 
 * 解锁文件
 */
mb.vi.wfCardView.prototype.cmUnLockFile = function() { 
	var _self = this;
    
	var result = rh_processData(WfActConst.SERV_PROC+".cmUnLockFile.do",_self.reqdata);
	
	if (result.rtnstr == "success") {
	    _self._parHandler.refresh();
	    _self._parHandler.showTip("解锁成功");		
	} else {
		_self._parHandler.showTipError("操作失败",true);
	}
};

/**
 * 分发-分发按钮绑定函数
 */
mb.vi.wfCardView.prototype.cmFenFa = function(event,actItem) {
	var _self = this;
	
	//校验是否保存
	var changeData = _self._parHandler.getChangeData();
	if (!jQuery.isEmptyObject(changeData)) {
		_self._parHandler.showTipWarn("页面数据已修改，请先保存或恢复修改");
		return false;
	}
	
	//初始化分发对象
	var fenfaObj = {
		"SERV_ID": _self._parHandler.servId,
		"DATA_ID": _self._parHandler._pkCode,
		"DATA_TITLE": _self.getBindTitle(),
		"DATA_FROM": "FIELD_CONFIG", //表单 字段 配置的 分发
		"TITLE": "",	//弹出窗口标题
		"MIND_CODE": "", //意见编号
		"SEND_ID": "", //被分发对象（用户/部门/角色）编号
		"itemArr": null //分发节点配置数组
	};
	
	//先判断  ， 已经发了没， 如果已经发了， 提示
	if (_self._parHandler.itemValue("SEND_FLAG") == "1") { 
		var sendFlagFenfa = confirm("该文件已经分发过，是否再次分发?");
		if (!sendFlagFenfa) {
			return false;
		}
	}
	
	//如果不是手动分发，处理分发节点配置
	if ("" != (actItem["WFE_PARAM"] || "")) {
		//解析节点配置json信息
		var fenFaWfParam = "";
		try {
			fenFaWfParam = jQuery.parseJSON(actItem["WFE_PARAM"]);
		}catch(exception){
			_self._parHandler.showTipError("请确定节点json串格式是否正确，格式为{\"\":\"\"}");
			return false;
		}

		fenfaObj.TITLE = fenFaWfParam["TITLE"] || "分发文件";
		if(fenFaWfParam["MIND_CODE"] && fenFaWfParam["MIND_CODE"] != ""){
			fenfaObj.MIND_CODE = fenFaWfParam["MIND_CODE"];
		}
		fenfaObj.mode = fenFaWfParam["mode"];
		
		if(fenfaObj.mode && fenfaObj.mode.indexOf("auto") >= 0){
			//获取分发节点配置数组
			var fenFaItemArr = fenFaWfParam["SEND_ITEM"] || [];
			if(fenFaItemArr.length == 0){
				_self._parHandler.showTipError("自动分发模式下分发字段为空，请检查分发节点配置");
				return false;
			}
			fenfaObj.itemArr = fenFaItemArr;
			
			for (var i = 0; i < fenFaItemArr.length; i++) {
				var itemObj = _self._parHandler.getItem(fenFaItemArr[i].code);
				if (itemObj == null) {
					_self._parHandler.showTipError("您选择的分发字段不存在，字段名为：" + fenFaItemArr[i].code);
					return false;
				} else {
					var itemValue = itemObj.getValue();
					if ("" == (itemValue || "")) {
						//_self._parHandler.showTipError("您选择的分发字段没有值！");
						//return false;
					}else{
						fenFaItemArr[i].sendId = itemValue;
					}
				}
			}
		}
	}
		
	_self.autoSend(fenfaObj);
};

/**
 * 分发-显示分发选择用户界面前调用此参数
 */
mb.vi.wfCardView.prototype.beforeFenfa = function(fenfaObj){
	
};

/**
 * 分发-按节点配置json信息分发
 */
mb.vi.wfCardView.prototype.autoSend = function(fenfaObj) {
	var _self = this;
	
	// 服务ID
	var servId = fenfaObj.SERV_ID;
	// 被分发数据的ID
	var dataId = fenfaObj.DATA_ID;
	// 被分发数据标题
	var dataTitle = fenfaObj.DATA_TITLE;
	var sendObj = {
		"SERV_ID" : servId,
		"DATA_ID" : dataId,
		"DATA_TITLE" : dataTitle
	};
	
	sendObj.fromScheme = "yes"; //来源于方案
	sendObj.ifFirst = "yes";
	sendObj._extWhere = " and DATA_ID = '" + dataId + "'";
	sendObj.SEND_ITEM = fenfaObj.itemArr;
	sendObj.DATA_FROM = fenfaObj.DATA_FROM || "";
	
	var mode = fenfaObj.mode || "";
	
	if(mode == "auto"){ //全自动(直接分发)
		sendObj.sendMode = "auto";
		var data = jQuery.toJSON(sendObj);	
		if(!confirm("请确认是否要发送文件？")){
			return false;
		}
		_self._parHandler.shield();
		setTimeout(function(){ //适当加上延迟提升用户感觉
			FireFly.doAct("SY_COMM_SEND_SHOW_USERS", "autoSend", {"data":data}, true);
			_self._parHandler.shieldHide();
			_self._parHandler.showTip("发送完成！");
			_self._parHandler.refresh();
		},500);
	}else if(mode == "semi-auto"){ //半自动(显示待分发人员列表)
		sendObj.sendMode = "semi_auto";
		var url = "SY_COMM_SEND_SHOW_USERS.list.do";
		var opts = {
			"url" : url,
			"tTitle" : "待分发人员列表",
			"params" : sendObj,
			"menuFlag" : 4
		};
		//Tab.open(opts);
	}else{ //手动分发(如果分发节点没有配置字段项，直接打开卡片)
		_self.beforeFenfa(fenfaObj);
		var shoudongUrl = encodeURI("SY_COMM_SEND_SHOW_CARD.showSend.do?data="+ JsonToStr(fenfaObj));
		var opts = {
			"id" : "SY_COMM_SEND_SHOW_CARD-" + fenfaObj.SERV_ID,
			"url" : shoudongUrl,
			"tTitle" : "分发",
			"menuFlag" : 4
		};
		//Tab.open(opts);
	}
};

/**
 * 审批单的附件、意见、相关文件管理
 */
mb.vi.wfCardView.prototype.cmWfDataMgr = function(event) {
	var _viewer = this;
	var servDataId = _viewer._parHandler._pkCode;
	var serID = _viewer._parHandler.servId;
	var sId = "SY_COMM_ENTITY_GL";
	var data = {};
    data["_searchWhere"] = " and SERV_ID='"+serID+"' and DATA_ID='"+servDataId+"'";
	var entityData = FireFly.getListData("SY_COMM_ENTITY_GL",data);
	var params={};
	params["handler"] = _viewer;
	var options = {"url":sId + ".card.do?pkCode=" + entityData["_DATA_"][0]["ENTITY_ID"] , "tTitle":"数据管理", "menuFlag":3,"params":params};
	Tab.open(options);	
};

/** 
 * 补登意见
 */
mb.vi.wfCardView.prototype.cmBuDeng = function(event, actItem) {
	var _self = this;
	var leaders = StrToJson(actItem.leaders);
	if ( null == leaders || "" == leaders) {
		_self._parHandler.showTipWarn("当前没有补登领导可以选择!");
		return;
	}
	//补登意见弹出匡
    jQuery("#bdmind").dialog("destroy");
    var winDialog = jQuery("<div style='margin:10px 0px 0px 10px'></div>").addClass("selectDialog").attr("id","bdmind").attr("title","补登意见");
	//补登意见必要属性
	var ul = jQuery("<ul></ul>").appendTo(winDialog);
	var inputNameLi = jQuery("<li class='right'></li>"); 
	var users = jQuery("<select id='TARGET_USER' class='ui-select-default2' name='TARGET_USER' style='width: 200px;' />").appendTo(inputNameLi);
	//foreach leaders
	jQuery.each(leaders,function(i, leader) {
		jQuery("<option value='" + leader.userCode + "'>").append(leader.userName).appendTo(users);
	});
	inputNameLi.appendTo(ul);	
	//时间
	var bdtime = jQuery("<ul></ul>").appendTo(winDialog);
	jQuery("<li  class='left' style='width: 30%'></li>").append("补登时间").appendTo(bdtime);
	var timeLi = jQuery("<li class='right'></li>").appendTo(bdtime);
	jQuery("<input id='TARGET_TIME' class='Wdate ui-date-default' onfocus=\"WdatePicker({startDate:'%y-%MM-%dd %H:%m:%ss',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:false})\"  type='text' style='width: 200px;' />").appendTo(timeLi);
	//意见输入
	var bdmind = jQuery("<ul></ul>").appendTo(winDialog);
	jQuery("<li  class='left' style='width: 30%'></li>").append("意见").appendTo(bdmind);
	var bdmindLi = jQuery("<li class='right'></li>");
	jQuery("<textarea class='ui-textarea-default' type='textarea' cols='50' rows='3' id ='TARGET_MIND' name ='TARGET_MIND'  />").appendTo(bdmindLi);
	bdmindLi.appendTo(bdmind);
	jQuery("<BR/>").appendTo(winDialog);
	jQuery("<BR/>").appendTo(winDialog);
	jQuery("<BR/>").appendTo(winDialog);
	//按钮-保存
	var okBtn = jQuery("<a><span class='rh-icon rh-icon-inner'>保存<span class='rh-icon-img btn-save'></span></span></a>").appendTo(winDialog);
	okBtn.bind("click",function() {
		var user = jQuery("#TARGET_USER").val();
		var uname = jQuery("#TARGET_USER").find('option:selected').text();
		var mindstr = "<xmp>" + jQuery("#TARGET_MIND").val() +  "</xmp>";
		var mindTime = jQuery("#TARGET_TIME").val();
		// 保存意见
	    var currMindCodeName = _self.getMindCodeBean().CODE_NAME;
	    var currNiId = _self.getNodeInstBean().NI_ID;
	    var currMindCode = _self.getMindCodeBean().CODE_ID;
	    var currMindDisRule = _self.getMindCodeBean().MIND_DIS_RULE;
	    var currMindType = 1;
	    //save budeng mind	 
	    var data = {};
	    data["MIND_CONTENT"] = mindstr;
	    data["MIND_CODE"] = currMindCode;
	    data["SERV_ID"] = _self._parHandler.servId;  //服务名
	    data["DATA_ID"] = _self._parHandler._pkCode;
	    data["MIND_TYPE"] = currMindType;
	    data["WF_NI_ID"]= currNiId;
	    data["MIND_DIS_RULE"]= "";
	    data["BD_USER"]= user;
	    data["BD_UNAME"]= uname;
	    if (mindTime) {
	    	data["MIND_TIME"]= mindTime;
	    }
	    var resultData = FireFly.doAct("SY_COMM_MIND","save",data);
	    if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
	    	_self._parHandler.mind.refresh();
	    } else {
	    	_self._parHandler.showTipError("返回错误，请检查！" + JsonToStr(resultData), true);
	    }	 
		winDialog.remove();
	});
	jQuery("<span>&nbsp;&nbsp;&nbsp;</span>").appendTo(winDialog);
	//按钮-取消
	var  cancelBtn = jQuery("<a><span class='rh-icon rh-icon-inner'>取消<span class='rh-icon-img btn-clear'></span></span></a>").appendTo(winDialog);
	cancelBtn.bind("click",function() {
		 winDialog.remove();
	 });	 
    var posArray = [];
    if (event) {
	    var cy = event.clientY;
	    posArray[0] = "";
	    posArray[1] = cy-120;
    }
	winDialog.appendTo(jQuery("body"));
	jQuery("#bdmind").dialog({
		autoOpen: false,
		width: 400,
		height: 260,
		modal: true,
		resizable:false,
		position:posArray,
		open: function() { 
	
		},
		close: function() {
			winDialog.remove();
		}
	});
	jQuery("#bdmind").dialog("open");
	jQuery(".ui-dialog-titlebar").last().css("display","block");//设置标题显示
};

/** 
 * 流程跟踪
 */
mb.vi.wfCardView.prototype.cmWfTracking = function() { 
	var _self = this;
	
//	var trackObj = "{'act':'query','PI_ID':'" +  _self.procInstId + "','INST_IF_RUNNING':'" + _self.wfState + "'}";
//	
//	var opts = {"sId":"SY_WFE_TRACK","url":"SY_WFE_TRACK.list.do","tTitle":"流程跟踪","params":trackObj,"menuFlag":3};
//	Tab.open(opts);
	
	var url = FireFly.getContextPath() + "/sy/wfe/listTrack_mb.jsp?PI_ID=" + _self.procInstId + "&INST_IF_RUNNING=" + _self.wfState;
//	window.location.href = url;	

	_self.getIframeDialog("cmWfTracking_dialog", "跟踪信息", url);
};

mb.vi.wfCardView.prototype.getIframeDialog = function(dialogId, title, src) {
	var _self = this;
	var winOptions = {
		autoOpen: false,
		height: document.documentElement.clientHeight,
		width: "100%",
		modal: true,
		resizable: false,
		position: [0,0]
	};
	var priMindDialog = _self.getDialog(dialogId, title, winOptions);
	var iframe = jQuery("<iframe width='100%' height='100%'></iframe>");
	iframe.attr("src", src);
	iframe.css("border", "none");
	iframe.appendTo(priMindDialog);
};

/** 
 * 独占 ， 即开始处理文件 用于 送角色的时候，其中某人进流程，点独占，角色内其他人员就不能收到待办了
 */
mb.vi.wfCardView.prototype.duZhan = function() { 
	var _self = this;
	var result = rh_processData(WfActConst.SERV_PROC+".duZhan.do",_self.reqdata);
	if (result.rtnstr == "success") {
		_self._parHandler.showTip("操作成功",true);
	} else {
		_self._parHandler.showTipError("操作失败",true);
	}
	_self._parHandler.refresh();
};

/** 
 * 结束当前工作
 */
mb.vi.wfCardView.prototype.stopWfNode = function() { 
	var _self = this;
	var result = rh_processData(WfActConst.SERV_PROC+".stopWfNode.do",_self.reqdata);
	if (result.rtnstr == "success") {
		_self._parHandler.showTip("操作成功",true);
	} else {
		_self._parHandler.showTipError("操作失败",true);
	}
	_self._parHandler.refresh();
};

/** 
 * 打印
 */
mb.vi.wfCardView.prototype.cmPrint = function() { 
	var _self = this;
	_self.reqdata.PK_CODE = _self._parHandler._pkCode;
	
	var formServId = _self._parHandler.servId;
	_self.reqdata.TMP_CODE = formServId;

	// 打印预览
	window.open(formServId+".cmPrint.do?data=" + JsonToStr(_self.reqdata)); 
};

/** 
 * 办结
 */
mb.vi.wfCardView.prototype.finish = function() { 
	var _self = this;
	if (!confirm("确定办结该文件？")){
		return;
	}
	var result = rh_processData(WfActConst.SERV_PROC+".finish.do",_self.reqdata);
	_self._parHandler.refresh();
	_self._parHandler.showTip("办结成功！");
	//关闭当前页面
	_self._pretendClickBack();	
};

/** 
 * 取消办结
 */
mb.vi.wfCardView.prototype.undoFinish = function() { 
	var _self = this;
	var result = rh_processData(WfActConst.SERV_PROC+".undoFinish.do",_self.reqdata);
	_self._parHandler.refresh();
};

/** 
 * 收回
 */
mb.vi.wfCardView.prototype.withdraw = function(event,actItem) { 
	var _self = this;	
	try{
		var niIds = new Array();
		if(actItem.wdlist){
			var wdlist = actItem.wdlist;
//			wdlist = [
//			    {"NI_ID":123,"TO_USER_NAME":"aaa","NODE_NAME":"bbb"},
//			    {"NI_ID":234,"TO_USER_NAME":"aaa","NODE_NAME":"bbb"},
//			    {"NI_ID":345,"TO_USER_NAME":"aaa","NODE_NAME":"bbb"}
//			]
			if (wdlist.length == 0) {
				_self._parHandler.showTip("没有可以收回的流程。");
			} else if (wdlist.length == 1) { //只有一个用户的请求需要被收会，则自动处理
				niIds.push(wdlist[0].NI_ID);
				_self._sendWithdrawReq(niIds.join(","));
			} else { //如果有多个用户请求需要被收回
				var arr = new Array();
				arr.push("<div title='选择收回流程' id='withdrawDlg'><table class='ml10 mt10'>");
				for ( var i = 0; i < wdlist.length; i++) {
					var niBean = wdlist[i];
					arr.push("<tr><td class='p5'><input type='checkbox' name='niIds' value='");
					arr.push(niBean.NI_ID);
					arr.push("'></td>");
					arr.push("<td class='p5'>");
					arr.push(niBean.TO_USER_NAME);
					arr.push("(");
					arr.push(niBean.NODE_NAME);
					arr.push(")</td></tr>");
				}
				arr.push("</table></div>");
				jQuery("body").append(arr.join(""));
				arr = null;
				jQuery("#withdrawDlg").dialog({modal: true,
					width:300,height:450,
					position: { my: "top", at: "top-40", of: ".form-container" },
					close:function(){
						jQuery("#withdrawDlg").remove();
					},buttons: [{ text: "确定",click:function(){
							jQuery("#withdrawDlg").find("input:checked").each(function(){
								niIds.push(jQuery(this).val());
							});
							
							_self._sendWithdrawReq(niIds.join(","));
							jQuery("#withdrawDlg").dialog("close");
							jQuery("#withdrawDlg").remove();
						}
					},{text: "取消",click:function(){
							jQuery("#withdrawDlg").dialog("close");
							jQuery("#withdrawDlg").remove();
						}
					}]
				});
			}
		}
	} catch(e){
		_self._parHandler.showTip(e.message);
	} finally{
		//
	}
};

/**
 * 发送收回请求
 */
mb.vi.wfCardView.prototype._sendWithdrawReq = function(niIds){
	var _self = this;
	var dataId = _self._parHandler._pkCode;
	var _data = {_PK_:dataId ,nextNiIds:niIds }; //合并请求参数
	jQuery.extend(true, _data, _self.reqdata);
	var result = FireFly.doAct(WfActConst.SERV_PROC,"withdraw",_data);
	if (result[UIConst.RTN_MSG] && result[UIConst.RTN_MSG].indexOf(UIConst.RTN_ERR) >= 0) {
		var errMsg = result[UIConst.RTN_MSG].replace(UIConst.RTN_ERR, "");
		_self._parHandler.showTip("未成功收回：" + errMsg);
	} else {
		_self._parHandler.showTip("已经成功收回");
		_self._parHandler.refresh();
	}
}

/** 
 * 终止并发
 */
mb.vi.wfCardView.prototype.stopParallelWf = function() { 
	var _self = this;
	var result = rh_processData(WfActConst.SERV_PROC+".stopParallelWf.do",_self.reqdata);
	_self._parHandler.refresh();
};

/** 
 * 删除
 */
mb.vi.wfCardView.prototype.deleteDoc = function() { 
	var _self = this;
	var confirmDel=confirm("确定删除该文件？");
	if (confirmDel==false){
	     return ;
	}
	
	_self.reqdata.SERV_ID = _self._parHandler.servId;
	_self.reqdata.DATA_ID = _self._parHandler._pkCode;
	var result = rh_processData(WfActConst.SERV_PROC+".deleteDoc.do",_self.reqdata);
	if (result.rtnstr == "success") {
		_self._parHandler.showTip("删除成功",true);
        _self._pretendClickBack();
	    _self._parHandler.backClick(true);
	} else {
		_self._parHandler.showTipError("删除失败",true);
	}
};

/**
 * 在页面的最下面显示  分发的详细信息，点连接打开  分发明细列表
 */
mb.vi.wfCardView.prototype._bldSendList = function() {
    _self = this;
	
	var _cardSendObj = jQuery("<div id='sendDtailBtn' style='margin:0 auto;background-color:white;width:98%;height:30px; line-height:30px;'></div>").addClass("ui-form-default").addClass("center").addClass("ui-corner-5").attr("height",300);
	
	var _sendDetailHref = jQuery("<a href='#' onclick='_self._showSendList()' style='margin-left:18px;padding-left:18px'>分发明细>></a>").addClass("btn-cmFenFa");
	_sendDetailHref.appendTo(_cardSendObj);
	
	_cardSendObj.appendTo(_self._parHandler.formCon);
};

/**
 * 分发明细列表
 */
mb.vi.wfCardView.prototype._showSendList = function() {
	var sendObj = {};
	sendObj.SERV_ID = _self._parHandler.servId;
    sendObj.DATA_ID = _self._parHandler._pkCode;
	if (_self.getAuthBean().userDoInWf == "true") {
		var opts = {"url":"SY_COMM_SEND_DETAIL.list.do","tTitle":"分发明细","params":sendObj,"menuFlag":4};
		Tab.open(opts);
	} else {
		var opts = {"url":"SY_COMM_SEND_DETAIL.list.do?readOnly=true","tTitle":"分发明细","params":sendObj,"menuFlag":4};
		Tab.open(opts);		    
	}
};

/**
 * 简单分发之前调用，常用于重置params的值。
 */
mb.vi.wfCardView.prototype.beforeCmSimpleFenFa = function(actItem, params){
	return true;
};

/**
 * 简单分发
 */
mb.vi.wfCardView.prototype.cmSimpleFenFa = function(event, actItem){
	var _self = this;
	var params = {
		"DATA_ID" : _self._parHandler._pkCode,
		"userSelectDict":"SY_ORG_DEPT_USER_SUB",
		"displaySendSchm":false,
		"includeSubOdept":true,
		"displayScope": "tdept"
	};
	
	if (this.beforeCmSimpleFenFa) {
		var result = this.beforeCmSimpleFenFa(actItem,params);
		if (!result) {
			return;
		}
	}
	var configStr = "@com.rh.core.serv.send.SimpleFenfaDict,{'TYPE':'multi'}";
	var options = {
		//"dictId": "SY_ORG_DEPT_USER",
		"config": configStr,
		"pCon":null,
		"linkWhere": "",
		"extWhere": "",
		"showSearchFlag":"true",
		"replaceCallBack" : function(id,value){
			var sendObj = {
				"SERV_ID" : _self._parHandler.servId,
				"DATA_ID" : _self._parHandler._pkCode,
				"DATA_TITLE" : _self.getBindTitle()
			};
			sendObj.fromScheme = "yes"; // 来源于方案
			sendObj.ifFirst = "yes";
			sendObj._extWhere = " and DATA_ID = '" + _self._parHandler._pkCode + "'";
			sendObj.SEND_ITEM = [{"sendId":id.join(",")}];
			sendObj.includeSubOdept = params.includeSubOdept;			
//			_self._parHandler.shield();			
//			setTimeout(function() { // 适当加上延迟提升用户感觉
//				try{		
					var data = jQuery.toJSON(sendObj);
					var result = FireFly.doAct("SY_COMM_SEND_SHOW_USERS", "autoSend",{"data":data}, true);
					if (!result[UIConst.RTN_MSG] || result[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) < 0) {
						_self._parHandler.showTipError("发送失败");
					} else {
						_self._parHandler.showTip("发送成功");
					}
//				} catch (e){
//					throw e;
//				} finally{
////					_self._parHandler.shieldHide();
//				}
//			}, 50);			
		},
		"parHandler":_self,
		"params" : params
	};
	var dictView = new mb.vi.selectList(options);
	dictView._bldWin(event);
	dictView.show(event);
};

/**
 * 关注，弹出列表页，可添加，删除，关注的事项
 */
mb.vi.wfCardView.prototype.cmSetAttention = function(event, actItem) {
	var _self = this;
	var attentionObj = {};
	attentionObj.PI_ID = _self.procInstId;
	attentionObj.S_USER = System.getVar("@USER_CODE@");
	attentionObj.act = "query";
	attentionObj.DATA_ID = _self._parHandler._pkCode;
	var url = encodeURI("SY_COMM_ATTENTION.list.do?data="+ JsonToStr(attentionObj));
	var opts = {
		"url" : url,
		"tTitle" : "关注",
		"params" : attentionObj,
		"menuFlag" : 3
	};
	Tab.open(opts);	
};


/**
 * Act 定义的一些常量
 */
var WfActConst = {
    /** 流程定义的服务 */
    SERV_PROC : "SY_WFE_PROC"
};
/**
 * 如果有委托办理，则增加委托办理人标识
 */
mb.vi.wfCardView.prototype.appendAgentUserFlagTo = function(reqdata){
	//增加委托用户USER_CODE
	var _agentUser = this._parHandler.getByIdData("_AGENT_USER_");
	if(_agentUser != ""){
		reqdata["_AGENT_USER_"] = _agentUser;
	}	
}
/**
 * @param varCode
 *            变量名称
 * @returns 指定变量的值，未找到指定变量则返回undefined
 */
mb.vi.wfCardView.prototype.getCustomVarContent = function(varCode) {
	var customVars = this._parHandler.byIdData.WF_CUSTOM_VARS;
	var result = undefined;
	if (customVars) {
		jQuery(customVars).each(function(index, item) {
			if (item.VAR_CODE == varCode) {
				result = item.VAR_CONTENT
			}
		});
	}

	return result;
}
/**
 * 取得审批单的现实模式。常用的查看模式有：MODE_DOING(办理模式)、MODE_ADMIN（流程管理员）、
 * MODE_FLOW（流经模式）、MODE_BASE（最低权限，分发、接收用户查看模式）
 */
mb.vi.wfCardView.prototype.getDisplayMode = function() {
	return this._parHandler.byIdData.WF_DISPLAY_MODE || "";
}
/**
 * 初始化意见
 */
mb.vi.wfCardView.prototype.initMind = function() {
	var _self = this;
	// 动态装载意见代码
	//Load.scriptJS("/sy/comm/mind/mind.js");

	if (!_self._parHandler.existSubServ("MINDLIST")) { //没有意见的关联服务
		var param = {"viewer":_self._parHandler,"id":"rh.cm.mind","servId":_self._parHandler.getServSrcId()
				,"dataId":_self._parHandler._pkCode,"wfCard":this};
		_self.mind = new rh.cm.mind(param);
		_self.mind.render();
	} else { //关联了意见，如果定义了意见分组框，将其隐藏
		var mindFieldset = UIConst.MIND_FIELDSET;
		
		var mindGroup = _self._parHandler.form.getGroup(mindFieldset);
		if (!mindGroup) {
			mindGroup = _self._parHandler.form.getGroup(null, UIConst.FITEM_ELEMENT_MIND);
		}
		
		if (mindGroup) {
			mindGroup.hide();
		}
	}
};

/*
 * 意见Code Bean
 */
mb.vi.wfCardView.prototype.getMindCodeBean = function() {
	var _self = this;
	var genMindList = _self._parHandler.byIdData.mindCodeBean;

	//处理完毕的方式
	if (_self.rhNext && _self.rhNext && _self.rhNext.getItem(_self.getWfSelectFieldName())){
		var wfSelectBaseField = _self.getAuthBean().S_WF_SELECT_BASE.FIELD_CODE;
		var genMindType = _self.rhNext.getItem(_self.getWfSelectFieldName()).getValue();
		
		_self._parHandler.byIdData[wfSelectBaseField] = genMindType;
		
		for (var i=0;i<genMindList.length;i++) {
			var def = genMindList[i];
			var mindScript = def.MIND_SCRIPT;
			mindScript = Tools.itemVarReplace(mindScript, _self._parHandler.byIdData);
			if (eval(mindScript)) {
				var newGenMind = JSON.parse(JSON.stringify(def));
				if (_self.mindMustExt) {
					newGenMind.MIND_MUST = "1";
				}
				return newGenMind;
			}
		}
	}
	
	var genMindBean = genMindList[0];
	if (genMindBean) {
		var newGenMind = JSON.parse(JSON.stringify(genMindBean)); //用这个是避免改变里面的值之后，影响之前的
		if (_self.mindMustExt) {
			newGenMind.MIND_MUST = "1";
		}
	}
	
	return newGenMind || genMindList;
};

/*
 * 最终 意见Code Bean
 */
mb.vi.wfCardView.prototype.getTerminalMind = function() {
	var _self = this;

	return _self._parHandler.byIdData.mindTerminal || {};
};

/**
 * 取得固定意见
 */
mb.vi.wfCardView.prototype.getRegularMind = function() {
	var _self = this;
	var result = jQuery.extend({}, _self._parHandler.byIdData.regularMind);
	return result;
}
/**
 * 取得意见类型的对象
 */
mb.vi.wfCardView.prototype.getNodeMindObj = function() {
	var _self = this;
	
	var nodeMind = {};
	
	nodeMind.REGULAR = JsonToStr(_self.getRegularMind());
	nodeMind.TERMINAL = JsonToStr(_self.getTerminalMind());
	nodeMind.GENERAL = JsonToStr(_self.getMindCodeBean());	
	
	return nodeMind;
}
/**
 * 取得当前审批单的办理用。兼容委托办理情况
 */
mb.vi.wfCardView.prototype.getDoUserBean = function(){
	//增加委托用户USER_CODE
	var _agentUserBean = this._parHandler.getByIdData("_AGENT_USER_BEAN_");
	if(_agentUserBean){
		return _agentUserBean;
	}
	
	return System.user;
};
/**
 * 是否能填写最终意见
 */
mb.vi.wfCardView.prototype.canWriteTerminalMind = function() {
	var mindCodeBean = this.getTerminalMind();
	if (mindCodeBean && mindCodeBean.READ_ONLY) {
		if (mindCodeBean.READ_ONLY == "true") {
			return false;
		}
	}

	return true;
};

/**
* 是否流程已启动
**/
mb.vi.wfCardView.prototype.isWorkflow = function(){
	var _self = this; 
	if (typeof(_self._parHandler.getItem("S_WF_INST")) != "undefined" 
			&& _self._parHandler.getItem("S_WF_INST").getValue().length >0 ) {
		return true;
	}
	return false;
};

/**
 * 弹出对话框
 */
mb.vi.wfCardView.prototype.getDialog = function(dialogId,title,params){
	//设置jqueryUi的dialog参数
	if(title == null){
		title = ""
	}
	var winDialog = jQuery("<div></div>").addClass("selectDialog mb-send").attr("id",dialogId).attr("title",title);
	winDialog.appendTo(jQuery("body"));
	var defaultOpt = {
		autoOpen: false,
		height: 400,
		width: 600,
		modal: true,
		resizable:false,
		position:[30,30],
		open: function(){
			jQuery(".mbCard-container").css("display","none");
		},
		close: function() {
			jQuery(".mbCard-container").css("display","block");
			winDialog.remove();
		}
	};
    //生成jqueryUi的dialog
	winDialog.dialog(jQuery.extend(defaultOpt, params));
	//手动打开dialog
	winDialog.dialog("open");
	winDialog.focus();
  jQuery(".ui-dialog-titlebar").last().css("display","block");//设置标题显示
  winDialog.parent().addClass("rh-bottom-right-radius rhSelectWidget-content");
  Tip.show("努力加载中...",null,jQuery(".ui-dialog-title",winDialog).last());
  return winDialog;
};

/**
 * 单独意见留言
 */
mb.vi.wfCardView.prototype.cmPriMind = function(){
	var _self = this;
	var card = _self._parHandler;
	var diaFuns = {
		"confirm": function(){},
		"cancel": function(){}
	};
	var lyMindCode = "BLY-0002";
	
	//0获取已填数据
	var where = " and DATA_ID = '" + card._pkCode + "' and WF_NI_ID = '" + _self.getNodeInstBean().NI_ID + "' and MIND_CODE = '" + lyMindCode + "'";
	FireFly.doAct("SY_COMM_MIND", "finds", {"_WHERE_": where} ,false, false, function(res){
		if (res["_DATA_"]) {
			var lyData = res["_DATA_"][0] || {};
			
			//1构造窗口
			var winOptions = {
				autoOpen: false,
				height: document.documentElement.clientHeight,
				width: "100%",
				modal: true,
				resizable: false,
				position: [(jQuery("body").width() - 730)/2,event.clientY-10],
				buttons:{
					"确定": function(){
						(diaFuns.confirm)();
					},
					"取消": function(){
						(diaFuns.cancel)();
					}
				}
			};
			var priMindDialog = _self.getDialog("cmPriMind_dialog", "给行领导留言", winOptions);
			priMindDialog.css({"background-color": "white"}); // 背景颜色修改改成白色突出内容
			
			//2构造输入区域
			var textAreaObj = jQuery("<textarea></textarea>");
			textAreaObj.css({
				"width": "90%",
				"height": "100px",
				"margin": "10px auto",
				"display": "block",
				"padding": "5px"
			}).val(lyData["MIND_CONTENT"] || "");
			priMindDialog.append(textAreaObj);
			
			//3绑定送交方法
			diaFuns.confirm = function(){
				var mindContent = textAreaObj.val();
				if (!mindContent) {
					top.Tip.addTip("意见不能为空", "warn");
					return false;
				}
				var data = {
					"DATA_ID": card._pkCode,
					"MIND_CODE": lyMindCode,
					"MIND_CODE_NAME": "总经理给行领导留言",
					"MIND_CONTENT": textAreaObj.val(),
					"MIND_DIS_RULE": "3",
					"MIND_FILE": "",
					"MIND_ID": lyData["MIND_ID"] || "",
					"MIND_TYPE": "1",
					"SERV_ID": card.servId,
					"S_FLAG": 2,
					"S_TDEPT": System.getVar('@TDEPT_CODE@'),
					"S_USER": System.getVar('@USER_CODE@'),
					"WF_NI_ID": _self.getNodeInstBean().NI_ID,
					"WF_NI_NAME": _self.getNodeInstBean().NODE_NAME,
					"_ADD_": !lyData["MIND_ID"],
					"_PK_": lyData["MIND_ID"] || ""
				}
				var resultData = FireFly.doAct("SY_COMM_MIND", "save", data);
				if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
					top.Tip.addTip("保存成功");
					diaFuns.cancel();
					setTimeout(function(){
						card.refresh();
					}, 200);
				} else {
					top.Tip.addTip("保存失败", "error");
				}
			};
			diaFuns.cancel = function(){
				priMindDialog.closest(".ui-dialog").find(".ui-icon-closethick").click();
			};
		} else {
			top.Tip.addTip("获取意见数据出错", "error");
		}
	});
};

/**
 * 构造完成并发送对话框（渲染状态为12或112时）
 */
mb.vi.wfCardView.prototype.bldNextConfirmWindow = function(){
	var _self = this;

	var extOpts = _self.beforeBldNextConfirmWindow();
	
	var diaFuns = {
		"save": function(){},
		"confirm": function(){},
		"cancel": function(){}
	};
	
	//1构造窗口
	var nextDialog;
	var winOptions = {
		autoOpen: false,
		height: document.documentElement.clientHeight,
		width: "100%",
		modal: true,
		resizable:false,
		position:[(jQuery("body").width() - 730)/2,event.clientY-10],
		buttons:{
			"保存": function(){
				(diaFuns.save)();
			},
			"确定": function(){
				(diaFuns.confirm)();
			},
			"取消": function(){
				(diaFuns.cancel)(nextDialog);
				jQuery(".mbCard-container").css("display","block");
			}
		}
	};
	nextDialog = _self.getDialog("cmSaveAndSend_dialog", "送交处理", winOptions);
	nextDialog.css({"background-color":"white"}); // 背景颜色修改改成白色突出内容
	
	//2构造下一环节设置区域
	var rhNextContainer = jQuery("<div id='rh-next' class='rh-next'></div>");
	
	//当前环节
	var opts = [{'type':'text', 'id':'currentStep', 'label':'当前环节', 'name':_self.getNodeInstBean().NODE_NAME, 'value':_self.getNodeInstBean().NI_ID, 'sort':100}];
	//构造 处理选择的部分
	_self.bldWfSelect(opts);
	
	//构造送交下一步的，节点及人员
	_self.bldNextStep(opts);
	
	//构造分发(阅知)区域，先判断是否定义了
	if (_self.getAuthBean().showYueZhi == 1) {
		var configStr = "@com.rh.core.serv.send.SimpleFenfaDict,{'TYPE':'multi','CHECKBOX':true,'extendDicSetting':{'rhexpand':false,'expandLevel':1,'cascadecheck':true,'checkParent':false,'childOnly':true}}";
		var yuezhi = {'type':'tree', 'id':'yuezhiSelect', 'label':'阅知', 'value':'', 'dictConfig':configStr ,'sort':400, "params": {"displaySendSchm": false, "displayScope": "tdept"}};
		
		opts.push(yuezhi);
	}
	
	opts = jQuery.merge(opts, extOpts);
	
	opts.sort(function(a, b){
        return a.sort - b.sort;
    });
	opts.diaFuns = diaFuns;
	
	this.rhNext = new RhNext(opts, null, {'nodeId':_self.getNodeInstBean().NI_ID, 'dataId':''});
	this.rhNext.appendTo(rhNextContainer);
	nextDialog.append(rhNextContainer);
	
	//重载 RhNext 确定时候的 事件，构造传入后台的数据
	this.rhNext.save = function() {
		var data = this.getStoreValue();
		data["MIND_DATA"] = _self.mind.getValue();
		store.set(this.storeKey, data);
		top.Tip.addTip("草稿成功保存");
	}
	this.rhNext.confirm = function() {
		return _self.confirmToNext();
	}
	
	//4构造意见区域
	var param = {"viewer":_self._parHandler,"id":"rh.cm.mind","servId":_self._parHandler.getServSrcId()
			,"dataId":_self._parHandler._pkCode,"wfCard":_self,"pCon":this.rhNext.getMindCon(),"_isDialog":true,"_asyncList":true};
	_self.mind = new rh.cm.mind(param);
	_self.mind.render();
	
	//注册下一步按钮变化的事件
	_self.regStepChg();
	
	//注册点击获取用户的时候的事件
	_self.userSelectClick();
	
	//初始化确定
	_self.setDialogConfirmName();
	
	//需要初始化的，eg:如果上次暂存的是ACT, 则把选人的隐藏，
	_self.initUserSelect();
	
	_self.afterBldNextConfirmWindow();
};

/**
 * 初始化用户的选择， 有两种特殊情况需要做处理
 * 1，如果之前缓存的是ACT， 则在第二次打开的时候，不能显示选人的框
 * 2，如果之前缓存的是 VP-N1-N2 ， 则第二次打开的时候， 需要显示两个送人的框，并且需要显示第二个框以前保存的值
 */
mb.vi.wfCardView.prototype.initUserSelect = function() {
	var _self = this;
	
	var nextStepName = WfNextStep.nextStep;
	var nextStepVlaue = _self.rhNext.getItem(nextStepName).getValue(); 

	//因为现在的VP-N1-N2 都是送交的，不会有其中有一个是ACT，
	if (nextStepVlaue.indexOf("ACT") == 0) { //是ACT的，
		var userSelectName0 = WfNextStep.userSelect();
		if (_self.rhNext.getItem(userSelectName0)) { //存在
			_self.rhNext.getItem(userSelectName0).initValue("", "");
			_self.rhNext.getItem(WfNextStep.deptRole()).setValue("");
			_self.rhNext.getItem(userSelectName0).setNotNull(false);
			_self.rhNext.getItem(userSelectName0).$dom.hide();
			_self.rhNext.getItem(userSelectName0).$chooseDom.hide();
		}
	}
	
	//如果是 VP-N1-N2 有多个选人的框，因为从N2之后，都是动态构建的，在这里重新画上并塞上值
	if (nextStepVlaue.indexOf("VP-") == 0) { //如果是同时送交的节点
		var devideObj = StrToJson(_self.getAuthBean().DEVIDE_TWO_NODE);
		var nodeObjs = devideObj.NODES;
		var firstNode = nodeObjs[0].NODE_CODE;
		
		jQuery.each(devideObj.NODES, function(index, item){
			if (index > 0) {
				_self.bldNewUserSelect(item, index);
				//设置上以前保存在本地的值
				var storeValue = _self.rhNext.getStoreValueById(WfNextStep.userSelect(index));
				_self.rhNext.getItem(WfNextStep.userSelect(index)).setValueFromStore(storeValue);
				
				var storeValueRole = _self.rhNext.getStoreValueById(WfNextStep.deptRole(index));
				_self.rhNext.getItem(WfNextStep.deptRole(index)).setValueFromStore(storeValueRole);
			} else {
				_self.reBindUserSelectClick(item.NODE_CODE);
			}
		});
	}
}


/**
 * 弹出处理完毕 点击获取用户的按钮
 */
mb.vi.wfCardView.prototype.userSelectClick = function() {
	var _self = this;
	
	var nextStepName = WfNextStep.nextStep;
	var userSelectName = WfNextStep.userSelect();

	_self.rhNext.getItem(userSelectName).$chooseDom.unbind("click").click(function(event){
		//获取到下一步的按钮
		var nextStep = _self.rhNext.getItem(nextStepName).getValue();
		
		//判断如果选择了下一步，则去做
		if (nextStep) {
			_self.fetchBinderData(nextStep, event);
		}
	});
}

/**
 * 重新绑定第一个 用户选择的事件
 */
mb.vi.wfCardView.prototype.reBindUserSelectClick = function(nodeCode) {
	var _self = this;
	
	var userSelectName = WfNextStep.userSelect();
	_self.rhNext.getItem(userSelectName).$chooseDom.unbind("click").click(function(event){
		_self.selectUserToPos = 0;
		_self.fetchBinderData(nodeCode, event);
	});
}


/**
 * 构造传入后台的数据
 */
mb.vi.wfCardView.prototype.confirmToNext = function() {
	var _self = this;
	
	var deferred = $.Deferred();

	//校验意见
	var idArray = _self.rhNext.getItem(WfNextStep.userSelect()).getValue();
	var nameArray = _self.rhNext.getItem(WfNextStep.userSelect()).getName();
	if (!_self.mind.mindCheck(_self.mind.getValue()) || !_self.callback(idArray, nameArray)) {
		deferred.reject();
		return deferred.promise();
	}
	
	if (confirm("确定送下一环节")) {
		var paramBean = {};
		paramBean.DATA_ID = _self._parHandler._pkCode;
		paramBean.SERV_ID = _self._parHandler.servId;
		paramBean.NI_ID = _self.getNodeInstBean().NI_ID;
		paramBean.INST_IF_RUNNING = _self.wfState; 
		
		paramBean.FORM_DATA = _self.confirmGetFormData(); //卡片js去处理
		
		var wfDataArray = new Array();
		var nextStepName = WfNextStep.nextStep;
		var nextStepVlaue = _self.rhNext.getItem(nextStepName).getValue(); 

		var nextStepArray = [nextStepVlaue];
		if (nextStepVlaue.indexOf("VP-") == 0) { //如果是同时送交的节点
			nextStepArray = nextStepVlaue.replace("VP-","").split("-");
		}
		for (var index=0;index<nextStepArray.length;index++) {
			var userSelectName = WfNextStep.userSelect(index);
			var deptRoleSelectName = WfNextStep.deptRole(index);
		
			var wfData = {};
			wfData.PI_ID = _self.procInstId; 
			wfData.INST_IF_RUNNING = _self.wfState; 
			wfData.NI_ID = _self.getNodeInstBean().NI_ID;
			
			wfData.NODE_CODE = nextStepArray[index]; 
			//先判断是送角色还是送用户，
			var toRoleCode = _self.rhNext.getItem(deptRoleSelectName).getValue();
			if (toRoleCode && toRoleCode.length > 0) { //送角色
				wfData.TO_DEPT = _self.rhNext.getItem(userSelectName).getValue();	
				wfData.TO_ROLE = toRoleCode;	
				wfData.TO_TYPE = "1"; 
			} else { //送人
				wfData.TO_USERS = _self.rhNext.getItem(userSelectName).getValue();	
				wfData.TO_TYPE = "3";
			}
			if (_self.rhNext.getItem(userSelectName).getValue().length > 0
					|| wfData.NODE_CODE.indexOf("ACT") == 0) { //如果送交对象不为空,才把数据放到一起去
				wfDataArray.push(wfData);	
			}
		}

		paramBean.WF_DATA = wfDataArray;

		if (_self.getAuthBean().showYueZhi == 1) { //有显示阅知
			var sendData = {};
			sendData.fromScheme = "yes";
			var yuezhiSelectedValue = _self.rhNext.getItem("yuezhiSelect").getValue();
			sendData.SEND_ITEM = [{"sendId":yuezhiSelectedValue}];
			sendData.includeSubOdept = true;
			
			paramBean.SEND_DATA = sendData;
		}
		
		paramBean.MIND_DATA = _self.mind.getValue(); //意见数据装配

		var rtnData = FireFly.doAct("SY_WFE_INTEGRATION", "process", {"data":jQuery.toJSON(paramBean)}, false);	
		
		if(rtnData["_MSG_"].indexOf("ERROR,") >= 0) {
			deferred.reject();
		} else {
//			_self._refreshView("处理完毕");
//			_self._parHandler.setParentRefresh();
			_self._parHandler.backClick(true);
			deferred.resolve();
		}
	} else {
		deferred.reject();
	}
    return deferred.promise();
}

/**
 * 重新装载页面
 */
mb.vi.wfCardView.prototype._refreshView = function(msg) {
	var _self = this;
	var viewer = _self._parHandler;
	var url = viewer.servId + ".byid.do?data={_PK_:" + viewer._pkCode ;
	if(_self.isAgent()){
		url += ",_AGENT_USER_:'" + _self.getAgentUserCode() + "'";
	}
	url += "}";
	viewer.setParentRefresh();
	viewer.resetReplaceUrl(url);
	viewer.refresh();
	if (msg) {
		viewer.cardBarTip(msg);
	}
}

/**
 * 获取可同时送交两个节点的配置
 */
mb.vi.wfCardView.prototype.getDevideTwoNode = function () {
	var _self = this;
	
	var devideTwoNode = _self.getAuthBean().DEVIDE_TWO_NODE;
	
	try {
		var devideTwoNode = StrToJson(devideTwoNode);
		
		return devideTwoNode.TARGETNODE;
	} catch(e) {
		return false;
	}
	
	return false;
}


/**
 * 获取表单上定义的数据，卡片js可覆盖该方法
 */
mb.vi.wfCardView.prototype.confirmGetFormData = function () {
	var _self = this;
	
	var formData = {};

	var formDataItems = _self.rhNext.getFormDataItem();
	for (var i=0;i<formDataItems.length;i++) {
		var item = formDataItems[i];
		formData[item.id] = item.getValue();
	}
	
	return formData;
}

/**
 * 根据处理选择的值，去动态的设置弹出框，显示确定的字
 */
mb.vi.wfCardView.prototype.setDialogConfirmName = function () {
	var _self = this;

	if (_self.rhNext.getItem(_self.getWfSelectFieldName())) {
		var currWfSelect = _self.rhNext.getItem(_self.getWfSelectFieldName()).getValue();
		jQuery.each(_self.getAuthBean().S_WF_SELECT, function(key, itemObj){
			if (currWfSelect == itemObj.ITEM_CODE) {
				var btnName = itemObj.VALUE_BTN;
				if (btnName.length == 0) {
					btnName = "确定";
				}
				jQuery("#cmSaveAndSend_dialog").parent().find(".ui-dialog-buttonset").children().first().next().html(btnName);		
			}
		});		
	}
}


/**
 * 注册下一步的改变的事件
 */
mb.vi.wfCardView.prototype.regStepChg = function() {
	var _self = this;
	this.userSelectCount = 0;
	
	//处理选择单选框的valueChange事件
	if (_self.rhNext.getItem(_self.getWfSelectFieldName())) { //存在处理选择
		_self.rhNext.getItem(_self.getWfSelectFieldName()).valueChanged = function(newValue, event) {
			_self.setDialogConfirmName();
			
			//改变下一环节的可送交节点的值
			_self.setOptionsForNextStep();
			
			//处理选择改变之后，将影响送交的过滤
			if (!_self.nextStepChange("", event)){ //第一个下拉框设置成空
				//改变意见定义
				_self.mind.changeGeneralMind();
			}
		}
	}
	
	//送交节点下拉框的选择的valueChange事件
	var nextStepName = WfNextStep.nextStep;
	var userSelectName = WfNextStep.userSelect();
	var deptRoleSelectName = WfNextStep.deptRole();

	_self.rhNext.getItem(nextStepName).valueChanged = function(newValue, event) {
		_self.rhNext.getItem(userSelectName).initValue("", ""); 
		
		_self.nextStepChange(newValue, event);
	}
}

/**
 * 对下一个节点设置能送交的点
 */
mb.vi.wfCardView.prototype.setOptionsForNextStep = function () {
	var _self = this;

	var nextStepArray = new Array(); //下一步可走的节点
	nextStepArray.push({'name':'','value':''});  //默认给一个空的值
	jQuery.each(_self.getNextStepByWfSelect(), function(i, actItem) {
		var stepObj = {};
		stepObj.name = actItem.NODE_NAME;
		stepObj.value = actItem.NODE_CODE;
		nextStepArray.push(stepObj);
	});
	
	_self.rhNext.getItem(WfNextStep.nextStep).setOptions(nextStepArray);
}


/**
 * 根据下一步的节点，弹出的用户选择(送交按钮变化的时候/或者点击重新获取的时候)，不影响pos之前的，pos之后的都置空
 * @param newValue 改变后的新值
 * @param event 事件
 */
mb.vi.wfCardView.prototype.nextStepChange = function(newValue, event) {
	var _self = this;

	//重新构造 pos之后的送交框，将 pos之后的人员选择框置空
	var nextStepName = WfNextStep.nextStep;
	var userSelectName = WfNextStep.userSelect();
	var deptRoleSelectName = WfNextStep.deptRole();
	
	if (newValue == "") { //操作的是当前点 ， 并且选中空白了
		_self.rhNext.getItem(userSelectName).initValue("", "");
	}
	
	//如果之前选择了一个ACT-button,把userSelect remove掉了， 这里需要添加上
	var userSelectName0 = WfNextStep.userSelect();
	
	_self.rhNext.getItem(userSelectName0).$dom.show(); //让处理人显示
	_self.rhNext.getItem(userSelectName0).$chooseDom.show();
	
	//将之前的创建的多个人员的输入删除
	if (_self.userSelectCount > 0) {
		for (var j=1; j<=_self.userSelectCount; j++) {
			var userSelectName = WfNextStep.userSelect(j);
			
			_self.rhNext.getItem(userSelectName).$dom.remove();
			_self.userSelectCount -= 1;
		}
	}
	
	//进行选人的逻辑
	if (newValue != "" && newValue.indexOf("ACT") != 0) {
		//判断 newValue 是不是虚拟出来的 VP-N1-N2 这种可同时送多个节点, 是则创建多个人员的输入项
		if (newValue.indexOf("VP-") == 0) {
			var devideObj = StrToJson(_self.getAuthBean().DEVIDE_TWO_NODE);
			var nodeObjs = devideObj.NODES;
			var firstNode = nodeObjs[0].NODE_CODE;
			
			jQuery.each(devideObj.NODES, function(index, item){
				var userSelectName = WfNextStep.userSelect(index);
				var deptRoleSelectName = WfNextStep.deptRole(index);
				if (index > 0) {
					_self.bldNewUserSelect(item, index);
				} else { //重新绑定获取人员的点击事件
					_self.reBindUserSelectClick(item.NODE_CODE);
				}
			});
			
			_self.selectUserToPos = 0; 
			_self.fetchBinderData(firstNode, event);
		} else {
			_self.userSelectClick(); //重新绑定获取人员的点击事件
			_self.selectUserToPos = 0; //选择的人放到哪个 userSelect的框里去
			_self.fetchBinderData(newValue, event);
		}
		_self.rhNext.getItem(userSelectName0).setNotNull(true);
	} else if (newValue.indexOf("ACT") == 0) { //ACT-button, 则需要将selectUser去掉
		if (_self.rhNext.getItem(userSelectName0)) { //存在
			//先隐藏， 如果这个ACT有返回的数据才去显示选择的用户的框
			_self.rhNext.getItem(userSelectName0).initValue("", "");
			_self.rhNext.getItem(WfNextStep.deptRole()).setValue("");
			
			_self.selectUserToPos = 0; 
			
			//提交请求，获取人员的数据
			_self.fetchBinderData(newValue, event);
		}
	}
	
	if (_self.stepChgCauseExt && typeof(_self.stepChgCauseExt) == "function") {
		_self.stepChgCauseExt(newValue); //送交下一步变化引起的其他变化
		return true;
	}
}

/**
 * 隐藏选人的框
 */
mb.vi.wfCardView.prototype.hideUserSelect = function () {
	var _self = this;

	var userSelectName0 = WfNextStep.userSelect();
	
	_self.rhNext.getItem(userSelectName0).setNotNull(false);
	_self.rhNext.getItem(userSelectName0).$dom.hide();
	_self.rhNext.getItem(userSelectName0).$chooseDom.hide();
	
}

/**
 * 构建一个新的用户选择的框
 * @param vpNodeItem 定义的节点信息
 * @param index 第几个选人的框
 */
mb.vi.wfCardView.prototype.bldNewUserSelect = function (vpNodeItem, index) {
	var _self = this;
	
	var userSelectName = WfNextStep.userSelect(index);
	var deptRoleSelectName = WfNextStep.deptRole(index);
	
	var labelStr = vpNodeItem.USER_LABEL;
	var nodeCode = vpNodeItem.NODE_CODE;
	
	var userSelectObj = {'type':'tree', 'id':userSelectName, 'label':labelStr, 'value':''};
	var deptRoleObj = {'type':'hidden', 'id':deptRoleSelectName, 'label':'', 'value':''};	
	
	_self.rhNext.addItem(userSelectObj, WfNextStep.userSelect(index - 1), WfNextStep.deptRole(index - 1));
	_self.rhNext.addItem(deptRoleObj, WfNextStep.userSelect(index - 1), WfNextStep.deptRole(index - 1));
	_self.userSelectCount += 1;
	
	//动态添加一项之后， 注册事件
	_self.rhNext.getItem(userSelectName).$chooseDom.unbind("click").click(function(event){
		_self.selectUserToPos = index;
		
		//需要除去的人， 该用户选择上面出现的 其他的 用户选择
		var excludeUserId = "";
		for (var j=0;j<index;j++) {
			var userSelectPre = WfNextStep.userSelect(j);
			excludeUserId += _self.rhNext.getItem(userSelectPre).getValue() + ",";
		}
		
		_self.fetchBinderData(nodeCode, event, excludeUserId);
	});	
}


/**
 * 送交下一步变化引起的其他变化 ， 卡片扩展js去处理
 */
mb.vi.wfCardView.prototype.stepChgCauseExt = null;

/**
 * 获取绑定的数据，包括下一步变化，及手动点击的事件
 * @param pos 第几组送交
 * @param nodeCode 送交的节点
 * @param event 事件
 */
mb.vi.wfCardView.prototype.fetchBinderData = function(nodeCode, event, excludeUserId) {
	var _self = this;
	
	//选择pos的送交之后，进行选人的逻辑
	var userSelectId = WfNextStep.userSelect(_self.selectUserToPos);
	if (_self._getWfNextBtn(nodeCode) && _self._getWfNextBtn(nodeCode).dataObj.NODE_USER) { //数据上已经有了用户信息
		var backUserName = _self._getWfNextBtn(nodeCode).dataObj.NODE_USER_NAME;
		var backUserCode = _self._getWfNextBtn(nodeCode).dataObj.NODE_USER;
		_self.rhNext.getItem(userSelectId).initValue(backUserName, backUserCode);
	} else { //去服务端获取数据  先看看之前有选择没有
		var userCodes = _self.rhNext.getItem(userSelectId).getValue();
		var userNames = _self.rhNext.getItem(userSelectId).getName();
		setTimeout(function(){ //使用异步是因为在iPhone5S的chrome上调用有问题
			_self._openSelectOrg(nodeCode, "", event, userCodes, userNames, excludeUserId);
		},0);
	}
}



/**
 * 对下一个节点设置能送交的点
 */
mb.vi.wfCardView.prototype.setOptionsForNextNode = function (newPos) {
	var _self = this;
	
	//已经选择过的节点
	var nodeCodeSelected = "";
	for (var i=0;i<newPos;i++) {
		var nodeCode = _self.rhNext.getItem(WfNextStep.nextStep(i)).getValue();
		
		nodeCodeSelected += "'" + nodeCode + "',";
	}
	
	var nextStepArray = new Array(); //下一步可走的节点
	nextStepArray.push({'name':'','value':''});  //默认给一个空的值
	jQuery.each(_self.getNextStepByWfSelect(), function(i, actItem) {
		if (nodeCodeSelected.indexOf("'" + actItem.NODE_CODE + "'") == -1) { //还没选择过的点	
			var stepObj = {};
			stepObj.name = actItem.NODE_NAME;
			stepObj.value = actItem.NODE_CODE;
			nextStepArray.push(stepObj);
		}
	});
	
	_self.rhNext.getItem(WfNextStep.nextStep(newPos)).setOptions(nextStepArray);
}


/**
 * 选择人员之后, 将选择的人员放到下拉框
 */
mb.vi.wfCardView.prototype.confirmUserSelectSend = function(sendObj, nameArray) {
	var _self = this;
	
	var userSelectItemName = WfNextStep.userSelect(_self.selectUserToPos) ;
	var deptRoleSelectItemName = WfNextStep.deptRole(_self.selectUserToPos);
	
	if (sendObj.TO_TYPE == 3) { //送人
		//将人员的下拉框设置上值
		_self.rhNext.getItem(userSelectItemName).initValue(nameArray.join(","), sendObj.TO_USERS);
		_self.rhNext.getItem(deptRoleSelectItemName).setValue(""); //设置角色为空,避免之前选过送角色了
	} else if (sendObj.TO_TYPE == 1) { //送角色
		_self.rhNext.getItem(userSelectItemName).initValue(nameArray.join(","), sendObj.TO_DEPT);
		_self.rhNext.getItem(deptRoleSelectItemName).setValue(sendObj.TO_ROLE);
	}
};



/**
 * 在构造处理完成 弹出框 之前 准备的 页面数据 , 卡片js可覆盖
 */
mb.vi.wfCardView.prototype.beforeBldNextConfirmWindow = function() {
	var _self = this;
	
	var fieldObjs = new Array();

	if (_self._parHandler.byIdData.FIELD_COMPLETE) {
		var fieldSort = 500;
		jQuery.each(_self._parHandler.byIdData.FIELD_COMPLETE, function(key, itemObj){
			var key = itemObj.ITEM_CODE; //字段名
			
			var itemDef = _self._parHandler._items[key]; //字段定义
			
			var itemType = "input";
			if (itemDef.ITEM_INPUT_TYPE == "1" && itemDef.ITEM_INPUT_MODE == "3" ) { //文本框 && 树形选择
				itemType = "tree";
			} else if (itemDef.ITEM_INPUT_TYPE == "1") { //文本输入
				itemType = "input";
			} else if (itemDef.ITEM_INPUT_TYPE == "2") { //下拉框
				itemType = "select";
			} else if (itemDef.ITEM_INPUT_TYPE == "3") { //单选
				itemType = "radio";
			} else if (itemDef.ITEM_INPUT_TYPE == "4") { //单选
				itemType = "check";
			} else if (itemDef.ITEM_INPUT_TYPE == "5") { //大文本
				itemType = "textarea";
			}
			
			var itemValue = itemObj.VALUE;
			var fieldItemValue = _self._parHandler.getItem(key).getValue();
			if (fieldItemValue.length > 0) { //如果之前该字段已经保存了值了，就用之前保存的值
				itemValue = fieldItemValue;
			}
			
			var notnull = false; //设置的是否必填
			if (itemObj.MUST == "1") {
				notnull = true;
			}
			
			var itemInputObj = {'type':itemType, 'id': WfNextStep.fieldPrefix + key, 'label':itemObj.ITEM_NAME, 'value':itemValue, 'sort':fieldSort, 'notnull': notnull};
			if (itemType == "tree") { //树形结构
				itemInputObj.dictConfig = itemDef.ITEM_INPUT_CONFIG;
			} 
			fieldObjs.push(itemInputObj);
			
			fieldSort = fieldSort + 10;
		});		
	}

	return fieldObjs;
};

/**
 * 在构造处理完成 弹出框 之后要做的相关事情 , 卡片js可覆盖
 */
mb.vi.wfCardView.prototype.afterBldNextConfirmWindow = function() {
};


/**
 * 构造处理选择的单选
 */
mb.vi.wfCardView.prototype.bldWfSelect = function(opts) {
	var _self = this;

	if (_self.getAuthBean().S_WF_SELECT && _self.getAuthBean().S_WF_SELECT.length > 0) { //存在处理选择
		var wfSelectArray = new Array();
		var defaultVlaue;
		jQuery.each(_self.getAuthBean().S_WF_SELECT, function(key, itemObj){
			var wfSelect = {};
			wfSelect.name = itemObj.ITEM_NAME;
			wfSelect.value = itemObj.ITEM_CODE;
			
			wfSelectArray.push(wfSelect);
			if (key == 0) {
				defaultVlaue = itemObj.ITEM_CODE;
			}
		});
		
		var selectBaseObj = _self.getAuthBean().S_WF_SELECT_BASE;
		var type = selectBaseObj.TYPE_CODE;
		
		var wfSelectObj = {'type':type, 'id':_self.getWfSelectFieldName(), 'label':selectBaseObj.LABEL_TEXT, 'value':defaultVlaue, 'options': wfSelectArray, 'sort':180, 'notnull':true};
		opts.push(wfSelectObj);
	}
}


/**
 * 构造一个送交的元素 ， 只有送交的节点需要在前一个节点上的基础上过滤
 */
mb.vi.wfCardView.prototype.bldNextStep = function(opts) {
	var _self = this;

	var defaultNodeValue = ""; //默认节点， 节点上配置的处理选择列表的第一个
	if (_self.getAuthBean().S_WF_SELECT && _self.getAuthBean().S_WF_SELECT.length > 0) {
		defaultNodeValue = _self.getAuthBean().S_WF_SELECT[0].VALUE_NODE; //定义的送交点
		defaultNodeValue = "," + defaultNodeValue + ",";
		jQuery.each(_self.getAuthBean().S_WF_SELECT[0].ACT_BTN_LIST, function(index, item){ //定义的ACT-button
			defaultNodeValue += item.ACT_CODE + ",";
		});
	}
	
	var nextStepArray = new Array(); //下一步可走的节点
	nextStepArray.push({'name':'','value':''});  //默认给一个空的值
	jQuery.each(_self.getNextStepBean(), function(i, actItem) {
		var stepObj = {};
		stepObj.name = actItem.NODE_NAME;
		stepObj.value = actItem.NODE_CODE;
		//如果有处理选择，则第一个送交的节点列表，通过处理选择过滤
		if (_self.getAuthBean().S_WF_SELECT && _self.getAuthBean().S_WF_SELECT.length > 0) { //有处理选择的定义
			var nodeCode = actItem.NODE_CODE.replace("R", "");
			if (defaultNodeValue.length > 0 && defaultNodeValue.indexOf("," + nodeCode + ",") >= 0) {
				nextStepArray.push(stepObj);
			}
		} else {
			nextStepArray.push(stepObj);
		}

		var wfBtnObj = {};
		wfBtnObj.dataObj = actItem;

		_self.wfNextBtns[actItem.NODE_CODE] = wfBtnObj;	
	});
	
	//循环去添加下一步的对象
	var nextStepSort = 200;
	var notnull = true;
	var store_option = true;
	
	var nextStepName = WfNextStep.nextStep;
	var userSelectName = WfNextStep.userSelect();
	var deptRoleSelectName = WfNextStep.deptRole();
	
	var nextStepObj = {'type':'select', 'id':nextStepName, 'label':'下一环节', 'value':'', 'options': nextStepArray, 'sort':nextStepSort, 'store_option':store_option, 'notnull':notnull};
	nextStepSort += 20;
	var userSelectObj = {'type':'tree', 'id':userSelectName, 'label':'处理人', 'value':'', 'sort':nextStepSort, 'notnull':notnull};
	nextStepSort += 20;
	var deptRoleObj = {'type':'hidden', 'id':deptRoleSelectName, 'label':'', 'value':'', 'sort':nextStepSort}
	nextStepSort += 20;
	
	opts.push(nextStepObj);
	opts.push(userSelectObj);
	opts.push(deptRoleObj);
};


/**
 * 公开获取rhnext对象的方法, 在卡片扩展js中可以去设置 处理完毕页面上的元素
 */
mb.vi.wfCardView.prototype.getRhNext = function() {
	var _self = this;
	
	return _self.rhNext;
};


/**
 * 从所有可送交的节点中过滤出满足条件的子集
 */
mb.vi.wfCardView.prototype.getNextStepByWfSelect = function() {
	var _self = this;
	
	if (_self.getAuthBean().S_WF_SELECT.length == 0) {
		return _self.getNextStepBean();
	}

	//当前的 处理选择
	var currWfSelect = _self.rhNext.getItem(_self.getWfSelectFieldName()).getValue();
	var canSendNodes = "";
	jQuery.each(_self.getAuthBean().S_WF_SELECT, function(key, itemObj){
		if (itemObj.ITEM_CODE == currWfSelect) {
			canSendNodes = "," + itemObj.VALUE_NODE + ",";  //节点上定义的可送交节点 + 定义的ACT
			if (itemObj.ACT_BTN_LIST) {
				jQuery.each(itemObj.ACT_BTN_LIST, function(j, actItem){
					canSendNodes += actItem.ACT_CODE + ",";
				});
			}
		}
	});
	
	var rtnArray = new Array();
	jQuery.each(_self.getNextStepBean(), function(i, actItem) {
		var nodeCode = actItem.NODE_CODE.replace("R", "");
		if (canSendNodes.indexOf("," + nodeCode + ",") >= 0) { //在可送交的节点中
			rtnArray.push(actItem);
		}
	});
	
	return rtnArray;
}


/**
 * 获取处理选择在节点定义上填写的字段名
 */
mb.vi.wfCardView.prototype.getWfSelectFieldName = function() {
	var _self = this;
	
	var wfSelectBaseField = "wfSelectId";
	if (_self.getAuthBean().S_WF_SELECT_BASE) {
		wfSelectBaseField = _self.getAuthBean().S_WF_SELECT_BASE.FIELD_CODE;
	}
	
	return wfSelectBaseField
}

/**
 * 流程下一步的常量, 用于构造处理完毕时，送交的下一步和选人的页面元素的ID值 
 * 最后的ID类似 nextStepSelect0/nextStepSelect1/nextStepSelect2
 */
var WfNextStep = {
	nextStep : function(pos) {
		return "nextStepSelect" + (pos || "0");
	},
	userSelect : function(pos) {
		return "userSelect" + (pos || "0");
	},
	deptRole : function(pos) {
		return "deptRoleSelect" + (pos || "0");
	},
	wfSelectId : "wfSelectId",
	FREE_NODE : "NODEX",
	fieldPrefix : "FIELD_COMPLETE_"
};

/**
 * 判断当前发送的人有没出部门
 * 
 * @param idArray
 *            树回写的id数组
 * @param nameArray
 *            树回写的name数组
 */
mb.vi.wfCardView.prototype.callback = function(idArray, nameArray) {
	return true;
}