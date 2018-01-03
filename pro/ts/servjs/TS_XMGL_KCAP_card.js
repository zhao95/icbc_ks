var _viewer = this;
$("#TS_XMGL_KCAP-save").css("right",200);
var xmId = _viewer.getItem("XM_ID").getValue();

$(".rhCard-tabs li[sid='TS_XMGL_KCAP_KWAP_V']").find("a").attr("href","");
$(".rhCard-tabs li[sid='TS_XMGL_KCAP_KWAP_V']").find("a").click(function(){
	window.open("/ts/jsp/kwap.jsp?xmId="+xmId,"_blank");
});


$(".form-container").hide();

var hdtitle = $("<div>").text("考场安排");

hdtitle.css({"width":"90%","font-family":"华文中宋,宋体","color":"black","font-size":"25px","text-align":"center","vertical-align":"text-top"});

$(".rhCard-btnBar").append(hdtitle).css({"padding":"0px 0px 2px 8px"});

$("#TS_XMGL_KCAP-winTabs").find("a").eq(2).trigger("click");
$("#TS_XMGL_KCAP-winTabs").find("li").eq(1).css("display","none");

function doPost(to, data) {  //to:提交动作（action）,data:参数
    var myForm = document.createElement("form");
    myForm.method = "post";
    myForm.action = to;
    for (var i in data) {
        var myInput = document.createElement("input");
        myInput.setAttribute("name", i);  // 为input对象设置name
        myInput.setAttribute("value", data[i]);  // 为input对象设置value
        myForm.appendChild(myInput);
    }
    document.body.appendChild(myForm);
    myForm.submit();
    document.body.removeChild(myForm);  // 提交后移除创建的form
}