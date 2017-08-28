/**
 * 手机卡片页面的二级子卡片页面渲染引擎
 */
GLOBAL.namespace('mb.vi');
/**
 * 构造方法
 */
mb.vi.cardToCardView = function(options) {
	var defaults = {
			'id': 'cardToCardView',
			'act': 'cardRead',
			'jsFileUrl': ''
	};
	
	this.opts = $.extend(defaults, options);
	
	this.id = this.opts.id; 			// 卡片页ID
	this.servId = this.opts.sId;		// 卡片服务ID
	this.pkCode = this.opts.pkCode;		// 卡片数据主键ID
	this.pId = this.opts.pId;			// 卡片上层父ID
	this.cardAct = this.opts.act;		// 卡片操作
	this.jsFileUrl = this.opts.jsFileUrl; // 扩展JS路径
};
/**
 * 显示卡片页面，主方法
 */
mb.vi.cardToCardView.prototype.show = function() {
	var _self = this;
	
	_self._bldPageLayout(); // 构建页面布局
	_self._afterLoad(); 	// 加载后执行
	
	// 添加加载效果
	$.mobile.loading('show', {
		text: '加载中...',
		textVisible: true,
		textonly: false
	});
	
	_self._initMainData().then(function() {
		// TODO
		_self._bldCardLayout(); // 构建布局
		_self._resetBtn(); 		// 重置按钮
		_self.pageWrp.enhanceWithin(); // 初始化页面
	});
	$.mobile.loading('hide');
	
	// 这段压缩的时候报错
	/*.catch(function(error) {
		// 打印错误
		console.log(error);
	}).finally(function() {
		// 移除加载效果
		$.mobile.loading('hide');
	});*/
};
/**
 * 构建页面布局
 */
mb.vi.cardToCardView.prototype._bldPageLayout = function() {
	var _self = this;
	
	this.pageWrp = $('#' + this.id);
	this.headerWrp = $('#' + this.id + '_header');
	this.contentWrp = $('#' + this.id + '_content');
	this.footerWrp = $('#' + this.id + '_footer');
};
/**
 * 加载后执行
 */
mb.vi.cardToCardView.prototype._afterLoad = function() {
	var _self = this;
	
	this._guideTo(this.pkCode);
};
/**
 * 跳转到该页
 */
mb.vi.cardToCardView.prototype._guideTo = function() {
	var _self = this;
	
	this.pageWrp.page().enhanceWithin();
	$.mobile.pageContainer.pagecontainer('change', this.pageWrp);
};
/**
 * 初始化数据
 * TODO 这里只获取serv和form，因为没有pkCode，等到有pkCode的时候再做特殊处理
 */
mb.vi.cardToCardView.prototype._initMainData = function() {
	var _self = this
	,	param = {};
	
	// 获取服务定义
	var cachedServData = FireFly.cache[this.servId + '-' + FireFly.servMainData];
	if (cachedServData) {
		_self._servData = cachedServData;
	} else {
		param['LOAD_SERV_DATA'] = true;
	}
	
	// 只获取serv/form数据
	console.log('---当前服务ID---' + this.servId + '---当前pkCode应为空---');
	return FireFly.byId4Card(this.servId, this.pkCode, param).then(function(result) {
		_self._data = result;
		console.debug(result);
		// 获取服务定义并塞入缓存
		if (result['serv']) {
			_self._servData = result['serv'];
			FireFly.setCache(_self.servId, FireFly.servMainData, result['serv']);
		}
		// 获取form数据，此时的数据是刚刚被初始化的数据
		_self._formData = result['form'];
		_self._fileData = result["file"] ? result["file"]["_DATA_"] : [];
		_self._linkData = result["link"] ? result["link"]["_DATA_"] : []; // link数据
		_self._mind = result["mind"] || {}; // mind数据
		// TODO 重置pkCode的值
		
		// 添加服务名称
		_self.servName = _self._servData.SERV_NAME || '';
		_self.headerWrp.find('h2').text(_self.servName);
	});
};
/**
 * 构建布局
 */
mb.vi.cardToCardView.prototype._bldCardLayout = function() {
	var _self = this;
	// 构建按钮
	this._bldBtnBar();
	// 构建表单
	this._bldForm();
};
/**
 * 构建按钮
 */
