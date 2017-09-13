/** 查询选择弹出框页面渲染引擎 */
GLOBAL.namespace("rh.vi");
/*
 * 例：SY_SERV_ITEM,{'TARGET':'CMPY_ADDRESS','SOURCE':'ITEM_CODE','EXTWHERE':' and SERV_ID=^SY_ORG_USER^','TYPE':'single'}
 * 
 * */
rh.vi.rhSelectListView = function(options) {
	this.conOptions = options;
	if (options.configs) {//关联选择配置
		this.opts = {"title":""};
		this.parHandler = options.parHandler;
		this.cardHandler = options.cardHandler;
		this.configs = options.configs;
		this.dialogId = GLOBAL.getUnId("selectDialog","UNIT");
	} else {//默认查询选择
		this._resetOptions(options);    
		this.dialogId = GLOBAL.getUnId("selectDialog",this.sId);
	}
	
	this._listViews = {}; // 支持多个列表
};
/*
 * 渲染页面主方法
 */
rh.vi.rhSelectListView.prototype.show = function(event,positionArray, dialogSizeArray) {
	var _self = this;
	//工作流特殊处理frame
	this._workflowControl();
	this._layout(event,positionArray,dialogSizeArray);
	if (this.configs) {
		this._bldChangeList();
		this.ul.appendTo(this._titBar);
		this.selectArea.appendTo(this.winDialog);
		this.winDialog.parent().find(".ui-dialog-title").empty();
		jQuery(".rh-select-top-li").first().click();
	} else {
		setTimeout(function() {
			_self._bldListView();
			_self._afterLoad();
			_self._lastInitHei();
		},0);	
	}
};

rh.vi.rhSelectListView.prototype._lastInitHei = function(options) {
	var lastHeight = $(".rh-select-container").height();
	
	if(lastHeight > 600) {
		lastHeight = 600;
	}
	this.winDialog.css("height",lastHeight + "px");
}
/*
 * 重置参数
 */
