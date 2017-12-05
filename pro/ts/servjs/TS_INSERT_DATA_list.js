_viewer=this;


_viewer.getBtn("ImpData").unbind("click").bind("click",function(event) {
	
	var configStr = "TS_XMGL,{'TARGET':'XM_TITLE~XM_NAME~XM_NAME','SOURCE':'XM_TITLE~XM_NAME~XM_NAME~XM_ID'," +
	"'HIDE':'XM_ID','TYPE':'single','HTMLITEM':''}";
var options = {
"config" :configStr,
"parHandler":_viewer,
"formHandler":_viewer.form,
"replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
	idArray=JSON.stringify(idArray); 
	idArray = JSON.parse(idArray);
	var infos = idArray.XM_ID;
	var param = {};
	param["XMID"]=infos;
	param["XMNAME"]=idArray.XM_NAME;
	 FireFly.doAct("TS_INSERT_DATA","insertinto",param,false,true);
	_viewer.refresh();
}
};
//2.用系统的查询选择组件 rh.vi.rhSelectListView()
var queryView = new rh.vi.rhSelectListView(options);
queryView.show(event);

});
/*
_viewer.getBtn("Impkc").unbind("click").bind("click",function(event) {
	var configStr = "SY_ORG_DEPT,{'TARGET':'DEPT_CODE~DEPT_NAME','SOURCE':'DEPT_CODE~DEPT_NAME'," +
	"'HIDE':'DEPT_CODE','TYPE':'multi','HTMLITEM':''}";
var options = {
"config" :configStr,
"parHandler":_viewer,
"formHandler":_viewer.form,
"replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
	idArray=JSON.stringify(idArray); 
	idArray = JSON.parse(idArray);
	var infos = idArray.DEPT_CODE;
	var param = {};
	param["DEPT_CODE"]=infos;
	 FireFly.doAct("TS_INSERT_DATA","insertkc",param,false,true);
	_viewer.refresh();
}
};
//2.用系统的查询选择组件 rh.vi.rhSelectListView()
var queryView = new rh.vi.rhSelectListView(options);
queryView.show(event);

});*/

_viewer.getBtn("Impkc").unbind("click").bind("click", function(event) {
	
	var configStr = "SY_ORG_DEPT_SUB,{'TARGET':'DEPT_CODE~DEPT_NAME','SOURCE':'DEPT_CODE~DEPT_NAME'," +
	"'HIDE':'DEPT_CODE','TYPE':'single','HTMLITEM':''}";
var options = {
"config" :configStr,
"parHandler":_viewer,
"formHandler":_viewer.form,
"replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
	idArray=JSON.stringify(idArray); 
	idArray = JSON.parse(idArray);
	var infos = idArray.DEPT_CODE;
	var param = {};
	param["DEPT_CODE"]=infos;
	 FireFly.doAct("TS_INSERT_DATA","insertkc",param,false,true);
	_viewer.refresh();
}
};
//2.用系统的查询选择组件 rh.vi.rhSelectListView()
var queryView = new rh.vi.rhSelectListView(options);
queryView.show(event);
});



_viewer.getBtn("ImpchData").unbind("click").bind("click",function(event) {
	
	var configStr = "TS_XMGL,{'TARGET':'XM_TITLE~XM_NAME~XM_NAME','SOURCE':'XM_TITLE~XM_NAME~XM_NAME~XM_ID'," +
	"'HIDE':'XM_ID','TYPE':'single','HTMLITEM':''}";
var options = {
"config" :configStr,
"parHandler":_viewer,
"formHandler":_viewer.form,
"replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
	idArray=JSON.stringify(idArray); 
	idArray = JSON.parse(idArray);
	var infos = idArray.XM_ID;
	var param = {};
	param["XMID"]=infos;
	param["XMNAME"]=idArray.XM_NAME;
	 FireFly.doAct("TS_INSERT_DATA","insertceshid",param,false,true);
	_viewer.refresh();
}
};
//2.用系统的查询选择组件 rh.vi.rhSelectListView()
var queryView = new rh.vi.rhSelectListView(options);
queryView.show(event);

});
