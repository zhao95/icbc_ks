/**
 * 手机卡片页面form组件
 */
GLOBAL.namespace('mb.ui');
mb.ui.form = function(options) {
	var defaults = {
		"id": options.pId + '-card',
		"sId": "",
		"pCon": null,
		"parHandler": null,
		"readOnly": false
	};
	this.opts = $.extend(defaults, options);
	
	this.servId = this.opts.sId;
	this.pkCode = this.opts.pkCode;
	this._pCon = this.opts.pCon;
	this._data = this.opts.data;			// 服务定义信息
	this._items = this._data.ITEMS; 		// 字段定义信息
	
	this.servSrcId = this.opts.data.SERV_SRC_ID; 	// 服务src
	
	this.dicts = this.opts.data.DICTS;
	this.origData = null;
	
	this.lTitle = UIConst.ITEM_MOBILE_LTITLE;				/*1 移动列表标题 */
	this.lItem = UIConst.ITEM_MOBILE_LITEM;					/*2 移动列表项 */
	this.cItem = UIConst.ITEM_MOBILE_CITEM;					/*3 移动卡片项 */
	this.lTime = UIConst.ITEM_MOBILE_LTIME;					/*4列表时间项 */
	this.lImg = UIConst.ITEM_MOBILE_LIMG;					/*5列表图片项 */
	this.cHidden = UIConst.ITEM_MOBILE_CHIDDEN;				/*9 移动卡隐藏项 */
	this.forceHidden = UIConst.ITEM_MOBILE_FORCEHIDDEN;		/*91 移动卡强制隐藏项(忽略流程设置) */
	
	this._uItems = {};
};
/**
 * 显示卡片页面，主方法
 */
mb.ui.form.prototype.render = function() {
	this._bldLayout();
	this._afterLoad();
};
/**
 * 填充数据
 */
mb.ui.form.prototype.fillData = function(formData, fileData, linkData) {
	this.origData = formData;
	$.each(this._uItems, function(i, n) {
		var value = formData[i];
		if (n.setValue) { // 如果对象有setValue这个方法
			if (formData[i + '__NAME']) {
				n.setValue(value, formData[i + '__NAME']);
			} else if (n.type == UIConst.FITEM_ELEMENT_ATTACH) { // 如果是上传文件
				n.setValue(fileData);
			} else if (n.type == UIConst.FITEM_ELEMENT_LINKSELECT) { // 如果是相关文件
				n.setValue(linkData);
			} else {
				n.setValue(value);
			}
		}
	});
};
/**
 * 构建表单，给文件下载绑定事件
 */
mb.ui.form.prototype._bldLayout = function() {
	// 创建卡片页面唯一一个form框
	this.mainContainer = $("<form class='zhbx-form'></form>").appendTo(this._pCon);
	
	// 第一个分组框
	this.group = $("<div id='" + this.pkCode + "_base' class='mb-card-group'></div>").appendTo(this.mainContainer);
	
	this._bldForm(); // 构建表单
	this._bindEvent(); // 绑定事件
};
/**
 * 构建表单
 */
mb.ui.form.prototype._bldForm = function() {
	var _self = this;
	var count = 0;
	$.each(this._items, function(i, obj) {
		var itemName = obj.ITEM_NAME,			// 名称
			inputType = obj.ITEM_INPUT_TYPE, 	// 输入类型
			mbType = obj.ITEM_MOBILE_TYPE, 		// 移动类型
			isHidden = obj.ITEM_HIDDEN;			// 隐藏
		if (mbType && mbType.length > 0) { // 卡片显示：包括卡片项和列表的展示项
			
			if (inputType == UIConst.FITEM_ELEMENT_HR) { // 如果是分组字段
				if (count == 0) {
					// 这里不存在分组框的概念
				} else {
					// 这里不存在分组框的概念
				}
				if (isHidden == 1) { // 隐藏分组框
					// 这里不存在分组框的概念
				}
				
				count++;
				return;
			}
			
			// 渲染各个字段
			var currField = _self._renderField(obj);
			
			if (currField) {
				if ((isHidden == 1 && mbType < _self.cHidden) || 
						(mbType == _self.cHidden) || 
						(mbType == _self.forceHidden)) { // 隐藏字段自动隐藏
					currField.hide();
				}
				currField.appendTo(_self.group);
			}
			count++;
		}
	});
};

/**
 * 异步请求文件
 * android的http底层会重复发送请求,所以改用defer防止此Bug
 */
mb.ui.form.prototype._openFileDef = function(fileId, method) {
	var deferred = Q.defer();
	$.ajax({
		url	: encodeURI(FireFly.getContextPath() + '/OA_MOBILE_FILE_SERV.' + method + '.do')  + '?t=' + Math.random(),
		type: 'POST',
		data: {'fileId': fileId},
		dataType: 'json',
		cache: false,
		timeout: 8000,
		success: function(result) {
			// 如果出现fileId为空时，将返回ERROR信息
			if (result['_MSG_'].indexOf('ERROR,') >= 0) {
				deferred.reject(); // 拒绝
			} else {
				deferred.resolve(result);
			}
		},
		error: function(err) {
			deferred.reject();
		}
	});
	return deferred.promise;
}

