
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
//	var perImg1 = FireFly.getContextPath() +""+ System.getUser("USER_IMG_SRC");
	var perImg2 = FireFly.getContextPath() + System.getUser("USER_IMG");
//	debugger;
	$("#userInfo2").append("<span class='hidden-xs' style='font-family: '黑体 Bold', '黑体 Regular', '黑体';font-weight: 700;font-style: normal;font-size: 16px;color: #FFFFFF;text-align: center;'>" + userName + "</span>");

	$("#userInfo2").append("<span class='hidden-xs' style='margin-left:10px;'><i class='fa fa-chevron-circle-down'></i></span>");
	if(perImg2==""){
		//	写死头像图片
		$("#userImg1").attr('src',"/sy/theme/default/images/common/user_64_64.png"); 
		$("#userImg2").attr('src',"/sy/theme/default/images/common/user0.png"); 
	}else{
	$("#userImg1").attr('src',perImg2); 
	$("#userImg2").attr('src',perImg2); 
	}
	$("#userImg3").attr('src',perImg2);  
}

/**
 * 菜单列表
 */
function showMenu() {
	var data = FireFly.doAct("TS_UTIL", "getMenu", {"S_FLAG":1});
	 var param = {};
     param["user_code"]=System.getVar("@USER_CODE@");
     param["zhuangtai"]="全部";
     param["shownum"] =10;
     param["where"]= "";
     param["nowpage"]= 1;
     var act = FireFly.doAct("TS_XMGL", "getUncheckList", param);
     var list = act.alllist;
     //查询是否有待审核的数据
	if (data.menuList.length > 0) {
		for (var i = 0; i < data.menuList.length; i++) {
//			debugger;
			  if(list.length==0&&data.menuList[i].MENU_NAME=="报名审核"){
			    	 //不显示
				  continue;
			     }
			  if(data.menuList[i].MENU_NAME=="报名审核"){
				  //报名审核 数据  是否有 待审核的数据
				  var xmids = "";
				  for(var j=0;j<list.length;j++){
					  xmids+=list[j].XM_ID+",";
				  }
				  var paramstay = {};
				  paramstay["ids"]=xmids;
				 var result =  FireFly.doAct("TS_BMSH_STAY","getStayList",paramstay);
				  if(result.flag!="true"){
					  //有待审核的数据
					  $(".sidebar-menu")
						.append(
								'<li><a href="'+data.menuList[i].MENU_URL+'" target="blank"><i class="'+data.menuList[i].MENU_IMG+'"></i> <span>'
										+ data.menuList[i].MENU_NAME
										+ '</span></a></li>');
				  }else{
					 var num =  result.num
					  //有需要 审核的数据  <span class="label label-warning" id="tipSum">0</span>
					  $(".sidebar-menu")
						.append(
								'<li><a href="'+data.menuList[i].MENU_URL+'" target="blank"><i class="'+data.menuList[i].MENU_IMG+'"></i> <span>'
										+ data.menuList[i].MENU_NAME
										+ '</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="label label-warning" id="tipSum">'+num+'</span></a></li>');
				  }
			  }else{
				  
				  $(".sidebar-menu")
				  .append(
						  '<li><a href="'+data.menuList[i].MENU_URL+'" target="blank"><i class="'+data.menuList[i].MENU_IMG+'"></i> <span>'
						  + data.menuList[i].MENU_NAME
						  + '</span></a></li>');
			  }
		}
	}
}

