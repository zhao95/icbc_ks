<%@ include file="sy/base/view/inHeader.jsp"%>
<script type="text/javascript">
	<%
	boolean debugMode = Context.getSyConf("PE_LOGIN_DEBUG_MODE", false);
	%>
	var debugMode ="<%=debugMode%>";
	if(debugMode){
		window.close();
	}else{
		window.location.href = FireFly.getContextPath() + "/";
	}
</script>
