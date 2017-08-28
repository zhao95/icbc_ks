/**
 * 列表页面渲染引擎
 */
GLOBAL.namespace('mb.vi');
/**
 * 构造方法
 */
mb.vi.listView = function(options) {
	var defaults = {
		"id": "listview",
		"sId": "", 			// 服务ID 例如SY_COMM_TODO
		"aId": "", 			// 操作ID 例如list
		"pCon": null,		// TODO 暂未用到
		"pId": null, 		// 数据主键ID
		"extWhere": "",		// 查询条件
		"type": null,		// TODO 暂未用到
		"parHandler": null, // 主卡片的句柄
		"readOnly": false, 	// 页面只读标识
		"headerTitle":"列表",
		'changeHash': true
	};
	this.opts = $.extend(defaults, options);
	this.id = this.opts.id;
	this.servId = this.opts.sId;
	this.headerTitle = this.opts.headerTitle; 	// 标题
	this._data = null;
	this._extendWhere = this.opts.extWhere; 	// 扩展查询条件
	this._readOnly = this.opts.readOnly;
	this._params = this.opts.params || "";		// 页面传入参数
//	this._secondStep = this.opts.secondStep; // 三级页面打开类型：card || 普通页面
	this.showRefresh = false;
	
	this.topTitle = null;						// TODO 暂未用到	
	this.PAGE = {};								// 分页数据
	this.changeHash = this.opts.changeHash;
	
	this.contentHei = ''; 			// 内容总高度
	this.scrollHei = ''; 			// 滚动高度
	this.screenHei = ''; 			// 屏幕高度
	this.listIScroll = '';
};
mb.vi.listView.prototype.show = function() {
	var _self = this;
	
	//1.构造布局
	_self._layout(); 
//	$.mobile.loading("show", {text: "加载中...",textVisible: true,textonly: false});
	//3.加载数据
	_self._initMainData().then(function() {
		// 成功
		_self._bldGrid();
		// 转向页面
		_self.changePage(); 
		// 重置页面主体高度
		_self._resetContentHei();
	}, function(reason) {
		// 失败
		console.log(reason);
		alert('错误!');
	});
//	$.mobile.loading('hide');
	
	//4.加载之后
	_self._afterLoad();
};
/**
 * 构建列表页面布局
 */
mb.vi.listView.prototype._layout = function() {
	var _self = this;
	this.pageWrp = $('#' + this.id);					// 找到页面
	
	this.headerWrp = $('#' + this.id + '_header');		// 设置标题
	var title = decodeURIComponent(this.headerTitle);
	this.headerWrp.find('h2').text(title);
	
	this.contentWrp = $('#' + this.id + '_content').find('#scroller');	// 清除页面主体内容
	this.contentWrp.empty();
	
	this.refreshBtn = $('#' + this.id + '_refresh');	// 刷新按钮
};
/**
 * 加载下一页数据
 */
mb.vi.listView.prototype.morePend = function(options) {
	var _self = this;
	if (options && options._PAGE_) {
		_self.PAGE["_PAGE_"] = options._PAGE_;
	}
	var data = {};
	if (this._extendWhere.length > 0) {
		data["_extWhere"] = this._extendWhere;
	}
	data = $.extend({}, _self.PAGE, data); // 合并分页信息
	if (options && options._NOPAGEFLAG_ && (options._NOPAGEFLAG_ == 'true')) { // 删除分页信息
		delete data._PAGE_;
		delete data._NOPAGEFLAG_;
	}
	
	console.log('当前查询的列表数据的服务ID: ' + this.servId);
	FireFly.getPageData(this.servId, data, true).then(function(result) {
		_self._listData = result;
		_self.grid._morePend(_self._listData);
		_self.grid.click(_self._openLayer, _self);
		_self.changePage();
		
	});
};
/**
 * 转换页面到新构建的列表页面
 */
mb.vi.listView.prototype.changePage = function() {
	var _self = this;
	$.mobile.pageContainer.pagecontainer("change", _self.pageWrp, {changeHash: _self.changeHash});
};
/**
 * 加载列表数据
 */
mb.vi.listView.prototype._initMainData = function() {
	var _self = this;
	// 获取服务定义
	function getServData() {
		return FireFly.getCache(_self.servId, FireFly.servMainData).then(function(result) {
			// 获取表结构
			_self._data = result;
			// 服务名称
			_self.servName = result["SERV_NAME"];
			rh.setRightTopMenu({"naviTitle":(_self.servName || _self.headerTitle)});
		});
	};
	// 获取表中一页的数据
	function getPageData() {
		var options = {
			"_PAGE_": {"SHOWNUM": "20", "NOWPAGE": "1"}	
		};
		if (_self._extendWhere.length > 0) {
			options["_extWhere"] = _self._extendWhere;
		}
		// 获取表数据
		return FireFly.getPageData(_self.servId, options, true).then(function(result) {
			_self._listData = result;
		});
	};
	// 先执行getServData,成功后再执行getPageData，然后回写返回值
	return getServData().then(getPageData);
};
/**
 * 构建列表页面布局
 */
