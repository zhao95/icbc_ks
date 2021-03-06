var _viewer = this;
var mdSId = "TS_PVLG_ROLE_MOD";
var roleId = _viewer.getItem("ROLE_ID").getValue();
var actCode = _viewer.opts.act;
var userCode = System.getVar("@USER_CODE@");
var userName = System.getVar("@USER_NAME@");

var curWidth = jQuery(window).width()-10;
if(curWidth <= "900") {
	$("#TS_PVLG_ROLE-mainTab").css({"overflow-y":"auto"});
	$(_viewer.formCon).css({"width":"880px"});
}

//$("#TS_PVLG_ROLE-mainTab").css({"overflow":"hidden"});
//$(_viewer.formCon).css({"width":"900px"});
//$("#TS_PVLG_ROLE-winDialog").css({"width":"900px","overflow-x":"hidden"});
//$("#TS_PVLG_ROLE-winDialog").parent().css({"overflow-x":"scroll","overflow-y":"hidden"});​​

if($("#TS_PVLG_ROLE-ROLE_DCODE__NAME").hasClass("disabled") == false) {
	
	$("#TS_PVLG_ROLE-ROLE_DCODE__NAME").unbind("click").bind("click", function(event) {

		var configStr = "TS_ORG_DEPT_ALL,{'TYPE':'single','sId':'TS_ORG_DEPT','pvlg':'CODE_PATH'}";

		var options = {
				"config" :configStr,
				"params" : {"USE_SERV_ID":"TS_ORG_DEPT"},
				"parHandler":_viewer,
				"formHandler":_viewer.form,
				"replaceCallBack":function(idArray,nameArray) {//回调，idArray为选中记录的相应字段的数组集合
					
					var codes = idArray;
					var names = nameArray;
					$("#TS_PVLG_ROLE-ROLE_DCODE__NAME").val(names);
					$("#TS_PVLG_ROLE-ROLE_DCODE").val(codes);
					$("#TS_PVLG_ROLE-ROLE_DNAME").val(names);
					console.log($("#TS_PVLG_ROLE-ROLE_DCODE").val());
					console.log($("#TS_PVLG_ROLE-ROLE_DNAME").val());
				}
		};
		
		var queryView = new rh.vi.rhDictTreeView(options);
		queryView.show(event,[],[0,495]);
	});
}

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

var roleRel = {};
var resultRel = FireFly.doAct("TS_PVLG_ROLE_REL", "finds", {"S_FLAG":1}, 2, false);

if(resultP._DATA_) {
	roleRel = resultRel._DATA_;
}

/**
 * 渲染角色所有模块 (主方法)
 */
render = function() {
	
	$(_viewer.formCon).find("#SEL_ALL").find(".icon-card-close").remove();
	
	$(_viewer.formCon).find("#SEL_ALL").css({"padding":"10px 0 10px 0"});
	
	var selall = $('<input type="checkbox" id="TS_PVLG_ROLE-CHECK_ALL_OPTS">').css({"vertical-align":"middle","margin-left":"5px"});
	
	$(_viewer.formCon).find("#SEL_ALL").find(".legend").append($("<span class='selall'>").append(selall));
	
	//全部选择功能
	selall.change(function() {
		
		var allobj = $("input[id='TS_PVLG_ROLE-CHECK_ALL_OPTS']");
		
		$(moduleArray).each(function (index, obj) {
			
			var obj = $("input[name='TS_PVLG_ROLE-"+obj.ITEM_CODE+"']:not(:disabled)");
			
			if(allobj.is(':checked')) {

				obj.attr("checked",true);
			} else {
				
				obj.attr("checked",false);
			}
		});
	});
	
	var optlab = $("div[id$='OPT_LAB']");
	
	$("div[id$='OPT_LAB']").css({"padding":"10px 0 10px 0"});
	
	optlab.each(function (idx, labObj) {
		
		var inputCfg = _viewer.getItemConfig(labObj.id);
	
		var formContent = $(_viewer.formCon).find("#"+labObj.id).find(".formContent");

		if(moduleArray) {
			
			$(moduleArray).each(function (index, obj) {
				
				if(inputCfg.indexOf(obj.ITEM_CODE)>=0) {
					
				  var row =  $("<div id='TS_PVLG_ROLE-"+obj.ITEM_CODE+"_div' class='inner' style='width:100%;max-width:1400px;'>");
				
				  //左侧模块区域
				  var name = $("<span class='name'>").text(obj.ITEM_NAME);
				
				  var start = $("<span class='star'>");
				
				  var leftContainer = $("<div class='container'>").append(name).append(start);
				
				  var leftDiv = $("<div id='TS_PVLG_ROLE-"+obj.ITEM_CODE+"_label' class='ui-label-default'>").append(leftContainer);
				
				  var left = $("<span class='left form__left30'>").append(leftDiv);
			
				  //右侧功能区域
				  var rightDiv = $('<div class="blank fl wp">').css({"float":"left","width":"85%","clear":"none","border-left":"none"});
				  
				  var rightDiv1 = $('<div class="blank fl wp">').css({"float":"left","width":"10%","clear":"none","background":"#ECF5FF"});
				  
				  var ckallSpan = $('<span id="TS_PVLG_ROLE-CHECK_ALL_SPAN_'+obj.ITEM_CODE+'" class="ui-checkbox-default">').appendTo(rightDiv1);
				  
				  var ckallObj = $('<input type="checkbox" id="TS_PVLG_ROLE-CHECK_ALL_'+obj.ITEM_CODE+'">').appendTo(ckallSpan);
				  
				  ckallSpan.append($("<label style='padding-left:1px'>全选</label>"));
				  
				  //全选功能
				  ckallObj.change(function() {
					  checkedAll(obj.ITEM_CODE);
				  }); 
				  
				  //显示功能选项(多选)
				  optRender(obj.ITEM_CODE, rightDiv);
				
				  var right = $("<span class='right form__right85'>").append(rightDiv1).append(rightDiv);
				
				  row.append(left).append(right);
				
				  $(formContent).append(row);
				}
			});
		}
	});
}

