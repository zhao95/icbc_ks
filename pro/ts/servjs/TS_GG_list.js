/** 服务卡片使用的js方法定义：开始fromTable */
var _viewer = this;

var res = _viewer.grid.getBtn("look");
res.unbind("click").bind("click",function() {
var pk = jQuery(this).attr("rowpk");//获取主键信息
//var title = _viewer.grid.getRowItemValue(pk,"KS_TITLE");
//var content = _viewer.grid.getRowItemValue(pk,"KS_NEIRONG");

 window.open('/qt/jsp/gg.jsp?id='+pk, 'newwindow', 'height=100, width=400, top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes, location=yes, status=yes')
	
 return false;
});