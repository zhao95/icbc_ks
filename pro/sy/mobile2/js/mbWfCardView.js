/**
 * 工作流引擎
 */
GLOBAL.namespace('mb.vi');
/**
 * 构造方法
 */
mb.vi.wfCardView = function(options) {
	var defaults = {
			'id': options.sId + '_viWfCardView',
			'sId': '', // 服务ID
			'pId': options.sId,
			'parHandler': null, // 主卡片的句柄
			'pkCode': ''
	};
	this.opts = $.extend(defaults, options);
	
	this.servId = this.opts.sId; // card 服务ID
	this.pkCode = this.opts.pkCode; // card 数据主键
	
	this._parHandler = this.opts.parHandler;
	this._nextStepNodeCode = '';
	
	// 从页面取得流程实例的信息
	if (this._parHandler && this._parHandler._formData 
			&& this._parHandler._formData) {
		this.procInstId = this._parHandler._formData.S_WF_INST;
		this.wfState = this._parHandler._formData.S_WF_STATE;
	}
	this.reqdata = {};
	this.reqdata['PI_ID'] = this.procInstId; // 流程实例ID
	this.reqdata['INST_IF_RUNNING'] = this.wfState; // 流程是否在运行
	if (!(this.getNodeInstBean() == undefined)) {
		this.reqdata['NI_ID'] = this.getNodeInstBean().NI_ID;
	}
	
	//增加委托用户USER_CODE	
	this.appendAgentUserFlagTo(this.reqdata);
	
	this.wfBtns = {};
	this.wfNextBtns = {};
	this.wfNextBtnNames = {};
	this._confirmSendFlag = true; // 送交标志 true:可以送交， false:不可以送交
};
/**
 * 渲染
 */
mb.vi.wfCardView.prototype.render = function() {
	// 绑定流程按钮
	this._bldBtnBar();
	// 处理文件控制
	this._doFiledControl();
	// 初始化意见
	this._initMind();
	// 确定是否意见为必填
	this._mindMust();
};

/**
 * 取得“操作”按钮列表jQuery对象
 */
mb.vi.wfCardView.prototype._getOperMenu = function() {
	var _self = this;
	var iconMatch = {
		"save":"saveIcon",
		"finish":"finishIcon"
	};
	var controlgroup = $('<div>')
		.addClass("ui-controlgroup-controls ui-panel-inner")
		.css({"padding-bottom":"0"});
	
	//按钮数组构建列表页面
	_self._buttonData = _self.getButtonBean();
	$.each(_self._buttonData, function(i, actItem) {
		var id = GLOBAL.getUnId(actItem.ACT_CODE, actItem.SERV_ID);
		var name = actItem.ACT_NAME;
		var btn = $('<a>')
			.addClass("ui-btn ui-corner-all ui-btn-icon-right ui-icon-" + actItem.ACT_CODE)
			.attr({
				"href":"#",
				"id":id,
				"style":"border-bottom:0;"
			}).text(name).appendTo(controlgroup);
		if (iconMatch[actItem.ACT_CODE]) { //特殊按钮样式对照
			btn.removeClass("ui-icon-"+actItem.ACT_CODE).addClass("ui-icon-"+iconMatch[actItem.ACT_CODE]);
		}
		if (i == _self._buttonData.length - 1) {
			btn.attr("style","");
		}

		// 绑定事件
		btn.on('vclick', function(event) {
			_self.clickOperBtn(actItem);
		});
	});
	
	return controlgroup;
};

mb.vi.wfCardView.prototype.refresh = function() {
	if (this._parHandler) {
		this._parHandler.refresh();
	}
};

/**
 * 取得“下一步”按钮列表jQuery对象
 */
mb.vi.wfCardView.prototype._getNextStepMenu = function() {
	var _self = this;
	var _nextData = _self.getNextStepBean();
	
	var target = $("<div>");
	$.each(_nextData, function(i, actItem) {
		if (StringUtils.startWith(actItem.NODE_CODE, 'ACTP-')) { //办结
			actItem.NODE_CODE = actItem.NODE_CODE.substring(5);
		}
		if (actItem.NODE_CODE && StringUtils.startWith(actItem.NODE_CODE, 'R')) { //返回类
			actItem.NODE_CODE = actItem.NODE_CODE.substring(1);
		}
		var id = GLOBAL.getUnId(actItem.NODE_CODE, "cmSaveAndSend");
		var name = actItem.NODE_NAME;
		var btn = $("<a href='#'>")
			.addClass("ui-btn ui-icon-arrow-r ui-btn-icon-right ui-corner-all")
			.attr({
				"id":id,
				"data-rel":"dialog",
				"style":"border-bottom:0;"
			}).text(name).appendTo(target);
		
		//绑定事件
		btn.bind('vclick', function() {
			if (isNaN(actItem.NODE_CODE)) { //非数字，当成操作按钮处理，寻找本地方法进行处理
				_self.clickOperBtn({
					"ACT_CODE":actItem.NODE_CODE,
					"ACT_NAME":actItem.NODE_NAME
				});
			} else {
				_self.clickNextBtn(actItem);
			}
		});
	});
	
	return target;
};

