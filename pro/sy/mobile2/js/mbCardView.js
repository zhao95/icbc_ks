/**
 * 手机卡片页面渲染引擎
 */
GLOBAL.namespace('mb.vi');
/**
 * 构造方法
 */
mb.vi.cardView = function(options) {
	var defaults = {
			'id': 'cardview',
			'act': 'cardRead'
	};
	
	this.opts = $.extend(defaults, options);
	
	this.id = this.opts.id;				// 当前卡片页面的ID this.id = cardview
	this.servId = this.opts.sId;		// card 服务id
	this.pkCode = this.opts.pkCode;		// card 数据主键
	this.pId = this.opts.pId;			// list item id  TODO 未用到
	this.cardAct = this.opts.act;       // card 操作
	
	this.niId = this.opts.niId;			// 流程节点Id
	this.sendId = this.opts.sendId; 	// 分发Id
	this.ownerCode = this.opts.ownerCode; 	// 数据所有人
	
//	this.secondStep = this.opts.secondStep; // 从mbDeskView.js中传下来的文件类别 TODO 以后不用这个值
};
/**
 * 显示卡片页面，主方法
 */
mb.vi.cardView.prototype.show = function() {
	var _self = this;
	_self._bldPageLayout();
	_self._afterLoad();
	$.mobile.loading("show", {
		text: "加载中...",
		textVisible: true,
		textonly: false
	});
	_self._initMainData().then(function() {
		_self._bldCardLayout();
		//TODO 有待提取方法
		_self._resetBtn();
		_self.pageWrp.enhanceWithin(); // 初始化页面
	});
	$.mobile.loading('hide');
};
/**
 * 设置布局
 */
mb.vi.cardView.prototype._bldPageLayout = function() {
	var _self = this;
	this.pageWrp = $('#'+this.id);
	this.headerWrp = $('#'+this.id+'_header');
	
	// 这里是卡片页面的刷新按钮，暂时屏蔽掉
	/*this.headerWrp.on('vclick', '.js-refresh', function(event) {
		event.preventDefault();
		event.stopImmediatePropagation();
		_self._refresh();
	});*/
	
	this.contentWrp = $('#'+this.id+'_content');
	this.footerWrp = $('#'+this.id+'_footer');
};
/**
 * 页面构造完成后，引擎执行动作
 */
mb.vi.cardView.prototype._afterLoad = function() {
	var _self = this;
	this._guideTo(this.pkCode);
};
/**
 * 跳转到该页面
 */
mb.vi.cardView.prototype._guideTo = function(id) {
	this.pageWrp.page().enhanceWithin(); // 页面自动初始化--其实我也不知道这玩意儿是干什么的，有时间弄明白
	$.mobile.pageContainer.pagecontainer('change', this.pageWrp);
};
/**
 * 初始化数据
 */
mb.vi.cardView.prototype._initMainData = function() {
	var _self = this,
		param = {};
	// 获取服务定义
	var cachedServData = FireFly.cache[this.servId + "-" + FireFly.servMainData];
	if (cachedServData) {
		_self._servData = cachedServData;
	} else {
		param["LOAD_SERV_DATA"] = true;
	}
	
	if (this.niId) {
		param["NI_ID"] = this.niId;
		param["_AGENT_USER_"] = this.ownerCode;
	}
	if (this.sendId) {
		param["SEND_ID"] = this.sendId;
	}
	
	// 一次性获取serv/form/mind数据，减少与服务器连接
	console.log('---当前服务ID---' + this.servId + '---');
	return FireFly.byId4Card(this.servId, this.pkCode, param).then(function(result) {
		_self._data = result;
		console.debug(result);
		if (result["serv"]) {
			_self._servData = result["serv"];
			FireFly.setCache(_self.servId, FireFly.servMainData, result["serv"]);
		}
		
		_self._formData = result["form"]; // form数据
		_self._fileData = result["file"] ? result["file"]["_DATA_"] : []; // file数据
		_self._linkData = result["link"] ? result["link"]["_DATA_"] : []; // link数据
		_self._mind = result["mind"] || {}; // mind数据
		
		// 添加服务名称
		_self.servName = _self._servData.SERV_NAME || '卡片';
		_self.headerWrp.find('h2').text(_self.servName);
	});
};
mb.vi.cardView.prototype._bldCardLayout = function() {
	var _self = this;
	// 构建按钮
	this._bldBtnBar();
	// 构建form
	this._bldForm();
};
/**
 * 构建按钮布局
 */
