/*
 * 高级查询组件
 */
GLOBAL.namespace("rh.ui");
rh.ui.ccexSearch = function(options) {
	var defaults = {
		"id":"",
//		"msg":"执行中，请稍后..",
		"msg":Language.transStatic("rh_ui_ccexSearch_string1"),
		"data":null,
		"treeLink":false,
		"pCon":null,//外层容器
		"col":1,//显示列数，默认值是1列
		"parHandler":null,
		"data":{}
	};
	this.opts = jQuery.extend(defaults,options);
	this._msg = this.opts.msg;
	this._id = "rh-advanceSearch-" + this.opts.id;
	this._treeLink = this.opts.treeLink;
	this._items = this.opts.data.ITEMS;
	this._dicts = this.opts.data.DICTS;
	this._parHandler = this.opts.parHandler;
	this._pCon = this.opts.pCon;
	this._col = this.opts.col;//显示列数
//	this._numberInputTipMsg = "请输入数字";
	this._numberInputTipMsg = Language.transStatic("rh_ui_ccexSearch_string2");
};
rh.ui.ccexSearch.prototype.show = function() {
	this._bldContent();
};
/*
 * 构建弹出框内字段显示
 */
rh.ui.ccexSearch.prototype._bldContent = function() {
	var _self = this;
	var colspanCount = 0;
	var widCon = 250;
	var sumCols = 4;//2大列显示是table共需4列
	if (this._col == 3) {
		widCon = 160;
		sumCols = 6;
	} else if (this._col == 1) {
		widCon = 280;
		sumCols = 2;		
	}
	jQuery.each(this._items,function(i,n) {
		if (n.ITEM_TYPE == UIConst.FORM_FIELD_TYPE_SELF) {//过滤自定义字段
			return true;
		}
		if (n.ITEM_SEARCH_FLAG == UIConst.STR_YES) {//启用高级查询的情况
			var tdLab = jQuery("<td></td>").css({"padding-right":"10px"}).appendTo(_self._pCon);
			var tdInp = jQuery("<td></td>").css({"padding-right":"20px"}).appendTo(_self._pCon);
			//标签部分
			jQuery("<label value='" + n.ITEM_CODE + "'>" + n.ITEM_NAME + "：</label>").addClass("rh-advSearch-lab").appendTo(tdLab);
			//输入框部分
			var code = n.ITEM_CODE;
			var type = n.ITEM_INPUT_TYPE;//输入类型
			var mode = n.ITEM_INPUT_MODE;//输入模式
			var conf = n.ITEM_INPUT_CONFIG;
			if (conf.indexOf("'SEARCHHIDE':'true'") > 0) {
				return true;
			}
			var array = conf.split(",");
			conf = jQuery.trim(n.ITEM_INPUT_CONFIG);
			var confJson = {};
			if(conf){
				//以"{"开头，
				if(conf.indexOf("{")==0){
					confJson = StrToJson(conf);
				} else if (array.length > 1) {//有配置附加参数的
					//不是以"{"开头，则可能是字典或其他配置
					confJson = StrToJson(array.slice(1).join(","));
				}
			}
			var dictCode = array[0];
			if ((type == UIConst.FITEM_ELEMENT_SELECT) && (conf.indexOf("'SEARCHTYPE':'multi'") < 0)) {
				if (dictCode == "") {//没有配置相应字典，则不予显示
					return true;
				}
				var temp = _self._dicts[dictCode] || "";
				if (temp == undefined) {//页面没有去后台取
					temp = FireFly.getDict(dictCode);
					if(temp && temp.length > 0){
						//默认取第一个
						temp = temp[0].CHILD;
					}
				}
				var sel = jQuery("<select></select>").addClass("rh-advSearch-val")
					.width(widCon + 5).css({"border":"1px #bebebe solid","height":"25px","line-height":"25px","padding":"3px 0"});
				sel.data("itemcode",code);
				sel.data("itemtype",UIConst.FITEM_ELEMENT_SELECT);
//				jQuery("<option value=''>---全部---</option>").appendTo(sel);
				jQuery("<option value=''>---"+Language.transStatic('rh_ui_ccexSearch_string3')+"---</option>").appendTo(sel);
				jQuery.each(temp,function(i,n) {
					var id = n.ID;
					var name = n.NAME;
					jQuery("<option value='" + id + "'>" + name + "</option>").appendTo(sel);
				});
				sel.data("symbol", confJson.SEARCHONE);
				sel.appendTo(tdInp);	
			} else if ((type == UIConst.FITEM_ELEMENT_SELECT) && (conf.indexOf("'SEARCHTYPE':'multi'") > 0)
					|| (type == UIConst.FITEM_ELEMENT_RADIO) 
					|| (type == UIConst.FITEM_ELEMENT_CHECKBOX)) {
				var temp = _self._dicts[dictCode];
				if (temp == undefined) {//页面没有去后台取
					temp = FireFly.getDict(dictCode);
					if(temp && temp.length > 0){
						//默认取第一个
						temp = temp[0].CHILD;
					}
				}
				var sel = jQuery("<fieldset></fieldset>").addClass("rh-advSearch-val").css({'padding':'0px 0px 0px 5px',"border":"1px #bebebe solid"});
				sel.data("itemcode",code);
				sel.data("itemtype",UIConst.FITEM_ELEMENT_CHECKBOX)
				jQuery.each(temp,function(i,n) {
					var id = n.ID;
					var name = n.NAME;
					var uid = code + "-" + id;
					var check = jQuery("<input type='checkbox' />");
					check.attr("id",uid);
					check.attr("name",code);
					check.attr("value",id);
					check.addClass("rh-advSearch-check").appendTo(sel);	
					jQuery("<label for='" + uid + "'></label>").text(name).addClass("rh-advSearch-checkLable").appendTo(sel);
				});
				sel.appendTo(tdInp);				
			} else {//其它输入类型
				var inp = jQuery("<input type='text' size=45/>").data("itemcode",code).addClass("rh-advSearch-val").width(widCon);
				var inpName = jQuery("<input type='text' size=45/>").addClass("rh-advSearch-inp").width(widCon);
				var inpId = _self._id + code;
				inp.attr("id",inpId);
				inpName.attr("id",inpId + "__NAME");
				
				if (mode == UIConst.FITEM_INPUT_QUERY) {//查询选择
					//查询选择，处理存在问题，暂时注掉，用户手动输出查询
					inp.data("itemmode",UIConst.FITEM_INPUT_QUERY).appendTo(tdInp);
				} else if(mode == UIConst.FITEM_INPUT_DICT) {//字典
					inp = jQuery("<input type='hidden' size=45/>").data("itemcode",code).data("itemmode",UIConst.FITEM_INPUT_DICT)
					       .addClass("rh-advSearch-val").width(widCon);
					inp.attr("id",inpId);
					inp.appendTo(tdInp);
					inpName.appendTo(tdInp);
//					var dictSel = jQuery("<a>选择</a>").addClass("rh-advSearch-sel").appendTo(tdInp);
					var dictSel = jQuery("<a>"+Language.transStatic('rh_ui_card_string60')+"</a>").addClass("rh-advSearch-sel").appendTo(tdInp);
					dictSel.bind("click",function(event) {
						var options = {
								"itemCode" : inpId,
								"config" : conf,
								"parHandler" : _self,
								"hide" : "explode",
								"show" : "blind",
								"searchFlag" : true,
								"rebackCodes":inpId
						};
						var dictView = new rh.vi.rhDictTreeView(options);
						dictView.show(event);
						dictView.tree.selectNodes(inp.val().split(","));
					});
					
					if (n.ITEM_INPUT_FLAG != "1") { //不是选择可录入
						inpName.css({"cursor":"pointer"});
						inpName.click(function() {
							dictSel.click();
						});
					}
					
//					var queryCan = jQuery("<a>取消</a>").addClass("rh-advSearch-cancle").appendTo(tdInp);
					var queryCan = jQuery("<a>"+Language.transStatic('rh_ui_card_string18')+"</a>").addClass("rh-advSearch-cancle").appendTo(tdInp);
					queryCan.bind("click",function() {
						inp.val("");
						inpName.val("");
					});
				} else if (mode == UIConst.FITEM_INPUT_DATE) {//日期
					var width = widCon;
					var symbolSelect = jQuery("<SELECT><option value='>='>>=</option><option value='<='><=</option><option value='>'>></option><option value='<'><</option><option value='=' selected='selected'>=</option></SELECT>")
						.css("border","1px #bebebe solid").height("25");
					if(confJson.SEARCHONE && confJson.SEARCHTWO){
						//如果配置了操作符,则设定为默认值
						symbolSelect.val(confJson.SEARCHONE);
						symbolSelect.appendTo(tdInp);
						tdInp.append("&nbsp;");
						width = widCon-40;
						var inp1 = jQuery("<input type='text' tabindex='-1' size=19/>").data("itemcode",code)
				           .data("itemmode",UIConst.FITEM_INPUT_DATE).addClass("rh-advSearch-val").width(width);
						inp1.appendTo(tdInp);
						inp1.bind("focus",function() {
							datePicker(conf);
						});
						tdInp.append("<br>");
						var symbolSelect2= symbolSelect.clone();
						//如果配置了操作符,则设定为默认值
						symbolSelect2.val(confJson.SEARCHTWO);
						symbolSelect2.appendTo(tdInp);
						tdInp.append("&nbsp;");
						var inp2 = jQuery("<input type='text' tabindex='-1' size=19/>").data("itemcode",code)
							.addClass("rh-advSearch-inp").width(width);
						inp1.data("nextInp",inp2);
						inp1.data("symbolSelect",symbolSelect);
						inp2.appendTo(tdInp);
						inp2.data("symbolSelect",symbolSelect2);
						inp2.bind("focus",function() {
							datePicker(conf);
						});
					}else if(confJson.SEARCHONE || confJson.SEARCHTWO){
						width = widCon-40;
						//如果配置了操作符,则设定为默认值
						symbolSelect.val(confJson.SEARCHONE ? confJson.SEARCHONE : confJson.SEARCHTWO);
						symbolSelect.appendTo(tdInp);
						tdInp.append("&nbsp;");
						//运算符号，默认为"="
						inp = jQuery("<input type='text' tabindex='-1' size=19/>").data("itemcode",code)
				           .data("itemmode",UIConst.FITEM_INPUT_DATE).addClass("rh-advSearch-val").width(width);
						inp.appendTo(tdInp);
						inp.data("symbolSelect",symbolSelect);
						inp.bind("focus",function() {
							datePicker(conf);
						});
					} else {
						//日期默认为区间查询
						//tdInp.attr("colspan","3");
						var wid = 112;
						var inp1 = jQuery("<input type='text' tabindex='-1' size=19/>").data("itemcode",code)
				           .data("itemmode",UIConst.FITEM_INPUT_DATE).addClass("rh-advSearch-val").addClass("Wdate").width(wid);
						inp1.appendTo(tdInp);
						inp1.bind("focus",function() {
							datePicker(conf);
						});
						jQuery("<span>到</span>").addClass("rh-advSearch-dao").appendTo(tdInp);
						var inp2 = jQuery("<input type='text' tabindex='-1' size=19/>").addClass("rh-advSearch-inp").addClass("Wdate").width(wid).data("itemcode",code);
						inp1.data("nextInp",inp2);
						inp2.bind("focus",function() {
							datePicker(conf);
						});
						inp2.appendTo(tdInp);
						//计算时间并返回相应条件
						if (confJson["SEARCHDAYS"] && confJson["SEARCHDAYS"].length > 0) {
							inp1.val(_self._time(confJson["SEARCHDAYS"]));
							inp2.val(System.getVar("@DATE@"));
							_self.defaultSetFlag = true;
						}
					}
				} else {
					//输入类型为“文本框”&&字段类型为“数字”
					if(type == UIConst.FITEM_ELEMENT_INPUT && n.ITEM_FIELD_TYPE==UIConst.DATA_TYPE_NUM){
						var width = widCon;
						var symbolSelect = jQuery("<SELECT><option value='>='>>=</option><option value='<='><=</option><option value='>'>></option><option value='<'><</option><option value='=' selected='selected'>=</option></SELECT>")
						.css("border","1px #bebebe solid").height("25");
						if(confJson.SEARCHONE && confJson.SEARCHTWO){
							//如果配置了操作符,则设定为默认值
							symbolSelect.val(confJson.SEARCHONE);
							symbolSelect.appendTo(tdInp);
							tdInp.append("&nbsp;");
							width = widCon-40;
							var inp1 = jQuery("<input type='text' size=19/>").data("itemcode",code)
					           .data("datatype",UIConst.DATA_TYPE_NUM).addClass("rh-advSearch-val").width(width);
							inp1.appendTo(tdInp);
							tdInp.append("<br>");
							var symbolSelect2= symbolSelect.clone();
							//如果配置了操作符,则设定为默认值
							symbolSelect2.val(confJson.SEARCHTWO);
							symbolSelect2.appendTo(tdInp);
							tdInp.append("&nbsp;");
							var inp2 = jQuery("<input type='text' size=19/>").data("itemcode",code)
								.addClass("rh-advSearch-inp").width(width);
							inp1.data("nextInp",inp2);
							inp1.data("symbolSelect",symbolSelect);
							inp2.appendTo(tdInp);
							inp2.data("symbolSelect",symbolSelect2);
							//输入框增加回车查询事件
							_self.addEnterEventObj(inp1);
							_self.addEnterEventObj(inp2);
							//数字输入框事件注册
							_self.addNumberInputEventObj(inp1);
							_self.addNumberInputEventObj(inp2);
						} else if(confJson.SEARCHONE || confJson.SEARCHTWO){
							width = widCon-40;
							//如果配置了操作符,则设定为默认值
							symbolSelect.val(confJson.SEARCHONE ? confJson.SEARCHONE : confJson.SEARCHTWO);
							symbolSelect.appendTo(tdInp);
							tdInp.append("&nbsp;");
							//运算符号，默认为"="
							//var symbol = confJson.SEARCHONE ? confJson.SEARCHONE : (confJson.SEARCHTWO ? confJson.SEARCHTWO : "=");
							inp = jQuery("<input type='text' size=19/>").data("itemcode",code)
					           .data("datatype",UIConst.DATA_TYPE_NUM).addClass("rh-advSearch-val").width(width);
							inp.appendTo(tdInp);
							inp.data("symbolSelect",symbolSelect);
							//输入框增加回车查询事件
							_self.addEnterEventObj(inp);
							//数字输入框事件注册
							_self.addNumberInputEventObj(inp);
						} else {
							//默认
							inp = jQuery("<input type='text' size=19/>").data("itemcode",code)
					           .data("datatype",UIConst.DATA_TYPE_NUM).addClass("rh-advSearch-val").width(widCon);
							inp.appendTo(tdInp);
							//输入框增加回车查询事件
							_self.addEnterEventObj(inp);
							//数字输入框事件注册
							_self.addNumberInputEventObj(inp);
						}
					} else{
//						var symbolSelect = jQuery("<SELECT><option value='like' selected='selected'>包含</option><option value='not like'>不包含</option><option value='='>等于</option></SELECT>")
//							.css("border","1px #bebebe solid").height("25");
						var symbolSelect = jQuery("<SELECT><option value='like' selected='selected'>"+Language.transStatic('rh_ui_ccexSearch_string4')+"</option><option value='not like'>"+Language.transStatic('rh_ui_ccexSearch_string5')+"</option><option value='='>"+Language.transStatic('rh_ui_ccexSearch_string6')+"</option></SELECT>")
						.css("border","1px #bebebe solid").height("25");
						if(confJson.SEARCHONE){
							//如果配置了操作符,则设定为默认值
							symbolSelect.val(confJson.SEARCHONE);
							symbolSelect.appendTo(tdInp);
							tdInp.append("&nbsp;");
							inp = jQuery("<input type='text' size=19/>").data("itemcode",code)
					           .data("datatype",UIConst.DATA_TYPE_STR).addClass("rh-advSearch-val").width(widCon-66);
							inp.appendTo(tdInp);
							inp.data("symbolSelect",symbolSelect);
						} else{
							//默认
							inp.appendTo(tdInp);
						}
						//输入框增加回车查询事件
						_self.addEnterEventObj(inp);
					}
				}
			}
		}
	});
	//查询按钮
	var btnTd = jQuery("<td></td>").appendTo(_self._pCon);
	this.btnDiv = jQuery("<div></div>");
//	this.advBtn = jQuery("<div class='rhSearch-button'><div class='rhSearch-inner'>查询</div></div>").addClass("rhSearch-button").appendTo(this.btnDiv);
	this.advBtn = jQuery("<div class='rhSearch-button'><div class='rhSearch-inner'>"+Language.transStatic('rh_ui_ccexSearch_string7')+"</div></div>").addClass("rhSearch-button").appendTo(this.btnDiv);
	this.btnDiv.appendTo(btnTd);
    this._btnBind();
};
/*
 * 输入框回车事件注册
 */