/**
 * 点击“下一步”类按钮
 */
mb.vi.wfCardView.prototype.clickNextBtn = function(actItem) {
	var _self = this;
	if (actItem.NODE_USER) { // 如果是返回XX的按钮，取得返回人的CODE
		if (_self.mindMust.pass == 'NO') { // 先执行送交前的代码，这里只判断了'必填意见'
//			alert(_self.mindMust.reason);
			rh.displayToast({text: _self.mindMust.reason});
			return false;
		}
		
		_self.reqdata['NODE_CODE'] = actItem.NODE_CODE;
		_self.reqdata['TO_USERS'] = actItem.NODE_USER;
		_self.reqdata['TO_TYPE'] = '3';
		// 把流程信息送交给oa
		FireFly.doAct('SY_WFE_PROC', 'toNext', _self.reqdata).done(function(result) {
			// 送交给oa得到oa返回的成功信息
			// TODO 如果这里的提示有问题，再处理
			if (result._MSG_.indexOf("OK,") >= 0) {
//				alert('已经成功' + actItem.NODE_NAME);
				rh.displayToast({text: '已经成功' + actItem.NODE_NAME});
				_self.back();
			} else {
//				alert(result._MSG_);
				rh.displayToast({text: result._MSG_});
			}
			
		});
	} else {
		_self._nextStepNodeCode = actItem.NODE_CODE;
		if (_self.getNodeInstBean()) {
			_self.reqdata['NI_ID'] = _self.getNodeInstBean().NI_ID;
		}
		_self.reqdata['NODE_CODE'] = actItem.NODE_CODE;
		FireFly.doAct('SY_WFE_PROC', 'getNextStepUsersForSelect', _self.reqdata).done(function(result) {
			if (result && result.TO_USERS) { //说明是一个人，且已直接送成功
//				alert("已成功送交：" + result.TO_USERS);
				rh.displayToast({text: "已成功送交：" + result.TO_USERS});
				_self.back();
			} else {
				_self._openSelectOrg(actItem.NODE_CODE, actItem.NODE_NAME, result);
			}
			
		});
	}
};

/**
 * 点击“操作”类按钮
 */
mb.vi.wfCardView.prototype.clickOperBtn = function(actItem) {
	var _self = this;
	if (actItem.ACT_MEMO) {
		eval(actItem.ACT_MEMO);
	} else {
		if (_self[actItem.ACT_CODE] 
			&& typeof(_self[actItem.ACT_CODE]) == "function") { //本地有方法进行处理
			_self[actItem.ACT_CODE](event, actItem);
		} else if (_self._parHandler 
				&& _self._parHandler[actItem.ACT_CODE]
				&& typeof(_self._parHandler[actItem.ACT_CODE]) == "function") { //卡片里有方法进行处理，如save
			_self._parHandler[actItem.ACT_CODE](event, actItem);
		} else {
//			alert("在流程JS中和卡片JS中都没有处理按钮["+actItem.ACT_CODE+"]的方法");
			rh.displayToast({text: "在流程JS中和卡片JS中都没有处理按钮["+actItem.ACT_CODE+"]的方法"});
		}
	}
}

/**
 * 提交原生端的菜单数据
 */
mb.vi.wfCardView.prototype.getFormatMenus = function() {
	var _self = this;
	
	var _buttonData = this.getButtonBean() || [];
	var _nextData = this.getNextStepBean() || [];
	
	var menuArr = [];
	$.each(_nextData, function(i, actItem) {
		if (StringUtils.startWith(actItem.NODE_CODE, 'ACTP-')) { //办结
			actItem.NODE_CODE = actItem.NODE_CODE.substring(5);
		}
		if (actItem.NODE_CODE && StringUtils.startWith(actItem.NODE_CODE, 'R')) { //返回类
			actItem.NODE_CODE = actItem.NODE_CODE.substring(1);
		}
		var eventName = UIConst.EVENT_PRE_CARDVIEW + actItem.NODE_CODE;
		menuArr.push({
			"eventName":eventName,
			"title":actItem.NODE_NAME,
		});
		
		//事件监听
		$("#cardview_content").unbind(UIConst.EVENT_MENU_PRE + eventName).bind(UIConst.EVENT_MENU_PRE + eventName, function(event) {
			if (isNaN(actItem.NODE_CODE)) { //非数字，当成操作按钮处理，寻找本地方法进行处理
				_self.clickOperBtn({
					"ACT_CODE":actItem.NODE_CODE,
					"ACT_NAME":actItem.NODE_NAME
				});
			} else {
				_self.clickNextBtn(actItem);
			}
		});
	});
	$.each(_buttonData, function(i, actItem) {
		var eventName = UIConst.EVENT_PRE_CARDVIEW + actItem.ACT_CODE;
		menuArr.push({
			"eventName":eventName,
			"title":actItem.ACT_NAME,
		});
		
		//事件监听
		$(document).unbind(UIConst.EVENT_MENU_PRE + eventName).bind(UIConst.EVENT_MENU_PRE + eventName, function(event) {
			_self.clickOperBtn(actItem);
		});
	});
	
	var method = UIConst.MENU_RENDER_METHOD || "setRightTopMenu";
	
	rh[method]({
		"menus" : menuArr,
		"naviTitle":_self._parHandler.servName
	});
	
	return menuArr;
};