mb.vi.cardToCardView.prototype._bldBtnBar = function() {
	this.btns = {};
	// 每屏按钮数量限制为5个
	this.btnCountLimit = 4;
	this.footerNavBar = $("<div data-role='navbar' class='customNav'></div>").appendTo(this.footerWrp);
	this.footerNavWrp = $("<ul></ul>").appendTo(this.footerNavBar);
};
/**
 * 构建卡片form，调用mb.ui.form组件
 */
mb.vi.cardToCardView.prototype._bldForm = function() {
	var _self = this;
	
	// 1.渲染Form
	var opts = {
			'pId': this.pkCode, // 这里的主键ID是用来给生成的form做ID的
			'sId': this.servId,
			'pkCode': this.pkCode,
			'data': this._servData,
			'parHandler': this,
			'pCon': this.contentWrp
	};
	this.form = new mb.ui.form(opts);
	this.form.render();
	
	// card只读时，填充form数据
	if (this.cardAct == UIConst.ACT_CARD_READ) { // TODO
		this.form.fillData(this._formData, this._fileData, this._linkData);
	}
	
	// 绘制意见列表
	if (this._mind.mindDatas && this._mind.mindDatas.mindList.mindList.length > 0) {
		this._bldMindCon(); // 构建意见框
		this._renderMindList(); // 渲染意见列表
	}
	
	// 加载外部脚本，此处的外部脚本非平台的脚本
	this._excuteProjectJS();
};
/**
 * 如果没有按钮，清楚navbar
 */
mb.vi.cardToCardView.prototype._resetBtn = function() {
	var len = this.footerWrp.find('li').length;
	if (!len) {
		this.footerWrp.empty();
	}
};
/**
 * 返回
 */
mb.vi.cardToCardView.prototype.back = function() {
	$.mobile.back();
};
/**
 * 构建意见框
 */
mb.vi.cardToCardView.prototype._bldMindCon = function() {
	var _self = this;
	
	var targetContainer = _self.form.mainContainer; // form表单的主页面
	var group = $("<div id='"+_self.pkCode+"_MIND' class='mb-card-group'></div>");
	_self.mindFieldCon = $("<div data-role='fieldcontain' data-theme='b'></div>"); // 意见列表Field
	_self.mindFieldCon.append("<label for='mind-list'>意见列表</label>");
		
	group.append(_self.mindFieldCon); // 追加到意见group中
	group.insertAfter(targetContainer.find('.mb-card-group:first'));
};
/**
 * 绘制意见列表
 */
mb.vi.cardToCardView.prototype._renderMindList = function() {
	var _self = this;
	
	var mindList = this._mind.mindDatas.mindList.mindList 		// 意见列表
	,	odeptList = this._mind.mindDatas.mindList.odeptList;	// 机构列表
	var mindListLen = mindList.length || 0						// 意见数
	,	odeptListLen = odeptList.length || 0;					// 机构数
	
	if (odeptListLen > 0) { // 如果有机构列表
		for (var j=0; j<odeptListLen; j++) {
			var $ulCon = _self._renderOdeptBean(odeptList[j], j);
			if (mindListLen > 0 && mindList[0]['S_ODEPT'] == odeptList[j]['DEPT_CODE']) {
				for (var i=0; i<mindListLen; i++) {
					$ulCon.append(_self._renderMindBean(mindList[i]));
				}
				$ulCon.parent().attr('data-collapsed', false);
				$ulCon.listview().listview('refresh');
			} else {
				for (var k=0; k<odeptList[j]['odeptMindList'].length; k++) {
					$ulCon.append(_self._renderMindBean(odeptList[j]['odeptMindList'][k]));
				}
				$ulCon.listview().listview('refresh');
			}
		}
	} else {
		// TODO 没有机构列表时隐藏掉意见列表文字
	}
	
};
/**
 * 渲染不同机构的collapse
 */
mb.vi.cardToCardView.prototype._renderOdeptBean = function(odeptBean, index) {
	var _self = this;
	
	var $odeptColl = $("<div id='" + odeptBean['DEPT_CODE'] + "_list' data-role='collapsible'><h4>" + odeptBean['DEPT_NAME'] + "</h4></div>");
	var $ulCon = $("<ul data-role='listview'></ul>").appendTo($odeptColl);
	
	// 添加空的label，解决pad上页面混乱的问题
	if (index >= 1) {
		$(_self.mindFieldCon).append('<label></label>');
	}
	$(_self.mindFieldCon).append($odeptColl);
	return $ulCon;
};
/**
 * 渲染意见
 */
