var _viewer = this;
var d = $("<div id='d' tabindex='1' style='background-color:#b6e1fd;border:1px solid #DCE6F5;display:none;'></div>");// 获取<div id="d">的  
var tableTag = $("body");  
tableTag.append(d);  
var dTag = d.get(0);  
 //创建自定义字段，增加按钮
$(".rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var XM_ID = item.id;
	 $(item).find("td[icode='buttons']").append(
	 '<div><span id="span_'+XM_ID+'" >....</span></div>'	 
     //'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_look" actcode="look" title="查看" rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-edit"></span></span></a>'+	
	// '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_copy" actcode="copy" title="复制" rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-copy"></span></span></a>'+
	// '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_edit" actcode="edit" title="编辑" rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-edit"></span></span></a>'+
	 //'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_set" actcode="set" title="设置"  rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-option"></span></span></a>'+
	 //'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_delete" actcode="delete" title="删除" rowpk="'+XM_ID+'"><span class="rh-icon-inner"><span class="rh-icon-img btn-delete"></span></span></a>'
	 );
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
	 var kk = $(this).parent().parent().get(0).clientWidth;
     var top = $(this).parent().parent().get(0).offsetTop;  
 	var pkCode = $(this).parent().parent().parent().children().get(2).innerText;
 	d.append( '<div id="div_'+pkCode+'"  style="display:block;">'+
		     '<a style="cursor:pointer" id="TS_XMGL_look'+pkCode+'"  rowpk="'+pkCode+'"  actcode="look"><span style="font-size:16px">查看</span></a>'+	
			 '<a>&nbsp</a>'+
		     '<a style="cursor:pointer" id="TS_XMGL_copy'+pkCode+'"  rowpk="'+pkCode+'" actcode="copy"><span style="font-size:16px">复制</span></a>'+
		    '<a>&nbsp</a>'+
		     '<a style="cursor:pointer" id="TS_XMGL_edit'+pkCode+'"  rowpk="'+pkCode+'"  actcode="edit"><span style="font-size:16px">编辑</span></a>'+
		    '<a>&nbsp</a>'+
		     '<a  style="cursor:pointer" id="TS_XMGL_set'+pkCode+'"   rowpk="'+pkCode+'"  actcode="set"><span style="font-size:16px">设置</span></a>'+
		    '<a>&nbsp</a>'+
		     '<a style="cursor:pointer" id="TS_XMGL_delete'+pkCode+'"  rowpk="'+pkCode+'"  actcode="delete"><span style="font-size:16px">删除</span></a>'+
	         '</div>');
 	 dTag.style.display="block";
 	 dTag.style.position="fixed";  
 	 dTag.style.left= document.body.clientWidth-160-kk/2+"px"; 
 	 dTag.style.top=top+75+"px";
 	 p=[dTag.offsetLeft,dTag.offsetTop,dTag.clientWidth,dTag.clientHeight];
 	 var spanDiv= $(this).parent().get(0);
 	 spanLoc=[spanDiv.offsetLeft,spanDiv.offsetTop,spanDiv.clientWidth,spanDiv.clientHeight];
 	 d.focus();
	//查看
	$("#TS_XMGL_look"+pkCode).unbind("click").bind("click", function(){
		//var pkCode = jQuery(this).attr("rowpk");
	    _viewer._openCardView(UIConst.ACT_CARD_MODIFY,pkCode,"",true);
	});
//编辑
jQuery("#TS_XMGL_edit"+pkCode).unbind("click").bind("click", function(){
	//var pkCode = jQuery(this).attr("rowpk");
	rowEdit(pkCode,_viewer,[1000,500],[200,100]);
});
//复制
jQuery("#TS_XMGL_copy"+pkCode).unbind("click").bind("click", function(){
	var pkCode = jQuery(this).attr("rowpk");
       param = {};
        param["pkCodes"] = pkCode;
        FireFly.doAct(_viewer.servId, "copy", param);
       _viewer.refresh();
});
//设置
$("#TS_XMGL_set"+pkCode).unbind("click").bind("click", function(){
	var pkCode = jQuery(this).attr("rowpk");
    var ext =  " and XM_ID = '" + pkCode + "'";
    window.location.href =
    	"stdListView.jsp?frameId=TS_XMGL_SZ-tabFrame&sId=TS_XMGL_SZ&paramsFlag=false&title=项目管理设置&XM_ID="+pkCode+"&extWhere="+ext;
});
    //删除
jQuery("td [id='TS_XMGL_delete']").unbind("click").bind("click", function(){
	//var pkCode = jQuery(this).attr("rowpk");
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
			 console.log("b:"+p[0]+"   "+p[1]+"   "+p[2]+"   "+p[3]);
			 console.log(x+"   "+y);
		}else{
			dTag.style.display="none";
			console.log("d:"+p[0]+"   "+p[1]+"   "+p[2]+"   "+p[3]);
			 console.log(x+"   "+y);
			p=null;
		}
	}
});
	/**
	 * 目录管理
	 */
_viewer.getBtn("ctlgMgr").unbind("click").bind("click",function(event) {
		module = 'PROJECT';
		var params = {"isHide":"true", "CTLG_MODULE":module};
		var options = {"url":"TS_COMM_CATALOG.list.do?isHide=true&CTLG_MODULE="+module,"params":params,"menuFlag":3};
		Tab.open(options);

});