rh.vi.rhSelectListView.prototype._resetOptions = function(options) {
	var _self = this;
	var defaults = {
			"id":options.sId + "-viSelectView",
			"aId":"", //操作ID
			"pCon":null,
			"pId":null,
			"linkWhere":"",
			"itemCode":"",
			"config":"",
			"rebackCodes":null,
			"parHandler":null,
			"formHandler":null,
			"gridRow":null,
			"cardHandler":null,// 卡片对象，用于计算查询选择框的位置
			"searchFlag" : false,
			"params":null,
			"replaceCallBack":null,
//			"title":"查询选择",
			"title":Language.transStatic("rhSelectListView_string1"),
			"hideAdvancedSearch":false,     //是否因此高级查询按钮
			"showSearchFlag":"true",       //是否显示查询输入框，包括普通查询和高级查询，默认为显示
			"appendValue" : false          //是否在输入框上追加数据
	};
	var _self = this;
	this.opts = jQuery.extend(defaults,options);
	var config = this.opts.config;
	var confArray = config.split(",");
	this.sId = confArray[0];
	var conf = confArray.slice(1);
	this._confJson = StrToJson(conf.join(",")) || {};
	this.pCodes = this._confJson && (this._confJson.TARGET) ? this._confJson.TARGET.split("~"):"";
	this.sCodes = this._confJson && (this._confJson.SOURCE) ? this._confJson.SOURCE.split("~"):"";
	this.showCodes = this._confJson && (this._confJson.SHOWITEM) ? this._confJson.SHOWITEM.split("~"):"";//取得用于显示的内容字段
	//指定被选择数据的服务ID，用于兼容查询服务和卡片服务ID不一致的问题。
	//默认是主键关联，可能存在特殊需求要关联别的字段，TARGET_ID_ITEM就是这个字段
	//默认是关联当前服务，但是可能存在特殊需求要关联别的服务，TARGET_SERV_ID_ITEM就是这个字段
	this.targetServIDItem = this._confJson && (this._confJson.TARGET_SERV_ID_ITEM) ? this._confJson.TARGET_SERV_ID_ITEM:"";
	this.targetIDItem = this._confJson && (this._confJson.TARGET_ID_ITEM) ? this._confJson.TARGET_ID_ITEM:"";
	this.hides = this._confJson && (this._confJson.HIDE) ? this._confJson.HIDE.split("~"):[];//隐藏列设定
	if (this.sCodes.length > 0) {
		_self.sCodesFilter = [];
		jQuery.each(this.sCodes,function(i,n) {
			if (n.indexOf("__NAME") < 0) {
				_self.sCodesFilter.push(jQuery.trim(n));
			}
		});
	}
	this.type = this._confJson && (this._confJson.TYPE) ? this._confJson.TYPE:"multi";
	this._searchType = this._confJson && (this._confJson.SEARCHTYPE) ? this._confJson.SEARCHTYPE:null;
	if (this.opts.searchFlag && this._searchType) {
		this.type = this._searchType;
	}
	this.pkHide = this._confJson && (this._confJson.PKHIDE) ? this._confJson.PKHIDE:"false";//增加主键的隐藏判断
	this._dataFlag = this._confJson && (this._confJson.DATAFLAG) ? this._confJson.DATAFLAG:"true";//是否显示列表内容
	this._addBtnFlag = this._confJson && (this._confJson.ADDBTN) ? this._confJson.ADDBTN:"false";//是否显示添加按钮
	this._deleteBtnFlag = this._confJson && (this._confJson.DELETEBTN) ? this._confJson.DELETEBTN:"false";//是否显示删除按钮
	this._modifyBtnFlag = this._confJson && (this._confJson.MODIFYBTN) ? this._confJson.MODIFYBTN:"false";//是否显示修改按钮
	this._split = this._confJson && (this._confJson.SPLIT) ? this._confJson.SPLIT:"";//是否用替换的分割符
	this._htmlItem = this._confJson && (this._confJson.HTMLITEM) ? this._confJson.HTMLITEM:"";//返回html()的字段
	
	this.parHandler = this.opts.parHandler;
	this.formHandler = this.opts.formHandler;
	this.cardHandler = this.opts.cardHandler;
	this.gridRow = this.opts.gridRow;
	this.searchFlag = this.opts.searchFlag;
	this.params = this.opts.params;// 扩展参数对象
	this.contentMain = jQuery();
	this._data = null;	
	this._searchWhere = "";//查询条件
	this._extendWhere = "";//扩展条件

	this._linkWhere = this.opts.linkWhere;//关联功能过滤条件
	this.links = this.opts.links || {};//关联功能过滤条件
	this._height = "";
	this._width = "";
	this.hideAdvancedSearch = this.opts.hideAdvancedSearch || this._confJson.hideAdvancedSearch;
	this.showSearchFlag = this._confJson.showSearchFlag || this.opts.showSearchFlag ;
	this.LINK_WHERE = UIConst.LINK_WHERE;
	//是否追加Value到指定字段
	this.appendValue = this.opts.appendValue || this._confJson.appendValue;
};
/*
 * 构建弹出框页面布局
 */
