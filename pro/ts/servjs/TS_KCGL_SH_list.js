var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
//每一行添加编辑和删除
$("#TS_KCGL_SH .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		var state = $(item).find("td[icode='KC_STATE']")[0].innerText;
		var state2 = $(item).find("td[icode='KC_STATE2']")[0].innerText;
		var user_pvlg=_viewer._userPvlg["TS_KCGL_SH_PVLG"];
		var roleOrgPvlg = user_pvlg.upd.ROLE_ORG_LV;
		if(roleOrgPvlg == 3){
			if(state == 3 && state2 != 1){
				$(item).css("color","red");
			}
		}else{
			if(state == 3){
				$(item).css("color","red");
			} 
		}
		var odeptLevel = System.getVar("@ODEPT_LEVEL@");
		if(odeptLevel == 1){
			FireFly.doAct("TS_KCGL_UPDATE","count",{"_WHERE_":"and kc_id='"+dataId+"' and kc_commit = 1 and UPDATE_AGREE = 0"},true,false,function(data){
				if(data._DATA_ > 0){
					$(item).css("color","blue");
				}
			});
		}
		
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optLookBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">审核</span><span class="rh-icon-img btn-edit"></span></a>'
				);
		// 为每个按钮绑定卡片
		bindCard();
	}
});

function bindCard(){
	//当行查看事件
	jQuery("td [operCode='optLookBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode);
		openMyCard(pkCode);
	});
}

/*
* 删除前方法执行
*/
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[1000,600],"xyArray":[100,50]};
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