var _viewer = this;

var height = jQuery(window).height()-200;
var width = jQuery(window).width()-200;
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
			'<a style="cursor:pointer" id="TS_XMGL_set"  actcode="set" rowpk="'+XM_ID+'">设置&nbsp</a>'+
			'<a style="cursor:pointer" id="TS_XMGL_delete" actcode="delete" rowpk="'+XM_ID+'">删除&nbsp&nbsp</a>';
    
		var divHeight = $(item).get(0).offsetHeight;
		var hoverDiv = "<div class='hoverDiv' id='hoverDiv_"+XM_ID+"' style=' height: "+divHeight+"px; line-height: "+(divHeight-4)+"px; display:none;color:#707070'>"+btns+"</div>";
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
 		openMyCard(pkCode,true);
 	 });
 	 //编辑
 	 $(".hoverDiv [actcode='edit']").unbind("click").bind("click", function(){
 		var pkCode = jQuery(this).attr("rowpk");
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

/**
 * 目录管理
 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
	module = 'PROJECT';
	var params = {"isHide":"true", "CTLG_MODULE":module};
	var options = {"url":"TS_COMM_CATALOG.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3,"top":true};
	Tab.open(options);
});

$(".hoverDiv").find("a").hover(function() {
	$(this).css("color", "##0071c2");//鼠标移入
}, function() {
	$(this).css("color", "#707070");//鼠标移出
}); 








