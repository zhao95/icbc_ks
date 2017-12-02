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
	var configStr = "TS_ORG_DEPT_ALL,{'TYPE':'single','sId':'TS_ORG_DEPT','pvlg':'CODE_PATH'}";

	var options = {
			"config" :configStr,
			"params" : {"USE_SERV_ID":"TS_ORG_DEPT"},
			"rebackCodes":"DEPT_TYPE",
			"parHandler":_viewer,
			"formHandler":_viewer.form,
			"replaceCallBack":function(idArray,nameArray) {//回调，idArray为选中记录的相应字段的数组集合
				var codes = idArray;
				var names = nameArray;
				$("#TS_KCGL_GLJG-JG_CODE__NAME").val(names);
				$("#TS_KCGL_GLJG-JG_CODE").val(codes);
				$("#TS_KCGL_GLJG-JG_NAME").val(names);
				
				var code = idArray[0];
				FireFly.doAct("SY_ORG_DEPT_ALL","byid",{"_PK_":code},true,false,function(data){
					_viewer.getItem("JG_TYPE").setValue(data.DEPT_TYPE);
				});
				
			}
	};
	
	var queryView = new rh.vi.rhDictTreeView(options);
	queryView.show(event,[],[0,495]);
}

