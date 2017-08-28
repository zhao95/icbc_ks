GLOBAL.namespace("rh.cm");

/**
 * 意见输入对象构造方法
 */
rh.cm.MindInput = function(name) {
	this.name = name;
	//意见ID
	this.mindId = null;
	//是否删除或删除文件
	this._fileIsModify = false;
	//意见输入框对象
	this.mindTextInput = null;
	//修改之前的意见内容
	this.oldMindText = "";
	//文件对象
	this.file = null;
	//保存回调方法
	this.mindSaveFunc = null;
	//意见对象
	this.mindObj = null;
	//意见输入框类型的名称
	this.mindInputName = "";
	//
	this.fileMap = new Map();
	//
	
	//设置输入框对象
	this.setMindTextInput = function(textInputObj) {
		var _self = this;
		this.mindTextInput = textInputObj;
		if(!this.mindTextInput.attr("readonly")){
			this.mindTextInput.mouseout(function() {
				//_self.save();
			});
		}
	}
	
	/**
	 *填充数据 
	 */
	this.fillData = function(data){
		if (null != this.mindId) {  
	  		data[UIConst.PK_KEY] = this.mindId;
	  		data["MIND_ID"] = this.mindId;
		}
		
		if (this._addFlag_) {
			data[UIConst.CARD_STATUS] = true;
		} else {
			data[UIConst.CARD_STATUS] = false;
		}
		
		data["MIND_CONTENT"] = this.mindTextInput.val();
		if(this._fileIsModify || this.mindObj._isDialog){
			data["MIND_FILE"] = this._getMindFileVal();
		}
	}
	
	/**
	 * 装配数据
	 */
	this.assembleData = function(mindDefBean) {
		var _self = this;
		var def;
		
		//获取意见定义信息bean
		if (!mindDefBean) {
			if (_self.name == "REGULAR_MIND") {
				def = _self.mindObj._wfCard.getRegularMind();
			} else if (_self.name == "TERMINAL_MIND") {
				def = _self.mindObj._wfCard.getTerminalMind();
			} else {
				def = _self.mindObj._wfCard.getMindCodeBean();
			}
		} else {
			def = mindDefBean;
		}
			
		//合成数据bean
		var data = _self.mindObj._createMindBean(def);
		
		//装配填写数据
		_self.fillData(data);
		
		//特定数据处理
		if (_self.name == "REGULAR_MIND") {
			data["USUAL_ID"] = _self.mindObj.regularUsualId.val(); // 固定意见类型，批准，同意等
			data["MIND_VALUE"] = _self.mindObj.regularMindValue.val(); // 固定意见类型，批准，同意等
			data["USUAL_CONTENT"] = _self.mindObj.regularMind.val(); // 固定意见类型，批准，同意等
		} else if (_self.name == "TERMINAL_MIND") {
			data["MIND_IS_TERMINAL"] = 1; // 是最终意见
		}
		
		data["S_USER"] = System.getVar("@USER_CODE@");
		data["S_TDEPT"] = System.getVar("@TDEPT_CODE@");
		data["S_FLAG"] = 1;
		
		return data;
	};
	
	/**
	 * 意见是否已修改，包括意见内容和意见文件。
	 */
	this.isModify = function(){
		return this._textIsModify() || this._fileIsModify;
	};
	
	/**
	 * 意见内容是否已修改
	 * */
	this._textIsModify = function(){
		var newVal = this.mindTextInput.val() || "";
		if(newVal == this.oldMindText){
			return false;
		}
		return true;		
	}
	
	/**
	 * 取得意见文件值 
	 */
	this._getMindFileVal = function(){
		if(this.fileMap && this.fileMap.size() > 0 ) {
			var newVal = "";
			this.fileMap.each(function(index,key,value){
				newVal += key + "," + value + ";";
			});
			
			return newVal;
		}
		
		return "";
	}
	
	this.putFile = function(fileId,fileName){
		this._fileIsModify = true; 
		this.fileMap.put(fileId, fileName);
	}
	
	this.afterUploadFile = function(fileData) {
		//取得文件ID和文件名
		var fileId = fileData[0].FILE_ID;
		var fileName = fileData[0].FILE_NAME;
		this._fileIsModify = true;
		this.fileMap.put(fileId, fileName);
		//保存意见内容
		this.save();
		//上传附件之后， 刷新下面的列表
		this.mindObj.refresh();
	}
	
	this.beforeUploadFile = function(file){
		return true;
	}
	
	this.afterDeleteFile = function(fileData){
		this._fileIsModify = true; 
		//取得文件ID和文件名
		var fileId = fileData.FILE_ID;
		//删除文件
		this.fileMap.remove(fileId);
		//保存意见内容
		this.save();
		//删除附件之后， 刷新下面的列表
		this.mindObj.refresh();
		return true;
	}

	/**
	 *初始化意见列表
	 */
	this.initMindPara = function(mindBean) {
		this.mindId = mindBean.MIND_ID;
		
		if (this.mindTextInput) {
			this.mindTextInput.val(mindBean.MIND_CONTENT);
			// 保存为旧的
			this.oldMindText = mindBean.MIND_CONTENT;
		}
		var strMindFile = mindBean.MIND_FILE;
		if(strMindFile && strMindFile.length > 0) {
			this.fileMap = this.parseMindFile(strMindFile);

			this.loadMindFile();
		}
	}
	
	/**
	 * 解析意见文件数据
	 * */
	this.parseMindFile = function(strMindFile){
		var fileMap = new Map();
		var files = strMindFile.split(";");
		for(var i = 0;i < files.length;i++){
			var file = files[i].split(",");
			if(file.length == 2 && file[0].length > 0 && file[1].length > 0){
				fileMap.put(file[0],file[1]);
			}
		}
		return fileMap;
	}

	/**
	 *初始化意见文件
	 */
	this.loadMindFile = function(strMindFile){
		if(this.file) {
			var _fileDatas;
			var fileList = FireFly.doAct("SY_COMM_FILE", "finds", {
				"SERV_ID" : "SY_COMM_MIND",
				"DATA_ID" : this.mindId
			});
			_fileDatas = fileList._DATA_;
			this.file.obj.find(".file").remove();
			this.file.fillData(_fileDatas);
		}		
	}
	
	//保存意见方法
	this.save = function(){
		if(typeof(this.mindSaveFunc) == "function"){
			this.mindSaveFunc.call(this.mindObj);
		}
	}
	
	/**
	 * 是否存在意见输入框 
	 */
	this.exist = function(){
		if(this.mindTextInput && !this.mindTextInput.attr("readonly")){
			return true;
		}
		return false;
	}
	
	/**
	 * 清除意见内容
	 * */
	this.clearContent = function(){
		this.mindTextInput.val("");
		this.file.clear();
		this._fileIsModify = false;
		this.mindId = "";
		this.oldMindText = "";
		this.fileMap.clear();
	}
	
	/**
	 * 给意见框给上红框
	 */
	this.setBlankErr = function() {
		this.mindTextInput.addClass("blank").addClass("blankError");
	}
};

/**
 * 意见对象构造方法
 */
rh.cm.mind = function(options) {
  	var defaults = {
    	"viewer":null,
    	"id":"",
    	"wfCard":null,
    	"servId":null,
    	"dataId":null,
    	"pCon":null,
    	"displayMindSaveBtn":true, //是否显示保存有意见按钮
    	"asyncList":false //是否异步加载意见列表
  	};
  	
  	this._opts = jQuery.extend(defaults,options);	
  	this._data = null;
  	this._servId = this._opts.servId;
  	this._dataId = this._opts.dataId;
  	this._viewer = this._opts.viewer;
  	this._wfCard = this._opts.wfCard;
  	this._mindWfCardView = this._opts.mindWfCardView
  	this._pCon = this._opts.pCon;
  	this._isDialog = this._opts._isDialog;
  	this._asyncList = this._opts._asyncList;
  	
  	this._generalMind = new rh.cm.MindInput("GENERAL_MIND");
  	this._generalMind.mindObj = this;
  	this._terminalMind = new rh.cm.MindInput("TERMINAL_MIND");
  	this._terminalMind.mindObj = this;
  	this._regularMind = new rh.cm.MindInput("REGULAR_MIND");
  	this._regularMind.mindObj = this;
  	this.isMobile = System.getMB("mobile"); //是否是移动版
  	this._rhNext = options._rhNext;
  	
  	// 意见分组框ID
  	this._mindFiledSetId = UIConst.MIND_FIELDSET;
  	
  	// 意见分组框
  	this._mindGroup = null;
  	
  	// 意见类型常量
  	this._mindType = {
  		M_NORMAL : 1,	// 普通意见
  		M_BRUSH : 2, 	// 画笔意见
  		M_ATTACH : 3	// 附件意见
  	};
  	
  	// 回调函数
  	this.callback = this._opts.callback ? this._opts.callback : function(){return true};
  	
  	// 所有意见
  	this.minds = [];
  	
  	// 整个意见的容器
  	this.obj = null;
};

/**
 * 渲染意见
 */
