$(function (){
	initUserInfoPage();
});


function initUserInfoPage(user_code){
	var user_state_type={
			"1":"在职",
			"2":"离职",
			"3":"退休"
	}
	var aaa=jQuery(".content-wrapper").css("min-height","900px");
	var user_code= System.getUser("USER_CODE");
	var userParam1 ={};
	userParam1["_extWhere"] = "and DATA_ID ='"+user_code+"' and SERV_ID='TS_BMLB_BM'";
	var ts_obj_result=FireFly.doAct("TS_OBJECT","query",userParam1);
	var ryl_mobile="暂无数据";
	if(ts_obj_result._DATA_.length!=0){
		ryl_mobile=ts_obj_result._DATA_[0].STR1;
	}
	var userParam ={};
//	var resultUserInfo = FireFly.doAct("SY_ORG_USER","query",userParam);
	var resultUserInfo = FireFly.doAct("SY_ORG_USER_INFO_SELF","query",userParam);
	var result = resultUserInfo._DATA_[0];
	var img_src = FireFly.getContextPath() + System.getUser("USER_IMG");
	if(img_src==""){
		$("#user_photo").attr("src","/qt/img/u844.jpg");
	}else{
		$("#user_photo").attr("src",img_src);
	}
	//给页面每一个对应的元素赋值
	for(var i in result){
		if(result[i]==""){
			result[i]="暂无数据";
		}
	}
	jQuery("#USER_NAME").html(result.USER_NAME);
	jQuery("#USER_CODE").html(result.USER_CODE);
	jQuery("#USER_LOGIN_NAME").html(result.USER_LOGIN_NAME);
	jQuery("#CMPY_CODE").html(result.CMPY_CODE);
	jQuery("#DEPT_CODE").html(result.DEPT_CODE__NAME);
	jQuery("#USER_OFFICE_PHONE").html(result.USER_OFFICE_PHONE);
	jQuery("#USER_MOBILE").html(result.USER_MOBILE);
	jQuery("#RYL_MOBILE").html(ryl_mobile);
	jQuery("#USER_EMAIL").html(result.USER_EMAIL);
	jQuery("#USER_WORK_LOC").html(result.USER_WORK_LOC);
	jQuery("#USER_POST").html(result.USER_POST);
	jQuery("#USER_POST_LEVEL").html(result.USER_POST_LEVEL);
	jQuery("#USER_WORK_NUM").html(result.USER_WORK_NUM);
	jQuery("#USER_IDCARD").html(result.USER_IDCARD);
	jQuery("#USER_BIRTHDAY").html(result.USER_BIRTHDAY);
	jQuery("#USER_NATION").html(result.USER_NATION);
	jQuery("#USER_SEX").html(result.USER_SEX__NAME);
	jQuery("#USER_HOME_LAND").html(result.USER_HOME_LAND__NAME);
	jQuery("#USER_POLITICS").html(result.USER_POLITICS);
	jQuery("#USER_EDU_LEVLE").html(result.USER_EDU_LEVLE);
	jQuery("#USER_EDU_SCHOOL").html(result.USER_EDU_SCHOOL);
	jQuery("#USER_EDU_MAJOR").html(result.USER_EDU_MAJOR);
	jQuery("#USER_TITLE").html(result.USER_TITLE);
	jQuery("#USER_TITLE_DATE").html(result.USER_TITLE_DATE);
	jQuery("#USER_WORK_DATE").html(result.USER_WORK_DATE);
	jQuery("#USER_CMPY_DATE").html(result.USER_CMPY_DATE);
	jQuery("#USER_STATE").html(user_state_type[result.USER_STATE]);
}
