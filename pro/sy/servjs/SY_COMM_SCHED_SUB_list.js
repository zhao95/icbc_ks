var _viewer = this;

_viewer.getBtn("exportSql").unbind().bind("click",function(){
	var pkCodes = _viewer.grid.getSelectPKCodes();
	if(pkCodes == null || pkCodes.length == 0){
		alert("请勾选需要的导出的任务");
		return;
	}
	
	var codes = "";
	for (var i=0; i<pkCodes.length; i++){
		codes += pkCodes[i]+",";
	}
    var result = FireFly.doAct("SY_COMM_SCHED_SUB","exportSql",{JOB_IDS:codes},false,false);
    if(Tools.actIsSuccessed(result)){
    	alert("导出成功，请到数据库服务器 UTL_FILE_DIR 变量对应目录获取生成dat和sql文件！")
    }else {
    	alert("导出失败！")
    }
});
