var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");
$("th[icode='BUTTONS']").css('width','15%');
//每一行添加编辑和删除
var idinfo = "";
$("#TS_XMGL_BMSH_SHGZ_MX .rhGrid").find("tr").each(function(index, item) {
	var table = this;
	if(index != 0) {
		var value1 = $('td[icode="MX_VALUE1"]',item).text();
		var val = $('td[icode="MX_VALUE2"]',item).text();
		if(val.indexOf("rzyear")>-1){
			idinfo=item.id;
			$(table).css("display",'none');
		}
		if(idinfo!=""){
			$($(table).find("td").eq(0)).html(index-1);
		}
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
						'<span class="rh-icon-inner-notext">设置</span><span class="rh-icon-img btn-change"></span></a>'+
						'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_BMSH_SHGZ_MX-copy" actcode="copy" rowpk="'+dataId+'" rowjson="'+val+'" rowname="'+name+'" >'+
						'<span class="rh-icon-inner-notext">复制</span><span class="rh-icon-img btn-copy"></span></a>'
				);
			}else{
				$(item).find("td[icode='BUTTONS']").append(
						'<a class="rhGrid-td-rowBtnObj rh-icon" id="TS_XMGL_BMSH_SHGZ_MX-setting" actcode="setting" rowpk="'+dataId+'" rowjson="'+val+'" rowname="'+name+'" >'+
						'<span class="rh-icon-inner-notext">设置</span><span class="rh-icon-img btn-change"></span></a>'
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
var glxlcodes = "";
var glnames = "";
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
				var sediv = $('<div></div>');
				for(var i=0;i<nameArg.length;i++) {
					if(i<nameArg.length-2){
						if(i==0){
							var span = document.createElement("span");
							span.innerHTML=nameArg[i];
							formConDiv7.append(span);
						}else if(i==1){
							//类别下拉框
							var param={};
							var result = FireFly.doAct("TS_BMLB_BM","getkslbk",param);
							var pageEntity = result.LBS;
							for(var k=0;k<pageEntity.length;k++){
								var id = pageEntity[k].KSLBK_ID;
								var name = pageEntity[k].KSLBK_NAME;
								if(k==0){
									var checkboxs = $('<span position="absolute">&nbsp;<input style="position:relative;top:5px" type="checkbox" id='+id+' name='+name+'>'+name+'&nbsp;</span>');
									formConDiv7.append(checkboxs);
								}else{
									var checkboxs = $('<span position="absolute"><input style="position:relative;top:5px" type="checkbox" id='+id+' name='+name+'>'+name+'&nbsp;</span>');
									formConDiv7.append(checkboxs);
								}
								
							}
							
						}
						
					}else if(i==(nameArg.length-1)){
						var inputaa = $('<input type="text" id="RULE-VAR-INPUT" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(obj2[i-1].val);
						if(obj2[obj2.length-1].type=="int"){
							inputaa.addClass("ui-text-default");
							
							inputaa.css("width","50px");
						}else{
							
							inputaa.addClass("Wdate ui-date-default").css("cursor","pointer");
							
							inputaa.css("width","150px");
							
							inputaa.attr("onfocus","WdatePicker({startDate:\'%y%MM%dd\',dateFmt:\'yyyyMMdd\',alwaysUseStartDate:false})");
							
						}
						sediv.append(nameArg[i]);
						sediv.append(inputaa);
						sediv.append(nameArg[nameArg.length-1]);
						formConDiv7.append(sediv);
					}else if(i==(nameArg.length-2)){
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
						sediv.append(select);
						formConDiv7.append(sediv);
					}
				}
			}else if(obj2[0].type== 'muty'){
				var codestr = [];
				var namestr = [];
//				debugger;
				for(var a=0;a<obj2.length;a++){
					if(a<obj2.length-2){
						namestr[a]=obj2[a].val;
						codestr[a]=obj2[a].code;
					}
				}
				xlcodes = codestr;
				xlnames = namestr;
			
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
						/*var inputaa = $('<input type="text" id="RULE-VAR-INPUT" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(obj2[i].val);*/
						var inputaa = $('<input type="text" id="RULE-VAR-INPUT" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(obj2[i].val);
						if(obj2[obj2.length-1].type=="int"){
							inputaa.addClass("ui-text-default");
							
							inputaa.css("width","50px");
						}else{
							
							inputaa.addClass("Wdate ui-date-default").css("cursor","pointer");
							
							inputaa.css("width","150px");
							
							inputaa.attr("onfocus","WdatePicker({startDate:\'%y%MM%dd\',dateFmt:\'yyyyMMdd\',alwaysUseStartDate:false})");
							
						}
							/*inputaa.addClass("ui-text-default");
							
							inputaa.css("width","50px");*/
							formConDiv7.append(nameArg[i]);
							formConDiv7.append(inputaa);
							formConDiv7.append(nameArg[i+1]);
							
							
							var butt = document.createElement("button");
							formConDiv7.append(butt);
							butt.innerHTML="请选择";
							butt.id="xlbutton";
					}
						
					}
			}else if(obj2[0].type=='string'){
				var  BUTT= document.createElement("button");
				BUTT.id="chongzhi";
				BUTT.innerHTML="重置";
				formConDiv7.append(BUTT);
				input8.val(name);
				input8.css("width","400px");
				formConDiv7.append(input8);
		}else if(obj2[0].type=='XL'){
			//管理类序列
			var codestr = [];
			var namestr = [];
			for(var a=0;a<obj2.length;a++){
					namestr[a]=obj2[a].val;
					codestr[a]=obj2[a].code;
			}
			glxlcodes=codestr;
			glnames = namestr;
			for(var i=0;i<nameArg.length;i++) {
				if(i<nameArg.length-1){
					var span  = document.createElement("span");
					if(i==0){
						span.innerHTML=obj2[i].val;
						formConDiv7.append(nameArg[i]);
						formConDiv7.append(span);
					}else{
						span.innerHTML=nameArg[i]+obj2[i].val;
						formConDiv7.append(span);
					}
				}else if (i==nameArg.length-1){
					//int值
					var span = document.createElement("span");
					span.innerHTML=nameArg[i];
					span.id='glastspan';
						formConDiv7.append(span);
						
				}
					
				}
			var butt = document.createElement("button");
			formConDiv7.append(butt);
			butt.innerHTML="请选择";
			butt.id="GLxlbutton";
			
			var divinfo = $("<div style='padding-top:10px;color:red;font-size:13px'></div>");
			
			var result = FireFly.byId("TS_XMGL_BMSH_SHGZ_MX",idinfo);
			
			var inputinfo = $('<button id="resetinfo">重置</button><input type="text" id="RULE-VAR-INPUT" value='+result.MX_NAME+' style="width:90%;border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">');
			divinfo.append(inputinfo);
			formConDiv7.append(divinfo);
		}else if(obj2[0].type=='level'){
			for(var i=0;i<nameArg.length;i++) {
				debugger;
				if(i<2){
					if(i==0){
						var span = document.createElement("span");
						span.innerHTML=nameArg[i];
						formConDiv7.append(span);
					}else if(i==1){
						//初中高级
						var select = document.createElement("select");  
						if(obj2[0].code=="1"){
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
						formConDiv7.append(select);
						var span  = document.createElement("span");
						span.innerHTML=nameArg[i];
						formConDiv7.append(span);
					}
					
				}else if(i==(nameArg.length-1)){
					var inputaa = $('<input type="text" id="RULE-VAR-INPUT2" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(obj2[i-1].val);
					if(obj2[obj2.length-1].type=="int"){
						inputaa.addClass("ui-text-default");
						
						inputaa.css("width","50px");
					}else{
						
						inputaa.addClass("Wdate ui-date-default").css("cursor","pointer");
						
						inputaa.css("width","150px");
						
						inputaa.attr("onfocus","WdatePicker({startDate:\'%y%MM%dd\',dateFmt:\'yyyyMMdd\',alwaysUseStartDate:false})");
						
					}
					
					formConDiv7.append(inputaa);
					var span  = document.createElement("span");
					span.innerHTML=nameArg[i];
					formConDiv7.append(span);
				}else if(i==(nameArg.length-2)){
					var inputaa = $('<input type="text" id="RULE-VAR-INPUT" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(obj2[i-1].val);
						inputaa.addClass("ui-text-default");
						
						inputaa.css("width","50px");
					formConDiv7.append(inputaa);
					var span  = document.createElement("span");
					span.innerHTML=nameArg[i];
					formConDiv7.append(span);
				}
			}
			
		}else{
			for(var i=0;i<nameArg.length;i++) {
				if(obj2[0].type == 'dateyear') {
					
					if(i==nameArg.length-1) {
						formConDiv7.append(nameArg[i]);
						var  BUTT= document.createElement("button");
						BUTT.id="jqlike";
						BUTT.innerHTML="模糊到年";
						formConDiv7.append(BUTT);
					} else {
						formConDiv7.append(nameArg[i]);
						var inputaa = $('<input type="text" name="yearlimit" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(obj2[i].val);
						inputaa.addClass("Wdate ui-date-default").css("cursor","pointer");
						inputaa.css("width","150px");
						
						inputaa.attr("onfocus","WdatePicker({startDate:\'%y%MM%dd\',dateFmt:\'yyyyMMdd\',alwaysUseStartDate:false})");
						
						formConDiv7.append(inputaa);
					}
				}else{
					if(obj2[0].type == 'date'){
						if(i==nameArg.length-1) {
							formConDiv7.append(nameArg[i]);
						} else {
							formConDiv7.append(nameArg[i]);
							var inputaa = $('<input type="text" name="yearlimit" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(obj2[i].val);
							inputaa.addClass("Wdate ui-date-default").css("cursor","pointer");
							inputaa.css("width","150px");
							
							inputaa.attr("onfocus","WdatePicker({startDate:\'%y%MM%dd\',dateFmt:\'yyyyMMdd\',alwaysUseStartDate:false})");
							
							formConDiv7.append(inputaa);
						}

					}else{
						if(i==nameArg.length-1) {
							formConDiv7.append(nameArg[i]);
						} else {
							formConDiv7.append(nameArg[i]);
							var inputaa = $('<input type="text" name="yearlimit" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val(obj2[i].val);
							
							inputaa.addClass("ui-text-default");
							
							inputaa.css("width","150px");
							formConDiv7.append(inputaa);
						}
						
					}
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
				if(obj2[0].type=='string'){
					var arr =$("#RULE-VAR-INPUT").val()
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
					$("input:checkbox:checked").each(function(index,item){
							var name = $(this).attr("name");
							var code = $(this).attr("id");
						var flag=false;
								mx_name+="#select#";
						arr1[index]=code;
						jsons+='{"vari":"select","val":"'+name+'","type":"select","code":"'+code+'"},';
					});
					if($("input:checkbox:checked").length==0){
						alert("考试类别必选，请选择");
						return false;
					}
					$(formConDiv7.find('select')).each(function(index,item){
								
								mx_name+="#select#";
						arr1[index]=$(this).val();
						jsons+='{"vari":"select","val":"'+$(this).find("option:selected").text()+'","type":"select","code":"'+$(this).val()+'"},';
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
					jsons+='{"vari":"muty","val":"'+$("#RULE-VAR-INPUT").val()+'","type":"datetime"}]';
					var ival = $("#RULE-VAR-INPUT").val();
					if(isNaN(ival)){
				    	alert("请输入数字!");
						return;
				    }
					mx_name+=nameArg[nameArg.length-2];
					mx_name+="#muty#";
					mx_name+=nameArg[nameArg.length-1];
				
					saveRuleVarCode(dataId,"","",jsons,mx_name);
			}else if(obj2[0].type=='XL'){
//					debugger;
					//保存 序列
					var mx_name = nameArg[0];
					var jsons = "[";
					for(var j=0;j<glxlcodes.length;j++){
						if(j==glxlcodes.length-1){
							jsons+='{"vari":"XL","val":"'+glnames[j]+'","type":"XL","code":"'+glxlcodes[j]+'"}';
							mx_name+="#XL#"
						}else{
							
							jsons+='{"vari":"XL","val":"'+glnames[j]+'","type":"XL","code":"'+glxlcodes[j]+'"},';
							mx_name+="#XL#、"
						}
						
					}
					jsons+="]"
					mx_name+=nameArg[nameArg.length-1];
					var mx_name1 = $("#RULE-VAR-INPUT").val();
					if(""!=mx_name1){
						var param={};
						param["id"]=idinfo;
						param["mx_name"]=mx_name1;
						FireFly.doAct("TS_XMGL_BMSH_SHGZ_MX","saveinfo",param);
					}else{
						alert("请输入提示信息");
						return false;
					}
					saveRuleVarCode(dataId,"","",jsons,mx_name);
				}else if(obj2[0].type=='level'){
					var arr1 = [];
					var arr = [];
					var mx_name = nameArg[0];
					var jsons = "[";
					$(formConDiv7.find('select')).each(function(index,item){
								
						mx_name+="#level#";
						arr1[index]=$(this).val();
						jsons+='{"vari":"level","val":"'+$(this).find("option:selected").text()+'","type":"level","code":"'+$(this).val()+'"},';
					});
					mx_name+=nameArg[1];
					mx_name+="#level#";
					mx_name+=nameArg[2];
					jsons+='{"vari":"level","val":"'+$("#RULE-VAR-INPUT").val()+'","type":"int"},';
					mx_name+="#level#";
					mx_name+=nameArg[3];
					jsons+='{"vari":"level","val":"'+$("#RULE-VAR-INPUT2").val()+'","type":"datetime"}]';
					saveRuleVarCode(dataId,arr,arr1,jsons,mx_name);
					
				}else{
				var text = "";
				$("input[name='yearlimit']").each(function(index,item){
					text = $(this).val();
				})
				
				saveRuleVar(dataId,text,jsonStr);
			}
		});
		
		$("div[class='ui-dialog ui-widget ui-widget-content ui-corner-all ui-draggable ui-resizable']").css("left","30%");
		
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
	$("#jqlike").click(function(){
		if($("#jqlike").html()=="精确到日"){
			$("#jqlike").html('模糊到年');
			$("input[name='yearlimit']").each(function(){
				$(this).attr("onfocus","WdatePicker({startDate:\'%y%MM%dd\',dateFmt:\'yyyyMMdd\',alwaysUseStartDate:false})");
				$(this).removeClass("WdateFmtErr");
				$(this).addClass("Wdate ui-date-default").css("cursor","pointer");
				$(this).val('20170809');
			});
		}else{
		//模糊
		$("#jqlike").html('精确到日');
		$("input[name='yearlimit']").each(function(){
			var inputaa = $('<input type="text" name="yearlimit" style="border:1px solid #ddd; margin:0px 5px 0px 5px;text-align:center">').val('1');
			inputaa.attr("onfocus","WdatePicker({startDate:\'%y\',dateFmt:\'yyyy\',alwaysUseStartDate:false})");
			inputaa.css("width","150px");
			inputaa.addClass("Wdate ui-date-default").css("cursor","pointer");
			$(this).before(inputaa);
			$(this).remove();
			inputaa.val('2017');
			/*$(this).addClass("ui-text-default");*/
		});
		}
	});
	$("#resetinfo").click(function(){
		//从规则库中将数据查出 
		var param = {};
		var result = FireFly.doAct("TS_XMGL_BMSH_SHGZ_MX","chongzhi",param);
		var mx_name = result.gzbean;
		$("#RULE-VAR-INPUT").val("");
		$("#RULE-VAR-INPUT").val(mx_name);
	});
	
	$("#chongzhi").click(function(){
		//从规则库中将数据查出 
		var param = {};
		param["GZ_ID"]="N03";
		var result = FireFly.doAct("TS_XMGL_BMSH_SHGZ_MX","getJkgz",param);
		var mx_name = result.gzbean;
		$("#RULE-VAR-INPUT").val("");
		$("#RULE-VAR-INPUT").val(mx_name);
	});
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
	$("#GLxlbutton").click(function(){
		
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
						glnames=names;
						glxlcodes = codes;
						var paramArray = [];
						$(".formContent").find("span[id!='glastspan']").each(function(index,item){
								
								$(this).remove();
						});
						var span = document.createElement("span");
						var s = "";
						for(var i=0;i<names.length;i++){
							//将选中的code和name保存
							if(i==(names.length-1)){
								s+=names[i];
							}else{
								s+=names[i]+"、";
							}
						}
						span.innerHTML=s;
						$("#glastspan").before(span);
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
	if(obj2.length==1){
		if(obj2[0].type =='int') {
			if(parseInt(val)!=val){
				alert("请输入数字!");
				return;
			}
		}
		obj2[0].val=val;
	}else{
		for(var i=0;i<obj2.length;i++){
			if(obj2[i].type =='int') {
				if(parseInt(val[i])!=val[i]){
					alert("请输入数字!");
					return;
				}
			}
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