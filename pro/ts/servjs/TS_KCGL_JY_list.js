var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
//每一行添加编辑和删除
$("#TS_KCGL_JY .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;		
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_KCGL_JY-update" actcode="update" rowpk="'+dataId+'"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_KCGL_JY-stop" actcode="stop" rowpk="'+dataId+'"><span class="rh-icon-inner">禁用</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_KCGL_JY-open" actcode="open" rowpk="'+dataId+'"><span class="rh-icon-inner">解禁</span><span class="rh-icon-img btn-edit"></span></a>'
		);
		// 为每个按钮绑定卡片
		bindCard();
	}
});


/*
 * 删除前方法执行  添加验证码
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};
function bindCard(){
	//当行编辑事件
	jQuery("td [id='TS_KCGL_JY-update']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		var height = jQuery(window).height()-50;
		var width = jQuery(window).width()-100;
//		rowEdit(pkCode,_viewer,[width,height],[50,50]);
		openMyCard(pkCode,true);
	});
	//禁用考场
	jQuery("td [id='TS_KCGL_JY-stop']").unbind("click").bind("click",function() {
		var pkCode = jQuery(this).attr("rowpk");
		var state = jQuery(this).parent().parent().find("td[icode='KC_STATE']").text();
		if(state == 6){
			_viewer.listBarTipError("当前考场已被禁用！");
			return;
		}
		var param = {};
		param["_PK_"] = pkCode;
		param["KC_STATE"] = 6;
		if (confirm("确定禁用此考场？") == true) {
			FireFly.doAct(_viewer.servId, "save", param, true, false,function(data) {
				if (true) {
					_viewer.refreshGrid();
				}
			});
		}
	});
	//放开考场
	jQuery("td [id='TS_KCGL_JY-open']").unbind("click").bind("click",function() {
		var pkCode = jQuery(this).attr("rowpk");
		var state = jQuery(this).parent().parent().find("td[icode='KC_STATE']").text();
		if(state == 5){
			_viewer.listBarTipError("当前考场未被禁用！");
			return;
		}
		var param = {};
		param["_PK_"] = pkCode;
		param["KC_STATE"] = 5;
		if (confirm("确定开放此考场？") == true) {
			FireFly.doAct(_viewer.servId, "save", param, true, false,function(data) {
				if (true) {
					_viewer.refreshGrid();
				}
			});
		}
	});
}

//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[900,600],"xyArray":[100,50]};
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