var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
//每一行添加编辑和删除
$("#TS_PVLG_GROUP_ROLE .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		
		var groupId = $(item).find("td[icode='G_ID']").text();
		
		var roleType = $(item).find("td[icode='ROLE_TYPE']").text();
		
		if(roleType == 2) {
			$(item).find("td[icode='BUTTONS']").append(
					'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP_ROLE-relOrg" actcode="relOrg" rowpk="'+dataId+'" rowGid="'+groupId+'"><span class="rh-icon-inner">定义机构</span><span class="rh-icon-img btn-edit"></span></a>'
			);
		}
		
		$(item).find("td[icode='BUTTONS']").append(
				//'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP_ROLE-upd" actcode="upd" rowpk="'+dataId+'"><span class="rh-icon-inner-notext"></span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP_ROLE-delete" actcode="delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
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
	/*jQuery("td [id='TS_PVLG_GROUP_ROLE-upd']").unbind("click").bind("click", function() {
		var pkCode = jQuery(this).attr("rowpk");
		var height = jQuery(window).height()-200;
		var width = jQuery(window).width()-200;
		rowEdit(pkCode,_viewer,[width,height],[100,100]);
	});
	*/
	
	jQuery("td [id='TS_PVLG_GROUP_ROLE-relOrg']").unbind("click").bind("click", function(){
		
		var pkCode = jQuery(this).attr("rowpk");
		var rowGid = jQuery(this).attr("rowGid");
	
			var configStr = "TS_ORG_DEPT_ALL,{'TYPE':'single','sId':'TS_ORG_DEPT','pvlg':'CODE_PATH'}";
	
			var options = {
					"config" :configStr,
					"params" : {"USE_SERV_ID":"TS_PVLG_ROLE"},
					"parHandler":_viewer,
					"formHandler":_viewer.form,
					"replaceCallBack":function(idArray,nameArray) {//回调，idArray为选中记录的相应字段的数组集合
						
						var codes = idArray;
						var names = nameArray;
						
						var paramArray = [];

						for(var i=0;i<codes.length;i++) {
							
							var param = {};
							//群组角色主键
							param._PK_ = pkCode;
							param.G_ID = rowGid;
							
							//自定义关联机构
							param.ROLE_DCODE = codes[i];
							param.ROLE_DNAME = names[i];

							paramArray.push(param);
						}
						
						var batchData = {};
						
						 batchData.BATCHDATAS = paramArray;
						 console.log(_viewer.servId,paramArray);
						//批量保存
						var rtn = FireFly.batchSave(_viewer.servId,batchData,null,2,false);
						
						_viewer.refresh();
					}
				}
			
			var queryView = new rh.vi.rhDictTreeView(options);
			queryView.show(event,[],[0,495]);
	});
}

_viewer.getBtn("impRole").unbind("click").bind("click", function(event) {
	var configStr = "TS_PVLG_ROLE,{'TARGET':'ROLE_ID~ROLE_NAME~ROLE_TYPE~ROLE_DCODE~ROLE_DNAME~ROLE_ORG_LV~S_ATIME','SOURCE':'ROLE_ID~ROLE_NAME~ROLE_TYPE~ROLE_DCODE~ROLE_DNAME~ROLE_ORG_LV~S_ATIME'," +
	"'HIDE':'S_ATIME,ROLE_ORG_LV,ROLE_DNAME,ROLE_DCODE','TYPE':'multi','HTMLITEM':''}";
	var options = {
			"config" :configStr,
			"parHandler":_viewer,
			"formHandler":_viewer.form,
			"replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
				var codes = idArray.ROLE_ID.split(",");
				var names = idArray.ROLE_NAME.split(",");
				var satime = idArray.S_ATIME.split(",");
				var rtype = idArray.ROLE_TYPE.split(",");
				var rdname = idArray.ROLE_DNAME.split(",");
				var rdcode = idArray.ROLE_DCODE.split(",");
				var rlv = idArray.ROLE_ORG_LV.split(",");

				var paramArray = [];

				for(var i=0;i<codes.length;i++) {
					var param = {};
					//群组ID
					param.G_ID = _viewer.getParHandler()._pkCode;
					//角色编码
					param.ROLE_CODE = codes[i];
					//角色名称
					param.ROLE_NAME = names[i];
					//创建时间
					param.S_ATIME = satime[i];
					//关联机构类型
					param.ROLE_TYPE = rtype[i];
					
					if(param.ROLE_TYPE == 2) {
						//自定义关联机构
						param.ROLE_DNAME = rdname[i];
						param.ROLE_DCODE = rdcode[i];
					} else if(param.ROLE_TYPE == 1) {
						//机构层级
						param.ROLE_ORG_LV = rlv[i];
						param.ROLE_DNAME = rdname[i];
					}
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

/*
* 删除前方法执行
*/
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};