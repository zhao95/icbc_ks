/**
 * 设备初始化
 */

/**
 * 在jqm没有起作用之前进行一个初始化配置
 */
$(document).on('mobileinit', function() {

	// 设置系统级信息
	var
		userCode = orgMapJson["@USER_CODE@"],
		userName = orgMapJson["@USER_NAME@"],
		userImg = orgMapJson["@USER_IMG@"],
		deptCode = orgMapJson["@DEPT_CODE@"],
		deptName = orgMapJson["@DEPT_NAME@"],
		odeptCode = orgMapJson["@ODEPT_CODE@"],
		jianCode = orgMapJson["@JIAN_CODES@"],
		odeptCodePath = orgMapJson["@ODEPT_CODE_PATH@"];
	System.setUser("USER_CODE", userCode);
	System.setUser("USER_NAME", userName);
	System.setUser("USER_IMG", userImg);
	System.setUser("DEPT_CODE", deptCode);
	System.setUser("DEPT_NAME", deptName);
	System.setUser("ODEPT_CODE", odeptCode);
	System.setUser("JIAN_CODES", jianCode);
	System.setUser("ODEPT_CODE_PATH", odeptCodePath);

	// 全局转场效果
	$.mobile.changePage.defaults.transition = 'none'; // 换页效果无
	$.mobile.defaultPageTransition = 'none'; // 页面切换效果无
	$.mobile.defaultDialogTransition = 'none'; // 对话框出现效果无
	$.mobile.buttonMarkup.hoverDelay = 0; // 按钮的反应延时
	
	/**
	 * 重写-设置活动页高度方法
	 */
	$.mobile.resetActivePageHeight = function(height) {
		var page = $('.' + $.mobile.activePageClass), // 当前活动页
			pageHeight = page.height(),
			pageOuterHeight = page.outerHeight(true);

		height = (typeof height === 'number') ? height : $.mobile.getScreenHeight();

		// 页面高度设定死
		// 注释掉，否则ios系统中页面没加载完就截断了，android设备上的photoSwipe插件图片跟不上页面滚动速度
		//		page.css('min-height', height - (pageOuterHeight - pageHeight));
		//		page.css('max-height', height - (pageOuterHeight - pageHeight));
	};
	
	/**
	 * routeview
	 * 当做转发的路由,例如:url?page=listview,将会跳转到listview页面
	 */
	$.mobile.document
	.on('pageshow', '#routeview', function(event) {
		var href = window.location.href;
		console.log(href);
		var point = '';	
		if (href.indexOf('?') > 0) {	// 有参数,解析参数
			var hash = {},
				vars = {};
			var hashes = href.slice(href.indexOf('?') + 1).split('&');
			for (var i = 0; i < hashes.length; i++) {
				hash = hashes[i].split('=');
				vars[hash[0]] = hash[1];
			}
			// 锚点
			point = vars['page'];
		}
		// 根据锚点,跳转
		switch (point) {
		case 'deskview':
			$.mobile.loadPage('../html/deskview.html');
			$.mobile.document.off('pageinit', '#deskview').on('pageinit', '#deskview', function() {
				$.mobile.loading('show', {text: '加载中...', textVisible: true, textonly: false});
				var deskView = new mb.vi.deskView();
					deskView.show();
					
				// 将句柄放入dom中
				$('#deskview').data('handler', deskView);
				setTimeout(function() { rh.hideLoading({}); }, 100);
			});
			break;
		case 'listview':
			$.mobile.loadPage('../html/listview.html');	// 加载页面
			$.mobile.document.off('pageinit', '#listview').on('pageinit', '#listview', function() {
				$.mobile.loading('show', {text: '加载中...', textVisible: true, textonly: false});
				var listView = new mb.vi.listView(vars);
				listView.show();
				
				// 将句柄放入dom中
				$(this).data('handler', listView);
				setTimeout(function() { rh.hideLoading({}); }, 100);
			});
			break;
		case 'cardview':
			$.mobile.loadPage('../html/cardview.html');	// 加载页面
			$.mobile.document.off('pageinit', '#cardview').on('pageinit', '#cardview', function() {
				$.mobile.loading('show', {text: '加载中...', textVisible: true, textonly: false});
				var cardView = new mb.vi.cardView2(vars);
				cardView.show();
				
				// 将句柄放入dom中
				$(this).data('handler', cardView);
				setTimeout(function() { rh.hideLoading({}); }, 100);
			});
			break;
		case 'searchview':
			$.mobile.loadPage('../html/searchview.html'); // 加载查询页面
			$.mobile.document.off('pageinit', '#searchview').on('pageinit', '#searchview', function() {
				$.mobile.loading('show', {text: '加载中...', textVisible: true, textonly: false});
				vars['id'] = 'searchview';
				vars['act'] = UIConst.ACT_CARD_ADD;
				var searchView = new mb.vi.cardView2(vars);
				searchView.show();
				
				// 将句柄放入dom中
				$(this).data('handler', searchView);
				setTimeout(function() { rh.hideLoading({}); }, 100);
			});
		default:
			// 留在路由页面
			break;
		}
	});
	
	/**
	 * 退回到路由页面后,将直接关闭window
	 */
	$.mobile.document
	.on('pagehide', function(event, ui) {
		var $self = $(this);
		// 移除挂载的句柄
		$self.removeData('handler');
		// 如果是回来路由页,直接关闭
		if ($(ui.nextPage).is('#routeview')) {
			console.log('window be close!');
			rh.closeWindow();
		}
	});
	
	// 一进来就load
	rh.displayLoading({text: '加载中...', timeout: '10'});
});