/**
 * 构建卡片按钮条，表单按钮，节点定义转换之后的按钮
 */
mb.vi.wfCardView.prototype._bldBtnBar = function() {
	var _self = this;
	
	_self.getFormatMenus();
	
	var currentPage = $.mobile.activePage;
	
	//2.渲染流程按钮dialog
	var controlgroup = _self._getOperMenu();
	var nextStepMenu = _self._getNextStepMenu();
	if (nextStepMenu) {
		controlgroup.prepend(nextStepMenu.children());
	}
	$("a:first", controlgroup).addClass("ui-first-child");
	$("a:last", controlgroup).addClass("ui-last-child");
	
	if (_self._parHandler.nextBtnIfPanel()) {
	} else {
		$('<a href="#" data-rel="back" class="ui-btn ui-corner-all" style="margin-top: 1em;">取消</a>').appendTo(controlgroup);
	}
	
	var nextBtnPanel = _self._parHandler.nextBtnLayoutId();
	if (_self._parHandler.nextBtnIfPanel()) {
		$(".ui-controlgroup-controls", nextBtnPanel).remove();
		$(nextBtnPanel).prepend(controlgroup);
	} else {
		$(".ui-content",nextBtnPanel).empty();
		$(".ui-content",nextBtnPanel).append(controlgroup);
	}
	
};

/**
 * 如果有委托办理，则增加委托办理人标识
 */
mb.vi.wfCardView.prototype.appendAgentUserFlagTo = function(reqdata){
	//增加委托用户USER_CODE
	var _agentUser = this._parHandler._formData._AGENT_USER_;
	if(_agentUser != ""){
		reqdata["_AGENT_USER_"] = _agentUser;
	}	
};

/**
 * 相关权限 比如能否手写意见，当前人是否正在处理当前的流程
 */
mb.vi.wfCardView.prototype.getAuthBean = function() {
	var _self = this;
	return _self._parHandler._formData.authBean;
};
/**
 * 获取按钮
 */
mb.vi.wfCardView.prototype.getButtonBean = function() {
	var _self = this;
	var wfBtnsArray = _self._parHandler._formData.buttonBean || [];
	return wfBtnsArray;
};

/**
 * 获取流程下一步Bean
 */
mb.vi.wfCardView.prototype.getNextStepBean = function() {
	var _self = this;
	if (_self._parHandler._formData.nextSteps == undefined || 
			_self._parHandler._formData.nextSteps == 'undefined') {
		return '';
	}
	return _self._parHandler._formData.nextSteps;
};
/**
 * 节点实例bean
 */
mb.vi.wfCardView.prototype.getNodeInstBean = function() {
	var _self = this;
	return _self._parHandler._formData.nodeInstBean;
};

/**
 * 关闭字典页面
 */
mb.vi.wfCardView.prototype.back = function() {
	$.mobile.back();
};

/**
 * 开启人员选择树
 */
mb.vi.wfCardView.prototype._openSelectOrg = function(nodeCode, nodeName, result) {
	var _self = this;
	
	if (_self.mindMust.pass == 'NO') { // 先执行送交前的代码，这里只判断了'必填意见'
//		alert(_self.mindMust.reason);
		rh.displayToast({text: _self.mindMust.reason});
		return false;
	}
	
	var rtnTreeData = result;
	var treeData = rtnTreeData.treeData;
	
	if (treeData == '[]') {
//		alert('当前节点没有可供选择的人员，请检查工作流配置！');
		rh.displayToast({text: '当前节点没有可供选择的人员，请检查工作流配置！'});
		return false;
	}
	
	this.binderType = rtnTreeData.binderType; // 送交类型
	
	if (_self.binderType == 'ROLE') {
		// 如果是角色，取到角色的CODE
		this._binderRoleCode = rtnTreeData.roleCode;
	}
	
	var extendTreeSetting = {
			cascadecheck: false,
			checkParent: false,
			rhexpand: true,
			showcheck: false,
			rhLeafIcon: "user"
	};
	
	var multiSelect = rtnTreeData.multiSelect; // 是否能多选
	if (multiSelect == 'true') {
		extendTreeSetting['cascadecheck'] = true;
		extendTreeSetting['showcheck'] = true;
	}
	
	treeData = eval('(' + treeData + ')');
	extendTreeSetting['data'] = treeData;
	
	// 加载树形选择页面,并绑定事件
	rh.setRightTopMenu({"naviTitle":nodeName});
	$.mobile.loadPage('../html/treeview.html');
	$.mobile.document.off('pageinit', '#treeview').on('pageinit', '#treeview', function(event, ui) {
		var pageWrp = $(this);
		var headerWrp = pageWrp.find('#treeview_header');
		var contentWrp = pageWrp.find('#treeview_content');
		var footerWrp = pageWrp.find('#treeview_footer');
		var $treeWrp = $('<div></div>').appendTo(contentWrp);
		
		// 取消
		footerWrp.off('vclick').on('vclick', '#cancel', function(evetn) {
			_self.back();
		})
		// 确认
		.on('vclick', '#save', function(event) {
			event.preventDefault();
			event.stopImmediatePropagation();
			var ids = [];
			if (multiSelect == 'true') { // 多选
				var checks = $treeWrp.find('.bbit-tree-node-leaf .checkbox_true_full');
				$.each(checks, function(i, el) {
					var $parent = $(this).parent();
					id = $parent.attr('itemid');
				ids.push(id);
			});
		} else { // radio
			var check = $treeWrp.find('.bbit-tree-node-leaf.bbit-tree-selected');
			if (check.length) {
				var id = check.attr('itemid');
				ids.push(id);
			}
		}
		_self._confirmSend(ids);
// 			返回两级的例子
//			_self.back();
//			setTimeout(_self._parHandler.back, 1000);
		});
		$treeWrp.treeview(extendTreeSetting);
		$.mobile.pageContainer.pagecontainer('change', pageWrp);
	});
};
/**
 * 确定送交
 */
