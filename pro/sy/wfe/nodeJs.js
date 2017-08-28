/**是否窗口正在关闭**/
var _winClosed = false;
var NODE_DEF_VERSION = "1.1";
var _OPEN_IFRAME = false;
/**
 * 数据权限控制输入框的ID数组
 */
var _fieldIds = ["FIELD_EXCEPTION","FIELD_HIDDEN","FIELD_MUST","FIELD_DISPLAY"
	                 ,"GROUP_DISPLAY","GROUP_HIDE","GROUP_EXPAND","GROUP_COLLAPSE"
	                 ,"MIND_CODE","MIND_REGULAR","MIND_TERMINAL"];

$(document).ready(function() {
	$( "#tabs" ).tabs();
	window.status= ""; 
	var nodeObj = window.dialogArguments;
	if(nodeObj == null) {
		var dataObj = _parent.$(_parent.document).find(".wfe-details-iframe").data("dataObj");
		if(dataObj != null) {
			nodeObj = dataObj;
			_OPEN_IFRAME = true;
		} else {
//			alert("无效的节点数据对象。");
			alert(Language.transStatic("lineJs_string1"));
			return;
		}
	}
	
	if (nodeObj.BUTTONS_DEF || nodeObj.WF_CUSTOM_VARS) {
		if (nodeObj.NODE_DEF_VERSION != NODE_DEF_VERSION) {
			// 由于流程定义数据格式有调整，因此判断节点定义版本，避免出现问题。
			alert(Language.transStatic("nodeJs_string1"));
			window.close();
			_winClosed = true;
			return;
		}
	}
	
	//初始化 基本信息
    initBaseInfo(nodeObj);
    
    //初始化 环节
    initHuanjie();
	
	//初始化 超时设置
	initTimeOut(nodeObj);
	
	//初始化 组织资源 页面
	initOrgInfo(nodeObj);
	
	//事件初始化
	initEvent(nodeObj);
	
	if(!nodeObj.FIELD_CONTROL){
		nodeObj.FIELD_CONTROL = "1";
	}
	
	$("#FIELD_CONTROL").val(nodeObj.FIELD_CONTROL);
	
	//向指定的输入框中设置上次定义的值。
	fillFieldValue(nodeObj,_fieldIds);
	
	//兼容老数据，如果有按钮定义节点，则使用新数据
	if(nodeObj.BUTTONS_DEF){
		var btnDef = DataEncode.decode(nodeObj.BUTTONS_DEF);
		WfAction.initActs(btnDef);
	}else{
		//显示老数据
		WfAction.initActs(nodeObj.FORM_BUTTONS,"FORM");
		WfAction.initActs(nodeObj.WF_BUTTONS,"WF");
	}
	
	//从node act 表中取得定义的规则 1 读 2 编辑 注释说明删除权限  
	var existedFileTypeJson = nodeObj.FILE_CONTROL;
	existedFileTypeJson = eval("(" + existedFileTypeJson + ")");
	initFileTypeField(nodeObj, existedFileTypeJson); //按照服务定义中的  自定义 字段 来 初始化
	
    //是否办结选项，如果被选中则办结按钮名称默认为“办结”
    jQuery("#PROC_END_FLAG").click(function(){
    	if(jQuery(this).attr("checked")){
//    		jQuery("#PROC_END_NAME").val("办结");
    		jQuery("#PROC_END_NAME").val(Language.transStatic("nodeJs_string2"));
    	}else{
    		jQuery("#PROC_END_NAME").val("");
    	}
    });
    //给返回拟稿人绑定事件
    $("#RETURN2DRAFTER").click(function(){
    	if (this.checked) {
//    		$("#RETURN2DRAFTER_NAME").val("退回经办人");
    		$("#RETURN2DRAFTER_NAME").val(Language.transStatic("nodeJs_string3"));
    		$("#RETURN2DRAFTER_NAME").attr("disabled",false);
    	} else {
    		$("#RETURN2DRAFTER_NAME").val("");
    		$("#RETURN2DRAFTER_NAME").attr("disabled",true);
    	}
    });
    (function(){
    	var drafterDom = $("#RETURN2DRAFTER")[0];
    	if (drafterDom) {
    		if (!drafterDom.checked) {
    			$("#RETURN2DRAFTER_NAME").attr("disabled",true);
    		}
    	}
    })();
    
    //初始化自定义变量定义界面
    customVar.init(DataEncode.decode(nodeObj.WF_CUSTOM_VARS));
    
    dataUpdater.init(nodeObj.FIELD_UPDATE);
    
    extJsonObj.init(nodeObj, DataEncode.decode(nodeObj.EXT_JSON));
});

/**
 * 初始化环节
 * 首先查询字典PE_HUANJIE，如果字典数据为空，还是input；否则，该为下拉显示字典数据。
 */
function initHuanjie() {
	var $select = $('<select id="HUANJIE" name="HUANJIE">');
	var PE_HUANJIE = FireFly.getDict("PE_HUANJIE1");
	if ($.isArray(PE_HUANJIE)) {
		PE_HUANJIE = PE_HUANJIE[0]["CHILD"];
	}
	if ($.isArray(PE_HUANJIE) && PE_HUANJIE.length > 0) {
//		$select.append('<option value="" selected="selected">-- 无 --</option>');
		$select.append('<option value="" selected="selected">-- '+Language.transStatic("rhPortalView_string34")+' --</option>');
		$.each(PE_HUANJIE, function(index,obj) {
			if (obj && obj.ID && obj.ID.indexOf("ALL")<=0) {
				$select.append('<option value="'+obj.ID+'">'+obj.NAME+'</option>');
			}
		});
		$("#HUANJIE_CON").html($select);
	}
	
}

/**
 * 初始化超时设置 
 * [{'TYPE':'YIBAN','TIMEOUT':'24','REMIND':'EMAIL,MESSAGE','OPER':'CUIBAN,BACK'},
 * {'TYPE':'JINJI','TIMEOUT':'24','REMIND':'EMAIL,MESSAGE','OPER':'CUIBAN'},
 * {'TYPE':'TEJI','TIMEOUT':'24','REMIND':'EMAIL,MESSAGE','OPER':'CUIBAN'}]
 */
function initTimeOut(nodeObj) {
	//构造页面
    var typeArray = ["YIBAN","JINJI","TEJI"];
    var operArray = ["BACK","CUIBAN"];
//    var operNameArray = ["退回","催办"];
    var operNameArray = [Language.transStatic("nodeJs_string4"),Language.transStatic("nodeJs_string5")];
    
	var dicList = FireFly.getDict("SY_COMM_REMIND_TYPE");
	var dicObjs = dicList[0]["CHILD"];//获取字典信息对象数组
	
    for(var i=0;i<typeArray.length;i++){ //紧急的类型
        var jinJiType = typeArray[i];
        
        var checkBoxRemind = new Array();
        checkBoxRemind.push("<span id='remindSpan"+jinJiType+"' class='ui-checkbox-default'>");
		for (var j = 0; j < dicObjs.length; j++) { //提醒方式的字典
			var remindType = dicObjs[j]["ID"];
			
			checkBoxRemind.push("<input type='checkbox' id='remindCheckbox"+jinJiType+remindType+"' name='remindCheckbox"+jinJiType+"' value ='"+remindType+"'>");
			checkBoxRemind.push(dicObjs[j]["NAME"]);
			checkBoxRemind.push("&nbsp;&nbsp;");
		}
		checkBoxRemind.push("</span>");
		
		var remindDiv = $("#NODE_TIMEOUT_"+jinJiType+"_REMIND");
		$(checkBoxRemind.join("")).appendTo(remindDiv);
		
		var checkBoxOper = new Array();
		checkBoxOper.push("<span id='operSpan"+jinJiType+"' class='ui-checkbox-default'>");
		for (var k=0;k<operArray.length;k++) { //操作的类型
			checkBoxOper.push("<input type='checkbox' id='operCheckbox"+jinJiType+operArray[k]+"' name='operCheckbox"+jinJiType+"' value ='"+operArray[k]+"'>");
			checkBoxOper.push(operNameArray[k]);
			checkBoxOper.push("&nbsp;&nbsp;");
		}
		checkBoxOper.push("</span>");
		
		var operDiv = $("#NODE_TIMEOUT_"+jinJiType+"_OPER");
		$(checkBoxOper.join("")).appendTo(operDiv);
    }
	
	//填值，
    var timeOutStr = Hex.decode(nodeObj.NODE_TIMEOUT);
	
	if (timeOutStr.indexOf("TIMEOUT") < 0) {
	    return;
	}
	
    var existedTimeoutJson = eval("(" + timeOutStr + ")");
	
	if(existedTimeoutJson != undefined) {
		jQuery.each(existedTimeoutJson,function(i,existitem) {
			var jinJiType = existitem.TYPE;
			//超时时间
			$("#NODE_TIMEOUT_" + jinJiType).val(existitem.TIMEOUT);
			
			//提醒方式
			if (existitem.REMIND && existitem.REMIND.length > 0) {
				var remindTypes = existitem.REMIND.split(",");
				for (var j=0;j<remindTypes.length;j++) {
					$("#remindCheckbox"+jinJiType+remindTypes[j]).attr("checked",true); 
				}				
			}
			
			//操作
			if (existitem.OPER && existitem.OPER.length > 0) {
				var operTypes = existitem.OPER.split(",");
				for (var j=0;j<operTypes.length;j++) {
					$("#operCheckbox"+jinJiType+operTypes[j]).attr("checked",true); 
				}
			}
		});
	}
}

/**
 * 初始化，文件类型，按照字段定义的
 */
function initFileTypeField(nodeObj, existedFileTypeJson) {
    var servDef = nodeObj.servDef;

    var fileTypeStr = new Array();
	for(var key in servDef.ITEMS){ 
		var itemDef = servDef.ITEMS[key];
		
		if (itemDef.ITEM_INPUT_TYPE == 14) { //自定义附件
			fileTypeStr.push(itemDef);
		}
	} 
    
    initFileTypeControlWithField(fileTypeStr, existedFileTypeJson);
}


/**
 * 向指定的输入框中设置上次定义的值。
 * @param fieldCtls 输入框ID数组
 */
function fillFieldValue(nodeObj,fieldCtls){
	for(var i=0;i<fieldCtls.length;i++){
		var fieldCtl = fieldCtls[i];
		$("#" + fieldCtl).val(nodeObj[fieldCtl]);
		if(nodeObj[fieldCtl + "__NAME"]){
			$("#" + fieldCtl + "__NAME").val(nodeObj[fieldCtl + "__NAME"]);
		}else{
			$("#" + fieldCtl + "__NAME").val(nodeObj[fieldCtl]);
		}
	}	
}

