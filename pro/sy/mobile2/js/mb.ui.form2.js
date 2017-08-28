/**
 * 手机卡片页面form组件
 */
GLOBAL.namespace('mb.ui');
mb.ui.form2 = function(options) {
	var defaults = {
		'id': options.pId + '-card',
		'sId': '',
		'pId': 'pkCode',
		'pCon': null,
		'parHandler': null,
		'readOnly': false
	};
	this.opts = $.extend(defaults, options);
	
	this.servId = this.opts.sId;
	this.pkCode = this.opts.pId;
	this._pCon = this.opts.pCon;
	this._data = this.opts.data;	// 服务定义信息
	this._items = this._data.ITEMS; // 字段定义信息
	this.cardRead = this.opts.readOnly;
	
//	this.servSrcId = this.opts.data.SERV_SRC_ID; TODO 这个不知道有什么用,用到时候再说
	
	this.dicts = this.opts.data.DICTS;
	this.origData = null;			// 原始数据信息
	
	this.lTitle = UIConst.ITEM_MOBILE_LTITLE;			// 1. 移动列表标题
	this.lItem = UIConst.ITEM_MOBILE_LITEM;				// 2. 移动列表项
	this.cItem = UIConst.ITEM_MOBILE_CITEM;				// 3. 移动卡片项
	this.lTime = UIConst.ITEM_MOBILE_LTIME;				// 4. 列表时间项
	this.lImg = UIConst.ITEM_MOBILE_LIMG;				// 5. 列表图片项
	this.cHidden = UIConst.ITEM_MOBILE_CHIDDEN;			// 9. 移动卡片隐藏项
	this.forceHidden = UIConst.ITEM_MOBILE_FORCEHIDDEN;	// 91.移动卡片强制隐藏项(忽略流程设置)
	
	this._uItems = {};
	this._validations = {}; // 校验信息
	//存储只读表达式
	this._expNotNullItems = {};
	//存储必填表达式
	this._expReadItems = {};
	//存储隐藏表达式
	this._expHiddenItems = {};
};
/**
 * 显示卡片页面,主方法
 */
mb.ui.form2.prototype.render = function() {
	this._bldLayout();
	this._afterLoad();
	this.doExpression();
};
/**
 * 构建表单,给文件下载绑定事件
 */
mb.ui.form2.prototype._bldLayout = function() {
	// 给页面添加一个头
//	this._pCon.append("<div class='ui-form-head'>" + this.opts.parHandler.servName + "</div><div class='mind-divider'></div>");
	
	// 创建卡片页面唯一一个form框
	this.mainContainer = $("<form class='zhbx-form' style='padding-bottom:3.2em;'></form>").appendTo(this._pCon);
	
	// 添加一个头
	this.mainContainer.append("<div class='ui-form-head'>" + this.opts.parHandler.servName + "</div><div class='mind-divider'></div>");
	
	// 基础分组框
	this.group = $("<div id='" + this.pkCode + "_base' class='mb-card-group'></div>").appendTo(this.mainContainer);
	
	this._bldForm(); 			// 构建表单
	this._bindEvent(); 			// 绑定事件 TODO 这个不应该放到这个文件中吧
};
/**
 * 构建表单
 */
mb.ui.form2.prototype._bldForm = function() {
	var _self = this;
	var count = 0;
	$.each(this._items, function(i, obj) {
		
		_self._initValidate(obj);
		
		var itemName = obj.ITEM_NAME,			// 名称
			inputType = obj.ITEM_INPUT_TYPE,	// 输入类型
			mbType = obj.ITEM_MOBILE_TYPE,		// 移动类型
			isHidden = obj.ITEM_HIDDEN;			// 隐藏字段中的值
		if (mbType && mbType.length > 0) {	// 卡片显示,包括卡片项和列表的展示项
			if (inputType == UIConst.FITEM_ELEMENT_HR) {	// 如果是分组字段
				// 这里不存在分组字段
				count++;
				return;
			}
		}
		
		// 渲染各个字段
		var currField = _self._renderField(obj);
		
		if (currField) {
			// 如果字段配置隐藏,或者强制隐藏,或者移动类型为隐藏
			if ((isHidden == 1 && mbType < _self.cHidden) || 
					(mbType == _self.cHidden) || 
					(mbType == _self.forceHidden)) { // 隐藏字段自动隐藏
				currField.hide();
			}
			currField.appendTo(_self.group);
		}
		count++;
	});
};
/**
 * 渲染各个字段
 */
