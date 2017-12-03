_viewer=this;

_viewer.getBtn("ImpFullData").unbind("click").bind("click",function(event) {
		var statu = confirm("你确定要现在开始导入全量接口表数据?         此操作会删除全量表所有数据，请慎重操作！！！跳转后无论看到什么情况（包括错误提示500），都请等待数据导入完成，期间不要做任何操作，否则就会导致系统瘫痪！");
		  if(!statu){
		   return false;
		  }
		  //跳转到全量导入页面initData.jsp并传递参数 R=12&ACT=impfulldata&ADMIN=0000803837;
		  //可供选择的用户名有：0000803837 或 0000956635 或 0000769940  因为这些统一认证的用户投产时会使用
		  window.location.href="../../sy/mgr/initData.jsp"+"?R=12&ACT=impfulldata&ADMIN=0000803837";
});