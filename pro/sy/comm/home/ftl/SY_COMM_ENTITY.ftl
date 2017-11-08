<script type="text/javascript" >
var param={};
param['type']=1;
var result = FireFly.doAct("TS_QJLB_QJ","getQjData",param);
var datalist = result.datalist;
for(var i=0;i<datalist.length;i++){
	var j=1;
	var newTR='<tr style="height:40px">'+
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
		 $("#qjid").val(qjid);
		   $("#todoId").val(todoid);
		   $("#tiaozhuanform").submit();
	});
	j++;
}
function openMoreZhubanListPage(SERVID){
	Tab.open({'url':'SY_COMM_TEMPL.show.do?model=view&pkCode='+SERVID,'tTitle':111,'menuFlag':3});
}
</script>
<div id='TS_COMM_TODO' class='portal-box'>
<div class='portal-box-title'><span class='portal-box-title-icon icon_portal_todo'></span><span class="portal-box-title-label">待审核的请假</span><span class="portal-box-hideBtn  conHeanderTitle-expand"></span><span class="portal-box-more"><a href="#" onclick="openMoreZhubanListPage('QJ_BACH_SP')"></a></span></div>
<div>

<table id="qjtable" border="solid 1px" width="100%">
<tr style="background:whitesmoke;height:40px"><td width="5%" align="left">#</td><td width="35%" align="center">名称</td><td width="15%" align="center">审核开始时间</td><td width="15%" align="center">审核截止日期</td><td width="15%" align="center">状态</td><td width="15%" align="center">操作</td></tr>

</tr>
</table>
<form id="tiaozhuanform" target="_blank" style="display:none" method="post" action="qjlb_qj2.jsp">
<input id="todoId" name ="todoId" />
<input id="qjid" name ="qjid" />
</form>
</div>
</div>
