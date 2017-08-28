/*平台级方法定义*/
/**
 * 命名空间管理器,定义全局变量，防止命名冲突
 * 如：GLOBAL.namespace("fire.ui");
 * fire.ui = function(){};
 */
var GLOBAL = new Object();
GLOBAL.namespace = function(fullNameSpaceName) {
	var nsArray = fullNameSpaceName.split('.');
	var sEval = '';
	var sNS = '';
	var len = nsArray.length;
	for (var i = 0; i < len; i++) {
		if (i != 0) sNS += '.';
		sNS += nsArray[i];
		sEval += "if (typeof(" + sNS + ") == 'undefined') " + sNS + " = new Object();";
	};
	if (sEval != '') eval(sEval);
};
GLOBAL.getUnId = function(id, sId) { // 获取sid和id的组合值
	return sId + '-' + id;
};
GLOBAL.style = {}; // 风格样式
GLOBAL.servStyle = {}; // 服务定义的嵌入html，包括<script>和<link>标签
GLOBAL.cardView = []; // 卡片页面对象
/**
 * 系统级变量，包括用户信息和时间信息
 */
var System = {
	temp:{},
	user:{},
	conf:{},
	parParams:{},
	mb:{},
	tempParams:{},
	getVar:function(data) {
		if (data.indexOf('@') == 0) {
			return this.temp[data];
		}
		return data;
	},
	getVars:function() {
		return this.temp;
	},
	setVars:function(data) { // 进入列表时获取的系统变量
		this.temp = data;
	},
	setUser:function(key, value) { // 用户信息，根据userBean获取
		this.user[key] = value;
	},
	getUser:function(key) {
		return this.user[key]; // System.getVar("@" + key + "@"); // 获取系统变量
	},
	getUserBean:function() {
		var temp = {};
		temp.USER_CODE = this.getUser('USER_CODE');
		temp.USER_NAME = this.getUser('USER_NAME');
		return temp;
	},
	setConf:function(key, value) {
		this.conf[key] = value;
	},
	getConf:function(key) {
		return this.conf[key];
	},
	setMB:function(key, value) { // 设置手机版本信息
		this.mb[key] = value;
	},
	getMB:function(key) {
		return this.mb[key];
	},
	setTempParams: function(servId, params) { // 作为中间存储临时对象
		this.tempParams[servId] = params;
	},
	getTempParams: function(servId) { // 获取临时对象
		return this.tempParams[servId];
	},
	setParParams: function(params) {
		this.parParams = params;
	},
	getParParams: function() {
		return this.parParams;
	}
};
/**
 * 检测登录是否超时
 * @param resultData
 */
function loginJuge(resultData) {
	if (typeof(resultData) == 'string') {
		_checkSessionTimeoutMsg(resultData);
	} else {
		var msg = resultData.msg;
		_checkSessionTimeoutMsg(msg);
	}
};
/**
 * 提示系统超时
 * @param msg
 * @returns {Boolean}
 */
function _checkSessionTimeoutMsg(msg) {
	if (msg.indexOf("var login = \"LOGIN\";") > 0) {
		Tip.showError("系统超时，需要重新登录！", true);
		var servName = '';
		var pos = msg.indexOf('//####');
		if (pos > 0) {
			servName = msg.substring(pos + 6);
			pos = servName.indexOf('####');
			servName = servName.substring(0, pos);
		}
		alert("系统超时，需要重新登录(" + servName + ")");
		
		// 直接关闭浏览器当前tab
		top.window.opener = top;
		top.window.open('', '_self', '');
		top.window.close();
		return false;
	}
};
/**
 * 通过ajax方式取得后台数据
 */
