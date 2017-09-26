var _viewer = this;
var height = jQuery(window).height()-200;
var width = jQuery(window).width()-200;
//列表需要建一个code为buttons的自定义字段。
$("#TS_WFS_APPLY .rhGrid").find("tr").each(function(index,item){
	if(index !=0){
		var  dataId=item.id;
		$(item).find("td[icode='buttons']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_WFS_APPLY_edit" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_WFS_APPLY_delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_WFS_APPLY_copy" rowpk="'+dataId+'"><span class="rh-icon-inner">复制</span><span class="rh-icon-img btn-copy"></span></a>'
		);
		//为每个按钮绑定卡片
		bindCard();
	}
});
/*
* 删除前方法执行
*/
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
}
//绑定的事件     
function bindCard(){
	//编辑
	jQuery("td [id='TS_WFS_APPLY_edit']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		openMyCard(pkCode);
	});
	//当行删除事件
	jQuery("td [id='TS_WFS_APPLY_delete']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	//复制
	jQuery("td [id='TS_WFS_APPLY_copy']").unbind("click").bind("click",function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_WFS_APPLY","copy",{"servId":_viewer.servId,"pkCode":pkCode,"primaryColCode":"WFS_ID"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
				window.location.reload();
			}
		});
		
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