var WfAction = {};

/**初始化按钮**/
WfAction.initActs = function(btns,actType){
	if(typeof(btns) == "object"){
		for(var i=0;i<btns.length;i++){
			btns[i].param = Hex.decode(btns[i].param);
			btns[i].newname = Hex.decode(btns[i].newname);
			WfAction.addAct(btns[i]);
		}
	}else{
		//如果btns为null则返回。
		if(!btns || btns.length == 0) return;
		var btnArr = btns.split(",");
		for(var i=0;i<btnArr.length;i++){
			var btnObj = {"name":"","code":btnArr[i],"type":actType};
			WfAction.addAct(btnObj);
		}
	}
}

/**增加一行按钮**/
WfAction.addAct = function(btnObj){
	//name,code,actType
	var actBtnsTbl = jQuery("#actBtnTable");
	
	if (btnObj.newname == undefined) {
		btnObj.newname = btnObj.name;
	}
	
	var jsonVal = btnObj.name + "^" + btnObj.code + "^" + btnObj.type;
	var str = "<tr jsonVal='" + jsonVal + "' ><td class='tl'>" + btnObj.name + 
	          "</td><td class='tl'><input type='text' name='newname' value='" + btnObj.newname + "'></td><td class='tl'>" 
		      + btnObj.code + "</td><td><textarea class='wp' rows=3 cols=40>";
	//如果参数存在
	if(btnObj.param){
		str += btnObj.param;
	}
//	str += "</textarea></td><td class='tc' onclick='javascript:WfAction.delAct(this)'>删除</td>";
	str += "</textarea></td><td class='tc' onclick='javascript:WfAction.delAct(this)'>"+Language.transStatic('rh_ui_card_string22')+"</td>";
	actBtnsTbl.append(str);
}

/**删除一行按钮**/
WfAction.delAct = function(obj){
	jQuery(obj).parent().remove();
}

/**
 * 初始化组织资源的页面
 */
function initOrgInfo(nodeObj){
	//bind类型
	if(nodeObj.NODE_BIND_MODE == 'ROLE'){
		$("#NODE_BIND_MODE_ROLE").attr("checked",true);
	}
    //角色
	$("#NODE_ROLE_CODES").val(nodeObj.NODE_ROLE_CODES);
	//默认给全部
	if (nodeObj.NODE_ROLE_MODE == undefined) {
	    nodeObj.NODE_ROLE_MODE = "2";
	}
	jQuery("input[name='NODE_ROLE_MODE'][value='"+nodeObj.NODE_ROLE_MODE+"']").attr("checked",true);
	
	$("#NODE_ROLE_WHERE").val(nodeObj.NODE_ROLE_WHERE);
	$("#NODE_ROLE_CODES__NAME").val(nodeObj.NODE_ROLE_CODES__NAME);
	
	//部门
	//默认给全部
	if (nodeObj.NODE_DEPT_MODE == undefined) {
	    nodeObj.NODE_DEPT_MODE = "2";
	}	
	jQuery("input[name='NODE_DEPT_MODE'][value='"+nodeObj.NODE_DEPT_MODE+"']").attr("checked",true);
	
	$("#NODE_DEPT_CODES").val(nodeObj.NODE_DEPT_CODES);
	if(nodeObj.NODE_DEPT_MODE == 3) { //如果是预定义， 处理下拉框
	    //将 NODE_DEPT_CODES 设置成 下拉框的默认值
	    //$("#NODE_DEPT_CODES").val($("#nodeDeptYuding").val());
	    //$("#nodeDeptYuding").attr("value",nodeObj.NODE_DEPT_CODES);
        $("#nodeDeptYuding").val(nodeObj.NODE_DEPT_CODES);
	}

	//$("#NODE_DEPT_LEVEL").val(nodeObj.NODE_DEPT_LEVEL);
	$("#NODE_DEPT_WHERE").val(nodeObj.NODE_DEPT_WHERE);
	$("#NODE_DEPT_CODES__NAME").val(nodeObj.NODE_DEPT_CODES__NAME);
	
	//人员
	//默认给全部
	if (nodeObj.NODE_USER_MODE == undefined) {
	    nodeObj.NODE_USER_MODE = 2;
	}
	
	if(nodeObj.NODE_USER_MODE == 3){//用户选择预定义
		$("#nodeUserYuding").val(nodeObj.NODE_USER_CODES);
		nodeObj.NODE_USER_CODES = "";
	}
	
	$("#NODE_USER_CODES").val(nodeObj.NODE_USER_CODES);
	jQuery("input[name='NODE_USER_MODE'][value='"+nodeObj.NODE_USER_MODE+"']").attr("checked",true);
	$("#NODE_USER_WHERE").val(nodeObj.NODE_USER_WHERE);		
	$("#NODE_USER_CODES__NAME").val(nodeObj.NODE_USER_CODES__NAME);
}

/**
 * 初始化 第一个tab 的基础信息
 */
function initBaseInfo(nodeObj){
	$("#SERV_ID").val(nodeObj.SERV_ID);	
	$("#SERV_PID").val(nodeObj.SERV_PID);	
	$("#PROC_CODE").val(nodeObj.PROC_CODE);	
	$("#NODE_CODE").val(nodeObj.NODE_CODE);	
	$("#NODE_NAME").val(nodeObj.NODE_NAME);
	$("#EN_NAME").val(nodeObj.EN_NAME);
	$("#NODE_CAPTION").val(nodeObj.NODE_CAPTION);
	$("#NODE_SORT").val(nodeObj.NODE_SORT);
	//$("#MIND_CODE").val(nodeObj.MIND_CODE);
	//$("#NODE_TIMEOUT").val(nodeObj.NODE_TIMEOUT);
	//$("#NODE_TIMEOUT_ACT").val(nodeObj.NODE_TIMEOUT_ACT);
	$("#PROC_END_NAME").val(nodeObj.PROC_END_NAME);
	$("#NODE_SUB_PROC").val(nodeObj.NODE_SUB_PROC);
	$("#EVENT_CLS").val(nodeObj.EVENT_CLS);
	// $("#BREAK_THROUGH").val(nodeObj.BREAK_THROUGH);
	$("#NODE_MEMO").val(nodeObj.NODE_MEMO);
	$("#NODE_EXTEND_CLASS").val(nodeObj.NODE_EXTEND_CLASS);	
	$("#NODE_REMIND_USER").val(nodeObj.NODE_REMIND_USER);	
	$("#NODE_TYPE").val(nodeObj.NODE_TYPE);
	//$("#MIND_TERMINAL").val(nodeObj.MIND_TERMINAL);
	$("#MIND_REGULAR_MUST").val(nodeObj.MIND_REGULAR_MUST);
	$("#MIND_REGULAR_SCRIPT").val(Hex.decode(nodeObj.MIND_REGULAR_SCRIPT));
	$("#MIND_SCRIPT").val(Hex.decode(nodeObj.MIND_SCRIPT));
	$("#MIND_TERMINAL_SCRIPT").val(Hex.decode(nodeObj.MIND_TERMINAL_SCRIPT));
	
	if(nodeObj.MIND_REGULAR_MUST=="1"){
		$("#MIND_REGULAR_MUST").attr("checked",true);
	}
	$("#MIND_TERMINAL_MUST").val(nodeObj.MIND_TERMINAL_MUST);
	if(nodeObj.MIND_TERMINAL_MUST=="1"){
		$("#MIND_TERMINAL_MUST").attr("checked",true);
	}
	$("#MIND_NEED_FLAG").val(nodeObj.MIND_NEED_FLAG);
	if(nodeObj.MIND_NEED_FLAG=="1"){
		$("#MIND_NEED_FLAG").attr("checked",true);
	}

	$("#CONVERGE_TARGET").val(nodeObj.CONVERGE_TARGET);

	$("#NODE_IF_CONVERGE").val(nodeObj.NODE_IF_CONVERGE);
	if(nodeObj.NODE_IF_CONVERGE=="1"){
		$("#NODE_IF_CONVERGE").attr("checked",true);
	}
	$("#PROC_END_FLAG").val(nodeObj.PROC_END_FLAG);
	if(nodeObj.PROC_END_FLAG=="1"){
		$("#PROC_END_FLAG").attr("checked",true);
	}
	
	/** 给是否自动结束给一个默认自动结束的值*/
	if(typeof(nodeObj.NODE_IF_AUTOEND) == "undefined"){
	    nodeObj.NODE_IF_AUTOEND = 1;
	}
	$("#NODE_IF_AUTOEND").val(nodeObj.NODE_IF_AUTOEND);
	if(nodeObj.NODE_IF_AUTOEND=="1"){
		$("#NODE_IF_AUTOEND").attr("checked",true);
	}
	
	$("#NODE_SELF_SEND").val(nodeObj.NODE_SELF_SEND);
}

/**
 * 事件初始化
 * @param nodeObj
 */
function initEvent(nodeObj){
	//组织资源页面 角色信息中 “角色”和“送全部”不能同时选中
	$("#NODE_ROLE_MODE_2").bind("click", function(){
		$("#NODE_BIND_MODE_ROLE").attr("checked", false);
	});
	$("#NODE_BIND_MODE_ROLE").bind("change", function(){
		if(this.checked){
			$("#NODE_ROLE_MODE_2").attr("checked", false);
		}
	});
}

/**
 * 从字段中读取 文件权限
 * @param filedDefs
 * @param existedFileTypeJson
 */
