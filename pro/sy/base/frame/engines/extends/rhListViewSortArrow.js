/**
 * 扩展方法，增加页面排序功能的图标，show方法调用
 * @author yjzhou
 * modify time 2017.02.16
 */
rh.vi.listView.prototype._renderSortIcon = function(){
	var _self = this;
    _self._removeServOrderInfo(_self.servId);
	_self._addSortIconHeader(_self.servId);
	_self._bindClickOrderIcon(_self.servId);
}

/**
 * 清除单个服务的排序配置缓存，在每个服务列表页面整体刷新时调用
 * @param {} servId
 * @author yjzhou
 * modify time 2017.02.16
 */
rh.vi.listView.prototype._removeServOrderInfo = function (servId) {
	//清空排序的缓存
	var temp = System.temp;
	var index = -1;
	if(temp["@SERV_ORDER_INFO@"]){
		var infoArr = temp["@SERV_ORDER_INFO@"];
		for (var i=0; i<infoArr.length; i++){
			if(infoArr[i].SERV_ID == servId){
				index = i;
				break;
			}
		}
		
		if(index == infoArr.length-1){
			infoArr.pop();
		}else if(index >=0 ){
			var isModify = false;
			for(var j=index; j<infoArr.length-1; j++){
				infoArr[j] = infoArr[j+1];
				isModify = true;
			}
			
			if(isModify){
				infoArr.pop();
			}
		}
	}
}

/**
 * 添加排序的表头
 * @param {} servId
 * @author yjzhou
 * modify time 2017.02.16 
 */
rh.vi.listView.prototype._addSortIconHeader = function (servId){
	var _self = this;
	var _thisGrid = _self.grid;
    if(_self._isNeedToPaintOrder(servId)){
    	jQuery("th[icode='sort_arrow']").remove();
		if(_thisGrid._lData.length){
//			jQuery("thead.rhGrid-thead tr").append("<th icode='sort_arrow' class='rhGrid-thead-th'>排序</th>");
			jQuery("thead.rhGrid-thead tr").append("<th icode='sort_arrow' class='rhGrid-thead-th'>"+Language.transStatic('rhListViewSortArrow_string1')+"</th>");
		}
    }else{
    	jQuery("th[icode='sort_arrow']").remove();
    }
}
/**
 * 查询配置表，找到当前服务的排序字段，若缓存中存在，则查询缓存中的信息，
 * 避免多选项卡切换时反复请求
 * @param servId
 * @author yjzhou
 * modify time 2017.02.16
 */
rh.vi.listView.prototype._isNeedToPaintOrder = function (servId){
	var _self = this;
	var temp = System.getVars();
	if(temp["@SERV_ORDER_INFO@"]){
		var infoArr = temp["@SERV_ORDER_INFO@"];
		for (var i=0; i<infoArr.length; i++){
			if(infoArr[i].SERV_ID == servId ){
				if(infoArr[i].ORDER_ITEM != ""){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	
	var orderInfo = FireFly.doAct("SY_SERV_ORDER","getOrderInfo",{"DATA_SERV_ID":servId},false,false);
	if(orderInfo == null || orderInfo == "undefined"){
		return false;
	}
	var orderItemName = orderInfo._DATA_.ORDER_ITEM;
	if(orderItemName && orderItemName != ""){
		_self._addOrderInfoIntoSystem(orderInfo._DATA_);
		return true;
	}else{
		var params = {SERV_ID:servId,ORDER_ITEM:""};
		_self._addOrderInfoIntoSystem(params);
		return false;
	}
}

/**
 * 将排序信息添加到缓存中
 * @param {} newOrderInfo
 * @author yjzhou
 * modify time 2017.02.16
 */
rh.vi.listView.prototype._addOrderInfoIntoSystem = function (newOrderInfo){
	var temp = System.getVars();
	if(temp["@SERV_ORDER_INFO@"]){
		var orderInfos = temp["@SERV_ORDER_INFO@"];
		for (var i=0; i<orderInfos.length; i++){
			if(orderInfos[i].SERV_ID == newOrderInfo.SERV_ID){
				orderInfos[i] = newOrderInfo;
				break;
			}
		}
		if(i == orderInfos.length){
			orderInfos.push(newOrderInfo);
		}
	}else{
	    var orderInfos = new Array();
	    orderInfos.push(newOrderInfo);
	    temp["@SERV_ORDER_INFO@"] = orderInfos;
	}
}

/**
 * 更新排序数据
 * @param {} dataServId
 * @param {} dataId
 * @param {} optType
 * @author yjzhou
 * modify time 2017.02.14
 */
rh.vi.listView.prototype._updateOrderNum = function (dataServId, dataId, optType){
	FireFly.doAct("SY_SERV_ORDER","updateOrderNum",{"DATA_SERV_ID":dataServId,"DATA_ID":dataId,"OPT_TYPE":optType},false,false);
//	this.refresh();
//	触发刷新按钮
//	jQuery("#"+dataServId).find("div a[class='rh-icon rhGrid-btnBar-a-refresh']").trigger("mousedown");
//	触发查询按钮
	jQuery("#"+dataServId).find("div table tbody tr td div[class='rhSearch-button']").trigger("click");
}

/**
 * 绑定单击图标操作
 * @param servId
 * @author yjzhou
 * modify time 2017.02.16
 */
rh.vi.listView.prototype._bindClickOrderIcon = function (servId){
	var _self = this;
    var _thisGrid = jQuery("div[id='"+servId+"']").find("tbody tr");
    jQuery("div[id='"+servId+"']").find("td[icode='sort_arrow']").remove();
    if(_self._isNeedToPaintOrder(servId)){
    	_thisGrid.each(function() {
			var id = jQuery(this).attr("id");
//			jQuery("#"+id+"").append("<td icode='sort_arrow' class='rhGrid-td-right'><a id='up_"+id+"' class='button' style='text-decoration:overline;font-size:large;font-weight:bold;color:grey;'>↑</a><span style='font-size:large;color:grey;'>|</span><a id='down_"+id+"' style='text-decoration:underline;font-size:large;font-weight:bold;color:grey;'>↓</a></td>");

			jQuery("#"+id+"").append("<td icode='sort_arrow' class='rhGrid-td-right'>" +
					"<a id='up_"+id+"'><img id='img_up_"+id+"' height='18px' width='18px' src='../frame/engines/extends/images/up_1.png' /></a>" +
							"<span style='font-size:large;color:grey;'>|</span>" +
							"<a id='down_"+id+"' ><img id='img_down_"+id+"' height='18px' width='18px' src='../frame/engines/extends/images/down_1.png' /></a></td>");

			jQuery("#up_"+id).unbind().bind("click",function(){
				_self._updateOrderNum(servId,id,"OPT_UP");
			});
			
			jQuery("#down_"+id).unbind().bind("click",function(){
			    _self._updateOrderNum(servId,id,"OPT_DOWN");
			});
    	});
		jQuery("td[icode='sort_arrow']").unbind().bind("click",function(e){
			//阻止冒泡事件
		    e.stopPropagation();
		});
	}else {
	   jQuery("div[id='"+servId+"']").find("th[icode='sort_arrow']").remove();
	   jQuery("div[id='"+servId+"']").find("td[icode='sort_arrow']").remove();
	}
}