rh.ui.ccexSearch.prototype.addEnterEventObj = function(obj){
	var _self = this;
	//绑定回车事件
	obj.keypress(function(event) {
        if (event.keyCode == '13') {
            _self.advBtn.click();
            return false;
        }
    });
};
/*
 * 数字输入框事件注册
 */
rh.ui.ccexSearch.prototype.addNumberInputEventObj = function(obj){
	var _self = this;
	//初始合法值定义为""
	obj.data("preValidVal","");
	obj.bind("keyup",function(){
		var flag = new RegExp("^(0|[-+]?[0-9]*([\.])?([0-9])*)$").test(obj.val());
		if(flag){
			//输入合法，设定值为上次合法值
			obj.data("preValidVal",obj.val());
		}else{
			//输入不合法，将值恢复至上次合法值
			obj.val(obj.data("preValidVal"));
		}
	});
	//初始化值为提示信息"请输入数字"
	obj.val(_self._numberInputTipMsg);
	obj.css("color","#999");
	obj.bind("focus",function(){
        var txt_value = obj.val();
        if(txt_value == _self._numberInputTipMsg){
        	obj.css("color","");
        	obj.val("");
        }
    });
	obj.bind("blur",function(){
       var txt_value = obj.val();
       if(txt_value == ""){
    	   obj.val(_self._numberInputTipMsg);
    	   obj.css("color","#999");
       }
   });
};
/*
 * 获取高级查询条件
 */