function rh_processData(ajaxUrl, queryParams, timeout, times, lastDefer) {
	var deferred = lastDefer || Q.defer();
	var params = $.extend({}, queryParams, {expando:$.expando});
		ajaxUrl = FireFly.getContextPath() + "/" + ajaxUrl;
	if (!FireFly.isEnableConnect()) { // 如果不能联网状态
		deferred.reject(); // defer拒绝
	} else {
		$.ajax({
			url		:	encodeURI(ajaxUrl),
			type	:	"post",
			data	:	params,
			dataType:	"json",
			cache	:	false,
			timeout	:	10000,
			success	:	function(data) {
				if (typeof data == 'string') { // 判断返回数据类型
					deferred.resolve($.parseJSON(data));
				} else {
					deferred.resolve(data);
				}
				FireFly.setEnableConnect(true);
			},
			error	:	function(err) {
				var $body = $.mobile.pageContainer;
				if (times == undefined) {
					times = 3;
				}
				if (timeout == undefined) {
					timeout = 5000;
//					timeout = 5000*1000; // 生成文件预览的时候响应时间比较长，暂将所有时间延长1000倍
				}
				if (!$body.find('.connect-overlay').length) {
					$body.append("<div class='connect-overlay'></div>");
				}
				if (!$body.find('.connect-retry').length) {
					$body.append("<div class='connect-retry'></div>");
				}
				
				/*
				if (times == 0) {
					var $retry = $body.find('.connect-retry'),
						width = $retry.outerWidth();
					$retry.animate({left:10-width}, 300);
					if (!$body.find('.connect-interrupt').length) {
						$body.append("<div class='connect-interrupt'>哎呀，网络不给力！<span>点击刷新</span></div>");
					}
					$('.connect-interrupt').animate({left:0}, 300);
					$body.on('click', '.connect-interrupt>span', function() {
						rh_processData(ajaxUrl, queryParams, timeout, 3, deferred);
						var $interrupt = $('.connect-interrupt'),
							w = $interrupt.outerWidth();
						$interrupt.animate({left:10-w}, 300);
					})
				} else {
					$body.find('.connect-retry').html("网络无法连接，尝试第<span>" + (3-times+1) + "</span>次连接到网络...").animate({left:0}, 300);
					return Q.delay(timeout).then(function() {
						return rh_processData(ajaxUrl, queryParams, timeout, times-1, deferred);
					});
				}
				*/
				
				if (times == 0) { // 3次后仍然无法连接
					if (confirm('网络无法连接,是否手动刷新?')) {
						rh_processData(ajaxUrl, queryParams, timeout, 3, deferred);
					}
				} else { // 尝试下一次连接
					return Q.delay(timeout).then(function() {
						return rh_processData(ajaxUrl, queryParams, timeout, times-1, deferred);
					});
				}
			}
		})
	}
	return deferred.promise;
};
/**
 * 通过queryData的方式从后台获取数据
 * 与rh_processData不同的是，参数的传递方式不同
 * @param postUrl
 * @param dataParams
 * @param timeout
 * @param times
 * @param lastDefer
 * @returns
 */
function listQueryData(postUrl, dataParams, timeout, times, lastDefer) {
	var deferred = lastDefer || Q.defer();
    var str = encodeURIComponent(jQuery.toJSON(dataParams));//json格式转字符串
    
    if (!FireFly.isEnableConnect()) { // 如果不能联网状态
		deferred.reject(); // defer拒绝
	} else {
		jQuery.ajax({
			type: "POST",
			url: postUrl,
			async: false,
			data: "data=" + str,
			success: function(data){
				if (typeof data == 'string') { // 判断返回数据类型
					deferred.resolve($.parseJSON(data));
				} else {
					deferred.resolve(data);
				}
				FireFly.setEnableConnect(true);
			},error:function(err) {
				var $body = $.mobile.pageContainer;
				if (times == undefined) {
					times = 3;
				}
				if (timeout == undefined) {
					timeout = 5000;
				}
				if (!$body.find('.connect-overlay').length) {
					$body.append("<div class='connect-overlay'></div>");
				}
				if (!$body.find('.connect-retry').length) {
					$body.append("<div class='connect-retry'></div>");
				}
				
				if (times == 0) {
					var $retry = $body.find('.connect-retry'),
						width = $retry.outerWidth();
					$retry.animate({left: 10-width}, 300);
					if (!$body.find('.connect-interrupt').length) {
						$body.append("<div class='connect-interrupt'>哎呀，网络不给力！<span>点击刷新</span></div>");
					}
					$('.connect-interrupt').animate({left: 0}, 300);
					$body.on('click', '.connect-interrupt>span', function() {
						listQueryData(postUrl, dataParams, timeout, 3, deferred);
						var $interrupt = $('.connect-interrupt'),
							w = $interrupt.outerWidth();
						$interrupt.animate({left: 10-w}, 300);
					});
				} else {
					$body.find('.connect-retry').html("网络无法连接，尝试第<span>" + (3-times+1) + "</span>次连接到网络...").animate({left: 0}, 300);
					return Q.delay(timeout).then(function() {
						return listQueryData(postUrl, dataParams, timeout, times-1, deferred);
					});
				}
			}
		});
	}
    return deferred.promise;
};

