var user_code = $("#user_code").val();
//隔一行 进行 背景颜色 渲染
function rowscolor(table){
	 var rows = table.getElementsByTagName("tr");  
	    for(i = 1; i < rows.length; i++){  
	        if(i % 2 == 0){  
	   
	            rows[i].style.backgroundColor = "Azure";  
	       }  
	    } 
}

//下一页 或上一页 按钮 重新  加载数据   传过来 yema1 是 要显示的页码数
function  backwarddata(user_code,yema1){
	 //每页条数
	var select = document.getElementById("yema")
	 var index = document.getElementById("yema").selectedIndex;
	var myts = select.options[index].value;
	//当前选中的页码  移除active
	$("li.active").removeClass();
	var s ="yema"+yema1;
	 $("#"+s).addClass("active");	 
		//要显示的页码
		var result =  getAllSelectData(user_code,yema1);
		//data为json格式字符串
		var first = (yema1-1)*myts+1;
 	var data = result.list;
 	//将json字符串 转换为 json对象
 	var pageEntity=JSON.parse(data);
 	//删除thead之外的 tr
 	$("#ybmtable tbody").html("");
 	var last = pageEntity.length+first-1;
 	 for(var i=0;i<pageEntity.length;i++){
  		var BM_TYPE = pageEntity[i].BM_TYPE;
			var BM_TITLE = pageEntity[i].BM_TITLE;
			var BM_XL = pageEntity[i].BM_XL;
			var BM_MK = pageEntity[i].BM_MK;
			var BM_LB = pageEntity[i].BM_LB;
		    var BM_STATE = pageEntity[i].BM_STATE;
			var BM_STARTDATE = pageEntity[i].BM_STARTDATE;
			var BM_ENDDATE = pageEntity[i].BM_ENDDATE;
		    var BM_ID =  pageEntity[i].BM_ID;
		    var xuhao = first+i;
		  //资格非资格
		    var type = "";
		    var leixng="";
		    if(BM_LB==""){
		    	type="非资格";
		    	leixng=BM_TITLE;
		    }else{
		    	type="资格";
		    	var leixng = BM_TITLE+": "+BM_LB+"-"+BM_XL+"-"+BM_MK+"-"+BM_TYPE; 
		    }
  		//为table重新appendtr
  		if(pageEntity[i].BM_STATE==1){
  			$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+first+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">审核中</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">提交审核</td><td icode="BM_OPTIONS" style="text-align: center"><a onclick="chakan('+i+')" href="#" style="color:lightseagreen" id="chakan">查看</a>&nbsp&nbsp<a href="#" onclick="chexiao('+i+')" style="color:red" id="chexiao'+i+'">撤销</a>&nbsp&nbsp<a onclick="formsubmit('+i+')" href="#" style="color:lightseagreen" id="shenkeliucheng">审核明细</a>&nbsp;&nbsp;<a href="#" data-toggle="modal" onclick="tjyiyi('+i+')" style="color:red" id="yiyi'+i+'">异议</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');
	     		}else{
	     		$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+first+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">审核中</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">提交审核</td><td icode="BM_OPTIONS" style="text-align: center"><a onclick="chakan('+i+')" href="#" style="color:lightseagreen" id="chakan">查看</a>&nbsp&nbsp<a style="color:red" id="chexiao">已撤销</a></td></tr>');	
	     		}
  	}  
 	
}


