var _viewer = this;
$("#TS_XMGL_CCCS_2 .rhGrid").find("tr").unbind("dblclick");
var xmId = _viewer.getParHandler().getPKCode();

$("#TS_XMGL_CCCS_2-expNo").hide();
$("#mytable").remove();

_viewer.getBtn("config").unbind("click").bind("click", function(event) {
	var temp = {
		"act" : UIConst.ACT_CARD_ADD,
		"sId" : "TS_XMGL_CCCS_V_CONF",
		"parHandler" : _viewer,
		"widHeiArray" : [ 600, 100 ],
		"xyArray" : [ 300, 300 ]
	};
	var cardView = new rh.vi.cardView(temp);
	cardView.show();
});

var scVal = Cookie.get("scVal");
var sjVal = Cookie.get("sjVal");
var cjVal = Cookie.get("cjVal");// 层级
var runVal = Cookie.get("runVal");

_viewer.notInArray = new Array();

if(runVal == "1"){
	Cookie.set("runVal", 0, 1);
	run();
}

_viewer.getBtn("run").unbind("click").bind("click", function(event) {
	if(scVal == "" || sjVal == "" || cjVal == "" || scVal == null || sjVal == null || cjVal == null){
		alert("请设置测算条件");
		return false;
	}
	run();
});

function run(){
	_viewer.refreshGrid();
	var allArray = new Array();
	var myloadbar = new rh.ui.loadbar({"id":"my-loadbar"});
	myloadbar.show();
	//符合计算条件的考生
	var all = new Array();
	//不符合计算条件的考生
	var non = new Array();
	setTimeout(function(){
		FireFly.doAct("TS_XMGL_CCCS_2","getKsList", {"xmId":xmId,"sjVal":sjVal,"cjVal":cjVal},true,false,function(data){
			all = data.all;
			var nonTmp = data.nonKsList;
			for(var i=0;i<nonTmp.length;i++){
				nonTmp[i].CAUSE = "考试时长不符合";
			}
			non = nonTmp;
		});
		var result_good  = getResult(1,all);
		var result_max = getResult(2,all);
		
		var x1 = 0;
		var sum = 0;
		//最大场次考场ID
		var maxDataId = "";
		//最大场次数
		var maxValue = 0;
		$("#TS_XMGL_CCCS_2,#JColResizer2").find("tr").each(function(index, item) {
			if (index != 0) {
				var dataId = item.id;
				if(dataId.length > 0){
					sum++;
					var goodCCNum = result_good.total[dataId].length;
					var maxCCNum = result_max.total[dataId].length;
					var sumNum = 0;
					for(var i=0;i<goodCCNum;i++){
						var tmpNum = result_good.total[dataId][i].length;
						sumNum += tmpNum;
					}
					var sumNum2 = 0;
					for(var i=0;i<maxCCNum;i++){
						var tmpNum = result_max.total[dataId][i].length;
						sumNum2 += tmpNum;
					}
					var kcName = $(item).find("td[icode='KC_NAME']").html();
					var kcGood = $(item).find("td[icode='KC_GOOD']").html();
					var kcMax = $(item).find("td[icode='KC_MAX']").html();
					$(item).find("td[icode='NUM_PEOPLE']").html(sumNum);
					$(item).find("td[icode='NUM_GOOD']").html(goodCCNum);
					$(item).find("td[icode='NUM_PEOPLE2']").html(sumNum2);
					$(item).find("td[icode='NUM_MAX']").html(maxCCNum);
					//场次数大于10的考场
					if(goodCCNum > 10 || maxCCNum > 10){
						$(item).css("background","#AAAAAA");
						x1++;
					}
					//场次最大的考场
					if(goodCCNum > maxValue){
						maxDataId = dataId;
						maxValue = goodCCNum;
					}
					var tmpBean = {};
					tmpBean["KC_ID"] = dataId;
					tmpBean["KC_NAME"] = kcName;
					tmpBean["KC_GOOD"] = kcGood;
					tmpBean["KC_MAX"] = kcMax;
					tmpBean["NUM_PEOPLE"] = sumNum;
					tmpBean["NUM_GOOD"] = goodCCNum;
					tmpBean["NUM_MAX"] = maxCCNum;
					allArray.push(tmpBean);
				}
			}
		});
		
		if(maxDataId != ""){
			$("#TS_XMGL_CCCS_2").find("tr[id='"+maxDataId+"']").find("td[icode='KC_NAME']").css("color","#ff6200");
		}
		var str = JSON.stringify(allArray); 
		var expData = {};
		expData["EXP_STR"] = str;
		expData["XM_ID"] = xmId;
		expData["TYPE"] = 1;
		var userCode = System.getVar("@USER_CODE@");
		//FireFly.doAct("TS_XMGL_CCCS_EXP","delete",{"_WHERE_":"and s_user = '"+userCode+"' and xm_id='"+xmId+"'"},false,true);
		FireFly.doAct("TS_XMGL_CCCS_EXP","save",expData,false);
		
		var percent1 = 0;
		if(x1 > 0 && sum > 0){
			percent1 = Math.round(x1*100/sum)
		}
		var fx = "分析:超过10场的考场有"+x1+"个，占比"+percent1+"%。说明：背景色为深色行为场次数大于10场，红色考场名称表示当前考场场次数最大。";
		$("#TS_XMGL_CCCS_2").find("table[id='JColResizer2']").find(".rhGrid-tbody").append("<tr class='tBody-tr'><td colspan=9 class='rhGrid-td-left'>"+fx+"</td></tr>")
		
		for(var i=0;i<result_good.all.length;i++){
			result_good.all[i].CAUSE = "关联机构不符合";
		}
		notInArray = non.concat(result_good.all);
//		_viewer.notInArray = non.concat(result_good.all);
		notInTable(notInArray);
		var str2 = JSON.stringify(notInArray); 
		var expData2 = {};
		expData2["EXP_STR"] = str2;
		expData2["XM_ID"] = xmId;
		expData2["TYPE"] = 2;
		//FireFly.doAct("TS_XMGL_CCCS_EXP","delete",{"_WHERE_":"and s_user = '"+userCode+"' and xm_id='"+xmId+"'"},false,true);
		FireFly.doAct("TS_XMGL_CCCS_EXP","save",expData2,false);
	},100);
	myloadbar.hideDelayed();	
}

