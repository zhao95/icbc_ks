var _viewer = this;

$(".rhGrid").find("tr").unbind("dblclick");

_viewer.getBtn("add").unbind("click").bind("click",function(event){
	//1.构造查询选择参数，其中参数【HTMLITEM】非必填，用以标识返回字段的值为html标签类的
	var configStr = "TS_KCGL,{'TARGET':'KC_ID~KC_NAME~KC_ODEPTNAME','SOURCE':'KC_ID~KC_NAME~KC_ODEPTNAME'," +
			"'HIDE':'','TYPE':'multi','HTMLITEM':''}";
	var options = {
		"config" :configStr,
		"parHandler":_viewer,
		"formHandler":_viewer.form,
	    "replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
	    	var groupId = _viewer.getParHandler().getPKCode();
	    	var ids = idArray.KC_ID;
	    	if(ids != "" && groupId != ""){
	    		FireFly.doAct(_viewer.servId, "kczAddKc", {"ids":ids,"groupId":groupId}, true,false,function(data){
		    		_viewer.refresh();
		    	});	
	    	}
		}
	};
	//2.用系统的查询选择组件 rh.vi.rhSelectListView()
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event);
});

$("#TS_KCZGL_KCGL .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		var state = $(item).find("td[icode='KC_STATE']").attr("title");
		
		$(item).find("td[icode='BUTTONS']").append("<div operCode='option' rowpk='"+dataId+"'><font size='3'>···</font></div>");

		var abtns = '<a class="rhGrid-td-rowBtnObj " operCode="optLookBtn" rowpk="'+dataId+'" style="cursor:pointer">&nbsp查看&nbsp</a>'+	
		'<a class="rhGrid-td-rowBtnObj " operCode="optIPScopeBtn" rowpk="'+dataId+'" style="cursor:pointer">考场IP段设置&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj " operCode="optIPZwhBtn" rowpk="'+dataId+'" style="cursor:pointer">考场IP座位号&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj " operCode="optJgBtn" rowpk="'+dataId+'" style="cursor:pointer">关联机构&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj " operCode="optEditBtn" rowpk="'+dataId+'" style="cursor:pointer">编辑&nbsp</a>'+
		'<a class="rhGrid-td-rowBtnObj " operCode="optDeleteBtn" rowpk="'+dataId+'" style="cursor:pointer">删除&nbsp</a>';
		
		var divHeight = $(item).get(0).offsetHeight;
		var hoverDiv = "<div class='hoverDiv' id='hoverDiv_"+dataId+"' style='height: "+divHeight+"px; line-height: "+(divHeight-4)+"px; display: none;color:#222222;'>"+abtns+"</div>";
		$("#TS_KCZGL_KCGL .content-main").find("table").before(hoverDiv);
		
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
		var divWidth = $("#hoverDiv_"+pkCode).get(0).innerText.length*11.1;//9.78;
		var marginLeft = trWidth - divWidth ;
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
	jQuery(".hoverDiv [operCode='optJgBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk");
//		_viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",false,{"showTab":"TS_KCGL_GLJG"});
		openMyCard(pkCode,"","TS_KCGL_GLJG");
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
}

//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[1000,600],"xyArray":[100,50]};
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

_viewer.getBtn("updateInfo").unbind("click").bind("click", function(event) {
	var pkArray = _viewer.grid.getSelectPKCodes();
	if (pkArray.length == 0) {
		alert("请选择记录");
		return;
	}
	FireFly.doAct(_viewer.servId,"updateKcInfo",{"servId":_viewer.servId,"pkCodes":pkArray.join()},true,false,function(data){
		if(data._MSG_.indexOf("OK")!= -1){
			_viewer.refresh();
		}
	});
});

//判断当前是考试组管理下还是项目管理下
var kczId = _viewer.getParHandler().getItem("KCZ_ID").getValue();
if(kczId != ""){
	var res = FireFly.byId("TS_KCZGL",kczId);
	if(res.SERV_ID == "TS_XMGL_CCCS_KCZGL"){
		_viewer.getBtn("updateInfo").hide();
	}else{
		_viewer.getBtn("updateInfo").show();
	}
}

/*
* 删除前方法执行
*/
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

$(".hoverDiv").find("a").hover(function(){
	$(this).css("color","#0066FF");
},function(){
	$(this).css("color","#222222");
});