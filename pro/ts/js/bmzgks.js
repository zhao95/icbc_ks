var user_code = System.getVar("@USER_CODE@");
var user_name = System.getVar("@USER_NAME@");
var user_sex = System.getVar("@USER_SEX@");
var odept_name = System.getVar("@ODEPT_NAME@");
var odept_code = System.getVar("@ODEPT_CODE@");
var user_office_phone = System.getVar("@USER_OFFICE_PHONE@");
var user_cmpy_date = System.getVar("@USER_CMPY_DATE@");
var xm_id  = $("#xmidval").val();

var bm_start="";
var bm_end = "";
var xm_name = "";
//项目信息进行展示
function xminfoshow(){
	param={};
	param["xmid"]=xm_id;
	FireFly.doAct("TS_XMGL_BMGL","getXmInfo",param,true,false,function(data){
		var bminfo = data.list;
		var bminfojson = JSON.parse(bminfo);
	    xm_name = data.xmname;
		var bm_id = bminfojson[0].BM_ID;
		var bm_ksxzs = bminfojson[0].BM_KSXZ;
		//将/n替换为  br  /s/g   全部替换
		var bm_ksxz=bm_ksxzs.replace(/\n/g,"<br>");
		 bm_start = bminfojson[0].BM_START;
		 bm_end = bminfojson[0].BM_END;
		var bm_name = bminfojson[0].BM_NAME;
		//给jsp赋值
		$("#xmnamecon").html(xm_name);
		$("#ksxzcon").html(bm_ksxz);
	});
}
//进行资格验证
	function checky(){
		debugger;
		var param = {};
		var bminfo={};
		bminfo['XM_ID'] = xm_id;
		bminfo['BM_CODE'] = user_code;
		bminfo['BM_STARTDATE'] = bm_start;
		bminfo['BM_ENDDATE'] = bm_end;
// 		xkArg.push(yk);
		var neAry=xkArg;
		if(yk.ID){
			neAry=xkArg.concat(yk);	
		}
		//数组去重
		for(var i=0; i < neAry.length; i++) {
		    for(var j=i+1;j< neAry.length; j++) {
		        if(neAry[i].ID == neAry[j].ID) {
		        	neAry.splice(neAry[j],1);
		        }
		    }
		}
		
		param['BM_INFO'] = JSON.stringify(bminfo);
		param['BM_LIST'] = JSON.stringify(neAry);
		//已报名的考试
		var parambm = {};
			
			parambm["user_code"]=user_code;
			parambm["xmid"]=xm_id;
			var results = FireFly.doAct("TS_BMLB_BM","getBmData",parambm);
			console.log(param);
		FireFly.doAct("TS_XMGL_BMSH", "vlidates", param, true,false,function(data){
    		yzgz=data;
    		console.log(data);
    		//获取后台传过来的key
    		var zgArray = document.getElementsByName("zgksname");
         	for(var i=0;i<zgArray.length;i++){
             	//获取验证规则div的id
       			var a=zgArray[i].value;
       			//获取验证结果div的id
             	var yzjg=a+"yzjg";
       			var dataArray =data[a];
            	//获取div对应的数组
       			//append内容之前判断是否有内容
       			var divtext1 = $("#"+a).html();
       			if(divtext1==null||divtext1.length==0){
       				var shArray=true;
       				//判断此考试是否已报名  如果已报名审核通过 必须删除 才能提交
       					var resdata = results.list;
       					var FLAG = false;
       				for(var z=0;z<resdata.length;z++){
       					if(resdata[z].KSLBK_ID==neAry[0].KSLBK_ID){
       						FLAG = true;
       					}
       				}
       				if(FLAG){
       					$("#"+a).append('已报名此考试,请撤销再报名');
       					$("#"+a).append('<div class="btn" name="existedbm" onclick="deleterow(this)" type="button" style="color:red;backgroundcolor:lightseagreen">请删除</div>');
       					$("#"+yzjg).append('审核不通过');
       					continue;
       				}
       				for(var j=0;j<dataArray.length;j++){
       					if(j==0){
       						$("#"+a).append('<div style="height:5px;"></div>');
       					}
       					if(dataArray[j].VLIDATE=="true"){
	       					$("#"+a).append('<div><img src="/ts/image/u4719.png">&nbsp;'+dataArray[j].NAME+'</div>');
	       					
						}if(dataArray[j].VLIDATE=="false"){
							
							$("#"+a).append('<div style="color:red;"><img src="/ts/image/u4721.png">&nbsp;'+dataArray[j].NAME+'</div>');
						}
						if(dataArray[j].VLIDATE=="false"){
							shArray=false;
						}
						$("#"+a).append('<div style="height:5px;"></div>');
       				}
       				if(shArray==false){
       					$("#"+yzjg).append('审核不通过');
       				
       				}if(shArray==true){
       					$("#"+a).append('<div></div>');
       					$("#"+a).append('<div></div>');
       					$("#"+yzjg).append('审核通过');
       				}
	       		}
       			
       		}
    	});	
	}
	//提交所有数据
	function mttijiao(){
		//获取手机号码
		var ryl_mobile = document.getElementById("user_mobile2").value
		//获取到资格考试类型主键id
		var zgArray = document.getElementsByName("zgksname");
		var zglb="";
     	for(var i=0;i<zgArray.length;i++){
   			if(i==0){
   				zglb = zgArray[i].value;
   			}else{
   				zglb +="," + zgArray[i].value;
   			}
   		}
     	var param = {};
// 		xkArg.push(yk);
		var neAry;
		var neAry=xkArg;
		if(yk.ID){
			neAry=xkArg.concat(yk);	
		}
		
		for(var i=0; i < neAry.length; i++) {
		    for(var j=i+1;j< neAry.length; j++) {
		        if(neAry[i].ID == neAry[j].ID) {
		        	neAry.splice(neAry[j],1);
		        }
		    }
		}
		param["USER_CODE"] = user_code;
		param["USER_NAME"] = user_name;
		param["USER_SEX"] = user_sex;
		param["ODEPT_NAME"] = odept_name;
		param["USER_OFFICE_PHONE"] = user_office_phone;
		param["USER_MOBILE"] = ryl_mobile;
		param["USER_CMPY_DATE"] = user_cmpy_date;
		param["XM_ID"] = xm_id;
		param["BM_START"] = bm_start;
		param["BM_END"] = bm_end;
		param["XM_NAME"] = xm_name;
		param["ODEPT_CODE"]=odept_code;
			param['BM_LIST'] = JSON.stringify(neAry);
			param["YZGZ_LIST"] = JSON.stringify(yzgz);
			//本序列表格行数
			var bxltabObj = document.getElementById("tableid");
			var bxlrows = bxltabObj.rows.length;
			//跨序列表格行数
			var kxlObj = document.getElementById("tablehang");
			var kxlrows = kxlObj.rows.length;
			if (bxlrows < 2 && kxlrows < 2) {
				alert("您没有选择考试")
			}
			if (bxlrows != 1 || kxlrows != 1) {
				if (ryl_mobile == "") {
					alert("手机号码不能为空");
				}
				if (ryl_mobile != "" && ryl_mobile != null) {
					var BM_ID = FireFly.doAct("TS_BMLB_BM", "addZgData", param,
							true, false);
					showMask();
					if (BM_ID.strresult != null || BM_ID.strresult != "") {
						hideMask();
					}
					window.location.href = "bm.jsp";
				}
			}
		}
	//定义一个统计页面中级考试数目的变量
	function showFzgList(showList){
		jQuery('#ksxxId').html('');
		var strchecked = checked.join(",");
			//已选中的
			paramstr={};
			paramstr["checked"]=strchecked;
			var data = FireFly.doAct("TS_BMLB_BM","getCheckedData",paramstr,true,false);
				var alldata = data.list;
				for(var i=0;i<alldata.length;i++){
					var kslb_name=alldata[i].KSLBK_NAME;
				       var kslb_xl=alldata[i].KSLBK_XL;
				       var kslb_mk=alldata[i].KSLBK_MK;
				       var kslb_type_name=alldata[i].KSLBK_TYPE_NAME;
					   var kslbk_id = alldata[i].KSLBK_ID;
					   var kslb_code=alldata[i].KSLBK_CODE;
				       var kslb_xl_code=alldata[i].KSLBK_XL_CODE;
				       var kslb_mk_code=alldata[i].KSLBK_MKCODE;
				       var kslb_type = alldata[i].KSLBK_TYPE;
				      
				       jQuery('#ksxxId').append([
				                         		'<tr style="height:30px">',
				                         		'<td style="text-align: center" width="10%"><image src="/ts/image/u4719.png"></image></td>',
				                         		'<td width="15%">'+kslb_name+'</td>',
				                         		'<td width="15%">'+kslb_xl+'</td>',
				                         		'<td width="45%">'+kslb_mk+'</td>',
				                         		'<td width="15%">'+kslb_type_name+'</td>',
				                         		'<td class="rhGrid-td-hide" id="HANGHAO'+i+'">'+i +'</td>',
				                         		'<td class="rhGrid-td-hide" >'+kslb_id+'</td>',
				                         		'<td class="rhGrid-td-hide">'+kslb_code+'</td>',
				                         		'<td class="rhGrid-td-hide">'+kslb_xl_code+'</td>',
				                         		'<td class="rhGrid-td-hide">'+kslb_mk_code+'</td>',
				                         		'<td class="rhGrid-td-hide">'+kslb_type+'</td>',
				                         	'</tr>'
				                         	].join('')
				                         	);  
				       
				}
				       
		for(var i=0; i<showList.length;i++){
			var showItem = showList[i];
			var kslb_id = showItem.KSLB_ID;
			var kslb_name = showItem.KSLB_NAME;
			var kslb_xl = showItem.KSLB_XL;
			var kslb_mk = showItem.KSLB_MK;
			var kslb_type_name = showItem.KSLB_TYPE_NAME;
			var kslb_code = showItem.KSLB_CODE;
			var kslb_xl_code = showItem.KSLB_XL_CODE;
			var kslb_mk_code = showItem.KSLB_MK_CODE;
			var kslb_type = showItem.KSLB_TYPE;
			
	jQuery('#ksxxId').append([
		'<tr>',
		'<td style="height:30px" width="10%"></td>',
		'<td width="15%">'+kslb_name+'</td>',
		'<td width="15%">'+kslb_xl+'</td>',
		'<td width="45%">'+kslb_mk+'</td>',
		'<td width="15%">'+kslb_type_name+'</td>',
		'<td class="rhGrid-td-hide" id="HANGHAO'+i+'">'+i +'</td>',
		'<td class="rhGrid-td-hide" >'+kslb_id+'</td>',
		'<td class="rhGrid-td-hide">'+kslb_code+'</td>',
		'<td class="rhGrid-td-hide">'+kslb_xl_code+'</td>',
		'<td class="rhGrid-td-hide">'+kslb_mk_code+'</td>',
		'<td class="rhGrid-td-hide">'+kslb_type+'</td>',
	'</tr>'
	].join('')
	);
		}
	}
	

	var checked = [];
	var yk={};
	var xkArg=[];//考试结果
	var yzgz;//资格验证后端返回到前端的数据
	 $(function(){ 
		 xminfoshow();
		 matchinfo();
		 mkfuzhi();
		 typeId(obj);
		 tongji();
		 var allList=getFzgList();
		 showFzgList(allList);
		 var param1 = {};
		 param1["DUTY_CODE"]=ADMIN_DUTY;
		 param1["STATION_TYPE_CODE"]=STATION_TYPE_CODE;
		 param1["STATION_NO_CODE"]=STATION_NO_CODE;
		/*var cengji =  FireFly.doAct("TS_BMLB_BM","getcengji",param1);
		var cengjinum = cengji.num;
		var sqlstr = " AND (KSLBK_TYPE<="+cengjinum+" or KSLBK_TYPE is null";*/
		 
		 /*if(cengjinum==1){
			 //只能报
			 $("#allnum").html(0);
			 $("#canheighnum").html(0);
		 }else if(cengjinum==2){
			 //
			 
		 }*/
        var extWhere="AND KSLBK_ID IN ((select kslbk_pid from ts_xmgl_bm_kslbk where kslbk_id in (select kslbk_pid from ts_xmgl_bm_kslbk where kslbk_id in (SELECT KSLBK_PID FROM TS_XMGL_BM_KSLBK WHERE KSLBK_ID IN (select KSLBK_ID FROM TS_XMGL_BM_KSLB  WHERE XM_ID='"+xm_id+"'))))union(select kslbk_pid from ts_xmgl_bm_kslbk where kslbk_id in (SELECT KSLBK_PID FROM TS_XMGL_BM_KSLBK WHERE KSLBK_ID IN (select KSLBK_ID FROM TS_XMGL_BM_KSLB  WHERE XM_ID='"+xm_id+"')))union(SELECT KSLBK_PID FROM TS_XMGL_BM_KSLBK WHERE KSLBK_ID IN (select KSLBK_ID FROM TS_XMGL_BM_KSLB  WHERE XM_ID='"+xm_id+"'))union(select KSLBK_ID FROM TS_XMGL_BM_KSLB  WHERE XM_ID='"+xm_id+"')) AND KSLBK_CODE<>'"+STATION_TYPE_CODE+"' AND (KSLBK_XL_CODE<>'"+STATION_NO_CODE+"' OR KSLBK_XL_CODE is null)";
		 var setting={data
	             :FireFly.getDict('TS_XMGL_BM_KSLBK','KSLBK_PID',extWhere),
	         dictId:"TS_XMGL_BM_KSLBK",expandLevel:1,
	         oncheckboxclick: function(item, s, id) {
	        	var itemjson =  item['ID'];
	        	var valid  = true;
	        	for(var i=0;i<checked.length;i++){
	        		if (checked[i].indexOf(itemjson) > -1) {
	        			var index = checked.indexOf(itemjson);
	        			checked.splice(index, 1);
	        			valid = false;
	        			showFzgList(obj);
	        			}
	        	}
	        	if(valid){
	        		if(item['NAME']=="初级"||item['NAME']=="高级"||item['NAME']=="中级"){
	        			checked.push(itemjson);
	        			showFzgList(obj);
	        		}
	        	}
	         },
	         onnodeclick :function (item) {
	        	 var idName=item['NAME'];
	        	 var showList=[];
	        	 for(var i=0; i<allList.length;i++){
	        		 var showItem = allList[i];
	        		 if(idName===showItem.KSLB_NAME || idName===showItem.KSLB_MK || idName===showItem.KSLB_XL){
	        			 
	        			 var strchecked = checked.join(",");
	        				paramstr={};
	        				paramstr["checked"]=strchecked;
	        				var data = FireFly.doAct("TS_BMLB_BM","getCheckedData",paramstr,true,false);
	        					var alldata = data.list;
	        					if(alldata.length==0){
	        						showList.push(showItem);
	        					}else{
	        						var flag = true;
	        						for(var j=0;j<alldata.length;j++){
	        							if(alldata[j].KSLBK_ID==showItem.KSLBK_ID){
	        								flag= false;
	        							}
	        						}
	        						if(flag){
	        							showList.push(showItem);
	        						}
	        					}
	        		 }
	        	 }
	        	 showFzgList(showList);
	         },
	         rhItemCode:"KSLBK_PID",
	         rhLeafIcon:"",
	         rhexpand:false,
			 showcheck:true,
			 childOnly:true,
	         theme: "bbit-tree-no-lines",
	         url  :"SY_COMM_INFO.dict.do"
	        };
	         var tree = new rh.ui.Tree(setting);
	         $('.content-navTree').append(tree.obj);
	 });
	 
	 function getFzgList(){
			var param = {};
	 		param["STATION_TYPE"]=STATION_TYPE;
	 		param["STATION_NO"]=STATION_NO;
	 		param["xm_id"]=xm_id;
	 		var fzgList= FireFly.doAct("TS_BMLB_BM", "getFzgValue", param,true,false);
	 		return fzgList['_DATA_'];
		}
	
	 function deletec(){
			var checkArray = document.getElementsByName("checkboxaa");
			var kslxArray = document.getElementsByName("checkname1");
			for(var j=0;j<checkArray.length;j++){
				for(var i=0;i<kslxArray.length;i++){
		     		if(kslxArray[i].value==checkArray[j]){
		     			kslxArray[i].disabled=false;
		     		}
				}
			}
		    
		}
	//跨序列资格考试选择数量上限
	var total = 0;
	function change(obj){
		var kslxArray = document.getElementsByName("checkname1");
		if($(obj).prop("checked")){ 
			total+=1;
			}else{
				total-=1;
				if(total==1){
			     	for(var i=0;i<kslxArray.length;i++){
			     		if(kslxArray[i].checked){
			     			
			     		}else{
			     			kslxArray[i].disabled=false;
			     		}
			     	}
				}
			}
		if(total==2){
	     	for(var i=0;i<kslxArray.length;i++){
	     		if(kslxArray[i].checked){
	     			
	     		}else{
	     			kslxArray[i].disabled=true;
	     		}
	     	}
		}
	}

	function goBack(){
		window.history.go(-1);
	}
	//模态页面 取消按钮 删除之前append的tr
	function quxiao(){
		//获取到table
		var motaitable = document.getElementById("motaitable");
		var rowlength = motaitable.rows.length-1;
		for(var i=rowlength;i>1;i--){
			motaitable.deleteRow(i);
		}
	}

	//删除
	//跨序列复选框变动
	function change2(obj){
		var arrChk=$("input[name='checkboxaa']"); 
		if($(obj).prop("checked")){ 
			}else{
				var tab = document.getElementById("tablehang");
				var hang = tab.rows.length;
		 	    var kslxArray = document.getElementsByName("checkboxaa");
		     	for(var i=0;i<kslxArray.length;i++){
		     		if(kslxArray[i].checked){
				       
					}else{
				var row =obj.parentNode.parentNode;
				var tds=row.getElementsByTagName("td");
				var j=obj.parentNode.parentNode.rowIndex;
				var hanghao = tds[5].innerText;
				//删除时清空数组中的元素
				var ys = tds[9].innerText;
				for(var i=0;i<xkArg.length;i++){
					xkArg[i]
					if(xkArg[i].ID===ys){
						var index = xkArg.indexOf(xkArg[i]);
						if (index > -1) {
							xkArg.splice(index, 1);
						}
					}
				}
				//删除行
				tab.deleteRow(j);
				if(tds[4].innerText=="中级"){
				middlenum--;
					}else if(tds[4].innerText=="高级"){
						highbmnum--;
					}
		     		
					}
		     	}
	     		
		}
	}
	//页面已选中级考试数目
	var middlenum=0;
	var highbmnum=0;
	//跨序列的考试
	function fuzhi(){
		var strchecked = checked.join(",");
		paramstr={};
		paramstr["checked"]=strchecked;
		var data = FireFly.doAct("TS_BMLB_BM","getCheckedData",paramstr,true,false);
			var alldata = data.list;
			for(var i=0;i<alldata.length;i++){
				var paduan = false;
				var arrChk=$("input[name='checkboxaa']"); 
				if(arrChk.length!=0){
					for(var j=0;j<arrChk.length;j++){
						var tr = arrChk[j].parentNode.parentNode;
						var tds=tr.getElementsByTagName("td");
						var kslb_id=tds[9].innerText;
						if(kslb_id==alldata[i].KSLBK_ID){
							paduan=true;
						}
					}
				}
				if(paduan){
					continue;
				}
				var kslb_name=alldata[i].KSLBK_NAME;
			       var kslb_xl=alldata[i].KSLBK_XL;
			       var kslb_mk=alldata[i].KSLBK_MK;
			       var kslb_type_name=alldata[i].KSLBK_TYPE_NAME;
				   var kslbk_id = alldata[i].KSLBK_ID;
				   var kslb_code=alldata[i].KSLBK_CODE;
			       var kslb_xl_code=alldata[i].KSLBK_XL_CODE;
			       var kslb_mk_code=alldata[i].KSLBK_MKCODE;
			       var kslb_type = alldata[i].KSLBK_TYPE;
			       if(kslb_type==2){
			    	   middlenum++;
			       }
			       if(kslb_type==3){
			    	   highbmnum++;
			       }
			       tbody=document.getElementById("goods");
			       var ntr = tbody.insertRow();
			       ntr.innerHTML=
			       '<td ><input checked="checked" type="checkbox" onchange="change2(this)" name="checkboxaa"></td>'+
			       '<td >'+kslb_name+'</td>'+
			       '<td >'+kslb_xl+'</td>'+
			       '<td >'+kslb_mk+'</td>'+
			       '<td >'+kslb_type_name+'</td>'+
			       '<td class="rhGrid-td-hide"><div>cannot</div></td>'+
			       '<td ><div id="'+kslbk_id+'"></div></td>'+
			       '<td ><div id="'+kslbk_id+'yzjg"></div></td>'+
				   '<td class="rhGrid-td-hide" ><input type="text" name="zgksname" value="'+kslbk_id+'"></td>'+
				   '<td class="rhGrid-td-hide" >'+kslbk_id+'</td>'+
				   '<td class="rhGrid-td-hide" >'+kslb_code+'</td>'+
				   '<td class="rhGrid-td-hide" >'+kslb_xl_code+'</td>'+
				   '<td class="rhGrid-td-hide" >'+kslb_mk_code+'</td>'+
				   '<td class="rhGrid-td-hide" >'+kslb_type+'</td>';
			       var xk = {};
			       xk['ID'] = kslbk_id;
			       xk['BM_LB'] = kslb_code;
			       xk['BM_XL'] = kslb_xl_code;
			       xk['BM_MK'] = kslb_mk_code;
			       xk['BM_TYPE'] =kslb_type;
			       xkArg.push(xk);
			}
		}
	
		/*debugger;
		var tab = document.getElementById("tablehang");
 	    var kslxArray = document.getElementsByName("checkname1");
     	for(var i=0;i<kslxArray.length;i++){
     		if(kslxArray[i].checked && !kslxArray[i].disabled){
		       var tr=kslxArray[i].parentNode.parentNode;
		       var tds=tr.getElementsByTagName("td");
		       var www=tds[0].innerText;
		       var kslb_name=tds[1].innerText;
		       var kslb_xl=tds[2].innerText;
		       var kslb_mk=tds[3].innerText;
		       var kslb_type_name=tds[4].innerText;
		       var hanghao = tds[5].innerText;
			   var kslb_id = tds[6].innerText;
			   var kslb_code=tds[7].innerText;
		       var kslb_xl_code=tds[8].innerText;
		       var kslb_mk_code=tds[9].innerText;
		       var kslb_type = tds[10].innerText;
		       tbody=document.getElementById("goods");
		       var ntr = tbody.insertRow();
		       ntr.innerHTML=
		       '<td ><input checked="checked" type="checkbox" onchange="change2(this)" name="checkboxaa"></td>'+
		       '<td >'+kslb_name+'</td>'+
		       '<td >'+kslb_xl+'</td>'+
		       '<td >'+kslb_mk+'</td>'+
		       '<td >'+kslb_type_name+'</td>'+
		       '<td class="rhGrid-td-hide" >'+hanghao+'</td>'+
		       '<td ><div id="'+kslb_id+'"></div></td>'+
		       '<td ><div id="'+kslb_id+'yzjg"></div></td>'+
			   '<td class="rhGrid-td-hide" ><input type="text" name="zgksname" value="'+kslb_id+'"></td>'+
			   '<td class="rhGrid-td-hide" >'+kslb_id+'</td>'+
			   '<td class="rhGrid-td-hide" >'+kslb_code+'</td>'+
			   '<td class="rhGrid-td-hide" >'+kslb_xl_code+'</td>'+
			   '<td class="rhGrid-td-hide" >'+kslb_mk_code+'</td>'+
			   '<td class="rhGrid-td-hide" >'+kslb_type+'</td>';
		      kslxArray[i].disabled=true;
		       var xk = {};
		       xk['ID'] = kslb_id;
		       xk['BM_LB'] = kslb_code;
		       xk['BM_XL'] = kslb_xl_code;
		       xk['BM_MK'] = kslb_mk_code;
		       xk['BM_TYPE'] =kslb_type;
		       xkArg.push(xk);
			}
     	}
     	
	}*/
	//兼容火狐、IE8   
    //显示遮罩层    
    function showMask(){     
        $("#mask").css("height",$(document).height());     
        $("#mask").css("width",$(document).width());     
        $("#mask").show();     
    }  
    //隐藏遮罩层  
    function hideMask(){     
        $("#mask").hide();     
    }  
	//等级改变事件
	function changeyk(obj){
		var sel = document.getElementById("lxid");
		var selected_val = sel.options[sel.selectedIndex].value;
		yk["BM_TYPE"]=selected_val;
		 var tds = $("#tableid tbody").find("tr").find("td");
		    $($(tds[7]).find("div").eq(0)).html("")
		   $($(tds[6]).find("div").eq(0)).html("");
	}	
	
	//获取应考试的值
	function tijiao(){
		var motaitable = document.getElementById("motaitable");
		var rowlength = motaitable.rows.length-1;
		var canhightnum = $("#canheighnum").text();
		var canmiddlenum = $("#cannum").text();
		if(highbmnum>canhightnum){
			alert("选择的高级考试数目超过上限，请删除再提交");
			$("#tjbt").attr("data-target","");
			//获取到table
			for(var i=rowlength;i>1;i--){
				motaitable.deleteRow(i);
			}
			return;
		}
		if(middlenum>canmiddlenum){
			alert("选择的中级考试数目超过上限，请删除再提交");
			$("#tjbt").attr("data-target","");
			//获取到table
			for(var i=rowlength;i>1;i--){
				motaitable.deleteRow(i);
			}
			return;
		}
		var div = $("div[name=existedbm]");
		if(div.length!=0){
			alert("请先删除已有的报名");
			$("#tjbt").attr("data-target","");
			//获取到table
			for(var i=rowlength;i>1;i--){
				motaitable.deleteRow(i);
			}
			return;
		}
		
		//获取手机号码
		 	var ryl_mobile = document.getElementById("user_mobile2").value=document.getElementById("user_mobile1").value; 
			//获取 当前页面中checkbox选中的数据
		 	//判断是否已经 进行了资格验证
			var arrChk=$("input[name='checkboxaa']"); 
			tbody=document.getElementById("xinxi");
			for(var i=0;i<arrChk.length;i++){
			 //得到tr
			  var tr=arrChk[i].parentNode.parentNode;
		      var tds=tr.getElementsByTagName("td");
		      if($(tds[6]).find("div").length<2){
		    	  alert("请先进行资格验证");
		    	  $("#tjbt").attr("data-target","");
		    	//获取到table
		  		var motaitable = document.getElementById("motaitable");
		  		var rowlength = motaitable.rows.length-1;
		  		for(var i=rowlength;i>1;i--){
		  			motaitable.deleteRow(i);
		  		}
		    	  return;
		      }
		      var ntr = tbody.insertRow();
		      
		      if(tds.length==11){
		    	  //本序列
		    	  if(i==0){
				      ntr.innerHTML=
				      '<td style="text-align:right;color:lightseagreen">报考类型</td>'+
				      '<td style="text-align:center">'+tds[1].innerHTML+'</td>'+
				      '<td style="text-align:left">'+tds[2].innerHTML+'</td>'+
				      '<td style="text-align:left">'+$("#mkid").children('option:selected').text()+'</td>'+
				      '<td style="text-align:left">'+$("#lxid").children('option:selected').text()+'</td>';
			     	}else{
			    	   ntr.innerHTML=
					       '<td style="text-align:center;color:blue"></td>'+
					       '<td style="text-align:center">'+tds[1].innerHTML+'</td>'+
					       '<td style="text-align:left">'+tds[2].innerHTML+'</td>'+
					       '<td style="text-align:left">'+$("#mkid").children('option:selected').text()+'</td>'+
					       '<td style="text-align:left">'+$("#lxid").children('option:selected').text()+'</td>';
			       }
		    	  continue;
		      }
		      if(i==0){
			      ntr.innerHTML=
			      '<td style="text-align:right;color:lightseagreen">报考类型</td>'+
			      '<td style="text-align:center">'+tds[1].innerHTML+'</td>'+
			      '<td style="text-align:left">'+tds[2].innerHTML+'</td>'+
			      '<td style="text-align:left">'+tds[3].innerHTML+'</td>'+
			      '<td style="text-align:left">'+tds[4].innerHTML+'</td>';
		     	}else{
		    	   ntr.innerHTML=
				       '<td style="text-align:center;color:blue"></td>'+
				       '<td style="text-align:center">'+tds[1].innerHTML+'</td>'+
				       '<td style="text-align:left">'+tds[2].innerHTML+'</td>'+
				       '<td style="text-align:left">'+tds[3].innerHTML+'</td>'+
				       '<td style="text-align:left">'+tds[4].innerHTML+'</td>';
		       }
			}
			 $("#tjbt").attr("data-target","#tiJiao");
	}
	

	//岗位类别名称代码
	var STATION_TYPE_CODE="";
	//岗位类别名称
	var STATION_TYPE="";
	// 序列名称代码
	var STATION_NO_CODE= "";
		//序列名称
	var STATION_NO= "";
		//职务层级
		var DUTY_LEVEL="";
	//查询 筛选出所有的数据
		 var lbname = "";
		 var xlname= "";
		 var lbcode="";
		 var xlcode= "";
	var result1 = "";
	function matchinfo(){
		//人员信息
		var result =  FireFly.byId("SY_HRM_ZDSTAFFPOSITION", user_code);
		if(result!=null){
			 STATION_TYPE_CODE=result.STATION_TYPE_CODE;
			 STATION_TYPE = result.STATION_TYPE;
			 STATION_NO = result.STATION_NO;
			 STATION_NO_CODE= result.STATION_NO_CODE;
			 ADMIN_DUTY = result.ADMIN_DUTY;
			 $("#gwlb").html(STATION_TYPE);
			 $("#gwxl").html(STATION_NO);
			 $("#zwcj").html(ADMIN_DUTY);
		}
		var wherexl = "AND KSLB_CODE="+"'"+STATION_TYPE_CODE+"'"+" AND KSLB_XL_CODE="+"'"+STATION_NO_CODE+"'"+" AND XM_ID="+"'"+xm_id+"'";
		var param={};
		param["where"]=wherexl;
		 result1 = FireFly.doAct("TS_BMLB_BM","getMatchData",param);
				if(result1.list==""){
					return;
				}
				debugger;
			var pageEntity = result1.list;
			 var kslb_id = pageEntity[0].KSLB_ID;
			  lbname = pageEntity[0].KSLB_NAME;
			  xlname= pageEntity[0].KSLB_XL;
			  lbcode= pageEntity[0].KSLB_CODE;
			  xlcode= pageEntity[0].KSLB_XL_CODE;
			 //拼接 tr
			 var tr = document.createElement('tr');
			tr.innerHTML='<td><input class="rhGrid-td-hide" type="text" name="checkboxaa"></td>'+
				'<td>'+lbname+'</td>'+
				'<td>'+xlname+'</td>'+
				'<td width="27%"><select id="mkid" onchange="typeId(this)"></select></td>'+
				'<td width="10%"><select id="lxid" onchange="changeyk(this)"><option></option></select></td>'+
				'<td class="rhGrid-td-hide"><input type="text" id="zglbid" name="zgksname" value='+kslb_id+'></td>'+
				'<td width="20%"><div id='+kslb_id+'></div></td>'+
				'<td width="15%"><div id="'+kslb_id+'yzjg"></div></td>'+
				'<td class="rhGrid-td-hide"><div>cannot</div></td>'+
				'<td class="rhGrid-td-hide"><div>biaoshi</div></td>'+
				'<td class="rhGrid-td-hide"><div>biaoshi</div></td>';
			
			 $("#tableid tbody").append(tr);
	
	}
			
			//模块改变事件
			function typeId(obj){
				var tab = document.getElementById("tableid");
			    //表格行数
			    var rows = tab.rows.length;
			    if(rows>1){
				var mkvalue= $("#mkid").children('option:selected').val();
				var param = {};
				param["MK"]=mkvalue;
				param["lbname"]=lbname;
				param["xlname"]=xlname;
				param["xm_id"]=xm_id;
				var ww= FireFly.doAct("TS_BMLB_BM", "getMkvalue", param,true,false);
				hh= ww.list;
				var tyArray = hh.split(",");
				var select = document.getElementById("lxid");
				jQuery("#lxid").empty();          //把select对象的所有option清除掉
				for(var i=0;i<tyArray.length;i++){
					select.options[i]=new Option((tyArray[i]=="1")?"初级":(tyArray[i]=="2")?"中级":(tyArray[i]=="3")?"高级":"无",tyArray[i]);
				}
				var tab = document.getElementById("tableid");
			    //表格行数
			    var rows = tab.rows.length;
			    if(rows>1){
					yk["BM_LB"]=lbcode;
					yk["BM_XL"]=xlcode;
					yk["ID"]=document.getElementById("zglbid").value;
					yk["BM_MK"]=mkvalue;
					var sel = document.getElementById("lxid");
					var selected_val = sel.options[sel.selectedIndex].value;
					yk["BM_TYPE"]=selected_val;
			       }
				}
			    var tds = $("#tableid tbody").find("tr").find("td");
			    $($(tds[7]).find("div").eq(0)).html("")
			   $($(tds[6]).find("div").eq(0)).html("");
			}
