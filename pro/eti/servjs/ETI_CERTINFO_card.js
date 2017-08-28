var _viewer = this;
/**
 * 证书信息模块
 * 实现岗位类别和岗位序列的二级联动效果
 * @author leader
 */
_viewer.getItem("STATION_TYPE").change(function(){
	//获取到对应类别
	var staType=_viewer.getItem("STATION_TYPE").getValue();
	//添加到请求串
	var data = {"_searchWhere":"and STATION_TYPE = '"+staType+"'","_NOPAGE_":"true"};
	//调用后台方法查询返回结果
	var result = FireFly.getListData("TS_STATION_TYPE_FW",data);
	//要返回的集合对象
	var stationList = [];
	for (var i = 0; i < result._DATA_.length; i++) {
		var station = result._DATA_[i];
		stationList.push({ID : station.STATION_ID, NAME : station.STATION_NAME});
	}
	//每次选择类别后都要清空序列
	_viewer.getItem("STATION_NO").removeOptions();
	//将后端查询到的序列集合返回给前端对象中。
	_viewer.getItem("STATION_NO").addOptions(stationList);
});

//debugger;
//var as = _viewer._actVar;
//alert(as);
//判断请求是添加还是修改，若是添加则调用扩展类的方法生成流水号，
//若是修改则直接调用平台方法获取数据返回
if(_viewer._actVar==UIConst.ACT_CARD_ADD){
	//卡片交互后台的方法，第一个参数为服务ID， 第二个参数为自己编写扩展类的方法名。
	var serialNum=FireFly.doAct('ETI_CERTINFO','uuid');
	//将后台返回的数据传入到对应的位置。
	_viewer.getItem("CERT_ID").setValue(serialNum.serialNum);
}