mb.ui.form2.prototype._renderField = function(item) {
	var _self = this;
	
	var fieldcontain = $("<div data-role='fieldcontain' code='" + item.ITEM_CODE + "' model='" + item.ITEM_MOBILE_TYPE + "' class='ui-field-contain'></div>");
	
	var id = item.ITEM_ID;						// 字段ID
	var itemName = item.ITEM_NAME;
	var type = item.ITEM_INPUT_TYPE; 			// 输入框类型
	var inputMode = item.ITEM_INPUT_MODE; 		// 输入模式
	var notNull = item.ITEM_NOTNULL;			// 必填
	var readOnly = item.ITEM_READONLY;			// 只读
	var itemCode = item.ITEM_CODE;
	var itemCols = item.ITEM_CARD_COLS;			// 卡片列数
	
	// 暂不支持:超大文本,文件上传,图片上传,意见,评论,iframe,分组框,嵌入服务
	var ui, 
		opts = {
			'id': id,				// 字段ID
			'item': item,			// 字段所有定义
			'readOnly': readOnly,	// 只读选项
			'itemCode': itemCode,	// 字段编码
			'itemName': itemName,	// 字段名称
			'itemCols': itemCols 	// 字段列数
	};
	switch(type) {
	case UIConst.FITEM_ELEMENT_INPUT : // 输入框
		if (inputMode == UIConst.FITEM_INPUT_QUERY) { // 查询选择
			if (_self.cardRead) {
				ui = new mb.ui.div(opts);
			} else {
				ui = new mb.ui.input(opts);
			}
		} else if (inputMode == UIConst.FITEM_INPUT_DICT) { // 字典
			if (_self.cardRead) {
				ui = new mb.ui.div(opts);
			} else {
				ui = new mb.ui.tree(opts);
			}
		} else if (inputMode == UIConst.FITEM_INPUT_AUTO) { // 输入框
			if (_self.cardRead) {
				ui = new mb.ui.div(opts);
			} else {
				ui = new mb.ui.input(opts);
			}
		} else if (inputMode == UIConst.FITEM_INPUT_DATE) { // 日期选择
			if (_self.cardRead) {
				ui = new mb.ui.div(opts);
			} else {
				ui = new mb.ui.date(opts);
			}
		}
		break;
	case UIConst.FITEM_ELEMENT_STATICTEXT : // 静态显示文本区
		if (_self.cardRead) {
			ui = new mb.ui.div(opts);
		} else {
			ui = new mb.ui.input(opts);
		}
		break;
	case UIConst.FITEM_ELEMENT_TEXTAREA : // 大文本
		opts.type = UIConst.FITEM_ELEMENT_TEXTAREA;
		if (_self.cardRead) {
			ui = new mb.ui.div(opts);
		} else {
			ui = new mb.ui.input(opts);
		}
		break;
	case UIConst.FITEM_ELEMENT_PSW : // 密码框
		opts['password'] = true;
		if (_self.cardRead) {
			ui = new mb.ui.div(opts);
		} else {
			ui = new mb.ui.input(opts);
		}
		break;
	case UIConst.FITEM_ELEMENT_RADIO : // 单选框
		opts['data'] = _self.dicts[item.DICT_ID]; // 单选框字典ID
		if (_self.cardRead) {
			ui = new mb.ui.div(opts);
		} else {
			ui = new mb.ui.selectMenu(opts);
		}
		break;
	case UIConst.FITEM_ELEMENT_CHECKBOX : // 多选
		opts['data'] = _self.dicts[item.DICT_ID];
		opts['multiple'] = true;
		if (_self.cardRead) {
			ui = new mb.ui.div(opts);
		} else {
			ui = new mb.ui.selectMenu(opts);
		}
		break;
	case UIConst.FITEM_ELEMENT_SELECT : // 下拉框
		opts['data'] = _self.dicts[item.DICT_ID]; // 下拉框字典ID
		if (_self.cardRead) {
			ui = new mb.ui.div(opts);
		} else {
			ui = new mb.ui.selectMenu(opts);
		}
		break;
	case UIConst.FITEM_ELEMENT_FILE : // 文件
		opts.type = UIConst.FITEM_ELEMENT_FILE;
		opts.form = _self;
		ui = new mb.ui.attach(opts);
		break;
	case UIConst.FITEM_ELEMENT_ATTACH : // 附件
		opts.type = UIConst.FITEM_ELEMENT_ATTACH;
		opts.form = _self;
		ui = new mb.ui.attach(opts);
		break;
	case UIConst.FITEM_ELEMENT_LINKSELECT : // 相关文件
		break;
//	case UIConst.FITEM_ELEMENT_IMAGE : // 图片
//		break;
//	case UIConst.FITEM_ELEMENT_DATA_SERVICE : // 嵌入服务
//		break;
	default: 
	};
	
	if (ui) {
		_self._uItems[itemCode] = ui; // item ui 的集合
		
		// 构造label
		var label = _self._label({'id': id, 'text': itemName});
		label.appendTo(fieldcontain);
		ui.obj.appendTo(fieldcontain);
	}
	// 必填项的支持
	if (notNull == UIConst.STR_YES) {
		$("<span>*</span>").addClass("mbCard-form-notNull mb-icon-notNull").appendTo(label);
	}
	
	// 存储必填表达式
	if ($.trim(item.ITEM_NOTNULL_SCRIPT).length > 0) {
		this._expNotNullItems[item.ITEM_CODE] = item.ITEM_NOTNULL_SCRIPT;
	}
	
	// 存储只读表达式
	if ($.trim(item.ITEM_READONLY_SCRIPT).length > 0) {
		this._expReadItems[item.ITEM_CODE] = item.ITEM_READONLY_SCRIPT;
	}
	
	// 存储隐藏表达式
	if ($.trim(item.ITEM_HIDDEN_SCRIPT).length > 0) {
		this._expHiddenItems[item.ITEM_CODE] = item.ITEM_HIDDEN_SCRIPT;
	}
	
	return fieldcontain;
};
/**
 * 构造label
 */
mb.ui.form2.prototype._label = function(options) {
	var defaults = {};
	var opts = $.extend(defaults, options);
	var text = opts.text || '';
	text = text.replace(/\&nbsp;/g, '').replace(/\&nbsp;/g, '');
	return $("<label></label>").attr('for', opts.id).text(text);
};
/**
 * 填充数据
 * 表单数据,文件附件数据,相关文件数据
 */
