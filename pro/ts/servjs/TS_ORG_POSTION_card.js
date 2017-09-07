var _viewer = this;

//---------处理岗位序列字段-------//
//获取初始岗位序列
var selectedPostionSequence = _viewer.getItem("POSTION_SEQUENCE").getValue();
//根据岗位类加载序列选项 并选中初始岗位序列
function loadPostionSequenceOption() {
    var postionType = _viewer.getItem("POSTION_TYPE_NAME").getValue();
    if (postionType) {
        //向后端发送请求获取 岗位类下的所有序列
        var data = {
            "_SELECT_": "KSLBK_XL,KSLBK_CODE",
            "_ORDER_": "KSLBK_XL",
            "_AFTER_SELECT_KEYWORDS": "distinct",
            "_linkWhere": " and KSLBK_NAME='" + postionType + "' and KSLBK_XL is not null ",
            "_NOPAGE_": "true"
        };
        var result = FireFly.getListData("TS_XMGL_BM_KSLBK", data, false);
        var sequenceList = [];
        for (var i = 0; i < result._DATA_.length; i++) {
            var sequence = result._DATA_[i];
            sequenceList.push({ID: sequence.KSLBK_XL, NAME: sequence.KSLBK_XL});
        }
        _viewer.getItem("POSTION_SEQUENCE").removeOptions();
        _viewer.getItem("POSTION_SEQUENCE").addOptions(sequenceList);
        //如果有初始选中的岗位序列选中
        if (selectedPostionSequence) {
            _viewer.getItem("POSTION_SEQUENCE").select(selectedPostionSequence);
        }
        $("#TS_ORG_POSTION-POSTION_SEQUENCE_ID").val("");
        $("#TS_ORG_POSTION-POSTION_TYPE").val(result._DATA_[0].KSLBK_CODE);
    }
}
//岗位序列ID
function loadPostionSequenceID() {
    var postionSEQUENCE = _viewer.getItem("POSTION_SEQUENCE").getValue();
    if (postionSEQUENCE) {
        //向后端发送请求获取 岗位类下的所有序列
        var data = {
            "_SELECT_": "KSLBK_XL_CODE",
            "_ORDER_": "KSLBK_XL",
            "_AFTER_SELECT_KEYWORDS": "distinct",
            "_linkWhere": " and KSLBK_XL='" + postionSEQUENCE + "' and KSLBK_XL is not null ",
            "_NOPAGE_": "true"
        };
        var result = FireFly.getListData("TS_XMGL_BM_KSLBK", data, false);
            var ID = result._DATA_[0].KSLBK_XL_CODE;
            $("#TS_ORG_POSTION-POSTION_SEQUENCE_ID").val(ID);
    }
}
//岗位类变更事件
_viewer.getItem("POSTION_TYPE_NAME").change(function () {
    //变更岗位类后，不保存初始的岗位序列
    selectedPostionSequence = "";
    loadPostionSequenceOption();
});
//岗位序列改变,序列CODE也随改变
_viewer.getItem("POSTION_SEQUENCE").change(function () {
	loadPostionSequenceID();
});
//移除岗位序列所有选项
_viewer.getItem("POSTION_SEQUENCE").removeOptions();
//加载完页面后加载岗位序列选项
loadPostionSequenceOption();