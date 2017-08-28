const _viewer = this;

const _bldBtn = function (pkCode, actCode, actName, imgClass, func, obj) {
    return jQuery('<a class="rh-icon " id="' + pkCode + '-view" title="' + actName + '" style="padding: 0;width:13px;background:transparent;">' +
        '<span class="rh-icon-img btn-' + imgClass + '"></span>' +
        '</a>')
        .unbind("click")
        .bind("click", {"id": pkCode, "trObj": obj}, func);
};

//操作栏-删除按鈕事件
const delRow = function (trId) {
    return function () {
        const id = trId;
        const res = confirm(Language.transStatic("rhListView_string9"));
        if (res === true) {
//			    		_viewer.listBarTipLoad("提交中...");
            _viewer.listBarTipLoad(Language.transStatic("rhListView_string7"));
            setTimeout(function () {
                if (!_viewer.beforeDelete(id)) {
                    return false;
                }
                const strs = id;
                const temp = {};
                temp[UIConst.PK_KEY] = strs;
                const resultData = FireFly.listDelete(_viewer.opts.sId, temp, _viewer.getNowDom());
                _viewer._deletePageAllNum();
                _viewer.refreshGrid();
                _viewer.afterDelete();
            }, 0);
        }
    };
};

//操作栏-查看按鈕事件
const viewRow = function (trId) {
    return function () {
        _viewer._openCardView(UIConst.ACT_CARD_MODIFY, trId, "", true);
    };
};

jQuery.each(_viewer.grid.getBodyTr(), function (i, n) {
    //添加 查看、删除按钮
    const optTdObj = jQuery(n).find('td[icode="OPERATION_S"]');
    _bldBtn(n.id, "delRow", "查看", "view", viewRow(n.id), n).appendTo(optTdObj);
    _bldBtn(n.id, "delRow", "删除", "delete", delRow(n.id), n).appendTo(optTdObj);
});