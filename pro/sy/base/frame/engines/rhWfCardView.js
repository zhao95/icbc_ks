GLOBAL.namespace("rh.vi");

rh.vi.wfCardView = function(options) {
	var defaults = {
		"id" : options.sId + "-viWfCardView",
		"sId" : "",// 服务ID
		"pId" : options.sId,
		"parHandler" : null,// 主卡片的句柄
		"pkCode" : ""
	};
	var _self = this;
	this.opts = jQuery.extend(defaults, options);
	this._parHandler = this.opts.parHandler;
	this._nextStepNodeCode = "";

	this.procInstId = this._parHandler.itemValue("S_WF_INST");
	this.wfState = this._parHandler.itemValue("S_WF_STATE");

	this.reqdata = {};
	this.reqdata["PI_ID"] = this.procInstId; // 流程实例ID
	this.reqdata["INST_IF_RUNNING"] = this.wfState; // 流程是否在运行
	this.reqdata["DATA_ID"] = this._parHandler._pkCode; 
	if (!(this.getNodeInstBean() == undefined)) {
		this.reqdata["NI_ID"] = this.getNodeInstBean().NI_ID;
	}

	//增加委托用户USER_CODE	
	this.appendAgentUserFlagTo(this.reqdata);
	
	this.wfBtns = {};
	this.wfNextBtns = {};
	this.wfNextBtnNames = {};
	this.existEditBtn = false;
	this.isSendState = false; // 按钮状态/发送状态(已经点了完成并发送了)
//	var penRefresh = jQuery(
//			"<a id='parent-refresh' style='display:none;' href='javascript:void(0);'>刷新列表</a>")
//			.appendTo(this._parHandler.btnBar);
//	penRefresh.bind("click", function() {
//		_self._parHandler.refreshFile();
//	});

	// 刷新首页，解决保存之后，首页不刷新
	this._refreshMainPage();

	
	// 意见默认值
	this._mindDefaultValues = {};
	
	
	return this;
};

/**
 * 邮件待办处理完毕跳转待办列表
 */
rh.vi.wfCardView.prototype.todoListJmp = function(){
	var _self = this;
	var urlParam = window.location.search;
	if(urlParam == null || urlParam.indexOf("fromType=mail") <0){
		_self._parHandler.backClick();
		return;
	}
	var id = 'S_CODE';
	var serverId = this._parHandler.servId;
	//todo 判断是否邮件打开&开关,oisUrl、ssicId、businessId
	var preValue = System.getVar('@C_SERV_JUMP_TODO_PRE@')
	var specServItem = System.getVar('@C_SERV_JUMP_TODO_ITEM@');
	var oisUrlFlowc = System.getVar('@C_TODO_OISURL@');
	var oisUrl = System.getVar('@C_OISURL@');
	var ssicId = System.getVar('@LOGIN_NAME@');
	var isItem = specServItem.indexOf(serverId);
	if(isItem>=0){
		var specServStr = specServItem.split(",");
		for(var i = 0;i<specServStr.length;i++){
			if(specServStr[i].indexOf(serverId)>=0){
				var arr = specServStr[i].split("#");
				if(arr.length == 2){
					id = arr[1];
					break;
				}else{
					_self._parHandler.backClick();
					return;
				}
			}
		}
	}else {
		var servIdPre = serverId.split("_")[0];
		if(preValue.indexOf(servIdPre)<0){
			_self._parHandler.backClick();
			return;
 		}
	}
	var businessId= this._parHandler.itemValue(id);
	var requestUrl = oisUrlFlowc + '&ssicId=' + ssicId + '&businessId=' + businessId;
	jQuery.ajax({
		url: requestUrl,
		type: "GET",
		dataType: "jsonp",
		async: false,
		data: {},
		success: function(data){
			try {
				if (data.totalNum>=0){
					var jmpUrl = oisUrl+'/index.jsp?jumpback=1&userId='+ ssicId;
					window.location.href = jmpUrl;
//					window.open(jmpUrl,"newwindow");	
				}
//				_self._parHandler.backClick();
			} catch(e){
				throw e;
			}
		},
		error: function(){
			_self._parHandler.backClick();
		}
	});
}

/**
 * 获取卡片对象
 */
rh.vi.wfCardView.prototype.getParHandler = function() {
	return this._parHandler;
};

/**
* 是否流程已启动
**/
rh.vi.wfCardView.prototype.isWorkflow = function(){
	var _self = this; 
	if (typeof(_self._parHandler.getItem("S_WF_INST")) != "undefined" 
			&& _self._parHandler.getItem("S_WF_INST").getValue().length >0 ) {
		return true;
	}
	return false;
}

rh.vi.wfCardView.prototype.render = function() {
	var _self = this;
	//如果存在忽略流程参数，则不加载流程信息。
	if(_self._parHandler.params && _self._parHandler.params["_IGNORE_WF_INFO_"]){
		return;
	}
	//判断是否挂了流程
	if (typeof(_self._parHandler.getItem("S_WF_INST")) != "undefined" 
		&& _self._parHandler.getItem("S_WF_INST").getValue().length >0 ) {
		
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
//				alert("系统配置：按钮条渲染方式SY_WF_BTN_RENDER配置值有误，请检查！");
				alert(Language.transStatic("rhWfCardView_string1"));
			}
		}
		
		//初始化按钮对象
		_self.wfActBtns = this.getButtonBean();//操作按钮
		_self.wfSendBtns = this.getNextStepBean();//流转按钮
		_self.wfActBtnGroups = {}; //按钮组对象
		_self.wfSendBtnGroup = []; //流程按钮组
		
		//以审批单已经构造好的按钮条作为按钮条继续使用
		this._btnBar = this._parHandler.btnBar;
		
		//构建工作流操作按钮
		this._bldBtnBar();
		
		//处理流程按钮
		if(_self.btnRenderMode == 0 ||_self.btnRenderMode == 100){ //流程按钮平铺
			this._bldNextBtnBar(_self.wfSendBtns);
		}else if(_self.btnRenderMode == 10 || _self.btnRenderMode == 110){ //流程按钮下拉组
			this._bldNextBtnBar(_self.wfSendBtns);
		}else{ //构建流程按钮单独按钮条			
			_self.nextBtnBar = jQuery("<div id='nextBtnBar'></div>").appendTo(this._btnBar);
		}
	
		//针对下拉组渲染方式处理隐藏
		if(_self.btnRenderMode != 0 && _self.btnRenderMode != 11 && _self.btnRenderMode != 12){
			jQuery("body").bind("click",function(){
				_self._hideAllBtnGroup();
			});
		}
		
		//处理文件控制
		this._doFiledControl();

		var opts = {};
		opts["wfHandler"] = this;
		//this.wfRelate = new rh.vi.relateFiles(opts);

		/** 显示分发列表
		if (this.getDisplayMode() != "MODE_BASE"
				&& this.getAuthBean().isShowFenfaList == "true") {
			this._bldSendList();
		}
		*/

		// 判断是否锁定 1,锁定，2正常 ， 锁定的文件，全部只读
		if (this.getAuthBean().lockState == "1") {
			this._parHandler.form.disabledAll();
			if(this._getBtn("save")){
				this._getBtn("save").layoutObj.hide();
			}
			_self._entirelyControl = true;
//			this._parHandler.headMsg("文件已锁定!");
			this._parHandler.headMsg(Language.transStatic("rhWfCardView_string2"));
		}

		if (this._parHandler.itemValue("S_FLAG") == "2") { // 废止
//			this._parHandler.headMsg("流程已终止，需在废件箱中恢复");
			this._parHandler.headMsg(Language.transStatic("rhWfCardView_string3"));
		}
		
		//初始化意见
		_self.initMind();
		
		//设置文件权限
	    jQuery(_self.getAuthBean().nodeFileControl).each(function(index, fileDef){
	    	var fileItem = _self._parHandler.getItem(fileDef.ID);
	    	if (fileItem) {
	    		if (_self.isLocked()) {
	    			//fileItem.setAcl && fileItem.setAcl(1);
	    		} else {
	    			if (Browser.systems().windows) {
	    				fileItem.setAcl && fileItem.setAcl(fileDef.VALUE);
	    			} else { // 需要检查有没有编辑和上传权限
	    				var totalLen = 6; // 权限2进制总位数
	    				var aclArr = [false,false,false,false,false,false];
	    				var acl = parseInt(fileDef.VALUE);
	    				// 把十进制数字转化成二进制字符串，对应转化为布尔数组，例如0x101010变成[true,false,true,false,true,false]
	    				var aclStr = (parseInt(acl) & 63).toString(2);
	    				var len = aclStr.length;
	    				// 不够6位补零
	    				var tmpLen = totalLen - len;
	    				for (var i = 0; i < tmpLen; i++) {
	    					aclStr = "0" + aclStr;
	    				}
	    				len = totalLen;
	    				for (var i = 0; i < len; i++) {
	    					var tmp = aclStr.substring(i, i + 1);
	    					if (tmp.toString() == "1") {
	    						aclArr[i] = true;
	    					} 
	    				}
	    				if (aclArr[0]) {
	    					// 移动平台隐藏下载按钮
	    					if (Browser.isMobileOS()) { 
	    						acl -= 32;
		    				}
	    				}
	    				if (aclArr[3]) {
	    					// 移动平台都隐藏掉上传按钮
	    					if (Browser.isMobileOS()) { 
	    						acl -= 4;
		    				}
	    				}
	    				if (aclArr[4]) { // 具有编辑权限则去除编辑权限
	    					acl -= 2;
	    				} 
	    				fileItem.setAcl && fileItem.setAcl(acl);
	    			}
	    		}
	    	}
	    });	
	    
	    //考虑优化，有修改时才刷新
	    _self._parentRefreshFlag = true;			
	} else { 
		if (_self._parHandler._actVar == UIConst.ACT_CARD_ADD) { //添加模式，隐藏意见分组框
			var mindGroup = _self._parHandler.form.getGroup(UIConst.MIND_FIELDSET);
			if (!mindGroup) {
		        mindGroup = _self._parHandler.form.getGroup(null, UIConst.FITEM_ELEMENT_MIND);
			}
			if (mindGroup) {
				mindGroup.hide();
			}
		}
	}
	//设置卡片title显示
	if (_self.getAuthBean() && _self.getAuthBean().userDoInWf == "true") {
		_self._parHandler.setTitle(_self._parHandler.getTitle() + " &gt; " + _self.getNodeInstBean().NODE_NAME);
	}
};

/*
 * 获取按钮
 */
rh.vi.wfCardView.prototype.getButtonBean = function() {
	var _self = this;
	return _self._parHandler.byIdData.buttonBean;
};

/*
 * 获取下一步的节点列表数据
 */
rh.vi.wfCardView.prototype.getNextStepBean = function() {
	var _self = this;

	if (_self._parHandler.byIdData.nextSteps == undefined
			|| _self._parHandler.byIdData.nextSteps == "undefined") {
		return "";
	}
	
	var wfSendBtns = _self._parHandler.byIdData.nextSteps;
	
	
	//柴志强：暂时前端进行按钮排序
	if (wfSendBtns && $.isArray(wfSendBtns)) {
		//返回按钮组
		$.each(wfSendBtns, function(index, btn) {
			if (!btn.NODE_ORDER || btn.NODE_ORDER == 0) {
				if (btn.NODE_CODE.indexOf("VP") >= 0) { //VP
					btn.NODE_ORDER = 90;
				} else if (btn.NODE_CODE.indexOf("ACT") >= 0) { //ACT
					btn.NODE_ORDER = 80;
				} else if (btn.NODE_CODE.indexOf("R") >= 0) { //R
					btn.NODE_ORDER = 10;
				} else {
					btn.NODE_ORDER = 100;
				}
			}
		});
		//排序
		wfSendBtns.sort(function(btn1, btn2) {
			return btn2.NODE_ORDER - btn1.NODE_ORDER;
		});
		
		return wfSendBtns;
	} else {
		return "";
	}
};

/**
 * 构建按钮组
 * name 组按钮名称
 * code 组按钮编码
 * 
 */
rh.vi.wfCardView.prototype._bldBtnGroup = function(name, code, next, actItem) {
	var _self = this;
	//构建按钮容器a标签
	var temp = jQuery("<a></a>").addClass("rh-icon").addClass("rhGrid-btnBar-a rhCard-btnBar-wf");
	temp.attr("id", GLOBAL.getUnId(code, _self.opts.sId));
	temp.attr("actcode", code);
	//构建按钮文字span标签
	var labelName = jQuery("<span></span>").addClass("rh-icon-inner").text(name);
	temp.append(labelName);
	//构建按钮图标span标签
	var icon = jQuery("<span></span>").addClass("rh-icon-img").addClass("btn-budeng");
	temp.append(icon);
	//更多下拉图标
	var moreIcon = jQuery("<span></span>").addClass("rh-icon-img-more");
	labelName.append(moreIcon);
	var btnGroupCon = jQuery("<div class='rh-icon-groupCon'></div>");
	btnGroupCon.appendTo(temp);
	
	//插入按钮
	if(_self.btnRenderMode >= 100){ //针对其他操作组特殊处理其顺序
		var otherGroup = jQuery("#funcBtnDivCon").find("a[actcode='OTHER_ACT_GROUP']");
		if(otherGroup.length == 1){
			temp.insertBefore(otherGroup);
		}else{
			temp.appendTo(jQuery("#funcBtnDivCon"));
		}
	}else{ //通用
		temp.appendTo(jQuery("#funcBtnDivCon"));
	}
	
	if(next){ //特殊处理“完成并发送”按钮
		_self._act(actItem.ACT_CODE, actItem.SERV_ID, temp, actItem);
	}else{ //将按钮组的父按钮加入到平铺按钮对象中
		_self.wfBtns[code] = {"layoutObj":temp};
	}

	//绑定事件
	temp.bind("click",function(event){
		var btnGroupObj = jQuery(this).find(".rh-icon-groupCon");
		if(btnGroupObj.css("display") == "none"){
			_self._showBtnGroup(btnGroupObj);
		} else {
			_self._hideBtnGroup(btnGroupObj);
		}
		event.stopPropagation();
	});
	
	return btnGroupCon;
}

/**
 * 显示按钮组
 * 组对象
 */
rh.vi.wfCardView.prototype._showBtnGroup = function(groupObj) {
	this._hideAllBtnGroup();
	groupObj.show();
}

/**
 * 隐藏按钮组
 * 组对象
 */
rh.vi.wfCardView.prototype._hideBtnGroup = function(groupObj) {
	groupObj.hide();
}

/**
 * 关闭当前所有按钮组
 * 组对象
 */
rh.vi.wfCardView.prototype._hideAllBtnGroup = function() {
	jQuery(".rh-icon-groupCon").hide();
}

/**
 * 常规方式渲染按钮
 * btnDef 按钮定义
 * btnsCon 按钮容器
 * @param isSort 普通按钮是否需要排序
 */
rh.vi.wfCardView.prototype._bldNormalBtn = function(btnDef, btnsCon, isSort) {
	var _self = this;
	var code = btnDef.NODE_CODE || btnDef.ACT_CODE;
	var name = btnDef.NODE_NAME || btnDef.ACT_NAME;
	var css = btnDef.ACT_CSS;
	var wfclass = btnDef.NODE_CODE ? "wfNext" : "wf";
	var order = btnDef.ACT_ORDER; // 按钮排序号
		
	//构建按钮容器a标签
	var temp = jQuery("<a></a>").addClass("rh-icon").addClass(
			"rhGrid-btnBar-a rhCard-btnBar-" + wfclass);
	temp.attr("id", GLOBAL.getUnId(code, _self.opts.sId));
	temp.attr("actcode", code);
	if (order) {
		temp.attr("order", order); // 排序号放到页面
	}
	//工作流按钮名称上输入有空格,则取空格后部分
	if(btnDef.NODE_NAME){
		var nodeNameSplitArray = btnDef.NODE_NAME.split(" ");
		if (nodeNameSplitArray.length > 1) {
			name = btnDef.NODE_NAME = nodeNameSplitArray[1];
		}
	}
	//构建按钮文字span标签
	var labelName = jQuery("<span></span>").addClass("rh-icon-inner").text(
			name);
	temp.append(labelName);
	//构建按钮图标span标签
	var icon = jQuery("<span></span>").addClass("rh-icon-img").addClass(
			"btn-" + css);
	temp.append(icon);
	//插入按钮
	if (_self.btnRenderMode >= 100) { //针对其他操作组特殊处理其顺序
		var otherGroup = btnsCon.find("a[actcode='OTHER_ACT_GROUP']");
		if (otherGroup.length == 1) {
			temp.insertBefore(otherGroup);
		} else {
			if (isSort && order >= 0)  {// 需要和表单按钮混合排序
				this._parHandler.addBtn(temp, order);
			} else { 
				temp.appendTo(btnsCon);
			}
		}
	} else { // 通用
		if (isSort && order >= 0)  {// 需要和表单按钮混合排序
			this._parHandler.addBtn(temp, order);
		} else { 
			temp.appendTo(btnsCon);
		}
	}
	
	_self._addStyleForBtn(code, temp);
	
	return temp;
}

/**
 * 插入组方式渲染按钮
 * btnDef 按钮定义
 * btnsCon 按钮容器
 */
rh.vi.wfCardView.prototype._bldGroupBtn = function(btnDef, btnsGroup) {
	var _self = this;
	var code = btnDef.NODE_CODE || btnDef.ACT_CODE;
	var name = btnDef.NODE_NAME || btnDef.ACT_NAME;
	var css = btnDef.ACT_CSS;
	var wfclass = btnDef.NODE_CODE ? "wfNext" : "wf";
		
	//构建按钮容器a标签
	var temp = jQuery("<a></a>").addClass("rh-icon").addClass(
			"rhGrid-btnBar-a rhCard-btnBar-" + wfclass);
	temp.attr("id", GLOBAL.getUnId(code, _self.opts.sId));
	temp.attr("actcode", code);
	//工作流按钮名称上输入有空格,则取空格后部分
	if(btnDef.NODE_NAME){
		var nodeNameSplitArray = btnDef.NODE_NAME.split(" ");
		if (nodeNameSplitArray.length > 1) {
			name = btnDef.NODE_NAME = nodeNameSplitArray[1];
		}
	}
	//构建按钮文字span标签
	var labelName = jQuery("<span></span>").addClass("rh-icon-inner").text(
			name);
	temp.append(labelName);
	//构建按钮图标span标签
	var icon = jQuery("<span></span>").addClass("rh-icon-img").addClass(
			"btn-" + css);
	temp.append(icon);
	//插入按钮
	temp.appendTo(btnsGroup);
	
	_self._addStyleForBtn(code, temp);
	
	return temp;
}

/**
 * 对按钮添加样式
 */
rh.vi.wfCardView.prototype._addStyleForBtn = function(code, btnObj) {
	var _self = this;	
	
	//如果已经分发过， 改变按钮样式
	if (code == "cmFenFa" && _self._parHandler.itemValue("SEND_FLAG") == "1") {
		btnObj.addClass("fblue");
	}
}

/**
 * 插入组方式渲染按钮2(旧方式)
 * btnDef 按钮定义
 * btnsCon 按钮容器
 */
rh.vi.wfCardView.prototype._bldGroupBtn_OLD = function(btnDef, btnsGroup) {
	var _self = this;
	var code = btnDef.NODE_CODE || btnDef.ACT_CODE;
	var name = btnDef.NODE_NAME || btnDef.ACT_NAME;
	var css = btnDef.ACT_CSS;
	var wfclass = btnDef.NODE_CODE ? "wfNext" : "wf";
		
	//构建按钮容器a标签
	var temp = jQuery("<a></a>").addClass("rh-icon").addClass(
			"rhGrid-btnBar-a rhCard-btnBar-" + wfclass);
	temp.attr("id", GLOBAL.getUnId(code, _self.opts.sId));
	temp.attr("actcode", code);
	//工作流按钮名称上输入有空格,则取空格后部分
	if(btnDef.NODE_NAME){
		var nodeNameSplitArray = btnDef.NODE_NAME.split(" ");
		if (nodeNameSplitArray.length > 1) {
			name = btnDef.NODE_NAME = nodeNameSplitArray[1];
		}
	}
	//构建按钮文字span标签
	var labelName = jQuery("<span></span>").addClass("rh-icon-inner").text(
			name);
	temp.append(labelName);
	//构建按钮图标span标签
	var icon = jQuery("<span></span>").addClass("rh-icon-img").addClass(
			"btn-" + css);
	temp.append(icon);
	//插入按钮
	temp.appendTo(btnsGroup);
	
	return temp;
};

/*
 * 构建卡片按钮条, 表单按钮 节点定义转换之后的按钮（构建操作按钮）
 */
