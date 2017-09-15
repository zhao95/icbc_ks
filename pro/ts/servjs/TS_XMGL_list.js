//var _viewer = this;
//var height = jQuery(window).height()-200;
//var width = jQuery(window).width()-200;
//
////取消行点击事件
//$(".rhGrid").find("tr").unbind("dblclick");
////列表需建一个code为BUTTONS的自定义字段
////每一行添加编辑和删除
//$("#TS_KCGL .rhGrid").find("tr").each(function(index, item) {
//	if(index != 0){
//		var dataId = item.id;
//		var state = $(item).find("td[icode='KC_STATE']").attr("title");
//		
//		$(item).find("td[icode='BUTTONS']").append("<div operCode='option' rowpk='"+dataId+"'><font size='3'>···</font></div>");
//		
//		var abtns = '<a class="rhGrid-td-rowBtnObj" operCode="optLookBtn" rowpk="'+dataId+'" style="cursor:pointer">&nbsp查看&nbsp</a>'+
//		'<a class="rhGrid-td-rowBtnObj" operCode="optEditBtn" rowpk="'+dataId+'" style="cursor:pointer">编辑&nbsp</a>'+
//		'<a class="rhGrid-td-rowBtnObj" operCode="optIPScopeBtn" rowpk="'+dataId+'" style="cursor:pointer">考场IP段设置&nbsp</span></a>'+
//		'<a class="rhGrid-td-rowBtnObj" operCode="optIPZwhBtn" rowpk="'+dataId+'" style="cursor:pointer">考场IP座位号&nbsp</a>'+
//		'<a class="rhGrid-td-rowBtnObj" operCode="optSeatBtn" rowpk="'+dataId+'" style="cursor:pointer">系统对应座位号&nbsp</a>'+
//		'<a class="rhGrid-td-rowBtnObj" operCode="optJgBtn" rowpk="'+dataId+'" style="cursor:pointer">关联机构&nbsp</a>'+
//		'<a class="rhGrid-td-rowBtnObj" operCode="optCopyBtn" rowpk="'+dataId+'" style="cursor:pointer">复制&nbsp</a>'+
//		'<a class="rhGrid-td-rowBtnObj" operCode="optDeleteBtn" rowpk="'+dataId+'" style="cursor:pointer">删除&nbsp</a>';
//		
//		if(state < 5){
//			abtns += '<a class="rhGrid-td-rowBtnObj" operCode="optTrashBtn" rowpk="'+dataId+'" style="cursor:pointer">垃圾箱&nbsp</a>';
//		}else{
//			abtns += '<a class="rhGrid-td-rowBtnObj" operCode="optBackTrashBtn" rowpk="'+dataId+'" style="cursor:pointer">撤销&nbsp</a>';
//		}	
//		
//		var divHeight = $(item).get(0).offsetHeight;
//		var hoverDiv = "<div class='hoverDiv' id='hoverDiv_"+dataId+"' style='height: "+divHeight+"px; line-height: "+(divHeight-4)+"px; display: none;'>"+abtns+"</div>";
//		$(".content-main").find("table").before(hoverDiv);
//				
//		// 为每个按钮绑定卡片
//		bindCard();
//	}
//});
////隐藏列表行按钮条
//$(".hoverDiv").bind("mouseleave", function(e){
////	var pkCode = jQuery(this).attr("id");
//	setTimeout(function(){
////		$("#"+pkCode).css('display','none');
//		$(".hoverDiv").css('display','none');
//	},1);	
//	
//});
//
////绑定的事件     
//function bindCard(){
//	jQuery("td[icode='BUTTONS']").unbind("mouseenter").bind("mouseenter", function(){
//		var pkCode = jQuery(this).parent().attr("id");
//		var trWidth = $(this).parent().get(0).offsetWidth;
//		var divWidth = $("#hoverDiv_"+pkCode).get(0).innerText.length*9.78;
//		var marginLeft = trWidth - divWidth;
//		var marginTop =$(this).get(0).offsetTop;
//		setTimeout(function(){
//			$(".hoverDiv").css('display','none');
//			$("#hoverDiv_"+pkCode).css('display','block'); 
//			$("#hoverDiv_"+pkCode).css('margin-left',marginLeft+'px'); 
//			$("#hoverDiv_"+pkCode).css('margin-top',marginTop+'px'); 
//			$("#hoverDiv_"+pkCode).focus();
//		},10);
//	});	
//	
//	//当行查看事件
//	jQuery(".hoverDiv [operCode='optLookBtn']").unbind("click").bind("click", function(){
//		var pkCode = jQuery(this).attr("rowpk");
//		openMyCard(pkCode,true);
//	});
//	
//	//当行删除事件
//	jQuery(".hoverDiv [operCode='optDeleteBtn']").unbind("click").bind("click", function(){
//		var pkCode = jQuery(this).attr("rowpk");
//		rowDelete(pkCode,_viewer);
//	});
//	
//	//复制
//	jQuery(".hoverDiv [operCode='optCopyBtn']").unbind("click").bind("click", function(){
//		var pkCode = jQuery(this).attr("rowpk");
//		FireFly.doAct("TS_UTIL","copy",{"servId":_viewer.servId,"pkCode":pkCode,"primaryColCode":"KC_ID"},true,false,function(data){
//			if(data._MSG_.indexOf("OK")!= -1){
//				window.location.reload();
//			}
//		});
//	});
//	
//	//当行编辑事件
//	jQuery(".hoverDiv [operCode='optEditBtn']").unbind("click").bind("click", function(){
//		var pkCode = jQuery(this).attr("rowpk");
////		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode);
//		 openMyCard(pkCode);
//	});
//	
//	//放入垃圾箱
//	jQuery(".hoverDiv [operCode='optTrashBtn']").unbind("click").bind("click", function(){
//		var pkCode = jQuery(this).attr("rowpk");
//		FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkCode,"stateColCode":"KC_STATE","action":"add"},true,false,function(data){
//			if(data._MSG_.indexOf("OK")!= -1){
////				_viewer.onRefreshGridAndTree();
//				window.location.reload();
//			}
//		});
//	});
//	//从垃圾箱收回
//	jQuery(".hoverDiv [operCode='optBackTrashBtn']").unbind("click").bind("click", function(){
//		var pkCode = jQuery(this).attr("rowpk");
//		FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkCode,"stateColCode":"KC_STATE","action":"del"},true,false,function(data){
//			if(data._MSG_.indexOf("OK")!= -1){
//				window.location.reload();
//			}
//		});
//	});
//	
//	jQuery(".hoverDiv [operCode='optSeatBtn']").unbind("click").bind("click", function(){
//		var pkCode = jQuery(this).attr("rowpk");
////		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_ZWDYB"});
//	    openMyCard(pkCode,"","TS_KCGL_ZWDYB");
//	});
//	
//	jQuery(".hoverDiv [operCode='optJgBtn']").unbind("click").bind("click", function(){
//		var pkCode = jQuery(this).attr("rowpk");
////		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_GLJG"});
//	    openMyCard(pkCode,"","TS_KCGL_GLJG");
//	});
//	
//	jQuery(".hoverDiv [operCode='optIPScopeBtn']").unbind("click").bind("click", function(){
//		var pkCode = jQuery(this).attr("rowpk");
////		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_IPSCOPE"});
//	    openMyCard(pkCode,"","TS_KCGL_IPSCOPE");
//	});
//	jQuery(".hoverDiv [operCode='optIPZwhBtn']").unbind("click").bind("click", function(){
//		var pkCode = jQuery(this).attr("rowpk");
////		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_IPZWH"});
//	    openMyCard(pkCode,"","TS_KCGL_IPZWH");
//	});
//}
//
//_viewer.getBtn("trash").unbind("click").bind("click", function(event) {
//	var pkArray = _viewer.grid.getSelectPKCodes();
//	if (pkArray.length == 0) {
//		alert("请选择记录");
//		return;
//	}
//
//	FireFly.doAct("TS_UTIL","trash",{"servId":_viewer.servId,"pkCodes":pkArray.join(),"stateColCode":"KC_STATE","action":"add"},true,false,function(data){
//		if(data._MSG_.indexOf("OK")!= -1){
//			window.location.reload();
//		}
//	});
//});
//
///*
//* 删除前方法执行
//*/
//_viewer.beforeDelete = function(pkArray) {
//	showVerify(pkArray,_viewer);
//};
//
///**
// * 目录管理
// */
//_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
//	
//	module = 'EXAM_ROOM';
//	var params = {"isHide":"true", "CTLG_MODULE":module};
//	var options = {"tTitle":"考场目录管理","url":"TS_COMM_CATALOG.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3};
//	options["top"] = true;
//	Tab.open(options);
//});
//
////列表操作按钮 弹dialog
//function openMyCard(dataId,readOnly,showTab){
//	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
//    temp[UIConst.PK_KEY] = dataId;
//    if(readOnly != ""){
//    	temp["readOnly"] = readOnly;
//    }
//    if(showTab != ""){
//    	temp["showTab"] = showTab;
//    }
//    var cardView = new rh.vi.cardView(temp);
//    cardView.show();
//}
//
//$(".hoverDiv").find("a").hover(function(){
//	$(this).css("color","#014677");
//},function(){
//	$(this).css("color","#0071c2");
//});
//









