/**
 * 工作台页面渲染引擎
 */
GLOBAL.namespace('mb.vi');

/**
 * 构造方法
 */
mb.vi.pageView = function() {

	formatHref();
	this.vars = getUrlVars();

	/**
	 * 正式解析Url中携带的参数
	 */
	function getUrlVars() {
		var vars = {},
			hash;
		var href = window.location.href;
		// 如果没有锚点或者没有参数,直接跳到首页
		if (href.indexOf('?') <= 0 || href.indexOf('#') <= 0) {
			return vars;
		}

		var hashes = href.slice(href.indexOf('?') + 1, href.indexOf('#')).split('&'); // 截取参数
		var act = href.slice(href.indexOf('#') + 1); // 截取锚链接
		// 向vars中添加参数
		for (var i = 0; i < hashes.length; i++) {
			hash = hashes[i].split('=');
			vars[hash[0]] = hash[1];
		}
		// 向vars中追加操作标识
		vars['act'] = act;
		
		console.debug(vars);
		return vars;
	};
	
	/**
	 * 预处理url,将page信息解析为锚链接,追加到url后面
	 */
	function formatHref() {
		var href = window.location.href;
		var vars = {};
		if (href.indexOf('?') >= 0 && href.indexOf('#') <= 0) {
			var hashes = href.slice(href.indexOf('?') + 1, href.length).split('&');
			for (var i = 0; i < hashes.length; i++) {
				var hash = hashes[i].split('=');
				vars[hash[0]] = hash[1];
			}
		}
		if (vars.page) {
			console.debug(vars);
			window.location.href = href + '#' + vars.page;
		}
	};
};

mb.vi.pageView.prototype.load = function() {
	if (this.vars["sId"]) {
		var act = this.vars["act"];
		if(act == "cardview") { // 默认显示卡片界面
			console.log('---card---');
			var options = {
					'sId': this.vars['sId'],
					'pId': this.vars['pId']
			};
			var cardview = new mb.vi.cardView2(options);
				cardview.show();
		} else if (act == "listview") { // 默认显示列表界面
			console.log('---list---');
			var options = {
					'sId': this.vars['sId'],
					'headerTitle': this.vars['title']
			};
			var listview = new mb.vi.listView(options);
			listview.show();
		}
	} else {
		// 默认显示deskview
		console.log('---desk---');
		var deskView = new mb.vi.deskView(); // 创建deskView对象
		deskView.show(); // 展示
		$.mobile.window.deskView = deskView;
	}
};
