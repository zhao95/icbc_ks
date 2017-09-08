var _viewer = this;
 //通过当前页得到关联的id,获取父级信息
var  qzId= _viewer.getParHandler().getPKCode();
var XM_SZ_ID=_viewer.getParHandler().getItem("XM_SZ_ID").getValue();
var BM_ID=_viewer.getParHandler().getItem("BM_ID").getValue();
var XM_ID=_viewer.getParHandler().getItem("XM_ID").getValue();
//列表需建一个code为BUTTONS的自定义字段，没行增加两个按钮
$("#TS_XMGL_BM_KSLB .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='buttons']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_BM_KSLB_delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
		);
		// 为每个按钮绑定卡片
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
	jQuery("td [id='TS_XMGL_BM_KSLB_delete']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
}		
_viewer.getBtn("adds").unbind("click").bind("click",function() {
	//1.构造查询选择参数，其中参数【HTMLITEM】非必填，用以标识返回字段的值为html标签类的
	var configStr = "TS_XMGL_BM_KSLBK,{'TARGET':'','SOURCE':'KSLBK_NAME~KSLBK_CODE~KSLBK_XL~KSLBK_XL_CODE~KSLBK_MK~KSLBK_MKCODE~KSLBK_TYPE_NAME~KSLBK_TYPE~KSLBK_ID'," +
			"'HIDE':'KSLBK_CODE~KSLBK_XL_CODE~KSLBK_MKCODE~KSLBK_TYPE','TYPE':'multi','HTMLITEM':''}";
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
	    	var names = idArray.KSLBK_NAME.split(",");
	    	var xls = idArray.KSLBK_XL.split(",");
	    	var mks = idArray.KSLBK_MK.split(",");
	    	var types = idArray.KSLBK_TYPE.split(",");
	    	var ids = idArray.KSLBK_ID.split(",");
	    	var LBcodes = idArray.KSLBK_CODE.split(",");
	    	var XLcodes = idArray.KSLBK_XL_CODE.split(",");
	    	var MKcodes = idArray.KSLBK_MKCODE.split(",");
	    	var typename = idArray.KSLBK_TYPE_NAME.split(",");
	    	var paramjson={};
	    	var paramlist = [];
	    	for(var i=0;i<ids.length;i++){
	    		//从数据库中查找数据
	    		FireFly.doAct("TS_XMGL_BM_KSLB","finds",{"_WHERE_":"and KSQZ_ID = '"+qzId+"' and KSLBK_ID='"+ids[i]+"'"},true,false,function(data){
	    			if(data._DATA_.length ==0){
	    			var param = {};
	    			param["KSLB_NAME"] = names[i];
	    			param["KSLB_XL"] = xls[i];
	    			param["KSLB_MK"] = mks[i];
	   				param["KSLB_TYPE"] = types[i]; 
	   				param["KSLBK_ID"]=ids[i];
	   				param["KSQZ_ID"] = qzId;
	   				param["XM_SZ_ID"] = XM_SZ_ID;
	   				param["BM_ID"] = BM_ID;
	   				param["XM_ID"] = XM_ID;
	   				param["KSLB_CODE"]=LBcodes[i];
	   				param["KSLB_XL_CODE"]=XLcodes[i];
	   				param["KSLB_MK_CODE"]=MKcodes[i];
	   				param["KSLB_TYPE_NAME"]=typename[i];
	   				paramlist.push(param);
	   				console.log(param);
	    	    }
    		});    
	    	}
	    	paramjson["BATCHDATAS"]= paramlist;
	    	var result =FireFly.batchSave(_viewer.servId,paramjson,"",false,true);	
	    	/*console.log(result);
	    	_viewer.listBarTip("保存成功");
	    	_viewer.listBarTipError("选择失败");*/
	    	_viewer.refresh();
		}
	}
	
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});

//返回按钮
_viewer.getBtn("goback").unbind("click").bind("click", function() {
	 window.location.href ="stdListView.jsp?frameId=TS_XMGL-tabFrame&sId=TS_XMGL&paramsFlag=false&title=项目管理";
});


