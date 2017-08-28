<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--StdCardView.jsp列表页面-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file= "/sy/base/view/inHeader.jsp" %>
<script type="text/javascript" src="<%=urlPath %>/sy/comm/mind/mind.js"></script>
<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/file/swfupload.js"></script>
<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/file/js/swfupload.queue.js"></script>
<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/file/js/fileprogress.js"></script>
<script type="text/javascript" src="<%=urlPath %>/sy/base/frame/coms/file/js/handlers.js"></script>
<%
    final String CONTEXT_PATH = request.getContextPath();
	String mindServ = RequestUtils.getStr(request, "mindServ");
%>
<body style="background-color:white;">
<div class="content-mainCont fr wp">
	<div style="width:96%;margin:20px auto;" id="mindContainerHI"></div>
</div>


<script language="javascript">
	var _viewer = window.parent.jQuery(window.parent.document).data("shareCradViewer");
	var _wfCardView = _viewer.wfCard;
	
	var opts = {"sId":_viewer.sId,"pkCode":_viewer._pkCode,"parHandler":_viewer};  		
	var _mindWfCardView = new rh.vi.wfCardView(opts);
	
	var pCon = jQuery("#mindContainerHI");

	var param = {"viewer":_viewer,"id":"rh.cm.mind","servId":_viewer._data["SERV_SRC_ID"]
			,"dataId":_viewer._pkCode,"wfCard":_wfCardView, "pCon":pCon, "mindWfCardView":_mindWfCardView};
	
	var mindServ = "<%=mindServ %>";
	if (mindServ) {
		param.mindServ = mindServ;
	}
	
	var mind = new rh.cm.mind(param);
	mind.render();
	jQuery(".mindInputDiv,.mindSaveBar", mind._pCon).hide();
	
	if(mind.MIND_TYPE_LIST_SIZE == 0){
		var info = new Array();
		info.push("<div class='mt50 mb50' style='font-size:14px;'>");
		info.push("本审批单未签意见。");
		info.push("</div>");
		jQuery("#mindContainerHI").append(info.join(""));
	}
	
	Tab.setCardTabFrameHei();
	jQuery(".mindOdpt,.mindSortClick").live("click",function(){
		Tab.setCardTabFrameHei();
	});
	
	jQuery(window).bind("unload", function(){
		_mindWfCardView.destroy();
	});	
</script>
<script type="text/javascript" src="<%=urlPath %>/sy/util/office/zotnClientLib_NTKO.js"></script>
</body>