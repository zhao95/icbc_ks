/** 列表页面渲染引擎 */
GLOBAL.namespace("mb.vi");

mb.vi.listTrack = function (){
	this._pCon = jQuery("body");
//	this.top = jQuery("<div></div>").addClass("mbTopBar").appendTo(this._pCon);
//	
//	var table = jQuery("<table class='mbTopBar-table'></table>").appendTo(this.top);
//    var tr = jQuery("<tr></tr>").appendTo(table);
//	var left = jQuery("<td class='mbTopBar-left'></td>").appendTo(tr);
//	var center = jQuery("<td class='mbTopBar-center'></td>").appendTo(tr);
//	this.right = jQuery("<td class='mbTopBar-right'></td>").appendTo(tr);
//	
//	this.back = jQuery("<div>返回</div>").addClass("mbTopBar-back").appendTo(left); 
//    left.bind("click",function() {
//    	left.addClass("mbTopBar-backActive");
//    	history.go(-1);
//    });
}

mb.vi.listTrack.prototype.render = function(){
	var listContainer = jQuery("<div></div>").addClass("mbList-container mbTtrack-list");
	var top = jQuery("<div></div>").addClass("mbList-container-top").appendTo(listContainer);//头部
	this.con = jQuery("<div></div>").addClass("mbList-container-con").appendTo(listContainer);//主内容区
	this.gridContainer = jQuery("<div></div>").addClass("mbList-container-con-grid").appendTo(listContainer);//列表外容器

	listContainer.appendTo(this._pCon);	
	
	this.loadData();
}

mb.vi.listTrack.prototype.loadData = function(){
	var _self = this;
	__transPrams["_isMobile"] = true;
	FireFly.doAct("SY_WFE_TRACK", "mbList", __transPrams, false,true,function(res){
		_self.con.append(res._CONTENT);
	});
}