rh.cm.mind.prototype.render = function() {
	var _self = this;
	
	if (_self._isDialog && !_self.canWriteMind()) { //弹出窗口模式下如果无意见配置则意见整体不展示
		return;
	}
	
	if (!_self._wfCard) {
//		alert("工作流没启动");
		alert(Language.transStatic("rh_ui_mind_string1"));
		return;
	}
	
	//如果是分发、签收用户则不能查看意见
	if(_self._wfCard.getDisplayMode() == "MODE_BASE"){
		var showMindMode = System.getVar("@C_CM_MIND_SHOW_EVERYTIME@");
		if (showMindMode != "TRUE") {
			return;
		} 
	}
	
	//初始化意见的默认显示类型
	_self._setDefaultSortType();
		
	//异步ajax装载模板（输入框与列表）、数据（输入框、上传文件与列表）并渲染
	_self._getMindAllData(undefined, function(){
		//初始化意见容器
	  	_self._pCon = _self.obj = _self.getMindContainer();
	  	
	  	//构造送交的按钮组
	  	if ((_self._wfCard.btnRenderMode != 12 && _self._wfCard.btnRenderMode != 112) 
	  			&& _self._viewer.existSubServ("MINDLIST") 
	  			&& _self._wfCard._getBtn("cmSaveAndSend") 
	  			&& _self._mindWfCardView) {
	  		_self._buildBtnGroup();
	  	}
	  	
	  	//如果能填写意见，则初始化意见输入框模板并绑定值
		if (_self.canWriteMind()) {
			_self._initInput();
			_self._bindInputData();
		}
		
		//如果能填写意见或存在列表数据，则初始化意见列表且设置未出部门的意见到意见框，否则隐藏意见分组框
		if (!_self._isDialog && (_self.canWriteMind() || _self.MIND_TYPE_LIST_SIZE > 0) || _self.MIND_TYPE_LIST_SIZE > 0) {
			if (_self._asyncList) {
				if (1==2 && !_self.isMobile) {
					var _asyncLoading = false;
					var _listTag = jQuery("<div style='width:100%;'>" +
							"<span id='mindListConvertor' class='rh-head-icons rh-head-close rh-head-expand'></span>" +
//							"<span style='float:right;line-height:33px;'>意见列表</span>" +
							"<span style='float:right;line-height:33px;'>"+Language.transStatic("rh_ui_mind_string2")+"</span>" +
							"</div>");
					_listTag.unbind("click").bind("click",function(event){
						var _listCon = jQuery("#mindContainer", _self._pCon);
						if (_listCon.length > 0) {
							if (_listCon.is(":visible")) {
								_listCon.hide();
								jQuery("#mindListConvertor").addClass("rh-head-expand");
							} else {
								_listCon.show();
								jQuery("#cmSaveAndSend_dialog").scrollTop(event.clientY);
								jQuery("#mindListConvertor").removeClass("rh-head-expand");
							}
						} else if (!_asyncLoading) {
							_asyncLoading = true;
							_self._initList();
							jQuery("#mindListConvertor").removeClass("rh-head-expand");
							jQuery("#cmSaveAndSend_dialog").scrollTop(event.clientY);
							_asyncLoading = false;
						}
					});
					_listTag.appendTo(_self._pCon);
					//暂时不预加载
					//setTimeout(function(){
					//	_asyncLoading = true;
					//	_self._initList();
					//	jQuery("#mindContainer", _self._pCon).hide();
					//	_asyncLoading = false;
					//}, 200);
				}
			} else {
				_self._initList();
			}
		} else if (_self._mindGroup) {
			_self._mindGroup.hide();
		}
		
		//初始化保存意见按钮
		_self._initSaveMindBtn();
			
		//重置高度
		_self._mindResetHeiWid();
	
		//渲染后
		_self.afterRender();
	});
	
	//向卡片注册保存表单时保存意见方法
	if (_self._wfCard.btnRenderMode != 12 && _self._wfCard.btnRenderMode != 112) {
		_self._viewer.saveMind = function () {_self.saveMind();return true;};
	}
	
	//向工作流注册出部门填写最终意见和固定意见提示
	if (_self._wfCard.btnRenderMode != 12 && _self._wfCard.btnRenderMode != 112) {
		_self.registerSendCallback();
	}
};

/**
 * 意见刷新更新
 */
rh.cm.mind.prototype.refresh = function(sortType, refreshList) {
	var _self = this;
	
	if (_self._isDialog && !refreshList) {
		_self._bindInputData();
	  	_self._mindResetHeiWid();
		return true;
	}
	
    //先清除页面dom中的意见列表
	var odeptCode = System.getUser("ODEPT_CODE");
	jQuery("#mindContent" + odeptCode, _self._pCon).remove();//本单位
	jQuery(".mindODeptTable", _self._pCon).each(function(index, obj){ //其他单位
		var otherOdeptCode = jQuery(obj).attr("deptCode");
		jQuery("#mindContent" + otherOdeptCode, _self._pCon).remove(); 
		jQuery("#mindContent" + otherOdeptCode, _self._pCon).remove();
	});
	jQuery(".mindODeptTable", _self._pCon).remove();   //清除意见标题
	//重新获取数据
	var sortTypeTemp = "";
	if (sortType) {
		sortTypeTemp = sortType;
	}
	_self._getMindAllData(sortTypeTemp,function(){
		_self._bindInputData();
		_self._initList();
	  	_self._mindResetHeiWid();
	});
};

rh.cm.mind.prototype._addGeneralMindSelect = function(generalMindDef) {
	var _self = this;
	_self._removeGeneralMindSelect();
	if(generalMindDef.EXT_JSON) {
		var confObj = JSON.parse(generalMindDef.EXT_JSON);
		var obj = {'type':'select', 'id':"rhMindSelect", 'label':confObj.label};
		var options = [{'name':'','value':''}];
		obj['options'] = options.concat(confObj.select);
		if(confObj.required) {
			obj['notnull'] = true;
		}
		var item = _self._rhNext.addItem(obj, null, "_rhMind");
		if(item) {
			item.valueChanged = function(value, event, name) {
				if(name) {
					_self._generalMind.mindTextInput.val(name);
				}
			}
		}
	}
}

rh.cm.mind.prototype._removeGeneralMindSelect = function(generalMindDef) {
	this._rhNext.removeItem("rhMindSelect");
}

/**
 * 切换普通意见
 */
rh.cm.mind.prototype.changeGeneralMind = function() {
	var _self = this;

	// 普通意见
 	var generalMindDef = _self._wfCard.getMindCodeBean();
	if (generalMindDef && generalMindDef.CODE_ID) {
		//current inputObj's value
		var currInputVal = _self._generalMind.mindTextInput && _self._generalMind.mindTextInput.val();
		(_self._oldGeneralMindDef && _self._oldGeneralMindDef["DEFAULT_CONTENT"] == currInputVal) && (currInputVal = "");
		//initInput
		_self._generalMind.file && _self._generalMind.file.destroy();//必须销毁之前的对象

		_self._addGeneralMindSelect(generalMindDef);

		_self._createGeneralMindFrame(generalMindDef);
		//bindInputData
		jQuery.each((_self._data || []), function(i, m) {
			if (_self._canInitTerminal(m)) {
				//
			} else if(_self._canInitRegular(m)){
				//
			}else if (_self._canInitGeneral(m)) {
				//普通
				_self._generalMind.initMindPara(m);
			}
		});
		//overlay new input value
		currInputVal && _self._generalMind.mindTextInput.val(currInputVal);
		//change mind name and require
		var nameObj = jQuery("#GENERAL_MIND_NAME", _self._pCon);
		nameObj.text(generalMindDef["CODE_NAME"]);
		var requireObj = jQuery("#GENERAL_MIND_REQUIRE", _self._pCon);
		if (generalMindDef["MIND_MUST"] == "1") {
			if (requireObj.length == 0) {
				requireObj = jQuery("<span id='GENERAL_MIND_REQUIRE' class='space require' style='display: inline;'>*</span>");
				nameObj.append(requireObj);
			} else {
				requireObj.show();
			}
		} else {
			(requireObj.length == 1) && requireObj.hide();
		}
		//reset mind height
		_self._mindResetHeiWid();
	}
};

/**
 * 从服务定义获取默认排序方式，默认为按类型排序TYPE
 */
rh.cm.mind.prototype._setDefaultSortType = function() {
	var _self = this;
	_self.defaultSortType = "TIME";
	jQuery.each(_self._viewer._data.ITEMS, function(index, item){
		if (item.ITEM_INPUT_TYPE == UIConst.FITEM_ELEMENT_MIND) {
			//获取字段定义的默认值
			var mindDef = _self._viewer.getItemConfig(item.ITEM_CODE);
			if (mindDef && mindDef.length > 0) {
				var mindDefJson = StrToJson(mindDef);
				_self.defaultSortType = mindDefJson.MIND_LIST_ORDER;
				_self.defaultExpandsionAll = mindDefJson.MIND_LIST_EXPANDSION;
			}
		}
	});
};

/**
 * 从服务器端获取意见输入数据
 */
rh.cm.mind.prototype._getMindInputTmplData = function(){
	var _self = this;
	var reqData = _self._wfCard.getNodeMindObj();
	reqData._isMobile = _self.isMobile;
	_self._wfCard.appendAgentUserFlagTo(reqData);
	var res = FireFly.doAct("SY_COMM_MIND", "showMindInput", reqData, false);
	if (_self._pCon) {
		_self._pCon.children().remove();
		_self._pCon.append(res.inputMindStr);
		_self.addAutoHeight(_self._pCon.find("textarea"));
	}
};

/**
 * 获取意见列表数据
 */
rh.cm.mind.prototype._getMindListData = function(sortType, odeptCode, type, callback){
	var _self = this;
	_self._getMindData(sortType, odeptCode, type, callback);
};

rh.cm.mind.prototype._getLocalDatas = function(locData) {
	var _self = this;
	lData = locData || store.get(_self._wfCard.rhNext.storeKey);
	if (lData) {
		lData.MIND_DATA = lData.MIND_DATA || {};
		var temp = [];
		if (lData.MIND_DATA["REGULAR"]) {
			temp.push(lData.MIND_DATA["REGULAR"]);
		}
		if (lData.MIND_DATA["TERMINAL"]) {
			temp.push(lData.MIND_DATA["TERMINAL"]);
		}
		if (lData.MIND_DATA["GENERAL"]) {
			temp.push(lData.MIND_DATA["GENERAL"]);
		}
	}
	return temp;
};

/**
 * 获取意见全数据
 */
