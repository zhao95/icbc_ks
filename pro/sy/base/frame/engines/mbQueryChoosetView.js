/** 查询选择页面渲染引擎 */
GLOBAL.namespace("mb.vi");
/*
 * 待解决问题：
 * 
 */

mb.ui.querychoose = function(options) {
	var defaults = {
			"id":options.chooseId + "-mbQueryChoose",
			"aId":"", //操作ID
			"pCon":null,
			"pId":null,
			"linkWhere":"",
			"itemCode":"",
			"config":"",
			"parHandler":null,
			"formHandler":null,
			"cardHandler":null,// 卡片对象，用于计算查询选择框的位置
			"_SELECT_":null,//显示的字段
			"replaceCallBack":null,
			"title":"查询选择",
	};
	this.opts = jQuery.extend(defaults,options);
    this._pCon = this.opts.pCon;
	this._parHandler = this.opts.parHandler;
    this.id = this.opts.id;
    this.title = "查询选择";
    var config = this.opts.config;
    var confArray = config.split(",");
	this.sId = confArray[0];
	var conf = confArray.slice(1);
	this._confJson = StrToJson(conf.join(","));
	this.pCodes = this._confJson && (this._confJson.TARGET) ? this._confJson.TARGET.split("~"):"";
	this.sCodes = this._confJson && (this._confJson.SOURCE) ? this._confJson.SOURCE.split("~"):"";
	this.showCodes = this._confJson && (this._confJson.SHOWITEM) ? this._confJson.SHOWITEM.split("~"):"";//取得用于显示的内容字段
	this.type = this._confJson && (this._confJson.TYPE) ? this._confJson.TYPE:"";//单选多选类型
	this.sCodesFilter = [];
	this._searchWhere = "";//查询条件
	this._extendWhere = "";//扩展条件

	this._extWhere = this._confJson && (this._confJson.EXTWHERE) ? this._confJson.EXTWHERE:"";
	this._height = "";
	this._width = "";
	this._data = null;	
	this.PAGE = {};
};
/*
 * 渲染列表主方法
 */
mb.ui.querychoose.prototype.show = function(event) {
	this._initMainData();
	this._layout();
	this._bldList();
	this._afterLoad();
};
/*
 * 构建表格，包括标题头和数据表格
 */
mb.ui.querychoose.prototype._bldWin = function(event) {
	var _self = this;
	jQuery("#" + this.id).empty();
	jQuery("#" + this.id).dialog("destroy");
	//1.构造dialog
	this.winDialog = jQuery("<div></div>").addClass("mbDialog").attr("id",this.id).attr("title",this.title);
	this.winDialog.appendTo(jQuery("body"));
	//@TODO:显示位置处理
	this.hei = document.documentElement.clientHeight;
    this.wid = "100%";
    var posArray = [];
    if (event) {
	    var cy = event.clientY;
	    posArray[0] = "";
	    posArray[1] = cy-100;
    }
    _self.mbCardConDis = true;
    if (jQuery(".mbCard-container").css("display") == "none") {
    	_self.mbCardConDis = false;
    }
	jQuery("#" + this.id).dialog({
		autoOpen: false,
		width: _self.wid,
		modal: true,
		resizable:false,
		position:posArray,
		open: function(){
			if (_self.mbCardConDis) {
				jQuery(".mbCard-container").css("display","none");
			}
		},
		close: function() {
			if (_self.mbCardConDis) {
				jQuery(".mbCard-container").css("display","block");
			}
			_self.winDialog.remove();
		}
	});
    jQuery("#" + this.id).dialog("open");
    jQuery(".ui-dialog-titlebar").last().css("display","block");//设置标题显示
//    var obj = jQuery("#" + this.id);
//    obj.parent().find(".ui-dialog-titlebar-close").hide();
//    var mbClose = jQuery("<a></a>").addClass("mbClose mb-radius-1em");
//    var inner = jQuery("<span></span>").addClass("mbClose-inner").appendTo(mbClose);
//    var text = jQuery("<span>关闭</span>").addClass("mbClose-text").appendTo(inner);
//    var icon = jQuery("<span>&nbsp;</span>").addClass("mbClose-icon mb-icon-close mb-radius-18").appendTo(inner);
//    mbClose.bind("click",function() {
////    	jQuery("html").css({"overflow":"auto","height":"auto"});
////    	jQuery("body").css({"overflow":"auto","height":"auto"});
//    	_self._close();
//    });
//    mbClose.appendTo(obj.parent().find(".ui-dialog-titlebar"));
};
/*
 * 初始化数据
 */