//将四个下拉框的 条件都算上 进行 分页查询 得出 第几页的数据 进行返回
function getAllSelectData(user_code,dijiye){
	 var jb = document.getElementById("jb");
    var indexjb = jb.selectedIndex;
    var jbvalue = jb.options[indexjb].value;
    //第一个下拉框值
    var type1 =  document.getElementById("gangwei");
 	var index1 = type1.selectedIndex;
 	var  value1 = type1.options[index1].value;
 	//第二个下拉框值
 	var type2 =  document.getElementById("xulie");
 	var index2 = type2.selectedIndex;
 	var  value2 = type2.options[index2].value;
 	//第三个下拉框值
 	var type3 =  document.getElementById("mokuai");
 	var index3 = type3.selectedIndex;
 	var  value3 = type3.options[index3].value;
 	
 	var param={};
 	var id = document.getElementById("xmid").value;
 	param["id"] = id;
 	param["user_code"]=user_code;
 		//每页条数
		var select = document.getElementById("yema");
		 var index = document.getElementById("yema").selectedIndex;
		var myts = select.options[index].value;
		//重新计算 页码
		param["nowpage"]=dijiye;
		param["shownum"]=myts;
 	if(value1=="" && jbvalue=="全部"){
 		param["where"]="";
 	}else if(value1=="" && jbvalue!=""){
 		param["where"] = "AND BM_TYPE="+"'"+jbvalue+"'";
 	}
 	else if(value1!="" && value2=="" && jbvalue!="全部"){
 		
 	param["where"]="AND BM_LB="+"'"+value1+"' "+"AND BM_TYPE="+"'"+jbvalue+"'";
 	
 	}else if(value1!="" && value2=="" && jbvalue=="全部"){
 		param["where"] = "AND BM_LB="+"'"+value1+"'";
 	}else if(value1!="" && value2!="" && value3!="" && jbvalue=="全部"){
 		param["where"]="AND BM_LB="+"'"+value1+"' "+"AND BM_XL="+"'"+value2+"' "+"AND BM_MK="+"'"+value3+"'";
 	}else if(value1!="" && value2!="" && value3!="" && jbvalue!="全部"){
 		param["where"]="AND BM_LB="+"'"+value1+"' "+"AND BM_XL="+"'"+value2+"' "+"AND BM_MK="+"'"+value3+"' "+"AND BM_TYPE="+"'"+jbvalue+"'";
 	}else if(value1!="" && value2!="" && value3=="" && jbvalue!="全部"){
 		param["where"]="AND BM_LB="+"'"+value1+"' "+"AND BM_XL="+"'"+value2+"' "+"AND BM_TYPE="+"'"+jbvalue+"'";
 	}else if(value1!="" && value2!="" && value3=="" && jbvalue=="全部"){
 		param["where"]="AND BM_LB="+"'"+value1+"' "+"AND BM_XL="+"'"+value2+"'";
 	}
 	var result = FireFly.doAct("TS_BMLB_BM","getSelectedData",param);
 	return result
}