function getResult(num,all){
	//num 1最优场次 2.最大场次
	var res = {};
	if (scVal != "" && scVal != null) {
		var total = {};
		var kcGljg = {};
		var stopKcArr = new Array();
		
		if(cjVal == 1){
			var kcArr1 = FireFly.doAct("TS_XMGL_CCCS_2","query",{"_WHERE_":"and XM_ID = '"+xmId+"' and KC_LEVEL = '一级'"})._DATA_;
			//是否再次计算
			var countFlag1 = true;
			while(countFlag1){
				countFlag1 = false;
				for(var i=0;i<kcArr1.length;i++){
					var kcBean = kcArr1[i];
					var kcId = kcBean.KC_ID;
					var kcGood = kcBean.KC_GOOD;
					var kcMax = kcBean.KC_MAX;
					var kcZwNum = 0;
					if(num == 1){
						kcZwNum = kcGood;
					}else{
						kcZwNum = kcMax;
					}
					var kcJgArr = new Array();
					if(stopKcArr.includes(kcId)){
						continue;
					}
					//得到考场的关联机构
					if(kcGljg[kcId] == undefined || kcGljg[kcId] == ""){
						kcJgArr = FireFly.doAct("TS_KCGL_GLJG_V","finds", {"_SELECT_":"JG_CODE,jg_type,code_path","_WHERE_":"and KC_ID = '"+kcId+"'"})._DATA_;
						kcGljg[kcId] = kcJgArr;
					}else{
						kcJgArr = kcGljg[kcId];
					}
					
					//本场次本考场分配的考生
					var tmp1 = new Array();
					//已分配的考生人员 用于从all人员中删除，取得剩余人员
					var rmArr = new Array();
					for(var j=0;j<all.length;j++){
						var bmCode = all[j].BM_CODE;
						var ks_path = all[j].CODE_PATH;
						var ks_odept = all[j].ODEPT_CODE_V;
						//判断考生是否在关联机构范围内
						if(tmp1.includes(bmCode)){
							continue;
						}
						var fh = isfh(ks_odept,ks_path,kcJgArr);
						if(fh){
							tmp1.push(bmCode);
							rmArr.push(all[j]);
						}
						//当前考场 该场次已满人
						if(tmp1.length == kcZwNum){
							break;
						}
						if(j == (all.length -1) && tmp1.length == 0){
							stopKcArr.push(kcId);
						}
					}
					//得到还剩的所有考生
					
					all = rmMthod(all,rmArr);
					if(tmp1.length > 0){
						countFlag1 = true;
						if(total[kcId] == undefined){
							var total_childArr = new Array();
							total_childArr.push(tmp1);
							total[kcId] = total_childArr;
						}else{
							total[kcId].push(tmp1);
						}
					}else{
						if(total[kcId] == undefined){
							var total_childArr = new Array();
							total[kcId] = total_childArr;
						}
					}
				}
			}
		
		}else if(cjVal == "1,2"){
			var kcArr1 = FireFly.doAct("TS_XMGL_CCCS_2","query",{"_WHERE_":"and XM_ID = '"+xmId+"' and KC_LEVEL = '一级'"})._DATA_;
			
			var kcArr2 = FireFly.doAct("TS_XMGL_CCCS_2","query",{"_WHERE_":"and XM_ID = '"+xmId+"' and KC_LEVEL = '二级'"})._DATA_;
			//二级考场
			var countFlag2 = true;
			while(countFlag2){
				countFlag2 = false;
				for(var i=0;i<kcArr2.length;i++){
					var kcBean = kcArr2[i];
					var kcId = kcBean.KC_ID;
					var kcGood = kcBean.KC_GOOD;
					var kcMax = kcBean.KC_MAX;
					var kcZwNum = 0;
					if(num == 1){
						kcZwNum = kcGood;
					}else{
						kcZwNum = kcMax;
					}
					var kcJgArr = new Array();
					//得到考场的关联机构
					if(kcGljg[kcId] == undefined || kcGljg[kcId] == ""){
						kcJgArr = FireFly.doAct("TS_KCGL_GLJG_V","finds", {"_SELECT_":"JG_CODE,jg_type,code_path","_WHERE_":"and KC_ID = '"+kcId+"'"})._DATA_;
						kcGljg[kcId] = kcJgArr;
					}else{
						kcJgArr = kcGljg[kcId];
					}
					
					//KC_ID,KC_GOOD,KC_MAX
					var tmp1 = new Array();
					var rmArr = new Array();
					for(var j=0;j<all.length;j++){
						var bmCode = all[j].BM_CODE;
						var ks_path = all[j].CODE_PATH;
						var ks_odept = all[j].ODEPT_CODE_V;
						//判断考生是否在关联机构范围内
						if(tmp1.includes(bmCode)){
							continue;
						}
						var fh = isfh(ks_odept,ks_path,kcJgArr);
						if(fh){
							tmp1.push(bmCode);
							rmArr.push(all[j]);
						}
						//当前考场 该场次已满人
						if(tmp1.length == kcZwNum){
							break;
						}
						if(i == (all.length -1) && tmp1.length == 0){
							stopKcArr.push(kcId);
						}
					}
					all = rmMthod(all,rmArr);
					if(tmp1.length > 0){
						countFlag2 = true;
						if(total[kcId] == undefined){
							var total_childArr = new Array();
							total_childArr.push(tmp1);
							total[kcId] = total_childArr;
						}else{
							total[kcId].push(tmp1);
						}
					}else{
						if(total[kcId] == undefined){
							var total_childArr = new Array();
							total[kcId] = total_childArr;
						}
					}
				}
			}
			/*****一级考场*********/
			//是否再次计算
			var countFlag1 = true;
			while(countFlag1){
				countFlag1 = false;
				for(var i=0;i<kcArr1.length;i++){
					var kcBean = kcArr1[i];
					var kcId = kcBean.KC_ID;
					var kcGood = kcBean.KC_GOOD;
					var kcMax = kcBean.KC_MAX;
					var kcZwNum = 0;
					if(num == 1){
						kcZwNum = kcGood;
					}else{
						kcZwNum = kcMax;
					}
					var kcJgArr = new Array();
					
					if(stopKcArr.includes(kcId)){
						continue;
					}
					//得到考场的关联机构
					if(kcGljg[kcId] == undefined || kcGljg[kcId] == ""){
						kcJgArr = FireFly.doAct("TS_KCGL_GLJG_V","finds", {"_SELECT_":"JG_CODE,jg_type,code_path","_WHERE_":"and KC_ID = '"+kcId+"'"})._DATA_;
						kcGljg[kcId] = kcJgArr;
					}else{
						kcJgArr = kcGljg[kcId];
					}
					
					//本场次本考场分配的考生
					var tmp1 = new Array();
					//已分配的考生人员 用于从all人员中删除，取得剩余人员
					var rmArr = new Array();
					for(var j=0;j<all.length;j++){
						var bmCode = all[j].BM_CODE;
						var ks_path = all[j].CODE_PATH;
						var ks_odept = all[j].ODEPT_CODE_V;
						//判断考生是否在关联机构范围内
						if(tmp1.includes(bmCode)){
							continue;
						}
						var fh = isfh(ks_odept,ks_path,kcJgArr);
						if(fh){
							tmp1.push(bmCode);
							rmArr.push(all[j]);
						}
						//当前考场 该场次已满人
						if(tmp1.length == kcZwNum){
							break;
						}
						if(i == (all.length -1) && tmp1.length == 0){
							stopKcArr.push(kcId);
						}
					}
					//得到还剩的所有考生
					all = rmMthod(all,rmArr);
					if(tmp1.length > 0){
						countFlag1 = true;
						if(total[kcId] == undefined){
							var total_childArr = new Array();
							total_childArr.push(tmp1);
							total[kcId] = total_childArr;
						}else{
							total[kcId].push(tmp1);
						}
					}else{
						if(total[kcId] == undefined){
							var total_childArr = new Array();
							total[kcId] = total_childArr;
						}
					}
				}
			}
			//一级考场
		}
		/******一二级考场计算 结束*********/
	}
	res["all"] = all;
	res["total"] = total;
	return res;
}