rh.vi.rhSelectListView.prototype._layout = function(event,positionArray,dialogSizeArray) {
	var _self = this;
	jQuery("#" + this.dialogId).dialog("destroy");
	//构造dialog
	this.winDialog = jQuery("<div></div>").addClass("selectDialog").attr("id",this.dialogId).attr("title",this.opts.title);
	this.winDialog.appendTo(jQuery("body"));
	
	// 获取dialog的位置
	var bodyWid = jQuery("body").width();
	var wid = bodyWid/2 + 100;
	var hei = GLOBAL.getDefaultFrameHei() - 100;
	if (dialogSizeArray && $.isArray(dialogSizeArray) && dialogSizeArray.length == 2) {
		hei = dialogSizeArray[1];
	}
		
    var posArray = Tools.getDialogPosition(wid, hei);
    var left = scroll.left + (bodyWid - wid) / 2;
    posArray[0] = left;
    
    //设定死
    // posArray[1] = 100;    
	jQuery("#" + this.dialogId).dialog({
		autoOpen: false,
		height: hei,
		width: wid,
		modal: true,
		resizable:false,
		position:posArray,
		open: function() { 

		},
		close: function() {
			_self.winDialog.find("iframe").remove();
			_self.winDialog.remove();
			_self._workflowControl();
		}
	});
	var dialogObj = jQuery("#" + this.dialogId);
	dialogObj.dialog("open");
	dialogObj.focus();
    this._titBar = jQuery(".ui-dialog-titlebar").last().css("display","block");//设置标题显示
    dialogObj.parent().addClass("rh-bottom-right-radius rhSelectWidget-content");
    //增加服务的切换
    //Tip.showLoad("努力加载中...",null,jQuery(".ui-dialog-title",this.windialog).last());
};
/*
 * 构造列表
 */
rh.vi.rhSelectListView.prototype._bldListView = function() {
	if (jQuery("#" + this.sId + "-select-container").length == 1) {
		jQuery(".rh-select-container").hide();
		jQuery("#" + this.sId + "-select-container").addClass("rh-select-show").show();
	} else if (jQuery("#" + this.sId + "-select-container").length == 0) {
		jQuery(".rh-select-container").hide();
		var _self = this;
		var extWhere = "";
		var res = this._confJson && (this._confJson.EXTWHERE) ? this._confJson.EXTWHERE:"";
		var advRes = this._confJson && (this._confJson.SEARCHEXTWHERE) ? this._confJson.SEARCHEXTWHERE:"";//高级查询的条件替换
		if (advRes.length > 0) {
			res = advRes;
		}
		if (this.searchFlag == false) {//非高级查询时
			//字段级替换
			var match = new RegExp("#.*?#","gm").exec(res);//#字段#
			while(match != null) {
				var temp = match.toString();
				var item = temp.substring(1,temp.length-1);
				var value = "";
				if (_self.gridRow) { //列表上查询选择依赖
					value = $(":input[icode='"+item+"']", _self.gridRow).val();
				} else {
					value = _self.formHandler.getItem(item).getValue();
				}
				res = res.replace(temp,value);
				match = new RegExp("#.*?#","gm").exec(res);
			}
		}
		extWhere = res.replace(/\^/g,"'");//替换^
		var div = jQuery("<div class='rh-select-container'></div>").attr("id",this.sId + "-select-container").appendTo(this.winDialog);
		var temp = {"sId":this.sId,"pCon":div,"batchFlag":false,"showTitleBarFlag":"false","showButtonFlag":"false",
				"_SELECT_":_self.sCodesFilter.join(","),"_HIDE_":_self.hides.join(","),"type":this.type,"selectView":true,"pkHide":this.pkHide,"replaceQueryModel":1,
				"resetHeiWid":this._resetHeiWid,"parHandler":_self,"extWhere":extWhere,"dataFlag":this._dataFlag,"params":this.params,
				"hideAdvancedSearch":this.hideAdvancedSearch,"showSearchFlag":this.showSearchFlag};
		this.listView = new rh.vi.listView(temp);
		this.listView.show();
		
		// 保存起来
		this._listViews[this.sId] = this.listView;
	}
};
/*
 * 刷新列表
 */
rh.vi.rhSelectListView.prototype.refreshAndTip = function() {
	this.listView.refresh();
//	Tip.show("已重新加载当前列表！",null,jQuery(".ui-dialog-title",this.windialog).last()).css("line-height","20px");
	Tip.show(Language.transStatic("rhSelectListView_string2"),null,jQuery(".ui-dialog-title",this.windialog).last()).css("line-height","20px");
};
/*
 * 构造按钮条
 */
