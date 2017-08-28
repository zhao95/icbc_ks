//var _viewer = this;
//alert(1)
//if(_viewer.opts.act == "cardAdd"){
//	var XM_SZ_ID = _viewer.opts.XM_SZ_ID;
//	alert(XM_SZ_ID);
//	
//	if(typeof(XM_SZ_ID)!="undefined"){ 
//		_viewer.getItem("XM_SZ_ID").setValue(XM_SZ_ID);
//	}
//}
//
//
////针对通知时间的校验
//_viewer.getItem("BM_TZ_START").obj.unbind("click").bind("click", function() {
//	    WdatePicker({
//	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-BM_TZ_END')}"
//	    });
//	});
//_viewer.getItem("BM_TZ_END").obj.unbind("click").bind("click", function() {
//
//	    WdatePicker({
//	        minDate : "#F{$dp.$D('" + _viewer.servId + "-BM_TZ_START')}"
//	    });
//	});
//
////针对报名时间的校验
//_viewer.getItem("BM_START").obj.unbind("click").bind("click", function() {
//	    WdatePicker({
//	        maxDate : "#F{$dp.$D('" + _viewer.servId + "-BM_END')}"
//	    });
//	});
//_viewer.getItem("BM_END").obj.unbind("click").bind("click", function() {
//
//	    WdatePicker({
//	        minDate : "#F{$dp.$D('" + _viewer.servId + "-BM_START')}"
//	    });
//	});

