/** 手机卡片页面渲染引擎 */
GLOBAL.namespace("mb.vi");
var _focus_ = false;
var _moved_ = false;
var _screenHei_ = document.documentElement.clientHeight;
console && console.log("_focus_:" + _focus_);
mb.vi.cardView = function(options) {
   var defaults = {
		"id":options.sId + "-mbCardView",
		"act":"",
		"sId":"",
		"pCon":null,
		"pId":"",
		"parHandler":null,
		"readOnly":false,
		"saveReturn":false,
		"servDef":null //服务定义数据
	};
   this._pkCode = options[UIConst.PK_KEY] || "";
   this.opts = jQuery.extend(defaults,options);
   this.servId = this.opts.sId;
   this._source = this.opts.source; //是否手机app方式
   this._platform = this.opts.platform; //设备系统平台
   this._pCon = this.opts.pCon;
   this._parHandler = this.opts.parHandler;
   this._actVar = this.opts.act;
   this._height = "";
   this._width = "";
   this._parentRefreshFlag = false;
   this._readOnly = this.opts.readOnly;
   this.dialogId = GLOBAL.getUnId("winDialog",this.opts.sId);
   this.tabsId = GLOBAL.getUnId("winTabs",this.opts.sId);
   this._mainTab = GLOBAL.getUnId("mainTab",this.opts.sId);
   // 卡片上的文件组件数组
   this._cardFile = {};
   this.topTitle = null;
};


/*
 * 显示卡片页面，主方法
 */
mb.vi.cardView.prototype.show = function() {
   this._initMainData();
   this._tabLayout();
   this._bldCardLayout();
   this._afterLoad();
};
mb.vi.cardView.prototype._initMainData = function() {
	if(this.opts.servDef){
		this._data = this.opts.servDef;
		FireFly.setCache(this.servId,FireFly.servMainData,this._data);
	}else{
		this._data = FireFly.getCache(this.servId,FireFly.servMainData);
	}
    this._items = this._data.ITEMS; //字段定义信息
    this._linkServ = this._data.LINKS || {};//关联功能信息
    this.servKeys = this._data.SERV_KEYS;
    this._fileFlag = this._data.SERV_FILE_FLAG || 2;
    this.servName = this._data.SERV_NAME || "";
    this._fileFlag = this._data.SERV_FILE_FLAG || 2;
    this._servCardLoad = this._data.SERV_CARD_LOAD || "";//卡片加载js
};
/*
 * 布局卡片tabs和主功能页
 */
mb.vi.cardView.prototype._tabLayout = function() {
    var _self = this;
	this.top = jQuery("<div></div>").addClass("mbTopBar").appendTo(this._pCon);

	var table = jQuery("<table class='mbTopBar-table'></table>").appendTo(this.top);
    var tr = jQuery("<tr></tr>").appendTo(table);
	var left = jQuery("<td class='mbTopBar-left'></td>").appendTo(tr);
	this.backA = left;
	var center = jQuery("<td class='mbTopBar-center'></td>").appendTo(tr);
	//this.right = jQuery("<td class='mbTopBar-right'></td>").appendTo(tr);
	
	this.back = jQuery("<div>返回</div>").addClass("mbTopBar-back").appendTo(left); 
    // this.back.html(UIConst.FONT_STROKE_BACK);
    left.bind("click", function(e){
    	_self.returnBack.call(_self, e, false);
    });

    //this.home = jQuery("<div>主页</div>").addClass("mbTopBar-refresh").appendTo(this.right);	
    //this.home.html(UIConst.FONT_STROKE_HOME);
//    var homePage = FireFly.getConfig("HOMEPAGE");
    //this.right.bind("click",function() {
	    	//_self.home.html(UIConst.FONT_STROKE_LOAD);
//	    if (homePage) { // 跳转到配置的主页
//	    		window.location.href = homePage.CONF_VALUE;
//	    } else {
//	    		left.addClass("mbTopBar-backActive");
//	    		history.go(-2);
//	    }
    	//window.location.href = FireFly.getContextPath() + "/sy/comm/desk-mb/desk-mb.jsp";
    //});
    this.mainContainer = jQuery("<div></div>").addClass("mbCard-container").appendTo(this._pCon);
	if ((this._source == "app") && (this._platform == "Android")) {
		this.home.hide();
		if (this._platform == "Android") {
			this.back.hide();
		}
	} 
};