function initFileTypeControlWithField(fileTypes, existedFileTypeJson) {
	var tableview = $("#fileTypeSelect");

	//循环服务中的文件类型串，取到act表中的具体值，取不到，当0处理
	jQuery.each(fileTypes,function(i,itemold) {
	    var existFlag = false;

	    //默认值从输入设置中配置的VALUE值
	    var inputConfig = StrToJson(itemold.ITEM_INPUT_CONFIG);
	    var defaultValue = 0;
	    if (inputConfig && inputConfig.VALUE) {
	    	defaultValue = inputConfig.VALUE;
	    }	    
	    
	    //将字段定义转化
	    var item = {};
	    item.ID = itemold.ITEM_CODE;
	    item.NAME = itemold.ITEM_NAME;
	    item.VALUE = defaultValue;
	    
		if(existedFileTypeJson != undefined) {
			jQuery.each(existedFileTypeJson,function(i,existitem) {
				if (item.ID == existitem.ID) {
					existFlag = true;	    
					//构建表格
					var str = "<tr id='"+existitem.ID+"'><td><span>"+item.NAME+"</span></td><td><input type='checkbox' class='read' id='checkRead"+item.NAME+"' value=''></td>"+
					          "<td><input type='checkbox' class='write' id='checkWrite"+item.NAME+"' value=''></td>"+
					          "<td><input class='addnew' type='checkbox' id='checkAdd"+item.NAME+"' value=''></td>"+
					          "<td><input class='del' type='checkbox' id='checkDel"+item.NAME+"' value=''></td>"+
					          "<td><input class='modify' type='checkbox' id='checkMdofiy"+item.NAME+"' value=''></td>"+
					          "<td><input class='download' type='checkbox' id='checkDown"+item.NAME+"' value=''></td></tr>";
					
					tableview.append(str);
					
					initFileItemValue(item,existitem);
					
					return;
				}
			});		
		}

		//没找到，设置初始值
		if (!existFlag) {
			var str = "<tr id='"+item.ID+"'><td><span>"+item.NAME+"</span></td><td><input type='checkbox' class='read' id='checkRead"+item.NAME+"' value=''/></td>"+
			          "<td><input class='write' type='checkbox' id='checkWrite"+item.NAME+"' value=''/></td>"+
			          "<td><input class='addnew' type='checkbox' id='checkAdd"+item.NAME+"' value=''/></td>"+
			          "<td><input class='del' type='checkbox' id='checkDel"+item.NAME+"' value=''/></td>"+
					  "<td><input class='modify' type='checkbox' id='checkMdofiy"+item.NAME+"' value=''></td>"+
					  "<td><input class='download' type='checkbox' id='checkDown"+item.NAME+"' value=''></td></tr>";
					
			tableview.append(str);			
			
			initFileItemValue(item);
		}		
	});	
}

/**
 * 32,16,8,4,2,1 	分别是下载、修改、删除、上传、编辑、查看
 * D, M, X,U,W,R 	分别是下载，修改，删除，上传，编辑，查看
 * @param defItemValue
 */
function getFileACTValue(defItemValue) {
	var digitalNum = 0;
	if (defItemValue.indexOf("D") > -1) {
		digitalNum += 32;
	}
	if (defItemValue.indexOf("M") > -1) {
		digitalNum += 16;
	}
	if (defItemValue.indexOf("X") > -1) {
		digitalNum += 8;
	}
	if (defItemValue.indexOf("U") > -1) {
		digitalNum += 4;
	}
	if (defItemValue.indexOf("W") > -1) {
		digitalNum += 2;
	}
	if (defItemValue.indexOf("R") > -1) {
		digitalNum += 1;
	}
	
	return digitalNum;
}

/**
 * @param defItem 服务中定义的
 * @param fileItem 节点中保存的
 */
function initFileItemValue(defItem,fileItem){
    var existitem = defItem;
	
	if (fileItem != undefined) {
	    existitem = fileItem;
	} else { //节点上没有值，就是取的定义上的值 ， 定义上的值，有可能是字母形式的，转成数字的 D,M,X,U,W,R
		if (existitem.VALUE) {
			var defItemValue = existitem.VALUE.toUpperCase();
			//存在字母权限的定义
			if (defItemValue.indexOf("D") > -1 || defItemValue.indexOf("M") > -1 ||defItemValue.indexOf("X") > -1 ||defItemValue.indexOf("U") > -1 ||defItemValue.indexOf("W") > -1 ||defItemValue.indexOf("R") > -1) { 
				existitem.VALUE = getFileACTValue(defItemValue);
			}			
		}
	}
	
    var acl = existitem.VALUE;
	acl = parseInt(acl);
	
	// 把十进制数字转化成二进制字符串，对应转化为布尔数组，例如0x1010变成[true,false,true,false]
	var acl = (acl & 63).toString(2);

	var len = acl.length;
	
	// 不够六位补零
	var tmpLen = 6 - len;
	for (var i = 0; i < tmpLen; i++) {
		acl = "0" + acl;
	}

	//下载
	var tmpDel = acl.substring(0, 1);
	if (tmpDel.toString() == "1") {
	    $("#checkDown"+defItem.NAME).attr("checked",true);  //下载
	}	
	
	//修改
	var tmpDel = acl.substring(1, 2);
	if (tmpDel.toString() == "1") {
	    $("#checkMdofiy"+defItem.NAME).attr("checked",true);  //修改
	}
	
	//删除
	var tmpDel = acl.substring(2, 3);
	if (tmpDel.toString() == "1") {
	    $("#checkDel"+defItem.NAME).attr("checked",true);  //删除
	}

	//添加
	var tmpAdd = acl.substring(3, 4);
	if (tmpAdd.toString() == "1") {
	    $("#checkAdd"+defItem.NAME).attr("checked",true);  //添加
	}

	//编辑	
	var tmpWrite = acl.substring(4, 5);
	if (tmpWrite.toString() == "1") {
	    $("#checkWrite"+defItem.NAME).attr("checked",true);  //编辑	
	}

	//查看
	var tmpRead = acl.substring(5, 6);
	if (tmpRead.toString() == "1") {
	    $("#checkRead"+defItem.NAME).attr("checked",true);  //查看
	}	
	
	
}

/**
 * 得到保存到数据中的文件控制的串
 * 
 */
function getFileControlStr(){
    var fileTypeArray = new Array(); 
	
	var trArray = [];
	$("#fileTypeSelect tr").each(function(j,m) {
	    if (jQuery(m).hasClass("topTr")) {
		   return;
		}
		var trObj = jQuery(this);
	    var trFileStr = "'ID':'" + trObj.attr('id')
					  + "','NAME':'" + trObj.find("SPAN").text() + "','VALUE':";
		var value = 0;
		trObj.find("input[type='checkbox']:checked").each(function(){
			var tdObj = jQuery(this);
			if (tdObj.hasClass("read")) {
			 value += 1; 
		   } else if (tdObj.hasClass("write")) {
			 value += 2; 				   
		   } else if (tdObj.hasClass("addnew")) {
			 value += 4; 				   
		   } else if (tdObj.hasClass("del")) {
			 value += 8; 
		   } else if (tdObj.hasClass("modify")) {
			 value += 16;
		   } else if (tdObj.hasClass("download")) {
			 value += 32;
		   }
		});
		trArray.push("{" + trFileStr + "'" + value + "'}");
	});
	var rtnFileControlStr = "[" + trArray.join(",") + "]";
	
    return rtnFileControlStr;
}

/**
 * 设置checkbox Dom对象的值
 * @param chkboxIds checkbox Dom对象ID数组
 */
function setCheckBoxVal(chkboxIds){
    for(var i=0;i<chkboxIds.length;i++){
        var chkboxId = chkboxIds[i];
        var checkbox = $("#" + chkboxId);
        if(checkbox.length > 0){
            if(checkbox[0].checked){
                checkbox.val('1');
            }else{
                checkbox.val('2');
            }
        }
    }
}

/**
 * 获取给定紧急程度的提醒设置
 * @param jinJiType 紧急类型
 */
function getTimeOutRemindValue(jinJiType) {
	var remindStr = "";
    $("input[name='remindCheckbox"+jinJiType+"']").each(function(){ 
        if($(this).attr("checked")){
        	remindStr += $(this).val()+","
        }
    });
    
    if (remindStr.length > 0) {
    	remindStr = remindStr.substring(0, remindStr.length-1);
    }
    
    return remindStr;
}

/**
 * 获取给定紧急程度的提醒设置
 * @param jinJiType 紧急类型
 */
function getTimeOutOperValue(jinJiType) {
	var operStr = "";
    $("input[name='operCheckbox"+jinJiType+"']").each(function(){ 
        if($(this).attr("checked")){
        	operStr += $(this).val()+","
        }
    });
    
    if (operStr.length > 0) {
    	operStr = operStr.substring(0, operStr.length-1);
    }    
    
    return operStr;	
}

/**
 * 获取超时设置的串
 * [{'TYPE':'YIBAN','TIMEOUT':'24','REMIND':'EMAIL,MESSAGE','OPER':'CUIBAN,BACK'},...]
 */
function getTimeoutStr() {
    var typeArray = ["YIBAN","JINJI","TEJI"];
    var operArray = ["BACK","CUIBAN"];
	
	var timeOut = new Array();
	timeOut.push("[");
    for(var i=0;i<typeArray.length;i++){
        var jinJiType = typeArray[i];
        var timeOutValue = $("#NODE_TIMEOUT_" + jinJiType).val();

		if(timeOutValue.replace(/[\d+]/ig,"").length>0){
//			alert("请检查，超时时间需填写数字!");
			alert(Language.transStatic("nodeJs_string6"));
			
			return false;
		}

	    timeOut.push("{'TYPE':'" + jinJiType + "',");
        timeOut.push("'TIMEOUT':'" + timeOutValue + "',");	
        timeOut.push("'REMIND':'" + getTimeOutRemindValue(jinJiType) + "',");	
		timeOut.push("'OPER':'" + getTimeOutOperValue(jinJiType) + "'}");
		timeOut.push(",");
    }
    
	timeOut.pop();
	timeOut.push("]");
	return timeOut.join("");
}

/**
 * 确认，并关闭弹出页面，返回节点定义
 */
