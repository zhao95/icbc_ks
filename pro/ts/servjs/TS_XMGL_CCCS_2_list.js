var _viewer = this;
$("#TS_XMGL_CCCS_2 .rhGrid").find("tr").unbind("dblclick");
var xmId = _viewer.getParHandler().getPKCode();
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

var notInArray = new Array();

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
		//所有考试类型
		var alltype = new Array();
		FireFly.doAct("TS_XMGL_CCCS_2","getKsList", {"xmId":xmId,"sjVal":sjVal},true,false,function(data){
			all = data.all;
			non = data.nonKsList;
			alltype = data.typeList;
		});	
		
		try{
			var x1 = 0;
			var sum = 0;
			//最大场次考场ID
			var maxDataId = "";
			//最大场次数
			var maxValue = 0;
			if (scVal != "" && scVal != null) {
				//导出顺序与列表一致
				var all_exp = all;
				$("#TS_XMGL_CCCS_2,#JColResizer2").find("tr").each(function(index, item) {
					if (index != 0) {
						var dataId = item.id;
						var kcName = $(item).find("td[icode='KC_NAME']").html();
						var kcGood = $(item).find("td[icode='KC_GOOD']").html();
						var kcMax = $(item).find("td[icode='KC_MAX']").html();
						var result = getResult(dataId,kcGood,kcMax,all_exp,alltype);
						all_exp = result.syKsArray;
						var tmpBean = {};
						tmpBean["KC_ID"] = dataId;
						tmpBean["KC_NAME"] = kcName;
						tmpBean["KC_GOOD"] = kcGood;
						tmpBean["KC_MAX"] = kcMax;
						tmpBean["NUM_PEOPLE"] = result.CC_PEOPLE_NUM;
						tmpBean["NUM_GOOD"] = result.CC_GOOD_NUM;
						tmpBean["NUM_MAX"] = result.CC_MAX_NUM;
						allArray.push(tmpBean);
					}
				});
				
				//二级考场优先级高于一级考场
				var param = {};
				param["XM_ID"] = xmId;
				param["cjVal"] = cjVal;
				param["_ORDER_"] = "KC_LEVEL desc";
				var searchResult =FireFly.doAct("TS_XMGL_CCCS_2","query",param)._DATA_;
				
				for(var i=0;i<searchResult.length;i++){
					sum++;
					var dataId = searchResult[i].KC_ID;
					var kcName = searchResult[i].KC_NAME;
					var kcGood = searchResult[i].KC_GOOD;
					var kcMax = searchResult[i].KC_MAX;
					
					var result = getResult(dataId,kcGood,kcMax,all,alltype);
					all = result.syKsArray;
					
					var trObj = $("#TS_XMGL_CCCS_2").find("tr[id='"+dataId+"']");
					
					trObj.find("td[icode='NUM_PEOPLE']").html(result.CC_PEOPLE_NUM);
					trObj.find("td[icode='NUM_GOOD']").html(result.CC_GOOD_NUM);
					trObj.find("td[icode='NUM_MAX']").html(result.CC_MAX_NUM);
					if(result.CC_GOOD_NUM > 10 || result.CC_MAX_NUM > 10){
						trObj.css("background","#AAAAAA");
						x1++;
					}
					if(result.CC_GOOD_NUM > maxValue){
						maxValue = result.CC_GOOD_NUM;
						maxDataId = dataId;
					}
					if(result.CC_MAX_NUM > maxValue){
						maxValue = result.CC_MAX_NUM;
						maxDataId = dataId;
					}
				}
			}
			
			if(maxDataId != ""){
				$("#TS_XMGL_CCCS_2").find("tr[id='"+maxDataId+"']").find("td[icode='KC_NAME']").css("color","#ff6200");
			}
			
			var str = JSON.stringify(allArray); 
			var expData = {};
			expData["EXP_STR"] = str;
			expData["XM_ID"] = xmId;
			var userCode = System.getVar("@USER_CODE@");
			//FireFly.doAct("TS_XMGL_CCCS_EXP","delete",{"_WHERE_":"and s_user = '"+userCode+"' and xm_id='"+xmId+"'"},false,true);
			FireFly.doAct("TS_XMGL_CCCS_EXP","save",expData,false);
			
			var percent1 = 0;
			if(x1 > 0 && sum > 0){
				percent1 = Math.round(x1*100/sum)
			}
			var fx = "分析:超过10场的考场有"+x1+"个，占比"+percent1+"%。说明：背景色为深色行为场次数大于10场，红色考场名称表示当前考场场次数最大。";
			$("#TS_XMGL_CCCS_2").find("table[id='JColResizer2']").find(".rhGrid-tbody").append("<tr class='tBody-tr'><td colspan=8 class='rhGrid-td-left'>"+fx+"</td></tr>")
			
			notInArray = non.concat(all);
			notInTable(notInArray);
			
		} catch(e) {
			console.log("error",e);
			myloadbar.hideDelayed();
		} finally {
			myloadbar.hideDelayed();
		}
	},100);
	
	myloadbar.hideDelayed();
	
}


