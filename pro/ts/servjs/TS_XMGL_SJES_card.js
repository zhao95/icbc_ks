var _viewer = this;
$("#TS_XMGL_SJES-save").css("right",200);
$("#TS_XMGL_SJES-winDialog").find(".ui-form-default,.item").css("padding","0 0 0 0");
$(".form-container").hide();
var hdtitle = $("<div>").text("试     卷");
hdtitle.css({"width":"90%","font-family":"华文中宋,宋体","color":"black","font-size":"25px","text-align":"center","vertical-align":"text-top"});
$(".rhCard-btnBar").append(hdtitle).css({"padding":"0px 0px 2px 8px"});
var  xmglId=_viewer.getItem("XM_ID").getValue();
var   pk=_viewer.getItem("XM_SZ_ID").getValue();
//var conf={};
//var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_BMGL","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,40]};
FireFly.doAct("TS_XMGL","finds",{"_WHERE_":" and XM_ID='"+xmglId+"'"},true,false,function(data){
	var datas = data._DATA_[0].XM_TYPE;
	if(datas=="资格类考试"){
		$("li.rhCard-tabs-bottomLi[sid='TS_XMGL_FZGSJ']").hide();
//创建弹出框
//var popPrompt = new rh.ui.popPrompt({title:'项目设置试卷'});
//popPrompt._layout(event, [100,50], [width,height]);
//var dialogObj = jQuery("#" + popPrompt.dialogId);
//jQuery(".ui-dialog-buttonpane button",dialogObj.parent()).css("display","none");//去掉确定关闭按钮
//getServListDialog(event,"sj_manager","考试试卷",width,height,[100,50]);
//getServListDialog(event,"sj_manager","考试试卷",width,height,[100,50]);		
/**const ext =  "and XM_ID= '"+xmglId+"'";
  var params={XM_ID:xmglId,XM_SZ_ID:pk};
   var conf = {
		"sId":"TS_XMGL_ZGSJ",
	    //"pCon":jQuery("#sj_manager"),
//        "resetHeiWid":_viewer._resetHeiWid,
        "parHandler":_viewer,
//        "showSearchFlag":"true",
//        "showTitleBarFlag":"false",
//        "listSonTabFlag":false,
//        "readOnly":"false",
        "params":params,
        "linkWhere":ext
    };
   var listView = new rh.vi.listView(conf);
   listView.show();*/

	}else if(datas=="其他类考试"){
		$("li.rhCard-tabs-bottomLi[sid='TS_XMGL_ZGSJ']").hide();
		//getServListDialog(event,"sj_manager","考试试卷",width,height,[100,50]);
		/*const ext =  "and XM_ID= '"+xmglId+"'";
		var params={XM_ID:xmglId,XM_SZ_ID:pk};
		var conf = {
				"sId":"TS_XMGL_FZGSJ",
			   // "pCon":jQuery("#sj_manager"),
		        "parHandler":_viewer,
		        "params":params,
		        "linkWhere":ext
		    };
	    var listView = new rh.vi.listView(conf);
	    listView.show();*/
	}
});	 