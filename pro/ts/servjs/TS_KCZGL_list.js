var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
$("#TS_KCZGL .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;		
		var state = $(item).find("td[icode='KCZ_STATE']").attr("title");
		
		$(item).find("td[icode='BUTTONS']").append("<div operCode='option' rowpk='"+dataId+"'><font size='3'>···</font></div>");
		
		var abtns ='<a class="rhGrid-td-rowBtnObj" operCode="optLookBtn" rowpk="'+dataId+'" style="cursor:pointer">&nbsp查看&nbsp</a>'+	
		'<a class="rhGrid-td-rowBtnObj" operCode="optZBtn" rowpk="'+dataId+'" style="cursor:pointer">组管理&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj" operCode="optEditBtn" rowpk="'+dataId+'" style="cursor:pointer">编辑&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj" operCode="optDeleteBtn" rowpk="'+dataId+'" style="cursor:pointer">删除&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj" operCode="optCopyBtn" rowpk="'+dataId+'" style="cursor:pointer">复制&nbsp</a>';
		if(state < 5){
			abtns += '<a class="rhGrid-td-rowBtnObj" operCode="optTrashBtn" rowpk="'+dataId+'" style="cursor:pointer">垃圾箱&nbsp</a>';
		}else{
			abtns += '<a class="rhGrid-td-rowBtnObj" operCode="optBackTrashBtn" rowpk="'+dataId+'" style="cursor:pointer">撤销&nbsp</a>';
		}	
		var divHeight = $(item).get(0).offsetHeight;
		var hoverDiv = "<div class='hoverDiv' id='hoverDiv_"+dataId+"' style='height: "+divHeight+"px; line-height: "+(divHeight-4)+"px; display: none;color:#666666;'>"+abtns+"</div>";
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
//	    _viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",true);
		openMyCard(pkCode,true);
	});
	//当行编辑事件
	jQuery(".hoverDiv [operCode='optEditBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode);
		openMyCard(pkCode);
	});
	//当行删除事件
	jQuery(".hoverDiv [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		rowDelete(pkCode,_viewer);
	});
	//复制
	jQuery(".hoverDiv [operCode='optCopyBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_UTIL","copy",{"servId":_viewer.servId,"pkCode":pkCode,"primaryColCode":"KCZ_ID"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
				window.location.reload();
			}
		});
	});
	//放入垃圾箱
	jQuery(".hoverDiv [operCode='optTrashBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkCode,"stateColCode":"KCZ_STATE","action":"add"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
//				_viewer.onRefreshGridAndTree();
				window.location.reload();
			}
		});
	});
	//从垃圾箱收回
	jQuery(".hoverDiv [operCode='optBackTrashBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
		FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkCode,"stateColCode":"KCZ_STATE","action":"del"},true,false,function(data){
			if(data._MSG_.indexOf("OK")!= -1){
				window.location.reload();
			}
		});
	});
	
	jQuery(".hoverDiv [operCode='optZBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCZGL_GROUP"});
	    openMyCard(pkCode,"","TS_KCZGL_GROUP");
	});
}

_viewer.getBtn("trash").unbind("click").bind("click", function(event) {
	var pkArray = _viewer.grid.getSelectPKCodes();
	if (pkArray.length == 0) {
		alert("请选择记录");
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

//列表操作按钮 弹dialog
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