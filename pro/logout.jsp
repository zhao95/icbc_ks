<%@ include file="sy/base/view/inHeader.jsp"%>
<script type="text/javascript">
	<%
	boolean debugMode = Context.getSyConf("PE_LOGIN_DEBUG_MODE", false);
	%>
	var debugMode =<%=debugMode%>;
    debugger;
    if(debugMode){
        window.location.href = FireFly.getContextPath() + "/t";
    }else{
        window.close();
    }
</script>