rh.cm.mind.prototype._getMindAllData = function(sortType, callback) {
	var _self = this;
  	_self._getMindData(sortType,undefined,undefined,function(res){
  		//送交对话框模式下如果本地有数据就用本地意见数据，但是列表数据还是用后台的
  		if (_self._isDialog) {
  			var locData = store.get(_self._wfCard.rhNext.storeKey);
  			if (locData) {
  				_self.hasLocal = true;
  				res._DATA_ = _self._getLocalDatas(locData);
  			} else {
  				res._DATA_ = [];//暂时忽略远端数据
  			}
  		}

	  	_self._data = res._DATA_;
	  	_self.initMindInfo();
	  	_self.MIND_TITLE = res.MIND_TITLE;
	  	_self.MIND_TYPE_LIST_SIZE = res.MIND_TYPE_LIST_SIZE;
	  	_self.MIND_ODEPT_SIZE = res.MIND_ODEPT_SIZE;
	  	//如果平台配置的是展开所有机构意见则初始化时就将全机构数据展示出来
	  	if (_self.defaultExpandsionAll == "TRUE") {
	  		_self.firstAllData = res.allData;
	  	}
  	  	if(callback){
  	  		callback.call();
  	  	}
  	});
};

/**
 * 从服务器端取得意见列表数据
 */
rh.cm.mind.prototype._getMindData = function(sortType, odeptCode, type, callback){
	var _self = this;
	var data = {};
  	data["_NOPAGE_"] = "YES";
  	data["DATA_ID"] =  _self._dataId;
  	data["SERV_ID"] = _self._servId;
  	data["_extWhere"] = " AND S_FLAG=1";
	data["SORT_TYPE"] = sortType || _self.defaultSortType;
	data["CAN_COPY"] = _self.canWriteMind();
	data["_isMobile"] = _self.isMobile;
	data["userDoInWf"] = _self._wfCard.getAuthBean().userDoInWf;
	//增加流程节点ID参数
	var nodeInstBean = _self._wfCard.getNodeInstBean();
	if(nodeInstBean){
		data["NI_ID"] = nodeInstBean["NI_ID"];
	}
	//增加是否允许删除意见的变量
	var delMindDef = _self._wfCard.getCustomVarContent("DEL_MIND");
	if(delMindDef == "true"){
		data["DEL_MIND"] = "true";
	}
	//调用方法
	var reqMethod = "displayMindTitle";
	if (type && type == "listMind") {
		reqMethod = "displayMindList";
		data["ODEPT_CODE"] = odeptCode;
	} else {
		data["EXPANDSION_ALL"] = _self.defaultExpandsionAll;
	}
	
	_self._wfCard.appendAgentUserFlagTo(data); //如果是委托办理，则增加委托用户标识
	//异步ajax
	FireFly.doAct("SY_COMM_MIND", reqMethod, data, false, true, callback);
};

/**
 * 
 */
rh.cm.mind.prototype.initMindInfo = function(){
	var _self = this;
	
	jQuery.each(_self._data, function(i, m) {
		if (_self._canInitTerminal(m)) { 
			_self.__terminalData = m;
		} else if (_self._canInitRegular(m)) {
			_self.__regularData = m;
		} else if (_self._canInitGeneral(m)) {
			_self.__generalData = m;
		}
	});
};

/**
 * 获取意见分组框存放内容的对象
 */
rh.cm.mind.prototype.getMindContainer = function() {
	var _self = this;
	
	// 存放意见的容器
	var mindContainer = null;
	
	if (_self._pCon) {
		return _self._pCon;
	}

	// 查找意见分组框
	this._mindGroup = this._viewer.form.getGroup(this._mindFiledSetId);
	if (this._mindGroup == undefined || this._mindGroup.length == 0) {
		this._mindGroup = this._viewer.form.getGroup(null, UIConst.FITEM_ELEMENT_MIND);
	}
	if (this._mindGroup && this._mindGroup.length == 1) {
		var contents = this._mindGroup.find(".formContent");
		if (contents.length > 0) { // PC版存放意见的div为formContent
			mindContainer = contents.first();
		} else {
			mindContainer = this._mindGroup.find(".mbCard-form-table").first();
		}
	} else {
//		var fs = new rh.ui.FieldSet({"formContainerId":this._mindFiledSetId,"legendName":"留言意见"});
		var fs = new rh.ui.FieldSet({"formContainerId":this._mindFiledSetId,"legendName":Language.transStatic("rh_ui_mind_string3")});
		this._mindGroup = fs.getObj();
		this._mindGroup.appendTo(this._viewer.formCon);
		//如果是模板显示页面，添加模板的样式
		if (this._viewer._data.SERV_CARD_TMPL == 1) { //启用了模板
			this._mindGroup.addClass("temp_container");
		}
		
		mindContainer = fs.getFormContent();
	}
	return mindContainer;
};

/**
 * 构造送交的按钮组
 */
rh.cm.mind.prototype._buildBtnGroup = function(){
	var _self = this;
	_self.btnBar = jQuery("<div class=\"rhCard-btnBar\"></div>").appendTo(_self._pCon);
	_self.btnCon = jQuery("<div id=\"funcBtnDivCon\"></div>").appendTo(_self.btnBar);
	_self._mindWfCard = _self._mindWfCardView;
	var renderMode = _self._wfCard.btnRenderMode || 10; //渲染模式请参考rhWfCardView.js中的说明
	if(renderMode == 11){
		renderMode = 0;
	}else if(renderMode == 111){
		renderMode = 100;
	}
	_self._mindWfCard.btnRenderMode = renderMode;
	_self.cmSaveAndSendItemBean = _self._wfCard._getBtn("cmSaveAndSend").dataObj;
	_self._mindWfCard.saveAndSendGroup = _self._mindWfCard._bldBtnGroup(
		_self.cmSaveAndSendItemBean.ACT_NAME, 
		_self.cmSaveAndSendItemBean.ACT_CODE, 
		true, 
		_self.cmSaveAndSendItemBean
	);
	_self._mindWfCard.wfSendBtnGroup = []; //流程按钮组
	_self._mindWfCard._bldNextBtnBar(_self._wfCard.wfSendBtns);
	if(renderMode == 0 || renderMode == 100){
		_self._mindWfCard.saveAndSendGroup.parent().hide();
	}
};

/**
 * 初始化保存意见按钮
 */
rh.cm.mind.prototype._initSaveMindBtn = function() {
	var _self = this;
	var saveMindBtn = jQuery("#saveMindBtn", _self._pCon);
	if (saveMindBtn.length > 0) {
		if (!_self._isDialog) {
			saveMindBtn.unbind("click").bind("click",function(event){
				var saveResult = _self.saveMind();
				if(saveResult){
					_self.refresh();
					if (_self._viewer && _self._viewer.existSubServ("MINDLIST")) {
						_self._viewer.setRefreshFlag(_self._viewer.servId, true);
					}
				}
			});
		} else {
			//隐藏保存按钮
			saveMindBtn.hide();
		}
	}
};

/**
 * 注册出部门填写最终意见和固定意见提示
 */
rh.cm.mind.prototype.registerSendCallback = function(){
	var _self = this;
	_self._wfCard.callback = function(idArray, nameArray) {
		if (_self._wfCard.binderType == "ROLE") {
			//如果是角色则不判断
			return true;
		}
		
		var	ifOutDept = false; // 是否出部门? 默认没有出部门或者机构
		// 判断用户是否送出部门？
		jQuery(idArray).each(function(index, user){
		   	if (user.indexOf("usr:") == 0) {
			   	userCode = user.substr(user.indexOf("usr:") + 4);
			   	var userBean = FireFly.byId("SY_ORG_USER", userCode);
				ifOutDept = userBean.TDEPT_CODE && userBean.TDEPT_CODE != System.getUser("TDEPT_CODE");
			   	return;
		   	}
		});
		
		if (ifOutDept) { // 出部门或者机构了
	   		
	  		 // 如果锁定，必须填写固定意见
  			if (_self._hasRegularMindInput()) { // 固定意见
  				if (_self._hasWriteRegularMind()) { 
  					//已选择固定意见
	  				return true;
	  			} else {
	  				var regularMind = _self._wfCard.getRegularMind();
	  				top.Tip.addTip(Language.transStatic('rh_ui_mind_string4') + regularMind.CODE_NAME + "！", "warn");
	  				return false;
	  			}
  			}
  			
  			if (_self._hasTerminalMindInput()) {
  				if (!_self._hasWriteTerminalMind()) { // 没有填写意见
  					top.Tip.addTip(Language.transStatic('rh_ui_mind_string5') + currMind.CODE_NAME + "！", "warn");
			   		return false;
		   		}
  			}
	   	}
		return true;
  	};
};

/**
 * 确定意见是添见还是修改状态
 */
rh.cm.mind.prototype._addOrModify = function(mindDef, mind, mindData) {
	var _self = this;
	
	if (mindData) {
		mindDef["MIND_ID"] = mindData["MIND_ID"];
		if (_self.hasLocal && mindData[UIConst.CARD_STATUS]) {
			mind._addFlag_ = true;
		} else {
			mind._addFlag_ = false;
		}
	} else {
		mind._addFlag_ = true;
	}
	mind.mindId = mindDef["MIND_ID"];
};

/**
 * 构造意见输入框
 */
rh.cm.mind.prototype._createMindFrame = function() {
	var _self = this;
	// 固定意见
	var regularMindDef = _self._wfCard.getRegularMind();
	if (regularMindDef && regularMindDef.CODE_ID) {
		_self._createRegularMindFrame(regularMindDef);
	}
	
	// 最终意见
	var terminalMindDef = _self._wfCard.getTerminalMind();
	if (terminalMindDef && terminalMindDef.CODE_ID) {
		_self._createTerminalMindFrame(terminalMindDef);
	}
	
	// 普通意见
 	var generalMindDef = _self._wfCard.getMindCodeBean();
	if (generalMindDef && generalMindDef.CODE_ID) {
		_self._createGeneralMindFrame(generalMindDef);
	}
};

/**
 * 创建固定意见
 */
