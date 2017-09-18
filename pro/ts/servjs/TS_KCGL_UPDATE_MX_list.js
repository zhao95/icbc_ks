var _viewer = this;

$("#TS_KCGL_UPDATE_MX .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var colValue = $(item).find("td[icode='MX_COL']").text();
		var data = $(item).find("td[icode='MX_DATA']").text();
		var data3 = $(item).find("td[icode='MX_DATA3']").text();
		var data4 = $(item).find("td[icode='MX_DATA4']").text();
		if(colValue == "KC_ODEPTCODE"){
			$(item).find("td[icode='ZDY']").text(data3);
		}else if(colValue == "KC_LEVEL"){
			$(item).find("td[icode='ZDY']").text(data4);
		}else{
			$(item).find("td[icode='ZDY']").text(data);
		}
	}
});