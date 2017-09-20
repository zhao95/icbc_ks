var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
//每一行添加编辑和删除
$("#TS_XMGL_BMSH_SHGZ_MX .rhGrid").find("tr").each(function(index, item) {
	if(index != 0) {
		var value1 = $('td[icode="MX_VALUE1"]',item).text();
		
		var val = $('td[icode="MX_VALUE2"]',item).text();
		
		if(value1 == 1 && val.length >0) {
			
			var dataId = item.id;
			
			var name = $('td[icode="MX_NAME"]',item).text();
			
			var val2 = val.replace(/\'/ig,"\"");
			
			var jsonv2 = JSON.parse(val2);
			
			var newName = name.replace("#"+jsonv2.vari+"#",jsonv2.val);
			
			$('td[icode="MX_NAME"]',item).text(newName);
		
			$(item).find("td[icode='BUTTONS']").append(
					'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_BMSH_SHGZ_MX-setting" actcode="setting" rowpk="'+dataId+'" rowjson="'+val+'" rowname="'+name+'" >'+
					'<span class="rh-icon-inner-notext"></span><span class="rh-icon-img btn-change"></span></a>'
					);
			// 为每个按钮绑定卡片
			bindCard();
		}
	}
});

//绑定的事件     
function bindCard() {
	
	//当行编辑事件
	jQuery("td [id='TS_XMGL_BMSH_SHGZ_MX-setting']").unbind("click").bind("click", function() {
		
		var dataId = jQuery(this).attr("rowpk");
		var name = jQuery(this).attr("rowname");
		var jsonStr = jQuery(this).attr("rowjson").replace(/\'/ig,"\"");
		var json = JSON.parse(jsonStr);
		
		var newName = name.replace("#"+json.vari+"#",json.val);
		
		var dialogCon = "setting-dialog-"+dataId;
		
		getDialog(dialogCon,newName,600,400);
		
		var input8 = $('<input type="text" id="RULE-VAR-INPUT" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(json.val);
		
		if(json.type == 'int') {
			
			input8.addClass("ui-text-default");
			
			input8.css("width","50px");
			
		} else if(json.type == 'date') {
			
			input8.addClass("Wdate ui-date-default").css("cursor","pointer");
			
			input8.css("width","150px");
			
			input8.attr("onfocus","WdatePicker({startDate:\'%y%MM%dd\',dateFmt:\'yyyyMMdd\',alwaysUseStartDate:false})");
			
		} else {
			
			input8.addClass("ui-text-default");
			
			input8.css("width","150px");
		}
		
		var formConDiv7 = $('<div class="formContent style="width:100%;">');
		
		var nameArg = name.split("#"+json.vari+"#");
		
		for(var i=0;i<nameArg.length;i++) {
			if(i==0) {
				formConDiv7.append(nameArg[0]);
			} else if(i==1) {
				formConDiv7.append(input8);
				formConDiv7.append(nameArg[1]);
			} else {
				formConDiv7.append(nameArg[i]);
			}
		}
		
		var fieldset6 = $('<fieldset>').append(formConDiv7);
		
		var fieldsetCon5 = $('<div class="fieldsetContainer">').append(fieldset6);
		
		var itemDiv4 = $('<div class="item ui-corner-5" id="">').append(fieldsetCon5);
		
		var formDiv3 = $('<div class="ui-form-default">').append(itemDiv4);
		
		var formCon2 = $('<div class="form-container" style="margin-bottom: 0px;">').append(formDiv3);
		
		var saveBtn2_1 = $('<a class="rh-icon rhGrid-btnBar-a" id="SETTING-saveRuleVar" actcode="saveRuleVar" title="" order="50">').append('<span class="rh-icon-inner">保存</span><span class="rh-icon-img btn-save"></span>');
		
		var btnBar2 = $('<div class="rhCard-btnBar">').append(saveBtn2_1);
		
		var mainTab = $('<div class="rhCard-mainTab ui-tabs-panel ui-widget-content" id="TEST-mainTab">').append(btnBar2,formCon2);
		
		$("#"+dialogCon).css("background-color","#F6F6F6").append(mainTab);
		
		$("#SETTING-saveRuleVar").bind("click",function() {
			saveRuleVar(dataId,input8.val(),json);
		});

	});
}


function saveRuleVar(dataId,val,json) {
	if(json.type == 'int') {
		var ival = parseInt(val);
	    if(isNaN(ival)){
	    	alert("请输入数字!");
			return;
	    }
	}
	json.val = val;
	
	var param = {};
	
	param['MX_ID'] = dataId;
	param['_PK_'] = dataId;
	param['MX_VALUE2'] = JSON.stringify(json).replace(/\"/ig,"\'");
	
//	console.log(param);
	
	//false表示成功不提示。2表示成功、失败都不提示
	var tipFlag = false;
	var result = FireFly.doAct(_viewer.servId, "save", param, tipFlag, false,function(data){
		
		if(data._MSG_.indexOf("OK")!= -1) {
			_viewer.refresh();
			var dialogId = "setting-dialog-"+dataId;
			jQuery("#" + dialogId).remove();
		}
		
	});
	
	

}

_viewer.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
};