var _viewer = this;
////返回按钮
//_viewer.getBtn("goback").unbind("click").bind("click", function() {
//	 window.location.href ="stdListView.jsp?frameId=TS_XMGL-tabFrame&sId=TS_XMGL&paramsFlag=false&title=项目管理";
//});
//列表需建一个code为BUTTONS的自定义字段，没行增加1个按钮
var qz_id  = "";
var kslbk_id = "";
	qz_id = $("#TS_XMGL_BM_KSLB_ADMIT-KSQZ_ID").val();
	
	kslbk_id =$("#TS_XMGL_BM_KSLB_ADMIT-KSLBK_ID").val();
$("#TS_XMGL_BM_KSLB_ADMIT_GRADE .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='buttons']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_BM_FZGKS_delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
								);
		// 为按钮绑定卡片
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
	//当行删除事件
	jQuery("td [id='TS_XMGL_BM_KSLB_ADMIT_GRADE_delete']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
}



_viewer.getBtn("add").unbind("click").bind("click",function() {
	//1.构造查询选择参数，其中参数【HTMLITEM】非必填，用以标识返回字段的值为html标签类的
	var configStr = "TS_XMGL_BM_KSLBK,{'TARGET':'','SOURCE':'KSLBK_NAME~KSLBK_CODE~KSLBK_XL~KSLBK_XL_CODE~KSLBK_MK~KSLBK_MKCODE~KSLBK_TYPE_NAME~KSLBK_TYPE~KSLBK_ID~KSLBK_TIME'," +
			"'HIDE':'KSLBK_CODE~KSLBK_XL_CODE~KSLBK_MKCODE~KSLBK_TYPE~KSLBK_TIME','EXTWHERE':'','TYPE':'multi','HTMLITEM':''}";
	/*and KSLBK_CODE!=^023001^*/
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
	    	var ids = idArray.KSLBK_ID.split(",");
	    	var xlcodes  = idArray.KSLBK_XL_CODE.split(",");
	    	var mkcodes  = idArray.KSLBK_MKCODE.split(",");
	    	var type  = idArray.KSLBK_TYPE.split(",");
	    	var lbcodes  = idArray.KSLBK_CODE.split(",");
	    	var paramjson={};
	    	var paramlist = [];
	    	 for(var i=0;i<ids.length;i++){
	    		 var param = {};
	    		 param["KSLBK_ADMIT_ID"] = ids[i];
	    		 param["KSQZ_ID"] = qz_id;
	    		 param["KSLBK_ID"]=kslbk_id;
	    		 param["KSLB_XL"]=xlcodes[i];
	    		 param["KSLB_MK"]=mkcodes[i];
	    		 param["KSLB_TYPE"]=type[i];
	    		 param["KSLB_LB"]=lbcodes[i];
	   			 paramlist.push(param);
	    	}
	    	paramjson["BATCHDATAS"]= paramlist;
	    	
	    	var result =FireFly.batchSave(_viewer.servId,paramjson,"",false,false);	
//	    	if(result._DATA_.length >0){
//	    		for(var j=0;j<result._DATA_.length;j++){
//	    			var paramKssj = {};
//	    			
//	    			paramKssj["KSSJ_KSNUM"]=result._DATA_[j].KSLB_KSNUM;
//	    			paramKssj["KSSJ_KSNAME"]=result._DATA_[j].KSLB_KSNAME;
//	    			paramKssj["XM_ID"]=result._DATA_[j].XM_ID;
//	    			//paramKssj["XM_SZ_ID"]=result._DATA_[j].XM_SZ_ID;
//	    			paramKssj["KSLB_ID"]=result._DATA_[j].KSLB_ID;
//	    			paramKsList.push(paramKssj);
//	    		}
//	    		paramKsJson["BATCHDATAS"]= paramKsList;
//	    		
//	    		FireFly.batchSave("TS_XMGL_KSSJ",paramKsJson,"",false,false);//保存到考试试卷中
//	    	}
	    	//console.log(result);
	    	//_viewer.listBarTip("保存成功");
	    	//_viewer.listBarTipError("选择失败");*/
	    	
	    	
	    	_viewer.refresh();
		}
	
	}
	
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event,[],[0,495]);
	
});