mb.ui.form2.prototype.fillData = function(formData, fileData, linkData) {
	this.origData = formData; // 原始数据存放
	$.each(this._uItems, function(i, n) {
		var value = formData[i];
		if (n.setValue) { // 如果对象有setValue方法
			if (formData[i + '__NAME']) {
				n.setValue(value, formData[i + '__NAME']); // 就值和中文添加进去
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
 * 加载后执行
 */
mb.ui.form2.prototype._afterLoad = function() {
	var _self = this;
	
	// TODO 只读也可以在这里控制
};
/**
 * 获取form的所有字段的值集合,拼接成json对象
 */
mb.ui.form2.prototype.getItemsValues = function() {
	var _self = this;
	
	var jsonObj = {};
	var tmpData = _self._data;
	for (var key in _self._uItems) { 	// 遍历所有表单域
		var ui = _self._uItems[key];
		var curVal = ui.getValue(); 	//	当前值
		if (!curVal) { 					// 如果curVal为空或undefined
			curVal = '';
		}
		jsonObj[key] = curVal;
	}
	return jsonObj;
};
/**
 * 获取修改了的item,拼接成json对象
 */
mb.ui.form2.prototype.getChangedItems = function() {
	var _self = this;
	
	var jsonObj = {};
	var tmpData = _self.origData;
	var uis = _self.getAllItems(); // 获取所有表单域
	for (var key in uis) { // 遍历所有表单域
		var ui = uis[key];
		if (ui.type == 'File' || ui.type == 'StaticText') { // Attach类型附件或者静态文本不通过Form提交
			continue;
		}
		var theVal = tmpData[key]; 	// 原始值
		var curVal = ui.getValue(); // 当前值
		if (theVal == undefined) {
			if (curVal) {
				// 如果ui返回对象,直接放入jsonObj中
				if (typeof(curVal) == 'object') {
					jsonObj = $.extend(jsonObj, curVal);
					continue;
				}
				jsonObj[key] = curVal;
			}
		} else if (theVal != curVal) { // 如果表单有变化,且不是从undefined变化而来的
			if (!curVal) { // 如果curVal为空或undefined
				curVal = '';
			}
			jsonObj[key] = curVal;
		}
	}
	return jsonObj;
};
/**
 * 获取所有表单值
 */
mb.ui.form2.prototype.getAllItemsValue = function() {
	var _self = this;
	
	var jsonObj = {};
	var items = _self.getAllItems();
	for (var i in items) { // 遍历所有表单域
		var item = items[i];
		var curVal = item.getValue(); // 当前值
		if (!curVal) { // 如果curVal为空或undefined
			curVal = '';
		}
		jsonObj[i] = curVal;
	}
	return jsonObj;
};
/**
 * 获取所有表单域
 */
mb.ui.form2.prototype.getAllItems = function() {
	var _self = this;
	return _self._uItems;
};
/**
 * 获取字段的值
 */
mb.ui.form2.prototype.itemValue = function(itemCode) {
	var _self = this;
	
	var ui = _self.getItem(itemCode);
	if (ui) {
		return ui.getValue();
	} else if (_self.origData[itemCode]) {
		return _self.origData[itemCode];
	} else {
		return '';
	}
};
/**
 * 获取已修改数据
 */
mb.ui.form2.prototype.getModifyData = function() {
	var _self = this;
	
	var modiData = {};
	if (_self.origData) { // 有初始数据
		var origData = _self.origData;
		$.each(_self._uItems, function(i, ui) {
			var origValue = origData[i];
			if (origValue != undefined) {
				if (ui.getValue() !== origValue) {
					modiData[i] = ui.getValue();
				}
			}
		});
	} else {
		$.each(_self._uItems, function(i, ui) {
			modiData[i] = ui.getValue();
		});
	}
	return modiData;
};
/**
 * 获取字段ui对象
 */
mb.ui.form2.prototype.getItem = function(itemCode) {
	var _self = this;
	
	var ui = _self._uItems[itemCode];
	return ui;
};
mb.ui.form2.prototype.validate = function() {
	var _self = this;
	
	var success = this;
	/**
	 * 校验处理函数
	 */
	function _valid(item, itemCode, errorMsg) {
		if (item && item.obj) {
			var validate = $("#" + itemCode + "-validate", item.obj.closest("[data-role='fieldcontain']"));
			if (validate.length == 0) {
				item.addError(errorMsg);
			}
		}
		success = false;
	};
	
	$.each(_self._validations, function(itemCode, rules) {
		console.log(itemCode);
		var item = _self.getItem(itemCode);
		if (item && item.getValue) {
			var value = item.getValue();
			var len = rules.length;
			for (var i = 0; i < len; i++) { // 挨个校验,直到第一个校验失败为止
				var regular = rules[i]['regular'];
				var errorMsg = rules[i]['errorMsg'];
				if (regular == "^[\s]{0,}$") { // 非空校验
					if (RegExp(regular).test(value)) {
						item.removeError();
						_valid(item, itemCode, errorMsg);
						return;
					} else {
						item.removeError();
					}
				} else {
					if (!RegExp(regular).test(value)) {
						item.removeError();
						_valid(item, itemCode, errorMsg);
						return;
					} else {
						item.removeError();
					}
				}
			}
		}
	});
	return success;
};
/**
 * 初始化校验
 * 收集校验规则: {itemCode:[{'regular' : '^\S+$', 'message' : '该项必须输入!'}]}
 */
mb.ui.form2.prototype._initValidate = function(data) {
	var _self = this;
	
	if (data.ITEM_HIDDEN == UIConst.NO) { // 没有隐藏的字段才做校验
		var rules = []; // 校验规则集, 可能存在多重校验
		if (data.ITEM_NOTNULL == UIConst.YES) { // 非空
			rules.push({'regular' : '^[\s]{0,}$', 'errorMsg' : '该项必须输入!'});
		}
		
		var type = data.ITEM_INPUT_TYPE;
		var fieldType = data.ITEM_FIELD_TYPE;
		var length = data.ITEM_FIELD_LENGTH;
		var inputMode = data.ITEM_INPUT_MODE;
		// 用户手动输入的才需要校验
		if ((type == UIConst.FITEM_ELEMENT_INPUT || type == UIConst.FITEM_ELEMENT_TEXTAREA)
				&& inputMode == UIConst.FITEM_INPUT_AUTO) {
			if (fieldType == UIConst.DATA_TYPE_NUM) { // 数字
				var rule = {};
				if (length.indexOf(',') > 0) { // 小数
					var intLength = length.substring(0, length.indexOf(','));
					var decLength = length.substring(length.indexOf(',') + 1);
					rule['regular'] = "^(0|[-+]?[0-9]{1," + (intLength - parseInt(decLength)) + "}([\.][0-9]{0," + decLength + "})?)$";
					rule['errorMsg'] = "请输入整数长度不超过" + (intLength - parseInt(decLength)) + "位,小数长度不超过" + decLength	 + "位的有效数字!";
				} else {
					rule['regular'] = "^(0|[-+]?[0-9]{0," + length + "})$";
					rule['errorMsg'] = "请输入长度不超过" + length + "位有效数字!";
				}
				rules.push(rule);
			} else if (fieldType == UIConst.DATA_TYPE_STR			// 字符串
					|| fieldType == UIConst.DATA_TYPE_BIGTEXT) {	// 大文本
				var regular = "^([\\S\\s]{0," + length + "})$";
				var message = "长度不能超过" + length + "位!";
				rules.push({'regular' : regular, 'errorMsg' : message});
			}
			
			// 正则表达式
			var regular = data.ITEM_VALIDATE;
			// 正则校验失败提示语
			var hint = data.ITEM_VALIDATE_HINT;
			if (regular && $.trim(regular) != '') { // 正则校验
				rules.push({'regular' : regular, 'errorMsg' : (hint ? hint : '')});
			}
		}
		
		if (!$.isEmptyObject(rules)) {
			_self._validations[data.ITEM_CODE] = rules;
		}
	}
};
mb.ui.form2.prototype.setNotNull = function(itemCode, bool) {
	var rules = this._validations[itemCode];
	var item = this.getItem(itemCode);
	if (bool) {
		if ($.isEmptyObject(rules)) { // 该字段不存在任何校验
			rules = [];
			rules.push({'regular' : '^[\s]{0,}$', 'errorMsg' : '该项必须输入!'});
			this._validations[itemCode] = rules;
		} else {
			var len = rules.length;
			var must = false;
			for (var i = 0; i < len; i++) { // 遍历查找是否已经存在必须输入校验
				var rule = rules[i];
				if (rule['regular'] == '^[\s]{0,}$') {
					must = true;
				}
			}
			if (!must) {
				rules.push({'regular' : '^[\s]{0,}$', 'errorMsg' : '该项必须输入!'});
			}
		}
		if (item) {
			console.log('添加必填星号');
			if (item.obj.closest("[data-role='fieldcontain']").find('label').find('.mbCard-form-notNull').length == 0) {
				$('<span>*</span>').addClass("mbCard-form-notNull mb-icon-notNull").appendTo(item.obj.closest("[data-role='fieldcontain']").find('label'));
			}
		}
	} else {
		if (!$.isEmptyObject(rules)) {
			var len = rules.length;
			var rule;
			var tmpRules = [];
			for (var i = 0; i < len; i++) { // 遍历查找是否已经存在必须输入验证,存在则删除
				rule = rules[i];
				if (rule['regular'] == '^[\s]{0,}$') {
					continue;
				}
				tmpRules.push(rule);
			}
			this._validations[itemCode] = tmpRules;
		}
		if (item) { // 移除必填星号
			item.obj.closest("[data-role='fieldcontain']").find('label').find('.mbCard-form-notNull').remove();
			console.log('移除必填星号');
		}
	}
};
mb.ui.form2.prototype.doExpression = function() {
	var _self = this;
	
	var itemCode, tmpExp, tarItem, len, i;
	
	// 必填
	function doNotNull(itemCode, exp) {
		if (_self.expFunc(exp, true)) {
			_self.setNotNull(itemCode, true);
		} else {
			_self.setNotNull(itemCode, false);
		}
	};
	
	// 只读
	function doReadOnly(item, exp) {
		if (_self.expFunc(exp, true)) {
			item.disabled();
		} else {
			item.enabled();
		}
	};
	
	// 隐藏
	function doHide(item, exp) {
		if (_self.expFunc(exp, true)) {
			item.hide();
		} else {
			item.show();
		}
	};
	
	// 必填表达式
	for (itemCode in this._expNotNullItems) {
		tmpExp = this._expNotNullItems[itemCode];
		// 被控对象
		tarItem = this._uItems[itemCode];
		if (!tarItem) {
			continue;
		}
		// 主控对象集合
		var srcNotNullItems = this._match(tmpExp);
		len = srcNotNullItems.length;
		for (i = 0; i < len; i++) {
			// 匿名函数是为了消除闭包的副作用
			(function(code, exp) {
				doNotNull(code, exp);
				srcNotNullItems[i].change(function() {
					doNotNull(code, exp);
				});
			})(itemCode, tmpExp);
		}
	}
	
	// 只读表达式
	for (itemCode in this._expReadItems) {
		tmpExp = this._expReadItems[itemCode];
		tarItem = this._uItems[itemCode];
		if (!tarItem) {
			continue;
		}
		var srcReadonlyItems = this._match(tmpExp);
		len = srcReadonlyItems.length;
		for (i = 0; i < len; i++) {
			(function(item, exp) {
				doReadOnly(item, exp);
				srcReadonlyItems[i].change(function() {
					doReadOnly(item, exp);
				});
			})(tarItem, tmpExp);
		}
	}
	
	// 隐藏表达式
	for (itemCode in this._expHiddenItems) {
		tmpExp = this._expHiddenItems[itemCode];
		tarItem = this._uItems[itemCode];
		if (!tarItem) {
			continue;
		}
		var srcHiddenItems = this._match(tmpExp);
		len = srcHiddenItems.length;
		for (i = 0; i < len; i++) {
			(function(item, exp) {
				doHide(item, exp);
				srcHiddenItems[i].change(function() {
					doHide(item, exp);
				});
			})(tarItem, tmpExp);
		}
	}
};
/**
 * 替换字段级和系统变量
 * @param tmpExp - 表达式值
 * @param current - 是否当前值
 */
mb.ui.form2.prototype.expFunc = function(tmpExp, current) {
	var _self = this;
	
	tmpExp = Tools.itemVarReplace(tmpExp, current ? _self.getItemsValues() : _self._byIdData); // 替换字段级变量
	tmpExp = Tools.systemVarReplace(tmpExp); // 替换系统变量
	tmpExp = Tools.itemVarReplace(tmpExp, _self._linksVars); // 关联定义的变量替换
	tmpExp = tmpExp.replace(/undefined/g, ''); // 替换undefined
	var actExp = eval(tmpExp);
	return actExp;
};
/**
 * 返回匹配的字段数组
 */
mb.ui.form2.prototype._match = function(str) {
	var matchItems = [];
	var regExp = new RegExp("#.*?#", "gm");
	var match = regExp.exec(str); // #字段#
	while (match) {
		var matchStr = match[0].substr(1, match[0].length - 2);
		var item = this._uItems[matchStr];
		if (item) {
			matchItems.push(item);
		}
		match = regExp.exec(str);
	}
	return matchItems;
};
mb.ui.form2.prototype.addElement = function() {
	var _self = this;
	
	$.each(_self._uItems, function(i, ui) {
		if (ui && ui.addElement) {
			ui.addElement();
		}
	});
};
/**
 * 绑定事件
 */
mb.ui.form2.prototype._bindEvent = function() {
	var _self = this;
	
	
	/**
	 * 给所有编辑按钮绑定事件,已弃用!
	 */
	/*
	_self.mainContainer.on('click', '.js-edit-field', function(event) {
		event.preventDefault();
		var ui = _self._uItems[$(this).parent().attr('code')];
		
		var editWrp = $("<div data-role='popup' id='edit' data-theme='b' class='ui-corner-all'><div style='padding:5px 20px;'></div></div>");
		var editTitle = $("<h4></h4>").appendTo(editWrp);
		var editTip = $("<p></p>").appendTo(editWrp);
		var editLabel = $("<label for='area' class='ui-hidden-accessible'>修改框:</label>").appendTo(editWrp);
		var editArea = $("<textarea id='area' autofocus=''></textarea>").appendTo(editWrp);
		var editOk = $("<button class='js-ok'>确定</button>").appendTo(editWrp);
		
		editArea.val(ui.getValue());
		editTitle.text('修改' + ui.getItemName());
		
		editOk.off('click').on('click', function() {
			ui.setValue(editArea.val());
			editWrp.popup('close');
		});
		
		editWrp.popup().popup('open');
		editWrp.trigger('create');
		editArea.focus();
	});
	*/
	
	/*
	_self.mainContainer.on('click', '.js-file-popup', function(event) {
		event.preventDefault();
		var updateFileWrp = $("<div id='file-update' data-role='popup' data-theme='a' data-overlay-theme='b' class='ui-content' style='padding:.5em 1em;'></div>");
		$("<h3>编辑文件信息</h3>").appendTo(updateFileWrp);
		var updateFileField = $("<div data-role='fieldcontain'><label for='file-name'>文件名:</label><textarea id='file-name'>需要更改的文件名字</textarea></div>").appendTo(updateFileWrp);
		var updateFileOkBtn = $("<button class='ui-btn-inline' data-theme='b' data-icon='check' data-iconpos='left'>保存文件名</button>").appendTo(updateFileWrp);
		$("<h3>删除文件</h3>").appendTo(updateFileWrp);
		var updateFileDelBtn = $("<button data-theme='c' data-icon='delete' data-iconpos='left'>删除此文件</button>").appendTo(updateFileWrp);
		
		updateFileWrp.popup().popup('open', {positionTo: '#cardview_content'});
		updateFileWrp.trigger('create');
	});
	*/
};




/*********************定义表单各组件****************************/
/**
 * 输入框,密码框类
 * 已弃用
 */
mb.ui.div = function(options) {
	var defaults = {
			'password': false	// 是否是密码框
	};
	
	var opts = $.extend(defaults, options);
	var item = opts.item;
	var readOnly = opts.readOnly;	// 是否只读
	this.password = opts.password;	// 是否密码框
	this.itemCode = opts.itemCode;	// 字段编码
	this.itemName = opts.itemName;	// 字段名称
	this.obj = $("<div class='js-edit-field ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset'><span></span></div>").attr('id', opts.id).attr('name', opts.id);
};
mb.ui.div.prototype.setValue = function(value, name) {
	if (this.password) { // 如果是密码框
		name = '******'; // 显示内容全用*表示
	}
	this.obj.children().html(name ? name : value);
	this.obj.children().data('item-value', value).data('item-text', name ? name : value);
};
mb.ui.div.prototype.getValue = function() {
	return this.obj.children().data('item-value');
};
mb.ui.div.prototype.getText = function() {
	return this.obj.children().data('item-text');
};
mb.ui.div.prototype.getContainer = function() {
	return this.obj.closest('div');
};
mb.ui.div.prototype.getItemCode = function() {
	return this.itemCode;
};
mb.ui.div.prototype.getItemName = function() {
	return this.itemName;
};
mb.ui.div.prototype.addError = function(msg) {
	this.obj.closest("[data-role='fieldcontain']").addClass('error');
	if (msg) {
		this.obj.closest("[data-role='fieldcontain']").append("<span id='" + this.itemCode + "-validate' class='error-msg'>" + msg + "<span>");
	}
};
mb.ui.div.prototype.removeError = function() {
	this.obj.closest("[data-role='fieldcontain']").removeClass('error');
	this.obj.closest("[data-role='fieldcontain']").find('#' + this.itemCode + '-validate').remove();
};
mb.ui.div.prototype.hide = function() {
	this.obj.closest("[data-role='fieldcontain']").hide();
};
mb.ui.div.prototype.show = function() {
	this.obj.closest("[data-role='fieldcontain']").show();
};

/**
 * 可编辑输入框,密码框,长输入框,大文本类
 */
mb.ui.input = function(options) {
	var _self = this;
	
	var defaults = {};
	var opts = $.extend(defaults, options);
	var item = opts.item;
	this.readOnly = opts.readOnly;	// 是否只读
	this.password = opts.password;	// 是否密码框
	this.itemCode = opts.itemCode;	// 字段编码
	
	this.obj = $("<input type='text' />"); // 默认是input
	if (opts.itemCols >= 2 || opts.type == UIConst.FITEM_ELEMENT_TEXTAREA) { // 如果字段列数大于等于2或者是文本框
		this.obj = $("<textarea></textarea>"); // 使用textarea
	}
	if (this.password) { // 如果是密码,使用password
		this.obj = $("<input type='password' />");
	}
	this.obj.attr('id', opts.id).attr('name', opts.id);
	
	this._changeFuncs = [];
};
mb.ui.input.prototype.setValue = function(value, name) {
	var _self = this;
	this.obj.val(name ? name : value);
	this.obj.data('item-value', value).data('item-text', name ? name : value);
	if (this.readOnly == 1) {
		_self.disabled();
	}
	this.onchange();
};
mb.ui.input.prototype.getValue = function() {
//	return this.obj.data('item-value');
	return this.obj.val();
};
mb.ui.input.prototype.getText = function() {
	return this.obj.data('item-text');
};
mb.ui.input.prototype.getContainer = function() {
	return this.obj.closest('div');
};
mb.ui.input.prototype.disabled = function() {
	this.obj.attr('disabled', 'disabled');
};
mb.ui.input.prototype.enabled = function() {
	this.obj.attr('disabled', '');
};
mb.ui.input.prototype.addError = function(msg) {
	this.obj.closest("[data-role='fieldcontain']").addClass('error');
	if (msg) {
		this.obj.closest("[data-role='fieldcontain']").append("<span id='" + this.itemCode + "-validate' class='error-msg'>" + msg + "<span>");
	}
};
mb.ui.input.prototype.removeError = function() {
	this.obj.closest("[data-role='fieldcontain']").removeClass('error');
	this.obj.closest("[data-role='fieldcontain']").find('#' + this.itemCode + '-validate').remove();
};
mb.ui.input.prototype.hide = function() {
	this.obj.closest("[data-role='fieldcontain']").hide();
};
mb.ui.input.prototype.show = function() {
	this.obj.closest("[data-role='fieldcontain']").show();
};
mb.ui.input.prototype.change = function(func) {
	var _self = this;
	if (func) {
		this._changeFuncs.push(func);
	}
	_self.obj.change(function() {
		console.log('触发了mb.ui.input的change事件......');
		if (func) {
			func.call(_self);
		}
	});
};
mb.ui.input.prototype.onchange = function() {
	var len = this._changeFuncs.length;
	for (var i = 0; i < len; i++) {
		this._changeFuncs[i].call(this);
	}
};
/**
 * 树形选择框类
 */
mb.ui.tree = function(options) {
	var _self = this;
	
	var defaults = {};
	var opts = $.extend(defaults, options);
	var item = opts.item;
	this.readOnly = opts.readOnly;
	this.password = opts.password;
	this.itemCode = opts.itemCode;
	
	this.obj = $("<div class='js-edit-field ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset'><span></span></div>")
				.attr('id', opts.id).attr('name', opts.id);
};
mb.ui.tree.prototype.setValue = function(value, name) {
	var _self = this;
	
	this.obj.children().html(name ? name : value);
	this.obj.children().data('item-value', value).data('item-text', name ? name : value);
	if (this.readOnly == 1) {
		_self.disabled();
	}
//	this.onchange();
};
mb.ui.tree.prototype.getValue = function() {
	return this.obj.children().data('item-value');
};
mb.ui.tree.prototype.getText = function() {
	return this.obj.children().data('item-text');
};
mb.ui.tree.prototype.getContainer = function() {
	return this.obj.closest('div');
};
mb.ui.tree.prototype.disabled = function() {
	this.obj.attr('disabled', 'disabled');
};
mb.ui.tree.prototype.enabled = function() {
	this.obj.attr('disabled', '');
};
mb.ui.tree.prototype.addError = function(msg) {
	this.obj.closest("[data-role='fieldcontain']").addClass('error');
	if (msg) {
		this.obj.closest("[data-role='fieldcontain']").append("<span id='" + this.itemCode + "-validate' class='error-msg'>" + msg + "<span>");
	}
};
mb.ui.tree.prototype.removeError = function() {
	this.obj.closest("[data-role='fieldcontain']").removeClass('error');
	this.obj.closest("[data-role='fieldcontain']").find('#' + this.itemCode + '-validate').remove();
};
mb.ui.tree.prototype.hide = function() {
	this.obj.closest("[data-role='fieldcontain']").hide();
};
mb.ui.tree.prototype.show = function() {
	this.obj.closest("[data-role='fieldcontain']").show();
};
/**
 * 日期选择类
 */
mb.ui.date = function(options) {
	var _self = this;
	
	var defaults = {};
	var opts = $.extend(defaults, options);
	this.readOnly = opts.readOnly;
	this.itemCode = opts.itemCode;
	this.item_input_config = opts.ITEM_INPUT_CONFIG || "";
	
	this.obj = $("<input readonly='readonly' class='js-ui-date' type='text' />").attr('id', opts.id).attr('name', opts.id).attr('itemCode', opts.itemCode);
	this.bindClickEvent(this.obj);
	
	this._changeFuncs = [];
};

mb.ui.date.prototype.bindClickEvent = function(obj) {
	var _self = this;
	if (_self.readOnly == 1) {
		return;
	}
	
	obj.off("vclick").on("vclick", function() {
		var configArray = [];
		if (!_self.item_input_config) { //默认值"DATE"
			configArray[0] = "DATE";
		} else {
			configArray = _self.item_input_config.split(",");
			if (configArray.length == 0) {
				configArray[0] = "DATE";
			}
		}
		
		var params = {
				'dateMode': configArray[0],
				'initDate': _self.getValue(),
				'minDate': (configArray[2] || ""), //如此写法主要与PC端配置兼容
				'maxDate': (configArray[4] || "")
		};
        rh.datepicker(params, function success(arg) {
        	_self.setValue(arg);
        }, function error(arg) {
        	alert("error:" + arg);
        });
	});
};

mb.ui.date.prototype.setValue = function(date) {
	var _self = this;
	this.obj.val(date);
	if (this.readOnly == 1) {
		_self.disabled();
	}
	this.onchange();
};
mb.ui.date.prototype.getValue = function() {
	return this.obj.val();
};
mb.ui.date.prototype.disabled = function() {
	this.obj.attr('disabled', 'disabled');
};
mb.ui.date.prototype.enabled = function() {
	this.obj.attr('disabled', '');
};
mb.ui.date.prototype.addError = function(msg) {
	this.obj.closest("[data-role='fieldcontain']").addClass('error');
	if (msg) {
		this.obj.closest("[data-role='fieldcontain']").append("<span id='" + this.itemCode + "-validate' class='error-msg'>" + msg + "<span>");
	}
};
mb.ui.date.prototype.removeError = function() {
	this.obj.closest("[data-role='fieldcontain']").removeClass('error');
	this.obj.closest("[data-role='fieldcontain']").find('#' + this.itemCode + '-validate').remove();
};
mb.ui.date.prototype.hide = function() {
	this.obj.closest("[data-role='fieldcontain']").hide();
};
mb.ui.date.prototype.show = function() {
	this.obj.closest("[data-role='fieldcontain']").show();
};
mb.ui.date.prototype.change = function(func) {
	var _self = this;
	if (func) {
		this._changeFuncs.push(func);
	}
	_self.obj.change(function() {
		if (func) {
			func.call(_self);
		}
	});
};
mb.ui.date.prototype.onchange = function() {
	var len = this._changeFuncs.length;
	for (var i = 0; i < len; i++) {
		this._changeFuncs[i].call(this);
	}
};
mb.ui.date.prototype.addElement = function() {
	console.debug(this.obj);
	var iconObj = $("<a href='#' tabindex='-1' aria-hidden='true' class='ui-input-clear ui-btn ui-icon-calendar ui-btn-icon-notext ui-corner-all' title='Clear text'>Clear text</a>").css({
		'position': 'absolute',
		'right': 0,
		'top': '75%',
		'margin': '-14px .3125em 0',
		'border': 0,
		'background-color': 'rgba(0, 0, 0, 0)'
	});
	this.obj.after(iconObj);
};
/**
 * 日期选择类
 */
/*
mb.ui.date = function(options) {
	var _self = this;
	
	var defaults = {};
	var opts = $.extend(defaults, options);
	this.readOnly = opts.readOnly;
	this.itemCode = opts.itemCode;
	
	this.obj = $("<div class='js-ui-date ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset ui-input-has-clear'><span></span>" +
			'<a href="#" tabindex="-1" aria-hidden="true" class="ui-input-clear ui-btn ui-icon-calendar ui-btn-icon-notext ui-corner-all" title="Clear text">Clear text</a>' +
			"</div>").attr('id', opts.id).attr('name', opts.id).attr('itemCode', opts.itemCode);
};
mb.ui.date.prototype.setValue = function(date) {
	var _self = this;
	
	this.obj.find('span').text(date);
	this.obj.find('span').data('item-value', date).data('item-text', date);
	if (this.readOnly == 1) {
		_self.disabled();
	}
};
mb.ui.date.prototype.getValue = function() {
	return this.obj.find('span').data("item-value");
};
mb.ui.date.prototype.disabled = function() {
	this.obj.attr('disabled', 'disabled');
};
mb.ui.data.prototype.enabled = function() {
	this.obj.attr('disabled', '');
};
mb.ui.date.prototype.addError = function(msg) {
	this.obj.closest("[data-role='fieldcontain']").addClass('error');
	if (msg) {
		this.obj.closest("[data-role='fieldcontain']").append("<span id='" + this.itemCode + "-validate' class='error-msg'>" + msg + "<span>");
	}
};
mb.ui.date.prototype.removeError = function() {
	this.obj.closest("[data-role='fieldcontain']").removeClass('error');
	this.obj.closest("[data-role='fieldcontain']").find('#' + this.itemCode + '-validate').remove();
};
mb.ui.date.prototype.hide = function() {
	this.obj.closest("[data-role='fieldcontain']").hide();
};
mb.ui.date.prototype.show = function() {
	this.obj.closest("[data-role='fieldcontain']").show();
};
*/
/**
 * 下拉框,单选框,复选框类
 */
mb.ui.selectMenu = function(options) {
	var _self = this;
	
	var defaults = {
			'multiple': false	// 是否可以复选
	};
	var opts = $.extend(defaults, options);
	
	var dictData = opts.data || ''; // 字典值
	this.readOnly = opts.readOnly;
	var multiple = opts.multiple; // 是否可以复选
	this.id = opts.id;				// 
	this.itemCode = opts.itemCode;
	
	var group = $("<select data-native-menu='false' data-icon='rh-carat-r'></select>").attr('id', opts.id).attr('name', opts.id);
	if (multiple) {
		group.attr('multiple', 'multiple');
	}
	$("<option value='" + UIConst.SELECT_MENU_NULL + "'>--请选择--</option>").appendTo(group);
	$.each(dictData, function(i, n) {
		var value = n.ITEM_CODE || n.ID;
		var labelText = n.NAME;
		$("<option value='" + value + "'>" + labelText + "</option>").appendTo(group);
	});
	this.obj = group;
	
	// change事件响应函数数组
	this._changeFuncs = [];
};
// 设置选择框的值,传入的值格式为: 1,3,5
mb.ui.selectMenu.prototype.setValue = function(value) {
	var _self = this;
	if (!value) {
		value = "";
	}
	var splitValue = value.split(',');
	$.each(splitValue, function(index, item) {
		if (item == '') {
			item = UIConst.SELECT_MENU_NULL;
		}
		_self.obj.find("option[value='" + item + "']").attr('selected', true);
	});
	this.obj.selectmenu().selectmenu('refresh');
	if (this.readOnly == 1) {
		_self.disabled();
	}
	this.onchange();
};
// 获取选择的值,格式为:['1', '3']
mb.ui.selectMenu.prototype.getValue = function() {
	var _self = this;
	// 如果值为常量,说明为空
	if (this.obj.val() === UIConst.SELECT_MENU_NULL) {
		return null;
	} else {
		return this.obj.val();
	}
};
mb.ui.selectMenu.prototype.getContainer = function() {
	return this.obj.closest('div');
};
mb.ui.selectMenu.prototype.disabled = function() {
	this.obj.selectmenu('disable');
};
mb.ui.selectMenu.prototype.enabled = function() {
	this.obj.selectmenu('enable');
};
mb.ui.selectMenu.prototype.addError = function(msg) {
	this.obj.closest("[data-role='fieldcontain']").addClass('error');
	if (msg) {
		this.obj.closest("[data-role='fieldcontain']").append("<span id='" + this.itemCode + "-validate' class='error-msg'>" + msg + "<span>");
	}
};
mb.ui.selectMenu.prototype.removeError = function() {
	this.obj.closest("[data-role='fieldcontain']").removeClass('error');
	this.obj.closest("[data-role='fieldcontain']").find('#' + this.itemCode + '-validate').remove();
};
mb.ui.selectMenu.prototype.hide = function() {
	this.obj.closest("[data-role='fieldcontain']").hide();
};
mb.ui.selectMenu.prototype.show = function() {
	this.obj.closest("[data-role='fieldcontain']").show();
};
mb.ui.selectMenu.prototype.change = function(func) {
	var _self = this;
	if (func) {
		this._changeFuncs.push(func);
	}
	this.obj.change(function() {
		if (func) {
			func.call(_self);
		}
	});
};
mb.ui.selectMenu.prototype.onchange = function() {
	var len = this._changeFuncs.length;
	for (var i = 0; i < len; i++) {
		this._changeFuncs[i].call(this);
	}
};

/**
 * 正文,附件类
 */
mb.ui.attach = function(options) {
	var _self = this;
	
	var defaults = {};
	this.opts = $.extend(defaults, options);
	var item = this.opts.item;
	this.form = this.opts.form;
	this.type = this.opts.type;
	this.itemCode = item.ITEM_CODE;
	this._fileIds = "";	// 文件ID集合
	this._files = []; //文件对象数组
	
	var group = $("<div style='float:left;min-height:2.2em;width:70%;'>");
	this.obj = group;
};
mb.ui.attach.prototype.clickDeleteBtn = function(fileId) {
	var _self = this;
	if (confirm("确定要删除该文件么？")) {
		FireFly.doAct("SY_COMM_FILE", "delete", {
			"_PK_":fileId,
			"FILE_ID":fileId
		}, false, true).then(function() {
			var newFiles = [];
			$.each(_self._files, function(i,file) {
				if (file.FILE_ID != fileId) {
					newFiles.push(file);
				}
				_self._files = [];
				_self._fileIds = "";
				_self.setValue(newFiles);
			});
		},function(){
			alert("删除失败了");
		});
	}
};
mb.ui.attach.prototype.clickEditBtn = function(fileId, fileName) {
	// 构建文件信息修改页面
    var fileEditDia = $("<div id='file-edit-dia' data-close-btn='none' data-role='page' data-dialog='true'></div>");
	$("<div data-role='header'><h2>文件信息修改</h2></div>").appendTo(fileEditDia);
	$("<div role='main' class='ui-content'>" +
			"<div data-role='fieldcontain'>" +
				"<div>文件名：</div>" +
				"<textarea class='fileName ui-btn-ruaho' value='" + fileName + "'>"+fileName+"</textarea>" +
			"</div>" +
//			"<div data-role='fieldcontain' class='ui-field-contain'>" +
//				"<label>排序</label>" +
//				"<input value='' />" +
//			"</div>" +
		"</div>").appendTo(fileEditDia);
	var btns = $("<div>").css({"text-align":"center"}).appendTo(fileEditDia);
	$("<a href='#' class='ui-btn ui-btn-inline ui-btn-ruaho' data-rel='back'>取消</a>").appendTo(btns);
	$("<a href='#' class='ui-btn ui-btn-inline ui-btn-ruaho'>确定</a>").appendTo(btns).on('click', function() {
		//更新数据库
		var name = $(".fileName", fileEditDia).val();
		if (!name) {
//			alert("文件名称不能为空");
			rh.displayToast({text: "文件名称不能为空"});
		} else {
			FireFly.doAct("SY_COMM_FILE", "save", {
				"_PK_":fileId,
				"FILE_ID":fileId,
				"DIS_NAME":name
			}, false, true).then(function() {
				$.mobile.back();
			},function(){
				alert("更新失败了");
			});
		}
	});
	
	// 加到当前页面上,初始化page
	fileEditDia.appendTo($.mobile.pageContainer).page();
	fileEditDia.on('pagehide', function(event, ui) {
		$(this).remove();
	});
	// 跳转到当前页
	$.mobile.changePage($('#file-edit-dia'), {transition: 'none'});
};
mb.ui.attach.prototype.clickViewBtn = function(fileId, fileName) {
	console.log(fileId + '----------' + fileName);
	rh.openFile(
			{
				url: window.location.protocol + '//' + window.location.host + '/docview/' + fileId,
				title: fileName
			}
	);
};
mb.ui.attach.prototype.clickBtn = function(popup,viewBtn,editBtn,deleteBtn, fileId, fileName) {
	var _self = this;
	editBtn.off('vclick').on('vclick', function(event) {
		event.preventDefault();
		_self.clickEditBtn(fileId, fileName);
		popup.popup("close");
	});
	deleteBtn.off('vclick').on('vclick', function(event) {
		event.preventDefault();
		_self.clickDeleteBtn(fileId);
		popup.popup("close");
	});
	viewBtn.off('vclick').on('vclick', function(event) {
		event.preventDefault();
		_self.clickViewBtn(fileId, fileName);
		popup.popup("close");
	});
};

mb.ui.attach.prototype.oneFileRender = function(file, pre) {
	var _self = this;
	var IconObj = {
		    "7z":"icon-zip",
		    "asc":"icon-txt",
		    "doc":"icon-word",
		    "docx":"icon-word",
		    "flv":"icon-flash",
		    "gif":"icon-image",
		    "gz":"icon-zip",
		    "gzip":"icon-zip",
		    "jpeg":"icon-image",
		    "jpg":"icon-image",
		    "pdf":"icon-pdf",
		    "png":"icon-image",
		    "ppt":"icon-ppt",
		    "rar":"icon-zip",
		    "swf":"icon-flash",
		    "tar":"icon-zip",
		    "txt":"icon-txt",
		    "unknown":"icon-unknown",
		    "xdoc":"icon-xdoc",
		    "xls":"icon-excel",
		    "xlsx":"icon-excel",
		    "zip":"icon-zip"
		};
	var fileId = file.FILE_ID;
	var fileName = file.DIS_NAME;
	if (pre) {
		pre = pre + ".";
	} else {
		pre = "";
	}
	
	var icon = IconObj[Tools.getFileSuffix(file.FILE_NAME)];
	if (!icon) {
		icon = "icon-unknown";
	}
	var $strArr = ['<span>'+pre+'</span>'];
	$strArr.push("<span class='" + icon + " iconC'></span>");
	$strArr.push('<span>'+fileName+'</span>');
	
	var $file = $("<div style='line-height:2.2em;'>").html($strArr.join(""));
	$file.off("click").on("click", function() {
		event.preventDefault();
		
		_self.clickViewBtn(file.FILE_ID, file.FILE_NAME);
		
		// 构造按钮组
		/*var filePopupWrp = $('<div data-role="popup" id="popupMenu" data-theme="a">');
		var fieldSetWrp = $('<ul data-role="listview" data-inset="true" style="min-width:210px;">').appendTo(filePopupWrp);
		var viewBtn = $('<li><a href="#">查看</a></li>').appendTo(fieldSetWrp);
		var editBtn = $('<li><a href="#">编辑</a></li>').appendTo(fieldSetWrp);
		var deleteBtn = $('<li><a href="#">删除</a></li>').appendTo(fieldSetWrp);
		_self.clickBtn(filePopupWrp,viewBtn, editBtn, deleteBtn, fileId, fileName);
		
		filePopupWrp.appendTo($.mobile.pageContainer).trigger('create');
		filePopupWrp.on('afterclose', function() {
			$(this).remove();
		});
		
		filePopupWrp.popup({
			overlayTheme: 'a',
			positionTo: $(this)
		}).popup('open');*/
	});
	return $file;
};

mb.ui.attach.prototype.setValue = function(fileData) {
	var _self = this;

	//取得对应的附件列表
	var fileIds = [];
	$.each(fileData, function(i, file) {
		if (file.FILE_CAT == _self.itemCode) {
			_self._files.push(file);
			fileIds.push(file.FILE_ID);
		}
	});
	_self._fileIds = fileIds.join(",");
	
	_self.obj.empty();
	if (_self._files.length == 1) { //只有一个文件，直接显示文件名称
		var file = _self._files[0];
		var $file = _self.oneFileRender(file);
		_self.obj.append($file);
	} else if (_self._files.length == 0) { //没有文件
	} else { //多个文件
		$.each(_self._files, function(i, file) {
			var $file = _self.oneFileRender(file, i+1);
			_self.obj.append($file);
		});
	}
};
mb.ui.attach.prototype.getValue = function() {
	var _self = this;
    return _self._fileIds;
};

