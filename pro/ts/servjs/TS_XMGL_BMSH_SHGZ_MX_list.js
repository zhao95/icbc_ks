var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
//每一行添加编辑和删除
$("#TS_XMGL_BMSH_SHGZ_MX .rhGrid").find("tr").each(function(index, item) {
	if(index != 0) {
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
			if(obj2[0].type=="select"){
				$(item).find("td[icode='BUTTONS']").append(
						'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_BMSH_SHGZ_MX-setting" actcode="setting" rowpk="'+dataId+'" rowjson="'+val+'" rowname="'+name+'" >'+
						'<span class="rh-icon-inner-notext"></span><span class="rh-icon-img btn-change"></span></a>'+
						'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_BMSH_SHGZ_MX-copy" actcode="copy" rowpk="'+dataId+'" rowjson="'+val+'" rowname="'+name+'" >'+
						'<span class="rh-icon-inner-notext"></span><span class="rh-icon-img btn-copy"></span></a>'
				);
			}else{
				$(item).find("td[icode='BUTTONS']").append(
						'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_BMSH_SHGZ_MX-setting" actcode="setting" rowpk="'+dataId+'" rowjson="'+val+'" rowname="'+name+'" >'+
						'<span class="rh-icon-inner-notext"></span><span class="rh-icon-img btn-change"></span></a>'
				);
			}
			// 为每个按钮绑定卡片
			bindCard();
		}
	}
});

