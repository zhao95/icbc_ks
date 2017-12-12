var _viewer = this;
$("#TS_XMGL_CCCS_V .rhGrid").find("tr").unbind("dblclick");
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

if(runVal == "1"){
	Cookie.set("runVal", 0, 1);
	run();
}

_viewer.getBtn("run").unbind("click").bind("click", function(event) {
	if(scVal == "" || sjVal == "" || cjVal == ""){
		alert("请设置测算条件");
	}
	run();
});

function run(type){

	_viewer.refreshGrid();
	var allArray = new Array();
	var myloadbar = new rh.ui.loadbar({"id":"my-loadbar"});
	myloadbar.show();

	setTimeout(function(){
		try{
			if (scVal != "" && scVal != null) {
				$("#TS_XMGL_CCCS_V .rhGrid").find("tr").each(function(index, item) {
					if (index != 0) {
						var dataId = item.id;
						if(dataId == "") return;
						if (cjVal == "1") {
							var param = {};
							param["_WHERE_"] = "and xm_id = '"+ xmId + "' and KC_LEVEL = '一级' and KC_ODEPTCODE='"+ dataId + "'";
							var kcArr = FireFly.doAct("TS_XMGL_CCCS_UTIL_V", "finds", param)._DATA_;
							if (kcArr.length > 0) {								
								var kcNum = kcArr.length;
								$(item).find("td[icode='CC_KC_NUM']").html(kcNum);
								var result = getResult(kcArr);
								$(item).find("td[icode='CC_COMPUTER_GOODNUM']").html(result.CC_COMPUTER_GOODNUM);
								$(item).find("td[icode='CC_COMPUTER_MAXNUM']").html(result.CC_COMPUTER_MAXNUM);
								$(item).find("td[icode='CC_PEOPLE_NUM']").html(result.CC_PEOPLE_NUM);
								$(item).find("td[icode='CC_GOOD_NUM']").html(result.CC_GOOD_NUM);
								$(item).find("td[icode='CC_MAX_NUM']").html(result.CC_MAX_NUM);
								$(item).find("td[icode='CC_GOOD_SYNUM']").html(result.CC_GOOD_SYNUM);
								$(item).find("td[icode='CC_MAX_SYNUM']").html(result.CC_MAX_SYNUM);
								
								var tmpBean = {};
								tmpBean["DEPT_CODE"] = dataId;
								tmpBean["DEPT_NAME"] = deptName;
								tmpBean["CC_KC_NUM"] = kcArrTemp.length;
								tmpBean["CC_PEOPLE_NUM"] = result.CC_PEOPLE_NUM;
								tmpBean["CC_COMPUTER_GOODNUM"] = result.CC_COMPUTER_GOODNUM;
								tmpBean["CC_GOOD_NUM"] = result.CC_GOOD_NUM;
								tmpBean["CC_GOOD_SYNUM"] = result.CC_GOOD_SYNUM;
								tmpBean["CC_COMPUTER_MAXNUM"] = result.CC_COMPUTER_MAXNUM;
								tmpBean["CC_MAX_NUM"] = result.CC_MAX_NUM;
								tmpBean["CC_MAX_SYNUM"] = result.CC_MAX_SYNUM;
								allArray.push(tmpBean);
							} else {
								$(item).find("td[icode='CC_KC_NUM']").html("0");
								$(item).find("td[icode='CC_PEOPLE_NUM']").html("0");
								$(item).find("td[icode='CC_COMPUTER_GOODNUM']").html("0");
								$(item).find("td[icode='CC_GOOD_NUM']").html("0");
								$(item).find("td[icode='CC_GOOD_SYNUM']").html("0");
								$(item).find("td[icode='CC_COMPUTER_MAXNUM']").html("0");
								$(item).find("td[icode='CC_MAX_NUM']").html("0");
								$(item).find("td[icode='CC_MAX_SYNUM']").html("0");
								
								var tmpBean = {};
								tmpBean["DEPT_CODE"] = 0;
								tmpBean["DEPT_NAME"] = 0;
								tmpBean["CC_KC_NUM"] = 0;
								tmpBean["CC_PEOPLE_NUM"] = 0;
								tmpBean["CC_COMPUTER_GOODNUM"] = 0;
								tmpBean["CC_GOOD_NUM"] = 0;
								tmpBean["CC_GOOD_SYNUM"] = 0;
								tmpBean["CC_COMPUTER_MAXNUM"] = 0;
								tmpBean["CC_MAX_NUM"] = 0;
								tmpBean["CC_MAX_SYNUM"] = 0;
								allArray.push(tmpBean);
							}
						} else {
							var twoLevelArray=new Array();
							// 考场数
							var kcNumSum = 0;
							// 报考人数
							var peopleNumSum = 0;
							// 最优计算机数
							var computerGoodNumSum = 0;
							// 最优计算机剩余数
							var goodSyNumSum = 0;
							// 最大计算机数
							var computerMaxNumSum = 0;
							// 最大计算机剩余数
							var maxSyNumSum = 0;
							// 最优计算机场次
							var CcGood = 0;
							// 最大计算机场次
							var CcMax = 0;
							// 1级考场
							var param = {};
							param["_WHERE_"] = "and xm_id = '" + xmId+ "' and KC_LEVEL = '一级' and KC_ODEPTCODE='" + dataId + "'";
							var deptName = $(item).find("td[icode='DEPT_NAME']").html();
							var deptNameA = "<a id='deptNameA"+dataId+"' class='deptNameA' myType='1'>"+ deptName + "</>";
							$(item).find("td[icode='DEPT_NAME']").html(deptNameA);
							
							var kcArr = FireFly.doAct("TS_XMGL_CCCS_UTIL_V", "finds", param)._DATA_;
							
							if(kcArr.length > 0){
								kcNumSum += kcArr.length;
								var result = getResult(kcArr);
								peopleNumSum += (result.CC_PEOPLE_NUM-0);
								computerGoodNumSum += result.CC_COMPUTER_GOODNUM;
								goodSyNumSum += result.CC_GOOD_SYNUM;
								computerMaxNumSum += result.CC_COMPUTER_MAXNUM;
								maxSyNumSum += result.CC_MAX_SYNUM;
								CcGood = result.CC_GOOD_NUM;
								CcMax = result.CC_MAX_NUM;
								
								var tmpBean = {};
								tmpBean["DEPT_CODE"] = dataId;
								tmpBean["DEPT_NAME"] = deptName;
								tmpBean["CC_KC_NUM"] = kcArr.length;
								tmpBean["CC_PEOPLE_NUM"] = result.CC_PEOPLE_NUM;
								tmpBean["CC_COMPUTER_GOODNUM"] = result.CC_COMPUTER_GOODNUM;
								tmpBean["CC_GOOD_NUM"] = result.CC_GOOD_NUM;
								tmpBean["CC_GOOD_SYNUM"] = result.CC_GOOD_SYNUM;
								tmpBean["CC_COMPUTER_MAXNUM"] = result.CC_COMPUTER_MAXNUM;
								tmpBean["CC_MAX_NUM"] = result.CC_MAX_NUM;
								tmpBean["CC_MAX_SYNUM"] = result.CC_MAX_SYNUM;
								
								twoLevelArray.push(tmpBean);
							}
							
							// 2级考场
							var param2 = {};
							param2["_SELECT_"] = "DEPT_CODE,DEPT_NAME";
							param2["_WHERE_"] = "and (DEPT_PCODE = '"+dataId+"' or dept_code = '"+dataId+"') and DEPT_TYPE=2 and s_flag = 1";
							var odept3Arr = FireFly.doAct("TS_ORG_DEPT","finds", param2)._DATA_;
							//二级机构数据
							if(odept3Arr.length > 0){
								for(var i=0;i<odept3Arr.length;i++){
									var tmpBean = {};
									var deptCode = odept3Arr[i].DEPT_CODE;
									var tempParam = {};
									tempParam["_WHERE_"] = "and xm_id = '" + xmId+ "' and KC_LEVEL = '二级' and KC_ODEPTCODE='" + deptCode + "'";
									var kcArrTemp = FireFly.doAct("TS_XMGL_CCCS_UTIL_V", "finds", tempParam)._DATA_;
									if(kcArrTemp.length > 0){
										
										kcNumSum += kcArrTemp.length;
										var result = getResult2(kcArrTemp);
										peopleNumSum += (result.CC_PEOPLE_NUM-0);
										computerGoodNumSum += result.CC_COMPUTER_GOODNUM;
										goodSyNumSum += result.CC_GOOD_SYNUM;
										computerMaxNumSum += result.CC_COMPUTER_MAXNUM;
										maxSyNumSum += result.CC_MAX_SYNUM;
										if(result.CC_GOOD_NUM > CcGood){
											CcGood = result.CC_GOOD_NUM;
										}
										if(result.CC_MAX_NUM > CcMax){
											CcMax = result.CC_MAX_NUM;
										}
										tmpBean["DEPT_CODE"] = odept3Arr[i].DEPT_CODE;
										tmpBean["DEPT_NAME"] = odept3Arr[i].DEPT_NAME;
										tmpBean["CC_KC_NUM"] = kcArrTemp.length;
										tmpBean["CC_PEOPLE_NUM"] = result.CC_PEOPLE_NUM;
										tmpBean["CC_COMPUTER_GOODNUM"] = result.CC_COMPUTER_GOODNUM;
										tmpBean["CC_GOOD_NUM"] = result.CC_GOOD_NUM;
										tmpBean["CC_GOOD_SYNUM"] = result.CC_GOOD_SYNUM;
										tmpBean["CC_COMPUTER_MAXNUM"] = result.CC_COMPUTER_MAXNUM;
										tmpBean["CC_MAX_NUM"] = result.CC_MAX_NUM;
										tmpBean["CC_MAX_SYNUM"] = result.CC_MAX_SYNUM;
										
										twoLevelArray.push(tmpBean);
									}
								}
							}
							$(item).find("td[icode='CC_KC_NUM']").html(kcNumSum);
							$(item).find("td[icode='CC_PEOPLE_NUM']").html(peopleNumSum);
							$(item).find("td[icode='CC_COMPUTER_GOODNUM']").html(computerGoodNumSum);
							$(item).find("td[icode='CC_GOOD_NUM']").html(CcGood);
							$(item).find("td[icode='CC_GOOD_SYNUM']").html(goodSyNumSum);
							$(item).find("td[icode='CC_COMPUTER_MAXNUM']").html(computerMaxNumSum);
							$(item).find("td[icode='CC_MAX_NUM']").html(CcMax);
							$(item).find("td[icode='CC_MAX_SYNUM']").html(maxSyNumSum);
							
							var tmpAllBean = {};
							tmpAllBean["DEPT_CODE"] = dataId;
							tmpAllBean["DEPT_NAME"] = deptName;
							tmpAllBean["CC_KC_NUM"] = kcNumSum;
							tmpAllBean["CC_PEOPLE_NUM"] = peopleNumSum;
							tmpAllBean["CC_COMPUTER_GOODNUM"] = computerGoodNumSum;
							tmpAllBean["CC_GOOD_NUM"] = CcGood;
							tmpAllBean["CC_GOOD_SYNUM"] = goodSyNumSum;
							tmpAllBean["CC_COMPUTER_MAXNUM"] = computerMaxNumSum;
							tmpAllBean["CC_MAX_NUM"] = CcMax;
							tmpAllBean["CC_MAX_SYNUM"] = maxSyNumSum;
							tmpAllBean["childArr"] = twoLevelArray;
							
							allArray.push(tmpAllBean);
							// $("#deptNameA"+dataId).click(function() {
							showDetail(dataId,twoLevelArray);
							// });
						}
					}
				});

			}
			
			var str = JSON.stringify(allArray); 
			var expData = {};
			expData["EXP_STR"] = str;
			expData["XM_ID"] = xmId;
			var userCode = System.getVar("@USER_CODE@");
			//FireFly.doAct("TS_XMGL_CCCS_EXP","delete",{"_WHERE_":"and s_user = '"+userCode+"' and xm_id='"+xmId+"'"},false,true);
			FireFly.doAct("TS_XMGL_CCCS_EXP","save",expData,false);
			
		} catch(e) {
			console.log("error",e);
			myloadbar.hideDelayed();
		} finally {
			myloadbar.hideDelayed();
		}
	},100);
	
	myloadbar.hideDelayed();
}