rh.cm.mind.prototype._createRegularMindFrame = function(regularMindDef) {
	var _self = this;
	var regularMind = _self._regularMind;
  	
  	//初始化输入框label
	regularMind.mindInputName = regularMindDef.CODE_NAME;	
	//初始化固定意见选择事件
	jQuery("#REGULAR_MIND", _self._pCon).unbind("click").bind("click",function(event){
		_self._chooseMind(event);
	});
	jQuery("#chooseRegularMind", _self._pCon).unbind("click").bind("click",function(event){
		_self._chooseMind(event);
	});	
	jQuery("#cancelRegularMind", _self._pCon).unbind("click").bind("click",function(event){
		_self._cancelChoose(event);
	});		
  	//索取固定意见ID对象
	_self.regularUsualId = jQuery("#USUAL_MIND_ID", _self._pCon);
  	//索取固定意见值对象
	_self.regularMindValue = jQuery("#USUAL_MIND_VALUE", _self._pCon);
  	//索取固定意见文本对象
	_self.regularMind = jQuery("#REGULAR_MIND", _self._pCon);
	//注入保存时方法
	_self._regularMind.mindSaveFunc = _self.saveRegularMind;
	//设置输入框对象
	regularMind.setMindTextInput(jQuery("#REGULAR_MIND_CONTENT", _self._pCon));
	//初始化默认值
	regularMind.mindTextInput.val(regularMindDef["DEFAULT_CONTENT"] || "");
	_self.regularUsualId.val(regularMindDef["DEFAULT_USUAL_ID"] || "");
	_self.regularMindValue.val(regularMindDef["DEFAULT_VALUE"] || "");
	_self.regularMind.val(regularMindDef["DEFAULT_CONTENT"] || "");
	//初始化MIND_ID
	_self._isDialog && _self._addOrModify(regularMindDef, regularMind, _self.__regularData);
	//创建文件上传按钮
//	_self._createFile(regularMind, false);
	//绑定常用批语
	_self._bldUsualMind(regularMind);
};

/**
 * 构造最终意见输入框
 */
rh.cm.mind.prototype._createTerminalMindFrame = function(terminalMindDef) {
	var _self = this;
	var terminalMind = _self._terminalMind;
  	
	//初始化输入框label
	terminalMind.mindInputName = terminalMindDef.CODE_NAME;
  	//最终意见是否是只读？
  	//？？？该值不传给_createFile方法吗？？？//var readonly = ! _self._wfCard.canWriteTerminalMind();
	//注入保存时方法
	terminalMind.mindSaveFunc = _self.saveTerminalMind;
	//设置输入框对象
	terminalMind.setMindTextInput(jQuery("#TERMINAL_MIND", _self._pCon));
	//初始化默认值
	terminalMind.mindTextInput.val(terminalMindDef["DEFAULT_CONTENT"] || "");
	//初始化MIND_ID
	_self._isDialog && _self._addOrModify(terminalMindDef, terminalMind, _self.__terminalData);
	//构建附件上传
//	_self._createFile(terminalMind, false);
	//绑定常用批语
	_self._bldUsualMind(terminalMind);
};

/**
 * 构造普通意见输入框，包括：自拟意见、手写意见、附件意见、固定意见(包含自拟意见)
 */
rh.cm.mind.prototype._createGeneralMindFrame = function(generalMindDef) {
	var _self = this;
	var generalMind = _self._generalMind;
	_self._oldGeneralMindDef = generalMindDef;

  	//初始化输入框label
	generalMind.mindInputName = generalMindDef.CODE_NAME;
	//注入保存时方法
	generalMind.mindSaveFunc = _self.saveGeneralMind;
	//设置输入框对象
	generalMind.setMindTextInput(jQuery("#GENERAL_MIND", _self._pCon));
	//初始化默认值
	generalMind.mindTextInput.val(generalMindDef["DEFAULT_CONTENT"] || "");
 	//初始化MIND_ID
	_self._isDialog && _self._addOrModify(generalMindDef, generalMind, _self.__generalData);
	//构建文件上传
//	_self._createFile(generalMind, false);
	//绑定常用批语
	_self._bldUsualMind(generalMind);
};

/**
 * 构建附件
 */
rh.cm.mind.prototype._createFile = function(mind, readonly) {
	// 移动版不显示上传按钮
	if (Browser.ignoreButton({"ACT_MOBILE_FLAG":"2"})) {
		readonly = true;
	}
	var _self = this;
	try {
		// 创建文件上传按钮
		var config = {"SERV_ID":"SY_COMM_MIND", "FILE_CAT":"REGULAR_MIND_FILE", "VALUE":15, "TYPES":"*.*"};
		_self._isDialog && (config["DATA_ID"] = mind.mindId);
		if(readonly){
			//设置文件操作权限为只读
			config.VALUE = 9;
		}
	
		mind.file = new rh.ui.File({
			"config" : config
		});
		jQuery("#upload_" + mind.name, _self._pCon).empty();
		jQuery("#upload_" + mind.name, _self._pCon).append(mind.file.obj);
	
		mind.file.afterUploadCallback = function(fileData){
			mind.afterUploadFile(fileData);
			if (_self._viewer && _self._viewer.existSubServ("MINDLIST") && !_self._isDialog) {
				_self._viewer.setRefreshFlag(_self._viewer.servId, true);
			}
		};
		
		mind.file.beforeUploadCallback = function(file){
			return mind.beforeUploadFile(file);
		};
		
		mind.file.afterDeleteCallback = function(fileData){
			mind.afterDeleteFile(fileData);
			if (_self._viewer && _self._viewer.existSubServ("MINDLIST") && !_self._isDialog) {
				_self._viewer.setRefreshFlag(_self._viewer.servId, true);
			}
		};	
		
		// 初始化上传按钮
		mind.file.initUpload();
	} catch(e) {
		//
	}
};

/**
 * 构建常用批语
 */
rh.cm.mind.prototype._bldUsualMind = function(mind){
	var _self = this;
    var mindTextInput = jQuery("#" + mind.name, _self._pCon);
    if (mind.name == "REGULAR_MIND") {
    	mindTextInput = jQuery("#REGULAR_MIND_CONTENT", _self._pCon);
    }
    
    var commTag =  null;
	if (_self.isMobile) { //移动版
		var _isRegular = mind.name.indexOf("REGULAR") >= 0;
		var inputId = _isRegular ? mind.name + "_CONTENT" : mind.name;
		var mindInput = jQuery("#" + inputId, _self._pCon);
		jQuery(".mb-mind-usual", mindInput.parent()).remove();
//		commTag = jQuery("<span class='mb-mind-button mb-mind-usual'><a href='javascript:void(0)' class='mb-mind-a'>常用批语</a></span>");
		commTag = jQuery("<span class='mb-mind-button mb-mind-usual'><a href='javascript:void(0)' class='mb-mind-a'>"+Language.transStatic('rh_ui_mind_string6')+"</a></span>");
		mindInput.after(commTag);
		if (_isRegular) {
//			var clearRegularBtn = jQuery("<span class='mb-mind-usual-clear'><a href='javascript:void(0)' class='mb-mind-a'>清除</a></span>");
			var clearRegularBtn = jQuery("<span class='mb-mind-usual-clear'><a href='javascript:void(0)' class='mb-mind-a'>"+Language.transStatic('rh_ui_mind_string7')+"</a></span>");
			jQuery("#" + mind.name, _self._pCon).after(clearRegularBtn);
			clearRegularBtn.click(function(event){
				_self._cancelChoose(event);
			});
		}
		commTag.click(function(){
			var selectView,
				handler = {"setValue" : function(id, name){
					mindTextInput.val(name);
				}}, 
				temp = {
					"dictId":"SY_COMM_USUAL", 
					"pCon":jQuery("body"), 
					"parHandler":handler, 
					"config":""
				};
		    selectView = new mb.vi.selectList(temp);
		    selectView._bldWin(event,true);
		    selectView.show();
		});
	} else {
		var uploadArea = jQuery("#upload_" + mind.name, _self._pCon);
		uploadArea.find(".uploadBtn").addClass("wp");
		uploadArea.find("OBJECT").addClass("fl");
//		commTag = jQuery("<span class='fl ml30 mt5 unl cp' style='font-size:14px;'>常用批语</span>");
		commTag = jQuery("<span class='fl ml30 mt5 unl cp' style='font-size:14px;'>"+Language.transStatic('rh_ui_mind_string6')+"</span>");
		uploadArea.find(".uploadBtntr").after(commTag);
		
		var opts = {"typeCode":"MIND"
			,"optionType":"single"
			,"fieldObj":mindTextInput
			,"optObj":commTag};
		Select.usualContent(opts,this._viewer,null,[500,400]);
	}
};

/**
 * 初始化输入框
 */
rh.cm.mind.prototype._initInput = function(){
	var _self = this;
	
	//获取输入框意见数据（不包括上传文件）
	_self._getMindInputTmplData();
	
	//设置意见输入框对象的属性及初始化变量
	_self._createMindFrame();
};

/**
 * 给INPUT模板绑定数据
 */
rh.cm.mind.prototype._bindInputData = function(){
	var _self = this;
	
	if (jQuery.isEmptyObject(this._data)) {
		return;
	}

	//给INPUT模板注入或更新值
	jQuery.each(_self._data, function(i, m) {
		if (_self._canInitTerminal(m)) {
			//
			_self._terminalMind.initMindPara(m);
		} else if(_self._canInitRegular(m)){
			//
			var regularMind = _self._wfCard.getRegularMind(); //  取出当前意见类型
			//写固定意见的值
			if(_self.regularUsualId){
				_self.regularUsualId.val(m.USUAL_ID);
			}
			if(_self.regularMindValue){
				_self.regularMindValue.val(m.MIND_VALUE);
			}
			if(_self.regularMind){
				_self.regularMind.val(m.USUAL_CONTENT);
			}
			_self._regularMind.initMindPara(m);
		}else if (_self._canInitGeneral(m)) {
			//
			_self._generalMind.initMindPara(m);
		}
	});
};

rh.cm.mind.prototype._canInitTerminal = function(data){
	var _self = this;
	var doUser = _self._wfCard.getDoUserBean();
	// 本部门的最终意见，且还没有出过部门
	return (
		_self._hasTerminalMindInput()
		&& data.MIND_IS_TERMINAL == 1 
		//&& data.S_FLAG == 2 
		&& doUser["TDEPT_CODE"] == data.S_TDEPT
	);
};

