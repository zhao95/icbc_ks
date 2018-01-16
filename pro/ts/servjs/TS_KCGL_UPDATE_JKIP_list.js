var _viewer = this;

var updateId = _viewer.getParHandler().getPKCode();
var kcId = _viewer.getParHandler().getItem("KC_ID").getValue();
_viewer.getBtn("impData").unbind("click").bind("click", function(event) {
	var param = {};
	param["SOURCE"] = "JKIP_NAME~JKIP_IP~JKIP_ID";
	param["TYPE"] = "multi";
	param["HIDE"] = "JKIP_ID";
	param["EXTWHERE"] = "and kc_id = '"+kcId+"'";
	var configStr = "TS_KCGL_JKIP,"+JsonToStr(param);
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {
	    	var jkIds = idArray.JKIP_ID;
	    	var jkNums = jkIds.split(",").length;
	    	for(var i = 0;i < jkNums;i++){
	    		var jkId = jkIds.split(",")[i];
	    		FireFly.doAct("TS_KCGL_JKIP","byid",{"_PK_":jkId},true,false,function(data){
	    			var bean = {};
	    			bean["UPDATE_ID"] = updateId;
	    			bean["JKIP_ACTION"] = "update";//引入数据默认为修改
	    			bean["JKIP_IP"] = data.JKIP_IP;
	    			bean["IPS_DESC"] = data.IPS_DESC;
	    			bean["ROOT_ID"] = data.JKIP_ID;
	    			FireFly.doAct("TS_KCGL_UPDATE_JKIP","save",bean);
	    		});
	    	}
	    	setTimeout(function(){
	    		 _viewer.refreshGrid();
	         },100);
		}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});

$("#TS_KCGL_UPDATE_JKIP .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optLookBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
				);	
		// 为每个按钮绑定卡片
		bindCard();
	}
});

//绑定的事件     
function bindCard(){
	
	//当行编辑事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
	    openMyCard(pkCode);
	});
	
	//当行查看事件
	jQuery("td [operCode='optLookBtn']").unbind("click").bind("click", function(){;
		var pkCode = jQuery(this).attr("rowpk");
		openMyCard(pkCode,true);
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

if (_viewer.getParHandler().getParHandler().getParHandler() != undefined
		&& _viewer.getParHandler().getParHandler().getParHandler() != null) {
	var parparServId = _viewer.getParHandler().getParHandler().getParHandler().servId;
	if (parparServId == "TS_KCGL_SH") {
		$("#TS_KCGL_UPDATE_JKIP-add").hide();
		$("#TS_KCGL_UPDATE_JKIP-impData").hide();
		$("#TS_KCGL_UPDATE_JKIP-delete").hide();
		$("#TS_KCGL_UPDATE_JKIP").find("a[opercode='optEditBtn']").hide();
	}
}

