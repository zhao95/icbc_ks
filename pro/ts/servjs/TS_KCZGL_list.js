var _viewer = this;
var  module = 'EXAM_GROUP';
$(".rhGrid").find("tr").unbind("dblclick");
$("#TS_KCZGL .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		if(dataId == "") return;
		var state = $(item).find("td[icode='KCZ_STATE']").attr("title");
		
		$(item).find("td[icode='BUTTONS']").append("<div operCode='option' rowpk='"+dataId+"'><font size='3'>···</font></div>");
		
		var abtns =''+	
		'<a class="rhGrid-td-rowBtnObj" operCode="optZBtn" rowpk="'+dataId+'" style="cursor:pointer">&nbsp组管理&nbsp</a>'+
// '<a class="rhGrid-td-rowBtnObj" operCode="optDeleteBtn" rowpk="'+dataId+'"
// style="cursor:pointer">删除&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj" operCode="optCopyBtn" rowpk="'+dataId+'" style="cursor:pointer">复制&nbsp</a>';
		if(state < 5){
			abtns += '<a class="rhGrid-td-rowBtnObj" operCode="optTrashBtn" rowpk="'+dataId+'" style="cursor:pointer">逻辑删除&nbsp</a>';
		}else{
			abtns += '<a class="rhGrid-td-rowBtnObj" operCode="optBackTrashBtn" rowpk="'+dataId+'" style="cursor:pointer">撤销&nbsp</a>';
		}
		
		abtns += '<a class="rhGrid-td-rowBtnObj" operCode="optEditBtn" rowpk="'+dataId+'" style="cursor:pointer">编辑&nbsp</a>';
		abtns += '<a class="rhGrid-td-rowBtnObj" operCode="optLookBtn" rowpk="'+dataId+'" style="cursor:pointer">查看&nbsp</a>';
		
		var divHeight = $(item).get(0).offsetHeight;
		var hoverDiv = "<div class='hoverDiv' id='hoverDiv_"+dataId+"' style='height: "+divHeight+"px; line-height: "+(divHeight-4)+"px; display: none;color:#666666;'>"+abtns+"</div>";
		$(".content-main").find("table").before(hoverDiv);	
		// 为每个按钮绑定卡片
		bindCard();
	}
});

// 隐藏列表行按钮条
$(".hoverDiv").bind("mouseleave", function(e){
	setTimeout(function(){
		$(".hoverDiv").css('display','none');
	},1);	
});

function bindCard(){
	jQuery("td[icode='BUTTONS']").unbind("mouseenter").bind("mouseenter", function(){
		var pkCode = jQuery(this).parent().attr("id");
		var trWidth = $(this).parent().get(0).offsetWidth;
		var divWidth = $("#hoverDiv_"+pkCode).get(0).innerText.length*9.78;
		var marginLeft = trWidth - divWidth;
		var marginTop =$(this).get(0).offsetTop;
		setTimeout(function(){
			$(".hoverDiv").css('display','none');
			$("#hoverDiv_"+pkCode).css('display','block'); 
			$("#hoverDiv_"+pkCode).css('margin-left',marginLeft+'px'); 
			$("#hoverDiv_"+pkCode).css('margin-top',marginTop+'px'); 
			$("#hoverDiv_"+pkCode).focus();
		},10);
	});	
	// 当行查看事件
	jQuery(".hoverDiv [operCode='optLookBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		$(".hoverDiv").css('display','none');
// _viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",true);
		openMyCard(pkCode,true);
	});
	// 当行编辑事件
	jQuery(".hoverDiv [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		$(".hoverDiv").css('display','none');
// _viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode);
		openMyCard(pkCode);
	});
	// 当行删除事件
	jQuery(".hoverDiv [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		$(".hoverDiv").css('display','none');
		rowDelete(pkCode,_viewer);
	});
	// 复制
	jQuery(".hoverDiv [operCode='optCopyBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		$(".hoverDiv").css('display','none');
		FireFly.doAct("TS_UTIL","copy",{"servId":_viewer.servId,"pkCode":pkCode,"primaryColCode":"KCZ_ID"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
				window.location.reload();
			}
		});
	});
	// 放入垃圾箱
	jQuery(".hoverDiv [operCode='optTrashBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		$(".hoverDiv").css('display','none');
		FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkCode,"stateColCode":"KCZ_STATE","action":"add"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
// _viewer.onRefreshGridAndTree();
				window.location.reload();
			}
		});
	});
	// 从垃圾箱收回
	jQuery(".hoverDiv [operCode='optBackTrashBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		$(".hoverDiv").css('display','none');
		FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkCode,"stateColCode":"KCZ_STATE","action":"del"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
				window.location.reload();
			}
		});
	});
	
	jQuery(".hoverDiv [operCode='optZBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		$(".hoverDiv").css('display','none');
// _viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCZGL_GROUP"});
	    openMyCard(pkCode,"","TS_KCZGL_GROUP");
	});
}

