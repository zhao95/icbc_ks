/**
 * 考试日历模块js
 */
var type = "kaoshi";
var flagObj = {
	"kaoshi":"rili",
	"huiyi":"rili",
	"geren":"rili"
};
var hasCreate = {
	"kaoshi":false,
	"huiyi":false,
	"geren":false
};
//页面加载时运行该函数
jQuery(document).ready(function(){
	//存放
	var eventArr = [];
	doPick($("[iiCode='kaoshi'] .TS_KS_CAL_picker"),type);
	jQuery(".title_002,.title_003,.title_004").unbind("click").click(function(){
		var iCode = jQuery(this).attr("iCode");
		type = iCode;
		jQuery(".title_002,.title_003,.title_004").removeClass("title_select");
		jQuery(this).addClass("title_select");
//		jQuery("[iiCode='kaoshi'],[iiCode='huiyi'],[iiCode='geren']").hide();
//		jQuery("[iiCode='"+type+"']").show();
		jQuery("[iiCode='kaoshi']").hide();
		jQuery("[iiCode='kaoshi']").show();
		
		if (!hasCreate[type]) {
			doPick($("[iiCode='"+type+"'] .TS_KS_CAL_picker"),type);
		}
	});
	//考试日历的按钮（‘更多>’）.点击之后跳转到考试日历的详细介绍页面
//	jQuery("div[icode='ksrl-header']").unbind("click").click(function(){
//		var url = FireFly.getContextPath() + "/qt/jsp/ksrl.jsp";
//		window.location.href = url;
//		});
	
	function doPick(target,type) {
		hasCreate[type] = true;
		eventArr = getMonthData("",type);

		
		
		
	    //获取所有的月份
		var picker = target.datepicker({
			inline: true,
			firstDay:0,
			showOtherMonths: true,
			selectOtherMonths: false,
			//汉化日历插件
			regional:$.datepicker.regional[ "zh-CN" ],
			//获取考试的内容和考试的时间
			beforeShowDay:function(date){
				
				//获取考试的时间
				var d = date.getTime();
				//获取考试的日期
				var day = date.getDate();
				//数组用来存放考试的名称
				var titleArr = [];
				//遍历了所有的元素， 查找到符合条件的考试内容机器考试时间
				jQuery.each(eventArr,function(i,n){
					if (n.CAL_TYPE && n.CAL_TYPE==2) {
						return [true,null,null];
					}
					//获取开始时间和结束时间
					var sTime = rhDate.stringToDate(n["START_DATE"].substring(0,10));
					var eTime = rhDate.stringToDate(n["END_DATE"].substring(0,10));
					
					if(d>=sTime.getTime() && d<=eTime.getTime()){
						//将考试的信息添加到之前创建好的数组中
						titleArr.push(n.CAL_NAME);
					}
				});
				
				//指定当前日期突出显示
				var showMM=new Date().getMonth();
		        var showDATE=new Date().getDate();
		        var showYYYY=new Date().getFullYear();
		        var formatDate=showYYYY+"-"+(showMM+1)+"-"+showDATE;//此处日期的格式化和speciald中的格式一样
		        var cMM = date.getMonth();
		        var cDate = date.getDate();
		        var cYYYY = date.getFullYear();
		        var currentDataformat = cYYYY+"-"+(cMM+1)+"-"+cDate;
		        //匹配参数的日期和当前日期
		        if(formatDate == currentDataformat){
		            //此处要返回一个数组，currentDayShow是添加样式的类
		            return [true,"currentDayShow",titleArr.join("<br>"),"select"];
		        }
				//如果存储考试信息的数组不为空，在每一行的考试后面添加换行标签
				if (titleArr.length > 0) {
					
					return [true,null,titleArr.join("<br>"),"select"];
				}
				return [true,null,null];
			},
			onSelect:function(data){
				
			},
			onChangeMonthYear:function(year, month, inst){
				 var date = new Date(year,month);
				 eventArr=getMonthData(date,type);
				 inst.addHtml = getLiebiao();
			}
		});
		picker.on("click",".ui-datepicker-title .item",function(event){
			jQuery(".ui-datepicker-title .item",picker).removeClass("item_select");
			jQuery(this).addClass("item_select");
			event.stopPropagation();
			
			var which = jQuery(this).attr("which");
			flagObj[type] = which;
			if (which == "liebiao") {
				jQuery("table.ui-datepicker-calendar",picker).hide();
				jQuery("div[content='liebiao']",picker).show();
			} else {
				jQuery("div[content='liebiao']",picker).hide();
				jQuery("table.ui-datepicker-calendar",picker).show();
			}
		});
		jQuery("div[content='liebiao']",picker).html(getLiebiao());
	}
	
//	<!--填充数据到列表-->
//	此时填充的是考试日历的列表的数据
	function getLiebiao() {
		
		var liebiaoArr = ["<table style='width:100%;line-height:30px;text-align:center;border:0;'><tr><td colspan='2' style='height:10px;line-height:10px;border:0;'>&nbsp;</td></tr>"];
		jQuery.each(eventArr,function(i,n){
			var name = n.CAL_NAME;
			var start = getDay(rhDate.stringToDate(n.START_DATE));
			var end = getDay(rhDate.stringToDate(n.END_DATE));
			var time = start+" - "+end;
			if (start == end) {
				time = start;
			}
			if (n.CAL_TYPE && n.CAL_TYPE==2) {
				time = n.CAL_MONTH.substring(5)+"月" || "";
			}
			//css样式,控制div内的字超出范围的省略号代替  overflow: hidden; text-overflow: ellipsis;
			liebiaoArr.push("<tr><td style='width:120px;border:0;text-align:center;padding-right:10px;'>"+time+"</td><td style='border:0;text-align:left;padding-right:4px;overflow: hidden; text-overflow: ellipsis;'>"+name+"</td></tr>");
//			liebiaoArr.push("<tr><td style='width:120px;border:0;text-align:center;padding-right:10px;'>"+time+"</td><td style='border:0;text-align:left;padding-right:4px;'>"+name+"</td></tr>");
		});
		if (eventArr.length == 0) {
			liebiaoArr.push("<tr><td colspan='2' style='border:0;'>暂无记录</td></tr>");
		}
		liebiaoArr.push("</table>");
	
		return liebiaoArr.join("");
	}
	
	
	function getMonthData(date,type){
		if(!date){
			date = new Date();
		} else {
//			<#-- 传入的date值月数是加1的，在这里去掉 -->
			date = new Date(date);
			date.setMonth(date.getMonth()-1);
		}
		
		var tDate = getDate(date);
		var userCode = System.getVar("@USER_CODE@");
		if (type == "kaoshi") {
			var params = {
				"_SELECT_":"CAL_ID,CAL_NAME,START_DATE,END_DATE,CAL_TYPE,CAL_MONTH",
				"_WHERE_":" and START_DATE like '"+tDate+"%' or END_DATE like '"+tDate+"%' or (CAL_TYPE=2 and CAL_MONTH='"+tDate+"')",
				"_ORDER_":"START_DATE,END_DATE"
			};
			var rtn = FireFly.doAct("TS_KS_CAL","query",params,false);
			return rtn._DATA_;
		} 
		return [];
	}
	
	function getDate(dateTime){
		var year = dateTime.getFullYear();
		var mon = dateTime.getMonth() + 1 + "";
		if (mon.length < 2) {
			mon = "0" + mon;
		}
		return year + "-" + mon;
	}
	
	function getDay(dateTime) {
		var mon = dateTime.getMonth() + 1 + "";
		if (mon.length < 2) {
			mon = "0" + mon;
		}
		
		var day = dateTime.getDate() + "";
		if (day.length < 2) {
			day = "0" + day;
		}
		
		return mon + "." + day;
	}
	$(document.body).append(div_ksrl);
	$(document.body).append(img_ksrl);
	
	
});
var div_ksrl = $("<div style='border:1.5px solid #ABAB99;display:none;width:200px;height:auto;position: absolute;background-color:#FFFFFF;top:10px;left:10px;font-size:12px;'></div>");
var img_ksrl = $("<img src='/ks/images/u205.png' style='position:absolute;display:none; '>");

function RYXX_ACA(obj){
	var title = $(obj).attr("titles");
	if(title != undefined){
		var lefts = $(obj).offset().left;
		var tops = $(obj).offset().top;
		var height = $(obj).height();
		var width = $(obj).width();
		div_ksrl.css({"top":tops + height + 19,"left":lefts - width});
		img_ksrl.css({"top":tops + height,"left":lefts});
		xfdiv(title);
	}
}
function RYXX_ACA_OUT(obj){
	div_ksrl.hide();
	img_ksrl.hide();
	
}
function xfdiv(title){
	div_ksrl.empty();
	div_ksrl.show();
	img_ksrl.show();
	
	var divtitle =  $("<div style='margin-left:5px;margin-top:4px;'>"+title+"</div>");
	div_ksrl.append(divtitle);

}