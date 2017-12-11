var _viewer = this;
//取消行点击事件TS_WFS_NODEAPPLY_ADMINER
$(".rhGrid").find("tr").unbind("dblclick");
var  height=jQuery(window).height()-200;
var  width=jQuery(window).width()-200;
//列表需要建一个code为buttons的自定义字段。
$("#TS_WFS_NODEAPPLY_ADMINER .rhGrid").find("tr").each(function(index,item){
	if(index !=0){
		var  dataId=item.id;
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_WFS_NODEAPPLY_ADMINER_edit" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'
				//'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_WFS_BMSHLC_delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
				)
				//为每个按钮绑定卡片
				bindCard();
	}
});


//绑定的事件     
function bindCard(){
	//编辑
	jQuery("td [id='TS_WFS_NODEAPPLY_ADMINER_edit']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		openMyCard(pkCode);
	});
}

//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
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