function showDetail(dataId,childArr) {
	var obj = $("#deptNameA"+dataId);
	var trObj = obj.parent().parent();
	var trPK = dataId;
	for(var i=0;i<childArr.length;i++){
		var strChild = "<tr class='tBody-tr tr_"+ trPK + " id="+ childArr[i].DEPT_CODE
		+ "'><td colspan='2'></td><td icode='DEPT_NAME'>&nbsp;&nbsp;"+ childArr[i].DEPT_NAME
		+ "</td><td icode='CC_KC_NUM'>"+ childArr[i].CC_KC_NUM
		+ "</td><td icode='CC_PEOPLE_NUM'>"+ childArr[i].CC_PEOPLE_NUM
		+ "</td><td icode='CC_COMPUTER_GOODNUM'>"+ childArr[i].CC_COMPUTER_GOODNUM
		+ "</td><td icode='CC_GOOD_NUM'>"+ childArr[i].CC_GOOD_NUM
		+ "</td><td icode='CC_GOOD_SYNUM'>"+ childArr[i].CC_GOOD_SYNUM
		+ "</td><td icode='CC_COMPUTER_MAXNUM'>"+ childArr[i].CC_COMPUTER_MAXNUM
		+ "</td><td icode='CC_MAX_NUM'>"+ childArr[i].CC_MAX_NUM
		+ "</td><td icode='CC_MAX_SYNUM'>"+ childArr[i].CC_MAX_SYNUM
		+ "</td></tr>";
		trObj.after(strChild);
	}
}