rh.ui.ccexSearch.prototype.getWhere = function() {
	var _self = this;
	var res = jQuery(".rh-advSearch-val",this.table);
	var temp = "";
	jQuery.each(res,function(i,n) {
		var obj = jQuery(n);
		var va = "";
		if (obj.data("itemtype") == UIConst.FITEM_ELEMENT_CHECKBOX) {
			var checks = obj.find("input:checked");
			var array = [];
			jQuery.each(checks, function(i,n) {
				array.push(jQuery(n).attr("value"));
			});
			va = array.join(",");
		} else {
			va = jQuery.trim(obj.val());
		}
		if (obj.data("itemmode") && obj.data("itemmode") == UIConst.FITEM_INPUT_DATE) {//日期特殊处理
			var symbolSelect = obj.data("symbolSelect");
			//日期为区间查询，如果没有选择过滤符号，第一个日期框默认为">="
			var symbol = symbolSelect ? symbolSelect.val() : ">=";
			if(va && va.length > 0){
				temp += " and ";
				temp += obj.data("itemcode");
				temp += (" " + symbol + " '");
				temp += va;
				temp += "'";
			}
			//判断是否是区间查询
			var nextInp = obj.data("nextInp");
			var nextVal = nextInp ? nextInp.val() : null;
			if (nextVal && nextVal.length > 0) {
				symbolSelect = nextInp.data("symbolSelect");
				//日期为区间查询，如果没有选择过滤符号，第二个日期框默认为"<="
				symbol = symbolSelect ? symbolSelect.val() : "<=";
				temp += " and ";
				temp += obj.data("itemcode");
				temp += (" " + symbol + " '");
				if(nextVal.length == 10){ //只有年月日，则加上时分秒
					temp += nextVal + " 24:00:00" ;
				}else if(nextVal.length == 16){ //有年月日时分，则增加秒
					temp += nextVal + ":60" ;
				}else {
					temp += nextVal;
				}
				temp += "'";
			}
		} else if ((obj.data("itemmode") == UIConst.FITEM_INPUT_DICT) //字典
				     || (obj.data("itemmode") == UIConst.FITEM_INPUT_QUERY) //查询选择
				     || (obj.data("itemtype") == UIConst.FITEM_ELEMENT_SELECT) //下拉框
				     || (obj.data("itemtype") == UIConst.FITEM_ELEMENT_CHECKBOX)) { //多选框
			//如果没有输入查询项，则返回
			if(!va || va.length <= 0){
				return true;
			}
			var array = va.split(",");
			if (array.length > 1) {
				var codesArray = [];
				for (var i = 0; i < array.length;i++) {
					var codes = "";
					codes += "'";
					codes += array[i];
					codes += "'";
					codesArray.push(codes);
				}
				temp += " and ";
				temp += jQuery(n).data("itemcode");
				temp += " in (";
				temp += codesArray.join(",");
				temp += ")";
			} else {
				var symbol = obj.data("symbol") ? obj.data("symbol") : "=";
				//如果没有选择过滤符号，默认为"="，但是下拉列表可能会通过值的like查询
				if(symbol == '='){
					temp += " and ";
					temp += jQuery(n).data("itemcode");
					temp += " "  + symbol + " '";
					temp += va;
					temp += "'";
				}else{
					temp += " and ";
					temp += jQuery(n).data("itemcode");
					temp += " "+symbol+" '%";
					temp += va;
					temp += "%'";
				}
			}
		} else if(obj.data("datatype") == UIConst.DATA_TYPE_NUM){
			var symbolSelect = obj.data("symbolSelect");
			//如果没有选择过滤符号，默认为"="
			var symbol = symbolSelect ? symbolSelect.val() : "=";
			if(va && va.length > 0 && va != _self._numberInputTipMsg){
				temp += " and ";
				temp += obj.data("itemcode");
				temp += (" " + symbol + " '");
				temp += va;
				temp += "'";
			}
			//判断是否是区间查询
			var nextInp = obj.data("nextInp");
			var nextVal = nextInp ? nextInp.val() : null;
			if (nextVal && nextVal.length > 0 && nextVal != _self._numberInputTipMsg) {
				symbolSelect = nextInp.data("symbolSelect");
				symbol = symbolSelect ? symbolSelect.val() : "=";
				temp += " and ";
				temp += obj.data("itemcode");
				temp += (" " + symbol + " '");
				temp += nextVal;
				temp += "'";
			}
		} else {
			//如果没有输入查询项，则返回
			if(!va || va.length <= 0){
				return true;
			}
			var symbolSelect = obj.data("symbolSelect");
			//如果没有选择过滤符号，默认为"like"
			var symbol = symbolSelect ? symbolSelect.val() : "like";
			if(symbol == '='){
				temp += " and ";
				temp += jQuery(n).data("itemcode");
				temp += " "+symbol+" '";
				temp += va;
				temp += "'";
				temp = temp;
			}else{
				temp += " and ";
				temp += jQuery(n).data("itemcode");
				temp += " "+symbol+" '%";
				temp += va;
				temp += "%'";
				temp = temp;
			}
		}
	});
	return temp;
};
/*
 * 关闭当前弹出框
 */
