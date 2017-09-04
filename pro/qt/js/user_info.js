$(function (){
	var a= getParam("USER_CODE");
	var user_code = a["USER_CODE"];
	debugger;
	initUserInfoPage(user_code);
});

function initUserInfoPage(user_code){
	var user_work_code = user_code;
	debugger;
	var userParam ={};
	userParam["_extWhere"] = "and USER_CODE ='"+user_work_code+"'";
	var resultUserInfo = FireFly.doAct("SY_ORG_USER_CENTER","query",userParam); 
	jQuery("#tbody_data").append(
			[
			 '<tr style="backGround-color:WhiteSmoke; height: 30px; font-size:14px;">',
								'<th style="width: 6%; text-align: center">用户编码</th>',
								'<th style="width: 30%;	text-align: center">考试名称</th>',
								'<th style="width: 10%;	text-align: center">考试级别</th>',
								'<th style="width: 10%; text-align: center">开始时间</th>',
								'<th style="width: 10%; text-align: center">结束时间</th>',
								'<th style="width: 14%; text-align: center">备注</th>',
								'<th style="width: 8%; text-align: center">考试月份</th>',
							'</tr>'].join("")
	);
}
//获取页面传递参数的方法
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