var _viewer = this;

//每一行添加编辑和删除
$("#TS_PVLG_GROUP_ROLE .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP_ROLE-upd" actcode="upd" rowpk="'+dataId+'"><span class="rh-icon-inner-notext"></span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP_ROLE-delete" actcode="delete" rowpk="'+dataId+'"><span class="rh-icon-inner-notext"></span><span class="rh-icon-img btn-delete"></span></a>'
				);
		// 为每个按钮绑定卡片
		bindCard();
	}
});

//绑定的事件     
function bindCard() {
	//当行删除事件
	jQuery("td [id='TS_PVLG_GROUP_ROLE-delete']").unbind("click").bind("click", function() {
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	//当行编辑事件
	jQuery("td [id='TS_PVLG_GROUP_ROLE-upd']").unbind("click").bind("click", function() {
		var pkCode = jQuery(this).attr("rowpk");
		var height = jQuery(window).height()-200;
		var width = jQuery(window).width()-200;
		rowEdit(pkCode,_viewer,[width,height],[100,100]);
	});
}

_viewer.getBtn("impRole").unbind("click").bind("click", function(event) {
	var configStr = "TS_PVLG_ROLE,{'TARGET':'ROLE_ID~ROLE_NAME','SOURCE':'ROLE_ID~ROLE_NAME'," +
	"'HIDE':'','TYPE':'multi','HTMLITEM':''}";
	var options = {
			"config" :configStr,
			"parHandler":_viewer,
			"formHandler":_viewer.form,
			"replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
				var codes = idArray.ROLE_ID.split(",");
				var names = idArray.ROLE_NAME.split(",");

				var paramArray = [];

				for(var i=0;i<codes.length;i++) {
					var param = {};
					//群组ID
					param.G_ID = _viewer.getParHandler()._pkCode;
					//角色编码
					param.ROLE_CODE = codes[i];
					//角色名称
					param.ROLE_NAME = names[i];
					
					paramArray.push(param);
				}
				
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