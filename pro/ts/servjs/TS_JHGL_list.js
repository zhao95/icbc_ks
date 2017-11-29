var _viewer = this;
var module = 'PLAN';
var height = jQuery(window).height()-50;
var width = jQuery(window).width()-100;
$(".rhGrid").find("tr").unbind("dblclick");
// 每一行添加编辑和删除
_viewer.grid._table.find("tr").each(function(index, item) {
	if (index != 0) {
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").append(
			'<a class="rh-icon rhGrid-btnBar-a" id="TS_JHGL_optViewBtn" operCode="optViewBtn"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-view"></span></a>'
		 +  '<a class="rh-icon rhGrid-btnBar-a" id="TS_JHGL_optEditBtn" operCode="optEditBtn"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
//			+ '<a class="rh-icon rhGrid-btnBar-a" id="TS_JHGL_delete" operCode="delete"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
			);
			// 为每个按钮绑定卡片
			bindCard();
	}
});

/*
 * 删除前方法执行
 */
_viewer.beforeDelete = function(pkArray) {
	var flag=true;
    for(var k =0;k<pkArray.length;k++){
        FireFly.doAct("TS_JHGL","finds",{"_PK_":pkArray[k]},true,false,function(data){
            if(data._DATA_[0].JH_STATUS =="2"){
               flag=false;
            }
        });
	}
	if(flag){
            showVerify(pkArray,_viewer);
	}else{
        Tip.show("请取消发布后再进行删除！");
    }
};

// 绑定的事件
function bindCard() {
	// 当行删除事件
	jQuery("td [operCode='delete']").unbind("click").bind("click", function() {
		var pkCode = $(this).parent().parent().attr("id");
		//删除前判断是否已发布
		var paramfb = {};
		paramfb["_extWhere"] = "and JH_ID ='"+pkCode+"'";
		var beanFb = FireFly.doAct(_viewer.servId, "query", paramfb);
		//判断是否已发布，否则提示已经发布 
		if(beanFb._DATA_ != 0){
			if(beanFb._DATA_[0].JH_STATUS=="2"){
				_viewer.listBarTipError("请取消发布后再删除！");
			}else if(beanFb._DATA_[0].JH_STATUS=="1"){
				var paramDele = {};
				paramDele['_extWhere'] = "and JH_ID ='"+pkCode+"'";
				var result1 = FireFly.doAct("TS_JHGL","query",paramDele);
				rowDelete(pkCode, _viewer);
				Tip.show("删除计划成功！");
			}
		}else if(beanFb._DATA_ == 0){
			Tip.show("当前用户无权限发布！");
		}
		
	});
	
/*	jQuery("td [id='"+servId+"_group']").unbind("click").bind("click", function(){
		var pk = jQuery(this).attr("rowpk");
		var extWhere = "and USER_CODE = '" + pk + "'";
		var params = {"_extWhere" : extWhere};
		var url = "TS_PVLG_GROUP_USER_V_SY_ORG_USER.list.do?&_extWhere=" + extWhere;
		var options = {"url" : url,"params" : params,"menuFlag" : 3,"top" : true};
		Tab.open(options);
	 });*/
	

	// 当行编辑事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click",function() {
		var pkCode = $(this).parent().parent().attr("id");
		//编辑修改前判断是否已发布
		var paramModify = {};
		paramModify["_extWhere"] = "and JH_ID ='"+pkCode+"'";
		var beanFb = FireFly.doAct(_viewer.servId, "query", paramModify);
		//判断是否已发布，否则提示已经发布，不能修改 
		if(beanFb._DATA_ != 0){
			if(beanFb._DATA_[0].JH_STATUS=="2"){
				Tip.show("请取消发布后再编辑！");
			}else if(beanFb._DATA_[0].JH_STATUS=="1"){
				openMyCard(pkCode);
			}
		}else if(beanFb._DATA_ == 0){
			Tip.show("当前用户无权限发布！");
		}

	});

	// 当行详细计划事件
	jQuery("td [operCode='optViewBtn']").unbind("click").bind("click",function() {
		var pkCode = $(this).parent().parent().attr("id");
		openMyCard(pkCode,true);
		/*var pkCode = $(this).parent().parent().attr("id");
		var jhTitle = _viewer.grid.getRowItemValue(pkCode, "JH_TITLE");
		var jhYear=_viewer.grid.getRowItemValue(pkCode, "JH_YEAR");
		// 定义一个对象
		var strwhere = " and JH_PTITLE ='" + pkCode + "' ";
		var params = {"JH_ID" : pkCode,"JH_TITLE" : jhTitle,"JH_YEAR" : jhYear,"_extWhere" : strwhere};
		var url = "TS_JHGL_XX.list.do?&_extWhere=" + strwhere;
		var options = {"url" : url,"params" : params,"menuFlag" : 3,"top" : true};
		Tab.open(options);*/
	});
}
// 点击时进行发布
_viewer.getBtn("fabu").unbind("click").bind("click", function() {
	var pkAarry = _viewer.grid.getSelectPKCodes();
	showRelease(pkAarry,_viewer);
//	if (pkAarry.length == 0) {
//		_viewer.listBarTipError("请选择相应记录！");
//	} else {
//		//遍历所选的所有大计划，依次进行判断执行。
//		for (var i = 0; i < pkAarry.length; i++) {;
//			var paramfb = {};
//			paramfb["_extWhere"] = "and JH_ID ='"+pkAarry[i]+"'";
//			var beanFb = FireFly.doAct(_viewer.servId, "query", paramfb);
//			//判断是否已发布，否则提示已经发布 
//			if(beanFb._DATA_ != 0){
//				if(beanFb._DATA_[0].JH_STATUS=="2"){
//					_viewer.listBarTipError("所选计划已发布！");
//				}else if(beanFb._DATA_[0].JH_STATUS=="1"){
//					var paramXm = {};
//					paramXm["pkCodes"] = pkAarry[i];
//					//showRelease(pkAarry,_viewer,paramXm);
//					//FireFly.doAct(_viewer.servId, "UpdateStatusStart", param,false,false,function(){
//						//Tip.show("计划发布成功！");
//					//});
//					//_viewer.refresh();
//				}
//			}else if(beanFb._DATA_  == 0){
//				Tip.show("当前用户无权限发布！");
//			}
//				
//		}
//	}
})

