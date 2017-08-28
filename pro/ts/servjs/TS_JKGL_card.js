var _viewer = this;

_viewer.getItem("JKGL_START_DATE").obj.unbind("click").bind("click", function() {
	    WdatePicker({
	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-JKGL_END_DATE')}"
	    });
	});
_viewer.getItem("JKGL_END_DATE").obj.unbind("click").bind("click", function() {
	    WdatePicker({
	        minDate : "#F{$dp.$D('" + _viewer.servId + "-JKGL_START_DATE')}"
	    });
	});

//刚进卡片页面要判断参加考试序列，加载出对应的模块
var testSequValue=_viewer.getItem("JKGL_TEST_SEQU").getValue();
//得到父编码为1的模块数据字典的值，赋值给模块的下拉框
var param = {};
param["dicServId"] = "SY_SERV_DICT_ITEM";
param["where"] = "AND ITEM_PCODE="+"'"+testSequValue+"' ORDER BY ITEM_CODE";
var result = FireFly.doAct(_viewer.servId, "getListDicCode",param);
var itemName=result.ITEM_NAME;
var moduleName=itemName.split(",");

var str="";
for(var i=0;i<moduleName.length;i++){
	str+="<option>"+moduleName[i]+"</option>";
}
_viewer.getItem("JKGL_TEST_MODULE").obj.empty();
_viewer.getItem("JKGL_TEST_MODULE").obj.append(str);

//给序列绑定一个改变事件
_viewer.getItem("JKGL_TEST_SEQU").obj.change(function(){ //提醒方式改变

//获取数据字典字段参加考试序列字段的值
var testSequValue=_viewer.getItem("JKGL_TEST_SEQU").getValue("JKGL_TEST_SEQU");
	
//如果序列编码是JY、YWYY、FX、XD    、FL、THGW、ZH、DKHJL、      GRKHJL、WY、GY,找父编码为WUMOKUAN的模块,
if(testSequValue =="JY" || testSequValue == "YWYY" || testSequValue == "FX" || testSequValue == "XD" 
		|| testSequValue == "FL" || testSequValue == "THGW" || testSequValue == "ZH" || testSequValue == "DKHJL"
		|| testSequValue == "GRKHJL" || testSequValue == "WY" || testSequValue == "GY"){
	_viewer.getItem("JKGL_TEST_MODULE").obj.empty();
	_viewer.getItem("JKGL_TEST_MODULE").obj.append("<option>"+"无模块"+"</option>");
}else{
	//得到父编码为1的模块数据字典的值，赋值给模块的下拉框
	var param = {};
	param["dicServId"] = "SY_SERV_DICT_ITEM";
	param["where"] = "AND ITEM_PCODE="+"'"+testSequValue+"'";
	var result = FireFly.doAct(_viewer.servId, "getListDicCode",param);
	var itemName=result.ITEM_NAME;
	var moduleName=itemName.split(",");

	var str="";
	for(var i=0;i<moduleName.length;i++){
		str+="<option>"+moduleName[i]+"</option>";
	}
	_viewer.getItem("JKGL_TEST_MODULE").obj.empty();
	_viewer.getItem("JKGL_TEST_MODULE").obj.append(str);
}
	
});	








