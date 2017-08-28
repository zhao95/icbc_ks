var _viewer = this;


_viewer.getBtn('restore').unbind("click").bind("click", function(event) {
	var pkArray = _viewer.grid.getSelectPKCodes();
    
	if (pkArray.length == 0) {
		alert("请选择记录");
		return;
	}
	
   var batchData = {};
   var tempArray = [];
   jQuery.each(pkArray, function(i,userCode) {
	   var temp = {"USER_CODE":userCode,"S_FLAG":1,"_ADD_":false,"_PK_":userCode};
		   tempArray.push(temp);
   });
   batchData["BATCHDATAS"] = tempArray;
   
   var resultData = FireFly.batchSave(_viewer.servId,batchData,null,_viewer.getNowDom());


	_viewer.refreshGrid();
});