//初次发布
function  firRelea(pkAarry,_viewer){
if (pkAarry.length == 0) {
	_viewer.listBarTipError("请选择相应记录！");
} else {
	//遍历所选的所有大计划，依次进行判断执行。
	for (var i = 0; i < pkAarry.length; i++) {
		var paramfb = {};
		paramfb["_extWhere"] = "and JH_ID ='"+pkAarry[i]+"'";
		var beanFb = FireFly.doAct(_viewer.servId, "query", paramfb);
		//判断是否已发布，否则提示已经发布 
		if(beanFb._DATA_ != 0){
			if(beanFb._DATA_[0].JH_STATUS=="2"){
				_viewer.listBarTipError("所选计划已发布！");
			}else if(beanFb._DATA_[0].JH_STATUS=="1"){
				var paramXm = {};
				paramXm["pkCodes"] = pkAarry[i];
				//showRelease(pkAarry,_viewer,paramXm);
				FireFly.doAct(_viewer.servId, "UpdateStatusStart", paramXm,false,false,function(){
					Tip.show("计划发布成功！");
				});
				_viewer.refresh();
			}
		}else if(beanFb._DATA_  == 0){
			Tip.show("当前用户无权限发布！");
		}
			
	}
}	 


//		FireFly.doAct("TS_JHGL", "UpdateStatusStart", paramXm,false,false,function(){
//			Tip.show("计划发布成功！");
//		});
//		_viewer.refresh();
}


// 点击时取消发布
_viewer.getBtn("qxfb").unbind("click").bind("click", function() { 
	var pkAarry = _viewer.grid.getSelectPKCodes();
	if (pkAarry.length == 0) {
		_viewer.listBarTipError("请选择相应记录！");
	} else {
		//遍历所选的所有大计划，依次进行判断执行。
		for (var i = 0; i < pkAarry.length; i++) {
			var paramfb = {};
			paramfb["_extWhere"] = "and JH_ID ='"+pkAarry[i]+"'";
			var beanFb = FireFly.doAct(_viewer.servId, "query", paramfb);
			//判断是否已发布，否则提示已经发布 
			if(beanFb._DATA_ != 0){
				if(beanFb._DATA_[0].JH_STATUS=="1"){
					_viewer.listBarTipError("所选计划已取消发布！");
				}else if(beanFb._DATA_[0].JH_STATUS=="2"){
					var param = {};
					param["pkCodes"] = pkAarry[i];
					FireFly.doAct(_viewer.servId, "UpdateStatusStop", param);
					Tip.show("计划已取消发布！");
					_viewer.refresh();
				}
			}else if(beanFb._DATA_  == 0){
				Tip.show("当前用户无权限取消发布！");
			}
		}
	}
//		var param = {};
//		param["pkCodes"] = pkAarry.join(",");
//		FireFly.doAct(_viewer.servId, "UpdateStatusStop", param);
//		Tip.show("计划已取消发布！");
//		_viewer.refresh();
})

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click", function(event) {
	module = 'PLAN';
	var params = {"isHide" : "true","CTLG_MODULE" : module};
	var options = {
		"url" : "TS_COMM_CATALOG_PLAN.list.do?isHide=true&CTLG_MODULE=" + module,
		"params" : params,
		"menuFlag" : 3,
		"top" : true
	};
	Tab.open(options);
});