/*
 * 构建卡片布局，包括form和下方关联的功能
 */
mb.vi.cardView.prototype._bldCardLayout = function() {
	var _self = this;
    this._bldBtnBar();
    this._bldForm();
    if (this._fileFlag == UIConst.YES) {//卡片级文件
    	if (this._data["SERV_SRC_ID"]) {
			this._bldCardFile(this._data["SERV_SRC_ID"]);
    	}
    }

};

/*
 * 模拟点击返回按钮
 */
mb.vi.cardView.prototype.backClick = function(refreshFlag) {
	this.returnBack(null, refreshFlag);
};

mb.vi.cardView.prototype.returnBack = function(e, refreshFlag) {
	var _self = this;
	_self.backA.addClass("mbTopBar-backActive");
	
	var parHref = window._parent.location.href;
	if (parHref.indexOf("token") > 0 && parHref.indexOf("device") > 0) {
		window.location.href = window.location.href + "#close.html";
		window.location.reload();
		if (window.close && typeof(window.close) == "function") {
			window.close();
		}
		return;
	}
	
	if (refreshFlag || _self._parentRefreshFlag) {
		setTimeout(function(){
			if (document.referrer) {
				window.location.href=document.referrer;
			} else {
				window.history.go(-1);
				window.history.go(0);
			}
		}, 30);
	} else {
		history.go(-1);
	}
};

/*
 * 构造卡片级文件显示
 */
mb.vi.cardView.prototype._bldCardFile = function(sId) {
	var _self = this;
	var fileData = null;
	if (this._actVar == UIConst.ACT_CARD_MODIFY) {
		fileData = FireFly.getCardFile(sId ? sId : this.servId,this._pkCode,"");
	}
	if (fileData) {
	    //var closeHrStr = "<span class='mbCard-hr-close fontomasRegular'>" + UIConst.FONT_STROKE_close + "</span>";
	    var set = jQuery("<div></div>").addClass("mbCard-form-set").appendTo(_self.mainContainer);
	    var hr = jQuery("<div></div>").html("关联文件").addClass("mbCard-form-hr");
	    set.prepend(hr);
//		hr.bind("click",function() {
//			_self.form._bindHrClick(jQuery(this));
//		});
	    var con = jQuery("<div></div>").addClass("mbCard-file-con mb-shadow-3 mb-radius-9").appendTo(set);
	    var ul = jQuery("<ul></ul>").addClass("mbCard-file-ul").appendTo(con);
	    if (jQuery.isEmptyObject(fileData)) {
	    	jQuery("<span>无关联文件！</span>").appendTo(ul);
	    } else {
	    	jQuery.each(fileData,function(i,n) {
	    		// 通过后缀名找到小图标
	    		var name = n.FILE_NAME;
	    		var icon = Icon[Tools.getFileSuffix(name)];
	    		if (!icon) {
	    			icon = Icon["unknown"];
	    		}
	    		//alert(JsonToStr(n));
	    		var span = "<span class='mbCard-file-icon " + icon + "'></span>";
	    		var a = "<a href='/file/" + n.FILE_ID + "' class='mbCard-file-a'>" + name + "</a>";
	    		
	    		var size = "";
	    		if (parseInt(n.FILE_SIZE) != 0) {
	    			size = Math.ceil(parseInt(n.FILE_SIZE) / 1024) + "KB";
	    		}
	    		var size = "<span class='mbCard-file-size'>" + size + "</span>";
	    		var time = "<span class='mbCard-file-time'>" + n.S_MTIME + "</span>";
	    		var li = jQuery("<li></li>").html(span + a).appendTo(ul);
	    		var extend = jQuery("<div></div>").html(size + time).appendTo(li);
	    	});
	    	
	    }
	}
};
/*
 * 页面构造完成后，引擎执行动作
 */
