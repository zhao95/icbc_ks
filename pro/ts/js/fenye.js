/* 考试日历查看详情js
 */
//分页+查询
var user_code = System.getVar("@USER_CODE@");
var listPage = function () {
    // 构建页码所需参数
    this.showPageNum = 5; // 最多显示的页码
    this.startNum = 1; // 中间页码的第一个页码
    this.endNum = this.startNum; // 中间页码的最后一个页码
};
 listPage.prototype.getListData = function (num) {
	//每页条数
		var select = document.getElementById("yema");
		 var index = document.getElementById("yema").selectedIndex;
		var myts = select.options[index].value;
		
	 var name = $("#mc").val();
	    var zuzhidanwei =  $("#zzdw").val();
	    var where1 = "";
	    var where2 = "";
	    if(jQuery.trim(name)!=""){
	    	where1 = "AND XM_NAME like"+"'%"+name+"%'";
	    }
	    if(jQuery.trim(zuzhidanwei)!=""){
	    	where2 = " AND XM_FQDW_NAME like"+"'%"+zuzhidanwei+"%'"
	    }
		var type =  document.getElementById("zhuangtai");
	var index = type.selectedIndex;
	var  zhuangtai = type.options[index].value;
	sqlWhere = where1 + where2;
	var param = {};
	//页面的输入查询条件放入传递的参数中
	var param={};
	param["zhuangtai"]=zhuangtai;
		param["user_code"]=user_code;
		param["nowpage"]=num;
		param["shownum"]=myts;
	param["where"] = sqlWhere;
		
     	return FireFly.doAct("TS_XMGL","getUncheckList",param)
     /*return FireFly.getListData("TS_KS_CAL", data, false);*/
//     debugger;
 };
 //全局变量  sql查询条件(页面输入的搜索条件)
 
 var sqlWhere= "";
