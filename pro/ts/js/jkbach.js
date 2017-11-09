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
	//页面的输入查询条件放入传递的参数中
		var param={};
		param["user_code"]=user_code;
		param["nowpage"]=num;
		param["shownum"]=myts;
		param['type']=1;
     	return FireFly.doAct("TS_QJLB_QJ","getQjData",param);
     /*return FireFly.getListData("TS_KS_CAL", data, false);*/
//     debugger;
 };
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
	 var datalist = listData.datalist;
	 for(var i=0;i<datalist.length;i++){
	 	var j=1;
	 	var newTR='<tr style="height:40px">'+
	 	'<td><input type="checkbox" id='+datalist[i].TODO_ID+' name="checkboxqj"/></td>'+
	 	'<td width="5%" align="center">'+j+'</td>'+
	 	'<td width="5%" align="center">'+datalist[i].TITLE+'</td>'+
	 	'<td width="5%" align="center">'+datalist[i].start+'</td>'+
	 	'<td width="5%" align="center">'+datalist[i].end+'</td>'+
	 	'<td align="center">待安排</td>'+
	 	'<td align="center" id='+datalist[i].DATA_ID+'><a id='+datalist[i].TODO_ID+' href="javascript:void(0);"><span style="color:lightblue">审批</span></a></td>'+
	 	'</tr>';
	 	$("#qjtable").append(newTR);
	 	$("#"+datalist[i].TODO_ID).click(function(){
	 		var todoid = $(this).attr("id");
	 		var qjid = $(this).parent().attr("id");
	 		 $("#jkid").val(qjid);
	 		   $("#todoId").val(todoid);
	 		   $("#hidden").val("2");
	 		   $("#tiaozhuanform").submit();
	 	});
	 	j++;
	 }
	 var rows =jQuery("#table").find("tr");  
		  	 for(i = 0; i < rows.length; i++){  
		     	 if(i % 2 == 0){  
		    	    rows[i].style.backgroundColor = "Azure";  
		    	  }  
		    	} 
		  	
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
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function () {
                 _self.prePage();
             }));
         } else {
//		this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
             this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
         }
         this._page.append("<span class='current ui-corner-4'>" + this._lPage.NOWPAGE + "</span>");	//当前页
         if (this._lData.length === this._lPage.SHOWNUM) {//下一页
//		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function () {
                 _self.nextPage();
             }));
         } else {
//		this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
             this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
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
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function () {
                 _self.prePage();
             }));
         } else {
//		this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
             this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
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
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function () {
                 _self.nextPage();
             }));
         } else {
//		this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
             this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
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
 //默认跳转到第一
 new listPage().gotoPage(1);

 $('#bachsh').click(function(){
 	var inputs = $("input[name='checkboxqj']:checked");
 	if(inputs.length==0){
 	alert("没有选中数据");
 	}else{
 		$("#tiJiao").modal().show;
 		
 	}
 })

 function mttijiao(){
	 var param={};
	 var radiovalue = $('input:radio:checked').val();
	  var liyou = document.getElementById("liyou").value;
	 param["shstatus"]=radiovalue;
	 param["shreason"]=liyou;
	 if(radiovalue==1){
		 param["isRetreat"]="false";
	 }else{
		 param["isRetreat"]="true";
	 }
	 var ids = "";
	 $("input[name='checkboxqj']:checked").each(function(){
		 ids+=$(this).attr("id");
	 });
	 param["todoId"]=ids;
	 //批量通过
	 var result = FireFly.doAct("TS_JKLB_JK","updateData",param);
	 $("#tiJiao").modal().hide;
 }