rh.vi.rhSelectListView.prototype._bldBtnBar = function() {
	var _self = this;
	var btnBar = this.listView._btnBar;
	btnBar.find(".rh-icon").remove();
	var btnData = [];
	if(_self.type == "multi"){ //如果是多选才增加确定选择按钮和取消选择按钮
//		btnData = [{"ACT_NAME":"确定","ACT_CODE":"selectOK","ACT_CSS":"ok"},
//	               {"ACT_NAME":"取消","ACT_CODE":"selectCancel","ACT_CSS":"cancel"}];
		btnData = [{"ACT_NAME":Language.transStatic("rh_ui_gridCard_string17"),"ACT_CODE":"selectOK","ACT_CSS":"ok"},
	               {"ACT_NAME":Language.transStatic("rh_ui_card_string18"),"ACT_CODE":"selectCancel","ACT_CSS":"cancel"}];
	}
	if (this._addBtnFlag === "true" || this._addBtnFlag === true) {
//		btnData.push({"ACT_NAME":"添加","ACT_CODE":"selectAdd","ACT_CSS":"add"});
		btnData.push({"ACT_NAME":Language.transStatic("rhSelectListView_string3"),"ACT_CODE":"selectAdd","ACT_CSS":"add"});
	}
	if (this._deleteBtnFlag === "true" || this._deleteBtnFlag === true) {
//		btnData.push({"ACT_NAME":"删除","ACT_CODE":"selectDelete","ACT_CSS":"delete"});
		btnData.push({"ACT_NAME":Language.transStatic("rh_ui_card_string22"),"ACT_CODE":"selectDelete","ACT_CSS":"delete"});
	}
	if (this._modifyBtnFlag === "true" || this._modifyBtnFlag === true) {
//		btnData.push({"ACT_NAME":"修改","ACT_CODE":"selectModify","ACT_CSS":"sync"});
		btnData.push({"ACT_NAME":Language.transStatic("rhSelectListView_string4"),"ACT_CODE":"selectModify","ACT_CSS":"sync"});
	}
	
	this.selectGrid = this.listView.grid;
	if(btnData.length == 0){ //如果没有按钮，则隐藏按钮栏
		return;
	}
	
	jQuery.each(btnData,function(i,n) {	
		var temp = jQuery("<a></a>").addClass("rh-icon").addClass("rhGrid-btnBar-a");
		temp.attr("id",_self._getUnId(n.ACT_CODE));
		_self._act(n.ACT_CODE,temp);
		var labelName = jQuery("<span></span>").addClass("rh-icon-inner").text(n.ACT_NAME);
		temp.append(labelName);
		var icon = jQuery("<span></span>").addClass("rh-icon-img").addClass("btn-" + n.ACT_CSS);
		temp.append(icon);
		btnBar.append(temp);
	});
};
/*
 * 根据动作绑定相应的方法
 * @param aId 动作ID
 */
