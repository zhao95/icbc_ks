var _viewer = this;
//每列添加操作按钮
$(".rhGrid").find("th[icode='set']").html("操作");
$(".rhGrid").find("tr").unbind("dblclick");
//隐藏多选框
//$(".rhGrid-thead-box").hide();
//$(".checkTD").hide();
//$(".rowIndex").hide();
//$(".rhGrid-thead-checkbox").hide();
//$(".rhGrid-thead-orderSpan").hide();
//$(".rhGrid-thead-th").unbind("click");
//列表后增加功能
_viewer.grid.getBtn("set").unbind("click").bind("click",function() {
	var pk = jQuery(this).attr("rowpk");//获取主键信息
	var xmglId = jQuery(this).parent().parent().find("td[icode='XM_ID']").html();
	var name = jQuery(this).parent().parent().find("td[icode='XM_SZ_NAME']").html();
	
	switch(name)
	{
	    case '报名':
	           bm(pk,xmglId);
	           break;
	    case '审核':
	    	   sh(pk,xmglId);
	           break;
	    case  '请假':   
	    	   qj(pk,xmglId);
	    	   break;
	    case   '异地借考':
	    	   jk(pk,xmglId);
	    	   break;
	    case  '试卷':
            	sj(pk,xmglId);
	    	   break;
	    case  '场次测算':
	    	   cs(xmglId);
	    	   break;
	    case  '考场安排':
	    	   ap(xmglId);
	    	   break;
	    default:;
	}
});
//报名
function bm(pk,xmglId){
	var dataId = "";
	FireFly.doAct("TS_XMGL_BMGL","finds",{"_WHERE_":" and XM_SZ_ID='"+pk+"'"},true,false,function(data){
		if(data._DATA_.length > 0){
			dataId = data._DATA_[0].BM_ID;
		}
	});
	var height = jQuery(window).height()-95;
	var width = jQuery(window).width()-200;
	var temp = {};
	if(dataId == ""){
		var temp = {"act":UIConst.ACT_CARD_ADD,"sId":"TS_XMGL_BMGL","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,40]};
	}else{
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_BMGL","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,40]};
	    temp[UIConst.PK_KEY] = dataId;//修改时，必填
	}
	temp["XM_SZ_ID"] = pk;
	temp["XM_ID"] =xmglId;
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
}
//审核
function sh(pk,xmglId){
	var dataId = "";
	FireFly.doAct("TS_XMGL_BMSH","finds",{"_WHERE_":" and XM_SZ_ID='"+pk+"' and XM_ID ='"+xmglId+"'"},true,false,function(data){
		if(data._DATA_.length > 0){
			dataId = data._DATA_[0].SH_ID;
		}
	});
	var height = jQuery(window).height()-95;
	var width = jQuery(window).width()-200;
	var temp = {};
	if(dataId == ""){
		var temp = {"act":UIConst.ACT_CARD_ADD,"sId":"TS_XMGL_BMSH","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,40]};
	}else{
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_BMSH","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,40]};
	    temp[UIConst.PK_KEY] = dataId;//修改时，必填
	}
	temp["XM_SZ_ID"] = pk;
	temp["XM_ID"] = xmglId;

    var cardView = new rh.vi.cardView(temp);
    cardView.show();
}
//请假
function  qj(pk,xmglId){
	var dataId = "";
	FireFly.doAct("TS_XMGL_QJGL","finds",{"_WHERE_":" and XM_SZ_ID='"+pk+"'"},true,false,function(data){
		if(data._DATA_.length > 0){
			dataId = data._DATA_[0].QJ_ID;
		}
	}); 
	var height = jQuery(window).height()-100;
	var width = jQuery(window).width()-200;
	var temp = {};
	if(dataId == ""){
		var temp = {"act":UIConst.ACT_CARD_ADD,"sId":"TS_XMGL_QJGL","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
	}else{
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_QJGL","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
	    temp[UIConst.PK_KEY] = dataId;//修改时，必填
	}
	temp["XM_SZ_ID"] = pk;
	temp["XM_ID"] = xmglId;
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
}
//异地借考
function  jk(pk,xmglId){
	var dataId = "";
	FireFly.doAct("TS_XMGL_YDJK","finds",{"_WHERE_":" and XM_SZ_ID='"+pk+"'"},true,false,function(data){
		if(data._DATA_.length > 0){
			dataId = data._DATA_[0].YDJK_ID;
		}
	});
	var height = jQuery(window).height()-200;
	var width = jQuery(window).width()-200;
	var temp = {};
	if(dataId == ""){
		var temp = {"act":UIConst.ACT_CARD_ADD,"sId":"TS_XMGL_YDJK","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
	}else{
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_YDJK","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
	    temp[UIConst.PK_KEY] = dataId;//修改时，必填
	}
	temp["XM_SZ_ID"] = pk;
	temp["XM_ID"] = xmglId;
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
}

//试卷
function sj(pk,xmglId){
	var height = jQuery(window).height()-80;
	var width = jQuery(window).width()-200;
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_SJES","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
	temp[UIConst.PK_KEY] = pk;//修改时，必填
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
	}
	

//	var temp = {};
//	if(dataId == ""){
//		var temp = {"act":UIConst.ACT_CARD_ADD,"sId":"TS_XMGL_SJ","parHandler":_viewer,"widHeiArray":[950,700],"xyArray":[200,100]};
//	}else{
//		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_SJ","parHandler":_viewer,"widHeiArray":[950,700],"xyArray":[200,100]};
//	    temp[UIConst.PK_KEY] = dataId;//修改时，必填
//	}
//	temp["XM_SZ_ID"] = pk;
//    var listView = new rh.vi.listView(temp);
//    listView.show();
//}
//场次测算
function cs(pk){
	var height = jQuery(window).height()-80;
	var width = jQuery(window).width()-200;
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_CCCS","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
	temp[UIConst.PK_KEY] = pk;//修改时，必填
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
}
//考场安排
function ap(pk){
	var height = jQuery(window).height()-80;
	var width = jQuery(window).width()-200;
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_KCAP","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
	temp[UIConst.PK_KEY] = pk;//修改时，必填
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
}
////返回按钮
//_viewer.getBtn("goback").unbind("click").bind("click", function() {
//	 window.location.href ="stdListView.jsp?frameId=TS_XMGL-tabFrame&sId=TS_XMGL&paramsFlag=false&title=项目管理";
//});
//通过名称绑定字典

_viewer.grid._table.find("tr").each(function(index, item) {
	var   XM_SZ_NAME = $('td[icode="XM_SZ_NAME"]',item).text();
	 var  XM_SZ_ID = $('td[icode="XM_SZ_ID"]',item).text();
	 var  XM_SZ_TYPE =_viewer.grid.getRowItemValue(XM_SZ_ID,"XM_SZ_TYPE");
		if(index>0 && XM_SZ_ID!=''){
		 var dictCode="";
		 if(XM_SZ_NAME=="报名"){
			 dictCode="TS_XMGL_BM";//对应的字典
		 }else  if(XM_SZ_NAME=="审核"){
			 dictCode="TS_XMGL_SH";
		 }else  if(XM_SZ_NAME=="请假"){
			 dictCode="TS_XMGL_QJ";
		 }else  if(XM_SZ_NAME=="异地借考"){
			 dictCode="TS_XMGL_JK";
		 }else  if(XM_SZ_NAME=="试卷"){
			 dictCode="TS_XMGL_SJ";
		 }else  if(XM_SZ_NAME=="场次测算"){
			 dictCode="TS_XMGL_CC";
		 }else if(XM_SZ_NAME=="考场安排"){
			 dictCode="TS_XMGL_KC";
		 }
		 if(dictCode!=''){
			var temp = FireFly.getDict(dictCode);
			if(temp  && temp.length > 0){
				//默认取第一个
				temp = temp[0].CHILD;
			}
			//var td = $(item).find("td").eq(4);
			var sel = jQuery("<select></select>").addClass("rh-advSearch-val")
				.width(200).css({"border":"1px #bebebe solid","height":"25px","line-height":"25px","padding":"3px 0"});
			//sel.data("itemcode",code);
			//sel.data("itemtype",UIConst.FITEM_ELEMENT_SELECT);
		//	jQuery("<option value=''>---全部---</option>").appendTo(sel);
			//jQuery("<option value=''>---"+Language.transStatic('rh_ui_ccexSearch_string3')+"---</option>").appendTo(sel);
			//jQuery("<option value=''></option>").appendTo(sel);
			jQuery.each(temp,function(i,n) {
				var id = n.ID;
				var name = n.NAME;
				jQuery("<option value='" + id + "'>" + name + "</option>").appendTo(sel);
			});
			//sel.appendTo(tdInp);
			sel.val(XM_SZ_TYPE);
			$(item).find("td").eq(4).html("");
			$(item).find("td").eq(4).html(sel);//找到状态那一列加进去
		}
	}
});
var oldStyle="";
var  name="";
$(".rh-advSearch-val").unbind("click").bind("click",function() {
//_viewer.grid.unbind("click").bind("click",function() {
oldStyle=$(this).find("option:selected").html();
name =$(this).parent().parent().find("td[icode='XM_SZ_NAME']").html();
});
//获取状态值放入数据库对应的位置
$(".rh-advSearch-val").on("change", function() {
  	var options=$(this).find("option:selected").val();//改变后的状态
		if(!confirm(name+"设置确定要改成'"+options+"'状态吗？")){
		options=oldStyle;
		_viewer.refresh();
		return  false;
	}
	var sz_id=$(this).parent().parent().find("td[icode='XM_SZ_ID']").html();
	var xm_id=$(this).parent().parent().find("td[icode='XM_ID']").html();
	var param={};
	param["_PK_"]=sz_id;
	param["XM_SZ_TYPE"]=options;
	FireFly.doAct(_viewer.servId,"save",param,true);
	
});
 