mb.vi.listView.prototype._bldGrid = function() {
//	var temp = {"id": this.servId, "mainData": this._data, "parHandler": this};
	var temp = {'id': 'listview', 'sId': this.servId, 'mainData': this._data, 'parHandler': this};
		temp["listData"] = this._listData;
	
	this.grid = new mb.ui.grid(temp);
	this.grid.render();
	
	console.log('点击列表的时候,咱们需要重写...');
	this.grid.click(this._openLayer, this);
};
/**
 * TODO 需要重写
 * 列表页单击后打开方式
 */
mb.vi.listView.prototype._openLayer = function(pkCode, node) {
	var _self = this;

	var params = _self.resetGridClickParams(node) || {};
	if (!params.sId) {
		params.sId = _self.servId; // 当前服务ID
	}
	if (!params.pId) {
		params.pId = pkCode; // 数据主键
	}
	params.parHandler = _self;
	
	rh.displayLoading({text: '加载中...', timeout: '10'});
	$.mobile.loadPage('../html/cardview.html');
	$.mobile.document.off('pageinit', '#cardview').on('pageinit', '#cardview', function(event) {
		event.preventDefault();
		event.stopImmediatePropagation();
		
		var cardView = new mb.vi.cardView2(params);
		cardView.show();
		
		// 销毁列表页iScroll
		setTimeout(function() {
			_self.destroyIScroll();
		}, 500);
		$(this).data('handler', cardView);
		setTimeout(function() { rh.hideLoading({}); }, 300);
	});
	return false;
};
/**
 * 销毁iScroll
 */
mb.vi.listView.prototype.destroyIScroll = function() {
	this.listIScroll.destroy();
};

/**
 * 重置点击列表行的参数：服务ID(sId)与主键(pId), 会打开指定服务的指定数据的卡片页面
 */
mb.vi.listView.prototype.resetGridClickParams = function(node) {
	return {
		"sId":"",
		"pId":""
	};
};

/**
 * 加载之后的逻辑
 */
mb.vi.listView.prototype._afterLoad = function() {
	//执行功能级js
    this._excuteProjectJS();
};
/**
 * 重置页面主体高度
 */
mb.vi.listView.prototype._resetContentHei = function() {
	var _self = this;
	// 设置内容主体固定高度
	setTimeout(function() {
		$('#' + _self.id + '_content').css({
			'height': $.mobile.getScreenHeight(),
			'overflow-y': 'hidden'
		});
		_self.listIScroll = new iScroll(_self.id + '_content', {
			scrollbars: true,
			onRefresh: function() {
				if (_self.grid._lPage.PAGES == _self.grid._lPage.NOWPAGE) {
					if (_self.grid._lPage.PAGES == 1) {
						var dataLen = _self.grid._lData.length || 0;
						if (dataLen == 0) {
							_self.grid.more.removeClass('flip').removeClass('loading').text('无相关记录!');
						} else {
							_self.grid.more.remove();
						}
					} else {
						_self.grid.more.removeClass('flip').removeClass('loading').text('全部加载完成!');
					}
				} else {
					_self.grid.more.removeClass('loading').find('.pullUpLabel').text('上拉加载更多...');
				}
			},
			onScrollMove: function() {
				if (_self.grid._lPage.PAGES == _self.grid._lPage.NOWPAGE) {
					_self.grid.more.removeClass('flip').removeClass('loading').text('全部加载完成!');
					return;
				}
				/*
				console.log(this.y + ' : ' + (this.maxScrollY - 5));
				if (this.y < (this.maxScrollY - 5) && !_self.grid.more.hasClass('flip')) {
					_self.grid.more.addClass('flip').find('.pullUpLabel').text('松手开始加载...');
				} else if (this.y > (this.maxScrollY + 5) && _self.grid.more.hasClass('flip')) {
					_self.grid.more.removeClass('flip').find('.pullUpLabel').text('上拉加载更多...');
				}
				*/
			},
			onScrollEnd: function() {
				// 如果不等于0
				if (this.y != '0' && _self.grid._lPage.PAGES != _self.grid._lPage.NOWPAGE) {
					_self.grid.more.addClass('loading').find('.pullUpLabel').text('加载中...');
					_self.grid._nextPage();
				}
			}
		});
	}, 200);
};
/**
 * 执行工程级JS
 */
mb.vi.listView.prototype._excuteProjectJS = function() {
	var _self = this;	
    //var loadArray = this._data.SERV_LIST_LOAD_NAMES.split(",");
	var loadArray = [_self.servId]; //暂时只加载自身一级JS
    for (var i = 0;i < loadArray.length;i++) {
    	if (loadArray[i] == "") {
    		return;
    	}
    	load(loadArray[i]);
    }
	function load(value) {
		var pathFolder = value.split("_");
		var lowerFolder = pathFolder[0].toLowerCase();
	    var jsFileUrl = FireFly.getContextPath() + "/" + lowerFolder + "/servjs/" + value + "_list_mb.js";
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
	            } catch(e){}
	        },
	        error: function(){;}
	    });			
	};
};
/**
 * 依靠浏览器历史,实现刷新列表功能
 */
