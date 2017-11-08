<script type="text/javascript" >
var param={};
param['type']=2;
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
		 $("#jkid").val(qjid);
		   $("#todoId").val(todoid);
		   $("#tiaozhuanform").submit();
	});
	j++;
}

</script>
<div id='TS_COMM_TODO' class='portal-box'>
<div class='portal-box-title'><span class='portal-box-title-icon icon_portal_todo'></span><span class="portal-box-title-label">待我审批的借考</span><span class="portal-box-hideBtn  conHeanderTitle-expand"></span><span class="portal-box-more"><a href="###SY_COMM_REMIND" onclick="Tools.openCard(this)"></a></span></div>
<div>

<table border="solid 1px" width="100%">
<tr style="background:whitesmoke;height:40px"><td width="5%" align="left">#</td><td width="35%" align="center">名称</td><td width="15%" align="center">审批开始时间</td><td width="15%" align="center">审批截止日期</td><td width="15%" align="center">状态</td><td width="15%" align="center">操作</td></tr>

</table>
<form id="tiaozhuanKCform" style="display:none" method="post" action="jklb_jk2.jsp">
<input id="todoId" name ="todoId" />
<input id="jkid" name ="jkid" />
</form>
</div>
</div>