rh.cm.mind.prototype._canInitRegular = function(data){
	var _self = this;
	var doUser = _self._wfCard.getDoUserBean();
	return (
		_self._hasRegularMindInput() 
		&& ("USUAL_ID" in data) 
		//&& data.S_FLAG == 2 
		&& doUser["TDEPT_CODE"] == data.S_TDEPT
	);
};

rh.cm.mind.prototype._canInitGeneral = function(data){
	var _self = this;
	var authBean = _self._wfCard.getAuthBean();
	var doUser = _self._wfCard.getDoUserBean();
	//当前人能正在办理本审批单且同一节点且自拟意见
	return (
		authBean.userDoInWf == "true" 
		&& doUser["USER_CODE"] == data.S_USER 
		//&& data.S_FLAG == 2 
		&& data.WF_NI_ID == _self._wfCard.getNodeInstBean().NI_ID 
		&& data.MIND_TYPE == _self._mindType.M_NORMAL 
		&& data.MIND_IS_TERMINAL != 1
	);
};

/**
 * 初始化列表且初始化输入框上传文件
 */
rh.cm.mind.prototype._initList = function() {
	var _self = this;
	
	//确定意见容器
	if (!_self.mindContainer) {
		_self.mindContainer = jQuery("<div id='mindContainer'></div>");
		_self.mindContainer.appendTo(this._pCon);		
	}
	
	//已从后台获取到列表模板并且列表有数据则渲染列表
	if(_self.MIND_TITLE && _self.MIND_TYPE_LIST_SIZE > 0){
		//插入列表页面dom
		jQuery(_self.MIND_TITLE).appendTo(_self.mindContainer);
		//初始化列表事件
		_self._initMindListEvent();
		//如果平台配置的是展开所有机构意见则初始化时就将全机构数据展示出来
		if (_self.firstAllData) {
			_self._expansionFisrtAll(_self.firstAllData);
		}
	}
};

/**
 * 一进页面，展开所有的意见列表（针对意见跨机构情况，默认只先显示本机构内的意见）
 */
rh.cm.mind.prototype._expansionFisrtAll = function(firstAllData) {
	var _self = this;
	
	jQuery.each(firstAllData, function(index, item){
		var odeptCode = item.odeptCode;
		
	  	jQuery("#mindTable" + odeptCode, _self._pCon).remove();
	  	var mindListObj = jQuery("#mindContent" + odeptCode, _self._pCon).append(item.MIND_LIST).removeClass("none");
	  	
	  	//机构上的图标打开状态
	  	var obj = jQuery("#mind" + odeptCode, _self._pCon).find(".mindOdpt");
		var objParent = obj.find(".rh-advSearch__iconC");
		if ((objParent.attr("class") || "").indexOf("icon-card-open") >=0) {
			objParent.addClass("icon-card-close").removeClass("icon-card-open");
		} else if (objParent.attr("class").indexOf("icon-card-close") >=0) {
			objParent.addClass("icon-card-open").removeClass("icon-card-close");
		}
		
		//默认的排序
		var sortType =  _self.defaultSortType;
		obj.parent().find("[sortType=" + sortType + "]").addClass("mindTypeSelected");
		
	  	_self._mindResetHeiWid();
	});
};

/**
 * 初始化其他机构的意见的列表
 */
rh.cm.mind.prototype._initMindListEvent = function() {
	var _self = this;
	var odeptCode = System.getVar("@ODEPT_CODE@");
	
	//绑定机构名称连接的click事件
	if (!_self.isMobile) {
		jQuery(".mindOdpt", _self._pCon).each(function(){
			var obj = jQuery(this);
			var iconObj;
			if (obj.attr("deptCode") == odeptCode) { //本机构
				iconObj = jQuery("<span class='icon-card-close rh-advSearch__iconC'>&nbsp;&nbsp;&nbsp;&nbsp;</span>");
			} else { //跨机构
				iconObj = jQuery("<span class='icon-card-open rh-advSearch__iconC'>&nbsp;&nbsp;&nbsp;&nbsp;</span>");
			}
			iconObj.appendTo(obj.attr("title",Language.transStatic('rh_ui_mind_string8')));
//			iconObj.appendTo(obj.attr("title","点击查看详细"));
			iconObj.bind("click", function(){
				var selfObj = jQuery(this);
				var odeptObj = selfObj.parent();
				var odeptCode = odeptObj.attr("deptCode");
				if ((selfObj.attr("class") || "").indexOf("icon-card-open") >=0) {
					selfObj.addClass("icon-card-close").removeClass("icon-card-open");
				} else if (selfObj.attr("class").indexOf("icon-card-close") >=0) {
					selfObj.addClass("icon-card-open").removeClass("icon-card-close");
				}
				var mindContentObj = jQuery("#mindContent" + odeptCode, _self._pCon);
				if(mindContentObj.contents().length == 0){ // 没有意见内容，则重新装载
					var sortType =  odeptObj.attr("sortType") || _self.defaultSortType;
					//从远端获取某机构意见列表并渲染
					_self.displayMindList(odeptCode, sortType);
					odeptObj.parent().find(".mindSortClick").removeClass("mindTypeSelected");
					var sortType =  odeptObj.attr("sortType")?odeptObj.attr("sortType"):_self.defaultSortType;
					odeptObj.parent().find("[sortType=" + sortType + "]").addClass("mindTypeSelected");
				} else if (mindContentObj.hasClass("none")) {
					mindContentObj.removeClass("none");
				} else {
					mindContentObj.addClass("none");
				}
				_self._mindResetHeiWid();
			});
		});
	}
	
	//绑定意见显示方式按钮的click事件(按时间查看、按类型查看)
	jQuery(".mindSortClick", _self._pCon).bind("click",function(){
		var obj = jQuery(this);
		//点击列表类型查看的时候对前面的显示标志进行联动
		var objParent = obj.parent().find(".rh-advSearch__iconC");
		if ((objParent.attr("class") || "").indexOf("icon-card-open") >=0) {
			objParent.addClass("icon-card-close").removeClass("icon-card-open");
		}
		var odeptCode = obj.attr("deptCode");
		var sortType =  obj.attr("sortType");
		//从远端获取某机构意见列表并渲染
		_self.displayMindList(odeptCode, sortType);
		obj.parent().find(".mindSortClick").removeClass("mindTypeSelected");
		var sortType = obj.attr("sortType")?obj.attr("sortType"):"TYPE";
		obj.parent().find("[sortType=" + sortType + "]").addClass("mindTypeSelected");
		//_self._mindResetHeiWid();
	});
	
	//绑定数据操作事件
	_self._bindCopyMindEvent();
	_self._bindDeleteMindEvent();
};

/**
 * 根据机构显示意见列表
 */
rh.cm.mind.prototype.displayMindList = function(odeptCode, sortType) {
	var _self = this;
	//远程异步ajax获取列表数据
	var res = _self._getMindListData(sortType, odeptCode, "listMind",function(res){
		//插入dom
	  	jQuery("#mindTable" + odeptCode, _self._pCon).remove();
	  	jQuery("#mindContent" + odeptCode, _self._pCon).append(res.MIND_LIST).removeClass("none");
	  	//重置高度
	  	_self._mindResetHeiWid();
	  	//绑定数据操作事件
		_self._bindCopyMindEvent();
		_self._bindDeleteMindEvent();
	});
};

/**
 * 绑定复制意见事件
 */
