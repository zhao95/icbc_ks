var _viewer = this;

//证书信息的列表JS
//每一行添加编辑和删除
//列表需要建立一个code为“OPERA_S”的自定义字段
//查找到对引发的数据区域，之后给每一个行添加操作按钮
$(".rhGrid").find("tr").each(function(index, item) {
	
	if (index != 0) {
	// index等于0时是标题行
	//获取到当前行的id值
	var dataId = item.id;
	$(item).find("td[icode='OPERA_S']").append(
		'<a class="rh-icon" operCode="optEditBtn" rowpk="'+dataId+'"  title="编辑" style="padding:0px;width:13px;background:transparent;"><span class="rh-icon-img btn-edit"></span></a>'+
		'<a class="rh-icon" operCode="optDeleteBtn" rowpk="'+dataId+'" title="删除" style="padding:0px;width:13px;background:transparent;"><span class="rh-icon-img btn-delete"></span></a>'
	);
	// 为每个按钮绑定卡片方法
	bindCard();
	}
});
//绑定的事件     
function bindCard(){
	//编辑行事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
//		var pkCode = jQuery(this).attr("rowpk");//获取到当前行id
		var pkCode = $(this).parent().parent().attr("id");
		rowEdit(pkCode,_viewer,[1000,500],[200,100]);
	});
	
	//删除行事件
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
		
	});
}
//未选中记录前点击按钮提示请选择的事件
_viewer.getBtn("trash").unbind("click").bind("click", function(event) {
	var pkArray = _viewer.grid.getSelectPKCodes();
	if (pkArray.length == 0) {
		alert("请选择记录");
		return;
	}

//	FireFly.doAct("ETI_CERTINFO","trash",{"servId":_viewer.servId,"pkCodes":pkArray.join(),"stateColCode":"KC_STATE","action":"add"},true,false,function(data){
//		if(data._MSG_.indexOf("OK")!= -1){
//			window.location.reload();
//		}
	
});