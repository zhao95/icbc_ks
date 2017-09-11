/** 服务卡片使用的js方法定义：开始fromTable */
var _viewer = this;
//针对时间的校验
_viewer.getItem("XM_START").obj.unbind("click").bind("click", function() {
	    WdatePicker({
	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-XM_END')}"
	    });
	});
_viewer.getItem("XM_END").obj.unbind("click").bind("click", function() {

	    WdatePicker({
	        minDate : "#F{$dp.$D('" + _viewer.servId + "-XM_START')}"
	    });
	});
//下一步按钮
//1把数据保存到数据库
_viewer.getBtn("nextbtn").unbind("click").bind("click", function(event) {
	_viewer.doActReload('saveAndToSZ');//这里不用传参数，这个方法默认是获取所有值
	var XM_ID = _viewer.getItem("XM_ID").getValue();//执行完保存后，自动把ID回填了
	var  XM_TYPE=_viewer.getItem("XM_TYPE").getValue();//得到类型值
	//从项目管理到项目设置传参
	var ext =  " and XM_ID = '" + XM_ID + "'";
	window.location.href ="stdListView.jsp?frameId=TS_XMGLSZ-tabFrame&sId=TS_XMGL_SZ&paramsFlag=false&title=项目管理设置&XM_ID="+XM_ID
				+"&extWhere="+ext;
});	
	
//保存后的操作	

_viewer.afterSave = function(resultdata){
		var XM_ID=resultdata.XM_ID;
		var XM_GJ=resultdata.XM_GJ;
		var  param={"XM_ID":XM_ID,"XM_GJ":XM_GJ}
		//_viewer.doActReload('saveAfterToSZ','param');//这里不用传参数，这个方法默认是获取所有值
	    FireFly.doAct(_viewer.servId, "afterSaveToSz", param);
	    _viewer.refresh();
	}

//查看按钮打开的卡片呈现只读样式

//var saveBtn=_viewer.getBtn("save");
//var nextBtn=_viewer.getBtn("nextbtn");
//	//var qq=$(".item ui-corner-5").readCard();
//		  saveBtn.hide();
//		  nextBtn.hide();
//	_viewer.readCard();
//	
	if(_viewer.opts.readOnly == "true"){
		_viewer.readCard();
	}

	if(_viewer.getItem("SERV_ID").getValue() == ""){
		_viewer.getItem("SERV_ID").setValue(_viewer.servId);
	}

	//打开自服务列表
	if(typeof(_viewer.opts.paramData) !="undefined"){ 
		var sid = _viewer.opts.paramData.showTab;
		if(sid != ""){
			var topObj = jQuery("li.rhCard-tabs-topLi[sid='" + sid + "']",_viewer.tabs);
			topObj.find("a").click();
		}
	}

	// 状态 0:新增未保存1:无效(待审核) 2:无效(审核未通过) 3:无效(审核中) 4:无效(扣分超过上限)5:有效
	// 11,12,13,14:逻辑删除 
	var jd = _viewer.getItem("XM_JD").getValue();
	//var saveBtn = _viewer.getBtn("save");
	//var applyBtn = _viewer.getBtn("apply");
	var saveBtn=_viewer.getBtn("save");
	var nextBtn=_viewer.getBtn("nextbtn");
	// 创建数据机构
	//var s_odept = _viewer.getItem("S_ODEPT").getValue();
	//var odept = System.getVar("@ODEPT_CODE@");

	// 只有创建机构能够修改数据和提交审批
	if (!"".equals(jd) ) {
		
		cardReadOnly();
	}

	function cardReadOnly() {
		saveBtn.hide();
		nextBtn.hide();
		_viewer.form.disabledAll();
	}
