var _viewer = this;
/*测试用
var res = _viewer.getBtn("exp");
res.unbind("click").bind("click",function() {
	var options = {"url":"SY_ORG_DEPT.list.do?extWhere=aaaa","tTitle":"test","menuFlag":3};
	Tab.open(options);
});*/
var servId = _viewer.servId;
$("#"+servId+".rhGrid").find("tr").unbind("dblclick"); 
$("#"+servId+" .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").prepend(
				'<a class="rhGrid-td-rowBtnObj rh-icon"  id="'+servId+'_edit" operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="'+servId+'_group"  operCode="optGroupBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">查看群组</span><span class="rh-icon-img btn-edit"></span></a>'
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