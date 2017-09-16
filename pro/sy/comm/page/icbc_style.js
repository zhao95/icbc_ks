//这个javascript为ICBC所特有，关于ICBC样式的改变都是调用的这个文件，方便查询

var ICBC = {};

ICBC.todoRefresh = function() {

	try {
		if (top && top.FireFly) { //本系统页面
			//因私出国（境）
			if (window.frames['PE_APPLICATION_TODO-tabFrame']) {
	            window.frames['PE_APPLICATION_TODO-tabFrame'].location.reload();
	        }
			//日常办公
	        if (window.frames['OS_AP_TODO-tabFrame']) {
	            window.frames['OS_AP_TODO-tabFrame'].location.reload();
	        }
	        //分行考勤
	        if (window.frames['KQ_FORM_TODO-tabFrame']) {
	            window.frames['KQ_FORM_TODO-tabFrame'].location.reload();
	        }
		} else { //其他系统页面
			window.location.reload();
		}
	} catch(e) { //其他系统页面
		window.location.reload();
	}
};

/**
 * 取得菜单列表
 */
ICBC.getMenuList = function() {
	var result = FireFly.doAct("SY_COMM_MENU", "getSingleMenuList", {}, false);
	return result.menuList;
}

/**
 * 自定义菜单是否显示(默认为显示)
 */
ICBC.showTopMenu = function() {
	return System.getVar("@C_SY_TOP_MENU_SHOW@") != "false";
}

/**
 * 右侧内容是否显示(默认为显示)
 */
ICBC.showRightContent = function() {
	return false;
//	ICBC.showTopMenu() && System.getVar("@C_PAGE_RIGHT_CONTENT_SHOW@") != "false";
}

/**
 * 取得菜单HTML
 */
ICBC.getMenuHtml = function() {
	var menuArr = ICBC.getMenuList();
	//隐藏掉首页header的红色栏
	var menuCon = jQuery('<div class="top_menu" style="display:none;">');//top_menu
	jQuery.each(menuArr, function(index, menu) {
		if(menu.EN_JSON){
			menu.NAME = Language.transDynamic("MENU_NAME", menu.EN_JSON, menu.NAME);
			menu.DSNAME = Language.transDynamic("DS_NAME", menu.EN_JSON, menu.DSNAME);
    	}
		var line = jQuery('<div class="line">');
		var obj = jQuery('<div class="item" menu_id="'+menu.ID+'">').text((menu.DSNAME || menu.NAME));
		obj.click(function() {
			//选中样式修改
			jQuery(".top_menu .item").removeClass("select");
			jQuery(this).addClass("select");
//			
//			//如果左侧菜单收起，自动展开
			if (jQuery("#left-homeMenu").hasClass("leftHide")) {
				jQuery(".leftMenu-close").click();
			}
//			
			if (menu.INFO) {
			    if (menu.TYPE == 1) { //服务
			    	opts = {"url":menu.INFO + ".list.do","tTitle":menu.NAME,"menuFlag":1,"menuId":menu.ID,"closeFlag":false};
					Tab.open(opts);
			    } else if (menu.TYPE == 2) {//链接
			    	var menuItem = {"ID":menu.ID,"NAME":menu.NAME,"INFO":menu.INFO,"MENU":1};
			    		Menu.linkNodeClick(menuItem);
			    } else if(menu.TYPE == 3) { //JS代码
			    	eval(menu.INFO);
			    }
			}
		});
		menuCon.append(obj).append(line);
	});
	return menuCon;
};

/**
 * 取得菜单HTML
 */
ICBC.openFirstTab = function() {
	jQuery(".top_menu .item:first").click();
};

/**
 * 取得菜单HTML
 */	
ICBC.getRightContentHtml = function() {
	//是否显示右侧模块
	if (!ICBC.showRightContent()) {
		return "";
	} else {
		var res = FireFly.doAct("SY_COMM_TEMPL","getPortalArea",{"PC_ID":"PAGE_RIGHT_CONTENT"},false);
		if (res && res.AREA) {
			return res.AREA;
		} else {
			return "";
		}
	}
};