rh.cm.mind.prototype._bindCopyMindEvent = function() {
	var _self = this;
	
	//点击意见复制按钮
  	jQuery(".mindTable", _self._pCon).find("a.COPY_MIND").unbind("click").bind("click", function(){
		var _copyBtn = jQuery(this);
		
		var mindContent = _copyBtn.parent().find("span.MIND_CONTENT").text();
		
		var mindFileObj = _copyBtn.parent().find("a.MIND_FILE");
		var mindFileIDs = new Array();

		if(mindFileObj.length > 0){
			for(var i=0;i<mindFileObj.length;i++){
				mindFileIDs.push(jQuery(mindFileObj[i]).attr("fileID"));
			}
		}
		var html = new Array();
//		html.push("<div id='DIV_MIND_SELECT' title='请选择意见类型'>");
		html.push("<div id='DIV_MIND_SELECT' title='"+Language.transStatic('rh_ui_mind_string9')+"'>");
		html.push("<table>");
//		html.push("<tr class='h25'><td colspan='2'>请选择意见类型</td></tr>");
		html.push("<tr class='h25'><td colspan='2'>"+Language.transStatic('rh_ui_mind_string9')+"</td></tr>");
		
		if(_self._terminalMind.exist()){
			html.push("<tr><td class='w30 h25'><input type='radio' name='mindTypeCode' value='_terminalMind'></td>");
			html.push("<td><span class='mindTypeName'>" + _self._terminalMind.mindInputName);
			html.push("</span></td></tr>");
		}
		if(_self._regularMind.exist()){
			html.push("<tr><td class='w30 h25'><input type='radio' name='mindTypeCode' value='_regularMind'></td>");
			html.push("<td><span class='mindTypeName'>" + _self._regularMind.mindInputName);
			html.push("</span></td></tr>");
		}
		if(_self._generalMind.exist()){
			html.push("<tr><td class='w30 h25'><input type='radio' name='mindTypeCode' value='_generalMind'></td>");
			html.push("<td><span class='mindTypeName'>" + _self._generalMind.mindInputName);
			html.push("</span></td></tr>");
		}

		html.push("</table>");
		html.push("</div>");
		
		var dialog = jQuery(html.join("")).addClass("dictDialog");
		
		// 点击文字也能选中
		dialog.find(".mindTypeName").css({"cursor":"pointer"}).click(function(){
			jQuery(this).parent().parent().find("input").first().click();
		});
		
		var hei = 200;
		var wid = 500;
		
		var scroll = RHWindow.getScroll(_parent.window);
	    var viewport = RHWindow.getViewPort(_parent.window);
	    var top = scroll.top - viewport.height / 2 + 2 *hei;
	    var posArray = [];
	    
	    posArray[0] = "";
	    posArray[1] = top;
		
		dialog.dialog({
			autoOpen: true,
			height: hei,
			width: wid,
			show: "bounce", 
	        hide: "puff",
			modal: true,
			resizable: false,
			position: posArray,
			buttons: {
				"确认": function() {
//				Language.transStatic("rh_ui_card_string59"): function() {	
					dialog.dialog("close");
					var selectItem = dialog.find("input[type=radio]:checked");

					if(selectItem.length == 1){
						var itemVal = selectItem.attr("value");
						var mindInputObj = null;
						if(itemVal == "_generalMind"){
							mindInputObj = _self._generalMind;
						}else if(itemVal == "_terminalMind"){
							mindInputObj = _self._terminalMind;
						}else if(itemVal == "_regularMind"){
							mindInputObj = _self._regularMind;
						}
						
						if(mindInputObj.mindTextInput.val() != ""){
//							if(!confirm("是否覆盖已有意见内容！")){
							if(!confirm(Language.transStatic("rh_ui_mind_string10"))){
								return;
							}
						}
						
						mindInputObj.mindTextInput.val(mindContent);
						mindInputObj.save();
						//如果存在意见附件，则复制意见附件
						for(var i =0;i<mindFileIDs.length;i++){
							var mindFileID = mindFileIDs[i];
							var paramBean = {
								"OLD_FILE_ID" : mindFileID,
								"NEW_SERV_ID" : "SY_COMM_MIND",
								"NEW_DATA_ID" : mindInputObj.mindId,
								"NEW_FILE_CAT" : mindInputObj.file._opts.config.FILE_CAT
							};

							//复制文件
							var resultData = FireFly.doAct("SY_COMM_FILE", "copyFile", paramBean);
							//显示文件复制结果，并保存
							if (resultData[UIConst.RTN_MSG] 
								&& resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
								mindInputObj.putFile(resultData.FILE_ID,resultData.FILE_NAME);
							}
						}
						
						if(mindFileIDs.length > 0){
							mindInputObj.save();
							mindInputObj.loadMindFile();
						}
						_self.refresh();
						mindInputObj.mindTextInput.focus();
					}
					return true;
				},
				"取消": function(){
//				Language.transStatic("rh_ui_card_string18"): function(){	
					dialog.dialog("close");
					return false;
				}
			}
		});
		// 注释掉头部关闭按钮
//		dialog.parent().find(".ui-dialog-titlebar-close").hide();
		var btns = jQuery(".ui-dialog-buttonpane button",dialog.parent()).attr("onfocus","this.blur()");
		btns.first().addClass("rh-small-dialog-ok");
		btns.last().addClass("rh-small-dialog-close");
		dialog.parent().addClass("rh-small-dialog").addClass("rh-bottom-right-radius");
	    jQuery(".ui-dialog-titlebar", _self._pCon).last().css("display","block");//设置标题显示
	});		
};

/**
 * 绑定删除意见事件
 */
rh.cm.mind.prototype._bindDeleteMindEvent = function(){
	var _self = this;
	
	//点击意见删除按钮
  	jQuery(".mindTable", _self._pCon).find("a.DELETE_MIND").unbind("click").bind("click", function(){
//  		if(confirm("确定删除意见？")){
  		if(confirm(Language.transStatic("rh_ui_mind_string11"))){	
  			var delBtn = jQuery(this);
  			var data = {};
  			var mindId = delBtn.attr("MIND_ID");
  			data["_PK_"] = mindId;
  			var res = FireFly.doAct("SY_COMM_MIND", "delete", data, true);
  			_self.refresh(null, true);

  			if (_self._regularMind) { // 清除固定意见框
  				if(_self._regularMind && _self._regularMind.mindId == mindId){
  					_self._regularMind.clearContent();
  				}
  			}
  			
  			if (_self._generalMind) { //清除普通意见框
  				if(_self._generalMind && _self._generalMind.mindId == mindId){
  					_self._generalMind.clearContent();
  				}
  			}
  			
  			if (_self._terminalMind) { //清除最终意见框
  				if(_self._terminalMind && _self._terminalMind.mindId == mindId){
  					_self._terminalMind.clearContent();
  				}
  			}
  		}
  	});
};

/**
 * 是否能输入意见
 */
rh.cm.mind.prototype.canWriteMind = function() {
	var _self = this;
	var authBean = _self._wfCard.getAuthBean();
	if ((_self._wfCard.btnRenderMode == 12 || _self._wfCard.btnRenderMode == 112) && !_self._isDialog) {
		return false;
	}
	if (_self._wfCard != null) {
		var mindCodeBean = this._wfCard.getMindCodeBean();
		if (mindCodeBean && mindCodeBean.CODE_ID) {
			return true;
		}
		mindCodeBean = this._wfCard.getTerminalMind();
		if (mindCodeBean && mindCodeBean.CODE_ID) {
			return true;
		}
		mindCodeBean = this._wfCard.getRegularMind();
		if (mindCodeBean && mindCodeBean.CODE_ID) {
			return true;
		}
	}
	return false;
};

/**
 * 获取意见框自动高度
 */
rh.cm.mind.prototype.addAutoHeight = function(mindTextInput){
	var _self = this;
	var thisTextObj = null;
	mindTextInput.each(function(){
		if (jQuery(this, _self._pCon).is(":visible")) {
			thisTextObj = jQuery(this, _self._pCon);
		}
	});
	if (null == thisTextObj) {
		return;
	}
	thisTextObj.css({"min-height":"50px", "line-height":"normal","overflow":"auto"});
	var hiddTextObj = document.getElementById("_textareacopy");
	//新建一个textarea用户计算高度
    if(!hiddTextObj){
		var hiddTextStyle = "position:absolute;left:-9999px;visibility:hidden;white-space:nowrap;";
		hiddTextObj = jQuery("<span id='_textareacopy' style='" + hiddTextStyle + "'></span>");
		//hiddSpanObj
		jQuery("body").append(hiddTextObj);
	} else {
		hiddTextObj = jQuery(hiddTextObj);
	}
	//添加计算字符串长度方法
	String.prototype.visualLength = function() {
		var ruler = hiddTextObj;
		ruler.text(this); 
		return ruler[0].offsetWidth; 
	}
	_self.getTextAreaRows(thisTextObj);
	//绑定事件
	thisTextObj.unbind("keyup").bind("keyup", function(){
		_self.getTextAreaRows(thisTextObj);
		_self._mindResetHeiWid();
	});
	//页面初始化
	thisTextObj.unbind("click").bind("click", {"isClick":false}, function(event){
		if (!event.data["isClick"]) { //用于页面初始化
			_self.getTextAreaRows(thisTextObj);
			_self._mindResetHeiWid();
			event.data["isClick"] = true;
		}
	});
};

/**
 * 修改意见输入框自动高度
 * @param {Object} jQueryTextArea 意见框jQuery对象
 */
rh.cm.mind.prototype.getTextAreaRows = function(jQueryTextArea){
	var textAreaArry = (jQueryTextArea.val() || "").split("\n");
	var rows = 0;
	var textAreaWid = jQueryTextArea.width();
	for (var i = 0; i < textAreaArry.length; i++) {
		var thisStr = textAreaArry[i];
		var thisLength = thisStr.visualLength();
		if (thisLength > textAreaWid) {
			rows += Math.ceil(thisLength / textAreaWid);//向上取整，只要有小数就加一
		} else {
			rows += 1;
		}
	}
	rows += 1;
	jQueryTextArea.attr("rows", rows > 3 ? rows : 4);
};

/**
 * 判断有没有配置最终意见
 * @return {Boolean}
 */
rh.cm.mind.prototype._hasTerminalMindInput = function() {
	var currTerminalMind = this._wfCard.getTerminalMind();
	if(currTerminalMind && currTerminalMind.CODE_ID) {
		return true;
	}

	return false;
}; 

/**
 * 是否已经填写最终意见
 */
rh.cm.mind.prototype._hasWriteTerminalMind = function() {
	var _self = this;
	if(_self._terminalMind.mindId){
		return true;
	}
	
	return true;
};

/**
 * 判断有没有配置固定意见
 */
rh.cm.mind.prototype._hasRegularMindInput = function() {
	var regularMind = this._wfCard.getRegularMind();
	if(regularMind && regularMind.CODE_ID){
		return true;
	}
	
	return false;
};

/**
 * 是否已填写固定意见
 */
rh.cm.mind.prototype._hasWriteRegularMind = function() {
	var _self = this;
	if(_self.regularUsualId && _self.regularUsualId.val() != null 
		&& _self.regularUsualId.val().length > 0) {
		return true;
	}
	return false;
};

/******************************************************************************************************************************************/

/**
 * 保存所有意见，供送一个节点之前调用
 */
rh.cm.mind.prototype.saveMind = function() {
	var _self = this;
 	// 保存固定意见
 	var saveReg = _self.saveRegularMind();
 	// 保存最终意见
 	var saveTerm = _self.saveTerminalMind();
 	// 保存普通意见
 	var saveGen = _self.saveGeneralMind();
	
  	return saveReg || saveTerm || saveGen;
};

/**
 * 将意见定义bean、单据数据、流程数据合成创建数据Bean 
 */
rh.cm.mind.prototype._createMindBean = function(mindCodeBean){
	var _self = this;
	var data = {};
	data["MIND_CODE"] = mindCodeBean.CODE_ID;
	data["MIND_CODE_NAME"] = mindCodeBean.CODE_NAME;
    data["SERV_ID"] = _self._servId;
    data["DATA_ID"] = _self._dataId;
    data["WF_NI_ID"] = this._wfCard.getNodeInstBean().NI_ID;
    data["WF_NI_NAME"] = this._wfCard.getNodeInstBean().NODE_NAME;
    data["MIND_DIS_RULE"] = mindCodeBean.MIND_DIS_RULE; //显示规则
    data["MIND_TYPE"] = "1"; //1:文字意见;2:手写意见
	data["S_FLAG"] = 1; //启用标志

	if(mindCodeBean.EXT_JSON) {
		var confObj = JSON.parse(mindCodeBean.EXT_JSON);
		if(confObj.select && confObj.select.length > 0) {
			var item = _self._rhNext.getItem("rhMindSelect");
			if(item) {
				data["OPTION_VAL"] = item.getValue();
				data["OPTION_NAME"] = item.getName();
			}
		}
	}
	
	//如果存在委托关系则增加委托用户USER_CODE
	_self._wfCard.appendAgentUserFlagTo(data); 
	
	return data;
};