mb.vi.listView.prototype.refresh = function() {
	var _self = this;
	
	$.mobile.loadPage('../html/listview.html', {reloadPage: true});
	
	/*
	var options = {
			'sId': _self.servId,
			'headerTitle': '列表'
	};
	var listView = new mb.vi.listView(options);
	listView.show();
	// 将句柄放入dom中
	$(this).data('handler', listView);
	*/
};

/*
mb.vi.listView.prototype._openLayer = function(pkCode, node) {
	var param = {},
		data = {};
	if (this._secondStep == "card") { // 三级打开方式为卡片
		
		if (node['TODO_CODE'] == 'TODO_REMIND') { // 如果是提醒类待办
			// dialog ID
			var alertId = 'alertId'; 
			// dialog 配置项
			var setting = {
					'selectCallBack': function() {
						var data = {};
						data[UIConst.PK_KEY] = node['TODO_ID'];
						data["TODO_ID"] = node['TODO_ID'];
						FireFly.doAct("SY_COMM_TODO","endReadCon",data).then(function(result) {
							if (result['_MSG_'].indexOf('OK') >= 0) {
								// 删除脏数据
								$.mobile.pageContainer.find('#' + node['_PK_']).remove();
								// 关闭对话框
								$('body').find('#' + alertId).dialog('close');
							} else {
								alert('待办提醒处理失败,请联系管理员!');
							}
						});
					},
					'cancelCallBack': function() {
						// 关闭对话框
						$('body').find('#' + alertId).dialog('close');
					}
			};
			// alert帮助类,打开alert
			AlertHelper.openAlert(alertId, $('body'), node['TODO_TITLE'], node['TODO_CONTENT'], setting);
			
		} else { // 非提醒类待办
			data["sId"] = node["SERV_ID"]; // 服务ID
			data["pkCode"] = node["TODO_OBJECT_ID1"]; // 文儿ID
			data["ownerCode"] = node["OWNER_CODE"]; // 待办所属人
			
			if (node['SERV_ID'] == 'OA_GW_TYPE_FW_SEND') { // 如果是系统来文
				data["niId"] = ''; // 流程ID
				data['sendId'] = node['TODO_OBJECT_ID2']; // 分发ID
			} else {
				data["niId"] = node["TODO_OBJECT_ID2"]; // 流程ID
				data['sendId'] = ''; // 分发ID
			}
//		    data["niId"] = node["TODO_OBJECT_ID2"]; // 流程ID
//			data['sendId'] = node['TODO_OBJECT_ID2']; // 分发ID
			data["readOnly"] = false;
			data["pId"] = pkCode;
			data["act"] = UIConst.ACT_CARD_READ;
			(function(params) {
				var cardView = new mb.vi.cardView(params); // TODO 没有写cardView
				cardView.show();
			}(data));
		}
	} else if (this._secondStep == "toread") { // 待阅的打开方式
		// 打开待办的时候先判断一下是否有主键，许多问题都是因此引起的
		if (!node['TODO_OBJECT_ID2']) {
			alert('---此条信息无主键，请联系管理员！---');
		}
		
		data["sId"] = node["SERV_ID"];
		data["pkCode"] = node["TODO_OBJECT_ID1"];
		data["ownerCode"] = node["OWNER_CODE"];
		data["sendId"] = node["TODO_OBJECT_ID2"]; // 要送交人
		data["readOnly"] = false;
		data["pId"] = pkCode;
		data["act"] = UIConst.ACT_CARD_READ;
		data['secondStep'] = 'toread'; // 待阅，这个标记在打开待阅时控制意见输入框使用
		(function(params) {
			var cardView = new mb.vi.cardView(params);
				cardView.show();
		}(data));
	} else if (this._secondStep == "unfinish") { // 已办的打开方式
		data["sId"] = node["SERV_ID"];
		data["pkCode"] = node["DATA_ID"];
		data["ownerCode"] = node["S_USER"];
		data["pId"] = pkCode;
		data["readOnly"] = false;
		data["act"] = UIConst.ACT_CARD_READ;
		(function(params) {
			var cardView = new mb.vi.cardView(params);
				cardView.show();
		}(data));
	} else if (this._secondStep == "readNews") { // 通知公告的打开方式
		data["sId"] = this.servId;
		data["pkCode"] = pkCode;
		data["headerTitle"] = this.headerTitle;
		(function(params) {
			var newsView = new mb.vi.newsView(params); // TODO 没写
				newsView.show();
		}(data));
	} 
};
*/

