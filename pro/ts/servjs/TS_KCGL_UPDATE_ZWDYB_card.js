var _viewer = this;

var action = _viewer.getItem("ZW_ACTION").getValue();
if(action == ""){
	_viewer.getItem("ZW_ACTION").setValue("add");
}

if(action == "" || action == "add"){
	$("#TS_KCGL_UPDATE_ZWDYB-ZW_ACTION input[value='update']").attr("disabled", true);
	$("#TS_KCGL_UPDATE_ZWDYB-ZW_ACTION input[value='delete']").attr("disabled", true);
}else{
	$("#TS_KCGL_UPDATE_ZWDYB-ZW_ACTION input[value='add']").attr("disabled", true);
}






