var _viewer = this;

var action = _viewer.getItem("JG_ACTION").getValue();
if(action == ""){
	_viewer.getItem("JG_ACTION").setValue("add");
}

if(action == "" || action == "add"){
	$("#TS_KCGL_UPDATE_GLJG-JG_ACTION input[value='update']").attr("disabled", true);
	$("#TS_KCGL_UPDATE_GLJG-JG_ACTION input[value='delete']").attr("disabled", true);
}else{
	$("#TS_KCGL_UPDATE_GLJG-JG_ACTION input[value='add']").attr("disabled", true);
}

if($("#TS_KCGL_UPDATE_GLJG-JG_CODE__NAME").hasClass("disabled") == false) {
	$("#TS_KCGL_UPDATE_GLJG-JG_CODE__NAME").unbind("click").bind("click", function(event) {

		var configStr = "TS_KCGL_GLJG_ODEPT,{'TYPE':'single'}";

		var options = {
				"config" :configStr,
				"parHandler":_viewer,
				"formHandler":_viewer.form,
				"replaceCallBack":function(idArray,nameArray) {//回调，idArray为选中记录的相应字段的数组集合
					
					var codes = idArray;
					var names = nameArray;
					$("#TS_KCGL_UPDATE_GLJG-JG_CODE__NAME").val(names);
					$("#TS_KCGL_UPDATE_GLJG-JG_CODE").val(codes);
					$("#TS_KCGL_UPDATE_GLJG-JG_NAME").val(names);
					var code = idArray[0];
					FireFly.doAct("SY_ORG_DEPT_ALL","byid",{"_PK_":code},true,false,function(data){
						_viewer.getItem("JG_TYPE").setValue(data.DEPT_TYPE);
					});
					console.log($("#TS_KCGL_UPDATE_GLJG-JG_CODE").val());
					console.log($("#TS_KCGL_UPDATE_GLJG-JG_NAME").val());
				}
		};
		
		var queryView = new rh.vi.rhDictTreeView(options);
		queryView.show(event,[],[0,495]);
	});
}

_viewer.afterSave = function(resultData) {
	setTimeout(function(){ 
		_viewer._parHandler.refreshGrid(); 
		jQuery("#" + _viewer.dialogId).dialog("close");
	}, 100);
};