rh.vi.rhSelectListView.prototype._act = function(aId,aObj) {
	var _self = this;
	var taObj = aObj;
	switch(aId) {
		case "selectOK"://确定选择
			taObj.bind("click",function() {
			   if (_self.configs) {
	               _self._backWriteItemConfigs();
	           } else {
	        	   var pks = _self.selectGrid.getSelectPKCodes();
	        	   if (pks.length == 0) {
//	        		   alert("请选择记录");
	        		   alert(Language.transStatic("rhSelectListView_string5"));
	        	   } else {
	        		   _self.backWriteItem();
	        	   }
	           }
			});	
			break;
		case "selectCancel"://取消选择
		    taObj.bind("click",function() {
		        _self.selectGrid.deSelectAllRows();
		    });  
		    break;
		case "selectAdd"://新加记录
		    taObj.bind("click",function() {
		    	var paramsAll = {"callBackHandler":_self};
		    	paramsAll["closeCallBackFunc"] = function() {
		    		_self.refreshAndTip();
		    	};
		    	paramsAll = jQuery.extend(paramsAll,_self.params);
//				var options = {"url":_self.sId + ".card.do","tTitle":'添加一条选择',"params":paramsAll,menuFlag:3};
				var options = {"url":_self.sId + ".card.do","tTitle":Language.transStatic("rhSelectListView_string6"),"params":paramsAll,menuFlag:3};
				top.Tab.open(options);
		    });  
		    break;
		case "selectDelete"://删除记录
		    taObj.bind("click",function() {
                var pkAarry = _self.listView.grid.getSelectPKCodes();
		    	if (jQuery.isEmptyObject(pkAarry)) {
//		    		 _self.listView.listBarTipError("请选择相应记录！");
		    		 _self.listView.listBarTipError(Language.transStatic("rhListView_string6"));
		    	} else {
//		    		 var res = confirm("您确定要删除该数据么？");
		    		 var res = confirm(Language.transStatic("rhListView_string9"));
		    		 if (res == true) {
//			    		_self.listView.listBarTipLoad("提交中...");
			    		_self.listView.listBarTipLoad(Language.transStatic("rhListView_string7"));
			    		setTimeout(function() {
				    		var strs = pkAarry.join(",");
				    		var temp = {};
				    		temp[UIConst.PK_KEY]=strs;
				    		var resultData = FireFly.listDelete(_self.sId,temp,true);
					        _self.listView.refresh();
			    		},0);	
		    		 } else {
		    		 	return false;
		    		 }
		    	}
		    });  
		    break;
		case "selectModify"://修改记录
			taObj.bind("click",function() {
				var paramsAll = {"callBackHandler":_self};
		    	paramsAll["closeCallBackFunc"] = function() {
		    		_self.refreshAndTip();
		    	};
				var pkAarry = _self.listView.grid.getSelectPKCodes();
		    	if (pkAarry.length>1||pkAarry.length==0) {
//		    		 _self.listView.listBarTipError("请选择一条记录！");
		    		 _self.listView.listBarTipError(Language.transStatic("rhSelectListView_string7"));
		    	}else{
		    		paramsAll = jQuery.extend(paramsAll,_self.params);
		    		var options = {"url":_self.sId + ".card.do?pkCode=" + pkAarry,"tTitle":Language.transStatic("rhSelectListView_string8"),"params":paramsAll,menuFlag:3};
		    		top.Tab.open(options);
		    	}
			});  
			break;
    };
};
/*
 * 回写值
 */
rh.vi.rhSelectListView.prototype.backWriteItem = function() {
	var _self = this;
    jQuery.each(_self.sCodes, function(i,n) {//回写字段，如果有配置回写字段
	   	   _self.pCode = _self.pCodes[i];
	   	   if (_self.pCode == "" || _self.pCode == null) {
	   	   	   return;
	   	   }
	       _self.iCodes = _self.selectGrid.getSelectItemValues(n);
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
	    	   
	    	   //处理字典名称
	    	   var name = _self.selectGrid.getSelectItemValues(n + "__NAME");
	    	   if (name) {
	    		   var nameItem = _self.formHandler.getItem(_self.pCode);
	    		   if (nameItem) {
	    			   nameItem.setValue(name);
	    		   }
	    	   }
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
	   	   var htmlItem = null;
           if ((_self._htmlItem.length > 0) && (_self._htmlItem.indexOf(n) >=0)) {//回写的取html()
        	   htmlItem = true;
           }
	       _self.iCodes = _self.selectGrid.getSelectItemValues(n,htmlItem);
	       array[n] = "" + _self.iCodes + "";
	       allSelectedDatas[n] = _self.iCodes;
	       sArray[n] = _self.pCode;
	    });
        var backFunc = _self.opts.replaceCallBack;
        var searchWhere = _self.listView.whereData[UIConst.SEARCH_WHERE];
        backFunc.call(_self.opts.parHandler,array,searchWhere,sArray,allSelectedDatas);
    }
   	jQuery("#" + _self.dialogId).dialog("close");
};
rh.vi.rhSelectListView.prototype.trClick = function() {
	var _self = this;
};
/*
 * 加载后执行
 */