rh.vi.wfCardView.prototype._bldBtnBar = function() {
	var _self = this;
	
	//构建普通按钮容器(下拉渲染模式的按钮组不使用此容器)
	var funcBtnDivCon = jQuery("<div id='funcBtnDivCon'></div>");
	funcBtnDivCon.appendTo(_self._btnBar);
	
	//隐藏非工作流的保存按钮
	jQuery("#" + GLOBAL.getUnId("save", this.opts.pId)).hide();

	//按钮渲染
	jQuery.each(_self.wfActBtns, function(i, actItem) {
		if (Browser.ignoreButton(actItem)) {
			return;
		}
		
		if (actItem.ACT_GROUP == "1") { //编辑组按钮存在
			_self.existEditBtn = true
		}
		
		//按渲染模式处理
		if(_self.btnRenderMode < 100){ //操作按钮平铺渲染，不包括完成并发送按钮
			if(actItem.ACT_CODE == "cmSaveAndSend"){ //“完成并发送”按钮
				if(_self.btnRenderMode == 0){ //平铺
					return true;
				}else if(_self.btnRenderMode == 10){ //下拉组
					if (_self.wfSendBtns && _self.wfSendBtns.length > 0) {
						_self.saveAndSendGroup = _self._bldBtnGroup(actItem.ACT_NAME, actItem.ACT_CODE, true, actItem);
					}
					return true;
				}else{ //单独流程条或弹出
					/*继续正常渲染*/
				}
			}else{
				/*继续正常渲染*/
			}
		}else{ //非平铺操作按钮（操作按钮下拉组渲染，不包括完成并发送按钮）		
			if(actItem.ACT_CODE == "cmSaveAndSend"){ //“完成并发送”按钮
				if(_self.btnRenderMode == 100){ //平铺
					return true;
				}else if(_self.btnRenderMode == 110){ //下拉组
					if (_self.wfSendBtns && _self.wfSendBtns.length > 0) {
					_self.saveAndSendGroup = _self._bldBtnGroup(actItem.ACT_NAME, actItem.ACT_CODE, true, actItem);
					}
					return true;
				}else{ //单独流程条或弹出
					/*继续正常渲染*/
				}
			}else{ //普通按钮
				var groupCode = actItem.GROUP_CODE;
				var btnsGroup = (_self.wfActBtnGroups)[groupCode];
				if(groupCode && typeof (btnsGroup) == "undefined"){
					(_self.wfActBtnGroups)[groupCode] = _self._bldBtnGroup(actItem.GROUP_NAME, groupCode, false, actItem);
					btnsGroup = (_self.wfActBtnGroups)[groupCode];
					btnsGroup.btns = [];
				}
			}			
		}
			
		//转义js删除关键字
		if (actItem.ACT_CODE == "delete") {
			actItem.ACT_CODE = "deleteDoc";
		}
		
		//渲染
		if(groupCode) {
			//插入组方式渲染
			var btn = _self._bldGroupBtn(actItem, btnsGroup);
			btnsGroup.btns.push(btn);
		} else {
			//普通方式渲染
			var btn = _self._bldNormalBtn(actItem, funcBtnDivCon, true);
		}
		
		//绑定事件
		_self._act(actItem.ACT_CODE, actItem.SERV_ID, btn, actItem);
		
		//处理会商提醒
		if (actItem.ACT_CODE == "stopParallelWf") {
			var headMsg = System.getVar("@C_SH_WF_HEAD_MSG@");
			if(headMsg){ //有系统配置则显示
				_self._parHandler.headMsg(headMsg);
			}
		}
	});
	
	if (_self.afterLoadBtn && typeof (_self.afterLoadBtn) == "function") {
		_self.afterLoadBtn();
	}
};

/*
 * 构建下一步卡片按钮条（构建流程按钮）
 */
rh.vi.wfCardView.prototype._bldNextBtnBar = function(_nextData) {
	var _self = this;
	if (!_nextData || _nextData.length == 0) {
		return;
	}
	
	_self.nextBtnCon = jQuery("<div id='nextBtnCon'></div>");
	
	if(_self.btnRenderMode == 0 || _self.btnRenderMode == 100){	//平铺
		var firstBtn = jQuery("a:visible:first", _self._btnBar);
		if(firstBtn.attr("actcode") == "save"){
			_self.nextBtnCon.insertAfter(firstBtn);			
		}else{
			_self.nextBtnCon.insertBefore(firstBtn);			
		}
	}else if(_self.btnRenderMode == 10 || _self.btnRenderMode == 110){ //下拉组
		_self.nextBtnCon = _self.saveAndSendGroup;
	}else { //单独按钮条或弹出
		_self.nextBtnCon.appendTo(_self.nextBtnBar);
	}
	
	jQuery.each(_nextData, function(i, actItem) {
		if (Browser.ignoreButton(actItem)) {
			return;
		}
		actItem.ACT_CSS = "deliver"
		if(_self.btnRenderMode == 0 || _self.btnRenderMode == 100){			
			//普通方式渲染按钮
			var temp = _self._bldNormalBtn(actItem, _self.nextBtnCon);
		}else if(_self.btnRenderMode == 10 || _self.btnRenderMode == 110){
			//插入组方式渲染按钮
			var temp = _self._bldGroupBtn(actItem, _self.nextBtnCon);
			_self.wfSendBtnGroup.push(temp);
		}else{
			//单独按钮条或弹出方式渲染按钮
			var temp = _self._bldNormalBtn(actItem, _self.nextBtnCon);
		}		
		
		if (typeof (actItem.NODE_USER) != "undefined") { // 如果是返回XX的按钮，取得返回人的CODE			
			var nextBtnObj = {};
			nextBtnObj.layoutObj = temp;
			nextBtnObj.dataObj = actItem;
			_self.wfNextBtns[actItem.NODE_CODE] = nextBtnObj;

			_self.wfNextBtnNames[actItem.NODE_NAME] = nextBtnObj;

			temp.bind("click", function() {
				_self._parHandler.shield();
				var idArray = [ "usr:" + actItem.NODE_USER ];
				var nameArray = [ actItem.NODE_NAME ];
				_self.binderType = "USER";
				
				_self._parHandler.saveMind();  //保存意见
				
				if (_self._beforeSongjiao() == false
						|| _self._mindMust() == false
						|| _self.callback(idArray, nameArray) == false) { // 先执行送交前的代码，比如必填意见
					_self._parHandler.shieldHide(); 
					return false;
				}
				var backNodeCode = actItem.NODE_CODE;
				if (backNodeCode.indexOf("R") == 0) {
					backNodeCode = backNodeCode.replace("R", "");
				}
				_self.reqdata["NODE_CODE"] = backNodeCode;
				_self.reqdata["TO_USERS"] = actItem.NODE_USER;
				_self.reqdata["TO_TYPE"] = "3";
				var newActInst = rh_processData("SY_WFE_PROC.toNext.do",
						_self.reqdata);
				if(Tools.actIsSuccessed(newActInst)){
					var namesStr = _self.isExistAgent(actItem.NODE_USER, "3");
					if (namesStr != "") {
//						Tip.show("已经成功送交 " + actItem.NODE_NAME + "(" + namesStr + ")", true);
						Tip.show(Language.transStatic("rhWfCardView_string4") + actItem.NODE_NAME + "(" + namesStr + ")", true);
					} else {
//						Tip.show("已经成功送交 " + actItem.NODE_NAME + "(" + actItem.NODE_USER_NAME + ")", true); //IE7 有问题
						Tip.show(Language.transStatic("rhWfCardView_string4") + actItem.NODE_NAME + "(" + actItem.NODE_USER_NAME + ")", true); //IE7 有问题
					}
					try {
						top.Tip.clear();
					} catch (e) {
						Tip.clear();
					}
					_self._parHandler.shieldHide();
					// 关闭当前页面
					_self._parHandler.backClick();
				}else{
//					alert("送交错误！");
					alert(Language.transStatic("rhWfCardView_string5"));
					_self._parHandler.shieldHide(); 
				}
			});

		} else {
			temp.bind("click", function(event) {
				var ifConfirm = true;
				if (actItem.CONFIRM_MSG) {
					ifConfirm = confirm(actItem.CONFIRM_MSG);
				}

				if (!ifConfirm)
					return;
				// 如果条件表达式没有通过则不能送交，且弹出提示
				if (actItem.COND_MSG && actItem.NOT_MEET_COND
						&& actItem.NOT_MEET_COND == "true") {
					alert(actItem.COND_MSG);
					return;
				}

				_self._openSelectOrg(actItem.NODE_CODE, temp, event);
			});

			if (actItem.COND_MSG && actItem.NOT_MEET_COND
					&& actItem.NOT_MEET_COND == "true") {
				temp.attr("disabled", "true");
				temp.attr("title", actItem.COND_MSG);
				temp.removeClass("rh-icon");
				temp.addClass("rh-icon-disable");
			}

			var nextBtnObj = {};
			nextBtnObj.layoutObj = temp;
			nextBtnObj.dataObj = actItem;
			_self.wfNextBtns[actItem.NODE_CODE] = nextBtnObj;

			_self.wfNextBtnNames[actItem.NODE_NAME] = nextBtnObj;
		}
	});

	// 如果是 完成并发送模式， 添加返回按钮
	if (_self.btnRenderMode == 11 ||_self.btnRenderMode == 111) {
		var actItem = {};
		actItem.NODE_CODE = "saveSendBack";
//		actItem.NODE_NAME = "返回上一步";
		actItem.NODE_NAME = Language.transStatic("rhWfCardView_string6");
		actItem.ACT_CSS = "sync";
		var temp = _self._bldNormalBtn(actItem, _self.nextBtnCon);

		temp.bind("click", function(event) {
			_self._saveSendBackToWfBtn();
			_self.afterSaveSendBackToWfBtn();
		});

		var nextBtnObj = {};
		nextBtnObj.layoutObj = temp;
		nextBtnObj.dataObj = actItem;
		_self.wfNextBtns[actItem.NODE_CODE] = nextBtnObj;

		_self.wfNextBtnNames[actItem.NODE_NAME] = nextBtnObj;
	}
};

/**
 * 刷新下一步的按钮
 */
rh.vi.wfCardView.prototype._refreshNextBtn = function() {
	this._destroyNextBtn();
	this._reloadNextBtn();
}

/**
 * 销毁下一步的按钮
 */
rh.vi.wfCardView.prototype._destroyNextBtn = function() {
	var _self = this;
	jQuery("#nextBtnCon").remove();
	jQuery(".rhCard-btnBar-bottom").remove();

	_self.nextBtnCon.html("");
	
	_self.wfNextBtns = [];

	_self.wfNextBtnNames = [];
}

/**
 * 重新加载下一步的按钮
 */
rh.vi.wfCardView.prototype._reloadNextBtn = function() {
	var _self = this;
	_self._destroyNextBtn();

	var result = rh_processData(WfActConst.SERV_PROC + ".getNextSteps.do",
			_self.reqdata);

	if (result.rtnStr = "success") {
		_self._bldNextBtnBar(result.nextSteps);
	}
}

/**
 * 保存并发送
 */
rh.vi.wfCardView.prototype.cmSaveAndSend = function() {
	var _self = this;

	// 完成并发送之前所做操作
	if (_self._beforeSaveAndSend() === false) {
		return false;
	}
	
	//保存之前的监听方法beforeSave()
    if (_self._parHandler.beforeSave() == false) {
//    	Tip.showError("校验未通过！");
    	Tip.showError(Language.transStatic("rhWfCardViewNodeExtends_string1"));
    	return false;
    };
	 //保存意见方法
    if (_self._parHandler.saveMind() == false) {
    	return false;
    }
	// 验证通过
	if (!_self._parHandler.form.validate()) {
//		Tip.showError("校验未通过");
		Tip.showError(Language.transStatic("rhWfCardViewNodeExtends_string1"));
		return false;
	}
	// 保存
	var changeData = _self._parHandler.getChangeData();
	if (!jQuery.isEmptyObject(changeData)) {
		_self._parHandler.modifySave(changeData, false, false);
	}

	// 清除功能性按钮
	// jQuery("#funcBtnDivCon").remove();
	// jQuery("#" + GLOBAL.getUnId("save",_self.opts.pId)).hide();
	// jQuery(".rhCard-btnBar-bottom").remove();
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
		_self.bldNextConfirmWindow(true);
	}
	
	//弹出下一步后，加载ICBC刷新待办的页面
	if (window.ICBC) {
		var OIS_SYSTEM_URL = (System.getVar("@C_OIS_URL@") || "") + "/icbc/ois/jumper.html";
		if (OIS_SYSTEM_URL) {
			$(".ICBC_TODO_REFRESH").remove();
			$("body").append('<div class="ICBC_TODO_REFRESH" style="display:none;"><iframe id="jumper" src=' + OIS_SYSTEM_URL + '></iframe></div>');
		}
	}

	// 构造页面下面的按钮
//	_self._parHandler._bldBtnBarBottom();

	_self.isSendState = true;

	// 完成并发送之后所做操作
	_self._afterSaveAndSend();
	
	return true;
}

/**
 * 点击完成并发送之前所做的逻辑操作
 */
rh.vi.wfCardView.prototype._beforeSaveAndSend = function() {
	return true;
};

/**
 * 点击完成并发送之后所做的逻辑操作
 */
rh.vi.wfCardView.prototype._afterSaveAndSend = function() {
};

/**
 * 返回上一步之后的操作
 */
rh.vi.wfCardView.prototype.afterSaveSendBackToWfBtn = function() {
};

/**
 * 如果是 完成并发送模式， 重新回到流程按钮
 */
rh.vi.wfCardView.prototype._saveSendBackToWfBtn = function() {
	var _self = this;

	_self.isSendState = false;

	// 销毁下一步的按钮
	_self._destroyNextBtn();

	// 销毁之前的流程按钮数组
	// _self.wfBtns = [];
	// _self._bldBtnBar();

	// 循环功能按钮，将其显示
	for ( var wfbtn in _self.wfBtns) {
		_self.wfBtns[wfbtn].layoutObj.show();
	}

	// 构造下面的一排按钮
	_self._parHandler._bldBtnBarBottom();
}

/**
 * 初始化意见
 */
