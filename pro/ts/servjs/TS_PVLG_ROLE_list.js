var _viewer = this;


//每一行添加编辑和删除
$("#TS_PVLG_ROLE .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		
		var roleType = $(item).find("td[icode='ROLE_TYPE']").text();
		
		if(roleType ==1) {
			
			var orgLv = $(item).find("td[icode='ROLE_ORG_LV__NAME']").text();
			
			$(item).find("td[icode='ROLE_DNAME']").text(orgLv);
		}
		
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_ROLE-upd" actcode="upd" rowpk="'+dataId+'"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
				'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_ROLE-delete" actcode="delete" rowpk="'+dataId+'"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
				);
		// 为每个按钮绑定卡片
		bindCard();
	}
});

//绑定的事件     
function bindCard() {
	//当行删除事件
	jQuery("td [id='TS_PVLG_ROLE-delete']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	//当行编辑事件
	jQuery("td [id='TS_PVLG_ROLE-upd']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		var height = jQuery(window).height()-50;
		var width = jQuery(window).width()-100;
		rowEdit(pkCode,_viewer,[width,height],[50,50]);
	});
	
}

/*
 * 删除前方法执行
 */
_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

/*
 * 删除后方法执行
 */
_viewer.afterDelete = function() {
	_viewer.refreshTreeAndGrid();
}

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

/**
 * 刷新按钮
 */
$("a[actcode='refresh']").unbind("mousedown").unbind("click").bind("click",function(event) {
	var _loadbar = new rh.ui.loadbar();
	_loadbar.show(true);
	
	_viewer.refreshTreeAndGrid();
	
	_loadbar.hideDelayed();
});

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
	
	module = 'ROLE';
	
	var params = {"isHide":"true", "CTLG_MODULE":module};
	
	var options = {"tTitle":"目录管理","url":"TS_COMM_CATALOG_ROLE.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3,"top":true};
	Tab.open(options);

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

