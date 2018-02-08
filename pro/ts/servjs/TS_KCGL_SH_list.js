var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
var user_pvlg=_viewer._userPvlg["TS_KCGL_SH_PVLG"];
var roleOrgPvlg = user_pvlg.upd.ROLE_DCODE;
var userCode = System.getVar("@USER_CODE@");

//每一行添加编辑和删除
$("#TS_KCGL_SH .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		if(dataId == "") return;
		var state = $(item).find("td[icode='KC_STATE']")[0].innerText;
		var state2 = $(item).find("td[icode='KC_STATE2']")[0].innerText;
		var c1 = userCode == "admin";
		var c2 = roleOrgPvlg != undefined;
		var c3 = false;
		var c4 = false;
		if(c2){
			c3 = roleOrgPvlg.indexOf('0010100000') > -1;
			c4 = roleOrgPvlg.indexOf('0010100500') > -1;
		}
		//总行审核
		if(c1 || (c2 && (c3 || c4))){
			if(state == 3){
				$(item).css("color","red");
			}
			
			//要求：若要修改，维护后需总行级管理员审批通过后，方可生效
			setTimeout( function() {
				FireFly.doAct("TS_KCGL_UPDATE","count",{"_WHERE_":"and kc_id='"+dataId+"' and kc_commit = 1 and UPDATE_AGREE = 0"},true,false,function(data){
					if(data._DATA_ > 0){
						$(item).css("color","blue");
					}
				});
			});
		}else{
			//一级分行审核
			if(state == 3 && state2 != 1 ){
				$(item).css("color","red");
			} 
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