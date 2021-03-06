/** 服务卡片使用的js方法定义：开始fromTable */
var _viewer = this;
//增加全选按钮
//<label>报名</label>
var $allSelect = jQuery('<input type="checkbox" style="position:relative;left :2px;top:4px" value="全选" name="ts_xmgl-xm_gj_all"  id="ts_xmgl-xm_gj_all_id"><label  style="position :relative;left :2px;"  for="ts_xmgl-xm_gj_all_id">全选</label>');
//点击'<div position="absolute"><input type="checkbox" style="position:relative;top:4px" value="全选" name="ts_xmgl-xm_gj_all" ><label>全选</label></div>'
//$allSelect.unbind('click').bind('click',function(){checked="checked"
//	var xmgjCheckBoxs=$('input[name="TS_XMGL-XM_GJ"]');
//	for(int i=0;){
//		xmgjCheckBoxs[0].batchSave
//	}
//});prependTo
$allSelect.insertBefore($("#TS_XMGL-XM_GJ"));

//th checkbox 全选/全不选 事件
$($allSelect).unbind('change').bind('change', function () {
    var $tdCheckboxs = $('input[name="TS_XMGL-XM_GJ"]');
    for (var i = 0; i < $tdCheckboxs.length; i++) {
        var tdCheckbox = $tdCheckboxs[i];
        tdCheckbox.checked = this.checked;
    }
});

//td checkbox td中checkbox变更，改变th checkbox
//var tdCheckboxs = $('input[name="TS_XMGL-XM_GJ"]');
//tdCheckboxs.unbind('change').bind('change', function () {debugger;
//    if ($allSelect.checked && !this.checked) {
//    	$allSelect.checked = false;
//    } else {
//        var allChecked = true;
//        for (var i = 0; i < tdCheckboxs.length; i++) {
//            var tdCheckbox = tdCheckboxs[i];
//            if (!tdCheckbox.checked) {
//                allChecked = false;
//            }
//        }
//        $allSelect.checked = allChecked;
//    }
//});