//所有下拉框onchang进行  查询数据  资格非资格 进行筛选
function selectdata(user_code){
	var jb = document.getElementById("jb");
        var indexjb = jb.selectedIndex;
        var jbvalue = jb.options[indexjb].value;
        //第一个下拉框值
        var type1 =  document.getElementById("gangwei");
     	var index1 = type1.selectedIndex;
     	var  value1 = type1.options[index1].value;
     	//第二个下拉框值
     	var type2 =  document.getElementById("xulie");
     	var index2 = type2.selectedIndex;
     	var  value2 = type2.options[index2].value;
     	//第三个下拉框值
     	var type3 =  document.getElementById("mokuai");
     	var index3 = type3.selectedIndex;
     	var  value3 = type3.options[index3].value;
     	var param={};
     	param["user_code"]=user_code;
     	//每页条数
		var select = document.getElementById("yema");
		 var index = document.getElementById("yema").selectedIndex;
		var myts = select.options[index].value;
		//重新计算 页码
		param["nowpage"]=1;
		param["shownum"]=myts;
     	if(value1=="" && jbvalue=="全部"){
     		param["where"]="";
     	}else if(value1=="" && jbvalue!=""){
     		param["where"] = "AND BM_TYPE="+"'"+jbvalue+"'";
     	}
     	else if(value1!="" && value2=="" && jbvalue!="全部"){
     		
     	param["where"]="AND BM_LB="+"'"+value1+"' "+"AND BM_TYPE="+"'"+jbvalue+"'";
     	
     	}else if(value1!="" && value2=="" && jbvalue=="全部"){
     		param["where"] = "AND BM_LB="+"'"+value1+"'";
     	}else if(value1!="" && value2!="" && value3!="" && jbvalue=="全部"){
     		param["where"]="AND BM_LB="+"'"+value1+"' "+"AND BM_XL="+"'"+value2+"' "+"AND BM_MK="+"'"+value3+"'";
     	}else if(value1!="" && value2!="" && value3!="" && jbvalue!="全部"){
     		param["where"]="AND BM_LB="+"'"+value1+"' "+"AND BM_XL="+"'"+value2+"' "+"AND BM_MK="+"'"+value3+"' "+"AND BM_TYPE="+"'"+jbvalue+"'";
     	}else if(value1!="" && value2!="" && value3=="" && jbvalue!="全部"){
     		param["where"]="AND BM_LB="+"'"+value1+"' "+"AND BM_XL="+"'"+value2+"' "+"AND BM_TYPE="+"'"+jbvalue+"'";
     	}else if(value1!="" && value2!="" && value3=="" && jbvalue=="全部"){
     		param["where"]="AND BM_LB="+"'"+value1+"' "+"AND BM_XL="+"'"+value2+"'";
     	}
     	var result = FireFly.doAct("TS_BMLB_BM","getSelectedData",param);
     	var result2 = FireFly.doAct("TS_BMLB_BM","getSelectData",param);
		var first =1;
		//data为json格式字符串
		var data2 = result2.list;
     	var data = result.list;
     	//将json字符串 转换为 json对象
     	if(data.length==2&&data2.length==2){
     		
     		$("#ybmtable tbody").html("");
     		$("#fenyeul").html("");
     		$("#fenyeul").append('<li><a href="#">&laquo;</a></li><li><a  href="#">&raquo;</a></li>');
     	}else{
     		
     	var pageEntity=JSON.parse(data);
     	var pageEntity2 = JSON.parse(data2);
     	$("#ybmtable tbody").html("");
     	  for(var i=0;i<pageEntity.length;i++){
     		 var BM_TYPES = pageEntity[i].BM_TYPE;
     		 var BM_TYPE="";
     		 if(BM_TYPES=="1"){
     			BM_TYPE="初级";
     		 }else if(BM_TYPE=="2"){
     			 BM_TYPE="中级";
     		 }else{
     			 BM_TYPE="高级";
     		 }
			var BM_TITLE = pageEntity[i].BM_TITLE;
			var BM_XL = pageEntity[i].BM_XL;
			var BM_MK = pageEntity[i].BM_MK;
			var BM_LB = pageEntity[i].BM_LB;
		    var BM_STATE = pageEntity[i].BM_STATE;
			var BM_STARTDATE = pageEntity[i].BM_STARTDATE;
			var BM_ENDDATE = pageEntity[i].BM_ENDDATE;
		    var BM_ID =  pageEntity[i].BM_ID;
     		first=i+1;
     		 //资格非资格
		    var type = "";
		    var leixng = "";
		    if(BM_LB==""){
		    	type="非资格";
		    	leixng=BM_TITLE;
		    }else{
		    	type="资格";
		    	var leixng = BM_TITLE+": "+BM_LB+"-"+BM_XL+"-"+BM_MK+"-"+BM_TYPE; 
		    }
		    var yiyistate = pageEntity[i].BM_YIYI_STATE;
		    var sh_state = pageEntity[i].BM_SH_STATE;
		    var sh_state_str = "审核通过";
		    if(sh_state==0){
		    	//审核中
		    	  sh_state_str = "审核中"
		    	}else if(sh_state==2||sh_state==3){
		    		sh_state_str="审核未通过"
		    	}
     		//为table重新appendtr
		    //已提交异议  
		    if(yiyistate==1){
		    	$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+first+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">'+sh_state_str+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">提交审核</td><td icode="BM_OPTIONS" style="text-align: center"><a href="#" onclick="chakan('+i+')" style="color:lightseagreen" id="chakan">查看</a>&nbsp&nbsp<a onclick="formsubmit('+i+')" href="#" style="color:lightseagreen" id="shenkeliucheng">审核明细</a>&nbsp;&nbsp;<a href="#" data-toggle="modal" onclick="tjyiyi('+i+')" style="color:red" id="yiyi'+i+'">异议详情</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');
		    	
		    }else{
		    	//没有提交异议  且没有撤销
		    	if(pageEntity[i].BM_STATE==1){
		    		//判断审核状态
		    		//审核未通过
		    		if(sh_state==2){
		    			$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+first+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">'+sh_state_str+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">提交审核</td><td icode="BM_OPTIONS" style="text-align: center"><a onclick="chakan('+i+')" href="#" style="color:lightseagreen" id="chakan">查看</a>&nbsp&nbsp<a href="#" onclick="chexiao('+i+')" style="color:red" id="chexiao'+i+'">撤销</a>&nbsp&nbsp<a onclick="formsubmit('+i+')" href="#" style="color:lightseagreen" id="shenkeliucheng">审核明细</a>&nbsp;&nbsp;<a href="#" data-toggle="modal" onclick="tjyiyi('+i+')" style="color:red" id="yiyi'+i+'">异议</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');
		    		}else if(sh_state==1){
		    			//审核通过 没有异议  没有撤销
		    			$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+first+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">'+sh_state_str+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">提交审核</td><td icode="BM_OPTIONS" style="text-align: center"><a onclick="chakan('+i+')" href="#" style="color:lightseagreen" id="chakan">查看</a>&nbsp&nbsp<a onclick="formsubmit('+i+')" href="#" style="color:lightseagreen" id="shenkeliucheng">审核明细</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');
		    		}else if(sh_state==0){
		    			//待审核
		    			$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+first+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">'+sh_state_str+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">提交审核</td><td icode="BM_OPTIONS" style="text-align: center"><a onclick="chakan('+i+')" href="#" style="color:lightseagreen" id="chakan">查看</a>&nbsp&nbsp<a href="#" onclick="chexiao('+i+')" style="color:red" id="chexiao'+i+'">撤销</a>&nbsp&nbsp<a onclick="formsubmit('+i+')" href="#" style="color:lightseagreen" id="shenkeliucheng">审核明细</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');
		    		}else{
		    			//审核未通过  没有手动审核
		    			$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+first+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">'+sh_state_str+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">提交审核</td><td icode="BM_OPTIONS" style="text-align: center"><a onclick="chakan('+i+')" href="#" style="color:lightseagreen" id="chakan">查看</a>&nbsp&nbsp<a onclick="formsubmit('+i+')" href="#" style="color:lightseagreen" id="shenkeliucheng">审核明细</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');
		    		}
		    	}else{
		    		$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+first+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">审核中</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">提交审核</td><td icode="BM_OPTIONS" style="text-align: center"><a onclick="chakan('+i+')" href="#" style="color:lightseagreen" id="chakan">查看</a>&nbsp&nbsp<a style="color:red" id="chexiao">已撤销</a></td></tr>');	
		    	}
		    }
     	  
     	  }
     	 //总条数/每页
		//页数
		 var yeshu = Math.floor(pageEntity2.length/myts);
     	
		 var yushu = pageEntity2.length%myts;
		 $("#fenyeul").html("");
			 if(yushu!=0){
				 //余数为0
				 yeshu+=1;
			 }
			  for(var z=0;z<yeshu;z++){
				  
			  var j=z+1;
				 if(z==0){
			$("#fenyeul").append('<li onclick="forward()"><a href="#">&laquo;</a></li><li id="yema1" class="active" onclick="chaxun('+j+')"><a href="#">1</a></li>');
				 }else {
			 $("#fenyeul").append('<li id="yema'+j+'" onclick=chaxun('+j+')><a  href="#">'+j+'</a></li>');
					 
				 }
			 
		 }
     		  //最后一页
			 var last =yeshu+1;
     	
			 $("#fenyeul").append(' <li id="yema'+last+'" onclick="backward('+last+')"><a  href="#">&raquo;</a></li>');
     	}	 
     	
     	var table = document.getElementById("ybmtable");  
     	 rowscolor(table);
     	
}
//-----------------------撤销按钮
function chexiao(i){
	var aid = "chexiao"+i;
	var aa = document.getElementById(aid).innerHTML;
	if(aa=="撤销"){
	var res = confirm("您确定要撤销吗？");
	
	if (res == true) {
	var id = document.getElementById("baomingid"+i).innerHTML;
	param={};
	param["id"]=id;
	var servId = "TS_BMLB_BM";
	FireFly.doAct(servId,"deletesingle",param);
	//刷新
	document.getElementById(aid).innerHTML="已撤销";
	}else{
		return false;
	}
	}else{
		return false;
	}
	selectdata(user_code);
}