mb.vi.wfCardView.prototype._confirmSend = function(idArray) {
	var _self = this;
	
	if (!_self._confirmSendFlag) { // 如果标志为false, 就是不能送交
		return false;
	} else {
		_self._confirmSendFlag = false;
	}
	
	// 送交的类型：1 送交部门+角色  , 3 送用户
	var toType = '3';
	if (_self.binderType == 'ROLE') {
		toType = '1';
		_self.reqdata['TO_DEPT'] = idArray[0].replace('dept:', ''); // 送交部门
		_self.reqdata['TO_ROLE'] = _self._binderRoleCode; // 送交角色
	} else {
		var userArray = [];
		
		if (idArray.length) {
			$(idArray).each(function(i, intrty) {
				if (intrty.indexOf('usr:') == 0) {
					userArray.push(intrty);
				}
			});
		}
		
		var userNameStr = userArray.toString().replace(new RegExp("usr:", "gm"), "");
		
		if (userNameStr.length <= 0) {
//			alert('没有选中人员，请重新选择送交人员！');
			rh.displayToast({text: '没有选中人员，请重新选择送交人员！'});
			_self._confirmSendFlag = true;
			return false;
		}
		_self.reqdata['TO_USERS'] = userNameStr; // 送交人  替换掉所有的usr:
	}
	
	_self.reqdata['TO_TYPE'] = toType; // 类别
	if (!(this.getNodeInstBean() == undefined)) {
		_self.reqdata['NI_ID'] = _self.getNodeInstBean().NI_ID; // 当前节点实例ID
	}
	_self.reqdata['NODE_CODE'] = _self._nextStepNodeCode; // 下一个节点CODE
	FireFly.doAct('SY_WFE_PROC', 'toNext', _self.reqdata).done(function(result) {
		console.log(result);
		if (result && result._MSG_ && result._MSG_.indexOf("OK,")>=0) {
//			alert('已经成功送交！');
			rh.displayToast({text: '已经成功送交！'});
//			$.mobile.pageContainer.find('#' + _self._parHandler.pId).remove();
			_self.back();
			_self._parHandler.back(500);
		} else if (result && result._MSG_) {
//			alert(result._MSG_);
			rh.displayToast({text: result._MSG_});
		} else {
//			alert('送交失败！');
			rh.displayToast({text: '送交失败！'});
		}
		setTimeout(function() {
			_self._confirmSendFlag = true;
		}, 1000);
	});
};
/**
 * 对页面字段进行控制，如显示、隐藏、必填等控制
 */
