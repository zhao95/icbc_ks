/**
 * 手机卡片页面渲染引擎
 * 不带任何的流程信息
 */
GLOBAL.namespace('mb.vi');
/**
 * 构造方法
 */
mb.vi.cardView2 = function(options) {
	var defaults = {
		'id': 'cardview',
		'act': UIConst.ACT_CARD_READ, // 初始化设定为修改模式,因为手机端还没有发起流程的功能
		'menuType': "panel", 			// 流程按钮弹出类型：panel与dialog
		'parHandler': '',				// 卡片父句柄
		'changeHash': true
	};
	
	this.opts = $.extend(defaults, options);
	
	this.id = this.opts.id; 		// 	卡片页面ID cardview
	this.servId = this.opts.sId; 	//	服务ID
	this._pkCode = this.opts.pId;	//	数据ID
	this._actVar = this.opts.act;  	// 	卡片操作:保存,修改等
	this.menuType = this.opts.menuType;
	this.changeHash = this.opts.changeHash; // 跳转页面时是否更换历史记录
	
	this.byIdParams = this.opts.byIdParams; //使用方法byidMB时会传入的参数对象
	
	this.contentHei = ''; 			// 内容总高度
	this.scrollHei = ''; 			// 滚动高度
	this.screenHei = ''; 			// 屏幕高度
	
};
/**
 * 显示页面主方法
 */
mb.vi.cardView2.prototype.show = function() {
	var _self = this;
	
	// 设置页面布局
	_self._bldPageLayout();
	
//	MobileHelper.loadShow('加载中...', true, false);
	// 获取数据,渲染卡片页面
	_self._initMainData().then(function() {
		_self._bldCardLayout();
		// ......
		_self.pageWrp.enhanceWithin();  // 初始化页面
		_self.addElement();				// 页面初始化完成后,添加自定义元素
		
		//意见分组框：隐藏
		$(".ui-field-contain[code='MIND_ITEM']").hide();
		
		// 页面构造完成后,将mobile的page内容设置为pageWrp,间接跳转
		// 跳转到卡片页面
		_self._guideTo(this._pkCode);
		
		// 重置页面主体高度
		_self._resetContentHei();
		
		_self._afterLoad();
	}, function(error) {
		console.log('-----出错-----');
		alert('错误!');
		console.log(error);
	});
};
/**
 * 设置页面布局
 */
mb.vi.cardView2.prototype._bldPageLayout = function() {
	var _self = this;
	
	this.pageWrp = $('#' + this.id);				// 页面
	this.headerWrp = $('#' + this.id + '_header');	// 头
	this.contentWrp = $('#' + this.id + '_content');// 内容
	this.footerWrp = $('#' + this.id + '_footer');	// 脚
	
	this.contentWrp.empty();						// 清空主体页面的内容
};
/**
 * 页面构造完成后,跳转到指定页面
 */
mb.vi.cardView2.prototype._afterLoad = function() {
	var _self = this;
	// 格式化时间为timeago
	_self.listenTimeago();
	// 监听头像点击事件
	_self._listenUserImg();
	//加载工程级JS
	_self._excuteProjectJS();
};
/*
 * 监听头像点击事件
 */
mb.vi.cardView2.prototype._listenUserImg = function() {
	var _self = this;
	
	$('#' + _self.id)
		.off('vclick', "img[data-user-img]")
		.on('vclick', "img[data-user-img]", function(event) {
			event.preventDefault();
			
			var userCode = $(this).attr('data-user-img');
			rh.startUserChat({USER_CODE: userCode});
	});
};
/*
 * 将所有data-time-ago属性的显示框格式化时间为timeago
 */
mb.vi.cardView2.prototype.listenTimeago = function() {
	var _self = this;
	
	$('[data-time-ago]').each(function(i, o) {
		var timestr = $(o).attr('data-time-ago');
		$(o).text(jQuery.timeago(timestr));
	});
};
/*
 * 卡片加载后执行工程级js方法
 */
mb.vi.cardView2.prototype._excuteProjectJS = function() {
	var _self = this;
    //var loadArray = this._data.SERV_CARD_LOAD_NAMES.split(",");
	var loadArray = [_self.servId]; //暂时只支持本级JS
    for (var i = 0;i < loadArray.length;i++) {
    	if (loadArray[i] == "") {
    		return;
    	}
    	load(loadArray[i]);
    }
	function load(value) {
		var pathFolder = value.split("_");
		var lowerFolder = (pathFolder[0] || "").toLowerCase();
	    var jsFileUrl = FireFly.getContextPath() + "/" + lowerFolder + "/servjs/" + value + "_card_mb.js";
	    jQuery.ajax({
	        url: jsFileUrl,
	        type: "GET",
	        dataType: "text",
	        async: false,
	        data: {},
	        success: function(data){
	            try {
	                var servExt = new Function(data);
	                servExt.apply(_self);
	            } catch(e){
	            	console.log(e.stack);
	            	alert('错误!');
	            }
	        },
	        error: function(){;}
	    });			
	};
};

