$(function () {
    $(".wrapper").css("background-color", "#064d78");
    //$(".main-sidebar").css("background-color", "#064d78");
    $(".main-header").css("background-color", "white");
    setTimeout(function () {
        userInfo();
        showMenu();
    });
});

/**
 * 用户信息
 */
function userInfo() {
    var userName = System.getVar("@USER_NAME@");
    var odeptName = System.getVar("@ODEPT_NAME@");

    var deptName = System.getVar("@DEPT_NAME@");
    var deptCode = System.getVar("@DEPT_CODE@");

    var deptNameM = System.getVar("@DEPT_NAME_M@");
    var deptCodeM = System.getVar("@DEPT_CODE_M@");

    var deptCSecond = System.getVar("@DEPT_CODES_SECOND@");
    var deptNSecond = System.getVar("@DEPT_NAMES_SECOND@");

    $("#userInfo").append("<div>" + userName + "</div>").append(
        "<div>" + odeptName + "</div>");
//	var perImg1 = FireFly.getContextPath() +""+ System.getUser("USER_IMG_SRC");
    var perImg2 = FireFly.getContextPath() + System.getUser("USER_IMG");
//	debugger;
    var $userInfo2 = $("#userInfo2");
    $userInfo2.append("<span class='hidden-xs' style='font-family: '黑体 Bold', '黑体 Regular', '黑体';font-weight: 700;font-style: normal;font-size: 16px;color: #FFFFFF;text-align: center;'>" + userName + "</span>");

    $userInfo2.append("<span class='hidden-xs' style='margin-left:10px;'><i class='fa fa-chevron-circle-down'></i></span>");
    if (perImg2 === "") {
        //	写死头像图片
        $("#userImg1").attr('src', "/sy/theme/default/images/common/user_64_64.png");
        $("#userImg2").attr('src', "/sy/theme/default/images/common/user0.png");
    } else {
        $("#userImg1").attr('src', perImg2);
        $("#userImg2").attr('src', perImg2);
    }
    $("#userImg3").attr('src', perImg2);

    //多机构用户切换身份
    if (deptCSecond.length > 0) {

        $("#mulitLi").css({"display": "block"});
        var $mulitDepts = $("#mulitDepts");
        $mulitDepts.append("<span class='hidden-xs' style='font-family: '黑体 Bold', '黑体 Regular', '黑体';font-weight: 700;font-style: normal;font-size: 16px;color: #FFFFFF;text-align: center;'>" + deptName + "</span>");
        $mulitDepts.append("<span class='hidden-xs' style='margin-left:10px;'><i class='fa fa-chevron-circle-down'></i></span>");

        var deptCArg = deptCSecond.split(",");
        var deptNArg = deptNSecond.split(",");

        console.log("DEPT_CODE", deptCode);
        console.log("DEPT_NAME", deptName);
        console.log("DEPT_CODE_M", deptCodeM);
        console.log("DEPT_NAME_M", deptNameM);
        console.log("DEPT_CODES_SECOND", deptCArg);
        console.log("DEPT_NAMES_SECOND", deptNArg);

        if (deptCode !== deptCodeM) {
            $("#mulitList").append("<li><a href='#' id='" + deptCodeM + "'><i class='fa fa-user-o fa-fw'></i>" + deptNameM + "</a></li>");

            $("#" + deptCodeM).unbind("click").bind("click", function () {

                FireFly.doAct("TS_COMM_USER_RELOGIN", "relogin", {"DEPT_CODE": $(this).attr("id")}, true, false, function (data) {
                    location.reload();
                });
            });
        }

        for (var dn in deptNArg) {

            if (deptCode !== deptCArg[dn]) {

                $("#mulitList").append("<li><a href='#' id='" + deptCArg[dn] + "'><i class='fa fa-user-o fa-fw'></i>" + deptNArg[dn] + "</a></li>");

                $("#" + deptCArg[dn]).unbind("click").bind("click", function () {

                    FireFly.doAct("TS_COMM_USER_RELOGIN", "relogin", {"DEPT_CODE": $(this).attr("id")}, true, false, function (data) {
                        location.reload();
                    });
                });
            }
        }
    }
}