mb.vi.cardView.prototype._afterLoad = function() {
	var _self = this;
	if (this.topTitle == null) {
		this.servName = this._data.SERV_NAME;
		var title = this.servName;
		var center = this.top.find(".mbTopBar-center");
		this.topTitle = jQuery("<div></div>").text(title).addClass("mbTopBar-title").appendTo(center);	
		jQuery("<div>" + title + "</div>").addClass("mbTopBar-title-all mbTopBar-title-all-none").appendTo(jQuery(".mbTopBar"));
		this.topTitle.bind("click",function() {
			var all = jQuery(".mbTopBar").find(".mbTopBar-title-all");
			if (all.hasClass("mbTopBar-title-all-none")) {
				all.fadeIn();
				all.removeClass("mbTopBar-title-all-none");
			} else {
				all.fadeOut();
				all.addClass("mbTopBar-title-all-none");
			}
		});
	}
	//工作流控制
	if (typeof(_self.itemValue("S_WF_INST")) != "undefined" && _self.itemValue("S_WF_INST").length >0 ) {
	    var opts = {};
	    opts["sId"] = this.servId;
	    opts[UIConst.PK_KEY] = this._pkCode;
	    opts["parHandler"] = this;
	    this.wfCard = new mb.vi.wfCardView(opts);
	    this.wfCard.render();
	}
	this._afterBtnLoad(); //执行加载完按钮方法
	this._excuteProjectJS();
	//临时改法(防止少部分iphone终端顶键盘时的问题)
	jQuery("body")[0].addEventListener("touchstart", function(event){
		_focus_ = false;
		//
		var tar = event.target;
		var tarObj = jQuery(tar);
		if (tar && (tar.nodeName == "TEXTAREA" || (tar.nodeName == "INPUT" && tarObj.attr("type") == "text" && !tarObj.hasClass("rhMobiScrollDatePicker")))) {
			
			if (!tarObj.hasClass("mbCard-item-disabled")) {
				_focus_ = true;
			}
		} else if (tar && tar.className == "mbCard-form-rightTd") {
			var childObj = tarObj.children().first();
			if (childObj[0].nodeName == "TEXTAREA" || (childObj[0].nodeName == "INPUT" && childObj.attr("type") == "text" && !tarObj.hasClass("rhMobiScrollDatePicker"))) {
				if (!tarObj.children().first().hasClass("mbCard-item-disabled")) {
					_focus_ = true;
				}
			}
		}
//		//
//		var topBar = jQuery(".mbTopBar");
//		var btnBar = jQuery(".mbCard-btnBar");
//		if (!_focus_) {
//			setTimeout(function(){
//				if (!_focus_) {
//					if (topBar.length == 1) {
//						topBar.show();
//					}
//					if (btnBar.length == 1) {
//						btnBar.show();
//					}
////					var st = jQuery("body").scrollTop();
////					jQuery("body").scrollTop(st-1);
//				}
//			}, 600);
//		} else {
//			setTimeout(function(){
//				if (topBar.length == 1) {
//					topBar.hide();
//				}
//				if (btnBar.length == 1) {
//					btnBar.hide();
//				}
//				setTimeout(function(){
//					if (topBar.length == 1) {
//						topBar.hide();
//					}
//					if (btnBar.length == 1) {
//						btnBar.hide();
//					}
////					var st = jQuery("body").scrollTop();
////					jQuery("body").scrollTop(st+1);
//				}, 400);
//			}, 0);
//		}
	}, true);
	jQuery("body")[0].addEventListener("touchmove", function(event){
		_focus_ = false;
		_moved_ = true;
	}, true);
	jQuery("body")[0].addEventListener("touchend", function(event){
		if (_moved_) {
			setTimeout(function(){
				_moved_ = false;
			}, 100);
			return;
		}
		//
		var topBar = jQuery(".mbTopBar");
		var btnBar = jQuery(".mbCard-btnBar");
		if (!_focus_) {
			setTimeout(function(){
				if (!_focus_) {
					if (topBar.length == 1) {
						topBar.show();
					}
					if (btnBar.length == 1) {
						btnBar.show();
					}
//					var st = jQuery("body").scrollTop();
//					jQuery("body").scrollTop(st-1);
				}
			}, 1000);
		} else {
			setTimeout(function(){
				if (topBar.length == 1) {
					topBar.hide();
				}
				if (btnBar.length == 1) {
					btnBar.hide();
				}
				setTimeout(function(){
					if (topBar.length == 1) {
						topBar.hide();
					}
					if (btnBar.length == 1) {
						btnBar.hide();
					}
//					var st = jQuery("body").scrollTop();
//					jQuery("body").scrollTop(st+1);
				}, 400);
			}, 0);
		}
	}, true);
	window.addEventListener("resize", function(event){
		var currScreenHei = document.documentElement.clientHeight;
		if (Math.abs(_screenHei_ - currScreenHei) < 10 && currScreenHei > 400) {
			var topBar = jQuery(".mbTopBar");
			var btnBar = jQuery(".mbCard-btnBar");
			setTimeout(function(){
				if (jQuery(".mbCard-btnBar:visible").length == 1) {
					return;
				}
				if (topBar.length == 1) {
					topBar.show();
				}
				if (btnBar.length == 1) {
					btnBar.show();
				}
			}, 1000);
		}
	}, true);
};