/**
 * 绑定事件
 */
mb.ui.form.prototype._bindEvent = function() {
	var _self = this;
	// 将click事件换成长按事件(taphold)，防止用户误操作这种耗时的功能
	this.mainContainer.on('vclick', '.zhbx-file-link', function(event) { // TODO
		event.preventDefault();
		var url = $(this).attr("data-href"),
			fileId = $(this).attr("data-fileId"),
			fileName = $(this).attr("data-fileName"),
			fileType = $(this).attr('data-fileType'),
			fileSize = $(this).attr('data-fileSize');
		
		// 检测文件类型
		if (fileType == 'zip' || fileType == 'rar' || fileType == 'default' ) {
			alert('文件格式不支持，请在电脑端查看！');
			return;
		}
		
		// 检测文件大小
		if (fileSize > 1024*1024*10) {
			alert('文件过大，请在电脑端查看！');
			return;
		}
		if (fileSize > 1024*1024*5 && fileSize <= 1024*1024*10) {
			if (!confirm('文件过大,是否继续用手机浏览?')) {
				return;
			}
		}
		
		$.mobile.loading('show', {
			text: "加载中...",
			textVisible: true,
			textonly: false
		});
		if (fileId) { // 如果fileId不为空，则发送请求
			
			if (fileType == 'gif' || fileType == 'jpg' || fileType == 'png') {
				wx.previewImage({
					current: FireFly.getContextPath() + '/file/' + fileId,
					urls: [FireFly.getContextPath() + '/file/' + fileId]
				});
				$.mobile.loading('hide');
			} else if (fileType == 'pdf') {
				_self._openFileDef(fileId, 'getImageByFileIdForPdf').then(function(result) {
					
					$.mobile.loading('hide');
					var picList = []
					,	pageCount = result['pageCount']
					,	time = result['time'];
					for (var i=0; i<pageCount; i++) {
//						picList.push(FireFly.getContextPath() + '/oa/mobile/file-temp-dir/' + fileId + '/' + i + '.jpg?t=' + Math.random());
						picList.push(FireFly.getContextPath() + "/jqm_test/server/jsp/download.jsp?fileId=" + fileId + "&pageName=" + i + ".jpg&t=" + Math.random());
					}
					wx.previewImage({
						current: picList[0],
						urls: picList
					});
				}, function() {
					$.mobile.loading('hide');
					alert('系统正在处理文件,请稍后再试！');
				});
			} else {
				_self._openFileDef(fileId, 'getImageByFileId').then(function(result) {
					
					$.mobile.loading('hide');
					var picList = []
					,	pageCount = result['pageCount']
					,	time = result['time'];
					for (var i=0; i<pageCount; i++) {
//						picList.push(FireFly.getContextPath() + '/oa/mobile/file-temp-dir/' + fileId + '/' + i + '.jpg?t=' + Math.random());
						picList.push(FireFly.getContextPath() + "/jqm_test/server/jsp/download.jsp?fileId=" + fileId + "&pageName=" + i + ".jpg&t=" + Math.random());
					}
					if (picList.length > 0) {
						wx.previewImage({
							current: picList[0],
							urls: picList
						});
					}
				}, function() {
					$.mobile.loading('hide');
					alert('系统正在处理文件,请稍后再试！');
				});
			}
		}
	});
	
	// 相关文件打开注册事件
	if ($.mobile.activePage.attr('id') == 'cardToCardView') {
		// 如果当前页面是相关文件页面，就不给相关文件绑定点击事件了，否则将造成循环点击的现象
	} else {
		this.mainContainer.on('vclick', '.zhbx-link-link', function(event) {
			event.preventDefault();
			var relateDataId = $(this).attr('data-relateDataId')
			,	relateId = $(this).attr('data-relateId')
			,	relateServId = $(this).attr('data-relateServId')
			,	title = $(this).attr('data-title')
			,	servName = $(this).attr('data-servName');
			
			var data = {};
			data['sId'] = relateServId;
			data['pkCode'] = relateDataId;
			data['pId'] = _self.pkCode;
			data['parHandler'] = _self;
			(function(params) {
				var cardToCardView = new mb.vi.cardToCardView(params);
				cardToCardView.show();
			}(data));
		});
	}
};

