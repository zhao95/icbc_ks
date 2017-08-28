var _viewer = this;
$("#TS_XMGL_KCAP-save").css("right",200);
var xmId = _viewer.getItem("XM_ID").getValue();

$(".rhCard-tabs li[sid='TS_XMGL_KCAP_KWAP_V']").find("a").attr("href","");
$(".rhCard-tabs li[sid='TS_XMGL_KCAP_KWAP_V']").find("a").click(function(){
	window.open("/ts/jsp/kwap.jsp?xmId="+xmId,"_blank");
});
