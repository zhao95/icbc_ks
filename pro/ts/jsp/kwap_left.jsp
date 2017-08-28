<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    final String CONTEXT_PATH = request.getContextPath();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="../../sy/base/view/inHeader.jsp"%>
<link rel="stylesheet" href="../js/dist/themes/default/style.min.css" />
<script src="../js/dist/jstree.min.js"></script>
</head>
<body>
	考试类型
	<div id="jstree1">
		<!-- in this example the tree is populated from inline HTML -->
		<ul>
			<li>Root node 1
				<ul>
					<li id="child_node_1">Child node 1</li>
					<li>Child node 2</li>
				</ul>
			</li>
			<li>Root node 2</li>
		</ul>
	</div>
	考场场次
	<div id="jstree2">
		<!-- in this example the tree is populated from inline HTML -->
		<!-- <ul>
			<li>Root node 1
				<ul>
					<li id="child_node_1">Child node 1</li>
					<li>Child node 2</li>
				</ul>
			</li>
			<li>Root node 2</li>
		</ul> -->
	</div>

	<script>
		$(function() {
			var xmId = getQueryString("xmId");

			/*  var resData =*/
			FireFly.doAct("TS_XMGL_KCAP_DAPCC", "getKcAndCc", {
				"xmId" : xmId
			}, true, false, function(data) {
				if (data != "") {
					for (var i = 0; i < data.list.length; i++) {
						var child = "";
						for (var j = 0; j < data.list[i].ccList.length; j++) {
							if (j == 0) {
								child += "<ul>";
							}
							
							var date = data.list[i].ccList[j].SJ_START.substring(0,10);
							var start = data.list[i].ccList[j].SJ_START.substring(11);
							var end = data.list[i].ccList[j].SJ_END.substring(11);
							
							var tmp = "<li id = '"+data.list[i].ccList[j].SJ_ID+"'>" + date + "(" + start +"-"+ end + ")" + "</li>";
							child += tmp;
							if (j == data.list[i].ccList.length - 1) {
								child += "</ul>";
							}
						}
						var str = "<ul><li id='" + data.list[i].CC_ID +"'>" + data.list[i].KC_NAME + child + "</li></ul>"
						$("#jstree2").append(str)
					}
				}
			});

			$('#jstree1').jstree();
			$('#jstree1').on("changed.jstree", function(e, data) {
				console.log(data.selected);
			});

			$('#jstree2').jstree();
			$('#jstree2').on("changed.jstree", function(e, data) {
				console.log(data.selected);
			});

			function getQueryString(name) {
				var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
				var r = window.parent.location.search.substr(1).match(reg);
				if (r != null)
					return unescape(r[2]);
				return null;
			}
		});
	</script>
</body>
</html>