mb.ui.form.prototype._renderField = function(item) {
	var _self = this;
	
	var fieldcontain = $("<div data-role='fieldcontain' code='" + item.ITEM_CODE + "' model='" + item.ITEM_MOBILE_TYPE + "'></div>");
	
	var id = item.ITEM_ID;
	var	itemName = item.ITEM_NAME;
	var type = item.ITEM_INPUT_TYPE; // 输入框类型
	var inputMode = item.ITEM_INPUT_MODE; // 输入模式
	var notNull = item.ITEM_NOTNULL; // 必填
	var readOnly = item.ITEM_READONLY; // 只读
	var itemCode = item.ITEM_CODE; 
	
//	console.debug('---itemCode--- : ' + itemCode);
	
	// 构造输入框
	var ui,
		opts = {
			'id': id,
			'item': item,
			'readOnly': readOnly
	};
	switch (type) {
	case UIConst.FITEM_ELEMENT_INPUT : // 输入框
		if (inputMode == UIConst.FITEM_INPUT_QUERY) { // 2查询选择
			
//			ui = new mb.ui.input(opts);
			ui = new mb.ui.div(opts);
			
		} else if (inputMode == UIConst.FITEM_INPUT_DICT) { // 字典
			
			opts['data'] = _self.dicts[item.DICT_ID]; // TODO
			opts['dictId'] = item.DICT_ID;
//			ui = new mb.ui.dict(opts); // TODO
			ui = new mb.ui.div(opts);
			
		} else if (inputMode == UIConst.FITEM_INPUT_AUTO) { // 输入框
			
//			ui = new mb.ui.input(opts);
			ui = new mb.ui.div(opts);
			
		} else if (inputMode == UIConst.FITEM_INPUT_DATE) { // 日期选择
			
			// 日期类型html5:datetime | datetime-local | date
			opts['inputType'] = item.ITEM_INPUT_CONFIG;
//			ui = new mb.ui.DateInput(opts);
			ui = new mb.ui.div(opts);
			// TODO 这个在首创没有，需要做
		}
		break;
		
	case UIConst.FITEM_ELEMENT_RADIO : // 单选框
		opts['data'] = _self.dicts[item.DICT_ID];
		ui = new mb.ui.radioBoxGroup(opts);
		break;
		
	case UIConst.FITEM_ELEMENT_CHECKBOX : // 多选
		if (item.DICT_ID) {
			opts['data'] = _self.dicts[item.DICT_ID];
			ui = new mb.ui.checkBoxGroup(opts);
		} else {
			ui = new mb.ui.input(opts);
		}
		break;
		
	case UIConst.FITEM_ELEMENT_SELECT : // 下拉框
		opts['data'] = _self.dicts[item.DICT_ID];
//		ui = new mb.ui.select(opts);
		ui = new mb.ui.div(opts);
		break;
		
	case UIConst.FITEM_ELEMENT_TEXTAREA : // 大文本
//		ui = new mb.ui.textArea(opts);
		ui = new mb.ui.div(opts);
		break;
		
	case UIConst.FITEM_ELEMENT_FILE : // 文件
		console.log('---case UIConst.FITEM_ELEMENT_FILE : // 文件---');
		ui = new mb.ui.file(opts); // TODO 这个应该就不用了
		break;
		
	case UIConst.FITEM_ELEMENT_ATTACH : // 附件
		opts['servId'] = _self.servId;
		opts['servSrcId'] = _self.servSrcId;
		ui = new mb.ui.attach(opts);
		ui.type = UIConst.FITEM_ELEMENT_ATTACH;
		break;
	case UIConst.FITEM_ELEMENT_LINKSELECT : // 相关文件
		/*
		if (itemType != 3) {//非自定义类别返回
			break;
		}
		var temp = {
			id : _self._getUnId(data.ITEM_CODE),
			name : _self._getUnId(data.ITEM_CODE),
			config : data.ITEM_INPUT_CONFIG,
			cardObj : _self.cardObj,
			servID : this._servID,
			servSrcId:_self._servSrcId,
			itemCode : data.ITEM_CODE,
			isNotNull : isNotNull,
			isReadOnly : isReadOnly,
			parHandler: _self
		};
		ui = new rh.ui.linkSelect(temp);
		break;
		*/
		opts['servId'] = _self.servId;
		opts['servSrcId'] = _self.servSrcId;
		ui = new mb.ui.linkSelect(opts);
		ui.type = UIConst.FITEM_ELEMENT_LINKSELECT;
		break;
		
	case UIConst.FITEM_ELEMENT_IMAGE : // 图片
		// TODO 暂时不用
		console.log('---图片展示功能暂不做---略----');
		break;
		
	case UIConst.FITEM_ELEMENT_PSW : // 密码框
		ui = new mb.ui.pwd(opts); // TODO
		break;
		
	case UIConst.FITEM_ELEMENT_STATICTEXT : // 静态显示文本区
//		ui = new mb.ui.staticText(opts);
		ui = new mb.ui.div(opts);
		break;
		
	case UIConst.FITEM_ELEMENT_DATA_SERVICE : // 嵌入服务
//		ui = new mb.ui.div(opts);
		// 这里直接换成附件字段了
		opts['servId'] = _self.servId;
		opts['servSrcId'] = _self.servSrcId;
		ui = new mb.ui.attach(opts);
		ui.type = UIConst.FITEM_ELEMENT_ATTACH;
		break;
		
	default :
		
	};
	
	if (ui) {
		_self._uItems[itemCode] = ui; // TODO
		
		if (ui.obj.is('fieldset')) { // 主要针对radioBoxGroup和checkBoxGroup
			_self._legend({'id': id, 'text': itemName}).prependTo(ui.obj);
		} else {
			// 构造label
			var label = _self._label({'id': id, 'text': itemName});
			// 对静态文本添加ui-input-text样式类，以对齐form
			if (ui.type == UIConst.FITEM_ELEMENT_STATICTEXT) {
				label.addClass('ui-input-text zhbx-static-label'); // TODO
			}
			
			label.appendTo(fieldcontain);
		}
		
		ui.obj.appendTo(fieldcontain);
	}
	
	// 必填项的支持
	if (notNull == UIConst.STR_YES) {
		$("<span>*</span>").addClass("mbCard-form-notNull mb-icon-notNull").appendTo(label); // TODO
	}
	
	return fieldcontain;
};
/**
 * 构造label
 */
