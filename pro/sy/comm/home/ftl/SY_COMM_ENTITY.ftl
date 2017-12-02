<script type="text/javascript" >
var param={};
param['type']="0";
var result = FireFly.doAct("TS_QJLB_QJ","getQtData",param);
var datalist = result.datalist;
	var j=1;
for(var i=0;i<datalist.length;i++){
	var newTR='<tr style="height:30px;" >'+
	'<td width="5%" align="center">'+j+'</td>'+
	'<td width="5%" align="center">'+datalist[i].TITLE+'</td>'+
	'<td width="5%" align="center">'+datalist[i].start+'</td>'+
	'<td width="5%" align="center">'+datalist[i].end+'</td>'+
	'<td align="center">待安排</td>'+
	'<td align="center" id='+datalist[i].DATA_ID+'><span style="color:lightblue"><a style="text-decoration:none;" id='+datalist[i].TODO_ID+' qjid="'+datalist[i].DATA_ID+'" href="javascript:void(0);">审批</a></span></td>'+
	'</tr>';
	$("#qjtable").append(newTR);
	$("#"+datalist[i].TODO_ID).click(function(){
		var todoid = $(this).attr("id");
		var qjid = $(this).attr("qjid");
		 $("#qjid").val(qjid);
		   $("#todoId").val(todoid);
		   $("#hidden").val("2");
		   $("#tiaozhuanform").submit();
	});
	j++;
}
$("td").css("border","solid 1px #dddddd");
$("#qjbach").click(function(){
	$("#bach").val("1");
	$("#bachformqj").submit();
})
</script>

<div id='TS_COMM_TODO' class='portal-box'>
<div class='portal-box-title'><span class='portal-box-title-icon icon_portal_todo'></span><span class="portal-box-title-label">待审核的请假</span><span class="portal-box-hideBtn  conHeanderTitle-expand"></span><span class="portal-box-more"><a href="#" id="qjbach"></a></span></div>
<div>

<table id="qjtable"   style="border:solid 1px #dddddd;width:100%">
<tr style="background:f4fbff;height:30px;color:#999999"><td width="5%" align="center">序号</td><td width="35%" align="center">名称</td><td width="15%" align="center">审核开始时间</td><td width="15%" align="center">审核截止日期</td><td width="15%" align="center">状态</td><td width="15%" align="center">操作</td></tr>

</table>
<form id="tiaozhuanform" target="_blank" style="display:none" method="post" action="/ts/jsp/qjlb_qj2.jsp">
<input id="todoId" name ="todoId" />
<input id="qjid" name ="qjid" />
<input id="hidden" name ="hidden" />
</form>
<form id="bachformqj" target="_blank" style="display:none" method="post" action="/qt/jsp/todo.jsp">
<input name = "batch" id="bach">
</form>
</div>
</div>