rh.vi.wfCardView.prototype.initMind = function() {
	var _self = this;
	// 动态装载意见代码
	//Load.scriptJS("/sy/comm/mind/mind.js");
	var mindListNotShow = _self._parHandler.byIdData._MIND_LIST_NOT_SHOW || "";
	
	if (!_self._parHandler.existSubServ("MINDLIST") && mindListNotShow != "TRUE") { //没有意见的关联服务
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
}

/**
 * 销毁所有引用
 */
rh.vi.wfCardView.prototype.destroy = function() {
	if (this.mind) {
		this.mind.destroy();
		this.mind = null;
	}
	
	if (this._parHandler) {
		this._parHandler = null;
	}
	
	this.wfBtns = null;
	this.wfNextBtns = null;
	this.wfNextBtnNames = null;
	this.reqdata = null;
	
	this.wfActBtns = null;
	this.wfSendBtns = null;
	this.wfActBtnGroups = null;
	this.wfSendBtnGroup = null;
};


/**
 * 找到 mindCode 对应的定义Bean, 将默认值设置成给过来的 content
 * @param mindCode 意见编码
 * @param content 内容
 */
rh.vi.wfCardView.prototype.setMindDefaultValue = function(mindCode, content) {
	var _self = this;
	
	var genMindList = _self._parHandler.byIdData.mindCodeBean;
	for (var i=0;i<genMindList.length;i++) {
		var def = genMindList[i];
		
		if (def.CODE_ID && def.CODE_ID == mindCode) {
			if (!this._mindDefaultValues[mindCode]) {
				this._mindDefaultValues[mindCode] = def.DEFAULT_CONTENT;
			}
			def.DEFAULT_CONTENT = content;
		}
	}
	
	var terminalMind = _self.getTerminalMind();
	
	if (terminalMind.CODE_ID && terminalMind.CODE_ID == mindCode) {
		if (!this._mindDefaultValues[mindCode]) {
			this._mindDefaultValues[mindCode] = terminalMind.DEFAULT_CONTENT;
		}
		terminalMind.DEFAULT_CONTENT = content;
	}
}


/**
 * 普通意见Code Bean
 */
rh.vi.wfCardView.prototype.getMindCodeBean = function() {
	var _self = this;
	var genMindList = _self._parHandler.byIdData.mindCodeBean;

	//如果有处理方式，则把当前处理方式的值放到ByIdData中。
	if (_self.rhNext && _self.rhNext.getItem(_self.getWfSelectFieldName())){
		var wfSelectBaseField = _self.getAuthBean().S_WF_SELECT_BASE.FIELD_CODE;
		var genMindType = _self.rhNext.getItem(_self.getWfSelectFieldName()).getValue();
		
		_self._parHandler.byIdData[wfSelectBaseField] = genMindType;
		var item = _self._parHandler.getItem(wfSelectBaseField);
		if(item) {
			item.setValue(genMindType);
		}
	}
	var genMindListSize = genMindList.length;
	// 找到第一个符合条件的意见输入框
	for (var i=0; i<genMindListSize; i++) {
		var def = genMindList[i];
		var mindScript = def.MIND_SCRIPT;
		if(!mindScript) {
			var newGenMind = JSON.parse(JSON.stringify(def));
			if (_self.mindMustExt) {
				newGenMind.MIND_MUST = "1";
			}			
			return newGenMind;
		}
		mindScript = Tools.itemVarReplace(mindScript, _self._parHandler.byIdData);
		if(eval(mindScript)) { // 如果没有条件，或者条件判断通过
			var newGenMind = JSON.parse(JSON.stringify(def));
			if (_self.mindMustExt) {
				newGenMind.MIND_MUST = "1";
			}
			return newGenMind;
		}
	}
	// 至少有一个意见输入框，避免出现渲染出错问题。
	var genMindBean = genMindList[genMindListSize-1];
	if (genMindBean) {
		var newGenMind = JSON.parse(JSON.stringify(genMindBean)); //用这个是避免改变里面的值之后，影响之前的
		if (_self.mindMustExt) {
			newGenMind.MIND_MUST = "1";
		}
	}
	
	return newGenMind || genMindList;
};

/**
 * 最终 意见Code Bean
 */
rh.vi.wfCardView.prototype.getTerminalMind = function() {
	var _self = this;

	return _self._parHandler.byIdData.mindTerminal || {};
};

/**
 * 取得固定意见
 */
rh.vi.wfCardView.prototype.getRegularMind = function() {
	var _self = this;
	var result = jQuery.extend({}, _self._parHandler.byIdData.regularMind);
	return result;
}


/**
 * 取得意见类型的对象
 */
rh.vi.wfCardView.prototype.getNodeMindObj = function() {
	var _self = this;
	
	var nodeMind = {};
	
	nodeMind.REGULAR = JsonToStr(_self.getRegularMind());
	nodeMind.TERMINAL = JsonToStr(_self.getTerminalMind());
	nodeMind.GENERAL = JsonToStr(_self.getMindCodeBean());	
	
	return nodeMind;
}



/**
 * 是否能填写最终意见
 */
rh.vi.wfCardView.prototype.canWriteTerminalMind = function() {
	var mindCodeBean = this.getTerminalMind();
	if (mindCodeBean && mindCodeBean.READ_ONLY) {
		if (mindCodeBean.READ_ONLY == "true") {
			return false;
		}
	}

	return true;
}

/**
 * 是否能填写最终意见
 */
rh.vi.wfCardView.prototype.canWriteRegularMind = function() {
	var mindCodeBean = this.getRegularMind();
	if (mindCodeBean && mindCodeBean.READ_ONLY) {
		if (mindCodeBean.READ_ONLY == "true") {
			return false;
		}
	}

	return true;
}

/*
 * 节点实例bean
 */
rh.vi.wfCardView.prototype.getNodeInstBean = function() {
	var _self = this;

	return _self._parHandler.byIdData.nodeInstBean;
};

/**
 * 是否是起草节点，启动工作流的节点 ， 第一次绑定工作流的时候
 */
rh.vi.wfCardView.prototype.isFirstNode = function() {
	var _self = this;
	var nodeInstBean = _self.getNodeInstBean();
	if (nodeInstBean && nodeInstBean.PRE_NI_ID == "") { // 前节点没有，
		return true;
	}

	return false;
};

/**
 * 是否是起草节点，送出去回来的也算
 */
rh.vi.wfCardView.prototype.isDraftNode = function() {
	var _self = this;
	if (_self.getAuthBean() && _self.getAuthBean().isDraftNode == "true") {
		return true;
	}

	return false;
};

/**
 * 流程表单是否点了锁定
 */
rh.vi.wfCardView.prototype.isLocked = function() {
	var _self = this;
	var authBean = _self.getAuthBean();
	if (authBean && authBean.lockState == "1") { // 锁定了
		return true;
	}

	return false;
};

/**
 * 流程表单 是否 存在 可编辑按钮
 */
rh.vi.wfCardView.prototype.isExistEditBtn = function() {
	var _self = this;	
	return _self.existEditBtn;
};

/*
 * 相关权限 比如能否手写意见， 当前人是否正在处理当前的流程
 */
rh.vi.wfCardView.prototype.getAuthBean = function() {
	var _self = this;
	return _self._parHandler.byIdData.authBean;
};

/*
 * 获取页面field控制的数据
 */
rh.vi.wfCardView.prototype.getFieldControlBean = function() {
	var _self = this;
	return _self._parHandler.byIdData.fieldControlBean;
};

/*
 * 获取页面field控制的数据
 */
rh.vi.wfCardView.prototype.getBindTitle = function() {
	var _self = this;
	return _self._parHandler.byIdData.bindTitle;
};

/*
 * 对页面字段进行控制 , 如隐藏， 必填等控制
 */
rh.vi.wfCardView.prototype._doFiledControl = function() {
	var _self = this;
	var _fileControlData = _self.getFieldControlBean();

	var entirelyControl = _fileControlData.FIELD_CONTROL;
	var exceptionFiledStr = _fileControlData.FIELD_EXCEPTION;
	var updateFiledStr = _fileControlData.FIELD_UPDATE;
	var hiddenFiledStr = _fileControlData.FIELD_HIDDEN;
	var mustFiledStr = _fileControlData.FIELD_MUST;
	var displayFieldStr = _fileControlData.FIELD_DISPLAY;

	var parServID = _self.opts.pId;
	// 可编辑字段
	if (entirelyControl == "false") {
		_self._parHandler.form.disabledAll();
		_self._entirelyControl = true;
		if (exceptionFiledStr.length > 0) {
			jQuery.each(exceptionFiledStr.split(','), function(i, item) {
				if (_self._parHandler.getItem(item)) {
					_self._parHandler.getItem(item).enabled();
				}
			});
		}
	}

	// 显示字段
	if (displayFieldStr && displayFieldStr.length > 0) {

		var disps = displayFieldStr.split(',');
		jQuery.each(disps, function(i, item) {
			var field = _self._parHandler.getItem(item);
			if (field) {
				field.show();
			}
		});
	}

	// 隐藏字段
	if (hiddenFiledStr.length > 0) {
		jQuery.each(hiddenFiledStr.split(','), function(i, item) {
			var field = _self._parHandler.getItem(item);
			if (field) {
				field.hide();
			}
		});
	}

	// 必填字段
	if (mustFiledStr.length > 0) {
		jQuery.each(mustFiledStr.split(','), function(i, item) {
			_self._parHandler.form.setNotNull(item, true);
		});
	}

	// 显示分组
	var groupDis = _fileControlData.GROUP_DISPLAY || "";
	if (groupDis.length > 0) {
		var groupArr = groupDis.split(',');
		jQuery.each(groupArr, function(i, item) {
			_self._parHandler.showGroup(item);
		});
	}

	// 隐藏分组
	var groupHide = _fileControlData.GROUP_HIDE || "";
	if (groupHide.length > 0) {
		var groupArr = groupHide.split(',');
		jQuery.each(groupArr, function(i, item) {
			_self._parHandler.hideGroup(item);
		});
	}

	// 折叠分组
	var groupExpand = _fileControlData.GROUP_EXPAND || "";
	if (groupExpand.length > 0) {
		var groupArr = groupExpand.split(',');
		jQuery.each(groupArr, function(i, item) {
			_self._parHandler.expandGroup(item);
		});
	}

	// 展开分组
	var groupCollapse = _fileControlData.GROUP_COLLAPSE || "";
	if (groupCollapse.length > 0) {
		var groupArr = groupCollapse.split(',');
		jQuery.each(groupArr, function(i, item) {
			_self._parHandler.collapseGroup(item);
		});
	}

	//
	if(this.getAuthBean() ) {
		var editableFields = this.getAuthBean().BIND_ENDEDITFIELD;
		if(editableFields) {
			jQuery.each(editableFields.split(','), function(i, item) {
				if (_self._parHandler.getItem(item)) {
					_self._parHandler.getItem(item).enabled();
				}
			});
		}
	}
};

/**
 * 刷新首页待办
 */
rh.vi.wfCardView.prototype._refreshMainPage = function() {
	// 刷新首页的待办，主办等区块
	if (_parent.Portal.getBlock("SY_COMM_TODO")) {
		_parent.Portal.getBlock("SY_COMM_TODO").refresh(); // 刷新待办
	}
	if (_parent.Portal.getBlock("SY_COMM_ENTITY")) {
		_parent.Portal.getBlock("SY_COMM_ENTITY").refresh(); // 刷新主办
	}
}

/**
 * 判断返回的组织资源树，是否只有一个人
 */
rh.vi.wfCardView.prototype.treeDataHaseOneMan = function(treeData) {
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

/**
 * 送交之前执行,业务代码可覆盖此方法
 */
rh.vi.wfCardView.prototype._beforeSongjiao = function() {
};

/**
 * 必填意见的判断，不通过mind去判断了 , 写到这里，直接查数据库
 */
rh.vi.wfCardView.prototype._mindMust = function() {
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

/*
 * 根据 节点信息， 打开人员选择窗口 ，也就是送交人 @param aId 动作ID
 */
rh.vi.wfCardView.prototype._openSelectOrg = function(nodeCode, aObj, event, 
		selectedUserIds, selectedNames, excludeUserId, selectUserPos) {
	var _self = this;
	
	if (_self.btnRenderMode != 12 && _self.btnRenderMode != 112) { //btnRenderMode 如果是12或112 的话，则不去检查这个意见
		_self._parHandler.saveMind();
		
		if (_self._beforeSongjiao() == false || _self._mindMust() == false) {
			return false;
		}
	}

	var taObj = aObj;
	_self._nextStepNodeCode = nodeCode;
	var inputName = GLOBAL.getUnId("wfToUser", _self.opts.sId);

	_self.reqdata["NI_ID"] = _self.getNodeInstBean().NI_ID;
	_self.reqdata["NODE_CODE"] = nodeCode;
	_self.reqdata["SERV_ID"] = _self._parHandler.servId;
	
	var nestStepBeanObj = null;
	nestStepBeanObj = _self._getWfNextBtn(nodeCode);
	if (null != nestStepBeanObj) {
		var dataBean = nestStepBeanObj["dataObj"];
		if (dataBean) {
			var huiqian = dataBean["HQ_ITEM"] || "";
			if ((_self.btnRenderMode != 12 && _self.btnRenderMode != 112) && huiqian.length > 0) {
				if (!jQuery.isEmptyObject(_self._parHandler.getChangeData())) {
					try {
						top.Tip.clear();
					} catch (e) {
						Tip.clear();
					}
//					alert("请先点击[保存]按钮保存数据");
					alert(Language.transStatic("rhWfCardView_string7"));
					return;
				}
			}
			var errorMsg = dataBean["ERROR_MSG"] || "";
			if (errorMsg.length > 0 ) {
				if (errorMsg == "empty") { //缺省提示
					try {
//						top.Tip.addTip("页面没有添加会签部门！", "warn");
						top.Tip.addTip(Language.transStatic("rhWfCardView_string8"), "warn");
					} catch (e) {
//						Tip.addTip("页面没有添加会签部门！", "warn");
						Tip.addTip(Language.transStatic("rhWfCardView_string8"), "warn");
					}
				} else {
					Tip.showError(errorMsg);
				}
				return;
			}
		}
	}
	
	if (excludeUserId) { //有需要排除的用户
		_self.reqdata["EXCLUDE_USERS"] = excludeUserId;
	} else {
		_self.reqdata["EXCLUDE_USERS"] = "";
	}

	var rtnTreeData = rh_processData(
			"SY_WFE_PROC.getNextStepUsersForSelect.do", _self.reqdata, false, true, callbackFetchUser);
	
	function callbackFetchUser(rtnTreeData) {
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
		
		if(rtnTreeData.TO_USERS != undefined) {
			if(rtnTreeData.TO_USERS){
//				Tip.show("已经成功送交给" + rtnTreeData.TO_USERS,true);
				Tip.show(Language.transStatic("rhWfCardView_string9") + rtnTreeData.TO_USERS,true);
				try {
					top.Tip.clear();
				} catch (e) {
					Tip.clear();
				}
				if(rtnTreeData._closeDlg == "false"){
					_self._parHandler.refresh();
				} else {
					// 关闭当前页面
					_self._parHandler.backClick();
				}
				_self._parHandler.shieldHide();
			}else{
//				alert("没有找到送交人，请联系系统管理员!");
				alert(Language.transStatic("rhWfCardView_string10"));
			}
			return;
		}

		var treeData = rtnTreeData.treeData;

		if (treeData == "[]") {
//			alert("当前节点没有可供选择的人员，请联系系统管理员！");
			alert(Language.transStatic("rhWfCardView_string11"));
			return false;
		}

		var sendDirectFlag = System.getVar("@C_SY_WF_DIRECT_SEND@") == "true" ? true : false;
		var oneUserCodes = "";

//		var bindTreeTitle = "人员选择";
		var bindTreeTitle = Language.transStatic("rhWfCardView_string12");
		_self.binderType = rtnTreeData.binderType; // 送交类型

		if (_self.binderType == "ROLE") {
			// 如果是角色，取到角色的CODE
			_self._binderRoleCode = rtnTreeData.roleCode;
//			bindTreeTitle = "角色选择";
			bindTreeTitle = Language.transStatic("rhWfCardView_string13");
			sendDirectFlag = false;
		} else { // 送人
			// var rtnOneValue = _self.treeDataHaseOneMan(treeData);

			// if (rtnOneValue == "multi") {
			// 	sendDirectFlag = false;
			// } else {
			// 	if (_self.btnRenderMode == 12 || _self.btnRenderMode == 112) { //如果是弹出 处理完毕的 模式， 只有一个人，即使系统配置不是直接送，这里也处理成直接送
			// 		sendDirectFlag = true;
			// 	}
			// 	oneUserCodes = rtnOneValue;
			// 	rtnTreeData.autoSelect = "1";
			// }
		}

		_self._setComboboxOptions(rtnTreeData, selectUserPos);
	}
};


rh.vi.wfCardView.prototype._setComboboxOptions = function(rtnTreeData, selectUserPos) {
	var _self = this;
	var datas = eval("(" + rtnTreeData.treeData + ")");

	var pos = selectUserPos;
	if(selectUserPos == undefined) {
		pos = _self.selectUserToPos;
	}

	var userSelectItemName = WfNextStep.userSelect(pos) ;
	var deptRoleSelectItemName = WfNextStep.deptRole(pos);

	// //判断，如果是 通过ACT选人 返回的结果， 则将人员的框 显示出来
	var nextStepName = WfNextStep.nextStep;
	var nextStep = _self.rhNext.getItem(nextStepName).getValue();
	if (nextStep.indexOf("ACT") == 0) { 
		var userSelectName0 = WfNextStep.userSelect();
		_self.rhNext.getItem(userSelectName0).$dom.show(); //让处理人显示
		_self.rhNext.getItem(userSelectName0).show(); //
		_self.rhNext.getItem(userSelectName0).setNotNull(true); 
	}

	var _selectItem = _self.rhNext.getItem(userSelectItemName);
	_selectItem.setOptions(datas); // 设置可选参数
	if(rtnTreeData.multiSelect == "true") {
		_selectItem.setMulti(true);
	}

	if (rtnTreeData.autoSelect == "1" || rtnTreeData.autoSelect == "3") {
		if(rtnTreeData.autoSelect == '3') { // 自动选择不修改
			_selectItem.disable();
		}
		if(_selectItem.options.length > 0 ) {
			_selectItem.setValue(_selectItem.options);
		} else if(_selectItem.options > 1) {
			_selectItem.setValue(_selectItem.options);
		}
	} else {
		if(_selectItem.options.length == 1) {
			_selectItem.setValue(_selectItem.options[0]);
		}
	}

	if (rtnTreeData.binderType == "USER") { //送人
		//设置角色为空,避免之前选过送角色了
		_self.rhNext.getItem(deptRoleSelectItemName).setValue(""); 
	} else if (rtnTreeData.binderType == "ROLE") { //送角色
		_self.rhNext.getItem(deptRoleSelectItemName).setValue(rtnTreeData.roleCode);
	} 
}

/**
 * 将流程送下个节点的人
 */
rh.vi.wfCardView.prototype._confirmSend = function(idArray, nameArray) {
	var _self = this;
	if (_self.btnRenderMode == 12 || _self.btnRenderMode == 112 || _self.callback.call(_self, idArray, nameArray)) { // 判断有没有出部门
		//_self._parHandler.shield();
		// 送交的类型 1 送部门+角色 ， 3 送用户
		var toType = "3";

		if (_self.binderType == "ROLE") {
			toType = "1";
			var toDepts = idArray.join(","); //.replace(new RegExp("dept:", "gm"), "");
			_self.reqdata["TO_DEPT"] = toDepts; // 送交部门
			_self.reqdata["TO_ROLE"] = _self._binderRoleCode; // 送交角色
		} else {
			var userArray = new Array();
			jQuery(idArray).each(function(indextrty, intrty) {
				if (intrty.indexOf("usr:") == 0) {
					userArray.push(intrty);
				}
			});

			var userNameStr = userArray.toString(); //.replace(new RegExp("usr:", "gm"), "");

			if (userNameStr.length <= 0) {
//				alert("没有选中人员，请重新选择送交人员");
				alert(Language.transStatic("rhWfCardView_string14"));
				_self._parHandler.shieldHide(); // 清除锁定页面

				return false;
			}
			_self.reqdata["TO_USERS"] = userNameStr; // 送交人 替换掉所有的usr:
		}
		_self.reqdata["TO_TYPE"] = toType; // 类别
		_self.reqdata["NI_ID"] = _self.getNodeInstBean().NI_ID; // 当前节点实例ID
		_self.reqdata["NODE_CODE"] = _self._nextStepNodeCode; // 下个节点CODE

		if (_self.btnRenderMode == 12 || _self.btnRenderMode == 112) { //如果是弹出框的模式
			_self.confirmUserSelectSend(_self.reqdata, nameArray);
			
			_self._parHandler.shieldHide();
			
			return;
		}

		var result = rh_processData("SY_WFE_PROC.toNext.do", _self.reqdata);
		
		if(Tools.actIsSuccessed(result)){
			//是否启用委托，显示委托提示信息
			var userNames = _self.isExistAgent(idArray, toType, _self._parHandler.servId);
			if (userNames != "") {
//				Tip.show("已经成功送交给" + userNames, true);
				Tip.show(Language.transStatic("rhWfCardView_string9") + userNames, true);
			} else {
//				Tip.show("已经成功送交给" + nameArray.join(","), true);
				Tip.show(Language.transStatic("rhWfCardView_string9") + nameArray.join(","), true);
			}
			try {
				top.Tip.clear();
			} catch (e) {
				Tip.clear();
			}
			//刷新待办			
			if(result._closeDlg == "false"){
				_self._parHandler.refresh();
			} else {
				// 关闭当前页面
				_self._parHandler.setParentRefresh();
				_self._parHandler.backClick();	
			}
		}else{
//			alert("送交失败。");
			alert(Language.transStatic("rhWfCardView_string15"));
		}
		//_self._parHandler.shieldHide();
	}
};

/**
 * 判断当前被送交人否存在委托
 * @param {Object} userCode 被送交人用户code
 * @param {Object} toType 送交类别
 */
rh.vi.wfCardView.prototype.isExistAgent = function(userCodes, toType, servId){
	var names = ""; //重构显示用户信息
	//如果是用户类别的去查看是否存在委托
	if (toType == "3") {
		var codesStr = "";
		if (typeof (userCodes) == "string") {
			codesStr = userCodes.replace(new RegExp("usr:", "gm"), "");
		} else {
			codesStr =  userCodes.join(",").replace(new RegExp("usr:", "gm"), "");
		}
		var outBean = FireFly.doAct("SY_ORG_USER_AGENT", "isExiteAnent", {"USER_CODES":codesStr, "servId":servId});
		var indexCount = outBean["_MSG_"].indexOf("ERROR,"); 
		if (indexCount >= 0) {
			alert(outBean["_MSG_"].replace("ERROR,", ""));
			return names;
		}
		return outBean["USER_NAMES"];
	}
	return names
};

/**
 * 判断当前发送的人有没出部门
 * 
 * @param idArray
 *            树回写的id数组
 * @param nameArray
 *            树回写的name数组
 */
rh.vi.wfCardView.prototype.callback = function(idArray, nameArray) {
	return true;
}

/*
 * 根据动作绑定相应的方法 @param actId 动作ID
 * 
 */
rh.vi.wfCardView.prototype._act = function(actId, servId, aObj, actItem) {
	var _self = this;
	var taObj = aObj;

	taObj.bind("click", function(event) {
		if (actItem.ACT_MEMO.length > 1 && actItem.ACT_MEMO != "null") {
			var func = new Function(actItem.ACT_MEMO);
			var res = func.apply(_self);
		} else {
			var res = eval("_self." + actItem.ACT_CODE + "(event,actItem)");
		}
		if(!res){
			event.stopImmediatePropagation();
		}
		event.stopPropagation();
	});

	var wfBtnObj = {};
	wfBtnObj.layoutObj = taObj;
	wfBtnObj.dataObj = actItem;

	_self.wfBtns[actItem.ACT_CODE] = wfBtnObj;
};

/**
 * 
 */
rh.vi.wfCardView.prototype.logItem = function(event,actItem){
	var _self = this;
    var extWhere = "and SERV_ID='" + _self._parHandler.servId + "' and DATA_ID='" + _self._parHandler._pkCode + "'";
//	var opts = {"url":"SY_SERV_LOG_ITEM_SINGLE.list.do?extWhere=" + encodeURIComponent(extWhere),"tTitle":"变更历史","menuFlag":3};
	var opts = {"url":"SY_SERV_LOG_ITEM_SINGLE.list.do?extWhere=" + encodeURIComponent(extWhere),"tTitle":Language.transStatic("rhWfCardView_string16"),"menuFlag":3};
	Tab.open(opts);
}


/**
 * 根据actCode 获取按钮对象
 * @return 按钮对象wfBtnObj,  包含 按钮在页面上的jquery对象 和 act定义数据
 * 	var wfBtnObj = {};
 *	wfBtnObj.layoutObj  页面上的jquery对象
 *	wfBtnObj.dataObj  act定义数据
 *  用法示例
 *  if (_viewer.wfCard) { //卡片页面中需要获取到wfCard的句柄
 *      var actCodeBtnObj = _viewer.wfCard._getBtn('actCode');	
 *      if(actCodeBtnObj){ //需要获取到该按钮
 *              //actCodeBtnObj.layoutObj 即获取到了页面上该按钮的 jquery对象，能直接对其绑定事件
 *   	        var actCodeBtn = actCodeBtnObj.layoutObj;
 *	            actCodeBtn.unbind("click").bind("click",function(event) {...});
 *
 *              //actCodeBtnObj.dataObj 即获取到了页面上该按钮 ACT定义数据对象，能直接获取相应的值
 *              var actCodeBtnData = actCodeBtnObj.dataObj;
 *	            var actCodeBtnMemo = actCodeBtnData.ACT_EXPRESSION; //即得到定义上的操作表达式
 *              var actCodeBtnMemo = actCodeBtnData.ACT_NAME; //即得到按钮名称
 *              ...
 *          }
 *      }
 *  }
 */
rh.vi.wfCardView.prototype._getBtn = function(actCode) {
	return this.wfBtns[actCode];
};

/**
 * 获取送下一步的按钮对象 根据ACT_CODE
 * @return 按钮对象nextBtnObj,  包含 按钮在页面上的jquery对象 和 act定义数据
 * 	var nextBtnObj = {};
 *	nextBtnObj.layoutObj  页面上的jquery对象
 *	nextBtnObj.dataObj  act定义数据
 */
rh.vi.wfCardView.prototype._getWfNextBtn = function(actCode) {
	return this.wfNextBtns[actCode];
};

/**
 * 获取按钮 根据名称
 */
rh.vi.wfCardView.prototype._getNextBtnByName = function(actName) {
	return this.wfNextBtnNames[actName];
};

/**
 * 点击流程按钮 前执行 funName 函数
 */
rh.vi.wfCardView.prototype._doWfBtnBeforeClick = function(funName, btnObj) {
	var _self = this;

	var layoutObj = btnObj.layoutObj;
	var actItem = btnObj.dataObj;
	
	if(!layoutObj){
		return;
	}

	layoutObj.unbind("click").bind("click", function(event) {
		var rtnValue = funName.call();

		if (rtnValue) {
			if (actItem.ACT_MEMO.length > 1 && actItem.ACT_MEMO != "null") {
				eval(actItem.ACT_MEMO);
			} else {
				eval("_self." + actItem.ACT_CODE + "(event,actItem)");
			}
		}
	});
};

/**
 * 点击流程按钮 后执行 funName 函数
 */
rh.vi.wfCardView.prototype._doWfBtnAfterClick = function(funName, btnObj) {
	var _self = this;

	var layoutObj = btnObj.layoutObj;
	var actItem = btnObj.dataObj;
	
	if(!layoutObj){
		return;
	}	

	layoutObj.unbind("click").bind("click", function(event) {
		if (actItem.ACT_MEMO.length > 1 && actItem.ACT_MEMO != "null") {
			eval(actItem.ACT_MEMO);
		} else {
			eval("_self." + actItem.ACT_CODE + "(event,actItem)");
		}
		// 之后执行
		funName.call();
	});
};


/**
 * 打开组织结构选择
 */
rh.vi.wfCardView.prototype._doOpenOrgSelect = function(event, btnObj) {
	var _self = this;

	var layoutObj = btnObj.layoutObj;
	var actItem = btnObj.dataObj;

	_self._openSelectOrg(actItem.NODE_CODE, layoutObj, event);
};

/**
 * 点击流程按钮按钮之前执行
 */
rh.vi.wfCardView.prototype._doNextStepBeforeClick = function(funName, btnObj) {
	var _self = this;

	var layoutObj = btnObj.layoutObj;
	var actItem = btnObj.dataObj;
	
	if(!layoutObj){
		return;
	}

	layoutObj.unbind("click").bind("click", function(event) {
		var rtnValue = funName.call();

		if (rtnValue) {
			_self._openSelectOrg(actItem.NODE_CODE, layoutObj, event);
		}
	});
};


/**
 * 退回 ， 填写退回说明 -> 添加退回的待办 -> 完成分发（设置返回的字段值） -> 待办变成已办
 * 
 */
rh.vi.wfCardView.prototype.cmTuiHui = function(event, actItem) {
	var _self = this;
	var sendId = actItem.SEND_ID;
	// 退回回执
	var winDialog = jQuery("<div></div>").attr("id", "tuiHuiDiv")
//		.attr("title","请填写退回原因");
		.attr("title",Language.transStatic("rhWfCardView_string17"));

	var huiZhiContent = jQuery("<div style='margin:10px;'></div>").appendTo(winDialog);
	jQuery("<textarea type='textarea' style='width:97%;height:260px;padding:5px' id ='huiZhiContent' name ='huiZhiContent'/>")
			.appendTo(huiZhiContent);

	var posArray = [];
	if (event) {
		var cy = event.clientY;
		posArray[0] = "";
		posArray[1] = cy + 120;
	}
	winDialog.appendTo(jQuery("body"));
	jQuery("#tuiHuiDiv").dialog({
		autoOpen : false,
		width : 400,
		height : 260,
		modal : true,
		resizable : false,
		position : posArray,
		close : function() {
			winDialog.remove();
		},buttons:{"确定":function(){
//		},buttons:{Language.transStatic("rh_ui_gridCard_string17"):function(){	
			var params = {};
			params["TODO_CONTENT"] = jQuery("#huiZhiContent").val();
			params["SEND_ID"] = sendId;
			if (jQuery("#huiZhiContent").val().length > 1000) {
//				alert("内容不能超过1000个汉字。");
				alert(Language.transStatic("rhWfCardView_string18"));
				return;
			}
			var result = rh_processData("SY_COMM_SEND_SHOW_CARD.cmTuiHui.do",
					params);
			if (result.rtnstr == "success") {
//				Tip.show("退回成功", true);
				Tip.show(Language.transStatic("rhWfCardView_string19"), true);
				if (_parent.Portal.getBlock("SY_COMM_TODO")) {
					_parent.Portal.getBlock("SY_COMM_TODO").refresh();
				}
				_self._parHandler.backClick();
				setTimeout(function() {
					jQuery("a.rhCard-refresh").click();
				}, 100);
			} else {
//				Tip.show("退回失败", true);
				Tip.show(Language.transStatic("rhWfCardView_string20"), true);
			}
		},"取消":function(){
//		},Language.transStatic("rh_ui_card_string18"):function(){	
			winDialog.remove();
		}}
	});
	jQuery("#tuiHuiDiv").dialog("open");
	jQuery(".ui-dialog-titlebar").last().css("display", "block");
}

/**
 * 转发 弹出选人的页面，确定即分发出去
 */
rh.vi.wfCardView.prototype.cmZhuanFa = function(event, actItem) {
	var _self = this;
	// 分发ID
	var sendId = actItem.SEND_ID;
	_self.reqdata.SEND_ID = sendId;

	var inputName = "zhuanfaUsers";

	//
	var configStr = "SY_ORG_DEPT_USER,{'TYPE':'multi'}";
	var extendTreeSetting = {
		'cascadecheck' : false,
		'checkParent' : false
	};
	var options = {
		"itemCode" : inputName,
		"config" : configStr,
		"hide" : "explode",
		"show" : "blind",
		"extendDicSetting" : extendTreeSetting,
		"replaceCallBack" : _self._zhuanFa,
		"parHandler" : _self
	};
	var dictView = new rh.vi.rhDictTreeView(options);
	dictView.show(event);
}

/**
 * 转发
 */
rh.vi.wfCardView.prototype._zhuanFa = function(idArray, nameArray) {
	var _self = this;
	_self.reqdata["TARGET_USERS"] = idArray.join(",");

	_self.reqdata["SERV_ID"] = _self._parHandler.servId;
	_self.reqdata["DATA_ID"] = _self._parHandler._pkCode;
	_self.reqdata["DATA_TITLE"] = _self.getBindTitle(); // _self._parHandler.itemValue("GW_TITLE");
	// 是否转发
	_self.reqdata["isZF"] = "yes"; 
	var result = FireFly.doAct("SY_COMM_SEND_SHOW_CARD","cmZhuanFa",
			_self.reqdata);
}

/**
 * 阅件签收
 */
rh.vi.wfCardView.prototype.cmQianShou = function(event, actItem) {
	var _self = this;
	// 分发ID
	var sendId = actItem.SEND_ID;
	_self.reqdata.SEND_ID = sendId;
	var result = rh_processData("SY_COMM_SEND_SHOW_CARD.cmQianShou.do",
			_self.reqdata);
	if (result.rtnstr == "success") {
//		Tip.show("操作成功", true);
		Tip.show(Language.transStatic("rhCommentView_string3"), true);
	} else {
//		Tip.show("操作失败", true);
		Tip.show(Language.transStatic("rhWfCardView_string21"), true);
	}
//	_self._parHandler.refresh();
	_self._parHandler.backClick();
}

/**
 * 终止流程，将文件放到废件箱
 */
rh.vi.wfCardView.prototype.cmToTrash = function() {
	var _self = this;

//	if (!confirm("请确定是否终止?")) {
	if (!confirm(Language.transStatic("rhWfCardView_string22"))) {	
		return false;
	}

	var result = rh_processData(WfActConst.SERV_PROC + ".deleteDoc.do",
			_self.reqdata);

	if (result.rtnstr == "success") {
//		alert("文件已废止，如需恢复，请通过废件箱操作。");
		alert(Language.transStatic("rhWfCardView_string23"));

		// 关闭当前页面
		_self._parHandler.backClick();
	} else {
//		Tip.show("操作失败", true);
		Tip.show(Language.transStatic("rhWfCardView_string24"), true);
	}
}

/**
 * 锁定文件
 */
rh.vi.wfCardView.prototype.cmLockFile = function() {
	var _self = this;

	var result = rh_processData(WfActConst.SERV_PROC + ".cmLockFile.do",
			_self.reqdata);

	if (result.rtnstr == "success") {
		_self._parHandler.refresh();
//		alert("锁定成功");
		alert(Language.transStatic("rhWfCardView_string25"));
	} else {
//		Tip.show("操作失败", true);
		Tip.show(Language.transStatic("rhWfCardView_string24"), true);
	}
}

/**
 * 解锁文件
 */
rh.vi.wfCardView.prototype.cmUnLockFile = function() {
	var _self = this;

	var result = rh_processData(WfActConst.SERV_PROC + ".cmUnLockFile.do",
			_self.reqdata);

	if (result.rtnstr == "success") {
		_self._parHandler.refresh();
//		alert("解锁成功");
		alert(Language.transStatic("rhWfCardView_string26"));
	} else {
//		Tip.show("操作失败", true);
		Tip.show(Language.transStatic("rhWfCardView_string24"), true);
	}
};

/**
 * 创建相关服务
 */
rh.vi.wfCardView.prototype.cmCreateRelateServ = function(event,actItem) {
	var _self = this;
	//校验是否保存
	var changeData = _self._parHandler.getChangeData();
	if (!jQuery.isEmptyObject(changeData)) {
//		alert("页面数据已修改，请先保存或恢复修改");
		alert(Language.transStatic("rhWfCardView_string27"));
		return false;
	}
	//初始化参数
	var createRelatedServOpt = {
		"SELECT_FILE_FLAG": true,
		"SELECT_SERVS":[]
	};
	//获取工作流按钮配置的参数并覆盖默认参数
	if ("" != (actItem["WFE_PARAM"] || "")) {
		//解析节点配置json信息
		var createRelatedServWfParam = {};
		try {
			createRelatedServWfParam = jQuery.parseJSON(actItem["WFE_PARAM"]);
		} catch(exception) {
//			Tip.showError("请确定节点json串格式是否正确，格式为{\"\":\"\"}",null);
			Tip.showError(Language.transStatic("rhWfCardView_string28") + "{\"\":\"\"}",null);
			return false;
		}
		jQuery.extend(createRelatedServOpt, createRelatedServWfParam);
	} else {
//		Tip.showError("请配置工作流按钮配置参数");
		Tip.showError(Language.transStatic("rhWfCardView_string28"));
		return false;
	}
	//
	var jsFileUrl = FireFly.getContextPath() + "/oa/servjs/OA_BEFORE_CREATE_RELATED_SERV.js";
    jQuery.ajax({
        url: jsFileUrl,
        type: "GET",
        dataType: "text",
        async: false,
        data: {},
        success: function(data){
            try {
                var servExt = new Function(data);
                servExt.call(_self.getParHandler(),createRelatedServOpt);
            } catch(e){
            	throw e;
            }
        },
        error: function(){;}
    });
};

/**
 * 分发-显示分发选择用户界面前调用此参数
 */
rh.vi.wfCardView.prototype.beforeFenfa = function(sendObj){
	return true;
};

/**
 * 分发-分发按钮绑定函数(亦“发送”按钮)
 */
rh.vi.wfCardView.prototype.cmFenFa = function(event,actItem) {
	var _self = this;
	
	//校验是否保存
	var changeData = _self._parHandler.getChangeData();
	if (!jQuery.isEmptyObject(changeData)) {
//		alert("页面数据已修改，请先保存或恢复修改");
		alert(Language.transStatic("rhWfCardView_string30"));
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
//		var sendFlagFenfa = confirm("该文件已经分发过，是否再次分发?");
		var sendFlagFenfa = confirm(Language.transStatic("rhWfCardView_string31"));
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
//			Tip.showError("请确定节点json串格式是否正确，格式为{\"\":\"\"}",null);
			Tip.showError(Language.transStatic("rhWfCardView_string28") + "{\"\":\"\"}",null);
			return false;
		}

//		fenfaObj.TITLE = fenFaWfParam["TITLE"] || "分发文件";
		fenfaObj.TITLE = fenFaWfParam["TITLE"] || Language.transStatic("rhWfCardView_string32");
		if(fenFaWfParam["MIND_CODE"] && fenFaWfParam["MIND_CODE"] != ""){
			fenfaObj.MIND_CODE = fenFaWfParam["MIND_CODE"];
		}
		fenfaObj.mode = fenFaWfParam["mode"];
		
		if(fenfaObj.mode && fenfaObj.mode.indexOf("auto") >= 0){
			//获取分发节点配置数组
			var fenFaItemArr = fenFaWfParam["SEND_ITEM"] || [];
			if(fenFaItemArr.length == 0){
//				alert("自动分发模式下分发字段为空，请检查分发节点配置");
				alert(Language.transStatic("rhWfCardView_string33"));
				return false;
			}
			fenfaObj.itemArr = fenFaItemArr;
			
			for (var i = 0; i < fenFaItemArr.length; i++) {
				var itemObj = _self._parHandler.getItem(fenFaItemArr[i].code);
				if (itemObj == null) {
//					alert("您选择的分发字段不存在，字段名为：" + fenFaItemArr[i].code);
					alert(Language.transStatic("rhWfCardView_string34") + fenFaItemArr[i].code);
					return false;
				} else {
					var itemValue = itemObj.getValue();
					if ("" == (itemValue || "")) {
						//alert("您选择的分发字段没有值！");
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
 * 分发-按节点配置json信息分发
 */
rh.vi.wfCardView.prototype.autoSend = function(fenfaObj) {
	var _self = this;
	
	// 服务ID
	var servId = fenfaObj.SERV_ID;
	// 被分发数据的ID
	var dataId = fenfaObj.DATA_ID;
	// 被分发数据标题
	var dataTitle = fenfaObj.DATA_TITLE;
	//分发参数
	var sendObj = {
		"SERV_ID" : servId,
		"DATA_ID" : dataId,
		"DATA_TITLE" : dataTitle,
		"includeSubOdept" : true
	};	
	sendObj.fromScheme = "yes"; //来源于方案
	sendObj.ifFirst = "yes";
	sendObj._extWhere = " and DATA_ID = '" + dataId + "'";
	sendObj.SEND_ITEM = fenfaObj.itemArr;
	sendObj.DATA_FROM = fenfaObj.DATA_FROM || "";
	
	//用户可重载参数
	if(this.beforeFenfa){
		var result = this.beforeFenfa(sendObj);
		if(!result){
			return;
		}
	}
	
	var mode = fenfaObj.mode || "";
	//var mode = "semi-auto";
	if(mode == "auto"){ //全自动(直接分发)
		sendObj.sendMode = "auto";
		var data = jQuery.toJSON(sendObj);	
//		if(!confirm("请确认是否要发送文件？")){
		if(!confirm(Language.transStatic("rhWfCardView_string35"))){	
			return false;
		}
		_self._parHandler.shield();
		setTimeout(function(){ //适当加上延迟提升用户感觉
			FireFly.doAct("SY_COMM_SEND_SHOW_USERS", "autoSend", {"data":data}, true);
			_self._parHandler.shieldHide();
//			alert("发送完成！");
			alert(Language.transStatic("rhWfCardView_string36"));
			_self._parHandler.refresh();
		},500);
	}else if(mode == "semi-auto"){ //半自动(显示待分发人员列表)
		sendObj.sendMode = "semi_auto";
		var url = "SY_COMM_SEND_SHOW_USERS.list.do";
		var opts = {
			"url" : url,
//			"tTitle" : "待分发人员列表",
			"tTitle" : Language.transStatic("rhWfCardView_string37"),
			"params" : sendObj,
			"menuFlag" : 4
		};
		Tab.open(opts);
	}else{ //手动分发(如果分发节点没有配置字段项，直接打开卡片)
		var shoudongUrl = encodeURI("SY_COMM_SEND_SHOW_CARD.showSend.do?data="+ JsonToStr(fenfaObj));
		var opts = {
			"id" : "SY_COMM_SEND_SHOW_CARD-" + fenfaObj.SERV_ID,
			"url" : shoudongUrl,
//			"tTitle" : "分发",
			"tTitle" : Language.transStatic("rhWfCardView_string38"),
			"menuFlag" : 4
		};
		Tab.open(opts);
	}
};

/**
 * 简单分发之前调用，常用于重置params的值。
 */
rh.vi.wfCardView.prototype.beforeCmSimpleFenFa = function(actItem,params){
	return true;
};

/**
 * 简单分发
 */
rh.vi.wfCardView.prototype.cmSimpleFenFa = function(event, actItem, callback){
	var _self = this;
	var params = {
		"DATA_ID" : _self._parHandler._pkCode,
		"userSelectDict":"SY_ORG_DEPT_USER_SUB",
		"displaySendSchm":false,
		"includeSubOdept":true,
		"displayScope": "tdept"
	};
	
	//用户可重载参数
	if(this.beforeCmSimpleFenFa){
		var result = this.beforeCmSimpleFenFa(actItem,params);
		if(!result){
			return;
		}
	}
	
	var configStr = "@com.rh.core.serv.send.SimpleFenfaDict,{'TYPE':'multi','CHECKBOX':true,'extendDicSetting':{'rhexpand':false,'expandLevel':1,'cascadecheck':true,'checkParent':false,'childOnly':true}}"; //针对传阅，多选时可以全选部门下的节点

	if (!callback) {
		callback = function(id,value){
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
			
			_self._parHandler.shield();
			
			setTimeout(function() { // 适当加上延迟提升用户感觉
				try{
					var data = jQuery.toJSON(sendObj);
					var result = FireFly.doAct("SY_COMM_SEND_SHOW_USERS", "autoSend",{"data":data}, true);
					if(!Tools.actIsSuccessed(result)){
//						alert("发送失败");
						alert(Language.transStatic("rhWfCardView_string39"));
					}
				} catch (e){
					throw e;
				} finally{
					_self._parHandler.shieldHide();
				}
			}, 50);
			
		}
	}
	
	var options = {
		//"itemCode" : "inputName",
		"config" : configStr,
		"hide" : "explode",
		"show" : "blind",
		"rebackCodes" : "inputName",
		"replaceCallBack" : callback,
		"dialogName" : actItem.ACT_NAME,
//		"parHandler" : _self._parHandler,
		"params" : params
	};

	var dictView = new rh.vi.rhDictTreeView(options);
	dictView.show(event);
};

/**
 * 审批单的附件、意见、相关文件管理
 */
rh.vi.wfCardView.prototype.cmWfDataMgr = function(event) {
	var _viewer = this;
	var servDataId = _viewer.getParHandler()._pkCode;
	var serID = _viewer.getParHandler().servId;
	var sId = "SY_COMM_ENTITY_GL";
	var data = {};
    data["_searchWhere"] = " and SERV_ID='"+serID+"' and DATA_ID='"+servDataId+"'";
	var entityData = FireFly.getListData("SY_COMM_ENTITY_GL",data);
	var params={};
	params["handler"] = _viewer;
//	var options = {"url":sId + ".card.do?pkCode=" + entityData["_DATA_"][0]["ENTITY_ID"] , "tTitle":"数据管理", "menuFlag":3,"params":params};
	var options = {"url":sId + ".card.do?pkCode=" + entityData["_DATA_"][0]["ENTITY_ID"] , "tTitle":Language.transStatic("rhWfCardView_string40"), "menuFlag":3,"params":params};
	Tab.open(options);	
}

/**
 * 选择补登的 固定意见
 */
rh.vi.wfCardView.prototype._chooseRegularMind = function(event, mindCode) {
	var _self = this;
	var inputName ="USUAL_CONTENT";

	var configStr = "SY_COMM_MIND_REGULAR,{'TARGET':'MIND_ID~REGULAR_MIND','SOURCE':'MIND_ID~MIND_CONTENT~MIND_VALUE','PKHIDE':true,'EXTWHERE':' and REGULAR_TYPE=^" + mindCode + "^', 'TYPE':'single'}";
	var options = {"itemCode":inputName, "config":configStr, "rebackCodes":inputName, "parHandler":_self._parHandler, "replaceCallBack":function(mind){
		jQuery("#USUAL_ID").val(mind.MIND_ID);
		jQuery("#USUAL_CONTENT").val(mind.MIND_CONTENT);
		jQuery("#TARGET_MIND").val(mind.MIND_CONTENT);
	}};

	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);	
}

/**
 * 补登意见
 */
rh.vi.wfCardView.prototype.cmBuDeng = function(event, actItem) {
	var _self = this;
	var leaders = StrToJson(actItem.leaders);
	if (null == leaders || "" == leaders) {
//		alert("当前没有补登领导可以选择!");
		alert(Language.transStatic("rhWfCardView_string41"));
		return;
	}
	
	if (!actItem.WFE_PARAM || actItem.WFE_PARAM.length==0) {
//		alert("工作流中没有配置补登意见的意见类型!");
		alert(Language.transStatic("rhWfCardView_string42"));
		return;
	}
	
	var bdConfig = StrToJson(actItem.WFE_PARAM);
	var mindCodeRtn = FireFly.byId("SY_COMM_MIND_CODE", bdConfig.MIND_CODE);
	if (mindCodeRtn[UIConst.RTN_MSG] && mindCodeRtn[UIConst.RTN_MSG].indexOf(UIConst.RTN_ERR) == 0) { //没有该意见类型， 提示检查工作流配置
//		alert("没有查到工作流中配置的意见编码，请检查意见编码是否存在!");
		alert(Language.transStatic("rhWfCardView_string43"));
		return;
	}
	
	var queryBean = {};
	queryBean.WF_NI_ID = _self.getNodeInstBean().NI_ID;
	queryBean.DATA_ID = _self._parHandler._pkCode;
	var budengMind = FireFly.doAct("SY_COMM_MIND", "getBudengMind", queryBean, false);
	
	// 补登意见弹出匡
	jQuery("#bdmind").dialog("destroy");
//	var winDialog = jQuery("<div style='background-color:#FFF;'></div>").addClass("selectDialog").attr("id", "bdmind").attr("title", "补登意见");
	var winDialog = jQuery("<div style='background-color:#FFF;'></div>").addClass("selectDialog").attr("id", "bdmind").attr("title", Language.transStatic("rhWfCardView_string44"));
	   
	jQuery("<table align='center' height='20px' border=1 width='90%'></table>").appendTo(winDialog);
	
	// 补登意见必要属性
	var tableBd = jQuery("<table align='center' width='90%'></table>").appendTo(winDialog);
	jQuery("<tr></tr>").appendTo(tableBd);
	var inputNameTr = jQuery("<tr></tr>").appendTo(tableBd);
	var inputNameTd1 =  jQuery("<td  width='80px'></td>").appendTo(inputNameTr);
	var inputNameTd2 =  jQuery("<td></td>").appendTo(inputNameTr);
//	var nameDiv = jQuery("<div>领导</div>").appendTo(inputNameTd1);
	var nameDiv = jQuery("<div>"+rhWfCardView_string45+"</div>").appendTo(inputNameTd1);
	var inputNameDiv = jQuery("<div class='ui-select-default' style='max-width: 202px;'></div>").appendTo(inputNameTd2);
	
	var users = jQuery("<select id='TARGET_USER' class='ui-select-default2' name='TARGET_USER' style='width: 200px;border:1px #CCC solid;' />")
			.appendTo(inputNameDiv);
	// foreach leaders
	jQuery.each(leaders, function(i, leader) {
		jQuery("<option value='" + leader.userCode + "'>").append(leader.userName).appendTo(users);
	});

	// 时间
	var bdtimeTr = jQuery("<tr></tr>").appendTo(tableBd);
	var bdtimeTd1 = jQuery("<td></td>").appendTo(bdtimeTr);
	var bdtimeTd2 = jQuery("<td></td>").appendTo(bdtimeTr);
//	jQuery("<div>补登时间</div>").appendTo(bdtimeTd1);
	jQuery("<div>"+rhWfCardView_string46+"</div>").appendTo(bdtimeTd1);
	var timeLi = jQuery("<div style='margin-bottom:10px;'></div>").appendTo(bdtimeTd2);
	jQuery("<input id='TARGET_TIME' class='Wdate ui-date-default' onfocus=\"WdatePicker({startDate:'%y-%MM-%dd %H:%m:%ss',dateFmt:'yyyy-MM-dd HH:mm:ss',alwaysUseStartDate:false})\"  "
			+ "type='text' style='width: 200px;border:1px #CCC solid;margin-left:0px;'/>").appendTo(timeLi);
	//给时间的默认值
	
	//意见类型
	var bdTypeTr = jQuery("<tr></tr>").appendTo(tableBd);
	var bdTypeTd1 = jQuery("<td></td>").appendTo(bdTypeTr);
	var bdTypeTd2 = jQuery("<td></td>").appendTo(bdTypeTr);
//	jQuery("<div>意见类型</div>").appendTo(bdTypeTd1);
	jQuery("<div>"+rhWfCardView_string47+"</div>").appendTo(bdTypeTd1);
	var bdTypeli = jQuery("<div style='margin-bottom:10px;'></div>").appendTo(bdTypeTd2);
	jQuery("<input id='MIND_CODE__NAME' type='text' class='ui-text-default ui-text-disabled' readonly='readonly' style='width: 200px;' />")
			.appendTo(bdTypeli);	
	jQuery("<input id='MIND_CODE' type='hidden' value='" + bdConfig.MIND_CODE + "'/>").appendTo(bdTypeli);	    
	
	// 意见输入
	var bdMindTr = jQuery("<tr></tr>").appendTo(tableBd);
	var bdMindTd1 = jQuery("<td></td>").appendTo(bdMindTr);
	var bdMindTd2 = jQuery("<td></td>").appendTo(bdMindTr);
//	jQuery("<div>补登意见</div>").appendTo(bdMindTd1);	
	jQuery("<div>"+Language.transStatic('rhWfCardView_string49')+"</div>").appendTo(bdMindTd1);
	
	var bdMindInput = jQuery("<div></div>").appendTo(bdMindTd2);
	if (mindCodeRtn.REGULAR_TYPE.length > 0 && mindCodeRtn.REGULAR_TYPE == 1) { //固定意见 , 构造查询选择
		var regularStr = "<div><input id='USUAL_CONTENT' class='ui-query-default fl' style='cursor:pointer;max-width:420px;' type='text' readonly='readonly'/>" +
		"<span id='chooseMindBD' class='iconChoose icon-input-select fr' style='float:left;cursor:pointer;width:30px;height:20px;'></span>" +
		"<span id='cancelMindBD' class='iconCancel btn-clear' style='float:left;cursor:pointer;width:30px;height:20px;'></span>" +
		"<input id='USUAL_ID' type='hidden' value=''/><input id='MIND_VALUE' type='hidden' value=''/></div>";
		
		jQuery(regularStr).appendTo(bdMindInput);
	}
	
	var bdMindInputObj = jQuery("<textarea cols='65' rows='7' id ='TARGET_MIND' name ='TARGET_MIND'  style='margin-left:0px;'></textarea>").focus()
			.appendTo(bdMindInput);	
	bdMindInputObj.css({"border": "1px solid #BBB","width":"97.8%","line-heigth":"1.4"});
	//常用批语
	var bdCYPiYuTr = jQuery("<tr></tr>").appendTo(tableBd);
	var bdCyPiyuTd1 = jQuery("<td></td>").appendTo(bdCYPiYuTr);
	var bdCyPiyuTd2 = jQuery("<td></td>").appendTo(bdCYPiYuTr);
	jQuery("<div></div>").appendTo(bdCyPiyuTd1);	
	
	var bdCyPiyuInput = jQuery("<div></div>").appendTo(bdCyPiyuTd2);	
//	var bdCypiyuLink =  jQuery("<a href='#'>常用批语</a>").appendTo(bdCyPiyuInput);	
	var bdCypiyuLink =  jQuery("<a href='#'>"+Language.transStatic('rhWfCardView_string48')+"</a>").appendTo(bdCyPiyuInput);
	var opts = {"typeCode":"MIND"
		,"optionType":"single"
		,"fieldObj":bdMindInputObj
		,"optObj":bdCypiyuLink};
	Select.usualContent(opts, _self._parHandler);

	jQuery("<table align='center' height='20px' border=1 width='90%'></table>").appendTo(winDialog);

	// 按钮-保存
	var btnTable = jQuery("<table align='center' width='50%'></table>").appendTo(winDialog);
	var bdBtnTr = jQuery("<tr></tr>").appendTo(btnTable);
	var bdBtnTd1 = jQuery("<td align='center'></td>").appendTo(bdBtnTr);
	var bdBtnTd2 = jQuery("<td align='center'></td>").appendTo(bdBtnTr);
//	var okBtn = jQuery("<a class='rh-icon rhGrid-btnBar-a rhCard-btnBar-wf'><span class='rh-icon-inner'>保存<span class='rh-icon-img btn-save'></span></span></a>")
	var okBtn = jQuery("<a class='rh-icon rhGrid-btnBar-a rhCard-btnBar-wf'><span class='rh-icon-inner'>"+Language.transStatic('rhCommentView_string9')+"<span class='rh-icon-img btn-save'></span></span></a>")
		.appendTo(bdBtnTd1);
	okBtn.bind("click", function() {
		var user = jQuery("#TARGET_USER").val();
		var uname = jQuery("#TARGET_USER").find('option:selected').text();
		var mindstr = jQuery("#TARGET_MIND").val();
		if (mindstr.length == 0) {
//			alert("请填写补登意见");
			alert(Language.transStatic("rhWfCardView_string50"));
			return;
		}
		if (jQuery("#chooseMindBD").length > 0 && jQuery("#USUAL_ID").val().length == 0) {
//			alert("请选择补登意见");
			alert(Language.transStatic("rhWfCardView_string50"));
			return;
		}
		
		// 保存意见
		var data = {};
		if (budengMind.MIND_ID) {
			data["MIND_ID"] = budengMind.MIND_ID;
		}
		data["MIND_CONTENT"] = mindstr;
		data["MIND_CODE"] = mindCodeRtn.CODE_ID;
		data["MIND_CODE_NAME"] = mindCodeRtn.CODE_NAME;
		data["SERV_ID"] = _self._parHandler.getServSrcId(); // 服务名
		data["DATA_ID"] = _self._parHandler._pkCode;
		data["MIND_TYPE"] = 1;
		data["WF_NI_ID"] = _self.getNodeInstBean().NI_ID;
		data["WF_NI_NAME"] = _self.getNodeInstBean().NODE_NAME;
		data["MIND_DIS_RULE"] = mindCodeRtn.MIND_DIS_RULE;
		data["TARGET_USER"] = user;
		data["MIND_TIME"] = jQuery("#TARGET_TIME").val();
		if (jQuery("#chooseMindBD").length > 0) {
			data["USUAL_ID"] = jQuery("#USUAL_ID").val();
			data["USUAL_CONTENT"] = jQuery("#USUAL_CONTENT").val();
		}
		var resultData = FireFly.doAct("SY_COMM_MIND", "saveBuDengMind", data);
		if (resultData[UIConst.RTN_MSG]
				&& resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
			winDialog.remove();
			if(_self.mind){
				_self.mind.refresh();
			}
		} else {
			winDialog.remove();
//			Tip.show("返回错误，请检查！" + JsonToStr(resultData), true);
			Tip.show(Language.transStatic("rhWfCardView_string51") + JsonToStr(resultData), true);
		}
	});

	// 按钮-取消
//	var cancelBtn = jQuery("<a class='rh-icon rhGrid-btnBar-a rhCard-btnBar-wf'><span class='rh-icon-inner'>取消<span class='rh-icon-img btn-clear'></span></span></a>")
	var cancelBtn = jQuery("<a class='rh-icon rhGrid-btnBar-a rhCard-btnBar-wf'><span class='rh-icon-inner'>"+Language.transStatic('rh_ui_card_string18')+"<span class='rh-icon-img btn-clear'></span></span></a>")
		.appendTo(bdBtnTd2);
	cancelBtn.bind("click", function() {
		winDialog.remove();
	});

	var posArray = [];
	if (event) {
		var cy = event.clientY;
		posArray[0] = "";
		posArray[1] = cy - 120;
	}
	winDialog.appendTo(jQuery("body"));
	jQuery("#bdmind").dialog({
		autoOpen : false,
		width : 640,
		height : 360,
		modal : true,
		resizable : false,
		position : posArray,
		open : function() {

		},
		close : function() {
			winDialog.remove();
		}
	});
	jQuery("#bdmind").dialog("open");
	jQuery(".ui-dialog-titlebar").last().css("display", "block");// 设置标题显示
	
	jQuery("#TARGET_TIME").val(rhDate.getCurentTime() + ":00");
	
	jQuery("#MIND_CODE__NAME").val(mindCodeRtn.CODE_NAME);
	if (budengMind.MIND_CONTENT) {
		jQuery("#TARGET_MIND").val(budengMind.MIND_CONTENT);
		if (jQuery("#USUAL_CONTENT").length > 0) {
			jQuery("#USUAL_CONTENT").val(budengMind.USUAL_CONTENT);
			jQuery("#USUAL_ID").val(budengMind.USUAL_ID);
		}
	}
	if (jQuery("#chooseMindBD").length > 0) {
		jQuery("#chooseMindBD").unbind("click").bind("click",function(){ //查询选择，
			_self._chooseRegularMind(event, mindCodeRtn.CODE_ID);
		});
		
		jQuery("#cancelMindBD").unbind("click").bind("click",function(){ //取消
			jQuery("#USUAL_ID").val("");
			jQuery("#USUAL_CONTENT").val("");
		});
	}
};

/**
 * 流程跟踪
 */
rh.vi.wfCardView.prototype.cmWfTracking = function() {
	var _self = this;

	var trackObj = {};
	trackObj._PK_ = _self._parHandler._pkCode;
	trackObj.SERV_ID = _self._parHandler.servId;
	trackObj.PI_ID = _self.procInstId;
	if (_self.getNodeInstBean() && _self.getNodeInstBean().NI_ID) {
		trackObj.NI_ID = _self.getNodeInstBean().NI_ID;
	}
	
	trackObj.INST_IF_RUNNING = _self.wfState;
	trackObj.act = "query";
	trackObj.S_FLAG = _self.getParHandler().getByIdData("S_FLAG");
	
	if (window.ICBC) {
		var queryView = new rh.vi.rhSelectListView({
			"config":"SY_WFE_TRACK",
//			"title":"跟踪信息"
			"title":Language.transStatic("rhWfCardView_string52")
		});
		queryView.show = function(event, positionArray, dialogSizeArray) {
			var _self = this;
			_self._workflowControl();
			_self._layout(event,positionArray,[0,400]);
			
			var div = jQuery("<div class='rh-select-container'></div>").css({
				"height":"314px",
				"overflow":"auto"
			})
			.attr("id",_self.sId + "-select-container").appendTo(_self.winDialog);
			trackObj = $.extend(trackObj, {
//				"title":"跟踪信息",
				"title":Language.transStatic("rhWfCardView_string52"),
			    "sId":_self.sId,
			    "readOnly":true,
			    "showTitleBarFlag":false,
			    "paramsFlag":true,
			    "transParams":$.extend({}, trackObj),
			    "pCon":div
			});
			var listView = new rh.vi.listView(trackObj);
			listView.show();
		};
		queryView.show();
	} else {
		var opts = {
			"url" : "SY_WFE_TRACK.list.do?type=single",
//			"tTitle" : "跟踪信息",
			"tTitle" : Language.transStatic("rhWfCardView_string52"),
			"params" : trackObj,
			"menuFlag" : 3
		};
		Tab.open(opts);
	}
};
/**
 * 图形化流程跟踪
 */
rh.vi.wfCardView.prototype.cmWfTrackFigure = function() {
	var _self = this;
	var params = {};
	params["PI_ID"] = _self.procInstId;
	params["INST_IF_RUNNING"] = _self.wfState;
	var opts = {"tTitle":"图形化流程跟踪","url":"SY_WFE_TRACK_FIGURE.show.do?data=" + JsonToStr(params),"params":params,"menuFlag":3};
	Tab.open(opts);
};
/**
 * 独占 ， 即开始处理文件 用于 送角色的时候，其中某人进流程，点独占，角色内其他人员就不能收到待办了
 */
rh.vi.wfCardView.prototype.duZhan = function() {
	var _self = this;
	var result = rh_processData(WfActConst.SERV_PROC + ".duZhan.do",
			_self.reqdata);
	if (result.rtnstr == "success") {
		Tip.show("操作成功", true);
	} else {
		Tip.show("操作失败", true);
	}
	_self._parHandler.refresh();
}

/**
 * 结束当前工作
 */
rh.vi.wfCardView.prototype.stopWfNode = function() {
	var _self = this;
	var result = rh_processData(WfActConst.SERV_PROC + ".stopWfNode.do",
			_self.reqdata);
	if (result.rtnstr == "success") {
//		Tip.show("操作成功", true);
		Tip.show(Language.transStatic("rhCommentView_string3"), true);
	} else {
//		Tip.show("操作失败", true);
		Tip.show(Language.transStatic("rhWfCardView_string21"), true);
	}
	_self._parHandler.refresh();
}

/**
 * 打印
 */
rh.vi.wfCardView.prototype.cmPrint = function(event,actObj) {
	var _self = this;
	
	//工行直接打印表单
	if (window.ICBC) {
		this.getParHandler().formCon.printThis({debug:false});
	} else {
		_self.reqdata.PK_CODE = _self._parHandler._pkCode;

		var formServId = _self._parHandler.servId;
		_self.reqdata.TMP_CODE = formServId;

		// 打印预览
		window.open(formServId + ".cmPrint.do?data=" + JsonToStr(_self.reqdata));
	}
}

/**
 * 打印审批单
 */
rh.vi.wfCardView.prototype.cmPrintAudit = function() {
	var _self = this;
	var servId = _self._parHandler.servId;
	var servSrcId = _self._parHandler._data["SERV_SRC_ID"];
	var dataId = _self._parHandler._pkCode;
	FireFly.doPrint(servId, servSrcId, dataId);
};

/**
 * 设置审签单打印模板
 */
rh.vi.wfCardView.prototype.setCmPrintAuditFtl = function(ftl) {
	this._auditFtlFile = ftl;
};

/*
 * 办结之前执行,业务代码可覆盖此方法
 */
rh.vi.wfCardView.prototype._beforeFinish = function() {
};

/**
 * 办结
 */
rh.vi.wfCardView.prototype.finish = function() {
	var _self = this;

//	if (!confirm("是否确认办结?")) {
	if (!confirm(Language.transStatic("rhWfCardView_string53"))) {	
		return false;
	}

	if (_self._beforeFinish() == false) {
		return false;
	}

	var result = rh_processData(WfActConst.SERV_PROC + ".finish.do",
			_self.reqdata);
	_self._parHandler.refresh();
	if (result.rtnstr == "success") {
//		alert("办结成功！");
		alert(Language.transStatic("rhWfCardView_string54"));
		// 关闭当前页面
		_self._parHandler.backClick();
	} else {
//		alert("办结未成功，请检查");
		alert(Language.transStatic("rhWfCardView_string55"));
	}
}

/*
 * 取消办结之前执行,业务代码可覆盖此方法
 */
rh.vi.wfCardView.prototype._beforeUndoFinish = function() {
};

/**
 * 取消办结
 */
rh.vi.wfCardView.prototype.undoFinish = function() {
	var _self = this;
	if (_self._beforeUndoFinish() == false) {
		return false;
	}

	var result = rh_processData(WfActConst.SERV_PROC + ".undoFinish.do",
			_self.reqdata);
	// 重新装载页面
//	_self._refreshView("处理完毕");
	_self._refreshView(Language.transStatic("rhCardView_string6"));
}

/**
 * 保存
 */
rh.vi.wfCardView.prototype.save = function() {
	var _self = this;

	_self._parHandler.saveForm();
}

/**
 * 收回
 */
rh.vi.wfCardView.prototype.withdraw = function(event,actItem) {
	var _self = this;
	try{
		_self._parHandler.shield();
		var niIds = new Array();
		if(actItem.wdlist){
			var wdlist = actItem.wdlist;
			if (wdlist.length == 0) {
//				alert("没有可以收回的流程。");
				alert(Language.transStatic("rhWfCardView_string56"));
			} else if (wdlist.length == 1) { //只有一个用户的请求需要被收会，则自动处理
				niIds.push(wdlist[0].NI_ID);
				_self._sendWithdrawReq(niIds.join(","));
			} else { //如果有多个用户请求需要被收回
				var arr = new Array();
//				arr.push("<div title='选择收回流程' id='withdrawDlg'><table class='ml10 mt10'>");
				arr.push("<div title='"+Language.transStatic('rhWfCardView_string58')+"' id='withdrawDlg'><table class='ml10 mt10'>");
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
//					},buttons: [{ text: "确定",click:function(){
					},buttons: [{ text: Language.transStatic("rh_ui_gridCard_string17"),click:function(){	
							jQuery("#withdrawDlg").find("input:checked").each(function(){
								niIds.push(jQuery(this).val());
							});
							
							_self._sendWithdrawReq(niIds.join(","));
							jQuery("#withdrawDlg").dialog("close");
							jQuery("#withdrawDlg").remove();
						}
//					},{text: "取消",click:function(){
					},{text: Language.transStatic("rh_ui_card_string18"),click:function(){	
							jQuery("#withdrawDlg").dialog("close");
							jQuery("#withdrawDlg").remove();
						}
					}]
				});
			}
		}
	} catch(e){
		alert(e.message);
	} finally{
		_self._parHandler.shieldHide();
	}
}

/**
 * 发送收回请求
 */
rh.vi.wfCardView.prototype._sendWithdrawReq = function(niIds){
	var _self = this;
	var servId = _self._parHandler.servId;
	var dataId = _self._parHandler._pkCode;
	
	var _data = {_PK_:dataId ,nextNiIds:niIds }; //合并请求参数

	//构建用户多机构缓存
	FireFly.doAct("SY_ORG_USER_INFO_SELF", "getMultiDeptContainsRoleByServId", {"PROC_SERV_ID":_self._parHandler.servId}, false);
	
	var formData = _self._parHandler.byIdData;
	var doUserDept = _self.reqdata["DO_USER_DEPT"];
	if(formData && doUserDept 
	   && doUserDept.indexOf(formData.S_USER) >= 0 
	   && doUserDept.indexOf(formData.S_DEPT) < 0){
	   	//若当前处理人为表单的拟稿人，且不是用户的挂职机构，则修改用户信息为虚拟出的机构部门信息，保持和表单的一致性
		_self.reqdata["DO_USER_DEPT"] = formData.S_USER+"^"+formData.S_DEPT;
	}
	
	jQuery.extend(true, _data, _self.reqdata);

	var result = FireFly.doAct(WfActConst.SERV_PROC,"withdraw",_data);
	
//	var result = rh_processData(WfActConst.SERV_PROC + ".withdraw.do",
//			_self.reqdata);
	// 重新装载页面

//	var url = servId + ".byid.do?data={_PK_:" + dataId + "}";
//	_self._parHandler.setParentRefresh();
//	_self._parHandler.resetReplaceUrl(url);
//	_self._parHandler.refresh();
	
//	_self._refreshView("处理完毕");
	_self._refreshView(Language.transStatic("rhCardView_string6"));
}

/**
 * 终止并发
 */
rh.vi.wfCardView.prototype.stopParallelWf = function() {
	var _self = this;
	var result = rh_processData(WfActConst.SERV_PROC + ".stopParallelWf.do",
			_self.reqdata);

	// 重新装载页面
//	var servId = _self._parHandler.servId;
//	var dataId = _self._parHandler._pkCode;
//	var url = servId + ".byid.do?data={_PK_:" + dataId ;
//	if(_self.isAgent()){
//		url += ",_AGENT_USER_:'" + _self.getAgentUserCode() + "'";
//	}
//	url += "}";
//	_self._parHandler.resetReplaceUrl(url);
//	_self._parHandler.refresh();
//	_self._refreshView("处理完毕");
	_self._refreshView(Language.transStatic("rhCardView_string6"));
}

/**
 * 删除
 */
rh.vi.wfCardView.prototype.deleteDoc = function() {
	var _self = this;
//	var confirmDel = confirm("确定删除该文件？");
	var confirmDel = confirm(Language.transStatic("rh_ui_card_string30"));
	if (confirmDel == false) {
		return;
	}

	_self.reqdata.SERV_ID = _self._parHandler.servId;
	_self.reqdata.DATA_ID = _self._parHandler._pkCode;
	var result = rh_processData(WfActConst.SERV_PROC + ".deleteDoc.do",
			_self.reqdata);
	if (result.rtnstr == "success") {
//		Tip.show("删除成功", true);
		Tip.show(Language.transStatic("rhCommentView_string16"), true);
		_self._parHandler.backClick();
	} else {
		alert(result.rtnstr);
		// Tip.show("删除失败",true);
	}
}

/**
 * 在页面的最下面显示 分发的详细信息，点连接打开 分发明细列表
 */
rh.vi.wfCardView.prototype._bldSendList = function() {
	var _self = this;

	var _cardSendObj = jQuery(
			"<div id='sendDtailBtn' style='margin:0 auto;background-color:white;width:98%;height:30px; line-height:30px;'></div>")
			.addClass("ui-form-default").addClass("center").addClass(
					"ui-corner-5").attr("height", 300);

	var _sendDetailHref = jQuery(
			"<a href='#' id='showSend"
					+ _self._parHandler.servId
					+ "'  style='margin-left:18px;padding-left:18px'>分发明细>></a>")
			.addClass("btn-cmFenFa");
	_sendDetailHref.appendTo(_cardSendObj);
	_cardSendObj.appendTo(_self._parHandler.formCon);

	_sendDetailHref
			.unbind("click")
			.bind(
					"click",
					function() {
						var sendObj = {};
						sendObj.SERV_ID = _self._parHandler.servId;
						sendObj.DATA_ID = _self._parHandler._pkCode;
						var strWhere = " and SERV_ID ='"
								+ _self._parHandler.servId
								+ "' and DATA_ID = '"
								+ _self._parHandler._pkCode + "'";
						sendObj._extWhere = strWhere;

						var url = "SY_COMM_SEND_DETAIL.list.do?data={'_extWhere':'"
								+ strWhere + "'}";
						if (_self.getAuthBean().userDoInWf == "true") {
							var opts = {
								"url" : url,
//								"tTitle" : "分发明细",
								"tTitle" : Language.transStatic("rhWfCardView_string59"),
								"params" : sendObj,
								"menuFlag" : 4
							};
							Tab.open(opts);
						} else {
							var opts = {
								"url" : url + "&readOnly=true",
//								"tTitle" : "分发明细",
								"tTitle" : Language.transStatic("rhWfCardView_string59"),
								"params" : sendObj,
								"menuFlag" : 4
							};
							Tab.open(opts);
						}
					});
}

/**
 * 隐藏所有的按钮
 */
rh.vi.wfCardView.prototype._hideAllBtns = function() {
	var _self = this;

	jQuery("#" + _self._parHandler.servId + "-mainTab").find("a").hide();
}

/**
 * @param varCode
 *            变量名称
 * @returns 指定变量的值，未找到指定变量则返回undefined
 */
rh.vi.wfCardView.prototype.getCustomVarContent = function(varCode) {
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
rh.vi.wfCardView.prototype.getDisplayMode = function() {
	return this._parHandler.byIdData.WF_DISPLAY_MODE || "";
}

/**
 * 关注，弹出列表页，可添加，删除，关注的事项
 */
rh.vi.wfCardView.prototype.cmSetAttention = function(event, actItem) {
	var _self = this;
	
	var attentionObj = {};
	attentionObj.PI_ID = _self.procInstId;
	attentionObj.S_USER = System.getVar("@USER_CODE@");
	attentionObj.act = "query";
	attentionObj.DATA_ID = _self._parHandler._pkCode;
	
	var url = "SY_COMM_ATTENTION.list.do?data="+ JsonToStr(attentionObj);
	var opts = {
		"url" : url,
//		"tTitle" : "关注",
		"tTitle" : Language.transStatic("rhWfCardView_string60"),
		"params" : attentionObj,
		"menuFlag" : 3
	};
	Tab.open(opts);	
}


/**
 * Act 定义的一些常量
 */
var WfActConst = {
	/** 流程定义的服务 */
	SERV_PROC : "SY_WFE_PROC"
};


/**
 * 我的收藏夹
 */
rh.vi.wfCardView.prototype.cmFavorite = function(event) {
	var _self = this;
	this.favoriteDialog(event,_self);
	this.showFavoriteItems(_self);
	jQuery("#favoriteForm input:button").addClass("rh-wf-card-favorite-button");
}
//弹出我的收藏夹对话框
rh.vi.wfCardView.prototype.favoriteDialog = function(event,_self) {
	var _this=this;
	this.dialogId = "favorite";	
	//设置jqueryUi的dialog参数
	this.winDialog = jQuery("<div style='background-color:#EFEFF2;'></div>").addClass("selectDialog").attr("id",this.dialogId).attr("title","我的收藏夹");
	this.winDialog.appendTo(jQuery("body"));
	var bodyWid = jQuery("body").width();
	var hei = 360;
    var wid = 700;
    var posArray = [30,30];
    if (event) {
	    var cy = event.clientY;
	    posArray[0] = "";
	    posArray[1] = cy;
    }
    //生成jqueryUi的dialog
	jQuery("#" + this.dialogId).dialog({
		autoOpen: false,
		height: hei,
		width: wid,
		modal: true,
		resizable:true,
		position:posArray,
		open: function() { 

		},
		close: function() {
			//关闭的时候进行数据保存
			rh.vi.wfCardView.prototype.favoriteDialogClose(_self);	
			//关闭对话框
			jQuery("#" + _this.dialogId).remove();
		}
	});
	//手动打开dialog
	var dialogObj = jQuery("#" + this.dialogId);
	dialogObj.dialog("open");
	dialogObj.focus();
    jQuery(".ui-dialog-titlebar").last().css("display","block");//设置标题显示
    dialogObj.parent().addClass("rh-bottom-right-radius rhSelectWidget-content");
}

//收藏夹对话框关闭时调用
rh.vi.wfCardView.prototype.favoriteDialogClose = function(_self){
	// 获取当前用户所有自定义的标签
	var allTag= this.allTag();
	//获取当前审批单的标签
	var selectedValue = this.selectedTag(_self);
	//获取当前流程服务ID
	var servId = _self._parHandler.servId;
	var servDataId = _self._parHandler._pkCode;	
	var tagId="";
	for(var i=0;i<(selectedValue._DATA_).length;i++){
		tagId +=(selectedValue._DATA_)[i].TAG_ID+",";
	}
	//获取storeTag的所有的标签
	var selectedLength = selectedValue._DATA_.length;
	var checkVal ="";
	//如果收藏标签上存在，数据库中不存在则添加
	jQuery("#storeTagDiv").find(":checkbox").each(function(){
		if(tagId.indexOf(jQuery(this).val())==-1){
			FireFly.cardAdd("SY_COMM_ENTITY_TAG",{"DATA_ID":servDataId,"SERV_ID":servId,"TAG_ID":jQuery(this).val()});
		}
		checkVal += jQuery(this).val()+",";
	});
	//如果收藏标签上不存在，数据库中存在 则删除
	var deleteData ="";
	for(var i=0;i<(selectedValue._DATA_).length;i++){
		if(checkVal.indexOf((selectedValue._DATA_)[i].TAG_ID)==-1){
			deleteData +=(selectedValue._DATA_)[i].ET_ID+",";
		}
	}
	if(deleteData !=""){
		var data={"_PK_":deleteData};
		FireFly.listDelete("SY_COMM_ENTITY_TAG",data);
	}	
}
rh.vi.wfCardView.prototype.tagCheckedChange = function(){
	//监听checkbox
	jQuery("#favoriteForm input:checkbox").unbind("click").bind("click",function(){
		jQuery(this).next().toggleClass("rh-wf-card-favorite-changecheckbox");
	});	
}
//收藏夹的按钮事件
rh.vi.wfCardView.prototype.favoriteBtnEvent = function(){
	var _self = this;
	return btnEvent = {
		//全选
		selectAll: function(){
			jQuery(this).parent().find(":checkbox").each(function(){
				jQuery(this).attr("checked", true);
				jQuery(this).next().addClass("rh-wf-card-favorite-changecheckbox");
			});
		},
		
		//全不选
		cancelAll: function(){
			jQuery(this).parent().find(":checkbox").each(function(){
				jQuery(this).attr("checked", false);
				jQuery(this).next().removeClass("rh-wf-card-favorite-changecheckbox");
			});
		},
		
		//storeTag的取消标签
		cancel: function(){
//			var r=confirm("您确定取消么？");
			var r=confirm(Language.transStatic("rhWfCardView_string61"));
			if(r==true){
				jQuery("#storeTagDiv").find(":checkbox").each(function(){
					if (jQuery(this).attr("checked")) {
						jQuery(this).attr("checked", false);
						jQuery(this).next().removeClass("rh-wf-card-favorite-changecheckbox");
						jQuery("#manageTagDiv").append(jQuery(this).parent());
					};
				});
			}
		},
		//manageTag的添加标签
		addTagFn: function(){
			jQuery("#addNewTag").show();
		},
		//manageTag的删除选中
		deleteTagFn: function(){
			var dataArr = [];
			jQuery("#manageTagDiv").find(":checkbox").each(function(){
				if (jQuery(this).attr("checked")) {
					dataArr.push(jQuery(this).val());
				};
			});
			if(dataArr.length>0){
//				var r=confirm("您确定取消么？");
				var r=confirm(Language.transStatic("rhWfCardView_string61"));
				if(r==true){
					var data = dataArr.toString();
					var datas = {
						"_PK_": data
					};
					//获取成功删除后的数据
					var deleteOkData = FireFly.listDelete("SY_COMM_TAG", datas);
					var deleteValue = deleteOkData._DELIDS_;
					var arrDelete = deleteValue.split(",");
					//删除页面上的标签
					for(var i=0;i<arrDelete.length;i++){
						jQuery("#manageTagDiv input[value="+arrDelete[i]+"]").parent().remove();
					}
				}
			}else{
//				alert("请选择您要删除的数据！");
				alert(Language.transStatic("rhWfCardView_string62"));
			}
		},
		//提交按钮事件
		submitFn: function(){
			jQuery("#manageTagDiv").find(":checkbox").each(function(){
				if (jQuery(this).attr("checked")) {
					jQuery(this).attr("checked", false);
					jQuery(this).next().removeClass("rh-wf-card-favorite-changecheckbox");
					jQuery("#storeTagDiv").append(jQuery(this).parent());
				};
			});
		},
		
		//隐藏div的确定按钮事件
		addNewTagBtn: function(){
			var newTagValue = jQuery("#newTagName").val();
			if(newTagValue !=""&& newTagValue!=null){
				if(newTagValue.length<7){
					var data={"TAG_NAME":newTagValue,"TAG_LEVEL":"PRIVATE","TAG_TYPE":"FAVORITE"}
					var newTagData = FireFly.cardAdd("SY_COMM_TAG",data);
					//判断标签名不能为空！
					if(newTagData.TAG_ID != "" && newTagData.TAG_ID !=null){
						var check = jQuery("<label><input type='checkbox' value="+newTagData.TAG_ID+"><span>"+newTagValue+"</span></label>");
						var check1 = jQuery("#manageTagDiv label").first().length;
						if(check1 !=0){
							jQuery("#manageTagDiv label").first().before(check);
						}else {
							jQuery("#manageTagDiv").append(check);
						}
						jQuery("#favoriteForm input:checkbox").addClass("rh-wf-card-favorite-checkbox");
						jQuery("#favoriteForm span").addClass("rh-wf-card-favorite-span");
						jQuery("#favoriteForm label").addClass("rh-wf-card-favorite-lable");
						_self.tagCheckedChange();
					}
				}else{
//					alert("收藏夹名称不超过6个字");
					alert(Language.transStatic("rhWfCardView_string63"));
				}
			}
		},
		//隐藏div的取消按钮事件
		cancelNewTagBtn: function(){
			jQuery("#newTagName").val("");
			jQuery("#addNewTag").hide();
		}
	}
}
//显示我的收藏夹对话框内容
rh.vi.wfCardView.prototype.showFavoriteItems = function(_self){
	//storeTag
	var storeTag = jQuery("<div style='height:150px;width: 100%;'></div>").attr("id","storeTag");
	var storeTagBtn = jQuery("<div style='height: 20%;width: 90%;margin-left: 5%;margin-top: 10px;'></div>").attr("id","storeTagBtn");
	var storeTagDiv = jQuery("<div style='border: solid 1px rgb(167, 167, 167);height: 70%;width: 90%;margin-left: 5%;background-color: white;'></div>").attr("id","storeTagDiv");
	storeTag.append(storeTagBtn);
	storeTag.append(storeTagDiv);
	//manageTag
	var manageTag = jQuery("<div style='height:150px;width: 100%;'></div>").attr("id","manageTag");
	var manageTagBtn = jQuery("<div style='height: 20%;width: 90%;margin-left: 5%;'></div>").attr("id","manageTagBtn");
	var manageTagDiv = jQuery("<div style='border: solid 1px rgb(167, 167, 167);height: 70%;width: 90%;margin-left: 5%;background-color: white;'></div>").attr("id","manageTagDiv");
	manageTag.append(manageTagBtn);
	manageTag.append(manageTagDiv);
	//addNewTag
	var addNewTag = jQuery("<div style='display:none;margin-left: 5%;'></div>").attr("id","addNewTag");
	//form表单
	var form = jQuery("<div></div>").attr("id","favoriteForm");
//	var storeTagText = jQuery("<a>已保存至收藏夹:</a>").addClass("rh-wf-card-favorite-text");
//	var manageTagText = jQuery("<a>可选收藏夹:</a>").addClass("rh-wf-card-favorite-text");
//	var selectAllStore = jQuery("<input id='selectAllStore' style='display: none;' type='button' value='全选' />");
//	var cancelAllStore = jQuery("<input id='cancelAllStore' style='display: none;' type='button' value='全不选' />");
//	var cancelTag = jQuery("<input id='cancelTag' type='button' value='取消收藏' />");
//	var addTag = jQuery("<input id='addTag' type='button' value=新建收藏夹 />");
//	var selectAllManage = jQuery("<input id='selectAllManage' style='display: none;' type='button' value='全选' />");
//	var cancelAllManage = jQuery("<input id='cancelAllManage' style='display: none;' type='button' value='全不选' />");
//	var deleteTag = jQuery("<input id='deleteTag' type='button' value='删除收藏夹'/>");
//	var submit = jQuery("<input id='submit' type='button' value='添加至收藏夹' />");
//	var newTagText = jQuery("<a>收藏夹名称:</a>");
//	var newTagName = jQuery("<input type='text'>").attr("id","newTagName");
//	var addNewTagBtn = jQuery("<input type='button' style='margin-left: 3px;'>").attr("id","addNewTagBtn").attr("value","添加");
//	var cancelNewTagBtn = jQuery("<input type='button'>").attr("id","cancelNewTagBtn").attr("value","取消");
	var storeTagText = jQuery("<a>"+Language.transStatic('rhWfCardView_string64')+"</a>").addClass("rh-wf-card-favorite-text");
	var manageTagText = jQuery("<a>"+Language.transStatic('rhWfCardView_string65')+"</a>").addClass("rh-wf-card-favorite-text");
	var selectAllStore = jQuery("<input id='selectAllStore' style='display: none;' type='button' value='"+Language.transStatic('rhWfCardView_string66')+"' />");
	var cancelAllStore = jQuery("<input id='cancelAllStore' style='display: none;' type='button' value='"+Language.transStatic('rhWfCardView_string67')+"' />");
	var cancelTag = jQuery("<input id='cancelTag' type='button' value='"+Language.transStatic('rhWfCardView_string68')+"' />");
	var addTag = jQuery("<input id='addTag' type='button' value='"+Language.transStatic('rhWfCardView_string69')+"' />");
	var selectAllManage = jQuery("<input id='selectAllManage' style='display: none;' type='button' value='"+Language.transStatic('rhWfCardView_string66')+"' />");
	var cancelAllManage = jQuery("<input id='cancelAllManage' style='display: none;' type='button' value='"+Language.transStatic('rhWfCardView_string67')+"' />");
	var deleteTag = jQuery("<input id='deleteTag' type='button' value='"+Language.transStatic('rhWfCardView_string70')+"'/>");
	var submit = jQuery("<input id='submit' type='button' value='"+Language.transStatic('rhWfCardView_string71')+"' />");
	var newTagText = jQuery("<a>"+Language.transStatic('rhWfCardView_string72')+"</a>");
	var newTagName = jQuery("<input type='text'>").attr("id","newTagName");
	var addNewTagBtn = jQuery("<input type='button' style='margin-left: 3px;'>").attr("id","addNewTagBtn").attr("value",Language.transStatic('rhWfCardView_string73'));
	var cancelNewTagBtn = jQuery("<input type='button'>").attr("id","cancelNewTagBtn").attr("value",Language.transStatic('rh_ui_card_string18'));
	//建立关联
	storeTagBtn.append(storeTagText);
	storeTagBtn.append(cancelTag);
	storeTagBtn.append(selectAllStore);
	storeTagBtn.append(cancelAllStore);
	//遍历当前审批单的标签。
	var selectedValue = this.selectedTag(_self);
	var selectedValues = "";
	var allTag = this.allTag();
	var allTags = "";
	for(var i=0;i<allTag._DATA_.length; i++){
		allTags += (allTag._DATA_)[i].TAG_ID;
	}
	for(var i=0;i<selectedValue._DATA_.length; i++){
		var tagId = (selectedValue._DATA_)[i].TAG_ID;
		if(allTags.indexOf(tagId) >=0){
			var tagObj = FireFly.byId("SY_COMM_TAG",tagId);
			var check = jQuery("<label><input type='checkbox' value="+tagId+"><span>"+tagObj.TAG_NAME+"</span></label>");
			storeTagDiv.append(check);
			selectedValues +=  tagId + ",";
		}
	}
	manageTagBtn.append(manageTagText);
	manageTagBtn.append(submit);
	manageTagBtn.append(selectAllManage);
	manageTagBtn.append(cancelAllManage);
	manageTagBtn.append(addTag);
	manageTagBtn.append(deleteTag);
	//获取当前用户所有自定义的标签
	
	for(var i=0;i<allTag._DATA_.length; i++){
		var tagId = (allTag._DATA_)[i].TAG_ID;
		var tagName = (allTag._DATA_)[i].TAG_NAME;
		if(selectedValues.indexOf(tagId) == -1){
			var check = jQuery("<label><input type='checkbox' value="+tagId+"><span>"+tagName+"</span></label>");
			manageTagDiv.append(check);
		}
		
	}
	//新添加标签
	addNewTag.append(newTagText);
	addNewTag.append(newTagName);
	addNewTag.append(addNewTagBtn);
	addNewTag.append(cancelNewTagBtn);
	
	form.append(storeTag);
	form.append(manageTag);
	form.append(addNewTag);
	//初始化事件	
	//storeTag
	selectAllStore.unbind("click").bind("click",this.favoriteBtnEvent().selectAll);
	cancelAllStore.unbind("click").bind("click",this.favoriteBtnEvent().cancelAll);
	cancelTag.unbind("click").bind("click",this.favoriteBtnEvent().cancel);
	//manageTag
	selectAllManage.unbind("click").bind("click",this.favoriteBtnEvent().selectAll);
	cancelAllManage.unbind("click").bind("click",this.favoriteBtnEvent().cancelAll);
	addTag.unbind("click").bind("click",this.favoriteBtnEvent().addTagFn);
	deleteTag.unbind("click").bind("click",this.favoriteBtnEvent().deleteTagFn);
	submit.unbind("click").bind("click",this.favoriteBtnEvent().submitFn);
	addNewTagBtn.unbind("click").bind("click",this.favoriteBtnEvent().addNewTagBtn);
	cancelNewTagBtn.unbind("click").bind("click",this.favoriteBtnEvent().cancelNewTagBtn);
	//显示
	jQuery("#favorite").append(form);
	jQuery("#favoriteForm input:checkbox").addClass("rh-wf-card-favorite-checkbox");
	jQuery("#favoriteForm span").addClass("rh-wf-card-favorite-span");
	jQuery("#favoriteForm label").addClass("rh-wf-card-favorite-lable");
	//监听复选框的状态
	this.tagCheckedChange();
}
//如果收藏标签中有则在标签管理中不显示
rh.vi.wfCardView.prototype.compareTag = function(){
	jQuery("#storeTagDiv").find(":checkbox").each(function(){
		var storeTagValue =	jQuery(this).val();
		jQuery("#manageTagDiv").find(":checkbox").each(function(){
			if(jQuery(this).val()==storeTagValue){
				jQuery(this).parent().remove();
			}
		})
	})
}
//获取当前审批单的标签
rh.vi.wfCardView.prototype.selectedTag = function(_self){
	//获取关联审批ID
	var servDataId = _self._parHandler._pkCode;
	var data= {};
	data["_searchWhere"] =" and DATA_ID='"+servDataId+"'";
	var selectedValue = FireFly.getListData("SY_COMM_ENTITY_TAG",data);
	return selectedValue;
}
//获取当前用户所有自定义的标签
rh.vi.wfCardView.prototype.allTag = function(){
	//获取当前用户所有自定义的标签
	var data = {"_searchWhere":" and TAG_TYPE = 'FAVORITE'"};
	data["_NOPAGE_"] = true;
	var allTag = FireFly.getListData("SY_COMM_TAG",data);
	return allTag;
};


//公文发布
rh.vi.wfCardView.prototype.publish = function() {
	var _self = this;
	//获取[是否公开]对象
	var isOpen = _self._parHandler.getItem("ISOPEN").getValue() || "";
	if ((isOpen == "") || (isOpen != UIConst.YES)) {
//		_self._parHandler.cardBarTipError("请先将此公文公开");
		_self._parHandler.cardBarTipError(Language.transStatic('rhWfCardView_string74'));
		return false;
	} else {
		//获取[是否公开]对象
		var openType = _self._parHandler.getItem("OPEN_TYPE").getValue() || "";
		if (openType == "") {
//			_self._parHandler.cardBarTipError("请先选择此公文公开类型");
			_self._parHandler.cardBarTipError(Language.transStatic('rhWfCardView_string75'));
			return false;
		} else {
			var data = {"_PK_":_self._parHandler._pkCode,
						"ISOPEN":_self._parHandler.getItem("ISOPEN").getValue(),
						"OPEN_TYPE":_self._parHandler.getItem("OPEN_TYPE").getValue()
					   };
			if(!_self._getFaBuData(data)){
//				_self._parHandler.cardBarTipError("请先保存数据！");
				_self._parHandler.cardBarTipError(Language.transStatic('rhWfCardView_string76'));
				return false;
			}
			var returnObj = FireFly.doAct("OA_GW_TMPL_FW_PUBLISH","operatePublishData",data);
			if (returnObj["MSG"] == "OK") {
//				_self._parHandler.cardBarTip("发布成功！");
				_self._parHandler.cardBarTip(Language.transStatic('rhWfCardView_string77'));
				_self._parHandler.refresh();
			} else {
//				_self._parHandler.cardBarTipError("发布失败！");
				_self._parHandler.cardBarTipError(Language.transStatic('rhWfCardView_string78'));
			}
		}
	}
};

/**
 * 获取公文发布数据
 */
rh.vi.wfCardView.prototype._getFaBuData = function (data){
	var _self = this;
	var changeData = _self._parHandler.getChangeData();
	//如果修改未保存
	if (changeData["ISOPEN"] || changeData["OPEN_TYPE"]) {
		return false;
	}
	//总公司
	if (data["OPEN_TYPE"] == "1") {
		data["OPEN_TYPE"] = System.getVar("@ODEPT_CODE@");
	//总省公司
	} else if (data["OPEN_TYPE"] == "2") {
		data["OPEN_TYPE"] = "ZS";
	//全国
	} else if (data["OPEN_TYPE"] == "3") {
		data["OPEN_TYPE"] = "RPUB";
	}
	return true;
};
/**
 * 获取工作流当前控制卡片的状态
 */
rh.vi.wfCardView.prototype.getEntirelyControl = function (){
	return this._entirelyControl;
};

/**
 * 取得当前审批单的办理用。兼容委托办理情况
 */
rh.vi.wfCardView.prototype.getDoUserBean = function(){
	//增加委托用户USER_CODE
	var _agentUserBean = this._parHandler.getByIdData("_AGENT_USER_BEAN_");
	if(_agentUserBean && _agentUserBean.length > 0){
		return _agentUserBean;
	}
	return System.getUserBean();
};

/**
 * 是否委托状态
 */
rh.vi.wfCardView.prototype.isAgent = function(){
	var _agentUser = this._parHandler.getByIdData("_AGENT_USER_");
	if(_agentUser != ""){
		return true;	
	}
	return false;
};

/**取得委托用户的Code**/
rh.vi.wfCardView.prototype.getAgentUserCode = function(){
	var _agentUser = this._parHandler.getByIdData("_AGENT_USER_");
	if(_agentUser != ""){
		return  _agentUser;
	}
	
	return undefined;
};

/**
 * 如果有委托办理，则增加委托办理人标识
 */
rh.vi.wfCardView.prototype.appendAgentUserFlagTo = function(reqdata){
	//增加委托用户USER_CODE
	var _agentUser = this._parHandler.getByIdData("_AGENT_USER_");
	if(_agentUser != ""){
		reqdata["_AGENT_USER_"] = _agentUser;
	}
	// 是否包含DO_USER_DEPT信息
	var authBean = this.getAuthBean();
	if((authBean != undefined) && authBean.DO_USER_DEPT) {
		reqdata["DO_USER_DEPT"] = authBean.DO_USER_DEPT;
	}
};

/**
 * 同意并送交
 */
rh.vi.wfCardView.prototype.cmAgreeSend = function() {
	var _self = this;
	var returnMindFlag = false;
	if (!_self.mind) {
		returnMindFlag = _self.isExitMind();
	} else {
		returnMindFlag = _self.mind.saveAgreeSendMind();
	}
	//getMindCodeBean //意见codeBean
	//getNodeMindObj //意见类型
	//保存成功，弹出送交树
	if (returnMindFlag) {
		var thisActObj = _self._getBtn("cmSaveAndSend");
		var cmAgreeSendObj = _self._getBtn("cmAgreeSend");
		if (cmAgreeSendObj.layoutObj.find("div").length <= 0) {
			var cmCloneSendDivObj = thisActObj.layoutObj.find("div").clone();
			cmCloneSendDivObj.css({"display":"block"});
			var eventAClick = cmCloneSendDivObj.find("a");
			var eventOldDiv = thisActObj.layoutObj.find("div");
			var eventOldClick = eventOldDiv.find("a");
			for (var i = 0; i < eventAClick.length; i++) {
				jQuery(eventAClick[i]).unbind("click").bind("click", {"row":i},function(event){
					jQuery(eventOldClick[event.data.row]).click();
					eventOldDiv.css({"display":"none"});
				});
			}
			cmAgreeSendObj.layoutObj.append(cmCloneSendDivObj);
		} else {
			cmAgreeSendObj.layoutObj.find("div").css({"display":"block"});
		}
	}
};

/**
 * 已阅并送交
 */
rh.vi.wfCardView.prototype.cmCounterSend = function() {
	var _self = this;
	var returnMindFlag = false;
	if (!_self.mind) {
//		returnMindFlag = _self.isExitMind("已阅");
		returnMindFlag = _self.isExitMind(Language.transStatic('rhWfCardView_string79'));
	} else {
//		returnMindFlag = _self.mind.saveAgreeSendMind("已阅");
		returnMindFlag = _self.mind.saveAgreeSendMind(Language.transStatic('rhWfCardView_string79'));
	}
	//getMindCodeBean //意见codeBean
	//getNodeMindObj //意见类型
	//保存成功，弹出送交树
	if (returnMindFlag) {
		var thisActObj = _self._getBtn("cmSaveAndSend");
		var cmCounterSendObj = _self._getBtn("cmCounterSend");
		if (cmCounterSendObj.layoutObj.find("div").length <= 0) {
			var cmCloneSendDivObj = thisActObj.layoutObj.find("div").clone();
			cmCloneSendDivObj.css({"display":"block"});
			var eventAClick = cmCloneSendDivObj.find("a");
			var eventOldDiv = thisActObj.layoutObj.find("div");
			var eventOldClick = eventOldDiv.find("a");
			for (var i = 0; i < eventAClick.length; i++) {
				jQuery(eventAClick[i]).unbind("click").bind("click", {"row":i},function(event){
					jQuery(eventOldClick[event.data.row]).click();
					eventOldDiv.css({"display":"none"});
				});
			}
			cmCounterSendObj.layoutObj.append(cmCloneSendDivObj);
		} else {
			cmCounterSendObj.layoutObj.find("div").css({"display":"block"});
		}
	}
};

/**
 * 是否已存在意见
 */
rh.vi.wfCardView.prototype.isExitMind = function(mindText) {
	var _self = this;
	var mindCodeBean = _self.getMindCodeBean();
	if (!mindCodeBean) { //看看是否为普通意见
		return false;
	}
	var mindMustReq = {};
	var nodeInstBean = _self.getNodeInstBean();
	mindMustReq.WF_NI_ID = nodeInstBean.NI_ID;
	mindMustReq.DATA_ID = _self._parHandler._pkCode;
	mindMustReq.SERV_ID = _self._parHandler.servId;
	mindMustReq.SERV_PID = _self._parHandler._servPId;
	mindMustReq.MIND_CODE = mindCodeBean.CODE_ID;
	mindMustReq.MIND_CODE_NAME = mindCodeBean.CODE_NAME;
//	mindMustReq.MIND_CONTENT = mindText || "同意";
	mindMustReq.MIND_CONTENT = mindText || Language.transStatic('rhWfCardView_string80');
	mindMustReq.MIND_DIS_RULE = mindCodeBean.MIND_DIS_RULE;
	mindMustReq.MIND_TYPE = mindCodeBean.MIND_TYPE;
	mindMustReq.S_FLAG = mindCodeBean.S_FLAG;
	mindMustReq.WF_NI_NAME=  nodeInstBean.NODE_NAME;
	var rtnData = FireFly.doAct("SY_COMM_MIND", "isExitMind", mindMustReq, false);
	if (rtnData["_MSG_"].indexOf("OK,") >= 0) {
		return true;
	}
	return false;
};

/**
 * 弹出对话框
 */
rh.vi.wfCardView.prototype.getDialog = function(dialogId,title,params){
	//设置jqueryUi的dialog参数
	if(title == null){
		title = ""
	}
	var winDialog = jQuery("<div></div>").addClass("selectDialog ui-form-default").attr("id",dialogId).attr("title",title);
	winDialog.appendTo(jQuery("body"));
	/*if (!top.frameInfos["opening"] || !top.frameInfos[top.frameInfos["opening"]]) {
		if (window.console) {
			console.log(top.frameInfos["opening"]);
		}
	}*/
	var defaultOpt = {
		autoOpen: false,
		height: 400,
		width: 600,
		modal: true,
		resizable:false,
		position:[30,30],
		show:"slide",
		open: function(){
			try {
				top.jQuery("body").css("overflow-y","hidden");
			} catch(e) {
				jQuery("body").css("overflow-y","hidden");
			}
//			top.frameInfos[top.frameInfos["opening"]] = true;
		},
		close: function() {
			try {
				top.jQuery("body").css("overflow-y","auto");
			} catch(e) {
				jQuery("body").css("overflow-y","auto");
			}
			
//			top.frameInfos[top.frameInfos["opening"]] = false;
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
//  Tip.show("努力加载中...",null,jQuery(".ui-dialog-title",winDialog).last());
  Tip.show(Language.transStatic('rhWfCardView_string81'),null,jQuery(".ui-dialog-title",winDialog).last());
  return winDialog;
};

/**
 * 单独意见留言
 */
rh.vi.wfCardView.prototype.cmPriMind = function(){
	var _self = this;
	var card = _self._parHandler;
	var diaFuns = {
		"confirm": function(){},
		"cancel": function(){}
	};
	var lyMindCode = "BLY-0002";
	
	//0获取已填数据
	var where = " and DATA_ID = '" + card.getPKCode() + "' and WF_NI_ID = '" + _self.getNodeInstBean().NI_ID + "' and MIND_CODE = '" + lyMindCode + "'";
	FireFly.doAct("SY_COMM_MIND", "finds", {"_WHERE_": where} ,false, false, function(res){
		if (res["_DATA_"]) {
			var lyData = res["_DATA_"][0] || {};
			
			//1构造窗口
			var winOptions = {
				autoOpen: false,
				height: 200,
				width: 500,
				modal: true,
				resizable: false,
				position: [(jQuery("body").width() - 730)/2,event.clientY-10],
				buttons:{
					"确定": function(){
//					Language.transStatic('rh_ui_gridCard_string17'): function(){	
						(diaFuns.confirm)();
					},
					"取消": function(){
//					Language.transStatic('rh_ui_card_string18'): function(){	
						(diaFuns.cancel)();
					}
				}
			};
//			var priMindDialog = _self.getDialog("cmPriMind_dialog", "给行领导留言", winOptions);
			var priMindDialog = _self.getDialog("cmPriMind_dialog", Language.transStatic('rhWfCardView_string82'), winOptions);
			priMindDialog.css({"background-color": "white"}); // 背景颜色修改改成白色突出内容
			
			//2构造输入区域
			var textAreaObj = jQuery("<textarea></textarea>");
			textAreaObj.css({
				"width": "460px",
				"height": "100px",
				"margin": "20px 16px",
				"padding": "2px 2px"
			}).val(lyData["MIND_CONTENT"] || "");
			priMindDialog.append(textAreaObj);
			
			//3绑定送交方法
			diaFuns.confirm = function(){
				var mindContent = textAreaObj.val();
				if (!mindContent) {
					try {
//						top.Tip.addTip("意见不能为空", "warn");
						top.Tip.addTip(Language.transStatic('rhWfCardView_string83'), "warn");
					} catch (e) {
//						Tip.addTip("意见不能为空", "warn");
						Tip.addTip(Language.transStatic('rhWfCardView_string83'), "warn");
						
					}
					return false;
				}
				var data = {
					"DATA_ID": card.getPKCode(),
					"MIND_CODE": lyMindCode,
//					"MIND_CODE_NAME": "总经理给行领导留言",
					"MIND_CODE_NAME": Language.transStatic('rhWfCardView_string84'),
					"MIND_CONTENT": textAreaObj.val(),
					"MIND_DIS_RULE": "3",
					"MIND_FILE": "",
					"MIND_ID": lyData["MIND_ID"] || "",
					"MIND_TYPE": "1",
					"SERV_ID": card.servId,
					"S_FLAG": 1,
					"S_TDEPT": System.getVar('@TDEPT_CODE@'),
					"S_USER": System.getVar('@USER_CODE@'),
					"WF_NI_ID": _self.getNodeInstBean().NI_ID,
					"WF_NI_NAME": _self.getNodeInstBean().NODE_NAME,
					"_ADD_": !lyData["MIND_ID"],
					"_PK_": lyData["MIND_ID"] || ""
				}
				var resultData = FireFly.doAct("SY_COMM_MIND", "save", data);
				if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
					try {
//						top.Tip.addTip("保存成功");
						top.Tip.addTip(Language.transStatic('rhCommentView_string10'));
					} catch (e) {
//						Tip.addTip("保存成功");
						Tip.addTip(Language.transStatic('rhCommentView_string10'));
					}
					diaFuns.cancel();
					setTimeout(function(){
						card.refresh();
					}, 200);
				} else {
					try {
//						top.Tip.addTip("保存失败", "error");
						top.Tip.addTip(Language.transStatic('rhWfCardView_string85'), "error");
					} catch (e) {
//						Tip.addTip("保存失败", "error");
						Tip.addTip(Language.transStatic('rhWfCardView_string85'), "error");
					}
				}
			};
			diaFuns.cancel = function(){
				priMindDialog.closest(".ui-dialog").find(".ui-icon-closethick").click();
			};
		} else {
			try {
//				top.Tip.addTip("获取意见数据出错", "error");
				top.Tip.addTip(Language.transStatic('rhWfCardView_string86'), "error");
			} catch (e) {
//				Tip.addTip("获取意见数据出错", "error");
				Tip.addTip(Language.transStatic('rhWfCardView_string86'), "error");
			}
		}
	});
};

/**
 * 构造完成并发送对话框（渲染状态为12或112时）
 * @param bool 是否重置默认值
 */
rh.vi.wfCardView.prototype.bldNextConfirmWindow = function(bool){
	var _self = this;

	/**
	 * 还原意见默认值
	 */
	if (bool) {
		var genMindList = _self._parHandler.byIdData.mindCodeBean;
		var mindListLen = genMindList.length;
		for (var index = 0; index < mindListLen; index++) {
			var def = genMindList[index];
			if (def.CODE_ID in this._mindDefaultValues) {
				def.DEFAULT_CONTENT = this._mindDefaultValues[def.CODE_ID];
				delete this._mindDefaultValues[def.CODE_ID];
			}
		}
		var terminalMind = _self.getTerminalMind();
		if (terminalMind.CODE_ID in this._mindDefaultValues) {
			terminalMind.DEFAULT_CONTENT = this._mindDefaultValues[terminalMind.CODE_ID];
			delete this._mindDefaultValues[terminalMind.CODE_ID];
		}
	}
	
	
	var extOpts = _self.beforeBldNextConfirmWindow();
	
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
			// "保存": function(){
			// 	isSave = true;
			// 	(diaFuns.save)();
			// },
			"确定": function(){
//			Language.transStatic('rh_ui_gridCard_string17'): function(){	
				(diaFuns.confirm)();	
			},
			"取消": function(){
//			Language.transStatic('rh_ui_card_string18'): function(){	
				(diaFuns.cancel)(nextDialog);
				try {
					top.jQuery("body").css("overflow-y","auto");
				} catch(e) {
					jQuery("body").css("overflow-y","auto");
				}
//				top.frameInfos[top.frameInfos["opening"]] = false;
			}
		},
		beforeClose: function(){
			
			if (isSave) {
				isSave = false;
				return true;
			}
			
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
			
			// if (hasContent && confirm("是否需要暂存？")) {
			// 	isSave = true;
			// 	(diaFuns.save)();
			// 	return true;
			// } else {
			// 	return true;
			// }
			
			return true;
		},
		close: function() {
			try {
				top.jQuery("body").css("overflow-y","auto");
			} catch (e) {
				jQuery("body").css("overflow-y","auto");
			}
//			top.frameInfos[top.frameInfos["opening"]] = false;
			nextDialog.remove();
		}
	};
	nextDialog = _self.getDialog("cmSaveAndSend_dialog", "处理完毕", winOptions);
	nextDialog.css({"background-color":"white"}); // 背景颜色修改改成白色突出内容
	nextDialog.parent().addClass('rh-next-dialog');
	
	//2构造下一环节设置区域
	var rhNextContainer = jQuery("<div id='rh-next' class='rh-next'></div>");
	
	//当前环节
//	var opts = [{'type':'text', 'id':'currentStep', 'label':'当前环节', 'name':_self.getNodeInstBean().NODE_NAME, 'value':_self.getNodeInstBean().NI_ID, 'sort':100}];
	var opts = [{'type':'text', 'id':'currentStep', 'label':Language.transStatic('rhWfCardViewNodeExtends_string3'), 'name':_self.getNodeInstBean().NODE_NAME, 'value':_self.getNodeInstBean().NI_ID, 'sort':100}];
	//构造 处理选择的部分
	_self.bldWfSelect(opts);
	
	//构造送交下一步的，节点及人员
	_self.bldNextStep(opts);
	
	//构造分发(阅知)区域，先判断是否定义了
	if (_self.getAuthBean().showYueZhi == 1) {
		var configStr = "@com.rh.core.serv.send.SimpleFenfaDict,{'TYPE':'multi','CHECKBOX':true,'extendDicSetting':{'rhexpand':false,'expandLevel':1,'cascadecheck':true,'checkParent':false,'childOnly':true}}";
//		var yuezhi = {'type':'tree', 'id':'yuezhiSelect', 'label':'阅知', 'value':'', 'dictConfig':configStr ,'sort':400, "params": {"displaySendSchm": false, "displayScope": "tdept"}};
		var yuezhi = {'type':'tree', 'id':'yuezhiSelect', 'label':Language.transStatic('rhWfCardViewNodeExtends_string4'), 'value':'', 'dictConfig':configStr ,'sort':400, "params": {"displaySendSchm": false, "displayScope": "tdept"}};
		
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
			top.Tip.show(Language.transStatic('rhWfCardViewNodeExtends_string5'));
		} catch (e) {
//			Tip.show("草稿成功保存");
			Tip.show(Language.transStatic('rhWfCardViewNodeExtends_string5'));
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
	
	//需要初始化的，eg:如果上次暂存的是ACT, 则把选人的隐藏，
	// _self.initUserSelect();
	
	//在构造处理完毕完成之后要做的事情
	_self.afterBldNextConfirmWindow();
};

/**
 * 获取可选的下一步的节点信息，返回对象数组
 * @return {}
 */
rh.vi.wfCardView.prototype.getParallelNextSelectNodes = function (){
	var _self = this;
	var nextStepName = WfNextStep.nextStep;
	var nextStepVlaue = _self.rhNext.getItem(nextStepName).getValue(); 
	if (nextStepVlaue.indexOf("VP-") == 0){
		var nextSelectBean =  StrToJson(_self.getAuthBean().DEVIDE_TWO_NODE);
		if (nextSelectBean && nextSelectBean.NODES){
			var arr = nextSelectBean.NODES;
			for (var i=0; i<arr.length; i++){
				arr[i].NODE_NAME = arr[i].USER_LABEL;
			}
			return arr;
		}else{
			return new Array();
		}
	}else if (nextStepVlaue.indexOf("ACT") == 0){
		var userSelectName0 = WfNextStep.userSelect();
		return new Array();
	}else {
		return _self.getNextStepBean();
	}
}

/**
 * 初始化用户的选择， 有两种特殊情况需要做处理
 * 1，如果之前缓存的是ACT， 则在第二次打开的时候，不能显示选人的框
 * 2，如果之前缓存的是 VP-N1-N2 ， 则第二次打开的时候， 需要显示两个送人的框，并且需要显示第二个框以前保存的值
 */
rh.vi.wfCardView.prototype.initUserSelect = function() {
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
			_self.rhNext.getItem(userSelectName0).hide();
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
				var userSelectName = WfNextStep.userSelect();
				_self.rhNext.getItem(userSelectName).setLabel(item.USER_LABEL);
			}
		});
	}
}


/**
 * 弹出处理完毕 点击获取用户的按钮
 */
rh.vi.wfCardView.prototype.userSelectClick = function() {
	var _self = this;
	
	var nextStepName = WfNextStep.nextStep;
	var userSelectName = WfNextStep.userSelect();

	var item = _self.rhNext.getItem(userSelectName);
	if(item.getLabel() == null ||  item.getLabel() == ""){
		item.setLabel("处理人");
	}

	item.click(function(event){
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
rh.vi.wfCardView.prototype.reBindUserSelectClick = function(nodeCode) {
	var _self = this;
	
	var userSelectName = WfNextStep.userSelect();
	_self.rhNext.getItem(userSelectName).click(function(event){
		_self.selectUserToPos = 0;
		_self.fetchBinderData(nodeCode, event, null, 0);
	});
}


/**
 * 构造传入后台的数据
 */
rh.vi.wfCardView.prototype.confirmToNext = function() {
	var _self = this;
	
	var deferred = $.Deferred();

	//校验意见
	var idArray = _self.rhNext.getItem(WfNextStep.userSelect()).getValue();
	var nameArray = _self.rhNext.getItem(WfNextStep.userSelect()).getName();
	if (!_self.mind.mindCheck(_self.mind.getValue()) || !_self.callback(idArray, nameArray)) {
		deferred.reject();
		return deferred.promise();
	}
	
//	if (confirm("确定送下一环节")) {
		var paramBean = {};
		paramBean.DATA_ID = _self._parHandler._pkCode;
		paramBean.SERV_ID = _self._parHandler.servId;
		paramBean.NI_ID = _self.getNodeInstBean().NI_ID;
		paramBean.INST_IF_RUNNING = _self.wfState; 
		_self.appendAgentUserFlagTo(paramBean);
		
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
			_self.appendAgentUserFlagTo(wfData);
			
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
		
		//过滤流程数据
		_self.filterWfData(paramBean.WF_DATA);

		if (_self.getAuthBean().showYueZhi == 1) { //有显示阅知
			var sendData = {};
			sendData.fromScheme = "yes";
			var yuezhiSelectedValue = _self.rhNext.getItem("yuezhiSelect").getValue();
			sendData.SEND_ITEM = [{"sendId":yuezhiSelectedValue}];
			sendData.includeSubOdept = true;
			
			paramBean.SEND_DATA = sendData;
		}
		
		paramBean.MIND_DATA = _self.mind.getValue(); //意见数据装配

		_self.doProcess(paramBean, deferred);
		
		_self.afterConfirmToNext();
		var isJump = System.getVar('@C_YESNO_JUMP@');
		if(isJump && isJump == 1){
		_self.todoListJmp();
		}
		
//	} else {
//		deferred.reject();
//	}
    return deferred.promise();
}

/**
 * 接口，供卡片js个性化调用，修改数据
 * @param {} wfData 流程流转的数据，数组
 */
rh.vi.wfCardView.prototype.filterWfData = function (wfData){
}

/**
 * 点击确定之后的操作
 */
rh.vi.wfCardView.prototype.afterConfirmToNext = function (){
	
}

/**
 * 处理流程
 */
rh.vi.wfCardView.prototype.doProcess = function(paramBean, deferred) {
	var _self = this;
	var rtnData = FireFly.doAct("SY_WFE_INTEGRATION", "process", {"data":jQuery.toJSON(paramBean)}, false);	
	
	if(rtnData["_MSG_"] && rtnData["_MSG_"].indexOf("ERROR,") >= 0) {
		deferred.reject();
		Tip.showError(rtnData["_MSG_"].substring(6), true);
	} else {
//		_self._refreshView("处理完毕");
		
		_self.todoRefresh();
		_self._parHandler.setParentRefresh();
		var isJump = System.getVar('@C_YESNO_JUMP@');
		if(isJump == null || isJump !=1){
		_self._parHandler.backClick();
		}
		deferred.resolve();
	}
};

/**
 * ICBC待办特殊处理
 */
rh.vi.wfCardView.prototype.todoRefresh = function() {
	try {
		if (window.opener) {
			window.opener.ICBC.todoRefresh();
		}
	} catch (e) {}
};

/**
 * 重新装载页面
 */
rh.vi.wfCardView.prototype._refreshView = function(msg) {
	var _self = this;
	var viewer = _self._parHandler;
	var url = viewer.servId + ".byid.do?data={_PK_:" + viewer._pkCode ;
	if(_self.isAgent()){
		url += ",_AGENT_USER_:'" + _self.getAgentUserCode() + "'";
	}
	url += "}";
	_self.todoRefresh();
	viewer.setParentRefresh();
	viewer.resetReplaceUrl(url);
	viewer.refresh();
	if (msg) {
		viewer.cardBarTip(msg);
	}
}


/**
 * 根据处理选择的值，去动态的设置弹出框，显示确定的字
 */
rh.vi.wfCardView.prototype.setDialogConfirmName = function () {
	var _self = this;

	if (_self.rhNext.getItem(_self.getWfSelectFieldName())) {
		var currWfSelect = _self.rhNext.getItem(_self.getWfSelectFieldName()).getValue();
		jQuery.each(_self.getAuthBean().S_WF_SELECT, function(key, itemObj){
			if (currWfSelect == itemObj.ITEM_CODE) {
				var btnName = itemObj.VALUE_BTN;
				if (btnName.length == 0) {
//					btnName = "确定";
					btnName = Language.transStatic('rh_ui_gridCard_string17');
				}
				jQuery("#cmSaveAndSend_dialog").parent().find(".ui-dialog-buttonset").children().first().next().html(btnName);		
			}
		});		
	}
}


/**
 * 注册下一步的改变的事件
 */
rh.vi.wfCardView.prototype.regStepChg = function() {
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
	// var userSelectName = WfNextStep.userSelect();
	// var deptRoleSelectName = WfNextStep.deptRole();

	_self.rhNext.getItem(nextStepName).valueChanged = function(newValue, event) {
		_self.nextStepChange(newValue, event);
		_self.mind.changeGeneralMind();
	}

	// 如果下一个环节选择框只有一个节点，则自动选中。
	var nextStepOptions = _self.rhNext.getItem(nextStepName).getOptions();
	if(jQuery.isArray(nextStepOptions) && nextStepOptions.length == 1) {
		var defaultVal = nextStepOptions[0].value;
		if(defaultVal) {
            try{
                _self.rhNext.getItem(nextStepName).setValue(defaultVal);
			    _self.rhNext.getItem(nextStepName).disable();
			    _self.rhNext.getItem(nextStepName).change();
            }catch(e){}
		}
	} 
	
}


/**
 * 根据下一步的节点，弹出的用户选择(送交按钮变化的时候/或者点击重新获取的时候)，不影响pos之前的，pos之后的都置空
 * @param newValue 改变后的新值
 * @param event 事件
 */
rh.vi.wfCardView.prototype.nextStepChange = function(newValue, event) {
	var _self = this;
	_self._parHandler.byIdData._nextStepAct = newValue;

	//重新构造 pos之后的送交框，将 pos之后的人员选择框置空
	var nextStepName = WfNextStep.nextStep;
	var userSelectName0 = WfNextStep.userSelect();
	var deptRoleSelectName = WfNextStep.deptRole();

	//清理上一次选中的数据，恢复页面样式
	var usersItem = _self.rhNext.getItem(userSelectName0);
	usersItem.setOptions();	
	usersItem.initValue("", "");
	usersItem.enable();
	//如果之前选择了一个ACT-button,把userSelect remove掉了， 这里需要添加上
	usersItem.show();
	
	//将之前的创建的多个人员的输入删除
	if (_self.userSelectCount > 0) {
		for (var j=1; j<=_self.userSelectCount; j++) {
			var userSelectName = WfNextStep.userSelect(j);
			var deptRoleSelectName = WfNextStep.deptRole(j);
			_self.rhNext.removeItem(userSelectName);
			_self.rhNext.removeItem(deptRoleSelectName);
			// _self.rhNext.getItem(userSelectName).$dom.remove();
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
				if (index > 0) {
					_self.bldNewUserSelect(item, index);
				} else { //重新绑定获取人员的点击事件
					_self.reBindUserSelectClick(item.NODE_CODE);
					var userSelectName = WfNextStep.userSelect();
					_self.rhNext.getItem(userSelectName).setLabel(item.USER_LABEL);
					// 默认打开第一个框的数据
					_self.selectUserToPos = 0; 
					_self.fetchBinderData(firstNode, event, null, 0);
				}
			});
		} else {
			_self.userSelectClick(); //重新绑定获取人员的点击事件
			_self.selectUserToPos = 0; //选择的人放到哪个 userSelect的框里去
			_self.fetchBinderData(newValue, event, null, 0);
		}
		_self.rhNext.getItem(userSelectName0).setNotNull(true);
	} else if (newValue.indexOf("ACT") == 0) { //ACT-button, 则需要将selectUser去掉
		if (_self.rhNext.getItem(userSelectName0)) { //存在
			//先隐藏， 如果这个ACT有返回的数据才去显示选择的用户的框
			_self.rhNext.getItem(userSelectName0).initValue("", "");
			_self.rhNext.getItem(WfNextStep.deptRole()).setValue("");
			
			_self.selectUserToPos = 0; 
			
			//提交请求，获取人员的数据
			_self.fetchBinderData(newValue, event, null, 0);
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
rh.vi.wfCardView.prototype.hideUserSelect = function () {
	var _self = this;

	var userSelectName0 = WfNextStep.userSelect();
	
	_self.rhNext.getItem(userSelectName0).setNotNull(false);
	_self.rhNext.getItem(userSelectName0).$dom.hide();
	_self.rhNext.getItem(userSelectName0).hide();
	
}



/**
 * 构建一个新的用户选择的框
 * @param vpNodeItem 定义的节点信息
 * @param index 第几个选人的框
 */
rh.vi.wfCardView.prototype.bldNewUserSelect = function (vpNodeItem, index) {
	var _self = this;
	var userSelectName = WfNextStep.userSelect(index);
	var deptRoleSelectName = WfNextStep.deptRole(index);
	
	var labelStr = vpNodeItem.USER_LABEL;
	var nodeCode = vpNodeItem.NODE_CODE;
	
	var userSelectObj = {'type':'combobox', 'id':userSelectName, 'label':labelStr, 'value':'', 'notnull':vpNodeItem.NOT_NULL};
	var deptRoleObj = {'type':'hidden', 'id':deptRoleSelectName, 'label':'', 'value':''};	
	
	userSelectObj.hide = _self._hideUserSelectTree();

	_self.rhNext.addItem(userSelectObj, WfNextStep.userSelect(index - 1), WfNextStep.deptRole(index - 1));
	_self.rhNext.addItem(deptRoleObj, WfNextStep.userSelect(index - 1), WfNextStep.deptRole(index - 1));
	_self.userSelectCount += 1;
	
	//动态添加一项之后， 注册事件
	// _self.rhNext.getItem(userSelectName).click(function(event){
		_self.selectUserToPos = index;
		
		//需要除去的人， 该用户选择上面出现的 其他的 用户选择
		var excludeUserId = "";
		for (var j=0;j<index;j++) {
			var userSelectPre = WfNextStep.userSelect(j);
			excludeUserId += _self.rhNext.getItem(userSelectPre).getValue() + ",";
		}
		
		_self.fetchBinderData(nodeCode, event, excludeUserId, index);
	// });	
}


/**
 * 送交下一步变化引起的其他变化 ， 卡片扩展js去处理
 * @param nextStep 下一步的值
 */
rh.vi.wfCardView.prototype.stepChgCauseExt = null;

/**
 * 获取绑定的数据，包括下一步变化，及手动点击的事件
 * @param pos 第几组送交
 * @param nodeCode 送交的节点
 * @param event 事件
 */
rh.vi.wfCardView.prototype.fetchBinderData = function (nodeCode, event, excludeUserId
	, selectUserPos) {
	var _self = this;
	//选择pos的送交之后，进行选人的逻辑
	var userSelectId = WfNextStep.userSelect(selectUserPos);
	if (_self._getWfNextBtn(nodeCode) && _self._getWfNextBtn(nodeCode).dataObj.NODE_USER) { //数据上已经有了用户信息
		var backUserName = _self._getWfNextBtn(nodeCode).dataObj.NODE_USER_NAME;
		var backUserCode = _self._getWfNextBtn(nodeCode).dataObj.NODE_USER;
		_self.rhNext.getItem(userSelectId).initValue(backUserName, backUserCode);
	} else { //去服务端获取数据  先看看之前有选择没有
		var userCodes = _self.rhNext.getItem(userSelectId).getValue();
		var userNames = _self.rhNext.getItem(userSelectId).getName();
		_self._openSelectOrg(nodeCode, "", event, userCodes, userNames, 
				excludeUserId, selectUserPos);
	}
}


/**
 * 对下一个节点设置能送交的点
 */
rh.vi.wfCardView.prototype.setOptionsForNextStep = function () {
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
 * 选择人员之后, 将选择的人员放到下拉框
 */
rh.vi.wfCardView.prototype.confirmUserSelectSend = function(sendObj, nameArray) {
	var _self = this;
	var userSelectItemName = WfNextStep.userSelect(_self.selectUserToPos) ;
	var deptRoleSelectItemName = WfNextStep.deptRole(_self.selectUserToPos);
	
	//判断，如果是 通过ACT选人 返回的结果， 则将人员的框 显示出来
	var nextStepName = WfNextStep.nextStep;
	var nextStep = _self.rhNext.getItem(nextStepName).getValue();
	if (nextStep.indexOf("ACT") == 0) { 
		var userSelectName0 = WfNextStep.userSelect();
		_self.rhNext.getItem(userSelectName0).$dom.show(); //让处理人显示
		_self.rhNext.getItem(userSelectName0).show(); //
		_self.rhNext.getItem(userSelectName0).setNotNull(true); 
		
		_self.reBindUserSelectClick(nextStep);  //绑定事件  TODO
	}
	
	if (sendObj.TO_TYPE == 3) { //送人
		//将人员的下拉框设置上值
		var _item = _self.rhNext.getItem(userSelectItemName);
		_item.disable();
		_item.initValue(nameArray.join(","), sendObj.TO_USERS);
		_self.rhNext.getItem(deptRoleSelectItemName).setValue(""); //设置角色为空,避免之前选过送角色了
		if(nextStep.indexOf("VP-") == 0 && _self.selectUserToPos == 0) {
			setTimeout(function() {
				var ids = nextStep.split("-");
				if(ids.length == 3) {
					_self.selectUserToPos = 1; 
					_self.fetchBinderData(ids[2], null);
				}
			}, 10);
		}
	} else if (sendObj.TO_TYPE == 1) { //送角色
		var _item = _self.rhNext.getItem(userSelectItemName);
		_item.disable();
		_item.initValue(nameArray.join(","), sendObj.TO_DEPT);
		_self.rhNext.getItem(deptRoleSelectItemName).setValue(sendObj.TO_ROLE);
	}
};


/**
 * 在构造处理完成 弹出框 之前 准备的 页面数据, 字段的设置
 */
rh.vi.wfCardView.prototype.beforeBldNextConfirmWindow = function() {
	return [];
}

/**
 * 字段的设置, 在处理完毕的弹出框上的显示
 */
rh.vi.wfCardView.prototype.bldFieldComplete = function() {
	var _self = this;
	
	var fieldObjs = new Array();

	if (_self._parHandler.byIdData.FIELD_COMPLETE) {
		var fieldSort = 500;
		jQuery.each(_self._parHandler.byIdData.FIELD_COMPLETE, function(key, itemObj){
			var key = itemObj.ITEM_CODE; //字段名
			
			var itemDef = _self._parHandler.form.items[key]; //字段定义
			
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
			
			var itemInputObj = {'type':itemType, 'id': key, 'label':itemObj.ITEM_NAME, 'value':itemValue, 'sort':fieldSort, 'notnull': notnull, 'formData':true};
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
 * 获取表单上定义的数据
 */
rh.vi.wfCardView.prototype.confirmGetFormData = function () {
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
 * 在构造处理完成 弹出框 之后要做的相关事情 , 卡片js可覆盖
 */
rh.vi.wfCardView.prototype.afterBldNextConfirmWindow = function() {
};


/**
 * 公开获取rhnext对象的方法, 在卡片扩展js中可以去设置 处理完毕页面上的元素
 */
rh.vi.wfCardView.prototype.getRhNext = function() {
	var _self = this;
	
	return _self.rhNext;
};

/**
 * 构造处理选择的单选
 */
rh.vi.wfCardView.prototype.bldWfSelect = function(opts) {
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
		
		var wfSelectObj = {'type':type, 'id':_self.getWfSelectFieldName(), 'label':selectBaseObj.LABEL_TEXT, 'value':defaultVlaue, 'options': wfSelectArray, 'sort':180, 'notnull':true, 'formData':true};
		opts.push(wfSelectObj);
	}
}


/**
 * 构造一个送交的元素
 */
rh.vi.wfCardView.prototype.bldNextStep = function(opts) {
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
	var _nextStepBeanArray = _self.getNextStepBean();
	if(jQuery.isArray(_nextStepBeanArray) && _nextStepBeanArray.length != 1) {
		nextStepArray.push({'name':'','value':''});  //默认给一个空的值
	}
	
	jQuery.each(_nextStepBeanArray, function(i, actItem) {
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
//	var nextStepObj = {'type':'select', 'id':nextStepName, 'label':'下一环节', 'value':'', 'options': nextStepArray, 'sort':nextStepSort, 'store_option':store_option, 'notnull':notnull};
	var nextStepObj = {'type':'select', 'id':nextStepName, 'label':Language.transStatic('rhWfCardView_string87'), 'value':'', 'options': nextStepArray, 'sort':nextStepSort, 'store_option':store_option, 'notnull':notnull};
	nextStepSort += 20;
//	var userSelectObj = {'type':'combobox', 'id':userSelectName, 'label':'处理人', 'value':'', 'sort':nextStepSort, 'notnull':notnull};
	var userSelectObj = {'type':'combobox', 'id':userSelectName, 'label':Language.transStatic('rhWfCardView_string88'), 'value':'', 'sort':nextStepSort, 'notnull':notnull};
	userSelectObj.hide = _self._hideUserSelectTree();

	nextStepSort += 20;
	var deptRoleObj = {'type':'hidden', 'id':deptRoleSelectName, 'label':'', 'value':'', 'sort':nextStepSort}
	nextStepSort += 20;
	
	opts.push(nextStepObj);
	opts.push(userSelectObj);
	opts.push(deptRoleObj);
};

rh.vi.wfCardView.prototype._hideUserSelectTree = function() {
//	if(this._parHandler.servId.startsWith("PE_")) {
//		return true;
//	}
//	
	if(this._parHandler.servId.indexOf("PE_")==0) {
		return true;
	}
	return false;
}

/**
 * 从所有可送交的节点中过滤出满足条件的子集
 */
rh.vi.wfCardView.prototype.getNextStepByWfSelect = function() {
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
rh.vi.wfCardView.prototype.getWfSelectFieldName = function() {
	var _self = this;
	
	var wfSelectBaseField = "wfSelectId";
	if (_self.getAuthBean().S_WF_SELECT_BASE) {
		wfSelectBaseField = _self.getAuthBean().S_WF_SELECT_BASE.FIELD_CODE;
	}
	
	return wfSelectBaseField
}

// 终止请求
rh.vi.wfCardView.prototype.cmTerminate = function() {
	var _self = this;
//	if (!confirm("是否确定废止?")) {
	if (!confirm(Language.transStatic("rhWfCardView_string89"))) {	
		return false;
	}
	var result = _self._parHandler.doAct("terminate");
	if(result) {
		_self._parHandler.shieldHide();
		// 关闭当前页面
		_self._parHandler.backClick();
	}
};

/**
* 如果用户属于多个部门，则显示选择拟稿部门的界面。
**/
rh.vi.wfCardView.prototype.showMultiDeptSelect = function() {
	if(/*System.getUser("_MULTI_DEPT", 2) != 1 ||*/ !this._parHandler.getByIdData("_ADD_")) {
		// 用户没有多个部门 或者 审批单处于添加状态
		return;
	}

	if(this._parHandler.getItem("S_WF_USER_STATE") == undefined) {
		// 没有流程状态字段
		return;
	}

	var _self = this;
	// 从服务器端获取用户属于那些部门
	/*var result = FireFly.doAct("SY_ORG_USER_INFO_SELF", "getMultiDept", {}, false);*/
	var result = FireFly.doAct("SY_ORG_USER_INFO_SELF", "getMultiDeptContainsRoleByServId", {"PROC_SERV_ID":_self._parHandler.servId}, false);
	var data = result["_DATA_"];
	if(!data || data.length == 0 || data.length == 1) {
		return;
	}
	
	//获取多岗位服务配置，如果该服务与挂职角色或部门绑定，表示userBean已经更新为当前挂职部门
	var postData = FireFly.doAct("SY_COMM_POST_ROLE", "getPostRoleByServId", {"SERV_ID":_self._parHandler.servId}, false);
	var postRole = postData["_DATA_"];
	if(postRole) {
		return;
	}

	var nextDialog;
	var winOptions = {
		autoOpen: false,
		height: 200,
		width: 500,
		modal: true,
		resizable: false,
		position: [(jQuery("body").width() - 500)/2, 200],
		buttons:{
			"确定": function(){
//			Language.transStatic("rh_ui_gridCard_string17"): function(){	
				//
				var radios = nextDialog.find("input[name=radio_deptSelect]:checked");
				if(radios.length == 0) {
//					alert("请选择拟稿单位");
					alert(Language.transStatic("rhWfCardView_string90"));
				}
				_self._replaceUserSelectedDept(radios);

				nextDialog.closest(".ui-dialog").find(".ui-icon-closethick").click();
			}
		}
	};

//	nextDialog = this.getDialog("MultiDeptSelect_dialog", "请选择拟稿单位", winOptions);
	nextDialog = this.getDialog("MultiDeptSelect_dialog", Language.transStatic("rhWfCardView_string90"), winOptions);

	var _radioPanel = new Array();
	_radioPanel.push("<div class='ml50 mt20 mb30'><table class='wp80'>");

	var count = data.length;
	for(var i=0;i<count;i++) {
		var deptData = data[i];
		_radioPanel.push("<tr><td class='w20 h25'><input type='radio'");
		if(i==0) {
			_radioPanel.push(" checked");
		}
		_radioPanel.push(" name='radio_deptSelect'" );
		_radioPanel.push(" DNAME='" + deptData.DEPT_NAME + "'");
		_radioPanel.push(" TDEPT='" + deptData.TDEPT_CODE + "'");
		_radioPanel.push(" TDNAME='" + deptData.TDEPT_NAME + "'");
		_radioPanel.push(" ODEPT='" + deptData.ODEPT_CODE + "'");
		_radioPanel.push(" ODNAME='" + deptData.ODEPT_NAME + "'");

		_radioPanel.push(" value='" + deptData.DEPT_CODE + "'></td>");
		_radioPanel.push("<td>");
		_radioPanel.push(deptData.DEPT_NAME);
		_radioPanel.push("</td></tr>")
	}
	_radioPanel.push("</table></div>");

	var selectContainer = jQuery(_radioPanel.join(""));

	nextDialog.append(selectContainer);
};

/**
* 用户选择部门之后，根据新部门的ID，替换默认部门。
**/
rh.vi.wfCardView.prototype._replaceUserSelectedDept = function (radioItem) {
	var _self = this;
	var deptField = _self._getJingbanDeptField();
	if(deptField && jQuery.isArray(deptField)) {
		// 用户选中的部门信息，写入到指定的经办人信息中
		var sDeptItem = _self._parHandler.getItem();
		_self._fillItemValue(deptField[0], radioItem.attr("value"));

		if(deptField.length > 0) {
			_self._fillItemValue(deptField[1], radioItem.attr("DNAME"));
		}
	}

	//用户选中的部门信息S_DEPT S_DNAME; S_TDEPT S_TDNAME;S_ODEPT S_ODNAME
	_self._fillItemValue("S_DEPT", radioItem.attr("value"));
	_self._fillItemValue("S_DNAME", radioItem.attr("DNAME"));
	_self._fillItemValue("S_TDEPT", radioItem.attr("TDEPT"));
	_self._fillItemValue("S_TDNAME", radioItem.attr("TDNAME"));
	_self._fillItemValue("S_ODEPT", radioItem.attr("ODEPT"));
	_self._fillItemValue("S_ODNAME", radioItem.attr("ODNAME"));
	
	//用户选择完起草机构后的处理逻辑
	_self.afterMultiDeptSelected({
		"S_DEPT":radioItem.attr("value"),
		"S_DNAME":radioItem.attr("DNAME"),
		"S_TDEPT":radioItem.attr("TDEPT"),
		"S_TDNAME":radioItem.attr("TDNAME"),
		"S_ODEPT":radioItem.attr("ODEPT"),
		"S_ODNAME":radioItem.attr("ODNAME")
	});
};

rh.vi.wfCardView.prototype.afterMultiDeptSelected = function(obj) {
	
};

rh.vi.wfCardView.prototype._fillItemValue = function(itemCode, itemValue) {
	var _self = this;
	var item = _self._parHandler.getItem(itemCode);
	if(item) {
		item.setValue(itemValue);
	}
};

rh.vi.wfCardView.prototype._getJingbanDeptField = function() {
	if(this._parHandler._data.SERV_EXPTENDS) {
		var conf = this._parHandler._data.SERV_EXPTENDS;
		var confBean = jQuery.parseJSON(conf);
		if(confBean.JINGBAN) {
			var depts = confBean.JINGBAN.DEPT;
			if(depts) {
				return depts.split(",");
			}
		}
	}

	return undefined;
};

/**
 * 流程下一步的常量, 用于构造处理完毕时，送交的下一步和选人的页面元素的ID值 
 * 最后的ID类似 nextStepSelect0/nextStepSelect1/nextStepSelect2
 */
var WfNextStep = {
	nextStep : "nextStepSelect",
	userSelect : function(pos) {
		return "userSelect" + (pos || "0");
	},
	deptRole : function(pos) {
		return "deptRoleSelect" + (pos || "0");
	},
	FREE_NODE : "NODEX",
	fieldPrefix : ""
};

/**
 * 添加可选的下一步的数据信息，作退回多节点的扩展使用
 * @param {} newNextStepBean
 */
rh.vi.wfCardView.prototype.addNextStepBean = function (newNextStepBean){
	var _self = this;

	if (_self._parHandler.byIdData.nextSteps == undefined
			|| _self._parHandler.byIdData.nextSteps == "undefined" || newNextStepBean == undefined) {
		return "";
	}
	
	var wfSendBtns = _self._parHandler.byIdData.nextSteps;
	for(var i=0; i<newNextStepBean.length; i++){
        if(!(_self.isExistNextBean(wfSendBtns,newNextStepBean[i]))) {
        	wfSendBtns.push(newNextStepBean[i]);
        }
	}
}

rh.vi.wfCardView.prototype.isExistNextBean = function (srcBeans,newBean){
	for (var j=0; j<srcBeans.length; j++){
		if(srcBeans[j].NODE_CODE == newBean.NODE_CODE){
			return true;
		}
	}
	
	return false;
}

/**
 * 获取前一个节点的相关信息，返回数组
 * @param {} newNextStepBean
 */
rh.vi.wfCardView.prototype.getPreStepDoUser = function (){
	var _self = this;
	var nodeBean = _self.getNodeInstBean();
	var result = FireFly.doAct("SY_WFE_PROC","getPreStepNodeDoUser",{PI_ID:nodeBean.PI_ID,NODE_CODE:nodeBean.NODE_CODE},false,false);
	var list = result._DATA_;
	for (var i=0; i<list.length; i++){
//		list[i].NODE_NAME = "返回"+list[i].NODE_USER_NAME;
		list[i].NODE_NAME = Language.transStatic("rh_ui_gridCard_string4")+list[i].NODE_USER_NAME;
	}
	return result._DATA_;
}



