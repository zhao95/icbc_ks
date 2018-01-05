var user_code = System.getVar("@USER_CODE@");
var user_name = System.getVar("@USER_NAME@");
var user_sex = System.getVar("@USER_SEX@");
var odept_name = System.getVar("@ODEPT_NAME@");
var dept_code = System.getVar("@DEPT_CODE@");
var maxnum = FireFly.getConfig("TS_BM_MIDDLE_MAXNUM").CONF_VALUE;
var maxhigh = FireFly.getConfig("TS_BM_HIGH_MAXNUM").CONF_VALUE;
var param = {};
param["user_code"]=user_code;
var user_cmpy_date = System.getVar("@USER_CMPY_DATE@");
var xm_id  = $("#xmidval").val();

var paramstrs = {};
paramstrs["XM_ID"]=xm_id;
var resultcount = FireFly.doAct("TS_BMLB_BM","getShState",paramstrs);
var countflag = resultcount.count;
if(resultcount.count==0){
	$("#zgyzbt").attr("disabled","disabled");
}else if(resultcount.count==2){
	$("#zgyzbt").attr("disabled","disabled");
}

var bm_start="";
var bm_end = "";
var xm_name = "";
var successinfo = "";
var yiyistate = "0";
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
		var bm_ksxz=bm_ksxzs.replace(/\n/g,"<br>");
		 bm_start = bminfojson[0].BM_START;
		 bm_end = bminfojson[0].BM_END;
		var bm_name = bminfojson[0].BM_NAME;
		successinfo = data.SH_TGTSY;
		failerinfo = data.SH_BTGTSY;
		$("#xmnamecon").html(xm_name);
		$("#ksxzcon").html(bm_ksxz);
	});
}
	function checky(){
		var param = {};
		var bminfo={};//将bminfo放入param中  
		bminfo['XM_ID'] = xm_id;
		bminfo['BM_CODE'] = user_code;
		bminfo['BM_STARTDATE'] = bm_start;
		bminfo['BM_ENDDATE'] = bm_end;
		var neAry=xkArg;
		if(yk.ID){
			neAry=xkArg.concat(yk);	//yk为考试参数   放入数组中
		}
		//数组去重
		for(var i=0; i < neAry.length; i++) {
		    for(var j=i+1;j< neAry.length; j++) {
		        if(neAry[i].ID == neAry[j].ID) {
		        	neAry.splice(neAry[j],1);
		        }
		    }
		}
		var checkArray = $("input[name=checkboxaa]");
		
		var checkeddata = [];
		for(var m=0;m<checkArray.length;m++){
			for(var n=0;n<neAry.length;n++){
			var length= $($(checkArray[m].parentNode.parentNode).find("td").eq(6)).find("div").length;
			if(length<2){
				if(neAry[n].ID==$($(checkArray[m].parentNode.parentNode).find("td").eq(6)).find("div").eq(0).attr("id")){
					checkeddata.push(neAry[n]);
				}
			}
			}
		}
		
		if(checkeddata.length==0){
			return;
		}
			param['BM_INFO'] = JSON.stringify(bminfo);
			param['BM_LIST'] = JSON.stringify(checkeddata);
			
			var parambm = {};	//已报名的考试
			parambm["user_code"]=user_code;
			parambm["xmid"]=xm_id;
			var results = FireFly.doAct("TS_BMLB_BM","getBmData",parambm);
		FireFly.doAct("TS_XMGL_BMSH", "vlidates", param, false,true,function(data){
    		yzgz=data;
    		//获取后台传过来的key
         	for(var i=0;i<checkeddata.length;i++){
         		var FLAG = false;//相同考试标志
   				var xlflag = false; //相同序列互斥标志
   				var xlcode = "";	//记录当前考试的序列编码
   				var resdata = results.list;//已报名的数据
         		var a=checkeddata[i].ID;//当前报考考试id
         		var bm_xl = checkeddata[i].BM_XL;//序列互斥
         		var yzjg=a+"yzjg";
         		for(var z=0;z<resdata.length;z++){
   					if(resdata[z].KSLBK_ID==a){
   						//判断此考试是否已报名  如果已报名审核通过 必须删除 才能提交
   						xlcode=resdata[z].BM_XL_CODE;
   						FLAG = true;
   					}
   					/*if(resdata[z].BM_XL_CODE==bm_xl){
   						//本序列 只能报名一个
   						xlflag = true;
   					}*/
   				}
         		var divtext1 = $("#"+a).html();//获取div对应的数组
       			
       			if(divtext1==null||divtext1.length==0){//append内容之前判断是否有内容
   				if(FLAG){
   					if(xlcode==STATION_NO_CODE){
   						$("#"+a).append('<div class="btn" name="existedbm" type="button" style="color:red;backgroundcolor:lightseagreen">已报名此类考试,请撤销再提交或请选择其它考试</div>');
   						$("#"+yzjg).append("验证不通过");
   					}else{
   						$("#"+a).append('已报名此考试,请撤销再报名');
   						$("#"+a).append('<div class="btn" name="existedbm" onclick="deleterow(this)" type="button" style="color:red;backgroundcolor:lightseagreen">请删除</div>');
   						$("#"+yzjg).append("验证不通过");
   					}
   					/*$("#"+yzjg).parent().parent().find("input[name='checkboxaa']:first").prop("checked",false);*/
   					continue;
   					
   				}
   				/*if(xlflag){
   					if(bm_xl==STATION_NO_CODE){
   						$("#"+a).append('<div class="btn" name="existedbm" type="button" style="color:red;backgroundcolor:lightseagreen">本序列只能报考一个,请撤销再提交或请选择其它考试</div>');
   						$("#"+yzjg).append("验证不通过");
   					}else{
   						$("#"+a).append('已报名此序列,请撤销再报名');
   						$("#"+a).append('<div class="btn" name="existedbm" onclick="deleterow(this)" type="button" style="color:red;backgroundcolor:lightseagreen">请删除</div>');
   						$("#"+yzjg).append("验证不通过");
   					}
   					$("#"+yzjg).parent().parent().find("input[name='checkboxaa']:first").prop("checked",false);
   					continue;
   				}*/
       			}
         		if(data.none=="true"){//如果没有引用规则的话 直接通过
         			
         			$("#"+a).append('<div style="height:5px;"></div>');
         			$("#"+a).append('<div style="height:5px;"></div>');
         			$("#"+a).append('<div style="height:5px;"></div>');
         			$("#"+yzjg).append("验证通过");
         			
         		}else{
       			var dataArray =data[a];	//获取验证结果div的id
       			var divtext1 = $("#"+a).html();//获取div对应的数组
       			
       			if(divtext1==null||divtext1.length==0){//append内容之前判断是否有内容
       				var shArray=true;
       				var shs = true;
       				var shti = "";
       				var truetisi = "";
       				var tishiyu = "";
       				var othergz = "";
       				var zsgz = "";
       				for(var j=0;j<dataArray.length;j++){
       					
       					var divid = a+j+"xxx";
       					
       					var littlegz = dataArray[j]["littlega"];
       					var sss =JSON.stringify(littlegz);
       					if(j==0){
       						$("#"+a).append('<div style="height:5px;"></div>');
       					}
       					
       					if(dataArray[j].VLIDATE=="true"){
       						if(dataArray[j].NAME=="证书规则"){
       							$("#"+a).append('<div><img src="/ts/image/u4719.png">&nbsp;'+dataArray[j].NAME+'&nbsp;&nbsp<span style="padding:2px 9px" class="btn btn-success" name="'+divid+'" onclick="showinfo(this)">详细</span></div><div style="display:none" id="'+divid+'">'+sss+'</div>');
       						}else{
       						 $("#"+a).append('<div><img src="/ts/image/u4719.png">&nbsp;'+dataArray[j].NAME+'&nbsp;&nbsp</div>');
       						}
						}else if(dataArray[j].VLIDATE=="false"){
							if(dataArray[j].NAME=="证书规则"){
								$("#"+a).append('<div style="color:red;"><img src="/ts/image/u4721.png">&nbsp;'+dataArray[j].NAME+'&nbsp;&nbsp<span style="padding:2px 9px" class="btn btn-success" name="'+divid+'" onclick="showinfo(this)">详细</span></div><div style="display:none" id="'+divid+'">'+sss+'</div>');
							}else{
								 $("#"+a).append('<div><img src="/ts/image/u4721.png">&nbsp;'+dataArray[j].NAME+'&nbsp;&nbsp</div>');
							}
						}
						
						shti=dataArray[j].TISHI;
						$("#"+a).append('<div style="height:5px;"></div>');
						
						if(dataArray[j].VLIDATE=="false"){
							if(countflag!=1){
							if(shti!="TRUE"){
								shArray=false;
							}else{
								truetisi="true"
								tishiyu=dataArray[j].tishiyu;
							}
							}else{
								shArray=false;
							}
						}
						if(dataArray[j].othergz=="false"){
							//除了证书 规则  其他规则不通过
							othergz="false";
						}
						if(dataArray[j].zsgz!=""){
							//证书规则 通过或不通过
							 zsgz = dataArray[j].zsgz
						}
						
						
       				}
       				if(othergz!="false"&&zsgz!=""){
       					//启用证书规则
       					if(zsgz=="true"){
       						//审核通过
       						yiyistate="0";
       					}else{
       						//审核不通过  可进行异议
       						yiyistate="2";
       					}
       				}
       				if(countflag==1){
       					//无手动
           				if(shArray==false){
           					if(""==failerinfo){
           						$("#"+a).append('<div></div>');
               					$("#"+a).append('<div></div>');
           						$("#"+yzjg).append('验证不通过');
           					}else{
           						$("#"+yzjg).append(failerinfo);
           					}
           					/*$("#"+yzjg).parent().parent().find("input[name='checkboxaa']:first").prop("checked",false);*/
           				}if(shArray==true){
           					$("#"+a).append('<div></div>');
           					$("#"+a).append('<div></div>');
           					if(""==successinfo){
           						$("#"+yzjg).append("验证通过");
           					}else{
           						$("#"+yzjg).append(successinfo);
           					}
           					$("#"+yzjg).append('<div></div>');
           					$("#"+yzjg).append("<div><a window.open('/qt/jsp/examref.jps') href='#'>相关学习材料</a></div>");
           					$("#"+yzjg).click(function(){
       							window.open('/qt/jsp/examref.jsp');
       						});
           				}
       				}else{
       					if(shArray==true&&truetisi=="true"){
       						$("#"+a).find("img[src='/ts/image/u4721.png']").attr("src","/ts/image/u4099.png");//xx改为 问号
       						/*$("#"+a).append('<div style="color:red;"><img src="/ts/image/u4721.png">&nbsp;'+dataArray[j].NAME+'</div>');*/
       						$("#"+a).append('<div">管理任职已满&nbsp;&nbsp;<input style="width:20%" name="yzspan"></input>&nbsp;&nbsp;年</div>');
       						$("#tishiyu").html(tishiyu);
       						$("#yzxx").modal("show");
       					}
       					if(shArray==false){
       						if(""==failerinfo){
       							$("#"+yzjg).append('验证不通过');
       						}else{
       							$("#"+yzjg).append(failerinfo);
       						}
/*       						$("#"+yzjg).parent().parent().find("input[name='checkboxaa']:first").prop("checked",false);
*/       					}if(shArray==true){
       						$("#"+a).append('<div></div>');
       						$("#"+a).append('<div></div>');
       						if(""==successinfo){
       							$("#"+yzjg).append("验证通过");
       						}else{
       							$("#"+yzjg).append(successinfo);
       						}
       						$("#"+yzjg).append('<div></div>');
       						$("#"+yzjg).append('<div><a href="#">相关学习材料</a></div>');
       						$("#"+yzjg).click(function(){
       							window.open('/qt/jsp/examref.jsp');
       						});
       					}
       				}
	       		}
         		}
         	}
         	$("#loading").modal("hide");
    	});	
	}
	//提交所有数据
	$("#mttijiao").click(function(){
		$(this).unbind("click");
		$("#tjloading").modal("show");

		is_confirm=false;
		//获取手机号码
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
		var checkArray = $("input[name=checkboxaa]:checked");
		var checkeddata = [];
		for(var m=0;m<checkArray.length;m++){
			for(var n=0;n<neAry.length;n++){
				if(neAry[n].ID==$($(checkArray[m].parentNode.parentNode).find("td").eq(6)).find("div").eq(0).attr("id")){
					//只提交选中的考试
					neAry[n].YIYIST=yiyistate;
					neAry[n].YEAR="";
					$($(checkArray[m].parentNode.parentNode).find("td").eq(6)).find("input[name='yzspan']").each(function(){
						if($(this).val()==""){
							neAry[n].YEAR=0;
						}else{
							neAry[n].YEAR = $(this).val();
						}
					})
					checkeddata.push(neAry[n]);
				}
			}
		}
		param["USER_CODE"] = user_code;
		param["USER_NAME"] = user_name;
		param["USER_SEX"] = user_sex;
		param["ODEPT_NAME"] = odept_name;
		param["USER_CMPY_DATE"] = user_cmpy_date;
		param["XM_ID"] = xm_id;
		param["BM_START"] = bm_start;
		param["BM_END"] = bm_end;
		param["XM_NAME"] = xm_name;
		param["DEPT_CODE"]=dept_code;
			param['BM_LIST'] = JSON.stringify(checkeddata);
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
				/*if (ryl_mobile == "") {
					alert("手机号码不能为空");
				}
				if (ryl_mobile != "" && ryl_mobile != null) {
				}*/
				$("#loading").modal("show");//不再进行手机号校验
				var res = FireFly.doAct("TS_BMLB_BM", "addZgData", param,true,false);
				setTimeout("fanhui()", 1000);
					
			}
	})
	function fanhui(){//报名完后跳转回页面
		$("#bmbq").val("1");
		$("#form1").submit();
	}
	
	function showFzgList(showList){
		rlzyglmk=[];
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
				       if(kslb_mk=="无模块"){
				    	   kslb_mk="";
				       }
				       var kslb_type_name=alldata[i].KSLBK_TYPE_NAME;
					   var kslbk_id = alldata[i].KSLBK_ID;
					   var kslb_code=alldata[i].KSLBK_CODE;
				       var kslb_xl_code=alldata[i].KSLBK_XL_CODE;
				       var kslb_mk_code=alldata[i].KSLBK_MKCODE;
				       var kslb_type = alldata[i].KSLBK_TYPE;
				       if("02200401"==kslb_mk_code){
				    	   //模块code
				    	   var paramstr = {};
				    	   paramstr["mk"]=alldata[i].KSLBK_MK;
				    	   paramstr["type"]=kslb_type_name;
				    	   rlzyglmk.push(paramstr);
				       }
				       jQuery('#ksxxId').append([
				                         		'<tr style="height:30px">',
				                         		'<td style="text-align: center" width="10%"><image src="/ts/image/u4719.png"></image></td>',
				                         		'<td width="15%">'+kslb_name+'</td>',
				                         		'<td width="15%">'+kslb_xl+'</td>',
				                         		'<td width="45%">'+kslb_mk+'</td>',
				                         		'<td width="15%">'+kslb_type_name+'</td>',
				                         		'<td class="rhGrid-td-hide" id="HANGHAO'+i+'">'+i +'</td>',
				                         		'<td class="rhGrid-td-hide" >'+kslbk_id+'</td>',
				                         		'<td class="rhGrid-td-hide">'+kslb_code+'</td>',
				                         		'<td class="rhGrid-td-hide">'+kslb_xl_code+'</td>',
				                         		'<td class="rhGrid-td-hide">'+kslb_mk_code+'</td>',
				                         		'<td class="rhGrid-td-hide">'+kslb_type+'</td>',
				                         	'</tr>'
				                         	].join('')
				                         	);  
				       
				}
				       
		/*for(var i=0; i<showList.length;i++){
			var showItem = showList[i];
			var kslb_id = showItem.KSLB_ID;
			var kslb_name = showItem.KSLB_NAME;
			var kslb_xl = showItem.KSLB_XL;
			var kslb_mk = showItem.KSLB_MK;
			if(kslb_mk=="无模块"){
		    	   kslb_mk="";
		       }
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
		}*/
	}
	
	var rlzyglmk = [];
	var checked = [];
	var yk={};
	var xkArg=[];//考试结果
	var yzgz;//资格验证后端返回到前端的数据
	var sqlstr = "";
	 $(function(){ 
		 xminfoshow();
		 matchinfo();
		 mkfuzhi();
		 var param1 = {};
		 param1["DUTY_LV_CODE"]=DUTY_LEVEL_CODE;
		 param1["STATION_TYPE_CODE"]=STATION_TYPE_CODE;
		 param1["STATION_NO_CODE"]=STATION_NO_CODE;
		 var cengji =  FireFly.doAct("TS_BMLB_BM","getcengji",param1);
		 var cengjinum = cengji.num;
		 var sqls = "";
		 if(cengjinum==""){
			 //防止抛异常
			 cengjinum = 10;
			 sqlstr = " AND (KSLBK_TYPE<="+cengjinum+" or KSLBK_TYPE is null)";
			 sqls = " AND (KSLB_TYPE<="+cengjinum+" or KSLB_TYPE is null)";
		 }else{
			 cengjinum+=1;
			 belongnum=cengjinum;
			 sqlstr = " AND (KSLBK_TYPE<="+cengjinum+" or KSLBK_TYPE is null)";
			 sqls = " AND (KSLB_TYPE<="+cengjinum+" or KSLB_TYPE is null)";
		 }
		 typeId(obj);
		 tongji();
		 var allList=getFzgList(sqls);
		 showFzgList(allList);
		 
		 if(cengjinum==2){
			 //只能报
			 $("#gaoji").html(0);
			 $("#canheighnum").html(0);
		 }
		 //没有数据的父节点不显示
		/* +" AND KSLBK_ID not in (SELECT KSLBK_ID FROM TS_XMGL_BM_KSLBK WHERE KSLBK_MK='无模块' AND KSLBK_TYPE is null)*/  
		 var itemid = "";
		 
		 var lbparam = {};
		 lbparam["xm_id"]=xm_id;
		 lbparam["STATION_NO_CODE"]=STATION_NO_CODE;
		 lbparam["STATION_TYPE_CODE"]=STATION_TYPE_CODE;
		 var resultFlag = FireFly.doAct("TS_BMLB_BM","checkXl",lbparam);
		 var extWhere="";
		 //不显示已报名的考试
		 var paramb = {};
		 paramb["xmid"]=xm_id;
		 var ressultids = FireFly.doAct("TS_BMLB_BM","getYibmids",paramb)
		 var ids = ressultids.ids;
		 if(resultFlag.flag=="false"){
		 extWhere="AND KSLBK_ID IN ("+ids+") AND (KSLBK_XL_CODE<>'"+STATION_NO_CODE+"' OR KSLBK_XL_CODE is null)";
		 }else{
		 extWhere="AND KSLBK_ID IN ("+ids+") AND KSLBK_CODE!='"+STATION_TYPE_CODE+"'";
		 }
		 var setting={data
	             :FireFly.getDict('TS_XMGL_BM_KSLBK','KSLBK_PID',extWhere),
	         dictId:"TS_XMGL_BM_KSLBK",expandLevel:1,
	         oncheckboxclick: function(item, s, id) {
	        	 //人力资源管理 两个模块 只能选中一个
	        	 if(rlzyglmk!=""){
	        		 var paramRl = {};
	        		 paramRl["mk"] = JSON.stringify(rlzyglmk);
	        		 paramRl["type"] = item['NAME'];
	        		 paramRl["kslbk_id"] = item['ID'];
	        		 var result = FireFly.doAct("TS_BMLB_BM","rlZyPd",paramRl);
	        		 if(result.flag=="true"){
	        			 alert("人力资源管理序列只能选择一个模块,请取消在进行选择");
	        			 return false;
	        		 }
	        	 }
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
	        		if(item.hasOwnProperty("CHILD")){
	        		}else{
	        			checked.push(itemjson);
	        			showFzgList(obj);
	        		}
	        	}
	         },
	         /*onnodeclick :function (item) {
	        	 var idName=item['NAME'];
	        	 for(var i=0; i<allList.length;i++){
	        		 var showItem = allList[i];
	        		 console.log(showItem);
	        		 alert(showItem.KSLB_CODE);
	        		 alert(showItem.KSLB_MK_CODE);
	        		 alert(showItem.KSLB_XL_CODE);
	        		 
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
	         },*/
	         rhItemCode:"KSLBK_PID",
	         rhLeafIcon:"",
	         rhexpand:false,
			 showcheck:true,
			 childOnly:true,
	         theme: "bbit-tree-no-lines",
	         url  :"SY_COMM_INFO.dict.do"
	        };
		 var data1 = setting.data;
		 //如果没有叶子节点则不显示
		 for(var j=0;j<data1[0].CHILD.length;j++){
			 if(data1[0].CHILD[j].hasOwnProperty("CHILD")){
			 for(var m=0;m<data1[0].CHILD[j].CHILD.length;m++){
				 if(data1[0].CHILD[j].CHILD[m].hasOwnProperty("CHILD")){
				 for(var n=0;n<data1[0].CHILD[j].CHILD[m].CHILD.length;n++){
					 if(!data1[0].CHILD[j].CHILD[m].CHILD[n].hasOwnProperty("CHILD")){
						 var index = data1[0].CHILD[j].CHILD[m].CHILD.indexOf(data1[0].CHILD[j].CHILD[m].CHILD[n]);
						 data1[0].CHILD[j].CHILD[m].CHILD.splice(index,1);
					 }
				 }
			 }
			 }
			 }
		 }
		 //将无模块的数据上提
		 for(var j=0;j<data1[0].CHILD.length;j++){
			 if(data1[0].CHILD[j].hasOwnProperty("CHILD")){
			 for(var m=0;m<data1[0].CHILD[j].CHILD.length;m++){
				 if(data1[0].CHILD[j].CHILD[m].hasOwnProperty("CHILD")){
				 for(var n=0;n<data1[0].CHILD[j].CHILD[m].CHILD.length;n++){
					 if(data1[0].CHILD[j].CHILD[m].CHILD[n].NAME=="无模块"){
						 //将无模块下的 CHILD节点下的  pid改为无模块的  pid 
					for(var q = 0;q<data1[0].CHILD[j].CHILD[m].CHILD[n].CHILD.length;q++){
						data1[0].CHILD[j].CHILD[m].CHILD[n].CHILD[q].PID = data1[0].CHILD[j].CHILD[m].CHILD[n].PID;
					}
					data1[0].CHILD[j].CHILD[m].CHILD = data1[0].CHILD[j].CHILD[m].CHILD[n].CHILD;
					 }
				 }
				 }
				 }
			 }
		 }
		 setting.data=data1;
	         var tree = new rh.ui.Tree(setting);
	         $('.content-navTree').append(tree.obj);
	         
	 });
	 
	 function getFzgList(sqls){
			var param = {};
	 		param["STATION_TYPE"]=STATION_TYPE;
	 		param["STATION_NO"]=STATION_NO;
	 		param["xm_id"]=xm_id;
	 		param["str"]=sqls;
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
		is_confirm=false;
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
		if($(obj).prop("checked")){
			var arrChk=$("input[name='checkboxaa']").each(function(){
				$(this).prop("checked",true);
			})
			}else{
				var arrChk=$("input[name='checkboxaa']").each(function(){
					$(this).prop("checked",false);
				})
	}
	}
	//跨序列的考试
	function fuzhi(){
		var divstr="";
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
			       if(kslb_mk=="无模块"){
			    	   kslb_mk="";
			       }
			       var ks_time = alldata[i].KSLBK_TIME;
			       var kslb_type_name=alldata[i].KSLBK_TYPE_NAME;
				   var kslbk_id = alldata[i].KSLBK_ID;
				   var kslb_code=alldata[i].KSLBK_CODE;
			       var kslb_xl_code=alldata[i].KSLBK_XL_CODE;
			       var kslb_mk_code=alldata[i].KSLBK_MKCODE;
			       var kslb_type = alldata[i].KSLBK_TYPE;
			       tbody=document.getElementById("goods");
			       var ntr = tbody.insertRow();
			       ntr.innerHTML=
			       '<td ><input type="checkbox" name="checkboxaa"></td>'+
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
			       if(ks_time==""){
			    	   ks_time="0";
			       }
			       xk['BM_KS_TIME']=ks_time;
			       xk['KSLBK_TYPE_LEVEL']=alldata[i].KSLBK_TYPE_LEVEL;
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
		yk['BM_KS_TIME']=$(sel.options[sel.selectedIndex]).attr("name");
		yk["ID"]=$(sel.options[sel.selectedIndex]).attr("icode");
		$($(obj.parentNode.parentNode).find("td").eq(6)).find("div").eq(0).attr("id",yk["ID"]);
		$("#yzjg_info").find("div").eq(0).attr("id",yk["ID"]+"yzjg");
		 var tds = $("#tableid tbody").find("tr").find("td");
		    $($(tds[7]).find("div").eq(0)).html("")
		   $($(tds[6]).find("div").eq(0)).html("");
	}	
	
	$("#user_mobile1").blur(function(){
		var myreg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/; 
		if(!myreg.test($("#user_mobile1").val())){ 
		    alert('请输入有效的手机号码！'); 
		    return false; 
		} 
	})
	$("#yzinput").blur(function(){
		//验证 数字 和任职年限 
	})
	$("#user_mobile2").blur(function(){
		var myreg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/; 
		if(!myreg.test($("#user_mobile1").val())){ 
		    alert('请输入有效的手机号码！'); 
		    return false; 
		} 

	})
	
	
	//获取应考试的值
	function tijiao(){
		var bdyz = false;
		$("input[name='checkboxaa']:checked").each(function(){
			if($(this.parentNode.parentNode).find("input[name='yzspan']").length>0){
				var inputvalue = $(this.parentNode.parentNode).find("input[name='yzspan']").val();
				if(""==inputvalue){
					alert("请输入任职年限");
					$("#tjbt").attr("data-target","");
					bdyz = true;
					return ;
				}
				if(parseInt(inputvalue)>60){
					alert("任职年限不能超过六十年");
					$("#tjbt").attr("data-target","");
					bdyz = true;
					return ;
				}
				if(parseInt(inputvalue)!=inputvalue){
					alert("请输入数字");
					$("#tjbt").attr("data-target","");
					bdyz = true;
					return ;
				}
			}
		})
		
		if(bdyz==true){
			return;
		}
		
		
		if($("input[name=checkboxaa]:checked").length==0){
			alert("请选择考试");
			$("#tjbt").attr("data-target","");
			return;
		}
		var exist = false;
		$("div[name=existedbm]").each(function(){
			$(this.parentNode.parentNode.parentNode).find("input[name=checkboxaa]").each(function(){
				if($(this).prop("checked")){
					alert("请先删除或撤销已有的报名");
					$("#tjbt").attr("data-target","");
					//获取到table
					exist=true;
				}
			});
		})
		if(exist){
			return;
		}
		var configflag = false;
		if(countflag==0||countflag==2){
			var neAry=xkArg;
			if(yk.ID){
				neAry=xkArg.concat(yk);	//yk为考试参数   放入数组中
			}
			//数组去重
			for(var i=0; i < neAry.length; i++) {
			    for(var j=i+1;j< neAry.length; j++) {
			        if(neAry[i].ID == neAry[j].ID) {
			        	neAry.splice(neAry[j],1);
			        }
			    }
			}
			var checkArray = $("input[name=checkboxaa]:checked");
			var checkeddata = [];
			for(var m=0;m<checkArray.length;m++){//匹配选中的考试
				for(var n=0;n<neAry.length;n++){
				var length= $($(checkArray[m].parentNode.parentNode).find("td").eq(6)).find("div").length;
				if(length<2){
					if(neAry[n].ID==$($(checkArray[m].parentNode.parentNode).find("td").eq(6)).find("div").eq(0).attr("id")){
						//只提交选中的考试
						checkeddata.push(neAry[n]);
					}
				}
				}
			}
			
			if(checkeddata.length==0){
				return;
			}
			var parambm = {};	//已报名的考试
			parambm["user_code"]=user_code;
			parambm["xmid"]=xm_id;
			var results = FireFly.doAct("TS_BMLB_BM","getBmData",parambm);
			for(var i=0;i<checkeddata.length;i++){
				var a=checkeddata[i].ID;//当前报考考试id
         		var bm_xl = checkeddata[i].BM_XL;//序列互斥
         		var yzjg=a+"yzjg";
				var shArray=true;
				var shs = true;
				var FLAG = false;//相同考试标志
				var xlflag = false; //相同序列互斥标志
				var xlcode = "";	//记录当前考试的序列编码
				var resdata = results.list;//已报名的数据
				for(var z=0;z<resdata.length;z++){
					if(resdata[z].KSLBK_ID==a){
						//判断此考试是否已报名  如果已报名审核通过 必须删除 才能提交
						xlcode=resdata[z].BM_XL_CODE;
						FLAG = true;
					}
				/*	if(resdata[z].BM_XL_CODE==bm_xl){
						//本序列 只能报名一个
						xlflag = true;
					}*/
				}
				if(FLAG){
					configflag=true;
					var divtext1 = $("#"+a).html();//获取div对应的数组
	       			
	       			if(divtext1==null||divtext1.length==0){//append内容之前判断是否有内容
					if(xlcode==STATION_NO_CODE){
						$("#"+a).append('<div class="btn" name="existedbm" type="button" style="color:red;backgroundcolor:lightseagreen">已报名此类考试,请取消再提交或请选择其它考试</div>');
						$("#"+yzjg).append("验证不通过");
					}else{
						$("#"+a).append('已报名此考试,请撤销再报名');
						$("#"+a).append('<div class="btn" name="existedbm" onclick="deleterow(this)" type="button" style="color:red;backgroundcolor:lightseagreen">请删除</div>');
						$("#"+yzjg).append("验证不通过");
					}
					/*$("#"+yzjg).parent().parent().find("input[name='checkboxaa']:first").prop("checked",false);*/
	       			}
					continue;
					
				}
				/*if(xlflag){
					configflag=true;
					var divtext1 = $("#"+a).html();//获取div对应的数组
	       			
	       			if(divtext1==null||divtext1.length==0){//append内容之前判断是否有内容
					if(bm_xl==STATION_NO_CODE){
						$("#"+a).append('<div class="btn" name="existedbm" type="button" style="color:red;backgroundcolor:lightseagreen">本序列只能报考一个,请撤销再提交或请选择其它考试</div>');
						$("#"+yzjg).append("验证不通过");
					}else{
						$("#"+a).append('已报名此序列,请撤销再报名');
						$("#"+a).append('<div class="btn" name="existedbm" onclick="deleterow(this)" type="button" style="color:red;backgroundcolor:lightseagreen">请删除</div>');
						$("#"+yzjg).append("验证不通过");
					}
					$("#"+yzjg).parent().parent().find("input[name='checkboxaa']:first").prop("checked",false);
					continue;
	       			}
				}*/
		}
	
		}
		if(configflag==true){
			$("#tjbt").attr("data-target","");
			alert("请先删除或撤销已有的报名");
			return false;
		}else{
			var motaitable = document.getElementById("motaitable");
			var rowlength = motaitable.rows.length-1;
			//选中了 重复的报名需要先删除再提交
			var highbmnum = 0;
			var middlenum = 0;
			var canhightnum = $("#canheighnum").text();
			var canmiddlenum = $("#cannum").text();
			//选中的考试  中级 高级不能超过上限判断
			var param={};
			param["lbcode"]=STATION_TYPE_CODE;
			param["xlcode"]=STATION_NO_CODE;
			param["user_code"]=user_code;
			var data = FireFly.doAct("TS_BMLB_BM","getBmNum",param,true,false);
			var yihig=data.highnum;
			var yimidd = data.allnum;
			if(canhightnum==(maxnum-yihig)){
				
			}else{
				canhightnum=maxnum-yihig;
				$("#gaoji").html(yihig);
				$("#canheighnum").html(canhightnum);
			}
			if(canmiddlenum==(maxnum-yimidd)){
				
			}else{
				canmiddlenum=maxnum-yimidd;
				$("#allnum").html(yimidd);//中级
				$("#cannum").html(canmiddlenum);
			}
			$("input[name=checkboxaa]:checked").each(function(){
				var tds = $(this.parentNode.parentNode).find("td");
				var JIBIE = "";
				if(tds.length==11){
					//本序列
					JIBIE = $("#lxid").find("option:selected").text();
					if(JIBIE=="中级"){
						middlenum++;
					}else if(JIBIE=="高级"){
						highbmnum++;
					}
					
				}else{
					JIBIE = tds[4].innerText;
					if(JIBIE=="中级"){
						middlenum++;
					}else if(JIBIE=="高级"){
						highbmnum++;
					}
				}
			})
			
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
			
			//获取 当前页面中checkbox选中的数据
			//判断是否已经 进行了资格验证
			var arrChk=$("input[name='checkboxaa']:checked"); 
			tbody=document.getElementById("xinxi");
			for(var i=0;i<arrChk.length;i++){
				//得到tr
				var tr=arrChk[i].parentNode.parentNode;
				var tds=tr.getElementsByTagName("td");
				if(countflag==0||countflag==2){
				}else{
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
		var DUTY_LEVEL_CODE="";
		var belongnum = "";
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
			 DUTY_LEVEL_CODE = result.DUTY_LV_CODE;
			 $("#gwlb").html(STATION_TYPE);
			 $("#gwxl").html(STATION_NO);
			 $("#zwcj").html(ADMIN_DUTY);
		}
		var wherexl = "AND KSLB_CODE="+"'"+STATION_TYPE_CODE+"'"+" AND KSLB_XL_CODE="+"'"+STATION_NO_CODE+"'"+" AND XM_ID="+"'"+xm_id+"'";
		var param={};
		param["where"]=wherexl;
		param["STATION_TYPE_CODE"]=STATION_TYPE_CODE;
		param["STATION_NO_CODE"]=STATION_NO_CODE;
		param["xm_id"]=xm_id;
		 result1 = FireFly.doAct("TS_BMLB_BM","getMatchData",param);
				if(result1.list==""){
					return;
				}
			var pageEntity = result1.list;
			
			 var kslb_id = pageEntity[0].KSLBK_ID;
			  lbname = pageEntity[0].KSLB_NAME;
			  xlname= pageEntity[0].KSLB_XL;
			  lbcode= pageEntity[0].KSLB_CODE;
			  xlcode= pageEntity[0].KSLB_XL_CODE;
			 //拼接 tr
			 var tr = document.createElement('tr');
				 tr.innerHTML='<td style="text-align:center"><input style="margin-right:12px;" type="checkbox" name="checkboxaa"></td>'+
				 '<td>'+lbname+'</td>'+
				 '<td>'+xlname+'</td>'+
				 '<td width="27%"><select id="mkid" onchange="typeId(this)"></select></td>'+
				 '<td width="10%"><select id="lxid" onchange="changeyk(this)"><option></option></select></td>'+
				 '<td class="rhGrid-td-hide"><input type="text" id="zglbid" name="zgksname" value='+kslb_id+'></td>'+
				 '<td width="20%" id="yz_info"><div id='+kslb_id+'></div></td>'+
				 '<td width="15%" id="yzjg_info"><div id="'+kslb_id+'yzjg"></div></td>'+
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
				param["typecode"]=STATION_TYPE_CODE;
				param["xlcode"]=STATION_NO_CODE;
				param["MK"]=mkvalue;
				param["lbname"]=lbname;
				param["xlname"]=xlname;
				param["xm_id"]=xm_id;
				param["cengji"]=belongnum;
				var ww= FireFly.doAct("TS_BMLB_BM", "getMkvalue", param,true,false);
				hh= ww.list;
				var ids = ww.ids.split(",");
				var tyArray = hh.split(",");
				var kstimes = ww.KS_TIME.split(",");
				var select = document.getElementById("lxid");
				jQuery("#lxid").empty();          //把select对象的所有option清除掉
				
				for(var i=0;i<tyArray.length;i++){
					select.options[i]=new Option((tyArray[i]=="1")?"初级":(tyArray[i]=="2")?"中级":(tyArray[i]=="3")?"高级":"无",tyArray[i]);
					$(select.options[i]).attr("name",kstimes[i]);
					$(select.options[i]).attr("icode",ids[i]);
				}
				var tab = document.getElementById("tableid");
			    //表格行数
			    var rows = tab.rows.length;
			    if(rows>1){
					yk["BM_LB"]=lbcode;
					yk["BM_XL"]=xlcode;
					yk["BM_MK"]=mkvalue;
					var sel = document.getElementById("lxid");
					var selected_val = sel.options[sel.selectedIndex].value;
					yk['BM_KS_TIME']=$(sel.options[sel.selectedIndex]).attr("name");
					yk["ID"]=$(sel.options[sel.selectedIndex]).attr("icode");
					$("#yz_info").find("div").eq(0).attr("id",yk["ID"]);
					$("#yzjg_info").find("div").eq(0).attr("id",yk["ID"]+"yzjg");
					yk["BM_TYPE"]=selected_val;
			       }
				}
			    var divstr = "<div></div><div></div><div></div>"
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
		var keys = key;
		if(i==0){
			if(key=="无模块"){
				keys="";
			}
			select.options[i]=new Option(keys,obj[key],true,true);
		}else{
			if(key=="无模块"){
				keys="";
			}
			select.options[i]=new Option(keys,obj[key]);
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
		for(var i=0;i<xkArg.length;i++){
			if(xkArg[i].ID==$(obj).parent().attr('id')){
				xkArg.splice(xkArg[i],1);
				
			}
		}
		$(obj).parent().attr('id');
		$(obj.parentNode.parentNode.parentNode).remove();
	}
function yanzheng(){
	var yanzhengflag = false;
	$('input[name=checkboxaa]').each(function(){
		
		var divlength = $($(this.parentNode.parentNode).find("td").eq(6)).find("div").length;
		
		if(divlength<2){
			yanzhengflag=true;
		}
	})
	if(yanzhengflag){
			checky(); 
		/*var opts = {
				  lines: 12, // The number of lines to draw
				  length: 10, // The length of each line
				  width: 16, // The line thickness
				  radius: 6, // The radius of the inner circle
				  corners: 1, // Corner roundness (0..1)
				  rotate: 0, // The rotation offset
				  color: 'lightseagreen', // #rgb or #rrggbb
				  speed: 1, // Rounds per second
				  trail: 60, // Afterglow percentage
				  shadow: false, // Whether to render a shadow
				  hwaccel: false, // Whether to use hardware acceleration
				  className: 'spinner', // The CSS class to assign to the spinner
				  zIndex: 2e9, // The z-index (defaults to 2000000000)
				  top: 'auto', // Top position relative to parent in px
				  left: 'auto' // Left position relative to parent in px
				};
				var target = document.getElementById('loadiv');
				var spinner = new Spinner(opts).spin(target);*/
		$("#zgyzbt").attr("data-target","#loading");
	}else{
		$("#zgyzbt").attr("data-target","");
	}
}

function yztj(){
	if(""==($("#yzinput").val())){
		alert("请输入任职年限");
		return false;
	}
	if(parseInt($("#yzinput").val())>60){
		alert("任职年限不能超过六十年");
		return false;
	}
	if(parseInt($("#yzinput").val())!=$("#yzinput").val()){
		alert("请输入数字");
		return false;
	}
	
	$("input[name='yzspan']").each(function(){
		$(this).val($("#yzinput").val());
	});
	$("#yzxx").modal("hide");
}
/*
//次机构下拉框
function initmsCodes(){
	var param = {};
	param["user_code"]=user_code;
	var result = FireFly.doAct("TS_BMLB_BM","getMSCodes",param);
	var slavecodes = result.slaver;
	var mastercode = result.master;
	var slavenames = result.slavenames;
	var mastername = result.mastername;
	if(slavecodes!=""){
		document.getElementById("deptspan").style.display="none";
		document.getElementById("selectdeptspan").style.display="block";
		var slavearr = slavecodes.split(",");
		var slavename = slavenames.split(",");
		for(var i=0;i<slavearr.length;i++){
			if(slavearr[i]!=""){
				document.getElementById("slaveselect").options.add(new Option(slavename[i], slavearr[i]));

			}
		}
		$("#selectks").hide();
		
		
		}
}

$("#radio1").click(function(){
	
	document.getElementById("tsspan").style.display="none";
	document.getElementById("slaveselect").style.display="none";
	//主机构身份登陆
	$("#odptnspan").html("");
	$("#odptnspan").html(System.getVar("@ODEPT_NAME@"));
	var param={};
	 param["user_code"]=user_code;
	 param["odept_code"]=System.getVar("@DEPT_CODE@");
	 var result = FireFly.doAct("TS_XMGL","getUserXm1",param);
	 var data = result.list;
	if(data==null){
		$("#selectks").hide();
		//不能提交报名 隐藏table
		$("#goods").hide();
		$("input[name='checkboxaa']").each(function(index,item){
			$(this).prop("checked",false);
		})
			alert("您选择的机构不能报名此项目");
		return;
	}
	var pageEntity = JSON.parse(data);
	var falg = false;
	for(var i=0;i<pageEntity.length;i++){
		var name = pageEntity[i].XM_NAME;
		 //项目中已存在array的  title  数据  将展示在  已报名信息中
		var id = pageEntity[i].XM_ID;
		if(id==xm_id){
			flag=true;
		}
	}
	if(flag){
		dept_code=System.getVar("@DEPT_CODE@");
		$("#selectks").show();
		$("#goods").show();
	}else{
		$("#selectks").hide();
		//不能提交报名 隐藏table
		$("#goods").hide();
		$("input[name='checkboxaa']").each(function(index,item){
			$(this).prop("checked",false);
		})
			alert("您选择的机构不能报名此项目");
	}
	
});

$("#radio2").click(function(){
	document.getElementById("tsspan").style.display="none";
	//次机构身份登陆
	//判断所选机构是否有考 这个项目的权限 没有的话隐藏  考试 不能报名
	var slavecode = $("#slaveselect").val();
	var slavename = $("#slaveselect").text();
	$("#odptnspan").html("");
	$("#odptnspan").html(slavename);
	if(slavecode==null){
		alert("没有次机构");
		$("#selectks").hide();
		$("#goods").hide();
		$("input[name='checkboxaa']").each(function(index,item){
			$(this).prop("checked",false);
		})
		return;
	}
	document.getElementById("slaveselect").style.display="block";
	 //报名项目列表调用(初始化后展示)
		 var param={};
		 param["user_code"]=user_code;
		 param["odept_code"]=slavecode;
		 var result = FireFly.doAct("TS_XMGL","getUserXm1",param);
		 var data = result.list;
		if(data==null){
			return;
		}
		var pageEntity = JSON.parse(data);
		var falg = false;
		for(var i=0;i<pageEntity.length;i++){
			var name = pageEntity[i].XM_NAME;
			 //项目中已存在array的  title  数据  将展示在  已报名信息中
			var id = pageEntity[i].XM_ID;
			if(id==xm_id){
				flag=true;
			}
		}
		if(flag){
			$("#selectks").show();
			$("#goods").show();
			dept_code=slavecode;
		}else{
			$("#selectks").hide();
			$("#goods").hide();
			$("input[name='checkboxaa']").each(function(index,item){
				$(this).prop("checked",false);
			})
				alert("您选择的机构不能报名此项目");
		}
});
$("#slaveselect").change(function(){
	var slavecode = $("#slaveselect").val();
	var slavename = $("#slaveselect").text();
	$("#odptnspan").html("");
	$("#odptnspan").html(slavename);
	 //报名项目列表调用(初始化后展示)
		 var param={};
		 param["user_code"]=user_code;
		 param["odept_code"]=slavecode;
		 var result = FireFly.doAct("TS_XMGL","getUserXm1",param);
		 var data = result.list;
		if(data==null){
			$("#selectks").hide();
			//不能提交报名 隐藏table
			$("#goods").hide();
			$("input[name='checkboxaa']").each(function(index,item){
				$(this).prop("checked",false);
			})
				alert("您选择的机构不能报名此项目");
			return;
		}
		var pageEntity = JSON.parse(data);
		var falg = false;
		for(var i=0;i<pageEntity.length;i++){
			var name = pageEntity[i].XM_NAME;
			 //项目中已存在array的  title  数据  将展示在  已报名信息中
			var id = pageEntity[i].XM_ID;
			if(id==xm_id){
				flag=true;
			}
		}
		if(flag){
			dept_code=slavecode;
			$("#selectks").show();
			$("#goods").show();
		}else{
			$("#selectks").hide();
			//不能提交报名 隐藏table
			$("#goods").hide();
			$("input[name='checkboxaa']").each(function(index,item){
				$(this).prop("checked",false);
			})
				alert("您选择的机构不能报名此项目");
		}
	
})
*/
//页面关闭时提示 是否要离开页面
function showinfo(obj){
	$("#littlediv").html("");
	var spanid = $(obj).attr("name");
	var jsonstr = JSON.parse($("#"+spanid).html());
	for(var i=0;i<jsonstr.length;i++){
		if(jsonstr[i].name!=undefined&&jsonstr[i].name!=""){
			if(jsonstr[i].validate=="false"){
				$("#littlediv").append("<div style='color:red;padding-left:20%'><img src='/ts/image/u4721.png'>"+jsonstr[i].name+"</div>");
			}else{
				$("#littlediv").append("<div style='color:lightseagreen;padding-left:20%'><img src='/ts/image/u4719.png'>"+jsonstr[i].name+"</div>");
			}
		}
	}
	if(jsonstr.length==0){
		alert("信息为空")
	}else{
		$("#littleyzxx").modal('show');
	}
}