/**
 * @param kcList
 *            考场别表
 * @return res
 */
function getResult(kcArr){
	// 返回结果
	var res = {};
	var goodSumNum = 0;
	var maxSumNum = 0;
	var jgSum = "";
	var goodCCNum = 0;
	var maxCCNum = 0;
	var goodSyNum = 0;
	var maxSyNum = 0;
	var peopleNum = 0;
	var kcIds = "";
	for (var i = 0; i < kcArr.length; i++) {
		var goodNum = kcArr[i].KC_GOOD - 0;
		var maxNum = kcArr[i].KC_MAX - 0;
		goodSumNum += goodNum;
		maxSumNum += maxNum;
		// 根据考场，得到机构的范围
//		var jgArr = FireFly.doAct("TS_KCGL_GLJG_V","finds", {"_WHERE_":"and KC_ID = '"+kcArr[i].KC_ID+"'"})._DATA_;
//		for(var j=0;j<jgArr.length;j++){
//			jgSum = jgSum + "," + jgArr[j].JG_CODE;
//		}
		kcIds += kcArr[i].KC_ID + ","
	}
	
	var jgSum = "";
	var paths = "";
	FireFly.doAct("TS_XMGL_CCCS_V","getOdetpScope", {"kcIds":kcIds},true,false,function(data){
//		jgSum = data.odeptCodes;
//		if(jgSum != ""){
//			jgSum = jgSum.substring(0,jgSum.length-1);
//			jgSum = jgSum.replace(/,/g, "','");
//		}
		paths = data.paths;
	});	
	var kcTypesArr = FireFly.doAct("TS_XMGL_CCCS_UTIL_TYPE_V","finds",{"_WHERE_":"and xm_id = '"+xmId+"'"},true,false)._DATA_;
	for(var i = 0; i < kcTypesArr.length; i++){
		var tmpBmXlCode = kcTypesArr[i].BM_XL_CODE;
		var tmpBmMkCode = kcTypesArr[i].BM_MK_CODE;
		var tmpBmType = kcTypesArr[i].BM_TYPE;
		var param = {};
//		param["_WHERE_"] = "and xm_Id = '"+xmId+"' and BM_KS_TIME in ("+sjVal+") and ODEPT_CODE_V in ('"+jgSum+"') and BM_XL_CODE = '"
//			+tmpBmXlCode+"' and BM_MK_CODE = '"+tmpBmMkCode+"' and BM_TYPE = '"+tmpBmType+"'";
//		var tmpPoepleNum = FireFly.doAct("TS_XMGL_CCCS_KSGL","count", param)._DATA_;
		
		param["_WHERE_"] = "and xm_Id = '"+xmId+"' and BM_KS_TIME in ("+sjVal+") and BM_XL_CODE = '"
		+tmpBmXlCode+"' and BM_MK_CODE = '"+tmpBmMkCode+"' and BM_TYPE = '"+tmpBmType+"'";
		var tmpPoepleNum = 0;
		var ksList = FireFly.doAct("TS_XMGL_CCCS_KSGL","finds", param)._DATA_;
		for(var j=0;j<ksList.length;j++){
			var codePth = ksList[j].CODE_PATH;
			var pathArr = paths.split(",");
			for(var k=0;k<pathArr.length;k++){
				if(pathArr[k] == "")continue;
				if(codePth.indexOf(pathArr[k]) != -1){
					tmpPoepleNum++;
					break;
				}
			}
		}
		
		peopleNum += tmpPoepleNum-0;
		if (tmpPoepleNum != 0 && goodSumNum != 0 && maxSumNum !=0) { //最优场次数 
//			maxSyNum += maxSumNum-tmpPoepleNum-0;
//			goodSyNum += goodSumNum-tmpPoepleNum-0;
			goodCCNum += Math.ceil(tmpPoepleNum/goodSumNum); //最大场次数 
			maxCCNum += Math.ceil(tmpPoepleNum/maxSumNum); //最优剩余机器数 
		}
	}
	res["CC_PEOPLE_NUM"] = peopleNum;
	res["CC_COMPUTER_GOODNUM"] = goodSumNum;
	res["CC_COMPUTER_MAXNUM"] = maxSumNum; 
	res["CC_GOOD_NUM"] = goodCCNum;
	res["CC_GOOD_SYNUM"] = goodCCNum * goodSumNum - peopleNum; ; 
	res["CC_MAX_NUM"] = maxCCNum;
	res["CC_MAX_SYNUM"] = maxCCNum * maxSumNum - peopleNum; 
	return res;
}

