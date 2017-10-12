var _viewer = this;
$("#TS_KCGL_GLJG .rhGrid").find("tr").unbind("dblclick"); 
$("#TS_KCGL_GLJG .rhGrid").find("th[icode='del']").html("操作");

//删除单行数据
//_viewer.grid.getBtn("del").unbind("click").bind("click",function() {
//	var pk = jQuery(this).attr("rowpk");//获取主键信息
//	rowDelete(pk,_viewer);
//});

/*
* 删除前方法执行
*/
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

$("#TS_KCGL_GLJG .rhGrid").find("tr").each(function(index, item) {
	debugger;
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").prepend(
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optEditBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<span style="height:30px;display:block;padding-left:200px;margin-top:-21px"><select style="width:50px" rowpk="'+dataId+'" operCode="'+dataId+'" name="TS_KCGL_GLJG-JG_FAR" ></select></span>'
		);
		bindCard(dataId);
	}
});	
function bindCard(dataId){
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		openMyCard(dataId);
	});
	var params={};
	params["dataId"]=dataId;
	var result = FireFly.doAct("TS_KCGL_GLJG",'getData',params);
	var dbfar = result.far;
	if(dbfar==1){
		jQuery("select[operCode="+dataId+"]").eq(0).append("<option selected='selected' value='1'>远</option>");//options.add(new Option('1','远')); 
		jQuery("select[operCode="+dataId+"]").eq(0).append("<option  value='2'>近</option>");//options.add(new Option('2','近')); 
		jQuery("select[operCode="+dataId+"]").eq(0).append("<option value='0'></option>");
	}else if(dbfar==2){
		jQuery("select[operCode="+dataId+"]").eq(0).append("<option  selected='selected' value='2'>近</option>");//options.add(new Option('2','近')); 
		jQuery("select[operCode="+dataId+"]").eq(0).append("<option  value='1'>远</option>");//options.add(new Option('1','远')); 
		jQuery("select[operCode="+dataId+"]").eq(0).append("<option value='0'></option>");
	}else{
		jQuery("select[operCode="+dataId+"]").eq(0).append("<option   value='2'>近</option>");//options.add(new Option('2','近')); 
		jQuery("select[operCode="+dataId+"]").eq(0).append("<option  value='1'>远</option>");//options.add(new O
		jQuery("select[operCode="+dataId+"]").eq(0).append("<option  selected='selected' value='0'></option>");
	}
	jQuery("select[operCode="+dataId+"]").eq(0).change(function(){
		var far = $(this).val() ;
		if(far==""){
			far=0;
		}
		var param={};
		param["far"]=far;
		param["dataId"]=dataId;
		FireFly.doAct("TS_KCGL_GLJG","editfar",param);
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