/** 列表页面渲染引擎 */
GLOBAL.namespace("rh.vi");
/*待解决问题：
 * 
 * */
rh.vi.listView = function(options) {
	var defaults = {
		"id":options.sId + "-viListView",
		"sId":"",//服务ID
		"aId":"", //操作IDf
		"pCon":null,
		"cardCon":null, //卡片对应容器
		"pId":null,
		"linkWhere":"",
		"extWhere":"",
		"treeExtWhere":"",
		"type":null,
		"pkHide":false,
		"paramsFlag":false,
		"showSearchFlag":"true", //普通查询显示
		"showTitleBarFlag":"true", //标题条显示
		"showPageFlag":"true", //分页条显示
		//@author chenwenming  是否显示按钮，默认为显示
		"showButtonFlag":"true",
		//@author guoyanhong 是否可以双击进入页面 设置为true时还需要进行权限判断
		"byIdFlag":true,
		//@author chenwenming  是否显示导航树，默认为显示
		"showNavTreeFlag":"true",
		//@author chenwenming  是否支持点击列表标题排序，默认为支持
		"sortGridFlag":"true",
		"reset":true, //重置外层高度
		"cardReset":true,//是否双击打开时，卡片内部也计算高度
		"cardBackBtn":true,//是否双击打开时，卡片显示返回按钮
		"parHandler":null,//主卡片的句柄,
		"batchFlag":true,
		"dataFlag":true,//第一次进入显示列表内容
		"resetHeiWid": "",//覆盖函数
		"_SELECT_":null,//显示的字段
		"_HIDE_":null,//隐藏的字段
		"selectView":false,//查询选择标识
		"readOnly":false,//页面只读标识
		"replaceQueryModel":null,//1:默认为简洁模式；2:平铺模式 3 窗口模式
		"parWhere":"",
		"parVar":"",//
		"params":null,
		"replaceNavItems":null, //替换左侧树导航定义字段
		"title":"",//列表标题
		"servDef":null, //服务定义数据
		"hideAdvancedSearch":true //是否隐藏高级查询
	};
	this.opts = jQuery.extend(defaults,options);
	this.servId = this.opts.sId;
	this._actVar = this.opts.act;
	this.pCon = this.opts.pCon;//外层容器
	this.listHeader = jQuery();
	this.contentMain = jQuery();
	this.contentSlider = jQuery();
	this._data = null;	
	this._treeWhere = "";//树形条件   _treeWhere
	this._treeExtWhere = this.opts.treeExtWhere;
	this._searchWhere = "";//查询条件
	this._originalWhere = "";//服务定义条件
	this._extendWhere = "";//扩展条件
	this._readOnly = this.opts.readOnly;
	this._replaceNavItems = this.opts.replaceNavItems;
	this._cardRead = false;
	this._byIdFlag = this.opts.byIdFlag; //是否可以进入卡片查看
	this._linkWhere = this.opts.linkWhere;//关联功能过滤条件
	this._dataFlag = this.opts.dataFlag;//是否第一次进入显示列表内容
	this.selectViewFlag = this.opts.selectView;//查询选择的标识
	this.links = this.opts.links || {};//关联功能过滤条件
    this._transferParams = this.opts.transParams || {};//系统处理的转换的jsp链接过来的参数对象
	this.params = this.opts.params || {};
	if ((this.opts.paramsFlag === "true") || (this.opts.paramsFlag === true)) {
		try {
			this.params = _parent.System.getTempParams(this.servId);//和paramsFlag配合使用，构造时传入的参数对象，仅用于传递参数
			if (opener) { //如果是弹出新页面的参数取值处理
				this.params = opener.System.getTempParams(this.servId);
			}
		} catch (e) {}
		if (this.params && this.params.links) {//外层传递的links参数对象
			this.links = this.params.links;
		}
	}
    if (!jQuery.isEmptyObject(this._transferParams)) {//系统处理的链接参数,可被params对象值覆盖
    	this.params = jQuery.extend(this._transferParams,this.params);
    }
	this._extWhere = (this.opts.extWhere && this.opts.extWhere != "undefined") ?  this.opts.extWhere : "";     
	this._transferData = {};
	this._height = "";
	this._width = "";
	this._batchFlag = this.opts.batchFlag;//外层传递给当前view的标识
	this._transBatchFlag = false;//传递给grid的标识
	this._rowBtns = [];//列表行按钮对象
	this._parWhere = this.opts.parWhere || "";//父传过来的系统条件
	this._parVar = this.opts.parVar || "";//父传过来的系统变量
	this._title = this.opts.title;
	this.setParParams(this._parVar);
	this.LINK_WHERE = UIConst.LINK_WHERE;  
	this.SEARCH_WHERE = UIConst.SEARCH_WHERE;
	this.TREE_WHERE = UIConst.TREE_WHERE;   
	this.EXT_WHERE = UIConst.EXT_WHERE; 
	this.whereData = {};
	this.PAGE = {};   
	
	// 是否显示按钮
	this._showButtonFlag = this.opts.showButtonFlag;
	//是否显示导航树
	this._showNavTreeFlag = this.opts.showNavTreeFlag;
	//查询模式
	this._replaceQueryModel = this.opts.replaceQueryModel;
	
	// 保存之前
	this.beforeSave = function() {};
	// 保存之后
	this.afterSave = function() {};
	// 查询之前
	this.beforeSearch = function(where) {return where;};
	
	//点击自己的Tab，刷新页面
	this.tabClickRefresh = false;
	
	this._userPvlg = null; //用户权限缓存
};
/*
 * 渲染列表主方法
 */
rh.vi.listView.prototype.show = function() {
	this._beforeLoad();
	this._initMainData();
	this._layout();
	this._bldGrid();
	this._bldSearch();
	this._bldNavTree();
	this._resetHeiWid();
	this._renderSortIcon();
	this._afterLoad();
};
/*
 * 加载之前
 */
rh.vi.listView.prototype._beforeLoad = function() {
	//背景图片
	Tools.rhSetBodyBack();//设置背景
};
/*
 * 构建列表页面布局
 */
rh.vi.listView.prototype._layout = function() {
	var _self = this;
	//头构造
	this._bldHeader();
	//常用查询预定义
	this._normalQuery();
	//默认布局和Tab生成
    this._tabLayout();
    setTimeout(function(){
    	_self._initLRMove();
    });
    //默认列表的构造
	this.treeFlag = false;
	var navItems = this._data.SERV_NAV_ITEMS;
	if (navItems && navItems.length > 0) {
		this.treeFlag = true;
	}
	var mainP = "wp";
	var navTreeP = "wp0 content-navTreeHide";
	_self.content = jQuery("<div></div>").addClass("content").attr("id",this.servId).addClass("rhList-tabs__con").addClass("rh-bottom-right-radius");//内容区域
	_self._flatSearch();//平铺查询模式构造
	_self._bldBtnBar().appendTo(_self.content);
	if (_self.treeFlag && _self._showNavTreeFlag == "true") {//左右树形和右侧列表宽度设定
		mainP = "wp75";
		navTreeP = "wp25";
		if (_self.selectViewFlag == true) {
			mainP = "wp70";
			navTreeP = "wp30";
		}
	}
	_self.contentMainCont = jQuery("<div></div>").addClass("content-mainCont").addClass("fr").addClass(mainP).appendTo(_self.content);//列表区域
	_self.contentMain = jQuery("<div></div>").addClass("content-main").appendTo(_self.contentMainCont);
	_self.navTreeContainer = jQuery("<div></div>").addClass("content-navTreeCont").addClass("fl").addClass(navTreeP).appendTo(_self.content);//树形区域
	_self.navTree = jQuery("<div></div>").addClass("content-navTree").appendTo(_self.navTreeContainer);/**.addClass("box-shadow-inset")*/
	_self.content.appendTo(_self.pCon);
	
	_self.sonTab[this.servId] = this;

	//this.contentFootr = jQuery("<div></div>").addClass("content-footer").appendTo(pCon);//底部区域，暂时无功能
};
/*
 * 构造头信息
 */
rh.vi.listView.prototype._bldHeader = function() {
	var _self = this;
	if (this.opts.showTitleBarFlag == "true") { //显示标题条
	    this.listHeader = jQuery("<div></div>").addClass("conHeader").appendTo(_self.pCon);//头部：名称信息条等
	} else {
		return true;
	}
	this.titleDiv = jQuery("<div></div>").addClass("conHeaderTitle").addClass("rh-right-radius-head");
	try{
	if (_parent.GLOBAL.style.SS_STYLE_BLOCK) {//区块头
		this.titleDiv.addClass(_parent.GLOBAL.style.SS_STYLE_BLOCK);
	}
	} catch (e) {}

	//展开-收起
	/*var closeImg = jQuery("<span>&nbsp;</span>").addClass("conHeanderTitle-expand").appendTo(this.titleDiv);
    closeImg.bind("click",function() {
    	if (jQuery(this).hasClass("conHeanderTitle-close")) {
    	  _self.content.show();	
    	  jQuery(this).removeClass("conHeanderTitle-close");
    	} else {
    	  _self.content.hide();	
    	  jQuery(this).addClass("conHeanderTitle-close");
    	}
    });*/
    //返回，模拟关闭当前tab
//    this.backImg = jQuery("<span>返回</span>").addClass("conHeanderTitle-refresh").appendTo(this.titleDiv);
    this.backImg = jQuery("<span>"+Language.transStatic('rh_ui_gridCard_string4')+"</span>").addClass("conHeanderTitle-refresh").appendTo(this.titleDiv);
    this.backImg.on("mousedown",function() {
	    if (window.self == window.top) {//顶层页面自动关闭
		   var browserName=navigator.appName;  
		   if (browserName=="Netscape"){  
			   window.open('', '_self', '');  
		   } 
		   window.close();
		   return false;
	    }
   	    if (_self.servId) {
   	    	Tab.close();//关闭当前tab
   	    } 
    });
    if (window.ICBC) {
    	this.backImg.hide();
    }
    if (window.self != window.top) {//顶层页面自动关闭
        //刷新
//    	_self.refreshImg = jQuery("<span>刷新</span>").addClass("conHeanderTitle-refresh").appendTo(this.titleDiv);
    	_self.refreshImg = jQuery("<span>"+Language.transStatic('rh_ui_gridCard_string7')+"</span>").addClass("conHeanderTitle-refresh").appendTo(this.titleDiv);
    	_self.refreshImg.on("mousedown",function() {
    		_self.clickRefreshBtn();
        });
    }
    this.titleDiv.appendTo(this.listHeader);
};

rh.vi.listView.prototype.clickRefreshBtn = function() {
	var _self = this;
	var _loadbar = new rh.ui.loadbar();
	_loadbar.show(true);
	_self._clearSearchValue();
	_self.whereData = {};
	_self.refresh();
	// _self.listBarTip("刷新成功");
	_loadbar.hideDelayed();	
}

/*
 * 添加左右移动按钮 
 */
rh.vi.listView.prototype._initLRMove = function() {
	var _self = this;
	if(this.opts.showTitleBarFlag != "true") {
		return true;
	}
	this.rightBtn = $("<input type='button' id='rightBtn' value=' &gt; '>").css({"float":"right","height":"35px","z-index":"101","position":"absolute","right":"0px","top":"0px"}).appendTo(this.listHeader);
	this.leftBtn = $("<input type='button' id='leftBtn' value=' &lt; '>").attr("disabled",true).css({"float":"right","height":"35px","z-index":"101","position":"absolute","left":"0px","top":"0px"}).appendTo(this.listHeader);
	_self.liTotalWidth = 0;
	this.titleDiv.css({"position":"relative","overflow":"hidden","z-index":"100"});
	$(".rhList-tabs__li",_self.titleDiv).each(function(i,n){
		if(!$(n).is(":hidden")) {
			_self.liTotalWidth += $(n).width();
		}
    });
	_self.liTotalWidth = _self.liTotalWidth + 50;
    if(_self.liTotalWidth > this.listHeader.width()) {
    	this.titleDiv.css("width",(_self.liTotalWidth+50) + "px");
    	this.rightBtn.show();
    	this.leftBtn.show();
    	this.titleDiv.css("margin-left","20px");
    } else {
    	this.titleDiv.css("width","100%");
    	this.rightBtn.hide();
    	this.leftBtn.hide();
    	this.titleDiv.css("margin-left","0px");
    }
    
    $(window).resize(function(){
    	_self.liTotalWidth = 0;
    	$(".rhList-tabs__li",_self.titleDiv).each(function(i,n){
    		if(!$(n).is(":hidden")) {
    			_self.liTotalWidth += $(n).width();
    		}
        });
    	_self.liTotalWidth = _self.liTotalWidth + 50;
    	if(_self.liTotalWidth > _self.listHeader.width()) {
    		_self.titleDiv.css("width",(_self.liTotalWidth+50) + "px");
    		_self.rightBtn.show();
    		_self.leftBtn.show();
    		_self.titleDiv.css("margin-left","20px");
        } else {
        	_self.titleDiv.css("width","100%");
        	_self.rightBtn.hide();
        	_self.leftBtn.hide();
        	_self.titleDiv.css({"margin-left":"0px","left":"0px"});
        }
    });
    
    this.rightBtn.unbind("click").bind("click", function(event) {
    	var p = _self.titleDiv.position();
    	var currWidth = _self.listHeader.width() - p.left;
    	if(currWidth < _self.liTotalWidth) {
    		_self.leftBtn.attr("disabled",false);
    		_self.rightBtn.attr("disabled",true);
    		_self.titleDiv.animate({"left":"-=92px"},500,null,function() {
    			if(_self.listHeader.width() - _self.titleDiv.position().left < _self.liTotalWidth) {
    				_self.rightBtn.attr("disabled",false);
    			}
    		});
    	}
    });
    
    this.leftBtn.unbind("click").bind("click", function(event) {
    	var p = _self.titleDiv.position();
    	if(p.left < 0) {
    		_self.leftBtn.attr("disabled",true);
    		_self.rightBtn.attr("disabled",false);
    		_self.titleDiv.animate({"left":"+=92px"},500,null,function(){
    			if(_self.titleDiv.position().left < 0) {
    				_self.leftBtn.attr("disabled",false);
    			}
    		});
    	}
    });
}