mb.ui.form.prototype._label = function(options) {
	var defaults = {
			'id': '',
			'text': null
	};
	var opts = $.extend(defaults, options);
	var text = opts.text || '';
	text = text.replace(/\&nbsp;/g, '').replace(/\&nbsp/g, '');
	return $("<label></label>").attr("for", opts.id).text(text);
};
/**
 * 针对单选和多选添加fieldset的标题legend
 */
mb.ui.form.prototype._legend = function(options) {
	var defaults = {
			'id': '',
			'text': null
	};
	var opts = $.extend(defaults, options);
	var text = opts.text || '';
	text = text.replace(/\&nbsp;/g, '').replace(/\&nbsp/g, '');
	return $("<legend></legend>").attr("for", opts.id).text(text);
};
/**
 * 获取页面修改后的数据
 */
mb.ui.form.prototype.getModifyData = function() {
	var _self = this;
	var modiData = {};
	if (_self.origData) { // 如果有初始数据
		var origData = _self.origData;
		$.each(_self._uItems, function(i, n) {
			var origValue = origData[i];
			if (n.getValue() == origValue) {
				
			} else { // 如果不与源数据一样，就记录起来
				modiData[i] = n.getValue();
			}
		});
	} else { // 没有初始数据
		$.each(this._uItems, function(i, n) {
			modiData[i] = n.getValue();
		});
	}
	return modiData;
};
/**
 * 通过key获取UI对象，key就是当前UI的Id
 */
mb.ui.form.prototype.getItem = function(itemCode) {
	var _self = this;
	var ui = _self._uItems[itemCode];
	return ui;
};
/**
 * 通过key获取UI对象的值
 */
mb.ui.form.prototype.itemValue = function(itemCode) {
	var _self = this;
	var ui = _self.getItem(itemCode);
	if (ui) {
		return ui.getValue();
	} else {
		return '';
	}
};
/**
 * 渲染结束后执行的动作
 */
mb.ui.form.prototype._afterLoad = function() {

};


//=================普通输入框======================================
mb.ui.input = function(options) {
	var defaults = {
			'id': '',
			'type': 'text'
	};
	var opts = $.extend(defaults, options);
	var item = opts.item;
	var readOnly = opts.readOnly;
	
	this.obj = $("<input type='text' />").attr('id', opts.id).attr('name', opts.id);
//	if (readOnly == '1') {
		this.obj.addClass('ui-disabled');
		this.obj.attr('readonly', true);
//	}
};
mb.ui.input.prototype.setValue = function(value) {
	this.obj.val(value);
};
mb.ui.input.prototype.getValue = function() {
	return this.obj.val();
};
mb.ui.input.prototype.getContainer = function() {
	return this.obj.closest("div");
};


//=================在表单不可修改的需求前提下，所有元素使用div渲染======================================
mb.ui.div = function(options) {
	var defaults = {
			'id': '',
			'type': 'text'
	};
	var opts = $.extend(defaults, options);
	var item = opts.item;
	var readOnly = opts.readOnly;
	this.obj = $("<div class='ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset'><span></span></div>").attr('id', opts.id).attr('name', opts.id);
};
mb.ui.div.prototype.setValue = function(value, name) {
	if (name) { // 查询选择，下拉选择等
		this.obj.children().html(name);
		this.obj.children().data('item-value', value).data('item-text', name);
	} else {
		this.obj.children().html(value);
		this.obj.children().data('item-value', value).data('item-text', value);
	}
};
mb.ui.div.prototype.getValue = function() {
//	return this.obj.children().html();
	return this.obj.children().data('item-value');
};
mb.ui.div.prototype.getText = function() {
	return this.obj.children().data('item-text');
};
mb.ui.div.prototype.getContainer = function() {
	return this.obj.closest('div');
};

//=================大文本框======================================
mb.ui.textArea = function(options) {
	var defaults = {
			'id': ''
	};
	var opts = $.extend(defaults, options);
	var item = opts.item;
	var readOnly = opts.readOnly;
	
	this.obj = $("<textarea></textarea>").attr('id', opts.id).attr('name', opts.id);
//	if (readOnly == '1') {
		this.obj.addClass('ui-disabled');
		this.obj.attr('readonly', true);
//	}
};
mb.ui.textArea.prototype.setValue = function(value) {
	this.obj.val(value);
};
mb.ui.textArea.prototype.getValue = function() {
	return this.obj.val();
};
mb.ui.textArea.prototype.getContainer = function() {
	return this.obj.closest("div");
};


