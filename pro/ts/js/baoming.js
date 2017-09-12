var user_code = System.getVar("@USER_CODE@");
//隔一行 进行 背景颜色 渲染
function rowscolor(table){
	 var rows = table.getElementsByTagName("tr");  
	    for(i = 1; i < rows.length; i++){  
	        if(i % 2 == 0){  
	   
	            rows[i].style.backgroundColor = "Azure";  
	       }  
	    } 
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
	new listPage().gotoPage(1);
}

//级别下拉框onchange事件
function jibieonchange(){
	new listPage().gotoPage(1);
}

//每页多少条 添加onchange事件
function fenyeselect(){	
	//跟 级别 按钮 的onchange时间一样都要 筛选所有条件下的数据
	new listPage().gotoPage(1);
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
	/*selectdata(user_code,1);*/
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
   new listPage().gotoPage(1);
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
 	           new listPage().gotoPage(1);
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
	  $("#liyou11").val("");
	  $("#liyou11").removeAttr("disabled");
	  $("#tjbutt").removeAttr("disabled");
	  var bmid = document.getElementById("baomingid"+i).innerHTML;
	  $("#yiyi"+i).attr("data-target",'#appeal');
	  var param={};
	  param["bmid"]=bmid;
	  var result = FireFly.doAct("TS_BMLB_BM","filehist",param);
	  var data = result.list;
	  var reason = result.liyou;
	  var pageEntity = JSON.parse(data);
	  $("#filehis").html("");
	  $("#formContainer2").html("");
	  if(pageEntity.length==0&&reason==""){
		  
	  }else{
		  $("#liyou11").val(reason);
		  $("#liyou11").attr("disabled","disabled");
		  $("#tjbutt").attr("disabled","disabled");
		  //已有上传文件记录
		  //模态窗口 append上传的东西
		  for(var i=0;i<pageEntity.length;i++){
			  var fileid = pageEntity[i].FILE_ID;
			  $("#filehis").append('<tr style="height:30px"><td style="width:30%"><td><a href="/file/'+fileid+'">'+fileid+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<image title="点击进行下载" src="/ts/image/u344.png"></image></a></td></tr>');
		  }
		  //将数据 保存
		  //不能再提交 给按钮置灰
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
	 var bmid = $("#baomingid"+idcode).html();
	 var liyou = $("#liyou11").val();
	 var param ={};
	 param["bmid"]=bmid
	 param["liyou"]=liyou;
	 FireFly.doAct("TS_BMSH_NOPASS","yiyi",param);
	 new listPage().gotoPage(1);
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
	var table = document.getElementById("table");  
	   rowscolor(table);
 }
 //审核明细
 function formsubmit(obj){
	 var bmid = document.getElementById("baomingid"+obj).innerHTML;
	  doPost('bmshmx.jsp', {bmidmx: bmid});
 }
 function chakan(obj){
	 var bmid = document.getElementById("baomingid"+obj).innerHTML;
	 doPost('zgchakan.jsp', {bmid4: bmid});
 }
 
 function doPost(to, data) {  // to:提交动作（action）,data:参数
     var myForm = document.createElement("form");
     myForm.method = "post";
     myForm.action = to;
     for (var i in data) {
         var myInput = document.createElement("input");
         myInput.setAttribute("name", i);  // 为input对象设置name
         myInput.setAttribute("value", data[i]);  // 为input对象设置value
         myForm.appendChild(myInput);
     }
     document.body.appendChild(myForm);
     myForm.submit();
     document.body.removeChild(myForm);  // 提交后移除创建的form
 }
 

 //分页+查询
 var listPage = function () {
     // 构建页码所需参数
     this.showPageNum = 5; // 最多显示的页码
     this.startNum = 1; // 中间页码的第一个页码
     this.endNum = this.startNum; // 中间页码的最后一个页码
 };
  listPage.prototype.getListData = function (num) {
	//每页条数
	  debugger;
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
	  param["nowpage"]=num;
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
      return FireFly.doAct("TS_BMLB_BM","getSelectedData",param);
  };
  //全局变量  sql查询条件(页面输入的搜索条件)
  var sqlWhere= "";
 // 创建页面显示数据的主体
  listPage.prototype._bldBody = function (num) {
      var listData = this.getListData(num);
      this._lPage = listData._PAGE_;
      this.bldTable(listData);
      this.bldPage();
      var listPage=this;
    //查询条件按钮（设置查询考试名称和年份的条件）
  };

/*  跳转到指定页*/
  listPage.prototype.gotoPage = function (num) {
 	 
      this._bldBody(num);
  };
/*  上一页*/
  listPage.prototype.prePage = function() {
      var prePage = parseInt(this._lPage.NOWPAGE) - 1;
      var nowPage = "" + ((prePage > 0) ? prePage:1);
      this.gotoPage(nowPage);
  };
 /* 下一页*/
  listPage.prototype.nextPage = function() {
      var nextPage = parseInt(this._lPage.NOWPAGE) + 1;
      var pages = parseInt(this._lPage.PAGES);
      var nowPage = "" + ((nextPage > pages) ? pages:nextPage);
      this.gotoPage(nowPage);
//      debugger;
  };
 /* 首页*/
  listPage.prototype.firstPage = function() {
      this.gotoPage(1);
  };
  /*末页*/
  listPage.prototype.lastPage = function() {
      this.gotoPage(this._lPage.PAGES);
  };
  listPage.prototype.bldTable = function (listData) {
	  $("#ybmtable tbody").html("");
	  var data = listData.list;
	  var pageEntity=JSON.parse(data);
	  var first = listData.first;
	  for(var i=0;i<pageEntity.length;i++){
		  var firint = parseInt(first);
  		 var BM_TYPES = pageEntity[i].BM_TYPE;
  		 var BM_TYPE="";
  		 if(BM_TYPES=="1"){
  			BM_TYPE="初级";
  		 }else if(BM_TYPES=="2"){
  			 BM_TYPE="中级";
  		 }else if(BM_TYPES=="3"){
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
		    firint = firint+i;
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
		    	//没有提交异议  且没有撤销
		    	if(pageEntity[i].BM_STATE==1){
		    		if(yiyistate==2){
		    		//判断审核状态
		    		//审核未通过 没有手动审核
		    		if(sh_state==2){
		    			$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+firint+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">'+sh_state_str+'</td><td  style="text-align: center"><a onclick="chakan('+i+')" href="#" style="color:lightseagreen" >查看</a>&nbsp&nbsp<a href="#" onclick="chexiao('+i+')" style="color:red" id="chexiao'+i+'">撤销</a>&nbsp&nbsp<a onclick="formsubmit('+i+')" href="#" style="color:lightseagreen" id="shenkeliucheng">审核明细</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');
		    		}else if(sh_state==1){
		    			//审核通过 没有异议  没有撤销
		    			$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+firint+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="color:red;text-align: center">'+sh_state_str+'</td><td style="text-align: center"><a onclick="chakan('+i+')" href="#" style="color:lightseagreen" >查看</a>&nbsp&nbsp<a href="#" onclick="chexiao('+i+')" style="color:red" id="chexiao'+i+'">撤销</a>&nbsp&nbsp<a onclick="formsubmit('+i+')" href="#" style="color:lightseagreen" id="shenkeliucheng">审核明细</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');
		    		}else if(sh_state==3){
		    			//审核未通过  手动审核
		    			$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+firint+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="color:red;text-align: center">'+sh_state_str+'</td><td style="text-align: center"><a onclick="chakan('+i+')" href="#" style="color:lightseagreen" >查看</a>&nbsp&nbsp<a href="#" onclick="chexiao('+i+')" style="color:red" id="chexiao'+i+'">撤销</a>&nbsp&nbsp<a onclick="formsubmit('+i+')" href="#" style="color:lightseagreen" id="shenkeliucheng">审核明细</a>&nbsp;&nbsp;<a href="#" data-toggle="modal" onclick="tjyiyi('+i+')" style="color:red" id="yiyi'+i+'">异议</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');
		    		}
		    		}else if(yiyistate==1){
		    			//提交了异议且审核状态 
		    			if(sh_state==1){
		    				//通过
		    				sh_state_str="复核异议通过"	
		    					$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+firint+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">'+sh_state_str+'</td><td icode="BM_OPTIONS" style="text-align: center"><a href="#" onclick="chakan('+i+')" style="color:lightseagreen" >查看</a>&nbsp&nbsp<a onclick="formsubmit('+i+')" href="#" style="color:lightseagreen" id="shenkeliucheng">审核明细</a>&nbsp;&nbsp;<a href="#" data-toggle="modal" onclick="tjyiyi('+i+')" style="color:red" id="yiyi'+i+'">异议详情</a>&nbsp&nbsp<a href="#" onclick="chexiao('+i+')" style="color:red" id="chexiao'+i+'">撤销</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');
		    			}else if(sh_state==2){
		    				//未通过
		    					sh_state_str="复核异议不通过"	
		    						$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+firint+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="color:red;text-align: center">'+sh_state_str+'</td><td icode="BM_OPTIONS" style="text-align: center"><a href="#" onclick="chakan('+i+')" style="color:lightseagreen" >查看</a>&nbsp&nbsp<a onclick="formsubmit('+i+')" href="#" style="color:lightseagreen" id="shenkeliucheng">审核明细</a>&nbsp;&nbsp;<a href="#" data-toggle="modal" onclick="tjyiyi('+i+')" style="color:red" id="yiyi'+i+'">异议详情</a>&nbsp&nbsp<a href="#" onclick="chexiao('+i+')" style="color:red" id="chexiao'+i+'">撤销</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');
		    			}else if(sh_state==0){
			    			//待审核
			    			$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+firint+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">'+sh_state_str+'</td><td style="text-align: center"><a onclick="chakan('+i+')" href="#" style="color:lightseagreen" >查看</a>&nbsp&nbsp<a href="#" onclick="chexiao('+i+')" style="color:red" id="chexiao'+i+'">撤销</a>&nbsp&nbsp<a onclick="formsubmit('+i+')" href="#" style="color:lightseagreen" id="shenkeliucheng">审核明细</a>&nbsp;&nbsp;<a href="#" data-toggle="modal" onclick="tjyiyi('+i+')" style="color:red" id="yiyi'+i+'">异议详情</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');
			    		}
		    			
		    			
		    		}
		    	}else {
		    		$("#ybmtable tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+firint+'</td><td class="indexTD" style="text-align: center">'+leixng+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: center">'+type+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: center">'+BM_STARTDATE+"-"+BM_ENDDATE+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: center">审核结束</td><td style="text-align: center"><a onclick="chakan('+i+')" href="#" style="color:lightseagreen" >查看</a>&nbsp&nbsp<a style="color:red" id="chexiao">已撤销</a></td><td class="rhGrid-td-hide" id="baomingid'+i+'">'+BM_ID+'</td></tr>');	
		    	}
		    
  	  
  	  }
		var table = document.getElementById("ybmtable");  
    	 rowscolor(table);
  }; 
 /* 添加分页展示*/
  listPage.prototype.bldPage = function () {
      this._buildPageFlag = true;
      var _self = this;
      this._page = jQuery(".rhGrid-page");
      this._page.html('');
      //判断是否构建分页
      if (this._buildPageFlag === "false" || this._buildPageFlag === false) {
          this._page.addClass("rhGrid-page-none");
      } else if (this._lPage.PAGES === null) {//没有总条数的情况
          if (this._lPage.NOWPAGE > 1) {//上一页 {"ALLNUM":"1","SHOWNUM":"1000","NOWPAGE":"1","PAGES":"1"}
// 		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function(){
              this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'><</a>").click(function () {
                  _self.prePage();
              }));
          } else {
// 		this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
              this._page.append("<span class='disabled ui-corner-4'><</span>");
          }
          this._page.append("<span class='current ui-corner-4'>" + this._lPage.NOWPAGE + "</span>");	//当前页
          if (this._lData.length === this._lPage.SHOWNUM) {//下一页
// 		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
              this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>></a>").click(function () {
                  _self.nextPage();
              }));
          } else {
// 		this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
              this._page.append("<span class='disabled ui-corner-4'>></span>");
          }
      } else if (!jQuery.isEmptyObject(this._lPage)) {
          // 当前页码
          var currentPageNum = parseInt(this._lPage.NOWPAGE);
          // 总页数
          var sumPage = parseInt(this._lPage.PAGES);

          if (this.startNum + this.showPageNum < sumPage) {
              this.endNum = this.startNum + this.showPageNum
          } else {
              this.endNum = sumPage;
          }

          // 总条数
          var allNum = parseInt(this._lPage.ALLNUM);
          // 显示上一页
          if (currentPageNum !== 1) {
// 		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function(){
              this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'><</a>").click(function () {
                  _self.prePage();
              }));
          } else {
// 		this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
              this._page.append("<span class='disabled ui-corner-4'><</span>");
          }
          // 移动页码
          if (currentPageNum > this.startNum + Math.floor((this.endNum - this.startNum) / 2)) {// 如果点击了后面的页码，则后移
              if (currentPageNum === sumPage) {// 点击了最后一页
                  this.endNum = sumPage;

                  if (this.endNum - this.showPageNum > 0) {
                      this.startNum = this.endNum - this.showPageNum;
                  } else {
                      this.startNum = 1;
                  }
              } else {
                  if (currentPageNum > this.showPageNum) {
                      this.endNum = currentPageNum + 1;
                      this.startNum = currentPageNum - this.showPageNum + 1;
                  }
              }
          } else {// 否则前移
              if (currentPageNum === 1) {// 点击了第一页
                  this.startNum = 1;
              } else {
                  this.startNum = currentPageNum - 1;
              }
              if (this.startNum + this.showPageNum < sumPage) {
                  this.endNum = this.startNum + this.showPageNum;
              } else {
                  this.endNum = sumPage;
              }
          }
          // 显示首页
          if (this.startNum !== 1) {
              this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>1</a>").click(function () {
                  _self.gotoPage(parseInt(jQuery(this).html()));
              })).append("...");
          }
          // 如果总页数小于本页显示的最大页码
          if (sumPage < this.endNum) {
              this.endNum = sumPage;
          }
          // 显示中间页码
          for (var i = this.startNum; i <= this.endNum; i++) {
              if (i === currentPageNum) {// 构建当前页
                  this._page.append("<span class='current ui-corner-4'>" + i + "</span>");
              } else {
                  this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>" + i + "</a>").click(function () {
                      _self.gotoPage(parseInt(jQuery(this).html()));
                  }));
              }
          }
          // 显示尾页
          if (sumPage > this.endNum) {
              this._page.append("...").append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>" + sumPage + "</a>").click(function () {
                  _self.lastPage();
              }));
          }
          // 显示下一页
          if (currentPageNum !== sumPage) {
// 		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
              this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>></a>").click(function () {
                  _self.nextPage();
              }));
          } else {
// 		this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
              this._page.append("<span class='disabled ui-corner-4'>></span>");
          }
          // 显示跳转到指定页码
          if (sumPage > 6) {
              this._page.append("<input class='toPageNum ui-corner-4' type='text' value=''/>").append(jQuery("<input class='toPageBtn' type='button' value='GO' />").click(function () {
                  try {
                      var val = parseInt(jQuery(this).prev().val());
                      if (val >= 1 && val <= sumPage) {
                          _self.gotoPage(val);
                      }
                  } catch (e) {
                      // 页码转换异常，忽略
                  }
              }));
          }
          //总条数显示
// 	jQuery("<span class='allNum'></span>").text("共" + allNum + "条").appendTo(this._page);
          jQuery("<span class='allNum'></span>").text(Language.transArr("rh_ui_grid_L1", [allNum])).appendTo(this._page);
      }
      return this._page;
  };
  //默认跳转到第一页
  /*new listPage().gotoPage(1);*/