/*
 * 根据动作绑定相应的方法
 * @param aId 动作ID
 */
mb.vi.cardView.prototype._act = function(aId,aObj) {
	var _self = this;
	var taObj = aObj;
	switch(aId) {
		case UIConst.ACT_SAVE://保存
		    taObj.bind("click",function() {
		    	//taObj.addClass("mbBotBar-nodeActive");
		    	taObj.addClass("mbBotBar-activeColor");
		    	//_self.home.html(UIConst.FONT_STROKE_LOAD);
			    setTimeout(function() {
			    		if (_self.form.validate()) {
			    			_self._saveForm();
			    		} else {
			    			_self.showTipError("校验失败，请检查您的输入！");
			    		}
		     	},0);

		    });  
		    break;
		case "test2"://test
		    taObj.bind("click",function(event) {
		    	var options = {"sId":"SY_ORG_USER"};
//		     	var dict = new rh.vi.rhDictTreeView(options);
//		     	dict.show(event);
		     	var select = new rh.vi.rhSelectListView(options);
		     	select.show(event);
		    });  
		    break;
	}	
};
/*
 * 保存form修改的数据
 */
mb.vi.cardView.prototype._saveForm = function() {
	var _self = this;
	var changeData = _self.form.getModifyData();
    if (jQuery.isEmptyObject(changeData)) {
    	_self.clearActive();
    	this.showTipError("没有修改数据，未做提交！");
    	return false;
    } else { // 有修改才做校验
    }
    if (_self._actVar == UIConst.ACT_CARD_MODIFY) {//修改
     	   changeData[UIConst.PK_KEY] = this._pkCode;	
     	   if (_self.itemValue("S_MTIME")) {
     		   changeData["S_MTIME"] = _self.itemValue("S_MTIME");	
     	   }
     	   var resultData = FireFly.cardModify(_self.servId,changeData);
     	   if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
     		   _self.showTip("已保存！");
     	   	   _self._pkCode = resultData[_self.servKeys];
               _self.refresh();
               _self.setParentRefresh();
     	   } else if (resultData[UIConst.RTN_MSG]) {
     		  _self.showTipWarn(resultData[UIConst.RTN_MSG].split(",")[1]);
     	   }
     } else if (_self._actVar == UIConst.ACT_CARD_ADD) {//添加
    	 	changeData = _self.form.getAllItemsValue();
    	   	var resultData = FireFly.cardAdd(_self.opts.sId,changeData);
     	   if (resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
     		  _self.showTip("已保存！");
     	   	  _self._pkCode = resultData[_self.servKeys];
     	   	  _self._actVar = _self.opts.act = UIConst.ACT_CARD_MODIFY;
     	   	  _self.refresh();
 	   	  	  _self.setParentRefresh();
     	   } else if (resultData[UIConst.RTN_MSG]) {
     		  _self.showTipWarn(resultData[UIConst.RTN_MSG].split(",")[1]);
     	   }
     }
     this.clearActive();
 	return true;
};
mb.vi.cardView.prototype.clearActive = function() {
	var _self = this;
    setTimeout(function() {
    	jQuery(".mbBotBar-activeColor").removeClass("mbBotBar-activeColor");
    	//_self.home.html(UIConst.FONT_STROKE_HOME);
    },100);
}
mb.vi.cardView.prototype.showTip = function(msg) {
	 var _self = this;
     var tip = jQuery("<div></div>").text(msg).addClass("mbTopBar-tip mb-radius-9").appendTo(jQuery("body"));
     setTimeout(function() {
    	 tip.remove();
     },4000);
};
mb.vi.cardView.prototype.showTipWarn = function(msg) {
	var _self = this;
    var tip = jQuery("<div></div>").text(msg).addClass("mbTopBar-tip-warn mb-radius-9").appendTo(jQuery("body"));
    setTimeout(function() {
   	    tip.remove();
    },4000);
};
mb.vi.cardView.prototype.showTipError = function(msg) {
	var _self = this;
    var tip = jQuery("<div></div>").text(msg).addClass("mbTopBar-tip-error mb-radius-9").appendTo(jQuery("body"));
    setTimeout(function() {
   	    tip.remove();
    },4000);
};
/*
 * 卡片页面与后台交互方法，公用方法
 * @param act 动作ID
 * @param codes 需传递到后台的字段编号，多个以逗号分隔；没有codes，则默认传递所有字段
 * @parem async 是否异步
 */