rh.ui.ccexSearch.prototype._btnBind = function() {
	var _self = this;
	var winObj = jQuery("#" + this._id);
	if (this._pCon) {
		winObj = this._pCon;
	}
	this.advBtn.unbind("click").bind("click",function() {
		if (_self._parHandler) {
//			_self._parHandler.listBarTipLoad("加载中...");
			_self._parHandler.listBarTipLoad(Language.transStatic("rh_ui_ccexSearch_string8"));
			setTimeout(function() {
				_self._parHandler._clearSearchValue();//清空普通查询
				var where = _self.getWhere();
				var treeLink = false;
				var check = jQuery(".rh-advSearch-div",winObj).find("input:checked");
				if ((_self._treeLink == true) && (check.prop("checked") == true)) {
					check = true;
				}
				//if (where.length > 0) {
				_self._parHandler.setSearchWhereAndRefresh(where,check);//开始查询
				//}
				jQuery("#" + _self._id).dialog("close");
			},0);
		}
	});
};
/*
 * 关闭当前弹出框
 */
rh.ui.ccexSearch.prototype.del = function() {
	jQuery("#" + this._id).dialog("close");
};
/*
 * 彻底销毁弹出框
 */
rh.ui.ccexSearch.prototype.des = function() {
	if (jQuery("#" + this._id).size() == 1) {
		jQuery("#" + this._id).dialog("destroy");
		jQuery("#" + this._id).empty();
		jQuery("#" + this._id).remove()
	}
};
/*
 * 清空各项值
 */
rh.ui.ccexSearch.prototype.clearAll = function() {
	jQuery(".rh-advSearch-inp").val("");
};
/*
 * 根据参数返回时间
 */
rh.ui.ccexSearch.prototype._time = function(time) {
	var newDay = rhDate.nextDate(System.getVar("@DATE@"),time);
	return newDay;
};
