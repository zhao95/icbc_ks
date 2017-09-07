$(function (){
	initUserInfoPage();
});

function initUserInfoPage(user_code){
	var user_code= System.getUser("USER_CODE");
	debugger;
	var userParam ={};
	userParam["_extWhere"] = "and USER_CODE ='"+user_code+"'";
	var resultUserInfo = FireFly.doAct("SY_ORG_USER","query",userParam);
	var result = resultUserInfo._DATA_[0];
	var img1 = resultUserInfo._DATA_[0].USER_CODE__IMG;
	var img_src = FireFly.getContextPath() + System.getUser("USER_IMG_SRC");
	$("#user_photo").attr("src",img_src);
//	jQuery("#main-left").append(
//			[
//			'<tr style="backGround-color:WhiteSmoke; height: 30px; font-size:14px;">',
//			'<th style="width: 6%; text-align: center">用户编码</th>',
//			'<th style="width: 30%;	text-align: center">考试名称</th>',
//			'<th style="width: 10%;	text-align: center">考试级别</th>',
//			'<th style="width: 10%; text-align: center">开始时间</th>',
//			'<th style="width: 10%; text-align: center">结束时间</th>',
//			'<th style="width: 14%; text-align: center">备注</th>',
//			'<th style="width: 8%; text-align: center">考试月份</th>',
//		'</tr>'].join("")
//	);
	jQuery("#user_info_div").append(
			[
			 '<div style="backGround-color:WhiteSmoke; height: 30px; font-size:14px;">',
				'<div style="display:inline-block;width: 30%; text-align: center;background-color:#ffffff; ">用户信息</div>',
				'<div style="display:inline-block;width: 60%; text-align: left;padding:">'+result.USER_NAME+'</div>',
			'</div>',
			'<div style="backGround-color:WhiteSmoke; height: 30px; font-size:14px;">',
				'<div style="display:inline-block; width: 30%;text-align: center">用户手机</div>',
				'<div style="display:inline-block; width: 60%;text-align: left">'+result.USER_MOBILE+'</div>',
			'</div>',
			'<div style="backGround-color:WhiteSmoke; height: 30px; font-size:14px;">',
				'<div style="display:inline-block; width: 30%;text-align: center">用户机构</div>',
				'<div style="display:inline-block; width: 60%;text-align: left">'+result.DEPT_CODE__NAME+'</div>',
			'</div>'
			].join("")
	);
}



//获取get方式页面传递参数的方法
function getParam() {
    var url = location.search; //获取url中"?"符后的字串
    var theRequest = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = decodeURIComponent(strs[i].split("=")[1]);
        }
    }
    return theRequest;
}