function confirmall(closeWin){
	var chkboxIds = ["PROC_END_FLAG", "NODE_IF_CONVERGE", "MIND_NEED_FLAG"
	                 ,"MIND_REGULAR_MUST","MIND_TERMINAL_MUST","NODE_IF_AUTOEND", "SHOW_YUE_ZHI"];
	
	setCheckBoxVal(chkboxIds);
	
	//bind类型
	if($("#NODE_BIND_MODE_ROLE").attr('checked')!=undefined) {
		$("#NODE_BIND_MODE").val("ROLE");
	}
	
	//部门 如果选择的是 预定义 ， 则取 nodeDeptYuding 中的值到 NODE_DEPT_CODES
	if($("input[name='NODE_DEPT_MODE']:checked").val() == 3) { //如果是预定义， 从下拉框取
	    $("#NODE_DEPT_CODES").val($("#nodeDeptYuding").val());
	}
	
	//如果是选的全部，将指定的input中的值清除
	if($("input[name='NODE_DEPT_MODE']:checked").val() == 2) { //部门是全部 ， 取消部门的值
	    $("#NODE_DEPT_CODES").val("");
		$("#NODE_DEPT_CODES__NAME").val("");
	}
	if($("input[name='NODE_DEPT_MODE']:checked").val() == 3) { //部门是预定义 ， 取消部门名称的值
		$("#NODE_DEPT_CODES__NAME").val("");
	}
	
	if($("input[name='NODE_ROLE_MODE']:checked").val() == 2) { //角色是全部 ， 取消角色的值
	    $("#NODE_ROLE_CODES").val("");
		$("#NODE_ROLE_CODES__NAME").val("");
	}
	var nodeUserModeVal = $("input[name='NODE_USER_MODE']:checked").val();
	if(nodeUserModeVal == 2) { //人员是全部 ， 取消人员的值
	    $("#NODE_USER_CODES").val("");
		$("#NODE_USER_CODES__NAME").val("");
	}else if(nodeUserModeVal == 4) { //人员是送角色 ， 取消人员的值
	    $("#NODE_USER_CODES").val("");
		$("#NODE_USER_CODES__NAME").val("");
	}else if(nodeUserModeVal == 3){//人员是预定义，则保存预定义选中项的值
		$("#NODE_USER_CODES").val($("#nodeUserYuding").val());
		$("#NODE_USER_CODES__NAME").val("");
	}
	
	/**按钮权限**/
	var FORM_BUTTONS = new Array();
	var WF_BUTTONS = new Array();
	var BUTTONS_DEF = new Array();
	var BUTTON_PARAMS = new Array();
	var BUTTON_ALIAS = new Array();
	
	jQuery("#actBtnTable").find("TR[jsonVal]").val(function(){
		var strJsonVal = jQuery(this).attr("jsonVal");
		var btnVal = strJsonVal.split("^");
		var btn = {};
		btn.name = btnVal[0];
		btn.code = btnVal[1];
		btn.type = btnVal[2];
		var newname = jQuery(this).find("input[name='newname']").val();
		btn.newname = Hex.encode(newname);
		var param = jQuery(this).find("TEXTAREA").val();
		btn.param = Hex.encode(param);
		BUTTONS_DEF.push(btn);
		
		if(param != ""){
			var paramObj = new Object();
			paramObj["name"] = btn.code;
			paramObj["value"] = param;
			BUTTON_PARAMS.push(paramObj);
		}
		
		BUTTON_ALIAS.push("{'ACT_CODE':'" + btn.code + "','ACT_NAME':'" + newname + "'}");
		
		if(btn.type == "FORM"){
			FORM_BUTTONS.push(btn.code);
		}else if(btn.type == "WF"){
			WF_BUTTONS.push(btn.code);
		}
	});

	
	var timeOutStr = getTimeoutStr();
	if (!timeOutStr) {
		return;
	}
	
    var nodeObj = {  
	    SERV_ID : $("#SERV_ID").val(),
		SERV_PID : $("#SERV_PID").val(),
		NODE_CODE : $("#NODE_CODE").val(),
		NODE_IF_AUTOEND : $("#NODE_IF_AUTOEND").val(),
		NODE_NAME : $("#NODE_NAME").val(),
		NODE_CAPTION : $("#NODE_CAPTION").val(),
		NODE_SORT : $("#NODE_SORT").val(),
		NODE_TIMEOUT : Hex.encode(timeOutStr),
		//NODE_TIMEOUT : $("#NODE_TIMEOUT").val(),
		//NODE_TIMEOUT_ACT : $("#NODE_TIMEOUT_ACT").val(),
		PROC_END_NAME : $("#PROC_END_NAME").val(),	
		NODE_SUB_PROC : $("#NODE_SUB_PROC").val(),
		EVENT_CLS : $("#EVENT_CLS").val(),
		// BREAK_THROUGH : $("#BREAK_THROUGH").val(),
		NODE_MEMO : $("#NODE_MEMO").val(),
		NODE_EXTEND_CLASS : $("#NODE_EXTEND_CLASS").val(),
		//NODE_REMIND_USER : $("#NODE_REMIND_USER").val(),
		NODE_TYPE : $("#NODE_TYPE").val(),
		MIND_NEED_FLAG : $("#MIND_NEED_FLAG").val(),
		MIND_TERMINAL_MUST : $("#MIND_TERMINAL_MUST").val(),
		MIND_REGULAR_MUST : $("#MIND_REGULAR_MUST").val(),
		MIND_REGULAR_SCRIPT : Hex.encode($("#MIND_REGULAR_SCRIPT").val()),
		MIND_SCRIPT : Hex.encode($("#MIND_SCRIPT").val()),
		MIND_TERMINAL_SCRIPT : Hex.encode($("#MIND_TERMINAL_SCRIPT").val()),	
		CONVERGE_TARGET : $("#CONVERGE_TARGET").val(),
		NODE_IF_CONVERGE : $("#NODE_IF_CONVERGE").val(),	
		PROC_END_FLAG : $("#PROC_END_FLAG").val(),
		NODE_SELF_SEND : $("#NODE_SELF_SEND").val(),
		NODE_ROLE_CODES : $("#NODE_ROLE_CODES").val(),
		NODE_ROLE_MODE : $("input[name='NODE_ROLE_MODE']:checked").val(),
		NODE_BIND_MODE : $("#NODE_BIND_MODE").val(),
		NODE_ROLE_WHERE : $("#NODE_ROLE_WHERE").val(),
		NODE_ROLE_CODES__NAME : $("#NODE_ROLE_CODES__NAME").val(),
		NODE_DEPT_CODES : $("#NODE_DEPT_CODES").val(),
		NODE_DEPT_MODE : $("input[name='NODE_DEPT_MODE']:checked").val(),
		//NODE_DEPT_LEVEL : $("#NODE_DEPT_LEVEL").val(),
		NODE_DEPT_WHERE : $("#NODE_DEPT_WHERE").val(),
		NODE_DEPT_CODES__NAME : $("#NODE_DEPT_CODES__NAME").val(),
		NODE_USER_CODES : $("#NODE_USER_CODES").val(),
		NODE_USER_MODE : $("input[name='NODE_USER_MODE']:checked").val(),
		NODE_USER_WHERE : $("#NODE_USER_WHERE").val(),
        NODE_USER_CODES__NAME : $("#NODE_USER_CODES__NAME").val(),
		FILE_CONTROL : getFileControlStr(),
		FIELD_CONTROL : $("#FIELD_CONTROL").val(),
		FIELD_UPDATE : dataUpdater.saveDef(),
		FORM_BUTTONS : FORM_BUTTONS.join(","),
		WF_BUTTONS : WF_BUTTONS.join(","),
		BUTTON_PARAMS : DataEncode.encode(BUTTON_PARAMS),
		BUTTONS_DEF : DataEncode.encode(BUTTONS_DEF),
		BUTTON_ALIAS : DataEncode.encode(BUTTON_ALIAS.join("~")),
		WF_CUSTOM_VARS : DataEncode.encode(customVar.saveDef()),
		NODE_DEF_VERSION : NODE_DEF_VERSION,
		EXT_JSON : DataEncode.encode(extJsonObj.saveDef()),
		EN_NAME : $("#EN_NAME").val()
    };
    for(var i=0;i<_fieldIds.length;i++){
    	nodeObj[_fieldIds[i]] = $("#" + _fieldIds[i]).val();
    	nodeObj[_fieldIds[i] + "__NAME"] = $("#" + _fieldIds[i] + "__NAME").val();
    }
    
    if(_OPEN_IFRAME) {
    	var dialogActs = getParentDialogActions();
    	if(dialogActs) {
    		dialogActs.saveNode(nodeObj);
    	}
    } else {
    	window.returnValue = nodeObj;
		if(closeWin){
			window.close();
			_winClosed = true;
		}
    }

}

function getParentDialogActions() {
	return _parent.$(".wfe-details-iframe", _parent.document).data("dialogActs");
}

/**
 * 取消，并返回父页面
 */
function cancelall(){
//	if(!confirm("是否确定取消？")){
	if(!confirm(Language.transStatic("lineJs_string7"))){
		return;
	}

	if(_OPEN_IFRAME) {
    	var dialogActs = getParentDialogActions();
    	if(dialogActs) {
    		dialogActs.close();
    	}
    } else {
	    window.returnValue = "undefined";
	    _winClosed = true;
	    window.close();
	}
}

/**
 * 打开选择字段名称的输入框
 * @param inputName 返回结果输入框的ID
 * @param strWhere 查询条件
 */
function  openFieldControlDialog(inputName,extParams) {
//	alert(extParams);
    var servIdName = $("#SERV_ID").val();
	var servPidName = $("#SERV_PID").val();
	
	var params = {"SRC_SERV_ID":servIdName};
	params = jQuery.extend(params,extParams);
	
	var configStr = "SY_SERV_ITEM_QUERY,{'TARGET':'"+inputName+"~','SOURCE':'ITEM_CODE~ITEM_NAME','TYPE':'multi'}";
	var options = {"itemCode":inputName,"config" :configStr,"rebackCodes":inputName,"parHandler":this,"formHandler":this,"replaceCallBack":function(obj){
		jQuery("#" + inputName).val(obj.ITEM_CODE);
		jQuery("#" + inputName + "__NAME").val(obj.ITEM_NAME);
	},"hideAdvancedSearch":true,"params":params};
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(null,[50,0]);
}

/**
 * 选择工作流定义按钮
 */
function openWfeButtonDialog(inputName) {
		
		var configStr = "SY_WFE_PROC_DEF_ACT,{'TARGET':'"+inputName+"~','SOURCE':'ACT_CODE~ACT_NAME','EXTWHERE':'' ,'TYPE':'multi'}";
		
   		var options = {"itemCode":inputName,"config" :configStr,"rebackCodes":inputName,"parHandler":this,"formHandler":this,"replaceCallBack":function(result){
			var actCodes = result.ACT_CODE.split(",");
			var actNames = result.ACT_NAME.split(",");
			for(var i=0;i < actCodes.length;i++){
				var btnObj = {name:actNames[i],newname:actNames[i],code:actCodes[i],type:"WF"};
				WfAction.addAct(btnObj);
			}
		},"hideAdvancedSearch":true};
		var queryView = new rh.vi.rhSelectListView(options);
		queryView.show(null,[50,0]);
}

/**
 * 选择表单按钮
 */