/*
 * 多Tab列表的支持
 */
rh.vi.listView.prototype._tabLayout = function() {
	var _self = this;
	if (this._title.length == 0) {//标题
		this._title = this._data.SERV_NAME;
	}
	_self.sonTab = {};//当前列表Tab的句柄存储对象
	//判断tab是否显示
	var listTabFlag = false;
	jQuery.each(this._linkServ,function(i,n) {
		if (n.LINK_SHOW_POSITION == 4) { //列表TAB关联
			listTabFlag = true;
			return false;
		}
	});
	if (listTabFlag) {//显示TAB
		this.mainUL = jQuery("<ul class='rhList-tabs'></ul>").appendTo(this.titleDiv);
		var temp = {};
		temp[this.servId] = {"LINK_SERV_ID":this.servId,"LINK_NAME":Language.transDynamic("SERV_NAME", this.enJson, this.servName),"LINK_SHOW_POSITION":4,"LINK_WHERE":""};
		var allTabs = jQuery.extend(temp,this._linkServ);
	    jQuery.each(allTabs,function(i,n) {
			  if (n.LINK_SHOW_POSITION == 4) { //列表关联
				  //tab标题的构造
			   	  var tempA = jQuery("<a class='rhList-tabs__li__a'></a>").attr("href","#" + n.LINK_SERV_ID).append(Language.transDynamic("LINK_NAME", n.EN_JSON, n.LINK_NAME));
			   	  var tempLi = jQuery("<li style='' class='rhList-tabs__li'></li>").attr("sid",n.LINK_SERV_ID).append(tempA);
			   	  tempLi.appendTo(_self.mainUL);
			   	  if (i == _self.servId) {
				   	  tempLi.addClass("rhList-tabs__li--selected");
			   	  }
		   		  tempLi.on("click",function(event) {//tab事件绑定
		   			  var sId = n.LINK_SERV_ID;
		   	  	  	  var readFlag = false;
		   	  	      var linkItem = n.SY_SERV_LINK_ITEM || {};
			   	  	  jQuery.each(linkItem, function(i,m) {//生成子功能过滤条件
					   	  	if ((m.LINK_WHERE_FLAG == 2) && (m.LINK_VALUE_FLAG == 2)) {//非过滤条件 && 主单常量值
				   	  	  	    if (m.LINK_ITEM_CODE.toUpperCase() == "READONLY") {//只读参数设置
				   	  	  	        readFlag = m.ITEM_CODE;
				   	  	  	    }
					   	  	}
			   	  	  });
		   	  	  	  var linkWhereAll = Tools.systemVarReplace(n.LINK_WHERE);
		   			  //点击tab样式的修改
		   			  jQuery(".rhList-tabs__li--selected",_self.pCon).removeClass("rhList-tabs__li--selected");
		   			  jQuery(this).addClass("rhList-tabs__li--selected");
		   			  
		   			  //点击tab时构造相应服务列表
		   			  if (_self.sonTab[sId]) { //已构造过列表
			   				if (n.LINK_REFRESH == 1
			   						|| (sId == _self.servId && _self.tabClickRefresh)) { //点击刷新
			   					if (typeof(_self.sonTab[sId].refresh) == "function") {
			   						_self.sonTab[sId].refresh();
			   					} else if (_self.sonTab[sId].is("iframe")) {
			   						_self.sonTab[sId][0].contentWindow.location.reload(true);
			   					}
				   			  }
			   				jQuery(".rhList-tabs__con",_self.pCon).hide();
			   				  jQuery(".rhList-tabs__con[id='" + sId + "']",_self.pCon).show();
			   				  jQuery(".rh-norQuery").hide();
			   				  if (_self.sonTab[sId].norQueryDiv) {
			   					_self.sonTab[sId].norQueryDiv.show();
			   				  }
			   				  try{_self.sonTab[sId]._resetHeiWid();}catch (e) {} 
			   				  
			   				  _self._addSortIconHeader(sId);
			   				  _self._bindClickOrderIcon(sId);
		   			  } else { //第一次点击tab
//		   				  _self.listBarTipLoad("加载中..");
		   				  _self.listBarTipLoad(Language.transStatic('rh_ui_ccexSearch_string8'));
		   				  setTimeout(function() {
		   					  jQuery(".rhList-tabs__con",_self.pCon).hide();
		   					if (n.LINK_SHOW_TYPE == 3) {//自定义url
					   	  		  linkWhereAll = Tools.xdocUrlReplace(linkWhereAll);//如果有doc则替换
					   	  		  linkWhereAll = FireFly.contextPath + linkWhereAll;
					   	  	      var frame = _self._bldURLTab({"tabSid":n.LINK_SERV_ID,"tabUrl":linkWhereAll,"pCon":_self.pCon});
					   	  	      frame._height = frame.height();
		                          _self.sonTab[n.LINK_SERV_ID] = frame;
		                          _self.listClearTipLoad();
					   	  	  } else {
					   	  		var temp = {"sId":sId,"pCon":_self.pCon,"showSearchFlag":"true","showTitleBarFlag":"false",
			   							  "parHandler":_self,"listSonTabFlag":true,"readOnly":readFlag,"linkWhere":linkWhereAll};
			   					  var listView = new rh.vi.listView(temp);
			   					  listView.show();
			   					_self.sonTab[sId] = listView;
					   	  	  }
		   				  },10);
		   			  }
					  event.stopPropagation();
					  return false;
			   	  });
			  }
		});
	} else {//标题设置
		jQuery("<span></span>").addClass("conHeaderTitle-span rh-slide-flagYellow").text(this._title).appendTo(this.titleDiv);
	}
};

/*
 * 构造tab的关联自定义url
 */
rh.vi.listView.prototype._bldURLTab = function(opts) {
   var _self = this;	
	
   var tabSid = opts.tabSid;
   var tabUrl = opts.tabUrl;
   var tabPCon = opts.pCon;
   var id = tabSid;
   var _url = _appendRhusSession(tabUrl);
   var frame = jQuery("<iframe border='0' class='rhList-tabs__con' frameborder='0'></iframe>").attr({"id":id,"name":id,"width":"100%","height":600,"src":_url});
   frame.appendTo(tabPCon);
   document.shareCradViewer = _self;
   jQuery(document).data("shareCradViewer", _self);
   if(frames[id].contentWindow) {
	   frames[id].contentWindow._parentViewer = this;
   } else {
	   frames[id]._parentViewer = this;
   }
   return frame;
};

/*
 * 构造平铺查询区
 */
rh.vi.listView.prototype._flatSearch = function() {
	var _self = this;
	if ((this.opts.showSearchFlag == "false")) {
		return false;
	}
	if (this._queryModel == 2) {//平铺时构造
		var flatSearchCon = jQuery("<div class='content-search'></div>");
    	var treeLink = false;
    	var navItems = _self._data.SERV_NAV_ITEMS;
    	if (navItems.length > 0) {
    		treeLink = true;
    	}
    	_self.advSearch = new rh.ui.search({"id":_self.servId,"data":_self._data,"parHandler":_self,
    		"pCon":flatSearchCon,"treeLink":treeLink,"col":2});
    	_self.advSearch.show();
		flatSearchCon.appendTo(this.content);
		if (_self.advSearch.defaultSetFlag == true) {
			var where = _self.advSearch.getWhere();
			_self.whereData[_self.SEARCH_WHERE] = where;
		}
	}
};

/**
 * 获取平铺模式的字段jQuery对象
 * @param {Object} itemCode 字段key
 */
rh.vi.listView.prototype.getTileItem = function(itemCode){
	var _self = this;
	return _self.advSearch.getItem(itemCode);
};

/*
 * 构造常用查询
 */
rh.vi.listView.prototype._normalQuery = function() {
	var _self = this;
	this.norQueryDiv = jQuery("<div></div>").addClass("rh-norQuery");
	if (!jQuery.isEmptyObject(this._querys)) {
//		var allQuery = {"ALL_QUERY":{"QUERY_NAME":"全部","QUERY_ID":"ALL_QUERY"}};
		var allQuery = {"ALL_QUERY":{"QUERY_NAME":Language.transStatic("rh_ui_ccexSearch_string3"),"QUERY_ID":"ALL_QUERY"}};
		allQuery = jQuery.extend(allQuery, this._querys);
		var queryDefault = 0;
		var count = 0;
		jQuery.each(allQuery,function(i,n) {
			var name = "<span class='rh-norQuery-span'>" + Language.transDynamic("QUERY_NAME", n.EN_JSON, n.QUERY_NAME) + "</span>";
			var queryA = jQuery("<a class='rh-norQuery-a' href='javascript:void(0);'></a>").attr("id",i).html(name)
			queryA.appendTo(_self.norQueryDiv);
			queryA.bind("click",function(event) {
				_self._queryId = i;
				if (i == "ALL_QUERY") {
					_self._queryId = "";
				}
				jQuery(".active",_self.norQueryDiv).removeClass("active");
				jQuery(this).addClass("active");
				_self._bldBtnBar();
				var opts = {"_NOPAGEFLAG_":"true"};
				_self.refreshTreeAndGrid(opts);
//				_self.listBarTip(n.QUERY_NAME + "已加载！");
				_self.listBarTip(Language.transDynamic("QUERY_NAME", n.EN_JSON, n.QUERY_NAME) + " " + Language.transStatic("rhListView_string1"));
			});
			if (n.QUERY_DEFAULT == 1) {//启用此常用查询
				_self._queryId = i;
				queryA.addClass("active");
				queryDefault = i;
			}
			count++;
		});
		//第一个
		this.norQueryDiv.find(".rh-norQuery-a").first().addClass("rh-norQuery-a-left rh-norQuery-borderLeftNone");
		//最后一个
		this.norQueryDiv.find(".rh-norQuery-a").last().addClass("rh-norQuery-a-right rh-norQuery-borderRightNone");
		if (queryDefault == 0) {
			jQuery(".active",_self.norQueryDiv).removeClass("active");
			_self.norQueryDiv.find("#ALL_QUERY").addClass("active");
			_self._queryId = "";
		}
		//构造常用查询
		if (this.titleDiv) {
			this.norQueryDiv.appendTo(this.titleDiv);
		} else if (this.getParHandler() && this.getParHandler().norQueryDiv) {
			this.getParHandler().norQueryDiv.hide(); 
			this.norQueryDiv.appendTo(this.getParHandler().titleDiv);
		}
	}
};


rh.vi.listView.prototype._bldSearchForAdv = function() {
	var _self = this;
	var temp = {"id":this.opts.id,"pid":this.opts.pId,"gridData":this._data};
	var tempDiv = jQuery("<table></table>").addClass("searchDiv");
	var tr = jQuery("<tr></tr>").appendTo(tempDiv);
	var flatSearchCon = jQuery("<div class='content-search'></div>");
	var treeLink = false;
	var navItems = _self._data.SERV_NAV_ITEMS;
	if (navItems.length > 0) {
		treeLink = true;
	}
	_self.advSearch = new rh.ui.ccexSearch({"id":_self.servId,"data":_self._data,"parHandler":_self,
		"pCon":tr,"treeLink":treeLink,"col":3});
	_self.advSearch.show();
	flatSearchCon.appendTo(this.content);
    tempDiv.appendTo(this._btnBar);
};


/*
 * 构建查询区域
 */