//选中的name  code 放入缓存中
var xlnames = "";
var xlcodes  = "";

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
			if(obj2[0].type== 'select'){
				//类别下拉框
				//获取级别和级别code
				for(var i=0;i<nameArg.length;i++) {
					if(i<nameArg.length-3){
						//类别下拉框
						var select = document.createElement("select");  
						var param={};
						var result = FireFly.doAct("TS_BMLB_BM","getkslbk",param);
						var pageEntity = result.LBS;
						var codes = pageEntity[0].KSLBK_CODE;
						for(var k=0;k<pageEntity.length;k++){
							if(pageEntity[k].KSLBK_CODE==obj2[i].code){
								
								select.add(new Option(pageEntity[k].KSLBK_NAME,pageEntity[k].KSLBK_CODE,true,true)); 
							}else{
								select.add(new Option(pageEntity[k].KSLBK_NAME,pageEntity[k].KSLBK_CODE)); 
							}
						}
						select.name="lbselect";
						var span = document.createElement("span");
						span.innerHTML=nameArg[i]
						formConDiv7.append(span);
						
						formConDiv7.append(select);
					}else if(i==(nameArg.length-2)){
						var inputaa = $('<input type="text" id="RULE-VAR-INPUT" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(obj2[i].val);
						if(obj2[obj2.length-1].type=="int"){
							inputaa.addClass("ui-text-default");
							
							inputaa.css("width","50px");
						}else{
							
							inputaa.addClass("Wdate ui-date-default").css("cursor","pointer");
							
							inputaa.css("width","150px");
							
							inputaa.attr("onfocus","WdatePicker({startDate:\'%y%MM%dd\',dateFmt:\'yyyyMMdd\',alwaysUseStartDate:false})");
							
						}
						formConDiv7.append(nameArg[i]);
						
						formConDiv7.append(inputaa);
						formConDiv7.append(nameArg[nameArg.length-1]);
					}else if(i==(nameArg.length-3)){
						//初中高
						var select = document.createElement("select");  
						if(obj2[i].code=="1"){
							select.id = "jibieselect";
							select.add(new Option("初级","1",true,true)); 
							select.add(new Option("中级","2")); 
							select.add(new Option("高级","3")); 
						}else if(obj2[i].code=="2"){
							select.id = "jibieselect";
							select.add(new Option("中级","2",true,true)); 
							select.add(new Option("初级","1")); 
							select.add(new Option("高级","3")); 
						}else{
							select.id = "jibieselect";
							select.add(new Option("高级","3",true,true)); 
							select.add(new Option("初级","1")); 
							select.add(new Option("中级","2")); 
						}
						var span = document.createElement("span");
						span.innerHTML=nameArg[i]
						formConDiv7.append(span);
						formConDiv7.append(select);
					}
				}
				var  span= document.createElement("button");
				span.id="minus";
				span.innerHTML="删除";
				formConDiv7.append(span);
			}
			
			if(obj2[0].type== 'muty'){
				var codestr = [];
				var namestr = [];
				debugger;
				for(var a=0;a<obj2.length;a++){
					if(a<obj2.length-2){
						namestr[a]=obj2[a].val;
						codestr[a]=obj2[a].code;
					}
				}
				xlcodes = codestr;
				xlnames = namestr;
				var butt = document.createElement("button");
				formConDiv7.append(butt);
				butt.style.width="60px";
				butt.style.height="25px";
				butt.style.fontSize="0.5px";
				butt.innerHTML="请选择";
				butt.id="xlbutton";
				for(var i=0;i<nameArg.length;i++) {
					if(i<nameArg.length-3){
						var span  = document.createElement("span");
						if(i==0){
							span.innerHTML=obj2[i].val;
							formConDiv7.append(nameArg[i]);
							formConDiv7.append(span);
						}else{
							span.innerHTML=nameArg[i]+obj2[i].val;
							formConDiv7.append(span);
						}
					}else if(i==nameArg.length-3){
						//初中高级
						var select = document.createElement("select");  
						if(obj2[i].code=="1"){
							select.id = "jibieselect";
							select.add(new Option("初级","1",true,true)); 
							select.add(new Option("中级","2")); 
							select.add(new Option("高级","3")); 
						}else if(obj2[i].code=="2"){
							select.id = "jibieselect";
							select.add(new Option("中级","2",true,true)); 
							select.add(new Option("初级","1")); 
							select.add(new Option("高级","3")); 
						}else{
							select.id = "jibieselect";
							select.add(new Option("高级","3",true,true)); 
							select.add(new Option("初级","1")); 
							select.add(new Option("中级","2")); 
						}
						var span  = document.createElement("span");
						span.innerHTML=nameArg[i];
						formConDiv7.append(span);
						formConDiv7.append(select);
					}else if (i==nameArg.length-2){
						//int值
						var inputaa = $('<input type="text" id="RULE-VAR-INPUT" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(obj2[i].val);
						
							inputaa.addClass("ui-text-default");
							
							inputaa.css("width","50px");
							formConDiv7.append(nameArg[i]);
							formConDiv7.append(inputaa);
							formConDiv7.append(nameArg[i+1]);
					}
						
					}
			}
			
			if(obj2[0].type=='string'){
				var  BUTT= document.createElement("button");
				BUTT.id="chongzhi";
				BUTT.innerHTML="重置";
				formConDiv7.append(BUTT);
				input8.val(name);
				input8.css("width","400px");
				formConDiv7.append(input8);
		}else if(obj2.length==1){
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
		bindselect();
		bindelete();
		$("#SETTING-saveRuleVar").bind("click",function() {
			
			if(obj2.length>1){
				if(obj2[0].type=='string'){
					var arr = [];
					$("input[name='inputaa']").each(function(index,item){
						arr[index]=$(this).val();
					})
					saveRuleVar(dataId,arr,jsonStr);
				}else if(obj2[0].type=='select'){
					//先去重
					//类别下拉框
					//再改变code编码
					var arr1 = [];
					var arr = [];
					var mx_name = nameArg[0];
					var selects = $(formConDiv7.find('select'));
					var jsons = "[";
					$(formConDiv7.find('select')).each(function(index,item){
						var flag=false;
						for(var a=0;a<arr1.length;a++){
							if(arr1[a]==$(this).val()){
								flag=true;
							}
							if($(this).val()==""){
								flag=true;
							}
						}
						if(flag){
						}else{
							if(selects.length-1==index){
								mx_name+="#select#";
							}else{
								
								mx_name+="#select#、";
							}
						arr1[index]=$(this).val();
						jsons+='{"vari":"select","val":"'+$(this).find("option:selected").text()+'","type":"select","code":"'+$(this).val()+'"},';
						}
					});
					mx_name+=nameArg[nameArg.length-2];
					mx_name+="#select#";
					mx_name+=nameArg[nameArg.length-1];
					jsons+='{"vari":"select","val":"'+$("#RULE-VAR-INPUT").val()+'","type":"date"}]';
					saveRuleVarCode(dataId,arr,arr1,jsons,mx_name);
				}else if(obj2[0].type=='muty'){
					//保存 序列
					var mx_name = nameArg[0];
					var jsons = "[";
					for(var j=0;j<xlcodes.length;j++){
						jsons+='{"vari":"muty","val":"'+xlnames[j]+'","type":"muty","code":"'+xlcodes[j]+'"},';
						
							mx_name+="#muty#、"
					}
					jsons+='{"vari":"muty","val":"'+$("#jibieselect").find("option:selected").text()+'","type":"muty","code":"'+$("#jibieselect").val()+'"},';
					mx_name+="#muty#";
					jsons+='{"vari":"muty","val":"'+$("#RULE-VAR-INPUT").val()+'","type":"int"}]';
					var ival = $("#RULE-VAR-INPUT").val();
					if(isNaN(ival)){
				    	alert("请输入数字!");
						return;
				    }
					mx_name+=nameArg[nameArg.length-2];
					mx_name+="#muty#";
					mx_name+=nameArg[nameArg.length-1];
					saveRuleVarCode(dataId,"","",jsons,mx_name);
				}
				
			}else{
				
				saveRuleVar(dataId,input8.val(),jsonStr);
			}
		});

	});
	jQuery("td [id='TS_XMGL_BMSH_SHGZ_MX-copy']").unbind("click").bind("click", function() {
		var dataId = jQuery(this).attr("rowpk");
		var param = {};
		param["dataId"]=dataId;
		FireFly.doAct("TS_XMGL_BMSH_SHGZ_MX","CopyMx",param);
		_viewer.refresh();
	});
	
}
function bindelete(){
	$("#chongzhi").click(function(){
		//从规则库中将数据查出 
		var param = {};
		param["GZ_ID"]="N03";
		var result = FireFly.doAct("TS_XMGL_BMSH_SHGZ_MX","getJkgz",param);
		var mx_name = result.gzbean;
		$("#RULE-VAR-INPUT").html("");
		$("#RULE-VAR-INPUT").html(mx_name);
	})
	$('#minus').click(function(){
		//删除一个下拉框
		var selectlen = $("select[name='lbselect']").length;
		if(selectlen==1){
			return;
		}
		$("select[name='lbselect']:last").remove();
		$('.formContent').eq(3).find("span:last").remove();
		bindselect();
	})
}
function bindselect(){
	$("#xlbutton").click(function(){
			var configStr = "TS_XMGL_BM_KSLBK_XL,{'TARGET':'KSLBK_XL~KSLBK_XL_CODE','SOURCE':'KSLBK_XL~KSLBK_XL_CODE'," +
			"'HIDE':'','TYPE':'multi','HTMLITEM':''}";
			var options = {
					"config" :configStr,
//					"params" : {"_TABLE_":"SY_ORG_USER"},
					"parHandler":_viewer,
					"formHandler":_viewer.form,
					"replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
						var codes = idArray.KSLBK_XL_CODE.split(",");
						var names = idArray.KSLBK_XL.split(",");
						xlnames=names;
						xlcodes = codes;
						var paramArray = [];
						$(".formContent").find("span").each(function(index,item){
								
								$(this).remove();
						});
						var span = document.createElement("span");
						var s = "";
						for(var i=0;i<names.length;i++){
							//将选中的code和name保存
							s+=names[i]+"、";
						}
						span.innerHTML=s;
						$("#jibieselect").before(span);
						//批量保存
						_viewer.refresh();
					}
			};
			//2.用系统的查询选择组件 rh.vi.rhSelectListView()
			var queryView = new rh.vi.rhSelectListView(options);
			queryView.show(event);
	})
	var selects = $("select[name='lbselect']");
	if(selects.length==5){
		return;
	}
	
	$("select[name='lbselect']:last").change(function(){
		var select = document.createElement("select");  
		var param={};
		var result = FireFly.doAct("TS_BMLB_BM","getkslbk",param);
		var pageEntity = result.LBS;
		var codes = pageEntity[0].KSLBK_CODE;
		for(var k=0;k<pageEntity.length;k++){
		select.add(new Option(pageEntity[k].KSLBK_NAME,pageEntity[k].KSLBK_CODE)); 
		}
		select.add(new Option("","",true,true)); 
		select.name="lbselect";
		$("#jibieselect").before(select);
		var span = document.createElement("span");
		span.innerHTML="、";
		$("#jibieselect").before(span);
		$(this).unbind("change");
		bindselect();
	})
}
function saveRuleVar(dataId,val,obj2) {
	obj2 = eval(obj2);
	if(obj2[0].type=="string"){
		var param = {};
		
		param['MX_ID'] = dataId;
		param['_PK_'] = dataId;
		param['MX_NAME']=val;
		var result = FireFly.doAct(_viewer.servId, "save", param, tipFlag, false,function(data){
			
			if(data._MSG_.indexOf("OK")!= -1) {
				_viewer.refresh();
				var dialogId = "setting-dialog-"+dataId;
				jQuery("#" + dialogId).remove();
			}
			
		});
	}else{
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
}


function saveRuleVarCode(dataId,arr,val,obj2,mx_name) {
	
	var ssss = obj2.split(",");
	var paramstr = "";
	var param = {};
	param['MX_ID'] = dataId;
	param['_PK_'] = dataId;
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
	param['MX_NAME']=mx_name;
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