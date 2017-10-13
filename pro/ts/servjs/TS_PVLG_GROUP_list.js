var _viewer = this;

//查询选择时呈现的操作.
if(_viewer.params.BUT){
	//取消行点击事件
	$(".rhGrid").find("tr").unbind("dblclick");
	$("#TS_PVLG_GROUP .rhGrid-thead tr").append('<th icode="button" class="rhGrid-thead-th" style="width:18.2%;">操作</th>');
	$("#TS_PVLG_GROUP .rhGrid-tbody ").find("tr").each(function(index, item) {//icode="button" class="rhGrid-thead-th" style="width:18.2%;"
			var dataId = item.id;
			$(item).append('<td icode="button" class="rhGrid-td-center " style="width:18.2%;"></td>');
			$(item).find("td[icode='button']").append(
					'<a class="rh-icon rhGrid-btnBar-a" id="TS_PVLG_GROUP_look" actcode="look" rowpk="'+dataId+'"><span class="rh-icon-inner">详细</span><span class="rh-icon-img btn-view"></span></a>'
			);
			// 为每个按钮绑定卡片
			lookCard();
		
	});
}

function  lookCard(){
	
	jQuery("td [actcode='look']").unbind("click").bind("click",function(){
		var pkCode = jQuery(this).attr("rowpk");
		// 定义一个对象
		var strwhere = "and G_ID ='" + pkCode + "'";
		var params = {"G_ID" : pkCode,"_extWhere" : strwhere};
		var url = "TS_PVLG_GROUP_USER.list.do?&_extWhere=" + strwhere;
		var options = {"url" : url,"params" : params,"menuFlag" : 3,"top" : true};
		Tab.open(options);
	});
	
}
//每一行添加编辑和删除
$("#TS_PVLG_GROUP .rhGrid").find("tr").each(function(index, item) {
	if(index != 0) {
		var dataId = item.id;
		
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP-upd" actcode="upd" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP-delete" actcode="delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
				);
		// 为每个按钮绑定卡片
		bindCard();
	}
});

//绑定的事件     
function bindCard() {
	//当行删除事件
	jQuery("td [id='TS_PVLG_GROUP-delete']").unbind("click").bind("click", function() {
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	//当行编辑事件
	jQuery("td [id='TS_PVLG_GROUP-upd']").unbind("click").bind("click", function() {
		var pkCode = jQuery(this).attr("rowpk");
		var height = jQuery(window).height()-200;
		var width = jQuery(window).width()-200;
		rowEdit(pkCode,_viewer,[width,height],[100,100]);
	});
}

/*
 * 删除前方法执行
 */
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
	
	module = 'GROUP';
	
	var params = {"isHide":"true", "CTLG_MODULE":module};
	
	var options = {"url":"TS_COMM_CATALOG_GROUP.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3,"top":true};
	Tab.open(options);

});

/**
 * 添加按钮
 */
_viewer.getBtn("add").unbind("click").bind("click",function() {
	var width = jQuery(window).width()-200;
	var height = jQuery(window).height()-200;
	
	var ctlgPcode = _viewer._transferData["CTLG_PCODE"];
	
	if(ctlgPcode == "" || typeof(ctlgPcode) == "undefined") {
		alert("请选择目录 !");
		return false;
	}
	var temp = {"act":UIConst.ACT_CARD_ADD,
			"sId":_viewer.servId,
			"transferData": _viewer._transferData,
			"links":_viewer.links,
			"parHandler":_viewer,
			"widHeiArray":[width,height],
			"xyArray":[100,100]
	};
	
	var cardView = new rh.vi.cardView(temp);
	cardView.show();
	return false;
});



//传给后台的数据
/*
* 业务可覆盖此方法，在导航树的点击事件加载前
*/
rh.vi.listView.prototype.beforeTreeNodeClickLoad = function(item,id,dictId) {
	var params = {};
	var user_pvlg=_viewer._userPvlg[_viewer.servId+"_PVLG"];
	params["USER_PVLG"] = user_pvlg;
	params["PVLG_FIELD"] = id;
	this.whereData["extParams"] = params;
	//点击树之前，判断是否在权限范围内，否则不能点击
	//获取登录人用户编码权限
//	var CurrentUser = System.getUser("USER_CODE");
	var arr=null;
	var i=0;
	for(let key in user_pvlg){
		if(arr==null){
			arr = user_pvlg[key].ROLE_DCODE;
		}else{
			var d = user_pvlg[key].ROLE_DCODE.split(",");
			for(var k=0;k<d.length;k++){
				if(arr.indexOf(d[k])<0){
					arr+=+","+d[k];
				}
			}
		}
	}
	console.log("arr",arr);
	/*var arrElement;","+
	if(arrLast.length>1){
		arrElement=arrLast[0];
		for(var i=1;i<arrLast.length;i++){
			if(arrLast[i]<arrElement){
				arrElement=arrLast[i];
			}
		}
	}else if(arrLast.length=1){
		arrElement=arrLast[0];
	}
	console.log(arrLast);
	if(arrElement.indexOf(',')){
		arrElement=arrElement.replace(",","");
	}*/
	var ctlg_path= item.CTLG_PATH;
	console.log(ctlg_path);
	var ctlgPathArray=ctlg_path.split("^");//最后一个元素为空
	console.log("ctlgPathArray",ctlgPathArray);
	var flag = false;
	for(var j=0;j<ctlgPathArray.length-1;j++){
		if(arr.indexOf(ctlgPathArray[j])>=0){
			flag=true;
			break;
		}
	}
	
	if(!flag){
		_viewer.listBarTipError("无权限查看所选机构数据");
		return false;
	}
	
};

//去重
function unique(arr){
    var res=[];
    for(var i=0,len=arr.length;i<len;i++){
        var obj = arr[i];
        for(var j=0,jlen = res.length;j<jlen;j++){
            if(res[j]===obj) break;            
        }
        if(jlen===j)res.push(obj);
    }
    return res;
}
