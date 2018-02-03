var _viewer = this;
/*测试用
var res = _viewer.getBtn("exp");
res.unbind("click").bind("click",function() {
	var options = {"url":"SY_ORG_DEPT.list.do?extWhere=aaaa","tTitle":"test","menuFlag":3};
	Tab.open(options);
});*/
$(".rhGrid").find("tr").unbind("dblclick");
var servId = _viewer.servId;
$("#"+servId+" .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").prepend(
				'<a class="rhGrid-td-rowBtnObj rh-icon"  id="'+servId+'_edit" operCode="optEditBtn" actcode="update" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="'+servId+'_group"  operCode="optGroupBtn" actcode="readGroup" rowpk="'+dataId+'"><span class="rh-icon-inner">查看群组</span><span class="rh-icon-img btn-edit"></span></a>'
		);
		bindCard();
	}
});	

function bindCard(){
	jQuery("td [id='"+servId+"_edit']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		openMyCard(pkCode);
	});
	
	jQuery("td [id='"+servId+"_group']").unbind("click").bind("click", function(){
		var pk = jQuery(this).attr("rowpk");
		var extWhere = "and USER_CODE = '" + pk + "'";
		var params = {"_extWhere" : extWhere};
		var url = "TS_PVLG_GROUP_USER_V_SY_ORG_USER.list.do?&_extWhere=" + extWhere;
		var options = {"url" : url,"params" : params,"menuFlag" : 3,"top" : true};
		Tab.open(options);
	 });
}

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

//传给后台的数据
/*
* 业务可覆盖此方法，在导航树的点击事件加载前
*/
rh.vi.listView.prototype.beforeTreeNodeClickLoad = function(item,id,dictId) {
    var params = {};
    var user_pvlg=_viewer._userPvlg[_viewer.servId+"_PVLG"];
    params["USER_PVLG"] = user_pvlg;
    _viewer.whereData["extParams"] = params;
    var flag = getListPvlg(item,user_pvlg,"CODE_PATH");
    _viewer.listClearTipLoad();
    return flag;
};

_viewer.getBtn("add").remove();
_viewer.getBtn("delete").remove();
_viewer.getBtn("impZip").remove();
_viewer.getBtn("expZip").remove();
_viewer.getBtn("exp").remove();
_viewer.getBtn("SELFDEFINED").remove();
_viewer.getBtn("initPinyin").remove();

// //重写add方法
// var height = jQuery(window).height()-50;
// var width = jQuery(window).width()-100;
// _viewer.getBtn("add").unbind("click").bind("click",function() {
//     var pcodeh = _viewer._transferData["DEPT_CODE"];
//     if(pcodeh === "" || typeof(pcodeh) === "undefined") {
//         alert("请选择添加机构层级 !");
//         return false;
//     }
//     var temp = {"act":UIConst.ACT_CARD_ADD,
//         "sId":_viewer.servId,
//         // "params":  {
//         //     "CTLG_MODULE" : module,
//         // },
//         "transferData": _viewer._transferData,
//         "links":_viewer.links,
//         "parHandler":_viewer,
//         "widHeiArray":[width,height],
//         "xyArray":[50,50]
//     };
//     console.log(temp);
//     var cardView = new rh.vi.cardView(temp);
//
//     cardView.show();
//
// });
