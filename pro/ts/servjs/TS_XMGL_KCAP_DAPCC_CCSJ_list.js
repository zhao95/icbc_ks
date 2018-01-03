var _viewer = this;

$(".rhGrid-page").hide(); // 隐藏分页区域
$("#TS_XMGL_KCAP_DAPCC_CCSJ .rhGrid").find("tr").unbind("dblclick");
$("ul[class='tabUL tabUL-bottom ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header']").hide(); // 隐藏关联子表title

var linkWhere = _viewer.opts.linkWhere;
var ccId = linkWhere.split("'")[1];
var xmId = _viewer.getParHandler().getParHandler().getParHandler()._pkCode;

var width = jQuery(window).width()-100;
var height = jQuery(window).height()-50;
_viewer.getBtn("myAdd").unbind("click").bind("click", function(event) {
	var odeptCode = System.getVar("@ODEPT_CODE@");
	var param = {};
	param["SOURCE"] = "ARR_CC~ARR_START~ARR_END";
	param["TYPE"] = "multi";
	param["EXTWHERE"] = "and ARR_STATE = 1 and XM_ID = '"+xmId+"' and (ARR_ODEPT_CODES = '' or ARR_ODEPT_CODES is null or ARR_ODEPT_CODES = '"+odeptCode+"')";
	var configStr = "TS_XMGL_CCCS_ARRANGE,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var starts = idArray.ARR_START.split(",");
	    	var ends = idArray.ARR_END.split(",");
	    	var ccs = idArray.ARR_CC.split(",");
	    	for(var i=0;i<starts.length;i++){
	    		var data = {};
	    		data["CC_ID"] = ccId;
	    		data["SJ_CC"] = ccs[i];
	    		data["SJ_START"] = starts[i];
	    		data["SJ_END"] = ends[i];
	    		FireFly.doAct("TS_XMGL_KCAP_DAPCC_CCSJ", "save", data);
//	    		FireFly.doAct("TS_XMGL_KCAP_DAPCC_CCSJ", "save", data, true,false);
	    	}
	    	_viewer.refresh();
	    }
	};
	// 2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});

$("#TS_XMGL_KCAP_DAPCC_CCSJ .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		if(dataId == "") return;
		var addType = $(item).find("td[icode='SJ_ADDTYPE']")[0].innerText;
		var start = $(item).find("td[icode='SJ_START']")[0].innerText;
		var end = $(item).find("td[icode='SJ_END']")[0].innerText;
		if(addType !=1 && !CountMin(start,end)){
			$(item).find("a").hide(); 
		}
	}
});
	
function CountMin(val1,val2){
	var val1 = val1.substring(11).split(":");
	var val2 = val2.substring(11).split(":");
	var num1 = parseInt(val1[0])*60 + parseInt(val1[1]);
	var num2 = parseInt(val2[0])*60 + parseInt(val2[1]);
	if(num2-num1 >= 120){
		return true;
	}else{
		return false;
	}
}

_viewer.grid.getBtn("edit").unbind("click").bind("click",function() {
	var pk = jQuery(this).attr("rowpk");// 获取主键信息
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[1000,600],"xyArray":[100,50]};
    temp[UIConst.PK_KEY] = pk;
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
});

/*
 * 删除前方法执行
 */
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};