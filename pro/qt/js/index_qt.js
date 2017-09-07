
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
	$("#userInfo").append("<div>" + userName + "</div>").append(
			"<div>" + odeptName + "</div>");
	var perImg1 = FireFly.getContextPath() + System.getUser("USER_IMG_SRC");
	var perImg2 = FireFly.getContextPath() + System.getUser("USER_IMG");
	$("#userInfo2").append("<span class='hidden-xs' style='font-family: '黑体 Bold', '黑体 Regular', '黑体';font-weight: 700;font-style: normal;font-size: 16px;color: #FFFFFF;text-align: center;'>" + userName + "</span>");

	$("#userInfo2").append("<span class='hidden-xs' style='margin-left:10px;'><i class='fa fa-chevron-circle-down'></i></span>");
	if(perImg1==""){
		//	写死头像图片
		$("#userImg1").attr('src',"/sy/theme/default/images/common/user_64_64.png"); 
		$("#userImg2").attr('src',"/sy/theme/default/images/common/user0.png"); 
	}else{
	$("#userImg1").attr('src',perImg1); 
	$("#userImg2").attr('src',perImg1); 
	}
	$("#userImg3").attr('src',perImg2);  
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