//=================日期输入框======================================

/**
 * 平台日期格式
	 * DATETIME		yyyy-MM-dd HH:mm:ss
	 * DATETIMEH	yyyy-MM-dd HH
	 * DATETIMEHM	yyyy-MM-dd HH:mm
	 * YEAR			yyyy
	 * MONTH		yyyy-MM
	 * TIME			H:mm
 * HTML5日期格式
	 * date				yyyy-MM-dd
	 * datetime			yyyy-MM-dd HH:mm:ss
	 * datetime-local	yyyy-MM-dd HH:mm
 */
mb.ui.DateInput = function(options) {
	var defaults = {
			'id': '',
			'inputType': 'date'
	};
	var opts = $.extend(defaults, options);
	var type = opts.inputType;
	var item = opts.item;
	var readOnly = opts.readOnly;
	
	if (type == 'DATETIMEHM') {
		type = 'datetime-local';
	} else if (type == 'MONTH') {
		type = 'month';
	} else {
		type = 'datetime';
	}
	
	this.obj = $("<input type='" + type + "' />").attr('id', opts.id).attr('name', opts.id);
//	if (readOnly == '1') {
		this.obj.addClass('ui-disabled');
		this.obj.attr('readonly', true);
//	}
};
mb.ui.DateInput.prototype.setValue = function(value) {
	this.obj.val(value);
};
mb.ui.DateInput.prototype.getValue = function() {
	return this.obj.val();
};
mb.ui.DateInput.prototype.getContainer = function() {
	return this.obj.closest("div");
};

//=================单选输入框======================================
mb.ui.radioBoxGroup = function(options) {
	var _self = this;
	var defaults = {
			'id': '',
			'data': null // 字典数据
	};
	var opts = $.extend(defaults, options);
	var dictData = opts.data;
	var readOnly = opts.readOnly;
	this.id = opts.id
	
	var len = dictData.length - 1;
	var group = $("<fieldset data-role='controlgroup' data-type='horizontal' data-mini='true'></fieldset>");
	
	$.each(dictData, function(i, n) {
		var name = _self.id + '-' + n.DICT_ID;
		var id = _self.id + '-' + n.ID;
		var value = n.ITEM_CODE;
		var labelText = n.NAME;
		$("<input />").attr({
				'id': id,
				'name': name,
				'value': value,
				'type': 'radio'
			}).appendTo(group);
		$("<label></label>").attr({'for': id}).html(labelText).appendTo(group);
	});
	this.obj = group;
//	if (readOnly == '1') {
		this.obj.addClass('ui-disabled');
		this.obj.attr('readonly', true);
//	}
};
mb.ui.radioBoxGroup.prototype.setValue = function(value) {
	var id = this.id + '-' + value;
	this.obj.find('#' + id).prop('checked', true); // 是radio选中
};
mb.ui.radioBoxGroup.prototype.getValue = function() {
	var res = this.obj.find(':checked').attr('value');
	return res || '';
};
mb.ui.radioBoxGroup.prototype.getContainer = function() {
	return this.obj.closest("div");
};

//=================多选输入框======================================
mb.ui.checkBoxGroup = function(options) {
	var _self = this;
	var defaults = {
			'id': '',
			'data':	null // 字典数据
	};
	var opts = $.extend(defaults, options);
	var item = opts.item;
	var readOnly = opts.readOnly;
	
	var data = opts.data;
	this.id = opts.id;
	
	var len = data.length - 1;
	var group = $("<fieldset data-role='controlgroup' data-type='horizontal' data-mini='true'></fieldset>");
	$.each(data, function(i, n) {
		var id = n.ID;
		var value = n.ITEM_CODE;
		var unitId = _self.id + '-' + id;
		var labelText = n.NAME;
		
		$("<input />").attr({
				'id': unitId,
				'name': _self.id,
				'value': value,
				'type': 'checkbox'
			}).appendTo(group);
		$("<label></label>").attr({'for': unitId}).html(labelText).appendTo(group);
	});
	this.obj = group;
//	if (readOnly == '1') {
		this.obj.addClass('ui-disabled');
		this.obj.attr('readonly', true);
//	}
};
mb.ui.checkBoxGroup.prototype.setValue = function(value) {
	var _self = this;
	// 对于checkbox，多个字放在一个字段里，比如：10，20,30
	if (value) {
		var arr = value.split(',');
		$.each(arr, function(i, val) {
			var unitId = _self.id + '-' + val;
			_self.obj.find('#' + unitId).attr('CHECKED', 'checked');
		});
	}
};
// 取消选中
mb.ui.checkBoxGroup.prototype.dsetValue = function(value) {
	var unitId = this.id + '-' + value;
	this.obj.find('#' + unitId).removeAttr('CHECKED');
};
mb.ui.checkBoxGroup.prototype.getValue = function() {
	var res = this.obj.find(':checked');
	var resArray = [];
	$.each(res, function(i, n) {
		resArray.push($(n).attr('value'));
	});
	return resArray.join(',');
};
mb.ui.checkBoxGroup.prototype.getText = function() {
	var res = this.obj.find(':checked');
	var resArray = [];
	$.each(res, function(i, n) {
		resArray.push($(n).siblings('label').html());
	});
	return resArray.join(',');
};
mb.ui.checkBoxGroup.prototype.getContainer = function() {
	return this.obj.closest("div");
};