/**
 * 菜单列表
 */
function showMenu() {
    //是否有可用准考证
    var printBean = FireFly.doAct("TS_XMGL_ADMISSION_FILE", 'getHasCanPrintAdmission', {});

    var data = FireFly.doAct("TS_UTIL", "getMenu", {"S_FLAG": 1});
    var result = FireFly.doAct("TS_BMLB_BM", "lookXn", "");//是否可见辖内报名
    var lookflag = result.look;
    var param = {};
    param["user_code"] = System.getVar("@USER_CODE@");
    param["zhuangtai"] = "全部";
    param["shownum"] = 10;
    param["where"] = "";
    param["nowpage"] = 1;
    var act = FireFly.doAct("TS_XMGL", "getMyShState", param);
    //判断此人是否可进行审核
    var flagstr = act.flag;
    //查询是否有待审核的数据
    if (data.menuList.length > 0) {
        for (var i = 0; i < data.menuList.length; i++) {
            var $menuItem;
            var MENU_URL = "'" + data.menuList[i].MENU_URL + "'";
            if (flagstr === "false" && data.menuList[i].MENU_NAME === "报名审核") {
                //不显示
                continue;
            }
            if (data.menuList[i].MENU_NAME === "报名审核") {
                var menu_url = MENU_URL;
                //报名审核 数据  是否有 待审核的数据
                /*  var xmids = "";
                 for(var j=0;j<list.length;j++){
                 xmids+=list[j].XM_ID+",";
                 }*/
                var paramstay = {};
                var MENU_IMG = data.menuList[i].MENU_IMG;
                var MENU_NAME = data.menuList[i].MENU_NAME;
                FireFly.doAct("TS_BMSH_STAY", "getStayList", "", true, true, function (result) {

                    if (result.flag !== "true") {
                        //有待审核的数据
                        $(".sidebar-menu")
                            .append(
                                '<li><a href="#" onclick="window.open(' + menu_url + ')"><i class="' + MENU_IMG + '"></i> <span>'
                                + MENU_NAME
                                + '</span></a></li>');
                    } else {
                        var num = result.num;
                        //有需要 审核的数据  <span class="label label-warning" id="tipSum">0</span>
                        $(".sidebar-menu")
                            .append(
                                '<li><a href="#" onclick="window.open(' + menu_url + ')"><i class="' + MENU_IMG + '"></i> <span>'
                                + MENU_NAME
                                + '</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="label label-warning" id="tipSum">' + num + '</span></a></li>');
                    }
                });

            } else if (data.menuList[i].MENU_NAME === "查看辖内报名" && lookflag === "false") {
                continue;
            } else if (data.menuList[i].MENU_NAME === "首页") {

                $(".sidebar-menu")
                    .append(
                        '<li><a href="' + data.menuList[i].MENU_URL + '"><i class="' + data.menuList[i].MENU_IMG + '"></i> <span>'
                        + data.menuList[i].MENU_NAME
                        + '</span></a></li>');
            } else {
                $menuItem = jQuery(
                    '<li><a href="#" onclick="window.open(' + MENU_URL + ')"><i class="' + data.menuList[i].MENU_IMG + '"></i> <span>'
                    + data.menuList[i].MENU_NAME
                    + '</span></a></li>');
                $(".sidebar-menu")
                    .append($menuItem);
            }

            if (data.menuList[i].MENU_NAME === "我的准考证") {
                //有可打印准考证 却还未打印
                if (printBean.hasPrint === 'true') {
                    $menuItem.find('a').append('<i style="margin-left: 5px;" class="fa fa-circle text-yellow"></i>');
                }
            }

            if (data.menuList[i].MENU_NAME === "我的请假") {
                var listBean = FireFly.doAct('TS_QJLB_QJ', 'getUserCanLeaveXmList', {});
                if (listBean._DATA_.length !== 0) {
                    $menuItem.find('a').append('<i style="margin-left: 5px;" class="fa fa-circle text-yellow"></i>');
                }
            }

        }
    }
}