/**
 * 跳转到该页面
 */
mb.vi.cardView2.prototype._guideTo = function(id) {
	var _self = this;
	this.pageWrp.page().enhanceWithin(); // page组件自动初始化
	$.mobile.pageContainer.pagecontainer('change', _self.pageWrp, {changeHash: _self.changeHash}); // 更换page内容为当前pageWrp
};
/**
 * 重置页面主体高度
 */
mb.vi.cardView2.prototype._resetContentHei = function() {
	var _self = this;
	// 屏幕高度
//	this.screenHei = $.mobile.getScreenHeight() 
//						- (this.headerWrp ? this.headerWrp.height() : 0) 
//						- ($.mobile.activePage.find('.ui-footer') ? $.mobile.activePage.find('.ui-footer').height() : 0)
//						- 5; // 有5px的误差
//	this.contentWrp.css('height', this.screenHei);
	
	setTimeout(function() {
		$('#' + _self.id + '_content').css({
			'height': $.mobile.getScreenHeight(),
			'overflow-y': 'hidden'
		});
		_self.cardIScroll = new iScroll(_self.id + '_content', {
			scrollbars: true
		});
	}, 200);
};
mb.vi.cardView2.prototype.destroyIScroll = function() {
	this.cardIScroll.destroy();
};
/**
 * 初始化数据
 */
mb.vi.cardView2.prototype._initMainData = function() {
	var _self = this,
		param = {};
	
	// 获取服务定义
	var cachedServData = FireFly.cache[this.servId + '-' + FireFly.servMainData];
	if (cachedServData) {
		_self._servData = cachedServData;
	} else {
		param['LOAD_SERV_DATA'] = true;
	}
	
	if (_self.byIdParams) {
		param = $.extend(param, _self.byIdParams);
	}
	console.log("卡片方法byId4Card参数：" + $.toJSON(param));
	return FireFly.byId4Card(this.servId, this._pkCode, param, true).then(function(result) {
		//在此对按钮进行过滤，只留下移动标志(ACT_MOBILE_FLAG)为1的按钮
		if (result && result.form && result.form.buttonBean) {
			var filterBtn = [];
			$.each(result.form.buttonBean, function(i, btn) {
				if (btn.ACT_MOBILE_FLAG == 1) { //移动标志
					filterBtn.push(btn);
		        }
			});
			result.form.buttonBean = filterBtn;
		}
		if (result && result.serv && result.serv.BTNS) {
			var filterBtn = [];
			$.each(result.serv.BTNS, function(i, btn) {
				if (btn.ACT_MOBILE_FLAG == 1) { //移动标志
					filterBtn.push(btn);
		        }
			});
			result.serv.BTNS = filterBtn;
		}
		
		_self._data = result;
		console.debug(result);
		// 如果有服务定义数据,将服务定义数据添加到缓存中
		if (result['serv']) {
			_self._servData = result['serv'];
			FireFly.setCache(_self.servId, FireFly.servMainData, result['serv']);
		}
		_self._formData = result['form']; // form数据
		_self._fileData = result['file'] ? result['file']['_DATA_'] : []; // 附件数据
		_self._linkData = result['link'] ? result['link']['_DATA_'] : []; // 相关文件数据
		_self._mind = result['mind'] || {}; // 意见数据
		
		// 将服务名称添加到标题
		_self.servName = _self._servData.SERV_NAME || '';
		_self.servKeys = _self._servData.SERV_KEYS || '';
		
		_self.headerWrp.find('h2').text(_self.servName);
	});
};
mb.vi.cardView2.prototype._bldCardLayout = function() {
	var _self = this;
	// 构建按钮列表
	this._bldBtnBar();
	// 构建form
	this._bldForm();
};
/**
 * 构建按钮布局
 */
mb.vi.cardView2.prototype._bldBtnBar = function() {
	var _self = this;
	
	//下一步按钮点击事件转向
	$(".nextBtn", _self.pageWrp).attr("href", _self.nextBtnLayoutId());
};

/**
 * 弹出按钮列表方式是否panel方式
 */
mb.vi.cardView2.prototype.nextBtnIfPanel = function() {
	return this.menuType == "panel";
};

/**
 * 关闭弹出的按钮列表
 */