function  openButtonDialog(inputName,formTableName,pTableName) {
        if(formTableName != "SY_WFE_PROC_DEF"){
		    formTableName = $("#SERV_ID").val();
			pTableName = $("#SERV_PID").val();
		}

    	//var extSql = " and S_FLAG=1 and (ACT_TYPE=3 and ACT_CODE !=^byid^) and SERV_ID in (^"+formTableName+"^,^"+pTableName+"^) " + 
        //" or act_id in (select act_id from sy_serv_act where serv_id  = ^"+formTableName+"^ and ACT_CODE in (^save^,^delete^))";       
		var configStr = "SY_SERV_ACT_QUERY,{'TARGET':'"+inputName+"~','SOURCE':'ACT_CODE~ACT_NAME','TYPE':'multi'}";
		
   		var options = {"itemCode":inputName,"config" :configStr,"rebackCodes":inputName,"parHandler":this
   				,"formHandler":this,"replaceCallBack":function(result){
			var actCodes = result.ACT_CODE.split(",");
			var actNames = result.ACT_NAME.split(",");
			for(var i=0;i < actCodes.length;i++){
				var btnObj = {name:actNames[i],newname:actNames[i],code:actCodes[i],type:"FORM"};
				WfAction.addAct(btnObj);
			}
		},"hideAdvancedSearch":true,"params":{"SRC_SERV_ID":formTableName}};
		var queryView = new rh.vi.rhSelectListView(options);
		queryView.show(null,[50,0]);
}

/**
 * 意见类型的选择
 * @param inputName 输入框ID
 * @param extWhere 附加查询条件
 */
function openMindTypeCode(inputName,extWhere){
	var strWhere = extWhere || " and REGULAR_TYPE = ^2^";
	
	var configStr = "SY_COMM_MIND_CODE,{'TARGET':'"
					+ inputName + "~','SOURCE':'CODE_ID~CODE_NAME~MIND_DESC','EXTWHERE':'"
					+ strWhere + "','TYPE':'single'}";
	var options = {"itemCode":inputName,"config" :configStr,"rebackCodes":inputName,"parHandler":this,
			"formHandler":this,"replaceCallBack":function(obj){
				jQuery("#" + inputName).val(obj.CODE_ID);
				jQuery("#" + inputName + "__NAME").val(obj.CODE_NAME + "(" + obj.CODE_ID + ")" );
			},"hideAdvancedSearch":true};		
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(null,[50,0]);
}

/**
 * 添加一条意见类型，在普通意见的行下面再添加一行
 */
function addMindCode() {
	var trArray = [];
	
	//已经有了的个数
	var usualMindNum = jQuery("#mindTableId").find(".mindUsual").length;
	var mindCode = "MIND_CODE_" + usualMindNum;
	var mindMust = "MIND_MUST_" + usualMindNum;
	var mindScript = "MIND_SCRIPT_" + usualMindNum;
	var mindContent = "MIND_CONTENT_" + usualMindNum;
	var trId = mindCode + "trId";
	
//	trArray.push("<tr class='mindUsual mindUsualAdd' id='"+trId+"'><td class='tl'>普通意见</td>");
	trArray.push("<tr class='mindUsual mindUsualAdd' id='"+trId+"'><td class='tl'>"+Language.transStatic('nodeJs_string7')+"</td>");
	trArray.push("<td align='left'>");
	trArray.push("<input type='text' id='"+mindCode+"__NAME' class='wp63' readonly value='' >");
	trArray.push("<input type='hidden' id='"+mindCode+"' value='' >");
//	trArray.push("<a href='#' onclick=\"openMindTypeCode('"+mindCode+"')\">选择</a>&nbsp;");
	trArray.push("<a href='#' onclick=\"openMindTypeCode('"+mindCode+"')\">"+Language.transStatic('rh_ui_card_string60')+"</a>&nbsp;");
//	trArray.push("<a href='#' onclick=\"cancelSelect('"+mindCode+"')\">删除</a></td>");
	trArray.push("<a href='#' onclick=\"cancelSelect('"+mindCode+"')\">"+Language.transStatic('rh_ui_card_string22')+"</a></td>");
	trArray.push("<td><input type='checkbox' id='"+mindMust+"' name='"+mindMust+"' value=''></td>");
	trArray.push("<td align='left'><input type='text' id='"+mindScript+"' value='' class='wp'></td>");
	trArray.push("<td align='left'><input type='text' id='"+mindContent+"' value='' class='wp'></td></tr>");
	
	var newMindCodeTr = trArray.join("");
	
	jQuery(newMindCodeTr).insertBefore(jQuery("#divideMindId"));	
}

/**
 * 打开选择固定意见
 */
function openSelectRegular() {
	var regularMindCode = jQuery("#MIND_REGULAR").val();
	if (!regularMindCode || regularMindCode == "") { 
//		alert("请先选择固定意见");
		alert(Language.transStatic("nodeJs_string8"));
		return;
	}

	var inputName = "MIND_REGULAR_CONTENT";
	var strWhere = " and REGULAR_TYPE = ^"+regularMindCode+"^";
	
	var configStr = "SY_COMM_MIND_REGULAR,{'TARGET':'"
					+ inputName + "~','SOURCE':'MIND_VALUE~MIND_CONTENT','EXTWHERE':'"
					+ strWhere + "','TYPE':'single'}";
	var options = {"itemCode":inputName,"config" :configStr,"rebackCodes":inputName,"parHandler":this,
			"formHandler":this,"replaceCallBack":function(obj){
				jQuery("#" + inputName).val(obj.MIND_CONTENT + "(" + obj.MIND_VALUE + ")" );
			},"hideAdvancedSearch":true};
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(null,[50,0]);	
	
}

/**
 * 取消固定意见的默认值
 */
function cancelRegularDefault() {
	jQuery("#MIND_REGULAR_CONTENT").val("");
}

/**
 * 打开部门选择和角色选择的树
 */
function openTreeDialogDept(inputName, radioInputId) {
		var configStr = "SY_ORG_DEPT_ALL, {'TYPE':'multi'}";
		var extendTreeSetting = {'cascadecheck':false,'checkParent':false};
		var options = {"itemCode":inputName,"config" : configStr,"hide":"explode","show":"blind",
		"extendDicSetting":extendTreeSetting,
		"replaceCallBack":function(id,value){
			jQuery("#" + inputName).val(id.join(","));
			jQuery("#" + inputName+ "__NAME").val(value.join(","));
			if(radioInputId){
				jQuery("#" + radioInputId).attr("checked","true");
			}

		}};
		var dictView = new rh.vi.rhDictTreeView(options);
		dictView.show();
		//如果没有选中，表示原有的数据和现有数据是不相配的
		if(!jQuery("#" + radioInputId).attr("checked")){
			jQuery("#" + inputName).val("");
		}
}

/**
 * 打开人员选择和角色选择的树
 */
function  openTreeDialog(inputName, formTableName,radioInputId) {
		var configStr = formTableName + ",{'TYPE':'multi','rtnLeaf':true}";
		var extendTreeSetting = {'cascadecheck':true,'checkParent':false};
		var options = {"itemCode":inputName,"config" : configStr,"hide":"explode","show":"blind",
		"extendDicSetting":extendTreeSetting,
		"replaceCallBack":function(id,value){
			jQuery("#" + inputName).val(id.join(","));
			jQuery("#" + inputName+ "__NAME").val(value.join(","));
			if(radioInputId){
				jQuery("#" + radioInputId).attr("checked","true");
			}

		}};
		var dictView = new rh.vi.rhDictTreeView(options);
		dictView.show();
		//如果没有选中，表示原有的数据和现有数据是不相配的
		if(!jQuery("#" + radioInputId).attr("checked")){
			jQuery("#" + inputName).val("");
		}
}

function addDrafter(inputName,radioInputId) {
	jQuery("#" + inputName).val("draftUser");
//	jQuery("#" + inputName+ "__NAME").val("起草人");
	jQuery("#" + inputName+ "__NAME").val(Language.transStatic("nodeJs_string9"));
	if(radioInputId){
		jQuery("#" + radioInputId).attr("checked","true");
	}
}

function cancelSelect(inputName) {
    jQuery("#" + inputName).val("");
    jQuery("#" + inputName + "__NAME").val("");
    
    //如果是动态添加的普通意见，则一并删除这一行
    if (jQuery("#" + inputName).parent().parent().hasClass("mindUsualAdd")) {
//    	if (confirm("是否删除该行?")) {
    	if (confirm(Language.transStatic("nodeJs_string10"))) {	
    		jQuery("#" + inputName).parent().parent().remove();	
    	}
    }
}

function cancelRegular(inputName) {
    jQuery("#" + inputName).val("");
	jQuery("#" + inputName + "__NAME").val("");
}

/**
 * 处理按钮定义的查询选择
 * @param itemCode 字典的定义
 * @param isWfSelect 处理选择中的true， 
 */
function actBtnSelectListView(itemCode, isWfSelect) {
    var formTableName = $("#SERV_ID").val();
	var _configs = [{'servId':'SY_SERV_ACT_QUERY','SOURCE':'ACT_ID~ACT_CODE~ACT_NAME','TARGET':'~ACT_CODE~ACT_NAME','servName':'审批单按钮','HIDE':'ACT_ID','SHOWITEM':'ACT_NAME'},{'servId':'SY_WFE_PROC_DEF_ACT','SOURCE':'ACT_ID~ACT_CODE~ACT_NAME','TARGET':'~ACT_CODE~ACT_NAME','servName':'流程按钮','HIDE':'ACT_ID','SHOWITEM':'ACT_NAME'}];
	var options = {
		"configs" : _configs,
		"parHandler" : this,
		"formHandler" : this,
		"params" : {
			"SRC_SERV_ID" : formTableName
		},
		"replaceCallBack" : function(values) {
			jQuery.each(values, function(index, item){
				var actCode = "ACT-" + item.ACT_CODE; //审批单按钮
				if (item.sId == "SY_WFE_PROC_DEF_ACT") { //流程按钮
					actCode = "ACTP-" + item.ACT_CODE;
				}
				if (isWfSelect) {
					wfSelect.appendBtnInActTd(itemCode, actCode, item.ACT_NAME);	
				} else {
					confirmBtnAdd(actCode, item.ACT_NAME);
				}
			});
		}
	};
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show();	
}


function getActBtnDivContent(actCode, actName, actCondition) {
	if(actCondition==undefined) {
		actCondition = "";
	}
	var newActBtn = "<div class=' actBtnClass' actCode='" + actCode + "'>" 
			+ "<div class='fl row_actCode'>" + actCode + "</div>"
			+ "<div class='fl row_actName'><input type='text' name='actName' class='wp100' value='" + actName + "'></div>"
			+ "<div class='fl row_clsName'><input type='text' name='actCondition' class='wp100' value='" + actCondition + "'></div>"
			+ "<div class='fl row_opt'>" + "[<a class='cp' href='#' onclick='wfSelect.removeItem(jQuery(this).parent())'>删除</a>]</div>";
			+ "</div>";
	return newActBtn;
}


