var _viewer = this;

//传给后台的数据
/*
 * 业务可覆盖此方法，在导航树的点击事件加载前
 */
rh.vi.listView.prototype.beforeTreeNodeClickLoad = function(item,id,dictId) {
    var params = {};
    var user_pvlg=_viewer._userPvlg[_viewer.servId+"_PVLG"];
    params["USER_PVLG"] = user_pvlg;
    _viewer.whereData["extParams"] = params;
    var flag = getListPvlg(item,user_pvlg,'CODE_PATH');
    _viewer.listClearTipLoad();
    return flag;
};

_viewer.getBtn("add").remove();
_viewer.getBtn("delete").remove();
_viewer.getBtn("impZip").remove();
_viewer.getBtn("expZip").remove();

/*//重写add方法
var height = jQuery(window).height()-50;
var width = jQuery(window).width()-100;

_viewer.getBtn("add").unbind("click").bind("click",function() {
    debugger;
    var pcodeh = _viewer._transferData["DEPT_PCODE"];
    if(pcodeh === "" || typeof(pcodeh) === "undefined") {
        alert("请选择添加机构层级 !");
        return false;
    }

    var temp = {"act":UIConst.ACT_CARD_ADD,
        "sId":_viewer.servId,
        // "params":  {
        //     "CTLG_MODULE" : module,
        // },
        "transferData": _viewer._transferData,
        "links":_viewer.links,
        "parHandler":_viewer,
        "widHeiArray":[width,height],
        "xyArray":[50,50]
    };
    console.log(temp);
    var cardView = new rh.vi.cardView(temp);

    cardView.show();

});*/
