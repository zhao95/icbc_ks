var _viewer = this;
var mdSId = "TS_PVLG_ROLE_MOD";
var roleId = _viewer.getItem("ROLE_ID").getValue();
var actCode = _viewer.opts.act;
var userCode = System.getVar("@USER_CODE@");
var userName = System.getVar("@USER_NAME@");

//角色目录CTLG_PCODE赋值
//if(_viewer.getItem("CTLG_PCODE").getValue() == "") {
//	if(_viewer.getParHandler()) {
//		_viewer.getItem("CTLG_PCODE").setValue(_viewer.getParHandler().getParams().CTLG_PCODE_H);
//	} else {
//		_viewer.getItem("CTLG_PCODE").setValue(_viewer.getParams().CTLG_PCODE_H);
//	}
//}


var ctlgPcode = _viewer.getItem("CTLG_PCODE").getValue();
//console.log("ctlgPcode",ctlgPcode);

var roleType = _viewer.getItem("ROLE_TYPE").getValue();
if(roleType == 1) {
//	console.log("ROLE_TYPE","本机构");
} else if(roleType == 2) {
//	console.log("ROLE_TYPE","自定义机构");
}

//权限模块字典
var mdDict = FireFly.getDict("TS_PVLG_MOD");
var moduleArray = mdDict[0].CHILD;

//当前角色已赋权限 (所有功能)
var roleChecked = {};
var result = FireFly.doAct(mdSId, "finds", {"ROLE_ID":roleId}, 2, false);
if(result._DATA_) {
	$(result._DATA_).each(function (index, item) {
		roleChecked[item.MD_CODE] = item;
	});
}
//console.log("roleChecked",roleChecked);
//创建者的权限 (所有功能)
var roleCheckedP = {};
var resultP = FireFly.doAct(mdSId, "getPvlgByUser", {"USER_CODE":userCode}, 2, false);
if(resultP._DATA_) {
	roleCheckedP = resultP._DATA_;
}
//console.log("roleCheckedP",roleCheckedP);

/**
 * 渲染角色所有模块 (主方法)
 */
render = function() {
	var formContent = $(_viewer.formCon).find("#MD_OPTS").find(".formContent");

	if(moduleArray) {
		
		$(moduleArray).each(function (index, obj) {
		   var row =  $("<div id='TS_PVLG_ROLE-"+obj.ITEM_CODE+"_div' class='inner' style='width:100%;max-width:1400px;'>");
		
		  //左侧模块区域
		  var name = $("<span class='name'>").text(obj.ITEM_NAME);
		
		  var start = $("<span class='star'>");
		
		  var leftContainer = $("<div class='container'>").append(name).append(start);
		
		  var leftDiv = $("<div id='TS_PVLG_ROLE-"+obj.ITEM_CODE+"_label' class='ui-label-default'>").append(leftContainer);
		
		  var left = $("<span class='left form__left30'>").append(leftDiv);
		
		  //右侧功能区域
		  var rightDiv = $('<div class="blank fl wp">').css({"float":"left","width":"92%","clear":"none","border-left":"none"});
		  
		  var rightDiv1 = $('<div class="blank fl wp">').css({"float":"left","width":"6%","clear":"none","background":"#ECF5FF"});
		  
		  var ckallSpan = $('<span id="TS_PVLG_ROLE-CHECK_ALL_SPAN_'+obj.ITEM_CODE+'" class="ui-checkbox-default">').appendTo(rightDiv1);
		  
		  var ckallObj = $('<input type="checkbox" id="TS_PVLG_ROLE-CHECK_ALL_'+obj.ITEM_CODE+'">').appendTo(ckallSpan);
		  
		  ckallSpan.append($("<label>全选</label>"));
		  
		  //全选功能
		  ckallObj.change(function() {
			  checkedAll(obj.ITEM_CODE);
		  }); 
		  
		  //显示功能选项(多选)
		  optRender(obj.ITEM_CODE, rightDiv);
		
		  var right = $("<span class='right form__right70'>").append(rightDiv1).append(rightDiv);
		
		  row.append(left).append(right);
		
		  $(formContent).append(row);
		});
	}
}

/**
 * 渲染模块下所有功能
 * code 模块编码
 * rightDiv 显示区域对象
 */
