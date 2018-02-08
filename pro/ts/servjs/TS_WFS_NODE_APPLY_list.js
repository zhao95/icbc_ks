var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
var height = jQuery(window).height() - 400;
var width = jQuery(window).width() - 200;
var userCode = System.getVar("@USER_CODE@");//当前登录用户code
var wfsId = _viewer.getParHandler()._pkCode;//WFS_ID
var wfsBean = FireFly.doAct("ts_wfs_apply", 'byid', {_PK_: wfsId});




var paramS = {};
paramS["USER_CODE"] = userCode;
paramS["WFS_ID"] = wfsId;
FireFly.doAct("TS_WFS_NODEAPPLY_ADMINER", "findShDate", paramS, true, false, function (data) {
    var num = data.TMP;//所在层级

//列表需要建一个code为buttons的自定义字段。
    $("#TS_WFS_NODE_APPLY .rhGrid").find("tr").each(function (index, item) {
        if (index !== 0) {
            var dataId = item.id;

            if (num <= index
                || (userCode === "admin" || userCode === wfsBean.S_USER)
            ) {
                $(item).find("td[icode='BUTTONS']").append(
                    '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_WFS_NODE_APPLY_edit" rowpk="' + dataId + '"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
                    //'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_WFS_NODE_APPLY_addes" rowpk="'+dataId+'"><span class="rh-icon-inner">添加管理</span><span class="rh-icon-img btn-edit"></span></a>'
                    //'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_WFS_NODE_APPLY_delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
                )
            }

            //为每个按钮绑定卡片
            bindCard();
        }
    });
});
/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function (pkArray) {
    showVerify(pkArray, _viewer);
}
//绑定的事件     
function bindCard() {
    //编辑
    jQuery("td [id='TS_WFS_NODE_APPLY_edit']").unbind("click").bind("click", function () {
        var pkCode = jQuery(this).attr("rowpk");
        openMyCard(pkCode);
    });
    //当行添加管理事件
    jQuery("td [id='TS_WFS_NODE_APPLY_addes']").unbind("click").bind("click", function () {
        var pkCode = jQuery(this).attr("rowpk");//NODEID
//		var height = jQuery(window).height()-350;
//		var width = jQuery(window).width()-200;
        var temp = {
            "act": UIConst.ACT_CARD_MODIFY,
            "sId": "TS_WFS_ADMINER",
            "parHandler": _viewer,
            "widHeiArray": [width, height],
            "xyArray": [100, 100]
        };
        temp[UIConst.PK_KEY] = pkCode;//修改时，必填
        var cardView = new rh.vi.cardView(temp);
        cardView.show();
    });
    //当行删除事件
    jQuery("td [id='TS_WFS_NODE_APPLY_delete']").unbind("click").bind("click", function () {
        var pkCode = jQuery(this).attr("rowpk");
        rowDelete(pkCode, _viewer);
    });

}

//列表操作按钮 弹dialog
function openMyCard(dataId, readOnly, showTab) {
    var temp = {
        "act": UIConst.ACT_CARD_MODIFY,
        "sId": _viewer.servId,
        "parHandler": _viewer,
        "widHeiArray": [width, height],
        "xyArray": [100, 100]
    };
    temp[UIConst.PK_KEY] = dataId;
    if (readOnly != "") {
        temp["readOnly"] = readOnly;
    }
    if (showTab != "") {
        temp["showTab"] = showTab;
    }
    var cardView = new rh.vi.cardView(temp);
    cardView.show();

}
//查看审核人
$("#TS_WFS_NODE_APPLY-serchers").unbind("click").bind("click",function(){
	  var extWhere = "and WFS_ID = '" + wfsId + "'";
	  var params = {"WFS_ID": wfsId, "_extWhere": extWhere};
      var url = "TS_WFS_BMSHLC_ALLNAME.list.do?&_extWhere=" + extWhere;
      var options = {"url": url, "params": params, "menuFlag": 3, "top": true};
      //$( ".ui-dialog-titlebar-close").click();
      Tab.open(options);
});

//		var dataId = "";
//		FireFly.doAct("TS_WFS_NODEAPPLY_ADMINER","finds",{"_WHERE_":" and NODE_ID='"+pkCode+"'"},true,false,function(data){
//			if(data._DATA_.length > 0){
//				dataId = data._DATA_[0].ADMINDER_ID;
//			}
//		}); 

//		var temp = {};
//		if(dataId == ""){
//			var temp = {"act":UIConst.ACT_CARD_ADD,"sId":"TS_WFS_NODEAPPLY_ADMINER","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
//		}else{
//			var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_WFS_NODEAPPLY_ADMINER","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
//		    temp[UIConst.PK_KEY] = dataId;//修改时，必填
//		}
//		
//		temp["NODE_ID"] = pkCode;
//	    var cardView = new rh.vi.cardView(temp);
//	    cardView.show();
//		var params={NODE_ID:pkCode};
//	    var height = jQuery(window).height()-80;
//		var width = jQuery(window).width()-200;
//		var temp = {
//				//"act":UIConst.ACT_CARD_MODIFY,
//				"sId":"TS_WFS_NODEAPPLY_ADMINER",
//				//"parHandler":_viewer,
//				"widHeiArray":[width,height],
//				"resetHeiWid":[width,height],
//				"xyArray":[100,100], 
//				"params":params};
//		//temp[UIConst.PK_KEY] = pkCode;//修改时，必填
//		  var listView = new rh.vi.listView(temp);
//		    listView.show();

//		var height = jQuery(window).height()-400;
//		var width = jQuery(window).width()-200;
//		    getServListDialog(event,"sj_manager","设置审核",width,height,[100,50]);
//			const ext =  "and NODE_ID= '"+pkCode+"'";
//			var params={NODE_ID:pkCode};
//			var conf = {
//					"sId":"TS_WFS_NODEAPPLY_ADMINER",
//					 "showSearchFlag":"true",
//				    "pCon":jQuery("#sj_manager"),
//			        "parHandler":_viewer,
//			        "params":params,
//			        "linkWhere":ext
//			    };
//		    var listView = new rh.vi.listView(conf);
//		    listView.show();   