rh.vi.rhSelectListView.prototype._afterLoad = function() {
	var _self = this;
	this._bldBtnBar();
	this.selectGrid.unbindTrdblClick();//取消双击绑定
	this.selectGrid.unbindIndexTDClick();//取消单击绑定
	if (this.configs) { 
		var newSId;
		this.selectGrid.getCheckBox().bind("click",function(event) {
			var tar = jQuery(event.target);
			var pTr = tar.parent().parent();
			var sId = _self.sId;
			newSId = sId;
			var id = pTr.attr("id");
			var newId = id;
			//如果查询服务与数据卡片服务ID字段不一致，则使用数据卡片服务的ID字段取得目标数据的ID。
			if(_self.targetIDItem.length > 0){
				newId = _self.selectGrid.getRowItemValue(id,_self.targetIDItem)
			}
			
			//如果查询服务与数据卡片服务不一致，则使用数据的卡片服务字段取得目标数据的服务ID。
			if(_self.targetServIDItem.length > 0){
				newSId = _self.selectGrid.getRowItemValue(id,_self.targetServIDItem);
			}
			var showItem = _self.showCodes;
			var title = "";
			if(showItem.length > 0){
				title = _self.selectGrid.getRowItemValue(id,showItem);
			} else {
				title = pTr.find("td").last().text();
			}
			if (pTr.hasClass("tBody-selectTr")) {
				_self._putIntoArea(newId, id, newSId, sId, title);
			} else {
				_self._deleteFormArea(newId);
				
				// 取消一个选中则取消全选
			    _self.selectGrid.getHeadCheckBox().attr("checked", false);
			}
		});
		
		this.selectGrid.getHeadCheckBox().unbind("click").bind("click",function(event) {
			
			// 防止重复选中，所以先清空选中
			_self.selectArea.find(".rh-select-area-span").each(function(index, n){
				var $this = jQuery(this);
				if ($this.attr("sId") == newSId) {
					$this.remove();
				}
			});
			
		    var sels = _self.selectGrid.getCheckBox();
		    if (jQuery(this).attr("checked")) {//已选中，取消所有选中
			    	jQuery.each(sels, function(i,n) {
			    		var tar = jQuery(n);
			    		var pTr = tar.parent().parent();
					var sId = _self.sId;
					var id = pTr.attr("id");
					var showItem = _self.showCodes;
					var title = "";
					if(showItem.length > 0){
						title = _self.selectGrid.getRowItemValue(id,showItem);
					} else {
						title = pTr.find("td").last().text();
					}
		    			pTr.addClass("tBody-selectTr");
		    			tar.attr("checked","true");
		    			var newId = id;
		    			var newSId = sId;
		    			//如果查询服务与数据卡片服务ID字段不一致，则使用数据卡片服务的ID字段取得目标数据的ID。
		    			if(_self.targetIDItem.length > 0){
		    				newId = _self.selectGrid.getRowItemValue(id,_self.targetIDItem)
		    			}
		    			
		    			//如果查询服务与数据卡片服务不一致，则使用数据的卡片服务字段取得目标数据的服务ID。
		    			if(_self.targetServIDItem.length > 0){
		    				newSId = _self.selectGrid.getRowItemValue(id,_self.targetServIDItem);
		    			}
		    			
		    			_self._putIntoArea(newId, id, newSId, sId, title);
			    	});
		    } else {//全部选中
			    	jQuery.each(sels, function(i, n) {
			    		var tar = jQuery(n);
			    		_self._deleteFormAreaBlock(tar);
			    	});
		    }
		});
		jQuery("#" + _self._getUnId("selectCancel")).unbind("click").bind("click",function() {
			jQuery(".rowIndex:checked",_self.selectGrid._table).each(function(i,n) {
		    		var tar = jQuery(n);
		    		_self._deleteFormAreaBlock(tar);
			});
		});
	} else {
		this.selectGrid.dblClick(this.backWriteItem,this);//增加双击绑定
		//增加提示信息
//		this.selectGrid.setTrTitle("双击此处确定选择");
		this.selectGrid.setTrTitle(Language.transStatic("rhSelectListView_string9"));
		//单选多选判断
		if (this.type == 'single') {
			this.selectGrid.trClick(function() {
				_self.trClick();
				_self.backWriteItem();
			},this);//增加单击事件
			//如果是单选，则点击redio就可以提交数据。
			this.selectGrid.getCheckBox().unbind("click").click(function(){
				_self.backWriteItem();
			});
//			this.selectGrid.setCheckTDTitle("单击确定选择");
			this.selectGrid.setCheckTDTitle(
					Language.transStatic("rhSelectListView_string10"));
		}	
	}
	//设置已选中行
};
//组合主键
rh.vi.rhSelectListView.prototype._getUnId = function(id) {
    var sId = this.opts.sId;
    return sId + "-" + id;
};
/*
 * 重置当前页面的高度，初始化时、从卡片返回列表时
 */
