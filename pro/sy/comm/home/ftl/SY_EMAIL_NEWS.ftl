﻿<style type="text/css">
body {
	font: normal 16px auto "宋体", Verdana, Arial, Helvetica, sans-serif;
	color: #000;
	background: #FFF;
	padding-left: 15px
}

#maintable {
	width: 750px;
	padding: 0;
	margin: 0;
	text-align: center;
	border-collapse: collapse
}

#maintable th {
	font: normal 16px auto "宋体", Verdana, Arial, Helvetica, sans-serif;
	border: 1px solid #888;
	letter-spacing: 1px;
	padding: 6px 6px 6px 12px;
	background: #e4e4e4 no-repeat
}

#maintable td {
	font: normal 16px auto "宋体", Verdana, Arial, Helvetica, sans-serif;
	border: 1px solid #888;
	background: #fff;
	padding: 6px 6px 6px 12px;
	text-align: left
}

#maintable a {
	color: #c75f3e
}

#footertable {
	font: 12px "宋体", Verdana, Arial, Helvetica, sans-serif
}

#footertable a {
	color: #1c48fd
}
</style>

<script type="text/javascript">
	var nullFun = function() {
		return false;
	}
	//禁用选择
	document.onselectstart = nullFun;
	//禁止复制
	document.oncopy = function() {
		document.selection.empty();
	};
	document.onselect = function() {
		document.selection.empty();
	};
	document.onpaste = nullFun;
	document.oncut = nullFun;
	//禁止查看源代码
	document.oncontextmenu = nullFun;
</script>
<div>
	<span>您好：</span><br /> <br />

	<div style="padding-left: 5%">
		<span>${title!""}信息如下：</span><br> <br> <br>
		<table id="maintable" cellspacing="0">
			<tbody>
				<tr>
					<th width="100%" colspan="4">${title!""}</th>
				</tr>
				<tr>
					<!--<th width="20%">出车事由</th>-->
					<td width="80%" colspan="4">${tipMsg!""}</td>
				</tr>

			</tbody>
		</table>


		<br /> <br /> <span>此邮件是系统自动发送邮件，无需回复。</span>

	</div>
</div>

<div style="padding-top: 50px;" color="gray">
	<hr width="300px" color="gray" size="1" align="left" />
	<table id="footertable">
		<tr>
			<td>办公信息化系统</td>
			<td style="padding-left: 10px"><a href="http://oas.icbc">http://oas.icbc</a></td>
		</tr>
		<tr>
			<td>OIS</td>
			<td style="padding-left: 10px">${CURRENT_TIME!''}</td>
		</tr>
	</table>
</div>
