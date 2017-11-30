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

$("#TS_KCGL_UPDATE_GLJG-JG_CODE__NAME").unbind("click").bind("click", function(event) {

	var configStr = "TS_ORG_DEPT_ALL,{'TYPE':'single','sId':'TS_ORG_DEPT','pvlg':'CODE_PATH'}";

	var options = {
			"config" :configStr,
			"params" : {"USE_SERV_ID":"TS_ORG_DEPT"},
			"parHandler":_viewer,
			"formHandler":_viewer.form,
			"replaceCallBack":function(idArray,nameArray) {//回调，idArray为选中记录的相应字段的数组集合
				
				var codes = idArray;
				var names = nameArray;
				$("#TS_KCGL_UPDATE_GLJG-JG_CODE__NAME").val(names);
				$("#TS_KCGL_UPDATE_GLJG-JG_CODE").val(codes);
				$("#TS_KCGL_UPDATE_GLJG-JG_NAME").val(names);
				console.log($("#TS_KCGL_UPDATE_GLJG-JG_CODE").val());
				console.log($("#TS_KCGL_UPDATE_GLJG-JG_NAME").val());
			}
	};
	
	var queryView = new rh.vi.rhDictTreeView(options);
	queryView.show(event,[],[0,495]);
});