//debugger;
var _viewer = this;
//var d = $("<div id='d' tabindex='1' style='background-color:#d6e0f5;display:none;'></div>");// 获取<div id="d">的  
//var tableTag = $(".content-main"); 
//t//ableTag.append(d); 
//var dTag = d.get(0);//dTag = div#d
var height = jQuery(window).height()-200;
var width = jQuery(window).width()-200;
//取消行点击事件
$(".rhGrid").find("tr").unbind("dblclick");
 //创建自定义字段，增加按钮
$(".rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var XM_ID = item.id;
		//判断显示按钮的情况
		//if(){
//			 $(item).find("td[icode='buttons']").append(
//				     '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_look" actcode="look" title="查看" rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-edit"></span></span></a>'+	
//					 '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_copy" actcode="copy" title="复制" rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-copy"></span></span></a>'+
//					 '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_edit" actcode="edit" title="编辑" rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-edit"></span></span></a>'+
//					 '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_set" actcode="set"  title="设置"  rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-option"></span></span></a>'+
//					 '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_delete" actcode="delete"  title="删除" rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-delete"></span></span></a>'
//					 );	
		//}else{
	// $(item).find("td[icode='buttons']").append(
    // '<div><span id="span_'+XM_ID+'" >···</span></div>'
	// );
		$(item).find("td[icode='buttons']").append("<div operCode='option' rowpk='"+XM_ID+"'><font size='3'>···</font></div>"); 
	
		var btns = //'<div id="div_'+XM_ID+'"  style="display:none;min-width:40px;min-height:25px">'+
			'<a style="cursor:pointer" id="TS_XMGL_look" actcode="look" rowpk="'+XM_ID+'">&nbsp查看&nbsp</a>'+
			'<a style="cursor:pointer" id="TS_XMGL_copy" actcode="copy" rowpk="'+XM_ID+'">复制&nbsp</a>'+
			'<a style="cursor:pointer" id="TS_XMGL_edit" actcode="edit" rowpk="'+XM_ID+'">编辑&nbsp</a>'+
			'<a style="cursor:pointer" id="TS_XMGL_set"  actcode="set" rowpk="'+XM_ID+'">设置&nbsp</a>'+
			'<a style="cursor:pointer" id="TS_XMGL_delete" actcode="delete" rowpk="'+XM_ID+'">删除&nbsp</a>';
     //'</div>'
     //'<a class="rhGrid-td-rowBtnObj" operCode="optEditBtn" rowpk="'+dataId+'" style="cursor:pointer">编辑&nbsp</a>'+
     //tableTag.append(btn); 
		//}
    
		var divHeight = $(item).get(0).offsetHeight;
		var hoverDiv = "<div class='hoverDiv' id='hoverDiv_"+XM_ID+"' style='height: "+divHeight+"px; line-height: "+(divHeight-4)+"px; display:none;'>"+btns+"</div>";
		$(".content-main").find("table").before(hoverDiv);
		bindCard();
	}
});

