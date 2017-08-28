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
	getVar:function(data, def) {
		if (data.indexOf('@') == 0) {
			return this.temp[data];
		} else if (def) {
			return def;
		} else {
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
 * 手机端对ajax返回结果的提示信息的处理
 */
function rh_processMsg(resultData, tipFlag) {
	// 不用判断session是否超时
	if (resultData[UIConst.RTN_MSG]) {
		var tip = resultData[UIConst.RTN_MSG];
		var time = resultData[UIConst.RTN_TIME] + '秒';
		if (tip.indexOf(UIConst.RTN_OK) == 0) {
			if (tip == UIConst.RTN_OK) {
				tip = '操作成功!';
			} else {
				tip = tip.substring(3);
			}
			console.log('ToastInfo' + tip + time + tipFlag);
		} else if (tip.indexOf(UIConst.RTN_ERR) == 0) {
			if (tip == UIConst.RTN_ERR) {
				tip = '操作错误!';
			} else {
				tip = tip.substring(6);
			}
			console.log('ToastError' + tip + tipFlag );
		} else if (tip.indexOf(UIConst.RTN_WARN) == 0) {
			if (tip == UIConst.RTN_WARN) {
				tip = '警告提示!';
			} else {
				tip = tip.substring(5);
			}
			console.log('ToastWarn' + tip + tipFlag);
		} else {
			console.log('ToastError' + '操作失败!' + tipFlag);
		}
	}
};
/**
 * 通过ajax方式获取后台数据
 * @param ajaxUrl - 请求地址
 * @param queryParams - 参数
 * @param tipFlag - 是否提示
 * @param async - 是否异步
 * @returns - 数据或者defer对象
 */
function rh_processData(ajaxUrl, queryParams, tipFlag, async) {
	var params = $.extend({}, queryParams, {expando: $.expando});
	ajaxUrl = FireFly.getContextPath() + '/' + ajaxUrl;
	if (async !== false) {
		async = true;
	}
	if (async) { // 异步
		var deferred = Q.defer();
		if (!FireFly.isEnableConnect()) { // 如果不能连网
			deferred.reject(); // 拒绝
		} else {
			$.ajax({
				url		: encodeURI(ajaxUrl),
				type	: 'post',
				data	: params,
				dataType: 'json',
				cache	: false,
				timeout	: 30000,
				async	: true,
				success	: function(data) {
					if (typeof data === 'string') { // 如果返回字符串数据类型
						deferred.resolve($.parseJSON(data));
					} else {
						deferred.resolve(data);
					}
					FireFly.setEnableConnect(true);
				},
				error	: function(err) {
					console.debug(err);
//					alert('网络无法连接,请手动刷新!');
					rh.displayToast({text: '网络无法连接,请手动刷新!'});
				}
			});
		}
		return deferred.promise;
	} else {
		var resultData = new Object();
		$.ajax({
			url		: encodeURI(ajaxUrl),
			type	: 'post',
			data	: params,
			dataType: 'json',
			cache	: false,
			timeout	: 10000,
			async	: false,
			success	: function(data) {
				resultData = {};
				resultData = data;
				if (typeof data === 'string') {
					resultData = $.parseJSON(data);
				}
				if (tipFlag != false) {
					rh_processMsg(resultData, tipFlag);
				}
			},
			error	: function(err) {
				resultData = {};
				resultData.exception = err;
				resultData.msg = err.responseText || 'error';
				alert('rh_processData is error!');
			}
		});
		return resultData;
	}
};
/**
 * TODO 参数传递的形式不知道能不能用
 * 列表查询调用
 * @param - 请求路径
 * @param - 参数
 * @param - 是否提示
 * @param - 是否异步
 * @returns - 返回值或者defer对象
 */
function listQueryData(postUrl, dataParams, tipFlag, async) {
	var str = $.toJSON(dataParams); // json转字符串
	var result = rh_processData(postUrl, {data: str}, tipFlag, async);
	return result;
};
/**
 * TODO 参数传递的形式不知道能不能用
 * 拼串方式的提交数据，可嵌套两层
 * 如data={'ADDFILE':[{'123':'qwe'},{'345':'tyu'}},'CODE':'uiop']}
 * @param - 请求路径
 * @param - 参数
 * @param - 是否提示
 * @param - 是否异步
 * @returns - 返回值或者defer对象
 */
function fixSaveData(postUrl, dataParams, tipFlag, async) {
	var paramJson = {data: $.toJSON(dataParams)};
	var result = rh_processData(postUrl, paramJson, tipFlag, async);
	return result;
};
/**
 * firefly对象,平台级缓存和与后台交互方法
 */
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
	 * @param - 服务ID
	 * @param - 数据ID
	 * @param - 扩展参数
	 * @param - 是否异步
	 */
	byId : function(sId, id, param, async) {
		var ajaxUrl = sId + '.byid.do';
		if (id) {
			ajaxUrl += '?' + UIConst.PK_KEY + '=' + id;
		}
		return rh_processData(ajaxUrl, param, false, async);
	},
	/**
	 * 根据id取得功能单条记录
	 * @param sId 参数：功能编码
	 * @param id 参数：主键
	 * @param param 参数：扩展参数
	 */
	/*byId : function(sId, id, param) {
		var ajaxUrl = sId + '.byid.do';
		if (id) {
			ajaxUrl += '?' + UIConst.PK_KEY + '=' + id;
		}
		return rh_processData(ajaxUrl, param);
	},*/
	/**
	 * 手机端获取卡片数据
	 */
	byId4Card : function(sId, id, param, async) {
		var ajaxUrl = sId + '.byidMB.do';
		if (id) {
			ajaxUrl += '?' + UIConst.PK_KEY + '=' + id;
		}
		this.setEnableConnect(true);
		return rh_processData(ajaxUrl, param, false, async);
	},
	/**
	 * 手机端获取卡片数据
	 */
	/*byId4Card : function(sId, id, param) {
		var ajaxUrl = sId + '.byidMB.do';
		if (id) {
			ajaxUrl += '?' + UIConst.PK_KEY + '=' + id;
		}
		this.setEnableConnect(true);
		return rh_processData(ajaxUrl, param);
	},*/
	/**
	 * 获取单条记录
	 */
	byIdParam : function(urlParam, async) {
		return rh_processData(ajaxUrl, {}, false, async);
	},
	/**
	 * 卡片页面保存修改
	 */
	cardAddModify : function(sId, data, async) {
		var url = sId + '.save.do';
		return fixSaveData(url, data, true, async);
	},
	/**
	 * 批量保存
	 */
	batchSave : function(sId, data, tipFlag, async) {
		var url = sId + '.batchSave.do';
		return fixSaveData(url, data, tipFlag, async);
	},
	/**
	 * 列表删除
	 */
	listDelete : function(sId, data, tipFlag, async) {
		var url = sId + '.delete.do';
		return rh_processData(url, data, tipFlag, async);
	},
	/**
	 * 获取服务定义
	 */
	getServMainData : function(sId) {
		var url = sId + '.serv.do';
		return rh_processData(url, {}, false, true).then(function(result) {
			FireFly.setCache(sId, FireFly.servMainData, result);
			return result;
		});
	},
	/**
	 * 获取列表数据
	 */
	getListData : function(sId, datas, async) {
		//{"_PAGE_":{"SHOWNUM":"30","PAGES":"2","ALLNUM":"60","NOWPAGE":"1"}};
		var url = sId + '.query.do'; 
		return listQueryData(url, datas, false, async);
	},
	/**
	 * 获取分页数据
	 */
	getPageData : function(sId, datas, async) {
		var url = sId + '.query.do';
		return listQueryData(url, datas, false, async);
	},
	/**
	 * 获取菜单
	 */
	/*getMenu : function(async) {
		var ajaxUrl = 'SY_COMM_INFO.menu.do';
		return rh_processData(ajaxUrl, {}, false, async);
	},*/
	/**
	 * 获取卡片附件
	 */
	getCardFile : function(sId, dataId, itemCode, async) {
		var ajaxUrl = 'SY_COMM_FILE.finds.do';
		var data = {
				'SERV_ID': sId,
				'DATA_ID': dataId
		};
		if (itemCode && itemCode.length > 0) {
			data['FILE_CAT'] = itemCode;
		}
		return rh_processData(ajaxUrl, data, false, async);
	},
	/**
	 * 获取字典
	 */
	getDict : function(dictCode, pid, extWhere, level, showPid, params, async) {
		var ajaxUrl = 'SY_COMM_INFO.dict.do';
		var data = {'DICT_ID': dictCode};
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
		
		if (async) { 	// 异步
			return rh_processData(ajaxUrl, data, false, true).then(function(result) {
				var temp = [];
				temp.push({
					'ID' : result.DICT_ID,
					'NAME' : result.DICT_NAME,
					'CHILD' : result.CHILD
				});
				FireFly.setCache(dictCode, FireFly.dictData, temp);
				return result;
			});
		} else { 		// 同步
			var res = rh_processData(ajaxUrl, data, false, false);
			var temp = [];
			temp.push({
				'ID': res.DICT_ID,
				'NAME': res.DICT_NAME,
				'CHILD': res.CHILD
			});
			FireFly.setCache(dictCode, FireFly.dictData, temp);
			return res;
		}
	},
	/**
	 * TODO 不知道这个跟doAct有什么区别???
	 */
	doActObject : function(sId, aId, data, tipFlag, async) {
		var url = sId + '.' + aId + '.do';
		return fixSaveData(url, data, tipFlag, async);
	},
	/**
	 * 获取数据方法
	 */
	doAct : function(sId, aId, data, tipFlag, async) {
		var ajaxUrl = sId + '.' + aId + '.do';
		var datas = data || {}
        return rh_processData(ajaxUrl,datas,tipFlag,async);
	},
	/**
	 * 根据code匹配字典数据
	 * @param 字典数据对象
	 * @param 以逗号分隔的编码串,如:A,B
	 * @return 逗号分隔的名称串,如:部门1, 部门2
	 */
	getDictNames : function(dict, code) {
		var names = '';
		var len = dict.length;
		for (var i = 0; i < len; i++) {
			if (code == dict[i]['ID']) {
				if (names == '') {
					names += dict[i]['NAME'];
				} else {
					names += ',' + dict[i]['NAME'];
				}
				return names;
			}
			
			if (dict[i]['CHILD']) {
				names += this.getDictNames(dict[i]['CHILD'], code);
			}
		}
		return names;
	},
	/**
	 * 获取服务的名称
	 */
	getServName : function(sId) {
		var res = FireFly.getCache('SY_SERV', FireFly.dictData)[0];
		var returnValue = '';
		var len = res.CHILD.length;
		var i = 0;
		for (i; i < len; i++) {
			if (res.CHILD[i].ID == sId) {
				returnValue = res.CHILD[i].NAME;
				break;
			}
		}
		return returnValue;
	},
	/**
	 * 获取公司的名称
	 */
	getCmpyName : function(cmpyCode) {
		if (!cmpyCode) {
			return 'unknow';
		}
		var res = FireFly.getCache('SY_ORG_CMPY_LIST', FireFly.dictData)[0];
		var returnValue = '';
		var len = res.CHILD.length;
		var i = 0;
		for (i; i < len; i++) {
			if (res.CHILD[i].ID == cmpyCode) {
				returnValue = res.CHILD[i].NAME;
				break;
			}
		}
		return returnValue;
	},
	/**
	 * 获取配置
	 */
	getConfig : function(key) {
		var ajaxUrl = 'SY_COMM_CONFIG.finds.do';
		var data = {'CONF_KEY': key};
		var result = rh_processData(ajaxUrl, data, false, false);
		if (result && result.length > 0) {
			return result[0];
		}
		return undefined;
	},
	/*getServMainData : function(sId) {
		var url = sId + '.serv.do';
		var data = {};
		this.setEnableConnect(true);
		return rh_processData(url, data).then(function(result) {
			FireFly.setCache(sId, FireFly.servMainData, result);
			return result;
		});
	},*/
	/*getPageData : function(sId, datas) {
		var url = sId + '.query.do'; // {'_PAGE_':{'SHOWNUM':'30','PAGES':'2','ALLNUM':'60','NOWPAGE':'1'}};
		this.setEnableConnect(true);
//		return rh_processData(url, datas);
		return listQueryData(url, datas);
	},*/
	getMenu : function() {
		var 
//			servId = 'SY_COMM_INFO',
//			actId = 'menu',
			servId = 'SY_COMM_MENU',
			actId = 'finds',
			ajaxUrl = servId + '.' + actId + '.do',
			params = {};
		params['_WHERE_'] = " AND MENU_AREA LIKE '%4%'";
		var cachedData = FireFly.cache[servId + '-' + FireFly.menuData];
		return Q(cachedData).then(function(data) {
			if (data) {
				return data;
			} else {
				FireFly.setEnableConnect(true);
				return rh_processData(ajaxUrl, params).then(function(result) {
					FireFly.setCache(servId, FireFly.menuData, result);
					return result;
				});
			}
		});
	},
	getContextPath : function() {
		return FireFly.contextPath;
	}
};