mb.vi.cardView.prototype.doAct = function(act,codes,reload,extendData,async) {
	var _self = this;
	var datas = {};
	if (codes) {
		datas = this.itemValue(codes);
    }
	datas[UIConst.PK_KEY] = this._pkCode;
	if (extendData) {
		datas = jQuery.extend(datas,extendData);
	}
	
	if(async) {// 异步
		FireFly.doAct(this.opts.sId, act, datas, true, true, function(result){
			if (result[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
		   	    if (reload && reload == true) {
		   	    	_self.refresh();
		   	    }
		    } 
		});
	} else {
	    var result = FireFly.doAct(this.opts.sId, act, datas);
	    if (result[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
	   	    if (reload && reload == true) {
	   	    	_self.showTip(result[UIConst.RTN_MSG].substring(3));
	   	    	_self.refresh();
	   	    }
	    } 
	}
};
mb.vi.cardView.prototype.doActReload = function(act,codes) {
    this.doAct(act,codes,true);
};

/*
 * 构建卡片按钮条
 */
mb.vi.cardView.prototype._bldBtnBar = function() {
    var _self = this;
    this.btnSpace = 4; //const
    this.temlBtnStore = {}; //临时按钮保存
    this.btns = {};
    this.btnBar = jQuery("<div></div>").addClass("mbBotBar mbCard-btnBar").css("display","none"); //底普通按钮条
    var oneVar = UIConst.STR_YES;
    var moreBtns = {};    
    var tempData = this._data.BTNS;
	jQuery.each(tempData,function(i,n) {
		if ((n.ACT_TYPE == UIConst.ACT_TYPE_CARD) && n.S_FLAG == oneVar && n.ACT_MOBILE_FLAG == "1") {//卡片按钮 && 启用  && 移动版ACT_MOBILE_FLAG
			var btn = _self.bldBtn(n);
    		_self.addBtn(n.ACT_CODE, btn);
		}
	});
    _self.btnBar.appendTo(this.mainContainer);
};

/*
 * 
 */
mb.vi.cardView.prototype.bldBtn = function(btnItem, bindEvent, handler) {
	var _self = this;
	//构造按钮外层容器
	var nodeCon = jQuery("<div></div>").addClass("mbBotBar-con");
	//构造按钮节点
	var node = jQuery("<div></div>").addClass("mbBotBar-node").appendTo(nodeCon);
	node.attr("id",GLOBAL.getUnId(btnItem.ACT_CODE,_self.servId));
	_self.btns[btnItem.ACT_CODE] = node;
	//构造图标
	var iconCss = btnItem.ACT_CSS;
	if (iconCss.length == 0) {
		iconCss = "default";
	}
	var iconStr = "mb-btn-" + iconCss;
	var icon = jQuery("<div></div>").addClass("mbBotBar-node-icon " + iconStr).appendTo(node);
	//构造说明
	var label = jQuery("<div></div>").addClass("mbBotBar-node-text").appendTo(node);
	label.text(btnItem.ACT_NAME);
	//绑定事件
	if (bindEvent && typeof(bindEvent) == "function") {
		nodeCon.bind("click", function(event){
			bindEvent.call(handler?handler:_self, event, btnItem);
		});
	} else {
		//绑定默认事件
		if (btnItem.ACT_CODE && btnItem.ACT_CODE.length > 0) { 
			_self._act(btnItem.ACT_CODE, nodeCon);
		}
		//绑定扩展事件，暂支持少数按钮
		if (btnItem.ACT_MEMO && btnItem.ACT_MEMO.length > 0) {
			nodeCon.bind("click",function() {
				var _funcExt = new Function(btnItem.ACT_MEMO);
				_funcExt.apply(_self);
			});
		}
	}
	return nodeCon;
}

/*
 * 构造组按钮（底部按钮条上的组按钮）
 */
mb.vi.cardView.prototype.bldGroupBtn = function(item, func, handler) {
	var _self = this;
	var openMore = function(){
		if(func && typeof(func) == "function"){
			if(!func.call(handler?handler:_self)){
				return false;
			}
		}
		jQuery("#GROUP_" + item.ACT_CODE, _self.mainContainer).addClass("mbBotBar-more-active").show();
		_self.bindEvent(document, "click", _self.watchBtnGroupHiding, true);
	};
	return _self.bldBtn(item, openMore);
};

/*
 * 绑定事件封装
 */
mb.vi.cardView.prototype.bindEvent = function(target, eventName, handler, isCapture){
	var _self = this;
	var _h;
    var eventHandler = function(e){
        handler.call(_self, e, _h);
    };
    _h = eventHandler;
    if (window.attachEvent) {//IE
        target.attachEvent("on" + eventName, eventHandler, isCapture);
    } else {
        target.addEventListener(eventName, eventHandler, isCapture);
    }
};

/*
 * 监控按钮组是否需要关闭及关闭方式
 */
mb.vi.cardView.prototype.watchBtnGroupHiding = function(e, handler){
	var _self = this;
	var tar = e.target;
	var groupObj = jQuery(".mbBotBar-more-active");
	if (tar && tar.className.indexOf("mbBotBar") >= 0) { //点击的是底部按钮及按钮组中的按钮
		//
		setTimeout(function(){
			groupObj.removeClass("mbBotBar-more-active").hide();
		}, 30);
	} else { //其他
		if (e.stopImmediatePropagation) {
			e.stopImmediatePropagation();
		} else {
			e.stopPropagation();
		}
		e.preventDefault();
		setTimeout(function(){
			groupObj.removeClass("mbBotBar-more-active").hide();
		}, 30);
	}
	document.removeEventListener("click", handler, true);
};

/*
 * 构造按钮组（点击组按钮展开的按钮列表）
 */
mb.vi.cardView.prototype.bldBtnGroup = function(groupCode) {
	var _self = this;
	//构造
	var groupBar = jQuery("<div></div>").attr("id","GROUP_" + groupCode).addClass("mbBotBar-more"); //底更多按钮条
	var cancel = jQuery("<div>取消</div>").addClass("mbBotBar-more-cancel").appendTo(groupBar);
	//渲染
	groupBar.appendTo(_self.mainContainer);
	//绑定
	cancel.bind("click",function() {
		//groupBar.removeClass("mbBotBar-more-active").hide();
	});
};

mb.vi.cardView.prototype.addBtn = function(nodeKey, nodeCon, unCountFlag) {
    var _self = this;
    if(!unCountFlag){
	    if (_self.temlBtnStore.COUNT) {
	    	_self.temlBtnStore.COUNT = _self.temlBtnStore.COUNT + 1
	    } else {
	    	_self.temlBtnStore.COUNT = 1;
	    }
    }
    _self.temlBtnStore[nodeKey] = nodeCon;
};

mb.vi.cardView.prototype._addBtnToBar = function(nodeCon) {
	var _self = this;
	nodeCon.appendTo(_self.btnBar);	
};

mb.vi.cardView.prototype._addBtnToGroup = function(groupCode, nodeCon) {
	var _self = this;
	nodeCon.appendTo(jQuery("#GROUP_" + groupCode, _self.mainContainer));
};

/*
 * 卡片按钮与流程按钮都加载完后执行方法
 */
mb.vi.cardView.prototype._afterBtnLoad = function() {
	var _self = this;
	var tmplBtns = _self.temlBtnStore;
	var hasSendBtnFlag = false;
	var hasMoreBtnFlag = false;
	var moreBtn = null;
	var count = 0;
	if (tmplBtns.cmSaveAndSend) {
		hasSendBtnFlag = true;
		count++;
		tmplBtns.COUNT++;
	}
	//渲染按钮
	
	jQuery.each(tmplBtns, function(code,nodeCon){
		if (code == "cmSaveAndSend" || code == "COUNT") {
			return;
		}
		if (code.indexOf("%") > 0 && code.indexOf("_") == 0) {
			_self._addBtnToGroup(code.substring(1,code.indexOf("%")), nodeCon);
		} else {
			count++;
			if (count < _self.btnSpace || tmplBtns.COUNT <= _self.btnSpace) {
				_self._addBtnToBar(nodeCon);
			} else {
				if (!moreBtn) {
					var item = {
						ACT_CODE: "more",
						ACT_NAME: "更多",
						ACT_CSS: "more"
					};
					moreBtn = _self.bldGroupBtn(item);
					_self.bldBtnGroup("more");
					hasMoreBtnFlag = true;
				}
				_self._addBtnToGroup("more", nodeCon);
			}
		}
	});
	if (hasSendBtnFlag) {
		_self._addBtnToBar(tmplBtns.cmSaveAndSend);
	}
	if (hasMoreBtnFlag) {
		if(moreBtn){
			_self._addBtnToBar(moreBtn);
		}
	}
	//绘制普通按钮条
	this.btnBar.show();
	//回收空间
	delete _self.temlBtnStore;
};

/*
 * 构建卡片form，调用mb.ui.form组件
 */
mb.vi.cardView.prototype._bldForm = function() {
    var _self = this;
    //构造Form
    this.formCon = jQuery("<div class='mbCard-formContainer'></div>");
	var opts = {
		"pId":_self.servId + "-form",
		"sId":_self.servId,
		data : this._data,
		"parHandler":_self,
		"readOnly":this._readOnly,
		"pCon":_self.mainContainer 
	}
	this.form = new mb.ui.card(opts);
    this.form.render();
    if (this._actVar == UIConst.ACT_CARD_MODIFY) {
    	var data = FireFly.byId(_self.servId,_self._pkCode);
    	this.form.fillData(data);
    	_self.byIdData = data;
    } else {
		var data = FireFly.byId(_self.servId, _self._pkCode);
    	this.form.fillData(data);
    	_self.byIdData = data;
	}
    this.form.afterRender();
};
/*
 * 刷新卡片页面
 */
mb.vi.cardView.prototype.refresh = function() {
	this.mainContainer.empty();
	this._bldCardLayout();
	this._afterLoad();
};

/*
 * 获取按钮对象
 */
mb.vi.cardView.prototype.getBtn = function(actCode) {
    var _self = this;
    if (this.btns[actCode]) {
    	return this.btns[actCode];
    } else {
    	return jQuery();
    }
};

/*
 * 卡片加载后执行工程级js方法
 */
mb.vi.cardView.prototype._excuteProjectJS = function() {
	var _self = this;
	var _self = this;	
    this._servPId = this._data.SERV_PID || "";//父服务ID
    var loadArray = this._data.SERV_CARD_LOAD_NAMES.split(",");
    for (var i = 0;i < loadArray.length;i++) {
    	if (loadArray[i] == "") {
    		return;
    	}
    	load(loadArray[i]);
    }
	function load(value) {
		var pathFolder = value.split("_");
		var lowerFolder = (pathFolder[0] || "").toLowerCase();
	    var jsFileUrl = FireFly.getContextPath() + "/" + lowerFolder + "/servjs/" + value + "_card_mb.js";
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
	            	console.log(e.stack);
	            }
	        },
	        error: function(){;}
	    });			
	};
};

