var _viewer = this;
_viewer.grid.unbindTrdblClick();
_viewer.grid.dblClick(function(value, node) {
	Todo.openEntity(_viewer);
}, _viewer);

_viewer.grid.getBodyTr().each(function(){
	var $sMtime = jQuery(this).find("td[icode='S_MTIME']");
    if($sMtime){
    	var title = $sMtime.attr("title");
	    var text = $sMtime.text();
	    $sMtime.attr("title",title.substring(0,19));
	    $sMtime.text(text.substring(0,19));
    }
});