//级别下拉框onchange事件
function jibieonchange(){
	selectdata(user_code);
}

//每页多少条 添加onchange事件
function fenyeselect(){	
	//跟 级别 按钮 的onchange时间一样都要 筛选所有条件下的数据
	selectdata(user_code);
}

//上一页 按钮
function forward(){
	//获取页码 数
	var yema = document.querySelectorAll("li[class='active']")[0].innerText;
	if(yema==1){
		return false;
	}else{
		//要显示的页码
		var yema1 = yema-1;
		backwarddata(user_code,yema1);
		//table tr  背景色
		var table = document.getElementById("ybmtable");   
		rowscolor(table);
	}
}


//下一页按钮
function backward(last){
	//获取页码 数
	var yema = document.querySelectorAll("li[class='active']")[0].innerText;
	var lastyema = yema-1+2;
	if(lastyema==last){
		return false;
	}else{
		//要显示的页码数
		 var yema1 = yema-1+2;
		backwarddata(user_code,yema1);
		//table tr  背景色
		var table = document.getElementById("ybmtable");   
		rowscolor(table);
     	  
	}
}
//点击第几页跳转
function chaxun(i){
	var id = "yema"+i;
	//点击第几页
	var ym = document.getElementById(id).innerText;
	//传入 要显示的页码数即可
	backwarddata(user_code,ym);
	//table tr  背景色
	var table = document.getElementById("ybmtable");   
	rowscolor(table);
} 

