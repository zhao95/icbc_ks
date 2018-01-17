/** 服务卡片使用的js方法定义：开始fromTable */
var _viewer = this;
var height = jQuery(window).height()-200;
var width = jQuery(window).width()-200;
//取消行点击事件
$(".rhGrid").find("tr").unbind("dblclick");
var module = 'PROJECT';

////每一行添加编辑和删除
$("#TS_GG .rhGrid").find("tr").each(function (index, item) {
    if (index != 0) {
        var dataId = item.id;
        if(index == 1){
        	 $(item).find("td[icode='BUTTONS']").append(
        	            '<a class="rhGrid-td-rowBtnObj rh-icon"  id="TS_GG_look" operCode="optLookBtn" rowpk="' + dataId + '"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>' +
        	            '<a class="rh-icon rhGrid-btnBar-a" id="TS_GG_edit" operCode="optEditBtn"   rowpk="' + dataId + '"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
        	            '<a class="rh-icon rhGrid-btnBar-a" id="TS_GG_first" operCode="optFirstBtn"   rowpk="' + dataId + '"><span class="rh-icon-inner">置顶</span><span class="rh-icon-img btn-edit"></span></a>'+
        	            '<a class="rh-icon rhGrid-btnBar-a" id="TS_GG_delete" operCode="optFirstBtn"   rowpk="' + dataId + '"><span class="rh-icon-inner">置后</span><span class="rh-icon-img btn-edit"></span></a>'
        	           
        	        );
        }else{
        	 $(item).find("td[icode='BUTTONS']").append(
     	            '<a class="rhGrid-td-rowBtnObj rh-icon"  id="TS_GG_look" operCode="optLookBtn" rowpk="' + dataId + '"><span class="rh-icon-inner">查看</span><span class="rh-icon-img btn-edit"></span></a>' +
     	            '<a class="rh-icon rhGrid-btnBar-a" id="TS_GG_edit" operCode="optEditBtn"   rowpk="' + dataId + '"><span class="rh-icon-inner">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
     	            '<a class="rh-icon rhGrid-btnBar-a" id="TS_GG_first" operCode="optFirstBtn"   rowpk="' + dataId + '"><span class="rh-icon-inner">置顶</span><span class="rh-icon-img btn-edit"></span></a>'
     	            );
        }
        
       
        // 为每个按钮绑定卡片
        bindCard();
    }
});

function  bindCard(){
		jQuery("td [id='TS_GG_look']").unbind("click").bind("click", function () {
			var pk = jQuery(this).attr("rowpk");
			 window.open('/qt/jsp/gg.jsp?id='+pk, 'newwindow', 'height=100, width=400, top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes, location=yes, status=yes')
			 return false;
	});
		jQuery("td [id='TS_GG_edit']").unbind("click").bind("click", function () {
			var pkCode = jQuery(this).attr("rowpk");
	 		$(".hoverDiv").css('display','none');
	 		openMyCard(pkCode);
			});
		
		jQuery("td [id='TS_GG_first']").unbind("click").bind("click", function () {	
			var pkCode = jQuery(this).attr("rowpk");
			var paramfb = {};
			paramfb["GG_ID"] = pkCode;
			 FireFly.doAct("TS_GG", "setValue", paramfb);
			 _viewer.refresh();
		});
		jQuery("td [id='TS_GG_delete']").unbind("click").bind("click", function () {	
			var pkCode = jQuery(this).attr("rowpk");
			var paramfb = {};
			paramfb["GG_ID"] = pkCode;
			 FireFly.doAct("TS_GG", "getValue", paramfb);
			 _viewer.refresh();
		});
		
		
}

//列表操作按钮 弹dialogfresh
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



//var res = _viewer.grid.getBtn("look");
//res.unbind("click").bind("click",function() {
//var pk = jQuery(this).attr("rowpk");//获取主键信息
////var title = _viewer.grid.getRowItemValue(pk,"KS_TITLE");
////var content = _viewer.grid.getRowItemValue(pk,"KS_NEIRONG");
//
// window.open('/qt/jsp/gg.jsp?id='+pk, 'newwindow', 'height=100, width=400, top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes, location=yes, status=yes')
//	
// return false;
//});

//传给后台的数据
/*
 * 业务可覆盖此方法，在导航树的点击事件加载前
 */
rh.vi.listView.prototype.beforeTreeNodeClickLoad = function(item,id,dictId) {
    var params = {};
    var user_pvlg=_viewer._userPvlg[_viewer.servId+"_PVLG"];
    params["USER_PVLG"] = user_pvlg;
    _viewer.whereData["extParams"] = params;
    var flag = getListPvlg(item,user_pvlg,"CODE_PATH");
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

    var width = jQuery(window).width()-200;
    var height = jQuery(window).height()-200;

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