mb.ui.querychoose.prototype._initMainData = function() {
	var _self = this;
	var options = {};
	if (_self.sCodes.length > 0) {
		jQuery.each(_self.sCodes,function(i,n) {
			if (n.indexOf("__NAME") < 0) {
				_self.sCodesFilter.push(jQuery.trim(n));
			}
		});
	}
	if (_self._extWhere.length > 0) {
		options[_self.EXT_WHERE] = _self._extWhere;
		//_self.whereData[_self.EXT_WHERE] = _self._extWhere;
	}
	if (_self.opts._SELECT_ == null) {//列信息
		options["_SELECT_"] = _self.sCodesFilter.toString();
	}
	this._data = FireFly.getCache(this.sId,FireFly.servMainData);
	this._listData =  FireFly.getPageData(this.sId,options) || {};	
};
/*
 * 刷新
 */
mb.ui.querychoose.prototype.refresh = function() {
	var _self = this;
};
/*
 * 构建列表页面布局
 */
mb.ui.querychoose.prototype._layout = function() {
	var _self = this;
	var listContainer = jQuery("<div></div>").addClass("mbQueryChoose-container");
	var top = jQuery("<div></div>").addClass("mbQueryChoose-container-top").appendTo(listContainer);//头部
	this.con = jQuery("<div></div>").addClass("mbQueryChoose-container-con").appendTo(listContainer);//主内容区
	this.gridContainer = jQuery("<div></div>").addClass("mbQueryChoose-container-con-grid").appendTo(listContainer);//列表外容器
	if(this.type == UIConst.TYPE_MULTI){
		var bottom = jQuery("<div></div>").addClass("mbSelectList-container-bottom").insertAfter(jQuery(".mbDialog"));//底部
		var okBtn = jQuery("<button>确定</button>").addClass("mb-select-ok");
		jQuery(okBtn).appendTo(bottom);
	 	jQuery(okBtn).bind("click",function(){
	 		_self.backWriteItem();
	 	});
	}
	var clearBtn = jQuery("<button>清空</button>").addClass("mb-select-clear");
	jQuery(clearBtn).appendTo(bottom);
 	jQuery(clearBtn).bind("click",function(){
 		_self._parHandler.setValue("");
 		_self._close();
 	});
	listContainer.appendTo(this.winDialog);
};
/*
 * 构建数据列表
 */
mb.ui.querychoose.prototype._bldList = function() {
	var _self = this;
	var temp = {"id":_self.sId,"mainData":_self._data,"parHandler":_self,"pCon":_self.gridContainer,"type":_self.type};
	temp["listData"] = _self._listData;
	this.grid = new mb.ui.grid(temp);
	this.grid.render();
	this.grid.click(this._setClick, this);
	//_self.winDialog.height(document.documentElement.clientHeight);
};
/*
 * 行点击回调函数
 */
mb.ui.querychoose.prototype._setClick = function(pkCode,node,checktr){
	var _self = this;
	if(_self.type == UIConst.TYPE_MULTI){
		var checkspan = checktr.find(".mb-icon-check");
		var flag = checkspan.hasClass("mb-icon-checked");
		if(!flag){
			checkspan.addClass("mb-icon-checked");
		} else{
			checkspan.removeClass("mb-icon-checked");
		}
	} else {
		/*
		_self._parHandler.setValue(_self.id,pkCode);
    	_self._close();
    	*/
		_self.backWriteItem(pkCode);
	}
	
}
/*
 * 回写
 */
