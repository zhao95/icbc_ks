<%
    final String CONTEXT_PATH = request.getContextPath();
%>	
<script>
	//####<%=System.getProperty("servName")%>####
	var login = "LOGIN";
	var url = FireFly.getContextPath() + "/ksgogogo.jsp"; //"<%=CONTEXT_PATH%>/";
<%-- 	var url = FireFly.getContextPath() + "/index.jsp"; //"<%=CONTEXT_PATH%>/"; --%>
	top.window.location.href = url; 
</script>
