var _viewer = this;

//岗位类 序列 业务 等级  字段编码
var icodes = ['REF_DYLB', 'REF_DYXL', 'REF_DYMK'];
var icodeValues = [];
//获取icode在icodes的下标
function getIcodeIndex(icode) {
    var result = -1;
    for (var i = 0; i < icodes.length; i++) {
        var obj = icodes[i];
        if (obj === icode) {
            result = i;
        }
    }
    return result;
}

//获取级联下拉框数据
function getDictData(linkWhere, codeName) {
    var data = {
        "_SELECT_": codeName,
        "_ORDER_": codeName,
        "_AFTER_SELECT_KEYWORDS": "distinct",
        "_linkWhere": linkWhere + " and " + codeName + " is not null ",
        "_NOPAGE_": "true"
    };
    var result = FireFly.getListData("TS_XMGL_BM_KSLBK", data, false);
    var dictData = [];
    for (var i = 0; i < result._DATA_.length; i++) {
        var dict = result._DATA_[i];
        dictData.push({ID: dict[codeName], NAME: dict[codeName]});
    }
    return dictData;
}

/**
 * 变更下拉框数据并选中值
 * @param icode 字段编码
 * @param dictData 下拉数据
 */
function changeOption(icode, dictData) {
    _viewer.getItem(icode).removeOptions();
    _viewer.getItem(icode).addOptions(dictData);
    //如果有初始选中的岗位序列选中
    if (icodeValues[getIcodeIndex(icode)]) {
        _viewer.getItem(icode).select(icodeValues[getIcodeIndex(icode)]);
    }
}

/**
 * icode对应下拉框值变更，更新级联下拉框数据
 * @param icode
 */
function loadOptions(icode) {
    var option = _viewer.getItem(icode).getValue();
    if (option) {
        icodeValues[getIcodeIndex(icode)] = option;
    }
    //向后端发送请求获取 岗位类下的所有序列
    var dictData = [];
    switch (icode) {
        case icodes[0]:
            dictData = getDictData(" and KSLBK_NAME='" + option + "'", 'KSLBK_XL');
            changeOption(icodes[1], dictData);
            loadOptions(icodes[getIcodeIndex(icodes[1])]);
            break;
        case icodes[1]:
            dictData = getDictData(" and KSLBK_XL='" + option + "'", 'KSLBK_MK');
            changeOption(icodes[2], dictData);
            loadOptions(icodes[getIcodeIndex(icodes[2])]);
            break;
    }
}

//下拉框添加监听事件
for (var i = 0; i < icodes.length; i++) {
    var icode = icodes[i];
    icodeValues[i] = _viewer.getItem(icode).getValue();
    _viewer.getItem(icode).change(function () {
        var icode = $(this)[0]._obj[0].id.split('-')[1];
        loadOptions(icode);
    });
}

//进入页面 更新下拉框选项
loadOptions(icodes[0]);