var _viewer = this;
var params = _viewer.getParams();
var projectId = params.JH_ID;
var projectTitle = params.JH_TITLE;
var height = jQuery(window).height()-200;
var width = jQuery(window).width()-200;
$(".rhGrid").find("tr").unbind("dblclick");
_viewer.getBtn("add").unbind("click").bind("click",function() {
	var paramDelete = {};
	paramDelete["_extWhere"] = "and JH_ID ='"+projectId+"'";
	var resultDe = FireFly.doAct("TS_JHGL","query",paramDelete);
	if(resultDe._DATA_[0]!=null){
		if(resultDe._DATA_[0].JH_STATUS =="2"){
			Tip.show("请取消发布后再进行添加！");
			return false;
		}
	}

	//打开添加页面act：方法（必填），sId：服务（必填），parHandler：当前句柄，widHeiArray:小卡片的宽度高度，xyArray：左上角坐标
    var temp = {"act":UIConst.ACT_CARD_ADD,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100],"JH_ID":projectId,"JH_TITLE":projectTitle};
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
});

//每一行添加编辑和删除
_viewer.grid._table.find("tr").each(function(index, item) {
	if (index != 0) {
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").append(
			'<a class="rh-icon rhGrid-btnBar-a" operCode="optEditBtn"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
			'<a class="rh-icon rhGrid-btnBar-a" operCode="optDeleteBtn"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
		);
		// 为每个按钮绑定卡片
		bindCard();
	}
});

/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	var paramDelete = {};
	paramDelete["_extWhere"] = "and JH_ID ='"+projectId+"'";
	var resultDe = FireFly.doAct("TS_JHGL","query",paramDelete);
	if(resultDe._DATA_[0].JH_STATUS =="2"){
		Tip.show("请取消发布后再进行删除！");
		return false;
	}
	showVerify(pkArray,_viewer);
};

//绑定的事件     
function bindCard(){
	//当行删除事件
	jQuery("td [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = $(this).parent().parent().attr("id");
		rowDelete(pkCode,_viewer);
	});
	
	//当行编辑事件
	jQuery("td [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = $(this).parent().parent().attr("id");
		//编辑修改前判断是否已发布
		var paramModify = {};
		paramModify["_extWhere"] = "and JH_ID ='"+pkCode+"'";
		var beanFb = FireFly.doAct(_viewer.servId, "query", paramModify);
		//判断是否已发布，否则提示已经发布，不能修改 
		if(beanFb._DATA_[0].JH_STATUS=="2"){
			Tip.show("请取消发布后再编辑！");
		}else if(beanFb._DATA_[0].JH_STATUS=="1"){
			openMyCard(pkCode);
		}
	});
}

//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,50]};
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