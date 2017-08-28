
$(function() {
	$(".wrapper").css("background-color", "#064d78");
	$(".main-sidebar").css("background-color", "#064d78");
	$(".main-header").css("background-color", "white");
	userInfo();
	showMenu();
});

/**
 * 用户信息
 */
function userInfo() {
	var userName = System.getVar("@USER_NAME@");
	var odeptName = System.getVar("@ODEPT_NAME@");
	$("#userInfo").append("<p>" + userName + "</p>").append(
			"<p>" + odeptName + "</p>");
	var perImg = FireFly.getContextPath() + System.getUser("USER_IMG");
	$("#userInfo2").append("<span class='hidden-xs'>" + userName + "</span>");
	$("#userImg1").attr('src',perImg); 
	$("#userImg2").attr('src',perImg); 
	$("#userImg3").attr('src',perImg);  
}

/**
 * 菜单列表
 */
function showMenu() {
	var data = FireFly.doAct("TS_UTIL", "getMenu", {});
	if (data.menuList.length > 0) {
		for (var i = 0; i < data.menuList.length; i++) {
			$(".sidebar-menu")
					.append(
							'<li><a href="'+data.menuList[i].MENU_URL+'" target="blank"><i class="fa fa-book"></i> <span>'
									+ data.menuList[i].MENU_NAME
									+ '</span></a></li>');
		}
	}
}