mb.vi.cardView.prototype._bldBtnBar = function() {
	this.btns = {};
	// 每屏按钮数量限制为3--zhbx显示在5个
	this.btnCountLimit = 4;
	this.footerNavBar = $("<div data-role='navbar' class='customNav'></div>").appendTo(this.footerWrp);
	this.footerNavWrp = $("<ul></ul>").appendTo(this.footerNavBar);
};
/**
 * 构建卡片form，调用mb.ui.form组件
 * 加入工作流控制，调用mb.vi.wfCardView组件
 */
mb.vi.cardView.prototype._bldForm = function() {
	var _self = this;
	
	// 1.渲染form
	var opts = {
			"pId": this.pkCode, // 这里的主键ID是用来给生成的form做id的
			"sId": this.servId,
			"pkCode": this.pkCode,
			"data": this._servData,
			"parHandler": this,
			"pCon": this.contentWrp
	};
	this.form = new mb.ui.form(opts);
	this.form.render();
	
	// card只读时，填充form数据
	if (this.cardAct == UIConst.ACT_CARD_READ) {
		this.form.fillData(this._formData, this._fileData, this._linkData);
	}
	
	// 工作流控制
	if (typeof(_self.itemValue('S_WF_INST')) != 'undefined' && _self.itemValue('S_WF_INST').length > 0) {
		var wfparam = {};
			wfparam['sId'] = this.servId;
			wfparam['pkCode'] = this.pkCode;
			wfparam['parHandler'] = this;
			
		this.wfCard = new mb.vi.wfCardView(wfparam); // TODO
		this.wfCard.render();
	}
	
	// 加载外部脚本
	this._excuteProjectJS();
};
/**
 * 如果没有按钮，清除navbar
 */
mb.vi.cardView.prototype._resetBtn = function() {
	var len = this.footerWrp.find('li').length;
	if (!len) {
		this.footerWrp.empty();
	}
};
/**
 * 内部调用的刷新方法
 */
mb.vi.cardView.prototype._refresh = function() {
	var _self = this;
	_self.contentWrp.empty();
	_self.footerWrp.empty();
	_self._render();
};
/**
 * 重新渲染页面
 */
mb.vi.cardView.prototype._render = function() {
	var _self = this;
	$.mobile.loading('show', {
		text: '加载中...',
		textVisible: true,
		textonly: false
	});
	
	_self._initMainData().then(function() {
		_self._bldCardLayout();
		_self._resetBtn();
		_self.pageWrp.enhanceWithin();
	});
	$.mobile.loading('hide');
	
	// 这段压缩的时候报错
	/*.catch(function(err) {
		console.debug(err);
	}).finally(function() {
		$.mobile.loading('hide');
	});*/
};
/**
 * 添加按钮,每屏显现4个按钮：3个功能按钮1个更多，其余按钮点击更多现实在popup页面内
 */
mb.vi.cardView.prototype.addBtn = function(nodeCon) {
	var _self = this;
	var count = this.footerWrp.find('li').length;
	
	if (count < this.btnCountLimit) { // 小于上限数，添加到导航
		nodeCon.appendTo(this.footerNavWrp);
	} else if (count == this.btnCountLimit) { // 更多按钮
		var nextWrp = $("<li class='next'><a href='#' data-icon='arrow-r'>下一页</a></li>");
			nextWrp.appendTo(this.footerNavWrp);
		
		this.footerNavBar = $("<div data-role='navbar' class='customNav ui-hide'></div>").appendTo(this.footerWrp);
		this.footerNavWrp = $("<ul></ul>").appendTo(this.footerNavBar);
		
		var prevWrp = $("<li class='prev ui-hide'><a href='#' data-icon='arrow-l'>上一页</a></li>");
			prevWrp.appendTo(this.footerNavWrp);
			
		this.footerWrp.on('vclick', '.next,.prev', function() {
			$(this).closest('.customNav').parent().find('.customNav').toggleClass('ui-hide');
		});
		nodeCon.appendTo(this.footerNavWrp);
	} else {
		nodeCon.appendTo(this.footerNavWrp);
	}
};
/**
 * 外部调用刷新卡片页面方法
 */