// 创建页面显示数据的主体
 listPage.prototype._bldBody = function (num) {
	 
     var listData = this.getListData(num);
     this._lPage = listData._PAGE_;
     console.log(listData);
     this.bldTable(listData);
     this.bldPage();
     var listPage=this;
   //查询条件按钮（设置查询考试名称和年份的条件）
 /* jQuery("#search").unbind("click").click(function(){
	  alert("a");
	  new listPage().gotoPage(1);
     });*/

		//渲染隔行背景色
  		//var table = jQuery("#kstable");  
  		//     rowscolor(table);
     	//背景色渲染方法
  		//function rowscolor(table){
		//	 var rows =jQuery("#tbody_data").find("tr");  
		//   	 for(i = 0; i < rows.length; i++){  
		//      	 if(i % 2 == 0){  
		//      	    rows[i].style.backgroundColor = "Azure";  
		//      	  }  
		//      	} 
		//}
 };
 function fenyeselect(){
	 new listPage().gotoPage(1);
 }
 
 /*跳转到指定页*/
 listPage.prototype.gotoPage = function (num) {
	 
     this._bldBody(num);
 };
 /*上一页*/
 listPage.prototype.prePage = function() {
     var prePage = parseInt(this._lPage.NOWPAGE) - 1;
     var nowPage = "" + ((prePage > 0) ? prePage:1);
     this.gotoPage(nowPage);
 };
 /*下一页*/
 listPage.prototype.nextPage = function() {
     var nextPage = parseInt(this._lPage.NOWPAGE) + 1;
     var pages = parseInt(this._lPage.PAGES);
     var nowPage = "" + ((nextPage > pages) ? pages:nextPage);
     this.gotoPage(nowPage);
//     debugger;
 };
 /*首页*/
 listPage.prototype.firstPage = function() {
     this.gotoPage(1);
 };
 /*末页*/
 listPage.prototype.lastPage = function() {
     this.gotoPage(this._lPage.PAGES);
 };
 listPage.prototype.bldTable = function (listData) {
	 $("#table tbody").html("");
	 var data = listData.list;
	 if(data==null){
		 return;
	 }
	 var pageEntity=JSON.parse(data);
	 for(var i=0;i<pageEntity.length;i++){
		 var strfirst = listData.first;
		 var first = parseInt(strfirst);
 		 var name = pageEntity[i].XM_NAME;
			var zzdw = pageEntity[i].XM_FQDW_NAME;
			var startdate = pageEntity[i].XM_START;
			var cjsj = pageEntity[i].S_ATIME;
			var enddate = pageEntity[i].XM_END;
			var xmtype = pageEntity[i].XM_TYPE;
			var id = pageEntity[i].XM_ID;
			var state = "已结束";
			//创建新时间 判断 状态
			var param1={};
			param1["xmid"]=id;
			var result1 = FireFly.doAct("TS_XMGL_BMGL","getBMState",param1);
			var data1 = result1.list;
			var pageEntity1 = JSON.parse(data1);
			//报名开始时间
			var startTime = pageEntity1[0].START_TIME;
			var state1 = pageEntity1[0].STATE;
			if(state1=="待报名"){
				state="报名审核"
			}else if(state1=="已结束"){
				
			}else{
				state = "未开始";
			}
			//是否展示审核人所在的 审核机构下的所有人
		/*	var resultlook = FireFly.doAct("TS_XMGL_BMGL","getShowLook",param1);
			var showlook = resultlook.showlook;*/
			/*
			//进行中 已结束 下拉框 进行筛选
			var zhuangtai = $("#zhuangtai").children('option:selected').val();
			if(zhuangtai=="1"&&state!="报名审核"){
				//进行中的项目
				continue;
			}else if(zhuangtai=="2"&&state!="已结束"){
				continue
			}*/
			//添加一行隐藏的项目id
			var xuhao = first+i;
 		//为table重新appendtr
		/*	if(state=="报名审核"){
 			$("#table tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+xuhao+'</td><td class="indexTD" style="text-align: left">'+name+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: left">'+zzdw+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: left">'+cjsj+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: left">'+state+'</td><td class="rhGrid-td-hide" id="XM_ID'+i+'" >'+id+'</td><td class="rhGrid-td-hide" id="XM_TYPE'+i+'">'+xmtype+'</td><td><input class = "btn" type="button" onclick="tiaozhuan('+i+')" style="border:none;color:white;font-size:13px;background-color:LightSeaGreen;height:30px;width:70px" value="审核"></input>&nbsp;&nbsp;<input data-toggle="modal" data-target="#bminfo" onclick="chakan('+i+')" type="button" class="btn" style="border:none;color:white;font-size:13px;background-color:LightSeaGreen;height:30px;width:70px" value="查看"></input></td></tr>');
     		
			}else{
				$("#table tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+xuhao+'</td><td class="indexTD" style="text-align: left">'+name+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: left">'+zzdw+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: left">'+cjsj+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: left">'+state+'</td><td class="rhGrid-td-hide" id="XM_ID'+i+'" >'+id+'</td><td><input data-toggle="modal" data-target="#bminfo" onclick="chakan('+i+')" class="btn" type="button" style="border:none;color:white;font-size:13px;background-color:LightSeaGreen;height:30px;width:70px" value="查看"></td></tr>');	
			}*/
		/*	</input>&nbsp;&nbsp;<input onclick="chakanbelong('+i+')" type="button" class="btn" style="border:none;color:white;font-size:13px;background-color:LightSeaGreen;height:30px;width:100px" value="辖内报名情况"></input>*/
			if(state=="报名审核"){
					$("#table tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+xuhao+'</td><td class="indexTD" style="text-align: left">'+name+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: left">'+zzdw+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: left">'+cjsj+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: left">'+state+'</td><td class="rhGrid-td-hide" id="XM_ID'+i+'" >'+id+'</td><td class="rhGrid-td-hide" id="XM_TYPE'+i+'">'+xmtype+'</td><td style="text-align:center"><input class = "btn" type="button" onclick="tiaozhuan('+i+')" style="border:none;color:white;font-size:13px;background-color:LightSeaGreen;height:30px;width:70px" value="审核"></input>&nbsp;&nbsp;<input data-toggle="modal" data-target="#bminfo" onclick="chakan('+i+')" type="button" class="btn" style="border:none;color:white;font-size:13px;background-color:LightSeaGreen;height:30px;width:70px" value="查看"></td></tr>');
			}else{
					$("#table tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+xuhao+'</td><td class="indexTD" style="text-align: left">'+name+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: left">'+zzdw+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: left">'+cjsj+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: left">'+state+'</td><td class="rhGrid-td-hide" id="XM_ID'+i+'" >'+id+'</td><td style="text-align:center"><input data-toggle="modal" data-target="#bminfo" onclick="chakan('+i+')" class="btn" type="button" style="border:none;color:white;font-size:13px;background-color:LightSeaGreen;height:30px;width:70px" value="查看"></td></tr>');	
			}
 	  
 	  }
	var table= document.getElementById("table");
	rowscolor(table);
 }; 
 
 /*添加分页展示*/
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
//		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function(){
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'><</a>").click(function () {
                 _self.prePage();
             }));
         } else {
//		this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
             this._page.append("<span class='disabled ui-corner-4'><</span>");
         }
         this._page.append("<span class='current ui-corner-4'>" + this._lPage.NOWPAGE + "</span>");	//当前页
         if (this._lData.length === this._lPage.SHOWNUM) {//下一页
//		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>></a>").click(function () {
                 _self.nextPage();
             }));
         } else {
//		this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
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
//		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function(){
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'><</a>").click(function () {
                 _self.prePage();
             }));
         } else {
//		this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
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
//		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>></a>").click(function () {
                 _self.nextPage();
             }));
         } else {
//		this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
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
//	jQuery("<span class='allNum'></span>").text("共" + allNum + "条").appendTo(this._page);
         jQuery("<span class='allNum'></span>").text(Language.transArr("rh_ui_grid_L1", [allNum])).appendTo(this._page);
     }
     return this._page;
 };
 //默认跳转到第一页
/* new listPage().gotoPage(1);*/
 function ztcx(){
	 new listPage().gotoPage(1);
 }
 function xzcu(){
	 new listPage().gotoPage(1);
 }
 