/*
 * 加载的提示信息
 * @param msg 消息
 */
mb.vi.cardView.prototype.cardBarTipLoad = function(msg) {
	Tip.showLoad(msg,null,null,null,this.getNowDom());
};
/*
 * 成功的提示信息
 * @param msg 提示内容
 */
mb.vi.cardView.prototype.cardBarTip = function(msg) {
	this.showTip(msg);
};
/*
 * 错误的提示信息
 * @param msg 提示内容
 */
mb.vi.cardView.prototype.cardBarTipError = function(msg) {
	this.showTipError(msg)
};
/*
 * 清除加载提示信息
 */
mb.vi.cardView.prototype.cardClearTipLoad = function() {
    Tip.clearLoad();
};
/*
 * 区分列表还是卡片
 */
mb.vi.cardView.prototype.getNowDom = function() {
	var _self = this;
    return this.mainUL;
};	
/*
 * 区分列表还是卡片
 */
mb.vi.cardView.prototype.itemValue = function(itemCode) {
	var _self = this;
    return this.form.itemValue(itemCode);
};	
/**
 * 取得SERV_SRC_ID的值
 */
mb.vi.cardView.prototype.getServSrcId = function(){
	var result = this._data.SERV_SRC_ID;
	return result || "";
};
/*
 * 获取业务数据，修改时获取的是业务数据，添加的时候获取的是默认值
 */