mb.vi.cardView.prototype.refresh = function() {
	this.contentWrp.empty();
	this._bldCardLayout();
	this._afterLoad();
};
/**
 * 返回
 */
mb.vi.cardView.prototype.back = function() {
	$.mobile.back();
};
/**
 * 卡片加载后执行工程级js方法
 */
mb.vi.cardView.prototype._excuteProjectJS = function() {
	var _self = this;
	this._servPId = this._servData.SERV_PID || ''; // 父服务ID
	var loadArray = this._servData.SERV_CARD_LOAD_NAMES.split(',');
	for (var i = 0; i < loadArray.length; i++) {
		if (loadArray[i] == '') {
			return;
		}
		load(loadArray[i]);
	}
	
	function load(value) {
		var pathFolder = value.split('_');
		var lowerFolder = pathFolder[0].toLowerCase();
		// 这里就直接制定到oa/mobile/servjs目录下了,否则还需要创建别的文件夹
//		var jsFileUrl = FireFly.getContextPath() + '/' + lowerFolder + '/mobile/servjs/' + value + '_card_mb.js';
		var jsFileUrl = FireFly.getContextPath() + '/jqm_test/server/servjs/' + value + '_card_mb.js';
		$.ajax({
			url: jsFileUrl,
			type: 'GET',
			dataType: 'text',
			async: false,
			data: {},
			success: function(data) {
				try {
					var servExt = new Function(data);
						servExt.apply(_self);
				} catch(e) {}
			},
			error: function() {}
		});
	};
};
/**
 * 隐藏指定ID的分组
 * @Param itemCode 分组框ITEM_CODE
 * zhbx这边没有分组框的概念，所以应该用不到
 */
mb.vi.cardView.prototype.hideGroup = function(itemCode) {
	$('#' + this.pkCode + '_' + itemCode).hide();
};
/**
 * 显示指定ID的分组
 * @Param itemCode 分组框ITEM_CODE
 * zhbx这边没有分组框的概念，所以应该用不到
 */
mb.vi.cardView.prototype.showGroup = function(itemCode) {
	$('#' + this.pkCode + '_' + itemCode).show();
};
/**
 * 获取当前卡片页面主键值
 */
mb.vi.cardView.prototype.getPKCode = function() {
	return this.pkCode;
};
/**
 * 获取字段对象的方法
 */
mb.vi.cardView.prototype.getItem = function(itemCode) {
	var _self = this;
	var item = this.form.getItem(itemCode);
	return item;
};
/**
 * 获取字段值
 */
mb.vi.cardView.prototype.itemValue = function(itemCode) {
	var _self = this;
	return this.form.itemValue(itemCode);
};
/**
 * 取得SERV_SRC_ID的值，引用自 服务（共享附件设置）
 */
mb.vi.cardView.prototype.getServSrcId = function() {
	var result = this._servData.SERV_SRC_ID;
	return result || '';
};
/**
 * 获取字段业务数据，修改时获取的是业务数据，添加时获取的是默认值
 */
mb.vi.cardView.prototype.getByIdData = function(itemCode) {
	return this._formData[itemCode] || '';
};
/**
 * 获取字段中的配置
 */
mb.vi.cardView.prototype.getItemConfig = function(itemCode) {
	var _self = this;
	var itemConfig = _self._servData.ITEMS[itemCode].ITEM_INPUT_CONFIG;
	return itemConfig;
};
/**
 * 设置服务数据
 * @param data 服务数据
 */
mb.vi.cardView.prototype.setServData = function(data) {
	if (!this._servData) {
		this._servData = data;
	}
};
/**
 * 获取服务数据
 */
mb.vi.cardView.prototype.getServData = function() {
	return this._servData || undefined;
};
/**
 * 获取服务项数据
 */
mb.vi.cardView.prototype.getServItemValue = function(key) {
	return this._servData ? this._servData[key] : undefined;
};


















