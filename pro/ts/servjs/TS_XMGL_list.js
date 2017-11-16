var _viewer = this;
var module = 'PROJECT';
var height = jQuery(window).height()-50;
var width = jQuery(window).width()-100;
//取消行点击事件
$(".rhGrid").find("tr").unbind("dblclick");
 //创建自定义字段，增加按钮
$(".rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var XM_ID = item.id;

		$(item).find("td[icode='buttons']").append("<div operCode='option' rowpk='"+XM_ID+"'><font size='3'>···</font></div>"); 
		var btns ='<a style="cursor:pointer" id="TS_XMGL_look" actcode="look" rowpk="'+XM_ID+'">&nbsp&nbsp查看&nbsp</a>'+
			'<a style="cursor:pointer " id="TS_XMGL_copy" actcode="copy" rowpk="'+XM_ID+'">复制&nbsp</a>'+
			'<a style="cursor:pointer" id="TS_XMGL_edit" actcode="edit" rowpk="'+XM_ID+'">编辑&nbsp</a>'+
//			'<a style="cursor:pointer" id="TS_XMGL_delete" actcode="delete" rowpk="'+XM_ID+'">删除&nbsp&nbsp</a>'+
			'<a style="cursor:pointer" id="TS_XMGL_set"  actcode="set" rowpk="'+XM_ID+'">设置&nbsp</a>';
		var divHeight = $(item).get(0).offsetHeight;
		var hoverDiv = "<div class='hoverDiv' id='hoverDiv_"+XM_ID+"' style=' height: "+divHeight+"px; line-height: "+(divHeight-4)+"px; display:none;color:#666666'>"+btns+"</div>";
		$(".content-main").find("table").before(hoverDiv);//="color:#F00">
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

function bindCard() {
	jQuery("td[icode='buttons']").unbind("mouseenter").bind("mouseenter",function() {
		var pkCode = jQuery(this).parent().attr("id");
		var trWidth = $(this).parent().get(0).offsetWidth;
		var divWidth = $("#hoverDiv_" + pkCode).get(0).innerText.length * 8.7;
		var marginLeft = trWidth - divWidth;
		var marginTop = $(this).get(0).offsetTop;
		setTimeout(function() {
			$(".hoverDiv").css('display', 'none');
			$("#hoverDiv_" + pkCode).css('display', 'block');
			$("#hoverDiv_" + pkCode).css('margin-left',marginLeft + 'px');
			$("#hoverDiv_" + pkCode).css('margin-top',marginTop + 'px');
			$("#hoverDiv_" + pkCode).focus();
		}, 10);
	});		
	
 	 //查看
 	 $(".hoverDiv [actcode='look']").unbind("click").bind("click", function(){
 		var pkCode = jQuery(this).attr("rowpk");
 		$(".hoverDiv").css('display','none');
 		openMyCard(pkCode,true);
 	 });
 	 //编辑
 	 $(".hoverDiv [actcode='edit']").unbind("click").bind("click", function(){
 		var pkCode = jQuery(this).attr("rowpk");
 		$(".hoverDiv").css('display','none');
 		openMyCard(pkCode);
 	 });
 	 //复制
 	 $(".hoverDiv [actcode='copy']").unbind("click").bind("click", function(){
 	  
 		var pkCode = jQuery(this).attr("rowpk");
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
 	 });
 	 //删除
 	 $(".hoverDiv [actcode='delete']").unbind("click").bind("click", function(){
 		var pkCode = jQuery(this).attr("rowpk");
 		rowDelete(pkCode,_viewer);
 	 });
}
	
//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[50,50]};
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

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
	
	var params = {"isHide":"true", "CTLG_MODULE":module};
	var options = {"url":"TS_COMM_CATALOG_PROJECT.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3,"top":true};
	Tab.open(options);
});

$(".hoverDiv").find("a").hover(function() {
	$(this).css("color", "#0066FF");//鼠标移入
}, function() {
	$(this).css("color", "#666666");//鼠标移出
}); 


/**
 * 发布按钮的功能
 */
_viewer.getBtn("fabu").unbind("click").bind("click",function(){
	//点击选择框，获取数据的id；
	var pkAarry = _viewer.grid.getSelectPKCodes();
	
	if(pkAarry.length==0){
		_viewer.listBarTipError("请选择要发布的项目！");
	}else{
		//判断数据库是否已经发布
		for (var i = 0; i < pkAarry.length; i++) {
			var  where="and XM_ID='"+pkAarry[i]+"'";
			var  data={_extWhere:where};
			var beanFb = FireFly.doAct("TS_XMGL", "query", data);
			if(beanFb._DATA_[0].XM_STATE==1){
				//Tip.show("已经发布！");
				_viewer.listBarTipError("所选项目已经发布！");
			}else if(beanFb._DATA_[0].XM_STATE==0){
				 var  paramXm={};
				 paramXm["pkCodes"]=pkAarry[i];
				showRelease(pkAarry,_viewer,paramXm);
				
//				// paramXm["pkCodes"]=pkAarry.join(',');debugger;
				
//				FireFly.doAct("TS_XMGL", "UpdateStatusStart", paramXm,false,false,function(){
//					Tip.show("项目发布成功！");
//				});
//				_viewer.refresh();
			}
				
		}
	}
	
});
//初次发布
function  firRelea(paramXm){
	 
		FireFly.doAct("TS_XMGL", "UpdateStatusStart", paramXm,false,false,function(){
			Tip.show("项目发布成功！");
		});
		_viewer.refresh();
}