/**
 * 基础信息中的添加ACT按钮
 */
function confirmBtnAdd(actCode, actName, actCondition) {
	var existActCode = false;
	jQuery("#DELIVER_ACT_DEF").find(".actBtnClass").each(function(k, item){
		var actBtnObj = jQuery(this);
		var actCodeOld = actBtnObj.attr("actCode");
		
		if (actCodeOld == actCode) { //已经存在
			existActCode = true;
		}
	});
	if (!existActCode) {
		var newActBtn = getActBtnDivContent(actCode, actName, actCondition);
		
		jQuery(newActBtn).appendTo(jQuery("#DELIVER_ACT_DEF"));
	}
}


/**自动选中部门预定义的值**/
jQuery(document).ready(function(){
	jQuery("#nodeDeptYuding").change(function(){
		jQuery("#NODE_DEPT_MODE_3").attr("checked","true");
	});
});

/**
* 关闭窗口
**/
jQuery(window).unload(function(){
	//&& confirm("是否保存？")
	if(!_winClosed){
		//如果不是关闭状态，则
		confirmall(false);
	}
}); 

/**
 * 扩展字段的处理，扩展字段的内容，保存到大字段 EXT_JSON中去
 */
var extJsonObj = {
	init : function(nodeObj, defVal){
		var defValObj = defVal;
		//意见默认值
		if (defValObj) {
			$("#MIND_CONTENT").val(defValObj.MIND_CONTENT);
			$("#MIND_TERMINAL_CONTENT").val(defValObj.MIND_TERMINAL_CONTENT);
			$("#MIND_REGULAR_CONTENT").val(defValObj.MIND_REGULAR_CONTENT);	
			
			//是否显示阅知
			$("#SHOW_YUE_ZHI").val(defValObj.SHOW_YUE_ZHI);
			if(defValObj.SHOW_YUE_ZHI=="1"){
				$("#SHOW_YUE_ZHI").attr("checked",true);
			}
			$("#RETURN2DRAFTER").attr("checked", (defValObj.RETURN2DRAFTER == "1"));
			$("#RETURN2DRAFTER_NAME").val(defValObj.RETURN2DRAFTER_NAME);
			
			$("#FORM_DATA_EXT_CLS").val(defValObj.FORM_DATA_EXT_CLS);
			$("#DEVIDE_TWO_NODE").val(defValObj.DEVIDE_TWO_NODE);
			
			if(defValObj.FREE_NODE == "1"){
				$("#FREE_NODE").attr("checked",true);
			}
			
			//环节类型
			$("#HUANJIE").val(defValObj.HUANJIE);
			
			// 多人竞争
			$("#MULT_USER").attr("checked",(defValObj.MULT_USER=="1"));
			
			// 自动选中
			$("#AUTO_SELECT").val(defValObj.AUTO_SELECT);

			// 自动流转
			$("#AUTOFLOW_CLS").val(defValObj.AUTOFLOW_CLS);

			// 启用待办提醒，默认为true
			var enableTodoRemind = true;
			if(defValObj.ENABLE_TODO_REMIND != undefined) {
				if(defValObj.ENABLE_TODO_REMIND == "2") {
					enableTodoRemind = false;
				}
			}
			$("#ENABLE_TODO_REMIND").attr("checked", enableTodoRemind);
			

			if (defValObj.DELIVER_ACT_DEF) {
				jQuery.each(defValObj.DELIVER_ACT_DEF, function(m, item){
					confirmBtnAdd(item.ACT_CODE, item.ACT_NAME, item.ACT_CONDITION);
				});
			}
		}

		//处理完毕的字段处理
		var fileCompelte = "";
		if (defValObj && defValObj['FIELD_COMPLETE']) {
			fileCompelte = defValObj['FIELD_COMPLETE'];
		}
		workCompleteField.init(nodeObj, fileCompelte);
		
		var wfSelectStr = "";
		if (defValObj && defValObj['S_WF_SELECT']) {
			wfSelectStr = defValObj['S_WF_SELECT'];
		}
		
		var wfSelectBase = "";
		if (defValObj && defValObj['S_WF_SELECT_BASE']) {
			wfSelectBase = defValObj['S_WF_SELECT_BASE'];
		}
		
		wfSelect.init(wfSelectStr, wfSelectBase);
		
		var mindAddStr = "";
		if (defValObj && defValObj['MIND_PUTONG_ADD']) {
			mindAddStr = defValObj['MIND_PUTONG_ADD'];
		}
		
		mindAdded.init(mindAddStr);
	},
	saveDef : function() {
		var extJsonSave = {};
		extJsonSave.MIND_CONTENT = $("#MIND_CONTENT").val();
		extJsonSave.MIND_TERMINAL_CONTENT = $("#MIND_TERMINAL_CONTENT").val();
		extJsonSave.MIND_REGULAR_CONTENT = $("#MIND_REGULAR_CONTENT").val();
		extJsonSave.SHOW_YUE_ZHI = $("#SHOW_YUE_ZHI").val();
		extJsonSave.RETURN2DRAFTER = $("#RETURN2DRAFTER").attr("checked")?"1":"2";
		extJsonSave.RETURN2DRAFTER_NAME = $("#RETURN2DRAFTER_NAME").val();
		if (extJsonSave.RETURN2DRAFTER == "1") {
			if ($("#RETURN2DRAFTER_NAME").val() == null || $("#RETURN2DRAFTER_NAME").val() == "") {
				alert("按钮名称不能为空");
				$("#RETURN2DRAFTER_NAME").focus();
				throw "按钮名称不能为空";
			}
		}
		
		extJsonSave.FORM_DATA_EXT_CLS = $("#FORM_DATA_EXT_CLS").val();
		extJsonSave.DEVIDE_TWO_NODE = $("#DEVIDE_TWO_NODE").val();
		extJsonSave.FIELD_COMPLETE = workCompleteField.saveDef();
		extJsonSave.S_WF_SELECT_BASE = wfSelect.saveBaseDef();
		extJsonSave.S_WF_SELECT = wfSelect.saveDef();
		extJsonSave.MIND_PUTONG_ADD = mindAdded.saveDef();
		extJsonSave.FREE_NODE = $("#FREE_NODE").attr("checked")?1:2;

		var actBtnArray = new Array();  //定义的ACT-button
		jQuery("#DELIVER_ACT_DEF").find(".actBtnClass").each(function(k, item){
			var actBtnObj = jQuery(this);
			var actCode = actBtnObj.attr("actCode");
			var actCondition = jQuery(this).find("[name=actCondition]").attr("value");
			var actName = jQuery(this).find("[name=actName]").attr("value");
			
			actBtnArray.push({"ACT_CODE":actCode, "ACT_NAME": actName, "ACT_CONDITION": actCondition});
		});
		
		extJsonSave.DELIVER_ACT_DEF = actBtnArray; //送交的ACT的定义
		extJsonSave.HUANJIE = $("#HUANJIE").val(); //环节的input
		extJsonSave.MULT_USER = $("#MULT_USER").attr("checked")?"1":"2"; // 多人竞争
		extJsonSave.AUTO_SELECT = $("#AUTO_SELECT").val(); // 自动选中用户
		// 自动流转
		extJsonSave.AUTOFLOW_CLS = $("#AUTOFLOW_CLS").val();
		// 启用待办提醒
		extJsonSave.ENABLE_TODO_REMIND = $("#ENABLE_TODO_REMIND").attr("checked")?"1":"2";
		
		return JSON.stringify(extJsonSave);
	}
};

/**
 * 处理完毕页面字段的处理
 * {TITLE:{'MUST':'1','VALUE':'xxx'},S_MTIME={}}
 */
var workCompleteField = {
	init : function(nodeObj, defValStr) {
		jQuery(document).ready(function() {
			jQuery("#btnAddFieldComp").click(function() {
				workCompleteField.addItem();
			});
		});
		
		var tableview = $("#tblFieldComplete");
	    var servDef = nodeObj.servDef;

	    try {
			jQuery(defValStr).each(function(index, itemDefVal){
				var str = "<tr id='"+itemDefVal.ITEM_CODE+"' nameValue='"+itemDefVal.ITEM_NAME+"'><td align='right'>"+itemDefVal.ITEM_NAME+"("+itemDefVal.ITEM_CODE+")</td>"+
		          "<td><input type='checkbox' id='checkMust"+itemDefVal.ITEM_CODE+"' value=''></td>"+
		          "<td><input type='text' id='defVal"+itemDefVal.ITEM_CODE+"' value=''></td>"+ 
//		          "<td onclick='workCompleteField.removeItem(this)'><a style='cursor: pointer;'>删除</a></td></tr>";
		          "<td onclick='workCompleteField.removeItem(this)'><a style='cursor: pointer;'>"+Language.transStatic('rh_ui_card_string22')+"</a></td></tr>";	
				tableview.append(str);

				//初始化值 , 必填
				if (itemDefVal.MUST == 1) {
				    $("#checkMust"+itemDefVal.ITEM_CODE).attr("checked",true); 
				}
				//默认值
				if (itemDefVal.VALUE) {
				    $("#defVal"+itemDefVal.ITEM_CODE).val(itemDefVal.VALUE); 
				}			
			});	    
	    } catch (e) {
	    	//alert(e.message());
	    }
	}, 
	addItem : function() {
	    var servIdName = $("#SERV_ID").val();
		var servPidName = $("#SERV_PID").val();
		
		var params = {"SRC_SERV_ID":servIdName};
		
		var configStr = "SY_SERV_ITEM_QUERY,{'TARGET':'AA_FIELD_COM~','SOURCE':'ITEM_CODE~ITEM_NAME','TYPE':'multi'}";
		var options = {"itemCode":"AA_FIELD_COM","config" :configStr,"rebackCodes":"AA_FIELD_COM","parHandler":this,"formHandler":this,"replaceCallBack":function(itemDefs){
			var itemCodeArray = itemDefs.ITEM_CODE.split(",");
			var itemNameArray = itemDefs.ITEM_NAME.split(",");
			
			for (var j=0;j<itemCodeArray.length;j++) {
				var rows = "<tr id='"+itemCodeArray[j]+"' nameValue='"+itemNameArray[j]+"'><td align='right'>"+itemNameArray[j]+"("+itemCodeArray[j]+")</td>"+
		          "<td><input type='checkbox' id='checkMust"+itemCodeArray[j]+"' value=''></td>"+
		          "<td><input type='text' id='defVal"+itemCodeArray[j]+"' value=''></td>"+ 
//		          "<td onclick='workCompleteField.removeItem(this)'><a style='cursor: pointer;'>删除</a></td></tr>";
				  "<td onclick='workCompleteField.removeItem(this)'><a style='cursor: pointer;'>"+Language.transStatic('rh_ui_card_string22')+"</a></td></tr>";
				jQuery("#tblFieldComplete").append(rows);					
			}
		},"hideAdvancedSearch":true,"params":params};
		var queryView = new rh.vi.rhSelectListView(options);
		queryView.show(null,[50,0]);		
	},
	removeItem : function(obj) {
		jQuery(obj).parent().remove();
	},
	saveDef : function() {
		var fieldObjs = new Array();
		$("#tblFieldComplete tr").each(function(j,m) {
		    if (jQuery(m).hasClass("topTr")) {
			   return;
			}
			var trObj = jQuery(this);
			
			var itemCode = trObj.attr('id');
			var itemName = trObj.attr('nameValue');
			var defaultValue = $("#defVal" + itemCode).val();
			var fieldMust = 2;
			
			if (trObj.find("input[type='checkbox']:checked").length > 0) {
				fieldMust = 1;
			}
			
			var fieldObj = {"ITEM_CODE": itemCode, "MUST": fieldMust, "VALUE": defaultValue,"ITEM_NAME":itemName};
			
			fieldObjs.push(fieldObj);
		});
		
		return fieldObjs;
	}
};

