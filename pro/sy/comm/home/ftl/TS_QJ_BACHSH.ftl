<script type="text/javascript" >
function openMoreZhubanListPage() {
	var strWhere = "";
	var params = {"extWhere":strWhere};
	var opts = {"url":"TS_COMM_TODO.list.do", "tTitle":"待审批请假列表", "menuFlag":4, "params":params};
	Tab.open(opts);
}


function openTitle(SERV_ID,DATA_ID){
   $("#qjid").val(DATA_ID);
   $("#servid").val(SERV_ID);
   $("#tiaozhuanform").submit();
}


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
	}else{
	var ids = "";
	$("input[name='checkboxqj']:checked").each(function(){
	ids+=$(this).attr("id");
	});
	
	}
})


</script>
<div id='TS_COMM_TODO' class='portal-box'>
<div class='portal-box-title'><span class='portal-box-title-icon icon_portal_todo'></span><span class="portal-box-title-label">待审核的请假</span>&nbsp;&nbsp;<button class="btn" id="bachsh">批量审核</button><span class="portal-box-more"><a href="#" onclick="openMoreZhubanListPage()"></a></span></div>
<div>

<table border="solid 1px" width="100%">
<tr style="background:whitesmoke;height:40px"><td width="3%" align='center'><input type="checkbox" id="checkall"/></td><td width="3%" align="center">序号</td><td width="35%" align="center">名称</td><td width="15%" align="center">审核开始时间</td><td width="15%" align="center">审核截止日期</td><td width="15%" align="center">状态</td><td width="15%" align="center">操作</td></tr>
<#list _DATA_ as content>
<tr style="height:40px">

<td width="3%"><input type="checkbox" id='${content.DATA_ID}' name="checkboxqj"/></td>
<td width="3%">
1		
</td>
<td width="5%">
${content.TITLE}
</td>
<td width="5%" align="center">${content.TODO_ID}</td>
<td width="5%" align="center">${content.OWNER_CODE}</td>
<td></td>
<td align="center"><a  href="javascript:void(0);"  onclick="openTitle('${content.SERV_ID!}','${content.DATA_ID!}');><span style="color:lightblue">审批</span></a></td>
</tr>
</#list>
</table>
<form id="tiaozhuanform" style="display:none" method="post" action="qjlb_qj2.jsp">
<input id="servid" name ="todoId" />
<input id="qjid" name ="qjid" />
</form>
</div>
</div>