//_viewer.getBtn("add").unbind("click").bind("click", function() {
//	var temp = {
//		"act" : UIConst.ACT_CARD_ADD,
//		"sId" : "TS_JHGL",
//		"parHandler" : _viewer,
//		"widHeiArray" : [ width,height ],
//		"xyArray" : [ 100, 100 ]
//	};
//	var cardView = new rh.vi.cardView(temp);
//	cardView.show();
//});

//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
    temp[UIConst.PK_KEY] = dataId;
    if(readOnly != ""){
    	temp["readOnly"] = readOnly;
    }
    if(showTab != ""){
    	temp["showTab"] = showTab;
    }
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
}


//传给后台的数据
/*
* 业务可覆盖此方法，在导航树的点击事件加载前
*/
rh.vi.listView.prototype.beforeTreeNodeClickLoad = function(item,id,dictId) {
	var params = {};
	var user_pvlg=_viewer._userPvlg[_viewer.servId+"_PVLG"];
	params["USER_PVLG"] = user_pvlg;
	_viewer.whereData["extParams"] = params;
	var flag = getListPvlg(item,user_pvlg);
	_viewer.listClearTipLoad();
	return flag;
};
//重写add方法
_viewer.getBtn("add").unbind("click").bind("click",function() {
	var pcodeh = _viewer._transferData["CTLG_PCODE"];
	if(pcodeh == "" || typeof(pcodeh) == "undefined") {
		alert("请选择添加目录的层级 !");
		return false;
	}
	
	//var width = jQuery(window).width()-200;
	//var height = jQuery(window).height()-200;
	
	var temp = {"act":UIConst.ACT_CARD_ADD,
			"sId":_viewer.servId,
			"params":  {
				"CTLG_MODULE" : module,
			},
			"transferData": _viewer._transferData,
			"links":_viewer.links,
			"parHandler":_viewer,
			"widHeiArray":[width,height],
			"xyArray":[50,50]
	};
	console.log(temp);
	var cardView = new rh.vi.cardView(temp);
	cardView.show();
});


/**
 * 列表发布 验证码
 * @parm pkArray 主键
 * @parm viewer 页面_viewer
 */
function showRelease(pkAarry,_viewer){
	var imgDate = new Date();
	var content = '<div><table>'
			+ '<tr id="errMsg" style="visibility: hidden;"><td><font color="red" size="5">验证码错误！</font></td></tr>'
			+ '<tr><td>请输入验证码:<input name="vcode" style="height: 30px; width: 130px; font-size: 22px;" type="text" id="vcode"></td></tr>'
			+ '<tr style="height:20px"><td></td></tr>'
			+ '<tr><td>验证码：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img id="codevalidate" src="/VerifyCodeServlet/'+imgDate.getMilliseconds()+'" style="height: 25px;" onclick="changeImg()"> '
			+ '<a href="javascript:;" onclick="changeImg()"><font size="2">看不清，换一张</font></a></td></tr>'
			+ '</table></div>'
			+ '<script>function changeImg() {var myDate = new Date();var url = $("#codevalidate").prop("src");url = url + "/" + myDate.getMilliseconds();$("#codevalidate").prop("src", url);}</script>';

	var dialog = jQuery("<div></div>").addClass("dictDialog").attr("title",
			"验证码");
	var container = jQuery(content).appendTo(dialog);
	dialog.appendTo(jQuery("body"));
	var hei = 230;
	var wid = 300;
    
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
				var vcode = $("#vcode").val();
				if (vcode.length != 4) {
					$("#errMsg").css("visibility", "visible");
				} else {
					 FireFly.doAct("TS_UTIL", "checkVerify", {
						"vcode" : vcode
					//}, true, false, function(data) {;
					}, true, false, function(data) {
						if (data.res == "true") {
							dialog.remove();
							firRelea(pkAarry,_viewer);
							//FireFly.listDelete(viewer.servId,{"_PK_":pkArray.toString()},true);
							_viewer.refresh();
							//viewer.afterDelete();
							
						} else {
							$("#errMsg").css("visibility", "visible");
							
						}
					});
				
				}
				
			},
			"关闭" : function() {
				_viewer.refresh();
				dialog.remove();
			}
		}
	});
	dialog.parent().find(".ui-dialog-titlebar-close").hide();
	var btns = jQuery(".ui-dialog-buttonpane button", dialog.parent()).attr(
			"onfocus", "this.blur()");
	btns.first().addClass("rh-small-dialog-ok");
	btns.last().addClass("rh-small-dialog-close");
	dialog.parent().addClass("rh-small-dialog").addClass(
			"rh-bottom-right-radius");
	jQuery(".ui-dialog-titlebar").last().css("display", "block");
	
}