/**
 * 处理选择
 */
var wfSelect = {
	init : function(defValStr, selectBase) {
		jQuery(document).ready(function() {
			jQuery("#btnAddSWfSelect").click(function() {
				wfSelect.addItem();
			});
		});
		//上面的基础信息
		jQuery("#wfselectLabelText").val(selectBase.LABEL_TEXT);
		jQuery("#wfselectFieldCode").val(selectBase.FIELD_CODE);
		jQuery("#wfselectDictCode").val(selectBase.DICT_CODE);
		jQuery("#wfselectType").val(selectBase.TYPE_CODE);
		
		//下面的表格
		jQuery(defValStr).each(function(index, itemDefVal){
			var itemCode = itemDefVal.ITEM_CODE;
			var rows = "<tr id='"+itemCode+"' nameValue='"+itemDefVal.ITEM_NAME+"'><td align='right'>" + itemDefVal.ITEM_NAME + "</td>"+
			  "<td id='defValNodeId"+itemCode+"'>&nbsp;<a class='fr cp' href='#' onclick='wfSelect.addNodeForWfSelect("+itemCode+")'>添加</a></td>"+
			  "<td id='defValActId"+itemCode+"'>&nbsp;<a class='fr cp' href='#' onclick='wfSelect.addActBtn("+itemCode+")'>添加</a></td>"+ 
			  "<td><input type='text' size='10' id='defValBtn"+itemCode+"' value=''></td>"+ 
//	          "<td onclick='wfSelect.removeItem(this)'><a style='cursor: pointer;'>删除</a></td></tr>";
			  "<td onclick='wfSelect.removeItem(this)'><a style='cursor: pointer;'>"+Language.transStatic('rh_ui_card_string22')+"</a></td></tr>";
			jQuery("#tblSWfSelect").append(rows);
			//可送交点
			if (itemDefVal.VALUE_NODE) {
				var nodeCodes = itemDefVal.VALUE_NODE.split(",");
				var nodeNames = itemDefVal.VALUE_NODE_NAME.split(",");
				for (var i=0;i<nodeCodes.length;i++) {
					wfSelect.appendNextNodeTd(itemCode, nodeCodes[i], nodeNames[i]);	
				}
			}
			
			//按钮名称
			if (itemDefVal.VALUE_BTN) {
			    $("#defValBtn"+itemCode).val(itemDefVal.VALUE_BTN); 
			}
			
			jQuery.each(itemDefVal.ACT_BTN_LIST, function(m, item){
				wfSelect.appendBtnInActTd(itemCode, item.ACT_CODE, item.ACT_NAME);	
			});
		});
	},
	addItem : function() {
		var dictCode = jQuery("#wfselectDictCode").val();
		
		if (dictCode.length == 0) {
//			alert("请先选择处理选择的基本信息。");
			alert(Language.transStatic("nodeJs_string11"));
			return;
		}
		
		var configStr = dictCode + ",{'TYPE':'multi','rtnLeaf':true}";
		var extendTreeSetting = {'cascadecheck':true,'checkParent':false};
		var options = {"itemCode":"wfeSelect","config" : configStr,"hide":"explode","show":"blind",
		"extendDicSetting":extendTreeSetting,
		"replaceCallBack":function(ids, values){
			
			jQuery.each(ids, function(i, item){
//				var defaultValueBtn = "确定";
				var defaultValueBtn = Language.transStatic("rh_ui_gridCard_string17");
				if (item == 10) {
//					defaultValueBtn = "签名";
					defaultValueBtn = Language.transStatic("nodeJs_string12");
				}
				
				var rows = "<tr id='"+item+"' nameValue='"+values[i]+"'><td align='right'>" + values[i] + "</td>"+
//		          "<td id='defValNodeId"+item+"'>&nbsp;<a class='fr cp' href='#' onclick='wfSelect.addNodeForWfSelect("+item+")'>添加</a></td>"+
//		          "<td id='defValActId"+item+"'>&nbsp;<a class='fr cp' href='#' onclick='wfSelect.addActBtn("+item+")'>添加</a></td>"+
//		          "<td><input type='text' size='10' id='defValBtn"+item+"' value='"+defaultValueBtn+"'></td>"+ 
//		          "<td onclick='wfSelect.removeItem(this)'><a style='cursor: pointer;'>删除</a></td></tr>";
				  "<td id='defValNodeId"+item+"'>&nbsp;<a class='fr cp' href='#' onclick='wfSelect.addNodeForWfSelect("+item+")'>"+Language.transStatic('nodeJs_string13')+"</a></td>"+
		          "<td id='defValActId"+item+"'>&nbsp;<a class='fr cp' href='#' onclick='wfSelect.addActBtn("+item+")'>"+Language.transStatic('nodeJs_string13')+"</a></td>"+
		          "<td><input type='text' size='10' id='defValBtn"+item+"' value='"+defaultValueBtn+"'></td>"+ 
		          "<td onclick='wfSelect.removeItem(this)'><a style='cursor: pointer;'>"+Language.transStatic('rh_ui_card_string22')+"</a></td></tr>";
				jQuery("#tblSWfSelect").append(rows);
			});
		}};
		var dictView = new rh.vi.rhDictTreeView(options);
		dictView.show();
	},
	addNodeForWfSelect : function(itemCode) {
		var procCode = $("#PROC_CODE").val();
		var curNodeCode = $("#NODE_CODE").val();
		var multiNodeAttr = $("#DEVIDE_TWO_NODE").val();
		var inputName = "wfSelect_node";

		var params = {"QUERY_NODE_CAN_TO":"1","PROC_CODE":procCode, "NODE_CODE":curNodeCode, "MULTI_NODE_ATTR":multiNodeAttr};
		
		var configStr = "SY_WFE_NODE_DEF,{'TARGET':'" + inputName + 
						"~','SOURCE':'NODE_CODE~NODE_NAME','EXTWHERE':'','TYPE':'multi'}";
		var options = {"itemCode":inputName,"config" :configStr,"rebackCodes":inputName,"parHandler":this,
				"formHandler":this,"replaceCallBack":function(obj){
					var nodeCodes = obj.NODE_CODE.split(",");
					var nodeNames = obj.NODE_NAME.split(",");
					for (var i=0;i<nodeCodes.length;i++) {
						wfSelect.appendNextNodeTd(itemCode, nodeCodes[i], nodeNames[i]);	
					}
				},"hideAdvancedSearch":true, "params" : params};
		var queryView = new rh.vi.rhSelectListView(options);
		queryView.show(null,[50,0]);
	},
	delNodeForWfSelect : function(itemCode) {
		jQuery("#defValNodeName" + itemCode).val("");
		jQuery("#defValNode" + itemCode).val("");
	},
	addActBtn : function(itemCode) {
		actBtnSelectListView(itemCode, true);
	},
	appendBtnInActTd : function (itemCode, actCode, actName) { //itemCode是字典的某一项的值
		//判断该actCode是否在页面上已经有了，如果有了则不再添加
		var existActCode = false;
		jQuery("#defValActId" + itemCode).find(".actBtnClass").each(function(k, item){
			var actBtnObj = jQuery(this);
			var actCodeOld = actBtnObj.attr("actCode");
			
			if (actCodeOld == actCode) { //已经存在
				existActCode = true;
			}
		});
		if (!existActCode) {
			var newActBtn = getActBtnDivContent(actCode, actName);
			
			jQuery(newActBtn).appendTo(jQuery("#defValActId" + itemCode));
		}
	},
	appendNextNodeTd : function (itemCode, nodeCode, nodeName) { //itemCode是字典的某一项的值
		var existNodeCode = false;
		jQuery("#defValNodeId" + itemCode).css({"padding":"5px 5px"}).find(".nextNodeClass").each(function(k, item){
			var nextNodeObj = jQuery(this);
			var nextNodeOld = nextNodeObj.attr("nodeCode");
			
			if (nextNodeOld == nodeCode) { //已经存在
				existNodeCode = true;
			}
		});
		if (!existNodeCode) {
			var nextNode = jQuery("<div class='fl nextNodeClass' style='padding:1px 1px;line-height:20px;float:left;' nodeCode='"+nodeCode+"' nodeName='"+nodeName+"'>"+nodeName+"("+nodeCode+")[<a class='cp deleteAct' href='#' onclick='wfSelect.removeItem(this)'>X</a>]&nbsp;</div>");
			nextNode.find(".deleteAct").hover(function(){
				nextNode.css({"background-color":"red"});
			}, function(){
				nextNode.css({"background-color":""});
			});
			nextNode.appendTo(jQuery("#defValNodeId" + itemCode));
		}
	},
	removeItem : function(obj){
		jQuery(obj).parent().remove();
	},
	removeWfSelectBase : function() { //删除处理选择的相关信息
		jQuery("#wfselectLabelText").val("");
		jQuery("#wfselectFieldCode").val("");
		jQuery("#wfselectDictCode").val("");
		jQuery("#wfselectType").val("");		
		
		$("#tblSWfSelect tr").each(function(j,m) {
		    if (jQuery(m).hasClass("topTr")) {
			   return;
			}
			var trObj = jQuery(this);
			
			trObj.remove();
		});		
	},
	saveDef : function(){
		var fieldObjs = new Array();
		$("#tblSWfSelect tr").each(function(j,m) {
		    if (jQuery(m).hasClass("topTr")) {
			   return;
			}
			var trObj = jQuery(this);
			
			var itemCode = trObj.attr('id');
			var itemName = trObj.attr('nameValue');
			var defValBtn = jQuery("#defValBtn" + itemCode).val();
			
			var nodeCodeArr = new Array();
			var nodeNameArr = new Array();
			trObj.find(".nextNodeClass").each(function(k, item){
				var nextNodeObj = jQuery(this);
				var nodeCode = nextNodeObj.attr("nodeCode");
				var nodeName = nextNodeObj.attr("nodeName");
				
				nodeCodeArr.push(nodeCode);
				nodeNameArr.push(nodeName);
			});
			var defValNode = nodeCodeArr.join(",");
			var defValNodeName = nodeNameArr.join(",");
			
			var fieldObj = {"ITEM_CODE": itemCode, "VALUE_BTN": defValBtn, "VALUE_NODE": defValNode, "VALUE_NODE_NAME" : defValNodeName, "ITEM_NAME":itemName};

			//ACT的定义信息
			var actBtnArray = new Array();
			trObj.find(".actBtnClass").each(function(k, item){
				var actBtnObj = jQuery(this);
				var actCode = actBtnObj.attr("actCode");
				var actName = actBtnObj.find(":input").val();
				
				actBtnArray.push({"ACT_CODE":actCode, "ACT_NAME": actName});
			});
			fieldObj.ACT_BTN_LIST = actBtnArray;
			
			fieldObjs.push(fieldObj);
		});
		
		return fieldObjs;
	},
	saveBaseDef : function() {
		var syWfSelectBase = {};
		
		syWfSelectBase.LABEL_TEXT = jQuery("#wfselectLabelText").val();
		syWfSelectBase.FIELD_CODE = jQuery("#wfselectFieldCode").val();
		syWfSelectBase.DICT_CODE = jQuery("#wfselectDictCode").val();
		syWfSelectBase.TYPE_CODE = jQuery("#wfselectType").val();	
		
		return syWfSelectBase;
	} 
};

