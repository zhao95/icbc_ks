_viewer=this;
//时间表单校验
_viewer.getItem("G_DEAD_BEGIN").obj.unbind("click").bind("click", function() {
	WdatePicker({
		dateFmt: 'yyyy-MM-dd HH:mm:ss',
		maxDate : "#F{$dp.$D('" + _viewer.servId + "-G_DEAD_END')}"
	});
});
_viewer.getItem("G_DEAD_END").obj.unbind("click").bind("click", function() {

	WdatePicker({
		dateFmt: 'yyyy-MM-dd HH:mm:ss',
		minDate : "#F{$dp.$D('" + _viewer.servId + "-G_DEAD_BEGIN')}"
	});
});