//=================下拉选项框======================================
mb.ui.select = function(options) {
	var _self = this;
	var defaults = {
			'id': '',
			'data': null // 字典数据
	};
	var opts = $.extend(defaults, options);
	var item = opts.item;
	var readOnly = opts.readOnly;
	this._data = opts.data;
	this.id = opts.id;
	
	var name = item.ITEM_NAME;
	this.$select = $("<select id='"+this.id+"'></select>").attr({'data-native-menu': false});
	var optionStr = "<option></option>";
	if (this._data) {
		$.each(this._data, function(i, n) {
			optionStr += "<option value='" + n.ID + "'>" + n.NAME + "</option>";
		});
	}
	this.$select.html(optionStr);
	
	this.obj = this.$select;
//	if (readOnly == '1') {
		this.obj.addClass('ui-disabled');
		this.obj.attr('readonly', true);
//	}
};
mb.ui.select.prototype.setValue = function(value, text) {
	var _self = this;
	if ($.trim(text)) {
		_self.$select.find("option[value='" + value + "']").attr("selected", true);
	}
};
mb.ui.select.prototype.getValue = function() {
	var _self = this;
	return _self.$select.find("option:selected").val();
};
mb.ui.select.prototype.getText = function() {
	var _self = this;
	return _self.$select.find("option:selected").text();
};
mb.ui.select.prototype.setActive = function() {
	var _self = this;
	// 没有做什么操作
};
mb.ui.select.prototype.getContainer = function() {
	return this.obj.closest("div");
};

//=================文件查看======================================
mb.ui.file = function(options) {
	var _self = this;
	var defaults = {
			'id': '',
			'data':	null // 字典数据
	};
	var opts = $.extend(defaults, options);
	var item = opts.item;
	var readOnly = opts.readOnly;
	this._data = opts.data;
	this.id = opts.id;
	
	var name = item.ITEM_NAME;
	var group = $("<ul data-role='listview' data-inset='true'></ul>");
//	this.a = $("<li><a href='#' data-ajax='false'></a></li>").appendTo(group);
	this.a = $("<li></li>").appendTo(group); // 去掉里面的a标签，使用li标签
	
	this.obj = group;
//	if (readOnly == '1') {
		this.obj.addClass("ui-disabled");
//	}
};
mb.ui.file.prototype.setValue = function(value) {
	var _self = this;
	if (value) {
		var array = value.split(';');
		for (var i = 0; i < array.length; i++) {
			if (array[i].length > 0) {
				var fileValue = array[i].split(',');
				var href = FireFlyContextPath + "/file/" + fileValue[0] + "?mobile=1";
//				_self.a.attr("href", href);
//				_self.a.text(fileValue[1]);
				_self.a.attr('data-href', href);
				_self.a.text(fileValue[1]);
			}
		}
	}
};
mb.ui.file.prototype.getValue = function() {
	var _self = this;
	return _self.a.text();
};
mb.ui.file.prototype.getContainer = function() {
	return this.obj.closest("div");
};