//针对项目开始时间的校验与互斥资格类考试其他类考试JH_TYPE
_viewer.beforeSave = function () {
    var xmType = _viewer.getItem("XM_TYPE").getValue();//获取项目考试类型
    var xmAssociateJhId = _viewer.getItem("JH_ID").getValue();//获取项目关联计划ID
    //选择考试计划，校验类别
    if(xmAssociateJhId!=""){
        var jhParam={};
        jhParam["_extWhere"]="AND JH_ID ='"+xmAssociateJhId+"' AND JH_STATUS='2'";
        var jhResultObj = FireFly.doAct("TS_JHGL_XX", "query", jhParam);
        //如果不选择考试计划，不做校验
        if(_viewer.getItem("XM_TITLE").getValue()=="" || _viewer.getItem("XM_TITLE").getValue()==undefined){

        }else if(jhResultObj._DATA_[0].JH_TYPE_NAME != xmType){
            $("#TS_XMGL-XM_TYPE").parent().showError("考试类型应与所选考试计划类型一致！");
            return false;
        }
    }
	var xmType = _viewer.getItem("XM_TYPE").getValue();//
    var xmStart = _viewer.getItem("XM_START").getValue();//项目开始时间
    var xmEnd = _viewer.getItem("XM_END").getValue();//项目截至时间
    var xmKsStartData = _viewer.getItem("XM_KSSTARTDATA").getValue();//考试开始时间
    var xmKsEndData = _viewer.getItem("XM_KSENDDATA").getValue();//考试截至时间
    var xmStarts = xmStart.split('-');
    var xmEnds = xmEnd.split('-');
    var xmKsStartDatas = xmKsStartData.split('-');
    var xmKsEndDatas = xmKsEndData.split('-');
    var xmStartStr = xmStarts[1] + '-' + xmStarts[2] + '-' + xmStarts[0];
    var xmEndStr = xmEnds[1] + '-' + xmEnds[2] + '-' + xmEnds[0];
    var xmKsStartDataStr = xmKsStartDatas[1] + '-' + xmKsStartDatas[2] + '-' + xmKsStartDatas[0];
    var xmKsEndDataStr = xmKsEndDatas[1] + '-' + xmKsEndDatas[2] + '-' + xmKsEndDatas[0];
    //项目开始时间和项目结束时间互斥
    var xmEndXmStart = (Date.parse(xmEndStr) - Date.parse(xmStartStr)) / 3600 / 1000;
    if (xmEndXmStart < 0 || xmEndXmStart === 0) {
        //$("#TS_XMGL-XM_START").addClass("blankError").addClass("errorbox");
        //$("#TS_XMGL-XM_END").addClass("blankError").addClass("errorbox");
        $("#TS_XMGL-XM_START").parent().showError("项目开始时间应早于项目结束时间");
        $("#TS_XMGL-XM_END").parent().showError("项目结束时间应晚于项目开始时间");
        return false;
    }
    //考试开始时间和考试结束时间互斥
    var ksEndKsStart = (Date.parse(xmKsEndDataStr) - Date.parse(xmKsStartDataStr)) / 3600 / 1000;
    if (ksEndKsStart < 0) {
        //$("#TS_XMGL-XM_KSSTARTDATA").addClass("blankError").addClass("errorbox");
        //$("#TS_XMGL-XM_KSENDDATA").addClass("blankError").addClass("errorbox");
        $("#TS_XMGL-XM_KSSTARTDATA").parent().showError("考试开始时间应早于考试结束时间");
        $("#TS_XMGL-XM_KSENDDATA").parent().showError("考试结束时间应晚于考试开始时间");
        return false;
    }
    //项目开始时间和考试考试时间互斥
    var ksStartXmStart = (Date.parse(xmKsStartDataStr) - Date.parse(xmStartStr)) / 3600 / 1000;
    if (ksStartXmStart < 0) {
        //$("#TS_XMGL-XM_START").addClass("blankError").addClass("errorbox");
        //$("#TS_XMGL-XM_KSSTARTDATA").addClass("blankError").addClass("errorbox");
        $("#TS_XMGL-XM_START").parent().showError("项目开始时间不应晚于考试开始时间");
        $("#TS_XMGL-XM_KSSTARTDATA").parent().showError("考试开始时间不应早于项目开始时间");

        return false;
    }
    //考试结束时间与项目结束时间互斥
    var xmEndKsEnd = (Date.parse(xmEndStr) - Date.parse(xmKsEndDataStr)) / 3600 / 1000;
    if (xmEndKsEnd < 0) {
        //$("#TS_XMGL-XM_END").addClass("blankError").addClass("errorbox");
        //$("#TS_XMGL-XM_KSENDDATA").addClass("blankError").addClass("errorbox");
        $("#TS_XMGL-XM_END").parent().showError("项目结束时间不应早于考试结束时间");
        $("#TS_XMGL-XM_KSENDDATA").parent().showError("考试结束时间不应晚于项目结束时间");
        return false;
    }
    
    
    
    

};
//修改input时间样式
var xmStart = _viewer.getItem("XM_START");
var xmEnd = _viewer.getItem("XM_END");
var xmKsStartData = _viewer.getItem("XM_KSSTARTDATA");
var xmKsEndData = _viewer.getItem("XM_KSENDDATA");
$('#' + xmStart._opts.id + "_div").css('min-height', '32px');
$('#' + xmEnd._opts.id + "_div").css('min-height', '32px');
$('#' + xmKsStartData._opts.id + "_div").css('min-height', '32px');
$('#' + xmKsEndData._opts.id + "_div").css('min-height', '32px');

