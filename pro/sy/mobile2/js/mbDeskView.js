/**
 * 工作台页面渲染引擎
 */
GLOBAL.namespace('mb.vi');
/**
 * 构造方法
 */
mb.vi.deskView = function(options) {
	var defaults = {
			id : 'deskview'
	};
	this.opts = $.extend(defaults, options);
	this.id = this.opts.id;
	this.iconData = {};
	
	this.count = 0;
};

mb.vi.deskView.prototype.show = function() {
	var _self = this;
	// 1.初始化桌面数据
	this._initMainData().done(function() {
		// 2.设置布局
		_self._bldLayout();
		_self.changePage();
		_self._afterLoad();
		// 3.设置兼岗信息
		if (System.getUser('JIAN_CODES') && System.getUser('JIAN_CODES').length > 0) {
			_self._getJiangang();
			_self.headerWrp.on('vclick', '.zhbx-username', function(event) {
				event.preventDefault();
				event.stopImmediatePropagation();
				$('.jiangang').slideToggle('fast');
			});
		}
	});
};
/**
 * 初始化数据
 */
mb.vi.deskView.prototype._initMainData = function() {
	var _self = this;
	return FireFly.getMenu().then(function(result) {
		var menuDatas = result._DATA_,
			menuLen = menuDatas.length;
		for (var i = 0; i < menuLen; i++) {
			_self._getLeafData(menuDatas[i]);
		}
	});
};
/**
 * 获取移动端菜单
 */
mb.vi.deskView.prototype._getLeafData = function(menuData) {
	var _self = this;
	var child = menuData.CHILD;
	if (child) {
		for (var i = 0, len = child.length; i < len; i++) {
			_self._getLeafData(child[i]);
		}
	} else {
		var id = menuData.MENU_ID,
			area = menuData.MENU_AREA || '';
//		if (area == '4') { // 手机显示
		if (area.indexOf('4') >= 0) { // 有手机显示这一项
			_self.iconData[id] = menuData;
		}
	}
};
/**
 * 设置布局
 */
mb.vi.deskView.prototype._bldLayout = function() {
	var _self = this;
	
	this.pageWrp = $('#' + this.id);
	this.headerWrp = $('#' + this.id + '_header');
	this.contentWrp = $('#' + this.id + '_content');
	this.gridCon = $('#' + this.id + '_grid');
	this.gridCon.appendTo(this.contentWrp);
	var index = 0;
	$.each(_self.iconData, function(i, item) {
		_self._bldOneApp(index, item).appendTo(_self.gridCon);
		index++;
	});
};
/**
 * 根据item组合一个app
 */
mb.vi.deskView.prototype._bldOneApp = function(index, item) {
	var _self = this;
	if (item == null) {
		return;
	}
	var id = item['MENU_ID']
	var classSuffix = String.fromCharCode(parseInt(97) + parseInt((index%3))); // 后缀的字母，分别是a/b/c
	var $item = $("<div class='ui-block-"+classSuffix+"'><a href='#' id='" + id + "'></a></div>");
		$item.data('sid', item['MENU_INFO']);
		$item.data('title', item['DS_NAME']);
	
	var $block = $("<div class='mb-desk-app'><span class='sc-desk-app-icon zhbx-desk-"+item['DS_ICON']+"'></span><div class='mb-desk-app-text'>"+item['DS_NAME']+"</div></div>").appendTo($item.children());
	
	$item.on('vclick', function(event) {
		event.preventDefault();
		event.stopImmediatePropagation();
		
		$.mobile.loadPage('../html/listview.html');
		$.mobile.document.off('pageinit', '#listview').on('pageinit', '#listview', function(event, ui) {
			// 打开列表页面
			var options = {
					'sId': $(this).data('sid'),
					'title': encodeURIComponent($(this).data('title')),
					'page': 'listview'
			};
			var listView = new mb.vi.listView(options);
				listView.show();
			$(this).data('handler', listView);
		});
	});
	
	if (id == 'COCHAT_MOBILE_TODO__ruaho') { // 如果是待办添加计数器
		$("<span class='mb-desk-new-count'></span>").attr('countserv', 'SY_COMM_TODO').appendTo($block);
	}
	if (id == 'COCHAT_MOBILE_TODO_READ__ruaho') { // 如果是待阅添加计数器
		$("<span class='mb-desk-new-count'></span>").attr('countserv', 'SY_COMM_TODO_READ').appendTo($block);
	}
	
	return $item;
};
/**
 * 加载后执行
 */