//=================自定义文件查看======================================
mb.ui.attach = function(options) {
	var _self = this;
	var defaults = {
		'id': '',
		'data': null, // 字典数据
		'servId': ''
	};
	this.opts = $.extend(defaults, options);
	var item = this.opts.item;
	var readOnly = this.opts.readOnly;
	this._data = this.opts.data;
	this.id = this.opts.id;
	this.itemCode = item.ITEM_CODE;
	this.servId = this.opts.servId;
	this.servSrcId = this.opts.servSrcId;
	
	var name = item.ITEM_NAME;
	var group = $("<ul data-role='listview' data-inset='true'></ul>");
	
	this.obj = group;
//	if (readOnly == '1') {
//		this.obj.addClass("ui-disabled");
//	}
};
mb.ui.attach.prototype.setValue = function(fileData) {
	var _self = this;
	
	var hasFlag = false; // 是否有文件数据
	// 载入附件
	for (var i = 0; i < fileData.length; i++) {
		var item = fileData[i];
//		console.debug(item);
		var fileSize = item['FILE_SIZE']
		,	fileMtype = item['FILE_NAME'];
		var fileSizeH = fileSize == '' ? '' : (fileSize/1024 > 1024 ? (fileSize/1024/1024 > 1024 ? parseInt(fileSize/1024/1024/1024) + 'GB' : parseInt(fileSize/1024/1024) + 'MB') : parseInt(fileSize/1024) + 'KB');
		var fileMtypeH = 'default';
		if (fileMtype) { // 如果有fileName
			var switchStr = fileMtype.substring(fileMtype.lastIndexOf('.') + 1, fileMtype.length + 1);
			switchStr = switchStr.toUpperCase();
			
			switch(switchStr) {
				case 'DOCX':
				case 'DOC':
					fileMtypeH = 'doc';break;
				case 'XLSX':
				case 'XLS':
					fileMtypeH = 'xls';break;
				case 'PPTX':
				case 'PPT':
					fileMtypeH = 'ppt';break;
				case 'PDF':
					fileMtypeH = 'pdf';break;
				case 'TXT':
					fileMtypeH = 'txt';break;
				case 'GIF':
					fileMtypeH = 'gif';break;
				case 'JPG':
					fileMtypeH = 'jpg';break;
				case 'PNG':
					fileMtypeH = 'png';break;
				case 'ZIP':
					fileMtypeH = 'zip';break;
				case 'RAR':
					fileMtypeH = 'rar';break;
			}
			
		}
		if (item["FILE_CAT"] == this.itemCode) {
			hasFlag = true; // 有文件数据
			$(
					"<li class='zhbx-file-link' style='color:#5C5CE8;' data-href='" + FireFlyContextPath + "/file/"
							+ item.FILE_ID + "' data-fileId='"
							+ item['FILE_ID'] + "' data-fileName='" + item['DIS_NAME'] + "' data-fileType='" + fileMtypeH + "' data-fileSize='" + fileSize + "'>" 
							+ "<img src='/jqm_test/server/css/customImages/file-icon/file-icon-" + fileMtypeH + ".png' class='ui-li-icon' />" 
							+ "<span style='font-size:.5em;color:#000;padding-right:.5em;'>(" + fileSizeH + ")</span>" + item['DIS_NAME']
							+ "</li>").appendTo(this.obj);
		}
	}
	
	// 如果没有文件数据，添加'无文件'字样
	if (!hasFlag) {
		$("<li>无文件</li>").appendTo(this.obj);
	}
};
mb.ui.attach.prototype.getValue = function() {
	var _self = this;
	return "";
};
mb.ui.attach.prototype.getContainer = function() {
	return this.obj.closest("div");
};

//=================自定义相关文件查看======================================
mb.ui.linkSelect = function(options) {
	var _self = this;
	/*
	this.type = 'LinkSelect'; // 组件类型
	var opts = {
			'id': '',
			'name': '',
			'sId': '',
			'cardObj': null,
			'isReadOnly': false,
			'cls': 'ui-linkselect-default'
	};
	this.opts = $.extend(opts, options);
	this._itemCode = this.opts.itemCode; // 服务的itemCode
	this._actTable = 'SY_SERV_RELATE'; // 操作表名
	this.srcServId = this.opts.servSrcId; // 服务的srcId
	this.cardObj = this.opts.cardObj; // 卡片句柄
	this._val = '';
	this._configs = this.opts.config; // 输入设置
	this.sId = this.opts.servID; // 服务code
	this.pkCode = this.cardObj.getPKCode(); // 卡片主键
	this.itemCode = this.opts.ITEM_CODE; // 字段编码
	this.parHandler = this.opts.parHandler; // form句柄
	this._isReadOnly = this.opts.isReadOnly;
	this._build();
	*/
	var defaults = {
			'id': '',
			'data': null,
			'servId': ''
	};
	this.opts = $.extend(defaults, options);
	var item = this.opts.item;
	var readOnly = this.opts.readOnly;
	this._data = this.opts.data;
	this.id = this.opts.id;
	this.itemCode = item.ITEM_CODE;
	this.servId = this.opts.servId;
	this.servSrcId = this.opts.servSrcId;
	
	var name = item.ITEM_NAME;
	var group = $("<ul data-role='listview' data-inset='true'></ul>");
	this.obj = group;
};
mb.ui.linkSelect.prototype.setValue = function(linkData) {
	var _self = this;
	
	var hasFlag = false; // 是否有文件
	var tempJson = {}; // 用来排重的临时json
	// 载入相关文件
	for (var i=0; i<linkData.length; i++) {
		var item = linkData[i];
		var pk = item['_PK_']; // 相关文件主键
		if (!tempJson[pk]) { // 如果临时json中没有相同的主键，就执行操作
			hasFlag = true;
			tempJson[pk] = item;
			console.debug(item);
			$("<li class='zhbx-link-link' style='color:#5c5c5c;' data-relateDataId='" + item['RELATE_DATA_ID'] + 
					"' data-relateId='" + item['RELATE_ID'] + 
					"' data-relateServId='" + item['RELATE_SERV_ID'] + 
					"' data-title='" + item['TITLE'] + 
					"' data-servName='" + item['SERV_NAME'] + "'>" + item['TITLE'] +
					"</li>").appendTo(this.obj);
		}
	}
	
	if (!hasFlag) {
		$('<li>无文件</li>').appendTo(this.obj);
	}
};
mb.ui.linkSelect.prototype.getValue = function() {
	var _self = this;
	return '';
};
mb.ui.linkSelect.prototype.getContainer = function() {
	return this.obj.closest('div');
};

