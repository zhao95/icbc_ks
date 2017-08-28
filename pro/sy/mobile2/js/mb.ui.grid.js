/**
 * 移动版表格组件
 */
GLOBAL.namespace('mb.ui');
mb.ui.grid = function(options) {
	var defaults = {
			'id': options.sId + 'mbGrid',
			'parHandler': null
	};
	this._opts = jQuery.extend(defaults, options);
	this._id = this._opts.id;						// this._id = listview
	this._parHandler = this._opts.parHandler;
	this._pCon = this._parHandler.contentWrp;
	
	this._lData = options.listData._DATA_ || {}; 	// 数据
	this._lPage = options.listData._PAGE_ || {}; 	// 分页数据
	this._data = options.mainData || {}; 			// 主数据
	this._cols = options.listData._COLS_ || {};		// 列表项字段定义
	this._items = this._data.ITEMS;					// 服务字段定义
	
	this.lTitle = UIConst.ITEM_MOBILE_LTITLE;				/* 1移动列表标题 */
	this.lItem = UIConst.ITEM_MOBILE_LITEM;					/* 2移动列表项 */
	this.cItem = UIConst.ITEM_MOBILE_CITEM;					/* 3移动卡片项 */
	this.cHidden = UIConst.ITEM_MOBILE_CHIDDEN;				/* 9移动卡隐藏项 */
	this.lTime = UIConst.ITEM_MOBILE_LTIME;					/* 4列表时间项 */
	this.lImg = UIConst.ITEM_MOBILE_LIMG;					/* 5列表图片项 */
	
	this.lData = {}; // 以pk为key的列表项数据集合
	
	this.nextLoding = false; // 下一页正在加载标记
};
/**
 * 表格渲染方法，入口
 */
mb.ui.grid.prototype.render = function() {
	var _self = this;
	this._bldGrid().appendTo(this._pCon); // 构建列表，放入容器
	this._bldPage().appendTo(this._pCon); // 构建分页，放入容器
	this._afterLoad(); // 渲染后执行方法
};
/**
 * 构建列表，包括列表项
 */
mb.ui.grid.prototype._bldGrid = function() {
//	this.listViewWrp = jQuery("<ul data-role='listview' data-inset='true'></ul>").attr('id', this._id); 列表边框样式调整
	this.listViewWrp = $("<ul data-role='listview'></ul>").attr('id', this._id);
	this.listViewWrp.append(this._bldItems()); // 将构建好的列表项放入
	
	return this.listViewWrp;
};
/**
 * 构建列表项
 */
mb.ui.grid.prototype._bldItems = function() {
	var _self = this;
	var preAllNum = parseInt(this._lPage.SHOWNUM) * (parseInt(this._lPage.NOWPAGE) - 1); // 数据总条数
	var trs = [];
	
	jQuery.each(this._lData, function(i, obj) {
		var nextPageNum = preAllNum + i; // 总条数+1
		_self.lData[obj._PK_] = obj;
		trs.push("<li id='" + obj._PK_ + "' serv='" + obj.SERV_ID + "'>");
		trs.push(_self._bldBlock(nextPageNum, i, obj)); // 创建列表项区块
		trs.push("</li>");
	});
	return trs.join('');
};
/**
 * 构建列表项区块
 */
mb.ui.grid.prototype._bldBlock = function(nextPageNum, index, trData) {
	var _self = this;
	
	var tdRight = [],
		titleArr = [], 	// 标题
//		deptArr = [], 	// 主办部门
//		stateArr = [], 	// 办理环节
		timeArr = [], 	// 时间
		itemArr = [],   // 普通列
		emergencyArr = []; // 紧急程度
	
	jQuery.each(this._cols, function(i, m) {
		var itemCode = m.ITEM_CODE; // 字段code
		var itemName = m.ITEM_NAME; // 字段名称
		var listFlag = m.ITEM_LIST_FLAG; // 字段是否列表显示
		
		var value = trData[itemCode]; // 字段中的值
		
		if (listFlag == 1) { // 是否显示在列表中
			// 通过item获取cols的详细信息
			var code = itemCode;
			var _code = ''; // 原始的字段code,去掉了__NAME后的
			if (itemCode.indexOf('__NAME') > 0) { // 例如这样的字段名"TODO_OPERATION__NAME"
				var code = itemCode.substring(0, itemCode.indexOf('__NAME'));
				_code = code;
			}
			if (itemCode.indexOf('__IMG') > 0) { // 如果是图片字段
				return true;
			}
			var tempN = _self._items[code]; // 获取字段的配置
			
			var mbType = tempN.ITEM_MOBILE_TYPE; // 列表项的移动显示方式
			
			if (mbType == _self.lTitle) { // 标题项
				titleArr.push("<h2 ");
				if (_code.length > 0) {
					titleArr.push(" _code='");
					titleArr.push(_code);
					titleArr.push("'");
				}
				titleArr.push(">");
				titleArr.push(value);
				titleArr.push("</h2>");
			} else if (mbType == _self.lTime) { // 时间项
				//时间必须格式：2012-12-26 11:33:27
				if (value && value.length >= 19) {
					value = value.substring(0,19);
					value = jQuery.timeago(value);
					timeArr.push("<p style='opacity:.6;' class='ui-li-aside'>");
					timeArr.push(value);
					timeArr.push("</p>");
				}
			} else if (mbType == _self.lItem) { // 普通列表项
				// TODO 没有处理紧急程度
				itemArr.push("<span style='opacity:.6;'>");
				itemArr.push(value);
				itemArr.push("</span>");
			}
		}
	});
//	tdRight.push("<a href='#'>");
	tdRight.push(titleArr.join(''));
	tdRight.push("<p>");
	tdRight.push(itemArr.join(''));
	tdRight.push("</p>");
	tdRight.push(timeArr.join(''));
//	tdRight.push("</a>");
	
	return tdRight.join('');
};
/**
 * 获取li集合
 */
