var _viewer = this;

var SZ_SERV_ID = "TS_XMGL_SZ";
var SERV_ID = _viewer.servId;
var xmId = _viewer.params.XM_ID;//项目id
var xmSzId = _viewer.params.XM_SZ_ID;//项目设置id

// debugger;

//--------按钮事件设置----------//
//隐藏按钮
// _viewer.getBtn('add').hide();
_viewer.getBtn('batchSave').hide();
//返回按钮
_viewer.getBtn("back").unbind("click").bind("click", function () {
    var pkCode = xmId;
    var ext = " and XM_ID = '" + pkCode + "'";
    window.location.href = "stdListView.jsp?frameId=TS_XMGLSZ-tabFrame&sId=TS_XMGL_SZ&paramsFlag=false&title=项目管理设置&XM_ID=" + pkCode + "&extWhere=" + ext;
});
//导入试卷(temp)
var importPaper = function (array) {
    array.XM_SZ_ID = xmSzId;
    FireFly.doAct(_viewer.servId, "savePaperLink", array, true, false, function () {
        _viewer.refresh();
    });
};
_viewer.getBtn("importPaper").unbind("click").bind("click", function () {
    var configStr = 'TS_SJ,{"SOURCE":"SJ_CODE~SJ_NAME~SJ_ID","TARGET":"SJ_CODE~SJ_NAME~SJ_ID","EXTWHERE":" and 1=1","HIDE":"SJ_ID", "TYPE":"multi"}';
    var options = {
        "config": configStr,
        "parHandler": _viewer,
        // "formHandler":_viewer.form,
        "replaceCallBack": function (array/*, searchWhere, sArray, allSelectedDatas*/) {//回调，idArray为选中记录的相应字段的数组集合
            importPaper(array);
        }
    };
    var queryView = new rh.vi.rhSelectListView(options);
    queryView.show(/*event*/);

});
//导入试卷
const IMPORT_FILE_ID = "TS_XMGL_LIST-IMPORT_FILE";
//避免刷新数据重复添加
if (jQuery('#' + IMPORT_FILE_ID).length === 0) {
    var config = {
        "SERV_ID": _viewer.servId,
        "TEXT": "引入试卷",
        "FILE_CAT": "",
        "FILENUMBER": 1,
        "BTN_IMAGE": "btn-imp",
        // "VALUE": 15,
        "TYPES": "*.xls;*.xlsx;",
        "DESC": ""
    };
    var file = new rh.ui.File({
        "id": IMPORT_FILE_ID,
        "config": config
    });
    file._obj.insertBefore(jQuery('#' + _viewer.servId + '-delete'));
    $("#" + file.time + "-upload span:first").css('padding', '0 7px 2px 20px');
    jQuery('<span class="rh-icon-img btn-imp"></span>').appendTo($("#" + file.time + "-upload"));
    file.initUpload();
    file.afterQueueComplete = function (fileData) {// 这个上传队列完成之后
        console.log("这个上传队列完成之后" + fileData);
        for (var propertyName in fileData) {
            var fileId = fileData[propertyName].FILE_ID;
            if (fileId) {
                var data = {};
                data.XM_SZ_ID = xmSzId;
                data.FILE_ID = fileId;
                FireFly.doAct(_viewer.servId, "saveFromExcel", data, true, false, function () {
                    _viewer.refresh();
                });
            }
        }
        file.clear();
    };
}


