/** 服务卡片使用的js方法定义：开始fromTable */
var _viewer = this;
var module = 'PROJECT';
var res = _viewer.grid.getBtn("look");
res.unbind("click").bind("click",function() {
var pk = jQuery(this).attr("rowpk");//获取主键信息
//var title = _viewer.grid.getRowItemValue(pk,"KS_TITLE");
//var content = _viewer.grid.getRowItemValue(pk,"KS_NEIRONG");

 window.open('/qt/jsp/gg.jsp?id='+pk, 'newwindow', 'height=100, width=400, top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes, location=yes, status=yes')
	
 return false;
});

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