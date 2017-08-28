<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>考位安排</title>
<link rel="stylesheet" href="../js/dist/themes/default/style.min.css" />
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.1/jquery.min.js"></script>
<script src="../js/dist/jstree.min.js"></script>

</head>

<frameset rows="5%,*" cols="*">
	<frame src="./kwap_top.jsp" marginwidth="5" marginheight="5"
		scrolling="Auto" frameborder="0" bordercolor="#0000FF" />
	<frameset cols="20%,*">
		<frame src="./kwap_left.jsp" marginwidth="5" marginheight="5"
			scrolling="Auto" frameborder="0" bordercolor="#0000FF" />
			<frame src="./kwap_right.jsp" name="right" id="right" />
			<body>
			</body>
</html>