rh.vi.listView.prototype._bldSearch = function() {
	var _self = this;
	if ((this.opts.showSearchFlag == "false") || (this._queryModel == 2) || (this._queryModel == 3)) {
		return false;
	} else if (this._queryModel == 4) {
		_self._bldSearchForAdv();
		return false;
	}
	var temp = {"id":this.opts.id,"pid":this.opts.pId,"gridData":this._data};
	var tempDiv = jQuery("<table></table>").addClass("searchDiv");
	var tr = jQuery("<tr></tr>").appendTo(tempDiv);
	var td1 = jQuery("<td></td>").appendTo(tr);
	var td2 = jQuery("<td></td>").appendTo(tr);
	var td3 = jQuery("<td text-valign=middle></td>").appendTo(tr);
	var td4 = jQuery("<td></td>").appendTo(tr);
    this._searchSel =jQuery("<select></select>").addClass("rhSearch-select").appendTo(td1);
//    this._searchText = jQuery("<input type='text' onfocus=\"this.value=''\" value='输入条件'/>").addClass("rhSearch-input").appendTo(td2);
    this._searchText = jQuery("<input type='text' onfocus=\"this.value=''\" value='"+ Language.transStatic("rhListView_string2") +"'/>").addClass("rhSearch-input").appendTo(td2);
    this._searchText.keypress(function(event) {
        if (event.keyCode == '13') {
            _self._searchBtn.click();
            return false;
        }
    });
//    this._searchBtn = jQuery("<div class='rhSearch-button'><div class='rhSearch-inner'>查询</div></div>").addClass("rhSearch-button").append("").appendTo(td3);
    this._searchBtn = jQuery("<div class='rhSearch-button'><div class='rhSearch-inner'>"+ Language.transStatic("rh_ui_ccexSearch_string7") +"</div></div>").addClass("rhSearch-button").append("").appendTo(td3);
    this._searchBtn.bind("click",function() {
//    	_self.listBarTipLoad("结果加载中..");
    	_self.listBarTipLoad(Language.transStatic("rhListView_string3"));
    	setTimeout(function() {//延迟0秒执行
    		var searcCode = _self._getSearchValue();
    		var searcItemType = _self._getSearchItemType();
    		var where = "";
    		var seaValue = jQuery.trim(_self._searchText.val().replace(/\'/g,""));
//    		if ((seaValue.length > 0) && (seaValue != "输入条件")) {//TODO：完善
    		if ((seaValue.length > 0) && (seaValue != Language.transStatic("rhListView_string4"))) {//TODO：完善	
    			if ((seaValue.indexOf("\"") >= 0) || ((seaValue.indexOf("\“") >= 0))) {
    				if ((seaValue.indexOf("\"") == 0) || (seaValue.indexOf("\“") == 0)) {
    					seaValue = seaValue.substring(1);
    				}
    				if ((seaValue.indexOf("\"") > 0) || (seaValue.indexOf("\”") > 0)) {
    					seaValue = seaValue.substring(0,seaValue.length - 1);
    				}
    				where += " and ";
    				if (searcItemType && searcItemType == "dict") {
    					where += "@@" + searcCode + "@" + seaValue + "@@";
    				} else {
    					where += searcCode + "='" + seaValue + "'";
    				}
    			} else {
    				where += " and ";
    				//字典条件替换
    				if (searcItemType && searcItemType == "dict") {
    					if (seaValue.indexOf("\%") >= 0) {
    						where += "@@" + searcCode + "@" + seaValue + "@@";
    					} else {
    						where += "@@" + searcCode + "@%" + seaValue + "%@@";
    					}
    				} else {
    					if (seaValue.indexOf("\%") >= 0) {
    						where += searcCode + " like '" + seaValue + "'";	
    					} else {
    						where += searcCode + " like '%" + seaValue + "%'";	
    					}
    				}
    			}
    		}
    		if (_self.advSearch) {
    			_self.advSearch.des();
    		}
    		_self.setSearchWhereAndRefresh(where,true);
    		return false;
    	},0);
    });
    
    
    //高级查询
//    this._advancedSearchBtn = jQuery("<div class='rhSearch-advancedButton'><div class='rhSearch-advancedButton-inner'>高级</div></div>").addClass("").append().appendTo(td4);
    this._advancedSearchBtn = jQuery("<div class='rhSearch-advancedButton'><div class='rhSearch-advancedButton-inner'>"+Language.transStatic('rhListView_string5')+"</div></div>").addClass("").append().appendTo(td4);
    this._advancedSearchBtn.unbind("click").bind("click",function() {
    	var treeLink = false;
    	var navItems = _self._data.SERV_NAV_ITEMS;
    	if (navItems.length > 0) {
    		treeLink = true;
    	}
    	_self.advSearch = new rh.ui.search({"id":_self.servId,"data":_self._data,"parHandler":_self,"treeLink":treeLink,"cols":1});
    	_self.advSearch.show();
    });
	//_self.searchLevel = this._advancedSearchBtn;//获取高级查询对象
	this.searchLevel = this._advancedSearchBtn;//获取高级查询对象
	//如果要隐藏高级查询按钮，则
	if(this.opts.hideAdvancedSearch){
		this._advancedSearchBtn.hide();
	}
	tempDiv.appendTo(this._btnBar);
};
/*
 * 设置查询对象值，查询和高级查询用到
 */
rh.vi.listView.prototype.setSearchWhereAndRefresh = function(where,treeLink,params) {
	where = this.beforeSearch(where);
	this.whereData[this.SEARCH_WHERE] = where;
	this.whereData["_NOPAGEFLAG_"] = "true";
	if(params) {
		this.whereData["extParams"] = params;
	}
	this.PAGE = {}; //新查询请求，清除上次查询的分页信息。
	if (treeLink == true) {//关联左侧导航树条件
		this.refresh(this.whereData);
	} else {//不关联左侧树条件
		this.whereData[this.TREE_WHERE] = "";
		this.refresh(this.whereData);
		this._refreshNavTree();
	}
};
/*
 *获取查询输入框对象
 */
rh.vi.listView.prototype.getSearchText = function(){
	var _self = this;
	return _self._searchText;
};
/*
 * 获取查询下拉框
 */
rh.vi.listView.prototype.getSearchSel = function(){
	var _self = this;
	return _self._searchSel;
};
/*
 * 获取查询按钮
 */
rh.vi.listView.prototype.getSearchBtn = function(){
	var _self = this;
	return _self._searchBtn;
	
};

/**
 * 隐藏查询区域
 * */
rh.vi.listView.prototype.hideSearchItem = function(){
	if (this.getSearchText()) {
		this.getSearchText().hide();
	}
	if (this.getSearchSel()) {
		this.getSearchSel().hide();
	}
	if (this.getSearchBtn()) {
		this.getSearchBtn().hide();
	}
}
/*
 * 获取高级查询按钮
 */
rh.vi.listView.prototype.getAdvancedSearchBtn = function(){
	var _self = this;
	return 	_self._advancedSearchBtn;
};

/*
 * 获取查询值
 */
rh.vi.listView.prototype._getSearchValue = function() {
	return this._searchSel.val();// + this._searchSel.text());
};
/*
 * 获取查询字段的类型
 */
rh.vi.listView.prototype._getSearchItemType = function() {
	return this._searchSel.find("option:selected").attr("itemtype");// + this._searchSel.text());
};
/*
 * 清空查询值
 */
rh.vi.listView.prototype._clearSearchValue = function() {
	if (this._searchText) {
		this._searchText.val("");
	}
};
/*
 * 获取列表查询按钮对象
 */
rh.vi.listView.prototype.getSearchBtn = function() {
	return this._searchBtn;
};

/*
 * 构建列表(rh.ui.grid)，包括按钮、数据表格、分页条
 */
rh.vi.listView.prototype._bldGrid = function() {
	var _self = this;
	//@TODO：考虑listdata在最后加载
	var options = {};
	if (this._linkWhere.length > 0) {
		options[_self.LINK_WHERE] = this._linkWhere;
		_self.whereData[_self.LINK_WHERE] = this._linkWhere;
		options["_linkServQuery"] = this.opts.linkServQuery || "";
	}
	if (this._extWhere.length > 0) {
		options[_self.EXT_WHERE] = this._extWhere;
		_self.whereData[_self.EXT_WHERE] = this._extWhere;
	}
	if (this._parWhere.length > 0) {//父传过来的条件
		if (_self._extWhere.length > 0) {
			_self.whereData[_self.EXT_WHERE] = _self._extWhere + Tools.parVarReplace(_self._parWhere);		
		} else {
			_self.whereData[_self.EXT_WHERE] = Tools.parVarReplace(_self._parWhere);		
		}
		options[_self.EXT_WHERE] = _self.whereData[_self.EXT_WHERE];
	}
	if (_self.whereData[_self.SEARCH_WHERE] && _self.whereData[_self.SEARCH_WHERE].length > 0) {//查询的条件
		options[_self.SEARCH_WHERE] = _self.whereData[_self.SEARCH_WHERE];
	}

	if (_self._batchFlag == false) {
		_self._transBatchFlag = false;
	}
	if (this.opts._SELECT_) {//列信息
		options["_SELECT_"] = this.opts._SELECT_;
	}
	if (this.opts._HIDE_) {//需隐藏的列
		options["_HIDE_"] = this.opts._HIDE_;
	}
	
	options = jQuery.extend(options,this.slimParams());
	if ((this._dataFlag === false) || (this._dataFlag === "false")) {
		options[_self.EXT_WHERE] = " and 1=2 ";
		this._dataFlag == true;
	}
	//构造常用查询的_queryId
	if (this._queryId && (this._queryId.length > 0)) {
		options["_queryId"] = this._queryId;
	}
	this._listData =  FireFly.getPageData(this.opts.sId,options) || {};	
	if (this.selectViewFlag == true || this.selectViewFlag == "true") {//查询选择取消行按钮显示
		//_self._rowBtns = null;
	}
	var temp = {"id":this.opts.id,"mainData":this._data,"rowBtns":_self._rowBtns,"byIdFlag":_self._byIdFlag,
	            "parHandler":this,"pCon":this.contentMain,"batchFlag":_self._transBatchFlag,"type":this.opts.type,"pkHide":this.opts.pkHide,
	            "sortGridFlag":_self.opts.sortGridFlag,"buildPageFlag":this.opts.showPageFlag};
	temp["listData"] = this._listData;
	this.grid = new rh.ui.grid(temp);
	this.grid.render();	
	//增加底部按钮条
	//this._bldBtnBarBottom();
};
/*
 * 打开卡片页面
 * @param act 动作act
 * @param paramData 自定义传递的参数，供卡片获取
 */
rh.vi.listView.prototype._openCardView = function(act,pkCode,servId,readOnly,paramData) {
	var _self = this;
	var _act = UIConst.ACT_CARD_MODIFY;
	if (act && act == UIConst.ACT_CARD_ADD) {
	    _act = act;	
	}
	var sId = this.opts.sId;
	if (servId) {
		sId = servId;
    }
    var readFlag = this._cardRead;
    if ((this._readOnly === true) || (this._readOnly === "true")) {//只读关联功能
  	    readFlag = true;
    }
    if (readOnly) {
    	readFlag = readOnly;
    }
    backBtn = this.opts.cardBackBtn;
    //打开小卡片条件
    var widHeiArray = null;
//    if (window.self == window.top) {//最外层的打开显示
        var miniCardWid = jQuery(window).width() - 200;
        var miniCardHei = jQuery(window).height() - 200;
        widHeiArray = [miniCardWid,miniCardHei];
    	backBtn = true;
//    }
    this.cardView = null;
    var temp = {"act":_act,"sId":sId,"parHandler":this,"transferData":this._transferData,"readOnly":readFlag,"title":this._title,
    		    "paramData":paramData,"links":this.links,"pCon":this.opts.cardCon,"reset":this.opts.cardReset,
    		    "backBtn":backBtn,"widHeiArray":widHeiArray};
    temp[UIConst.PK_KEY] = pkCode || "";
    
    if (window.ICBC && window.self != window.top && false) { //不是弹出页面，才弹出，否则不弹出
    	var url = sId + ".card.do?pkCode=" + temp[UIConst.PK_KEY] + "&readOnly=" + readFlag;
    	url = _appendRhusSession(url);
    	temp.url = url;
    	temp.params = paramData;
    	temp.tTitle = this._title;
    	temp.params = {
			"callBackHandler" : _self,
			"closeCallBackFunc" : function() {
				_self.refresh();
			}
		};
    	Tab.open(temp);
    } else {
    	this.cardView = new rh.vi.cardView(temp);
        this.cardView.show(temp);
    }
    //RHFile.bldDestroyBase(this.cardView);
};
/*
 * 获取服务和id的合并值(服务-ID)
 * @param id ID值
 */
rh.vi.listView.prototype._getUnId = function(id) {
    var sId = this.opts.sId;
    return sId + "-" + id;
};

/*
* 导入前运行
*/
rh.vi.listView.prototype.beforeImp = function() {
	return true;
};

/*
 * 根据动作绑定相应的方法
 * @param aId 动作ID
 */
rh.vi.listView.prototype._act = function(aId,aObj) {
	var _self = this;
	var taObj = aObj;
	switch(aId) {
		case UIConst.ACT_ADD://添加
			taObj.bind("click",function(event) {
               _self._openCardView(UIConst.ACT_CARD_ADD);
               event.stopPropagation();
               return false;
			});	
			break;
		case UIConst.ACT_BATCH_SAVE://保存
		    taObj.bind("click",function() {
		    	  var datas = _self.grid.getModifyTrDatas();     
	              if (datas == null) {
//	              	   _self.listBarTipError("请选择相应记录！");
	              	   _self.listBarTipError(Language.transStatic("rhListView_string6"));
	              } else {
	            	  //判断列表如果需要校验，对列表进行校验
	            	  if(_self.grid.needValidate() && !_self.grid.validate()) {
//		          	       _self.listBarTipError("校验未通过");
		          	       _self.listBarTipError(Language.transStatic("rhWfCardViewNodeExtends_string1"));
		          	       return false;
		          	   }
//	              	   _self.listBarTipLoad("提交中...");
	              	   _self.listBarTipLoad(Language.transStatic("rhListView_string7"));
	              	   _self._batchSave(datas);
	              }
		    });  
		    break;
	    case UIConst.ACT_DELETE://删除
		    taObj.bind("click",function() {
                var pkArray = _self.grid.getSelectPKCodes();
		    	if (jQuery.isArray(pkArray) && pkArray.length == 0) {
//		    		 _self.listBarTipError("请选择要删除的条目");
		    		 _self.listBarTipError(Language.transStatic("rhListView_string8"));
		    	} else {
//		    		 var res = confirm("您确定要删除该数据么？");
		    		 var res = confirm(Language.transStatic("rhListView_string9"));
		    		 if (res == true) {
//			    		_self.listBarTipLoad("提交中...");
			    		_self.listBarTipLoad(Language.transStatic("rhListView_string7"));
			    		setTimeout(function() {
			    			if(!_self.beforeDelete(pkArray)){
			    				return false;
			    			}
				    		var strs = pkArray.join(",");
				    		var temp = {};
				    		temp[UIConst.PK_KEY]=strs;
				    		var resultData = FireFly.listDelete(_self.opts.sId,temp,_self.getNowDom());
				    		_self._deletePageAllNum();
					        _self.refreshGrid();
					        _self.afterDelete();
			    		},0);	
		    		 } else {
		    		 	return false;
		    		 }
		    	}
		    }); 
		    break;
	    case UIConst.ACT_EXPORT://导出
		    taObj.bind("click",function() {
		    	var select = _self.grid.getSelectPKCodes();
		    	var data = {"_PK_":select.join(",")};
		    	data = jQuery.extend(data,_self.whereData);
		    	var param = jQuery(this).attr("param"); //如果按钮设定存在参数
		    	if (param) { //合并上按钮设定中的参数
		    		data = jQuery.extend(data, jQuery.parseJSON(param));
		    	}
		    	window.open(FireFly.getContextPath() + '/' + _self.opts.sId + '.exp.do?data=' + 
		    		encodeURIComponent(jQuery.toJSON(data)));
		    }); 
		    break;
	    case UIConst.ACT_IMPORT://导入Excel
		    taObj.bind("click",function(event) {

//		    	var config = {"SERV_ID":_self.opts.sId, "FILE_CAT":"EXCEL_UPLOAD", "FILENUMBER":1, 
//		    		"VALUE":5, "TYPES":"*.xls;*.xlsx", "DESC":"导入Excel文件"};
		    	var config = {"SERV_ID":_self.opts.sId, "FILE_CAT":"EXCEL_UPLOAD", "FILENUMBER":1, 
			    		"VALUE":5, "TYPES":"*.xls;*.xlsx", "DESC":Language.transStatic("rhListView_string10")};
		    	var file = new rh.ui.File({
		    		"config" : config,"width":"99%"
		    	});
		    	
		    	var importWin = new rh.ui.popPrompt({
//		    		title:"请选择文件",
		    		title:Language.transStatic("rhListView_string11"),
//		    		tip:"请选择要导入的Excel文件：",
		    		tip:Language.transStatic("rhListView_string12"),
		    		okFunc:function() {
		    			var fileData = file.getFileData();
		    			if (jQuery.isEmptyObject(fileData)) {
//		    				alert("请选择文件上传");
		    				alert(Language.transStatic("rhListView_string13"));
		    				return;
		    			}
		    			var fileId = null;
		    			for (var key in fileData) {
		    				fileId = key;
		    			}
		    			if (fileId == null){
//		    				alert("请选择文件上传");
		    				alert(Language.transStatic("rhListView_string13"));
		    				return;
		    			}
		    			_self._imp(fileId);
		    			importWin.closePrompt();
				        // _self.refreshGrid();
		    			file.destroy();
		    		},
		    		closeFunc:function() {
		    			file.destroy();
		    		}
		    	});
		  
		        var container = _self._getImpContainer(event, importWin);
		    	container.append(file.obj);
		    	file.obj.css({'margin-left':'5px'});
		    	file.initUpload();
		    }); 
		    break;
	    case UIConst.ACT_EXPORT_ZIP://批量导出zip
		    taObj.bind("click",function() {
		    	var content = '<div class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-draggable ui-dialog-buttons rh-small-dialog rh-bottom-right-radius" tabindex="-1" role="dialog" aria-labelledby="ui-id-1" style="outline: 0px; z-index: 1002; height: auto; width: 290px; top: 100px; left: 501px; display: block;"><div class="ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix" style="display: block; padding-right: 0px;"><span id="ui-id-1" class="ui-dialog-title">导出zip数据类型</span><a href="#" class="ui-dialog-titlebar-close ui-corner-all" role="button"><span class="ui-icon ui-icon-closethick"></span></a></div><div class="dictDialog ui-dialog-content ui-widget-content" id="EXPZIP_TYPE-dictDialog" scrolltop="0" scrollleft="0" style="width: auto; min-height: 0px; height: 207px;"><div class="dictTree-left"><div id="bbtree1509001394418" class="bbit-tree"><div class="bbit-tree-bwrap"><div class="bbit-tree-body bbit-tree-single"><ul class="bbit-tree-root bbit-tree-no-lines"><li class="bbit-tree-node"><div id="bbtree1509001394418_1" tpath="0" unselectable="on" title="EXCEL" itemid="1" class="bbit-tree-node-el bbit-tree-node-collapsed"><span class="bbit-tree-node-indent"></span><img class="bbit-tree-ec-icon bbit-tree-elbow" src="/sy/base/frame/coms/tree/images/s.gif"><img class="bbit-tree-node-icon" src="/sy/base/frame/coms/tree/images/s.gif"><a hidefocus="" class="bbit-tree-node-anchor" tabindex="1" href="javascript:void(0);"><span unselectable="on">EXCEL</span></a></div></li><li class="bbit-tree-node"><div id="bbtree1509001394418_2" tpath="1" unselectable="on" title="JSON" itemid="2" class="bbit-tree-node-el bbit-tree-node-collapsed"><span class="bbit-tree-node-indent"></span><img class="bbit-tree-ec-icon bbit-tree-elbow-end" src="/sy/base/frame/coms/tree/images/s.gif"><img class="bbit-tree-node-icon" src="/sy/base/frame/coms/tree/images/s.gif"><a hidefocus="" class="bbit-tree-node-anchor" tabindex="1" href="javascript:void(0);"><span unselectable="on">JSON</span></a></div></li></ul></div></div></div></div></div><div class="ui-dialog-buttonpane ui-widget-content ui-helper-clearfix" style="padding-left: 0px; width: 100%;"><div class="ui-dialog-buttonset"><span class="buttonCon">&nbsp;<button type="button">确认</button>&nbsp;</span><span class="buttonCon">&nbsp;<button type="button">关闭</button>&nbsp;</span></div></div></div>';	    	
		    	 var content1 = '<div class="ui-widget-overlay" style="width: 1280px; height: 1518px; z-index: 1001;"></div>'
		    	jQuery(content).appendTo(jQuery("body"));
		    	jQuery(content1).appendTo(jQuery("body"));
		    	$("span[class='buttonCon']:first").unbind("click").bind("click",function(){
		    		if($("div[class='bbit-tree-node-el bbit-tree-node-collapsed bbit-tree-selected']").length==0){
		    			alert("请选择导出ZIP类型");
		    		}else{
		    			debugger;
		    			var str = $("div[class='bbit-tree-node-el bbit-tree-node-collapsed bbit-tree-selected']").find("span[unselectable='on']").html();
		    			var select = _self.grid.getSelectPKCodes();
		    			var data = {"_PK_":select.join(","),"_TYPEDC_":str};
		    			data = jQuery.extend(data,_self.whereData);
		    			window.open(FireFly.getContextPath() + '/' + _self.opts.sId + '.expZip.do?data=' + 
		    					encodeURIComponent(jQuery.toJSON(data)));
		    			$("div[class='ui-dialog ui-widget ui-widget-content ui-corner-all ui-draggable ui-dialog-buttons rh-small-dialog rh-bottom-right-radius']").remove();
		    			$("div[class='ui-widget-overlay']").remove();
		    		}
		    	});
		    	$("span[class='buttonCon']:last").unbind("click").bind("click",function(){
		    		
		    			$("div[class='ui-dialog ui-widget ui-widget-content ui-corner-all ui-draggable ui-dialog-buttons rh-small-dialog rh-bottom-right-radius']").remove();
		    			$("div[class='ui-widget-overlay']").remove();
		    	});
		    	$("a[class='ui-dialog-titlebar-close ui-corner-all']").unbind("click").bind("click",function(){
		    		
	    			$("div[class='ui-dialog ui-widget ui-widget-content ui-corner-all ui-draggable ui-dialog-buttons rh-small-dialog rh-bottom-right-radius']").remove();
	    			$("div[class='ui-widget-overlay']").remove();
	    	});
		    	
		    	$("#bbtree1509001394418_2").unbind("click").bind("click",function(){
		    	$(this).attr("class","bbit-tree-node-el bbit-tree-node-collapsed bbit-tree-selected");
		    		$("#bbtree1509001394418_1").attr("class","bbit-tree-node-el bbit-tree-node-collapsed");
		    	});
		    	$("#bbtree1509001394418_1").unbind("click").bind("click",function(){
		    		$(this).attr("class","bbit-tree-node-el bbit-tree-node-collapsed bbit-tree-selected");
		    		$("#bbtree1509001394418_2").attr("class","bbit-tree-node-el bbit-tree-node-collapsed");
		    	});
		    });
		    break;
	    case UIConst.ACT_IMPORT_ZIP://批量导导入zip
		    taObj.bind("click",function(event) {
		    	var config = {"SERV_ID":_self.opts.sId, "FILE_CAT":"JSON_UPLOAD", "FILENUMBER":1, 
//		    		"VALUE":15, "TYPES":"*.zip;", "DESC":"导入压缩数据"};
		    		"VALUE":15, "TYPES":"*.zip;", "DESC":Language.transStatic("rhListView_string14")};
		    	var file = new rh.ui.File({
		    		"config" : config
		    	});
		    	
		    	var importWin = new rh.ui.popPrompt({
//		    		title:"请选择文件",
		    		title:Language.transStatic("rhListView_string11"),
//		    		tip:"请选择要导入的zip文件：",
		    		tip:Language.transStatic("rhListView_string15"),
		    		okFunc:function() {
		    			var fileData = file.getFileData();
	
		    			if (jQuery.isEmptyObject(fileData)) {
//		    				alert("请选择文件上传");
		    				alert(Language.transStatic("rhListView_string13"));
		    				return;
		    			}
		    			
		    			var fileId = null;
		    			for (var key in fileData) {
		    				fileId = key;
		    			}
		    			
		    			if (fileId == null){
//		    				alert("请选择文件上传");
		    				alert(Language.transStatic("rhListView_string13"));
		    				return;
		    			}
		    			var param = {};
		    			param["fileId"] = fileId;
		    			//提交  导入 只将fileId传入即可
		    			rh_processData(_self.opts.sId + ".impZip.do", param, true);
		    			importWin.closePrompt();
				        _self.refreshGrid();
		    			file.destroy();
		    		},
		    		closeFunc:function() {
		    			file.destroy();
		    		}
		    	});
		    	importWin._layout(event,undefined,[450,230]);
		    	
		    	var container = jQuery("#" + importWin.dialogId);
		    	container.empty();
		    	importWin.tipBar = jQuery("<div></div>").text(importWin.tip).css({"height":"40px","font-weight":"normal","margin":"15px 15px 0px 15px","color":"red"});
		        container.append(importWin.tipBar);
		    	container.append(file.obj);
		    	file.obj.css({'margin-left':'5px'});
		    	file.initUpload();
		    }); 
		    break;
	    case "back"://返回按钮绑定
		    taObj.bind("click",function() {
                  _self.backImg.mousedown();
		    }); 
		    break;
	    case "test"://测试
		    taObj.bind("click",function() {
                  //_self.grid.addNewTrs();
		    }); 
		    break;
	    case "SELFDEFINED"://自定义显示列
		    taObj.bind("click",function() {
		    	var user_code = System.getVar("@USER_CODE@");
		    	var servId = _self.opts.sId;
		    	var param = {};
		    	param["servid"]=servId;
		    	param["user_code"]=user_code;
		    	var result = FireFly.doAct("TS_SELF_DEFINED_EXP","getServColumn",param);
		    	var data = result.list;
		    	var data1 = result.list1;
		    	var content = '<div style="width:40%;padding-left:5%"><table id="selftable"><tr><th>所有字段</th></tr>';
		    	for(var i=0;i<data.length;i++){
		    		content+='<tr><td position="absolute"><input type="checkbox" style="position:relative;top:5px" name="checkboxaa"><span>'+data[i].ITEM_NAME+'</span></td><td class="_PK_ rhGrid-td-hide">'+data[i].ITEM_CODE;+'</td></tr>';
		    	}
		    	 content += '</table></div>';
		    	 content+='<div style="position:absolute;left:45%;top:150px;"><a id="removeleft" href="#"><img id="imageleft" src="/ts/image/1124.png"></a></div><div style="position:absolute;left:45%;top:250px"><a id="removeright" href="#"><img id="imageright" src="/ts/image/1348.png"></a></div>'
		    	 content+='<div style="position:absolute;left:65%;top:0px;"><table id="selftable2"><tr><th>已选字段</th></tr>';
		    	 for(var a=0;a<data1.length;a++){
		    		 content+='<tr><td position="absolute"><input type="checkbox" style="position:relative;top:5px" name="checkboxbb"><span>'+data1[a].COLUMN_NAME+'</span></td><td class="_PK_ rhGrid-td-hide">'+data1[a].COLUMN_CODE;+'</td></tr>';
		    	 }
		    	 content += '</table></div>';
		    	 
		    	var dialog = jQuery("<div></div>").addClass("dictDialog").attr("title",
		    	"自定义导出列");
		    	var container = jQuery(content).appendTo(dialog);
		    	dialog.appendTo(jQuery("body"));
		    	_self._bindfunc();
		    	
		    	var hei = 430;
		    	var wid = 480;

		    	var scroll = RHWindow.getScroll(parent.window);
		    	var viewport = RHWindow.getViewPort(parent.window);
		    	var top = scroll.top + viewport.height / 2 - hei / 2 - 88;
		    	var posArray = [ "", top ];
		    	dialog.dialog({
		    		autoOpen : true,
		    		height : hei,
		    		width : wid,
		    		show : "bounce",
		    		hide : "puff",
		    		modal : true,
		    		resizable : false,
		    		position : posArray,
		    		buttons : {
		    			"确定" : function() {
		    				var codes = "";
		    				var names = "";
		    				$("input[name='checkboxbb']").each(function(index,item){
		    					var tr = this.parentNode.parentNode
		    					var tds = $(tr).find("td");
		    					var code = tds[1].innerHTML;
		    					var name = $(tds[0]).find("span").html();
		    					codes+=code+",";
		    					names+=name+",";
		    				});
		    				var paramstr = {};
		    				param["codes"]=codes;
		    				param["names"]=names;
		    				param["ServID"]=servId;
		    				param["user_code"]=user_code;
		    				FireFly.doAct("TS_SELF_DEFINED_EXP","saveself",param)
		    				dialog.remove();
		    				
		    			},
		    			"关闭" : function() {
		    				dialog.remove();
		    				
		    			}
		    		}
		    	});
		    }); 
		    break;
	}
};

rh.vi.listView.prototype._bindfunc=function(){
	jQuery("#removeleft").unbind("click").bind("click",function(){
		$('input:checkbox[name=checkboxaa]:checked').each(function(){
			  $(this).attr("name","checkboxbb");
			 var s =  this.parentNode.parentNode.innerHTML;
			 this.parentNode.parentNode.remove();
			 $("#selftable2 tbody").append('<tr>'+s+'</tr>')
		  });
		_mouse();
	});
	jQuery("#removeright").unbind("click").bind("click",function(){
		$('input:checkbox[name=checkboxbb]:checked').each(function(){
			  $(this).attr("name","checkboxaa");
			 var s =  this.parentNode.parentNode.innerHTML;
			 this.parentNode.parentNode.remove();
			 $("#selftable tbody").append('<tr>'+s+'</tr>')
		  });
		 _mouse_();
	});
	document.getElementById("imageleft").onmouseover = function(){
		 this.src = "/ts/image/2020.png";
		 
	}
	document.getElementById("imageleft").onmouseout = function() {
	      this.src = '/ts/image/1124.png';
	  }
	document.getElementById("imageright").onmouseover = function(){
		 this.src = "/ts/image/2024.png";
	}
	document.getElementById("imageright").onmouseout = function() {
	     this.src = '/ts/image/1348.png';
	 }
	//拖动效果
	 var fixHelperModified = function(e, tr) {
         var $originals = tr.children();
         var $helper = tr.clone();
         $helper.children().each(function(index) {
             $(this).width($originals.eq(index).width())
         });
         return $helper;
     },
     updateIndex = function(e, ui) {
         /*$('td.index', ui.item.parent()).each(function (i) {
             $(this).html(i + 1);
         });*/
     };
     $("#selftable2 tbody").sortable({
 helper: fixHelperModified,
 stop: updateIndex
}).disableSelection();
     _mouse();
}

function _mouse(){
	//背景色
	 var btns = document.getElementsByName('checkboxbb');
	 for(var z=0;z<btns.length;z++){
		 var td = btns[z].parentNode;
		 td.onmouseover = function() {
			 this.style.backgroundColor = '#e9e9e9';
		 }
		 td.onmouseout = function() {
			 this.style.background = 'white';
		 }
		 
	 }
}
/**
 * 删除页信息的总条数，用于在添加或删除的时候
 */
rh.vi.listView.prototype._deletePageAllNum = function() {
	if (this.PAGE && this.PAGE["_PAGE_"]) {
		delete this.PAGE["_PAGE_"]["ALLNUM"];
	}
}


rh.vi.listView.prototype._batchSave = function(datas) {
	var _self = this;
	setTimeout(function() {
		// 保存之前
		_self.beforeSave.call(_self, datas);
		var _loadbar = new rh.ui.loadbar();
		try{
			_loadbar.show(true);
			var batchData = {};
			batchData["BATCHDATAS"] = datas;
			var resultData = FireFly.batchSave(_self.opts.sId,batchData,null, false);
		   
		   	// 保存之后
		   	_self.afterSave.call(_self, datas, resultData);
		   	_self.refreshGrid();
	   	 } finally {
			_loadbar.hideDelayed();	
    	}
   	},0);
};

/** 导入数据 **/
rh.vi.listView.prototype._imp = function(fileId, param) {
	var data = {"fileId":fileId};
	if(param) {
		data = jQuery.extend(data, param);
	}
	var _self = this;
	var _loadbar = new rh.ui.loadbar();
	_loadbar.show(true);
	//form提交，需要服务器再返回Excel
	FireFly.doAct(_self.opts.sId, "imp", data, false, true, function(result) {
		if(result._MSG_.indexOf("ERROR,") == 0) {
//			var msg = "导入文件失败，点击“确定按钮”下载文件。请打开文件查看导入结果。";
			var msg = Language.transStatic("rhListView_string16");
			SysMsg.alert(msg, function(){
				if(result.FILE_ID) {
					var url = FireFlyContextPath + "/file/" + result.FILE_ID;
					window.open(url);
				}
			});
		} 
		_self._deletePageAllNum();
		_self.refreshGrid();
		_loadbar.hideDelayed();	
	});
};

/**
 * 导入数据的dialog样式调整
 */
rh.vi.listView.prototype._getImpContainer = function(event, importWin) {
	importWin._layout(event,undefined,[500,230]);
	
	var container = jQuery("#" + importWin.dialogId);
	container.empty();
	importWin.tipBar = jQuery("<div></div>").text(importWin.tip).css({"height":"40px","font-weight":"normal","margin":"15px 15px 0px 15px","color":"red"});
    container.append(importWin.tipBar);
    
	return container;
};

/*
 * 加载的提示信息
 * @param msg 消息
 */
rh.vi.listView.prototype.listBarTipLoad = function(msg,areaFlag) {
	var flag = true;
 	if (areaFlag) {
 		flag = areaFlag;
 	}
	Tip.showLoad(msg,flag);
};
/*
 * 成功的提示信息
 * @param msg 提示内容
 */
rh.vi.listView.prototype.listBarTip = function(msg,areaFlag) {
	var flag = true;
 	if (areaFlag) {
 		flag = areaFlag;
 	}
	Tip.show(msg,flag);
};
/*
 * 错误的提示信息
 * @param msg 提示内容
 */
rh.vi.listView.prototype.listBarTipError = function(msg,areaFlag) {
	var flag = true;
 	if (areaFlag) {
 		flag = areaFlag;
 	}
	Tip.showError(msg,flag);
};
/*
 * 警告的提示信息
 * @param msg 提示内容
 */
rh.vi.listView.prototype.listBarTipAttention= function(msg,areaFlag) {
	var flag = true;
 	if (areaFlag) {
 		flag = areaFlag;
 	}
	Tip.showAttention(msg,flag);
};
/*
 * 清除加载提示信息
 */
rh.vi.listView.prototype.listClearTipLoad = function() {
    Tip.clearLoad();
};
/*
 * 区分列表还是卡片
 */
rh.vi.listView.prototype.getNowDom = function() {
	var _self = this;
	if (this.opts.parHandler && _self.opts.bottomTabFlag && (_self.opts.bottomTabFlag == true)) {
	    return "listBottom";
	} else if (this.opts.parHandler) {
		return null;
	} else {
		return "list";
	}
};
/*
 * 列表页面与后台交互方法，公用方法
 * @param act 动作ID
 * @param async 是否异步
 */
rh.vi.listView.prototype.doAct = function(act,reload,async) {
	var _self = this;
	var pkArray = _self.grid.getSelectPKCodes();
	var strs = pkArray.join(",");
	var datas = {};
	datas[UIConst.PK_KEY]=strs;
	datas = jQuery.extend(datas,this.links);
	
	if(async) {// 异步
	    FireFly.doAct(this.opts.sId, act, datas, true, true, function(result){
			if (result[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0 || result[UIConst.RTN_MSG].indexOf(UIConst.RTN_WARN) == 0) {
		   	    if (reload && reload == true) {
		   	    	_self._refreshGridBody();
		   	    }
		    }
		});
	} else {
		var result = FireFly.doAct(this.opts.sId, act, datas,_self.getNowDom());
	    if (result[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0 || result[UIConst.RTN_MSG].indexOf(UIConst.RTN_WARN) == 0) {
	   	    if (reload && reload == true) {
	   	    	_self._refreshGridBody();
	   	    }
	    }
	}
  
};
/*
 * 列表页面与后台交互方法，公用方法,传递servId的方法act
 * @param act 动作ID
 */
rh.vi.listView.prototype.doServAct = function(servId,act,reload) {
	var _self = this;
	var pkArray = _self.grid.getSelectPKCodes();
	var strs = pkArray.join(",");
	var datas = {};
	datas[UIConst.PK_KEY]=strs;
	datas = jQuery.extend(datas,this.links);
    var result = FireFly.doAct(servId, act, datas);
    if (result[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
   	    if (reload && reload == true) {
   	    	_self._refreshGridBody();
   	    }
    } 
};
/*
 * 列表页面与后台交互方法，公用方法,成功后刷新页面
 * @param act 动作ID
 */
rh.vi.listView.prototype.doActReload = function(act) {
	var _self = this;
    this.doAct(act,true);
};

/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete= function(pkArray) {
	return true;
};
/*
 * 删除后方法执行
 */
rh.vi.listView.prototype.afterDelete = function() {
};
/*
 * 根据过滤条件查询出需要数据
 */
rh.vi.listView.prototype.queryData = function() {
	//根据过滤条件查询出需要数据
	
};
/*
 * 构建按钮条
 */
rh.vi.listView.prototype._bldBtnBar = function() {
	var _self = this;
	if (typeof this._btnBar != "object") {//第一次构造
		this._btnBar = jQuery("<div></div>").addClass("rhGrid-btnBar");
	} else {
		this._btnBar.find(".rhGrid-btnBar-a").remove();
	}
	this.btns = {};
	var tempData = this._data.BTNS;
	var oneVar = UIConst.STR_YES;
	_self.listBtnGroups = {}; //缓存列表按钮组
	
	//添加刷新按钮
	if (window.ICBC) {
		if (_self.refreshImg) {
			_self.refreshImg.hide();
		}
		$("a.rhGrid-btnBar-a-refresh", _self._btnBar).remove();
//		var refreshBtn = $('<a class="rh-icon rhGrid-btnBar-a-refresh"><span class="rh-icon-inner rh-icon-inner-refresh">刷新</span><span class="rh-icon-img btn-refresh"></span></a>');
		var refreshBtn = $('<a class="rh-icon rhGrid-btnBar-a-refresh" id="TS_COMM_CATALOG-refresh" actcode="refresh" title><span class="rh-icon-inner rh-icon-inner-refresh">'+ Language.transStatic("rh_ui_gridCard_string7") +'</span><span class="rh-icon-img btn-refresh"></span></a>');
		refreshBtn.appendTo(_self._btnBar);
		refreshBtn.on("mousedown",function() {
			var _loadbar = new rh.ui.loadbar();
			_loadbar.show(true);
        	_self._clearSearchValue();
        	_self.whereData = {};
        	_self.refresh();
        	_loadbar.hideDelayed();	
        	// _self.listBarTip("刷新成功");
        });
	}
	
	//根据参数判断是否显示按钮
	if(_self._showButtonFlag == "true" && tempData){
		_self._rowBtns = []; //列表行按钮数组
		jQuery.each(tempData,function(i,n) {
			 
			if (Browser.ignoreButton(n)) {
				return;
			}
			if ((n.ACT_CODE == UIConst.ACT_BYID) && (n.ACT_TYPE == UIConst.ACT_TYPE_NOBTN)) {//非按钮&&查看
				_self._byIdFlag = true;
			}
			var showFlag = true;//按钮的只读开关
	        if ((_self._readOnly === true || _self._readOnly === "true") && (n.ACT_GROUP == oneVar)) {//页面只读&&非编辑组
	        	showFlag = false;
	        }     
			if ((n.ACT_TYPE == UIConst.ACT_TYPE_LIST) && showFlag) {
				if (n.LIST_DIS_GROUP && n.LIST_DIS_GROUP.length >= 0) {
					var groupName = n.LIST_DIS_GROUP;
					var groupCode = "";
				}
				var temp;
				if (groupName) { //存在按钮组
					groupCode = "BtnGroup_" + escape(groupName);
					var btnsGroup = (_self.listBtnGroups)[groupCode];
					//首次构造按钮组
					if (typeof (btnsGroup) == "undefined") {
						(_self.listBtnGroups)[groupCode] = _self._bldBtnGroup(groupName, groupCode, n);
						btnsGroup = (_self.listBtnGroups)[groupCode];
					}
				}
				//构建按钮
				temp = _self._bldBtn(n);
				//缓存按钮对象
				_self.btns[n.ACT_CODE] = temp;
				//处理批量编辑标志
				if (n.ACT_CODE == UIConst.ACT_BATCH_SAVE) {//批量编辑的标志
					_self._transBatchFlag = true;
				}
				//处理按钮表达式并渲染按钮
				if ((n.ACT_EXPRESSION.length > 0) && _self._excuteActExp(n.ACT_EXPRESSION) == false) {//判断操作表达式
					temp.hide();
				}
				//处理卡片只读标记
				if (n.ACT_CODE == UIConst.ACT_ADD) {//没有添加按钮则只读卡片
					_self._cardRead = false;
				}
				//渲染按钮
				if (btnsGroup) {
					btnsGroup.append(temp);
				} else {
					_self._btnBar.append(temp);
				}
			} else if ((n.ACT_TYPE == UIConst.ACT_TYPE_LISTROW) && showFlag) {//列表行按钮
				_self._rowBtns.push(n);
			}
		});
	} else {
		//将原来的按钮条替换为间隔条，改善外观
		_self._btnBar.addClass("rhGrid-btnBar__spacer").appendTo(this.content);
	}
	return this._btnBar;
};

/*
 * 构建按钮组
 * name 组按钮名称
 * code 组按钮编码
 */
rh.vi.listView.prototype._bldBtnGroup = function(name, code, actItem) {
	var _self = this;
	//构建按钮容器a标签
	var temp = jQuery("<a></a>").addClass("rh-icon").addClass("rhGrid-btnBar-a");
	temp.attr("id", _self._getUnId(code));
	temp.attr("actcode", code);
	temp.attr("title", name);
	//构建按钮文字span标签
	var labelName = jQuery("<span></span>").addClass("rh-icon-inner").text(name);
	temp.append(labelName);
	//构建按钮图标span标签
	var icon = jQuery("<span></span>").addClass("rh-icon-img").addClass("btn-budeng");
	temp.append(icon);
	//更多下拉图标
	var moreIcon = jQuery("<span></span>").addClass("rh-icon-img-more");
	labelName.append(moreIcon);
	var btnGroupCon = jQuery("<div class='rh-icon-groupCon'></div>");
	btnGroupCon.appendTo(temp);
	
	//渲染按钮
	_self._btnBar.append(temp);

	//绑定事件
	temp.bind("click",function(event){
		var btnGroupObj = jQuery(this).find(".rh-icon-groupCon");
		if(btnGroupObj.css("display") == "none"){
			_self._showBtnGroup(btnGroupObj);
		} else {
			_self._hideBtnGroup(btnGroupObj);
		}
		event.stopPropagation();
	});
	
	return btnGroupCon;
};

/*
 * 显示按钮组
 * 组对象
 */
rh.vi.listView.prototype._showBtnGroup = function(groupObj) {
	this._hideAllBtnGroup();
	groupObj.show();
};

/*
 * 隐藏按钮组
 * 组对象
 */
rh.vi.listView.prototype._hideBtnGroup = function(groupObj) {
	groupObj.hide();
};

/*
 * 关闭当前所有按钮组
 * 组对象
 */
rh.vi.listView.prototype._hideAllBtnGroup = function() {
	jQuery(".rh-icon-groupCon").hide();
}

/*
 * 构建按钮
 * btnDef 按钮定义
 * btnsCon 按钮容器
 */
rh.vi.listView.prototype._bldBtn = function(actItem) {
	var _self = this;
	//构造按钮
	var temp = jQuery("<a></a>").addClass("rh-icon").addClass("rhGrid-btnBar-a");
	temp.attr("id",_self._getUnId(actItem.ACT_CODE));
	temp.attr("actcode",actItem.ACT_CODE);
	temp.attr("title",actItem.ACT_TIP);
	_self._act(actItem.ACT_CODE,temp);
	var labelName = jQuery("<span></span>").addClass("rh-icon-inner").text(Language.transDynamic("ACT_NAME", actItem.EN_JSON, actItem.ACT_NAME));
	temp.append(labelName);
	var icon = jQuery("<span></span>").addClass("rh-icon-img").addClass("btn-" + actItem.ACT_CSS);
	temp.append(icon);
	//绑定按钮
	if (actItem.ACT_MEMO.length > 0) {
		temp.bind("click",function() {
			var _funcExt = new Function(actItem.ACT_MEMO);
			_funcExt.apply(_self);
		});
	}
	return temp;
};

/*
 * 构建底部按钮条
 */
rh.vi.listView.prototype._bldBtnBarBottom = function() {
	var _self = this;
	if (this.opts.parHandler) {
		return false;
	}
	var btns = this._btnBar.find(".rhGrid-btnBar-a");
	var _btnBar = jQuery("<div></div>").addClass("rhGrid-btnBar-bottom");
	jQuery.each(btns,function(i,n) {
		var a = jQuery(n).clone();
		a.bind("click",function() {
			var code = a.attr("actcode");
			_self.btns[code].click();
		});
		a.appendTo(_btnBar);
	});
	_btnBar.appendTo(this.contentMain);
};
/*
 * 获取列表是否含有显示按钮
 */
rh.vi.listView.prototype._isHaveShowBtn = function() {
	var _self = this;
	var btns =  this._btnBar.find(".rhGrid-btnBar-a");
	if (btns.length == 0) {
		return false;
	} else {
		return true;
	}
};
/*
 * 隐藏多选框列
 */
rh.vi.listView.prototype.hideCheckBox = function() {
	var _self = this;
	if (_self.grid && this.selectViewFlag === false) {//非查询选择
		if (this._isHaveShowBtn() === false) {
			this.grid.hideCheckBoxColum();
		}
	}
};
/*
 * 添加新的一行表格，不含数据
 */
rh.vi.listView.prototype._addNewTr = function() {
	var _self = this;
	this.grid.addNewTr();
};
/*
 * 构建树形导航区
 */
rh.vi.listView.prototype._bldNavTree = function() {
	var _self = this;
	_self.nvaTreeArray = [];
	_self.navTreeWhere = {};
	_self.navTreeWhereExt = {};
	var _defaultHei = GLOBAL.defaultFrameHei;
	function replaceId (str) {
		return str.replace(/[^\w]/gi, "_");
	};
    //jQuery(document).ready(function(){
    	//setTimeout(function() {
			if (_self.navTreeContainer.hasClass("wp0")) {
				return false;
			}
			if (_self.contentMainCont.height() > _defaultHei) {
				_defaultHei = _self.contentMainCont.height();	
			}
			//_self.contentMainCont.height(_defaultHei);
		    _self.navTree.height(_defaultHei-10);
		    var items = _self._data.ITEMS;
		    var navItems = _self._data.SERV_NAV_ITEMS;
		    if (_self._replaceNavItems && _self._replaceNavItems.length > 0) {//替换左侧导航定义
		    	navItems = _self._replaceNavItems;
		    }
		    var itemArray = navItems.split(",");
		    var countTree = 0;
		    jQuery.each(itemArray,function(i,n) {
		    	var itemCode = n;
		    	var dictId = items[n].DICT_ID;
		    	var config = items[n].ITEM_INPUT_CONFIG;

				var confArray = config.split(",");
				var dictId = confArray[0];
				var conf = confArray.slice(1);
				var confJson = StrToJson(conf.join(","));
				var configExtendDicSetting = confJson && (confJson.extendDicSetting) ? confJson.extendDicSetting:null;
				var extWhere = confJson && (confJson.EXTWHERE) ? confJson.EXTWHERE:null;
				//如果外层有替换的extWhere条件，则替换默认配置，[字典编号__EXTWHERE]
				if (_self.slimParams()[dictId + "__EXTWHERE"]) {
					extWhere = _self.slimParams()[dictId + "__EXTWHERE"];
				}
				var dictSubs = confJson && (confJson.DICT_SUBS) ? confJson.DICT_SUBS:null;//是否查询当前点的所有子
				var pId = confJson && (confJson.PID) ? confJson.PID:null;
                var expandLevelParam = 1;
				var setting = {
						rhexpand: false,
						expandLevel:expandLevelParam,
				    	showcheck: false,   
				    	url: "SY_COMM_INFO.dict.do", 
				        theme: "bbit-tree-no-lines", //bbit-tree-lines ,bbit-tree-no-lines,bbit-tree-arrows
				        rhItemCode:itemCode,
				        rhBeforeOnNodeClick: function (item,id) {//点击添加选中状态之前
				        	var nodeObj = jQuery("#" + id + "_" + replaceId(item.ID),_self.navTree);
				        	if (nodeObj.hasClass("bbit-tree-selected")) {//节点取消选中
				        		nodeObj.addClass("rh-bbit-tree-selected");
				        	}
				        },
				        onnodeclick: function(item,id) {//节点点击
				        	_self.listBarTipLoad("加载中..");
				        	//var where = " and " + item.rhItemCode + "='" + item.ID + "'";
				        	var nodeObj = jQuery("#" + id + "_" + replaceId(item.ID),_self.navTree);
				        	if (nodeObj.hasClass("rh-bbit-tree-selected")) {//节点取消选中
				        		nodeObj.removeClass("bbit-tree-selected");
				        		nodeObj.removeClass("rh-bbit-tree-selected");
				        		delete _self.navTreeWhere[item.rhItemCode];//删除条件
				        		//重置传递参数对象的值
				        		_self._transferData[item.rhItemCode] = "";
						    	_self._transferData[item.rhItemCode + "__NAME"] = "";
				        	} else if (item.ID == dictId) {
				        		delete _self.navTreeWhere[item.rhItemCode];//删除条件
				        	} else {
				        		var flag = _self.beforeTreeNodeClickLoad(item,id,dictId);
				        		if(typeof(flag) != "undefined" && !flag){
				        			if (nodeObj.hasClass("bbit-tree-selected")) {//节点取消选中
						        		nodeObj.removeClass("bbit-tree-selected");
						        		nodeObj.removeClass("rh-bbit-tree-selected");
						        		delete _self.navTreeWhere[item.rhItemCode];//删除条件
						        		//重置传递参数对象的值
						        		_self._transferData[item.rhItemCode] = "";
								    	_self._transferData[item.rhItemCode + "__NAME"] = "";
						        	}
				        			return false;
				        		}
				        		_self.navTreeWhere[item.rhItemCode] = item.ID;//保存点击条件
				        		_self.navTreeWhereExt[item.rhItemCode] = dictSubs;//扩展传递条件
				        		//增加传递参数对象值
				        		_self._transferData[item.rhItemCode] = item.ID;
						    	_self._transferData[item.rhItemCode + "__NAME"] = item.NAME;
				        	}
				        	if (item.ID == dictId) {//如果点击的是根节点，@TODO:多个根节点的点击，
				        		nodeObj.css("background","none");
				        	}
					    	_self.whereData[_self.TREE_WHERE] = _self.getNavTreeWhereStr();
					    	//_self.beforeTreeNodeClickLoad(item,id,dictId);
					    	var opts = {};
					    	opts._NOPAGEFLAG_ = "true";
						    _self.refresh(opts);
						    if (item.ID != dictId) {//非虚拟根节点传值
						    	_self._transferData[item.rhItemCode] = item.ID;
						    	_self._transferData[item.rhItemCode + "__NAME"] = item.NAME;
						    }
						    _self.afterTreeNodeClick(item,id,dictId);
					    	return false;
						}
				    };
					setting.rhLeafIcon = Tools.getTreeLeafClass(dictId);//系统默认提供
	                if (configExtendDicSetting) {
	                		setting = jQuery.extend(setting,configExtendDicSetting);//合并扩展的树形设置信息    	
	                }
	                // 0为加载所有，这个应该是可以配置的//加PID
	                var params = null;
	                if (jQuery.isEmptyObject(System.getParParams()) == false) {
	                		params = System.getParParams();
	                }
	                // 字典额外参数配置，用于传到后台字典处理类做特殊处理
	                if(confJson && !jQuery.isEmptyObject(confJson.params)) {
	                		params = jQuery.extend({}, params, confJson.params);
	                		setting.url += "?" + jQuery.param(confJson.params);
	                }
	                extWhere = Tools.parVarReplace(extWhere);
	                extWhere = Tools.systemVarReplace(extWhere);
	                extWhere = Tools.itemVarReplace(extWhere,_self.links);
	                var extWhere = Tools.parVarReplace(extWhere);
	                extWhere += _self._treeExtWhere;
	                params = jQuery.extend({}, params, {"USE_SERV_ID":_self.servId});
					setting.data = FireFly.getDict(dictId,pId,extWhere,null,null,params);//s_menuData.TOPMENU;//// 设置树的初始化数据
					var child = setting.data[0].CHILD;
				    if (child.length == 1) {
				    	setting.data = child;
				    }
				    // 异步加载条件
				    setting.dictId = dictId;
					setting.extWhere = Tools.parVarReplace(extWhere);
				    //setting.data[0]["NAME"] = n.ITEM_NAME;
				    var tree = new rh.ui.Tree(setting);
				    _self.nvaTreeArray[dictId] = tree;
					_self.navTree.append(tree.obj);
					countTree++;
					if (countTree < itemArray.length) {
						var lineSplit = jQuery("<div></div>").addClass("content-navTree-line");
						_self.navTree.append(lineSplit);
						
					}
		    });
		    //展开收起的功能实现
	       _self.navTreeContainer.hover(
	    	   function(event) {
		    	   if (jQuery(".content-navTree-close").length == 0) {
		    		 /*  var close = jQuery("<div></div>").addClass("content-navTree-close").appendTo(jQuery(this));*/
		    		   close.bind("click",function(event) {
		    			   if (jQuery(".content-navTree-close").hasClass("content-navTree-expand")) {
		    				   _self.navTreeContainer.width(_self.navTreeContainer.data("orWid")-1);
		    				   _self.contentMainCont.width(_self.contentMainCont.data("orWid"));
		    				   jQuery(".content-navTree-close").removeClass("content-navTree-expand");	 
		    				   jQuery(".content-navTree-pannel").remove();
		    			   } else {
		    				   _self.navTreeContainer.data("orWid",_self.navTreeContainer.width());
		    				   _self.contentMainCont.data("orWid",_self.contentMainCont.width());
		    				   _self.navTreeContainer.width("2%");
		    				   _self.contentMainCont.width("98%");
		    				   var pannel = jQuery("<div></div>").addClass("content-navTree-pannel").appendTo(_self.navTreeContainer);
		    				   jQuery(".content-navTree-close").addClass("content-navTree-expand");
		    			   }
		    		   });
		    	   }
			   },
			   function(event) {
	               if (jQuery(".content-navTree-expand").length == 1) {
	               } else {
	            	   jQuery(".content-navTree-close").remove();
	               }
			   }
		  );
    	//},0);
    //});
};
/*
* 业务可覆盖此方法，在导航树的点击事件加载前
*/
rh.vi.listView.prototype.beforeTreeNodeClickLoad = function(item,id,dictId) {
	
};
/*
* 业务可覆盖此方法，在导航树的点击事件执行后
*/
rh.vi.listView.prototype.afterTreeNodeClick = function(item,id,dictId) {
	
};
/*
* 获取树形组合条件
*/
rh.vi.listView.prototype.getNavTreeWhereStr = function() {
	var _self = this;
	var where = [];
	jQuery.each(_self.navTreeWhere,function(i,n) {
		var temp = {};
		temp["DICT_ITEM"] = i;
		temp["DICT_VALUE"] = n; 
		if (_self.navTreeWhereExt[i]) {//扩展配置传递
			temp["DICT_SUBS"] = _self.navTreeWhereExt[i];
		}
		where.push(temp);
	});
	return where;
};
/*
* 获取导航树对象
* dictId:字典编码
*/
rh.vi.listView.prototype.getNavTreeObj = function(dictId) {
	var _self = this;
	return _self.nvaTreeArray[dictId];
};
/*
 * 内部刷新左侧导航树方法
 */
rh.vi.listView.prototype._refreshNavTree = function() {
	if (this.navTree.hasClass("wp0")) {
		return true;
	}
	this.navTree.empty();
	this._bldNavTree();
};
/*
 * 初始化服务主数据，包括服务定义、字段、按钮等
 */
rh.vi.listView.prototype._initMainData = function() {
	if(this.opts.servDef){
		this._data = this.opts.servDef;
		FireFly.setCache(this.opts.sId,FireFly.servMainData,this._data);
	}else{
		this._data = FireFly.getCache(this.opts.sId,FireFly.servMainData);
	}
    //服务定义的标签script和css文件
    var servJS = this._data.SERV_JS;
    if (servJS && servJS.length > 0) {
	    var servStyle = GLOBAL.servStyle[this.servId];
	    if (!servStyle) {
	    	servJS += "";
	    	servJS = servJS.replace(/@urlPath@/i, FireFly.getContextPath());
		    jQuery(servJS).appendTo(jQuery("head"));
		    GLOBAL.servStyle[this.servId] = "true";
	    }
    }
    this.servName = this._data.SERV_NAME;//服务名称
	this._linkServ = this._data.LINKS || {};//关联功能信息
	this._itemData = this._data.ITEMS;
	this._querys = this._data.QUERIES;//常用查询
	this._queryModel = this._data.SERV_QUERY_MODE || 1;//查询模式
	this.enJson = this._data.EN_JSON;
	if (this._replaceQueryModel) {
		this._queryModel = this._replaceQueryModel;
	}
	
	if(this._userPvlg) {
		
		FireFly.setCache(this.opts.sId,FireFly.servMainData,this._userPvlg);
	} else {
		var userCode = System.getVar("@USER_CODE@");
//		console.log("userCode",userCode);
		if (userCode) {
			this._userPvlg = FireFly.getCache(userCode,FireFly.userPvlg);
		}
	}
};
/*
 * 获取列表的数据记录
 */
rh.vi.listView.prototype.getListData = function() {
	return this._listData;
};
/*
 * 重置当前页面的高度(对外方法)
 */
rh.vi.listView.prototype.resetSize = function() {
	this._resetHeiWid();
};
/*
 * 重置当前页面的高度，初始化时、从卡片返回列表时
 */
rh.vi.listView.prototype._resetHeiWid = function() {
	var _self = this;
	if ((this.opts.reset == false) || (this.opts.reset == "false")) {
		return true;
	}
	if (jQuery.isFunction(this.opts.resetHeiWid)) {
		_self.opts.resetHeiWid.call(_self.opts.parHandler);
	} else {
		clearTimeout(_self._resetTimer);
	    jQuery(document).ready(function(){
	    	_self._resetTimer = setTimeout(function() {
	    		var _default = GLOBAL.getDefaultFrameHei();
				_self._height = _self.content.height() + _self.listHeader.height();
				_self._width = _self.content.width();
				if (_self._height < _default) {
					_self._height = _default;
				}
				if (_self.opts.parHandler) {//关联功能的list执行
					var parHandler = _self.opts.parHandler;
				    if (_self.opts.bottomTabFlag == true) {//主单下列表
				    	var diffHei = _self._height + 70;//列表内容高度+外层tab条高度
				    	var mainHei = _self.contentMainCont.height();//列表实际高度
				    	if ((diffHei - mainHei) > 200) {
				    		diffHei = 500;
				    	}
				    	parHandler._resetHeiWid(diffHei);//调用rhCardView的方法
				    	_self._height = diffHei;//列表实际高度
				    } else if(_self.opts.sonTabFlag == true || _self.opts.sonTabFlag == "true"){//rhListExpanderView 卡片子列表
				    	parHandler._resetHeiWid();//调用rhCardView的方法
				    } else if (_self.opts.listSonTabFlag == true || _self.opts.listSonTabFlag == "true") {//列表子列表
				    	_self._height = _self._height + parHandler.listHeader.height(); //当前content高度加上父的头高度
						Tab.setFrameHei(_self._height+20);	
				    } else {
						_self._height += 20;
						parHandler._changeTabHeiWid(_self._height);
				    }
				} else {
					if (_self._height == _default) {//如果主列表页面内容小于默认值
						_self._height = _default-20;
					}
					Tab.setFrameHei(_self._height+20);	//TODO:临时处理，增加列表下面间隙
				}
	    	},0);
	    });
	}
};
/*
 * 提供外部调用刷新列表方法
 */
rh.vi.listView.prototype.refresh = function(options) {
	this.destroyUI();
	this._deletePageAllNum();
	this._refreshGridBody(options);
    //查询选择的加载后事件执行
	if (this.selectViewFlag == true) {
		this.getParHandler()._afterLoad();
	}
	//渲染排序
	this._bindClickOrderIcon(this.servId);
};
/*
 * 销毁组件
 */
rh.vi.listView.prototype.destroyUI = function() {
};
/*
 * 提供外部调用刷新列表和树方法
 */
rh.vi.listView.prototype.refreshTreeAndGrid = function(options) {
	this._refreshNavTree();
	this._refreshGridBody(options);
};
/*
 * 提供外部调用刷新列表和树方法(treeWhere,gridWhere)
 */
rh.vi.listView.prototype.refreshTreeGrid = function(treeWhere,gridWhere) {
	this.whereData[this.SEARCH_WHERE] = gridWhere;
	this.whereData["_NOPAGEFLAG_"] = "true";
	this.refresh(this.whereData);
	
	this._treeExtWhere = treeWhere;
	this._refreshNavTree();
}
/*
 * 刷新列表体不重置页面大小
 */
rh.vi.listView.prototype.refreshGridBodyNoResize = function(options) {
	var _self = this;
	if (this._linkWhere.length > 0) {
		_self.whereData[_self.LINK_WHERE] = this._linkWhere;
		_self.whereData["_linkServQuery"] = this.opts.linkServQuery || "";//关联功能过滤规则标识
	}
	if (this._extWhere.length > 0) {
		_self.whereData[_self.EXT_WHERE] = this._extWhere;
	}
	if (this._parWhere.length > 0) {//父传过来的条件
		if (_self._extWhere.length > 0) {
			_self.whereData[_self.EXT_WHERE] = _self._extWhere + Tools.parVarReplace(_self._parWhere);		
		} else {
			_self.whereData[_self.EXT_WHERE] = Tools.parVarReplace(_self._parWhere);		
		}
	}
	if (options && options._PAGE_) {
		_self.PAGE["_PAGE_"] = options._PAGE_;
	}
	var data = jQuery.extend({},_self.whereData,options);//合并条件
	data = jQuery.extend({},_self.PAGE,data);//合并分页信息
	
	if (this.opts._SELECT_) {//查询选择的话会显示不一样的列信息
		data["_SELECT_"] = this.opts._SELECT_;
	}
	if (this.opts._HIDE_) {//需隐藏的列
		options["_HIDE_"] = this.opts._HIDE_;
	}
	if (options && options._NOPAGEFLAG_ && (options._NOPAGEFLAG_ == "true")) {//删除分页信息
		delete data._PAGE_;
		delete data._NOPAGEFLAG_;
	}
	data = jQuery.extend(data,this.slimParams());
	//构造常用查询的_queryId
	if (this._queryId && (this._queryId.length > 0)) {
		data["_queryId"] = this._queryId;
	}
	this._listData =  FireFly.getPageData(this.opts.sId,data);
	this.grid.refresh(this._listData);	
	//执行工程级js方法
	this._excuteProjectJS();
	//没有按钮则自动隐藏多选框
	this.hideCheckBox();
	//处理工作流活动节点用户状态字段S_WF_USER_STATE的值。
	this._loadWfUserState();
	//加载公司领导意见
	this._loadLeaderMind();
	this._loadEmergency();
	//清除提示加载信息
	this.listClearTipLoad();
	this.afterRender();
};
/*
 * 内部刷新列表数据方法
 */
rh.vi.listView.prototype._refreshGridBody = function(options) {
	this.refreshGridBodyNoResize(options);
	//清除复选框的选中
	var headBox = this.grid.getHeadCheckBox();
	if (headBox.attr("checked")) {
		headBox.removeAttr("checked");
	}
	//重算高度
	this._resetHeiWid();
	this._renderSortIcon();
};
/*
 * 对外刷新列表数据方法
 */
rh.vi.listView.prototype.refreshGrid = function(options) {
	this._refreshGridBody();
};
/*
 * 重置刷新方法(增加刷新树形的方法)
 */
rh.vi.listView.prototype.onRefreshGridAndTree = function() {
	var _self = this;
	this.refreshGrid = function() {
		_self._refreshGridBody();
		_self._refreshNavTree();
	};
};
/*
 * 引擎最后执行
 */
rh.vi.listView.prototype._afterLoad = function() {
	//var stopWatch = new Stopwatch();
	//stopWatch.start();
	var _self = this;
	//1.填充普通查询数据
	this._fillSearchList();
	// 处理tab的表达式
	this._bldLinkServExp();
	//2.执行执行工程级js方法
	this._excuteProjectJS();	
	//3.没有按钮则自动隐藏多选框
	this.hideCheckBox();
	//4.重算高度
	this._resetHeiWid();
	//5.处理工作流活动节点用户状态字段S_WF_USER_STATE的值。
	this._loadWfUserState();
	//6.加载公司领导意见
	this._loadLeaderMind();
	//隐藏一些有优先级但不要显示的紧急程度值。
	this._loadEmergency();
	//显示返回按钮，默认隐藏
	//this._btnBarBackBtn();
	//用户按钮权限控制 工商银行考试系统
	this._checkUserPvlg();
	//7.清空加载信息提示
	Tip.clearLoad();
	//console.log(stopWatch.time() + "毫秒");
	//添加列表加载后的监听事件，用于隐藏列表添加按钮
	jQuery(document).trigger("afterListViewLoad", [this]);
	
	this.grid._bldTdWid();
	this.grid._table.colResizable();
};

rh.vi.listView.prototype.afterRender = function(){
}

/**
 * 处理工作流活动节点用户状态字段S_WF_USER_STATE的值。格式如：[{"D":"稽核部总经理","U":"c180c0c718f36e8e0118f36ec6f100bc","N":"周有扣","O":"N"}] 
 */
rh.vi.listView.prototype._loadWfUserState = function() {
	var colDef = this.grid.getColumnDef("S_WF_USER_STATE");
	//如果字段不存在则返回
	if(!colDef){
		return;
	}
	if(colDef.ITEM_LIST_FLAG != "1"){ //如果该字段不显示则返回
		return;
	}
	//取得整个列的所有行数据
	var userStateCells = this.grid.getTdItems("S_WF_USER_STATE");	
	RHWF.loadWfUserState(userStateCells, this.grid._table, "S_WF_USER_STATE");
};
/**zzx增加--2013.1.31
 * 列表页判断列表页是否存在S_HAS_PS_MIND列值，如果存在列值，则将已经签署领导意见的列用“对钩”样子的小图片替换
 * 当鼠标放到图片上后，查询出领导的意见。
 */
rh.vi.listView.prototype._loadLeaderMind = function() {
	var _viewer = this ;
	var grid = _viewer.grid;
	RHWF.showLeaderMind(grid);	
};

/**
 * 处理紧急字段
 */
rh.vi.listView.prototype._loadEmergency = function(){
	var colDef = this.grid.getColumnDef("S_EMERGENCY__NAME");
	//如果字段不存在则返回
	if(colDef){
		if(colDef.ITEM_LIST_FLAG != "1"){ //如果该字段不显示则返回
			return;
		}
		
		this.grid.getTdItems("S_EMERGENCY__NAME").each(function(){
			var cellObj = jQuery(this);
			if(jQuery.isNumeric(cellObj.text())){
				cellObj.text("");
			}
			RHWF.figuredEmergency(cellObj, true); //图片化
		});
		return;
	}
	colDef = this.grid.getColumnDef("S_EMERGENCY");
	if(colDef){
		if(colDef.ITEM_LIST_FLAG != "1"){ //如果该字段不显示则返回
			return;
		}
		
		this.grid.getTdItems("S_EMERGENCY").each(function(){
			var cellObj = jQuery(this);
			if(jQuery.isNumeric(cellObj.text())){
				cellObj.text("");
			}
			RHWF.figuredEmergency(cellObj, false); //图片化
		});
		
	}
};

/*
 * 填充普通查询的下拉列表
 */
rh.vi.listView.prototype._fillSearchList = function() {
    var _self = this;
    var selects = [];
    var hides = [];
    if (_self.selectViewFlag == true) {
    	selects = _self.opts._SELECT_.split(",");//查询选择的字段
    	hides = _self.opts._HIDE_.split(",");//查询选择的隐藏字段
    }
	jQuery.each(this._itemData,function(i,n) {
		if (n.ITEM_TYPE != "3") {
			if (_self.selectViewFlag == true) {//查询选择
				if ((jQuery.inArray(n.ITEM_CODE,selects) > -1) && (jQuery.inArray(n.ITEM_CODE,hides) == -1)) {
					var temp = jQuery("<option value='" + n.ITEM_CODE + "'>" + Language.transDynamic("ITEM_NAME", n.EN_JSON,n.ITEM_NAME) + "</option>").appendTo(_self._searchSel);
					if ((n.ITEM_INPUT_MODE == UIConst.FITEM_INPUT_DICT) 
							|| (n.ITEM_INPUT_TYPE == UIConst.FITEM_ELEMENT_SELECT)
							|| (n.ITEM_INPUT_TYPE == UIConst.FITEM_ELEMENT_RADIO)
							|| (n.ITEM_INPUT_TYPE == UIConst.FITEM_ELEMENT_CHECKBOX)) {//字典
						temp.attr("itemtype","dict");
					}
				}			
			} else { //默认情况
				if (n.ITEM_LIST_FLAG == UIConst.STR_YES) {
					var temp = jQuery("<option value='" + n.ITEM_CODE + "'>" + Language.transDynamic("ITEM_NAME", n.EN_JSON,n.ITEM_NAME) + "</option>").appendTo(_self._searchSel);
					if ((n.ITEM_INPUT_MODE == UIConst.FITEM_INPUT_DICT) 
							|| (n.ITEM_INPUT_TYPE == UIConst.FITEM_ELEMENT_SELECT)
							|| (n.ITEM_INPUT_TYPE == UIConst.FITEM_ELEMENT_RADIO)
							|| (n.ITEM_INPUT_TYPE == UIConst.FITEM_ELEMENT_CHECKBOX)) {//字典
						temp.attr("itemtype","dict");
					}
				}
			}
		}
	});
	if (_self._searchSel 
			&& _self._searchSel.length > 0
			&& $("option",_self._searchSel).length == 0) {
		_self._searchSel.closest(".searchDiv").hide();
	}
};
/*
 * 获取按钮对象
 */
rh.vi.listView.prototype.getBtn = function(actCode) {
    var _self = this;
    if (this.btns[actCode]) {
    	return this.btns[actCode];
    } else {
    	return jQuery();
    }
};
/*
 * 列表加载后执行工程级js方法
 */
rh.vi.listView.prototype._excuteProjectJS = function() {
	var _self = this;
    this._servPId = this._data.SERV_PID || "";//父服务ID
    var loadArray = this._data.SERV_LIST_LOAD_NAMES.split(",");
    for (var i = 0;i < loadArray.length;i++) {
    	if (loadArray[i] == "") {
    		return;
    	}
    	load(loadArray[i]);
    }
	function load(value) {
		var pathFolder = value.split("_");
		var lowerFolder = pathFolder[0].toLowerCase();
	    var jsFileUrl = FireFly.getContextPath() + "/" + lowerFolder + "/servjs/" + value + "_list.js";
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
	            } catch(e){
//	            	alert("执行列表js错误：" + e.message);
	            	alert(Language.transStatic("rhListView_string16") + e.message);
	            }
	        },
	        error: function(){;}
	    });			
	};
};
/*
 * 解析操作表达式
 */
rh.vi.listView.prototype._excuteActExp = function(tmpExp) {
	var _self = this;
    //替换系统变量
	if (tmpExp.indexOf(";") > 0) {//如果有显示控制和后台操作表达式一起控制
		var expArray = tmpExp.split(";");
		tmpExp = expArray[0];
	}  
    tmpExp = Tools.systemVarReplace(tmpExp);
	tmpExp = Tools.itemVarReplace(tmpExp,_self.links);//关联定义的变量替换
	if (typeof this._queryId == "string") {//常用查询的替换变量 #系统#
		var normalData = {};
		normalData[UIConst.QUERY_ID] = _self._queryId;
		tmpExp = Tools.itemVarReplace(tmpExp,normalData);
	}
    //表达式是否为true
    var actExp = eval(tmpExp);
    return actExp;
};
rh.vi.listView.prototype._locHref = function(target) {
	window.location.hash = "href-" + target;
};
/*
 * 获取当前页面的父句柄
 */
rh.vi.listView.prototype.getParHandler = function() {
	return this.opts.parHandler;
};
/*
 * 给列表增加红头文字
 */
rh.vi.listView.prototype.addRedHeader = function(text, style) {
	jQuery(".redHeader", this.content).remove();
	jQuery("<div class='redHeader' style='" + (style ? style : "") + "'>" + text + "</div>")
		.insertBefore(this._btnBar);
};
/*
 * 让当前页面处于屏蔽状态
 */
rh.vi.listView.prototype.shield = function() {
	this.loadBar = new rh.ui.loadbar();
	this.loadBar.show();
};
/*
 * 让当前页面取消屏蔽状态
 */
rh.vi.listView.prototype.shieldHide = function() {
	this.loadBar.hide();
};
/*
 * 设置父关联参数对象
 */
rh.vi.listView.prototype.setParParams = function(data) {
	this._parVar = data;
	System.setParParams(data);
};
/*

 * 获取参数对象，过滤掉含有句柄对象的结果
 */
rh.vi.listView.prototype.slimParams = function() {
	var params = {};
	if (!jQuery.isEmptyObject(this.params)) {
		jQuery.each(this.params,function(i,n) {
			if (i == "handler" || i == "viewer" || i == "callBackHandler" || i == "closeCallBackFunc") {
			} else {
				params[i] = n;
			}
		});
	}
	jQuery.each(this.links,function(i,n) {//关联参数
		params[i] = n;
	});
	return params;
};
/*
 * 获取参数对象
 */
rh.vi.listView.prototype.getParams = function() {
	return this.params;
};
/*
 * 扩展_extWhere参数设置
 */
rh.vi.listView.prototype.setExtWhere = function(extWhere) {
	this._extWhere = extWhere;
};
/*
 * 设置列表标题
 */
rh.vi.listView.prototype.setTitle = function(title) {
    this.listHeader.find(".conHeaderTitle-span").text(title);
};

/*
 * 返回查询ID
 */
rh.vi.listView.prototype.getQueryId = function() {
    return this._queryId;
};
/**
 * 关联服务打开卡片后返回按钮设定
 */
rh.vi.listView.prototype._btnBarBackBtn = function(){
	if ((System.getVar("@C_SY_PJ_BACKBTN@") == "true") && (this.getParHandler() == null)) {//顶层页面自动关闭
		var actCode = "back";
		var temp = jQuery("<a></a>").addClass("rh-icon").addClass("rhGrid-btnBar-a");
		temp.attr("id",GLOBAL.getUnId(actCode,this.servId));
		temp.attr("actcode","back");
//		temp.attr("title","返回");
		temp.attr("title",Language.transStatic("rh_ui_gridCard_string4"));
		this._act(actCode,temp);
//		var labelName = jQuery("<span></span>").addClass("rh-icon-inner").text("返回");
		var labelName = jQuery("<span></span>").addClass("rh-icon-inner").text(Language.transStatic("rh_ui_gridCard_string4"));
		temp.append(labelName);
		var icon = jQuery("<span></span>").addClass("rh-icon-img").addClass("btn-back");
		temp.append(icon);
		this._btnBar.append(temp);
	} else {
		if (this.backImg) {
			this.backImg.show();
		}
	}
};
/*
 * 执行关联服务定义的操作表式
 */
rh.vi.listView.prototype._bldLinkServExp = function() {
	var _self = this;
	   jQuery.each(_self._linkServ,function(i,n) {
			if (n.LINK_SHOW_POSITION == 4) { //列表TAB关联
				  var exp = n.LINK_EXPRESSION;
				  var sid = n.LINK_SERV_ID;
				  if (exp && exp.length > 0) {
					  var flag = _self._excuteActExp(exp);
					  if (flag == false) {
						  _self.tabHide(sid);
					  }
				  }
			}
	   });
};
/*
 * 设置子tab的触发
 */
rh.vi.listView.prototype.tabFocus = function(sid) {
	var _self = this;
	setTimeout(function() {
		var topObj = jQuery("li.rhList-tabs__li[sid='" + sid + "']",_self.mainUL);
		if (topObj.length == 1) {//头部关联
			topObj.click();
		}
	},500);
};
/*
 * 设置列表tab的隐藏
 */
rh.vi.listView.prototype.tabHide = function(sid) {
	var _self = this;
	var topObj = jQuery("li.rhList-tabs__li[sid='" + sid + "']",_self.mainUL);
	if (topObj.length == 1) {//头部关联
		  var index = jQuery(".rhList-tabs__liLi",_self.mainUL).index(topObj);
		  topObj.hide();
	}
};

/*
 * 获取用户权限(工行考试系统 权限管理)
 */
rh.vi.listView.prototype.getUserPvlg = function(){
	var _self = this;
	return _self._userPvlg;
	
};

/*
 * 用户按钮功能权限(工行考试系统 权限管理)
 */
rh.vi.listView.prototype._checkUserPvlg = function() {
	var _self = this;
	console.log("全部权限",_self._userPvlg);
	var odeptLv = System.getVar("@ODEPT_LEVEL@",0);
	var deptCode = System.getVar("@DEPT_CODE@");
	var deptPath = System.getVar("@CODE_PATH@");

	var servPvlg ;
	if(_self._userPvlg){
		
		servPvlg = _self._userPvlg[_self.servId+"_PVLG"]
	}
	console.log(this.servId+"权限",servPvlg);
	
	if("admin" != System.getVar("@LOGIN_NAME@")){
	
	for(var key in servPvlg) {
			
		if(servPvlg[key] == 0) {
//			console.log(key,false);
			$("a[actcode='"+key+"']").remove();
		} else {
				var ispvlg = false; // 是否有权限
				
				var optPvlg = servPvlg[key]; // 角色关联部门
				
				if(!ispvlg) {
					var orglv = optPvlg.ROLE_ORG_LV; // 关联部门层级
					if(orglv) {
						
						var orglvArg = orglv.split(",");
						
						for(var i in orglvArg) {
							if((odeptLv-1) >= orglvArg[i]) { // 用户当前机构层级 >= 关联部门层级 表示有操作权限
								ispvlg = true;
								break;
							}
						}
					}
				}
				
				if(!ispvlg) {
					var dcode = optPvlg.ROLE_DCODE; // 自定义关联部门
					
					if(dcode) {
						
						var dcodeArg = dcode.split(",");
						
						for(var i in dcodeArg) {
							
							var start = deptPath.indexOf(dcodeArg[i]);
							// 截取当前用户机构层级  关联部门机构
							var orgPath = "^"+deptPath.substring(start,deptPath.length)+"^";
							
							if(orgPath.indexOf("^"+deptCode+"^") >= 0) {
								ispvlg = true;
								break;
							}
						}
					}
				}
				
				if(!ispvlg) {
					$("a[actcode='"+key+"']").remove();
//					console.log(key,false);
				} else {
					
//					console.log(key,true);
				}
		}
		
	}
	}
};




