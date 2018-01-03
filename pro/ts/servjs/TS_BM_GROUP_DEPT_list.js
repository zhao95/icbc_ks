var _viewer = this;
var height = jQuery(window).height() - 50;
var width = jQuery(window).width() - 200;
//取消行点击事件
$(".rhGrid").find("tr").unbind("dblclick");
//每一行添加编辑和删除
$("#TS_BM_GROUP_DEPT .rhGrid").find("tr").each(function (index, item) {
    if (index != 0) {
        var dataId = item.id;

        $(item).find("td[icode='BUTTONS']").append(
            '<a class="rhGrid-td-rowBtnObj rh-icon"  id="TS_BM_GROUP_DEPT_look" operCode="optLookBtn" rowpk="' + dataId + '"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>' +
            '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_BM_GROUP_DEPT_delete" actcode="delete" rowpk="' + dataId + '"><span class="rh-icon-inner-notext">删除</span><span class="rh-icon-img btn-delete"></span></a>'
        );
        // 为每个按钮绑定卡片
        bindCard();
    }
});

//绑定的事件     
function bindCard() {
	//当行删除事件
	jQuery("td [id='TS_BM_GROUP_DEPT_delete']").unbind("click").bind("click", function() {
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
		$("#TS_COMM_CATALOG-refresh").trigger("click");
	});
	
	 //查看
    jQuery("td [id='TS_BM_GROUP_DEPT_look']").unbind("click").bind("click", function () {
        var pkCode = jQuery(this).attr("rowpk");
        //$(".hoverDiv").css('display','none');
        openMyCard(pkCode, true);
    });
}
/*
* 删除前方法执行
*/
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
}
//列表操作按钮 弹dialog
function openMyCard(dataId, readOnly, showTab) {
    var temp = {
        "act": UIConst.ACT_CARD_MODIFY,
        "sId": _viewer.servId,
        "parHandler": _viewer,
        "widHeiArray": [width, height],
        "xyArray": [50, 50]
    };
    temp[UIConst.PK_KEY] = dataId;
    if (readOnly != "") {
        temp["readOnly"] = readOnly;
    }
    if (showTab != "") {
        temp["showTab"] = showTab;
    }
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
}
_viewer.getBtn("impDept").unbind("click").bind("click", function(event) {
//	var configStr = "TS_ORG_DEPT,{'TARGET':'DEPT_CODE~DEPT_NAME','SOURCE':'DEPT_CODE~DEPT_NAME'," +
//	"'HIDE':'DEPT_CODE','TYPE':'multi','HTMLITEM':''}";

    var configStr = "TS_ORG_DEPT_ALL,{'TYPE':'multi','sId':'TS_ORG_DEPT','pvlg':'CODE_PATH'}";

	var options = {
			"config" :configStr,
//			"params" : {"_TABLE_":"SY_ORG_USER"},
			"params" : {"USE_SERV_ID":"TS_ORG_DEPT"},
			"parHandler":_viewer,
			"formHandler":_viewer.form,
			"replaceCallBack":function(idArray,nameArray) {//回调，idArray为选中记录的相应字段的数组集合

				var codes = idArray;
				var names = nameArray;
				
				var paramArray = [];
				
				var codestr = "";
				
				for(var i=0;i<codes.length;i++){
					if(i==0){
						codestr=""+codes[i]+"";
					}else{
						codestr+=","+codes[i]+"";
					}
				}
				var paramstr = {};
				paramstr["codes"]=codestr;
				paramstr["G_ID"]=_viewer.getParHandler()._pkCode;
				var resultstr = FireFly.doAct("TS_BMLB_BM","getMaxDept",paramstr);
				/*for(var i=0;i<codes.length;i++){
					var param = {};
					//群组ID
					param.G_ID = _viewer.getParHandler()._pkCode;
					//用户编码
					param.USER_DEPT_CODE = codes[i];
					//用户名称
					param.USER_DEPT_NAME = names[i];
					//选取类型 1人员
					param.G_TYPE = 2;
					
					paramArray.push(param);
				}*/
				/* var batchData = {};
				 batchData.BATCHDATAS = paramArray;
				//批量保存
				var rtn = FireFly.batchSave(_viewer.servId,batchData,null,2,false);*/
				
				_viewer.refresh();
			}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
//	var queryView = new rh.vi.rhSelectListView(options);
	var queryView = new rh.vi.rhDictTreeView(options);
	queryView.show(event,[],[0,495]);
});