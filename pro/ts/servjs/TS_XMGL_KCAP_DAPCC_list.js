var _viewer = this;
$("#TS_XMGL_KCAP_DAPCC .rhGrid").find("th[icode='del']").html("操作");

var xmId = _viewer.getParHandler()._pkCode;
//得到当前机构代码 得到考场
var odeptCode = System.getVar("@ODEPT_CODE@");
//确认当前机构是总行/省级机构/市级机构
var level = System.getVar("@ODEPT_LEVEL@");

_viewer.getBtn("add").unbind("click").bind("click", function(event) {	
	var param = {};
	param["SOURCE"] = "KC_ID~KC_NAME~KC_ADDRESS~CTLG_PCODE";
	param["TYPE"] = "multi";
	param["HIDE"] = "KC_ID,CTLG_PCODE";
//	param["EXTWHERE"] = "and KC_ODEPTCODE = '"+odeptCode+"'";
	param["EXTWHERE"] = " and GROUP_ID in (select group_id from TS_KCZGL_GROUP b where serv_id = 'ts_kczgl_group' and b.kcz_id in (select kcz_id from TS_KCZGL a where serv_id = 'ts_xmgl_cccs_kczgl' and XM_ID = '"+xmId+"'))";
	var configStr = "TS_XMGL_KCAP_DAPCC_UTIL_V,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var ids = idArray.KC_ID.split(",");
	    	var kcNames = idArray.KC_NAME.split(",");
	    	var ctlgpCodes = idArray.CTLG_PCODE.split(",");
	    	for(var i=0;i<ids.length;i++){
	    		var data = {}
	    		data["XM_ID"] = xmId;
	    		data["KC_ID"] = ids[i];
	    		data["KC_NAME"] = kcNames[i];
	    		data["CTLG_PCODE"] = ctlgpCodes[i];
	    		FireFly.doAct("TS_XMGL_KCAP_DAPCC", "save", data,false,false,function(result){
					if(result._MSG_.indexOf("OK") == -1){
						_viewer.listBarTipError("添加失败！");
					}else{
						var ccId = result.CC_ID;
						var kcId = result.KC_ID;
						FireFly.doAct("TS_KCGL_GLJG", "finds", {"_WHERE_":"and kc_id = '"+kcId+"'"},false,false,function(res){
							for(var i=0;i<res._DATA_.length;i++){
								var bean = {};
								bean["CC_ID"] = ccId;
								bean["JG_CODE"] = res._DATA_[i].JG_CODE;
								bean["JG_FAR"] = res._DATA_[i].JG_FAR;
								bean["JG_NAME"] = res._DATA_[i].JG_NAME;
								bean["JG_TYPE"] = res._DATA_[i].JG_TYPE;
								bean["KC_ID"] = res._DATA_[i].KC_ID;
								FireFly.doAct("TS_XMGL_KCAP_GLJG","save",bean);
							}
						});
					}
	    		});
	    	}
	    	_viewer.refresh();
	    }
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});

/*
 * 删除前方法执行
 */
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

_viewer.getBtn("addOther").unbind("click").bind("click",function(event){
	
	var param = {};
	param["SOURCE"] = "KC_ID~KC_NAME~KC_ADDRESS~KC_ODEPTCODE~CTLG_PCODE";
	param["TYPE"] = "multi";
	param["HIDE"] = "KC_ID,KC_ODEPTCODE,CTLG_PCODE";
//	param["EXTWHERE"] = "and KC_ODEPTCODE = '"+odeptCode+"'";
	var configStr = "TS_KCGL,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var ids = idArray.KC_ID.split(",");
	    	var kcNames = idArray.KC_NAME.split(",");
	    	var ctlgpCodes = idArray.CTLG_PCODE.split(",");
	    	for(var i=0;i<ids.length;i++){
	    		var data = {}
	    		data["XM_ID"] = xmId;
	    		data["KC_ID"] = ids[i];
	    		data["KC_NAME"] = kcNames[i];
	    		data["CTLG_PCODE"] = ctlgpCodes[i];
	    		FireFly.doAct("TS_XMGL_KCAP_DAPCC", "save", data,false,false,function(result){
	    			
					if(result._MSG_.indexOf("OK") == -1){
						_viewer.listBarTipError("添加失败！");
					}
	    		});
	    	}
	    	_viewer.refresh();
	    }
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});

$("#TS_XMGL_KCAP_DAPCC .rhGrid").find("th[icode='scope']").html("操作");
//删除单行数据
_viewer.grid.getBtn("scope").unbind("click").bind("click",function() {
	var pk = jQuery(this).attr("rowpk");//获取主键信息
	openMyCard(pk);
});


//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var height = jQuery(window).height()-200;
	var width = jQuery(window).width()-200;
	
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
    temp[UIConst.PK_KEY] = dataId;
    if(readOnly != ""){
    	temp["readOnly"] = readOnly;
    }
    if(showTab != ""){
    	temp["showTab"] = showTab;
    }
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
}