function mkfuzhi(){

	var obj = result1.mkoption;
	if(obj==""){
		return;
	}
	var i=0;
	var select = document.getElementById("mkid");
	jQuery("#mkid").empty();
	for(var key in obj){
		if(i==0){
			select.options[i]=new Option(key,obj[key],true,true);
		}else{
			select.options[i]=new Option(key,obj[key]);
		}
		i++;
	}
}
var highnum=0;

	//统计 已报名的考试  针对中级
	function tongji(){
		var param={};
		param["lbcode"]=STATION_TYPE_CODE;
		param["xlcode"]=STATION_NO_CODE;
		param["user_code"]=user_code;
		var data = FireFly.doAct("TS_BMLB_BM","getBmNum",param,true,false);
		var num = data.allnum;
		$("#allnum").html(num);//中级
		var maxnum = FireFly.getConfig("TS_BM_MIDDLE_MAXNUM").CONF_VALUE;//配置中级最大数
		var maxhigh = FireFly.getConfig("TS_BM_HIGH_MAXNUM").CONF_VALUE;//配置高级最大数
		//高级剩余次数
		var high = data.highnum;
		var highcanum = maxhigh-high;
		var cannum = maxnum-num;//剩余次数 中级
		$("#cannum").html(cannum);
		var serianum = data.serianum;//本序列已报名
		var othernum = data.othernum;//夸序列所有
		$("#gaoji").html(high);
		$("#canheighnum").html(highcanum);
		highnum=data.highnum;
}
	//删除已报名的考试
	function deleterow(obj){
		var j=obj.parentNode.parentNode.parentNode.rowIndex;
		var tab = document.getElementById("tablehang");
		tab.deleteRow(j);
		
	}
	