////列表后增加功能
//_viewer.grid._table.find("tr").each(function(index, item) {
//		var l = $("#TS_XMGL_SZ").find('table th').length;
//		//alert(l);
//	if (index == 0 && l==5) {
//		$(item).append('<th class="rhGrid-thead-th" icode="progress" style="width:70%;">状态</th>','<th class="rhGrid-thead-th" icode="progress" style="width:190%;">概况</th>','<th class="rhGrid-thead-th" icode="oper" style="width:70%;">操作</th>');
//	}else if(index!=0){
//		var  XM_SZ_ID = $('td[icode="XM_SZ_ID"]',item).text();
//		 var  XM_SZ_NAME = $('td[icode="XM_SZ_NAME"]',item).text();
//		   if(XM_SZ_ID!=null&&XM_SZ_ID!=''){
//			  
//			   if(XM_SZ_NAME=="报名"){
//		          var ite=$(item).append('<td class="rhGrid-td-left" icode="progress" style="width:10%;"><select style="width:70%"><option  value="1">未开启</option><option    value="2" >报名中</option><option    value="3" >已结束</option></select></td>');
//		          var obj = jQuery("<td class='rhGrid-td-center'><div>报考人数：10000</div><div>报考初级人数：5000，中级人数：3000，高级人数：2000</div></td>").appendTo($(ite));  
//		          var obj1 = jQuery("<td class='rhGrid-td-center'></td>");
//		          obj1.appendTo($(ite));
//			   }else if(XM_SZ_NAME=="审核"){
//				  var ite=$(item).append('<td class="rhGrid-td-left" icode="progress" style="width:10%;"><select style="width:70%"><option  value="1">未开启</option><option    value="2" >审核中</option><option    value="3" >已结束</option></select></td>');   
//				  var obj = jQuery("<td class='rhGrid-td-center'><div>审核通过人数：9000 (其中，自动审核：8500；手动审核：500)</div><div>审核不通过人数：1000</div></td>").appendTo($(ite));  
//			      var obj1 = jQuery("<td class='rhGrid-td-center'></td>");
//			      obj1.appendTo($(ite));
//			   }else if(XM_SZ_NAME=="请假"){
//				   var ite=$(item).append('<td class="rhGrid-td-left" icode="progress" style="width:10%;"><select style="width:70%"><option  value="1">未开启</option><option    value="2" >考前请假开放中</option><option    value="3" >考后请假开放中</option><option    value="4" >已结束</option></select><div>(距申请截止时间：2天4时50分)</div></td>');   
//				   var obj = jQuery("<td class='rhGrid-td-center'><div>申请人数   ：200</div><div>已审核人数：120 （初审通过：80，终审通过：40；不通过40）</div><div>未审核人数：80</div></td>").appendTo($(ite));  
//				   var obj1 = jQuery("<td class='rhGrid-td-center'></td>");
//				   obj1.appendTo($(ite));
//			   }else if(XM_SZ_NAME=="异地借考"){
//				   var ite=$(item).append('<td class="rhGrid-td-left" icode="progress" style="width:10%;"><select style="width:70%"><option  value="1">未开启</option><option    value="2" >异地借考开放中</option><option    value="3" >已结束</option></select><div>(距申请截止时间：1天2时3分)</div></td>');   
//				   var obj = jQuery("<td class='rhGrid-td-center'><div>申请人数   ：80</div><div>完成处理人数：20</div><div>处理中:50</div><div>未处理：10</div></td>").appendTo($(ite));  
//				   var obj1 = jQuery("<td class='rhGrid-td-center'></td>");
//				   obj1.appendTo($(ite));
//			   }else if(XM_SZ_NAME=="试卷"){
//				   var ite=$(item).append('<td class="rhGrid-td-left" icode="progress" style="width:10%;"><select style="width:70%"><option  value="1">未设置</option><option    value="2" >已设置</option></select></td>');   
//				   var obj = jQuery("<td class='rhGrid-td-center'></td>").appendTo($(ite));  
//				   var obj1 = jQuery("<td class='rhGrid-td-center'></td>");
//				   obj1.appendTo($(ite));
//			   }else if(XM_SZ_NAME=="场次测算"){
//				   var ite=$(item).append('<td class="rhGrid-td-left" icode="progress" style="width:10%;"><select style="width:70%"><option  value="1">未设置</option><option    value="2" >已设置</option></select></td>');   
//				   var obj = jQuery("<td class='rhGrid-td-center'><div>最大场次数：8</div><div>考场数：205（一级：150，二级：55）</div></td>").appendTo($(ite));  
//				   var obj1 = jQuery("<td class='rhGrid-td-center'></td>");
//				   obj1.appendTo($(ite));
//			   }else if(XM_SZ_NAME=="考场安排"){
//				   var ite=$(item).append('<td class="rhGrid-td-left" icode="progress" style="width:10%;"><select style="width:70%"><option  value="1">未设置</option><option    value="2" >已设置</option></select></td>');   
//				   var obj = jQuery("<td class='rhGrid-td-center'><div>最大场次数：8</div><div>实际使用考场数：180（一级：150，二级：30）</div></td>").appendTo($(ite));  
//				   var obj1 = jQuery("<td class='rhGrid-td-center'></td>");
//				   obj1.appendTo($(ite));
//			   }
//    
//		        var span = jQuery("<span style='color:red'>【设置】</span>");
//		        span.appendTo(obj1);
// 
//		        //设置按钮	
//	            span.unbind("click").bind("click", function(event) {
//			    showset(XM_SZ_ID, XM_SZ_NAME);
//	            });
//	 
//	  	}else{
//		$(item).find('td').attr('colspan','6');
//	     }
//	}
//});
////返回按钮
//_viewer.getBtn("goback").unbind("click").bind("click", function() {
//	 window.location.href ="stdListView.jsp?frameId=TS_XMGL-tabFrame&sId=TS_XMGL&paramsFlag=false&title=项目管理";
//});
//
//
//function showset(XM_SZ_ID, XM_SZ_NAME){
//	
//	if( XM_SZ_NAME=="报名"){
//		
//	//	var ext =  " and XM_SZ_ID = '" + XM_SZ_ID + "'";
//		window.location.href ="stdCardView.jsp?frameId=TS_XMGL_BMGL-card-dopkCodeTS_XMGL_BMGLreadOnlyfalse-TS_XMGL_BMGL-tabFrame&sId=TS_XMGL_BMGL&paramsFlag=true&areaId=&title=报名信息设置&pkCode=TS_XMGL_BMGL&readOnly=false"
//		//window.location.href ="stdCardView.jsp?frameId=TS_XMGL_BMGL-tabFrame&sId=TS_XMGL_BMGL&paramsFlag=false&title=报名信息设置&XM_SZ_ID="+XM_SZ_ID
//					//+"&extWhere="+ext;	
//	}
//}


