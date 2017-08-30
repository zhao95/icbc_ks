$(function (){

	//新建元素为页面要展示模块的数组，将之后查询到的考试项目挂接模块的数组与该数组进行比较，
	//若有相同元素，则存入项目所有进度的数组，并根据项目当前进度的数组的长度与项目所有进度的数组的长度进行公式计算，
	//即可得出当前项目的百分比，并将该百分比的值设置成页面的进度num,并改变对应进度条颜色
	//公式为        width = 当前项目进度数组的长度/项目所有进度数组的长度 *100 

	var pageAllMK = ["报名","审核","请假","考场安排","场次测算"];
	//定义状态map键值映射规则
	//定义一个map集合，（对象的形式），存储状态对应的颜色
	var typeMap ={
			"未开启":"c1",
			"已结束":"c3",
			"报名中":"c2",
			"审核中":"c2",
			"考前请假开放中":"c2",
			"考后请假开放中":"c2",
			"异地借考开放中":"c2",
			"未设置":"c1",
			"已设置":"c3"
	}
	//定义名称map键值映射规则
	var nameMap = {
		"报名":"jdtDivInner1",
		"审核":"jdtDivInner2",
		"考场安排":"jdtDivOut .divColor"
//		"异地借考":"jdtDivOut .divColor",
//		"请假":"jdtDivOut .divColor"
//		"场次测算":"jdtDivInner6",
//		"异地借考":"jdtDivInner7",
//		"考场安排":"jdtDivInner8"
	}
	//获取对应的色号
	function color_num (num){
		//定义颜色map键值映射规则
		var colorMap = {
			//未开启
			c1:"#27b9f8",
			//进行中
			c2:"#ff0000",
			//已结束/已设置
			c3:"#70c0b3"
		}
		return colorMap[num];
//		debugger;
	}
	
	//通过当前登录用户的用户信息，获取到用户报名的相关考试项目
	var param1 = {};
	//调用平台级的方法，使用系统变量获取到当前登录用户的人力资源编码
	//通过人力资源编码查询报名项目的服务对应的表
//	var CurrentUser_work_num=System.getUser("USER_WORK_NUM");
	
	//因审核暂时使用的是用户编码，所以此时暂时使用USER_CODE(后续要改)
	var CurrentUser_work_num=System.getUser("USER_CODE");
	param1["_extWhere"]= "and STR1='"+CurrentUser_work_num+"' AND INT1 ='1'";
	var resultUserAssociateXM = FireFly.doAct("TS_XMZT","query",param1);
	//上面结果的项目id获取到，再查询到对应的项目的挂接模块
	//此时获取到的是项目挂接模块的名称以逗号分割的字符串
	
	if(resultUserAssociateXM._DATA_.length != 0){
		var xm_id = resultUserAssociateXM._DATA_[0].DATA_ID;
//		debugger;
		
		//使用字符串的方法，将去除逗号的BM_GJ  模块合并成一条字符串， 并将字符串根据，进行分割， 得到分割后的数组。
		var param2 = {};
		param2["_extWhere"]="and XM_ID ='"+xm_id+"'";
		var resultXM = FireFly.doAct("TS_XMGL","query",param2);
		var xm_gj_str = resultXM._DATA_[0].XM_GJ;
		var ks_name=resultXM._DATA_[0].XM_NAME;
		//获取项目的进度百分数
		var jdtNum = resultXM._DATA_[0].XM_JD;
		if(jdtNum==undefined){
			jdtNum="8.8";
		}else{
		var jdeNumArr = jdtNum.split(".");
		jdtNum = jdeNumArr[0]+"0.0";
		}
		//此时数组存储的是内容为查询到的挂接模块的名称
		var mkArray = xm_gj_str.split(",");
		//创建存储当前项目所有进度的数组
		var xmzt_arr_all = [];
		//遍历查询到的挂接模块的数组，并将之与页面显示总模块对比，符合条件的数组元素添加到xmzt_arr_all数组中。
		for (var i = 0; i < mkArray.length; i++) {
			//判断当前浏览器是否支持indexof方法，如果不支持，则扩展该方法
			if (!Array.indexOf) {  
			    Array.prototype.indexOf = function (obj) {  
			        for (var i = 0; i < this.length; i++) {  
			            if (this[i] == obj) {  
			                return i;  
			            }  
			        }  
			        return -1;  
			    }  
			} 
			//判断当前元素是否存在于页面模块的数组
			if(pageAllMK.indexOf(mkArray[i])>=0){
				//将该元素添加到项目所有模块的数组中的最后一位，并返回该数组的长度（此时长度暂未用到）
				var xmzt_arr_all_length=xmzt_arr_all.push(mkArray[i]); 
			}
		}
		
		//此时需要获取项目的当前进度模块，需要获取项目的挂接模块的状态。将获取到的状态进行分析，
		//如果未开始，则表示项目未开始，如果开放中，则表示项目开始中，如果已结束，则表示项目已结束
		//将已完成和进行中的项目模块放进CurrentXMGJ数组中,作为当前项目进度数组， 留待与总模块通过公式计算当前项目百分比
		//之后对三种状态的模块对应的进度条变色
		var param3={};
		//传递什么参数？当前项目的xm_id? xm_id怎么获取？ 仍旧是最初的项目id。
		//此时 TS_XMGL_BM的XM_ID和TS_XMGL_SZ的XM_ID是同一ID
		param3["_extWhere"] = "and xm_id='"+xm_id+"'"
		//获取到的是项目设置中的所有挂接模块的列表
		var current_xm = FireFly.doAct("TS_XMGL_SZ","query",param3);
//		debugger;
		
		
		
		//新建数组存储已执行的模块的名称，留待判断百分比用
		var CurrentXMGJ =[];
		//遍历列表的数据，获取每一个挂接模块的名称和状态
		for (var i = 0; i < current_xm._DATA_.length; i++) {
			//获取该模块的名称
			var currentN = current_xm._DATA_[i].XM_SZ_NAME;
			//获取该模块的状态
			var currentT = current_xm._DATA_[i].XM_SZ_TYPE;
			//获取到模块对应的状态和颜色的值
			var currentDiv =nameMap[currentN];
			var currentColor = typeMap[currentT];
			//如果当前模块的状态属于已完成或者进行中， 则将该模块存储进CurrentXMGJ数组
			if(currentColor==="c2" || currentColor ==="c3"){
				CurrentXMGJ.push(current_xm._DATA_[i]);
			}
			
			//将模块对应的状态和颜色设置对应的进度条样式颜色
			jQuery("#"+currentDiv).css("background-color",color_num(currentColor));
			if(currentN==="请假" && currentColor ==="c2"){
				jQuery("#qj_sp").css("color","#ff0000");
			}
			if(currentN==="异地借考"  && currentColor ==="c2"){
				jQuery("#jk_sp").css("color","#ff0000");
			}
//			debugger;
		}

		//设置进度值为百分之xx
//		var jdtNum = CurrentXMGJ.length / xmzt_arr_all.length * 100;
//		debugger;
		$("#jdtNum").html(""+jdtNum+"");
		$("#jdtName").html(""+ks_name+"");
	}
	else{
		//如果未设置首页展示的项目，则查询审核通过的表添加到首页展示
		var paramSH={};
		paramSH["_extWhere"]= "and BM_CODE='"+CurrentUser_work_num+"'";
		var resultSH = FireFly.doAct("TS_BMSH_PASS","query",paramSH);
		//如果没有报名考试， 则显示提示信息到页面
		if(resultSH._DATA_.length == 0){
			$("#jdtNum").html("0.0");
			$("#jdtName").html("您暂时未参加任何考试！");
		}else{
			var menuXM_ID = resultSH._DATA_[0].XM_ID;
			//使用字符串的方法，将去除逗号的BM_GJ  模块合并成一条字符串， 并将字符串根据，进行分割， 得到分割后的数组。
			var param2 = {};
			param2["_extWhere"]="and XM_ID ='"+menuXM_ID+"'";
			var resultXM = FireFly.doAct("TS_XMGL","query",param2);
			var xm_gj_str = resultXM._DATA_[0].XM_GJ;
			var ks_name=resultXM._DATA_[0].XM_NAME;
			//获取项目的进度百分数
			var jdtNum = resultXM._DATA_[0].XM_JD;
			if(jdtNum==undefined){
				jdtNum="8.8";
			}else{
			var jdeNumArr = jdtNum.split(".");
			jdtNum = jdeNumArr[0]+"0.0";
			}
			//此时数组存储的是内容为查询到的挂接模块的名称
			var mkArray = xm_gj_str.split(",");
			//创建存储当前项目所有进度的数组
			var xmzt_arr_all = [];
			//遍历查询到的挂接模块的数组，并将之与页面显示总模块对比，符合条件的数组元素添加到xmzt_arr_all数组中。
			for (var i = 0; i < mkArray.length; i++) {
				//判断当前浏览器是否支持indexof方法，如果不支持，则扩展该方法
				if (!Array.indexOf) {  
				    Array.prototype.indexOf = function (obj) {  
				        for (var i = 0; i < this.length; i++) {  
				            if (this[i] == obj) {  
				                return i;  
				            }  
				        }  
				        return -1;  
				    }  
				} 
				//判断当前元素是否存在于页面模块的数组
				if(pageAllMK.indexOf(mkArray[i])>=0){
					//将该元素添加到项目所有模块的数组中的最后一位，并返回该数组的长度（此时长度暂未用到）
					var xmzt_arr_all_length=xmzt_arr_all.push(mkArray[i]); 
				}
			}
			//此时需要获取项目的当前进度模块，需要获取项目的挂接模块的状态。将获取到的状态进行分析，
			//如果未开始，则表示项目未开始，如果开放中，则表示项目开始中，如果已结束，则表示项目已结束
			//将已完成和进行中的项目模块放进CurrentXMGJ数组中,作为当前项目进度数组， 留待与总模块通过公式计算当前项目百分比
			//之后对三种状态的模块对应的进度条变色
			var param3={};
			//传递什么参数？当前项目的xm_id? xm_id怎么获取？ 仍旧是最初的项目id。
			//此时 TS_XMGL_BM的XM_ID和TS_XMGL_SZ的XM_ID是同一ID
			param3["_extWhere"] = "and xm_id='"+menuXM_ID+"'"
			//获取到的是项目设置中的所有挂接模块的列表
			var current_xm = FireFly.doAct("TS_XMGL_SZ","query",param3);
//			debugger;
			
			//新建数组存储已执行的模块的名称，留待判断百分比用
			var CurrentXMGJ =[];
			//遍历列表的数据，获取每一个挂接模块的名称和状态
			for (var i = 0; i < current_xm._DATA_.length; i++) {
				//获取该模块的名称
				var currentN = current_xm._DATA_[i].XM_SZ_NAME;
				//获取该模块的状态
				var currentT = current_xm._DATA_[i].XM_SZ_TYPE;
				//获取到模块对应的状态和颜色的值
				var currentDiv =nameMap[currentN];
				var currentColor = typeMap[currentT];
				//如果当前模块的状态属于已完成或者进行中， 则将该模块存储进CurrentXMGJ数组
				if(currentColor==="c2" || currentColor ==="c3"){
					CurrentXMGJ.push(current_xm._DATA_[i]);
				}
				
				//将模块对应的状态和颜色设置对应的进度条样式颜色
				jQuery("#"+currentDiv).css("background-color",color_num(currentColor));
				if(currentN==="请假" && currentColor ==="c2"){
					jQuery("#qj_sp").css("color","#ff0000");
				}
				if(currentN==="异地借考"  && currentColor ==="c2"){
					jQuery("#jk_sp").css("color","#ff0000");
				}
//				debugger;
			}

			//设置进度值为百分之xx
//			var jdtNum = CurrentXMGJ.length / xmzt_arr_all.length * 100;
//			debugger;
			$("#jdtNum").html(""+jdtNum+"");
			$("#jdtName").html(""+ks_name+"");
		}
	}

	
	//点击首页模块状态的更多跳转到详细页面。
	$("#jdtMore").unbind("click").click(function(){
		var url = FireFly.getContextPath() + "/qt/jsp/xmzt.jsp";
		window.location.href = url;
	});
	//此时使用js控制动态颜色的改变，因为CSS的加载顺序比JS早，
	//而进度条的颜色是根据js获取到的，故此使用js控制鼠标移入移出的颜色变化。
	//鼠标移入移出事件,改变进度条的颜色
	var divOverColor = "";
	//进度条移入移出变色
	$("#jdtDivOut .divColor").mouseover(function (){
		divOverColor = $("#jdtDivOut .divColor").css("background-color");
		$("#jdtDivOut .divColor").css("background-color","#fad09e");
		}).mouseout(function (){  
		$("#jdtDivOut .divColor").css("background-color",divOverColor);
    });  
	$("#jdtDivInner1").mouseover(function (){
		divOverColor = $("#jdtDivInner1").css("background-color");
		$("#jdtDivInner1").css("background-color","#fad09e");
	}).mouseout(function (){  
		$("#jdtDivInner1").css("background-color",divOverColor);
	});  
	$("#jdtDivInner2").mouseover(function (){
		divOverColor = $("#jdtDivInner2").css("background-color");
		$("#jdtDivInner2").css("background-color","#fad09e");
	}).mouseout(function (){  
		$("#jdtDivInner2").css("background-color",divOverColor);
	});  
	
	
})