mb.vi.cardView.prototype.getByIdData = function(itemCode) {
    var _self = this;
    if (_self._actVar == UIConst.ACT_CARD_MODIFY) {//修改
      return _self.byIdData[itemCode] || "";
    } else if (_self._actVar == UIConst.ACT_CARD_ADD) {//添加
      return _self.byIdData[itemCode] || "";
    }
};
/**
 * 是否存在这样一个关联子服务
 */
mb.vi.cardView.prototype.existSubServ = function() {
	var _self = this;
	var rtnVal = false;
    return rtnVal;
};
/*
 * 重置当前卡片页面的高度，初始化时
 * @bottomHei：列表高度-系统默认列表高度。（当主单下列表的tab切换时调用）
 */
mb.vi.cardView.prototype._resetHeiWid = function() {
	var _self = this;
    return true;
};
/**
 * 设置点击是否刷新主卡片
 * @param servId 服务ID
 * @param bool 布尔值
 */
mb.vi.cardView.prototype.setRefreshFlag = function(servId, bool) {
};
/*
 * 获取字段 中 的 配置 mind中用到
 * @param itemCode 字段编码
 */
mb.vi.cardView.prototype.getItemConfig = function(itemCode) {
    var _self = this;
    var itemConfig = _self._data.ITEMS[itemCode].ITEM_INPUT_CONFIG;
    return itemConfig;
};
/*
 * 获取字段 对象
 * @param itemCode 字段编码
 */
