var _viewer = this;

var xmId = _viewer.getParHandler()._pkCode;

//自动分配规则
_viewer.getBtn("zdfpcc").unbind("click").bind("click", function(event) {
	var datas = FireFly.doAct("TS_XMGL_CONFIG","finds", {"_WHERE_":"and XM_ID = '"+xmId+"'"})._DATA_;
	if(datas.length > 0){
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_CONFIG","parHandler":_viewer,"widHeiArray":[700,500],"xyArray":[200,100]};
	    temp[UIConst.PK_KEY] = datas[0].CONF_ID;//修改时，必填
	    var cardView = new rh.vi.cardView(temp);
	    cardView.show();
	}else{
		//打开添加页面act：方法（必填），sId：服务（必填），parHandler：当前句柄，widHeiArray:小卡片的宽度高度，xyArray：左上角坐标
	    var temp = {"act":UIConst.ACT_CARD_ADD,"sId":"TS_XMGL_CONFIG","parHandler":_viewer,"widHeiArray":[700,500],"xyArray":[200,100],"xmId":xmId};
	    var cardView = new rh.vi.cardView(temp);
	    cardView.show();
	}
});