var FireFly = {
	jsessionid: '',
	servMainData: 'SERV_MAIN_DATA',
	servListData: 'SERV_LIST_DATA',
	dictData: 'DICT_DATA',
	menuData: 'MENU_DATA',
	contextPath: FireFlyContextPath,
	/**
	 * firefly级缓存变量，获取缓存和设置缓存的方法
	 * serv: 方法名
	 * key: 键
	 * value: 值
	 */
	cache: {},
	setCache: function(serv, key, value) {
		FireFly.cache[serv + '-' + key] = value;
	},
	getCache: function(serv, key) {
		if (FireFly.cache[serv + '-' + key]) {
			return Q(FireFly.cache[serv + '-' + key]);
		}
		
		var defer;
		switch(key) {
			case FireFly.servMainData :
				defer = FireFly.getServMainData(serv);
				break;
			case FireFly.dictData :
				defer = FireFly.getDict(serv);
				break;
			default :
				defer = null;
				break;
		}
		return defer;
	},
	setEnableConnect : function(flag) {
		var $body = $.mobile.pageContainer;
		if (flag) {
			if ($body.hasClass('sc-disconnected')) {
				$body.removeClass('sc-disconnected');
			}
		} else {
			if (!$body.hasClass('sc-disconnected')) {
				$body.addClass('sc-disconnected');
			}
		}
		$('.connect-interrupt').remove();
		$('.connect-retry').remove();
		$('.connect-overlay').remove();
	},
	isEnableConnect : function() {
		var $body = $.mobile.pageContainer;
		return $body.hasClass('sc-disconnected') ? false : true;
	},
	/**
	 * 根据id取得功能单条记录
	 * @param sId 参数：功能编码
	 * @param id 参数：主键
	 * @param param 参数：扩展参数
	 */
	byId : function(sId, id, param) {
		var ajaxUrl = sId + '.byid.do';
		if (id) {
			ajaxUrl += '?' + UIConst.PK_KEY + '=' + id;
		}
		return rh_processData(ajaxUrl, param);
	},
	/**
	 * 手机端获取卡片数据
	 */
	byId4Card : function(sId, id, param) {
		var ajaxUrl = sId + '.byidMB.do';
		if (id) {
			ajaxUrl += '?' + UIConst.PK_KEY + '=' + id;
		}
		this.setEnableConnect(true);
		return rh_processData(ajaxUrl, param);
	},
	getServMainData : function(sId) {
		var url = sId + '.serv.do';
		var data = {};
		this.setEnableConnect(true);
		return rh_processData(url, data).then(function(result) {
			FireFly.setCache(sId, FireFly.servMainData, result);
			return result;
		});
	},
	getPageData : function(sId, datas) {
		var url = sId + '.query.do'; // {'_PAGE_':{'SHOWNUM':'30','PAGES':'2','ALLNUM':'60','NOWPAGE':'1'}};
		this.setEnableConnect(true);
//		return rh_processData(url, datas);
		return listQueryData(url, datas);
	},
	getMenu : function() {
		var 
//			servId = 'SY_COMM_INFO',
//			actId = 'menu',
			servId = 'SY_COMM_MENU',
			actId = 'finds',
			ajaxUrl = servId + '.' + actId + '.do';
		var cachedData = FireFly.cache[servId + '-' + FireFly.menuData];
		return Q(cachedData).then(function(data) {
			if (data) {
				return data;
			} else {
				FireFly.setEnableConnect(true);
				return rh_processData(ajaxUrl, {'MENU_PID':'1TNOWXNkh2I9B3fD9rqW4R'}).then(function(result) {
					FireFly.setCache(servId, FireFly.menuData, result);
					return result;
				});
			}
		});
	},
	getDict : function(dictCode, pid, extWhere, level, showPid, params) {
		var ajaxUrl = 'SY_COMM_INFO.dict.do';
		var data = {'DICT_ID':dictCode};
		this.setEnableConnect(true);
		if (pid) {
			data['PID'] = pid;
			dictCode = dictCode + '-' + pid;
		}
		if (extWhere) {
			data['_extWhere'] = extWhere;
		}
		if (level) {
			data['LEVEL'] = level;
			dictCode = dictCode + '-' + level;
		}
		if (showPid) {
			data['SHOWPID'] = true;
			dictCode = dictCode + '-' + showPid;
		}
		if (params) {
			data['PARAMS'] = params;
			ajaxUrl += '?' + $.param(params);
		}
		return rh_processData(ajaxUrl, data).then(function(result) {
			var temp = [];
			temp.push({
				'ID' : result.DICT_ID,
				'NAME' : result.DICT_NAME,
				'CHILD' : result.CHILD
			});
			FireFly.setCache(dictCode, FireFly.dictData, temp);
			return result;
		});
	},
	doAct : function(sId, aId, data) {
		var ajaxUrl = sId + '.' + aId + '.do';
		var datas = data || {};
		this.setEnableConnect(true);
		return rh_processData(ajaxUrl, datas);
	},
	login : function(id, pwd, cmpyCode, type, params) {
		var str = '',
			ajaxUrl = '';
		if (params) {
			str = $.param(params);
		}
		if (type == 'name') {
			ajaxUrl = 'SY_ORG_LOGIN.weblogin.do?loginName=' + id + '&password=' + pwd + '&cmpyCode=' + cmpyCode + '&' + str;
		} else {
			ajaxUrl = 'SY_ORG_LOGIN.mblogin.do?key=' + id + '&password=' + pwd + '&cmpyCode=' + cmpyCode + '&' + str;
		}
		var data = {};
		this.setEnableConnect(true);
		return rh_processData(ajaxUrl, {});
	},
	logout : function() {
		var ajaxUrl = 'SY_ORG_LOGIN.logout.do';
		var data = {};
		return rh_processData(ajaxUrl, data, false);
	},
	getContextPath : function() {
		return FireFly.contextPath;
	},
	getHttpHost : function() {
		return window.location.protocol + '//' + window.location.host;
	}
}

