//报名时根据类型跳转不同页面
function tiaozhuan(i){
		//计划名称
	 var hid = "BM_ID"+i;
	var id = document.getElementById(hid).innerHTML;
	var a = "BM_NAME"+i;
	var jhname = document.getElementById(a).innerHTML;
	 // 项目类型  资格非资格
	var b = "BM_TYPE"+i;
	var  kstype = document.getElementById(b).innerHTML;
	if(kstype=="资格类考试"){
		
		document.getElementById("zgtz").value=id;
		document.getElementById("form1").submit();
	  
	}else {
		
	document.getElementById("fzgtz").value=id;
	document.getElementById("form2").submit(); 
	} 
	
}
//可选报名  已选报名字体图片改变
$('#akeshen').click(function(){
	document.getElementById("keshen").style.color="LightSeaGreen";
	document.getElementById("keshenimage").src="/ts/image/u975.png";
	document.getElementById("yishenimage").src="/ts/image/u984.png";
	document.getElementById("yishen").style.color="black";
	var table = document.getElementById("table");  
   rowscolor(table);
	
});
$('#ayishen').click(function(){
  
	document.getElementById("keshen").style.color="black";
	document.getElementById("yishenimage").src="/ts/image/u7733.png";
	document.getElementById("keshenimage").src="/ts/image/u1131.png";
	document.getElementById("yishen").style.color="LightSeaGreen";
	selectdata(user_code);
	var table = document.getElementById("ybmtable");  
   rowscolor(table);
   
});
//加载完毕  显示第一个 tab active  显示隐藏
$(function () {
	$('#myTab li:eq(1) a').tab('show');
	var table = document.getElementById("table");  
  //对每一行 进行  渲染 颜色
	ksqxm();
   selectcreate();
 }); 

