var _viewer = this;
//考场主键
var updateId = _viewer.getParHandler().getPKCode();
var kcId = _viewer.getParHandler().getItem("KC_ID").getValue();
_viewer.getBtn("impData").unbind("click").bind("click", function(event) {
	var param = {};
	param["SOURCE"] = "IPS_TITLE~IPS_SCOPE~IPS_DESC~IPS_ID";
	param["TYPE"] = "multi";
	param["HIDE"] = "IPS_ID";
	param["EXTWHERE"] = "and kc_id = '"+kcId+"'";
	var configStr = "TS_KCGL_IPSCOPE,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var ipIds = idArray.IPS_ID;
	    	var ipNums = ipIds.split(",").length;
	    	for(var i = 0;i < ipNums;i++){
	    		var ipId = ipIds.split(",")[i];
	    		FireFly.doAct("TS_KCGL_IPSCOPE","byid",{"_PK_":ipId},true,false,function(data){
	    			var bean = {};
	    			bean["UPDATE_ID"] = updateId;
	    			bean["IPS_ACTION"] = "update";//引入数据默认为修改
	    			bean["IPS_TITLE"] = data.IPS_TITLE;
	    			bean["IPS_SCOPE"] = data.IPS_SCOPE;
	    			bean["IPS_DESC"] = data.IPS_DESC;
	    			bean["ROOT_ID"] = data.IPS_ID;
	    			FireFly.doAct("TS_KCGL_UPDATE_IPSCOPE","save",bean);
	    		});
	    	}
		}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);	 
});


$("#TS_KCGL_UPDATE_IPSCOPE .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
//				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optDeleteBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
				);	
		// 为每个按钮绑定卡片
		bindCard();
	}
});

//绑定的事件     
function bindCard(){
	//当行删除事件
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	//当行编辑事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
	    openMyCard(pkCode);
	});
};

rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

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

