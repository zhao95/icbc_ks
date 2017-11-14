var _viewer = this;
$("#TS_XMGL_CCCS-save").css("right",200);
$("#TS_XMGL_CCCS-winDialog").find(".ui-form-default,.item").css("padding","0 0 0 0");

$(".form-container").hide();

var hdtitle = $("<div>").text("场次测算");

hdtitle.css({"width":"90%","font-family":"华文中宋,宋体","color":"black","font-size":"25px","text-align":"center","vertical-align":"text-top"});

$(".rhCard-btnBar").append(hdtitle).css({"padding":"0px 0px 2px 8px"});