/**
 * 动态添加的普通意见的初始化和保存
 */
var mindAdded = {
	init : function(defVal) { //初始化，显示到意见的表格里面去
		
		jQuery(defVal).each(function(index, itemDefVal){
			addMindCode();
			
			var usualMindNum = jQuery("#mindTableId").find(".mindUsualAdd").length;
			var mindCode = "MIND_CODE_" + usualMindNum;
			var mindMust = "MIND_MUST_" + usualMindNum;
			var mindScript = "MIND_SCRIPT_" + usualMindNum;
			var mindContent = "MIND_CONTENT_" + usualMindNum;
			
			jQuery("#" + mindCode).val(itemDefVal.MIND_CODE);
			jQuery("#" + mindCode + "__NAME").val(itemDefVal.MIND_CODE_NAME);
			jQuery("#" + mindMust).val(itemDefVal.MIND_NEED_FLAG);
			if(itemDefVal.MIND_NEED_FLAG=="1"){
				$("#" + mindMust).attr("checked",true);
			}
			
			jQuery("#" + mindScript).val(itemDefVal.MIND_SCRIPT);
			jQuery("#" + mindContent).val(itemDefVal.MIND_CONTENT);			
		});
	}, 
	saveDef : function () { //将表格中的数据获取，并返回
		var mindAddArray = new Array();
		jQuery("#mindTableId").find(".mindUsualAdd").each(function(key, item){
			var trObj = jQuery(this);
			
			key = key + 1;
			var mindCode = "MIND_CODE_" + key;
			var mindCodeName = mindCode + "__NAME";
			var mindScript = "MIND_SCRIPT_" + key;
			var mindContent = "MIND_CONTENT_" + key;
			
			var fieldMust = 2;
			if (trObj.find("input[type='checkbox']:checked").length > 0) {
				fieldMust = 1;
			}
			
			var fieldObj = {};
			fieldObj.MIND_CODE = jQuery("#" + mindCode).val();
			fieldObj.MIND_CODE_NAME = jQuery("#" + mindCodeName).val();
			fieldObj.MIND_NEED_FLAG = fieldMust;
			fieldObj.MIND_SCRIPT = jQuery("#" + mindScript).val();
			fieldObj.MIND_CONTENT = jQuery("#" + mindContent).val();
			
			mindAddArray.push(fieldObj);
		});
		
		return mindAddArray;
	}
}


/**
 * 自定义变量功能常用方法
 */
var customVar = {
	init : function(defVal){
		var objVal = eval(defVal);
		/**为自定义变量的增加按钮绑定点击事件**/
		jQuery(document).ready(function(){
			jQuery("#btnCustomVarAdd").bind("click",function(){
				customVar.addVar();
			});
		});
		
		//初始化数据
		jQuery(objVal).each(function(index,obj){
			customVar.addVar(obj.VAR_CODE,obj.VAR_CONTENT,obj.VAR_MEMO);
		});

	},
	/** 增加客户端自定义变量 **/
	addVar : function(varName,varContent,varMemo){
		varName = varName || "";
		varContent = varContent || "";
		varMemo = varMemo || "";
		var rows = "<tr class='varList'><td>"
				 + "<input type='text' name='VAR_CODE' value='" + varName + "' class='wp h' />"
				 + "</td><td>"
				 + "<textarea name='VAR_CONTENT' class='wp' rows='3'>" + varContent + "</textarea>"
				 + "</td><td>"
				 + "<textarea name='VAR_MEMO' class='wp' rows='3'>" + varMemo + "</textarea>"
//				 + "</td><td onclick='customVar.removeVar(this)'><a>删除</a></td></tr>";
		 		 + "</td><td onclick='customVar.removeVar(this)'><a>"+Language.transStatic('rh_ui_card_string22')+"</a></td></tr>";
		
		jQuery("#tblCustomVarAdd").append(rows);
	},
	 /**删除指定变量**/
	removeVar : function(obj){
		jQuery(obj).parent().remove();
	},
	/**取得保存到服务器端的数据**/
	saveDef : function(){
		var def = new Array();
		jQuery("#tblCustomVarAdd").find("tr.varList").each(function(index,obj){
			var item = {
				VAR_CODE : jQuery(obj).find("input[name=VAR_CODE]").val(),
				VAR_CONTENT : jQuery(obj).find("textarea[name=VAR_CONTENT]").val(),
				VAR_MEMO : jQuery(obj).find("textarea[name=VAR_MEMO]").val()
			};
			def.push(item);
			
		});
		return def;
	}
};

/**
 *编码json对象 
 */
var DataEncode = {
	encode : function(obj) {
		if( typeof (obj) == "object" || typeof (obj) == "array") {
			return Hex.encode(jQuery.toJSON(obj));
		}

		return Hex.encode(obj);
	},
	decode : function(obj) {
		if(typeof(obj) == "string"){
			if(obj == ""){
				return {};	
			}
			var orgStr = Hex.decode(obj);
			return jQuery.evalJSON(orgStr);
		}
		return obj;
	}
}; 


/**更新数据**/
var dataUpdater = {
	init : function(defVal) {
		jQuery(document).ready(function() {
			jQuery("#btnDataUpdate").click(function() {
				dataUpdater.addItem();
			});
		});
		
		if(defVal){
			defVal = Hex.decode(defVal);
			if(typeof(defVal) == 'string'){
				try{
					var list = eval(defVal);
					//初始化数据
					jQuery(list).each(function(index,obj){
						dataUpdater.addItem(obj.UPDATE_MOMENT,obj.UPDATE_CONDS,obj.UPDATE_FIELD,obj.UPDATE_VALUE);
					});
				}catch(e){
					
				}
			}

		}

	},
	addItem : function(moment,conds,field,value) {
		moment = moment || "";
		conds = conds || "";
		field = field || "";
		value = value || "";
		var row = new Array();
		row.push("<tr class='expressList'><td><select name='UPDATE_MOMENT'>");
//		row.push("<option value='ENTER'>进入节点</option>");
//		row.push("<option value='FINISH'>结束节点</option>");
//		row.push("<option value='MIND'>保存意见</option>");
//		row.push("<option value='VIEW'>查看审批单</option>");
		row.push("<option value='ENTER'>"+Language.transStatic('nodeJs_string14')+"</option>");
		row.push("<option value='FINISH'>"+Language.transStatic('nodeJs_string15')+"</option>");
		row.push("<option value='MIND'>"+Language.transStatic('nodeJs_string16')+"</option>");
		row.push("<option value='VIEW'>"+Language.transStatic('nodeJs_string17')+"</option>");
		row.push("</select></td><td>");
		row.push("<textarea name='UPDATE_CONDS' class='wp' rows='3'>" + conds + "</textarea>");
		row.push("</td><td>");
		row.push("<textarea name='UPDATE_FIELD' class='wp' rows='3'>"  + field + "</textarea>");
		row.push("</td><td>");
		row.push("<textarea name='UPDATE_VALUE' class='wp' rows='3'>" + value + "</textarea>");
//		row.push("</td><td onclick='dataUpdater.removeItem(this)'><a>删除</a></td></tr>");
		row.push("</td><td onclick='dataUpdater.removeItem(this)'><a>"+Language.transStatic('rh_ui_card_string22')+"</a></td></tr>");
		
		jQuery(row.join("")).appendTo(jQuery("#dataUpdateTable")).find("select[name=UPDATE_MOMENT]").val(moment);
	},
	/**删除指定行**/
	removeItem : function(colObj) {
		jQuery(colObj).parent().remove();
	},
	/**取得保存到服务器端的数据**/
	saveDef : function() {
		var def = new Array();
		jQuery("#dataUpdateTable").find("tr.expressList").each(function(index,obj){
			var item = {
				UPDATE_MOMENT :jQuery(obj).find("select[name=UPDATE_MOMENT]").val(),
				UPDATE_CONDS : jQuery(obj).find("textarea[name=UPDATE_CONDS]").val(),
				UPDATE_FIELD : jQuery(obj).find("textarea[name=UPDATE_FIELD]").val(),
				UPDATE_VALUE : jQuery(obj).find("textarea[name=UPDATE_VALUE]").val()
			};
			def.push(item);
			
		});
		return Hex.encode(jQuery.toJSON(def));
	}
};