/**
 * 渲染模块下所有功能
 * code 模块编码
 * rightDiv 显示区域对象
 */
optRender = function(code,rightDiv) {

	var rightSpan = $('<span id="TS_PVLG_ROLE-'+code+'" class="ui-checkbox-default">');
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
			
			var comps = ",TS_ORG_DEPT_PVLG,TS_ORG_USER_ALL_PVLG,";
			
			if(comps.indexOf(","+code+",")>=0) { //人员选择，部门选择默认选中
				ckItem.attr("checked",true);
			}
			
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
			
			ckItem.change(function() {
				checkedRel(ckItem,code,item.ITEM_CODE);
			}); 
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
	
//	var param = {};
//	param.ROLE_ID = roleId;
//	//功能编码
//	param.MD_CODE = "TS_ORG_DEPT_PVLG";
//	//功能名称
//	param.MD_NAME = "机构选择";
//	//多选value
//	param.MD_VAL = "show";
//	
//	paramArray.push(param);
//	
//	//功能编码
//	param.MD_CODE = "TS_ORG_USER_ALL_PVLG";
//	//功能名称
//	param.MD_NAME = "人员选择";
//	//多选value
//	param.MD_VAL = "show";
//	
//	paramArray.push(param);
	
	var batchData = {};
//	console.log("save paramArray",paramArray);
	batchData["BATCHDATAS"] = paramArray;
	//批量保存
	var rtn = FireFly.batchSave(mdSId,batchData,null,2,false);
	
//	var rtnMsg = rtn._MSG_;
	
//	showTip(rtnMsg);
	
	var changeData = this.getChangeData();
	 
	 if (jQuery.isEmptyObject(changeData)) {
		 
		 _viewer.cancelSave(true);
		 
		 _viewer.cardClearTipLoad();
		 
//		 _viewer.cardBarTip("保存成功！");
		 
		 _viewer.cardListBarTip("保存成功！");
	 }
	 
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

checkedRel = function(obj,mod,opt) {
	
	if($(obj).is(':checked')) {
		
		try{
			
			$(roleRel).each(function (relIdx, relItem) {
				var roleMod = relItem.ROLE_MOD;
				var roleOpt = ","+relItem.ROLE_MOD_OPT+",";
				
				if(roleMod == mod && roleOpt.indexOf(","+opt+",")>=0) {
					
					var relMod = relItem.REL_MOD;
					var relOpt = ","+relItem.REL_MOD_OPT+",";
					
					var obj = $("input[name='TS_PVLG_ROLE-"+relMod+"']:not(:disabled)");
					
					$(obj).each(function () {
						
						var val = $(this).attr('value');
						
						if(relOpt.indexOf(","+val+",")>=0) {
							
							$(this).attr("checked",true);
						}
					});
				}
			});
		} catch(e){
			console.log(e);
		}
		
	} else {
		
		try{
			$(roleRel).each(function (relIdx, relItem) {
				var roleMod = relItem.ROLE_MOD;
				var roleOpt = ","+relItem.ROLE_MOD_OPT+",";
				
				if(roleMod == mod && roleOpt.indexOf(","+opt+",")>=0) {
					
					var relMod = relItem.REL_MOD;
					var relOpt = ","+relItem.REL_MOD_OPT+",";
					
					var obj = $("input[name='TS_PVLG_ROLE-"+relMod+"']:not(:disabled)");
					
					$(obj).each(function () {
						
						var val = $(this).attr('value');
						
						if(relOpt.indexOf(","+val+",")>=0) {
							
							$(this).removeAttr("checked");
						}
					});
				}
			});
		} catch(e){
			console.log(e);
		}
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

//var checked = "";
//$("input[type='checkbox'][name='ROLE_PID']").change(function(){
//	alert(0);
//});