mb.ui.querychoose.prototype.backWriteItem = function(pkCode){
	var _self = this;
    jQuery.each(_self.sCodes, function(i,n) {//回写字段，如果有配置回写字段
   	   _self.pCode = _self.pCodes[i];
   	   if (_self.pCode == "" || _self.pCode == null) {
   	   	   return;
   	   }
	   if(_self.type == UIConst.TYPE_MULTI){
	      _self.iCodes = _self.grid.getSelectItemValues(n);
	   } else {
		   _self.iCodes = _self.grid.getRowItemValue(pkCode,n);
	   }
//	       _self._parHandler.setGroupValue(_self.pCode,_self.iCodes);
       if (_self._split && _self._split.length > 0) {//有自定义的分割符
    	   var str = _self.iCodes + "";
    	   _self.iCodes = str.replace(/\,/g,_self._split);
       }
       if (_self.opts.rebackCodes) {
		   var val = jQuery("#" + _self.opts.rebackCodes).val() ;
		   if(_self.appendValue && val.length > 0){ //是否追加数据
			   val += "," + _self.iCodes;
		   }else{
    		   val = _self.iCodes;
    	   }
		   jQuery("#" + _self.opts.rebackCodes).val(val);
       } else if (_self.pCode.indexOf("__NAME") > 0) {
    	   var code = _self.pCode.substring(0,_self.pCode.indexOf("__NAME"));
    	   var val = _self.formHandler.getItem(code).obj.val();
    	   if(_self.appendValue && val.length > 0){ //是否追加数据
    		   val += "," + _self.iCodes;
    	   }else{
    		   val = _self.iCodes;
    	   }
    	   _self.formHandler.getItem(code).obj.val(val);
       } else {
    	   if (_self.formHandler) {
    		   var val = _self.formHandler.getItem(_self.pCode).getValue();
    		   if(_self.appendValue && val.length > 0){
	    		   val += "," + _self.iCodes;
	    	   }else{
	    		   val = _self.iCodes;
	    	   }
    		   _self.formHandler.getItem(_self.pCode).setValue(val);
    	   }
       }
	});
    if (_self.opts.replaceCallBack) { //有替换的回调函数
	    var array = {};
	    var allSelectedDatas = {};
	    var sArray = {};//源和目标对应
		jQuery.each(_self.sCodes, function(i,n) {
	   	   _self.pCode = _self.pCodes[i];
	   	   if (_self.pCode == "" || _self.pCode == null) {
	   	   	   return;
	   	   }
		   if(_self.type == UIConst.TYPE_MULTI){
		      _self.iCodes = _self.grid.getSelectItemValues(n);
		   } else {
			   _self.iCodes = _self.grid.getRowItemValue(pkCode,n);
		   }
	       array[n] = "" + _self.iCodes + "";
	       allSelectedDatas[n] = _self.iCodes;
	       sArray[n] = _self.pCode;
	    });
        var backFunc = _self.opts.replaceCallBack;
        var searchWhere = _self._extWhere;//???
        backFunc.call(_self.opts.parHandler,array,searchWhere,sArray,allSelectedDatas);
    }
    _self._close();
};
/*
 * 构建查看更多列表
 */
mb.ui.querychoose.prototype.morePend = function(options) {
	var _self = this;
	if (options && options._PAGE_) {
		_self.PAGE["_PAGE_"] = options._PAGE_;
	}
	var data = {};
	data = jQuery.extend({},_self.PAGE,data);//合并分页信息
	if (options && options._NOPAGEFLAG_ && (options._NOPAGEFLAG_ == "true")) {//删除分页信息
		delete data._PAGE_;
		delete data._NOPAGEFLAG_;
	}
	this._listData = FireFly.getPageData(this.sId,data);
	this.grid._morePend(this._listData);
	this.grid.click(this._setClick, this);

};
/*
 * 构建按钮条
 */
mb.ui.querychoose.prototype._bldBtnBar = function() {
	var _self = this;

};

/*
 * 加载后执行
 */
mb.ui.querychoose.prototype._afterLoad = function() {


};
/*
 * 关闭dialog
 */
mb.ui.querychoose.prototype._close = function() {
	var _self = this;
	if (this._parHandler) {
		//this._parHandler.setActive();
	}
	if (_self.mbCardConDis) {
		jQuery(".mbCard-container").css("display","block");
	}
	jQuery("#" + _self.id).empty();
	jQuery("#" + _self.id).dialog("destroy");
	_self.winDialog.remove();
};