/**
 * arr1数组删除arr2数组中元素
 * @param arr1
 * @param arr2
 */
function rmMthod(arr1,arr2){
	var newArr = new Array();
	for(var i = 0; i < arr1.length; i++){
		var flag = true;
		for(var j = 0; j < arr2.length; j++){
			if(arr1[i] == arr2[j]){
				flag = false;
				break;
			}
		}
		if(flag){
			newArr.push(arr1[i]);
		}
	}
	return newArr;
}
/**
 * 根据考生的机构，部门路径 判断是否在关联机构范围内
 */
function isfh(ks_odept,ks_path,jgArr){
	var flag = false;
	if(jgArr == undefined || jgArr.length == 0){
		return false;
	}

	for(var j = 0; j < jgArr.length; j++){
		var jg_code = jgArr[j].JG_CODE;
		var jg_type = jgArr[j].JG_TYPE;
		var code_path = jgArr[j].CODE_PATH;
		if(jg_type == 1){
			//本机构
			if(ks_odept == jg_code){
				flag = true;
				break;
			}
		}else{
			//本机构及下级机构
			if(ks_path.indexOf(code_path) != -1){
				flag = true;
				break;
			}
		}
	}
	return flag;
}


// 导出
_viewer.getBtn("expExcel").unbind("click").bind("click",function(event) {
	window.open(FireFly.getContextPath() + '/TS_XMGL_CCCS_2.expExcel.do?data=' + 
    		encodeURIComponent(jQuery.toJSON({"xmId":xmId})));
});