mb.vi.wfCardView.prototype._doFiledControl = function() {
	var _self = this;
	var _fileControlData = _self.getFieldControlBean();
	
//	var entirelyControl = _fileControlData.FIELD_CONTROL;		// 是否完全控制
	var exceptionFieldStr = _fileControlData.FIELD_EXCEPTION;  	// 可编辑
	var displayFieldStr = _fileControlData.FIELD_DISPLAY;		// 显示字段
	var hiddenFieldStr = _fileControlData.FIELD_HIDDEN;			// 隐藏字段
	var mustFieldStr = _fileControlData.FIELD_MUST;				// 必填字段
	var groupDis = _fileControlData.GROUP_DISPLAY || '';		// 显示分组
	var groupHide = _fileControlData.GROUP_HIDE || '';			// 隐藏分组
	
	var parServID = _self.opts.pId;
	
	// 可编辑字段
	/*if (entirelyControl == 'false') {
		_self._parHandler.form.disabledAll();
		if (exceptionFiledStr.length > 0) {
			$.each(exceptionFiledStr.split(','), function(i, item) {
				if (_self._parHandler.getItem(item)) {
					_self._parHandler.getItem(item).enabled();
				}
			});
		}
	}*/
	
	// 显示字段
	if (displayFieldStr && displayFieldStr.length > 0) {
		var disps = displayFieldStr.split(',');
		$.each(disps, function(i, itemCode) {
			var field = _self._parHandler.getItem(itemCode);
			if (field) {
				var $li = field.getContainer();
				if ($li.attr('model') != UIConst.ITEM_MOBILE_FORCEHIDDEN) { // 如果不是强制隐藏
					$li.show();
				}
			}
		});
	}
	// 隐藏字段
	if (hiddenFieldStr && hiddenFieldStr.length > 0) {
		$.each(hiddenFieldStr.split(','), function(i, item) {
			_self._parHandler.getItem(item).getContainer().hide();
		});
	}
	// 必填字段
	/*if (mustFieldStr && mustFieldStr.length > 0) {
		$.each(mustFieldStr.split(','), function(i, item) {
			_self._parHandler.form.setNotNull(item, true); // TODO 没有这个方法啊
		});
	}*/
	// 显示分组
	if (groupDis && groupDis.length > 0) {
		var groupArr = groupDis.split(',');
		$.each(groupArr, function(i, itemCode) {
			_self._parHandler.showGroup(itemCode); // TODO 这个方法大概也没有
		});
	}
	// 隐藏分组
	if (groupHide && groupHide.length > 0) {
		var groupArr = groupHide.split(',');
		$.each(groupArr, function(i, itemCode) {
			_self._parHandler.hideGroup(itemCode);
		});
	}
	
};
/**
 * 获取页面field控制的数据
 */
mb.vi.wfCardView.prototype.getFieldControlBean = function() {
	var _self = this;
	return _self._parHandler._formData.fieldControlBean;
};
/**
 * 初始化意见
 */
mb.vi.wfCardView.prototype._initMind = function() {
	var _self = this;
	var targetContainer = _self._parHandler.form.mainContainer;
	
	var group = $("<div id='"+_self._parHandler._pkCode+"_MIND' class='mb-card-group'></div>");
	
	if (_self._parHandler._formData.authBean.userDoInWf == "true"
			|| _self.reqdata._AGENT_USER_ == System.getUser('USER_CODE')) {
		var footer = $('<div data-role="footer" data-position="fixed" data-tap-toggle="false">')
			.css({"background":"#E3E3E3","border":"0","text-align":"center"})
			.appendTo($("#cardview"));
		var mindInputFieldCon = $("<div style='margin:.5em;'><input readonly='readonly' placeholder='我的意见...' /></div>"); // 意见输入框Field
		mindInputFieldCon.appendTo(footer);
	}
	
	
//	var mindFieldCon = $("<div data-role='fieldcontain' data-theme='b'></div>"); // 意见列表Field
//		mindFieldCon.append("<label for='mind-list'>意见列表</label>");
	var mindFieldCon = $("<div></div>");
		
//	group.append("<div class='mind-divider'></div>");
	group.append(mindFieldCon); // 追加到意见group中
	group.insertAfter(targetContainer.find('.mb-card-group:first'));
	
	var param = {
//			'servId': _self.servId, //_self._parHandler.getServSrcId(),
			'parHandler': _self,
			'servId': _self._parHandler.getServSrcId(),
			'dataId': _self.pkCode,
			'mind': _self._parHandler._mind, // TODO 确定意见是否是按照部门排起来的
			'wfCard': this,
			'pCon': [mindInputFieldCon, mindFieldCon]
	};
	
	this.mind = new mb.vi.mind(param); // TODO
	this.mind.show();
};
/**
 * 必填意见的判断，不通过mind去判断了，写到这里，直接查数据库
 */
mb.vi.wfCardView.prototype._mindMust = function() {
	var _self = this;
	
	var mindMustReq = {};
	mindMustReq.GENERAL = JsonToStr(_self.getMindCodeBean());
	mindMustReq.REGULAR = JsonToStr({'MIND_MUST': '2'});
	mindMustReq.TERMINAL = JsonToStr({'MIND_MUST': '2'});
	
	if (!(this.getNodeInstBean() == undefined)) {
		mindMustReq.NI_ID = _self.getNodeInstBean().NI_ID;
	}
	mindMustReq.DATA_ID = _self._parHandler._pkCode;
	
	FireFly.doAct('SY_COMM_MIND', 'checkFillMind', mindMustReq).done(function(result) {
		_self.mindMust = result
	});
};
/**
 * 意见 Code Bean
 */
mb.vi.wfCardView.prototype.getMindCodeBean = function() {
	var _self = this;
	
	return _self._parHandler._formData.mindCodeBean[0];
};
/**
 * 终止并发
 */