//---------------------下拉框生成
function selectcreate(){
	var param={};
	var sanjistring = FireFly.doAct("TS_BMLB_BM","getJsonString",param);
	var stringsanji = sanjistring.s1;
	var stringsanji2 = sanjistring.s2;
	var stringsanji3 = sanjistring.s3; 
	 var jsonstr = stringsanji;
	 var jsonstr1 = stringsanji2;
	 var jsonstr2 = stringsanji3;
	 var jsonObj = JSON.parse(jsonstr);
	 var jsonObj1 = JSON.parse(jsonstr1);
	 var jsonObj2 = JSON.parse(jsonstr2);
	 var items = jsonObj;
	 var items1 = jsonObj1;
	 var items2 = jsonObj2;
	 
	 var ele = {};
	 ele.type= 'select';
	 ele.name= 'gangwei';
	 ele.title= '类型';
	 ele.withNull= true;
	 var elsPar = {};
	 ele.items = items;
	 var elsPar1 = {};
	 elsPar1.ele = ele;
	 
	 var ele = {};
	 ele.type= 'select';
	 ele.name= 'xulie';
	 ele.title= '';
	 ele.withNull= true;
	 ele.items = items1;
	 var elsPar2 = {};
	 elsPar2.ele = ele;
	 
	 var ele = {};
	 ele.type= 'select';
	 ele.name= 'mokuai';
	 ele.title= '';
	 ele.withNull= true;
	 ele.items = items2;
	 
	 var elePar = {};
	 elsPar.ele = ele;
	
	 var par = [];
	 par.push(elsPar1);
	 par.push(elsPar2);
	 par.push(elsPar);
	 var grndp = [];
	 grndp.push(par);
	 
	 var eles = grndp;
		
	       var bsForm = new BSForm({ eles: eles,autoLayout: true }).Render('formContainer', function (sf) {
	          //编辑页面的绑定 
	           sf.InitFormData({ 
	              gangwei: 'GuangDong', 
	              xulie: 'GuangZhou', 
	              mokuai:'TH' 
	           }); 
	          //必须先赋值再生成插件 
	          global.Fn.CascadeSelect({ targets: ['gangwei', 'xulie', 'mokuai'], primaryKey: 'data-id', relativeKey: 'data-parentId' }); 
	       }); 
	    
}

//动态生成三级联动  拼接 json 字符串