// 下一步按钮
// 1把数据保存到数据库
_viewer.getBtn("nextbtn").unbind("click").bind("click", function (event) {
    var xmStart = _viewer.getItem("XM_START").getValue();//项目开始时间
    var xmEnd = _viewer.getItem("XM_END").getValue();//项目截至时间
    var xmKsStartData = _viewer.getItem("XM_KSSTARTDATA").getValue();//考试开始时间
    var xmKsEndData = _viewer.getItem("XM_KSENDDATA").getValue();//考试截至时间
    var xmName = _viewer.getItem("XM_NAME").getValue();
//	alert(xmName);
    if (xmName === "") {
        //$("#TS_XMGL-XM_NAME").addClass("blankError").addClass("errorbox");
        $("#TS_XMGL-XM_NAME").parent().showError("项目名称不能为空");
    }// else {
       // $("#TS_XMGL-XM_NAME").removeClass("blankError").removeClass("errorbox");
    //}

    var a = setTime(xmStart, xmEnd, xmKsStartData, xmKsEndData);
    //alert(typeof(a)!="undefined");
//	alert(a);
    if (xmName !== "" && a) {
        //if(xmName!="" && (a !=false||typeof(a)!="undefined")){
//		alert(23);
        _viewer.doActReload('saveAndToSZ');// 这里不用传参数，这个方法默认是获取所有值
        var XM_ID = _viewer.getItem("XM_ID").getValue();// 执行完保存后，自动把ID回填了
        var XM_TYPE = _viewer.getItem("XM_TYPE").getValue();// 得到类型值
        // 从项目管理到项目设置传参
        var extWhere = "and XM_ID = '" + XM_ID + "'";
        var params = {"XM_ID": XM_ID, "_extWhere": extWhere};
        var url = "TS_XMGL_SZ.list.do?&_extWhere=" + extWhere;
        var options = {"url": url, "params": params, "menuFlag": 3, "top": true};
        //$( ".ui-dialog-titlebar-close").click();
        Tab.open(options);
        _viewer.getParHandler().refresh();
        _viewer.backA.mousedown();
    } else {
        return false;
    }
});

// 保存后的操作ui-dialog-titlebar-close

_viewer.afterSave = function (resultdata) {
    var XM_ID = resultdata.XM_ID;
    var XM_GJ = resultdata.XM_GJ;
    var param = {
        "XM_ID": XM_ID,
        "XM_GJ": XM_GJ
    };
    // _viewer.doActReload('saveAfterToSZ','param');//这里不用传参数，这个方法默认是获取所有值
    FireFly.doAct(_viewer.servId, "afterSaveToSz", param);
    _viewer.refresh();
};

// 查看按钮打开的卡片呈现只读样式

// var saveBtn=_viewer.getBtn("save");
// var nextBtn=_viewer.getBtn("nextbtn");
// //var qq=$(".item ui-corner-5").readCard();
// saveBtn.hide();
// nextBtn.hide();
// _viewer.readCard();
//	
if (_viewer.opts.readOnly) {
    _viewer.getBtn("nextbtn").hide();
    _viewer.readCard();
}


//根据选择是否人工审核
_viewer.getItem("XM_TYPE").change(function () {
    var flowSerTmp = _viewer.getItem("XM_TYPE").getValue();

    if ("资格类考试" === flowSerTmp) {
        _viewer.getItem("XM_KHDKZ").setValue(1);
    } else {
        _viewer.getItem("XM_KHDKZ").setValue(2);
    }
});

