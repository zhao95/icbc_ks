var _viewer = this;

_viewer.getBtn("impGroup").unbind("click").bind("click", function(event) {
	var configStr = "TS_PVLG_GROUP,{'TARGET':'G_ID~G_NAME~G_DEAD_BEGIN~G_DEAD_END~S_USER','SOURCE':'G_ID~G_NAME~G_DEAD_BEGIN~G_DEAD_END~S_USER'," +
	"'HIDE':'','TYPE':'multi','HTMLITEM':''}";
	var options = {
			"config" :configStr,
			"params":{BUT:true},
			"parHandler":_viewer,
			"formHandler":_viewer.form,
			"replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
				var codes = idArray.G_ID.split(",");
				var names = idArray.G_NAME.split(",");
				var gbegin = idArray.G_DEAD_BEGIN.split(",");
				var gend = idArray.G_DEAD_END.split(",");
				var suser = idArray.S_USER.split(",");
				
				var paramArray = [];
				for(var i=0;i<codes.length;i++) {
					var param = {};
					//项目ID
					param.XM_SZ_ID= _viewer.getParHandler().getItem("XM_SZ_ID").getValue();
					//param.XM_SZ_ID= _viewer.getParHandler().grid.getSelectItemValues("XM_SZ_ID");
					param.BM_ID = _viewer.getParHandler()._pkCode;
					//群组编码
					param.RYSZQZ_CODE = codes[i];
					//群组名称名称
					param.RYSZQZ_NAME = names[i];
					param.RYSZQZ_STARTTIME = gbegin[i];
					param.RYSZQZ_ENDTTIME = gend[i];
					param.RYSZQZ_USER = suser[i];
					
					//$(".rhGrid").find("th[icode='set']").html("操作");
					paramArray.push(param);
				}
				console.log(_viewer.servId,paramArray);
				var batchData = {};
				 batchData.BATCHDATAS = paramArray;
				//批量保存
				var rtn = FireFly.batchSave(_viewer.servId,batchData,null,2,false);
				_viewer.refresh();
			}
	};
//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});
////返回按钮
//_viewer.getBtn("goback").unbind("click").bind("click", function() {
//	 window.location.href ="stdListView.jsp?frameId=TS_XMGL-tabFrame&sId=TS_XMGL&paramsFlag=false&title=项目管理";
//});
//列表需建一个code为BUTTONS的自定义字段，没行增加1个按钮
$("#TS_XMGL_BM_RYSZQZ .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='buttons']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_BM_RYSZQZ_delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'+
				'<a class="rh-icon rhGrid-btnBar-a" id="TS_XMGL_BM_RYSZQZ_look" actcode="look" rowpk="'+dataId+'"><span class="rh-icon-inner">详细</span><span class="rh-icon-img btn-view"></span></a>'
		);
		// 为按钮绑定卡片
		bindCard();
	}
});

/*
* 删除前方法执行
*/
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
}
//绑定的事件     
function bindCard(){
	//当行删除事件
	jQuery("td [id='TS_XMGL_BM_RYSZQZ_delete']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	//详情val
	jQuery("td [id='TS_XMGL_BM_RYSZQZ_look']").unbind("click").bind("click", function(){
		var ryszqzCode = jQuery(this).parent().parent().find('td[icode="RYSZQZ_CODE"]').html().trim();
		debugger;
		// 定义一个对象
		var strwhere = "and G_ID ='" + ryszqzCode + "'";
		var params = {"G_ID" : ryszqzCode,"_extWhere" : strwhere};
		var url = "TS_PVLG_GROUP_USER.list.do?&_extWhere=" + strwhere;
		var options = {"url" : url,"params" : params,"menuFlag" : 3,"top" : true};
		Tab.open(options);
	});
}