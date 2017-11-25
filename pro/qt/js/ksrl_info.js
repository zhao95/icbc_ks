/**
 * 考试日历查看详情js
 */

var ks_type_obj={
		"1":"资格类考试",
		"2":"非资格类考试"
}
$("#initData").unbind("click").bind("click",function(){
	var statu = confirm("Are you sure to delete the current data?");
	  if(!statu){
	   return false;
	  }
	  //跳转到全量导入页面initData.jsp并传递参数 R=12&ACT=impfulldata&ADMIN=0000803837;
	  window.location.href="../../sy/mgr/initData.jsp"+"?R=12&ACT=impfulldata&ADMIN=0000803837";
});
//分页+查询
var listPage = function () {
    // 构建页码所需参数
    this.showPageNum = 5; // 最多显示的页码
    this.startNum = 1; // 中间页码的第一个页码
    this.endNum = this.startNum; // 中间页码的最后一个页码
};
 listPage.prototype.getListData = function (num) {
	////初始化页面
	 var currentDate = new Date;
	 var currentYear = currentDate.getFullYear();
     var data = {};
//     data["_extWhere"] = "and START_DATE like '%"+currentYear+"%'"+sqlWhere;
     data["_extWhere"] = sqlWhere;
     //控制当前页数,显示的条数
     data["_PAGE_"] = {"NOWPAGE": num, "SHOWNUM": 30};
/*     return FireFly.getListData("TS_KS_CAL", data, false);*/
     return FireFly.doAct("TS_KS_CAL", "query");
//     debugger;
 };
 //全局变量  sql查询条件(页面输入的搜索条件)
 var sqlWhere= "";
// 创建页面显示数据的主体
 listPage.prototype._bldBody = function (num) {
	 
     var listData = this.getListData(num);
     this._lPage = listData._PAGE_;
     this._lData = listData._DATA_;
     this.bldTable(listData);
     this.bldPage();
     var listPage=this;
   //查询条件按钮（设置查询考试名称和年份的条件）
  jQuery("#search").unbind("click").click(function(){
     	var calName = jQuery("#ks_name").val();
     	calName = trimAll(calName);
     	var calYear = jQuery("#ks_year").val();
     	calYear = trimAll(calYear);
     	//定义考试名称查询框
     	var whereName ="";
     	//定义考试年份查询框
     	var whereYear ="";
     	//初始化搜索sql条件
//     	sqlWhere = "";
     	if(jQuery.trim(calName)!==""){
     		whereName = "and CAL_NAME LIKE '%"+calName+"%'";
     	}
     	if(jQuery.trim(calYear)!==""){
     		whereYear = "and START_DATE LIKE '%"+calYear+"%'";
     	}
     	sqlWhere = whereName + whereYear;
     	var param = {};
     	//页面的输入查询条件放入传递的参数中
     	param["_extWhere"] = sqlWhere;
     	//获取到查询后的数据
     	var searchResult = FireFly.doAct("TS_KS_CAL","query",param);
     	//将数据填入页面
     	listPage._lPage = searchResult._PAGE_;
     	listPage._lData = searchResult._DATA_;
     	listPage.bldTable(searchResult);
     	listPage.bldPage();
        //table tr  隔行改变背景色
     	//var table = jQuery("#kstable");  
     	//rowscolor(table);
     	//去掉字符串中所有的空格方法
     	 function trimAll(str) {return str.replace(/\s+/g, "");}
     });

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
     var rhGridTBody = jQuery("#tbody_data");
     rhGridTBody.html('');
     for (var i = 0; i < listData._DATA_.length; i++) {
    	 var j=i+1;
         var item = listData._DATA_[i];
         var trs = ['<tr>',
             '<td style="text-align: center;">' + j + '</td>',
             '<td style="text-align: left;">' + item.CAL_NAME + '</td>',
             '<td>' + ks_type_obj[item.CAL_TYPE] + '</td>',
             '<td>' + item.KS_LEVEL + '</td>',
             '<td>' + item.BM_START_DATE + '</td>',
             '<td>' + item.BM_END_DATE + '</td>',
             '<td>' + item.START_DATE + '</td>',
             '<td>' + item.END_DATE + '</td>',
             '<td>' + item.CAL_COMMENT + '</td>',
             '<td>' + item.CAL_MONTH + '</td>',
             '</tr>'].join("");
         rhGridTBody.append(trs);
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
 new listPage().gotoPage(1);