mb.vi.deskView.prototype._afterLoad = function() {
	var _self = this;
	var title = '您好，' + System.getUser('USER_NAME');
	$(this.headerWrp).find('.zhbx-username span').html(title);
	rh.setRightTopMenu({"naviTitle":title});
	
	if (FireFly.isEnableConnect()) { // 如果联网状态
		this._getCounts();
	}
	if (this.timeout) {
		clearTimeout(this.timeout);
	}
	this.timeout = setTimeout(function() {
		if (FireFly.isEnableConnect()) {
			_self._getCounts();
		}
	}, 3*60*1000); // 3分钟检查一次
};
/**
 * 获取提醒消息个数
 */
mb.vi.deskView.prototype._getCounts = function() {
	return FireFly.doAct('SY_COMM_TODO', 'getTodoCountMb').then(function(result) {
		if (result && result._DATA_) {
			var data = result._DATA_;
			$('.mb-desk-new-count').each(function(i, n) {
				var count = 0,
					countserv = $(this).attr('countserv');
				if (countserv == 'SY_COMM_TODO') { // 待办
					count = data[1];
				} else if (countserv == 'SY_COMM_TODO_READ') { // 待阅
					count = data[2];
				}
				if (count && count > 0) {
					count = count > 99 ? '99+' : count;
					$(this).text(count).show();
				} else {
					$(this).hide();
				}
			});
		}
	});
};
/**
 * 设置兼岗信息
 */
mb.vi.deskView.prototype._getJiangang = function() {
	var _self = this;
	return FireFly.doAct('SY_ORG_LOGIN', 'getJianUsers').then(function(result) {
		if (result && result['_DATA_']) {
			$('#'+_self.id).find('.jiangang').remove();
			
			var listData = result['_DATA_'];
			// 如果不存在兼岗信息，则渲染
			var liArr = [], 
				tipsFlag = false;
			$.each(listData, function(i, obj) {
				liArr.push("<li data-code='"+obj['USER_CODE']+"'><h2>"+obj['USER_NAME']+"</h2><p>"+obj['ODEPT_NAME']+' '+obj['TDEPT_NAME']+"</p><span class='ui-li-count'>"+obj['TODO_COUNT']+"</span></li>");
				// 如果TODO_COUNT>0,则jiangang-tips提示
				if (parseInt(obj['TODO_COUNT'],10) > 0) {
					tipsFlag = true;
				}
			});
			// 设置兼岗标识
			var $deskTitle = $('#'+_self.id).find('.zhbx-username'); // TODO 没有zhbx-desk-title
			if (!$('.jiangang-tips').length) {
				$deskTitle.append("<li class='jiangang-tips'></li>");
			}
			if (tipsFlag) {
				$('#'+_self.id).find('.jiangang-tips').addClass('info');
			} else {
				$('#'+_self.id).find('.jiangang-tips').removeClass('info');
			}
			var jiangangCtn = $("<div class='jiangang'></div>").appendTo($('#'+_self.id));
			var $ul = $("<ul data-role='listview' data-inset='true'></ul>").appendTo(jiangangCtn);
			$ul.append(liArr.join(''));
			jiangangCtn.css("height", $.mobile.getScreenHeight() - 53).enhanceWithin(); // 组件初始化
			$ul.on('vclick', 'li', function(event) {
				event.preventDefault();
				event.stopImmediatePropagation();
				_self.changeUserCode = $(this).data("code");
				
//				$.mobile.loading('show', {text: "加载中...",textVisible: true,textonly: false});
				
				FireFly.doAct('SY_ORG_LOGIN', 'changeUser', {'TO_USER_CODE':_self.changeUserCode}).then(function() {
//					$.mobile.window.deskView._refresh();
					// TODO 需要加密useCode
//					window.location.href = homeUrl + "?userCode=" + _self.changeUserCode + "&homeUrl=" + homeUrl;
					var hrefUrl = "/jqm_test/server/jsp/login_sso_mb.jsp?userCode=" + _self.changeUserCode;
					window.location.href = hrefUrl;
				});
//				$.mobile.loading('hide');
				
				// 这段压缩的时候报错
				/*.finally(function() {
					$.mobile.loading('hide');
				});*/
			});
		}
	});
};
/**
 * 清空桌面
 */
mb.vi.deskView.prototype._clear = function() {
	this.contentWrp.empty();
};
/**
 * 刷新桌面
 */
mb.vi.deskView.prototype._refresh = function() {
	this._clear();
	this.show();
};
/**
 * TODO 
 * 这个方法看着有问题，用的时候再看
 */
mb.vi.deskView.prototype.refresh = function() {
	this._afterLoad();
};
/**
 * 转换页面到新构建的桌面页面
 */
mb.vi.deskView.prototype.changePage = function() {
	var _self = this;
	$.mobile.pageContainer.pagecontainer("change", _self.pageWrp, {changeHash: true});
};