mb.vi.cardView2.prototype.nextBtnPanelClose = function() {
	if (this.nextBtnIfPanel()) {
		$(this.nextBtnLayoutId()).panel("close");
	} else {
		//$(this.nextBtnLayoutId()).dialog("close");
		$.mobile.back();
	}
};

/**
 * 取得按钮列表所在的jQuery对象
 */
mb.vi.cardView2.prototype.nextBtnLayoutId = function() {
	if (this.nextBtnIfPanel()) {
		return "#cardPanel";
	} else {
		return "#cardDialog";
	}
};

mb.vi.cardView2.prototype._bldForm = function() {
	var _self = this;
	
	var opts = {
			'pId': this._pkCode,
			'sId': this.servId,
			'data': this._servData,
			'parHandler': this,
			'pCon': this.contentWrp,
			'readOnly': this._actVar == UIConst.ACT_CARD_READ ? true : false
	};
	this.form = new mb.ui.form2(opts); // TODO
	this.form.render();
	
	// 如果card只读或修改时,填充form数据
	if (this._actVar == UIConst.ACT_CARD_READ || this._actVar == UIConst.ACT_CARD_MODIFY) {
		this.form.fillData(this._formData, this._fileData, this._linkData);
	}
	
	// TODO 工作流控制,这里先省略工作流
	// 工作流控制
	if (_self._formData && _self._formData.S_WF_INST) {
		var wfparam = {};
			wfparam['sId'] = this.servId;
			wfparam['pkCode'] = this._pkCode;
			wfparam['parHandler'] = this;
			
		this.wfCard = new mb.vi.wfCardView(wfparam); // TODO
		this.wfCard.render();
	} else {
		$(".nextBtn", _self.pageWrp).text("操作");
		_self._bldBtn();
	}
};

/**
 * 点击“操作”类按钮:空实现，需用户自行实现
 */
mb.vi.cardView2.prototype.clickOperBtn = function(actItem) {
	
};

/**
 * 过滤操作按钮接口，用户可在卡片JS中覆盖
 */
mb.vi.cardView2.prototype.filterBtn = function(btns) {
	return btns;
};

/**
 * 提交原生端的菜单数据
 */
mb.vi.cardView2.prototype.getFormatMenus = function() {
	var _self = this;
	
	/*var menuArr = [];
	var servBtns = _self.filterBtn(_self._servData.BTNS || []);
	$.each(servBtns, function(i, actItem) {
		var eventName = UIConst.EVENT_PRE + actItem.ACT_CODE;
		menuArr.push({
			"eventName":eventName,
			"title":actItem.ACT_NAME,
		});
		
		//事件监听
		$(document).unbind(eventName).bind(eventName, function(event) {
			_self.clickOperBtn(actItem);
		});
	});*/
	
	rh.setRightTopMenu({"naviTitle":_self.servName});
};

/**
 * 添加按钮,可以从后端过滤出移动端按钮,或者从前台过滤按钮
 */
mb.vi.cardView2.prototype._bldBtn = function() {
	var _self = this;
	
	_self.getFormatMenus();
	
	var iconMatch = {
			"save":"saveIcon"
		};
	var controlgroup = $('<div>')
		.addClass("ui-controlgroup-controls ui-panel-inner")
		.css({"padding-bottom":"0"});
	var nextBtnPanel = _self.nextBtnLayoutId();
	if (_self.nextBtnIfPanel()) {
		$(".ui-controlgroup-controls", nextBtnPanel).remove();
		$(nextBtnPanel).prepend(controlgroup);
	} else {
		$(".ui-content",nextBtnPanel).empty();
		$(".ui-content",nextBtnPanel).append(controlgroup);
	}
	
	//按钮数组构建列表页面
	var servBtns = _self._servData.BTNS || [];
	$.each(servBtns, function(i, actItem) {
		console.log(actItem);
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

		// 绑定事件
		btn.on("vclick", function() {
			_self.clickOperBtn(actItem);
		});
	});
	controlgroup.children(":first").addClass("ui-first-child");
	controlgroup.children(":last").addClass("ui-last-child").attr("style","");
};

/**
 * 获取修改的页面数据
 */
mb.vi.cardView2.prototype.getChangeData = function() {
	var _self = this;
	
	var changeData = "";
	if (this._actVar == UIConst.ACT_CARD_ADD) { // 添加操作
		changeData = _self.form.getItemsValues(); // 获取form的所有值
	} else { // 非添加操作
		changeData = _self.form.getChangedItems(); // 获取页面修改的字段值集合对象
	}
	if (changeData['USER_PASSWORD'] == '') {
		delete changeData['USER_PASSWORD'];
	}
	if (_self.extendSubmitData) { // 扩展的参数传递到后台
		changeData = $.extend(changeData, _self.extendSubmitData);
	}
	return changeData;
};
/**
 * 修改保存
 */
