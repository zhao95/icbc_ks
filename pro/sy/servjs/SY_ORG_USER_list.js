var _viewer = this;
/*测试用
var res = _viewer.getBtn("exp");
res.unbind("click").bind("click",function() {
	var options = {"url":"SY_ORG_DEPT.list.do?extWhere=aaaa","tTitle":"test","menuFlag":3};
	Tab.open(options);
});*/
$(".rhGrid").find("tr").unbind("dblclick"); 
$(".rhGrid").find("th[icode='editBtn']").html("操作");

_viewer.grid.getBtn("editBtn").unbind("click").bind("click",function() {
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