rh.vi.rhSelectListView.prototype._resetHeiWid = function() {
	var _self = this;
};
/*
 * 工作流定义页面特殊处理
 */
rh.vi.rhSelectListView.prototype._workflowControl = function() {
	var _self = this;
	var wfIframe = "#SY_WFE_PROC_DEF-includeJSP";
	if (jQuery(wfIframe).html() != null) {
		var obj = jQuery(wfIframe);
		if (obj.hasClass("hideFrame")) {
			obj.css("display","");
			obj.removeClass("hideFrame");
		} else {
			obj.css("display","none");
			obj.addClass("hideFrame");
		}
	}
};
/*
 * 选择的多列表切换
 */
rh.vi.rhSelectListView.prototype._bldChangeList = function() {
	var _self = this;
	this.ul = jQuery("<ul class='rh-select-top-ul'></ul>");
	var array = eval(this.configs);
	for (var i = 0;i < array.length;i++) {
		var item = array[i];
		var arr = JsonToStr(item);
		var li = jQuery("<li class='rh-select-top-li'>" + item.servName + "</li>").appendTo(_self.ul);
		li.bind("click", {"con":arr,"servId":item.servId}, function(event) {
			var data = event.data;
			jQuery(".rh-select-top-liActive").removeClass("rh-select-top-liActive");
			jQuery(this).addClass("rh-select-top-liActive");
			var config = data.servId + "," + data.con;
			_self._resetListload({"sId":item.servId,"config":config,"replaceCallBack":_self.conOptions.replaceCallBack,"formHandler":_self.conOptions.formHandler, "params":_self.conOptions.params});
		});
	}
	this.selectArea = jQuery("<div class='rh-select-top-area'></div>");
};
/*
 * 重置列表的加载
 */
rh.vi.rhSelectListView.prototype._resetListload = function(options) {
	var _self = this;
	//参数重置
	this._resetOptions(options);
	//列表加载
	this._bldListView();
	
	// 重新设置当前的listView和selectGrid
	this.listView = this._listViews[this.sId];
	this.selectGrid = this.listView.grid;
	
	if (jQuery("#" + this.sId + "-select-container").hasClass("rh-select-show")) {
	} else {
		this._afterLoad();
	}
};
/** 
 * 将数据展示在临时展示区
 * @param newId 关联服务中需要关联的ID，默认是关联列表中的主键
 * @param id 列表中的主键，默认是关联该这个的
 * @param newSId 关联服务中需要关联的服务ID，默认是关联列表对应的服务ID
 * @param sId 列表对应的服务ID，默认应该关联这个的
 * @param title 显示文字
 */
