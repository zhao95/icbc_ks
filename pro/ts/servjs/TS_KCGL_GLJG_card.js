var _viewer = this;
if(_viewer.opts.act == "cardAdd"){
	var handler = _viewer.getParHandler();
	var extWhere = handler._extWhere;
	if(typeof(extWhere.split("'")[1])!="undefined"){ 
		_viewer.getItem("KC_ID").setValue(extWhere.split("'")[1]);
	}
}

if (_viewer.opts.readOnly) {
	_viewer.readCard();
	}


$("#TS_KCGL_GLJG-JG_CODE__NAME").unbind("click").bind("click", function(event) {
	sel(event);
});

$("#TS_KCGL_GLJG-JG_CODE__NAME").next().unbind("click").bind("click", function(event) {
	sel(event);
});

function sel(event){
	
	var seltype = "single";
	
	if(_viewer._actVar == UIConst.ACT_CARD_ADD) {
		seltype = "multi" ;
	}
	
	var configStr = "TS_KCGL_GLJG_ODEPT,{'TYPE':'"+seltype+"'}";//single

	var options = {
			"config" :configStr,
			"parHandler":_viewer,
			"formHandler":_viewer.form,
			"replaceCallBack":function(idArray,nameArray) {//回调，idArray为选中记录的相应字段的数组集合
				var codes = idArray;
				var names = nameArray;
				
				$("#TS_KCGL_GLJG-JG_CODE__NAME").val(names);
				$("#TS_KCGL_GLJG-JG_CODE").val(codes);
				$("#TS_KCGL_GLJG-JG_NAME").val(names);
				
				if(_viewer.getItem("JG_NAME").getValue().length>0){
					$("#TS_KCGL_GLJG-JG_CODE_label").find(".name").removeClass("error");
					$("#TS_KCGL_GLJG-JG_CODE__NAME").parent().removeAttr("validate_msg").removeClass("blankError").removeClass("errorbox");
				}
				
//				var code = idArray[0];
//				FireFly.doAct("SY_ORG_DEPT_ALL","byid",{"_PK_":code},true,false,function(data){
//					_viewer.getItem("JG_TYPE").setValue(data.DEPT_TYPE);
//				});
			}
	};
	
	var queryView = new rh.vi.rhDictTreeView(options);
	queryView.show(event,[],[0,495]);
}

if(_viewer._actVar == UIConst.ACT_CARD_ADD) {
	
_viewer.getBtn("save").unbind("click").bind("click", function(event) {
	
	var jgCode = _viewer.getItem("JG_CODE").getValue();
	var jgName = _viewer.getItem("JG_NAME").getValue();
	var kcId = _viewer.getItem("KC_ID").getValue();
	var jgFar =  _viewer.getItem("JG_FAR").getValue();
	
	var codes = jgCode.split(",");
	var names = jgName.split(",");
	
	var paramArray = [];
	
	for(var i=0; i<codes.length; i++) {
		
		FireFly.doAct("SY_ORG_DEPT_ALL","byid",{"_PK_":codes[i]},true,false,function(data) {
			
			var param = {};
			//用户编码
			param.JG_CODE = codes[i];
			//用户名称
			param.JG_NAME = names[i];
			
			param.KC_ID = kcId;
			
			param.JG_FAR = jgFar;
			
			//机构类型 1部门 2机构
//			param.JG_TYPE = data.DEPT_TYPE;
			
			paramArray.push(param);
			
		});
	}
	
	var batchData = {};
	batchData.BATCHDATAS = paramArray;
	
	console.log("batchSave",paramArray);
	//批量保存
	var rtn = FireFly.batchSave(_viewer.servId,batchData,null,2,false);
	
	setTimeout(function(){ 
		_viewer._parHandler.refreshGrid(); 
		jQuery("#" + _viewer.dialogId).dialog("close");
	}, 100);
	
});

}
