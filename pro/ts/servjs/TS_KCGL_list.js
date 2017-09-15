var _viewer = this;
var height = jQuery(window).height()-200;
var width = jQuery(window).width()-200;

//取消行点击事件
$(".rhGrid").find("tr").unbind("dblclick");
//列表需建一个code为BUTTONS的自定义字段
//每一行添加编辑和删除
$("#TS_KCGL .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		var state = $(item).find("td[icode='KC_STATE']").attr("title");
		
		$(item).find("td[icode='BUTTONS']").append("<div operCode='option' rowpk='"+dataId+"'><font size='3'>···</font></div>");
		
		var abtns = '<a class="rhGrid-td-rowBtnObj" operCode="optLookBtn" rowpk="'+dataId+'" style="cursor:pointer">&nbsp查看&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj" operCode="optEditBtn" rowpk="'+dataId+'" style="cursor:pointer">编辑&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj" operCode="optIPScopeBtn" rowpk="'+dataId+'" style="cursor:pointer">考场IP段设置&nbsp</span></a>'+
		'<a class="rhGrid-td-rowBtnObj" operCode="optIPZwhBtn" rowpk="'+dataId+'" style="cursor:pointer">考场IP座位号&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj" operCode="optSeatBtn" rowpk="'+dataId+'" style="cursor:pointer">系统对应座位号&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj" operCode="optJgBtn" rowpk="'+dataId+'" style="cursor:pointer">关联机构&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj" operCode="optCopyBtn" rowpk="'+dataId+'" style="cursor:pointer">复制&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj" operCode="optDeleteBtn" rowpk="'+dataId+'" style="cursor:pointer">删除&nbsp</a>';
		
		if(state < 5){
			abtns += '<a class="rhGrid-td-rowBtnObj" operCode="optTrashBtn" rowpk="'+dataId+'" style="cursor:pointer">垃圾箱&nbsp</a>';
		}else{
			abtns += '<a class="rhGrid-td-rowBtnObj" operCode="optBackTrashBtn" rowpk="'+dataId+'" style="cursor:pointer">撤销&nbsp</a>';
		}	
		
		var divHeight = $(item).get(0).offsetHeight;
		var hoverDiv = "<div class='hoverDiv' id='hoverDiv_"+dataId+"' style='height: "+divHeight+"px; line-height: "+(divHeight-4)+"px; display: none;'>"+abtns+"</div>";
		$(".content-main").find("table").before(hoverDiv);
				
		// 为每个按钮绑定卡片
		bindCard();
	}
});

//隐藏列表行按钮条
$(".hoverDiv").bind("mouseleave", function(e){
	setTimeout(function(){
		$(".hoverDiv").css('display','none');
	},1);	
});

//绑定的事件     
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
	
	//当行查看事件
	jQuery(".hoverDiv [operCode='optLookBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		openMyCard(pkCode,true);
	});
	
	//当行删除事件
	jQuery(".hoverDiv [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	
	//复制
	jQuery(".hoverDiv [operCode='optCopyBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_UTIL","copy",{"servId":_viewer.servId,"pkCode":pkCode,"primaryColCode":"KC_ID"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
				window.location.reload();
			}
		});
	});
	
	//当行编辑事件
	jQuery(".hoverDiv [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode);
		 openMyCard(pkCode);
	});
	
	//放入垃圾箱
	jQuery(".hoverDiv [operCode='optTrashBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkCode,"stateColCode":"KC_STATE","action":"add"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
//				_viewer.onRefreshGridAndTree();
				window.location.reload();
			}
		});
	});
	//从垃圾箱收回
	jQuery(".hoverDiv [operCode='optBackTrashBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkCode,"stateColCode":"KC_STATE","action":"del"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
				window.location.reload();
			}
		});
	});
	
	jQuery(".hoverDiv [operCode='optSeatBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_ZWDYB"});
	    openMyCard(pkCode,"","TS_KCGL_ZWDYB");
	});
	
	jQuery(".hoverDiv [operCode='optJgBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_GLJG"});
	    openMyCard(pkCode,"","TS_KCGL_GLJG");
	});
	
	jQuery(".hoverDiv [operCode='optIPScopeBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_IPSCOPE"});
	    openMyCard(pkCode,"","TS_KCGL_IPSCOPE");
	});
	jQuery(".hoverDiv [operCode='optIPZwhBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_IPZWH"});
	    openMyCard(pkCode,"","TS_KCGL_IPZWH");
	});
}

_viewer.getBtn("trash").unbind("click").bind("click", function(event) {
	var pkArray = _viewer.grid.getSelectPKCodes();
	if (pkArray.length == 0) {
		alert("请选择记录");
		return;
	}

	FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkArray.join(),"stateColCode":"KC_STATE","action":"add"},true,false,function(data){
		if(data._MSG_.indexOf("OK")!= -1){
			window.location.reload();
		}
	});
});

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
	
	module = 'EXAM_ROOM';
	var params = {"isHide":"true", "CTLG_MODULE":module};
	var options = {"tTitle":"考场目录管理","url":"TS_COMM_CATALOG.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3};
	options["top"] = true;
	Tab.open(options);
});

//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
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
	$(this).css("color","#014677");
},function(){
	$(this).css("color","#0071c2");
});
