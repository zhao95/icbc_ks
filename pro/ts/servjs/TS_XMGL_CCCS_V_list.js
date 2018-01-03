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

var notInArray = new Array();

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
		FireFly.doAct("TS_XMGL_CCCS_V","getKsList", {"xmId":xmId,"sjVal":sjVal},true,false,function(data){
			all = data.all;
			non = data.nonKsList;
			alltype = data.typeList;
		});	
		
		try{
			if (scVal != "" && scVal != null) {
				$("#TS_XMGL_CCCS_V .rhGrid").find("tr").each(function(index, item) {
					if (index != 0) {
						
						var dataId = item.id;
						var deptName = $(item).find("td[icode='DEPT_NAME']").html();
						if(dataId == "") return;
						if (cjVal == "1") {
							var param = {};
							param["_WHERE_"] = "and xm_id = '"+ xmId + "' and KC_LEVEL = '一级' and KC_ODEPTCODE='"+ dataId + "'";
							//符合条件考场集合
							var kcArr = FireFly.doAct("TS_XMGL_CCCS_UTIL_V", "finds", param)._DATA_;
							if (kcArr.length > 0) {								
								var kcNum = kcArr.length;
								$(item).find("td[icode='CC_KC_NUM']").html(kcNum);
								var result = getResult(kcArr,all,alltype);
								all = result.syKsArray;
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
								tmpBean["CC_KC_NUM"] = kcNum;//kcArrTemp.length;
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
								tmpBean["DEPT_CODE"] = dataId;
								tmpBean["DEPT_NAME"] = deptName;
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
							
							// 2级考场
							var param2 = {};
							param2["_SELECT_"] = "DEPT_CODE,DEPT_NAME";
//							param2["_WHERE_"] = "and (DEPT_PCODE = '"+dataId+"' or dept_code = '"+dataId+"') and DEPT_TYPE=2 and s_flag = 1";
							param2["_WHERE_"] = "and (DEPT_PCODE = '"+dataId+"') and DEPT_TYPE=2 and s_flag = 1";
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
										var result = getResult(kcArrTemp,all,alltype);
										all = result.syKsArray;
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
							
							// 1级考场
							var param = {};
							param["_WHERE_"] = "and xm_id = '" + xmId+ "' and KC_LEVEL = '一级' and KC_ODEPTCODE='" + dataId + "'";
							var deptName = $(item).find("td[icode='DEPT_NAME']").html();
							var deptNameA = "<a id='deptNameA"+dataId+"' class='deptNameA' myType='1'>"+ deptName + "</>";
							$(item).find("td[icode='DEPT_NAME']").html(deptNameA);
							
							var kcArr = FireFly.doAct("TS_XMGL_CCCS_UTIL_V", "finds", param)._DATA_;
							
							if(kcArr.length > 0){
								kcNumSum += kcArr.length;
								var result = getResult(kcArr,all,alltype);
								all = result.syKsArray;
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
 * @param kcArr 考场数组 allks:未安排的考生 alltype:所有的考试类型
 * @return res
 */
function getResult(kcArr,allks,alltype){
	// 返回结果
	var res = {};
	var goodSumNum = 0;
	var maxSumNum = 0;
	var goodCCNum = 0;
	var maxCCNum = 0;
	var goodSyNum = 0;
	var maxSyNum = 0;
	var peopleNum = 0;
	var sumJgArr = new Array();
	for (var i = 0; i < kcArr.length; i++) {
		var goodNum = kcArr[i].KC_GOOD - 0;
		var maxNum = kcArr[i].KC_MAX - 0;
		goodSumNum += goodNum;
		maxSumNum += maxNum;
		// 根据考场，得到机构的范围
		var jgArr = FireFly.doAct("TS_KCGL_GLJG_V","finds", {"_SELECT_":"jg_code,jg_type,code_path","_WHERE_":"and KC_ID = '"+kcArr[i].KC_ID+"'"})._DATA_;
		sumJgArr = sumJgArr.concat(jgArr);
	}
	
	//关联机构范围内的考生
	var scopeKs = new Array();
	//得出在关联机构范围内的考生
	for(var i = 0; i < allks.length; i++){
			var ks_odept = allks[i].ODEPT_CODE_V;
			var ks_path = allks[i].CODE_PATH;
			
			for(var j = 0; j < sumJgArr.length; j++){
				var jg_code = sumJgArr[j].JG_CODE;
				var jg_type = sumJgArr[j].CODE_TYPE;
				var code_path = sumJgArr[j].CODE_PATH;
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

	
	for(var i = 0; i < alltype.length; i++){
		var tmpBmXlCode = alltype[i].BM_XL_CODE;
		var tmpBmMkCode = alltype[i].BM_MK_CODE;
		var tmpBmType = alltype[i].BM_TYPE;
		var userArray = new Array();
		for(var j=0;j<scopeKs.length;j++){
			if(tmpBmXlCode == scopeKs[j].BM_XL_CODE&&tmpBmMkCode == scopeKs[j].BM_MK_CODE&&tmpBmType == scopeKs[j].BM_TYPE){
				userArray.push(scopeKs[j]);
			}
		}
		var userArrayLength = userArray.length;
		if(userArrayLength > 0){
			goodCCNum += Math.ceil(userArrayLength/goodSumNum); //最大场次数 
			maxCCNum += Math.ceil(userArrayLength/maxSumNum); //最优场次数 
		}
	}
	
	peopleNum = scopeKs.length;
	res["CC_PEOPLE_NUM"] = peopleNum;
	res["CC_COMPUTER_GOODNUM"] = goodSumNum;
	res["CC_COMPUTER_MAXNUM"] = maxSumNum; 
	res["CC_GOOD_NUM"] = goodCCNum;
	res["CC_GOOD_SYNUM"] = goodCCNum * goodSumNum - peopleNum; ; 
	res["CC_MAX_NUM"] = maxCCNum;
	res["CC_MAX_SYNUM"] = maxCCNum * maxSumNum - peopleNum; 
	res["syKsArray"] = syKsArray;
	return res;
}

// 导出
_viewer.getBtn("expExcel").unbind("click").bind("click",function(event) {
	window.open(FireFly.getContextPath() + '/TS_XMGL_CCCS_V.expExcel.do?data=' + 
    		encodeURIComponent(jQuery.toJSON({"xmId":xmId})));
});

function notInTable(array){
	var main = $("#TS_XMGL_CCCS_V .content-mainCont");
	$("#mytable").remove();
	
	var htmlStr = '<table border="1" class="rhGrid JPadding JColResizer" id="mytable" style="margin:0px 8px 15px 8px;width:0%">';
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
