var _viewer = this;
$("#TS_WFS_ADMINER-save").css("right",200);
$("#TS_WFS_ADMINER-winDialog").find(".ui-form-default,.item").css("padding","0 0 0 0");
$("#TS_WFS_ADMINER-winDialog .form-container").hide();
var hdtitle = $("<div>").text("管理审核人");
hdtitle.css({"width":"90%","font-family":"华文中宋,宋体","color":"black","font-size":"25px","text-align":"center","vertical-align":"text-top"});
$("#TS_WFS_ADMINER-winDialog .rhCard-btnBar").append(hdtitle).css({"padding":"0px 0px 2px 8px"});