function getResult2(kcArr){
	// 返回结果
	var res = {};
	var goodSumNum = 0;
	var maxSumNum = 0;
	var jgSum = "";
	var goodCCNum = 0;
	var maxCCNum = 0;
	var goodSyNum = 0;
	var maxSyNum = 0;
	var peopleNum = 0;
	var kcIds = "";
	for (var i = 0; i < kcArr.length; i++) {
		var goodNum = kcArr[i].KC_GOOD - 0;
		var maxNum = kcArr[i].KC_MAX - 0;
		goodSumNum += goodNum;
		maxSumNum += maxNum;
		// 根据考场，得到机构的范围
//		var jgArr = FireFly.doAct("TS_KCGL_GLJG_V","finds", {"_WHERE_":"and KC_ID = '"+kcArr[i].KC_ID+"'"})._DATA_;
//		for(var j=0;j<jgArr.length;j++){
//			jgSum = jgSum + "," + jgArr[j].JG_CODE;
//		}
		kcIds += kcArr[i].KC_ID + ","
	}
	
	var jgSum = "";
	FireFly.doAct("TS_XMGL_CCCS_V","get2OdetpScope", {"kcIds":kcIds},true,false,function(data){
		jgSum = data.odeptCodes;
		if(jgSum != ""){
			jgSum = jgSum.substring(0,jgSum.length-1);
			jgSum = jgSum.replace(/,/g, "','");
		}
	});	
	var kcTypesArr = FireFly.doAct("TS_XMGL_CCCS_UTIL_TYPE_V","finds",{"_WHERE_":"and xm_id = '"+xmId+"'"},true,false)._DATA_;
	for(var i = 0; i < kcTypesArr.length; i++){
		var tmpBmXlCode = kcTypesArr[i].BM_XL_CODE;
		var tmpBmMkCode = kcTypesArr[i].BM_MK_CODE;
		var tmpBmType = kcTypesArr[i].BM_TYPE;
		var param = {};
		param["_WHERE_"] = "and xm_Id = '"+xmId+"' and BM_KS_TIME in ("+sjVal+") and ODEPT_CODE_V in ('"+jgSum+"') and BM_XL_CODE = '"
			+tmpBmXlCode+"' and BM_MK_CODE = '"+tmpBmMkCode+"' and BM_TYPE = '"+tmpBmType+"'";
		var tmpPoepleNum = FireFly.doAct("TS_XMGL_CCCS_KSGL","count", param)._DATA_;
		if (tmpPoepleNum != 0 && goodSumNum != 0 && maxSumNum !=0) { //最优场次数 
			peopleNum += tmpPoepleNum-0;
			//maxSyNum += maxSumNum-tmpPoepleNum-0;
			//goodSyNum += goodSumNum-tmpPoepleNum-0;
			goodCCNum += Math.ceil(tmpPoepleNum/goodSumNum); //最大场次数 
			maxCCNum += Math.ceil(tmpPoepleNum/maxSumNum); //最优剩余机器数 
		}
	}
	res["CC_PEOPLE_NUM"] = peopleNum;
	res["CC_COMPUTER_GOODNUM"] = goodSumNum;
	res["CC_COMPUTER_MAXNUM"] = maxSumNum; 
	res["CC_GOOD_NUM"] = goodCCNum;
	res["CC_GOOD_SYNUM"] = goodCCNum * goodSumNum - peopleNum; 
	res["CC_MAX_NUM"] = maxCCNum;
	res["CC_MAX_SYNUM"] = maxCCNum * maxSumNum - peopleNum; 
	return res;
}
// 导出
_viewer.getBtn("expExcel").unbind("click").bind("click",function(event) {
//	tabletoExcel("JColResizer2");
	window.open(FireFly.getContextPath() + '/TS_XMGL_CCCS_V.expExcel.do?data=' + 
    		encodeURIComponent(jQuery.toJSON({"xmId":xmId})));
});