mb.vi.cardView.prototype.getItem = function(itemCode) {
    var _self = this;
    return _self.form.getItem(itemCode);
};

/*
 * 获取修改的页面数据
 */
mb.vi.cardView.prototype.getChangeData = function() {
    var changeData = "";
    if (this._actVar == UIConst.ACT_CARD_ADD && (this._copyNewFlag && this._copyNewFlag == true)) {//复制过来的话
    	changeData = this.form.getItemsValues();
    } else {
    	changeData = this.form.getChangedItems();//获取页面修改的字段值集合对象
    }
    if ((changeData["USER_PASSWORD"] == "")) {
		delete changeData["USER_PASSWORD"];
    }
    if (this.extendSubmitData) {//扩展的参数传递到后台
    	changeData = jQuery.extend(changeData,this.extendSubmitData);
    }
    return changeData;
};

/*
 * 修改保存
 */
mb.vi.cardView.prototype.modifySave = function(changeData,afterReload) {
	var _self = this;
    changeData[UIConst.PK_KEY] = this._pkCode;	
    if (_self.itemValue("S_MTIME")) {
	   changeData["S_MTIME"] = _self.itemValue("S_MTIME");	
    }
    var resultData = FireFly.cardModify(_self.opts.sId,changeData);
    _self.afterSave(resultData);//保存后监听方法
    if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
   	  _self._pkCode = resultData[_self.servKeys];
   	  _self.setParentRefresh();
   	  _self._lastData = resultData;
   	  if (afterReload == false) {
   	  } else {
   		  _self.refresh();
   	  }
    }
};

/*
 * 设置点击返回时刷新父列表页面
 */
mb.vi.cardView.prototype.setParentRefresh = function() {
	return this._parentRefreshFlag = true;
};

/*
 * 保存之后执行,业务代码可覆盖此方法
 */
mb.vi.cardView.prototype.afterSave = function(resultData) {
};