rh.vi.rhSelectListView.prototype._putIntoArea = function(newId, id, newSId, sId, title) {
	var _self = this;
	var span = jQuery("<span class='rh-select-area-span'><span class='rh-select-area-text'>" + Tools.replaceXSS(title) + "</span><span class='rh-select-area-close btn-delete'></span></span>").attr("selectid", newId).attr("sId", newSId).appendTo(this.selectArea);
	span.bind("mouseover",function() {
		jQuery(this).find(".rh-select-area-close").show();
	}).bind("mouseleave",function() {
		jQuery(this).find(".rh-select-area-close").hide();
	});
	(function(id, sId){
		span.data("target", _self._getTargetValuesById(id)); // 把所有需要返回的数据都放到暂存区里
		span.find(".rh-select-area-close").bind("click",function(event) {
			jQuery(this).parent().remove();
			//同步匹配列表数据
			var obj = jQuery(this).parent();
			var conId = "#" + sId + "-select-container";
			var tr = jQuery(conId).find("#" + id);
			
		    tr.find(".rowIndex").removeAttr("checked");
		    tr.removeClass("tBody-selectTr");
		    
		    // 取消一个选中则取消全选
		    _self.selectGrid.getHeadCheckBox().attr("checked", false);
		});
	})(id, sId);
};
/**
 *  获取指定id的行需要返回的所有数据，包括sId和dataId
 */
rh.vi.rhSelectListView.prototype._getTargetValuesById = function(id) {
	var _self = this;
	
	var tmp = {};
	
	/**
	 * 兼容之前的代码
	 */
	var sId = this.sId;
	var dataId = id;
	if(_self.targetIDItem && this.targetIDItem.length > 0){
		dataId = this.selectGrid.getRowItemValue(id, this.targetIDItem)
	}
	if(this.targetServIDItem && this.targetServIDItem.length > 0){
		sId = this.selectGrid.getRowItemValue(id, this.targetServIDItem);
	}
	tmp["sId"] = sId;
	tmp["dataId"] = dataId;
	
	/**
	 * 增加配置里的TARGET的值
	 */
	jQuery.each(this.pCodes, function(i, n){ // 获取需要获取的字段值
		// 获取改行的指定字段code的值
		if (n && n.length > 0) {
			var val = _self.selectGrid.getRowItemValue(id, n);
			if (val) {
				tmp[n] = val;
			}
		}
	});
	return tmp;
};
/* 
 * 根据checkbox对象删除area内容
 */
rh.vi.rhSelectListView.prototype._deleteFormAreaBlock = function(tar) {
	var _self = this;
	var pTr = tar.parent().parent();
	var sId = _self.sId;
	var id = pTr.attr("id");
	if(_self.targetIDItem.length > 0){ // 条件成立则selectid存放的是targetIDItem而不是id
		id = _self.selectGrid.getRowItemValue(id, _self.targetIDItem);
	}
	pTr.removeClass("tBody-selectTr");
	tar.removeAttr("checked");
	_self._deleteFormArea(id);
};
/* 
 * 将数据删除临时展示区
 */
rh.vi.rhSelectListView.prototype._deleteFormArea = function(data) {
	this.selectArea.find(".rh-select-area-span[selectid='" + data + "']").remove();
};
/* 
 * 将数据删除临时展示区
 */
rh.vi.rhSelectListView.prototype._areaShowHide = function() {
	if (this.selectArea.find(".rh-select-area-span").length == 0) {
		this.selectArea.hide();
	} else  {
		this.selectArea.show();
	}
};
/* 
 * 回写临时列表区
 */
rh.vi.rhSelectListView.prototype._backWriteItemConfigs = function() {
	var _self = this;
	var values = [];
	this.selectArea.find(".rh-select-area-span").each(function(index, n){
		var $this = jQuery(n);
		var value = $this.data("target");
		values.push(value);
	});

	if (values.length > 0) {
		if (this.opts.replaceCallBack) { //有替换的回调函数
	        this.opts.replaceCallBack.call(this.conOptions.parHandler, values);
	    } else {
			this.conOptions.parHandler.buildList(values);
	    }
		jQuery("#" + _self.dialogId).dialog("close");
	} else {
//		alert("请选中记录！");
		alert(Language.transStatic("rhSelectListView_string11"));
	}
};