//传给后台的数据
/*
* 业务可覆盖此方法，在导航树的点击事件加载前
*/

rh.vi.listView.prototype.beforeTreeNodeClickLoad = function(item,id,dictId) {
	var params = {};
	var user_pvlg=_viewer._userPvlg[_viewer.servId+"_PVLG"];
	params["USER_PVLG"] = user_pvlg;
	_viewer.whereData["extParams"] = params;
	 var flag = getListPvlg(item,user_pvlg);
	_viewer.listClearTipLoad();
	return flag;
};
//重写add方法
_viewer.getBtn("add").unbind("click").bind("click",function() {
	var pcodeh = _viewer._transferData["CTLG_PCODE"];
	if(pcodeh == "" || typeof(pcodeh) == "undefined") {
		alert("请选择添加目录的层级 !");
		return false;
	}
	
	var temp = {"act":UIConst.ACT_CARD_ADD,
			"sId":_viewer.servId,
			"params":  {
				"CTLG_MODULE" : module,
			},
			"transferData": _viewer._transferData,
			"links":_viewer.links,
			"parHandler":_viewer,
			"widHeiArray":[width,height],
			"xyArray":[50,50]
	};
	console.log(temp);
	var cardView = new rh.vi.cardView(temp);
	
	cardView.show();
	
});




/**
 * 列表发布 验证码
 * @parm pkArray 主键
 * @parm viewer 页面_viewer
 */
function showRelease(pkArray,viewer,paramXm){
	var imgDate = new Date();
	var content = '<div><table>'
			+ '<tr id="errMsg" style="visibility: hidden;"><td><font color="red" size="5">验证码错误！</font></td></tr>'
			+ '<tr><td>请输入验证码:<input name="vcode" style="height: 30px; width: 130px; font-size: 22px;" type="text" id="vcode"></td></tr>'
			+ '<tr style="height:20px"><td></td></tr>'
			+ '<tr><td>验证码：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img id="codevalidate" src="/VerifyCodeServlet/'+imgDate.getMilliseconds()+'" style="height: 25px;" onclick="changeImg()"> '
			+ '<a href="javascript:;" onclick="changeImg()"><font size="2">看不清，换一张</font></a></td></tr>'
			+ '</table></div>'
			+ '<script>function changeImg() {var myDate = new Date();var url = $("#codevalidate").prop("src");url = url + "/" + myDate.getMilliseconds();$("#codevalidate").prop("src", url);}</script>';

	var dialog = jQuery("<div></div>").addClass("dictDialog").attr("title",
			"验证码");
	var container = jQuery(content).appendTo(dialog);
	dialog.appendTo(jQuery("body"));
	var hei = 230;
	var wid = 300;
    
	var scroll = RHWindow.getScroll(parent.window);
	var viewport = RHWindow.getViewPort(parent.window);
	var top = scroll.top + viewport.height / 2 - hei / 2 - 88;
	var posArray = [ "", top ];
	dialog.dialog({
		autoOpen : true,
		height : hei,
		width : wid,
		show : "bounce",
		hide : "puff",
		modal : true,
		resizable : false,
		position : posArray,
		buttons : {
			"确定" : function() {
				var vcode = $("#vcode").val();
				if (vcode.length != 4) {
					$("#errMsg").css("visibility", "visible");
				} else {
					 FireFly.doAct("TS_UTIL", "checkVerify", {
						"vcode" : vcode
					//}, true, false, function(data) {debugger;
					}, true, false, function(data) {
						if (data.res == "true") {
							dialog.remove();
							firRelea(paramXm);
							//FireFly.listDelete(viewer.servId,{"_PK_":pkArray.toString()},true);
							viewer.refresh();
							//viewer.afterDelete();
							
						} else {
							$("#errMsg").css("visibility", "visible");
							
						}
					});
				
				}
				
			},
			"关闭" : function() {
				viewer.refresh();
				dialog.remove();
			}
		}
	});
	dialog.parent().find(".ui-dialog-titlebar-close").hide();
	var btns = jQuery(".ui-dialog-buttonpane button", dialog.parent()).attr(
			"onfocus", "this.blur()");
	btns.first().addClass("rh-small-dialog-ok");
	btns.last().addClass("rh-small-dialog-close");
	dialog.parent().addClass("rh-small-dialog").addClass(
			"rh-bottom-right-radius");
	jQuery(".ui-dialog-titlebar").last().css("display", "block");
	
}