//=================图片显示======================================
mb.ui.img = function(options) {
	// TODO 略
	console.log('---图片显示---略----');
};

//=================密码框======================================
mb.ui.pwd = function(options) {
	// TODO 略
	console.log('---密码框显示---略----');
};

//=================字典选择======================================TODO:异步加载的情况
/**
 * 字典单独弹出一页面，用anchor模拟
 * 故需在label和a上添加ui-select样式类，以对齐form
 */
mb.ui.dict = function(options) {
	var _self = this;
	var defaults = {
		'id': '',
		'dictId': '',
		'data': null // 字典数据
	};
	var opts = $.extend(defaults, options);
	var item = opts.item;
	this._data = opts.data;
	this.id = opts.id;
	this.type = 'dict';
	this.dictId = opts.dictId;
	this.readOnly = opts.readOnly;
	var name = item.ITEM_NAME;
	
	var targetUrl = "stdSelectListView-mb.jsp?dictId=" + this.dictId; // TODO
	
	/**
	 * TODO
	 * 字典的渲染是在前期需求去掉保存按钮后开发的，所以跟首创的就一样了
	 */
	
	this.text = $("<input type='text' id='" + this.id + "' class='ui-disabled' />");
//	this.text = $("<a href='" + _self.dictId + "' data-role='button' data-rel='dialog' data-transition='slideup' data-icon='arrow-d' data-iconpos='right'></a>");
	
	/*this.text.on('click', function() {
		$(this).attr("href", "#" + _self.dictId);
		var len = $("#" + _self.dictId).length;
		if (len == 0) {
			var dialogTempl = "<div data-role='page' id='" + _self.dictId + "'>" +
								"<div data-role='header' id='" + _self.dictId + "_header'>" +
								"<h1>" + name + "</h1>" +
								"</div>" +
								"<div data-role='content' id='" + _self.dictId + "_content'></div>" +
								"</div>";
			$("body").append(dialogTempl);
			// TODO 这里没有写完，肯定得把这个解决掉，在首创没有做过处理
		}
		
		var temp = {"dictId": _self.dictId, "parHandler": _self};
		var selectView = new mb.vi.selectList(temp);
		selectView.show();
	});*/
	
//	if (this.readOnly == '1') { // 只读
		this.text.addClass('ui-disabled');
		this.text.attr('readonly', true);
//	}
	
	/*this.input = $("<input type='hidden' />");
	this.input.after(this.text);*/
	
	this.obj = this.text;
};
mb.ui.dict.prototype.setValue = function(value, text) {
	var _self = this;
	var i = 0;
	if (text) {
//		_self.input.val(value);
		_self.text.val(text);
//		if (_self.text.find(".ui-btn-text").length > 0) {
//			_self.text.find(".ui-btn-text").html(text);
//		} else {
//			_self.text.html(text);
//		}
	} else {
//		_self.pickNode(value, _self._data);
	}
};
mb.ui.dict.prototype.pickNode = function(id, data) {
	var _self = this;
	var len = data ? data.length : 0;
	for (var i=0; i<len; i++) { // 遍历查找NAME
		if (id == data[i].ID) {
			_self.input.val(id);
			if (_self.text.find(".ui-btn-text").length > 0) {
				_self.text.find(".ui-btn-text").html(data[i].NAME);
			} else {
				_self.text.html(data[i].NAME);
			}
			return true; // 找到了
		} else if (data[i].CHILD) {
			var ret = _self.pickNode(id, data[i].CHILD);
			if (ret) { // 找到了则直接返回，不进行余下的循环
				return true;
			}
		}
	}
	return false; // 没有找到
};
mb.ui.dict.prototype.getValue = function()	{
	var _self = this;
	return _self.input.val();
};
mb.ui.dict.prototype.getText = function() {
	var _self = this;
	return _self.text.val();
};
mb.ui.dict.prototype.setActive = function() {
	var _self = this;
};
mb.ui.dict.prototype.getContainer = function() {
	return this.obj.closest('li');
};

//=================静态文本======================================
mb.ui.staticText = function(options) {
	var _self = this;
	var defaults = {
		'id': ''	
	};
	var opts = $.extend(defaults, options);
	this.item = opts.item;
	this.id = opts.id;
	var name = this.item.ITEM_NAME;
	this.type = this.item.ITEM_INPUT_TYPE;
	this.obj = $("<div class='ui-input-text'></div>");
	this.text = $("<span></span>").attr("id", opts.id).appendTo(this.obj);
};
mb.ui.staticText.prototype.setValue = function(value) {
	this.text.html(this.item.ITEM_INPUT_CONFIG ? this.item.ITEM_INPUT_CONFIG : this.item.ITEM_INPUT_DEFAULT);
};
mb.ui.staticText.prototype.getValue = function() {
	var _self = this;
	return this.text.html();
};
mb.ui.staticText.prototype.getContainer = function() {
	return this.obj.closest("div");
};