mb.vi.cardToCardView.prototype._renderMindBean = function(mindBean, isNew) {
	var _self = this;
	
	// 意见列表
	var $mindLi = '';
	if (mindBean) {
		var canDelete = false;
		if (isNew || this.pkCode == mindBean['MIND_ID']) {
			canDelete = true;
		}
		$mindLi = $("<li data-mind-id = '" + mindBean['MIND_ID'] + "'></li>").append("<h5>" + mindBean['WF_NI_NAME'] + "</h5>");
		var $pCon = $("<p></p>").append("<strong>" + mindBean['S_UNAME'] + "</strong>")
					.append("&nbsp;(" + mindBean['S_DNAME'] + ")&nbsp;：" + mindBean['MIND_CONTENT'] + "(" + mindBean['MIND_TIME'] + ")");
		// TODO 缺少删除按钮和附件
		$mindLi.append($pCon);
	}
	return $mindLi;
};
/**
 * 卡片加载后执行工程级js方法
 */
mb.vi.cardToCardView.prototype._excuteProjectJS = function() {
	var _self = this;
	var jsFileUrl = '';
	
	if (this.jsFileUrl == '') {
		this._servPId = this._servData.SERV_PID || ''; // 父服务ID
		var loadArray = this._servData.SERV_CARD_LOAD_NAMES.split(',');
		for (var i=0; i<loadArray.length; i++) {
			if (loadArray[i] == '') {
				return;
			}
			load(loadArray[i]);
		}
		
		function load(value) {
			var pathFolder = value.split('_');
			var lowerFolder = pathFolder[0].toLowerCase();
				jsFileUrl = FireFly.getContextPath() + '/jqm_test/server/servjs/' + value + '_card_mb.js';
			
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
	} else {
		jsFileUrl = FireFly.getContextPath() + '/' + this.jsFileUrl;
		
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
			error: function(error) {
				console.log(error);
			}
		});
	}
};
/*
mb.vi.cardToCardView.prototype._excuteProjectJS = function() {
	var _self = this;
	
	this._servPId = this._servData.SERV_PID || ''; // 父服务ID
	var loadArray = this._servData.SERV_CARD_LOAD_NAMES.split(',');
	for (var i=0; i<loadArray.length; i++) {
		if (loadArray[i] == '') {
			return;
		}
		load(loadArray[i]);
	}
	
	function load(value) {
		var pathFolder = value.split('_');
		var lowerFolder = pathFolder[0].toLowerCase();
		var jsFileUrl = FireFly.getContextPath() + '/' + lowerFolder + '/mobile/servjs/' + value + '_card_mb.js';
		
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
*/
/**
 * 获取当前卡片页面主键值
 */
mb.vi.cardToCardView.prototype.getPKCode = function() {
	return this.pkCode;
};
/**
 * 获取字段对象的方法
 */
mb.vi.cardToCardView.prototype.getItem = function(itemCode) {
	var _self = this;
	var item = this.form.getItem(itemCode);
	return item;
};
/**
 * 获取字段值
 */
mb.vi.cardToCardView.prototype.itemValue = function(itemCode) {
	var _self = this;
	return this.form.itemValue(itemCode);
};
/**
 * 获取字段业务数据，修改时获取的是业务数据，添加时获取的是默认值
 */
mb.vi.cardToCardView.prototype.getByIdData = function(itemCode) {
	return this._formData[itemCode] || '';
};
/**
 * 获取字段中的配置
 */
mb.vi.cardToCardView.prototype.getItemConfig = function(itemCode) {
	var _self = this;
	var itemConfig = _self._servData.ITEMS[itemCode].ITEM_INPUT_CONFIG;
	return itemConfig;
};
/**
 * 设置服务数据
 * @param data 服务数据
 */
mb.vi.cardToCardView.prototype.setServData = function(data) {
	if (!this._servData) {
		this._servData = data;
	}
};
/**
 * 获取服务数据
 */
mb.vi.cardToCardView.prototype.getServData = function() {
	return this._servData || undefined;
};
/**
 * 获取服务项数据
 */
mb.vi.cardToCardView.prototype.getServItemValue = function(key) {
	return this._servData ? this._servData[key] : undefined;
};