function setTime(xmStart, xmEnd, xmKsStartData, xmKsEndData) {

    var xmStarts = xmStart.split('-');
    var xmEnds = xmEnd.split('-');
    var xmKsStartDatas = xmKsStartData.split('-');
    var xmKsEndDatas = xmKsEndData.split('-');
    var xmStartStr = xmStarts[1] + '-' + xmStarts[2] + '-' + xmStarts[0];
    var xmEndStr = xmEnds[1] + '-' + xmEnds[2] + '-' + xmEnds[0];
    var xmKsStartDataStr = xmKsStartDatas[1] + '-' + xmKsStartDatas[2] + '-' + xmKsStartDatas[0];
    var xmKsEndDataStr = xmKsEndDatas[1] + '-' + xmKsEndDatas[2] + '-' + xmKsEndDatas[0];
    //项目开始时间和项目结束时间互斥
    var xmEndXmStart = (Date.parse(xmEndStr) - Date.parse(xmStartStr)) / 3600 / 1000;
    if (xmEndXmStart < 0 || xmEndXmStart === 0 || isNaN(xmEndXmStart)) {
        //$("#TS_XMGL-XM_START").addClass("blankError").addClass("errorbox");
        //$("#TS_XMGL-XM_END").addClass("blankError").addClass("errorbox");
        $("#TS_XMGL-XM_START").parent().showError("项目开始时间应早于项目结束时间");
        $("#TS_XMGL-XM_END").parent().showError("项目结束时间应晚于项目开始时间");
        return false;
    }
    //考试开始时间和考试结束时间互斥
    var ksEndKsStart = (Date.parse(xmKsEndDataStr) - Date.parse(xmKsStartDataStr)) / 3600 / 1000;
    if (ksEndKsStart < 0 || isNaN(ksEndKsStart)) {
        //$("#TS_XMGL-XM_KSSTARTDATA").addClass("blankError").addClass("errorbox");
        //$("#TS_XMGL-XM_KSENDDATA").addClass("blankError").addClass("errorbox");
        $("#TS_XMGL-XM_KSSTARTDATA").parent().showError("考试开始时间应早于考试结束时间");
        $("#TS_XMGL-XM_KSENDDATA").parent().showError("考试结束时间应晚于考试开始时间");
        return false;
    }
    //项目开始时间和考试考试时间互斥
    var ksStartXmStart = (Date.parse(xmKsStartDataStr) - Date.parse(xmStartStr)) / 3600 / 1000;
    if (ksStartXmStart < 0 || isNaN(ksStartXmStart)) {
        //$("#TS_XMGL-XM_START").addClass("blankError").addClass("errorbox");
        //$("#TS_XMGL-XM_KSSTARTDATA").addClass("blankError").addClass("errorbox");
        $("#TS_XMGL-XM_START").parent().showError("项目开始时间应早于考试开始时间");
        $("#TS_XMGL-XM_KSSTARTDATA").parent().showError("考试开始时间应晚于项目开始时间");
        return false;
    }
    //考试结束时间与项目结束时间互斥
    var xmEndKsEnd = (Date.parse(xmEndStr) - Date.parse(xmKsEndDataStr)) / 3600 / 1000;
    if (xmEndKsEnd < 0 || isNaN(ksStartXmStart)) {
        //$("#TS_XMGL-XM_END").addClass("blankError").addClass("errorbox");
        //$("#TS_XMGL-XM_KSENDDATA").addClass("blankError").addClass("errorbox");
        $("#TS_XMGL-XM_END").parent().showError("项目结束时间应晚于考试结束时间");
        $("#TS_XMGL-XM_KSENDDATA").parent().showError("考试结束时间应早于项目结束时间");
        return false;
    }
    if (xmEndXmStart > 0 && ksEndKsStart >= 0 && ksStartXmStart >= 0 && xmEndKsEnd >= 0) {
        return true;
    }

}

if ($("#TS_XMGL-XM_FQDW_CODE__NAME").hasClass("disabled") === false) {

    $("#TS_XMGL-XM_FQDW_CODE__NAME").unbind("click").bind("click", function (event) {

        var configStr = "TS_ORG_DEPT_ALL,{'TYPE':'single','sId':'TS_ORG_DEPT','pvlg':'CODE_PATH'}";

        var options = {
            "config": configStr,
            "params": {"USE_SERV_ID": "TS_ORG_DEPT"},
            "parHandler": _viewer,
            "formHandler": _viewer.form,
            "replaceCallBack": function (idArray, nameArray) {//回调，idArray为选中记录的相应字段的数组集合

                var codes = idArray;
                var names = nameArray;
                $("#TS_XMGL-XM_FQDW_CODE__NAME").val(names);
                $("#TS_XMGL-XM_FQDW_CODE").val(codes);
                $("#TS_XMGL-XM_FQDW_NAME").val(names);
                console.log($("#TS_XMGL-XM_FQDW_CODE").val());
                console.log($("#TS_XMGL-XM_FQDW_NAME").val());
            }
        };

        var queryView = new rh.vi.rhDictTreeView(options);
        queryView.show(event, [], [0, 495]);
    });
}


//添加初始化准考证模板下载按钮
var xmglExcelTemplateId = 'TS_XMGL-EXCEL_TEMPLATE_ID';
var $impTemplateFile = jQuery('#' + xmglExcelTemplateId + "-impFile");
if ($impTemplateFile.length <= 0) {
    var $xmglExcelTemplate = jQuery('#' + xmglExcelTemplateId + '_div');
    $impTemplateFile = jQuery(
        ['<div id="' + xmglExcelTemplateId + '-impFile" class="inner" style="width:50%;">',
            '   <a class="rh-icon rhGrid-btnBar-a" actcode="impFile" title=""><span class="rh-icon-inner">准考证模板</span><span class="rh-icon-img btn-download"></span></a>',
            '</div>'].join(''));
    $impTemplateFile.find('a').unbind('click').bind('click', function () {
        window.open(FireFly.getContextPath() + '/ts/imp_template/准考证模板.xls');
    });
    $xmglExcelTemplate.after($impTemplateFile);
}