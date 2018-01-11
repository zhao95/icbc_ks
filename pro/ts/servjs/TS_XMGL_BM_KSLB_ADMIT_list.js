var _viewer = this;
var height = jQuery(window).height()-100;
var width = jQuery(window).width()-200;
//取消行点击事件
$(".rhGrid").find("tr").unbind("dblclick");
var  nowDate=new   Date();
var year=nowDate.getFullYear();//4位
    var years =year.toString().substr(2, 2);
var month=nowDate.getMonth()+1;//月份
    month =(month<10 ? "0"+month:month); 
var mydate = (years+month.toString());//1711

 //通过当前页得到关联的id,获取父级信息
/*var  qzId= _viewer.getParHandler().getPKCode();*/
/*var XM_SZ_ID=_viewer.getParHandler().getItem("XM_SZ_ID").getValue();
var BM_ID=_viewer.getParHandler().getItem("BM_ID").getValue();
var XM_ID=_viewer.getParHandler().getItem("XM_ID").getValue();*/
//列表需建一个code为BUTTONS的自定义字段，没行增加两个按钮
$("#TS_XMGL_BM_KSLB_ADMIT .rhGrid").find("tr").each(function(index, item) {
	if(index != 0){
		var dataId = item.id;
		$(item).find("td[icode='BUTTONS']").append(
				'<a class="rhGrid-td-rowBtnObj rh-icon" operCode="optSetBtn" rowpk="'+dataId+'"><span class="rh-icon-inner">设置</span><span class="rh-icon-img btn-option"></span></a>'
		);	
		// 为每个按钮绑定卡片
		bindCard();
	}
});

/*
* 删除前方法执行
*/
rh.vi.listView.prototype.beforeDelete = function(pkArray) {
	showVerify(pkArray,_viewer);
}
//绑定的事件     
function bindCard(){
	//准入成绩设置
	jQuery("td [operCode='optSetBtn']").unbind("click").bind("click", function(){
		var pkCode = jQuery(this).attr("rowpk")
		//打开查看页面act：方法（必填），sId：服务（必填），parHandler：当前句柄，widHeiArray:小卡片的宽度高度，xyArray：左上角坐标
		var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":"TS_XMGL_BM_KSLB_ADMIT","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,0]};
		temp[UIConst.PK_KEY]=pkCode;//修改时，必填	    
		 var cardView = new rh.vi.cardView(temp);
		cardView.show(true);
	});
	
}

//列表操作按钮 弹dialog
function openMyCard(dataId,readOnly,showTab){
	var temp = {"act":UIConst.ACT_CARD_MODIFY,"sId":_viewer.servId,"parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,50]};
    temp[UIConst.PK_KEY] = dataId;
    if(readOnly != ""){
    	temp["readOnly"] = readOnly;
    }
    if(showTab != ""){
    	temp["showTab"] = showTab;
    }
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
}