/**
 * @param kcArr 考场数组 allks:未安排的考生 alltype:所有的考试类型
 * @return res
 */
function getResult(kcId,kcGood,kcMax,allks,alltype){
	// 返回结果
	var res = {};
	var goodCCNum = 0;
	var maxCCNum = 0;
	var peopleNum = 0;
	var sumJgArr = new Array();
	
	// 根据考场，得到机构的范围
	var jgArr = FireFly.doAct("TS_KCGL_GLJG_V","finds", {"_SELECT_":"JG_CODE,jg_type,code_path","_WHERE_":"and KC_ID = '"+kcId+"'"})._DATA_;
	
	//关联机构范围内的考生
	var scopeKs = new Array();
	//得出在关联机构范围内的考生
	for(var i = 0; i < allks.length; i++){
			var ks_odept = allks[i].ODEPT_CODE_V;
			var ks_path = allks[i].CODE_PATH;
			
			for(var j = 0; j < jgArr.length; j++){
				var jg_code = jgArr[j].JG_CODE;
				var jg_type = jgArr[j].JG_TYPE;
				var code_path = jgArr[j].CODE_PATH;
				if(jg_type == 1){
					//本机构
					if(ks_odept == jg_code){
						scopeKs.push(allks[i]);
						break;
					}
				}else{
					//本机构及下级机构
					if(ks_path.indexOf(code_path) != -1){
						scopeKs.push(allks[i]);
						break;
					}
				}
			}
	}
	
	//得出不在本考场关联机构范围的的考生
	var syKsArray = new Array();
	
	for(var i = 0; i < allks.length; i++){
		var flag = true;
		for(var j = 0; j < scopeKs.length; j++){
			if(allks[i] == scopeKs[j]){
				flag = false;
				break;
			}
		}
		if(flag){
			syKsArray.push(allks[i]);
		}
	}
	
//	for(var i = 0; i < alltype.length; i++){
//		var tmpBmXlCode = alltype[i].BM_XL_CODE;
//		var tmpBmMkCode = alltype[i].BM_MK_CODE;
//		var tmpBmType = alltype[i].BM_TYPE;
//		var userArray = new Array();
//		for(var j=0;j<scopeKs.length;j++){
//			if(tmpBmXlCode == scopeKs[j].BM_XL_CODE&&tmpBmMkCode == scopeKs[j].BM_MK_CODE&&tmpBmType == scopeKs[j].BM_TYPE){
//				userArray.push(scopeKs[j]);
//			}
//		}
//		var userArrayLength = userArray.length;
//		if(userArrayLength > 0){
//			goodCCNum += Math.ceil(userArrayLength/kcGood); //最大场次数 
//			maxCCNum += Math.ceil(userArrayLength/kcMax); //最优场次数 
//		}
//	}
	/***********/
	var myArr = new Array();
	for(var i = 0; i < alltype.length; i++){
		var tmpBmXlCode = alltype[i].BM_XL_CODE;
		var tmpBmMkCode = alltype[i].BM_MK_CODE;
		var tmpBmType = alltype[i].BM_TYPE;
		var userArray = new Array();
		for(var j=0;j<scopeKs.length;j++){
			if(tmpBmXlCode == scopeKs[j].BM_XL_CODE&&tmpBmMkCode == scopeKs[j].BM_MK_CODE&&tmpBmType == scopeKs[j].BM_TYPE){
				userArray.push(scopeKs[j].BM_CODE);
			}
		}
		
		if(userArray.length > 0){
			myArr.push(userArray);
		}
	}
	
	var myParam = {};
	myParam["myArr"] = encodeURIComponent(JSON.stringify(myArr));
	myParam["kcGood"] = kcGood;
	myParam["kcMax"] = kcMax;
	var rescc = FireFly.doAct("TS_XMGL_CCCS_2","getCC",myParam,true,false);
	
	goodCCNum = rescc.ccGood;
	maxCCNum = rescc.ccMax;
	/***********/
	peopleNum = scopeKs.length;
	res["CC_PEOPLE_NUM"] = peopleNum;
	res["CC_GOOD_NUM"] = goodCCNum;
	res["CC_MAX_NUM"] = maxCCNum;
	res["syKsArray"] = syKsArray;
	return res;
}