optRender = function(code,rightDiv) {

	var rightSpan = $('<span id="TS_PVLG_ROLE-'+obj.ITEM_CODE+'" class="ui-checkbox-default">');
	//功能权限主键
	var mdId = $('<input type="hidden">').attr("name","TS_PVLG_ROLE-"+code+"_MID").appendTo(rightSpan);
	
	var optDict = FireFly.getDict(code);
	//所有功能权限
	var option = optDict[0].CHILD;

	if(option) {
		
		//选中的功能权限
		var curChecked = roleChecked[code];
		//权限数据主键，用作权限修改
		if(typeof(curChecked) != "undefined") {
			mdId.val(curChecked.MD_ID)
		} else {
			curChecked = {};
		}
		
		//父级选中的功能权限
		var parChecked = roleCheckedP[code];
		
		if(actCode == "cardAdd") {
			curChecked = roleCheckedP[code];
		}
		
		//选中的功能权限 val数组
		var curVal = {};
		if (curChecked && typeof(curChecked.MD_VAL) != "undefined") {
			
			var curArray = curChecked.MD_VAL.split(",");
			$(curArray).each(function(i,val) {
				curVal[val] = null;
			});
		}
//		console.log("curVal",curVal);
		
		//选中的功能权限 val数组
		var parVal = {};
		if (typeof(parChecked) != "undefined") {
			var parArray = parChecked.split(",");
			$(parArray).each(function(i,val){
				parVal[val] = null;
			});
		}
//		console.log("parVal",parVal);
		
		
		
		//遍历字典中所有功能
		$(option).each(function (index, item) {
			
			var ckItem = $('<input type="checkbox">').attr("name","TS_PVLG_ROLE-"+code).val(item.ITEM_CODE);
			//功能权限名称 checkbox名称
			var lab = $("<label>").text(item.ITEM_NAME);

			rightSpan.append(ckItem).append(lab);
			
			if(typeof(curVal[item.ITEM_CODE]) != "undefined") {
				ckItem.attr("checked",true);
			}
			
			if(typeof(parVal[item.ITEM_CODE]) == "undefined" && userName != '系统管理员') {
				ckItem.attr("disabled","disabled");
				lab.css("color","#dddddd");
			}
			
		});
	}
	
	rightDiv.append(rightSpan);
};

/**
 * 保存角色之前 先保存角色的权限
 */
_viewer.beforeSave = function() {
	
	var paramArray = [];
	 
	//遍历所有模块
	$(moduleArray).each(function (index, module) {
		
		var param = {};

		var vals = "";

		//所有功能权限的checkbox对象
		var obj = $("input[name='TS_PVLG_ROLE-"+module.ITEM_CODE+"']:checkbox");

		obj.each(function() {

			if ($(this).is(':checked')) {
				if(vals==""){
					vals = $(this).val();
				} else {
					vals += ","+$(this).val();
				}
			}
		});
		
		var midCode = "TS_PVLG_ROLE-"+module.ITEM_CODE+"_MID";
		
		var mId = $("input[name='"+midCode+"'").val();
		
//		console.log("save "+midCode,mId)
		
		//主键
		param.MD_ID = mId;
		param._PK_ = mId;
		//角色id
		param.ROLE_ID = roleId;
		//功能编码
		param.MD_CODE = module.ITEM_CODE;
		//功能名称
		param.MD_NAME = module.ITEM_NAME;
		//多选value
		param.MD_VAL = vals;
		
		paramArray.push(param);
		
	});
	
	var batchData = {};
//	console.log("save paramArray",paramArray);
	batchData["BATCHDATAS"] = paramArray;
	//批量保存
	var rtn = FireFly.batchSave(mdSId,batchData,null,2,false);
	
//	var rtnMsg = rtn._MSG_;
	
//	showTip(rtnMsg);
	
};

//保存后刷新tree和列表
_viewer.afterSave = function() {
	_viewer.getParHandler().refreshTreeAndGrid();
};

/**
 * 全选/取消全选
 */
checkedAll = function(code) {
	
	var obj = $("input[name='TS_PVLG_ROLE-"+code+"']:not(:disabled)");
	
	if($("#TS_PVLG_ROLE-CHECK_ALL_"+code).is(':checked')) {
		
		obj.attr("checked",true);
	} else {
		
		obj.attr("checked",false);
	}	
};

/**
 * 提示信息
 */
showTip = function(rtnMsg) {
if (StringUtils.startWith(rtnMsg, "OK,") || StringUtils.startWith(rtnMsg, "ERROR,批量保存失败：没有数据")) {
		
		if(_viewer.getParHandler()){
			_viewer.getParHandler().listBarTip("保存成功");
		} else {
			_viewer.cardBarTip("保存成功");
		}
		
	} else {
		if(_viewer.getParHandler()){
			_viewer.getParHandler().listBarTipError(rtnMsg);
		} else {
			_viewer.cardBarTipError(rtnMsg);
		}
	}
}
//调用主方法
render();