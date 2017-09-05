var _viewer = this;
//创建自定义字段，增加按钮
$(".rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var XM_ID = item.id;
	 $(item).find("td[icode='buttons']").append(
     '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_look" actcode="look" title="查看" rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-edit"></span></span></a>'+	
	 '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_copy" actcode="copy" title="复制" rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-copy"></span></span></a>'+
	 '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_edit" actcode="edit" title="编辑" rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-edit"></span></span></a>'+
	 '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_set" actcode="set" title="设置"  rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-option"></span></span></a>'+
	 '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_delete" actcode="delete" title="删除" rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-delete"></span></span></a>'
	 );
	bindCard();
	}
});

/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

function bindCard(){
	//查看
	jQuery("td [id='TS_XMGL_look']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
	    _viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",true);
	});
//编辑
jQuery("td [id='TS_XMGL_edit']").unbind("click").bind("click", function(){
	var pkCode = jQuery(this).attr("rowpk");
	rowEdit(pkCode,_viewer,[1000,500],[200,100]);
});
//复制
jQuery("td [id='TS_XMGL_copy']").unbind("click").bind("click", function(){
	var pkCode = jQuery(this).attr("rowpk");
       param = {};
        param["pkCodes"] = pkCode;
        FireFly.doAct(_viewer.servId, "copy", param);
       _viewer.refresh();
});
//设置
jQuery("td [id='TS_XMGL_set']").unbind("click").bind("click", function(){
	var pkCode = jQuery(this).attr("rowpk");
    var ext =  " and XM_ID = '" + pkCode + "'";
    window.location.href ="stdListView.jsp?frameId=TS_XMGL_SZ-tabFrame&sId=TS_XMGL_SZ&paramsFlag=false&title=项目管理设置&XM_ID="+pkCode+"&extWhere="+ext;
});
    //删除
jQuery("td [id='TS_XMGL_delete']").unbind("click").bind("click", function(){
	var pkCode = jQuery(this).attr("rowpk");
	rowDelete(pkCode,_viewer);
});

}
	
	/**
	 * 目录管理
	 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
		module = 'PROJECT';
		var params = {"isHide":"true", "CTLG_MODULE":module};
		var options = {"url":"TS_COMM_CATALOG.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3};
		Tab.open(options);

});