//生成下拉联动框
 //onchange进行筛选
  global.Fn.CascadeSelect = function (opt) {
	
 	    opt = $.extend(true, { relativeKey: 'data-parentId', primaryKey: 'data-id' }, opt);
 	    for (var i = 0; i < opt.targets.length; i++) {
 	    	$("#" + opt.targets[i]).find("option").first().attr('selected',true);
 	    	//$("#"+opt.targets[i]).find("option").filter("option:hidden").first().attr('selected', true)
 	        $("#" + opt.targets[i]).bind("change.cascade", function () {
 	            var $this = $(this);
 	            var nextIndex = opt.targets.indexOf($this.attr('id')) + 1;
 	            var $next = $("#" + opt.targets[nextIndex]);
 	            var curKeyValue = $this.find('option:checked').attr(opt.primaryKey);
 	            var nextVal = $next.val();
 	            var $nextItems = $next.find('option');
				
 	            $next.find('option[' + opt.relativeKey + '="' + curKeyValue + '"]').removeClass('hide');
 	            $next.find('option[' + opt.relativeKey + '!="' + curKeyValue + '"]').addClass('hide');
 	            $("#" + opt.targets[nextIndex+1]).find('option[' + opt.relativeKey + '!="' + curKeyValue + '"]').addClass('hide');
 	            $next.find('option[value=""]').removeClass('hide');
 	            $("#" + opt.targets[nextIndex+1]).find('option[value=""]').removeClass('hide');
				
 	            //如果下一项的option处于显示状态，则自动选中，否则显示请选择  第二级一样
 	            $next.find("option").attr('selected', false)
 	            $next.find("option").first().attr('selected', true)
 	            $("#" + opt.targets[nextIndex+1]).find("option").attr('selected', false)
 	            $("#" + opt.targets[nextIndex+1]).find("option").first().attr('selected', true)
 	           selectdata(user_code);
 	        });
 	    }
 	    var $this = $("#gangwei");
        var nextIndex = opt.targets.indexOf($this.attr('id')) + 1;
        var $next = $("#" + opt.targets[nextIndex]);
        var curKeyValue = $this.find('option:checked').attr(opt.primaryKey);
        var nextVal = $next.val();
        var $nextItems = $next.find('option');
		
        $next.find('option[' + opt.relativeKey + '="' + curKeyValue + '"]').removeClass('hide');
        $next.find('option[' + opt.relativeKey + '!="' + curKeyValue + '"]').addClass('hide');
        $("#" + opt.targets[nextIndex+1]).find('option[' + opt.relativeKey + '!="' + curKeyValue + '"]').addClass('hide');
        $next.find('option[value=""]').removeClass('hide');
        $("#" + opt.targets[nextIndex+1]).find('option[value=""]').removeClass('hide');
		
        //如果下一项的option处于显示状态，则自动选中，否则显示请选择  第二级一样
        $next.find("option").attr('selected', false)
        $next.find("option").first().attr('selected', true)
        $("#" + opt.targets[nextIndex+1]).find("option").attr('selected', false)
        $("#" + opt.targets[nextIndex+1]).find("option").first().attr('selected', true)
 	}
 //审核未通过提起上诉
  var formnum =0;
  var idcode = -1;
  function tjyiyi(i){
	  var bmid = document.getElementById("baomingid"+i).innerHTML;
	  $("#yiyi"+i).attr("data-target",'#appeal');
	  var param={};
	  param["bmid"]=bmid;
	  var result = FireFly.doAct("TS_BMLB_BM","filehist",param);
	  var data = result.list;
	  var pageEntity = JSON.parse(data);
	  $("#filehis").html("");
	  $("#formContainer2").html("");
	  if(pageEntity.length==0){
		  
	  }else{
		  var param1 = {};
		  param1["bmid"]=bmid;
		  var result = FireFly.doAct("TS_BMLB_BM","getLiyou",param1);
		  var reason = result.liyou;
		  $("#liyou11").val(reason);
		  $("#liyou11").attr("disabled","disabled");
		  
		  //已有上传文件记录
		  //模态窗口 append上传的东西
		  for(var i=0;i<pageEntity.length;i++){
			  var fileid = pageEntity[i].FILE_ID;
			  $("#filehis").append('<tr style="height:30px"><td style="width:30%"><td><a href="/file/'+fileid+'">'+fileid+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<image title="点击进行下载" src="/ts/image/u344.png"></image></a></td></tr>');
		  }
		  //将数据 保存
		  //不能再提交 给按钮置灰
		  document.getElementById("tjbutt").style.display="none";
		  return;
	  }
		  $("#formContainer2").html("");
	  var eles=[ 
                     [ 
                       {ele:{type:'img',id:'img1',name:'files',title:'',extendAttr:{filed:'deatil_img',handle:'single',url:''}}}   
                     ] 
                 ]; 
                 var bsForm = new BSForm({ eles: eles, autoLayout:true}).Render('formContainer2',function(bf){ 
                	 
                     global.Fn.InitPlugin('img','formContainer2',bmid); 
                     
                 }); 
                 idcode=i;
	  
  }
  //关闭上传模态页面
  function closemotai(){
	//关闭时删除刚刚上传的文件
	 var linum =  $("#formContainer2").find('li').length;
	 //不为0 时可以删除
	 if(linum!=0){
		 var lis = document.getElementsByName("filedown");
		 //循环不便利li里的内容拼接 删除
		 var ids = "";
		 for(i = 0; i < lis.length; i++){  
		       var s = lis[i].innerHTML;
		       if(i==lis.length-1){
		    	   ids+=s;
		       }else{
		    	   ids+=s+",";
		       }
		    } 
		 var bmid = document.getElementById("baomingid"+idcode).innerHTML;
		 //可以删除
		 var param={};
		 param["SERV_ID"]="TS_BMLB_BM";
		 param["DATA_ID"]=bmid;
		 param["_PK_"]=ids;
		 //删除
		 FireFly.doAct("SY_COMM_FILE","delete",param);
		 
	 }
  }
  //删除文件
 function deletefile(obj){
	var id =  obj.parentNode.parentNode.id;
	 //删除数据库
	 var bmid = document.getElementById("baomingid"+idcode).innerHTML;
	 var param={};
	 param["SERV_ID"]="TS_BMLB_BM";
	 param["DATA_ID"]=bmid;
	 param["_PK_"]=id;
	 //删除
	FireFly.doAct("SY_COMM_FILE","delete",param);
	
	 //删除页面
	 document.getElementById(id).remove();
 }
 //提交意义
 function tijiaoyiyi(){
	 $('#appeal').modal('hide');
	 //将数据从审核未通过中删除
	 //加入到待审核中
	 var bmid = document.getElementById("baomingid"+idcode).innerHTML;
	 var liyou = document.getElementById("liyou11").innerHTML;
	 var param ={};
	 param["bmid"]=bmid
	 param["liyou"]=liyou;
	 FireFly.doAct("TS_BMSH_NOPASS","yiyi",param);
	 selectdata(user_code);
}
 
 //报名项目列表调用(初始化后展示)
 function ksqxm(){
	 var param={};
	 param["user_code"]=user_code;
	 var result = FireFly.doAct("TS_XMGL","getUserXm",param);
	 var data = result.list;
	if(data==null){
		return;
	}
	var pageEntity = JSON.parse(data);
	for(var i=0;i<pageEntity.length;i++){
		var name = pageEntity[i].XM_NAME;
		 //项目中已存在array的  title  数据  将展示在  已报名信息中
		var id = pageEntity[i].XM_ID;
		var dept = pageEntity[i].XM_FQDW_NAME;
		var type = pageEntity[i].XM_TYPE;
		var state = "未开始";
		var display = "none";
		
		//获取报名时间判断  报名状态
		var param1={};
		param1["xmid"]=id;
		var result1 = FireFly.doAct("TS_XMGL_BMGL","getBMState",param1);
		var data1 = result1.list;
		var pageEntity1 = JSON.parse(data1);
		var startTime = pageEntity1[0].START_TIME;
		var state = pageEntity1[0].STATE;
		if(state=="待报名"){
	    display = "block";
		}
		//append数据
		var j=i+1;
		$("#table tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+j+'</td><td class="rhGrid-td-hide" id="BM_TYPE'+i+'" >'+type+'</td><td class="rhGrid-td-hide" id="BM_ID'+i+'" >'+id+'</td><td class="rhGrid-td-left " id="BM_NAME'+i+'" style="text-align: left">'+name+'</td><td class="rhGrid-td-left " id="BM_ODEPT__NAME" style="text-align: left">'+dept+'</td><td class="rhGrid-td-left " id="S_ATIME" style="text-align: left" >'+startTime+'</td><td class="rhGrid-td-left " id="BM_STATE__NAME" style="text-align: left">'+state+'</td><td id="BM_OPTIONS"><input type="button" onclick="tiaozhuan('+i+')" style="margin-left:30px;display:'+display+';color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:80px"value="报名"></input></td></tr>');
	}
 }
 //审核明细
 function formsubmit(obj){
	 var bmid = document.getElementById("baomingid"+obj).innerHTML;
			document.getElementById("bmid").value=bmid;
			$("#form3").submit();
 }
 function chakan(obj){
	 var bmid = document.getElementById("baomingid"+obj).innerHTML;
		document.getElementById("bmid").value=bmid;
		$("#form4").submit();
 }
 
 