//---------试卷状态设置------------//
//隐藏搜索栏
jQuery(".rhGrid-btnBar .searchDiv").hide();
var sjztDivId = 'TS_XMGL_SZ_SJ_SJZT_DIV';//试卷状态div id
var sjztSelectId = 'TS_XMGL_SZ_SJ_SJZT_SELECT';//试卷状态下拉框id
if (jQuery('#' + sjztDivId).length === 0) {
    //试卷状态设置div
    var sjztDivEl = jQuery('<div id="' + sjztDivId + '" style="float: right;"><span style="margin-right: 3px;">试卷状态</span></div>');
    //获取试卷状态的值
    var data = {'_PK_': xmSzId};
    var xmSzType = FireFly.doAct(SZ_SERV_ID, 'byid', data, false).XM_SZ_TYPE;
    //获取试卷状态字典数据
    var dictData = FireFly.getDict('TS_XMGL_SJ')[0].CHILD;
    //创建试卷状态下拉框
    var sjztSelect = new rh.ui.Select({
        id: sjztSelectId,
        cls: 'rhSearch-select',
        data: dictData,
        _default: xmSzType,
    });
    sjztSelect._obj.find('option:first').html("---" + Language.transStatic('rh_ui_ccexSearch_string3') + "---");
    // jQuery().appendTo(sel);
    sjztSelect.fillDefault();
    sjztSelect.change(function () {//试卷状态 变更保存事件
        var option = sjztSelect.getValue();
        var param = {};
        param["_PK_"] = xmSzId;
        param['XM_SZ_TYPE'] = option;
        FireFly.doAct(SZ_SERV_ID, "save", param, true);
    });
    //添加试卷状态下拉框到按钮栏
    sjztSelect._obj.appendTo(sjztDivEl);
    sjztDivEl.appendTo(jQuery('.rhGrid-btnBar'));
}

//----------操作栏 及 对应报名下拉框 设置--------//
//操作栏按钮
var _bldBtn = function (pkCode, actCode, actName, imgClass, func, obj) {
    return jQuery('<a class="rh-icon " id="' + pkCode + '-view" title="' + actName + '" style="padding: 0;width:13px;background:transparent;">' +
        '<span class="rh-icon-img btn-' + imgClass + '"></span>' +
        '</a>')
        .unbind("click")
        .bind("click", {"id": pkCode, "trObj": obj}, func);
};
//操作栏删除按钮事件
var delRow = function (trId) {
    return function () {
        var id = trId;
        var res = confirm(Language.transStatic("rhListView_string9"));
        if (res === true) {
            _viewer.listBarTipLoad(Language.transStatic("rhListView_string7"));
            setTimeout(function () {
                if (!_viewer.beforeDevare(id)) {
                    return false;
                }
                var strs = id;
                var temp = {};
                temp[UIvar.PK_KEY] = strs;
                var resultData = FireFly.listDevare(_viewer.opts.sId, temp, _viewer.getNowDom());
                _viewer._devarePageAllNum();
                _viewer.refreshGrid();
                _viewer.afterDevare();
            }, 0);
        }
    };
};
//操作栏查看按钮事件
var viewRow = function (trId) {
    return function () {
        _viewer._openCardView(UIConst.ACT_CARD_MODIFY, trId, "", true);
    };
};

//暂存向后端发送的请求数据（避开多个请求多次提示）
var saveActList = [];
//是否选择 之前选择过的下拉选项
var isSaveOptionValue = true;

//项目试卷设置 岗位类 序列 业务 等级  字段编码
var icodes = ['SJ_DYLB', 'SJ_DYXL', 'SJ_DYMK', 'SJ_DYJB'];
var icodeSelectMinWidth = [64, 104, 171, 51];
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

//获取级联下拉数据
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

//icode对应下拉框值变更，更新级联下拉框数据
function loadOptions(pk, icode) {
    var select = $('select[icode="' + icode + '"][ pk="' + pk + '"]');
    var option = select.find("option:selected").val();
    if (option) {
        icodeValues[pk][getIcodeIndex(icode)] = option;
    } else {
        // Tip.showError('该选项不能为空', true);
        // return false;
    }
    var dictData;
    switch (icode) {
        case icodes[0]:
            dictData = getDictData(" and KSLBK_NAME='" + option + "'", 'KSLBK_XL');
            changeOption(icodes[1], pk, dictData);
            loadOptions(pk, icodes[1]);
            break;
        case icodes[1]:
            dictData = getDictData(" and KSLBK_XL='" + option + "'", 'KSLBK_MK');
            changeOption(icodes[2], pk, dictData);
            loadOptions(pk, icodes[2]);
            break;
        // case icodes[2]:
        //     dictData = getDictData(" and KSLBK_MK='" + option + "'", 'KSLBK_TYPE');
        //     changeOption(icodes[3], pk, dictData);
        //     loadOptions(pk, icodes[3]);
        //     break;
    }
    var param = {};
    param["_PK_"] = pk;
    param[icode] = option;
    saveActList.push(param);
    // FireFly.doAct(_viewer.servId, "save", param, true);
}

