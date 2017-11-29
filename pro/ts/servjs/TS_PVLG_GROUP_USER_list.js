var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
//每一行添加编辑和删除
$("#TS_PVLG_GROUP_USER .rhGrid").find("tr").each(function (index, item) {
    if (index != 0) {
        var dataId = item.id;

        $(item).find("td[icode='BUTTONS']").append(
            //'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP_USER-upd" actcode="upd" rowpk="'+dataId+'"><span class="rh-icon-inner-notext">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
            //'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP_USER-upd" actcode="upd" rowpk="'+dataId+'"><span class="rh-icon-inner-notext">编辑</span><span class="rh-icon-img btn-edit"></span></a>'+
            '<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP_USER-delete" actcode="delete" rowpk="' + dataId + '"><span class="rh-icon-inner">删除</span><span class="rh-icon-img btn-delete"></span></a>'
            //'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_PVLG_GROUP_USER-delete" actcode="delete" rowpk="'+dataId+'"><span class="rh-icon-inner-notext">删除</span><span class="rh-icon-img btn-delete"></span></a>'
        );
        // 为每个按钮绑定卡片
        bindCard();
    }
});

//绑定的事件     
function bindCard() {
    //当行删除事件
    jQuery("td [id='TS_PVLG_GROUP_USER-delete']").unbind("click").bind("click", function () {
        var pkCode = jQuery(this).attr("rowpk");
        rowDelete(pkCode, _viewer);
    });

    //当行编辑事件
    /*jQuery("td [id='TS_PVLG_GROUP_USER-upd']").unbind("click").bind("click", function() {

     var pkCode = jQuery(this).attr("rowpk");
     var height = jQuery(window).height()-100;
     var width = jQuery(window).width()-200;
     rowEdit(pkCode,_viewer,[width,height],[100,50]);
     });
     */
}

_viewer.getBtn("impUser").unbind("click").bind("click", function (event) {
    var configStr = "TS_ORG_USER_ALL,{'TARGET':'USER_CODE~USER_NAME~USER_LOGIN_NAME','SOURCE':'USER_CODE~USER_NAME~USER_LOGIN_NAME'," +
        "'HIDE':'','TYPE':'multi','HTMLITEM':''}";
    var options = {
        "config": configStr,
//			"params" : {"_TABLE_":"SY_ORG_USER"},
        "parHandler": _viewer,
        "formHandler": _viewer.form,
        "replaceCallBack": function (idArray) {//回调，idArray为选中记录的相应字段的数组集合
            var codes = idArray.USER_CODE.split(",");
            var names = idArray.USER_NAME.split(",");

            var paramArray = [];

            for (var i = 0; i < codes.length; i++) {
                var param = {};
                //群组ID
                param.G_ID = _viewer.getParHandler()._pkCode;
                //用户编码
                param.USER_CODE = codes[i];
                //用户名称
                param.USER_NAME = names[i];
                //选取类型 1人员
                param.GU_TYPE = 1;

                paramArray.push(param);
            }
            var batchData = {};
            batchData.BATCHDATAS = paramArray;
            //批量保存
            var rtn = FireFly.batchSave(_viewer.servId, batchData, null, 2, false);

            _viewer.refresh();
        }
    };
    //2.用系统的查询选择组件 rh.vi.rhSelectListView()
    var queryView = new rh.vi.rhSelectListView(options);
    queryView.show(event);
});


//从excel中导入人员
const IMPORT_FILE_ID = "TS_PVLG_GROUP_USER-impUserByExcel";
//避免刷新数据重复添加
if (jQuery('#' + IMPORT_FILE_ID).length === 0) {
    var config = {
        "SERV_ID": _viewer.servId,
        "TEXT": "导入用户",
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
                // data.XM_SZ_ID = xmSzId;
                data.G_ID = _viewer.getParHandler()._pkCode;
                data.FILE_ID = fileId;
                FireFly.doAct(_viewer.servId, "saveFromExcel", data, false, false, function (data) {
                    _viewer.refresh();
                    alert(data._MSG_);
                });
            }
        }
        file.clear();
    };
}
$('#'+IMPORT_FILE_ID).attr('title','导入文件为excel格式文件，内容为无标题的单列数据，一行数据为一个用户，数据为人力资源编码、统一认证号、身份证号');