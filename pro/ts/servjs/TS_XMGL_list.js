var _viewer = this;
var d = $("<div id='d' tabindex='1' style='background-color:#d6e0f5;border:0px solid #DCE6F5;display:none;'></div>");// 获取<div id="d">的  
var tableTag = $("body");  
tableTag.append(d); 
var dTag = d.get(0);//dTag = div#d
 //创建自定义字段，增加按钮
$(".rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var XM_ID = item.id;
	 $(item).find("td[icode='buttons']").append(
     '<div><span id="span_'+XM_ID+'" >....</span></div>'
	 );
	 
	 var btn = '<div id="div_'+XM_ID+'"  style="display:none;min-width:40px;min-height:25px">'+
     '<a style="cursor:pointer" id="TS_XMGL_look'+XM_ID+'" actcode="look" rowpk="'+XM_ID+'"><span style="font-size:14px">&nbsp;查看 | </span></a>'+
     '<a style="cursor:pointer" id="TS_XMGL_copy'+XM_ID+'" actcode="copy" rowpk="'+XM_ID+'"><span style="font-size:14px">复制 | </span></a>'+
     '<a style="cursor:pointer" id="TS_XMGL_edit'+XM_ID+'" actcode="edit" rowpk="'+XM_ID+'"><span style="font-size:14px">编辑 | </span></a>'+
     '<a style="cursor:pointer" id="TS_XMGL_set'+XM_ID+'"  actcode="set" rowpk="'+XM_ID+'"><span style="font-size:14px">设置 | </span></a>'+
     '<a style="cursor:pointer" id="TS_XMGL_delete'+XM_ID+'" actcode="delete" rowpk="'+XM_ID+'"><span style="font-size:14px">删除&nbsp;</span></a>'+
     '</div>'
     
     tableTag.append(btn); 
	 
	 bindCard();
	}
});

/*
 * 删除前方法执行
 */
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};

//按钮的操作
var p=null;//div位置
var spanLoc=null;//span位置
function bindCard(){
jQuery("td[icode='buttons'] span").unbind("mouseover").bind("mouseover", function(){
	 d.empty();
	 var tdTag =this;
	 var kk = $(this).parent().parent().get(0).clientWidth;//操作td的宽度
     var top = $(this).parent().parent().get(0).offsetTop; 
 	 var pkCode = $(this).parent().parent().parent().children().get(2).innerText;
 	 
 	 var btn = $("#div_"+pkCode);
 	
 	 //btn.css("height":tdTag.height);
 	 
 	 d.append(btn.clone().css("display",""));
 	 
// 	var winWidth=windows.innerWidth;if (windows.innerWidth) {
//        winWidth = windows.innerWidth; 
//    }else if ((document.body) && (document.body.clientWidth)) {
//        winWidth = document.body.clientWidth; 
//}

 	 dTag.style.display="block";
 	 dTag.style.position="fixed";
 	 dTag.style.left= document.body.clientWidth-220-kk/2+"px"; 
 	 dTag.style.top=top+71+"px";
 	 p=[dTag.offsetLeft,dTag.offsetTop,dTag.clientWidth,dTag.clientHeight];
 	 var spanDiv= $(this).parent().get(0);
 	 spanLoc=[spanDiv.offsetLeft,spanDiv.offsetTop,spanDiv.clientWidth,spanDiv.clientHeight];
 	 d.focus();
 	 //查看
 	 $("#TS_XMGL_look"+pkCode).unbind("click").bind("click", function(){
 		
 	    _viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",true);
 		 //openMyCard(pkCode,true);
 	 });
 	 //编辑
 	 $("#TS_XMGL_edit"+pkCode).unbind("click").bind("click", function(){
 		
 	    rowEdit(pkCode,_viewer,[1000,500],[200,100]);
 		 //openMyCard(pkCode);
 	 });
 	 //复制
 	 $("#TS_XMGL_copy"+pkCode).unbind("click").bind("click", function(){
 	    param = {};
 	    param["pkCodes"] = pkCode;
 	    FireFly.doAct(_viewer.servId, "copy", param);
 	   _viewer.refresh();
 	 });
 	 //设置
 	 $("#TS_XMGL_set"+pkCode).unbind("click").bind("click", function(){
	 	var ext =  " and XM_ID = '" + pkCode + "'";
	 	//window.location.href ="stdListView.jsp?frameId=TS_XMGL_SZ-tabFrame&sId=TS_XMGL_SZ&paramsFlag=false&title=项目管理设置&XM_ID="+pkCode+"&extWhere="+ext;
	   var url =   "stdListView.jsp?frameId=TS_XMGL_SZ-tabFrame&sId=TS_XMGL_SZ&paramsFlag=true&title=项目管理设置&XM_ID="
			+ pkCode + "&extWhere=" + ext;
		//var params = {"isHide":"true", "XM_ID":pkCode};
		//var options = {"url":"TS_XMGL_SZ.list.do?isHide=true","params":params,"menuFlag":3,"top":true};
		//Tab.open(options);
		var options = {
			"url":url,
//			//"params":params,
			"menuFlag":3,
			"top":true
		};
		Tab.open(options);
 	 });
 	 //删除
 	 $("#TS_XMGL_delete"+pkCode).unbind("click").bind("click", function(){
 		rowDelete(pkCode,_viewer);
 	 });
  });
}
	
$("#d").mouseout(function(e){
	if(p!=null && p.length>0){
		e=e||window.event;
		var x= e.clientX,y=e.clientY;
		if((x>p[0] && x<p[0]+p[2] && y>p[1] && y<p[1]+p[3]) ){
			dTag.style.display="block";
//			 console.log("b:"+p[0]+"   "+p[1]+"   "+p[2]+"   "+p[3]);
//			 console.log(x+"   "+y);
		}else{
			dTag.style.display="none";
//			console.log("d:"+p[0]+"   "+p[1]+"   "+p[2]+"   "+p[3]);
//			 console.log(x+"   "+y);
			p=null;
		}
	}
});

//列表操作按钮 弹dialog
//function openMyCard(dataId,readOnly,showTab){
//	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[1000,600],"xyArray":[100,50]};
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



	/**
	 * 目录管理
	 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
		module = 'PROJECT';
		var params = {"isHide":"true", "CTLG_MODULE":module};
		var options = {"url":"TS_COMM_CATALOG.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3,"top":true};
		Tab.open(options);

});