/**
 * 保存固定意见
 */
rh.cm.mind.prototype.saveRegularMind = function() {
	var _self = this;
	
	//获取固定意见定义bean
 	var currRegularMindDef = _self._wfCard.getRegularMind();
	
 	//弹出模式是保存在前端
	if (_self._isDialog) {
		if (currRegularMindDef["CODE_ID"]) {
			var locData = store.get(_self._wfCard.rhNext.storeKey);
			locData = locData || {};
			locData["MIND_DATA"] = locData["MIND_DATA"] || {};
			locData["MIND_DATA"]["REGULAR"] = _self._regularMind.assembleData(currRegularMindDef)
			store.set(_self._wfCard.rhNext.storeKey, locData);
			_self._data = _self._getLocalDatas();
		}
		return true;
	}
	
	//校验区
	if(!_self.regularMindValue || ! _self._regularMind.mindTextInput){
		//是否存在固定意见的输入框
		return false;
	}
	if (!_self.regularUsualId || !_self.regularUsualId.val() || _self.regularUsualId.val().length == 0) {
		//没有选择最终意见
		return false;
	}
	if (!_self._regularMind.isModify()) {
		//意见内容没有变化
		return false;
	}
  	
 	//装配保存数据
	var data = _self._regularMind.assembleData(currRegularMindDef);
	
	//开始送交服务器保存
	if (_self.regularMindValue.val() == 1) { // 批准需要提示一下
		if (_self._viewer.params && _self._viewer.params.isProxyUser) { // 代理身份的提示
//			if (confirm("您已经批准了该审签单，请确认您是否有权限批准？")) {
			if (confirm(Language.transStatic("rh_ui_mind_string12"))) {
				_self._saveMindToServer(data,_self._regularMind);
			}
		} else { // 正常身份
			_self._saveMindToServer(data,_self._regularMind);
		} 
	} else { // 非批准意见也不用提醒
		_self._saveMindToServer(data,_self._regularMind);
	} 
	
	//发送状态下刷新下一步中的组按钮
	if (_self._wfCard.isSendState) {
		_self._wfCard._reloadNextBtn();
	}
	
	return true;
};

/**
 * 保存最终意见
 */
rh.cm.mind.prototype.saveTerminalMind = function() {
	var _self = this;
	var wfCard = _self._wfCard;
	
	//获取最终意见定义bean
	var currTerminalMindDef = wfCard.getTerminalMind();
	
	//弹出模式是保存在前端
	if (_self._isDialog) {
		if (currTerminalMindDef["CODE_ID"]) {
			var locData = store.get(_self._wfCard.rhNext.storeKey);
			locData = locData || {};
			locData["MIND_DATA"] = locData["MIND_DATA"] || {};
			locData["MIND_DATA"]["TERMINAL"] = _self._terminalMind.assembleData(currTerminalMindDef)
			store.set(_self._wfCard.rhNext.storeKey, locData);
			_self._data = _self._getLocalDatas();
		}
		return true;
	}
	
	//校验区
	if(!wfCard.canWriteTerminalMind()){//不能填写最终意见，则返回
		return;
	}
	if(!_self._hasTerminalMindInput()){//最终意见输入框不存在则返回
		return false;
	}
	if (!_self._terminalMind.isModify()) {
		// 意见输入框的值等于原始值，表示已经更改过
		return false;
	}
	if (!_self._terminalMind.mindTextInput.val() || !_self._terminalMind.mindTextInput.val().length > 0) {
		return true; //跳过
	}
	
	//装配保存数据
	var data = _self._terminalMind.assembleData(currTerminalMindDef);
		
	//开始送交服务器保存
	if (_self._viewer.params && _self._viewer.params.isProxyUser) { // 代理身份的提示
		if (confirm(Language.transStatic("rh_ui_mind_string13"))) {
//		if (confirm("您已经填写了最终意见，请确认您是否有权限填写最终意见？")) {	
			_self._saveMindToServer(data,_self._terminalMind);
		}
	} else { // 正常身份的提示
		_self._saveMindToServer(data,_self._terminalMind);
	}
	
	return true;
};

/**
 * 保存普通意见
 */
rh.cm.mind.prototype.saveGeneralMind = function() {
	var _self = this;
	
	//获取普通意见定义bean
	var currGeneralMindDef = _self._wfCard.getMindCodeBean();
	
	//弹出模式是保存在前端
	if (_self._isDialog) {
		if (currGeneralMindDef["CODE_ID"]) {
			var locData = store.get(_self._wfCard.rhNext.storeKey);
			locData = locData || {};
			locData["MIND_DATA"] = locData["MIND_DATA"] || {};
			locData["MIND_DATA"]["GENERAL"] = _self._generalMind.assembleData(currGeneralMindDef)
			store.set(_self._wfCard.rhNext.storeKey, locData);
			_self._data = _self._getLocalDatas();
		}
		return true;
	}
	
	//校验区
	if (!currGeneralMindDef.CODE_ID){
		//没有意见输入框，则返回不保存
		return false;
	}
	if (!_self._generalMind.isModify()) {
		//意见内容没有变化，则返回不保存
		return false;	
	}
	
	//
	_self._generalMind.oldMindText = _self._generalMind.mindTextInput.val();
			
	//装配保存数据
	var data = _self._generalMind.assembleData(currGeneralMindDef);
	
	//开始送交服务器保存
	_self._saveMindToServer(data,_self._generalMind);
	
	return true;
};

/**
 * 单独保存意见到服务器
 * @param {} data
 */
rh.cm.mind.prototype._saveMindToServer = function(data,mindInputObj) {
	var _self = this;
	var resultData = FireFly.doAct("SY_COMM_MIND", "save", data);
	
	if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
  		mindInputObj.mindId = resultData.MIND_ID;
  		mindInputObj._fileIsModify = false;
  		mindInputObj.oldMindText = data.MIND_CONTENT;
  		// 保存完了删除新文件的标志
        if (mindInputObj.file) {
            mindInputObj.file.obj.find( ".newFile").remove();
        }
  	    _self.callback.call(_self._viewer, resultData); // 回调
//  		this._viewer.cardBarTip("意见已保存！" );//调用卡片句柄的提示信息方法
  		this._viewer.cardBarTip(Language.transStatic("rh_ui_mind_string14") );//调用卡片句柄的提示信息方法
	} else {
		this._viewer.cardBarTipError(Language.transStatic("rh_ui_mind_string15") + JsonToStr(resultData));
	}
};

/**
 * 保存意见，用于流程节点[同意并送交]按钮
 * @param {Object} data
 * @param {Object} mindInputObj
 */
rh.cm.mind.prototype.saveAgreeSendMind = function(mindText){
	var _self = this;
	var currMind = this._wfCard.getMindCodeBean();
	if (!currMind) {
		//不存在普通意见
		return false;
	}
	if (!currMind.CODE_ID){
		//没有意见输入框，则返回不保存
		return false;
	}
	var mindTextVal = _self._generalMind.oldMindText || "";
	if (mindTextVal == "") {
		if (!jQuery("#GENERAL_MIND", _self._pCon).is("textarea")) {
			_self._generalMind.setMindTextInput(jQuery("#GENERAL_MIND", _self._pCon));
		}
//		_self._generalMind.mindTextInput.val(mindText || "同意");
		_self._generalMind.mindTextInput.val(mindText || Language.transStatic("rh_ui_mind_string16"));
	} else {
		if (_self._generalMind.oldMindText == _self._generalMind.mindTextInput.val()) {
			return true;
		}
	}
	_self._generalMind.oldMindText = _self._generalMind.mindTextInput.val();
	var data = _self._createMindBean(currMind);
	_self._generalMind.fillData(data);
	
	//-----------重构保存-------------
	var resultData = FireFly.doAct("SY_COMM_MIND", "save", data);
	var mindInputObj = _self._generalMind;
	if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
  		mindInputObj.mindId = resultData.MIND_ID;
  		mindInputObj._fileIsModify = false;
  		mindInputObj.oldMindText = data.MIND_CONTENT;
  		// 保存完了删除新文件的标志
        if (mindInputObj.file) {
            mindInputObj.file.obj.find( ".newFile").remove();
        }
  	    _self.callback.call(_self._viewer, resultData); // 回调
//  		this._viewer.cardBarTip("意见已保存！" );//调用卡片句柄的提示信息方法
  		this._viewer.cardBarTip(Language.transStatic("rh_ui_mind_string14") );//调用卡片句柄的提示信息方法
	}
	return true;
};

/******************************************************************************************************************************************/

/**
 * 打开固定意见选择框
 * @param {} event
 */
rh.cm.mind.prototype._chooseMind = function(event) {
	var _self = this;
	var regularMind = _self._wfCard.getRegularMind();
	var inputName ="REGULAR_MIND";
	var configStr = "SY_COMM_MIND_REGULAR,{'TARGET':'MIND_ID~REGULAR_MIND','SOURCE':'MIND_ID~MIND_CONTENT~MIND_VALUE','PKHIDE':true,'EXTWHERE':' and REGULAR_TYPE=^" + regularMind.CODE_ID + "^', 'TYPE':'single','HIDE':'MIND_ID'}";
	var options = {
		"itemCode":inputName, 
		"config":configStr, 
		"rebackCodes":inputName, 
		"parHandler":_self._viewer, 
		"replaceCallBack":function(mind){
			//回调方法
			_self.regularUsualId.val(mind.MIND_ID);
			_self.regularMind.val(mind.MIND_CONTENT);
			_self.regularMindValue.val(mind.MIND_VALUE);
			
			var newRegularMindVal = mind.MIND_CONTENT + "。" +  _self._regularMind.mindTextInput.val();
			 _self._regularMind.mindTextInput.val(newRegularMindVal);
			
			// 保存固定意见
			_self.saveRegularMind();
			
			//选择固定意见之后，刷一下列表
			_self._regularMind.mindObj.refresh();
		}
	};
	var queryView;
	if (!_self.isMobile) {
		queryView = new rh.vi.rhSelectListView(options);
	} else {
		options["chooseId"] = "regularMind";
		queryView = new mb.ui.querychoose(options);
		queryView._bldWin(event);
	}
	queryView.show(event);
};