mb.vi.cardView2.prototype.modifySave = function(changeData, afterReload) {
	var _self = this;
	
	changeData[UIConst.PK_KEY] = _self._pkCode;
	if (_self.itemValue('S_MTIME')) {
		changeData['S_MTIME'] = _self.itemValue('S_MTIME');
	}
//	var resultData = FireFly.cardModify(_self.opts.sId, changeData);
	console.log('-------请求后台修改方法-------');
	_self.afterSave(resultData);
	if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
		_self._pkCode = resultData[_self.servKeys]; // servKeys是服务主键
		_self.setParentRefresh();
		_self._lastData = resultData;
		if (afterReload == true) {
			_self.refresh();
		}
	}
};
/**
 * 保存form修改的数据
 */
mb.vi.cardView2.prototype.save = function() {
	this._saveForm();
};
mb.vi.cardView2.prototype._saveForm = function() {
	var _self = this;
	
	var changeData = _self.form.getModifyData();
	if ($.isEmptyObject(changeData)) {
//		alert('没有修改数据,未做提交!');
		rh.displayToast({text: '没有修改数据,未做提交!'});
		return false;
	} else {
		// 有修改才做校验
		if (!_self.form.validate()) {
//			alert('校验失败!');
			rh.displayToast({text: '校验失败!'});
			return false;
		}
	}
	if (_self._actVar == UIConst.ACT_CARD_MODIFY) { // 修改
		changeData[UIConst.PK_KEY] = _self._pkCode;
		if (_self.itemValue('S_MTIME')) {
			changeData['S_MTIME'] = _self.itemValue('S_MTIME');
		}
		var resultData = FireFly.cardAddModify(_self.servId, changeData, false);
		if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
			console.log('已保存!');
			_self._pkCode = resultData[_self.servKeys];
			_self.refresh();
			_self.setParentRefresh();
		} else if (resultData[UIConst.RTN_MSG]) {
			console.log(resultData[UIConst.RTN_MSG].split(',')[1]);
			alert('错误!');
		}
	} else if (_self._actVar == UIConst.ACT_CARD_ADD) { // 添加
		changeData = _self.form.getAllItemsValue();
		var resultData = FireFly.cardAdd(_self.opts.sId, changeData);
		if (resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
			console.log('已保存!');
			_self._pkCode = resultData[_self.servKeys];
			_self._actVar = _self.opts.act = UIConst.ACT_CARD_MODITY;
			_self.refresh();
			_self.setParentRefresh();
		} else if (resultData[UIConst.RTN_MSG]) {
			console.log(resultData[UIConst.RTN_MSG].split(',')[1]);
			alert('错误!');
		}
	}
	return true;
};
/**
 * 获取字段的值
 */
mb.vi.cardView2.prototype.itemValue = function(itemCode) {
	var _self = this;
	
	return _self.form.itemValue(itemCode);
};
/**
 * 保存之后执行,业务代码可覆盖此方法
 */
mb.vi.cardView2.prototype.afterSave = function(resultData) {
};
/**
 * 设置点击返回时刷新父列表页面
 */
mb.vi.cardView2.prototype.setParentRefresh = function() {
	var _self = this;
	if (_self.opts.parHandler) { // 如果有父句柄
		_self.opts.parHandler.showRefresh = true;
	}
};
/**
 * 刷新卡片页面
 */
mb.vi.cardView2.prototype.refresh = function() {
	var _self = this;
	_self.nextBtnPanelClose();
	
	$.mobile.loadPage('../html/cardview.html', {reloadPage: true});
	/*
	// 卡片刷新
	var options = {
			'sId': _self.servId,
			'pId': _self._pkCode,
			'parHandler': _self.opts.parHandler,
			'changeHash': false
	};
	var cardView = new mb.vi.cardView2(options);
	cardView.show();
	
	// 将句柄放入dom中
	$(this).data('handler', cardView);
	*/
};
/**
 * 当页面的jqueryMobile初始化完成后,框架自动添加的元素添加完之后,添加自定义的元素
 */
mb.vi.cardView2.prototype.addElement = function() {
	this.form.addElement();
};
mb.vi.cardView2.prototype.back = function(time) {
	time = time ? time : 0;
	setTimeout(function() {
		$.mobile.back();
	}, time);
};
/**
 * 取得SERV_SRC_ID的值，引用自 服务（共享附件设置）
 */
mb.vi.cardView2.prototype.getServSrcId = function() {
	var result = this._servData.SERV_SRC_ID;
	return result || '';
};

mb.vi.cardView2.prototype.getItem = function(code) {
	return this.form.getItem(code);
};