/**
 * 变更下拉框数据并选中值
 * @param icode
 * @param pk
 * @param dictData [{ID:'id1' NAME:'nam1'}]
 * @param selectValue
 */
function changeOption(icode, pk, dictData, selectValue) {
    if (selectValue) {
    } else {
        //selectValue为空或null或undefined
        selectValue = isSaveOptionValue ? icodeValues[pk][getIcodeIndex(icode)] : '';
    }
    // selectValue = selectValue ? selectValue : '';
    var select = $('select[icode="' + icode + '"][ pk="' + pk + '"]');
    //移除原有的选项
    select.find('option').remove();
    var opt = [];
    opt.push("<option value=''></option>");
    jQuery.each(dictData, function (i, dictItem) {
        opt.push("<option value='");
        opt.push(dictItem.ID);
        opt.push("'");
        if (dictItem.ID === selectValue) {
            opt.push(" selected ");
        }
        opt.push(">");
        opt.push(dictItem.NAME);
        opt.push("</option>");
    });
    jQuery(opt.join("")).appendTo(select);
}

function getTrIcodeValues(pk) {
    var result = [];
    for (var i = 0; i < icodes.length; i++) {
        var icode = icodes[i];
        var select = $('select[icode="' + icode + '"][ pk="' + pk + '"]');
        result[i] = select.find("option:selected").val();
    }
    return result;
}

//表头th修改
const dybm = $('th[icode="' + icodes[0] + '"]');//__NAME
dybm.html('对应报名');
dybm.unbind('click');//去除对应报名(th)点击排序
var deleteThNum = 0;//删除的th数量
for (var i = 0; i < icodes.length; i++) {
    if (i !== 0) {
        var obj = icodes[i];
        _viewer.grid._tHead.find('th[icode="' + obj + '"]').remove();//__NAME
        deleteThNum++;
    }
}
debugger;
//修改无记录tr的colspan值
var rhGridTBody = _viewer.grid._table.find('.rhGrid-tbody');
var rhGridShowNO = rhGridTBody.find('.rhGrid-showNO');
rhGridShowNO.attr('colspan', rhGridShowNO.attr('colspan') - deleteThNum);

//遍历每行设置下拉框及操作栏
jQuery.each(_viewer.grid.getBodyTr(), function (i, tr) {
    //对应报名
    var td1 = jQuery(tr).find('td[icode="' + icodes[0] + '__NAME"]');
    for (var i = 0; i < icodes.length; i++) {
        var icode = icodes[i];
        if (i === 0) {
            var select = jQuery(tr).find('select[icode="' + icode + '__NAME"]');
            select.css('min-width', icodeSelectMinWidth[i] + 'px');
        } else {
            var findTd = jQuery(tr).find('td[icode="' + icode + '__NAME"]');
            jQuery(findTd.html()).css('margin-left', '8px').css('min-width', icodeSelectMinWidth[i] + 'px').appendTo(td1);
            findTd.remove();
        }
    }
    //操作栏
    var optTdObj = jQuery(tr).find('td[icode="OPERATION_S"]');
    _bldBtn(tr.id, "delRow", "查看", "view", viewRow(tr.id), tr).appendTo(optTdObj);
    _bldBtn(tr.id, "delRow", "删除", "delete", delRow(tr.id), tr).appendTo(optTdObj);

    icodeValues[tr.id] = getTrIcodeValues(tr.id);
    loadOptions(tr.id, icodes[0]);
});

//下拉框添加事件
for (var i = 0; i < icodes.length; i++) {
    var icode = icodes[i];
    var select = $('select[icode="' + icode + '"]');
    // select.find('option:first').remove();
    select.on("change", function () {
        saveActList = [];
        // isSaveOptionValue=false;//变更下拉框选项后 可选择不保留之前选择过项
        var icode = $(this).attr("icode");
        var pk = $(this).attr('pk');
        loadOptions(pk, icode);

        //下拉框变更 向后端发送请求
        for (var j = 0; j < saveActList.length; j++) {
            var param = saveActList[j];
            var tipFlag = false;
            if (j === saveActList.length - 1) {
                tipFlag = true;
            }
            FireFly.doAct(_viewer.servId, "save", param, tipFlag);
        }
    });
}