/**
 * 清除固定意见
 */
rh.cm.mind.prototype._cancelChoose = function(event) {
	var _self = this;
	var regularMind = _self.regularMind;
	
	_self.regularUsualId.val("");
	_self.regularMindValue.val("");
	regularMind.val("");
	
	// 保存固定意见
	if (_self._regularMind.mindId) {
		!_self._isDialog && FireFly.doAct("SY_COMM_MIND", "delete", {"_PK_":_self._regularMind.mindId});
		_self._regularMind.mindTextInput.val("");
	}
};

/**
 * 意见中重置页面高度，如果页面上 没有关联 意见的 卡片自定义页面，则重置
 */
rh.cm.mind.prototype._mindResetHeiWid = function() {
	var _self = this;
	
  	if (!_self._viewer.existSubServ("MINDLIST")) { //不存在意见标签页面
  		var bottomTabHei = 0;
  		if (_self._viewer.getBottomTabServ && _self._viewer.getBottomTabServ()) {
  			try {
  				bottomTabHei = _self._viewer.sonTab[_self._viewer.getBottomTabServ()]._height ; 
  				_self._viewer._resetHeiWid(bottomTabHei);
  			} catch(e) {}
  		}
  	} else { //重置标签页面的高度
  		Tab.setCardTabFrameHei();
  	}	
}

/**
 * 获取意见Label与内容宽度比例
 */
rh.cm.mind.prototype.getWidth = function(cols) {
	var width = this._viewer.form.getItemWidth(cols?cols:this._viewer.form.cols, this._viewer.form.cols);
	return {"itemWidth":width.ITEM_WIDTH, "leftWidth":width.LEFT_WIDTH, "rightWidth":width.RIGHT_WIDTH, "maxWidth":width.MAX_WIDTH};
};

/**
 * 销毁组件
 */
rh.cm.mind.prototype.destroy = function() {
	var _self = this;
	if (_self._terminalMind) {
		if (_self._terminalMind.file) {
			_self._terminalMind.file.destroy();
		}
		_self._terminalMind = null;
	}
	
	if (_self._generalMind) {
		if (_self._generalMind.file) {
			_self._generalMind.file.destroy();
		}
		_self._generalMind = null;
	}
	
	if (_self._regularMind) {
		if (_self._regularMind.file) {
			_self._regularMind.file.destroy();
		}
		_self._regularMind = null;
	}
	
	if (_self._mindWfCardView) {
		_self._mindWfCardView.destroy();
		_self._mindWfCardView = null;
	}
	
	if (_self._wfCard) {
		_self._wfCard = null;
	}
	
	if (_self._viewer) {
		_self._viewer.mind = null;
		_self._viewer = null;
	}
};

/**
 * 设置意见只读
 */
rh.cm.mind.prototype.setMindDisabed = function() {
	var _self = this;
	if (_self.terminalMindInput) { // 最终意见框
		_self.terminalMindInput.attr("readonly", true);
		_self.terminalMindInput.addClass("ui-text-disabled");
		if (_self.saveTerminalMindBtn) {// 最终意见保存按钮不可用
			_self.saveTerminalMindBtn.attr("disabled", true);
		}
	}
	if (_self.regularMind) { // 固定意见框
		_self.regularMind.attr("disabled", true);
		_self.chooseMind.attr("disabled", true);
		_self.cancelMind.attr("disabled", true);
		if (_self.saveRegularMindBtn) {
			_self.saveRegularMindBtn.attr("disabled", true);
		}
	}
	if (_self.mindText) { // 意见输入框
		_self.mindText.attr("readonly", true);
		_self.mindText.addClass("ui-textarea-disabled");
	}
	if (_self.saveMindBtn) { // 保存意见按钮
		_self.saveMindBtn.attr("disabled", true);
	}
	if (_self.mindTable) { // 意见列表
		_self.mindTable.find(".deleteMind").attr("disabled", true);
		_self.mindTable.find(".copyMind").attr("disabled", true);
	}
};

/**
 * 设置意见可写
 */
rh.cm.mind.prototype.setMindEnabled = function() {
	var _self = this;
	_self.mindText.attr("readonly", false);
	_self.mindText.removeClass("ui-textarea-disabled");
	_self.saveMindBtn.attr("disabled", false);
	_self.mindTable.find(".deleteMind").attr("disabled", false);
};

/**
 * 兼容移动端问题
 */
rh.cm.mind.prototype.__trickMobile = function() {
	var _self = this;
	//移动版解决页面渲染bug临时改法
	var _mindInputCon_ = jQuery(".mb-mind-textarea-container", _self._pCon);
	jQuery.each(_mindInputCon_, function(i,con){
		var _mindInputTA_ = jQuery(jQuery(con).children()[0]);
		_mindInputTA_.focus(function(){
			var topBar = jQuery(".mbTopBar");
			var btnBar = jQuery(".mbCard-btnBar");
			_focus_ = true;
			console && console.log("_focus_:" + _focus_);
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
				var st = jQuery("body").scrollTop();
				jQuery("body").scrollTop(st+1);
			}, 400);
		});
		_mindInputTA_.blur(function(){
			var topBar = jQuery(".mbTopBar");
			var btnBar = jQuery(".mbCard-btnBar");
			_focus_ = false;
			console && console.log("_focus_:" + _focus_);
			setTimeout(function(){
				if (!_focus_) {
					if (topBar.length == 1) {
						topBar.show();
					}
					if (btnBar.length == 1) {
						btnBar.show();
					}
					var st = jQuery("body").scrollTop();
					jQuery("body").scrollTop(st-1);
				}
			}, 200);
		});
	});
};

/**
 * 提取意见集合（配合对话框方式使用）
 */
rh.cm.mind.prototype.getValue = function(){
	var _self = this;
	var mindObj = {};
	var regularMindDef = _self._wfCard.getRegularMind();
	var terminalMindDef = _self._wfCard.getTerminalMind();
	var generalMindDef = _self._wfCard.getMindCodeBean();
	var regularMind = _self._regularMind;
	var terminalMind = _self._terminalMind;
	var generalMind = _self._generalMind;

	//提取固定意见
	if (regularMindDef["CODE_ID"]) {
		mindObj["REGULAR"] = regularMind.assembleData(regularMindDef);
	}
	
	//提取最终意见
	if (terminalMindDef["CODE_ID"]) {
		mindObj["TERMINAL"] = terminalMind.assembleData(terminalMindDef);
	}
	
	//提取普通意见
	if (generalMindDef["CODE_ID"]) {
		mindObj["GENERAL"] = generalMind.assembleData(generalMindDef);
	}
	
	return mindObj;
};

/**
 * Setter（配合对话框方式使用）
 */
//rh.cm.mind.prototype.setValue = function(mindObj){
//	//设置固定意见
//	//设置最终意见
//	//设置普通意见
//};

/**
 * 渲染后（供外部重载）
 */
rh.cm.mind.prototype.afterRender = function(){
	//重载
	var _self = this;
	//兼容移动端问题
	if (_self.isMobile) {
		_self.__trickMobile();
	}
};

/**
 * 意见校验（供外部重载）
 */
rh.cm.mind.prototype.mindCheck = function(mindData){
	var _self = this;
	var regularMindDef = _self._wfCard.getRegularMind();
	var terminalMindDef = _self._wfCard.getTerminalMind();
	var generalMindDef = _self._wfCard.getMindCodeBean();
	
	//校验固定意见
	if (regularMindDef && regularMindDef["CODE_ID"] && regularMindDef["MIND_MUST"] == "1") {
		if (!mindData || !mindData["REGULAR"] || (!mindData["REGULAR"]["MIND_CONTENT"] && !mindData["REGULAR"]["MIND_FILE"])) {
//			top.Tip.addTip("请填写" + regularMindDef["CODE_NAME"], "warn");
			top.Tip.addTip(Language.transStatic("rh_ui_mind_string5") + regularMindDef["CODE_NAME"], "warn");
			_self._regularMind.setBlankErr();
			return false;
		}
	}
	
	//校验最终意见
	if (terminalMindDef && terminalMindDef["CODE_ID"] && terminalMindDef["MIND_MUST"] == "1") {
		if (!mindData || !mindData["TERMINAL"] || (!mindData["TERMINAL"]["MIND_CONTENT"] && !mindData["TERMINAL"]["MIND_FILE"])) {
//			top.Tip.addTip("请填写" + terminalMindDef["CODE_NAME"], "warn");
			top.Tip.addTip(Language.transStatic("rh_ui_mind_string5") + terminalMindDef["CODE_NAME"], "warn");
			_self._terminalMind.setBlankErr();
			return false;
		}
	}
	
	//校验普通意见
	if (generalMindDef && generalMindDef["CODE_ID"] && generalMindDef["MIND_MUST"] == "1") {
		if (!mindData || !mindData["GENERAL"] || (!mindData["GENERAL"]["MIND_CONTENT"] && !mindData["GENERAL"]["MIND_FILE"])) {
//			top.Tip.addTip("请填写" + generalMindDef["CODE_NAME"], "warn");
			top.Tip.addTip(Language.transStatic("rh_ui_mind_string5") + generalMindDef["CODE_NAME"], "warn");
			_self._generalMind.setBlankErr();
			return false;
		}
	}
	
	return true;
};
