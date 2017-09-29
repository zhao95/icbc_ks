var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
//每一行添加编辑和删除
$("#TS_XMGL_BMSH_SHGZ_MX .rhGrid").find("tr").each(function(index, item) {
	if(index != 0) {
		debugger;
		var value1 = $('td[icode="MX_VALUE1"]',item).text();
		var val = $('td[icode="MX_VALUE2"]',item).text();
		if(value1 == 1 && val.length >0) {
				 obj2=eval(val);  
			
			var dataId = item.id;
			var name = $('td[icode="MX_NAME"]',item).text();
			var newName = "";
			for(var i =0;i<obj2.length;i++){
				var objd = obj2[i];
				/*obj2[i].replace(/\'/ig,"\"");*/
				name = name.replace("#"+objd.vari+"#",objd.val);
				newName = name;
			}
			 name = $('td[icode="MX_NAME"]',item).text();
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
		var jsonStr = jQuery(this).attr("rowjson");
		var obj2=eval("("+jsonStr+")"); 
		var obj2=eval("("+jsonStr+")"); 
			var newName = "";
			debugger;
			for(var i =0;i<obj2.length;i++){
				var objd = obj2[i];
				name = name.replace("#"+objd.vari+"#",objd.val);
				newName = name;
			}
			var name = jQuery(this).attr("rowname");
		var dialogCon = "setting-dialog-"+dataId;
		
		getDialog(dialogCon,newName,600,400);
		
		var input8 = $('<input type="text" id="RULE-VAR-INPUT" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(obj2[0].val);
		
		if(obj2[0].type == 'int') {
			
			input8.addClass("ui-text-default");
			
			input8.css("width","50px");
			
		} else if(obj2[0].type == 'date') {
			
			input8.addClass("Wdate ui-date-default").css("cursor","pointer");
			
			input8.css("width","150px");
			
			input8.attr("onfocus","WdatePicker({startDate:\'%y%MM%dd\',dateFmt:\'yyyyMMdd\',alwaysUseStartDate:false})");
			
		} else {
			
			input8.addClass("ui-text-default");
			
			input8.css("width","150px");
		}
		
		var formConDiv7 = $('<div class="formContent style="width:100%;">');
		
		var nameArg = name.split("#"+obj2[0].vari+"#");
		debugger;
		if(obj2.length>1){
			for(var i=0;i<nameArg.length;i++) {
				var inputxx ="";
				if(i<4){
					inputxx= $('<input type="text" name="inputaa" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(obj2[i].val);
					
					inputxx.addClass("ui-text-default");
					inputxx.css("width","150px");
				}
				if(i==0) {
					formConDiv7.append(inputxx);
				} else if(i==1) {
					formConDiv7.append(nameArg[1]);
					formConDiv7.append(inputxx);
				} else if(i==2){
					formConDiv7.append(nameArg[i]);
					formConDiv7.append(inputxx);
				} else if(i==3){
					formConDiv7.append(nameArg[i]);
					formConDiv7.append(inputxx);
				} 
			}
		}else{
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
			if(obj2.length>1){
				var arr = [];
				$("input[name='inputaa']").each(function(index,item){
					arr[index]=$(this).val();
				})
				saveRuleVar(dataId,arr,jsonStr);
			}else{
				
				saveRuleVar(dataId,input8.val(),jsonStr);
			}
		});

	});
}


function saveRuleVar(dataId,val,obj2) {
	debugger;
	obj2 = eval(obj2);
	if(obj2[0].type == 'int') {
		var ival = parseInt(val);
	    if(isNaN(ival)){
	    	alert("请输入数字!");
			return;
	    }
	}
	if(obj2.length==1){
		obj2[0].val=val;
	}else{
		for(var i=0;i<obj2.length;i++){
			obj2[i].val = val[i];
			}
	}
	var param = {};
	
	param['MX_ID'] = dataId;
	param['_PK_'] = dataId;
	obj2=JSON.stringify(obj2)
	var ssss = obj2.split(",");
	var paramstr = "";
	for(var i=0;i<ssss.length;i++){
		var sss=ssss[i].split(":");
		for(var j=0;j<sss.length;j++){
			if(j==0){
				sss[0] = sss[0].replace(/\"/g, "");
				paramstr+=sss[j]+":";
			}else{
				sss[1] = sss[1].replace(/\"/g, "'");
				paramstr+=sss[j];
			}
		}
		if(i!=ssss.length-1){
			paramstr+=","
		}
	}
	param['MX_VALUE2'] = paramstr;
	
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