mb.ui.grid.prototype.getBlocks = function() {
	var _self = this;
	return this.listViewWrp.find('li');
};
/**
 * 行点击事件，供外部调用
 */
mb.ui.grid.prototype.click = function(func, parSelf) {
	var _self = this;
	this.listViewWrp.off('vclick', 'li').on('vclick', 'li', function(event) {
		event.preventDefault();
		event.stopImmediatePropagation();
//		if (_self.scrolling) { // 如果滚动条正在滚动,则触发的vclick忽略
//			return false;
//		}
		var pkCode = $(this).attr('id');
		func.call(parSelf, pkCode, _self.lData[pkCode]);
		return false;
	});
};
/**
 * 构建翻页
 */
mb.ui.grid.prototype._bldPage = function() {
	var _self = this;
	
	this.more = $('<div id="pullUp"><span class="pullUpIcon"></span><span class="pullUpLabel">上拉加载更多...</span></div>');
	return this.more;
};
/**
 * 记录加载完毕提示
 */
mb.ui.grid.prototype._recordOverTip = function() {
	var _self = this;
	
	this.overTip = jQuery('<div></div>').addClass('mbGrid-overTip');
	var dataLen = this._lData.length || 0;
	var tipText = '全部数据已加载！';
	if (dataLen == 0) {
		tipText = '无相关记录！';
	}
	this.overTip.text(tipText);
	/*
	 * 回到顶部有问题，暂时先屏蔽掉
	var toTop = jQuery('<span>回到顶部</span>').addClass('mbGrid-toTop').appendTo(this.overTip);
	toTop.bind('tap', function() {
		$.mobile.silentScroll();
	});
	*/
	return this.overTip.appendTo(this._pCon);
};
/**
 * 下一页
 */
mb.ui.grid.prototype._nextPage = function() {
	var _self = this;
	var nextPage = parseInt(this._lPage.NOWPAGE) + 1;
	var pages = parseInt(this._lPage.PAGES);
	this._lPage.NOWPAGE = '' + ((nextPage > pages) ? pages : nextPage);
	var data = {'_PAGE_':this._lPage};
	this._parHandler.morePend(data);
};
/**
 * 将更多添加到列表
 */
mb.ui.grid.prototype._morePend = function(listData) {
	this._lData = listData._DATA_ || {};
	this._lPage = listData._PAGE_ || {};
	this.listViewWrp.append(this._bldItems());
	this.listViewWrp.listview().listview('refresh');
	this._afterLoad();
};
/**
 * 加载后执行
 */
mb.ui.grid.prototype._afterLoad = function() {
	var _self = this;
	var nowPage = this._lPage.NOWPAGE;
	var pages = this._lPage.PAGES;
	var allNum = this._lPage.ALLNUM;
	if (nowPage == pages) {
		_self.more.removeClass('loading').find('.pullUpLabel').text('全部加载完成!');
	} 
	// 如果只有一页,并且有数据时,就去掉更多元素
	if (pages == '1' && allNum != '0') {
		_self.more.remove();
	}
	
	this.listViewWrp.listview().listview('refresh');
	if (_self._parHandler.listIScroll) {
		_self._parHandler.listIScroll.refresh();
	}
	
	// 给刷新按钮绑定事件
	this._parHandler.refreshBtn.off('click').on('click', function() {
		_self._parHandler.refresh();
	});
};