function notInTable(array){
	var main = $("#TS_XMGL_CCCS_2 .content-mainCont");
//	$("#mytable").remove();
	$("#TS_XMGL_CCCS_2-expNo").show();
	var htmlStr = '<table border="1" class="rhGrid JPadding JColResizer" id="mytable" style="margin:0px 8px 15px 8px;width:98%">';
	htmlStr += '<caption align="left">不符合条件考生：</caption>';
	htmlStr += '<thead class="rhGrid-thead"><tr><th class="rhGrid-thead-num" style="width: 3.3%;"></th><th icode="BM_NAME" class="rhGrid-thead-th" style="width: 15.7%;">姓名</th><th icode="BM_CODE" class="rhGrid-thead-th" style="width: 15.7%;">人力资源编码</th><th icode="BM_LB" class="rhGrid-thead-th" style="width: 15.7%;">类别</th><th icode="BM_XL" class="rhGrid-thead-th" style="width: 15.7%;">序列</th><th icode="BM_MK" class="rhGrid-thead-th" style="width: 15.7%;">模块</th><th icode="BM_TYPE_NAME" class="rhGrid-thead-th" style="width: 5.9%;">级别</th><th icode="BM_KS_TIME" class="rhGrid-thead-th" style="width: 10%;">考试时长</th><th icode="ONE" class="rhGrid-thead-th" style="width: 16%;">一级机构</th><th icode="CAUSE" class="rhGrid-thead-th" style="width: 16%;">原因</th></tr></thead>';
	htmlStr += '<tbody class="rhGrid-tbody">';
	for(var i=0;i<array.length;i++){
		var num = i+1;
		var BM_NAME = array[i].BM_NAME;
		var BM_CODE = array[i].BM_CODE;
		var BM_LB = array[i].BM_LB;
		var BM_XL = array[i].BM_XL;
		var BM_MK = array[i].BM_MK;
		var BM_TYPE_NAME = array[i].BM_TYPE_NAME;
		var BM_KS_TIME = array[i].BM_KS_TIME;
		var ODEPT_CODE_V = array[i].ODEPT_CODE_V;
		var CAUSE = array[i].CAUSE;
		var ONE = array[i].ONE;
		var odeptName = FireFly.doAct("TS_XMGL_CCCS_V","getDictItemName", {"dictId":"SY_ORG_ODEPT_ALL","itemCode":ODEPT_CODE_V},true,false).ITEM_NAME;
		htmlStr += '<tr class="tBody-tr tBody-trOdd>';
		htmlStr += '<td class="indexTD"></td>';
		htmlStr += '<td class="indexTD" icode="num">'+num+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_NAME">'+BM_NAME+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_CODE">'+BM_CODE+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_XL">'+BM_LB+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_XL">'+BM_XL+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_MK">'+BM_MK+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_TYPE_NAME">'+BM_TYPE_NAME+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_TYPE_NAME">'+BM_KS_TIME+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="ODEPT_CODE_V__NAME">'+ONE+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="CAUSE">'+CAUSE+'</td>';
		htmlStr += '</tr>';
	}

	htmlStr += '</tbody></table>';
	main.append(htmlStr);
}

_viewer.getBtn("expNo").unbind("click").bind("click",function(event){
	window.open(FireFly.getContextPath() + '/TS_XMGL_CCCS_2.expNoExcel.do?data=' + 
    		encodeURIComponent(jQuery.toJSON({"xmId":xmId})));
});