mb.vi.wfCardView.prototype.stopParallelWf = function() {
	var _self = this;
	
	FireFly.doAct('SY_WFE_PROC', 'stopParallelWf', _self.reqdata).done(function(result) {
		// 将此条信息的列表删除，避免点击过期的数据
		_self.back();
	});
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
 * 转发
 */
mb.vi.wfCardView.prototype._zhuanFa = function(idArray, nameArray) {
	var _self = this;
	_self.reqdata['TARGET_USERS'] = idArray.join(',');
	
	_self.reqdata['SERV_ID'] = _self._parHandler.servId;
	_self.reqdata['DATA_ID'] = _self._parHandler._pkCode;
	_self.reqdata['DATA_TITLE'] = _self.getBindTitle() + '(分发)'; 
	
	var result = rh_processData(WfActConst.SERV_PROC + '.cmZhuanFa.do', _self.reqdata);
	
	if (result.rtnstr == 'success') {
		Tip.show('转发成功', true);
	} else {
		Tip.show('转发失败', true);
	}
	_self._parHandler.back();
};
/**
 * 获取标题
 */
mb.vi.wfCardView.prototype.getBindTitle = function() {
	var _self = this;
	return _self._parHandler._formData.bindTitle;
};
/**
 * 签收 | 阅毕
 */
mb.vi.wfCardView.prototype.cmQianShou = function(event, actItem) {
	var _self = this;
	// 分发ID
	var sendId = actItem.SEND_ID;
	_self.reqdata.SEND_ID = sendId;
	FireFly.doAct('SY_COMM_SEND_SHOW_CARD', 'cmQianShou', _self.reqdata).done(function(result) {
		if (result.rtnstr == 'success') {
			_self.back();
		} else {
//			alert('操作失败！');
			rh.displayToast({text: '操作失败！'});
		}
	});
};
/**
 * 流程跟踪
 */
mb.vi.wfCardView.prototype.cmWfTracking = function() {
    var _self = this;
    FireFly.doAct("SY_WFE_TRACK","queryForMB", {
        "S_FLAG":"1",
        "PI_ID":_self.procInstId,
        "INST_IF_RUNNING":_self.wfState,
        "servId":_self.servId,
        "pkCode":_self._parHandler._pkCode
    }, false, true).then(function(result) {
        if (result) {
        	result.serverUrl = window.location.protocol + '//' + window.location.host;
        	console.log($.toJSON(result));
            rh.displayWorkFlowHistory(result);
        }
    });
};
/**
 * 签收转收文登记
 */
mb.vi.wfCardView.prototype.toShouwen = function(event, actItem) {
	var _self = this;
	
	var reqData = {};
	reqData.TMPL_CODE = _self._parHandler.getByIdData("TMPL_CODE");
	
	FireFly.doAct("OA_GW_TYPE_FW_SEND","findUserSwMenu",reqData).done(function(result) {
		// TODO 中华只用到了绑定一个模板，所以下面的没有测试
		if (result.SW_TMPL_CODE) { //找到了模板编码上设置的收文模板
			_self.qianshouToShouwen(result.SW_TMPL_CODE);
			return;
		} else {
			var dataList = result._DATA_;
			if (dataList.length == 0) {
//				alert('您还没有起草收文的权限。');
				rh.displayToast({text: '您还没有起草收文的权限。'});
				return;
			} else if (dataList.length == 1) {
				var menuItem = dataList[0];
				_self.qianshouToShouwen(menuItem.SERV_NAME, menuItem.SERV_ID); // TODO
				return;
			}
			
			var id = 'qianshou-to-shouwen-popup';
			var popupWrp = $("<div data-role='popup' id='" + id + "_popup' data-dismissible='false'>" +
								"<a href='javascript:void(0);' class='zhbx-btn-close ui-btn ui-corner-all ui-shadow ui-btn-a ui-icon-delete ui-btn-icon-notext ui-btn-right'>" +
									"Close" +
								"</a>" +
							 "</div>");
			var popupListView = $("<ul data-role='listview' data-inset='true' style='min-width:210px;'>" +
									"<li data-role='divider'>" +
										"请选择收文类型" +
									"</li>" +
								  "</ul>").appendTo(popupWrp);
			$.each(dataList, function(index, item) {
				popupListView.append("<li><a href='javascript:void(0);' class='qianshou-to-shouwen-item-js' data-servid='" + item['SERV_ID'] + "'>" + item['SERV_NAME'] + "</a></li>");
			});
			$.mobile.activePage.append(popupWrp); // 追加popup到活动页
			popupWrp.popup().enhanceWithin(); // 初始化popup
			
			// 关闭绑定事件
			popupWrp.on('tap', 'a.zhbx-btn-close', function() {
				event.preventDefault();
				event.stopImmediatePropagation();
				popupWrp.popup('close');
			});
			// 列表绑定点击事件
			popupWrp.on('tap', 'a.qianshou-to-shouwen-item-js', function(event) {
				event.preventDefault();
				event.stopImmediatePropagation();
				
				var servId = $(this).attr('data-servid')
				,	servName = $(this).text();
				
				if (!confirm('是否转换成 ' + servName + '?')) {
					return;
				}
				try {
					_self.qianshouToShouwen(servId);
				} finally {
					popupWrp.popup('close');
				}
			});
		}
	});
};
/**
 * 签收转收文登记的转换方法
 */
mb.vi.wfCardView.prototype.qianshouToShouwen = function(newServId) {
	var _self = this;
	
	var params = {};
	params['SEND_ID'] = _self._parHandler.getByIdData('SEND_ID');
	params['nextServId'] = newServId;
	params['SERV_ID'] = _self._parHandler.getByIdData('TMPL_CODE');
	params['DATA_ID'] = _self._parHandler.getByIdData('GW_ID');
	
//	$.mobile.loading('show', {text: '处理中...',textVisible: true,textonly: false});
	
	FireFly.doAct('OA_GW_TYPE_FW_SEND', 'toShouwen', params).done(function(result) {
//		$.mobile.loading('hide');
		if (result != null && StringUtils.startWith(result._MSG_, 'OK')) {
			_self.back();
		} else {
//			alert(result._MSG_);
			rh.displayToast({text: result._MSG_});
		}
	});
};
/**
 * 办结
 */
mb.vi.wfCardView.prototype.finish = function() {
	var _self = this;
	FireFly.doAct('SY_WFE_PROC', 'finish', _self.reqdata).done(function(result) {
//		alert('办结成功！');
		rh.displayToast({text: '办结成功！'});
		_self.back();
	});
};
/**
 * 取消办结
 */
mb.vi.wfCardView.prototype.undoFinish = function() {
	var _self = this;
	FireFly.doAct('SY_WFE_PROC', 'undoFinish', _self.reqdata).done(function(result) {
		// 返回到list页面
		_self.back();
	});
};
/**
 * 收回
 */
mb.vi.wfCardView.prototype.withdraw = function(event, actItem) {
	var _self = this;
	var wdlist = actItem.wdlist;
	if (wdlist) {
		if (wdlist.length == 0) {
//			alert('没有可以收回的流程。');
			rh.displayToast({text: '没有可以收回的流程。'});
		} else if (wdlist.length == 1) {
			_self._sendWithdrawReq(wdlist[0]['NI_ID']);
		} else { // 如果有多个用户请求需要被收回
			// 弹出dialog
			var pageWrp = $("<div data-role='page' id='" + _self.procInstId + "_withdraw'></div>");
			var headerWrp = $("<div data-role='header' data-position='fixed' data-tap-toggle='false' data-theme='b'>" +
//								"<a href='#' data-rel='back' data-icon='back' data-inline='true' data-iconpos='notext'></a>" +
								"<a href='' data-rel='back' data-icon='back' data-iconpos='notext'>返回</a>" +
								"<h1></h1>" +
								"</div>").appendTo(pageWrp);
			var contentWrp = $("<div role='main' class='ui-content'></div>").appendTo(pageWrp);
			var tempArr = [];
			tempArr.push("<fieldset data-role='controlgroup'>");
			for (var i=0; i<wdlist.length; i++) {
				var niBean = wdlist[i];
//				tempArr.push("<input type='checkbox' id='" + niBean['NI_ID'] + "'");
//				tempArr.push("<label for='" + niBean['NI_ID'] + "'>" + niBean['TO_USER_NAME'] + "</label>");
				tempArr.push("<label for='" + niBean['NI_ID'] + "'>" + niBean['TO_USER_NAME'] + "</label>");
				tempArr.push("<input type='checkbox' id='" + niBean['NI_ID'] + "' />");
			}
			tempArr.push("</fieldset>");
			contentWrp.html(tempArr.join(""));
			
//			var footerWrp = $("<div	data-role='footer' data-position='fixed' data-tap-toggle='false'>" +
//								"<a href='#' id='withdrawCancel' data-role='button' data-rel='back' data-icon='delete' class='ui-btn-left'>取消</a>" +
//								"<span class='ui-title'></span>" +
//								"<a href='#' id='withdrawSave' data-role='button' data-icon='check' class='ui-btn-right'>确认</a>" +
//								"</div>").appendTo(pageWrp);
			var footerWrp = $("<div data-role='footer' data-position='fixed' data-tap-toggle='false' data-theme='b'>" +
								"<div data-role='navbar'>" +
								"<ul class='ui-grid-a'>" +
								"<li class='ui-block-a'>" +
								"<a href='#' id='withdrawCancel' data-icon='cancel'>取消</a>" +
								"</li>" +
								"<li class='ui-block-b'>" +
								"<a href='#' id='withdrawSave' data-icon='confirm'>确认</a>" +
								"</li>" +
								"</ul>" +
								"</div>" +
								"</div>").appendTo(pageWrp);
			
			// 确认
//			footerWrp.on('tap', '#widthdrawSave', function(event) {
			footerWrp.on('vclick', '#withdrawSave', function(event) {
				var ids = [];
				contentWrp.find("input:checked").each(function(i, obj) {
					ids.push(this.id);
				});
				_self._sendWithdrawReq(ids.join(","));
			});
			
			pageWrp.appendTo($.mobile.pageContainer).page();
			pageWrp.on('pagehide', function(event, ui) {
				$(this).remove();
			});
			$.mobile.changePage($('#' + _self.procInstId + '_withdraw'), {transition: 'slideup'});
		}
	}
};
/**
 * 发送收回请求
 */
mb.vi.wfCardView.prototype._sendWithdrawReq = function(nextNiIds) {
	var _self = this;
	var data = {};
	$.extend(data, _self.reqdata, {'nextNiIds': nextNiIds});
	FireFly.doAct('SY_WFE_PROC', 'withdraw', data).done(function(result) {
		_self.back();
	});
};
/**
 * 简单分发
 */
mb.vi.wfCardView.prototype.cmSimpleFenFa = function(event, actItem) {
	var _self = this;
	
	$('#cmSimpleFenFa_dialog').remove(); // 移除旧dialog
	
	var params = { // 异步加载子节点参数
			'DATA_ID': _self._parHandler._pkCode,
			'userSelectDict': 'SY_ORG_DEPT_USER_SUB',
			'displaySendSchm': true,
			'includeSubOdept': false	// 这个参数貌似没用，控制组织机构的是USER_SUB和USER这两个字典
	};
	
	//用户可重载参数
	if(this.beforeCmSimpleFenFa){
		var result = this.beforeCmSimpleFenFa(actItem,params);
		if(!result){
			return;
		}
	}
	
	// 异步加载子节点请求路径
	var dictUrl = FireFly.getContextPath() + '/SY_COMM_INFO.dict.do' + '?' + $.param(params);
	// treeview的输入参数
	var extendTreeSetting = {
		cascadecheck: true,
		checkParent: false,
		url: dictUrl,
		dictId: '@com.rh.core.serv.send.SimpleFenfaDict',
		rhexpand: false,
		showcheck: true,
		rhLeafIcon: "user"
	};
	var treeData = [];
	
	FireFly.getDict('@com.rh.core.serv.send.SimpleFenfaDict', '', '', '', '', params, true).then(function(tempData) {
		
		// 构造树形显示数据
		treeData.push(tempData['CHILD'][0]);
		if (tempData['CHILD'].length > 1) {
			treeData.push(tempData['CHILD'][1]);
		}
		extendTreeSetting['data'] = treeData;
		
		// 加载树形选择页
		rh.setRightTopMenu({"naviTitle":actItem.ACT_NAME});
		$.mobile.loadPage('../html/treeview.html');
		$.mobile.document.off('pageinit', '#treeview').on('pageinit', '#treeview', function(event, ui) {
			var pageWrp = $(this);
			var headerWrp = pageWrp.find('#treeview_header');
			var contentWrp = pageWrp.find('#treeview_content');
			var footerWrp = pageWrp.find('#treeview_footer');
			
			var $treeWrp = $('<div></div>').appendTo(contentWrp);
			
			// 取消
			footerWrp.off('vclick')
			.on('vclick', '#cancel', function(event) {
				_self.back();
			})
			.on('vclick', '#save', function(event) {
				event.preventDefault();
				event.stopImmediatePropagation();
				rh.displayLoading({text: '加载中...', timeout: '10'});
				var ids = [];
				var checks = $treeWrp.find('.bbit-tree-node-leaf .checkbox_true_full');
				$.each(checks, function(i, el) {
					var $parent = $(this).parent(),
						id = $parent.attr('itemid');
					ids.push(id);
				});
				// 发送
				var sendObj = {
					'SERV_ID': _self._parHandler.servId,
					'DATA_ID': _self._parHandler._pkCode,
					'DATA_TITLE': _self.getBindTitle()
				};
				sendObj.fromScheme = 'yes'; // 来源于方案
				sendObj.ifFirst = 'yes';	
				sendObj._extWhere = " and DATA_ID = '" + _self._parHandler._pkCode + "'";
				sendObj.SEND_ITEM = [{'sendId': ids.join(',')}];
				sendObj.includeSubOdept = false; // 是否包含分发方案
				
				// loading
//				$.mobile.loading('show', {text: '处理中...', textVisible: true, textonly: false});
				
				setTimeout(function() { // 加一点儿延迟,提升用户体验
					try {
						var data = $.toJSON(sendObj);
						FireFly.doAct('SY_COMM_SEND_SHOW_USERS', 'autoSend', {'data': data}).then(function(result) {
							// 去掉loading
							setTimeout(function() { rh.hideLoading({}); }, 100);
//							$.mobile.loading('hide');
							if (result['_MSG_'].indexOf('OK,') < 0) {
//								alert('分发失败,请联系管理员!');
								rh.displayToast({text: '分发失败,请联系管理员!'});
							} else {
//								alert('分发成功,点击确认按钮返回!');
								rh.displayToast({text: '分发成功,点击确认按钮返回!'});
								_self.back();
							}
						});
					} catch (e) {
						throw e;
					} finally {}
				}, 0);
			});
			$treeWrp.treeview(extendTreeSetting);
			$.mobile.pageContainer.pagecontainer('change', pageWrp);
		});
	});
};

/**
 * 简单分发之前调用，常用于重置params的值。
 */
mb.vi.wfCardView.prototype.beforeCmSimpleFenFa = function(actItem,params){
	return true;
};