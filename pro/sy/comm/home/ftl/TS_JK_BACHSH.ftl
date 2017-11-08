<script type="text/javascript" >
//全选
$("#checkall").click(function(){
	if($(this).prop("checked")){
	$("input[name='checkboxqj']").each(function(){
	$(this).prop("checked","true");
	});
	}else{
	$("input[name='checkboxqj']").each(function(){
	$(this).prop("checked","false");
	});
	}
});

$('#bachsh').click(function(){
	var inputs = $("input[name='checkboxqj']:checked");
	if(inputs.length==0){
	alert("没有选中数据")
	var winDialog = jQuery("<div></div>").addClass("selectDialog").attr("id","shdialog").attr("title","批量借考请假");
	var container = '<div style="padding-top:8%"><span style="padding-left:20%" id="radiospan1"><input style="vertical-align:text-bottom; margin-bottom:-3;" name="state" type="radio" value="1" checked>审核通过&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span id="radiospan2"><input name="state" style="vertical-align:text-bottom; margin-bottom:-4;" type="radio" value="2">审核不通过</span></div>'
		var content = jQuery(container).appendTo(winDialog);
	
	winDialog.appendTo(jQuery("body"));
	jQuery("#shdialog").dialog({
		autoOpen: false,
		height: 200,
		width: 400,
		modal: true,
		resizable:true,
		position:[450,100],
		buttons : {
			"确定" : function() {
				var ids = "";
				$("input[name='checkboxqj']:checked").each(function(){
				ids+=$(this).attr("id");
				});
				var paramids = {};
				paramids["ids"]=ids;
				FireFly.doAct("","",paramids);
				winDialog.remove();
			},
			"关闭" : function() {
				winDialog.remove();
			}
		},
		open: function() {

		},
		close: function() {
			jQuery("#shdialog").remove();
			//_viewer.refresh();
		}
	});
	
	//手动打开dialog
	var dialogObj = jQuery("#shdialog");
	dialogObj.dialog("open");
	dialogObj.focus();
	
	}else{
	
	
	}
})

//待审批  数据
var param={};
param['type']=1;
var result = FireFly.doAct("TS_QJLB_QJ","getQjData",param);
var datalist = result.datalist;
for(var i=0;i<datalist.length;i++){
	var j=1;
	var newTR='<tr style="height:40px">'+
	'<input type="checkbox" id='+datalist[i].DATA_ID+' name="checkboxqj"/>'
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
<div class='portal-box-title'><span class='portal-box-title-icon icon_portal_todo'></span><span class="portal-box-title-label">待审核的借考</span>&nbsp;&nbsp;<button class="btn" id="bachsh">批量审核</button>&nbsp;&nbsp;<button class="btn" id="sphistory">审批记录</button>&nbsp;&nbsp;<button class="btn" onclick="Tab.close()">返回</button></div>
<div>
<table id="qjtable" border="solid 1px" width="100%">
<tr style="background:whitesmoke;height:40px"><td width="3%" align='center'><input type="checkbox" id="checkall"/></td><td width="5%" align="left">序号</td><td width="35%" align="center">名称</td><td width="15%" align="center">审核开始时间</td><td width="15%" align="center">审核截止日期</td><td width="15%" align="center">状态</td><td width="15%" align="center">操作</td></tr>

</table>
<form id="tiaozhuanKCform" target="_blank" style="display:none" method="post" action="jklb_jk2.jsp">
<input id="todoId" name ="todoId" />
<input id="jkid" name ="jkid" />
</form>
</div>
</div>