_viewer.getBtn("trash").unbind("click").bind("click", function(event) {
	var pkArray = _viewer.grid.getSelectPKCodes();
	if (pkArray.length == 0) {
		Tip.showError("请选择记录", true);
		return;
	}

	FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkArray.join(),"stateColCode":"KCZ_STATE","action":"add"},true,false,function(data){
		if(data._MSG_.indexOf("OK")!= -1){
			window.location.reload();
		}
	});
});

/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

// 列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var height = jQuery(window).height()-200;
	var width = jQuery(window).width()-200;
	
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
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

$(".hoverDiv").find("a").hover(function(){
	$(this).css("color","#0066FF");
},function(){
	$(this).css("color","#666666");
});

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
	var params = {"isHide":"true", "CTLG_MODULE":module};
	var options = {"tTitle":"考场组目录管理","url":"TS_COMM_CATALOG_EXAM_GROUP.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3};
	options["top"] = true;
	Tab.open(options);
});

// 传给后台的数据
/*
 * 业务可覆盖此方法，在导航树的点击事件加载前
 */
rh.vi.listView.prototype.beforeTreeNodeClickLoad = function(item,id,dictId) {
	var params = {};
	var user_pvlg=_viewer._userPvlg[_viewer.servId+"_PVLG"];
	params["USER_PVLG"] = user_pvlg;
	_viewer.whereData["extParams"] = params;
	 var flag = getListPvlg(item,user_pvlg,"CODE_PATH");
	_viewer.listClearTipLoad();
	return flag;
};
// 重写add方法
_viewer.getBtn("add").unbind("click").bind("click",function() {
	var pcodeh = _viewer._transferData["CTLG_PCODE"];
	if(pcodeh == "" || typeof(pcodeh) == "undefined") {
		alert("请选择添加目录的层级 !");
		return false;
	}
	
	var width = jQuery(window).width()-200;
	var height = jQuery(window).height()-200;
	
	var temp = {"act":UIConst.ACT_CARD_ADD,
			"sId":_viewer.servId,
			"params":  {
				"CTLG_MODULE" : module,
			},
			"transferData": _viewer._transferData,
			"links":_viewer.links,
			"parHandler":_viewer,
			"widHeiArray":[width,height],
			"xyArray":[100,100]
	};
	console.log(temp);
	var cardView = new rh.vi.cardView(temp);
	cardView.show();
});

// 重写imp方法
_viewer.getBtn("imp").unbind("click").bind("click",function() {
	var pcodeh = _viewer._transferData["CTLG_PCODE"];
	if(pcodeh == "" || typeof(pcodeh) == "undefined") {
		Tip.showError("请选择添加目录的层级 !", true);
		return false;
	}
	
	var config = {"SERV_ID":_viewer.opts.sId, "FILE_CAT":"EXCEL_UPLOAD", "FILENUMBER":1, 
		"VALUE":5, "TYPES":"*.xls;*.xlsx", "DESC":"导入Excel文件"};
	var file = new rh.ui.File({
		"config" : config,"width":"99%"
	});
	
	var importWin = new rh.ui.popPrompt({
		title:"请选择文件",
		tip:"请选择要导入的Excel文件：",
		okFunc:function() {
			var fileData = file.getFileData();
			if (jQuery.isEmptyObject(fileData)) {
				Tip.showError("请选择文件上传 !", true);
				return;
			}
			var fileId = null;
			for (var key in fileData) {
				fileId = key;
			}
			if (fileId == null){
				Tip.showError("请选择文件上传 !", true);
				return;
			}
			
			var param = {};
			param["CTLG_PCODE"] = pcodeh;
			
			_viewer._imp(fileId,param);
			importWin.closePrompt();
	        // _viewer.refreshGrid();
			file.destroy();
		},
		closeFunc:function() {
			file.destroy();
		}
	});

    var container = _viewer._getImpContainer(event, importWin);
	container.append(file.obj);
	file.obj.css({'margin-left':'5px'});
	file.initUpload();

});

_viewer.getBtn("tmplBtn").unbind("click").bind("click",function(){
	window.open(FireFly.getContextPath() + '/ts/imp_template/考场群组管理导入模版.xls');
});

/*******/
var mySearch = $(".content-navTreeCont").find("input[id='myTreeSearch']");
if(mySearch.length == 0){
	var serachDiv = "<input type='text' id='myTreeSearch' class='rhSearch-input' style='width:65%;'><input id='myTreeSearchBtn' type='button' value='查询'>";
	$(".bbit-tree").prepend(serachDiv);
}

$("#myTreeSearchBtn").unbind("click").bind("click",function(){
	var searchTree = _viewer.navTree;
	var searchVal = $("#myTreeSearch").val();
	var selectDiv = $(".bbit-tree-body").find("div[title*='"+searchVal+"']").eq(0);
	if($(".bbit-tree-body").find("div[title*='"+searchVal+"']").length == 0){_viewer.refreshTreeAndGrid(_viewer.opts);}
	selectDiv.expandParentForTpath(searchTree);
});