// 导出
_viewer.getBtn("expExcel").unbind("click").bind("click",function(event) {
	window.open(FireFly.getContextPath() + '/TS_XMGL_CCCS_2.expExcel.do?data=' + 
    		encodeURIComponent(jQuery.toJSON({"xmId":xmId})));
});

function notInTable(array){
	var main = $("#TS_XMGL_CCCS_2 .content-mainCont");
	$("#mytable").remove();
	
	var htmlStr = '<table border="1" class="rhGrid JPadding JColResizer" id="mytable" style="margin:0px 8px 15px 8px;width:98%">';
	htmlStr += '<caption align="left">不符合条件考生：</caption>';
	htmlStr += '<thead class="rhGrid-thead"><tr><th class="rhGrid-thead-num" style="width: 3.3%;"></th><th icode="BM_NAME" class="rhGrid-thead-th" style="width: 15.7%;">姓名</th><th icode="BM_CODE" class="rhGrid-thead-th" style="width: 15.7%;">人力资源编码</th><th icode="BM_XL" class="rhGrid-thead-th" style="width: 15.7%;">序列</th><th icode="BM_MK" class="rhGrid-thead-th" style="width: 15.7%;">模块</th><th icode="BM_TYPE_NAME" class="rhGrid-thead-th" style="width: 5.9%;">级别</th><th icode="BM_KS_TIME" class="rhGrid-thead-th" style="width: 10%;">考试时长</th><th icode="ODEPT_CODE_V__NAME" class="rhGrid-thead-th" style="width: 16%;">机构</th></tr></thead>';
	htmlStr += '<tbody class="rhGrid-tbody">';
	for(var i=0;i<array.length;i++){
		var num = i+1;
		var BM_NAME = array[i].BM_NAME;
		var BM_CODE = array[i].BM_CODE;
		var BM_XL = array[i].BM_XL;
		var BM_MK = array[i].BM_MK;
		var BM_TYPE_NAME = array[i].BM_TYPE_NAME;
		var BM_KS_TIME = array[i].BM_KS_TIME;
		var ODEPT_CODE_V = array[i].ODEPT_CODE_V;
		var odeptName = FireFly.doAct("TS_XMGL_CCCS_V","getDictItemName", {"dictId":"SY_ORG_ODEPT_ALL","itemCode":ODEPT_CODE_V},true,false).ITEM_NAME;
		htmlStr += '<tr class="tBody-tr tBody-trOdd>';
		htmlStr += '<td class="indexTD"></td>';
		htmlStr += '<td class="indexTD" icode="num">'+num+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_NAME">'+BM_NAME+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_CODE">'+BM_CODE+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_XL">'+BM_XL+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_MK">'+BM_MK+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_TYPE_NAME">'+BM_TYPE_NAME+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="BM_TYPE_NAME">'+BM_KS_TIME+'</td>';
		htmlStr += '<td class="rhGrid-td-left " icode="ODEPT_CODE_V__NAME">'+odeptName+'</td>';
		htmlStr += '</tr>';
	}

	htmlStr += '</tbody></table>';
	main.append(htmlStr);
}