//隐藏列表行按钮条
$(".hoverDiv").bind("mouseleave", function(e){
	setTimeout(function(){
		$(".hoverDiv").css('display','none');
	},1);	
});

/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};	
	

function bindCard(){
jQuery("td[icode='buttons']").unbind("mouseenter").bind("mouseenter", function(){
	//var pkCode = $(this).parent().parent().parent().children().get(2).innerText;
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
//jQuery(".hoverDiv [operCode='optLookBtn']").unbind("click").bind("click", function(){
//	var pkCode = jQuery(this).attr("rowpk");
//	openMyCard(pkCode,true);
//});
	
	
	 //var pkCode = $(this).parent().parent().parent().children().get(2).innerText;
 	 //查看
 	 $(".hoverDiv [actcode='look']").unbind("click").bind("click", function(){
 		
 		//var pkCode = jQuery(this).parent().attr("id");
 		var pkCode = jQuery(this).attr("rowpk");debugger;
 		
 		//d.empty();
// 	    _viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",true);
 		openMyCard(pkCode,true);
 	 });
 	 //编辑
 	 $(".hoverDiv [actcode='edit']").unbind("click").bind("click", function(){
 		var pkCode = jQuery(this).attr("rowpk");
 		//d.empty();
// 	    rowEdit(pkCode,_viewer,[1000,500],[200,100]);
 		openMyCard(pkCode);
 	 });
 	 //复制
 	 $(".hoverDiv [actcode='copy']").unbind("click").bind("click", function(){
 		var pkCode = jQuery(this).attr("rowpk");
 		//d.empty();
 	    param = {};
 	    param["pkCodes"] = pkCode;
 	    FireFly.doAct(_viewer.servId, "copy", param);
 	   _viewer.refresh();
 	 });
 	 //设置
 	 $(".hoverDiv [actcode='set']").unbind("click").bind("click", function(){
 		var pkCode = jQuery(this).attr("rowpk");
	 	var extWhere = "and XM_ID = '" + pkCode + "'";
		var params = {"XM_ID" : pkCode,"_extWhere" : extWhere};
		var url = "TS_XMGL_SZ.list.do?&_extWhere=" + extWhere;
		var options = {"url" : url,"params" : params,"menuFlag" : 3,"top" : true};
		Tab.open(options);
	 	//window.location.href ="stdListView.jsp?frameId=TS_XMGL_SZ-tabFrame&sId=TS_XMGL_SZ&paramsFlag=false&title=项目管理设置&XM_ID="+pkCode+"&extWhere="+ext;

 	 });
 	 //删除
 	 $(".hoverDiv [actcode='delete']").unbind("click").bind("click", function(){
 		var pkCode = jQuery(this).attr("rowpk");
 		rowDelete(pkCode,_viewer);
 	 });
 	
// 	 d.empty();
//	 var tdTag =this;
//	 var kk = $(this).parent().parent().get(0).clientWidth;//操作td的宽度
//     var top = $(this).parent().parent().get(0).offsetTop; 
    
 	// debugger;
 	// var btn = $("#div_"+pkCode);
 	
 	 //btn.css("height":tdTag.height);
 	 
 	// d.append(btn.clone(true).css("display",""));
 	 
// 	var winWidth=windows.innerWidth;if (windows.innerWidth) {
//        winWidth = windows.innerWidth; 
//    }else if ((document.body) && (document.body.clientWidth)) {
//        winWidth = document.body.clientWidth; 
//}

// 	 dTag.style.display="block";
// 	 dTag.style.position="fixed";
// 	
// 	 //document.body.scrollWidth-
// 	 dTag.style.right= kk/8+"px"; 
// 	 dTag.style.top=top+"px";
// 	 p=[dTag.offsetLeft,dTag.offsetTop,dTag.clientWidth,dTag.clientHeight ];//
//// 	 var spanDiv= $(this).parent().get(0);
//// 	 spanLoc=[spanDiv.offsetLeft,spanDiv.offsetTop,spanDiv.clientWidth,spanDiv.clientHeight];//spanwe
// 	 d.focus();
 	 //debugger;
 
}
	

//$("#d").mouseout(function(e){
//	if(p!=null && p.length>0){
//		e=e||event;//window.event
//		var x= e.clientX,y=e.clientY;
//		if((x>p[0] && x<p[0]+p[2] && y>p[1] && y<p[1]+p[3]) ){
//			dTag.style.display="block";
////			 console.log("b:"+p[0]+"   "+p[1]+"   "+p[2]+"   "+p[3]);
////			 console.log(x+"   "+y);
//		}else{
//			dTag.style.display="none";
////			console.log("d:"+p[0]+"   "+p[1]+"   "+p[2]+"   "+p[3]);
////			 console.log(x+"   "+y);
//			p=null;
//			d.empty();
//		}
//	}
//	
//});

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
    d.empty();
}

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
		module = 'PROJECT';
		var params = {"isHide":"true", "CTLG_MODULE":module};
		var options = {"url":"TS_COMM_CATALOG.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3,"top":true};
		Tab.open(options);

});

//_viewer.getBtn("add").unbind("click").bind("click", function(event) {
//    var temp = {"act":UIConst.ACT_CARD_ADD,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
//    var cardView = new rh.vi.cardView(temp);
//    cardView.show();
//});


$(".hoverDiv").find("a").hover(function(){
	$(this).css("color","#014677");
},function(){
	$(this).css("color","#0071c2");
}); 








